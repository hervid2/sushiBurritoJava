package com.restaurante.app;

import com.restaurante.app.views.authentication.LoginView;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 *
 * <p>The Sushi Burrito desktop app runs inside a Spring {@code ApplicationContext} that provides
 * dependency injection and lifecycle management, while Swing remains the presentation layer. No web
 * server is started ({@link WebApplicationType#NONE}); the context is booted first and, once ready,
 * the Swing {@link LoginView} is shown on the Event Dispatch Thread.
 */
@SpringBootApplication
public class SushiBurritoApplication {

    /**
     * Boots the Spring context as a non-web, non-headless application so AWT/Swing can run.
     *
     * @param args command line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(SushiBurritoApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);
    }

    /**
     * Launches the Swing UI once the context has fully started. Running on the Event Dispatch Thread
     * keeps every Swing interaction on the correct thread.
     *
     * @return a runner that displays the login window
     */
    @Bean
    CommandLineRunner startUi() {
        return args -> SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
