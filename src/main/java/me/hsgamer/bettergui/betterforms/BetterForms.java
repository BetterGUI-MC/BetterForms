package me.hsgamer.bettergui.betterforms;

import me.hsgamer.bettergui.api.addon.GetLogger;
import me.hsgamer.bettergui.betterforms.modal.ModalFormMenu;
import me.hsgamer.bettergui.betterforms.sender.FloodgateFormSender;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.betterforms.sender.GeyserFormSender;
import me.hsgamer.bettergui.betterforms.simple.SimpleFormMenu;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.logger.common.LogLevel;
import org.bukkit.Bukkit;

public final class BetterForms implements Expansion, GetLogger {
    private boolean isGeyserInstalled;
    private boolean isFloodgateInstalled;

    @Override
    public boolean onLoad() {
        isGeyserInstalled = Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null;
        isFloodgateInstalled = Bukkit.getPluginManager().getPlugin("floodgate") != null;

        if (!isGeyserInstalled && !isFloodgateInstalled) {
            getLogger().log(LogLevel.WARN, "You don't have Geyser or Floodgate installed. The addon won't work properly");
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        FormSender sender;
        if (isFloodgateInstalled) {
            sender = new FloodgateFormSender();
        } else if (isGeyserInstalled) {
            sender = new GeyserFormSender();
        } else {
            throw new IllegalStateException("Neither Geyser nor Floodgate is installed");
        }

        MenuBuilder.INSTANCE.register(config -> new ModalFormMenu(sender, config), "modal-form");
        MenuBuilder.INSTANCE.register(config -> new SimpleFormMenu(sender, config), "simple-form");
    }
}
