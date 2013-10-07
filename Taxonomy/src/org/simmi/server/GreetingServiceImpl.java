package org.simmi.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.simmi.client.GreetingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	
	public String greetServer(String input, String searchnum) throws IllegalArgumentException {		
		StringBuilder ret = new StringBuilder();
		
		String[] split = input.split( "," );
		try {
			//FileReader 		fr = new FileReader(searchnum+".TCA.454Reads.fna.gz");
			FileInputStream fis = new FileInputStream(searchnum+".TCA.454Reads.fna.gz");
			GZIPInputStream gis = new GZIPInputStream( fis );
			InputStreamReader	isr = new InputStreamReader( gis );
			BufferedReader 	br = new BufferedReader( isr );
			String line = br.readLine();
			boolean inside = false;
			while( line != null ) {
				if( line.startsWith(">") ) {
					inside = false;
					for( String s : split ) {
						if( line.contains( s ) ) {
							ret.append( line+"\n" );
							inside = true;
							break;
						}
					}
				} else if( inside ) ret.append( line+"\n" );
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret.toString();
	}

	@Override
	public String getRemoteAddress() throws IllegalArgumentException {
		return this.getThreadLocalRequest().getRemoteAddr();
	}
}
