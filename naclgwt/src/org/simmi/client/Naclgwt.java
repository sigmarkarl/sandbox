package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Naclgwt implements EntryPoint {
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
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public native void init() /*-{
		var ths = this;
		$doc.appendText = function( str ) {
			ths.@org.simmi.client.Naclgwt::appendText(Ljava/lang/String;)( str );
		};
	}-*/;
	
	public void appendText( String str ) {
		textarea.setText( textarea.getText()+str+"\n" );
	}
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	public native void postMessage( String message ) /*-{
		$wnd.postMessage( message );
	}-*/;
	
	final TextArea	textarea = new TextArea();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		init();
		
		final RootPanel	rp = RootPanel.get();
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		
		rp.setSize(w+"px", h+"px");
		Window.addResizeHandler( new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		
		textarea.addKeyPressHandler( new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char cc = event.getCharCode();
				if( cc == '\r' ) {
					String val = textarea.getText();
					int i = val.lastIndexOf('\n');
					String last = "";
					if( i != -1 ) {
						last = val.substring(i+1, val.length());
					} else {
						last = val.substring(0, val.length());
					}
					console( last );
					postMessage( last );
				}
			}
		});
		rp.add( textarea );
	}
}
