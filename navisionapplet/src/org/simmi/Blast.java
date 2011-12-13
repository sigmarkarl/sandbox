package org.simmi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Blast {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.setIn( new FileInputStream("/home/horfrae/all16S.fsa") );
			String[] command = {"/opt/ncbi-blast-2.2.25+/bin/blastn","-db","/usr/lib/cgi-bin/16SMicrobial","-num_alignments","1","-num_descriptions","1"};
			ProcessBuilder pb = new ProcessBuilder(	 command );
			final Process p = pb.start();
			
			new Thread() {
				public void run() {
					try {
						OutputStream po = p.getOutputStream();
						OutputStreamWriter osw = new OutputStreamWriter( po );
						BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
						String line = br.readLine();
						while( line != null ) {
							osw.write( line+"\n" );
							line = br.readLine();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}.run();
			
			new Thread() {
				public void run() {
					try {
						InputStream pi = p.getInputStream();
						BufferedReader br = new BufferedReader( new InputStreamReader(pi) );
						String line = br.readLine();
						while( line != null ) {
							System.out.println( line );
							line = br.readLine();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}.run();
			
			new Thread() {
				public void run() {
					try {
						byte[] bb = new byte[1024];
						InputStream pe = p.getErrorStream();
						int r = pe.read( bb );
						while( r > 0 ) {
							System.out.write( bb, 0, r );
							r = pe.read( bb );
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
