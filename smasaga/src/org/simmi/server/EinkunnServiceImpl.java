package org.simmi.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.simmi.client.EinkunnSerializable;
import org.simmi.client.EinkunnService;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EinkunnServiceImpl extends RemoteServiceServlet implements EinkunnService {

	public List<EinkunnSerializable> einkunnServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		/*if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}*/
		
		//ImageIO.read(input);

		//String serverInfo = getServletContext().getServerInfo();
		//String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		//return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>" + userAgent;
		
		BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + GreetingImpl.class.getName();
	    List<GreetingImpl> greets = (List<GreetingImpl>) pm.newQuery(query).execute();
	    
	    String gistr = null;
	    GreetingImpl gi = null;
	    for( GreetingImpl greet : greets ) {
	    	Key key = greet.getKey();
	    	if( KeyFactory.keyToString(key).equals(input) ) {
	    		gistr = KeyFactory.keyToString( key );
	    		gi = greet;
	    		break;
	    	}
	    }
		
	    query = "select from " + EinkunnPersistent.class.getName();
		//String query = "select from " + EinkunnPersistent.class.getName();
	    
	    System.err.println( query + "  " + gi );
	    List<EinkunnPersistent> einks = (List<EinkunnPersistent>)pm.newQuery(query).execute();
	    System.err.println( "sst " + einks.size() );
	    
	    //BlobInfoFactory f = new BlobInfoFactory();
	    List<EinkunnSerializable>	einkunnir = new ArrayList<EinkunnSerializable>();
	    for( EinkunnPersistent eink : einks ) {
	    	if( gistr.equals( eink.getBook() ) ) einkunnir.add( new EinkunnSerializable( eink.getGrader().getNickname(), eink.getGrade(), eink.getComment() ) );
	    }
	    
		return einkunnir;
	}
}
