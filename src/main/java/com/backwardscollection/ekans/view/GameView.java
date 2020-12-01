package com.backwardscollection.ekans.view;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.MoveDirection;
import com.backwardscollection.ekans.utility.FXUtility;
import com.backwardscollection.ekans.viewmodel.GameViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class GameView extends StackPane implements InitializingBean {
    
    private final GameViewModel gameViewModel;
    
    private StackPane contentPane;
    private Label displayLabel;
    
    
    @Autowired
    public GameView(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Initialize Logo
        initLogoButton();
        
        //Initialize Arena
        initArena();
        
        
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
                    return arenaContentPane;
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
        contentPane.getStylesheets().add(GameView.class.getResource("generic.css").toExternalForm());
    }
    
    private Button logoButton;
    
    private void initLogoButton() {
        logoButton = new Button("LOGO BUTTON");
        logoButton.setOnAction(event -> {
            gameViewModel.phaseProperty().set(GamePhase.PLAY);
        });
    }
    
    private StackPane arenaContentPane;
    private AnchorPane arenaPane;
    private final ListProperty<Node> snakeNodes = new SimpleListProperty<>();
    private Pane foodPane;
    
    private void initArena() {
        arenaContentPane = new StackPane();
        arenaContentPane.setId("arena-content-pane");
        arenaContentPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        arenaContentPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
    
        arenaPane = new AnchorPane();
        arenaContentPane.getChildren().add(arenaPane);
        arenaPane.widthProperty().addListener((obs, oldValue, newValue)->{
            log.debug("ARENA W{} H{} MW{} MH{}", arenaPane.getWidth(), arenaPane.getHeight(), arenaPane.getMaxWidth(), arenaPane.getMaxHeight());
        });
        arenaPane.heightProperty().addListener((obs, oldValue, newValue)->{
            log.debug("ARENA W{} H{} MW{} MH{}", arenaPane.getWidth(), arenaPane.getHeight(), arenaPane.getMaxWidth(), arenaPane.getMaxHeight());
        });
        arenaPane.setId("arena-pane");
        Platform.runLater(()->{
            arenaPane.maxHeightProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.8), this.getScene().getWindow().heightProperty().multiply(.8)));
            arenaPane.maxWidthProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.8), this.getScene().getWindow().heightProperty().multiply(.8)));
            arenaPane.prefWidthProperty().bind(arenaPane.maxWidthProperty());
        });
        arenaPane.setBackground(new Background(new BackgroundFill(Color.MAGENTA, CornerRadii.EMPTY, Insets.EMPTY)));
        snakeNodes.bind(Bindings.createObjectBinding(() -> {
            return FXCollections.observableArrayList(gameViewModel.snakeFX().bodyPartsProperty().stream().map(snakeBodyPartFX -> {
                var bodyPartPane = new Pane();
                bodyPartPane.minWidthProperty().bind(arenaPane.maxWidthProperty().divide(gameViewModel.gridXAmountProperty()));
                bodyPartPane.minHeightProperty().bind(bodyPartPane.minWidthProperty());
                AnchorPane.setLeftAnchor(bodyPartPane, snakeBodyPartFX.xProperty().get() * bodyPartPane.minWidthProperty().get());
                AnchorPane.setTopAnchor(bodyPartPane, snakeBodyPartFX.yProperty().get() * bodyPartPane.minHeightProperty().get());
                bodyPartPane.backgroundProperty().bind(Bindings.createObjectBinding(
                        () -> {
                            return new Background(
                                    new BackgroundFill(snakeBodyPartFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY));
                        }, snakeBodyPartFX.colorProperty()));
                snakeBodyPartFX.xProperty().addListener((obs, oldValue, newValue) -> {
                    AnchorPane.setLeftAnchor(bodyPartPane, newValue.doubleValue() * bodyPartPane.minWidthProperty().get());
                });
                snakeBodyPartFX.yProperty().addListener((obs, oldValue, newValue) -> {
                    AnchorPane.setTopAnchor(bodyPartPane, newValue.doubleValue() * bodyPartPane.minHeightProperty().get());
                });
                this.getScene().widthProperty().addListener((obs,oldValue, newValue)->{
                    AnchorPane.setLeftAnchor(bodyPartPane, snakeBodyPartFX.xProperty().doubleValue() * bodyPartPane.minWidthProperty().get());
                    AnchorPane.setTopAnchor(bodyPartPane, snakeBodyPartFX.xProperty().doubleValue() * bodyPartPane.minHeightProperty().get());
                });
                this.getScene().heightProperty().addListener((obs,oldValue, newValue)->{
                    AnchorPane.setLeftAnchor(bodyPartPane, snakeBodyPartFX.yProperty().doubleValue() * bodyPartPane.minWidthProperty().get());
                    AnchorPane.setTopAnchor(bodyPartPane, snakeBodyPartFX.yProperty().doubleValue() * bodyPartPane.minHeightProperty().get());
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
        foodPane.setId("food-pane");
        foodPane.minWidthProperty().bind(arenaPane.maxWidthProperty().divide(gameViewModel.gridXAmountProperty()));
        foodPane.minHeightProperty().bind(foodPane.minWidthProperty());
        var foodFX = gameViewModel.foodFX();
        foodFX.xProperty().addListener((obs, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(foodPane, newValue.doubleValue() * foodPane.minWidthProperty().get());
        });
        foodFX.yProperty().addListener((obs, oldValue, newValue) -> {
            AnchorPane.setTopAnchor(foodPane, newValue.doubleValue() * foodPane.minHeightProperty().get());
        });
        foodPane.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            return new Background(new BackgroundFill(foodFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY));
        }, foodFX.colorProperty()));
        foodPane.visibleProperty().bind(foodFX.visibleProperty());

        //Add Listeners for changing the Scene Width/Height
        Platform.runLater(()->{
            this.getScene().widthProperty().addListener((obs,oldValue, newValue)->{
                AnchorPane.setLeftAnchor(foodPane, foodFX.xProperty().doubleValue() * foodPane.minWidthProperty().get());
                AnchorPane.setTopAnchor(foodPane, foodFX.yProperty().doubleValue() * foodPane.minHeightProperty().get());
            });
            this.getScene().heightProperty().addListener((obs,oldValue, newValue)->{
                AnchorPane.setLeftAnchor(foodPane, foodFX.xProperty().doubleValue() * foodPane.minWidthProperty().get());
                AnchorPane.setTopAnchor(foodPane, foodFX.yProperty().doubleValue() * foodPane.minHeightProperty().get());
            });
        });
        
    }
    
    
}
