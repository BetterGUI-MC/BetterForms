package me.hsgamer.bettergui.betterforms.simple;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.betterforms.common.CommonButtonComponent;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SimpleButtonComponent extends CommonButtonComponent {
    private final int index;
    private final String value;
    private final Function<UUID, FormImage> imageFunction;

    public SimpleButtonComponent(Menu menu, String name, int index, Map<String, Object> options) {
        super(menu, name, options);
        this.index = index;

        value = Optional.ofNullable(MapUtils.getIfFound(options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
        imageFunction = ComponentUtil.createImageFunction(options, this);
    }

    public void apply(UUID uuid, SimpleForm.Builder builder) {
        String replaced = StringReplacerApplier.replace(value, uuid, this);
        FormImage image = imageFunction.apply(uuid);
        builder.button(replaced, image);
    }

    public void handle(UUID uuid, SimpleFormResponse response) {
        if (response.clickedButtonId() == index) {
            handleClick(uuid);
        }
    }
}
