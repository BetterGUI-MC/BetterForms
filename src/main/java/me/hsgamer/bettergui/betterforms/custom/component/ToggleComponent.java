package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

public class ToggleComponent extends ValueCustomFormComponent {
    public ToggleComponent(CustomFormComponentBuilder.Input input) {
        super(input);
    }

    @Override
    protected String getValueFromResponse(CustomForm form, CustomFormResponse response) {
        return Boolean.toString(response.asToggle());
    }
}
