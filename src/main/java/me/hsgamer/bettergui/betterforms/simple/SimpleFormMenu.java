package me.hsgamer.bettergui.betterforms.simple;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;

import java.util.Optional;

public class SimpleFormMenu extends FormMenu<SimpleForm, SimpleFormResponse, SimpleForm.Builder> {
    private final String content;

    public SimpleFormMenu(FormSender sender, Config config) {
        super(sender, config);
        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected SimpleForm.Builder createFormBuilder(Player player) {
        return SimpleForm.builder().content(StringReplacerApplier.replace(content, player.getUniqueId(), this));
    }

    @Override
    protected ComponentProviderBuilder<SimpleForm, SimpleFormResponse, SimpleForm.Builder> getComponentProviderBuilder() {
        ComponentProviderBuilder<SimpleForm, SimpleFormResponse, SimpleForm.Builder> builder = new ComponentProviderBuilder<>();
        builder.register(SimpleButtonComponentProvider::new, "button", "");
        return builder;
    }
}
