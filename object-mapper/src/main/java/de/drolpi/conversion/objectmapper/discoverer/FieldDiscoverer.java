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

import static java.util.Objects.requireNonNull;

public interface FieldDiscoverer<T> {

    static @NotNull RecordFieldDiscoverer record() {
        return RecordFieldDiscoverer.INSTANCE;
    }

    static @NotNull FieldDiscoverer<?> create() {
        return ObjectFieldDiscoverer.EMPTY_CONSTRUCTOR_INSTANCE;
    }

    static @NotNull FieldDiscoverer<?> create(@NotNull Function<Type, Supplier<Object>> instanceFactory) {
        requireNonNull(instanceFactory, "instanceFactory");
        return new ObjectFieldDiscoverer(instanceFactory);
    }

    boolean isSuitable(@NotNull Type type);

    <U> void discoverFields(@NotNull Type type, @NotNull List<FieldData<U, T>> fields);

    @NotNull InstanceFactory<T> createInstanceFactory(@NotNull Type type);

    interface InstanceFactory<T> {

        @NotNull T begin();

        @NotNull Object complete(@NotNull T intermediate);

        void complete(@NotNull Object value, @NotNull T intermediate);

    }

    record FieldData<T, U>(String name, Type type, Deserializer<U> deserializer, Serializer<T> serializer) {

        public interface Deserializer<T> extends BiConsumer<T, Object> {

        }

        public interface Serializer<T> extends Function<T, Object> {

        }
    }

}
