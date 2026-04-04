package cz.masci.springfx.validation;

import cz.masci.springfx.validation.controller.PersonViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listens for {@link StageReadyEvent} and sets up the primary stage with the
 * person-form scene.
 */
@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        var personViewController = applicationContext.getBean(PersonViewController.class);
        var root = personViewController.getView();

        Stage stage = event.getStage();
        stage.setTitle("Person Form");
        stage.setScene(new Scene(root, 420, 320));
        stage.setResizable(false);
        stage.show();
    }
}
