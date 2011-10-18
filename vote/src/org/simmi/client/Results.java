package org.simmi.client;

import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Results implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	@Override
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		Canvas c = Canvas.createIfSupported();
		c.setSize("100%", "100%");
		
		console("about");
		greetingService.getVotes( new AsyncCallback<Map<String,Integer>>() {
			@Override
			public void onSuccess(Map<String, Integer> result) {
				console("mu" + result.size());
				for( String name : result.keySet() ) {
					console( name + "+t" + result.get(name) );
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				console("fal");
				console( caught.getMessage() );
			}
		});
		console("next");
		
		rp.add( c );
	}
}
