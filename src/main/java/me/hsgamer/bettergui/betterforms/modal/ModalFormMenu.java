package me.hsgamer.bettergui.betterforms.modal;

import me.hsgamer.bettergui.betterforms.common.FormMenu;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.CaseInsensitivePathString;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;

import java.util.*;

public class ModalFormMenu extends FormMenu<ModalForm.Builder> {
    private final String content;
    private final Map<String, ModalButtonComponent> buttonComponentMap = new LinkedHashMap<>();

    public ModalFormMenu(FormSender sender, Config config) {
        super(sender, config);

        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");

        boolean first = true;
        for (Map.Entry<CaseInsensitivePathString, Object> configEntry : configSettings.entrySet()) {
            String key = PathString.toPath(configEntry.getKey().getPathString());
            Map<String, Object> value = MapUtils.castOptionalStringObjectMap(configEntry.getValue())
                    .<Map<String, Object>>map(CaseInsensitiveStringMap::new)
                    .orElseGet(Collections::emptyMap);
            buttonComponentMap.put(key, new ModalButtonComponent(this, key, first, value));
            first = false;
        }
    }

    @Override
    protected Optional<ModalForm.Builder> createFormBuilder(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        ModalForm.Builder builder = ModalForm.builder();
        builder.content(StringReplacerApplier.replace(content, uuid, this));
        buttonComponentMap.values().forEach(component -> component.apply(uuid, builder));
        builder.validResultHandler(response -> buttonComponentMap.values().forEach(component -> component.handle(uuid, response)));
        return Optional.of(builder);
    }
}
