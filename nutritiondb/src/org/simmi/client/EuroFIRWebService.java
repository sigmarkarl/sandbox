package org.simmi.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("EruoFIRWebService")
public interface EuroFIRWebService extends RemoteService {
	String foodXML( String query );
}
