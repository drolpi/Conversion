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

import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

/**
 * Represents the entry point interface for type conversion.
 */
public interface ConversionBus {

    static @NotNull ConfigurableConversionBus create() {
        return new BasicConversionBus();
    }

    static @NotNull ConfigurableConversionBus createDefault() {
        return new DefaultConversionBus();
    }

    static @NotNull ConfigurableConversionBus createAlgorithm() {
        return new AlgorithmConversionBus();
    }

    static @NotNull ConfigurableConversionBus createAlgorithmDefault() {
        return new DefaultAlgorithmConversionBus();
    }

    /**
     * Returns whether this conversion bus can convert objects of {@code sourceType} to the {@code targetType}.
     *
     * @param sourceType the source type to convert from
     * @param targetType the target type to convert to
     * @return true, if this conversion bus can convert, false, if not
     */
    boolean canConvert(@Nullable Type sourceType, @NotNull Type targetType);

    /**
     * Converts the given {@code source} to the specified {@code targetType}.
     *
     * @param source the source object to convert
     * @param targetType the target type as {@link Type} to convert to
     * @return the converted object, an instance of targetType
     */
    @Nullable Object convert(@Nullable Object source, @NotNull Type targetType);

    /**
     * Converts the given {@code source} to the specified {@code targetType}.
     *
     * @param source the source object to convert
     * @param targetType the target type as {@link Class} to convert to
     * @return the converted object, an instance of targetType
     */
    @SuppressWarnings("unchecked")
    default <T> @Nullable T convert(@Nullable Object source, @NotNull Class<T> targetType) {
        requireNonNull(targetType, "targetType");
        return (T) this.convert(source, (Type) targetType);
    }

    /**
     * Converts the given {@code source} to the specified {@code targetType}.
     *
     * @param source the source object to convert
     * @param targetType the target type as {@link TypeToken} to convert to
     * @return the converted object, an instance of targetType
     */
    @SuppressWarnings("unchecked")
    default <T> @Nullable T convert(@Nullable Object source, @NotNull TypeToken<T> targetType) {
        requireNonNull(targetType, "targetType");
        return (T) this.convert(source, targetType.getType());
    }
}
