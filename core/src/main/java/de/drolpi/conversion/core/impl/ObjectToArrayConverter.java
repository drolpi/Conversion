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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Set;

public final class ObjectToArrayConverter extends AbstractObjectToElementConverter<Object> {

    public ObjectToArrayConverter(ConversionBus conversionBus) {
        super(conversionBus);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object.class, Object[].class)
        );
    }

    @Override
    protected Object createNew(Class<?> type, Class<?> elementType) {
        return Array.newInstance(elementType, 1);
    }

    @Override
    protected void add(int index, Object collection, @Nullable Object element) {
        Array.set(collection, index, element);
    }
}
