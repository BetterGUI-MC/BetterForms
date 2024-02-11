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
