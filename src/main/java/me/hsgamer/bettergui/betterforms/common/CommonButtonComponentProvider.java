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
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Optional;
import java.util.UUID;

public abstract class CommonButtonComponentProvider<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> extends BaseComponentProvider<F, R, B> {
    private final ActionApplier actionApplier;
    private final RequirementApplier clickRequirementApplier;

    protected CommonButtonComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
        this.actionApplier = Optional.ofNullable(MapUtils.getIfFound(input.options, "action", "command"))
                .map(o -> new ActionApplier(input.menu, o))
                .orElse(ActionApplier.EMPTY);
        this.clickRequirementApplier = Optional.ofNullable(input.options.get("click-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new RequirementApplier(input.menu, input.name + "_click", m))
                .orElse(RequirementApplier.EMPTY);
    }

    protected void handleClick(UUID uuid) {
        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE)
                .addLast(process -> {
                    Requirement.Result result = clickRequirementApplier.getResult(uuid);
                    result.applier.accept(uuid, process);
                    if (result.isSuccess) {
                        process.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(actionProcess -> actionApplier.accept(uuid, actionProcess));
                    }
                    process.next();
                });
        SchedulerUtil.async().run(batchRunnable);
    }
}
