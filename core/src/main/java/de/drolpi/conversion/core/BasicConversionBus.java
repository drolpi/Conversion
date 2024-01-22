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
import de.drolpi.conversion.core.converter.NonGenericConverter;
import de.drolpi.conversion.core.exception.ConverterNotFoundException;
import de.drolpi.conversion.core.util.ClassCollectorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

class BasicConversionBus implements ConfigurableConversionBus {

    private static final NoOpConverter NO_MATCH_CONVERTER = new NoOpConverter();
    private static final NoOpConverter NO_OP_CONVERTER = new NoOpConverter();

    private final ConverterRegistrar registrar;
    private final Map<CacheKey, NonGenericConverter> cache = new ConcurrentHashMap<>(64);

    BasicConversionBus() {
        this.registrar = new ConverterRegistrar();
    }

    public BasicConversionBus(ConverterRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public <U, V> void register(@NotNull final Class<? extends U> sourceType, final @NotNull Class<V> targetType,
        @NotNull final Converter<U, V> converter
    ) {
        // Register via adapter (GenericConverter)
        this.register(new ConverterAdapter(converter, sourceType, targetType));
    }

    @Override
    public void register(@NotNull final NonGenericConverter converter) {
        // Register in registrar
        this.registrar.add(converter);

        // Invalidate cache because maybe previously not possible conversions are possible now
        this.invalidateCache();
    }

    @Override
    public void unregister(@NotNull Class<?> source, @NotNull Class<?> target) {
        // Remove from registrar
        this.registrar.remove(source, target);

        // Invalidate cache because maybe a suitable converter is cached
        this.invalidateCache();
    }

    @Override
    public void unregister(@NotNull NonGenericConverter converter) {
        // Remove from registrar
        this.registrar.remove(converter);

        // Invalidate cache because maybe this converter is cached
        this.invalidateCache();
    }

    @Override
    public Object convert(@Nullable final Object source, @NotNull final Type targetType) {
        final Class<?> sourceType = source == null ? Object.class : source.getClass();
        final Converter<Object, Object> converter = this.converter(sourceType, targetType);

        if (converter == null) {
            // No Converter found
            throw new ConverterNotFoundException(sourceType, targetType);
        }

        //TODO:
        return converter.convert(source, sourceType, targetType);
    }

    private @Nullable NonGenericConverter converter(@NotNull final Type sourceType, @NotNull final Type targetType) {
        final CacheKey cacheKey = new CacheKey(sourceType, targetType);

        // Take a look at the cache to see if this conversion has been done before
        NonGenericConverter converter = this.cache.get(cacheKey);

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
    private static final class ConverterAdapter implements NonGenericConverter {

        private final Converter<Object, Object> converter;
        private final ConversionPath path;

        private ConverterAdapter(final Converter<?, ?> converter, final Class<?> sourceType, final Class<?> targetType) {
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
        public @Nullable Object convert(@Nullable final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return this.converter.convert(source, sourceType, targetType);
        }
    }

    protected static class ConverterRegistrar {

        protected final Map<NonGenericConverter.ConversionPath, Deque<NonGenericConverter>> converters = new ConcurrentHashMap<>();
        private final Set<NonGenericConverter> globalConverters = new CopyOnWriteArraySet<>();

        private void add(@NotNull final NonGenericConverter converter) {
            final Set<NonGenericConverter.ConversionPath> paths = converter.paths();

            if (paths.isEmpty()) {
                this.globalConverters.add(converter);
                return;
            }

            for (final NonGenericConverter.ConversionPath conversionPath : paths) {
                this.converters.computeIfAbsent(conversionPath, k -> new ConcurrentLinkedDeque<>()).add(converter);
            }
        }

        private void remove(@NotNull Class<?> source, @NotNull Class<?> target) {
            this.converters.remove(new NonGenericConverter.ConversionPath(source, target));
        }

        private void remove(@NotNull NonGenericConverter converter) {
            this.globalConverters.remove(converter);

            for (final NonGenericConverter.ConversionPath path : converter.paths()) {
                this.remove(path.sourceType(), path.targetType());
            }
        }

        protected NonGenericConverter find(@NotNull final Type sourceType, @NotNull final Type targetType) {
            // Search the full type tree
            final List<Class<?>> sourceTree = ClassCollectorUtil.classTree(sourceType);
            final List<Class<?>> targetTree = ClassCollectorUtil.classTree(targetType);

            for (final Class<?> targetCandidate : targetTree) {
                for (final Class<?> sourceCandidate : sourceTree) {
                    final NonGenericConverter.ConversionPath path = new NonGenericConverter.ConversionPath(sourceCandidate, targetCandidate);
                    final NonGenericConverter converter = this.converter(path);

                    if (converter != null) {
                        return converter;
                    }
                }
            }

            return null;
        }

        private NonGenericConverter converter(@NotNull final NonGenericConverter.ConversionPath path) {
            // Check specifically registered converters
            final Deque<NonGenericConverter> convertersForPath = this.converters.get(path);

            if (convertersForPath != null) {
                for (final NonGenericConverter converter : convertersForPath) {
                    if (!converter.isSuitable(path.sourceType(), path.targetType())) {
                        continue;
                    }
                    return converter;
                }
            }

            // Check ConditionalConverters for a dynamic match
            for (final NonGenericConverter converter : this.globalConverters) {
                if (converter.isSuitable(path.sourceType(), path.targetType())) {
                    return converter;
                }
            }

            return null;
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

    private static final class NoOpConverter implements NonGenericConverter {

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
        public Object convert(final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return source;
        }
    }
}
