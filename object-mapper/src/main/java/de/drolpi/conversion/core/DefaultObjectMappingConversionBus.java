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

import de.drolpi.conversion.objectmapper.ObjectMapper;
import de.drolpi.conversion.objectmapper.impl.MapToObjectConverter;
import de.drolpi.conversion.objectmapper.impl.ObjectToMapConverter;
import de.drolpi.conversion.objectmapper.impl.ObjectToObjectConverter;

public class DefaultObjectMappingConversionBus extends DefaultConversionBus implements ObjectMappingConversionBus {

    public DefaultObjectMappingConversionBus() {
        super();
        DefaultObjectMappingConversionBus.registerDefaults(this);
    }

    public static void registerDefaults(ConfigurableConversionBus bus) {
        final ObjectMapper.Factory factory = ObjectMapper.factory();
        bus.register(new ObjectToMapConverter(factory));
        bus.register(new MapToObjectConverter(factory));
        bus.register(new ObjectToObjectConverter(factory));
    }
}
