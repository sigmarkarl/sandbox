package org.simmi.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface EinkunnServiceAsync {
	void einkunnServer(String input, AsyncCallback<List<EinkunnSerializable>> callback) throws IllegalArgumentException;
}
