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

package de.drolpi.conversion.core.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public interface NonGenericConverter extends ConditionalConverter<Object, Object> {

    @Override
    @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType);

    @NotNull Set<ConversionPath> paths();

    record ConversionPath(@NotNull Class<?> sourceType, @NotNull Class<?> targetType) {

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof ConversionPath otherPath)) {
                return false;
            }

            return (this.sourceType == otherPath.sourceType && this.targetType == otherPath.targetType);
        }
    }
}
