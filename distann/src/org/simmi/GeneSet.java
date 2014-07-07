package org.simmi;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
import javax.swing.JPanel;
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

import netscape.javascript.JSObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.Annotation;
import org.simmi.shared.Erm;
import org.simmi.shared.Sequence;
import org.simmi.shared.Sequences;
import org.simmi.shared.Serifier;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.shared.TreeUtil.NodeSet;
import org.simmi.signed.NativeRun;
import org.simmi.unsigned.JavaFasta;
import org.simmi.unsigned.SmithWater;

import flobb.ChatServer;

public class GeneSet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 * @throws IOException
	 */
	
	static SerifyApplet currentSerify = null;

	/*private static StringBuilder dnaSearch(String query) {
		/*
		 * aquery.name = query; int ind = Arrays.binarySearch(aas, aquery); if(
		 * ind < 0 ) { System.err.println(); } return ind < 0 ? null : aas[ ind
		 * ].aas;
		 *

		if (dnaa.containsKey(query))
			return dnaa.get(query);
		return null;
	}*/
	
	public static void recursiveSet(int fin, int val) {
		if (val < fin) {
			recursiveSet(fin, val + 1);
		} else {

		}
		
		//javax.web
	}

	public CharSequence trimSubstring(StringBuilder ac, String sb) {
		int first, last;

		for (first = 0; first < sb.length(); first++)
			if (!Character.isWhitespace(sb.charAt(first)))
				break;

		for (last = sb.length(); last > first; last--)
			if (!Character.isWhitespace(sb.charAt(last - 1)))
				break;

		return ac.append(sb, first, last);
	}
	
	public void loadcazymap( Map<String,String> cazymap, Reader rd ) throws IOException {
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String id = null;
		String hit = null;
		String evalue = null;
		while( line != null ) {
			if( line.startsWith("Query:") ) {
				if( hit != null && id != null ) {
					cazymap.put( id, hit );
				}
				String[] split = line.split("[\t ]+");
				
				//line = br.readLine();
				//int k = line.lastIndexOf('[');
				id = split[1]; //line.substring( k+1, line.indexOf(']', k+1) ).trim()+"_"+split[1].trim();
				
				if( id.contains("..") ) {
					line = br.readLine();
					int k = line.indexOf('[');
					if( k != -1 ) {
						int u = line.indexOf(']',k+1);
						String spec = line.substring(k+1,u);
						id = spec+"_"+id;
					} //else id = 
				}
				
				hit = null;
				evalue = null;
			} else if( hit == null && line.trim().startsWith("E-value") ) {
				line = br.readLine();
				line = br.readLine().trim();
				if( line.startsWith("--") ) line = br.readLine().trim();
				String[] split = line.split("[ ]+");
				evalue = split[0];
			} else if( hit == null && line.startsWith(">>") ) {
				hit = line.substring( 3, line.indexOf('.') ) + "("+evalue+")";
			}
			
			line = br.readLine();
		}
		
		if( hit != null && id != null ) {
			cazymap.put( id, hit );
		}
	}
	
	public Map<String,Cog> loadcogmap( Reader rd ) throws IOException {
		Map<String,Cog>	map = new HashMap<String,Cog>();
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String current = null;
		String id = null;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				current = line.substring(7);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					current += line;
					line = br.readLine();
				}
				current = current.trim();
				String lname = current;
				
				int i = lname.lastIndexOf('[');
				if( i == -1 ) {
					//i = lname.indexOf("contig");
					//if( i == -1 ) i = lname.indexOf("scaffold");
					
					int u = lname.indexOf(' ');
					if( u == -1 ) u = lname.length();
					id = lname.substring(0,u);
				} else {
					int u = lname.indexOf(' ');
					int k = lname.indexOf(']', i+1);
					id = lname.substring(0, u);
					if( id.contains("..") ) {
						id = lname.substring(i+1, k) + "_" + id;
					}
				}
			} else if( line.startsWith(">") ) {
				String val = line.substring(1);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					val += line;
					line = br.readLine();
				}
				val = val.trim();
				
				int i = current.lastIndexOf('[');
				int n = current.indexOf(']', i+1);
				
				if( i == -1 || n == -1 ) {
					n = current.indexOf(" ");
					if( n == -1 ) n = current.length();
				}
				
				/*if( i == -1 || n == -1 ) {
					System.err.println( val );
				}*/ //Write materials and methods, insert new genomes into preexisting zip file, laga annoteringu (JGI vs NCBI).
				
				String spec = current.substring(i+1, n);
				int k = spec.indexOf("_contig");
				if( k == -1 ) {
					k = spec.indexOf("_uid");
					k = spec.indexOf('_', k+4);
				}
				
				if( k == -1 ) {
					k = spec.indexOf('_');
					k = spec.indexOf('_', k+1);
				}
				if( k != -1 ) spec = spec.substring(0, k);
				if( !spec.contains("_") ) {
					System.err.println();
				}
				
				i = val.indexOf('[');
				n = val.indexOf(']', i+1);
				/*if( i == -1 || n == -1 ) {
					System.err.println( val );
				}*/
				String cog = val.substring(i+1, n);
				int u = cog.indexOf('/');
				if( u != -1 ) cog = cog.substring(0, u);
				u = cog.indexOf(';');
				if( u != -1 ) cog = cog.substring(0, u);
				String erm = cog.replace("  ", " ");
				while( !erm.equals( cog ) ) {
					cog = erm;
					erm = cog.replace("  ", " ");
				}
				cog = cog.trim();
				
				int ci = val.indexOf(" COG");
				int ce = val.indexOf(',', ci+1);
				String cogid = val.substring(ci+1, ce);
				
				ci = val.lastIndexOf(" COG");
				ce = val.indexOf(',', ci+1);
				String cogan = val.substring(ce+1, i).trim();
				
				cogidmap.put(cogid, cog);
				map.put( id, new Cog( cogid, Cog.cogchar.get(cog), cog, cogan ) );
			}
			line = br.readLine();
		}
		//fr.close();
		
		return map;
	}
	
	public Map<String,String> loadnamemap( BufferedReader br ) throws IOException {
		Map<String,String>	map = new HashMap<String,String>();
		
		//BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		while( line != null ) {
			String[] split = line.split("\t");
			map.put( split[0], split[1] );
			line = br.readLine();
		}
		
		return map;
	}

	public Map<String,String> loadunresolvedmap( Reader rd ) throws IOException {
		Map<String,String>	map = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		String current = null;
		String id = null;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				current = line.substring(7);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					current += line;
					line = br.readLine();
				}
				current = current.trim();
				int i = current.indexOf(' ');
				if( i == -1 ) i = current.length();
				id = current.substring(0, i);
			} else if( line.startsWith(">") ) {
				String val = line.substring(1);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					val += line;
					line = br.readLine();
				}
				
				line = br.readLine();
				line = br.readLine();
				int e = line.indexOf("Expect =");
				int n = line.indexOf(',', e);
				double eval = Double.parseDouble( line.substring(e+9, n).trim() );
				
				if( eval < 1e-5 ) {
					val = val.trim();
					n = val.indexOf(']');
				
					map.put( "MAT"+id, val.substring(0, n+1) );
				}
			}
			line = br.readLine();
		}
		//fr.close();
		
		return map;
	}
	
	public String getGeneName( String selectedItem, Gene gene ) {
		if( selectedItem.equals("Default names") ) {
			String genename = gene.getName();
			//if( commonname.isSelected() && genename.contains("_") ) genename = next.getGene().getGeneGroup().getCommonName();
			return genename.contains("hypothetical") ? "hth-p" : genename;
		} else if( selectedItem.equals("Group names") ) {
			String genename = gene.getGeneGroup() != null ? gene.getGeneGroup().getCommonName() : "";
			//if( genename.contains("_") ) genename = gene.getGeneGroup().getCommonName();
			return genename.contains("hypothetical") ? "hth-p" : genename;
		} else if( selectedItem.equals("Species") ) {
			gene.getSpecies();
		} else if( selectedItem.equals("Ids") ) {
			return gene.id;
		} else if( selectedItem.equals("Refids") ) {
			return gene.refid;
		} else if( selectedItem.equals("Cog") ) {
			Cog cog = gene.getGeneGroup().getCommonCog(cogmap);
			if( cog != null ) return cog.id;
		} else if( selectedItem.equals("Cazy") ) {
			String cazy = gene.getGeneGroup().getCommonCazy(cazymap);
			if( cazy != null ) return cazy;
		}
		return "";
	}
	
	private void loci2aaseq( List<Set<String>> lclust, Map<String,Gene> refmap, Map<String,String> designations ) {
		for( Set<String> clust : lclust ) {
			for( String line : clust ) {
				String cont = line;
				String[] split = cont.split("#");
				String lname = split[0].trim().replace(".fna", "");
				//prevline = line;
				int start = 0;
				int stop = -1;
				int dir = 0;
				
				boolean succ = false;
				int i = 0;
				while( !succ && i+3 < split.length ) {
					succ = true;
					try {
						start = Integer.parseInt(split[i+1].trim());
						stop = Integer.parseInt(split[i+2].trim());
						dir = Integer.parseInt(split[i+3].trim());
					} catch( Exception e ) {
						succ = false;
						lname += split[i+1];
						e.printStackTrace();
					}
					i++;
				}
				
				String contigstr = null;
				String contloc = null;
				
				String origin;
				String id;
				String name;
				i = lname.lastIndexOf('[');
				if( i == -1 ) {
					i = lname.indexOf("contig");
					if( i == -1 ) {
						i = lname.indexOf("scaffold");
					}
					int u = lname.lastIndexOf('_');
					if( u == -1 ) {
						System.err.println();
					}
					contigstr = lname.substring(0, u);
					origin = lname.substring(0, i-1);
					contloc = lname.substring(i, lname.length());
					name = lname;
					id = lname;
				} else {
					int n = lname.indexOf(']', i+1);
					contigstr = lname.substring(i+1, n);
					int u = lname.indexOf(' ');
					id = lname.substring(0, u);
					
					String spec = lname.substring(i+1, n);
					if( id.contains("..") ) {
						id = spec + "_" + id;
					}
					
					name = lname.substring(u+1, i).trim();
					
					u = Contig.specCheck( contigstr );
					
					if( u == -1 ) {
						u = contigIndex( contigstr );
						origin = contigstr.substring(0, u-1);
						contloc = contigstr.substring(u, contigstr.length());
					} else {
						n = contigstr.indexOf("_", u+1);
						if( n == -1 ) n = contigstr.length();
						origin = contigstr.substring(0, n);
						contloc = n < contigstr.length() ? contigstr.substring(n+1) : "";
					}
					
					if( line != null ) {
						i = line.lastIndexOf('#');
						if( i != -1 ) {
							u = line.indexOf(';', i+1);
							if( u != -1 ) {
								id = line.substring(u+1, line.length());
								mu.add( id );
							}
						}
					}
				}
				
				String idstr = null;
				int ids = name.lastIndexOf('(');
				if( ids != -1 ) {
					int eds = name.indexOf(')', ids+1);
					if( eds != -1 ) {
						idstr = name.substring(ids+1,eds);
						name = name.substring(0, ids);
					}
				}
				
				String addname = "";
				String newid = id;
				String neworigin = origin;
				if( unresolvedmap.containsKey(id) ) {
					String map = unresolvedmap.get(id);
					int f = map.indexOf('|');
					int l = map.indexOf('|', f+1);
					int n = map.indexOf('[', l+1);
					int e = map.indexOf(']', n+1);
					if( l != -1 ) newid = map.substring(f+1,l);
					if( n != -1 ) addname = ":" + map.substring(l+1,n).trim();
					if( e != -1 ) neworigin = map.substring(n+1,e).trim();
				} else {
					System.err.print( id );
					System.err.println();
				}
				
				if( !refmap.containsKey(id) ) {
					Tegeval tv = new Tegeval();
					Contig contig;
					if( contigmap.containsKey( contigstr ) ) {
						contig = contigmap.get( contigstr );
					} else {
						 contig = new Contig( contigstr );
					}
					
					tv.init( lname, contig, contloc, start, stop, dir );
					tv.name = line;
					//ac.setName( lname );
					//tv.setAlignedSequence( ac );
					aas.put( lname, tv );
					
					//System.err.println( "erm " + start + "   " + stop + "   " + contig.toString() );
					contig.add( tv );
					
					String newname = (addname.length() == 0 ? name : addname.substring(1)); //name+addname
					Gene gene = new Gene( null, id, newname, origin );
					gene.designation = designations != null ? designations.get( id ) : null;
					gene.refid = newid;
					gene.setIdStr( idstr );
					gene.allids = new HashSet<String>();
					gene.allids.add( newid );
					if( idstr != null ) {
						int ec = idstr.indexOf("EC");
						if( ec != -1 ) {
							//int ecc = name.indexOf(')', ec+1);
							//if( ecc == -1 ) ecc = name.length();
							int k = ec+3;
							if( k < idstr.length() ) {
								char c = idstr.charAt(k);
								while( (c >= '0' && c <= '9') || c == '.' ) {
									c = idstr.charAt( k++ );
									if( k == idstr.length() ) {
										k++;
										break;
									}
								}
								gene.ecid = idstr.substring(ec+2, k-1).trim();
							}
						}
					
						int go = idstr.indexOf("GO:");
						while( go != -1 ) {
							int ngo = idstr.indexOf("GO:", go+1);
							
							if (gene.funcentries == null)
								gene.funcentries = new HashSet<Function>();
							
							String goid;
							if( ngo != -1 ) goid = idstr.substring(go, ngo);
							else {
								int ni = go+10;//Math.minname.indexOf(')', go+1);
								goid = idstr.substring( go, ni );
							}
							Function func;
							if( funcmap.containsKey( goid ) ) {
								func = funcmap.get( goid );
							} else {
								func = new Function( goid );
								funcmap.put( goid, func );
							}
							gene.funcentries.add( func );
							
							go = ngo;
						}
					}
					refmap.put( id, gene );
					
					tv.setGene( gene );
					tv.setTegund( origin );
					
					gene.tegeval = tv;
				}
			}
		}
	}
	
	public static int contigIndex( String lname ) {
		int i = lname.indexOf("contig");
		if( i == -1 ) i = lname.indexOf("scaffold");
		if( i == -1 ) i = lname.lastIndexOf('_')+1;
		return i;
	}
	
	Set<String>	mu = new HashSet<String>();
	private void loci2aasequence(BufferedReader br, Map<String,Gene> refmap, Map<String,String> designations, String filename) throws IOException {
		//BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String lname = null;
		String prevline = null;
		//Sequence ac = new Sequence( null, null );
		Tegeval tv = new Tegeval();
		int start = 0;
		int stop = -1;
		int dir = 0;
		// StringBuffer ac = new StringBuffer();
		// List<Aas> aass = new ArrayList<Aas>();

		//Tegeval preval = null;
		while (line != null) {
			if (line.startsWith(">")) {
				/*if( line.contains("MAT4609") ) {
					System.err.println();
				}*/
				if (tv.getSequenceLength() > 0) {
					String contigstr = null;
					String contloc = null;
					
					String origin;
					String id;
					String name;
					int i = lname.lastIndexOf('[');
					if( i == -1 ) {
						i = lname.indexOf("contig");
						if( i == -1 ) {
							i = lname.indexOf("scaffold");
						}
						int u = lname.lastIndexOf('_');
						if( u == -1 ) {
							System.err.println();
						}
						contigstr = lname.substring(0, u);
						origin = lname.substring(0, i-1);
						contloc = lname.substring(i, lname.length());
						name = lname;
						id = lname;
					} else {
						int n = lname.indexOf(']', i+1);
						contigstr = lname.substring(i+1, n);
						int u = lname.indexOf(' ');
						id = lname.substring(0, u);
						if( id.contains("..") ) {
							id = lname.substring(i+1, n) + "_" + id;
						}
						
						name = lname.substring(u+1, i).trim();
						
						u = Contig.specCheck( contigstr );
						
						if( u == -1 ) {
							/*u = contigstr.indexOf("contig");
							if( u == -1 ) u = contigstr.indexOf("scaffold");
							if( u == -1 ) u = contigstr.lastIndexOf('_')+1;*/
							u = contigIndex( contigstr );
							origin = contigstr.substring(0, u-1);
							contloc = contigstr.substring(u, contigstr.length());
						} else {
							n = contigstr.indexOf("_", u+1);
							if( n == -1 ) n = contigstr.length();
							origin = contigstr.substring(0, n);
							contloc = n < contigstr.length() ? contigstr.substring(n+1) : "";
						}
						
						if( prevline != null ) {
							i = prevline.lastIndexOf('#');
							if( i != -1 ) {
								u = prevline.indexOf(';', i+1);
								if( u != -1 ) {
									id = prevline.substring(u+1, prevline.length());
									mu.add( id );
								}
							}
						}
					}
					
						//i = query.indexOf('_', i);
						//String[] qsplit = query.substring(i+5).split("_");
					
					//int fi = lname.indexOf('_');
					//int li = lname.lastIndexOf('_');
					//contigstr = lname.substring(0, li);// + "_" + qsplit[0];// +"_"+qsplit[2];
																// //&query.substring(0,
																// sec);
						/*int k = 0;
						if (qsplit[1].contains("|")) {
							contig += "_" + qsplit[1];
							k++;
						}*/
					//contloc = lname.substring(fi+1,lname.length());//qsplit[0] + "_" + qsplit[1 + k]; // query.substring(first+1,sec);
					/*} else {
						contig = qsplit[0];
						contloc = qsplit[0] + "_" + qsplit[1];
					}*/
					
					String idstr = null;
					int ids = name.lastIndexOf('(');
					if( ids != -1 ) {
						int eds = name.indexOf(')', ids+1);
						if( eds != -1 ) {
							idstr = name.substring(ids+1,eds);
							name = name.substring(0, ids);
						}
					}
					
					String addname = "";
					String newid = id;
					String neworigin = origin;
					if( unresolvedmap.containsKey(id) ) {
						String map = unresolvedmap.get(id);
						int f = map.indexOf('|');
						int l = map.indexOf('|', f+1);
						int n = map.indexOf('[', l+1);
						int e = map.indexOf(']', n+1);
						if( l != -1 ) newid = map.substring(f+1,l);
						if( n != -1 ) addname = ":" + map.substring(l+1,n).trim();
						if( e != -1 ) neworigin = map.substring(n+1,e).trim();
					}
					
					if( !refmap.containsKey(id) ) {
						Contig contig;
						if( contigmap.containsKey( contigstr ) ) {
							contig = contigmap.get( contigstr );
						} else {
							 contig = new Contig( contigstr );
						}
						
						tv.init( lname, contig, contloc, start, stop, dir );
						tv.name = prevline.substring(1);
						//ac.setName( lname );
						//tv.setAlignedSequence( ac );
						aas.put( lname, tv );
						
						//System.err.println( "erm " + start + "   " + stop + "   " + contig.toString() );
						contig.add( tv );
						
						String newname = (addname.length() == 0 ? name : addname.substring(1)); //name+addname
						Gene gene = new Gene( null, id, newname, origin );
						gene.designation = designations != null ? designations.get( id ) : null;
						gene.refid = newid;
						gene.setIdStr( idstr );
						gene.allids = new HashSet<String>();
						gene.allids.add( newid );
						if( idstr != null ) {
							int ec = idstr.indexOf("EC");
							if( ec != -1 ) {
								//int ecc = name.indexOf(')', ec+1);
								//if( ecc == -1 ) ecc = name.length();
								int k = ec+3;
								if( k < idstr.length() ) {
									char c = idstr.charAt(k);
									while( (c >= '0' && c <= '9') || c == '.' ) {
										c = idstr.charAt( k++ );
										if( k == idstr.length() ) {
											k++;
											break;
										}
									}
									gene.ecid = idstr.substring(ec+2, k-1).trim();
								}
								
								/*if( ecc > ec+3 ) {
									gene.ecid = name.substring(ec+3, ecc).trim();
								} else {
									System.err.println();
								}*/
							}
						
							int go = idstr.indexOf("GO:");
							while( go != -1 ) {
								int ngo = idstr.indexOf("GO:", go+1);
								
								if (gene.funcentries == null)
									gene.funcentries = new HashSet<Function>();
								
								String goid = null;
								if( ngo != -1 ) goid = idstr.substring(go, ngo);
								else {
									int ni = go+3;
									while( ni < go+10 && ni < idstr.length() && idstr.charAt(ni) >= '0' && idstr.charAt(ni) <= '9' ) {
										ni++;
									}
									//int ni = go+10;//Math.minname.indexOf(')', go+1);
									if( ni <= idstr.length() ) {
										goid = idstr.substring( go, ni );
									}
									
									while( goid.length() < 10 ) {
										/*if( goid.length() < 4 ) {
											System.err.println();
										}*/
										goid = "GO:0"+goid.substring(3);
									}
								}
								
								if( goid != null ) {
									Function func;
									if( funcmap.containsKey( goid ) ) {
										func = funcmap.get( goid );
									} else {
										func = new Function( goid );
										funcmap.put( goid, func );
									}
									gene.funcentries.add( func );
								}
								
								go = ngo;
							}
						}
						//gene.species = new HashMap<String, Teginfo>();
						
						//if( !newid.equals(id) ) 
						//refmap.put( newid, gene );
						refmap.put( id, gene );
						
						tv.setGene( gene );
						tv.setTegund( origin );
						
						//tv.unresolvedGap();
						
						//Teginfo ti = new Teginfo();
						//ti.add( tv );
						gene.tegeval = tv;
						
						/*if( preval != null ) {
							Contig precontig = preval.getContshort();
							Contig curcontig = tv.getContshort();
							boolean bu = precontig.equals( curcontig );
							//System.err.println( bu + "  " + precontig.toString().equals( curcontig.toString()) + "  " + (precontig == curcontig) );
							if( bu ) {
								tv.setPrevious( preval );
								tv.setNum( preval.getNum()+1 );
								curcontig.end = tv;
							} else {
								tv.setNum(0);
								curcontig.start = tv;
								curcontig.end = tv;
							}
						} else tv.setNum( 0 );
						preval = tv;*/
						
						
						//new Aas(name, ac, start, stop, dir));
						// aass.add( new Aas(name, ac) );
					}
				}

				tv = new Tegeval();
				String cont = line.substring(1) + "";
				String[] split = cont.split("#");
				lname = split[0].trim().replace(".fna", "");
				prevline = line;
				
				boolean succ = false;
				int i = 0;
				while( !succ && i+3 < split.length ) {
					succ = true;
					try {
						start = Integer.parseInt(split[i+1].trim());
						stop = Integer.parseInt(split[i+2].trim());
						dir = Integer.parseInt(split[i+3].trim());
					} catch( Exception e ) {
						succ = false;
						lname += split[i+1];
						e.printStackTrace();
					}
					i++;
				}
				// int v = name.indexOf("contig");
				/*
				 * if( v != -1 ) { int i1 = name.indexOf('_',v); int i2 =
				 * name.indexOf('_', i1+1); name = name.substring(0,i1) +
				 * name.substring(i2); }
				 */
			} else {
				String str = line.trim();
				if( str.contains("X") ) tv.dirty = true;
				tv.append( str );
			}
			// else trimSubstring(ac, line);
			line = br.readLine();
			// br.re
		}

		if (tv.getSequenceLength() > 0) {
			String contigstr = null;
			String contloc = null;
			
			String origin;
			String id;
			String name;
			int i = lname.lastIndexOf('[');
			if( i == -1 ) {
				i = contigIndex( lname );
				int u = lname.lastIndexOf('_');
				contigstr = lname.substring(0, u);
				origin = lname.substring(0, i-1);
				contloc = lname.substring(i, lname.length());
				name = lname;
				id = lname;
			} else {
				int n = lname.indexOf(']', i+1);
				contigstr = lname.substring(i+1, n);
				int u = lname.indexOf(' ');
				id = lname.substring(0, u);
				if( id.contains("..") ) {
					id = lname.substring(i+1, n) + "_" + id;
				}
				name = lname.substring(u+1, i).trim();
				
				u = Contig.specCheck( contigstr );
				
				if( u == -1 ) {
					u = contigIndex( contigstr );
					/*u = contigstr.indexOf("contig");
					if( u == -1 ) u = contigstr.indexOf("scaffold");
					if( u == -1 ) u = contigstr.lastIndexOf('_')+1;*/
					origin = contigstr.substring(0, u-1);
					contloc = contigstr.substring(u, contigstr.length());
				} else {
					n = contigstr.indexOf("_", u+1);
					if( n == -1 ) n = contigstr.length();
					origin = contigstr.substring(0, n);
					contloc = n < contigstr.length() ? contigstr.substring(n+1) : "";
				}
				
				if( prevline != null ) {
					i = prevline.lastIndexOf('#');
					if( i != -1 ) {
						u = prevline.indexOf(';', i+1);
						if( u != -1 ) {
							id = prevline.substring(u+1, prevline.length());
						}
					}
				}
			}

			//int fi = lname.indexOf('_');
			//int li = lname.lastIndexOf('_');
			//contigstr = lname.substring(0, li);
			//contloc = lname.substring(fi+1,lname.length());//qsplit[0] + "_" + qsplit[1 + k]; // query.substring(first+1,sec);
			
			String addname = "";
			String newid = id;
			String neworigin = origin;
			if( unresolvedmap.containsKey(id) ) {
				String map = unresolvedmap.get(id);
				int f = map.indexOf('|');
				int l = map.indexOf('|', f+1);
				int n = map.indexOf('[', l+1);
				int e = map.indexOf(']', n+1);
				if( l != -1 ) newid = map.substring(f+1,l);
				if( n != -1 ) addname = ":" + map.substring(l+1,n).trim();
				if( e != -1 ) neworigin = map.substring(n+1,e).trim();
			}
			
			if( !refmap.containsKey(id) ) {
				Contig contig;
				if( contigmap.containsKey( contigstr ) ) {
					contig = contigmap.get( contigstr );
				} else {
					 contig = new Contig( contigstr );
				}
				tv.init( lname, contig, contloc, start, stop, dir );
				tv.name = prevline.substring(1);
				//tv.setAlignedSequence( ac );
				aas.put(lname, tv );
				
				contig.add( tv );
				// aass.add( new Aas(name, ac, start, stop, dir) );
				
				String newname = addname.length() == 0 ? name : addname.substring(1);
				Gene gene = new Gene( null, id, newname, origin );
				gene.designation = designations != null ? designations.get( id ) : null;
				gene.refid = newid;
				gene.allids = new HashSet<String>();
				gene.allids.add( newid );
				//gene.species = new HashMap<String, Teginfo>();
				
				//if( !newid.equals(id) ) 
				//refmap.put( newid, gene );
				refmap.put(id, gene);
				
				tv.setGene( gene );
				tv.setTegund( origin );
				
				//Teginfo ti = new Teginfo();
				//ti.add( tv );
				gene.tegeval = tv;
			}
		}
		tv = null;
		// fw.close();

		/*
		 * System.err.println("erm "+aass.size()); aas = new Aas[aass.size()];
		 * int i = 0; for( Aas a : aass ) { aas[i++] = a; } aass.clear();
		 * System.gc();
		 */

		System.err.println( refmap.size() );
		System.err.println();
		// Arrays.sort( aas );
	}
	
	public void sortLoci() {
		for( String cstr : contigmap.keySet() ) {
			Contig ct = contigmap.get( cstr );
			ct.sortLocs();
		}
	}

	Set<String>	plasmids = new HashSet<String>();
	private List<String> loadcontigs(BufferedReader br, String filename) throws IOException {		
		String line = br.readLine();
		String name = null;
		StringBuilder ac = new StringBuilder();
		int size = 0;
		while (line != null) {
			if (line.startsWith(">")) {				
				if( size > 0 ) {
					Contig contig = new Contig( name, ac );
					contigmap.put( name, contig );
					
					String spec = contig.getSpec();
					List<Contig>	ctlist;
					if( speccontigMap.containsKey( spec ) ) {
						ctlist = speccontigMap.get( spec );
					} else {
						ctlist = new ArrayList<Contig>();
						speccontigMap.put( spec, ctlist );
					}
					ctlist.add( contig );
					contig.partof = ctlist;
				}

				ac = new StringBuilder();
				size = 0;

				int i = line.indexOf(' ');
				if( i == -1 ) i = line.length();
				name = filename+line.substring(1, i).replace(".fna", "");
				
				//int first = tv.cont.indexOf("_");
				//int sec = tv.cont.indexOf("_", first + 1);

				//cname = tv.cont.substring(0, sec);
			} else {
				ac.append(line);
				size += line.length();
			}
			line = br.readLine();
		}
		
		if (ac.length() > 0) {
			Contig contig = new Contig( name, ac );
			contigmap.put( name, contig );
			
			List<Contig>	ctlist;
			String spec = contig.getSpec();
			
			if( speccontigMap.containsKey( spec ) ) {
				ctlist = speccontigMap.get( spec );
			} else {
				ctlist = new ArrayList<Contig>();
				speccontigMap.put( spec, ctlist );
			}
			ctlist.add( contig );
			contig.partof = ctlist;
		}
		
		for( String spec : speccontigMap.keySet() ) {
			List<Contig> ctg = speccontigMap.get(spec);
			
			System.err.println( spec + " " + ctg.size() );
			
			if( ctg.size() < 4 && ctg.size() > 1 ) {
				Contig chrom = null;
				for( Contig c : ctg ) {
					if( chrom == null || c.length() > chrom.length() ) chrom = c;
				}
				for( Contig c : ctg ) {
					if( c != chrom ) c.plasmid = true;
				}
			} else {
				for( Contig c : ctg ) {
					c.plasmid = plasmids.contains( c.toString() );
				}
			}
		}
		
		for( String spec : speccontigMap.keySet() ) {
			List<Contig> ctg = speccontigMap.get(spec);
			
			List<Contig> plasmids = new ArrayList<Contig>();
			for( Contig c : ctg ) {
				if( c.isPlasmid() ) plasmids.add( c );
			}
			ctg.removeAll( plasmids );
			ctg.addAll( plasmids );
		}
		
		return new ArrayList<String>( speccontigMap.keySet() );
	}

	/*private static void loci2dnasequence(Reader rd) throws IOException {
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
	}*/

	//static Aas aquery = new Aas(null, null, 0, 0, 0);
	// static Aas[] aas;
	Map<String, Tegeval> aas = new HashMap<String, Tegeval>();
	//static Map<String, StringBuilder> dnaa = new HashMap<String, StringBuilder>();
	//static Map<String, StringBuilder> contigsmap = new HashMap<String, StringBuilder>();

	/*public static void loci2aasequence(String[] stuff, File dir2) throws IOException {
		for (String st : stuff) {
			File aa = new File(dir2, st);
			loci2aasequence(new FileReader(aa));
		}
	}*/

	public static void printnohits(String[] stuff, File dir, File dir2) throws IOException {
		//loci2aasequence(stuff, dir2);
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

	public void testbmatrix(String str) throws IOException {
		Set<String> testset = new HashSet<String>(Arrays.asList(new String[] { "1s", "2s", "3s", "4s" }));
		Map<Set<String>, Set<Map<String, Set<String>>>> clustermap = new HashMap<Set<String>, Set<Map<String, Set<String>>>>();
		Set<Map<String, Set<String>>> smap = new HashSet<Map<String, Set<String>>>();
		smap.add(new HashMap<String, Set<String>>());
		smap.add(new HashMap<String, Set<String>>());
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "1s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "2s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "3s" })), smap);
		clustermap.put(new HashSet<String>(Arrays.asList(new String[] { "4s" })), smap);

		BufferedImage img = bmatrix(testset, clustermap, "");

		ImageIO.write(img, "png", new File(str));
	}
	
	public BufferedImage animatrix( Collection<String> species1, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, String designation, Collection<GeneGroup> allgg ) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		
		List<String> specset;// = new ArrayList<String>(species1);
		if( designation.length() > 0 ) {
			Set<String> sset = new TreeSet<String>();
			for( Gene g : genelist ) {
				if( g.designation != null && g.designation.equals( designation ) ) {
					sset.add( g.getSpecies() );
				}
			}
			specset = new ArrayList<String>( sset );
		} else specset = new ArrayList<String>(species1);
		
		for (String spec : specset) {
			String spc = nameFix( spec );
			int tstrw = g2.getFontMetrics().stringWidth(spc);
			if (tstrw > mstrw)
				mstrw = tstrw;
		}

		int sss = mstrw + 72 * specset.size() + 10 + 72;
		bi = new BufferedImage(sss, sss, BufferedImage.TYPE_INT_ARGB);
		g2 = (Graphics2D) bi.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.white);
		g2.fillRect(0, 0, sss, sss);
		
		Set<String>	d1 = new HashSet<String>();
		Set<String>	d2 = new HashSet<String>();
		
		double[] matrix = new double[ specset.size()*specset.size() ];
		try {
			Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
			int where = 0;
			for (String spec1 : specset) {
				int wherex = 0;
				
				//String spc1 = nameFix( spec1 );
				for (String spec2 : specset) {
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
										Sequence seq1 = tv1.alignedsequence;
										Sequence seq2 = tv2.alignedsequence;
										if( seq1 != null && seq2 != null ) {
											int mest = 0;
											int tmest = 0;
											//bval = Math.max( GeneCompare.blosumVal(tv1.alignedsequence, tv2.alignedsequence, blosumap), bval );
											
											//public static double blosumVal( Sequence seq1, Sequence seq2, Map<String,Integer> blosumap ) {
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
						double ani = (double)totalscore/(double)totaltscore;
						matrix[ where*specset.size()+wherex ] = 1.0-ani;
					}
					wherex++;
				}
				where++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TreeUtil tu = new TreeUtil();
		corrInd.clear();
		for( String spec : specset ) {
			corrInd.add( nameFix( spec ) );
		}
		Node n = tu.neighborJoin(matrix, corrInd, null, false, false);
		
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
		System.err.println( "ordind " + ordInd );
		System.err.println( "tree " + n );
	
		int where = 0;
		for (String spec1 : ordInd) {
			int wherex = 0;
			int w = corrInd.indexOf( spec1 );
			//String spc1 = nameFix( spec1 );
			int strw = g2.getFontMetrics().stringWidth(spec1);
			
			g2.setColor(Color.black);
			g2.drawString(spec1, mstrw - strw, mstrw + 47 + where * 72);
			g2.rotate(Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			g2.drawString(spec1, mstrw + 47 + where * 72, mstrw - strw);
			g2.rotate(-Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			//String spc1 = nameFix( spec1 );
			for (String spec2 : ordInd) {
				if( where != wherex ) {
					
					int wx = corrInd.indexOf( spec2 );
					double ani = 1.0-matrix[ w*specset.size()+wx ];
					
					float cval = Math.min( 0.9f, Math.max( 0.0f,4.2f - (float)(4.2*ani) ) );
					System.err.println( cval + "  " + ani );
					Color color = new Color( cval, cval, cval );
					g2.setColor( color );
					g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);
					
					g2.setColor( Color.white );
					String str = String.format("%.1f%s", (float) (ani * 100.0), "%");
					int nstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 + 15);
				}
				wherex++;
			}
			where++;
		}
		
		System.err.println( d1.size() + "  " + d2.size() );
		if( d1.size() > d2.size() ) {
			d1.removeAll( d2 );
			System.err.println( d1.size() );
		} else {
			d2.removeAll( d1 );
			System.err.println( d2.size() );
		}
		
		return bi;
	}

	public BufferedImage bmatrix(Collection<String> species1, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, String designation) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		
		Collection<String> specset = species1;
		if( designation.length() > 0 ) {
			specset = new TreeSet<String>();
			for( Gene g : genelist ) {
				if( g.designation != null && g.designation.equals( designation ) ) {
					specset.add( g.getSpecies() );
				}
			}
		}
		
		for (String spc : specset) {
			int tstrw = g2.getFontMetrics().stringWidth(spc);
			if (tstrw > mstrw)
				mstrw = tstrw;
		}

		int sss = mstrw + 72 * specset.size() + 10 + 72;
		bi = new BufferedImage(sss, sss, BufferedImage.TYPE_INT_ARGB);
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

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		Map<String,Set<Gene>>		geneMap = new HashMap<String,Set<Gene>>();
		Map<String,Set<GeneGroup>>	ggMap = new HashMap<String,Set<GeneGroup>>();
		if( designation.length() > 0 ) {
			for( Gene g : genelist ) {
				if( g.designation != null && g.designation.equals(designation) ) {
					String spec = g.getSpecies();
					
					if( geneMap.containsKey( spec ) ) {
						geneMap.get( spec ).add( g );
					} else {
						Set<Gene> set = new HashSet<Gene>();
						set.add( g );
						geneMap.put( spec, set );
					}
					
					if( ggMap.containsKey( spec ) ) {
						ggMap.get( spec ).add( g.getGeneGroup() );
					} else {
						Set<GeneGroup> set = new HashSet<GeneGroup>();
						set.add( g.getGeneGroup() );
						ggMap.put( spec, set );
					}
				}
			}
			
			int where = 0;
			for (String spec1 : specset) {
				Set<GeneGroup> specset1 = ggMap.get(spec1);
				//String spc1 = nameFix( spec1 );
				int wherex = 0;
				for (String spec2 : specset) {
					Set<GeneGroup> specset2 = ggMap.get(spec2);
					
					if( where == wherex ) {
						double hh = (double) specset1.size() / (double) geneMap.get(spec1).size();
						if (hh > maxh)
							maxh = hh;
						if (hh < minh)
							minh = hh;
					} else {
						int count = 0;
						for( GeneGroup gg : specset1 ) {
							if( specset2.contains( gg ) ) count++;
						}
						double hhwoc = (double) count / (double) specset1.size();
						if (hhwoc > maxhwoc)
							maxhwoc = hhwoc;
						if (hhwoc < minhwoc)
							minhwoc = hhwoc;
					}

					if( wherex == 0 ) {
						int ss = geneMap.get(spec1).size();
						if (ss > maxs)
							maxs = ss;
						if (ss < mins)
							mins = ss;
	
						int rs = specset1.size();
						if (rs > maxrs)
							maxrs = rs;
						if (rs < minrs)
							minrs = rs;
						
						double rr = (double)rs / (double)ss;
						if (rr > maxr)
							maxr = rr;
						if (rr < minr)
							minr = rr;
					}
					
					wherex++;
				}
				where++;
			}
		} else {
			int where = 0;
			for (String spec1 : specset) {
				int wherex = 0;
				//String spc1 = nameFix( spec1 );
				for (String spec2 : specset) {
					//String spc2 = nameFix( spec2 );
					
					int spc1tot = 0;
					int spc2tot = 0;
					int totot = 0;

					int spc1totwoc = 0;
					int spc2totwoc = 0;
					int tototwoc = 0;
					for (Set<String> set : clusterMap.keySet()) {
						Set<Map<String, Set<String>>> erm = clusterMap.get(set);
						if (set.contains(spec1)) {
							//if (set.size() < specset.size()) {
							if ( !specset.containsAll(set) ) {
								spc1totwoc += erm.size();
								for (Map<String, Set<String>> sm : erm) {
									Set<String> hset = sm.get(spec1);
									if( hset != null ) {
										tototwoc += hset.size();
									} else {
										System.err.println();
									}
								}

								if (set.contains(spec2)) {
									spc2totwoc += erm.size();
								}

								if (spc2totwoc > spc1totwoc)
									System.err.println("okoko " + spc1totwoc + " " + spc2totwoc);
							}

							spc1tot += erm.size();
							for (Map<String, Set<String>> sm : erm) {
								Set<String> hset = sm.get(spec1);
								if( hset != null ) totot += hset.size();
							}

							if (set.contains(spec2)) {
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
				//where++;
			}
		}
	
		int where = 0;
		for (String spc1 : specset) {
			String spec1 = nameFix( spc1 );
			int strw = g2.getFontMetrics().stringWidth(spec1);
			
			g2.setColor(Color.black);
			g2.drawString(spec1, mstrw - strw, mstrw + 47 + where * 72);
			g2.rotate(Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			g2.drawString(spec1, mstrw + 42 + where * 72, mstrw - strw);
			g2.rotate(-Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			
			int wherex = 0;
			if( designation.length() == 0 ) {
				for (String spc2 : specset) {
					int spc1tot = 0;
					int spc2tot = 0;
					int totot = 0;
	
					int spc1totwocore = 0;
					int spc2totwocore = 0;
					int tototwocore = 0;
					for (Set<String> set : clusterMap.keySet()) {
						Set<Map<String, Set<String>>> erm = clusterMap.get(set);
						if (set.contains(spc1)) {
							//if (set.size() < specset.size()) {
							if ( !specset.containsAll(set) ) {
								spc1totwocore += erm.size();
								for (Map<String, Set<String>> sm : erm) {
									Set<String> hset = sm.get(spc1);
									if( hset != null ) tototwocore += hset.size();
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
								if( hset != null ) totot += hset.size();
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
						g2.fillRoundRect(mstrw + 10 + specset.size() * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);
	
						g2.setColor(Color.white);
						String str = dval + "";
						int nstrw2 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + specset.size() * 72 - nstrw2 / 2, mstrw + 47 + where * 72);
						// int nstrw2 = g2.getFontMetrics().stringWidth( str );
	
						dval = spc1tot;
						cval = (int) (200.0 * (maxrs - dval) / (maxrs - minrs));
						g2.setColor(new Color(cval, cval, 255));
						g2.fillRoundRect(mstrw + 10 + where * 72, mstrw + 10 + specset.size() * 72, 64, 64, 16, 16);
						g2.setColor(Color.white);
						str = spc1tot + "";
						int nstrw1 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + where * 72 - nstrw1 / 2, mstrw + 47 + specset.size() * 72);
					}
	
					wherex++;
				}
	
				//System.err.println();
				where++;
			} else {
				Set<GeneGroup> specset1 = ggMap.get(spc1);
				for (String spc2 : specset) {
					Set<GeneGroup> specset2 = ggMap.get(spc2);
					if (where == wherex) {
						double dval = (double) specset1.size() / (double) geneMap.get(spc1).size();
						int cval = (int) (200.0 * (maxr - dval) / (maxr - minr));
						g2.setColor(new Color(255, cval, cval));
						g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);
	
						g2.setColor(Color.white);
						String str = specset1.size() + "";
						int nstrw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 - 15);
	
						str = geneMap.get(spc1).size() + "";
						nstrw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72);
	
						ps.printf("%.1f%s", (float) (dval * 100.0), "%");
						str = baos.toString();
						baos.reset();
						nstrw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 + 15);
					} else {
						int count = 0;
						for( GeneGroup gg : specset1 ) {
							if( specset2.contains( gg ) ) count++;
						}
						double dval = (double) count / (double) specset1.size();
						//double dval = (double) spc2totwocore / (double) spc1totwocore;
						int cval = (int) (200.0 * (maxhwoc - dval) / (maxhwoc - minhwoc));
						g2.setColor(new Color(cval, 255, cval));
						g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);
	
						g2.setColor(Color.white);
						String str = count + "";
						int nstrw2 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw2 / 2, mstrw + 47 + where * 72 - 15);
						// int nstrw2 = g2.getFontMetrics().stringWidth( str );
						str = specset1.size() + "";
						int nstrw1 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw1 / 2, mstrw + 47 + where * 72);
	
						double hlut = 100.0 * ((double) count / (double) specset1.size());
						ps.printf("%.1f%s", (float) hlut, "%");
						str = baos.toString();
						baos.reset();
						int pstrw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - pstrw / 2, mstrw + 47 + where * 72 + 15);
					}
	
					if (wherex == 0) {
						int dval = geneMap.get(spc1).size();
						int cval = (int) (200.0 * (maxs - dval) / (maxs - mins));
						g2.setColor(new Color(cval, cval, 255));
						g2.fillRoundRect(mstrw + 10 + specset.size() * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);
	
						g2.setColor(Color.white);
						String str = dval + "";
						int nstrw2 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + specset.size() * 72 - nstrw2 / 2, mstrw + 47 + where * 72);
						// int nstrw2 = g2.getFontMetrics().stringWidth( str );
	
						dval = specset1.size();
						cval = (int) (200.0 * (maxrs - dval) / (maxrs - minrs));
						g2.setColor(new Color(cval, cval, 255));
						g2.fillRoundRect(mstrw + 10 + where * 72, mstrw + 10 + specset.size() * 72, 64, 64, 16, 16);
						g2.setColor(Color.white);
						str = specset1.size() + "";
						int nstrw1 = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + where * 72 - nstrw1 / 2, mstrw + 47 + specset.size() * 72);
					}
	
					wherex++;
				}
				where++;
			}
		}
		
		g2.setColor(Color.gray);
		g2.fillRoundRect(mstrw + 10 + specset.size() * 72, mstrw + 10 + specset.size() * 72, 64, 64, 16, 16);

		int core = 0;
		int pan = 0;

		if( designation.length() == 0 ) {
			for (Set<String> set : clusterMap.keySet()) {
				Set<Map<String, Set<String>>> setmap = clusterMap.get(set);
	
				if( !Collections.disjoint(set, specset) ) pan += setmap.size();
				if (set.containsAll(specset))
					core += setmap.size();
			}
		} else {
			Set<GeneGroup> panset = new HashSet<GeneGroup>();
			Set<GeneGroup> coreset = new HashSet<GeneGroup>();
			for( String spec : ggMap.keySet() ) {
				Set<GeneGroup> ggSet = ggMap.get( spec );
				coreset.addAll( ggSet );
				panset.addAll( ggSet );
				break;
			}
			
			for( String spec : ggMap.keySet() ) {
				Set<GeneGroup> ggSet = ggMap.get( spec );
				coreset.retainAll( ggSet );
				panset.addAll( ggSet );
			}
			
			pan = panset.size();
			core = coreset.size();
		}

		g2.setColor(Color.white);
		String str = core + "";
		int nstrw2 = g2.getFontMetrics().stringWidth(str);
		g2.drawString(str, mstrw + 42 + specset.size() * 72 - nstrw2 / 2, mstrw + 47 + specset.size() * 72 - 15);
		// int nstrw2 = g2.getFontMetrics().stringWidth( str );
		str = pan + "";
		int nstrw1 = g2.getFontMetrics().stringWidth(str);
		g2.drawString(str, mstrw + 42 + specset.size() * 72 - nstrw1 / 2, mstrw + 47 + specset.size() * 72);

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

	private static List<Set<String>> loadSimpleClusters(BufferedReader br) throws IOException {
		// FileReader fr = new FileReader( file );
		//BufferedReader br = new BufferedReader(rd);
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
					/*int c = 1;
					int v = 1;
					while( c > 0 ) {
						int s = trimline.indexOf('[', v);
						int e = trimline.indexOf(']', v);
						v = Math.min(s, e);
						
						if( trimline.charAt(v) == ']' ) c--;
						else c++;
					}*/
					
					//String[] subsplit = trimline.substring(1, trimline.length() - 1).split(", ");
					Set<String> trset = new TreeSet<String>();
					
					int s = 1;
					int i = trimline.indexOf('#');
					while( i != -1 ) {
						int k = trimline.indexOf(',', i+1);
						int u = trimline.indexOf(';', i+1);
						if( u > 0 && trimline.charAt(u-1) == '#' && ( u < k || k == -1 ) ) {
						//if( u == i+1 ) {
							String loc =  trimline.substring(u+1, k == -1 ? trimline.length()-1: k).trim();
							trset.add( loc );
						} else trset.add( trimline.substring(s, /*i*/k == -1 ? trimline.length()-1: k).trim() );
					
						if( k == -1 ) {
							i = -1;
						} else {
							s = k+1;
							i = trimline.indexOf('#', s);
						}
					}
					
					if (prevset != null)
						prevset.addAll(trset);
					// ret.add( trset );
				}
			}
			line = br.readLine();
		}
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

	private static Map<Set<String>, Set<Map<String, Set<String>>>> initCluster(Collection<Set<String>> total) {
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = new HashMap<Set<String>, Set<Map<String, Set<String>>>>();

		for (Set<String> t : total) {
			Set<String> teg = new HashSet<String>();
			for (String e : t) {				
				int i =  e.lastIndexOf('[');
				if( i != -1 ) {
					String str = e.substring(i+1, e.indexOf(']', i+1));
					
					String spec;
					int u = Contig.specCheck( str );
					
					if( u == -1 ) {
						u = contigIndex(str);
						spec = str.substring( 0, u-1 );
					} else {
						int l = str.indexOf('_', u+1);
						spec = str.substring( 0, l );
					}
					/*
					 * if( joinmap.containsKey( str ) ) { str = joinmap.get(str); }
					 */
					teg.add(spec);
				} else {
					i = contigIndex(e);
					/*i = e.indexOf("contig");
					if( i == -1 ) i = e.indexOf("scaffold");
					if( i == -1 ) i = e.lastIndexOf('_')+1;*/
					String spec = i == -1 ? "Unknown" : e.substring(0, i-1);
					
					teg.add(spec);
				}

				//species.add(str);
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
				//int i = e.indexOf('_');
				int i =  e.lastIndexOf('[');
				if( i != -1 ) {
					//String str = e.substring(0,i);
					String str = e.substring(i+1, e.indexOf(']', i+1));
					/*
					 * if( joinmap.containsKey( str ) ) { str = joinmap.get(str); }
					 */
					
					String spec;
					int u = Contig.specCheck( str );
					
					if( u == -1 ) {
						u = contigIndex( str );
						spec = str.substring( 0, u-1 );
					} else {
						int l = str.indexOf('_', u+1);
						spec = str.substring( 0, l );
					}
	
					Set<String> set;
					if (submap.containsKey(spec)) {
						set = submap.get(spec);
					} else {
						set = new HashSet<String>();
						submap.put(spec, set);
					}
					set.add(e);
				} else {
					i = contigIndex(e);
					String spec = i == -1 ? "Unknown" : e.substring(0, i-1);
					
					Set<String> set;
					if (submap.containsKey(spec)) {
						set = submap.get(spec);
					} else {
						set = new HashSet<String>();
						submap.put(spec, set);
					}
					set.add(e);
				}
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

	/*public Set<String> speciesFromCluster(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) {
		Set<String> species = new TreeSet<String>();

		for (Set<String> clustset : clusterMap.keySet()) {
			species.addAll(clustset);
		}

		return species;
	}*/

	public void func4(File dir, String[] stuff) throws IOException {
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
		//Set<String> species = speciesFromCluster(clusterMap);

		// writeSimplifiedCluster( "/home/sigmar/burb2.txt", clusterMap );

		writeBlastAnalysis(clusterMap, specList);
	}
	
	public List<String> getSpecies() {
		return specList;
	}

	public void clusterFromSimplifiedBlast(String filename) throws IOException {
		clusterFromSimplifiedBlast(filename, null);
	}

	public void clusterFromSimplifiedBlast(String filename, String writeSimplifiedCluster) throws IOException {
		//Set<String> species = new TreeSet<String>();
		List<Set<String>> total = readBlastList(filename); // "/home/sigmar/blastcluster.txt"
															// );
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = initCluster(total);

		if (writeSimplifiedCluster != null)
			writeSimplifiedCluster(writeSimplifiedCluster, clusterMap); // "/home/sigmar/burb2.txt",
																		// clusterMap
																		// );

		writeBlastAnalysis(clusterMap, specList);
	}

	public void clusterFromBlastResults(Path dir, String[] stuff) throws IOException {
		clusterFromBlastResults(dir, stuff, null, null, true);
	}

	public void clusterFromBlastResults(Path dir, String[] stuff, String writeSimplifiedCluster, String writeSimplifiedBlast, boolean union) throws IOException {
		Set<String> species = new TreeSet<String>();
		List<Set<String>> total = new ArrayList<Set<String>>();
		Serifier serifier = new Serifier();
		for( String name : stuff ) {
			//File ff = new File( dir, name );
			//FileInputStream	fis = new FileInputStream( ff );
			Path ff = dir.resolve(name);
			BufferedReader fis = Files.newBufferedReader( ff );
			serifier.joinBlastSets(fis, writeSimplifiedBlast, union, total, 0.0);
		}
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = initCluster(total);

		if (writeSimplifiedCluster != null)
			writeSimplifiedCluster(writeSimplifiedCluster, clusterMap); // "/home/sigmar/burb2.txt",
																		// clusterMap
																		// );

		writeBlastAnalysis(clusterMap, specList);
	}

	public void writeBlastAnalysis(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, Collection<String> species) throws IOException {
		BufferedImage img = bmatrix(species, clusterMap, "");
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

	public void next(Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) throws IOException {
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
										StringBuilder aa = ((Tegeval)aas.get(loci)).getProteinSequence();
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

						StringBuilder aastr = ((Tegeval)aas.get(gene)).getProteinSequence();
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

	private Map<String, Integer> loadFrequency(Reader r, Set<String> included) throws IOException {
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

				//aquery.name = prename;
				if( aas.containsKey(prename) )
					lociMap.put(prename, "No match\t" + aas.get(prename));
				//else if (dnaa.containsKey(prename))
				//	lociMap.put(prename, "No match\t" + dnaa.get(prename));
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

	private void loci2gene(String base, Reader rd, String outfile, String filtercont, Map<String, Integer> freqmap, Set<String> included) throws IOException {
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

				//aquery.name = prename;
				if(aas.containsKey(prename) )
					lociMap.put(prename, "No match\t" + aas.get(prename));
				//else if (dnaa.containsKey(prename))
				//	lociMap.put(prename, "No match\t" + dnaa.get(prename));
				else lociMap.put(prename, "No match");

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

	Map<String, String> lociMap = new HashMap<String, String>();
	public void loci2gene(String[] stuff, File dir) throws IOException {
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
		for (int i = 0; i < Math.pow(Sequence.uff.size(), val); i++) {
			String e = "";
			for (int k = 0; k < val; k++) {
				e += Sequence.uff.get((i / (int) Math.pow(Sequence.uff.size(), val - (k + 1))) % Sequence.uff.size()).c;
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

		for (Erm e : Sequence.isoel) {
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
					genelist.add(new Gene(null, last, last, "mool"));
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
		genelist.add(new Gene(null, last, last, "moool"));
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
					Gene g = new Gene(null, last, last, "mool");
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
		Gene g = new Gene(null, last, last, "mool");
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
	
	static Scene scene = null;
	JFrame	fxframe = null;
	private static Scene createScene( String[] names, double[] xdata, double[] ydata ) {
        final NumberAxis xAxis = new NumberAxis(-0.5, 0.5, 0.025);
        final NumberAxis yAxis = new NumberAxis(-0.5, 0.5, 0.025);    
        final ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Dim 1");
        yAxis.setLabel("Dim 2");
        sc.setTitle("Genes");
       
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("PCA");
        for( int i = 0; i < xdata.length; i++ ) {
        	XYChart.Data d = new XYChart.Data( xdata[i], ydata[i] );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	series1.getData().add( d );
        }
 
        sc.getData().addAll(series1);
        if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );
        
        for (XYChart.Series<Number, Number> s : sc.getData()) {
        	int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }
        
        sc.setBackground( Background.EMPTY );
        
        final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem();
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(sc, event.getScreenX(), event.getScreenY());
               }
             }
        });
        
        return scene;
    }
	
	private Scene createWebPageScene( String webp ) {
		
		/*if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );*/
		
		return scene;
	}
	
	private Scene createDualPieChartScene( Map<Character,Integer> map, Map<Character,Integer> mip ) {
        List<String> speclist = new ArrayList<String>();
        /*for( String spec : mip.keySet() ) {
        	speclist.add( nameFix(spec) );
        }*/
        
        HBox hbox = new HBox();
       
        final PieChart sc = new PieChart();
        sc.labelsVisibleProperty().set( true );
        sc.setLegendVisible(false);
        sc.setLegendSide( Side.RIGHT );
        sc.setTitle("COG core");
        
        final PieChart sc2 = new PieChart();
        sc2.labelsVisibleProperty().set( true );
        sc2.setLegendSide( Side.RIGHT );
        sc2.setVisible( true );
        sc2.setTitle("COG accessory");
        
        
        
        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );
       
       /* Map<String,Integer> countmap = new HashMap<String,Integer>();
        for( String spec : map.keySet() ) {
        	Map<Character,Integer> submap = map.get(spec);
        	int total = 0;
        	for( Character f : submap.keySet() ) {
        		total += submap.get(f);
        	}
        	countmap.put( spec, total );
        }*/
        
        //PieChart.Data d = new PieChart.Data();
        for( String s : Cog.coggroups.keySet() ) {
        	Set<Character> schar = Cog.coggroups.get( s );
	        if( s.contains("METABOLISM") ) {
	        	for( Character cogsymbol : schar ) {
		        	if( cogsymbol != null ) {
			        	int count = 0;
			        	if( map.containsKey(cogsymbol) ) count = map.get( cogsymbol );
			        	
				        /*XYChart.Series<String,Number> core = new XYChart.Series<String,Number>();
				        int i = longname.indexOf(',', 50);
				        if( i == -1 ) i = longname.length();
				        core.setName( longname.substring(0,i) );
				        for( String spec : map.keySet() ) {
				        	Map<Character,Integer> submap = map.get(spec);
				        	//int last = 0;
				        	//for( String f : submap.keySet() ) {
				        	if( submap.containsKey(flock) ) {
				        		int total = countmap.get(spec);
					        	int ival = submap.get( flock );
					        	String fixspec = nameFix(spec);
					        	XYChart.Data<String,Number> d = uniform ?  new XYChart.Data<String,Number>( fixspec, (double)ival/(double)total ) : new XYChart.Data<String,Number>( fixspec, ival );
					        	//Tooltip.install( d.getNode(), new Tooltip( flock ) );
					        	core.getData().add( d );
				        	}
				        	
					        //last = last+ival;
				        }*/
			        	PieChart.Data d = new Data( Character.toString(cogsymbol)/*Cog.charcog.get(cogsymbol)*/, count );
			        	ObservableList<Data> ob = sc.getData();
			        	ob.add( d );
		        	}
	        	}
	        } else {
	        	int count = 0;
	        	for( Character cogsymbol : schar ) {
		        	if( cogsymbol != null ) {
			        	if( map.containsKey(cogsymbol) ) count += map.get( cogsymbol );
		        	}
	        	}
	        	PieChart.Data d = new Data( s/*Cog.charcog.get(cogsymbol)*/, count );
	        	ObservableList<Data> ob = sc.getData();
	        	ob.add( d );
	        }
        }
        
        for( String s : Cog.coggroups.keySet() ) {
        	Set<Character> schar = Cog.coggroups.get( s );
	        if( s.contains("METABOLISM") ) {
	        	for( Character cogsymbol : schar ) {
		        	if( cogsymbol != null ) {
			        	int count = 0;
			        	if( mip.containsKey(cogsymbol) ) count = mip.get( cogsymbol );
			        	PieChart.Data d = new Data( Character.toString(cogsymbol)/*Cog.charcog.get(cogsymbol)*/, count );
			        	ObservableList<Data> ob = sc2.getData();
			        	ob.add( d );
		        	}
	        	}
	        } else {
	        	int count = 0;
	        	for( Character cogsymbol : schar ) {
		        	if( cogsymbol != null ) {
		        		if( mip.containsKey(cogsymbol) ) count += mip.get( cogsymbol );
		        	}
	        	}
	        	PieChart.Data d = new Data( s/*Cog.charcog.get(cogsymbol)*/, count );
	        	ObservableList<Data> ob = sc2.getData();
	        	ob.add( d );
	        }
        }
        
        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( "dd", 100 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        //}
        XYChart.Series<String,Number> pan2 = new XYChart.Series<String,Number>();
        pan2.setName("Core");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d2 = new XYChart.Data<String,Number>( "2", 200 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan2.getData().add( d2 );
        //}
        sc.getData().addAll(pan, pan2);*/
        
        hbox.getChildren().addAll( sc, sc2 );
        if( scene == null ) {
        	scene = new Scene( hbox );
        } else scene.setRoot( hbox );
        
        /*for (XYChart.Series<String, Number> s : sc.getData()) {
        	//int i = 0;
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( s.getName()+": "+d.getYValue() ) );
            }
        }*/
        
        sc.setBackground( Background.EMPTY );
        
        final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem();
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("/Users/sigmar/fximg.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(sc, event.getScreenX(), event.getScreenY());
               }
             }
        });
        
        return scene;
    }
	
	private Scene createStackedBarChartScene( Map<String,String> all, Map<String,Map<Character,Integer>> map, boolean uniform ) {
        final CategoryAxis 	xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis();
        
        xAxis.setTickLabelRotation( 90.0 );
        
        List<String> speclist = new ArrayList<String>();
        for( String spec : map.keySet() ) {
        	speclist.add( nameFix(spec) );
        }
        xAxis.setCategories( FXCollections.<String>observableArrayList( speclist ) );
        //yAxis.
        
        final StackedBarChart<String,Number> sc = new StackedBarChart<String,Number>(xAxis,yAxis);
        sc.setLegendSide( Side.RIGHT );
        xAxis.setLabel("");
        yAxis.setLabel("");
        sc.setTitle("COG catogories");
        
        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );
        sc.getXAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
        sc.getYAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
       
        Map<String,Integer> countmap = new HashMap<String,Integer>();
        for( String spec : map.keySet() ) {
        	Map<Character,Integer> submap = map.get(spec);
        	int total = 0;
        	for( Character f : submap.keySet() ) {
        		total += submap.get(f);
        	}
        	countmap.put( spec, total );
        }
        
        for( String flock : all.keySet() ) {
        	//Map<String,Integer> submap = map.get( spec );
        	String longname = all.get(flock);
	        XYChart.Series<String,Number> core = new XYChart.Series<String,Number>();
	        int i = longname.indexOf(',', 50);
	        if( i == -1 ) i = longname.length();
	        core.setName( longname.substring(0,i) );
	        for( String spec : map.keySet() ) {
	        	Map<Character,Integer> submap = map.get(spec);
	        	//int last = 0;
	        	//for( String f : submap.keySet() ) {
	        	if( submap.containsKey(flock) ) {
	        		int total = countmap.get(spec);
		        	int ival = submap.get( flock );
		        	String fixspec = nameFix(spec);
		        	XYChart.Data<String,Number> d = uniform ?  new XYChart.Data<String,Number>( fixspec, (double)ival/(double)total ) : new XYChart.Data<String,Number>( fixspec, ival );
		        	//Tooltip.install( d.getNode(), new Tooltip( flock ) );
		        	core.getData().add( d );
	        	}
	        	
		        //last = last+ival;
	        }
	        sc.getData().add( core );
        }
        
        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( "dd", 100 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        //}
        XYChart.Series<String,Number> pan2 = new XYChart.Series<String,Number>();
        pan2.setName("Core");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d2 = new XYChart.Data<String,Number>( "2", 200 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan2.getData().add( d2 );
        //}
        sc.getData().addAll(pan, pan2);*/
        if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );
        
        for (XYChart.Series<String, Number> s : sc.getData()) {
        	//int i = 0;
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( s.getName()+": "+d.getYValue() ) );
            }
        }
        
        sc.setBackground( Background.EMPTY );
        
        final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem();
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(sc, event.getScreenX(), event.getScreenY());
               }
             }
        });
        
        return scene;
    }
	
	private static Scene createStackedBarChartScene( List<StackBarData> lsbd, String[] categories ) {
        final CategoryAxis 	xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis();
        
        xAxis.setTickLabelRotation( 90.0 );
        
        List<String>	names = new ArrayList<String>();
        for( StackBarData sbd : lsbd ) {
        	names.add( sbd.name );
        }
        xAxis.setCategories( FXCollections.<String>observableArrayList( names ) );
        //yAxis.
        
        final StackedBarChart<String,Number> sc = new StackedBarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel("");
        yAxis.setLabel("");
        sc.setTitle("Pan-core genome");
        
        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );
        sc.getXAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
        sc.getYAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
       
        for( String category : categories ) {
	        XYChart.Series<String,Number> core = new XYChart.Series<String,Number>();
	        StackBarData last = lsbd.get( lsbd.size()-1 );
	        int lastval = last.b.get( category );
	        core.setName( category + lastval );
	        for( StackBarData sbd : lsbd ) {
	        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( sbd.name, sbd.b.get( category ) );
	        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
	        	core.getData().add( d );
	        }
	        
	        sc.getData().add( core );
        }
        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan: " + ydata[ydata.length-1] );
        for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( names[i], ydata[i]-xdata[i] );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        }*/
 
        //sc.getData().addAll(core, pan);
        if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );
        
        /*for (XYChart.Series<Number, Number> s : sc.getData()) {
        	int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }*/
        
        sc.setBackground( Background.EMPTY );
        
        final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem();
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(sc, event.getScreenX(), event.getScreenY());
               }
             }
        });
        
        return scene;
    }
	
	private static Scene createBarChartScene( String[] names, XYChart.Series<String,Number> data, String xTitle, String yTitle, double start, double stop, double step, String title ) {
        final CategoryAxis 	xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis( start, stop, step ); // 0.6, 0.7, 0.02
        //yAxis.set
        
        /*yAxis.setTickLabelFormatter( new StringConverter<Number>() {
			@Override
			public String toString(Number arg0) {
				return Double.toString( Math.round( (arg0.doubleValue() + 0.6)*100.0 )/100.0 );
			}
			
			@Override
			public Number fromString(String arg0) {
				return Double.parseDouble( arg0 );
			}
		});*/
        
        xAxis.setTickLabelRotation( 90.0 );
        
        xAxis.setCategories( FXCollections.<String>observableArrayList( Arrays.asList(names) ) );
        //yAxis.
        
        final BarChart<String,Number> sc = new BarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel( xTitle );
        yAxis.setLabel( yTitle );
        sc.setTitle( title );
        
        xAxis.setStyle("-fx-tick-label-font-size: 1.4em;");
        yAxis.setStyle("-fx-tick-label-font-size: 1.4em;");
 
        sc.getData().addAll(data);
        if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );
        
        /*for (XYChart.Series<Number, Number> s : sc.getData()) {
        	int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }*/
        
        sc.setBackground( Background.EMPTY );
        
        final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem();
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(sc, event.getScreenX(), event.getScreenY());
               }
             }
        });
        
        return scene;
    }
	
	private Scene createScene( String webp ) {
        //Group  root  =  new  Group();
		final WebView	wv = new WebView();
		wv.setPrefSize(1600, 900);
		//wv.
		
		WebEngine we = wv.getEngine();
		we.loadContent( webp );
		
		ScrollPane	sp = new ScrollPane();
		sp.setPrefViewportWidth(1600);
		sp.setPrefViewportHeight(900);
		sp.setContent( wv );
		final Scene  scene = new  Scene(sp);
		/*we.setOnResized(
	        new EventHandler<WebEvent<Rectangle2D>>() {
	            public void handle(WebEvent<Rectangle2D> ev) {
	                Rectangle2D r = ev.getData();
	                
	                System.err.println( r.getWidth() + "  " + r.getHeight() );
	                //stage.setWidth(r.getWidth());
	                //stage.setHeight(r.getHeight());
	                scene.getWindow().setWidth( r.getWidth() );
	                scene.getWindow().setHeight( r.getHeight() );
	            }
		 });*/
		
		final ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem("Save");
        mi.setOnAction( new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent arg0) {
				SnapshotParameters sp = new SnapshotParameters();
				//sp.
				WritableImage fximg = wv.snapshot( sp, null );
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("/Users/sigmar/Desktop/chart.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        menu.getItems().add( mi );
        scene.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
        	 @Override
             public void handle(javafx.scene.input.MouseEvent event) {
               //if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                 menu.show(wv, event.getScreenX(), event.getScreenY());
               //}
             }
        });
		/*File file = new File("/Users/sigmar/Desktop/chart.png");
		WritableImage wi = wv.snapshot( new SnapshotParameters(), null );
		try {
		    ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", file);
		} catch (IOException e) {}*/
		
        /*Text  text  =  new  Text();
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");
        root.getChildren().add(text);*/
        return (scene);
    }
	
	public void initAndShowGUI( final String webp ) {
        // This method is invoked on Swing thread
        JFrame frame = new JFrame("FX");
        frame.setSize(800, 600);
        
        final Dimension dim = new Dimension(1920*2, 1024*2);
        final BufferedImage bimg = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bimg.createGraphics();
        
		/*final JFXPanel fxPanel = new JFXPanel() {
        	public void paintComponent( Graphics g ) {
        		super.paintComponent( g );
        	}
        };
        fxPanel.addMouseListener( new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				fxPanel.setPreferredSize( dim );
		        fxPanel.setSize( dim );
		        fxPanel.invalidate();
		        fxPanel.revalidate();
		        fxPanel.repaint();
		        
				fxPanel.paint( g2 );
				try {
					ImageIO.write( bimg, "png", new File("/home/sigmar/erm.png") );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
        
        fxPanel.setPreferredSize( dim );
        fxPanel.setSize( dim );
        JScrollPane	scroll = new JScrollPane( fxPanel );
        frame.add( scroll );
        frame.setVisible(true);
        
        fxPanel.addComponentListener( new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				int w = e.getComponent().getWidth();
				int h = e.getComponent().getHeight();
				
				System.err.println( e.getComponent().getWidth() + " c " + e.getComponent().getHeight() );
				BufferedImage bimg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = bimg.createGraphics();
				fxPanel.print( g2 );
				try {
					ImageIO.write( bimg, "png", new File("/home/sigmar/erm.png") );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, webp);
            }
        });*/
    }

    public void initFX(JFXPanel fxPanel, String webp) {
        Scene scene = createScene( webp );
        fxPanel.setScene(scene);
    }
    
    public void initFXChart( JFXPanel fxPanel, String[] names, double[] xdata, double[] ydata ) {
        Scene scene = createScene( names, xdata, ydata );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initWebPage( JFXPanel fxPanel, String webp ) {
        Scene scene = createScene( webp );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initDualPieChart( JFXPanel fxPanel, Map<Character,Integer> map, Map<Character,Integer> mip ) {
        Scene scene = createDualPieChartScene( map, mip );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initStackedBarChart( JFXPanel fxPanel, Map<String,String> all, Map<String,Map<Character,Integer>> map, boolean uniform ) {
        Scene scene = createStackedBarChartScene( all, map, uniform );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initStackedBarChart( JFXPanel fxPanel, List<StackBarData> lsbd, String[] categories ) {
        Scene scene = createStackedBarChartScene( lsbd, categories );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initBarChart( JFXPanel fxPanel, String[] names, int[] xdata, String xTitle, String yTitle, double start, double stop, double step, String title ) {
    	XYChart.Series<String,Number> data = new XYChart.Series<String,Number>();
        //core.setName("Core: " + xdata[xdata.length-1] );
        for( int i = 0; i < xdata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( names[i], xdata[i] );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	data.getData().add( d );
        }
        
        Scene scene = createBarChartScene( names, data, xTitle, yTitle, start, stop, step, title );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initBarChart( JFXPanel fxPanel, String[] names, double[] xdata, String xTitle, String yTitle, double start, double stop, double step, String title ) {
    	XYChart.Series<String,Number> data = new XYChart.Series<String,Number>();
        //core.setName("Core: " + xdata[xdata.length-1] );
        for( int i = 0; i < xdata.length; i++ ) {
        	String name = names[i];
        	double dval = xdata[i];
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( name, dval );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	data.getData().add( d );
        }
        
    	Scene scene = createBarChartScene( names, data, xTitle, yTitle, start, stop, step, title );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
	
	public void viggo( String fastapath, String qualpath, String blastoutpath, String resultpath ) throws IOException {
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
		FileReader fr = new FileReader("thermustype.blastout");
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
		int[] iv = { 0, 10, 16, 50, 100, 100, 60, 30, 500, 150, 500, 500, 30, 50, 200};//, 30, 30 };
		for (int i = 0; i < iv.length-1; i++) {
			iv[i] += 1;
		}
		int[] isum = new int[iv.length];
		isum[0] = iv[0];
		for (int i = 1; i < iv.length; i++) {
			isum[i] = isum[i - 1] + iv[i];
		}

		FileOutputStream fos = new FileOutputStream("noname.txt");
		PrintStream pos = new PrintStream(fos);
		//pos.println( "name\tacc\tspecies\tlen\tident\tdoi\tpubmed\tjournal\tauth\tsub_auth\tsub_date\tcountry\tsource\ttemp\tpH" );
		pos.println( "name\tacc\tfullname\tspecies\tlen\tident\tcountry\tsource\tdoi\tpubmed\tauthor\tjournal\tsub_auth\tsub_date\tlat_lon\tdate\ttitle\tcolor\ttemp\tpH" );
		
		fr = new FileReader("export.nds");
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
			String title = line.substring( isum[13], Math.min( isum[14], line.length() ) ).trim();
			//String length = line.substring(isum[14], isum[15]).trim();
			//String arb = isum.length > 16 && isum[16] <= line.length() ? line.substring(isum[15], isum[16]).trim() : "";
			
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
				pos.println(name + "\t" + acc + "\t" + fullname + "\t" + teg.name + "\t" + teg.len + "\t" + teg.id + "\t" + country + "\t" + source + "\t" + doi + "\t" + pubmed + "\t" + author + "\t" + journal + "\t" + sub_auth + "\t" + sub_date + "\t" + lat_lon + "\t" + date + "\t" + title /*+ "\t" + arb*/ + "\t" + teg.color + "\t\t" );
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
	
	public static void dummy() throws IOException {}

	interface RunnableResult {
		public void run( String res );
	};
	
	ChatServer cs;
	public GeneSet() {
		super();
		
		try {
			cs = new ChatServer( 8887 ) {
				String evalstr = "0.00001";
				@Override
				public void onMessage( WebSocket conn, String message ) {
					//System.err.println( message );
					if( message.startsWith(">") ) {
						final RunnableResult run = new RunnableResult() {
							public void run( String resl ) {
								//BufferedReader br = new BufferedReader( new StringReader( resl ) );
								/*try {
									String line = br.readLine();
									while( line != null ) {
										/*if( rr != null ) {
											rr.run( line+"\n" );
											//res += line+"\n";
										}*
										
										String res = line+"\n";
										if( line.startsWith("> ") ) {
											int i = line.indexOf(' ', 2);
											if( i == -1 ) i = line.length();
											String id = line.substring(2, i);
											
											/*line = br.readLine();
											while( line != null && !line.startsWith("Query=") && !line.startsWith(">") ) {
												res += line+"\n";
												line = br.readLine();
											}*/
									
								if( resl.equals("close") ) cs.sendToAll(resl);
								else {
									byte[] bb = Base64.getEncoder().encode(resl.getBytes());
												
									int i = resl.indexOf(' ', 2);
									int k = resl.indexOf('\n', 2);
									if( i == -1 ) i = resl.length();
									if( k == -1 ) k = resl.length();
									
									i = Math.min(i, k);
									
									String id = resl.substring(2, i);
									Gene g = genemap.get( id );
									if( g != null ) {
										GeneGroup gg = g.getGeneGroup();
										
										String ec = gg.getCommonEc();
										Set<String> kegg = new TreeSet<String>();
										
										for( String pathw : pathwaymap.keySet() ) {
											Set<String> ecs = pathwaymap.get( pathw );
											int u = pathw.lastIndexOf(' ');
											if( ecs.contains(ec) ) kegg.add( pathw.substring(u) + "=" + pathw.substring(0,u).replace(",", "") );
										}
										
										Cog cog = gg.getCommonCog(cogmap);
										String cogid = cog != null ? cog.id : null;
										String symbol = gg.getCommonSymbol();
										if( cog != null && symbol == null ) symbol = cog.genesymbol;
										
										String seqstr = g.id + "\t" + gg.getCommonName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + cazymap.get(g.refid) + "\t" + gg.getCommonGO(false, true, null) + "\t" + kegg + "\t" + g.getSpecies() + "\n";
										//String old =  g.id + "\t" + gg.getCommonName() + "\t" + gg.getCommonSymbol() + "\t" + gg.getCommonEc() + "\t" + gg.getCommonCazy(cazymap) + "\t" + gg.getCommonGO(false, true, null) + "\t" + g.getSpecies() + "\t" + new String(bb) + "\n";
										cs.sendToAll( seqstr );
									}
								}
								//cs.sendToAll( res );
							}
						};
						doBlast( message, evalstr, true, run );
					} else if( message.contains("ready") ) {
						cs.sendToAll( "simmi" ); //cs.message );
					} else if( message.contains("query:") ) {
						StringBuilder sb = new StringBuilder();
						//int i = 0;
						
						String querystr = message.substring(6);
						String[] sp = querystr.split(",");
						Set<String> str = new HashSet<String>( Arrays.asList(sp) );
						for( Gene g : genelist ) {
							String gref = g.name;
							
							boolean cont = false;
							for( String sval : str ) {
								cont = gref.toLowerCase().contains(sval);
							}
							if( cont ) {
								
								GeneGroup gg = g.getGeneGroup();
								String ec = gg.getCommonEc();
								Set<String> kegg = new TreeSet<String>();
								
								for( String pathw : pathwaymap.keySet() ) {
									Set<String> ecs = pathwaymap.get( pathw );
									int k = pathw.lastIndexOf(' ');
									if( ecs.contains(ec) ) kegg.add( pathw.substring(k) + "=" + pathw.substring(0,k).replace(",", "") );
								}
								
								Cog cog = gg.getCommonCog(cogmap);
								String cogid = cog != null ? cog.id : null;
								String symbol = gg.getCommonSymbol();
								if( cog != null && symbol == null ) symbol = cog.genesymbol;
								String seqstr = g.id + "\t" + gg.getCommonName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + cazymap.get(g.refid) + "\t" + gg.getCommonGO(false, true, null) + "\t" + kegg + "\t" + g.getSpecies() + "\n";
								sb.append( seqstr );
							} else {
								String cazy = cazymap.get( g.refid );
								if( cazy != null ) {
									String ec = g.getGeneGroup().getCommonEc();
									String kegg = null;
									
									for( String pathw : pathwaymap.keySet() ) {
										Set<String> ecs = pathwaymap.get( pathw );
										if( ecs.contains(ec) ) kegg = pathw.substring(pathw.lastIndexOf(' '));
									}
									
									int i = cazy.indexOf('(');
									if( i == -1 ) i = cazy.length();
									cazy = cazy.substring(0,i);
									if( str.contains(cazy) ) {
										GeneGroup gg = g.getGeneGroup();
										Cog cog = gg.getCommonCog(cogmap);
										String cogid = cog != null ? cog.id : null;
										String symbol = gg.getCommonSymbol();
										if( cog != null && symbol == null ) symbol = cog.genesymbol;
										sb.append( g.id + "\t" + gg.getCommonName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + cazymap.get(g.refid) + "\t" + gg.getCommonGO(false, true, null) + "\t" + kegg + "\t" + g.getSpecies() + "\n" );
									}
								}
							}
						}
						/*for( Gene g : genelist ) {
							GeneGroup gg = g.getGeneGroup();
							sb.append( g.id + "\t" + gg.getCommonName() + "\t" + gg.getCommonSymbol() + "\t" + gg.getCommonEc() + "\t" + gg.getCommonCazy(cazymap) + "\t" + gg.getCommonGO(true, null) + "\n" );
						
							if( i++ > 10 ) break;
						}*/
						if( sb.length() > 0 ) {
							cs.sendToAll( sb.toString() );
							cs.sendToAll( "close" );
						}
					} else if( message.contains("request:") ) {
						String idlist = message.substring(8).trim();
						String[] split = idlist.split(",");
						StringBuilder sb = new StringBuilder();
						for( String id : split ) {
							Gene g = refmap.get(id);
							if( g != null ) {
								try {
									g.getFasta( sb, false );
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						cs.sendToAll( sb.toString() );
					} else if( message.contains("evalue:") ) {
						evalstr = message.substring(7).trim();
					} else if( message.contains("cazy:") ) {
						Set<String> cz = new TreeSet<String>();
						for( Gene g : genelist ) {
							String cazy = cazymap.get( g.refid );
							if( cazy != null ) {
								int i = cazy.indexOf('(');
								if( i == -1 ) i = cazy.length();
								cazy = cazy.substring(0,i);
								cz.add( cazy );
							}
						}
						
						StringBuilder sb = new StringBuilder();
						sb.append("cazy:");
						for( String cazy : cz ) {
							if( sb.length() == 0 ) sb.append( cazy );
							else sb.append( ","+cazy );
						}
						cs.sendToAll( sb.toString() );
					}
				}
			};
			cs.start();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if( args.length == 0 ) {
		
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
		} else {
			Serifier.main(args);
		}
		 

		// System.err.println( Runtime.getRuntime().availableProcessors() );

		/*
		 * try { testbmatrix("/home/sigmar/mynd.png"); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		//init( args );

		try {
			//List<Set<String>> sc = loadSimpleClusters( Files.newBufferedReader( Paths.get("/Users/sigmar/clusters.txt") ) );
			//System.err.println( sc.size() );
				
			//ftp://ftp.ebi.ac.uk/pub/databases/GO/goa/UNIPROT/gene_association.goa_uniprot.gz
			
	//		FileInputStream fi = new FileInputStream( "/data/gene_association.goa_uniprot.gz" );
	//		GZIPInputStream gi = new GZIPInputStream( fi );
	//		funcMappingStatic( new InputStreamReader( gi ) );
			
			/*Map<String,String>	sp2ko = new HashMap<String,String>();
			FileReader fr = new FileReader("/vg454flx/sp2ko.txt");
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				sp2ko.put( split[0], split[2] );
				line = br.readLine();
			}
			fr.close();
			
			FileWriter fw = new FileWriter("/vg454flx/ko2go.txt");
			InputStreamReader isr = new InputStreamReader( new GZIPInputStream( new FileInputStream("/vg454flx/sp2go.txt.gz") ) );
			br = new BufferedReader( isr );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split(" = ");
				String ko = sp2ko.get( split[0] );
				if( ko != null ) fw.write( ko + " = " + split[1] + "\n" );
				line = br.readLine();
			}
			isr.close();
			fw.close();*/
			
			//simmi();
			
			/*Map<String,String>							all = new TreeMap<String,String>();
			Map<String, Map<String,Integer>> 			map = new TreeMap<String, Map<String,Integer>>();
			
			FileReader fw = new FileReader("/home/sigmar/meta/cogmeta14.blastout");
			gs.cogCalc( "MET14", fw, all, map );
			fw.close();
			fw = new FileReader("/home/sigmar/meta/cogmeta567.blastout");
			gs.cogCalc( "MET567", fw, all, map );
			fw.close();
			
			StringWriter sw = gs.writeCog( all, map );
			System.out.println( sw );*/
			
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

	public Map<String, Gene> idMapping(String blastfile, String idfile, Writer outfile, int ind, int secind, Map<String,Gene> genmap, Map<String,Gene> gimap) throws IOException {
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

		return idMapping(new FileReader(idfile), outfile, ind, secind, refids, genmap, gimap);
	}
	
	public Map<String,String> loadDesignations( InputStreamReader id, Set<String> deset ) throws IOException {
		Map<String,String>	ret = new TreeMap<String,String>();
		
		BufferedReader br = new BufferedReader( id );
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if( split.length > 1 ) {
				ret.put( split[0], split[1] );
				deset.add( split[1] );
			}
			
			line = br.readLine();
		}
		//br.close();
		
		return ret;
	}
	
	public Set<String> loadPlasmids( InputStreamReader id ) throws IOException {
		Set<String>	ret = new HashSet<String>();
		
		BufferedReader br = new BufferedReader( id );
		String line = br.readLine();
		while (line != null) {
			ret.add( line );
			
			line = br.readLine();
		}
		//br.close();
		
		return ret;
	}
	
	public Map<String,String> ko2nameMapping( InputStreamReader id ) throws IOException {
		Map<String,String>	ko2name = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader( id );
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if( split.length > 1 ) ko2name.put( split[0], split[1] );
			
			line = br.readLine();
		}
		//br.close();
		
		return ko2name;
	}
	
	public void loadSmap( Reader rd, Map<String,Gene> unimap ) throws IOException {
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if( unimap.containsKey(split[0]) ) {
				Gene g = unimap.get( split[0] );
				g.symbol = split[1];
			}
			line = br.readLine();
		}
	}
	
	public void uni2symbol( Reader rd, BufferedWriter bw, Map<String,Gene> unimap ) throws IOException {
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if( unimap.containsKey( split[0] ) ) {
				Gene g = unimap.get( split[0] );
				g.symbol = split[1];
				
				bw.write(line+"\n");
			}
			line = br.readLine();
		}
	}

	public Map<String, Gene> idMapping( Reader rd, Writer ps, int ind, int secind, Map<String, Gene> refids, Map<String, Gene> genmap, Map<String,Gene> gimap ) throws IOException {
		Map<String, Gene> 	unimap = new HashMap<String, Gene>();
		Map<String, String> ref2kegg = new HashMap<String, String>();
		Map<String, String> ref2pdb = new HashMap<String, String>();
		Map<String, String> ref2ko = new HashMap<String, String>();
		Map<String, String> ref2cog = new HashMap<String, String>();

		/*PrintWriter ps = null;
		if( out != null ) {
			ps = new PrintWriter( out );
		}*/
		
		/*PrintStream ps = null;
		if (outfile != null) {
			ps = new PrintStream(outfile);
			System.setOut(ps);
		}*/

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
					if (tone && genmap != null) {
						for (String sstr : list) {
							String[] spl = sstr.split("\t");
							if (sstr.contains("KEGG")) {
								ref2kegg.put(spl[0], spl[2]);
							} else if (sstr.contains("PDB")) {
								ref2pdb.put(spl[0], spl[2]);
							} else if (spl[1].contains("KO")) {
								ref2ko.put(spl[0], spl[2]);
							} else if (spl[1].contains("eggNOG")) {
								ref2cog.put(spl[0], spl[2]);
							}
						}
					}

					if (ps != null && tone) {
						for (String sstr : list) {
							ps.write(sstr+"\n");
						}
						tone = false;
					}
					list.clear();
				}
				list.add(line);

				if( genmap != null ) {
					if( split[1].startsWith("RefSeq") ) {
						String refid = split[ind];
						if( refids.containsKey(refid) ) {
							Gene gene = refids.get(refid);
							gene.uniid = split[secind];
							unimap.put(gene.uniid, gene);
							
							gene.allids.add(split[secind]);
							tone = true;
						}
					} else if( split[1].equals("GI") ) {
						String gid = "GI"+split[ind];
						if( gimap.containsKey(gid) ) {
							Gene gene = gimap.get(gid);
							gene.uniid = split[secind];
							unimap.put(gene.uniid, gene);
							
							gene.allids.add(split[secind]);
							tone = true;
						}
					} else if( split[1].equals("GeneID") ) {
						String genid = split[ind];
						if( genmap.containsKey(genid) ) {
							Gene gene = genmap.get(genid);
							gene.uniid = split[secind];
							unimap.put(gene.uniid, gene);
							
							gene.allids.add(split[secind]);
							tone = true;
						}
					}
				} else {
					String refid = split[ind];
					if( refids.containsKey(refid) ) {
						Gene gene = refids.get(refid);
						String genid = split[secind];
						gene.genid = genid;
						unimap.put(genid, gene);
						if( gene.koname == null || gene.koname.length() == 0 && split.length > 15 ) {
							gene.koname = split[15];
							//if( gene.koname.startsWith("dna") ) System.err.println( gene.koname );
						}
						
						gene.allids.add(genid);
						String gi = "GI"+split[6];
						gene.allids.add(gi);
						
						gimap.put( gi, gene );
						
						tone = true;
					}
					
					/*if( gene.allids == null ) {
						System.err.println();
					}*/
				}
			}

			if (split.length > secind) {
				last = split[secind];
			}
			line = br.readLine();
		}

		if (tone && genmap != null) {
			for (String sstr : list) {
				String[] spl = sstr.split("\t");
				if (sstr.contains("KEGG")) {
					ref2kegg.put(spl[0], spl[2]);
				} else if (sstr.contains("PDB")) {
					ref2pdb.put(spl[0], spl[2]);
				} else if (spl[1].contains("KO")) {
					if( spl[2].contains("00999") ) {
						System.err.println();
					}
					ref2ko.put(spl[0], spl[2]);
				} else if (spl[1].contains("eggNOG")) {
					ref2cog.put(spl[0], spl[2]);
				}
			}
		}

		if (ps != null && tone) {
			for (String sstr : list) {
				ps.write(sstr+"\n");
			}
			tone = false;
		}
		list.clear();
		//br.close();

		if ( genmap != null ) {
			for (String s : refids.keySet()) {
				Gene g = refids.get(s);
				if (g.allids != null)
					for (String id : g.allids) {
						if( ref2kegg.containsKey(id) ) {
							g.keggid = ref2kegg.get(id);
						}

						if( ref2pdb.containsKey(id) ) {
							g.pdbid = ref2pdb.get(id);
						}
						
						if( ref2ko.containsKey(id) ) {
							String koid = ref2ko.get(id);
							if( koid.contains("00999") ) {
								System.err.println();
							}
							g.koid = koid;
						}
						
						if( ref2cog.containsKey(id) ) {
							String cogid = ref2cog.get(id);
							g.cog = new Cog( cogid, null, null, null );
						}

						if( g.keggid != null && g.pdbid != null && g.koid != null && g.cog != null )
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

	public void funcMapping(Reader rd, Map<String, Gene> genids, String outshort) throws IOException {
		//Map<String, Function> funcmap = new HashMap<String, Function>();

		FileWriter fw = null;
		if (outshort != null)
			fw = new FileWriter(outshort);

		// FileReader fr = new FileReader( gene2go );
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			if( split.length > 1 && genids.containsKey(split[1]) ) {
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
	
	public static void funcMappingStatic( Reader rd ) throws IOException {
		//Map<String,Set<String>> unipGo = new HashMap<String,Set<String>>();
		Map<String,String>	symbolmap = new HashMap<String,String>();
		FileWriter fw = new FileWriter("/home/sigmar/sp2go.txt");
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String prev = null;
		while (line != null) {
			if( !line.startsWith("!") ) {
				String[] split = line.split("\t");
				if( split.length > 4 ) {
					String id = split[1];
					String sm = split[2];
					String go = split[4];
					symbolmap.put(id, sm);
					if( go.startsWith("GO:") ) {
						if( !id.equals( prev ) ) {
							if( prev == null ) fw.write( id + " = " + go );
							else fw.write( "\n" + id + " = " + go );
						} else fw.write( " " + go );
					}
					prev = split[1];
				}
			}
			line = br.readLine();
		}
		br.close();
		fw.close();
		
		fw = new FileWriter( "/home/sigmar/smap.txt" );
		for( String id : symbolmap.keySet() ) {
			String sm = symbolmap.get( id );
			fw.write(id + "\t" + sm + "\n");
		}
		fw.close();
	}

	public void funcMappingUni(BufferedReader br, Map<String, Gene> uniids, BufferedWriter bw ) throws IOException {
		// FileReader fr = new FileReader( sp2go );
		//BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.split("=");
			if (split.length > 1 && uniids.containsKey(split[0].trim())) {
				Gene gene = uniids.get(split[0].trim());
				if (gene.funcentries == null)
					gene.funcentries = new HashSet<Function>();
				
				for( String erm : split[1].trim().split("[\t ]+") ) {
					Function func;
					if( !funcmap.containsKey( erm ) ) {
						func = new Function( erm );
						funcmap.put( erm, func );
					} else func = funcmap.get( erm );
					gene.funcentries.add( func );
				}
				if (bw != null) bw.write(line + "\n");
			}

			line = br.readLine();
		}
		//br.close();
	}

	public void updateFilter(JTable table, RowFilter filter, JLabel label) {
		DefaultRowSorter<TableModel, Integer> rowsorter = (DefaultRowSorter<TableModel, Integer>) table.getRowSorter();
		rowsorter.setRowFilter(filter);
		if (label != null)
			label.setText(table.getRowCount() + "/" + table.getSelectedRowCount());
	}
	
	public void scrollToSelection( JTable table ) {
		int r = table.getSelectedRow();
		Rectangle rct = table.getCellRect(r, 0, true);
		table.scrollRectToVisible( rct );
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

	private void proxi(JTable table, int[] rr, List<Gene> genelist, Set<Integer> genefilterset, boolean remove) {
		/*Set<String> ct = new HashSet<String>();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			
			GeneGroup gg;
			if( table.getModel() == groupModel ) {
				gg = allgenegroups.get( cr );
			} else {
				gg = genelist.get(cr).getGeneGroup();
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
			
			GeneGroup gg;
			if( table.getModel() == groupModel ) {
				gg = allgenegroups.get( cr );
			} else {
				gg = genelist.get(cr).getGeneGroup();
			}
			
			genefilterset.add( gg.index );
			for( Gene g : gg.genes ) {
				Tegeval next = g.tegeval.getNext();
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
							Contig ctg = contigmap.get( cstr );
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
				Tegeval prev = g.tegeval.getPrevious();
				if( prev != null ) {
					GeneGroup pgg = prev.getGene().getGeneGroup();
					if( pgg != null ) genefilterset.add( pgg.index );
					else {
						System.err.println();
					}
				}
			}
		}
		
		/*if( table.getModel() == groupModel ) {
			for( Gene g : genelist ) {
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
			for( Gene g : genelist ) {
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
	
	public List getSelspecContigs( List<JComponent> complist, final String ... selspec ) {
		List<Contig>				contigs = null;
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
					List<Contig>	contigs = speccontigMap.get( spec );
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
				return Contig.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int 		r = stable.getSelectedRow();
				String 		spec = selspec.length > 0 ? selspec[0] : (String)stable.getValueAt(r, 0);
				List<Contig>	contigs = speccontigMap.get( spec );
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
				List<Contig> ctgs = speccontigMap.get( spec );
				rr = ctable.getSelectedRows();
				contigs = new ArrayList<Contig>();
				for( int r : rr ) {
					int i = ctable.convertRowIndexToModel(r);
					contigs.add( ctgs.get(i) );
				}
			}
			
			return contigs;
		}
	}
	
	/*public Set<String> getSelspec( Component comp, final List<String>	specs ) {
		return getSelspec( comp, specs, null );
	}*/
	
	public Set<String> getSelspec( Component comp, final List<String>	specs, final JCheckBox ... contigs ) {
		final JTable	table = new JTable();
		JScrollPane	scroll = new JScrollPane( table );
		table.setAutoCreateRowSorter( true );
		
		final List<Contig> ctgs = new ArrayList<Contig>( contigmap.values() );
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
		Set<String>	selspec = new TreeSet<String>();
		for( int i = 0; i < table.getRowCount(); i++ ) {
			if( table.isRowSelected(i) ) selspec.add( (String)table.getValueAt(i, 0) );
		}
		return selspec;
	}
	
	public int containmentCount( Set<String> set1, Set<String> set2 ) {
		int r = 0;
		for( String s1 : set1 ) {
			if( set2.contains( s1 ) ) r++;
		}
		return r;
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
				GeneGroup gg = allgenegroups.get(cr);
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
				Gene g = genelist.get(cr);
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
	
	public Serifier getConcatenatedSequences( boolean proximityJoin ) {
		Map<String,Map<Sequence,String>>	smap = new HashMap<String,Map<Sequence,String>>();
		Set<String>							specset = new HashSet<String>();
		Map<GeneGroup,Integer>				genegroups = new HashMap<GeneGroup,Integer>();
		int[] rr = table.getSelectedRows();
		if( table.getModel() == groupModel ) {
			for (int r : rr) {
				int cr = table.convertRowIndexToModel(r);
				int max = 0;
				GeneGroup gg = allgenegroups.get(cr);
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
				Gene g = genelist.get(cr);
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
		
		Map<String,Set<Tegeval>>	donetvs = new HashMap<String,Set<Tegeval>>();
		for( GeneGroup ggroup : genegroups.keySet() ) {
			int len = genegroups.get( ggroup );
			for( String spec : specset ) {
				List<Tegeval> ltv = ggroup.getTegevals(spec);
				
				/*if( selspec.contains("hermus") ) spec = selspec;
				else {
					Matcher m = Pattern.compile("\\d").matcher(selspec); 
					int firstDigitLocation = m.find() ? m.start() : 0;
					if( firstDigitLocation == 0 ) spec = "Thermus_" + selspec;
					else spec = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
				}*/
				
				if( !proximityJoin ) {
					Map<Sequence,String> seqs;
					if( smap.containsKey( spec ) ) {
						seqs = smap.get( spec );
						Map<Sequence,String> addseqs = new HashMap<Sequence,String>();
					
						for( Sequence seq : seqs.keySet() ) {
							String loc = seqs.get( seq );
							boolean first = true;
							if( ltv != null && ltv.size() > 0 ) {
								for( Tegeval tv : ltv ) {
									Sequence tseq;
									if( !first ) {
										tseq = new Sequence( nameFix(spec), null );
										addseqs.put( tseq, loc );
										tseq.append( seq.sb.subSequence(0, seq.length()-len) );
									} else tseq = seq;
									
									//seqs.add( tseq );
									
									StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
									if( seqstr != null && seqstr.length() > 0 ) {
										if( seqstr.length() != len ) {
											System.err.println( "        bleh " + spec + "  " + seqstr.length() + "   " + ggroup.size() + "  " + ggroup.getCommonName() + "  " + ggroup.getIndex() );
											for( Gene g : ggroup.genes ) {
												System.err.println( g.getSpecies() + "  " + g.tegeval.getAlignedSequence().length() + "   " + g.id);
											}
										}
										tseq.append( seqstr );
									} else {
										for( int i = 0; i < len; i++ ) tseq.append( "-" );
									}
									first = false;
								}
							} else /*if( first )*/ {
								for( int i = 0; i < len; i++ ) seq.append( "-" );
							}
						}
						seqs.putAll( addseqs );
					} else {
						seqs = new HashMap<Sequence,String>();
						
						if( ltv != null && ltv.size() > 0 ) {
							for( Tegeval tv : ltv ) {
								Sequence seq = new Sequence( nameFix(spec), null );
								seqs.put( seq, tv.getContshort().getName() );
								
								Sequence aseq = tv.getAlignedSequence();
								StringBuilder seqstr = aseq != null ? aseq.getStringBuilder() : null;
								if( seqstr != null && seqstr.length() > 0 ) {
									seq.append( seqstr );
								} else {
									for( int i = 0; i < len; i++ ) seq.append( "-" );
								}
							}
						} else {
							Sequence tseq = new Sequence( nameFix(spec), null );
							seqs.put( tseq, null );
							for( int i = 0; i < len; i++ ) tseq.append( "-" );
						}
						/*for( Tegeval tv : ltv ) {
							Sequence seq = new Sequence( spec, null );
							seqs.add( seq );
							
							StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
							if( seqstr != null && seqstr.length() > 0 ) {
								seq.append( seqstr );
							} else {
								for( int i = 0; i < len; i++ ) seq.append( "-" );
							}
						}*/
						smap.put( spec, seqs );
					}
				} else {
					Map<Sequence,String> 		seqs;
					
					Set<Tegeval>	tvals;
					if( donetvs.containsKey(spec) ) {
						tvals = donetvs.get( spec );
					} else {
						tvals = new HashSet<Tegeval>();
						donetvs.put( spec, tvals );
					}
					
					if( smap.containsKey( spec ) ) {
						seqs = smap.get( spec );
						Map<Sequence,String> 	addseqs = new HashMap<Sequence,String>();
						Set<Tegeval>			accountedfor = new HashSet<Tegeval>();
						
						for( Sequence seq : seqs.keySet() ) {
							String loc = seqs.get( seq );
							boolean first = true;
							for( Tegeval tv : ltv ) {
								//if( tvals.add( tv ) ) {
									if( loc == null || tv.getContshort().getName().equals(loc) ) {
										accountedfor.add( tv );
										Sequence tseq;
										if( !first ) {
											tseq = new Sequence( nameFix( spec ), null );
											tseq.append( seq.sb.subSequence(0, seq.length()-len) );
										} else tseq = seq;
										StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
										if( seqstr != null && seqstr.length() > 0 ) {
											tseq.append( seqstr );
										} else {
											for( int i = 0; i < len; i++ ) tseq.append( "-" );
										}
										first = false;
										addseqs.put( tseq, loc == null ? tv.getContshort().getName() : loc );
									}
								//}
							}
						}
						
						for( Sequence seq : seqs.keySet() ) {
							if( !addseqs.containsKey( seq ) ) {
								for( int i = 0; i < len; i++ ) seq.append( "-" );
								addseqs.put( seq, seqs.get(seq) );
							}
							
									/*boolean check = false;
									for( Sequence sseq : seqs.keySet() ) {
										String sloc = seqs.get( sseq );
										
										if( sloc == null || tv.getContshort().getName().equals(sloc) ) {
											check = true;
											break;
										}
									}
									
									if( !check ) {
										Sequence tseq;
										if( !first ) {
											tseq = new Sequence( spec, null );
											addseqs.put( tseq, loc );
											tseq.append( seq.sb.subSequence(0, seq.length()-len) );
										} else {
											tseq = new Sequence( spec, null );
											addseqs.put( tseq, loc );
											for( int i = 0; i < len; i++ ) tseq.append( "-" );
											//tseq.append( seq.sb.subSequence(0, seq.length()-len) );
											
											//tseq = seq;
										}
										
										//for( int i = 0; i < len; i++ ) tseq.append( "-" );
									}/* else {
										Sequence tseq = new Sequence( spec, null );
										for( int i = 0; i < len; i++ ) tseq.append( "-" );
										addseqs.put( tseq, loc );
										tseq.append( seq.sb.subSequence(0, seq.length()-len) );
									}
								}*/
								
								/*if( first ) {
									Sequence tseq;
									if( !first ) {
										tseq = new Sequence( spec, null );
										addseqs.put( tseq, loc );
										tseq.append( seq.sb.subSequence(0, seq.length()-len) );
									} else tseq = seq;
									
									for( int i = 0; i < len; i++ ) tseq.append( "-" );
								}*/
						}
						
						Sequence seq = null;
						for( Sequence tseq : seqs.keySet() ) {
							seq = tseq;
						}
						
						for( Tegeval tv : ltv ) {
							//if( tvals.add( tv ) ) {
								if( !accountedfor.contains(tv) ) {
									Sequence tseq = new Sequence( nameFix(spec), null );
									for( int i = 0; i < seq.length()-len; i++ ) tseq.append( "-" );
									StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
									if( seqstr != null && seqstr.length() > 0 ) {
										tseq.append( seqstr );
									} else {
										for( int i = 0; i < len; i++ ) tseq.append( "-" );
									}
									//tseq.append( seq.sb.subSequence(0, seq.length()-len) );
									addseqs.put( tseq, tv.getContshort().getName() );
								}
							//}
						}
						seqs.putAll( addseqs );
					} else {
						seqs = new HashMap<Sequence,String>();
						if( ltv != null && ltv.size() > 0 ) {
							for( Tegeval tv : ltv ) {
								//if( tvals.add( tv ) ) {
									Sequence seq = new Sequence( nameFix(spec), null );
									seqs.put( seq, tv.getContshort().getName() );
									
									StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
									if( seqstr != null && seqstr.length() > 0 ) {
										seq.append( seqstr );
									} else {
										for( int i = 0; i < len; i++ ) seq.append( "-" );
									}
								//}
							}
						} else {
							Sequence tseq = new Sequence( nameFix(spec), null );
							seqs.put( tseq, null );
							for( int i = 0; i < len; i++ ) tseq.append( "-" );
						}
						smap.put( spec, seqs );
					}
				}
			}
		}
		
		Serifier			serifier = new Serifier();
		for( String spec : smap.keySet() ) {
			Map<Sequence,String> seqs = smap.get( spec );
			for( Sequence seq : seqs.keySet() ) {
				String val = seqs.get(seq);
				serifier.addSequence( seq );
			}
		}
		return serifier;
	}
	
	public void showAlignedSequences( Component comp, Serifier	serifier ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
		jf.initGui(frame);
		jf.updateView();

		frame.setVisible(true);
	}
	
	public void showSomeSequences( Component comp, List<Sequence> lseq ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
		jf.initGui(frame);

		for( Sequence seq : lseq ) {
			serifier.addSequence(seq);
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
	
	public void showSelectedSequences( Component comp, Set<Tegeval> tset, boolean dna, String names ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
		jf.initGui(frame);

		for( Tegeval tv : tset ) {
			Contig cont = tv.getContshort();
			if( cont != null ) {
				String contig = cont.getSpec();//tv.getContig();
				StringBuilder seqstr = dna ? new StringBuilder(tv.getSequence()) : tv.getProteinSequence();
				Sequence seq = new Sequence( getGeneName(names, tv.getGene()), seqstr, serifier.mseq );
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
	
	String nameFix( String selspec ) {
		String ret = selspec;
		if( selspec.contains("Rhodothermus_") ) {
			selspec = selspec.replace("Rhodothermus_", "R.");
			int i = selspec.indexOf("_uid");
			if( i != -1 ) {
				ret = selspec.substring(0,i);
			} else {
				i = selspec.indexOf('_');
				if( i != -1 ) {
					i = selspec.indexOf('_', i+1);
					if( i != -1 ) {
						i = selspec.indexOf('_', i+1);
						if( i != -1 ) {
							i = selspec.lastIndexOf('_', i+1);
							if( i != -1 ) ret = selspec.substring(0, i);
						}
					}
				}
			}
		} else if( selspec.contains("hermus") ) {			
			int i = selspec.indexOf("_uid");
			if( i != -1 ) {
				ret = selspec.substring(0,i);
			} else if(selspec.contains("DSM")) {
				int k = selspec.indexOf("DSM");
				k = selspec.indexOf('_', k+4);
				if( k == -1 ) k = selspec.length();
				ret = selspec.substring(0,k);
			} else {
				
				if( selspec.equals("Thermus_4884") ) ret = "Thermus_aquaticus_4884";
				else if( selspec.equals("Thermus_2121") ) ret = "Thermus_scotoductus_2121";
				
				i = selspec.indexOf('_');
				if( i != -1 ) {
					i = selspec.indexOf('_', i+1);
					if( i != -1 ) {
						i = selspec.indexOf('_', i+1);
						if( i != -1 ) {
							i = selspec.lastIndexOf('_', i+1);
							if( i != -1 ) ret = selspec.substring(0, i);
						}
					}
				}
			}
		} else if( selspec.contains("GenBank") || selspec.contains("MAT") ) {
			
		} else {
			if( selspec.contains("islandicus") ) ret = "Thermus_islandicus_3838";
			
			Matcher m = Pattern.compile("\\d").matcher(selspec); 
			int firstDigitLocation = m.find() ? m.start() : 0;
			if( firstDigitLocation == 0 ) ret = "Thermus_" + selspec;
			else ret = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
		}
		return ret.replace("Thermus_", "T.");
	}
	
	public void showSequences( Component comp, Set<GeneGroup> ggset, boolean dna, Set<String> specs ) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Serifier	serifier = new Serifier();
		JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
		jf.initGui(frame);

		for( GeneGroup ggroup : ggset ) {
		//for( Gene gg : ggroup.genes ) {
			//if (gg.species != null) {
				//for (String sp : gg.species.keySet()) {
				//	Teginfo stv = gg.species.get(sp);
			for( Tegeval tv : ggroup.getTegevals() ) {
				Contig cont = tv.getContshort();
				if( cont != null ) {
					String selspec = cont.getSpec();//tv.getContig();
					if( specs == null || specs.contains(selspec ) ) {
						String spec = nameFix( selspec );
						
						StringBuilder seqstr = dna ? new StringBuilder(tv.getSequence()) : tv.getProteinSequence();
						/*String name = tv.getGene().id;
						String cazy = tv.getGene().getGeneGroup().getCommonCazy(cazymap);
						String ec = tv.getGene().getGeneGroup().getCommonEc();
						if( cazy != null ) name += " " + cazy;
						if( ec != null ) name += " " + ec;*/
						Sequence seq = new Sequence( spec, seqstr, serifier.mseq );
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
	
	public StringBuilder getSelectedASeqs( JTable table, List<Gene> genelist, JApplet applet, Collection<String> species ) {
		StringBuilder sb = new StringBuilder();
		
		Set<String> selectedSpecies = getSelspec( applet, new ArrayList<String>( species ) );
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			//if (gg.species != null) {
			sb.append(gg.name + ":\n");
				//for (String sp : gg.species.keySet()) {
				//for( String sp : selectedSpecies ) {
			if( selectedSpecies.contains(species) ) {
				Tegeval tv = gg.tegeval;
				sb.append(">" + tv.name + " " + tv.teg + " " + tv.eval + "\n");
				StringBuilder protseq = tv.getProteinSequence();
				for (int i = 0; i < tv.getProteinLength(); i += 70) {
					int end = Math.min(i + 70, tv.getProteinLength());
					sb.append( protseq.substring(i, end) + "\n"); // new
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
		return sb;
	}
	
	public static StringBuilder getSelectedSeqs( JTable table, List<Gene> genelist ) {
		StringBuilder sb = new StringBuilder();
		
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Gene gg = genelist.get(cr);
			//if(gg.species != null) {
			sb.append(gg.name + ":\n");
			//for (String sp : gg.species.keySet()) {
			Tegeval tv = gg.tegeval;
			sb.append(">" + tv.name + " " + tv.teg + " " + tv.eval + "\n");
			for (int i = 0; i < tv.getLength(); i += 70) {
				sb.append( tv.getSubstring(i, Math.min(i + 70, tv.getLength())) + "\n");
			}
		}
		
		return sb;
	}
	
	public void cogCalc( String filename, Set<Character> includedCogs, Map<String,Map<Character,Integer>> map, Set<String> selspec, boolean contigs ) throws IOException {		
		if( table.getModel() == groupModel ) {
			for( int r = 0; r < table.getRowCount(); r++ ) {
				int i = table.convertRowIndexToModel(r);
				if( i >= 0 && i < allgenegroups.size() ) {
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
										if( map.containsKey( tv.getSpecies() ) ) {
											submap = map.get( tv.getSpecies() );
											if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
										} else {
											submap = new HashMap<Character,Integer>();
											map.put(spec, submap);
										}
										submap.put( cog.symbol, val+1 );
									}
								}
							}
						}
					}
				}
			}
		} else {
			
		}
		//cogCalc(filename, br, map, selspec, contigs);
	}
	
	public void cogCalc( String filename, BufferedReader br, Map<String,Map<Character,Integer>> map, Set<String> selspec, boolean contigs ) throws IOException {		
		String line = br.readLine();
		String current = null;
		
		int count = 0;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				current = line.substring(7);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					current += line;
					line = br.readLine();
				}
				current = current.trim();
			} else if( line.startsWith(">") ) {
				String val = line.substring(1);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					val += line;
					line = br.readLine();
				}
				val = val.trim();
				
				String spec = null;
				if( filename != null ) {
					spec = filename;
				} else {
					int i = current.lastIndexOf('[');
					int n = current.indexOf(']', i+1);
					
					if( i == -1 || n == -1 ) {
						n = current.indexOf(" ");
					}
					
					String specval = current.substring(i+1, n);
					
					for( String nspec : selspec ) {
						if( specval.contains( nspec ) ) {
							spec = nspec;
							
							String name = null;//names[i];
							if( contigs ) {
								name = nspec;
								/*if( nspec.contains("hermus") ) name = nspec;
								else {
									Matcher m = Pattern.compile("\\d").matcher(nspec); 
									int firstDigitLocation = m.find() ? m.start() : 0;
									if( firstDigitLocation == 0 ) name = "Thermus_" + nspec;
									else name = "Thermus_" + nspec.substring(0,firstDigitLocation) + "_" + nspec.substring(firstDigitLocation);
								}*/
								
								int k = contigIndex(name);
								if( k == -1 ) {
									name = spec;
								} else {
									name = name.substring(k);						
								}
							} else {
								int k = nspec.lastIndexOf('_');
								if( k == -1 ) k = nspec.length(); 
								name = nspec.substring( 0, k );
								/*if( nspec.contains("hermus") ) name = nspec.substring( 0, nspec.lastIndexOf('_') );
								else {
									Matcher m = Pattern.compile("\\d").matcher(nspec); 
									int firstDigitLocation = m.find() ? m.start() : 0;
									if( firstDigitLocation == 0 ) name = "Thermus_" + nspec;
									else name = "Thermus_" + nspec.substring(0,firstDigitLocation) + "_" + nspec.substring(firstDigitLocation);
								}*/
							}
							
							spec = name;
							break;
						}
					}
					/*int k = spec.indexOf("_contig");
					if( k == -1 ) k = spec.indexOf("_scaffold");
					if( k == -1 ) {
						k = spec.indexOf("_uid");
						k = spec.indexOf('_', k+4);
					}
					
					if( k == -1 ) {
						k = spec.indexOf('_');
						k = spec.indexOf('_', k+1);
					}
					if( k != -1 ) spec = spec.substring(0, k);
					if( !spec.contains("_") ) {
						System.err.println();
					}*/
				}
				
				if( spec != null ) {
					int i = val.lastIndexOf('[');
					int n = val.indexOf(']', i+1);
					String cog = val.substring(i+1, n);
					int u = cog.indexOf('/');
					if( u != -1 ) cog = cog.substring(0, u);
					u = cog.indexOf(';');
					if( u != -1 ) cog = cog.substring(0, u);
					String erm = cog.replace("  ", " ");
					while( !erm.equals( cog ) ) {
						cog = erm;
						erm = cog.replace("  ", " ");
					}
					cog = cog.trim();
					String coglong = cog;
					
					/*int ki = coglong.indexOf(' ');
					ki = ki == -1 ? coglong.length() : ki;
					int ui = coglong.indexOf(',');
					ui = ui == -1 ? coglong.length() : ui;
					int ci = coglong.indexOf('-');
					ci = ci == -1 ? coglong.length() : ci;
					
					if( ui != -1 && ui < ki ) {
						ki = ui;
					}
					if( ci != -1 && ci < ki ) {
						ki = ci;
					}
					if( coglong.startsWith("Cell") ) {
						if( ki != -1 ) {
							ki = coglong.indexOf(' ', ki+1);
						}
					}					
					ki = ki == -1 ? coglong.length() : ki;
					cog = coglong.substring(0,ki);*/
					
					Map<Character,Integer> cogmap;
					if( map.containsKey( spec ) ) {
						cogmap = map.get(spec);
					} else {
						cogmap = new HashMap<Character,Integer>();
						map.put( spec, cogmap );
					}
					
					Character cogchar = Cog.cogchar.get( coglong );
					if( cogchar == null ) {
						System.err.print( coglong );
						System.err.println();
					}
					
					count++;
					if( cogmap.containsKey( cogchar ) ) {
						cogmap.put( cogchar, cogmap.get(cogchar)+1 );
					} else cogmap.put( cogchar, 1 );
					
					//all.put( cog, coglong );
				} else {
					System.err.println("no hit");
					count++;
				}
			}
			line = br.readLine();
		}
		System.out.println( "count " + count );
	}
	
	public StringWriter writeSimpleCog( Map<String,Map<Character,Integer>> map ) throws IOException {
		StringWriter fw = new StringWriter();
		fw.write( "['Species" );
		for( String coggroup : Cog.coggroups.keySet() ) {
			fw.write("','"+coggroup);
		}
		fw.write("']");
		for( String s : map.keySet() ) {
			fw.write(",\n");
			fw.write( "['"+s+"'" );
			Map<Character,Integer> cm = map.get( s );
			//for( Character cogchar : Cog.charcog.keySet() ) {
			for( String coggroup : Cog.coggroups.keySet() ) {
				int val = 0;
				Set<Character> groupchars = Cog.coggroups.get(coggroup);
				for( Character cogchar : groupchars ) {
					if( cm.containsKey( cogchar ) ) {
						val += cm.get(cogchar);
					}
				}
				fw.write(","+val);
			}
			fw.write("]");
		}
		fw.close();
		
		return fw;
	}
	
	public StringWriter writeCog( Map<String,Map<Character,Integer>> map, Set<Character> includedCogs ) throws IOException {
		StringWriter fw = new StringWriter();
		fw.write( "['Species" );
		for( Character cogchar : includedCogs ) {
			String coglong = Cog.charcog.get( cogchar );
			fw.write("','"+coglong);
		}
		fw.write("']");
		for( String s : map.keySet() ) {
			fw.write(",\n");
			int total = 0;
			fw.write( "['"+s+"'" );
			Map<Character,Integer> cm = map.get( s );
			for( Character cogchar : includedCogs ) {
				int val = 0;
				//String coglong = Cog.charcog.get(cogchar);
				//Character cogchar = Cog.cogchar.get( coglong );
				if( cm.containsKey( cogchar ) ) {
					val = cm.get(cogchar);
				}// else val = -1;
				fw.write(","+val);
			}
			fw.write("]");
		}
		
		/*fw.write( "Species" );
		for( String cog : all.keySet() ) {
			String coglong = all.get( cog );
			fw.write("\t"+coglong);
		}
		for( String s : map.keySet() ) {
			int total = 0;
			fw.write( "\n"+s );
			Map<String,Integer> cm = map.get( s );
			for( String cog : all.keySet() ) {
				int val = 0;
				if( cm.containsKey( cog ) ) val = cm.get(cog);
				fw.write("\t"+val);
			}
			//fw.write("\n");
		}
		
		/*for( String cog : all ) {
			fw.write( "\n"+cog );
			for( String spec : map.keySet() ) {
				Map<String,Integer> cm = map.get( spec );
				if( cm.containsKey( cog ) ) fw.write( "\t" + cm.get( cog )  );
				else fw.write( "\t" + 0  );
			}
		}*/
		
		fw.close();
		
		return fw;
	}
	
	private void assignGain( Node n, Map<Node,List<GeneGroup>> gainMap, PrintStream ps ) {
		Set<String>	specs = n.getLeaveNames();
		
		List<GeneGroup> lgg = ggSpecMap.get( specs );
		gainMap.put( n, lgg );
		
		ps.println( specs );
		if( lgg != null ) for( GeneGroup gg : lgg ) {
			/*Set<String>	nset = new HashSet<String>();
			for( Gene g : gg.genes ) {
				nset.add( g.name );
			}*/
			ps.println( "\t" + gg.getCommonName() );
		}
		
		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes) {
				assignGain( node, gainMap, ps );
			}
		}
	}
	
	private void assignLoss( Node n, Map<Node,List<GeneGroup>> lossMap, PrintStream ps ) {
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
							/*Set<String>	nset = new HashSet<String>();
							for( Gene g : gg.genes ) {
								nset.add( g.name );
							}*/
							ps.println( "\t" + gg.getCommonName() );
						}
						lossMap.put(nnode, lgg);
					}
				}
				assignLoss( node, lossMap, ps );
			}
		}
	}
	
	public Teginfo getGroupTes( GeneGroup gg, String spec ) {		
		return gg.species.get( spec );
	}
	
	public JTable getGeneTable() {
		return table;
	}
	
	public JTable getFunctionTable() {
		return ftable;
	}
	
	public final class StackBarData {		
		String 				name;
		String				oname;
		Map<String,Integer> b = new HashMap<String,Integer>();
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
					//Gene g = genelist.get(m);
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
	
	public void doBlast( final String fasta, final String evaluestr, final boolean ids, final RunnableResult rr ) {
		File blastn;
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
		}
		
		int procs = Runtime.getRuntime().availableProcessors();
		String[] mcmds = { makeblastdb.getAbsolutePath(), "-dbtype", "prot", "-title", "tmp", "-out", "tmp" };
		List<String>	lcmd = new ArrayList<String>( Arrays.asList(mcmds) );
		
		final ProcessBuilder mpb = new ProcessBuilder( lcmd );
		mpb.redirectErrorStream( true );
		try {
			final Process mp = mpb.start();
			
			new Thread() {
				public void run() {
					try {
						OutputStream pos = mp.getOutputStream();
						for( Gene g : genelist ) {
							if( g.getTag() == null || g.getTag().length() == 0 ) {
								GeneGroup gg = g.getGeneGroup();
							
								if( gg != null ) {
									if( ids ) pos.write( (">" + g.id + "\n").getBytes() );
									else {
										String addstr = "";
										Cog cog = gg.getCommonCog( cogmap );
										String cazy = gg.getCommonCazy( cazymap );
										if( cog != null ) addstr += "_"+cog.id;
										if( cazy != null ) {
											if( addstr.length() > 0 ) addstr += cazy;
											addstr += "_"+cazy;
										}
										if( addstr.length() > 0 ) addstr += "_";
										pos.write( (">" + g.name + addstr + "[" + g.id + "]\n").getBytes() );
									}
									StringBuilder sb = g.tegeval.getProteinSequence();
									for( int i = 0; i < sb.length(); i+=70 ) {
										pos.write( sb.substring(i, Math.min( sb.length(), i+70) ).getBytes() );
									}
									pos.write( '\n' );
								}
							}
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
			
			File blastFile = blastp; //dbType.equals("prot") ? type.equals("prot") ? blastp : blastx : blastn;
			
			String[] 		cmds = { blastFile.getAbsolutePath(), "-query", "-", "-db", "tmp", "-evalue", evaluestr, "-num_threads", Integer.toString(procs) };
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
							if( line.startsWith("> ") ) {
								int i = line.indexOf(' ', 2);
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
										i = genelist.indexOf( g );
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
						
						rr.run("close");
						
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
	
	public void blast() {
		String dbname = null;
		
		/*try {
			Map<String,String> env = new HashMap<String,String>();
			Path path = zipfile.toPath();
			String uristr = "jar:" + path.toUri();
			zipuri = URI.create( uristr /*.replace("file://", "file:")* );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );
			
			final PathMatcher pm = zipfilesystem.getPathMatcher("*.p??");
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
		JOptionPane.showMessageDialog(GeneSet.this, objs);
		
		doBlast( ta.getText(), tf.getText(), true, null );
	}
	
	JRadioButtonMenuItem	gb;
	JRadioButtonMenuItem	ggb;
	
	JTable		table;
	JTable		ftable;
	TableModel	ftablemodel;
	TableModel	defaultModel;
	TableModel	groupModel;
	
	//refmap, genelist, funclist, iclusterlist, uclusterlist, specset
	Map<String, Gene> genemap;
	List<Gene> genelist = new ArrayList<Gene>();
	List<Function> funclist = new ArrayList<Function>();
	List<Set<String>> iclusterlist;
	List<Set<String>> uclusterlist;
	Map<Set<String>, ShareNum> specset;
	
	Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap;
	BufferedImage bimg;
	
	JComboBox<String> selcomb;
	JComboBox<String> searchcolcomb;
	JComboBox<String> syncolorcomb;
	Map<String,Function> funcmap = new HashMap<String,Function>();
	
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
	
	private JComponent showGeneTable(/*final Map<String, Gene> genemap, final List<Gene> genelist, 
			final List<Function> funclist, final List<Set<String>> iclusterlist, final List<Set<String>> uclusterlist,
			final Map<Set<String>, ShareNum> specset,*/ final Map<Set<String>, ClusterInfo> clustInfoMap, final JButton jb,
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
								Gene gg = genelist.get(cr);
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
									List<Contig> scontigs = speccontigMap.get( tspec );
									
									double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
									if( ratio == -1 ) {
										ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
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
								List<Contig> scontigs = speccontigMap.get( tspec );
								
								double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
								if( ratio == -1 ) {
									ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
									label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
									label.setForeground( Color.white );
								} else {
									label.setBackground( GeneCompare.gradientColor( ratio ) );
									label.setForeground( Color.black );
								}
							}
						} else {
							List<Contig> contigs = speccontigMap.get( spec );
							if( value instanceof Teginfo ) {
								Teginfo ti = (Teginfo)value;
								label.setBackground( Color.green );
								for( Tegeval tv : ti.tset ) {
									double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
									if( ratio == -1 ) {
										ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
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
								
								double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
								if( ratio == -1 ) {
									ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
									label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
									label.setForeground( Color.white );
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
							phage = phage | tv.getGene().isPhage();
							plasmid = plasmid | tv.getContshort().isPlasmid();
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
						
						boolean phage = g.isPhage();
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
		InputStream is = GeneSet.class.getResourceAsStream("/kegg_pathways");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				current = new HashSet<String>();
				currentko = new HashSet<String>();
				pathwaymap.put(line.substring(1), current);
				pathwaykomap.put(line.substring(1), currentko);
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
				Set<String> specs = getSelspec(GeneSet.this, specList, null);
				updateShareNum(specs);
			}
		};
		AbstractAction	importgenesymbolaction = new AbstractAction("Import gene symbols") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						Path path = zipfile.toPath();
						String uristr = "jar:" + path.toUri();
						zipuri = URI.create( uristr /*.replace("file://", "file:")* );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						Path nf = zipfilesystem.getPath("/smap_short.txt");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						uni2symbol(new InputStreamReader(is), bw, unimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						zipfilesystem.close();
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
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						Path path = zipfile.toPath();
						String uristr = "jar:" + path.toUri();
						zipuri = URI.create( uristr /*.replace("file://", "file:")/ );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						Path nf = zipfilesystem.getPath("/idmapping_short.dat");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						if( unimap != null ) unimap.clear();
						unimap = idMapping(new InputStreamReader(is), bw, 2, 0, refmap, genmap, gimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						zipfilesystem.close();
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

									for (Gene g : genelist) {
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

		splitpane.setBottomComponent(botcomp);
		splitpane.setTopComponent(topcomp);

		groupModel = new TableModel() {
			@Override
			public int getRowCount() {
				return allgenegroups == null ? 0 : allgenegroups.size();
			}

			@Override
			public int getColumnCount() {
				return 29+specList.size();
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
					return "KO";
				} else if (columnIndex == 7) {
					return "KSymbol";
				} else if (columnIndex == 8) {
					return "Symbol";
				} else if (columnIndex == 9) {
					return "KO name";
				} else if (columnIndex == 10) {
					return "Pdbid";
				} else if (columnIndex == 11) {
					return "EC";
				} else if (columnIndex == 12) {
					return "Cog name";
				} else if (columnIndex == 13) {
					return "Cog";
				} else if (columnIndex == 14) {
					return "Cog annotation";
				} else if (columnIndex == 15) {
					return "Cog symbol";
				} else if (columnIndex == 16) {
					return "Cazy";
				} else if (columnIndex == 17) {
					return "Present in";
				} else if (columnIndex == 18) {
					return "Group index";
				} else if (columnIndex == 19) {
					return "Group coverage";
				} else if (columnIndex == 20) {
					return "Group size";
				} else if (columnIndex == 21) {
					return "Locprev";
				} else if (columnIndex == 22) {
					return "Avg GC%";
				} else if (columnIndex == 23) {
					return "# of locus";
				} else if (columnIndex == 24) {
					return "# of loc in group";
				} else if (columnIndex == 25) {
					return "max length";
				} else if (columnIndex == 26) {
					return "sharing number";
				} else if (columnIndex == 27) {
					return "# Cyc";
				} else if (columnIndex == 28) {
					return "16S Corr";
				} else {
					String spec = specList.get( columnIndex - 29 );
					if( spec.toLowerCase().contains("thermus") ) {
						int i = spec.indexOf('_');
						return spec.substring(i+1, spec.length());
					} else return spec;
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
				if( columnIndex == 18 || columnIndex == 19 || columnIndex == 27 )
					return Double.class;
				else if(columnIndex == 9 || (columnIndex >= 16 && columnIndex <= 27) )
					return Integer.class;
				else if (columnIndex >= 29)
					return Teg.class;
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
					return gg.getCommonKO();
				} else if (columnIndex == 7) {
					return gg.getCommonKSymbol();
				} else if (columnIndex == 8) {
					return gg.getCommonSymbol(); //ko2name != null ? ko2name.get( gg.getCommonKO() ) : null;
				} else if (columnIndex == 9) {
					String ret = ko2name != null ? ko2name.get( gg.getCommonKO() ) : null;
					if( ret == null ) {
						String symbol = gg.getCommonSymbol();
						if( symbol != null ) {
							if( symbol.length() <= 5 ) ret = symbol;
						}
					}
					return ret;
				} else if (columnIndex == 10) {
					return null;//gene.pdbid;
				} else if (columnIndex == 11) {
					return gg.getCommonEc();
				} else if (columnIndex == 12) {
					Cog cog = gg.getCommonCog( cogmap );
					if( cog != null ) {
						if( cog.name == null ) cog.name = cogidmap.get( cog.id );
						return cog.name;
					}
					return null;
				} else if (columnIndex == 13) {
					Cog cog = gg.getCommonCog( cogmap );
					return cog != null ? cog.id : null;
				} else if (columnIndex == 14) {
					Cog cog = gg.getCommonCog( cogmap );
					return cog != null ? cog.annotation : null;
				} else if (columnIndex == 15) {
					Cog cog = gg.getCommonCog( cogmap );
					return cog != null ? cog.genesymbol : null;
				} else if (columnIndex == 16) {
					return gg.getCommonCazy( cazymap );
				} else if (columnIndex == 17) {
					return gg.getSpecies().size();
				} else if (columnIndex == 18) {
					return gg.groupIndex;
				} else if (columnIndex == 19) {
					return gg.getGroupCoverage();
				} else if (columnIndex == 20) {
					return gg.getGroupGeneCount();
				} else if (columnIndex == 21) {
					return null;//gene.proximityGroupPreservation;
				} else if (columnIndex == 22) {
					return gg.getAvgGCPerc();
				} else if (columnIndex == 23) {
					return gg.genes.size();
				} else if (columnIndex == 24) {
					return gg.getGroupCount();
				} else if (columnIndex == 25) {
					return gg.getMaxLength();
				} else if (columnIndex == 26) {
					return specset.get( gg.getSpecies() );
				} else if (columnIndex == 27) {
					return gg.getMaxCyc();
				} else if (columnIndex == 28) {
					return gg.getGroupCoverage() == 39 && gg.getGroupCount() == 39 ? 0 : -1;
				} else {
					String spec = specList.get( columnIndex - 29 );
					Teginfo ret = getGroupTes( gg, spec );
					return ret;
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
				int gs = genelist.size();
				return gs;
			}

			@Override
			public int getColumnCount() {
				return 26+specList.size();
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
					return specList.get( columnIndex - 26 );
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
				Gene gene = genelist.get(rowIndex);
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
					return gg != null ? gg.getCommonKOName( ko2name ) : null;
				} else if (columnIndex == 10) {
					return gene.pdbid;
				} else if (columnIndex == 11) {
					return gene.ecid;
				} else if (columnIndex == 12) {
					Cog cog = gene.getGeneGroup() != null ? gene.getGeneGroup().getCommonCog( cogmap ) : null;
					if( cog != null ) return cog.id;
					return null;
				} else if (columnIndex == 13) {
					Cog cog = gene.getGeneGroup() != null ? gene.getGeneGroup().getCommonCog( cogmap ) : null;
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
						return specset.get( gg.getSpecies() );
					}
					return null;
				} else if (columnIndex == 24) {
					gene.getMaxCyc();
				} else if (columnIndex == 25) {
					return gene.getGroupCoverage() == 35 && gene.getGroupCount() == 35 ? gene.corr16s : -1;
				} else {
					String spec = specList.get( columnIndex-26 );
					//Teginfo set = gene.species.equals(spec) ? gene.teginfo : null;
					if( gene.getSpecies().equals( spec ) ) {
						return gene.tegeval;
					} else {
						return gene.getGeneGroup().species.get( spec );
					}
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
				for (String cstr : contigmap.keySet()) {
					Contig c = contigmap.get(cstr);
					//c.count = 0;
					c.loc = 0.0;
				}

				if( table.getModel() == defaultModel ) {
					for (Gene g : genelist) {
						Tegeval tv = g.tegeval;
							// int first = tv.cont.indexOf('_');
							// int sec = tv.cont.indexOf('_',first+1);
						Contig cont = tv.getContshort(); // tv.cont.substring(0,sec);
						if( cont != null && contigmap.containsKey(cont.getName()) ) {
							Contig c = contigmap.get(cont.getName());
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
					Function f = funclist.get(i);
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
		popup.add( new AbstractAction("Plasmid") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel( r );
				Gene g = genelist.get(i);
				Contig contig = g.tegeval.getContshort();
				String contigstr = contig.toString();
				contig.plasmid = !plasmids.contains( contigstr );
				if( contig.plasmid ) plasmids.add( contigstr );
				else plasmids.remove( contigstr );
				
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + zippath.toUri();
					zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					zipfilesystem = FileSystems.newFileSystem( zipuri, env );
					
					//fs = FileSystems.newFileSystem( uri, env );
					//FileSystem fs = FileSystems.newFileSystem(uri, env);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for( String contigname : plasmids ) {
				    	baos.write( (contigname + "\n").getBytes() );
					}
					
					Path nf = zipfilesystem.getPath("/plasmids.txt");
					long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
					//System.err.println( "eeerm " + bl );
					zipfilesystem.close();
				    
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
				JComboBox<String>	descombo = new JComboBox<String>( deset.toArray( new String[deset.size()] ) );
				descombo.setEditable( true );
				JOptionPane.showMessageDialog(GeneSet.this, descombo);
				String val = descombo.getSelectedItem().toString();
				deset.add( val );
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel( r );
					Gene g = genelist.get(i);
					g.designation = val;
					designations.put( g.id, val );
					//ta.append( g.tegeval.id + "\n" );
				}
				
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + zippath.toUri();
					zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					zipfilesystem = FileSystems.newFileSystem( zipuri, env );
					
					//fs = FileSystems.newFileSystem( uri, env );
					//FileSystem fs = FileSystems.newFileSystem(uri, env);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for( String geneid : designations.keySet() ) {
						String design = designations.get( geneid );
				    	baos.write( (geneid + "\t" + design + "\n").getBytes() );
					}
					
					Path nf = zipfilesystem.getPath("/designations.txt");
					long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING);
					//System.err.println( "eeerm " + bl );
					zipfilesystem.close();
				    
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
				for( Gene g : genelist ) {
					if( g.koid != null && g.koid.length() > 0 && !ko2name.containsKey( g.koid ) ) koids.add( g.koid );
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
				
				Serifier 	serifier = getConcatenatedSequences( false );
				String 		tree = serifier.getFastTree( serifier.lseq );
				
				TreeUtil 	tu = new TreeUtil();
				Node 		n = tu.parseTreeRecursive( tree, false );
				
				TableModel model = new TableModel() {
					@Override
					public int getRowCount() {
						return GeneSet.this.getSpecies().size();
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
						return GeneSet.this.getSpecies().get( rowIndex );
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
		popup.add( new AbstractAction("Concatenate tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Serifier serifier = getConcatenatedSequences( false );
				
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
						GeneGroup gg = allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = genelist.get(cr);
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

					String tree = serifier.getFastTree( serifier.lseq );
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
				return funclist.size();
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
				Function func = funclist.get(rowIndex);
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
				if (pathwaymap.containsKey(sel)) {
					Set<String> enz = pathwaymap.get(sel);
					for (Function f : funclist) {
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
				for (Gene g : genelist) {
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
				for (Function f : funclist) {
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
					Function f = funclist.get(fr);
					if( table.getModel() == groupModel ) {
						Set<GeneGroup> sset = f.getGeneGroups();
						for (GeneGroup gg : sset) {
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
							GeneGroup gg = allgenegroups.get(cr);
							for( Function f : gg.getFunctions() ) {
								try {
									int rf = ftable.convertRowIndexToView(f.index);
									if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
								} catch( Exception ex ) {
									ex.printStackTrace();
								}
							}
						} else {
							Gene g = genelist.get(cr);
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
					scrollToSelection( table );
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
							GeneGroup gg = allgenegroups.get(cr);
							for( Function f : gg.getFunctions() ) {
								filterset.add(f.index);
							}
						}
					} else {
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

						Function f = funclist.get(cr);
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
						Function f = funclist.get(cr);
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
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, searchi, e.isAltDown(), 7, 8, 9 ) : searchTable( table, text, searchi, e.isAltDown(), 0 );
				}
			}
		});

		textfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(0, text, table, genefilter, genefilterset, label, 7, 8, 9 );
					else updateFilter(0, text, table, genefilter, genefilterset, label, 0 );
				} else {
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 7, 8, 9 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void insertUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(1, text, table, genefilter, genefilterset, label, 7, 8, 9);
					else updateFilter(1, text, table, genefilter, genefilterset, label, 0);
				} else {
					searchi = searchcolcomb.getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 7, 8, 9 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void removeUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectedItem().equals("Symbol") ) updateFilter(2, text, table, genefilter, genefilterset, label, 7, 8, 9 );
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
		popup.add(new AbstractAction("Show genes with same sharing") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int r = table.getSelectedRow();
				if (r >= 0) {
					int cr = table.convertRowIndexToModel(r);
					GeneGroup gg = allgenegroups.get(cr);
					for (GeneGroup g : allgenegroups) {
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
				updateFilter(ftable, rowfilter, null);
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
		popup.add(new AbstractAction("Select genes in proximity") {
			@Override
			public void actionPerformed(ActionEvent e) {
				genefilterset.clear();
				int[] rr = table.getSelectedRows();
				proxi(table, rr, genelist, genefilterset, false);
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
					Tegeval tv = gg.tegeval;
					for (Set<String> uset : iclusterlist) {
						if (uset.contains(tv.name)) {
							ct.addAll(uset);
							break;
						}
					}
				}

				for (Gene g : genelist) {
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
				Gene gg = genelist.get(cr);
				if (gg.getSpecies() != null) {
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
	
	public void repaintGCSkew( List<Contig> selclist, Graphics2D g2, int size, GeneGroup gg, String selspec ) {
		g2.setColor( Color.white );
		g2.fillRect(0, 0, 1024, 1024);
		g2.setFont( g2.getFont().deriveFont(10.0f) );
		int total = 0;
		int g = 0;
		int c = 0;
		double gcstotal = 0.0;
		for( Contig ctg : selclist ) {
			//Contig ctg = clist.get( u );
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
				for( GeneGroup gg : allgenegroups ) {
					for( Gene g : gg.genes ) {
						if( g.refid.toLowerCase().contains(ustr) ) {
							filterset.add(i);
							break;
						}
					}
					i++;
				}
			} else {
				for( Gene g : genelist ) {
					if( g.refid.toLowerCase().contains(ustr) ) {
						filterset.add(i);
					}
					i++;
				}
			}
		}
		
		updateFilter(table, filter, label);
	}

	boolean ftableisselecting = false;
	boolean tableisselecting = false;

	private void readGoInfo(Reader rd, Map<Function, Set<Gene>> gofilter, String outfile) throws IOException {
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
					//if (f.getGeneentries() == null) f.getGeneentries() = new HashSet<Gene>();
					f.addGeneentries( gofilter.get(f) );
					//f.addGroupentries(ge)
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
				} else if (line.contains("KO:")) {
					f.ko = line.substring(line.indexOf("KO:") + 3);
				}
			} else if (line.startsWith("is_a:")) {
				if (line.contains("GO:")) {
					String parid = line.substring(6, 6+10);
					if( f.isa == null ) {
						f.isa = new HashSet<String>();
					}
					f.isa.add( parid );
					/*if( !funcmap.containsKey( go ) ) {
						f = new Function( go );
						funcmap.put( go, f );
					} else f = funcmap.get( go );*/
				}
			}

			if (on) fw.write(line + "\n");

			line = br.readLine();
		}
		if (f != null && gofilter.containsKey(f)) {
			f.addGeneentries(gofilter.get(f));
			//retmap.put(f.go, f);
		}
		//br.close();

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
	
	public int loadrnas( Map<String,GeneGroup>	ggmap, Reader reader, int groupIndex, String tag ) throws IOException {
		//List<Gene> genelist = new ArrayList<Gene>();
		
		BufferedReader 			br = new BufferedReader( reader );
		String 					line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith(">") ) {				
				int i = trim.indexOf(' ');
				int k = trim.indexOf('[');
				
				String loc = trim.substring(1,i);
				String name = trim.substring(i+1, k-1);
				String cont = trim.substring(k+1, trim.length()-1);
				String spec;
				String contshort;
				int u = Contig.specCheck( cont );
				
				if( u == -1 ) {
					u = contigIndex(cont);
					spec = cont.substring( 0, u-1 );
					contshort = cont.substring( u, cont.length() );
				} else {
					int l = cont.indexOf('_', u+1);
					spec = cont.substring( 0, l );
					contshort = cont.substring( l+1, cont.length() );
				}
				
				String[] split;
				boolean rev = false;
				if( loc.startsWith("comp") ) {
					rev = true;
					int s = loc.indexOf('(');
					int e = loc.indexOf(')');
					split = loc.substring(s+1, e).split("\\.\\.");
				} else split = loc.split("\\.\\.");
				
				int start = Integer.parseInt( split[0] );
				int stop = Integer.parseInt( split[1] );
				
				String idstr = null;
				int ids = name.lastIndexOf('(');
				if( ids != -1 ) {
					int eds = name.indexOf(')', ids+1);
					if( eds != -1 ) {
						idstr = name.substring(ids+1,eds);
						name = name.substring(0, ids);
					}
				}
				
				if( tag.contains("rrna") ) {
					String namel = name.toLowerCase();
					if( namel.contains("23s") || namel.contains("lsu") ) name = "23S rRNA";
					else if( namel.contains("16s") || namel.contains("ssu") ) name = "16S rRNA";
					else if( namel.contains("5s") || namel.contains("tsu") ) name = "5S rRNA";
					//name = namel;
				}
				
				GeneGroup 	gg;
				if( ggmap.containsKey( name ) ) {
					gg = ggmap.get( name );
				} else {
					gg = new GeneGroup( groupIndex++ );
					ggmap.put( name, gg );
				}
				Gene g = new Gene( gg, name, name, spec );
				g.setIdStr( idstr );
				
				Contig contig = contigmap.get( cont );
				Tegeval tegeval = new Tegeval( g, spec, 0.0, trim.substring(1,trim.length()-1), contig, contshort, start, stop, rev ? -1 : 1 );
				tegeval.type = tag;
				g.setTegeval( tegeval );
				gg.addGene( g );
			}
			line = br.readLine();
		}
		
		return groupIndex;
	}
	
	public int loadRrnas( Map<String,GeneGroup>	ggmap, Reader reader, int groupIndex ) throws IOException {
		//List<Gene> genelist = new ArrayList<Gene>();
		
		BufferedReader 			br = new BufferedReader( reader );
		String 					line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith(">") ) {
				int i = trim.indexOf("DIR");
				int b = trim.lastIndexOf('_', i-2);
				
				String loc = trim.substring(b+1, i-1);
				String cont = trim.substring(6, b).replace(".fna", "");
				int end = cont.indexOf("_contig");
				if( end == -1 ) end = cont.indexOf("_scaffold");
				if( end == -1 ) end = cont.length();
				String spec = cont.substring(0,end);
				
				int bil = trim.indexOf(' ', i+15);
				String name = trim.substring(i+15, bil);
				
				String namel = name.toLowerCase();
				if( namel.contains("23s") || namel.contains("lsu") ) name = "23S rRNA";
				else if( namel.contains("16s") || namel.contains("ssu") ) name = "16S rRNA";
				else if( namel.contains("5s") || namel.contains("tsu") ) name = "5S rRNA";
				//name = namel;
				
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
				if( ggmap.containsKey( name ) ) {
					gg = ggmap.get( name );
				} else {
					gg = new GeneGroup( groupIndex++ );
					ggmap.put( name, gg );
				}
				Gene g = new Gene( gg, cont+"_"+loc, name, spec );
				
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
				
				Tegeval tegeval = new Tegeval( g, spec, 0.0, trim.substring(6, i+4), contig, loc, start, stop, rev ? -1 : 1 );
				tegeval.type = "rrna";
				g.setTegeval( tegeval );
				gg.addGene( g );
			}
			line = br.readLine();
		}
		
		return groupIndex;
		//br.close();
	}
		
	public int loadTrnas( Map<String,GeneGroup>	ggmap, Reader reader, int groupIndex ) throws IOException {
		BufferedReader br = new BufferedReader( reader );
		String line = br.readLine();
		while( line != null ) {
			String[] split = line.split("[\t ]+");
			String cont = split[0].replace(".fna", "");
			String name = "tRNA-"+split[4];
			int end = cont.indexOf("_contig");
			if( end == -1 ) end = cont.indexOf("_scaffold");
			if( end == -1 ) {
				System.err.println();
			}
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
			if( ggmap.containsKey( name ) ) {
				gg = ggmap.get( name );
			} else {
				gg = new GeneGroup( groupIndex++ );
				ggmap.put( name, gg );
			}
			Gene g = new Gene( gg, cont+"_"+start+"_"+stop, name, spec );
			
			Contig contig = contigmap.get( cont );
			Tegeval tegeval = new Tegeval( g, spec, 0.0, null, contig, null, start, stop, ori );
			tegeval.type = "trna";
			g.setTegeval( tegeval );
			gg.addGene( g );
			
			line = br.readLine();
		}
		
		return groupIndex;
	}
	
	public void proxPreserve( Map<String, Gene> locgene ) {
		int count = 0;
		System.err.println( "blehehe " + genelist.size() );
		for (Gene gg : genelist) {
			Set<String> ct = new HashSet<String>();
			Tegeval tv = gg.tegeval;
			if( tv.name != null ) {
				ct.add(tv.name);
				int idx = tv.name.lastIndexOf("_");
				int val = Integer.parseInt(tv.name.substring(idx + 1));

				String next = tv.name.substring(0, idx + 1) + (val + 1);
				ct.add(next);
				if (val > 1) {
					String prev = tv.name.substring(0, idx + 1) + (val - 1);
					ct.add(prev);
				}
			}

			Set<Integer> groupIdxSet = new HashSet<Integer>();
			for (String cont : ct) {
				Gene g = locgene.get(cont);
				if (g != null ) {
					Tegeval tv2 = g.tegeval;
					if (ct.contains(tv2.name)) {
						groupIdxSet.add( g.getGroupIndex() );
						// if( remove ) genefilterset.remove(
						// g.index );
						// else genefilterset.add( g.index );
						// break;
					}
				}
			}
			gg.proximityGroupPreservation = Math.ceil(groupIdxSet.size() / 2.0);
		}
		locgene.clear();
	}
	
	Map<String, Set<String>> 				ko2go = new TreeMap<String, Set<String>>();
	
	JComboBox<String> 						specombo;
	JComboBox<String> 						combo;
	Map<String, Set<String>> 				pathwaymap = new TreeMap<String, Set<String>>();
	Map<String, Set<String>> 				pathwaykomap = new TreeMap<String, Set<String>>();
	List<String> 							corrInd;
	List<GeneGroup>							allgenegroups;
	Map<Set<String>,List<GeneGroup>> 		ggSpecMap;
	Map<String,Set<GeneGroup>>				specGroupMap;
	List<String>							specList = new ArrayList<String>();
	//byte[] 									zipf;
	Map<String,Cog>							cogmap = new HashMap<String,Cog>();
	Map<String,String>						cogidmap = new HashMap<String,String>();
	Map<String,String>						unresolvedmap = new HashMap<String,String>();
	Map<String,String>						namemap = new HashMap<String,String>();
	Map<String,String>						cazymap = new HashMap<String,String>();
	
	/*private Map<String,String> loadCog() {
		Map<String,String>	cogmap = new HashMap<String,String>();
		FileReader fr = new FileReader("/vg454flx/cogthermus.blastout");
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		String id = null;
		String current;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				current = line.substring(7);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					current += line;
					line = br.readLine();
				}
				current = current.trim();
				
				String lname = current;
				int i = lname.lastIndexOf('[');
				if( i == -1 ) {
					id = lname;
				} else {		
					int n = lname.indexOf(']', i+1);
					int u = lname.indexOf(' ');
					id = lname.substring(0, u);
				}

			} else if( line.startsWith(">") ) {
				String val = line.substring(1);
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					val += line;
					line = br.readLine();
				}
				val = val.trim();
				
				int i = current.lastIndexOf('[');
				int n = current.indexOf(']', i+1);
				
				if( i == -1 || n == -1 ) {
					n = current.indexOf(" ");
				}
				
				/*if( i == -1 || n == -1 ) {
					System.err.println( val );
				}*
				
				String spec = current.substring(i+1, n);
				
				int k = spec.indexOf("_contig");
				if( k == -1 ) {
					k = spec.indexOf("_uid");
					k = spec.indexOf('_', k+4);
				}
				
				if( k == -1 ) {
					k = spec.indexOf('_');
					k = spec.indexOf('_', k+1);
				}
				if( k != -1 ) spec = spec.substring(0, k);
				if( !spec.contains("_") ) {
					System.err.println();
				}
				
				i = val.indexOf('[');
				n = val.indexOf(']', i+1);
				/*if( i == -1 || n == -1 ) {
					System.err.println( val );
				}*
				String cog = val.substring(i+1, n);
				int u = cog.indexOf('/');
				if( u != -1 ) cog = cog.substring(0, u);
				String erm = cog.replace("  ", " ");
				while( !erm.equals( cog ) ) {
					cog = erm;
					erm = cog.replace("  ", " ");
				}
				cog = cog.trim();
				
				Map<String,Integer> cogmap;
				if( map.containsKey( spec ) ) {
					cogmap = map.get(spec);
				} else {
					cogmap = new HashMap<String,Integer>();
					map.put( spec, cogmap );
				}
				
				if( cogmap.containsKey( cog ) ) {
					cogmap.put( cog, cogmap.get(cog)+1 );
				} else cogmap.put( cog, 1 );
				
				all.add( cog );
			}
			line = br.readLine();
		}
		fr.close();
		
		return cogmap;
	}*/
	
	public void cleanUp() {
		refmap.clear();
		genmap.clear();
		unimap.clear();
		gimap.clear();
		
		allgenes.clear();
		geneset.clear();
		geneloc.clear();
		poddur.clear();
		locgene.clear();
		
		if( ko2name != null ) ko2name.clear();
		if( designations != null ) designations.clear();
		if( deset != null ) deset.clear();
		
		if( allgenegroups != null ) allgenegroups.clear();
		if( genelist != null ) genelist.clear();
		
		if( cogidmap != null ) cogidmap.clear();
		if( cogmap != null ) cogmap.clear();
		
		if( ko2go != null ) ko2go.clear();
		if( ko2name != null ) ko2name.clear();
		
		if( ggSpecMap != null ) ggSpecMap.clear();
		if( specGroupMap != null ) specGroupMap.clear();
		if( specList != null ) specList.clear();
		
		if( unresolvedmap != null ) unresolvedmap.clear();
		if( cazymap != null ) cazymap.clear();
		if( namemap != null ) namemap.clear();
		
		if( contigmap!= null ) contigmap.clear();
	}
	
	Map<String, Gene> 			refmap = new HashMap<String, Gene>();
	Map<String, Gene> 			genmap = new HashMap<String, Gene>();
	Map<String, Gene> 			unimap = new HashMap<String, Gene>();
	Map<String, Gene> 			gimap = new HashMap<String, Gene>();
	
	Map<String, String> 		allgenes = new HashMap<String, String>();
	Map<String, Set<String>> 	geneset = new HashMap<String, Set<String>>();
	Map<String, Set<String>> 	geneloc = new HashMap<String, Set<String>>();
	Set<String> 				poddur = new HashSet<String>();
	Map<String, Gene> 			locgene = new HashMap<String, Gene>();
	
	Path		zippath;
	//File		zipfile;
	FileSystem	zipfilesystem;
	URI			zipuri;
	Map<String,String>	ko2name;
	Map<String,String>	designations;
	Set<String>			deset = new HashSet<String>();
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
		
		if( fail && zippath == null ) {
			JFileChooser fc = new JFileChooser();
			if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
				zippath = fc.getSelectedFile().toPath();				
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
			
		if( zippath != null ) {
			Map<String,String> env = new HashMap<String,String>();
			//Path path = zipfile.toPath();
			String uristr = "jar:" + zippath.toUri();
			zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );
			
			Path nf = zipfilesystem.getPath("/unresolved.blastout");
			if( Files.exists( nf ) ) unresolvedmap = loadunresolvedmap( new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
			nf = zipfilesystem.getPath("/namemap.txt");
			if( Files.exists( nf ) ) namemap = loadnamemap( Files.newBufferedReader(nf) );
			nf = zipfilesystem.getPath("/designations.txt");
			if( Files.exists( nf ) ) designations = loadDesignations( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), deset );
			nf = zipfilesystem.getPath("/plasmids.txt");
			if( Files.exists( nf ) ) plasmids = loadPlasmids( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );
			
			/*zipfilesystem.close();			
			path = zipfile.toPath();
			String uristr = "jar:" + path.toUri();
			zipuri = URI.create( uristr /*.replace("file://", "file:")* );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );*/
			
			nf = zipfilesystem.getPath("/allthermus.fna");
			if( Files.exists( nf ) ) specList = loadcontigs( Files.newBufferedReader(nf), "" );
			//else {
			for( Path root : zipfilesystem.getRootDirectories() ) {
				Files.list(root).filter( new Predicate<Path>() {
					@Override
					public boolean test(Path t) {
						String filename = t.getFileName().toString();
						return filename.endsWith(".fna") && !filename.equals("allthermus.fna");
					}
				}).forEach( new Consumer<Path>() {
					@Override
					public void accept(Path t) {
						try {
							String filename = t.getFileName().toString().replace(".fna", "");
							specList = loadcontigs( Files.newBufferedReader(t), "" );
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			//}
			
			nf = zipfilesystem.getPath("/aligned");
			if( Files.exists(nf) ) {
				Files.list(nf).filter( new Predicate<Path>() {
					@Override
					public boolean test(Path t) {
						String filename = t.getFileName().toString();
						System.err.println("filename " + filename);
						boolean b = (filename.endsWith(".aa") || filename.endsWith(".fsa")) && !filename.contains("allthermus");
						return b;
					}
				}).forEach( new Consumer<Path>() {
					@Override
					public void accept(Path t) {
						if( Files.exists( t ) )
							try {
								String filename = t.getFileName().toString().replace(".fna", "");
								loci2aasequence( Files.newBufferedReader(t), refmap, designations, filename );
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
				});
			} else {
				nf = zipfilesystem.getPath("/allthermus_aligned.aa");
				if( Files.exists( nf ) ) loci2aasequence( Files.newBufferedReader(nf), refmap, designations, "" );
				//else {
				
				/*for( String id : refmap.keySet() ) {
					Gene g = refmap.get( id );
					if( g.getSpecies().contains("MAT4685") ) {
						System.err.println();
					}
				}*/
				
				for( Path root : zipfilesystem.getRootDirectories() ) {
					Files.list(root).filter( new Predicate<Path>() {
						@Override
						public boolean test(Path t) {
							String filename = t.getFileName().toString();
							System.err.println("filename " + filename);
							boolean b = (filename.endsWith(".aa") || filename.endsWith(".fsa")) && !filename.contains("allthermus");
							return b;
						}
					}).forEach( new Consumer<Path>() {
						@Override
						public void accept(Path t) {
							if( Files.exists( t ) )
								try {
									String filename = t.getFileName().toString().replace(".fna", "");
									loci2aasequence( Files.newBufferedReader(t), refmap, designations, filename );
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					});
				}
			}
			//}
			
			/*int tot = 0;
			int mot = 0;
			for( String cstr : contigmap.keySet() ) {
				Contig ctg = contigmap.get( cstr );
				if( ctg.tlist != null ) for( Tegeval tv : ctg.tlist ) {
					Gene gn = tv.getGene();
					
					if( gn.getGeneGroup() != null ) {
						tot++;
					} else mot++;
				}
			}
			System.err.println( tot + "  " + mot );
			tot = 0;
			mot = 0;
			for( Gene gn : genelist ) {	
				if( gn.getGeneGroup() != null ) {
					tot++;
				} else mot++;
			}
			System.err.println( tot + "  " + mot );
			System.err.println();*/
			
			nf = zipfilesystem.getPath("/clusters.txt");
			if( Files.exists( nf ) ) {
				uclusterlist = loadSimpleClusters( Files.newBufferedReader(nf) );
				//if( refmap.size() == 0 ) {
				loci2aaseq( uclusterlist, refmap, designations );
				//}
			}
			nf = zipfilesystem.getPath("/cog.blastout");
			if( Files.exists( nf ) ) cogmap = loadcogmap( Files.newBufferedReader(nf) );
			nf = zipfilesystem.getPath("/cazy");
			if( Files.exists( nf ) ) loadcazymap( cazymap, Files.newBufferedReader(nf) );
				
			/*int zcount = 0;
			while( zcount < 3 ) {
				zipm = new ZipInputStream( new ByteArrayInputStream( zipf ) );
				ze = zipm.getNextEntry();
				while( ze != null ) {
					String zname = ze.getName();
					if( zcount == 0 && (zname.equals("allthermus.fna") || zname.equals("allglobus.fna") || zname.equals("allrhodo.fna")) ) {
						specList = loadcontigs( new InputStreamReader( zipm ) );
						zcount = 1;
					} else if( zcount == 1 && (zname.equals("allthermus_aligned.fsa") || zname.equals("allthermus_aligned.aa") || zname.equals("allglobus_aligned.aa") || zname.equals("allrhodo_aligned.aa")) ) {
						loci2aasequence( new InputStreamReader( zipm ), refmap, designations );
						zcount = 2;
					} else if( zcount == 2 && zname.equals("clusters.txt") ) {
						uclusterlist = loadSimpleClusters( new InputStreamReader( zipm ) );
						zcount = 3;
					} else if( zname.equals("cog.blastout") ) {
						cogmap = loadcogmap( new InputStreamReader( zipm ) );
					} else if( zname.endsWith(".cazy") ) {
						loadcazymap( cazymap, new InputStreamReader( zipm ) );
					}
					
				/*int size = (int)ze.getSize();
				byte[] bb = new byte[ size ];
				//ByteArrayOutputStream baos = new ByteArrayOutputStream( size );
				int total = 0;
				r = zipm.read( bb );
				total += r;
				while( total < size ) {
					r = zipm.read(bb, total, size-total);
					total += r;
				}
				/*while( r > 0 ) {
					baos.write( bb, 0, r );
					r = zipm.read( bb );
				}*
				//baos.close();
				//mop.put( ze.getName(), bb );
					ze = zipm.getNextEntry();
				}
			}*/
			genemap = refmap;
			
			//syncolorcomb = new JComboBox();
			syncolorcomb.removeAllItems();
			syncolorcomb.addItem("");
			for( String spec : speccontigMap.keySet() ) {
				syncolorcomb.addItem( spec );
			}
			syncolorcomb.addItem("All");
			//loadCog();
			
			//specList = loadcontigs( new InputStreamReader( new ByteArrayInputStream( mop.remove("allthermus.fna") ) ) );			
			//loci2aasequence( new InputStreamReader( new ByteArrayInputStream( mop.remove("allthermus.aa") ) ), refmap );
			//List<Set<String>> uclusterlist = loadSimpleClusters( new InputStreamReader( new ByteArrayInputStream( mop.remove("clusters.txt") ) ) );
			/*try {
				is = GeneSet.class.getResourceAsStream("/allthermus.fna");
				//InputStream cois = GeneSet.class.getResourceAsStream("/contigorder.txt");
				//is = GeneSet.class.getResourceAsStream("/all.fsa");
				// is = GeneSet.class.getResourceAsStream("/arciformis.nn");
				if (is != null)
					loadcontigs(new InputStreamReader(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//InputStream is = GeneSet.class.getResourceAsStream("/all.aa");
			is = GeneSet.class.getResourceAsStream("/allthermus.aa");
			// InputStream is = GeneSet.class.getResourceAsStream("/arciformis.aa");
			if (is != null)
				loci2aasequence(new InputStreamReader(is));*/
	
			// URL url = new URL("http://192.168.1.69/all.nn");
			/*try {
				is = GeneSet.class.getResourceAsStream("/allthermus.nn");
				//is = GeneSet.class.getResourceAsStream("/all.nn");
				// is = GeneSet.class.getResourceAsStream("/arciformis.nn");
				if (is != null)
					loci2dnasequence(new InputStreamReader(is));
			} catch (Exception e) {
				e.printStackTrace();
			}*/
	
			// url = new URL("http://192.168.1.69/all.fsa");
	
			//is = GeneSet.class.getResourceAsStream("/intersect_cluster.txt");
			//List<Set<String>> iclusterlist = null; //loadSimpleClusters(new InputStreamReader(is));
	
			//is = GeneSet.class.getResourceAsStream("/thermus_unioncluster.txt");
			//is = GeneSet.class.getResourceAsStream("/allthermus_unioncluster2.txt");
			//is = GeneSet.class.getResourceAsStream("/thomas1.clust");
			
			//is = GeneSet.class.getResourceAsStream("/allthermus_new.clust");
			//List<Set<String>> uclusterlist = loadSimpleClusters( new InputStreamReader(is) );
			
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
	
			//is = GeneSet.class.getResourceAsStream("/thermus_nr.blastout");
			//panCoreFromNRBlast( new InputStreamReader(is), "c:/sandbox/distann/src/thermus_nr_short.blastout", refmap, allgenes, geneset, geneloc, locgene, poddur, uclusterlist ); 
			//is = GeneSet.class.getResourceAsStream("/thermus_nr_short.blastout");
			//is = new FileInputStream( "/home/sigmar/thermus_nr_short.blastout" );
			//is = new FileInputStream( "/home/sigmar/thermus_nr_ftp_short.blastout" );
			
			//is = GeneSet.class.getResourceAsStream("/thermus_nr_ftp_short.blastout");
			//is = GeneSet.class.getResourceAsStream("/ncbithermus_short.blastout");		
			//InputStream nis = GeneSet.class.getResourceAsStream("/exp.blastout");
			
			/*is = GeneSet.class.getResourceAsStream("/thermus_ncbi_short.blastout");
			InputStream nis = null;//GeneSet.class.getResourceAsStream("/exp_short.blastout");
			Blast blast = new Blast();
			blast.panCoreFromNRBlast(new InputStreamReader(is), null/*new InputStreamReader(nis)*, null/*"/u0/sandbox/distann/src/thermus_ncbi_short.blastout"*, null /*"/u0/sandbox/distann/src/exp_short.blastout"*, refmap, allgenes, geneset, geneloc, locgene, poddur, uclusterlist, aas, contigmap);*/
	
			geneloc.clear();
			allgenes.clear();
			geneset.clear();
			poddur.clear();
			
			/*FileReader fr = new FileReader("/vg454flx/cogthermus.blastout");
			cogmap = loadcogmap( fr );
			fr.close();*/
			
			// Map<String,Gene> refmap = new TreeMap<String,Gene>();
			for (String genedesc : refmap.keySet()) {
				Gene gene = refmap.get(genedesc);
				if( namemap.containsKey(genedesc) ) {
					gene.koname = namemap.get( genedesc );
				}
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
			double[] corr16sArray = is == null ? new double[0] : load16SCorrelation(new InputStreamReader(is), corrInd);
	
			if( uclusterlist != null ) Collections.sort(uclusterlist, new Comparator<Set<String>>() {
				@Override
				public int compare(Set<String> o1, Set<String> o2) {
					return o1.size() - o2.size();
				}
			});
	
			Map<Set<String>, double[]> corrList = new HashMap<Set<String>, double[]>();
	
			List<GeneGroup>	ggList = new ArrayList<GeneGroup>();
			int i = 0;
			//Set<String> ss = new HashSet<String>();
			Set<String> gs = new HashSet<String>();	
			
			int countclust = 0;
			if( uclusterlist != null ) for (Set<String> cluster : uclusterlist) {
				//ss.clear();
				gs.clear();
				
				/*if( cluster.size() == 1 ) {
					String s = "";
					for( String u : cluster ) s = u;
				}*/
	
				Set<Gene> gset = new HashSet<Gene>();
				for( String cont : cluster ) {
					String gid = null;
					//String spec;
					int b = cont.lastIndexOf('[');
					if( b != -1 ) {
						int u = cont.indexOf(']', b+1);
						int k = cont.indexOf(' ');
						
						/*int n = cont.lastIndexOf('#');
						if( n != -1 ) {
							int m = cont.lastIndexOf(';', n+1);
							if( m != -1 ) {
								gid = cont.substring(m+1);
							}
						}*/
						
						if( gid == null ) {
							gid = cont.substring(0, k);
							if( gid.contains("..") ) {
								gid = cont.substring(b+1, u) + "_" + gid;
							}
						}
						
						String scont = cont.substring(b+1, u);
						
						int l = Contig.specCheck( scont );
						
						if( l == -1 ) {
							l = scont.indexOf("contig");
							//spec = scont.substring(0, l-1);
						} else {
							int m = scont.indexOf('_', l+1);
							if( m == -1 ) {
								m = scont.length(); //"monster";
							}
							//spec = scont.substring(0, m);
						}
					} else {
						int k = cont.indexOf('#');
						if( k == -1 ) k = cont.length();
						gid = cont.substring(0, k).trim();
						
						//int k = cont.indexOf("contig");
						//spec = cont.substring(0, k-1);
					}
					//String[] split = cont.split("_");
					
					//ss.add( spec );
					Gene g = refmap.get(gid);
					
					if (g != null) {
						gs.add(g.refid);
						gset.add(g);
						countclust++;
					} else {
						if( mu.contains( gid ) ) {
							System.err.println("e " + gid);
						} else {
							System.err.println("r " + gid);
						}
					}
				}
	
				int val = gset.size();	
				/*int len = 20; //16
				if (val == len && ss.size() == len) {
					corrList.put(cluster, new double[20*20]);
				}*/
	
				GeneGroup gg = new GeneGroup( i );
				ggList.add( gg );
				//gg.addSpecies( ss );
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
			}// else {
			
			for( Gene g : genelist ) {
				if( g.getGeneGroup() == null ) {
					GeneGroup gg = new GeneGroup( i++ );
					ggList.add( gg );
					gg.setGroupCount( 1 );
					
					g.setGeneGroup( gg );
				}
			}
			//}
			System.err.println( countclust + "  " + genelist.size() );
			int me = 0;
			int mu = 0;
			for( Gene g : genelist ) {
				if( g.getGeneGroup() != null ) me++;
				else mu++;
			}
			System.err.println( me + "  " + mu );
			
			final Map<String,GeneGroup>	rnamap = new HashMap<String,GeneGroup>();
			//is = GeneSet.class.getResourceAsStream("/rrna.fasta");
			//InputStream tis = //GeneSet.class.getResourceAsStream("/trna.txt"); //GeneSet.class.getResourceAsStream("/trna_sub.txt");
			/*ZipInputStream zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("trnas.txt") ) {
					i  = loadTrnas( rnamap, new InputStreamReader( zipin ), i );
				} else if( ze.getName().equals("rrnas.fasta") ) {
					i = loadRrnas( rnamap, new InputStreamReader( zipin ), i );
				} else if( ze.getName().equals("allthermus.trna") || ze.getName().equals("allglobus.trna") ) {
					i = loadrnas( rnamap, new InputStreamReader( zipin ), i, "trna" );
				} else if( ze.getName().equals("allthermus.rrna") || ze.getName().equals("allglobus.rrna") ) {
					i = loadrnas( rnamap, new InputStreamReader( zipin ), i, "rrna" );
				}
				
				ze = zipin.getNextEntry();
			}
			zipin.close();*/
			
			nf = zipfilesystem.getPath("/trnas.txt");
			//loadcazymap( cazymap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
			if( Files.exists( nf ) ) i  = loadTrnas( rnamap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), i );
			nf = zipfilesystem.getPath("/rrnas.txt");
			if( Files.exists( nf ) ) i = loadRrnas( rnamap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), i );
			
			nf = zipfilesystem.getPath("/allthermus.trna");
			if( Files.exists( nf ) ) i = loadrnas( rnamap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), i, "trna" );
			else {
				for( Path root : zipfilesystem.getRootDirectories() ) {
					Files.list(root).filter( new Predicate<Path>() {
						@Override
						public boolean test(Path t) {
							String filename = t.getFileName().toString();
							//System.err.println("filename " + filename);
							boolean b = filename.endsWith(".trna") && !filename.contains("allthermus");
							return b;
						}
					}).forEach( new Consumer<Path>() {
						@Override
						public void accept(Path t) {
							if( Files.exists( t ) )
								try {
									loadrnas( rnamap, new InputStreamReader( Files.newInputStream(t, StandardOpenOption.READ) ), 0, "trna" );
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					});
				}
			}
			
			nf = zipfilesystem.getPath("/allthermus.rrna");
			if( Files.exists( nf ) ) i = loadrnas( rnamap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), i, "rrna" );
			else {
				for( Path root : zipfilesystem.getRootDirectories() ) {
					Files.list(root).filter( new Predicate<Path>() {
						@Override
						public boolean test(Path t) {
							String filename = t.getFileName().toString();
							//System.err.println("filename " + filename);
							boolean b = filename.endsWith(".rrna") && !filename.contains("allthermus");
							return b;
						}
					}).forEach( new Consumer<Path>() {
						@Override
						public void accept(Path t) {
							if( Files.exists( t ) )
								try {
									loadrnas( rnamap, new InputStreamReader( Files.newInputStream(t, StandardOpenOption.READ) ), 0, "rrna" );
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					});
				}
			}
			//zipfilesystem.close();
			
			//loadRrnas( new InputStreamReader(is), new InputStreamReader(tis), i );
			for( String ggname : rnamap.keySet() ) {
				GeneGroup gg = rnamap.get( ggname );
				ggList.add( gg );
				
				for( Gene g : gg.genes ) {
					refmap.put( g.refid, g );
					genelist.add( g );
					
					//gg.addSpecies( g.species );
				}
			}
			
			ggSpecMap = new HashMap<Set<String>,List<GeneGroup>>();
			for( GeneGroup gg : ggList ) {
				List<GeneGroup>	speclist;
				Set<String> specset = gg.species.keySet();
				if( ggSpecMap.containsKey( specset ) ) {
					speclist = ggSpecMap.get(specset);
				} else {
					speclist = new ArrayList<GeneGroup>();
					ggSpecMap.put( specset, speclist );
				}
				speclist.add( gg );
			}
			
			specGroupMap = new HashMap<String,Set<GeneGroup>>();
			int ind = 0;
			for( GeneGroup gg : ggList ) {
				for( String spec : gg.species.keySet() ) {
					Set<GeneGroup>	ggset;
					if( !specGroupMap.containsKey( spec ) ) {
						ggset = new HashSet<GeneGroup>();
						specGroupMap.put( spec, ggset );
					} else ggset = specGroupMap.get( spec );
					ggset.add( gg );
				}
				if( ind == 4049 ) {
					System.err.println( gg.species.keySet() );
					System.err.println();
				}
				gg.setIndex( ind++ );
			}
			allgenegroups = ggList;
			
			for( Gene g : genelist ) {
				if( g.getGeneGroup() != null ) me++;
				else mu++;
			}
			System.err.println( me + " agargargargarg " + mu );
			
			/*if( rnamap != null ) for( String ggname : rnamap.keySet() ) {
				GeneGroup gg = rnamap.get( ggname );
				List<Tegeval> tegevals = gg.getTegevals();
				
				/*for( Tegeval te : tegevals ) {
					Contig contig = te.getContshort();
										
					//if( contig.getName().contains("antrani") && contig.getName().contains("contig00006") ) {
					//	System.err.println( contig.getGeneCount() );
					//}
					
					if( contig != null ) {
						contig.add( te );
						
						if( te.getGene().getName().contains("Met") ) {
							System.err.println( contig.getName() + "  " + te.getGene().getName() + "  " + contig.getTegevalsList().indexOf( te ) + "  " + contig.getGeneCount() );
						}
						/*Tegeval ste = check( contig );
						
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
								} else {
									contig.start = te;
								}
								
								te.setNum( ste.getNum() );
								Tegeval next = te.getNext();
								while( next != null ) {
									next.setNum( next.getPrevious().getNum()+1 );
									next = next.getNext();
								}
							} else {
								Tegeval prevnext = ste.setNext( te );
								te.setPrevious( ste );
								if( prevnext != null ) {
									prevnext.setPrevious( te );
									
									System.err.println( prevnext.getGene().getName() );
								} else {
									contig.end = te;
								}
								
								te.setNum( ste.getNum()+1 );
								Tegeval next = te.getNext();
								while( next != null ) {
									next.setNum( next.getPrevious().getNum()+1 );
									next = next.getNext();
								}
							}
						} else {
							contig.start = te;
							contig.end = te;
							te.setNum( 0 );
						}*
					}
				}*
			}*/
			sortLoci();
			
			for( String spec : speccontigMap.keySet() ) {
				System.err.println( spec );
				List<Contig> ctgs = speccontigMap.get( spec );
				for( Contig c : ctgs ) {
					System.err.println( c );
					int im = 0;
					if( c.getAnnotations() != null ) for( Annotation tv : c.getAnnotations() ) {
						((Tegeval)tv).unresolvedGap( im++ );
					}
				}
			}
			
			for( String ggname : rnamap.keySet() ) {
				GeneGroup gg = rnamap.get( ggname );
				List<Tegeval> tegevals = gg.getTegevals();
				
				/*for( Tegeval te : tegevals ) {
					Contig contig = te.getContshort();
					if( te.getGene().getName().contains("Met") ) {
						System.err.println( contig.getName() + "  " + te.getGene().getName() + "  " + contig.getTegevalsList().indexOf( te ) + "  " + contig.getGeneCount() );
					}
				}*/
			}
			
			nf = zipfilesystem.getPath("/contigorder.txt");
			//loadcazymap( cazymap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
			if( Files.exists( nf ) ) {
				is = Files.newInputStream(nf, StandardOpenOption.READ);
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
						
						if( ctg != null ) {
							List<Contig> splct = speccontigMap.get( ctg.getSpec() );
							
							if( c == '\\' ) ctg.setReverse( true );
							if( prevctg != null ) {
								prevctg.setConnection( ctg, ctg.isReverse(), !prevctg.isReverse() );
								
								splct.remove( ctg );
								int k = splct.indexOf( prevctg );
								splct.add( k+1, ctg );
							} else {
								splct.remove( ctg );
								splct.add( 0, ctg );
							}
							prevctg = ctg;
						} else {
							/*for( String key : contigmap.keySet() ) {
								if( key.contains("eiot") ) {
									System.err.println( key );
								}
							}*/
						}
						
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
					if( ctg != null ) {
						List<Contig> splct = speccontigMap.get( ctg.getSpec() );
						if( c == '\\' ) ctg.setReverse( true );
						if( prevctg != null ) {
							prevctg.setConnection( ctg, ctg.isReverse(), !prevctg.isReverse() );
							
							splct.remove( ctg );
							int k = splct.indexOf( prevctg );
							splct.add( k+1, ctg );
						} else {
							splct.remove( ctg );
							splct.add( 0, ctg );
						}
					}					
					line = br.readLine();
				}
				br.close();
			}
			
			/*FileWriter fw = null; // new FileWriter("all_short.blastout");
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
			// if( fw != null ) fw.close();*/		
	
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
					//ss.add(split[0]);
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
	
			//proxPreserve( locgene );
	
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
	
			//Map<String, Gene> unimap = null;
			//Map<String, Gene> genmap = null;
			//Map<String, Gene> gimap = new HashMap<String,Gene>();
			
			nf = zipfilesystem.getPath("/gene2refseq_short.txt");
			if( Files.exists( nf ) ) genmap = idMapping(new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), null, 5, 1, refmap, genmap, gimap);
			//loadcazymap( cazymap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
			
			/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("gene2refseq_short.txt") ) genmap = idMapping(new InputStreamReader(zipin), null, 5, 1, refmap, genmap, gimap);
				
				ze = zipin.getNextEntry();
			}
			zipin.close();*/
			
			//is = GeneSet.class.getResourceAsStream("/gene2refseq_short.txt"); // ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/
			//is = new GZIPInputStream( new FileInputStream("/data/gene2refseq.gz") );
			//genmap = idMapping(new InputStreamReader(is), "/home/sigmar/gene2refseq_short.txt", 5, 1, refmap, genmap, gimap);
			//is = GeneSet.class.getResourceAsStream("/gene2go_short.txt");
			//is = new GZIPInputStream( new FileInputStream("/home/sigmar/gene2go.gz") );
			//funcMapping(new InputStreamReader(is), genmap, "/home/sigmar/thermus/gene2go_short.txt");
			
			/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("idmapping_short.dat") ) unimap = idMapping(new InputStreamReader(zipin), null, 2, 0, refmap, genmap, gimap);
				else if( ze.getName().equals("ko2name.txt") ) ko2name = ko2nameMapping( new InputStreamReader(zipin) );
				
				ze = zipin.getNextEntry();
			}
			zipin.close();*/
			
			nf = zipfilesystem.getPath("/idmapping_short.dat");
			if( Files.exists( nf ) ) unimap = idMapping(new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), null, 2, 0, refmap, genmap, gimap);
			nf = zipfilesystem.getPath("/smap_short.txt");
			if( Files.exists( nf ) && !unimap.isEmpty() ) loadSmap( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), unimap );
			nf = zipfilesystem.getPath("/ko2name.txt");
			if( Files.exists( nf ) ) ko2name = ko2nameMapping( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );
			
			if( ko2name != null && !ko2name.isEmpty() ) {
				for( Gene g : genelist ) {
					if( ko2name.containsKey( g.koid ) ) {
						String name = ko2name.get( g.koid );
						if( name.startsWith("E") ) {
							int k = name.indexOf(',');
							if( k == -1 ) k = name.length(); 
							else ko2name.put( g.koid, name.substring(k+1).trim() );
							g.ecid = name.substring(1, k);
						} else if( name.contains(",") ) {
							if( name.charAt(1) >= 'A' && name.charAt(1) <= 'Z' ) {
								String[] split = name.split(",");
								String newname = "";
								for( int m = 1; m < split.length; m++ ) {
									newname += split[m].trim()+", ";
								}
								newname += split[0];
								ko2name.put( g.koid, newname );
							}
						}
					}
				}
			}
			
			//is = GeneSet.class.getResourceAsStream("/idmapping_short.dat"); // ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/
			//is = new GZIPInputStream( new FileInputStream("/data/idmapping.dat.gz") );
			//is = new FileInputStream("/u0/idmapping_short.dat");
			//unimap = idMapping(new InputStreamReader(is), "/home/sigmar/idmapping_short.dat", 2, 0, refmap, genmap, gimap);
			
			if( unimap != null ) {
				/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
				ze = zipin.getNextEntry();
				while( ze != null ) {
					if( ze.getName().equals("sp2go_short.txt") ) {
						funcMappingUni( new InputStreamReader( zipin ), unimap, null );
					} else if( ze.getName().equals("ko2go.txt") ) {
						BufferedReader br = new BufferedReader( new InputStreamReader(zipin) );
						String line = br.readLine();
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
					}
					//else if( ze.getName().equals("gene2refseq_short.txt") ) genmap = idMapping(new InputStreamReader(zipin), null, 5, 1, refmap, true);
					
					ze = zipin.getNextEntry();
				}
				zipin.close();*/
				//is = GeneSet.class.getResourceAsStream("/sp2go_short.txt");
				//is = new GZIPInputStream( new FileInputStream( "/data/sp2go.txt.gz" ) );
				//funcMappingUni(new InputStreamReader(is), unimap, "/home/sigmar/sp2go_short.txt");
				
				nf = zipfilesystem.getPath("/sp2go_short.txt");
				if( Files.exists( nf ) ) funcMappingUni( new BufferedReader( new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) )), unimap, null );
				nf = zipfilesystem.getPath("/ko2go.txt");
				if( Files.exists(nf) ) {
					BufferedReader br = new BufferedReader( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );
					String line = br.readLine();
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
				}
			}
			if( genmap != null ) genmap.clear();
	
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
			
			/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("go_short.obo") ) readGoInfo( new InputStreamReader(zipin), totalgo, null);
				
				ze = zipin.getNextEntry();
			}
			zipin.close();*/
			is = GeneSet.class.getResourceAsStream("/gene_ontology_ext.obo");
			//Map<String,Function> funcmap = 
			if( is != null ) {
				readGoInfo( new InputStreamReader(is), totalgo, null ); // "/home/sigmar/MAT/go_short.obo");
			}
			
			//is = GeneSet.class.getResourceAsStream("/go_short.obo");
			//readGoInfo(new InputStreamReader(is), totalgo, null);
			for (String go : funcmap.keySet()) {
				Function f = funcmap.get(go);
				f.index = funclist.size();
				funclist.add(f);
			}
			totalgo.clear();
			
			Map<String,String>	jgiGeneMap = new HashMap<String,String>();
			/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("genes.faa") ) jgiGeneMap = jgiGeneMap( new InputStreamReader(zipin) );
				
				ze = zipin.getNextEntry();
			}
			zipin.close();
			
			zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
			ze = zipin.getNextEntry();
			while( ze != null ) {
				if( ze.getName().equals("ko.tab.txt") ) jgiGene2KO( new InputStreamReader(zipin), jgiGeneMap, refmap );
				
				ze = zipin.getNextEntry();
			}
			zipin.close();*/
			
			nf = zipfilesystem.getPath("/genes.faa");
			if( Files.exists( nf ) ) jgiGeneMap = jgiGeneMap( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );
			nf = zipfilesystem.getPath("/ko.tab.txt");
			if( Files.exists( nf ) ) jgiGene2KO( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), jgiGeneMap, refmap );
			zipfilesystem.close();
			
			/*
			 * is = GeneSet.class.getResourceAsStream(""); Map<String,String> komap
			 * = koMapping( new FileReader("/home/sigmar/asgard-bio/data/ko"),
			 * funclist, genelist ); for( Function f : funclist ) { if(
			 * komap.containsKey( f.ec ) ) { for( String gn : f.geneentries ) { Gene
			 * g = refmap.get(gn); if( g.keggid == null ) g.keggid =
			 * komap.get(f.ec); } } }
			 */
	
			updateShareNum( specList );
			
			Set<String> allecs = new HashSet<String>();
			for( Function f : funclist ) {
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
	
			Set<String> set = new TreeSet<String>();
			for (Gene g : genelist) {
				Tegeval tv = g.tegeval;
				if (tv.eval <= 0.00001 && tv.teg.startsWith("[") && tv.teg.endsWith("]"))
					set.add(tv.teg);
			}
	
			for (String sp : set) {
				specombo.addItem(sp);
			}
			
			if( uclusterlist != null ) clusterMap = initCluster(uclusterlist);
			
			//table.tableChanged( new TableModelEvent( table.getModel() ) );
			//ftable.tableChanged( new TableModelEvent( ftable.getModel() ) );
			TableModel nullmodel = new AbstractTableModel() {
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
			table.setModel( nullmodel );
			ftable.setModel( nullmodel );
			table.setModel( groupModel );
			ftable.setModel( ftablemodel );
			
			me = 0;
			mu = 0;
			for( Gene g : genelist ) {
				if( g.getGeneGroup() != null ) me++;
				else mu++;
			}
			System.err.println( me + "  " + mu );
		}
		
		File idf = new File("/home/sigmar/idspec.txt");
		BufferedWriter bw = new BufferedWriter( new FileWriter(idf) ); //Files.newBufferedWriter(idf.toPath(), OpenOption.);
		for( String id : refmap.keySet() ) {
			Gene g = refmap.get(id);
			bw.write( id + "\t" + g.getSpecies() + "\n" );
		}
		bw.close();
	}
	
	private void updateShareNum( Collection<String> specs ) {
		if( specset != null ) specset.clear();
		else specset = new HashMap<Set<String>, ShareNum>();
		
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
		Map<Set<String>, ShareNum> subset = new HashMap<Set<String>, ShareNum>();
		
		for (GeneGroup gg : allgenegroups) {
			Set<String>	species = gg.getSpecies();
			Set<String>	tmpspec = new HashSet<String>( species );
			tmpspec.retainAll( specs );
			
			if( species != null ) {
				ShareNum sharenum = null;
				if (subset.containsKey( tmpspec ) ) {
					sharenum = subset.get( tmpspec );
					if( !specset.containsKey( species ) ) specset.put(species, sharenum);
					sharenum.numshare++;
				} else {
					sharenum = new ShareNum(1, sn++);
					specset.put( species, sharenum );
					subset.put( tmpspec, sharenum );
				}
			}
		}
	}
	
	private void jgiGene2KO(InputStreamReader inputStreamReader, Map<String, String> jgiGeneMap, Map<String, Gene> refmap) throws IOException {
		BufferedReader br = new BufferedReader( inputStreamReader );
		String	line = br.readLine();
		while( line != null ) {
			String[] split = line.split("\t");
			if( jgiGeneMap.containsKey( split[0] ) ) {
				String refid = jgiGeneMap.get( split[0] );
				Gene g = refmap.get( refid );
				if( g != null ) {
					if( g.koid == null || g.koid.length() == 0 ) g.koid = split[9].substring(3);
					if( (g.ecid == null || g.ecid.length() == 0) && split[11].length() > 3 ) g.ecid = split[11].substring(3);
				}
			}
			line = br.readLine();
		}
	}

	private Map<String, String> jgiGeneMap(InputStreamReader inputStreamReader) throws IOException {
		Map<String,String>	jgimap = new HashMap<String,String>();
		BufferedReader br = new BufferedReader( inputStreamReader );
		String	line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				String[] split = line.substring(1).split(" ");
				jgimap.put(split[0], split[1]);
			}
			line = br.readLine();
		}
		return jgimap;
	}

	private JComponent newSoft(JButton jb, Container comp, JApplet applet, JComboBox selcomblocal) throws IOException {
		/*InputStream nis2 = GeneSet.class.getResourceAsStream("/exp_short.blastout");
		BufferedReader br2 = new BufferedReader( new InputStreamReader(nis2) );
		String line2 = br2.readLine();
		br2.close();*/
		// aas.clear();

		// return new JComponent() {};
		return showGeneTable(null, jb, comp, applet, selcomblocal);// clustInfoMap// );
	}
	
	public Tegeval check( Contig contig ) {
		for( GeneGroup sgg : allgenegroups ) {
			for( Tegeval ste : sgg.getTegevals() ) {
				if( ste.getContig() != null && ste.getContshort().equals( contig ) ) {
					return ste;
				}
			}
		}
		return null;
	}

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

	public void saveContigOrder() throws IOException {
		Map<String,String> env = new HashMap<String,String>();
		env.put("create", "true");
		//Path path = zipfile.toPath();
		String uristr = "jar:" + zippath.toUri();
		zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
		zipfilesystem = FileSystems.newFileSystem( zipuri, env );

		Path nf = zipfilesystem.getPath("/contigorder.txt");
		//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
		BufferedWriter bf = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
		for( String spec : speccontigMap.keySet() ) {
			List<Contig>	lct = speccontigMap.get( spec );
			for( Contig ct : lct ) {
				bf.write( (ct.isReverse() ? "\\" : "/")+ct.getName() );
			}
			bf.write("\n");
		}
		bf.close();
		zipfilesystem.close();
		
		/*FileWriter fw = new FileWriter("/home/sigmar/sandbox/distann/src/contigorder.txt");
		fw.write("co\n");
		for( String spec : speccontigMap.keySet() ) {
			List<Contig>	lct = speccontigMap.get( spec );
			for( Contig ct : lct ) {
				fw.write( (ct.isReverse() ? "\\" : "/")+ct.getName() );
			}
			fw.write("\n");
		}*/
		
		//Set<Contig>	saved = new HashSet<Contig>();
		/*for( String cs : GeneSet.contigmap.keySet() ) {
			Contig c = GeneSet.contigmap.get( cs );
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
		}*/
		//fw.close();
	}
	
	public void stop() {
		super.stop();
		try {
			if( zippath != null ) saveContigOrder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportProteinSequences() {
		JFileChooser filechooser = new JFileChooser();
		if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			try {
				Path dbPath = filechooser.getSelectedFile().toPath();
				BufferedWriter bw = Files.newBufferedWriter(dbPath);
				for( Gene g : genelist ) {
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
	
	public void exportGeneClusters() {
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
	
	public void exportGenomes() {
		JCheckBox	joincontigs = new JCheckBox("Join contigs");
		JCheckBox	translations = new JCheckBox("Include translations");
		JComponent[] comps = new JComponent[] { joincontigs, translations };
		List scl = getSelspecContigs( Arrays.asList( comps ) );
		
		if( scl.get(0) instanceof String ) {
			List<String> lspec = (List<String>)scl;
			
			JFileChooser filechooser = new JFileChooser();
			filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( filechooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				for( String spec : lspec ) {
					Map<String,List<Annotation>> mapan = new HashMap<String,List<Annotation>>();
					List<Contig> contigs = speccontigMap.get(spec);
					Serifier serifier = new Serifier();
					for( Contig c : contigs ) {
						serifier.addSequence(c);
						serifier.mseq.put(c.getName(), c);
						
						List<Annotation> lann = new ArrayList<Annotation>();
						if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
							Tegeval tv = (Tegeval)ann;
							Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, tv.getGene().getName() );
							anno.type = "CDS";
							GeneGroup gg = tv.getGene().getGeneGroup();
							String cazy = gg != null ? gg.getCommonCazy(cazymap) : null;
							if( cazy != null ) anno.addDbRef( "CAZY:"+cazy );
							Cog cog = gg != null ? gg.getCommonCog(cogmap) : null;
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
			List<Contig> contigs = (List<Contig>)scl;
			Serifier serifier = new Serifier();
			for( Contig c : contigs ) {
				serifier.addSequence(c);
				serifier.mseq.put(c.getName(), c);
				
				List<Annotation> lann = new ArrayList<Annotation>();
				if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
					Tegeval tv = (Tegeval)ann;
					Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, tv.getGene().getName() );
					anno.type = "CDS";
					GeneGroup gg = tv.getGene().getGeneGroup();
					String cazy = gg != null ? gg.getCommonCazy(cazymap) : null;
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
	
	public void fetchGenomes() {
		if( zippath == null ) {
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
			String uristr = "jar:" + zippath.toUri();
			zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );
			
			final SerifyApplet	sa = new SerifyApplet( zipfilesystem );
			sa.init( frame );
			
			for( Path root : zipfilesystem.getRootDirectories() ) {
				Files.list(root).filter( new Predicate<Path>() {
					@Override
					public boolean test(Path t) {
						String fname = t.getFileName().toString();
						return /*fname.endsWith(".gbk") || */fname.endsWith(".fna") || fname.endsWith(".fsa") || fname.endsWith(".fa") || fname.endsWith(".fasta") || fname.endsWith(".aa") || fname.endsWith(".nn") || fname.endsWith(".trna") || fname.endsWith(".rrna");
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
				zipfilesystem.close();
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
					zipfilesystem.close();

					cleanUp();
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
			zippath = fc.getSelectedFile().toPath();
			//is = new FileInputStream( zipfile );
		}
	}
	
	TextField tb1 = null;
	TextField tb2 = null;
	TextField epar = null;
	final JFXPanel fxpanel = new JFXPanel();
	Scene fxs = null;

	Component comp;
	public void init(final Container comp) {
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
				exportGenomes();
			}
		});
		file.addSeparator();
		file.add( new AbstractAction("Export protein sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportProteinSequences();
			}
		});
		file.add( new AbstractAction("Export gene clusters") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportGeneClusters();
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
				
				if( tb1 != null ) {
					float id = Float.parseFloat( tb1.getText() );
					float len = Float.parseFloat( tb2.getText() );
					String expar = epar.getText();
					
					tb1 = null;
					tb2 = null;
					epar = null;
					
					try {
						Set<String> species = getSelspec(null, getSpecies(), null);
						
						Path queryPath = Files.createTempFile("all", ".fsa");
						BufferedWriter qbw = Files.newBufferedWriter(queryPath);
						
						Path dbPath = Files.createTempFile("all", ".fsa");
						BufferedWriter bw = Files.newBufferedWriter(dbPath);
						for( Gene g : genelist ) {
							if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
								if( species.contains( g.getSpecies() ) ) {
									StringBuilder gs = g.tegeval.getProteinSequence();
									qbw.append(">" + g.id + "\n");
									for (int i = 0; i < gs.length(); i += 70) {
										qbw.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
									}
								}
								
								StringBuilder gs = g.tegeval.getProteinSequence();
								bw.append(">" + g.id + "\n");
								for (int i = 0; i < gs.length(); i += 70) {
									bw.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
								}
							}
						}
						bw.close();
						qbw.close();
						NativeRun nrun = new NativeRun();
						SerifyApplet.blastRun(nrun, queryPath, dbPath, "prot", expar, null, true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	alignclusters = new AbstractAction("Align clusters") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Map<String,String> env = new HashMap<String,String>();
					env.put("create", "true");
					String uristr = "jar:" + zippath.toUri();
					zipuri = URI.create( uristr );
					zipfilesystem = FileSystems.newFileSystem( zipuri, env );
					//s.makeBlastCluster(zipfilesystem.getPath("/"), p, 1);
					Path aldir = zipfilesystem.getPath("aligned");
					final Path aligneddir = Files.exists( aldir ) ? aldir : Files.createDirectory( aldir );
					
					NativeRun nrun = new NativeRun();
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try {
								zipfilesystem.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					
					//ExecutorService es = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
					
					Object[] cont = new Object[3];
					
					Collection<GeneGroup> ggset;
					int[] rr = table.getSelectedRows();
					ggset = new HashSet<GeneGroup>();
					if( rr.length == 0 ) {
						for( GeneGroup gg : allgenegroups ) {
							//GeneGroup gg = allgenegroups.get(table.convertRowIndexToModel(r));
							//gg.getCommonTag()
							if( gg != null && gg.getCommonTag() == null && gg.size() > 1 ) ggset.add( gg );
						}
					} else {
						for( int r : rr ) {
							GeneGroup gg = allgenegroups.get(table.convertRowIndexToModel(r));
							//gg.getCommonTag()
							if( gg != null && gg.getCommonTag() == null && gg.size() > 1 ) ggset.add( gg );
						}
					}
					
					//int i = 0;
					List commandsList = new ArrayList();
					for( GeneGroup gg : ggset ) {
						/*if( gg.size() > 1 ) {
							System.err.println( gg.size() );
						}*/
						String fasta = gg.getFasta( true );
						String[] cmds = new String[] {"/usr/bin/muscle"};
						Object[] paths = new Object[] {fasta.getBytes(), aligneddir.resolve(gg.getCommonId()+".aa"), null};
						commandsList.add( paths );
						commandsList.add( Arrays.asList(cmds) );
						
						//if( i++ > 5000 ) break;
					}
					nrun.runProcessBuilder("Running muscle", commandsList, run, cont, true);
				} catch (IOException e1) {
					if( zipfilesystem != null ) {
						try {
							zipfilesystem.close();
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
				Set<String> specs = getSelspec(GeneSet.this, specList, null);
				updateShareNum(specs);
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
				
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					Path p = fc.getSelectedFile().toPath();
					Serifier s = new Serifier();
					//s.mseq = aas;
					for( String gk : refmap.keySet() ) {
						Gene g = refmap.get( gk );
						if( g.tegeval.alignedsequence != null ) System.err.println( g.tegeval.alignedsequence.name );
						s.mseq.put(gk, g.tegeval.alignedsequence);
					}
					
					Map<String,String>	idspec = new HashMap<String,String>();
					for( String idstr : refmap.keySet() ) {
						Gene gene = refmap.get( idstr );
						idspec.put(idstr, gene.getSpecies());
					}
					//Sequences seqs = new Sequences(user, name, type, path, nseq)
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						String uristr = "jar:" + zippath.toUri();
						zipuri = URI.create( uristr );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						List<Set<String>> cluster = new ArrayList<Set<String>>();
						/*for( Set<String> specs : clusterMap.keySet() ) {
							Set<Map<String,Set<String>>> uset = clusterMap.get( specs );
							for( Map<String,Set<String>> umap : uset ) {
								for( String val : umap.keySet() ) {
									Set<String> sset = umap.get(val);
									cluster.add( sset );
								}
							}
						}*/
						
						s.makeBlastCluster(zipfilesystem.getPath("/"), p, 1, id, len, idspec, cluster);
						
						System.err.println( cluster.get(0) );
						if( uclusterlist != null ) System.err.println( uclusterlist.get(0) );
						
						zipfilesystem.close();
					} catch (IOException e1) {
						if( zipfilesystem != null ) {
							try {
								zipfilesystem.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	importgenesymbolaction = new AbstractAction("Import gene symbols") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						//Path path = zipfile.toPath();
						String uristr = "jar:" + zippath.toUri();
						zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						Path nf = zipfilesystem.getPath("/smap_short.txt");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						uni2symbol(new InputStreamReader(is), bw, unimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						zipfilesystem.close();
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
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						BufferedReader rd = Files.newBufferedReader(fc.getSelectedFile().toPath());
						loadcazymap(cazymap, rd);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	functionmappingaction = new AbstractAction("Function mapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						//Path path = zipfile.toPath();
						String uristr = "jar:" + zippath.toUri();
						zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						Path nf = zipfilesystem.getPath("/sp2go_short.txt");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						BufferedReader br = new BufferedReader( new InputStreamReader( new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) ) ) );
						if( unimap != null ) unimap.clear();
						//unimap = idMapping(new InputStreamReader(is), bw, 2, 0, refmap, genmap, gimap);
						funcMappingUni( br, unimap, bw );
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						zipfilesystem.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		AbstractAction	importidmappingaction = new AbstractAction("Import idmapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						Map<String,String> env = new HashMap<String,String>();
						env.put("create", "true");
						//Path path = zipfile.toPath();
						String uristr = "jar:" + zippath.toUri();
						zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
						
						Path nf = zipfilesystem.getPath("/idmapping_short.dat");
						BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);
						
						InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
						if( unimap != null ) unimap.clear();
						unimap = idMapping(new InputStreamReader(is), bw, 2, 0, refmap, genmap, gimap);
						
						bw.close();
						//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
						zipfilesystem.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
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
		ActionCollection.addAll( view, clusterMap, GeneSet.this, speccontigMap, table, comp, cs );
		
		JMenu		help = new JMenu("Help");
		help.add( new AbstractAction("About") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog( comp, "CompGen 1.0" );
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
				if( currentSerify == null ) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);
					
					Map<String,String> env = new HashMap<String,String>();
					//Path path = zipfile.toPath();
					String uristr = "jar:" + zippath.toUri();
					zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
					try {
						zipfilesystem = FileSystems.newFileSystem( zipuri, env );
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
					
					SerifyApplet sa = new SerifyApplet( zipfilesystem );
					sa.init( frame );
					//frame.add( )
					currentSerify = sa;
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
					Gene gg = genelist.get(cr);
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
				
				StringBuilder sb = new StringBuilder();
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
						StringBuilder ps = tv.getProteinSequence();
						sb.append(">" + tv.name.substring(0, tv.name.indexOf('_')) + "\n");
						for (int i = 0; i < ps.length(); i += 70) {
							sb.append( ps.substring(i, Math.min(i + 70, tv.getProteinLength() )) + "\n");
						}
					}
				}
				
				try {
					currentSerify.addSequences("uh", new StringReader( sb.toString() ), Paths.get("/"), null);
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
					Gene gg = genelist.get(cr);
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
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
				jf.initGui(frame);

				Map<String, Sequence> contset = new HashMap<String, Sequence>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Gene gg = genelist.get(cr);
					Tegeval tv = gg.tegeval;
					String contig = tv.getContshort().getName();
					StringBuilder aa = tv.getProteinSequence();
					Sequence seq = new Sequence( contig, aa, serifier.mseq );
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
						GeneGroup gg = allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				Set<String>	specs = null;
				if( rr.length > 1 ) specs = getSelspec(comp, specList, null);
				showSequences( comp, genegroups, false, specs );
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
						GeneGroup gg = allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				
				Serifier	serifier = new Serifier();
				for( GeneGroup ggroup : genegroups ) {
					for( Tegeval tv : ggroup.getTegevals() ) {
						String selspec = tv.getContshort().getSpec();//tv.getContig();
						String spec = nameFix( selspec );
						/*if( selspec.contains("hermus") ) spec = selspec;
						else {
							Matcher m = Pattern.compile("\\d").matcher(selspec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) spec = "Thermus_" + selspec;
							else spec = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
						}*/
						
						Sequence seq = tv.getAlignedSequence();
						
						if( seq != null ) {
							seq.setName( spec );
							//Sequence seq = new Sequence( contig, seqstr, null );
							serifier.addSequence(seq);
						} else {
							StringBuilder sb = tv.getProteinSequence();
							Sequence sseq = new Sequence( spec, sb, serifier.mseq );
							serifier.addSequence( sseq );
						}
					}
				}
				showAlignedSequences( comp, serifier );
			}
		});
		sequencemenu.add(new AbstractAction("Split/Show sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = getSelectedASeqs( table, genelist, GeneSet.this, specList );
				if( currentSerify == null ) {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
					frame.setSize(800, 600);
					
					SerifyApplet sa = new SerifyApplet( zipfilesystem );
					sa.init( frame );
					currentSerify = sa;
					
					frame.setVisible( true );
				}
				
				try {
					currentSerify.addSequences("uh", new StringReader( sb.toString() ), Paths.get("/"), null);
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
						GeneGroup gg = allgenegroups.get(cr);
						genegroups.add( gg );
					}
				} else {
					for (int r : rr) {
						int cr = table.convertRowIndexToModel(r);
						Gene gg = genelist.get(cr);
						genegroups.add( gg.getGeneGroup() );
					}
				}
				Set<String> specs = null;
				if( rr.length > 1 ) specs = getSelspec( comp, specList, null );
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
							Gene gg = genelist.get(cr);
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

						Set<Contig> contset = new HashSet<Contig>();
						int[] rr = table.getSelectedRows();
						for (int r : rr) {
							int cr = table.convertRowIndexToModel(r);
							Gene gg = genelist.get(cr);
							Tegeval tv = gg.tegeval;
							contset.add( tv.getContshort() );
						}

						FileWriter fw = new FileWriter(f);
						for (Contig contig : contset) {
							fw.append(">" + contig + "\n");
							if (contigmap.containsKey(contig)) {
								StringBuilder dna = contigmap.get(contig).getStringBuilder();
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
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
				jf.initGui(frame);

				Set<Contig> contset = new HashSet<Contig>();
				Set<Tegeval> tvset = new HashSet<Tegeval>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene gg = genelist.get(cr);
						Tegeval tv = gg.tegeval;
						tvset.add( tv );
						//serifier.addAnnotation( tv );
						contset.add( tv.getContshort() );
					} else {
						GeneGroup gg = allgenegroups.get(cr);
						for( Tegeval tv : gg.getTegevals() ) {
							tv.color = Color.red;
							tvset.add( tv );
							Contig contig = tv.getContshort();
							contset.add( contig );							
							//serifier.addAnnotation( tv );
						}
					}
					/*Sequence seq;
					Contig contig = tv.getContshort();
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

				for (Contig contig : contset) {
					int start = Integer.MAX_VALUE;
					int stop = Integer.MIN_VALUE;
					for( Tegeval tv : tvset ) {
						if( contig == tv.seq ) {
							start = Math.min(start, tv.start);
							stop = Math.max(stop, tv.stop);
						}
					}
					Sequence newseq = new Sequence( contig.name, new StringBuilder(contig.getSubstring(start, stop, 1)), serifier.mseq);
					if( contig.isReverse() ) {
						newseq.reverse();
						newseq.complement();
					}
					
					serifier.addSequence( newseq );
					for( Tegeval tv : tvset ) {
						if( contig == tv.seq ) {
							newseq.addAnnotation( tv );
						}
						serifier.addAnnotation(tv);
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
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
				jf.initGui(frame);

				//Map<Contig, Sequence> contset = new HashMap<Contig, Sequence>();
				/*int[] rr = table.getSelectedRows();
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

				Set<Contig> contigs = new HashSet<Contig>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					if( table.getModel() == defaultModel ) {
						Gene g = genelist.get( cr );
						Tegeval tv = g.tegeval;
						tv.color = Color.red;
						Contig contig = tv.getContshort();
						contig.offset = -tv.start;
						contigs.add( contig );
						/*Annotation a = new Annotation(contig, contig.getName(), Color.red, serifier.mann);
						a.setStart(tv.start);
						a.setStop(tv.stop);
						a.setOri(tv.ori);
						a.setGroup(g.name);
						a.setType("gene");*/
						//serifier.addAnnotation( tv );
					} else {
						GeneGroup gg = allgenegroups.get( cr );
						for( Tegeval tv : gg.getTegevals() ) {
							tv.color = Color.red;
							Contig contig = tv.getContshort();
							contig.offset = -tv.start;
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
					//Gene gg = genelist.get(cr);
					//for (Gene g : genelist) {
					//if (g.species != null) {
						//for (String sp : g.species.keySet()) {
				}

				for( Contig contig : contigs ) {
					for( Annotation ann : contig.getAnnotations() ) {
						serifier.addAnnotation( ann );
					}
					
					serifier.addSequence( contig );
					//if(contig.getAnnotations() != null)
					//	Collections.sort(contig.getAnnotations());
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
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
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
					//if( gb.isSelected() ) new GeneSorter().mynd( GeneSet.this, genelist, table, null, contigmap );
					//else 
					new GeneSorter().groupMynd( GeneSet.this, allgenegroups, specList, genelist, table, contigmap, specset );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		AbstractAction matrixaction = new AbstractAction("Relation matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>	descombo = new JComboBox<String>( deset.toArray( new String[deset.size()] ) );
				JCheckBox			anicheck = new JCheckBox("ANImatrix");
				descombo.insertItemAt("", 0);
				descombo.setSelectedIndex( 0 );
				JOptionPane.showMessageDialog( GeneSet.this, new Object[] { descombo, anicheck } );
				String val = descombo.getSelectedItem().toString();
				
				Collection<GeneGroup> ss = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					ss.add( allgenegroups.get( table.convertRowIndexToModel(r) ) );
				}
				if( ss.isEmpty() ) ss = allgenegroups;
				
				Set<String> species = getSelspec( GeneSet.this, specList );
				bimg = anicheck.isSelected() ? animatrix( species, clusterMap, val, ss ) : bmatrix( species, clusterMap, val );
				
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
							for (String spc : specList) {
								if (++i == specList.size())
									ret.append(spc + "\n");
								else
									ret.append(spc + "\t");
							}

							int where = 0;
							for (String spc1 : specList) {
								int wherex = 0;
								for (String spc2 : specList) {
									int spc1tot = 0;
									int spc2tot = 0;
									int totot = 0;

									int spc1totwocore = 0;
									int spc2totwocore = 0;
									int tototwocore = 0;
									for (Set<String> set : clusterMap.keySet()) {
										Set<Map<String, Set<String>>> erm = clusterMap.get(set);
										if (set.contains(spc1)) {
											if (set.size() < specList.size()) {
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
										if (where == specList.size() - 1)
											ret.append(0 + "\n");
										else
											ret.append(0 + "\t");
									} else {
										double hlut = (double) spc2totwocore / (double) spc1totwocore;
										double sval = hlut; // 1.0/( 1.1-hlut );
										double val = Math.pow(50.0, sval - 0.3) - 1.0;
										double dval = Math.round(100.0 * (val)) / 100.0;

										if (wherex == specList.size() - 1)
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
						        	if( jfc.showSaveDialog( GeneSet.this ) == JFileChooser.APPROVE_OPTION ) {
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
		AbstractAction anitreeaction = new AbstractAction("ANI tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> species = getSelspec( GeneSet.this, specList );
				List<String> speclist = new ArrayList<String>( species );
				
				Collection<GeneGroup> allgg = new HashSet<GeneGroup>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					allgg.add( allgenegroups.get( table.convertRowIndexToModel(r) ) );
				}
				if( allgg.isEmpty() ) allgg = allgenegroups;
				
				try {
					Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
					
					double[] corrarr = new double[ speclist.size()*speclist.size() ];
					int where = 0;
					for (String spec1 : speclist) {
						int wherex = 0;
						
						String spc1 = nameFix( spec1 );
						
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
												Sequence seq1 = tv1.alignedsequence;
												Sequence seq2 = tv2.alignedsequence;
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
					corrInd.clear();
					for( String spec : speclist ) {
						corrInd.add( nameFix( spec ) );
					}
					Node n = tu.neighborJoin(corrarr, corrInd, null, false, false);
					System.err.println( n );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		windowmenu.add( matrixaction );
		windowmenu.add( anitreeaction );
		windowmenu.add( new AbstractAction("Neighbourhood") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Set<GeneGroup>	geneset = new HashSet<GeneGroup>();
					int[] rr = table.getSelectedRows();
					if( table.getModel() == groupModel ) {
						for( int rowIndex : rr ) {
							int r = table.convertRowIndexToModel( rowIndex );
							GeneGroup gg = allgenegroups.get( r );
							geneset.add( gg );
						}
					} else {
						for( int rowIndex : rr ) {
							int r = table.convertRowIndexToModel( rowIndex );
							Gene gene = genelist.get( r );
							geneset.add( gene.getGeneGroup() );
						}
					}
					new Neighbour().neighbourMynd( GeneSet.this, comp, genelist, geneset, contigmap );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		windowmenu.add( new AbstractAction("Synteny") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Set<String> species = speciesFromCluster( clusterMap );
				new Synteni().syntenyMynd( GeneSet.this, comp, genelist );
			}
		});
		AbstractAction compareplotaction = new AbstractAction("Gene atlas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new GeneCompare().comparePlot( GeneSet.this, comp, genelist, clusterMap );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		windowmenu.add( compareplotaction );
		
		AbstractAction syntenygradientaction = new AbstractAction("Synteny gradient") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SyntGrad().syntGrad( GeneSet.this );
			}
		};
		windowmenu.add( syntenygradientaction );
		
		AbstractAction genexyplotaction = new AbstractAction("Gene XY plot") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new XYPlot().xyPlot( GeneSet.this, comp, genelist, clusterMap );
			}
		};
		windowmenu.add( genexyplotaction );
		windowmenu.addSeparator();
		windowmenu.add( new AbstractAction("Run antismash") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Serifier ser = new Serifier();
					Set<String> selspec = getSelspec(null, getSpecies(), null);
					
					Path[] pt = null;
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
						pt = new Path[3];
						pt[2] = fc.getSelectedFile().toPath();
					}
					
					List<Object> commands = new ArrayList<Object>();
					//commands.add(genexyplotaction)
					
					for( String spec : selspec ) {
						Path p = Paths.get("/Users/sigmar/"+spec+".gbk");
						//BufferedWriter fw = Files.newBufferedWriter( p );
						List<Contig> clist = speccontigMap.get( spec );
						
						Map<String,List<Annotation>> mapan = new HashMap<String,List<Annotation>>();
						Serifier serifier = new Serifier();
						for( Contig c : clist ) {
							serifier.addSequence(c);
							serifier.mseq.put(c.getName(), c);
							
							List<Annotation> lann = new ArrayList<Annotation>();
							if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
								Tegeval tv = (Tegeval)ann;
								Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, tv.getGene().getName() );
								anno.type = "CDS";
								GeneGroup gg = tv.getGene().getGeneGroup();
								String cazy = gg != null ? gg.getCommonCazy(cazymap) : null;
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
						String[] cmds = {"/Users/sigmar/antiSMASH2/run_antismash.py", apath};
						commands.add( pt );
						commands.add( Arrays.asList( cmds ) );
					}
					
					Runnable run = new Runnable() {
						@Override
						public void run() {
							
						}
					};
					
					NativeRun nr = new NativeRun();
					nr.runProcessBuilder("antismash", commands, run, new Object[3], true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JMenu		select = new JMenu("Select");
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
				Set<String> specset = getSelspec(GeneSet.this, specList);
				for( GeneGroup gg : allgenegroups ) {
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
				Set<String> specset = getSelspec(GeneSet.this, specList);
				for( GeneGroup gg : allgenegroups ) {
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
				Set<String> specset = getSelspec(GeneSet.this, specList);
				for( GeneGroup gg : allgenegroups ) {
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
				for( GeneGroup gg : allgenegroups ) {
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
				for( GeneGroup gg : allgenegroups ) {
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
				
				final List<String> species = getSpecies();
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
				
				for( GeneGroup gg : allgenegroups ) {
					if( blehbtn.isSelected() ) {
						Set<String> ss = new HashSet<String>( gg.species.keySet() );
						ss.removeAll( specs );
						if( ss.size() == 0 ) {
							int r = GeneSet.this.table.convertRowIndexToView( gg.index );
							GeneSet.this.table.addRowSelectionInterval( r, r );
						}
					} else if( gg.species.keySet().containsAll( specs ) && (panbtn.isSelected() || specs.size() == gg.species.size()) ) {
						int r = GeneSet.this.table.convertRowIndexToView( gg.index );
						if( r >= 0 && r < GeneSet.this.table.getRowCount() ) {
							GeneSet.this.table.addRowSelectionInterval( r, r );
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
					for( GeneGroup gg : allgenegroups ) {
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
		select.add( new AbstractAction("Blast select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blast();
			}
		});
		
		menubar.add( file );
		menubar.add( edit );
		menubar.add( view );
		menubar.add( sequencemenu );
		menubar.add( windowmenu );
		menubar.add( select );
		menubar.add( help );
		
		final Window window = SwingUtilities.windowForComponent(comp);
		initFSKeyListener(window);
		if ( comp instanceof JFrame || window instanceof JFrame) {
			JFrame frame = (JFrame)( window == null ? comp : window );
			if (!frame.isResizable())
				frame.setResizable(true);

			frame.addKeyListener(keylistener);
			frame.setJMenuBar(menubar);
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
			} catch ( NoSuchMethodError | Exception e1 ) {
				e1.printStackTrace();
			}
		
		try {
			comp.add(newSoft(jb, comp, GeneSet.this, selcomb));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveSel( String name, String val) throws Exception, NoSuchMethodError {
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

	List<JSplitPane> 				splitpaneList = new ArrayList<JSplitPane>();

	JFrame 							frame = new JFrame();
	public Map<String, Contig> 		contigmap = new TreeMap<String, Contig>();
	Map<String, List<Contig>>		speccontigMap = new TreeMap<String, List<Contig>>();
	//Map<Contig, List<Tegeval>>		contigLocMap = new HashMap<Contig, List<Tegeval>>();
	//static final List<Tegeval> 	ltv = new ArrayList<Tegeval>();
	//final List<Contig> 			contigs = new ArrayList<Contig>();
	
	/**
	 * Deprecated
	 * @param species
	 * @return
	 */
	public Map<String,List<Contig>> getSpecContigMap( Collection<String> species ) {
		final Map<String,List<Contig>>	specContMap = new HashMap<String,List<Contig>>();
		for( String ctname : contigmap.keySet() ) {
			for( String spec : species ) {
				if( ctname.contains( spec ) ) {
					List<Contig>	contlist;
					if( specContMap.containsKey( spec ) ) {
						contlist = specContMap.get( spec );
					} else {
						contlist = new ArrayList<Contig>();
						specContMap.put( spec, contlist );
					}
					contlist.add( contigmap.get( ctname ) );
					break;
				}
			}
		}
		return specContMap;
	}
	
	public int getGlobalIndex( Tegeval tv ) {
		int count = 0;
		List<Contig>	ctlist = speccontigMap.get( tv.getContshort().getSpec() );
		
		if( ctlist != null ) for( Contig ct : ctlist ) {
			if( ct != tv.getContshort() ) {
				count += ct.getGeneCount();
			} else {
				count += ct.isReverse() ? ct.getGeneCount() - tv.getNum() : tv.getNum();
				break;
			}
		}
		
		return count;
	}
	
	static Map<String, String> swapmap = new HashMap<String, String>();
	private void init(String[] args) {
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
			
			clusterFromBlastResults(new File("/home/sigmar/").toPath(), new String[] { "thermus_join.blastout" }, "/home/sigmar/thermus_cluster.txt", "/home/sigmar/simblastcluster.txt", true);
			
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
	
	boolean bleh = false;
}
