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

import de.drolpi.conversion.core.converter.Converter;
import de.drolpi.conversion.core.exception.ConversionFailedException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class StringToNumberConverter implements Converter<String, Number> {

    @Override
    public Number convert(@NotNull String source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (source.isEmpty()) {
            return null;
        }

        final String trimmed = source.trim();
        try {
            if (Byte.class == targetType || byte.class == targetType) {
                return (this.isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
            } else if (Short.class == targetType || short.class == targetType) {
                return (this.isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
            } else if (Integer.class == targetType || int.class == targetType) {
                return (this.isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
            } else if (Long.class == targetType || long.class == targetType) {
                return (this.isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
            } else if (BigInteger.class == targetType) {
                return (this.isHexNumber(trimmed) ? this.decodeBigInteger(trimmed) : new BigInteger(trimmed));
            } else if (Float.class == targetType || float.class == targetType) {
                return Float.valueOf(trimmed);
            } else if (Double.class == targetType || double.class == targetType) {
                return Double.valueOf(trimmed);
            } else if (BigDecimal.class == targetType || Number.class == targetType) {
                return new BigDecimal(trimmed);
            }
        } catch (NumberFormatException ignored) {

        }

        throw new ConversionFailedException(sourceType, targetType, source);
    }

    private boolean isHexNumber(String value) {
        final int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    private BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        final BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }
}
