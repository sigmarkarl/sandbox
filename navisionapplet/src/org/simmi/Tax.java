package org.simmi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class Tax {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuilder ret = new StringBuilder();
		String str = "";
		
		//str = "eyjosilva_HM2RNR208JLXW0";
		//str = "snaedisTCTCTATGCG_HWBYD8R01A4J1X";
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bb = new byte[1024];
			int r = System.in.read( bb );
			while( r > 0 ) {
				baos.write( bb, 0, r );
				r = System.in.read( bb );
			}
			baos.close();
			
			str = new String( baos.toByteArray() );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String searchnum = "";
		int ind = -1;
		if( str != null ) {
			ind = str.indexOf('_');
			searchnum = str.substring(0,ind);
			/*try {
				searchnum = Integer.parseInt( str.substring(0, ind) );
			} catch( Exception e ) {}*/
		}
		
		if( ind != -1 ) {
			String input = str.substring(ind+1);
			String[] split = input.split( "," );
			for( int i = 0; i < split.length; i++ ) {
				split[i] = split[i].trim();
			}
			try {
				//FileReader 		fr = new FileReader(searchnum+".TCA.454Reads.fna.gz");
				File f = new File( /*"/var/www/cgi-bin/"+*/searchnum+".TCA.454Reads.fna.gz" );
				//System.err.println( f.getName() + "  " + split[0] );
				FileInputStream fis = new FileInputStream( f );
				GZIPInputStream gis = new GZIPInputStream( fis );
				InputStreamReader	isr = new InputStreamReader( gis );
				BufferedReader 	br = new BufferedReader( isr );
				String line = br.readLine();
				boolean inside = false;
				while( line != null ) {
					if( line.startsWith(">") ) {
						//System.out.println( line );
						inside = false;
						for( String s : split ) {
							//System.out.println( "mumumu " + s );
							if( line.contains( s ) ) {
								ret.append( line+"\n" );
								inside = true;
								break;
							}
						}
					} else if( inside ) {
						ret.append( line+"\n" );
					}
					line = br.readLine();
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println( ret.toString() );
	}
}
