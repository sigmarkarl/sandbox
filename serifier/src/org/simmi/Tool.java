package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Tool {
	public static void main(String[] args) {
		Set<String>	set1 = new HashSet<String>();
		Set<String>	set2 = new HashSet<String>();
		
		try {
			FileReader fr1 = new FileReader("/home/sigmar/sim.txt");
			BufferedReader br1 = new BufferedReader( fr1 );
			String line = br1.readLine();
			while( line != null ) {
				set1.add( line );
				line = br1.readLine();
			}
			fr1.close();
			
			FileReader fr2 = new FileReader("/home/sigmar/sims.txt");
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
