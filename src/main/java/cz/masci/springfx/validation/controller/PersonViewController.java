package cz.masci.springfx.validation.controller;

import cz.masci.springfx.validation.model.PersonModel;
import cz.masci.springfx.validation.validator.FXValidator;
import cz.masci.springfx.validation.view.PersonViewBuilder;
import jakarta.validation.Validator;
import javafx.scene.layout.Region;
import org.springframework.stereotype.Component;

@Component
public class PersonViewController {
    private final FXValidator<PersonModel> validator;
    private final PersonModel viewModel = new PersonModel();

    public PersonViewController(Validator validator) {
        this.validator = new FXValidator<>(validator, viewModel);
    }

    public Region getView() {
        return new PersonViewBuilder(viewModel, validator, this::onSave).build();
    }

    private void onSave() {
        // In a real application this would delegate to a service.
        System.out.printf("Saved: name=%s, age=%s, gender=%s%n",
                viewModel.getName(), viewModel.getAge(), viewModel.getGender());
    }
}
