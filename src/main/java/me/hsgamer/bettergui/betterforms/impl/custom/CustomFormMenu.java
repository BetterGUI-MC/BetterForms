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
package me.hsgamer.bettergui.betterforms.impl.custom;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

public class CustomFormMenu extends FormMenu<CustomForm, CustomFormResponse, CustomForm.Builder> {
    private static final ComponentProviderBuilder<CustomForm, CustomFormResponse, CustomForm.Builder> builder = new ComponentProviderBuilder<>();

    static {
        builder.register(SubmitComponentProvider::new, "submit", "button");
        builder.register(IconComponentProvider::new, "icon", "image");
        builder.register(LabelComponentProvider::new, "label", "text", "content");
        builder.register(DropdownComponentProvider::new, "dropdown", "select");
        builder.register(InputComponentProvider::new, "input");
        builder.register(SubmitComponentProvider::new, "slider");
        builder.register(StepSliderComponentProvider::new, "step-slider", "step");
        builder.register(SubmitComponentProvider::new, "toggle", "switch");
    }

    public CustomFormMenu(FormSender sender, Config config) {
        super(sender, config);
    }

    @Override
    protected CustomForm.Builder createFormBuilder(Player player) {
        return CustomForm.builder();
    }

    @Override
    protected ComponentProviderBuilder<CustomForm, CustomFormResponse, CustomForm.Builder> getComponentProviderBuilder() {
        return builder;
    }
}
