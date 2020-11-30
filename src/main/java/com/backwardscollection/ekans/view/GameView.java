package com.backwardscollection.ekans.view;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.MoveDirection;
import com.backwardscollection.ekans.viewmodel.GameViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.reactfx.util.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GameView extends StackPane implements InitializingBean {
    
    private final GameViewModel gameViewModel;
    
    private StackPane contentPane;
    private Label displayLabel;
    private Button logoButton;
    private AnchorPane arenaPane;
    private ListProperty<Node> snakeNodes = new SimpleListProperty<>();
    private Pane foodPane;
    
    @Autowired
    public GameView(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Initialize LogoButton
        logoButton = new Button("LOGO BUTTON");
        logoButton.setOnAction(event -> {
            gameViewModel.phaseProperty().set(GamePhase.PLAY);
        });
        
        //Initialize Arena
        // #TODO Come back and fix HARD CODED PARTS
        arenaPane = new AnchorPane();
        arenaPane.setPrefHeight(300);//temp
        arenaPane.setPrefWidth(300);//temp
        arenaPane.setBackground(new Background(new BackgroundFill(Color.MAGENTA, CornerRadii.EMPTY, Insets.EMPTY)));
        snakeNodes.bind(Bindings.createObjectBinding(() -> {
            return FXCollections.observableArrayList(gameViewModel.snakeFX().bodyPartsProperty().stream().map(snakeBodyPartFX -> {
                var bodyPartPane = new Pane();
                bodyPartPane.setMinHeight(10);//temp
                bodyPartPane.setMinWidth(10);//temp
                AnchorPane.setLeftAnchor(bodyPartPane, snakeBodyPartFX.xProperty().get() * 10d);
                AnchorPane.setTopAnchor(bodyPartPane, snakeBodyPartFX.yProperty().get() * 10d);
                bodyPartPane.backgroundProperty().bind(Bindings.createObjectBinding(
                        () -> {
                            return new Background(
                                    new BackgroundFill(snakeBodyPartFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY));
                        }, snakeBodyPartFX.colorProperty()));
                snakeBodyPartFX.xProperty().addListener((obs, oldValue, newValue) -> {
                    log.debug("X{}", newValue.doubleValue() * 10d);
                    AnchorPane.setLeftAnchor(bodyPartPane, newValue.doubleValue() * 10d);
                });
                snakeBodyPartFX.yProperty().addListener((obs, oldValue, newValue) -> {
                    log.debug("Y{}", newValue.doubleValue() * 10d);
                    AnchorPane.setTopAnchor(bodyPartPane, newValue.doubleValue() * 10d);
                });
                return bodyPartPane;
            }).collect(Collectors.toList()));
        }, gameViewModel.snakeFX().bodyPartsProperty()));
        snakeNodes.addListener((obs, oldValue, newValue) -> {
            arenaPane.getChildren().clear();
            arenaPane.getChildren().add(foodPane);
            arenaPane.getChildren().addAll(snakeNodes);
        });
        
        //Initialize Food
        foodPane = new Pane();
        foodPane.setMinHeight(10);//temp
        foodPane.setMinWidth(10);//temp
        var foodFX = gameViewModel.foodFX();
        AnchorPane.setLeftAnchor(foodPane, foodFX.xProperty().get() * 10d);
        AnchorPane.setTopAnchor(foodPane, foodFX.yProperty().get() * 10d);
        foodFX.xProperty().addListener((obs, oldValue, newValue) -> {
            log.debug("X{}", newValue.doubleValue() * 10d);
            AnchorPane.setLeftAnchor(foodPane, newValue.doubleValue() * 10d);
        });
        foodFX.yProperty().addListener((obs, oldValue, newValue) -> {
            log.debug("Y{}", newValue.doubleValue() * 10d);
            AnchorPane.setTopAnchor(foodPane, newValue.doubleValue() * 10d);
        });
        foodPane.backgroundProperty().bind(Bindings.createObjectBinding(()->{
            return new Background(new BackgroundFill(foodFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY));
        }, foodFX.colorProperty()));
        foodPane.visibleProperty().bind(foodFX.visibleProperty());
        
        
        //Initialize Display
        displayLabel = new Label();
        displayLabel.getStyleClass().clear();
        displayLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {
            var phase = gameViewModel.phaseProperty().get();
            
            switch (phase) {
                case MAIN_MENU -> {
                    return logoButton;
                }
                case PLAY, END -> {
                    return arenaPane;
                }
            }
            return null;
        }, gameViewModel.phaseProperty()));
        
        //Initialize Key presses
        Platform.runLater(() -> {
            this.getScene().setOnKeyPressed(event -> {
                MoveDirection moveDirectionRequested = switch (event.getCode()) {
                    case UP -> MoveDirection.UP;
                    case DOWN -> MoveDirection.DOWN;
                    case LEFT -> MoveDirection.LEFT;
                    case RIGHT -> MoveDirection.RIGHT;
                    default -> null;
                };
                log.debug("MOVE{}", moveDirectionRequested);
                gameViewModel.moveDirectionRequestProperty().set(moveDirectionRequested);
            });
        });
        
        //Initialize Root/Content Stuff
        contentPane = new StackPane();
        contentPane.getChildren().add(displayLabel);
        this.getChildren().add(contentPane);
        
    }
    
}
