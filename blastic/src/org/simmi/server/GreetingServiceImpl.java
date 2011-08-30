package org.simmi.server;

import java.util.Arrays;
import java.util.List;

import org.simmi.client.GreetingService;
import org.simmi.shared.FieldVerifier;
import org.simmi.shared.Sequences;

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
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

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
			seqsArray[i++] = new Sequences( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("path"), (Integer)e.getProperty("num") );
		}
		
		return seqsArray; //Arrays.asList( seqsArray );
	}

	@Override
	public String saveSequences(Sequences seqs) {
		// TODO Auto-generated method stub
		return null;
	}
}
