package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.betterforms.form.FormSender;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.floodgate.api.FloodgateApi;

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
        if (!FloodgateApi.getInstance().isFloodgatePlayer(uuid)) {
            return false;
        }

        Optional<T> formBuilder = createFormBuilder(player, args, bypass);
        if (formBuilder.isPresent()) {
            sender.sendForm(uuid, formBuilder.get());
            return true;
        }
        return false;
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
