package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.UUID;
import java.util.function.Consumer;

public interface CustomFormComponent extends MenuElement {
    default Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        return form -> {
            // EMPTY
        };
    }

    default void handle(UUID uuid, CustomForm form, CustomFormResponse response) {
        // EMPTY
    }

    default void execute(UUID uuid, CustomForm form, CustomFormResponse response) {
        // EMPTY
    }

    default String getValue(UUID uuid) {
        return "";
    }
}
