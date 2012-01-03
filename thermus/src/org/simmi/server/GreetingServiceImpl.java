package org.simmi.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simmi.client.GreetingService;
import org.simmi.shared.Chunk;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer(String acc, String country, boolean valid) throws IllegalArgumentException {
		System.err.println("juhu");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("thermus");
		//Query query = new Query("sequences");	
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("acc", acc);
		ent.setProperty("country", country);
		ent.setProperty("valid", valid);
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}
	
	public Map<String,Chunk> getThermus() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("thermus");
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Map<String,Chunk>	ret = new HashMap<String,Chunk>();
		
		for( Entity e : seqsEntities ) {
			ret.put( (String)e.getProperty("acc"), new Chunk( (String)e.getProperty("country"), (Boolean)e.getProperty("valid") ) );
		}
		
		return ret;
	}
}
