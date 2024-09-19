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
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class OptionListComponentProvider extends ValueComponentProvider {
    private final Type type;
    private final Map<UUID, List<String>> optionsMap = new ConcurrentHashMap<>();
    private final String text;
    private final List<String> options;
    private final String defaultOption;

    public OptionListComponentProvider(Type type, ComponentProviderBuilder.Input input) {
        super(input);
        this.type = type;
        this.text = ofNullable(MapUtils.getIfFound(input.options, "text"))
                .map(Object::toString)
                .orElse("");
        this.options = ofNullable(MapUtils.getIfFound(input.options, "option", "options"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(Collections.emptyList());
        this.defaultOption = ofNullable(MapUtils.getIfFound(input.options, "default-option", "default"))
                .map(Objects::toString)
                .orElse("0");
    }

    @Override
    protected void apply(UUID uuid, CustomForm.Builder builder) {
        String replacedText = StringReplacerApplier.replace(this.text, uuid, this);
        List<String> replacedOptions = this.options.stream()
                .map(s -> StringReplacerApplier.replace(s, uuid, this))
                .collect(Collectors.toList());
        String replacedDefaultOption = StringReplacerApplier.replace(this.defaultOption, uuid, this);
        int defaultOption = Validate.getNumber(replacedDefaultOption).map(Number::intValue).orElse(0);

        switch (this.type) {
            case DROPDOWN:
                builder.dropdown(replacedText, replacedOptions, defaultOption);
                break;
            case STEP_SLIDER:
                builder.stepSlider(replacedText, replacedOptions, defaultOption);
                break;
        }
        this.optionsMap.put(uuid, replacedOptions);
    }

    @Override
    protected String getValue(UUID uuid, CustomFormResponse response) {
        int index = 0;
        switch (this.type) {
            case DROPDOWN:
                index = response.asDropdown();
                break;
            case STEP_SLIDER:
                index = response.asStepSlider();
                break;
        }
        return this.optionsMap.get(uuid).get(index);
    }

    public enum Type {
        DROPDOWN,
        STEP_SLIDER
    }
}
