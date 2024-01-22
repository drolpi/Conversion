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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class OptionalToObjectConverter implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public OptionalToObjectConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        Type source = Object.class;

        if (sourceType instanceof final ParameterizedType parameterizedType) {
            source = parameterizedType.getActualTypeArguments()[0];
        }

        return this.conversionBus.canConvert(source, targetType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Optional<Object> optional = (Optional<Object>) source;

        if (optional.isEmpty()) {
            return null;
        }

        return this.conversionBus.convert(optional.get(), targetType);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Optional.class, Object.class)
        );
    }
}
