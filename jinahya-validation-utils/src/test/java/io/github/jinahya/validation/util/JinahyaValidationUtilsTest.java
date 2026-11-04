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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.bootstrap.GenericBootstrap;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.spi.ValidationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.jinahya.validation.util.JinahyaValidationUtils.applyValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link JinahyaValidationUtils}.
 */
class JinahyaValidationUtilsTest {

    /**
     * A bean with two constraints in the default group.
     *
     * @param a the first value; must not be {@code null}.
     * @param b the second value; must not be {@code null}.
     */
    record Bean(@NotNull String a, @NotNull String b) {

    }

    /**
     * A bean which violates both of its constraints.
     */
    static final Bean INVALID = new Bean(null, null);

    /**
     * A message interpolator which interpolates every message to {@value #MESSAGE}.
     */
    static final MessageInterpolator INTERPOLATOR = new MessageInterpolator() {

        @Override
        public String interpolate(final String messageTemplate, final Context context) {
            return MESSAGE;
        }

        @Override
        public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
            return MESSAGE;
        }
    };

    /**
     * The message which the {@link #INTERPOLATOR} interpolates every message to.
     */
    static final String MESSAGE = "interpolated";

    /**
     * Returns the messages of the constraint violations of {@link #INVALID} validated with the specified factory.
     *
     * @param factory the factory to get a validator from.
     * @return the messages of the constraint violations of {@link #INVALID}.
     */
    static List<String> messages(final ValidatorFactory factory) {
        return factory.getValidator().validate(INVALID).stream().map(ConstraintViolation::getMessage).toList();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A configuration type for {@link FakeProvider}; never instantiated.
     */
    private interface FakeConfiguration
            extends Configuration<FakeConfiguration> {

    }

    /**
     * A provider type for the argument checks which fail before any provider is looked up; never instantiated.
     */
    private interface FakeProvider
            extends ValidationProvider<FakeConfiguration> {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Supplier, Function)}.
     */
    @DisplayName("applyValidatorFactory(supplier, function)")
    @Nested
    class ApplyValidatorFactoryWithSupplierTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code supplier} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullSupplier() {
            assertThatThrownBy(() -> applyValidatorFactory(
                    (Supplier<ValidatorFactory>) null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction() {
            assertThatThrownBy(() -> applyValidatorFactory(
                    () -> mock(ValidatorFactory.class), null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code supplier} supplies
         * {@code null}.
         */
        @Test
        void _throwNullPointerException_supplierSuppliesNull() {
            assertThatThrownBy(() -> applyValidatorFactory(
                    () -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method applies the function to the supplied factory, and closes the factory.
         */
        @Test
        void _appliedAndClosed_factorySupplied() {
            final var factory = mock(ValidatorFactory.class);
            final var result = applyValidatorFactory(() -> factory, f -> f);
            assertThat(result).isSameAs(factory);
            verify(factory).close();
        }

        /**
         * Verifies that the method closes the factory even when the function throws, and propagates what it throws.
         */
        @Test
        void _closed_functionThrows() {
            final var factory = mock(ValidatorFactory.class);
            final var thrown = new RuntimeException();
            assertThatThrownBy(() -> applyValidatorFactory(() -> factory, f -> {
                throw thrown;
            }))
                    .isSameAs(thrown);
            verify(factory).close();
        }
    }

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Function)}.
     */
    @DisplayName("applyValidatorFactory(function)")
    @Nested
    class ApplyValidatorFactoryWithDefaultTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction() {
            assertThatThrownBy(() -> applyValidatorFactory(
                    (Function<ValidatorFactory, Object>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method applies the function to the factory which
         * {@link Validation#buildDefaultValidatorFactory()} builds, and closes the factory.
         */
        @Test
        void _appliedToDefaultFactoryAndClosed_function() {
            final var factory = mock(ValidatorFactory.class);
            try (var validation = mockStatic(Validation.class)) {
                validation.when(Validation::buildDefaultValidatorFactory).thenReturn(factory);
                final var result = applyValidatorFactory(f -> f);
                assertThat(result).isSameAs(factory);
            }
            verify(factory).close();
        }

        /**
         * Verifies that the default factory reports every constraint violation of an invalid object.
         */
        @Test
        void _allViolations_invalidObject() {
            final var size = applyValidatorFactory(
                    f -> f.getValidator().validate(INVALID).size());
            assertThat(size).isEqualTo(2);
        }
    }

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Function, Function)}.
     */
    @DisplayName("applyValidatorFactory(function1, function2)")
    @Nested
    class ApplyValidatorFactoryWithBootstrapTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction1() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            (Function<GenericBootstrap, Configuration<?>>) null, Function.identity()
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function2} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction2() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            GenericBootstrap::configure, null
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} returns
         * {@code null}.
         */
        @Test
        void _throwNullPointerException_function1ReturnsNull() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            b -> null, Function.identity()
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the configuration which the {@code function1} returns is applied, with a message interpolator
         * which interpolates every message to {@value #MESSAGE}.
         */
        @Test
        void _configurationApplied_function1ConfiguresInterpolator() {
            final var messages = applyValidatorFactory(
                    b -> b.configure().messageInterpolator(INTERPOLATOR),
                    JinahyaValidationUtilsTest::messages);
            assertThat(messages).hasSize(2).containsOnly(MESSAGE);
        }
    }

    /**
     * Tests {@link JinahyaValidationUtils#applyValidatorFactory(Class, Function, Function)} for the checks which fail
     * before any provider is looked up; the tests which need an actual provider are in the provider-specific test
     * sources, e.g., {@code src/test/java-hibernate-validator}.
     */
    @DisplayName("applyValidatorFactory(providerType, function1, function2)")
    @Nested
    class ApplyValidatorFactoryWithProviderTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code providerType} is
         * {@code null}.
         */
        @Test
        void _throwNullPointerException_nullProviderType() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            (Class<FakeProvider>) null, c -> c, Function.identity()
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function1} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction1() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            FakeProvider.class, null, Function.identity()
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code function2} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullFunction2() {
            assertThatThrownBy(
                    () -> applyValidatorFactory(
                            FakeProvider.class,
                            c -> c,
                            null
                    )
            ).isInstanceOf(NullPointerException.class);
        }
    }
}
