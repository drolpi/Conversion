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
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class AbstractElementToElementConverter<T> implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public AbstractElementToElementConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        final Type sourceParams = this.elementType(sourceType);
        final Type targetParams = this.elementType(targetType);

        if (sourceParams == null || targetParams == null) {
            return false;
        }

        return this.conversionBus.canConvert(sourceParams, targetParams);
    }

    @SuppressWarnings("unchecked")
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final T sourceCollection = (T) source;
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);

        final AtomicBoolean copyRequired = new AtomicBoolean(!erasedTargetType.isInstance(sourceCollection));
        if (!copyRequired.get() && this.size(sourceCollection) == 0) {
            return sourceCollection;
        }

        final Type targetElementType = this.elementType(targetType);
        final T target = this.createNew(erasedTargetType, GenericTypeReflector.erase(targetElementType), this.size(sourceCollection));

        final AtomicInteger count = new AtomicInteger();
        this.forEachElement(sourceCollection, sourceElement -> {
            final Object targetElement = this.conversionBus.convert(sourceElement, targetElementType);
            this.add(count.getAndIncrement(), target, targetElement);
            if (sourceElement != targetElement) {
                copyRequired.set(true);
            }
        });

        return (copyRequired.get() ? target : sourceCollection);
    }

    protected abstract Type elementType(Type containerType);

    protected abstract T createNew(Class<?> type, Class<?> elementType, int length);

    protected abstract int size(T collection);

    protected abstract void forEachElement(T collection, Consumer<Object> consumer);

    protected abstract void add(int index, T collection, @Nullable Object element);
}
