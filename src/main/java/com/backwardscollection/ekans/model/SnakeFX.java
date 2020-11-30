package com.backwardscollection.ekans.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
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
    public SnakeFX(){ }
}
