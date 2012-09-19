package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Webnutrition implements EntryPoint {

	public native void console( String str ) /*-{
		if( $wnd.console ) $wnd.console.log( str );
	}-*/;
	
	@Override
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		String url = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20*%20FROM%201_6lpPsZem6FpvoFF8tLsHvu1e34a2POc3Ed-u6E&key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, url );
		try {
			rb.sendRequest( "", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					console( response.getText() );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		Canvas canvas = Canvas.createIfSupported();
		canvas.setSize("1024px", "768px");
		vp.add( canvas );
		
		rp.add( vp );
	}
}
