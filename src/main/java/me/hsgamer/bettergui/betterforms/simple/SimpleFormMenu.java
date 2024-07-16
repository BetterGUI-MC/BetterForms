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
package me.hsgamer.bettergui.betterforms.simple;

import me.hsgamer.bettergui.betterforms.common.FormBuilderMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.*;

public class SimpleFormMenu extends FormBuilderMenu<SimpleForm, SimpleForm.Builder> {
    private final String content;
    private final Map<String, SimpleButtonComponent> buttonComponentMap = new LinkedHashMap<>();

    public SimpleFormMenu(FormSender sender, Config config) {
        super(sender, config);

        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");

        int index = 0;
        for (Map.Entry<String, Object> configEntry : configSettings.entrySet()) {
            String key = configEntry.getKey();
            Map<String, Object> value = MapUtils.castOptionalStringObjectMap(configEntry.getValue())
                    .<Map<String, Object>>map(CaseInsensitiveStringMap::new)
                    .orElseGet(Collections::emptyMap);
            buttonComponentMap.put(key, new SimpleButtonComponent(this, key, index++, value));
        }
    }

    @Override
    protected Optional<SimpleForm.Builder> createFormBuilder(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        SimpleForm.Builder builder = SimpleForm.builder();
        builder.content(StringReplacerApplier.replace(content, uuid, this));
        buttonComponentMap.values().forEach(component -> component.apply(uuid, builder));
        builder.validResultHandler(response -> buttonComponentMap.values().forEach(component -> component.handle(uuid, response)));
        return Optional.of(builder);
    }
}
