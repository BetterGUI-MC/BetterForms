package me.hsgamer.bettergui.betterforms.modal.content;

import me.hsgamer.bettergui.betterforms.modal.ModalFormComponent;
import me.hsgamer.bettergui.betterforms.modal.ModalFormComponentBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Optional;
import java.util.UUID;

public class ContentComponent extends ModalFormComponent {
    private final String value;

    public ContentComponent(ModalFormComponentBuilder.Input input) {
        super(input);
        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public void apply(UUID uuid, ModalForm.Builder builder) {
        builder.content(StringReplacerApplier.replace(value, uuid, this));
    }

    @Override
    public void handle(UUID uuid, ModalFormResponse response) {
        // EMPTY
    }
}
