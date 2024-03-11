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

import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class FormBuilderMenu<F extends Form, B extends FormBuilder<?, F, ?>> extends FormMenu<F, B> {
    protected FormBuilderMenu(FormSender sender, Config config) {
        super(sender, config);
    }

    protected abstract Optional<B> createFormBuilder(Player player, String[] args, boolean bypass);

    @Override
    protected Optional<Pair<B, Consumer<F>>> createFormConstructor(Player player, String[] args, boolean bypass) {
        return createFormBuilder(player, args, bypass).map(builder -> Pair.of(builder, form -> {
        }));
    }
}
