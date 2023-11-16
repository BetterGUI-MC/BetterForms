package me.hsgamer.bettergui.betterforms.menu;

import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ModalFormMenu extends StandardMenu {
    private final String title;
    private final String content;
    private final String button1;
    private final String button2;

    public ModalFormMenu(Config config) {
        super(config);

        title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "title"))
                .map(Object::toString)
                .orElse("");
        content = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "content"))
                .map(Object::toString)
                .orElse("");
        button1 = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "button1"))
                .map(Object::toString)
                .orElse("");
        button2 = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "button2"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public boolean create(Player player, String[] args, boolean bypass) {
        return false;
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void close(Player player) {

    }

    @Override
    public void closeAll() {

    }
}
