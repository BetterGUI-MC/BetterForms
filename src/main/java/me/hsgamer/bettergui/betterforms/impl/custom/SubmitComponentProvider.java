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
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponentProvider;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SubmitComponentProvider extends CommonButtonComponentProvider {
    public SubmitComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
    }

    @Override
    protected List<Component> provideChecked(UUID uuid, int index) {
        return Collections.singletonList(new Component() {
            @Override
            public void apply(FormBuilder<?, ?, ?> builder) {
                // EMPTY
            }

            @Override
            public void handle(Form form, FormResponse response) {
                handleClick(uuid);
            }
        });
    }
}
