package me.hsgamer.bettergui.betterforms.sender;

import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateFormSender implements FormSender {
    @Override
    public boolean canSendForm(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }

    @Override
    public boolean sendForm(UUID uuid, Form form) {
        return FloodgateApi.getInstance().sendForm(uuid, form);
    }
}
