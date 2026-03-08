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
package me.hsgamer.bettergui.betterforms.builder;

import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.component.impl.*;
import me.hsgamer.bettergui.betterforms.menu.FormMenu;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Objects;

public class ComponentBuilder extends FunctionalMassBuilder<ComponentBuilder.Input, Component> {
    public static final ComponentBuilder INSTANCE = new ComponentBuilder();

    private ComponentBuilder() {
        register(ButtonComponent::new, "submit", "button");
        register(IconComponent::new, "icon", "image");
        register(LabelComponent::new, "label", "text", "content");
        register(input -> new OptionListComponent(OptionListComponent.Type.DROPDOWN, input), "dropdown", "select");
        register(InputComponent::new, "input");
        register(SliderComponent::new, "slider");
        register(input -> new OptionListComponent(OptionListComponent.Type.STEP_SLIDER, input), "step-slider", "step");
        register(ToggleComponent::new, "toggle", "switch");
        register(ConditionalComponent::new, "conditional", "predicate");
        register(ListComponent::new, "list");
    }

    @Override
    protected String getType(Input input) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
        return Objects.toString(keys.get("type"), input.menu.getDefaultComponentType());
    }

    public static final class Input {
        public final FormMenu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(FormMenu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
