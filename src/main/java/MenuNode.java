

import graphbits.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javaslang.Tuple3;

// this is the box that encases a level and all it's info and buttons.
public class MenuNode extends HBox {
    final Label title = new Label();
    final Label description = new Label();

    final VBox root = new VBox(10);
    final Button[] buttons;
    final String name;
    final String descript;
    
    final Image menuGraphIm = new Image("file:images/potential11.jpg");

    public MenuNode(Tuple3<Button[], String, String> level, final int index) {
        this.buttons = level._1;
        this.name = level._2;
        this.descript = level._3;

        // this whole initialization is a bit weird right now. I'll clean it up some time.

        final HBox topHBox = new HBox(0);

        title.setText(name);
        title.setStyle("-fx-font-size: 20");
        description.setText(descript);

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) buttons[i].setStyle("-fx-font-size: 18");
        }

        final HBox buttonsHBox = new HBox();
        
        for (int i = 0; i < buttons.length; i++) buttonsHBox.getChildren().add(buttons[i]);
        
        topHBox.setPadding(new Insets(0, 0, 0, 0));
        topHBox.getChildren().addAll(title, buttonsHBox);
        topHBox.setAlignment(Pos.CENTER);

        root.setPadding(new Insets(20));
        Utils.BufferHBox(topHBox, Priority.SOMETIMES, true, false, Integer.MAX_VALUE);

        HBox.setHgrow(topHBox, Priority.ALWAYS);

        root.getChildren().addAll(topHBox, description);

        description.setPrefWidth(Integer.MAX_VALUE);

        HBox.setHgrow(root, Priority.ALWAYS);

        this.getChildren().add(root);
        //this.setStyle("-fx-background-color: " + Utils.hex(Color.BURLYWOOD));
        final BackgroundImage bg = new BackgroundImage(menuGraphIm, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
        		BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        this.setBackground(new Background(bg));
        this.setPrefHeight(100);
    }
}
