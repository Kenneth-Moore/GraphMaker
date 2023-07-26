

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javaslang.Tuple;
import javaslang.Tuple2;

public class SaveWindow {
    final Button saveBtn = new Button();
    final TextField title = new TextField();
    final TextArea description = new TextArea();

    final VBox root = new VBox(10);

    private final Stage stage = new Stage();
    private final Scene scene = new Scene(root);

    final String dbName; // TODO change this to be the actual db

    boolean saved = false;

    Optional<Tuple2<String, String>> result = Optional.empty();

    public SaveWindow(final String windowTitle, final String db) {
        this.dbName = db;

        stage.setScene(scene);

        stage.setTitle(windowTitle);
        stage.setOnCloseRequest(event -> stage.close());

        saveBtn.setText("Save");

        saveBtn.setOnAction(event -> {
            saved = true;
            result = Optional.of(Tuple.of(title.getText(), description.getText()));
            stage.close();
        });

        title.setPromptText("Title");
        title.setFocusTraversable(false);
        description.setPromptText("Discription");
        description.setFocusTraversable(false);
        description.setPrefRowCount(2);
        root.getChildren().addAll(title, description, saveBtn);
        root.setPadding(new Insets(10));

        VBox.setVgrow(description, Priority.ALWAYS);

        title.setPromptText("Title");
        description.setPromptText("Discription");
    }

    public Optional<Tuple2<String, String>> saver() {

        stage.showAndWait();

        return result;

//        if (saved) {
//            final Alert alert = new Alert(AlertType.INFORMATION);
//            alert.setHeaderText("Success!");
//            alert.setContentText("Saved to " + dbName);
//            //alert.getDialogPane().setExpandableContent(  );
//            alert.showAndWait();
//        }

    }
}
