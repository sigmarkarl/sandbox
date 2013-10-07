package org.simmi.client;

public class Rectangle {
	double	x, y, width, height;
	
	public Rectangle() {
		
	}
	
	public Rectangle( double x, double y, double w, double h ) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	public boolean intersects( Rectangle r ) {
		boolean xint = x < r.x+r.width;
		boolean rxint = r.x < x+width;
		boolean yint = y < r.y+r.height;
		boolean ryint = r.y < y+height;
		
		boolean fx = xint && rxint;
		boolean fy = yint && ryint;
		
		return fx && fy;
	}

	public boolean contains(double pxs, double pys) {
		if( pxs >= x && pxs <= x+width && pys >= y && pys <= y+height ) return true;
		return false;
	}
}
