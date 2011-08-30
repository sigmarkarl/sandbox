package org.simmi.client;

import org.simmi.shared.Sequences;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getSequences(AsyncCallback<Sequences[]> asyncCallback);
	void saveSequences(Sequences seqs, AsyncCallback<String> asyncCallback);
}
