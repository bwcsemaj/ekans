package com.backwardscollection.ekans.viewmodel;

import com.backwardscollection.ekans.utility.FXUtility;
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
    public static final Color[] DEFAULT_COLORS = {
            Color.rgb(255, 240, 255),
            Color.rgb(255, 233, 255),
            Color.rgb(255, 225, 255),
            Color.rgb(251, 218, 255),
            Color.rgb(244, 210, 255),
            Color.rgb(236, 203, 250),
            Color.rgb(229, 195, 242),
            Color.rgb(221, 188, 235),
            Color.rgb(214, 180, 227),
            Color.rgb(206,173,220),
            Color.rgb(199,165,212),
            Color.rgb(195,161,209),
            Color.rgb(175,141,188),
            Color.rgb(154,121,168),
            Color.rgb(134,101,148),
            Color.rgb(114,81,128),
            Color.rgb(94,61,108),
            Color.rgb(74,40,88),
            Color.rgb(54,20,68),
            Color.rgb(34,0,47),
            Color.rgb(13,0,27)
    };
    //Constructor
    
    /**
     * Nullary Constructor
     */
    public SnakeFX() {
    }
    
    public void grow(int x, int y) {
        if (bodyPartsProperty.size() == 0) {
            bodyPartsProperty.add(new SnakeBodyPartFX(bodyPartsProperty.size(), Color.color(1,1,1,1), x, y));
        } else {
            bodyPartsProperty.add(new SnakeBodyPartFX(bodyPartsProperty.size(),
                    DEFAULT_COLORS[bodyPartsProperty.size() % DEFAULT_COLORS.length],
                    x, y, bodyPartsProperty.get(bodyPartsProperty.size() - 1)));
        }
        move(x, y);
    }
    
    public void move(int x, int y) {
        var headBodyPartFX = bodyPartsProperty.get(0);
        headBodyPartFX.xProperty().set(x);
        headBodyPartFX.yProperty().set(y);
    }
}
