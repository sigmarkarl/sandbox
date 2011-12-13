package org.simmi;

import java.io.IOException;

public class Blast {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String[] command = {"/opt/ncbi-blast-2.2.25+/bin/blastn","-db","16SMicrobial"};
			ProcessBuilder pb = new ProcessBuilder( command );
			Process p = pb.start();
			
			byte[] bb = new byte[1024];
			int r = System.in.read( bb );
			while( r > 0 ) {
				p.getOutputStream().write( bb, 0, r );
				r = System.in.read( bb );
			}
			
			r = p.getInputStream().read( bb );
			while( r > 0 ) {
				System.out.write( bb );
				r = p.getInputStream().read( bb );
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
