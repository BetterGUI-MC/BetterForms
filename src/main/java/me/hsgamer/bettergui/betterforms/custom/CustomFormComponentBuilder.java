package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.betterforms.common.FormComponentBuilder;
import me.hsgamer.bettergui.betterforms.custom.component.*;

import java.util.Map;

public class CustomFormComponentBuilder extends FormComponentBuilder<CustomFormComponentBuilder.Input, CustomFormComponent> {
    public static final CustomFormComponentBuilder INSTANCE = new CustomFormComponentBuilder();

    private CustomFormComponentBuilder() {
        register(SubmitComponent::new, "submit");
        register(IconComponent::new, "icon", "image");
        register(LabelComponent::new, "label", "text", "content");
        register(DropdownComponent::new, "dropdown", "select");
        register(InputComponent::new, "input");
        register(SliderComponent::new, "slider");
        register(StepSliderComponent::new, "step-slider", "step");
        register(ToggleComponent::new, "toggle", "switch");
    }

    @Override
    protected Map<String, Object> getOptions(Input input) {
        return input.options;
    }

    public static final class Input {
        public final CustomFormMenu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(CustomFormMenu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
