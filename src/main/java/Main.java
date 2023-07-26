

import database.Admin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application{

	private final String windowTitle = "Graph Maker";

    @Override
    public void start(Stage primaryStage) {

        Admin.createNewDatabase();

        primaryStage.setTitle(windowTitle);
        primaryStage.setOnCloseRequest(close -> Platform.exit());
        primaryStage.setHeight(900);
        primaryStage.setWidth(1200);

        final Interslice main = new Interslice(primaryStage);

        main.start();
    }
	
}
