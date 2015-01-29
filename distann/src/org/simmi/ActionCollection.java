package org.simmi;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.imageio.ImageIO;
import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.GeneSet.StackBarData;
import org.simmi.shared.Annotation;
import org.simmi.shared.Cog;
import org.simmi.shared.Function;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.PrincipleComponentAnalysis;
import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;
import org.simmi.unsigned.JavaFasta;

import flobb.ChatServer;

public class ActionCollection {
	public static StringBuilder panCore( GeneSet geneset, Set<String> selspec, final String[] categories, final List<StackBarData>	lsbd ) {
		Set<GeneGroup>	pan = new HashSet<GeneGroup>();
		Set<GeneGroup>	core = new HashSet<GeneGroup>();
		StringBuilder	restext = new StringBuilder();
		
		for( String spec : selspec ) {
			String newspec = geneset.nameFix( spec );
			StackBarData sbd = geneset.new StackBarData();
			int i = newspec.indexOf('_');
			if( i == -1 ) i = newspec.length();
			sbd.oname = spec;
			sbd.name = newspec;//.substring(0,i);
			/*if( spec.contains("hermus") ) sbd.name = spec.substring( 0, spec.lastIndexOf('_') );
			else {
				Matcher m = Pattern.compile("\\d").matcher(spec);
				int firstDigitLocation = m.find() ? m.start() : 0;
				if( firstDigitLocation == 0 ) sbd.name = "Thermus_" + spec;
				else sbd.name = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
			}*/
			lsbd.add( sbd );
		}
		
		/*Collections.sort( lsbd, new Comparator<StackBarData>() {
			@Override
			public int compare(StackBarData o1, StackBarData o2) {
				return o1.name.compareTo( o2.name );
			}
		});*/
		
		boolean avg = false;
		if( avg ) {
			for( int i = 0; i < lsbd.size(); i++ ) {
				for( int k = i; k < lsbd.size(); k++ ) {
					StackBarData 	sbd = lsbd.get(i);
					String spec = 	sbd.oname;
					Set<GeneGroup> 	ggset = geneset.specGroupMap.get( spec );
					
					if( ggset != null ) {
						Set<GeneGroup> 	theset = new HashSet<GeneGroup>();
						for( GeneGroup gg : ggset ) {
							for( Gene g : gg.genes ) {
								if( g.getMaxLength() >= 100 ) {
									theset.add( gg );
									break;
								}
							}
						}
					
						pan.addAll( theset );
						if( core.isEmpty() ) core.addAll( theset );
						else core.retainAll( theset );
					}
				}
			}
		} else {
			//restext.append( "['Species', 'Pan: "+pan+"', 'Core: "+core+"']" );
			for( int i = 0; i < lsbd.size(); i++ ) {
				StackBarData sbd = lsbd.get(i);
				String spec = sbd.oname;
				
				restext.append( ",\n['"+sbd.name+"', " );
				Set<GeneGroup> ggset = geneset.specGroupMap.get( spec );
				
				Set<GeneGroup> theset = new HashSet<GeneGroup>();
				for( GeneGroup gg : ggset ) {
					for( Gene g : gg.genes ) {
						if( g.getMaxLength() >= 100 ) {
							theset.add( gg );
							break;
						}
					}
				}
				
				if( ggset != null ) {
					pan.addAll( theset );
					if( core.isEmpty() ) core.addAll( theset );
					else core.retainAll( theset );
				}
				
				restext.append( core.size()+", " );
				restext.append( (pan.size()-core.size())+"]" );
				
				sbd.b.put( "Core: ", core.size() );
				sbd.b.put( "Accessory: ", pan.size()-core.size() );
			}
			restext.insert( 0, "['Species', 'Pan: "+pan.size()+"', 'Core: "+core.size()+"']" );
		}
		return restext;
	}
	
	public static String longestCommonSubstring(String str1, String str2) {
		if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0 )
			return "";
	 
		String ret = "";
		for( int i = 0; i < str1.length(); i++ ) {
			int count = 0;
			for( int y = 0; y < Math.min(str2.length(),str1.length()-i); y++ ) {
				char c1 = str1.charAt(i+y);
				char c2 = str2.charAt(y);
				
				if( c1 == c2 ) {
					count++;
				} else {
					if( count > ret.length() ) {
						ret = str1.substring(i,count+i);
					}
					count = 0;
				}
			}
			
			if( count > ret.length() ) {
				ret = str1.substring(i,count+i);
			}
		}
		
		for( int i = 0; i < str2.length(); i++ ) {
			int count = 0;
			for( int y = 0; y < Math.min(str1.length(),str2.length()-i); y++ ) {
				char c1 = str2.charAt(i+y);
				char c2 = str1.charAt(y);
				
				if( c1 == c2 ) {
					count++;
				} else {
					if( count > ret.length() ) {
						ret = str2.substring(i,count+i);
					}
					count = 0;
				}
			}
			
			if( count > ret.length() ) {
				ret = str2.substring(i,count+i);
			}
		}
		
		return ret;
		
		/*int[][] num = new int[str1.length()][str2.length()];
		String maxstr = "";
	 
		for (int i = 0; i < str1.length(); i++)
		{
			for (int j = 0; j < str2.length(); j++)
			{
				if (str1.charAt(i) != str2.charAt(j))
					num[i][j] = 0;
				else
				{
					if ((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i - 1][j - 1];
	 
					if (num[i][j] > maxstr.length()) {
						maxstr = str1.substring(i,num[i][j]-i);
					}
				}
			}
		}
		return maxstr;*/
	}
	
	public static String htmlTable( GeneSet geneset, Collection<String> selspecs, Map<String,List<Sequence>> speccontigMap, boolean withHtml ) throws IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sh = wb.createSheet("genome stats");
		
		int k = 0;
		Row rw = sh.createRow(0);
		rw.createCell(k).setCellValue("Species");
		k++;
		
		final StringWriter fw = new StringWriter();
		if( withHtml ) fw.write("<html><head></head><body>");
		fw.write("<table border=1><tr><td>Species</td>");
		for( String spec : selspecs) {
			//int i = spec.indexOf('_');
			//if( i == -1 ) i = spec.length();
			String specstr = geneset.nameFix( spec ); //spec.substring(0, i);
			fw.write( "<td colspan=2>"+specstr+"</td>" );
			
			rw.createCell(k).setCellValue(specstr);
			k+=2;
		}
		
		k=1;
		rw = sh.createRow(1);
		//rw.createCell(1).setCellValue("");
		fw.write("</tr><tr><td></td>");
		for( String spec : selspecs ) {
			fw.write( "<td>Number</td>" );
			fw.write( "<td>% of total</td>" );
			
			rw.createCell(k++).setCellValue("Number");
			rw.createCell(k++).setCellValue("% of total");
		}
		
		k=0;
		rw = sh.createRow(2);
		rw.createCell(k++).setCellValue("DNA, total number of bases");
		fw.write("</tr><tr><td>DNA, total number of bases</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			
			for( Sequence c : lcont ) {
				System.err.println( c );
			}
			
			int len = 0;
			if( lcont != null )
			for( Sequence ct : lcont ) {
				len += ct.length();
			}
			fw.write( "<td>"+len+"</td>" );
			fw.write( "<td>100%</td>" );
			
			rw.createCell(k++).setCellValue(len);
			rw.createCell(k++).setCellValue("100%");
		}
		
		k=0;
		rw = sh.createRow(3);
		rw.createCell(k++).setCellValue("DNA coding number of bases");
		fw.write("</tr><tr><td>DNA coding number of bases</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int len = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				total += ct.length();
				if( ct.getAnnotations() != null ) for( Annotation ann : ct.getAnnotations() ) {
					len += ann.getLength();
				}
			}
			fw.write( "<td>"+len+"</td>" );
			double d = (double)len/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(len);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(4);
		rw.createCell(k++).setCellValue("DNA, G+C number of bases");
		fw.write("</tr><tr><td>DNA, G+C number of bases</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int len = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				total += ct.length();
				len += ct.getGCCount();
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+len+"</td>" );
			double d = (double)len/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(len);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(5);
		rw.createCell(k++).setCellValue("DNA contigs");
		fw.write("</tr><tr><td>DNA contigs</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int size = lcont != null ? lcont.size() : 0;
			fw.write( "<td>"+size+"</td>" );
			fw.write( "<td>100%</td>" );
			
			rw.createCell(k++).setCellValue(size);
			rw.createCell(k++).setCellValue(100+"%");
		}
		
		k=0;
		rw = sh.createRow(6);
		rw.createCell(k++).setCellValue("Genes total number");
		fw.write("</tr><tr><td>Genes total number</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) total += ct.getAnnotations().size();
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+total+"</td>" );
			fw.write( "<td>100%</td>" );
			
			rw.createCell(k++).setCellValue(total);
			rw.createCell(k++).setCellValue("100%");
		}
		
		k=0;
		rw = sh.createRow(7);
		rw.createCell(k++).setCellValue("Protein coding genes");
		fw.write("</tr><tr><td>Protein coding genes</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						if( ann.type == null || ann.type.length() == 0 || ann.type.equalsIgnoreCase("gene") ) count++;
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(8);
		rw.createCell(k++).setCellValue("RNA genes");
		fw.write("</tr><tr><td>RNA genes</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						if( ann.type != null && ann.type.contains("rna") ) count++;
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(9);
		rw.createCell(k++).setCellValue("rRNA genes");
		fw.write("</tr><tr><td>rRNA genes</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						if( ann.type != null && ann.type.contains("rrna") ) count++;
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(10);
		rw.createCell(k++).setCellValue("5S rRNA");
		fw.write("</tr><tr><td>5S rRNA</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						String lowername = tv.getGene().getName().toLowerCase();
						if( ann.type != null && ann.type.contains("rrna") && (lowername.contains("5s") || lowername.contains("tsu")) ) count++;
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(11);
		rw.createCell(k++).setCellValue("16S rRNA");
		fw.write("</tr><tr><td>16S rRNA</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						boolean rrna = ann.type != null && ann.type.contains("rrna");
						String lowername = tv.getGene().getName().toLowerCase();
						boolean ssu16s = lowername.contains("16s") || lowername.contains("ssu");
						
						if( rrna /*^ ssu16s*/ ) {
							System.err.println( "16S erm: " + spec + "  " + tv.getGene().getName() + " bbo " + ssu16s );
						}
						
						if( rrna && ssu16s ) {
							//System.err.println( spec + " " + tv.getGene().getName() );
							count++;
						}
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(12);
		rw.createCell(k++).setCellValue("23S rRNA");
		fw.write("</tr><tr><td>23S rRNA</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						String lowername = tv.getGene().getName().toLowerCase();
						if( ann.type != null && ann.type.contains("rrna") && (lowername.contains("23s") || lowername.contains("lsu")) ) {
							//System.err.println( "eeeerm: "+tv.getSpecies() );
							count++;
						}
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(13);
		rw.createCell(k++).setCellValue("tRNA genes");
		fw.write("</tr><tr><td>tRNA genes</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						if( ann.type != null && ann.type.contains("trna") ) count++;
					}
					total += ct.getAnnotations().size();
				}
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(14);
		rw.createCell(k++).setCellValue("Genes in paralog clusters");
		fw.write("</tr><tr><td>Genes in paralog clusters</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						int cc = 0;
						//String spec = tv.getGene().getSpecies();
						for( Gene g : tv.getGene().getGeneGroup().genes ) {
							if( g.getSpecies().equals(spec) ) cc++;
						}
						if( cc >= 2 ) {
							count++;
						}
						//if( (tv.getGene().funcentries != null && tv.getGene().funcentries.size() > 0) || (tv.getGene().ecid != null && tv.getGene().ecid.length() > 0) ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(15);
		rw.createCell(k++).setCellValue("Protein coding genes with enzyme/function prediction");
		fw.write("</tr><tr><td>Protein coding genes with enzyme/function prediction</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						for( Gene g : tv.getGene().getGeneGroup().genes ) {
							if( (g.funcentries != null && g.funcentries.size() > 0) || (g.ecid != null && g.ecid.length() > 0) ) {
								count++;
								break;
							}
						}
						//if( (tv.getGene().funcentries != null && tv.getGene().funcentries.size() > 0) || (tv.getGene().ecid != null && tv.getGene().ecid.length() > 0) ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(16);
		rw.createCell(k++).setCellValue("Protein coding genes with function prediction");
		fw.write("</tr><tr><td>Protein coding genes with function prediction</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null && tv.getGene().getGeneGroup().getFunctions().size() > 0 ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(17);
		rw.createCell(k++).setCellValue("Protein coding genes with enzymes");
		fw.write("</tr><tr><td>Protein coding genes with enzymes</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().ecid != null && tv.getGene().ecid.length() > 0 ) count++;
						else if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null ) {
							for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
								if( f.ec != null && f.ec.length() > 0 ) {
									count++;
									break;
								}
							}
						}
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(18);
		rw.createCell(k++).setCellValue("Protein coding genes with COG function prediction");
		
		fw.write("</tr><tr><td>Protein coding genes with COG function prediction</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						Cog cog = geneset.cogmap.get( tv.getGene().id );
						if( cog != null ) {
							System.err.println( cog.id + "  " + count );
							count++;
						}
						/*if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null ) for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
							if( f.metacyc != null && f.metacyc.length() > 0 ) {
								count++;
								break;
							}
						}*/
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(19);
		rw.createCell(k++).setCellValue("Protein coding genes connected to MetaCyc pathways");
		fw.write("</tr><tr><td>Protein coding genes connected to MetaCyc pathways</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null ) for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
							if( f.metacyc != null && f.metacyc.length() > 0 ) {
								count++;
								break;
							}
						}
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(20);
		rw.createCell(k++).setCellValue("Protein coding genes connected to KEGG reactions");
		fw.write("</tr><tr><td>Protein coding genes connected to KEGG reactions</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null ) {
							for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
								boolean found = false;
								if( f.kegg != null && f.kegg.length() > 0 ) {
									count++;
									found = true;
								}
								if( !found && f.isa != null ) for( String nid : f.isa ) {
									Function nf = geneset.funcmap.get( nid );
									if( nf != null && nf.kegg != null && nf.kegg.length() > 0 ) {
										count++;
										found = true;
										break;
									}
								}
								if( found ) break;
							}
						}
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(21);
		rw.createCell(k++).setCellValue("Protein coding genes connected to KEGG pathways");
		fw.write("</tr><tr><td>Protein coding genes connected to KEGG pathways</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().genes != null ) {
							boolean found = false;
							for( Gene g : tv.getGene().getGeneGroup().genes ) {
								
								if( g.koid != null && g.koid.length() > 0 ) {
									for( String pw : geneset.pathwaykomap.keySet() ) {
										Set<String> s = geneset.pathwaykomap.get( pw );
										if( s.contains( g.koid ) ) {
											found = true;
											break;
										}
									}
								}
								
								if( !found ) {
									if( g.ecid != null && g.ecid.length() > 0 ) {
										for( String pw : geneset.pathwaymap.keySet() ) {
											Set<String> s = geneset.pathwaymap.get( pw );
											if( s.contains( g.ecid ) ) {
												found = true;
												break;
											}
										}
									}
								}
								
								if( found ) break;
								/*if( !found && f.isa != null ) for( String nid : f.isa ) {
									Function nf = funcmap.get( nid );
									if( nf != null && nf.kegg != null && nf.kegg.length() > 0 ) {
										count++;
										found = true;
										break;
									}
								}*/
							}
							
							if( !found ) for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
								/*boolean found = false;
								if( f.kegg != null && f.kegg.length() > 0 ) {
									count++;
									found = true;
								}
								if( !found && f.isa != null ) for( String nid : f.isa ) {
									Function nf = funcmap.get( nid );
									if( nf != null && nf.kegg != null && nf.kegg.length() > 0 ) {
										count++;
										found = true;
										break;
									}
								}*/
								
								if( f.ko != null && f.ko.length() > 0 ) {
									for( String pw : geneset.pathwaykomap.keySet() ) {
										Set<String> s = geneset.pathwaykomap.get( pw );
										if( s.contains( f.ko ) ) {
											found = true;
											break;
										}
									}
								}
								
								if( !found ) {
									if( f.ec != null && f.ec.length() > 0 ) {
										for( String pw : geneset.pathwaymap.keySet() ) {
											Set<String> s = geneset.pathwaymap.get( pw );
											if( s.contains( f.ec ) ) {
												found = true;
												break;
											}
										}
									}
								}										
								if( found ) break;
							}
							
							if( found ) count++;
						}
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(22);
		rw.createCell(k++).setCellValue("Protein coding genes connected to KEGG Orthology (KO)");
		fw.write("</tr><tr><td>Protein coding genes connected to KEGG Orthology (KO)</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().getFunctions() != null ) {
							if( tv.getGene().getGeneGroup() != null && tv.getGene().getGeneGroup().genes != null ) {
								boolean found = false;
								for( Gene g : tv.getGene().getGeneGroup().genes ) {
									if( g.koid != null && g.koid.length() > 0 ) {
										found = true;
										break;
									}
									
									if( g.funcentries != null && !found ) {
										for( Function f : g.funcentries ) {
											if( f.ko != null && f.ko.length() > 0 ) {
												found = true;
												break;
											}
										}
									}
									/*if( !found && f.isa != null ) for( String nid : f.isa ) {
										Function nf = funcmap.get( nid );
										if( nf != null && nf.kegg != null && nf.kegg.length() > 0 ) {
											count++;
											found = true;
											break;
										}
									}*/
									
									if( found ) break;
								}
								
								if( !found ) {
									for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {												
										if( f.ko != null && f.ko.length() > 0 ) {
											found = true;
											break;
										}
									}
								}
								
								if( !found ) {
									for( Function f : tv.getGene().getGeneGroup().getFunctions() ) {
										for( String ko : geneset.ko2go.keySet() ) {
											Set<String> gos = geneset.ko2go.get(ko);
											if( gos.contains( f.go ) ) {
												found = true;
												break;
											}
										}
									}
								}
								
								if( found ) count++;
							}
						}
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		fw.write("</tr></table>" );
		if( withHtml ) fw.write("</body></html>");
		
		k=0;
		rw = sh.createRow(23);
		rw.createCell(k++).setCellValue("Paralogous groups");
		fw.write("</tr><tr><td>Paralogous groups</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					Set<Gene> gset = new HashSet<Gene>();
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						int cc = 0;
						//String spec = tv.getGene().getSpecies();
						for( Gene g : tv.getGene().getGeneGroup().genes ) {
							if( g.getSpecies().equals(spec) ) {
								if( gset.add(g) ) cc++;
							}
						}
						if( cc >= 2 ) {
							count++;
						}
						//if( (tv.getGene().funcentries != null && tv.getGene().funcentries.size() > 0) || (tv.getGene().ecid != null && tv.getGene().ecid.length() > 0) ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(24);
		rw.createCell(k++).setCellValue("Genes with signal peptides");
		fw.write("</tr><tr><td>Genes with signal peptides</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().signalp ) {
							count++;
						}
						/*for( Gene g : tv.getGene().getGeneGroup().genes ) {
							if( g.getSpecies().equals(spec) ) {
								if( gset.add(g) ) cc++;
							}
						}*/
						//if( (tv.getGene().funcentries != null && tv.getGene().funcentries.size() > 0) || (tv.getGene().ecid != null && tv.getGene().ecid.length() > 0) ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		k=0;
		rw = sh.createRow(25);
		rw.createCell(k++).setCellValue("Genes with transmembrane helices");
		fw.write("</tr><tr><td>Genes with transmembrane helices</td>");
		for( String spec : selspecs) {
			List<Sequence> lcont = speccontigMap.get(spec);
			int count = 0;
			int total = 0;
			if( lcont != null ) for( Sequence ct : lcont ) {
				if( ct.getAnnotations() != null ) {
					for( Annotation ann : ct.getAnnotations() ) {
						Tegeval tv = (Tegeval)ann;
						if( tv.getGene().transm ) {
							count++;
						}
						/*for( Gene g : tv.getGene().getGeneGroup().genes ) {
							if( g.getSpecies().equals(spec) ) {
								if( gset.add(g) ) cc++;
							}
						}*/
						//if( (tv.getGene().funcentries != null && tv.getGene().funcentries.size() > 0) || (tv.getGene().ecid != null && tv.getGene().ecid.length() > 0) ) count++;
					}
					total += ct.getAnnotations().size();
				}
				/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
					len += tv.getLength();
				}*/
			}
			fw.write( "<td>"+count+"</td>" );
			double d = (double)count/(double)total;
			d = Math.round( d*10000.0 )/100.0;
			fw.write( "<td>"+d+"%</td>" );
			
			rw.createCell(k++).setCellValue(count);
			rw.createCell(k++).setCellValue(d+"%");
		}
		
		File f = new File("/Users/sigmar/genstat.xlsx");
		FileOutputStream fos = new FileOutputStream( f );
		wb.write( fos );
		fos.close();
		
		Desktop.getDesktop().open(f);
		
		return fw.toString();
	}
	
	public static void addAll( JMenu menu, 
			final Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap, 
			final GeneSet geneset, final Map<String,List<Sequence>> speccontigMap, 
			final JTable table, final Container comp, final ChatServer cs ) {
		//JButton matrixbutton = new JButton(matrixaction);
		
		AbstractAction codregaction = new AbstractAction("Coding regions") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CodingRegions().coderegPlot( geneset, comp );
			}
		};
		
		AbstractAction freqdistaction = new AbstractAction("Freq dist") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Genome frequency distribution");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500, 500);

				final Map<Integer,Integer>	frqmap = new TreeMap<Integer,Integer>();
				//int i = 0;
				Set<String> ss = new HashSet<String>();
				//Set<String> gs = new HashSet<String>();
				for (Set<String> cluster : geneset.uclusterlist) {
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
				
				StringBuilder restext = new StringBuilder();
				restext.append("['a', ' ']");
				for( Integer k : frqmap.keySet() ) {
					int h = frqmap.get( k );
					restext.append(",\n["+k+", "+h+"]");
				}
				
				final StringBuilder sb = new StringBuilder();
				InputStream is = GeneSet.class.getResourceAsStream("/chart.html");
				try {
					int c = is.read();
					while( c != -1 ) {
						sb.append( (char)c );
						c = is.read();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				final String smuck = sb.toString().replace("smuck", restext.toString()).replace("Pan-core genome", "Gene frequency distribution");
				System.err.println( smuck );
				//restext.append( smuck );
				
				//final String smuck = sb.toString();
				
				SwingUtilities.invokeLater(new Runnable() {
	                 @Override
	                 public void run() {
	                     geneset.initAndShowGUI( smuck );
	                 }
	            });
				
				/*JScrollPane	jsp = new JScrollPane();
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
				f.setVisible( true );*/
			}
		};
		//JButton freqdistbutton = new JButton(freqdistaction);
		
		AbstractAction gcpaction = new AbstractAction("Gene GC% histogram") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Gene GC% histogram");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500, 500);

				final String xTitle = "Gene";
				final String yTitle = "GC%";
				
				final Map<Integer,Integer>	frqmap = new TreeMap<Integer,Integer>();
				
				double min = Double.MAX_VALUE;
				double max = 0.0;
				for( GeneGroup gg : geneset.allgenegroups ) {
					double val = gg.getAvgGCPerc();
					if( val > 0.0 ) {
						if( val > max ) max = val;
						if( val < min ) min = val;
					}
				}
				
				double bil = max - min;
				for( GeneGroup gg : geneset.allgenegroups ) {
					double val = gg.getAvgGCPerc();
					
					if( val > 0.0 ) {
						int bin = (int)((val-min)*49.99/bil);
						
						if( !frqmap.containsKey( bin ) ) {
							frqmap.put( bin, 1 );
						} else {
							frqmap.put( bin, frqmap.get(bin)+1 );
						}
					}
				}
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					StringBuilder restext = new StringBuilder();
					restext.append("['a', ' ']");
					for( Integer k : frqmap.keySet() ) {
						int h = frqmap.get( k );
						restext.append(",\n["+(k*bil/50.0+min)+", "+h+"]");
					}
					
					final StringBuilder sb = new StringBuilder();
					InputStream is = GeneSet.class.getResourceAsStream("/columnchart.html");
					try {
						int c = is.read();
						while( c != -1 ) {
							sb.append( (char)c );
							c = is.read();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					final String smuck = sb.toString().replace("smuck", restext.toString());
					String b64str = Base64.getEncoder().encodeToString( smuck.getBytes() );
					
					boolean succ = true;
					try {
						window.call("string2Blob", new Object[] {b64str,"text/html"});
					} catch( Exception exc ) {
						succ = false;
						exc.printStackTrace();
					}
				
					if( succ == false ) {
						try {
							window.setMember("b64str", b64str);
							window.eval("var binary = atob(b64str)");
							window.eval("var i = binary.length");
							window.eval("var view = new Uint8Array(i)");
						    window.eval("while(i--) view[i] = binary.charCodeAt(i)");
							window.eval("var b = new Blob( [view], { \"type\" : \"text\\/html\" } );");
							window.eval("open( URL.createObjectURL(b), '_blank' )");
						} catch( Exception exc ) {
							exc.printStackTrace();
						}
					}
				} else if( Desktop.isDesktopSupported() ) {
					final String[] 	names = new String[ frqmap.size() ];
					final int[]		vals = new int[ names.length ];
					int i = 0;
					for( Integer k : frqmap.keySet() ) {
						int h = frqmap.get( k );
						names[i] = ""+(k*bil/50.0+min);
						vals[i] = h;
						
						i++;
					}
					
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							if( geneset.fxframe == null ) {
								geneset.fxframe = new JFrame("Pan-core");
								geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
								geneset.fxframe.setSize(800, 600);
								
								final JFXPanel	fxpanel = new JFXPanel();
								geneset.fxframe.add( fxpanel );
								
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                     geneset.initBarChart( fxpanel, names, vals, xTitle, yTitle, 0.6, 0.7, 0.02, "GC%" );
					                 }
					            });
							} else {
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                	 geneset.initBarChart( null, names, vals, xTitle, yTitle, 0.6, 0.7, 0.02, "GC%" );
					                 }
					            });
							}						
							geneset.fxframe.setVisible( true );
						}
					});
					/*try {
						FileWriter fw = new FileWriter("c:/smuck.html");
						fw.write( smuck );
						fw.close();
						Desktop.getDesktop().browse( new URI("file://c:/smuck.html") );
					} catch( Exception exc ) {
						exc.printStackTrace();
					}*/
				}
				/*try { 
					JSObject window = JSObject.getWindow( geneset );
					window.call("string2Blob", new Object[] {b64str,"text/html"});
				} catch( Exception exc ) {
					exc.printStackTrace();
				}*/
				
				//System.err.println( smuck );
				//restext.append( smuck );
				
				//final String smuck = sb.toString();
				
				/*SwingUtilities.invokeLater(new Runnable() {
	                 @Override
	                 public void run() {
	                     initAndShowGUI( smuck );
	                 }
	            });*/
				
				/*JScrollPane	jsp = new JScrollPane();
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
				f.setVisible( true );*/
			}
		};

		AbstractAction presabsaction = new AbstractAction("Pres-Abs tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	check = new JCheckBox("Skip core");
				JCheckBox	align = new JCheckBox("Show alignment");
				JCheckBox	output = new JCheckBox("Output fasta");
				JOptionPane.showMessageDialog( comp, new Object[] {check, align, output} );
				
				Set<String>	selspec = geneset.getSelspec( geneset, geneset.specList );
				
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
									if( specset.size() > 1 && (!check.isSelected() || geneset.containmentCount(specset, selspec) < selspec.size()) ) {
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
						if( specset.size() > 1 && (!check.isSelected() || geneset.containmentCount(specset, selspec) < selspec.size()) ) {
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
		//JButton presabsbutton = new JButton( presabsaction );
		
		
		AbstractAction	keggaction = new AbstractAction("KEGG pathway") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,String> env = new HashMap<String,String>();
				env.put("create", "true");
				
				String uristr = "jar:" + geneset.zippath.toUri();
				URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
				final List<Path>	lbi = new ArrayList<Path>();
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem( zipuri, env );
					for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
						try {
							Files.list(root).filter( new Predicate<Path>() {
								@Override
								public boolean test(Path t) {
									String filename = t.getFileName().toString();
									//System.err.println("filename " + filename);
									boolean b = filename.startsWith("t") && filename.length() == 4;
									return b;
								}
							}).forEach( new Consumer<Path>() {
								@Override
								public void accept(Path t) {
									if( Files.exists( t ) ) {
										try {
											Files.list(t).filter( new Predicate<Path>() {
												@Override
												public boolean test(Path t) {
													String filename = t.getFileName().toString();
													return filename.endsWith(".png");
												}
											}).forEach( new Consumer<Path>() {
												@Override
												public void accept(Path t) {
													lbi.add( t );
												}
											});
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							});
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					
					JTable tb = new JTable();
					JScrollPane	sc = new JScrollPane( tb );
					tb.setModel( new TableModel() {
						@Override
						public int getRowCount() {
							return lbi.size();
						}

						@Override
						public int getColumnCount() {
							return 1;
						}

						@Override
						public String getColumnName(int columnIndex) {
							return "Img";
						}

						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return BufferedImage.class;
						}

						@Override
						public boolean isCellEditable(int rowIndex, int columnIndex) {
							return false;
						}

						@Override
						public Object getValueAt(int rowIndex, int columnIndex) {
							return lbi.get(rowIndex);
						}

						@Override
						public void setValueAt(Object aValue, int rowIndex,
								int columnIndex) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void addTableModelListener(TableModelListener l) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void removeTableModelListener(TableModelListener l) {
							// TODO Auto-generated method stub
							
						}
					});
					JOptionPane.showMessageDialog(geneset, sc);
					int 	r = tb.getSelectedRow();
					final BufferedImage selimg = ImageIO.read( Files.newInputStream( lbi.get(r) ) );
					JFrame 	frame = new JFrame("KEGG");
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
					
					geneset.zipfilesystem.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		};
		
		
		AbstractAction	genomestataction = new AbstractAction("Genome statistics") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final List<String>			species = new ArrayList<String>( speccontigMap.keySet() );
				
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
				
				JOptionPane.showMessageDialog(comp, c);
				
				final List<String>	selspecs = new ArrayList<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String spec = (String)table.getValueAt(r, 0);
					selspecs.add( spec );
				}
				
				String htmlstr = null;
				try {
					htmlstr = htmlTable( geneset, selspecs, speccontigMap, true );
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					try {
						window.setMember("smuck", htmlstr);
						//window.eval("var binary = atob(b64str)");
						//window.eval("var i = binary.length");
						//window.eval("var view = new Uint8Array(i)");
					    //window.eval("while(i--) view[i] = binary.charCodeAt(i)");
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else if( Desktop.isDesktopSupported() ) {
					try {					
						FileWriter fww = new FileWriter( "/Users/sigmar/genstat.html" );
						fww.write( htmlstr );
						fww.close();
						Desktop.getDesktop().browse( new URI("file:///Users/sigmar/genstat.html") );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
				
				/*if( !succ ) {
					SwingUtilities.invokeLater(new Runnable() {
		                 @Override
		                 public void run() {
		                     initAndShowGUI( fw.toString() );
		                 }
		            });
				}*/
			}
		};
		AbstractAction	seqstat = new AbstractAction("Sequence statistics") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Workbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet("genome");
				
				List<Row>	rows = new ArrayList<Row>();
				
				int i = 0;
				Row row = sheet.createRow(0);
				Row row1 = sheet.createRow(1);
				
				Set<String> specs = geneset.getSelspec(geneset, geneset.specList);
				for( String spc : specs ) {
					Cell cell = row.createCell(i);
					cell.setCellValue( geneset.nameFix(spc) );
					cell = row.createCell(i+1);
					cell.setCellValue("length");
					cell = row.createCell(i+2);
					cell.setCellValue("genes");
					cell = row.createCell(i+3);
					cell.setCellValue("subcontigs");
					cell = row.createCell(i+4);
					cell.setCellValue("plasmid");
					cell = row.createCell(i+5);
					cell.setCellValue("GC%");
					
					int k = 0;
					List<Sequence> sctg = geneset.speccontigMap.get(spc);
					
					cell = row1.createCell(i);
					cell.setCellValue( sctg.size() );
					
					Collections.sort( sctg, new Comparator<Sequence>() {
						@Override
						public int compare(Sequence o1, Sequence o2) {
							return o2.length() - o1.length();
						}
					});
					
					for( Sequence ctg : sctg ) {
						Row nrow;
						if( k >= rows.size() ) {
							nrow = sheet.createRow(k+2);
							rows.add( nrow );
						} else {
							nrow = rows.get(k);
						}
						Cell ctname = nrow.createCell(i);
						ctname.setCellValue( ctg.getName() );
						Cell ctlen = nrow.createCell(i+1);
						ctlen.setCellValue( ctg.length() );
						Cell ctgen = nrow.createCell(i+2);
						ctgen.setCellValue( ctg.getAnnotationCount() );
						Cell ctctg = nrow.createCell(i+3);
						ctctg.setCellValue( ctg.getNumberOfSubContigs() );
						Cell ctpla = nrow.createCell(i+4);
						ctpla.setCellValue( ctg.isPlasmid() );
						Cell ctgcp = nrow.createCell(i+5);
						ctgcp.setCellValue( ctg.getGCP() );
						k++;
					}
					
					i+=6;
				}
				
				File f = new File("/Users/sigmar/wb.xlsx");
				try {
					FileOutputStream fos = new FileOutputStream(f);
					workbook.write( fos );
					fos.close();
					Desktop.getDesktop().open(f);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		AbstractAction	crispraction = new AbstractAction("CRISPR-table") {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = geneset.table.getSelectedRows();
				if( rr.length > 0 ) {
					Set<String> includeSpecs = geneset.getSelspec(geneset, geneset.specList, null);
					
					Workbook wb = new XSSFWorkbook();
					for( int r : rr ) {
						int i = geneset.table.convertRowIndexToModel(r);
						GeneGroup gg = geneset.allgenegroups.get(i);
						
						Sheet sheet = wb.createSheet( gg.getCommonName() );
						
						Map<String,String> sspec = new TreeMap<String,String>();
						for( String spec : gg.getSpecies() ) {
							if( includeSpecs.contains(spec) ) sspec.put( Sequence.nameFix(spec, true), spec );
						}
						
						Row row = sheet.createRow(0);
						Cell cell = row.createCell( 0 );
						cell.setCellValue( "Strain" );
						cell = row.createCell( 1 );
						cell.setCellValue( "Location" );
						cell = row.createCell( 2 );
						cell.setCellValue( "Upstream repeats" );
						cell = row.createCell( 3 );
						cell.setCellValue( "Repeat sequence" );
						cell = row.createCell( 4 );
						cell.setCellValue( "Phage hits" );
						cell = row.createCell( 5 );
						cell.setCellValue( "Phage hits - not identical" );
						cell = row.createCell( 6 );
						cell.setCellValue( "Downstream repeats" );
						cell = row.createCell( 7 );
						cell.setCellValue( "Repeat sequence" );
						cell = row.createCell( 8 );
						cell.setCellValue( "Phage hits" );
						cell = row.createCell( 9 );
						cell.setCellValue( "Phage hits - not identical" );
						int k = 1;
						
						Map<String,List<String>>	specudSpacers = new LinkedHashMap<String,List<String>>();
						
						for( String spec : sspec.keySet() ) {
							String sp = sspec.get(spec);
							row = sheet.createRow(k++);
							cell = row.createCell( 0 );
							cell.setCellValue( spec );
							cell = row.createCell( 1 );
							
							String loc = "";
							
							Tegeval best = null;
							List<Tegeval> ltv = gg.getTegevals(sp);
							for( Tegeval tv : ltv ) {
								if( tv.seq.isPlasmid() ) {
									loc += "plasmid";
								} else {
									loc += "chromosome";
								}
								best = tv;
							}
							cell.setCellValue( loc );
							
							String lcs = "";
							String plcs = "";
							int count = 0;
							int pcount = 0;
							
							Map<String,Integer> uphage = new HashMap<String,Integer>();
							Map<String,Integer> dphage = new HashMap<String,Integer>();
							
							Map<String,Integer> uphage_n = new HashMap<String,Integer>();
							Map<String,Integer> dphage_n = new HashMap<String,Integer>();
							
							List<String>	upSpacers = new ArrayList<String>();
							String spacers = "";
							int u = 0;
							Annotation before = best;
							Annotation next = best.getNext();
							while( next != null ) {
								if( next.type != null && next.type.contains("mummer") ) {
									count++;
									
									if( before.type != null && before.type.contains("mummer") ) {
										/*if( before.stop < -10 || next.start < -10 || before.stop > next.start) {
											System.err.println( before.start + "  " + before.stop + "  " + next.start + "  " + next.stop );
										}
										int up = Math.min(before.stop,next.start)+1;
										int en = Math.max(before.stop,next.start)-1;*/
										
										int up;
										int en;
										if( next.start > before.start ) {
											up = before.stop+1;
											en = next.start-1;
										} else {
											up = next.stop+1;
											en = before.start-1;
										}
										
										String spacer = best.seq.getSubstring(up, en, 1);
										if( spacer.length() > 0 ) {
											upSpacers.add( spacer );
											spacers += ">"+u+"\n"+spacer+"\n";
										}
									}
									
									String newstr = next.getName();
									int o = newstr.indexOf('-');
									if( o != -1 ) {
										newstr = newstr.substring(o+1);
									}
									if( lcs.length() > 0 ) {
										lcs = longestCommonSubstring(lcs,newstr.toUpperCase());
									} else {
										lcs = newstr.toUpperCase();
									}
								} else if( before.type != null && before.type.contains("mummer") ) {
									break;
								} else if( u > 100 ) {
									break;
								}
								
								before = next;
								next = next.getNext();
								
								u++;
							}
							
							//spacers = ">1\nACGTCGCTAGCTCGATCGCT\n";
							if( spacers.length() > 0 ) {
								ProcessBuilder pb = new ProcessBuilder("blastn","-db","phage_radir.fna","-word_size","20","-num_threads","4");//,"-num_alignments","1","-num_descriptions","1");
								pb.directory( new File( System.getProperty("user.home") ) );
								try {
									final Process p = pb.start();
									final byte[] sbytes = spacers.getBytes();
									Thread t = new Thread() {
										public void run() {
											try {
												OutputStream os = p.getOutputStream();
												os.write( sbytes );
												os.close();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									};
									t.start();
									
									InputStream is = p.getInputStream();
									BufferedReader br = new BufferedReader( new InputStreamReader(is) );
									String line = br.readLine();
									
									Set<String>	allready = new HashSet<String>();
									while( line != null ) {
										if( line.startsWith("Query=") ) {
											allready.clear();
										} else if( line.startsWith(">") ) {
											String trim = line.substring(1).trim();
											String phage = trim.substring(0,trim.indexOf('-') );
											
											if( !allready.contains( phage ) ) {
												line = br.readLine();
												while( !line.contains("Gaps") ) {
													line = br.readLine();
												}
												int ik = line.indexOf("Gaps");
												if( line.substring( ik+7, line.indexOf('/',ik+7) ).trim().equals("0") ) {
													if( !uphage.containsKey(phage) ) {
														uphage.put(phage, 1);
													} else {
														uphage.put(phage, uphage.get(phage)+1);
													}
												} else {
													if( !uphage_n.containsKey(phage) ) {
														uphage_n.put(phage, 1);
													} else {
														uphage_n.put(phage, uphage_n.get(phage)+1);
													}
												}
												allready.add( phage );
											}
										}
										line = br.readLine();
									}
									is.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							
							List<String>	downSpacers = new ArrayList<String>();
							spacers = "";
							u = 0;
							before = best;
							Annotation prev = best.getPrevious();
							while( prev != null ) {
								if( prev.type != null && prev.type.contains("mummer") ) {
									pcount++;
									
									if( before.type != null && before.type.contains("mummer") ) {
										int up;
										int en;
										if( prev.start > before.start ) {
											up = before.stop+1;
											en = prev.start-1;
										} else {
											up = prev.stop+1;
											en = before.start-1;
										}
										
										String spacer = best.seq.getSubstring(up, en, 1);
										if( spacer.length() > 0 ) {
											downSpacers.add( spacer );
											spacers += ">"+u+"\n"+spacer+"\n";
										}
									}
									
									String newstr = prev.getName();
									int o = newstr.indexOf('-');
									if( o != -1 ) {
										newstr = newstr.substring(o+1);
									}
									if( plcs.length() > 0 ) {
										plcs = longestCommonSubstring(plcs,newstr.toUpperCase());
									} else {
										plcs = newstr.toUpperCase();
									}
								} else if( before.type != null && before.type.contains("mummer") ) {
									break;
								} else if( u > 100 ) {
									break;
								}
								
								before = prev;
								prev = prev.getPrevious();
								
								u++;
							}
							
							if( spacers.length() > 0 ) {
								ProcessBuilder pb = new ProcessBuilder("blastn","-db","phage_radir.fna","-word_size","20","-num_threads","4","-num_alignments","1","-num_descriptions","1");
								pb.directory( new File( System.getProperty("user.home") ) );
								try {
									final Process p = pb.start();
									final byte[] sbytes = spacers.getBytes();
									Thread t = new Thread() {
										public void run() {
											try {
												OutputStream os = p.getOutputStream();
												os.write( sbytes );
												os.close();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									};
									t.start();
									
									InputStream is = p.getInputStream();
									BufferedReader br = new BufferedReader( new InputStreamReader(is) );
									String line = br.readLine();
									while( line != null ) {
										if( line.startsWith(">") ) {
											String trim = line.substring(1).trim();
											String phage = trim.substring(0,trim.indexOf('-') );
											
											line = br.readLine();
											while( !line.contains("Gaps") ) {
												line = br.readLine();
											}
											int ik = line.indexOf("Gaps");
											if( line.substring( ik+7, line.indexOf('/',ik+7) ).trim().equals("0") ) {
												if( !dphage.containsKey(phage) ) {
													dphage.put(phage, 1);
												} else {
													dphage.put(phage, dphage.get(phage)+1);
												}
											} else {
												if( !dphage_n.containsKey(phage) ) {
													dphage_n.put(phage, 1);
												} else {
													dphage_n.put(phage, dphage_n.get(phage)+1);
												}
											}
										}
										line = br.readLine();
									}
									is.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							
							cell = row.createCell( 2 );
							cell.setCellValue( count );
							cell = row.createCell( 3 );
							cell.setCellValue( lcs );
							cell = row.createCell( 4 );
							cell.setCellValue( uphage.toString() );
							cell = row.createCell( 5 );
							cell.setCellValue( uphage_n.toString() );
							
							
							cell = row.createCell( 6 );
							cell.setCellValue( pcount );
							cell = row.createCell( 7 );
							cell.setCellValue( plcs );
							cell = row.createCell( 8 );
							cell.setCellValue( dphage.toString() );
							cell = row.createCell( 9 );
							cell.setCellValue( dphage_n.toString() );
							
							if( upSpacers.size() > 0 ) specudSpacers.put(spec+"up", upSpacers);
							if( downSpacers.size() > 0 ) specudSpacers.put(spec+"down", downSpacers);
						}
						
						row = sheet.createRow(k);
						row.createCell(0).setCellValue("Spacers");
						row = sheet.createRow(++k);
						int u = 0;
						for( String spec : specudSpacers.keySet() ) {
							Cell cl = row.createCell(u++);
							cl.setCellValue(spec);
						}
						
						int l = 0;
						boolean empty = false;
						while( !empty ) {
							row = sheet.createRow(++k);
							empty = true;
							u = 0;
							for( String spec : specudSpacers.keySet() ) {
								List<String> ss = specudSpacers.get(spec);
								if( l < ss.size() ) {
									empty = false;
									Cell cl = row.createCell(u);
									String spcr = ss.get(l).toUpperCase();
									cl.setCellValue( spcr.substring(0, Math.min(50,spcr.length())) );
								}
								u++;
							}
							l++;
						}
					}
					
					File file = new File("/Users/sigmar/crispr.xlsx");
					try {
						FileOutputStream fos = new FileOutputStream(file);
						wb.write( fos );
						fos.close();
						Desktop.getDesktop().open(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		};
		AbstractAction	shuffletreeaction = new AbstractAction("Recomb tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>		selspec = geneset.getSelspec( geneset, new ArrayList<String>( geneset.specList ) );
				List<String>	speclist = new ArrayList<String>( selspec );
				double[] 		mat = new double[selspec.size()*selspec.size()];
				for( int y = 0; y < speclist.size(); y++ ) {
					mat[ y*speclist.size()+y ] = 0.0;
				}
				
				for( int y = 0; y < speclist.size(); y++ ) {
					String spec1 = speclist.get(y);
					List<Sequence> lcont1 = speccontigMap.get( spec1 );
					
					for( int x = y+1; x < speclist.size(); x++ ) {
						String spec2 = speclist.get(x);
					
						int count = 0;
						
						for( Sequence c : lcont1 ) {
							List<Annotation> ltv = c.getAnnotations();
							if( ltv != null ) {
								Tegeval prev = null;
								for( Annotation ann : ltv ) {
									Tegeval tv = (Tegeval)ann;
									if( prev != null ) {
										GeneGroup gg = tv.getGene().getGeneGroup();
										GeneGroup pg = prev.getGene().getGeneGroup();
										
										List<Tegeval> ltv2 = gg.getTegevals( spec2 );
										boolean bp = true;
										for( Tegeval tv2 : ltv2 ) {
											Annotation anext = tv2.getNext();
											Annotation aprev = tv2.getPrevious();
											GeneGroup fwgg = anext != null && anext instanceof Tegeval ? ((Tegeval)anext).getGene().getGeneGroup() : null;
											GeneGroup bkgg = aprev != null && aprev instanceof Tegeval ? ((Tegeval)aprev).getGene().getGeneGroup() : null;
											if( pg.equals( fwgg ) || pg.equals( bkgg ) ) {
												bp = false;
												break;
											};
										}
										if( bp ) count++;
									}
									prev = tv;
								}
							}
						}
						
						mat[ y*speclist.size() + x ] = count;
						mat[ x*speclist.size() + y ] = count;
					}
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append( "\t"+speclist.size() );
				for( int i = 0; i < mat.length; i++ ) {
					if( i % speclist.size() == 0 ) sb.append( "\n"+speclist.get(i/speclist.size())+"\t"+mat[i] );
					else sb.append( "\t"+mat[i] );
				}
				sb.append( "\n" );
				
				String 				tree = sb.toString();
				
				boolean succ = true;
				try {
					JSObject win = JSObject.getWindow( (Applet)comp );
					win.call("showTree", new Object[] { tree });
				} catch( Exception e1 ) {
					e1.printStackTrace();
					succ = false;
				}
				
				if( !succ ) {
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
						System.err.println( tree );
			    	}
				}
				
				/*for( int y = 0; y < speclist.size(); y++ ) {
					String spec1 = speclist.get(y);
					final List<Tegeval> ltv = new ArrayList<Tegeval>();
					for (Gene g : genelist) {
						//Tegeval tv = g.tegeval;
						if( g.species.equals(spec1) ) {
							ltv.add(g.tegeval);
						}
								//tv.

								//int first = tv.cont.indexOf("_");
								//int sec = tv.cont.indexOf("_", first + 1);

								//String cname = tv.cont.substring(0, sec);
								//contigmap.put(cname, new Sequence(cname));
					}
					Tegeval.locsort = true;
					Collections.sort(ltv);
					
					for( int x = y+1; x < speclist.size(); x++ ) {
						String spec2 = speclist.get(x);
						
						final List<Tegeval> subltv = new ArrayList<Tegeval>();
						for (Gene g : genelist) {
							Tegeval tv = g.tegeval;
							if(g.species.equals(spec2)) {
								subltv.add(tv);
							}
									//tv.

									//int first = tv.cont.indexOf("_");
									//int sec = tv.cont.indexOf("_", first + 1);

									//String cname = tv.cont.substring(0, sec);
									//contigmap.put(cname, new Sequence(cname));
						}
						Tegeval.locsort = true;
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
							}*
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
				
				/*Set<String>	selspec = geneset.getSelspec( applet, new ArrayList( species ) );
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
		//JButton	shuffletreebutton = new JButton( shuffletreeaction );
		
		AbstractAction cazyexportaction = new AbstractAction("Export cazy ids") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> cz = new TreeSet<String>();
				for( Gene g : geneset.genelist ) {
					String cazy = geneset.cazymap.get( g.refid );
					if( cazy != null ) {
						int i = cazy.indexOf('(');
						if( i == -1 ) i = cazy.length();
						cazy = cazy.substring(0,i);
						cz.add( cazy );
					}
				}
				
				for( String cazy : cz ) {
					System.err.println( cazy );
				}
			}
		};
		
		AbstractAction koexportaction = new AbstractAction("Export pathway ids") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	kobtn = new JCheckBox("KO");
				kobtn.setSelected( true );
				JCheckBox	ecbtn = new JCheckBox("EC");
				ecbtn.setSelected( true );
				JCheckBox	cogbtn = new JCheckBox("COG");
				cogbtn.setSelected( true );
				JCheckBox	gibtn = new JCheckBox("GI");
				gibtn.setSelected( true );
				JTextField	tf = new JTextField("#0000ff");
				
				JTextArea	conflict = new JTextArea();
				JScrollPane	scroll = new JScrollPane( conflict );
				
				Object[] objs = new Object[] { kobtn, ecbtn, cogbtn, gibtn, tf, scroll };
				JOptionPane.showMessageDialog( geneset, objs, "Select id types", JOptionPane.PLAIN_MESSAGE );
				
				Set<String> ids = new HashSet<String>();
				int[] rr = geneset.table.getSelectedRows();
				for( int r : rr ) {
					if( kobtn.isSelected() ) {
						String ko = (String)geneset.table.getValueAt(r, 6);
						if( ko != null ) ids.add( ko );
					}
					
					if( ecbtn.isSelected() ) {
						String ec = (String)geneset.table.getValueAt(r, 10);
						if( ec != null ) ids.add( "E"+ec.replace(":", "") );
					}
					
					if( cogbtn.isSelected() ) {
						String cog = (String)geneset.table.getValueAt(r, 11);
						if( cog != null ) ids.add( cog.substring( cog.lastIndexOf(' ')+1 ) );
					}
					
					if( gibtn.isSelected() ) {
						int i = geneset.table.convertRowIndexToModel(r);
						if( i != -1 ) {
							GeneGroup gg = geneset.allgenegroups.get(i);
							for( Gene g : gg.genes ) {
								if( g.genid != null ) {
									System.err.println( g.genid );
									ids.add( g.genid );
								}
							}
							/*for( Function f : gg.getFunctions() ) {
								if( f.ec != null && f.ec.length() > 1 ) ids.add( f.gi );
							}*/
						}
					}
					
					if( ecbtn.isSelected() ) {
						int i = geneset.table.convertRowIndexToModel(r);
						if( i != -1 ) {
							GeneGroup gg = geneset.allgenegroups.get(i);
							for( Function f : gg.getFunctions() ) {
								if( f.ec != null && f.ec.length() > 1 ) ids.add( "E"+f.ec );
							}
						}
					}
				}
				
				Set<String>	conflicting = new HashSet<String>();
				String text = conflict.getText();
				String[] lines = text.split("\n");
				for( String line : lines ) {
					String[] split = line.split("[\t ]+");
					if( split.length > 1 ) {
						conflicting.add( split[0] );
						conflicting.add( split[1] );
					}
				}
				ids.removeAll( conflicting );
				
				String colorstr = tf.getText();				
				StringWriter tmp = new StringWriter();
				for( String id : ids ) {
					tmp.write( id + " " + colorstr + "\n" );
				}
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					try {
						window.setMember("smuck", tmp.toString());
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/plain\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else {
					try {
						FileWriter tmpf = new FileWriter("/Users/sigmar/kolist.txt");
						if( colorstr != null && colorstr.length() > 0 ) {
							for( String id : ids ) {
								tmpf.write( id + " " + colorstr + "\n" );
							}
						} else {
							for( String id : ids ) {
								tmpf.write( id + "\n" );
							}
						}
						tmpf.close();
						
						Desktop.getDesktop().browse( new URI("file:///Users/sigmar/kolist.txt") );
					} catch( Exception e1 ) {
						e1.printStackTrace();
					}
				}
			}
		};
		
		/*AbstractAction bsexportaction = new AbstractAction("Export BioSystem ids") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				Set<String> ids = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					if( i != -1 ) {
						GeneGroup gg = allgenegroups.get(i);
						for( Function f : gg.getFunctions() ) {
							if( f.ec != null && f.ec.length() > 1 ) ids.add( "E"+f.ec );
						}
					}
				}
				
				String colorstr = tf.getText();				
				StringWriter tmp = new StringWriter();
				for( String id : ids ) {
					tmp.write( id + " " + colorstr + "\n" );
				}
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					try {
						window.setMember("smuck", tmp.toString());
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/plain\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else {
					try {
						FileWriter tmpf = new FileWriter("c:/kolist.txt");
						for( String id : ids ) {
							tmpf.write( id + " " + colorstr + "\n" );
						}
						tmpf.close();
						
						Desktop.getDesktop().browse( new URI("file://c:/kolist.txt") );
					} catch( Exception e1 ) {
						e1.printStackTrace();
					}
				}
			}
		};*/
		
		AbstractAction blastaction = new AbstractAction("Blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				AccessController.doPrivileged( new PrivilegedAction<String>() {
					@Override
					public String run() {
						//NativeRun nrun = new NativeRun();
						
						final Object[] cont = new Object[3];
						Runnable run = new Runnable() {
							@Override
							public void run() {
								
							}
						};
						
						File makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\makeblastdb.exe" );
						if( !makeblastdb.exists() ) makeblastdb = new File( "/opt/ncbi-blast-2.2.28+/bin/makeblastdb" );
						if( makeblastdb.exists() ) {
							/*String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", nrun.fixPath( "/tmp/thermus.fasta" ), "-title", "thermus", "-dbtype", "prot", "-out", "/tmp/thermus" };
							try {
								nrun.runProcessBuilder( "Creating database", Arrays.asList( cmds ), run, cont );
							} catch (IOException e) {
								e.printStackTrace();
							}*/
						}
						
						return "";
					}
				});
			}
		};
		
		AbstractAction pancoreaction = new AbstractAction("Pan-core") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ) );
				
				for( String spec : selspec ) {
					System.err.println( spec );
				}
				
				
				final String[] categories = { "Core: ", "Accessory: " };
				final List<StackBarData> lsbd = new ArrayList<StackBarData>();
				StringBuilder restext = panCore( geneset, selspec, categories, lsbd );
				
				JFrame f = new JFrame("Pan-core chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				final StringBuilder sb = new StringBuilder();
				InputStream is = GeneSet.class.getResourceAsStream("/chart.html");
				try {
					int c = is.read();
					while( c != -1 ) {
						sb.append( (char)c );
						c = is.read();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				final String smuck = sb.toString().replace("smuck", restext.toString());
				
				//String b64str = Base64.encodeBase64String( smuck.getBytes() );
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					/*boolean succ = true;
					try {
						window.call("string2Blob", new Object[] {b64str,"text/html"});
					} catch( Exception exc ) {
						succ = false;
						exc.printStackTrace();
					}
				
					if( succ == false ) {*/
					try {
						window.setMember("smuck", smuck);
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else if( Desktop.isDesktopSupported() ) {
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							if( geneset.fxframe == null ) {
								geneset.fxframe = new JFrame("Pan-core");
								geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
								geneset.fxframe.setSize(800, 600);
								
								final JFXPanel	fxpanel = new JFXPanel();
								geneset.fxframe.add( fxpanel );
								
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                     geneset.initStackedBarChart( fxpanel, lsbd, categories );
					                 }
					            });
							} else {
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                     geneset.initStackedBarChart( null, lsbd, categories );
					                 }
					            });
							}						
							geneset.fxframe.setVisible( true );
						}
					});
					/*try {
						FileWriter fw = new FileWriter("c:/smuck.html");
						fw.write( smuck );
						fw.close();
						Desktop.getDesktop().browse( new URI("file://c:/smuck.html") );
					} catch( Exception exc ) {
						exc.printStackTrace();
					}*/
				}
				//}
				
				restext.append( smuck );
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
				
				/*SwingUtilities.invokeLater(new Runnable() {
	                 @Override
	                 public void run() {
	                     initAndShowGUI( smuck );
	                 }
	            });*/
				/*JFXPanel	jfxpanel = new JFXPanel();
				jfxpanel.add
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.add( jfxpanel );
				frame.setVisible( true );*/
			}
		};
		
		AbstractAction genomesizeaction = new AbstractAction("Genome size") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox	contigs = new JCheckBox("Show contigs");
				Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ), contigs );
				StringBuilder	restext = new StringBuilder();
				
				Map<String,Integer>	map = new TreeMap<String,Integer>();
				
				int tmax = 0;
				restext.append( "['Species', 'Size']" );
				for( String spec : selspec ) {
					restext.append( ",\n['"+spec+"', " );
					
					//int len = 0;
					int total = 0;
					
					if( contigs.isSelected() ) {
						Sequence ct = geneset.contigmap.get( spec );
						total = ct.length();
						//len = ct.getGCCount();
					} else {
						List<Sequence> lcont = speccontigMap.get(spec);
						for( Sequence ct : lcont ) {
							total += ct.length();
							//len += ct.getGCCount();
						}
					}
					
					tmax = Math.max( tmax, total );
					//double d = (double)len/(double)total;					
					String name = null;//names[i];
					if( contigs.isSelected() ) {
						if( spec.contains("hermus") ) name = spec;
						else {
							Matcher m = Pattern.compile("\\d").matcher(spec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) name = "Thermus_" + spec;
							else name = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
						}
					} else {
						if( spec.contains("hermus") ) name = spec.substring( 0, spec.lastIndexOf('_') );
						else {
							Matcher m = Pattern.compile("\\d").matcher(spec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) name = "Thermus_" + spec;
							else name = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
						}
					}
					
					map.put( name, total );
					restext.append( total+"]" );
				}
				
				final int max = tmax;
				final String[] names = new String[ map.size() ];
				final double[] vals = new double[ map.size() ];
				
				String scaffspec = null;
				int i = 0;
				for( String spec : map.keySet() ) {
					if( contigs.isSelected() ) {
						int k = spec.indexOf("contig");
						if( k == -1 ) k = spec.indexOf("scaffold");
						if( k == -1 ) k = spec.lastIndexOf('_');
						if( k == -1 ) {
							names[i] = spec;
							scaffspec = spec;
						} else {
							names[i] = spec.substring(k);						
							scaffspec = spec.substring(0, k-1);
						}
					} else names[i] = spec;
					vals[i] = map.get( spec );
					i++;
				}
				
				final String xTitle = scaffspec != null ? "Scaffolds/Contigs" : "Species";
				final String yTitle = scaffspec != null ? scaffspec + " contig size" : "Genome size";
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					final StringBuilder sb = new StringBuilder();
					InputStream is = GeneSet.class.getResourceAsStream("/columnchart.html");
					try {
						int c = is.read();
						while( c != -1 ) {
							sb.append( (char)c );
							c = is.read();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					final String smuck = sb.toString().replace("smuck", restext.toString());
					//String b64str = Base64.encodeBase64String( smuck.getBytes() );
					try {
						window.setMember("smuck", smuck);
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else if( Desktop.isDesktopSupported() ) {
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							if( geneset.fxframe == null ) {
								geneset.fxframe = new JFrame("Pan-core");
								geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
								geneset.fxframe.setSize(800, 600);
								
								final JFXPanel	fxpanel = new JFXPanel();
								geneset.fxframe.add( fxpanel );
								
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                	 geneset.initBarChart( fxpanel, names, vals, xTitle, yTitle, 0, max, 10000, contigs.isSelected() ? "Sequence sizes" : "Genome sizes" );
					                 }
					            });
							} else {
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                	 geneset.initBarChart( null, names, vals, xTitle, yTitle, 0, max, 10000, contigs.isSelected() ? "Sequence sizes" : "Genome sizes" );
					                 }
					            });
							}						
							geneset.fxframe.setVisible( true );
						}
					});
				}
				
				JFrame f = new JFrame("GC% chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
				
				
				
				/*Set<String>	selspec = geneset.getSelspec( applet, new ArrayList( specList ) );
				
				StringBuilder	restext = new StringBuilder();
				restext.append( "['Species', 'Size']" );
				for( String spec : selspec ) {
					restext.append( ",\n['"+spec+"', " );
					
					List<Sequence> lcont = speccontigMap.get(spec);
					int total = 0;
					for( Sequence ct : lcont ) {
						if( ct.getAnnotations() != null ) total += ct.getAnnotations().size();
						/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
							len += tv.getLength();
						}*
					}
					//Set<GeneGroup> ggset = specGroupMap.get( spec );
					//pan.addAll( ggset );
					//if( core.isEmpty() ) core.addAll( ggset );
					//else core.retainAll( ggset );
					
					//restext.append( core.size()+", " );
					restext.append( total+"]" );
				}
				
				final StringBuilder sb = new StringBuilder();
				InputStream is = GeneSet.class.getResourceAsStream("/genomesizechart.html");
				try {
					int c = is.read();
					while( c != -1 ) {
						sb.append( (char)c );
						c = is.read();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				final String smuck = sb.toString().replace("smuck", restext.toString());
				
				//String b64str = Base64.encodeBase64String( smuck.getBytes() );
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {				
					try {
						window.setMember("str", smuck);
						window.eval("var b = new Blob( [str], { \"type\" : \"text\\/html\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				}
				
				/*JFrame f = new JFrame("Genome size chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				/*final StringBuilder sb = new StringBuilder();
				InputStream is = GeneSet.class.getResourceAsStream("/chart.html");
				try {
					int c = is.read();
					while( c != -1 ) {
						sb.append( (char)c );
						c = is.read();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				final String smuck = sb.toString().replace("smuck", restext.toString());
				
				//restext.append( restext.toString() );
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
				
				/*SwingUtilities.invokeLater(new Runnable() {
	                 @Override
	                 public void run() {
	                     initAndShowGUI( smuck );
	                 }
	            });*/
				/*JFXPanel	jfxpanel = new JFXPanel();
				jfxpanel.add
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.add( jfxpanel );
				frame.setVisible( true );*/
			}
		};
		
		AbstractAction mltreemapaction = new AbstractAction("mlTreeMap genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	mltreemap = new HashSet<String>();
				mltreemap.add( "COG0012" );
				mltreemap.add( "COG0016" );
				mltreemap.add( "COG0018" );
				mltreemap.add( "COG0048" );
				mltreemap.add( "COG0049" );
				mltreemap.add( "COG0052" );
				mltreemap.add( "COG0080" );
				mltreemap.add( "COG0081" );
				mltreemap.add( "COG0085" );
				mltreemap.add( "COG0087" );
				
				mltreemap.add( "COG0088" );
				mltreemap.add( "COG0090" );
				mltreemap.add( "COG0091" );
				mltreemap.add( "COG0092" );
				mltreemap.add( "COG0093" );
				mltreemap.add( "COG0094" );
				mltreemap.add( "COG0096" );
				mltreemap.add( "COG0097" );
				mltreemap.add( "COG0098" );
				mltreemap.add( "COG0099" );
				
				mltreemap.add( "COG0100" );
				mltreemap.add( "COG0102" );
				mltreemap.add( "COG0103" );
				mltreemap.add( "COG0124" );
				mltreemap.add( "COG0172" );
				mltreemap.add( "COG0184" );
				mltreemap.add( "COG0185" );
				mltreemap.add( "COG0186" );
				mltreemap.add( "COG0197" );
				mltreemap.add( "COG0200" );
				
				mltreemap.add( "COG0201" );
				mltreemap.add( "COG0202" );
				mltreemap.add( "COG0215" );
				mltreemap.add( "COG0256" );
				mltreemap.add( "COG0495" );
				mltreemap.add( "COG0522" );
				mltreemap.add( "COG0525" );
				mltreemap.add( "COG0533" );
				mltreemap.add( "COG0541" );
				mltreemap.add( "COG0552" );
				
				for( String refid : geneset.cogmap.keySet() ) {				
					Cog cog = geneset.cogmap.get( refid );
					if( mltreemap.contains( cog.id ) ) {
						Gene g = geneset.genemap.get(refid);
						if( g != null ) {
							GeneGroup gg = g.getGeneGroup();
							int i = geneset.allgenegroups.indexOf( gg );
							int r = -1;
							if( i != -1 ) r = table.convertRowIndexToView( i );
							if( r != -1 ) table.addRowSelectionInterval( r, r );
						}
					}
				}
			}
		};
		
		AbstractAction sevenaction = new AbstractAction("7 housekeeping genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	mltreemap = new HashSet<String>();
				mltreemap.add( "fusA" );
				mltreemap.add( "ileS" );
				mltreemap.add( "leuS" );
				mltreemap.add( "lepA" );
				mltreemap.add( "pyrG" );
				mltreemap.add( "recA" );
				mltreemap.add( "recG" );
				
				for( Gene g : geneset.genelist ) {
					String koname = g.koname;
					if( koname != null && koname.length() > 0 ) {
						for( String gn : mltreemap ) {
							if( koname.contains(gn) ) {
								GeneGroup gg = g.getGeneGroup();
								int i = geneset.allgenegroups.indexOf( gg );
								int r = -1;
								if( i != -1 ) r = table.convertRowIndexToView( i );
								if( r != -1 ) table.addRowSelectionInterval( r, r );
								
								break;
							}
						}
					}
				}
			}
		};
		
		AbstractAction gcaction = new AbstractAction("GC% chart data") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox	contigs = new JCheckBox("Show contigs");
				Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ), contigs );
				StringBuilder	restext = new StringBuilder();
				
				Map<String,Double>	map = new TreeMap<String,Double>();
				
				restext.append( "['Species', 'Size']" );
				//int i = 0;
				for( String spec : selspec ) {
					restext.append( ",\n['"+geneset.nameFix(spec)+"', " );
					
					int len = 0;
					int total = 0;
					
					if( contigs.isSelected() ) {
						Sequence ct = geneset.contigmap.get( spec );
						total = ct.length();
						len = ct.getGCCount();
					} else {
						List<Sequence> lcont = speccontigMap.get(spec);
						for( Sequence ct : lcont ) {
							total += ct.length();
							len += ct.getGCCount();
							/*if( c.annset != null ) for( Tegeval tv : c.annset ) {
								len += tv.getLength();
							}*/
						}
					}
					double d = (double)len/(double)total;
					
					//vals[ i ] = d;
					
					String name = null;//names[i];
					if( contigs.isSelected() ) {
						if( spec.contains("hermus") ) name = spec;
						else {
							Matcher m = Pattern.compile("\\d").matcher(spec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) name = "Thermus_" + spec;
							else name = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
						}
					} else {
						/*if( spec.contains("hermus") ) name = spec.substring( 0, spec.lastIndexOf('_') );
						else {
							Matcher m = Pattern.compile("\\d").matcher(spec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) name = "Thermus_" + spec;
							else name = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
						}*/
						name = geneset.nameFix(spec);
					}
					
					map.put( name, d );
					
					//i++;
					
					//d = Math.round( d*10000.0 )/100.0;
					//Set<GeneGroup> ggset = specGroupMap.get( spec );
					//pan.addAll( ggset );
					//if( core.isEmpty() ) core.addAll( ggset );
					//else core.retainAll( ggset );
					
					//restext.append( core.size()+", " );
					restext.append( d+"]" );
				}
				
				final String[] names = new String[ map.size() ];
				final double[] vals = new double[ map.size() ];
				
				String scaffspec = null;
				int i = 0;
				for( String spec : map.keySet() ) {
					if( contigs.isSelected() ) {
						int k = spec.indexOf("contig");
						if( k == -1 ) k = spec.indexOf("scaffold");
						if( k == -1 ) k = spec.indexOf("chromosome");
						if( k == -1 ) k = spec.indexOf("plasmid");
						if( k == -1 ) k = spec.lastIndexOf('_');
						if( k == -1 ) {
							names[i] = spec;
							scaffspec = spec;
						} else {
							names[i] = spec.substring(k);						
							scaffspec = spec.substring(0, k-1);
						}
					} else names[i] = spec;
					vals[i] = map.get( spec );
					i++;
				}
				
				final String xTitle = scaffspec != null ? "Scaffolds/Contigs" : "Species";
				final String yTitle = scaffspec != null ? scaffspec + " GC%" : "GC%";
				
				JSObject window = null;
				try {
					window = JSObject.getWindow( geneset );
				} catch( NoSuchMethodError | Exception exc ) {
					exc.printStackTrace();
				}
				
				if( window != null ) {
					final StringBuilder sb = new StringBuilder();
					InputStream is = GeneSet.class.getResourceAsStream("/columnchart.html");
					try {
						int c = is.read();
						while( c != -1 ) {
							sb.append( (char)c );
							c = is.read();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					final String smuck = sb.toString().replace("smuck", restext.toString());
					//String b64str = Base64.encodeBase64String( smuck.getBytes() );
					try {
						window.setMember("smuck", smuck);
						window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
						window.eval("open( URL.createObjectURL(b), '_blank' )");
					} catch( Exception exc ) {
						exc.printStackTrace();
					}
				} else if( Desktop.isDesktopSupported() ) {
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							if( geneset.fxframe == null ) {
								geneset.fxframe = new JFrame("Pan-core");
								geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
								geneset.fxframe.setSize(800, 600);
								
								final JFXPanel	fxpanel = new JFXPanel();
								geneset.fxframe.add( fxpanel );
								
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                	 geneset.initBarChart( fxpanel, names, vals, xTitle, yTitle, 0.6, 0.7, 0.02, "GC%" );
					                 }
					            });
							} else {
								Platform.runLater(new Runnable() {
					                 @Override
					                 public void run() {
					                	 geneset.initBarChart( null, names, vals, xTitle, yTitle, 0.6, 0.7, 0.02, "GC%" );
					                 }
					            });
							}						
							geneset.fxframe.setVisible( true );
						}
					});
					/*try {
						FileWriter fw = new FileWriter("c:/smuck.html");
						fw.write( smuck );
						fw.close();
						Desktop.getDesktop().browse( new URI("file://c:/smuck.html") );
					} catch( Exception exc ) {
						exc.printStackTrace();
					}*/
				}
				
				JFrame f = new JFrame("GC% chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				/*final StringBuilder sb = new StringBuilder();
				InputStream is = GeneSet.class.getResourceAsStream("/chart.html");
				try {
					int c = is.read();
					while( c != -1 ) {
						sb.append( (char)c );
						c = is.read();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				final String smuck = sb.toString().replace("smuck", restext.toString());*/
				
				//restext.append( restext.toString() );
				JTextArea	ta = new JTextArea();
				ta.setText( restext.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
				
				/*SwingUtilities.invokeLater(new Runnable() {
	                 @Override
	                 public void run() {
	                     initAndShowGUI( smuck );
	                 }
	            });*/
				/*JFXPanel	jfxpanel = new JFXPanel();
				jfxpanel.add
				
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.add( jfxpanel );
				frame.setVisible( true );*/
			}
		};
		
		AbstractAction gcskewaction = new AbstractAction("GC skew chart") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final List<String>			species = new ArrayList<String>( speccontigMap.keySet() );
				
				final GeneGroup	gg;
				int r = geneset.table.getSelectedRow();
				int i = -1;
				if( r != -1 ) i = geneset.table.convertRowIndexToModel( r );
				if( i != -1 ) {
					gg = geneset.allgenegroups.get( i );
				} else gg = null;
				
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
				
				//table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				JScrollPane	scroll = new JScrollPane( table );
				
				FlowLayout flowlayout = new FlowLayout();
				JComponent c1 = new JComponent() {};
				c1.setLayout( flowlayout );
				c1.add( scroll );
				
				JOptionPane.showMessageDialog(comp, c1);
				
				final BufferedImage bimg = new BufferedImage( 1024, 1024, BufferedImage.TYPE_INT_ARGB );
				final JComponent c = new JComponent() {
					public void paintComponent( Graphics g ) {
						super.paintComponent( g );
						
						g.drawImage( bimg, 0, 0, this );
					}
				};
				
				Dimension dim = new Dimension(1024, 1024);
				c.setPreferredSize( dim );
				c.setSize( dim );
				scroll = new JScrollPane( c );
				JFrame frame = new JFrame("GC skew");
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				frame.add( scroll );
				
				r = table.getSelectedRow();
				final String selspec = (String)table.getValueAt( r, 0 );
				final List<Sequence>	clist = speccontigMap.get( selspec );
				
				model = new TableModel() {
					@Override
					public int getRowCount() {
						return clist.size();
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
						return Sequence.class;
					}

					@Override
					public boolean isCellEditable(int rowIndex, int columnIndex) {
						return false;
					}

					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						return clist.get( rowIndex );
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				table = new JTable( model );
				table.setAutoCreateRowSorter( true );
				
				table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				scroll = new JScrollPane( table );
				
				flowlayout = new FlowLayout();
				JComponent c2 = new JComponent() {};
				c2.setLayout( flowlayout );
				c2.add( scroll );
				JOptionPane.showMessageDialog(comp, c2);
				
				final List<Sequence> selclist = new ArrayList<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int row : rr ) {
					i = table.convertRowIndexToModel( row );
					selclist.add( clist.get(i) );
				}
				
				int size = 0;
				final Graphics2D g2 = bimg.createGraphics();
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				for( Sequence ctg : selclist ) {
					size += ctg.length();
				}
				g2.setColor( Color.white );
				g2.fillRect( 0, 0, 1024, 1024 );
				
				final int fsize = size;
				
				JPopupMenu popup = new JPopupMenu();
				popup.add( new AbstractAction("Repaint") {
					@Override
					public void actionPerformed(ActionEvent e) {
						geneset.repaintGCSkew(selclist, g2, fsize, gg, selspec);
					}
				});
				popup.add( new AbstractAction("Auto invert") {
					@Override
					public void actionPerformed(ActionEvent e) {
						Map<Sequence,Double>	val = new HashMap<Sequence,Double>();
						int total = 0;
						//boolean[] boo = new boolean[ selclist.size() ];
						//Arrays.fill(boo, false);
						for( Sequence ctg : selclist ) {
							for( int i = 0; i < ctg.length(); i+=500 ) {
								int gcount = 0;
								int ccount = 0;
								int acount = 0;
								int tcount = 0;
								for( int k = i; k < Math.min( ctg.length(), i+10000 ); k++ ) {
									char chr = k-5000 < 0 ? ctg.getCharAt( ctg.length()+(k-5000) ) : ctg.getCharAt(k-5000);
									if( chr == 'g' || chr == 'G' ) gcount++;
									else if( chr == 'c' || chr == 'C' ) ccount++;
									else if( chr == 'a' || chr == 'A' ) acount++;
									else if( chr == 't' || chr == 'T' ) tcount++;
								}
								
								if( gcount > 0 || ccount > 0 ) {
									double gcskew = (gcount-ccount)/(double)(gcount+ccount);
									if( val.containsKey( ctg ) ) {
										val.put( ctg, val.get(ctg)+gcskew );
									} else {
										val.put( ctg, gcskew );
									}
								}
								
								if( acount > 0 || tcount > 0 ) {
									double atskew = (acount-tcount)/(double)(acount+tcount);
								}
							}
							total += ctg.length();
						}
						
						double min = Double.MAX_VALUE;
						int mini = 0;
						for( int i = 0; i < Math.pow(2.0, selclist.size()); i++ ) {
							double dval = 0.0;
							int k = 0;
							for( Sequence ctg : selclist ) {
								double calc = 0.0;
								if( val.containsKey(ctg) ) calc = val.get(ctg);//*ctg.length();
								else {
									System.err.println();
								}
								if( (i & (1 << k)) > 0 ) dval -= calc;
								else dval += calc;
								k++;
							}
							if( Math.abs( dval ) < min ) {
								min = Math.abs( dval );
								mini = i;
							}
						}
						
						int i = 0;
						for( Sequence ctg : selclist ) {
							if( (mini & (1 << i)) > 0 ) ctg.setReverse( !ctg.isReverse() );
							i++;
						}
						
						geneset.repaintGCSkew(selclist, g2, fsize, gg, selspec);
					}
				});
				popup.add( new AbstractAction("Auto connect") {
					@Override
					public void actionPerformed(ActionEvent e) {
						for( Sequence ctg : selclist ) {
							
						}
					}
				});
				popup.addSeparator();
				popup.add( new AbstractAction("Save") {
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean succ = true;
						try {
							ImageIO.write(bimg, "png", new File("c:/cir.png") );
						} catch(Exception e1) {
							succ = false;
							e1.printStackTrace();
						}
						
						try {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(bimg, "png", baos);
							baos.close();
							String b64str = Base64.getEncoder().encodeToString( baos.toByteArray() );
							
							JSObject window = JSObject.getWindow( geneset );
							window.call( "string2Blob", new Object[] {b64str, "image/png"} );
						} catch(Exception e1) {
							succ = false;
							e1.printStackTrace();
						}
						
						if( !succ ) {
							FileSaveService fss = null;
					        FileContents fileContents = null;
					    	 
					        try {
					        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
						        //OutputStreamWriter	osw = new OutputStreamWriter( baos );
								ImageIO.write(bimg, "png", baos);
								baos.close();

						    	try {
						    		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
						    	} catch( UnavailableServiceException e1 ) {
						    		fss = null;
						    	}
						    	 
						        if( fss != null ) {
						        	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
						            fileContents = fss.saveFileDialog(null, null, bais, "export.png");
						            bais.close();
						            OutputStream os = fileContents.getOutputStream(true);
						            os.write( baos.toByteArray() );
						            os.close();
						        }
					        } catch( Exception e1 ) {
					        	e1.printStackTrace();
					        }
						}
					}
				});
				c.setComponentPopupMenu( popup );
				
				c.addMouseListener( new MouseListener() {
					int x;
					int y;
					Sequence sctg;
					
					@Override
					public void mouseReleased(MouseEvent e) {
						if( sctg != null ) {
							int rx = e.getX();
							int ry = e.getY();
							
							double horn = Math.atan2( (double)(512-ry), (double)(512-rx) )+Math.PI;
							int val = (int)( (double)(horn*fsize)/(double)(2*Math.PI) );
							
							int tot = 0;
							Sequence sctg2 = null;
							for( Sequence ctg : selclist ) {
								if( tot > val ) break;
								tot += ctg.length();
								sctg2 = ctg;
							}
							
							int i = selclist.indexOf( sctg2 );
							selclist.remove( sctg );
							selclist.add( i, sctg );
							
							i = clist.indexOf( sctg2 );
							clist.remove( sctg );
							clist.add( i, sctg );
							
							geneset.repaintGCSkew(selclist, g2, fsize, gg, selspec);
							c.repaint();
						}
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						x = e.getX();
						y = e.getY();
						
						double horn = Math.atan2( (double)(512-y), (double)(512-x) )+Math.PI;
						int val = (int)( (double)(horn*fsize)/(double)(2*Math.PI) );
						
						int tot = 0;
						for( Sequence ctg : selclist ) {
							if( tot > val ) break;
							tot += ctg.length();
							sctg = ctg;
						}
						
						if( e.getClickCount() == 2 ) {							
							sctg.setReverse( !sctg.isReverse() );
							geneset.repaintGCSkew(selclist, g2, fsize, gg, selspec);
							c.repaint();
						}
					}
					
					@Override
					public void mouseExited(MouseEvent e) {}
					
					@Override
					public void mouseEntered(MouseEvent e) {}
					
					@Override
					public void mouseClicked(MouseEvent e) {}
				});
				
				geneset.repaintGCSkew( selclist, g2, size, gg, selspec );				
				frame.setVisible( true );
			}
		};
		
		//Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ), null );
		
		AbstractAction totalcogaction = new AbstractAction("COG total") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = new JCheckBox("Plasmid");
				Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ), cb );
				
				char nohit = '*';
				final Map<Character,Integer>	mip = new HashMap<Character,Integer>();
				final Map<Character,Integer>	map = new HashMap<Character,Integer>();
				if( geneset.table.getModel() == geneset.groupModel ) {
					for( int r = 0; r < geneset.table.getRowCount(); r++ ) {
						int i = geneset.table.convertRowIndexToModel(r);
						if( i >= 0 && i < geneset.allgenegroups.size() ) {
							GeneGroup gg = geneset.allgenegroups.get(i);
							Cog cog = gg.getCommonCog(geneset.cogmap);
							if( cog != null /*&& includedCogs.contains(cog.symbol)*/ ) {
								if( cb.isSelected() ) {
									Set<String> tmp = new HashSet<String>( gg.species.keySet() );
									tmp.retainAll( selspec );
									
									if( tmp.size() > 0 ) {
										int total = gg.size();
										int p = 0;
										for( Gene g : gg.genes ) {
											if( g.getContig().isPlasmid() ) p++;
										}
										
										if( gg.isOnAnyPlasmid() ) { //(float)p/(float)total > 0.9 ) { //gg.isOnAnyPlasmid() ) {
											int k = 0;
											if( mip.containsKey( cog.symbol ) ) k = mip.get(cog.symbol);
											mip.put( cog.symbol, k+1 );
										} else {
											int k = 0;
											if( map.containsKey( cog.symbol ) ) k = map.get(cog.symbol);
											map.put( cog.symbol, k+1 );
										}
									}
								} else {
									if( gg.species.keySet().containsAll(selspec) ) {
										int k = 0;
										if( map.containsKey( cog.symbol ) ) k = map.get(cog.symbol);
										map.put( cog.symbol, k+1 );
									} else {
										Set<String> tmp = new HashSet<String>( gg.species.keySet() );
										tmp.removeAll( selspec );
										
										if( tmp.size() < gg.species.size() ) {
											int k = 0;
											if( mip.containsKey( cog.symbol ) ) k = mip.get(cog.symbol);
											mip.put( cog.symbol, k+1 );
										}
									}
								}
								/*for( String spec : selspec ) {
									if( gg.species.containsKey( spec ) ) {
										Teginfo ti = gg.species.get( spec );
										for( Tegeval tv : ti.tset ) {
											Map<Character,Integer> submap;
											/*if( contigs ) {
												if( map.containsKey( tv.contloc ) ) {
													
												}
											} else {*
												int val = 0;
												if( map.containsKey( tv.getSpecies() ) ) {
													submap = map.get( tv.getSpecies() );
													if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
												} else {
													submap = new HashMap<Character,Integer>();
													map.put(spec, submap);
												}
												submap.put( cog.symbol, val+1 );
											//}
										}
									}
								}*/
							} else {
								if( cb.isSelected() ) {
									Set<String> tmp = new HashSet<String>( gg.species.keySet() );
									tmp.retainAll( selspec );
									
									if( tmp.size() > 0 ) {
										int total = gg.size();
										int p = 0;
										for( Gene g : gg.genes ) {
											if( g.getContig().isPlasmid() ) p++;
										}
										
										if( gg.isOnAnyPlasmid() ) { //(float)p/(float)total > 0.9 ) { //gg.isOnAnyPlasmid() ) {
											int k = 0;
											if( mip.containsKey( nohit ) ) k = mip.get(nohit);
											mip.put( nohit, k+1 );
										} else {
											int k = 0;
											if( map.containsKey( nohit ) ) k = map.get(nohit);
											map.put( nohit, k+1 );
										}
									}
								} else {
									if( gg.species.keySet().containsAll(selspec) ) {
										int k = 0;
										if( map.containsKey( nohit ) ) k = map.get(nohit);
										map.put( nohit, k+1 );
									} else {
										Set<String> tmp = new HashSet<String>( gg.species.keySet() );
										tmp.removeAll( selspec );
										
										if( tmp.size() < gg.species.size() ) {
											int k = 0;
											if( mip.containsKey( nohit ) ) k = mip.get(nohit);
											mip.put( nohit, k+1 );
										}
									}
								}
							}
						}
					}
				}
				
				//Character last = null;
				StringBuilder sb = new StringBuilder();
				for( String s : Cog.coggroups.keySet() ) {
					Set<Character> sc = Cog.coggroups.get(s);
					if( s.contains("METABOLISM") ) {
						for( Character c : sc ) {
							sb.append( "\t"+Cog.charcog.get(c) );
						}
					} else {
						sb.append( "\t"+s );
						if( sc.contains('V') ) {
							sb.append( "\tDefence mechanism" );
						}
					}
					//last = c;
				}
				sb.append( "\tNo hit" );
				
				if( cb.isSelected() ) sb.append( "\nChromosome" );
				else sb.append( "\nCore" );
				
				int mit = 0;
				int mat = 0;
				for( String s : Cog.coggroups.keySet() ) {
					Set<Character> sc = Cog.coggroups.get(s);
					if( s.contains("METABOLISM") ) {
						for( Character c : sc ) {
							int count = 0;
							if( map.containsKey(c) ) {
								int val = map.get(c);
								count = val;
								//if( c.equals(last) ) sb.append("\n");
								//sb.append("\t");
								mat += val;
							}
							sb.append("\t"+count);
						}
					} else {
						int count = 0;
						for( Character c : sc ) {
							if( !c.equals('V') && map.containsKey(c) ) {
								int val = map.get(c);
								count += val;
								//if( c.equals(last) ) sb.append("\n");
								//sb.append("\t");
								mat += val;
							}
						}
						sb.append( "\t"+count );
						
						if( sc.contains('V') ) {
							count = 0;
							if( map.containsKey('V') ) {
								int val = map.get('V');
								count += val;
							}
							sb.append( "\t"+count );
						}
					}
				}
				int count = 0;
				if( map.containsKey(nohit) ) count = map.get( nohit );
				sb.append( "\t"+count );
				
				if( cb.isSelected() ) sb.append( "\nPlasmid" );
				else sb.append( "\nAccessory" );
				
				for( String s : Cog.coggroups.keySet() ) {
					Set<Character> sc = Cog.coggroups.get(s);
					if( s.contains("METABOLISM") ) {
						for( Character c : sc ) {
							count = 0;
							if( mip.containsKey(c) ) {
								int val = mip.get(c);
								count = val;
								mit += val;
							}
							sb.append("\t"+count);
						}
					} else {
						count = 0;
						for( Character c : sc ) {
							if( !c.equals('V') && mip.containsKey(c) ) {
								int val = mip.get(c);
								count += val;
								mit += val;
							}
						}
						sb.append( "\t"+count );
						
						if( sc.contains('V') ) {
							count = 0;
							if( mip.containsKey('V') ) {
								int val = mip.get('V');
								count += val;
							}
							sb.append( "\t"+count );
						}
					}
				}
				count = 0;
				if( mip.containsKey(nohit) ) count = mip.get( nohit );
				sb.append( "\t"+count );
				sb.append( "\n" );
				System.err.println( "map size " + mat + " mip size " + mit );
				
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						if( geneset.fxframe == null ) {
							geneset.fxframe = new JFrame("COG");
							geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
							geneset.fxframe.setSize(800, 600);
							
							final JFXPanel	fxpanel = new JFXPanel();
							geneset.fxframe.add( fxpanel );
							
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                     geneset.initDualPieChart( fxpanel, map, mip );
				                 }
				            });
						} else {
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                     geneset.initDualPieChart( null, map, mip );
				                 }
				            });
						}						
						geneset.fxframe.setVisible( true );
					}
				});
				
				JFrame f = new JFrame("GC% chart");
				f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				f.setSize( 800, 600 );
				
				JTextArea	ta = new JTextArea();
				ta.setText( sb.toString() );
				JScrollPane	sp = new JScrollPane(ta);
				f.add( sp );
				f.setVisible( true );
			}
		};
		AbstractAction cogaction = new AbstractAction("COG chart data") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					/*Map<String,String> env = new HashMap<String,String>();
					//env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")* );
					geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
					
					Path nf = geneset.zipfilesystem.getPath("/cog.blastout");
					
					//InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
					//uni2symbol(new InputStreamReader(is), bw, unimap);
					
					//bw.close();
					//long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );

					BufferedReader br = Files.newBufferedReader(nf);*/
					final JCheckBox	contigs = new JCheckBox("Show contigs");
					final JCheckBox	uniform = new JCheckBox("Uniform");
					Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ), contigs, uniform );
					
					final List<Character> coglist = new ArrayList<Character>( Cog.charcog.keySet() );
					HashSet<Character>	includedCogs = new HashSet<Character>();
					JTable cogtable = new JTable();
					cogtable.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
					TableModel cogmodel = new TableModel() {
						@Override
						public int getRowCount() {
							return coglist.size();
						}

						@Override
						public int getColumnCount() {
							return 2;
						}

						@Override
						public String getColumnName(int columnIndex) {
							if( columnIndex == 0 ) return "Symbol";
							else return "Name";
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
							Character c = coglist.get( rowIndex);
							if( columnIndex == 0 ) return c.toString();
							else return Cog.charcog.get( c );
						}

						@Override
						public void setValueAt(Object aValue, int rowIndex,int columnIndex) {}

						@Override
						public void addTableModelListener(TableModelListener l) {}

						@Override
						public void removeTableModelListener(TableModelListener l) {}
						
					};
					cogtable.setModel( cogmodel );
					JScrollPane cogscroll = new JScrollPane( cogtable );
					JOptionPane.showMessageDialog( geneset, cogscroll );
					
					int[] rr = cogtable.getSelectedRows();
					for( int r : rr ) {
						includedCogs.add( coglist.get(r) );
					}
					
					final Map<String,String>					all = new TreeMap<String,String>();
					final Map<String, Map<Character,Integer>> 	map = new LinkedHashMap<String, Map<Character,Integer>>();
					geneset.cogCalc( null, includedCogs, map, selspec, contigs.isSelected() );
					
					Map<Character,Row> rl = new HashMap<Character,Row>();
					
					Workbook	wb = new XSSFWorkbook();
					Sheet sh = wb.createSheet("COG");
					
					int k = 0;
					for( String sp : map.keySet() ) {
						Map<Character,Integer> mm = map.get(sp);
						if( mm != null ) for( char c : mm.keySet() ) {
							int cn = mm.get(c);
							
							if( !rl.containsKey(c) ) {
								rl.put( c, sh.createRow(rl.size()) );
							}
							
							Row r = rl.get(c);
							r.createCell(k).setCellValue( ""+c );
							r.createCell(k+1).setCellValue( cn );
						}
						k+=2;
					}
					
					File f = new File("/Users/sigmar/tmp.xlsx");
					FileOutputStream fos = new FileOutputStream( f );
					wb.write(fos);
					fos.close();
					
					Desktop.getDesktop().open(f);
					
					
					StringWriter fw = geneset.writeCog( map, includedCogs, uniform.isSelected() );
					String repl = fw.toString();
					
					fw = geneset.writeSimpleCog( map );
					String stuff = fw.toString();
					
					String stxt = "";
					final StringBuilder sb = new StringBuilder();
					InputStream is = GeneSet.class.getResourceAsStream("/cogchart.html");
					if( is != null ) {
						try {
							int c = is.read();
							while( c != -1 ) {
								sb.append( (char)c );
								c = is.read();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						stxt = sb.toString().replace("smuck", repl);
					}
					final String smuck = stxt;
					
					//String b64str = Base64.encodeBase64String( smuck.getBytes() );
					/*JSObject window = null;
					try {
						window = JSObject.getWindow( geneset );
					} catch( NoSuchMethodError | Exception exc ) {
						exc.printStackTrace();
					}*/
					
					boolean web = true;
						
					if( web ) {
						if( geneset.fxframe == null ) {
							geneset.fxframe = new JFrame("COG");
							geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
							geneset.fxframe.setSize(800, 600);
							
							final JFXPanel	fxpanel = new JFXPanel();
							geneset.fxframe.add( fxpanel );
							
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                	 geneset.initWebPage( fxpanel, smuck );
				                 }
				            });
						} else {
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                	 geneset.initWebPage( null, smuck );
				                 }
				            });
						}						
						geneset.fxframe.setVisible( true );
						
						/*boolean succ = true;
						try {
							window.setMember("smuck", smuck);
							//window.eval("var binary = atob(b64str)");
							//window.eval("var i = binary.length");
							//window.eval("var view = new Uint8Array(i)");
						    //window.eval("while(i--) view[i] = binary.charCodeAt(i)");
							window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
							window.eval("open( URL.createObjectURL(b), '_blank' )");
						} catch( Exception exc ) {
							exc.printStackTrace();
						}*

						try {
							window.setMember("smuck", smuck);
							
							//window.eval("var binary = atob(b64str)");
							//window.eval("var i = binary.length");
							//window.eval("var view = new Uint8Array(i)");
						    //window.eval("while(i--) view[i] = binary.charCodeAt(i)");
							window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
							window.eval("open( URL.createObjectURL(b), '_blank' )");
						} catch( Exception exc ) {
							exc.printStackTrace();
						}*/
						
						if( Desktop.isDesktopSupported() ) {
							try {
								FileWriter fwr = new FileWriter("/Users/sigmar/smuck.html");
								fwr.write( smuck );
								fwr.close();
								Desktop.getDesktop().browse( new URI("file:///Users/sigmar/smuck.html") );
							} catch( Exception exc ) {
								exc.printStackTrace();
							}
						}
					} else {
						SwingUtilities.invokeLater( new Runnable() {
							@Override
							public void run() {
								if( geneset.fxframe == null ) {
									geneset.fxframe = new JFrame("COG");
									geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
									geneset.fxframe.setSize(800, 600);
									
									final JFXPanel	fxpanel = new JFXPanel();
									geneset.fxframe.add( fxpanel );
									
									Platform.runLater(new Runnable() {
						                 @Override
						                 public void run() {
						                     geneset.initStackedBarChart( fxpanel, all, map, uniform.isSelected() );
						                 }
						            });
								} else {
									Platform.runLater(new Runnable() {
						                 @Override
						                 public void run() {
						                     geneset.initStackedBarChart( null, all, map, uniform.isSelected() );
						                 }
						            });
								}						
								geneset.fxframe.setVisible( true );
							}
						});
					}
					geneset.zipfilesystem.close();
					
					JFrame fr = new JFrame("GC% chart");
					fr.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					fr.setSize( 800, 600 );
					
					JTextArea	ta = new JTextArea();
					ta.setText( repl + "\n" + stuff );
					JScrollPane	sp = new JScrollPane(ta);
					fr.add( sp );
					fr.setVisible( true );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		
		AbstractAction fetchcoreaction = new AbstractAction("Fetch core") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String>	selspec = geneset.getSelspec( geneset, new ArrayList( geneset.specList ) );
				
				JFrame frame = null;
				if( geneset.currentSerify == null ) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);
					
					SerifyApplet sa = new SerifyApplet( geneset.zipfilesystem );
					sa.init( frame );
					//frame.add( )
					geneset.currentSerify = sa;
				} /*else {
					currentSerify.clearSequences();
					frame = (JFrame)currentSerify.cnt;
				}*/

				//Map<Integer,String>			ups = new HashMap<Integer,String>();
				//Set<Integer>				stuck = new HashSet<Integer>();
				//Map<Integer,List<Tegeval>>	ups2 = new HashMap<Integer,List<Tegeval>>();
				//int[] rr = table.getSelectedRows();
				List<GeneGroup>	includedGroups = new ArrayList<GeneGroup>();
				for( GeneGroup genegroup : geneset.allgenegroups ) {
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
						
						List<Tegeval>	annset;
						if( ups2.containsKey( gg.getGroupIndex() ) ) annset = ups2.get( gg.getGroupIndex() );
						else {
							annset = new ArrayList<Tegeval>();
							ups2.put( gg.getGroupIndex(), annset );
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
									annset.add( tv );
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
					List<Tegeval>	annset = genegroup.getTegevals( selspec ); //ups2.get(gi);
					
					sb.append( "[" + genegroup.getCommonFunction( false, fggmap.keySet() ) + "]" + genegroup.groupIndex + "_" + name.replace('/', '-') + ":\n");
					/*if( annset.size() < 28 ) {
						for( Tegeval tv : annset ) {
							System.err.println( tv.cont );
						}
						System.err.println();
					}*/
					for( Tegeval tv : annset ) {
						sb.append(">" + tv.name.substring(0, tv.name.indexOf('_')) + "\n");
						for (int i = 0; i < tv.getProteinLength(); i += 70) {
							sb.append( tv.getProteinSubsequence(i, Math.min(i + 70, tv.getProteinLength() )) + "\n");
						}
					}
				}
				
				try {
					String fastaStr = sb.toString();
					/*FileWriter fw = new FileWriter("/root/erm.fasta");
					fw.write( fastaStr );
					fw.close();*/
					
					geneset.currentSerify.addSequences("uh", new StringReader( fastaStr ), Paths.get("/"), null);
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
								System.err.println( "cm " + geneset.contigmap.size() );
							}
						}
						
						JFrame f = new JFrame();
						f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
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
		
		AbstractAction selectflankingaction = new AbstractAction("Select flanking") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<GeneGroup>	ggset = new HashSet<GeneGroup>();
				for( String str : geneset.contigmap.keySet() ) {
					Sequence c = geneset.contigmap.get( str );
					
					if( c != null && c.getAnnotations() != null && c.getAnnotations().size() > 0 ) {
						ggset.add( ((Tegeval)c.getAnnotation( 0 )).getGene().getGeneGroup() );
						ggset.add( ((Tegeval)c.getAnnotation( c.getAnnotations().size()-1 )).getGene().getGeneGroup() );
					}
				}
				
				for( GeneGroup gg : ggset ) {
					int i = geneset.allgenegroups.indexOf( gg );
					int r = -1;
					if( i != -1 ) r = table.convertRowIndexToView( i );
					if( r != -1 ) table.addRowSelectionInterval( r, r );
				}
			}
		};
		
		AbstractAction showflankingaction = new AbstractAction("Show flanking") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<JComponent> complist = new ArrayList<JComponent>();
				
				JCheckBox	check = new JCheckBox("Genes");
				complist.add( check );
				
				JRadioButton gaps = new JRadioButton("Gaps");
				JRadioButton ctgs = new JRadioButton("Contigs");
				ButtonGroup bg = new ButtonGroup();
				bg.add( gaps );
				bg.add( ctgs );
				
				complist.add( gaps );
				complist.add( ctgs );
				
				ctgs.setSelected( true );
				
				List<Sequence> contigs = geneset.getSelspecContigs( complist );
				//int[] rr = ctable.getSelectedRows();
				
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				if( check.isSelected() ) {
					JTextArea	text = new JTextArea();
					for( Sequence ctg : contigs ) {
						//int i = ctable.convertRowIndexToModel( row );
						//Sequence ctg = contigs.get( i );
						if( ctg.getAnnotations() != null ) {
							if( ctg.isReverse() ) {
								Tegeval tv0 = (Tegeval)ctg.getAnnotation(0);
								Tegeval tv1 = (Tegeval)ctg.getAnnotation(1);
								
								Tegeval tv = (Tegeval)ctg.getAnnotation(ctg.getAnnotations().size()-1);
								Tegeval tv2 = (Tegeval)ctg.getAnnotation(ctg.getAnnotations().size()-2);
								text.append( tv.getGene().getGeneGroup().getCommonName() + " -- " + tv2.getGene().getGeneGroup().getCommonName() + " -- " + ctg.getName() + " -- " + tv1.getGene().getGeneGroup().getCommonName() + " -- " + tv0.getGene().getGeneGroup().getCommonName() + "\n" );
							} else {
								
								if( ctg.getAnnotations().size() > 3 ) {
									String n0 = ((Tegeval)ctg.getAnnotation(0)).getGene().getGeneGroup().getCommonName();
									String n1 = ((Tegeval)ctg.getAnnotation(1)).getGene().getGeneGroup().getCommonName();
									String n_2 = ((Tegeval)ctg.getAnnotation(ctg.getAnnotations().size()-1)).getGene().getGeneGroup().getCommonName();
									String n_1 = ((Tegeval)ctg.getAnnotation(ctg.getAnnotations().size()-2)).getGene().getGeneGroup().getCommonName();
									
									text.append( n0 + " -- " + n1 + " -- " + ctg.getName() + " -- " + n_2 + " -- " + n_1 + "\n" );
								} else if( ctg.getAnnotations().size() > 1 ) {
									text.append( ((Tegeval)ctg.getAnnotation(0)).getGene().getGeneGroup().getCommonName() + " -- " + ctg.getName() + " -- " + ((Tegeval)ctg.getAnnotation(ctg.getAnnotations().size()-1)).getGene().getGeneGroup().getCommonName() + "\n" );
								} else if( ctg.getAnnotations().size() == 1 ) {
									Tegeval tv = (Tegeval)ctg.getAnnotation(0);
									text.append( tv.getGene().getGeneGroup().getCommonName() + " -- " + ctg.getName() + "\n" );
								}
							}
						}
					}
					frame.add( text );
				} else {
					Serifier serifier = new Serifier();
					JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
					jf.initGui(frame);

					if( gaps.isSelected() ) {
						for( Sequence ctg : contigs ) {
							//int i = ctable.convertRowIndexToModel( row );
							//Sequence ctg = contigs.get( i );
							
							List<Integer> starts = new ArrayList<Integer>();
							List<Integer> stops = new ArrayList<Integer>();
							
							int started = -1;
							for( int k = 0; k < ctg.length(); k++ ) {
								char b = ctg.getCharAt(k);
								if( b == 'n' || b == 'N' ) {
									if( started == -1 ) started = k;
								} else if( started != -1 ) {
									starts.add( started );
									stops.add( k );
									started = -1;
								}
							}
							
							for( int k = 0; k < starts.size(); k++ ) {
								int start = starts.get(k);
								int stop = stops.get(k);
								
								Sequence seq = new Sequence(ctg.getName()+"_"+start+"_"+stop, null);
								seq.append( ctg.sb.substring( Math.max(0, start-100), start ) );
								seq.append( "-----" );
								seq.append( ctg.sb.substring( stop, Math.min(stop+100, ctg.length()) ) );
								
								serifier.addSequence( seq );
							}
						}
					} else {
						for( Sequence ctg : contigs ) {
							//int i = ctable.convertRowIndexToModel( row );
							//Sequence ctg = contigs.get( i );
							Sequence seq = new Sequence(ctg.getName(), null);
							if( ctg.length() <= 200 ) {
								seq.append( ctg.sb );
							} else {
								seq.append( ctg.sb.substring(0, 100) );
								seq.append( "-----" );
								seq.append( ctg.sb.substring(ctg.length()-100, ctg.length()) );
							}
							if( ctg.isReverse() ) {
								seq.reverse();
								seq.complement();
							}
							serifier.addSequence( seq );
						}
					}
					
					jf.updateView();
				}
				frame.setVisible(true);
			}
		};
		
		AbstractAction showcontigsaction = new AbstractAction("Show contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final List<Sequence>	allcontigs = new ArrayList<Sequence>();
				for( String spec : speccontigMap.keySet() ) {
					List<Sequence>	ctgs = speccontigMap.get( spec );
					allcontigs.addAll( ctgs );
				}
				
				TableModel model = new TableModel() {
					@Override
					public int getRowCount() {
						return allcontigs.size();
					}

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public String getColumnName(int columnIndex) {
						return "Sequence";
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
						return allcontigs.get( rowIndex ).getName();
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				JTable table = new JTable( model );
				table.setAutoCreateRowSorter( true );
				table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				JScrollPane	scroll = new JScrollPane( table );
				
				FlowLayout flowlayout = new FlowLayout();
				JComponent c1 = new JComponent() {};
				c1.setLayout( flowlayout );
				c1.add( scroll );
				
				JOptionPane.showMessageDialog(comp, c1);
				
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
				jf.initGui(frame);

				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel( r );
					Sequence ctg = allcontigs.get( i );
					serifier.addSequence( ctg );
				}
				
				jf.updateView();
				frame.setVisible(true);
			}
		};
		
		AbstractAction showunresolved = new AbstractAction("Show unresolved genes") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta( (comp instanceof JApplet) ? (JApplet)comp : null, serifier, cs );
				jf.initGui(frame);
				
				for( GeneGroup gg : geneset.allgenegroups ) {
					String commonName = gg.getCommonName();
					if( commonName != null && (commonName.contains("contig") || commonName.contains("scaffold")) ) {
						Tegeval tv = gg.getLongestSequence();
						Sequence seq = tv.getAlignedSequence();
						seq.setName( commonName );
						seq.removeGaps();
						if( seq.sb.indexOf("X") == -1 ) {
							serifier.addSequence( seq );
						}
					}
				}
				jf.updateView();
				frame.setVisible( true );
			}
		};
		
		//PrincipleComponentAnalysis pca = new PrincipleComponentAnalysis();
		//pca.
		
		AbstractAction	genephyl = new AbstractAction("Gene phylogeny") {	
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
				
				final double[] b0;
				final double[] b1;
				final double[] b2;
				final String[] names;
				if( blosumap != null ) {
					int[] rr = geneset.table.getSelectedRows();
					
					double[] mat = new double[ rr.length*rr.length ];
					Arrays.fill( mat, 0.0 );
					
					int selr = geneset.table.convertRowIndexToModel( rr[0] );
					GeneGroup gg = geneset.allgenegroups.get(selr);
					List<String>	speclist = new ArrayList<String>( gg.species.keySet() );
					
					int samplesize = (speclist.size()-1)*(speclist.size())/2;
					PrincipleComponentAnalysis pca = new PrincipleComponentAnalysis();
					pca.setup(samplesize, rr.length);
					Map<GeneGroup,double[]>	valmap = new HashMap<GeneGroup,double[]>();
					names = new String[ rr.length ];
					for( int k = 0; k < rr.length; k++ ) {
						int i = geneset.table.convertRowIndexToModel( rr[k] );
						gg = geneset.allgenegroups.get(i);
						names[k] = gg.getCommonName();
						
						double[] sdb = new double[ samplesize ];
						
						int u = 0;
						double norm = 0.0;
						for( int m = 0; m < speclist.size()-1; m++ ) {
							String  spec1 = speclist.get( m );
							Teginfo ti = gg.species.get( spec1 );
							for( int n = m+1; n < speclist.size(); n++ ) {
								String spec2 = speclist.get( n );
								double val = GeneCompare.blosumVal(ti.best.getAlignedSequence(), spec2, gg, blosumap);
								sdb[u++] = val;
								norm += val*val;
							}
						}
						
						norm = Math.sqrt( norm );
						for( int v = 0; v < samplesize; v++ ) {
							sdb[v] /= norm;
						}
						valmap.put( gg, sdb );
						//pca.addSample( sdb );
					}
					
					//List<double[]>	dvals = new ArrayList<double[]>();
					for( int i = 0; i < samplesize; i++ ) {
						double[] d = new double[ valmap.size() ];
						//dvals
						int u = 0;
						for( GeneGroup geneg : valmap.keySet() ) {
							double[] sdb = valmap.get( geneg );
							d[u++] = sdb[i];
						}
						pca.addSample( d );
					}
					
					pca.computeBasis(3);
					
					b0 = pca.getBasisVector(0);
					b1 = pca.getBasisVector(1);
					b2 = pca.getBasisVector(2);
					System.err.println( b0.length );
					
					//b0 = Arrays.copyOf( b00, 100 );
					//b1 = Arrays.copyOf( b11, 100 );
					
					for( int i = 0; i < b0.length; i++ ) {
						//b0[i] *= 10000.0;
						//b1[i] *= 10000.0;
						System.err.println( b0[i] + "\t" + b1[i] );
					}
				} else {
					b0 = null;
					b1 = null;
					b2 = null;
					names = null;
				}
				
				/*JFXPanel fxpanel = new JFXPanel();
				Platform.runLater( new Runnable() {
					@Override
					public void run() {
						new ChartApp( names, b0, b1 );
					}
				});*/
				
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						if( geneset.fxframe == null ) {
							geneset.fxframe = new JFrame("Gene phyl");
							geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
							geneset.fxframe.setSize(800, 600);
							
							final JFXPanel	fxpanel = new JFXPanel();
							geneset.fxframe.add( fxpanel );
							
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                     geneset.initFXChart( fxpanel, names, b0, b1 );
				                 }
				            });
						} else {
							Platform.runLater(new Runnable() {
				                 @Override
				                 public void run() {
				                     geneset.initFXChart( null, names, b0, b1 );
				                 }
				            });
						}						
						geneset.fxframe.setVisible( true );
					}
				});
				
				//double[] sdb1 = new double[ (speclist.size()-1)*(speclist.size())/2 ];
				//double[] sdb2 = new double[ sdb1.length ];
				
				/*for( int k = 0; k < rr.length-1; k++ ) {
					int i = table.convertRowIndexToModel( rr[k] );
					GeneGroup gg1 = allgenegroups.get(i);
					double[] sdb1 = valmap.get(gg1);
					
					/*int v = 0;
					for( int m = 0; m < speclist.size()-1; m++ ) {
						String  spec1 = speclist.get( m );
						Teginfo ti = gg1.species.get( spec1 );
						for( int n = m+1; n < speclist.size(); n++ ) {
							String spec2 = speclist.get( n );
							//Teginfo ti2 = gg1.species.get( spec2 );
							sdb1[v++] = GeneCompare.blosumVal(ti.best, spec2, gg1, blosumap);
						}
					}*
					
					for( int l = k+1; l < rr.length; l++ ) {
						int i1 = table.convertRowIndexToModel( rr[l] );
						GeneGroup gg2 = allgenegroups.get(i1);
						double[] sdb2 = valmap.get(gg2);
						
						double val = 0.0;
						for( int x = 0; x < sdb1.length; x++ ) {
							double diff = sdb1[x]-sdb2[x];
							val += diff*diff;
						}
						val = Math.sqrt( val );
						
						mat[k*rr.length+l] = val;
						mat[l*rr.length+k] = val;
					}
				}
				
				/*List<String>	genenames = new ArrayList<String>();
				StringBuilder 	sb = new StringBuilder();
				sb.append( "\t"+rr.length );
				for( int i = 0; i < mat.length; i++ ) {
					if( i % rr.length == 0 ) {
						int r = i / rr.length;
						int v = table.convertRowIndexToModel(rr[r]);
						gg = allgenegroups.get(v);
						genenames.add( gg.getCommonName().replace(",", "").replace("(", "").replace(")", "").replace(":", "") );
						sb.append( "\n" + gg.getCommonName() + "\t" + mat[i] );
					} else sb.append( "\t" + mat[i] );
				}
				sb.append("\n");
				
				JTextArea	ta = new JTextArea();
				frame.add( ta );
				ta.setText( sb.toString() );
				
				TreeUtil tu = new TreeUtil();
				Node n = tu.neighborJoin(mat, genenames, null, false, false);
				
				String tree = n.toString();
				
				boolean succ = true;
				try {
					JSObject win = JSObject.getWindow( geneset );
					win.call("fastTree", new Object[] { tree });
				} catch( NoSuchMethodError | Exception e1 ) {
					e1.printStackTrace();
					succ = false;
				}
				
				if( !succ ) {
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
				}*/
			}
		};		
		final JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem();
		checkbox.setAction(new AbstractAction("Sort by location") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tegeval.locsort = checkbox.isSelected();
			}
		});
		
		menu.add( checkbox );
		menu.addSeparator();
		menu.add( keggaction );
		menu.add( genomestataction );
		menu.add( seqstat );
		menu.add( shuffletreeaction );
		menu.add( crispraction );
		menu.add( presabsaction );
		menu.add( freqdistaction );
		menu.add( gcpaction );
		//menu.add( matrixaction );
		menu.add( pancoreaction );
		menu.add( blastaction );
		menu.add( koexportaction );
		menu.add( cazyexportaction );
		menu.add( genomesizeaction );
		menu.add( gcaction );
		menu.add( gcskewaction );
		menu.add( mltreemapaction );
		menu.add( sevenaction );
		menu.add( totalcogaction );
		menu.add( cogaction );
		menu.add( codregaction );
		menu.add( fetchcoreaction );
		menu.add( loadcontiggraphaction );
		menu.add( selectflankingaction );
		menu.add( showflankingaction );
		menu.add( showcontigsaction );
		menu.add( showunresolved );
		menu.add( genephyl );
	}
}
