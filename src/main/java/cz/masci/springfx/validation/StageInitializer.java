package cz.masci.springfx.validation;

import cz.masci.springfx.validation.controller.PersonFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        try {
            FXMLLoader loader = new FXMLLoader(
                    PersonFormController.class.getResource("person-form.fxml"));
            // Let Spring supply the controller so it can inject dependencies.
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = event.getStage();
            stage.setTitle("Person Form");
            stage.setScene(new Scene(root, 420, 320));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load person-form.fxml", e);
        }
    }
}
