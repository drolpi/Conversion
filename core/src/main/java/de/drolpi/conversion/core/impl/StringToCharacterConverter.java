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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public final class StringToCharacterConverter implements Converter<String, Character> {

    @Override
    public @Nullable Character convert(@NotNull String source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source.isEmpty()) {
            return null;
        }

        if (source.length() == 1) {
            return source.charAt(0);
        }

        throw new ConversionFailedException(sourceType, targetType,
            "Can only convert a [String] with length of 1 to a [Character]; string value '" + source + "'  has "
                + "length of " + source.length());
    }
}
