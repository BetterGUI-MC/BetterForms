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
package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.betterforms.common.FormMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringLinkedMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

import java.util.*;
import java.util.function.Consumer;

public class CustomFormMenu extends FormMenu<CustomForm, CustomForm.Builder> {
    private final Map<String, CustomFormComponent> componentMap = new CaseInsensitiveStringLinkedMap<>();

    public CustomFormMenu(FormSender sender, Config config) {
        super(sender, config);

        for (Map.Entry<String, Object> configEntry : configSettings.entrySet()) {
            String key = configEntry.getKey();
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
