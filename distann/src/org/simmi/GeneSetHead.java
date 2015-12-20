/*
 * Copyright (c) 2015 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */

package org.simmi;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.GeneSet.ClusterInfo;
import org.simmi.GeneSet.RunnableResult;
import org.simmi.shared.Annotation;
import org.simmi.shared.Cog;
import org.simmi.shared.Function;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Sequences;
import org.simmi.shared.Serifier;
import org.simmi.shared.Teg;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.shared.TreeUtil.NodeSet;
import org.simmi.unsigned.FlxReader;
import org.simmi.unsigned.JavaFasta;
import org.simmi.unsigned.NativeRun;
import org.simmi.unsigned.SmithWater;

import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import netscape.javascript.JSObject;

/**
 *
 * @version $Id:  $
 */
public class GeneSetHead extends JApplet {
	GeneSet							geneset;
	List<JSplitPane> 				splitpaneList = new ArrayList<JSplitPane>();
	JFrame 							frame = new JFrame();
	
	public GeneSetHead( GeneSet geneset ) {
		super();
		this.geneset = geneset;
	}
	
	final Set<Integer> filterset = new HashSet<Integer>();
	final Set<Integer> genefilterset = new HashSet<Integer>();
	
	final JLabel label = new JLabel();
	
	final RowFilter rowfilter = new RowFilter() {
		@Override
		public boolean include(Entry entry) {
			return filterset.isEmpty() || filterset.contains(entry.getIdentifier());
		}
	};
	final RowFilter genefilter = new RowFilter() {
		@Override
		public boolean include(Entry entry) {
			return genefilterset.isEmpty() || genefilterset.contains(entry.getIdentifier());
		}
	};
	
	public void repaintGCSkew( List<Sequence> selclist, Graphics2D g2, int size, GeneGroup gg, String selspec ) {
		g2.setColor( Color.white );
		g2.fillRect(0, 0, 1024, 1024);
		g2.setFont( g2.getFont().deriveFont(10.0f) );
		int total = 0;
		int g = 0;
		int c = 0;
		double gcstotal = 0.0;
		for( Sequence ctg : selclist ) {
			//Sequence ctg = clist.get( u );
			if( gg != null ) {
				for( Tegeval tv : gg.getTegevals() ) {
					if( tv.getContshort() == ctg ) {					
						int i = tv.start;
						
						int x1 = (int)(512.0+(384.0-100.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
						int y1 = (int)(512.0+(384.0-100.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
						int x2 = (int)(512.0+(384.0+100.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
						int y2 = (int)(512.0+(384.0+100.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
						
						g2.setColor( Color.green );
						g2.drawLine(x1, y1, x2, y2);
					}
				}
			}
			
			double horn = total*2.0*Math.PI/size;
			double horn2 = (total+ctg.length())*2.0*Math.PI/size;
			
			int x1 = (int)(512.0+(384.0-100.0)*Math.cos( horn ));
			int y1 = (int)(512.0+(384.0-100.0)*Math.sin( horn ));
			int x2 = (int)(512.0+(384.0+100.0)*Math.cos( horn ));
			int y2 = (int)(512.0+(384.0+100.0)*Math.sin( horn ));
			g2.setColor( Color.black );
			g2.drawLine(x1, y1, x2, y2);
			
			int xoff = (int)(512.0+(384.0+100.0)*Math.cos( horn2 ));
			int yoff = (int)(512.0+(384.0+100.0)*Math.sin( horn2 ));
			if( horn < Math.PI ) {
				g2.translate( x2, y2 );
				g2.rotate(horn+Math.PI/2.0);
				g2.drawString(ctg.getName(), 0, 0);
				g2.rotate(-horn-Math.PI/2.0);
				g2.translate( -x2, -y2 );
			} else {
				g2.translate( xoff, yoff );
				g2.rotate(horn2+Math.PI/2.0);
				g2.drawString(ctg.getName(), -g2.getFontMetrics().stringWidth(ctg.getName()), 0);
				g2.rotate(-horn2-Math.PI/2.0);
				g2.translate( -xoff, -yoff );
			}
			
			for( int i = 0; i < ctg.length(); i+=500 ) {
				for( int k = i; k < Math.min( ctg.length(), i+500 ); k++ ) {
					char chr = ctg.getCharAt( k );
					if( chr == 'g' || chr == 'G' ) {
						g++;
					} else if( chr == 'c' || chr == 'C' ) {
						c++;
					}
				}
				
				int gcount = 0;
				int ccount = 0;
				int acount = 0;
				int tcount = 0;
				for( int k = i; k < Math.min( ctg.length(), i+10000 ); k++ ) {
					char chr = k-5000 < 0 ? ctg.getCharAt( ctg.length()+(k-5000) ) : ctg.getCharAt(k-5000);
					if( chr == 'g' || chr == 'G' ) {
						gcount++;
					} else if( chr == 'c' || chr == 'C' ) {
						ccount++;
					}
					else if( chr == 'a' || chr == 'A' ) acount++;
					else if( chr == 't' || chr == 'T' ) tcount++;
				}
				
				if( gcount > 0 || ccount > 0 ) {
					double gcskew = (gcount-ccount)/(double)(gcount+ccount);
					
					gcstotal += gcskew;
					
					x1 = (int)(512.0+(384.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y1 = (int)(512.0+(384.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					x2 = (int)(512.0+(384.0+gcskew*100.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y2 = (int)(512.0+(384.0+gcskew*100.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					
					if( gcskew >= 0 ) g2.setColor( Color.blue );
					else g2.setColor( Color.red );
					g2.drawLine(x1, y1, x2, y2);
				}
				
				if( acount > 0 || tcount > 0 ) {
					double atskew = (acount-tcount)/(double)(acount+tcount);
					
					x1 = (int)(512.0+(300.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y1 = (int)(512.0+(300.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					x2 = (int)(512.0+(300.0+atskew*100.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y2 = (int)(512.0+(300.0+atskew*100.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					
					if( atskew >= 0 ) g2.setColor( Color.blue );
					else g2.setColor( Color.red );
					g2.drawLine(x1, y1, x2, y2);
				}
			}
			total += ctg.length();
		}
		
		g2.setColor( Color.black );
		g2.setFont( g2.getFont().deriveFont( java.awt.Font.ITALIC ).deriveFont(32.0f) );
		String[] specsplit; // = selspec.split("_");
		
		if( selspec.contains("hermus") ) specsplit = selspec.split("_");
		else {
			Matcher m = Pattern.compile("\\d").matcher(selspec); 
			int firstDigitLocation = m.find() ? m.start() : 0;
			if( firstDigitLocation == 0 ) specsplit = new String[] {"Thermus", selspec};
			else specsplit = new String[] {"Thermus", selspec.substring(0,firstDigitLocation), selspec.substring(firstDigitLocation)};
		}
		
		int k = 0;
		for( String spec : specsplit ) {
			int strw = g2.getFontMetrics().stringWidth( spec );
			g2.drawString( spec, (1024-strw)/2, 1024/2 - specsplit.length*32/2 + 32 + k*32 );
			k++;
		}
		
		/*double gcs = gcstotal/total; //(g-c)/(g+c);
		String gcstr = Double.toString( Math.round( gcs*1000000.0 ) );
		g2.drawString( gcstr+"ppm", 768, 512 );*/
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
	
	boolean ftableisselecting = false;
	boolean tableisselecting = false;
	
	static TableModel nullmodel = new AbstractTableModel() {
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return null;
		}
		
		@Override
		public int getRowCount() {
			return 0;
		}
		
		@Override
		public int getColumnCount() {
			return 0;
		}
	};
	
	private void importStuff() throws IOException, UnavailableServiceException {
		boolean fail = true;
		InputStream	is = null;
		/*try {
			FileOpenService fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
			FileContents fc = fos.openFileDialog(null, null);
			is = fc.getInputStream();
		} catch( Exception e ) {
			fail = true;
		}*/
		
		Path zipp = null;
		if( fail && zipp == null ) {
			JFileChooser fc = new JFileChooser();
			if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
				zipp = fc.getSelectedFile().toPath();				
				//is = new FileInputStream( zipfile );
			}
		}
		
		//if( is != null ) {
		/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bab = new byte[1024];
		int rd = is.read( bab );
		while( rd > 0 ) {
			baos.write(bab, 0, rd);
			rd = is.read( bab );
		}
		baos.close();
		zipf = baos.toByteArray();*/
		
		/*ZipInputStream zipm = new ZipInputStream( new ByteArrayInputStream( zipf ) );
		ZipEntry ze = zipm.getNextEntry();
		while( ze != null ) {
			String zname = ze.getName();
			if( zname.equals("unresolved.blastout") ) {
				unresolvedmap = loadunresolvedmap( new InputStreamReader( zipm ) );
			} else if( zname.equals("namemap.txt") ) {
				namemap = loadnamemap( new InputStreamReader( zipm ) );
			} else if( ze.getName().equals("designations.txt") ) {
				designations = loadDesignations( new InputStreamReader(zipm), deset );
			} else if( ze.getName().equals("plasmids.txt") ) {
				plasmids = loadPlasmids( new InputStreamReader(zipm) );
			}
			ze = zipm.getNextEntry();
		}
		if( designations == null ) designations = new TreeMap<String,String>();
		//List<Set<String>> uclusterlist = null;*/
			
		if( zipp != null ) {
			geneset.loadStuff( zipp );
			
			//syncolorcomb = new JComboBox();
			syncolorcomb.removeAllItems();
			syncolorcomb.addItem("");
			for( String spec : geneset.speccontigMap.keySet() ) {
				syncolorcomb.addItem( spec );
			}
			syncolorcomb.addItem("All");
			
			/*
			 * is = GeneSet.class.getResourceAsStream(""); Map<String,String> komap
			 * = koMapping( new FileReader("/home/sigmar/asgard-bio/data/ko"),
			 * funclist, genelist ); for( Function f : funclist ) { if(
			 * komap.containsKey( f.ec ) ) { for( String gn : f.geneentries ) { Gene
			 * g = refmap.get(gn); if( g.keggid == null ) g.keggid =
			 * komap.get(f.ec); } } }
			 */
	
			geneset.updateShareNum( geneset.specList );
			
			Set<String> allecs = new HashSet<String>();
			for( Function f : geneset.funclist ) {
				if (f.ec != null)
					allecs.add(f.ec);
			}
	
			for (String val : geneset.pathwaymap.keySet()) {
				Set<String> set = geneset.pathwaymap.get(val);
				for (String s : set) {
					if (allecs.contains(s)) {
						combo.addItem(val);
						break;
					}
				}
			}
	
			Set<String> set = new TreeSet<String>();
			for (Gene g : geneset.genelist) {
				Tegeval tv = g.tegeval;
				if (tv.eval <= 0.00001 && tv.teg != null && tv.teg.startsWith("[") && tv.teg.endsWith("]"))
					set.add(tv.teg);
			}
	
			for (String sp : set) {
				specombo.addItem(sp);
			}
			
			if( geneset.uclusterlist != null ) geneset.clusterMap = Serifier.initClusterNew(geneset.uclusterlist, null, null);	
			//table.tableChanged( new TableModelEvent( table.getModel() ) );
			//ftable.tableChanged( new TableModelEvent( ftable.getModel() ) );
			table.setModel( nullmodel );
			ftable.setModel( nullmodel );
			table.setModel( groupModel );
			ftable.setModel( ftablemodel );
			
			int me = 0;
			int mu = 0;
			for( Gene g : geneset.genelist ) {
				if( g.getGeneGroup() != null ) me++;
				else mu++;
			}
			System.err.println( me + "  " + mu );
		}
		
		String userhome = System.getProperty("user.home");
		File f = new File( userhome );
		File idf = new File(f, "idspec.txt");
		BufferedWriter bw = new BufferedWriter( new FileWriter(idf) ); //Files.newBufferedWriter(idf.toPath(), OpenOption.);
		for( String id : geneset.refmap.keySet() ) {
			Gene g = geneset.refmap.get(id);
			bw.write( id + "\t" + g.getSpecies() + "\n" );
		}
		bw.close();
	}
	
	public Serifier getConcatenatedSequencesMaxLength() {
		Map<String,Sequence>	smap = new HashMap<String,Sequence>();
		Set<String>				specset = new HashSet<String>();
		Map<GeneGroup,Integer>	genegroups = new HashMap<GeneGroup,Integer>();
		int[] rr = table.getSelectedRows();
		if( table.getModel() == groupModel ) {
			for (int r : rr) {
				int cr = table.convertRowIndexToModel(r);
				int max = 0;
				GeneGroup gg = geneset.allgenegroups.get(cr);
				specset.addAll( gg.getSpecies() );
				
				for( Tegeval tv : gg.getTegevals() ) {
					int l = tv.getAlignedSequence().length();
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		} else {
			for (int r : rr) {
				int cr = table.convertRowIndexToModel(r);
				Gene g = geneset.genelist.get(cr);
				int max = 0;
				GeneGroup gg = g.getGeneGroup();
				specset.addAll( gg.getSpecies() );
				for( Tegeval tv : gg.getTegevals() ) {
					int l = tv.getAlignedSequence().length();
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		}
		
		//List<Sequence>	seqs = new ArrayList<Sequence>();
		/*Map<String>	specset = new HashMap<String,Integer>();
		for( GeneGroup ggroup : genegroups ) {
			int max = 0;
			for( Tegeval tv : ggroup.getTegevals() ) {
				int len = tv.getAlignedSequence().length();
				if( len > max ) {
					max = len;
					specset.put( tv.getContshort().getSpec(), max );
				}
			}
		}*/
		
		for( GeneGroup ggroup : genegroups.keySet() ) {
			int len = genegroups.get( ggroup );
			for( String selspec : specset ) {				
				List<Tegeval> ltv = ggroup.getTegevals(selspec);
				
				String spec;
				if( selspec.contains("hermus") ) spec = selspec;
				else {
					Matcher m = Pattern.compile("\\d").matcher(selspec);
					int firstDigitLocation = m.find() ? m.start() : 0;
					if( firstDigitLocation == 0 ) spec = "Thermus_" + selspec;
					else spec = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
				}
				
				Sequence seq;
				if( smap.containsKey( spec ) ) {
					seq = smap.get( spec );
				} else {					
					seq = new Sequence( spec, null );					
					smap.put( spec, seq );
				}
				
				int max = 0;
				StringBuilder 	seqstr = null;
				for( Tegeval tv : ltv ) {
					int seqlen = tv.getLength();
					if( seqlen > max ) {
						seqstr = tv.getAlignedSequence().getStringBuilder();
						max = seqlen;
					}
					
					/*if( seqstr != null && seqstr.length() > 0 ) {
						break;
					}*/
				}
				
				if( seqstr != null && seqstr.length() > 0 ) {
					seq.append( seqstr );
				} else {
					for( int i = 0; i < len; i++ ) seq.append( "-" );
				}
			}
		}
		
		Serifier			serifier = new Serifier();
		for( String spec : smap.keySet() ) {
			Sequence seq = smap.get( spec );
			serifier.addSequence( seq );
		}
		return serifier;
	}
	
	public Serifier getConcatenatedSequences( boolean proximityJoin, boolean namefix ) {
		Set<String>							specset = new HashSet<String>();
		Map<GeneGroup,Integer>				genegroups = new HashMap<GeneGroup,Integer>();
		int[] rr = table.getSelectedRows();
		if( table.getModel() == groupModel ) {
			for (int r : rr) {
				int cr = table.convertRowIndexToModel(r);
				int max = 0;
				GeneGroup gg = geneset.allgenegroups.get(cr);
				specset.addAll( gg.getSpecies() );
				
				for( Tegeval tv : gg.getTegevals() ) {
					Sequence seq = tv.getAlignedSequence();
					int l = seq != null ? seq.length() : 0;
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		} else {
			for (int r : rr) {
				int cr = table.convertRowIndexToModel(r);
				Gene g = geneset.genelist.get(cr);
				int max = 0;
				GeneGroup gg = g.getGeneGroup();
				specset.addAll( gg.getSpecies() );
				for( Tegeval tv : gg.getTegevals() ) {
					int l = tv.getAlignedSequence().length();
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		}
		return geneset.getConcatenatedSequences(proximityJoin, genegroups, specset, namefix);
	}
	
	public void blastn( boolean show ) {
		String dbname = null;
		
		final JTextArea ta = new JTextArea();
		JScrollPane sp = new JScrollPane( ta );
		Dimension dim = new Dimension( 400, 300 );
		sp.setSize( dim );
		sp.setPreferredSize( dim );
		JTextField	tf = new JTextField();
		tf.setText( "0.00001" );
		Object[] objs = new Object[] { sp, tf };
		JOptionPane.showMessageDialog(GeneSetHead.this, objs);
		
		doBlastn( ta.getText(), tf.getText(), false, null, show );
	}
	
	private void proxi(JTable table, int[] rr, List<Gene> genelist, Set<Integer> genefilterset, boolean remove, boolean onlygenes) {
		/*Set<String> ct = new HashSet<String>();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			
			GeneGroup gg;
			if( table.getModel() == groupModel ) {
				gg = allgenegroups.get( cr );
			} else {
				gg = geneset.genelist.get(cr).getGeneGroup();
			}
			// genefilterset.add( gg.index );
			for( Gene g : gg.genes ) {
				Tegeval tv = g.tegeval;
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
		}*/
		
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			
			Gene gene = null;
			GeneGroup gg;
			if( table.getModel() == groupModel ) {
				gg = geneset.allgenegroups.get( cr );
			} else {
				gene = geneset.genelist.get(cr);
				gg = gene.getGeneGroup();
			}
			
			if( onlygenes ) {
				genefilterset.add( gene.index );
				Annotation next = gene.tegeval.getNext();
				if( next != null ) {
					Gene ng = next.getGene();
					if( ng != null ) genefilterset.add( ng.index );
				}
				Annotation prev = gene.tegeval.getPrevious();
				if( prev != null ) {
					Gene pg = prev.getGene();
					if( pg != null ) genefilterset.add( pg.index );
					else {
						System.err.println();
					}
				}
			} else {
				genefilterset.add( gg.index );
				for( Gene g : gg.genes ) {
					Annotation next = g.tegeval.getNext();
					if( next != null ) {
						
						/*Gene n = next.getGene();
						int k = this.genelist.indexOf(n);
						System.err.println( k );
						
						n = g.tegeval.gene;
						k = this.genelist.indexOf(n);
						System.err.println( k );*/
						
						GeneGroup ngg = next.getGene().getGeneGroup();
						if( ngg != null ) genefilterset.add( ngg.index );
						/*else {
							int tot = 0;
							int mot = 0;
							for( String cstr : contigmap.keySet() ) {
								Sequence ctg = contigmap.get( cstr );
								if( ctg.tlist != null ) for( Tegeval tv : ctg.tlist ) {
									Gene gn = tv.getGene();
									
									if( tv == next ) {
										System.err.println( gn );
									}
									
									if( gn.getGeneGroup() != null ) {
										tot++;
									} else mot++;
								}
							}
							System.err.println( tot + "  " + mot );
							System.err.println();
						}*/
					}
					Annotation prev = g.tegeval.getPrevious();
					if( prev != null ) {
						GeneGroup pgg = prev.getGene().getGeneGroup();
						if( pgg != null ) genefilterset.add( pgg.index );
						else {
							System.err.println();
						}
					}
				}
			}
		}
		
		/*if( table.getModel() == groupModel ) {
			for( Gene g : geneset.genelist ) {
				Tegeval tv = g.tegeval;
				if( ct.contains(tv.cont) ) {
					if (remove)
						genefilterset.remove(g.getGeneGroup().index);
					else
						genefilterset.add(g.getGeneGroup().index);
					break;
				}
			}
		} else {
			for( Gene g : geneset.genelist ) {
				Tegeval tv = g.tegeval;
				//if( ct.contains(tv.cont) ) {
					if (remove)
						genefilterset.remove(g.index);
					else
						genefilterset.add(g.index);
					break;
				//}
			}
		}*/
	}
	
	public void blast( boolean x ) {
		//String dbname = null;
		
		/*try {
			Map<String,String> env = new HashMap<String,String>();
			Path path = zipfile.toPath();
			String uristr = "jar:" + path.toUri();
			geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")* );
			geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
			
			final PathMatcher pm = geneset.zipfilesystem.getPathMatcher("*.p??");
			for( Path p : zipfilesystem.getRootDirectories() ) {
				Files.walkFileTree( p, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if( pm.matches( file ) ) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] bb = new byte[1024];
							int r = zipm.read( bb );
							while( r > 0 ) {
								baos.write(bb, 0, r);
								r = zipm.read( bb );
							}
							Files.write( new File("c:/"+file.getFileName().).toPath(), baos.toByteArray(), StandardOpenOption.CREATE);
							//unresolvedmap = loadunresolvedmap( new InputStreamReader( zipm ) );
							
							dbname = zname.substring(0, zname.length()-4);
						}
						return null;*
					}
				});
			}*/
			//Path nf = zipfilesystem.getPath("/unresolved.blastout");
			//unresolvedmap = loadunresolvedmap( new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
			
			/*ZipInputStream zipm = new ZipInputStream( new ByteArrayInputStream( zipf ) );
			ZipEntry ze = zipm.getNextEntry();
			while( ze != null ) {
				String zname = ze.getName();
				if( zname.endsWith(".phr") || zname.endsWith(".pin") || zname.endsWith(".psq") ) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] bb = new byte[1024];
					int r = zipm.read( bb );
					while( r > 0 ) {
						baos.write(bb, 0, r);
						r = zipm.read( bb );
					}
					Files.write( new File("c:/"+zname).toPath(), baos.toByteArray(), StandardOpenOption.CREATE);
					//unresolvedmap = loadunresolvedmap( new InputStreamReader( zipm ) );
					
					dbname = zname.substring(0, zname.length()-4);
				}
				ze = zipm.getNextEntry();
			}*
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		
		final JTextArea ta = new JTextArea();
		JScrollPane sp = new JScrollPane( ta );
		Dimension dim = new Dimension( 400, 300 );
		sp.setSize( dim );
		sp.setPreferredSize( dim );
		JTextField	tf = new JTextField();
		tf.setText( "0.00001" );
		Object[] objs = new Object[] { sp, tf };
		JOptionPane.showMessageDialog(GeneSetHead.this, objs);
		
		doBlast( ta.getText(), tf.getText(), true, null, x );
	}
	
	public Set<String> getSelspec( Component comp, final List<String> specs, final JCheckBox ... contigs ) {
		final JTable	table = new JTable();
		table.setDragEnabled( true );
		JScrollPane	scroll = new JScrollPane( table );
		table.setAutoCreateRowSorter( true );
		
		final List<Sequence> ctgs = new ArrayList<Sequence>( geneset.contigmap.values() );
		final TableModel contigmodel = new TableModel() {
			@Override
			public int getRowCount() {
				return ctgs.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Contigs/Scaffolds";
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
				return ctgs.get( rowIndex ).toString();
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		final TableModel specmodel = new TableModel() {
			@Override
			public int getRowCount() {
				return specs.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Spec";
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
				return specs.get(rowIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		table.setModel( specmodel );
		
		TransferHandler th = dragRows( table, specs );
		table.setTransferHandler( th );
		
		if( contigs != null && contigs.length > 0 && !contigs[0].getText().equals("Plasmid") ) contigs[0].addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if( contigs[0] != null && contigs[0].isSelected() ) table.setModel( contigmodel );
				else table.setModel( specmodel );
			}
		});
		
		Object[] ctls = new Object[] { scroll, contigs };
		//Object[] ctls2 = new Object[] { scroll };
		
		//if( contigs != null ) 
		JOptionPane.showMessageDialog( comp, ctls );
		//else JOptionPane.showMessageDialog( comp, ctls2 );
		Set<String>	selspec = new LinkedHashSet<String>();
		for( int i = 0; i < table.getRowCount(); i++ ) {
			if( table.isRowSelected(i) ) {
				String spec = (String)table.getValueAt(i, 0);
				System.err.println("test " + spec);
				selspec.add( spec );
			}
		}
		return selspec;
	}
	
	public JTable getGeneTable() {
		return table;
	}
	
	public JTable getFunctionTable() {
		return ftable;
	}
	
	int searchi = 0;
	public int searchTable( JTable table, String text, int i, boolean back, int ... columns ) {
		int v;
		if( back ) {
			v = i-1;
			if( v == -1 ) v = table.getRowCount();
		} else v = (i+1)%table.getRowCount();
		if( table.getModel() == defaultModel ) {
			while( v != i ) {
				//int m = table.convertRowIndexToModel(v);
				//if( m != -1 ) {
					//Gene g = geneset.genelist.get(m);
					//String name = column == 7 || column[0] == 8 ? g.getGeneGroup().getCommonSymbol() + ", " + g.getGeneGroup().getCommonKOName( ko2name ) + ", " +  : g.getGeneGroup().getCommonName();
					
				String name = "";
				for( int k : columns ) name += table.getValueAt(v, k);
				
				if( name.toLowerCase().contains( text ) ) {
					//int r = table.convertRowIndexToView(v);
					Rectangle rect = table.getCellRect(v, 0, true);
					table.scrollRectToVisible( rect );
					break;
				}
				if( back ) {
					v--;
					if( v == -1 ) v = table.getRowCount()-1;
				} else v = (v+1)%table.getRowCount();
					
				//} else break;
			}
		} else {
			while( v != i ) {
				//int m = table.convertRowIndexToModel(v);
				//if( m != -1 ) {
					
				//GeneGroup gg = allgenegroups.get(m);
				//String name = gg.getCommonName();
				String name = "";
				for( int k : columns ) name += table.getValueAt(v, k);
				
				if( name.toLowerCase().contains( text ) ) {
					//int r = table.convertRowIndexToView(i);
					Rectangle rect = table.getCellRect(v, 0, true);
					table.scrollRectToVisible( rect );
					break;
				}
				if( back ) {
					v--;
					if( v == -1 ) v = table.getRowCount()-1;
				} else v = (v+1)%table.getRowCount();
				
				//} else break;
			}
		}
		return v;
	}
	
	public void doBlast( final String fasta, final String evaluestr, final boolean ids, final RunnableResult rr, boolean x ) {
		/*File blastn;
		File blastp;
		File makeblastdb;
		File blastx = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastx.exe" );
		if( !blastx.exists() ) {
			blastx = new File( "/opt/ncbi-blast-2.2.29+/bin/blastx" );
			if( !blastx.exists() ) {
				blastx = new File( "/usr/local/ncbi/blast/bin/blastx" );
				blastn = new File( "/usr/local/ncbi/blast/bin/blastn" );
				blastp = new File( "/usr/local/ncbi/blast/bin/blastp" );
				
				makeblastdb = new File( "/usr/local/ncbi/blast/bin/makeblastdb" );
			} else {
				blastn = new File( "/opt/ncbi-blast-2.2.29+/bin/blastn" );
				blastp = new File( "/opt/ncbi-blast-2.2.29+/bin/blastp" );
				
				makeblastdb = new File( "/opt/ncbi-blast-2.2.29+/bin/makeblastdb" );
			}
		} else {
			blastn = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastn.exe" );
			blastp = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastp.exe" );
			
			makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\makeblastdb.exe" );
		}*/
		
		String OS = System.getProperty("os.name").toLowerCase();
		int procs = Runtime.getRuntime().availableProcessors();
		String[] mcmds = { OS.indexOf("mac") >= 0 ? "/usr/local/bin/makeblastdb" : "makeblastdb", "-dbtype", "prot", "-title", "tmp", "-out", "tmp" };
		List<String>	lcmd = new ArrayList<String>( Arrays.asList(mcmds) );
		
		final ProcessBuilder mpb = new ProcessBuilder( lcmd );
		mpb.redirectErrorStream( true );
		try {
			final Process mp = mpb.start();
			
			new Thread() {
				public void run() {
					try {
						OutputStream pos = mp.getOutputStream();
						Writer ow = new OutputStreamWriter( pos );
						for( Gene g : geneset.genelist ) {
							if( g.getTag() == null || g.getTag().length() == 0 ) {
								GeneGroup gg = g.getGeneGroup();
							
								if( gg != null ) {
									String name;
									if( ids ) name = g.id;
									else {
										String addstr = "";
										Cog cog = gg.getCommonCog( geneset.cogmap );
										String cazy = gg.getCommonCazy( geneset.cazymap );
										if( cog != null ) addstr += "_"+cog.id;
										if( cazy != null ) {
											if( addstr.length() > 0 ) addstr += cazy;
											addstr += "_"+cazy;
										}
										if( addstr.length() > 0 ) addstr += "_";
										
										name = g.name + addstr + "[" + g.id + "]";
										//pos.write( (">" + g.name + addstr + "[" + g.id + "]\n").getBytes() );
									}
									Sequence sb = g.tegeval.getProteinSequence();
									sb.setName( name );
									sb.writeSequence(ow);
									/*for( int i = 0; i < sb.length(); i+=70 ) {
										pos.write( sb.substring(i, Math.min( sb.length(), i+70) ).getBytes() );
									}
									pos.write( '\n' );*/
								}
							}
						}
						ow.close();
						pos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			new Thread() {
				public void run() {
					try {
						InputStream pin = mp.getInputStream();
						InputStreamReader rdr = new InputStreamReader( pin );
						//FileReader fr = new FileReader( new File("c:/dot.blastout") );
						BufferedReader br = new BufferedReader( rdr );
						String line = br.readLine();
						while( line != null ) {
							System.out.println( line );
							line = br.readLine();
						}
						pin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.run();
			
			//File blastFile = x ? blastx : blastp; //dbType.equals("prot") ? type.equals("prot") ? blastp : blastx : blastn;
			
			String[] 		cmds = { OS.indexOf("mac") >= 0 ? "/usr/local/bin/blastp" : "blastp", "-query", "-", "-db", "tmp", "-evalue", evaluestr, "-num_threads", Integer.toString(procs) };
			lcmd = new ArrayList<String>( Arrays.asList(cmds) );
			//String[] exts = extrapar.trim().split("[\t ]+");
			
			ProcessBuilder pb = new ProcessBuilder( lcmd );
			pb.redirectErrorStream( true );
			final Process p = pb.start();
		
			final Thread t = new Thread() {
				public void run() {
					try {
						OutputStream pos = p.getOutputStream();
						pos.write( fasta.getBytes() );
						pos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			
			final Thread t2 = new Thread() {
				public void run() {
					try {
						System.err.println("WHY NOT");
						InputStreamReader rdr = new InputStreamReader( p.getInputStream() );
						//FileReader fr = new FileReader( new File("c:/dot.blastout") );
						
						String res = "";
						BufferedReader br = new BufferedReader( rdr );
						String line = br.readLine();
						while( line != null ) {
							//System.err.println( line );
							if( line.startsWith("> ") ) {
								int i = line.indexOf(' ', 2);
								if( i == -1 ) i = line.length();
								String id = line.substring(2, i);
								
								Gene g = geneset.genemap.get( id );
								if( g != null ) {
									if( table.getModel() == groupModel ) {
										i = geneset.allgenegroups.indexOf( g.getGeneGroup() );
										if( i != -1 && i < table.getRowCount() ) {
											int r = table.convertRowIndexToView( i );
											table.addRowSelectionInterval(r, r);
										}
									} else {
										i = geneset.genelist.indexOf( g );
										if( i != -1 && i < table.getRowCount() ) {
											int r = table.convertRowIndexToView( i );
											table.addRowSelectionInterval(r, r);
										}
									}
								}
								
								String stuff = line+"\n";
								line = br.readLine();
								while( line != null && !line.startsWith("Query=") && !line.startsWith("> ") ) {
									stuff += line+"\n";
									line = br.readLine();
								}
								if( rr != null ) {
									rr.run( stuff );
									//res += line+"\n";
								}
							} else line = br.readLine();
						}
						br.close();
						//System.err.println("wn done");
						p.destroy();
						
						if( rr != null ) rr.run("close");
						
						/*if( rr != null ) {
							rr.run( res );
						}*/
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t2.start();
			//fr.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public void doBlastn( final String fasta, final String evaluestr, final boolean ids, final RunnableResult rr, boolean show ) {
		/*File blastn;
		File blastp;
		File makeblastdb;
		File blastx = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastx.exe" );
		if( !blastx.exists() ) {
			blastx = new File( "/opt/ncbi-blast-2.2.29+/bin/blastx" );
			if( !blastx.exists() ) {
				blastx = new File( "/usr/local/ncbi/blast/bin/blastx" );
				blastn = new File( "/usr/local/ncbi/blast/bin/blastn" );
				blastp = new File( "/usr/local/ncbi/blast/bin/blastp" );
				
				makeblastdb = new File( "/usr/local/ncbi/blast/bin/makeblastdb" );
			} else {
				blastn = new File( "/opt/ncbi-blast-2.2.29+/bin/blastn" );
				blastp = new File( "/opt/ncbi-blast-2.2.29+/bin/blastp" );
				
				makeblastdb = new File( "/opt/ncbi-blast-2.2.29+/bin/makeblastdb" );
			}
		} else {
			blastn = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastn.exe" );
			blastp = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\blastp.exe" );
			
			makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.29+\\bin\\makeblastdb.exe" );
		}*/
		
		int procs = Runtime.getRuntime().availableProcessors();
		String[] mcmds = { "makeblastdb", "-dbtype", "nucl", "-title", "tmp", "-out", "tmp" };
		List<String>	lcmd = new ArrayList<String>( Arrays.asList(mcmds) );
		
		final ProcessBuilder mpb = new ProcessBuilder( lcmd );
		mpb.redirectErrorStream( true );
		try {
			final Process mp = mpb.start();
			
			new Thread() {
				public void run() {
					try {
						OutputStream pos = mp.getOutputStream();
						for( String cname : geneset.contigmap.keySet() ) {
							Sequence c = geneset.contigmap.get( cname );
							if( ids ) pos.write( (">" + c.id + "\n").getBytes() );
							else {
								pos.write( (">" + c.getName() + "\n").getBytes() );
							}
							StringBuilder sb = c.getStringBuilder();
							for( int i = 0; i < sb.length(); i+=70 ) {
								pos.write( sb.substring(i, Math.min( sb.length(), i+70) ).getBytes() );
							}
							pos.write( '\n' );
						}
						pos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			new Thread() {
				public void run() {
					try {
						InputStream pin = mp.getInputStream();
						InputStreamReader rdr = new InputStreamReader( pin );
						//FileReader fr = new FileReader( new File("c:/dot.blastout") );
						BufferedReader br = new BufferedReader( rdr );
						String line = br.readLine();
						while( line != null ) {
							System.out.println( line );
							line = br.readLine();
						}
						pin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.run();
			
			//File blastFile = blastn; //dbType.equals("prot") ? type.equals("prot") ? blastp : blastx : blastn;
			
			String[] 		cmds1 = { "blastn", "-dust", "no", "-perc_identity", "99", "-word_size", "21", "-query", "-", "-db", "tmp", "-evalue", evaluestr, "-num_threads", Integer.toString(procs) };
			String[] 		cmds2 = { "blastn", "-query", "-", "-db", "tmp", "-evalue", evaluestr, "-num_threads", Integer.toString(procs) };
			String[]		cmds = show ? cmds2 : cmds1;
			
			lcmd = new ArrayList<String>( Arrays.asList(cmds) );
			//String[] exts = extrapar.trim().split("[\t ]+");
			
			ProcessBuilder pb = new ProcessBuilder( lcmd );
			pb.redirectErrorStream( true );
			final Process p = pb.start();
		
			final Thread t = new Thread() {
				public void run() {
					try {
						OutputStream pos = p.getOutputStream();
						pos.write( fasta.getBytes() );
						pos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			
			Map<String,Set<String>>	tph = new HashMap<String,Set<String>>();
			Map<String,Map<String,String>>	tvp = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>>	tmr = new HashMap<String,Map<String,String>>();
			
			Map<String,Integer>		specindex = new LinkedHashMap<String,Integer>();
			Map<String,Integer>		phindex = new LinkedHashMap<String,Integer>();
			
			/*final Thread t2 = new Thread() {
				public void run() {*/
					try {
						System.err.println("WHY NOT");
						InputStreamReader rdr = new InputStreamReader( p.getInputStream() );
						//FileReader fr = new FileReader( new File("c:/dot.blastout") );
						
						String qspec = null;
						String query = null;
						String ctype = null;
						Annotation at  = new Annotation();
						int o = 0;
						StringBuilder res = new StringBuilder();
						BufferedReader br = new BufferedReader( rdr );
						String line = br.readLine();
						res.append( line+"\n" );
						while( line != null ) {
							if( line.startsWith("Query= ") ) {
								query = line.substring(7,line.length());
								int e = query.indexOf("CRISPR")-1;
								if( e > 0 ) {
									qspec = query.substring(0, e);
									qspec = Sequence.getSpec(qspec);
									String rest = query.substring(e+8);
									int ri = rest.lastIndexOf('-');
									if( ri != -1 ) ctype = rest.substring(ri+1);
								} else {
									System.err.println();
								}
								
								line = br.readLine();
								res.append( line+"\n" );
								while( !line.startsWith("Length") ) {
									line = br.readLine();
									res.append( line+"\n" );
								}
								o = Integer.parseInt( line.substring(7) );
							} else if( line.startsWith("> ") ) {
								String contname = line.substring(1).trim();
								//line = br.readLine();
								//res.append( line+"\n" );
								//int o = Integer.parseInt( line.substring(7) );
								
								Sequence cont = geneset.contigmap.get(contname);
								
								if( cont != null ) {
									int start = -1;
									int stop = 0;
									line = br.readLine();
									res.append( line+"\n" );
									String lastmatch = null;
									while( line != null && !line.startsWith(">") && !line.startsWith("Query=") /*&& !line.contains("Expect =")*/ ) {
										if( line.startsWith("Sbjct") ) {
											String[] split = line.split("[\t ]+");
											int k = Integer.parseInt( split[1] );
											int m = Integer.parseInt( split[3] );
											lastmatch = split[2];
											
											if( start == -1 ) start = k;
											stop = m;
										}
										line = br.readLine();
										res.append( line+"\n" );
									}
									
									if( start > stop ) {
										int tmp = start;
										start = stop;
										stop = tmp;
									}
									
									at.start = start;
									at.stop = stop;
									
									//if( stop - start < o*2 ) {
									List<Annotation> lann = cont.getAnnotations();
									if( lann != null ) {
										int k = Collections.binarySearch(lann, at);
										
										//System.err.println( "kkk  " + k + "   " + lann.size() );
										
										if( k < 0 ) k = -(k+1)-1;
											
										Annotation ann = lann.get(Math.max(0,k));
										
										boolean yes = true;
										if( ann.type != null && ann.type.contains("ummer") ) {
											yes = false;
										}
										
										int u = k-1;
										Annotation nann = null;
										if( u >= 0 && u < lann.size() ) nann = lann.get(u);
										
										u = k+1;
										Annotation rann = null;
										if( u >= 0 && u < lann.size() ) rann = lann.get(u);
										
										if( nann != null && nann.type != null && nann.type.contains("ummer") ) {
											yes = false;
										}
												
										if( rann != null && rann.type != null && rann.type.contains("ummer") ) {
											yes = false;
										}
										
										if( !yes ) {
											//System.err.println();
										}
										
										Gene g = ann.getGene();
										String desig = ann.designation;
										
										if( yes && g != null ) { //ann.stop > at.start && ann.start < at.stop ) {
											GeneGroup gg = g.getGeneGroup();
											if( desig != null && desig.contains("phage") ) {
												if( !phindex.containsKey( desig ) ) phindex.put(desig, phindex.size());
												
												Map<String,String> tvps;
												String specname = qspec;//Sequence.nameFix(qspec, true);
												if( !specindex.containsKey( specname ) ) specindex.put(specname, specindex.size());
												
												if( tvp.containsKey(specname) ) {
													tvps = tvp.get(specname);
												} else {
													tvps = new HashMap<String,String>();
													tvp.put(specname, tvps);
												}
												tvps.put( desig, ctype );
												
												String contspec = cont.getSpec();
												System.err.println( query + " asdf " + contspec + " " + lastmatch + "  " + at.start + " " + at.stop + "  " + ann.start + " " + ann.stop + " rann " + (rann != null ? rann.start + "  " + rann.stop : "") + " nann " + (nann != null ? nann.start + "  " + nann.stop : "") );
												if( qspec.equals(contspec) ) {
													if( tmr.containsKey(specname) ) {
														tvps = tmr.get(specname);
													} else {
														tvps = new HashMap<String,String>();
														tmr.put(specname, tvps);
													}
													tvps.put( desig, ctype );
												}
												
												/*if( specname.contains("brockianus_MAT_338") ) {
													System.err.println();
												}*/
											}
												
											if( table.getModel() == groupModel ) {
												int ggindex = geneset.allgenegroups.indexOf( gg );
												int i = table.convertRowIndexToView( ggindex );
												if( i != -1 ) table.addRowSelectionInterval(i, i);
											} else if( table.getModel() == defaultModel ) {
												int gindex = geneset.genelist.indexOf( g );
												int i = table.convertRowIndexToView( gindex );
												table.addRowSelectionInterval(i, i);
											}
										}
									
										
										/*for( Annotation ann : lann ) {
											if( ann.stop > start && ann.start < stop ) {
												Gene g = ann.getGene();
												if( g != null ) {
													if( table.getModel() == groupModel ) {
														GeneGroup gg = g.getGeneGroup();
														
														int ggindex = allgenegroups.indexOf( gg );
														int i = table.convertRowIndexToView( ggindex );
														table.addRowSelectionInterval(i, i);
													} else if( table.getModel() == defaultModel ) {
														int gindex = geneset.genelist.indexOf( g );
														int i = table.convertRowIndexToView( gindex );
														table.addRowSelectionInterval(i, i);
													}
												}
											}
										}*/
									}
									//}
									continue;
								}
							}
							
								/*int i = line.indexOf(' ', 2);
								if( i == -1 ) i = line.length();
								String id = line.substring(2, i);
								
								Gene g = genemap.get( id );
								if( g != null ) {
									if( table.getModel() == groupModel ) {
										i = allgenegroups.indexOf( g.getGeneGroup() );
										if( i != -1 && i < table.getRowCount() ) {
											int r = table.convertRowIndexToView( i );
											table.addRowSelectionInterval(r, r);
										}
									} else {
										i = geneset.genelist.indexOf( g );
										if( i != -1 && i < table.getRowCount() ) {
											int r = table.convertRowIndexToView( i );
											table.addRowSelectionInterval(r, r);
										}
									}
								}
								
								String stuff = line+"\n";
								line = br.readLine();
								while( line != null && !line.startsWith("Query=") && !line.startsWith("> ") ) {
									stuff += line+"\n";
									line = br.readLine();
								}
								if( rr != null ) {
									rr.run( stuff );
									//res += line+"\n";
								}
								} //else*/
							line = br.readLine();
							res.append( line+"\n" );
						}
						br.close();
						p.destroy();
						
						for( String specname : geneset.speccontigMap.keySet() ) {
							List<Sequence> lseq = geneset.speccontigMap.get(specname);
							for( Sequence seq : lseq ) {
								List<Annotation> lann = seq.getAnnotations();
								if( lann != null ) {
									for( Annotation a : lann ) {
										String desig = a.designation;
										if( desig != null && desig.contains("phage") && phindex.containsKey(desig) ) {
											if( !specindex.containsKey( specname ) ) specindex.put(specname, specindex.size());
											
											Set<String> tvps;
											if( tph.containsKey(specname) ) {
												tvps = tph.get(specname);
											} else {
												tvps = new HashSet<String>();
												tph.put(specname, tvps);
											}
											tvps.add( desig );
										}
									}
								}
							}
						}
						
						int k = 0;
						int u = 0;
						Workbook wb = new XSSFWorkbook();
						Sheet sh = wb.createSheet("Phage");
						Row rw = sh.createRow(u++);
						//res = new StringBuilder();
						for( String ph : phindex.keySet() ) {
							res.append("\t"+ph);
							rw.createCell(++k).setCellValue( ph );
						}
						res.append("\n");
						for( String rspec : specindex.keySet() ) {
							String spec = Sequence.nameFix(rspec, true);
							rw = sh.createRow(u++);
							k = 0;
							rw.createCell(k++).setCellValue(spec);
							
							Map<String,String> set = tvp.get(rspec);
							res.append(spec);
							if( set != null ) {
								for( String ph : phindex.keySet() ) {
									if( set.containsKey(ph) ) {
										String type = set.get(ph);
										if( type == null || type.length() == 0 ) type = "yes";
										res.append("\t"+type);
										rw.createCell(k).setCellValue(type);
									} else {
										res.append("\t");
									}
									
									k++;
								}
							}
							res.append("\n");
						}
						
						for( String ph : phindex.keySet() ) {
							res.append("\t"+ph);
						}
						res.append("\n");
						
						u++;
						for( String rspec : specindex.keySet() ) {
							String spec = Sequence.nameFix(rspec, true);
							
							rw = sh.createRow(u++);
							k = 0;
							rw.createCell(k++).setCellValue(spec);
							
							Map<String,String> set = tmr.get(rspec);
							res.append(spec);
							if( set != null ) {
								for( String ph : phindex.keySet() ) {
									if( set.containsKey(ph) ) {
										String type = set.get(ph);
										if( type == null || type.length() == 0 ) type = "yes";
										res.append("\t"+type);
										rw.createCell(k).setCellValue(type);
									}
									else res.append("\t");
									
									k++;
								}
							}
							res.append("\n");
						}
						
						u++;
						for( String rspec : specindex.keySet() ) {
							String spec = Sequence.nameFix(rspec, true);
							
							rw = sh.createRow(u++);
							k = 0;
							rw.createCell(k++).setCellValue(spec);
							
							Set<String> set = tph.get(rspec);
							Map<String,String> setvp = tvp.get(rspec);
							res.append(spec);
							if( set != null ) {
								for( String ph : phindex.keySet() ) {
									if( set.contains(ph) ) {
										if( setvp != null && setvp.containsKey(ph) ) {
											res.append("\tyes wspacer");
											rw.createCell(k).setCellValue("yes wspacer");
										} else {
											res.append("\tyes");
											rw.createCell(k).setCellValue("yes");
										}
									}
									else res.append("\t");
									
									k++;
								}
							}
							res.append("\n");
						}
						
						File 				file = new File("/Users/sigmar/phage.xlsx");
						FileOutputStream 	fos = new FileOutputStream(file);
						wb.write(fos);
						fos.close();
						
						Desktop.getDesktop().open(file);
						
						//if( !show ) {
							JFrame frame = new JFrame();
							frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
							frame.setSize(800, 600);
							
							JTextArea ta = new JTextArea();
							ta.setFont( new Font("monospaced", Font.PLAIN, 12) );
							ta.append( res.toString() );
							JScrollPane sp = new JScrollPane( ta );
							frame.add( sp );
							
							frame.setVisible( true );
							
							FileWriter fw = new FileWriter("/Users/sigmar/file.txt");
							fw.write( res.toString() );
							fw.close();
							
							if( rr != null ) rr.run("close");
						//}
						
						/*if( rr != null ) {
							rr.run( res );
						}*/
					} catch (IOException e) {
						e.printStackTrace();
					}
			/*	}
			};
			t2.start();*/
			//fr.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public void cogCalc( String filename, Set<Character> includedCogs, Map<String,Map<Character,Integer>> map, Set<String> selspec, boolean contigs ) throws IOException {		
		if( table.getModel() == groupModel ) {
			for( String spec : selspec ) {
				Map<Character,Integer>	submap = new HashMap<Character,Integer>();
				map.put(spec, submap);
				
				List<Sequence> sctg = geneset.speccontigMap.get(spec);
				for( Sequence c : sctg ) {
					if( c.getAnnotations() != null ) for( Annotation a : c.getAnnotations() ) {
						Cog cog = a.getGene().cog;
						if( cog == null ) {
							cog = geneset.cogmap.get(a.getGene().id);
						}
						if( cog != null && cog.symbol != null ) {
							int val = 0;
							if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
							submap.put(cog.symbol, val+1);
						}
					}
				}
			}
			
			/*for( int r = 0; r < table.getRowCount(); r++ ) {
				//int i = table.convertRowIndexToModel(r);
				//if( i >= 0 && i < allgenegroups.size() ) {
			for( int i = 0; i < allgenegroups.size(); i++ ) {
					GeneGroup gg = allgenegroups.get(i);
					Cog cog = gg.getCommonCog(cogmap);
					if( cog != null && includedCogs.contains(cog.symbol) ) {
						for( String spec : selspec ) {
							if( gg.species.containsKey( spec ) ) {
								Teginfo ti = gg.species.get( spec );
								for( Tegeval tv : ti.tset ) {
									Map<Character,Integer> submap;
									if( contigs ) {
										if( map.containsKey( tv.contloc ) ) {
											
										}
									} else {
										int val = 0;
										//if( map.containsKey( tv.getSpecies() ) ) {
										submap = map.get( tv.getSpecies() );
										if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
										/*} else {
											submap = new HashMap<Character,Integer>();
											map.put(spec, submap);
										}*
										submap.put( cog.symbol, val+1 );
									}
								}
							}
						}
					}
				//}
			}*/
		} else {
			
		}
		//cogCalc(filename, br, map, selspec, contigs);
	}
	
	public StringBuffer getSelectedASeqs( JTable table, List<Gene> genelist, JApplet applet, Collection<String> species ) throws IOException {
		StringWriter sb = new StringWriter();
		
		Set<String> selectedSpecies = getSelspec( applet, new ArrayList<String>( species ) );
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = geneset.genelist.get(cr);
			//if (gg.species != null) {
			sb.append(gg.name + ":\n");
				//for (String sp : gg.species.keySet()) {
				//for( String sp : selectedSpecies ) {
			if( selectedSpecies.contains(species) ) {
				Tegeval tv = gg.tegeval;
				//sb.append(">" + tv.name + " " + tv.teg + " " + tv.eval + "\n");
				Sequence protseq = tv.getProteinSequence();
				protseq.setName( tv.name + " " + tv.teg + " " + tv.eval );
				protseq.writeSequence(sb);
				/*for (int i = 0; i < tv.getProteinLength(); i += 70) {
					int end = Math.min(i + 70, tv.getProteinLength());
					sb.append( protseq.substring(i, end) + "\n"); // new
																		// String(
																		// tv.seq,
																		// i,
																		// Math.min(i+70,tv.seq.length())
																		// )+"\n");
				}*/
					// textarea.append( ">" + tv.cont + " " + tv.teg
					// + " " + tv.eval + "\n" + tv.seq + "\n" );
			}
		}
		return sb.getBuffer();
	}
	
	int[]		currentRowSelection;
	public TransferHandler dragRows( final JTable table, final List<String> specs ) {
		TransferHandler th = null;
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<String>	selseq = new ArrayList<String>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( specs.get(i) );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						for( int r = 0; r < table.getRowCount(); r++ ) {
							Object o = table.getValueAt(r, 0);
							if( o != null ) {
								ret += o.toString();
							} else {
								ret += "";
							}
							for( int c = 1; c < table.getColumnCount(); c++ ) {
								o = table.getValueAt(r, c);
								if( o != null ) {
									ret += "\t"+o.toString();
								} else {
									ret += "\t";
								}
							}
							ret += "\n";
						}
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.getBytes( charset ) );
					}
					//return ret;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df, ndf };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) || arg0.equals(ndf) ) {
						return true;
					}
					return false;
				}
			};
			
			th = new TransferHandler() {
				private static final long serialVersionUID = 1L;
				
				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}

				public boolean canImport(TransferHandler.TransferSupport support) {					
					return true;
				}

				protected Transferable createTransferable(JComponent c) {
					currentRowSelection = table.getSelectedRows();
					
					return transferable;
				}

				public boolean importData(TransferHandler.TransferSupport support) {
					try {
						System.err.println( table.getSelectedRows().length );
						
						DataFlavor[] dfs = support.getDataFlavors();
						if( support.isDataFlavorSupported( ndf ) ) {					
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<String>	seqs = (ArrayList<String>)obj;
							
							/*ArrayList<String> newlist = new ArrayList<String>( serifier.lgse.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( specs.get(i) );
							}
							serifier.lgseq.clear();
							serifier.lgseq = newlist;*/
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							specs.removeAll( seqs );
							for( String s : seqs ) {
								specs.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							
							return true;
						}/* else if( support.isDataFlavorSupported( df ) ) {							
							Object obj = support.getTransferable().getTransferData( df );
							InputStream is = (InputStream)obj;
							
							System.err.println( charset );
							importReader( new BufferedReader(new InputStreamReader(is, charset)) );
							
							updateView();
							
							return true;
						}  else if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {							
							Object obj = support.getTransferable().getTransferData( DataFlavor.stringFlavor );
							String str = (String)obj;
							importReader( new BufferedReader( new StringReader(str) ) );
							
							updateView();
							
							return true;
						}*/
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return th;
	}
	
	JRadioButtonMenuItem	gb;
	JRadioButtonMenuItem	ggb;
	
	JTable		table;
	JTable		ftable;
	TableModel	ftablemodel;
	TableModel	defaultModel;
	TableModel	groupModel;
	
	public void updateFilter(JTable table, RowFilter filter, JLabel label) {
		DefaultRowSorter<TableModel, Integer> rowsorter = (DefaultRowSorter<TableModel, Integer>) table.getRowSorter();
		rowsorter.setRowFilter(filter);
		if (label != null)
			label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
	}
	
	public BufferedImage gatest( String spec1 ) {
		GeneCompare gc = new GeneCompare(); //comparePlot( GeneSetHead.this, comp, genelist, clusterMap );
		final BufferedImage bimg = new BufferedImage( 1024, 1024, BufferedImage.TYPE_INT_RGB );
		final Graphics2D g2 = bimg.createGraphics();
		final Map<String,Integer>	blosumap = JavaFasta.getBlosumMap();
		
		if( spec1 != null && spec1.length() > 0 ) {
			List<Sequence> clist = geneset.speccontigMap.get(spec1);
			int total = 0;
			int ptotal = 0;
			for( Sequence ctg : clist ) {
				if( ctg.isPlasmid() ) ptotal += ctg.getAnnotationCount();
				else total += ctg.getAnnotationCount();
			}
		
			gc.draw(g2, spec1, GeneSetHead.this, 1024, 1024, clist, geneset.specList, blosumap, total, ptotal);
		} else {
			gc.draw(g2, null, GeneSetHead.this, 1024, 1024, null, geneset.specList, blosumap, table.getRowCount(), 0 );
		}
		g2.dispose();
		
		return bimg;
	}
	
	public void updateFilter(int val, String ustr, JTable table, RowFilter filter, Set<Integer> filterset, JLabel label, int ... ind ) {
		filterset.clear();
		TableModel model = table.getModel();
		for (int r = 0; r < model.getRowCount(); r++) {
			for( int i = 0; i < ind.length; i++ ) {
				String vstr = (String)model.getValueAt(r, ind[i]);
				String s = vstr != null ? vstr.toLowerCase() : null;
				
				if( (s != null && s.contains(ustr)) ) {
					filterset.add(r);
					break;
				}
			}
		}
		
		if( filterset.isEmpty() ) {
			int i = 0;
			if( table.getModel() == groupModel ) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					for( Gene g : gg.genes ) {
						if( g.refid.toLowerCase().contains(ustr) ) {
							filterset.add(i);
							break;
						}
					}
					i++;
				}
			} else {
				for( Gene g : geneset.genelist ) {
					if( g.refid.toLowerCase().contains(ustr) ) {
						filterset.add(i);
					}
					i++;
				}
			}
		}
		
		updateFilter(table, filter, label);
	}
	
	public void showAlignedSequences( Component comp, Serifier	serifier ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
		jf.initGui(frame);
		jf.updateView();

		frame.setVisible(true);
	}
	
	public void showSomeSequences( Component comp, Serifier serifier ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
		jf.initGui(frame);

		/*for( String contig : contset.keySet() ) {
			Sequence seq = contset.get(contig);
			serifier.addSequence(seq);
			if (seq.getAnnotations() != null)
				Collections.sort(seq.getAnnotations());
		}*/
		jf.getSerifier().checkMaxMin();
		jf.updateView();

		frame.setVisible(true);
	}
	
	public void showSelectedSequences( Component comp, Set<Annotation> tset, boolean dna, String names ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
		jf.initGui(frame);

		for( Annotation tv : tset ) {
			Sequence cont = tv.getContig();
			if( cont != null ) {
				//String contig = cont.getSpec();//tv.getContig();
				Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
				seq.setName( geneset.nameFix(tv.getSpecies()) );
				//Sequence seq = new Sequence( nameFix(tv.getSpecies())/*getGeneName(names, tv.getGene())*/, seqstr, serifier.mseq );
				serifier.addSequence(seq);
			}
		}

		/*for( String contig : contset.keySet() ) {
			Sequence seq = contset.get(contig);
			serifier.addSequence(seq);
			if (seq.getAnnotations() != null)
				Collections.sort(seq.getAnnotations());
		}*/
		jf.updateView();

		frame.setVisible(true);
	}
	
	public void showSequences( Component comp, Set<GeneGroup> ggset, boolean dna, Set<String> specs ) {
		showSequences(comp, ggset, dna, specs, false);
	}
	
	public void showSequences( Component comp, Set<GeneGroup> ggset, boolean dna, Set<String> specs, boolean genename ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
		jf.initGui(frame);

		for( GeneGroup ggroup : ggset ) {
		//for( Gene gg : ggroup.genes ) {
			//if (gg.species != null) {
				//for (String sp : gg.species.keySet()) {
				//	Teginfo stv = gg.species.get(sp);
			for( Tegeval tv : ggroup.getTegevals() ) {
				Sequence cont = tv.getContshort();
				if( cont != null ) {
					String selspec = cont.getSpec();//tv.getContig();
					if( genename ) {
						Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
						seq.setName( tv.gene != null ? tv.gene.name : tv.name );
						serifier.mseq.put( seq.getName(), seq );
						//Sequence seq = new Sequence( tv.gene != null ? tv.gene.name : tv.name, seqstr, serifier.mseq );
						serifier.addSequence(seq);
					} else if( specs == null || specs.contains(selspec ) ) {
						String spec = geneset.nameFix( selspec );
						
						Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
						seq.setName( spec );
						/*String name = tv.getGene().id;
						String cazy = tv.getGene().getGeneGroup().getCommonCazy(cazymap);
						String ec = tv.getGene().getGeneGroup().getCommonEc();
						if( cazy != null ) name += " " + cazy;
						if( ec != null ) name += " " + ec;*/
						//Sequence seq = new Sequence( spec, seqstr, serifier.mseq );
						serifier.addSequence(seq);
					}
				}
				//contset.put( contig, seq );
				
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

		/*for( String contig : contset.keySet() ) {
			Sequence seq = contset.get(contig);
			serifier.addSequence(seq);
			if (seq.getAnnotations() != null)
				Collections.sort(seq.getAnnotations());
		}*/
		jf.updateView();

		frame.setVisible(true);
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
		Sequence.colorCodes[0] = new Color(180, 255, 180);
		Sequence.colorCodes[1] = new Color(180, 245, 190);
		Sequence.colorCodes[2] = new Color(180, 235, 200);
		Sequence.colorCodes[3] = new Color(180, 225, 210);
		Sequence.colorCodes[4] = new Color(180, 215, 220);
		Sequence.colorCodes[5] = new Color(180, 205, 230);
		Sequence.colorCodes[6] = new Color(180, 195, 240);
		Sequence.colorCodes[7] = new Color(180, 185, 250);
		Sequence.colorCodes[8] = new Color(180, 180, 255);
	}
	
	TextField tb1 = null;
	TextField tb2 = null;
	TextField epar = null;
	final JFXPanel fxpanel = new JFXPanel();
	Scene fxs = null;
	
	BufferedImage bimg;
	
	JComboBox<String> selcomb;
	JComboBox<String> searchcolcomb;
	JComboBox<String> syncolorcomb;
	
	JComboBox<String> 						specombo;
	JComboBox<String> 						combo;

	Component comp;
	public void init(final Container comp, final SwingNode upper, final SwingNode lower, final MenuBar menubar) {
		geneset.user = System.getProperty("user.name");
		JavaFasta.user = geneset.user;
		//SerifyApplet.user = user;
		
		/*try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}*/
		
		String userhome = System.getProperty("user.home");
		boolean windows = false;
		try {
			InputStream isk = GeneSetHead.this.getClass().getResourceAsStream("genesetkey");
			//Path gkey = Paths.get( url.toURI() );
			InputStream iskp = GeneSetHead.this.getClass().getResourceAsStream("genesetkey.pub");
			//Path gkeypub = Paths.get( url.toURI() );
			
			Path gkeyssh = Paths.get(userhome);
			//Path gkeyssh = userpath.resolve(".ssh");
			if( !Files.exists(gkeyssh) ) Files.createDirectory(gkeyssh);
			Path gkeylocal = gkeyssh.resolve("genesetkey");
			Path gkeylocalpub = gkeyssh.resolve("genesetkey.pub");
			if( !Files.exists(gkeylocal) ) {
				Files.copy(isk, gkeylocal);
			}
			if( !Files.exists(gkeylocalpub) ) {
				Files.copy(iskp, gkeylocalpub);
			}
			
			Set<PosixFilePermission> poset = new HashSet<PosixFilePermission>();
			poset.add( PosixFilePermission.OWNER_READ );
			poset.add( PosixFilePermission.OWNER_WRITE );
			Files.setPosixFilePermissions(gkeylocal, poset);
			Files.setPosixFilePermissions(gkeylocalpub, poset);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch( UnsupportedOperationException e2 ) {
			windows = true;
			e2.printStackTrace();
		}
		
		if( windows ) {
			File f = new File( userhome+"\\genesetkey" );
			f.setExecutable(false, false);
			f.setWritable(false, false);
			f.setReadable(false, false);
			
			f.setWritable(true, true);
			f.setReadable(true, true);
		}
		
		this.comp = comp;
		selcomb = new JComboBox<String>();
		searchcolcomb = new JComboBox<String>();
		syncolorcomb = new JComboBox<String>();
		
		searchcolcomb.addItem("Name");
		searchcolcomb.addItem("Symbol");
		
		setColors();

		JMenuBar	menubar = new JMenuBar();
		JMenu		file = new JMenu("File");
		file.add( new AbstractAction("New") {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		file.add( new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					importStuff();
				} catch (IOException | UnavailableServiceException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.addSeparator();
		file.add( new AbstractAction("Import genomes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fetchGenomes();
			}
		});
		file.add( new AbstractAction("Export genomes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportGenomes( geneset.speccontigMap );
			}
		});
		file.addSeparator();
		file.add( new AbstractAction("Export protein sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportProteinSequences( geneset.genelist );
			}
		});
		file.add( new AbstractAction("Export gene clusters") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportGeneClusters( geneset.allgenegroups );
			}
		});
		file.addSeparator();
		file.add( new AbstractAction("Quit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit( 0 );
			}
		});
		JMenu		edit = new JMenu("Edit");
		AbstractAction	clustergenes = new AbstractAction("Cluster genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//fxpanel.setScene( null );
				/*Platform.runLater(new Runnable() {
		            @Override
		            public void run() {
		            	Label label1 = new Label("Id:");
						tb1 = new TextField("0.5");
						Label label2 = new Label("Len:");
						tb2 = new TextField("0.5");
						
						VBox vbox = new VBox();
						HBox hbox1 = new HBox();
						hbox1.getChildren().addAll( label1, tb1 );
						HBox hbox2 = new HBox();
						hbox2.getChildren().addAll( label2, tb2 );
						
						epar = new TextField();
						vbox.getChildren().add( epar );
						
						vbox.getChildren().addAll( hbox1, hbox2 );
						if( fxs == null ) fxs = new Scene( vbox );
						fxs.setRoot( vbox );
						
						fxpanel.setScene( fxs );
		            }
				});*/
				
				JPanel panel = new JPanel();
				GridBagLayout grid = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				panel.setLayout( grid );
				
				/*JLabel label1 = new JLabel("Id:");
				JTextField tb1 = new JTextField("0.5");
				JLabel label2 = new JLabel("Len:");
				JTextField tb2 = new JTextField("0.5");
				
				Dimension d = new Dimension( 300, 30 );
				JTextField epar = new JTextField();
				epar.setSize( d );
				epar.setPreferredSize( d );
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridheight = 1;
				
				c.gridx = 0;
				c.gridy = 0;
				panel.add( label1, c );
				c.gridx = 1;
				c.gridy = 0;
				panel.add( tb1, c );
				c.gridx = 0;
				c.gridy = 1;
				panel.add( label2, c );
				c.gridx = 1;
				c.gridy = 1;
				panel.add( tb2, c );
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 2;
				panel.add( epar, c );
				
				JOptionPane.showMessageDialog(comp, new Object[] {panel}, "Clustering parameters", JOptionPane.PLAIN_MESSAGE );*/
				
				/*if( tb1 != null ) {
					float id = Float.parseFloat( tb1.getText() );
					float len = Float.parseFloat( tb2.getText() );
					String expar = epar.getText();
					
					tb1 = null;
					tb2 = null;
					epar = null;*/
					
					Set<String> species = getSelspec(null, geneset.getSpecies(), null);
					geneset.clusterGenes( species, false );
				//}
			}
		};
		AbstractAction	alignclusters = new AbstractAction("Align clusters") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String OS = System.getProperty("os.name").toLowerCase();
					
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					//s.makeBlastCluster(zipfilesystem.getPath("/"), p, 1);
					Path aldir = geneset.zipfilesystem.getPath("aligned");
					final Path aligneddir = Files.exists( aldir ) ? aldir : Files.createDirectory( aldir );
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try {
								geneset.zipfilesystem.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					
					NativeRun nrun = new NativeRun( run );
					//ExecutorService es = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
					
					Object[] cont = new Object[3];
					
					Collection<GeneGroup> ggset;
					int[] rr = table.getSelectedRows();
					ggset = new HashSet<GeneGroup>();
					if( rr.length == 0 ) {
						for( GeneGroup gg : geneset.allgenegroups ) {
							//GeneGroup gg = allgenegroups.get(table.convertRowIndexToModel(r));
							//gg.getCommonTag()
							if( gg != null && gg.getCommonTag() == null && gg.size() > 1 ) ggset.add( gg );
						}
					} else {
						for( int r : rr ) {
							GeneGroup gg = geneset.allgenegroups.get(table.convertRowIndexToModel(r));
							//gg.getCommonTag()
							if( gg != null && gg.getCommonTag() == null && gg.size() > 1 ) ggset.add( gg );
						}
					}
					
					//int i = 0;
					List commandsList = new ArrayList();
					for( GeneGroup gg : ggset ) {
						String fasta = gg.getFasta( true );
						String[] cmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/bin/mafft" : "/usr/bin/mafft", "-"};
						Object[] paths = new Object[] {fasta.getBytes(), aligneddir.resolve(gg.getCommonId()+".aa"), null};
						commandsList.add( paths );
						commandsList.add( Arrays.asList(cmds) );
						
						//if( i++ > 5000 ) break;
					}
					nrun.runProcessBuilder("Running mafft", commandsList, cont, true, run, false);
				} catch (IOException e1) {
					if( geneset.zipfilesystem != null ) {
						try {
							geneset.zipfilesystem.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					e1.printStackTrace();
				}
			}
		};
		AbstractAction	sharenumaction = new AbstractAction("Update share numbers") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> specs = getSelspec(GeneSetHead.this, geneset.specList, null);
				geneset.updateShareNum(specs);
			}
		};
		AbstractAction	importgeneclusteringaction = new AbstractAction("Import gene clustering") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				GridBagLayout grid = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				panel.setLayout( grid );
				
				JLabel label1 = new JLabel("Id:");
				JTextField tb1 = new JTextField("0.5");
				JLabel label2 = new JLabel("Len:");
				JTextField tb2 = new JTextField("0.5");
				
				Dimension d = new Dimension( 300, 30 );
				JTextField epar = new JTextField();
				epar.setSize( d );
				epar.setPreferredSize( d );
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridheight = 1;
				
				c.gridx = 0;
				c.gridy = 0;
				panel.add( label1, c );
				c.gridx = 1;
				c.gridy = 0;
				panel.add( tb1, c );
				c.gridx = 0;
				c.gridy = 1;
				panel.add( label2, c );
				c.gridx = 1;
				c.gridy = 1;
				panel.add( tb2, c );
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 2;
				panel.add( epar, c );
				
				JOptionPane.showMessageDialog(comp, new Object[] {panel}, "Clustering parameters", JOptionPane.PLAIN_MESSAGE );
				
				float id = Float.parseFloat( tb1.getText() );
				float len = Float.parseFloat( tb2.getText() );
				
				//JFileChooser fc = new JFileChooser();
				//if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
				Serifier s = new Serifier();
				//s.mseq = aas;
				for( String gk : geneset.refmap.keySet() ) {
					Gene g = geneset.refmap.get( gk );
					if( g.tegeval.getAlignedSequence() != null ) System.err.println( g.tegeval.getAlignedSequence().getName() );
					s.mseq.put( gk, g.tegeval.getAlignedSequence() );
				}
				
				Map<String,String>	idspec = new HashMap<String,String>();
				for( String idstr : geneset.refmap.keySet() ) {
					if( idstr.contains(" ") ) {
						System.err.println( "coooonnnnnni " + idstr );
					}
					
					Gene gene = geneset.refmap.get( idstr );
					idspec.put(idstr, gene.getSpecies());
				}
				//Sequences seqs = new Sequences(user, name, type, path, nseq)
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					Path root = geneset.zipfilesystem.getPath("/");
					Path p = geneset.zipfilesystem.getPath("cluster.blastout"); //root.resolve("culster.blastout");
					
					List<Set<String>> cluster = geneset.uclusterlist == null ? new ArrayList<Set<String>>() : new ArrayList<Set<String>>( geneset.uclusterlist );
					/*for( Set<String> specs : clusterMap.keySet() ) {
						Set<Map<String,Set<String>>> uset = clusterMap.get( specs );
						for( Map<String,Set<String>> umap : uset ) {
							for( String val : umap.keySet() ) {
								Set<String> sset = umap.get(val);
								
								/*Set<String> ss = new HashSet<String>();
								for( String str : sset ) {
									int k = str.indexOf(' ');
									if( k == -1 ) k = str.length();
									ss.add( str.substring(0,k) );
									/*if( str.contains(" ") ) {
										System.err.println( "coooonnnnnni2 " + str );
									}*
								}*
								cluster.add( sset );
							}
						}
					}*/
					
					//p = null;
					s.makeBlastCluster(root, p, 1, id, len, idspec, cluster, geneset.refmap);
					
					System.err.println( cluster.get(0) );
					if( geneset.uclusterlist != null ) System.err.println( geneset.uclusterlist.get(0) );
					
					geneset.zipfilesystem.close();
				} catch (IOException e1) {
					if( geneset.zipfilesystem != null ) {
						try {
							geneset.zipfilesystem.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					e1.printStackTrace();
				}
			}
		};
		AbstractAction	importgenesymbolaction = new AbstractAction("Import gene symbols") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						//Path path = zipfile.toPath();
						String uristr = "jar:" + geneset.zippath.toUri();
						geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						
						Path nf = geneset.zipfilesystem.getPath("/smap_short.txt");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						geneset.uni2symbol(new InputStreamReader(is), bw, geneset.unimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						geneset.zipfilesystem.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	importcazyaction = new AbstractAction("Import Cazy") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						BufferedReader rd = Files.newBufferedReader(fc.getSelectedFile().toPath());
						geneset.loadcazymap(geneset.cazymap, rd);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	functionmappingaction = new AbstractAction("Function mapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JTextField 	tf = new JTextField();
				JButton				btn = new JButton("File");
				JComponent 			comp = new JComponent() {};
				comp.setLayout( new BorderLayout() );
				comp.add( tf );
				comp.add(btn, BorderLayout.EAST);
				tf.setText("http://130.208.252.239/data/sp2go.txt.gz");
				
				final File[] file = new File[1];
				btn.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
							file[0] = fc.getSelectedFile();
							try {
								tf.setText( fc.getSelectedFile().toURI().toURL().toString() );
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					Path nf = geneset.zipfilesystem.getPath("/sp2go_short.txt");
					BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
					
					JOptionPane.showMessageDialog(GeneSetHead.this, comp);
					
					final JDialog	dialog = new JDialog();
					dialog.setTitle( "Function mapping" );
					dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					dialog.setSize(400, 300);
					
					comp = new JComponent() {};
					comp.setLayout( new BorderLayout() );
					
					final JTextArea		ta = new JTextArea();
					/*final InputStream fis;
					if( file[0] != null ) fis = new FileInputStream( file[0] );
					else {
						JTextField host = new JTextField("localhost");
						JOptionPane.showMessageDialog(null, host);
						
						String username = System.getProperty("user.name");
						String hostname = host.getText();
						
						List<String> commandsList = Arrays.asList( new String[] {"ssh",username+"@"+hostname,"cat",tf.getText()} );
						ProcessBuilder pb = new ProcessBuilder( commandsList );
						Process p = pb.start();
						
						for( Object commands : commandsList ) {
							if( commands instanceof List ) {
								for( Object cmd : (List)commands ) {
									ta.append(cmd+" ");
								}
								ta.append("\n");
							} else {
								ta.append(commands+" ");
							}
						}
						ta.append("\n");
						
						fis = p.getInputStream();
					}*/
					
					final JProgressBar	pbar = new JProgressBar();
					final Thread t = new Thread() {
						public void run() {
							try {
								URL url = new URL(tf.getText());
								InputStream fis = url.openStream();
								
								BufferedReader br = new BufferedReader( new InputStreamReader( new GZIPInputStream( fis ) ) );
								//if( unimap != null ) unimap.clear();
								//unimap = idMapping(new InputStreamReader(is), bw, 2, 0, refmap, genmap, gimap);
								geneset.funcMappingUni( br, geneset.unimap, bw );
								
								fis.close();
								bw.close();
								
								try { geneset.zipfilesystem.close(); } catch( Exception e2 ) { e2.printStackTrace(); };
								
								pbar.setIndeterminate(false);
								pbar.setEnabled(false);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					
					ta.setEditable( false );
					final JScrollPane	sp = new JScrollPane( ta );
					
					dialog.add( comp );
					comp.add( pbar, BorderLayout.NORTH );
					comp.add( sp, BorderLayout.CENTER );
					pbar.setIndeterminate( true );
					
					t.start();
					dialog.addWindowListener( new WindowListener() {
						
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
							if( pbar.isEnabled() ) {
								pbar.setIndeterminate( false );
								pbar.setEnabled( false );
							}
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );
					//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		AbstractAction	importidmappingaction = new AbstractAction("Import idmapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JTextField 	tf = new JTextField();
				JButton				btn = new JButton("File");
				JComponent 			comp = new JComponent() {};
				comp.setLayout( new BorderLayout() );
				comp.add( tf );
				comp.add(btn, BorderLayout.EAST);
				tf.setText("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping.dat.gz");
				
				final File[] file = new File[1];
				btn.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
							file[0] = fc.getSelectedFile();
							try {
								tf.setText( fc.getSelectedFile().toURI().toURL().toString() );
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				
				JOptionPane.showMessageDialog(GeneSetHead.this, comp);
				
				//Thread t = new Thread() {
				//	public void run() {
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					Path nf = geneset.zipfilesystem.getPath("/idmapping_short.dat");
					final BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
					
					final JDialog	dialog = new JDialog();
					dialog.setTitle( "Id mapping" );
					dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					dialog.setSize(400, 300);
					
					comp = new JComponent() {};
					comp.setLayout( new BorderLayout() );
					
					final JTextArea		ta = new JTextArea();
					
					/*final InputStream fis;
					if( file[0] != null ) fis = new FileInputStream( file[0] );
					else {
						/*Object[] cont = new Object[3];
						Runnable run = new Runnable() {
							public void run() {
								try {
									bw.close();
									geneset.zipfilesystem.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};*
						
						JTextField host = new JTextField("localhost");
						JOptionPane.showMessageDialog(null, host);
						
						String username = System.getProperty("user.name");
						String hostname = host.getText();
						
						List<String> commandsList = Arrays.asList( new String[] {"ssh",username+"@"+hostname,"cat",tf.getText()} );
						ProcessBuilder pb = new ProcessBuilder( commandsList );
						Process p = pb.start();
						
						for( Object commands : commandsList ) {
							if( commands instanceof List ) {
								for( Object cmd : (List)commands ) {
									ta.append(cmd+" ");
								}
								ta.append("\n");
							} else {
								ta.append(commands+" ");
							}
						}
						ta.append("\n");
						
						fis = p.getInputStream();
					}*/
					
					final JProgressBar	pbar = new JProgressBar();
					final Thread t = new Thread() {
						public void run() {
							try {
								URL url = new URL(tf.getText());
								InputStream fis = url.openStream();
								InputStream is = new GZIPInputStream( fis );
								if( geneset.unimap != null ) geneset.unimap.clear();
								geneset.unimap = geneset.idMapping(new InputStreamReader(is), bw, 2, 0, geneset.refmap, geneset.genmap, geneset.gimap);
								is.close();
								fis.close();
								bw.close();
								
								try { geneset.zipfilesystem.close(); } catch(Exception ep) { ep.printStackTrace(); };
								
								pbar.setIndeterminate(false);
								pbar.setEnabled(false);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					
					ta.setEditable( false );
					final JScrollPane	sp = new JScrollPane( ta );
					
					dialog.add( comp );
					comp.add( pbar, BorderLayout.NORTH );
					comp.add( sp, BorderLayout.CENTER );
					pbar.setIndeterminate( true );
					
					t.start();
						
						/*System.err.println( "about to run" );
						for( Object commands : commandsList ) {
							if( commands instanceof List ) {
								for( Object c : (List)commands ) {
									System.err.print( c+" " );
								}
								System.err.println();
							} else {
								System.err.print( commands+" " );
							}
						}
						System.err.println();*/
					dialog.addWindowListener( new WindowListener() {
						
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
							if( pbar.isEnabled() ) {
								/*String result = ta.getText().trim();
								if( run != null ) {
									cont[0] = null;
									cont[1] = result;
									cont[2] = new Date( System.currentTimeMillis() ).toString();
									run.run();
								}*/
								
								pbar.setIndeterminate( false );
								pbar.setEnabled( false );
							}
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );
						
					/*NativeRun nrun = new NativeRun();
					nrun.runProcessBuilder("Idmapping", Arrays.asList( tf.getText().split(" ") ), run, cont, false);
					ProcessBuilder pb = new ProcessBuilder( tf.getText().split(" ") );
					Process p = pb.start();
					fis = p.getInputStream();
					}*/
					
					//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
				} catch (IOException e1) {
					e1.printStackTrace();
					try { geneset.zipfilesystem.close(); } catch( Exception e2 ) { e2.printStackTrace(); };
				}
				//	}
				//};
				//t.start();
				//}
			}
		};
		AbstractAction	cogblastaction = new AbstractAction("Cog blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Set<String> species = getSelspec(null, geneset.getSpecies(), null);
					
					StringWriter sb = new StringWriter();
					String dbPath = "/data/Cog";
					for( Gene g : geneset.genelist ) {
						if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
							if( species.contains( g.getSpecies() ) ) {
								Sequence gs = g.tegeval.getProteinSequence();
								if( gs != null ) {
									gs.setName( g.id );
									gs.writeSequence( sb );
								}
								/*sb.append(">" + g.id + "\n");
								for (int i = 0; i < gs.length(); i += 70) {
									sb.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
								}*/
							}
						}
					}
					
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					Path resPath = geneset.zipfilesystem.getPath("/cog.blastout");
					
					NativeRun nrun = new NativeRun();
					SerifyApplet.rpsBlastRun(nrun, sb.getBuffer(), dbPath, resPath, "", null, true, geneset.zipfilesystem, geneset.user);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		AbstractAction	unresolvedblastaction = new AbstractAction("Unresolved blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Set<String> species = getSelspec(null, geneset.getSpecies(), null);
					
					StringWriter sb = new StringWriter();
					Path dbPath = Paths.get("/data/nr");
					int[] rr = table.getSelectedRows();
					if( rr != null && rr.length > 0 ) {
						if( table.getModel() == defaultModel ) {
							for( int r : rr ) {
								int i = table.convertRowIndexToModel(r);
								Gene g = geneset.genelist.get(i);
								Sequence gs = g.tegeval.getProteinSequence();
								gs.setName( g.id );
								gs.writeSequence( sb );
							}
						} else {
							for( int r : rr ) {
								int i = table.convertRowIndexToModel(r);
								GeneGroup gg = geneset.allgenegroups.get(i);
								Gene g = null;
								for( Gene gene : gg.genes ) {
									g = gene;
									break;
								}
								Sequence gs = g.tegeval.getProteinSequence();
								gs.setName( g.id );
								gs.writeSequence( sb );
							}
						}
					} else {
						for( Gene g : geneset.genelist ) {
							if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
								if( species.contains( g.getSpecies() ) ) {
									Sequence gs = g.tegeval.getProteinSequence();
									gs.setName( g.id );
									gs.writeSequence( sb );
									
									/*sb.append(">" + g.id + "\n");
									for (int i = 0; i < gs.length(); i += 70) {
										sb.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
									}*/
								}
							}
						}
					}
					
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					Path resPath = geneset.zipfilesystem.getPath("/unresolved.blastout");
					
					NativeRun nrun = new NativeRun();
					SerifyApplet.blastpRun(nrun, sb.getBuffer(), dbPath, resPath, "-evalue 0.00001", null, true, geneset.zipfilesystem, geneset.user);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		
		AbstractAction	importkeggpathwayaction = new AbstractAction("Import kegg pathways") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> keggids = new HashSet<String>();
				for( Gene g : geneset.genelist ) {
					if( g.keggid != null ) {
						int i = g.keggid.indexOf(':');
						if( i > 0 ) {
							keggids.add( g.keggid.substring(0, i) );
						}
					}
				}
				System.err.println( keggids );
				
				JTextField tf = new JTextField("http://130.208.252.239/organisms/");
				JOptionPane.showMessageDialog(null, tf);
				
				Map<String,String> env = new HashMap<String,String>();
				env.put("create", "true");
				
				Path rootp = null;
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
				} catch( Exception ee ) { ee.printStackTrace(); }
				
				for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
					rootp = root;
					break;
				}
				
				
				for( String kegg : keggids ) {
					try {
						URL url = new URL( tf.getText() + kegg + ".tar.gz" );
						InputStream is = url.openStream();
						GZIPInputStream gz = new GZIPInputStream(is);
					
						TarArchiveInputStream tar = new TarArchiveInputStream(gz);
						TarArchiveEntry tae = (TarArchiveEntry)tar.getNextEntry();
						while( tae != null ) {
							geneset.traverseTar( tar, tae, rootp );
							
							tae = (TarArchiveEntry)tar.getNextEntry();
						}
						
						is.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				try { geneset.zipfilesystem.close(); } catch( Exception ee ) { ee.printStackTrace(); };
			}
		};
		
		edit.add( clustergenes );
		edit.add( alignclusters );
		edit.addSeparator();
		edit.add( sharenumaction );
		edit.add( importgeneclusteringaction );
		edit.add( importgenesymbolaction );
		edit.add( importcazyaction );
		edit.add( functionmappingaction );
		edit.add( importidmappingaction );
		edit.add( importkeggpathwayaction );
		edit.addSeparator();
		edit.add( cogblastaction );
		edit.add( unresolvedblastaction );
		
		JMenu		view = new JMenu("View");
		gb = new JRadioButtonMenuItem( new AbstractAction("Genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( defaultModel );
			}
		});
		view.add( gb );
		ggb = new JRadioButtonMenuItem( new AbstractAction("Gene groups") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( groupModel );
			}
		});
		ButtonGroup	bg = new ButtonGroup();
		bg.add( gb );
		bg.add( ggb );
		ggb.setSelected( true );
		view.add( ggb );
		ActionCollection.addAll( view, geneset.clusterMap, GeneSetHead.this, geneset.speccontigMap, table, comp, geneset.cs );
		
		JMenu		help = new JMenu("Help");
		help.add( new AbstractAction("About") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog( comp, "CompGen 1.0" );
			}
		});
		help.add( new AbstractAction("Test") {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*for( Gene g : geneset.genelist ) {
					Sequence seq = g.tegeval.getContig();
					if( seq == null ) {
						System.err.println();
					}
				}*/
				
				for( String spec : geneset.speccontigMap.keySet() ) {
					if( spec.contains("RAST") ) {
						List<Sequence> lseq = geneset.speccontigMap.get(spec);
						for( Sequence seq : lseq ) {
							for( Annotation a : seq.getAnnotations() ) {
								System.err.println( a.getGene().getGeneGroup().species );
								/*Sequence tseq = a.getContig();
								if( tseq == null ) {
									System.err.println();
								}*/
							}
						}
					}
				}
				
				/*for( GeneGroup gg : allgenegroups ) {
					if( gg.species.size() > 1 ) {
						System.err.println( gg.species );
					}
				}*/
			}
		});
		help.addSeparator();
		help.add( new AbstractAction("Run server") {

			@Override
			public void actionPerformed(ActionEvent e) {
				JSpinner	spin = new JSpinner();
				JOptionPane.showMessageDialog(GeneSetHead.this, spin, "Port", JOptionPane.QUESTION_MESSAGE);
				try {
					geneset.cs = WSServer.startServer(GeneSetHead.this, (Integer)spin.getValue());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		help.addSeparator();
		
		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem("Use geneset user");
		help.add( cbmi );
		
		cbmi.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( cbmi.isSelected() ) {
					geneset.user = "geneset";
				} else geneset.user = System.getProperty("user.name");
				
				JavaFasta.user = geneset.user;
				if( geneset.currentSerify != null ) geneset.currentSerify.user = geneset.user;
			}
		});
		
		help.addSeparator();
		help.add( new AbstractAction("Help & Tutorial") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse( new URI("http://thermusgenes.appspot.com/pancore.html") );
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JMenu		sequencemenu = new JMenu("Sequence");
		sequencemenu.add( new AbstractAction("Show group sequences") {
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
				if( geneset.currentSerify == null ) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);
					
					Map<String,String> env = new HashMap<String,String>();
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					try {
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
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
							
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					
					SerifyApplet sa = new SerifyApplet( geneset.zipfilesystem );
					sa.init( frame, geneset.user );
					//frame.add( )
					geneset.currentSerify = sa;
				}/* else frame = (JFrame)currentSerify.cnt;*/
				
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
					Gene gg = geneset.genelist.get(cr);
					if (gg.getSpecies() != null) {
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
							//Teginfo stv = gg.species.equals(sp) ? gg.teginfo : null;
							
							if( gg.getSpecies().equals(sp) ) tlist.add( gg.tegeval );
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
							/*if( stv == null ) {
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
									}*
								}
							}*/
						}
						//if( count < gg.species.size() ) {
						//	System.err.println( gg.species );
						//	System.err.println();
						//}
						//if( specs.size() < 28 ) System.err.println("mu " + specs);
					}
				}
				
				try {
					StringWriter sb = new StringWriter();
					for( int gi : ups.keySet() ) {
						String name = ups.get(gi);
						List<Tegeval>	tlist = ups2.get(gi);
						
						sb.append(name.replace('/', '-') + ":\n");
						if( tlist.size() < 28 ) {
							for( Tegeval tv : tlist ) {
								System.err.println( tv.name );
							}
							System.err.println();
						}
						for( Tegeval tv : tlist ) {
							Sequence ps = tv.getProteinSequence();
							ps.setName( tv.name.substring(0, tv.name.indexOf('_')) );
							ps.writeSequence(sb);
							/*sb.append(">" + tv.name.substring(0, tv.name.indexOf('_')) + "\n");
							for (int i = 0; i < ps.length(); i += 70) {
								sb.append( ps.substring(i, Math.min(i + 70, tv.getProteinLength() )) + "\n");
							}*/
						}
					}
				
					geneset.currentSerify.addSequences("uh", new StringReader( sb.toString() ), Paths.get("/"), null);
				} catch (URISyntaxException | IOException e1) {
					e1.printStackTrace();
				}
				frame.setVisible(true);
			}
		});
		sequencemenu.add( new AbstractAction("Show group DNA sequences") {
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
					Gene gg = geneset.genelist.get(cr);
					if (gg.getSpecies() != null) {
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
						tlist.add( gg.tegeval );
							/*textarea.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
							if (tv.dna != null) {
								for (int i = 0; i < tv.dna.length(); i += 70) {
									textarea.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
								}
							}*/
					}
				}
				
				for( int gi : ups.keySet() ) {
					String name = ups.get(gi);
					List<Tegeval>	tlist = ups2.get(gi);
					
					textarea.append(name.replace('/', '-') + ":\n");
					for( Tegeval tv : tlist ) {
						textarea.append(">" + tv.name.substring(0, tv.name.indexOf('_')) + "\n");
						for (int i = 0; i < tv.getLength(); i += 70) {
							textarea.append(tv.getSubstring(i, Math.min(i + 70, tv.getLength())) + "\n");
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
		sequencemenu.addSeparator();
		sequencemenu.add(new AbstractAction("Show all sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
				jf.initGui(frame);

				Map<String, Sequence> contset = new HashMap<String, Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = geneset.genelist.get(cr);
					Tegeval tv = gg.tegeval;
					String contig = tv.getContshort().getName();
					Sequence seq = tv.getProteinSequence();
					seq.setName(contig);
					serifier.mseq.put( seq.getName(), seq );
					//Sequence seq = new Sequence( contig, aa, serifier.mseq );
					serifier.addSequence(seq);
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		sequencemenu.add(new AbstractAction("Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<GeneGroup>	genegroups = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				if( table.getModel() == groupModel ) {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = geneset.genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				Set<String>	specs = null;
				if( rr.length > 1 ) specs = getSelspec(comp, geneset.specList, null);
				showSequences( comp, genegroups, false, specs );
			}
		});
		sequencemenu.add(new AbstractAction("Show sequences w/genenames") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<GeneGroup>	genegroups = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				if( table.getModel() == groupModel ) {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = geneset.genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				//Set<String>	specs = null;
				//if( rr.length > 1 ) specs = getSelspec(comp, specList, null);
				showSequences( comp, genegroups, false, null, true );
			}
		});
		sequencemenu.add(new AbstractAction("Show aligned sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<GeneGroup>	genegroups = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				if( table.getModel() == groupModel ) {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = geneset.genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				
				Serifier	serifier = new Serifier();
				for( GeneGroup ggroup : genegroups ) {
					for( Tegeval tv : ggroup.getTegevals() ) {
						String selspec = tv.getContshort().getSpec();//tv.getContig();
						String spec = geneset.nameFix( selspec );
						/*if( selspec.contains("hermus") ) spec = selspec;
						else {
							Matcher m = Pattern.compile("\\d").matcher(selspec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) spec = "Thermus_" + selspec;
							else spec = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
						}*/
						
						Sequence seq = tv.getAlignedSequence();
						//System.err.println( "seqlen " + seq.length() );
						if( seq != null ) {
							seq.setName( spec );
							//Sequence seq = new Sequence( contig, seqstr, null );
							serifier.addSequence(seq);
						} else {
							Sequence sb = tv.getProteinSequence();
							sb.setName(spec);
							//Sequence sseq = new Sequence( spec, sb, serifier.mseq );
							serifier.addSequence( sb );
						}
					}
				}
				showAlignedSequences( comp, serifier );
			}
		});
		sequencemenu.add(new AbstractAction("Split/Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					StringBuffer sb = getSelectedASeqs( table, geneset.genelist, GeneSetHead.this, geneset.specList );
					if( geneset.currentSerify == null ) {
						JFrame frame = new JFrame();
						frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
						frame.setSize(800, 600);
						
						SerifyApplet sa = new SerifyApplet( geneset.zipfilesystem );
						sa.init( frame, geneset.user );
						geneset.currentSerify = sa;
						
						frame.setVisible( true );
					}
					geneset.currentSerify.addSequences("uh", new StringReader( sb.toString() ), Paths.get("/"), null);
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
		sequencemenu.add( new AbstractAction("Show DNA sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<GeneGroup>	genegroups = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				if( table.getModel() == groupModel ) {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = geneset.genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				Set<String> specs = null;
				if( rr.length > 1 ) specs = getSelspec( comp, geneset.specList, null );
				showSequences( comp, genegroups, true, specs );
				
				/*StringBuilder sb = getSelectedSeqs( table, genelist );
				
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
				frame.setVisible(true);*/
			}
		});
		sequencemenu.add(new AbstractAction("Export all DNA sequences") {
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
							Gene gg = geneset.genelist.get(cr);
							FileWriter fw = null;
							if (lfw.containsKey(gg.getGroupIndex())) {
								fw = lfw.get(gg.getGroupIndex());
							} else {
								fw = new FileWriter(new File(f, "group_" + gg.getGroupIndex() + ".fasta"));
								lfw.put(gg.getGroupIndex(), fw);
							}

							Tegeval tv = gg.tegeval;
							fw.append(">" + tv.name + " " + tv.teg + " " + tv.eval + "\n");
							for (int i = 0; i < tv.getLength(); i += 70) {
								fw.append( tv.getSubstring(i, Math.min(i + 70, tv.getLength()) ) + "\n");
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
		sequencemenu.add(new AbstractAction("Export relevant contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				try {
					Map<Integer, FileWriter> lfw = new HashMap<Integer, FileWriter>();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();

						Set<Sequence> contset = new HashSet<Sequence>();
						int[] rr = table.getSelectedRows();
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							Gene gg = geneset.genelist.get(cr);
							Tegeval tv = gg.tegeval;
							contset.add( tv.getContshort() );
						}

						FileWriter fw = new FileWriter(f);
						for (Sequence contig : contset) {
							fw.append(">" + contig + "\n");
							if (geneset.contigmap.containsKey(contig)) {
								StringBuilder dna = geneset.contigmap.get(contig).getStringBuilder();
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
		sequencemenu.addSeparator();
		sequencemenu.add(new AbstractAction("View selected range") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
				jf.initGui(frame);

				Set<Sequence> contset = new HashSet<Sequence>();
				Set<Tegeval> tvset = new HashSet<Tegeval>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene gg = geneset.genelist.get(cr);
						Tegeval tv = gg.tegeval;
						tvset.add( tv );
						//serifier.addAnnotation( tv );
						contset.add( tv.getContshort() );
					} else {
						GeneGroup gg = geneset.allgenegroups.get(cr);
						for( Tegeval tv : gg.getTegevals() ) {
							tv.color = Color.red;
							tvset.add( tv );
							Sequence contig = tv.getContshort();
							contset.add( contig );							
							//serifier.addAnnotation( tv );
						}
					}
					/*Sequence seq;
					Sequence contig = tv.getContshort();
					/*if (contset.containsKey(contig)) {
						seq = contset.get(contig);
					} else {
						if( contigmap.containsKey(contig) ) {
							StringBuilder dna = contigmap.get(contig).getStringBuilder();
							seq = new Sequence(contig.getName(), dna, serifier.mseq);
						} else
							seq = new Sequence(contig.getName(), serifier.mseq);
						contset.put(contig, seq);
					}

					Annotation a = new Annotation(contig, contig.getName(), Color.green, serifier.mann);
					a.setStart(tv.start);
					a.setStop(tv.stop);
					a.setOri(tv.ori);
					a.setGroup(gg.name);
					a.setType("gene");*/
					// seq.addAnnotation( new Annotation( seq, ) );
				}

				for (Sequence contig : contset) {
					int start = Integer.MAX_VALUE;
					int stop = Integer.MIN_VALUE;
					for( Tegeval tv : tvset ) {
						if( contig == tv.seq ) {
							start = Math.min(start, tv.start);
							stop = Math.max(stop, tv.stop);
						}
					}
					
					int rstart = 0;
					int rstop = contig.length();
					if( contig.annset != null ) for( Annotation tv : contig.annset ) {
						if( contig == tv.seq ) {
							if( tv.stop < start && tv.stop > rstart ) {
								rstart = tv.stop;
							}
							if( tv.start > stop && tv.start < rstop ) {
								rstop = tv.start;
							}
						}
					}
					
					start = rstart;
					stop = rstop;
					
					Sequence newseq = new Sequence( contig.getName(), new StringBuilder(contig.getSubstring(start, stop, 1)), serifier.mseq);
					/*if( contig.isReverse() ) {
						newseq.reverse();
						newseq.complement();
					}*/
					
					serifier.addSequence( newseq );
					for( Tegeval tv : tvset ) {
						Annotation newann = new Annotation(newseq, tv.start-start, tv.stop-start, tv.ori, tv.name);
						if( contig == tv.seq ) {
							newseq.addAnnotation( newann );
						}
						serifier.addAnnotation(newann);
					}
					/*for( Annotation ann : contig.getAnnotations() ) {
						serifier.addAnnotation( ann );
					}*/
					/*if (seq.getAnnotations() != null)
						Collections.sort(seq.getAnnotations());*/
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		sequencemenu.add(new AbstractAction("View whole contigs for selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
				jf.initGui(frame);

				//Map<Sequence, Sequence> contset = new HashMap<Sequence, Sequence>();
				/*int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = geneset.genelist.get(cr);
					if (gg.species != null) {
						for (String sp : gg.species.keySet()) {
							Teginfo stv = gg.species.get(sp);
							for (Tegeval tv : stv.tset) {
								Sequence seq;
								Sequence contig = tv.getContshort();
								if (contset.containsKey(contig)) {
									seq = contset.get(contig);
								} else {
									if( GeneSet.contigmap.containsKey(contig) ) {
										//StringBuilder dna = GeneSet.contigmap.get(contig).seq;
										StringBuilder dna = contig.getSequence().getStringBuilder();
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
								 *
								// seq.addAnnotation( new Annotation( seq, ) );
							}
						}
					}
				}*/

				Set<Sequence> contigs = new HashSet<Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene g = geneset.genelist.get( cr );
						Tegeval tv = g.tegeval;
						tv.color = Color.red;
						Sequence contig = tv.getContshort();
						//contig.offset = -tv.start;
						contigs.add( contig );
						
						
						/*Annotation a = new Annotation(contig, contig.getName(), Color.red, serifier.mann);
						a.setStart(tv.start);
						a.setStop(tv.stop);
						a.setOri(tv.ori);
						a.setGroup(g.name);
						a.setType("gene");*/
						//serifier.addAnnotation( tv );
					} else {
						GeneGroup gg = geneset.allgenegroups.get( cr );
						for( Tegeval tv : gg.getTegevals() ) {
							tv.color = Color.red;
							Sequence contig = tv.getContshort();
							//contig.offset = -tv.start;
							contigs.add( contig );					
							
							
							/*Annotation a = new Annotation(contig, contig.getName(), Color.red, serifier.mann);
							a.setStart(tv.start);
							a.setStop(tv.stop);
							a.setOri(tv.ori);
							a.setGroup(gg.getCommonName());
							a.setType("gene");*/
							//serifier.addAnnotation( tv );
						}
						
					}
					//Gene gg = geneset.genelist.get(cr);
					//for (Gene g : geneset.genelist) {
					//if (g.species != null) {
						//for (String sp : g.species.keySet()) {
				}

				for( Sequence contig : contigs ) {
					for( Annotation ann : contig.getAnnotations() ) {
						serifier.addAnnotation( ann );
					}
					
					serifier.addSequence( contig );
					serifier.mseq.put( contig.getName(), contig );
					//if(contig.getAnnotations() != null)
					//	Collections.sort(contig.getAnnotations());
				}
				jf.updateView();

				frame.setVisible(true);
			}
		});
		sequencemenu.addSeparator();
		sequencemenu.add( new AbstractAction("View species sequence") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> selspec = getSelspec(GeneSetHead.this, geneset.specList);
				
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
				jf.initGui(frame);
				
				for( String spec : selspec ) {
					List<Sequence> contigs = geneset.speccontigMap.get( spec );

					for( Sequence contig : contigs ) {
						List<Annotation> lann = contig.getAnnotations();
						if( lann != null ) for( Annotation ann : lann ) {
							serifier.addAnnotation( ann );
						}
						
						serifier.addSequence( contig );
						serifier.mseq.put( contig.getName(), contig );
					}
				}
				
				jf.updateView();

				frame.setVisible(true);
			}
		});
		
		JMenu		windowmenu = new JMenu("Tools");
		windowmenu.add( new AbstractAction("Sequence viewer") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, geneset.cs );
				jf.initGui(frame);
				jf.updateView();

				frame.setVisible(true);
			}
		});
		windowmenu.addSeparator();
		windowmenu.add( new AbstractAction("Gene sorter") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//if( gb.isSelected() ) new GeneSorter().mynd( GeneSetHead.this, genelist, table, null, contigmap );
					//else 
					new GeneSorter().groupMynd( GeneSetHead.this, geneset.allgenegroups, geneset.specList, geneset.genelist, table, geneset.contigmap, geneset.specset );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		AbstractAction specorderaction = new AbstractAction("Order species list") {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeUtil tu = new TreeUtil();
				/*corrInd.clear();
				for( String spec : specList ) {
					corrInd.add( nameFix( spec ) );
				}*/
				
				Serifier serifier = getConcatenatedSequences( false, false );
				
				Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
				double[] dmat = new double[ serifier.lseq.size()*serifier.lseq.size() ];
				Sequence.distanceMatrixNumeric( serifier.lseq, dmat, null, false, false, null, blosumap );
				
				List<String>	ret = new ArrayList<String>();
				for( Sequence seqname : serifier.lseq ) {
					ret.add( seqname.getName() ); //.replace(' ', '_') );
				}
				//List<String>	corrInd = currentjavafasta.getNames();
				
				//Sequence.distanceMatrixNumeric(serifier.lseq, dmat, idxs, bootstrap, cantor, ent, blosum);
				Node n = tu.neighborJoin(dmat, ret, null, false, false);
				
				Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						int c1 = o1.countLeaves();
						int c2 = o2.countLeaves();
						
						if( c1 > c2 ) return 1;
						else if( c1 == c2 ) return 0;
						
						return -1;
					}
				};
				tu.arrange( n, comp );
				//corrInd.clear();
				List<String> ordInd = n.traverse();
				
				for( String spec : ordInd ) {
					System.err.println( spec );
				}
				
				for( String oldspec : geneset.specList ) {
					if( !ordInd.contains( oldspec ) ) {
						ordInd.add( oldspec );
					}
				}
				geneset.specList = ordInd;
				
				TableModel model = table.getModel();
				table.setModel( nullmodel );
				table.setModel( model );
				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				//table.getColumnModel().
				System.err.println( geneset.specList.size() );
			}
		};
		AbstractAction matrixaction = new AbstractAction("Relation matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>	descombo = new JComboBox<String>( geneset.deset.toArray( new String[geneset.deset.size()] ) );
				JCheckBox			anicheck = new JCheckBox("ANImatrix");
				descombo.insertItemAt("", 0);
				descombo.setSelectedIndex( 0 );
				JOptionPane.showMessageDialog( GeneSetHead.this, new Object[] { descombo, anicheck } );
				String val = descombo.getSelectedItem().toString();
				
				Collection<GeneGroup> ss = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					ss.add( geneset.allgenegroups.get( table.convertRowIndexToModel(r) ) );
				}
				if( ss.isEmpty() ) ss = geneset.allgenegroups;
				
				Set<String> species = getSelspec( GeneSetHead.this, geneset.specList );
				bimg = anicheck.isSelected() ? geneset.animatrix( species, geneset.clusterMap, val, ss ) : geneset.bmatrix( species, geneset.clusterMap, val );
				
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
							for (String spc : geneset.specList) {
								if (++i == geneset.specList.size())
									ret.append(spc + "\n");
								else
									ret.append(spc + "\t");
							}

							int where = 0;
							for (String spc1 : geneset.specList) {
								int wherex = 0;
								for (String spc2 : geneset.specList) {
									int spc1tot = 0;
									int spc2tot = 0;
									int totot = 0;

									int spc1totwocore = 0;
									int spc2totwocore = 0;
									int tototwocore = 0;
									for (Set<String> set : geneset.clusterMap.keySet()) {
										Set<Map<String, Set<String>>> erm = geneset.clusterMap.get(set);
										if (set.contains(spc1)) {
											if (set.size() < geneset.specList.size()) {
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
										if (where == geneset.specList.size() - 1)
											ret.append(0 + "\n");
										else
											ret.append(0 + "\t");
									} else {
										double hlut = (double) spc2totwocore / (double) spc1totwocore;
										double sval = hlut; // 1.0/( 1.1-hlut );
										double val = Math.pow(50.0, sval - 0.3) - 1.0;
										double dval = Math.round(100.0 * (val)) / 100.0;

										if (wherex == geneset.specList.size() - 1)
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
						        	if( jfc.showSaveDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
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
		AbstractAction tniaction = new AbstractAction("TNI/ANI") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> species = getSelspec( GeneSetHead.this, geneset.specList );
				String makeblastdb = "makeblastdb";
				String OS = System.getProperty("os.name").toLowerCase();
				if( OS.indexOf("mac") != -1 ) makeblastdb = "/usr/local/bin/makeblastdb";
				for( String spec : species ) {
					List<Sequence> lseq = geneset.speccontigMap.get(spec);
					ProcessBuilder pb = new ProcessBuilder(makeblastdb,"-dbtype","nucl","-title",spec,"-out",spec);
					File dir = new File( System.getProperty("user.home") );
					
					
					
					
					/*try {
						FileWriter w = new FileWriter( new File(dir, spec+".fna") );
						for( Sequence seq : lseq ) {
							seq.writeSequence(w);
						}
						w.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}*/
					
					
					
					
					pb.directory( dir );
					try {
						Process p = pb.start();
						Writer fw = new OutputStreamWriter( p.getOutputStream() );
						for( Sequence seq : lseq ) {
							seq.writeSequence(fw);
						}
						fw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				int y = 0;
				double[] matrix = new double[ species.size()*species.size() ];
				for( String dbspec : species ) {
					int x = 0;
					for( String spec : species ) {
						//if( !spec.equals(dbspec) ) {
							final List<Sequence> lseq = geneset.speccontigMap.get(spec);
							String blastn = "blastn";
							if( OS.indexOf("mac") != -1 ) blastn = "/usr/local/bin/blastn";
							ProcessBuilder pb = new ProcessBuilder(blastn,"-db",dbspec,
									"-num_threads",Integer.toString(Runtime.getRuntime().availableProcessors()),
									"-num_alignments","1","-num_descriptions","1"); //,"-max_hsps","1");
							File dir = new File( System.getProperty("user.home") );
							pb.directory( dir );
							try {
								Process p = pb.start();
								final BufferedWriter fw = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );
								Thread t = new Thread() {
									public void run() {
										try {
											for( Sequence seq : lseq ) {
												seq.writeSplitSequence(fw);
												//seq.writeSequence(fw);
											}
											fw.close();
										} catch (IOException e1) {
											e1.printStackTrace();
										}
									}
								};
								t.start();
								//Path path = Paths.get("/Users/sigmar/"+spec+"_"+dbspec+".blastout");
								//Files.copy(p.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
								
								int tnum = 0;
								int tdenum = 0;
								double avg = 0.0;
								int count = 0;
								
								BufferedReader br = new BufferedReader( new InputStreamReader(p.getInputStream()) );
								String line = br.readLine();
								while( line != null ) {
									if( line.startsWith(" Identities") ) {
										int i = line.indexOf('(');
										String sub = line.substring(14,i-1);
										String[] split = sub.split("/");
										int num = Integer.parseInt(split[0]);
										int denum = Integer.parseInt(split[1]);
										
										avg += (double)num/(double)denum;
										
										tnum += num;
										tdenum += denum;
										count++;
									}
									line = br.readLine();
								}
								br.close();
								
								if( count > 0 ) avg /= count;
								double val = (double)tnum/(double)tdenum;
								matrix[y*species.size()+x] = avg;//val;
								System.err.println( spec + " on " + dbspec + " " + val );
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						//}
							x++;
					}
					y++;
				}
				
				geneset.corrInd.clear();
				for( String spec : species ) {
					geneset.corrInd.add( geneset.nameFix( spec ) );
				}
				
				final BufferedImage bi = geneset.showRelation( geneset.corrInd, matrix, false );
				JFrame f = new JFrame("TNI matrix");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500, 500);
				
				JComponent comp = new JComponent() {
					public void paintComponent( Graphics g ) {
						super.paintComponent(g);
						g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), this);
					}
				};
				Dimension dim = new Dimension(bi.getWidth(),bi.getHeight());
				comp.setPreferredSize(dim);
				comp.setSize( dim );
				JScrollPane scroll = new JScrollPane(comp);
				f.add(scroll);
				
				f.setVisible( true );
			}
		};
		AbstractAction anitreeaction = new AbstractAction("ANI tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> species = getSelspec( GeneSetHead.this, geneset.specList );
				List<String> speclist = new ArrayList<String>( species );
				
				Collection<GeneGroup> allgg = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					allgg.add( geneset.allgenegroups.get( table.convertRowIndexToModel(r) ) );
				}
				if( allgg.isEmpty() ) allgg = geneset.allgenegroups;
				
				Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
				
				double[] corrarr = new double[ speclist.size()*speclist.size() ];
				int where = 0;
				for (String spec1 : speclist) {
					int wherex = 0;
					
					String spc1 = geneset.nameFix( spec1 );
					
					//String spc1 = nameFix( spec1 );
					for (String spec2 : speclist) {
						if( where != wherex ) {
							int totalscore = 0;
							int totaltscore = 1;
							for( GeneGroup gg : allgg ) {
								if( /*gg.getSpecies().size() > 40 &&*/ gg.getSpecies().contains(spec1) && gg.getSpecies().contains(spec2) ) {
									Teginfo ti1 = gg.species.get(spec1);
									Teginfo ti2 = gg.species.get(spec2);
									//if( ti1.tset.size() == 1 && ti2.tset.size() == 1 ) {
										//double bval = 0.0;
									
									int score = 0;
									int tscore = 1;
									for( Tegeval tv1 : ti1.tset ) {
										for( Tegeval tv2 : ti2.tset ) {
											Sequence seq1 = tv1.getAlignedSequence();
											Sequence seq2 = tv2.getAlignedSequence();
											if( seq1 != null && seq2 != null ) {
												int mest = 0;
												int tmest = 0;
												
												int startcheck = 0;
												int start = -1;
												int stopcheck = 0;
												int stop = -1;
												for( int i = 0; i < seq1.length(); i++ ) {
													if( seq1.getCharAt(i) != '-' ) {
														startcheck |= 1;
													}
													if( seq2.getCharAt(i) != '-' ) {
														startcheck |= 2;
													}
													
													if( start == -1 && startcheck == 3 ) {
														start = i;
														break;
													}
												}
												
												for( int i = seq1.length()-1; i >= 0; i-- ) {
													if( seq1.getCharAt(i) != '-' ) {
														stopcheck |= 1;
													}
													if( seq2.getCharAt(i) != '-' ) {
														stopcheck |= 2;
													}
													
													if( stop == -1 && stopcheck == 3 ) {
														stop = i+1;
														break;
													}
												}
												//count += stop-start;
												
										        for( int i = start; i < stop; i++ ) {
										        	char lc = seq1.getCharAt(i);
										        	char c = Character.toUpperCase( lc );
										        	//if( )
										        	String comb = c+""+c;
										        	if( blosumap.containsKey(comb) ) tmest += blosumap.get(comb);
										        }
										        
										        for( int i = start; i < stop; i++ ) {
										        	char lc = seq1.getCharAt( i );
										        	char c = Character.toUpperCase( lc );
										        	char lc2 = seq2.getCharAt( i );
										        	char c2 = Character.toUpperCase( lc2 );
										        	
										        	String comb = c+""+c2;
										        	if( blosumap.containsKey(comb) ) mest += blosumap.get(comb);
										        }
										        
										        double tani = (double)mest/(double)tmest;
										        if( tani > (double)score/(double)tscore ) {
										        	score = mest;
										        	tscore = tmest;
										        }
										        //ret = (double)score/(double)tscore; //int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
												//return ret;
											}
											//if( where == 0 ) d1.add( gg.getCommonName() );
											//else d2.add( gg.getCommonName() );
										}
									}
									totalscore += score;
									totaltscore += tscore;
										
										/*if( bval > 0 ) {
											ani += bval;
											count++;
										}*/
									//}
								}
							}
							double ani = (double)(totaltscore-totalscore)/(double)totaltscore;
							corrarr[ where*speclist.size()+wherex ] = ani;
						}
						wherex++;
					}
					where++;
				}
				TreeUtil tu = new TreeUtil();
				geneset.corrInd.clear();
				for( String spec : speclist ) {
					geneset.corrInd.add( geneset.nameFix( spec ) );
				}
				Node n = tu.neighborJoin(corrarr, geneset.corrInd, null, false, false);
				System.err.println( n );
			
			}
		};
		windowmenu.add( specorderaction );
		windowmenu.add( matrixaction );
		windowmenu.add( tniaction );
		windowmenu.add( anitreeaction );
		windowmenu.add( new AbstractAction("Neighbourhood") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Set<GeneGroup>	genset = new HashSet<GeneGroup>();
					int[] rr = table.getSelectedRows();
					if( table.getModel() == groupModel ) {
						for( int rowIndex : rr ) {
							int r = table.convertRowIndexToModel( rowIndex );
							GeneGroup gg = geneset.allgenegroups.get( r );
							genset.add( gg );
						}
					} else {
						for( int rowIndex : rr ) {
							int r = table.convertRowIndexToModel( rowIndex );
							Gene gene = geneset.genelist.get( r );
							genset.add( gene.getGeneGroup() );
						}
					}
					new Neighbour( genset ).neighbourMynd( GeneSetHead.this, comp, geneset.genelist, geneset.contigmap );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		windowmenu.add( new AbstractAction("Synteny") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Set<String> species = speciesFromCluster( clusterMap );
				new Synteni().syntenyMynd( GeneSetHead.this, comp, geneset.genelist );
			}
		});
		AbstractAction compareplotaction = new AbstractAction("Gene atlas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new GeneCompare().comparePlot( GeneSetHead.this, comp, geneset.genelist, geneset.clusterMap, 4096, 4096 );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				/*gatest("MAT4726");
				
				final JFrame frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				
				final JComponent c = new JComponent() {
					public void paintComponent( Graphics g ) {
						g.drawImage(bimg, 0, 0, frame);
					}
				};
				c.setPreferredSize( new Dimension(bimg.getWidth(), bimg.getHeight()) );
				JScrollPane	scrollpane = new JScrollPane( c );
				frame.add( scrollpane );
				frame.setVisible( true );*/
			}
		};
		windowmenu.add( compareplotaction );
		
		AbstractAction syntenygradientaction = new AbstractAction("Synteny gradient") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> presel = new HashSet<String>();
				if( table.getModel() == defaultModel ) {
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel(r);
						presel.addAll( geneset.genelist.get(i).getGeneGroup().getSpecies() );
					}
				} else {
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel(r);
						presel.addAll( geneset.allgenegroups.get(i).getSpecies() );
					}
				}
				new SyntGrad().syntGrad( GeneSetHead.this, 2048, 2048, presel );
			}
		};
		windowmenu.add( syntenygradientaction );
		
		AbstractAction genexyplotaction = new AbstractAction("Gene XY plot") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new XYPlot().xyPlot( GeneSetHead.this, comp, geneset.genelist, geneset.clusterMap );
			}
		};
		windowmenu.add( genexyplotaction );
		
		AbstractAction refalignaction = new AbstractAction("Reference align") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JTable 				table = getGeneTable();
				final Collection<String> 	specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
				final List<String>			species = new ArrayList<String>( specset );
				
				TableModel model = new TableModel() {
					@Override
					public int getRowCount() {
						return species.size();
					}

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public String getColumnName(int columnIndex) {
						return null;
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
						return geneset.nameFix( species.get( rowIndex ) );
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				JTable table1 = new JTable( model );
				JTable table2 = new JTable( model );
				
				table1.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
				table2.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				
				JScrollPane	scroll1 = new JScrollPane( table1 );
				JScrollPane	scroll2 = new JScrollPane( table2 );
				
				FlowLayout flowlayout = new FlowLayout();
				JComponent c = new JComponent() {};
				c.setLayout( flowlayout );
				
				c.add( scroll1 );
				c.add( scroll2 );
				
				JOptionPane.showMessageDialog(comp, c);
				
				int r = table1.getSelectedRow();
				int i = table1.convertRowIndexToModel(r);
				String spec = i == -1 ? null : species.get( i );
				List<Sequence> lcont = geneset.speccontigMap.get(spec);
				
				r = table2.getSelectedRow();
				i = table2.convertRowIndexToModel(r);
				String refspec = i == -1 ? null : species.get( i );
				List<Sequence> lrefcont = geneset.speccontigMap.get(spec);
				
				/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Writer fw = new OutputStreamWriter( baos );
				try {
					List<Sequence> lcont = geneset.speccontigMap.get(spec);
					for( Sequence seq : lcont ) {
						seq.writeSequence(fw);
					}
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				String comp = spec;
				byte[] bb = baos.toByteArray();*/
				
				FlxReader flx = new FlxReader();
				
				Map<String,String> env = new HashMap<String,String>();
				env.put("create", "true");
				//String uristr = "jar:" + geneset.zippath.toUri();
				//URI geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
				
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
						Path subf = root.resolve(spec+".grp");
						if( Files.exists(subf) ) {
							BufferedReader br = Files.newBufferedReader(subf);
							Map<String,Map<String,String>> mm = flx.loadContigGraph(br);
							br.close();
							
							String home = System.getProperty("user.home")+"/";
							StringBuilder sb = comp != null ? flx.referenceAssembly( home, spec, refspec, lrefcont, lcont ) : null;
							Sequence cseq = new Sequence(spec+"_chromosome", null);
							if( sb != null && sb.length() > 0 ) {
								br = new BufferedReader(new StringReader( sb.toString() ));
							} else {
								Path sca = root.resolve(spec+".csc");
								if( !Files.exists(sca) ) {
									sca = root.resolve(spec+".sca");
								}
								br = Files.newBufferedReader(sca);
							}
							//br = new BufferedReader( fr );
							
							flx.connectContigs(br, cseq, false, new FileWriter(home+spec+"_new.fna"), spec);
							br.close();
						}
						
						break;
					}
				} catch( Exception ex ) {
					ex.printStackTrace();
				} finally {
					try{ geneset.zipfilesystem.close(); } catch( IOException ie ) { ie.printStackTrace(); };
				}
				
				//flx.start( f.getParentFile().getAbsolutePath()+"/", f.getName(), false, fw, comp, bb);
			}
		};
		windowmenu.add( refalignaction );
		
		windowmenu.addSeparator();
		windowmenu.add( new AbstractAction("Run antismash") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Serifier ser = new Serifier();
					Set<String> selspec = getSelspec(null, geneset.getSpecies(), null);
					
					JTextField host = new JTextField("localhost");
					JOptionPane.showMessageDialog(null, host);
					
					String username = System.getProperty("user.name");
					String hostname = host.getText();
					
					/*Path[] pt = null;
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
						pt = new Path[3];
						pt[2] = fc.getSelectedFile().toPath();
					}*/
					
					List<Object> commands = new ArrayList<Object>();
					//commands.add(genexyplotaction)
					
					for( String spec : selspec ) {
						String userhome = System.getProperty("user.home");
						Path pp = Paths.get( userhome );
						Path p = pp.resolve(spec+".gbk");
						//BufferedWriter fw = Files.newBufferedWriter( p );
						List<Sequence> clist = geneset.speccontigMap.get( spec );
						
						Map<String,List<Annotation>> mapan = new HashMap<String,List<Annotation>>();
						Serifier serifier = new Serifier();
						for( Sequence c : clist ) {
							serifier.addSequence(c);
							serifier.mseq.put(c.getName(), c);
							
							List<Annotation> lann = new ArrayList<Annotation>();
							if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
								Tegeval tv = (Tegeval)ann;
								
								Gene g = tv.getGene();
								GeneGroup gg = g.getGeneGroup();
								String name = g.getName();
								if( gg != null && name.contains(spec) ) {
									name = gg.getCommonName();
								}
								Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, name );
								anno.id = tv.getGene().getId();
								anno.type = "CDS";
								
								String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
								if( cazy != null ) anno.addDbRef( "CAZY:"+cazy );
								lann.add( anno );
							}
							mapan.put( c.getName(), lann );
						}
						Sequences s = new Sequences(null,spec,"nucl",null,clist.size());
						//serifier.addSequences(seqs);
						serifier.writeGenebank( p, false, true, s, mapan);
						
						//fw.close();
						
						String apath = p.toAbsolutePath().toString();
						if( hostname.equals("localhost") ) {
							String[] cmds = {"run_antismash", apath};
							//commands.add( pt );
							commands.add( Arrays.asList( cmds ) );
						} else {
							String aname = p.getFileName().toString();
							String adir = aname.substring(0, aname.length()-4);
							String cyghome = NativeRun.cygPath( userhome );
							String[] cmds = {"scp",apath,hostname+":~",";","ssh",hostname,"run_antismash",aname,";","scp","-r",hostname+":~/"+adir,cyghome};//userhome+"~"};
							//commands.add( pt );
							commands.add( Arrays.asList( cmds ) );
						}
					}
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							for( String spec : selspec ) {
								Path p = Paths.get(userhome, spec);
								
								Map<String,String> env = new HashMap<String,String>();
								env.put("create", "true");
								
								String uristr = "jar:" + geneset.zippath.toUri();
								URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
								final List<Path>	lbi = new ArrayList<Path>();
								try {
									geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
									for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
										Path specdir = root;
										Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
											@Override
										      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
										        final Path destFile = Paths.get(specdir.toString(),file.toString());
										        //System.out.printf("Extracting file %s to %s\n", file, destFile);
										        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
										        return FileVisitResult.CONTINUE;
										      }
										 
										      @Override
										      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
										    	String specdirstr = specdir.toString();
										    	String dirstr = dir.toString();
										    	final Path dirToCreate = specdir.resolve( dirstr.substring(userhome.length()+1) );
										        if( Files.notExists(dirToCreate) ) {
										          System.out.printf("Creating directory %s\n", dirToCreate);
										          Files.createDirectory(dirToCreate);
										        }
										        return FileVisitResult.CONTINUE;
										      }
										});
										break;
									}
									
									URI uri = new URI("file://"+userhome+"/"+spec+"/index.html");
									Desktop.getDesktop().browse(uri);
								} catch( Exception ex ) {
									ex.printStackTrace();
								} finally {
									try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
								}
							}
						}
					};
					
					NativeRun nr = new NativeRun( run );
					nr.runProcessBuilder("antismash", commands, new Object[3], false, run, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		windowmenu.add( new AbstractAction("Run signalP") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Serifier ser = new Serifier();
					Set<String> selspec = getSelspec(null, geneset.getSpecies(), null);
					
					JTextField host = new JTextField("localhost");
					JOptionPane.showMessageDialog(null, host);
					
					String username = System.getProperty("user.name");
					String hostname = host.getText();
					
					/*Path[] pt = null;
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
						pt = new Path[3];
						pt[2] = fc.getSelectedFile().toPath();
					}*/
					
					List<Object> commands = new ArrayList<Object>();
					//commands.add(genexyplotaction)
					
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						
						String uristr = "jar:" + geneset.zippath.toUri();
						URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
							for( String spec : selspec ) {
								/*Path specdir = root.resolve(spec+".prodigal.fsa");
								if( !Files.exists(specdir) ) {
									if( spec.startsWith("MAT") ) {
										specdir = root.resolve(spec+".gbk.aa");
									} else specdir = root.resolve("fn_"+spec+"_scaffolds.prodigal.fsa");
								}*/
								Stream<Gene> genestream = geneset.genelist.stream().filter( gene -> spec.equals(gene.getSpecies()) && (gene.tegeval.type == null || gene.tegeval.type.length() == 0) );
								Path sigout = root.resolve(spec+".signalp");
								Path[] pt = new Path[] {null,sigout,null};
								if( hostname.equals("localhost") ) {
									String[] cmds = {"signalp", "-t", "gram-", "-"};
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								} else {
									Path p = Paths.get(spec+".signalp");
									BufferedWriter bw = Files.newBufferedWriter(p, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
									genestream.forEachOrdered( gene -> {
										try {
											gene.writeGeneIdFasta(bw);
										} catch (Exception e1) {
											e1.printStackTrace();
										}
									});
									bw.close();
									
									
									//Files.copy(specdir, p, StandardCopyOption.REPLACE_EXISTING);
									
									String[] cmds = {"scp",spec+".signalp",hostname+":~",";","ssh",hostname,"signalp","-t","gram-",spec+".signalp"};
									//String[] cmds = {"ssh",hostname,"signalp","-t","gram-","-"};
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								}
							}
							
							break;
						}
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
						}
					};
					
					NativeRun nr = new NativeRun( run );
					nr.runProcessBuilder("signalp", commands, new Object[3], false, run, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		windowmenu.add( new AbstractAction("Run TransM") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Serifier ser = new Serifier();
					Set<String> selspec = getSelspec(null, geneset.getSpecies(), null);
					
					JTextField host = new JTextField("localhost");
					JOptionPane.showMessageDialog(null, host);
					
					String username = System.getProperty("user.name");
					String hostname = host.getText();
					
					/*Path[] pt = null;
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
						pt = new Path[3];
						pt[2] = fc.getSelectedFile().toPath();
					}*/
					
					List<Object> commands = new ArrayList<Object>();
					//commands.add(genexyplotaction)
					
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						
						String uristr = "jar:" + geneset.zippath.toUri();
						URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
							for( String spec : selspec ) {
								/*Path specdir = root.resolve(spec+".prodigal.fsa");
								if( !Files.exists(specdir) ) {
									if( spec.startsWith("MAT") ) {
										specdir = root.resolve(spec+".gbk.aa");
									} else specdir = root.resolve("fn_"+spec+"_scaffolds.prodigal.fsa");
								}*/
								
								Stream<Gene> genestream = geneset.genelist.stream().filter( gene -> spec.equals(gene.getSpecies()) && (gene.tegeval.type == null || gene.tegeval.type.length() == 0) );
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(baos) );
								genestream.forEach( gene -> {
									try {
										gene.writeGeneIdFasta(bw);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								});
								bw.close();
								baos.close();
								String seqs = baos.toString();
								seqs = seqs.replace('*', 'X');
								byte[] bb = seqs.getBytes();
								Path sigout = root.resolve(spec+".tm");
								Object[] pt = new Object[] {bb,sigout,null};
								if( hostname.equals("localhost") ) {
									String[] cmds = {"decodeanhmm", "-f", "/opt/tmhmm-2.0c/lib/TMHMM2.0.options", "-modelfile", "/opt/tmhmm-2.0c/lib/TMHMM2.0.model"};
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								} else {
									//Path p = Paths.get(spec+".tm");
									//Files.copy(specdir, p, StandardCopyOption.REPLACE_EXISTING);
									
									String[] cmds = {"ssh",hostname,"decodeanhmm", "-f", "/opt/tmhmm-2.0c/lib/TMHMM2.0.options", "-modelfile", "/opt/tmhmm-2.0c/lib/TMHMM2.0.model"};
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								}
							}
							
							break;
						}
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
						}
					};
					
					NativeRun nr = new NativeRun( run );
					nr.runProcessBuilder("transm", commands, new Object[3], false, run, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		windowmenu.add( new AbstractAction("tRNAscan") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Serifier ser = new Serifier();
					Set<String> selspec = getSelspec(null, geneset.getSpecies(), null);
					
					JTextField host = new JTextField("localhost");
					JOptionPane.showMessageDialog(null, host);
					
					String username = System.getProperty("user.name");
					String hostname = host.getText();
					
					/*Path[] pt = null;
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
						pt = new Path[3];
						pt[2] = fc.getSelectedFile().toPath();
					}*/
					
					List<Object> commands = new ArrayList<Object>();
					//commands.add(genexyplotaction)
					
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						
						String uristr = "jar:" + geneset.zippath.toUri();
						URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
							for( String spec : selspec ) {
								Path specdir = root.resolve(spec+".fna");
								if( !Files.exists(specdir) ) {
									if( spec.startsWith("MAT") ) {
										specdir = root.resolve(spec+".gbk.fna");
									} else specdir = root.resolve("fn_"+spec+"_scaffolds.fastg");
								}
								
								System.err.println( Files.exists(specdir) );
								
								Path sigout = root.resolve("trnas.txt");
								Path[] pt = new Path[] {null,sigout,null};
								if( hostname.equals("localhost") ) {
									String[] cmds = {"/usr/local/bin/tRNAscan-SE","-B","-"};
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								} else {
									Path p = Paths.get(spec+".trnascan");
									Files.copy(specdir, p, StandardCopyOption.REPLACE_EXISTING);
									
									String[] cmds = {"scp",spec+".trnascan",hostname+":~",";","ssh",hostname,"trnascan-1.4",spec+".trnascan"};
									//String[] cmds = {"ssh",hostname,"tRNAscan-SE","-B","-"};
									
									commands.add( pt );
									commands.add( Arrays.asList( cmds ) );
								}
							}
							
							break;
						}
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
						}
					};
					
					NativeRun nr = new NativeRun( run );
					nr.runProcessBuilder("tRNAscan", commands, new Object[3], false, run, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JMenu		select = new JMenu("Select");
		AbstractAction breakpointselAction = new AbstractAction("Select breakpoints") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec = (String)syncolorcomb.getSelectedItem();
				
				int rr = 0;
				for( Gene g : geneset.genelist ) {
					if( !spec.equals( g.getSpecies() ) && g.getSpecies().contains("eggert") ) {
						Tegeval tv2 = g.tegeval;
						Annotation n2 = tv2.getNext();
						Annotation p2 = tv2.getPrevious();
						
						GeneGroup gg = g.getGeneGroup();
						
						if( gg.getCommonName().contains("rhodane") ) {
							System.err.println();
						}
						
						Teginfo ti = gg.getGenes( spec );
						int msimcount = 0;
						if( ti != null ) {
							for( Tegeval tv1 : ti.tset ) {
								int simcount = 0;
								
								Annotation n = tv1.getNext();
								Annotation p = tv1.getPrevious();
								
								GeneGroup ggg = tv1.getGene().getGeneGroup();
								if( n2 != null ) {
									if( ggg == n2.getGene().getGeneGroup() ) {
										simcount++;
									}
									
									Annotation nn2 = n2.getNext();
									if( nn2 != null ) {
										if( ggg == nn2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
								}
								
								if( p2 != null ) {
									if( ggg == p2.getGene().getGeneGroup() ) {
										simcount++;
									}
									
									Annotation pp2 = p2.getPrevious();
									if( pp2 != null ) {
										if( ggg == pp2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
								}
								
								if( n != null ) {
									GeneGroup ngg = n.getGene().getGeneGroup();
								
									if( ngg == tv2.getGene().getGeneGroup() ) {
										simcount++;
									}
									
									if( n2 != null ) {
										if( ngg == n2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
									
									if( p2 != null ) {
										if( ngg == p2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
									
									Annotation nn = n.getNext();
									if( nn != null ) {
										ngg = nn.getGene().getGeneGroup();
										
										if( ngg == tv2.getGene().getGeneGroup() ) {
											simcount++;
										}
										
										if( n2 != null ) {
											if( ngg == n2.getGene().getGeneGroup() ) {
												simcount++;
											}
										}
										
										if( p2 != null ) {
											if( ngg == p2.getGene().getGeneGroup() ) {
												simcount++;
											}
										}
									}
								}
								
								if( p != null ) {
									GeneGroup pgg = p.getGene().getGeneGroup();
									
									if( pgg == tv2.getGene().getGeneGroup() ) {
										simcount++;
									}
									
									if( n2 != null ) {
										if( pgg == n2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
									
									if( p2 != null ) {
										if( pgg == p2.getGene().getGeneGroup() ) {
											simcount++;
										}
									}
									
									Annotation pp = p.getPrevious();
									if( pp != null ) {
										pgg = pp.getGene().getGeneGroup();
										
										if( pgg == tv2.getGene().getGeneGroup() ) {
											simcount++;
										}
										
										if( n2 != null ) {
											if( pgg == n2.getGene().getGeneGroup() ) {
												simcount++;
											}
										}
										
										if( p2 != null ) {
											if( pgg == p2.getGene().getGeneGroup() ) {
												simcount++;
											}
										}
									}
								}
								
								//double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
								if( simcount >= msimcount ) {
									//tv = tv1;
									msimcount = simcount;
								}
								
								//double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
								//GeneCompare.gradientColor();
							}
							
							if( msimcount < 2 ) {
								int i = table.convertRowIndexToView(g.index);
								table.addRowSelectionInterval(i, i);
							}
						}
					}
					rr++;
				}
				/*List<Sequence> contigs = geneset.speccontigMap.get( spec );
				for( Sequence c : contigs ) {
					for( Annotation ann : c.annset ) {
						Tegeval tv = (Tegeval)ann;
						
					}
				}*/
			}
		};
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
							((GeneSetHead)comp).saveSel( selname, val);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		select.add( breakpointselAction );
		select.add( saveselAction );
		select.addSeparator();
		select.add(new AbstractAction("Show all") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				updateFilter(table, genefilter, label);
			}
		});
		select.add(new AbstractAction("Crop to selection") {
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
		select.add(new AbstractAction("Crop to inverted selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<Integer> iset = new HashSet<Integer>();
				for( int r : rr ) {
					iset.add( r );
				}
				for (int r = 0; r < table.getRowCount(); r++) {
					if( !iset.contains(r) ) {
						int mr = table.convertRowIndexToModel(r);
						genefilterset.add(mr);
					}
				}
				updateFilter(table, genefilter, label);
			}
		});
		select.add(new AbstractAction("Remove selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// genefilterset.clear();
				int[] rr = table.getSelectedRows();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<Integer>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < geneset.genelist.size(); i++) {
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
		select.add(new AbstractAction("Invert selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// genefilterset.clear();
				int[] rr = table.getSelectedRows();
				Set<Integer> iset = new HashSet<Integer>();
				for( int r : rr ) {
					iset.add( r );
				}
				table.clearSelection();
				for (int r = 0; r < table.getRowCount(); r++) {
					if( !iset.contains(r) ) table.addRowSelectionInterval(r, r);
					/*if (table.isRowSelected(r))
						table.removeRowSelectionInterval(r, r);
					else
						table.addRowSelectionInterval(r, r);*/
				}
			}
		});
		//select.addSeparator();
		select.addSeparator();
		select.add(new AbstractAction("Select single copy genes found in multiple strains") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
				for( GeneGroup gg : geneset.allgenegroups ) {
					Set<String> checkspec = new HashSet<String>( gg.species.keySet() );
					checkspec.retainAll( specset );
					if( gg.getCommonTag() == null && checkspec.size() > 1 && gg.getTegevals().size() == gg.species.size() ) {//gg.getTegevals(checkspec).size() == checkspec.size() ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
						//table.setro
					}
				}
			}
		});
		select.add(new AbstractAction("Select single copy genes in accessory genome of multiple strains") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
				for( GeneGroup gg : geneset.allgenegroups ) {
					Set<String> checkspec = new HashSet<String>( gg.species.keySet() );
					checkspec.retainAll( specset );
					if( gg.getCommonTag() == null && checkspec.size() > 1 && checkspec.size() < specset.size() && gg.getTegevals().size() == gg.species.size() ) {//gg.getTegevals(checkspec).size() == checkspec.size() ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
						//table.setro
					}
				}
			}
		});
		select.add(new AbstractAction("Select single copy genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.getTegevals().size() == gg.species.size() ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
						//table.setro
					}
				}
			}
		});
		select.add(new AbstractAction("Select duplicated genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 2 ) {
							List<Tegeval> ta = new ArrayList<Tegeval>( ti.tset );
							if( ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1)) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}
				}
			}
		});
		select.add(new AbstractAction("Select triplicated genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 3 ) {
							List<Tegeval> ta = new ArrayList<Tegeval>( ti.tset );
							if( (ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1)) 
									&& (ta.get(1).getNext() == ta.get(2) || ta.get(1).getPrevious() == ta.get(2))) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}
				}
			}
		});
		select.add(new AbstractAction("Select plasmid genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.isOnAnyPlasmid() ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}
					/*int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 3 ) {
							List<Tegeval> ta = new ArrayList<Tegeval>( ti.tset );
							if( (ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1)) && (ta.get(1).getNext() == ta.get(2) || ta.get(1).getPrevious() == ta.get(2))) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}*/
				}
			}
		});
		select.add(new AbstractAction("Select phage genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.isInAnyPhage() ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}
					/*int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 3 ) {
							List<Tegeval> ta = new ArrayList<Tegeval>( ti.tset );
							if( (ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1)) && (ta.get(1).getNext() == ta.get(2) || ta.get(1).getPrevious() == ta.get(2))) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						int r = table.convertRowIndexToView(gg.index);
						table.addRowSelectionInterval(r, r);
					}*/
				}
			}
		});
		select.addSeparator();
		AbstractAction	selectsharingaction = new AbstractAction("Select sharing") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton panbtn = new JRadioButton("Pan");
				JRadioButton corebtn = new JRadioButton("Core");
				JRadioButton blehbtn = new JRadioButton("Only in");
				ButtonGroup	bg = new ButtonGroup();
				bg.add( panbtn );
				bg.add( corebtn );
				bg.add( blehbtn );
				corebtn.setSelected( true );
				//Object[] objs = new Object[] { panbtn, corebtn };
				//JOptionPane.showMessageDialog( geneset, objs, "Select id types", JOptionPane.PLAIN_MESSAGE );
				
				final List<String> species = geneset.getSpecies();
				TableModel model = new TableModel() {
					@Override
					public int getRowCount() {
						return species.size();
					}

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public String getColumnName(int columnIndex) {
						return null;
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
						return species.get( rowIndex );
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				JTable table = new JTable( model );
				table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				JScrollPane	scroll = new JScrollPane( table );
				
				FlowLayout flowlayout = new FlowLayout();
				JComponent c = new JComponent() {};
				c.setLayout( flowlayout );
				c.add( scroll );
				c.add( panbtn );
				c.add( corebtn );
				c.add( blehbtn );
				
				JOptionPane.showMessageDialog(comp, c);
				
				final Set<String>	specs = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String spec = (String)table.getValueAt(r, 0);
					specs.add( spec );
				}
				
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( blehbtn.isSelected() ) {
						Set<String> ss = new HashSet<String>( gg.species.keySet() );
						ss.removeAll( specs );
						if( ss.size() == 0 ) {
							int r = GeneSetHead.this.table.convertRowIndexToView( gg.index );
							if( r >= 0 && r < GeneSetHead.this.table.getRowCount() ) GeneSetHead.this.table.addRowSelectionInterval( r, r );
						}
					} else if( gg.species.keySet().containsAll( specs ) && (panbtn.isSelected() || specs.size() == gg.species.size()) ) {
						int r = GeneSetHead.this.table.convertRowIndexToView( gg.index );
						if( r >= 0 && r < GeneSetHead.this.table.getRowCount() ) {
							GeneSetHead.this.table.addRowSelectionInterval( r, r );
						}
					}
				}
			}
		};
		select.add( selectsharingaction );
		AbstractAction	selectdirtyaction = new AbstractAction("Select dirty") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( table.getModel() == groupModel ) {
					int i = 0;
					for( GeneGroup gg : geneset.allgenegroups ) {
						if( gg.containsDirty() ) {
							int r = table.convertRowIndexToView( i );
							if( r != -1 ) table.addRowSelectionInterval( r, r );
						}
						i++;
					}
				}
			}
		};
		select.add( selectdirtyaction );
		AbstractAction	selectdesignationaction = new AbstractAction("Select designation") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>	descombo = new JComboBox<String>( geneset.deset.toArray( new String[geneset.deset.size()] ) );
				descombo.insertItemAt("", 0);
				descombo.setSelectedIndex( 0 );
				
				JOptionPane.showMessageDialog(GeneSetHead.this, descombo);
				String seldes = (String)descombo.getSelectedItem();
				if( table.getModel() == groupModel ) {
					int i = 0;
					for( GeneGroup gg : geneset.allgenegroups ) {
						if( gg.genes != null ) for( Gene g : gg.genes ) {
							if( seldes.equals(g.tegeval.designation) ) {
								int r = table.convertRowIndexToView(i);
								if( r >= 0 && r < table.getRowCount() ) table.addRowSelectionInterval(r, r);
							}
						}
						i++;
					}
				}
			}
		};
		select.add( selectdesignationaction );
		select.add( new AbstractAction("Blast select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blast( false );
			}
		});
		select.add( new AbstractAction("Blastx select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blast( true );
			}
		});
		select.add( new AbstractAction("Blastn select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blastn( true );
			}
		});
		select.add( new AbstractAction("Blast search") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blastn( false );
			}
		});
		
		menubar.add( file );
		menubar.add( edit );
		menubar.add( view );
		menubar.add( sequencemenu );
		menubar.add( windowmenu );
		menubar.add( select );
		menubar.add( help );
		
		if( comp != null ) {
			final Window window = SwingUtilities.windowForComponent(comp);
			initFSKeyListener(window);
			if ( comp instanceof JFrame || window instanceof JFrame) {
				JFrame frame = (JFrame)( window == null ? comp : window );
				if (!frame.isResizable())
					frame.setResizable(true);
	
				frame.addKeyListener(keylistener);
				frame.setJMenuBar(menubar);
			}
		}

		final JButton jb = new JButton(new AbstractAction("Atlas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					URL url = new URL("file:///home/sigmar/workspace/distann/bin/circle.html");
					GeneSetHead.this.getAppletContext().showDocument(url, "_blank");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JComponent cc = null;
		try {
			cc = newSoft(jb, comp, upper, lower, GeneSetHead.this, selcomb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if( comp != null ) {
			if( comp instanceof Applet )
				try {
					((GeneSetHead)comp).saveSel( null, null);
				} catch ( NoSuchMethodError | Exception e1 ) {
					e1.printStackTrace();
				}
			comp.add( cc );
		}
	}
	
	public void saveSel( String name, String val) throws Exception, NoSuchMethodError {
		JSObject jso = JSObject.getWindow( this );
		jso.call("saveSel", new Object[] { name, val });
	}
	
	public void stop() {
		super.stop();
		try {
			if( geneset.zippath != null ) geneset.saveContigOrder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newFile() {
		JFileChooser fc = new JFileChooser();
		javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".zip")||f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Zip files (*.zip)";
			}
		};
		fc.setFileFilter(ff);
		if( fc.showSaveDialog( comp ) == JFileChooser.APPROVE_OPTION ) {
			geneset.zippath = fc.getSelectedFile().toPath();
			//is = new FileInputStream( zipfile );
		}
	}
	
	public void fetchGenomes() {
		if( geneset.zippath == null ) {
			newFile();
		}
		
		JFrame frame = new JFrame("Fetch genomes");
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setSize(400, 600);
		
		//frame.add(topcomp)
		
		try {
			Map<String,String> env = new HashMap<String,String>();
			env.put("create", "true");
			//Path path = zipfile.toPath();
			String uristr = "jar:" + geneset.zippath.toUri();
			geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
			geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
			
			final SerifyApplet	sa = new SerifyApplet( geneset.zipfilesystem );
			sa.init( frame, geneset.user );
			
			for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
				Files.list(root).filter( new Predicate<Path>() {
					@Override
					public boolean test(Path t) {
						String fname = t.getFileName().toString();
						return /*fname.endsWith(".gbk") || */fname.endsWith(".fna") || fname.endsWith("fastg") || fname.endsWith(".fsa") || fname.endsWith(".fa") || fname.endsWith(".fasta") || fname.endsWith(".aa") || fname.endsWith(".nn") || fname.endsWith(".trna") || fname.endsWith(".rrna") || fname.endsWith(".ssu") || fname.endsWith(".lsu") || fname.endsWith(".tsu");
					}
				}).forEach( new Consumer<Path>() {
					@Override
					public void accept(Path t) {
						try {
							sa.addSequences(t.getFileName().toString(), t, null);
						} catch (URISyntaxException | IOException e) {
							e.printStackTrace();
						}
					}
				});;
			}
		} catch (IOException e1) {
			try {
				geneset.zipfilesystem.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
		
		//BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
		
		//InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
		//uni2symbol(new InputStreamReader(is), bw, unimap);
		
		//bw.close();
		//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
		
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
					geneset.zipfilesystem.close();

					geneset.cleanUp();
					importStuff();
				} catch (IOException | UnavailableServiceException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		frame.setVisible( true );
	}
	
	private JComponent showGeneTable(/*final Map<String, Gene> genemap, final List<Gene> genelist, 
			final List<Function> funclist, final List<Set<String>> iclusterlist, final List<Set<String>> uclusterlist,
			final Map<Set<String>, ShareNum> specset,*/ final Map<Set<String>, ClusterInfo> clustInfoMap, final JButton jb, final SwingNode upper, final SwingNode lower,
			final Container comp, final JApplet applet, final JComboBox selcomblocal) throws IOException {
		JSplitPane splitpane = new JSplitPane();
		splitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitpane.setDividerLocation(400);
		JScrollPane scrollpane = new JScrollPane();
		table = new JTable() {
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
								Gene gg = geneset.genelist.get(cr);
								//if (gg.species != null) {
									// ret.append( gg.name + ":\n" );
									//for (String sp : gg.species.keySet()) {
								Tegeval tv = gg.tegeval;
								if (!contigs.containsKey(tv.getContshort())) {
									List<Tegeval> ltv = new ArrayList<Tegeval>();
									ltv.add(tv);
									contigs.put(tv.getContshort().getName(), ltv);
								} else {
									List<Tegeval> ltv = contigs.get(tv.getContshort());
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
							for (String cont : contigs.keySet()) {
								List<Tegeval> tv = contigs.get(cont);
								String dna = tv.get(0).getSequence();
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

		final Color darkgreen = new Color( 0, 128, 0 );
		final Color darkred = new Color( 128, 0, 0 );
		final Color darkblue = new Color( 0, 0, 128 );
		final Color darkmag = new Color( 128, 0, 128 );
		table.setDefaultRenderer(Teg.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if( value == null ) {
					label.setBackground(Color.white);
				} else {
					String spec = (String)syncolorcomb.getSelectedItem();
					if( spec.length() > 0 ) {
						if( spec.equals("All") ) {
							if( value instanceof Teginfo ) {
								Teginfo ti = (Teginfo)value;
								label.setBackground( Color.green );
								for( Tegeval tv : ti.tset ) {
									String tspec = tv.getGene().getSpecies();
									List<Sequence> scontigs = geneset.speccontigMap.get( tspec );
									GeneGroup gg = tv.getGene().getGeneGroup();
									double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
									if( ratio == -1 ) {
										ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
										label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
										label.setForeground( Color.white );
									} else {
										label.setBackground( GeneCompare.gradientColor( ratio ) );
										label.setForeground( Color.black );
									}
									break;
									//GeneCompare.gradientColor();
								}
							} else if( value instanceof Tegeval ) {
								Tegeval tv = (Tegeval)value;
								String tspec = tv.getGene().getSpecies();
								List<Sequence> scontigs = geneset.speccontigMap.get( tspec );
								GeneGroup gg = tv.getGene().getGeneGroup();
								double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
								if( ratio == -1 ) {
									ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
									label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
									label.setForeground( Color.white );
								} else {
									label.setBackground( GeneCompare.gradientColor( ratio ) );
									label.setForeground( Color.black );
								}
							}
						} else {
							List<Sequence> contigs = geneset.speccontigMap.get( spec );
							if( value instanceof Teginfo ) {
								//Teginfo ti = (Teginfo)value;
								label.setBackground( Color.green );
								
								//GeneGroup 	gg = ti.best.getGene().getGeneGroup();
								//Teginfo		gene2s = gg.getGenes(spec);
								//double ratio = -1.0;
								int msimcount = 0;
								
								GeneGroup gg = null;
								Tegeval tv = null;
								Tegeval tv2 = null;
								if( table.getModel() == defaultModel ) {
									int i = table.convertRowIndexToModel(row);
									Gene g = geneset.genelist.get(i);
									gg = g.getGeneGroup();
									tv2 = g.tegeval;
								}
								
								if( gg != null ) {
									Teginfo		gene2s = gg.getGenes(spec);
									if( gene2s != null && gene2s.tset != null ) for( Tegeval tv1 : gene2s.tset ) {
										int simcount = 0;
										
										Annotation n = tv1.getNext();
										Annotation p = tv1.getPrevious();
										Annotation n2 = tv2.getNext();
										Annotation p2 = tv2.getPrevious();
										
										if( n != null ) {
											GeneGroup ngg = n.getGene().getGeneGroup();
											if( n2 != null ) {
												if( ngg == n2.getGene().getGeneGroup() ) simcount++;
											}
											
											if( p2 != null ) {
												if( ngg == p2.getGene().getGeneGroup() ) simcount++;
											}
										}
										
										if( p != null ) {
											GeneGroup pgg = p.getGene().getGeneGroup();
											if( n2 != null ) {
												if( pgg == n2.getGene().getGeneGroup() ) simcount++;
											}
											
											if( p2 != null ) {
												if( pgg == p2.getGene().getGeneGroup() ) simcount++;
											}
										}
										
										//double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
										if( simcount >= msimcount ) {
											tv = tv1;
											msimcount = simcount;
										}
										
										//double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
										//GeneCompare.gradientColor();
									}
								}
								
								double ratio = GeneCompare.invertedGradientRatio(spec, contigs, tv);
								if( ratio == -1 ) {
									if( gg != null ) ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, gg);
									label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
									label.setForeground( Color.black );
								} else {
									label.setBackground( GeneCompare.gradientColor( ratio ) );
									label.setForeground( Color.black );
								}
							} else if( value instanceof Tegeval ) {
								Tegeval tv = (Tegeval)value;
								Tegeval tv2 = null;
								GeneGroup gg = tv.getGene().getGeneGroup();
								int msimcount = 0;
								if( gg != null ) {
									Teginfo		gene2s = gg.getGenes(spec);
									if( gene2s != null && gene2s.tset != null ) for( Tegeval tv1 : gene2s.tset ) {
										int simcount = 0;
										
										Annotation n = tv1.getNext();
										Annotation p = tv1.getPrevious();
										Annotation n2 = tv.getNext();
										Annotation p2 = tv.getPrevious();
										
										if( n != null ) {
											GeneGroup ngg = n.getGene().getGeneGroup();
											if( n2 != null ) {
												if( ngg == n2.getGene().getGeneGroup() ) simcount++;
											}
											
											if( p2 != null ) {
												if( ngg == p2.getGene().getGeneGroup() ) simcount++;
											}
										}
										
										if( p != null ) {
											GeneGroup pgg = p.getGene().getGeneGroup();
											if( n2 != null ) {
												if( pgg == n2.getGene().getGeneGroup() ) simcount++;
											}
											
											if( p2 != null ) {
												if( pgg == p2.getGene().getGeneGroup() ) simcount++;
											}
										}
										
										//double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
										if( simcount >= msimcount ) {
											tv2 = tv1;
											msimcount = simcount;
										}
										
										//double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
										//GeneCompare.gradientColor();
									}
								}
								//double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
								double ratio = GeneCompare.invertedGradientRatio(spec, contigs, tv2);
								if( ratio == -1 ) {
									ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, gg);
									label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
									label.setForeground( Color.black );
								} else {
									label.setBackground( GeneCompare.gradientColor( ratio ) );
									label.setForeground( Color.black );
								}
								
								
								/*double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
								label.setBackground( GeneCompare.gradientColor( ratio ) );*/
							}
						}
					} else if( value instanceof Teginfo ) {
						Teginfo ti = (Teginfo)value;						
						boolean plasmid = false;
						boolean phage = false;
						for( Tegeval tv : ti.tset ) {
							phage = phage | tv.isPhage();
							
							Sequence seq = tv.getContshort();
							
							if( seq == null ) {
								//System.err.println();
							}
							
							plasmid = plasmid | (seq != null && seq.isPlasmid());
						}
						
						if( phage && plasmid ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkmag );
							else label.setBackground( Color.magenta );
						} else if( phage ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkblue );
							else label.setBackground( Color.blue );
						} else if( plasmid ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkred );
							else label.setBackground( Color.red );
						} else {
							if( ti.tset.size() > 1 ) label.setBackground( darkgreen );
							else label.setBackground( Color.green );
						}
					} else {
						Tegeval tv = (Tegeval)value;
						Gene g = tv.getGene();
						GeneGroup gg = g.getGeneGroup();
						Teginfo ti = gg.species.get( g.getSpecies() );
						
						boolean phage = tv.isPhage();
						boolean plasmid = tv.getContshort().isPlasmid();
						if( phage && plasmid ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkmag );
							else label.setBackground( Color.magenta );
						} else if( phage ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkblue );
							else label.setBackground( Color.blue );
						} else if( plasmid ) {
							if( ti.tset.size() > 1 ) label.setBackground( darkred );
							else label.setBackground( Color.red );
						} else {
							if( ti.tset.size() > 1 ) label.setBackground( darkgreen );
							else label.setBackground( Color.green );
						}
					}
					// label.setText( value.toString() );
					/*if (colorCodes[0] == null)
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
						label.setBackground(colorCodes[8]);*/
				}
				return label;
			}
		});

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		scrollpane.setViewportView(table);

		Set<String> current = null;
		Set<String> currentko = null;
		InputStream is = GeneSet.class.getResourceAsStream("kegg_pathways");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				current = new HashSet<String>();
				currentko = new HashSet<String>();
				geneset.pathwaymap.put(line.substring(1), current);
				geneset.pathwaykomap.put(line.substring(1), currentko);
			} else if( !line.startsWith("K") ) {
				if (current != null) {
					String str = line.split("[\t ]+")[0];
					current.add(str);
				}
			} else {
				if (currentko != null) {
					String str = line.split("[\t ]+")[0];
					currentko.add(str);
				}
			}
			line = br.readLine();
		}
		br.close();
		
		//FileReader fr = new FileReader("/vg454flx/ko2go.txt");
		/*is = GeneSet.class.getResourceAsStream("/ko2go.txt");
		InputStreamReader isr = new InputStreamReader( is );
		br = new BufferedReader( isr );
		line = br.readLine();
		while (line != null) {
			String[] split = line.split(" = ");
			String[] subsplit = split[1].split(" ");
			Set<String> gos = new HashSet<String>();
			for( String go : subsplit ) {
				gos.add( go );
			}
			ko2go.put( split[0], gos );
			line = br.readLine();
		}
		br.close();*/
		
		

		final JTextField textfield = new JTextField();
		JComponent topcomp = new JComponent() {};
		topcomp.setLayout(new BorderLayout());
		topcomp.add(scrollpane);

		textfield.setPreferredSize(new Dimension(350, 25));
		
		final JRadioButton	search = new JRadioButton("Search");
		final JRadioButton	filter = new JRadioButton("Filter");
		
		ButtonGroup bgsf = new ButtonGroup();
		bgsf.add( search );
		bgsf.add( filter );
		
		filter.setSelected( true );
		
		JToolBar topcombo = new JToolBar();
		// topcombo.
		// topcombo.setLayout( new FlowLayout() );
		
		specombo = new JComboBox<String>();
		combo = new JComboBox<String>();
				
		specombo.addItem("Select blast species");
		combo.addItem("Select pathway");
		topcombo.add(combo);
		topcombo.add(specombo);
		topcomp.add(topcombo, BorderLayout.SOUTH);

		JComponent ttopcom = new JComponent() {};
		ttopcom.setLayout(new FlowLayout());

/*				frame.setVisible( true );
			}
		};
		AbstractAction	sharenumaction = new AbstractAction("Update share numbers") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> specs = getSelspec(GeneSetHead.this, specList, null);
				updateShareNum(specs);
			}
		};
		AbstractAction	importgenesymbolaction = new AbstractAction("Import gene symbols") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						Path path = zipfile.toPath();
						String uristr = "jar:" + path.toUri();
						geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")* );
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						
						Path nf = geneset.zipfilesystem.getPath("/smap_short.txt");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						uni2symbol(new InputStreamReader(is), bw, unimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						geneset.zipfilesystem.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		
		AbstractAction	importidmappingaction = new AbstractAction("Id mapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						Path path = zipfile.toPath();
						String uristr = "jar:" + path.toUri();
						geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")/ );
						geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
						
						Path nf = geneset.zipfilesystem.getPath("/idmapping_short.dat");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						if( unimap != null ) unimap.clear();
						unimap = idMapping(new InputStreamReader(is), bw, 2, 0, refmap, genmap, gimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						geneset.zipfilesystem.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		
		final JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem();
		checkbox.setAction(new AbstractAction("Sort by location") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tegeval.locsort = checkbox.isSelected();
			}
		});
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
		
		JMenuBar	menubar = new JMenuBar();
		JMenu		menu = new JMenu("Functions");
		menu.add( importidmappingaction );
		menu.add( functionmappingaction );
		menu.add( importgenesymbolaction );
		menu.add( fetchaction );
		menu.add( blast2action );
		menu.add( sharenumaction );
		menu.addSeparator();
		menu.add( checkbox );
		menu.add( saveselAction );
		menu.addSeparator();
		menu.add( genomestataction );
		menu.add( selectsharingaction );
		menu.add( shuffletreeaction );
		menu.add( presabsaction );
		menu.add( freqdistaction );
		menu.add( gcpaction );
		menu.add( matrixaction );
		menu.add( pancoreaction );
		menu.add( blastaction );
		menu.add( koexportaction );
		menu.add( genomesizeaction );
		menu.add( gcaction );
		menu.add( gcskewaction );
		menu.add( mltreemapaction );
		menu.add( sevenaction );
		menu.add( cogaction );
		menu.add( genexyplotaction );
		menu.add( compareplotaction );
		menu.add( syntenygradientaction );
		menu.add( codregaction );
		menu.add( fetchcoreaction );
		menu.add( loadcontiggraphaction );
		menu.add( selectflankingaction );
		menu.add( showflankingaction );
		menu.add( showcontigsaction );
		menu.add( showunresolved );
		menu.add( genephyl );
		
		menubar.add( menu );
		ttopcom.add( menubar );
		
		JMenu		view = new JMenu("View");
		menubar.add( view );
		
		gb = new JRadioButtonMenuItem( new AbstractAction("Genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( defaultModel );
			}
		});
		view.add( gb );
		ggb = new JRadioButtonMenuItem( new AbstractAction("Gene groups") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setModel( groupModel );
			}
			
		});
		ButtonGroup	bg = new ButtonGroup();
		bg.add( gb );
		bg.add( ggb );
		
		ggb.setSelected( true );
		
		view.add( ggb );*/
		
		//ttopcom.add( shuffletreebutton );
		//ttopcom.add( presabsbutton );
		//ttopcom.add(freqdistbutton);
		//ttopcom.add(matrixbutton);
		
		ttopcom.add(textfield);
		ttopcom.add(search);
		ttopcom.add(filter);
		ttopcom.add(label);

		selcomblocal.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String key = (String)e.getItem();
				if( ((GeneSetHead)comp).selectionMap.containsKey(key) ) {
					Set<Integer> val = ((GeneSetHead)comp).selectionMap.get(key);
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
		
		
		/*syncolorcomb.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String spec = (String)syncolorcomb.getSelectedItem();
				//if( spec.length() > 0 )
			}
		});*/
		ttopcom.add( searchcolcomb );
		ttopcom.add( syncolorcomb );
		topcomp.add(ttopcom, BorderLayout.NORTH);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
				for( JSplitPane gsplitpane : splitpaneList ) {
					gsplitpane.repaint();
				}
			}
		});

		/*JButton but = new JButton(new AbstractAction("Gene sorter") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GeneSorter.mynd(genelist, table, "t.scotoductusSA01", contigs);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});*/
		
		JScrollPane fscrollpane = new JScrollPane();
		final JTextField ftextfield = new JTextField();
		JComponent botcomp = new JComponent() {};
		botcomp.setLayout(new BorderLayout());
		botcomp.add(fscrollpane);

		// JButton sbutt = new JButton("Find conserved terms");
		ftextfield.setPreferredSize(new Dimension(500, 25));
		JComponent botcombo = new JComponent() {
		};
		botcombo.setLayout(new FlowLayout());
		botcombo.add( ftextfield );
		//botcombo.add( but );

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
				JComponent c = new JComponent() {};
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
										} else if (aln.getScore() < 3.0f * (first.getScore() / 4.0f))
											break;
										result += aln.toString();
										regnames.add(aln.getShortDestName());

										if (++count == 10)
											break;
									}
									textarea.setText(result);

									for (Gene g : geneset.genelist) {
										boolean found = false;
										Tegeval tv = g.tegeval;
										if (regnames.contains(tv.name)) {
											found = true;
											break;
										}
										if (found) {
											int rr = table.convertRowIndexToView(g.index);
											if (rr != -1)
												table.addRowSelectionInterval(rr, rr);
											break;
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
		
		/*Action blastsearchaction = new AbstractAction("Blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blast();
			}
		};*/
		//JButton blastb = new JButton( blastsearchaction );
		//botcombo.add( blastb );
		
		botcombo.add(jb);

		// botcombo.add( sbutt );
		botcomp.add(botcombo, BorderLayout.SOUTH);

		if( upper != null ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					upper.setContent( botcomp );
					lower.setContent( topcomp );
				}
			});
		} else {
			splitpane.setBottomComponent(botcomp);
			splitpane.setTopComponent(topcomp);
		}

		groupModel = new TableModel() {
			@Override
			public int getRowCount() {
				return geneset.allgenegroups == null ? 0 : geneset.allgenegroups.size();
			}

			@Override
			public int getColumnCount() {
				return 32+geneset.specList.size();
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
					return "Kegg pathway";
				} else if (columnIndex == 7) {
					return "KO";
				} else if (columnIndex == 8) {
					return "KSymbol";
				} else if (columnIndex == 9) {
					return "Symbol";
				} else if (columnIndex == 10) {
					return "KO name";
				} else if (columnIndex == 11) {
					return "Pdbid";
				} else if (columnIndex == 12) {
					return "EC";
				} else if (columnIndex == 13) {
					return "Cog name";
				} else if (columnIndex == 14) {
					return "Cog";
				} else if (columnIndex == 15) {
					return "Cog annotation";
				} else if (columnIndex == 16) {
					return "Cog symbol";
				} else if (columnIndex == 17) {
					return "Cazy";
				} else if (columnIndex == 18) {
					return "Present in";
				} else if (columnIndex == 19) {
					return "Group index";
				} else if (columnIndex == 20) {
					return "Group coverage";
				} else if (columnIndex == 21) {
					return "Group size";
				} else if (columnIndex == 22) {
					return "Locprev";
				} else if (columnIndex == 23) {
					return "Avg GC%";
				} else if (columnIndex == 24) {
					return "# of locus";
				} else if (columnIndex == 25) {
					return "# of loc in group";
				} else if (columnIndex == 26) {
					return "max length";
				} else if (columnIndex == 27) {
					return "sharing number";
				} else if (columnIndex == 28) {
					return "# Cyc";
				} else if (columnIndex == 29) {
					return "16S Corr";
				} else if (columnIndex == 30) {
					return "SingalP";
				} else if (columnIndex == 31) {
					return "TransM";
				} else {
					String spec = geneset.specList.get( columnIndex - 32 );
					if( spec != null ) {
						if( spec.toLowerCase().contains("thermus") ) {
							int i = spec.indexOf('_');
							return spec.substring(i+1, spec.length());
						} else return spec;
					}
					return "";
				}
				/* else if (columnIndex == 19) {
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
				}*/
				
				//return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 19 || columnIndex == 20 || columnIndex == 28 )
					return Double.class;
				else if(columnIndex == 10 || (columnIndex >= 17 && columnIndex <= 28) )
					return Integer.class;
				else if (columnIndex >= 32)
					return Teg.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				GeneGroup gg = geneset.allgenegroups.get(rowIndex);
				if (columnIndex == 0) {
					return gg.getCommonName();
				} else if (columnIndex == 1) {
					return gg.getCommonOrigin();
				} else if (columnIndex == 2) {
					return null;//gene.genid;
				} else if (columnIndex == 3) {
					return gg.getCommonRefId();
				} else if (columnIndex == 4) {
					return gg.getCommonUnId();
				} else if (columnIndex == 5) {
					return gg.getKeggid();
				} else if (columnIndex == 6) {
					return gg.getKeggPathway();
				} else if (columnIndex == 7) {
					return gg.getCommonKO();
				} else if (columnIndex == 8) {
					return gg.getCommonKSymbol();
				} else if (columnIndex == 9) {
					return gg.getCommonSymbol(); //ko2name != null ? ko2name.get( gg.getCommonKO() ) : null;
				} else if (columnIndex == 10) {
					String ret = geneset.ko2name != null ? geneset.ko2name.get( gg.getCommonKO() ) : null;
					if( ret == null ) {
						String symbol = gg.getCommonSymbol();
						if( symbol != null ) {
							if( symbol.length() <= 5 ) ret = symbol;
						}
					}
					return ret;
				} else if (columnIndex == 11) {
					return null;//gene.pdbid;
				} else if (columnIndex == 12) {
					return gg.getCommonEc();
				} else if (columnIndex == 13) {
					Cog cog = gg.getCommonCog( geneset.cogmap );
					if( cog != null ) {
						if( cog.name == null ) cog.name = geneset.cogidmap.get( cog.id );
						return cog.name;
					}
					return null;
				} else if (columnIndex == 14) {
					Cog cog = gg.getCommonCog( geneset.cogmap );
					return cog != null ? cog.id : null;
				} else if (columnIndex == 15) {
					Cog cog = gg.getCommonCog( geneset.cogmap );
					return cog != null ? cog.annotation : null;
				} else if (columnIndex == 16) {
					Cog cog = gg.getCommonCog( geneset.cogmap );
					return cog != null ? cog.genesymbol : null;
				} else if (columnIndex == 17) {
					return gg.getCommonCazy( geneset.cazymap );
				} else if (columnIndex == 18) {
					return gg.getSpecies().size();
				} else if (columnIndex == 19) {
					return gg.groupIndex;
				} else if (columnIndex == 20) {
					return gg.getGroupCoverage();
				} else if (columnIndex == 21) {
					return gg.getGroupGeneCount();
				} else if (columnIndex == 22) {
					return null;//gene.proximityGroupPreservation;
				} else if (columnIndex == 23) {
					return gg.getAvgGCPerc();
				} else if (columnIndex == 24) {
					return gg.genes.size();
				} else if (columnIndex == 25) {
					return gg.getGroupCount();
				} else if (columnIndex == 26) {
					return gg.getMaxLength();
				} else if (columnIndex == 27) {
					return geneset.specset.get( gg.getSpecies() );
				} else if (columnIndex == 28) {
					return gg.getMaxCyc();
				} else if (columnIndex == 29) {
					return gg.getGroupCoverage() == 39 && gg.getGroupCount() == 39 ? 0 : -1;
				} else if (columnIndex == 30) {
					return gg.getCommonSignalP();
				} else if (columnIndex == 31) {
					return gg.getCommonTransM();
				} else {
					String spec = geneset.specList.get( columnIndex - 32 );
					Teginfo ret = geneset.getGroupTes( gg, spec );
					return ret;
					//return null;
				}
				//return columnIndex >= 11 ? null : "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		defaultModel = new TableModel() {
			@Override
			public int getRowCount() {
				int gs = geneset.genelist.size();
				return gs;
			}

			@Override
			public int getColumnCount() {
				return 26+geneset.specList.size();
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
					return "KOid";
				} else if (columnIndex == 7) {
					return "KSymbol";
				} else if (columnIndex == 8) {
					return "Symbol";
				} else if (columnIndex == 9) {
					return "KOname";
				} else if (columnIndex == 10) {
					return "Pdbid";
				} else if (columnIndex == 11) {
					return "ecid";
				} else if (columnIndex == 12) {
					return "COG";
				} else if (columnIndex == 13) {
					return "COG name";
				} else if (columnIndex == 14) {
					return "Present in";
				} else if (columnIndex == 15) {
					return "Group index";
				} else if (columnIndex == 16) {
					return "Group coverage";
				} else if (columnIndex == 17) {
					return "Group size";
				} else if (columnIndex == 18) {
					return "Locprev";
				} else if (columnIndex == 19) {
					return "Avg GC%";
				} else if (columnIndex == 20) {
					return "# of locus";
				} else if (columnIndex == 21) {
					return "# of loc in group";
				} else if (columnIndex == 22) {
					return "max length";
				} else if (columnIndex == 23) {
					return "sharing number";
				} else if (columnIndex == 24) {
					return "# Cyc";
				} else if (columnIndex == 25) {
					return "16S Corr";
				} else {
					return geneset.specList.get( columnIndex - 26 );
				} /*else if (columnIndex == 19) {
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
				}*/
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 16 || columnIndex == 19 || columnIndex == 25 )
					return Double.class;
				else if(columnIndex >= 13 && columnIndex <= 24)
					return Integer.class;
				else if (columnIndex >= 26)
					return Teg.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Gene gene = geneset.genelist.get(rowIndex);
				if (columnIndex == 0) {
					GeneGroup gg = gene.getGeneGroup();
					return gg != null ? gene.getGeneGroup().getCommonName() : null;
				} else if (columnIndex == 1) {
					return gene.getSpecies();
				} else if (columnIndex == 2) {
					return gene.genid;
				} else if (columnIndex == 3) {
					return gene.refid;
				} else if (columnIndex == 4) {
					return gene.uniid;
				} else if (columnIndex == 5) {
					return gene.keggid;
				} else if (columnIndex == 6) {
					GeneGroup gg = gene.getGeneGroup();
					return gg != null ? gg.getCommonKO() : null;
				} else if (columnIndex == 7) {
					GeneGroup gg = gene.getGeneGroup();
					return gg != null ? gg.getCommonKSymbol() : null;
				} else if (columnIndex == 8) {
					GeneGroup gg = gene.getGeneGroup();
					return gg != null ? gg.getCommonSymbol() : null; //gene.symbol
				} else if (columnIndex == 9) {
					GeneGroup gg = gene.getGeneGroup();
					return gg != null ? gg.getCommonKOName( geneset.ko2name ) : null;
				} else if (columnIndex == 10) {
					return gene.pdbid;
				} else if (columnIndex == 11) {
					return gene.ecid;
				} else if (columnIndex == 12) {
					Cog cog = gene.getGeneGroup() != null ? gene.getGeneGroup().getCommonCog( geneset.cogmap ) : null;
					if( cog != null ) return cog.id;
					return null;
				} else if (columnIndex == 13) {
					Cog cog = gene.getGeneGroup() != null ? gene.getGeneGroup().getCommonCog( geneset.cogmap ) : null;
					if( cog != null ) return cog.name;
					return null;
				} else if (columnIndex == 14) {
					return gene.getGeneGroup().getSpecies().size();
				} else if (columnIndex == 15) {
					return gene.getGroupIndex();
				} else if (columnIndex == 16) {
					return gene.getGroupCoverage();
				} else if (columnIndex == 17) {
					return gene.getGroupGenCount();
				} else if (columnIndex == 18) {
					return gene.proximityGroupPreservation;
				} else if (columnIndex == 19) {
					return gene.getGCPerc();
				} else if (columnIndex == 20) {
					/*int val = 0;
					for (String str : gene.species.keySet()) {
						val += gene.species.get(str).tset.size();
					}*/
					return 1;
				} else if (columnIndex == 21) {
					return gene.getGroupCount();
				} else if (columnIndex == 22) {
					return gene.getMaxLength();
				} else if (columnIndex == 23) {
					GeneGroup gg = gene.getGeneGroup();
					if( gg != null && gg.getSpecies() != null ) {
						return geneset.specset.get( gg.getSpecies() );
					}
					return null;
				} else if (columnIndex == 24) {
					gene.getMaxCyc();
				} else if (columnIndex == 25) {
					return gene.getGroupCoverage() == 35 && gene.getGroupCount() == 35 ? gene.corr16s : -1;
				} else {
					/*String spec = specList.get( columnIndex-26 );
					/*if( spec.contains("timidus") ) {
						System.err.println();
					}*
					//Teginfo set = gene.species.equals(spec) ? gene.teginfo : null;
					if( gene.getSpecies().equals( spec ) ) {
						return gene.tegeval;
					} else {
						return gene.getGeneGroup().species.get( spec );
					}*/
					
					return null;
				}
				return columnIndex >= 17 ? null : "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		table.setModel( groupModel );
		//table.setModel( defaultModel );

		/*
		 * Comparator<Tegeval> wrapMe = new Comparator<Tegeval>() { public int
		 * compare(Tegeval o1, Tegeval o2) { return o1.compareTo(o2); } };
		 * DefaultRowSorter<TableModel, Integer> rowsorter =
		 * (DefaultRowSorter<TableModel,Integer>)table.getRowSorter(); for( int
		 * i = 10; i < 23; i++ ) { rowsorter.setComparator(i,
		 * NullComparators.atEnd(wrapMe)); }
		 */

		table.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				for (String cstr : geneset.contigmap.keySet()) {
					Sequence c = geneset.contigmap.get(cstr);
					//c.count = 0;
					c.loc = 0.0;
				}

				if( table.getModel() == defaultModel ) {
					for (Gene g : geneset.genelist) {
						Tegeval tv = g.tegeval;
							// int first = tv.cont.indexOf('_');
							// int sec = tv.cont.indexOf('_',first+1);
						Sequence cont = tv.getContshort(); // tv.cont.substring(0,sec);
						if( cont != null && geneset.contigmap.containsKey(cont.getName()) ) {
							Sequence c = geneset.contigmap.get(cont.getName());
							//c.count++;
							int val = table.convertRowIndexToView(g.index);
							c.loc += (double) val;
						}
					}
				}
				for( JSplitPane gsplitpane : splitpaneList ) {
					gsplitpane.repaint();
				}
			}
		});

		ftable = new JTable() {
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
						// GeneSetHead.this.getAppletContext().
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
		fpopup.addSeparator();
		fpopup.add( new AbstractAction("Excel report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Workbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet("enzyme");
				int k = 0;
				int[] rr = ftable.getSelectedRows();
				for( int r : rr ) {
					//String ec = (String)ftable.getValueAt(r, 1);
					//String go = (String)ftable.getValueAt(r, 0);
					
					int i = ftable.convertRowIndexToModel(r);
					Function f = geneset.funclist.get(i);
					for( GeneGroup gg : f.getGeneGroups() ) {
						for( String spec : gg.getSpecies() ) {
							Teginfo ti = gg.getGenes(spec);
							
							Row 	row = sheet.createRow(k++);
							Cell 	ecell = row.createCell(0);
							ecell.setCellValue( "EC:"+f.ec );
							Cell 	ncell = row.createCell(1);
							ncell.setCellValue( f.name );
							Cell 	spell = row.createCell(2);
							spell.setCellValue( spec );
							Cell 	seqcell = row.createCell(3);
							seqcell.setCellValue( ti.tset.size() );
						}
						/*for( Gene g :gg.genes ) {
							Row 	row = sheet.createRow(k++);
							Cell 	ecell = row.createCell(0);
							ecell.setCellValue( "EC:"+f.ec );
							Cell 	ncell = row.createCell(1);
							ncell.setCellValue( f.name );
							Cell 	spell = row.createCell(2);
							spell.setCellValue( g.getSpecies() );
							Cell 	seqcell = row.createCell(3);
							seqcell.setCellValue( g.tegeval.getAlignedSequence().toString() );
						}*/
					}
					sheet.createRow(k++);
				}
				
				try {
					workbook.write( new FileOutputStream("/u0/excel.xlsx") );
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		ftable.setComponentPopupMenu(fpopup);

		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Show KEGG pathway") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel( r );
				GeneGroup gg = geneset.allgenegroups.get(i);
				
				Map<String,String> env = new HashMap<String,String>();
				env.put("create", "true");
				
				String uristr = "jar:" + geneset.zippath.toUri();
				URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
				final List<Path>	lbi = new ArrayList<Path>();
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
						for( Gene g : gg.genes ) {
							if( g.keggpathway != null ) {
								String sub = g.keggpathway.substring(0,3);
								Path subf = root.resolve(sub);
								if( Files.exists(subf) ) {
									String[] split = g.keggpathway.split(" ");
									for( String s : split ) {
										Path pimg = subf.resolve(s+".png");
										if( Files.exists(pimg) ) {
											showKeggPathway( sub, pimg );
										}
									}
								}
							}
						}
						break;
					}
					geneset.zipfilesystem.close();
				} catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Plasmid") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel( r );
				Gene g = geneset.genelist.get(i);
				Sequence contig = g.tegeval.getContshort();
				String contigstr = contig.toString();
				contig.plasmid = !geneset.plasmids.contains( contigstr );
				if( contig.plasmid ) geneset.plasmids.add( contigstr );
				else geneset.plasmids.remove( contigstr );
				
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					//fs = FileSystems.newFileSystem( uri, env );
					//FileSystem fs = FileSystems.newFileSystem(uri, env);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for( String contigname : geneset.plasmids ) {
				    	baos.write( (contigname + "\n").getBytes() );
					}
					
					Path nf = geneset.zipfilesystem.getPath("/plasmids.txt");
					long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
					//System.err.println( "eeerm " + bl );
					geneset.zipfilesystem.close();
				    
				    /*Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
				    for( String phage : phageset ) {
				    	writer.write( phage + "\n" );
				    }
				    writer.close();*/
				    
				    
				    //writer.write("hello");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Designate") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>	descombo = new JComboBox<String>( geneset.deset.toArray( new String[geneset.deset.size()] ) );
				descombo.setEditable( true );
				JOptionPane.showMessageDialog(GeneSetHead.this, descombo);
				String val = descombo.getSelectedItem().toString();
				geneset.deset.add( val );
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel( r );
					Gene g = geneset.genelist.get(i);
					g.tegeval.designation = val;
					if( g.id != null ) {
						geneset.designations.put( g.id, val );
					} else {
						System.err.println( g.refid );
					}
					//ta.append( g.tegeval.id + "\n" );
				}
				
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					//fs = FileSystems.newFileSystem( uri, env );
					//FileSystem fs = FileSystems.newFileSystem(uri, env);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for( String geneid : geneset.designations.keySet() ) {
						String design = geneset.designations.get( geneid );
				    	baos.write( (geneid + "\t" + design + "\n").getBytes() );
					}
					
					Path nf = geneset.zipfilesystem.getPath("/designations.txt");
					long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING);
					//System.err.println( "eeerm " + bl );
					geneset.zipfilesystem.close();
				    
				    /*Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
				    for( String phage : phageset ) {
				    	writer.write( phage + "\n" );
				    }
				    writer.close();*/
				    
				    
				    //writer.write("hello");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				/*JFrame frame = new JFrame("Ids");
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				JTextArea	ta = new JTextArea();
				JScrollPane sp = new JScrollPane( ta );
				frame.add( sp );
				
				frame.setVisible( true );*/
			}
		});
		popup.add( new AbstractAction("KO to name") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	koids = new HashSet<String>();
				for( Gene g : geneset.genelist ) {
					if( g.koid != null && g.koid.length() > 0 && !geneset.ko2name.containsKey( g.koid ) ) koids.add( g.koid );
				}
				
				try {
					Map<String,String>	ko2name = new HashMap<String,String>();
					int cnt = 0;
					for( String koid : koids ) {
						URL url = new URL("http://www.kegg.jp/dbget-bin/www_bget?ko:"+koid);
						InputStream is = url.openStream();
						StringBuilder sb = new StringBuilder();
						BufferedReader br = new BufferedReader( new InputStreamReader(is) );
						String line = br.readLine();
						while( line != null ) {
							sb.append( line );
							line = br.readLine();
						}
						br.close();
						
						int i = sb.indexOf("<nobr>Name</nobr>");
						if( i != -1 ) {
							int k = sb.indexOf(":hidden\">");
							if( k != -1 ) {
								String koname = sb.substring(k+9, sb.indexOf("<br>", k) );
								ko2name.put( koid, koname );
								
								System.err.println( koid + "\t" + koname );
							}
						}
						
						//System.err.println( ko2name.size() + " " + koids.size() );
						//if( cnt++ > 20 ) break;
					}
					
					FileWriter fw = new FileWriter("c:/ko2name.txt");
					for( String koid : ko2name.keySet() ) {
						fw.write( koid + "\t" + ko2name.get(koid) + "\n" );
					}
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Gene gain/loss") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<Node,List<GeneGroup>>	nodeGainMap = new HashMap<Node,List<GeneGroup>>();
				Map<Node,List<GeneGroup>>	nodeLossMap = new HashMap<Node,List<GeneGroup>>();
				
				/*String treestr = "";
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( applet ) == JFileChooser.APPROVE_OPTION ) {
					File file = fc.getSelectedFile();
					try {
						byte[] bb = Files.readAllBytes( Paths.get(file.toURI()) );
						treestr = new String( bb );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}*/
				
				Serifier 	serifier = getConcatenatedSequences( false, true );
				String 		tree = serifier.getFastTree( serifier.lseq, geneset.user, false );
				
				TreeUtil 	tu = new TreeUtil();
				Node 		n = tu.parseTreeRecursive( tree, false );
				
				TableModel model = new TableModel() {
					@Override
					public int getRowCount() {
						return geneset.getSpecies().size();
					}

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public String getColumnName(int columnIndex) {
						return null;
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
						return geneset.getSpecies().get( rowIndex );
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				JTable table = new JTable( model );
				table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				JScrollPane	scroll = new JScrollPane( table );
				FlowLayout flowlayout = new FlowLayout();
				JComponent c = new JComponent() {};
				c.setLayout( flowlayout );
				c.add( scroll );
				JOptionPane.showMessageDialog(comp, c);
				
				List<String>	rootgroup = new ArrayList<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					rootgroup.add( (String)table.getValueAt(r, 0) );
				}
				
				//String[] sobj = {"mt.ruber", "mt.silvanus", "o.profundus", "m.hydrothermalis"};
				Node newnode = tu.getParent( n, new HashSet<String>( rootgroup ) );
				tu.rerootRecur( n, newnode );
				
				File f = new File("/home/sigmar/gain_list.txt");
				try {
					PrintStream ps = new PrintStream( f );
					geneset.assignGain( newnode, nodeGainMap, ps );
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
				f = new File("/home/sigmar/loss_list.txt");
				try {
					PrintStream ps = new PrintStream( f );
					geneset.assignLoss( newnode, nodeLossMap, ps );
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Concatenate tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Serifier serifier = getConcatenatedSequences( false, true );
				
				boolean succ = true;
				if( comp instanceof Applet ) {
					try {
						JSObject win = JSObject.getWindow( (Applet)comp );
						StringWriter sw = new StringWriter();
						serifier.writeFasta(serifier.lseq, sw, null);
						sw.close();
						win.call("fasttree", new Object[] { sw.toString() });
					} catch( NoSuchMethodError | Exception e1 ) {
						e1.printStackTrace();
						succ = false;
					}
				}
				
				/*if( !succ ) {
					String 				tree = serifier.getFastTree();
					if( cs.connections().size() > 0 ) {
			    		cs.sendToAll( tree );
			    	} else if( Desktop.isDesktopSupported() ) {
			    		cs.message = tree;
			    		//String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
			    		String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:8887";
						try {
							Desktop.getDesktop().browse( new URI(uristr) );
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
			    	}
					System.err.println( tree );
				}*/
				showAlignedSequences( comp, serifier );
			}
		});
		popup.add( new AbstractAction("Majority rule consensus") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Serifier serifier = new Serifier();
				
				Set<GeneGroup>	genegroups = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				if( table.getModel() == groupModel ) {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = geneset.genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				
				TreeUtil treeutil = new TreeUtil();
				Map<Set<String>,NodeSet> nmap = new HashMap<Set<String>,NodeSet>();
				for( GeneGroup ggroup : genegroups ) {
					//List<Sequence>	seqlist = new ArrayList<Sequence>();
					
					for( Tegeval tv : ggroup.getTegevals() ) {
						String spec = tv.getContshort().getSpec();
						Sequence seq = tv.getAlignedSequence();
						
						//Sequence seq = new Sequence( spec, null );
						//if( seqstr != null && seqstr.length() > 0 ) seq.append( seqstr );
						serifier.addSequence( seq );			
					}

					String tree = serifier.getFastTree( serifier.lseq, geneset.user, false );
					Node n = treeutil.parseTreeRecursive( tree, false );
					treeutil.setLoc( 0 );
					n.nodeCalcMap( nmap );
				}
		    	
				Node guidetree = null;
				
				/*********************************** Serifier serifier = getConcatenatedSequences();
				String tree = serifier.getFastTree();
				guidetree = treeutil.parseTreeRecursive( tree, false );*/
				
				Node root = DataTable.majoRuleConsensus(treeutil, nmap, guidetree, false);
				String tree = root.toString();
				
				if( geneset.cs.connections().size() > 0 ) {
					geneset.cs.sendToAll( tree );
		    	} else if( Desktop.isDesktopSupported() ) {
		    		geneset.cs.message = tree;
		    		//String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
		    		String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:8887";
					try {
						Desktop.getDesktop().browse( new URI(uristr) );
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
		    	}
			}
		});
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
		ftablemodel = new TableModel() {
			@Override
			public int getRowCount() {
				return geneset.funclist.size();
			}

			@Override
			public int getColumnCount() {
				return 9;
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
					return "Function coverage";
				else if (columnIndex == 5)
					return "Number of proteins";
				else if (columnIndex == 6)
					return "Name";
				else if (columnIndex == 7)
					return "Namespace";
				else if (columnIndex == 8)
					return "Def";
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 4 || columnIndex == 5 )
					return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Function func = geneset.funclist.get(rowIndex);
				if( columnIndex == 0 )
					return func.go;
				else if( columnIndex == 1 )
					return func.ec;
				else if( columnIndex == 2 )
					return func.metacyc;
				else if( columnIndex == 3 )
					return func.kegg;
				else if( columnIndex == 4 )
					return func.getSpeciesCount();
				else if( columnIndex == 5 )
					return table.getModel() == groupModel ? func.getGroupSize() : func.getGeneCount();
				else if( columnIndex == 6 )
					return func.name;
				else if( columnIndex == 7 )
					return func.namespace;
				else if( columnIndex == 8 )
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
		};
		ftable.setModel( ftablemodel );
		fscrollpane.setViewportView(ftable);

		updateFilter(ftable, rowfilter, null);
		updateFilter(table, genefilter, label);

		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String sel = (String) e.getItem();
				filterset.clear();
				if (geneset.pathwaymap.containsKey(sel)) {
					Set<String> enz = geneset.pathwaymap.get(sel);
					for (Function f : geneset.funclist) {
						if (f.ec != null && enz.contains(f.ec)) {
							filterset.add(f.index);
						}
					}
				}
				updateFilter(ftable, rowfilter, null);
			}
		});

		specombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String sel = (String) e.getItem();
				genefilterset.clear();
				for (Gene g : geneset.genelist) {
					Tegeval tv = g.tegeval;
					if (sel.equals(tv.teg)) {
						//System.out.println(g.name + " " + sp + " " + sel + "  " + tv.eval);
						genefilterset.add(g.index);
					}
				}
				updateFilter(table, genefilter, label);
			}
		});

		fpopup.add(new AbstractAction("Find conserved terms") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Integer> res = new HashSet<Integer>();
				for (Function f : geneset.funclist) {
					if (f.getGeneGroups() != null) {
						Set<String> check = new HashSet<String>();
						for( GeneGroup g : f.getGeneGroups() ) {
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
				updateFilter(ftable, rowfilter, null);
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
					Function f = geneset.funclist.get(fr);
					if( table.getModel() == groupModel ) {
						Set<GeneGroup> sset = f.getGeneGroups();
						if( sset != null ) for (GeneGroup gg : sset) {
							//Gene g = genemap.get(s);
							genefilterset.add(gg.index);
						}
					} else {
						Set<Gene> sset = f.getGeneentries();
						for (Gene g : sset) {
							//Gene g = genemap.get(s);
							genefilterset.add(g.index);
						}
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
						if( table.getModel() == groupModel ) {
							GeneGroup gg = geneset.allgenegroups.get(cr);
							for( Function f : gg.getFunctions() ) {
								try {
									int rf = ftable.convertRowIndexToView(f.index);
									if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
								} catch( Exception ex ) {
									ex.printStackTrace();
								}
							}
						} else {
							Gene g = geneset.genelist.get(cr);
							if (g.funcentries != null) {
								for( Function f : g.funcentries) {
									//Function f = funcmap.get(go);
									try {
										int rf = ftable.convertRowIndexToView(f.index);
										if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
									} catch( Exception ex ) {
										ex.printStackTrace();
									}
								}
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
					updateFilter(ftable, rowfilter, null);
				}
			}
		});

		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					genefilterset.clear();
					updateFilter(table, genefilter, label);
					geneset.scrollToSelection( table );
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
					if( table.getModel() == groupModel ) {
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							GeneGroup gg = geneset.allgenegroups.get(cr);
							for( Function f : gg.getFunctions() ) {
								filterset.add(f.index);
							}
						}
					} else {
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							Gene g = geneset.genelist.get(cr);
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
					}
					updateFilter(ftable, rowfilter, null);
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

						Function f = geneset.funclist.get(cr);
						if (f.getGeneentries() != null) {
							if( table.getModel() == groupModel ) {
								for( Gene g : f.getGeneentries() ) {
									GeneGroup gg = g.getGeneGroup();
									if( gg != null ) genefilterset.add( gg.getIndex() );
								}
							} else {
								for( Gene g : f.getGeneentries() ) {
									//Gene g = genemap.get(ref);
									// int rf = table.convertRowIndexToView( g.index
									// );
									// table.addRowSelectionInterval(rf, rf);
									genefilterset.add(g.index);
								}
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
						Function f = geneset.funclist.get(cr);
						if( f.getGeneentries() != null ) {
							for( Gene g : f.getGeneentries() ) {
								//Gene g = genemap.get(ref);
								int i = g.getGroupIndex();
								if( i >= 0 && i <= table.getRowCount() ) {
									int rf = table.convertRowIndexToView(i);
									table.addRowSelectionInterval(rf, rf);
								}
							}
						}
					}
				}
				ftableisselecting = false;
			}
		});
		
		textfield.addKeyListener( new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				String text = textfield.getText().toLowerCase();
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, searchi, e.isAltDown(), 8, 9, 10, 16 ) : searchTable( table, text, searchi, e.isAltDown(), 0 );
				}
			}
		});

		textfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(0, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					else updateFilter(0, text, table, genefilter, genefilterset, label, 0 );
				} else {
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void insertUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(1, text, table, genefilter, genefilterset, label, 8, 9, 10, 16);
					else updateFilter(1, text, table, genefilter, genefilterset, label, 0);
				} else {
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void removeUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(2, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					else updateFilter(2, text, table, genefilter, genefilterset, label, 0);
				} else {
					searchi = searchTable( table, text, 0, false, searchcolcomb.getSelectedItem().equals("Symbol") ? 7 : 0 );
				}
			}
		});

		ftextfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateFilter(0, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}
		});
		popup.add(new AbstractAction("KEGG gene lookup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if (r != -1) {
					int rr = table.convertRowIndexToModel(r);
					Gene g = geneset.genelist.get(rr);
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
		popup.add(new AbstractAction("Show genes with same sharing") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int r = table.getSelectedRow();
				if (r >= 0) {
					int cr = table.convertRowIndexToModel(r);
					GeneGroup gg = geneset.allgenegroups.get(cr);
					for (GeneGroup g : geneset.allgenegroups) {
						if (gg.species != null && g.species != null) {
							Set<String> ggset = gg.species.keySet();
							Set<String> gset = g.species.keySet();

							if (gset.size() == ggset.size() && gset.containsAll(ggset)) {
								genefilterset.add(g.index);
							}
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
						Gene gg = geneset.genelist.get(cr);
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
						GeneGroup gg = geneset.allgenegroups.get(cr);
						Set<Function> fset = gg.getFunctions();
						if( startfunc.isEmpty() ) {
							startfunc.addAll( fset );
						} else {
							startfunc.retainAll( fset );
						}
					}
				}
				for( Function f : geneset.funclist ) {
					filterset.add( f.index );
				}
				updateFilter(ftable, rowfilter, null);
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
						Gene gg = geneset.genelist.get(cr);
						if (gg.funcentries != null) {
							for( Function f : gg.funcentries ) {
								filterset.add( f.index );
							}
						}
					} else {
						GeneGroup gg = geneset.allgenegroups.get(cr);
						Set<Function> fset = gg.getFunctions();
						for( Function f : fset ) {
							filterset.add( f.index );
						}
					}
				}
				updateFilter(ftable, rowfilter, null);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show gene groups in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				proxi(table, rr, geneset.genelist, genefilterset, false, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Select gene groups in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				proxi(table, rr, geneset.genelist, genefilterset, false, false);
				for( int i : genefilterset ) {
					int r = table.convertRowIndexToView( i );
					if( r != -1 ) {
						table.addRowSelectionInterval( r, r );
					}
				}
				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				if (label != null) label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
				//updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Select genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				proxi(table, rr, geneset.genelist, genefilterset, false, true);
				for( int i : genefilterset ) {
					int r = table.convertRowIndexToView( i );
					if( r != -1 ) {
						table.addRowSelectionInterval( r, r );
					}
				}
				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				if (label != null) label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
				//updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Add gene groups in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				proxi(table, rr, geneset.genelist, genefilterset, false, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Remove gene groups in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<Integer>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < geneset.genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				}
				proxi(table, rr, geneset.genelist, genefilterset, true, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.addSeparator();
		popup.add(new AbstractAction("Show related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				relati(table, rr, geneset.genelist, genefilterset, geneset.uclusterlist, false);
				updateFilter(table, genefilter, label);
			}
		});
		popup.add(new AbstractAction("Add related genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				relati(table, rr, geneset.genelist, genefilterset, geneset.uclusterlist, false);
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
					for (int i = 0; i < geneset.genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				}
				relati(table, rr, geneset.genelist, genefilterset, geneset.uclusterlist, true);
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
					Gene gg = geneset.genelist.get(cr);
					// genefilterset.add( gg.index );
					Tegeval tv = gg.tegeval;
					for (Set<String> uset : geneset.iclusterlist) {
						if (uset.contains(tv.name)) {
							ct.addAll(uset);
							break;
						}
					}
				}

				for (Gene g : geneset.genelist) {
					Tegeval tv = g.tegeval;
					if (ct.contains(tv.name)) {
						genefilterset.add(g.index);
						break;
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
				Gene gg = geneset.genelist.get(cr);
				if (gg.getSpecies() != null) {
					for (String s : geneset.corrInd) {
						if (s.equals(geneset.corrInd.get(0)))
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
					treeutil.neighborJoin( newcorr, geneset.corrInd, null, true, true );
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
		 * for( Gene g : geneset.genelist ) { if( g.species != null ) { for( String key
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
	
	private static void relati(JTable table, int[] rr, List<Gene> genelist, Set<Integer> genefilterset, List<Set<String>> uclusterlist, boolean remove) {
		Set<String> ct = new HashSet<String>();
 		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			GeneGroup ggroup = gg.getGeneGroup();
			for( Gene g : ggroup.genes ) {
				ct.add( g.refid );
			}
			// genefilterset.add( gg.index );
			/*Tegeval tv = gg.tegeval;
				//System.err.println( tv.cont );
				//int li = tv.cont.lastIndexOf('_');
				//String tvshort = tv.contshort+tv.cont.substring(li);
			for (Set<String> uset : uclusterlist) {
				if (uset.contains(tv.cont)) {
					System.err.println( uset );
					ct.addAll(uset);
					break;
				}
			}*/
		}

		for (Gene g : genelist) {
			//Tegeval tv = g.tegeval;
				//System.err.println( tv.cont );
				//int li = tv.cont.lastIndexOf('_');
				//String tvshort = tv.contshort+tv.cont.substring(li);
			if( ct.contains(g.refid) ) {
				if (remove)
					genefilterset.remove( genelist.indexOf( g ) );
				else
					genefilterset.add( genelist.indexOf( g ) );
				//break;
			}
		}
		System.err.println( genefilterset.size() + "  " + ct.size() );
	}
	
	private JComponent newSoft(JButton jb, Container comp, SwingNode upper, SwingNode lower, JApplet applet, JComboBox selcomblocal) throws IOException {
		/*InputStream nis2 = GeneSet.class.getResourceAsStream("/exp_short.blastout");
		BufferedReader br2 = new BufferedReader( new InputStreamReader(nis2) );
		String line2 = br2.readLine();
		br2.close();*/
		// aas.clear();

		// return new JComponent() {};
		return showGeneTable(null, jb, upper, lower, comp, applet, selcomblocal);// clustInfoMap// );
	}
	
	public void init() {
		init(this, null, null, null);
	}
	
	public void exportGenomes( Map<String,List<Sequence>> speccontigMap ) {
		JCheckBox	joincontigs = new JCheckBox("Join contigs");
		JCheckBox	translations = new JCheckBox("Include translations");
		JComponent[] comps = new JComponent[] { joincontigs, translations };
		List scl = getSelspecContigs( Arrays.asList( comps ), speccontigMap );
		
		if( scl.get(0) instanceof String ) {
			List<String> lspec = (List<String>)scl;
			
			JFileChooser filechooser = new JFileChooser();
			filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				for( String spec : lspec ) {
					Map<String,List<Annotation>> mapan = new HashMap<String,List<Annotation>>();
					List<Sequence> contigs = geneset.speccontigMap.get(spec);
					Serifier serifier = new Serifier();
					for( Sequence c : contigs ) {
						serifier.addSequence(c);
						serifier.mseq.put(c.getName(), c);
						
						List<Annotation> lann = new ArrayList<Annotation>();
						if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
							Tegeval tv = (Tegeval)ann;
							GeneGroup gg = tv.getGene().getGeneGroup();
							
							Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, gg != null ? gg.getCommonName() : tv.getGene().getName() );
							anno.type = "CDS";
							anno.id = tv.getGene().id;
							
							String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
							if( cazy != null ) anno.addDbRef( "CAZY:"+cazy );
							Cog cog = gg != null ? gg.getCommonCog(geneset.cogmap) : null;
							if( cog != null ) anno.addDbRef( "COG:"+cog.id );
							String ec = gg != null ? gg.getCommonEc() : null;
							if( ec != null ) anno.addDbRef( "EC:"+ec );
							
							lann.add( anno );
						}
						mapan.put( c.getName(), lann );
					}
					Sequences s = new Sequences(null,spec,"nucl",null,contigs.size());
					try {
						serifier.writeGenebank( filechooser.getSelectedFile().toPath().resolve(spec+".gbk"), !joincontigs.isSelected(), translations.isSelected(), s, mapan);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Map<String,List<Annotation>> mapan = new HashMap<String,List<Annotation>>();
			List<Sequence> contigs = (List<Sequence>)scl;
			Serifier serifier = new Serifier();
			for( Sequence c : contigs ) {
				serifier.addSequence(c);
				serifier.mseq.put(c.getName(), c);
				
				List<Annotation> lann = new ArrayList<Annotation>();
				if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
					Tegeval tv = (Tegeval)ann;
					GeneGroup gg = tv.getGene().getGeneGroup();
					
					Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori,  gg != null ? gg.getCommonName() : tv.getGene().getName() );
					anno.type = "CDS";
					anno.id = tv.getGene().id;
					
					String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
					if( cazy != null ) anno.addDbRef( "CAZY:"+cazy );
					lann.add( anno );
				}
				mapan.put( c.getName(), lann );
			}
			Sequences s = new Sequences(null,contigs.get(0).getSpec(),"nucl",null,contigs.size());
			
			JFileChooser filechooser = new JFileChooser();
			if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				try {
					serifier.writeGenebank( filechooser.getSelectedFile().toPath(), !joincontigs.isSelected(), translations.isSelected(), s, mapan);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//serifier.addSequences(seqs);
		}
	}
	
	public void exportProteinSequences( List<Gene> genelist ) {
		JFileChooser filechooser = new JFileChooser();
		if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			try {
				Path dbPath = filechooser.getSelectedFile().toPath();
				BufferedWriter bw = Files.newBufferedWriter(dbPath);
				for( Gene g : geneset.genelist ) {
					if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
						g.getFasta( bw, true );
						/*StringBuilder sb = g.tegeval.getProteinSequence();
						if( sb.toString().contains("0") ) {
							System.err.println( g.id );
						}
						bw.append(">" + g.id + "\n");
						for (int i = 0; i < sb.length(); i += 70) {
							bw.append( sb.substring(i, Math.min(i + 70, sb.length())) + "\n");
						}*/
					}
				}
				bw.close();
				//serifier.writeGenebank( filechooser.getSelectedFile(), !joincontigs.isSelected(), translations.isSelected(), s, mapan);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void exportGeneClusters( List<GeneGroup> allgenegroups ) {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			try {
				Path dbPath = filechooser.getSelectedFile().toPath();
				for( GeneGroup gg : allgenegroups ) {
					if( gg != null && gg.getCommonTag() == null ) {
						Path file = dbPath.resolve( gg.getCommonId()+".aa" );
						BufferedWriter bw = Files.newBufferedWriter( file );
						gg.getFasta( bw, true );
						bw.close();
						/*StringBuilder sb = g.tegeval.getProteinSequence();
						if( sb.toString().contains("0") ) {
							System.err.println( g.id );
						}
						bw.append(">" + g.id + "\n");
						for (int i = 0; i < sb.length(); i += 70) {
							bw.append( sb.substring(i, Math.min(i + 70, sb.length())) + "\n");
						}*/
					}
				}
				//serifier.writeGenebank( filechooser.getSelectedFile(), !joincontigs.isSelected(), translations.isSelected(), s, mapan);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showKeggPathway( String sub, Path p ) throws IOException {
		final BufferedImage selimg = ImageIO.read( Files.newInputStream( p ) );
		if( selimg != null ) {
			JFrame 	frame = new JFrame( sub );
			frame.setSize(800, 600);
			final JComponent c = new JComponent() {
				public void paintComponent( Graphics g ) {
					super.paintComponent(g);
					g.drawImage(selimg, 0, 0, this);
				}
			};
			Dimension dim = new Dimension( selimg.getWidth(), selimg.getHeight() );
			c.setSize(dim);
			c.setPreferredSize(dim);
			JScrollPane sc2 = new JScrollPane( c );
			frame.add( sc2 );
			frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			frame.setVisible(true);
		}
	}
	
	public List getSelspecContigs( List<JComponent> complist, final Map<String,List<Sequence>> speccontigMap, final String ... selspec ) {
		List<Sequence>				contigs = null;
		final List<String>			specs = new ArrayList<String>( speccontigMap.keySet() );
		final JTable				stable = new JTable();
		stable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		final TableModel					stablemodel = new TableModel() {
			@Override
			public int getRowCount() {
				return specs.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Species";
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
				if( rowIndex >= 0 && rowIndex < specs.size() ) return specs.get(rowIndex);
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex,	int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		stable.setModel( stablemodel );
		
		final JTable				ctable = new JTable();
		ctable.setAutoCreateRowSorter( true );
		final TableModel			ctablemodel = new TableModel() {
			@Override
			public int getRowCount() {
				int 			r = stable.getSelectedRow();
				String 			spec = selspec.length > 0 ? selspec[0] : (String)stable.getValueAt(r, 0);
				if( spec != null ) {
					List<Sequence>	contigs = geneset.speccontigMap.get( spec );
					return contigs.size();
				}
				return 0;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Contigs";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Sequence.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int 		r = stable.getSelectedRow();
				String 		spec = selspec.length > 0 ? selspec[0] : (String)stable.getValueAt(r, 0);
				List<Sequence>	contigs = geneset.speccontigMap.get( spec );
				return contigs.get(rowIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex,	int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		ctable.setModel( ctablemodel );
		
		JScrollPane	sscrollpane = new JScrollPane( stable );
		JScrollPane	cscrollpane = new JScrollPane( ctable );
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		if( selspec.length == 0 ) c.add( sscrollpane );
		c.add( cscrollpane );
		
		if( complist != null ) {
			for( JComponent comp : complist ) {
				c.add( comp );
			}
		}
		
		stable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ctable.tableChanged( new TableModelEvent( ctablemodel ) );
			}
		});
		JOptionPane.showMessageDialog(this, c);
		
		int[] rr = stable.getSelectedRows();
		if( rr.length > 1 ) {
			List slist = new ArrayList();
			for( int r : rr ) {
				int i = stable.convertRowIndexToModel(r);
				slist.add( specs.get(i) );
			}
			
			return slist;
		} else {
			int 			sr = stable.getSelectedRow();
			String 			spec = selspec.length > 0 ? selspec[0] : (String)stable.getValueAt(sr, 0);
			if( spec != null ) {
				List<Sequence> ctgs = geneset.speccontigMap.get( spec );
				rr = ctable.getSelectedRows();
				contigs = new ArrayList<Sequence>();
				for( int r : rr ) {
					int i = ctable.convertRowIndexToModel(r);
					contigs.add( ctgs.get(i) );
				}
			}
			
			return contigs;
		}
	}
}
