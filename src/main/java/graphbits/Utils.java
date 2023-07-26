package graphbits;


import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Utils {
	
	public static final String[] defaultEdge = 
		{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
		 "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	
	public static final String[] defaultVert = 
		{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
		 "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	public static final String delim = "////";
	
	public static int containsFix(final Vertex v, final ArrayList<Vertex> list) {
		for (int i = 0; i < list.size(); i++) {
			if (v.equalsV(list.get(i))) return i;
		}
		return -1;
	}
	
    public static String hex(final Color color) {
        final long rd = Math.round(color.getRed() * 255);
        final long gr = Math.round(color.getGreen() * 255);
        final long bl = Math.round(color.getBlue() * 255);

        final String hex = String.format("%02x%02x%02x", rd, gr, bl);

        return hex;
    }

    public static void colorButton(final Node button, final Color color, final Color clicked) {
        button.setStyle("-fx-base: #" + hex(color));
        button.addEventHandler(
                MouseEvent.MOUSE_PRESSED, e -> button.setStyle("-fx-base: #" + hex(clicked)));
        button.addEventHandler(
                MouseEvent.MOUSE_RELEASED, e -> button.setStyle("-fx-base: #" + hex(color)));
    }

    public static ImageView renderColor(final Color color, final int wid, final int hei) {
        final WritableImage image = new WritableImage(wid, hei);

        setImageColor(image, color, wid, hei);

        final ImageView imageView = new ImageView(image);

        return imageView;
    }

    private static void setImageColor(final WritableImage image, final Color color, final int wid, final int hei) {
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (int pixelX = 0; pixelX < wid; pixelX += 1) {
            for (int pixelY = 0; pixelY < hei; pixelY += 1) {
                pixelWriter.setColor(pixelX, pixelY, color);
            }
        }
    }

   public static void mainControlStyleBtn(final Button btn, final String text) {
        //TODO make it a cool circular button and such
        btn.setStyle("-fx-background-radius: 5em;"
                + "-fx-font-size: 16;"
                + "-fx-base: #" + hex(Color.ANTIQUEWHITE) ) ;
        btn.setText(text);
    }

    public static void gameStyleBtn(final ToggleButton btn, final String text, final String tooltip) {
        //TODO make it a cool circular button and such
        btn.setStyle("{ -fx-background-radius: 8em;"
                + "-fx-font-size: 14;"
                + "-fx-base: #" + hex(Color.ANTIQUEWHITE) +";}"
        		);
        btn.setPrefHeight(40);
        btn.setPrefWidth(120);

        btn.setText(text);
        btn.setTooltip(new Tooltip(tooltip));
    }

    public static void zoomStyleBtn(final Button btn, final String text) {
        //TODO make it a cool circular button and such
        btn.setStyle("-fx-background-radius: 8em;"
                + "-fx-font-size: 12;"
                + "-fx-base: #" + hex(Color.CADETBLUE) ) ;
        btn.setPrefHeight(30);
        btn.setPrefWidth(30);

        btn.setText(text);
    }

    public static void BufferHBox(final HBox box, final Priority priority,
                                  boolean thickMid, boolean ends, final int max) {
        final ObservableList<Node> kids = box.getChildren();

        final HBox result = new HBox(box.getSpacing());

        if (ends) {
            addGrowingPaneH(result, priority, max);
        }

        while (!kids.isEmpty()) {

            result.getChildren().add(kids.get(0));

            if (!kids.isEmpty() || ends) {
                addGrowingPaneH(result, priority, max);
            }
            if (!kids.isEmpty() && thickMid) addGrowingPaneH(result, priority, max);
        }
        box.getChildren().addAll(result.getChildren());
    }

    public static void addGrowingPaneH(final HBox box, final Priority priority, final int max) {
        final Pane start = new Pane();
        start.setMaxWidth(max);
        HBox.setHgrow(start, priority);
        box.getChildren().add(start);
    }

    public static void BufferVBox(final VBox box, final Priority priority, boolean thickMid) {
        final ObservableList<Node> kids = box.getChildren();

        final VBox result = new VBox(box.getSpacing());

        addGrowingPaneV(result, priority);

        while (!kids.isEmpty()) {

            result.getChildren().add(kids.get(0));

            addGrowingPaneV(result, priority);
            if (!kids.isEmpty() && thickMid) addGrowingPaneV(result, priority);
        }
        box.getChildren().addAll(result.getChildren());
    }

    public static void addGrowingPaneV(final VBox box, final Priority priority) {
        final Pane start = new Pane();
        VBox.setVgrow(start, priority);
        box.getChildren().add(start);
    }
    
    public static void PicBuffer(final VBox box, int coef) {
    	final Node node = box.getChildren().get(0);
    	
    	box.getChildren().clear();
    	addGrowingPaneV(box, Priority.SOMETIMES);
    	box.getChildren().add(node);
	
        final Pane start = new Pane();
        start.setMinHeight(coef);
        VBox.setVgrow(start, Priority.SOMETIMES);
        box.getChildren().add(start);

        addGrowingPaneV(box, Priority.SOMETIMES);
    }
}
