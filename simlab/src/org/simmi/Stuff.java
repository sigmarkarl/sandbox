package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stuff {

	static Map<Character,Integer> map = new HashMap<Character,Integer>();
	static {
		map.put('A',0);
		map.put('R',1);
		map.put('N',2);
		map.put('D',3);
		map.put('C',4);
		map.put('Q',5);
		map.put('E',6);
		map.put('G',7);
		map.put('H',8);
		map.put('I',9);
		map.put('L',10);
		map.put('K',11);
		map.put('M',12);
		map.put('F',13);
		map.put('P',14);
		map.put('S',15);
		map.put('T',16);
		map.put('W',17);
		map.put('Y',18);
		map.put('V',19);
		
	};
	
	public static class Ss {
		public Ss(String pp, double dd) {
			this.pep = pp;
			this.score = dd;
		}
		
		String	pep;
		double	score;
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/B5401/test.dat");
			BufferedReader br = new BufferedReader(fr);
			List<Ss>	lss = new ArrayList<Ss>();
			String line = br.readLine();
			while( line != null ) {
				String[]	split = line.split("[\t ]+");
				if( split.length > 1 ) {
					lss.add( new Ss(split[0],Double.parseDouble(split[1])) );
				}
				line = br.readLine();
			}
			byte[]	bb = new byte[9*20*lss.size()];
			Arrays.fill( bb, (byte)0 );
			int i = 0;
			for( Ss ss : lss ) {
				String pep = ss.pep;
				if( pep == null ) {
					System.err.println();
				}
				for( int a = 0; a < 9; a++ ) {
					char c = pep.charAt(a);
					bb[9*20*i + 20*a + map.get(c)] = 1;
				}
				i++;
			}
			
			br.close();
			FileWriter	fw = new FileWriter("/home/sigmar/mat.txt");
			i = 1;
			for( byte b : bb ) {
				if( i % (9*20) == 0 ) {
					if( b == 0 ) fw.write("0\n");
					else fw.write("1\n");
				} else {
					if( b == 0 ) fw.write("0\t");
					else fw.write("1\t");
				}
				i++;
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
