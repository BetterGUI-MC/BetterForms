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
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponentProvider;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SimpleButtonComponentProvider extends CommonButtonComponentProvider<SimpleForm, SimpleFormResponse, SimpleForm.Builder> {
    private final String value;
    private final Function<UUID, FormImage> imageFunction;

    public SimpleButtonComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);

        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
        imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    protected List<Component<SimpleForm, SimpleFormResponse, SimpleForm.Builder>> provideChecked(UUID uuid, int index) {
        return Collections.singletonList(new Component<SimpleForm, SimpleFormResponse, SimpleForm.Builder>() {
            @Override
            public void apply(SimpleForm.Builder builder) {
                String replaced = StringReplacerApplier.replace(value, uuid, SimpleButtonComponentProvider.this);
                FormImage image = imageFunction.apply(uuid);
                builder.button(replaced, image);
            }

            @Override
            public void handle(SimpleForm form, SimpleFormResponse response) {
                if (response.clickedButtonId() == index) {
                    handleClick(uuid);
                }
            }
        });
    }
}
