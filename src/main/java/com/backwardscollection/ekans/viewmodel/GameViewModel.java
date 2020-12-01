package com.backwardscollection.ekans.viewmodel;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.MoveDirection;
import com.backwardscollection.ekans.utility.EkansUtility;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Accessors(fluent = true)
@Value
@Component
public class GameViewModel implements InitializingBean {
    
    private final IntegerProperty gridXAmountProperty = new SimpleIntegerProperty(30);
    private final IntegerProperty gridYAmountProperty = new SimpleIntegerProperty(30);
    
    
    private final SnakeFX snakeFX = new SnakeFX();
    private final FoodFX foodFX = new FoodFX();
    private final ObjectProperty<GamePhase> phaseProperty = new SimpleObjectProperty<>(GamePhase.MAIN_MENU);
    private final LongProperty startTimeProperty = new SimpleLongProperty(-1);
    private final IntegerProperty pointsProperty = new SimpleIntegerProperty();
    
    private final ObjectProperty<MoveDirection> moveDirectionRequestProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<MoveDirection> lastValidMoveDirectionProperty = new SimpleObjectProperty<>(MoveDirection.RIGHT);
    
    //Set up Time Line
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
            event -> {
                step();
            }
    ), new KeyFrame(Duration.millis(120)));
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Listen for when phase changes
        phaseProperty.addListener((obs, oldValue, newValue) -> {
            switch (newValue) {
                case MAIN_MENU -> {
                
                }
                case PLAY -> {
                    start();
                    log.debug("PLAY");
                }
                case END -> {
                    end();
                }
            }
        });
        
        //Have timeline go indefinitely
        timeline.setCycleCount(Animation.INDEFINITE);
        
    }
    
    public void start() {
        //Initialize Snake
        snakeFX.bodyPartsProperty().clear();
        snakeFX.grow(gridXAmountProperty.get() / 2, gridYAmountProperty.get() / 2);
        
        //Initialize Food
        //#TODO Fix, need to check if body part is on the cord, cycle cords, total xs/ys, return ones below thres, random fischer
        foodFX.xProperty().set(EkansUtility.generateRandomIntInRange(gridXAmountProperty.get(), 0));
        foodFX.yProperty().set(EkansUtility.generateRandomIntInRange(gridYAmountProperty.get(), 0));
        
        timeline.play();
        log.debug("START");
        
    }
    
    public void step() {
        log.debug("STEP");
        //local variables
        var headBodyPartFX = snakeFX.bodyPartsProperty().get(0);
        int headX = headBodyPartFX.xProperty().get();
        int headY = headBodyPartFX.yProperty().get();
        var foodX = foodFX.xProperty().get();
        var foodY = foodFX.yProperty().get();
        
        //Check if game over (if head is on another body part)
        if (gameOver()) {
            phaseProperty.set(GamePhase.END);
            end();
            return;
        }
        
        //Try Update last Valid move Direction
        var previousLastValidMove = lastValidMoveDirectionProperty.get();
        var moveDirectionRequest = moveDirectionRequestProperty.get();
        if (moveDirectionRequest != null && moveDirectionRequest.getOpposite() != lastValidMoveDirectionProperty.get()) {
            lastValidMoveDirectionProperty.set(moveDirectionRequestProperty.get());
        }
        
        //move
        switch (lastValidMoveDirectionProperty.get()) {
            case UP -> {
                headY--;
            }
            case DOWN -> {
                headY++;
            }
            case LEFT -> {
                headX--;
            }
            case RIGHT -> {
                headX++;
            }
        }
        
        //If snake is on food then grow snake at end after move and move food to place snake isn't
        if (headX == foodX && headY == foodY) {
            snakeFX.grow(headX, headY);
        } else {
            snakeFX.move(headX, headY);
        }
        
        
    }
    
    /**
     * Check if the game is considered over
     *
     * @return whether head is out of bounds or head is touching another body part
     */
    private boolean gameOver() {
        var headBodyPartFX = snakeFX.bodyPartsProperty().get(0);
        int headX = headBodyPartFX.xProperty().get();
        int headY = headBodyPartFX.yProperty().get();
        int gridX = gridXAmountProperty.get();
        int gridY = gridYAmountProperty.get();
        return headX > gridX || headX < 0 || headY > gridY || headY < 0 ||
                snakeFX.bodyPartsProperty()
                        .get()
                        .stream()
                        .skip(4) // We don't need to check the head and the next three because it is impossible to touch
                        .filter(snakeBodyPartFX -> snakeBodyPartFX.xProperty().get() == headBodyPartFX.xProperty().get())
                        .filter(snakeBodyPartFX -> snakeBodyPartFX.yProperty().get() == headBodyPartFX.yProperty().get())
                        .count() > 0;
    }
    
    public void end() {
        timeline.stop();
    }
}
