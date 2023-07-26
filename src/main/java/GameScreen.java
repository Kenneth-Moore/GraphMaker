

import java.util.ArrayList;
import java.util.Optional;

import graphbits.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

// this is where you actually play the game.
public class GameScreen extends Scene {

    // global mutable state:
    private static String title;
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<GraphBit> selected = new ArrayList<>();

    final static StackPane root = new StackPane();
    final static Pane objs = new Pane();
    final Pane labels = new Pane();
    final Pane manipulators = new Pane();
    
    final HBox clickArea = new HBox();
    final StackPane clickStack = new StackPane();

    final PixRadMap map;

    final Image backgroundIm = new Image("file:images/background.jpg");
    final ImageView background = new ImageView(backgroundIm);

    final Button backBtn;
    final Button saveBtn;
    final Button printAdjacency;
    final Button modifyBtn;

    final CheckBox labVerts = new CheckBox();
    final CheckBox labEdges = new CheckBox();
    final CheckBox direcBtn = new CheckBox();
    final CheckBox gridBtn = new CheckBox();
    final CheckBox snapBtn = new CheckBox();
    final TextField radiallines = new TextField();
    
    final Button plusBtn = new Button();
    final Button minuBtn = new Button();

    final Label levelLable = new Label();

    final ToggleGroup controlGroup = new ToggleGroup();
    
    SimpleBooleanProperty deleteProperty = new SimpleBooleanProperty();

    public GameScreen(final Button back, final Button save) {
        super(root);
        this.backBtn = back;
        this.saveBtn = save;

        backBtn.setText("Back");

        saveBtn.setText("Save");
        
        printAdjacency = new Button();
        printAdjacency.setText("Print Adj. Matrix");
        printAdjacency.setOnAction(even -> { System.out.println(generateAdjacency()); });
        
        modifyBtn = new Button();
        modifyBtn.setText("Combine Graph");
        
        levelLable.setPadding(new Insets(0, 50, 44, 0));
        levelLable.setStyle("-fx-font-size: 24");
        levelLable.setTextFill(Color.ALICEBLUE);
        
        // the modes are the things that can happen when you click on the screen, like panning or drawing a stick
        // it is: PAN - VERTEX - EDGE - EDIT - ???
        final ToggleButton[] modes = 
        	{new ToggleButton(), new ToggleButton(), new ToggleButton(), new ToggleButton(), new ToggleButton()};
        for (int i = 0; i < modes.length; i++) {
            // here we set the toggle group, and then make sure the buttons stay selected if you click the same
            // button twice.
            final int finI = i;
            modes[i].setToggleGroup(controlGroup);
            modes[i].setOnAction(click -> {
                if(!modes[finI].isSelected()) modes[finI].setSelected(true);
                renderScreen();
            });
        }
        modes[0].setSelected(true);

        final GameMenu controls = new GameMenu(back, save, printAdjacency, modifyBtn, modes, 
        		plusBtn, minuBtn, labVerts, labEdges, direcBtn, gridBtn, radiallines, snapBtn);

        map = new PixRadMap(Math.max((int) clickArea.getWidth(), 1), Math.max((int) clickArea.getHeight(), 1));
        
        clickArea.setOnMousePressed(click -> {
            final double initX = map.toPix(map.transX) + click.getX();
            final double initY = map.toPix(map.transY) + click.getY();

            if ((modes[0].isSelected() && !(click.getButton() == MouseButton.SECONDARY) && !deleteProperty.get()) || click.isShiftDown()) {
                // drag shouldn't have this effect unless we have pressed while on the move button
                // so each time we pan, we define and then reset the effect of clicking and dragging
            	final Optional<Vertex> optVert = getClosestVert(map.radianX(click.getX()), map.radianY(click.getY()));
            	final Optional<Edge> optEdge = getClosestEdge(click.getX(), click.getY());

            	if (optVert.isPresent()) {
            		final Vertex vert = optVert.get();
            		clickArea.setOnMouseDragged(drag -> {
            			double newx = map.radianX(drag.getX());
            			double newy = map.radianY(drag.getY());
            			if (snapBtn.isSelected()) {
            				final Optional<String> snapstropt;
            				if      (gridBtn.isSelected()) snapstropt = nearestgridpt(newx, newy);
            				else if (gridBtn.isIndeterminate()) snapstropt = nearestcircgridpt(newx, newy);
            				else snapstropt = Optional.empty();
            				
            				if (snapstropt.isPresent()) {
            					final String[] snapstr = snapstropt.get().split(" ");
            					newx = Double.valueOf(snapstr[0]);
            					newy = Double.valueOf(snapstr[1]);
            				}
            			}
	                    vert.move(newx, newy);
	                    renderScreen();
	                });
	                clickArea.setOnMouseReleased(drop -> {
            			double newx = map.radianX(drop.getX());
            			double newy = map.radianY(drop.getY());
            			if (snapBtn.isSelected()) {
            				final Optional<String> snapstropt;
            				if      (gridBtn.isSelected()) snapstropt = nearestgridpt(newx, newy);
            				else if (gridBtn.isIndeterminate()) snapstropt = nearestcircgridpt(newx, newy);
            				else snapstropt = Optional.empty();
            				
            				if (snapstropt.isPresent()) {
            					final String[] snapstr = snapstropt.get().split(" ");
            					newx = Double.valueOf(snapstr[0]);
            					newy = Double.valueOf(snapstr[1]);
            				}
            			}
	                    vert.move(newx, newy);
	                    clickArea.setOnMouseDragged(drag2 -> {});
	                    clickArea.setOnMouseReleased(drop2 -> {});
	                    renderScreen();
	                });
            	} else if (optEdge.isPresent()) {
            		final Edge edge = optEdge.get();
            		renderScreen();
            		makeManipulaters(edge);
            		
            	} else {
	                basicmovefunct(initX, initY);
            	}
            } else if (modes[1].isSelected() && !(click.getButton() == MouseButton.SECONDARY) && !deleteProperty.get()) {
            	clickVertFunct(click);
            } else if (modes[2].isSelected() && !(click.getButton() == MouseButton.SECONDARY) && !deleteProperty.get()) {
            	clickSticFunct(click, initX, initY);
            } else if ((modes[3].isSelected() && !deleteProperty.get()) || (click.getButton() == MouseButton.SECONDARY)) {
            	final Optional<Vertex> optVert = getClosestVert(map.radianX(click.getX()), map.radianY(click.getY()));
            	final Optional<Edge> optEdge = getClosestEdge(click.getX(), click.getY());
            	
            	if (optVert.isPresent()) {
            		final Vertex vert = optVert.get();
            		final EditBox boc = new EditBox(vert, Optional.empty(), Optional.of(vert));
            		renderScreen();

            		manipulators.getChildren().add(boc);
            		
            	} else if (optEdge.isPresent()) {
            		final Edge edge = optEdge.get();
            		final EditBox boc = new EditBox(edge, Optional.of(edge), Optional.empty());
            		renderScreen();
            		
            		manipulators.getChildren().add(boc);
            		
            	} else {
            		basicmovefunct(initX, initY);
            	}
            	
            } else if ((modes[4].isSelected() && !(click.getButton() == MouseButton.SECONDARY)) || deleteProperty.get()) {
            	final Optional<Vertex> optVert = getClosestVert(map.radianX(click.getX()), map.radianY(click.getY()));
            	final Optional<Edge> optEdge = getClosestEdge(click.getX(), click.getY());
            	
            	if (optVert.isPresent()) {
            		final Vertex todel = optVert.get();
            		vertices.remove(todel);
            		while (!todel.getKids().isEmpty()) {
            			final Edge todel2 = todel.getKids().get(0);
                		final Vertex v1 = todel2.getv1();
                		final Vertex v2 = todel2.getv2();
                		v1.evictChild(todel2);
                		v2.evictChild(todel2);
                		edges.remove(todel2);
            		}
            		renderScreen();
            	} else if (optEdge.isPresent()) {
            		final Edge todel = optEdge.get();
            		final Vertex v1 = todel.getv1();
            		final Vertex v2 = todel.getv2();
            		edges.remove(todel);
            		v1.evictChild(todel);
            		v2.evictChild(todel);
            		repair(v1, v2);
            		renderScreen();
            	}
            	else {
            		basicmovefunct(initX, initY);
            	}
            	
            } else {
                // death
            }
        });
        
        clickArea.setOnScroll(event -> {
        	map.scaleBy((300.0 + event.getDeltaY())/300.0);
            renderScreen();
        });
        
        plusBtn.setOnAction(even -> {
            map.scaleBy(1.5);
            renderScreen();
        });
        minuBtn.setOnAction(even -> {
            map.scaleBy(1/1.5);
            renderScreen();
        });
        
        direcBtn.setOnAction(even -> {
        	renderScreen();
        });
        
        gridBtn.setOnAction(event -> {
        	renderScreen();
        });
        gridBtn.setAllowIndeterminate(true);
        
        radiallines.setOnAction(event -> {
        	renderScreen();
        });
        
        //background.fitWidthProperty().bind(root.widthProperty());
        //background.fitHeightProperty().bind(root.heightProperty());

        clickArea.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                map.resizeX(newValue.intValue());
                renderScreen();
            }
        });
        clickArea.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                map.resizeY(newValue.intValue());
                renderScreen();
            }
        });
        clickStack.getChildren().addAll(objs, clickArea, labels);
        
        manipulators.setPickOnBounds(false);
        labels.setPickOnBounds(false);

        controls.setPickOnBounds(false);

        root.getChildren().addAll(levelLable, clickStack, manipulators, controls);
        StackPane.setAlignment(levelLable, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(controls, Pos.CENTER_LEFT);
        
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            		deleteProperty.setValue(true);
            	}
            	else if (event.getCode() == KeyCode.M || event.getCode() == KeyCode.DIGIT1) {
            		modes[0].setSelected(true);
            	}
            	else if (event.getCode() == KeyCode.V || event.getCode() == KeyCode.DIGIT2) {
            		modes[1].setSelected(true);
            	}
            	else if (event.getCode() == KeyCode.E || event.getCode() == KeyCode.DIGIT3) {
            		modes[2].setSelected(true);
            	}
            	else if (event.getCode() == KeyCode.C || event.getCode() == KeyCode.DIGIT4) {
            		modes[3].setSelected(true);
            	}
            	else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.DIGIT5) {
            		modes[4].setSelected(true);
            	}
            }
        });

        this.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	deleteProperty.setValue(false);
            }
        });
    }
    
    public void basicmovefunct(double initX, double initY) {
    	clickArea.setOnMouseDragged(drag -> {
            map.translateX(map.toRad(initX - drag.getX()));
            map.translateY(map.toRad(initY - drag.getY()));
            renderScreen();
        });
        
        clickArea.setOnMouseReleased(drop -> {
            clickArea.setOnMouseDragged(drag2 -> {});
            clickArea.setOnMouseReleased(drop2 -> {});
        });
    	renderScreen();
    }

    // this redraws the screen. It's also used to refresh things since it clears all lists.
    public void renderScreen() {
        objs.getChildren().clear();
        labels.getChildren().clear();
        manipulators.getChildren().clear();
        selected.clear();

        for (Edge edge : edges) {
        	drawLine(edge);
        	if (direcBtn.isSelected()) {
                objs.getChildren().add(0, edge.directionVis(map));
        	}
        }
        for (Vertex vert : vertices) drawVert(vert);
       
        if (gridBtn.isSelected()) drawGrid();
        else if (gridBtn.isIndeterminate()) drawCircleGrid();
    }

    // redraws the screen, but doesn't clear manipulators. This is good for when you are editing.
    private void renderSafe() {
        objs.getChildren().clear();
        labels.getChildren().clear();

        for (Edge edge : edges) {
        	drawLine(edge);
        	if (direcBtn.isSelected()) {
                objs.getChildren().add(0, edge.directionVis(map));
        	}
        }
        for (Vertex vert : vertices) drawVert(vert);
        
        if (gridBtn.isSelected()) drawGrid();
        else if (gridBtn.isIndeterminate()) drawCircleGrid();
    }
    
    // this sets up the control points for the bezier edge curves
    private void makeManipulaters(final Edge edge) {
    	final CubicCurve curv = drawSelect(edge);
		final Circle gui1 = new Circle(curv.getControlX1(), curv.getControlY1(), 12);
		final Circle gui2 = new Circle(curv.getControlX2(), curv.getControlY2(), 12);
		curv.controlX1Property().bind(gui1.centerXProperty());
		curv.controlY1Property().bind(gui1.centerYProperty());
		curv.controlX2Property().bind(gui2.centerXProperty());
		curv.controlY2Property().bind(gui2.centerYProperty());
		
		final Line gui1l = new Line();
		final Line gui2l = new Line();
		
		gui1l.startXProperty().bind(curv.startXProperty());		
		gui1l.startYProperty().bind(curv.startYProperty());	
		gui1l.endXProperty().bind(gui1.centerXProperty());	
		gui1l.endYProperty().bind(gui1.centerYProperty());			

		gui2l.startXProperty().bind(curv.endXProperty());		
		gui2l.startYProperty().bind(curv.endYProperty());	
		gui2l.endXProperty().bind(gui2.centerXProperty());	
		gui2l.endYProperty().bind(gui2.centerYProperty());	
		
		gui1l.setStroke(Color.GRAY);
		gui1l.setStrokeWidth(4);
		gui1l.getStrokeDashArray().addAll(15d, 15d);
		gui2l.setStroke(Color.GRAY);
		gui2l.setStrokeWidth(4);
		gui2l.getStrokeDashArray().addAll(15d, 15d);

		manipulators.getChildren().addAll(gui1, gui2);
		objs.getChildren().add(0, gui1l);
		objs.getChildren().add(0, gui2l);
		gui1.setStroke(Color.GOLDENROD);
		gui2.setStroke(Color.GOLDENROD);
		
		gui1.setOnMousePressed(click -> {
			gui1.setOnMouseDragged(drag -> {
				gui1.setCenterX(drag.getX());
				gui1.setCenterY(drag.getY());
			});
			gui1.setOnMouseReleased(drop -> {
				edge.manipulate(map.radianX(drop.getX()), map.radianY(drop.getY()), true);
				renderScreen();
				makeManipulaters(edge);
			});
		});
		gui2.setOnMousePressed(lc -> {
			gui2.setOnMouseDragged(drag -> {
				gui2.setCenterX(drag.getX());
				gui2.setCenterY(drag.getY());
			});
			gui2.setOnMouseReleased(drop -> {
				edge.manipulate(map.radianX(drop.getX()), map.radianY(drop.getY()), false);
				renderScreen();
				makeManipulaters(edge);
			});
		});
    }
    
    // this is called when they click on screen and want to draw a ball.
    private void clickVertFunct(final MouseEvent click) {
        int listSize = vertices.size();
        String labelBuild = "";
        if (listSize == 0) labelBuild += "A";
        while (listSize > 0) {
        	labelBuild += Utils.defaultVert[listSize % Utils.defaultVert.length];
        	listSize /= Utils.defaultVert.length;
        }
        final String label = labelBuild;
        drawVert(new Vertex(map.radianX(click.getX()), map.radianY(click.getY()), label, false, Color.BLUE));

        clickArea.setOnMouseDragged(drag -> {
            renderScreen();
            drawVert(new Vertex(map.radianX(drag.getX()), map.radianY(drag.getY()), label, false, Color.BLUE));
        });

        clickArea.setOnMouseReleased(drop -> {
            // when they release, we write the change and then set releasing back to doing nothing.
            vertices.add(new Vertex(
            		map.radianX(drop.getX()), map.radianY(drop.getY()), label, labVerts.isSelected(), Color.BLUE));
            clickArea.setOnMouseDragged(drag2 -> {});
            clickArea.setOnMouseReleased(drop2 -> {});
            renderScreen();
        });
    }

    private void drawVert(final Vertex vert) {
        objs.getChildren().add(vert.getVis(map));
        
        if (vert.getShow()) {
	        final GraphLabel text = new GraphLabel(vert);
	        labels.getChildren().add(text);
	        text.setLayoutX(map.pixelX(vert.getX() + vert.getlabOffX()));
	        text.setLayoutY(map.pixelY(vert.getY() + vert.getlabOffY()));
	        
	        text.setOnMousePressed(click -> {
	        	text.setOnMouseDragged(drag -> {
	        		text.setLayoutX(drag.getSceneX());
	        		text.setLayoutY(drag.getSceneY());
				});
	        	text.setOnMouseReleased(drop -> {
	        		vert.setlabOffX(map.radianX(drop.getSceneX()) - vert.getX());
	        		vert.setlabOffY(map.radianY(drop.getSceneY()) - vert.getY());
	
	        		renderScreen();
				});
			});
        }
    }
    
    private void clickSticFunct(final MouseEvent click, final double initX, final double initY) {
        int listSize = edges.size();
        String labelBuild = "";
        if (listSize == 0) labelBuild += "a";
        while (listSize > 0) {
        	labelBuild += Utils.defaultEdge[listSize % Utils.defaultEdge.length];
        	listSize /= Utils.defaultEdge.length;
        }
        final String label = labelBuild;

        // this optional is only empty when there are no vertices on screen.
        final Optional<Vertex> optVert = getClosestVert(map.radianX(click.getX()), map.radianY(click.getY()));
        final Vertex begin;
        if (optVert.isPresent()) begin = optVert.get();
        else {
        	basicmovefunct(initX, initY);
        	return;
        }
        
        drawLine(new Edge(begin, begin, label, Color.BLACK, false));

        clickArea.setOnMouseDragged(drag -> {
            renderScreen();
            
            final Optional<Vertex> dragV = getClosestVert(map.radianX(drag.getX()), map.radianY(drag.getY()));
            if (dragV.isPresent()) drawLine(
            		new Edge(begin, dragV.get(), label, Color.BLACK, false));
            else {
            	drawFakeLine(map.pixelX(begin.getX()), drag.getX(), map.pixelY(begin.getY()), drag.getY());
            }
        });

        clickArea.setOnMouseReleased(drop -> {

        	final Optional<Vertex> dropV = getClosestVert(map.radianX(drop.getX()), map.radianY(drop.getY()));
        	if (dropV.isPresent()) {
		    	final Edge eddie = new Edge(begin, dropV.get(), label, Color.BLACK, labEdges.isSelected());
		        edges.add(eddie);
		        begin.addChild(eddie);
		        dropV.get().addChild(eddie);
		        repair(begin, dropV.get());
		        clickArea.setOnMouseDragged(drag2 -> {});
		        clickArea.setOnMouseReleased(drop2 -> {});
        	}
        	renderScreen();
        });
    }
    
    // we want to avoid lines overlapping, so when you draw multiedges we call this.
    private void repair(final Vertex v1, final Vertex v2) {
    	final ArrayList<Edge> editors = new ArrayList<>();
    	for (final Edge ed : v1.getKids()) {
    		
    		if (v1.equals(v2)) {
    			if (ed.getv1().equals(ed.getv2())) {
    				if (!ed.standardpos()) return; // they have edited an edge
	    			
    				if (!editors.contains(ed))
    					editors.add(ed);
    			}
    		}
    		else {
	    		if (v2.getKids().contains(ed)) {
	    			if (!ed.standardpos()) return; // they have edited an edge
	    			
	    			editors.add(ed);
	    		}
    		}
    	}
    	for (int i=0; i < editors.size(); i++) {
    		final Edge ed = editors.get(i);
    		ed.setstandardoff(i, editors.size(), ed.getv1().equals(v1));
    	}
    }

    // these x and y are in radians
    final Optional<Vertex> getClosestVert(final double x, final double y) {
    	// result will be the square of the true shortest distance
    	double result = 1600;
    	Optional<Vertex> vertex = Optional.empty();
    	for (int i = 0; i < vertices.size(); i++) {
    		final Vertex v = vertices.get(i);
    		final double dist = Math.pow(v.getX() - x, 2) + Math.pow(v.getY() - y, 2);
    		if (dist < result) {
    			result = dist;
    			vertex = Optional.of(v);
    		}
    	}
    	return vertex;
    }
    
    // these x and y are in radians
    final Optional<Edge> getClosestEdge(final double x, final double y) {
    	Optional<Edge> edd = Optional.empty();
    	for (int i = 0; i < edges.size(); i++) {
    		if (edges.get(i).hitDetect(x, y, map)) {
    			edd = Optional.of(edges.get(i));
    		}
    	}
    	return edd;
    }
    
    // this does the tricky process of drawing the bezier edge lines
    private void drawLine(final Edge stik) {

        objs.getChildren().add(0, stik.getVis(map));
        
        if (stik.getShow()) {
	        final GraphLabel text = new GraphLabel(stik);
	        labels.getChildren().add(text);
	        text.setLayoutX(map.pixelX(stik.getX() + stik.getlabOffX()));
	        text.setLayoutY(map.pixelY(stik.getY() + stik.getlabOffY()));
	        
	        text.setOnMousePressed(click -> {
	        	text.setOnMouseDragged(drag -> {
	        		text.setLayoutX(drag.getSceneX());
	        		text.setLayoutY(drag.getSceneY());
				});
	        	text.setOnMouseReleased(drop -> {
	        		stik.setlabOffX(map.radianX(drop.getSceneX()) - stik.getX());
	        		stik.setlabOffY(map.radianY(drop.getSceneY()) - stik.getY());
	
	        		renderScreen();
				});
			});
        }
    }
    
    private void drawGrid() {
    	final double widd = map.toRad(clickArea.getWidth());
    	final double hiee = map.toRad(clickArea.getHeight());
    	
    	double step = 1;
    	while (widd/step > 30) step = step*2;
    	while (widd/step < 15) step = step/2;
    	
    	if (step < 0.00000001) return;
    	
    	double startradx = 0;
    	while (startradx < map.radianX(0)) startradx += step;
    	while (startradx > map.radianX(0)) startradx -= step;
    	
    	double startrady = 0;
    	while (startrady < map.radianY(0)) startrady += step;
    	while (startrady > map.radianY(0)) startrady -= step;
    	
    	
    	for (double i=startradx; i < startradx + widd + step; i += step) {
    		final Line vertline = new Line(map.pixelX(i), clickArea.getHeight(), map.pixelX(i), 0);
    		vertline.setStroke(Color.GRAY);
    		vertline.setStrokeWidth(1);
    		objs.getChildren().add(0, vertline);
    	}
    	
    	for (double i=startrady; i < startrady + hiee + step; i += step) {
    		final Line horline = new Line(0, map.pixelY(i), clickArea.getWidth(), map.pixelY(i));
    		horline.setStroke(Color.GRAY);
    		horline.setStrokeWidth(1);
    		objs.getChildren().add(0, horline);
    	}
    }
    
    private void drawCircleGrid() {
    	final double widd = map.toRad(clickArea.getWidth());
    	
    	double step = 1;
    	while (widd/step > 30) step = step*2;
    	while (widd/step < 15) step = step/2;
    	
    	if (step < 0.00000001) return;
    	
    	final double v1 = map.radianX(0)*map.radianX(0) 
    			          + map.radianY(0)*map.radianY(0);
    	final double v2 = map.radianX(0)*map.radianX(0) 
    			          + map.radianY(clickArea.getHeight())*map.radianY(clickArea.getHeight());
    	final double v3 = map.radianX(clickArea.getWidth())*map.radianX(clickArea.getWidth()) 
    			          + map.radianY(0)*map.radianY(0);
    	final double v4 = map.radianX(clickArea.getWidth())*map.radianX(clickArea.getWidth()) 
    			          + map.radianY(clickArea.getHeight())*map.radianY(clickArea.getHeight());
    	
    	final double maxrad = Math.sqrt(Math.max(Math.max(v1,v2), Math.max(v3, v4)));
    	
    	for (double i=0; i < maxrad; i += step) {
    		final Circle radcirc = new Circle(map.pixelX(0), map.pixelY(0), map.toPix(i));
    		radcirc.setFill(Color.TRANSPARENT);
    		radcirc.setStroke(Color.GRAY);
    		radcirc.setStrokeWidth(1);
    		objs.getChildren().add(0, radcirc);
    	}
    	
    	final int numlines;
    	try {
    		numlines = Integer.valueOf(radiallines.getText());
    	} catch (Exception e) {
    		System.out.println("Tried to make grid, but the text box had a non-int!");
    		return;
    	}
    	if (numlines < 3) {
    		System.out.println("Tried to make grid, but the number of lines was too low!");
    		return;
    	}
    	
    	for (double i=0; i < numlines; i ++) {
    		final double ang = 2 * i * (Math.PI/numlines);
    		final Line radline = new Line(map.pixelX(0), map.pixelY(0), 
    					map.pixelX(maxrad*Math.cos(ang)),  map.pixelY(maxrad*Math.sin(ang)));
    		radline.setStroke(Color.GRAY);
    		radline.setStrokeWidth(1);
    		objs.getChildren().add(0, radline);
    	}
    }
    
    private Optional<String> nearestcircgridpt(final double radx, final double rady) {
    	final double widd = map.toRad(clickArea.getWidth());
    	
    	double step = 1;
    	while (widd/step > 30) step = step*2;
    	while (widd/step < 15) step = step/2;
    	
    	if (step < 0.00000001) return Optional.empty();
    	
    	double temprad = Math.sqrt(radx*radx + rady*rady);
    	int sub = 0;
    	while (temprad > map.toRad(5)) {
    		temprad -= step;
    		sub += 1;
    	}
    	
    	final int numlines;
    	try {
    		numlines = Integer.valueOf(radiallines.getText());
    	} catch (Exception e) {
    		System.out.println("Tried to make grid, but the text box had a non-int!");
    		return Optional.empty();
    	}
    	if (numlines < 3) {
    		System.out.println("Tried to make grid, but the number of lines was too low!");
    		return Optional.empty();
    	}
    	
    	double tempang = Math.atan2(rady, radx) + (2*Math.PI);
    	int subang = 0;
    	while (tempang > Math.PI/(4*numlines)) {
    		tempang -= 2 * (Math.PI/numlines);
    		subang += 1;
    	}
    	// we are not close to any grid pt
    	if (temprad < map.toRad(-5)) return Optional.empty();
    	else if (tempang < -Math.PI/(4*numlines)) return Optional.empty();
    	
    	// we are close!
    	else {
    		final double radsnap = step * sub;
    		final double angsnap = 2 * (Math.PI/numlines) * subang;
    		
    		return Optional.of("" + (radsnap * Math.cos(angsnap)) + " " + (radsnap * Math.sin(angsnap)));
    	}
    	
    }
    
    private Optional<String> nearestgridpt(final double radx, final double rady) {
    	// we want to snap to a ball of 4 pixels radius...
    	final double widd = map.toRad(clickArea.getWidth());

    	double step = 1;
    	while (widd/step > 30) step = step*2;
    	while (widd/step < 15) step = step/2;
    	
    	if (step < 0.00000001) return Optional.empty();
    	
    	double tempradx = radx;
    	int subx = 0;
    	while (tempradx < map.toRad(-5)) {
    		tempradx += step;
    		subx -= 1;
    	}
    	while (tempradx > map.toRad(5)) {
    		tempradx -= step;
    		subx += 1;
    	}

    	double temprady = rady;
    	int suby = 0;
    	while (temprady < map.toRad(-5)) {
    		temprady += step;
    		suby -= 1;
    	}
    	while (temprady > map.toRad(5)) {
    		temprady -= step;
    		suby += 1;
    	}
    	
    	// we are not close to any grid pt
    	if      (tempradx < map.toRad(-5)) return Optional.empty();
    	else if (temprady < map.toRad(-5)) return Optional.empty();
    	
    	// we are close!
    	else {
    		final double xsnap = step * subx;
    		final double ysnap = step * suby;
    		
    		return Optional.of("" + xsnap + " " + ysnap);
    	}
    	
    	
    }
    
    //The fake red line that appears as you draw edges
    private void drawFakeLine(final double startx, final double endx, 
    		 				  final double starty, final double endy) {
    	
    	final Line fake = new Line(startx, starty, endx, endy);
    	fake.setStrokeWidth(2);
    	fake.getStrokeDashArray().addAll(5d, 5d);
    	fake.setStroke(Color.RED);
    	
    	objs.getChildren().add(0, fake);
    }
    
    // add the yellow glow to this edge
    private CubicCurve drawSelect(final Edge stik) {
    	final CubicCurve curv = stik.getVis(map);
    	final CubicCurve curv2 = curv;
    	objs.getChildren().remove(curv);
    	curv2.setStrokeWidth(10);
    	curv2.setStroke(Color.GOLD);
    	
        objs.getChildren().add(0, curv);
        
        return curv2;
    }

    // the graph data is of this form (note there can be multiple edges to a pair of vertices):
    
    // xtrans;
    // ytrans;
    // zoom;
    // MATRIX
	// v1;
    // v2;
    // v3;
    // MATRIX
    // e11 e12 e13;
    // e21 e22 e23;
    // e31 e32 e33;
    public String generateGraphData() {
    	final StringBuilder info = new StringBuilder();
        final StringBuilder vBuilder = new StringBuilder();
        final StringBuilder mBuilder = new StringBuilder();
        
        info.append(map.transX + ";\n");
        info.append(map.transY + ";\n");
        info.append(map.getScale() + "");
        
        for (Vertex vert : vertices) {
        	vBuilder.append(vert.toString() + ";\n");
        	
    		final StringBuilder matline = new StringBuilder();
        	for (Vertex vert2 : vertices) {
        		final StringBuilder matCoef = new StringBuilder();
        		for (Edge edge: edges) {
        			if (edge.checkParents(vert, vert2, true)) {
        				if (matCoef.length() != 0) matCoef.append(":"); 
        				matCoef.append(edge.toString());
        			}
        		}
        		if (matCoef.length() == 0) matline.append("null"); 
        		matline.append(matCoef.toString() + " ");
        	}
        	mBuilder.append(matline.toString() + ";\n");
        }

        // System.out.println(vBuilder.toString().trim() + "\n\n" + mBuilder.toString());
        
        return info.toString() + "\nMATRIX\n" + vBuilder.toString().trim() + "\nMATRIX\n" + mBuilder.toString();
    }
    
    public String generateAdjacency() {
        final StringBuilder mBuilder = new StringBuilder();
        for (Vertex vert : vertices) {
        	mBuilder.append("\n");
        	
    		final StringBuilder matline = new StringBuilder();
        	for (Vertex vert2 : vertices) {
        		int numedge = 0;
        		
        		for (Edge edge: edges) {
        			if (edge.checkParents(vert, vert2, direcBtn.isSelected())) {
        				numedge += 1;
        			}
        		}
        		matline.append(numedge + " ");
        	}
        	mBuilder.append(matline.toString());
        }
        
        return mBuilder.toString();
    }

    // when they maximize the screen, for some reason this doesn't register as a change of size for the listeners that
    // adjust the pixradmap on a resize. So, we have to manually render it in this case.
    public void maximizeScreen() {
        renderScreen();
    }

    public void setGraphData(final String name, final String graphDat) {
    	System.out.println(graphDat);
        this.vertices.clear();
        this.edges.clear();
        title = name;
        
        if (graphDat.trim().equals("")) return;
        
        final String infoStr =  graphDat.split("MATRIX")[0].trim();
        final String vertsStr = graphDat.split("MATRIX")[1].trim();
        final String edgesStr = graphDat.split("MATRIX")[2].trim();
        final String[] infoLines = infoStr.split(";");
        final String[] mainLines = vertsStr.split(";");
        final String[] matLines = edgesStr.split(";");
        
        for (int i = 0; i < mainLines.length; i++ ) {
            final String trimLine = mainLines[i].trim();
            final String[] matCoefs = matLines[i].split(" ");
            
            final Vertex v1;
            final Vertex temp = Vertex.getFromData(trimLine);
            if (Utils.containsFix(temp, vertices) != -1) {
    			final int index = Utils.containsFix(temp, vertices);
    			v1 = vertices.get(index);
    		} else {
    			vertices.add(temp);
    			v1 = temp;
    		}
            
            for (int j = 0; j < mainLines.length; j++) {
            	if (matCoefs[j].trim().equals("null")) continue;

            	final String trimLine2 = mainLines[j].trim();
            	final Vertex v2;
            	if (i == j) v2 = v1;
            	else { 
            		final Vertex temp2 = Vertex.getFromData(trimLine2);
            		
            		if (Utils.containsFix(temp2, vertices) != -1) {
            			final int index = Utils.containsFix(temp2, vertices);
            			v2 = vertices.get(index);
            		} else {
            			vertices.add(temp2);
            			v2 = temp2;
            		}
            	}
            	
            	final String[] edgeLines = matCoefs[j].trim().split(":");
            	for (int k = 0; k < edgeLines.length; k++) {
            		final Edge e = Edge.getFromData(edgeLines[k].trim(), v1, v2);
            		this.edges.add(e);
            		v1.addChild(e);
            		v2.addChild(e);
            	}
            }
        }
        map.translateX(Double.valueOf(infoLines[0]));
        map.translateY(Double.valueOf(infoLines[1]));
        map.setScale(Double.valueOf(infoLines[2]));
        levelLable.setText(title);
        renderScreen();
    }
    
    final class EditBox extends Pane {

    	final static int COLS = 12;
    	final static int RECT_SIZE = 20;
    	private final Color[] colors = {
    		Color.BLACK,  Color.RED,  Color.BLUE, Color.GREEN,   Color.GOLD,   Color.CHOCOLATE,
    	    Color.ORANGE, Color.PINK, Color.LIME, Color.MAGENTA, Color.PURPLE, Color.TURQUOISE
    	};
    	
    	final VBox root = new VBox(10);
    	final TextField labelfield = new TextField();
    	final HBox labelHB = new HBox(10);
    	final ComboBox<Integer> fontSizeCB = new ComboBox<>();
    	final CheckBox showCB = new CheckBox();
    	
    	final HBox lineTypeHB = new HBox(10);
    	final RadioButton lineType1Btn = new RadioButton();
    	final RadioButton lineType2Btn= new RadioButton();
    	final ToggleGroup lineTypeGroup = new ToggleGroup();
    	
    	final HBox colorsHB = new HBox();
    	
        private EditBox(final GraphBit piece, final Optional<Edge> optEdge, final Optional<Vertex> optVert) {
        	super();
        	
        	if (!optEdge.isPresent() && !optVert.isPresent()) {
        		throw new RuntimeException("I don't know what was passed into the edit box");
        	}
        	
        	this.setStyle("-fx-background-color: #" + Utils.hex(Color.ANTIQUEWHITE));
        	
        	labelfield.setText(piece.getLabel());
         	labelfield.setPrefWidth(88);
         	labelfield.setOnAction(event -> {
         		piece.setLabel(labelfield.getText());
    			renderSafe();
        	});
        	
        	showCB.setText("Lable");
    		showCB.setSelected(piece.getShow());
    		showCB.setOnAction(click -> {
    			piece.setShow(showCB.isSelected());
    			renderSafe();
    		});
        	
        	fontSizeCB.setPrefWidth(70);
        	fontSizeCB.getItems().addAll(6, 8, 9, 10, 11, 12, 13, 14, 18, 24, 32, 44, 56);
        	fontSizeCB.setValue(piece.getLabFont());
        	fontSizeCB.setOnAction(change -> {
        		piece.setLabFont(fontSizeCB.getValue());
        		renderSafe();
        	});
        	
        	for (int col = 0; col < COLS; col++) {
                final Color currentColor = colors[col];

                final Rectangle rect = new Rectangle(RECT_SIZE, RECT_SIZE, currentColor);

                rect.setOnMouseClicked(event -> {
                    piece.setColor(currentColor);
                    renderSafe();
                });

                colorsHB.getChildren().add(rect);
            }
        	
        	labelHB.getChildren().addAll(showCB, labelfield, fontSizeCB);
        	labelHB.setAlignment(Pos.CENTER);
        	
        	root.setPadding(new Insets(10));
        	root.getChildren().addAll(colorsHB, labelHB);
        	this.getChildren().add(root);
        	
        	final double gotX;
        	final double gotY;
        	
        	if (optVert.isPresent()) {
        		final Vertex vert = optVert.get();
        		gotX = vert.getX();
        		gotY = vert.getY();
        	} else {
        		final Edge edge = optEdge.get();
            	lineType1Btn.setToggleGroup(lineTypeGroup);
            	lineType1Btn.setSelected(!edge.getDash());
            	lineType1Btn.setOnAction(nodash -> {
            		edge.setDash(false);
            		renderSafe();
            	});
            	lineType2Btn.setToggleGroup(lineTypeGroup);
            	lineType2Btn.setSelected(edge.getDash());
            	lineType2Btn.setOnAction(dash -> {
            		edge.setDash(true);
            		renderSafe();
            	});
            	
            	final Line lineType1 = new Line(0, 0, 75, 0);
            	final Line lineType2 = new Line(0, 0, 75, 0);
            	lineType2.getStrokeDashArray().addAll(8d, 8d);

            	lineTypeHB.getChildren().addAll(lineType1Btn, lineType1, lineType2Btn, lineType2);
            	lineTypeHB.setAlignment(Pos.CENTER);
            	
            	root.getChildren().addAll(lineTypeHB);

        		gotX = edge.getX();
        		gotY = edge.getY();
        	}
        	
        	if (map.pixelX(gotX) < map.width - (RECT_SIZE * COLS + 20)) { 
	        	this.setTranslateX(map.pixelX(gotX));
        	} else {
        		this.setTranslateX(map.pixelX(gotX) - (RECT_SIZE*COLS + 20));
        	}
        	
        	if (map.pixelY(gotY) + 100 < map.height) {
        		this.setTranslateY(map.pixelY(gotY));
        	} else {
        		this.setTranslateY(map.pixelY(gotY) - 100);
        	}
        }
    }
}



