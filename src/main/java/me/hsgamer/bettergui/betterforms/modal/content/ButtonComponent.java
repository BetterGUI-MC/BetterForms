package me.hsgamer.bettergui.betterforms.modal.content;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.betterforms.modal.ModalFormComponent;
import me.hsgamer.bettergui.betterforms.modal.ModalFormComponentBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ButtonComponent extends ModalFormComponent {
    private final String value;
    private final boolean first;
    private final ActionApplier actionApplier;
    private final RequirementApplier requirementApplier;

    public ButtonComponent(ModalFormComponentBuilder.Input input) {
        super(input);
        value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
        first = Optional.ofNullable(MapUtils.getIfFound(input.options, "first", "first-button"))
                .map(Object::toString)
                .map(Boolean::parseBoolean)
                .orElse(true);
        actionApplier = Optional.ofNullable(MapUtils.getIfFound(input.options, "action", "command"))
                .map(o -> new ActionApplier(input.menu, o))
                .orElseGet(() -> new ActionApplier(Collections.emptyList()));

        Map<String, Object> requirementMap = Optional.ofNullable(MapUtils.getIfFound(input.options, "requirement", "click-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElse(Collections.emptyMap());
        requirementApplier = new RequirementApplier(input.menu, input.name, requirementMap);
    }

    @Override
    public void apply(UUID uuid, ModalForm.Builder builder) {
        String replaced = StringReplacerApplier.replace(value, uuid, this);
        if (first) {
            builder.button1(replaced);
        } else {
            builder.button2(replaced);
        }
    }

    @Override
    public void handle(UUID uuid, ModalFormResponse response) {
        if (first != response.clickedFirst()) return;

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
}
