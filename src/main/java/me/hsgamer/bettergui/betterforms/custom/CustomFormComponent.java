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
package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.UUID;
import java.util.function.Consumer;

public interface CustomFormComponent extends MenuElement {
    default Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        return form -> {
            // EMPTY
        };
    }

    default void handle(UUID uuid, CustomForm form, CustomFormResponse response) {
        // EMPTY
    }

    default void execute(UUID uuid, CustomForm form, CustomFormResponse response) {
        // EMPTY
    }

    default String getValue(UUID uuid) {
        return "";
    }
}
