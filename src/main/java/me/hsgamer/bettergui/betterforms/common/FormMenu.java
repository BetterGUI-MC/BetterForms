package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.menu.BaseMenu;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class FormMenu<F extends Form, B extends FormBuilder<?, F, ?>> extends BaseMenu {
    private final FormSender sender;
    private final String title;
    private final List<BiConsumer<UUID, B>> formModifiers = new ArrayList<>();

    protected FormMenu(FormSender sender, Config config) {
        super(config);
        this.sender = sender;

        title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "title"))
                .map(Object::toString)
                .orElse("");
        Optional.ofNullable(MapUtils.getIfFound(menuSettings, "invalid-action"))
                .map(o -> new ActionApplier(this, o))
                .ifPresent(invalidAction -> formModifiers.add((uuid, builder) -> {
                    builder.invalidResultHandler(() -> {
                        BatchRunnable batchRunnable = new BatchRunnable();
                        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> invalidAction.accept(uuid, process));
                        Scheduler.current().async().runTask(batchRunnable);
                    });
                }));


        if (!closeActionApplier.isEmpty()) {
            formModifiers.add((uuid, builder) -> {
                builder.closedResultHandler(() -> {
                    BatchRunnable batchRunnable = new BatchRunnable();
                    batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeActionApplier.accept(uuid, process));
                    Scheduler.current().async().runTask(batchRunnable);
                });
            });
        }
    }

    protected abstract Optional<Pair<B, Consumer<F>>> createFormConstructor(Player player, String[] args, boolean bypass);

    @Override
    protected boolean createChecked(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        if (!sender.canSendForm(uuid)) {
            return false;
        }

        Optional<Pair<B, Consumer<F>>> optional = createFormConstructor(player, args, bypass);
        if (!optional.isPresent()) {
            return false;
        }
        Pair<B, Consumer<F>> pair = optional.get();

        B builder = pair.getKey();
        builder.title(StringReplacerApplier.replace(title, uuid, this));
        formModifiers.forEach(modifier -> modifier.accept(uuid, builder));
        F form = builder.build();
        pair.getValue().accept(form);
        if (sender.sendForm(uuid, form)) {
            if (!openActionApplier.isEmpty()) {
                BatchRunnable batchRunnable = new BatchRunnable();
                batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> openActionApplier.accept(uuid, process));
                Scheduler.current().async().runTask(batchRunnable);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(Player player) {
        // EMPTY
    }

    @Override
    public void close(Player player) {
        // EMPTY
    }

    @Override
    public void closeAll() {
        // EMPTY
    }
}
