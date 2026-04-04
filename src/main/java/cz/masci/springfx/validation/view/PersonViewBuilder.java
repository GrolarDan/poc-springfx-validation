package cz.masci.springfx.validation.view;

import cz.masci.springfx.validation.model.Gender;
import cz.masci.springfx.validation.model.PersonModel;
import cz.masci.springfx.validation.validator.FXValidator;
import io.github.palexdev.materialfx.builders.base.LabeledBuilder;
import io.github.palexdev.materialfx.builders.control.ButtonBuilder;
import io.github.palexdev.materialfx.builders.control.ComboBuilder;
import io.github.palexdev.materialfx.builders.control.TextFieldBuilder;
import io.github.palexdev.materialfx.builders.layout.GridPaneBuilder;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import jakarta.validation.ConstraintViolation;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class PersonViewBuilder implements Builder<Region> {
    private final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
    private final PersonModel viewModel;
    private final FXValidator<PersonModel> validator;
    private final Runnable onChange;

    @Override
    public Region build() {
        ColumnConstraints col = new ColumnConstraints();
        col.setMinWidth(200.0);
        col.setPrefWidth(260.0);
        col.setHgrow(Priority.ALWAYS);

        var items = FXCollections.observableList(Arrays.asList(Gender.values()));

        var nameField = createTextField("Name", "name");
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        var nameFiledWithError = enhanceWithValidation(nameField, TextInputControl::textProperty);

        var ageField = createTextField("Age", "age");
        ageField.textProperty().bindBidirectional(viewModel.ageProperty());
        var ageFieldWithError = enhanceWithValidation(ageField, TextInputControl::textProperty);

        var genderComboBox = createComboBox(items);
        genderComboBox.valueProperty().bindBidirectional(viewModel.genderProperty());
        var genderComboBoxWithError = enhanceWithValidation(genderComboBox, MFXComboBox::valueProperty);

        var saveButton = ButtonBuilder.button()
                .setOnAction(_ -> onChange.run())
                .setText("Save")
                .setId("saveButton")
                .getNode();

        saveButton.disableProperty().bind(validator.validProperty().not());

        GridPane.setColumnIndex(saveButton, 1);
        GridPane.setHalignment(saveButton, HPos.RIGHT);

        return GridPaneBuilder.gridPane()
                .setHGap(10.0)
                .setVGap(4.0)
                .setColumnConstraints(col)
                .addRow(0, nameFiledWithError)
                .addRow(1, ageFieldWithError)
                .addRow(2, genderComboBoxWithError)
                .addRow(3, saveButton)
                .setPadding(new Insets(20.0, 20.0, 20.0, 20.0))
                .getNode();

    }

    private MFXTextField createTextField(String floatingText, String id) {
        return TextFieldBuilder.textField()
                .setFloatingText(floatingText)
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId(id)
                .getNode();
    }

    private <T> MFXComboBox<T> createComboBox(ObservableList<T> items) {
        return ComboBuilder.combo(new MFXComboBox<T>())
                .setItems(items)
                .setFloatingText("Gender")
                .setFloatMode(FloatMode.ABOVE)
                .setMaxWidth(Double.MAX_VALUE)
                .setId("gender")
                .getNode();
    }

    private Labeled createErrorLabel(String id) {
        Labeled label = LabeledBuilder.control(new Label())
                .setId(id)
                .setStyleClasses("error-label")
                .getNode();
        label.setVisible(false);
        return label;
    }

    private <C extends Node> void wireValidation(C control, Observable triggerObservable, Labeled errorLabel, String propertyName) {
        triggerObservable.addListener(_ ->
                validator.validateProperty(propertyName,
                        () -> {
                            errorLabel.setVisible(false);
                            control.pseudoClassStateChanged(INVALID, false);
                        },
                        violations -> {
                            // Show NotBlank/NotNull violations first so "must not be empty" takes priority
                            String message = violations.stream()
                                    .min(Comparator.comparingInt(v -> v.getConstraintDescriptor()
                                            .getAnnotation().annotationType().getSimpleName().startsWith("Not") ? 0 : 1))
                                    .map(ConstraintViolation::getMessage)
                                    .orElse("N/A");
                            errorLabel.setText(message);
                            errorLabel.setVisible(true);
                            control.pseudoClassStateChanged(INVALID, true);
                        }
                )
        );
    }

    private <C extends Node> GridPane enhanceWithValidation(C control, Function<C, Observable> observableFunction) {
        var propertyName = control.getId();
        var errorLabel = createErrorLabel(propertyName + "Error");
        GridPane.setHgrow(errorLabel, Priority.ALWAYS);

        wireValidation(control, observableFunction.apply(control), errorLabel, propertyName);

        ColumnConstraints col = new ColumnConstraints();
        col.setMinWidth(200.0);
        col.setPrefWidth(260.0);
        col.setHgrow(Priority.ALWAYS);

        return GridPaneBuilder.gridPane()
                .setColumnConstraints(col)
                .addRow(0, control)
                .addRow(1, errorLabel)
                .getNode();
    }
}
