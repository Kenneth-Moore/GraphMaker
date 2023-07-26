package graphbits;

import javafx.scene.paint.Color;

public class GraphBit {

	protected Color color;
	protected boolean showObj;
	
	protected String label;
	protected boolean showLable;
	protected int labFont;
	protected double labOffX;
	protected double labOffY;	
	
	public GraphBit(final String lable, final boolean showLab, final Color col, 
			final double xlaboff, final double ylaboff) {
		this.showObj = true;
		this.showLable = showLab;
		this.color = col;
		this.label = lable;
		this.labFont = 11;
		
		this.labOffX = xlaboff;
		this.labOffY = ylaboff;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(final String newLab) {
		this.label = newLab;
	}
	
	public boolean getShow() {
		return this.showLable;
	}
	
	public void setShow(final boolean newShow) {
		this.showLable = newShow;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(final Color color) {
		this.color = color;
	}
	
	public double getlabOffX() {
		return this.labOffX;
	};
	
	public double getlabOffY() {
		return this.labOffY;
	};	
	
	public void setlabOffX(final double newoffX) {
		this.labOffX = newoffX;
	};
	
	public void setlabOffY(final double newoffY) {
		this.labOffY = newoffY;
	};
	
	public int getLabFont() {
		return this.labFont;
	}
	
	public void setLabFont(final int font) {
		this.labFont = font;
	}
}
