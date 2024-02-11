package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class LabelComponent extends StandardCustomFormComponent {
    private final String value;

    public LabelComponent(CustomFormComponentBuilder.Input input) {
        super(input);
        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content", "label"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        String replaced = StringReplacerApplier.replace(value, uuid, this);
        builder.label(replaced);
        return super.apply(uuid, builder);
    }
}
