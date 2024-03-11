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
package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class IconComponent extends StandardCustomFormComponent {
    private final Function<UUID, FormImage> imageFunction;

    public IconComponent(CustomFormComponentBuilder.Input input) {
        super(input);

        imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    public Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        FormImage image = imageFunction.apply(uuid);
        if (image != null) {
            builder.icon(image);
        }
        return super.apply(uuid, builder);
    }
}
