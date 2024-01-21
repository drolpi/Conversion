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

package de.drolpi.conversion.objectmapper.impl;

import de.drolpi.conversion.objectmapper.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractObjectMappingConverter {

    protected final ObjectMapper.Factory factory;

    public AbstractObjectMappingConverter(ObjectMapper.Factory factory) {
        this.factory = factory;
    }

    protected boolean isMapSuitable(@NotNull Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)) {
            //TODO: is this unsafe?
            return true;
        }

        final Type[] typeArgs = parameterizedType.getActualTypeArguments();

        if (typeArgs.length != 2) {
            return false;
        }

        return typeArgs[0].equals(String.class) && typeArgs[1].equals(Object.class);
    }
}