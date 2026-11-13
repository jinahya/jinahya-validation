package io.github.jinahya.validation.test.util;

/*-
 * #%L
 * hr
 * %%
 * Copyright (C) 2024 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.github.jinahya.validation.util.JinahyaValidationUtils;
import jakarta.validation.ValidatorFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Caches validator factories by their suppliers, and closes them when the JVM exits.
 *
 * @see JinahyaValidationUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaValidationTestUtils {

    /**
     * Holds the validator factories cached by their suppliers, which the class loads on first use.
     */
    private static final class Factories {

        /**
         * The validator factories, keyed by their suppliers, and closed by a shutdown hook.
         */
        private static final Map<Supplier<? extends ValidatorFactory>, ValidatorFactory> FACTORIES =
                new ConcurrentHashMap<>();

        static {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> FACTORIES.values().forEach(ValidatorFactory::close)));
        }

        /**
         * Creates a new instance, which is not allowed.
         *
         * @throws AssertionError always.
         */
        private Factories() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to the validator factory cached for the specified supplier, which the function
     * must not close.
     * <p>
     * The factory is built with the {@code supplier} on the first call with it, cached by the identity of the
     * {@code supplier}, and closed when the JVM exits.
     *
     * @param supplier the supplier of a new validator factory; pass the same instance, e.g., a {@code static final}
     *                 field, or a reference to a static method, to get the same factory.
     * @param function the function to apply; must not close the factory, nor let it escape.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws NullPointerException when either argument is {@code null}, or the {@code supplier} supplies
     *                              {@code null}.
     * @apiNote A lambda which captures a value is a new instance on each evaluation, so passing one builds, and
     *         keeps until the JVM exits, a new factory on each call.
     * @see JinahyaValidationUtils#applyValidatorFactory(Supplier, Function)
     */
    public static <R> R applyCachedValidatorFactory(
            final Supplier<? extends ValidatorFactory> supplier,
            final Function<? super ValidatorFactory, ? extends R> function) {
        Objects.requireNonNull(supplier, "supplier is null");
        Objects.requireNonNull(function, "function is null");
        final var factory = Factories.FACTORIES.computeIfAbsent(
                supplier,
                s -> Objects.requireNonNull(s.get(), "supplier.get() is null")
        );
        return function.apply(factory);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     *
     * @throws AssertionError always.
     */
    private JinahyaValidationTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
