package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.UUID;

public abstract class ModalFormComponent implements MenuElement {
    protected final ModalFormComponentBuilder.Input input;

    protected ModalFormComponent(ModalFormComponentBuilder.Input input) {
        this.input = input;
    }

    public abstract void apply(UUID uuid, ModalForm.Builder builder);

    public abstract void handle(UUID uuid, ModalFormResponse response);

    @Override
    public Menu getMenu() {
        return input.menu;
    }
}
