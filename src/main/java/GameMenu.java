

import graphbits.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// the menu on the side of the game screen.
public class GameMenu extends HBox {
    final Button backBtn;
    final Button saveBtn;
    final Button printBtn;
    final Button modBtn;

    final ToggleButton moveBtn;
    final ToggleButton vertexBtn;
    final ToggleButton edgeBtn;
    final ToggleButton selectBtn;
    final ToggleButton textBtn;

    final CheckBox labVerts;
    final CheckBox labEdges;
    final CheckBox directedBtn;
    final CheckBox gridBtn;
    final TextField radiallines;
    final CheckBox snapBtn;
    
    final Button plusBtn;
    final Button minusBtn;

    public GameMenu(final Button back, final Button save, final Button printAdjacency, final Button mod, 
    		final ToggleButton[] modes, final Button plus, final Button minus, final CheckBox verts, final CheckBox edges, 
            final CheckBox directed, final CheckBox grid, final TextField rads, final CheckBox snap) {
        backBtn = back;
        saveBtn = save;
        printBtn = printAdjacency;
        modBtn = mod;

        moveBtn = modes[0];
        vertexBtn = modes[1];
        edgeBtn = modes[2];
        selectBtn = modes[3];
        textBtn = modes[4];
        
        labVerts = verts;
        labEdges = edges;
        gridBtn = grid;
        radiallines = rads;
        snapBtn = snap;
        
        directedBtn = directed;

        plusBtn = plus;
        minusBtn = minus;

        final String[] modenames = {"Move", "Add Vertex", "Add Edge", "Edit Info", "Delete"};
        final String[] tooltips  = {"Keyboard shortcuts: 1 or M, or hold shift", "Keyboard shortcuts: 2 or V", 
        							"Keyboard shortcuts: 3 or E", "Keyboard shortcuts: 4 or C", 
        							"Keyboard shortcuts: 5 or D, or hold backspace"};
        
        for (int i = 0; i < modes.length; i++) Utils.gameStyleBtn(modes[i], modenames[i], tooltips[i]);

        //TODO replace all these texts with helpful images
        backBtn.setText("Back");
        saveBtn.setText("Save");
        
        backBtn.setPrefWidth(55);
        saveBtn.setPrefWidth(55);
        printBtn.setPrefWidth(120);
        modBtn.setPrefWidth(120);

        labVerts.setText("Auto Lbl Vertex");
        labEdges.setText("Auto Lbl Edge");
        gridBtn.setText("Show Grid");
        radiallines.setPrefWidth(42);
        radiallines.setText("12");
        snapBtn.setText("Snap to Grid");
        
        directedBtn.setText("Directed Graph");

        final HBox radBox = new HBox(10);
        radBox.setAlignment(Pos.CENTER);
        radBox.getChildren().addAll(new Label("Grid lines:"), radiallines);
        
        final VBox labelBox = new VBox(10);
        labelBox.getChildren().addAll(labVerts, labEdges, directedBtn, gridBtn, radBox, snapBtn);
        labelBox.setStyle("-fx-background-color: #" + Utils.hex(Color.WHITE));
        labelBox.setPadding(new Insets(10));
        
        final HBox backsave = new HBox(10);
        backsave.setAlignment(Pos.CENTER);
        backsave.getChildren().addAll(backBtn, saveBtn);
        
        Utils.zoomStyleBtn(plusBtn, "+");
        Utils.zoomStyleBtn(minusBtn, "-");

        final Pane vTopSpacer = new Pane();
        vTopSpacer.setStyle("-fx-background-color: #" + Utils.hex(Color.LIGHTBLUE));
        final Pane vBotSpacer = new Pane();
        vBotSpacer.setStyle("-fx-background-color: #" + Utils.hex(Color.LIGHTBLUE));
        VBox.setVgrow(vBotSpacer, Priority.ALWAYS);
        VBox.setVgrow(vTopSpacer, Priority.ALWAYS);

        final VBox buttonsVBox = new VBox(10);
        buttonsVBox.getChildren().addAll(
        		backsave, printBtn, modBtn, moveBtn, vertexBtn, edgeBtn, selectBtn, textBtn, labelBox);
        buttonsVBox.setStyle("-fx-background-color: #" + Utils.hex(Color.DARKBLUE));
        buttonsVBox.setPadding(new Insets(10));
        buttonsVBox.setAlignment(Pos.CENTER);

        HBox.setHgrow(buttonsVBox, Priority.ALWAYS);

        final VBox spacedBtns = new VBox();
        spacedBtns.getChildren().addAll(vTopSpacer, buttonsVBox, vBotSpacer);

        final VBox zoomVBox = new VBox(15);
        zoomVBox.getChildren().addAll(plusBtn, minusBtn);
        zoomVBox.setPadding(new Insets(15));
        zoomVBox.setAlignment(Pos.TOP_LEFT);
        zoomVBox.setPickOnBounds(false);

        this.getChildren().addAll(spacedBtns, zoomVBox);
    }
}
