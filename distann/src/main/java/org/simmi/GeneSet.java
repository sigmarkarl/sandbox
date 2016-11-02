package org.simmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTable;

import javafx.stage.Stage;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.simmi.shared.Annotation;
import org.simmi.shared.Cog;
import org.simmi.shared.Contig;
import org.simmi.shared.Erm;
import org.simmi.shared.Function;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;
import org.simmi.shared.ShareNum;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.unsigned.JavaFasta;
import org.simmi.unsigned.NativeRun;

import flobb.ChatServer;
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
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GeneSet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 * @throws IOException
	 */
	
	static SerifyApplet currentSerify = null;
	static Map<String,String> keggMap = new HashMap<>();
	static {}
	
	public static String user;

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
			if( line.startsWith("Query:") || line.startsWith("Query=") ) {
				if( hit != null && id != null ) {
					cazymap.put( id, hit );
				}
				String[] split = line.split("[\t ]+");
				
				//int k = line.lastIndexOf('[');
				id = split[1]; //line.substring( k+1, line.indexOf(']', k+1) ).trim()+"_"+split[1].trim();
				line = br.readLine();
				while( line.length() > 0 ) {
					id += line;
					line = br.readLine();
				}
				
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
			} else if( hit != null && line.contains("Expect =") ) {
				/*line = br.readLine();
				line = br.readLine().trim();
				if( line.startsWith("--") ) line = br.readLine().trim();
				String[] split = line.split("[ ]+");*/
				int i = line.indexOf("Expect =");
				evalue = line.substring(i+8,line.indexOf(',',i+8)).trim();
				
				double e = Double.parseDouble(evalue);
				if( e < 0.01 ) {
					hit += "("+evalue+")";
				} else hit = null;
			} else if( hit == null && line.startsWith(">") ) {
				hit = line.substring( 2, line.length() );
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
					id = idFix( id, lname.substring(i+1, k) );
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
				
				i = val.lastIndexOf('[');
				if( i != -1 ) {
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
					String cogan = i > ce ? val.substring(ce+1, i).trim() : "";
					
					cogidmap.put(cogid, cog);
					map.put( id, new Cog( cogid, Cog.cogchar.get(cog), cog, cogan ) );
				}
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
				
					map.put( id, val.substring(0, n+1) );
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
			String genename = gene.getGeneGroup() != null ? gene.getGeneGroup().getName() : "";
			//if( genename.contains("_") ) genename = gene.getGeneGroup().getCommonName();
			
			return genename.contains("hypothetical") ? "hth-p" : genename;
		} else if( selectedItem.equals("Species") ) {
			gene.getSpecies();
		} else if( selectedItem.equals("Ids") ) {
			return gene.id;
		} else if( selectedItem.equals("Refids") ) {
			return gene.refid;
		} else if( selectedItem.equals("Cog") ) {
			Cog cog = gene.getGeneGroup().getCog(cogmap);
			if( cog != null ) return cog.id;
		} else if( selectedItem.equals("Cazy") ) {
			String cazy = gene.getGeneGroup().getCommonCazy(cazymap);
			if( cazy != null ) return cazy;
		}
		return "";
	}
	
	public String idFix( String id, String contigstr ) {
		if( id.contains("..") ) {
			int k = id.lastIndexOf('_');
			if( k != -1 ) return contigstr + "_" + id.substring(k+1);
			else return id = contigstr + "_" + id;
		}
		return id;
	}
	
	private void loci2aaseq( List<Set<String>> lclust, Map<String,Gene> refmap, Map<String,String> designations ) {
		System.err.println( lclust.size() );
		//int count = 0;
		
		for( Set<String> clust : lclust ) {
			for( String line : clust ) {
				/*if( line.contains("scotoductus2101_scaffold00007") ) {
					count++;
				}*/
				
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
				
				if( lname.contains("RAST") ) {
					System.err.println();
				}
				
				i = lname.lastIndexOf('[');
				if( i == -1 ) {
					i = Sequence.parseSpec( lname );
					int u = lname.lastIndexOf('_');
					
					contigstr = lname.substring(0, u);
					origin = lname.substring(0, i-1);
					contloc = lname.substring(i, lname.length());
					name = lname;
					id = lname;
				} else {
					int n = lname.indexOf(']', i+1);
					/*if( n < 0 || n > lname.length() ) {
						System.err.println();
					}*/
					contigstr = lname.substring(i+1, n);
					int u = lname.indexOf(' ');
					id = lname.substring(0, u);
					
					//String spec = lname.substring(i+1, n);
					id = idFix( id, contigstr );
					
					name = lname.substring(u+1, i).trim();
					
					u = Sequence.specCheck( contigstr );
					
					if( u == -1 ) {
						u = Serifier.contigIndex( contigstr );
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
					//System.err.print( id );
					//System.err.println();
				}
				
				if( !refmap.containsKey(id) ) {
					
					Tegeval tv = new Tegeval();
					Sequence contig = null;
					if( contigmap.containsKey( contigstr ) ) {
						contig = contigmap.get( contigstr );
					}/* else {
						 contig = new Contig( contigstr );
					}*/
					
					tv.init( lname, contig, contloc, start, stop, dir );
					tv.name = line;
					//ac.setName( lname );
					//tv.setAlignedSequence( ac );
					aas.put( lname, tv );
					
					//System.err.println( "erm " + start + "   " + stop + "   " + contig.toString() );
					if( contig != null ) contig.addAnnotation( tv );
					
					String newname = (addname.length() == 0 ? name : addname.substring(1)); //name+addname
					Gene gene = new Gene( null, id, newname, origin );
					tv.designation = designations != null ? designations.get( id ) : null;
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
					
					if( id.contains("hypot") ) {
						System.err.println();
					}
					
					tv.setGene( gene );
					tv.setTegund( origin );
					
					gene.tegeval = tv;
				} else {
					Gene g = refmap.get(id);
					
					Sequence contig = null;
					if( contigmap.containsKey( contigstr ) ) {
						contig = contigmap.get( contigstr );
					} else {
						System.err.println();
						
						
						 //contig = new Contig( contigstr );
					}
					
					g.tegeval.init( lname, contig, contloc, start, stop, dir );
					if( contig != null ) contig.addAnnotation( g.tegeval );
					//g.tegeval.name = line;
					//ac.setName( lname );
					//tv.setAlignedSequence( ac );
					aas.put( lname, g.tegeval );
					g.name = name;
					g.id = id;
					
					g.tegeval.designation = designations != null ? designations.get( id ) : null;
					g.refid = newid;
					g.setIdStr( idstr );
					g.allids = new HashSet<String>();
					g.allids.add( newid );
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
								g.ecid = idstr.substring(ec+2, k-1).trim();
							}
						}
					
						int go = idstr.indexOf("GO:");
						while( go != -1 ) {
							int ngo = idstr.indexOf("GO:", go+1);
							
							if (g.funcentries == null)
								g.funcentries = new HashSet<Function>();
							
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
							g.funcentries.add( func );
							
							go = ngo;
						}
					}
				}
			}
		}
		//System.err.println( count );
		//System.err.println();
	}
	
	Set<String>	mu = new HashSet<String>();
	private void loci2aasequence(BufferedReader br, Map<String,Gene> refmap, Map<String,String> designations, String filename) throws IOException {
		/*if( filename.contains("timi") ) {
			System.err.println();
		}*/
		
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
				/*if( line.contains("Colwellia_Colwellia") ) {
					System.err.println();
				}*/
				if (tv.getSequenceLength() > 0) {
					String contigstr = null;
					String contloc = null;
					
					String origin = null;
					String id;
					String name;
					int i = lname.lastIndexOf('[');
					if( i == -1 ) {
						i = Sequence.parseSpec( lname );
						
						/*if( i == 4 ) {
							System.err.println();
						}*/
						//if( i == -1 ) i = 5;
						int u = lname.lastIndexOf('_');
						if( u != -1 ) contigstr = lname.substring(0, u);
						
						if( i != -1 ) {
							origin = lname.substring(0, i-1);
							contloc = lname.substring(i, lname.length());
						}
						name = lname;
						id = lname;
					} else {
						int n = lname.indexOf(']', i+1);
						contigstr = lname.substring(i+1, n);
						int u = lname.indexOf(' ');
						id = lname.substring(0, u);
						id = idFix( id, contigstr );
						
						name = lname.substring(u+1, i).trim();
						
						u = Sequence.specCheck( contigstr );
						
						if( u == -1 ) {
							/*u = contigstr.indexOf("contig");
							if( u == -1 ) u = contigstr.indexOf("scaffold");
							if( u == -1 ) u = contigstr.lastIndexOf('_')+1;*/
							u = Serifier.contigIndex( contigstr );
							if( u == 0 ) {
								System.err.println();
							}
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
					
					/*if( contigstr.contains("MAT4696_contig00270") ) {
						System.err.println();
					}*/
					
					if( !refmap.containsKey(id) ) {
						Sequence contig = null;
						if( contigstr != null ) {
							if( contigmap.containsKey( contigstr ) ) {
								contig = contigmap.get( contigstr );
							}/* else {
								contig = new Contig( contigstr );
							}*/
						}
						
						tv.init( lname, contig, contloc, start, stop, dir );
						tv.name = prevline.substring(1);
						//ac.setName( lname );
						//tv.setAlignedSequence( ac );
						aas.put( lname, tv );
						if( contig != null ) contig.addAnnotation( tv );
						
						String newname = (addname.length() == 0 ? name : addname.substring(1)); //name+addname
						Gene gene = new Gene( null, id, newname, origin );
						tv.designation = designations != null ? designations.get( id ) : null;
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
						
						if( id.contains("hypot") ) {
							System.err.println();
						}
						
						tv.setGene( gene );
						tv.setTegund( origin );
						
						//tv.unresolvedGap();
						
						//Teginfo ti = new Teginfo();
						//ti.add( tv );
						gene.tegeval = tv;
						
						/*if( preval != null ) {
							Sequence precontig = preval.getContshort();
							Sequence curcontig = tv.getContshort();
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
					} else {
						/*if( id.startsWith("YP") ) {
							System.err.println();
						}*/
						Gene g = refmap.get(id);
						Sequence contig = g.tegeval.getContig();
						if( contig == null ) {
							contig = contigmap.get( contigstr );
							if( contig == null ) {
								System.err.println();
							}
							g.tegeval.setContig( contig );
							if( contig != null ) contig.addAnnotation( g.tegeval );
						}
					}
				}

				tv = new Tegeval();
				String cont = line.substring(1) + "";
				int o = cont.indexOf("Colwellia_Colwellia");
				if( o != -1 ) {
					cont = cont.substring(o+10);
				}
				String[] split = cont.split("#");
				lname = split[0].trim().replace(".fna", "");
				
				/*if( !lname.contains("_") ) {
					System.err.println();
				}*/
				
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
			
			String origin = null;
			String id;
			String name;
			int i = lname.lastIndexOf('[');
			if( i == -1 ) {
				i = Sequence.parseSpec( lname );
				//i = Serifier.contigIndex( lname );
				int u = lname.lastIndexOf('_');
				if( u != -1 ) contigstr = lname.substring(0, u);
				if( i > 0 ) {
					origin = lname.substring(0, i-1);
					contloc = lname.substring(i, lname.length());
				}/* else {
					System.err.println( lname );
					System.err.println();
				}*/
				name = lname;
				id = lname;
			} else {
				int n = lname.indexOf(']', i+1);
				contigstr = lname.substring(i+1, n);
				int u = lname.indexOf(' ');
				id = lname.substring(0, u);
				id = idFix( id, contigstr );
				name = lname.substring(u+1, i).trim();
				
				u = Sequence.specCheck( contigstr );
				
				if( u == -1 ) {
					u = Serifier.contigIndex( contigstr );
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
				Sequence contig = null;
				if( contigstr != null ) {
					if( contigmap.containsKey( contigstr ) ) {
						contig = contigmap.get( contigstr );
					}/* else {
						 contig = new Contig( contigstr );
					}*/
				}
				
				tv.init( lname, contig, contloc, start, stop, dir );
				tv.name = prevline.substring(1);
				//tv.setAlignedSequence( ac );
				aas.put(lname, tv );
				
				if( contig != null ) contig.addAnnotation( tv );
				// aass.add( new Aas(name, ac, start, stop, dir) );
				
				String newname = addname.length() == 0 ? name : addname.substring(1);
				Gene gene = new Gene( null, id, newname, origin );
				tv.designation = designations != null ? designations.get( id ) : null;
				gene.refid = newid;
				gene.allids = new HashSet<String>();
				gene.allids.add( newid );
				//gene.species = new HashMap<String, Teginfo>();
				
				//if( !newid.equals(id) ) 
				//refmap.put( newid, gene );
				refmap.put(id, gene);
				
				if( id.contains("hypot") ) {
					System.err.println();
				}
				
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

		//System.err.println( refmap.size() );
		//System.err.println();
		// Arrays.sort( aas );
	}
	
	public void sortLoci() {
		for( String cstr : contigmap.keySet() ) {
			Sequence ct = contigmap.get( cstr );
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
				System.err.println("mu " + line);
				if( size > 0 ) {
					/*if( name == null ) {
						System.err.println();
					}*/
					
					Sequence contig = new Contig( name, ac );
					
					String spec = contig.getSpec();
					List<Sequence>	ctlist;
					if( speccontigMap.containsKey( spec ) ) {
						ctlist = speccontigMap.get( spec );
					} else {
						ctlist = new ArrayList<Sequence>();
						speccontigMap.put( spec, ctlist );
					}
					
					boolean cont = false;
					for( Sequence c : ctlist ) {
						if( c.getName().equals(name) ) {
							cont = true;
							break;
						}
					}
					if( !cont ) {
						contigmap.put( name, contig );
						ctlist.add( contig );
						contig.partof = ctlist;
					}
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
			Sequence contig = new Contig( name, ac );
			contigmap.put( name, contig );
			
			List<Sequence>	ctlist;
			String spec = contig.getSpec();
			
			if( speccontigMap.containsKey( spec ) ) {
				ctlist = speccontigMap.get( spec );
			} else {
				ctlist = new ArrayList<Sequence>();
				speccontigMap.put( spec, ctlist );
			}
			
			boolean cont = false;
			for( Sequence c : ctlist ) {
				if( c.getName().equals(name) ) {
					cont = true;
					break;
				}
			}
			if( !cont ) {
				contigmap.put( name, contig );
				ctlist.add( contig );
				contig.partof = ctlist;
			}
		}
		
		for( String spec : speccontigMap.keySet() ) {
			List<Sequence> ctg = speccontigMap.get(spec);
			
			//System.err.println( spec + " " + ctg.size() );
			
			if( ctg.size() < 4 && ctg.size() > 1 ) {
				Sequence chrom = null;
				for( Sequence c : ctg ) {
					if( chrom == null || c.length() > chrom.length() ) chrom = c;
				}
				for( Sequence c : ctg ) {
					if( c != chrom ) c.plasmid = true;
				}
			} else {
				for( Sequence c : ctg ) {
					c.plasmid = plasmids.contains( c.toString() );
				}
			}
		}
		
		for( String spec : speccontigMap.keySet() ) {
			List<Sequence> ctg = speccontigMap.get(spec);
			
			List<Sequence> plasmids = new ArrayList<Sequence>();
			for( Sequence c : ctg ) {
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
		List<String> specset;// = new ArrayList<String>(species1);
		if( designation != null && designation.length() > 0 ) {
			Set<String> sset = new TreeSet<String>();
			for( Gene g : genelist ) {
				if( g.tegeval.designation != null && g.tegeval.designation.equals( designation ) ) {
					sset.add( g.getSpecies() );
				}
			}
			specset = new ArrayList<String>( sset );
		} else specset = new ArrayList<String>(species1);
		
		Set<String>	d1 = new HashSet<String>();
		Set<String>	d2 = new HashSet<String>();
		
		double[] matrix = new double[ specset.size()*specset.size() ];
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
									Sequence seq1 = tv1.getAlignedSequence();
									Sequence seq2 = tv2.getAlignedSequence();
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
	
		BufferedImage bi = showRelation( ordInd, matrix, true );
		
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
	
	public BufferedImage showRelation( Collection<String> specset, double[] matrix, boolean inverted ) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		for (String spec : specset) {
			String spc = nameFix( spec );
			int tstrw = g2.getFontMetrics().stringWidth(spc);
			if (tstrw > mstrw)
				mstrw = tstrw;
		}
		
		int sss = mstrw + 72 * specset.size() + 10 + 72;
		bi = new BufferedImage(sss, sss, BufferedImage.TYPE_INT_RGB);
		g2 = (Graphics2D) bi.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.white);
		g2.fillRect(0, 0, sss, sss);
		
		int where = 0;
		for (String spec1 : specset) {
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
			for (String spec2 : specset) {
				if( where != wherex ) {
					int wx = corrInd.indexOf( spec2 );
					double ani = inverted ? 1.0-matrix[ w*specset.size()+wx ] : matrix[ w*specset.size()+wx ];
					
					//float cval = Math.min( 0.9f, Math.max( 0.0f,4.2f - (float)(4.2*ani) ) );
					float cval = Math.min( 0.9f, Math.max( 0.0f, 1.2f - (float)(1.2*ani) ) );
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
		
		return bi;
	}

	public BufferedImage bmatrix(Collection<String> species1, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, String designation) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		
		Collection<String> specset = species1;
		if( designation.length() > 0 ) {
			specset = new TreeSet<String>();
			for( Gene g : genelist ) {
				if( g.tegeval.designation != null && g.tegeval.designation.equals( designation ) ) {
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

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		Map<String,Set<Gene>>		geneMap = new HashMap<String,Set<Gene>>();
		Map<String,Set<GeneGroup>>	ggMap = new HashMap<String,Set<GeneGroup>>();
		if( designation.length() > 0 ) {
			for( Gene g : genelist ) {
				if( g.tegeval.designation != null && g.tegeval.designation.equals(designation) ) {
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
									}/* else {
										System.err.println();
									}*/
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
					
					if (prevset != null) {
						/*if( trset.isEmpty() ) {
							System.err.println();
						}*/
						prevset.addAll(trset);
					}
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
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = Serifier.initClusterNew(total, null, null);

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
		Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap = Serifier.initClusterNew(total, null, null);

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
		/*for (StrSort ss : sortmap) {
			System.err.println(ss.s);
		}
		System.err.println();
		System.err.println();*/

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
										Sequence aa = ((Tegeval)aas.get(loci)).getProteinSequence();
										gene = aa.sb.toString();
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

						Sequence aastr = ((Tegeval)aas.get(gene)).getProteinSequence();
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

	public void initWebPage(Stage stage, String webp ) {
		Scene scene = createScene( webp );
		if( stage != null ) stage.setScene(scene);
	}
    
    public void initDualPieChart( JFXPanel fxPanel, Map<Character,Integer> map, Map<Character,Integer> mip ) {
        Scene scene = createDualPieChartScene( map, mip );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
    
    public void initStackedBarChart( Stage stage, Map<String,String> all, Map<String,Map<Character,Integer>> map, boolean uniform ) {
        Scene scene = createStackedBarChartScene( all, map, uniform );
        if( stage != null ) stage.setScene(scene);
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
	
	//GeneSetHead gsh;
	ChatServer cs;
	public GeneSet() {
		super();
		//gsh = new GeneSetHead( this );
	}
	
	public static void main(String[] args) {
		if( args.length > 1 && args[0].endsWith(".zip") ) {
			GeneSet	gs = new GeneSet();
			Path p = Paths.get(args[0]);
			try {
				gs.loadStuff( p );
				if( args[1].equalsIgnoreCase("clusterGenes") ) {
					gs.clusterGenes(gs.getSpecies(), true);
				} else if( args[1].equalsIgnoreCase("cogBlast") ) {
					gs.cogBlast( null, args[2], args.length > 3 ? args[3] : "localhost", true, true );
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Serifier.main(args);
		}

		//SplashScreen.getSplashScreen().
		
		/*if( args.length == 0 ) {
			GeneSet	gs = new GeneSet();
			GeneSetHead gsh = new GeneSetHead( gs );
			/*String[] stra = {"A", "B", "C", "D"};
			corrInd = Arrays.asList( stra );
			double[] dd = { 0.0, 17.0, 21.0, 27.0, 17.0, 0.0, 12.0, 18.0, 21.0, 12.0, 0.0, 14.0, 27.0, 18.0, 14.0, 0.0 };
			TreeUtil treeutil = new TreeUtil();
			treeutil.neighborJoin( dd, 4, corrInd );*
			
			JFrame frame = new JFrame(); frame.setDefaultCloseOperation(
			JFrame.EXIT_ON_CLOSE );
			frame.setSize(800, 600); 
			
			gsh.init( frame, null, null, null, null, null, null, null );
			frame.setVisible( true );
		} else if( args[0].endsWith(".zip") ) {
			GeneSet	gs = new GeneSet();
			Path p = Paths.get(args[0]);
			try {
				gs.loadStuff( p );
				gs.clusterGenes( gs.getSpecies(), true );
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Serifier.main(args);
		}*/
		 

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
			
	//FileInputStream fi = new FileInputStream( "/data/gene_association.goa_uniprot.gz" );
	//GZIPInputStream gi = new GZIPInputStream( fi );
	//funcMappingStatic( new InputStreamReader( gi ) );
	//FileInputStream fi = new FileInputStream( "/Users/sigmar/gene_association.goa_uniprot.gz" );
	//GZIPInputStream gi = new GZIPInputStream( fi );
	//funcMappingStatic( new InputStreamReader( gi ) );
			
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
		Map<String, Gene> 	unimap = new HashMap<>();
		Map<String, String> ref2kegg = new HashMap<>();
		Map<String, String> ref2pdb = new HashMap<>();
		Map<String, String> ref2ko = new HashMap<>();
		Map<String, String> ref2cog = new HashMap<>();

		Map<String,Gene> nrefids = new HashMap<>();
		for( String key : refids.keySet() ) {
			Gene g = refids.get(key);
			nrefids.put(g.refid, g);
		}

		int i = 0;
		List<String> list = new ArrayList<>();
		boolean tone = false;
		// FileReader fr = new FileReader(idfile);
		BufferedReader br = new BufferedReader(rd);
		String line = br.readLine();
		String last = "";
		while (line != null) {
			i++;
			
			if( i%1000000 == 0 ) {
				System.err.println( "reading line " + i );
			}
			
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
						if( nrefids.containsKey(refid) ) {
							Gene gene = nrefids.get(refid);
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
					if( nrefids.containsKey(refid) ) {
						Gene gene = nrefids.get(refid);
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
		
		final Map<String,Set<String>> keggidpath = new HashMap<>();
		for( Path root : zipfilesystem.getRootDirectories() ) {
			try {
				Files.list(root).filter(t -> {
                    String filename = t.getFileName().toString();
                    boolean b = filename.length() == 4;
                    return b;
                }).forEach(pt -> {
                    if( Files.exists( pt ) ) {
                        try {
                            Files.list(pt).filter(t -> {
                                String filename = t.getFileName().toString();
                                System.err.println( "filename: " + filename + "   " + pt.getFileName() );
                                return filename.equals(pt.getFileName().toString().subSequence(0, 3)+".list");
                            }).forEach(t -> {
                                try {
                                    BufferedReader br1 = Files.newBufferedReader( t );
                                    String line1 = br1.readLine();
                                    while( line1 != null ) {
                                        String[] split = line1.split("[ \t]+");
                                        Set<String> idset;
                                        if( keggidpath.containsKey(split[1]) ) {
                                            idset = keggidpath.get(split[1]);
                                        } else {
                                            idset = new HashSet<>();
                                            keggidpath.put(split[1], idset);
                                        }
                                        idset.add( split[0].substring(5) );
                                        line1 = br1.readLine();
                                    }
                                    br1.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if ( genmap != null ) {
			for (String s : refids.keySet()) {
				Gene g = refids.get(s);
				if (g.allids != null)
					for (String id : g.allids) {
						if( ref2kegg.containsKey(id) ) {
							g.keggid = ref2kegg.get(id);
							if( keggidpath.containsKey(g.keggid) ) {
								Set<String> kstr = keggidpath.get(g.keggid);
								for( String str : kstr ) {
									if( g.keggpathway == null ) g.keggpathway = str;
									else g.keggpathway += " " + str;
								}
							}
						}

						if( ref2pdb.containsKey(id) ) {
							g.pdbid = ref2pdb.get(id);
						}
						
						if( ref2ko.containsKey(id) ) {
							String koid = ref2ko.get(id);
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
		FileWriter fw = new FileWriter("/Users/sigmar/sp2go.txt");
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
		
		fw = new FileWriter( "/Users/sigmar/smap.txt" );
		for( String id : symbolmap.keySet() ) {
			String sm = symbolmap.get( id );
			fw.write(id + "\t" + sm + "\n");
		}
		fw.close();
	}

	public void funcMappingUni(BufferedReader br, Map<String, Gene> uniids, BufferedWriter bw ) throws IOException {
		// FileReader fr = new FileReader( sp2go );
		//BufferedReader br = new BufferedReader(rd);
		
		int i = 0;
		
		System.err.println( uniids.keySet() );
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

			i++;
			
			if( i%1000000 == 0 ) System.err.println("funcmapping line "+i);
			
			line = br.readLine();
		}
		//br.close();
	}
	
	public void scrollToSelection( TableView table ) {
		table.scrollTo( table.getSelectionModel().getSelectedIndex() );
	}
	
	/*public Set<String> getSelspec( Component comp, final List<String>	specs ) {
		return getSelspec( comp, specs, null );
	}*/
	
	public int containmentCount( Set<String> set1, Set<String> set2 ) {
		int r = 0;
		for( String s1 : set1 ) {
			if( set2.contains( s1 ) ) r++;
		}
		return r;
	}

	public void cogBlast( Set<String> species, String dbPath, String hostname, boolean headless, boolean docker ) {
		try {
			StringWriter sb = new StringWriter();
			for( Gene g : genelist ) {
				if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
					if( species == null || species.contains( g.getSpecies() ) ) {
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

			Map<String,String> env = new HashMap<>();
			env.put("create", "true");
			String uristr = "jar:" + zippath.toUri();
			zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );
			Path resPath = zipfilesystem.getPath("/cog.blastout");

			NativeRun nrun = new NativeRun();
			if( docker ) SerifyApplet.deltaBlastRun(nrun, sb.getBuffer(), dbPath, resPath, "", null, true, zipfilesystem, user, hostname, headless, docker);
			else SerifyApplet.rpsBlastRun(nrun, sb.getBuffer(), dbPath, resPath, "", null, true, zipfilesystem, user, hostname, headless);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public Serifier getConcatenatedSequences( boolean proximityJoin, Map<GeneGroup,Integer> genegroups, Set<String> specset, boolean namefix ) {
		Map<String,Map<Sequence,String>>	smap = new HashMap<String,Map<Sequence,String>>();
		
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
		
		Map<String,Set<Tegeval>>	donetvs = new HashMap<>();
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
										tseq = new Sequence( namefix ? nameFix(spec) : spec, null );
										addseqs.put( tseq, loc );
										tseq.append( seq.sb.subSequence(0, seq.length()-len) );
									} else tseq = seq;
									
									//seqs.add( tseq );
									
									StringBuilder seqstr = tv.getAlignedSequence().getStringBuilder();
									if( seqstr != null && seqstr.length() > 0 ) {
										if( seqstr.length() != len ) {
											//System.err.println( "        bleh " + spec + "  " + seqstr.length() + "   " + ggroup.size() + "  " + ggroup.getCommonName() + "  " + ggroup.getIndex() );
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
								Sequence seq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
							Sequence tseq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
											tseq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
									Sequence tseq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
									Sequence seq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
							Sequence tseq = new Sequence( namefix ? nameFix(spec) : spec, null );
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
	
	boolean isthermus = true;
	String nameFix( String spec ) {
		return Sequence.nameFix( spec, isthermus );
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
								
								int k = Serifier.contigIndex(name);
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
	
	public StringWriter writeCog( Map<String,Map<Character,Integer>> map, Set<Character> includedCogs, boolean uniform ) throws IOException {
		StringWriter fw = new StringWriter();
		fw.write("[");
		fw.write( "['Species" );
		for( Character cogchar : includedCogs ) {
			String coglong = Cog.charcog.get( cogchar );
			fw.write("','"+coglong);
		}
		fw.write("']");
		
		Map<String,Integer> totmap = new HashMap<String,Integer>();
		for( String s : map.keySet() ) {
			int total = 0;
			Map<Character,Integer> cm = map.get( s );
			for( Character cogchar : includedCogs ) {
				int val = 0;
				if( cm.containsKey( cogchar ) ) {
					val = cm.get(cogchar);
				}
				total += val;
			}
			totmap.put( s, total );
		}
		
		for( String s : map.keySet() ) {
			fw.write(",\n");
			int total = totmap.get( s );
			fw.write( "['"+nameFix(s)+"'" );
			Map<Character,Integer> cm = map.get( s );
			for( Character cogchar : includedCogs ) {
				int val = 0;
				//String coglong = Cog.charcog.get(cogchar);
				//Character cogchar = Cog.cogchar.get( coglong );
				if( cm.containsKey( cogchar ) ) {
					val = cm.get(cogchar);
				}// else val = -1;
				
				if( uniform ) {
					fw.write(","+((double)val/(double)total));
				} else {
					fw.write(","+val);
				}
			}
			fw.write("]");
		}
		fw.write("]");
		
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
	
	public void assignGain( Node n, Map<Node,List<GeneGroup>> gainMap, PrintStream ps ) {
		Set<String>	specs = n.getLeaveNames();
		
		List<GeneGroup> lgg = ggSpecMap.get( specs );
		gainMap.put( n, lgg );
		
		ps.println( specs );
		if( lgg != null ) for( GeneGroup gg : lgg ) {
			/*Set<String>	nset = new HashSet<String>();
			for( Gene g : gg.genes ) {
				nset.add( g.name );
			}*/
			ps.println( "\t" + gg.getName() );
		}
		
		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes) {
				assignGain( node, gainMap, ps );
			}
		}
	}
	
	public void assignLoss( Node n, Map<Node,List<GeneGroup>> lossMap, PrintStream ps ) {
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
							ps.println( "\t" + gg.getName() );
						}
						lossMap.put(nnode, lgg);
					}
				}
				assignLoss( node, lossMap, ps );
			}
		}
	}
	
	public Teginfo getGroupTes( GeneGroup gg, String spec ) {
		Teginfo teginfo = gg.species.get( spec );
		//System.err.println( gg.species + "  " + teginfo + "  " + spec );
		return teginfo;
	}
	
	public final class StackBarData {		
		String 				name;
		String				oname;
		Map<String,Integer> b = new HashMap<String,Integer>();
	}
	
	//refmap, genelist, funclist, iclusterlist, uclusterlist, specset
	Map<String, Gene> genemap;
	List<Gene> genelist = new ArrayList<Gene>();
	List<Function> funclist = new ArrayList<Function>();
	List<Set<String>> iclusterlist;
	List<Set<String>> uclusterlist;
	Map<Set<String>, ShareNum> specset;
	
	Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap;
	Map<String,Function> funcmap = new HashMap<String,Function>();

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
				f.setName( line.substring(6) );
			} else if (line.startsWith("namespace:")) {
				f.setNamespace( line.substring(11) );
			} else if (line.startsWith("def:")) {
				f.setDesc( line.substring(5) );
			} else if (line.startsWith("xref:")) {
				if (line.contains("EC:")) {
					f.setEc( line.substring(line.indexOf("EC:") + 3) );
				} else if (line.contains("MetaCyc:")) {
					f.setMetaCyc( line.substring(line.indexOf("MetaCyc:") + 8) );
				} else if (line.contains("KEGG:")) {
					f.setKegg( line.substring(line.indexOf("KEGG:") + 5) );
				} else if (line.contains("KO:")) {
					f.setKO( line.substring(line.indexOf("KO:") + 3) );
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
	
	public void loadtransm( Set<String> tmids, BufferedReader r ) throws IOException {
		String line = r.readLine();
		String current = null;
		while( line != null ) {
			if( line.startsWith(">") ) {
				current = line.substring(1,line.indexOf(' '));
			} else if( line.startsWith("%pred NB(0): i") ) {
				tmids.add( current );
			}
			line = r.readLine();
		}
	}
	
	public void loadsignalp( Set<String> spids, BufferedReader r ) throws IOException {
		String line = r.readLine();
		while( line != null ) {
			String[] split = line.split("[\t ]+");
			if( split[split.length-3].trim().equals("Y") ) {
				spids.add( split[0] );
			}
			line = r.readLine();
		}
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
				if( i+1 < 0 || k-1 < 0 || i+1 > trim.length() || k-1 > trim.length() ) {
					
					System.err.println();
				}
				String name = trim.substring(i+1, k-1);
				String cont = trim.substring(k+1, trim.length()-1);
				String spec;
				String contshort;
				int u = Sequence.specCheck( cont );
				
				if( u == -1 ) {
					u = Serifier.contigIndex(cont);
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
					gg = new GeneGroup( groupIndex++, specset, cogmap, ko2name, biosystemsmap );
					ggmap.put( name, gg );
				}
				Gene g = new Gene( gg, name, name, spec );
				g.setIdStr( idstr );
				
				Sequence contig = contigmap.get( cont );
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
				if( end == -1 ) end = cont.indexOf("_chromosome");
				if( end == -1 ) end = cont.indexOf("_plasmid");
				if( end == -1 && (cont.charAt(0) == 'J' || cont.charAt(0) == 'A' || cont.charAt(0) == 'B' || cont.charAt(0) == 'L') && cont.charAt(4) == '0' ) end = 4;
				if( end == -1 ) {
					int k = cont.indexOf("uid");
					if( k != -1 ) {
						end = cont.indexOf('_', k);
					}
				}
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
					gg = new GeneGroup( groupIndex++, specset, cogmap, ko2name, biosystemsmap );
					ggmap.put( name, gg );
				}
				Gene g = new Gene( gg, cont+"_"+loc, name, spec );
				
				Sequence contig = contigmap.get( cont );
				/*Sequence contig = null;
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
		String cont = null;
		int start = 0;
		int laststart = -1;
		int stop = -1;
		int ori = 1;
		String name = null;
		String spec = null;
		boolean compl = false;

		if( !line.startsWith("Sequence") ) {
			final String seqstart = "sequence name=";
			final String startposstr = "start position=";
			final String endposstr = "end position=";
			final String predict = "tRNA predict as a";
			while (line != null) {
				//String[] split = line.split("[\t ]+");
				//String cont = split[0].replace(".fna", "");

				if (line.startsWith(seqstart)) {
					if (name != null) {
						GeneGroup gg;
						if (ggmap.containsKey(name)) {
							gg = ggmap.get(name);
						} else {
							gg = new GeneGroup(groupIndex++, specset, cogmap, ko2name, biosystemsmap);
							ggmap.put(name, gg);
						}
						Gene g = new Gene(gg, cont + "_" + start + "_" + stop, name, spec);

						Sequence contig = contigmap.get(cont);
						Tegeval tegeval = new Tegeval(g, spec, 0.0, null, contig, null, start, stop, ori);
						tegeval.type = "trna";
						g.setTegeval(tegeval);
						gg.addGene(g);
					}

					cont = line.substring(seqstart.length()).trim();
					compl = false;
					name = null;
					laststart = -1;

					int end = cont.indexOf("_contig");
					if (end == -1) end = cont.indexOf("_scaffold");
					if (end == -1) end = cont.indexOf("_chromosome");
					if (end == -1) end = cont.indexOf("_plasmid");
					if (end == -1) {
						System.err.println();
					}
					spec = cont.substring(0, end);
				} else if (cont != null) {
					if (line.startsWith("complementary strand")) compl = true;
					else if (line.startsWith(startposstr)) {
						if (name != null && laststart != start) {
							GeneGroup gg;
							if (ggmap.containsKey(name)) {
								gg = ggmap.get(name);
							} else {
								gg = new GeneGroup(groupIndex++, specset, cogmap, ko2name, biosystemsmap);
								ggmap.put(name, gg);
							}
							Gene g = new Gene(gg, cont + "_" + start + "_" + stop, name, spec);

							System.err.println("adding " + spec + "  " + name + "  " + start + "  " + stop);

							Sequence contig = contigmap.get(cont);
							Tegeval tegeval = new Tegeval(g, spec, 0.0, null, contig, null, start, stop, ori);
							tegeval.type = "trna";
							g.setTegeval(tegeval);
							gg.addGene(g);
						}

						int k = line.indexOf(endposstr);
						laststart = start;
						start = Integer.parseInt(line.substring(startposstr.length(), k).trim());
						stop = Integer.parseInt(line.substring(k + endposstr.length()).trim());
						ori = 1;
						if (start > stop) {
							ori = stop;
							stop = start;
							start = ori;
							ori = -1;
						}
					} else if (line.startsWith(predict)) {
						int e = line.indexOf(':', predict.length());
						name = line.substring(predict.length(), e).trim();
					}

					//int start = Integer.parseInt( split[2] );
					//int stop = Integer.parseInt( split[3] );
				}

				if( name != null ) {
					GeneGroup 	gg;
					if( ggmap.containsKey( name ) ) {
						gg = ggmap.get( name );
					} else {
						gg = new GeneGroup( groupIndex++, specset, cogmap, ko2name, biosystemsmap );
						ggmap.put( name, gg );
					}
					Gene g = new Gene( gg, cont+"_"+start+"_"+stop, name, spec );

					Sequence contig = contigmap.get( cont );
					Tegeval tegeval = new Tegeval( g, spec, 0.0, null, contig, null, start, stop, ori );
					tegeval.type = "trna";
					g.setTegeval( tegeval );
					gg.addGene( g );
				}

				line = br.readLine();
			}
		} else {
			while (line != null) {
				if( line.startsWith("Sequence") ) {
					br.readLine();
					br.readLine();
					line = br.readLine();
				}
				String[] split = line.split("[\t ]+");
				cont = split[0].replace(".fna", "");

				start = Integer.parseInt( split[2] );
				stop = Integer.parseInt( split[3] );
				name = split[4];

				int end = cont.indexOf("_contig");
				if (end == -1) end = cont.indexOf("_scaffold");
				if (end == -1) end = cont.indexOf("_chromosome");
				if (end == -1) end = cont.indexOf("_plasmid");
				if (end == -1) {
					System.err.println();
				}
				spec = cont.substring(0, end);

				GeneGroup 	gg;
				if( ggmap.containsKey( name ) ) {
					gg = ggmap.get( name );
				} else {
					gg = new GeneGroup( groupIndex++, specset, cogmap, ko2name, biosystemsmap );
					ggmap.put( name, gg );
				}
				Gene g = new Gene( gg, cont+"_"+start+"_"+stop, name, spec );

				Sequence contig = contigmap.get( cont );
				Tegeval tegeval = new Tegeval( g, spec, 0.0, null, contig, null, start, stop, ori );
				tegeval.type = "trna";
				g.setTegeval( tegeval );
				gg.addGene( g );

				line = br.readLine();
			}
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
	Map<String,String>	designations = new HashMap<String,String>();
	Set<String>			deset = new HashSet<String>();
	
	Map<String, Set<String>> 				pathwaymap = new TreeMap<>();
	Map<String, Set<String>> 				pathwaykomap = new TreeMap<>();
	List<String> 							corrInd;
	public List<GeneGroup>					allgenegroups;
	Map<Set<String>,List<GeneGroup>> 		ggSpecMap;
	Map<String,Set<GeneGroup>>				specGroupMap;
	List<String>							specList = new ArrayList<>();
	//byte[] 									zipf;
	public Map<String,Cog>					cogmap = new HashMap<>();
	Map<String,String>						cogidmap = new HashMap<>();
	Map<String,String>						unresolvedmap = new HashMap<>();
	Map<String,String>						namemap = new HashMap<>();
	Map<String,String>						cazymap = new HashMap<>();
	Map<String,Set<String>>					biosystemsmap = new HashMap<>();
	
	Map<String, Set<String>> 				ko2go = new TreeMap<>();
	
	public void loadStuff( Path zipp ) throws IOException {
		Map<String,String> env = new HashMap<String,String>();
		zippath = zipp;
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
		nf = zipfilesystem.getPath("/biosystems.txt");
		if( Files.exists( nf ) ) biosystemsmap = Files.newBufferedReader(nf).lines().map( line -> line.split("\t") ).collect(Collectors.toMap(s->s[0],s->Arrays.stream(s[1].split(",")).collect(Collectors.toSet())));

		/*zipfilesystem.close();
		path = zipfile.toPath();
		String uristr = "jar:" + path.toUri();
		zipuri = URI.create( uristr /*.replace("file://", "file:")* );
		zipfilesystem = FileSystems.newFileSystem( zipuri, env );*/
		
		nf = zipfilesystem.getPath("/allthermus.fna");
		if( Files.exists( nf ) ) {
			specList = loadcontigs( Files.newBufferedReader(nf), "" );
		}
		nf = zipfilesystem.getPath("/454LargeContigs.fna");
		if( Files.exists( nf ) ) {
			specList = loadcontigs( Files.newBufferedReader(nf), "" );
		}
		//else {
		for( Path root : zipfilesystem.getRootDirectories() ) {
			Files.list(root).filter( new Predicate<Path>() {
				@Override
				public boolean test(Path t) {
					String filename = t.getFileName().toString();
					return (filename.endsWith(".fna") || filename.endsWith(".fastg")) && !filename.equals("allthermus.fna");
				}
			}).forEach( new Consumer<Path>() {
				@Override
				public void accept(Path t) {
					try {
						String filename = t.getFileName().toString().replace(".fna", "").replace(".fastg", "");
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
					//System.err.println("filename " + filename);
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
		}// else {
		nf = zipfilesystem.getPath("/allthermus_aligned.aa");
		if( Files.exists( nf ) ) loci2aasequence( Files.newBufferedReader(nf), refmap, designations, "" );
		nf = zipfilesystem.getPath("/genes.faa");
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
					//System.err.println("filename " + filename);
					boolean b = (filename.endsWith(".aa") || filename.endsWith(".faa") || filename.endsWith(".fsa")) && !filename.contains("allthermus");
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
		//}
		//}
		
		/*int tot = 0;
		int mot = 0;
		for( String cstr : contigmap.keySet() ) {
			Sequence ctg = contigmap.get( cstr );
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
		nf = zipfilesystem.getPath("/ko2name.txt");
		if( Files.exists( nf ) ) ko2name = ko2nameMapping( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );

		
		/*for( String cstr : contigmap.keySet() ) {
			Sequence c = contigmap.get(cstr);
			if( c.annset != null ) for( Annotation a : c.annset ) {
				if( cstr.contains("00270") ) {
					System.err.println( a.name );
				}
			}
		}
		System.err.println();*/
		
		
		
		
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
		InputStream is = GeneSet.class.getResourceAsStream("/thermus16S.blastout");
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
						gid = idFix( gid, cont.substring(b+1, u) );
					}
					
					String scont = cont.substring(b+1, u);
					
					int l = Sequence.specCheck( scont );
					
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

			GeneGroup gg = new GeneGroup( i, specset, cogmap, ko2name, biosystemsmap );
			ggList.add( gg );
			//gg.addSpecies( ss );
			gg.setGroupCount( val );
			//gg.setGroupGeneCount( gs.size() );
			
			if( gset.size() == 0 ) {
				System.err.println();
			}
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
				GeneGroup gg = new GeneGroup( i++, specset, cogmap, ko2name, biosystemsmap );
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
		
		final Map<String,GeneGroup>	rnamap = new HashMap<>();
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
		else {
			for( Path root : zipfilesystem.getRootDirectories() ) {
				Files.list(root).filter( new Predicate<Path>() {
					@Override
					public boolean test(Path t) {
						String filename = t.getFileName().toString();
						//System.err.println("filename " + filename);
						boolean b = filename.endsWith(".lsu") || filename.endsWith(".ssu") || filename.endsWith(".tsu");
						return b;
					}
				}).forEach( new Consumer<Path>() {
					@Override
					public void accept(Path t) {
						if( Files.exists( t ) )
							try {
								loadRrnas( rnamap, new InputStreamReader( Files.newInputStream(t, StandardOpenOption.READ) ), 0 );
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
				});
			}
		}
		
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
				if( g.refid.contains("hypot") ) {
					System.err.println();
				}
				
				genelist.add( g );
				
				//gg.addSpecies( g.species );
			}
		}
		
		
		System.err.println("fuck2");
		for( String k : refmap.keySet() ) {
			if( !k.contains("scaffold") && !k.contains("contig") ) {
				System.err.println( k );
			}
		}
		
		
		
		/*for( String cstr : contigmap.keySet() ) {
			Sequence c = contigmap.get(cstr);
			if( c.annset != null ) for( Annotation a : c.annset ) {
				if( cstr.contains("00270") ) {
					System.err.println( a.name );
				}
			}
		}
		System.err.println();*/
		
		
		
		final Set<String>	spids = new HashSet<String>();
		for( Path root : zipfilesystem.getRootDirectories() ) {
			Files.list(root).filter( new Predicate<Path>() {
				@Override
				public boolean test(Path t) {
					String filename = t.getFileName().toString();
					//System.err.println("filename " + filename);
					boolean b = filename.endsWith(".signalp");
					return b;
				}
			}).forEach( new Consumer<Path>() {
				@Override
				public void accept(Path t) {
					if( Files.exists( t ) )
						try {
							System.err.println("loading " + t.toString() );
							loadsignalp( spids, Files.newBufferedReader(t) );
							System.err.println("spids " + spids.size() );
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			});
		}
		for( Gene g : genelist ) {
			System.err.println( g.id );
			if( spids.contains(g.id) ) {
				g.signalp = true;
			}
		}
		
		final Set<String>	tmids = new HashSet<String>();
		for( Path root : zipfilesystem.getRootDirectories() ) {
			Files.list(root).filter( new Predicate<Path>() {
				@Override
				public boolean test(Path t) {
					String filename = t.getFileName().toString();
					//System.err.println("filename " + filename);
					boolean b = filename.endsWith(".tm");
					return b;
				}
			}).forEach( new Consumer<Path>() {
				@Override
				public void accept(Path t) {
					if( Files.exists( t ) )
						try {
							loadtransm( tmids, Files.newBufferedReader(t) );
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			});
		}
		for( Gene g : genelist ) {
			if( tmids.contains(g.id) ) {
				g.transm = true;
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
			/*if( gg.genes.size() == 0 ) {
				System.err.println();
			}*/
			
			for( String spec : gg.species.keySet() ) {
				Set<GeneGroup>	ggset;
				if( !specGroupMap.containsKey( spec ) ) {
					ggset = new HashSet<GeneGroup>();
					specGroupMap.put( spec, ggset );
				} else ggset = specGroupMap.get( spec );
				ggset.add( gg );
			}
			/*if( ind == 4049 ) {
				System.err.println( gg.species.keySet() );
				System.err.println();
			}*/
			gg.setIndex( ind++ );
		}
		allgenegroups = ggList;
		
		for( Gene g : genelist ) {
			if( g.getGeneGroup() != null ) me++;
			else mu++;
		}
		System.err.println( me + " agargargargarg " + mu );
		
		
		System.err.println("fuck3");
		for( String k : refmap.keySet() ) {
			if( !k.contains("scaffold") && !k.contains("contig") ) {
				System.err.println( k );
			}
		}
		
		/*if( rnamap != null ) for( String ggname : rnamap.keySet() ) {
			GeneGroup gg = rnamap.get( ggname );
			List<Tegeval> tegevals = gg.getTegevals();
			
			/*for( Tegeval te : tegevals ) {
				Sequence contig = te.getContshort();
									
				//if( contig.getName().contains("antrani") && contig.getName().contains("contig00006") ) {
				//	System.err.println( contig.getAnnotationCount() );
				//}
				
				if( contig != null ) {
					contig.add( te );
					
					if( te.getGene().getName().contains("Met") ) {
						System.err.println( contig.getName() + "  " + te.getGene().getName() + "  " + contig.getTegevalsList().indexOf( te ) + "  " + contig.getAnnotationCount() );
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
			List<Sequence> ctgs = speccontigMap.get( spec );
			for( Sequence c : ctgs ) {
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
				Sequence contig = te.getContshort();
				if( te.getGene().getName().contains("Met") ) {
					System.err.println( contig.getName() + "  " + te.getGene().getName() + "  " + contig.getTegevalsList().indexOf( te ) + "  " + contig.getAnnotationCount() );
				}
			}*/
		}
		
		System.err.println("fuck4");
		for( String k : refmap.keySet() ) {
			if( !k.contains("scaffold") && !k.contains("contig") ) {
				System.err.println( k );
			}
		}
		
		System.err.println("starting ......................................");
		for( String str : speccontigMap.keySet() ) {
			List<Sequence> cts = speccontigMap.get(str);
			System.err.println( str + "  " + cts.size() );
		}
		
		nf = zipfilesystem.getPath("/contigorder.txt");
		//loadcazymap( cazymap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
		if( Files.exists( nf ) ) {
			is = Files.newInputStream(nf, StandardOpenOption.READ);
			BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
			String line = br.readLine();
			if( line != null ) line = br.readLine();
			while( line != null ) {
				Sequence prevctg = null;
				i = 0;
				int ni1 = line.indexOf('/', i+1);
				int ni2 = line.indexOf('\\', i+1);
				ni1 = ni1 == -1 ? line.length() : ni1; 
				ni2 = ni2 == -1 ? line.length() : ni2;
				int n = Math.min( ni1, ni2 );
				while( n != line.length() ) {
					char c = line.charAt(i);
					String ctgn = line.substring(i+1, n);
					Sequence ctg = contigmap.get( ctgn );
					
					if( ctg != null ) {
						String spec = ctg.getSpec();
						
						List<Sequence> splct = speccontigMap.get( spec );
						
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
				Sequence ctg = contigmap.get( ctgn );
				if( ctg != null ) {
					List<Sequence> splct = speccontigMap.get( ctg.getSpec() );
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
		
		System.err.println("starting ......................................");
		for( String str : speccontigMap.keySet() ) {
			List<Sequence> cts = speccontigMap.get(str);
			System.err.println( str + "  " + cts.size() );
		}
		System.err.println("ending ......................................");
		
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
		
		nf = zipfilesystem.getPath("/org/simmi/gene2refseq_short.txt");
		if( Files.exists( nf ) ) genmap = idMapping(new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ), null, 5, 1, refmap, null, gimap);
		//loadcazymap( cazymap, new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) ) );
		
		/*zipin = new ZipInputStream( new ByteArrayInputStream(zipf) );
		ze = zipin.getNextEntry();
		while( ze != null ) {
			if( ze.getName().equals("gene2refseq_short.txt") ) genmap = idMapping(new InputStreamReader(zipin), null, 5, 1, refmap, genmap, gimap);
			
			ze = zipin.getNextEntry();
		}
		zipin.close();*/
		
		//is = GeneSet.class.getResourceAsStream("/gene2refseq_short.txt");
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
		
		nf = zipfilesystem.getPath("/org/simmi/idmapping_short.dat");
		if( Files.exists( nf ) ) {
			unimap = idMapping(new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), null, 2, 0, refmap, genmap, gimap);
		}
		
		nf = zipfilesystem.getPath("/smap_short.txt");
		if( Files.exists( nf ) && !unimap.isEmpty() ) loadSmap( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), unimap );

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
			
			nf = zipfilesystem.getPath("/org/simmi/sp2go_short.txt");
			if( Files.exists( nf ) ) {
				funcMappingUni( new BufferedReader( new InputStreamReader( Files.newInputStream(nf, StandardOpenOption.READ) )), unimap, null );
			}
			
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
		is = GeneSet.class.getResourceAsStream("org/simmi/go.obo");
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
		
		System.err.println("fuck8");
		for( String k : refmap.keySet() ) {
			if( !k.contains("scaffold") && !k.contains("contig") ) {
				System.err.println( k );
			}
		}
		
		nf = zipfilesystem.getPath("/genes.faa");
		if( Files.exists( nf ) ) jgiGeneMap = jgiGeneMap( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)) );
		nf = zipfilesystem.getPath("/ko.tab.txt");
		if( Files.exists( nf ) ) jgiGene2KO( new InputStreamReader(Files.newInputStream(nf, StandardOpenOption.READ)), jgiGeneMap, refmap );
		zipfilesystem.close();
	}
	
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
	
	public void updateShareNum( Collection<String> specs ) {
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
			gg.setSpecSet( this.specset );
			
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
	
	public Tegeval check( Sequence contig ) {
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
			List<Sequence>	lct = speccontigMap.get( spec );
			for( Sequence ct : lct ) {
				bf.write( (ct.isReverse() ? "\\" : "/")+ct.getName() );
			}
			bf.write("\n");
		}
		bf.close();
		zipfilesystem.close();
		
		/*FileWriter fw = new FileWriter("/home/sigmar/sandbox/distann/src/contigorder.txt");
		fw.write("co\n");
		for( String spec : speccontigMap.keySet() ) {
			List<Sequence>	lct = speccontigMap.get( spec );
			for( Sequence ct : lct ) {
				fw.write( (ct.isReverse() ? "\\" : "/")+ct.getName() );
			}
			fw.write("\n");
		}*/
		
		//Set<Sequence>	saved = new HashSet<Sequence>();
		/*for( String cs : GeneSet.contigmap.keySet() ) {
			Sequence c = GeneSet.contigmap.get( cs );
			if( !saved.contains( c ) && (c.prev != null || c.next != null) ) {
				Sequence prev = c.isReverse() ? c.next : c.prev;
				
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
				Sequence next = c.isReverse() ? c.prev : c.next;
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
	
	public void traverseTar( TarArchiveInputStream taris, TarArchiveEntry tae, Path p ) throws IOException {
		Path newp = p.resolve(tae.getName());
		if( tae.isDirectory() ) {
			if( !Files.exists(newp) ) Files.createDirectory(newp);
			for( TarArchiveEntry taes : tae.getDirectoryEntries() ) {
				traverseTar(taris, taes, newp);
			}
		} else if( tae.isFile() ) {
			File file = tae.getFile();
			if( file != null ) {
				Path dpath = file.toPath();
				Files.copy(dpath, newp);
			} else {
				//long size = tae.getSize();
				//tae.get
				//byte[] bb = new byte[(int)size];
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int total = 0;
				int r = taris.read();
				while( r != -1 ) {
					total++;
					baos.write(r);
					r = taris.read();
				}
				byte[] bb = baos.toByteArray();
				System.err.println( total + " dd " + bb.length );
				if( !Files.exists(newp) ) Files.write(newp, bb);
				else Files.write(newp, bb, StandardOpenOption.TRUNCATE_EXISTING);
				System.err.println("file null " + newp.toString());
			}
		} else {
			System.err.println( newp.toString() );
			System.err.println( tae.getName() );
		}
	}
	
	public void clusterGenes( Collection<String> species, boolean headless ) {
		try {
			Map<String,String> env = new HashMap<String,String>();
			env.put("create", "true");
			String uristr = "jar:" + zippath.toUri();
			zipuri = URI.create( uristr );
			zipfilesystem = FileSystems.newFileSystem( zipuri, env );
			//s.makeBlastCluster(zipfilesystem.getPath("/"), p, 1);
			Path resPath = zipfilesystem.getPath("cluster.blastout");
			
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
			NativeRun nrun = new NativeRun( run );
			
			String userhome = System.getProperty("user.home");
			Path userhomepath = Paths.get( userhome );
			Path queryPath = userhomepath.resolve("tmp_cluster.fsa");//Files.createTempFile("all", ".fsa");
			BufferedWriter qbw = Files.newBufferedWriter(queryPath);
			
			Path dbPath = Files.createTempFile("db", ".fsa");
			BufferedWriter bw = Files.newBufferedWriter(dbPath);
			for( Gene g : genelist ) {
				if( g.getTag() == null || g.getTag().equalsIgnoreCase("gene") ) {
					System.err.println("asdfasdf " + g.getSpecies());
					if( g.getSpecies().contains("Eva") ) {
						System.err.println();
					}
					if( species.contains( g.getSpecies() ) ) {
						Sequence gs = g.tegeval.getProteinSequence();
						//System.err.println("stuff "+g.id + " bllbleh " + species + "  " + g.getSpecies());
						if( gs != null ) {
							gs.setName( g.id );
							gs.writeSequence(qbw);
						}
						/*qbw.append(">" + g.id + "\n");
						for (int i = 0; i < gs.length(); i += 70) {
							qbw.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
						}*/
					}
					
					Sequence gs = g.tegeval.getProteinSequence();
					if( gs != null ) {
						gs.setName( g.id );
						gs.writeSequence(bw);
					}/* else {
						System.err.println(g.getSpecies());
						System.err.println();
					}
					
					/*bw.append(">" + g.id + "\n");
					for (int i = 0; i < gs.length(); i += 70) {
						bw.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
					}*/
				}
			}
			bw.close();
			qbw.close();
			SerifyApplet.blastRun(nrun, queryPath, dbPath, resPath, "prot", "-evalue 0.00001", null, true, user, headless);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void blastAlign(Reader r, String main, String second) {
		// BufferedReader br = new BufferedReader();
	}
	
	/*static class Connection {
		Sequence	contig;
		boolean	ori;
	}*/

	public Map<String, Sequence> 		contigmap = new TreeMap<String, Sequence>();
	Map<String, List<Sequence>>		speccontigMap = new TreeMap<String, List<Sequence>>();
	//Map<Sequence, List<Tegeval>>		contigLocMap = new HashMap<Sequence, List<Tegeval>>();
	//static final List<Tegeval> 	ltv = new ArrayList<Tegeval>();
	//final List<Sequence> 			contigs = new ArrayList<Sequence>();
	
	/**
	 * Deprecated
	 * @param species
	 * @return
	 */
	public Map<String,List<Sequence>> getSpecContigMap( Collection<String> species ) {
		final Map<String,List<Sequence>>	specContMap = new HashMap<String,List<Sequence>>();
		for( String ctname : contigmap.keySet() ) {
			for( String spec : species ) {
				if( ctname.contains( spec ) ) {
					List<Sequence>	contlist;
					if( specContMap.containsKey( spec ) ) {
						contlist = specContMap.get( spec );
					} else {
						contlist = new ArrayList<Sequence>();
						specContMap.put( spec, contlist );
					}
					contlist.add( contigmap.get( ctname ) );
					break;
				}
			}
		}
		return specContMap;
	}
	
	public int getGlobalIndex( Annotation tv ) {
		int count = 0;
		List<Sequence>	ctlist = speccontigMap.get( tv.getContig().getSpec() );
		
		if( ctlist != null ) for( Sequence ct : ctlist ) {
			if( ct != tv.getContig() ) {
				count += ct.getAnnotationCount();
			} else {
				count += ct.isReverse() ? ct.getAnnotationCount() - tv.getNum() : tv.getNum();
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