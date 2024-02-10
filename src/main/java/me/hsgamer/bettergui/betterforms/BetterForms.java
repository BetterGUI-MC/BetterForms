package me.hsgamer.bettergui.betterforms;

import me.hsgamer.bettergui.betterforms.modal.ModalFormMenu;
import me.hsgamer.bettergui.betterforms.simple.SimpleFormMenu;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.hscore.expansion.common.Expansion;

public final class BetterForms implements Expansion {
    @Override
    public void onEnable() {
        MenuBuilder.INSTANCE.register(ModalFormMenu::new, "modal-form");
        MenuBuilder.INSTANCE.register(SimpleFormMenu::new, "simple-form");
    }
}
