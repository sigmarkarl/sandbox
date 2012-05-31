package org.simmi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
			try {
				//FileReader 		fr = new FileReader(searchnum+".TCA.454Reads.fna.gz");
				FileInputStream fis = new FileInputStream( searchnum+".TCA.454Reads.fna.gz" );
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
		}
		
		System.out.println( ret.toString() );
	}

}
