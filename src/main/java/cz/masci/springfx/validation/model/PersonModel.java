package cz.masci.springfx.validation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PersonModel {

    @NotBlank(message = "Name must not be empty")
    private final StringProperty name = new SimpleStringProperty();

    @NotBlank(message = "Age must not be empty")
    @Pattern(regexp = "-?\\d+", message = "Age must be a number")
    private final StringProperty age = new SimpleStringProperty();

    @NotNull(message = "Gender must be selected")
    private final ObjectProperty<Gender> gender = new SimpleObjectProperty<>();

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getAge() {
        return age.get();
    }

    public StringProperty ageProperty() {
        return age;
    }

    public Gender getGender() {
        return gender.get();
    }

    public ObjectProperty<Gender> genderProperty() {
        return gender;
    }
}
