package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, String searchnum, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getRemoteAddress(AsyncCallback<String> callback);
}
