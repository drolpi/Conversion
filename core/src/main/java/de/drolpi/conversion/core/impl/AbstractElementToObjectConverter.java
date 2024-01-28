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
import de.drolpi.conversion.core.util.ConversionUtil;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public abstract class AbstractElementToObjectConverter<T> implements NonGenericConverter {

    private final ConversionBus conversionBus;

    AbstractElementToObjectConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@Nullable Type sourceType, @NotNull Type targetType) {
        return ConversionUtil.canConvertElements(ConversionUtil.elementType(sourceType, 1), targetType, this.conversionBus);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object convert(@Nullable Object source, @Nullable Type sourceType, @NotNull Type targetType) {
        if (source == null || sourceType == null) {
            return null;
        }

        final Class<?> erasedSourceType = GenericTypeReflector.erase(sourceType);
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        if (erasedSourceType.isAssignableFrom(erasedTargetType)) {
            return source;
        }

        final T sourceCollection = (T) source;
        if (this.isEmpty(sourceCollection)) {
            return null;
        }

        final Object element = this.firstElement(sourceCollection);
        return this.conversionBus.convert(element, targetType);
    }

    protected abstract boolean isEmpty(T collection);

    protected abstract Object firstElement(T collection);
}
