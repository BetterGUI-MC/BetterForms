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

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponentProvider;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ModalButtonComponentProvider extends CommonButtonComponentProvider<ModalForm, ModalFormResponse, ModalForm.Builder> {
    private final String value;

    public ModalButtonComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected List<Component<ModalForm, ModalFormResponse, ModalForm.Builder>> provideChecked(UUID uuid, int index) {
        boolean first = index == 0;
        return Collections.singletonList(new Component<ModalForm, ModalFormResponse, ModalForm.Builder>() {
            @Override
            public void apply(ModalForm.Builder builder) {
                String replaced = StringReplacerApplier.replace(value, uuid, ModalButtonComponentProvider.this);
                if (first) {
                    builder.button1(replaced);
                } else {
                    builder.button2(replaced);
                }
            }

            @Override
            public void handle(ModalForm form, ModalFormResponse response) {
                if (first == response.clickedFirst()) {
                    handleClick(uuid);
                }
            }
        });
    }
}
