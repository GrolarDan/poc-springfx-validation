package cz.masci.springfx.validation;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * Spring application event published when the JavaFX primary stage is ready.
 */
public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
