package org.simmi.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Channel extends HttpServlet {
	public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		long cexp = 60*60*24*365;
		resp.setHeader("Pragma", "Public");
		resp.setHeader("Cache-Control", "max-age="+cexp);
		Calendar.getInstance();
		//resp.setHeader("Expires", System.currentTimeMillis()+);
		PrintWriter pw = resp.getWriter();
		pw.println( "<script src=\"//connect.facebook.net/en_US/all.js\"></script>" );
		pw.close();
	}
}
