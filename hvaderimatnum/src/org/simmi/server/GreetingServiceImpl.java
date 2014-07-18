package org.simmi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.simmi.client.GreetingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	//private GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	private static final boolean isUsingEncId = true;
	
	public String greetServer(String query) {
		String res = "";
	
		/*try {
			if( service == null ) {
				service = new GoogleService("fusiontables", "fusiontables.ApiExample");
				service.setConnectTimeout(0);
			}
			
			String lowercaseQuery = query.toLowerCase();
			String encodedQuery = URLEncoder.encode(query, "UTF-8");
			 GDataRequest request;
			   // If the query is a select, describe, or show query, run a GET request.
			   if (lowercaseQuery.startsWith("select") ||
			       lowercaseQuery.startsWith("describe") ||
			       lowercaseQuery.startsWith("show")) {
			     URL url = new URL(SERVICE_URL + "?sql=" + encodedQuery + "&encid=" + isUsingEncId);
			     request = service.getRequestFactory().getRequest(RequestType.QUERY, url, ContentType.TEXT_PLAIN);
			   } else {
			     // Otherwise, run a POST request.
			     URL url = new URL(SERVICE_URL + "?encid=" + isUsingEncId);
			     request = service.getRequestFactory().getRequest(RequestType.INSERT, url,
			         new ContentType("application/x-www-form-urlencoded"));
			     OutputStreamWriter writer = new OutputStreamWriter(request.getRequestStream());
			     writer.append("sql=" + encodedQuery);
			     writer.flush();
			   }
		
			   request.setConnectTimeout(0);
			   request.execute();
		
			   res = getResultsText( request.getResponseStream() ).toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}*/
	
		  return res;
	}

	private StringBuilder getResultsText( InputStream is ) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader( is, "UTF-8" );
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + '\n' );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb;
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
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
