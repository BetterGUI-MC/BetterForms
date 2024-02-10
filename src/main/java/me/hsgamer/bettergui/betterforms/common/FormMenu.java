package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.betterforms.form.FormSender;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.Optional;
import java.util.UUID;

public abstract class FormMenu<T extends FormBuilder<?, ?, ?>> extends StandardMenu {
    private final FormSender sender;

    protected FormMenu(FormSender sender, Config config) {
        super(config);
        this.sender = sender;
    }

    protected abstract Optional<T> createFormBuilder(Player player, String[] args, boolean bypass);

    @Override
    public boolean create(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        if (!sender.canSendForm(uuid)) {
            return false;
        }

        Optional<T> formBuilder = createFormBuilder(player, args, bypass);
        return formBuilder.filter(t -> sender.sendForm(uuid, t)).isPresent();
    }

    @Override
    public void update(Player player) {
        // EMPTY
    }

    @Override
    public void close(Player player) {
        // EMPTY
    }

    @Override
    public void closeAll() {
        // EMPTY
    }
}
