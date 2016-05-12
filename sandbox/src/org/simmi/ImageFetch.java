package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class ImageFetch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader("/home/horfrae/workspace/vote/war/list.txt");
			BufferedReader br = new BufferedReader( fr );
			
			char[] cc = new char[1024];
			StringBuilder	sb = new StringBuilder();
			URL url = new URL( "http://www.matis.is/um-matis-ohf/starfsfolk/" );
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader( is );
			int r = isr.read( cc );
			while( r > 0 ) {
				sb.append( cc );
				
				r = isr.read( cc );
			}
			is.close();
			
			Random rnd = new Random();
			
			int u = 0;
			String line = br.readLine();
			while( line != null ) {
				/*String[] split = line.split("\t");
				String name = split[0];
				
				int k = sb.indexOf( name );
				int um = sb.indexOf( "/um-matis-ohf", 0 );
				while( um < k ) {
					u = um;
					um = sb.indexOf( "/um-matis-ohf", u+1 );
				}
				
				if( k > 0 ) {
					int m = sb.indexOf( "mailto:", k );
					if( m > 0 ) {
						int g = sb.indexOf( "\"", m );
						String email = sb.substring(m+7, g).replace("()", "@");
						String urlstr = "http://www.matis.is"+sb.substring(u, k-2);
						url = new URL( urlstr );
						
						StringBuilder imgstr = new StringBuilder();
						is = url.openStream();
						isr = new InputStreamReader( is );
						r = isr.read( cc );
						while( r > 0 ) {
							imgstr.append( cc );
							
							r = isr.read( cc );
						}
						is.close();
						
						int b = imgstr.indexOf( "<img src=\"/media/stmyndir" );
						if( b > 0 ) {
							int e = imgstr.indexOf( "\"", b+11 );
							if( split.length == 1 ) System.out.println( line + "\t" + email + "\t" + imgstr.substring(b,e+1).replace("/media/", "http://www.matis.is/media/") ); 
							else System.out.println( line + "\t" + imgstr.substring(b,e+1).replace("/media/", "http://www.matis.is/media/") );
						} else {
							if( split.length == 1 ) System.out.println( line + "\t" + email); 
							else System.out.println( line );
						}
						//"\t" + email.replace("()", "@") +
					} else System.out.println( line );
				} else*/ System.out.println( line + "\t" + Math.abs(rnd.nextLong()) );
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
