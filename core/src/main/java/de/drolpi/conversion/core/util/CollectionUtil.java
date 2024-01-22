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

package de.drolpi.conversion.core.util;

import io.leangen.geantyref.GenericTypeReflector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.Objects.requireNonNull;

public final class CollectionUtil {

    private CollectionUtil() {

    }

    public static Collection<Object> createCollection(final Type type, final Type elementType, final int length) {
        return createCollection(GenericTypeReflector.erase(type), elementType != null ? GenericTypeReflector.erase(elementType) : null, length);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection<Object> createCollection(final Class<?> type, final Class<?> elementType, final int length) {
        if (LinkedHashSet.class == type || HashSet.class == type || Set.class == type || Collection.class == type) {
            return new LinkedHashSet<>(length);
        } else if (LinkedList.class == type) {
            return new LinkedList<>();
        } else if (TreeSet.class == type || NavigableSet.class == type || SortedSet.class == type) {
            return new TreeSet<>();
        } else if (EnumSet.class.isAssignableFrom(type)) {
            requireNonNull(elementType, "elementType");
            if (!Enum.class.isAssignableFrom(elementType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + elementType.getName());
            }

            return EnumSet.noneOf(elementType.<Enum>asSubclass(Enum.class));
        }

        return new ArrayList<>();
    }

    public static Map<Object, Object> createMap(final Type mapType, final Type keyType, final int capacity) {
        return createMap(GenericTypeReflector.erase(mapType), keyType != null ? GenericTypeReflector.erase(keyType) : null, capacity);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<Object, Object> createMap(final Class<?> mapType, final Class<?> keyType, final int capacity) {
        if (EnumMap.class == mapType) {
            if (!Enum.class.isAssignableFrom(keyType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + keyType.getName());
            }

            return new EnumMap(keyType.asSubclass(Enum.class));
        }

        if (SortedMap.class == mapType || NavigableMap.class == mapType) {
            return new TreeMap<>();
        }

        return new LinkedHashMap<>(capacity);
    }
}
