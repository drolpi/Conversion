/*
 * Copyright 2023-2024 Lars Nippert
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

import de.drolpi.conversion.core.converter.NonGenericConverter;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Set;

public class StringToEnumConverter implements NonGenericConverter {

    private final IntegerToEnumConverter helpConverter = new IntegerToEnumConverter();

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object convert(Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        final String identifier = (String) source;

        try {
            int id = Integer.parseInt(identifier);
            return this.helpConverter.convert(id, int.class, targetType);
        } catch (NumberFormatException ignored) {

        }

        if (identifier.isEmpty()) {
            // It's an empty enum identifier: reset the enum value to null.
            return null;
        }
        final Class<? extends Enum> enumType = (Class<? extends Enum>) GenericTypeReflector.erase(targetType);
        return Enum.valueOf(enumType, identifier.trim());
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(String.class, Enum.class)
        );
    }
}