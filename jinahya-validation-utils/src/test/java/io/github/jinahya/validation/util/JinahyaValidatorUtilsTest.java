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

class JinahyaValidatorUtilsTest {

    private interface Group {
    }

    private record Bean(@NotNull String a, @NotNull(groups = Group.class) String b) {
    }

    private static ValidatorFactory factory;

    private static Validator validator;

    @BeforeAll
    static void buildFactory() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @DisplayName("validate(validator, object, groups)")
    @Nested
    class Validate {

        @Test
        void __nullValidator() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(null, new Bean("a", "b")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullObject() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(validator, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __nullGroups() {
            assertThatThrownBy(() -> JinahyaValidatorUtils.validate(validator, new Bean("a", "b"), (Class<?>[]) null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void __valid() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean("a", "b"))).isEmpty();
        }

        @Test
        void __invalidAgainstDefaultGroup() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean(null, null)))
                    .singleElement()
                    .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("a"));
        }

        @Test
        void __invalidAgainstSpecifiedGroup() {
            assertThat(JinahyaValidatorUtils.validate(validator, new Bean(null, null), Group.class))
                    .singleElement()
                    .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("b"));
        }
    }
}
