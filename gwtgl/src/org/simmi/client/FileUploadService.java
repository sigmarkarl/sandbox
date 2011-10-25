package org.simmi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("FileUploadService")
public interface FileUploadService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static FileUploadServiceAsync instance;
		public static FileUploadServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(FileUploadService.class);
			}
			return instance;
		}
	}
}
