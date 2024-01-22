/*
 * Copyright 2023-2024 Lars Nippert
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

package de.drolpi.conversion.objectmapper.impl;

import de.drolpi.conversion.core.converter.NonGenericConverter;
import de.drolpi.conversion.objectmapper.ObjectMapper;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public final class MapToObjectConverter extends AbstractObjectMappingConverter implements NonGenericConverter {

    public MapToObjectConverter(ObjectMapper.Factory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        final Map<String, Object> sourceMap = (Map<String, Object>) source;
        final ObjectMapper<Object> objectMapper = (ObjectMapper<Object>) this.factory.get(targetType);

        return objectMapper.load(sourceMap);
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return this.isMapSuitable(sourceType);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Map.class, Object.class)
        );
    }
}
