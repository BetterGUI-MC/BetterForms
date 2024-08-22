package me.hsgamer.bettergui.betterforms.api.menu;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.api.component.ComponentProvider;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
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
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class FormMenu<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends BaseMenu {
    private final FormSender sender;
    private final String title;
    private final List<BiConsumer<UUID, B>> formModifiers = new ArrayList<>();
    private final ActionApplier javaActionApplier;
    private final Map<String, ComponentProvider<F, R, B>> componentMap = new LinkedHashMap<>();

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

        ComponentProviderBuilder<F, R, B> componentProviderBuilder = getComponentProviderBuilder();
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

    protected abstract B createFormBuilder();

    protected abstract ComponentProviderBuilder<F, R, B> getComponentProviderBuilder();

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

        B builder = createFormBuilder();
        builder.title(StringReplacerApplier.replace(title, uuid, this));

        List<Component<F, R, B>> components = new ArrayList<>();
        for (ComponentProvider<F, R, B> provider : componentMap.values()) {
            List<Component<F, R, B>> newComponents = provider.provide(uuid, components.size());
            components.addAll(newComponents);
        }

        components.forEach(component -> component.apply(builder));
        formModifiers.forEach(modifier -> modifier.accept(uuid, builder));

        builder.validResultHandler((form, response) -> {
            components.forEach(component -> component.preHandle(form, response));
            components.forEach(component -> component.handle(form, response));
        });

        F form = builder.build();
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
}
