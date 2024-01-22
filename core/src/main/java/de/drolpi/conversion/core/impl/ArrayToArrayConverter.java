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
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Consumer;

public final class ArrayToArrayConverter extends AbstractCollectionConverter<Object[]> {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public ArrayToArrayConverter(ConversionBus conversionBus) {
        super(conversionBus);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object[].class, Object[].class)
        );
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        return super.convert(toObjectArray(source), sourceType, targetType);
    }

    @Override
    protected Type elementType(Type containerType) {
        return GenericTypeReflector.getArrayComponentType(containerType);
    }

    @Override
    protected Object[] createNew(Class<?> type, Class<?> elementType, int length) {
        return (Object[]) Array.newInstance(GenericTypeReflector.erase(elementType), length);
    }

    @Override
    protected int size(Object[] collection) {
        return collection.length;
    }

    @Override
    protected void forEachElement(Object[] collection, Consumer<Object> consumer) {
        for (final Object element : collection) {
            consumer.accept(element);
        }
    }

    @Override
    protected void add(int index, Object[] collection, @Nullable Object element) {
        collection[index] = element;
    }

    private Object[] toObjectArray(@Nullable Object source) {
        if (source instanceof Object[] objects) {
            return objects;
        }
        if (source == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }
}
