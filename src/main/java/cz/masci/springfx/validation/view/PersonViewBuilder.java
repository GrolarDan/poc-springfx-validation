package cz.masci.springfx.validation.view;

import cz.masci.springfx.validation.model.Gender;
import cz.masci.springfx.validation.model.PersonModel;
import io.github.palexdev.materialfx.builders.base.LabeledBuilder;
import io.github.palexdev.materialfx.builders.control.ButtonBuilder;
import io.github.palexdev.materialfx.builders.control.ComboBuilder;
import io.github.palexdev.materialfx.builders.control.TextFieldBuilder;
import io.github.palexdev.materialfx.builders.layout.GridPaneBuilder;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.materialfx.validation.Validated;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
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

        var nameField = createTextField("Name", "nameField");
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        var nameError = createErrorLabel("nameError");
        nameField.getValidator()
                .constraint(Constraint.of(Severity.ERROR, "Name must not be empty",
                        Bindings.createBooleanBinding(() -> {
                            String t = nameField.getText();
                            return t != null && !t.isBlank();
                        }, nameField.textProperty())));
        wireValidation(nameField, nameField.textProperty(), nameError);

        var ageField = createTextField("Age", "ageField");
        ageField.textProperty().bindBidirectional(viewModel.ageProperty());
        var ageError = createErrorLabel("ageError");
        ageField.getValidator()
                .constraint(Constraint.of(Severity.ERROR, "Age must not be empty",
                        Bindings.isNotEmpty(ageField.textProperty())))
                .constraint(Constraint.of(Severity.ERROR, "Age must be a number",
                        Bindings.createBooleanBinding(() -> {
                            String t = ageField.getText();
                            return t != null && t.matches("-?\\d+");
                        }, ageField.textProperty())));
        wireValidation(ageField, ageField.textProperty(), ageError);

        var genderComboBox = createComboBox(items, "Gender", "genderComboBox");
        genderComboBox.valueProperty().bindBidirectional(viewModel.genderProperty());
        var genderError = createErrorLabel("genderError");
        genderComboBox.getValidator()
                .constraint(Constraint.of(Severity.ERROR, "Gender must be selected",
                        genderComboBox.valueProperty().isNotNull()));
        wireValidation(genderComboBox, genderComboBox.valueProperty(), genderError);

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

    private MFXTextField createTextField(String floatingText, String id) {
        var field = TextFieldBuilder.textField()
                .setFloatingText(floatingText)
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId(id)
                .getNode();
        GridPane.setColumnSpan(field, 2);
        GridPane.setColumnIndex(field, 0);
        return field;
    }

    private <T> MFXComboBox<T> createComboBox(ObservableList<T> items, String floatingText, String id) {
        var combo = ComboBuilder.combo(new MFXComboBox<T>())
                .setItems(items)
                .setFloatingText(floatingText)
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId(id)
                .getNode();
        GridPane.setColumnSpan(combo, 2);
        GridPane.setColumnIndex(combo, 0);
        return combo;
    }

    private Label createErrorLabel(String id) {
        var label = (Label) LabeledBuilder.control(new Label())
                .setId(id)
                .setStyleClasses("error-label")
                .getNode();
        GridPane.setColumnSpan(label, 2);
        GridPane.setColumnIndex(label, 0);
        label.setVisible(false);
        return label;
    }

    private <T extends Node & Validated> void wireValidation(T control, Observable triggerObservable, Label errorLabel) {
        control.getValidator().validProperty().addListener((_, _, newValue) -> control.pseudoClassStateChanged(INVALID, !newValue));
        triggerObservable.addListener(_ -> {
            log.debug("{} changed", control.getId());
            var errors = control.getValidator().validate();
            errorLabel.setVisible(!errors.isEmpty());
            if (!errors.isEmpty()) {
                errorLabel.setText(errors.getFirst().getMessage());
            }
        });
    }
}
