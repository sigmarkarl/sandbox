package org.simmi.server;

import java.util.List;

import org.simmi.client.GreetingService;
import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.FieldVerifier;
import org.simmi.shared.Sequences;

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

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@Override
	public Sequences[] getSequences() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("sequences");
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		
		int i = 0;
		for( Entity e : seqsEntities ) {
			seqsArray[i++] = new Sequences( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("path"), (Long)e.getProperty("num") );
		}
		
		return seqsArray; //Arrays.asList( seqsArray );
	}

	@Override
	public String saveSequences(Sequences seqs) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity ent = new Entity("sequences");
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("user", seqs.getUser());
		ent.setProperty("name", seqs.getName());
		ent.setProperty("type", seqs.getType());
		ent.setProperty("path", seqs.getPath());
		ent.setProperty("num", seqs.getNum());
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}

	@Override
	public Blast[] getBlastResults() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("blast");
		List<Entity> blEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Blast[] blArray = new Blast[ blEntities.size() ];
		
		int i = 0;
		for( Entity e : blEntities ) {
			blArray[i++] = new Blast( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("path"), (String)e.getProperty("result") );
		}
		
		return blArray;
	}

	@Override
	public Database[] getDatabases() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("database");
		List<Entity> dEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Database[] dArray = new Database[ dEntities.size() ];
		
		int i = 0;
		for( Entity e : dEntities ) {
			dArray[i++] = new Database( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("database"), (String)e.getProperty("type"), (String)e.getProperty("path") );
		}
		
		return dArray;
	}

	@Override
	public String saveDb(Database db) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("database");
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("user", db.getUser());
		ent.setProperty("name", db.getName());
		ent.setProperty("type", db.getType());
		ent.setProperty("path", db.getPath());
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}

	@Override
	public String saveBlast(Blast b) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("blast");
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("user", b.getUser());
		ent.setProperty("name", b.getName());
		ent.setProperty("type", b.getType());
		ent.setProperty("path", b.getPath());
		//ent.setProperty("result", b.getResult());
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}
}