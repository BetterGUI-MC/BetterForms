package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.betterforms.common.FormMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringLinkedMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.CaseInsensitivePathString;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

import java.util.*;
import java.util.function.Consumer;

public class CustomFormMenu extends FormMenu<CustomForm, CustomForm.Builder> {
    private final Map<String, CustomFormComponent> componentMap = new CaseInsensitiveStringLinkedMap<>();

    public CustomFormMenu(FormSender sender, Config config) {
        super(sender, config);

        for (Map.Entry<CaseInsensitivePathString, Object> configEntry : configSettings.entrySet()) {
            String key = PathString.toPath(configEntry.getKey().getPathString());
            MapUtils.castOptionalStringObjectMap(configEntry.getValue())
                    .map(CaseInsensitiveStringMap::new)
                    .map(map -> new CustomFormComponentBuilder.Input(this, key, map))
                    .flatMap(CustomFormComponentBuilder.INSTANCE::build)
                    .ifPresent(customFormComponent -> componentMap.put(key, customFormComponent));
        }

        variableManager.register("form_", StringReplacer.of((original, uuid) ->
                Optional.ofNullable(componentMap.get(original))
                        .map(customFormComponent -> customFormComponent.getValue(uuid))
                        .orElse(null))
        );
    }

    @Override
    protected Optional<Pair<CustomForm.Builder, Consumer<CustomForm>>> createFormConstructor(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        CustomForm.Builder builder = CustomForm.builder();
        List<Consumer<CustomForm>> consumerList = new ArrayList<>();
        componentMap.forEach((key, value) -> consumerList.add(value.apply(player.getUniqueId(), builder)));
        builder.validResultHandler((form, response) -> {
            Collection<CustomFormComponent> components = componentMap.values();
            components.forEach(component -> component.handle(uuid, form, response));
            components.forEach(component -> component.execute(uuid, form, response));
        });
        return Optional.of(Pair.of(builder, form -> consumerList.forEach(consumer -> consumer.accept(form))));
    }
}
