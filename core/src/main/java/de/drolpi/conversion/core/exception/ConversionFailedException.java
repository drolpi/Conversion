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

package de.drolpi.conversion.core.exception;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Represents the exception, which gets thrown when an actual type conversion attempt fails.
 */
public class ConversionFailedException extends ConversionException {

    private final Object source;

    public ConversionFailedException(@NotNull Type sourceType, @NotNull Type targetType, @NotNull Object source) {
        super(sourceType, targetType, String.format("Failed to convert from type [%s] to type [%s] for value [%s]", sourceType, targetType, source));
        this.source = source;
    }

    public @NotNull Object source() {
        return this.source;
    }
}
