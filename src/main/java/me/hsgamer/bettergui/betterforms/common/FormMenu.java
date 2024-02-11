package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public abstract class FormMenu<F extends Form, B extends FormBuilder<?, F, ?>> extends StandardMenu {
    private final FormSender sender;
    private final String title;
    private final List<Permission> permissions;
    private final ArgumentHandler argumentHandler;
    private final List<BiConsumer<UUID, B>> formModifiers = new ArrayList<>();

    protected FormMenu(FormSender sender, Config config) {
        super(config);
        this.sender = sender;

        title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "title"))
                .map(Object::toString)
                .orElse("");

        permissions = Optional.ofNullable(menuSettings.get("permission"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
                .orElse(Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName())));

        Optional.ofNullable(menuSettings.get("command"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .ifPresent(list -> {
                    for (String s : list) {
                        if (s.contains(" ")) {
                            getInstance().getLogger().warning("Illegal characters in command '" + s + "'" + "in the menu '" + getName() + "'. Ignored");
                        } else {
                            getInstance().getMenuCommandManager().registerMenuCommand(s, this);
                        }
                    }
                });

        argumentHandler = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "argument-processor", "arg-processor", "argument", "arg"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new ArgumentHandler(this, m))
                .orElseGet(() -> new ArgumentHandler(this, Collections.emptyMap()));

        Optional.ofNullable(MapUtils.getIfFound(menuSettings, "close-action"))
                .map(o -> new ActionApplier(this, o))
                .ifPresent(closeAction -> formModifiers.add((uuid, builder) -> {
                    builder.closedResultHandler(() -> {
                        BatchRunnable batchRunnable = new BatchRunnable();
                        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeAction.accept(uuid, process));
                        Scheduler.current().async().runTask(batchRunnable);
                    });
                }));
        Optional.ofNullable(MapUtils.getIfFound(menuSettings, "invalid-action"))
                .map(o -> new ActionApplier(this, o))
                .ifPresent(invalidAction -> formModifiers.add((uuid, builder) -> {
                    builder.invalidResultHandler(() -> {
                        BatchRunnable batchRunnable = new BatchRunnable();
                        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> invalidAction.accept(uuid, process));
                        Scheduler.current().async().runTask(batchRunnable);
                    });
                }));
    }

    protected abstract Optional<Pair<B, Consumer<F>>> createFormConstructor(Player player, String[] args, boolean bypass);

    @Override
    public boolean create(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        if (!sender.canSendForm(uuid)) {
            return false;
        }

        // Check Argument
        if (!argumentHandler.process(uuid, args).isPresent()) {
            return false;
        }

        // Check Permission
        if (!bypass && !PermissionUtils.hasAnyPermission(player, permissions)) {
            MessageUtils.sendMessage(player, getInstance().getMessageConfig().getNoPermission());
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
        return sender.sendForm(uuid, form);
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
