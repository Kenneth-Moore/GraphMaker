package graphbits;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Edge extends GraphBit {
	
	private static final double defaultStep = Math.PI/12;
	
	private final Vertex v1;
	private final Vertex v2;
	// the following are how curved this edge is.
	private double bez1ang;
	private double bez1;
	private double bez2ang;
	private double bez2;
	
	private boolean dashed;
	
	public Edge(final Vertex v1, final Vertex v2, final String lable, final Color col, final boolean showLab) {
		super(lable, showLab, col, 0, 0);
		
		this.v1 = v1;
		this.v2 = v2;

		// the folloing keep track of the curvature. bezJang is the angle of the bezier line on vertex J, and bezJ is
		// the ratio of it's length to the length between the vertices. The reason we store the info this odd way is 
		// because when manipulating vertices we don't want to mess with the curvature of the attatched edges, and 
		// this makes that easy.
		
		this.bez1ang = 0;
		this.bez1 = 0.333;
		this.bez2ang = 0;
		this.bez2 = 0.333;
		
		this.dashed = false;
		
		if (v1.equals(v2)) {
			bez1ang = Math.PI / 2;
		} 
	}
	
	public void setstandardoff(final int i, final int total, boolean reversed) {
		this.bez1 = 0.333;
		this.bez2 = 0.333;
		
		this.bez1ang = defaultStep * (i-(total-1.0)/2.0);
		if (reversed) this.bez1ang = -this.bez1ang;
		this.bez2ang = this.bez1ang;
		if (v1.equals(v2)) this.bez1ang += Math.PI / 2;
	}
	
	// this method is meant to get a new edge by gently increasing or decreasing the bezangs, for the purposes of hit-
	// detection.
	private Edge(final Edge base, final boolean upper) {
		super("", false, Color.TRANSPARENT, 0, 0);
		this.v1 = base.v1;
		this.v2 = base.v2;
		
		if (upper) {
			this.bez1ang = base.bez1ang + Math.PI / 16;
			this.bez2ang = base.bez2ang + Math.PI / 16;
		} else {
			this.bez1ang = base.bez1ang - Math.PI / 16;
			this.bez2ang = base.bez2ang - Math.PI / 16;
		}
		this.bez1 = base.bez1;
		this.bez2 = base.bez2;
	}
	
	public static Edge getFromData(final String data, final Vertex vert1, final Vertex vert2) {
		final String[] args = data.split(Utils.delim);
		final Color col = Color.valueOf(args[0]);
		//final boolean show = Boolean.valueOf(args[1]);
		final String lab = args[2];
		final Boolean showLab = Boolean.valueOf(args[3]);
		final int font = Integer.parseInt(args[4]);
		final double xLabOff = Double.valueOf(args[5]);
		final double yLabOff = Double.valueOf(args[6]);
		final double bez1a = Double.valueOf(args[7]);
		final double bez1l = Double.valueOf(args[8]);
		final double bez2a = Double.valueOf(args[9]);
		final double bez2l = Double.valueOf(args[10]);
		final boolean dash = Boolean.valueOf(args[11]);
		
		final Edge edge = new Edge(vert1, vert2, lab, col, showLab);
		edge.setDash(dash);
		edge.setlabOffX(xLabOff);
		edge.setlabOffY(yLabOff);
		edge.setBez1(bez1l);
		edge.setBez1ang(bez1a);
		edge.setBez2(bez2l);
		edge.setBez2ang(bez2a);
		edge.setLabFont(font);
		return edge;
	}
	
	public Polygon directionVis(final PixRadMap map) {
		
		final CubicCurve cub = this.getVis(map);
		
		final double thet = Math.PI + Math.atan2(v2.getY()-map.radianY(cub.getControlY2()), 
												 v2.getX()-map.radianX(cub.getControlX2()));
		final double point1x = v2.getX() + 12*Math.cos(thet);
		final double point1y = v2.getY() + 12*Math.sin(thet);
		final double point2x = v2.getX() + 25*Math.cos(thet + defaultStep);
		final double point2y = v2.getY() + 25*Math.sin(thet + defaultStep);
		final double point3x = v2.getX() + 25*Math.cos(thet - defaultStep);
		final double point3y = v2.getY() + 25*Math.sin(thet - defaultStep);
		
		Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
        	map.pixelX(point1x), map.pixelY(point1y),
        	map.pixelX(point2x), map.pixelY(point2y),
        	map.pixelX(point3x), map.pixelY(point3y),
        });
        
        polygon.setStroke(color);
        polygon.setFill(color);

        return polygon;
	}
	
	public CubicCurve getVis(final PixRadMap map) {
		if (!showObj) return new CubicCurve();
		
		double dist1 = this.getLen() * bez1;
		double dist2 = this.getLen() * bez2;
		
		CubicCurve bezier = new CubicCurve();
		bezier.setStartX(map.pixelX(v1.getX()));
		bezier.setStartY(map.pixelY(v1.getY()));
		bezier.setControlX1(map.pixelX(v1.getX() - dist1 * Math.cos(this.getAng() + bez1ang)));
		bezier.setControlY1(map.pixelY(v1.getY() - dist1 * Math.sin(this.getAng() + bez1ang)));
		bezier.setControlX2(map.pixelX(v2.getX() + dist2 * Math.cos(this.getAng() - bez2ang)));
		bezier.setControlY2(map.pixelY(v2.getY() + dist2 * Math.sin(this.getAng() - bez2ang)));
		bezier.setEndX(map.pixelX(v2.getX()));
		bezier.setEndY(map.pixelY(v2.getY()));
		
		bezier.setFill(Color.TRANSPARENT);
		bezier.setStrokeWidth(map.toPix(5));
		if (dashed) bezier.getStrokeDashArray().addAll(10d, 10d);
		bezier.setStroke(color);
		return bezier;
	}
	
	public boolean standardpos() {
		return this.bez1 == 0.333 && this.bez2 == 0.333; // && (this.bez1ang == this.bez2ang);
	}
	
	// these x and y are expected to be in radians
	public void manipulate(final double x, final double y, final boolean firstone) {
		
		if (firstone) {
			bez1 = Math.sqrt(Math.pow(v1.getY() - y, 2) + Math.pow(v1.getX() - x, 2)) / this.getLen();
			bez1ang = -Math.atan2(y - v1.getY(), v1.getX() - x) - this.getAng();
			if (Math.abs(bez1ang) < (Math.PI / 64)) bez1ang = 0;
			
		} else {
			bez2 = Math.sqrt(Math.pow(v2.getY() - y, 2) + Math.pow(v2.getX() - x, 2)) / this.getLen();
			bez2ang = Math.atan2(v2.getY() - y, x - v2.getX()) + this.getAng();
			if (Math.abs(bez2ang) < (Math.PI / 64)) bez2ang = 0;
		}
	}
	
	// x and y are in pixels here
	public boolean hitDetect(final double x, final double y, final PixRadMap map) {
		final CubicCurve curUp = (new Edge(this, true)).getVis(map);
		final CubicCurve curDn = (new Edge(this, false)).getVis(map);
		
		curUp.setStrokeWidth(0);
		curDn.setStrokeWidth(0);

		final boolean topper = curUp.contains(x, y);
		final boolean botter = curDn.contains(x, y);
		
		// if we clicked in one but not both of those curves, we consider it a hit
		return ((topper || botter) && !(topper && botter));
	}
	
	public void setBez1ang(double bez1ang) {
		this.bez1ang = bez1ang;
	}

	public void setBez1(double bez1) {
		this.bez1 = bez1;
	}

	public void setBez2ang(double bez2ang) {
		this.bez2ang = bez2ang;
	}

	public void setBez2(double bez2) {
		this.bez2 = bez2;
	}
	
	public boolean isLoop() {
		return v1.equals(v2);
	}
	
	public double getlabOffX() {
		return this.labOffX;
	}
	
	public double getlabOffY() {
		return this.labOffY;
	}
	
	public void setlabOffX(final double newoffX) {
		this.labOffX = newoffX;
	}
	
	public void setlabOffY(final double newoffY) {
		this.labOffY = newoffY;
	}
	
	public Boolean getDash() {
		return this.dashed;
	}
	
	public void setDash(final Boolean newDash) {
		this.dashed = newDash;
	}
	
	public void setColor(final Color color) {
		this.color = color;
	}
	
	public int getLabFont() {
		return this.labFont;
	}
	
	public void setLabFont(final int font) {
		this.labFont = font;
	}
	
	public double getX() {
		return (v1.getX() + v2.getX()) / 2;
	}
	
	public double getY() {
		return (v1.getY() + v2.getY()) / 2;
	}

	// this is the angle made by the two vertices
	public double getAng() {
		return Math.atan2(v1.getY() - v2.getY(), v1.getX() - v2.getX());
	}
	
	public Color getCol() {
		return this.color;
	}
	
	public Vertex getv1() {
		return v1;
	}
	
	public Vertex getv2() {
		return v2;
	}
	
	public boolean checkParents(final Vertex v11, final Vertex v22, final boolean directed) {
		if (directed) return v1.equals(v11) && v2.equals(v22);
		else          return (v1.equals(v11) && v2.equals(v22)) || v2.equals(v11) && v1.equals(v22);
	}
	
	public double getLen() {
		if (v1.equals(v2)) return 500;
		else return Math.sqrt((v1.getY() - v2.getY()) * (v1.getY() - v2.getY()) + 
				              (v1.getX() - v2.getX()) * (v1.getX() - v2.getX()));
	}

	@Override
	public String toString() {
		final String del = Utils.delim;
		return color + del + showObj + del + label + del + showLable
				 + del + labFont + del + labOffX + del + labOffY + del + bez1ang
				 + del + bez1 + del + bez2ang + del + bez2 + del + dashed;
	}
	
	
}
