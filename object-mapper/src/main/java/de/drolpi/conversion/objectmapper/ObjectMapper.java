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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

public interface ObjectMapper<T> {

    static @NotNull Factory factory() {
        return ObjectMapperFactoryImpl.INSTANCE;
    }

    static @NotNull Factory.Builder factoryBuilder() {
        return new ObjectMapperFactoryImpl.BuilderImpl();
    }

    T load(Map<String, Object> source);

    void load(T value, Map<String, Object> source);

    Map<String, Object> save(T source);

    void save(Map<String, Object> target, T value);

    interface Factory {

        @SuppressWarnings("unchecked")
        default @NotNull <T> ObjectMapper<T> get(@NotNull Class<T> type) {
            return (ObjectMapper<T>) this.get((Type) type);
        }

        @NotNull ObjectMapper<?> get(@NotNull Type type);

        interface Builder {

            @NotNull Builder addDiscoverer(@NotNull FieldDiscoverer<?> discoverer);

            @NotNull Builder conversionBus(@NotNull ConversionBus conversionBus);

            @NotNull ObjectMapper.Factory build();

        }
    }
}
