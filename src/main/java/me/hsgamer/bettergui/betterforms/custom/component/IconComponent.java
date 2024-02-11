package me.hsgamer.bettergui.betterforms.custom.component;

import me.hsgamer.bettergui.betterforms.custom.CustomFormComponentBuilder;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class IconComponent extends StandardCustomFormComponent {
    private final Function<UUID, FormImage> imageFunction;

    public IconComponent(CustomFormComponentBuilder.Input input) {
        super(input);

        imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    public Consumer<CustomForm> apply(UUID uuid, CustomForm.Builder builder) {
        FormImage image = imageFunction.apply(uuid);
        if (image != null) {
            builder.icon(image);
        }
        return super.apply(uuid, builder);
    }
}
