package org.simmi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Text;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.http.GoogleGDataRequest;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	private final String tableid = "1dmyUhlXVEoWHrT-rfAaAHl3vl3lCUvQy3nkuNUw";
	//private final String tableid = "1QbELXQViIAszNyg_2NHOO9XcnN_kvaG1TLedqDc";
	public String greetServer(String acc, String country, boolean valid) throws IllegalArgumentException {
		/*DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity("thermus");
		//Query query = new Query("sequences");
		//List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		ent.setProperty("acc", acc);
		ent.setProperty("country", country);
		ent.setProperty("valid", valid);
		Key key = datastore.put( ent );
		
		return KeyFactory.keyToString(key);*/
		
		if( service == null ) {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			
			/*try {
				service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}*/
		}
		
		if( service != null ) {
			try {
				String rowid = run("select rowid from "+tableid+" where acc = '"+acc+"'", true);
				String[] split = rowid.split("\n");
				if( split.length > 1 ) {
					String ret = run("update "+tableid+" set country = '"+country+"' where rowid = '"+split[1]+"'", true);
					return ret;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public Map<String,String> getThermus() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("thermus");
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Map<String,String>	ret = new HashMap<String,String>();
		
		for( Entity e : seqsEntities ) {
			String str = (String)e.getProperty("country");
			//String val = str == null ? "" : str;
			Boolean b = (Boolean)e.getProperty("valid");
			if( b != null ) {
				if( str == null || str.length() == 0 ) str = ";"+Boolean.toString(b);
				else str += ";"+Boolean.toString(b);
			}
			ret.put( (String)e.getProperty("acc"), str );
		}
		return ret;
	}

	@Override
	public Map<String,String> saveSel(String name, String val) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		if( name != null ) {
			Entity ent = new Entity("selection");
			ent.setProperty("name", name);
			ent.setProperty("sel", val);
			Key key = datastore.put( ent );
		}
		
		Map<String,String>	retmap = new HashMap<String,String>();
		Query query = new Query("selection");	
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		for( Entity ent : seqsEntities ) {
			retmap.put( (String)ent.getProperty("name"), (String)ent.getProperty("sel") );
		}
		//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];		
		
		return retmap;
	}
	
	@Override
	public Map<String,String> saveThermusSel(String name, String val) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Map<String,String>	retmap = new HashMap<String,String>();
		if( name != null ) {
			Entity ent = new Entity("thermusselection");
			ent.setProperty("name", name);
			ent.setProperty("sel", new Text(val) );
			Key key = datastore.put( ent );
			
			retmap.put( name, val );
		} else {
			Query query = new Query("thermusselection");
			List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			for( Entity ent : seqsEntities ) {
				retmap.put( (String)ent.getProperty("name"), ((Text)ent.getProperty("sel")).getValue() );
			}
			//Sequences[] seqsArray = new Sequences[ seqsEntities.size() ];
		}
		
		return retmap;
	}
	
	private GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	
	@Override
	public String getThermusFusion() {
		System.setProperty(GoogleGDataRequest.DISABLE_COOKIE_HANDLER_PROPERTY, "true");
		if( service == null ) {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			/*try {
				service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}*/
		}
		
		if( service != null ) {
			try {
				String ret = run("select * from "+tableid, true);
				return ret;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public String run(String query, boolean isUsingEncId) throws IOException, ServiceException {
		   String lowercaseQuery = query.toLowerCase();
		   String encodedQuery = URLEncoder.encode(query, "UTF-8");

		   GDataRequest request;
		   // If the query is a select, describe, or show query, run a GET request.
		   if (lowercaseQuery.startsWith("select") ||
		       lowercaseQuery.startsWith("describe") ||
		       lowercaseQuery.startsWith("show")) {
		     URL url = new URL(SERVICE_URL + "?sql=" + encodedQuery + "&encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.QUERY, url,
		         ContentType.TEXT_PLAIN);
		   } else {
		     // Otherwise, run a POST request.
		     URL url = new URL(SERVICE_URL + "?encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.INSERT, url,
		         new ContentType("application/x-www-form-urlencoded"));
		     OutputStreamWriter writer = new OutputStreamWriter(request.getRequestStream());
		     writer.append("sql=" + encodedQuery);
		     writer.flush();
		   }

		   request.execute();

		   return getResultsText(request);
	}
	
	private String getResultsText(GDataRequest request) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(request.getResponseStream());
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb.toString();
	}
	
	private QueryResults getResults(GDataRequest request) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(request.getResponseStream());
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		List<String[]>	csvLines = new ArrayList<String[]>();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			csvLines.add( line.split(",") );
			
			line = bufferedStreamReader.readLine();
		}
		//CSVReader reader = new CSVReader(bufferedStreamReader);
		// The first line is the column names, and the remaining lines are the rows.
		List<String> columns = Arrays.asList(csvLines.get(0));
		List<String[]> rows = csvLines.subList(1, csvLines.size());
		QueryResults results = new QueryResults(columns, rows);
		
		return results;
	}
	
	private static class QueryResults {
		   final List<String> columnNames;
		   final List<String[]> rows;

		   public QueryResults(List<String> columnNames, List<String[]> rows) {
		     this.columnNames = columnNames;
		     this.rows = rows;
		   }

		  /**
		   * Prints the query results.
		   *
		   * @param the results from the query
		   */
		  public void print() {
		    String sep = "";
		    for (int i = 0; i < columnNames.size(); i++) {
		      System.out.print(sep + columnNames.get(i));
		      sep = ", ";
		    }
		    System.out.println();

		    for (int i = 0; i < rows.size(); i++) {
		      String[] rowValues = rows.get(i);
		      sep = "";
		      for (int j = 0; j < rowValues.length; j++) {
		        System.out.print(sep + rowValues[j]);
		        sep = ", ";
		      }
		      System.out.println();
		    }
		  }
	}

	@Override
	public String saveSeq(Map<String,String> jsonmap) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Set<String>	keyset = jsonmap.keySet();
		Query query = new Query("sequence");
		query.addFilter("name", FilterOperator.IN, keyset);
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		for( Entity e : seqsEntities ) {
			String name = (String)e.getProperty("name");
			String seq = (String)jsonmap.get( name );
			e.setProperty("seq", new Text(seq) );
			Key key = datastore.put( e );
			jsonmap.remove( name );
		}
		for( String keyval : keyset ) {
			Entity ent = new Entity("sequence");
			ent.setProperty("name", keyval);
			ent.setProperty("seq", new Text(jsonmap.get(keyval)) );
			Key key = datastore.put( ent );
		}
		return "";
	}

	@Override
	public Map<String, String> fetchSeq(String include) {
		Map<String,String>	retmap = new HashMap<String,String>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Set<String>	keyset = new HashSet<String>( Arrays.asList(include.split(",")) );
		Query query = new Query("sequence");
		query.addFilter("name", FilterOperator.IN, keyset);
		List<Entity> seqsEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		for( Entity e : seqsEntities ) {
			String name = (String)e.getProperty("name");
			String seq = ((Text)e.getProperty("seq")).getValue();
			retmap.put( name, seq );
		}
		for( String str : keyset ) {
			if( !retmap.containsKey(str) ) retmap.put(str, null);
		}
		return retmap;
	};
}
