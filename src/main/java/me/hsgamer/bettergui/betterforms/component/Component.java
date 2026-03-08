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
package me.hsgamer.bettergui.betterforms.component;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.betterforms.builder.ComponentBuilder;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.Optional;
import java.util.UUID;

public abstract class Component implements MenuElement {
    protected final ComponentBuilder.Input input;

    protected Component(ComponentBuilder.Input input) {
        this.input = input;
    }

    public abstract Optional<FormResponseHandler> apply(UUID uuid, int index, FormBuilder<?, ?, ?> builder);

    public String getValue(UUID uuid, String args) {
        return "";
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }
}
