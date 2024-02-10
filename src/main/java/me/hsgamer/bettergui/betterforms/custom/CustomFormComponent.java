package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.UUID;

public interface CustomFormComponent extends MenuElement {
    void apply(UUID uuid, CustomForm.Builder builder);

    void handle(UUID uuid, CustomFormResponse response);
}
