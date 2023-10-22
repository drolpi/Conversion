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

package de.drolpi.conversion.core.converter;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a converter to convert a source object of type {@code T} to a target object of type {@code U}.
 *
 * @param <T> the source type
 * @param <U> the target type
 */
@FunctionalInterface
public interface Converter<T, U> {

    /**
     * Returns the target object of the conversion from the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     */
    @NotNull U convert(@NotNull T source);

}
