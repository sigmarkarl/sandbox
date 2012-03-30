package org.simmi.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simmi.client.GreetingService;
import org.simmi.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {	
	public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		/*resp.setContentType("text/xml");
		try {
			PrintWriter pw = resp.getWriter();
			
			pw.println("<xml>");
			pw.println("<simmi attr=\"erm\">");
			pw.println("hoho");
			pw.println("</simmi>");
			pw.println("</xml>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//super.dopo*/
		//resp.setContentType("text/html");
		
		//InputStream is = this.getClass().getResourceAsStream("/Webconnectron.html");
		/*FileInputStream is = new FileInputStream( "Treedraw.html" );
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		PrintWriter pw = resp.getWriter();
		String line = br.readLine();
		while( line != null ) {
			pw.println(line);
			line = br.readLine();
		}
		br.close();
		pw.close();*/
		
		PrintWriter pw = resp.getWriter();
		pw.println("<html>");
		pw.println("<head>");
		pw.println( "<script type=\"text/javascript\" language=\"javascript\" src=\"org.simmi.Facebooktree/org.simmi.Facebooktree.nocache.js\"></script>" );
		pw.println("</head>");
		pw.println("<body>");
		pw.println("</body>");
		pw.println("</html>");
		pw.close();
	}
	
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
}
