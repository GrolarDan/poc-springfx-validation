package cz.masci.springfx.validation.controller;

import cz.masci.springfx.validation.model.Gender;
import cz.masci.springfx.validation.validator.Constraint;
import cz.masci.springfx.validation.validator.Constraints;
import cz.masci.springfx.validation.validator.ValidationResult;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller for the person form.
 *
 * <p>Each field is validated on every change using the composable
 * {@link Constraint} framework. The Save button is enabled only when all
 * fields are simultaneously valid.</p>
 */
@Component
public class PersonFormController implements Initializable {

    // ---- name field --------------------------------------------------------
    @FXML
    private TextField nameField;
    @FXML
    private Label nameError;

    // ---- age field ---------------------------------------------------------
    @FXML
    private TextField ageField;
    @FXML
    private Label ageError;

    // ---- gender field ------------------------------------------------------
    @FXML
    private ComboBox<Gender> genderComboBox;
    @FXML
    private Label genderError;

    // ---- save button -------------------------------------------------------
    @FXML
    private Button saveButton;

    // ---- constraints -------------------------------------------------------
    private final Constraint<String> nameConstraint =
            Constraints.notBlank("Name");

    private final Constraint<String> ageConstraint =
            Constraints.<String>notBlank("Age").and(Constraints.isInteger("Age"));

    private final Constraint<Gender> genderConstraint =
            Constraints.notNull("Gender");

    // ---- state -------------------------------------------------------------
    private boolean nameValid = false;
    private boolean ageValid = false;
    private boolean genderValid = false;

    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderComboBox.getItems().addAll(Gender.values());

        // Attach listeners so validation runs on every keystroke / selection.
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            nameValid = applyConstraint(nameConstraint, newVal, nameError);
            updateSaveButton();
        });

        ageField.textProperty().addListener((obs, oldVal, newVal) -> {
            ageValid = applyConstraint(ageConstraint, newVal, ageError);
            updateSaveButton();
        });

        genderComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            genderValid = applyConstraint(genderConstraint, newVal, genderError);
            updateSaveButton();
        });

        // Disable save until the form is valid.
        saveButton.setDisable(true);
    }

    @FXML
    private void onSave() {
        // In a real application this would delegate to a service.
        System.out.printf("Saved: name=%s, age=%s, gender=%s%n",
                nameField.getText(), ageField.getText(), genderComboBox.getValue());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Runs the given constraint against {@code value}, updates the error label,
     * and returns {@code true} when the constraint passed.
     */
    private <T> boolean applyConstraint(Constraint<T> constraint, T value, Label errorLabel) {
        ValidationResult result = constraint.validate(value);
        if (result.valid()) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        } else {
            errorLabel.setText(result.message());
            errorLabel.setVisible(true);
        }
        return result.valid();
    }

    private void updateSaveButton() {
        saveButton.setDisable(!(nameValid && ageValid && genderValid));
    }
}
