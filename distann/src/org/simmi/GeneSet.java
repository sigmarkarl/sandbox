package org.simmi;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.jnlp.ClipboardService;
import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.Sequence;
import org.simmi.shared.Sequence.Annotation;
import org.simmi.shared.Serifier;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.unsigned.JavaFasta;

public class GeneSet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 * @throws IOException
	 */

	static Map<Character, Character> sidechainpolarity = new HashMap<Character, Character>();
	static Map<Character, Integer> sidechaincharge = new HashMap<Character, Integer>();
	static Map<Character, Double> hydropathyindex = new HashMap<Character, Double>();
	static Map<Character, Double> aamass = new HashMap<Character, Double>();
	static Map<Character, Double> isoelectricpoint = new HashMap<Character, Double>();

	static Color[] colorCodes = new Color[9];
	
	static SerifyApplet currentSerify = null;

	// abundance
	// aliphatic - aromatic
	// size
	// sortcoeff

	static class StrSort implements Comparable<StrSort> {
		double d;
		String s;

		StrSort(double d, String s) {
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
		double d;
		char c;

		Erm(double d, char c) {
			this.d = d;
			this.c = c;
		}

		@Override
		public int compareTo(Erm o) {
			double mis = d - o.d;

			return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
		}
	};

	static List<Erm> uff = new ArrayList<Erm>();
	static List<Erm> uff2 = new ArrayList<Erm>();
	static List<Erm> uff3 = new ArrayList<Erm>();
	static List<Erm> mass = new ArrayList<Erm>();
	static List<Erm> isoel = new ArrayList<Erm>();

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

		aamass.put('A', 89.09404);
		aamass.put('C', 121.15404);
		aamass.put('D', 133.10384);
		aamass.put('E', 147.13074);
		aamass.put('F', 165.19184);
		aamass.put('G', 75.06714);
		aamass.put('H', 155.15634);
		aamass.put('I', 131.17464);
		aamass.put('K', 146.18934);
		aamass.put('L', 131.17464);
		aamass.put('M', 149.20784);
		aamass.put('N', 132.11904);
		aamass.put('O', 100.0);
		aamass.put('P', 115.13194);
		aamass.put('Q', 146.14594);
		aamass.put('R', 174.20274);
		aamass.put('S', 105.09344);
		aamass.put('T', 119.12034);
		aamass.put('U', 168.053);
		aamass.put('V', 117.14784);
		aamass.put('W', 204.22844);
		aamass.put('Y', 181.19124);

		isoelectricpoint.put('A', 6.01);
		isoelectricpoint.put('C', 5.05);
		isoelectricpoint.put('D', 2.85);
		isoelectricpoint.put('E', 3.15);
		isoelectricpoint.put('F', 5.49);
		isoelectricpoint.put('G', 6.06);
		isoelectricpoint.put('H', 7.6);
		isoelectricpoint.put('I', 6.05);
		isoelectricpoint.put('K', 9.6);
		isoelectricpoint.put('L', 6.01);
		isoelectricpoint.put('M', 5.74);
		isoelectricpoint.put('N', 5.41);
		isoelectricpoint.put('O', 21.0);
		isoelectricpoint.put('P', 6.3);
		isoelectricpoint.put('Q', 5.65);
		isoelectricpoint.put('R', 10.76);
		isoelectricpoint.put('S', 5.68);
		isoelectricpoint.put('T', 5.6);
		isoelectricpoint.put('U', 20.0);
		isoelectricpoint.put('V', 6.0);
		isoelectricpoint.put('W', 5.89);
		isoelectricpoint.put('Y', 5.64);

		for (char c : hydropathyindex.keySet()) {
			double d = hydropathyindex.get(c);
			uff.add(new Erm(d, c));
		}
		Collections.sort(uff);

		for (char c : sidechainpolarity.keySet()) {
			double d = sidechainpolarity.get(c);
			uff2.add(new Erm(d, c));
		}
		Collections.sort(uff2);

		for (char c : sidechaincharge.keySet()) {
			double d = sidechaincharge.get(c);
			uff3.add(new Erm(d, c));
		}
		Collections.sort(uff3);

		for (char c : aamass.keySet()) {
			double d = aamass.get(c);
			mass.add(new Erm(d, c));
		}
		Collections.sort(mass);

		for (char c : isoelectricpoint.keySet()) {
			double d = isoelectricpoint.get(c);
			isoel.add(new Erm(d, c));
		}
		Collections.sort(isoel);
	}

	private static StringBuilder aaSearch(String query) {
		/*
		 * aquery.name = query; int ind = Arrays.binarySearch(aas, aquery); if(
		 * ind < 0 ) { System.err.println(); } return ind < 0 ? null : aas[ ind
		 * ].aas;
		 */

		Aas aass = aas.get(query);
		return aass == null ? null : aass.aas;
	}

	private static StringBuilder dnaSearch(String query) {
		/*
		 * aquery.name = query; int ind = Arrays.binarySearch(aas, aquery); if(
		 * ind < 0 ) { System.err.println(); } return ind < 0 ? null : aas[ ind
		 * ].aas;
		 */

		if (dnaa.containsKey(query))
			return dnaa.get(query);
		return null;
	}
	
	private static Gene genestuff( List<Set<String>> uclusterlist, String query, String desc, String teg, String val, Map<String,Gene> ret ) {
		Gene gene = null;
		
		if( ret.containsKey(val) ) {
			gene = ret.get(val);
			//int i = 0;
			//System.err.println( uclusterlist.size() );
			for( Set<String>	clusterset : uclusterlist ) {
				//System.err.println( i++ );
				/*boolean mugg = false;
				if( clusterset.size() == 4 ) {
					int count = 0;
					for( String s : clusterset ) {
						if( s.contains("t.oshimai") || s.contains("mt.silvanus") ) count++;
					}
					if( count >= 2 ) {
						System.err.println( clusterset );
						mugg = true;
					}
				}*/
				if( clusterset.contains( query ) ) {
					for( String padd : gene.species.keySet() ) {
						Teginfo stv = gene.species.get(padd);
						for( Tegeval tvl : stv.tset ) {
							if( !clusterset.contains( tvl.cont ) ) {
								Gene newgene = genestuff( uclusterlist, query, desc, teg, val+" new", ret );
								
								if( newgene == null ) {
									newgene = new Gene(null, desc, teg);
									newgene.allids = new HashSet<String>();
									newgene.species = new HashMap<String, Teginfo>();
									newgene.refid = gene.refid + " new";
									ret.put( newgene.refid, newgene );
								}
								
								return newgene;
								
								//break;
							}
						}
						
						//if( newgene != gene ) break;
					}
					//newgene = genestuff( gene, clusterset, desc, teg, id, ret );
				}
			}
		}
		
		return gene;
	}

	private static void panCoreFromNRBlast(Reader rd, Reader rd2, String outfile, String outfile2, Map<String, Gene> ret, Map<String, String> allgenes, Map<String, Set<String>> geneset, 
			Map<String, Set<String>> geneloc, Map<String, Gene> locgene, Set<String> poddur, List<Set<String>> uclusterlist ) 
			throws IOException {
		FileWriter fw = null;
		if (outfile != null)
			fw = new FileWriter(outfile);

		/*
		 * Set<String> extra = new HashSet<String>();
		 * 
		 * extra.add("protein of unknown function DUF820");
		 * extra.add("ABC transporter ATP-binding protein");
		 * extra.add("short-chain dehydrogenase/reductase SDR");
		 * extra.add("two component transcriptional regulator, winged helix family"
		 * ); extra.add("Tetratricopeptide TPR_2 repeat protein");
		 * extra.add("extracellular solute-binding protein family 1");
		 * extra.add("histidine kinase"); extra.add("cytochrome c class I");
		 * extra.add("NUDIX hydrolase");
		 * extra.add("GCN5-related N-acetyltransferase");
		 * extra.add("acyl-CoA dehydrogenase domain protein");
		 * extra.add("protein of unknown function DUF6 transmembrane");
		 * extra.add("response regulator receiver protein");
		 * extra.add("Peptidase M23"); extra.add("glycosyltransferase");
		 * extra.add("metallophosphoesterase");
		 * extra.add("ABC transporter permease protein");
		 * extra.add("UspA domain protein");
		 * extra.add("transposase, IS605 OrfB family");
		 * extra.add("methyltransferase");
		 * extra.add("aminotransferase class I and II");
		 * extra.add("PilT protein domain protein");
		 * extra.add("hypothetical protein");
		 * extra.add("nucleotidyltransferase");
		 * extra.add("serine/threonine protein kinase");
		 * extra.add("transcriptional regulator");
		 * extra.add("ABC-2 type transporter");
		 * extra.add("AMP-dependent synthetase and ligase");
		 * extra.add("DNA polymerase beta domain protein region");
		 * extra.add("ABC transporter-like protein");
		 * extra.add("Radical SAM domain protein");
		 * extra.add("Extracellular ligand-binding receptor");
		 * extra.add("HAD-superfamily hydrolase, subfamily IA, variant 3");
		 * extra.add("metal dependent hydrolase");
		 * extra.add("sugar ABC transporter, permease protein");
		 * extra.add("acetyl-CoA acetyltransferase");
		 * extra.add("LmbE family protein");
		 * extra.add("transcriptional repressor");
		 * extra.add("transposase IS605 OrfB family");
		 * extra.add("beta-lactamase domain protein");
		 * extra.add("dehydrogenase"); extra.add("enoyl-CoA hydratase");
		 * extra.add("metal dependent phosphohydrolase");
		 * extra.add("two-component response regulator");
		 * extra.add("ABC transporter, permease protein"); extra.add(
		 * "alkyl hydroperoxide reductase/ Thiol specific antioxidant/ Mal");
		 * extra.add("SMC domain protein");
		 * extra.add("protein of unknown function DUF88");
		 * extra.add("Roadblock/LC7 family protein");
		 * extra.add("TetR family transcriptional regulator");
		 * extra.add("translation initiation factor IF-2");
		 * extra.add("glycosyl transferase family 2");
		 * extra.add("Integrase catalytic region");
		 * extra.add("putative lipoprotein");
		 * extra.add("tetratricopeptide repeat domain-containing protein");
		 * extra.add("ABC transporter permease");
		 * extra.add("conserved domain-containing protein");
		 * extra.add("MutT/nudix family protein");
		 * extra.add("sensor histidine kinase");
		 * extra.add("CBS domain-containing protein");
		 * extra.add("transporter, major facilitator family");
		 * extra.add("acyl-CoA dehydrogenase"); extra.add("acetyltransferase");
		 * extra.add("universal stress protein family");
		 * extra.add("putative hydrolase");
		 * extra.add("sugar ABC transporter permease");
		 * extra.add("ATP-dependent protease La"); extra.add("permease");
		 * extra.add("metallo-beta-lactamase family protein");
		 * extra.add("immunogenic protein"); extra.add(
		 * "leucine-, isoleucine-, valine-, threonine-, and alanine-binding");
		 * extra.add("branched-chain amino acid ABC transporter permease");
		 * extra.add("membrane protein"); extra.add("serine protease");
		 * extra.add
		 * ("oxidoreductase, short-chain dehydrogenase/reductase family");
		 * extra.
		 * add("branched-chain amino acid ABC transporter ATP-binding protein");
		 * extra.add("cytochrome c-552");
		 * extra.add("hypothetical protein TaqDRAFT_4901");
		 * extra.add("putative PIN domain-containing protein");
		 * extra.add("ATPase"); extra.add("long-chain-fatty-acid--CoA ligase");
		 * extra.add("thioredoxin"); extra.add("aspartyl-tRNA synthetase");
		 * extra.add("thioredoxin reductase");
		 * extra.add("ggdef domain-containing protein");
		 * extra.add("transposase BAC55317.2");
		 * extra.add("integrase, catalytic region");
		 * extra.add("ribose-phosphate pyrophosphokinase");
		 * extra.add("N-acetyl-gamma-glutamyl-phosphate reductase");
		 * extra.add("putative oxidoreductase");
		 * extra.add("glycolate oxidase subunit GlcE");
		 * extra.add("GntR family transcriptional regulator");
		 * extra.add("3-oxoacyl-[acyl-carrier-protein] reductase");
		 * extra.add("PilT protein domain-containing protein");
		 * extra.add("acetylglutamate kinase");
		 * extra.add("spermidine/putrescine import ATP-binding protein PotA");
		 */

		// uclusterlist with avg identity
		BufferedReader br = new BufferedReader(rd);
		parseBlast( br, fw, ret, allgenes, geneset, geneloc, locgene, poddur, uclusterlist, false, false );
		br.close();
		if (fw != null) {
			fw.close();
		}
		
		fw = null;
		if (outfile2 != null)
			fw = new FileWriter(outfile2);

		if( rd2 != null ) {
			BufferedReader br2 = new BufferedReader(rd2);
			//String line = br.readLine();
			//System.err.println( line );
			parseBlast( br2, fw, ret, allgenes, geneset, geneloc, locgene, poddur, uclusterlist, true, true );
			br2.close();
			if (fw != null) {
				fw.close();
			}
		}
	}
	
	public static void parseBlast( BufferedReader br, FileWriter fw, Map<String, Gene> ret, Map<String, String> allgenes, Map<String, Set<String>> geneset, 
			Map<String, Set<String>> geneloc, Map<String, Gene> locgene, Set<String> poddur, List<Set<String>> uclusterlist, boolean old, boolean addon ) throws IOException {
		String query = null;
		int start = 0;
		int stop = 0;
		int ori = 0;
		String evalue = null;
		String line = br.readLine();
		int cnt = 0;
		Tegeval preval = null;
		while (line != null) {
			String trim = line.trim();
			cnt++;
			//if (query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg"))) {
			if (query != null && trim.startsWith(">") ) {
				//String[] split = trim.split("\\|");

				/*line = br.readLine();
				while( !line.startsWith("Length=") && !line.startsWith("Query=") ) {
					trim += " "+line.trim();
					line = br.readLine();
				}*/
				
				int i = trim.indexOf(".aa", 2);
				int aaa = 4;
				if( i == -1 ) {
					i = 2;
					aaa = 0;
				}
				int i2 = trim.indexOf(' ', i);
				String id;
				//boolean addon = i == -1;
				if( addon ) {
					int si = trim.indexOf('|');
					int ei = trim.lastIndexOf('|'); //, i2);
					id = trim.substring(si+1,ei);
				} else {
					id = trim.substring(i+aaa,i2).trim();
				}
				//String id = i == -1 ? trim.substring(1,i2).trim() : trim.substring(i+4,i2).trim();
				String desc = trim.substring(i2+1);
				//String id = split[1];
				//String desc = split[2];
				String teg = "";

				int idx = desc.lastIndexOf('[');
				int idx2 = desc.indexOf(']', idx);
				String newline = "";
				if (idx > idx2 || idx == -1) {
					newline = br.readLine();
					if (!newline.startsWith("Query=")) {
						line = line + newline;
						trim = line.trim();

						i = trim.indexOf(' ', 2);
						id = trim.substring(2,i);
						desc = trim.substring(i+1);
						/*split = trim.split("\\|");

						id = split[1];
						desc = split[2];*/

						idx = desc.lastIndexOf('[');
					}
				}

				if (idx > 0) {
					teg = desc.substring(idx);
					desc = desc.substring(0, idx - 1).trim();
				} else {
					desc = desc.trim();
				}

				/*
				 * if( extra.contains(desc) ||
				 * desc.equals("conserved hypothetical protein") ||
				 * desc.equals("transposase") || desc.equals("hydrolase") ||
				 * desc.equals("ABC transporter related") ||
				 * desc.equals("transporter") ||
				 * desc.equals("putative cytoplasmic protein") || desc.equals(
				 * "binding-protein-dependent transport systems inner membrane component"
				 * ) || desc.equals("major facilitator superfamily MFS_1") ||
				 * desc.equals("transposase IS4 family protein") ||
				 * desc.equals("inner-membrane translocator") ) { desc = desc +
				 * " " + id; }
				 */

				/*String[] qsplit = null;
				if (query != null) {
					qsplit = query.split("_");
				}*/

				Set<String> set;
				//String padda = qsplit[0];
				int ival = query.indexOf("|");
				if( ival == -1 ) ival = query.length();
				int ival2 = query.indexOf("ontig");
				if( ival2 == -1 ) ival2 = query.length();
				i = query.lastIndexOf("_", Math.min(ival,ival2) );
				if( i == -1 ) {
					System.err.println();
				}
				String padda = query.substring(0, i);
				
				/*if( padda.startsWith("Ocean") ) padda = "o.profundus";
				else if( padda.startsWith("Marin") ) padda = "m.hydrothermalis";
				else if( padda.contains("Silvanus") ) padda = "mt.silvanus";
				else if( padda.contains("Ruber") ) padda = "mt.ruber";
				else if( padda.contains("t.thermophilus_SG0_5JP17_16") ) {
					padda = "t.thermSG0_5JP17_16";
				}*/
				//if( padda.endsWith(".fna") ) padda = padda.substring(0, padda.length()-4);
				
				if (geneset.containsKey(padda)) {
					set = geneset.get(padda);
				} else {
					set = new HashSet<String>();
					System.err.println("new padda " + padda);
					geneset.put(padda, set);
				}

				String val = id;
				// set.add( desc );

				// int first = query.indexOf('_');
				// int sec = query.indexOf('_', first+1 );

				String contigstr = null;
				String contloc = null;

				/*if (qsplit.length < 3) {
					System.err.println();
				}*/
				
					//i = query.indexOf('_', i);
					//String[] qsplit = query.substring(i+5).split("_");
				
					int fi = query.indexOf('_');
					int li = query.lastIndexOf('_');
					contigstr = query.substring(0, li);// + "_" + qsplit[0];// +"_"+qsplit[2];
															// //&query.substring(0,
															// sec);
					/*int k = 0;
					if (qsplit[1].contains("|")) {
						contig += "_" + qsplit[1];
						k++;
					}*/
					contloc = query.substring(fi+1,query.length());//qsplit[0] + "_" + qsplit[1 + k]; // query.substring(first+1,sec);
				/*} else {
					contig = qsplit[0];
					contloc = qsplit[0] + "_" + qsplit[1];
				}*/
				// System.err.println("contig "+contig);

				StringBuilder aa = aaSearch(query);
				/*
				 * int nq = query.lastIndexOf('_'); int mq =
				 * query.lastIndexOf('_', nq-1); String nquery; if( mq != -1 ) {
				 * nquery = query.substring(0, nq); } else { nquery = query; }
				 */
				StringBuilder dn = dnaSearch(query);
				if( dn == null ) {
					System.err.println( query );
					System.err.println();
				}
				// System.err.println( "ermm " + ori );
				double deval = 0.0;
				try {
					deval = Double.parseDouble(evalue);
				} catch( Exception e ) {
					e.printStackTrace();
				}				
				Gene gene;
				String check = addon ? "_"+aa : val;
				if( ret.containsKey( check ) ) {
					gene = genestuff( uclusterlist, query, desc, teg, check, ret );
					if( addon ) {
						gene.name = desc;
						gene.origin = teg;
						gene.allids.remove( check );
					}
				} else {
					gene = new Gene(null, desc, teg);
					gene.allids = new HashSet<String>();
					gene.species = new HashMap<String, Teginfo>();
					ret.put(val, gene);
					gene.refid = id;
				}
				gene.allids.add(id);
				set.add(val);
				
				Contig contig = contigmap.containsKey( contigstr ) ? contigmap.get( contigstr ) : new Contig( contigstr );
				if( contigstr.contains("CCB") ) {
					for( String str : contigmap.keySet() ) {
						if( str.contains("CCB") ) {
							System.err.println( str + "  " + contigstr );
						}
					}
				}
				Tegeval tv = new Tegeval(gene, teg, deval, aa, dn, query, contig, contloc, start, stop, ori);
				if( preval != null ) {
					Contig precontig = preval.getContshort();
					Contig curcontig = tv.getContshort();
					boolean bu = precontig.equals( curcontig );
					//System.err.println( bu + "  " + precontig.toString().equals( curcontig.toString()) + "  " + (precontig == curcontig) );
					if( bu ) {
						tv.setPrevious( preval );
						curcontig.end = tv;
					} else {
						curcontig.start = tv;
						curcontig.end = tv;
					}
				}
				preval = tv;
				
				Teginfo stv;
				if (!gene.species.containsKey(padda)) {
					stv = new Teginfo();
					gene.species.put(padda, stv);
				} else {
					stv = gene.species.get(padda);
				}
				
				if( addon ) {
					Tegeval rem = null;
					for( Tegeval te : stv.tset ) {
						if( te.cont.equals( query ) ) {
							rem = te;
							break;
						}
					}
					if( rem != null ) {
						stv.tset.remove( rem );
					}
				}
				stv.add( tv );
				// gene.blastspec = teg;
				/*
				 * if( gene.species == null ) { gene.species = new
				 * HashMap<String,Set<Tegeval>>(); }
				 */

				if (!allgenes.containsKey(val) || allgenes.get(val) == null) {
					//allgenes.put(val, split.length > 1 ? teg + " " + id : null);
					allgenes.put(val, teg + " " + id );
				}

				Set<String> locset = null;
				if (geneloc.containsKey(val)) {
					locset = geneloc.get(val);
				} else {
					locset = new HashSet<String>();
					geneloc.put(val, locset);
				}
				// locset.add( swapmap.get(name)+"_"+query + " " + evalue );
				locset.add(query + " " + evalue);

				//int li = query.lastIndexOf('_');
				//int ln = query.lastIndexOf('_', li-1);
				//String 	queryshort = query.substring(0, ln)+query.substring(li);
				locgene.put(query, gene);

				query = null;
				if (fw != null) {
					/*
					 * if( line.lastIndexOf('[') > line.indexOf(']') ) { String
					 * newline = br.readLine(); line += newline.trim(); }
					 */
					fw.write(trim + "\n");
				}

				if (newline.startsWith("Query=")) {
					line = newline;
					continue;
				}
			} else if ( trim.contains("No hits")) {
				if( !addon ) {
					Gene gene;
	
					StringBuilder aa = aaSearch(query);
					String aaid = "_"+aa;
					
					
					/*String padda = query.substring(0, query.indexOf('_')); //split("_")[0];
					if( padda.endsWith(".fna") ) padda = padda.substring(0,padda.length()-4);*/
					
					String padda;
					if( query != null ) {
						int ival = query.indexOf("|");
						if( ival == -1 ) ival = query.length();
						int ival2 = query.indexOf("ontig");
						if( ival2 == -1 ) ival2 = query.length();
						int i = query.lastIndexOf("_", Math.min(ival,ival2) );
						padda = query.substring(0, i);
					} else {
						padda = "";
					}
					
					/*if( padda.startsWith("Ocean") ) padda = "o.profundus";
					else if( padda.startsWith("Marin") ) padda = "m.hydrothermalis";
					else if( padda.contains("Silvanus") ) padda = "mt.silvanus";
					else if( padda.contains("Ruber") ) padda = "mt.ruber";
					else if( padda.contains("t.thermophilus_SG0_5JP17_16") ) padda = "t.thermSG0_5JP17_16";*/
					
					if (ret.containsKey(aaid)) {
						gene = ret.get(aaid);
					} else {
						gene = new Gene(null, aaid, padda);
						ret.put(aaid, gene);
						gene.allids = new HashSet<String>();
						gene.species = new HashMap<String, Teginfo>();
						ret.put(aaid, gene);
						gene.refid = aaid;
					}
					gene.allids.add(aaid);
	
					if (gene.species == null)
						gene.species = new HashMap<String, Teginfo>();
					double deval = -1.0;
					/*
					 * try { deval = Double.parseDouble(evalue); } catch( Exception
					 * e ) { System.err.println("ok"); }
					 */
					// gene.species.put( padda, new Tegeval( padda, deval,
					// aas.get(query), query ) );
					Teginfo stv;
					if (!gene.species.containsKey(padda)) {
						stv = new Teginfo();
						gene.species.put(padda, stv);
						
						System.err.println( "new annars " + padda );
					} else
						stv = gene.species.get(padda);
	
					String contigstr = null;
					String contloc = null;
	
					/*int first = query.indexOf('_');
					int sec = query.indexOf('_', first + 1);
					if (sec != -1) {
						contig = query.substring(0, sec);
						contloc = query.substring(first + 1);
					} else {
						contig = query;
						contloc = query.substring(first + 1);
					}*/
					
					int fi = query.indexOf('_');
					int li = query.lastIndexOf('_');
					contigstr = query.substring(0, li);
					contloc = query.substring(fi+1,query.length());
	
					StringBuilder aastr = aaSearch(query);
					/*int nq = query.lastIndexOf('_');
					int mq = query.lastIndexOf('_', nq - 1);
					String nquery;
					if (mq != -1) {
						nq = mq;
						nquery = query.substring(0, nq);
					} else {
						nquery = query;
					}*/
	
					Contig contig = contigmap.containsKey( contigstr ) ? contigmap.get( contigstr ) : new Contig( contigstr );
					StringBuilder dn = dnaSearch( query ); //dnaa.get(nquery);
					stv.add(new Tegeval(gene, padda, deval, aastr, dn, query, contig, contloc, start, stop, ori));
					
					if (!allgenes.containsKey(aaid) || allgenes.get(aaid) == null) {
						allgenes.put(aaid, "Thermus " + aaid);
					}
	
					Set<String> locset = null;
					if (geneloc.containsKey(aaid)) {
						locset = geneloc.get(aaid);
					} else {
						locset = new HashSet<String>();
						geneloc.put(aaid, locset);
					}
					
					locset.add(query + " -1.0");
					//int li = query.lastIndexOf('_');
					//int ln = query.lastIndexOf('_', li-1);
					//String queryshort = query.substring(0, ln)+query.substring(li);
					locgene.put(query, gene);
				}

				if (fw != null)
					fw.write(line + "\n");
			} else if (trim.startsWith("Query=")) {
				// if( trim.)
				//query = trim.substring(6).trim().split("[ ]+")[0].replace(".fna", "");
				
				/*int count = 0;
				if( query.contains("contig") ) {
					int k = query.indexOf('_');
					while( k != -1 ) {
						k = query.indexOf('_', k+1);
						count++;
					}
				} else if( query.contains("|") && !query.startsWith("mt.") ) {
					count = 3;
				}
				
				if( count == 3 ) {
					int li = query.lastIndexOf('_');
					int ln = query.lastIndexOf('_', li-1);
					query = query.substring(0, ln)+query.substring(li);
				}*/
				
				String[] split = trim.split("#");
				if ( split.length > 1 && split.length < 4) {
					String newline = br.readLine();
					line = line + newline;
					trim = line.trim();
					split = trim.split("#");
				}
				query = trim.substring(6).trim().split("[ ]+")[0]; //.replace(".fna", "");

				if (split.length >= 3) {
					start = Integer.parseInt(split[1].trim());
					stop = Integer.parseInt(split[2].trim());
				}
				if (split.length >= 4) {
					ori = Integer.parseInt(split[3].trim());
				}
				
				int ival = query.indexOf("|");
				if( ival == -1 ) ival = query.length();
				int ival2 = query.indexOf("ontig");
				if( ival2 == -1 ) ival2 = query.length();
				int i = query.lastIndexOf("_", Math.min(ival,ival2) );
				String padda = query.substring(0, i);
				
				poddur.add( padda );
				
				if (fw != null) {
					// String[] split = trim.split("#");
					// if( split.length < 4 ) {
					// line += br.readLine();
					// }
					fw.write(line + "\n");
				}
			} else {
				if( old ) {
					if (query != null && (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|"))) {
						//} else if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//line = br.readLine();
						//line = br.readLine();
						trim = line.trim();
						
						String[] split = trim.split("[\t ]+");
						evalue = split[split.length - 1];
						if( evalue.length() == 0 ) {
							System.err.println();
						}
						if (fw != null)
							fw.write(trim + "\n");
					}
				} else {
					if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//} else if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//line = br.readLine();
						//line = br.readLine();
						trim = line.trim();
						
						String[] split = trim.split("[\t ]+");
						evalue = split[split.length - 1];
						if( evalue.length() == 0 ) {
							System.err.println();
						}
						if (fw != null)
							fw.write(trim + "\n");
					}
				}
			}

			if( cnt % 1000 == 0 ) {
				System.err.println( cnt );
			}
			line = br.readLine();
		}
	}

	static Map<String, String> swapmap = new HashMap<String, String>();

	public static Map<String, Gene> panCoreFromNRBlast(String[] names, File dir) throws IOException {
		Map<String, Gene> ret = new HashMap<String, Gene>();

		Map<String, String> allgenes = new HashMap<String, String>();
		Map<String, Set<String>> geneset = new HashMap<String, Set<String>>();
		Map<String, Set<String>> geneloc = new HashMap<String, Set<String>>();
		Map<String, Gene> locgene = new HashMap<String, Gene>();

		// PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		// System.setOut( ps );

		Set<String> poddur = new HashSet<String>();
		for (String name : names) {
			File f = new File(dir, name);
			// FileReader fr = new FileReader( f );
			panCoreFromNRBlast(new FileReader(f), null, null, null, ret, allgenes, geneset, geneloc, locgene, poddur, null);
		}

		for (String name : geneset.keySet()) {
			Set<String> set = geneset.get(name);
			System.out.println(name + " genes total: " + set.size());
			geneset.put(name, set);
		}

		Set<String> allset = new HashSet<String>(allgenes.keySet());
		for (String name : geneset.keySet()) {
			Set<String> gset = geneset.get(name);
			allset.retainAll(gset);
		}
		System.out.println("Core genome size: " + allset.size());
		System.out.println("Pan genome size: " + allgenes.size());

		Set<String> nameset = null;

		for (String gname : allset) {
			System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
		}

		boolean info = true;

		for (String aname : poddur) {
			allset = new HashSet<String>(allgenes.keySet());
			nameset = new HashSet<String>(poddur);
			nameset.remove(aname);
			for (String tname : nameset) {
				allset.removeAll(geneset.get(tname));
			}
			// System.err.println( "Genes found only in " + swapmap.get(aname) +
			// "\t" + allset.size() );

			if (allset.size() > 0)
				System.out.println("Genes only in " + aname + "\t" + allset.size());
			if (info) {
				for (String gname : allset) {
					System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
				}
			}
		}

		for (String aname : poddur) {
			allset = new HashSet<String>(allgenes.keySet());
			nameset = new HashSet<String>(poddur);
			nameset.remove(aname);
			allset.removeAll(geneset.get(aname));
			for (String tname : nameset) {
				allset.retainAll(geneset.get(tname));
			}

			Set<String> reset = new HashSet<String>();
			for (String name : nameset) {
				// reset.add( swapmap.get(name) );
				reset.add(name);
			}

			if (allset.size() > 0)
				System.out.println("Genes only in all of " + reset + "\t" + allset.size());
			if (info)
				printflubb(allset, allgenes, geneloc);
		}

		List<String> poddulist = new ArrayList<String>(poddur);
		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				allset = new HashSet<String>(allgenes.keySet());
				nameset = new HashSet<String>(poddulist);
				// nameset.add( names[i] );
				// nameset.add( names[y] );
				nameset.remove(poddulist.get(i));
				nameset.remove(poddulist.get(y));

				allset.removeAll(geneset.get(poddulist.get(i)));
				allset.removeAll(geneset.get(poddulist.get(y)));
				for (String tname : nameset) {
					allset.retainAll(geneset.get(tname));
				}

				Set<String> reset = new HashSet<String>();
				// reset.add( swapmap.get(names[i]) );
				// reset.add( swapmap.get(names[y]) );

				for (String name : nameset) {
					// reset.add( swapmap.get(name) );
					reset.add(name);
				}

				if (allset.size() > 0)
					System.out.println("Genes only in all of " + reset + "\t" + allset.size());
				if (info)
					printflubb(allset, allgenes, geneloc);
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				allset = new HashSet<String>(allgenes.keySet());
				nameset = new HashSet<String>(poddulist);
				// nameset.add( names[i] );
				// nameset.add( names[y] );
				nameset.remove(poddulist.get(i));
				nameset.remove(poddulist.get(y));

				allset.retainAll(geneset.get(poddulist.get(i)));
				allset.retainAll(geneset.get(poddulist.get(y)));
				for (String tname : nameset) {
					allset.removeAll(geneset.get(tname));
				}

				Set<String> reset = new HashSet<String>();
				// reset.add( swapmap.get(names[i]) );
				// reset.add( swapmap.get(names[y]) );
				reset.add(poddulist.get(i));
				reset.add(poddulist.get(y));

				/*
				 * for( String name : nameset ) { reset.add( swapmap.get(name)
				 * ); }
				 */

				if (allset.size() > 0)
					System.out.println("Genes only in all of " + reset + "\t" + allset.size());
				if (info)
					printflubb(allset, allgenes, geneloc);
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k) }), info, false);
					if (info)
						printflubb(allset, allgenes, geneloc);

					allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k) }), info, true);
					if (info)
						printflubb(allset, allgenes, geneloc);
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l) }), info, false);
						if (info) {
							for (String gname : allset) {
								System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
							}
						}

						allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l) }), info, true);
						if (info) {
							for (String gname : allset) {
								System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						for (int m = l + 1; m < poddulist.size(); m++) {
							allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m) }), info, false);
							if (info)
								printflubb(allset, allgenes, geneloc);

							allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m) }), info, true);
							if (info)
								printflubb(allset, allgenes, geneloc);
						}
					}
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						for (int m = l + 1; m < poddulist.size(); m++) {
							for (int n = m + 1; n < poddulist.size(); n++) {
								allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m), poddulist.get(n) }), info, false);
								if (info)
									printflubb(allset, allgenes, geneloc);
							}
						}
					}
				}
			}
		}

		System.out.println("Unique genes total: " + allgenes.size());

		// ps.close();

		return ret;
	}

	public static void printflubb(Set<String> allset, Map<String, String> allgenes, Map<String, Set<String>> geneloc) {
		for (String gname : allset) {
			System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
		}
	}

	public static Set<String> flubb(Set<String> allgeneskeyset, List<String> poddulist, Map<String, Set<String>> geneset, Collection<String> rpoddur, boolean info, boolean invert) {
		Set<String> allset = new HashSet<String>(allgeneskeyset);
		Set<String> nameset = new HashSet<String>(poddulist);
		// nameset.add( names[i] );
		// nameset.add( names[y] );

		nameset.removeAll(rpoddur);
		// nameset.remove( poddulist.get(i) );
		// nameset.remove( poddulist.get(y) );
		// nameset.remove( poddulist.get(k) );

		Set<String> reset = new HashSet<String>();
		if (invert) {
			for (String padda : rpoddur) {
				allset.removeAll(geneset.get(padda));
			}
			// allset.removeAll( geneset.get( poddulist.get(i) ) );
			// allset.removeAll( geneset.get( poddulist.get(y) ) );
			// allset.removeAll( geneset.get( poddulist.get(k) ) );
			for (String tname : nameset) {
				allset.retainAll(geneset.get(tname));
			}

			for (String name : nameset) {
				// reset.add( swapmap.get(name) );
				reset.add(name);
			}
		} else {
			for (String padda : rpoddur) {
				allset.retainAll(geneset.get(padda));
			}
			for (String tname : nameset) {
				allset.removeAll(geneset.get(tname));
			}
			for (String name : rpoddur) {
				reset.add(name);
			}
		}

		/*
		 * reset.add( swapmap.get(names[i]) ); reset.add( swapmap.get(names[y])
		 * ); reset.add( swapmap.get(names[k]) );
		 */

		if (allset.size() > 0)
			System.out.println("Genes only in all of " + reset + "\t" + allset.size());
		return allset;
	}

	public static void recursiveSet(int fin, int val) {
		if (val < fin) {
			recursiveSet(fin, val + 1);
		} else {

		}
	}

	public static CharSequence trimSubstring(StringBuilder ac, String sb) {
		int first, last;

		for (first = 0; first < sb.length(); first++)
			if (!Character.isWhitespace(sb.charAt(first)))
				break;

		for (last = sb.length(); last > first; last--)
			if (!Character.isWhitespace(sb.charAt(last - 1)))
				break;

		return ac.append(sb, first, last);
	}

	private static void loci2aasequence(Reader rd) throws IOException {
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String name = null;
		StringBuilder ac = new StringBuilder();
		int start = 0;
		int stop = -1;
		int dir = 0;
		// StringBuffer ac = new StringBuffer();
		// List<Aas> aass = new ArrayList<Aas>();

		while (line != null) {
			if (line.startsWith(">")) {
				if (ac.length() > 0) {
					aas.put(name, new Aas(name, ac, start, stop, dir));
					// aass.add( new Aas(name, ac) );
				}

				ac = new StringBuilder();
				String cont = line.substring(1) + "";
				String[] split = cont.split(" ");
				name = split[0].replace(".fna","");
				start = Integer.parseInt(split[2]);
				stop = Integer.parseInt(split[4]);
				dir = Integer.parseInt(split[6]);

				// int v = name.indexOf("contig");
				/*
				 * if( v != -1 ) { int i1 = name.indexOf('_',v); int i2 =
				 * name.indexOf('_', i1+1); name = name.substring(0,i1) +
				 * name.substring(i2); }
				 */
			} else
				ac.append(line.trim() + "");
			// else trimSubstring(ac, line);
			line = br.readLine();
			// br.re
		}

		if (ac.length() > 0) {
			aas.put(name, new Aas(name, ac, start, stop, dir));
			// aass.add( new Aas(name, ac, start, stop, dir) );
		}
		ac = null;
		br.close();
		// fw.close();

		/*
		 * System.err.println("erm "+aass.size()); aas = new Aas[aass.size()];
		 * int i = 0; for( Aas a : aass ) { aas[i++] = a; } aass.clear();
		 * System.gc();
		 */

		// Arrays.sort( aas );
	}

	private static void loadcontigs(Reader rd) throws IOException {
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String name = null;
		StringBuilder ac = new StringBuilder();
		while (line != null) {
			if (line.startsWith(">")) {
				if (ac.length() > 0) {
					contigmap.put(name, new Contig( name, ac ) );
				}

				ac = new StringBuilder();

				int i = line.indexOf(' ');
				if( i == -1 ) i = line.length();
				name = line.substring(1, i).replace(".fna", "");
				
				//int first = tv.cont.indexOf("_");
				//int sec = tv.cont.indexOf("_", first + 1);

				//cname = tv.cont.substring(0, sec);
			} else
				ac.append(line);
			line = br.readLine();
		}
		
		if (ac.length() > 0) {
			contigmap.put( name, new Contig( name, ac ) );
		}
		br.close();
	}

	private static void loci2dnasequence(Reader rd) throws IOException {
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String name = null;
		StringBuilder ac = new StringBuilder();
		while (line != null) {
			if (line.startsWith(">")) {
				if (ac.length() > 0)
					dnaa.put(name, ac);

				ac = new StringBuilder();
				name = line.substring(1).split(" ")[0].replace(".fna","");

				//int v = name.indexOf("contig");
			} else
				ac.append(line.trim() + "");
			line = br.readLine();
		}
		if (ac.length() > 0)
			dnaa.put(name, ac);
		br.close();
	}

	static Aas aquery = new Aas(null, null, 0, 0, 0);

	public final static class Aas implements Comparable<Aas> {
		String name;
		final StringBuilder aas;
		int start;
		int stop;
		int dir;

		public Aas(String name, StringBuilder aas, int start, int stop, int dir) {
			this.name = name;
			this.aas = aas;
			this.start = start;
			this.stop = stop;
			this.dir = dir;
		}

		@Override
		public final int compareTo(Aas o) {
			return name.compareTo(o.name);
		}

		public final String toString() {
			return name;
		}

		// public byte[] get( String name ) {
	};

	// static Aas[] aas;
	static Map<String, Aas> aas = new HashMap<String, Aas>();
	static Map<String, StringBuilder> dnaa = new HashMap<String, StringBuilder>();
	//static Map<String, StringBuilder> contigsmap = new HashMap<String, StringBuilder>();

	public static void loci2aasequence(String[] stuff, File dir2) throws IOException {
		for (String st : stuff) {
			File aa = new File(dir2, st);
			loci2aasequence(new FileReader(aa));
		}
	}

	public static void printnohits(String[] stuff, File dir, File dir2) throws IOException {
		loci2aasequence(stuff, dir2);
		for (String st : stuff) {
			System.err.println("Unknown genes in " + swapmap.get(st + ".out"));

			File ba = new File(dir, "new2_" + st + ".out");
			BufferedReader br = new BufferedReader(new FileReader(ba));
			String line = br.readLine();
			String name = null;
			// String ac = null;
			while (line != null) {
				if (line.startsWith("Query= ")) {
					name = line.substring(8).split(" ")[0];
				}

				if (line.contains("No hits")) {
					// System.err.println( name + "\t" +
					// aas.get(swapmap.get(st+".out")+" "+name) );
				}

				line = br.readLine();
			}
			br.close();
		}
	}

	public static void createConcatFsa(String[] names, File dir) throws IOException {
		FileWriter fw = new FileWriter(new File(dir, "all.fsa"));
		for (String name : names) {
			File f = new File(dir, name + ".fsa");
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith(">"))
					fw.write(">" + name + "_" + line.substring(1) + "\n");
				else
					fw.write(line + "\n");

				line = br.readLine();
			}
			br.close();
		}
		fw.close();
	}

	public static void testbmatrix(String str) throws IOException {
		Set<String> testset = new HashSet<String>(Arrays.asList(new String[] { "1s", "2s", "3s", "4s" }));
		Map<Set<String>, Set<Map<String, Set<String>>>> clustermap = new HashMap<Set<String>, Set<Map<String, Set<String>>>>();
		Set<Map<String, Set<String>>> smap = new HashSet<Map<String, Set<String>>>();
		smap.add(new HashMap<String, Set<String>>());
		smap.add(new HashMap<String, Set<String>>());
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "1s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "2s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "3s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "4s" })), smap);

		BufferedImage img = bmatrix(testset, clustermap);

		ImageIO.write(img, "png", new File(str));
	}

	public static BufferedImage bmatrix(Set<String> species, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		for (String spc : species) {
			int tstrw = g2.getFontMetrics().stringWidth(spc);
			if (tstrw > mstrw)
				mstrw = tstrw;
		}

		int sss = mstrw + 72 * species.size() + 10 + 72;
		bi = new BufferedImage(sss, sss, BufferedImage.TYPE_INT_RGB);
		g2 = (Graphics2D) bi.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.white);
		g2.fillRect(0, 0, sss, sss);

		double minh = 100.0;
		double maxh = 0.0;

		double minhwoc = 100.0;
		double maxhwoc = 0.0;

		double minr = 100.0;
		double maxr = 0.0;

		int mins = 1000000;
		int maxs = 0;

		int minrs = 1000000;
		int maxrs = 0;

		int where = 0;
		for (String spc1 : species) {
			int strw = g2.getFontMetrics().stringWidth(spc1);

			g2.setColor(Color.black);
			g2.drawString(spc1, mstrw - strw, mstrw + 47 + where * 72);
			g2.rotate(Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			g2.drawString(spc1, mstrw + 42 + where * 72, mstrw - strw);
			g2.rotate(-Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);

			int wherex = 0;
			for (String spc2 : species) {
				int spc1tot = 0;
				int spc2tot = 0;
				int totot = 0;

				int spc1totwoc = 0;
				int spc2totwoc = 0;
				int tototwoc = 0;
				for (Set<String> set : clusterMap.keySet()) {
					Set<Map<String, Set<String>>> erm = clusterMap.get(set);
					if (set.contains(spc1)) {
						if (set.size() < species.size()) {
							spc1totwoc += erm.size();
							for (Map<String, Set<String>> sm : erm) {
								Set<String> hset = sm.get(spc1);
								tototwoc += hset.size();
							}

							if (set.contains(spc2)) {
								spc2totwoc += erm.size();
							}

							if (spc2totwoc > spc1totwoc)
								System.err.println("okoko " + spc1totwoc + " " + spc2totwoc);
						}

						spc1tot += erm.size();
						for (Map<String, Set<String>> sm : erm) {
							Set<String> hset = sm.get(spc1);
							totot += hset.size();
						}

						if (set.contains(spc2)) {
							spc2tot += erm.size();
						}
					}
				}

				double hh = (double) spc2tot / (double) spc1tot;
				if (hh > maxh)
					maxh = hh;
				if (hh < minh)
					minh = hh;

				double hhwoc = (double) spc2totwoc / (double) spc1totwoc;
				if (hhwoc > maxhwoc)
					maxhwoc = hhwoc;
				if (hhwoc < minhwoc)
					minhwoc = hhwoc;

				double rr = (double) spc1tot / (double) totot;
				if (rr > maxr)
					maxr = rr;
				if (rr < minr)
					minr = rr;

				int ss = totot;
				if (ss > maxs)
					maxs = ss;
				if (ss < mins)
					mins = ss;

				int rs = (int) spc1tot;
				if (rs > maxrs)
					maxrs = rs;
				if (rs < minrs)
					minrs = rs;
			}
			//System.err.println();
			where++;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		where = 0;
		for (String spc1 : species) {
			int wherex = 0;
			for (String spc2 : species) {
				int spc1tot = 0;
				int spc2tot = 0;
				int totot = 0;

				int spc1totwocore = 0;
				int spc2totwocore = 0;
				int tototwocore = 0;
				for (Set<String> set : clusterMap.keySet()) {
					Set<Map<String, Set<String>>> erm = clusterMap.get(set);
					if (set.contains(spc1)) {
						if (set.size() < species.size()) {
							spc1totwocore += erm.size();
							for (Map<String, Set<String>> sm : erm) {
								Set<String> hset = sm.get(spc1);
								tototwocore += hset.size();
							}

							if (set.contains(spc2)) {
								spc2totwocore += erm.size();
							}

							if (spc2totwocore > spc1totwocore)
								System.err.println("okoko " + spc1totwocore + " " + spc2totwocore);
						}

						spc1tot += erm.size();
						for (Map<String, Set<String>> sm : erm) {
							Set<String> hset = sm.get(spc1);
							totot += hset.size();
						}

						if (set.contains(spc2)) {
							spc2tot += erm.size();
						}
					}
				}

				if (where == wherex) {
					double dval = (double) spc1tot / (double) totot;
					int cval = (int) (200.0 * (maxr - dval) / (maxr - minr));
					g2.setColor(new Color(255, cval, cval));
					g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);

					g2.setColor(Color.white);
					String str = spc1tot + "";
					int nstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 - 15);

					str = totot + "";
					nstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72);

					ps.printf("%.1f%s", (float) (dval * 100.0), "%");
					str = baos.toString();
					baos.reset();
					nstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 + 15);
				} else {
					double dval = (double) spc2totwocore / (double) spc1totwocore;
					int cval = (int) (200.0 * (maxhwoc - dval) / (maxhwoc - minhwoc));
					g2.setColor(new Color(cval, 255, cval));
					g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);

					g2.setColor(Color.white);
					String str = spc2totwocore + "";
					int nstrw2 = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw2 / 2, mstrw + 47 + where * 72 - 15);
					// int nstrw2 = g2.getFontMetrics().stringWidth( str );
					str = spc1totwocore + "";
					int nstrw1 = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw1 / 2, mstrw + 47 + where * 72);

					double hlut = 100.0 * ((double) spc2totwocore / (double) spc1totwocore);
					ps.printf("%.1f%s", (float) hlut, "%");
					str = baos.toString();
					baos.reset();
					int pstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - pstrw / 2, mstrw + 47 + where * 72 + 15);
				}

				if (wherex == 0) {
					int dval = totot;
					int cval = (int) (200.0 * (maxs - dval) / (maxs - mins));
					g2.setColor(new Color(cval, cval, 255));
					g2.fillRoundRect(mstrw + 10 + species.size() * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);

					g2.setColor(Color.white);
					String str = dval + "";
					int nstrw2 = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + species.size() * 72 - nstrw2 / 2, mstrw + 47 + where * 72);
					// int nstrw2 = g2.getFontMetrics().stringWidth( str );

					dval = spc1tot;
					cval = (int) (200.0 * (maxrs - dval) / (maxrs - minrs));
					g2.setColor(new Color(cval, cval, 255));
					g2.fillRoundRect(mstrw + 10 + where * 72, mstrw + 10 + species.size() * 72, 64, 64, 16, 16);
					g2.setColor(Color.white);
					str = spc1tot + "";
					int nstrw1 = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + where * 72 - nstrw1 / 2, mstrw + 47 + species.size() * 72);
				}

				wherex++;
			}

			//System.err.println();
			where++;
		}

		g2.setColor(Color.gray);
		g2.fillRoundRect(mstrw + 10 + species.size() * 72, mstrw + 10 + species.size() * 72, 64, 64, 16, 16);

		int core = 0;
		int pan = 0;

		for (Set<String> set : clusterMap.keySet()) {
			Set<Map<String, Set<String>>> setmap = clusterMap.get(set);

			pan += setmap.size();
			if (set.size() == species.size())
				core += setmap.size();
		}

		g2.setColor(Color.white);
		String str = core + "";
		int nstrw2 = g2.getFontMetrics().stringWidth(str);
		g2.drawString(str, mstrw + 42 + species.size() * 72 - nstrw2 / 2, mstrw + 47 + species.size() * 72 - 15);
		// int nstrw2 = g2.getFontMetrics().stringWidth( str );
		str = pan + "";
		int nstrw1 = g2.getFontMetrics().stringWidth(str);
		g2.drawString(str, mstrw + 42 + species.size() * 72 - nstrw1 / 2, mstrw + 47 + species.size() * 72);

		return bi;
	}

	private static void intersectSets(Set<String> all, List<Set<String>> total) {
		// Set<Set<String>> rem = new HashSet<Set<String>>();
		// int i = 0;
		
		List<Set<String>> newtotal = new ArrayList<Set<String>>();
		for (Set<String> check : total) {
			Set<String> cont = new HashSet<String>();

			for (String aval : all) {
				if (check.contains(aval))
					cont.add(aval);
			}
			all.removeAll(cont);

			if (cont.size() > 0) {
				if (cont.size() < check.size()) {
					Set<String> ncont = new HashSet<String>(check);
					ncont.removeAll(cont);

					newtotal.add(ncont);
				}
				newtotal.add(cont);
			} else
				newtotal.add(check);

			// else if( all.size() > 0 ) newtotal.add( all );

			if (cont.size() > 0)
				all.removeAll(cont);
		}

		if (all.size() > 0)
			newtotal.add(all);

		total.clear();
		total.addAll(newtotal);

		/*
		 * for( Set<String> erm : rem ) { int ind = -1; int count = 0; for(
		 * Set<String> ok : total ) { if( ok.size() == erm.size() &&
		 * ok.containsAll(erm) ) { ind = count; break; } count++; }
		 * 
		 * if( ind != -1 ) { total.remove( ind ); } }
		 * 
		 * rem.clear(); if( cont == null ) total.add( all );
		 * 
		 * Set<String> erm = new HashSet<String>(); System.err.println( "erm " +
		 * total.size() ); for( Set<String> ss : total ) { for( String s : ss )
		 * { if( erm.contains( s ) ) { System.err.println( "buja " + s ); break;
		 * } } erm.addAll( ss ); }
		 */
	}

	private static void joinSets(Set<String> all, List<Set<String>> total) {
		Set<String> cont = null;
		Set<Set<String>> rem = new HashSet<Set<String>>();
		int i = 0;
		for (Set<String> check : total) {
			for (String aval : all) {
				if (check.contains(aval)) {
					if (cont == null) {
						cont = check;
						check.addAll(all);
						break;
					} else {
						cont.addAll(check);
						rem.add(check);
						break;
					}
				}
			}

			i++;
		}

		// for( Set<String> s : rem ) {
		// if( s.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821" ) )
		// System.err.println("okerm" + rem.size());
		// }

		// int sbef = total.size();
		// if( rem.size() > 0 ) System.err.println("tsize bef "+sbef);
		// total.removeAll( rem );

		for (Set<String> erm : rem) {
			// if( erm.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821"
			// ) ) System.err.println( "asdf "+erm.toString()+"  "+erm );

			int ind = -1;
			int count = 0;
			for (Set<String> ok : total) {
				if (ok.size() == erm.size() && ok.containsAll(erm)) {
					ind = count;
					break;
				}
				count++;
			}

			if (ind != -1) {
				total.remove(ind);
			}
		}

		// for( Set<String> erm : total ) {
		// if( erm.contains( "ttHB27join_gi|46197919|gb|AE017221.1|_978_821" ) )
		// System.err.println( "asdf2 "+erm.toString()+"  "+erm );
		// }

		// int saft = total.size();
		// if( rem.size() > 0 ) System.err.println("tsize aft "+saft);

		rem.clear();
		if (cont == null)
			total.add(all);

		Set<String> erm = new HashSet<String>();
		System.err.println("erm " + total.size());
		for (Set<String> ss : total) {
			for (String s : ss) {
				if (erm.contains(s)) {
					System.err.println("buja " + s);
					break;
				}
			}
			erm.addAll(ss);
		}
	}

	/*private static Collection<Set<String>> joinBlastSets(File dir, String[] stuff, String write, boolean union) throws IOException {
		List<Set<String>> total = new ArrayList<Set<String>>();
		FileWriter fw = write == null ? null : new FileWriter(write); // new
																		// FileWriter("/home/sigmar/blastcluster.txt");
		for (String name : stuff) {
			File f = new File(dir, name);
			BufferedReader br = new BufferedReader(new FileReader(f));

			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("Sequences prod")) {
					line = br.readLine();
					Set<String> all = new HashSet<String>();
					while (line != null && !line.startsWith(">")) {
						String trim = line.trim();
						if (trim.startsWith("t.scoto") || trim.startsWith("t.antr") || trim.startsWith("t.aqua") || trim.startsWith("t.t") || trim.startsWith("t.egg") || trim.startsWith("t.island") || trim.startsWith("t.oshi") || trim.startsWith("t.brock") || trim.startsWith("t.fili") || trim.startsWith("t.igni") || trim.startsWith("t.kawa") || trim.startsWith("mt.") ) {
							String val = trim.substring(0, trim.indexOf('#') - 1);
							int v = val.indexOf("contig");
							/*
							 * if( v != -1 ) { int i1 = val.indexOf('_',v); int
							 * i2 = val.indexOf('_', i1+1); val =
							 * val.substring(0,i1) + val.substring(i2); }
							 *
							all.add(val);
						}
						line = br.readLine();
					}

					if (fw != null)
						fw.write(all.toString() + "\n");

					if (union)
						joinSets(all, total);
					else
						intersectSets(all, total);

					if (line == null)
						break;
				}

				line = br.readLine();
			}
		}
		if (fw != null)
			fw.close();

		return total;
	}*/

	private static List<Set<String>> readBlastList(String filename) throws IOException {
		List<Set<String>> total = new ArrayList<Set<String>>();

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			String substr = line.substring(1, line.length() - 1);
			String[] split = substr.split("[, ]+");
			Set<String> all = new HashSet<String>(Arrays.asList(split));

			joinSets(all, total);

			line = br.readLine();
		}
		br.close();

		return total;
	}
	
	private static List<Set<String>> loadUClusters(Reader rd) throws IOException {
		// FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		List<Set<String>> ret = new ArrayList<Set<String>>();
		Set<String> prevset = null;

		while (line != null) {
			String[] split = line.split("[\t ]+");
			if( line.startsWith("S") ) {
				prevset = new TreeSet<String>();
				ret.add(prevset);
			}
			if( !line.startsWith("C") && prevset != null ) {
				prevset.add( split[8] );
			}
			
			line = br.readLine();
		}
		br.close();

		return ret;
	}

	private static List<Set<String>> loadSimpleClusters(Reader rd) throws IOException {
		// FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		List<Set<String>> ret = new ArrayList<Set<String>>();
		Set<String> prevset = null;

		while (line != null) {
			if (!line.startsWith("\t")) {
				String[] split = line.split("[\t ]+");
				try {
					Integer.parseInt(split[0]);
					prevset = new TreeSet<String>();
					ret.add(prevset);
				} catch (Exception e) {

				}
			} else { // if( line.startsWith("\t") ) {
				String trimline = line.trim();
				if (trimline.startsWith("[")) {
					String[] subsplit = trimline.substring(1, trimline.length() - 1).split("[, ]+");
					Set<String> trset = new TreeSet<String>(Arrays.asList(subsplit));
					if (prevset != null)
						prevset.addAll(trset);
					// ret.add( trset );
				}
			}
			line = br.readLine();
		}
		br.close();

		return ret;
	}

	private static Map<Set<String>, Set<Map<String, Set<String>>>> loadCluster(String path) throws IOException {
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = new HashMap<Set<String>, Set<Map<String, Set<String>>>>();

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		Set<String> olderm = null;
		Map<String, Set<String>> ok = null;
		Set<Map<String, Set<String>>> setmap = null;
		String curspec = null;

		Set<String> trall = new HashSet<String>();
		List<String> tralli = new ArrayList<String>();

		while (line != null) {
			if (line.startsWith("[")) {
				Set<String> erm = new HashSet<String>();
				String substr = line.substring(1, line.length() - 1);
				String[] split = substr.split("[, ]+");
				erm.addAll(Arrays.asList(split));

				if (olderm != null) {
					clusterMap.put(olderm, setmap);
				}
				olderm = erm;
				setmap = new HashSet<Map<String, Set<String>>>();
			} else if (!line.startsWith("\t")) {
				if (olderm != null) {
					clusterMap.put(olderm, setmap);
				}

				ok = new HashMap<String, Set<String>>();
				setmap.add(ok);
			} else {
				String trimline = line.trim();
				if (trimline.startsWith("[")) {
					String[] subsplit = trimline.substring(1, trimline.length() - 1).split("[, ]+");
					Set<String> trset = new HashSet<String>(Arrays.asList(subsplit));

					for (String trstr : trset) {
						if (trstr.contains("ttaqua")) {
							if (trall.contains(trstr)) {
								System.err.println("uhm1 " + trstr);
							} else {
								trall.add(trstr);
							}
							tralli.add(trstr);
						}
					}

					ok.put(curspec, trset);
				} else {
					curspec = trimline;
				}
			}
			line = br.readLine();
		}
		clusterMap.put(olderm, setmap);

		br.close();

		System.err.println("hohoho " + trall.size() + "  " + tralli.size());

		return clusterMap;
	}

	private static Map<Set<String>, Set<Map<String, Set<String>>>> initCluster(Collection<Set<String>> total, Set<String> species) {
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = new HashMap<Set<String>, Set<Map<String, Set<String>>>>();

		for (Set<String> t : total) {
			Set<String> teg = new HashSet<String>();
			for (String e : t) {
				String str = e.substring(0, e.indexOf('_'));
				/*
				 * if( joinmap.containsKey( str ) ) { str = joinmap.get(str); }
				 */
				teg.add(str);

				species.add(str);
			}

			Set<Map<String, Set<String>>> setmap;
			if (clusterMap.containsKey(teg)) {
				setmap = clusterMap.get(teg);
			} else {
				setmap = new HashSet<Map<String, Set<String>>>();
				clusterMap.put(teg, setmap);
			}

			Map<String, Set<String>> submap = new HashMap<String, Set<String>>();
			setmap.add(submap);

			for (String e : t) {
				String str = e.substring(0, e.indexOf('_'));
				/*
				 * if( joinmap.containsKey( str ) ) { str = joinmap.get(str); }
				 */

				Set<String> set;
				if (submap.containsKey(str)) {
					set = submap.get(str);
				} else {
					set = new HashSet<String>();
					submap.put(str, set);
				}
				set.add(e);
			}
		}

		return clusterMap;
	}

	private static void writeSimplifiedCluster(String filename, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) throws IOException {
		FileWriter fos = new FileWriter(filename);
		for (Set<String> set : clusterMap.keySet()) {
			Set<Map<String, Set<String>>> mapset = clusterMap.get(set);
			fos.write(set.toString() + "\n");
			int i = 0;
			for (Map<String, Set<String>> erm : mapset) {
				fos.write((i++) + "\n");

				for (String erm2 : erm.keySet()) {
					Set<String> erm3 = erm.get(erm2);
					fos.write("\t" + erm2 + "\n");
					fos.write("\t\t" + erm3.toString() + "\n");
				}
			}
		}
		fos.close();
	}

	public static Set<String> speciesFromCluster(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) {
		Set<String> species = new TreeSet<String>();

		for (Set<String> clustset : clusterMap.keySet()) {
			species.addAll(clustset);
		}

		return species;
	}

	public static void func4(File dir, String[] stuff) throws IOException {
		// HashMap<Set<String>,Set<Map<String,Set<String>>>> clusterMap = new
		// HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		/*
		 * Map<String,String> joinmap = new HashMap<String,String>();
		 * joinmap.put("ttpHB27", "ttHB27"); joinmap.put("ttp1HB8", "ttHB8");
		 * joinmap.put("ttp2HB8", "ttHB8");
		 */

		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = loadCluster("/home/sigmar/burb2.txt");

		// Set<String> species = new TreeSet<String>();
		// Set<Set<String>> total = func4_header( dir, stuff );
		// List<Set<String>> total = readBlastList(
		// "/home/sigmar/blastcluster.txt" );

		/*
		 * FileWriter fw = new FileWriter("/home/sigmar/joincluster.txt"); for(
		 * Set<String> sset : total ) { fw.write( sset.toString()+"\n" ); }
		 * fw.close();
		 */

		/*
		 * Set<String> trall = new HashSet<String>(); for( Set<String> sset :
		 * total ) { for( String trstr : sset ) { if( trstr.contains("ttaqua") )
		 * { if( trall.contains( trstr ) ) { System.err.println( "uhm1 " + trstr
		 * ); } else { trall.add( trstr ); } //tralli.add( trstr ); } } }
		 */

		// Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap =
		// initCluster( total, species );
		Set<String> species = speciesFromCluster(clusterMap);

		// writeSimplifiedCluster( "/home/sigmar/burb2.txt", clusterMap );

		writeBlastAnalysis(clusterMap, species);
	}

	public static void clusterFromSimplifiedBlast(String filename) throws IOException {
		clusterFromSimplifiedBlast(filename, null);
	}

	public static void clusterFromSimplifiedBlast(String filename, String writeSimplifiedCluster) throws IOException {
		Set<String> species = new TreeSet<String>();
		List<Set<String>> total = readBlastList(filename); // "/home/sigmar/blastcluster.txt"
															// );
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = initCluster(total, species);

		if (writeSimplifiedCluster != null)
			writeSimplifiedCluster(writeSimplifiedCluster, clusterMap); // "/home/sigmar/burb2.txt",
																		// clusterMap
																		// );

		writeBlastAnalysis(clusterMap, species);
	}

	public static void clusterFromBlastResults(File dir, String[] stuff) throws IOException {
		clusterFromBlastResults(dir, stuff, null, null, true);
	}

	public static void clusterFromBlastResults(File dir, String[] stuff, String writeSimplifiedCluster, String writeSimplifiedBlast, boolean union) throws IOException {
		Set<String> species = new TreeSet<String>();
		List<Set<String>> total = new ArrayList<Set<String>>();
		Serifier serifier = new Serifier();
		for( String name : stuff ) {
			File ff = new File( dir, name );
			FileInputStream	fis = new FileInputStream( ff );
			
			serifier.joinBlastSets(fis, writeSimplifiedBlast, union, total, 0.0);
		}
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = initCluster(total, species);

		if (writeSimplifiedCluster != null)
			writeSimplifiedCluster(writeSimplifiedCluster, clusterMap); // "/home/sigmar/burb2.txt",
																		// clusterMap
																		// );

		writeBlastAnalysis(clusterMap, species);
	}

	public static void writeBlastAnalysis(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, Set<String> species) throws IOException {
		BufferedImage img = bmatrix(species, clusterMap);
		ImageIO.write(img, "png", new File("/home/sigmar/mynd.png"));

		PrintStream ps = new PrintStream(new FileOutputStream("/home/sigmar/out3.out"));
		System.setErr(ps);

		// System.err.println( "Total gene sets: " + total.size() );
		// System.err.println();

		List<StrSort> sortmap = new ArrayList<StrSort>();
		for (Set<String> set : clusterMap.keySet()) {
			Set<Map<String, Set<String>>> setmap = clusterMap.get(set);

			int i = 0;
			for (Map<String, Set<String>> map : setmap) {
				for (String s : map.keySet()) {
					Set<String> genes = map.get(s);
					i += genes.size();
				}
			}
			sortmap.add(new StrSort(setmap.size(), set.size() + "\t" + setmap.size() + "\tIncluded in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes\ttotal of " + i + " loci"));
			// System.err.println( "Included in : " + set.size() + " scotos " +
			// set + " containing: " + setmap.size() +
			// " set of genes\ttotal of " + i + " loci" );
		}

		Collections.sort(sortmap);
		for (StrSort ss : sortmap) {
			System.err.println(ss.s);
		}
		System.err.println();
		System.err.println();

		next(clusterMap);

		ps.close();

		/*
		 * System.err.println( "# clusters: " + total.size() ); int max = 0;
		 * for( Set<String> sc : total ) { if( sc.size() > max ) max =
		 * sc.size(); System.err.println( "\tcluster size: " + sc.size() ); }
		 * System.err.println( "maxsize: " + max );
		 * 
		 * int[] ia = new int[max]; for( Set<String> sc : total ) {
		 * ia[sc.size()-1]++; }
		 * 
		 * for( int i : ia ) { System.err.println( "hist: " + i ); }
		 */
	}

	public static void next(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) throws IOException {
		for (Set<String> set : clusterMap.keySet()) {
			Set<Map<String, Set<String>>> setmap = clusterMap.get(set);

			int i = 0;
			for (Map<String, Set<String>> map : setmap) {
				for (String s : map.keySet()) {
					Set<String> genes = map.get(s);
					i += genes.size();
				}
			}

			List<Map<String, Set<String>>> maplist = new ArrayList<Map<String, Set<String>>>();
			for (Map<String, Set<String>> map : setmap) {
				maplist.add(map);
			}

			Set<Integer> kfound = new HashSet<Integer>();
			String ermstr = "Included in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes";
			for (i = 0; i < maplist.size(); i++) {
				// if( !kfound.contains(i) ) {
				Set<String> ss = new HashSet<String>();
				Map<String, Set<String>> map = maplist.get(i);
				for (String s : map.keySet()) {
					for (String s2 : map.get(s)) {
						int ii = s2.indexOf('_', 0);
						if (ii == -1) {
							System.out.println(s2);
						} else {
							ss.add(s2.substring(0, ii));
						}
					}
				}

				if (ss.size() > 1) {
					Set<Integer> innerkfound = new HashSet<Integer>();
					for (int k = i + 1; k < maplist.size(); k++) {
						if (!kfound.contains(k)) {
							Set<String> ss2 = new HashSet<String>();
							map = maplist.get(k);
							for (String s : map.keySet()) {
								for (String s2 : map.get(s)) {
									ss2.add(s2.substring(0, s2.indexOf('_', 0)));
								}
							}
							if (ss.containsAll(ss2) && ss2.containsAll(ss)) {
								kfound.add(k);
								innerkfound.add(k);
							}
						}
					}

					if (innerkfound.size() > 0) {
						innerkfound.add(i);
						if (ermstr != null) {
							System.err.println(ermstr);
							ermstr = null;
						}
						System.err.println("Preserved clusters " + innerkfound);
						for (int k : innerkfound) {
							Map<String, Set<String>> sm = maplist.get(k);
							Set<String> geneset = new HashSet<String>();
							for (String s : sm.keySet()) {
								Set<String> sout = sm.get(s);
								for (String loci : sout) {
									String gene = lociMap.get(loci);
									if (gene == null) {
										StringBuilder aa = aas.get(loci).aas;
										gene = aa.toString();
									}

									if (gene == null) {
										System.out.println("error" + loci);
									} else
										geneset.add(gene.replace('\t', ' '));
								}
							}
							System.err.println("\t" + geneset);
						}
					}
				}
			}
		}

		FileWriter fw = new FileWriter("/home/sigmar/dragon.txt");

		boolean done;
		for (Set<String> set : clusterMap.keySet()) {
			Set<Map<String, Set<String>>> setmap = clusterMap.get(set);

			int i = 0;
			for (Map<String, Set<String>> map : setmap) {
				for (String s : map.keySet()) {
					Set<String> genes = map.get(s);
					i += genes.size();
				}
			}
			System.err.println("Included in : " + set.size() + " scotos\t" + set + "\tcontaining: " + setmap.size() + " set of genes\ttotal of " + i + " loci");

			i = 0;
			for (Map<String, Set<String>> map : setmap) {
				System.err.println("Starting set " + i);
				done = false;
				for (String s : map.keySet()) {
					Set<String> genes = map.get(s);

					System.err.println("In " + s + " containing " + genes.size());
					for (String gene : genes) {
						String aa = lociMap.get(gene);

						StringBuilder aastr = aas.get(gene).aas;
						if (aa == null) {
							aa = "Not found\t" + aastr;
						}
						aa += "\t" + aastr;
						System.err.println(gene + "\t" + aa);

						if (!done && set.size() == 12 && gene.startsWith("ttHB27")) {
							fw.write(">" + gene + "\n");
							for (int k = 0; k < aa.length(); k += 60) {
								fw.write(aa.substring(k, Math.min(k + 60, aa.length())) + "\n");
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

	private static Map<String, Integer> loadFrequency(Reader r, Set<String> included) throws IOException {
		Map<String, Integer> ret = new HashMap<String, Integer>();

		BufferedReader br = new BufferedReader(r);
		String name = null;
		String evalue = null;
		String score = null;
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("Query= ")) {
				String[] split = line.split("[ ]+");
				name = split[1];

				String lstr = split[split.length - 1];
				int us = lstr.indexOf('_');
				int l = 0;
				try {
					l = Integer.parseInt( us == -1 ? lstr.substring(7) : lstr.substring(7, us) );
				} catch( Exception e ) {
					System.err.println( lstr );
				}
				if (l > 80 && included.contains(name)) {
					evalue = null;
					score = null;
				} else {
					name = null;
				}
			}

			if (line.contains("No hits")) {
				String prename = name;

				aquery.name = prename;
				if (aas.containsKey(prename))
					lociMap.put(prename, "No match\t" + aas.get(prename));
				else if (dnaa.containsKey(prename))
					lociMap.put(prename, "No match\t" + dnaa.get(prename));
				else
					lociMap.put(prename, "No match");

				score = "";
				evalue = "";
				name = null;
			}

			if (evalue == null && (line.startsWith("ref|") || line.startsWith("sp|") || line.startsWith("pdb|") || line.startsWith("dbj|") || line.startsWith("gb|") || line.startsWith("emb|") || line.startsWith("pir|") || line.startsWith("tpg|"))) {
				String[] split = line.split("[\t ]+");
				if (score == null) {
					score = split[split.length - 2];

					if (ret.containsKey(split[0])) {
						int cnt = ret.get(split[0]);
						ret.put(split[0], cnt + 1);
					} else {
						ret.put(split[0], 1);
					}
				} else {
					String scstr = split[split.length - 2];
					double dsc = Double.parseDouble(scstr);
					double osc = Double.parseDouble(score);

					if (dsc != osc) {
						evalue = split[split.length - 1];
					} else {
						if (ret.containsKey(split[0])) {
							int cnt = ret.get(split[0]);
							ret.put(split[0], cnt + 1);
						} else {
							ret.put(split[0], 1);
						}
					}
				}
				// evalue = split[split.length-1];
			}

			line = br.readLine();
		}

		return ret;
	}

	public static class StrCont {
		String str;
	}

	private static void loci2gene(String base, Reader rd, String outfile, String filtercont, Map<String, Integer> freqmap, Set<String> included) throws IOException {
		FileWriter fw = null;

		Map<String, String> giid = new HashMap<String, String>();
		Map<String, StrCont> idtax = new HashMap<String, StrCont>();
		// Map<String,String> reftax = new HashMap<String,String>();

		for (String fid : freqmap.keySet()) {
			String[] split = fid.split("\\|");
			giid.put(split[1]/* .replace('.', ',') */, null);
		}

		FileInputStream gfr = new FileInputStream(base + "micjoin.fna");
		// GZIPInputStream gzi = new GZIPInputStream( gfr );
		BufferedReader gbr = new BufferedReader(new InputStreamReader(gfr));
		String gline = gbr.readLine();
		while (gline != null) {
			if (gline.startsWith(">")) {
				String[] split = gline.split("\\|");
				String gkey = split[3];
				if (giid.containsKey(gkey)) {
					String gi = split[1];

					giid.put(gkey, gi);
					idtax.put(gi, new StrCont());
				}
			}
			gline = gbr.readLine();
		}
		gfr.close();

		/*
		 * FileInputStream gfr = new
		 * FileInputStream(base+"GbAccList.0918.2011.gz"); GZIPInputStream gzi =
		 * new GZIPInputStream( gfr ); BufferedReader gbr = new BufferedReader(
		 * new InputStreamReader( gzi ) ); String gline = gbr.readLine(); while(
		 * gline != null ) { int li = gline.lastIndexOf(','); String gkey =
		 * gline.substring(0, li); if( giid.containsKey( gkey ) ) { String gi =
		 * gline.substring(li+1);
		 * 
		 * giid.put( gkey, gi ); idtax.put( gi, new StrCont() ); } gline =
		 * gbr.readLine(); } gfr.close();
		 */

		FileInputStream dfr = new FileInputStream(base + "gi_taxid_nucl.dmp.gz");
		GZIPInputStream dgzi = new GZIPInputStream(dfr);
		// FileReader dfr = new FileReader(base+"gi_taxid_nucl.dmp");
		BufferedReader dbr = new BufferedReader(new InputStreamReader(dgzi));
		String dstr = dbr.readLine();
		while (dstr != null) {
			int fi = dstr.indexOf('\t');
			String mi = dstr.substring(0, fi);
			// String[] ss = dstr.split("[\t ]+");
			StrCont sc = idtax.get(mi);
			if (sc != null)
				sc.str = dstr.substring(fi + 1);
			/*
			 * if( idtax.containsKey( ss[0]) ) { idtax.put( ss[0], ss[1] ); }
			 */

			dstr = dbr.readLine();
		}
		dfr.close();

		/*
		 * FileInputStream dfr = new
		 * FileInputStream(base+"release50.accession2geneid.gz");
		 * GZIPInputStream dgzi = new GZIPInputStream( dfr ); //FileReader dfr =
		 * new FileReader(base+"gi_taxid_nucl.dmp"); BufferedReader dbr = new
		 * BufferedReader( new InputStreamReader( dgzi ) ); String dstr =
		 * dbr.readLine(); while( dstr != null ) { String[] split =
		 * dstr.split("[\t ]+"); if( freqmap.containsKey(split[2]) ) {
		 * reftax.put( split[2], split[0] ); } dstr = dbr.readLine(); }
		 * dfr.close();
		 */

		List<String> taxmap = new ArrayList<String>();
		FileReader tfr = new FileReader(base + "names.dmp");
		BufferedReader tbr = new BufferedReader(tfr);
		String tstr = tbr.readLine();
		while (tstr != null) {
			if (tstr.contains("scientific name")) {
				String[] ss = tstr.split("\\|");
				String tid = ss[0].trim();
				int td = Integer.parseInt(tid);

				while (taxmap.size() < td) {
					taxmap.add(null);
				}
				if (td == taxmap.size())
					taxmap.add(ss[1].trim());
				else
					taxmap.set(td, ss[1].trim());
			}

			tstr = tbr.readLine();
		}
		tfr.close();

		Map<Integer, Integer> parmap = new HashMap<Integer, Integer>();
		FileReader pfr = new FileReader(base + "nodes.dmp");
		BufferedReader pbr = new BufferedReader(pfr);
		String pstr = pbr.readLine();
		while (pstr != null) {
			String[] ss = pstr.split("\\|");

			String tid = ss[0].trim();
			int td = Integer.parseInt(tid);

			String pid = ss[1].trim();
			int pd = Integer.parseInt(pid);

			parmap.put(td, pd);

			pstr = pbr.readLine();
		}
		pfr.close();

		for (String fid : freqmap.keySet()) {
			String[] spid = fid.split("\\|");
			if (spid.length > 1) {
				String gid = giid.get(spid[1]/* .replace('.', ',') */);
				StrCont sc = idtax.get(gid);
				String tid = sc == null ? null : sc.str;
				// String tid = reftax.get( spid[1] );

				int td = -1;
				try {
					td = Integer.parseInt(tid);
				} catch (Exception e) {
					System.err.println("trying " + tid);
				}

				if (td != -1) {
					Integer ntd = parmap.get(td);
					String taxname = taxmap.get(ntd);
					Integer ptd = parmap.get(ntd);
					String npar = taxmap.get(ptd);
					if (taxname.contains("nvironmental samples") && npar.equals("Bacteria")) {
						freqmap.put(fid, 0);
					}
					System.err.println(taxname);
				}
			}
		}

		Map<String, String> id2desc = new HashMap<String, String>();
		Map<String, List<String>> maplist = null;
		if (outfile != null) {
			fw = new FileWriter(outfile);
			maplist = new HashMap<String, List<String>>();
		}

		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String name = null;
		String nohitname = null;
		String evalue = null;
		String parseval = null;
		String score = null;
		int freq = 0;
		String freqname = null;
		String mostrecenttype = null;
		String id = null;
		String extend = null;

		// int linen = 0;
		while (line != null) {
			// if( linen++ % 1000 == 0 ) System.err.println( linen );
			// String trim = line.trim();
			if (line.startsWith("Query= ")) {
				if (extend != null && fw != null) {
					List<String> list;
					if (maplist.containsKey(id)) {
						list = maplist.get(id);
					} else {
						list = new ArrayList<String>();
						maplist.put(id, list);
					}
					String addstr = nohitname + "\t" + id + "\t" + evalue + extend;
					if (mostrecenttype != null)
						addstr += "\t" + mostrecenttype;
					list.add(addstr);
				}

				String[] split = line.split("[ ]+");
				name = split[1];
				nohitname = name;

				/*
				 * if( nohitname.contains("GFLUK6W04CMDEV") ) {
				 * System.err.println("erm"); }
				 */

				String lstr = split[split.length - 1];
				int us = lstr.indexOf('_');
				int l = 0;
				try {
					l = Integer.parseInt( us == -1 ? lstr.substring(7) : lstr.substring(7, us) );
				} catch( Exception e ) {
					System.err.println( lstr );
				}
				if (l > 80 && included.contains(name)) {
					evalue = null;
					score = null;
					freq = -1;
					freqname = null;
				} else {
					line = br.readLine();
					while (line != null && !line.startsWith("Query=")) {
						line = br.readLine();
					}
					if (line == null)
						break;
					else {
						extend = null;
						continue;
					}
					// name = null;
				}
				mostrecenttype = null;
				// int i1 = name.indexOf('_');
				// int i2 = name.indexOf('_', i1+1);
				// name = name.substring(0,i1) + name.substring(i2);
				// System.err.println(name);

				// if( fw != null ) fw.write( line + "\n" );
			}

			if (line.contains("No hits")) {
				String prename = nohitname; // swapmap.get(st+".out")+"_"+name;

				aquery.name = prename;
				if (aas.containsKey(prename))
					lociMap.put(prename, "No match\t" + aas.get(prename));
				else if (dnaa.containsKey(prename))
					lociMap.put(prename, "No match\t" + dnaa.get(prename));
				else
					lociMap.put(prename, "No match");

				if (fw != null) {
					String desc = "*** No hits ***";
					List<String> list;
					if (maplist.containsKey(desc)) {
						list = maplist.get(desc);
					} else {
						list = new ArrayList<String>();
						maplist.put(desc, list);
					}
					String addstr = name;
					list.add(addstr);
				}

				evalue = "";
				freqname = "";
				name = null;

				// System.err.println( prename + "\tNo match" );
				// if( fw != null ) fw.write( line + "\n" );
			}

			if (evalue == null && (line.startsWith("ref|") || line.startsWith("sp|") || line.startsWith("pdb|") || line.startsWith("dbj|") || line.startsWith("gb|") || line.startsWith("emb|") || line.startsWith("pir|") || line.startsWith("tpg|"))) {
				String[] split = line.split("[\t ]+");

				if (score == null) {
					score = split[split.length - 2];
					int nfreq = freqmap.get(split[0]);
					if (nfreq > freq) {
						freq = nfreq;
						freqname = split[0];
					} else if (nfreq == freq) {
						if (!line.contains("cult")) {
							freq = nfreq;
							freqname = split[0];
						}
					}
					parseval = split[split.length - 1];
				} else {
					String scstr = split[split.length - 2];
					double dsc = Double.parseDouble(scstr);
					double osc = Double.parseDouble(score);

					if (dsc != osc) {
						evalue = parseval; // split[split.length-1];
					} else {
						score = split[split.length - 2];
						int nfreq = freqmap.get(split[0]);
						if (nfreq > freq) {
							freq = nfreq;
							freqname = split[0];
						} else if (nfreq == freq) {
							if (!line.contains("cult")) {
								freq = nfreq;
								freqname = split[0];
							}
						}
					}
				}

				// if( fw != null ) fw.write( line + "\n" );
			}

			if ((line.startsWith(">ref") || line.startsWith(">sp") || line.startsWith(">pdb") || line.startsWith(">dbj") || line.startsWith(">gb") || line.startsWith(">emb") || line.startsWith(">pir") || line.startsWith(">tpg"))) {
				String[] split = line.split("\\|");

				String myid = split[0] + "|" + split[1] + "|";
				String desc = split[2];
				String teg = "";

				int idx = desc.lastIndexOf('[');
				int idx2 = desc.indexOf(']', idx);
				String newline = "";
				boolean qbool = false;
				if (idx > idx2 || idx == -1) {
					newline = br.readLine();
					qbool = newline.startsWith("Query=");
					if (!newline.startsWith("Length=") && !qbool) {
						line = line + newline;
						String newtrim = line.trim();

						split = newtrim.split("\\|");

						myid = split[0] + "|" + split[1] + "|";
						desc = split[2];

						idx = desc.lastIndexOf('[');
					}
				}

				boolean ibool = false;
				while (!qbool && !ibool) {
					newline = br.readLine();
					qbool = newline.startsWith("Query=");
					if (!qbool) {
						ibool = newline.contains("Identities");
					}
				}

				if (idx > 0) {
					teg = desc.substring(idx);
					desc = desc.substring(0, idx - 1).trim();
				} else {
					desc = desc.trim();
				}

				String nl = "";
				if (ibool) {
					String trim = newline.trim();
					int end = trim.indexOf(',');
					nl = trim.substring(13, end);
				}

				if (mostrecenttype == null && !desc.contains("cult")) {
					mostrecenttype = desc/* .replace(",", "") */+ "\t" + nl;
				}

				if (name != null) {
					String checkstr = line.substring(1, freqname.length() + 1);
					if (evalue == null)
						evalue = parseval;
					if (freqname.equals(checkstr)) {
						id = myid;
						// String prename = name;
						// //swapmap.get(st+".out")+"_"+name;

						/*
						 * URL url = new URL(
						 * "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id="
						 * +split[1]+"&retmode=xml" ); InputStream stream =
						 * url.openStream();
						 * 
						 * StringBuilder sb = new StringBuilder(); try { byte[]
						 * bb = new byte[1024]; int r = stream.read( bb );
						 * while( r > 0 ) { sb.append( new String(bb,0,r) ); r =
						 * stream.read( bb ); } } catch( Exception e ) {
						 * e.printStackTrace(); } stream.close();
						 * 
						 * String gbstart = "<GBSeq_taxonomy>"; int lind =
						 * sb.indexOf(gbstart); String sub = "not found"; if(
						 * lind > 0 ) { int lend =
						 * sb.indexOf("</GBSeq_taxonomy>",
						 * lind+gbstart.length()); sub =
						 * sb.substring(lind+gbstart.length(), lend); }
						 */

						id2desc.put(id, desc);

						extend = nl.length() == 0 ? nl : "\t" + nl;
						String stuff = id + "\t" + desc + "\t" + evalue + extend;
						lociMap.put(name, stuff);

						// lociMap.put( prename, split[1] + (split.length > 2 ?
						// "\t" + split[2] : "") + "\t" + evalue );
						name = null;
						// System.err.println( prename + "\t" + split[1] );
					}
				}
				if (qbool) {
					line = newline;
					continue;
				}
			}

			line = br.readLine();
		}
		br.close();

		if (fw != null) {
			List<String> list;
			if (maplist.containsKey(id)) {
				list = maplist.get(id);
			} else {
				list = new ArrayList<String>();
				maplist.put(id, list);
			}
			String addstr = nohitname + "\t" + id + "\t" + evalue + extend;
			if (mostrecenttype != null)
				addstr += "\t" + mostrecenttype;
			list.add(addstr);

			Map<Integer, List<String>> mupl = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
			int tot = 0;
			int subtot = 0;
			for (String nid : maplist.keySet()) {
				List<String> listi = maplist.get(nid);
				int i = listi.size();
				tot += i;

				// String spec = id2desc.get( id );

				if (!nid.contains("No hits"))
					subtot += i;
				List<String> erm;
				if (mupl.containsKey(i)) {
					erm = mupl.get(i);
				} else {
					erm = new ArrayList<String>();
					mupl.put(i, erm);
				}
				erm.add(nid);
			}
			fw.write("total: " + tot + " subtot: " + subtot + "\n");

			tot = 0;
			for (int i : mupl.keySet()) {
				List<String> listi = mupl.get(i);
				for (String nid : listi) {
					String spec = id2desc.get(nid);

					String[] spid = nid.split("\\|");
					if (spid.length > 1) {
						String gid = giid.get(spid[1]/* .replace('.', ',') */);
						StrCont sc = idtax.get(gid);
						String tid = sc == null ? null : sc.str;
						// String tid = reftax.get(spid[1]);

						int td = -1;
						try {
							td = Integer.parseInt(tid);
						} catch (Exception e) {
							System.err.println("trying " + tid);
						}

						if (td != -1) {
							fw.write(taxmap.get(td));
							Integer ntd = parmap.get(td);
							while (ntd != null && ntd != td) {
								td = ntd;

								fw.write(" : " + taxmap.get(td));

								ntd = parmap.get(td);
							}
							fw.write("\n");
						}
					}

					fw.write(nid + "\t" + spec + "\t" + i + "\n");

					if (maplist.containsKey(nid)) {
						fw.write("(");
						boolean first = true;
						List<String> res = maplist.get(nid);
						for (String rstr : res) {
							if (rstr != null) {
								String[] rspl = rstr.split("\t");
								if (rspl.length == 1) {
									if (first) {
										first = false;
										fw.write(rspl[0]);
									} else {
										fw.write("," + rspl[0]);
									}
								} else if (rspl.length == 4) {
									if (first) {
										first = false;
										fw.write(rspl[0] + " " + rspl[2] + " " + rspl[3]);
									} else
										fw.write("," + rspl[0] + " " + rspl[2] + " " + rspl[3]);
								} else if (rspl.length == 6) {
									if (first) {
										first = false;
										fw.write(rspl[0] + " " + rspl[2] + " " + rspl[3] + " " + rspl[4] + " " + rspl[5]);
									} else
										fw.write("," + rspl[0] + " " + rspl[2] + " " + rspl[3] + " " + rspl[4] + " " + rspl[5]);
								}
							}
						}
						fw.write(")\n");
					}

					fw.write("\n");
					if (tot >= 0 && (spec == null || !spec.contains("No hits"))) {
						tot += i;
						if (tot * 100 / subtot > 90) {
							fw.write("\n90%\n\n");
							tot = -1;
						}
					}

					if (filtercont != null && spec.contains(filtercont)) {
						List<String> mlist = maplist.get(nid);
						for (String str : mlist) {
							fw.write("\t" + str + "\n");
						}
					}
				}
			}

			fw.close();
		}
	}

	static Map<String, String> lociMap = new HashMap<String, String>();

	public static void loci2gene(String[] stuff, File dir) throws IOException {
		// Map<String,String> aas = new HashMap<String,String>();
		for (String st : stuff) {
			File ba = new File(dir, st);
			loci2gene("", new FileReader(ba), null, null, null, null);
		}
	}

	public static void aahist(File f1, File f2, int val) throws IOException {
		Map<String, Long> aa1map = new HashMap<String, Long>();
		Map<String, Long> aa2map = new HashMap<String, Long>();

		long t1 = 0;
		FileReader fr = new FileReader(f1);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			if (!line.startsWith(">")) {
				for (int i = 0; i < line.length() - val + 1; i++) {
					String c = line.substring(i, i + val);
					if (aa1map.containsKey(c)) {
						aa1map.put(c, aa1map.get(c) + 1L);
					} else
						aa1map.put(c, 1L);

					t1++;
				}
			}
			line = br.readLine();
		}
		br.close();

		// Runtime.getRuntime().availableProcessors()

		long t2 = 0;
		fr = new FileReader(f2);
		br = new BufferedReader(fr);
		line = br.readLine();
		while (line != null) {
			if (!line.startsWith(">")) {
				for (int i = 0; i < line.length() - val + 1; i++) {
					String c = line.substring(i, i + val);
					if (aa2map.containsKey(c)) {
						aa2map.put(c, aa2map.get(c) + 1L);
					} else
						aa2map.put(c, 1L);

					t2++;
				}
			}
			line = br.readLine();
		}
		br.close();

		System.err.println(t1 + "\t" + t2);
		int na1 = 0;
		int na2 = 0;
		int nab = 0;
		int u = 0;
		double dt = 0.0;
		Set<String> notfound = new HashSet<String>();
		Set<String> notfound2 = new HashSet<String>();
		for (int i = 0; i < Math.pow(uff.size(), val); i++) {
			String e = "";
			for (int k = 0; k < val; k++) {
				e += uff.get((i / (int) Math.pow(uff.size(), val - (k + 1))) % uff.size()).c;
			}

			if (aa1map.containsKey(e) || aa2map.containsKey(e)) {
				boolean b1 = aa1map.containsKey(e);
				boolean b2 = aa2map.containsKey(e);

				if (!b1) {
					if (val == 3)
						notfound.add(e);
					na1++;
				}
				if (!b2) {
					if (val == 3)
						notfound2.add(e);
					na2++;
				}

				double dval = (b1 ? aa1map.get(e) / (double) t1 : 0.0) - (b2 ? aa2map.get(e) / (double) t2 : 0.0);
				dval *= dval;
				dt += dval;
				u++;

				System.err.println(e + "\t" + (aa1map.get(e)) + "\t" + (aa2map.containsKey(e) ? (aa2map.get(e)) : "-"));
			} else {
				if (val == 3) {
					notfound.add(e);
					notfound2.add(e);
				}
				nab++;
			}
		}
		System.err.println("MSE: " + (dt / u) + " for " + val);
		System.err.println("Not found in 1: " + na1 + ", Not found in 2: " + na2 + ", found in neither: " + nab);

		for (String ns : notfound) {
			System.err.println(ns);
		}
		System.err.println();
		for (String ns : notfound2) {
			System.err.println(ns);
		}
	}

	public static void aahist(File f1, File f2) throws IOException {
		Map<Character, Long> aa1map = new HashMap<Character, Long>();
		Map<Character, Long> aa2map = new HashMap<Character, Long>();

		long t1 = 0;
		FileReader fr = new FileReader(f1);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			if (!line.startsWith(">")) {
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (aa1map.containsKey(c)) {
						aa1map.put(c, aa1map.get(c) + 1L);
					} else
						aa1map.put(c, 1L);

					t1++;
				}
			}
			line = br.readLine();
		}
		br.close();

		long t2 = 0;
		fr = new FileReader(f2);
		br = new BufferedReader(fr);
		line = br.readLine();
		while (line != null) {
			if (!line.startsWith(">")) {
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (aa2map.containsKey(c)) {
						aa2map.put(c, aa2map.get(c) + 1L);
					} else
						aa2map.put(c, 1L);

					t2++;
				}
			}
			line = br.readLine();
		}
		br.close();

		for (Erm e : isoel) {
			char c = e.c;
			if (aa1map.containsKey(c)) {
				System.err.println(e.d + "\t" + c + "\t" + (aa1map.get(c) / (double) t1) + "\t" + (aa2map.containsKey(c) ? (aa2map.get(c) / (double) t2) : "-"));
			}
		}
	}

	public static void newstuff() throws IOException {
		Map<String, Set<String>> famap = new HashMap<String, Set<String>>();
		Map<String, String> idmap = new HashMap<String, String>();
		Map<String, String> nmmap = new HashMap<String, String>();
		File f = new File("/home/sigmar/groupmap.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if (split.length > 1) {
				idmap.put(split[1], split[0]);
				nmmap.put(split[0], split[1]);

				String[] subsplit = split[0].split("_");
				Set<String> fam = null;
				if (famap.containsKey(subsplit[0])) {
					fam = famap.get(subsplit[0]);
				} else {
					fam = new HashSet<String>();
					famap.put(subsplit[0], fam);
				}
				fam.add(split[0]);
			}

			line = br.readLine();
		}
		br.close();

		Set<String> remap = new HashSet<String>();
		Set<String> almap = new HashSet<String>();
		for (String erm : famap.keySet()) {
			if (erm.startsWith("Trep") || erm.startsWith("Borr") || erm.startsWith("Spir")) {
				remap.add(erm);
				almap.addAll(famap.get(erm));
			}
		}
		for (String key : remap)
			famap.remove(key);
		famap.put("TrepSpirBorr", almap);

		for (String fam : famap.keySet()) {
			Set<String> subfam = famap.get(fam);
			System.err.println("fam: " + fam);
			for (String sf : subfam) {
				System.err.println("\tsf: " + nmmap.get(sf));
			}
		}

		f = new File("/home/sigmar/group_21.dat");
		Map<Set<String>, Set<String>> common = new HashMap<Set<String>, Set<String>>();
		/*
		 * File[] files = f.listFiles( new FilenameFilter() {
		 * 
		 * @Override public boolean accept(File dir, String name) { if(
		 * name.startsWith("group") && name.endsWith(".dat") ) { return true; }
		 * return false; } });
		 */

		Set<String> erm2 = new HashSet<String>();
		erm2.addAll(famap.get("Brachyspira"));
		erm2.addAll(famap.get("Leptospira"));

		Set<String> all = new HashSet<String>();
		br = new BufferedReader(new FileReader(f));
		line = br.readLine();
		while (line != null) {
			String[] split = line.split("[\t]+");
			if (split.length > 2) {
				Set<String> erm = new HashSet<String>();
				for (int i = 2; i < split.length; i++) {
					erm.add(idmap.get(split[i].substring(0, split[i].indexOf('.'))));
					// erm.add( split[i].substring(0, split[i].indexOf('.') ) );
				}

				Set<String> incommon = null;
				if (common.containsKey(erm)) {
					incommon = common.get(erm);
				} else {
					incommon = new HashSet<String>();
					common.put(erm, incommon);
				}
				incommon.add(line);

				if (erm.size() == 13) {
					// if( erm.containsAll(famap.get("TrepSpirBorr")) &&
					// erm.containsAll(famap.get("Leptospira")) ) {
					// if( erm.containsAll(famap.get("TrepSpirBorr")) &&
					// erm.containsAll(famap.get("Brachyspira")) ) {
					// if( erm.containsAll(famap.get("Leptospira")) ) {
					// if( erm.containsAll(famap.get("Brachyspira")) ) {
					boolean includesAllLeptos = erm.containsAll(famap.get("Leptospira"));
					boolean includesAllBrachys = erm.containsAll(famap.get("Brachyspira"));
					Set<String> ho = new HashSet<String>(erm);
					ho.removeAll(famap.get("Brachyspira"));
					ho.removeAll(famap.get("TrepSpirBorr"));
					boolean includesSomeLeptos = ho.size() > 0 && ho.size() != famap.get("Leptospira").size();
					ho = new HashSet<String>(erm);
					ho.removeAll(famap.get("Leptospira"));
					ho.removeAll(famap.get("TrepSpirBorr"));
					boolean includesSomeBrachys = ho.size() > 0 && ho.size() != famap.get("Brachyspira").size();

					if (erm.containsAll(famap.get("TrepSpirBorr"))) { // && (
																		// includesSomeBrachys
																		// ||
																		// includesSomeLeptos
																		// ) ) {
						// int start =
						// line.indexOf("5743b451ec3e92efc596500d604750ed");
						// int start =
						// line.indexOf("be1843abfce51adcaa86b07a3c6bedbb");
						// int start =
						// line.indexOf("7394569560a961ac7ffe674befec5056");
						// Set<String> ho = new TreeSet<String>( erm );
						// ho.removeAll(famap.get("TrepSpirBorr"));
						// System.err.println("erm " + ho);
						int start = line.indexOf("d719570adc9e2969b0374564745432cd");

						if (start > 0) {
							int end = line.indexOf('\t', start);
							// if( end == -1 ) end = line.indexOf('\n', start);
							if (end == -1)
								end = line.length();
							all.add(line.substring(start, end));
						} else {
							System.err.println();
						}
					}
				}

				/*
				 * if( erm.size() >= 22 ) { int start =
				 * line.indexOf("696cf959d443a23e53786f1eae8eb6c9");
				 * 
				 * if( start > 0 ) { int end = line.indexOf('\t', start); //if(
				 * end == -1 ) end = line.indexOf('\n', start); if( end == -1 )
				 * end = line.length(); all.add( line.substring(start, end) ); }
				 * else { System.err.println(); } }
				 */
			}

			line = br.readLine();
		}
		br.close();

		PrintStream ps = new PrintStream("/home/sigmar/iron5.giant");
		// System.setErr( ps );

		int count = 0;
		f = new File("/home/sigmar/21.fsa");
		br = new BufferedReader(new FileReader(f));
		line = br.readLine();
		while (line != null) {
			if (all.contains(line.substring(1))) {
				count++;
				System.err.println(line);
				line = br.readLine();
				while (line != null && !line.startsWith(">")) {
					System.err.println(line);
					line = br.readLine();
				}
			} else
				line = br.readLine();
		}
		br.close();

		System.err.println("hey: " + count);

		int total = 0;
		System.err.println("total groups " + common.size());
		for (Set<String> keycommon : common.keySet()) {
			Set<String> incommon = common.get(keycommon);
			System.err.println(incommon.size() + "  " + keycommon.size() + "  " + keycommon);
			total += incommon.size();
		}
		System.err.println(total);
		System.err.println("");

		total = 0;
		System.err.println("boundary crossing groups");
		for (Set<String> keycommon : common.keySet()) {
			Set<String> incommon = common.get(keycommon);

			boolean s = true;
			for (String fam : famap.keySet()) {
				Set<String> famset = famap.get(fam);
				if (famset.containsAll(keycommon)) {
					s = false;
					break;
				}
			}
			if (s) {
				System.err.println(incommon.size() + "  " + keycommon.size() + "  " + keycommon);
				total++;
			}
		}
		System.err.println("for the total of " + total);

		/*
		 * System.err.println( all.size() ); for( String astr : all ) {
		 * System.err.println( astr ); }
		 */

		ps.close();
	}

	static class Pepbindaff implements Comparable<Pepbindaff> {
		String pep;
		double aff;

		public Pepbindaff(String pep, String aff) {
			this.pep = pep;
			this.aff = Double.parseDouble(aff);
		}

		public Pepbindaff(String pep, double aff) {
			this.pep = pep;
			this.aff = aff;
		}

		@Override
		public int compareTo(Pepbindaff o) {
			return aff > o.aff ? 1 : (aff < o.aff ? -1 : 0);
		}
	}

	public static void pearsons(Map<Character, Double> what, List<Pepbindaff> peppi) {
		double sums[] = new double[peppi.get(0).pep.length()];
		Arrays.fill(sums, 0.0);
		for (Pepbindaff paff : peppi) {
			for (int i = 0; i < paff.pep.length(); i++) {
				char c = paff.pep.charAt(i);
				sums[i] += what.get(c);
			}
		}
		for (int i = 0; i < peppi.get(0).pep.length(); i++) {
			sums[i] /= peppi.size();
		}

		for (int i = 0; i < peppi.get(0).pep.length(); i++) {
			for (int j = i; j < peppi.get(0).pep.length(); j++) {
				double t = 0.0;
				double nx = 0.0;
				double ny = 0.0;
				for (Pepbindaff paff : peppi) {
					char c = paff.pep.charAt(j);
					char ct = paff.pep.charAt(i);
					double h = what.get(c) - sums[j];
					double ht = what.get(ct) - sums[i];
					t += h * ht;
					nx += h * h;
					ny += ht * ht;
				}
				double xy = nx * ny;
				double val = (xy == 0 ? 0.0 : (t / Math.sqrt(xy)));
				if (Math.abs(val) > 0.1 || i == j)
					System.err.println("Pearson (" + i + " " + j + "): " + val);
			}
		}
	}

	public static void kendaltau(List<Pepbindaff> peppi) {
		int size = peppi.get(0).pep.length();
		List<Pepbindaff> erm = new ArrayList<Pepbindaff>();
		for (int x = 0; x < size - 1; x++) {
			for (int y = x + 1; y < size; y++) {
				int con = 0;
				int dis = 0;
				for (int i = 0; i < peppi.size(); i++) {
					for (int j = i + 1; j < peppi.size(); j++) {
						char xi = peppi.get(i).pep.charAt(x);
						char xj = peppi.get(j).pep.charAt(x);
						char yi = peppi.get(i).pep.charAt(y);
						char yj = peppi.get(j).pep.charAt(y);

						if ((xi > xj && yi > yj) || (xi < xj && yi < yj))
							con++;
						else if (xi > xj && yi < yj || xi < xj && yi > yj)
							dis++;
					}
				}
				double kt = (double) (2 * (con - dis)) / (double) (peppi.size() * (peppi.size() - 1));
				erm.add(new Pepbindaff("kt " + x + " " + y + ": ", kt));
				// System.err.println( "kt "+i+" "+j+": "+kt );
			}
		}
		Collections.sort(erm);
		for (Pepbindaff p : erm) {
			System.err.println(p.pep + " " + p.aff);
		}
	}

	public static void algoinbio() throws IOException {
		FileReader fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/A0101/A0101.dat");
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		Map<String, Double> pepaff = new TreeMap<String, Double>();
		List<Pepbindaff> peppi = new ArrayList<Pepbindaff>();
		while (line != null) {
			String[] split = line.trim().split("[\t ]+");
			peppi.add(new Pepbindaff(split[0], split[1]));

			line = br.readLine();
		}
		br.close();

		Collections.sort(peppi);

		kendaltau(peppi);
		// pearsons( hydropathyindex, peppi );

		/*
		 * for( Pepbindaff paff : peppi ) { System.err.print( paff.pep ); for(
		 * int i = 0; i < paff.pep.length(); i++ ) { char c1 =
		 * paff.pep.charAt(i); //char c2 = paff.pep.charAt(i+1);
		 * //System.err.print(
		 * "\t"+Math.min(hydropathyindex.get(c1),hydropathyindex.get(c2) ) );
		 * //System.err.print( "\t"+sidechaincharge.get(c) ); System.err.print(
		 * "\t"+isoelectricpoint.get(c1) ); //System.err.print(
		 * "\t"+Math.min(isoelectricpoint.get(c1),isoelectricpoint.get(c2)) );
		 * //System.err.print( "\t"+sidechainpolarity.get(c) ); }
		 * System.err.println( "\t"+paff.aff ); }
		 * 
		 * /*double[] hyp = new double[9]; double[] chr = new double[9];
		 * double[] iso = new double[9]; double[] pol = new double[9];
		 * Arrays.fill(hyp, 0.0); Arrays.fill(chr, 0.0); Arrays.fill(iso, 0.0);
		 * Arrays.fill(pol, 0.0); int count = 0; while( line != null ) {
		 * String[] split = line.split("[\t ]+"); String pep = split[0]; double
		 * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
		 * i++ ) { char c = pep.charAt(i);
		 * 
		 * hyp[i] += val*hydropathyindex.get(c); chr[i] +=
		 * val*sidechaincharge.get(c); iso[i] += val*isoelectricpoint.get(c);
		 * pol[i] += val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0); }
		 * count++; line = br.readLine(); } br.close();
		 * 
		 * /* Lowpoint
		 */

		/*
		 * fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/f000");
		 * br = new BufferedReader( fr ); line = br.readLine();
		 * 
		 * double[] hype = new double[9]; double[] chre = new double[9];
		 * double[] isoe = new double[9]; double[] pole = new double[9];
		 * Arrays.fill(hype, 0.0); Arrays.fill(chre, 0.0); Arrays.fill(isoe,
		 * 0.0); Arrays.fill(pole, 0.0); double hypmax = Double.MIN_VALUE;
		 * double chrmax = Double.MIN_VALUE; double isomax = Double.MIN_VALUE;
		 * double polmax = Double.MIN_VALUE; while( line != null ) { double hypt
		 * = 0.0; double chrt = 0.0; double isot = 0.0; double polt = 0.0;
		 * 
		 * String[] split = line.split("[\t ]+"); String pep = split[0]; double
		 * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
		 * i++ ) { char c = pep.charAt(i);
		 * 
		 * double d = val*hydropathyindex.get(c) - hyp[i]/count; hype[i] += d*d;
		 * d = val*sidechaincharge.get(c) - chr[i]/count; chre[i] += d*d; d =
		 * val*isoelectricpoint.get(c) - iso[i]/count; isoe[i] += d*d; d =
		 * val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i]/count;
		 * pole[i] += d*d;
		 * 
		 * d = count*hydropathyindex.get(c) - hyp[i]; hypt += d*d; d =
		 * count*sidechaincharge.get(c) - chr[i]; chrt += d*d; d =
		 * count*isoelectricpoint.get(c) - iso[i]; isot += d*d; d =
		 * count*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i]; polt
		 * += d*d; }
		 * 
		 * hypt /= hyp.length; chrt /= hyp.length; isot /= hyp.length; polt /=
		 * hyp.length;
		 * 
		 * //hypsum += hypt; if( hypt > hypmax ) hypmax = hypt; if( chrt >
		 * chrmax ) chrmax = chrt; if( isot > isomax ) isomax = isot; if( polt >
		 * polmax ) polmax = polt;
		 * 
		 * line = br.readLine(); } br.close();
		 * 
		 * for( int i = 0; i < hyp.length; i++ ) { hype[i] /= Math.abs(hyp[i]);
		 * chre[i] /= Math.abs(chr[i]); isoe[i] /= Math.abs(iso[i]); pole[i] /=
		 * Math.abs(pol[i]);
		 * 
		 * System.err.println( "pos " + i + " " + hype[i] + " " + chre[i] + " "
		 * + isoe[i] + " " + pole[i] ); }
		 * 
		 * /*********
		 * 
		 * fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week11/c000");
		 * br = new BufferedReader( fr ); line = br.readLine();
		 * 
		 * List<Double> hyplist = new ArrayList<Double>(); List<Double> hyptlist
		 * = new ArrayList<Double>(); double hyptsum = 0.0; double hypsum = 0.0;
		 * while( line != null ) { double hypt = 0.0; double chrt = 0.0; double
		 * isot = 0.0; double polt = 0.0;
		 * 
		 * String[] split = line.split("[\t ]+"); String pep = split[0]; double
		 * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
		 * i++ ) { char c = pep.charAt(i);
		 * 
		 * double d = count*hydropathyindex.get(c) - hyp[i]; hypt +=
		 * d*d*(1.0-hype[i]); d = count*sidechaincharge.get(c) - chr[i]; chrt +=
		 * d*d*(1.0-chre[i]*100); d = count*isoelectricpoint.get(c) - iso[i];
		 * isot += d*d*(1.0-isoe[i]); d = count*(sidechainpolarity.get(c) == 'p'
		 * ? 1.0 : -1.0) - pol[i]; polt += d*d*(1.0-pole[i]); }
		 * 
		 * hypt /= hyp.length; chrt /= hyp.length; isot /= hyp.length; polt /=
		 * hyp.length;
		 * 
		 * double calc = (hypmax - hypt)/hypmax; //double calc = (chrmax -
		 * chrt)/chrmax; hypsum += val; hyptsum += calc; hyplist.add(val);
		 * hyptlist.add( calc );
		 * 
		 * System.err.println( calc + "  " + val );
		 * 
		 * line = br.readLine(); } br.close();
		 * 
		 * double hypmed = hypsum/hyplist.size(); double hyptmed =
		 * hyptsum/hyptlist.size(); double t = 0.0; double nx = 0.0; double ny =
		 * 0.0; for( int i = 0; i < hyplist.size(); i++ ) { double h =
		 * hyplist.get(i) - hypmed; double ht = hyptlist.get(i) - hyptmed; t +=
		 * h*ht; nx += h*h; ny += ht*ht; }
		 * 
		 * double xy = nx * ny; System.err.println( "Pearson: " + (xy == 0 ? 0.0
		 * : (t/Math.sqrt(xy))) );
		 * 
		 * /*System.err.println( "hyp" ); for( double d : hyp ) {
		 * System.err.println( d ); } System.err.println( "chr" ); for( double d
		 * : chr ) { System.err.println( d ); } System.err.println( "iso" );
		 * for( double d : iso ) { System.err.println( d ); }
		 * System.err.println( "pol" ); for( double d : pol ) {
		 * System.err.println( d ); }
		 */
	}

	public static void blastparse(String fn) throws IOException {
		Set<String> set = new HashSet<String>();
		FileReader fr = new FileReader(fn);
		BufferedReader br = new BufferedReader(fr);
		String query = null;
		String evalue = null;
		String line = br.readLine();
		int count = 0;
		while (line != null) {
			String trim = line.trim();
			if (query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg"))) {
				// String[] split = trim.split("\\|");
				set.add(trim + "\t" + evalue);

				/*
				 * if( !allgenes.containsKey( split[1] ) || allgenes.get(
				 * split[1] ) == null ) { allgenes.put( split[1], split.length >
				 * 1 ? split[2].trim() : null ); }
				 * 
				 * /*Set<String> locset = null; if( geneloc.containsKey(
				 * split[1] ) ) { locset = geneloc.get(split[1]); } else {
				 * locset = new HashSet<String>(); geneloc.put(split[1],
				 * locset); } locset.add( query + " " + evalue );
				 */

				query = null;
				evalue = null;
			} else if (trim.startsWith("Query=")) {
				query = trim.substring(6).trim().split("[ ]+")[0];
			} else if (evalue == null && query != null
					&& (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|"))) {
				String[] split = trim.split("[\t ]+");
				evalue = split[split.length - 1];
			}
			count++;
			line = br.readLine();
		}

		System.err.println(count);

		Map<String, Set<String>> mapset = new HashMap<String, Set<String>>();
		for (String gene : set) {
			if (gene.contains("ribosomal")) {
				Set<String> subset = null;
				if (mapset.containsKey("ribosomal proteins")) {
					subset = mapset.get("ribosomal proteins");
				} else {
					subset = new TreeSet<String>();
					mapset.put("ribosomal proteins", subset);
				}
				subset.add(gene);
			} else if (gene.contains("inase")) {
				Set<String> subset = null;
				if (mapset.containsKey("inase")) {
					subset = mapset.get("inase");
				} else {
					subset = new TreeSet<String>();
					mapset.put("inase", subset);
				}
				subset.add(gene);
			} else if (gene.contains("flag")) {
				Set<String> subset = null;
				if (mapset.containsKey("flag")) {
					subset = mapset.get("flag");
				} else {
					subset = new TreeSet<String>();
					mapset.put("flag", subset);
				}
				subset.add(gene);
			} else if (gene.contains("ATP")) {
				Set<String> subset = null;
				if (mapset.containsKey("ATP")) {
					subset = mapset.get("ATP");
				} else {
					subset = new TreeSet<String>();
					mapset.put("ATP", subset);
				}
				subset.add(gene);
			} else if (gene.contains("hypot")) {
				Set<String> subset = null;
				if (mapset.containsKey("hypot")) {
					subset = mapset.get("hypot");
				} else {
					subset = new TreeSet<String>();
					mapset.put("hypot", subset);
				}
				subset.add(gene);
			} else {
				Set<String> subset = null;
				if (mapset.containsKey("other")) {
					subset = mapset.get("other");
				} else {
					subset = new TreeSet<String>();
					mapset.put("other", subset);
				}
				subset.add(gene);
			}
		}

		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setErr(ps);
		for (String genegroup : mapset.keySet()) {
			Set<String> subset = mapset.get(genegroup);
			System.err.println(genegroup + "   " + subset.size());
			for (String gene : subset) {
				System.err.println("\t" + gene);
			}
		}
		ps.close();
	}

	static class Function {
		public Function() {}
		public Function( String go ) { this.go = go; }

		String go;
		String ec;
		String metacyc;
		String kegg;
		String name;
		String namespace;
		String desc;
		Set<String> isa;
		Set<String> subset;
		Set<Gene> geneentries;
		int index;
		
		public String getName() {
			return name;
		}
		
		public String getNamespace() {
			return namespace;
		}
		
		@Override
		public String toString() {
			return name;
		}
	};

	static boolean locsort = true;

	static class Tegeval implements Comparable<Tegeval> {
		public Tegeval(Gene gene, String tegund, double evalue, StringBuilder sequence, StringBuilder dnaseq, String contig, Contig shortcontig, String locontig, int sta, int sto, int orient) {
			teg = tegund;
			eval = evalue;
			dna = dnaseq;
			cont = contig;
			contshort = shortcontig;
			contloc = locontig;
			start = sta;
			stop = sto;
			ori = orient;
			this.gene = gene;

			if( dna != null ) gc = (double)gcCount()/(double)dna.length();
			else gc = -1.0;
			
			numCys = 0;
			setSequence(sequence);
		}
		
		public String getSpecies() {
			return teg;
		}
		
		public int getLength() {
			return stop - start;
		}
		
		public int getProteinLength() {
			return getLength()/3;
		}
		
		public Contig getContshort() {
			return contshort;
		}
		
		public String getContloc() {
			return contloc;
		}
		
		public String getContig() {
			return cont;
		}
		
		public Tegeval getNext() {
			if( contshort != null ) return contshort.isReverse() ? prev : next;
			return null;
		}
		
		public Tegeval getPrevious() {
			if( contshort != null )return contshort.isReverse() ? next : prev;
			return null;
		}
		
		public Tegeval setNext( Tegeval next ) {
			Tegeval old = this.next;
			this.next = next;
			return old;
		}
		
		public Tegeval setPrevious( Tegeval prev ) {
			Tegeval old = this.prev;
			this.prev = prev;
			prev.setNext( this );
			return old;
		}
		
		private double gcCount() {
			int gc = 0;
			for( int i = 0; i < dna.length(); i++ ) {
				char c = dna.charAt(i);
				if( c == 'g' || c == 'G' || c == 'c' || c == 'C' ) gc++;
			}
			return gc;
		}
		
		public double getGCPerc() {
			return gc;
		}

		double			gc;
		String 			teg;
		double 			eval;
		String 			cont;
		Contig 			contshort;
		String 			contloc;
		StringBuilder 	seq;
		StringBuilder 	dna;
		int 			start;
		int 			stop;
		int 			ori;
		int 			numCys;
		Gene			gene;
		Tegeval			next;
		Tegeval			prev;
		boolean			selected = false;
		
		public boolean isSelected() {
			return selected;
		}
		
		public void setSelected( boolean sel ) {
			this.selected = sel;
		}
		
		public void setGene( Gene gene ) {
			this.gene = gene;
		}
		
		public Gene getGene() {
			return this.gene;
		}

		public void setSequence(StringBuilder seq) {
			if (seq != null) {
				for (int i = 0; i < seq.length(); i++) {
					char c = (char) seq.charAt(i);
					if (c == 'C' || c == 'c')
						numCys++;
				}
				this.seq = seq;
			}
		}

		public String toString() {
			return eval + " " + contloc;
		}

		@Override
		public int compareTo(Tegeval o) {
			if (locsort) {
				int ret = contshort.compareTo(o.contshort);
				/*
				 * if( o.contshort != null || o.contshort.length() < 2 ) { ret =
				 * contshort.compareTo(o.contshort); } else {
				 * System.err.println(); }
				 */
				return ret == 0 ? start - o.start : ret;
			} else {
				int comp = Double.compare(eval, o.eval);
				return comp == 0 ? teg.compareTo(o.teg) : comp;
			}
		}
	}

	static class NullComparators {
		static <T> Comparator<T> atEnd(final Comparator<T> comparator) {
			return new Comparator<T>() {

				public int compare(T o1, T o2) {
					if (o1 == null && o2 == null) {
						return 0;
					}

					if (o1 == null) {
						return 1;
					}

					if (o2 == null) {
						return -1;
					}

					return comparator.compare(o1, o2);
				}
			};
		}

		static <T> Comparator<T> atBeginning(final Comparator<T> comparator) {
			return Collections.reverseOrder(atEnd(comparator));
		}
	}

	static class Teginfo implements Comparable<Teginfo> {
		String tegund;
		Set<Tegeval> tset;
		Tegeval best;

		public void add(Tegeval tv) {
			if (tset == null)
				tset = new HashSet<Tegeval>();
			tset.add(tv);
			if (best == null || tv.eval < best.eval)
				best = tv;
		}

		public String toString() {
			String ret = best.toString();
			for (Tegeval tv : tset) {
				if (tv != best)
					ret += " " + tv.toString();
			}
			return ret;
		}

		@Override
		public int compareTo(Teginfo o) {
			return best.compareTo(o.best);
		}
	}

	public static class GeneGroup {
		Set<Gene>	genes = new HashSet<Gene>();
		Set<String>	species = new HashSet<String>();
		int 		groupIndex = -10;
		int 		groupCount = -1;
		//int			groupGeneCount;
		
		public List<Tegeval> getTegevals( Set<String> sortspecies ) {
			List<Tegeval>	ltv = new ArrayList<Tegeval>();
			
			for( String sp : sortspecies )
			/*for( Gene g : genes ) {
				Teginfo stv = g.species.get(sp);
				if( stv == null ) {
					//System.err.println( sp );
				} else {
					for (Tegeval tv : stv.tset) {
						ltv.add( tv );
					}
				}
			}*/
				ltv.addAll( getTegevals( sp ) );
			
			return ltv;
		}
		
		public List<Tegeval> getTegevals( String species ) {
			List<Tegeval>	ltv = new ArrayList<Tegeval>();
			
			for( Gene g : genes ) {
				Teginfo stv = g.species.get(species);
				if( stv == null ) {
					//System.err.println( sp );
				} else {
					for (Tegeval tv : stv.tset) {
						ltv.add( tv );
					}
				}
			}
			
			return ltv;
		}
		
		public List<Tegeval> getTegevals() {
			List<Tegeval>	ltv = new ArrayList<Tegeval>();
			
			for( Gene g : genes ) {
				for( String species : g.species.keySet() ) {
					Teginfo stv = g.species.get(species);
					if( stv == null ) {
						//System.err.println( sp );
					} else {
						for (Tegeval tv : stv.tset) {
							ltv.add( tv );
						}
					}
				}
			}
			
			return ltv;
		}
		
		public double getAvgGCPerc() {
			double gc = 0.0;
			int count = 0;
			for( Gene g : genes ) {
				for( String spec : g.species.keySet() ) {
					Teginfo ti = g.species.get(spec);
					for( Tegeval te : ti.tset ) {
						gc += te.getGCPerc();
						count++;
					}
				}
			}
			return gc/count;
		}
		
		public double getStddevGCPerc( double avggc ) {
			double gc = 0.0;
			int count = 0;
			for( Gene g : genes ) {
				for( String spec : g.species.keySet() ) {
					Teginfo ti = g.species.get(spec);
					for( Tegeval te : ti.tset ) {
						double val = te.getGCPerc()-avggc;
						gc += val*val;
						count++;
					}
				}
			}
			return Math.sqrt(gc/count);
		}
		
		public Set<Function> getFunctions() {
			Set<Function>	funcset = new HashSet<Function>();
			for( Gene g : genes ) {
				if( g.funcentries != null && g.funcentries.size() > 0 ) {
					for( Function f : g.funcentries ) {
						//Function f = funcmap.get( go );
						funcset.add( f );
					}
				}
			}
			return funcset;
		}
		
		public String getCommonFunction( boolean breakb, Set<Function> allowedFunctions ) {
			String ret = "";
			for( Gene g : genes ) {
				if( g.funcentries != null && g.funcentries.size() > 0 ) {
					for( Function f : g.funcentries ) {
						//Function f = funcmap.get( go );
						
						if( allowedFunctions == null || allowedFunctions.contains(f) ) {
							String name = f.getName().replace('/', '-').replace(",", "");
								
							//System.err.println( g.getName() + "  " + go );
							if( ret.length() == 0 ) ret += name;
							else ret += ","+name;
						}
					}
					if( breakb ) break;
				}
			}
			return ret;
		}
		
		public String getCommonNamespace() {
			String ret = "";
			Set<String>	included = new HashSet<String>();
			for( Gene g : genes ) {
				if( g.funcentries != null ) for( Function f : g.funcentries ) {
					//Function f = funcmap.get( go );
					String namespace = f.getNamespace();
					//System.err.println( g.getName() + "  " + go );
					if( !included.contains(namespace) ) {
						if( ret.length() == 0 ) ret += namespace;
						else ret += ","+namespace;
						included.add(namespace);
					}
				}
			}
			return ret;
		}
		
		public String getCommonName() {
			String ret = null;
			for( Gene g : genes ) {
				if( ret == null || !(g.getName().contains("unnamed") || g.getName().contains("hypot")) ) ret = g.getName();
			}
			return ret;
		}
		
		public String getKeggid() {
			String ret = null;
			for( Gene g : genes ) {
				if( g.keggid != null ) ret = g.keggid;
			}
			return ret;
		}
		
		public Set<String> getSpecies() {
			return species;
		}
		
		public boolean isSingluar() {
			return this.getGroupCount() == this.getGroupCoverage();
		}
		
		public void addGene( Gene gene ) {
			if( gene.getGeneGroup() != this ) gene.setGeneGroup( this );
			else genes.add( gene );
		}
		
		public void addSpecies( Set<String> species ) {
			this.species.addAll( species );
		}
		
		public GeneGroup( int i ) {
			this.groupIndex = i;
		}
		
		public int getGroupCoverage() {
			return this.species.size();
		}
		
		public void setGroupCount( int count ) {
			this.groupCount = count;
		}
		
		public int getGroupCount() {
			if( groupCount == -1 ) {
				int val = 0;
				for (Gene g : genes) {
					if (g.species != null) {
						for (String str : g.species.keySet()) {
							val += g.species.get(str).tset.size();
						}
					}
				}
				this.groupCount = val;
			}
			return this.groupCount;
		}
		
		public int getGroupGeneCount() {
			return this.genes.size();//this.groupGeneCount;
		}
	};
	
	public static class Gene {
		public Gene(GeneGroup gg, String name, String origin) {
			this.name = name;
			this.origin = origin;
			this.gg = gg;
			// this.setAa( aa );
			
			//groupIdx = -10;
		}

		public void setAa(String aa) {
			if (aa != null) {
				this.aac = aa;
			}
		}

		public String getAa() {
			return aac;
		}
		
		public void setGeneGroup( GeneGroup gg ) {
			this.gg = gg;
			
			gg.addGene( this );
			//gg.addSpecies( this.species );	
		}
		
		public GeneGroup getGeneGroup() {
			return gg;
		}
		
		public int getGroupIndex() {
			if( gg != null ) return gg.groupIndex;
			return -10;
		}
		
		public int getGroupCoverage() {
			if( gg != null ) return gg.getGroupCoverage();
			return -1;
		}
		
		public int getGroupCount() {
			if( gg != null ) return gg.getGroupCount();
			return -1;
		}
		
		public int getGroupGenCount() {
			if( gg != null ) return gg.getGroupGeneCount();
			return -1;
		}
		
		public String getName() {
			return name;
		}
		
		public double getAvgGCPerc() {
			double gc = 0.0;
			int count = 0;
			for( String spec : species.keySet() ) {
				Teginfo ti = species.get(spec);
				for( Tegeval te : ti.tset ) {
					gc += te.getGCPerc();
					count++;
				}
			}
			return gc/count;
		}
		
		public double getStddevGCPerc( double avggc ) {
			double gc = 0.0;
			int count = 0;
			for( String spec : species.keySet() ) {
				Teginfo ti = species.get(spec);
				for( Tegeval te : ti.tset ) {
					double val = te.getGCPerc()-avggc;
					gc += val*val;
					count++;
				}
			}
			return Math.sqrt(gc/count);
		}
		
		public void addTegeval( Tegeval tegeval ) {
			Teginfo ti;
			if( species == null ) species = new HashMap<String,Teginfo>();
			if( species.containsKey( tegeval.teg ) ) {
				ti = species.get( tegeval.teg );
			} else {
				ti = new Teginfo();
				species.put( tegeval.teg, ti );
			}
			ti.add( tegeval );
		}

		String name;
		String origin;
		String refid;
		Set<String> allids;
		String genid;
		String uniid;
		String keggid;
		String pdbid;
		String blastspec;
		Set<Function> funcentries;
		Map<String, Teginfo> species;
		private String aac;
		int index;

		GeneGroup	gg;
		// Set<String> group;
		//int groupGenCount;
		//int groupCoverage;
		//int groupIdx;
		//int groupCount;
		double corr16s;
		double[] corrarr;

		double proximityGroupPreservation;
	};

	public static void splitGenes(String dir, String filename) throws IOException {
		Map<String, List<Gene>> genemap = new HashMap<String, List<Gene>>();
		File f = new File(dir, filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String last = null;
		// String aa = "";
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				if (last != null) {
					String strain = last.split("_")[0].substring(1);
					List<Gene> genelist = null;
					if (genemap.containsKey(strain)) {
						genelist = genemap.get(strain);
					} else {
						genelist = new ArrayList<Gene>();
						genemap.put(strain, genelist);
					}
					genelist.add(new Gene(null, last, "mool"));
				}
				last = line + "\n";
				// aa = "";
			}/*
			 * else { aa += line+"\n"; }
			 */
			line = br.readLine();
		}
		String strain = last.split("_")[0].substring(1);
		List<Gene> genelist = null;
		if (genemap.containsKey(strain)) {
			genelist = genemap.get(strain);
		} else {
			genelist = new ArrayList<Gene>();
			genemap.put(strain, genelist);
		}
		genelist.add(new Gene(null, last, "moool"));
		br.close();

		for (String str : genemap.keySet()) {
			f = new File(dir, str + ".orf.fsa");
			FileWriter fw = new FileWriter(f);
			List<Gene> glist = genemap.get(str);
			for (int i = 0; i < glist.size(); i++) {
				Gene g = glist.get(i);
				fw.write(g.name);
				fw.write(g.getAa());
			}
			fw.close();
		}
	}

	public static void splitGenes(String dir, String filename, int parts) throws IOException {
		List<Gene> genelist = new ArrayList<Gene>();
		File f = new File(dir, filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String last = null;
		String aa = "";
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				if (last != null) {
					Gene g = new Gene(null, last, "mool");
					g.setAa(aa);
					genelist.add(g);
				}
				last = line + "\n";
				aa = "";
			} else {
				aa += line + "\n";
			}
			line = br.readLine();
		}
		Gene g = new Gene(null, last, "mool");
		g.setAa(aa);
		genelist.add(g);
		br.close();

		int k = 0;
		int chunk = genelist.size() / parts + 1;
		System.err.println("Number of genes " + genelist.size() + " chunk size " + chunk);
		FileWriter fw = null;
		for (int i = 0; i < genelist.size(); i++) {
			g = genelist.get(i);
			if (i % chunk == 0) {
				f = new File(dir, filename.substring(0, filename.lastIndexOf('.')) + "_" + (k++) + ".aa");
				if (fw != null)
					fw.close();
				fw = new FileWriter(f);
			}

			fw.write(g.name);
			fw.write(g.getAa());
		}
		fw.close();
	}

	public static void aaset() throws IOException {
		Set<String> set1 = new HashSet<String>();
		File fa = new File("/home/sigmar/dtu/27623-AlgoInBio/week7/");
		File[] ff = fa.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.length() == 5)
					return true;
				return false;
			}
		});

		for (File fb : ff) {
			File f = new File(fb, fb.getName() + ".dat");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				while (line != null) {
					String[] s = line.split("[\t ]+");
					set1.add(s[0]);

					line = br.readLine();
				}
				br.close();

				Set<String> set2 = new HashSet<String>();
				f = new File("/home/sigmar/dtu/27623-AlgoInBio/project/train2.dat");
				br = new BufferedReader(new FileReader(f));
				line = br.readLine();
				while (line != null) {
					String[] s = line.split("[\t ]+");
					set2.add(s[0]);

					line = br.readLine();
				}
				br.close();

				int s1 = set1.size();
				int s2 = set2.size();
				set1.removeAll(set2);
				int ns1 = set1.size();

				if (s1 != ns1) {
					System.err.println(fb.getName());
					System.err.println("\t" + s1);
					System.err.println("\t" + s2);
					System.err.println("\t" + ns1);
				}
			}
		}
	}

	public static void newsets() throws IOException {
		File mf = new File("/home/sigmar/dtu/new/dtu/main_project/code/SMM/");
		File[] ff = mf.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				if (name.length() == 5 && (name.startsWith("B") || name.startsWith("A")) && pathname.isDirectory())
					return true;
				return false;
			}
		});

		for (File f : ff) {
			for (int x = 0; x < 5; x++) {
				for (int y = x + 1; y < 5; y++) {
					FileWriter fw = new FileWriter(new File(f, "f00_" + x + "_" + y));
					for (int u = 0; u < 5; u++) {
						if (u != x && u != y) {
							FileReader fr = new FileReader(new File(f, "c00" + u));
							BufferedReader br = new BufferedReader(fr);
							String line = br.readLine();
							while (line != null) {
								fw.write(line + "\n");
								line = br.readLine();
							}
						}
					}
					fw.close();
				}
			}
		}
	}

	/*
	 * public static void blastJoin( String name ) throws IOException {
	 * FileReader fr = new FileReader( name ); BufferedReader br = new
	 * BufferedReader( fr ); String line = br.readLine();
	 * 
	 * Map<String,Map<String,Set<String>>> specmap = new
	 * HashMap<String,Map<String,Set<String>>>();
	 * 
	 * String stuff = null; String subject = null; String length = null; String
	 * start = null; String stop = null; String score = null; String strand =
	 * null;
	 * 
	 * String thespec = null; while( line != null ) { if(
	 * line.startsWith("Query=") ) { if( subject != null ) { String inspec =
	 * subject.substring(0, subject.indexOf('_')); String spec =
	 * stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
	 * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
	 * contigmap = specmap.get(spec); } else { contigmap = new
	 * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
	 * 
	 * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
	 * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
	 * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
	 * + stop + " " + score + " " + strand ); }
	 * 
	 * subject = null; } stuff = line.substring(7).trim(); if( thespec == null )
	 * thespec = stuff.split("_")[0]; } else if( line.startsWith("Length=") ) {
	 * length = line; } else if( line.startsWith(">") ) { if( subject != null )
	 * { String inspec = subject.substring(0, subject.indexOf('_')); String spec
	 * = stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
	 * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
	 * contigmap = specmap.get(spec); } else { contigmap = new
	 * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
	 * 
	 * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
	 * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
	 * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
	 * + stop + " " + score + " " + strand ); } } length = null; start = null;
	 * stop = null; subject = line.substring(1).trim(); } else if(
	 * line.startsWith("Sbjct") ) { String[] split = line.split("[\t ]+"); if(
	 * start == null ) start = split[1]; stop = split[split.length-1]; } else
	 * if( length == null && subject != null ) { subject += line; } else if(
	 * line.startsWith(" Score") ) { if( start != null ) { String inspec =
	 * subject.substring(0, subject.indexOf('_')); String spec =
	 * stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
	 * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
	 * contigmap = specmap.get(spec); } else { contigmap = new
	 * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
	 * 
	 * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
	 * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
	 * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
	 * + stop + " " + score + " " + strand ); } } score = line; start = null;
	 * stop = null; } else if( line.startsWith(" Strand") ) { strand = line; }
	 * 
	 * line = br.readLine(); } fr.close();
	 * 
	 * for( String spec : specmap.keySet() ) { if( spec.contains( thespec ) ) {
	 * System.out.println( spec );
	 * 
	 * List<List<String>> sortorder = new ArrayList<List<String>>();
	 * 
	 * Map<List<String>,List<Integer>> joinMap = new
	 * HashMap<List<String>,List<Integer>>(); Map<String,Set<String>> contigmap
	 * = specmap.get(spec); for( String contig : contigmap.keySet() ) {
	 * Set<String> hitmap = contigmap.get(contig); List<String> hitlist = new
	 * ArrayList<String>( hitmap ); hitmap.clear(); for( int i = 0; i <
	 * hitlist.size(); i++ ) { for( int x = i+1; x < hitlist.size(); x++ ) {
	 * String str1 = hitlist.get(i); String str2 = hitlist.get(x);
	 * 
	 * boolean left1 = str1.contains("left"); boolean left2 =
	 * str2.contains("left"); boolean minus1 = str1.contains("Minus"); boolean
	 * minus2 = str2.contains("Minus"); boolean all1 = str1.contains("all");
	 * boolean all2 = str2.contains("all");
	 * 
	 * if( (all1 || all2) || (left1 != left2 && minus1 == minus2) || (left1 ==
	 * left2 && minus1 != minus2) ) { String[] split1 = str1.split("[\t ]+");
	 * String[] split2 = str2.split("[\t ]+");
	 * 
	 * int start1 = Integer.parseInt( split1[1] ); int stop1 = Integer.parseInt(
	 * split1[2] ); int start2 = Integer.parseInt( split2[1] ); int stop2 =
	 * Integer.parseInt( split2[2] );
	 * 
	 * if( start1 > stop1 ) { int tmp = start1; start1 = stop1; stop1 = tmp; }
	 * 
	 * if( start2 > stop2 ) { int tmp = start2; start2 = stop2; stop2 = tmp; }
	 * 
	 * if( (stop2-start2 > 50) && (stop1-start1 > 50) && ((start2 > start1-150
	 * && start2 < stop1+150) || (stop2 > start1-150 && stop2 < stop1+150)) ) {
	 * hitmap.add( str1 ); hitmap.add( str2 );
	 * 
	 * int ind1 = str1.indexOf("_left"); if( ind1 == -1 ) ind1 =
	 * str1.indexOf("_right"); if( ind1 == -1 ) ind1 = str1.indexOf("_all");
	 * String str1simple = str1.substring(0,ind1); String str1compl =
	 * str1.substring(0, str1.indexOf(' ', ind1));
	 * 
	 * int ind2 = str2.indexOf("_left"); if( ind2 == -1 ) ind2 =
	 * str2.indexOf("_right"); if( ind2 == -1 ) ind2 = str2.indexOf("_all");
	 * String str2simple = str2.substring(0,ind2); String str2compl =
	 * str2.substring(0, str2.indexOf(' ', ind2));
	 * 
	 * /*if( minus1 != minus2 ) { if( str1compl.compareTo( str2compl ) > 0 ) {
	 * str1compl += " Minus"; } else str2compl += " Minus"; }*
	 * 
	 * if( !str2simple.equals(str1simple) ) { List<String> joinset = new
	 * ArrayList<String>(); joinset.add( str1compl ); joinset.add( str2compl );
	 * Collections.sort( joinset ); if( minus1 != minus2 ) joinset.set(1,
	 * joinset.get(1)+" Minus");
	 * 
	 * if( joinMap.containsKey( joinset ) ) { List<Integer> li =
	 * joinMap.get(joinset); li.add( Integer.parseInt(score) ); } else {
	 * List<Integer> li = new ArrayList<Integer>(); li.add(
	 * Integer.parseInt(score) ); joinMap.put( joinset, li ); } } } } } } if(
	 * hitmap.size() > 1 ) { System.out.println( "\t"+contig ); for( String hit
	 * : hitmap ) { System.out.println( "\t\t"+hit ); } } }
	 * 
	 * System.out.println("Printing join count");
	 * Map<Integer,List<List<String>>> reverseset = new
	 * TreeMap<Integer,List<List<String>>>( Collections.reverseOrder() ); for(
	 * List<String> joinset : joinMap.keySet() ) { int cnt =
	 * joinMap.get(joinset).size();
	 * 
	 * if( joinset.get(0).contains("all") || joinset.get(1).contains("all") )
	 * cnt -= 1000;
	 * 
	 * if( reverseset.containsKey(cnt) ) { List<List<String>> joinlist =
	 * reverseset.get(cnt); joinlist.add( joinset ); } else { List<List<String>>
	 * joinlist = new ArrayList<List<String>>(); joinlist.add( joinset );
	 * reverseset.put(cnt, joinlist); } }
	 * 
	 * for( int cnt : reverseset.keySet() ) { List<List<String>> joinlist =
	 * reverseset.get(cnt); for( List<String> joinset : joinlist ) {
	 * System.out.println( joinset + ": " + cnt); } }
	 * 
	 * for( int cnt : reverseset.keySet() ) { List<List<String>> joinlist =
	 * reverseset.get(cnt); for( List<String> joinset : joinlist ) {
	 * 
	 * String str1 = joinset.get(0); String str2 = joinset.get(1);
	 * 
	 * /*for( String joinstr : joinset ) { if( str1 == null ) str1 = joinstr;
	 * else { str2 = joinstr; break; } }*
	 * 
	 * boolean minus1 = str1.contains("Minus"); str1 = str1.replace(" Minus",
	 * ""); str2 = str2.replace(" Minus", ""); //boolean minus1 =
	 * str1.contains("Minus"); //String str1com =
	 * str1.substring(0,str1.lastIndexOf('_')); //String str2simple =
	 * str1.substring(0,str2.lastIndexOf('_')); String str1simple =
	 * str1.substring(0,str1.lastIndexOf('_')); String str2simple =
	 * str2.substring(0,str2.lastIndexOf('_'));
	 * 
	 * List<String> seqlist1 = null; List<String> seqlist2 = null; //boolean
	 * both = false; for( List<String> sl : sortorder ) { for( String seq : sl )
	 * { /*if( seq.contains(str1simple) && seq.contains(str2simple) ) { seqlist1
	 * = sl; seqlist2 = sl; } else*
	 * 
	 * if( seq.contains(str1simple) ) { if( seqlist1 == null ) seqlist1 = sl; }
	 * else if( seq.contains(str2simple) ) { if( seqlist2 == null ) seqlist2 =
	 * sl; } } if( seqlist1 != null && seqlist2 != null ) break; }
	 * 
	 * /*for( List<String> sl1 : sortorder ) { for( List<String> sl2 : sortorder
	 * ) { if( sl1 != sl2 ) { for( String str : sl1 ) { if( sl2.contains(str) )
	 * { System.err.println( str ); System.err.println(); for( String s1 : sl1 )
	 * { System.err.println( s1 ); } System.err.println(); for( String s2 : sl2
	 * ) { System.err.println( s2 ); } System.err.println(); } } } } }
	 * 
	 * int count = 0; if( seqlist1 != null ) { for( String s : seqlist1 ) { if(
	 * s.contains("00006") ) count++; else if( s.contains("00034") ) count++;
	 * 
	 * if( count == 2 ) { System.err.println(); } } }
	 * 
	 * if( seqlist2 != null ) { for( String s : seqlist2 ) { if(
	 * s.contains("00006") ) count++; else if( s.contains("00034") ) count++;
	 * 
	 * if( count == 2 ) { System.err.println(); } } }*
	 * 
	 * boolean left1 = str1.contains("left"); boolean left2 =
	 * str2.contains("left");
	 * 
	 * if( seqlist1 == null && seqlist2 == null ) { List<String> seqlist = new
	 * ArrayList<String>(); sortorder.add( seqlist );
	 * 
	 * if( left1 ) { if( left2 ) { if( minus1 ) { seqlist.add( str1+" reverse"
	 * ); seqlist.add( str2 ); } else { seqlist.add( str2+" reverse" );
	 * seqlist.add( str1 ); } } else { if( minus1 ) { seqlist.add(
	 * str1+" reverse" ); seqlist.add( str2+" reverse" ); } else { seqlist.add(
	 * str2 ); seqlist.add( str1 ); } } } else { if( left2 ) { if( minus1 ) {
	 * seqlist.add( str2+" reverse" ); seqlist.add( str1+" reverse" ); } else {
	 * seqlist.add( str1 ); seqlist.add( str2 ); } } else { if( minus1 ) {
	 * seqlist.add( str2 ); seqlist.add( str1+" reverse" ); } else {
	 * seqlist.add( str1 ); seqlist.add( str2+" reverse" ); } } } } else if(
	 * (seqlist1 == null && seqlist2 != null) || (seqlist1 != null && seqlist2
	 * == null) ) { List<String> seqlist; String selseq = null; String noseq =
	 * null;
	 * 
	 * int ind = -1; if( seqlist1 == null ) { seqlist = seqlist2; selseq = str2;
	 * noseq = str1;
	 * 
	 * String seqf = seqlist.get(0); String seql = seqlist.get( seqlist.size()-1
	 * ); boolean bf = true; //(seqf.contains("left") &&
	 * !seqf.contains("reverse")) || (!seqf.contains("left") &&
	 * seqf.contains("reverse")); boolean bl = true; //(seql.contains("left") &&
	 * seql.contains("reverse")) || (!seql.contains("left") &&
	 * !seql.contains("reverse"));
	 * 
	 * if( seqf.contains(str2simple) && bf ) ind = 0; else if(
	 * seql.contains(str2simple) && bl ) ind = seqlist.size()-1; } else {
	 * seqlist = seqlist1; selseq = str1; noseq = str2;
	 * 
	 * String seqf = seqlist.get(0); String seql = seqlist.get( seqlist.size()-1
	 * ); boolean bf = true; //(seqf.contains("left") &&
	 * !seqf.contains("reverse")) || (!seqf.contains("left") &&
	 * seqf.contains("reverse")); boolean bl = true; //(seql.contains("left") &&
	 * seql.contains("reverse")) || (!seql.contains("left") &&
	 * !seql.contains("reverse"));
	 * 
	 * if( seqf.contains(str1simple) && bf ) ind = 0; else if(
	 * seql.contains(str1simple) && bl ) ind = seqlist.size()-1; }
	 * 
	 * if( ind != -1 ) { String tstr = seqlist.get(ind); boolean leftbef =
	 * tstr.contains("left"); boolean leftaft = selseq.contains("left"); boolean
	 * allaft = false;//selseq.contains("all"); boolean revbef =
	 * tstr.contains("reverse"); //boolean revaft = selseq.contains("reverse");
	 * 
	 * boolean leftno = noseq.contains("left"); //boolean revno =
	 * selseq.contains("reverse");
	 * 
	 * if( leftbef && revbef) { if( leftaft ) { if( ind == seqlist.size()-1 ||
	 * allaft ) { if( leftno ) seqlist.add( seqlist.size(), noseq ); else
	 * seqlist.add( seqlist.size(), noseq+" reverse" ); } } else { if( ind == 0
	 * || allaft ) { seqlist.add( 0, selseq+" reverse" ); if( leftno )
	 * seqlist.add( 0, noseq+" reverse" ); else seqlist.add( 0, noseq ); } } }
	 * else if( !leftbef && !revbef ) { if( leftaft ) { if( ind == 0 || allaft )
	 * { seqlist.add( 0, selseq ); if( leftno ) seqlist.add( 0, noseq+" reverse"
	 * ); else seqlist.add( 0, noseq ); } } else { if( ind == seqlist.size()-1
	 * || allaft ) { if( leftno ) seqlist.add( seqlist.size(), noseq ); else
	 * seqlist.add( seqlist.size(), noseq+" reverse" ); } } } else if( !leftbef
	 * && revbef ) { if( leftaft ) { if( ind == seqlist.size()-1 || allaft ) {
	 * seqlist.add( seqlist.size(), selseq+" reverse" ); if( leftno )
	 * seqlist.add( seqlist.size(), noseq ); else seqlist.add( seqlist.size(),
	 * noseq+" reverse" ); } } else { if( ind == 0 || allaft ) { if( leftno )
	 * seqlist.add( 0, noseq+" reverse" ); else seqlist.add( 0, noseq ); } }
	 * 
	 * //if( leftno ) seqlist.add( 0, noseq+" reverse" ); //else seqlist.add( 0,
	 * noseq ); } else if( leftbef && !revbef ) { if( leftaft ) { if( ind == 0
	 * || allaft ) { if( leftno ) seqlist.add( 0, noseq+" reverse" ); else
	 * seqlist.add( 0, noseq ); } } else { if( ind == seqlist.size()-1 || allaft
	 * ) { seqlist.add( seqlist.size(), selseq ); if( leftno ) seqlist.add(
	 * seqlist.size(), noseq ); else seqlist.add( seqlist.size(),
	 * noseq+" reverse" ); } }
	 * 
	 * //if( leftno ) seqlist.add( 0, noseq+" reverse" ); //else seqlist.add( 0,
	 * noseq ); }
	 * 
	 * /*if( selseq.contains(str1simple) ) { if( selseq.contains("reverse ") ) {
	 * if( left1 ) { if( left2 ) { seqlist.add( ind+1, str2 ); } else {
	 * seqlist.add( ind+1, str2+" reverse" ); } } else { if( left2 ) {
	 * seqlist.add( ind, str2+" reverse" ); } else { seqlist.add( ind, str2 ); }
	 * } } else { if( left1 ) { if( left2 ) { seqlist.add( ind, str2+" reverse"
	 * ); } else { seqlist.add( ind, str2 ); } } else { if( left2 ) {
	 * seqlist.add( ind+1, str2 ); } else { seqlist.add( ind+1, str2+" reverse"
	 * ); } } } } else { if( selseq.contains("reverse ") ) { if( left1 ) { if(
	 * left2 ) { seqlist.add( ind+1, str1 ); } else { seqlist.add( ind,
	 * str1+" reverse" ); } } else { if( left2 ) { seqlist.add( ind+1,
	 * str1+" reverse" ); } else { seqlist.add( ind, str1 ); } } } else { if(
	 * left1 ) { if( left2 ) { seqlist.add( ind, str1+" reverse" ); } else {
	 * seqlist.add( ind+1, str1 ); } } else { if( left2 ) { seqlist.add( ind,
	 * str1 ); } else { seqlist.add( ind+1, str1+" reverse" ); } } } }* } } else
	 * if( seqlist1 != seqlist2 ) { String selseq1 = null; String selseq2 =
	 * null;
	 * 
	 * int ind1 = -1; if( seqlist1.get(0).contains(str1simple) ) { ind1 = 0;
	 * selseq1 = seqlist1.get(0); } else if( seqlist1.get( seqlist1.size()-1
	 * ).contains(str1simple) ) { ind1 = seqlist1.size()-1; selseq1 =
	 * seqlist1.get( seqlist1.size()-1 ); }
	 * 
	 * int ind2 = -1; if( seqlist2.get(0).contains(str2simple) ) { ind2 = 0;
	 * selseq2 = seqlist2.get(0); } else if( seqlist2.get( seqlist2.size()-1
	 * ).contains(str2simple) ) { ind2 = seqlist2.size()-1; selseq2 =
	 * seqlist2.get( seqlist2.size()-1 ); }
	 * 
	 * boolean success = false;
	 * 
	 * if( selseq1 == null || selseq2 == null ) { System.err.println("bleh"); }
	 * else { System.err.println( "joining: " + seqlist1 ); System.err.println(
	 * "and: " + seqlist2 );
	 * 
	 * boolean lef1 = selseq1.contains("left"); boolean lef2 =
	 * selseq2.contains("left"); boolean rev1 = selseq1.contains("reverse");
	 * boolean rev2 = selseq2.contains("reverse");
	 * 
	 * boolean bb = false; if( bb ) { System.err.println("subleh"); } else { if(
	 * lef1 && !left1 ) { if( rev1 && ind1 == 0 ) seqlist1.add( 0,
	 * str1+" reverse" ); else if( ind1 == seqlist1.size()-1 ) seqlist1.add(
	 * seqlist1.size(), str1 ); } else if( !lef1 && left1 ) { if( rev1 && ind1
	 * == seqlist1.size()-1 ) seqlist1.add( seqlist1.size(), str1+" reverse" );
	 * else if( ind1 == 0 ) seqlist1.add( 0, str1 ); }
	 * 
	 * if( lef2 && !left2 ) { if( rev2 && ind2 == 0 ) seqlist2.add( 0,
	 * str2+" reverse" ); else if( ind2 == seqlist2.size()-1 ) seqlist2.add(
	 * seqlist2.size(), str2 ); } else if( !lef2 && left2 ) { if( rev2 && ind2
	 * == seqlist2.size()-1 ) seqlist2.add( seqlist2.size(), str2+" reverse" );
	 * else if( ind2 == 0 ) seqlist2.add( 0, str2 ); }
	 * 
	 * boolean left1beg = seqlist1.get(0).contains("left"); boolean rev1beg =
	 * seqlist1.get(0).contains("reverse"); boolean left1end =
	 * seqlist1.get(seqlist1.size()-1).contains("left"); boolean rev1end =
	 * seqlist1.get(seqlist1.size()-1).contains("reverse");
	 * 
	 * boolean left2beg = seqlist2.get(0).contains("left"); boolean rev2beg =
	 * seqlist2.get(0).contains("reverse"); boolean left2end =
	 * seqlist2.get(seqlist2.size()-1).contains("left"); boolean rev2end =
	 * seqlist2.get(seqlist2.size()-1).contains("reverse");
	 * 
	 * if( seqlist1.get(0).contains(str1simple) ) { if(
	 * seqlist2.get(0).contains(str2simple) ) { if( ((left1beg && !rev1beg) ||
	 * (!left1beg && rev1beg)) && (((left2beg && !rev2beg) || (!left2beg &&
	 * rev2beg))) ) { Collections.reverse( seqlist2 ); for( int u = 0; u <
	 * seqlist2.size(); u++ ) { String val = seqlist2.get(u); if(
	 * val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "")
	 * ); else { int end = val.length()-1; while( val.charAt(end) == 'I' )
	 * end--; seqlist2.set(u, val.substring(0,
	 * end+1)+" reverse"+val.substring(end+1, val.length()) ); } } success =
	 * true; seqlist1.addAll(0, seqlist2); } } else { if( ((left1beg &&
	 * !rev1beg) || (!left1beg && rev1beg)) && (((left2end && rev2end) ||
	 * (!left2end && !rev2end))) ) { success = true; seqlist1.addAll(0,
	 * seqlist2); } } } else { //if( seqlist1.indexOf(str1) == seqlist1.size()-1
	 * ) { if( seqlist2.get(0).contains(str2simple) ) { if( ((left1end &&
	 * rev1end) || (!left1end && !rev1end)) && (((left2beg && !rev2beg) ||
	 * (!left2beg && rev2beg))) ) { success = true;
	 * seqlist1.addAll(seqlist1.size(), seqlist2); } } else { if( ((left1end &&
	 * rev1end) || (!left1end && !rev1end)) && (((left2end && rev2end) ||
	 * (!left2end && !rev2end))) ) { Collections.reverse( seqlist2 ); for( int u
	 * = 0; u < seqlist2.size(); u++ ) { String val = seqlist2.get(u); if(
	 * val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "")
	 * ); else { int end = val.length()-1; while( val.charAt(end) == 'I' )
	 * end--; seqlist2.set(u, val.substring(0,
	 * end+1)+" reverse"+val.substring(end+1, val.length()) ); } } success =
	 * true; seqlist1.addAll(seqlist1.size(), seqlist2); } } }
	 * 
	 * if( success ) { if( !sortorder.remove( seqlist2 ) ) {
	 * System.err.println("no remove"); } }
	 * System.err.println("result is: "+seqlist1); } } } else {
	 * System.err.println( "same shit " + seqlist1 + " " + str1 + " " + str2 );
	 * /*for( int k = 0; k < seqlist1.size(); k++ ) { if(
	 * seqlist1.get(k).contains(str1) ) seqlist1.set(k, seqlist1.get(k)+"I");
	 * else if( seqlist1.get(k).contains(str2) ) seqlist1.set(k,
	 * seqlist1.get(k)+"I"); }* int i = 0; i = 2; } } //} //} //} }
	 * 
	 * System.out.println("join"); for( List<String> so : sortorder ) { for( int
	 * i = 0; i < so.size(); i++ ) { String s = so.get(i); String ss =
	 * s.substring(0, s.indexOf("_contig")+12); if( i == so.size()-1 ) {
	 * System.out.println( ss + (s.contains("everse") ? "_reverse" : "") ); }
	 * else { String n = so.get(i+1); String nn = n.substring(0,
	 * n.indexOf("_contig")+12); if( ss.equals(nn) ) { System.out.println( ss +
	 * (s.contains("everse") ? "_reverse" : "") ); i++; } else {
	 * System.out.println( ss + (s.contains("everse") ? "_reverse" : "") ); } }
	 * } /*for( String s : so ) { System.out.println(s); }*
	 * System.out.println(); } } } }
	 */

	public static void blast2Filt(String name, String filtname) throws IOException {
		FileReader fr = new FileReader(name);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		Set<String> contigs = new HashSet<String>();
		String stuff = null;
		while (line != null) {
			if (line.startsWith("Query=")) {
				stuff = line.substring(7).trim();
			} else if (line.contains("contig")) {
				contigs.add(stuff);
			}
			// String[] split = line.trim().split("[\t ]+");
			// contigs.add( split[0].trim() );

			line = br.readLine();
		}
		fr.close();

		FileWriter fw = new FileWriter(filtname);
		for (String contig : contigs) {
			fw.write(contig.substring(0, 14) + "\n");
		}
	}

	public static void contigShare(String name) throws IOException {
		FileReader fr = new FileReader(name);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		Set<String> contigs = new HashSet<String>();
		while (line != null) {
			String[] split = line.trim().split("[\t ]+");
			contigs.add(split[0].trim());

			line = br.readLine();
		}
		fr.close();

		System.err.println(contigs.size());
		for (String contig : contigs) {
			System.err.println(contig);
		}
	}

	public static void trimFasta(String name, String newname, String filter, boolean inverted) throws IOException {
		Set<String> filterset = new HashSet<String>();
		File ff = new File(filter);
		FileReader fr = new FileReader(ff);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			filterset.add(line);
			line = br.readLine();
		}
		fr.close();

		trimFasta(name, newname, filterset, inverted);
	}

	public static void trimFasta(String name, String newname, Set<String> filterset, boolean inverted) throws IOException {
		FileWriter fw = new FileWriter(newname);
		BufferedWriter bw = new BufferedWriter(fw);

		Reader fr;
		if (name.endsWith("gz")) {
			FileInputStream fis = new FileInputStream(name);
			GZIPInputStream gis = new GZIPInputStream(fis);
			fr = new InputStreamReader(gis);
		} else {
			fr = new FileReader(name);
		}
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		String seqname = null;
		while (line != null) {
			if (line.startsWith(">")) {
				if (inverted) {
					seqname = line;
					for (String f : filterset) {
						if (line.contains(f)) {
							seqname = null;
							break;
						}
					}
					if (seqname != null)
						bw.write(seqname + "\n");
				} else {
					seqname = null;
					for (String f : filterset) {
						if (line.contains(f)) {
							bw.write(line + "\n");
							seqname = line;
							break;
						}
					}
				}
			} else if (seqname != null) {
				bw.write(line + "\n");
			}

			line = br.readLine();
		}
		fr.close();

		bw.flush();
		fw.close();
	}

	public static void flankingFasta(String name, String newname) throws IOException {
		FileWriter fw = new FileWriter(newname);
		BufferedWriter bw = new BufferedWriter(fw);

		int fasti = 60;
		FileReader fr = new FileReader(name);
		BufferedReader br = new BufferedReader(fr, 100000000);
		String line = br.readLine();
		String seqname = null;
		StringBuilder seq = new StringBuilder();
		while (line != null) {
			if (line.startsWith(">")) {
				if (seqname != null) {
					if (seq.length() < 200) {
						fw.write(seqname + "_all" + "\n");
						for (int i = 0; i < seq.length(); i += fasti) {
							fw.write(seq.substring(i, Math.min(seq.length(), i + fasti)) + "\n");
						}
					} else {
						fw.write(seqname + "_left" + "\n");
						for (int i = 0; i < 150; i += fasti) {
							fw.write(seq.substring(i, Math.min(150, i + fasti)) + "\n");
						}
						fw.write(seqname + "_right" + "\n");
						for (int i = seq.length() - 151; i < seq.length(); i += fasti) {
							fw.write(seq.substring(i, Math.min(seq.length(), i + fasti)) + "\n");
						}
					}
				}
				int endind = line.indexOf(' ');
				if (endind == -1)
					endind = line.indexOf('\t');
				if (endind == -1)
					endind = line.length();
				seqname = line.substring(0, endind);
				seq = new StringBuilder();
			} else {
				seq.append(line);
			}

			line = br.readLine();
		}
		fr.close();

		bw.flush();
		fw.close();
	}
	
	public static class HitList implements Comparable<HitList> {
		List<String>	hitlist;
		String			group;
		
		public HitList( String group, List<String> hitlist ) {
			this.group = group;
			this.hitlist = hitlist;
		}
		
		@Override
		public int compareTo(HitList o) {
			return o.hitlist.size() - this.hitlist.size();
		}
	};

	public static void eyjo( String blast, String filter, String result, int threshold ) throws IOException {
		Map<String,String>	filtermap = new HashMap<String,String>();
		if( filter != null ) {
			FileReader fr = new FileReader( filter );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			String name = null;
			while( line != null ) {
				if( line.startsWith(">") ) {
					int i = line.indexOf(' ');
					if( i == -1 ) i = line.length();
					name = line.substring(1, i);
				} else {
					filtermap.put( name, line );
				}
				line = br.readLine();
			}
			br.close();
		}
		
		Map<String,Map<String,List<String>>>	treemapmap = new HashMap<String,Map<String,List<String>>>();
		Map<String,List<String>> treemap = null;// = hlmnew HashMap<String,List<String>>();
		
		FileReader	fr = new FileReader( blast );
		String hit = null;
		BufferedReader	br = new BufferedReader( fr );
		String line = br.readLine();
		boolean hitb = false;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				hit = line.substring(7).trim();
				
				line = br.readLine();
				while( !line.startsWith("Length=") ) {
					line = br.readLine();
				}
				String lenstr = line.substring(7);
				int len = Integer.parseInt( lenstr );
				
				if( len >= 300 ) {
					int i = hit.indexOf(' ');
					if( i > 0 ) hit = hit.substring(0,i);
					
					String val = filtermap.get( hit );
					if( treemapmap.containsKey( val ) ) treemap = treemapmap.get( val );
					else {
						treemap = new HashMap<String,List<String>>();
						treemapmap.put( val, treemap );
					}
					hitb = true;
				} else hitb = false;
			} else if( hitb && line.startsWith("***** No hits") ) {
				String group = "No hits";
				
				List<String>	hitlist;
				if( treemap.containsKey( group ) ) {
					hitlist = treemap.get(group);
				} else {
					hitlist = new ArrayList<String>();
					treemap.put( group, hitlist );
				}
				hitlist.add( hit+" 0.0 0/0 (0%)" );
			} else if( hitb && line.startsWith(">") ) {
				String group = line.substring(2);
				
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					group += line;
					line = br.readLine();	
				}
				
				String idstr = "";
				String estr = "";
				line = br.readLine();
				while( !line.contains("Strand=") ) {
					int i = line.indexOf("Expect = ");
					if( i != -1 ) {
						estr = line.substring(i+9);
					}
					
					i = line.indexOf("Identities = ");
					if( i != -1 ) {
						int k = line.indexOf(',');
						idstr = line.substring(i+13,k);
					}
					
					line = br.readLine();
				}
				
				int svidx = idstr.indexOf('(');
				int esvidx = idstr.indexOf('%', svidx+1);
				int id = Integer.parseInt( idstr.substring(svidx+1, esvidx) );
				if( id < threshold ) {
					group = "No hits";
					hit += " 0.0 0/0 (0%)";
				} else {
					hit += " "+idstr;
					hit += " "+estr;
				}
				
				List<String>	hitlist;
				if( treemap.containsKey( group ) ) {
					hitlist = treemap.get(group);
				} else {
					hitlist = new ArrayList<String>();
					treemap.put( group, hitlist );
				}
				hitlist.add( hit );
			}
			line = br.readLine();
		}
		br.close();
		
		Map<String,Integer>	count = new HashMap<String,Integer>();
		for( String val : filtermap.keySet() ) {
			String val2 = filtermap.get( val );
			if( count.containsKey( val2 ) ) {
				count.put( val2, count.get(val2)+1 );
			} else count.put( val2, 1 );
		}
		
		for( String val : treemapmap.keySet() ) {
			if( count.get(val) > 20 ) {
				treemap = treemapmap.get( val );
				
				FileWriter	fw = new FileWriter( result+"_"+val+".txt" );
				fw.write("total: 0 subtot: 0\n");
				
				List<HitList>	lhit = new ArrayList<HitList>();
				for( String group : treemap.keySet() ) {
					List<String> hitlist = treemap.get( group );
					lhit.add( new HitList( group, hitlist ) );
				}
				Collections.sort( lhit );
				
				for( HitList hlist : lhit ) {
					String 			group = hlist.group;
					List<String>	hitlist = hlist.hitlist;
					
					String first = "No_hits";
					String lst = group;
					if( !group.contains("No hits") ) {
						int i = group.indexOf(' ');
						first = group.substring(0,i);
						lst = group.substring(i+1);
					}
					
					String[] split = lst.split(";");
					fw.write( split[ split.length-1 ] );
					for( int i = split.length-2; i >= 0; i-- ) {
						fw.write( " : " + split[i] );
					}
					fw.write( " : root" );
					fw.write( "\n>"+first+"  "+hitlist.size()+"\n(" );
					fw.write( hitlist.get(0) );
					for( int i = 1; i < hitlist.size(); i++ ) {
						fw.write( ","+hitlist.get(i) );
					}
					fw.write(")\n\n");
				}
				fw.close();
			}
		}
	}
	
	public static void viggo( String fastapath, String qualpath, String blastoutpath, String resultpath ) throws IOException {
		/*
		 * String base = "/vg454flx/viggo/viggo/"; int num = 16; int seqcount =
		 * 0; Set<String> included = new HashSet<String>(); FileReader fr = new
		 * FileReader(base+"reads/"+num+".TCA.454Reads.fna"); BufferedReader br
		 * = new BufferedReader( fr ); String line = br.readLine(); String
		 * current = null; boolean inc = false; while( line != null ) { if(
		 * line.startsWith(">") ) { if( inc && current != null ) { included.add(
		 * current ); } int k = line.indexOf(' '); current = line.substring(1,
		 * k); inc = true; seqcount++; } else { if( inc && (line.indexOf('N') !=
		 * -1 || line.indexOf('n') != -1) ) inc = false; } line = br.readLine();
		 * } br.close();
		 * 
		 * if( inc && current != null ) { included.add( current ); }
		 * 
		 * System.err.println( seqcount + "  " + included.size() );
		 * 
		 * int sum = 0; int sumc = 0; fr = new
		 * FileReader(base+"reads/"+num+".TCA.454Reads.qual"); br = new
		 * BufferedReader( fr ); line = br.readLine(); current = null; while(
		 * line != null ) { if( line.startsWith(">") ) { if( current != null &&
		 * sum/sumc < 30 ) { included.remove( current ); } int k =
		 * line.indexOf(' '); current = line.substring(1, k); inc = true; sum =
		 * 0; sumc = 0; } else { String[] split = line.split("[ ]+"); for(
		 * String s : split ) { int i = Integer.parseInt(s.trim()); sum += i;
		 * sumc++; } } line = br.readLine(); } br.close();
		 * 
		 * System.err.println( seqcount + "  " + included.size() );
		 * 
		 * Map<String,Integer> freqmap = loadFrequency( new
		 * FileReader(base+""+num+".blastout"), included ); for( String val :
		 * freqmap.keySet() ) { int fv = freqmap.get(val); System.err.println(
		 * val + "  " + fv ); } loci2gene( "/vg454flx/tax/", new
		 * FileReader(base+""+num+".blastout"), base+""+num+"v1.txt", null,
		 * freqmap, included );
		 */

		//String base = "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/stuff/viggo/";
		//int num = 6;
		int seqcount = 0;
		Set<String> included = new HashSet<String>();
		FileReader fr = new FileReader( fastapath ); //new FileReader(base + "reads/" + num + ".TCA.454Reads.fna");
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		String current = null;
		boolean inc = false;
		while (line != null) {
			if (line.startsWith(">")) {
				if (inc && current != null) {
					included.add(current);
				}
				int k = line.indexOf(' ');
				current = line.substring(1, k);
				inc = true;
				seqcount++;
			} else {
				if (inc && (line.indexOf('N') != -1 || line.indexOf('n') != -1))
					inc = false;
			}
			line = br.readLine();
		}
		br.close();

		if (inc && current != null) {
			included.add(current);
		}

		System.err.println(seqcount + "  " + included.size());

		int sum = 0;
		int sumc = 0;
		fr = new FileReader( qualpath ); // new FileReader(base + "reads/" + num + ".TCA.454Reads.qual");
		br = new BufferedReader(fr);
		line = br.readLine();
		current = null;
		while (line != null) {
			if (line.startsWith(">")) {
				if (current != null && sum / sumc < 30) {
					included.remove(current);
				}
				int k = line.indexOf(' ');
				current = line.substring(1, k);
				inc = true;
				sum = 0;
				sumc = 0;
			} else {
				String[] split = line.split("[ ]+");
				for (String s : split) {
					int i = Integer.parseInt(s.trim());
					sum += i;
					sumc++;
				}
			}
			line = br.readLine();
		}
		br.close();

		System.err.println(seqcount + "  " + included.size());

		//String blastoutFilename = base + "16S_" + num + ".blastout";
		Map<String, Integer> freqmap = loadFrequency(new FileReader(blastoutpath), included);
		for (String val : freqmap.keySet()) {
			int fv = freqmap.get(val);
			System.err.println(val + "  " + fv);
		}
		loci2gene("/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/stuff/tax/", new FileReader(blastoutpath), resultpath, null, freqmap, included);
	}

	public static class StrId {
		public StrId(String teg, int len) {
			name = teg;
			this.len = len;
			
			if( teg.contains("thermophilus") || teg.contains("composti") ) {
				color = "small_blue";
			} else if( teg.contains("scotoductus") || teg.contains("antrani") || teg.contains("yunnan") || teg.contains("rehai")  ) {
				color = "small_orange";
			} else if( teg.contains("aquaticus") ) {
				color = "small_red";
			} else if( teg.contains("oshimai") ) {
				color = "small_brown";
			} else if( teg.contains("filiformis") ) {
				color = "small_purple";
			} else if( teg.contains("arciformis") || teg.contains("islandicus") || teg.contains("kawaray") ) {
				color = "small_green";
			} else if( teg.contains("eggertsoni") || teg.contains("brockianus") || teg.contains("igniterr") ) {
				color = "small_yellow";
			} else if( teg.contains("Meiothermus") || teg.contains("Vulcanithermus") ) {
				color = "small_black";
			} else if( teg.contains("Oceanithermus") ) {
				color = "small_grey";
			} else /*if( teg.contains("Marinithermus") )*/ {
				color = "small_white";
			}
		}

		String name;
		int id;
		int len;
		String color;
	};

	public static void simmi() throws IOException {
		FileReader fr = new FileReader("/home/sigmar/allbutthermus.blastout");
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		String current = null;
		StrId currteg = null;
		int currlen = 0;
		boolean done = false;
		Map<String, StrId> tegmap = new HashMap<String, StrId>();
		while (line != null) {
			String trim = line.trim();
			if (trim.startsWith("Query=")) {
				String[] split = trim.substring(7).trim().split("[ ]+");
				current = split[0];
				done = false;
			} else if (trim.startsWith("Length=")) {
				currlen = Integer.parseInt(trim.substring(7).trim());
			} else if (line.startsWith(">") && !done) {
				int i = line.lastIndexOf('|');
				if (i == -1)
					i = 0;
				String teg = line.substring(i + 1).trim();
				line = br.readLine();
				while (!line.startsWith("Length")) {
					teg += line;
					line = br.readLine();
				}
				//if (teg.contains("Thermus") || teg.startsWith("t.")) {
					currteg = new StrId(teg, currlen);
					tegmap.put(current, currteg);
				//}
			} else if (trim.startsWith("Ident") && !done) {
				int sv = trim.indexOf('(');
				int svl = trim.indexOf('%', sv + 1);

				currteg.id = Integer.parseInt(trim.substring(sv + 1, svl));

				done = true;
			}

			line = br.readLine();
		}
		fr.close();

		System.err.println(tegmap.size());

		//int[] iv = { 0, 10, 16, 50, 8, 8, 8, 8, 60, 50, 30, 50, 150, 150, 80, 50 };
		int[] iv = { 0, 10, 16, 50, 100, 100, 60, 30, 500, 150, 500, 500, 30, 50, 200, 30, 30 };
		for (int i = 0; i < iv.length-1; i++) {
			iv[i] += 1;
		}
		int[] isum = new int[iv.length];
		isum[0] = iv[0];
		for (int i = 1; i < iv.length; i++) {
			isum[i] = isum[i - 1] + iv[i];
		}

		FileOutputStream fos = new FileOutputStream("/home/sigmar/noname.txt");
		PrintStream pos = new PrintStream(fos);
		//pos.println( "name\tacc\tspecies\tlen\tident\tdoi\tpubmed\tjournal\tauth\tsub_auth\tsub_date\tcountry\tsource\ttemp\tpH" );
		pos.println( "name\tacc\tfullname\tspecies\tlen\tident\tcountry\tsource\tdoi\tpubmed\tauthor\tjournal\tsub_auth\tsub_date\tlat_lon\tdate\ttitle\tarb\tcolor\ttemp\tpH" );
		
		fr = new FileReader("/home/sigmar/export3.nds");
		br = new BufferedReader(fr);
		line = br.readLine();
		while (line != null) {
			String name = line.substring(isum[0], isum[1]).trim();
			String acc = line.substring(isum[1], isum[2]).trim();
			String fullname = line.substring(isum[2], isum[3]).trim();
			String country = line.substring(isum[3], isum[4]).trim();
			String source = line.substring(isum[4], isum[5]).trim();
			String doi = line.substring(isum[5], isum[6]).trim();
			String pubmed = line.substring(isum[6], isum[7]).trim();
			String author = line.substring(isum[7], isum[8]).trim().replace("\"", "");
			String journal = line.substring(isum[8], isum[9]).trim();
			String sub_auth = line.substring(isum[9], isum[10]).trim().replace("\"", "");
			String sub_date = line.substring(isum[10], isum[11]).trim().replace("\"", "");
			String lat_lon = line.substring(isum[11], isum[12]).trim();
			String date = line.substring(isum[12], isum[13]).trim();
			String title = line.substring(isum[13], isum[14]).trim();
			String length = line.substring(isum[14], isum[15]).trim();
			String arb = isum.length > 16 && isum[16] <= line.length() ? line.substring(isum[15], isum[16]).trim() : "";
			
			/*String country = line.substring(isum[7], isum[8]).trim();
			String doi = line.substring(isum[8], isum[9]).trim();
			String pubmed = line.substring(isum[9], isum[10]).trim();
			String journal = line.substring(isum[10], isum[11]).trim();
			String auth = line.substring(isum[11], isum[12]).trim();
			String sub_auth = line.substring(isum[12], isum[13]).trim();
			String sub_date = line.substring(isum[13], isum[14]).trim();
			String source = isum.length > 14 ? line.substring(isum[14], isum[15] - 1).trim() : "";*/

			if (tegmap.containsKey(name)) {
				// StrId teg = tegmap.get(name);
				StrId teg = tegmap.remove(name);
				if( teg.name.contains("Thermus") || teg.name.contains("Meiothermus") || teg.name.contains("Marinithermus") || teg.name.contains("Oceanithermus") || teg.name.contains("Vulcani") ) 
				pos.println(name + "\t" + acc + "\t" + fullname + "\t" + teg.name + "\t" + teg.len + "\t" + teg.id + "\t" + country + "\t" + source + "\t" + doi + "\t" + pubmed + "\t" + author + "\t" + journal + "\t" + sub_auth + "\t" + sub_date + "\t" + lat_lon + "\t" + date + "\t" + title + "\t" + arb + "\t" + teg.color + "\t\t" );
			}
			// System.err.println( line.substring( isum[7], isum[8] ).trim() );

			line = br.readLine();
		}
		fr.close();

		for (String name : tegmap.keySet()) {
			StrId teg = tegmap.get(name);
			pos.println(name + "\t" + name + "\t" + name + "\t" + teg.name + "\t" + teg.len + "\t" + teg.id + "\t" + "Simmaland" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + teg.color + "\t" + "\t" );
		}
		fos.close();
	}
	
	public static void dummy() throws IOException {
		
	}

	public static void main(String[] args) {
		
		/*String[] stra = {"A", "B", "C", "D"};
		corrInd = Arrays.asList( stra );
		double[] dd = { 0.0, 17.0, 21.0, 27.0, 17.0, 0.0, 12.0, 18.0, 21.0, 12.0, 0.0, 14.0, 27.0, 18.0, 14.0, 0.0 };
		TreeUtil treeutil = new TreeUtil();
		treeutil.neighborJoin( dd, 4, corrInd );*/
		
		JFrame frame = new JFrame(); frame.setDefaultCloseOperation(
		JFrame.EXIT_ON_CLOSE );
		 
		frame.setSize(800, 600); 
		GeneSet gs = new GeneSet(); 
		gs.init( frame );
		frame.setVisible( true );
		 

		// System.err.println( Runtime.getRuntime().availableProcessors() );

		/*
		 * try { testbmatrix("/home/sigmar/mynd.png"); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		//init( args );

		try {
			//simmi();
			dummy();
			//SerifyApplet.blastJoin(new FileInputStream("/home/horfrae/peter/stuff.blastout"), System.out);

			// flankingFasta("/home/sigmar/playground/all.fsa",
			// "/home/sigmar/playground/flank.fsa");
			// blast2Filt( "/home/sigmar/kaw.blastout",
			// "/home/sigmar/newkaw_filter.txt" );
			// contigShare( "/home/sigmar/kaw_bac_contigs.txt" );
			// trimFasta("/home/sigmar/kawarayensis.fna",
			// "/home/sigmar/kawarayensis_bacillus.fna",
			// "/home/sigmar/flx/kaw_filter.txt" );
			// trimFasta("/home/sigmar/kawarayensis.fna",
			// "/home/sigmar/kawarayensis_bacillus.fna",
			// "/home/sigmar/flx/kaw_filter.txt" );
			// trimFasta("/home/sigmar/kawarayensis_repeated_assembled.fna",
			// "/home/sigmar/kawarayensis_clean.fna", "/home/sigmar/contig.txt",
			// true );
			// loci2gene( new
			// FileReader("/home/sigmar/flx/arciformis_repeat.blastout"),
			// "/home/sigmar/flx/arciformis_repeat_detailed.txt", "ferro" );
			// loci2gene( new
			// FileReader("/home/sigmar/flx/kawarayensis_repeat.blastout"),
			// "/home/sigmar/flx/kawarayensis_repeat_detailed.txt", "ferro" );
			// loci2gene( new
			// FileReader("/home/sigmar/flx/islandicus.blastoutcat"),
			// "/home/sigmar/flx/islandicus.txt" );
			// loci2gene( new
			// FileReader("/home/sigmar/flx/scoto2127.blastoutcat"),
			// "/home/sigmar/flx/scoto2127.txt" );

			// String[] filt = {"Thermus"};
			// trimFasta(
			// "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/nt.gz",
			// "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/out.fna", new
			// HashSet<String>( Arrays.asList(filt) ), false );
			// String[] filt = {"16S"};
			// trimFasta(
			// "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/out.fna",
			// "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/16s.fna", new
			// HashSet<String>( Arrays.asList(filt) ), false );

			/*
			 * FileReader fr = new FileReader(
			 * "/home/sigmar/parc_thermus_accs.txt" );//new FileReader(
			 * "/home/horfrae/new.txt" ); BufferedReader br = new
			 * BufferedReader( fr );
			 * 
			 * Set<String> accset = new HashSet<String>(); String line =
			 * br.readLine(); while( line != null ) { /*if(
			 * line.contains("acc:") ) { int k = line.indexOf(":"); if( k != -1
			 * ) { String acc = line.substring(k+1).trim(); accset.add( acc ); }
			 * }* accset.add( line ); line = br.readLine(); } br.close();
			 * 
			 * fr = new FileReader( "/home/sigmar/noname.fasta" );//new
			 * FileReader( "/home/horfrae/new.txt" ); br = new BufferedReader(
			 * fr );
			 * 
			 * Set<String> accset2 = new HashSet<String>(); line =
			 * br.readLine(); while( line != null ) { if( line.startsWith(">") )
			 * { /*if( line.contains("acc:") ) { int k = line.indexOf(":"); if(
			 * k != -1 ) { String acc = line.substring(k+1).trim(); accset.add(
			 * acc ); } }* String[] split = line.split("[ ]+"); accset2.add(
			 * split[ split.length-1 ] ); } line = br.readLine(); } br.close();
			 * 
			 * accset.removeAll( accset2 ); System.err.println( "accsize " +
			 * accset.size() );
			 * 
			 * for( String erm : accset ) { System.err.println( erm ); }
			 * 
			 * //trimFasta(
			 * "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/ssu-parc.fasta"
			 * ,
			 * "/media/3cb6dcc1-0069-4cb7-9e8e-db00bf300d96/movies/parc_thermus.fna"
			 * , accset, false );
			 */

			//eyjo( "/u0/snaedis8.blastout", "/home/sigmar/ok8.fasta", "/home/sigmar/short8", 98 );
			//eyjo( "/home/sigmar/flex2.blastout", "/home/sigmar/ok.fasta", "/home/sigmar/gumol" );
			//eyjo( "/home/sigmar/flex.blastout", "/home/sigmar/eyjo_filter.fasta", "/home/sigmar/mysilva" );
			//viggo( "/home/sigmar/Dropbox/eyjo/sim.fasta", "/home/sigmar/Dropbox/eyjo/8.TCA.454Reads.qual", "/home/sigmar/blastresults/sim16S.blastout", "/home/sigmar/my1.txt");
			//simmi();

			// Map<String,Integer> freqmap = loadFrequency( new
			// FileReader("c:/viggo//arciformis_repeat.blastout") );
			// loci2gene( new FileReader("c:/viggo/arciformis_repeat.blastout"),
			// "c:/viggo/arciformis_v1.txt", null, freqmap );

			/*
			 * loci2gene( new FileReader("/home/horfrae/viggo/1.blastout"),
			 * "/home/horfrae/viggo/1v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/2.blastout"),
			 * "/home/horfrae/viggo/2v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/3.blastout"),
			 * "/home/horfrae/viggo/3v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/4.blastout"),
			 * "/home/horfrae/viggo/4v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/5.blastout"),
			 * "/home/horfrae/viggo/5v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/6.blastout"),
			 * "/home/horfrae/viggo/6v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/7.blastout"),
			 * "/home/horfrae/viggo/7v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/8.blastout"),
			 * "/home/horfrae/viggo/8v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/9.blastout"),
			 * "/home/horfrae/viggo/9v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/10.blastout"),
			 * "/home/horfrae/viggo/10v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/11.blastout"),
			 * "/home/horfrae/viggo/11v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/12.blastout"),
			 * "/home/horfrae/viggo/12v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/13.blastout"),
			 * "/home/horfrae/viggo/13v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/14.blastout"),
			 * "/home/horfrae/viggo/14v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/15.blastout"),
			 * "/home/horfrae/viggo/15v.txt", null ); loci2gene( new
			 * FileReader("/home/horfrae/viggo/16.blastout"),
			 * "/home/horfrae/viggo/16v.txt", null );
			 */

			// panCoreFromNRBlast( new String[] { "arciformis.blastout" }, new
			// File("/home/sigmar/") );

			// blastparse( "/home/sigmar/blastout/nilli.blastout" );
			// blastparse( "/home/sigmar/thermus/lepto.blastout.txt" );
			// blastparse( "/home/sigmar/lept_spir.blastout.txt" );
			// blastparse( "/home/sigmar/spiro_blastresults.txt" );
			// blastparse( "/home/sigmar/lept_spir.lept_ames.blastout.txt" );
			// blastparse( "/home/sigmar/brach_spir.brachh.blastout.txt" );
			// blastparse( "/home/sigmar/lept_brach.lepto_inter.blastout.txt" );
			// blastparse( "/home/sigmar/spir_brach.blastout.txt" );
			// blastparse(
			// "/home/sigmar/spiro_core_in_leptobrach_pan.blastout.txt" );

			// blastparse( "/home/sigmar/sim.blast" );
			// blastparse( "/home/sigmar/thermus/newthermus/all.blastout" );

			// newstuff();
			// algoinbio();

			// newsets();

			// aaset();

			/*
			 * for( int i = 1; i <= 16; i++ ) { splitGenes(
			 * "/home/sigmar/viggo/", i+".TCA.454Reads.fna", 8 ); }
			 */
			// splitGenes( "/home/sigmar/thermus/newthermus/", "all.aa", 128 );
			// splitGenes( "/home/sigmar/thermus/newthermus", "0_t.aa", 64 );
			// splitGenes( "/home/sigmar/thermus/newthermus/test/", "erm.aa", 64
			// );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static List<String> res = new ArrayList<String>();

	public static void fixFile(String fastafile, String blastlist, String outfile) throws IOException {
		Set<String> faset = new HashSet<String>();
		FileReader fr = new FileReader(fastafile);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				String[] split = line.split("[\t ]+");
				faset.add(split[0].substring(1));

				int ind = line.lastIndexOf('|');
				String resval;
				if (ind > 0) {
					String sub = line.substring(ind);
					resval = line.substring(1, ind + 1);
					split = sub.split("_");
					ind = 1;
				} else {
					split = line.split("_");
					ind = 2;
					resval = split[0].substring(1) + "_" + split[1];
				}

				int val = 0;
				try {
					val = Integer.parseInt(split[ind]);
				} catch (Exception e) {
					System.err.println(split[ind]);
				}
				while (val >= res.size())
					res.add(null);
				res.set(val, resval);
			}

			line = br.readLine();
		}
		br.close();

		FileWriter fw = new FileWriter(outfile);

		int count = 0;
		int tcount = 0;
		Set<String> regset = new HashSet<String>();
		String last = "";
		String lastline = "";
		String lastsp = "";
		fr = new FileReader(blastlist);
		br = new BufferedReader(fr);
		line = br.readLine();
		while (line != null) {
			String[] split = line.split("[\t ]+");
			if (!split[0].equals(last)) {
				int sind = split[0].lastIndexOf("_");
				String shorter = split[0].substring(0, sind);

				sind = last.lastIndexOf("_");
				String lshorter = null;
				if (sind > 0)
					lshorter = last.substring(0, sind);

				if (shorter.equals(lshorter)) {
					if (!split[1].equals(lastsp)) {
						count++;
						// System.err.println( "erm " + line + "\n    " +
						// lastline + "  " + lastsp );
					}
					tcount++;
				} else {
					/*
					 * if( regset.contains(shorter) ) { System.err.println(
					 * split[0] + " " + last + "  " + shorter ); } else
					 * regset.add( shorter );(
					 */

					if (split[0].startsWith("_")) {
						String[] lsp = split[0].split("_");
						int val = Integer.parseInt(lsp[1]);
						// System.err.println( lsp[1] + "  " + res.get( val ) );
						String str = res.get(val) + split[0];
						for (int i = 1; i < split.length; i++) {
							str += "\t" + split[i];
						}
						fw.write(str + "\n");

						regset.add(res.get(val) + shorter);
					} else {
						fw.write(line + "\n");

						regset.add(shorter);
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

		System.err.println(count + "  " + tcount);

		faset.removeAll(regset);
		for (String s : faset) {
			System.err.println(s);
		}
		System.err.println(faset.size());
	}

	public static Map<String, Gene> idMapping(String blastfile, String idfile, String outfile, int ind, int secind, boolean getgeneids) throws IOException {
		Map<String, Gene> refids = new HashMap<String, Gene>();
		FileReader fr = new FileReader(blastfile);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("ref|") || line.startsWith("sp|") || line.startsWith("pdb|") || line.startsWith("dbj|") || line.startsWith("gb|") || line.startsWith("emb|") || line.startsWith("pir|") || line.startsWith("tpg|")) {
				String[] split = line.split("\\|");
				refids.put(split[1], null);
			}
			line = br.readLine();
		}
		fr.close();

		return idMapping(new FileReader(idfile), outfile, ind, secind, refids, getgeneids);
	}

	public static Map<String, Gene> idMapping(Reader rd, String outfile, int ind, int secind, Map<String, Gene> refids, boolean getgeneids) throws IOException {
		Map<String, Gene> unimap = new HashMap<String, Gene>();
		Map<String, String> ref2kegg = new HashMap<String, String>();
		Map<String, String> ref2pdb = new HashMap<String, String>();

		PrintStream ps = null;
		if (outfile != null) {
			ps = new PrintStream(outfile);
			System.setOut(ps);
		}

		List<String> list = new ArrayList<String>();
		boolean tone = false;
		// FileReader fr = new FileReader(idfile);
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String last = "";
		while (line != null) {
			String[] split = line.split("\t");
			if (split.length > ind) {
				if (!split[secind].equals(last)) {
					if (tone && !getgeneids) {
						for (String sstr : list) {
							String[] spl = sstr.split("\t");
							if (sstr.contains("KEGG")) {
								ref2kegg.put(spl[0], spl[2]);
							} else if (sstr.contains("PDB")) {
								ref2pdb.put(spl[0], spl[2]);
							}
						}
					}

					if (ps != null && tone) {
						for (String sstr : list) {
							System.out.println(sstr);
						}
						tone = false;
					}
					list.clear();
				}
				list.add(line);

				String refid = split[ind];
				if (refids.containsKey(refid)) {
					Gene gene = refids.get(refid);
					if (getgeneids) {
						gene.genid = split[secind];
						unimap.put(gene.genid, gene);
					} else {
						gene.uniid = split[secind];
						unimap.put(gene.uniid, gene);
					}
					gene.allids.add(split[secind]);
					tone = true;
				}
			}

			if (split.length > secind) {
				last = split[secind];
			}
			line = br.readLine();
		}

		if (tone && !getgeneids) {
			for (String sstr : list) {
				String[] spl = sstr.split("\t");
				if (sstr.contains("KEGG")) {
					ref2kegg.put(spl[0], spl[2]);
				} else if (sstr.contains("PDB")) {
					ref2pdb.put(spl[0], spl[2]);
				}
			}
		}

		if (ps != null && tone) {
			for (String sstr : list) {
				System.out.println(sstr);
			}
			tone = false;
		}
		list.clear();

		br.close();
		if (ps != null)
			ps.close();

		if (!getgeneids) {
			for (String s : refids.keySet()) {
				Gene g = refids.get(s);
				if (g.allids != null)
					for (String id : g.allids) {
						if (ref2kegg.containsKey(id)) {
							g.keggid = ref2kegg.get(id);
						}

						if (ref2pdb.containsKey(id)) {
							g.pdbid = ref2pdb.get(id);
						}

						if (g.keggid != null && g.pdbid != null)
							break;
					}
			}
			/*
			 * for( String s : unimap.keySet() ) { Gene g = unimap.get(s);
			 * g.keggid = ref2kegg.get(g.uniid); //System.err.println( g.refid +
			 * "  " + g.keggid ); }
			 */
		}

		return unimap;
	}

	public static void funcMapping(Reader rd, Map<String, Gene> genids, String outshort) throws IOException {
		//Map<String, Function> funcmap = new HashMap<String, Function>();

		FileWriter fw = null;
		if (outshort != null)
			fw = new FileWriter(outshort);

		// FileReader fr = new FileReader( gene2go );
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if (split.length > 1 && genids.containsKey(split[1])) {
				Gene gene = genids.get(split[1]);
				if (gene.funcentries == null)
					gene.funcentries = new HashSet<Function>();
				
				Function func;
				if( !funcmap.containsKey( split[2] ) ) {
					func = new Function( split[2] );
					funcmap.put( split[2], func );
				} else func = funcmap.get( split[2] );
				gene.funcentries.add( func );

				if (fw != null)
					fw.write(line + "\n");
			}

			line = br.readLine();
		}
		br.close();

		if (fw != null)
			fw.close();

		//return funcmap;
	}

	public static void funcMappingUni(Reader rd, Map<String, Gene> uniids, String outfile) throws IOException {
		FileWriter fw = null;
		if (outfile != null)
			fw = new FileWriter(outfile);

		// FileReader fr = new FileReader( sp2go );
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("=");
			if (split.length > 1 && uniids.containsKey(split[0].trim())) {
				Gene gene = uniids.get(split[0].trim());
				if (gene.funcentries == null)
					gene.funcentries = new HashSet<Function>();
				
				for (String erm : split[1].trim().split("[\t ]+")) {
					Function func;
					if( !funcmap.containsKey( erm ) ) {
						func = new Function( erm );
						funcmap.put( erm, func );
					} else func = funcmap.get( erm );
					gene.funcentries.add( func );
				}
				if (fw != null) fw.write(line + "\n");
			}

			line = br.readLine();
		}
		br.close();

		if (fw != null)
			fw.close();
	}

	public static void updateFilter(JTable table, RowFilter filter, JLabel label) {
		DefaultRowSorter<TableModel, Integer> rowsorter = (DefaultRowSorter<TableModel, Integer>) table.getRowSorter();
		rowsorter.setRowFilter(filter);
		if (label != null)
			label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
	}

	private static void relati(JTable table, int[] rr, List<Gene> genelist, Set<Integer> genefilterset, List<Set<String>> uclusterlist, boolean remove) {
		Set<String> ct = new HashSet<String>();
 		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			// genefilterset.add( gg.index );
			if (gg.species != null) {
				for (String sp : gg.species.keySet()) {
					Teginfo stv = gg.species.get(sp);
					for (Tegeval tv : stv.tset) {
						//System.err.println( tv.cont );
						//int li = tv.cont.lastIndexOf('_');
						//String tvshort = tv.contshort+tv.cont.substring(li);
						for (Set<String> uset : uclusterlist) {
							if (uset.contains(tv.cont)) {
								ct.addAll(uset);
								break;
							}
						}
					}
				}
			}
		}

		for (Gene g : genelist) {
			if (g.species != null) {
				for (String sp : g.species.keySet()) {
					Teginfo stv = g.species.get(sp);
					for (Tegeval tv : stv.tset) {
						//System.err.println( tv.cont );
						//int li = tv.cont.lastIndexOf('_');
						//String tvshort = tv.contshort+tv.cont.substring(li);
						if (ct.contains(tv.cont)) {
							if (remove)
								genefilterset.remove(g.index);
							else
								genefilterset.add(g.index);
							break;
						}
					}
				}
			}
		}
	}

	private static void proxi(JTable table, int[] rr, List<Gene> genelist, Set<Integer> genefilterset, boolean remove) {
		Set<String> ct = new HashSet<String>();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			// genefilterset.add( gg.index );
			if (gg.species != null) {
				for (String sp : gg.species.keySet()) {
					Teginfo stv = gg.species.get(sp);
					for (Tegeval tv : stv.tset) {
						ct.add(tv.cont);
						int ind = tv.cont.lastIndexOf("_");
						int val = Integer.parseInt(tv.cont.substring(ind + 1));

						String next = tv.cont.substring(0, ind + 1) + (val + 1);
						System.err.println(next);
						ct.add(next);
						if (val > 1) {
							String prev = tv.cont.substring(0, ind + 1) + (val - 1);
							ct.add(prev);
						}
					}
				}
			}
		}

		for (Gene g : genelist) {
			if (g.species != null) {
				for (String sp : g.species.keySet()) {
					Teginfo stv = g.species.get(sp);
					for (Tegeval tv : stv.tset) {
						if (ct.contains(tv.cont)) {
							if (remove)
								genefilterset.remove(g.index);
							else
								genefilterset.add(g.index);
							break;
						}
					}
				}
			}
		}
	}
	
	public static Set<String> getSelspec( Component comp, final List<String>	specs ) {		
		final Boolean[] b = new Boolean[ specs.size() ];
		for( int i = 0; i < b.length; i++ ) {
			b[i] = true;
		}
		JTable	table = new JTable();
		JScrollPane	scroll = new JScrollPane( table );
		table.setAutoCreateRowSorter( true );
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return specs.size();
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Sel";
				else return "Spec";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return Boolean.class;
				else return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) return b[rowIndex];
				else return specs.get(rowIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				b[ rowIndex ] = (Boolean)aValue;
			}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		Object[] ctls = new Object[] { scroll };
		JOptionPane.showMessageDialog( comp, ctls );
		Set<String>	selspec = new HashSet<String>();
		for( int i = 0; i < table.getRowCount(); i++ ) {
			if( (Boolean)table.getValueAt(i, 0) ) selspec.add( (String)table.getValueAt(i, 1) );
		}
		return selspec;
	}
	
	public static int containmentCount( Set<String> set1, Set<String> set2 ) {
		int r = 0;
		for( String s1 : set1 ) {
			if( set2.contains( s1 ) ) r++;
		}
		return r;
	}
	
	public static void showSequences( Component comp, Set<Gene> genes ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier );
		jf.initGui(frame);

		Map<String, Sequence> contset = new HashMap<String, Sequence>();
		for( Gene gg : genes ) {
			if (gg.species != null) {
				for (String sp : gg.species.keySet()) {
					Teginfo stv = gg.species.get(sp);
					for (Tegeval tv : stv.tset) {
						String contig = tv.cont;
						StringBuilder dna = tv.seq;
						Sequence seq = new Sequence( contig, dna, serifier.mseq );
						contset.put( contig, seq );
						
						/*String contig = tv.contshort;
						if (contset.containsKey(contig)) {
							seq = contset.get(contig);
						} else {
							if (GeneSet.contigs.containsKey(contig)) {
								StringBuilder dna = GeneSet.contigs.get(contig);
								seq = new Sequence(contig, dna, jf.mseq);
							} else
								seq = new Sequence(contig, jf.mseq);
							contset.put(contig, seq);
						}

						Annotation a = seq.new Annotation(seq, contig, Color.red, jf.mann);
						a.setStart(tv.start);
						a.setStop(tv.stop);
						a.setOri(tv.ori);
						a.setGroup(gg.name);
						a.setType("gene");
						jf.addAnnotation(a);*/
						// seq.addAnnotation( new Annotation( seq, ) );
					}
				}
			}
		}

		for (String contig : contset.keySet()) {
			Sequence seq = contset.get(contig);
			serifier.addSequence(seq);
			if (seq.getAnnotations() != null)
				Collections.sort(seq.getAnnotations());
		}
		jf.updateView();

		frame.setVisible(true);
	}
	
	public static StringBuilder getSelectedASeqs( JTable table, List<Gene> genelist, JApplet applet, Set<String> species ) {
		StringBuilder sb = new StringBuilder();
		
		Set<String> selectedSpecies = getSelspec( applet, new ArrayList( species ) );
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			if (gg.species != null) {
				sb.append(gg.name + ":\n");
				//for (String sp : gg.species.keySet()) {
				for( String sp : selectedSpecies ) {
					if( gg.species.containsKey(sp) ) {
						Teginfo stv = gg.species.get(sp);
						for (Tegeval tv : stv.tset) {
							sb.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
							for (int i = 0; i < tv.seq.length(); i += 70) {
								int end = Math.min(i + 70, tv.seq.length());
								sb.append(tv.seq.substring(i, end) + "\n"); // new
																					// String(
																					// tv.seq,
																					// i,
																					// Math.min(i+70,tv.seq.length())
																					// )+"\n");
							}
							// textarea.append( ">" + tv.cont + " " + tv.teg
							// + " " + tv.eval + "\n" + tv.seq + "\n" );
						}
					}
				}
			}
		}
		return sb;
	}
	
	public static StringBuilder getSelectedSeqs( JTable table, List<Gene> genelist ) {
		StringBuilder sb = new StringBuilder();
		
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			if(gg.species != null) {
				sb.append(gg.name + ":\n");
				for (String sp : gg.species.keySet()) {
					Teginfo stv = gg.species.get(sp);
					for (Tegeval tv : stv.tset) {
						sb.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
						if (tv.dna != null) {
							for (int i = 0; i < tv.dna.length(); i += 70) {
								sb.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
							}
						}
					}
				}
			}
		}
		
		return sb;
	}
	
	private static void assignGain( Node n, Map<Node,List<GeneGroup>> gainMap, PrintStream ps ) {
		Set<String>	specs = n.getLeaveNames();
		
		List<GeneGroup> lgg = ggSpecMap.get( specs );
		gainMap.put( n, lgg );
		
		ps.println( specs );
		if( lgg != null ) for( GeneGroup gg : lgg ) {
			Set<String>	nset = new HashSet<String>();
			for( Gene g : gg.genes ) {
				nset.add( g.name );
			}
			ps.println( "\t" + nset );
		}
		
		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes) {
				assignGain( node, gainMap, ps );
			}
		}
	}
	
	private static void assignLoss( Node n, Map<Node,List<GeneGroup>> lossMap, PrintStream ps ) {
		//Set<String>	specs = n.getLeaveNames();
		
		/*List<GeneGroup> lgg = ggSpecMap.get( specs );
		gainMap.put( n, lgg );
		
		ps.println( specs );
		if( lgg != null ) for( GeneGroup gg : lgg ) {
			Set<String>	nset = new HashSet<String>();
			for( Gene g : gg.genes ) {
				nset.add( g.name );
			}
			ps.println( "\t" + nset );
		}*/
		
		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes) {
				Set<String> loss = node.getLeaveNames();
				List<GeneGroup> lgg = null;
				for( Set<String> specs : ggSpecMap.keySet() ) {
					boolean fail = false;
					for( String spec : specs ) {
						if( !loss.contains(spec) ) {
							fail = true;
							break;
						}
					}
					if( !fail ) {
						if( lgg == null ) lgg = new ArrayList<GeneGroup>();
						lgg.addAll( ggSpecMap.get(specs) );
					}
				}
				//ps.println( loss );
				if( lgg != null ) for( Node nnode : nodes ) {
					if( nnode != node ) {
						ps.println( nnode.getLeaveNames() );
						for( GeneGroup gg : lgg ) {
							Set<String>	nset = new HashSet<String>();
							for( Gene g : gg.genes ) {
								nset.add( g.name );
							}
							ps.println( "\t" + nset );
						}
						lossMap.put(nnode, lgg);
					}
				}
				assignLoss( node, lossMap, ps );
			}
		}
	}
	
	public static Teginfo getGroupTes( GeneGroup gg, String spec ) {
		Teginfo ti = null;
		
		for( Gene g : gg.genes ) {
			Teginfo sti = g.species.get( spec );
			if( sti != null ) {
				ti = sti;
				break;
			}
		}
		
		return ti;
	}
	
	static TableModel	defaultModel;
	static TableModel	groupModel;
	
	JComboBox selcomb;
	static Map<String,Function> funcmap = new HashMap<String,Function>();
	private static JComponent showGeneTable(final Map<String, Gene> genemap, final List<Gene> genelist, 
			final List<Function> funclist, final List<Set<String>> iclusterlist, final List<Set<String>> uclusterlist,
			final Map<Set<String>, ShareNum> specset, final Map<Set<String>, ClusterInfo> clustInfoMap, final JButton jb,
			final Container comp, final JApplet applet, final JComboBox selcomblocal) throws IOException {
		JSplitPane splitpane = new JSplitPane();
		splitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitpane.setDividerLocation(400);
		JScrollPane scrollpane = new JScrollPane();
		final JTable table = new JTable() {
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

		table.setDragEnabled(true);
		try {
			final DataFlavor df = new DataFlavor("text/plain;charset=utf-8");
			// System.err.println( df.getHumanPresentableName() + " " +
			// df.getPrimaryType() + " " + df.getSubType() + " " +
			// df.getMimeType() );
			// DataFlavor df1 = DataFlavor.getTextPlainUnicodeFlavor();
			// System.err.println( df.getHumanPresentableName() + " " +
			// df.getPrimaryType() + " " + df.getSubType() + " " +
			// df.getMimeType() );
			TransferHandler th = new TransferHandler() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}

				public boolean canImport(TransferHandler.TransferSupport support) {
					return true;
				}

				protected Transferable createTransferable(JComponent c) {
					return new Transferable() {
						@Override
						public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
							Map<String, List<Tegeval>> contigs = new HashMap<String, List<Tegeval>>();
							StringBuilder ret = new StringBuilder();
							int[] rr = table.getSelectedRows();
							for (int r : rr) {
								int cr = table.convertRowIndexToModel(r);
								Gene gg = genelist.get(cr);
								if (gg.species != null) {
									// ret.append( gg.name + ":\n" );
									for (String sp : gg.species.keySet()) {
										Teginfo stv = gg.species.get(sp);
										for (Tegeval tv : stv.tset) {
											if (!contigs.containsKey(tv.contshort)) {
												List<Tegeval> ltv = new ArrayList<Tegeval>();
												ltv.add(tv);
												contigs.put(tv.getContshort().getName(), ltv);
											} else {
												List<Tegeval> ltv = contigs.get(tv.contshort);
												ltv.add(tv);
											}
											/*
											 * ret.append( ">" + tv.cont + " " +
											 * tv.teg + " " + tv.eval + "\n" );
											 * if( tv.dna != null ) { for( int i
											 * = 0; i < tv.dna.length(); i+=70 )
											 * { ret.append(tv.dna.substring( i,
											 * Math.min(i+70,tv.dna.length())
											 * )+"\n"); } }
											 */
										}
									}
								}
							}
							for (String cont : contigs.keySet()) {
								List<Tegeval> tv = contigs.get(cont);
								StringBuilder dna = tv.get(0).dna;
								ret.append(">" + cont + "\n"); // + " " + tv.teg
																// + " " +
																// tv.eval +
																// "\n" );
								if (dna != null) {
									for (int i = 0; i < dna.length(); i += 70) {
										ret.append(dna.substring(i, Math.min(i + 70, dna.length())) + "\n");
									}
								}
							}
							for (String cont : contigs.keySet()) {
								List<Tegeval> ltv = contigs.get(cont);
								ret.append(">" + cont + "\n"); // + " " + tv.teg
																// + " " +
																// tv.eval +
																// "\n" );
								for (Tegeval tv : ltv) {
									ret.append("erm\t#0000ff\t" + tv.start + "\t" + tv.stop + "\n");
								}
							}

							return new ByteArrayInputStream(ret.toString().getBytes());
						}

						@Override
						public DataFlavor[] getTransferDataFlavors() {
							return new DataFlavor[] { df };
						}

						@Override
						public boolean isDataFlavorSupported(DataFlavor arg0) {
							if (arg0.equals(df)) {
								return true;
							}
							return false;
						}
					};
				}

				public boolean importData(TransferHandler.TransferSupport support) {
					Object obj = null;

					System.err.println(support.getDataFlavors().length);
					int b = Arrays.binarySearch(support.getDataFlavors(), DataFlavor.javaFileListFlavor, new Comparator<DataFlavor>() {
						@Override
						public int compare(DataFlavor o1, DataFlavor o2) {
							return o1 == o2 ? 1 : 0;
						}
					});

					try {
						obj = support.getTransferable().getTransferData(DataFlavor.imageFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						if (obj != null && obj instanceof File[]) {
							// File[] ff = (File[])obj;
							// wbStuff( ff[0].getCanonicalPath() );
						} else if (obj instanceof Image) {

						} else {
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							System.err.println(obj);
							URL url = null;
							try {
								url = new URL((String) obj);
								Image image = ImageIO.read(url);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					return true;
				}
			};
			table.setTransferHandler(th);
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}

		table.setDefaultRenderer(Teginfo.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Teginfo tv = (Teginfo) value;
				if (tv == null) {
					label.setBackground(Color.white);
				} else {
					// label.setText( value.toString() );
					if (colorCodes[0] == null)
						GeneSet.setColors();
					if (tv.best.eval == 0) {
						label.setBackground(colorCodes[0]);
					} else if (tv.best.eval < 1e-100)
						label.setBackground(colorCodes[0]);
					else if (tv.best.eval < 1e-50)
						label.setBackground(colorCodes[1]);
					else if (tv.best.eval < 1e-24)
						label.setBackground(colorCodes[2]);
					else if (tv.best.eval < 1e-10)
						label.setBackground(colorCodes[3]);
					else if (tv.best.eval < 1e-5)
						label.setBackground(colorCodes[4]);
					else if (tv.best.eval < 1e-2)
						label.setBackground(colorCodes[5]);
					else if (tv.best.eval < 1e-1)
						label.setBackground(colorCodes[6]);
					else if (tv.best.eval < 1e0)
						label.setBackground(colorCodes[7]);
					else if (tv.best.eval < 1e10)
						label.setBackground(colorCodes[8]);
				}
				return label;
			}
		});

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		scrollpane.setViewportView(table);

		final JComboBox combo = new JComboBox();
		combo.addItem("Select pathway");

		final Map<String, Set<String>> pathwaymap = new TreeMap<String, Set<String>>();
		Set<String> current = null;
		InputStream is = GeneSet.class.getResourceAsStream("/kegg_pathways");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				current = new HashSet<String>();
				pathwaymap.put(line.substring(1), current);
			} else if (!line.startsWith("K")) {
				if (current != null) {
					String str = line.split("[\t ]+")[0];
					current.add(str);
				}
			}
			line = br.readLine();
		}
		br.close();

		Set<String> allecs = new HashSet<String>();
		for (Function f : funclist) {
			if (f.ec != null)
				allecs.add(f.ec);
		}

		for (String val : pathwaymap.keySet()) {
			Set<String> set = pathwaymap.get(val);
			for (String s : set) {
				if (allecs.contains(s)) {
					combo.addItem(val);
					break;
				}
			}
		}

		final JComboBox specombo = new JComboBox();
		specombo.addItem("Select blast species");

		Set<String> set = new TreeSet<String>();
		for (Gene g : genelist) {
			if (g.species != null) {
				for (String sp : g.species.keySet()) {
					Teginfo stv = g.species.get(sp);
					for (Tegeval tv : stv.tset) {
						if (tv.eval <= 0.00001 && tv.teg.startsWith("[") && tv.teg.endsWith("]"))
							set.add(tv.teg);
					}
				}
			}
		}

		for (String sp : set) {
			specombo.addItem(sp);
		}

		final JTextField textfield = new JTextField();
		JComponent topcomp = new JComponent() {
		};
		topcomp.setLayout(new BorderLayout());
		topcomp.add(scrollpane);

		final JLabel label = new JLabel();
		textfield.setPreferredSize(new Dimension(500, 25));
		JToolBar topcombo = new JToolBar();
		// topcombo.
		// topcombo.setLayout( new FlowLayout() );
		topcombo.add(combo);
		topcombo.add(specombo);
		topcomp.add(topcombo, BorderLayout.SOUTH);

		JComponent ttopcom = new JComponent() {
		};
		ttopcom.setLayout(new FlowLayout());
		final JCheckBox checkbox = new JCheckBox();
		checkbox.setAction(new AbstractAction("Sort by location") {
			@Override
			public void actionPerformed(ActionEvent e) {
				locsort = checkbox.isSelected();
			}
		});
		ttopcom.add(checkbox);

		final Set<String> species = new TreeSet<String>();
		final Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = initCluster(uclusterlist, species);
		final BufferedImage bimg = bmatrix(species, clusterMap);

		AbstractAction matrixaction = new AbstractAction("Relation matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Relation matrix");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500, 500);

				/*
				 * { public void paintComponent( Graphics g ) {
				 * super.paintComponent(g); g.drawImage(bimg, 0, 0, this); } };
				 */

				try {
					final DataFlavor df = new DataFlavor("text/plain;charset=utf-8");
					final Transferable transferable = new Transferable() {
						@Override
						public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
							StringBuilder ret = new StringBuilder();

							int i = 0;
							for (String spc : species) {
								if (++i == species.size())
									ret.append(spc + "\n");
								else
									ret.append(spc + "\t");
							}

							int where = 0;
							for (String spc1 : species) {
								int wherex = 0;
								for (String spc2 : species) {
									int spc1tot = 0;
									int spc2tot = 0;
									int totot = 0;

									int spc1totwocore = 0;
									int spc2totwocore = 0;
									int tototwocore = 0;
									for (Set<String> set : clusterMap.keySet()) {
										Set<Map<String, Set<String>>> erm = clusterMap.get(set);
										if (set.contains(spc1)) {
											if (set.size() < species.size()) {
												spc1totwocore += erm.size();
												for (Map<String, Set<String>> sm : erm) {
													Set<String> hset = sm.get(spc1);
													tototwocore += hset.size();
												}

												if (set.contains(spc2)) {
													spc2totwocore += erm.size();
												}

												if (spc2totwocore > spc1totwocore)
													System.err.println("okoko " + spc1totwocore + " " + spc2totwocore);
											}

											spc1tot += erm.size();
											for (Map<String, Set<String>> sm : erm) {
												Set<String> hset = sm.get(spc1);
												totot += hset.size();
											}

											if (set.contains(spc2)) {
												spc2tot += erm.size();
											}
										}
									}

									if (where == wherex) {
										if (where == species.size() - 1)
											ret.append(0 + "\n");
										else
											ret.append(0 + "\t");
									} else {
										double hlut = (double) spc2totwocore / (double) spc1totwocore;
										double sval = hlut; // 1.0/( 1.1-hlut );
										double val = Math.pow(50.0, sval - 0.3) - 1.0;
										double dval = Math.round(100.0 * (val)) / 100.0;

										if (wherex == species.size() - 1)
											ret.append(dval + "\n");
										else
											ret.append(dval + "\t");
									}
									wherex++;
								}
								where++;
							}

							return new ByteArrayInputStream(ret.toString().getBytes());
						}

						@Override
						public DataFlavor[] getTransferDataFlavors() {
							return new DataFlavor[] { df };
						}

						@Override
						public boolean isDataFlavorSupported(DataFlavor arg0) {
							if (arg0.equals(df)) {
								return true;
							}
							return false;
						}
					};
					final TransferComponent comp = new TransferComponent(bimg, transferable);

					TransferHandler th = new TransferHandler() {
						private static final long serialVersionUID = 1L;

						public int getSourceActions(JComponent c) {
							return TransferHandler.COPY_OR_MOVE;
						}

						public boolean canImport(TransferHandler.TransferSupport support) {
							return false;
						}

						protected Transferable createTransferable(JComponent c) {
							return transferable;
						}

						public boolean importData(TransferHandler.TransferSupport support) {
							return true;
						}
					};
					comp.setTransferHandler(th);

					comp.setEnabled(true);
					JScrollPane fsc = new JScrollPane(comp);
					comp.setPreferredSize(new Dimension(bimg.getWidth(), bimg.getHeight()));

					JPopupMenu	popup = new JPopupMenu();
					popup.add( new AbstractAction("Save image") {
						@Override
						public void actionPerformed(ActionEvent e) {
							FileSaveService fss = null;
					        FileContents fileContents = null;
					    	 
					        try {
					        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
						        OutputStreamWriter	osw = new OutputStreamWriter( baos );
								ImageIO.write(bimg, "png", baos);
								baos.close();

						    	try {
						    		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
						    	} catch( UnavailableServiceException e1 ) {
						    		fss = null;
						    	}
						    	 
						        if (fss != null) {
						        	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
						            fileContents = fss.saveFileDialog(null, null, bais, "export.png");
						            bais.close();
						            OutputStream os = fileContents.getOutputStream(true);
						            os.write( baos.toByteArray() );
						            os.close();
						        } else {
						        	JFileChooser jfc = new JFileChooser();
						        	if( jfc.showSaveDialog( applet ) == JFileChooser.APPROVE_OPTION ) {
						        		 File f = jfc.getSelectedFile();
						        		 FileOutputStream fos = new FileOutputStream( f );
						        		 fos.write( baos.toByteArray() );
						        		 fos.close();
						        		 
						        		 Desktop.getDesktop().browse( f.toURI() );
						        	}
						        }
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
					});
					comp.setComponentPopupMenu( popup );
					
					f.add(fsc);
					f.setVisible(true);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		};
		JButton matrixbutton = new JButton(matrixaction);
		
		AbstractAction freqdistaction = new AbstractAction("Freq dist") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Genome frequency distribution");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500, 500);

				final Map<Integer,Integer>	frqmap = new HashMap<Integer,Integer>();
				//int i = 0;
				Set<String> ss = new HashSet<String>();
				//Set<String> gs = new HashSet<String>();
				for (Set<String> cluster : uclusterlist) {
					ss.clear();
					//gs.clear();

					//Set<Gene> gset = new HashSet<Gene>();
					for (String cont : cluster) {
						String[] split = cont.split("_");
						ss.add(split[0]);
						//Gene g = locgene.get(cont);
						
						/*if (g != null) {
							gs.add(g.refid);
							gset.add(g);
						}*/
					}
					
					if( frqmap.containsKey( ss.size() ) ) {
						frqmap.put( ss.size(), frqmap.get(ss.size())+1 );
					} else frqmap.put( ss.size(), 1 );

					/*int val = 0;
					for (Gene g : gset) {
						if (g.species != null) {
							for (String str : g.species.keySet()) {
								val += g.species.get(str).tset.size();
							}
						}
					}

					for (Gene g : gset) {
						g.groupIdx = i;
						g.groupCoverage = ss.size();
						g.groupGenCount = gs.size();
						g.groupCount = val;
					}

					i++;*/
				}
				
				JScrollPane	jsp = new JScrollPane();
				JComponent	comp = new JComponent() {
					public void paintComponent( Graphics g ) {
						super.paintComponent(g);
						g.setColor( Color.white );
						g.fillRect(0, 0, this.getWidth(), this.getHeight());
						g.setColor( Color.blue );
						
						int min = 30;
						int max = 0;
						
						int minh = 100000000;
						int maxh = 0;
						
						for( Integer k : frqmap.keySet() ) {
							if( k > max ) max = k;
							if( k < min ) min = k;
							
							int h = frqmap.get( k );
							if( h > maxh ) maxh = h;
							if( h < minh ) minh = h;
						}
						
						if( minh != maxh ) {
							for( Integer k : frqmap.keySet() ) {
								int h = frqmap.get( k );
								int x = (k-min)*(this.getWidth()-20)/(max-min)+10;
								int y = -(h-minh)*(this.getHeight()-20)/(maxh-minh)+(this.getHeight()-10);
								g.fillOval(x-4, y-4, 8, 8);
							}
						}
					}
				};
				comp.setPreferredSize( new Dimension(800,600) );
				jsp.setViewportView( comp );
				f.add( jsp );
				f.setVisible( true );
			}
		};
		JButton freqdistbutton = new JButton(freqdistaction);
		
		AbstractAction presabsaction = new AbstractAction("Pres-Abs tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	check = new JCheckBox("Skip core");
				JCheckBox	align = new JCheckBox("Show alignment");
				JCheckBox	output = new JCheckBox("Output fasta");
				JOptionPane.showMessageDialog( comp, new Object[] {check, align, output} );
				
				Set<String>	selspec = getSelspec( applet, new ArrayList( species ) );
				
				boolean succ = true;
				String restext = null;
				if( !align.isSelected() ) {
					StringBuilder distmat = new StringBuilder();
					distmat.append("\t"+selspec.size()+"\n");
					for( String spec1 : selspec ) {
						distmat.append( spec1 );
						for( String spec2 : selspec ) {
							if( spec1.equals(spec2) ) distmat.append( "\t0.0" );
							else {
								int total = 0;
								int count = 0;
								for( Set<String> specset : clusterMap.keySet() ) {
									if( specset.size() > 1 && (!check.isSelected() || containmentCount(specset, selspec) < selspec.size()) ) {
										boolean b1 = specset.contains(spec1);
										boolean b2 = specset.contains(spec2);
										Set<Map<String,Set<String>>>	sm = clusterMap.get( specset );
										if( b1 || b2 ) {
											total += sm.size();
											if( b1 && b2 ) count += sm.size();
										}
									}/* else {
										System.err.println("blehbheh");
									}*/
								}
								distmat.append( "\t"+(double)(total-count)/(double)total );
							}
						}
						distmat.append("\n");
					}
					
					restext = distmat.toString();
					try {
						JSObject win = JSObject.getWindow( (Applet)comp );
						win.call("showTree", new Object[] { restext });
					} catch( Exception e1 ) {
						succ = false;
					}
				} else {
					succ = false;
					
					char one = output.isSelected() ? 'A' : '1';
					char zero = output.isSelected() ? 'C' : '0';
					
					Map<String,StringBuilder>	sbmap = new HashMap<String,StringBuilder>();
					for( Set<String> specset : clusterMap.keySet() ) {
						if( specset.size() > 1 && (!check.isSelected() || containmentCount(specset, selspec) < selspec.size()) ) {
							for( String spec : selspec ) {
								StringBuilder sb;
								if( sbmap.containsKey( spec ) ) {
									sb = sbmap.get( spec );
								} else {
									sb = new StringBuilder();
									sbmap.put( spec, sb );
								}
								
								Set<Map<String,Set<String>>> cset = clusterMap.get( specset );
								if( specset.contains( spec ) ) {
									for( int i = 0; i < cset.size(); i++ ) sb.append(one);
								} else {
									for( int i = 0; i < cset.size(); i++ ) sb.append(zero);
								}
							}
						}
					}
					
					/*StringBuilder sb = new StringBuilder();
					for( String s : sbmap.keySet() ) {
						sb.append( ">"+s+"\n" );
						StringBuilder subsb = sbmap.get( s );
						for( int i = 0; i < subsb.length(); i+=70 ) {
							sb.append( subsb.substring( i, Math.min( i+70, subsb.length() ) ) + "\n" );
						}
					}
					restext = sb.toString();*/
					
					List<Sequence> ls = new ArrayList<Sequence>();
					for( String s : sbmap.keySet() ) {
						StringBuilder sb = sbmap.get(s);
						Sequence seq = new Sequence( s, s, sb, null );
						ls.add( seq );
					}
					if( output.isSelected() ) {
						Serifier ser = new Serifier();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						OutputStreamWriter osw = new OutputStreamWriter( baos );
						try {
							ser.writeFasta(ls, osw, null);
							osw.close();
							baos.close();
							restext = baos.toString();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						restext = Sequence.getPhylip( ls, false );
					}
				}
				
				if( !succ ) {
					JFrame f = new JFrame("Pres-Abs dist matrix");
					f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					f.setSize( 800, 600 );
					
					JTextArea	ta = new JTextArea();
					ta.setText( restext );
					JScrollPane	sp = new JScrollPane(ta);
					f.add( sp );
					f.setVisible( true );
				}
			}
		};
		JButton presabsbutton = new JButton( presabsaction );
		
		AbstractAction	shuffletreeaction = new AbstractAction("Recomb tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>		selspec = getSelspec( applet, new ArrayList<String>( species ) );
				List<String>	speclist = new ArrayList<String>( selspec );
				double[] 		mat = new double[selspec.size()*selspec.size()];
				for( int y = 0; y < speclist.size(); y++ ) {
					mat[ y*speclist.size()+y ] = 0.0;
				}
				
				for( int y = 0; y < speclist.size(); y++ ) {
					String spec1 = speclist.get(y);
					final List<Tegeval> ltv = new ArrayList<Tegeval>();
					for (Gene g : genelist) {
						if (g.species != null) {
							for (String spec : g.species.keySet()) {
								Teginfo stv = g.species.get(spec);
								if (stv != null)
									for (Tegeval tv : stv.tset) {
										if (spec.equals(spec1)) {
											ltv.add(tv);
										}
										//tv.
	
										//int first = tv.cont.indexOf("_");
										//int sec = tv.cont.indexOf("_", first + 1);
	
										//String cname = tv.cont.substring(0, sec);
										//contigmap.put(cname, new Contig(cname));
									}
							}
							// }
						}
					}
					locsort = true;
					Collections.sort(ltv);
					
					for( int x = y+1; x < speclist.size(); x++ ) {
						String spec2 = speclist.get(x);
						
						final List<Tegeval> subltv = new ArrayList<Tegeval>();
						for (Gene g : genelist) {
							if (g.species != null) {
								for (String spec : g.species.keySet()) {
									Teginfo stv = g.species.get(spec);
									if (stv != null)
										for (Tegeval tv : stv.tset) {
											if (spec.equals(spec2)) {
												subltv.add(tv);
											}
											//tv.
		
											//int first = tv.cont.indexOf("_");
											//int sec = tv.cont.indexOf("_", first + 1);
		
											//String cname = tv.cont.substring(0, sec);
											//contigmap.put(cname, new Contig(cname));
										}
								}
								// }
							}
						}
						locsort = true;
						Collections.sort(subltv);
						
						int count = 0;
						for( int i = 0; i < ltv.size()-1; i++ ) {
							Tegeval tv1 = ltv.get(i);
							Tegeval tv2 = ltv.get(i+1);
							
							GeneGroup gg1 = tv1.getGene().getGeneGroup();
							GeneGroup gg2 = tv2.getGene().getGeneGroup();
							
							if( gg1 == null || gg2 == null ) {
								System.err.println( tv1 + "   " + tv2 );
							}
							
							if( gg1 != null && gg1.getGroupGeneCount() < 500 && gg2 != null && gg2.getGroupGeneCount() < 500 ) {
								for( int k = 0; k < subltv.size()-1; k++ ) {
									Tegeval subtv1 = subltv.get(k);
									Tegeval subtv2 = subltv.get(k+1);
									
									GeneGroup sgg1 = subtv1.getGene().getGeneGroup();
									GeneGroup sgg2 = subtv2.getGene().getGeneGroup();
									
									if( (sgg1 == gg1 && sgg2 == gg2) || (sgg1 == gg2 && sgg2 == gg1) ) {
										count++;
										break;
									}
								}
							}
							
							/*if( gg1.species.contains(spec2) && gg2.species.contains(spec2) ) {
								final List<Tegeval> ltv1 = new ArrayList<Tegeval>();
								for( Gene g : gg1.genes ) {
									if (g.species != null) {
										for (String spec : g.species.keySet()) {
											Teginfo stv = g.species.get(spec);
											if (stv != null)
												for (Tegeval tv : stv.tset) {
													if (spec.equals(spec1)) {
														ltv1.add(tv);
													}
												}
										}
									}
								}
								final List<Tegeval> ltv2 = new ArrayList<Tegeval>();
								for( Gene g : gg2.genes ) {
									if (g.species != null) {
										for (String spec : g.species.keySet()) {
											Teginfo stv = g.species.get(spec);
											if (stv != null)
												for (Tegeval tv : stv.tset) {
													if (spec.equals(spec1)) {
														ltv2.add(tv);
													}
												}
										}
									}
								}
								
								for( Tegeval tev1 : ltv1 ) {
									for( Tegeval tev2 : ltv2 ) {
										System.err.println( tev1.cont + "  " + tev2.cont );
									}
								}
								//Collections.sort(ltv1);
								//Collections.sort(ltv2);
							}*/
						}
						mat[ y*speclist.size() + x ] = count;
						mat[ x*speclist.size() + y ] = count;
					}
				}
				
				System.err.print("\t"+speclist.size());
				for( int i = 0; i < mat.length; i++ ) {
					if( i % speclist.size() == 0 ) System.err.print("\n"+speclist.get(i/speclist.size())+"\t"+(mat[i] == 0 ? 0.0 : 2100-mat[i]));
					else System.err.print("\t"+(mat[i] == 0 ? 0.0 : 2100-mat[i]));
				}
				System.err.println();
				
				/*				
				List<Tegeval>	spec1eval = new ArrayList<Tegeval>();
				List<Tegeval>	spec2eval = new ArrayList<Tegeval>();
				
				double[] mat = new double[selspec.size()*selspec.size()];
				for( int y = 0; y < speclist.size(); y++ ) {
					String spec1 = speclist.get(y);
					for( int x = 0; x < speclist.size(); x++ ) {
						String spec2 = speclist.get(x);
						if( spec1.equals( spec2 ) ) {
							mat[y*speclist.size()+x] = 0.0;
						} else {
							for( Set<String> specset : ggSpecMap.keySet() ) {
								boolean b1 = specset.contains(spec1);
								boolean b2 = specset.contains(spec2);
								if( b1 && b2 ) {
									List<GeneGroup> gglist = ggSpecMap.get( specset );
									Teginfo spec1sel = null;
									Teginfo sepc2sel = null;
									for( GeneGroup gg : gglist ) {
										for( Gene g : gg.genes ) {
											if( g.species.containsKey(spec1) ) {
												if( spec1sel != null ) {
													spec1sel = null;
													break;
												} else spec1sel = spec1;
											}
										}
									}
								}
							}
						}
					}
				}
				/*String restext = null;
				StringBuilder distmat = new StringBuilder();
				distmat.append("\t"+selspec.size()+"\n");
				for( String spec1 : selspec ) {
					distmat.append( spec1 );
					for( String spec2 : selspec ) {
						if( spec1.equals(spec2) ) distmat.append( "\t0.0" );
						else {
							int total = 0;
							int count = 0;
							for( Set<String> specset : clusterMap.keySet() ) {
								if( !check.isSelected() || containmentCount(specset, selspec) < selspec.size() ) {
									boolean b1 = specset.contains(spec1);
									boolean b2 = specset.contains(spec2);
									Set<Map<String,Set<String>>>	sm = clusterMap.get( specset );
									if( b1 || b2 ) {
										total += sm.size();
										if( b1 && b2 ) count += sm.size();
									}
								}/* else {
									System.err.println("blehbheh");
								}*
							}
							distmat.append( "\t"+(double)(total-count)/(double)total );
						}
					}
					distmat.append("\n");
				}*/
				
				/*Set<String>	emap = null;
				for( Set<String> gmap : ggSpecMap.keySet() ) {
					if( emap == null || emap.size() < gmap.size() ) emap = gmap;
				}
				List<GeneGroup>	lgg = ggSpecMap.get( emap );
				List<GeneGroup>	slgg = new ArrayList<GeneGroup>();*/
				
				/*for( GeneGroup gg : lgg ) {
					for( Gene g : gg.genes ) {
						if( g.groupCoverage == g.groupCount ) {
							System.err.println( g.groupCount );
							slgg.add( gg );
						}
						
						break;
					}
				}*/
				
				//System.err.println( "slgg " + slgg.size() + "  " + lgg.size() );
				
				/*Set<String>	selspec = getSelspec( applet, new ArrayList( species ) );
				StringBuilder distmat = new StringBuilder();
				distmat.append("\t"+selspec.size()+"\n");
				for( String spec1 : selspec ) {
					distmat.append( spec1 );
					for( String spec2 : selspec ) {
						if( spec1.equals(spec2) ) distmat.append( "\t0.0" );
						else {
							
							
							int total = 0;
							int count = 0;
							for( Set<String> specset : clusterMap.keySet() ) {
								System.err.println("asdf");
								
								/*if( !check.isSelected() || containmentCount(specset, selspec) < selspec.size() ) {
									boolean b1 = specset.contains(spec1);
									boolean b2 = specset.contains(spec2);
									Set<Map<String,Set<String>>>	sm = clusterMap.get( specset );
									if( b1 || b2 ) {
										total += sm.size();
										if( b1 && b2 ) count += sm.size();
									}
								}/* else {
									System.err.println("blehbheh");
								}*
							}
							distmat.append( "\t"+(double)(total-count)/(double)total );
						}
					}
					distmat.append("\n");
				}
				
				boolean	succ = true;
				String restext = distmat.toString();
				
				//TreeUtil treeutil = new TreeUtil();
				//treeutil.neighborJoin( newcorr, corrInd, null, true, true );
				
				try {
					JSObject win = JSObject.getWindow( (Applet)comp );
					win.call("showTree", new Object[] { restext });
				} catch( Exception e1 ) {
					succ = false;
				}
				
				if( !succ ) {
					JFrame f = new JFrame("Shuffle tree");
					f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					f.setSize( 800, 600 );
					
					JTextArea	ta = new JTextArea();
					ta.setText( restext );
					JScrollPane	sp = new JScrollPane(ta);
					f.add( sp );
					f.setVisible( true );
				}*/
			}
		};
		JButton	shuffletreebutton = new JButton( shuffletreeaction );
		
		AbstractAction pancoreaction = new AbstractAction("Pan-core") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	selspec = getSelspec( applet, new ArrayList( species ) );
				
				Set<GeneGroup>	pan = new HashSet<GeneGroup>();
				Set<GeneGroup>	core = new HashSet<GeneGroup>();
				StringBuilder	restext = new StringBuilder();
				for( String spec : selspec ) {
					restext.append( spec );
					Set<GeneGroup> ggset = specGroupMap.get( spec );
					pan.addAll( ggset );
					if( core.isEmpty() ) core.addAll( ggset );
					else core.retainAll( ggset );
					
					restext.append( "\t"+core.size() );
					restext.append( "\t"+pan.size()+"\n" );
				}
				
				JFrame f = new JFrame("Pan-core chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
			}
		};
		
		AbstractAction fetchcoreaction = new AbstractAction("Fetch core") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	selspec = getSelspec( applet, new ArrayList( species ) );
				
				JFrame frame = null;
				if( currentSerify == null ) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);
					
					SerifyApplet sa = new SerifyApplet();
					sa.init( frame );
					//frame.add( )
					currentSerify = sa;
				} else {
					currentSerify.clearSequences();
					frame = (JFrame)currentSerify.cnt;
				}

				//Map<Integer,String>			ups = new HashMap<Integer,String>();
				//Set<Integer>				stuck = new HashSet<Integer>();
				//Map<Integer,List<Tegeval>>	ups2 = new HashMap<Integer,List<Tegeval>>();
				//int[] rr = table.getSelectedRows();
				List<GeneGroup>	includedGroups = new ArrayList<GeneGroup>();
				for( GeneGroup genegroup : allgenegroups ) {
					//int cr = table.convertRowIndexToModel(r);
					//Gene gg = genelist.get(cr);
					if( genegroup.isSingluar() && genegroup.getSpecies().containsAll(selspec) ) {
						includedGroups.add( genegroup );
					//if (gg.species != null) {
						/*if( gg.genid != null && gg.genid.length() > 0 ) {
							ups.put( gg.getGroupIndex(), gg.name );
							stuck.add( gg.getGroupIndex() );
						}
						if( !stuck.contains(gg.getGroupIndex()) ) {
							if( !ups.containsKey(gg.getGroupIndex()) || !(gg.name.contains("unnamed") || gg.name.contains("hypot")) ) ups.put( gg.getGroupIndex(), gg.name );
						}
						
						List<Tegeval>	tlist;
						if( ups2.containsKey( gg.getGroupIndex() ) ) tlist = ups2.get( gg.getGroupIndex() );
						else {
							tlist = new ArrayList<Tegeval>();
							ups2.put( gg.getGroupIndex(), tlist );
						}
						
						//Set<String>	 specs = new HashSet<String>();
						//textarea.append(gg.name + ":\n");
						//for (String sp : gg.species.keySet()) {
						int count = 0;
						for(String sp : farr) {
							Teginfo stv = gg.species.get(sp);
							if( stv == null ) {
								//System.err.println( sp );
							} else {
								count++;
								//specs.add( sp );
								for (Tegeval tv : stv.tset) {
									tlist.add( tv );
								}
							}
						}
						if( count < gg.species.size() ) {
							System.err.println( gg.species );
							System.err.println();
						}*/
						//if( specs.size() < 28 ) System.err.println("mu " + specs);
					}
				}
				
				Map<Function,Set<GeneGroup>> fggmap = new HashMap<Function,Set<GeneGroup>>();
				for( GeneGroup genegroup : includedGroups ) {
					Set<Function>	funcset = genegroup.getFunctions();
					for( Function f : funcset ) {
						Set<GeneGroup>	sgg;
						if( !fggmap.containsKey( f ) ) {
							sgg = new HashSet<GeneGroup>();
							fggmap.put(f, sgg);
						} else sgg = fggmap.get( f );
						sgg.add( genegroup );
					}
				}
				
				Set<Function> delset = new HashSet<Function>();
				for( Function f1 : fggmap.keySet() ) {
					Set<GeneGroup> sgg1 = fggmap.get(f1);
					for( Function f2 : fggmap.keySet() ) {
						if( !f1.equals(f2) ) {
							Set<GeneGroup> sgg2 = fggmap.get(f2);
							if( sgg1.containsAll( sgg2 ) ) {
								delset.add( f2 );
							}
						}
					}
				}
				for( Function f : delset ) {
					fggmap.remove( f );
				}
				
				StringBuilder sb = new StringBuilder();
				//for( int gi : ups.keySet() ) {
				for( GeneGroup genegroup : includedGroups ) {
					String name = genegroup.getCommonName(); //ups.get(gi);
					List<Tegeval>	tlist = genegroup.getTegevals( selspec ); //ups2.get(gi);
					
					sb.append( "[" + genegroup.getCommonFunction( false, fggmap.keySet() ) + "]" + genegroup.groupIndex + "_" + name.replace('/', '-') + ":\n");
					/*if( tlist.size() < 28 ) {
						for( Tegeval tv : tlist ) {
							System.err.println( tv.cont );
						}
						System.err.println();
					}*/
					for( Tegeval tv : tlist ) {
						sb.append(">" + tv.cont.substring(0, tv.cont.indexOf('_')) + "\n");
						if (tv.seq != null) {
							for (int i = 0; i < tv.seq.length(); i += 70) {
								sb.append(tv.seq.substring(i, Math.min(i + 70, tv.seq.length())) + "\n");
							}
						}
					}
				}
				
				try {
					String fastaStr = sb.toString();
					FileWriter fw = new FileWriter("/root/erm.fasta");
					fw.write( fastaStr );
					fw.close();
					
					currentSerify.addSequences("uh", new StringReader( fastaStr ), "/");
				} catch (URISyntaxException | IOException e1) {
					e1.printStackTrace();
				}
				frame.setVisible(true);
				/*Set<GeneGroup>	pan = new HashSet<GeneGroup>();
				Set<GeneGroup>	core = new HashSet<GeneGroup>();
				StringBuilder	restext = new StringBuilder();
				for( String spec : selspec ) {
					restext.append( spec );
					Set<GeneGroup> ggset = specGroupMap.get( spec );
					pan.addAll( ggset );
					if( core.isEmpty() ) core.addAll( ggset );
					else core.retainAll( ggset );
					
					restext.append( "\t"+core.size() );
					restext.append( "\t"+pan.size()+"\n" );
				}
				
				JFrame f = new JFrame("Pan-core chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );*/
			}
		};
		
		AbstractAction loadcontiggraphaction = new AbstractAction("Load contig graph") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int res = jfc.showOpenDialog( comp );
				if( res == JFileChooser.APPROVE_OPTION ) {
					File file = jfc.getSelectedFile();
					try {
						List<String> lines = Files.readAllLines( Paths.get( file.toURI() ), Charset.defaultCharset());
						double scaleval = 400.0;
						
						Connectron	connectron = new Connectron();
						connectron.importFrom454ContigGraph(lines, scaleval);
						
						Collections.sort( Corp.corpList, new Comparator<Corp>() {
							@Override
							public int compare(Corp o1, Corp o2) {
								return o1.connections.size()+o1.backconnections.size()-o2.connections.size()-o2.backconnections.size();
							}
						});
						
						for( Corp c : Corp.corpList ) {
							System.err.println( c.getName() + " " + c.connections.size() + "   " + c.backconnections.size() );
						}
						
						
						
						for( Corp c : Corp.corpList ) {
							if( c.connections.size() > 1 && c.backconnections.size() > 1 ) {
								System.err.println( c.getName() );
								System.err.println( "cm " + contigmap.size() );
							}
						}
						
						JFrame f = new JFrame();
						f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
						f.setSize(800, 600);
						try {
							connectron.initGUI( f );
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
						f.add( connectron.scrollpane );
						f.setVisible(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		
		JMenuBar	menubar = new JMenuBar();
		JMenu		menu = new JMenu("Functions");
		menu.add( shuffletreeaction );
		menu.add( presabsaction );
		menu.add( freqdistaction );
		menu.add( matrixaction );
		menu.add( pancoreaction );
		menu.add( fetchcoreaction );
		menu.add( loadcontiggraphaction );
		
		menubar.add( menu );
		ttopcom.add( menubar );
		
		JMenu		view = new JMenu("View");
		menubar.add( view );
		view.add( new AbstractAction("Genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( defaultModel );
			}
		});
		view.add( new AbstractAction("Gene Groups") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( groupModel );
			}
		});
		//ttopcom.add( shuffletreebutton );
		//ttopcom.add( presabsbutton );
		//ttopcom.add(freqdistbutton);
		//ttopcom.add(matrixbutton);
		
		ttopcom.add(textfield);
		ttopcom.add(label);

		AbstractAction saveselAction = new AbstractAction("Save selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				if( rr.length > 0 ) {
					String val = Integer.toString( table.convertRowIndexToModel(rr[0]) );
					for( int i = 1; i < rr.length; i++ ) {
						val += ","+table.convertRowIndexToModel(rr[i]);
					}
					String selname = JOptionPane.showInputDialog("Selection name");
					if( comp instanceof Applet ) {
						try {
							((GeneSet)comp).saveSel( selname, val);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		JButton saveselButt = new JButton(saveselAction);
		ttopcom.add(saveselButt);

		selcomblocal.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String key = (String)e.getItem();
				if( ((GeneSet)comp).selectionMap.containsKey(key) ) {
					Set<Integer> val = ((GeneSet)comp).selectionMap.get(key);
					if( val != null ) {
						table.clearSelection();
						for( int i : val ) {
							int r = table.convertRowIndexToView(i);
							table.addRowSelectionInterval(r, r);
						}
					} else {
						System.err.println( "null "+key );
					}
				} else {
					System.err.println( "no "+key );
				}
			}
		});
		ttopcom.add(selcomblocal);
		topcomp.add(ttopcom, BorderLayout.NORTH);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
				if (gsplitpane != null)
					gsplitpane.repaint();
			}
		});

		JButton but = new JButton(new AbstractAction("Compare") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mynd(genelist, table, "t.scotoductusSA01");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton butgroup = new JButton(new AbstractAction("Compare groups") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					groupMynd(allgenegroups, genelist, table);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton neighbut = new JButton(new AbstractAction("Neighbourhood") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Set<Gene>	geneset = new HashSet<Gene>();
					int[] rr = table.getSelectedRows();
					for( int rowIndex : rr ) {
						int r = table.convertRowIndexToModel( rowIndex );
						Gene gene = genelist.get( r );
						geneset.add( gene );
					}
					neighbourMynd( comp, genelist, table, geneset);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JScrollPane fscrollpane = new JScrollPane();
		final JTextField ftextfield = new JTextField();
		JComponent botcomp = new JComponent() {
		};
		botcomp.setLayout(new BorderLayout());
		botcomp.add(fscrollpane);

		// JButton sbutt = new JButton("Find conserved terms");
		ftextfield.setPreferredSize(new Dimension(500, 25));
		JComponent botcombo = new JComponent() {
		};
		botcombo.setLayout(new FlowLayout());
		botcombo.add( ftextfield );
		botcombo.add( but );
		botcombo.add( butgroup );
		botcombo.add( neighbut );

		JComboBox scombo = new JComboBox();
		scombo.addItem("5S/8S");
		scombo.addItem("16S/18S");
		scombo.addItem("23S/28S");
		scombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String name = e.getItem().toString().split("/")[0];
					InputStream is = GeneSet.class.getResourceAsStream("/all" + name + ".fsa");
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);

					JTextArea textarea = new JTextArea();
					JScrollPane scrollpane = new JScrollPane(textarea);

					try {
						String line = br.readLine();
						while (line != null) {
							textarea.append(line + "\n");

							line = br.readLine();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.add(scrollpane);
					frame.setSize(400, 300);
					frame.setVisible(true);
				}
			}
		});
		botcombo.add(scombo);

		JButton swsearch = new JButton(new AbstractAction("SW Search") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComponent c = new JComponent() {
				};
				final JProgressBar pb = new JProgressBar();
				final JTextArea textarea = new JTextArea();
				JButton searchbut = new JButton(new AbstractAction("Blast") {
					@Override
					public void actionPerformed(ActionEvent e) {
						final String fasta = textarea.getText();
						final SmithWater sw = new SmithWater();
						final InputStream is = GeneSet.class.getResourceAsStream("/allthermus.aa");
						new Thread() {
							public void run() {
								try {
									sw.fasta_align(new StringReader(fasta), new InputStreamReader(is), pb);
									List<SmithWater.ALN> alns = sw.getAlignments();
									SmithWater.ALN first = null;
									int count = 0;
									String result = "";
									Set<String> regnames = new HashSet<String>();
									for (SmithWater.ALN aln : alns) {
										if (first == null) {
											first = aln;
										} else if (aln.score < 3.0f * (first.score / 4.0f))
											break;
										result += aln.toString();
										regnames.add(aln.getShortDestName());

										if (++count == 10)
											break;
									}
									textarea.setText(result);

									for (Gene g : genelist) {
										if (g.species != null) {
											for (String teg : g.species.keySet()) {
												boolean found = false;
												Teginfo stv = g.species.get(teg);
												for (Tegeval tv : stv.tset) {
													if (regnames.contains(tv.cont)) {
														found = true;
														break;
													}
												}
												if (found) {
													int rr = table.convertRowIndexToView(g.index);
													if (rr != -1)
														table.addRowSelectionInterval(rr, rr);
													break;
												}
											}
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}.start();
					}
				});
				c.setLayout(new BorderLayout());
				JScrollPane scrollpane = new JScrollPane(textarea);
				c.add(scrollpane);
				c.add(pb, BorderLayout.NORTH);
				c.add(searchbut, BorderLayout.SOUTH);

				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(c);
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});
		botcombo.add(swsearch);
		botcombo.add(jb);

		// botcombo.add( sbutt );
		botcomp.add(botcombo, BorderLayout.SOUTH);

		splitpane.setBottomComponent(botcomp);
		splitpane.setTopComponent(topcomp);

		groupModel = new TableModel() {
			@Override
			public int getRowCount() {
				return allgenegroups.size();
			}

			@Override
			public int getColumnCount() {
				return 47;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) {
					return "Desc";
				} else if (columnIndex == 1) {
					return "Origin";
				} else if (columnIndex == 2) {
					return "Genid";
				} else if (columnIndex == 3) {
					return "Refid";
				} else if (columnIndex == 4) {
					return "Unid";
				} else if (columnIndex == 5) {
					return "Keggid";
				} else if (columnIndex == 6) {
					return "Pdbid";
				} else if (columnIndex == 7) {
					return "Present in";
				} else if (columnIndex == 8) {
					return "Group index";
				} else if (columnIndex == 9) {
					return "Group coverage";
				} else if (columnIndex == 10) {
					return "Group size";
				} else if (columnIndex == 11) {
					return "Locprev";
				} else if (columnIndex == 12) {
					return "Avg GC%";
				} else if (columnIndex == 13) {
					return "# of locus";
				} else if (columnIndex == 14) {
					return "# of loc in group";
				} else if (columnIndex == 15) {
					return "max length";
				} else if (columnIndex == 16) {
					return "sharing number";
				} else if (columnIndex == 17) {
					return "# Cyc";
				} else if (columnIndex == 18) {
					return "16S Corr";
				} else if (columnIndex == 19) {
					return "T.tSG0";
				} else if (columnIndex == 20) {
					return "T.tJL18";
				} else if (columnIndex == 21) {
					return "T.tHB8";
				} else if (columnIndex == 22) {
					return "T.tHB27";
				} else if (columnIndex == 23) {
					return "T.scotoSA01";
				} else if (columnIndex == 24) {
					return "T.aqua";
				} else if (columnIndex == 25) {
					return "T.eggert";
				} else if (columnIndex == 26) {
					return "T.island";
				} else if (columnIndex == 27) {
					return "T.antan";
				} else if (columnIndex == 28) {
					return "T.scoto346";
				} else if (columnIndex == 29) {
					return "T.scoto1572";
				} else if (columnIndex == 30) {
					return "T.scoto252";
				} else if (columnIndex == 31) {
					return "T.scoto2101";
				} else if (columnIndex == 32) {
					return "T.scoto2127";
				} else if (columnIndex == 33) {
					return "T.scoto4063";
				} else if (columnIndex == 34) {
					return "T.oshimai";
				} else if (columnIndex == 35) {
					return "T.brockianus";
				} else if (columnIndex == 36) {
					return "T.filiformis";
				} else if (columnIndex == 37) {
					return "T.igniterrae";
				} else if (columnIndex == 38) {
					return "T.kawarayensis";
				} else if (columnIndex == 39) {
					return "T.arciformis";
				} else if (columnIndex == 40) {
					return "T.spCCB";
				} else if (columnIndex == 41) {
					return "T.spRLM";
				} else if (columnIndex == 42) {
					return "T.oshimaiJL2";
				} else if (columnIndex == 43) {
					return "MT.silvianus";
				} else if (columnIndex == 44) {
					return "MT.ruber";
				} else if (columnIndex == 45) {
					return "M.hydro";
				} else if (columnIndex == 46) {
					return "O.profu";
				}
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 9 || columnIndex == 12 || columnIndex == 18 )
					return Double.class;
				else if(columnIndex >= 6 && columnIndex <= 17)
					return Integer.class;
				else if (columnIndex >= 19)
					return Teginfo.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				GeneGroup gg = allgenegroups.get(rowIndex);
				if (columnIndex == 0) {
					return gg.getCommonName();
				} else if (columnIndex == 1) {
					return null;//gene.origin;
				} else if (columnIndex == 2) {
					return null;//gene.genid;
				} else if (columnIndex == 3) {
					return null;//gene.refid;
				} else if (columnIndex == 4) {
					return null;//gene.uniid;
				} else if (columnIndex == 5) {
					return gg.getKeggid();
				} else if (columnIndex == 6) {
					return null;//gene.pdbid;
				} else if (columnIndex == 7) {
					return gg.getSpecies().size();
				} else if (columnIndex == 8) {
					return gg.groupIndex;
				} else if (columnIndex == 9) {
					return gg.getGroupCoverage();
				} else if (columnIndex == 10) {
					return gg.getGroupGeneCount();
				} else if (columnIndex == 11) {
					return null;//gene.proximityGroupPreservation;
				} else if (columnIndex == 12) {
					return gg.getAvgGCPerc();
				} else if (columnIndex == 13) {
					int val = 0;
					for( Gene gene : gg.genes ) {
						for (String str : gene.species.keySet()) {
							val += gene.species.get(str).tset.size();
						}
					}
					return val;
				} else if (columnIndex == 14) {
					return gg.getGroupCount();
				} else if (columnIndex == 15) {
					int max = 0;
					for( Gene gene : gg.genes ) {
						for (String str : gene.species.keySet()) {
							Teginfo set = gene.species.get(str);
							for (Tegeval tv : set.tset) {
								if (tv.seq != null)
									max = Math.max(max, tv.seq.length());
							}
						}
					}
					return max;
				} else if (columnIndex == 16) {
					return specset.get( gg.getSpecies() );
				} else if (columnIndex == 17) {
					int max = 0;
					for( Gene gene : gg.genes ) {
						for (String str : gene.species.keySet()) {
							Teginfo set = gene.species.get(str);
							for (Tegeval tv : set.tset) {
								max = Math.max(max, tv.numCys);
							}
						}
					}
					return max;
				} else if (columnIndex == 18) {
					return gg.getGroupCoverage() == 28 && gg.getGroupCount() == 28 ? 0 : -1;
				} else if (columnIndex == 19) {
					return getGroupTes( gg, "t.thermophilusSG0" );
				} else if (columnIndex == 20) {
					return getGroupTes( gg, "t.thermophilusJL18");
				} else if (columnIndex == 21) {
					return getGroupTes( gg, "t.thermophilusHB8");
				} else if (columnIndex == 22) {
					return getGroupTes( gg, "t.thermophilusHB27");
				} else if (columnIndex == 23) {
					return getGroupTes( gg, "t.scotoductusSA01");
				} else if (columnIndex == 24) {
					return getGroupTes( gg, "t.aquaticus");
				} else if (columnIndex == 25) {
					return getGroupTes( gg, "t.eggertsoni");
				} else if (columnIndex == 26) {
					return getGroupTes( gg, "t.islandicus");
				} else if (columnIndex == 27) {
					return getGroupTes( gg, "t.antranikiani");
				} else if (columnIndex == 28) {
					return getGroupTes( gg, "t.scotoductus346");
				} else if (columnIndex == 29) {
					return getGroupTes( gg, "t.scotoductus1572");
				} else if (columnIndex == 30) {
					return getGroupTes( gg, "t.scotoductus252");
				} else if (columnIndex == 31) {
					return getGroupTes( gg, "t.scotoductus2101");
				} else if (columnIndex == 32) {
					return getGroupTes( gg, "t.scotoductus2127");
				} else if (columnIndex == 33) {
					return getGroupTes( gg, "t.scotoductus4063");
				} else if (columnIndex == 34) {
					return getGroupTes( gg, "t.oshimai");
				} else if (columnIndex == 35) {
					return getGroupTes( gg, "t.brockianus");
				} else if (columnIndex == 36) {
					return getGroupTes( gg, "t.filiformis");
				} else if (columnIndex == 37) {
					return getGroupTes( gg, "t.igniterrae");
				} else if (columnIndex == 38) {
					return getGroupTes( gg, "t.kawarayensis");
				} else if (columnIndex == 39) {
					return getGroupTes( gg, "t.arciformis");
				} else if (columnIndex == 40) {
					return getGroupTes( gg, "t.spCCB");
				} else if (columnIndex == 41) {
					return getGroupTes( gg, "t.RLM");
				} else if (columnIndex == 42) {
					return getGroupTes( gg, "t.oshimaiJL2");
				} else if (columnIndex == 43) {
					return getGroupTes( gg, "mt.silvanus");
				} else if (columnIndex == 44) {
					return getGroupTes( gg, "mt.ruber");
				} else if (columnIndex == 45) {
					return getGroupTes( gg, "m.hydrothermalis");
				} else if (columnIndex == 46) {
					return getGroupTes( gg, "o.profundus");
				}
				return columnIndex >= 11 ? null : "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}
		};
		defaultModel = new TableModel() {
			@Override
			public int getRowCount() {
				return genelist.size();
			}

			@Override
			public int getColumnCount() {
				return 47;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) {
					return "Desc";
				} else if (columnIndex == 1) {
					return "Origin";
				} else if (columnIndex == 2) {
					return "Genid";
				} else if (columnIndex == 3) {
					return "Refid";
				} else if (columnIndex == 4) {
					return "Unid";
				} else if (columnIndex == 5) {
					return "Keggid";
				} else if (columnIndex == 6) {
					return "Pdbid";
				} else if (columnIndex == 7) {
					return "Present in";
				} else if (columnIndex == 8) {
					return "Group index";
				} else if (columnIndex == 9) {
					return "Group coverage";
				} else if (columnIndex == 10) {
					return "Group size";
				} else if (columnIndex == 11) {
					return "Locprev";
				} else if (columnIndex == 12) {
					return "Avg GC%";
				} else if (columnIndex == 13) {
					return "# of locus";
				} else if (columnIndex == 14) {
					return "# of loc in group";
				} else if (columnIndex == 15) {
					return "max length";
				} else if (columnIndex == 16) {
					return "sharing number";
				} else if (columnIndex == 17) {
					return "# Cyc";
				} else if (columnIndex == 18) {
					return "16S Corr";
				} else if (columnIndex == 19) {
					return "T.tSG0";
				} else if (columnIndex == 20) {
					return "T.tJL18";
				} else if (columnIndex == 21) {
					return "T.tHB8";
				} else if (columnIndex == 22) {
					return "T.tHB27";
				} else if (columnIndex == 23) {
					return "T.scotoSA01";
				} else if (columnIndex == 24) {
					return "T.aqua";
				} else if (columnIndex == 25) {
					return "T.eggert";
				} else if (columnIndex == 26) {
					return "T.island";
				} else if (columnIndex == 27) {
					return "T.antan";
				} else if (columnIndex == 28) {
					return "T.scoto346";
				} else if (columnIndex == 29) {
					return "T.scoto1572";
				} else if (columnIndex == 30) {
					return "T.scoto252";
				} else if (columnIndex == 31) {
					return "T.scoto2101";
				} else if (columnIndex == 32) {
					return "T.scoto2127";
				} else if (columnIndex == 33) {
					return "T.scoto4063";
				} else if (columnIndex == 34) {
					return "T.oshimai";
				} else if (columnIndex == 35) {
					return "T.brockianus";
				} else if (columnIndex == 36) {
					return "T.filiformis";
				} else if (columnIndex == 37) {
					return "T.igniterrae";
				} else if (columnIndex == 38) {
					return "T.kawarayensis";
				} else if (columnIndex == 39) {
					return "T.arciformis";
				} else if (columnIndex == 40) {
					return "T.spCCB";
				} else if (columnIndex == 41) {
					return "T.spRLM";
				} else if (columnIndex == 42) {
					return "T.oshimaiJL2";
				} else if (columnIndex == 43) {
					return "MT.silvianus";
				} else if (columnIndex == 44) {
					return "MT.ruber";
				} else if (columnIndex == 45) {
					return "M.hydro";
				} else if (columnIndex == 46) {
					return "O.profu";
				}
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 9 || columnIndex == 12 || columnIndex == 18 )
					return Double.class;
				else if(columnIndex >= 6 && columnIndex <= 17)
					return Integer.class;
				else if (columnIndex >= 19)
					return Teginfo.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Gene gene = genelist.get(rowIndex);
				if (columnIndex == 0) {
					return gene.name;
				} else if (columnIndex == 1) {
					return gene.origin;
				} else if (columnIndex == 2) {
					return gene.genid;
				} else if (columnIndex == 3) {
					return gene.refid;
				} else if (columnIndex == 4) {
					return gene.uniid;
				} else if (columnIndex == 5) {
					return gene.keggid;
				} else if (columnIndex == 6) {
					return gene.pdbid;
				} else if (columnIndex == 7) {
					return gene.species == null ? -1 : gene.species.size();
				} else if (columnIndex == 8) {
					return gene.getGroupIndex();
				} else if (columnIndex == 9) {
					return gene.getGroupCoverage();
				} else if (columnIndex == 10) {
					return gene.getGroupGenCount();
				} else if (columnIndex == 11) {
					return gene.proximityGroupPreservation;
				} else if (columnIndex == 12) {
					return gene.getAvgGCPerc();
				} else if (columnIndex == 13) {
					if (gene.species != null) {
						int val = 0;
						for (String str : gene.species.keySet()) {
							val += gene.species.get(str).tset.size();
						}
						return val;
					}
				} else if (columnIndex == 14) {
					return gene.getGroupCount();
				} else if (columnIndex == 15) {
					if (gene.species != null) {
						int max = 0;
						for (String str : gene.species.keySet()) {
							Teginfo set = gene.species.get(str);
							for (Tegeval tv : set.tset) {
								if (tv.seq != null)
									max = Math.max(max, tv.seq.length());
							}
						}
						return max;
					}
				} else if (columnIndex == 16) {
					if (gene.species != null) {
						return specset.get(gene.species.keySet());
					}
				} else if (columnIndex == 17) {
					if (gene.species != null) {
						int max = 0;
						for (String str : gene.species.keySet()) {
							Teginfo set = gene.species.get(str);
							for (Tegeval tv : set.tset) {
								max = Math.max(max, tv.numCys);
							}
						}
						return max;
					}
					return 0;
				} else if (columnIndex == 18) {
					return gene.getGroupCoverage() == 28 && gene.getGroupCount() == 28 ? gene.corr16s : -1;
				} else if (columnIndex == 19) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.thermophilusSG0");
						return set;
					}
				} else if (columnIndex == 20) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.thermophilusJL18");
						return set;
					}
				} else if (columnIndex == 21) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.thermophilusHB8");
						return set;
					}
				} else if (columnIndex == 22) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.thermophilusHB27");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("ttHB27join").iterator().next();
				} else if (columnIndex == 23) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductusSA01");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("ttaqua").iterator().next();
				} else if (columnIndex == 24) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.aquaticus");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("ttaqua").iterator().next();
				} else if (columnIndex == 25) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.eggertsoni");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("eggertsoni2789").iterator().next();
				} else if (columnIndex == 26) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.islandicus");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("islandicus180610").iterator().next();
				} else if (columnIndex == 27) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.antranikiani");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("antag2120").iterator().next();
				} else if (columnIndex == 28) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus346");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto346").iterator().next();
				} else if (columnIndex == 29) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus1572");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto1572").iterator().next();
				} else if (columnIndex == 30) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus252");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto252").iterator().next();
				} else if (columnIndex == 31) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus2101");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto2101").iterator().next();
				} else if (columnIndex == 32) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus2127");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto2127").iterator().next();
				} else if (columnIndex == 33) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.scotoductus4063");
						return set;
					}
					// return gene.species == null ? null :
					// gene.species.get("scoto4063").iterator().next();
				} else if (columnIndex == 34) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.oshimai");
						return set;
					}
				} else if (columnIndex == 35) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.brockianus");
						return set;
					}
				} else if (columnIndex == 36) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.filiformis");
						return set;
					}
				} else if (columnIndex == 37) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.igniterrae");
						return set;
					}
				} else if (columnIndex == 38) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.kawarayensis");
						return set;
					}
				} else if (columnIndex == 39) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.arciformis");
						return set;
					}
				} else if (columnIndex == 40) {
					if (gene.species != null) {
						for( String key : gene.species.keySet() ) {
							if( key.contains("CCB") ) {
								System.err.println( " erm " + key );
							}
						}
						Teginfo set = gene.species.get("t.spCCB");
						return set;
					}
				} else if (columnIndex == 41) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.RLM");
						return set;
					}
				} else if (columnIndex == 42) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("t.oshimaiJL2");
						return set;
					}
				} else if (columnIndex == 43) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("mt.silvanus");
						return set;
					}
				} else if (columnIndex == 44) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("mt.ruber");
						return set;
					}
				} else if (columnIndex == 45) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("m.hydrothermalis");
						return set;
					}
				} else if (columnIndex == 46) {
					if (gene.species != null) {
						Teginfo set = gene.species.get("o.profundus");
						return set;
					}
				}
				return columnIndex >= 11 ? null : "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}
		};
		table.setModel( defaultModel );

		/*
		 * Comparator<Tegeval> wrapMe = new Comparator<Tegeval>() { public int
		 * compare(Tegeval o1, Tegeval o2) { return o1.compareTo(o2); } };
		 * DefaultRowSorter<TableModel, Integer> rowsorter =
		 * (DefaultRowSorter<TableModel,Integer>)table.getRowSorter(); for( int
		 * i = 10; i < 23; i++ ) { rowsorter.setComparator(i,
		 * NullComparators.atEnd(wrapMe)); }
		 */

		table.getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				if (gsplitpane != null) {
					for (String cstr : contigmap.keySet()) {
						Contig c = contigmap.get(cstr);
						c.count = 0;
						c.loc = 0.0;
					}

					for (Gene g : genelist) {
						if (g.species != null) {
							for (String spec : g.species.keySet()) {
								Teginfo stv = g.species.get(spec);
								for (Tegeval tv : stv.tset) {
									// int first = tv.cont.indexOf('_');
									// int sec = tv.cont.indexOf('_',first+1);
									Contig cont = tv.getContshort(); // tv.cont.substring(0,sec);
									if( cont != null && contigmap.containsKey(cont.getName()) ) {
										Contig c = contigmap.get(cont.getName());
										c.count++;
										int val = table.convertRowIndexToView(g.index);
										c.loc += (double) val;
									}
								}
							}
						}
					}
					gsplitpane.repaint();
				}
			}
		});

		final JTable ftable = new JTable() {
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

		JPopupMenu fpopup = new JPopupMenu();
		fpopup.add(new AbstractAction("Amigo lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = ftable.getSelectedRow();
				if (r >= 0) {
					String go = (String) ftable.getValueAt(r, 0);
					try {
						// GeneSet.this.getAppletContext().
						Desktop.getDesktop().browse(new URI("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + go));
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
				if (r >= 0) {
					String kegg = (String) ftable.getValueAt(r, 3);
					try {
						Desktop.getDesktop().browse(new URI("http://www.genome.jp/dbget-bin/www_bget?rn:" + kegg));
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
				if (r >= 0) {
					String ec = (String) ftable.getValueAt(r, 1);
					try {
						Desktop.getDesktop().browse(new URI("http://www.expasy.ch/cgi-bin/nicezyme.pl?" + ec));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		ftable.setComponentPopupMenu(fpopup);

		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Gene gain/loss") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<Node,List<GeneGroup>>	nodeGainMap = new HashMap<Node,List<GeneGroup>>();
				Map<Node,List<GeneGroup>>	nodeLossMap = new HashMap<Node,List<GeneGroup>>();
				
				String treestr = "";
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( applet ) == JFileChooser.APPROVE_OPTION ) {
					File file = fc.getSelectedFile();
					try {
						byte[] bb = Files.readAllBytes( Paths.get(file.toURI()) );
						treestr = new String( bb );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				TreeUtil tu = new TreeUtil();
				Node n = tu.parseTreeRecursive(treestr, false);
				String[] sobj = {"mt.ruber", "mt.silvanus", "o.profundus", "m.hydrothermalis"};
				Node newnode = tu.getParent( n, new HashSet<String>( Arrays.asList( sobj ) ) );
				tu.rerootRecur( n, newnode );
				
				File f = new File("/home/sigmar/gain_list.txt");
				try {
					PrintStream ps = new PrintStream( f );
					assignGain( newnode, nodeGainMap, ps );
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
				f = new File("/home/sigmar/loss_list.txt");
				try {
					PrintStream ps = new PrintStream( f );
					assignLoss( newnode, nodeLossMap, ps );
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Show group sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//JTextArea textarea = new JTextArea();
				//JScrollPane scrollpane = new JScrollPane(textarea);

				/*try {
					if (clipboardService == null)
						clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
					Action action = new CopyAction("Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL + KeyEvent.VK_C));
					textarea.getActionMap().put("copy", action);
					grabFocus = true;
				} catch (Exception ee) {
					ee.printStackTrace();
					System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
				}

				textarea.setDragEnabled(true);*/
				
				JFrame frame = null;
				if( currentSerify == null ) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);
					
					SerifyApplet sa = new SerifyApplet();
					sa.init( frame );
					//frame.add( )
					currentSerify = sa;
				} else frame = (JFrame)currentSerify.cnt;
				
				String[] farr = new String[] {"o.profundus", "mt.silvanus", "mt.ruber", "m.hydrothermalis", "t.thermophilus_SG0_5JP17_16", 
						"t.thermophilusJL18", "t.thermophilusHB8", "t.thermophilusHB27", "t.scotoductusSA01", "t.scotoductus4063",
						"t.scotoductus1572", "t.scotoductus2101", "t.scotoductus2127", "t.scotoductus346",
						"t.scotoductus252", "t.antranikiani", "t.kawarayensis", "t.brockianus", "t.igniterrae", "t.eggertsoni", 
						"t.RLM", "t.oshimai_JL2", "t.oshimai", "t.filiformis", "t.arciformis", "t.islandicus", "t.aquaticus", "t.spCCB"};

				Map<Integer,String>			ups = new HashMap<Integer,String>();
				Set<Integer>				stuck = new HashSet<Integer>();
				Map<Integer,List<Tegeval>>	ups2 = new HashMap<Integer,List<Tegeval>>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if (gg.species != null) {
						if( gg.genid != null && gg.genid.length() > 0 ) {
							ups.put( gg.getGroupIndex(), gg.name );
							stuck.add( gg.getGroupIndex() );
						}
						if( !stuck.contains(gg.getGroupIndex()) ) {
							if( !ups.containsKey(gg.getGroupIndex()) || !(gg.name.contains("unnamed") || gg.name.contains("hypot")) ) ups.put( gg.getGroupIndex(), gg.name );
						}
						
						List<Tegeval>	tlist;
						if( ups2.containsKey( gg.getGroupIndex() ) ) tlist = ups2.get( gg.getGroupIndex() );
						else {
							tlist = new ArrayList<Tegeval>();
							ups2.put( gg.getGroupIndex(), tlist );
						}
						
						//Set<String>	 specs = new HashSet<String>();
						//textarea.append(gg.name + ":\n");
						//for (String sp : gg.species.keySet()) {
						int count = 0;
						for(String sp : farr) {
							Teginfo stv = gg.species.get(sp);
							/*for( String key : gg.species.keySet() ) {
								if( key.contains("JL2") ) {
									System.err.println( " erm " + key );
								}
							}*/
							/*if( stv == null && gg.species.size() == 28 ) {
								System.err.println( gg.species );
								System.err.println( sp );
							}*/
							//System.err.println( gg.species.keySet() );
							if( stv == null ) {
								//System.err.println( sp );
							} else {
								count++;
								//specs.add( sp );
								for (Tegeval tv : stv.tset) {
									tlist.add( tv );
									/*textarea.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
									if (tv.dna != null) {
										for (int i = 0; i < tv.dna.length(); i += 70) {
											textarea.append(tv.dna.gg.speciessubstring(i, Math.min(i + 70, tv.dna.length())) + "\n");
										}
									}*/
								}
							}
						}
						if( count < gg.species.size() ) {
							System.err.println( gg.species );
							System.err.println();
						}
						//if( specs.size() < 28 ) System.err.println("mu " + specs);
					}
				}
				
				StringBuilder sb = new StringBuilder();
				for( int gi : ups.keySet() ) {
					String name = ups.get(gi);
					List<Tegeval>	tlist = ups2.get(gi);
					
					sb.append(name.replace('/', '-') + ":\n");
					if( tlist.size() < 28 ) {
						for( Tegeval tv : tlist ) {
							System.err.println( tv.cont );
						}
						System.err.println();
					}
					for( Tegeval tv : tlist ) {
						sb.append(">" + tv.cont.substring(0, tv.cont.indexOf('_')) + "\n");
						if (tv.seq != null) {
							for (int i = 0; i < tv.seq.length(); i += 70) {
								sb.append(tv.seq.substring(i, Math.min(i + 70, tv.seq.length())) + "\n");
							}
						}
					}
				}
				
				try {
					currentSerify.addSequences("uh", new StringReader( sb.toString() ), "/");
				} catch (URISyntaxException | IOException e1) {
					e1.printStackTrace();
				}
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("Show group DNA sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JTextArea textarea = new JTextArea();
				JScrollPane scrollpane = new JScrollPane(textarea);

				try {
					if (clipboardService == null)
						clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
					Action action = new CopyAction("Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL + KeyEvent.VK_C));
					textarea.getActionMap().put("copy", action);
					grabFocus = true;
				} catch (Exception ee) {
					ee.printStackTrace();
					System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
				}

				textarea.setDragEnabled(true);
				
				try {
					final DataFlavor df = new DataFlavor("text/plain;charset=utf-8");
					TransferHandler th = new TransferHandler() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
	
						public int getSourceActions(JComponent c) {
							return TransferHandler.COPY_OR_MOVE;
						}
	
						public boolean canImport(TransferHandler.TransferSupport support) {
							return false;
						}
	
						protected Transferable createTransferable(JComponent c) {
							return new Transferable() {
								@Override
								public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
									if (arg0.equals(df) ) {
										return new ByteArrayInputStream( textarea.getText().getBytes() );
									} else {
										return textarea.getText();
									}
								}
	
								@Override
								public DataFlavor[] getTransferDataFlavors() {
									return new DataFlavor[] { df, DataFlavor.stringFlavor };
								}
	
								@Override
								public boolean isDataFlavorSupported(DataFlavor arg0) {
									if (arg0.equals(df) || arg0.equals(DataFlavor.stringFlavor)) {
										return true;
									}
									return false;
								}
							};
						}
	
						public boolean importData(TransferHandler.TransferSupport support) {
							return false;
						}
					};
					textarea.setTransferHandler( th );
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				Map<Integer,String>			ups = new HashMap<Integer,String>();
				Set<Integer>				stuck = new HashSet<Integer>();
				Map<Integer,List<Tegeval>>	ups2 = new HashMap<Integer,List<Tegeval>>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if (gg.species != null) {
						if( gg.genid != null && gg.genid.length() > 0 ) {
							ups.put( gg.getGroupIndex(), gg.name );
							stuck.add( gg.getGroupIndex() );
						}
						if( !stuck.contains(gg.getGroupIndex()) ) {
							if( !ups.containsKey(gg.getGroupIndex()) || !(gg.name.contains("unnamed") || gg.name.contains("hypot")) ) ups.put( gg.getGroupIndex(), gg.name );
						}
						
						List<Tegeval>	tlist;
						if( ups2.containsKey( gg.getGroupIndex() ) ) tlist = ups2.get( gg.getGroupIndex() );
						else {
							tlist = new ArrayList<Tegeval>();
							ups2.put( gg.getGroupIndex(), tlist );
						}
						
						//textarea.append(gg.name + ":\n");
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								tlist.add( tv );
								/*textarea.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
								if (tv.dna != null) {
									for (int i = 0; i < tv.dna.length(); i += 70) {
										textarea.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
									}
								}*/
							}
						}
					}
				}
				
				for( int gi : ups.keySet() ) {
					String name = ups.get(gi);
					List<Tegeval>	tlist = ups2.get(gi);
					
					textarea.append(name.replace('/', '-') + ":\n");
					for( Tegeval tv : tlist ) {
						textarea.append(">" + tv.cont.substring(0, tv.cont.indexOf('_')) + "\n");
						if (tv.dna != null) {
							for (int i = 0; i < tv.dna.length(); i += 70) {
								textarea.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
							}
						}
					}
				}
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Add similar") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();

				Object o = table.getValueAt(r, c);

				if (c >= 18) {
					for (int i = 0; i < table.getRowCount(); i++) {
						Object no = table.getValueAt(i, c);
						if (no != null && !table.isRowSelected(i))
							table.addRowSelectionInterval(i, i);
					}
				} else {
					for (int i = 0; i < table.getRowCount(); i++) {
						Object no = table.getValueAt(i, c);
						if (o.equals(no) && !table.isRowSelected(i))
							table.addRowSelectionInterval(i, i);
					}
				}
			}
		});
		popup.add(new AbstractAction("Select similar") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();

				Object o = table.getValueAt(r, c);

				table.removeRowSelectionInterval(0, table.getRowCount() - 1);
				if (c >= 18) {
					for (int i = 0; i < table.getRowCount(); i++) {
						Object no = table.getValueAt(i, c);
						if (no != null)
							table.addRowSelectionInterval(i, i);
					}
				} else {
					for (int i = 0; i < table.getRowCount(); i++) {
						Object no = table.getValueAt(i, c);
						if (o.equals(no))
							table.addRowSelectionInterval(i, i);
					}
				}
			}
		});
		popup.add(new AbstractAction("Table text") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea ta = new JTextArea();
				ta.setDragEnabled(true);
				JScrollPane scrollpane = new JScrollPane(ta);

				StringBuilder sb = new StringBuilder();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					for (int c = 0; c < table.getColumnCount() - 1; c++) {
						Object o = table.getValueAt(r, c);
						if (c > 18) {
							if (o != null) {
								String val = o.toString();
								int k = val.indexOf(' ');
								sb.append(val.substring(0, k));
								sb.append("\t" + val.substring(k + 1));
							} else
								sb.append("\t");
						} else {
							if (o != null) {
								sb.append(o.toString());
							}
						}
						sb.append("\t");
					}
					Object o = table.getValueAt(r, table.getColumnCount() - 1);
					if (o != null) {
						String val = o.toString();
						int k = val.indexOf(' ');
						sb.append(val.substring(0, k));
						sb.append("\t" + val.substring(k + 1));
					} else
						sb.append("\t");
					sb.append("\n");
				}

				ta.setText(sb.toString());
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("NCBI lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if (r >= 0) {
					String ref = (String) table.getValueAt(r, 2);
					try {
						Desktop.getDesktop().browse(new URI("http://www.ncbi.nlm.nih.gov/gene?term=" + ref));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		table.setComponentPopupMenu(popup);

		ftable.setAutoCreateRowSorter(true);
		ftable.setModel(new TableModel() {
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
				if (columnIndex == 0)
					return "GO";
				else if (columnIndex == 1)
					return "EC";
				else if (columnIndex == 2)
					return "MetaCyc";
				else if (columnIndex == 3)
					return "KEGG";
				else if (columnIndex == 4)
					return "Number of proteins";
				else if (columnIndex == 5)
					return "Name";
				else if (columnIndex == 6)
					return "Namespace";
				else if (columnIndex == 7)
					return "Def";
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 4)
					return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Function func = funclist.get(rowIndex);
				if (columnIndex == 0)
					return func.go;
				else if (columnIndex == 1)
					return func.ec;
				else if (columnIndex == 2)
					return func.metacyc;
				else if (columnIndex == 3)
					return func.kegg;
				else if (columnIndex == 4)
					return func.geneentries == null ? 0 : func.geneentries.size();
				else if (columnIndex == 5)
					return func.name;
				else if (columnIndex == 6)
					return func.namespace;
				else if (columnIndex == 7)
					return func.desc;
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}
		});
		fscrollpane.setViewportView(ftable);

		final Set<Integer> filterset = new HashSet<Integer>();
		final Set<Integer> genefilterset = new HashSet<Integer>();

		final RowFilter filter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return filterset.isEmpty() || filterset.contains(entry.getIdentifier());
			}
		};
		updateFilter(ftable, filter, null);

		final RowFilter genefilter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return genefilterset.isEmpty() || genefilterset.contains(entry.getIdentifier());
			}
		};
		updateFilter(table, genefilter, label);

		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String sel = (String) e.getItem();
				filterset.clear();
				if (pathwaymap.containsKey(sel)) {
					Set<String> enz = pathwaymap.get(sel);
					for (Function f : funclist) {
						if (f.ec != null && enz.contains(f.ec)) {
							filterset.add(f.index);
						}
					}
				}
				updateFilter(ftable, filter, null);
			}
		});

		specombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String sel = (String) e.getItem();
				genefilterset.clear();
				for (Gene g : genelist) {
					if (g.species != null) {
						for (String sp : g.species.keySet()) {
							Teginfo stv = g.species.get(sp);
							for (Tegeval tv : stv.tset) {
								if (sel.equals(tv.teg)) {
									System.out.println(g.name + " " + sp + " " + sel + "  " + tv.eval);
									genefilterset.add(g.index);
								}
							}
						}
					}
				}
				updateFilter(table, genefilter, label);
			}
		});

		fpopup.add(new AbstractAction("Find conserved terms") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Integer> res = new HashSet<Integer>();
				for (Function f : funclist) {
					if (f.geneentries != null) {
						Set<String> check = new HashSet<String>();
						for (Gene g : f.geneentries) {
							//Gene g = genemap.get(str);
							if (g.species != null) {
								if (check.isEmpty())
									check.addAll(g.species.keySet());
								else if (!(check.size() == g.species.size() && check.containsAll(g.species.keySet()))) {
									check.clear();
									break;
								}
							}
						}
						if (!check.isEmpty())
							res.add(f.index);
					}
				}
				filterset.clear();
				for (int i : res) {
					filterset.add(i);
				}
				updateFilter(ftable, filter, null);
			}
		});
		fpopup.addSeparator();
		fpopup.add(new AbstractAction("Show genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = ftable.getSelectedRows();
				genefilterset.clear();
				for (int r : rr) {
					int fr = ftable.convertRowIndexToModel(r);
					Function f = funclist.get(fr);
					Set<Gene> sset = f.geneentries;
					for (Gene g : sset) {
						//Gene g = genemap.get(s);
						genefilterset.add(g.index);
					}
				}
				updateFilter(table, genefilter, label);
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// table.clearSelection();
				tableisselecting = true;
				if (!ftableisselecting && filterset.isEmpty()) {
					//ftable.removeRowSelectionInterval(0, ftable.getRowCount() - 1);
					int[] rr = table.getSelectedRows();
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene g = genelist.get(cr);
						if (g.funcentries != null) {
							for( Function f : g.funcentries) {
								//Function f = funcmap.get(go);
								int rf = ftable.convertRowIndexToView(f.index);
								ftable.addRowSelectionInterval(rf, rf);
							}
						}
					}
				}
				tableisselecting = false;
			}
		});

		ftable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					filterset.clear();
					updateFilter(ftable, filter, null);
				}
			}
		});

		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					genefilterset.clear();
					updateFilter(table, genefilter, label);
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				tableisselecting = true;
				if (!ftableisselecting && e.getClickCount() == 2) {
					/*
					 * int[] rr = ftable.getSelectedRows(); int minr =
					 * ftable.getRowCount(); int maxr = 0; for( int r : rr ) {
					 * if( r < minr ) minr = r; if( r > maxr ) maxr = r; }
					 * ftable.removeRowSelectionInterval(minr, maxr);
					 */
					// ftable.removeRowSelectionInterval(0, filterset.isEmpty()
					// ? ftable.getRowCount()-1 : filterset.size()-1 );
					filterset.clear();
					int[] rr = table.getSelectedRows();
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene g = genelist.get(cr);
						if (g.funcentries != null) {
							for( Function f : g.funcentries ) {
								//Function f = funcmap.get(go);
								// ftable.getRowSorter().convertRowIndexToView(index)
								// int rf = ftable.convertRowIndexToView(
								// f.index );
								filterset.add(f.index);
								// ftable.addRowSelectionInterval(rf, rf);
							}
						}
					}
					updateFilter(ftable, filter, null);
					// ftable.sorterChanged( new RowSorterEvent(
					// ftable.getRowSorter() ) );
					// ftable.tableChanged( new TableModelEvent(
					// ftable.getModel() ) );
				}
				tableisselecting = false;
			}
		});

		ftable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				ftableisselecting = true;
				if (!tableisselecting && e.getClickCount() == 2) {
					genefilterset.clear();
					int[] rr = ftable.getSelectedRows();
					for (int r : rr) {
						int cr = ftable.convertRowIndexToModel(r);

						Function f = funclist.get(cr);
						if (f.geneentries != null) {
							for( Gene g : f.geneentries ) {
								//Gene g = genemap.get(ref);
								// int rf = table.convertRowIndexToView( g.index
								// );
								// table.addRowSelectionInterval(rf, rf);
								genefilterset.add(g.index);
							}
						}
					}
					updateFilter(table, genefilter, label);
					// ftable.sorterChanged( new RowSorterEvent(
					// ftable.getRowSorter() ) );
					// ftable.tableChanged( new TableModelEvent(
					// ftable.getModel() ) );
				}
				ftableisselecting = false;
			}
		});

		ftable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// ftable.clearSelection();
				ftableisselecting = true;
				if (!tableisselecting && genefilterset.isEmpty()) {
					table.removeRowSelectionInterval(0, table.getRowCount() - 1);
					int[] rr = ftable.getSelectedRows();
					for (int r : rr) {
						int cr = ftable.convertRowIndexToModel(r);
						Function f = funclist.get(cr);
						if (f.geneentries != null) {
							for( Gene g : f.geneentries ) {
								//Gene g = genemap.get(ref);
								int rf = table.convertRowIndexToView(g.getGroupIndex());
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
				updateFilter(0, textfield.getText(), table, genefilter, genefilterset, 0, label);
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, textfield.getText(), table, genefilter, genefilterset, 0, label);
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, textfield.getText(), table, genefilter, genefilterset, 0, label);
			}
		});

		ftextfield.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				updateFilter(0, ftextfield.getText(), ftable, filter, filterset, 5, null);
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, ftextfield.getText(), ftable, filter, filterset, 5, null);
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, ftextfield.getText(), ftable, filter, filterset, 5, null);
			}
		});
		popup.add(new AbstractAction("KEGG gene lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if (r != -1) {
					int rr = table.convertRowIndexToModel(r);
					Gene g = genelist.get(rr);
					try {
						Desktop.getDesktop().browse(new URI("http://www.genome.jp/dbget-bin/www_bget?" + g.keggid));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show all") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Crop to selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int mr = table.convertRowIndexToModel(r);
					genefilterset.add(mr);
				}
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Remove selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// genefilterset.clear();
				int[] rr = table.getSelectedRows();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<Integer>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				} else {
					for (int r : rr) {
						int mr = table.convertRowIndexToModel(r);
						genefilterset.remove(mr);
					}
				}
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Invert selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// genefilterset.clear();
				int[] rr = table.getSelectedRows();
				for (int r = 0; r < table.getRowCount(); r++) {
					if (table.isRowSelected(r))
						table.removeRowSelectionInterval(r, r);
					else
						table.addRowSelectionInterval(r, r);
				}
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show genes with same sharing") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int r = table.getSelectedRow();
				if (r >= 0) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					for (Gene g : genelist) {
						if (gg.species != null && g.species != null) {
							Set<String> ggset = gg.species.keySet();
							Set<String> gset = g.species.keySet();

							if (gset.size() == ggset.size() && gset.containsAll(ggset))
								genefilterset.add(g.index);
						}
					}
					updateFilter(table, genefilter, label);
				}
			}
		});
		popup.add(new AbstractAction("Show shared function") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				Set<Function> startfunc = new HashSet<Function>();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene gg = genelist.get(cr);
						if (gg.funcentries != null) {
							if( startfunc.isEmpty() ) {
								startfunc.addAll( gg.funcentries );
							} else {
								startfunc.retainAll( gg.funcentries );
							}
						}
							/*if (startfunc == null)
								startfunc = new HashSet<Function>(gg.funcentries);
							else {
								startfunc.retainAll(gg.funcentries);
							}*/
					} else {
						GeneGroup gg = allgenegroups.get(cr);
						Set<Function> fset = gg.getFunctions();
						if( startfunc.isEmpty() ) {
							startfunc.addAll( fset );
						} else {
							startfunc.retainAll( fset );
						}
					}
				}
				for( Function f : funclist ) {
					filterset.add( f.index );
				}
				updateFilter(ftable, filter, null);
			}
		});
		popup.add(new AbstractAction("Show all functions") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				Set<Function> startfunc = null;
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene gg = genelist.get(cr);
						if (gg.funcentries != null) {
							for( Function f : gg.funcentries ) {
								filterset.add( f.index );
							}
						}
					} else {
						GeneGroup gg = allgenegroups.get(cr);
						Set<Function> fset = gg.getFunctions();
						for( Function f : fset ) {
							filterset.add( f.index );
						}
					}
				}
				updateFilter(ftable, filter, null);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show all sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier );
				jf.initGui(frame);

				Map<String, Sequence> contset = new HashMap<String, Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if (gg.species != null) {
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								String contig = tv.getContshort().getName();
								StringBuilder dna = tv.seq;
								Sequence seq = new Sequence( contig, dna, serifier.mseq );
								serifier.addSequence(seq);
							}
						}
					}
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		popup.add(new AbstractAction("Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Gene>	genes = new HashSet<Gene>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					genes.add( gg );
				}
				showSequences( comp, genes );
			}
		});
		popup.add(new AbstractAction("Split/Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = getSelectedASeqs( table, genelist, applet, species );
				if( currentSerify == null ) {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
					frame.setSize(800, 600);
					
					SerifyApplet sa = new SerifyApplet();
					sa.init( frame );
					currentSerify = sa;
					
					frame.setVisible( true );
				}
				
				try {
					currentSerify.addSequences("uh", new StringReader( sb.toString() ), "/");
				} catch (URISyntaxException | IOException e1) {
					e1.printStackTrace();
				}
				
				//JTextArea textarea = new JTextArea();
				//textarea.append( sb.toString() );
				
				/*try {
					if (clipboardService == null)
						clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
					Action action = new CopyAction("Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL + KeyEvent.VK_C));
					textarea.getActionMap().put("copy", action);
					grabFocus = true;
				} catch (Exception ee) {
					ee.printStackTrace();
					System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
				}

				*
				 * final DataFlavor df =
				 * DataFlavor.getTextPlainUnicodeFlavor();//new
				 * DataFlavor("text/plain;charset=utf-8"); final String charset
				 * = df.getParameter("charset"); final Transferable transferable
				 * = new Transferable() {
				 * 
				 * @Override public Object getTransferData(DataFlavor arg0)
				 * throws UnsupportedFlavorException, IOException { String ret =
				 * makeCopyString( detailTable ); return new
				 * ByteArrayInputStream( ret.getBytes( charset ) ); }
				 * 
				 * @Override public DataFlavor[] getTransferDataFlavors() {
				 * return new DataFlavor[] { df }; }
				 * 
				 * @Override public boolean isDataFlavorSupported(DataFlavor
				 * arg0) { if( arg0.equals(df) ) { return true; } return false;
				 * } };
				 * 
				 * TransferHandler th = new TransferHandler() { private static
				 * final long serialVersionUID = 1L;
				 * 
				 * public int getSourceActions(JComponent c) { return
				 * TransferHandler.COPY_OR_MOVE; }
				 * 
				 * public boolean canImport(TransferHandler.TransferSupport
				 * support) { return false; }
				 * 
				 * protected Transferable createTransferable(JComponent c) {
				 * return transferable; }
				 * 
				 * public boolean importData(TransferHandler.TransferSupport
				 * support) { /*try { Object obj =
				 * support.getTransferable().getTransferData( df ); InputStream
				 * is = (InputStream)obj;
				 * 
				 * byte[] bb = new byte[2048]; int r = is.read(bb);
				 * 
				 * //importFromText( new String(bb,0,r) ); } catch
				 * (UnsupportedFlavorException e) { e.printStackTrace(); } catch
				 * (IOException e) { e.printStackTrace(); }* return false; } };
				 * textarea.setTransferHandler( th );
				 *
				textarea.setDragEnabled(true);

				JScrollPane scrollpane = new JScrollPane(textarea);
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible(true);*/
			}
		});
		popup.add(new AbstractAction("Show DNA sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = getSelectedSeqs( table, genelist );
				
				if( currentSerify == null ) {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
					frame.setSize(800, 600);
					
					SerifyApplet sa = new SerifyApplet();
					sa.init( frame );
					
					try {
						sa.addSequences("uh", new StringReader( sb.toString() ), "/");
					} catch (URISyntaxException | IOException e1) {
						e1.printStackTrace();
					}
					
					frame.setVisible( true );
				}
				
				JTextArea textarea = new JTextArea();
				JScrollPane scrollpane = new JScrollPane(textarea);

				try {
					if (clipboardService == null)
						clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
					Action action = new CopyAction("Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL + KeyEvent.VK_C));
					textarea.getActionMap().put("copy", action);
					grabFocus = true;
				} catch (Exception ee) {
					ee.printStackTrace();
					System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
				}

				textarea.setDragEnabled(true);
				textarea.append( sb.toString() );
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});
		popup.add(new AbstractAction("Export all DNA sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				try {
					Map<Integer, FileWriter> lfw = new HashMap<Integer, FileWriter>();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();

						int[] rr = table.getSelectedRows();
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							Gene gg = genelist.get(cr);
							if (gg.species != null) {
								FileWriter fw = null;
								if (lfw.containsKey(gg.getGroupIndex())) {
									fw = lfw.get(gg.getGroupIndex());
								} else {
									fw = new FileWriter(new File(f, "group_" + gg.getGroupIndex() + ".fasta"));
									lfw.put(gg.getGroupIndex(), fw);
								}

								for (String sp : gg.species.keySet()) {
									Teginfo stv = gg.species.get(sp);
									for (Tegeval tv : stv.tset) {
										fw.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
										if (tv.dna != null) {
											for (int i = 0; i < tv.dna.length(); i += 70) {
												fw.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
											}
										}
									}
								}
							}
						}
					}
					for (int gi : lfw.keySet()) {
						lfw.get(gi).close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add(new AbstractAction("Export relevant contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				try {
					Map<Integer, FileWriter> lfw = new HashMap<Integer, FileWriter>();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();

						Set<Contig> contset = new HashSet<Contig>();
						int[] rr = table.getSelectedRows();
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							Gene gg = genelist.get(cr);
							if (gg.species != null) {
								for (String sp : gg.species.keySet()) {
									Teginfo stv = gg.species.get(sp);
									for (Tegeval tv : stv.tset) {
										contset.add( tv.getContshort() );
									}
								}
							}
						}

						FileWriter fw = new FileWriter(f);
						for (Contig contig : contset) {
							fw.append(">" + contig + "\n");
							if (GeneSet.contigmap.containsKey(contig)) {
								StringBuilder dna = GeneSet.contigmap.get(contig).seq;
								for (int i = 0; i < dna.length(); i += 70) {
									fw.append(dna.substring(i, Math.min(i + 70, dna.length())) + "\n");
								}
							}
						}
						fw.close();
					}
					for (int gi : lfw.keySet()) {
						lfw.get(gi).close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("View selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier );
				jf.initGui(frame);

				Map<Contig, Sequence> contset = new HashMap<Contig, Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if (gg.species != null) {
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								Sequence seq;
								Contig contig = tv.getContshort();
								if (contset.containsKey(contig)) {
									seq = contset.get(contig);
								} else {
									if (GeneSet.contigmap.containsKey(contig)) {
										StringBuilder dna = GeneSet.contigmap.get(contig).seq;
										seq = new Sequence(contig.getName(), dna, serifier.mseq);
									} else
										seq = new Sequence(contig.getName(), serifier.mseq);
									contset.put(contig, seq);
								}

								Annotation a = seq.new Annotation(seq, contig.getName(), Color.red, serifier.mann);
								a.setStart(tv.start);
								a.setStop(tv.stop);
								a.setOri(tv.ori);
								a.setGroup(gg.name);
								a.setType("gene");
								serifier.addAnnotation(a);
								// seq.addAnnotation( new Annotation( seq, ) );
							}
						}
					}
				}

				for (Contig contig : contset.keySet()) {
					Sequence seq = contset.get(contig);
					serifier.addSequence(seq);
					if (seq.getAnnotations() != null)
						Collections.sort(seq.getAnnotations());
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		popup.add(new AbstractAction("View whole contigs for selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier );
				jf.initGui(frame);

				Map<Contig, Sequence> contset = new HashMap<Contig, Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					if (gg.species != null) {
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								Sequence seq;
								Contig contig = tv.getContshort();
								if (contset.containsKey(contig)) {
									seq = contset.get(contig);
								} else {
									if( GeneSet.contigmap.containsKey(contig) ) {
										//StringBuilder dna = GeneSet.contigmap.get(contig).seq;
										StringBuilder dna = contig.getSequence();
										seq = new Sequence(contig.getName(), dna, serifier.mseq);
									} else {
										seq = new Sequence(contig.getName(), serifier.mseq);
									}

									contset.put(contig, seq);
								}

								/*
								 * Annotation a = jf.new Annotation( seq,
								 * contig, Color.red ); a.setStart( tv.start );
								 * a.setStop( tv.stop ); a.setOri( tv.ori );
								 * a.setGroup( gg.name ); a.setType( "gene" );
								 * jf.addAnnotation( a );
								 */
								// seq.addAnnotation( new Annotation( seq, ) );
							}
						}
					}
				}

				for (Gene g : genelist) {
					if (g.species != null) {
						for (String sp : g.species.keySet()) {
							Teginfo stv = g.species.get(sp);
							for (Tegeval tv : stv.tset) {
								Contig contig = tv.getContshort();
								if (contset.keySet().contains(contig)) {
									Sequence seq = contset.get(contig);
									Annotation a = seq.new Annotation(seq, contig.getName(), Color.red, serifier.mann);
									a.setStart(tv.start);
									a.setStop(tv.stop);
									a.setOri(tv.ori);
									a.setGroup(g.name);
									a.setType("gene");
									serifier.addAnnotation(a);
								}
							}
						}
					}
				}

				for( Contig contig : contset.keySet() ) {
					Sequence seq = contset.get(contig);
					serifier.addSequence(seq);
					if (seq.getAnnotations() != null)
						Collections.sort(seq.getAnnotations());
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				proxi(table, rr, genelist, genefilterset, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Add genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				proxi(table, rr, genelist, genefilterset, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Remove genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<Integer>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				}
				proxi(table, rr, genelist, genefilterset, true);
				updateFilter(table, genefilter, label);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				relati(table, rr, genelist, genefilterset, uclusterlist, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Add related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				relati(table, rr, genelist, genefilterset, uclusterlist, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Remove related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<Integer>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				}
				relati(table, rr, genelist, genefilterset, uclusterlist, true);
				updateFilter(table, genefilter, label);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show closely related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<String> ct = new HashSet<String>();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					// genefilterset.add( gg.index );
					if (gg.species != null) {
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								for (Set<String> uset : iclusterlist) {
									if (uset.contains(tv.cont)) {
										ct.addAll(uset);
										break;
									}
								}
							}
						}
					}
				}

				for (Gene g : genelist) {
					if (g.species != null) {
						for (String sp : g.species.keySet()) {
							Teginfo stv = g.species.get(sp);
							for (Tegeval tv : stv.tset) {
								if (ct.contains(tv.cont)) {
									genefilterset.add(g.index);
									break;
								}
							}
						}
					}
				}

				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Show distance matrix") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextArea textarea = new JTextArea();
				
				try {
					if (clipboardService == null)
						clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
					Action action = new CopyAction("Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL + KeyEvent.VK_C));
					textarea.getActionMap().put("copy", action);
					grabFocus = true;
				} catch (Exception ee) {
					ee.printStackTrace();
					System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
				}
				textarea.setDragEnabled(true);

				JScrollPane scrollpane = new JScrollPane(textarea);

				int r = table.getSelectedRow();
				int cr = table.convertRowIndexToModel(r);
				Gene gg = genelist.get(cr);
				if (gg.species != null) {
					for (String s : corrInd) {
						if (s.equals(corrInd.get(0)))
							textarea.append(s);
						else
							textarea.append("\t" + s);
					}

					int i = 0;
					int j = 0;
					
					int len = 16;
					double[] min = new double[len];
					double[] max = new double[len];
					
					for( i = 0; i < len; i++ ) {
						min[i] = Double.MAX_VALUE;
						max[i] = 0.0;
					}
					
					double[] corrarr = gg.corrarr;
					boolean symmetrize = true;
					if( symmetrize ) {
						for( i = 0; i < len; i++ ) {
							for( int k = i+1; k < len; k++ ) {
								corrarr[i*len+k] = (corrarr[k*len+i]+corrarr[i*len+k])/2.0;
								corrarr[k*len+i] = corrarr[i*len+k];
							}
						}
					}
					
					for (i = 0; i < len; i++) {
						for (int k = 0; k < len; k++) {
							if (corrarr[i * len + k] < min[i])
								min[i] = corrarr[i * len + k];
							if (corrarr[i * len + k] > max[i])
								max[i] = corrarr[i * len + k];
						}

						/*for (int k = 0; k < len; k++) {
							corrarr[i * 16 + k] = corrarr[i * 16 + k] - min;
						}*/
					}
					
					i = 0;
					for (double d : corrarr) {
						double dval = d;
						if (i % len == 0)
							textarea.append("\n" + dval);
						else
							textarea.append("\t" + dval);

						i++;
					}
					textarea.append("\n");

					i = 0;
					for (double d : corrarr) {
						double dval = Math.exp( (d-min[i/len]) / 20.0 + 1.0) / 100.0; // 0.0 ?
																		// 0.0 :
																		// 100.0/d;
						if (i % len == 0)
							textarea.append("\n" + dval);
						else
							textarea.append("\t" + dval);

						i++;
					}
					double[] newcorr = Arrays.copyOf(corrarr, corrarr.length);
					textarea.append("\nD matrix\n");
					i = 0;
					for (double d : corrarr) {
						double dval = max[i/len]-d;
						newcorr[i] = dval;
						if (i % len == 0)
							textarea.append("\n" + dval);
						else
							textarea.append("\t" + dval);

						i++;
					}
					
					TreeUtil treeutil = new TreeUtil();
					treeutil.neighborJoin( newcorr, corrInd, null, true, true );
				}
				
				/*
				 * int[] rr = table.getSelectedRows(); for( int r : rr ) { int
				 * cr = table.convertRowIndexToModel(r); Gene gg =
				 * genelist.get(cr); if( gg.species != null ) { textarea.append(
				 * gg.name + ":\n" ); for( String sp : gg.species.keySet() ) {
				 * Teginfo stv = gg.species.get( sp ); for( Tegeval tv :
				 * stv.tset ) { textarea.append( ">" + tv.cont + " " + tv.teg +
				 * " " + tv.eval + "\n" ); for( int i = 0; i < tv.seq.length();
				 * i+=70 ) { int end = Math.min(i+70,tv.seq.length());
				 * textarea.append( tv.seq.substring(i, end)+"\n" ); //new
				 * String( tv.seq, i, Math.min(i+70,tv.seq.length()) )+"\n"); }
				 * //textarea.append( ">" + tv.cont + " " + tv.teg + " " +
				 * tv.eval + "\n" + tv.seq + "\n" ); } } } }
				 */
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(scrollpane);
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});

		/*
		 * final List<String> reglist = new ArrayList<String>(); final
		 * Map<String,Gene> regidx = new TreeMap<String,Gene>();
		 * 
		 * for( Gene g : genelist ) { if( g.species != null ) { for( String key
		 * : g.species.keySet() ) { Set<Tegeval> stv = g.species.get(key); for(
		 * Tegeval tv : stv ) { regidx.put(tv.cont, g); } } } }
		 * 
		 * for( String key : regidx.keySet() ) { reglist.add(key); }
		 * 
		 * final JTable contigtable = new JTable();
		 * contigtable.setAutoCreateRowSorter( true ); contigtable.setModel( new
		 * TableModel() {
		 * 
		 * @Override public int getRowCount() { return reglist.size(); }
		 * 
		 * @Override public int getColumnCount() { return 1; }
		 * 
		 * @Override public String getColumnName(int columnIndex) { return
		 * "Region"; }
		 * 
		 * @Override public Class<?> getColumnClass(int columnIndex) { return
		 * String.class; }
		 * 
		 * @Override public boolean isCellEditable(int rowIndex, int
		 * columnIndex) { return false; }
		 * 
		 * @Override public Object getValueAt(int rowIndex, int columnIndex) {
		 * return reglist.get(rowIndex); }
		 * 
		 * @Override public void setValueAt(Object aValue, int rowIndex, int
		 * columnIndex) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void addTableModelListener(TableModelListener l) {
		 * // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void removeTableModelListener(TableModelListener l)
		 * { // TODO Auto-generated method stub
		 * 
		 * } });
		 * 
		 * contigtable.getSelectionModel().addListSelectionListener( new
		 * ListSelectionListener() {
		 * 
		 * @Override public void valueChanged(ListSelectionEvent e) {
		 * genefilterset.clear(); int[] rr = contigtable.getSelectedRows(); for(
		 * int r : rr ) { String s = (String)contigtable.getValueAt(r, 0); Gene
		 * g = regidx.get( s );
		 * 
		 * genefilterset.add( g.index ); updateFilter(table, genefilter, label);
		 * //int k = table.convertRowIndexToView(g.index); //if( k != -1
		 * )table.addRowSelectionInterval(k, k); } } }); JScrollPane
		 * contigscroll = new JScrollPane( contigtable );
		 * 
		 * JSplitPane mainsplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		 * mainsplit.setLeftComponent( contigscroll );
		 * mainsplit.setRightComponent( splitpane );
		 */

		return splitpane;
	}

	static ClipboardService clipboardService;
	static boolean grabFocus;

	public static void copyData(Component source) {
		JTextArea textarea = (JTextArea) source;
		String s = textarea.getText();

		if (s == null || s.trim().length() == 0) {
			JOptionPane.showMessageDialog(source, "There is no data selected!");
		} else {
			StringSelection selection = new StringSelection(s);
			clipboardService.setContents(selection);
		}

		if (grabFocus) {
			source.requestFocus();
		}
	}

	static class CopyAction extends AbstractAction {
		public CopyAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {
			copyData((Component) e.getSource());
		}
	}

	public static void updateFilter(int val, String str, JTable table, RowFilter filter, Set<Integer> filterset, int ind, JLabel label) {
		filterset.clear();
		String ustr = str.toLowerCase();
		TableModel model = table.getModel();
		for (int r = 0; r < model.getRowCount(); r++) {
			String s = model.getValueAt(r, ind).toString().toLowerCase();
			if (s != null && s.contains(ustr))
				filterset.add(r);
		}
		updateFilter(table, filter, label);
	}

	static boolean ftableisselecting = false;
	static boolean tableisselecting = false;

	private static void readGoInfo(Reader rd, Map<Function, Set<Gene>> gofilter, String outfile) throws IOException {
		FileWriter fw = null;
		if (outfile != null)
			fw = new FileWriter(outfile);

		// FileReader fr = new FileReader( obo );
		BufferedReader br = new BufferedReader(rd);

		boolean on = false;
		Function f = null;
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("[Term]")) {
				on = false;
				if (f != null && gofilter.containsKey(f)) {
					if (f.geneentries == null) f.geneentries = new HashSet<Gene>();
					f.geneentries.addAll(gofilter.get(f));
					//retmap.put(f.go, f);
				}
				//f = new Function();
			} else if (line.startsWith("id:")) {
				String go = line.substring(4); // line.indexOf("GO:")+3 );
				if( !funcmap.containsKey( go ) ) {
					f = new Function( go );
					funcmap.put( go, f );
				} else f = funcmap.get( go );
				
				if (fw != null && gofilter.containsKey(f)) {
					fw.write("[Term]\n");
					on = true;
				}
			} else if (line.startsWith("name:")) {
				f.name = line.substring(6);
			} else if (line.startsWith("namespace:")) {
				f.namespace = line.substring(11);
			} else if (line.startsWith("def:")) {
				f.desc = line.substring(5);
			} else if (line.startsWith("xref:")) {
				if (line.contains("EC:")) {
					f.ec = line.substring(line.indexOf("EC:") + 3);
				} else if (line.contains("MetaCyc:")) {
					f.metacyc = line.substring(line.indexOf("MetaCyc:") + 8);
				} else if (line.contains("KEGG:")) {
					f.kegg = line.substring(line.indexOf("KEGG:") + 5);
				}
			}

			if (on) fw.write(line + "\n");

			line = br.readLine();
		}
		if (f != null && gofilter.containsKey(f)) {
			if (f.geneentries == null)
				f.geneentries = new HashSet<Gene>();
			f.geneentries.addAll(gofilter.get(f));
			//retmap.put(f.go, f);
		}
		br.close();

		if (fw != null) fw.close();
	}

	public static class ClusterInfo {
		int id;
		int sharing;
		int genum;

		public ClusterInfo(int id, int sharing, int genum) {
			this.id = id;
			this.sharing = sharing;
			this.genum = genum;
		}
	};

	public static double[] load16SCorrelation(Reader r, List<String> order) throws IOException {
		List<Double> ret = new ArrayList<Double>();

		Map<String, Map<String, Integer>> tm = new TreeMap<String, Map<String, Integer>>();

		String currentSpec = null;
		Map<String, Integer> subtm = null;
		BufferedReader br = new BufferedReader(r);
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("Query=")) {
				currentSpec = line.substring(7).split("_")[0];
				if (!tm.containsKey(currentSpec)) {
					subtm = new TreeMap<String, Integer>();
					tm.put(currentSpec, subtm);
				} else
					currentSpec = null;
			} else if (line.startsWith(">") && currentSpec != null) {
				String thespec = line.substring(2).split("_")[0];
				if (!subtm.containsKey(thespec)) {
					line = br.readLine();
					String trim = line.trim();
					while (!trim.startsWith("Score")) {
						line = br.readLine();
						trim = line.trim();
					}
					int score = 0;
					try {
						score = Integer.parseInt(trim.split("[ ]+")[2]);
					} catch( Exception e ) {
						System.err.println( line );
					}

					subtm.put(thespec, score);
				}
			}

			line = br.readLine();
		}

		for (String key : tm.keySet()) {
			subtm = tm.get(key);
			for (String subkey : subtm.keySet()) {
				ret.add(subtm.get(subkey).doubleValue());
			}

			order.add(key);
		}

		System.err.println(order);

		double sum = 0.0;
		for (double d : ret) {
			sum += d;
		}

		double[] dret = new double[ret.size()];
		for (int i = 0; i < ret.size(); i++) {
			dret[i] = ret.get(i) / sum;
		}

		return dret;
	}
	
	public static Map<String,GeneGroup> loadRnas( Reader reader, Reader subread, int groupIndex ) throws IOException {
		//List<Gene> genelist = new ArrayList<Gene>();
		
		Map<String,GeneGroup>	ggmap = new HashMap<String,GeneGroup>();
		BufferedReader 			br = new BufferedReader( reader );
		String 					line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith(">") ) {
				int i = trim.indexOf("DIR");
				int b = trim.lastIndexOf('_', i-2);
				
				String loc = trim.substring(b+1, i-1);
				String cont = trim.substring(6, b).replace(".fna", "");
				int end = cont.indexOf('_');
				if( end == -1 ) end = cont.length();
				String spec = cont.substring(0,end);
				
				int bil = trim.indexOf(' ', i+15);
				String name = trim.substring(i+15, bil);
				
				boolean rev = trim.charAt(i+3) == '-';
				String[] split = loc.split("-");
				int start = Integer.parseInt( split[0] );
				int stop = Integer.parseInt( split[1] );
				
		/*		int i = trim.indexOf(' ');
				int k = trim.indexOf('[');
				
				String loc = trim.substring(1,i);
				String name = trim.substring(i+1, k-1);
				String spec = trim.substring(k+1, trim.length()-1);
				
				String[] split;
				boolean rev = false;
				if( loc.startsWith("comp") ) {
					rev = true;
					int s = loc.indexOf('(');
					int e = loc.indexOf(')');
					split = loc.substring(s+1, e).split("\\.\\.");
				} else split = loc.split("\\.\\.");
				
				int start = Integer.parseInt( split[0] );
				int stop = Integer.parseInt( split[1] );*/
				
				GeneGroup 	gg;
				Gene 		g = null;
				if( ggmap.containsKey( name ) ) {
					gg = ggmap.get( name );
					for( Gene gene : gg.genes ) {
						g = gene;
					}
				} else {
					gg = new GeneGroup( groupIndex++ );
					Gene gene = new Gene( gg, name, "[Thermus]" );
					gene.refid = name;
					g = gene;
					ggmap.put( name, gg );
				}
				
				Contig contig = contigmap.get( cont );
				/*Contig contig = null;
				if( spec.contains("SG0") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.thermophilusSG0";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("JL_18") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.thermophilusJL18";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("HB8") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.thermophilusHB8";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("HB27") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.thermophilusHB27";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("SA_01") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.scotoductusSA01";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("JL_2") ) {
					spec = "t.oshimaiJL2";
				} else if( spec.contains("Marinithermus") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "m.hydrothermalis";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
					System.err.println( spec );
				} else if( spec.contains("Oceanithermus") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "o.profundus";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				} else if( spec.contains("silvanus") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "mt.silvanus";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				}
				else if( spec.contains("ruber") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "mt.ruber";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				}
				else if( spec.contains("CCB") ) spec = "t.spCCB";
				else if( spec.contains("aquaticus") ) {
					int us = spec.lastIndexOf('_');
					us = spec.lastIndexOf('_', us-1);
					String id = spec.substring( us+1 );
					spec = "t.aquaticus";
					for( String c : contigmap.keySet() ) {
						if( c.contains( id ) ) {
							contig = contigmap.get( c );
							break;
						}
					}
				}
				else if( spec.contains("RLM") ) spec = "t.RLM";*/
				
				Tegeval tegeval = new Tegeval( g, spec, 0.0, null, null, null, contig, null, start, stop, rev ? -1 : 1 );
				g.addTegeval( tegeval );
				gg.addGene( g );
			}
			line = br.readLine();
		}
		br.close();
		
		br = new BufferedReader( subread );
		line = br.readLine();
		while( line != null ) {
			String[] split = line.split("[\t ]+");
			String cont = split[0].replace(".fna", "");
			String name = "tRNA-"+split[4];
			int end = cont.indexOf('_');
			String spec = cont.substring(0, end);
			int start = Integer.parseInt( split[2] );
			int stop = Integer.parseInt( split[3] );
			int ori = 1;
			if( start > stop ) {
				ori = stop;
				stop = start;
				start = ori;
				ori = -1;
			}
			
			GeneGroup 	gg;
			Gene 		g = null;
			if( ggmap.containsKey( name ) ) {
				gg = ggmap.get( name );
				for( Gene gene : gg.genes ) {
					g = gene;
				}
			} else {
				gg = new GeneGroup( groupIndex++ );
				Gene gene = new Gene( gg, name, "[Thermus]" );
				gene.refid = name;
				g = gene;
				ggmap.put( name, gg );
			}
			
			Contig contig = contigmap.get( cont );
			Tegeval tegeval = new Tegeval( g, spec, 0.0, null, null, null, contig, null, start, stop, ori );
			g.addTegeval( tegeval );
			gg.addGene( g );
			
			line = br.readLine();
		}
		br.close();
		
		return ggmap;
	}
	
	static List<String> 							corrInd;
	static List<GeneGroup>							allgenegroups;
	static Map<Set<String>,List<GeneGroup>> 		ggSpecMap;
	static Map<String,Set<GeneGroup>>				specGroupMap;
	private static JComponent newSoft(JButton jb, Container comp, JApplet applet, JComboBox selcomblocal) throws IOException {
		/*InputStream nis2 = GeneSet.class.getResourceAsStream("/exp_short.blastout");
		BufferedReader br2 = new BufferedReader( new InputStreamReader(nis2) );
		String line2 = br2.readLine();
		br2.close();*/
		
		
		//InputStream is = GeneSet.class.getResourceAsStream("/all.aa");
		InputStream is = GeneSet.class.getResourceAsStream("/allthermus.aa");
		// InputStream is = GeneSet.class.getResourceAsStream("/arciformis.aa");
		if (is != null)
			loci2aasequence(new InputStreamReader(is));

		// URL url = new URL("http://192.168.1.69/all.nn");
		try {
			is = GeneSet.class.getResourceAsStream("/allthermus.nn");
			//is = GeneSet.class.getResourceAsStream("/all.nn");
			// is = GeneSet.class.getResourceAsStream("/arciformis.nn");
			if (is != null)
				loci2dnasequence(new InputStreamReader(is));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// url = new URL("http://192.168.1.69/all.fsa");
		try {
			is = GeneSet.class.getResourceAsStream("/allthermus.fna");
			//InputStream cois = GeneSet.class.getResourceAsStream("/contigorder.txt");
			//is = GeneSet.class.getResourceAsStream("/all.fsa");
			// is = GeneSet.class.getResourceAsStream("/arciformis.nn");
			if (is != null)
				loadcontigs(new InputStreamReader(is));
		} catch (Exception e) {
			e.printStackTrace();
		}

		is = GeneSet.class.getResourceAsStream("/intersect_cluster.txt");
		List<Set<String>> iclusterlist = null; //loadSimpleClusters(new InputStreamReader(is));

		//is = GeneSet.class.getResourceAsStream("/thermus_unioncluster.txt");
		//is = GeneSet.class.getResourceAsStream("/allthermus_unioncluster2.txt");
		//is = GeneSet.class.getResourceAsStream("/thomas1.clust");
		is = GeneSet.class.getResourceAsStream("/allthermus_new.clust");
		List<Set<String>> uclusterlist = loadSimpleClusters( new InputStreamReader(is) );
		
		//is = GeneSet.class.getResourceAsStream("/results.uc");
		//List<Set<String>> uclusterlist = loadUClusters( new InputStreamReader(is) );

		/*System.err.println( uclusterlist.size() );
		for( Set<String> clust : uclusterlist ) {
			/*if( clust.size() == 4 ) {
				for( String s : clust ) {
					System.err.print( "  " + s );
				}
				System.err.println();
			}// else System.err.println( uclusterlist.size() );
			if( clust.size() > 10 ) System.err.println( "   " + clust.size() );
		}*/
		
		Map<String, Gene> refmap = new HashMap<String, Gene>();
		Map<String, String> allgenes = new HashMap<String, String>();
		Map<String, Set<String>> geneset = new HashMap<String, Set<String>>();
		Map<String, Set<String>> geneloc = new HashMap<String, Set<String>>();
		Set<String> poddur = new HashSet<String>();
		Map<String, Gene> locgene = new HashMap<String, Gene>();

		//is = GeneSet.class.getResourceAsStream("/thermus_nr.blastout");
		//panCoreFromNRBlast( new InputStreamReader(is), "c:/sandbox/distann/src/thermus_nr_short.blastout", refmap, allgenes, geneset, geneloc, locgene, poddur, uclusterlist ); 
		//is = GeneSet.class.getResourceAsStream("/thermus_nr_short.blastout");
		//is = new FileInputStream( "/home/sigmar/thermus_nr_short.blastout" );
		//is = new FileInputStream( "/home/sigmar/thermus_nr_ftp_short.blastout" );
		
		//is = GeneSet.class.getResourceAsStream("/thermus_nr_ftp_short.blastout");
		//is = GeneSet.class.getResourceAsStream("/ncbithermus_short.blastout");
		is = GeneSet.class.getResourceAsStream("/thermus_ncbi_short.blastout");
		
		//InputStream nis = GeneSet.class.getResourceAsStream("/exp.blastout");
		InputStream nis = null;//GeneSet.class.getResourceAsStream("/exp_short.blastout");
		panCoreFromNRBlast(new InputStreamReader(is), null/*new InputStreamReader(nis)*/, null/*"/u0/sandbox/distann/src/thermus_ncbi_short.blastout"*/, null /*"/u0/sandbox/distann/src/exp_short.blastout"*/, refmap, allgenes, geneset, geneloc, locgene, poddur, uclusterlist);

		geneloc.clear();
		allgenes.clear();
		geneset.clear();
		poddur.clear();
		
		// Map<String,Gene> refmap = new TreeMap<String,Gene>();
		List<Gene> genelist = new ArrayList<Gene>();
		for (String genedesc : refmap.keySet()) {
			Gene gene = refmap.get(genedesc);
			// refmap.put(gene.refid, gene);
			gene.index = genelist.size();
			genelist.add(gene);
			
			/*if( gene.species.size() == 4 ) {
				if( gene.species.containsKey("t.oshimai") && gene.species.containsKey("mt.silvanus") ) {
					System.err.println( gene.index );
					for( String spec : gene.species.keySet() ) {
						System.err.print( "  " + spec );
					}
					System.err.println();
				}
			}*/

			/*
			 * if( gene.species != null ) { for( Set<String> ucluster :
			 * uclusterlist ) { for( String str : gene.species.keySet() ) {
			 * Teginfo stv = gene.species.get(str); for( Tegeval tv : stv.tset )
			 * { if( ucluster.contains(tv.cont) ) { gene.group = ucluster;
			 * break; } } if( gene.group != null ) break; } if( gene.group !=
			 * null ) break; } }
			 */
		}

		int id = 0;
		// Map<Set<String>,ClusterInfo> clustInfoMap = new
		// HashMap<Set<String>,ClusterInfo>();

		corrInd = new ArrayList<String>();
		is = GeneSet.class.getResourceAsStream("/thermus16S.blastout");
		double[] corr16sArray = load16SCorrelation(new InputStreamReader(is), corrInd);

		Collections.sort(uclusterlist, new Comparator<Set<String>>() {
			@Override
			public int compare(Set<String> o1, Set<String> o2) {
				return o1.size() - o2.size();
			}
		});

		Map<Set<String>, double[]> corrList = new HashMap<Set<String>, double[]>();

		List<GeneGroup>	ggList = new ArrayList<GeneGroup>();
		int i = 0;
		Set<String> ss = new HashSet<String>();
		Set<String> gs = new HashSet<String>();
		for (Set<String> cluster : uclusterlist) {
			ss.clear();
			gs.clear();
			
			if( cluster.size() == 1 ) {
				String s = "";
				for( String u : cluster ) s = u;
				if( s.contains("ilva") ) {
					System.err.println();
				}
			}

			Set<Gene> gset = new HashSet<Gene>();
			for (String cont : cluster) {
				String[] split = cont.split("_");
				ss.add(split[0]);
				Gene g = locgene.get(cont);
				
				if (g != null) {
					gs.add(g.refid);
					gset.add(g);
				}
			}

			int val = 0;
			for (Gene g : gset) {
				if (g.species != null) {
					for (String str : g.species.keySet()) {
						val += g.species.get(str).tset.size();
					}
				}
			}

			int len = 20; //16
			if (val == len && ss.size() == len) {
				corrList.put(cluster, new double[20*20]);
			}

			GeneGroup gg = new GeneGroup( i );
			ggList.add( gg );
			gg.addSpecies( ss );
			gg.setGroupCount( val );
			//gg.setGroupGeneCount( gs.size() );
			
			for (Gene g : gset) {
				g.setGeneGroup( gg );
				/*g.groupIdx = i;
				g.groupCoverage = ss.size();
				g.groupGenCount = gs.size();
				g.groupCount = val;*/
				
				//gg.addGene( g );
			}

			i++;

			// ClusterInfo cInfo = new ClusterInfo(id++,ss.size(),gs.size());
			// clustInfoMap.put( cluster, cInfo);
		}
		
		is = GeneSet.class.getResourceAsStream("/rrna.fasta");
		InputStream tis = GeneSet.class.getResourceAsStream("/trna.txt"); //GeneSet.class.getResourceAsStream("/trna_sub.txt");
		Map<String,GeneGroup>	rnamap = loadRnas( new InputStreamReader(is), new InputStreamReader(tis), i );
		for( String ggname : rnamap.keySet() ) {
			GeneGroup gg = rnamap.get( ggname );
			ggList.add( gg );
			
			for( Gene g : gg.genes ) {
				refmap.put( g.refid, g );
				genelist.add( g );
			}
		}
		
		ggSpecMap = new HashMap<Set<String>,List<GeneGroup>>();
		for( GeneGroup gg : ggList ) {
			List<GeneGroup>	speclist;
			Set<String> specset = gg.species;
			if( ggSpecMap.containsKey( specset ) ) {
				speclist = ggSpecMap.get(specset);
			} else {
				speclist = new ArrayList<GeneGroup>();
				ggSpecMap.put( specset, speclist );
			}
			speclist.add( gg );
		}
		
		specGroupMap = new HashMap<String,Set<GeneGroup>>();
		for( GeneGroup gg : ggList ) {
			for( String spec : gg.species ) {
				Set<GeneGroup>	ggset;
				if( !specGroupMap.containsKey( spec ) ) {
					ggset = new HashSet<GeneGroup>();
					specGroupMap.put( spec, ggset );
				} else ggset = specGroupMap.get( spec );
				ggset.add( gg );
			}
		}
		
		allgenegroups = ggList;
		
		for( String ggname : rnamap.keySet() ) {
			GeneGroup gg = rnamap.get( ggname );
			List<Tegeval> tegevals = gg.getTegevals();
			
			for( Tegeval te : tegevals ) {
				Contig contig = te.getContshort();
				if( contig != null ) {
					Tegeval ste = check( contig );
					
					if( ste != null ) {
						while( te.start < ste.start ) {
							Tegeval prev = ste.getPrevious();
							if( prev == null ) break;
							else ste = prev;
						}	
						while( te.start > ste.start ) {
							Tegeval next = ste.getNext();
							if( next == null ) break;
							else ste = next;
						}
 						
						if( te.start < ste.start ) {
							Tegeval prevprev = ste.setPrevious( te );
							if( prevprev != null ) {
								//prevprev.setNext( te );
								te.setPrevious( prevprev );
								
								System.err.println( prevprev.getGene().getName() );
							} else contig.start = te;
						} else {
							Tegeval prevnext = ste.setNext( te );
							te.setPrevious( ste );
							if( prevnext != null ) {
								prevnext.setPrevious( te );
								
								System.err.println( prevnext.getGene().getName() );
							} else contig.end = te;
						}
					} else {
						contig.start = te;
						contig.end = te;
					}
				}
			}
		}
		
		is = GeneSet.class.getResourceAsStream("/contigorder.txt");
		if( is != null ) {
			BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
			String line = br.readLine();
			if( line != null ) line = br.readLine();
			while( line != null ) {
				Contig prevctg = null;
				i = 0;
				int ni1 = line.indexOf('/', i+1);
				int ni2 = line.indexOf('\\', i+1);
				ni1 = ni1 == -1 ? line.length() : ni1; 
				ni2 = ni2 == -1 ? line.length() : ni2;
				int n = Math.min( ni1, ni2 );
				while( n != line.length() ) {
					char c = line.charAt(i);
					String ctgn = line.substring(i+1, n);
					Contig ctg = contigmap.get( ctgn );
					if( c == '\\' ) ctg.setReverse( true );
					if( prevctg != null ) {
						prevctg.setConnection( ctg, ctg.isReverse(), !prevctg.isReverse() );
					}
					prevctg = ctg;
					
					i = n;
					ni1 = line.indexOf('/', i+1);
					ni2 = line.indexOf('\\', i+1);
					ni1 = ni1 == -1 ? line.length() : ni1; 
					ni2 = ni2 == -1 ? line.length() : ni2;
					n = Math.min( ni1, ni2 );
				}
				char c = line.charAt(i);
				String ctgn = line.substring(i+1, n);
				Contig ctg = contigmap.get( ctgn );
				if( c == '\\' ) ctg.setReverse( true );
				if( prevctg != null ) prevctg.setConnection( ctg, ctg.isReverse(), !prevctg.isReverse() );
				
				line = br.readLine();
			}
			br.close();
		}
		
		FileWriter fw = null; // new FileWriter("all_short.blastout");
		//is = GeneSet.class.getResourceAsStream("/all_short.blastout");
		is = GeneSet.class.getResourceAsStream("/all_short.blastout");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("Query=")) {
				if (fw != null)
					fw.write(line + "\n");
				int k = line.indexOf('#');
				if (k != -1) {
					Set<String> cluster = null;
					String query = line.substring(7, k - 1).trim();
					for (Set<String> thecluster : corrList.keySet()) {
						if (thecluster.contains(query)) {
							cluster = thecluster;
							break;
						}
					}

					if (cluster != null) {
						double[] da = corrList.get(cluster);

						int vi = query.indexOf('_');
						int ki = 16 * corrInd.indexOf(query.substring(0, vi));

						line = br.readLine();
						while (line != null && !line.startsWith(">") && !line.startsWith("Query=")) {
							String trim = line.trim();

							String[] split = trim.split("[ ]+");
							String val = split[0];
							if (cluster.contains(val)) {
								if (fw != null)
									fw.write(line + "\n");
								vi = val.indexOf('_');
								int ni = corrInd.indexOf(val.substring(0, vi));

								double el = -10.0;
								try {
									el = Double.parseDouble(split[split.length - 2]);
								} catch (Exception e) {
								}
								da[ki + ni] = el;
							}

							line = br.readLine();
						}

						if (line == null || line.startsWith("Query="))
							continue;
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		// if( fw != null ) fw.close();

		double davg = 1.0 / corr16sArray.length;
		for (Set<String> cluster : corrList.keySet()) {
			double[] dcorr = corrList.get(cluster);

			double dsum = 0.0;
			for (i = 0; i < dcorr.length; i++) {
				dsum += dcorr[i];
			}
			/*
			 * for( i = 0; i < dcorr.length; i++ ) { dcorr[i] /= dsum; }
			 */

			double cval = 0.0;
			double xval = 0.0;
			double yval = 0.0;
			for (i = 0; i < dcorr.length; i++) {
				double xx = (dcorr[i] / dsum - davg);
				double yy = (corr16sArray[i] - davg);

				cval += xx * yy;
				xval += xx * xx;
				yval += yy * yy;
			}

			double r = cval / (Math.sqrt(xval) * Math.sqrt(yval));

			Set<Gene> gset = new HashSet<Gene>();
			for (String cont : cluster) {
				String[] split = cont.split("_");
				ss.add(split[0]);
				Gene g = locgene.get(cont);

				if (g != null) {
					gs.add(g.refid);
					gset.add(g);
				}
			}

			for (Gene g : gset) {
				g.corr16s = r;
				g.corrarr = dcorr;
			}
		}

		int count = 0;
		System.err.println( "blehehe " + genelist.size() );
		for (Gene gg : genelist) {
			System.err.println( "blehehe count " + count++ );
			if (gg.species != null) {
				Set<String> ct = new HashSet<String>();
				for (String sp : gg.species.keySet()) {
					Teginfo stv = gg.species.get(sp);
					for (Tegeval tv : stv.tset) {
						if( tv.cont != null ) {
							ct.add(tv.cont);
							int ind = tv.cont.lastIndexOf("_");
							int val = Integer.parseInt(tv.cont.substring(ind + 1));
	
							String next = tv.cont.substring(0, ind + 1) + (val + 1);
							ct.add(next);
							if (val > 1) {
								String prev = tv.cont.substring(0, ind + 1) + (val - 1);
								ct.add(prev);
							}
						}
					}
				}

				Set<Integer> groupIdxSet = new HashSet<Integer>();
				for (String cont : ct) {
					Gene g = locgene.get(cont);
					if (g != null && g.species != null) {
						for (String sp : g.species.keySet()) {
							Teginfo stv = g.species.get(sp);
							for (Tegeval tv : stv.tset) {
								if (ct.contains(tv.cont)) {
									groupIdxSet.add( g.getGroupIndex() );
									// if( remove ) genefilterset.remove(
									// g.index );
									// else genefilterset.add( g.index );
									// break;
								}
							}
						}
					}
				}
				gg.proximityGroupPreservation = Math.ceil(groupIdxSet.size() / 2.0);
			}
		}
		System.err.println("blehehe");
		locgene.clear();

		// genemap = idMapping( "/home/sigmar/blastout/nilli.blastout",
		// "/mnt/tmp/gene2refseq.txt", "/home/sigmar/idmapping_short2.dat", 5,
		// 1, genemap );
		// genemap = idMapping( "/home/sigmar/blastout/nilli.blastout",
		// "/home/sigmar/thermus/newthermus/idmapping.dat",
		// "/home/sigmar/idmapping_short.dat", 2, 0, genemap );

		/*
		 * Map<String,Gene> unimap = idMapping( "/home/sigmar/idmap.dat",
		 * "/home/sigmar/workspace/distann/idmapping_short.dat", 2, 0, refmap,
		 * false ); Map<String,Gene> genmap = idMapping(
		 * "/mnt/tmp/gene2refseq.txt",
		 * "/home/sigmar/workspace/distann/gene2refseq_short.txt", 5, 1, refmap,
		 * true ); funcMapping( "/home/sigmar/asgard-bio/data/gene2go", genmap,
		 * "/home/sigmar/workspace/distann/gene2go_short.txt" ); funcMappingUni(
		 * "/home/sigmar/asgard-bio/data/sp2go.txt", unimap,
		 * "/home/sigmar/workspace/distann/sp2go_short.txt" );
		 */

		is = GeneSet.class.getResourceAsStream("/idmapping_short.dat");
		Map<String, Gene> unimap = idMapping(new InputStreamReader(is), null, 2, 0, refmap, false);
		is = GeneSet.class.getResourceAsStream("/gene2refseq_short.txt");
		Map<String, Gene> genmap = idMapping(new InputStreamReader(is), null, 5, 1, refmap, true);
		is = GeneSet.class.getResourceAsStream("/gene2go_short.txt");
		funcMapping(new InputStreamReader(is), genmap, null);
		is = GeneSet.class.getResourceAsStream("/sp2go_short.txt");
		funcMappingUni(new InputStreamReader(is), unimap, null);

		unimap.clear();
		genmap.clear();

		Map<Function, Set<Gene>> totalgo = new HashMap<Function, Set<Gene>>();
		for (Gene g : genelist) {
			if (g.funcentries != null) {
				for( Function f : g.funcentries) {
					Set<Gene> set;
					if (totalgo.containsKey(f)) {
						set = totalgo.get(f);
					} else {
						set = new HashSet<Gene>();
						totalgo.put(f, set);
					}
					set.add(g);
				}
			}
		}
		// Map<String,Function> funcmap = readGoInfo( new
		// FileReader("/home/sigmar/asgard-bio/data/gene_ontology_ext.obo"),
		// totalgo, "/home/sigmar/workspace/distann/go_short.obo");
		is = GeneSet.class.getResourceAsStream("/go_short.obo");
		readGoInfo(new InputStreamReader(is), totalgo, null);
		List<Function> funclist = new ArrayList<Function>();
		for (String go : funcmap.keySet()) {
			Function f = funcmap.get(go);
			f.index = funclist.size();
			funclist.add(f);
		}
		totalgo.clear();

		/*
		 * is = GeneSet.class.getResourceAsStream(""); Map<String,String> komap
		 * = koMapping( new FileReader("/home/sigmar/asgard-bio/data/ko"),
		 * funclist, genelist ); for( Function f : funclist ) { if(
		 * komap.containsKey( f.ec ) ) { for( String gn : f.geneentries ) { Gene
		 * g = refmap.get(gn); if( g.keggid == null ) g.keggid =
		 * komap.get(f.ec); } } }
		 */

		Map<Set<String>, ShareNum> specset = new HashMap<Set<String>, ShareNum>();
		int sn = 0;
		/*for (Gene g : genelist) {
			if (g.species != null) {
				ShareNum sharenum = null;
				if (specset.containsKey(g.species.keySet())) {
					sharenum = specset.get(g.species.keySet());
					sharenum.numshare++;
				} else {
					specset.put(g.species.keySet(), new ShareNum(1, sn++));
				}
			}
		}*/
		
		for (GeneGroup gg : allgenegroups) {
			Set<String>	species = gg.getSpecies();
			if( species != null ) {
				ShareNum sharenum = null;
				if (specset.containsKey( species ) ) {
					sharenum = specset.get( species );
					sharenum.numshare++;
				} else {
					specset.put( species, new ShareNum(1, sn++) );
				}
			}
		}

		// aas.clear();

		// return new JComponent() {};
		return showGeneTable(refmap, genelist, funclist, iclusterlist, uclusterlist, specset, null, jb, comp, applet, selcomblocal);// clustInfoMap// );
	}
	
	public static Tegeval check( Contig contig ) {
		for( GeneGroup sgg : allgenegroups ) {
			for( Tegeval ste : sgg.getTegevals() ) {
				if( ste.getContig() != null && ste.getContshort().equals( contig ) ) {
					return ste;
				}
			}
		}
		return null;
	}

	public static class ShareNum implements Comparable<ShareNum> {
		int numshare;
		int sharenum;

		public ShareNum(int numshare, int sharenum) {
			this.numshare = numshare;
			this.sharenum = sharenum;
		}

		@Override
		public int compareTo(ShareNum o) {
			int ret = numshare - o.numshare;

			if (ret == 0)
				ret = sharenum - o.sharenum;

			return ret;
		}

		public String toString() {
			return Integer.toString(numshare);
		}
	};

	private static Map<String, String> koMapping(Reader r, List<Function> funclist, List<Gene> genelist) throws IOException {
		Map<String, String> ret = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(r);
		String line = br.readLine();
		String name = null;
		while (line != null) {
			if (line.startsWith("NAME")) {
				String[] split = line.split("[\t ]+");
				if (split.length > 1) {
					int com = split[1].indexOf(',');
					if (com != -1) {
						name = split[1].substring(1, com);
					} else {
						name = split[1].substring(1);
					}
				}
			} else if (line.contains("TTJ:") || line.contains("TTH:")) {
				ret.put(name, line.trim().replaceAll("[\t ]+", ""));
			}

			line = br.readLine();
		}

		return ret;
	}

	KeyListener keylistener;

	public void initFSKeyListener(final Window window) {
		keylistener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (e.getKeyCode() == KeyEvent.VK_F11 && gd.isFullScreenSupported()) {
					Window w = gd.getFullScreenWindow();
					if (w != null) {
						gd.setFullScreenWindow(null);
					} else {
						gd.setFullScreenWindow(window);
					}
				}
			}
		};
	}

	private static void setColors() {
		colorCodes[0] = new Color(180, 255, 180);
		colorCodes[1] = new Color(180, 245, 190);
		colorCodes[2] = new Color(180, 235, 200);
		colorCodes[3] = new Color(180, 225, 210);
		colorCodes[4] = new Color(180, 215, 220);
		colorCodes[5] = new Color(180, 205, 230);
		colorCodes[6] = new Color(180, 195, 240);
		colorCodes[7] = new Color(180, 185, 250);
		colorCodes[8] = new Color(180, 180, 255);
	}
	
	public static void saveContigOrder() throws IOException {
		FileWriter fw = new FileWriter("/home/sigmar/sandbox/distann/src/contigorder.txt");
		fw.write("co\n");
		Set<Contig>	saved = new HashSet<Contig>();
		for( String cs : contigmap.keySet() ) {
			Contig c = contigmap.get( cs );
			if( !saved.contains( c ) && (c.prev != null || c.next != null) ) {
				Contig prev = c.isReverse() ? c.next : c.prev;
				
				int i = 0;
				while( prev != null ) {
					c = prev;
					prev = c.isReverse() ? c.next : c.prev;
					
					if( i > 50 ) {
						System.err.println();
					}
					if( i++ > 100 ) {
						break;
					}
				}
				
				i = 0;
				saved.add( c );
				fw.write( (c.isReverse() ? "\\" : "/") + c.name );
				Contig next = c.isReverse() ? c.prev : c.next;
				while( next != null ) {
					c = next;
					saved.add( c );
					fw.write( (c.isReverse() ? "\\" : "/") + c.name );
					next = c.isReverse() ? c.prev : c.next;
					
					if( i > 50 ) {
						System.err.println();
					}
					if( i++ > 100 ) break;
				}
				
				fw.write("\n");
			}
		}
		fw.close();
	}
	
	public void stop() {
		super.stop();
		try {
			saveContigOrder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init(Container comp) {
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
		selcomb = new JComboBox();
		
		setColors();

		final Window window = SwingUtilities.windowForComponent(comp);
		initFSKeyListener(window);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);

			frame.addKeyListener(keylistener);
		}

		final JButton jb = new JButton(new AbstractAction("Atlas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					URL url = new URL("file:///home/sigmar/workspace/distann/bin/circle.html");
					GeneSet.this.getAppletContext().showDocument(url, "_blank");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		});

		if( comp instanceof Applet )
			try {
				((GeneSet)comp).saveSel( null, null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		
		try {
			comp.add(newSoft(jb, comp, GeneSet.this, selcomb));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.gc();
	}
	
	public void saveSel( String name, String val) throws Exception {
		JSObject jso = JSObject.getWindow( this );
		jso.call("saveSel", new Object[] { name, val });
	}

	Map<String, Set<Integer>> selectionMap = new HashMap<String, Set<Integer>>();
	public void fillSelectionSave(String json) throws JSONException {
		selectionMap.clear();
		selcomb.removeAllItems();
		
		System.err.println( json.toString() );
		JSONObject jsono = new JSONObject(json);
		Iterator<String> keys = jsono.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			System.err.println( "key: "+key );
			selcomb.addItem(key);
			JSONArray jo = jsono.getJSONArray(key);
			System.err.println( "array: "+jo.toString() );
			Set<Integer> selset = new HashSet<Integer>();
			for (int i = 0; i < jo.length(); i++) {
				selset.add(jo.getInt(i));
			}
			selectionMap.put(key, selset);
		}
	}

	public void init() {
		init(this);
	}

	private static void blastAlign(Reader r, String main, String second) {
		// BufferedReader br = new BufferedReader();
	}
	
	/*static class Connection {
		Contig	contig;
		boolean	ori;
	}*/

	static class Contig implements Comparable<Contig> {
		public Contig(String name) {
			this.name = name;
			loc = 0.0;
			count = 0;
		}
		
		public Contig( String name, StringBuilder sb ) {
			this( name );
			seq = sb;
		}
		
		public String toString() {
			return name;
		}
		
		public String getSpec() {
			int i = name.indexOf('_');
			return name.substring(0, i);
		}
		
		/*@Override
		public boolean equals( Object other ) {
			return other instanceof Contig && name.equals( ((Contig)other).toString() );
		}*/
		
		public String getName() {
			return name;
		}
		
		public StringBuilder getSequence() {
			return seq;
		}

		String 			name;
		double 			loc;
		int 			count;
		StringBuilder	seq;
		boolean			reverse = false;
		Contig			next;
		Contig			prev;
		Tegeval			start;
		Tegeval			end;
		
		public void setConnection( Contig contig, boolean rev, boolean forw ) {
			if( forw ) setForwardConnection( contig, rev );
			else setBackwardConnection( contig, rev );
		}
		
		public void setForwardConnection( Contig contig, boolean rev ) {
			this.next = contig;
			if( rev ) {
				contig.next = this;
				
				this.end.next = contig.end;
				contig.end.next = this.end;
				
				if( this.isReverse() == contig.isReverse() ) {
					Contig nextc = contig;
					while( nextc != null ) {
						nextc.setReverse( !nextc.isReverse() );
						nextc = nextc.isReverse() ? nextc.prev : nextc.next;
					}
				}
			} else {
				contig.prev = this;
				
				this.end.next = contig.start;
				contig.start.prev = this.end;
				
				if( this.isReverse() != contig.isReverse() ) {
					Contig nextc = contig;
					while( nextc != null ) {
						nextc.setReverse( !nextc.isReverse() );
						nextc = nextc.isReverse() ? nextc.prev : nextc.next;
					}
				}
			}
		}
		
		public void setBackwardConnection( Contig contig, boolean rev ) {
			this.prev = contig;
			if( rev ) {
				contig.next = this;
				
				this.start.prev = contig.end;
				contig.end.next = this.start;
				
				if( this.isReverse() != contig.isReverse() ) {
					Contig nextc = contig;
					while( nextc != null ) {
						nextc.setReverse( !nextc.isReverse() );
						nextc = nextc.isReverse() ? nextc.next : nextc.prev;
					}
				}
			} else {
				contig.prev = this;
				
				this.start.prev = contig.start;
				contig.start.prev = this.start;
				
				if( this.isReverse() == contig.isReverse() ) {
					Contig nextc = contig;
					while( nextc != null ) {
						nextc.setReverse( !nextc.isReverse() );
						nextc = nextc.isReverse() ? nextc.next : nextc.prev;
					}
				}
			}
		}
		
		public Contig getNextContig() {
			return next;
		}
		
		public Contig getPrevContig() {
			return prev;
		}
		
		public boolean isReverse() {
			return reverse;
		}
		
		public void setReverse( boolean rev ) {
			this.reverse = rev;
		}
		
		@Override
		public int compareTo(Contig o) {
			return name.compareTo( o.getName() );
		}
	}

	static JSplitPane gsplitpane = null;

	static JFrame frame = new JFrame();
	static Map<String, Contig> 	contigmap = new HashMap<String, Contig>();
	//static final List<Tegeval> 	ltv = new ArrayList<Tegeval>();
	static final List<Contig> 	contigs = new ArrayList<Contig>();
	
	public static void loadContigs( List<GeneGroup> genegroups ) {
		for (GeneGroup gg : genegroups) {
			for( Gene g : gg.genes ) {
				// for( String sp : g.species.keySet() ) {
				for (String spec : g.species.keySet()) {
					Teginfo stv = g.species.get(spec);
					if (stv != null)
						for (Tegeval tv : stv.tset) {
							//int first = tv.cont.indexOf("_");
							//int sec = tv.cont.indexOf("_", first + 1);
							
							if( tv.cont != null ) {
								int sec = tv.cont.lastIndexOf('_');
	
								String cname = tv.cont.substring(0, sec);
								//System.err.println( cname );
								if( !contigmap.containsKey( cname ) ) {
									contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
			}
		}

		contigs.clear();
		for (String c : contigmap.keySet()) {
			contigs.add( contigmap.get(c) );
		}
	}
	
	public static Tegeval getSelectedTe( Point p, JTable rowheader, JRadioButton sequenceView, List<Tegeval> lte, int rowheight ) {
		if( sequenceView.isSelected() ) {			
			for( int y = 0; y < rowheader.getRowCount(); y++ ) {
				int r = rowheader.convertRowIndexToModel( y );
				/*	String species = speclist.get( r );
				for( Gene selectedGene : selectedGenes ) {
				//for( String species : selectedGene.species.keySet() ) {
					if( selectedGene.species.containsKey(species) ) {
						Teginfo ti = selectedGene.species.get( species );
						for( Tegeval te : ti.tset ) {*/
				//for( Tegeval te : lte ) {
				int xoff = 3000;
				
				Tegeval te = lte.get(r);
				Tegeval next = te;
				//int k = 0;
				while( next != null && xoff < 5500 ) {					
					double len = next.getProteinLength()*neighbourscale;											
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					
					if( rect.contains( p ) ) return next;
					
					xoff += len+10;
					next = next.getNext();
				}
				
				xoff = 3000;
				Tegeval prev = te.getPrevious();
				//int k = 0;
				while( prev != null && xoff > 5 ) {					
					double len = prev.getProteinLength()*neighbourscale;
					xoff -= len+10;
					
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					if( rect.contains( p ) ) return prev;
					
					prev = prev.getPrevious();
				}
				//break;
			}
		} else {
			List<Tegeval>	hteglocal = new ArrayList<Tegeval>( lte );
			int xoff = 3000;
			//int k = 0;
			while( xoff < 5500 ) {
				int max = 0;
				for( Tegeval tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Tegeval te = hteglocal.get(r);
					Tegeval next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				xoff += max+10;
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Tegeval te = hteglocal.get(i);
					if( te != null ) hteglocal.set(i, te.getNext() );
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
			
			hteglocal.clear();
			for( Tegeval te : lte ) {
				hteglocal.add( te.getPrevious() );
			}
			xoff = 3000;
			//int k = 0;
			while( xoff > 500 ) {
				int max = 0;
				for( Tegeval tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				xoff -= max+10;
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Tegeval te = hteglocal.get(r);
					Tegeval next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Tegeval te = hteglocal.get(i);
					if( te != null ) hteglocal.set(i, te.getPrevious() );
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
					
			/*		xoff = 3000;
					Tegeval prev = te.getPrevious();
					//int k = 0;
					while( prev != null && xoff > 5 ) {					
						double len = prev.getProteinLength()*neighbourscale;
						xoff -= len+10;
						
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						if( rect.contains( p ) ) return prev;
						
						prev = prev.getPrevious();
					}
				}
			}*/
		}
		
		return null;
	}
	
	public static List<Tegeval> loadContigs( Collection<Gene> genes, String species ) {
		final List<Tegeval> ltv = new ArrayList<Tegeval>();
		for (Gene g : genes) {
			if (g.species != null) {
				System.err.println( g.species.keySet() );
				// for( String sp : g.species.keySet() ) {
				for (String spec : g.species.keySet()) {
					Teginfo stv = g.species.get(spec);
					if (stv != null)
						for (Tegeval tv : stv.tset) {
							if (spec.equals(species)) {
								ltv.add(tv);
							}

							//int first = tv.cont.indexOf("_");
							//int sec = tv.cont.indexOf("_", first + 1);
							if( tv.cont != null ) {
								int sec = tv.cont.lastIndexOf('_');
	
								String cname = tv.cont.substring(0, sec);
								if( !contigmap.containsKey( cname ) ) {
									contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
				// }
			}
		}
		// locsort = true;
		Collections.sort(ltv);
		// locsort = false;

		contigs.clear();
		for (String c : contigmap.keySet()) {
			Contig contig = contigmap.get(c);
			contigs.add( contig );
		}
		
		return ltv;
	}

	public static void mynd(final List<Gene> genes, final JTable sorting, String species) throws IOException {
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	shareColorScheme = new JRadioButton("Sharing");
		final JRadioButton	groupCoverageColorScheme = new JRadioButton("GroupCoverage");
		
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {			
			List<Tegeval> ltv = loadContigs( genes, species );
			
			final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			System.out.println(hey);
			final JTable rowheader = new JTable();
				
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();
				Color altcol = Color.black;
				// Color dg = Color.green.darker();

				public void paintComponent(Graphics g) {
					super.paintComponent(g);

					Gene	lastgene = null;
					Rectangle rc = g.getClipBounds();
					for (int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++) {
						int r = sorting.convertRowIndexToModel(i);
						Gene gene = genes.get(r);

						if( binaryColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								if (i % 2 == 0)
									g.setColor(rd);
								else
									g.setColor(dr);
							} else {
								if (i % 2 == 0)
									g.setColor(gr);
								else
									g.setColor(dg);
							}
						} else if( gcColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								double gcp = Math.min( Math.max( 0.5, gene.getAvgGCPerc() ), 0.8 );
								g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
							} else {
								double gcp = Math.min( Math.max( 0.5, gene.getAvgGCPerc() ), 0.8 );
								g.setColor( new Color( (float)(gcp-0.5)/0.3f, (float)(0.8-gcp)/0.3f, 0.0f ) );
							}
						} else if( locprevColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								double locprev = Math.min( 5.0, gene.proximityGroupPreservation );
								g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
							} else {
								double locprev = Math.min( 5.0, gene.proximityGroupPreservation );
								g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
							}
						} else if( cycColorScheme.isSelected() ) {
							int max = 0;
							if (gene.species != null) {
								for (String str : gene.species.keySet()) {
									Teginfo set = gene.species.get(str);
									for (Tegeval tv : set.tset) {
										max = Math.max(max, tv.numCys);
									}
								}
							}
							
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								g.setColor( new Color( 1.0f-(float)max/27.0f, 1.0f, 1.0f ) );
							} else {
								g.setColor( new Color( (float)max/27.0f, 0.0f, 0.0f ) );
							}
						} else if( lenColorScheme.isSelected() ) {
							int max = -1;
							if (gene.species != null) {
								max = 0;
								for (String str : gene.species.keySet()) {
									Teginfo set = gene.species.get(str);
									for (Tegeval tv : set.tset) {
										if (tv.seq != null)
											max = Math.max(max, tv.seq.length());
									}
								}
							}
							
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								g.setColor( new Color( 1.0f-(float)(max-20)/2775.0f, 1.0f, 1.0f ) );
							} else {
								g.setColor( new Color( (float)(max-20)/2775.0f, 0.0f, 0.0f ) );
							}
						} else if( shareColorScheme.isSelected() ) {
							//if( lastgene == null || !gene.species.keySet().equals( lastgene.species.keySet() ) ) {
							if( lastgene == null || !gene.getGeneGroup().getSpecies().equals( lastgene.getGeneGroup().getSpecies() ) ) {
								if( altcol == Color.black ) altcol = Color.red;
								else altcol = Color.black;
							}
							
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								g.setColor( altcol );
							} else {
								g.setColor( altcol );
							}
						} else if( groupCoverageColorScheme.isSelected() ) {
							int gc = gene.getGroupCoverage();
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								if( gc == -1 ) g.setColor( Color.cyan );
								else g.setColor( new Color( 1.0f-(float)(gc)/28.0f, 1.0f, 1.0f ) );
							} else {
								if( gc == -1 ) g.setColor( Color.blue );
								else g.setColor( new Color( (float)(gene.getGroupCoverage())/28.0f, 0.0f, 0.0f ) );
							}
						}
						lastgene = gene;
						
						if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								String contig = (String) rowheader.getValueAt(y, 0);

								int und = contig.indexOf("_");
								String spec = contig.substring(0, und);
								if (gene.species.containsKey(spec)) {
									Teginfo stv = gene.species.get(spec);
									if( stv != null && stv.tset != null )
									for (Tegeval tv : stv.tset) {
										if( tv.cont != null && tv.cont.startsWith(contig)) {
											g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
										}
									}
								}
							}
						}
					}

					/*
					 * Color color; int i = 0; for( Tegeval tv : ltv ) { if(
					 * tv.ori < 0 ) color = Color.red; else color = Color.green;
					 * 
					 * if( (++i)%2 == 0 ) { color = color.darker(); }
					 * g.setColor( color );
					 * 
					 * if( (tv.stop-tv.start)/1000 > 100 ) {
					 * System.out.println("hund"); } g.fillRect(tv.start/1000,
					 * 0, (tv.stop-tv.start)/10, 20); } System.out.println( i );
					 */
				}

				/*
				 * public Rectangle getBounds() { Rectangle r =
				 * super.getBounds(); r.width = hey; r.height =
				 * rowheader.getHeight(); return r; }
				 * 
				 * public void setBounds( int x, int y, int w, int h ) {
				 * super.setBounds(x, y, hey, rowheader.getHeight()); }
				 */
			};
			
			final AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			binaryColorScheme.setAction( a );
			gcColorScheme.setAction( a );
			locprevColorScheme.setAction( a );
			cycColorScheme.setAction( a );
			lenColorScheme.setAction( a );
			shareColorScheme.setAction( a );
			groupCoverageColorScheme.setAction( a );
			
			binaryColorScheme.setText("Binary");
			gcColorScheme.setText("GC");
			locprevColorScheme.setText("Loc");
			cycColorScheme.setText("#Cys");
			lenColorScheme.setText("Len");
			shareColorScheme.setText("Share");
			groupCoverageColorScheme.setText("GroupCoverage");
			
			binaryColorScheme.setSelected( true );
			
			ButtonGroup	bg = new ButtonGroup();
			bg.add( binaryColorScheme );
			bg.add( gcColorScheme );
			bg.add( locprevColorScheme );
			bg.add( cycColorScheme );
			bg.add( lenColorScheme );
			bg.add( shareColorScheme );
			bg.add( groupCoverageColorScheme );

			c.addMouseListener(new MouseAdapter() {
				Point p;

				public void mousePressed(MouseEvent me) {
					p = me.getPoint();
				}

				public void mouseReleased(MouseEvent me) {
					Point np = me.getPoint();

					if (np.x > p.x) {
						Rectangle rect = sorting.getCellRect(p.x, 0, false);
						rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						sorting.scrollRectToVisible(rect);
						sorting.setRowSelectionInterval(p.x, np.x);
					}
				}
			});

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if (columnIndex == 1)
						return "species";
					else if (columnIndex == 2)
						return "com";
					return "contig";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 2)
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (columnIndex == 2) {
						Contig c = contigs.get(rowIndex);
						if (c.count > 0)
							return (int) ((c.loc) / c.count);
						return 0;
					} else if (columnIndex == 1) {
						Contig c = contigs.get(rowIndex);
						String cname = c.name;
						int i = cname.indexOf('_');
						return cname.substring(0, i);
					}
					return contigs.get(rowIndex).name;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				}

				@Override
				public void addTableModelListener(TableModelListener l) {
				}

				@Override
				public void removeTableModelListener(TableModelListener l) {
				}

			});
			scrollpane.setRowHeaderView(rowheader);
			rowheaderscroll.setViewport(scrollpane.getRowHeader());
			rowheaderscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			rowheaderscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			// scrollpane.setCorner( JScrollPane.UPPER_LEFT_CORNER,
			// rowheader.getTableHeader() );

			rowheader.getRowSorter().addRowSorterListener(new RowSorterListener() {
				@Override
				public void sorterChanged(RowSorterEvent e) {
					c.repaint();
				}
			});

			splitpane.setLeftComponent(rowheaderscroll);
			splitpane.setRightComponent(scrollpane);

			JComponent fillup = new JComponent() {};
			fillup.setPreferredSize(new Dimension(hey, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(hey, rh));
			c.setSize(hey, rh);
		}
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( binaryColorScheme );
		toolbar.add( gcColorScheme );
		toolbar.add( locprevColorScheme );
		toolbar.add( cycColorScheme );
		toolbar.add( lenColorScheme );
		toolbar.add( shareColorScheme );
		toolbar.add( groupCoverageColorScheme );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( splitpane );

		//if (!frame.isVisible()) {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add( panel );

		frame.setVisible(true);
	}
	
	public static void recenter( JTable rowheader, JComponent c ) {
		selectedGenes = new HashSet<Gene>( currentTe.getGene().getGeneGroup().genes );
		//hteg = loadContigs( selectedGenes, null );
		hteg.clear();
		hteg = new ArrayList<Tegeval>();
		for( Gene selectedGene : selectedGenes ) {
			for( String species : selectedGene.species.keySet() ) {
				Teginfo ti = selectedGene.species.get( species );
				for( Tegeval te : ti.tset ) {
					hteg.add( te );
				}
			}
		}
		/*speclist.clear();
		for( Gene selectedGene : selectedGenes ) {
			for( String species : selectedGene.species.keySet() ) {
				if( !speclist.contains( species ) ) speclist.add( species );
			}
		}*/
		rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
		int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
		if (rh == 0) {
			rh = rowheader.getRowCount() * rowheader.getRowHeight();
		}
		c.setPreferredSize( new Dimension(6000, rh) );
		c.setSize(6000, rh);
		
		int i = hteg.indexOf( currentTe );
		if( i != -1 ) {
			int r = rowheader.convertRowIndexToView( i );
			rowheader.setRowSelectionInterval(r, r);
		}
		
		c.repaint();
	}
	
	static double neighbourscale = 1.0;
	static Tegeval currentTe = null;
	static Set<Gene> selectedGenes;
	static List<Tegeval>	hteg;
	//static int colorscheme = 0;
	//static List<String>	speclist;
	public static void neighbourMynd( final Container comp, final List<Gene> genes, final JTable sorting, final Set<Gene> selGenes ) throws IOException {
		selectedGenes = selGenes;
		
		final JRadioButton	sequenceView = new JRadioButton("Sequence");
		final JRadioButton	blocksView = new JRadioButton("Blocks");
		final JButton	zoomIn = new JButton("+");
		final JButton	zoomOut = new JButton("-");
		final JButton	recenter = new JButton("Recenter");
		final JButton	addrelated = new JButton("Add related");
		final JButton	highrel = new JButton("Highlight related");
		final JButton	showseqs = new JButton("Show sequences");
		final JButton	showdnaseqs = new JButton("Show DNA sequences");
		final JMenuBar	mbr = new JMenuBar();
		final JMenu		mnu = new JMenu("Colors");
		final JMenu		mvmnu = new JMenu("Move");
		final JButton	turn = new JButton("Forward");
		
		final JButton	backTen = new JButton("<<");
		final JButton	back = new JButton("<");
		final JButton	forw = new JButton(">");
		final JButton	forwTen = new JButton(">>");
		
		mbr.add( mnu );
		mbr.add( mvmnu );
		final JRadioButtonMenuItem funcol = new JRadioButtonMenuItem("Functions");
		final JRadioButtonMenuItem gccol = new JRadioButtonMenuItem("GC%");
		final JRadioButtonMenuItem abucol = new JRadioButtonMenuItem("Abundance");
		final JRadioButtonMenuItem precol = new JRadioButtonMenuItem("Proximity preservation");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( funcol );
		bg.add( gccol );
		bg.add( abucol );
		bg.add( precol );
		mnu.add( funcol );
		mnu.add( gccol );
		mnu.add( abucol );
		mnu.add( precol );
		if (true) { //gsplitpane == null) {
			//hteg = loadContigs( genes, null );
			//hteg.clear();
			hteg = new ArrayList<Tegeval>();
			for( Gene selectedGene : selectedGenes ) {
				for( String species : selectedGene.species.keySet() ) {
					Teginfo ti = selectedGene.species.get( species );
					for( Tegeval te : ti.tset ) {
						hteg.add( te );
					}
				}
			}
			
			/*speclist = new ArrayList<String>();
			for( Gene selectedGene : selectedGenes ) {
				for( String species : selectedGene.species.keySet() ) {
					if( !speclist.contains( species ) ) speclist.add( species );
				}
			}*/
			
			//final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			final JTable rowheader = new JTable();
			
			final int		nPoints = 6;
			final int[]		xPoints = new int[ nPoints ];
			final int[]		yPoints = new int[ nPoints ];
			
			final Map<Set<Function>,Color>	funcMap = new HashMap<Set<Function>,Color>();
			final Random rand = new Random();
			
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();
				Color altcol = Color.black;
				// Color dg = Color.green.darker();

				public String getToolTipText( MouseEvent me ) {
					Point p = me.getPoint();
					Tegeval te = getSelectedTe(p, rowheader, sequenceView, hteg, rowheader.getRowHeight());
					if( te != null ) return "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+ "<br>" + te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>";
					return null;
				}
				
				public void paintComponent( Graphics g ) {
					super.paintComponent(g);
					
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
					g.setFont( g.getFont().deriveFont( 8.0f ) );
					
					Rectangle clip = this.getVisibleRect(); //g.getClipBounds();
					if( sequenceView.isSelected() ) {
						//int y = 0;
						for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
							int r = rowheader.convertRowIndexToModel( i );
						//	String species = speclist.get( r );
							//for( String species : selectedGene.species.keySet() ) {
							
							/*if( speclist == null ) {
								speclist = new ArrayList<String>();
								for( Gene selGene : selectedGenes ) {
									for( String species : selGene.species.keySet() ) {
										if( !speclist.contains( species ) ) speclist.add( species );
									}
								}
							}*
							
							for( Gene selectedGene : selectedGenes ) {
								if( selectedGene.species.containsKey(species) ) {
									g.setColor( Color.black );
									g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
									
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {*/
						//for( Tegeval te : hteg ) {
							Tegeval te = hteg.get(r);
							int xoff = 3000;
							
							if( clip.x+clip.width > xoff ) {
								Tegeval next = te;
								//int k = 0;
								while( next != null && xoff <= 5500 && clip.x+clip.width > xoff ) {
									double len = next.getProteinLength()*neighbourscale;
									if( next.getGene() != null ) {
										String genename = next.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										if( xoff+len > clip.x ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												
												Gene gene = next.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = next.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = next.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( next.getNext() != null ) {
													tegevals = next.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( next.gc <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.gc-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (next.ori == -1) ^ next.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( next.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									}
									
									xoff += len+10;
									next = next.getNext();
									/*if( tev == null ) {
										Contig nextcontig = next.getContshort().next;
										nextcontig.
									} else next = tev;*/
								}
							}
							
							xoff = 3000;
							if( clip.x < xoff ) {
								Tegeval prev = te != null ? te.getPrevious() : null;
								
								//int k = 0;
								while( prev != null && xoff >= 500 && clip.x < xoff ) {
									double len = prev.getProteinLength()*neighbourscale;
									xoff -= len+10;
									if( prev.getGene() != null ) {
										String genename = prev.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										if( clip.x+clip.width > xoff ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												Gene gene = prev.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = prev.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = prev.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( prev.getNext() != null ) {
													tegevals = prev.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( prev.gc <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)prev.gc-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (prev.ori == -1) ^ prev.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( prev.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											g.setColor( Color.black );
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									}
									prev = prev.getPrevious();
									/*if( prev != null ) {
										len = prev.getProteinLength()*neighbourscale;
										xoff -= len+10;
									}*/
								}
							}
							//y++;
						}
					} else {						
						/*for( Gene selectedGene : selectedGenes ) {
							for( String species : speclist ) {
								if( selectedGene.species.containsKey(species) ) {
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {
										hteg.add( te );
									}
								}
							}
						}*/
						
						List<Tegeval>	hteglocal = new ArrayList<Tegeval>( hteg );
						int xoff =  3000;
						while( xoff < 5500 ) {
							int max = 0;
							for( Tegeval tes : hteglocal ) {
								//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
								int val = 0;
								if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
								if( val > max ) max = val;
							}
							
							for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
								int r = rowheader.convertRowIndexToModel( i );
								Tegeval te = hteglocal.get(r);
								//int y = 0;
								//for( Tegeval te : hteglocal ) {
								//g.setColor( Color.black );
								//g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
								
								if( te != null ) {
									Tegeval next = te;
									if( te.getGene() != null ) {
										String genename = te.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										double len = te.getProteinLength()*neighbourscale;
										
										if( clip.x+clip.width > xoff ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												Gene gene = next.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = next.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = next.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( next.getNext() != null ) {
													tegevals = next.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( next.gc <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.gc-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (next.ori == -1) ^ next.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( next.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											g.setColor( Color.black );
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									/*g.setColor( Color.green );
									Set<Function> funcset = te.getGene().getGeneGroup().getFunctions();
									if( funcset != null && funcset.size() > 0 ) {
										if( funcMap.containsKey( funcset ) ) {
											g.setColor( funcMap.get( funcset ) );
										} else {
											Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
											g.setColor( rc );
											funcMap.put( funcset, rc );
										}
									}*/
									//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
									/*xPoints[0] = xoff; yPoints[0] = y * rowheader.getRowHeight()+2;
									xPoints[1] = xoff+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
									xPoints[2] = xoff+(int)len+5; yPoints[2] = y * rowheader.getRowHeight()+2+6;
									xPoints[3] = xoff+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
									xPoints[4] = xoff; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
									xPoints[5] = xoff+5; yPoints[5] = y * rowheader.getRowHeight()+2+6;
									g.fillPolygon(xPoints, yPoints, nPoints);
									g.setColor( te.isSelected() ? Color.black : Color.gray );
									g.drawPolygon(xPoints, yPoints, nPoints);
									g.setColor( Color.black );
									
									int strlen = g.getFontMetrics().stringWidth( genename );
									while( strlen > len ) {
										genename = genename.substring(0, genename.length()-1);
										strlen = g.getFontMetrics().stringWidth( genename );
									}
									g.setColor( Color.black );
									g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );*/
									}
								}
								//y++;
							}
							xoff += max+10;
							
							for( int i = 0; i < hteglocal.size(); i++ ) {
								Tegeval te = hteglocal.get(i);
								if( te != null ) hteglocal.set(i, te.getNext() );
								//if( te.getLength() > max ) max = te.getLength();
							}
						}
						
						hteglocal.clear();
						hteglocal.addAll( hteg );
						for( int i = 0; i < hteglocal.size(); i++ ) {
							Tegeval te = hteglocal.get(i);
							if( te != null ) hteglocal.set(i, te.getPrevious() );
							//if( te.getLength() > max ) max = te.getLength();
						}
						/************* 
						hteg.clear();
						hteg = new ArrayList<Tegeval>();
						for( Gene selectedGene : selectedGenes ) {
							for( String species : speclist ) {
								if( selectedGene.species.containsKey(species) ) {
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {
										hteg.add( te.getPrevious() );
									}
								}
							}
						}*****************************/
						
						xoff =  3000;
						while( xoff > 500 ) {
							int max = 0;
							for( Tegeval te : hteglocal ) {
								int val = 0;
								if( te != null ) val = (int)(te.getProteinLength()*neighbourscale);
								if( val > max ) max = val;
							}
							
							xoff -= max+10;
							for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
								int r = rowheader.convertRowIndexToModel( i );
								Tegeval te = hteglocal.get(r);
								//g.setColor( Color.black );
								//g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
								
								if( te != null ) {
									Tegeval prev = te;
									if( te.getGene() != null ) {
										String genename = te.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										double len = te.getProteinLength()*neighbourscale;
										/*g.setColor( Color.green );
										Set<Function> funcset = te.getGene().getGeneGroup().getFunctions();
										if( funcset != null && funcset.size() > 0 ) {
											if( funcMap.containsKey( funcset ) ) {
												g.setColor( funcMap.get( funcset ) );
											} else {
												Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
												g.setColor( rc );
												funcMap.put( funcset, rc );
											}
										}*/
										
										int y = i;
										//g.fillRect(xoff, y * rowheader.getRowHeight()+2, len, rowheader.getRowHeight() - 4);
										if( funcol.isSelected() ) {
											g.setColor( Color.green );
											Gene gene = prev.getGene();
											GeneGroup gg = gene.getGeneGroup();
											Set<Function> funcset = gg != null ? gg.getFunctions() : null;
											if( funcset != null && funcset.size() > 0 ) {
												if( funcMap.containsKey( funcset ) ) {
													g.setColor( funcMap.get( funcset ) );
												} else {
													Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
													g.setColor( rc );
													funcMap.put( funcset, rc );
												}
											}
										} else if( abucol.isSelected() ) {
											GeneGroup gg = prev.getGene().getGeneGroup();
											int numspec = gg.species.size();
											float abu = numspec/28.0f;
											Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
											g.setColor( rc );
										} else if( precol.isSelected() ) {
											Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
											shanmap.clear();
											double res = 0.0;
											
											List<Tegeval> tegevals = prev.getGene().getGeneGroup().getTegevals();
											int total = tegevals.size();
											for( Tegeval tev : tegevals ) {
												Tegeval thenext = tev.getNext();
												GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
												int val = 0;
												if( shanmap.containsKey(c) ) val = shanmap.get(c);
												shanmap.put( c, val+1 );
											}
											for( GeneGroup c : shanmap.keySet() ) {
												int val = shanmap.get(c);
												double p = (double)val/(double)total;
												res -= p*Math.log(p)/Math.log(2.0);
											}
											
											if( prev.getNext() != null ) {
												tegevals = prev.getNext().getGene().getGeneGroup().getTegevals();
												total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getPrevious();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
											}
											
											float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
											Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
											g.setColor( rc );
										} else {
											if( prev.gc <= 0 ) {
												Color rc = new Color( 1.0f, 1.0f, 1.0f );
												g.setColor( rc );
											} else {
												float gc = Math.max( 0.0f, Math.min(((float)prev.gc-0.5f)*4.0f, 1.0f) );
												Color rc = new Color( 1.0f-gc, gc, 1.0f );
												g.setColor( rc );
											}
										}
										
										boolean revis = (prev.ori == -1) ^ prev.getContshort().isReverse();
										int addon = revis ? -5 : 5;
										int offset = revis ? 5 : 0;
										//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
										xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
										xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
										xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
										xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
										xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
										xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
										g.fillPolygon(xPoints, yPoints, nPoints);
										g.setColor( prev.isSelected() ? Color.black : Color.gray );
										g.drawPolygon(xPoints, yPoints, nPoints);
										g.setColor( Color.black );
										
										int strlen = g.getFontMetrics().stringWidth( genename );
										while( strlen > len ) {
											genename = genename.substring(0, genename.length()-1);
											strlen = g.getFontMetrics().stringWidth( genename );
										}
										g.setColor( Color.black );
										g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
									}
								}
							}
							
							for( int i = 0; i < hteglocal.size(); i++ ) {
								Tegeval te = hteglocal.get(i);
								if( te != null ) hteglocal.set( i, te.getPrevious() );
								//if( te.getLength() > max ) max = te.getLength();
							}
						}
					}
				
					/*Rectangle rc = g.getClipBounds();
					for (int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++) {
						int r = sorting.convertRowIndexToModel(i);
						Gene gene = genes.get(r);
						
						if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								String contig = (String) rowheader.getValueAt(y, 0);

								int und = contig.indexOf("_");
								String spec = contig.substring(0, und);
								if (gene.species.containsKey(spec)) {
									Teginfo stv = gene.species.get(spec);
									for (Tegeval tv : stv.tset) {
										if (tv.cont.startsWith(contig)) {
											g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
										}
									}
								}
							}
						}
					}*/

					/*
					 * Color color; int i = 0; for( Tegeval tv : ltv ) { if(
					 * tv.ori < 0 ) color = Color.red; else color = Color.green;
					 * 
					 * if( (++i)%2 == 0 ) { color = color.darker(); }
					 * g.setColor( color );
					 * 
					 * if( (tv.stop-tv.start)/1000 > 100 ) {
					 * System.out.println("hund"); } g.fillRect(tv.start/1000,
					 * 0, (tv.stop-tv.start)/10, 20); } System.out.println( i );
					 */
				}

				/*
				 * public Rectangle getBounds() { Rectangle r =
				 * super.getBounds(); r.width = hey; r.height =
				 * rowheader.getHeight(); return r; }
				 * 
				 * public void setBounds( int x, int y, int w, int h ) {
				 * super.setBounds(x, y, hey, rowheader.getHeight()); }
				 */
			};
			c.setToolTipText("bleh");
			
			final AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			funcol.setAction( a );
			gccol.setAction( a );
			abucol.setAction( a );
			precol.setAction( a );
			
			funcol.setText("Functions");
			gccol.setText("GC%");
			abucol.setText("Abundance");
			precol.setText("Proximity preservation");
			
			turn.setAction( new AbstractAction("Forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.err.println("hteg " + hteg.size());
					for( Tegeval te : hteg ) {
						boolean rev = te.ori == -1 ^ te.getContshort().isReverse();
						if( rev ) te.getContshort().setReverse( !te.getContshort().isReverse() );
					}
					c.repaint();
				}
			});
			mvmnu.add( new AbstractAction("Inject forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval previous = currentTe.prev;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							currentTe.setPrevious( te );
							te.setPrevious( previous );
						} else {
							Tegeval next = currentTe.next;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							te.setPrevious( currentTe );
							next.setPrevious( te );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Inject back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval next = currentTe.next;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							te.setPrevious( currentTe );
							next.setPrevious( te );
						} else {
							Tegeval previous = currentTe.prev;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							currentTe.setPrevious( te );
							te.setPrevious( previous );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Delete forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval prevprev = currentTe.prev.prev;
							currentTe.setPrevious( prevprev );
						} else {
							Tegeval nextnext = currentTe.next.next;
							nextnext.setPrevious( currentTe );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Delete back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval nextnext = currentTe.next.next;
							nextnext.setPrevious( currentTe );
						} else {
							Tegeval prevprev = currentTe.prev.prev;
							currentTe.setPrevious( prevprev );
						}
						c.repaint();
					}
				}
			});
			
			zoomIn.setAction( new AbstractAction("+") {
				@Override
				public void actionPerformed(ActionEvent e) {
					neighbourscale *= 1.25;
					c.repaint();
				}
			});
			zoomOut.setAction( new AbstractAction("-") {
				@Override
				public void actionPerformed(ActionEvent e) {
					neighbourscale *= 0.8;
					c.repaint();
				}
			});
			
			backTen.setAction( new AbstractAction("<<") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te;
					i = 0;
					while( currentTe.getPrevious() != null && i < 10 ) {
						currentTe = currentTe.getPrevious();
						i++;
					}
					recenter( rowheader, c );
				}
			});
			back.setAction( new AbstractAction("<") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te.getPrevious() == null ? te : te.getPrevious();
					recenter( rowheader, c );
				}
			});
			forw.setAction( new AbstractAction(">") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te.getNext() == null ? te : te.getNext();
					recenter( rowheader, c );
					
				}
			});
			forwTen.setAction( new AbstractAction(">>") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te;
					i = 0;
					while( currentTe.getNext() != null && i < 10 ) {
						currentTe = currentTe.getNext();
						i++;
					}
					recenter( rowheader, c );
				}
			});
			
			recenter.setAction( new AbstractAction("Recenter") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						recenter( rowheader, c );
					}
				}
			});
			addrelated.setAction( new AbstractAction("Add related") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						GeneGroup gg = currentTe.getGene().getGeneGroup();
						List<Tegeval> lte = gg.getTegevals();
						List<Tegeval> include = new ArrayList<Tegeval>();
						for( Tegeval te : lte ) {
							Contig ct = te.getContshort();
							for( Tegeval ste : hteg ) {
								if( ste.getContshort() == ct ) {
									ct = null;
									break;
								}
							}
							if( ct != null ) include.add( te );
						}
						hteg.addAll( include );
						
						rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
						int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
						if (rh == 0) {
							rh = rowheader.getRowCount() * rowheader.getRowHeight();
						}
						c.setPreferredSize( new Dimension(6000, rh) );
						c.setSize(6000, rh);
						c.repaint();
					}
				}
			});
			highrel.setAction( new AbstractAction("Highlight related") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						List<Tegeval> lte = currentTe.getGene().getGeneGroup().getTegevals();
						for( Tegeval te : lte ) {
							te.setSelected( true );
						}
						c.repaint();
					}
				}
			});
			showseqs.setAction( new AbstractAction("Show sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					showSequences( comp, selectedGenes );
				}
			});
			
			sequenceView.setAction( a );
			blocksView.setAction( a );			
			sequenceView.setText("Sequence");
			blocksView.setText("Blocks");
			
			sequenceView.setSelected( true );
			
			bg = new ButtonGroup();
			bg.add( sequenceView );
			bg.add( blocksView );
			
			c.addMouseListener(new MouseAdapter() {
				Point p;

				public void mousePressed(MouseEvent me) {
					p = me.getPoint();
					
					Tegeval te = getSelectedTe( p, rowheader, sequenceView, hteg, rowheader.getRowHeight() );
					//System.err.println();
					if( te != null ) {
						if( me.getClickCount() == 2 ) {
							c.setToolTipText( "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+"<br>"+ te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>" );
							//c.sett
						} else {
							te.setSelected( !te.isSelected() );
							int i = genes.indexOf( te.getGene() );
							int r = sorting.convertRowIndexToView(i);
							if( te.isSelected() ) {
								currentTe = te;
								if( r >= 0 && r < sorting.getRowCount() ) {
									sorting.addRowSelectionInterval(r, r);
									sorting.scrollRectToVisible( sorting.getCellRect(r, 0, false) );
								}
							} else {
								if( r >= 0 && r < sorting.getRowCount() ) {
									sorting.removeRowSelectionInterval(r, r);
									sorting.scrollRectToVisible( sorting.getCellRect(r, 0, false) );
								}
							}
							c.repaint();
						}
					}
				}

				public void mouseReleased(MouseEvent me) {
					Point np = me.getPoint();

					if (np.x > p.x) {
						Rectangle rect = sorting.getCellRect(p.x, 0, false);
						rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						sorting.scrollRectToVisible(rect);
						//sorting.setRowSelectionInterval(p.x, np.x);
					}
				}
			});
			
			JPopupMenu	popup = new JPopupMenu();
			popup.add( new AbstractAction("Reverse") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						Contig contig = te.getContshort();
						te.getContshort().setReverse( !contig.isReverse() );
						
						Contig nextc = contig.next;
						while( nextc != null ) {
							nextc.setReverse( !nextc.isReverse() );
							nextc = nextc.next;
						}
						
						Contig prevc = contig.prev;
						while( prevc != null ) {
							prevc.setReverse( !prevc.isReverse() );
							prevc = prevc.prev;
						}
						
						/*for( Gene selectedGene : selectedGenes ) {
							String spec = (String)rowheader.getValueAt(r, 0);
							if( selectedGene.species.containsKey( spec ) ) {
								Teginfo ti = selectedGene.species.get( spec );
								for( Tegeval te : ti.tset ) {
									te.getContshort().setReverse( !te.getContshort().isReverse() );
									break;
								}
							}
						}*/
					}
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Connect contig") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					Contig 	cont = te.getContshort();
					String	spec = cont.getSpec();
					
					final List<Contig>	specont = new ArrayList<Contig>();
					for( String name : contigmap.keySet() ) {
						Contig c = contigmap.get( name );
						if( c != cont && spec.equals( c.getSpec() ) ) specont.add( c );
					}
					
					JTable	table = new JTable();
					table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
					table.setAutoCreateRowSorter( true );
					TableModel model = new TableModel() {
						@Override
						public int getRowCount() {
							return specont.size();
						}

						@Override
						public int getColumnCount() {
							return 1;
						}

						@Override
						public String getColumnName(int columnIndex) {
							return "Contig";
						}

						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return String.class;
						}

						@Override
						public boolean isCellEditable(int rowIndex, int columnIndex) {
							return false;
						}

						@Override
						public Object getValueAt(int rowIndex, int columnIndex) {
							return specont.get(rowIndex).name;
						}

						@Override
						public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
							
						}

						@Override
						public void addTableModelListener(TableModelListener l) {}

						@Override
						public void removeTableModelListener( TableModelListener l ) {}
					};
					table.setModel( model );
					JScrollPane	scroll = new JScrollPane( table );
					JCheckBox	reverse = new JCheckBox( "reverse" );
					JCheckBox	forward = new JCheckBox( "forward" );
					Object[] message = { scroll, reverse, forward };
					JOptionPane.showMessageDialog( frame, message, "Select contig", JOptionPane.PLAIN_MESSAGE );
					
					r = table.getSelectedRow();
					i = -1;
					if( r != -1 ) i = table.convertRowIndexToModel( r );
					if( i != -1 ) {
						if( forward.isSelected() ) {
							Tegeval con = cont.end.next;
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.start.prev : cont.end.next;
								
								if( con != null && con.getContshort().equals( cont ) ) {
									break;
								}
							}
						} else {
							Tegeval con = cont.start.prev;
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.start.prev : cont.end.next;
								
								if( con != null && con.getContshort().equals( cont ) ) {
									break;
								}
							}
						}
						
						cont.setConnection( specont.get(i), reverse.isSelected(), forward.isSelected() );
					}
					
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Delete") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Tegeval>	ste = new HashSet<Tegeval>();
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						ste.add( hteg.get(i) );
					}
					hteg.removeAll( ste );
					
					rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
					int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
					if (rh == 0) {
						rh = rowheader.getRowCount() * rowheader.getRowHeight();
					}
					c.setPreferredSize( new Dimension(6000, rh) );
					c.setSize(6000, rh);
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Move left") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						hteg.set( i, te.next == null ? te : te.next );
					}
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Move right") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						hteg.set( i, te.prev == null ? te : te.prev );
					}
					c.repaint();
				}
			});
			rowheader.setComponentPopupMenu( popup );

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.addKeyListener( new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
						Set<Tegeval>	ste = new HashSet<Tegeval>();
						int[] rr = rowheader.getSelectedRows();
						for( int r : rr ) {
							int i = rowheader.convertRowIndexToModel( r );
							ste.add( hteg.get(i) );
						}
						hteg.removeAll( ste );
						
						rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
						int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
						if (rh == 0) {
							rh = rowheader.getRowCount() * rowheader.getRowHeight();
						}
						c.setPreferredSize( new Dimension(6000, rh) );
						c.setSize(6000, rh);
						c.repaint();
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return hteg.size();
				}

				@Override
				public int getColumnCount() {
					return 4;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if( columnIndex == 1 ) return "Contig";
					else if( columnIndex == 2 ) return "Length";
					else if( columnIndex == 3 ) return "Orientation";
					return "Species";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if( columnIndex == 2 ) return Integer.class;
					else if( columnIndex == 3 ) return Boolean.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					//String species = speclist.get( rowIndex );
					Tegeval te = hteg.get(rowIndex);
					if( columnIndex == 0 ) return te.getSpecies();
					else if( columnIndex == 1 ) return te.getContshort().getName();
					else if( columnIndex == 2 ) return te.getLength();
					else if( columnIndex == 3 ) return te.getContshort().isReverse();
					/*for( Gene selectedGene : selectedGenes ) {
						if( selectedGene.species.containsKey( species ) ) {
							Teginfo ti = selectedGene.species.get( species );
							for( Tegeval te : ti.tset ) {
								return te.getContshort().getName();
							}
						}
					}*/
					return null;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

				@Override
				public void addTableModelListener(TableModelListener l) {}

				@Override
				public void removeTableModelListener(TableModelListener l) {}
			});
			scrollpane.setRowHeaderView(rowheader);
			rowheaderscroll.setViewport(scrollpane.getRowHeader());
			rowheaderscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			rowheaderscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			// scrollpane.setCorner( JScrollPane.UPPER_LEFT_CORNER,
			// rowheader.getTableHeader() );

			rowheader.getRowSorter().addRowSorterListener(new RowSorterListener() {
				@Override
				public void sorterChanged(RowSorterEvent e) {
					c.repaint();
				}
			});

			gsplitpane = new JSplitPane();
			gsplitpane.setLeftComponent(rowheaderscroll);
			gsplitpane.setRightComponent(scrollpane);

			JComponent fillup = new JComponent() {};
			fillup.setPreferredSize(new Dimension(6000, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(6000, rh));
			c.setSize(6000, rh);
		}
		
		/*JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( gsplitpane );*/
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( sequenceView );
		toolbar.add( blocksView );
		toolbar.add( zoomIn );
		toolbar.add( zoomOut );
		toolbar.add( backTen );
		toolbar.add( back );
		toolbar.add( forw );
		toolbar.add( forwTen );
		toolbar.add( recenter );
		toolbar.add( addrelated );
		toolbar.add( highrel );
		toolbar.add( showseqs );
		toolbar.add( showdnaseqs );
		toolbar.add( mbr );
		toolbar.add( turn );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( gsplitpane );

		if (!frame.isVisible()) {
			frame = new JFrame();
			frame.addWindowListener( new WindowListener() {
				@Override
				public void windowOpened(WindowEvent e) {}
				
				@Override
				public void windowIconified(WindowEvent e) {}
				
				@Override
				public void windowDeiconified(WindowEvent e) {}
				
				@Override
				public void windowDeactivated(WindowEvent e) {}
				
				@Override
				public void windowClosing(WindowEvent e) {}
				
				@Override
				public void windowClosed(WindowEvent e) {
					try {
						saveContigOrder();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				@Override
				public void windowActivated(WindowEvent e) {}
			});
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(800, 600);
			frame.add( panel );
		}

		frame.setVisible(true);
	}
	
	public static void groupMynd(final List<GeneGroup> geneGroups, final List<Gene> genelist, final JTable sorting) throws IOException {
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	freqColorScheme = new JRadioButton("Freq");
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			loadContigs( geneGroups );
			
			JCheckBox	check = new JCheckBox("All positions");
			JOptionPane.showMessageDialog( null, check );
			final boolean allpos = check.isSelected();
			
			final int hey = geneGroups.size(); // ltv.get(ltv.size()-1).stop/1000;
			System.out.println(hey);
			final JTable rowheader = new JTable();
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();

				// Color dg = Color.green.darker();

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Rectangle rc = g.getClipBounds();
					Set<GeneGroup>	ggset = new HashSet<GeneGroup>();
					for (int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++) {
						int r = sorting.convertRowIndexToModel(i);
						Gene 		tgene = genelist.get(r);
						//GeneGroup genegroup = geneGroups.get(r);
						GeneGroup 	genegroup = tgene.getGeneGroup();
						if( ggset.add( genegroup ) ) {
							if( allpos ) ggset.clear();
							
							if( binaryColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									if (i % 2 == 0)
										g.setColor(rd);
									else
										g.setColor(dr);
								} else {
									if (i % 2 == 0)
										g.setColor(gr);
									else
										g.setColor(dg);
								}
							} else if( gcColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
								} else {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(gcp-0.5)/0.3f, (float)(0.8-gcp)/0.3f, 0.0f ) );
								}
							} else if( locprevColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
								}
							} else if( freqColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									//g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {

								}
								
								double freq = genegroup.species.size()/28;
								//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
								g.setColor( new Color( 1.0f-(float)freq, 1.0f, 1.0f ) );
							} else if( cycColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
								}
							} else if( lenColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
								}
							}
	
							//if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								if( y < rowheader.getRowCount() ) {
									String contig = (String)rowheader.getValueAt(y, 0);
	
									int und = contig.indexOf("_");
									String spec = contig.substring(0, und);
									if( genegroup.species.contains(spec) ) {
										List<Tegeval>	ltv = genegroup.getTegevals( spec );
										//Teginfo stv = gene.species.get(spec);
										for (Tegeval tv : ltv /*stv.tset*/ ) {
											if (tv.cont.startsWith(contig)) {
												g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
											}
										}
									}
								}
							}
						}
					}
					ggset.clear();

					/*
					 * Color color; int i = 0; for( Tegeval tv : ltv ) { if(
					 * tv.ori < 0 ) color = Color.red; else color = Color.green;
					 * 
					 * if( (++i)%2 == 0 ) { color = color.darker(); }
					 * g.setColor( color );
					 * 
					 * if( (tv.stop-tv.start)/1000 > 100 ) {
					 * System.out.println("hund"); } g.fillRect(tv.start/1000,
					 * 0, (tv.stop-tv.start)/10, 20); } System.out.println( i );
					 */
				}

				/*
				 * public Rectangle getBounds() { Rectangle r =
				 * super.getBounds(); r.width = hey; r.height =
				 * rowheader.getHeight(); return r; }
				 * 
				 * public void setBounds( int x, int y, int w, int h ) {
				 * super.setBounds(x, y, hey, rowheader.getHeight()); }
				 */
			};
			
			AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			binaryColorScheme.setAction( a );
			gcColorScheme.setAction( a );
			locprevColorScheme.setAction( a );
			cycColorScheme.setAction( a );
			lenColorScheme.setAction( a );
			freqColorScheme.setAction( a );
			
			binaryColorScheme.setText("Binary");
			gcColorScheme.setText("GC");
			locprevColorScheme.setText("Loc");
			cycColorScheme.setText("#Cys");
			lenColorScheme.setText("Len");
			freqColorScheme.setText("Freq");
			
			binaryColorScheme.setSelected( true );
			
			ButtonGroup	bg = new ButtonGroup();
			bg.add( binaryColorScheme );
			bg.add( gcColorScheme );
			bg.add( locprevColorScheme );
			bg.add( cycColorScheme );
			bg.add( lenColorScheme );
			bg.add( freqColorScheme );

			c.addMouseListener(new MouseAdapter() {
				Point p;

				public void mousePressed(MouseEvent me) {
					p = me.getPoint();
				}

				public void mouseReleased(MouseEvent me) {
					Point np = me.getPoint();

					if (np.x > p.x) {
						Rectangle rect = sorting.getCellRect(p.x, 0, false);
						rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						sorting.scrollRectToVisible(rect);
						sorting.setRowSelectionInterval(p.x, np.x);
					}
				}
			});

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if (columnIndex == 1)
						return "species";
					else if (columnIndex == 2)
						return "com";
					return "contig";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 2)
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (columnIndex == 2) {
						Contig c = contigs.get(rowIndex);
						if (c.count > 0)
							return (int) ((c.loc) / c.count);
						return 0;
					} else if (columnIndex == 1) {
						Contig c = contigs.get(rowIndex);
						String cname = c.name;
						int i = cname.indexOf('_');
						return cname.substring(0, i);
					}
					return contigs.get(rowIndex).name;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

				@Override
				public void addTableModelListener(TableModelListener l) {}

				@Override
				public void removeTableModelListener(TableModelListener l) {}
			});
			scrollpane.setRowHeaderView(rowheader);
			rowheaderscroll.setViewport(scrollpane.getRowHeader());
			rowheaderscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			rowheaderscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			// scrollpane.setCorner( JScrollPane.UPPER_LEFT_CORNER,
			// rowheader.getTableHeader() );

			rowheader.getRowSorter().addRowSorterListener(new RowSorterListener() {
				@Override
				public void sorterChanged(RowSorterEvent e) {
					c.repaint();
				}
			});

			splitpane.setLeftComponent(rowheaderscroll);
			splitpane.setRightComponent(scrollpane);

			JComponent fillup = new JComponent() {};
			fillup.setPreferredSize(new Dimension(hey, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(hey, rh));
			c.setSize(hey, rh);
		}
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( binaryColorScheme );
		toolbar.add( gcColorScheme );
		toolbar.add( locprevColorScheme );
		toolbar.add( cycColorScheme );
		toolbar.add( lenColorScheme );
		toolbar.add( freqColorScheme );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( gsplitpane );

		//if (!frame.isVisible()) {
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(800, 600);
			frame.add( panel );
		//}

		frame.setVisible(true);
	}

	private static void init(String[] args) {
		String[] stuff = { "scoto346", "scoto2101", "antag2120", "scoto2127", "scoto252", "scoto1572", "scoto4063", "eggertsoni2789", "islandicus180610", "tscotoSA01", "ttHB27join", "ttHB8join", "ttaqua" };
		String[] stuff2 = { "aa1", "aa2", "aa4", "aa6", "aa7", "aa8" };
		String[] names = { "aa1.out", "aa2.out", "aa4.out", "aa6.out", "aa7.out", "aa8.out" };
		String[] all = { "all.aa" };
		String[] name = { "all.blastout" };
		String[] nrblastres = { "nilli.blastout" };
		File dir = new File("/home/sigmar/blastout/");
		File dir2 = new File("/home/sigmar/thermus/newthermus/");

		swapmap.put("aa1.out", "scoto346");
		swapmap.put("aa2.out", "scoto2101");
		swapmap.put("aa4.out", "scoto2127");
		swapmap.put("aa6.out", "scoto252");
		swapmap.put("aa7.out", "scoto1572");
		swapmap.put("aa8.out", "scoto4063");

		try {
			// idMapping( "/home/sigmar/blastout/nilli.blastout",
			// "/home/sigmar/thermus/newthermus/idmapping.dat",
			// "/home/sigmar/idmapping_short.dat", 2, 0 );

			/*
			 * JFrame frame = new JFrame(); frame.setSize( 800, 600 );
			 * frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			 * JComponent comp = newSoft(); frame.add( comp ); frame.setVisible(
			 * true );
			 */

			// panCoreFromNRBlast( nrblastres, dir );
			// printnohits( stuff, dir, dir2 );
			// createConcatFsa( stuff, dir2 );

			// fixFile( "/home/sigmar/thermus/newthermus/all.aa",
			// "/home/sigmar/thermus/newthermus/blastres_all.aa",
			// "/home/sigmar/ermm.txt" );

			// loci2aasequence( all, dir2 );
			// loci2gene( nrblastres, dir );
			//clusterFromBlastResults(new File("/home/horfrae/workspace/distann/src"), new String[] { "all.blastout" }, "/home/horfrae/union_cluster.txt", "/home/horfrae/simblastcluster.txt", true);
			
			clusterFromBlastResults(new File("/home/sigmar/"), new String[] { "thermus_join.blastout" }, "/home/sigmar/thermus_cluster.txt", "/home/sigmar/simblastcluster.txt", true);
			
			// clusterFromBlastResults( new File("/home/sigmar/thermus/"), new
			// String[] {"all.blastout"}, "/home/sigmar/intersect_cluster.txt",
			// "/home/sigmar/simblastcluster.txt", false);

			// blastAlign( new
			// FileReader("/home/sigmar/thermus/newthermus/all.aa"),
			// "tscotoSA01", "scoto346" );

			// PrintStream ps = new PrintStream("/home/sigmar/uff.txt");
			// System.setErr( ps );

			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/alls.fsa"), 1 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/alls.fsa"), 2 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/alls.fsa"), 3 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/alls.fsa"), 4 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/alls.fsa"), 5 );

			// aahist( new File("/home/sigmar/tp.aa"), new
			// File("/home/sigmar/nc.aa") );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/nc.aa"), 1 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/nc.aa"), 2 );
			// aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			// File("/home/sigmar/nc.aa"), 3 );
			/*
			 * aahist( new File("/home/sigmar/thermus/out/all.fsa"), new
			 * File("/home/sigmar/nc.aa"), 4 ); aahist( new
			 * File("/home/sigmar/thermus/out/all.fsa"), new
			 * File("/home/sigmar/nc.aa"), 5 );
			 * 
			 * System.err.println();
			 * 
			 * aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/tp.aa"), 1 ); aahist( new
			 * File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/tp.aa"), 2 ); aahist( new
			 * File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/tp.aa"), 3 ); aahist( new
			 * File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/tp.aa"), 4 ); aahist( new
			 * File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/tp.aa"), 5 );
			 * 
			 * System.err.println(); //aahist( new
			 * File("/home/sigmar/thermus/out/aa2.fsa"), new
			 * File("/home/sigmar/nc.aa"), 6 ); aahist( new
			 * File("/home/sigmar/thermus/hb27.aa"), new
			 * File("/home/sigmar/tp.aa"), 1 ); aahist( new
			 * File("/home/sigmar/thermus/hb27.aa"), new
			 * File("/home/sigmar/tp.aa"), 2 ); aahist( new
			 * File("/home/sigmar/thermus/hb27.aa"), new
			 * File("/home/sigmar/tp.aa"), 3 ); aahist( new
			 * File("/home/sigmar/thermus/hb27.aa"), new
			 * File("/home/sigmar/tp.aa"), 4 ); aahist( new
			 * File("/home/sigmar/thermus/hb27.aa"), new
			 * File("/home/sigmar/tp.aa"), 5 );
			 */
			// aahist( new File("/home/sigmar/thermus/hb27.aa"), new
			// File("/home/sigmar/thermus/out/aa2.fsa") );
			// aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new
			// File("/home/sigmar/thermus/hb27.aa") );

			// ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
