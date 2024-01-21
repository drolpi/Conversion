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

public final class NumberToNumberConverter implements Converter<Number, Number> {

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    @Override
    public Number convert(Number source, @NotNull Type sourceType, @NotNull Type targetType) {
        if (Byte.class == targetType) {
            final long value = this.convertToLong(source, sourceType, targetType);
            if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                return source.byteValue();
            }
        } else if (Short.class == targetType) {
            final long value = this.convertToLong(source, sourceType, targetType);
            if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                return source.shortValue();
            }
        } else if (Integer.class == targetType) {
            final long value = this.convertToLong(source, sourceType, targetType);
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                return source.intValue();
            }
        } else if (Long.class == targetType) {
            return this.convertToLong(source, sourceType, targetType);
        } else if (BigInteger.class == targetType) {
            if (source instanceof BigDecimal bigDecimal) {
                return bigDecimal.toBigInteger();
            } else {
                return BigInteger.valueOf(source.longValue());
            }
        } else if (Float.class == targetType) {
            return source.floatValue();
        } else if (Double.class == targetType) {
            return source.doubleValue();
        } else if (BigDecimal.class == targetType) {
            return new BigDecimal(source.toString());
        }

        throw new ConversionFailedException(sourceType, targetType, source);
    }

    private long convertToLong(final Number source, final Type sourceType, final Type targetType) {
        final BigInteger bigInt = source instanceof BigInteger bigInteger ? bigInteger
            : source instanceof BigDecimal bigDecimal ? bigDecimal.toBigInteger() : null;

        if (bigInt == null || (bigInt.compareTo(LONG_MIN) >= 0 && bigInt.compareTo(LONG_MAX) <= 0)) {
            return source.longValue();
        }

        throw new ConversionFailedException(sourceType, targetType, source);
    }
}
