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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class IntegerToEnumConverter implements NonGenericConverter {

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Integer identifier = (Integer) source;
        final Class<? extends Enum> enumType = (Class<? extends Enum>) GenericTypeReflector.erase(targetType);

        return enumType.getEnumConstants()[identifier];
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Integer.class, Enum.class)
        );
    }
}
