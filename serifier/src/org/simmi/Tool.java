package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Tool {
	public static void mu() throws IOException {
		FileReader fr = new FileReader("/home/olafur/YL_shotgun_seqcap_16.02.2014/assembly/yl_ylnew.blastout");
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		int matchcount = 0;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				line = br.readLine();
				while( !line.startsWith("Length=") ) {
					line = br.readLine();
				}
				int flen = Integer.parseInt( line.substring(7) );
				line = br.readLine();
				while( line != null && !line.startsWith("Length=") && !line.startsWith("Query=") ) {
					line = br.readLine();
				}
				if( line != null && line.startsWith("Length") ) {
					int nlen = Integer.parseInt( line.substring(7) );
					if( Math.abs( nlen-flen ) < 10 ) matchcount++;
				} else continue;
			}
			line = br.readLine();
		}
		br.close();
		
		System.err.println( "match " + matchcount );
	}
	
	public static void main(String[] args) {
		/*try {
			mu();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		
		Set<String>	set1 = new HashSet<String>();
		Set<String>	set2 = new HashSet<String>();
		
		try {
			//FileReader fr1 = new FileReader("/home/olafur/SeqCap_urvinnsla/seqcapcontrol.txt");
			FileReader fr1 = new FileReader("/virtual/secondseqcap.txt");
			BufferedReader br1 = new BufferedReader( fr1 );
			String line = br1.readLine();
			while( line != null ) {
				set1.add( line );
				line = br1.readLine();
			}
			fr1.close();
			
			//FileReader fr2 = new FileReader("/home/olafur/SeqCap_urvinnsla/seqcap.txt");
			FileReader fr2 = new FileReader("/virtual/seqcap.txt");
			BufferedReader br2 = new BufferedReader( fr2 );
			line = br2.readLine();
			while( line != null ) {
				set2.add( line );
				line = br2.readLine();
			}
			fr2.close();
			
			set1.removeAll( set2 );
			System.err.println( set1.size() );
			
			/*for( String s : set2 ) {
				System.err.println( s );
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
