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
import de.drolpi.conversion.core.converter.NonGenericConverter;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings("ClassCanBeRecord")
public final class MapToMapConverter implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public MapToMapConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        final Type[] sourceElementTypes = this.elementTypes(sourceType);
        final Type[] targetElementTypes = this.elementTypes(targetType);

        if (sourceElementTypes == null || targetElementTypes == null) {
            return false;
        }

        return true;
        //return this.conversionBus.canConvert(sourceElementTypes[0], targetElementTypes[0])
        //    && this.conversionBus.canConvert(sourceElementTypes[1], targetElementTypes[1]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Map<Object, Object> sourceMap = (Map<Object, Object>) source;
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        boolean copyRequired = !erasedTargetType.isInstance(sourceMap);

        if (!copyRequired && sourceMap.isEmpty()) {
            return sourceMap;
        }

        final Type[] targetParams = this.elementTypes(targetType);
        final List<Map.Entry<Object, Object>> targetEntries = new ArrayList<>(sourceMap.size());

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

        if (!copyRequired) {
            return sourceMap;
        }

        final Map<Object, Object> targetMap = this.createMap(erasedTargetType, GenericTypeReflector.erase(targetParams[0]), sourceMap.size());

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

    private @Nullable Type[] elementTypes(final Type type) {
        if (!(type instanceof final ParameterizedType parameterizedType)) {
            return null;
        }

        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length != 2) {
            return null;
        }

        return typeArgs;

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<Object, Object> createMap(final Class<?> mapType, final Class<?> keyType, final int capacity) {
        if (EnumMap.class == mapType) {
            if (!Enum.class.isAssignableFrom(keyType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + keyType.getName());
            }

            return new EnumMap(keyType.asSubclass(Enum.class));
        }

        if (SortedMap.class == mapType || NavigableMap.class == mapType) {
            return new TreeMap<>();
        }

        return new LinkedHashMap<>(capacity);
    }
}
