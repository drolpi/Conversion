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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ScalarConvertersTest {

    private final ConversionBus conversionBus = ConversionBus.createDefault();

    @Test
    void testStringToCharacter() {
        assertEquals(this.conversionBus.convert("1", Character.class), Character.valueOf('1'));
    }

    @Test
    void testCharacterToString() {
        assertEquals(this.conversionBus.convert('3', String.class), "3");
    }

    @Test
    void testCharsetToString() {
        assertEquals(this.conversionBus.convert(StandardCharsets.UTF_8, String.class), "UTF-8");
    }

    @Test
    void testStringToCurrency() {
        assertEquals(this.conversionBus.convert("EUR", Currency.class), Currency.getInstance("EUR"));
    }

    @Test
    void testCurrencyToString() {
        assertEquals(this.conversionBus.convert(Currency.getInstance("USD"), String.class), "USD");
    }

    @Test
    void testStringToString() {
        final String str = "string";
        assertSame(this.conversionBus.convert(str, String.class), str);
    }

    @Test
    void testUuidToStringAndStringToUuid() {
        final UUID uuid = UUID.randomUUID();
        final String convertToString = this.conversionBus.convert(uuid, String.class);
        final UUID convertToUUID = this.conversionBus.convert(convertToString, UUID.class);
        assertEquals(convertToUUID, uuid);
    }

    @Test
    void testStringToPatternEmptyString() {
        assertNull(this.conversionBus.convert("", Pattern.class));
    }

    @Test
    void testStringToPattern() {
        final String pattern = "\\s";
        final Pattern convertToPattern = this.conversionBus.convert(pattern, Pattern.class);
        assertNotNull(convertToPattern);
        assertEquals(convertToPattern.pattern(), pattern);
    }

    @Test
    void testPatternToString() {
        final String regex = "\\d";
        assertEquals(this.conversionBus.convert(Pattern.compile(regex), String.class), regex);
    }

    @Test
    void testStringToBooleanEmptyString() {
        assertNull(this.conversionBus.convert("", Boolean.class));
    }

    @Test
    void testStringToBooleanInvalidString() {
        assertThrowsExactly(ConversionFailedException.class, () -> this.conversionBus.convert("invalid", Boolean.class));
    }

    @Test
    void testStringToBooleanTrue() {
        assertEquals(Boolean.TRUE, this.conversionBus.convert("1", Boolean.class));
        assertEquals(Boolean.TRUE, this.conversionBus.convert("true", Boolean.class));
        assertEquals(Boolean.TRUE, this.conversionBus.convert("yes", Boolean.class));
        assertEquals(Boolean.TRUE, this.conversionBus.convert("TRUE", Boolean.class));
        assertEquals(Boolean.TRUE, this.conversionBus.convert("YES", Boolean.class));
    }

    @Test
    void testStringToBooleanFalse() {
        assertEquals(Boolean.FALSE, this.conversionBus.convert("0", Boolean.class));
        assertEquals(Boolean.FALSE, this.conversionBus.convert("false", Boolean.class));
        assertEquals(Boolean.FALSE, this.conversionBus.convert("no", Boolean.class));
        assertEquals(Boolean.FALSE, this.conversionBus.convert("FALSE", Boolean.class));
        assertEquals(Boolean.FALSE, this.conversionBus.convert("NO", Boolean.class));
    }

    @Test
    void testBooleanToString() {
        assertEquals(this.conversionBus.convert(true, String.class), "true");
    }
}
