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
package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponent;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ModalButtonComponent extends CommonButtonComponent {
    private final String value;
    private final boolean first;

    public ModalButtonComponent(Menu menu, String name, boolean first, Map<String, Object> options) {
        super(menu, name, options);
        this.first = first;

        value = Optional.ofNullable(MapUtils.getIfFound(options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
    }

    public void apply(UUID uuid, ModalForm.Builder builder) {
        String replaced = StringReplacerApplier.replace(value, uuid, this);
        if (first) {
            builder.button1(replaced);
        } else {
            builder.button2(replaced);
        }
    }

    public void handle(UUID uuid, ModalFormResponse response) {
        if (first == response.clickedFirst()) {
            handleClick(uuid);
        }
    }
}
