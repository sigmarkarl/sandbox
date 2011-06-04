package org.simmi.mapviewer.client;

import java.io.IOException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("LocationFetch")
public interface LocationFetch extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static LocationFetchAsync instance;
		public static LocationFetchAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(LocationFetch.class);
			}
			return instance;
		}
	}

	String fetchLocations() throws IOException;
}
