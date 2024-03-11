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
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ValueCustomFormComponent extends StandardCustomFormComponent {
    private final Map<UUID, String> valueMap = new ConcurrentHashMap<>();

    protected ValueCustomFormComponent(CustomFormComponentBuilder.Input input) {
        super(input);
    }

    protected abstract String getValueFromResponse(CustomForm form, CustomFormResponse response);

    @Override
    public void handle(UUID uuid, CustomForm form, CustomFormResponse response) {
        valueMap.put(uuid, getValueFromResponse(form, response));
    }

    @Override
    public String getValue(UUID uuid) {
        return valueMap.getOrDefault(uuid, "");
    }
}
