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
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Optional;
import java.util.UUID;

public class ToggleComponent extends ValueComponent {
    private final String text;
    private final String defaultValue;
    private final String onTrue;
    private final String onFalse;

    public ToggleComponent(ComponentBuilder.Input input) {
        super(input);

        this.text = Optional.ofNullable(MapUtils.getIfFound(input.options, "text"))
                .map(Object::toString)
                .orElse("");
        this.defaultValue = Optional.ofNullable(MapUtils.getIfFound(input.options, "default"))
                .map(Object::toString)
                .orElse("false");
        this.onTrue = Optional.ofNullable(MapUtils.getIfFound(input.options, "on-true"))
                .map(Object::toString)
                .orElse("true");
        this.onFalse = Optional.ofNullable(MapUtils.getIfFound(input.options, "on-false"))
                .map(Object::toString)
                .orElse("false");
    }

    @Override
    protected void apply(UUID uuid, CustomForm.Builder builder) {
        String replacedText = StringReplacerApplier.replace(text, uuid, this);
        String replacedDefaultValue = StringReplacerApplier.replace(defaultValue, uuid, this);
        boolean defaultValue = Boolean.parseBoolean(replacedDefaultValue);
        builder.toggle(replacedText, defaultValue);
    }

    @Override
    protected String getValue(UUID uuid, CustomFormResponse response) {
        return Boolean.toString(response.asToggle());
    }

    @Override
    public String getValue(UUID uuid, String args) {
        String text = Boolean.parseBoolean(super.getValue(uuid, args)) ? onTrue : onFalse;
        return StringReplacerApplier.replace(text, uuid, this);
    }
}
