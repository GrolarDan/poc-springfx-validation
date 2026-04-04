# poc-springfx-validation

POC demonstrating real-time form validation in a **Spring Boot 4.0.3 + JavaFX 25** desktop application, with Material Design UI via [MaterialFX](https://github.com/palexdev/MaterialFX) (v11.17.0).

Validation is implemented using **Jakarta Bean Validation** annotations on the model and Spring's `Validator` bean, bridged to JavaFX properties via the custom `FXValidator` class.

---

## Quick Start

```bash
# Build
mvn clean package

# Run the JavaFX application
mvn javafx:run

# Run all tests
mvn test
```

---

## Project Structure

```
src/main/java/cz/masci/springfx/validation/
├── App.java                       # Spring Boot entry point
├── JavaFxApplication.java         # JavaFX Application, bootstraps Spring context
├── StageInitializer.java          # Builds the primary scene on StageReadyEvent
├── StageReadyEvent.java           # Custom Spring event bridging JavaFX ↔ Spring
├── controller/
│   └── PersonViewController.java  # Spring component; creates FXValidator and the view
├── model/
│   ├── PersonModel.java           # JavaFX model with Jakarta validation annotations on getters
│   └── Gender.java
├── validator/
│   └── FXValidator.java           # Wraps Jakarta Validator; exposes validProperty()
└── view/
    └── PersonViewBuilder.java     # Programmatic UI builder wired to FXValidator
```

---

## Validation – Jakarta Bean Validation + `FXValidator`

Validation rules are declared as standard Jakarta annotations on model getters. `FXValidator` bridges the Jakarta `Validator` to a JavaFX `BooleanProperty` so the Save button can be bound declaratively.

### Key classes

| Class | Role |
|---|---|
| `FXValidator<T>` | Wraps Jakarta `Validator`; validates per-property and exposes `validProperty()` |
| Model class | Plain JavaFX model with Jakarta annotations (`@NotBlank`, `@NotNull`, `@Pattern`, …) on **getters** |

### How to use in a new form

**1. Add the dependency** to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**2. Annotate the model getters** (Jakarta annotations go on the getter, not the field):

```java
public class PersonModel {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty age  = new SimpleStringProperty();
    private final ObjectProperty<Gender> gender = new SimpleObjectProperty<>();

    @NotBlank(message = "Name must not be empty")
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    @NotBlank(message = "Age must not be empty")
    @Pattern(regexp = "-?\\d+", message = "Age must be a number")
    public String getAge() { return age.get(); }
    public StringProperty ageProperty() { return age; }

    @NotNull(message = "Gender must be selected")
    public Gender getGender() { return gender.get(); }
    public ObjectProperty<Gender> genderProperty() { return gender; }
}
```

**3. Create an `FXValidator`** in the controller (inject Spring's `Validator`):

```java
@Component
public class MyViewController {
    private final FXValidator<PersonModel> validator;
    private final PersonModel viewModel = new PersonModel();

    public MyViewController(Validator validator) {   // Spring injects jakarta.validation.Validator
        this.validator = new FXValidator<>(validator, viewModel);
    }

    public Region getView() {
        return new MyViewBuilder(viewModel, validator, this::onSave).build();
    }

    private void onSave() {
        // delegate to service
    }
}
```

**4. Bind the Save button** to `validProperty()` in the view builder:

```java
saveButton.disableProperty().bind(validator.validProperty().not());
```

**5. Trigger per-property validation on change** using the property name matching the model getter (e.g. `"name"` for `getName()`):

```java
private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

nameField.textProperty().addListener(_ ->
    validator.validateProperty("name",
        () -> {
            errorLabel.setVisible(false);
            nameField.pseudoClassStateChanged(INVALID, false);
        },
        violations -> {
            String message = violations.iterator().next().getMessage();
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            nameField.pseudoClassStateChanged(INVALID, true);
        }
    )
);
```

**6. Reusable helper** – `PersonViewBuilder.enhanceWithValidation` wraps any control with an error label automatically. The control's `id` must match the property name in the model:

```java
// id = "name" → validateProperty("name") → validates getName()
var nameField = createTextField("Name", "name");
nameField.textProperty().bindBidirectional(viewModel.nameProperty());
var nameWithError = enhanceWithValidation(nameField, TextInputControl::textProperty);
```

Internally it calls `wireValidation`, which registers a change listener and delegates to `FXValidator.validateProperty`:

```java
private <C extends Node> void wireValidation(C control, Observable trigger, Labeled errorLabel, String propertyName) {
    trigger.addListener(_ ->
        validator.validateProperty(propertyName,
            () -> {
                errorLabel.setVisible(false);
                control.pseudoClassStateChanged(INVALID, false);
            },
            violations -> {
                String message = violations.stream()
                        .min(Comparator.comparingInt(v ->
                                v.getConstraintDescriptor().getAnnotation()
                                 .annotationType().getSimpleName().startsWith("Not") ? 0 : 1))
                        .map(ConstraintViolation::getMessage)
                        .orElse("N/A");
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                control.pseudoClassStateChanged(INVALID, true);
            }
        )
    );
}
```

---

## Spring Boot + JavaFX Integration

Spring and JavaFX have separate lifecycles bridged via a custom event:

1. `App` (`@SpringBootApplication`) calls `Application.launch(JavaFxApplication.class)`.
2. `JavaFxApplication.init()` bootstraps the Spring context (with `headless=false`) and publishes `StageReadyEvent` from `start(Stage)`.
3. `StageInitializer` (`@Component`, `ApplicationListener<StageReadyEvent>`) builds the primary scene using `PersonViewController`.

JavaFX controllers that need Spring injection must be `@Component`s and loaded via the Spring `ApplicationContext` as the FXML controller factory.