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

import de.drolpi.conversion.core.util.ClassCollectorUtil;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class ObjectFieldDiscoverer implements FieldDiscoverer<Map<Field, Object>> {

    static final ObjectFieldDiscoverer EMPTY_CONSTRUCTOR_INSTANCE = new ObjectFieldDiscoverer(new EmptyConstructorFactory(), false);

    private final Function<Type, Supplier<Object>> instanceFactory;
    private final boolean requiresInstanceCreation;

    ObjectFieldDiscoverer(Function<Type, Supplier<Object>> instanceFactory, boolean requiresInstanceCreation) {
        this.instanceFactory = instanceFactory;
        this.requiresInstanceCreation = requiresInstanceCreation;
    }

    @Override
    public boolean isSuitable(@NotNull Type type) {
        final Class<?> erasedTargetType = GenericTypeReflector.erase(type);
        return !erasedTargetType.isInterface() && !erasedTargetType.isRecord();
    }

    @Override
    public <U> void discoverFields(@NotNull Type type, List<FieldData<U, Map<Field, Object>>> fields) {
        final List<Class<?>> sourceTree = ClassCollectorUtil.classTree(type);

        for (final Class<?> collectClass : sourceTree) {
            for (final Field field : collectClass.getDeclaredFields()) {
                // Check if field is static or transient
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
                    continue;
                }

                field.setAccessible(true);
                // Store field data
                fields.add(new FieldData<>(field.getName(), field.getType(), (intermediate, value) -> intermediate.put(field, value), u -> {
                    try {
                        return field.get(u);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        }
    }

    @Override
    public DataApplier<Map<Field, Object>> createDataApplier(@NotNull Type type) {
        final Supplier<Object> maker = this.instanceFactory.apply(type);
        if (maker == null && this.requiresInstanceCreation) {
            throw new RuntimeException();
        }

        return new DataApplier<>() {
            @Override
            public Map<Field, Object> begin() {
                return new HashMap<>();
            }

            @Override
            public void complete(final Object instance, final Map<Field, Object> fieldData) {
                for (final Map.Entry<Field, Object> entry : fieldData.entrySet()) {
                    try {
                        entry.getKey().set(instance, entry.getValue());
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public Object complete(final Map<Field, Object> fieldData) {
                final Object instance = maker == null ? null : maker.get();
                if (instance == null) {
                    throw new RuntimeException("Unable to create instances for this type");
                }
                this.complete(instance, fieldData);
                return instance;
            }
        };
    }

    private static final class EmptyConstructorFactory implements Function<Type, Supplier<Object>> {

        @Override
        public Supplier<Object> apply(final Type type) {
            try {
                final Constructor<?> constructor = GenericTypeReflector.erase(type).getDeclaredConstructor();
                constructor.setAccessible(true);
                return () -> {
                    try {
                        return constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            } catch (final NoSuchMethodException ignored) {
                return null;
            }
        }
    }
}
