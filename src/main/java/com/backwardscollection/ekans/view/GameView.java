package com.backwardscollection.ekans.view;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.MoveDirection;
import com.backwardscollection.ekans.viewmodel.GameViewModel;
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
import org.reactfx.util.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class GameView extends StackPane implements InitializingBean {
    
    private final GameViewModel gameViewModel;
    
    private StackPane contentPane;
    private Label displayLabel;
    private Button logoButton;
    private AnchorPane arenaPane;
    private ListProperty<Node> snakeNodes = new SimpleListProperty<>();
    
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
                bodyPartPane.backgroundProperty().bind(Bindings.createObjectBinding(
                        () -> {
                            return new Background(
                                    new BackgroundFill(snakeBodyPartFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY));
                        }, snakeBodyPartFX.colorProperty()));
                bodyPartPane.layoutXProperty().bind(Bindings.createObjectBinding(()->{
                    return snakeBodyPartFX.xProperty().get() * 10; //fix
                }, snakeBodyPartFX.xProperty(), gameViewModel.gridXAmountProperty(), arenaPane.widthProperty()));
                bodyPartPane.layoutYProperty().bind(Bindings.createObjectBinding(()->{
                    return snakeBodyPartFX.yProperty().get() * 10; //fix
                }, snakeBodyPartFX.yProperty(), gameViewModel.gridYAmountProperty(), arenaPane.heightProperty()));
                return bodyPartPane;
            }).collect(Collectors.toList()));
        }, gameViewModel.snakeFX().bodyPartsProperty()));
        snakeNodes.addListener((obs, oldValue, newValue)->{
            arenaPane.getChildren().setAll(snakeNodes);
        });
        
        //Initialize Display
        displayLabel = new Label();
        displayLabel.getStyleClass().clear();
        displayLabel.graphicProperty().bind(Bindings.createObjectBinding(()->{
            var phase = gameViewModel.phaseProperty().get();
            
            switch(phase){
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
        this.setOnKeyPressed(event->{
            MoveDirection moveDirectionRequested = switch (event.getCode()) {
                case UP -> MoveDirection.UP;
                case DOWN -> MoveDirection.DOWN;
                case LEFT -> MoveDirection.LEFT;
                case RIGHT -> MoveDirection.RIGHT;
                default -> null;
            };
            gameViewModel.moveDirectionRequestProperty().set(moveDirectionRequested);
        });
        
        //Initialize Root/Content Stuff
        contentPane = new StackPane();
        contentPane.getChildren().add(displayLabel);
        this.getChildren().add(contentPane);
        
    }
    
}
