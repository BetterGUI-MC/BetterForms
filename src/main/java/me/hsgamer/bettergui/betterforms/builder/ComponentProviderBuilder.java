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

import me.hsgamer.bettergui.betterforms.component.ComponentProvider;
import me.hsgamer.bettergui.betterforms.component.impl.*;
import me.hsgamer.bettergui.betterforms.menu.FormMenu;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Objects;

public class ComponentProviderBuilder extends FunctionalMassBuilder<ComponentProviderBuilder.Input, ComponentProvider> {
    public static final ComponentProviderBuilder INSTANCE = new ComponentProviderBuilder();

    private ComponentProviderBuilder() {
        register(ButtonComponentProvider::new, "submit", "button");
        register(IconComponentProvider::new, "icon", "image");
        register(LabelComponentProvider::new, "label", "text", "content");
        register(input -> new OptionListComponentProvider(OptionListComponentProvider.Type.DROPDOWN, input), "dropdown", "select");
        register(InputComponentProvider::new, "input");
        register(SliderComponentProvider::new, "slider");
        register(input -> new OptionListComponentProvider(OptionListComponentProvider.Type.STEP_SLIDER, input), "step-slider", "step");
        register(ToggleComponentProvider::new, "toggle", "switch");
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
