package com.backwardscollection.ekans.viewmodel;

import com.backwardscollection.ekans.config.GamePhase;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;

@Component
public class GameViewModel {

    private final SnakeFX snakeFX = new SnakeFX();
    private final FoodFX foodFX = new FoodFX();
    private final ObjectProperty<GamePhase> phaseProperty = new SimpleObjectProperty<>();
    private final LongProperty startTimeProperty = new SimpleLongProperty();
    private final IntegerProperty pointsProperty = new SimpleIntegerProperty();
}
