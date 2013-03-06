package org.simmi.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String acc, String country, boolean valid, AsyncCallback<String> callback);
	void getThermus( AsyncCallback<Map<String,String>> callback );
	void saveSel(String name, String val, AsyncCallback<Map<String,String>> callback);
	void getThermusFusion(AsyncCallback<String> callback);
	void saveThermusSel(String name, String val, AsyncCallback<Map<String, String>> callback);
	void saveSeq(Map<String,String> jsonstr, AsyncCallback<String> asyncCallback);
	void fetchSeq(String include,
			AsyncCallback<Map<String, String>> asyncCallback);
}
