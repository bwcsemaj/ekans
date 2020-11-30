package com.backwardscollection.ekans.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value
@EqualsAndHashCode()
public class SnakeBodyPartFX {
    
    //Attributes
    private final IntegerProperty positionProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();
    private final IntegerProperty xProperty = new SimpleIntegerProperty();
    private final IntegerProperty yProperty = new SimpleIntegerProperty();
    
    /**
     * Parameterized Constructor that builds SnakeBodyPartFX
     * @param position : index of where the snake body is located in the SnakeFX
     * @param color : color of the SnakeBodyFX
     * @param x : X/Y coordinate system where SnakeBodyFX x value is this
     * @param y : X/Y coordinate system where SnakeBodyFX y value is this
     */
    public SnakeBodyPartFX(int position, Color color, int x, int y){
        this.positionProperty.set(position);
        this.colorProperty.set(color);
        this.xProperty.set(x);
        this.yProperty.set(y);
    }
}
