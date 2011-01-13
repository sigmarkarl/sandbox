package org.simmi.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ListGreetingServiceAsync {
	void greetServer(String input, AsyncCallback<List<Greeting>> callback) throws IllegalArgumentException;
}
