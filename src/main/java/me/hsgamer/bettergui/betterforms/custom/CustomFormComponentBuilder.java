package me.hsgamer.bettergui.betterforms.custom;

import me.hsgamer.bettergui.betterforms.common.FormComponentBuilder;
import me.hsgamer.bettergui.betterforms.custom.component.DropdownComponent;
import me.hsgamer.bettergui.betterforms.custom.component.IconComponent;
import me.hsgamer.bettergui.betterforms.custom.component.LabelComponent;
import me.hsgamer.bettergui.betterforms.custom.component.SubmitComponent;

import java.util.Map;

public class CustomFormComponentBuilder extends FormComponentBuilder<CustomFormComponentBuilder.Input, CustomFormComponent> {
    public static final CustomFormComponentBuilder INSTANCE = new CustomFormComponentBuilder();

    private CustomFormComponentBuilder() {

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
