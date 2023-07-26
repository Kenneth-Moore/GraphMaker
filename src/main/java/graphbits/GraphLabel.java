package graphbits;

import javafx.scene.control.Label;

// this class is essentially just a regular label, but it can map us back to what it labels.
public class GraphLabel extends Label {
	final GraphBit piece;
	
	public GraphLabel(final GraphBit e) {
		super(e.getLabel());
		this.piece = e;
        this.setStyle("-fx-font-size: " + (e.getLabFont() * 2));
        this.setTextFill(e.getColor());
	}
	
	// return true if this labels an edge, false for a vertex.
	
	public GraphBit get() {
		return piece;
	}
}
