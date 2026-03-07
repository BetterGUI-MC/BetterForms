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

import me.hsgamer.bettergui.betterforms.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.component.BaseComponentProvider;
import me.hsgamer.bettergui.betterforms.component.Component;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ValueComponentProvider extends BaseComponentProvider {
    private final Map<UUID, String> valueMap = new ConcurrentHashMap<>();

    protected ValueComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
    }

    protected abstract void apply(UUID uuid, CustomForm.Builder builder);

    protected abstract String getValue(UUID uuid, CustomFormResponse response);

    @Override
    protected List<Component> provideChecked(UUID uuid, int index) {
        return Collections.singletonList(new Component() {
            @Override
            public void apply(FormBuilder<?, ?, ?> builder) {
                if (builder instanceof CustomForm.Builder) {
                    ValueComponentProvider.this.apply(uuid, (CustomForm.Builder) builder);
                }
            }

            @Override
            public void handle(Form form, FormResponse response) {
                // EMPTY
            }

            @Override
            public void preHandle(Form form, FormResponse response) {
                if (response instanceof CustomFormResponse) {
                    valueMap.put(uuid, getValue(uuid, (CustomFormResponse) response));
                }
            }
        });
    }

    @Override
    public String getValue(UUID uuid, String args) {
        return valueMap.getOrDefault(uuid, "");
    }
}
