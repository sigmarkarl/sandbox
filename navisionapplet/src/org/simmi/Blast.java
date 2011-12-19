package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;


public class Blast {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			byte[] bb = new byte[1024];
			int r = System.in.read( bb );
			String id = new String( bb, 0, r ).trim();
			//String id = "bleh";
			//System.out.println( "bleh " + id );
			
			String[] command = {"/opt/ncbi-blast-2.2.25+/bin/blastn","-db","16SMicrobial","-num_alignments","1","-num_descriptions","1"};
			ProcessBuilder pb = new ProcessBuilder(	 command );
			pb.redirectErrorStream( true );
			final Process p = pb.start();
			
			OutputStream o = p.getOutputStream();
			PrintStream po = new PrintStream( o );
			
			FileReader fr = new FileReader("parc_thermus.fna");
			BufferedReader	br = new BufferedReader( fr );
			
			boolean export = false;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					int i = line.indexOf('.');
					if( i > 0 ) {
						String val = line.substring(1, i).trim();
						
						if( val.equals( id ) ) {
							po.println( line );
							export = true;
						} else export = false;
					}
				} else if( export ) {
					po.println( line );
				}
				line = br.readLine();
			}
			br.close();
			po.close();
			o.close();
			
			InputStream i = p.getInputStream();
			br = new BufferedReader( new InputStreamReader(i) );
			line = br.readLine();
			while( line != null ) {
				System.out.println( line );
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*try {
			//System.setIn( new FileInputStream("/home/horfrae/all16S.fsa") );
			/*String[] command = {"/opt/ncbi-blast-2.2.25+/bin/blastn","-db","/usr/lib/cgi-bin/16SMicrobial","-num_alignments","1","-num_descriptions","1"};
			ProcessBuilder pb = new ProcessBuilder(	 command );
			pb.redirectErrorStream( true );
			final Process p = pb.start();*
			
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}*/
	}
}
