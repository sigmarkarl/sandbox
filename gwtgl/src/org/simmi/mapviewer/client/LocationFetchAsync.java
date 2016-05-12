package org.simmi.mapviewer.client;

import java.io.IOException;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LocationFetchAsync {
	void fetchLocations(AsyncCallback<String> callback) throws IOException;
}
