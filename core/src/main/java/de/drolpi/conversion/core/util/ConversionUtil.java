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

package de.drolpi.conversion.core.util;

import de.drolpi.conversion.core.ConversionBus;
import io.leangen.geantyref.GenericTypeReflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ConversionUtil {

    private ConversionUtil() {

    }

    public static Type[] elementTypes(final Type type, int maxLength) {
        final Class<?> erased = GenericTypeReflector.erase(type);
        final Type[] typeArgs;

        if (erased.isArray()) {
            typeArgs = new Type[]{GenericTypeReflector.getArrayComponentType(type)};
        } else {
            if (!(type instanceof ParameterizedType parameterizedType)) {
                return null;
            }
            typeArgs = parameterizedType.getActualTypeArguments();
        }

        if (typeArgs.length != maxLength) {
            throw new RuntimeException();
        }

        return typeArgs;
    }

    public static Type elementType(final Type type, int maxLength) {
        final Type[] types = elementTypes(type, maxLength);
        if (types == null) {
            return null;
        }
        return types[0];
    }

    public static boolean canConvertElements(Type sourceType, Type targetType, ConversionBus conversionBus) {
        final Type sourceElementType = elementType(sourceType, 1);
        final Type targetElementType = elementType(targetType, 1);

        if (targetElementType == null) {
            return true;
        }
        if (sourceElementType == null) {
            return true;
        }

        return conversionBus.canConvert(sourceElementType, targetElementType);
    }
}
