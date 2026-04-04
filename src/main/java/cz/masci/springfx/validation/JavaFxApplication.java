package cz.masci.springfx.validation;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Entry point for the JavaFX application.
 *
 * <p>Bootstraps the Spring context in {@link #init()} so that all Spring beans
 * are available before the primary stage is shown.</p>
 */
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run(getParameters().getRaw().toArray(new String[0]));

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(false))
                .setDeploy(true)
                .setDebug(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
    }

    @Override
    public void start(Stage primaryStage) {
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
