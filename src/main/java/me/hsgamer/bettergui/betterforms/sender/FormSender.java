package me.hsgamer.bettergui.betterforms.sender;

import org.geysermc.cumulus.form.Form;

import java.util.UUID;

public interface FormSender {
    boolean canSendForm(UUID uuid);

    boolean sendForm(UUID uuid, Form form);
}
