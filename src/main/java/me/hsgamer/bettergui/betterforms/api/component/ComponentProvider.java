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
package me.hsgamer.bettergui.betterforms.api.component;

import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.List;
import java.util.UUID;

public interface ComponentProvider extends MenuElement {
    List<Component> provide(UUID uuid, int index);

    default String getValue(UUID uuid, String args) {
        return "";
    }
}
