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

import de.drolpi.conversion.core.util.ClassTreeUtil;
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

import static java.util.Objects.requireNonNull;

@SuppressWarnings("ClassCanBeRecord")
public final class ObjectFieldDiscoverer implements FieldDiscoverer<Map<Field, Object>> {

    static final ObjectFieldDiscoverer EMPTY_CONSTRUCTOR_INSTANCE = new ObjectFieldDiscoverer(new EmptyConstructorFactory());

    private final Function<Type, Supplier<Object>> instanceFactory;

    ObjectFieldDiscoverer(@NotNull final Function<Type, Supplier<Object>> instanceFactory) {
        requireNonNull(instanceFactory, "instanceFactory");
        this.instanceFactory = instanceFactory;
    }

    @Override
    public boolean isSuitable(@NotNull final Type type) {
        requireNonNull(type, "type");
        final Class<?> erasedTargetType = GenericTypeReflector.erase(type);
        return !erasedTargetType.isInterface() && !erasedTargetType.isRecord();
    }

    @Override
    public <U> void discoverFields(@NotNull final Type type, @NotNull final List<FieldData<U, Map<Field, Object>>> fields) {
        requireNonNull(type, "type");
        requireNonNull(fields, "fields");
        final List<Class<?>> sourceTree = ClassTreeUtil.collect(type);

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
    public @NotNull InstanceFactory<Map<Field, Object>> createInstanceFactory(@NotNull final Type type) {
        requireNonNull(type, "type");
        final Supplier<Object> maker = this.instanceFactory.apply(type);

        return new InstanceFactory<>() {
            @Override
            public @NotNull Map<Field, Object> begin() {
                return new HashMap<>();
            }

            @Override
            public void complete(@NotNull final Object instance, @NotNull final Map<Field, Object> fieldData) {
                requireNonNull(instance, "instance");
                requireNonNull(fieldData, "fieldData");
                for (final Map.Entry<Field, Object> entry : fieldData.entrySet()) {
                    try {
                        entry.getKey().set(instance, entry.getValue());
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public @NotNull Object complete(@NotNull final Map<Field, Object> fieldData) {
                requireNonNull(fieldData, "fieldData");
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
        public Supplier<Object> apply(@NotNull final Type type) {
            requireNonNull(type, "type");
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
