package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GBK2AminoFasta {

	public static class Anno {
		String 	name;
		int		start;
		int		stop;
		boolean comp;
	};
	
	static Map<String,String>	amimap = new HashMap<String,String>();
	static Map<String,String>	revcom = new HashMap<String,String>();
	static {
		amimap.put("TTT","F");
		amimap.put("TTC","F");
		amimap.put("TTA","L");
		amimap.put("TTG","L");
		amimap.put("TCT","S");
		amimap.put("TCC","S");
		amimap.put("TCA","S");
		amimap.put("TCG","S");
		amimap.put("TAT","Y");
		amimap.put("TAC","Y");
		amimap.put("TAA","1");
		amimap.put("TAG","0");
		amimap.put("TGT","C");
		amimap.put("TGC","C");
		amimap.put("TGA","0");
		amimap.put("TGG","W");
		amimap.put("CTT","L");
		amimap.put("CTC","L");
		amimap.put("CTA","L");
		amimap.put("CTG","L");
		amimap.put("CCT","P");
		amimap.put("CCC","P");
		amimap.put("CCA","P");
		amimap.put("CCG","P");
		amimap.put("CAT","H");
		amimap.put("CAC","H");
		amimap.put("CAA","Q");
		amimap.put("CAG","Q");
		amimap.put("CGT","R");
		amimap.put("CGC","R");
		amimap.put("CGA","R");
		amimap.put("CGG","R");
		amimap.put("ATT","I");
		amimap.put("ATC","I");
		amimap.put("ATA","I");
		amimap.put("ATG","M");
		amimap.put("ACT","T");
		amimap.put("ACC","T");
		amimap.put("ACA","T");
		amimap.put("ACG","T");
		amimap.put("AAT","N");
		amimap.put("AAC","N");
		amimap.put("AAA","K");
		amimap.put("AAG","K");
		amimap.put("AGT","S");
		amimap.put("AGC","S");
		amimap.put("AGA","R");
		amimap.put("AGG","R");
		amimap.put("GTT","V");
		amimap.put("GTC","V");
		amimap.put("GTA","V");
		amimap.put("GTG","V");
		amimap.put("GCT","A");
		amimap.put("GCC","A");
		amimap.put("GCA","A");
		amimap.put("GCG","A");
		amimap.put("GAT","D");
		amimap.put("GAC","D");
		amimap.put("GAA","E");
		amimap.put("GAG","E");
		amimap.put("GGT","G");
		amimap.put("GGC","G");
		amimap.put("GGA","G");
		amimap.put("GGG","G");
		
		revcom.put("TTT","AAA");
		revcom.put("TTC","GAA");
		revcom.put("TTA","TAA");
		revcom.put("TTG","CAA");
		revcom.put("TCT","AGA");
		revcom.put("TCC","GGA");
		revcom.put("TCA","TGA");
		revcom.put("TCG","CGA");
		revcom.put("TAT","ATA");
		revcom.put("TAC","GTA");
		revcom.put("TAA","TTA");
		revcom.put("TAG","CTA");
		revcom.put("TGT","ACA");
		revcom.put("TGC","GCA");
		revcom.put("TGA","TCA");
		revcom.put("TGG","CCA");
		revcom.put("CTT","AAG");
		revcom.put("CTC","GAG");
		revcom.put("CTA","TAG");
		revcom.put("CTG","CAG");
		revcom.put("CCT","AGG");
		revcom.put("CCC","GGG");
		revcom.put("CCA","TGG");
		revcom.put("CCG","CGG");
		revcom.put("CAT","ATG");
		revcom.put("CAC","GTG");
		revcom.put("CAA","TTG");
		revcom.put("CAG","CTG");
		revcom.put("CGT","ACG");
		revcom.put("CGC","GCG");
		revcom.put("CGA","TCG");
		revcom.put("CGG","CCG");
		revcom.put("ATT","AAT");
		revcom.put("ATC","GAT");
		revcom.put("ATA","TAT");
		revcom.put("ATG","CAT");
		revcom.put("ACT","AGT");
		revcom.put("ACC","GGT");
		revcom.put("ACA","TGT");
		revcom.put("ACG","CGT");
		revcom.put("AAT","ATT");
		revcom.put("AAC","GTT");
		revcom.put("AAA","TTT");
		revcom.put("AAG","CTT");
		revcom.put("AGT","ACT");
		revcom.put("AGC","GCT");
		revcom.put("AGA","TCT");
		revcom.put("AGG","CCT");
		revcom.put("GTT","AAC");
		revcom.put("GTC","GAC");
		revcom.put("GTA","TAC");
		revcom.put("GTG","CAC");
		revcom.put("GCT","AGC");
		revcom.put("GCC","GGC");
		revcom.put("GCA","TGC");
		revcom.put("GCG","CGC");
		revcom.put("GAT","ATC");
		revcom.put("GAC","GTC");
		revcom.put("GAA","TTC");
		revcom.put("GAG","CTC");
		revcom.put("GGT","ACC");
		revcom.put("GGC","GCC");
		revcom.put("GGA","TCC");
		revcom.put("GGG","CCC");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader( args[0] );
			BufferedReader	br = new BufferedReader( fr );
			
			String line = br.readLine();
			Anno		anno = null;
			List<Anno>	annolist = new ArrayList<Anno>();
			
			while( line != null ) {
				String trimline = line.trim();
				
				if( trimline.startsWith("CDS") || trimline.startsWith("gene") ) {
					anno = new Anno();
					String[] split = trimline.split("[\t ]+");
					if( split[1].startsWith("compl") ) {
						int iof = split[1].indexOf(")");
						String substr = split[1].substring(11, iof);
						String[] nsplit = substr.split("\\.\\.");
						anno.start = Integer.parseInt( nsplit[0] );
						anno.stop = Integer.parseInt( nsplit[1] );
						anno.comp = true;
					} else {
						String[] nsplit = split[1].split("\\.\\.");
						anno.start = Integer.parseInt( nsplit[0] );
						anno.stop = Integer.parseInt( nsplit[1] );
						anno.comp = false;
					}
				} else if( trimline.startsWith("/product") ) {
					anno.name = trimline.substring(10,trimline.length()-1);
					annolist.add( anno );
				} else if( trimline.startsWith("ORIGIN") ) {
					break;
				}
				
				line = br.readLine();
			}
			
			StringBuffer	strbuf = new StringBuffer();
			line = br.readLine();
			while( line != null ) {
				strbuf.append( line.replaceAll("[\t 1234567890]+", "") );
				
				line = br.readLine();
			}
			
			PrintStream ps = new PrintStream( new File("/home/sigmar/stuff2.txt") ); //System.out;
			
			int c = 0;
			for( Anno ao : annolist ) {
				ps.println( ">"+ao.name + " " + ao.comp );
				int start = Math.min( ao.start, ao.stop );
				int stop = Math.max( ao.start, ao.stop );
				String 	val = strbuf.substring( start, stop+1 );
				
				//System.err.println(val);
				//String	ami = "";
				
				int t = 0;
				if( ao.start > ao.stop || ao.comp ) {
					for( int i = val.length()-3; i > 2; i-=3 ) {
						//ami += 
						String first = val.substring(i, i+3).toUpperCase();
						String second = revcom.get(first);
						String str = amimap.get( second );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else ps.print( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) ps.println();
						}
					}
				} else {
					for( int i = 0; i < val.length()-2; i+=3 ) {
						//ami += 
						String first = val.substring(i, i+3).toUpperCase();
						String str = amimap.get( first );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) {
								break;
							} else ps.print( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) ps.println();
						}
					}
				}
				ps.println();
				
				//if( c++ > 10 ) break;
			}
			ps.close();
			
			/*FileWriter fw = new FileWriter("/home/sigmar/stuff2.txt");
			fw.write( strbuf.toString() );
			fw.close();*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
