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

import org.apache.bval.jsr.ApacheValidationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link JinahyaValidationUtils} with Apache BVal, as the actual provider.
 *
 * @see JinahyaValidationUtilsTest
 */
class JinahyaValidationUtilsApacheBValTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Class, Function, Function)} with
     * {@link ApacheValidationProvider}.
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
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    ApacheValidationProvider.class, c -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the configuration which the {@code function1} returns is applied, with a message interpolator
         * which interpolates every message to {@value JinahyaValidationUtilsTest#MESSAGE}.
         */
        @Test
        void _configurationApplied_function1ConfiguresInterpolator() {
            final var messages = JinahyaValidationUtils.applyValidatorFactory(
                    ApacheValidationProvider.class,
                    c -> c.messageInterpolator(JinahyaValidationUtilsTest.INTERPOLATOR),
                    JinahyaValidationUtilsTest::messages);
            assertThat(messages).hasSize(2).containsOnly(JinahyaValidationUtilsTest.MESSAGE);
        }
    }
}
