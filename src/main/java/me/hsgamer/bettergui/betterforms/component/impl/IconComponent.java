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
package me.hsgamer.bettergui.betterforms.component.impl;

import me.hsgamer.bettergui.betterforms.builder.ComponentBuilder;
import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.component.FormResponseHandler;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.util.FormImage;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class IconComponent extends Component {
    private final Function<UUID, FormImage> imageFunction;

    public IconComponent(ComponentBuilder.Input input) {
        super(input);

        imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    public Optional<FormResponseHandler> apply(UUID uuid, int index, FormBuilder<?, ?, ?> builder) {
        if (builder instanceof CustomForm.Builder) {
            FormImage image = imageFunction.apply(uuid);
            if (image != null) {
                ((CustomForm.Builder) builder).icon(image);
            }
            return Optional.of(FormResponseHandler.EMPTY);
        }
        return Optional.empty();
    }
}
