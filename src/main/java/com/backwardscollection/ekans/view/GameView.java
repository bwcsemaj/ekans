package com.backwardscollection.ekans.view;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.Direction;
import com.backwardscollection.ekans.utility.FXUtility;
import com.backwardscollection.ekans.viewmodel.GameViewModel;
import com.backwardscollection.ekans.viewmodel.SnakeBodyPartFX;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GameView extends StackPane implements InitializingBean {
    
    private final GameViewModel gameViewModel;
    
    private StackPane contentPane;
    private Label displayLabel;
    private BorderPane gameContentPane;
    private Text topText;
    private Text bottomText;
    
    
    @Autowired
    public GameView(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Initialize Logo
        initLogoButton();
        
        //Initialize SCORE/GAME OVER and RETURN
        gameContentPane = new BorderPane();
        var topTextHolder = new StackPane();
        topText = new Text("HELLO");
        topText.setTextAlignment(TextAlignment.CENTER);
        topText.textProperty().bind(Bindings.when(gameViewModel.phaseProperty().isEqualTo(GamePhase.PLAY))
                .then(gameViewModel.snakeFX().bodyPartsProperty().sizeProperty().asString("SCORE: %d"))
                .otherwise(gameViewModel.snakeFX().bodyPartsProperty().sizeProperty().asString("GAME OVER: %d")));
        topText.fontProperty().bind(Bindings.createObjectBinding(() -> {
            if (this.getScene() == null) {
                return Font.font(20);
            }
            var fontSize = FXUtility.determineFontSize(topText.getText(), this.getScene().getWidth() * .4, this.getScene().getHeight() * .4);
            return Font.font(fontSize);
        }, this.sceneProperty(), this.widthProperty(), this.heightProperty()));
        topTextHolder.getChildren().add(topText);
        gameContentPane.setTop(topTextHolder);
        topText.setId("top-text");
        
        var bottomTextHolder = new StackPane();
        bottomTextHolder.visibleProperty().bind(gameViewModel.phaseProperty().isEqualTo(GamePhase.END));
        bottomText = new Text("RETURN");
        bottomText.setOnMouseReleased(event -> {
            gameViewModel.returnToMenu();
        });
        bottomText.setTextAlignment(TextAlignment.CENTER);
        bottomText.setText("RETURN");
        bottomText.fontProperty().bind(topText.fontProperty());
        bottomTextHolder.getChildren().add(bottomText);
        gameContentPane.setBottom(bottomTextHolder);
        bottomText.setId("bottom-text");
        
        //Initialize Arena
        initArena();
        gameContentPane.setCenter(arenaContentPane);
        
        
        //Initialize Display
        displayLabel = new Label();
        displayLabel.getStyleClass().clear();
        displayLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {
            var phase = gameViewModel.phaseProperty().get();
            
            switch (phase) {
                case MAIN_MENU -> {
                    scaleTransition.play();
                    return logoButton;
                }
                case PLAY, END -> {
                    scaleTransition.stop();
                    return gameContentPane;
                }
            }
            return null;
        }, gameViewModel.phaseProperty()));
        
        //Initialize Key presses
        Platform.runLater(() -> {
            this.getScene().setOnKeyPressed(event -> {
                Direction directionRequested = switch (event.getCode()) {
                    case UP -> Direction.UP;
                    case DOWN -> Direction.DOWN;
                    case LEFT -> Direction.LEFT;
                    case RIGHT -> Direction.RIGHT;
                    default -> null;
                };
                log.debug("MOVE{}", directionRequested);
                gameViewModel.directionRequestQueProperty().add(directionRequested);
            });
        });
        
        //Initialize Root/Content Stuff
        contentPane = new StackPane();
        contentPane.getChildren().add(displayLabel);
        this.getChildren().add(contentPane);
        contentPane.getStyleClass().add("content-pane");
        contentPane.getStylesheets().add(GameView.class.getResource("generic.css").toExternalForm());
    }
    
    private GridPane logoContentPane;
    private Button logoButton;
    private ScaleTransition scaleTransition;
    
    private void initLogoButton() {
        //Content Pane
        logoContentPane = new GridPane();
        logoContentPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        logoContentPane.getRowConstraints().addAll(FXUtility.createRowConstraints(true, 25, 25, 25, 25, 25, 25, 25, 25));
        logoContentPane.getColumnConstraints().addAll(FXUtility.createColumnConstraints(true, 25, 25, 25, 25, 25, 25, 25, 25));
        
        //Top
        double rotate = -90;
        var snakeLogoPanes = createSnakeLogoPanes(rotate);
        for (int index = 0; snakeLogoPanes.size() > index; index++) {
            logoContentPane.add(snakeLogoPanes.get(index), index, 0);
        }
        
        //Right
        rotate *= 2;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        for (int index = 0; snakeLogoPanes.size() > index; index++) {
            logoContentPane.add(snakeLogoPanes.get(index), snakeLogoPanes.size(), index);
        }
        
        //Bottom
        rotate *= 2;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        var index = 0;
        for (int column = snakeLogoPanes.size(); column > 0; column--, index++) {
            logoContentPane.add(snakeLogoPanes.get(index), column, snakeLogoPanes.size());
        }
        
        //Left
        rotate *= 2;
        snakeLogoPanes = createSnakeLogoPanes(rotate);
        index = 0;
        for (int row = snakeLogoPanes.size(); row > 0; row--, index++) {
            logoContentPane.add(snakeLogoPanes.get(index), 0, row);
        }
    
        //Start Logo
        logoButton = new Button();
        
        //Food Very middle
        for(int col = 3; 4 >= col; col++){
            for(int row = 3; 4 >= row; row++){
                var foodPane = new Pane();
                FXUtility.maxGrid(foodPane);
                foodPane.setId("food-pane");
                foodPane.backgroundProperty().bind(Bindings.createObjectBinding(()->{
                    Color color;
                    log.debug("P{} H{}", logoButton.isHover(), logoButton.isPressed());
                    if(logoButton.isPressed()){
                        color = Color.PURPLE;
                    }   else if(logoButton.isHover()){
                        color = Color.MAGENTA;
                    }   else{
                        color = Color.YELLOW;
                    }
                    return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
                }, logoButton.hoverProperty(), logoButton.pressedProperty()));
                logoContentPane.add(foodPane, col, row);
            }
        }
        
        scaleTransition = new ScaleTransition(Duration.millis(10000), logoButton);
        scaleTransition.setToX(1.5f);
        scaleTransition.setToY(1.5f);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        logoButton.getStyleClass().clear();
        Platform.runLater(() -> {
            logoButton.prefHeightProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.4), this.getScene().getWindow().heightProperty().multiply(.4)));
            logoButton.prefWidthProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.4), this.getScene().getWindow().heightProperty().multiply(.4)));
        });
        logoButton.setGraphic(logoContentPane);
        logoButton.setId("logo-button");
        logoButton.setOnAction(event -> {
            gameViewModel.phaseProperty().set(GamePhase.PLAY);
        });
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
        snakeTailPane.setRotate(rotate);
        Collections.reverse(snakeLogoPanes);
        return snakeLogoPanes;
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
        arenaPane.setId("arena-pane");
        Platform.runLater(() -> {
            arenaPane.maxHeightProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.6), this.getScene().getWindow().heightProperty().multiply(.6)));
            arenaPane.maxWidthProperty()
                    .bind(Bindings.min(this.getScene().getWindow().widthProperty().multiply(.6), this.getScene().getWindow().heightProperty().multiply(.6)));
            arenaPane.prefWidthProperty().bind(arenaPane.maxWidthProperty());
            arenaPane.minWidthProperty().bind(arenaPane.maxWidthProperty());
            arenaPane.minHeightProperty().bind(arenaPane.maxHeightProperty());
        });
        arenaPane.setBackground(new Background(new BackgroundFill(Color.MAGENTA, CornerRadii.EMPTY, Insets.EMPTY)));
        snakeNodes.bind(Bindings.createObjectBinding(() -> {
            var snakeFX = gameViewModel.snakeFX();
            if (gameViewModel.snakeFX().bodyPartsProperty().size() == 0) {
                return FXCollections.emptyObservableList();
            }
            ObservableList<Node> bodyParts = FXCollections.observableArrayList(gameViewModel.snakeFX().bodyPartsProperty().stream().map(snakeBodyPartFX -> {
                var bodyPartPane = new Pane();
                bodyPartPane.minWidthProperty().bind(arenaPane.maxWidthProperty().divide(gameViewModel.gridLengthProperty()));
                bodyPartPane.minHeightProperty().bind(bodyPartPane.minWidthProperty());
                repositionSnake(bodyPartPane, snakeBodyPartFX);
                bodyPartPane.setBackground(new Background(
                        new BackgroundFill(snakeBodyPartFX.colorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY)));
                snakeBodyPartFX.xProperty().addListener((obs, oldValue, newValue) -> {
                    repositionSnake(bodyPartPane, snakeBodyPartFX);
                    tryRotateSnakeBodyPart(newValue.intValue(), oldValue.intValue(), bodyPartPane, 90d, -90d);
                });
                snakeBodyPartFX.yProperty().addListener((obs, oldValue, newValue) -> {
                    repositionSnake(bodyPartPane, snakeBodyPartFX);
                    tryRotateSnakeBodyPart(newValue.intValue(), oldValue.intValue(), bodyPartPane, 180, 0);
                });
                this.getScene().widthProperty().addListener((obs, oldValue, newValue) -> {
                    repositionSnake(bodyPartPane, snakeBodyPartFX);
                });
                this.getScene().heightProperty().addListener((obs, oldValue, newValue) -> {
                    repositionSnake(bodyPartPane, snakeBodyPartFX);
                });
                return bodyPartPane;
            }).collect(Collectors.toList()));
            
            if (bodyParts.size() > 0) {
                bodyParts.get(0).setId("snake-head-pane");
            }
            if (bodyParts.size() > 1) {
                bodyParts.get(bodyParts.size() - 1).setId("snake-tail-pane");
            }
            
            return bodyParts;
        }, gameViewModel.snakeFX().bodyPartsProperty()));
        snakeNodes.addListener((obs, oldValue, newValue) -> {
            arenaPane.getChildren().clear();
            arenaPane.getChildren().add(foodPane);
            arenaPane.getChildren().addAll(snakeNodes);
        });
        
        //Initialize Food
        foodPane = new Pane();
        foodPane.setId("food-pane");
        foodPane.minWidthProperty().bind(arenaPane.maxWidthProperty().divide(gameViewModel.gridLengthProperty()));
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
        Platform.runLater(() -> {
            this.getScene().widthProperty().addListener((obs, oldValue, newValue) -> {
                AnchorPane.setLeftAnchor(foodPane, foodFX.xProperty().doubleValue() * foodPane.minWidthProperty().get());
                AnchorPane.setTopAnchor(foodPane, foodFX.yProperty().doubleValue() * foodPane.minHeightProperty().get());
            });
            this.getScene().heightProperty().addListener((obs, oldValue, newValue) -> {
                AnchorPane.setLeftAnchor(foodPane, foodFX.xProperty().doubleValue() * foodPane.minWidthProperty().get());
                AnchorPane.setTopAnchor(foodPane, foodFX.yProperty().doubleValue() * foodPane.minHeightProperty().get());
            });
        });
        
    }
    
    private void repositionSnake(Pane bodyPartPane, SnakeBodyPartFX snakeBodyPartFX) {
        double posNewX = snakeBodyPartFX.xProperty().get() * bodyPartPane.minWidthProperty().get();
        var snakeSideLength = bodyPartPane.getWidth();
        if (posNewX < 0) {
            AnchorPane.setLeftAnchor(bodyPartPane, 0d);
        } else if (posNewX + snakeSideLength > arenaPane.widthProperty().get()) {
            AnchorPane.setLeftAnchor(bodyPartPane, arenaPane.widthProperty().get() - snakeSideLength);
        } else {
            AnchorPane.setLeftAnchor(bodyPartPane, posNewX);
        }
        double posNewY = snakeBodyPartFX.yProperty().get() * bodyPartPane.minWidthProperty().get();
        if (posNewY < 0) {
            AnchorPane.setTopAnchor(bodyPartPane, 0d);
        } else if (posNewY + snakeSideLength > arenaPane.heightProperty().get()) {
            AnchorPane.setTopAnchor(bodyPartPane, arenaPane.heightProperty().get() - snakeSideLength);
        } else {
            AnchorPane.setTopAnchor(bodyPartPane, posNewY);
        }
    }
    
    //Up Right = positive rotate
    //Left Down = negative rotate
    private void tryRotateSnakeBodyPart(int newValue, int oldValue, Pane bodyPartPane, double positiveRotate, double negativeRotate) {
        if (newValue == oldValue) {
            return;
        } else if (newValue > oldValue) {
            //Going right
            if ("snake-head-pane".equals(bodyPartPane.idProperty().get())) {
                bodyPartPane.setRotate(positiveRotate);
            } else if ("snake-tail-pane".equals(bodyPartPane.idProperty().get())) {
                bodyPartPane.setRotate(negativeRotate);
            }
            return;
        }
        //Going left
        if ("snake-head-pane".equals(bodyPartPane.idProperty().get())) {
            bodyPartPane.setRotate(negativeRotate);
        } else if ("snake-tail-pane".equals(bodyPartPane.idProperty().get())) {
            bodyPartPane.setRotate(positiveRotate);
        }
    }
}
