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

import de.drolpi.conversion.core.ConversionBus;
import de.drolpi.conversion.core.converter.ConversionPath;
import de.drolpi.conversion.core.converter.NonGenericConverter;
import de.drolpi.conversion.core.util.CollectionUtil;
import de.drolpi.conversion.core.util.ConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class ArrayToCollectionConverter implements NonGenericConverter {

    private final ConversionBus conversionBus;

    public ArrayToCollectionConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean isSuitable(@Nullable Type sourceType, @NotNull Type targetType) {
        return ConversionUtil.canConvertElements(ConversionUtil.elementType(sourceType, 1), ConversionUtil.elementType(targetType, 1),
            this.conversionBus);
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, @Nullable Type sourceType, @NotNull Type targetType) {
        if (source == null) {
            return null;
        }

        final Type targetElementType = ConversionUtil.elementType(targetType, 1);
        final int length = Array.getLength(source);
        final Collection<Object> target = CollectionUtil.createCollection(targetType, targetElementType, length);

        for (int i = 0; i < length; i++) {
            final Object sourceElement = Array.get(source, i);
            final Object targetElement = this.conversionBus.convert(sourceElement, targetElementType);
            target.add(targetElement);
        }
        return target;
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object[].class, Collection.class)
        );
    }
}
