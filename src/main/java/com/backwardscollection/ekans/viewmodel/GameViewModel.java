package com.backwardscollection.ekans.viewmodel;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.Direction;
import com.backwardscollection.ekans.utility.EkansUtility;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.util.Duration;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.BitSet;

@Slf4j
@Accessors(fluent = true)
@Value
@Component
public class GameViewModel implements InitializingBean {
    
    private static final int GRID_LENGTH = 20;
    private final IntegerProperty gridLengthProperty = new SimpleIntegerProperty(GRID_LENGTH);
    
    
    private final SnakeFX snakeFX = new SnakeFX();
    private final FoodFX foodFX = new FoodFX();
    private final ObjectProperty<GamePhase> phaseProperty = new SimpleObjectProperty<>(GamePhase.MAIN_MENU);
    private final LongProperty startTimeProperty = new SimpleLongProperty(-1);
    private final IntegerProperty pointsProperty = new SimpleIntegerProperty();
    
    private final ListProperty<Direction> directionRequestQueProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Direction> lastValidMoveDirectionProperty = new SimpleObjectProperty<>(Direction.RIGHT);
    
    //Set up Time Line
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
            event -> {
                step();
            }
    ), new KeyFrame(Duration.millis(200)));
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Listen for when phase changes
        phaseProperty.addListener((obs, oldValue, newValue) -> {
            switch (newValue) {
                case MAIN_MENU -> {
                
                }
                case PLAY -> {
                    start();
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
        snakeFX.grow(GRID_LENGTH / 2, GRID_LENGTH / 2);
        
        //Initialize Food
        moveFood();
        
        timeline.play();
    }
    
    /**
     * Move food to a open spot
     * An open spot means a spot inside the grid that snake is not on
     * to determine this spot best way is to convert the grid to a single numeric system
     * y*(gridYSize) + x = cellNumber
     * totalCells = gridYSize*gridXSize
     * to make things really easy we'll just use a BitSet, false = notOpenCell true = openCell
     * than generate a random integer from 0 to cardinal (number of cells left) cycling through BitSet
     * we will assume when new game is started bitset is reset
     */
    private BitSet gridBitSet = new BitSet(GRID_LENGTH * GRID_LENGTH);
    
    public void moveFood() {
        //Reset BitSet to all open
        gridBitSet.set(0, GRID_LENGTH * GRID_LENGTH);
        
        // Cycle through snake body parts and set cells empty
        snakeFX.bodyPartsProperty().forEach(bodyPartFX -> {
            var cellNumber = bodyPartFX.yProperty().get() * GRID_LENGTH + bodyPartFX.xProperty().get();
            gridBitSet.clear(cellNumber);
        });
        
        // Generate random cell number
        var random = EkansUtility.generateRandomIntInRange(gridBitSet.cardinality(), 0);
        int newFoodCellNumber = 0;
        while (random > 0) {
            newFoodCellNumber = gridBitSet.nextSetBit(newFoodCellNumber + 1);
            random--;
        }
        var foodX = newFoodCellNumber % GRID_LENGTH;
        var foodY = newFoodCellNumber / GRID_LENGTH;
        foodFX.xProperty().set(foodX);
        foodFX.yProperty().set(foodY);
    }
    
    
    public void step() {
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
        
        Direction directionRequest = null;
        if (directionRequestQueProperty.size() > 0) {
            directionRequest = directionRequestQueProperty.remove(0);
        }
        if (directionRequest != null && directionRequest.getOpposite() != lastValidMoveDirectionProperty.get()) {
            lastValidMoveDirectionProperty.set(directionRequest);
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
            moveFood();
        } else {
            snakeFX.move(headX, headY);
        }
        
        
    }
    
    /**
     * Check if the game is considered over
     *
     * @return whether head is out of bounds or head is touching another body part or no more spots left for food to go
     */
    private boolean gameOver() {
        var headBodyPartFX = snakeFX.bodyPartsProperty().get(0);
        int headX = headBodyPartFX.xProperty().get();
        int headY = headBodyPartFX.yProperty().get();
        int gridX = GRID_LENGTH;
        int gridY = GRID_LENGTH;
        return headX >= gridX || headX < 0 || headY >= gridY || headY < 0 || gridBitSet.cardinality() == 0 ||
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
    
    public void returnToMenu() {
        phaseProperty.set(GamePhase.MAIN_MENU);
    }
}
