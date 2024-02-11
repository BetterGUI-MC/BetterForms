package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.common.CommonButtonComponent;
import me.hsgamer.bettergui.betterforms.custom.CustomFormComponent;
import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.UUID;

public class SubmitComponent extends CommonButtonComponent implements CustomFormComponent {
    public SubmitComponent(CustomFormComponentBuilder.Input input) {
        super(input.menu, input.name, input.options);
    }

    @Override
    public void execute(UUID uuid, CustomForm form, CustomFormResponse response) {
        handleClick(uuid);
    }
}
