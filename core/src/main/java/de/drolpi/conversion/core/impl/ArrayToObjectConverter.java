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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Set;

public final class ArrayToObjectConverter extends AbstractElementToObjectConverter<Object> {

    public ArrayToObjectConverter(ConversionBus conversionBus) {
        super(conversionBus);
    }

    @Override
    protected boolean isEmpty(Object collection) {
        return Array.getLength(collection) == 0;
    }

    @Override
    protected Object firstElement(Object collection) {
        return Array.get(collection, 0);
    }

    @Override
    public @NotNull Set<ConversionPath> paths() {
        return Set.of(
            new ConversionPath(Object[].class, Object.class)
        );
    }
}
