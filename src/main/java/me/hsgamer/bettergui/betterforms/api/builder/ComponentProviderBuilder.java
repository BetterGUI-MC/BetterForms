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
package me.hsgamer.bettergui.betterforms.api.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.api.component.ComponentProvider;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Map;
import java.util.Objects;

public class ComponentProviderBuilder<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends FunctionalMassBuilder<ComponentProviderBuilder.Input, ComponentProvider<F, R, B>> {
    protected String getDefaultType() {
        return "";
    }

    @Override
    protected String getType(Input input) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
        return Objects.toString(keys.get("type"), getDefaultType());
    }

    public static final class Input {
        public final Menu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(Menu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
