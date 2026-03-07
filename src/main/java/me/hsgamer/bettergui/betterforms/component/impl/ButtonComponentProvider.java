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
package me.hsgamer.bettergui.betterforms.component.impl;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.betterforms.builder.ComponentProviderBuilder;
import me.hsgamer.bettergui.betterforms.component.BaseComponentProvider;
import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.menu.FormMenu;
import me.hsgamer.bettergui.betterforms.util.ComponentUtil;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class ButtonComponentProvider extends BaseComponentProvider {
    private final FormMenu.FormType formType;
    private final ActionApplier actionApplier;
    private final RequirementApplier clickRequirementApplier;
    private final String value;
    private final Function<UUID, FormImage> imageFunction;

    public ButtonComponentProvider(ComponentProviderBuilder.Input input) {
        super(input);
        this.formType = input.menu.getFormType();
        this.actionApplier = Optional.ofNullable(MapUtils.getIfFound(input.options, "action", "command"))
                .map(o -> new ActionApplier(input.menu, o))
                .orElse(ActionApplier.EMPTY);
        this.clickRequirementApplier = Optional.ofNullable(input.options.get("click-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(m -> new RequirementApplier(input.menu, input.name + "_click", m))
                .orElse(RequirementApplier.EMPTY);
        this.value = Optional.ofNullable(MapUtils.getIfFound(input.options, "value", "text", "content"))
                .map(Object::toString)
                .orElse("");
        this.imageFunction = ComponentUtil.createImageFunction(input.options, this);
    }

    @Override
    protected List<Component> provideChecked(UUID uuid, int index) {
        switch (formType) {
            case MODAL:
                boolean first = index == 0;
                return Collections.singletonList(new Component() {
                    @Override
                    public void apply(FormBuilder<?, ?, ?> builder) {
                        if (builder instanceof ModalForm.Builder) {
                            String replaced = StringReplacerApplier.replace(value, uuid, ButtonComponentProvider.this);
                            if (first) {
                                ((ModalForm.Builder) builder).button1(replaced);
                            } else {
                                ((ModalForm.Builder) builder).button2(replaced);
                            }
                        }
                    }

                    @Override
                    public void handle(Form form, FormResponse response) {
                        if (response instanceof ModalFormResponse) {
                            if (first == ((ModalFormResponse) response).clickedFirst()) {
                                handleClick(uuid);
                            }
                        }
                    }
                });
            case SIMPLE:
                return Collections.singletonList(new Component() {
                    @Override
                    public void apply(FormBuilder<?, ?, ?> builder) {
                        if (builder instanceof SimpleForm.Builder) {
                            String replaced = StringReplacerApplier.replace(value, uuid, ButtonComponentProvider.this);
                            FormImage image = imageFunction.apply(uuid);
                            ((SimpleForm.Builder) builder).button(replaced, image);
                        }
                    }

                    @Override
                    public void handle(Form form, FormResponse response) {
                        if (response instanceof SimpleFormResponse) {
                            if (((SimpleFormResponse) response).clickedButtonId() == index) {
                                handleClick(uuid);
                            }
                        }
                    }
                });
            case CUSTOM:
                return Collections.singletonList(new Component() {
                    @Override
                    public void apply(FormBuilder<?, ?, ?> builder) {
                        // EMPTY
                    }

                    @Override
                    public void handle(Form form, FormResponse response) {
                        handleClick(uuid);
                    }
                });
            default:
                return Collections.emptyList();
        }
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
