package org.simmi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacebookTreeServlet extends HttpServlet {
	public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		Map m = req.getParameterMap();
		String[] str = (String[])m.get("signed_request");
		String s = str[0];
		
		respi( resp, s );
	}
	
	public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException {		
		respi( resp, null );
	}
	
	private void respi( HttpServletResponse resp, String s ) throws IOException {
		resp.setHeader("X-Frame-Options", "GOFORIT");
		PrintWriter pw = resp.getWriter();
		
		/*pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta property=\"signed_reqest\" content=\""+s+"\" />");
		pw.println("<script type=\"text/javascript\" language=\"javascript\" src=\"/org.simmi.Facebooktree/org.simmi.Facebooktree.nocache.js\"></script>" );
		pw.println("</head>");
		pw.println("<body>");
		pw.println("</body>");
		pw.println("</html>");*/
		
		InputStream is = this.getServletContext().getResourceAsStream("FacebookTree/Facebook.html");
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		
		String line = br.readLine();
		while( line != null ) {
			pw.println( line );
			line = br.readLine();
		}
		pw.close();
	}
}
