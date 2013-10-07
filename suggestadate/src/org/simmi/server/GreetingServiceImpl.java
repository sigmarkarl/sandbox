package org.simmi.server;

import java.util.List;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		String ret = "";
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if( input.startsWith("accept") ) {
			String substr = input.substring(7);
			String[] subsplit = substr.split("\t");
			String keystr = subsplit[0];
			String uid = subsplit[1];
			String email = subsplit[2];
			Key key = KeyFactory.stringToKey(keystr);
			try {
				Entity e = datastore.get(key);
				String uid1 = (String)e.getProperty("uid1");
				String uid2 = (String)e.getProperty("uid2");
				
				String femail = "";
				String fuid = "";
				if( uid.equals(uid1) ) {
					fuid = (String)e.getProperty("uid2");
					femail = (String)e.getProperty("email2");
					e.setProperty("email1", email);
					ret = fuid+"\t"+femail;
				} else if( uid.equals(uid2) ) {
					fuid = (String)e.getProperty("uid1");
					femail = (String)e.getProperty("email1");
					e.setProperty("email2", email);
					ret = fuid+"\t"+femail;
				}
				
				datastore.put(e);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		} else if( input.startsWith("delete") ) {
			String keystr = input.substring(7);
			//ret = input;
			//this.log( "hey mf: " + keystr );
			//int i = keystr.indexOf( "=" );
			Key key = KeyFactory.stringToKey( keystr );
			datastore.delete( key );
		} else if( !input.contains("\t") ) {
			Query query = new Query("date");
			Filter filter = new FilterPredicate("uid1", FilterOperator.EQUAL, input);
			query.setFilter( filter );
			List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity e : seqsEntities ) {
				String val = KeyFactory.keyToString(e.getKey())+"\t"+(String)e.getProperty("date2")+"\t"+(String)e.getProperty("link2")+"\t"+(String)e.getProperty("email2")+"\t"+(String)e.getProperty("email1");
				if( ret.length() == 0 ) ret += val;
				else ret += "\n"+val;
			}
			
			query = new Query("date");
			filter = new FilterPredicate("uid2", FilterOperator.EQUAL, input);
			query.setFilter( filter );
			seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity e : seqsEntities ) {
				String val = KeyFactory.keyToString(e.getKey())+"\t"+(String)e.getProperty("date1")+"\t"+(String)e.getProperty("link1")+"\t"+(String)e.getProperty("email1")+"\t"+(String)e.getProperty("email2");
				if( ret.length() == 0 ) ret += val;
				else ret += "\n"+val;
			}
		} else {
			String[] split = input.split("\t");
			
			String uid1 = split[4];
			String uid2 = split[5];
			String l1 = split[6];
			String l2 = split[7];
			
			boolean already = false;
			
			Query query = new Query("date");
			Filter filter = new FilterPredicate("uid1", FilterOperator.EQUAL, uid1);
			query.setFilter( filter );
			List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity e : seqsEntities ) {
				String nuid2 = (String)e.getProperty("uid2");
				if( nuid2.equals(uid2) ) {
					already = true;
					break;
				}
			}
			
			if( !already ) {
				query = new Query("date");
				filter = new FilterPredicate("uid2", FilterOperator.EQUAL, uid1);
				query.setFilter( filter );
				seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
				for( Entity e : seqsEntities ) {
					String nuid2 = (String)e.getProperty("uid1");
					if( nuid2.equals(uid2) ) {
						already = true;
						break;
					}
				}
			}
			
			if( !already ) {
				Entity ent = new Entity("date");
				ent.setProperty("user", split[0]);
				ent.setProperty("date1", split[1]);
				ent.setProperty("date2", split[2]);
				ent.setProperty("uid", split[3]);
				ent.setProperty("uid1", uid1);
				ent.setProperty("uid2", uid2);
				ent.setProperty("link1", l1);
				ent.setProperty("link2", l2);
				ent.setProperty("email1", "");
				ent.setProperty("email2", "");
				
				//Key key = 
				datastore.put( ent );
			} else {
				ret = "already";
			}
		}
		
		return ret;
	}
}
