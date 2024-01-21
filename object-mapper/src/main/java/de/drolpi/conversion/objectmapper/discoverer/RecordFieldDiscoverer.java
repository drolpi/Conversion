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

import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.List;

public class RecordFieldDiscoverer implements FieldDiscoverer<Object[]> {

    @Override
    public boolean isSuitable(@NotNull Type type) {
        final Class<?> erasedType = GenericTypeReflector.erase(type);
        return erasedType.isRecord();
    }

    @Override
    public <U> void discoverFields(@NotNull Type type, List<FieldData<U, Object[]>> fields) {
        final Class<?> clazz = GenericTypeReflector.erase(type);
        final RecordComponent[] recordComponents = clazz.getRecordComponents();

        for (int i = 0, recordComponentsLength = recordComponents.length; i < recordComponentsLength; i++) {
            final RecordComponent component = recordComponents[i];
            final Method accessor = component.getAccessor();
            accessor.setAccessible(true);

            final String name = component.getName();
            final Type genericType = component.getType();

            final Type resolvedType = GenericTypeReflector.resolveExactType(genericType, type);
            final int targetIdx = i;
            fields.add(new FieldData<>(name, GenericTypeReflector.erase(resolvedType), (intermediate, value) -> intermediate[targetIdx] = value,
                value -> {
                    try {
                        return accessor.invoke(value);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }));
        }
    }

    @Override
    public @NotNull DataApplier<Object[]> createDataApplier(@NotNull Type type) {
        final Class<?> clazz = GenericTypeReflector.erase(type);
        final RecordComponent[] recordComponents = clazz.getRecordComponents();
        final Class<?>[] constructorParams = new Class<?>[recordComponents.length];

        for (int i = 0, recordComponentsLength = recordComponents.length; i < recordComponentsLength; i++) {
            final RecordComponent component = recordComponents[i];
            final Type genericType = component.getType();
            constructorParams[i] = GenericTypeReflector.erase(genericType);
        }

        // canonical constructor, which we'll use to make new instances
        final Constructor<?> clazzConstructor;
        try {
            clazzConstructor = clazz.getDeclaredConstructor(constructorParams);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        clazzConstructor.setAccessible(true);

        return new DataApplier<>() {
            @Override
            public Object[] begin() {
                return new Object[recordComponents.length];
            }

            @Override
            public void complete(Object value, Object[] intermediate) {

            }

            @Override
            public Object complete(Object[] intermediate) {
                try {
                    return clazzConstructor.newInstance(intermediate);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
