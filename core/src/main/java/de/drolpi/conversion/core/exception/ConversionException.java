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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Represents the base class for exceptions thrown by the conversion system.
 */
public abstract class ConversionException extends RuntimeException {

    private final Type sourceType;
    private Type targetType;

    public ConversionException(@NotNull Type sourceType, String message) {
        super(message);
        this.sourceType = sourceType;
    }

    public ConversionException(@NotNull Type sourceType, @NotNull Type targetType, String message) {
        super(message);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public ConversionException(@NotNull Type sourceType, @NotNull Type targetType, String message, Throwable cause) {
        super(message, cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public @NotNull Type sourceType() {
        return this.sourceType;
    }

    public @Nullable Type targetType() {
        return this.targetType;
    }
}
