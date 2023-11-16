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

package de.drolpi.conversion.core.impl;

import de.drolpi.conversion.core.converter.Converter;
import de.drolpi.conversion.core.exception.ConversionFailedException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public final class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

    private static final int TRUE_VALUE = 1;
    private static final int FALSE_VALUE = 0;

    @Override
    public @NotNull Boolean convert(@NotNull Integer source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == TRUE_VALUE) {
            return true;
        }

        if (source == FALSE_VALUE) {
            return false;
        }

        throw new ConversionFailedException(sourceType, targetType, "Can only convert an [Integer] with value equal to 0 or 1 to a [Boolean]");
    }
}
