/*
 * Copyright 2023-2023 Lars Nippert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use bus file except in compliance with the License.
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

import de.drolpi.conversion.core.impl.ArrayToArrayConverter;
import de.drolpi.conversion.core.impl.ArrayToCollectionConverter;
import de.drolpi.conversion.core.impl.ArrayToObjectConverter;
import de.drolpi.conversion.core.impl.BooleanToIntegerConverter;
import de.drolpi.conversion.core.impl.CharacterToNumberConverter;
import de.drolpi.conversion.core.impl.CollectionToArrayConverter;
import de.drolpi.conversion.core.impl.CollectionToCollectionConverter;
import de.drolpi.conversion.core.impl.CollectionToObjectConverter;
import de.drolpi.conversion.core.impl.EnumToIntegerConverter;
import de.drolpi.conversion.core.impl.EnumToStringConverter;
import de.drolpi.conversion.core.impl.IntegerToBooleanConverter;
import de.drolpi.conversion.core.impl.IntegerToEnumConverter;
import de.drolpi.conversion.core.impl.MapToMapConverter;
import de.drolpi.conversion.core.impl.NumberToCharacterConverter;
import de.drolpi.conversion.core.impl.NumberToNumberConverter;
import de.drolpi.conversion.core.impl.ObjectToArrayConverter;
import de.drolpi.conversion.core.impl.ObjectToCollectionConverter;
import de.drolpi.conversion.core.impl.ObjectToOptionalConverter;
import de.drolpi.conversion.core.impl.ObjectToStringConverter;
import de.drolpi.conversion.core.impl.OptionalToObjectConverter;
import de.drolpi.conversion.core.impl.StringToBooleanConverter;
import de.drolpi.conversion.core.impl.StringToCharacterConverter;
import de.drolpi.conversion.core.impl.StringToCurrencyConverter;
import de.drolpi.conversion.core.impl.StringToEnumConverter;
import de.drolpi.conversion.core.impl.StringToNumberConverter;
import de.drolpi.conversion.core.impl.StringToPatternConverter;
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

    DefaultConversionBus() {
        DefaultConversionBus.registerDefaults(this);
    }
    
    public static void registerDefaults(ConfigurableConversionBus bus) {
        // Scalar converters
        bus.register(Boolean.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, Boolean.class, new StringToBooleanConverter());
        bus.register(Boolean.class, Integer.class, new BooleanToIntegerConverter());
        bus.register(Integer.class, Boolean.class, new IntegerToBooleanConverter());

        bus.register(Character.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, Character.class, new StringToCharacterConverter());
        bus.register(Character.class, Number.class, new CharacterToNumberConverter());
        bus.register(Number.class, Character.class, new NumberToCharacterConverter());

        bus.register(Charset.class, String.class, new ObjectToStringConverter());
        bus.register(CharSequence.class, String.class, new ObjectToStringConverter());
        bus.register(StringWriter.class, String.class, new ObjectToStringConverter());

        bus.register(Number.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, Number.class, new StringToNumberConverter());
        bus.register(Number.class, Number.class, new NumberToNumberConverter());

        bus.register(Currency.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, Currency.class, new StringToCurrencyConverter());

        bus.register(UUID.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, UUID.class, new StringToUuidConverter());

        bus.register(Pattern.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, Pattern.class, new StringToPatternConverter());

        bus.register(Locale.class, String.class, new ObjectToStringConverter());

        bus.register(URL.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, URL.class, new StringToUrlConverter());

        bus.register(URI.class, String.class, new ObjectToStringConverter());
        bus.register(String.class, URI.class, new StringToUriConverter());

        // Generic converters
        bus.register(new EnumToStringConverter());
        bus.register(new StringToEnumConverter());
        bus.register(new EnumToIntegerConverter());
        bus.register(new IntegerToEnumConverter());

        bus.register(new ObjectToOptionalConverter(bus));
        bus.register(new OptionalToObjectConverter(bus));

        // Collection converters
        bus.register(new MapToMapConverter(bus));
        bus.register(new CollectionToCollectionConverter(bus));
        bus.register(new CollectionToObjectConverter(bus));
        bus.register(new ObjectToCollectionConverter(bus));
        bus.register(new CollectionToArrayConverter(bus));
        bus.register(new ArrayToCollectionConverter(bus));
        bus.register(new ArrayToObjectConverter(bus));
        bus.register(new ObjectToArrayConverter(bus));
        bus.register(new ArrayToArrayConverter(bus));
    }
}
