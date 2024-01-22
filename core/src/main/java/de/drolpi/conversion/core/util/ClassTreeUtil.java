/*
 * Copyright 2023-2023 Lars Nippert
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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class ClassTreeUtil {

    private ClassTreeUtil() {
        throw new RuntimeException();
    }

    public static @NotNull List<Class<?>> collect(@NotNull final Type type) {
        requireNonNull(type, "type");
        final Class<?> erasedType = GenericTypeReflector.erase(GenericTypeReflector.box(type));
        final List<Class<?>> tree = new ArrayList<>(20);
        final Set<Class<?>> visited = new HashSet<>(20);

        collectClass(0, erasedType, false, tree, visited);
        final boolean array = erasedType.isArray();

        // Walk through super class tree
        for (int i = 0; i < tree.size(); i++) {
            Class<?> candidate = tree.get(i);
            candidate = (array ? candidate.componentType() : candidate);

            // Collect
            final Class<?> superclass = candidate.getSuperclass();
            if (superclass != null && superclass != Object.class && superclass != Enum.class) {
                collectClass(i + 1, candidate.getSuperclass(), array, tree, visited);
            }
            collectInterfaces(candidate, array, tree, visited);
        }

        // Check whether the type is an enum or not
        if (Enum.class.isAssignableFrom(erasedType)) {
            collectClass(tree.size(), Enum.class, array, tree, visited);
            collectClass(tree.size(), Enum.class, false, tree, visited);
            collectInterfaces(Enum.class, array, tree, visited);
        }

        // Add object type
        collectClass(tree.size(), Object.class, array, tree, visited);
        collectClass(tree.size(), Object.class, false, tree, visited);
        return tree;
    }

    private static void collectInterfaces(@NotNull final Class<?> type, final boolean asArray,
        @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
    ) {
        requireNonNull(type, "type");
        requireNonNull(hierarchy, "hierarchy");
        requireNonNull(visited, "visited");
        for (final Class<?> implementedInterface : type.getInterfaces()) {
            collectClass(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
        }
    }

    private static void collectClass(final int index, @NotNull Class<?> type, final boolean asArray,
        @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
    ) {
        requireNonNull(type, "type");
        requireNonNull(hierarchy, "hierarchy");
        requireNonNull(visited, "visited");
        if (asArray) {
            type = type.arrayType();
        }
        if (visited.add(type)) {
            hierarchy.add(index, type);
        }
    }
}
