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
import de.drolpi.conversion.core.converter.ConversionPath;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Consumer;

public final class ArrayToArrayConverter extends AbstractElementToElementConverter<Object> {

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
    protected Type elementType(Type containerType) {
        return GenericTypeReflector.getArrayComponentType(containerType);
    }

    @Override
    protected Object createNew(Class<?> type, Class<?> elementType, int length) {
        return Array.newInstance(GenericTypeReflector.erase(elementType), length);
    }

    @Override
    protected int size(Object collection) {
        return Array.getLength(collection);
    }

    @Override
    protected void forEachElement(Object collection, Consumer<Object> consumer) {
        for (int i = 0; i < Array.getLength(collection); i++) {
            consumer.accept(Array.get(collection, i));
        }
    }

    @Override
    protected void add(int index, Object collection, @Nullable Object element) {
        Array.set(collection, index, element);
    }
}
