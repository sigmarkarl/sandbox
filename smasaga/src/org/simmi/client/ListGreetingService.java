package org.simmi.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("listgreet")
public interface ListGreetingService extends RemoteService {
	List<Greeting> greetServer(String name) throws IllegalArgumentException;
}
