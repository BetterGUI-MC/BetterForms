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
package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.betterforms.common.FormBuilderMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;

import java.util.*;

public class ModalFormMenu extends FormBuilderMenu<ModalForm, ModalForm.Builder> {
    private final String content;
    private final Map<String, ModalButtonComponent> buttonComponentMap = new LinkedHashMap<>();

    public ModalFormMenu(FormSender sender, Config config) {
        super(sender, config);

        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");

        boolean first = true;
        for (Map.Entry<String, Object> configEntry : configSettings.entrySet()) {
            String key = configEntry.getKey();
            Map<String, Object> value = MapUtils.castOptionalStringObjectMap(configEntry.getValue())
                    .<Map<String, Object>>map(CaseInsensitiveStringMap::new)
                    .orElseGet(Collections::emptyMap);
            buttonComponentMap.put(key, new ModalButtonComponent(this, key, first, value));
            first = false;
        }
    }

    @Override
    protected Optional<ModalForm.Builder> createFormBuilder(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        ModalForm.Builder builder = ModalForm.builder();
        builder.content(StringReplacerApplier.replace(content, uuid, this));
        buttonComponentMap.values().forEach(component -> component.apply(uuid, builder));
        builder.validResultHandler(response -> buttonComponentMap.values().forEach(component -> component.handle(uuid, response)));
        return Optional.of(builder);
    }
}
