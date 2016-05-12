package org.simmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadFile extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		InputStream is = req.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] bb = new byte[1024];
        int r = is.read(bb);
        while( r > 0 ) {
        	baos.write(bb, 0, r);
        	r = is.read(bb);
        }
        byte[] allcontent = baos.toByteArray();
        for( int i = 0; i < allcontent.length; i++ ) {
        	if( allcontent[i] == 0 ) allcontent[i] = 'a';
        }
        String allstr = new String( allcontent, 0, allcontent.length );
        
        System.err.println( allstr );
	}
}
