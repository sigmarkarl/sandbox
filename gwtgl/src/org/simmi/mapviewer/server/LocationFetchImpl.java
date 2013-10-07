package org.simmi.mapviewer.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.simmi.mapviewer.client.LocationFetch;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LocationFetchImpl extends RemoteServiceServlet implements LocationFetch {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String fetchLocations() throws IOException {
		File		f = new File("locs.txt");
		FileReader fr = new FileReader( f );
		
		char[] cbuf = new char[ (int)f.length() ];
		fr.read(cbuf);
		
		return new String( cbuf );
	}
}
