package me.hsgamer.bettergui.betterforms.component.impl;

import me.hsgamer.bettergui.betterforms.builder.ComponentBuilder;
import me.hsgamer.bettergui.betterforms.component.Component;
import me.hsgamer.bettergui.betterforms.component.FormResponseHandler;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListComponent extends Component {
    private final List<Component> components;
    private final Map<UUID, Integer> indexMap = new ConcurrentHashMap<>();

    public ListComponent(ComponentBuilder.Input input) {
        super(input);
        this.components = Optional.ofNullable(MapUtils.getIfFound(input.options, "child"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElseGet(Collections::emptyMap)
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    String key = entry.getKey();
                    return MapUtils.castOptionalStringObjectMap(entry.getValue())
                            .map(CaseInsensitiveStringMap::new)
                            .map(map -> new ComponentBuilder.Input(input.menu, input.name + "_child_" + key, map))
                            .flatMap(ComponentBuilder.INSTANCE::build)
                            .map(Stream::of)
                            .orElseGet(Stream::empty);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FormResponseHandler> apply(UUID uuid, int index, FormBuilder<?, ?, ?> builder) {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            Optional<FormResponseHandler> result = component.apply(uuid, index, builder);
            if (result.isPresent()) {
                indexMap.put(uuid, i);
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public String getValue(UUID uuid, String args) {
        Integer index = indexMap.get(uuid);
        if (index == null || index < 0 || index >= components.size()) {
            return "";
        }
        return components.get(index).getValue(uuid, args);
    }
}
