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

import io.leangen.geantyref.TypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.leangen.geantyref.GenericTypeReflector.erase;
import static java.util.Objects.requireNonNull;

public final class Types {

    private Types() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull Class<List<T>> list(@NotNull final Class<T> value) {
        requireNonNull(value, "value");
        return (Class<List<T>>) erase(TypeFactory.parameterizedClass(List.class, value));
    }

    @SuppressWarnings("unchecked")
    public static <T, U> @NotNull Class<Map<T, U>> map(@NotNull final Class<T> key, Class<U> value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");
        return (Class<Map<T, U>>) erase(TypeFactory.parameterizedClass(Map.class, key, value));
    }
}
