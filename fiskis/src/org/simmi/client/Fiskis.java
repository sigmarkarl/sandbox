package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Fiskis implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public interface Drawable {
		public void draw( Context2d ctx );
	};
	
	public class Rectangle {
		public Rectangle( double x, double y, double w, double h ) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		double	x, y, w, h;
		
		public boolean isInside( double x, double y ) {
			return x >= this.x && x <= this.x+16 && y >= this.y && y <= this.y+h;
		}
	}
	
	public interface Clickable {
		public void click( double x, double y );
		public Rectangle getBounds();
	};
	
	public class Meter implements Drawable, Clickable {
		public Meter( String label, double x, double y, double h, double start, double stop, double jump ) {
			this.label = label;
			this.start = start;
			this.stop = stop;
			this.h = h;
			this.x = x;
			this.y = y;
			this.val = start;
			this.jump = jump;
		}
		
		double	start, stop;
		double	h;
		double	x, y;
		double 	val;
		String	label;
		double	jump;
		
		public void draw( Context2d ctx ) {
			ctx.clearRect(x-8, y-20, 32, h+40);
			
			ctx.setFillStyle("#000000");
			ctx.beginPath();
			ctx.moveTo(x, y);
			ctx.lineTo(x+16, y);
			ctx.closePath();
			ctx.stroke();
			
			ctx.beginPath();
			ctx.moveTo(x+8, y);
			ctx.lineTo(x+8, y+h);
			ctx.closePath();
			ctx.stroke();
			
			ctx.beginPath();
			ctx.moveTo(x, y+h);
			ctx.lineTo(x+16, y+h);
			ctx.closePath();
			ctx.stroke();
			
			ctx.beginPath();
			ctx.arc(x+8, y+h-(h*(val-start))/(stop-start), 5.0, 0.0, 2*Math.PI );
			ctx.closePath();
			ctx.stroke();
			
			double tw = ctx.measureText( label ).getWidth();
			ctx.fillText( label, x+(16-tw)/2, y+h+12 );
			
			String sval = val+"";
			double vw = ctx.measureText( sval ).getWidth();
			ctx.fillText( sval, x+(16-vw)/2, y-3 );
		}

		@Override
		public void click(double x, double y) {
			double retval = start + (h-y)*(stop-start)/h;
			retval = Math.round( retval*jump )/jump;
			val = Math.max( start, Math.min( stop, retval ) );
		}

		@Override
		public Rectangle getBounds() {
			return new Rectangle( x, y, 16, h );
		}
	}
	
	List<Drawable>	dlist = new ArrayList<Drawable>();
	List<Clickable>	clist = new ArrayList<Clickable>();
	
	public void draw( Context2d ctx ) {
		for( Drawable d : dlist ) {
			d.draw( ctx );
		}
	}
	
	Clickable	currentclick;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		Canvas		c = Canvas.createIfSupported();
		c.setSize("800x", "600px");
		c.setCoordinateSpaceWidth( 800 );
		c.setCoordinateSpaceHeight( 600 );
		
		dlist.add( new Meter( "dagar", 20, 20, 500.0, 0.0, 7.0, 1.0 ) );
		dlist.add( new Meter( "hiti", 60, 20, 500.0, 0.0, 10.0, 10.0 ) );
		
		for( Drawable d : dlist ) {
			if( d instanceof Clickable ) clist.add( (Clickable)d );
		}
		
		final Context2d ctx = c.getContext2d();
		draw( ctx );
		
		c.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				double x = event.getX();
				double y = event.getY();
				
				for( Clickable c : clist ) {
					Rectangle b = c.getBounds();
					if( b.isInside( x, y ) ) {
						currentclick = c;
						c.click( x-b.x, y-b.y );
						if( c instanceof Drawable ) ((Drawable)c).draw( ctx );
					}
				}
			}
		});
		
		c.addMouseMoveHandler( new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if( currentclick != null ) {
					double x = event.getX();
					double y = event.getY();
			
					Rectangle b = currentclick.getBounds();
					currentclick.click( x-b.x, y-b.y );
					if( currentclick instanceof Drawable ) ((Drawable)currentclick).draw( ctx );
				}
			}
		});
		
		c.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				currentclick = null;
			}
		});
		rp.add( c );
	}
}
