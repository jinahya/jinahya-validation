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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link JinahyaValidatorFactoryUtils}.
 */
class JinahyaValidatorFactoryUtilsTest {

    /**
     * Tests {@link JinahyaValidatorFactoryUtils#applyValidator(ValidatorFactory, Function, Function)}.
     */
    @DisplayName("applyValidator(factory, function1, function2)")
    @Nested
    class ApplyValidatorWithFunctionTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code factory} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFactory() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    null, ValidatorFactory::getValidator, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction1() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function2} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction2() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), ValidatorFactory::getValidator, null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} returns
         * {@code null}.
         */
        @Test
        void _throwNullPointerException_function1ReturnsNull() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), f -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method applies the {@code function2} to the validator which the {@code function1} returns,
         * without getting one from the factory.
         */
        @Test
        void _appliedToValidatorOfFunction1_function1ReturnsValidator() {
            final var factory = mock(ValidatorFactory.class);
            final var validator = mock(Validator.class);
            final var result = JinahyaValidatorFactoryUtils.applyValidator(factory, f -> validator, v -> v);
            assertThat(result).isSameAs(validator);
            verify(factory, never()).getValidator();
        }
    }

    /**
     * Tests {@link JinahyaValidatorFactoryUtils#applyValidator(ValidatorFactory, Function)}.
     */
    @DisplayName("applyValidator(factory, function)")
    @Nested
    class ApplyValidatorTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code factory} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFactory() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(mock(ValidatorFactory.class), null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code factory} returns {@code null}
         * for a validator.
         */
        @Test
        void _throwNullPointerException_factoryReturnsNull() {
            final var factory = mock(ValidatorFactory.class);
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(factory, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method applies the function to the validator which the factory returns, without closing the
         * factory.
         */
        @Test
        void _appliedToValidatorOfFactory_factoryReturnsValidator() {
            final var factory = mock(ValidatorFactory.class);
            final var validator = mock(Validator.class);
            given(factory.getValidator()).willReturn(validator);
            final var result = JinahyaValidatorFactoryUtils.applyValidator(factory, v -> v);
            assertThat(result).isSameAs(validator);
            verify(factory).getValidator();
            verify(factory, never()).close();
        }
    }
}
