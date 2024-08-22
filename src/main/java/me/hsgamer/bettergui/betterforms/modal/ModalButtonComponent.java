package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponentProvider;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ModalButtonComponent extends CommonButtonComponentProvider<ModalForm, ModalFormResponse, ModalForm.Builder> {
    private final String value;

    public ModalButtonComponent(ComponentProviderBuilder.Input input) {
        super(input);
        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    protected List<Component<ModalForm, ModalFormResponse, ModalForm.Builder>> provideChecked(UUID uuid, int currentComponentSize) {
        boolean first = currentComponentSize == 0;
        return Collections.singletonList(new Component<ModalForm, ModalFormResponse, ModalForm.Builder>() {
            @Override
            public void apply(ModalForm.Builder builder) {
                String replaced = StringReplacerApplier.replace(value, uuid, ModalButtonComponent.this);
                if (first) {
                    builder.button1(replaced);
                } else {
                    builder.button2(replaced);
                }
            }

            @Override
            public void handle(ModalForm form, ModalFormResponse response) {
                if (first == response.clickedFirst()) {
                    handleClick(uuid);
                }
            }
        });
    }
}
