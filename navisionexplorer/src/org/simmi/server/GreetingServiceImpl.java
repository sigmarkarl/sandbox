package org.simmi.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simmi.client.GreetingService;
import org.simmi.client.Person;
import org.simmi.shared.FieldVerifier;

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
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	public List<Person> loadPersons() throws SQLException {
		List<Person>	plist = new ArrayList<Person>();
		
		String sql = "select [Name], [User ID] from [MATIS].[dbo].[User]";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			String kt = rs.getString(2);
			if( kt.length() == 10 ) plist.add( new Person( rs.getString(1), kt ) );
		}
		
		rs.close();
		ps.close();
		
		return plist;
	}	
	
	Connection con = null;
	public void connect( String connectionUrl ) throws SQLException {
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		if( con == null || !con.isClosed() ) con = DriverManager.getConnection( connectionUrl );
	}

	@Override
	public List<Person> getAllUsers() throws IllegalArgumentException {
		/*try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		
		String connectionUrl = "jdbc:sqlserver://navision.rf.is;databaseName=MATIS;user=simmi;password=mirodc30;";*/
		
		List<Person>	persons = null;
		/*try {
			connect( connectionUrl );
			persons = loadPersons();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return persons;
	}

	@Override
	public String getFrystilager() {
		String ret = null;
		
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			
			String sql = "select * from .[dbo].Frystilager";
			con.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
