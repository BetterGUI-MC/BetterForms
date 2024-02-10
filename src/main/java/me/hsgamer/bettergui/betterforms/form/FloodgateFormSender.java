package me.hsgamer.bettergui.betterforms.form;

import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateFormSender implements FormSender {
    @Override
    public boolean sendForm(UUID uuid, FormBuilder<?, ?, ?> formBuilder) {
        return FloodgateApi.getInstance().sendForm(uuid, formBuilder);
    }
}
