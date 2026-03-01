package cz.masci.springfx.validation.validator;

import cz.masci.springfx.validation.model.Gender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the composable {@link Constraint} validation framework.
 */
class ConstraintTest {

    // ---- Constraints.notBlank -------------------------------------------

    @Test
    void notBlank_shouldFailOnNull() {
        Constraint<String> c = Constraints.notBlank("Name");
        ValidationResult result = c.validate(null);
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("Name must not be empty");
    }

    @Test
    void notBlank_shouldFailOnEmptyString() {
        Constraint<String> c = Constraints.notBlank("Name");
        assertThat(c.validate("").valid()).isFalse();
    }

    @Test
    void notBlank_shouldFailOnBlankString() {
        Constraint<String> c = Constraints.notBlank("Name");
        assertThat(c.validate("   ").valid()).isFalse();
    }

    @Test
    void notBlank_shouldPassOnNonBlankString() {
        Constraint<String> c = Constraints.notBlank("Name");
        assertThat(c.validate("Alice").valid()).isTrue();
    }

    // ---- Constraints.isInteger ------------------------------------------

    @Test
    void isInteger_shouldFailOnNonNumericString() {
        Constraint<String> c = Constraints.isInteger("Age");
        ValidationResult result = c.validate("abc");
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("Age must be a number");
    }

    @Test
    void isInteger_shouldPassOnNumericString() {
        Constraint<String> c = Constraints.isInteger("Age");
        assertThat(c.validate("42").valid()).isTrue();
    }

    @Test
    void isInteger_shouldPassOnNegativeInteger() {
        Constraint<String> c = Constraints.isInteger("Age");
        assertThat(c.validate("-5").valid()).isTrue();
    }

    // ---- Constraints.notNull -------------------------------------------

    @Test
    void notNull_shouldFailOnNull() {
        Constraint<Gender> c = Constraints.notNull("Gender");
        ValidationResult result = c.validate(null);
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("Gender must be selected");
    }

    @Test
    void notNull_shouldPassOnNonNull() {
        Constraint<Gender> c = Constraints.notNull("Gender");
        assertThat(c.validate(Gender.MAN).valid()).isTrue();
    }

    // ---- Constraint.and (composition) ----------------------------------

    @Test
    void and_shouldShortCircuitOnFirstFailure() {
        Constraint<String> combined = Constraints.<String>notBlank("Age")
                .and(Constraints.isInteger("Age"));

        // blank input: first constraint fails, second should not run
        ValidationResult result = combined.validate("");
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("Age must not be empty");
    }

    @Test
    void and_shouldEvaluateSecondWhenFirstPasses() {
        Constraint<String> combined = Constraints.<String>notBlank("Age")
                .and(Constraints.isInteger("Age"));

        // non-blank but not a number: second constraint fails
        ValidationResult result = combined.validate("notANumber");
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("Age must be a number");
    }

    @Test
    void and_shouldPassWhenBothConstraintsPass() {
        Constraint<String> combined = Constraints.<String>notBlank("Age")
                .and(Constraints.isInteger("Age"));

        assertThat(combined.validate("25").valid()).isTrue();
    }

    // ---- ValidationResult helpers --------------------------------------

    @Test
    void validationResult_successHasNoMessage() {
        ValidationResult r = ValidationResult.success();
        assertThat(r.valid()).isTrue();
        assertThat(r.message()).isNull();
    }

    @Test
    void validationResult_failureHasMessage() {
        ValidationResult r = ValidationResult.failure("something wrong");
        assertThat(r.valid()).isFalse();
        assertThat(r.message()).isEqualTo("something wrong");
    }
}
