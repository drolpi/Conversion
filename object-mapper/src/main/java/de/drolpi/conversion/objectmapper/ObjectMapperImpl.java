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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"ClassCanBeRecord", "unchecked"})
final class ObjectMapperImpl<T, U> implements ObjectMapper<T> {

    //TODO: Add methods to interface

    private final List<FieldDiscoverer.FieldData<T, U>> fieldData;
    private final FieldDiscoverer.DataApplier<U> dataApplier;
    private final ConversionBus conversionBus;

    ObjectMapperImpl(List<FieldDiscoverer.FieldData<T, U>> fieldData, FieldDiscoverer.DataApplier<U> dataApplier, ConversionBus conversionBus) {
        this.fieldData = Collections.unmodifiableList(fieldData);
        this.dataApplier = dataApplier;
        this.conversionBus = conversionBus;
    }

    @Override
    public T load(Map<String, Object> source) {
        return this.load(source, intermediate -> (T) this.dataApplier.complete(intermediate));
    }

    public void load(T value, Map<String, Object> source) {
        this.load(source, intermediate -> {
            this.dataApplier.complete(value, intermediate);
            return value;
        });
    }

    private T load(Map<String, Object> source, Function<U, T> completer) {
        final U fieldData = this.dataApplier.begin();

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
    public Map<String, Object> save(T value) {
        final Map<String, Object> target = new HashMap<>();

        this.save(target, value);
        return target;
    }

    public void save(Map<String, Object> target, T value) {
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