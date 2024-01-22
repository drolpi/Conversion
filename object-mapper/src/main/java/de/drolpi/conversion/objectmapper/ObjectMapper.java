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

package de.drolpi.conversion.objectmapper;

import de.drolpi.conversion.core.ConversionBus;
import de.drolpi.conversion.objectmapper.discoverer.FieldDiscoverer;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public interface ObjectMapper<T> {

    static @NotNull Factory factory() {
        return ObjectMapperFactoryImpl.INSTANCE;
    }

    static @NotNull Factory.Builder factoryBuilder() {
        return new ObjectMapperFactoryImpl.BuilderImpl();
    }

    @NotNull T load(@NotNull Map<String, Object> source);

    void load(@NotNull T value, @NotNull Map<String, Object> source);

    @NotNull Map<String, Object> save(@NotNull T source);

    void save(@NotNull Map<String, Object> target, @NotNull T value);

    interface Factory {

        @NotNull ObjectMapper<?> get(@NotNull Type type);

        @SuppressWarnings("unchecked")
        default @NotNull <T> ObjectMapper<T> get(@NotNull Class<T> type) {
            requireNonNull(type, "type");
            return (ObjectMapper<T>) this.get((Type) type);
        }

        @SuppressWarnings("unchecked")
        default @NotNull <T> ObjectMapper<T> get(@NotNull TypeToken<T> type) {
            requireNonNull(type, "type");
            return (ObjectMapper<T>) this.get(type.getType());
        }

        interface Builder {

            @NotNull Builder addDiscoverer(@NotNull FieldDiscoverer<?> discoverer);

            @NotNull Builder conversionBus(@NotNull ConversionBus conversionBus);

            @NotNull ObjectMapper.Factory build();

        }
    }
}
