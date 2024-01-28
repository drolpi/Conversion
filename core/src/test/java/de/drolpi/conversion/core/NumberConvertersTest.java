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

package de.drolpi.conversion.core;

import de.drolpi.conversion.core.exception.ConversionFailedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class NumberConvertersTest {

    private final ConversionBus conversionBus = ConversionBus.createDefault();

    @Test
    void testStringToByte() {
        assertEquals(this.conversionBus.convert("1", Byte.class), (byte) 1);
    }

    @Test
    void testByteToString() {
        assertEquals(this.conversionBus.convert("A".getBytes()[0], String.class), "65");
    }

    @Test
    void testStringToShort() {
        assertEquals(this.conversionBus.convert("1", Short.class), (short) 1);
    }

    @Test
    void testShortToString() {
        assertEquals(this.conversionBus.convert((short) 3, String.class), "3");
    }

    @Test
    void testStringToInteger() {
        assertEquals(this.conversionBus.convert("1", Integer.class), 1);
    }

    @Test
    void testIntegerToString() {
        assertEquals(this.conversionBus.convert(3, String.class), "3");
    }

    @Test
    void testStringToLong() {
        assertEquals(this.conversionBus.convert("1", Long.class), Long.valueOf(1));
    }

    @Test
    void testLongToString() {
        assertEquals(this.conversionBus.convert(3L, String.class), "3");
    }

    @Test
    void testStringToFloat() {
        assertEquals(this.conversionBus.convert("1.0", Float.class), Float.valueOf("1.0"));
    }

    @Test
    void testFloatToString() {
        assertEquals(this.conversionBus.convert(Float.valueOf("1.0"), String.class), "1.0");
    }

    @Test
    void testStringToDouble() {
        assertEquals(this.conversionBus.convert("1.0", Double.class), Double.valueOf("1.0"));
    }

    @Test
    void testDoubleToString() {
        assertEquals(this.conversionBus.convert(Double.valueOf("1.0"), String.class), "1.0");
    }

    @Test
    void testStringToBigInteger() {
        assertEquals(this.conversionBus.convert("1", BigInteger.class), new BigInteger("1"));
    }

    @Test
    void testBigIntegerToString() {
        assertEquals(this.conversionBus.convert(new BigInteger("100"), String.class), "100");
    }

    @Test
    void testStringToBigDecimal() {
        assertEquals(this.conversionBus.convert("1.0", BigDecimal.class), new BigDecimal("1.0"));
    }

    @Test
    void testBigDecimalToString() {
        assertEquals(this.conversionBus.convert(new BigDecimal("100.00"), String.class), "100.00");
    }

    @Test
    void testStringToNumber() {
        assertEquals(this.conversionBus.convert("1.0", Number.class), new BigDecimal("1.0"));
    }

    @Test
    void testStringToNumberEmptyString() {
        assertNull(this.conversionBus.convert("", Number.class));
    }

    @Test
    void testStringToCharacterEmptyString() {
        assertNull(this.conversionBus.convert("", Character.class));
    }

    @Test
    void testNumberToNumber() {
        assertEquals(this.conversionBus.convert(1, Long.class), Long.valueOf(1));
    }

    @Test
    void testNumberToNumberNotSupportedNumber() {
        assertThrowsExactly(ConversionFailedException.class, () -> this.conversionBus.convert(1, TestNumber.class));
    }

    @Test
    void testNumberToCharacter() {
        assertEquals( this.conversionBus.convert(65, Character.class), Character.valueOf('A'));
    }

    @Test
    void testCharacterToNumber() {
        assertEquals(this.conversionBus.convert('A', Integer.class), 65);
    }

    public static class TestNumber extends Number {

        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }
    }
}
