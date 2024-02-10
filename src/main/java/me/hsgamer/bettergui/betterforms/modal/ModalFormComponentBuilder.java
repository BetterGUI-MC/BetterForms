package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.betterforms.common.FormComponentBuilder;
import me.hsgamer.bettergui.betterforms.modal.content.ButtonComponent;
import me.hsgamer.bettergui.betterforms.modal.content.ContentComponent;
import me.hsgamer.bettergui.betterforms.modal.content.TitleComponent;

import java.util.Map;

public class ModalFormComponentBuilder extends FormComponentBuilder<ModalFormComponentBuilder.Input, ModalFormComponent> {
    public static final ModalFormComponentBuilder INSTANCE = new ModalFormComponentBuilder();

    private ModalFormComponentBuilder() {
        register(TitleComponent::new, "title");
        register(ContentComponent::new, "content");
        register(ButtonComponent::new, "button");
    }

    @Override
    protected Map<String, Object> getOptions(Input input) {
        return input.options;
    }

    public static final class Input {
        public final ModalFormMenu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(ModalFormMenu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
