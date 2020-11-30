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
    ), new KeyFrame(Duration.millis(16)));
    
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
        
        //Add listener to move direction request to capture last valid request
        moveDirectionRequestProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                lastValidMoveDirectionProperty.set(moveDirectionRequestProperty.get());
            }
        });
        
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
        //Check if game over (if head is on another body part
        
        //If snake is on food then grow snake at end after move and move food to place snake isn't
        
        //move
        var headBodyPartFX = snakeFX.bodyPartsProperty().get(0);
        int x = headBodyPartFX.xProperty().get();
        int y = headBodyPartFX.yProperty().get();
        switch (lastValidMoveDirectionProperty.get()) {
            case UP -> {
                y++;
            }
            case DOWN -> {
                y--;
            }
            case LEFT -> {
                x--;
            }
            case RIGHT -> {
                x++;
            }
        }
        log.debug("X{}", x);
        log.debug("Y{}", y);
        int gridX = gridXAmountProperty.get();
        int gridY = gridYAmountProperty.get();
        if (x > gridX || x < 0 || y > gridY || y < 0) {
            phaseProperty.set(GamePhase.END);
            return;
        }
        
        //try grow if possible
    }
    
    public void end() {
        timeline.stop();
    }
}
