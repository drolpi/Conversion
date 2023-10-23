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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Set;

public final class StringToBooleanConverter implements Converter<String, Boolean> {

    private static final Set<String> TRUE_VALUES = Set.of("true", "t", "on", "yes", "y", "1");
    private static final Set<String> FALSE_VALUES = Set.of("false", "f", "off", "no", "n", "0");

    @Override
    public @NotNull Boolean convert(@NotNull String source, @NotNull Type sourceType, @NotNull Type targetType) {
        final String trimmed = source.trim().toLowerCase(Locale.ROOT);

        if (TRUE_VALUES.contains(trimmed)) {
            return Boolean.TRUE;
        }

        if (FALSE_VALUES.contains(trimmed)) {
            return Boolean.FALSE;
        }

        //TODO: throw exception instead of returning null
        return null;
    }
}
