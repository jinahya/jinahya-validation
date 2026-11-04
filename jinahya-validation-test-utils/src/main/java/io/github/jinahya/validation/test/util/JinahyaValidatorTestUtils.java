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

import io.github.jinahya.validation.util.JinahyaValidatorUtils;
import jakarta.validation.Validator;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test utilities for {@link jakarta.validation.Validator}.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaValidatorTestUtils {

    /**
     * Validates the specified object against the specified groups with the specified validator, and returns it when it
     * is valid.
     *
     * @param validator the validator to validate with.
     * @param object    the object to validate.
     * @param groups    the groups to validate against; none for the default group.
     * @param <T>       the type of the object.
     * @return the {@code object}.
     * @throws AssertionError       when the {@code object} has any constraint violation.
     * @throws NullPointerException when any argument is {@code null}.
     * @see JinahyaValidatorUtils#validate(Validator, Object, Class[])
     */
    public static <T> T assertValid(final Validator validator, final T object, final Class<?>... groups) {
        Objects.requireNonNull(validator, "validator is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        final var violations = JinahyaValidatorUtils.validate(validator, object, groups);
        assertThat(violations)
                .as("the constraint violations of %s", object)
                .isEmpty();
        return object;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     *
     * @throws AssertionError always.
     */
    private JinahyaValidatorTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
