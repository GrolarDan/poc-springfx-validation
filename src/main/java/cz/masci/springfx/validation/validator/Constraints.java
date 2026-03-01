package cz.masci.springfx.validation.validator;

/**
 * Factory methods for common {@link Constraint} implementations.
 */
public final class Constraints {

    private Constraints() {
    }

    /**
     * Constraint that fails when the string value is {@code null} or blank.
     */
    public static Constraint<String> notBlank(String fieldName) {
        return value -> (value == null || value.isBlank())
                ? ValidationResult.failure(fieldName + " must not be empty")
                : ValidationResult.success();
    }

    /**
     * Constraint that fails when the string value cannot be parsed as an integer.
     * Assumes the value is already non-blank (chain after {@link #notBlank}).
     */
    public static Constraint<String> isInteger(String fieldName) {
        return value -> {
            try {
                Integer.parseInt(value);
                return ValidationResult.success();
            } catch (NumberFormatException e) {
                return ValidationResult.failure(fieldName + " must be a number");
            }
        };
    }

    /**
     * Constraint that fails when the value is {@code null}.
     */
    public static <T> Constraint<T> notNull(String fieldName) {
        return value -> value == null
                ? ValidationResult.failure(fieldName + " must be selected")
                : ValidationResult.success();
    }
}
