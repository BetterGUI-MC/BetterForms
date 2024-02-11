package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class InputComponent extends ValueCustomFormComponent {
    private final String text;
    private final String placeholder;
    private final String defaultText;

    public InputComponent(CustomFormComponentBuilder.Input input) {
        super(input);
        this.text = Optional.ofNullable(MapUtils.getIfFound(input.options, "text"))
                .map(Object::toString)
                .orElse("");
        this.placeholder = Optional.ofNullable(MapUtils.getIfFound(input.options, "placeholder"))
                .map(Object::toString)
                .orElse("");
        this.defaultText = Optional.ofNullable(MapUtils.getIfFound(input.options, "default", "default-text"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected String getValueFromResponse(CustomForm form, CustomFormResponse response) {
        return response.asInput();
    }

    @Override
    public Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        String replacedText = StringReplacerApplier.replace(text, uuid, this);
        String replacedPlaceholder = StringReplacerApplier.replace(placeholder, uuid, this);
        String replacedDefaultText = StringReplacerApplier.replace(defaultText, uuid, this);
        builder.input(replacedText, replacedPlaceholder, replacedDefaultText);
        return super.apply(uuid, builder);
    }
}
