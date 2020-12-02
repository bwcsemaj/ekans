package com.backwardscollection.ekans.driver;

import com.backwardscollection.ekans.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan({"com.backwardscollection"})
@SpringBootApplication
public class SnakeDriver extends Application implements CommandLineRunner {
    
    @Autowired
    private GameView gameView;
    private static final String TITLE = "ekans";
    private static final int INITIAL_WIDTH = 500;
    private static final int INITIAL_HEIGHT = 500;
    private static Stage stage;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        log.info("STARTING APPLICATION");
        this.stage = stage;
        Platform.runLater(() -> {
            var springContext =
                    new SpringApplicationBuilder(SnakeDriver.class).web(WebApplicationType.NONE).run();
            
        });
    }
    
    @Override
    public void run(String... args) throws Exception {
        stage.setScene(new Scene(gameView, INITIAL_WIDTH, INITIAL_HEIGHT));
        stage.setTitle(TITLE);
        stage.getIcons().addAll(FXCollections.observableArrayList(new Image(SnakeDriver.class.getResource("/ekans_icon_128x128.png").toExternalForm()),
                new Image(SnakeDriver.class.getResource("/ekans_icon_16x16.png").toExternalForm()),
                new Image(SnakeDriver.class.getResource("/ekans_icon_32x32.png").toExternalForm()),
                new Image(SnakeDriver.class.getResource("/ekans_icon_64x64.png").toExternalForm())));
        stage.centerOnScreen();
        stage.setOnCloseRequest((event) -> System.exit(1));
        log.info("SHOWING STAGE");
        stage.show();
    }
}
