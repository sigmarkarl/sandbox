package org.simmi.client;

import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	public native void initFunctions() /*-{
		var s = this;
		$wnd.saveSel = function( name, val ) {
			s.@org.simmi.client.Thermus::saveSel(Ljava/lang/String;Ljava/lang/String;)( name, val );
		};
	}-*/;
	
	public native void fillSelSaveInApplet( Element e, String json ) /*-{
		e.fillSelectionSave( json );
	}-*/;
	
	public void saveSel( String name, String val ) {
		greetingService.saveSel( name, val, new AsyncCallback<Map<String,String>>() {
			@Override
			public void onSuccess(Map<String, String> result) {
				Element e = Document.get().getElementById("thermusapplet");
				JSONObject	json = new JSONObject();
				for( String key : result.keySet() ) {
					JSONArray array = new JSONArray();
					String str = result.get(key);
					String[] numbers = str.split(",");
					int k = 0;
					for( String num : numbers ) {
						array.set(k++, new JSONNumber(Integer.parseInt(num)) );
					}
					json.put( key, array );
				}
				fillSelSaveInApplet( e, json.toString() );
			}
			
			@Override
			public void onFailure(Throwable caught) {
				console( caught.getMessage() );
			}
		});
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initFunctions();
		
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
		
		ae.setAttribute("id", "thermusapplet");
		ae.setAttribute("name", "thermusapplet");
		//ae.setAttribute("codebase", "http://dl.dropbox.com/u/10024658/");
		ae.setAttribute("codebase", "http://thermusgenes.appspot.com/");
		//ae.setAttribute("codebase", "http://127.0.0.1:8888/");
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("jnlp_href", "distann.jnlp");
		ae.setAttribute("archive", "http://test.matis.is/thermus/distann.jar");
		ae.setAttribute("code", "org.simmi.GeneSet");
		
		rp.getElement().appendChild( ae );
	}
}
