package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EuroFIRWebServiceAsync {
	void foodXML(String query, AsyncCallback<String> callback);
}
