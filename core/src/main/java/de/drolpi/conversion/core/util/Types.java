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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.leangen.geantyref.GenericTypeReflector.erase;

public final class Types {

    private Types() {
        throw new RuntimeException();
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<List<T>> list(Class<T> value) {
        return (Class<List<T>>) erase(TypeFactory.parameterizedClass(List.class, value));
    }

    @SuppressWarnings("unchecked")
    public static <T, U> Class<Map<T, U>> map(Class<T> key, Class<U> value) {
        return (Class<Map<T, U>>) erase(TypeFactory.parameterizedClass(Map.class, key, value));
    }
}
