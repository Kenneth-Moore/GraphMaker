package graphbits;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class Vertex extends GraphBit {
	
	private static final double radius = 15;
	
	private double xPos;
	private double yPos;

	private ArrayList<Edge> childs;
	
	public Vertex(final double xPos, final double yPos, final String lable, final boolean showLab, final Color col) {
		super(lable, showLab, col, radius, radius);
		this.xPos = xPos;
		this.yPos = yPos;
		
		childs = new ArrayList<>();
	}
	
	public Circle getVis(final PixRadMap map) {
		final Circle circ = new Circle(map.pixelX(xPos), map.pixelY(yPos), map.toPix(radius));
		circ.setFill(color);
		circ.setStroke(color.darker());
		circ.setStrokeWidth(map.toPix(radius/5));
		circ.setStrokeType(StrokeType.INSIDE);
		return circ;
	}
	
	public static Vertex getFromData(final String data) {
		final String[] args = data.split(Utils.delim);
		final double xPosi =     Double.valueOf(args[0]);
		final double yPosi =     Double.valueOf(args[1]);
		final Color col =         Color.valueOf(args[2]);
		final boolean show =    Boolean.valueOf(args[3]);
		final String lab =                      args[4];
		final Boolean showLab = Boolean.valueOf(args[5]);
		final int labFont =    Integer.parseInt(args[6]);
		final double xlaboff =   Double.valueOf(args[7]);
		final double ylaboff =   Double.valueOf(args[8]);
		
		final Vertex vert = new Vertex(xPosi, yPosi, lab, showLab, col);
		vert.setShowObj(show);
		vert.setLabFont(labFont);
		vert.setlabOffX(xlaboff);
		vert.setlabOffY(ylaboff);
		
		return vert;
	}
	
	public void setShowObj(final boolean show) {
		this.showObj = show;
	}
	
	public double getRad() {
		return radius;
	}
		
	public void move(final double x, final double y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	public double getX() {
		return this.xPos;
	}
	
	public double getY() {
		return this.yPos;
	}
	
	public void addChild(Edge eddie) {
		childs.add(eddie);
	}
	
	public void evictChild(Edge eddie) {
		childs.remove(eddie);
	}

	public ArrayList<Edge> getKids() {
		return this.childs;
	}
	
	public ArrayList<Edge> commonKids(final Vertex v) {
		final ArrayList<Edge> result = new ArrayList<>();
		
		if (v.equals(this)) {
			for (Edge e : this.getKids()) {
				if (e.isLoop()) {
					result.add(e);
				}
			}
		}
		
		else {
			for (Edge e : this.getKids()) {
				if (v.getKids().contains(e)) {
					result.add(e);
				}
			}
		}
		return result;
	}
	
	public boolean equalsV(final Vertex v) {
		return xPos == v.getX() && yPos == v.getY();
	}
	
	@Override
	public String toString() {
		final String del = Utils.delim;
		return xPos + del + yPos + del + color + del + showObj + del + label + del + showLable + del + labFont 
			 + del + labOffX + del + labOffY;
	}

}
