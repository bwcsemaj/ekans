package com.backwardscollection.ekans.driver;

import com.backwardscollection.ekans.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
    private static final String TITLE = "ekans";
    private static final int INITIAL_WIDTH = 500;
    private static final int INITIAL_HEIGHT= 500;
    private static Stage stage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Platform.runLater(() -> {
            var springContext =
                    new SpringApplicationBuilder(SnakeDriver.class).web(WebApplicationType.NONE).run();
            
        });
    }
    
    @Override
    public void run(String... args) throws Exception {
        stage.setScene(new Scene(gameView, INITIAL_WIDTH, INITIAL_HEIGHT));
        stage.setTitle(TITLE);
        stage.show();
        stage.setOnCloseRequest((event) -> System.exit(1));
        stage.centerOnScreen();
    }
}
