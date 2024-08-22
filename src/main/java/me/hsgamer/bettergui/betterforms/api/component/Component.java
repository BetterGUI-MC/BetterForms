package me.hsgamer.bettergui.betterforms.api.component;

import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

public interface Component<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends MenuElement {
    void apply(B builder);

    void handle(F form, R response);

    default void postApply(F form) {
        // EMPTY
    }

    default void preHandle(F form, R response) {
        // EMPTY
    }
}
