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
package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CommonButtonComponent implements FormComponent {
    protected final Menu menu;
    private final ActionApplier actionApplier;
    private final RequirementApplier requirementApplier;

    public CommonButtonComponent(Menu menu, String name, Object actionValue, Map<String, Object> requirementMap) {
        this.menu = menu;
        actionApplier = new ActionApplier(menu, actionValue);
        requirementApplier = new RequirementApplier(menu, name, requirementMap);
    }

    public CommonButtonComponent(Menu menu, String name, Map<String, Object> options) {
        this(
                menu,
                name,
                Optional.ofNullable(MapUtils.getIfFound(options, "action", "command")).orElse(Collections.emptyList()),
                Optional.ofNullable(MapUtils.getIfFound(options, "requirement", "click-requirement"))
                        .flatMap(MapUtils::castOptionalStringObjectMap)
                        .orElse(Collections.emptyMap())
        );
    }

    public void handleClick(UUID uuid) {
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
        SchedulerUtil.async().run(batchRunnable);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
