package me.hsgamer.bettergui.betterforms.util;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.util.FormImage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class ComponentUtil {
    public static Function<UUID, FormImage> createImageFunction(Map<String, Object> options, MenuElement menuElement) {
        Optional<String> path = Optional.ofNullable(MapUtils.getIfFound(options, "path")).map(Object::toString);
        Optional<String> url = Optional.ofNullable(MapUtils.getIfFound(options, "url")).map(Object::toString);

        FormImage.Type imageType = null;
        String imageValue = null;
        if (path.isPresent()) {
            imageType = FormImage.Type.PATH;
            imageValue = path.get();
        } else if (url.isPresent()) {
            imageType = FormImage.Type.URL;
            imageValue = url.get();
        }

        final FormImage.Type finalImageType = imageType;
        final String finalImageValue = imageValue;

        return uuid -> {
            if (finalImageType != null) {
                String replacedImageValue = StringReplacerApplier.replace(finalImageValue, uuid, menuElement);
                return FormImage.of(finalImageType, replacedImageValue);
            } else {
                return null;
            }
        };
    }
}
