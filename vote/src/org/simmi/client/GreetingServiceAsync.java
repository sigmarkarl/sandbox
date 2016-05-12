package org.simmi.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	void saveVote(String key, String name, String vote, AsyncCallback<String> callback);
	void getVotes(AsyncCallback<Map<String, Integer>> callback);
}
