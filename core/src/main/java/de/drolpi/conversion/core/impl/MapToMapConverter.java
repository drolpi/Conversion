/*
 * Copyright 2023-2023 Lars Nippert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.drolpi.conversion.core.impl;

import de.drolpi.conversion.core.ConversionBus;
import de.drolpi.conversion.core.converter.ConversionPath;
import de.drolpi.conversion.core.converter.NonGenericConverter;
import de.drolpi.conversion.core.util.CollectionUtil;
import de.drolpi.conversion.core.util.ConversionUtil;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class MapToMapConverter implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public MapToMapConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@Nullable Type sourceType, @NotNull Type targetType) {
        final Type[] sourceElementTypes = ConversionUtil.elementTypes(sourceType, 2);
        final Type[] targetElementTypes = ConversionUtil.elementTypes(targetType, 2);

        if (sourceElementTypes == null || targetElementTypes == null) {
            return false;
        }

        return this.conversionBus.canConvert(sourceElementTypes[0], targetElementTypes[0])
            && this.conversionBus.canConvert(sourceElementTypes[1], targetElementTypes[1]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @Nullable Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Map<Object, Object> sourceMap = (Map<Object, Object>) source;
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        boolean copyRequired = !erasedTargetType.isInstance(sourceMap);

        if (!copyRequired && sourceMap.isEmpty()) {
            return sourceMap;
        }

        final Type[] targetParams = ConversionUtil.elementTypes(targetType, 2);
        final List<Map.Entry<Object, Object>> targetEntries = new ArrayList<>(sourceMap.size());

        if (targetParams == null) {
            for (final Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
                targetEntries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }
        } else {
            for (final Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
                final Object sourceKey = entry.getKey();
                final Object sourceValue = entry.getValue();

                final Object targetKey = this.conversionBus.convert(sourceKey, targetParams[1]);
                final Object targetValue = this.conversionBus.convert(targetKey, targetParams[1]);
                targetEntries.add(new AbstractMap.SimpleEntry<>(targetKey, targetValue));

                if (sourceKey != targetKey || sourceValue != targetValue) {
                    copyRequired = true;
                }
            }
        }

        if (!copyRequired) {
            return sourceMap;
        }

        final Map<Object, Object> targetMap = CollectionUtil.createMap(targetType, targetParams != null ? targetParams[0] : null, sourceMap.size());

        for (final Map.Entry<Object, Object> entry : targetEntries) {
            targetMap.put(entry.getKey(), entry.getValue());
        }

        return targetMap;
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Map.class, Map.class)
        );
    }
}
