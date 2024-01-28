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

public abstract class AbstractObjectToElementConverter<T> implements NonGenericConverter {

    private final ConversionBus conversionBus;

    AbstractObjectToElementConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@Nullable Type sourceType, @NotNull Type targetType) {
        return ConversionUtil.canConvertElements(sourceType, ConversionUtil.elementType(targetType, 1), this.conversionBus);
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, @Nullable Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        final Type targetElementType = ConversionUtil.elementType(targetType, 1);

        final T target = this.createNew(erasedTargetType, GenericTypeReflector.erase(targetElementType));
        final Object targetElement = this.conversionBus.convert(source, targetElementType);
        this.add(0, target, targetElement);
        return target;
    }

    protected abstract T createNew(Class<?> type, Class<?> elementType);

    protected abstract void add(int index, T collection, @Nullable Object element);
}
