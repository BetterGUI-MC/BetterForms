package me.hsgamer.bettergui.betterforms.impl.custom;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

public class CustomFormMenu extends FormMenu<CustomForm, CustomFormResponse, CustomForm.Builder> {
    private static final ComponentProviderBuilder<CustomForm, CustomFormResponse, CustomForm.Builder> builder = new ComponentProviderBuilder<>();

    static {

    }

    public CustomFormMenu(FormSender sender, Config config) {
        super(sender, config);
    }

    @Override
    protected CustomForm.Builder createFormBuilder(Player player) {
        return CustomForm.builder();
    }

    @Override
    protected ComponentProviderBuilder<CustomForm, CustomFormResponse, CustomForm.Builder> getComponentProviderBuilder() {
        return builder;
    }
}
