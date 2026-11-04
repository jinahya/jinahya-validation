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

import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class JinahyaValidationTestUtilsTest {

    @DisplayName("applyCachedValidatorFactory(supplier, function)")
    @Nested
    class ApplyCachedValidatorFactory {

        @Test
        void __nullSupplier() {
            assertThatThrownBy(() -> JinahyaValidationTestUtils.applyCachedValidatorFactory(
                    null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction() {
            assertThatThrownBy(() -> JinahyaValidationTestUtils.applyCachedValidatorFactory(
                    () -> mock(ValidatorFactory.class), null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __supplierSuppliesNull() {
            assertThatThrownBy(() -> JinahyaValidationTestUtils.applyCachedValidatorFactory(
                    () -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __sameSupplier_sameFactoryBuiltOnceAndNotClosed() {
            final var factory = mock(ValidatorFactory.class);
            final var count = new AtomicInteger();
            final Supplier<ValidatorFactory> supplier = () -> {
                count.incrementAndGet();
                return factory;
            };
            final var first = JinahyaValidationTestUtils.applyCachedValidatorFactory(supplier, f -> f);
            final var second = JinahyaValidationTestUtils.applyCachedValidatorFactory(supplier, f -> f);
            assertThat(first).isSameAs(factory);
            assertThat(second).isSameAs(factory);
            assertThat(count).hasValue(1);
            verify(factory, never()).close();
        }

        @Test
        void __differentSuppliers_differentFactories() {
            final var factory1 = mock(ValidatorFactory.class);
            final var factory2 = mock(ValidatorFactory.class);
            final Supplier<ValidatorFactory> supplier1 = () -> factory1;
            final Supplier<ValidatorFactory> supplier2 = () -> factory2;
            final var result1 = JinahyaValidationTestUtils.applyCachedValidatorFactory(supplier1, f -> f);
            final var result2 = JinahyaValidationTestUtils.applyCachedValidatorFactory(supplier2, f -> f);
            assertThat(result1).isSameAs(factory1);
            assertThat(result2).isSameAs(factory2);
        }
    }
}
