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
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.common.BaseComponentProvider;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.util.FormImage;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class IconComponentProvider extends BaseComponentProvider {
    private final Function<UUID, FormImage> imageFunction;

    public IconComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);

        imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    protected List<Component> provideChecked(UUID uuid, int index) {
        return Collections.singletonList(new Component() {
            @Override
            public void apply(FormBuilder<?, ?, ?> builder) {
                if (builder instanceof CustomForm.Builder) {
                    FormImage image = imageFunction.apply(uuid);
                    if (image != null) {
                        ((CustomForm.Builder) builder).icon(image);
                    }
                }
            }

            @Override
            public void handle(Form form, FormResponse response) {
                // EMPTY
            }
        });
    }
}
