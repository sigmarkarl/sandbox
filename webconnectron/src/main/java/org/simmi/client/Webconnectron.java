package org.simmi.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webconnectron implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	Connectron connectron;
	public void handleText( String stuff ) {
		connectron.handleText( stuff );
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		/*<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_s-xclick">
		<input type="hidden" name="hosted_button_id" value="7MQHN385ZR89W">
		<input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
		<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
		</form>
		
		/*VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");

		vp.add( new HTML("<h1>Web Connectron</h1><h3>You need <a href=\"http://www.java.com\">Java</a> to run this page</h3>") );
		vp.add( new HTML("Make graphs in 3D. Right click the canvas for options. Zoom in/out with +/-. Control the size of nodes with ,/.. Hold shift and drag to select.<br>Press space to move to center of mass. Double click on node and drag to connect nodes. Load sample data and select Spring graph for demonstration.") );
		vp.add( new HTML("<applet codebase=\"http://dl.dropbox.com/u/10024658/connectron/\" archive=\"connectron.jar\" code=\"org.simmi.Connectron\" width=\"800\" height=\"800\" jnlp_href=\"connectron.jnlp\"><param name=\"jnlp_href\" value=\"connectron.jnlp\"/></applet>") );
		vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://webspectroscope.appspot.com\">http://webspectroscope.appspot.com</a> | <a href=\"http://websimlab.appspot.com\">http://websimlab.appspot.com</a> | <a href=\"http://nutritiondb.appspot.com\">http://nutritiondb.appspot.com</a> | <a href=\"http://fasteignaverd.appspot.com\">http://fasteignaverd.appspot.com</a>") );
		
		RootPanel.get("connectron").add( vp );*/
		
		Window.enableScrolling(false);
		final RootPanel 	rp = RootPanel.get( "main" );
		/*rp.addDomHandler( new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ContextMenuEvent.getType());*/
		
		Style				st = rp.getElement().getStyle();
		st.setMargin( 0.0, Unit.PX );
		st.setPadding( 0.0, Unit.PX );
		st.setBorderWidth( 0.0, Unit.PX );
		
		int w = Window.getClientWidth()-160;
		int h = Window.getClientHeight();
		//rp.setPixelSize(w, h);
		rp.setSize(w+"px", h+"px");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		hp.setSize("100%", "100%");
		
		//final Element ads = Document.get().getElementById("ads");
		//SimplePanel	sp = new SimplePanel();
		//sp.getElement().appendChild( ads );
		
		connectron = new Connectron();
		hp.add( connectron );
		//hp.add( sp );
		
		int ww = w;
		int hh = h-20;
		connectron.getCanvas().setSize(ww+"px", hh+"px");
		connectron.getCanvas().setCoordinateSpaceWidth( ww );
		connectron.getCanvas().setCoordinateSpaceHeight( hh );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth()-160;
				int h = event.getHeight();
				
				if( (w != oldw && w != olderw) || (h != oldh && h != olderh) ) {
					//rp.setPixelSize( w, h );
					rp.setSize(w+"px", h+"px");
					connectron.getCanvas().setSize(w+"px", (h-20)+"px");
					connectron.getCanvas().setCoordinateSpaceWidth( w );
					connectron.getCanvas().setCoordinateSpaceHeight( h );
					
					Context2d context = connectron.getCanvas().getContext2d();
					context.setFillStyle("#000000");
					String str = "Drop text in distance matrix or newick tree format to this canvas";
					TextMetrics tm = context.measureText( str );
					context.fillText(str, (w-tm.getWidth())/2.0, h/2.0-8.0);
					str = "Double click to open file dialog";
					tm = context.measureText( str );
					context.fillText(str, (w-tm.getWidth())/2.0, h/2.0+8.0);
				}
				
				olderw = oldw;
				oldw = w;
				olderh = oldh;
				oldh = h;
			}
		});
		
		rp.add( hp );
		
		if( Window.Location.getParameterMap().keySet().contains("callback") ) {
			/*var s = this;
			$wnd.addEventListener('message',function(event) {
				$wnd.console.log( event.origin );
				$wnd.console.log('message received from treedraw');
				if(event.origin == 'http://'+from+'.appspot.com') {
					$wnd.console.log('correct treedraw origin');
					s.@org.simmi.client.Webconnectron::handleText(Ljava/lang/String;)( event.data );
				}
			});
			$wnd.console.log('posting ready');	
			$wnd.opener.postMessage('ready','http://'+from+'.appspot.com');*/
		
			final elemental.html.Window wnd = Browser.getWindow();
			wnd.addEventListener("message", new EventListener() {
				@Override
				public void handleEvent(Event evt) {
					wnd.getConsole().log("erm");
					MessageEvent me = (MessageEvent)evt;
					String text = (String)me.getData();
					handleText( text );
				}
			}, false);
			wnd.getOpener().postMessage("ready", "*");
			//postParent( Window.Location.getParameter("callback") );
		}
	}
	int oldw, olderw;
	int oldh, olderh;
}
