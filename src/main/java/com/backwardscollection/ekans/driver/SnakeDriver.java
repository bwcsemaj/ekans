package com.backwardscollection.ekans.driver;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SnakeDriver extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        var label = new Label();
        label.setId("label");
        primaryStage.setScene(new Scene(label, 500, 500));
        primaryStage.show();
        primaryStage.setOnCloseRequest((event) -> System.exit(1));
        primaryStage.centerOnScreen();
        primaryStage.
    }
}
