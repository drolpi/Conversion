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

import de.drolpi.conversion.core.converter.NonGenericConverter;
import de.drolpi.conversion.core.exception.ConversionFailedException;
import de.drolpi.conversion.core.util.ClassCollectorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

class AlgorithmConversionBus extends BasicConversionBus {

    public AlgorithmConversionBus() {
        super(new AlgorithmConverterRegistrar());
    }

    private static final class AlgorithmConverterRegistrar extends ConverterRegistrar {

        @Override
        protected NonGenericConverter find(@NotNull Type sourceType, @NotNull Type targetType) {
            // Try to find an explicitly registered converter
            final NonGenericConverter converter = super.find(sourceType, targetType);
            if (converter != null) {
                return converter;
            }

            final AlgorithmResult result = new AlgorithmResult();
            final AlgorithmPath emptyPath = new AlgorithmPath();
            this.algorithm(sourceType, targetType, emptyPath, result);

            if (result.isEmpty()) {
                return null;
            }
            // Sort so that we start with the shortest path
            result.sort(Comparator.comparingInt(List::size));

            return new AlgorithmConverter(result);
        }

        private void algorithm(Type source, Type target, AlgorithmPath previousPath, AlgorithmResult result) {
            // Search the whole type tree
            final List<Class<?>> sourceTree = ClassCollectorUtil.classTree(source);
            final List<Class<?>> targetTree = ClassCollectorUtil.classTree(target);

            // Iterate through the whole class tree to find a converter with a matching source type
            for (final Class<?> sourceCandidate : sourceTree) {
                // Iterate through all registered converters
                for (final Map.Entry<NonGenericConverter.ConversionPath, Deque<NonGenericConverter>> entry : this.converters.entrySet()) {
                    final NonGenericConverter.ConversionPath path = entry.getKey();
                    // Skip if the source type does not match
                    if (path.sourceType() != sourceCandidate) {
                        continue;
                    }
                    // Skip if we already have this converter in our path to avoid going round in circles
                    if (previousPath.contains(entry.getValue())) {
                        continue;
                    }
                    // Create a new path, as we have multiple options here
                    final AlgorithmPath nextPath = new AlgorithmPath(previousPath);
                    nextPath.add(entry.getValue());

                    // Iterate through the entire class tree to check whether we are already at the end and the target type of the converter matches
                    for (final Class<?> targetCandidate : targetTree) {
                        if (!path.targetType().equals(targetCandidate)) {
                            continue;
                        }
                        result.add(nextPath);
                        return;
                    }

                    // Skip if the maximum depth is reached
                    if (nextPath.size() == 5) {
                        continue;
                    }

                    // Call recursive
                    this.algorithm(path.targetType(), target, nextPath, result);
                }
            }
        }
    }

    private record AlgorithmConverter(AlgorithmResult result) implements NonGenericConverter {

        @Override
        public @Nullable Object convert(@Nullable Object source, @NotNull Type sourceType, @NotNull Type targetType) {
            // Iterate through all the possibilities to try them out
            for (final AlgorithmPath path : this.result) {
                try {
                    // Try path
                    return this.tryConvert(path, source, sourceType, targetType);
                } catch (Exception ignored) {
                    // Do nothing because the next path will be tried
                }
            }

            throw new ConversionFailedException(sourceType, targetType, source);
        }

        private Object tryConvert(AlgorithmPath path, Object source, Type sourceType, Type targetType) {
            Object result = source;

            // Iterate through all ways of the path
            for (int i = 0; i < path.size(); i++) {
                final Deque<NonGenericConverter> converterDeque = path.get(i);

                //TODO: Can a converter generate a value other than null from the source null? Currently not!
                if (result == null) {
                    return null;
                }

                // Iterate through all possible converters of the way to check their conditions
                for (final NonGenericConverter nonGenericConverter : converterDeque) {
                    final Type sourceT = result.getClass();
                    Type targetT = targetType;
                    boolean suitable = false;

                    // Iterate through all suitable paths to check whether one of them is suitable
                    for (final ConversionPath conversionPath : nonGenericConverter.paths()) {
                        // If this is not the last converter, set the intermediate target type
                        if ((i + 1) != path.size()) {
                            targetT = conversionPath.targetType();
                        }

                        if (nonGenericConverter.isSuitable(sourceType, targetT)) {
                            suitable = true;
                            break;
                        }
                    }

                    if (suitable) {
                        result = nonGenericConverter.convert(result, sourceT, targetT);
                        break;
                    }
                }
            }

            return result;
        }

        @Override
        public @NotNull Set<ConversionPath> paths() {
            return Set.of();
        }

        @Override
        public boolean isSuitable(@NotNull Type sourceType, @NotNull Type targetType) {
            return true;
        }
    }

    private static class AlgorithmResult extends ArrayList<AlgorithmPath> {

    }

    private static class AlgorithmPath extends ArrayList<Deque<NonGenericConverter>> {

        public AlgorithmPath() {

        }

        public AlgorithmPath(@NotNull Collection<? extends Deque<NonGenericConverter>> c) {
            super(c);
        }
    }
}