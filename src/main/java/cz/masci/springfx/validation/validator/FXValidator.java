package cz.masci.springfx.validation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class FXValidator<T> {
    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    private final Validator validator;
    private final T model;

    public boolean isValid() {
        return valid.get();
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    public void validateProperty(String propertyName, Runnable doOnValidProperty, Consumer<Set<ConstraintViolation<T>>> doOnInvalidProperty) {
        log.debug("Validating property: {}", propertyName);
        // validate property
        var propertyViolations = validator.validateProperty(model, propertyName);
        log.debug("Validation result for {}: valid={}, property violations={}", propertyName, valid.get(), propertyViolations.size());
        if (propertyViolations.isEmpty()) {
            doOnValidProperty.run();
        } else {
            doOnInvalidProperty.accept(propertyViolations);
        }
        // validate model
        var modelViolations = validator.validate(model);
        valid.setValue(modelViolations.isEmpty());
    }
}
