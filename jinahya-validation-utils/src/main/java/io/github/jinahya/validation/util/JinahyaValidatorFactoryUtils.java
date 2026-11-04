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

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * Utilities which get a {@link Validator} from a {@link ValidatorFactory} and apply a function to it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaValidatorFactoryUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified second function to the validator which the specified first function gets from the
     * specified validator factory.
     *
     * @param factory   the validator factory to get the validator from.
     * @param function1 the function which gets the validator from the {@code factory}.
     * @param function2 the function to apply to the validator.
     * @param <R>       the type of the result.
     * @return the result of the {@code function2}.
     * @throws NullPointerException when any argument is {@code null}, or the {@code function1} returns
     *                              {@code null}.
     */
    public static <R> R applyValidator(final ValidatorFactory factory,
                                       final Function<? super ValidatorFactory, ? extends Validator> function1,
                                       final Function<? super Validator, ? extends R> function2) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(function1, "function1 is null");
        Objects.requireNonNull(function2, "function2 is null");
        return function2.apply(Objects.requireNonNull(function1.apply(factory), "function1.apply(factory) is null"));
    }

    /**
     * Applies the specified function to a validator which the specified validator factory returns.
     *
     * @param factory  the validator factory to get the validator from.
     * @param function the function to apply to the validator.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws NullPointerException when either argument is {@code null}, or the {@code factory} returns
     *                              {@code null}.
     * @see ValidatorFactory#getValidator()
     */
    public static <R> R applyValidator(final ValidatorFactory factory,
                                       final Function<? super Validator, ? extends R> function) {
        return applyValidator(factory, ValidatorFactory::getValidator, function);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     *
     * @throws AssertionError always.
     */
    private JinahyaValidatorFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
