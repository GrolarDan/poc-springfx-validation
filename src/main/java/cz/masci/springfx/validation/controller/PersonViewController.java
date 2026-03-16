package cz.masci.springfx.validation.controller;

import cz.masci.springfx.validation.model.PersonModel;
import cz.masci.springfx.validation.view.PersonViewBuilder;
import javafx.scene.layout.Region;
import org.springframework.stereotype.Component;

@Component
public class PersonViewController {
    private final PersonModel viewModel = new PersonModel();

    public Region getView() {
        return new PersonViewBuilder(viewModel, this::onSave).build();
    }

    private void onSave() {
        // In a real application this would delegate to a service.
        System.out.printf("Saved: name=%s, age=%s, gender=%s%n",
                viewModel.getName(), viewModel.getAge(), viewModel.getGender());
    }
}
