package com.backwardscollection.ekans.viewmodel;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value
@EqualsAndHashCode()
public class FoodFX {
    
    //Attributes
    public static final Color DEFAULT_FOOD_COLOR = Color.GREEN;
    private final IntegerProperty xProperty = new SimpleIntegerProperty();
    private final IntegerProperty yProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(DEFAULT_FOOD_COLOR);
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty(true);
    
}
