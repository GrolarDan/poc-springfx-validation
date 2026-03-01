package cz.masci.springfx.validation.validator;

/**
 * A single validation constraint.
 *
 * <p>Constraints can be composed with {@link #and(Constraint)} to build a chain where
 * the first failing constraint short-circuits the rest.</p>
 *
 * @param <T> type of the value being validated
 */
@FunctionalInterface
public interface Constraint<T> {

    ValidationResult validate(T value);

    /**
     * Returns a composed constraint that first applies {@code this} constraint and,
     * only if it passes, applies the {@code other} constraint.
     */
    default Constraint<T> and(Constraint<T> other) {
        return value -> {
            ValidationResult result = this.validate(value);
            return result.valid() ? other.validate(value) : result;
        };
    }
}
