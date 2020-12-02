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
     * Creates an array of RowConstraints given contstraints
     * @param fill : whether items in row should be fill the space
     * @param percentages : row percentages where index corresponds to row
     * @return an RowConstraints array
     */
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
    
    /**
     * Creates an array of ColumnConstraints given contstraints
     * @param fill : whether items in row should be fill the space
     * @param percentages : column percentages where index corresponds to column
     * @return an ColumnConstraints array
     */
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
    
    /**
     * Makes given nodes able to grow as much as possible inside of a GridPane
     * @param nodes : nodes to grow
     * @return same nodes
     */
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
    
    /**
     * Determines the maximum font size given String and length requirements
     * @param text : String value
     * @param width : width of the area
     * @param height : height of the area
     * @return maximum size of font
     */
    public static double determineFontSize(String text, double width, double height){
        Text textNode = new Text(text);
        textNode.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, textNode.getFont().getSize()));
        // textNode.setFont(currentFont);
        double fontSize = textNode.getFont().getSize();
        double stringWidth = textNode.getLayoutBounds().getWidth();
        
        // Find out how much the font can grow in width.
        double widthRatio = (double) width / (double) stringWidth;
        double newFontSize = (fontSize * widthRatio);
        
        // Instead of subtracting 5 might want to passin the baseline offset also
        // And apply that number to determining the fontSizeToUse
        double fontSizeToUse = Math.min(newFontSize, height - 5);
        if(fontSizeToUse <= 0){
            return 1;
        }
        
        return fontSizeToUse;
    }
}
