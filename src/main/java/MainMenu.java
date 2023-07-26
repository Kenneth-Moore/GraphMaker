

import java.util.ArrayList;

import graphbits.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javaslang.Tuple3;

public class MainMenu extends Scene {

    final static StackPane root = new StackPane();

    // right now the styling is done on the background image, this is just for planning
    // purposes. We'll eventually make the background neutral and do all the styling in
    // the code.
    final Image backgroundIm = new Image("file:images/background.jpg");
    final ImageView background = new ImageView(backgroundIm);
    
    final Image menuGraphIm = new Image("file:images/graphmain.png");
    final ImageView menuGraph = new ImageView(menuGraphIm);

    final HBox mainHBox = new HBox(20);

    // controlPane buttons. We can add more functions!
    final Button newBtn;
    
    final Label label = new Label();

    // could do this with a gridpane, but those are frustrating to get the spacing right
    final VBox levelPane0 = new VBox();

    public MainMenu(Button makeNew, final ArrayList<Tuple3<Button[], String, String>> graphInfos) {
        super(root);
        
        this.newBtn = makeNew;
        
        label.setText("Here are your saved Graphs:");
        label.setStyle("-fx-font-size: 22;");

        Utils.mainControlStyleBtn(newBtn, "Make new");

        for (int i = 0; i < graphInfos.size(); i++) {
            final MenuNode grph = new MenuNode(graphInfos.get(i), i);
            levelPane0.getChildren().add(0, grph);
        }

        HBox.setHgrow(levelPane0, Priority.SOMETIMES);
        levelPane0.setSpacing(10);
        levelPane0.setPadding(new Insets(10, 10, 10, 10));
        
        final ScrollPane graphs = new ScrollPane();
        graphs.setContent(levelPane0);
        graphs.setFitToWidth(true);

        final HBox controlPane = new HBox(10);
        controlPane.getChildren().addAll(label, newBtn);
        controlPane.setAlignment(Pos.CENTER_LEFT);
        Utils.BufferHBox(controlPane, Priority.ALWAYS, false, false, 300);
        
        final VBox totallPane = new VBox(20);
        totallPane.getChildren().addAll(controlPane, graphs);
        VBox.setVgrow(graphs, Priority.ALWAYS);
        
        final VBox imagePane = new VBox();
        final VBox imspacer = new VBox();
        menuGraph.setPreserveRatio(true);
        menuGraph.setManaged(false);
        menuGraph.fitHeightProperty().bind(imagePane.heightProperty());
        menuGraph.fitWidthProperty().bind(imagePane.widthProperty());
        imagePane.getChildren().addAll(menuGraph);
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setPrefWidth(600);
        imspacer.getChildren().add(imagePane);
        Utils.PicBuffer(imspacer, 450);
        
        totallPane.setPrefWidth(600);
        
        mainHBox.getChildren().add(totallPane);
        Utils.addGrowingPaneH(mainHBox, Priority.ALWAYS, 2000);
        mainHBox.getChildren().add(imspacer);
        Utils.addGrowingPaneH(mainHBox, Priority.ALWAYS, 2000);

        mainHBox.setPadding(new Insets(20));
        mainHBox.maxWidthProperty().bind(root.widthProperty());
        mainHBox.setAlignment(Pos.CENTER_LEFT);

        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(background, mainHBox);
    }
    
    public void refresh(final ArrayList<Tuple3<Button[], String, String>> graphInfos) {
    	levelPane0.getChildren().clear();
    	for (int i = 0; i < graphInfos.size(); i++) {
            final MenuNode grph = new MenuNode(graphInfos.get(i), i);
            levelPane0.getChildren().add(0, grph);
        }
    }
}
