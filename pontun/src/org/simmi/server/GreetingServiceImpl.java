package org.simmi.server;

import java.util.List;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("verk");
		ent.setProperty("mix", input);
		
		datastore.put( ent );
		
		return input;
	}

	@Override
	public String getAllVerk() throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("verk");
		List<Entity> dEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		StringBuilder ret = new StringBuilder();
		
		for( Entity e : dEntities ) {
			if( ret.length() == 0 ) ret.append( (String)e.getProperty("mix") );
			else ret.append( "\n"+(String)e.getProperty("mix") );
		}
		
		return ret.toString();
	}
}
