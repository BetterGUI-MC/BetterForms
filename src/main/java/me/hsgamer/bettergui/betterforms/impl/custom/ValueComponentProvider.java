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
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ValueComponentProvider extends BaseComponentProvider<CustomForm, CustomFormResponse, CustomForm.Builder> {
    private final Map<UUID, String> valueMap = new ConcurrentHashMap<>();

    protected ValueComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
    }

    protected abstract void apply(UUID uuid, CustomForm.Builder builder);

    protected abstract String getValue(UUID uuid, CustomFormResponse response);

    @Override
    protected List<Component<CustomForm, CustomFormResponse, CustomForm.Builder>> provideChecked(UUID uuid, int index) {
        return Collections.singletonList(new Component<CustomForm, CustomFormResponse, CustomForm.Builder>() {
            @Override
            public void apply(CustomForm.Builder builder) {
                ValueComponentProvider.this.apply(uuid, builder);
            }

            @Override
            public void preHandle(CustomForm form, CustomFormResponse response) {
                valueMap.put(uuid, getValue(uuid, response));
            }

            @Override
            public void handle(CustomForm form, CustomFormResponse response) {
                // EMPTY
            }
        });
    }

    @Override
    public String getValue(UUID uuid, String args) {
        return valueMap.getOrDefault(uuid, "");
    }
}
