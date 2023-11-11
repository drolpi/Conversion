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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClassTreeUtil {

    private ClassTreeUtil() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull List<Class<?>> classTree(@NotNull final Class<?> type) {
        final List<Class<?>> tree = new ArrayList<>(20);
        final Set<Class<?>> visited = new HashSet<>(20);

        add(0, type, false, tree, visited);
        final boolean array = type.isArray();

        // Walk through super class tree
        for (int i = 0; i < tree.size(); i++) {
            Class<?> candidate = tree.get(i);
            candidate = (array ? candidate.componentType() : candidate);
            Class<?> superclass = candidate.getSuperclass();
            if (superclass != null && superclass != Object.class && superclass != Enum.class) {
                add(i + 1, candidate.getSuperclass(), array, tree, visited);
            }
            addInterfaces(candidate, array, tree, visited);
        }

        // Check whether the type is an enum or not
        if (Enum.class.isAssignableFrom(type)) {
            add(tree.size(), Enum.class, array, tree, visited);
            add(tree.size(), Enum.class, false, tree, visited);
            addInterfaces(Enum.class, array, tree, visited);
        }

        // Add object type
        add(tree.size(), Object.class, array, tree, visited);
        add(tree.size(), Object.class, false, tree, visited);
        return tree;
    }

    private static void addInterfaces(@NotNull final Class<?> type, final boolean asArray,
        @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
    ) {

        for (Class<?> implementedInterface : type.getInterfaces()) {
            add(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
        }
    }

    private static void add(int index, @NotNull Class<?> type, boolean asArray,
        @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
    ) {

        if (asArray) {
            type = type.arrayType();
        }
        if (visited.add(type)) {
            hierarchy.add(index, type);
        }
    }
}
