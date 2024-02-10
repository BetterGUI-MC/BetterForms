package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public abstract class FormMenu<T extends FormBuilder<?, ?, ?>> extends StandardMenu {
    private final FormSender sender;
    private final String title;
    private final List<Permission> permissions;
    private final ArgumentHandler argumentHandler;

    protected FormMenu(FormSender sender, Config config) {
        super(config);
        this.sender = sender;

        title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "title"))
                .map(Object::toString)
                .orElse("");

        permissions = Optional.ofNullable(menuSettings.get("permission"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
                .orElse(Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName())));

        Optional.ofNullable(menuSettings.get("command"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .ifPresent(list -> {
                    for (String s : list) {
                        if (s.contains(" ")) {
                            getInstance().getLogger().warning("Illegal characters in command '" + s + "'" + "in the menu '" + getName() + "'. Ignored");
                        } else {
                            getInstance().getMenuCommandManager().registerMenuCommand(s, this);
                        }
                    }
                });

        argumentHandler = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "argument-processor", "arg-processor", "argument", "arg"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new ArgumentHandler(this, m))
                .orElseGet(() -> new ArgumentHandler(this, Collections.emptyMap()));
    }

    protected abstract Optional<T> createFormBuilder(Player player, String[] args, boolean bypass);

    @Override
    public boolean create(Player player, String[] args, boolean bypass) {
        UUID uuid = player.getUniqueId();
        if (!sender.canSendForm(uuid)) {
            return false;
        }

        // Check Argument
        if (!argumentHandler.process(uuid, args).isPresent()) {
            return false;
        }

        // Check Permission
        if (!bypass && !PermissionUtils.hasAnyPermission(player, permissions)) {
            MessageUtils.sendMessage(player, getInstance().getMessageConfig().getNoPermission());
            return false;
        }

        Optional<T> formBuilder = createFormBuilder(player, args, bypass);
        if (!formBuilder.isPresent()) {
            return false;
        }

        T builder = formBuilder.get();
        builder.title(StringReplacerApplier.replace(title, uuid, this));
        return sender.sendForm(uuid, builder);
    }

    @Override
    public void update(Player player) {
        // EMPTY
    }

    @Override
    public void close(Player player) {
        // EMPTY
    }

    @Override
    public void closeAll() {
        // EMPTY
    }
}
