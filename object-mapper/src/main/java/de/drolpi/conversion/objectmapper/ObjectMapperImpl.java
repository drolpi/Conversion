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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unchecked", "ClassCanBeRecord"})
final class ObjectMapperImpl<T, U> implements ObjectMapper<T> {

    private final List<FieldDiscoverer.FieldData<T, U>> fieldData;
    private final FieldDiscoverer.InstanceFactory<U> instanceFactory;
    private final ConversionBus conversionBus;

    ObjectMapperImpl(final List<FieldDiscoverer.FieldData<T, U>> fieldData, final FieldDiscoverer.InstanceFactory<U> instanceFactory,
        final ConversionBus conversionBus
    ) {
        this.fieldData = Collections.unmodifiableList(fieldData);
        this.instanceFactory = instanceFactory;
        this.conversionBus = conversionBus;
    }

    @Override
    public @NotNull T load(@NotNull Map<String, Object> source) {
        requireNonNull(source, "source");
        return this.load(source, intermediate -> (T) this.instanceFactory.complete(intermediate));
    }

    @Override
    public void load(@NotNull T value, @NotNull Map<String, Object> source) {
        requireNonNull(value, "value");
        requireNonNull(source, "source");
        this.load(source, intermediate -> {
            this.instanceFactory.complete(value, intermediate);
            return value;
        });
    }

    private T load(Map<String, Object> source, Function<U, T> completer) {
        final U fieldData = this.instanceFactory.begin();

        // Iterate through all fields
        for (final FieldDiscoverer.FieldData<T, U> field : this.fieldData) {
            // Get value from map
            final Object mapValue = source.get(field.name());

            if (mapValue == null) {
                continue;
            }

            final Type fieldType = field.type();
            final Class<?> erasedBoxedType = GenericTypeReflector.erase(GenericTypeReflector.box(fieldType));
            // Convert value to field type
            final Object fieldValue = this.conversionBus.convert(mapValue, fieldType);

            if (!erasedBoxedType.isInstance(fieldValue)) {
                throw new RuntimeException("Object " + fieldValue + " is not of expected type " + fieldType);
            }
            // Set converted value
            field.deserializer().accept(fieldData, fieldValue);
        }

        return completer.apply(fieldData);
    }

    @Override
    public @NotNull Map<String, Object> save(@NotNull T value) {
        requireNonNull(value, "value");
        final Map<String, Object> target = new HashMap<>();

        this.save(target, value);
        return target;
    }

    @Override
    public void save(@NotNull Map<String, Object> target, @NotNull T value) {
        requireNonNull(target, "target");
        requireNonNull(value, "value");
        // Iterate through all fields
        for (final FieldDiscoverer.FieldData<T, U> fieldData : this.fieldData) {
            // Get value from field
            final Object fieldValue = fieldData.serializer().apply(value);

            if (fieldValue == null) {
                target.put(fieldData.name(), null);
                continue;
            }

            //TODO:
            //final Object mapValue = this.conversionBus.convertToObject(fieldValue);

            // Store value in map
            target.put(fieldData.name(), fieldValue);
        }
    }
}