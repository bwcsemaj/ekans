package com.backwardscollection.ekans.driver;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.utility.FXUtility;
import com.backwardscollection.ekans.view.GameView;
import com.backwardscollection.ekans.viewmodel.GameViewModel;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class SnakeLogoDriver extends Application {
    
    private static int WIDTH_INDEX = 0;
    private static int HEIGHT_INDEX = 1;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Platform.runLater(() -> {
            var rootContentPane = new GridPane();
            rootContentPane.getStylesheets().add(GameView.class.getResource("generic.css").toExternalForm());
            
            double[][] iconSizes = {{16, 16}, {32, 32}, {64, 64}, {128, 128}};
            var iconContentPane = new HBox();
            iconContentPane.setSpacing(5);
            FXUtility.maxGrid(iconContentPane);
            for (int index = 0; iconSizes.length > index; index++) {
                var iconSize = iconSizes[index];
                var width = iconSize[WIDTH_INDEX];
                var height = iconSize[HEIGHT_INDEX];
                var iconNode = createButton(stage);
                iconNode.setMaxSize(width, height);
                iconNode.setPrefSize(width, height);
                iconContentPane.getChildren().add(iconNode);
                Platform.runLater(() -> {
                    FXUtility.saveAsPng(iconNode, "ekans_icon_" + (int) width + "x" + (int) height + ".png");
                });
            }
            
            rootContentPane.add(iconContentPane, 1, 1);
            var scene = new Scene(rootContentPane, 500, 500);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(event -> System.exit(0));
        });
        
        
    }
    
    private Button createButton(Stage stage) {
        //Content Pane
        var logoContentPane = new GridPane();
        logoContentPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        logoContentPane.getRowConstraints().addAll(FXUtility.createRowConstraints(true, 25, 25, 25, 25, 25, 25, 25, 25));
        logoContentPane.getColumnConstraints().addAll(FXUtility.createColumnConstraints(true, 25, 25, 25, 25, 25, 25, 25, 25));
        
        //Top
        double rotate = 90;
        var snakeLogoPanes = createSnakeLogoPanes(rotate);
        for (int index = 0; snakeLogoPanes.size() > index; index++) {
            logoContentPane.add(snakeLogoPanes.get(index), index, 0);
        }
        
        //Right
        rotate = 180;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        for (int index = 0; snakeLogoPanes.size() > index; index++) {
            logoContentPane.add(snakeLogoPanes.get(index), snakeLogoPanes.size(), index);
        }
        
        //Bottom
        rotate = 270;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        var index = 0;
        for (int column = snakeLogoPanes.size(); column > 0; column--, index++) {
            logoContentPane.add(snakeLogoPanes.get(index), column, snakeLogoPanes.size());
        }
        
        //Left
        rotate = 0;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        index = 0;
        for (int row = snakeLogoPanes.size(); row > 0; row--, index++) {
            logoContentPane.add(snakeLogoPanes.get(index), 0, row);
        }
        
        //Start Logo
        var logoButton = new Button();
        
        //Food Very middle
        for (int col = 3; 4 >= col; col++) {
            for (int row = 3; 4 >= row; row++) {
                var foodPane = new Pane();
                FXUtility.maxGrid(foodPane);
                foodPane.setId("food-pane");
                foodPane.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
                    Color color;
                    log.debug("P{} H{}", logoButton.isHover(), logoButton.isPressed());
                    if (logoButton.isPressed()) {
                        color = Color.PURPLE;
                    } else if (logoButton.isHover()) {
                        color = Color.MAGENTA;
                    } else {
                        color = Color.YELLOW;
                    }
                    return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
                }, logoButton.hoverProperty(), logoButton.pressedProperty()));
                logoContentPane.add(foodPane, col, row);
            }
        }
        
        logoButton.getStyleClass().clear();
        logoButton.setGraphic(logoContentPane);
        return logoButton;
    }
    
    private List<Pane> createSnakeLogoPanes(double rotate) {
        var snakeLogoPanes = new ArrayList<Pane>();
        for (int index = 0; 7 > index; index++) {
            var snakePane = new Pane();
            FXUtility.maxGrid(snakePane);
            snakeLogoPanes.add(snakePane);
            snakePane.setId(String.format("snake-logo-%d", index));
        }
        snakeLogoPanes.get(0).setId("snake-head-pane");
        var snakeTailPane = snakeLogoPanes.get(snakeLogoPanes.size() - 1);
        snakeTailPane.setId("snake-tail-pane");
        snakeLogoPanes.get(0).setRotate(rotate);
        Collections.reverse(snakeLogoPanes);
        return snakeLogoPanes;
    }
}
