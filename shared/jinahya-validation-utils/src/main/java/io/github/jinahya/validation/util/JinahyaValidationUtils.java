package io.github.jinahya.validation.util;

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

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.bootstrap.GenericBootstrap;
import jakarta.validation.spi.ValidationProvider;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utilities which get a {@link ValidatorFactory}, apply a function to it, and close it.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaValidationUtils {

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to a validator factory got from the specified supplier, and closes the factory.
     *
     * @param supplier the supplier of a new validator factory.
     * @param function the function to apply; must not close the factory, nor let it escape.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws NullPointerException when either argument is {@code null}, or the {@code supplier} supplies
     *                              {@code null}.
     */
    public static <R> R applyValidatorFactory(
            final Supplier<? extends ValidatorFactory> supplier,
            final Function<? super ValidatorFactory, ? extends R> function) {
        Objects.requireNonNull(supplier, "supplier is null");
        Objects.requireNonNull(function, "function is null");
        try (var factory = Objects.requireNonNull(supplier.get(), "supplier.get() is null")) {
            return function.apply(factory);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to a validator factory built with the default provider and its default
     * configuration, and closes the factory.
     *
     * @param function the function to apply; must not close the factory, nor let it escape.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws NullPointerException when the {@code function} is {@code null}.
     * @see Validation#buildDefaultValidatorFactory()
     */
    public static <R> R applyValidatorFactory(
            final Function<? super ValidatorFactory, ? extends R> function) {
        return applyValidatorFactory(Validation::buildDefaultValidatorFactory, function);
    }

    /**
     * Applies the specified second function to a validator factory built from the configuration which the specified
     * first function returns for the {@link Validation#byDefaultProvider() default provider's bootstrap}, and closes
     * the factory.
     *
     * @param function1 the function which returns, from the default provider's bootstrap, the configuration to build
     *                  the factory from.
     * @param function2 the function to apply to the factory; must not close the factory, nor let it escape.
     * @param <R>       the type of the result.
     * @return the result of the {@code function2}.
     * @throws NullPointerException when either function is {@code null}, or the {@code function1} returns
     *                              {@code null}.
     * @see Validation#byDefaultProvider()
     */
    public static <R> R applyValidatorFactory(
            final Function<? super GenericBootstrap, ? extends Configuration<?>> function1,
            final Function<? super ValidatorFactory, ? extends R> function2) {
        Objects.requireNonNull(function1, "function1 is null");
        return applyValidatorFactory(
                () -> Objects.requireNonNull(function1.apply(Validation.byDefaultProvider()),
                                             "function1 returned null")
                        .buildValidatorFactory(),
                function2
        );
    }

    /**
     * Applies the specified second function to a validator factory built from the configuration which the specified
     * first function returns for the specified provider's configuration, and closes the factory.
     *
     * @param providerType the type of the validation provider.
     * @param function1    the function which returns, from the provider's configuration, the configuration to build the
     *                     factory from.
     * @param function2    the function to apply to the factory; must not close the factory, nor let it escape.
     * @param <T>          the provider-specific type of the configuration.
     * @param <U>          the type of the validation provider.
     * @param <R>          the type of the result.
     * @return the result of the {@code function2}.
     * @throws NullPointerException when any argument is {@code null}, or the {@code function1} returns {@code null}.
     * @see Validation#byProvider(Class)
     */
    public static <T extends Configuration<T>, U extends ValidationProvider<T>, R> R applyValidatorFactory(
            final Class<U> providerType,
            final Function<? super T, ? extends T> function1,
            final Function<? super ValidatorFactory, ? extends R> function2) {
        Objects.requireNonNull(providerType, "providerType is null");
        Objects.requireNonNull(function1, "function1 is null");
        return applyValidatorFactory(
                () -> Objects.requireNonNull(function1.apply(Validation.byProvider(providerType).configure()),
                                             "function1 returned null")
                        .buildValidatorFactory(),
                function2
        );
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     *
     * @throws AssertionError always.
     */
    private JinahyaValidationUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
