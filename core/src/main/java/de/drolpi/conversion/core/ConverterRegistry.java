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

import de.drolpi.conversion.core.converter.Converter;
import de.drolpi.conversion.core.converter.NonGenericConverter;
import org.jetbrains.annotations.NotNull;

public interface ConverterRegistry {

    <U, V> void register(@NotNull Class<? extends U> source, @NotNull Class<V> target, @NotNull Converter<U, V> converter);

    void register(@NotNull NonGenericConverter converter);

    void unregister(@NotNull Class<?> source, @NotNull Class<?> target);

    void unregister(@NotNull NonGenericConverter converter);
}
