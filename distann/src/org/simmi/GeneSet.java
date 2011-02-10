package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.swing.AbstractAction;
import javax.swing.DefaultRowSorter;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class GeneSet extends JApplet {
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
	
	private static void panCoreFromNRBlast( Reader rd, String outfile, Map<String,Gene>  ret, Map<String,String> allgenes, Map<String,Set<String>> geneset, Map<String,Set<String>> geneloc, Set<String> poddur ) throws IOException {
		FileWriter fw = null;
		if( outfile != null ) fw = new FileWriter( outfile );
		
		Set<String> extra = new HashSet<String>();
		
		extra.add("protein of unknown function DUF820");
		extra.add("ABC transporter ATP-binding protein");
		extra.add("short-chain dehydrogenase/reductase SDR");
		extra.add("two component transcriptional regulator, winged helix family");
		extra.add("Tetratricopeptide TPR_2 repeat protein");
		extra.add("extracellular solute-binding protein family 1");
		extra.add("histidine kinase");
		extra.add("cytochrome c class I");
		extra.add("NUDIX hydrolase");
		extra.add("GCN5-related N-acetyltransferase");
		extra.add("acyl-CoA dehydrogenase domain protein");
		extra.add("protein of unknown function DUF6 transmembrane");
		extra.add("response regulator receiver protein");
		extra.add("Peptidase M23");
		extra.add("glycosyltransferase");
		extra.add("metallophosphoesterase");
		extra.add("ABC transporter permease protein");
		extra.add("UspA domain protein");
		extra.add("transposase, IS605 OrfB family");
		extra.add("methyltransferase");
		extra.add("aminotransferase class I and II");
		extra.add("PilT protein domain protein");
		extra.add("hypothetical protein");
		extra.add("nucleotidyltransferase");
		extra.add("serine/threonine protein kinase");
		extra.add("transcriptional regulator");
		extra.add("ABC-2 type transporter");
		extra.add("AMP-dependent synthetase and ligase");
		extra.add("DNA polymerase beta domain protein region");
		extra.add("ABC transporter-like protein");
		extra.add("Radical SAM domain protein");
		extra.add("Extracellular ligand-binding receptor");
		extra.add("HAD-superfamily hydrolase, subfamily IA, variant 3");
		extra.add("metal dependent hydrolase");
		extra.add("sugar ABC transporter, permease protein");
		extra.add("acetyl-CoA acetyltransferase");
		extra.add("LmbE family protein");
		extra.add("transcriptional repressor");
		extra.add("transposase IS605 OrfB family");
		extra.add("beta-lactamase domain protein");
		extra.add("dehydrogenase");
		extra.add("enoyl-CoA hydratase");
		extra.add("metal dependent phosphohydrolase");
		extra.add("two-component response regulator");
		extra.add("ABC transporter, permease protein");
		extra.add("alkyl hydroperoxide reductase/ Thiol specific antioxidant/ Mal");
		extra.add("SMC domain protein");
		extra.add("protein of unknown function DUF88");
		extra.add("Roadblock/LC7 family protein");
		extra.add("TetR family transcriptional regulator");
		extra.add("translation initiation factor IF-2");
		extra.add("glycosyl transferase family 2");
		extra.add("Integrase catalytic region");
		extra.add("putative lipoprotein");
		extra.add("tetratricopeptide repeat domain-containing protein");
		extra.add("ABC transporter permease");
		extra.add("conserved domain-containing protein");
		extra.add("MutT/nudix family protein");
		extra.add("sensor histidine kinase");
		extra.add("CBS domain-containing protein");
		extra.add("transporter, major facilitator family");
		extra.add("acyl-CoA dehydrogenase");
		extra.add("acetyltransferase");
		extra.add("universal stress protein family");
		extra.add("putative hydrolase");
		extra.add("sugar ABC transporter permease");
		extra.add("ATP-dependent protease La");
		extra.add("permease");
		extra.add("metallo-beta-lactamase family protein");
		extra.add("immunogenic protein");
		extra.add("leucine-, isoleucine-, valine-, threonine-, and alanine-binding");
		extra.add("branched-chain amino acid ABC transporter permease");
		extra.add("membrane protein");
		extra.add("serine protease");
		extra.add("oxidoreductase, short-chain dehydrogenase/reductase family");
		extra.add("branched-chain amino acid ABC transporter ATP-binding protein");
		extra.add("cytochrome c-552");
		extra.add("hypothetical protein TaqDRAFT_4901");
		extra.add("putative PIN domain-containing protein");
		extra.add("ATPase");
		extra.add("long-chain-fatty-acid--CoA ligase");
		extra.add("thioredoxin");
		extra.add("aspartyl-tRNA synthetase");
		extra.add("thioredoxin reductase");
		extra.add("ggdef domain-containing protein");
		extra.add("transposase BAC55317.2");
		extra.add("integrase, catalytic region");
		extra.add("ribose-phosphate pyrophosphokinase");
		extra.add("N-acetyl-gamma-glutamyl-phosphate reductase");
		extra.add("putative oxidoreductase");
		extra.add("glycolate oxidase subunit GlcE");
		extra.add("GntR family transcriptional regulator");
		extra.add("3-oxoacyl-[acyl-carrier-protein] reductase");
		extra.add("PilT protein domain-containing protein");
		extra.add("acetylglutamate kinase");
		extra.add("spermidine/putrescine import ATP-binding protein PotA");

		
		BufferedReader 	br = new BufferedReader( rd );
		String 	query = null;
		String	evalue = null;
		String line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg") ) {
				String[] split = trim.split("\\|");
				
				String id = split[1];
				String desc = split[2];
				String teg = "";
				int idx = desc.lastIndexOf('[');
				if( idx > 0 ) {
					teg = desc.substring(idx);
					desc = desc.substring(0, idx-1).trim();
				} else {
					desc = desc.trim();
				}
				
				if( extra.contains(desc) || desc.equals("conserved hypothetical protein") || desc.equals("transposase") || desc.equals("hydrolase") || desc.equals("ABC transporter related") || desc.equals("transporter")
						|| desc.equals("putative cytoplasmic protein") || desc.equals("binding-protein-dependent transport systems inner membrane component") || desc.equals("major facilitator superfamily MFS_1")
						|| desc.equals("transposase IS4 family protein") || desc.equals("inner-membrane translocator") ) {
					desc = desc + " " + id;
				}
				
				Set<String>	set;
				String padda = query.split("_")[0];
				if( geneset.containsKey( padda ) ) {
					set = geneset.get( padda );
				} else {
					set = new HashSet<String>();
					geneset.put( padda, set );
				}
				
				//set.add( id );
				set.add( desc );
				
				Gene gene;
				if( ret.containsKey( desc ) ) {
					gene = ret.get( desc );
				} else {
					gene = new Gene(desc, null);
					ret.put( desc, gene );
					gene.refid = id;
				}
				//gene.blastspec = teg;
				if( gene.species == null ) {
					gene.species = new HashMap<String,Set<Tegeval>>();
				}
				
				Set<Tegeval>	stv;
				if( !gene.species.containsKey(padda) ) {
					stv = new HashSet<Tegeval>(); 
					gene.species.put( padda, stv );
				} else stv = gene.species.get(padda);
				stv.add( new Tegeval( teg, Double.parseDouble(evalue), aas.get(query), query ) );
				
				if( !allgenes.containsKey( desc ) || allgenes.get( desc ) == null ) {
					allgenes.put( desc, split.length > 1 ? teg + " " + id : null );
				}
				
				Set<String>	locset = null;
				if( geneloc.containsKey( desc ) ) {
					locset = geneloc.get(desc);
				} else {
					locset = new HashSet<String>();
					geneloc.put(desc, locset);
				}
				//locset.add( swapmap.get(name)+"_"+query + " " + evalue );
				locset.add( query + " " + evalue );
				
				query = null;
				if( fw != null ) {
					if( line.indexOf('[') > line.indexOf(']') ) {
						String newline = br.readLine();
						line += newline.trim();
					}
					fw.write( line + "\n" );
				}
			} else if( trim.contains("No hits") ) {
				Gene gene;
				String aa = aas.get(query);
				if( ret.containsKey( aa ) ) {
					gene = ret.get( aa );
				} else {
					gene = new Gene("_"+aa, null);
					ret.put( "_"+aa, gene );
					gene.refid = query;
				}
				
				String padda = query.split("_")[0];
				if( gene.species == null ) gene.species = new HashMap<String,Set<Tegeval>>();
				double deval = -1.0;
				try {
					deval = Double.parseDouble(evalue);
				} catch( Exception e ) {
					System.err.println("ok");
				}
				//gene.species.put( padda, new Tegeval( padda, deval, aas.get(query), query ) );
				Set<Tegeval>	stv;
				if( !gene.species.containsKey(padda) ) {
					stv = new HashSet<Tegeval>(); 
					gene.species.put( padda, stv );
				} else stv = gene.species.get(padda);
				stv.add( new Tegeval( padda, deval, aas.get(query), query ) );
				
				
				//System.err.println( prename + "\tNo match" );
				if( fw != null ) fw.write( line + "\n" );
			} else if( trim.startsWith("Query=") ) {
				query = trim.substring(6).trim().split("[ ]+")[0];
				poddur.add( query.split("_")[0] );
				if( fw != null ) fw.write( line + "\n" );
			} else if( query != null && trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|") ) {
				String[] split = trim.split("[\t ]+");
				evalue = split[split.length-1];
				if( fw != null ) fw.write( line + "\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		if( fw != null ) {
			fw.close();
		}
	}
	
	static Map<String,String>		swapmap = new HashMap<String,String>();
	public static Map<String,Gene> panCoreFromNRBlast( String[] names, File dir ) throws IOException {
		Map<String,Gene>	ret = new HashMap<String,Gene>();
		
		Map<String,String>		allgenes = new HashMap<String,String>();
		Map<String,Set<String>>	geneset = new HashMap<String,Set<String>>();
		Map<String,Set<String>>	geneloc = new HashMap<String,Set<String>>();
		
		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setOut( ps );
		
		Set<String>	poddur = new HashSet<String>();
		for( String name : names ) {
			File 			f = new File( dir, name );			
			//FileReader		fr = new FileReader( f );
			panCoreFromNRBlast( new FileReader(f), null, ret, allgenes, geneset, geneloc, poddur );
		}
		
		for( String name : geneset.keySet() ) {
			Set<String> set = geneset.get(name);
			System.out.println( name + " genes total: " + set.size() );
			geneset.put( name, set );
		}
		
		Set<String>	allset = new HashSet<String>( allgenes.keySet() );
		for( String name : geneset.keySet() ) {
			Set<String>	gset = geneset.get( name );
			allset.retainAll( gset );
		}
		System.out.println( "Core genome size: " + allset.size() );
		System.out.println( "Pan genome size: " + allgenes.size() );
		
		Set<String>	nameset = null;
		
		for( String gname : allset ) {
			System.out.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
		}
		
		boolean info = true;
		
		for( String aname : poddur ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( poddur );
			nameset.remove(aname);
			for( String tname : nameset ) {
				allset.removeAll( geneset.get(tname) );
			}
			//System.err.println( "Genes found only in " + swapmap.get(aname) + "\t" + allset.size() );
			
			if( allset.size() > 0 ) System.out.println( "Genes only in " + aname + "\t" + allset.size() );
			if( info ) {
				for( String gname : allset ) {
					System.out.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
				}
			}
		}
		
		for( String aname : poddur ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( poddur );
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
			
			if( allset.size() > 0 ) System.out.println( "Genes only in all of " + reset + "\t" + allset.size() );
			if( info ) printflubb( allset, allgenes, geneloc );
		}
		
		List<String> poddulist = new ArrayList<String>( poddur );
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( poddulist );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( poddulist.get(i) );
				nameset.remove( poddulist.get(y) );
				
				allset.removeAll( geneset.get( poddulist.get(i) ) );
				allset.removeAll( geneset.get( poddulist.get(y) ) );
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
				
				if( allset.size() > 0 ) System.out.println( "Genes only in all of " + reset + "\t" + allset.size() );
				if( info ) printflubb( allset, allgenes, geneloc );
			}
		}
		
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( poddulist );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( poddulist.get(i) );
				nameset.remove( poddulist.get(y) );
				
				allset.retainAll( geneset.get( poddulist.get(i) ) );
				allset.retainAll( geneset.get( poddulist.get(y) ) );
				for( String tname : nameset ) {
					allset.removeAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				//reset.add( swapmap.get(names[i]) );
				//reset.add( swapmap.get(names[y]) );
				reset.add( poddulist.get(i) );
				reset.add( poddulist.get(y) );
				
				/*for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}*/
				
				if( allset.size() > 0 ) System.out.println( "Genes only in all of " + reset + "\t" + allset.size() );
				if( info ) printflubb( allset, allgenes, geneloc );
			}
		}
		
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				for( int k = y+1; k < poddulist.size(); k++ ) {
					allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k)} ), info, false );
					if( info ) printflubb( allset, allgenes, geneloc );
					
					allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k)} ), info, true );
					if( info ) printflubb( allset, allgenes, geneloc );
				}
			}
		}
		
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				for( int k = y+1; k < poddulist.size(); k++ ) {
					for( int l = k+1; l < poddulist.size(); l++ ) {
						allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l)} ), info, false );
						if( info ) {
							for( String gname : allset ) {
								System.out.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
							}
						}
						
						allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l)} ), info, true );
						if( info ) {
							for( String gname : allset ) {
								System.out.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
							}
						}
					}
				}
			}
		}
		
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				for( int k = y+1; k < poddulist.size(); k++ ) {
					for( int l = k+1; l < poddulist.size(); l++ ) {
						for( int m = l+1; m < poddulist.size(); m++ ) {
							allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m)} ), info, false );
							if( info ) printflubb( allset, allgenes, geneloc );
							
							allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m)} ), info, true );
							if( info ) printflubb( allset, allgenes, geneloc );
						}
					}
				}
			}
		}
		
		for( int i = 0; i < poddulist.size(); i++ ) {
			for( int y = i+1; y < poddulist.size(); y++ ) {
				for( int k = y+1; k < poddulist.size(); k++ ) {
					for( int l = k+1; l < poddulist.size(); l++ ) {
						for( int m = l+1; m < poddulist.size(); m++ ) {
							for( int n = m+1; n < poddulist.size(); n++ ) {
								allset = flubb( allgenes.keySet(), poddulist, geneset, Arrays.asList( new String[] {poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m), poddulist.get(n)} ), info, false );
								if( info ) printflubb( allset, allgenes, geneloc );
							}
						}
					}
				}
			}
		}
		
		System.out.println( "Unique genes total: " + allgenes.size() );
		
		ps.close();
		
		return ret;
	}
	
	public static void printflubb( Set<String> allset, Map<String,String> allgenes, Map<String,Set<String>> geneloc ) {
		for( String gname : allset ) {
			System.out.println( gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname) );
		}
	}
	
	public static Set<String> flubb( Set<String> allgeneskeyset, List<String> poddulist, Map<String,Set<String>> geneset, Collection<String> rpoddur, boolean info, boolean invert ) {
		Set<String> allset = new HashSet<String>( allgeneskeyset );
		Set<String> nameset = new HashSet<String>( poddulist );
		//nameset.add( names[i] );
		//nameset.add( names[y] );
		
		nameset.removeAll( rpoddur );
		//nameset.remove( poddulist.get(i) );
		//nameset.remove( poddulist.get(y) );
		//nameset.remove( poddulist.get(k) );
		
		Set<String>	reset = new HashSet<String>();
		if( invert ) {
			for( String padda : rpoddur ) {
				allset.removeAll( geneset.get(padda) );
			}
			//allset.removeAll( geneset.get( poddulist.get(i) ) );
			//allset.removeAll( geneset.get( poddulist.get(y) ) );
			//allset.removeAll( geneset.get( poddulist.get(k) ) );
			for( String tname : nameset ) {
				allset.retainAll( geneset.get(tname) );
			}
			
			for( String name : nameset ) {
				//reset.add( swapmap.get(name) );
				reset.add( name );
			}
		} else {
			for( String padda : rpoddur ) {
				allset.retainAll( geneset.get(padda) );
			}
			for( String tname : nameset ) {
				allset.removeAll( geneset.get(tname) );
			}
			for( String name : rpoddur ) {
				reset.add( name );
			}
		}
		
		/*reset.add( swapmap.get(names[i]) );
		reset.add( swapmap.get(names[y]) );
		reset.add( swapmap.get(names[k]) );*/
		
		if( allset.size() > 0 ) System.out.println( "Genes only in all of " + reset + "\t" + allset.size() );
		return allset;
	}
	
	public static void recursiveSet( int fin, int val ) {
		if( val < fin ) {
			recursiveSet( fin, val+1 );
		} else {
			
		}
	}
	
	private static void loci2aasequence( Reader rd ) throws IOException {
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String name = null;
		String ac = "";
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( ac.length() > 0 ) aas.put(name, ac);
				
				ac = "";
				name = line.substring(1).split(" ")[0];
				
				int v = name.indexOf("contig");
				/*if( v != -1 ) {
					int i1 = name.indexOf('_',v);
					int i2 = name.indexOf('_', i1+1);
					name = name.substring(0,i1) + name.substring(i2);
				}*/
			} else ac += line.trim();
			line = br.readLine();
		}
		if( ac.length() > 0 ) aas.put(name, ac);
		br.close();
	}
	
	static Map<String,String>	aas = new HashMap<String,String>();
	public static void loci2aasequence( String[] stuff, File dir2 ) throws IOException {
		for( String st : stuff ) {
			File aa = new File( dir2, st );
			loci2aasequence( new FileReader(aa) );
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
		
		int sss = mstrw+72*species.size()+10+72;
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
	
		int mins = 1000000;
		int maxs = 0;
		
		int minrs = 1000000;
		int maxrs = 0;
		
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
				if( rr > maxr ) maxr = rr;
				if( rr < minr ) minr = rr;
				
				int ss = totot;
				if( ss > maxs ) maxs = ss;
				if( ss < mins ) mins = ss;
				
				int rs = (int)spc1tot;
				if( rs > maxrs ) maxrs = rs;
				if( rs < minrs ) minrs = rs;
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
					String str = spc1tot+"";
					int	nstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw/2, mstrw+47+where*72-15 );
					
					str = totot+"";
					nstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw/2, mstrw+47+where*72 );
					
					ps.printf("%.1f%s", (float)(dval*100.0), "%" );
					str = baos.toString();
					baos.reset();
					nstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw/2, mstrw+47+where*72+15 );
				} else {
					double dval = (double)spc2tot/(double)spc1tot;
					int cval = (int)(200.0*(maxh - dval)/(maxh - minh));
					g2.setColor( new Color( cval, 255, cval ) );
					g2.fillRoundRect( mstrw+10+wherex*72, mstrw+10+where*72, 64, 64, 16, 16);
					
					g2.setColor( Color.white );
					String str = spc2tot+"";
					int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw2/2, mstrw+47+where*72-15 );
					//int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					str = spc1tot+"";
					int	nstrw1 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-nstrw1/2, mstrw+47+where*72 );
					
					double hlut = 100.0*((double)spc2tot/(double)spc1tot);
					ps.printf("%.1f%s", (float)hlut, "%" );
					str = baos.toString();
					baos.reset();
					int	pstrw = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+wherex*72-pstrw/2, mstrw+47+where*72+15 );
				}
				
				if( wherex == 0 ) {
					int dval = totot;
					int cval = (int)(200.0*(maxs - dval)/(maxs - mins));
					g2.setColor( new Color( cval, cval, 255 ) );
					g2.fillRoundRect( mstrw+10+species.size()*72, mstrw+10+where*72, 64, 64, 16, 16);
					
					g2.setColor( Color.white );
					String str = dval+"";
					int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+species.size()*72-nstrw2/2, mstrw+47+where*72 );
					//int	nstrw2 = g2.getFontMetrics().stringWidth( str );
					
					dval = spc1tot;
					cval = (int)(200.0*(maxrs - dval)/(maxrs - minrs));
					g2.setColor( new Color( cval, cval, 255 ) );
					g2.fillRoundRect( mstrw+10+where*72, mstrw+10+species.size()*72, 64, 64, 16, 16);
					g2.setColor( Color.white );
					str = spc1tot+"";
					int	nstrw1 = g2.getFontMetrics().stringWidth( str );
					g2.drawString( str, mstrw+42+where*72-nstrw1/2, mstrw+47+species.size()*72 );
				}
				
				wherex++;
			}
			
			System.err.println();
			where++;
		}
		
		g2.setColor( Color.gray );
		g2.fillRoundRect( mstrw+10+species.size()*72, mstrw+10+species.size()*72, 64, 64, 16, 16);
		
		int core = 0;
		int pan = 0;
		
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
						
			pan += setmap.size();
			if( set.size() == 12 ) core += setmap.size();
		}
		
		g2.setColor( Color.white );
		String str = core+"";
		int	nstrw2 = g2.getFontMetrics().stringWidth( str );
		g2.drawString( str, mstrw+42+species.size()*72-nstrw2/2, mstrw+47+species.size()*72-15 );
		//int	nstrw2 = g2.getFontMetrics().stringWidth( str );
		str = pan+"";
		int	nstrw1 = g2.getFontMetrics().stringWidth( str );
		g2.drawString( str, mstrw+42+species.size()*72-nstrw1/2, mstrw+47+species.size()*72 );
		
		return bi;
	}
	
	private static void intersectSets( Set<String> all, List<Set<String>> total ) {
		//Set<Set<String>>	rem = new HashSet<Set<String>>();
		//int i = 0;
		
		if( all.contains("scoto2101_contig00162_11") ) {
			System.err.println();
		}
		
		List<Set<String>>	newtotal = new ArrayList<Set<String>>();
		for( Set<String>	check : total ) {
			Set<String> 		cont = new HashSet<String>();
			
			for( String aval : all ) {
				if( check.contains( aval ) ) cont.add( aval );
			}
			all.removeAll( cont );
			
			if( cont.size() > 0 ) {
				if( cont.size() < check.size() ) {
					Set<String> 		ncont = new HashSet<String>( check );
					ncont.removeAll( cont );
					
					newtotal.add( ncont );
				}
				newtotal.add( cont );
			} else newtotal.add( check );
			
			//else if( all.size() > 0 ) newtotal.add( all );
			
			if( cont.size() > 0 ) all.removeAll( cont );
		}
		
		if( all.size() > 0 ) newtotal.add( all );
		
		total.clear();
		total.addAll( newtotal );
		
		/*for( Set<String> erm : rem ) {
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
		}*/
	}
	
	private static void joinSets( Set<String> all, List<Set<String>> total ) {		
		Set<String> cont = null;
		Set<Set<String>>	rem = new HashSet<Set<String>>();
		int i = 0;
		for( Set<String>	check : total ) {			
			for( String aval : all ) {
				if( check.contains(aval) ) {
					if( cont == null ) {
						cont = check;
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
	
	private static Collection<Set<String>> joinBlastSets( File dir, String[] stuff, String write, boolean union ) throws IOException {
		List<Set<String>>	total = new ArrayList<Set<String>>();
		FileWriter fw = write == null ? null : new FileWriter( write ); //new FileWriter("/home/sigmar/blastcluster.txt");
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
						if( trim.startsWith("scoto") || trim.startsWith("anta") || trim.startsWith("tt") || trim.startsWith("egg") || trim.startsWith("island") || trim.startsWith("tscoto") ) {
							String val = trim.substring( 0, trim.indexOf('#')-1 );
							int v = val.indexOf("contig");
							/*if( v != -1 ) {
								int i1 = val.indexOf('_',v);
								int i2 = val.indexOf('_', i1+1);
								val = val.substring(0,i1) + val.substring(i2);
							}*/
							all.add( val );
						}
						line = br.readLine();
					}
					
					if( fw != null ) fw.write( all.toString()+"\n" );
					
					if( union ) joinSets( all, total );
					else intersectSets( all, total );
					
					if( line == null ) break;
				}
				
				line = br.readLine();
			}
		}
		if( fw != null ) fw.close();
		
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
	
	private static List<Set<String>> loadSimpleClusters( Reader rd ) throws IOException {
		//FileReader		fr = new FileReader( file );
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		List<Set<String>>	ret = new ArrayList<Set<String>>();
		Set<String>			prevset = null;
		
		while( line != null ) {
			if( !line.startsWith("\t") ) {
				String[] split = line.split("[\t ]+");
				try {
					Integer.parseInt( split[0] );
					prevset = new HashSet<String>();
					ret.add( prevset );
				} catch( Exception e ) {
					
				}
			} else { //if( line.startsWith("\t") ) {
				String trimline = line.trim();
				if( trimline.startsWith("[") ) {
					String[] subsplit = trimline.substring(1,trimline.length()-1).split("[, ]+");
					Set<String> trset = new HashSet<String>( Arrays.asList(subsplit) );
					if( prevset != null ) prevset.addAll( trset );
					//ret.add( trset );
				}
			}
			line = br.readLine();
		}
		br.close();
		
		return ret;
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
	
	private static Map<Set<String>,Set<Map<String,Set<String>>>> initCluster( Collection<Set<String>>	total, Set<String> species ) {
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
		
		
		Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = loadCluster("/home/sigmar/burb2.txt");
		
		//Set<String>	species = new TreeSet<String>();
		//Set<Set<String>> total = func4_header( dir, stuff );
		//List<Set<String>> total = readBlastList( "/home/sigmar/blastcluster.txt" );
		
		/*FileWriter	fw = new FileWriter("/home/sigmar/joincluster.txt");
		for( Set<String> sset : total ) {
			fw.write( sset.toString()+"\n" );
		}
		fw.close();*/
		
		/*Set<String>	trall = new HashSet<String>();
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
		}*/
		
		//Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
		Set<String>	species = speciesFromCluster( clusterMap );
		
		//writeSimplifiedCluster( "/home/sigmar/burb2.txt", clusterMap );
		
		writeBlastAnalysis( clusterMap, species );
	}
	
	public static void clusterFromSimplifiedBlast( String filename ) throws IOException {
		clusterFromSimplifiedBlast(filename, null);
	}
	
	public static void clusterFromSimplifiedBlast( String filename, String writeSimplifiedCluster ) throws IOException {
		Set<String>	species = new TreeSet<String>();
		List<Set<String>> total = readBlastList( filename ); //"/home/sigmar/blastcluster.txt" );
		Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
		
		if( writeSimplifiedCluster != null ) writeSimplifiedCluster( writeSimplifiedCluster, clusterMap ); //"/home/sigmar/burb2.txt", clusterMap );
		
		writeBlastAnalysis( clusterMap, species );
	}
	
	public static void clusterFromBlastResults( File dir, String[] stuff ) throws IOException {
		clusterFromBlastResults( dir, stuff, null, null, true );
	}
	
	public static void clusterFromBlastResults( File dir, String[] stuff, String writeSimplifiedCluster, String writeSimplifiedBlast, boolean union ) throws IOException {
		Set<String>	species = new TreeSet<String>();
		Collection<Set<String>> total = joinBlastSets( dir, stuff, writeSimplifiedBlast, union );
		Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
		
		if( writeSimplifiedCluster != null ) writeSimplifiedCluster( writeSimplifiedCluster, clusterMap ); //"/home/sigmar/burb2.txt", clusterMap );
		
		writeBlastAnalysis( clusterMap, species );
	}
	
	public static void writeBlastAnalysis( Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap, Set<String> species ) throws IOException {
		BufferedImage	img = bmatrix( species, clusterMap );
		ImageIO.write( img, "png", new File("/home/sigmar/mynd.png") );
		
		PrintStream ps = new PrintStream( new FileOutputStream("/home/sigmar/out3.out") );
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
		
		
		next( clusterMap );
		
		
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
	
	public static void next( Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) throws IOException {
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
										} else geneset.add(gene.replace('\t', ' '));
									}
								}
								System.err.println( "\t"+geneset );
							}
						}
					}
			}
		}
		
		
		FileWriter fw = new FileWriter("/home/sigmar/dragon.txt");
		
		boolean done;
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
				done = false;
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					
					System.err.println( "In " + s + " containing " + genes.size() );
					for( String gene : genes ) {
						String aa = lociMap.get(gene);
						if( aa == null ) {
							aa = "Not found\t" + aas.get( gene );
						}
						aa += "\t" + aas.get(gene);
						System.err.println( gene + "\t" + aa );
						
						if( !done && set.size() == 12 && gene.startsWith("ttHB27") ) {
							fw.write(">"+gene+"\n");
							for( int k = 0; k < aa.length(); k+=60 ) {
								fw.write( aa.substring(k, Math.min(k+60, aa.length()) )+"\n");
							}
							done = true;
						}
					}
				}
				System.err.println();
				
				i++;
			}
		}
		
		fw.close();
	}
	
	private static void loci2gene( Reader rd, String outfile ) throws IOException {
		FileWriter fw = null;
		if( outfile != null ) fw = new FileWriter( outfile );
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String name = null;
		String evalue = null;
		while( line != null ) {
			if( line.startsWith("Query= ") ) {
				name = line.substring(8).split(" ")[0];
				//int i1 = name.indexOf('_');
				//int i2 = name.indexOf('_', i1+1);
				//name = name.substring(0,i1) + name.substring(i2);
				//System.err.println(name);
				
				if( fw != null ) fw.write( line + "\n" );
			}
			
			if( line.contains("No hits") ) {
				String prename = name; //swapmap.get(st+".out")+"_"+name;
				lociMap.put( prename, "No match\t"+aas.get(prename) );
				//System.err.println( prename + "\tNo match" );
				if( fw != null ) fw.write( line + "\n" );
			}
			
			if( line.startsWith("ref|") || line.startsWith("sp|") || line.startsWith("pdb|") || 
					line.startsWith("dbj|") || line.startsWith("gb|") || line.startsWith("emb|") || line.startsWith("pir|") || line.startsWith("tpg|") ) {
				String[] split = line.split("[\t ]+");
				evalue = split[split.length-1];
				
				if( fw != null ) fw.write( line + "\n" );
			}
			
			if( line.startsWith(">ref") || line.startsWith(">sp") || line.startsWith(">pdb") || 
					line.startsWith(">dbj") || line.startsWith(">gb") || line.startsWith(">emb") || line.startsWith(">pir") || line.startsWith(">tpg") ) {
				String prename = name; //swapmap.get(st+".out")+"_"+name;
				String[] split = line.split("\\|");
				lociMap.put( prename, split[1] + (split.length > 2 ? "\t" + split[2] : "") + "\t" + evalue );
				//System.err.println( prename + "\t" + split[1] );
				if( fw != null ) fw.write( line + "\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		
		if( fw != null ) fw.close();
	}
	
	static Map<String,String>	lociMap = new HashMap<String,String>();
	public static void loci2gene( String[] stuff, File dir ) throws IOException {
		//Map<String,String>	aas = new HashMap<String,String>();
		for( String st : stuff ) {			
			File ba = new File( dir, st );
			loci2gene( new FileReader(ba), null );
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
			if( query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || 
					trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg") ) ) {
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
			} else if( evalue == null && query != null && (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || 
					trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|") ) ) {
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
			} else if( gene.contains("inase") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("inase") ) {
					subset = mapset.get("inase");
				} else {
					subset = new TreeSet<String>();
					mapset.put("inase", subset);
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
			} else if( gene.contains("hypot") ) {
				Set<String>	subset = null;
				if( mapset.containsKey("hypot") ) {
					subset = mapset.get("hypot");
				} else {
					subset = new TreeSet<String>();
					mapset.put("hypot", subset);
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
	
	static class Function {
		public Function() {}
		String go;
		String ec;
		String metacyc;
		String kegg;
		String name;
		String namespace;
		String desc;
		Set<String>	isa;
		Set<String>	subset;
		Set<String>	geneentries;
		int index;
	};
	
	static class Tegeval implements Comparable<Tegeval> {
		public Tegeval( String tegund, double evalue, String sequence, String contig ) {
			teg = tegund;
			eval = evalue;
			seq = sequence;
			cont = contig;
		}
		
		String 	teg;
		double	eval;
		String	cont;
		String	seq;
		
		public String toString() {
			return eval + " " + teg;
		}

		@Override
		public int compareTo(Tegeval o) {
			int comp = Double.compare(eval, o.eval);
			return comp == 0 ? teg.compareTo(o.teg) : comp;
		}
	}
	
	static class Gene {
		public Gene( String name, String aa ) {
			this.name = name;
			this.aa = aa;
		}
		String	name;
		String	refid;
		String	genid;
		String	uniid;
		String  keggid;
		String	blastspec;
		Set<String>	funcentries;
		Map<String,Set<Tegeval>>		species;
		String	aa;
		int index;
	};
	
	public static void splitGenes( String dir, String filename ) throws IOException {
		Map<String,List<Gene>>	genemap = new HashMap<String,List<Gene>>();
		File f = new File( dir, filename );
		BufferedReader br = new BufferedReader( new FileReader( f ) );
		String last = null;
		String aa = "";
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( last != null ) {
					String strain = last.split("_")[0].substring(1);
					List<Gene>	genelist = null;
					if( genemap.containsKey( strain ) ) {
						genelist = genemap.get(strain);
					} else {
						genelist = new ArrayList<Gene>();
						genemap.put( strain, genelist );
					}
					genelist.add( new Gene(last, aa) );
				}
				last = line+"\n";
				aa = "";
			} else {
				aa += line+"\n";
			}
			line = br.readLine();
		}
		String strain = last.split("_")[0].substring(1);
		List<Gene>	genelist = null;
		if( genemap.containsKey( strain ) ) {
			genelist = genemap.get(strain);
		} else {
			genelist = new ArrayList<Gene>();
			genemap.put( strain, genelist );
		}
		genelist.add( new Gene( last, aa ) );
		br.close();
		
		for( String str : genemap.keySet() ) {
			f = new File( dir, str+".orf.fsa" );
			FileWriter 	fw = new FileWriter( f );
			List<Gene> glist = genemap.get(str);
			for( int i = 0; i < glist.size(); i++ ) {
				Gene g = glist.get(i);			
				fw.write( g.name );
				fw.write( g.aa );
			}
			fw.close();
		}
	}
	
	public static void splitGenes( String dir, String filename, int parts ) throws IOException {
		List<Gene>	genelist = new ArrayList<Gene>();
		File f = new File( dir, filename );
		BufferedReader br = new BufferedReader( new FileReader( f ) );
		String last = null;
		String aa = "";
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( last != null ) genelist.add( new Gene( last, aa) );
				last = line+"\n";
				aa = "";
			} else {
				aa += line+"\n";
			}
			line = br.readLine();
		}
		genelist.add( new Gene( last, aa ) );
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
			//blastparse( "/home/sigmar/blastout/nilli.blastout" );
			//blastparse( "/home/sigmar/thermus/lepto.blastout.txt" );
			//blastparse( "/home/sigmar/lept_spir.blastout.txt" );
			//blastparse( "/home/sigmar/spiro_blastresults.txt" );
			//blastparse( "/home/sigmar/lept_spir.lept_ames.blastout.txt" );
			//blastparse( "/home/sigmar/brach_spir.brachh.blastout.txt" );
			//blastparse( "/home/sigmar/lept_brach.lepto_inter.blastout.txt" );
			//blastparse( "/home/sigmar/spir_brach.blastout.txt" );
			//blastparse( "/home/sigmar/spiro_core_in_leptobrach_pan.blastout.txt" );
			
			//blastparse( "/home/sigmar/sim.blast" );
			//blastparse( "/home/sigmar/thermus/newthermus/all.blastout" );
			
			//newstuff();
			//algoinbio();
			
			//newsets();
			
			//aaset();
			
			//splitGenes( "/home/sigmar/thermus/newthermus/", "all.aa", 128 );
			//splitGenes( "/home/sigmar/thermus/newthermus", "0_t.aa", 64 );
			//splitGenes( "/home/sigmar/thermus/newthermus/test/", "erm.aa", 64 );
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	static List<String>	res = new ArrayList<String>();
	public static void fixFile( String fastafile, String blastlist, String outfile ) throws IOException {
		Set<String>	faset = new HashSet<String>();
		FileReader fr = new FileReader( fastafile );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {				
				String[] split = line.split("[\t ]+");
				faset.add( split[0].substring(1) );
				
				int ind = line.lastIndexOf('|');
				String resval;
				if( ind > 0 ) {
					String sub = line.substring( ind );
					resval = line.substring(1, ind+1);
					split = sub.split("_");
					ind = 1;
				} else {
					split = line.split("_");
					ind = 2;
					resval = split[0].substring(1)+"_"+split[1];
				}
				
				int val = 0;
				try {
					val = Integer.parseInt( split[ind] );
				} catch( Exception e ) {
					System.err.println( split[ind] );
				}
				while( val >= res.size() ) res.add( null );
				res.set( val, resval );
			}
			
			line = br.readLine();
  		}
		br.close();
		
		FileWriter	fw = new FileWriter( outfile );
		
		int count = 0;
		int tcount = 0;
		Set<String>	regset = new HashSet<String>();
		String last = "";
		String lastline = "";
		String lastsp = "";
		fr = new FileReader( blastlist );
		br = new BufferedReader( fr );
		line = br.readLine();
		while( line != null ) {
			String[] split = line.split("[\t ]+");
			if( !split[0].equals(last) ) {
				int sind = split[0].lastIndexOf("_");
				String shorter = split[0].substring(0, sind);
				
				sind = last.lastIndexOf("_");
				String lshorter = null;
				if( sind > 0 ) lshorter = last.substring(0, sind);
				
				if( shorter.equals(lshorter) ) {
					if( !split[1].equals(lastsp) ) {
						count++;
						//System.err.println( "erm " + line + "\n    " + lastline + "  " + lastsp );
					}
					tcount++;
				} else {				
				/*if( regset.contains(shorter) ) {
					System.err.println( split[0] + " " + last + "  " + shorter );
				} else regset.add( shorter );(*/
				
					if( split[0].startsWith("_") ) {
						String[] lsp = split[0].split("_");
						int val = Integer.parseInt( lsp[1] );
						//System.err.println( lsp[1] + "  " + res.get( val ) );
						String str = res.get( val ) + split[0];
						for( int i = 1; i < split.length; i++ ) {
							str += "\t"+split[i];
						}
						fw.write( str + "\n" );
						
						regset.add( res.get(val) + shorter );
					} else {
						fw.write( line+"\n" );
						
						regset.add( shorter );
					}
				}
				
				lastsp = split[1];
			}
			
			last = split[0];
			lastline = line;
			
			line = br.readLine();
		}
		fw.close();
		br.close();
		
		System.err.println( count + "  " + tcount );
		
		faset.removeAll( regset );
		for( String s : faset ) {
			System.err.println( s );
		}
		System.err.println( faset.size() );
	}
	
	public static Map<String,Gene> idMapping( String blastfile, String idfile, String outfile, int ind, int secind, boolean getgeneids ) throws IOException {
		Map<String,Gene>	refids = new HashMap<String,Gene>();
		FileReader	fr = new FileReader(blastfile);
		BufferedReader	br = new BufferedReader( fr );
		String	line = br.readLine();
		while( line != null ) {
			if( line.startsWith("ref|") || line.startsWith("sp|") || line.startsWith("pdb|") || 
					line.startsWith("dbj|") || line.startsWith("gb|") || line.startsWith("emb|") || line.startsWith("pir|") || line.startsWith("tpg|") ) {
				String[] split = line.split("\\|");
				refids.put( split[1], null );
			}
			line = br.readLine();
		}
		fr.close();
		
		return idMapping( new FileReader(idfile), outfile, ind, secind, refids, getgeneids );
	}
	
	public static Map<String,Gene> idMapping( Reader rd, String outfile, int ind, int secind, Map<String,Gene> refids, boolean getgeneids ) throws IOException {
		Map<String,Gene>	unimap = new HashMap<String,Gene>();
		Map<String,String>	ref2kegg = new HashMap<String,String>();
		
		PrintStream ps = null;
		if( outfile != null ) {
			ps = new PrintStream(outfile);
			System.setOut( ps );
		}
		
		List<String>	list = new ArrayList<String>();
		boolean	tone = false;
		//FileReader fr = new FileReader(idfile);
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String last = "";
		while( line != null ) {
			String[] split = line.split("\t");
			if( split.length > ind ) {
				if( !split[secind].equals(last) ) {
					if( tone && !getgeneids ) {
						for( String sstr : list ) {
							String[] spl = sstr.split("\t");
							if( sstr.contains("KEGG") ) {
								ref2kegg.put(spl[0], spl[2]);
							}
						}
					}
					
					if( ps != null && tone ) {
						for( String sstr : list ) {
							System.out.println( sstr );
						}
						tone = false;
					}
					list.clear();
				}
				list.add( line );
				
				String refid = split[ind];
				if( refids.containsKey( refid ) ) {
					Gene gene = refids.get( refid );
					if( getgeneids ) {
						gene.genid = split[secind];
						unimap.put( gene.genid, gene );
					} else {
						gene.uniid = split[secind];
						unimap.put( gene.uniid, gene );
					}
					tone = true;
				}
			}
			
			if( split.length > secind ) {
				last = split[secind];
			}
			line = br.readLine();
		}
		
		if( tone && !getgeneids ) {
			for( String sstr : list ) {
				String[] spl = sstr.split("\t");
				if( sstr.contains("KEGG") ) {
					ref2kegg.put(spl[0], spl[2]);
				}
			}
		}
		
		if( ps != null && tone ) {
			for( String sstr : list ) {
				System.out.println( sstr );
			}
			tone = false;
		}
		list.clear();
		
		br.close();
		if( ps != null ) ps.close();
		
		if( !getgeneids ) {
			for( String s : unimap.keySet() ) {
				Gene g = unimap.get(s);
				g.keggid = ref2kegg.get(g.uniid);
				//System.err.println( g.refid + "  " + g.keggid );
			}
		}
		
		return unimap;
	}
	
	public static Map<String,Function> funcMapping( Reader rd, Map<String,Gene>	genids, String outshort ) throws IOException {
		Map<String,Function>	funcmap = new HashMap<String,Function>();
		
		FileWriter fw = null;
		if( outshort != null ) fw = new FileWriter(outshort);
		
		//FileReader fr = new FileReader( gene2go );
		BufferedReader	br = new BufferedReader( rd );
		String line = br.readLine();
		while( line != null ) {
			String[]	split = line.split("\t");
			if( split.length > 1 && genids.containsKey( split[1]) ) {
				Gene gene = genids.get( split[1] );
				if( gene.funcentries == null ) gene.funcentries = new HashSet<String>();
				gene.funcentries.add( split[2] );
				
				if( fw != null ) fw.write( line + "\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		
		if( fw != null ) fw.close();
		
		return funcmap;
	}
	
	public static void funcMappingUni( Reader rd, Map<String,Gene>	uniids, String outfile ) throws IOException {
		FileWriter fw = null;
		if( outfile != null ) fw = new FileWriter( outfile );
		
		//FileReader fr = new FileReader( sp2go );
		BufferedReader	br = new BufferedReader( rd );
		String line = br.readLine();
		while( line != null ) {
			String[]	split = line.split("=");
			if( split.length > 1 && uniids.containsKey( split[0].trim() ) ) {
				Gene gene = uniids.get( split[0].trim() );
				if( gene.funcentries == null ) gene.funcentries = new HashSet<String>();
				for( String erm : split[1].trim().split("[\t ]+") ) {
					gene.funcentries.add( erm );
				}
				if( fw != null ) fw.write( line + "\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		
		if( fw != null ) fw.close();
	}
	
	public static void updateFilter( JTable table, RowFilter filter, JLabel label ) {
		DefaultRowSorter<TableModel, Integer>	rowsorter = (DefaultRowSorter<TableModel,Integer>)table.getRowSorter();
		rowsorter.setRowFilter( filter );
		if( label != null ) label.setText( table.getRowCount() + "/" + table.getSelectedRowCount() );
	}
	
	private static JComponent showGeneTable( final Map<String,Gene> genemap, final List<Gene> genelist, final Map<String,Function> funcmap, final List<Function> funclist, final List<Set<String>> iclusterlist, final List<Set<String>> uclusterlist ) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		JSplitPane	splitpane = new JSplitPane();
		splitpane.setOrientation( JSplitPane.VERTICAL_SPLIT );
		splitpane.setDividerLocation(400);
		JScrollPane	scrollpane = new JScrollPane();
		final JTable		table = new JTable() {
			public String getToolTipText(MouseEvent me) {
				Point p = me.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				if (r >= 0 && r < super.getRowCount()) {
					Object ret = super.getValueAt(r, c);
					if (ret != null) {
						return ret.toString(); // super.getToolTipText( me );
					}
				}
				return "";
			}
		};
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setAutoCreateRowSorter( true );
		scrollpane.setViewportView( table );
		
		final JTextField	textfield = new JTextField();
		JComponent topcomp = new JComponent() {};
		topcomp.setLayout( new BorderLayout() );
		topcomp.add( scrollpane );
		
		final JLabel	label = new JLabel();
		textfield.setPreferredSize( new Dimension(500,25) );
		JComponent topcombo = new JComponent() {};
		topcombo.setLayout( new FlowLayout() );
		topcombo.add( textfield );
		topcombo.add( label );		
		topcomp.add( topcombo, BorderLayout.SOUTH );
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				label.setText( table.getRowCount() + "/" + table.getSelectedRowCount() );
			}
		});
		
		JScrollPane	fscrollpane = new JScrollPane();		
		final JTextField	ftextfield = new JTextField();
		JComponent botcomp = new JComponent() {};
		botcomp.setLayout( new BorderLayout() );
		botcomp.add( fscrollpane );
		
		//JButton sbutt = new JButton("Find conserved terms");
		ftextfield.setPreferredSize( new Dimension(500,25) );
		JComponent	botcombo = new JComponent() {};
		botcombo.setLayout( new FlowLayout() );
		botcombo.add( ftextfield );
		//botcombo.add( sbutt );
		botcomp.add( botcombo, BorderLayout.SOUTH );
		
		splitpane.setBottomComponent( botcomp );
		splitpane.setTopComponent( topcomp );
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return genelist.size();
			}

			@Override
			public int getColumnCount() {
				return 21;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) {
					return "Desc";
				} else if( columnIndex == 1 ) {
					return "Genid";
				} else if( columnIndex == 2 ) {
					return "Refid";
				} else if( columnIndex == 3 ) {
					return "Unid";
				} else if( columnIndex == 4 ) {
					return "Keggid";
				} else if( columnIndex == 5 ) {
					return "Present in";
				} else if( columnIndex == 6 ) {
					return "# of locus";
				} else if( columnIndex == 7 ) {
					return "max length";
				} else if( columnIndex == 8 ) {
					return "T.HB8";
				} else if( columnIndex == 9 ) {
					return "T.HB27";
				} else if( columnIndex == 10 ) {
					return "T.SA01";
				} else if( columnIndex == 11 ) {
					return "T.aqua";
				} else if( columnIndex == 12 ) {
					return "T.eggert";
				} else if( columnIndex == 13 ) {
					return "T.island";
				} else if( columnIndex == 14 ) {
					return "T.antan";
				} else if( columnIndex == 15 ) {
					return "T.scoto346";
				} else if( columnIndex == 16 ) {
					return "T.scoto1572";
				} else if( columnIndex == 17 ) {
					return "T.scoto252";
				} else if( columnIndex == 18 ) {
					return "T.scoto2101";
				} else if( columnIndex == 19 ) {
					return "T.scoto2127";
				} else if( columnIndex == 20 ) {
					return "T.scoto4063";
				}
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 5 || columnIndex == 6 || columnIndex == 7 ) return Integer.class;
				else if( columnIndex >= 8 ) return Tegeval.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Gene gene = genelist.get( rowIndex );
				if( columnIndex == 0 ) {
					return gene.name;
				} else if( columnIndex == 1 ) {
					return gene.genid;
				} else if( columnIndex == 2 ) {
					return gene.refid;
				} else if( columnIndex == 3 ) {
					return gene.uniid;
				} else if( columnIndex == 4 ) {
					return gene.keggid;
				} else if( columnIndex == 5 ) {
					return gene.species == null ? -1 : gene.species.size();
				} else if( columnIndex == 6 ) {
					if( gene.species != null ) {
						int val = 0;
						for( String str : gene.species.keySet() ) {
							val += gene.species.get(str).size();
						}
						return val;
					}
				} else if( columnIndex == 7 ) {
					if( gene.species != null ) {
						int max = 0;
						for( String str : gene.species.keySet() ) {
							Set<Tegeval>	set = gene.species.get(str);
							for( Tegeval tv : set ) {
								max = Math.max(max,tv.seq.length());
							}
						}
						return max;
					}
				} /*else if( columnIndex == 4 ) {
					if( gene.funcentries != null ) {
						String ret = "";
						for( String fe : gene.funcentries ) {
							ret += funcmap.get(fe).name + ", ";
						}
						return ret;
					}
				}*/
				else if( columnIndex == 8 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("ttHB8join");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
				} else if( columnIndex == 9 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("ttHB27join");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("ttHB27join").iterator().next();
				} else if( columnIndex == 10 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("tscotoSA01");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("ttaqua").iterator().next();
				} else if( columnIndex == 11 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("ttaqua");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("ttaqua").iterator().next();
				} else if( columnIndex == 12 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("eggertsoni2789");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("eggertsoni2789").iterator().next();
				} else if( columnIndex == 13 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("islandicus180610");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("islandicus180610").iterator().next();
				} else if( columnIndex == 14 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("antag2120");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("antag2120").iterator().next();
				} else if( columnIndex == 15 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto346");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto346").iterator().next();
				} else if( columnIndex == 16 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto1572");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto1572").iterator().next();
				} else if( columnIndex == 17 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto252");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto252").iterator().next();
				} else if( columnIndex == 18 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto2101");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto2101").iterator().next();
				} else if( columnIndex == 19 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto2127");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto2127").iterator().next();
				} else if( columnIndex == 20 ) {
					if( gene.species != null ) {
						Set<Tegeval> set = gene.species.get("scoto4063");
						if( set != null ) {
							for( Tegeval tv : set ) return tv;
						}
					}
					//return gene.species == null ? null : gene.species.get("scoto4063").iterator().next();
				}
				return columnIndex >= 5 ? null : "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		
		final JTable 		ftable = new JTable() {
			public String getToolTipText(MouseEvent me) {
				Point p = me.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				if (r >= 0 && r < super.getRowCount()) {
					Object ret = super.getValueAt(r, c);
					if (ret != null) {
						return ret.toString(); // super.getToolTipText( me );
					}
				}
				return "";
			}
		};
		
		JPopupMenu	fpopup = new JPopupMenu();
		fpopup.add(new AbstractAction("Amigo lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = ftable.getSelectedRow();
				if( r >= 0 ) {
					String go = (String)ftable.getValueAt(r, 0);
					try {
						Desktop.getDesktop().browse( new URI("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term="+go) );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		fpopup.add(new AbstractAction("KEGG lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = ftable.getSelectedRow();
				if( r >= 0 ) {
					String kegg = (String)ftable.getValueAt(r, 3);
					try {
						Desktop.getDesktop().browse( new URI("http://www.genome.jp/dbget-bin/www_bget?rn:"+kegg) );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		fpopup.add(new AbstractAction("EC lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = ftable.getSelectedRow();
				if( r >= 0 ) {
					String ec = (String)ftable.getValueAt(r, 1);
					try {
						Desktop.getDesktop().browse( new URI("http://www.expasy.ch/cgi-bin/nicezyme.pl?"+ec) );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		ftable.setComponentPopupMenu( fpopup );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add(new AbstractAction("NCBI lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if( r >= 0 ) {
					String ref = (String)table.getValueAt(r, 2);
					try {
						Desktop.getDesktop().browse( new URI("http://www.ncbi.nlm.nih.gov/gene?term="+ref) );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		table.setComponentPopupMenu( popup );
		
		ftable.setAutoCreateRowSorter( true );
		ftable.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return funclist.size();
			}

			@Override
			public int getColumnCount() {
				return 8;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "GO";
				else if( columnIndex == 1 ) return "EC";
				else if( columnIndex == 2 ) return "MetaCyc";
				else if( columnIndex == 3 ) return "KEGG";
				else if( columnIndex == 4 ) return "Number of proteins";
				else if( columnIndex == 5 ) return "Name";
				else if( columnIndex == 6 ) return "Namespace";
				else if( columnIndex == 7 ) return "Def";
				return "";
			}

			@Override
			public Class<?> getColumnClass( int columnIndex ) {
				if( columnIndex == 4 ) return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Function func = funclist.get( rowIndex );
				if( columnIndex == 0 ) return func.go;
				else if( columnIndex == 1 ) return func.ec;
				else if( columnIndex == 2 ) return func.metacyc;
				else if( columnIndex == 3 ) return func.kegg;
				else if( columnIndex == 4 ) return func.geneentries == null ? 0 : func.geneentries.size();
				else if( columnIndex == 5 ) return func.name;
				else if( columnIndex == 6 ) return func.namespace;
				else if( columnIndex == 7 ) return func.desc;
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		fscrollpane.setViewportView( ftable );
		
		final Set<Integer>	filterset = new HashSet<Integer>();
		final Set<Integer>	genefilterset = new HashSet<Integer>();
		
		final RowFilter	filter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return filterset.isEmpty() || filterset.contains( entry.getIdentifier() );
			}
		};
		updateFilter( ftable, filter, null );
		
		final RowFilter	genefilter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return genefilterset.isEmpty() || genefilterset.contains( entry.getIdentifier() );
			}
		};
		updateFilter( table, genefilter, label );
		
		fpopup.add( new AbstractAction("Find conserved terms") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Integer>	res = new HashSet<Integer>();
				for( Function f : funclist ) {
					if( f.geneentries != null ) {
						Set<String>	check = new HashSet<String>();
						for( String str : f.geneentries ) {
							Gene g = genemap.get(str);
							if( g.species != null ) {
								if( check.isEmpty() ) check.addAll( g.species.keySet() );
								else if( !(check.size() == g.species.size() && check.containsAll( g.species.keySet() )) ) {
									check.clear();
									break;
								}
							}
						}
						if( !check.isEmpty() ) res.add(f.index);
					}
				}
				filterset.clear();
				for( int i : res ) {
					filterset.add(i);
				}
				updateFilter( ftable, filter, null );
			}
		});
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//table.clearSelection();
				tableisselecting = true;
				if( !ftableisselecting && filterset.isEmpty() ) {
					ftable.removeRowSelectionInterval(0, ftable.getRowCount()-1);
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int cr = table.convertRowIndexToModel( r );
						Gene g = genelist.get( cr );
						if( g.funcentries != null ) {
							for( String go : g.funcentries ) {
								Function f = funcmap.get( go );
								int rf = ftable.convertRowIndexToView( f.index );
								ftable.addRowSelectionInterval(rf, rf);
							}
						}
					}
				}
				tableisselecting = false;
			}
		});
		
		ftable.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent ke ) {
				if( ke.getKeyCode() == KeyEvent.VK_ESCAPE ) {
					filterset.clear();
					updateFilter( ftable, filter, null );
				}
			}
		});
		
		table.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent ke ) {
				if( ke.getKeyCode() == KeyEvent.VK_ESCAPE ) {
					genefilterset.clear();
					updateFilter( table, genefilter, label );
				}
			}
		});
		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				tableisselecting = true;
				if( !ftableisselecting && e.getClickCount() == 2 ) {
					/*int[] rr = ftable.getSelectedRows();
					int minr = ftable.getRowCount();
					int maxr = 0;
					for( int r : rr ) {
						if( r < minr ) minr = r;
						if( r > maxr ) maxr = r;
					}
					ftable.removeRowSelectionInterval(minr, maxr);*/
					//ftable.removeRowSelectionInterval(0, filterset.isEmpty() ? ftable.getRowCount()-1 : filterset.size()-1 );
					filterset.clear();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int cr = table.convertRowIndexToModel( r );
						Gene g = genelist.get( cr );
						if( g.funcentries != null ) {
							for( String go : g.funcentries ) {
								Function f = funcmap.get( go );
								//ftable.getRowSorter().convertRowIndexToView(index)
								//int rf = ftable.convertRowIndexToView( f.index );
								filterset.add( f.index );
								//ftable.addRowSelectionInterval(rf, rf);
							}
						}
					}
					updateFilter( ftable, filter, null );
					//ftable.sorterChanged( new RowSorterEvent( ftable.getRowSorter() ) );
					//ftable.tableChanged( new TableModelEvent( ftable.getModel() ) );
				}
				tableisselecting = false;
			}
		});
		
		ftable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				ftableisselecting = true;
				if( !tableisselecting && e.getClickCount() == 2 ) {
					genefilterset.clear();
					int[] rr = ftable.getSelectedRows();
					for( int r : rr ) {
						int cr = ftable.convertRowIndexToModel( r );
						
						Function f = funclist.get( cr );
						if( f.geneentries != null ) {
							for( String ref : f.geneentries ) {
								Gene g = genemap.get( ref );
								//int rf = table.convertRowIndexToView( g.index );
								//table.addRowSelectionInterval(rf, rf);
								genefilterset.add( g.index );
							}
						}
					}
					updateFilter( table, genefilter, label );
					//ftable.sorterChanged( new RowSorterEvent( ftable.getRowSorter() ) );
					//ftable.tableChanged( new TableModelEvent( ftable.getModel() ) );
				}
				ftableisselecting = false;
			}
		});
		
		ftable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//ftable.clearSelection();
				ftableisselecting = true;
				if( !tableisselecting && genefilterset.isEmpty() ) {
					table.removeRowSelectionInterval(0, table.getRowCount()-1);
					int[] rr = ftable.getSelectedRows();
					for( int r : rr ) {
						int cr = ftable.convertRowIndexToModel( r );
						Function f = funclist.get( cr );
						if( f.geneentries != null ) {
							for( String ref : f.geneentries ) {
								Gene g = genemap.get( ref );
								int rf = table.convertRowIndexToView( g.index );
								table.addRowSelectionInterval(rf, rf);
							}
						}
					}
				}
				ftableisselecting = false;
			}
		});
		
		textfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateFilter(0, textfield.getText(), table, genefilter, genefilterset, 0, label );
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, textfield.getText(), table, genefilter, genefilterset, 0, label );
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, textfield.getText(), table, genefilter, genefilterset, 0, label );
			}
		});
		
		ftextfield.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				updateFilter(0, ftextfield.getText(), ftable, filter, filterset, 5, null );
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, ftextfield.getText(), ftable, filter, filterset, 5, null );
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, ftextfield.getText(), ftable, filter, filterset, 5, null );
			}
		});
		
		popup.add(new AbstractAction("Show genes with same sharing") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int r = table.getSelectedRow();
				if( r >= 0 ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					for( Gene g : genelist ) {
						if( gg.species != null && g.species != null ) {
							Set<String> ggset = gg.species.keySet();
							Set<String> gset = g.species.keySet();
							
							if( gset.size() == ggset.size() && gset.containsAll( ggset ) ) genefilterset.add( g.index ); 
						}
					}
					updateFilter( table, genefilter, label );
				}
			}
		});
		popup.add(new AbstractAction("Show shared function") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String>	startfunc = null;
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if( gg.funcentries != null ) {
						if( startfunc == null )	startfunc = new HashSet<String>( gg.funcentries );
						else {
							startfunc.retainAll( gg.funcentries );
						}
					}
				}
				for( String s : startfunc ) {
					filterset.add( funcmap.get(s).index );
				}
				updateFilter( ftable, filter, null );
			}
		});
		popup.add(new AbstractAction("Show all functions") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String>	startfunc = null;
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if( gg.funcentries != null ) {
						if( startfunc == null )	startfunc = new HashSet<String>( gg.funcentries );
						else {
							startfunc.addAll( gg.funcentries );
						}
					}
				}
				for( String s : startfunc ) {
					filterset.add( funcmap.get(s).index );
				}
				updateFilter( ftable, filter, null );
			}
		});
		popup.add(new AbstractAction("Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea textarea = new JTextArea();
				JScrollPane	scrollpane = new JScrollPane( textarea );
				
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if( gg.species != null ) {
						textarea.append( gg.name + ":\n" );
						for( String sp : gg.species.keySet() ) {
							Set<Tegeval> stv = gg.species.get( sp );
							for( Tegeval tv : stv ) {
								textarea.append( ">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n" + tv.seq + "\n" );
							}
						}
					}
				}			
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible( true );
			}
		});
		popup.add(new AbstractAction("Show genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String>	ct = new HashSet<String>();
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					//genefilterset.add( gg.index );
					if( gg.species != null ) {
						for( String sp : gg.species.keySet() ) {
							Set<Tegeval> stv = gg.species.get( sp );
							for( Tegeval tv : stv ) {
								ct.add( tv.cont );
								int ind = tv.cont.lastIndexOf("_");
								int val = Integer.parseInt( tv.cont.substring(ind+1) );
								
								String next = tv.cont.substring(0, ind+1)+(val+1);
								System.err.println( next );
								ct.add( next );
								if( val > 1 ) {
									String prev = tv.cont.substring(0, ind+1)+(val-1);
									ct.add( prev );
								}
							}
						}
					}
				}
				
				for( Gene g : genelist ) {
					if( g.species != null ) {
						for( String sp : g.species.keySet() ) {
							Set<Tegeval> stv = g.species.get( sp );
							for( Tegeval tv : stv ) {
								if( ct.contains(tv.cont) ) {
									genefilterset.add( g.index );
									break;
								}
							}
						}
					}
				}
				
				updateFilter( table, genefilter, label );
			}
		});
		popup.add(new AbstractAction("Show related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String>	ct = new HashSet<String>();
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					//genefilterset.add( gg.index );
					if( gg.species != null ) {
						for( String sp : gg.species.keySet() ) {
							Set<Tegeval> stv = gg.species.get( sp );
							for( Tegeval tv : stv ) {
								for( Set<String> uset : uclusterlist ) {
									if( uset.contains(tv.cont) ) {
										ct.addAll( uset );
										break;
									}
								}
							}
						}
					}
				}
				
				for( Gene g : genelist ) {
					if( g.species != null ) {
						for( String sp : g.species.keySet() ) {
							Set<Tegeval> stv = g.species.get( sp );
							for( Tegeval tv : stv ) {
								if( ct.contains(tv.cont) ) {
									genefilterset.add( g.index );
									break;
								}
							}
						}
					}
				}
				
				updateFilter( table, genefilter, label );
			}
		});
		popup.add(new AbstractAction("Show closely related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String>	ct = new HashSet<String>();
				for( int r : rr ) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					//genefilterset.add( gg.index );
					if( gg.species != null ) {
						for( String sp : gg.species.keySet() ) {
							Set<Tegeval> stv = gg.species.get( sp );
							for( Tegeval tv : stv ) {
								for( Set<String> uset : iclusterlist ) {
									if( uset.contains(tv.cont) ) {
										ct.addAll( uset );
										break;
									}
								}
							}
						}
					}
				}
				
				for( Gene g : genelist ) {
					if( g.species != null ) {
						for( String sp : g.species.keySet() ) {
							Set<Tegeval> stv = g.species.get( sp );
							for( Tegeval tv : stv ) {
								if( ct.contains(tv.cont) ) {
									genefilterset.add( g.index );
									break;
								}
							}
						}
					}
				}
				
				updateFilter( table, genefilter, label );
			}
		});
		
		return splitpane;
	}
	
	public static void updateFilter( int val, String str, JTable table, RowFilter filter, Set<Integer> filterset, int ind, JLabel label ) {
		filterset.clear();
		String ustr = str.toLowerCase();
		TableModel model = table.getModel();
		for( int r = 0; r < model.getRowCount(); r++ ) {
			String s = model.getValueAt(r, ind).toString().toLowerCase();
			if( s != null && s.contains( ustr) ) filterset.add(r);
		}
		updateFilter( table, filter, label );
	}
	
	static boolean ftableisselecting = false;
	static boolean tableisselecting = false;
	
	private static Map<String,Function> readGoInfo( Reader rd, Map<String,Set<String>> gofilter, String outfile ) throws IOException {
		Map<String,Function>	retmap = new HashMap<String,Function>();
		
		FileWriter fw = null;
		if( outfile != null ) fw = new FileWriter( outfile );
		
		//FileReader		fr = new FileReader( obo );
		BufferedReader	br = new BufferedReader( rd );
		
		boolean on = false;
		Function f = null;
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith("[Term]") ) {
				on = false;
				if( f != null && gofilter.containsKey(f.go) ) {
					if( f.geneentries == null ) f.geneentries = new HashSet<String>();
					f.geneentries.addAll( gofilter.get(f.go) );
					retmap.put(f.go, f);
				}
				f = new Function();
			} else if( line.startsWith("id:") ) {
				f.go = line.substring( 4 ); //line.indexOf("GO:")+3 );
				
				if( fw != null && gofilter.containsKey(f.go) ) {
					fw.write("[Term]\n");
					on = true;
				}
			} else if( line.startsWith("name:") ) {
				f.name = line.substring(6);
			} else if( line.startsWith("namespace:") ) {
				f.namespace = line.substring(11);
			} else if( line.startsWith("def:") ) {
				f.desc = line.substring(5);
			} else if( line.startsWith("xref:") ) {
				if( line.contains("EC:") ) {
					f.ec = line.substring( line.indexOf("EC:")+3 );
				} else if( line.contains("MetaCyc:") ) {
					f.metacyc = line.substring( line.indexOf("MetaCyc:")+8 );
				} else if( line.contains("KEGG:") ) {
					f.kegg = line.substring( line.indexOf("KEGG:")+5 );
				}
			}
			
			if( on ) fw.write( line+"\n" );
			
			line = br.readLine();
		}
		if( f != null && gofilter.containsKey(f.go) ) {
			if( f.geneentries == null ) f.geneentries = new HashSet<String>();
			f.geneentries.addAll( gofilter.get(f.go) );
			retmap.put(f.go, f);
		}
		br.close();
		
		if( fw != null ) fw.close();
		
		return retmap;
	}
	
	private static JComponent newSoft() throws IOException {
		InputStream is = GeneSet.class.getResourceAsStream("/allnew.aa");
		loci2aasequence( new InputStreamReader( is ) );
		
		is = GeneSet.class.getResourceAsStream("/intersect_cluster_new.txt");
		List<Set<String>>	iclusterlist = loadSimpleClusters( new InputStreamReader(is) );
		
		is = GeneSet.class.getResourceAsStream("/union_cluster_new.txt");
		List<Set<String>>	uclusterlist = loadSimpleClusters( new InputStreamReader(is) );
		
		Map<String,Gene>	descmap = new HashMap<String,Gene>();
		Map<String,String>		allgenes = new HashMap<String,String>();
		Map<String,Set<String>>	geneset = new HashMap<String,Set<String>>();
		Map<String,Set<String>>	geneloc = new HashMap<String,Set<String>>();
		Set<String>				poddur = new HashSet<String>();
		//panCoreFromNRBlast( new FileReader("/home/sigmar/blastout/nr.blastout"), "/home/sigmar/workspace/distann/src/nr_short.blastout", descmap, allgenes, geneset, geneloc, poddur );
		is = GeneSet.class.getResourceAsStream("/nr_short.blastout");
		panCoreFromNRBlast( new InputStreamReader(is), null, descmap, allgenes, geneset, geneloc, poddur );
		Map<String,Gene>	refmap = new TreeMap<String,Gene>();
		List<Gene>			genelist = new ArrayList<Gene>();
		for( String genedesc : descmap.keySet() ) {
			Gene gene = descmap.get( genedesc );
			refmap.put(gene.refid, gene);
			gene.index = genelist.size();
			genelist.add( gene );
		}
		//genemap = idMapping( "/home/sigmar/blastout/nilli.blastout", "/mnt/tmp/gene2refseq.txt", "/home/sigmar/idmapping_short2.dat", 5, 1, genemap );
		//genemap = idMapping( "/home/sigmar/blastout/nilli.blastout", "/home/sigmar/thermus/newthermus/idmapping.dat", "/home/sigmar/idmapping_short.dat", 2, 0, genemap );
		
		/*Map<String,Gene> unimap = idMapping( "/home/sigmar/idmap.dat", "/home/sigmar/workspace/distann/idmapping_short.dat", 2, 0, refmap, false );
		Map<String,Gene> genmap = idMapping( "/mnt/tmp/gene2refseq.txt", "/home/sigmar/workspace/distann/gene2refseq_short.txt", 5, 1, refmap, true );
		funcMapping( "/home/sigmar/asgard-bio/data/gene2go", genmap, "/home/sigmar/workspace/distann/gene2go_short.txt" );
		funcMappingUni( "/home/sigmar/asgard-bio/data/sp2go.txt", unimap, "/home/sigmar/workspace/distann/sp2go_short.txt" );*/
		
		is = GeneSet.class.getResourceAsStream("/idmapping_short.dat");
		Map<String,Gene> unimap = idMapping( new InputStreamReader(is), null, 2, 0, refmap, false );
		is = GeneSet.class.getResourceAsStream("/gene2refseq_short.txt");
		Map<String,Gene> genmap = idMapping( new InputStreamReader(is), null, 5, 1, refmap, true );
		is = GeneSet.class.getResourceAsStream("/gene2go_short.txt");
		funcMapping( new InputStreamReader(is), genmap, null );
		is = GeneSet.class.getResourceAsStream("/sp2go_short.txt");
		funcMappingUni( new InputStreamReader(is), unimap, null );
		
		Map<String,Set<String>>	totalgo = new HashMap<String,Set<String>>();
		for( Gene g : genelist ) {
			if( g.funcentries != null ) {
				for( String f : g.funcentries ) {
					Set<String>	set;
					if( totalgo.containsKey( f ) ) {
						set = totalgo.get(f);
					} else {
						set = new HashSet<String>();
						totalgo.put( f,  set );
					}
					set.add( g.refid );
				}
			}
		}
		//Map<String,Function>	funcmap = readGoInfo( new FileReader("/home/sigmar/asgard-bio/data/gene_ontology_ext.obo"), totalgo, "/home/sigmar/workspace/distann/go_short.obo");
		is = GeneSet.class.getResourceAsStream("/go_short.obo");
		Map<String,Function>	funcmap = readGoInfo( new InputStreamReader(is), totalgo, null);
		List<Function>	funclist = new ArrayList<Function>();
		for( String go : funcmap.keySet() ) {
			Function f = funcmap.get(go);
			f.index = funclist.size();
			funclist.add( f );
		}
		return showGeneTable( refmap, genelist, funcmap, funclist, iclusterlist, uclusterlist );
	}
	
	public void init() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}
		
		try {
			this.add( newSoft() );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void blastAlign( Reader r, String main, String second ) {
		//BufferedReader	br = new BufferedReader();
	}
	
	private static void init( String[] args ) {
		String[]	stuff = {"scoto346","scoto2101","antag2120","scoto2127","scoto252","scoto1572","scoto4063","eggertsoni2789","islandicus180610","tscotoSA01","ttHB27join","ttHB8join","ttaqua"};
		String[]	stuff2 = {"aa1","aa2","aa4","aa6","aa7","aa8"};
		String[]	names = {"aa1.out","aa2.out","aa4.out","aa6.out","aa7.out","aa8.out"};
		String[]	all = {"all.aa"};
		String[]	name = {"all.blastout"};
		String[]	nrblastres = {"nilli.blastout"};
		File 		dir = new File("/home/sigmar/blastout/");
		File 		dir2 = new File("/home/sigmar/thermus/newthermus/");
		
		swapmap.put("aa1.out", "scoto346");
		swapmap.put("aa2.out", "scoto2101");
		swapmap.put("aa4.out", "scoto2127");
		swapmap.put("aa6.out", "scoto252");
		swapmap.put("aa7.out", "scoto1572");
		swapmap.put("aa8.out", "scoto4063");
		
		try {
			//idMapping( "/home/sigmar/blastout/nilli.blastout", "/home/sigmar/thermus/newthermus/idmapping.dat", "/home/sigmar/idmapping_short.dat", 2, 0 );
			
			/*JFrame	frame = new JFrame();
			frame.setSize( 800, 600 );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			JComponent comp = newSoft();
			frame.add( comp );
			frame.setVisible( true );*/		
			
			//panCoreFromNRBlast( nrblastres, dir );
			//printnohits( stuff, dir, dir2 );
			//createConcatFsa( stuff, dir2 );
			
			//fixFile( "/home/sigmar/thermus/newthermus/all.aa", "/home/sigmar/thermus/newthermus/blastres_all.aa", "/home/sigmar/ermm.txt" );
			
			//loci2aasequence( all, dir2 );
			//loci2gene( nrblastres, dir );
			//clusterFromBlastResults( new File("/home/sigmar/thermus/newthermus/"), new String[] {"allnew.blastout"}, "/home/sigmar/union_cluster.txt", "/home/sigmar/simblastcluster.txt", true);
			clusterFromBlastResults( new File("/home/sigmar/thermus/newthermus/"), new String[] {"allnew.blastout"}, "/home/sigmar/intersect_cluster_new.txt", "/home/sigmar/simblastcluster.txt", false);
			
			//blastAlign( new FileReader("/home/sigmar/thermus/newthermus/all.aa"), "tscotoSA01", "scoto346" );
			
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
