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
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Optional;
import java.util.UUID;

public class SliderComponentProvider extends ValueComponentProvider {
    private final String text;
    private final String min;
    private final String max;
    private final String step;
    private final String defaultValue;

    public SliderComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);

        this.text = Optional.ofNullable(MapUtils.getIfFound(input.options, "text"))
                .map(Object::toString)
                .orElse("");
        this.min = Optional.ofNullable(MapUtils.getIfFound(input.options, "min"))
                .map(Object::toString)
                .orElse("0");
        this.max = Optional.ofNullable(MapUtils.getIfFound(input.options, "max"))
                .map(Object::toString)
                .orElse("100");
        this.step = Optional.ofNullable(MapUtils.getIfFound(input.options, "step"))
                .map(Object::toString)
                .orElse("1");
        this.defaultValue = Optional.ofNullable(MapUtils.getIfFound(input.options, "default"))
                .map(Object::toString)
                .orElse("0");
    }

    @Override
    protected void apply(UUID uuid, CustomForm.Builder builder) {
        String replacedText = StringReplacerApplier.replace(text, uuid, this);
        String replacedMin = StringReplacerApplier.replace(min, uuid, this);
        String replacedMax = StringReplacerApplier.replace(max, uuid, this);
        String replacedStep = StringReplacerApplier.replace(step, uuid, this);
        String replacedDefaultValue = StringReplacerApplier.replace(defaultValue, uuid, this);

        float min = Validate.getNumber(replacedMin).map(Number::floatValue).orElse(0F);
        float max = Validate.getNumber(replacedMax).map(Number::floatValue).orElse(100F);
        float step = Validate.getNumber(replacedStep).map(Number::floatValue).orElse(1F);
        float defaultValue = Validate.getNumber(replacedDefaultValue).map(Number::floatValue).orElse(0F);

        max = Math.max(min, max);
        defaultValue = Math.min(max, Math.max(min, defaultValue));

        builder.slider(replacedText, min, max, step, defaultValue);
    }

    @Override
    protected String getValue(UUID uuid, CustomFormResponse response) {
        return Float.toString(response.asSlider());
    }
}
