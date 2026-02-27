package cz.masci.springfx.validation;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application class.
 *
 * <p>The actual JavaFX entry point is {@link JavaFxApplication}, which
 * bootstraps this Spring context via {@link org.springframework.boot.builder.SpringApplicationBuilder}.</p>
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        // Launch the JavaFX Application, which in turn starts Spring.
        javafx.application.Application.launch(JavaFxApplication.class, args);
    }
}
