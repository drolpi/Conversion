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

package de.drolpi.conversion.objectmapper.discoverer;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FieldDiscoverer<T> {

    static @NotNull FieldDiscoverer<?> create() {
        return ObjectFieldDiscoverer.EMPTY_CONSTRUCTOR_INSTANCE;
    }

    static @NotNull FieldDiscoverer<?> create(@NotNull Function<Type, Supplier<Object>> instanceFactory) {
        return new ObjectFieldDiscoverer(instanceFactory);
    }

    boolean isSuitable(@NotNull Type type);

    <U> void discoverFields(@NotNull Type type, List<FieldData<U, T>> fields);

    @NotNull DataApplier<T> createDataApplier(@NotNull Type type);

    interface DataApplier<T> {

        T begin();

        void complete(Object value, T intermediate);

        Object complete(T intermediate);

    }

    record FieldData<T, U>(String name, Type type, Deserializer<U> deserializer, Serializer<T> serializer) {

        public interface Deserializer<T> extends BiConsumer<T, Object> {

        }

        public interface Serializer<T> extends Function<T, Object> {

        }
    }

}
