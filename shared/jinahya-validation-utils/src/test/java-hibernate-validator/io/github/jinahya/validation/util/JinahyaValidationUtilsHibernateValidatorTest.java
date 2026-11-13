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

import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static io.github.jinahya.validation.util.JinahyaValidationUtils.applyValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link JinahyaValidationUtils} with Hibernate Validator, as the actual provider.
 *
 * @see JinahyaValidationUtilsTest
 */
class JinahyaValidationUtilsHibernateValidatorTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Class, Function, Function)} with
     * {@link HibernateValidator}.
     */
    @DisplayName("applyValidatorFactory(providerType, function1, function2)")
    @Nested
    class ApplyValidatorFactoryWithProviderTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} returns
         * {@code null}.
         */
        @Test
        void _throwNullPointerException_function1ReturnsNull() {
            assertThatThrownBy(() -> applyValidatorFactory(
                    HibernateValidator.class, c -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the configuration which the {@code function1} returns is applied, with a message interpolator
         * which interpolates every message to {@value JinahyaValidationUtilsTest#MESSAGE}.
         */
        @Test
        void _configurationApplied_function1ConfiguresInterpolator() {
            final var messages = applyValidatorFactory(
                    HibernateValidator.class,
                    c -> c.messageInterpolator(JinahyaValidationUtilsTest.INTERPOLATOR),
                    JinahyaValidationUtilsTest::messages);
            assertThat(messages).hasSize(2).containsOnly(JinahyaValidationUtilsTest.MESSAGE);
        }

        /**
         * Verifies that a provider-specific configuration is applied, with Hibernate Validator's fail-fast mode, which
         * stops at the first constraint violation.
         */
        @Test
        void _firstViolationOnly_function1EnablesFailFast() {
            final var size = applyValidatorFactory(
                    HibernateValidator.class,
                    c -> c.failFast(true),
                    f -> f.getValidator().validate(JinahyaValidationUtilsTest.INVALID).size());
            assertThat(size).isEqualTo(1);
        }
    }
}
