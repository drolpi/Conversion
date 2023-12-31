/*
 * Copyright 2023-2023 Lars Nippert
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

import de.drolpi.conversion.core.impl.BooleanToIntegerConverter;
import de.drolpi.conversion.core.impl.EnumToIntegerConverter;
import de.drolpi.conversion.core.impl.EnumToStringConverter;
import de.drolpi.conversion.core.impl.IntegerToBooleanConverter;
import de.drolpi.conversion.core.impl.MapToMapConverter;
import de.drolpi.conversion.core.impl.NumberToCharacterConverter;
import de.drolpi.conversion.core.impl.ObjectToStringConverter;
import de.drolpi.conversion.core.impl.StringToBooleanConverter;
import de.drolpi.conversion.core.impl.StringToCharacterConverter;
import de.drolpi.conversion.core.impl.StringToCurrencyConverter;
import de.drolpi.conversion.core.impl.StringToUriConverter;
import de.drolpi.conversion.core.impl.StringToUrlConverter;
import de.drolpi.conversion.core.impl.StringToUuidConverter;

import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

class DefaultConversionBus extends BasicConversionBus {

    public DefaultConversionBus() {
        // Scalar converters

        this.register(Boolean.class, String.class, new ObjectToStringConverter());
        this.register(String.class, Boolean.class, new StringToBooleanConverter());
        this.register(Boolean.class, Integer.class, new BooleanToIntegerConverter());
        this.register(Integer.class, Boolean.class, new IntegerToBooleanConverter());

        this.register(Character.class, String.class, new ObjectToStringConverter());
        this.register(String.class, Character.class, new StringToCharacterConverter());
        this.register(Number.class, Character.class, new NumberToCharacterConverter());

        this.register(Charset.class, String.class, new ObjectToStringConverter());
        this.register(CharSequence.class, String.class, new ObjectToStringConverter());
        this.register(StringWriter.class, String.class, new ObjectToStringConverter());

        this.register(Number.class, String.class, new ObjectToStringConverter());

        this.register(Currency.class, String.class, new ObjectToStringConverter());
        this.register(String.class, Currency.class, new StringToCurrencyConverter());

        this.register(UUID.class, String.class, new ObjectToStringConverter());
        this.register(String.class, UUID.class, new StringToUuidConverter());

        this.register(Pattern.class, String.class, new ObjectToStringConverter());

        this.register(Locale.class, String.class, new ObjectToStringConverter());

        this.register(URL.class, String.class, new ObjectToStringConverter());
        this.register(String.class, URL.class, new StringToUrlConverter());

        this.register(URI.class, String.class, new ObjectToStringConverter());
        this.register(String.class, URI.class, new StringToUriConverter());

        this.register(Enum.class, String.class, new EnumToStringConverter());
        this.register(Enum.class, Integer.class, new EnumToIntegerConverter());

        // Collection converters
        this.register(new MapToMapConverter());
    }
}
