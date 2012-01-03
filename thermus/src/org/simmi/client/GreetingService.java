package org.simmi.client;

import java.util.Map;

import org.simmi.shared.Chunk;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String acc, String country, boolean valid) throws IllegalArgumentException;
	Map<String,Chunk>	getThermus();
}
