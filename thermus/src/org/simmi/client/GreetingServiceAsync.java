package org.simmi.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String acc, String country, AsyncCallback<String> callback);
	void getThermus( AsyncCallback<Map<String,String>> callback );
}
