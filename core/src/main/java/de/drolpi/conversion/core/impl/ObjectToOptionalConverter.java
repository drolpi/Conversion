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

package de.drolpi.conversion.core.impl;

import de.drolpi.conversion.core.ConversionBus;
import de.drolpi.conversion.core.converter.NonGenericConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class ObjectToOptionalConverter implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public ObjectToOptionalConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        if (!(targetType instanceof final ParameterizedType parameterizedSourceType)) {
            return true;
        }

        return this.conversionBus.canConvert(sourceType, parameterizedSourceType.getActualTypeArguments()[0]);
    }

    @Override
    public @NotNull Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return Optional.empty();
        }

        if (source instanceof Optional) {
            return source;
        }

        if (targetType instanceof ParameterizedType parameterizedType && parameterizedType.getActualTypeArguments().length == 1) {
            Object target = this.conversionBus.convert(source, parameterizedType.getActualTypeArguments()[0]);
            if (target == null || (target.getClass().isArray() && Array.getLength(target) == 0) ||
                (target instanceof Collection<?> collection && collection.isEmpty())
            ) {
                return Optional.empty();
            }
            return Optional.of(target);
        }

        return Optional.of(source);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object.class, Optional.class)
        );
    }
}
