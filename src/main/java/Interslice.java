

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import database.Admin;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;

public class Interslice {

	final Stage primaryStage;
	
	// this is the button on the title screen
	final Button initBtn = new Button();
	
	// this is the make new graph button in the main menu
	final Button addGraph = new Button();
	
	// these are in the gameScene
	final Button backFromGameBtn = new Button();
	final Button saveGraphBtn = new Button();
	
	// this is the playBtn, xBtn, title, and descript for 
    ArrayList<Tuple3<Button[], String, String>> graphs;
	
	final GameScreen gameScene;
	final MainMenu menuScene;
	final TitleScreen titleScene;
	
    public Interslice(final Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        graphs = setUpInfo();
        
        this.titleScene = new TitleScreen(initBtn);
        this.menuScene = new MainMenu(addGraph, graphs);
        this.gameScene = new GameScreen(backFromGameBtn, saveGraphBtn);
        
        addGraph.setOnAction(event -> {
        	gameScene.setGraphData("", "");
        	gameScene.renderScreen();
        	primaryStage.setScene(gameScene);
        });
        
        backFromGameBtn.setOnAction(event -> primaryStage.setScene(menuScene));
        
        initBtn.setOnAction(event -> primaryStage.setScene(menuScene));
        
        saveGraphBtn.setOnAction(event -> {
            final SaveWindow save = new SaveWindow("Save Graph", Admin.dbGraphs);
            final Optional<Tuple2<String, String>> info = save.saver();
            if (info.isPresent()) {
                insertGraph(info.get()._1, info.get()._2, gameScene.generateGraphData());
            }
        });
        
        primaryStage.setScene(titleScene);
    }
    
    public void start() {
        primaryStage.show();
    }
    
    // this takes the info about your user made levels from the database.
    private ArrayList<Tuple3<Button[], String, String>> setUpInfo() {
        final ResultSet results = Admin.fetch();

        final ArrayList<Tuple3<Button[], String, String>> resultArray = new ArrayList<>();

        try {
            while (results.next()) {
                final String title = results.getString("title");
                final String description = results.getString("description");
                final String graphData = results.getString("graph");
                final long date = results.getLong("date");
                final Button playBtn = new Button("Play");
                final Button exBtn = new Button("X");

                final Button[] buttons = {playBtn, exBtn};

                playBtn.setOnAction(Event -> {
                    gameScene.setGraphData(title, graphData);
                    backFromGameBtn.setOnAction(back -> primaryStage.setScene(menuScene));
                    primaryStage.setScene(gameScene);
                });
                exBtn.setOnAction(event -> {
                    Admin.deleteDes(date);
                    graphs = setUpInfo();
                    menuScene.refresh(graphs);
                });

                resultArray.add(Tuple.of(buttons, title, description));
            }
        } catch (SQLException e) {
            System.out.println("setuplevelinfo: " + e.getMessage());
        }

        return resultArray;
    }
    
    public void insertGraph(String name, String description, String data) {
        Admin.insertGraph(name, description, data);

        graphs = setUpInfo();

        menuScene.refresh(graphs);
    }
    
}
