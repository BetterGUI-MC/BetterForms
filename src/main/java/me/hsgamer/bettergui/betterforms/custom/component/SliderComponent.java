package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class SliderComponent extends ValueCustomFormComponent {
    private final String text;
    private final String min;
    private final String max;
    private final String step;
    private final String defaultValue;

    public SliderComponent(CustomFormComponentBuilder.Input input) {
        super(input);
        this.text = Optional.ofNullable(MapUtils.getIfFound(input.options, "text"))
                .map(Object::toString)
                .orElse("");
        this.min = Optional.ofNullable(MapUtils.getIfFound(input.options, "min"))
                .map(Object::toString)
                .orElse("0");
        this.max = Optional.ofNullable(MapUtils.getIfFound(input.options, "max"))
                .map(Object::toString)
                .orElse("100");
        this.step = Optional.ofNullable(MapUtils.getIfFound(input.options, "step"))
                .map(Object::toString)
                .orElse("1");
        this.defaultValue = Optional.ofNullable(MapUtils.getIfFound(input.options, "default"))
                .map(Object::toString)
                .orElse("0");
    }

    @Override
    protected String getValueFromResponse(CustomForm form, CustomFormResponse response) {
        return Float.toString(response.asSlider());
    }

    @Override
    public Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        String replacedText = StringReplacerApplier.replace(text, uuid, this);
        String replacedMin = StringReplacerApplier.replace(min, uuid, this);
        String replacedMax = StringReplacerApplier.replace(max, uuid, this);
        String replacedStep = StringReplacerApplier.replace(step, uuid, this);
        String replacedDefaultValue = StringReplacerApplier.replace(defaultValue, uuid, this);

        float min = Validate.getNumber(replacedMin).map(Number::floatValue).orElse(0F);
        float max = Validate.getNumber(replacedMax).map(Number::floatValue).orElse(100F);
        float step = Validate.getNumber(replacedStep).map(Number::floatValue).orElse(1F);
        float defaultValue = Validate.getNumber(replacedDefaultValue).map(Number::floatValue).orElse(0F);

        max = Math.max(min, max);
        defaultValue = Math.min(max, Math.max(min, defaultValue));

        builder.slider(replacedText, min, max, step, defaultValue);
        return super.apply(uuid, builder);
    }
}
