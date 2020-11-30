package com.backwardscollection.ekans.utility;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FXUtility {
    
    /**
     * Make a Node be able to move the "stage"
     * @param stage : stage the node will belong to
     * @param node : node that will be able to "moveable"
     */
    public static void setMoveControl(Stage stage, Node node){
        node.setOnDragDetected((event) -> {
            node.startFullDrag();
        });
        node.setOnMouseDragged((event) -> {
            if(event.isPrimaryButtonDown()){
                // Local Variables
                Rectangle2D screen = Screen.getScreensForRectangle(event.getScreenX(), event.getScreenY(), 1, 1)
                        .get(0).getVisualBounds();
                // If the mouse is a certain distance a way and it is snapped, unsnap and make size previous
                int x = (int) stage.getX();
                stage.setX(event.getScreenX() - stage.getWidth() / 2);
                stage.setY(event.getScreenY() - node.getBoundsInParent().getHeight() / 2);
            }
        });
    }
    
}
