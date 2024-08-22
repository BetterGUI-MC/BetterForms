package me.hsgamer.bettergui.betterforms.common;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.betterforms.api.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.api.component.Component;
import me.hsgamer.bettergui.betterforms.api.component.ComponentProvider;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseComponentProvider<F extends Form, R extends FormResponse, B extends FormBuilder<B, F, R>> implements ComponentProvider<F, R, B> {
    protected final ComponentProviderBuilder.Input input;
    private final RequirementApplier viewRequirementApplier;

    protected BaseComponentProvider(ComponentProviderBuilder.Input input) {
        this.input = input;

        viewRequirementApplier = Optional.ofNullable(input.options.get("view-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new RequirementApplier(input.menu, input.name + "_view", m))
                .orElse(RequirementApplier.EMPTY);
    }

    protected abstract List<Component<F, R, B>> provideChecked(UUID uuid, int index);

    @Override
    public List<Component<F, R, B>> provide(UUID uuid, int index) {
        Requirement.Result result = viewRequirementApplier.getResult(uuid);

        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
        });
        SchedulerUtil.async().run(batchRunnable);

        if (result.isSuccess) {
            return provideChecked(uuid, index);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }
}
