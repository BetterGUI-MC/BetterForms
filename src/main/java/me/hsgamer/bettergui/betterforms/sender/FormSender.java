package me.hsgamer.bettergui.betterforms.sender;

import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.UUID;

public interface FormSender {
    boolean canSendForm(UUID uuid);

    boolean sendForm(UUID uuid, FormBuilder<?, ?, ?> formBuilder);
}
