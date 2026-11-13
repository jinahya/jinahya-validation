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

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link JinahyaValidatorUtils}.
 */
class JinahyaValidatorUtilsTest {

    /**
     * A validation group other than the default.
     */
    private interface Group {

    }

    /**
     * A bean with a constraint in the default group, and another in the {@link Group}.
     *
     * @param a a value; must not be {@code null} in the default group.
     * @param b a value; must not be {@code null} in the {@link Group}.
     */
    private record Bean(@NotNull String a, @NotNull(groups = Group.class) String b) {

    }

    /**
     * The factory which {@link #buildFactory()} builds, and {@link #closeFactory()} closes.
     */
    private static ValidatorFactory factory;

    /**
     * The validator of the {@link #factory}.
     */
    private static Validator validator;

    /**
     * Builds the {@link #factory} with the provider of the active profile, and gets the {@link #validator} from it.
     */
    @BeforeAll
    static void buildFactory() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Closes the {@link #factory}.
     */
    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    /**
     * Tests {@link JinahyaValidatorUtils#validate(Validator, Object, Class[])}.
     */
    @DisplayName("validate(validator, object, groups)")
    @Nested
    class ValidateTest {

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code validator} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullValidator() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(null, new Bean("a", "b")))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code object} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullObject() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(validator, null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method throws a {@link NullPointerException} when the {@code groups} is {@code null}.
         */
        @Test
        void _throwNullPointerException_nullGroups() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(validator, new Bean("a", "b"), (Class<?>[]) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Verifies that the method returns no constraint violation for a valid object.
         */
        @Test
        void _empty_validObject() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean("a", "b"))).isEmpty();
        }

        /**
         * Verifies that the method validates against the default group when no group is specified.
         */
        @Test
        void _violationOfDefaultGroup_noGroups() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean(null, null)))
                    .singleElement()
                    .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("a"));
        }

        /**
         * Verifies that the method validates against the specified group only.
         */
        @Test
        void _violationOfSpecifiedGroup_groupSpecified() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean(null, null), Group.class))
                    .singleElement()
                    .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("b"));
        }
    }
}
