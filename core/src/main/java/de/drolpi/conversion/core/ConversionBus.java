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

import java.lang.reflect.Type;

public interface ConversionBus {

    //TODO: Enable null sources

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

    Object convert(@NotNull Object source, @NotNull Type targetType);

    @SuppressWarnings("unchecked")
    default <T> T convert(@NotNull Object source, @NotNull Class<T> targetType) {
        return (T) this.convert(source, (Type) targetType);
    }

    @SuppressWarnings("unchecked")
    default <T> T convert(@NotNull Object source, @NotNull TypeToken<T> typeToken) {
        return (T) this.convert(source, typeToken.getType());
    }
}
