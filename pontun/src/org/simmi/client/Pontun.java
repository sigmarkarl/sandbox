package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Pontun implements EntryPoint {
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

	public native int initApplet( final String server ) /*-{
		$wnd.runApplet = function() {
			var attributes = { codebase:'http://'+server+'/', archive:'pontun.jar', code:'org.simmi.Order', width:'100%', height:'100%', id:'order', name:'order' };
	    	var parameters = { jnlp_href:'order.jnlp' };
	    	$wnd.deployJava.runApplet(attributes, parameters, '1.6');
		}
	}-*/;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		String server = Location.getHost();
		//initApplet( server );
		
		Window.enableScrolling( false );
		Window.setMargin("0px");
		
		final RootPanel	rp = RootPanel.get();
		Style rootstyle = rp.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		rp.setWidth( Window.getClientWidth()+"px" );
		rp.setHeight( Window.getClientHeight()+"px" );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				rp.setWidth( event.getWidth()+"px" );
				rp.setHeight( event.getHeight()+"px" );
			}
		});
		
		//ScriptElement se = Document.get().createScriptElement();
		/*Style scriptstyle = se.getStyle();
		scriptstyle.setMargin(0.0, Unit.PX);
		scriptstyle.setPadding(0.0, Unit.PX);*/
		
		/*se.setText( //"alert('hoho');\n" +
			"var attributes = { codebase:'http://"+server+"/', archive:'pontun.jar', code:'org.simmi.Order', width:'100%', height:'100%', hspace:'0', vspace:'0', id:'order', name:'order' };\n"+
	    	"var parameters = { jnlp_href:'order.jnlp' };\n"+
	    	"deployJava.runApplet(attributes, parameters, '1.6');" );*/
		
		Element ae = Document.get().createElement("applet");
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("codebase", "http://"+server+"/");
		ae.setAttribute("archive", "order.jar,sqljdbc4.jar,sqljdbc_auth.jar,mail.jar");
		ae.setAttribute("code", "org.simmi.Order");
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "order.jnlp");
		ae.appendChild( pe );
		//HTML applet = new HTML( "<applet width='100%' height='100%' codebase='http://"+server+"/' archive='pontun.jar' code='org.simmi.Order'><param name='jnlp_href' value='order.jnlp' /></applet>" );
		rp.getElement().appendChild( ae );
		
		/*Element ae = Document.get().getElementById("order");
		Style appletstyle = ae.getStyle();
		appletstyle.setMargin(0.0, Unit.PX);
		appletstyle.setPadding(0.0, Unit.PX);*/
	}
}
