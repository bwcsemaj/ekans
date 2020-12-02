package com.backwardscollection.ekans.utility;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FXUtility {
    
    
    
    /**
     * Make a Node be able to move the "stage"
     *
     * @param stage : stage the node will belong to
     * @param node  : node that will be able to "moveable"
     */
    public static void setMoveControl(Stage stage, Node node) {
        node.setOnDragDetected((event) -> {
            node.startFullDrag();
        });
        node.setOnMouseDragged((event) -> {
            if (event.isPrimaryButtonDown()) {
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
    
    public static RowConstraints[] createRowConstraints(boolean fill, double... percentages){
        RowConstraints[] rowConstraints = new RowConstraints[percentages.length];
        for(int index = 0; percentages.length > index; index++){
            RowConstraints rowC = new RowConstraints();
            rowC.fillHeightProperty().set(fill);
            rowC.setPercentHeight(percentages[index]);
            rowConstraints[index] = rowC;
        }
        return rowConstraints;
    }
    
    public static ColumnConstraints[] createColumnConstraints(boolean fill, double... percentages){
        ColumnConstraints[] colConstraints = new ColumnConstraints[percentages.length];
        for(int index = 0; percentages.length > index; index++){
            ColumnConstraints colC = new ColumnConstraints();
            colC.fillWidthProperty().set(fill);
            colC.setPercentWidth(percentages[index]);
            colConstraints[index] = colC;
        }
        return colConstraints;
    }
    
    public static Node[] maxGrid(Region... nodes){
        for(Region region : nodes){
            region.setMinSize(0, 0);
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        for(Node node : nodes){
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setVgrow(node, Priority.ALWAYS);
            GridPane.setFillHeight(node, true);
            GridPane.setFillWidth(node, true);
        }
        return nodes;
    }
}
