package org.simmi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

public class GeneSet {
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static Map<Character,Character>	sidechainpolarity = new HashMap<Character,Character>();
	static Map<Character,Integer>	sidechaincharge = new HashMap<Character,Integer>();
	static Map<Character,Double>	hydropathyindex = new HashMap<Character,Double>();
	static Map<Character,Double>	aamass = new HashMap<Character,Double>();
	static Map<Character,Double>	isoelectricpoint = new HashMap<Character,Double>();
	//abundance
	//aliphatic - aromatic
	//size
	//sortcoeff

	static class StrSort implements Comparable<StrSort> {
		double	d;
		String	s;
		
		StrSort( double d, String s ) {
			this.d = d;
			this.s = s;
		}

		@Override
		public int compareTo(StrSort o) {
			double mis = o.d - d;
			
			return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
		}
	};
	
	static class Erm implements Comparable<Erm> {
		double	d;
		char	c;
		
		Erm( double d, char c ) {
			this.d = d;
			this.c = c;
		}

		@Override
		public int compareTo(Erm o) {
			double mis = d - o.d;
			
			return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
		}
	};
	static List<Erm>	uff = new ArrayList<Erm>();
	static List<Erm>	uff2 = new ArrayList<Erm>();
	static List<Erm>	uff3 = new ArrayList<Erm>();
	static List<Erm>	mass = new ArrayList<Erm>();
	static List<Erm>	isoel = new ArrayList<Erm>();
	
	static {
		sidechainpolarity.put('A', 'n');
		sidechainpolarity.put('R', 'p');
		sidechainpolarity.put('N', 'p');
		sidechainpolarity.put('D', 'p');
		sidechainpolarity.put('C', 'n');
		sidechainpolarity.put('E', 'p');
		sidechainpolarity.put('Q', 'p');
		sidechainpolarity.put('G', 'n');
		sidechainpolarity.put('H', 'p');
		sidechainpolarity.put('I', 'n');
		sidechainpolarity.put('L', 'n');
		sidechainpolarity.put('K', 'p');
		sidechainpolarity.put('M', 'n');
		sidechainpolarity.put('F', 'n');
		sidechainpolarity.put('P', 'n');
		sidechainpolarity.put('S', 'p');
		sidechainpolarity.put('T', 'p');
		sidechainpolarity.put('W', 'n');
		sidechainpolarity.put('Y', 'p');
		sidechainpolarity.put('V', 'n');
		
		sidechaincharge.put('A', 0);
		sidechaincharge.put('R', 1);
		sidechaincharge.put('N', 0);
		sidechaincharge.put('D', -1);
		sidechaincharge.put('C', 0);
		sidechaincharge.put('E', -1);
		sidechaincharge.put('Q', 0);
		sidechaincharge.put('G', 0);
		sidechaincharge.put('H', 0);
		sidechaincharge.put('I', 0);
		sidechaincharge.put('L', 0);
		sidechaincharge.put('K', 1);
		sidechaincharge.put('M', 0);
		sidechaincharge.put('F', 0);
		sidechaincharge.put('P', 0);
		sidechaincharge.put('S', 0);
		sidechaincharge.put('T', 0);
		sidechaincharge.put('W', 0);
		sidechaincharge.put('Y', 0);
		sidechaincharge.put('V', 0);
		
		hydropathyindex.put('A', 1.8);
		hydropathyindex.put('R', -4.5);
		hydropathyindex.put('N', -3.5);
		hydropathyindex.put('D', -3.5);
		hydropathyindex.put('C', 2.5);
		hydropathyindex.put('E', -3.5);
		hydropathyindex.put('Q', -3.5);
		hydropathyindex.put('G', -0.4);
		hydropathyindex.put('H', -3.2);
		hydropathyindex.put('I', 4.5);
		hydropathyindex.put('L', 3.8);
		hydropathyindex.put('K', -3.9);
		hydropathyindex.put('M', 1.9);
		hydropathyindex.put('F', 2.8);
		hydropathyindex.put('P', -1.6);
		hydropathyindex.put('S', -0.8);
		hydropathyindex.put('T', -0.7);
		hydropathyindex.put('W', -0.9);
		hydropathyindex.put('Y', -1.3);
		hydropathyindex.put('V', 4.2);
		
		aamass.put('A', 89.09404 );
		aamass.put('C', 121.15404 );
		aamass.put('D', 133.10384 );
		aamass.put('E', 147.13074 );
		aamass.put('F', 165.19184 );
		aamass.put('G', 75.06714 );
		aamass.put('H', 155.15634 );
		aamass.put('I', 131.17464 );
		aamass.put('K', 146.18934 );
		aamass.put('L', 131.17464 );
		aamass.put('M', 149.20784 );
		aamass.put('N', 132.11904 );
		aamass.put('O', 100.0 );
		aamass.put('P', 115.13194 );
		aamass.put('Q', 146.14594 );
		aamass.put('R', 174.20274 );
		aamass.put('S', 105.09344 );
		aamass.put('T', 119.12034 );
		aamass.put('U', 168.053 );
		aamass.put('V', 117.14784 );
		aamass.put('W', 204.22844 );
		aamass.put('Y', 181.19124 );
		
		isoelectricpoint.put('A', 6.01 );
		isoelectricpoint.put('C', 5.05 );
		isoelectricpoint.put('D', 2.85 );
		isoelectricpoint.put('E', 3.15 );
		isoelectricpoint.put('F', 5.49 );
		isoelectricpoint.put('G', 6.06 );
		isoelectricpoint.put('H', 7.6 );
		isoelectricpoint.put('I', 6.05 );
		isoelectricpoint.put('K', 9.6 );
		isoelectricpoint.put('L', 6.01 );
		isoelectricpoint.put('M', 5.74 );
		isoelectricpoint.put('N', 5.41 );
		isoelectricpoint.put('O', 21.0 );
		isoelectricpoint.put('P', 6.3 );
		isoelectricpoint.put('Q', 5.65 );
		isoelectricpoint.put('R', 10.76 );
		isoelectricpoint.put('S', 5.68 );
		isoelectricpoint.put('T', 5.6 );
		isoelectricpoint.put('U', 20.0 );
		isoelectricpoint.put('V', 6.0 );
		isoelectricpoint.put('W', 5.89 );
		isoelectricpoint.put('Y', 5.64 );
		
		for( char c : hydropathyindex.keySet() ) {
			double d = hydropathyindex.get(c);
			uff.add( new Erm( d, c ) );
		}
		Collections.sort( uff );
		
		for( char c : sidechainpolarity.keySet() ) {
			double d = sidechainpolarity.get(c);
			uff2.add( new Erm( d, c ) );
		}
		Collections.sort( uff2 );
		
		for( char c : sidechaincharge.keySet() ) {
			double d = sidechaincharge.get(c);
			uff3.add( new Erm( d, c ) );
		}
		Collections.sort( uff3 );
		
		for( char c : aamass.keySet() ) {
			double d = aamass.get(c);
			mass.add( new Erm( d, c ) );
		}
		Collections.sort( mass );
		
		for( char c : isoelectricpoint.keySet() ) {
			double d = isoelectricpoint.get(c);
			isoel.add( new Erm( d, c ) );
		}
		Collections.sort( isoel );
	}
	
	static Map<String,String>		swapmap = new HashMap<String,String>();
	public static void func1( String[] names, File dir ) throws IOException {
		Map<String,String>		allgenes = new HashMap<String,String>();
		Map<String,Set<String>>	geneset = new HashMap<String,Set<String>>();
		Map<String,Set<String>>	geneloc = new HashMap<String,Set<String>>();
		
		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setErr( ps );
		
		for( String name : names ) {
			File 			f = new File( dir, name );
			Set<String>		set = new HashSet<String>();
			
			FileReader		fr = new FileReader( f );
			BufferedReader 	br = new BufferedReader( fr );
			String 	query = null;
			String	evalue = null;
			String line = br.readLine();
			while( line != null ) {
				String trim = line.trim();
				if( trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") ) {
					String[] split = trim.split("\\|");
					set.add( split[1] );
					
					if( !allgenes.containsKey( split[1] ) || allgenes.get( split[1] ) == null ) {
						allgenes.put( split[1], split.length > 1 ? split[2].trim() : null );
					}
					
					Set<String>	locset = null;
					if( geneloc.containsKey( split[1] ) ) {
						locset = geneloc.get(split[1]);
					} else {
						locset = new HashSet<String>();
						geneloc.put(split[1], locset);
					}
					//locset.add( swapmap.get(name)+"_"+query + " " + evalue );
					locset.add( name+"_"+query + " " + evalue );
					
					query = null;
				} else if( trim.startsWith("Query=") ) {
					query = trim.substring(6).trim().split("[ ]+")[0];
				} else if( query != null && trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") ) {
					String[] split = trim.split("[\t ]+");
					evalue = split[split.length-1];
				}
				
				line = br.readLine();
			}
			
			System.err.println( name + " genes total: " + set.size() );
			geneset.put( name, set );
		}
		
		Set<String>	allset = new HashSet<String>( allgenes.keySet() );
		for( String name : geneset.keySet() ) {
			Set<String>	gset = geneset.get( name );
			allset.retainAll( gset );
		}
		System.err.println( "Core genome size: " + allset.size() );
		
		Set<String>	nameset = null;
		
		for( String gname : allset ) {
			System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
		}
		
		boolean info = true;
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			for( String tname : nameset ) {
				allset.removeAll( geneset.get(tname) );
			}
			//System.err.println( "Genes found only in " + swapmap.get(aname) + "\t" + allset.size() );
			System.err.println( "Genes found only in " + aname + "\t" + allset.size() );
			if( info ) {
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
				}
			}
		}
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			allset.removeAll( geneset.get(aname) );
			for( String tname : nameset ) {
				allset.retainAll( geneset.get(tname) );
			}
			
			Set<String>	reset = new HashSet<String>();
			for( String name : nameset ) {
				//reset.add( swapmap.get(name) );
				reset.add( name );
			}
			System.err.println( "Genes only in all off " + reset + "\t" + allset.size() );
			if( info ) {
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.removeAll( geneset.get(names[i]) );
				allset.removeAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.retainAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				//reset.add( swapmap.get(names[i]) );
				//reset.add( swapmap.get(names[y]) );
				
				for( String name : nameset ) {
					//reset.add( swapmap.get(name) );
					reset.add( name );
				}
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				if( info ) {
					for( String gname : allset ) {
						System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
					}
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.retainAll( geneset.get(names[i]) );
				allset.retainAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.removeAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				//reset.add( swapmap.get(names[i]) );
				//reset.add( swapmap.get(names[y]) );
				reset.add( names[i] );
				reset.add( names[y] );
				
				/*for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}*/
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				if( info ) {
					for( String gname : allset ) {
						System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
					}
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				for( int k = y+1; k < names.length; k++ ) {
					allset = new HashSet<String>( allgenes.keySet() );
					nameset = new HashSet<String>( Arrays.asList(names) );
					//nameset.add( names[i] );
					//nameset.add( names[y] );
					nameset.remove( names[i] );
					nameset.remove( names[y] );
					nameset.remove( names[k] );
					
					allset.removeAll( geneset.get(names[i]) );
					allset.removeAll( geneset.get(names[y]) );
					allset.removeAll( geneset.get(names[k]) );
					for( String tname : nameset ) {
						allset.retainAll( geneset.get(tname) );
					}
					
					Set<String>	reset = new HashSet<String>();
					/*reset.add( swapmap.get(names[i]) );
					reset.add( swapmap.get(names[y]) );
					reset.add( swapmap.get(names[k]) );*/
					
					for( String name : nameset ) {
						//reset.add( swapmap.get(name) );
						reset.add( name );
					}
					
					
					System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
					if( info ) {
						for( String gname : allset ) {
							System.err.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
						}
					}
				}
			}
		}
		
		System.err.println( "Unique genes total: " + allgenes.size() );
		
		ps.close();
	}
	
	static Map<String,String>	aas = new HashMap<String,String>();
	public static void loci2aasequence( String[] stuff, File dir2 ) throws IOException {
		for( String st : stuff ) {
			File aa = new File( dir2, st );
			BufferedReader br = new BufferedReader( new FileReader(aa) );
			String line = br.readLine();
			String name = null;
			String ac = "";
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( ac.length() > 0 ) aas.put(name, ac);
					
					ac = "";
					name = line.substring(1).split(" ")[0];
					
					int v = name.indexOf("contig");
					if( v != -1 ) {
						int i1 = name.indexOf('_',v);
						int i2 = name.indexOf('_', i1+1);
						name = name.substring(0,i1) + name.substring(i2);
					}
				} else ac += line.trim();
				line = br.readLine();
			}
			if( ac.length() > 0 ) aas.put(name, ac);
			br.close();
		}
	}
	
	public static void printnohits( String[] stuff, File dir, File dir2 ) throws IOException {
		loci2aasequence(stuff, dir2);
		for( String st : stuff ) {
			System.err.println("Unknown genes in " + swapmap.get(st+".out"));
			
			File ba = new File( dir, "new2_"+st+".out" );
			BufferedReader br = new BufferedReader( new FileReader(ba) );
			String line = br.readLine();
			String name = null;
			//String ac = null;
			while( line != null ) {
				if( line.startsWith("Query= ") ) {
					name = line.substring(8).split(" ")[0];
				}
				
				if( line.contains("No hits") ) {
					System.err.println( name + "\t" + aas.get(swapmap.get(st+".out")+" "+name) );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void createConcatFsa( String[] names, File dir ) throws IOException {
		FileWriter	fw = new FileWriter( new File( dir, "all.fsa" ) );
		for( String name : names ) {
			File 			f = new File( dir, name+".fsa" );
			BufferedReader 	br = new BufferedReader( new FileReader( f ) );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) fw.write( ">"+name+"_"+line.substring(1)+"\n" );
				else fw.write( line+"\n" );
				
				line = br.readLine();
			}
			br.close();
		}
		fw.close();
	}
	
	public static void testbmatrix( String str ) throws IOException {
		Set<String>	testset = new HashSet<String>( Arrays.asList( new String[] {"1s","2s","3s","4s"} ) );
		Map<Set<String>,Set<Map<String,Set<String>>>> clustermap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		Set<Map<String,Set<String>>> smap = new HashSet<Map<String,Set<String>>>();
		smap.add( new HashMap<String,Set<String>>() );
		smap.add( new HashMap<String,Set<String>>() );
		clustermap.put( new HashSet<String>( Arrays.asList( new String[] {"1s"} ) ), smap );
		clustermap.put( new HashSet<String>( Arrays.asList( new String[] {"2s"} ) ), smap );
		clustermap.put( new HashSet<String>( Arrays.asList( new String[] {"3s"} ) ), smap );
		clustermap.put( new HashSet<String>( Arrays.asList( new String[] {"4s"} ) ), smap );
		
		BufferedImage img = bmatrix( testset, clustermap );
		
		ImageIO.write( img, "png", new File(str) );
	}
	
	public static BufferedImage bmatrix( Set<String> species, Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap ) {
		BufferedImage	bi = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
		Graphics2D		g2 = (Graphics2D)bi.getGraphics();
		int mstrw = 0;
		for( String spc : species ) {
			int tstrw = g2.getFontMetrics().stringWidth( spc );
			if( tstrw > mstrw ) mstrw = tstrw;
		}
		
		int sss = mstrw+72*species.size()+10;
		bi = new BufferedImage( sss, sss, BufferedImage.TYPE_INT_RGB );
		g2 = (Graphics2D)bi.getGraphics();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2.setColor( Color.white );
		g2.fillRect(0, 0, sss, sss);
		
		double minh = 100.0;
		double maxh = 0.0;
		
		double minr = 100.0;
		double maxr = 0.0;
		
		int where = 0;
		for( String spc1 : species ) {
			int strw = g2.getFontMetrics().stringWidth( spc1 );
			
			g2.setColor( Color.black );
			g2.drawString(spc1, mstrw-strw, mstrw+47+where*72);
			g2.rotate( Math.PI/2.0, mstrw+47+where*72, mstrw-strw );
			g2.drawString(spc1, mstrw+42+where*72, mstrw-strw );
			g2.rotate( -Math.PI/2.0, mstrw+47+where*72, mstrw-strw );			
			
			int wherex = 0;
			for( String spc2 : species ) {				
				int spc1tot = 0;
				int spc2tot = 0;
				int totot = 0;
				for( Set<String> set : clusterMap.keySet() ) {
					Set<Map<String,Set<String>>>	erm = clusterMap.get(set);
					if( set.contains( spc1 ) ) {
						spc1tot += erm.size();
						for( Map<String,Set<String>> sm : erm ) {
							Set<String> hset = sm.get(spc1);
							totot += hset.size();
						}
						
						if( set.contains( spc2 ) ) {
							spc2tot += erm.size();
						}
					}
				}
				
				double hh = (double)spc2tot/(double)spc1tot;
				if( hh > maxh ) maxh = hh;
				if( hh < minh ) minh = hh;
				
				double rr = (double)spc1tot/(double)totot;
				if( rr > maxh ) maxr = rr;
				if( rr < minh ) minr = rr;
				
				System.err.print( spc2tot+"/"+spc1tot+"\t" );
				System.err.print( spc1tot+"/"+totot+"\t" );
			}
			System.err.println();
			where++;
		}
		
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream	ps = new PrintStream( baos );
		
		where = 0;
		for( String spc1 : species ) {			
			int wherex = 0;
			for( String spc2 : species ) {				
				int spc1tot = 0;
				int spc2tot = 0;
				int totot = 0;
				for( Set<String> set : clusterMap.keySet() ) {
					Set<Map<String,Set<String>>>	erm = clusterMap.get(set);
					if( set.contains( spc1 ) ) {
						spc1tot += erm.size();
						for( Map<String,Set<String>> sm : erm ) {
							Set<String> hset = sm.get(spc1);
							totot += hset.size();
						}
						
						if( set.contains( spc2 ) ) {
							spc2tot += erm.size();
						}
					}
				}
				
				if( where == wherex ) {
					double dval = (double)spc1tot/(double)totot;
					int cval = (int)(200.0*(maxr - dval)/(maxr - minr));
					g2.setColor( new Color( 255, cval, cval ) );
					g2.fillRoundRect( mstrw+10+wherex*72, mstrw+10+where*72, 64, 64, 16, 16);
					
					g2.setColor( Color.white );
					String str = spc1tot+"/"+totot;
					int	nstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw/2, mstrw+47+where*72 );
				} else {
					double dval = (double)spc2tot/(double)spc1tot;
					int cval = (int)(200.0*(maxh - dval)/(maxh - minh));
					g2.setColor( new Color( cval, 255, cval ) );
					g2.fillRoundRect( mstrw+10+wherex*72, mstrw+10+where*72, 64, 64, 16, 16);
					
					g2.setColor( Color.white );
					String str = spc2tot+"";
					int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw2/2, mstrw+47+where*72-25 );
					//int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					str = spc1tot+"";
					int	nstrw1 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw1/2, mstrw+47+where*72 );
					
					double hlut = ((double)spc2tot/(double)spc1tot);
					ps.printf("%.2f", (float)hlut );
					str = baos.toString();
					baos.reset();
					int	pstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-pstrw/2, mstrw+47+where*72+25 );
				}
				
				wherex++;
			}
			System.err.println();
			where++;
		}
		
		return bi;
	}
	
	private static void joinSets( Set<String> all, List<Set<String>> total ) {
		System.err.println("starting");
		
		Set<String> cont = null;
		Set<Set<String>>	rem = new HashSet<Set<String>>();
		int i = 0;
		for( Set<String>	check : total ) {
			if( total.size() == 444 ) {
				if( check.contains("ttHB27join_gi|46197919|gb|AE017221.1|_978_821") ) {
					System.err.println( "uhuh " + i );
					if( all.contains("ttHB27join_gi|46197919|gb|AE017221.1|_978_821") ) {
						System.err.println("okok");
					}
				}
			}
			
			for( String aval : all ) {
				if( check.contains(aval) ) {
					if( cont == null ) {
						cont = check;
						System.err.println( "conidx " + i );
						check.addAll( all );
						break;
					} else {
						cont.addAll( check );
						rem.add( check );
						break;
					}
				}
			}
			
			i++;
		}
		
		//for( Set<String> s : rem ) {
		//	if( s.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821" ) ) System.err.println("okerm" + rem.size());
		//}
		
		//int sbef = total.size();
		//if( rem.size() > 0 ) System.err.println("tsize bef "+sbef);
		//total.removeAll( rem );
		
		for( Set<String> erm : rem ) {
			//if( erm.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821" ) ) System.err.println( "asdf "+erm.toString()+"  "+erm );

			int ind = -1;
			int count = 0;
			for( Set<String> ok : total ) {
				if( ok.size() == erm.size() && ok.containsAll(erm) ) {
					ind = count;
					break;
				}
				count++;
			}
			
			if( ind != -1 ) {
				total.remove( ind );
			}
		}
		
		//for( Set<String> erm : total ) {
		//	if( erm.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821" ) ) System.err.println( "asdf2 "+erm.toString()+"  "+erm );
		//}
		
		//int saft = total.size();
		//if( rem.size() > 0 ) System.err.println("tsize aft "+saft);
		
		rem.clear();
		if( cont == null ) total.add( all );
		
		Set<String>	erm = new HashSet<String>();
		System.err.println( "erm " + total.size() );
		for( Set<String> ss : total ) {
			for( String s : ss ) {
				if( erm.contains( s ) ) {
					System.err.println( "buja " + s );
					break;
				}
			}
			erm.addAll( ss );
		}
	}
	
	private static Collection<Set<String>> func4_header( File dir, String[] stuff ) throws IOException {
		List<Set<String>>	total = new ArrayList<Set<String>>();
		FileWriter fw = new FileWriter("/home/sigmar/blastcluster.txt");
		for( String name : stuff ) {
			File f = new File( dir, name );
			BufferedReader	br = new BufferedReader( new FileReader( f ) );
			
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith("Sequences prod") ) {
					line = br.readLine();
					Set<String>	all = new HashSet<String>();
					while( line != null && !line.startsWith(">") ) {
						String trim = line.trim();
						if( trim.startsWith("scoto") || trim.startsWith("anta") || trim.startsWith("tt") || trim.startsWith("egg") || trim.startsWith("island") ) {
							String val = trim.substring( 0, trim.indexOf('#')-1 );
							int v = val.indexOf("contig");
							if( v != -1 ) {
								int i1 = val.indexOf('_',v);
								int i2 = val.indexOf('_', i1+1);
								val = val.substring(0,i1) + val.substring(i2);
							}
							all.add( val );
						}
						line = br.readLine();
					}
					
					fw.write( all.toString()+"\n" );
					
					joinSets( all, total );
					
					if( line == null ) break;
				}
				
				line = br.readLine();
			}
		}
		fw.close();
		
		return total;
	}
	
	private static List<Set<String>> readBlastList( String filename ) throws IOException {
		List<Set<String>>	total = new ArrayList<Set<String>>();
		
		FileReader 		fr = new FileReader( filename );
		BufferedReader	br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			String substr = line.substring(1, line.length()-1);
			String[] split = substr.split("[, ]+");
			Set<String>	all = new HashSet<String>( Arrays.asList( split ) );
			
			joinSets( all, total );
			
			line = br.readLine();
		}
		br.close();
		
		return total;
	}
	
	private static Map<Set<String>,Set<Map<String,Set<String>>>> loadCluster( String path ) throws IOException {
		Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		FileReader		fr = new FileReader( path );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		Set<String>	olderm = null;
		Map<String,Set<String>>	ok = null;
		Set<Map<String,Set<String>>>	setmap = null;
		String curspec = null;
		
		Set<String>	trall = new HashSet<String>();
		List<String>	tralli = new ArrayList<String>();
		
		while( line != null ) {
			if( line.startsWith("[") ) {
				Set<String>	erm = new HashSet<String>();
				String substr = line.substring(1, line.length()-1);
				String[] split = substr.split("[, ]+");
				erm.addAll( Arrays.asList(split) );
				
				if( olderm != null ) {
					clusterMap.put( olderm, setmap );
				}
				olderm = erm;
				setmap = new HashSet< Map<String,Set<String>> >();
			} else if( !line.startsWith("\t") ) {
				if( olderm != null ) {
					clusterMap.put( olderm, setmap );
				}
				
				ok = new HashMap<String,Set<String>>();
				setmap.add( ok );
			} else {
				String trimline = line.trim();
				if( trimline.startsWith("[") ) {
					String[] subsplit = trimline.substring(1,trimline.length()-1).split("[, ]+");
					Set<String> trset = new HashSet<String>( Arrays.asList(subsplit) );
					
					for( String trstr : trset ) {
						if( trstr.contains("ttaqua") ) {
							if( trall.contains( trstr ) ) {
								System.err.println( "uhm1 " + trstr );
							} else {
								trall.add( trstr );
							}
							tralli.add( trstr );
						}
					}
					
					ok.put( curspec, trset );
				} else {
					curspec = trimline;
				}
			}
			line = br.readLine();
		}
		clusterMap.put( olderm, setmap );
		
		br.close();
		
		System.err.println( "hohoho " + trall.size() + "  " + tralli.size() );
		
		return clusterMap;
	}
	
	private static Map<Set<String>,Set<Map<String,Set<String>>>> initCluster( List<Set<String>>	total, Set<String> species ) {
		Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		for( Set<String>	t : total ) {
			Set<String>	teg = new HashSet<String>();
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf('_') );
				/*if( joinmap.containsKey( str ) ) {
					str = joinmap.get(str);
				}*/
				teg.add( str );
				
				species.add(str);
			}
			
			Set<Map<String,Set<String>>>	setmap;
			if( clusterMap.containsKey( teg ) ) {
				setmap = clusterMap.get( teg );
			} else {
				setmap = new HashSet<Map<String,Set<String>>>();
				clusterMap.put( teg, setmap );
			}
			
			Map<String,Set<String>>	submap = new HashMap<String,Set<String>>();
			setmap.add( submap );
			
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf('_') );
				/*if( joinmap.containsKey( str ) ) {
					str = joinmap.get(str);
				}*/
				
				Set<String>	set;
				if( submap.containsKey( str ) ) {
					set = submap.get(str);
				} else {
					set = new HashSet<String>();
					submap.put( str, set );
				}
				set.add( e );
			}
		}
		
		return clusterMap;
	}
	
	private static void writeSimplifiedCluster( String filename, Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap ) throws IOException {
		FileWriter	fos = new FileWriter( filename );
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	mapset = clusterMap.get( set );
			fos.write( set.toString()+"\n" );
			int i = 0;
			for( Map<String,Set<String>> erm : mapset ) {
				fos.write((i++)+"\n");
				
				for( String erm2 : erm.keySet() ) {
					Set<String>	erm3 = erm.get(erm2);
					fos.write("\t"+erm2+"\n");
					fos.write("\t\t"+erm3.toString()+"\n");
				}
			}
		}
		fos.close();
	}
	
	public static Set<String> speciesFromCluster( Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap ) {
		Set<String>	species = new TreeSet<String>();
		
		for( Set<String> clustset : clusterMap.keySet() ) {
			species.addAll( clustset );
		}
		
		return species;
	}
	
	public static void func4( File dir, String[] stuff ) throws IOException {
		//HashMap<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		/*Map<String,String>	joinmap = new HashMap<String,String>();
		joinmap.put("ttpHB27", "ttHB27");
		joinmap.put("ttp1HB8", "ttHB8");
		joinmap.put("ttp2HB8", "ttHB8");*/
		
		//Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = loadCluster("/home/sigmar/burb.txt");
		
		Set<String>	species = new TreeSet<String>();
		//Set<Set<String>> total = func4_header( dir, stuff );
		List<Set<String>> total = readBlastList( "/home/sigmar/blastcluster.txt" );
		
		FileWriter	fw = new FileWriter("/home/sigmar/joincluster.txt");
		for( Set<String> sset : total ) {
			fw.write( sset.toString()+"\n" );
		}
		fw.close();
		
		Set<String>	trall = new HashSet<String>();
		for( Set<String> sset : total ) {
			for( String trstr : sset ) {
				if( trstr.contains("ttaqua") ) {
					if( trall.contains( trstr ) ) {
						System.err.println( "uhm1 " + trstr );
					} else {
						trall.add( trstr );
					}
					//tralli.add( trstr );
				}
			}
		}
		
		Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
		//Set<String>	species = speciesFromCluster( clusterMap );
		
		writeSimplifiedCluster( "/home/sigmar/burb2.txt", clusterMap );
		
		BufferedImage	img = bmatrix( species, clusterMap );
		ImageIO.write( img, "png", new File("/home/sigmar/mynd.png") );
		
		PrintStream ps = new PrintStream( new FileOutputStream("/home/sigmar/out2.out") );
		System.setErr( ps );
		
		//System.err.println( "Total gene sets: " + total.size() );
		//System.err.println();
		
		List<StrSort>	sortmap = new ArrayList<StrSort>();
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
			
			int i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					i += genes.size();
				}
			}
			sortmap.add( new StrSort( setmap.size(), set.size()+"\t"+setmap.size()+"\tIncluded in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes\ttotal of " + i + " loci" ) );
			//System.err.println( "Included in : " + set.size() + " scotos " + set + " containing: " + setmap.size() + " set of genes\ttotal of " + i + " loci" );
		}
		
		Collections.sort( sortmap );
		for( StrSort ss : sortmap ) {
			System.err.println( ss.s );
		}
		System.err.println();
		System.err.println();
		
		
		//next( clusterMap );
		
		
		ps.close();
		
		/*System.err.println( "# clusters: " + total.size() );
		int max = 0;
		for( Set<String> sc : total ) {
			if( sc.size() > max ) max = sc.size();
			System.err.println( "\tcluster size: " + sc.size() );
		}
		System.err.println( "maxsize: " + max );
		
		int[]	ia = new int[max];
		for( Set<String> sc : total ) {
			ia[sc.size()-1]++;
		}
		
		for( int i : ia ) {
			System.err.println( "hist: " + i );
		}*/
	}
	
	public static void next( HashMap<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) {
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
			
			int i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					i += genes.size();
				}
			}
			
			List<Map<String,Set<String>>>	maplist = new ArrayList<Map<String,Set<String>>>();
			for( Map<String,Set<String>>	map : setmap ) {
				maplist.add( map );
			}
			
			Set<Integer>	kfound = new HashSet<Integer>();
			String ermstr = "Included in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes";
			for( i = 0; i < maplist.size(); i++ ) {
				//if( !kfound.contains(i) ) {
					Set<String>	ss = new HashSet<String>();
					Map<String,Set<String>>	map = maplist.get(i);
					for( String s: map.keySet() ) {
						for( String s2 : map.get(s) ) {
							int ii = s2.indexOf('_', 0);
							if( ii == -1 ) {
								System.out.println( s2 );
							} else {
								ss.add( s2.substring(0, ii) );
							}
						}
					}
					
					if( ss.size() > 1 ) {						
						Set<Integer>	innerkfound = new HashSet<Integer>();
						for( int k = i+1; k < maplist.size(); k++ ) {
							if( !kfound.contains(k) ) {
								Set<String>	ss2 = new HashSet<String>();
								map = maplist.get(k);
								for( String s: map.keySet() ) {
									for( String s2 : map.get(s) ) {
										ss2.add( s2.substring(0, s2.indexOf('_', 0)) );
									}
								}
								if( ss.containsAll( ss2 ) && ss2.containsAll( ss ) ) {
									kfound.add( k );
									innerkfound.add( k );
								}
							}
						}
						
						if( innerkfound.size() > 0 ) {
							innerkfound.add( i );
							if( ermstr != null ) {
								System.err.println( ermstr );
								ermstr = null;
							}
							System.err.println( "Preserved clusters " + innerkfound );
							for( int k : innerkfound ) {
								Map<String,Set<String>>	sm = maplist.get(k);
								Set<String>	geneset = new HashSet<String>();
								for( String s : sm.keySet() ) {
									Set<String>	sout = sm.get(s);
									for( String loci : sout ) {
										String gene = lociMap.get(loci);
										if( gene == null ) {
											gene = aas.get( loci );
										}
										if( gene == null ) {
											System.out.println( "error" + loci );
										}
										geneset.add(gene.replace('\t', ' '));
									}
								}
								System.err.println( "\t"+geneset );
							}
						}
					}
			}
		}
		
		
		
		
		
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
			
			int i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					i += genes.size();
				}
			}
			System.err.println( "Included in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes\ttotal of " + i + " loci" );
			
			i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				System.err.println("Starting set " + i);
				
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					
					System.err.println( "In " + s + " containing " + genes.size() );
					for( String gene : genes ) {
						System.err.println( gene + "\t" + lociMap.get(gene) );
					}
				}
				System.err.println();
				
				i++;
			}
		}
	}
	
	static Map<String,String>	lociMap = new HashMap<String,String>();
	public static void loci2gene( String[] stuff, File dir ) throws IOException {
		//Map<String,String>	aas = new HashMap<String,String>();
		for( String st : stuff ) {			
			File ba = new File( dir, "new2_"+st+".out" );
			BufferedReader br = new BufferedReader( new FileReader(ba) );
			String line = br.readLine();
			String name = null;
			while( line != null ) {
				if( line.startsWith("Query= ") ) {
					name = line.substring(8).split(" ")[0];
					int i1 = name.indexOf('_');
					int i2 = name.indexOf('_', i1+1);
					name = name.substring(0,i1) + name.substring(i2);
					System.err.println(name);
				}
				
				if( line.contains("No hits") ) {
					String prename = swapmap.get(st+".out")+"_"+name;
					lociMap.put( prename, "No match\t"+aas.get(prename) );
					//System.err.println( prename + "\tNo match" );
				}
				
				if( line.startsWith(">ref") || line.startsWith(">sp") || line.startsWith(">pdb") || line.startsWith(">dbj") || line.startsWith(">gb") || line.startsWith(">emb") ) {
					String prename = swapmap.get(st+".out")+"_"+name;
					String[] split = line.split("\\|");
					lociMap.put( prename, split[1] + (split.length > 2 ? "\t" + split[2] : "") );
					//System.err.println( prename + "\t" + split[1] );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void aahist( File f1, File f2, int val ) throws IOException {
		Map<String,Long>	aa1map = new HashMap<String,Long>();
		Map<String,Long>	aa2map = new HashMap<String,Long>();
		
		long t1 = 0;
		FileReader fr = new FileReader( f1 );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length()-val+1; i++ ) {
					String c = line.substring(i, i+val);
					if( aa1map.containsKey(c) ) {
						aa1map.put( c, aa1map.get(c)+1L );
					} else aa1map.put( c, 1L );
					
					t1++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		//Runtime.getRuntime().availableProcessors()
		
		long t2 = 0;
		fr = new FileReader( f2 );
		br = new BufferedReader( fr );
		line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length()-val+1; i++ ) {
					String c = line.substring(i, i+val);
					if( aa2map.containsKey(c) ) {
						aa2map.put( c, aa2map.get(c)+1L );
					} else aa2map.put( c, 1L );
					
					t2++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		System.err.println( t1 + "\t" + t2 );
		int na1 = 0;
		int na2 = 0;
		int nab = 0;
		int u = 0;
		double dt = 0.0;
		Set<String>	notfound = new HashSet<String>();
		Set<String>	notfound2 = new HashSet<String>();
		for( int i = 0; i < Math.pow(uff.size(), val); i++ ) {
			String e = "";
			for( int k = 0; k < val; k++ ) {
				e += uff.get( (i/(int)Math.pow(uff.size(), val-(k+1)))%uff.size() ).c;
			}
			
			if( aa1map.containsKey( e ) || aa2map.containsKey( e ) ) {
				boolean b1 = aa1map.containsKey(e);
				boolean b2 = aa2map.containsKey(e);
				
				if( !b1 ) {
					if( val == 3 ) notfound.add(e);
					na1++;
				}
				if( !b2 ) {
					if( val == 3 ) notfound2.add(e);
					na2++;
				}
				
				double dval = (b1 ? aa1map.get(e)/(double)t1 : 0.0) - (b2 ? aa2map.get(e)/(double)t2 : 0.0);
				dval *= dval;
				dt += dval;
				u++;
				
				System.err.println( e + "\t" + (aa1map.get(e)) + "\t" + (aa2map.containsKey(e) ? (aa2map.get(e)) : "-") );
			} else {
				if( val == 3 ) {
					notfound.add(e);
					notfound2.add(e);
				}
				nab++;
			}
		}
		System.err.println( "MSE: " + (dt/u) + " for " + val );
		System.err.println( "Not found in 1: " + na1 + ", Not found in 2: " + na2 + ", found in neither: " + nab );
		
		for( String ns : notfound ) {
			System.err.println(ns);
		}
		System.err.println();
		for( String ns : notfound2 ) {
			System.err.println(ns);
		}
	}
	
	public static void aahist( File f1, File f2 ) throws IOException {
		Map<Character,Long>	aa1map = new HashMap<Character,Long>();
		Map<Character,Long>	aa2map = new HashMap<Character,Long>();
		
		long t1 = 0;
		FileReader fr = new FileReader( f1 );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length(); i++ ) {
					char c = line.charAt(i);
					if( aa1map.containsKey(c) ) {
						aa1map.put( c, aa1map.get(c)+1L );
					} else aa1map.put( c, 1L );
					
					t1++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		long t2 = 0;
		fr = new FileReader( f2 );
		br = new BufferedReader( fr );
		line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length(); i++ ) {
					char c = line.charAt(i);
					if( aa2map.containsKey(c) ) {
						aa2map.put( c, aa2map.get(c)+1L );
					} else aa2map.put( c, 1L );
					
					t2++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		
		for( Erm e : isoel ) {
			char c = e.c;
			if( aa1map.containsKey( c ) ) {
				System.err.println( e.d + "\t" + c + "\t" + (aa1map.get(c)/(double)t1) + "\t" + (aa2map.containsKey(c) ? (aa2map.get(c)/(double)t2) : "-") );
			}
		}	
	}
	
	public static void newstuff() throws IOException {
		Map<String,Set<String>>	famap = new HashMap<String,Set<String>>();
		Map<String,String>	idmap = new HashMap<String,String>();
		Map<String,String>	nmmap = new HashMap<String,String>();
		File f = new File("/home/sigmar/groupmap.txt");
		BufferedReader br = new BufferedReader( new FileReader(f) );
		String line = br.readLine();
		while( line != null ) {
			String[] split = line.split("\t");
			if( split.length > 1 ) {
				idmap.put( split[1], split[0] );
				nmmap.put( split[0], split[1] );
				
				String[] subsplit = split[0].split("_");
				Set<String>	fam = null;
				if( famap.containsKey(subsplit[0]) ) {
					fam = famap.get(subsplit[0]);
				} else {
					fam = new HashSet<String>();
					famap.put(subsplit[0], fam);
				}
				fam.add( split[0] );
			}
			
			line = br.readLine();
		}
		br.close();
		
		Set<String>	remap = new HashSet<String>();
		Set<String>	almap = new HashSet<String>();
		for( String erm : famap.keySet() ) {
			if( erm.startsWith("Trep") || erm.startsWith("Borr") || erm.startsWith("Spir") ) {
				remap.add( erm );
				almap.addAll( famap.get(erm) );
			}
		}
		for( String key : remap ) famap.remove(key);
		famap.put("TrepSpirBorr", almap);
		
		for( String fam : famap.keySet() ) {
			Set<String>	subfam = famap.get(fam);
			System.err.println( "fam: " + fam );
			for( String sf : subfam ) {
				System.err.println( "\tsf: " + nmmap.get(sf) );
			}
		}
		
		f = new File("/home/sigmar/group_21.dat");
		Map<Set<String>,Set<String>>	common = new HashMap<Set<String>,Set<String>>();
		/*File[] files = f.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if( name.startsWith("group") && name.endsWith(".dat") ) {
					return true;
				}
				return false;
			}
		});*/
		
		Set<String> erm2 = new HashSet<String>();
		erm2.addAll( famap.get("Brachyspira") );
		erm2.addAll( famap.get("Leptospira") );
		
		Set<String>	all = new HashSet<String>();
		br = new BufferedReader( new FileReader( f ) );
		line = br.readLine();
		while( line != null ) {
			String[] split = line.split("[\t]+");
			if( split.length > 2 ) {
				Set<String>	erm = new HashSet<String>();
				for( int i = 2; i < split.length; i++ ) {
					erm.add( idmap.get(split[i].substring(0, split[i].indexOf('.') )) );
					//erm.add( split[i].substring(0, split[i].indexOf('.') ) );
				}
				
				Set<String>	incommon = null;
				if( common.containsKey(erm) ) {
					incommon = common.get(erm);
				} else {
					incommon = new HashSet<String>();
					common.put( erm, incommon );
				}
				incommon.add( line );
				
				if( erm.size() == 13 ) {
					//if( erm.containsAll(famap.get("TrepSpirBorr")) && erm.containsAll(famap.get("Leptospira")) ) {
					//if( erm.containsAll(famap.get("TrepSpirBorr")) && erm.containsAll(famap.get("Brachyspira")) ) {
					//if( erm.containsAll(famap.get("Leptospira")) ) {
					//if( erm.containsAll(famap.get("Brachyspira")) ) {
					boolean includesAllLeptos = erm.containsAll(famap.get("Leptospira"));
					boolean includesAllBrachys = erm.containsAll(famap.get("Brachyspira"));
					Set<String> ho = new HashSet<String>( erm );
					ho.removeAll(famap.get("Brachyspira"));
					ho.removeAll(famap.get("TrepSpirBorr"));
					boolean includesSomeLeptos = ho.size() > 0 && ho.size() != famap.get("Leptospira").size();
					ho = new HashSet<String>( erm );
					ho.removeAll(famap.get("Leptospira"));
					ho.removeAll(famap.get("TrepSpirBorr"));
					boolean includesSomeBrachys = ho.size() > 0 && ho.size() != famap.get("Brachyspira").size();
					
					if( erm.containsAll(famap.get("TrepSpirBorr")) ) { // && ( includesSomeBrachys || includesSomeLeptos ) ) {
						//int start = line.indexOf("5743b451ec3e92efc596500d604750ed");
						//int start = line.indexOf("be1843abfce51adcaa86b07a3c6bedbb");
						//int start = line.indexOf("7394569560a961ac7ffe674befec5056");
						//Set<String> ho = new TreeSet<String>( erm );
						//ho.removeAll(famap.get("TrepSpirBorr"));
						//System.err.println("erm " + ho);
						int start = line.indexOf("d719570adc9e2969b0374564745432cd");
						
						if( start > 0 ) {
							int end = line.indexOf('\t', start);
							//if( end == -1 ) end = line.indexOf('\n', start);
							if( end == -1 ) end = line.length();
							all.add( line.substring(start, end) );
						} else {
							System.err.println();
						}
					}
				}
				
				/*if( erm.size() >= 22 ) {
					int start = line.indexOf("696cf959d443a23e53786f1eae8eb6c9");
					
					if( start > 0 ) {
						int end = line.indexOf('\t', start);
						//if( end == -1 ) end = line.indexOf('\n', start);
						if( end == -1 ) end = line.length();
						all.add( line.substring(start, end) );
					} else { 
						System.err.println();
					}
				}*/
			}
			
			line = br.readLine();
		}
		br.close();
		
		PrintStream ps = new PrintStream("/home/sigmar/iron5.giant");
		//System.setErr( ps );
		
		int count = 0;
		f = new File("/home/sigmar/21.fsa");
		br = new BufferedReader( new FileReader(f) );
		line = br.readLine();
		while( line != null ) {
			if( all.contains(line.substring(1)) ) {
				count++;
				System.err.println( line );
				line = br.readLine();
				while( line != null && !line.startsWith(">") ) {
					System.err.println( line );
					line = br.readLine();
				}
			} else line = br.readLine();
		}
		br.close();
		
		System.err.println( "hey: " + count );
		
		int total = 0;
		System.err.println("total groups "+common.size());
		for( Set<String> keycommon : common.keySet() ) {
			Set<String>	incommon = common.get(keycommon);
			System.err.println( incommon.size() + "  " + keycommon.size() + "  " + keycommon );
			total += incommon.size();
		}
		System.err.println(total);
		System.err.println("");
		
		total = 0;
		System.err.println("boundary crossing groups");
		for( Set<String> keycommon : common.keySet() ) {
			Set<String>	incommon = common.get(keycommon);
			
			boolean s = true;
			for( String fam : famap.keySet() ) {
				Set<String>	famset = famap.get( fam );
				if( famset.containsAll( keycommon ) ) {
					s = false;
					break;
				}
			}
			if( s ) {
				System.err.println( incommon.size() + "  " + keycommon.size() + "  " + keycommon );
				total++;
			}
		}
		System.err.println( "for the total of " + total );
		
		/*System.err.println( all.size() );
		for( String astr : all ) {
			System.err.println( astr );
		}*/
		
		ps.close();
	}
	
	static class Pepbindaff implements Comparable<Pepbindaff> {
		String	pep;
		double	aff;
		
		public Pepbindaff( String pep, String aff ) {
			this.pep = pep;
			this.aff = Double.parseDouble( aff );
		}
		
		public Pepbindaff( String pep, double aff ) {
			this.pep = pep;
			this.aff = aff;
		}
		
		@Override
		public int compareTo(Pepbindaff o) {
			return aff > o.aff ? 1 : (aff < o.aff ? -1 : 0);
		}		
	}
	
	public static void pearsons( Map<Character,Double> what, List<Pepbindaff> peppi) {
		double sums[] = new double[peppi.get(0).pep.length()];
		Arrays.fill( sums, 0.0 );
		for( Pepbindaff paff : peppi ) {
			for( int i = 0; i < paff.pep.length(); i++ ) {
				char c = paff.pep.charAt(i);
				sums[i] += what.get(c);
			}
		}
		for( int i = 0; i < peppi.get(0).pep.length(); i++ ) {
			sums[i] /= peppi.size();
		}
		
		for( int i = 0; i < peppi.get(0).pep.length(); i++ ) {
			for( int j = i; j < peppi.get(0).pep.length(); j++ ) {
				double t = 0.0;
				double nx = 0.0;
				double ny = 0.0;
				for( Pepbindaff paff : peppi ) {
					char c = paff.pep.charAt(j);
					char ct = paff.pep.charAt(i);
					double h = what.get(c) - sums[j];
					double ht = what.get(ct) - sums[i];
					t += h*ht;
					nx += h*h;
					ny += ht*ht;
				}
				double xy = nx * ny;
				double val = (xy == 0 ? 0.0 : (t/Math.sqrt(xy)));
				if( Math.abs(val) > 0.1 || i == j ) System.err.println( "Pearson (" + i + " " + j + "): " + val );
			}
		}
	}
	
	public static void kendaltau( List<Pepbindaff> peppi ) {
		int size = peppi.get(0).pep.length();
		List<Pepbindaff>	erm = new ArrayList<Pepbindaff>();
		for( int x = 0; x < size-1; x++ ) {
			for( int y = x+1; y < size; y++ ) {
				int con = 0;
				int dis = 0;
				for( int i = 0; i < peppi.size(); i++ ) {
					for( int j = i+1; j < peppi.size(); j++ ) {
						char xi = peppi.get(i).pep.charAt(x);
						char xj = peppi.get(j).pep.charAt(x);
						char yi = peppi.get(i).pep.charAt(y);
						char yj = peppi.get(j).pep.charAt(y);
						
						if( (xi > xj && yi > yj) || (xi < xj && yi < yj) ) con++;
						else if( xi > xj && yi < yj || xi < xj && yi > yj ) dis++;
					}
				}
				double kt = (double)(2*(con - dis))/(double)(peppi.size()*(peppi.size()-1));
				erm.add( new Pepbindaff("kt "+x+" "+y+": ", kt) );
				//System.err.println( "kt "+i+" "+j+": "+kt );
			}
		}
		Collections.sort( erm );
		for( Pepbindaff p : erm ) {
			System.err.println(p.pep + " " + p.aff);
		}
	}
	
	public static void algoinbio() throws IOException {
		FileReader fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/A0101/A0101.dat");
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		
		Map<String,Double>	pepaff = new TreeMap<String,Double>();
		List<Pepbindaff>	peppi = new ArrayList<Pepbindaff>();
		while( line != null ) {
			String[] split = line.trim().split("[\t ]+");
			peppi.add( new Pepbindaff(split[0], split[1]) );
			
			line = br.readLine();
		}
		br.close();
		
		Collections.sort( peppi );
		
		kendaltau( peppi );
		//pearsons( hydropathyindex, peppi );
		
		
		/*for( Pepbindaff paff : peppi ) {
			System.err.print( paff.pep );
			for( int i = 0; i < paff.pep.length(); i++ ) {
				char c1 = paff.pep.charAt(i);
				//char c2 = paff.pep.charAt(i+1);
				//System.err.print( "\t"+Math.min(hydropathyindex.get(c1),hydropathyindex.get(c2) ) );
				//System.err.print( "\t"+sidechaincharge.get(c) );
				System.err.print( "\t"+isoelectricpoint.get(c1) );
				//System.err.print( "\t"+Math.min(isoelectricpoint.get(c1),isoelectricpoint.get(c2)) );
				//System.err.print( "\t"+sidechainpolarity.get(c) );
			}
			System.err.println( "\t"+paff.aff );
		}
		
		/*double[]	hyp = new double[9];
		double[]	chr = new double[9];
		double[]	iso = new double[9];
		double[]	pol = new double[9];
		Arrays.fill(hyp, 0.0);
		Arrays.fill(chr, 0.0);
		Arrays.fill(iso, 0.0);
		Arrays.fill(pol, 0.0);
		int count = 0;
		while( line != null ) {
			String[] split = line.split("[\t ]+");
			String pep = split[0];
			double val = Double.parseDouble(split[1]);
			for( int i = 0; i < hyp.length; i++ ) {
				char c = pep.charAt(i);
				
				hyp[i] += val*hydropathyindex.get(c);
				chr[i] += val*sidechaincharge.get(c);
				iso[i] += val*isoelectricpoint.get(c);
				pol[i] += val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0);
			}
			count++;
			line = br.readLine();
		}
		br.close();
		
		/* Lowpoint */
		
		/*fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/f000");
		br = new BufferedReader( fr );
		line = br.readLine();
		
		double[]	hype = new double[9];
		double[]	chre = new double[9];
		double[]	isoe = new double[9];
		double[]	pole = new double[9];
		Arrays.fill(hype, 0.0);
		Arrays.fill(chre, 0.0);
		Arrays.fill(isoe, 0.0);
		Arrays.fill(pole, 0.0);
		double hypmax = Double.MIN_VALUE;
		double chrmax = Double.MIN_VALUE;
		double isomax = Double.MIN_VALUE;
		double polmax = Double.MIN_VALUE;
		while( line != null ) {
			double	hypt = 0.0;
			double	chrt = 0.0;
			double	isot = 0.0;
			double	polt = 0.0;
			
			String[] split = line.split("[\t ]+");
			String pep = split[0];
			double val = Double.parseDouble(split[1]);
			for( int i = 0; i < hyp.length; i++ ) {
				char c = pep.charAt(i);
				
				double d = val*hydropathyindex.get(c) - hyp[i]/count;
				hype[i] += d*d;
				d = val*sidechaincharge.get(c) - chr[i]/count;
				chre[i] += d*d;
				d = val*isoelectricpoint.get(c) - iso[i]/count;
				isoe[i] += d*d;
				d = val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i]/count;
				pole[i] += d*d;
				
				d = count*hydropathyindex.get(c) - hyp[i];
				hypt += d*d;
				d = count*sidechaincharge.get(c) - chr[i];
				chrt += d*d;
				d = count*isoelectricpoint.get(c) - iso[i];
				isot += d*d;
				d = count*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i];
				polt += d*d;
			}
			
			hypt /= hyp.length;
			chrt /= hyp.length;
			isot /= hyp.length;
			polt /= hyp.length;
			
			//hypsum += hypt;
			if( hypt > hypmax ) hypmax = hypt;
			if( chrt > chrmax ) chrmax = chrt;
			if( isot > isomax ) isomax = isot;
			if( polt > polmax ) polmax = polt;
			
			line = br.readLine();
		}
		br.close();
		
		for( int i = 0; i < hyp.length; i++ ) {
			hype[i] /= Math.abs(hyp[i]);
			chre[i] /= Math.abs(chr[i]);
			isoe[i] /= Math.abs(iso[i]);
			pole[i] /= Math.abs(pol[i]);
			
			System.err.println( "pos " + i + " " + hype[i] + " " + chre[i] + " " + isoe[i] + " " + pole[i] );
		}
		
		/*********
		
		fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week11/c000");
		br = new BufferedReader( fr );
		line = br.readLine();
		
		List<Double>	hyplist = new ArrayList<Double>();
		List<Double>	hyptlist = new ArrayList<Double>();
		double hyptsum = 0.0;
		double hypsum = 0.0;
		while( line != null ) {
			double	hypt = 0.0;
			double	chrt = 0.0;
			double	isot = 0.0;
			double	polt = 0.0;
			
			String[] split = line.split("[\t ]+");
			String pep = split[0];
			double val = Double.parseDouble(split[1]);
			for( int i = 0; i < hyp.length; i++ ) {
				char c = pep.charAt(i);
				
				double d = count*hydropathyindex.get(c) - hyp[i];
				hypt += d*d*(1.0-hype[i]);
				d = count*sidechaincharge.get(c) - chr[i];
				chrt += d*d*(1.0-chre[i]*100);
				d = count*isoelectricpoint.get(c) - iso[i];
				isot += d*d*(1.0-isoe[i]);
				d = count*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i];
				polt += d*d*(1.0-pole[i]);
			}
			
			hypt /= hyp.length;
			chrt /= hyp.length;
			isot /= hyp.length;
			polt /= hyp.length;
			
			double calc = (hypmax - hypt)/hypmax;
			//double calc = (chrmax - chrt)/chrmax;
			hypsum += val;
			hyptsum += calc;
			hyplist.add(val);
			hyptlist.add( calc );
			
			System.err.println( calc + "  " + val );
			
			line = br.readLine();
		}
		br.close();
		
		double hypmed = hypsum/hyplist.size();
		double hyptmed = hyptsum/hyptlist.size();
		double t = 0.0;
		double nx = 0.0;
		double ny = 0.0;
		for( int i = 0; i < hyplist.size(); i++ ) {
			double h = hyplist.get(i) - hypmed;
			double ht = hyptlist.get(i) - hyptmed;
			t += h*ht;
			nx += h*h;
			ny += ht*ht;
		}
		
		double xy = nx * ny;
		System.err.println( "Pearson: " + (xy == 0 ? 0.0 : (t/Math.sqrt(xy))) );
		
		/*System.err.println( "hyp" );
		for( double d : hyp ) {
			System.err.println( d );
		}
		System.err.println( "chr" );
		for( double d : chr ) {
			System.err.println( d );
		}
		System.err.println( "iso" );
		for( double d : iso ) {
			System.err.println( d );
		}
		System.err.println( "pol" );
		for( double d : pol ) {
			System.err.println( d );
		}*/
	}
	
	public static void blastparse( String fn ) throws IOException {
		Set<String>		set = new HashSet<String>();
		FileReader		fr = new FileReader( fn );
		BufferedReader 	br = new BufferedReader( fr );
		String 	query = null;
		String	evalue = null;
		String line = br.readLine();
		int count = 0;
		while( line != null ) {
			String trim = line.trim();
			if( query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb")) ) {
				//String[] split = trim.split("\\|");
				set.add( trim + "\t" + evalue );
				
				/*if( !allgenes.containsKey( split[1] ) || allgenes.get( split[1] ) == null ) {
					allgenes.put( split[1], split.length > 1 ? split[2].trim() : null );
				}
				
				/*Set<String>	locset = null;
				if( geneloc.containsKey( split[1] ) ) {
					locset = geneloc.get(split[1]);
				} else {
					locset = new HashSet<String>();
					geneloc.put(split[1], locset);
				}
				locset.add( query + " " + evalue );*/
				
				query = null;
				evalue = null;
			} else if( trim.startsWith("Query=") ) {
				query = trim.substring(6).trim().split("[ ]+")[0];
			} else if( evalue == null && query != null && (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|")) ) {
				String[] split = trim.split("[\t ]+");
				evalue = split[split.length-1];
			}
			count++;
			line = br.readLine();
		}
		
		System.err.println(count);
		
		Map<String,Set<String>>	mapset = new HashMap<String,Set<String>>();
		for( String gene : set ) {
			if( gene.contains("ribosomal") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("ribosomal proteins") ) {
					subset = mapset.get("ribosomal proteins");
				} else {
					subset = new TreeSet<String>();
					mapset.put("ribosomal proteins", subset);
				}
				subset.add(gene);
			} else if( gene.contains("flag") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("flag") ) {
					subset = mapset.get("flag");
				} else {
					subset = new TreeSet<String>();
					mapset.put("flag", subset);
				}
				subset.add(gene);
			} else if( gene.contains("ATP") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("ATP") ) {
					subset = mapset.get("ATP");
				} else {
					subset = new TreeSet<String>();
					mapset.put("ATP", subset);
				}
				subset.add(gene);
			} else if( gene.contains("hypo") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("hypo") ) {
					subset = mapset.get("hypo");
				} else {
					subset = new TreeSet<String>();
					mapset.put("hypo", subset);
				}
				subset.add(gene);
			} else {
				Set<String>	subset = null;
				if( mapset.containsKey("other") ) {
					subset = mapset.get("other");
				} else {
					subset = new TreeSet<String>();
					mapset.put("other", subset);
				}
				subset.add(gene);
			}
		}
		
		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setErr( ps );
		for( String genegroup : mapset.keySet() ) {
			Set<String> subset = mapset.get( genegroup );
			System.err.println( genegroup + "   " + subset.size() );
			for( String gene : subset ) {
				System.err.println( "\t" + gene );
			}
		}
		ps.close();
	}
	
	static class Gene {
		public Gene( String name, String aa ) {
			this.name = name;
			this.aa = aa;
		}
		String	name;
		String	aa;
	};
	
	public static void splitGenes( String dir, String filename, int parts ) throws IOException {
		List<Gene>	genelist = new ArrayList<Gene>();
		File f = new File( dir, filename );
		BufferedReader br = new BufferedReader( new FileReader( f ) );
		String last = null;
		String aa = "";
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( last != null ) genelist.add( new Gene(last, aa) );
				last = line+"\n";
				aa = "";
			} else {
				aa += line+"\n";
			}
			line = br.readLine();
		}
		genelist.add( new Gene( last, aa) );
		br.close();
		
		int k = 0;
		int chunk = genelist.size()/parts+1;
		System.err.println( "Number of genes "+genelist.size()+" chunk size "+chunk );
		FileWriter fw = null;
		for( int i = 0; i < genelist.size(); i++ ) {
			Gene g = genelist.get(i);
			if( i%chunk == 0 ) {
				f = new File( dir, (k++)+"_t.aa" );
				if( fw != null ) fw.close();
				fw = new FileWriter( f );
			}
			
			fw.write( g.name );
			fw.write( g.aa );
		}
		fw.close();
	}
	
	public static void aaset() throws IOException {
		Set<String>	set1 = new HashSet<String>();
		File fa = new File("/home/sigmar/dtu/27623-AlgoInBio/week7/");
		File[] ff = fa.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if( name.length() == 5 ) return true;
				return false;
			}
		});
		
		for( File fb : ff ) {
			File f = new File( fb, fb.getName()+".dat" );
			if( f.exists() ) {
				BufferedReader br = new BufferedReader( new FileReader(f) );
				String line = br.readLine();
				while( line != null ) {
					String[] s = line.split("[\t ]+");
					set1.add( s[0] );
					
					line = br.readLine();
				}
				br.close();
				
				Set<String>	set2 = new HashSet<String>();
				f = new File("/home/sigmar/dtu/27623-AlgoInBio/project/train2.dat");
				br = new BufferedReader( new FileReader(f) );
				line = br.readLine();
				while( line != null ) {
					String[] s = line.split("[\t ]+");
					set2.add( s[0] );
					
					line = br.readLine();
				}
				br.close();
				
				int s1 = set1.size();
				int s2 = set2.size();
				set1.removeAll( set2 );
				int ns1 = set1.size();
				
				if( s1 != ns1 ) {
					System.err.println( fb.getName() );
					System.err.println( "\t"+s1 );
					System.err.println( "\t"+s2 );
					System.err.println( "\t"+ns1 );
				}
			}
		}
	}
	
	public static void newsets() throws IOException {
		File mf = new File("/home/sigmar/dtu/new/dtu/main_project/code/SMM/");
		File[] ff = mf.listFiles( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				if( name.length() == 5 && (name.startsWith("B") || name.startsWith("A")) && pathname.isDirectory() ) return true;
				return false;
			}
		});
		
		for( File f : ff ) {
			for( int x = 0; x < 5; x++ ) {
				for( int y = x+1; y < 5; y++ ) {
					FileWriter fw = new FileWriter( new File( f, "f00_"+x+"_"+y ) );
					for( int u = 0; u < 5; u++ ) {
						if( u != x && u != y ) {
							FileReader fr = new FileReader( new File( f, "c00"+u ) );
							BufferedReader br = new BufferedReader( fr );
							String line = br.readLine();
							while( line != null ) {
								fw.write( line+"\n" );
								line = br.readLine();
							}
						}
					}
					fw.close();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//System.err.println( Runtime.getRuntime().availableProcessors() );
		
		/*try {
			testbmatrix("/home/sigmar/mynd.png");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		init( args );
		
		/*try {
			//blastparse( "/home/sigmar/brachy_hyody.blastout.txt" );
			//blastparse( "/home/sigmar/thermus/lepto.blastout.txt" );
			//blastparse( "/home/sigmar/lept_spir.blastout.txt" );
			//blastparse( "/home/sigmar/spiro_blastresults.txt" );
			//blastparse( "/home/sigmar/lept_spir.lept_ames.blastout.txt" );
			//blastparse( "/home/sigmar/brach_spir.brachh.blastout.txt" );
			//blastparse( "/home/sigmar/lept_brach.lepto_inter.blastout.txt" );
			//blastparse( "/home/sigmar/spir_brach.blastout.txt" );
			//blastparse( "/home/sigmar/spiro_core_in_leptobrach_pan.blastout.txt" );
			
			blastparse( "/home/sigmar/thermus/newthermus/all.blastout" );
			
			//newstuff();
			//algoinbio();
			
			//newsets();
			
			//aaset();
			
			//splitGenes( "/home/sigmar/thermus/sandbox/", "thermus.aa", 32 );
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	private static void init( String[] args ) {
		String[]	stuff = {"scoto346","scoto2101","antag2120","scoto2127","scoto252","scoto1572","scoto4063","eggertsoni2789","islandicus180610","ttHB27join","ttHB8join","ttaqua"};
		String[]	stuff2 = {"aa1","aa2","aa4","aa6","aa7","aa8"};
		String[]	names = {"aa1.out","aa2.out","aa4.out","aa6.out","aa7.out","aa8.out"};
		String[]	all = {"all.aa"};
		String[]	name = {"all.blastout"};
		File 		dir = new File("/home/sigmar/thermus/results/");
		File 		dir2 = new File("/home/sigmar/thermus/newthermus/");
		
		swapmap.put("aa1.out", "scoto346");
		swapmap.put("aa2.out", "scoto2101");
		swapmap.put("aa4.out", "scoto2127");
		swapmap.put("aa6.out", "scoto252");
		swapmap.put("aa7.out", "scoto1572");
		swapmap.put("aa8.out", "scoto4063");
		
		try {
			//func1( name, dir2 );
			//printnohits( stuff, dir, dir2 );
			//createConcatFsa( stuff, dir2 );
			
			loci2aasequence( all, dir2 );
			loci2gene( stuff2, dir );
			func4( dir2, name );
			
			//PrintStream ps = new PrintStream("/home/sigmar/uff.txt");
			//System.setErr( ps );
			
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/alls.fsa"), 1 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/alls.fsa"), 2 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/alls.fsa"), 3 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/alls.fsa"), 4 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/alls.fsa"), 5 );
			
			//aahist( new File("/home/sigmar/tp.aa"), new File("/home/sigmar/nc.aa") );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/nc.aa"), 1 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/nc.aa"), 2 );
			//aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/nc.aa"), 3 );
			/*aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/nc.aa"), 4 );
			aahist( new File("/home/sigmar/thermus/out/all.fsa"), new File("/home/sigmar/nc.aa"), 5 );
			
			System.err.println();
			
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/tp.aa"), 1 );
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/tp.aa"), 2 );
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/tp.aa"), 3 );
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/tp.aa"), 4 );
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/tp.aa"), 5 );
			
			System.err.println();
			//aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/nc.aa"), 6 );
			aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/tp.aa"), 1 );
			aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/tp.aa"), 2 );
			aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/tp.aa"), 3 );
			aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/tp.aa"), 4 );
			aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/tp.aa"), 5 );*/
			//aahist( new File("/home/sigmar/thermus/hb27.aa"), new File("/home/sigmar/thermus/out/aa2.fsa") );
			//aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/thermus/hb27.aa") );
			
			//ps.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
