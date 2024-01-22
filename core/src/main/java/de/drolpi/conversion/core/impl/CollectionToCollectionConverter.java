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
import de.drolpi.conversion.core.util.CollectionUtil;
import de.drolpi.conversion.core.util.ConversionUtil;
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
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Collection.class, Collection.class)
        );
    }

    @Override
    protected Type elementType(final Type type) {
        return ConversionUtil.elementType(type, 1);
    }

    @Override
    protected Collection<Object> createNew(Class<?> type, Class<?> elementType, int length) {
        return CollectionUtil.createCollection(type, elementType, length);
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
