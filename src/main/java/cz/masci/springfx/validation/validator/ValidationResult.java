package cz.masci.springfx.validation.validator;

/**
 * Holds the result of a validation check.
 *
 * @param valid   {@code true} if the value passed the constraint, {@code false} otherwise
 * @param message human-readable error message when {@code valid} is {@code false}, {@code null} otherwise
 */
public record ValidationResult(boolean valid, String message) {

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }
}
