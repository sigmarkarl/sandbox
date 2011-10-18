package org.simmi.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("vote");
		query.addFilter("name", FilterOperator.EQUAL, input);
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		String ret = null;
		for( Entity e : seqsEntities ) {
			ret = KeyFactory.keyToString( e.getKey() ) + "\t" + (String)e.getProperty("vote");
		}
		
		return ret;
	}
	
	public Map<String,Integer> getVotes() throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("vote");
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		Map<String,Integer>	votes = new HashMap<String,Integer>();	
		for( Entity e : seqsEntities ) {
			String votestr = (String)e.getProperty("vote");
			String[] split = votestr.split("\t");
			for( String name : split ) {
				if( votes.containsKey(name) ) votes.put( name, votes.get(name)+1 );
				else votes.put( name, 1 );
			}
		}
		
		return votes;
	}

	@Override
	public String saveVote(String keystr, String name, String vote) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = null;
		if( keystr != null )
			try {
				ent = datastore.get( KeyFactory.stringToKey(keystr) );
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		else {
			ent = new Entity("vote");
			ent.setProperty("name", name);
		}
		ent.setProperty("vote", vote);
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}
}
