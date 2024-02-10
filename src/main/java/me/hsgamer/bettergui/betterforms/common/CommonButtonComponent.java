package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CommonButtonComponent implements MenuElement {
    protected final Menu menu;
    private final ActionApplier actionApplier;
    private final RequirementApplier requirementApplier;

    public CommonButtonComponent(Menu menu, String name, Map<String, Object> options) {
        this.menu = menu;

        actionApplier = Optional.ofNullable(MapUtils.getIfFound(options, "action", "command"))
                .map(o -> new ActionApplier(menu, o))
                .orElseGet(() -> new ActionApplier(Collections.emptyList()));

        Map<String, Object> requirementMap = Optional.ofNullable(MapUtils.getIfFound(options, "requirement", "click-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElse(Collections.emptyMap());
        requirementApplier = new RequirementApplier(menu, name, requirementMap);
    }

    protected void handleClick(UUID uuid) {
        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE)
                .addLast(process -> {
                    Requirement.Result result = requirementApplier.getResult(uuid);
                    result.applier.accept(uuid, process);
                    if (result.isSuccess) {
                        process.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(actionProcess -> actionApplier.accept(uuid, actionProcess));
                    }
                    process.next();
                });
        Scheduler.current().async().runTask(batchRunnable);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
