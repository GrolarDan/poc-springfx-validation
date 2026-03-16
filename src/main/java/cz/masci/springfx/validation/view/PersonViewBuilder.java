package cz.masci.springfx.validation.view;

import cz.masci.springfx.validation.model.Gender;
import cz.masci.springfx.validation.model.PersonModel;
import io.github.palexdev.materialfx.builders.base.LabeledBuilder;
import io.github.palexdev.materialfx.builders.control.ButtonBuilder;
import io.github.palexdev.materialfx.builders.control.ComboBuilder;
import io.github.palexdev.materialfx.builders.control.TextFieldBuilder;
import io.github.palexdev.materialfx.builders.layout.GridPaneBuilder;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class PersonViewBuilder implements Builder<Region> {
    private final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
    private final PersonModel viewModel;
    private final Runnable onChange;

    @Override
    public Region build() {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100.0);
        col1.setPrefWidth(120.0);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(200.0);
        col2.setPrefWidth(260.0);
        col2.setHgrow(Priority.ALWAYS);

        var items = FXCollections.observableList(Arrays.asList(Gender.values()));

        var nameField = TextFieldBuilder.textField()
                .setFloatingText("Name")
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId("nameField")
                .getNode();
        GridPane.setColumnSpan(nameField, 2);
        GridPane.setColumnIndex(nameField, 0);
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        var nameError = LabeledBuilder.control(new Label())
                .setId("nameError")
                .setStyleClasses("error-label")
                .getNode();
        GridPane.setColumnSpan(nameError, 2);
        GridPane.setColumnIndex(nameError, 0);
        nameError.setVisible(false);

        nameField.getValidator()
                .constraint(Constraint.of(Severity.ERROR, "Name must not be empty",
                        Bindings.createBooleanBinding(() -> {
                            String t = nameField.getText();
                            return t != null && !t.isBlank();
                        }, nameField.textProperty())));
        nameField.getValidator().validProperty().addListener((_, _, newValue) -> nameField.pseudoClassStateChanged(INVALID, !newValue));
        nameField.textProperty().addListener((_, _, newValue) -> {
            log.debug("Name field text changed to '{}'", newValue);
            var errors = nameField.getValidator().validate();
            nameError.setVisible(!errors.isEmpty());
            if (!errors.isEmpty()) {
                nameError.setText(errors.getFirst().getMessage());
            }
        });

        var ageField = TextFieldBuilder.textField()
                .setFloatingText("Age")
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId("ageField")
                .getNode();
        GridPane.setColumnSpan(ageField, 2);
        GridPane.setColumnIndex(ageField, 0);
        ageField.textProperty().bindBidirectional(viewModel.ageProperty());

        var ageError = LabeledBuilder.control(new Label())
                .setId("ageError")
                .setStyleClasses("error-label")
                .getNode();
        GridPane.setColumnSpan(ageError, 2);
        GridPane.setColumnIndex(ageError, 0);
        ageError.setVisible(false);

        ageField.getValidator()
                .constraint(
                        Constraint.of(
                                Severity.ERROR,
                                "Age must not be empty",
                                Bindings.isNotEmpty(ageField.textProperty())
                        )
                )
                .constraint(Constraint.of(
                        Severity.ERROR,
                        "Age must be a number",
                        Bindings.createBooleanBinding(() -> {
                            String t = ageField.getText();
                            return t != null && t.matches("-?\\d+");
                        }, ageField.textProperty())));
        ageField.getValidator().validProperty().addListener((_, _, newValue) -> ageField.pseudoClassStateChanged(INVALID, !newValue));
        ageField.textProperty().addListener((_, _, newValue) -> {
            log.debug("Age field text changed to '{}'", newValue);
            var errors = ageField.getValidator().validate();
            ageError.setVisible(!errors.isEmpty());
            if (!errors.isEmpty()) {
                ageError.setText(errors.getFirst().getMessage());
            }
        });

        var genderComboBox = ComboBuilder.combo(new MFXComboBox<Gender>())
                .setItems(items)
                .setFloatingText("Gender")
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId("genderComboBox")
                .getNode();
        GridPane.setColumnSpan(genderComboBox, 2);
        GridPane.setColumnIndex(genderComboBox, 0);
        genderComboBox.valueProperty().bindBidirectional(viewModel.genderProperty());

        var genderError = LabeledBuilder.control(new Label())
                .setId("genderError")
                .setStyleClasses("error-label")
                .getNode();
        GridPane.setColumnSpan(genderError, 2);
        GridPane.setColumnIndex(genderError, 0);
        genderError.setVisible(false);

        genderComboBox.getValidator()
                .constraint(Constraint.of(Severity.ERROR, "Gender must be selected",
                        genderComboBox.valueProperty().isNotNull()));
        genderComboBox.getValidator().validProperty().addListener((_, _, newValue) -> genderComboBox.pseudoClassStateChanged(INVALID, !newValue));
        genderComboBox.valueProperty().addListener((_, _, _) -> {
            var errors = genderComboBox.getValidator().validate();
            genderError.setVisible(!errors.isEmpty());
            if (!errors.isEmpty()) {
                genderError.setText(errors.getFirst().getMessage());
            }
        });

        var saveButton = ButtonBuilder.button()
                .setOnAction(_ -> onChange.run())
                .setText("Save")
                .setId("saveButton")
                .getNode();

        saveButton.disableProperty().bind(
                nameField.getValidator().validProperty()
                        .and(ageField.getValidator().validProperty())
                        .and(genderComboBox.getValidator().validProperty())
                        .not()
        );

        // Trigger validation to update initial state
        nameField.validate();
        ageField.validate();
        genderComboBox.validate();

        GridPane.setColumnIndex(saveButton, 1);
        GridPane.setHalignment(saveButton, HPos.RIGHT);

        return GridPaneBuilder.gridPane()
                .setHGap(10.0)
                .setVGap(4.0)
                .setColumnConstraints(col1, col2)
                .addRow(0, nameField)
                .addRow(1, nameError)
                .addRow(2, ageField)
                .addRow(3, ageError)
                .addRow(4, genderComboBox)
                .addRow(5, genderError)
                .addRow(6, saveButton)
                .setPadding(new Insets(20.0, 20.0, 20.0, 20.0))
                .getNode();

    }
}
