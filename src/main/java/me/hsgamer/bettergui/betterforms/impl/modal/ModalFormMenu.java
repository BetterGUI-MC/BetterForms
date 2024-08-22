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
package me.hsgamer.bettergui.betterforms.impl.modal;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Optional;

public class ModalFormMenu extends FormMenu<ModalForm, ModalFormResponse, ModalForm.Builder> {
    private static final ComponentProviderBuilder<ModalForm, ModalFormResponse, ModalForm.Builder> builder = new ComponentProviderBuilder<>();

    static {
        builder.register(ModalButtonComponentProvider::new, "button", "");
    }

    private final String content;

    public ModalFormMenu(FormSender sender, Config config) {
        super(sender, config);

        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected ModalForm.Builder createFormBuilder(Player player) {
        return ModalForm.builder().content(StringReplacerApplier.replace(content, player.getUniqueId(), this));
    }

    @Override
    protected ComponentProviderBuilder<ModalForm, ModalFormResponse, ModalForm.Builder> getComponentProviderBuilder() {
        return builder;
    }
}
