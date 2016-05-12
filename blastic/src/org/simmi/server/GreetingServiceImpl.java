package org.simmi.server;

import java.util.ArrayList;
import java.util.List;

import org.simmi.client.GreetingService;
import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.FieldVerifier;
import org.simmi.shared.Machine;
import org.simmi.shared.Sequences;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>" + userAgent;
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
		
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
    	
    	Sequences[] seqsArray = null;
    	if( user != null ) {
			FilterPredicate filter = new FilterPredicate("userid", FilterOperator.EQUAL, user.getUserId());
			query.setFilter( filter );
			List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			seqsArray = new Sequences[ seqsEntities.size() ];
			
			int i = 0;
			for( Entity e : seqsEntities ) {
				Sequences seqs = new Sequences( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("path"), (Long)e.getProperty("num") );
				seqs.setKey( KeyFactory.keyToString( e.getKey()) );
				seqsArray[i++] = seqs;
			}
    	}
		
		return seqsArray; //Arrays.asList( seqsArray );
	}

	@Override
	public String saveSequences(Sequences seqs) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("sequences");
		
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		
    	if( user != null ) {
    		ent.setProperty("user", user.getNickname());
			ent.setProperty("userid", user.getUserId());
    	}
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
		
		Blast[] blArray = null;
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
		
    	if( user != null ) {
	    	FilterPredicate filter = new FilterPredicate("userid", FilterOperator.EQUAL, user.getUserId());
			query.setFilter( filter );
			List<Entity> blEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			blArray = new Blast[ blEntities.size() ];
			
			int i = 0;
			for( Entity e : blEntities ) {
				Blast b = new Blast( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("path"), (String)e.getProperty("machine"), (String)e.getProperty("start"), (String)e.getProperty("end"), (String)e.getProperty("result") );
				b.setKey( KeyFactory.keyToString( e.getKey() ) );
				blArray[i++] = b;
			}
    	}
		
		return blArray;
	}

	@Override
	public Database[] getDatabases() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("database");
		
		Database[] dArray = null;
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
    	
    	if( user != null ) {
			FilterPredicate filter = new FilterPredicate("userid", FilterOperator.EQUAL, user.getUserId());
			query.setFilter( filter );
			
			List<Entity> dEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			dArray = new Database[ dEntities.size() ];
			
			int i = 0;
			for( Entity e : dEntities ) {
				String result = "";
				Object res = e.getProperty("result");
				if( res == null ) result = "";
				else if( res instanceof String ) result = (String)res;
				else if( res instanceof Text ) result = ((Text)res).getValue();
				
				Database db = new Database( (String)e.getProperty("user"), (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("path"), (String)e.getProperty("machine"), result );
				db.setKey( KeyFactory.keyToString( e.getKey() ) );
				dArray[i++] = db;
			}
    	}
		
		return dArray;
	}

	@Override
	public String saveDb(Database db) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("database");
		
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		
    	if( user != null ) {
    		ent.setProperty("user", user.getNickname());
			ent.setProperty("userid", user.getUserId());
    	}
		ent.setProperty("name", db.getName());
		ent.setProperty("type", db.getType());
		ent.setProperty("path", db.getPath());
		ent.setProperty("machine", db.getMachine());
		ent.setProperty("result", new Text(db.getResult()));
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}

	@Override
	public String saveBlast(Blast b) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("blast");
		
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
    	if( user != null ) {
    		ent.setProperty("user", user.getNickname());
			ent.setProperty("userid", user.getUserId());
    	}
		ent.setProperty("name", b.getName());
		ent.setProperty("type", b.getType());
		ent.setProperty("path", b.getPath());
		ent.setProperty("machine", b.getMachine());
		ent.setProperty("start", b.getStart());
		ent.setProperty("end", b.getStop());
		ent.setProperty("result", b.getResult());
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}

	@Override
	public String deleteKey(String keystr ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.stringToKey( keystr );
		datastore.delete( key );
		
		return null;
	}

	@Override
	public Machine[] getMachineInfo( String machineid, int procs ) {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query query = new Query("machine");
			List<Entity> mEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			List<Machine> mArray = new ArrayList<Machine>();
			
			Machine mymachine = null;
			Entity em = null;
			//int i = 0;
			for( Entity e : mEntities ) {
				int nproc = ((Number)e.getProperty("nproc")).intValue();
				String name = (String)e.getProperty("name");
				boolean on = (Boolean)e.getProperty("on");
				
				if( name != null ) {
					Machine m = new Machine( name, nproc, on );
					if( name.equals( machineid ) ) {
						mymachine = m;
						em = e;
					}
					
					m.setKey( KeyFactory.keyToString( e.getKey() ) );
					//mArray[i++] = m;
					mArray.add( m );
				}
			}
			
			if( mymachine == null ) {
				mymachine = new Machine( machineid, procs, true );
				em = new Entity("machine");
				em.setProperty("name", machineid);
				
				mArray.add( mymachine );
			} else {
				mymachine.setOn( true );
				mymachine.setProcs( procs );
			}
			
			em.setProperty("on", true);
			em.setProperty("nproc", procs);
			mymachine.setKey( KeyFactory.keyToString( datastore.put( em ) ) );
			
			return mArray.toArray( new Machine[mArray.size()] );
		} catch( Exception e ) {
			StackTraceElement[] stes = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			
			for( StackTraceElement ste : stes ) {
				sb.append( ste.toString() );
			}
			Machine[] mh = { new Machine(sb.toString(),-1,false) };
			return mh;
		}
	}

	@Override
	public String saveMachine(Machine m) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("machine");
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("name", m.getName());
		ent.setProperty("nproc", m.getProcs());
		ent.setProperty("on", m.getOn());
		
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);
	}
}