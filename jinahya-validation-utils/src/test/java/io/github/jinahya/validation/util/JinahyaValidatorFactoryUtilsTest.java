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

class JinahyaValidatorFactoryUtilsTest {

    @DisplayName("applyValidator(factory, function1, function2)")
    @Nested
    class ApplyValidatorWithFunction {

        @Test
        void __nullFactory() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    null, ValidatorFactory::getValidator, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction1() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction2() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), ValidatorFactory::getValidator, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __function1ReturnsNull() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(
                    mock(ValidatorFactory.class), f -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesToValidatorFromFunction1() {
            final var factory = mock(ValidatorFactory.class);
            final var validator = mock(Validator.class);
            final var result = JinahyaValidatorFactoryUtils.applyValidator(factory, f -> validator, v -> v);
            assertThat(result).isSameAs(validator);
            verify(factory, never()).getValidator();
        }
    }

    @DisplayName("applyValidator(factory, function)")
    @Nested
    class ApplyValidator {

        @Test
        void __nullFactory() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction() {
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(mock(ValidatorFactory.class), null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __factoryReturnsNull() {
            final var factory = mock(ValidatorFactory.class);
            assertThatThrownBy(() -> JinahyaValidatorFactoryUtils.applyValidator(factory, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesToFactoryValidator() {
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
