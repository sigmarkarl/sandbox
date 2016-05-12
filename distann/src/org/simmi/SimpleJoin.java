package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleJoin {
	public static List<Collection<String>>	setlist = new ArrayList<Collection<String>>();
	
	private static Map<String,StringBuilder> loci2dnasequence( Reader rd ) throws IOException {
		Map<String,StringBuilder>	seqmap = new HashMap<String,StringBuilder>();
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String name = null;
		StringBuilder ac = new StringBuilder();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( ac.length() > 0 ) seqmap.put(name, ac);
				
				ac = new StringBuilder();
				name = line.substring(1).split(" ")[0];
				
				int v = name.indexOf("contig");
			} else ac.append( line.trim()+"" );
			line = br.readLine();
		}
		if( ac.length() > 0 ) seqmap.put(name, ac);
		br.close();
		
		return seqmap;
	}
	
	public static Collection<String> checkCurrentSet( String name, Collection<String> currentset ) {
		Collection<String> retset = null;
		for( Collection<String> set : setlist ) {
			if( set.contains( name ) ) {
				retset = set;
				break;
			}
		}
		
		if( retset == null ) {
			if( currentset == null ) {
				retset = new HashSet<String>();
				setlist.add( retset );
			} else {
				retset = currentset;
			}
			retset.add( name );
		} else if( currentset != null ) {
			if( retset != currentset ) {
				retset.addAll( currentset );
				setlist.remove( currentset );
				currentset.clear();
			}
		}
		
		return retset;
	}
	
	public static void main(String[] args) {
		final int minlen = 80;
		File	f = new File( "/home/sigmar/5.TCA.454Reads.blastout" );
		try {
			Set<String>	allset = new HashSet<String>();
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			
			//int curl = 0;
			int i = 0;
			Collection<String>	currentset = null;
			while( line != null ) {
				if( line.startsWith("Query=") ) {
					String[] split = line.split(" ");
					String name = split[1];
					String len = split[ split.length-1 ];
					int curl = Integer.parseInt( len.substring(7) );
					
					//int f1 = allset.size();
					//int n1 = 0;
					
					if( curl > minlen ) {
						allset.add( name );
						
						/*if( currentset != null && !setlist.contains( currentset ) ) {
							setlist.add( currentset );
						}*/
						
						/*for( Collection<String> coll : setlist ) {
							n1 += coll.size();
						}
						
						if( n1 != f1 ) {
							System.err.println( "erm" );
						}*/
						
						currentset = checkCurrentSet( name, null );
					} else currentset = null;
				} else if( currentset != null ) {
					String trim = line.trim();
					if( trim.startsWith( "GFL" ) ) {
						String[] split = trim.split("[ ]+");
						String name = split[0];
						String score = split[ split.length-2 ];
						String eval = split[ split.length-1 ];
						String len = split[ split.length-3 ];
						
						int l = Integer.parseInt( len.substring(7) );
						double dscore = Double.parseDouble( score );
						if( l > minlen && dscore > 100.0 ) {					
							allset.add( name );
							currentset = checkCurrentSet( name, currentset );
						}
					}
				}
				
				if( i++ % 100000 == 0 ) {
					System.err.println( "line "+i+" setsize "+setlist.size() );
				}
				line = br.readLine();
			}
			br.close();
			if( currentset != null && !setlist.contains( currentset ) ) setlist.add( currentset );
			
			System.err.println( "allsize " + allset.size() );
			int sum = 0;
			System.err.println( "Number of sets: " + setlist.size() );
			for( Collection<String> set : setlist ) {
				System.err.println( "\t"+set.size() );
				sum += set.size();
			}
			System.err.println( "sum " + sum );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
