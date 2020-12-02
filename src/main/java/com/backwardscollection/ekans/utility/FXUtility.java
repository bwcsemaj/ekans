package com.backwardscollection.ekans.utility;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
    
    public static String FONT_FAMILY = "Monospace";
    
    public static double determineFontSize(String text, double width, double height){
        Text textNode = new Text(text);
        textNode.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, textNode.getFont().getSize()));
        // textNode.setFont(currentFont);
        double fontSize = textNode.getFont().getSize();
        double stringWidth = textNode.getLayoutBounds().getWidth();
        
        // String Variables needed
        String stringText = text;
        if(stringText == null){
            stringText = new String("");
        }
        
        // Find out how much the font can grow in width.
        double widthRatio = (double) width / (double) stringWidth;
        double newFontSize = (fontSize * widthRatio);
        
        // #TODO Instead of subtracting 5 might want to passin the baseline offset also
        // And apply that number to determining the fontSizeToUse
        double fontSizeToUse = Math.min(newFontSize, height - 5);
        if(fontSizeToUse <= 0){
            return 1;
        }
        
        return fontSizeToUse;
    }
}
