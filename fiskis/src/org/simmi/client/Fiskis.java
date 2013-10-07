package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	
	public interface ChangeListener {
		public void onChange( double oldval, double newval );
		public void setDrawable( Drawable d );
		public Drawable getDrawable();
	}
	
	public interface Clickable {
		public void click( double x, double y );
		public Rectangle getBounds();
		public void addChangeListener( ChangeListener c );
	};
	
	public class Kassi implements Drawable {
		double 			x,y,w,h;
		private double 	hiti;
		private double  umh;
		private double 	dagar;
		Image			wallpaper;
		
		public Kassi( double x, double y, double w, double h, Image wallpaper ) {
			this.wallpaper = wallpaper;
			
			this.w = w;
			this.h = h;
			this.x = x;
			this.y = y;
		}
		
		private void drawKassi(Context2d ctx, double ratio ) {
			ctx.setStrokeStyle("#000000");
			ctx.setFillStyle("#FFFF00");
			ctx.beginPath();
			ctx.moveTo(x+40, y+50);
			ctx.lineTo(x+40, y+350);
			ctx.lineTo(x+450, y+350);
			ctx.lineTo(x+450, y+50);
			ctx.lineTo(x+445, y+50);
			ctx.lineTo(x+445, y+345);
			ctx.lineTo(x+45, y+345);
			ctx.lineTo(x+45, y+50);
			ctx.lineTo(x+40, y+50);
			ctx.closePath();
			ctx.stroke();
			ctx.fill();
			
			//ctx.setFillStyle("#FFFEFE");
			ctx.setFillStyle("#2299FF");
			ctx.fillRect(x+45.5, y+100.0, 399, ratio*245.0);
			ctx.setLineWidth(0.5);
			ctx.strokeRect(x+45.5, y+100.0, 399, ratio*245.0);
			
			/*ctx.setFillStyle("#2299FF");
			ctx.fillRect(x+45.5, y+100.0+ratio*245.0, 399, 245.0-ratio*245.0);
			ctx.setLineWidth(0.5);
			ctx.strokeRect(x+45.5, y+100.0+ratio*245.0, 399, 245.0-ratio*245.0);*/
			
			double w = Math.max( 85.0, wallpaper.getWidth()*0.2 );
			double h = Math.max( 35.2, wallpaper.getHeight()*0.2 );
			
			ctx.save();
			ctx.beginPath();
			ctx.rect( x+45.5, y+100.5, 398.0, 243.0 );
			ctx.closePath();
			ctx.clip();
			
			for( double xx = x+45.5; xx < x+46.0+398.0; xx += w ) {
				for( double yy = y+100.5+ratio*245.0; yy < y+100.0+245.0; yy += h ) {
					ctx.drawImage( ImageElement.as( wallpaper.getElement() ), xx, yy, w, h );
				}
			}
			
			ctx.restore();
		}
		
		@Override
		public void draw(Context2d ctx) {
			ctx.clearRect(x, y, w, h);
			
			ctx.setFillStyle( "#000000" );
			String label;
			if( dagar == 0 ) label = "Kæla "+hiti+" gráðu heitan fisk í 0 gráður";
			else label = "Kæla og viðhalda við 0 gráður í "+dagar+" daga úr "+hiti+" gráðum við "+umh+" gráðu umhverfishita";
			double tw = ctx.measureText( label ).getWidth();
			ctx.fillText( label, x+(w-tw)/2, y+30 );
			
			if( dagar == 0 ) {
				if( stuckdb == fiskdb ) {
					double dval = Math.round( fiskdb.getValue()*0.0114*hiti*10.0 )/10.0;
					isdb.setValue( dval );
				} else {
					double dval = Math.round( 10.0*isdb.getValue()/(0.0114*hiti) )/10.0;
					fiskdb.setValue( dval );
				}
				
				double ratio = isdb.getValue()/(fiskdb.getValue()+isdb.getValue());
				drawKassi( ctx, ratio );
				ctx.setLineWidth( 1.0 );
				
				String isstr = isdb.getValue()+" kg af ís";
				double strw = ctx.measureText( isstr ).getWidth();
				ctx.fillText( isstr, x+(w-strw)/2.0, y+50 );
				
				ctx.setFillStyle( "#000000" );
				String fiskstr = fiskdb.getValue()+" kg af fiski";
				strw = ctx.measureText( fiskstr ).getWidth();
				ctx.fillText( fiskstr, x+(w-strw)/2.0, y+100.0+(1.0+ratio)*245.0/2.0 );
			} else {
				if( stuckdb == fiskdb ) {
					double dval = Math.round( fiskdb.getValue()*0.015*umh*dagar*10.0 )/10.0  +  Math.round( fiskdb.getValue()*0.0114*hiti*10.0 )/10.0;
					isdb.setValue( dval );
				} else {
					double dval = Math.round( 10.0*isdb.getValue()/(0.015*dagar*umh+0.0114*hiti) )/10.0;
					fiskdb.setValue( dval );
				}
				
				double ratio = isdb.getValue()/(fiskdb.getValue()+isdb.getValue());
				drawKassi( ctx, ratio );
				ctx.setLineWidth( 1.0 );
				
				String isstr = isdb.getValue()+" kg af ís";
				double strw = ctx.measureText( isstr ).getWidth();
				ctx.fillText( isstr, x+(w-strw)/2.0, y+50 );
				
				ctx.setFillStyle( "#000000" );
				String fiskstr = fiskdb.getValue()+" kg af fiski";
				strw = ctx.measureText( fiskstr ).getWidth();
				ctx.fillText( fiskstr, x+(w-strw)/2.0, y+100.0+(1.0+ratio)*245.0/2.0 );
			}
			
			ctx.setStrokeStyle("#EEEEEE");
			ctx.strokeRect(x, y, w, h);
		}

		public double getHiti() {
			return hiti;
		}
		
		public double getUmHiti() {
			return umh;
		}

		public void setHiti(double hiti) {
			this.hiti = hiti;
		}
		
		public void setUmHiti(double hiti) {
			this.umh = hiti;
		}

		public double getDagar() {
			return dagar;
		}

		public void setDagar(double dagar) {
			this.dagar = dagar;
		}
	}
	
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
		
		public double getValue() {
			return val;
		}
		
		public void setValue( double val ) {
			this.val = val;
		}
		
		public void draw( Context2d ctx ) {
			ctx.clearRect(x-8, y-20, 32, h+40);
			
			ctx.setLineWidth( 1.0 );
			ctx.setStrokeStyle("#000000");
			//ctx.setFillStyle("#000000");
			ctx.beginPath();
			ctx.moveTo(x, y);
			ctx.lineTo(x+12, y);
			ctx.closePath();
			ctx.stroke();
			
			ctx.beginPath();
			ctx.moveTo(x+6, y);
			ctx.lineTo(x+6, y+h);
			ctx.closePath();
			ctx.stroke();
			
			ctx.beginPath();
			ctx.moveTo(x, y+h);
			ctx.lineTo(x+12, y+h);
			ctx.closePath();
			ctx.stroke();
			
			ctx.setFillStyle("#FF9999");
			ctx.beginPath();
			ctx.arc(x+6, y+h-(h*(val-start))/(stop-start), 5.0, 0.0, 2*Math.PI );
			ctx.closePath();
			ctx.stroke();
			ctx.fill();
			
			ctx.setFillStyle("#000000");
			double tw = ctx.measureText( label ).getWidth();
			ctx.fillText( label, x+(12-tw)/2, y+h+18 );
			
			String sval = val+"";
			double vw = ctx.measureText( sval ).getWidth();
			ctx.fillText( sval, x+(12-vw)/2, y-7 );
		}

		@Override
		public void click(double x, double y) {
			double retval = start + (h-y)*(stop-start)/h;
			retval = Math.round( retval*jump )/jump;
			double oldval = val;
			val = Math.max( start, Math.min( stop, retval ) );
			for( ChangeListener cl : listchange ) {
				cl.onChange( oldval, val );
			}
		}

		@Override
		public Rectangle getBounds() {
			return new Rectangle( x, y, 16, h );
		}

		List<ChangeListener>	listchange = new ArrayList<ChangeListener>();
		@Override
		public void addChangeListener(ChangeListener c) {
			listchange.add( c );
		}
	}
	
	List<Drawable>	dlist = new ArrayList<Drawable>();
	List<Clickable>	clist = new ArrayList<Clickable>();
	DoubleBox		fiskdb = new DoubleBox();
	DoubleBox		isdb = new DoubleBox();
	DoubleBox		stuckdb = fiskdb;
	
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
		final RootPanel	rp = RootPanel.get();
		rp.setSize( Window.getClientWidth()+"px", Window.getClientHeight()+"px" );
		Window.addResizeHandler( new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				rp.setSize( event.getWidth()+"px", event.getHeight()+"px" );
			}
		});
		
		Image img = new Image( "/torskur.jpg" );
		
		VerticalPanel	pvp = new VerticalPanel();
		pvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		pvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		pvp.setSize( "100%", "100%" );
		//HorizontalPanel	php = new HorizontalPanel();
		//php.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		//php.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("640px", "480px");
		
		pvp.add( vp );
		
		Canvas		c = Canvas.createIfSupported();
		c.setSize("640x", "450px");
		c.setCoordinateSpaceWidth( 640 );
		c.setCoordinateSpaceHeight( 450 );
		final Context2d ctx = c.getContext2d();
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing( 0 );
		hp.setSize( "640px", "30px" );
		
		Label	l = new Label("Fiskmagn:");
		fiskdb.setWidth("60px");
		fiskdb.setValue( 1000.0 );
		Label	m = new Label("kg");
		hp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		Label	isl = new Label("Ísmagn:");
		isdb.setWidth("60px");
		isdb.setValue( 0.0 );
		//Label	ism = new Label("kg");
		
		hp.add( l );
		hp.add( fiskdb );
		//hp.add( m );
		
		hp.add( isl );
		hp.add( isdb );
		hp.add( m );
		
		vp.add( c );
		vp.add( hp );
		
		final Meter dagameter = new Meter( "dagar", 30, 30, 390.0, 0.0, 7.0, 1.0 );
		final Meter hitameter = new Meter( "gráður", 60, 30, 390.0, 0.0, 20.0, 10.0 );
		final Meter umhmeter = new Meter( "umhv. hiti", 90, 30, 390.0, 0.0, 20.0, 10.0 );
		
		final Kassi kassi = new Kassi( 120.0, 30.0, 500.0, 390.0, img );
		
		dlist.add( dagameter );
		dlist.add( hitameter );
		dlist.add( umhmeter );
		dlist.add( kassi );
		
		fiskdb.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				stuckdb = fiskdb;
				kassi.draw( ctx );
			}
		});
		
		isdb.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				stuckdb = isdb;
				kassi.draw( ctx );
			}
		});
		
		ChangeListener cl = new ChangeListener() {
			Drawable d;
			
			@Override
			public void onChange(double oldval, double newval) {
				kassi.setDagar(newval);
				//kassi.setHiti( 0.0 );
				//hitameter.setValue( 0.0 );
				//hitameter.draw( ctx );
				if( d != null ) d.draw(ctx);
			}

			@Override
			public void setDrawable(Drawable d) {
				this.d = d;
			}

			@Override
			public Drawable getDrawable() {
				return this.d;
			}
		};
		cl.setDrawable( kassi );
		dagameter.addChangeListener( cl );
		
		cl = new ChangeListener() {
			Drawable d;
			
			@Override
			public void onChange(double oldval, double newval) {
				kassi.setHiti(newval);
				//kassi.setDagar( 0.0 );
				//dagameter.setValue( 0.0 );
				//dagameter.draw( ctx );
				if( d != null ) d.draw(ctx);
			}

			@Override
			public void setDrawable(Drawable d) {
				this.d = d;
			}

			@Override
			public Drawable getDrawable() {
				return this.d;
			}
		};
		cl.setDrawable( kassi );
		hitameter.addChangeListener( cl );
		
		cl = new ChangeListener() {
			Drawable d;
			
			@Override
			public void onChange(double oldval, double newval) {
				kassi.setUmHiti(newval);
				//kassi.setDagar( 0.0 );
				//dagameter.setValue( 0.0 );
				//dagameter.draw( ctx );
				if( d != null ) d.draw(ctx);
			}

			@Override
			public void setDrawable(Drawable d) {
				this.d = d;
			}

			@Override
			public Drawable getDrawable() {
				return this.d;
			}
		};
		cl.setDrawable( kassi );
		umhmeter.addChangeListener( cl );
		
		for( Drawable d : dlist ) {
			if( d instanceof Clickable ) clist.add( (Clickable)d );
		}
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
		rp.add( pvp );
	}
}
