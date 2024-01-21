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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public final class CollectionToCollectionConverter extends AbstractCollectionConverter<Collection<Object>> {

    public CollectionToCollectionConverter(ConversionBus conversionBus) {
        super(conversionBus);
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return true;
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Collection.class, Collection.class)
        );
    }

    @Override
    protected Type elementType(final Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length != 1) {
            return null;
        }

        return typeArgs[0];

    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Collection<Object> createNew(Class<?> type, Class<?> elementType, int length) {
        if (LinkedHashSet.class == type || HashSet.class == type
            || Set.class == type || Collection.class == type
        ) {
            return new LinkedHashSet<>(length);
        } else if (LinkedList.class == type) {
            return new LinkedList<>();
        } else if (TreeSet.class == type || NavigableSet.class == type
            || SortedSet.class == type
        ) {
            return new TreeSet<>();
        } else if (EnumSet.class.isAssignableFrom(type)) {
            if (!Enum.class.isAssignableFrom(elementType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + elementType.getName());
            }

            return EnumSet.noneOf(elementType.<Enum>asSubclass(Enum.class));
        }

        return new ArrayList<>();
    }

    @Override
    protected int size(Collection<Object> collection) {
        return collection.size();
    }

    @Override
    protected void forEachElement(Collection<Object> collection, Consumer<Object> consumer) {
        for (Object element : collection) {
            consumer.accept(element);
        }
    }

    @Override
    protected void add(int index, Collection<Object> collection, @Nullable Object element) {
        collection.add(element);
    }
}
