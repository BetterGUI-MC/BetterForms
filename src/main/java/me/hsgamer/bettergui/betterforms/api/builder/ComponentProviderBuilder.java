package me.hsgamer.bettergui.betterforms.api.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.api.component.ComponentProvider;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Map;
import java.util.Objects;

public class ComponentProviderBuilder<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends FunctionalMassBuilder<ComponentProviderBuilder.Input, ComponentProvider<F, R, B>> {
    protected String getDefaultType() {
        return "";
    }

    @Override
    protected String getType(Input input) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
        return Objects.toString(keys.get("type"), getDefaultType());
    }

    public static final class Input {
        public final Menu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(Menu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
