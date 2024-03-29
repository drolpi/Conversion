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
 * Represents the exception, which gets thrown when no suitable converter could be found in a given conversion service.
 */
public class ConverterNotFoundException extends ConversionException {

    public ConverterNotFoundException(@Nullable Type sourceType, @NotNull Type targetType) {
        super(sourceType, targetType, String.format("No converter found for the conversion from type [%s] to type [%s]", sourceType, targetType));
    }
}
