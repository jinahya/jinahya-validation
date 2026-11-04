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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class JinahyaValidationUtilsTest {

    private record Bean(@NotNull String a, @NotNull String b) {
    }

    private static final Bean INVALID = new Bean(null, null);

    /**
     * A message interpolator which interpolates every message to {@value #MESSAGE}.
     */
    private static final MessageInterpolator INTERPOLATOR = new MessageInterpolator() {

        @Override
        public String interpolate(final String messageTemplate, final Context context) {
            return MESSAGE;
        }

        @Override
        public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
            return MESSAGE;
        }
    };

    private static final String MESSAGE = "interpolated";

    /**
     * Returns the messages of the constraint violations of {@link #INVALID} validated with the specified factory.
     */
    private static List<String> messages(final ValidatorFactory factory) {
        return factory.getValidator().validate(INVALID).stream().map(ConstraintViolation::getMessage).toList();
    }

    /**
     * Returns the type of the validation provider, which the active {@code jakarta-ee-*-*} profile names with the
     * {@code validation.provider} system property.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Configuration<T>, U extends ValidationProvider<T>> Class<U> providerType() {
        final var name = Objects.requireNonNull(System.getProperty("validation.provider"),
                                                "no validation.provider system property");
        try {
            return (Class<U>) Class.forName(name);
        } catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException("validation provider not found: " + name, cnfe);
        }
    }

    /**
     * Applies the {@link #INTERPOLATOR} through the provider overload, and returns the messages.
     */
    private static <T extends Configuration<T>, U extends ValidationProvider<T>> List<String> messagesWithProvider() {
        final Class<U> providerType = providerType();
        return JinahyaValidationUtils.applyValidatorFactory(
                providerType,
                c -> c.messageInterpolator(INTERPOLATOR),
                JinahyaValidationUtilsTest::messages
        );
    }

    @DisplayName("applyValidatorFactory(supplier, function)")
    @Nested
    class ApplyValidatorFactoryWithSupplier {

        @Test
        void __nullSupplier() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    (Supplier<ValidatorFactory>) null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    () -> mock(ValidatorFactory.class), null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __supplierSuppliesNull() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    () -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesAndCloses() {
            final var factory = mock(ValidatorFactory.class);
            final var result = JinahyaValidationUtils.applyValidatorFactory(() -> factory, f -> f);
            assertThat(result).isSameAs(factory);
            verify(factory).close();
        }

        @Test
        void __closesWhenFunctionThrows() {
            final var factory = mock(ValidatorFactory.class);
            final var thrown = new RuntimeException();
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(() -> factory, f -> {
                throw thrown;
            }))
                    .isSameAs(thrown);
            verify(factory).close();
        }
    }

    @DisplayName("applyValidatorFactory(function)")
    @Nested
    class ApplyValidatorFactoryWithDefault {

        @Test
        void __nullFunction() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    (Function<ValidatorFactory, Object>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesToDefaultFactoryAndCloses() {
            final var factory = mock(ValidatorFactory.class);
            try (var validation = mockStatic(Validation.class)) {
                validation.when(Validation::buildDefaultValidatorFactory).thenReturn(factory);
                final var result = JinahyaValidationUtils.applyValidatorFactory(f -> f);
                assertThat(result).isSameAs(factory);
            }
            verify(factory).close();
        }

        @Test
        void __validates() {
            final var size = JinahyaValidationUtils.applyValidatorFactory(
                    f -> f.getValidator().validate(INVALID).size());
            assertThat(size).isEqualTo(2);
        }
    }

    @DisplayName("applyValidatorFactory(function1, function2)")
    @Nested
    class ApplyValidatorFactoryWithBootstrap {

        @Test
        void __nullFunction1() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    (Function<GenericBootstrap, Configuration<?>>) null, Function.<ValidatorFactory>identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction2() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    b -> b.configure(), null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __function1ReturnsNull() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    b -> null, Function.<ValidatorFactory>identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesConfiguration() {
            final var messages = JinahyaValidationUtils.applyValidatorFactory(
                    b -> b.configure().messageInterpolator(INTERPOLATOR),
                    JinahyaValidationUtilsTest::messages);
            assertThat(messages).hasSize(2).containsOnly(MESSAGE);
        }
    }

    @DisplayName("applyValidatorFactory(providerType, function1, function2)")
    @Nested
    class ApplyValidatorFactoryWithProvider {

        @Test
        void __nullProviderType() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    (Class<FakeProvider>) null, c -> c, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction1() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    FakeProvider.class, null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullFunction2() {
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    FakeProvider.class, c -> c, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        <T extends Configuration<T>, U extends ValidationProvider<T>> void __function1ReturnsNull() {
            final Class<U> providerType = providerType();
            assertThatThrownBy(() -> JinahyaValidationUtils.applyValidatorFactory(
                    providerType, c -> null, Function.identity()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __appliesProviderSpecificConfiguration() {
            assertThat(messagesWithProvider()).hasSize(2).containsOnly(MESSAGE);
        }
    }

    /**
     * A configuration type for {@link FakeProvider}; never instantiated.
     */
    private interface FakeConfiguration extends Configuration<FakeConfiguration> {
    }

    /**
     * A provider type for the argument checks which fail before any provider is looked up; never instantiated.
     */
    private interface FakeProvider extends ValidationProvider<FakeConfiguration> {
    }
}
