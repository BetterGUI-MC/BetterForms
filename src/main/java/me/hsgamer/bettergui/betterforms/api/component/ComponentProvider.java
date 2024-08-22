package me.hsgamer.bettergui.betterforms.api.component;

import me.hsgamer.bettergui.api.menu.MenuElement;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.List;
import java.util.UUID;

public interface ComponentProvider<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends MenuElement {
    List<Component<F, R, B>> provide(UUID uuid, int currentComponentSize);

    default String getValue(UUID uuid, String args) {
        return "";
    }
}
