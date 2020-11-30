package com.backwardscollection.ekans.driver;

import com.backwardscollection.ekans.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.backwardscollection"})
@SpringBootApplication
public class SnakeDriver extends Application implements CommandLineRunner {
    
    @Autowired
    private GameView gameView;
    private static Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Platform.runLater(() -> {
            var springContext =
                    new SpringApplicationBuilder(SnakeDriver.class).web(WebApplicationType.NONE).run();
            
        });
    }
    
    @Override
    public void run(String... args) throws Exception {
        primaryStage.setScene(new Scene(gameView, 500, 500));
        primaryStage.show();
        primaryStage.setOnCloseRequest((event) -> System.exit(1));
        primaryStage.centerOnScreen();
    }
}
