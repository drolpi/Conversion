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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumConvertersTest {

    private final ConversionBus conversionBus = ConversionBus.createDefault();

    @Test
    void testStringToEnum() {
        assertEquals(this.conversionBus.convert("BAR", Foo.class), Foo.BAR);
    }

    @Test
    void testStringToEnumWithSubclass() {
        assertEquals(this.conversionBus.convert("BAZ", SubFoo.class), SubFoo.BAZ);
    }

    @Test
    void testStringToEnumEmptyString() {
        assertNull(this.conversionBus.convert("", Foo.class));
    }

    @Test
    void testEnumToString() {
        assertEquals(this.conversionBus.convert(Foo.BAR, String.class), "BAR");
    }

    @Test
    void testIntegerToEnum() {
        assertEquals(this.conversionBus.convert(0, Foo.class), Foo.BAR);
    }

    @Test
    void testIntegerToEnumWithSubclass() {
        assertEquals(this.conversionBus.convert(1, SubFoo.class), SubFoo.BAZ);
    }

    @Test
    void testIntegerToEnumNull() {
        assertNull(this.conversionBus.convert(null, Foo.class));
    }

    @Test
    void testEnumToInteger() {
        assertEquals(this.conversionBus.convert(Foo.BAR, Integer.class), 0);
    }

    public enum Foo {

        BAR, BAZ
    }


    public enum SubFoo {

        BAR {
            @Override
            String s() {
                return "x";
            }
        },
        BAZ {
            @Override
            String s() {
                return "y";
            }
        };

        abstract String s();
    }
}
