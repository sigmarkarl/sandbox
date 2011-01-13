package org.simmi.server;

import javax.jdo.PersistenceManager;
import javax.jdo.identity.ObjectIdentity;

import org.simmi.client.Greeting;
import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public Greeting greetServer(String input) throws IllegalArgumentException {
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
		
		/*String ret = "error";
		BlobstoreService blobstore = null;
		try {
			blobstore = BlobstoreServiceFactory.getBlobstoreService();
		} catch( Exception e ) {
			ret = "bloberror";
		}
		
		if( blobstore != null ) {
			try {
				ret = blobstore.createUploadUrl("/upload");
			} catch( Exception e ) {
				ret = e.getMessage()+" urlerror ";
				for( StackTraceElement ste : e.getStackTrace() ) {
					ret += ste.toString();
				}
			}
		}
		
		return ret;*/
		
		System.err.println( "hoho " + input );
		PersistenceManager pm = PMF.get().getPersistenceManager();
		GreetingImpl greet = pm.getObjectById( GreetingImpl.class, Integer.parseInt(input) );
		if( greet.getKey() != null ) {    		
    		String filename = greet.getFilename();
	    	String filetype = greet.getFiletype();
	    	int filesize = greet.getFilesize();
    		String date = greet.getDate().toString();
	    	String	keystr = KeyFactory.keyToString(greet.getKey());
	    	User	user = greet.getAuthor();
	    	ObjectIdentity oi = ((ObjectIdentity)pm.getObjectId(greet));
	    	String id = oi.toString();
	    	int inf = id.indexOf('(');
	    	int inl = id.indexOf(')', inf);
	    	String rid = id.substring(inf+1, inl);

	    	int numgrades = greet.getGrades().size();
	    	return new Greeting( keystr, rid, greet.getTitle(), greet.getDescription(), date, user == null ? "none" : user.getNickname(), user == null ? "none" : user.getFederatedIdentity(), filename, filetype, filesize, numgrades );
    	}
		
		return null;
	}
}
