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
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Thermus implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel rp = RootPanel.get();
		//final RootPanel thermus = RootPanel.get("thermus");
		
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		Window.enableScrolling( false );
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		//thermus.setSize(w+"px", h+"px");
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize(w+"px", h+"px");
				//thermus.setSize(w+"px", h+"px");
				
				//console( w+" "+h );
			}
		});
		
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "distann.jnlp");
		
		final Element ae = Document.get().createElement("applet");
		ae.appendChild( pe );
		
		ae.setAttribute("id", "thermus");
		ae.setAttribute("name", "thermus");
		//ae.setAttribute("codebase", "http://dl.dropbox.com/u/10024658/");
		ae.setAttribute("codebase", "http://thermusgenes.appspot.com/");
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("jnlp_href", "distann.jnlp");
		ae.setAttribute("archive", "distann.jar");
		ae.setAttribute("code", "org.simmi.GeneSet");
		
		rp.getElement().appendChild( ae );
	}
}
