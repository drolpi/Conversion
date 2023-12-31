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

import de.drolpi.conversion.core.converter.NonGenericConverter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class MapToMapConverter implements NonGenericConverter {

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Collections.singleton(new ConversionPath(Map.class, Map.class));
    }

    @Override
    public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
        return false;
    }

    @Override
    public @NotNull Object convert(@NotNull Object source, @NotNull Type sourceType, @NotNull Type targetType) {
        return null;
    }
}
