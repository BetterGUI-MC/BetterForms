package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.api.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Optional;

public class ModalFormMenu extends FormMenu<ModalForm, ModalFormResponse, ModalForm.Builder> {
    private final String content;

    public ModalFormMenu(FormSender sender, Config config) {
        super(sender, config);

        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected ModalForm.Builder createFormBuilder(Player player) {
        return ModalForm.builder().content(StringReplacerApplier.replace(content, player.getUniqueId(), this));
    }

    @Override
    protected ComponentProviderBuilder<ModalForm, ModalFormResponse, ModalForm.Builder> getComponentProviderBuilder() {
        ComponentProviderBuilder<ModalForm, ModalFormResponse, ModalForm.Builder> builder = new ComponentProviderBuilder<>();
        builder.register(ModalButtonComponentProvider::new, "button", "");
        return builder;
    }
}
