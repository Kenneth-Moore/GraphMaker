

import java.util.Random;

import graphbits.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TitleScreen extends Scene {

    final static StackPane root = new StackPane();

    final Button playBtn;

    final Image backgroundIm = new Image("file:images/stars.png");
    final ImageView background = new ImageView(backgroundIm);

    final HBox controlPane = new HBox();
    final Label waterMark = new Label();
    
    
    // new stuff
    final String[] words = { "three", "four" , "able", "acid", "angry", "automatic", "beautiful",
    		"black", "boiling", "bright", "broken", "brown", "cheap", "chemical", "chief", "clean", 
    		"clear", "common", "complex", "conscious",  "deep", "dependent", "early", "elastic",
    		"electric", "equal", "fat", "fertile", "first", "fixed", "flat", "free", "frequent", 
    		"full", "general", "good", "great", "grey", "hanging", "happy", "hard", "healthy",
    		"high", "hollow", "important", "kind", "like", "living", "long", "married", "material",
    		"medical", "military", "natural", "necessary", "new", "normal", "open", "parallel", 
    		"past", "physical", "political", "poor", "possible", "present", "private", "probable",
    		"quick", "quiet", "ready", "red", "regular", "responsible", "right", "round", "same",
    		"second", "separate", "serious", "sharp", "smooth", "sticky", "stiff", "straight", 
    		"strong", "sudden", "sweet", "tall", "thick", "tight", "tired", "true", "violent", 
    		"waiting", "warm", "wide", "wise", "yellow", "young"
    		};

    String userguess = "";
    int guessesremain = 4;
	Random random = new Random();
    int w = random.nextInt(words.length);

    
    public TitleScreen(final Button pressPlay) {
        super(root);
        playBtn = pressPlay;

        
        playBtn.setText("Graph Maker!");

        //TODO better style than this
        playBtn.setStyle(
                "-fx-padding: 10 10 10 10;\n" +
                "-fx-base: #" + Utils.hex(Color.ANTIQUEWHITE) + "; " +
                "-fx-font-size: 2.4em;");

        waterMark.setText("Trademark Flancrest Enterprises 2018");
        waterMark.setStyle("-fx-font-size: 14;"
                + "-fx-background-color: #" + Utils.hex(Color.CADETBLUE));
        waterMark.setPadding(new Insets(5));

        //root.setBackground(new Background(new BackgroundImage(backgroundIm,
        //        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
        //        BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

		
        
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        
        // new stuff
        
       
        
        final VBox controlbox = new VBox(10);
        final HBox buttonshbox = new HBox(10);

       
        
        buttonshbox.getChildren().addAll(playBtn);
        controlbox.getChildren().addAll(buttonshbox);
        controlbox.setAlignment(Pos.CENTER);
        
        controlPane.getChildren().addAll(controlbox);
        controlPane.setAlignment(Pos.CENTER);

        root.getChildren().addAll(background, controlPane, waterMark);
        StackPane.setAlignment(waterMark, Pos.BOTTOM_RIGHT);
    }
}
