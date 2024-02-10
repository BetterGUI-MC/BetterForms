package me.hsgamer.bettergui.betterforms.sender;

import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class GeyserFormSender implements FormSender {
    @Override
    public boolean canSendForm(UUID uuid) {
        return GeyserApi.api().isBedrockPlayer(uuid);
    }

    @Override
    public boolean sendForm(UUID uuid, FormBuilder<?, ?, ?> formBuilder) {
        return GeyserApi.api().sendForm(uuid, formBuilder);
    }
}
