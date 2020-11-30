package com.backwardscollection.ekans.viewmodel;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value
@EqualsAndHashCode()
public class SnakeFX {
    
    //Attributes
    private ListProperty<SnakeBodyPartFX> bodyPartsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    
    //Constructor
    
    /**
     * Nullary Constructor
     */
    public SnakeFX() {
    }
    
    public void grow(int x, int y) {
        if (bodyPartsProperty.size() == 0) {
            bodyPartsProperty.add(new SnakeBodyPartFX(bodyPartsProperty.size(), Color.ALICEBLUE, x, y));
        } else {
            bodyPartsProperty.add(new SnakeBodyPartFX(bodyPartsProperty.size(),
                    Color.ALICEBLUE, x, y, bodyPartsProperty.get(bodyPartsProperty.size() - 1)));
        }
       move(x, y);
    }
    
    public void move(int x, int y) {
        var headBodyPartFX = bodyPartsProperty.get(0);
        headBodyPartFX.xProperty().set(x);
        headBodyPartFX.yProperty().set(y);
    }
}
