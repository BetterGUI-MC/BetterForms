package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ValueCustomFormComponent extends StandardCustomFormComponent {
    private final Map<UUID, String> valueMap = new ConcurrentHashMap<>();

    protected ValueCustomFormComponent(CustomFormComponentBuilder.Input input) {
        super(input);
    }

    protected abstract String getValueFromResponse(CustomForm form, CustomFormResponse response);

    @Override
    public void handle(UUID uuid, CustomForm form, CustomFormResponse response) {
        valueMap.put(uuid, getValueFromResponse(form, response));
    }

    @Override
    public String getValue(UUID uuid) {
        return valueMap.getOrDefault(uuid, "");
    }
}
