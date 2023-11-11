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

package de.drolpi.conversion.core;

import de.drolpi.conversion.core.converter.ConditionalConverter;
import de.drolpi.conversion.core.converter.Converter;
import de.drolpi.conversion.core.converter.GenericConverter;
import de.drolpi.conversion.core.exception.ConverterNotFoundException;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

class BasicConversionBus implements ConfigurableConversionBus {

    private static final NoOpConverter NO_MATCH_CONVERTER = new NoOpConverter();
    private static final NoOpConverter NO_OP_CONVERTER = new NoOpConverter();

    private final ConverterRegistrar registrar = new ConverterRegistrar();
    private final Map<CacheKey, GenericConverter> cache = new ConcurrentHashMap<>(64);

    BasicConversionBus() {

    }

    @Override
    public <U, V> void register(@NotNull final Class<? extends U> sourceType, final @NotNull Class<V> targetType,
        @NotNull final Converter<? extends U, ? extends V> converter
    ) {
        // Register via adapter (GenericConverter)
        this.register(new ConverterAdapter(converter, sourceType, targetType));
    }

    @Override
    public void register(@NotNull final GenericConverter converter) {
        // Register in storage
        this.registrar.add(converter);

        // Invalidate cache because maybe previously not possible conversions are possible now
        this.invalidateCache();
    }

    @Override
    public @NotNull Object convert(@NotNull final Object source, @NotNull final Type targetType) {
        final Converter<Object, Object> converter = this.converter(source.getClass(), targetType);

        if (converter == null) {
            // No Converter found
            throw new ConverterNotFoundException(source.getClass(), targetType);
        }

        return converter.convert(source, source.getClass(), targetType);
    }

    private @Nullable GenericConverter converter(@NotNull final Type sourceType, @NotNull final Type targetType) {
        final CacheKey cacheKey = new CacheKey(sourceType, targetType);

        // Take a look at the cache to see if this conversion has been done before
        GenericConverter converter = this.cache.get(cacheKey);

        if (converter != null) {
            // Check whether a converter was found in the previous conversion or not
            return converter != NO_MATCH_CONVERTER ? converter : null;
        }

        // Try to get converter from registrar
        converter = this.registrar.find(sourceType, targetType);

        // Check whether a conversion is necessary at all
        if (converter == null && sourceType == targetType) {
            // Use a non-operating converter
            converter = NO_OP_CONVERTER;
        }

        // Check whether the converter is suitable or not
        if (converter != null) {
            // Cache the suitable converter
            this.cache.put(cacheKey, converter);
            return converter;
        }

        // Cache that no suitable converter was found
        this.cache.put(cacheKey, NO_MATCH_CONVERTER);
        return null;
    }

    private void invalidateCache() {
        this.cache.clear();
    }

    @SuppressWarnings("unchecked")
    private static final class ConverterAdapter implements GenericConverter {

        private final Converter<Object, Object> converter;
        private final ConversionPath path;

        public ConverterAdapter(final Converter<?, ?> converter, final Class<?> sourceType, final Class<?> targetType) {
            this.converter = (Converter<Object, Object>) converter;
            this.path = new ConversionPath(sourceType, targetType);
        }

        @Override
        public @NotNull Set<ConversionPath> paths() {
            return Collections.singleton(this.path);
        }

        @Override
        public boolean isSuitable(@NotNull final Type sourceType, @NotNull final Type targetType) {
            if (this.path.targetType() != targetType) {
                return false;
            }

            return !(this.converter instanceof ConditionalConverter conditionalConverter) ||
                conditionalConverter.isSuitable(sourceType, targetType);
        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return this.converter.convert(source, sourceType, targetType);
        }
    }

    private static final class ConverterRegistrar {

        private final Set<GenericConverter> globalConverters = new CopyOnWriteArraySet<>();
        private final Map<GenericConverter.ConversionPath, Deque<GenericConverter>> converters = new ConcurrentHashMap<>();

        public void add(@NotNull final GenericConverter converter) {
            final Set<GenericConverter.ConversionPath> paths = converter.paths();

            if (paths.isEmpty()) {
                this.globalConverters.add(converter);
                return;
            }

            for (final GenericConverter.ConversionPath conversionPath : paths) {
                this.converters.computeIfAbsent(conversionPath, k -> new ConcurrentLinkedDeque<>()).add(converter);
            }
        }

        public GenericConverter find(@NotNull final Type sourceType, @NotNull final Type targetType) {
            // Search the full type tree
            final List<Class<?>> sourceTree = this.collectClassTree(sourceType);
            final List<Class<?>> targetTree = this.collectClassTree(targetType);

            for (final Class<?> targetCandidate : targetTree) {
                for (final Class<?> sourceCandidate : sourceTree) {
                    final GenericConverter.ConversionPath path = new GenericConverter.ConversionPath(sourceCandidate, targetCandidate);
                    final GenericConverter converter = this.converter(path);

                    if (converter != null) {
                        return converter;
                    }
                }
            }
            return null;
        }

        private GenericConverter converter(@NotNull final GenericConverter.ConversionPath path) {
            // Check specifically registered converters
            final Deque<GenericConverter> convertersForPath = this.converters.get(path);

            if (convertersForPath != null) {
                for (final GenericConverter converter : convertersForPath) {
                    if (!converter.isSuitable(path.sourceType(), path.targetType())) {
                        continue;
                    }
                    return converter;
                }
            }

            // Check ConditionalConverters for a dynamic match
            for (final GenericConverter converter : this.globalConverters) {
                if (converter.isSuitable(path.sourceType(), path.targetType())) {
                    return converter;
                }
            }

            return null;
        }

        private @NotNull List<Class<?>> collectClassTree(@NotNull final Type type) {
            final Class<?> erasedType = GenericTypeReflector.erase(GenericTypeReflector.box(type));
            final List<Class<?>> tree = new ArrayList<>(20);
            final Set<Class<?>> visited = new HashSet<>(20);

            this.collectClass(0, erasedType, false, tree, visited);
            final boolean array = erasedType.isArray();

            // Walk through super class tree
            for (int i = 0; i < tree.size(); i++) {
                Class<?> candidate = tree.get(i);
                candidate = (array ? candidate.componentType() : candidate);

                // Collect
                final Class<?> superclass = candidate.getSuperclass();
                if (superclass != null && superclass != Object.class && superclass != Enum.class) {
                    this.collectClass(i + 1, candidate.getSuperclass(), array, tree, visited);
                }
                this.collectInterfaces(candidate, array, tree, visited);
            }

            // Check whether the type is an enum or not
            if (Enum.class.isAssignableFrom(erasedType)) {
                this.collectClass(tree.size(), Enum.class, array, tree, visited);
                this.collectClass(tree.size(), Enum.class, false, tree, visited);
                this.collectInterfaces(Enum.class, array, tree, visited);
            }

            // Add object type
            this.collectClass(tree.size(), Object.class, array, tree, visited);
            this.collectClass(tree.size(), Object.class, false, tree, visited);
            return tree;
        }

        private void collectInterfaces(@NotNull final Class<?> type, final boolean asArray,
            @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
        ) {
            for (final Class<?> implementedInterface : type.getInterfaces()) {
                this.collectClass(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
            }
        }

        private void collectClass(int index, @NotNull Class<?> type, boolean asArray,
            @NotNull final List<Class<?>> hierarchy, @NotNull final Set<Class<?>> visited
        ) {
            if (asArray) {
                type = type.arrayType();
            }
            if (visited.add(type)) {
                hierarchy.add(index, type);
            }
        }
    }

    record CacheKey(Type sourceType, Type targetType) {

        @Override
        public boolean equals(@Nullable final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheKey otherCacheKey)) {
                return false;
            }
            return (this.sourceType.equals(otherCacheKey.sourceType)) && this.targetType.equals(otherCacheKey.targetType);
        }
    }

    private static final class NoOpConverter implements GenericConverter {

        private NoOpConverter() {

        }

        @Override
        public boolean isSuitable(@NotNull final Type sourceType, @NotNull final Type targetType) {
            return true;
        }

        @Override
        public @NotNull Set<ConversionPath> paths() {
            return Collections.emptySet();
        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return source;
        }
    }
}
