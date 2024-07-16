/*
   Copyright 2024 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.menu.BaseMenu;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
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
    private final ActionApplier javaActionApplier;

    protected FormMenu(FormSender sender, Config config) {
        super(config);
        this.sender = sender;

        title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "title"))
                .map(Object::toString)
                .orElse("");
        javaActionApplier = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "java-action", "not-bedrock-action"))
                .map(o -> new ActionApplier(this, o))
                .orElse(ActionApplier.EMPTY);
        Optional.ofNullable(MapUtils.getIfFound(menuSettings, "invalid-action"))
                .map(o -> new ActionApplier(this, o))
                .ifPresent(invalidAction -> formModifiers.add((uuid, builder) -> {
                    builder.invalidResultHandler(() -> {
                        BatchRunnable batchRunnable = new BatchRunnable();
                        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> invalidAction.accept(uuid, process));
                        SchedulerUtil.async().run(batchRunnable);
                    });
                }));

        if (!closeActionApplier.isEmpty()) {
            formModifiers.add((uuid, builder) -> {
                builder.closedResultHandler(() -> {
                    BatchRunnable batchRunnable = new BatchRunnable();
                    batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeActionApplier.accept(uuid, process));
                    SchedulerUtil.async().run(batchRunnable);
                });
            });
        }
    }

    protected abstract Optional<Pair<B, Consumer<F>>> createFormConstructor(Player player, String[] args, boolean bypass);

    @Override
    protected boolean createChecked(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        if (!sender.canSendForm(uuid)) {
            if (!javaActionApplier.isEmpty()) {
                BatchRunnable batchRunnable = new BatchRunnable();
                batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> javaActionApplier.accept(uuid, process));
                SchedulerUtil.async().run(batchRunnable);
            }
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
                SchedulerUtil.async().run(batchRunnable);
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
