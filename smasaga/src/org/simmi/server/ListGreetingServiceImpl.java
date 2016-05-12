package org.simmi.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.identity.ObjectIdentity;

import org.simmi.client.Greeting;
import org.simmi.client.ListGreetingService;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ListGreetingServiceImpl extends RemoteServiceServlet implements ListGreetingService {

	public List<Greeting> greetServer(String input) throws IllegalArgumentException {
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
		
		//BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + GreetingImpl.class.getName();
	    List<GreetingImpl> greets = (List<GreetingImpl>) pm.newQuery(query).execute();
	    
	    //BlobInfoFactory f = new BlobInfoFactory();
	    List<Greeting>	greetings = new ArrayList<Greeting>();
	    for( GreetingImpl greet : greets ) {
	    	if( greet.getKey() != null ) {
		    	//BlobInfo blobinfo = f.loadBlobInfo( greet.getBlobKey() );
		    	/*String filename = blobinfo.getFilename();
		    	String filetype = blobinfo.getContentType();
		    	int filesize = (int)blobinfo.getSize();
		    	String 	date = blobinfo.getCreation().toString();*/
	    		
	    		String filename = greet.getFilename();
		    	String filetype = greet.getFiletype();
		    	int filesize = greet.getFilesize();
	    		String date = greet.getDate().toString();
		    	String	keystr = KeyFactory.keyToString(greet.getKey());
		    	User	user = greet.getAuthor();
		    	ObjectIdentity oi = ((ObjectIdentity)pm.getObjectId(greet));
		    	//greet.
		    	String id = oi.toString();
		    	int inf = id.indexOf('(');
		    	int inl = id.indexOf(')', inf);
		    	String rid = id.substring(inf+1, inl);
		    	//String key = oi.getKey().toString();
		    	//greet.
		    	//System.err.println("id: "+id + "  " + key);
		    	int numgrades = greet.getGrades().size();
		    	greetings.add( new Greeting( keystr, rid, greet.getTitle(), greet.getDescription(), date, user == null ? "none" : user.getNickname(), user == null ? "none" : user.getFederatedIdentity(), filename, filetype, filesize, numgrades ) );
	    	}
	    }
	    
		return greetings;
	}
}
