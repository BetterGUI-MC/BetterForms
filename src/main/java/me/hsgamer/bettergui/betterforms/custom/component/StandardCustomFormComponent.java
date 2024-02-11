package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.custom.CustomFormComponent;
import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;

public class StandardCustomFormComponent implements CustomFormComponent {
    protected final CustomFormComponentBuilder.Input input;

    public StandardCustomFormComponent(CustomFormComponentBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }
}
