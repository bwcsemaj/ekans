package com.backwardscollection.ekans.viewmodel;

import com.backwardscollection.ekans.config.GamePhase;
import com.backwardscollection.ekans.config.MoveDirection;
import javafx.beans.property.*;
import lombok.Value;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Accessors(fluent = true)
@Value
@Component
public class GameViewModel implements InitializingBean {
    
    private final IntegerProperty gridXAmountProperty = new SimpleIntegerProperty(30);
    private final IntegerProperty gridYAmountProperty = new SimpleIntegerProperty(30);
    
    
    private final SnakeFX snakeFX = new SnakeFX();
    private final FoodFX foodFX = new FoodFX();
    private final ObjectProperty<GamePhase> phaseProperty = new SimpleObjectProperty<>();
    private final LongProperty startTimeProperty = new SimpleLongProperty();
    private final IntegerProperty pointsProperty = new SimpleIntegerProperty();
    
    private final ListProperty<MoveDirection> moveDirectionRequestProperty = new SimpleListProperty();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //Listen for when phase changes
        phaseProperty.addListener((obs, oldValue, newValue)->{
            switch(newValue){
    
                case MAIN_MENU -> {
                
                }
                case PLAY -> {
                }
                case END -> {
                }
            }
        });
        
    }
    
    public void start(){
    
    }
    
    public void step(){
        //If snake is on food then grow snake at end after move and move food to place snake isn't
        
        //move
        
        //try grow if possible
    }
    
    public void end(){
    
    }
}