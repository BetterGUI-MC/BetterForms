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
package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class FormComponentBuilder<I, O> extends MassBuilder<I, O> {
    protected abstract Map<String, Object> getOptions(I input);

    protected String getDefaultType() {
        return null;
    }

    public void register(Function<I, O> creator, String... type) {
        register(input -> {
            Map<String, Object> keys = new CaseInsensitiveStringMap<>(getOptions(input));

            String button = Objects.toString(keys.get("type"), getDefaultType());
            if (button == null) {
                return Optional.empty();
            }

            for (String s : type) {
                if (button.equalsIgnoreCase(s)) {
                    return Optional.of(creator.apply(input));
                }
            }
            return Optional.empty();
        });
    }
}
