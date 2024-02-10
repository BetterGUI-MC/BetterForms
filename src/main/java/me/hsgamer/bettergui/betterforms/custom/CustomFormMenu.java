package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.betterforms.common.FormMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Optional;

public class CustomFormMenu extends FormMenu<CustomForm.Builder> {
    public CustomFormMenu(FormSender sender, Config config) {
        super(sender, config);
    }

    @Override
    protected Optional<CustomForm.Builder> createFormBuilder(Player player, String[] args, boolean bypass) {
        return Optional.empty();
    }
}
