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
package me.hsgamer.bettergui.betterforms.menu;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.betterforms.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.component.ComponentProvider;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import me.hsgamer.bettergui.menu.BaseMenu;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FormMenu extends BaseMenu {
    private final FormSender sender;
    private final FormMenu.FormType formType;
    private final String defaultComponentType;
    private final Function<Player, FormBuilder<?, ?, ?>> formBuilderFunction;
    private final String title;
    private final List<BiConsumer<UUID, FormBuilder<?, ?, ?>>> formModifiers = new ArrayList<>();
    private final ActionApplier javaActionApplier;
    private final Map<String, ComponentProvider> componentMap = new LinkedHashMap<>();

    public FormMenu(FormSender sender, Config config, FormType formType) {
        super(config);
        this.sender = sender;
        this.formType = formType;

        String content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(ComponentUtil::toMultilineString)
                .orElse("");
        switch (formType) {
            case SIMPLE:
                this.defaultComponentType = "button";
                this.formBuilderFunction = player -> SimpleForm.builder().content(StringReplacerApplier.replace(content, player.getUniqueId(), this));
                break;
            case MODAL:
                this.defaultComponentType = "button";
                this.formBuilderFunction = player -> ModalForm.builder().content(StringReplacerApplier.replace(content, player.getUniqueId(), this));
                break;
            case CUSTOM:
                this.defaultComponentType = "";
                this.formBuilderFunction = player -> CustomForm.builder();
                break;
            default:
                throw new IllegalArgumentException("Form type " + formType + " is not supported");
        }

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

        ComponentProviderBuilder componentProviderBuilder = ComponentProviderBuilder.INSTANCE;
        for (Map.Entry<String, Object> configEntry : configSettings.entrySet()) {
            String key = configEntry.getKey();
            MapUtils.castOptionalStringObjectMap(configEntry.getValue())
                    .map(CaseInsensitiveStringMap::new)
                    .map(map -> new ComponentProviderBuilder.Input(this, key, map))
                    .flatMap(componentProviderBuilder::build)
                    .ifPresent(customFormComponent -> componentMap.put(key, customFormComponent));
        }

        if (!closeActionApplier.isEmpty()) {
            formModifiers.add((uuid, builder) -> {
                builder.closedResultHandler(() -> {
                    BatchRunnable batchRunnable = new BatchRunnable();
                    batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeActionApplier.accept(uuid, process));
                    SchedulerUtil.async().run(batchRunnable);
                });
            });
        }

        variableManager.register("form_", StringReplacer.of((original, uuid) -> {
            String[] split = original.split(":", 2);
            String component = split[0];
            String key = split.length > 1 ? split[1] : "";
            return Optional.ofNullable(componentMap.get(component))
                    .map(provider -> provider.getValue(uuid, key))
                    .orElse(null);
        }));
    }

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

        FormBuilder<?, ?, ?> builder = this.formBuilderFunction.apply(player);
        builder.title(StringReplacerApplier.replace(title, uuid, this));

        List<Component> components = new ArrayList<>();
        for (ComponentProvider provider : componentMap.values()) {
            List<Component> newComponents = provider.provide(uuid, components.size());
            components.addAll(newComponents);
        }

        components.forEach(component -> component.apply(builder));
        formModifiers.forEach(modifier -> modifier.accept(uuid, builder));

        builder.validResultHandler((form, response) -> {
            components.forEach(component -> component.preHandle(form, response));
            components.forEach(component -> component.handle(form, response));
        });

        Form form = builder.build();
        components.forEach(component -> component.postApply(form));

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

    public String getDefaultComponentType() {
        return defaultComponentType;
    }

    public FormType getFormType() {
        return formType;
    }

    public enum FormType {
        SIMPLE,
        MODAL,
        CUSTOM
    }
}
