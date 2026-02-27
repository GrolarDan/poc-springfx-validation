package cz.masci.springfx.validation.model;

/**
 * Represents the gender options available in the person form.
 */
public enum Gender {
    MAN("Man"),
    WOMAN("Woman");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
