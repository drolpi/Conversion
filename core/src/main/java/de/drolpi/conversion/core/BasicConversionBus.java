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

import de.drolpi.conversion.core.converter.Converter;
import de.drolpi.conversion.core.converter.GenericConverter;
import de.drolpi.conversion.core.exception.ConverterNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class BasicConversionBus implements ConfigurableConversionBus {

    private static final NoOpConverter NO_MATCH_CONVERTER = new NoOpConverter();
    private static final NoOpConverter NO_OP_CONVERTER = new NoOpConverter();

    private final ConverterStorage storage = new ConverterStorage();
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
        this.storage.register(converter);

        // Invalidate cache because maybe previously not possible conversions are possible now
        this.invalidateCache();
    }

    @Override
    public @NotNull Object convert(@NotNull final Object source, @NotNull final Type targetType) {
        throw new ConverterNotFoundException(source.getClass(), targetType);
    }

    private @Nullable GenericConverter converter(@NotNull final Type sourceType, @NotNull final Type targetType) {
        final CacheKey cacheKey = new CacheKey(sourceType, targetType);

        // Take a look at the cache to see if this conversion has been done before
        GenericConverter converter = this.cache.get(cacheKey);

        if (converter != null) {
            // Check whether a converter was found in the previous conversion or not
            return converter != NO_MATCH_CONVERTER ? converter : null;
        }

        // Try to get converter from storage
        converter = this.storage.converter(sourceType, targetType);

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

            //TODO:
            return true;
        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return this.converter.convert(source, sourceType, targetType);
        }
    }

    private static final class ConverterStorage {

        //TODO:

        public void register(@NotNull final GenericConverter converter) {

        }

        public GenericConverter converter(@NotNull final Type sourceType, @NotNull final Type targetType) {
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
