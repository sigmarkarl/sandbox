package org.simmi.server;

import java.util.List;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
		String ret = "";
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if( input.startsWith("delete") ) {
			String keystr = input.substring(7);
			Key key = KeyFactory.stringToKey( keystr );
			datastore.delete( key );
		} else if( !input.contains("\t") ) {
			Query query = new Query("date");
			query.addFilter("uid1", FilterOperator.EQUAL, input);
			List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity e : seqsEntities ) {
				String val = (String)e.getProperty("uid2")+"\t"+(String)e.getProperty("date2");;
				if( ret.length() == 0 ) ret += val;
				else ret += "\n"+val;
			}
			
			query = new Query("date");
			query.addFilter("uid2", FilterOperator.EQUAL, input);
			seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity e : seqsEntities ) {
				String val = KeyFactory.keyToString(e.getKey())+"\t"+(String)e.getProperty("date1");;
				if( ret.length() == 0 ) ret += val;
				else ret += "\n"+val;
			}
		} else {
			String[] split = input.split("\t");
			Entity ent = new Entity("date");
			
			ent.setProperty("user", split[0]);
			ent.setProperty("date1", split[1]);
			ent.setProperty("date2", split[2]);
			ent.setProperty("uid", split[3]);
			ent.setProperty("uid1", split[4]);
			ent.setProperty("uid2", split[5]);
			
			//Key key = 
			datastore.put( ent );
		}
		
		return ret;
	}
}
