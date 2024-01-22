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
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ObjectMapperFactoryImpl implements ObjectMapper.Factory {

    static final ObjectMapper.Factory INSTANCE = ObjectMapper
        .factoryBuilder()
        .addDiscoverer(FieldDiscoverer.create())
        .addDiscoverer(FieldDiscoverer.record())
        .build();
    private static final int MAXIMUM_MAPPERS_SIZE = 64;

    private final Map<Type, ObjectMapper<?>> mappers = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Type, ObjectMapper<?>> eldest) {
            return this.size() > MAXIMUM_MAPPERS_SIZE;
        }
    };
    private final List<FieldDiscoverer<?>> fieldDiscoverers;
    private final ConversionBus conversionBus;

    ObjectMapperFactoryImpl(final BuilderImpl builder) {
        this.fieldDiscoverers = new ArrayList<>(builder.discoverer);
        this.conversionBus = builder.conversionBus;
    }

    @Override
    public @NotNull ObjectMapper<?> get(@NotNull final Type type) {
        // Check if type is missing type parameters
        if (GenericTypeReflector.isMissingTypeParameters(type)) {
            throw new RuntimeException("Raw types are not supported!");
        }

        synchronized (this.mappers) {
            // Check if we already have cached a suitable object mapper
            ObjectMapper<?> objectMapper = this.mappers.get(type);

            if (objectMapper != null) {
                return objectMapper;
            }

            // Iterate through all field discoverers and if we can create an object mapper
            for (final FieldDiscoverer<?> discoverer : this.fieldDiscoverers) {
                if (!discoverer.isSuitable(type)) {
                    continue;
                }

                // Create an object mapper
                objectMapper = this.createMapper(type, discoverer);
                this.mappers.put(type, objectMapper);
                return objectMapper;
            }

            //TODO: Replace with own exception
            throw new RuntimeException();
        }
    }

    private <T, U> ObjectMapper<T> createMapper(final Type type, final FieldDiscoverer<U> discoverer) {
        final FieldDiscoverer.InstanceFactory<U> instanceFactory = discoverer.createInstanceFactory(type);
        final List<FieldDiscoverer.FieldData<T, U>> fields = new ArrayList<>();
        discoverer.discoverFields(type, fields);

        return new ObjectMapperImpl<>(fields, instanceFactory, this.conversionBus);
    }

    static final class BuilderImpl implements ObjectMapper.Factory.Builder {

        private final List<FieldDiscoverer<?>> discoverer = new ArrayList<>();
        private ConversionBus conversionBus = ConversionBus.createDefault();

        @Override
        public @NotNull Builder addDiscoverer(@NotNull final FieldDiscoverer<?> discoverer) {
            this.discoverer.add(discoverer);
            return this;
        }

        @Override
        public @NotNull Builder conversionBus(@NotNull final ConversionBus conversionBus) {
            this.conversionBus = conversionBus;
            return this;
        }

        @Override
        public ObjectMapper.@NotNull Factory build() {
            return new ObjectMapperFactoryImpl(this);
        }
    }
}