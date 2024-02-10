package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponent;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ModalButtonComponent extends CommonButtonComponent {
    private final String value;
    private final boolean first;

    public ModalButtonComponent(Menu menu, String name, boolean first, Map<String, Object> options) {
        super(menu, name, options);
        this.first = first;

        value = Optional.ofNullable(MapUtils.getIfFound(options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
    }

    public void apply(UUID uuid, ModalForm.Builder builder) {
        String replaced = StringReplacerApplier.replace(value, uuid, this);
        if (first) {
            builder.button1(replaced);
        } else {
            builder.button2(replaced);
        }
    }

    public void handle(UUID uuid, ModalFormResponse response) {
        if (first == response.clickedFirst()) {
            handle(uuid);
        }
    }
}
