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

import de.drolpi.conversion.core.converter.ConversionPath;
import de.drolpi.conversion.core.converter.NonGenericConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public final class EnumToStringConverter implements NonGenericConverter {

    @Override
    public @Nullable Object convert(@Nullable Object source, @Nullable Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        if (!(source instanceof Enum<?> enumSource)) {
            return null;
        }

        return enumSource.name();
    }

    @Override
    public boolean isSuitable(@Nullable Type sourceType, @NotNull Type targetType) {
        return true;
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Enum.class, String.class)
        );
    }
}
