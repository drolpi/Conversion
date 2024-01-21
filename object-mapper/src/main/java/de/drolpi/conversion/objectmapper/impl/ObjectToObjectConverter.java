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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class ObjectToObjectConverter extends AbstractObjectMappingConverter implements NonGenericConverter {

    public ObjectToObjectConverter(ObjectMapper.Factory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        final ObjectMapper<Object> sourceMapper = (ObjectMapper<Object>) this.factory.get(sourceType);
        final ObjectMapper<Object> targetMapper = (ObjectMapper<Object>) this.factory.get(targetType);

        final Map<String, Object> map = sourceMapper.save(source);
        return targetMapper.load(map);
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return true;
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object.class, Object.class)
        );
    }
}
