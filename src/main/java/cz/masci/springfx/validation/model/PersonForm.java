package cz.masci.springfx.validation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Model backing the person form. Bean Validation annotations document the
 * constraints for each field. The UI validates fields in real-time via the
 * composable {@link cz.masci.springfx.validation.validator.Constraint} framework;
 * these annotations can additionally be used with a Spring {@code Validator}
 * for server-side or programmatic validation.
 */
@Data
public class PersonForm {

    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotBlank(message = "Age must not be empty")
    @Pattern(regexp = "-?\\d+", message = "Age must be a number")
    private String age;

    @NotNull(message = "Gender must be selected")
    private Gender gender;
}
