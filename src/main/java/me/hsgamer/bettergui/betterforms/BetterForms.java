/*
   Copyright 2024 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.bettergui.betterforms;

import me.hsgamer.bettergui.api.addon.GetLogger;
import me.hsgamer.bettergui.betterforms.custom.CustomFormMenu;
import me.hsgamer.bettergui.betterforms.modal.ModalFormMenu;
import me.hsgamer.bettergui.betterforms.sender.FloodgateFormSender;
import me.hsgamer.bettergui.betterforms.sender.FormSender;
import me.hsgamer.bettergui.betterforms.sender.GeyserFormSender;
import me.hsgamer.bettergui.betterforms.simple.SimpleFormMenu;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.license.common.LicenseChecker;
import me.hsgamer.hscore.license.common.LicenseResult;
import me.hsgamer.hscore.license.polymart.PolymartLicenseChecker;
import me.hsgamer.hscore.license.spigotmc.SpigotLicenseChecker;
import me.hsgamer.hscore.logger.common.LogLevel;
import me.hsgamer.hscore.variable.VariableBundle;
import org.bukkit.Bukkit;

public final class BetterForms implements Expansion, GetLogger {
    private final VariableBundle variableBundle = new VariableBundle();
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
        checkLicense();

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
        MenuBuilder.INSTANCE.register(config -> new CustomFormMenu(sender, config), "custom-form");

        variableBundle.register("forms_accept", StringReplacer.of((original, uuid) -> Boolean.toString(sender.canSendForm(uuid))), true);
    }

    private void checkLicense() {
        LicenseChecker licenseChecker = PolymartLicenseChecker.isAvailable()
                ? new PolymartLicenseChecker("5616", true, true)
                : new SpigotLicenseChecker("115565");
        Scheduler.current().async().runTask(() -> {
            LicenseResult result = licenseChecker.checkLicense();
            switch (result.getStatus()) {
                case VALID:
                    getLogger().log(LogLevel.INFO, "Thank you for supporting BetterForms. Your support is greatly appreciated");
                    break;
                case INVALID:
                    getLogger().log(LogLevel.WARN, "Thank you for using BetterForms");
                    getLogger().log(LogLevel.WARN, "If you like this addon, please consider supporting it by purchasing from one of these platforms:");
                    getLogger().log(LogLevel.WARN, "- SpigotMC: https://www.spigotmc.org/resources/betterforms.115565/");
                    getLogger().log(LogLevel.WARN, "- Polymart: https://polymart.org/resource/betterforms.5616");
                    break;
                case OFFLINE:
                    getLogger().log(LogLevel.WARN, "Cannot check your license for BetterForms. Please check your internet connection");
                    getLogger().log(LogLevel.WARN, "Note: You can still use this addon without a license, and there is no limit on the features");
                    getLogger().log(LogLevel.WARN, "However, if you like this addon, please consider supporting it by purchasing it from one of these platforms:");
                    getLogger().log(LogLevel.WARN, "- SpigotMC: https://www.spigotmc.org/resources/betterforms.115565/");
                    getLogger().log(LogLevel.WARN, "- Polymart: https://polymart.org/resource/betterforms.5616");
                    break;
                case UNKNOWN:
                    getLogger().log(LogLevel.WARN, "Cannot check your license for BetterForms. Please try again later");
                    getLogger().log(LogLevel.WARN, "Note: You can still use this addon without a license, and there is no limit on the features");
                    break;
            }
        });
    }
}
