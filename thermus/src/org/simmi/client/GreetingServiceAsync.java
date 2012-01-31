package org.simmi.client;

import java.util.Map;

import org.simmi.shared.Chunk;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String acc, String country, boolean valid, AsyncCallback<String> callback);
	void getThermus( AsyncCallback<Map<String,String>> callback );
	void saveSel(String name, String val, AsyncCallback<Map<String,String>> callback);
}
