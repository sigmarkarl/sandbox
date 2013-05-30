package org.simmi.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.simmi.client.Corp.LinkInfo;
import org.simmi.shared.TreeUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;

public class Connectron extends VerticalPanel 
	implements 	DoubleClickHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler,
				KeyDownHandler, KeyUpHandler, KeyPressHandler, 
				DragHandler, DragEnterHandler, DragLeaveHandler, DragOverHandler, DropHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Canvas				canvas;
	Rectangle 			selRect = null;
	CssColor			selColor = CssColor.make( "rgba(0,128,255,32)" );//"#0077ff33"; //new Color( 0,128,255,32 );
	Corp				linkCorp = null;
	Corp				linkCorp2 = null;
	boolean 			toggle = false;
	boolean				birta = true;
	boolean				drawLinks = true;
	boolean				drawLinkNames = true;
	boolean				drawNodeNames = true;
	//boolean				drawPersonNames = true;
	boolean				d3 = true;
	
	double hhx, hhy, hhz;
	double cx;
	double cy;
	double cz;
	boolean shift = false;
	boolean	fixed = false;
	
	double dsize = 1.0;
	
	public void repaint() {
		paintComponent( canvas.getContext2d() );
	}
	
	public Connectron() {
		init();
	}
	
	int 	npx;
	int		npy;
	int		px;
	int		py;
	public Timer springThread() {
		Timer t = new Timer() {
			@Override
			public void run() {
				if( toggle ) repaint();
				else this.cancel();
			}
		};
		
		return t;
		
		/*return new Thread() {
			public void run() {
				while( toggle ) {
					repaint();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};*/
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public void requestFocus() {
		canvas.setFocus( true );
	}
	
	double mincoulombradius = 0.1;
	//double u = 1000.0;
	public void spring() {
		final double damp = 0.97;
		//final double u = 1000.0;
		final double gorm = 0.0;
		final double k = 1.0;
		
		double mcr = Math.pow( mincoulombradius, 1.0/3.0 );
		for( Corp corp : Corp.corpList ) {
			double fx = 0;
			double fy = 0;
			double fz = 0;
			for( Corp c : Corp.corpList ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				double r2 = r*r;
				double r3 = r2*r;
				
				double u = c.getCoulomb();
				if( r3 > mincoulombradius ) {
					fx += (u*dx)/r3;
					fy += (u*dy)/r3;
					fz += (u*dz)/r3;
				} else {
					//fx += (u*dx*mcr)/(r*mincoulombradius);
					//fy += (u*dy*mcr)/(r*mincoulombradius);
					//fz += (u*dz*mcr)/(r*mincoulombradius);
				}
			}
			for( Corp c : corp.backconnections.keySet() ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				
				LinkInfo li = corp.backconnections.get(c);
				double h = li.getOffset();
				double st = li.getStrength();
					//double k = li.getStrength();
				
				r = Math.max( r, 0.1 );
				//if( r > 0.1 ) {
				double dh = r-h;
				
				fx -= k*st*(dx*dh/r-gorm);
				fy -= k*st*(dy*dh/r-gorm);
				fz -= k*st*(dz*dh/r-gorm);
				//}
			}
			for( Corp c : corp.connections.keySet() ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				
				LinkInfo li = corp.connections.get(c);
				double h = li.getOffset();
				double st = li.getStrength();
					//double k = li.getStrength();
				
				r = Math.max( r, 0.1 );
				//if( r > 0.1 ) {
				double dh = r-h;
				
				fx = -k*st*(dx*dh/r-gorm);
				fy = -k*st*(dy*dh/r-gorm);
				fz = -k*st*(dz*dh/r-gorm);
				//}
			}
			
			for( Corp c : corp.connections.keySet() ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				
				LinkInfo li = corp.connections.get(c);
				double h = li.getOffset();
				double st = li.getStrength();
					//double k = li.getStrength();
				
				r = Math.max( r, 0.1 );
				//if( r > 0.1 ) {
				double dh = r-h;
				
				fx -= k*st*(dx*dh/r-gorm);
				fy -= k*st*(dy*dh/r-gorm);
				fz -= k*st*(dz*dh/r-gorm);
				//}
			}
			
			corp.vx = (corp.vx+fx)*damp;
			corp.vy = (corp.vy+fy)*damp;
			corp.vz = (corp.vz+fz)*damp;
		
			corp.setx( corp.getx() + corp.vx );
			corp.sety( corp.gety() + corp.vy );
			corp.setz( corp.getz() + corp.vz );
			
			//corp.setBounds( (int)(corp.x-Corp.size/2), (int)(corp.y-Corp.size/2), Corp.size, Corp.size );
		}
		//c.repaint();
	}
	
	Corp backtracker = null;
	void backtrack( double x, double y, double z, int w, int h, Corp c ) {
		double cosx = Math.cos(hhx);
		double sinx = Math.sin(hhx);
		double cosy = Math.cos(hhy);
		double siny = Math.sin(hhy);
		double cosz = Math.cos(hhz);
		double sinz = Math.sin(hhz);
		
		backtracker = c;
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		double dx = x - w/2.0;
		double dy = y - h/2.0;
		double dz = z; //c.depz;//zoomval;
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );
	}
	
	void backtrack( int px, int py, double z, Corp c ) {		
		//double hx = hhx;
		//double hy = hhy;
		//double hz = hhz;
		
		/*if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}*/
		
		//System.err.println( p + "    " + np );
		
		//updateCenterOfMass();
		backtrack( px, py, z, getParentWidth(), getParentHeight(), c );
		
		/*double dx = p.x - this.getWidth()/2;
		double dy = p.y - this.getHeight()/2;
		double dz = zoomval;
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );*/
	}
	
	public int getWidth() {
		return canvas.getCoordinateSpaceWidth();
	}
	
	public int getHeight() {
		return canvas.getCoordinateSpaceHeight();
	}
	
	public int getParentWidth() {
		return this.getWidth();
	}
	
	public int getParentHeight() {
		return this.getHeight();
	}
	
	public void backtest( double x, double y ) {		
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		//System.err.println( "testing " + hx + "  " + hy + "  " + hz );
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (npx != -1 && px != -1 ) {
			if (!shift)
				hy += (npx - px) / 100.0;
			hx += (npy - py) / 100.0;
			if (shift)
				hz += (npx - px) / 100.0;
		}
		
		//System.err.println( p + "    " + np );

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		updateCenterOfMass();
		
		//System.err.println( "xyz " + x + "  " + y + "  " + cz );
		
		double dx = x - getParentWidth()/2;
		double dy = y - getParentHeight()/2;
		double dz = cz;
		
		//System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		//System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		//System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		//System.err.println( "xxyz " + xx + "  " + yy + "  " + zz + "  " + cosx + "   " + sinx );
		
		double ry = yy + cy;
		double rx = xx * cosy + zz * siny + cx;
		double rz = zz * cosy - xx * siny + cz;
		
		System.err.println( "erm " + cx + "  " + cy + "  " + cz );
		System.err.println( "erm " + hx + "  " + hy + "  " + hz );
		System.err.println( "erm " + cosx + "  " + cosy + "  " + cosz );
		
		//System.err.println( "rxyz " + rx + "  " + ry + "  " + rz );
		
		xx = (rx - cx) * cosy - (rz - cz) * siny;
		yy = (ry - cy);
		zz = (rx - cx) * siny + (rz - cz) * cosy;
		
		//System.err.println( "xxyz " + xx + "  " + yy + "  " + zz );

		nx = xx;
		ny = yy * cosx - zz * sinx;
		nz = yy * sinx + zz * cosx; // cz;
		
		//System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );

		lx = nx * cosz + ny * sinz;
		ly = ny * cosz - nx * sinz;
		lz = nz + zoomval;
		
		//System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );

		dz = lz;
		dx = (lx * mval) / (zval + dz);
		dy = (ly * mval) / (zval + dz);
		
		//System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		x = dx+getParentWidth()/2;
		y = dy+getParentHeight()/2;
		double z = dz;
		
		//System.err.println( "xyz " + x + "  " + y + "  " + z );
	}
	
	void updateCenterOfMass() {
		double len = Corp.corpList.size();
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		for( Corp rel : Corp.corpList ) {
			cx += rel.getx();
			cy += rel.gety();
			cz += rel.getz();
		}
		if( len > 0 ) {
			cx /= len;
			cy /= len;
			cz /= len;
		}
		//console( "center update "+len );
	}
	
	double zoomval = 500.0;
	double dzoomval = 500.0;
	public void depth() {
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		//System.err.println("depcalc");
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (npx != -1 && px != -1) {
			if (!shift)
				hy += (npx - px) / 100.0;
			hx += (npy - py) / 100.0;
			if (shift)
				hz += (npx - px) / 100.0;
		}

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		if( !fixed ) {
			updateCenterOfMass();
		}
		
		for( Corp rel : Corp.corpList ) {
			double xx = (rel.getx() - cx) * cosy - (rel.getz() - cz) * siny;
			double yy = (rel.gety() - cy);
			double zz = (rel.getx() - cx) * siny + (rel.getz() - cz) * cosy;

			double nx = xx;
			double ny = yy * cosx - zz * sinx;
			double nz = yy * sinx + zz * cosx; // cz;

			double lx = nx * cosz + ny * sinz;
			double ly = ny * cosz - nx * sinz;
			double lz = nz + zoomval;

			double dz = lz;
			double dx = (lx * mval) / (zval + dz);
			double dy = (ly * mval) / (zval + dz);

			rel.px = dx+getParentWidth()/2.0;
			rel.py = dy+getParentHeight()/2.0;
			rel.pz = dz;
			
			int size = (int) (d * rel.getSize() * dsize / (zval + dz));
			
			int x = (int)dx+getParentWidth()/2;
			int y = (int)dy+getParentHeight()/2;
			
			/*if( backtracker != null ) {
				System.err.println( "backtracker " + x + "  " + y + "  " + dz );
				
				backtracker = null;
			}*/
			
			rel.depz = dz;

			/*if( rel == Corp.corpList.get(Corp.corpList.size()-1) ) {
				System.err.println( "hoho " + cx + "  " + cy + "  " + cz );
				System.err.println( "hoho " + hx + "  " + hy + "  " + hz );
				System.err.println( "hoho " + cosx + "  " + cosy + "  " + cosz );
				System.err.println( "heyr " + rel.x + "   " + rel.y + "   " + rel.z );
				System.err.println( "xx " + xx + "   " + yy + "   " + zz );
				System.err.println( "nx " + nx + "   " + ny + "   " + nz );
				System.err.println( "lx " + lx + "   " + ly + "   " + lz );
				System.err.println( "dx " + dx + "   " + dy + "   " + dz );
				System.err.println( "x " + x + "   " + y );
			}*/
			//System.err.println( "set bounds " + x + "   " + y + "   " + rel.x + "   " + rel.y + "   " + rel.z );
			rel.setBounds( x, y, size, size );
		}
	}
	
	public void selectAll() {
		for( Corp c : Corp.corpList ) {
			c.selected = true;
			Corp.selectedList.add( c );
		}
		Connectron.this.repaint();
	}
	
	public void invertSelection() {
		Corp.selectedList.clear();
		for( Corp c : Corp.corpList ) {
			if( !c.selected ) {
				Corp.selectedList.add( c );
			}
			c.selected = !c.selected;
		}
		Connectron.this.repaint();
	}
	
	public void init() {
		//this.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		this.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		this.setSize("100%", "100%");
		
		initGUI();
		
		//this.add( scrollpane );
	}
	
	/*private class Node {
		String 		name;
		private double		h;
		private double		h2;
		String		color;
		List<Node>	nodes;
		
		public Node() {
			nodes = new ArrayList<Node>();
		}
		
		public double geth() {
			return h2;
		}
		
		public String toString() {
			String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i)+",";
				}
				str += nodes.get(i)+")";
			}
			
			return str+name;
		}
		
		public int countLeaves() {
			int total = 0;
			for( Node node : nodes ) {
				total += node.countLeaves();
			}	
			return Math.max( 1, total );
		}
		
		public int countMaxHeight() {
			int val = 0;
			for( Node node : nodes ) {
				val = Math.max( val, node.countMaxHeight() );
			}
			return val+1;
		}
	};
	
	double minh = Double.MAX_VALUE;
	double maxh = 0.0;
	double minh2 = Double.MAX_VALUE;
	double maxh2 = 0.0;
	/*private Node parseTreeRecursive( String str, boolean inverse ) {
		Node ret = new Node();
		Node node = null;
		while( loc < str.length()-1 && str.charAt(loc) != ')' ) {
			loc++;
			char c = str.charAt(loc);
			if( c == '(' ) {
				node = parseTreeRecursive(str, inverse);
				if( inverse ) node.nodes.add( ret );
				else ret.nodes.add( node );
			} else {
				node = new Node();
				int end = loc+1;
				char n = str.charAt(end);
				while( end < str.length()-1 && n != ',' && n != ')' ) {
					n = str.charAt(++end);
				}
				String code = str.substring( loc, end );
				if( code.contains(":") ) {
					String[] split = code.split(":");
					node.name = split[0].replaceAll("'", "");
					if( split.length > 2 ) {
						String color = split[2].substring(1);
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						node.color = "rgb( "+r+","+g+","+b+")"; //new Color( r,g,b );
					} else node.color = null;
					
					String dstr = split[1].trim();
					String dstr2 = "0";
					if( dstr.contains("[") ) {
						int start = split[1].indexOf('[');
						int stop = split[1].indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}
					node.h = Double.parseDouble( dstr );
					node.h2 = Double.parseDouble( dstr2 );
					
					if( node.h < minh ) minh = node.h;
					if( node.h > maxh ) maxh = node.h;
					
					if( node.h2 < minh2 ) minh2 = node.h2;
					if( node.h2 > maxh2 ) maxh2 = node.h2;
				} else {
					node.name = code.replaceAll("'", "");;
				}
				loc = end;
				
				if( inverse ) node.nodes.add( ret );
				else ret.nodes.add( node );
			}
		}
		
		if( loc < str.length()-1 ) {
			loc++;
			int end = loc;
			char n = str.charAt(end);
			while( end < str.length()-1 && n != ',' && n != ';' && n != ')' ) {
				n = str.charAt(++end);
			}
			String code = str.substring( loc, end );
			if( code.contains(":") ) {
				String[] split = code.split(":");
				ret.name = split[0].replaceAll("'", "");;
				if( split.length > 2 ) {
					String color = split[2].substring(1);
					int r = Integer.parseInt( color.substring(0, 2), 16 );
					int g = Integer.parseInt( color.substring(2, 4), 16 );
					int b = Integer.parseInt( color.substring(4, 6), 16 );
					ret.color = "rgb( "+r+","+g+","+b+")"; //new Color( r,g,b );
				} else ret.color = null;
				String dstr = split[1].trim();
				String dstr2 = "0";
				if( dstr.contains("[") ) {
					int start = split[1].indexOf('[');
					int stop = split[1].indexOf(']');
					dstr2 = dstr.substring( start+1, stop );
					dstr = dstr.substring( 0, start );
				}
				try {
					ret.h = Double.parseDouble( dstr );
					ret.h2 = Double.parseDouble( dstr2 );
				} catch( Exception e ) {}
				if( ret.h < minh ) minh = ret.h;
				if( ret.h > maxh ) maxh = ret.h;
				if( ret.h2 < minh2 ) minh2 = ret.h2;
				if( ret.h2 > maxh2 ) maxh2 = ret.h2;
			} else {
				ret.name = code.replaceAll("'", "");
			}
			loc = end;
		}
		
		return inverse ? node : ret;
	}*/
	
	int loc;
	Random r = new Random();
	private Corp recursiveNodeGeneration( List<Corp> corpList, TreeUtil.Node node, TreeUtil.Node parent, int dim ) {
		//int i = node.getName().indexOf("Thermus");
		Corp corp = new Corp( node.getName() ); //i > 0 ? node.getName().substring(i) : node.getName() );
		corp.setCoulomb( 100.0 );
		corp.setx( 400.0*r.nextDouble() );
		corp.sety( 400.0*r.nextDouble() );
		corp.setz( dim == 2 ? 0.0 : 400.0*r.nextDouble() );
		
		if( (node.getNodes() != null && node.getNodes().size() > 0) || node.getName() == null || node.getName().trim().length() == 0 ) {
			corp.setSize( 8 );
		}
		
		/*	corp.color = "#000000";
		} else {*/
			String color = node.getColor();
			if( color != null ) corp.color = color;
			else {
				//TreeUtil.Node parent = node.getParent();
				if( parent != null ) {
					corp.color = parent.getColor();
				}
			}
		//}
		//else corp.color = "#000000";
		//console( "col " + corp.color );
		this.add( corp );
		corpList.add( corp );
		
		for( TreeUtil.Node n : node.getNodes() ) {
			Corp c = recursiveNodeGeneration(corpList, n, node, dim);
			//double val = (1.0/( Math.abs( node.geth() )+0.0005 ))/50.0;
			String strval = Double.toString( n.geth() ); //Math.round(val*100.0)/100.0 );
			corp.addLink(c, strval, 0.02, 100.0*n.geth() );
			c.addLink(corp, strval, 0.02, 100.0*n.geth() );
		}
		
		return corp;
	}
	
	public void importFrom454ContigGraph( String text, double scaleval ) {
		String[] split = text.split("\n");
		
		//Map<String,Corp> corpMap = new HashMap<String,Corp>();
		//Map<String,Integer>	lenMap = new HashMap<String,Integer>();
		//Map<String,Double>	covMap = new HashMap<String,Double>();
		
		List<String[]>	lines = new ArrayList<String[]>();
		for( int i = 0; i < split.length; i++ ) {
			String line = split[i];
			String[] subsplit = line.split("\t");
			if( line.startsWith("C") ) lines.add( subsplit );
			else if( line.contains("contig") ) {
				String str = subsplit[0];
				try {
					Integer.parseInt( str );
					//lenMap.put( str, Integer.parseInt(subsplit[2]) );
					//covMap.put( str, Double.parseDouble(subsplit[3]) );
					
					int size = Integer.parseInt(subsplit[2]);
					double cov = Double.parseDouble(subsplit[3]);
					
					int val = (int)(Math.min(100.0, cov)*255.0/100.0);
					String cc = Integer.toString(val, 16);
					if( cc.length() == 1 ) cc = "0"+cc;
					
					Browser.getWindow().getConsole().log( "ok "+str );
					Corp c1 = new Corp( str );
					c1.setSize( Math.log(size/10.0)*5.0 );
					c1.subcolor = "#"+cc+cc+cc;
					//corpMap.put( str, c1 );
					Connectron.this.add( c1 );
				} catch( Exception e ) {}
			}
		}
		
		Random r = new Random();
		for( String[] spec : lines ) {
			if( spec.length > 1 ) {
				Corp c1 = null;
				if( Corp.corpMap.containsKey(spec[1]) ) {
					c1 = Corp.corpMap.get( spec[1] );
				} /*else {
					c1 = new Corp( spec[1] );
					int size = lenMap.get( spec[1] );
					 cov = lenMap.get( spec[1] );
					c1.setSize( Math.log(size/10.0)*5.0 );
					//corpMap.put( spec[1], c1 );
					Connectron.this.add( c1 );
				}*/
				/*if( c1 == null ) {
					for( String key : corpMap.keySet() ) 
					Browser.getWindow().getConsole().log( "dd " + corpMap.keySet() );
				}*/
				
				c1.setCoulomb(5000.0);
				c1.setx( scaleval*r.nextDouble() );
				c1.sety( scaleval*r.nextDouble() );
				c1.setz( scaleval*r.nextDouble() );
				//c1.color = "#1111ee";
				Connectron.this.add( c1 );
				
				Corp c2 = null;
				if( Corp.corpMap.containsKey(spec[3]) ) {
					c2 = Corp.corpMap.get( spec[3] );
				}/* else {
					c2 = new Corp( spec[3] );
					int size = lenMap.get( spec[3] );
					c2.setSize( Math.log(size/10.0)*5.0 );
					corpMap.put( spec[3], c2 );
					Connectron.this.add( c2 );
				}*/
				
				c2.setCoulomb(50.0);
				c2.setx( scaleval*r.nextDouble() );
				c2.sety( scaleval*r.nextDouble() );
				c2.setz( scaleval*r.nextDouble() );
				//c2.color = "#1111ee";
				
				boolean col1set = c1.color != null && c1.color.length() > 0;
				boolean col2set = c2.color != null && c2.color.length() > 0;
				
				double tscale = 0.0001;
				Double ds = Double.parseDouble( spec[5] );
				if( spec[2].equals("3'") && spec[4].equals("3'") ) {	
					if( col1set && col2set ) {
						if( c1.color.equals("#1111ee") ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals("#1111ee") ) {
							c2.color = "#11ee11";
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = "#1111ee";
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals("#1111ee") ) {
							c1.color = "#11ee11";
						} else {
							c1.color = "#1111ee";
						}
					} else {
						c2.color = "#11ee11";
						c1.color = "#1111ee";
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("3'") && spec[4].equals("5'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals("#1111ee") ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals("#1111ee") ) {
							c2.color = "#11ee11";
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = "#1111ee";
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals("#1111ee") ) {
							c1.color = "#11ee11";
						} else {
							c1.color = "#1111ee";
						}
					} else {
						c2.color = "#11ee11";
						c1.color = "#1111ee";
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("5'") && spec[4].equals("3'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals("#1111ee") ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals("#1111ee") ) {
							c2.color = "#11ee11";
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = "#1111ee";
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals("#1111ee") ) {
							c1.color = "#11ee11";
						} else {
							c1.color = "#1111ee";
						}
					} else {
						c2.color = "#11ee11";
						c1.color = "#1111ee";
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("5'") && spec[4].equals("5'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals("#1111ee") ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = "#ee11ee";
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals("#1111ee") ) {
							c2.color = "#11ee11";
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = "#1111ee";
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals("#1111ee") ) {
							c1.color = "#11ee11";
						} else {
							c1.color = "#1111ee";
						}
					} else {
						c2.color = "#11ee11";
						c1.color = "#1111ee";
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				}
				
				//c1.addLink( c2, spec[5], 0.001*ds, 0.0 );
				//c2.addLink( c1, spec[5], 0.001*ds, 0.0 );
			}
		}
	}
	
	public void importFromTree( String text, int dim ) {
		//u = 50.0;
		this.removeAll();
		
		loc = 0;
		TreeUtil treeutil = new TreeUtil();
		treeutil.init( text, false, null, null, false, null, null, false );
		TreeUtil.Node resultnode = treeutil.getNode();
		//Node resultnode = parseTreeRecursive( text, false );
		
		if( dim == 2 ) mincoulombradius = 1.0;
		List<Corp> corpList = new ArrayList<Corp>();
		recursiveNodeGeneration( corpList, resultnode, null, dim );
		
		repaint();
	}
	
	public void importFromText( String text ) {
		//u = 50000.0;
		this.removeAll();
		
		String[] split = text.split("\n");
		String[] species = split[0].split("\t");
		
		List<Corp> corpList = new ArrayList<Corp>();
		
		Random r = new Random();
		for( String spec : species ) {
			Corp corp = new Corp( spec );
			corp.setx( 400.0*r.nextDouble() );
			corp.sety( 400.0*r.nextDouble() );
			corp.setz( 400.0*r.nextDouble() );
			this.add( corp );
			
			corpList.add( corp );
		}
		
		for( int i = 1; i < split.length; i++ ) {
			String[] subsplit = split[i].split("\t");
			int y = i-1;
			for( int x = 0; x < subsplit.length; x++ ) {
				if( x != y ) {
					double d = Double.parseDouble( subsplit[x] );
					
					Corp corpDst = corpList.get(x);
					Corp corpSrc = corpList.get(y);
					
					corpSrc.addLink( corpDst, subsplit[x], d, 0.0 );
				}
			}
		}
		
		repaint();
	}
	
	public void importFromMatrix( final String text, double scaleval ) {
		this.removeAll();
		
		String[] split = text.split("\n");
		//String[] persons = split[0].split("\t");
		
		List<Corp> corpList = new ArrayList<Corp>();
		
		//double mulval = mult.getValue();
		//double scaleval = scale.getValue();
		
		List<String[]>	lines = new ArrayList<String[]>();
		for( int i = 1; i < split.length; i++ ) {
			lines.add( split[i].split("\t") );
		}
		
		Random r = new Random();
		for( String[] spec : lines ) {
			if( spec.length > 1 ) {
				Corp corp = new Corp( spec[0] );
				corp.setCoulomb(100.0);
				corp.setx( scaleval*r.nextDouble() );
				corp.sety( scaleval*r.nextDouble() );
				corp.setz( scaleval*r.nextDouble() );
				corp.color = "#1111ee";
				Connectron.this.add( corp );
				
				corpList.add( corp );
			}
		}
		
		int i = 0;
		for( String[] spec : lines ) {
			//int y = i-1;
			//String spec = subsplit[0];
			Corp corp = corpList.get(i); //new Corp( spec );
			/*corp.setx( 400.0*r.nextDouble() );
			corp.sety( 400.0*r.nextDouble() );
			corp.setz( 400.0*r.nextDouble() );
			corp.color = "#1111ee";
			this.add( corp );
			corpList.add( corp );*/
			
			for( int x = 0; x < spec.length-1; x++ ) {
				double d = Double.parseDouble( spec[x+1] );
				if( i != x && d > 0.0 ) {
					//Corp corpDst = corpList.get(x);
					//Corp corpSrc = corpList.get(y);
					
					Corp pcorp = corpList.get(x);
					corp.addLink( pcorp, d+"", 0.01, 10000.0*d );
					//pcorp.addLink( corp, subsplit[x], d*mulval );
				}
			}
			i++;
		}
		
		console( "c size: " + corpList.size() );
		
		repaint();
	}
	
	public void oldImportFromMatrix( final String text ) {
		DialogBox	db = new DialogBox( true );
		
		Grid		grid = new Grid(3,2);
		final DoubleBox	colomb = new DoubleBox();
		colomb.setValue( 50.0 );
		Label		colabel = new Label( "Coulomb: " );
		final DoubleBox	scale = new DoubleBox();
		scale.setValue( 400.0 );
		Label		scabel = new Label( "Scale: " );
		final DoubleBox	mult = new DoubleBox();
		mult.setValue( 1.0 );
		Label		mulabel = new Label( "Multiplier: " );
		
		grid.setWidget(0, 0, colabel);
		grid.setWidget(0, 1, colomb);
		grid.setWidget(1, 0, scabel);
		grid.setWidget(1, 1, scale);
		grid.setWidget(2, 0, mulabel);
		grid.setWidget(2, 1, mult);
		db.add( grid );
		db.center();
		
		db.addCloseHandler( new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				//su = colomb.getValue();
				importFromMatrix(text, scale.getValue());
			}
		});
	}
	
	long last = 0;
	public boolean isVisible() {
		return super.isVisible() && birta;
	}
	
	boolean painting = false;
	public void paintComponent( Context2d g ) {
		if( !painting ) {
			painting = true;
			if( !shift ) {
				birta = false;
				if( toggle ) {
					updateCenterOfMass();
					spring();
				}
				depth();
				birta = true;
			}
			
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			
			/*String	oldFont = g.getFont();
			if( oldFont.getSize() != 7 ) {
				oldFont = oldFont.deriveFont(8.0f);
				g2.setFont( oldFont );
			}*/
			if( drawLinks ) {
				g.setStrokeStyle("#000000");
				
				for(Corp corp : this.getComponents() ) {
					//Rectangle inrect = new Rectangle( this.getHorizontalScrollPosition(), this.getVerticalScrollPosition(), this.getWidth(), this.getHeight() ); //this.getVisibleRect();
					Rectangle inrect = new Rectangle( 0, 0, this.getWidth(), this.getHeight() ); //this.getVisibleRect();
					for( Corp cc : corp.getLinks() ) {
						double x1 = corp.getX()+corp.getWidth()/2;
						double y1 = corp.getY()+corp.getHeight()/2;
						double x2 = cc.getX()+cc.getWidth()/2;
						double y2 = cc.getY()+cc.getHeight()/2;
						
						if( cc.depz > 0.0 && (inrect.contains(x1, y1) || inrect.contains(x2, y2)) ) {
							g.setFillStyle( "#777777" );
							
							g.beginPath();
							g.moveTo( x1, y1 );
							g.lineTo( x2, y2 );
							g.closePath();
							g.stroke();
							
							/*double h1 = Math.atan2( y2 - y1, x2 - x1 );
							double h2 = h1 + Math.PI/8.0;
							int x3 = (int)( x2 - cc.getWidth()*Math.cos(h1)/2.0 );
							int y3 = (int)( y2 - cc.getHeight()*Math.sin(h1)/2.0 );
							
							//int offx = (x2 - x3)/5;
							//int offy = (y2 - y3)/5;
							//System.err.println( offx + "  " + offy );
							g.drawLine( x3, y3, (int)(x3 - 10.0*Math.cos( h2 )), (int)(y3 - 10.0*Math.sin( h2 )) );*/
							
							if( drawLinkNames && !toggle && px == -1 ) {
								Set<String> strset = corp.connections.get(cc).linkTitles;
								double x = (x1+x2)/2;
								double y = (y1+y2)/2;
								double t = Math.atan2( y2-y1, x2-x1 );
								
								g.translate(x, y);
								g.rotate(t); //, x, y);
								int k = 0;
								g.setFillStyle( "#000000" );
								for( String str : strset ) {
									if( !str.equals("link") ) {
										TextMetrics tm = g.measureText( str );
										double strw = tm.getWidth();
										//if( corp.selectedLink != null ) System.err.println( corp.selectedLink );
										/*if( cc == linkCorp2 && str.equals( corp.selectedLink ) ) {
											g2.setFont( oldFont.deriveFont( Font.BOLD ) );
										}*/
										g.fillText( str, -strw/2, -5-k );
										//if( g2.getFont() != oldFont ) g2.setFont( oldFont );
										k += 10;
									}
								}
								g.rotate(-t); //, x, y);
								g.translate(-x, -y);
							}
						}
					}
				}
			}
		
			g.setFillStyle( "#000000" );
			for( Corp c : this.getComponents() ) {
				Corp corp = (Corp)c;
				TextMetrics tm = g.measureText( corp.getName() );
				double strWidth = tm.getWidth();
				//if( (corp.type.equals("person") && drawNodeNames) || (corp.type.equals("corp") && drawCorpNames) ) {
				if( drawNodeNames ) {
					if( corp.getName().length() > 50 ) {
						g.fillText( corp.getName().substring(0, 50), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
					} else {
						g.fillText( corp.getName(), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
					}
				}
			}
			
			Corp c = drag;
			if( c != null && !c.dragging && c.px != -1 ) {
				g.setStrokeStyle("#000000");
				g.beginPath();
				g.moveTo( c.getX()+c.getWidth()/2, c.getY()+c.getHeight()/2 );
				//console( "erm " + c.px + "  " + c.py );
				g.lineTo( c.pxs, c.pys );
				g.closePath();
				g.stroke();
			}
			
			for( Corp corp : this.getComponents() ) {
				g.translate( corp.getX(), corp.getY() );
				corp.paintComponent( g );
				g.translate( -corp.getX(), -corp.getY() );
			}
			
			if( selRect != null ) {
				//g.setFillStyle( selColor );
				g.setStrokeStyle( "#000000" );
				g.strokeRect( selRect.x, selRect.y, selRect.width, selRect.height );
			}
			
			/*if( toggle ) {
				long val = System.currentTimeMillis();
				long diff = val - last;
				val = last;
				
				if( diff < 100 ) {
					Timer t = new Timer() {
						@Override
						public void run() {
							painting = false;
						}
					};
					t.schedule( (int)(100-diff) );
				} else painting = false;
				this.repaint();
			} else*/
			painting = false;
		}
	}
	
	PopupPanel	popup;
	public void initGUI() {				
		Corp.prop = new Prop();
		Corp.prop.setBounds(0, 0, 400, 75);
		
		//int w = Window.getClientWidth();
		//int h = Window.getClientHeight();
		
		canvas = Canvas.createIfSupported();
		//canvas.setSize("100%", "100%");
		
		canvas.addDoubleClickHandler( this );
		canvas.addMouseDownHandler( this );
		canvas.addMouseUpHandler( this );
		canvas.addMouseMoveHandler( this );
		canvas.addKeyDownHandler( this );
		canvas.addKeyUpHandler( this );
		canvas.addKeyPressHandler( this );
		
		canvas.addDragHandler( this );
		canvas.addDragEnterHandler( this );
		canvas.addDragLeaveHandler( this );
		canvas.addDragOverHandler( this );
		canvas.addDropHandler( this );
		//canvas.addKeyPressHandler( this );
		
		/*final DataFlavor df = new DataFlavor("text/plain;charset=utf-8");
		
		final Transferable transferable = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
				StringBuilder ret = new StringBuilder();				
				return new ByteArrayInputStream( ret.toString().getBytes() );
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { df };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor arg0) {
				if( arg0.equals(df) ) {
					return true;
				}
				return false;
			}
		};
		
		TransferHandler th = new TransferHandler() {
			private static final long serialVersionUID = 1L;
			
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			protected Transferable createTransferable(JComponent c) {
				return transferable;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				try {
					Object obj = support.getTransferable().getTransferData( df );
					InputStream is = (InputStream)obj;
					
					byte[] bb = new byte[2048];
					int r = is.read(bb);
					
					importFromText( new String(bb,0,r) );
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		c.setTransferHandler( th );*/
		
		loadAll();
		//canvas.setPixelSize( this.getWidth(), this.getHeight() );
		
		//popup = new PopupPanel( true );
		final MenuBar	menu = new MenuBar( true );
		//popup.add( menu );
		
		menu.addItem( "Find node", new Command() {
			@Override
			public void execute() {
				DialogBox	db = new DialogBox( true );
				db.setTitle("Find node");
				HorizontalPanel	hp = new HorizontalPanel();
				final TextBox	tb = new TextBox();
				tb.addKeyPressHandler( new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						char c = event.getCharCode();
						if( c == '\r' ) {
							String text = tb.getText();
							for( Corp cp : Connectron.this.getComponents() ) {
								if( text.equals( cp.getName() ) ) {
									cp.setSelected( true );
								}
							}
						}
					}
				});
				hp.add( tb );
				db.add( hp );
				db.center();
			}
		});
		menu.addItem( "Show/Hide Links", new Command() {
			@Override
			public void execute() {
				drawLinks = !drawLinks;
				if( popup != null ) popup.hide();
				repaint();
			}
		});
		menu.addItem( "Show/Hide Links Names", new Command() {
			@Override
			public void execute() {
				drawLinkNames = !drawLinkNames;
				if( popup != null ) popup.hide();
				repaint();
			}
		});
		menu.addItem( "Show/Hide Node Names", new Command() {
			@Override
			public void execute() {
				drawNodeNames = !drawNodeNames;
				if( popup != null ) popup.hide();
				repaint();
			}
		});
		menu.addSeparator();
		menu.addItem("Distance matrix", new Command() {
			@Override
			public void execute() {
				DialogBox	db = new DialogBox(true);
				TextArea	ta = new TextArea();
				ta.setSize("800px", "600px");
				
				String dmatrix = "";
				for( Corp c : Corp.corpList ) {
					if( dmatrix.length() == 0 ) dmatrix += c.getName();
					else dmatrix += "\t" + c.getName();
				}
				dmatrix += "\n";
				for( Corp c1 : Corp.corpList ) {
					for( Corp c2 : Corp.corpList ) {
						if( dmatrix.charAt( dmatrix.length()-1 ) == '\n' ) dmatrix += c1.distance( c2 );
						else dmatrix += "\t" + c1.distance( c2 );
					}
					dmatrix += "\n";
				}
				ta.setText( dmatrix );
				
				db.add( ta );
				db.center();
			}
		});
		menu.addSeparator();
		menu.addItem( "Enable/Disable autosave", new Command() {
			@Override
			public void execute() {
				Corp.autosave = !Corp.autosave;
				
				if( popup != null ) popup.hide();
			}
		});
		menu.addSeparator();
		menu.addItem( "Add Node", new Command() {
			@Override
			public void execute() {
				double z = 0.0;
				for( Corp corp : Corp.corpList ) {
					z += corp.depz;
				}
				if( Corp.corpList.size() > 0 ) z /= Corp.corpList.size();
				//System.err.println( "len "+ Corp.corpList.size() );
				
				Corp 		corp = new Corp(Corp.getCreateName(),"unknown",mx-32/2, my-32/2);
				
				//backtest( m.x, m.y );
				
				//System.err.println( m );
				backtrack( mx, my, z, corp );
				corp.save();
				add( corp );
				
				if( popup != null ) popup.hide();
				repaint();
			}
		});
		menu.addItem( "Select all", new Command() {
			@Override
			public void execute() {
				selectAll();
				if( popup != null ) popup.hide();
			}
		});
		menu.addItem( "Invert selection", new Command() {
			@Override
			public void execute() {
				invertSelection();
				if( popup != null ) popup.hide();
			}
		});
		menu.addItem( "Flatten", new Command() {
			@Override
			public void execute() {
				/*double len = Corp.corpList.size();
				double lcx = 0.0;
				double lcy = 0.0;
				double lcz = 0.0;
				for( Corp rel : Corp.corpList ) {
					lcx += rel.getx();
					lcy += rel.gety();
					lcz += rel.getz();
				}
				lcx /= len;
				lcy /= len;
				lcz /= len;*/
				
				for( Corp rel : Corp.corpList ) {
					Browser.getWindow().getConsole().log( "setz" );
					rel.setz( 0.0 );
				}
			}
		});
		/*popup.add( new AbstractAction("Add Corp") {
			@Override
			public void execute() {
				Corp 		corp = new Corp("unknown","corp",m.x,m.y);
				try {
					corp.save();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});*/
		menu.addSeparator();
		
		menu.addItem( "Load sample data", new Command() {
			@Override
			public void execute() {
				/*InputStream is = this.getClass().getResourceAsStream("/Greining2.xlsx");
				try {
					excelLoad( is );
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
				if( popup != null ) popup.hide();
			}
		});
		
		/*popup.add( new AbstractAction("Import from dirty Excel") {
			@Override
			public void execute() {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						excelLoad( new FileInputStream( f ) );
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
		});*/
		menu.addItem( "Spring Graph", new Command() {
			@Override
			public void execute() {
				if( !toggle ) {
					Timer t = springThread();
					t.scheduleRepeating( 100 );
				}
				
				toggle = !toggle;
				
				if( popup != null ) popup.hide();
				
				canvas.setFocus( true );
				repaint();
			}
		});		
		//scrollpane.setViewportView( c );
		
		final MenuBar filemenu = new MenuBar(true);
		filemenu.addItem( "Open", new Command() {
			@Override
			public void execute() {
				openFileDialog( 0 );
			}
		});
		
		final MenuBar viewmenu = new MenuBar(true);
		viewmenu.addItem( "Swap colors", new Command() {
			@Override
			public void execute() {
				swapAllColors();
			}
		});
		
		MenuBar	menubar = new MenuBar();
		menubar.addItem("File", filemenu);
		menubar.addItem("View", viewmenu);
		menubar.addItem("Options", menu);
		this.add( menubar );
		this.add( canvas );
	}

	int		mx = 0;
	int		my = 0;
	public void mouseDragged(MouseMoveEvent e) {
		//console("erm");
		
		npx = e.getX();
		npy = e.getY();
		if( shift ) {
			selRect = new Rectangle( Math.min(px, npx), Math.min(py, npy), Math.abs(px-npx), Math.abs(py-npy) );
			
			/*Rectangle rect = c.getVisibleRect();			
			rect.translate( p.x-np.x, p.y-np.y );
			c.scrollRectToVisible( rect );*/
		}
		repaint();
	}
	
	public void saveAll() {
		for( Corp c : this.getComponents() ) {
			Corp corp = (Corp)c;
			corp.save();
		}
	}
	
	public void reloadAll() {
		for( Corp c : this.getComponents() ) {
			Corp corp = (Corp)c;
			corp.load();
		}
	}
	
	public void loadAll() {
		boolean succ = false;
		/*try {
			PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
			 
			String[] names = ps.getNames( bs.getCodeBase() );
			for( String name : names ) {
				if( name.startsWith("spoil_") ) {
					Corp corp = new Corp( name.substring(6) );
					c.add( corp );
				}
			}
			
			//URL url = new URL( bs.getCodeBase().toString() + "spoil_" + this.getName() );
			//ps.create( url, 1000 );
			//FileContents fc = ps.get( url );
			//InputStream is = fc.getInputStream();
			//InputStreamReader isr = new InputStreamReader( is );
			//loadFile( isr, bs.getCodeBase().toString() );
			
			succ = true;
		} catch( NoClassDefFoundError e ) {
			e.printStackTrace();
		} catch( Exception e ) {
			e.printStackTrace();
		}*/
		
		/*if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( dir.exists() ) {
				File[] ff = dir.listFiles();
				for( File f : ff ) {
					if( !f.isDirectory() ) {
						Corp corp = new Corp( f.getName() );
						add( corp );
					}
					//addFile( f );
				}
			}
		}*/
			
		for( String name : Corp.corpMap.keySet() ) {
			Corp c = Corp.corpMap.get( name );
			c.load();
		}
	}
	
	public void addCorp( String name ) {
		add( load( name ) );
	}
	
	/*public void addFile( File save ) {
		add( loadFile( save ) );
	}*/
	
	public Corp load( String name ) {
		Corp corp = new Corp( name );
		corp.load();
		return corp;
	}
	
	/*public Corp loadFile( File save ) {
		String name = save.getName();
		Corp corp = new Corp( name );
		//corp.loadFile( new FileReader(save), new File( save.getParentFile(), "images" ).toURI().toURL().toString() );
		return corp;
	}*/

	public void mouseMoved(MouseMoveEvent e) {
		mx = e.getX();
		my = e.getY();
	}
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;

	public void mousePressed(MouseDownEvent e, int x, int y, int nativebutton ) {
		toggle = false;
		Corp c = this.getComponentAt( x, y );
		if( c == null ) {
			px = x;
			py = y;
			npx = px;
			npy = py;
			shift = e.isShiftKeyDown();
			
			if( nativebutton == NativeEvent.BUTTON_RIGHT ) {
				mousedown = false;
				//popup.setPopupPosition(e.getX(), e.getY());
				//popup.show();
			} else {
				requestFocus();
				
				Corp.selectedList.clear();
				linkCorp = null;
				linkCorp2 = null;
				for( Corp comp : getComponents() ) {
					Corp corp = ((Corp)comp);
					corp.selected = false;
					corp.selectedLink = null;
					corp.vx = 0;
					corp.vy = 0;
					corp.vz = 0;
					for( Corp corp2 : corp.connections.keySet() ) {						
						double x1 = corp.getX() + corp.getWidth()/2;
						double x2 = corp2.getX() + corp2.getWidth()/2;
						double y1 = corp.getY() + corp.getHeight()/2;
						double y2 = corp2.getY() + corp2.getHeight()/2;
						double xx = (x1 + x2) / 2.0;
						double yy = (y1 + y2) / 2.0;
						
						double h1 = Math.atan2( x1-xx, y1-yy );
						//if( h1 < 0 ) h1 += 2*Math.PI;
						double h2 = Math.atan2( px-xx, py-yy );
						//if( h2 < 0 ) h2 += 2*Math.PI;
						double h = h2 - h1;
						if( h > Math.PI ) h -= 2*Math.PI;
						if( h < -Math.PI ) h += 2*Math.PI;
						
						if( h < 0 ) {
							Set<String>	strset = corp.connections.get(corp2).linkTitles;
							double xxx = xx - px;
							double yyy = yy - py;
							if( strset != null && strset.size() > 0 && Math.sqrt( xxx*xxx + yyy*yyy ) < 32 ) {
								linkCorp = corp;
								linkCorp2 = corp2;
								
								//System.err.println("found link " + linkCorp.getName() + "  " + linkCorp2.getName() );
								corp.selectedLink = corp.connections.get(corp2).linkTitles.iterator().next();
								
								break;
							}
						}
						if( linkCorp != null ) break;
					}
				}
				//c.remove( Corp.textfield );
				if( Corp.prop != null ) {
					Corp.prop.currentCorp = null;
					remove( Corp.prop );
				}
				repaint();
			}
		} else {
			//if( dcorp != null ) {
			//	dcorp.addLink( c );
			//	dcorp = null;
			//} else {
			c.mousePressed( e, x, y, e.isShiftKeyDown(), false );
		}
	}
	
	Set<Prop>	properties = new HashSet<Prop>();
	public void add( Prop p ) {
		properties.add( p );
	}
	
	public void remove( Prop p ) {
		properties.remove( p );
		
		if( p.currentCorp != null ) {
			p.currentCorp.setName( p.name.getText() );
			p.currentCorp.type = p.kt.getText();
			p.currentCorp.text = p.text.getText();
			p.currentCorp.color = p.color;
			
			for( Corp corp : Corp.corpList ) {
				if( corp != p.currentCorp && corp.type.equals( p.currentCorp.type ) ) {
					corp.color = p.currentCorp.color;
				}
			}
			
			//if( p.currentCorp.text.length() > 0 ) p.currentCorp.setToolTipText( p.currentCorp.text );
			p.currentCorp.save();
		}
	}
	
	Set<Corp>	components = new HashSet<Corp>();
	public Collection<Corp> getComponents() {
		return components;
	}
	
	public void add( Corp c ) {
		components.add( c );
		c.setParent( this );
	}
	
	public void remove( Corp c ) {
		c.delete();
		//c.setParent( null );
	}
	
	public void swapAllColors() {
		for( Corp c : Corp.corpList ) {
			c.swapColors();
		}
	}
	
	public void removeAll() {
		components.clear();
		Corp.corpList.clear();
		Corp.corpMap.clear();
	}
	
	public Corp getComponentAt( int pxs, int pys ) {
		for( Corp c : components ) {
			if( c.getBounds().contains( pxs, pys ) ) return c;
		}
		
		return null;
	}

	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		Corp c = this.getComponentAt( x, y );
		if( c == null ) {
			if( selRect != null ) {
				for( Corp cc : this.getComponents() ) {
					Corp corp = (Corp)cc;
					if( corp.getBounds().intersects(selRect) ) {
						corp.selected = true;
						Corp.selectedList.add(corp);
					}
					else corp.selected = false;
				}
				selRect = null;
			}
			
			if( px != -1 && !shift ) {
				double d = (npx - px) / 100.0 + hhy + Math.PI;
				double h = Math.floor( d / (2.0*Math.PI) );
				double nd = d - h * (2.0*Math.PI) - Math.PI;
				hhy = nd; //(np.x - p.x) / 100.0 + hhy;
				/*} else {
					double d = (np.x - p.x) / 100.0 + hhz + Math.PI;
					double h = Math.floor( d / (2.0*Math.PI) );
					double nd = d - h * (2.0*Math.PI) - Math.PI;
					hhz = nd;
				}*/
				
				d = (npy - py) / 100.0 + hhx + Math.PI;
				h = Math.floor( d / (2.0*Math.PI) );
				nd = d - h * (2.0*Math.PI) - Math.PI;
				hhx = nd;
			}
			
			shift = false;
			px = -1;
			py = -1;
			repaint();
		} else {
			c.mouseReleased( e, x, y, e.isShiftKeyDown(), false, drag );
		}
	}

	public void keyPressed( int keychar, int keycode ) {
		if( keycode == 107 || keycode == 187 /*+*/ ) {
			zoomval -= 100;
		} else if( keycode == 109 || keycode == 189 /*-*/ ) {
			zoomval += 100;
		} else if( keycode == 106 || keycode == 56 /* * */ ) {
			dzoomval -= 100;
		} else if( keycode == 111 || keycode == 55 /*/*/ ) {
			dzoomval += 100;
		} else if( keycode == 188 /*,*/ ) {
			dsize *= 0.8;
		} else if( keycode == 190 /*.*/ ) {
			dsize *= 1.25;
		} else if( keychar == ' ' /* */ ) {
			updateCenterOfMass();
		} else if( linkCorp != null ) {
			Browser.getWindow().getConsole().log("mu");
			if( keycode == KeyCodes.KEY_DELETE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( strset.size() == 0 ) linkCorp.connections.remove( linkCorp2 );
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( keycode == KeyCodes.KEY_ENTER ) {
				linkCorp.save();
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( keycode == KeyCodes.KEY_BACKSPACE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.length() > 0 ) {
					linkCorp.selectedLink = linkCorp.selectedLink.substring(0, linkCorp.selectedLink.length()-1);
					strset.add( linkCorp.selectedLink );
				} /*else if( strset.size() == 0 ) {
					linkCorp.connections.remove( linkCorp2 );
				}*/
			} else if( keycode != KeyCodes.KEY_ALT && keycode != KeyCodes.KEY_CTRL && keycode != KeyCodes.KEY_SHIFT ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.equals("link") ) linkCorp.selectedLink = "";
				linkCorp.selectedLink += (char)keycode;
				strset.add( linkCorp.selectedLink );
			}
		} else {
			if( keycode == KeyCodes.KEY_DELETE ) {
				Set<Corp>	delset = new HashSet<Corp>();
				for( Corp c : Corp.corpList ) {
					//Corp c = Corp.corpMap.get(name);
					if( c.selected ) {
						delset.add( c );
					}
				}
				
				for( Corp c : delset ) {
					c.delete();
				}
			}
		}
		
		repaint();
	}
	
	/*public static void main(String[] args) {
		Connectron cntr = new Connectron();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setSize(800, 600);
		try {
			cntr.initGUI( f );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		f.add( cntr.scrollpane );
		f.setVisible(true);
	}*/

	@Override
	public void onKeyUp(KeyUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keychar = event.getNativeEvent().getCharCode();
		int keycode = event.getNativeKeyCode();
		
		keyPressed( keychar, keycode );
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		int x = event.getX();
		int y = event.getY();
		
		if( drag != null ) {
			if( mousedown ) {
				drag.mouseDragged( event, x, y, event.isShiftKeyDown() );
			} //else Corp.drag.mouseMoved( event, x, y, event.isShiftKeyDown(), true );
		} else {
			if( mousedown ) mouseDragged( event );
			else mouseMoved( event );
		}
	}

	Corp	drag = null;
	boolean mousedown = false;
	@Override
	public void onMouseUp(MouseUpEvent event) {
		//console( "mouseup" );
		
		mousedown = false;
		
		int x = event.getX();
		int y = event.getY();
		
		if( drag != null ) {
			Corp c = drag;
			drag = null;
			c.mouseReleased( event, x, y, event.isShiftKeyDown(), true, c );
		}
		mouseReleased( event );
	}

	@Override
	public void onMouseDown(MouseDownEvent e) {
		//console( "mousedown" );
		
		int x = e.getX();
		int y = e.getY();
		
		if( mousedown ) {			
			if( drag != null ) {
				Corp c = drag;
				drag = null;
				c.mouseReleased( e, x, y, e.isShiftKeyDown(), false, c );
			}
		} else {
			mousedown = true;
			
			Corp c = this.getComponentAt( x, y );
			if( c != null ) {
				drag = c;
				c.mousePressed( e, x, y, e.isShiftKeyDown(), false );
			}
			else mousePressed( e, x, y, e.getNativeButton() );
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		int keychar = event.getCharCode();
		int keycode = event.getNativeEvent().getKeyCode();
		
		keyPressed( keychar, keycode );
	}
	
	public void handleText( String dropstuff ) {
		console( dropstuff.substring( 0, Math.min( 100,dropstuff.length() ) ) );
		char c = dropstuff.charAt( 0 );
		if( dropstuff.contains("contig") ) {
			importFrom454ContigGraph( dropstuff, 400.0 );
		} else if( c >= '0' && c <= '9' && dropstuff.charAt(1) == '(' ) {
			importFromTree( dropstuff.substring(1).replaceAll("[\r\n]+", ""), c-'0' );
		} else if( dropstuff.startsWith("(") ) {
			importFromTree( dropstuff.replaceAll("[\r\n]+", ""), 3 );
		} else if( dropstuff.startsWith("\t") ) {
			importFromMatrix( dropstuff, 400.0 );
		} else if( !dropstuff.startsWith("{") && !dropstuff.startsWith("(") ) importFromText( dropstuff );
	}
	
	public native String handleFiles( Element ie, int append ) /*-{
		var hthis = this;
		file = ie.files[0];
		var reader = new FileReader();
		reader.onload = function(e) {
			hthis.@org.simmi.client.Connectron::handleText(Ljava/lang/String;)(e.target.result);
		};
		reader.onerror = function(e) {
	  		$wnd.console.log("error", e);
	  		$wnd.console.log(e.getMessage());
		};
		reader.readAsText( file, "utf8" );
	}-*/;
	
	public native void handle454Graph( com.google.gwt.dom.client.InputElement fel ) /*-{
		var s = this;
		var file = fel.files(0);
		var fr = new FileReader();
		fr.onload = function(e) {
			hthis.@org.simmi.client.Connectron::handleText(Ljava/lang/String;)(e.target.result);
		};
		fr.onerror = function(e) {
	  		$wnd.console.log("error", e);
	  		$wnd.console.log(e.getMessage());
		};
		reader.readAsText( file, "utf8" );
	}-*/;
	
	public void openFileDialog( final int append ) {
		final DialogBox	db = new DialogBox();
		db.setText("Open file ...");
		
		final FormPanel	form = new FormPanel();
		form.setAction("/");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    
		//form.add( vp );*/
		final FileUpload	file = new FileUpload();
		file.getElement().setId("fasta");
	
		form.add( file );
		db.add( form );
		/*file.fireEvent( GwtEvent)
		
		//SubmitButton	submit = new SubmitButton();	
		//submit.setText("Open");*/
		
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				//if( file.getName().equals("454ContigGraph.txt") ) {
				//	handle454Graph( file.getElement() );
				//} else 
				handleFiles( file.getElement(), append );
				db.hide();
			}
		});
		
		db.setAutoHideEnabled( true );
		db.center();
		//file.fireEvent( new ClickEvent(){} );
		click( file.getElement() );
	}
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;

	@Override
	public void onDoubleClick(DoubleClickEvent e) {
		int x = e.getX();
		int y = e.getY();
		Corp c = this.getComponentAt( x, y );
		if( c != null ) {
			mousedown = true;
			drag = c;
			c.mousePressed(e, x, y, e.isShiftKeyDown(), true);
		} else {
			openFileDialog( 0 );
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		String dropstuff = event.getData( "text/plain;charset=utf-8" );
		handleText( dropstuff );
	}

	@Override
	public void onDragOver(DragOverEvent event) {}

	@Override
	public void onDragLeave(DragLeaveEvent event) {}

	@Override
	public void onDragEnter(DragEnterEvent event) {}

	@Override
	public void onDrag(DragEvent event) {}
}
