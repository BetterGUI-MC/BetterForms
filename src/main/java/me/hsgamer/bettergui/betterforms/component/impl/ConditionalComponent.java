/*
   Copyright 2026 Huynh Tien

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
package me.hsgamer.bettergui.betterforms.component.impl;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.betterforms.builder.ComponentBuilder;
import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.component.FormResponseHandler;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class ConditionalComponent extends Component {
    private final Component component;
    private final Component fallbackComponent;

    private final RequirementApplier viewRequirementApplier;

    private final Set<UUID> whoCannotView = new ConcurrentSkipListSet<>();

    public ConditionalComponent(ComponentBuilder.Input input, Component component, Component fallbackComponent) {
        super(input);
        this.component = component;
        this.fallbackComponent = fallbackComponent;

        viewRequirementApplier = Optional.ofNullable(input.options.get("view-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new RequirementApplier(input.menu, input.name + "_view", m))
                .orElse(RequirementApplier.EMPTY);
    }

    public ConditionalComponent(ComponentBuilder.Input input) {
        this(
                input,
                MapUtils.castOptionalStringObjectMap(input.options.get("component"))
                        .flatMap(map -> ComponentBuilder.INSTANCE.build(new ComponentBuilder.Input(input.menu, input.name + "_component", map)))
                        .orElse(null),
                MapUtils.castOptionalStringObjectMap(input.options.get("fallback"))
                        .flatMap(map -> ComponentBuilder.INSTANCE.build(new ComponentBuilder.Input(input.menu, input.name + "_fallback", map)))
                        .orElse(null)
        );
    }

    @Override
    public Optional<FormResponseHandler> apply(UUID uuid, int index, FormBuilder<?, ?, ?> builder) {
        Requirement.Result result = viewRequirementApplier.getResult(uuid);

        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
        });
        SchedulerUtil.async().run(batchRunnable);

        if (result.isSuccess) {
            whoCannotView.remove(uuid);
            return component == null ? Optional.empty() : component.apply(uuid, index, builder);
        } else {
            whoCannotView.add(uuid);
            return fallbackComponent == null ? Optional.empty() : fallbackComponent.apply(uuid, index, builder);
        }
    }

    @Override
    public String getValue(UUID uuid, String args) {
        return whoCannotView.contains(uuid) ? fallbackComponent.getValue(uuid, args) : component.getValue(uuid, args);
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }
}
