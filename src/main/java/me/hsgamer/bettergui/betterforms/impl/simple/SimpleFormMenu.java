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
package me.hsgamer.bettergui.betterforms.impl.simple;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;

import java.util.List;

public class SimpleFormMenu extends FormMenu<SimpleForm, SimpleFormResponse, SimpleForm.Builder> {
    private static final ComponentProviderBuilder<SimpleForm, SimpleFormResponse, SimpleForm.Builder> builder = new ComponentProviderBuilder<>();

    static {
        builder.register(SimpleButtonComponentProvider::new, "button", "");
    }

    private final StringBuilder content = new StringBuilder();

    public SimpleFormMenu(FormSender sender, Config config) {
        super(sender, config);

        Object source = MapUtils.getIfFound(menuSettings, "content");
        if (source == null) {
            return;
        }
        if (source instanceof List<?>) {
            for (Object item : (List<?>) source) {
                content.append(item.toString()).append("\n");
            }
            return;
        }
        content.append(source);
    }

    @Override
    protected SimpleForm.Builder createFormBuilder(Player player) {
        return SimpleForm.builder().content(StringReplacerApplier.replace(content.toString(), player.getUniqueId(), this));
    }

    @Override
    protected ComponentProviderBuilder<SimpleForm, SimpleFormResponse, SimpleForm.Builder> getComponentProviderBuilder() {
        return builder;
    }
}
