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

package org.simmi.distann;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
/*import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.ann.ANIResult;
import org.simmi.javafasta.DataTable;
import org.simmi.javafasta.shared.*;
import org.simmi.javafasta.unsigned.*;
import org.simmi.serifier.SerifyApplet;
import org.simmi.treedraw.shared.TreeUtil.Node;
import org.simmi.treedraw.shared.TreeUtil.NodeSet;
import scala.Tuple2;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static org.simmi.serifier.SerifyApplet.blastpRun;

/**
 *
 * @version $Id:  $
 */
public class GeneSetHead {
	GeneSet							geneset;
	DistannFX 						app;
	List<JSplitPane> 				splitpaneList = new ArrayList<>();

	public class Locus {
		public Locus(String s, String l) {
			species = s;
			locus = l;
		}

		String species;
		String locus;

		public String getSpecies() {
			return species;
		}

		public String getLocus() {
			return locus;
		}

		public void setLocus(String newloc) {
			locus = newloc;
		}
	}

	public GeneSetHead( DistannFX app, GeneSet geneset ) {
		super();
		this.geneset = geneset;
		this.app = app;
	}
	
	final Set<Integer> filterset = new HashSet<>();
	final Set<Integer> genefilterset = new HashSet<>();
	
	final Label label = new Label();
	
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
	
	public void repaintGCSkew(List<Sequence> selclist, Graphics2D g2, int size, GeneGroup gg, String selspec ) {
		g2.setColor( Color.white );
		g2.fillRect(0, 0, 2048, 2048);
		Font f20 = g2.getFont().deriveFont(16.0f);
		Font f32 = g2.getFont().deriveFont(32.0f);
		int total = 0;
		/*int g = 0;
		int c = 0;
		double gcstotal = 0.0;*/
		int count = 0;
		Stroke basicStroke = new BasicStroke(40);
		Stroke oStroke = g2.getStroke();
		int alt = 0;
		double skewrad = 344.0*2;
		double middle = 1024.0;
		for( Sequence ctg : selclist ) {
			//Sequence ctg = clist.get( u );
			if( gg != null ) {
				for( Annotation tv : gg.getTegevals() ) {
					if( tv.getContshort() == ctg ) {					
						int i = tv.start;
						
						int x1 = (int)(middle+(skewrad-200.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
						int y1 = (int)(middle+(skewrad-200.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
						int x2 = (int)(middle+(skewrad+200.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
						int y2 = (int)(middle+(skewrad+200.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
						
						g2.setColor( Color.green );
						g2.drawLine(x1, y1, x2, y2);
					}
				}
			}
			
			double horn = total*2.0*Math.PI/size;
			double horn2 = (total+ctg.length())*2.0*Math.PI/size;

			int x1 = (int)(middle+(skewrad-200.0)*Math.cos( horn ));
			int y1 = (int)(middle+(skewrad-200.0)*Math.sin( horn ));
			int x2 = (int)(middle+(skewrad+280.0)*Math.cos( horn ));
			int y2 = (int)(middle+(skewrad+280.0)*Math.sin( horn ));
			g2.setColor( Color.black );
			//g2.drawLine(x1, y1, x2, y2);
			
			int xoff = (int)(middle+(skewrad+280.0)*Math.cos( horn2 ));
			int yoff = (int)(middle+(skewrad+280.0)*Math.sin( horn2 ));
			/*if( horn < Math.PI ) {
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
			}*/

			List<Annotation> alist = ctg != null ? ctg.getAnnotations() : null;
			if( alist != null ) for( Annotation a : alist ) {
				double ah = (total+a.getStart())*2*Math.PI/size;
				double ah2 = (total+a.getEnd())*2*Math.PI/size;
				double hornid = (ah+ah2)/2;

				g2.setStroke( basicStroke );
				Gene gn = a.getGene();
				if( gn != null && gn.getGeneGroup() != null ) {
					Cog cg = gn.getGeneGroup().getCog( geneset.cogmap );
					if( cg != null && Cog.charcogcol.containsKey(cg.symbol) ) {
						Color colr = Cog.charcogcol.get(cg.symbol);
						g2.setColor( colr );
					} else {
						g2.setColor( Color.gray );
					}
				} else {
					g2.setColor( Color.darkGray );
				}

				Annotation tegeval = gn != null ? gn.getTegeval() : null;
				if( tegeval != null && tegeval.type != null && tegeval.type.length() > 0 ) {
					int x = (int)(middle+(skewrad+120.0)*Math.cos( hornid ));
					int y = (int)(middle+(skewrad+120.0)*Math.sin( hornid ));
					g2.translate(x, y);
					g2.rotate(hornid + Math.PI / 2.0);
					if( tegeval.type.equalsIgnoreCase("rrna") ) g2.setColor( Color.red );
					else if( tegeval.type.equalsIgnoreCase("trna") ) g2.setColor( Color.darkGray );
					else g2.setColor( Color.blue );
					g2.fillRect(-1, 0, 2, 40);
					g2.rotate(-hornid - Math.PI / 2.0);
					g2.translate(-x, -y);
				} else {
					int x = (int)(middle+(skewrad+200.0)*Math.cos( hornid ));
					int y = (int)(middle+(skewrad+200.0)*Math.sin( hornid ));
					g2.translate(x, y);
					g2.rotate(hornid + Math.PI / 2.0);
					g2.fillRect(-1, a.isReverse() ? 40 : 0, 2, 40);
					g2.rotate(-hornid - Math.PI / 2.0);
					g2.translate(-x, -y);
				}

				if( a.designation != null && a.designation.length() > 0 ) {
					int x = (int)(middle+(skewrad+80.0)*Math.cos( hornid ));
					int y = (int)(middle+(skewrad+80.0)*Math.sin( hornid ));
					g2.translate( x, y );
					g2.rotate(hornid+Math.PI/2.0);
					g2.setColor( Color.cyan );
					g2.fillRect(-1,0,3,40);
					g2.rotate(-hornid-Math.PI/2.0);
					g2.translate( -x, -y );
				}

				//if( a.isReverse() ) g2.drawArc(100, 100, 824, 824, (int)(ah), (int)Math.max(1,(ah2-ah)));
				//else g2.drawArc(120, 120, 784, 784, (int)(ah), (int)Math.max(1,(ah2-ah)));
				g2.setStroke( oStroke );
			}

			g2.setFont( f20 );
			for( int i = 0; i < ctg.length(); i+=500 ) {
				/*for( int k = i; k < Math.min( ctg.length(), i+500 ); k++ ) {
					char chr = ctg.charAt( k );
					if( chr == 'g' || chr == 'G' ) {
						g++;
					} else if( chr == 'c' || chr == 'C' ) {
						c++;
					}
				}*/
				
				int gcount = 0;
				int ccount = 0;
				int acount = 0;
				int tcount = 0;
				for( int k = i; k < Math.min( ctg.length(), i+10000 ); k++ ) {
					char chr = k-5000 < 0 ? ctg.charAt( ctg.length()+(k-5000) ) : ctg.charAt(k-5000);
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
					
					//gcstotal += gcskew;

					x1 = (int)(middle+(skewrad)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y1 = (int)(middle+(skewrad)*Math.sin( (i+total)*2.0*Math.PI/size ));
					x2 = (int)(middle+(skewrad+gcskew*200.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y2 = (int)(middle+(skewrad+gcskew*200.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					
					if( gcskew >= 0 ) g2.setColor( Color.blue );
					else g2.setColor( Color.red );
					g2.drawLine(x1, y1, x2, y2);
				}
				
				if( acount > 0 || tcount > 0 ) {
					double atskew = (acount-tcount)/(double)(acount+tcount);

					double inskewrad = 280.0*2;
					x1 = (int)(middle+(inskewrad)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y1 = (int)(middle+(inskewrad)*Math.sin( (i+total)*2.0*Math.PI/size ));
					x2 = (int)(middle+(inskewrad+atskew*200.0)*Math.cos( (i+total)*2.0*Math.PI/size ));
					y2 = (int)(middle+(inskewrad+atskew*200.0)*Math.sin( (i+total)*2.0*Math.PI/size ));
					
					if( atskew >= 0 ) g2.setColor( Color.blue );
					else g2.setColor( Color.red );
					g2.drawLine(x1, y1, x2, y2);
				}

				if( count % 100 == 0 ) {
					double h = (i+total)*2.0*Math.PI/size;
					int x = (int)(middle+(400.0)*Math.cos( h ));
					int y = (int)(middle+(400.0)*Math.sin( h ));

					String str = (total+i)/1000+"kb";
					int strw = g2.getFontMetrics().stringWidth(str);
					g2.setColor( Color.black );
					g2.translate( x, y );
					g2.rotate(h+Math.PI/2.0);
					g2.fillRect(0,0,1,3);
					g2.rotate(-h-Math.PI/2.0);
					g2.translate( -x, -y );
					if( count % 500 == 0 ) {
						if( h < Math.PI/2 || h > 3*Math.PI/2 ) g2.drawString(str, x-strw, y);
						else g2.drawString(str, x, y);
					}
				}
				count++;
			}
			g2.setStroke( basicStroke );
			g2.setColor( alt++ % 2 == 0 ? Color.gray : Color.darkGray );
			g2.drawArc(104, 104, 1840, 1840, (int)(180*horn/Math.PI), (int)(180*(horn2-horn)/Math.PI));
			g2.setStroke( oStroke );
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
			else specsplit = new String[] {"Thermus", selspec.substring(0,firstDigitLocation).replace("eggertsoni","eggertssoniae"), selspec.substring(firstDigitLocation)};
		}
		
		int k = 0;
		for( String spec : specsplit ) {
			int strw = g2.getFontMetrics().stringWidth( spec );
			g2.drawString( spec, (2048-strw)/2, 2048/2 - specsplit.length*32/2 + 32 + k*32 );
			k++;
		}
		
		/*double gcs = gcstotal/total; //(g-c)/(g+c);
		String gcstr = Double.toString( Math.round( gcs*1000000.0 ) );
		g2.drawString( gcstr+"ppm", 768, 512 );*/
	}

	//static ClipboardService clipboardService;
	static boolean grabFocus;

	public static void copyData(Component source) {
		JTextArea textarea = (JTextArea) source;
		String s = textarea.getText();

		if (s == null || s.trim().length() == 0) {
			JOptionPane.showMessageDialog(source, "There is no data selected!");
		} else {
			StringSelection selection = new StringSelection(s);
			//clipboardService.setContents(selection);
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
	
	Map<String, Set<Integer>> selectionMap = new HashMap<>();
	public void fillSelectionSave(String json) throws JSONException {
		selectionMap.clear();
		selcomb.getItems().clear();
		
		System.err.println(json);
		JSONObject jsono = new JSONObject(json);
		Iterator<String> keys = jsono.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			System.err.println( "key: "+key );
			selcomb.getItems().add(key);
			JSONArray jo = jsono.getJSONArray(key);
			System.err.println( "array: "+jo.toString() );
			Set<Integer> selset = new HashSet<>();
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

	FilteredList<GeneGroup>					filteredData;
	SortedList<GeneGroup>					sortedData;
	SortedList<GeneGroup>					sortedData2;
	FilteredList<Gene>						geneFilteredList;
	SortedList<Gene>						geneSortedList;
	FilteredList<Function>					ffilteredData;
	SortedList<Function>					fsortedData;
	private void importStuff( Path zipp ) throws IOException {
		boolean fail = true;
		/*try {
			FileOpenService fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
			FileContents fc = fos.openFileDialog(null, null);
			is = fc.getInputStream();
		} catch( Exception e ) {
			fail = true;
		}*/

		if( fail && zipp == null ) {
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(null);
			if( f != null ) zipp = f.toPath();
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
			syncolorcomb.getItems().clear();
			syncolorcomb.getItems().add("");
			for( String spec : geneset.speccontigMap.keySet() ) {
				syncolorcomb.getItems().add( spec );
			}
			syncolorcomb.getItems().add("All");
			
			/*
			 * is = GeneSet.class.getResourceAsStream(""); Map<String,String> komap
			 * = koMapping( new FileReader("/home/sigmar/asgard-bio/data/ko"),
			 * funclist, genelist ); for( Function f : funclist ) { if(
			 * komap.containsKey( f.ec ) ) { for( String gn : f.geneentries ) { Gene
			 * g = refmap.get(gn); if( g.keggid == null ) g.keggid =
			 * komap.get(f.ec); } } }
			 */
	
			geneset.updateShareNum( geneset.specList );
			
			Set<String> allecs = new HashSet<>();
			for( Function f : geneset.funclist ) {
				if (f.getEc() != null)
					allecs.add(f.getEc());
			}
	
			for (String val : geneset.pathwaymap.keySet()) {
				Set<String> set = geneset.pathwaymap.get(val);
				for (String s : set) {
					if (allecs.contains(s)) {
						combo.getItems().add(val);
						break;
					}
				}
			}
	
			Set<String> set = new TreeSet<>();
			for (Gene g : geneset.genelist) {
				Annotation tv = g.getTegeval();
				if (tv.eval <= 0.00001 && tv.getSpecies() != null && tv.getSpecies().startsWith("[") && tv.getSpecies().endsWith("]"))
					set.add(tv.getSpecies());
			}
	
			for (String sp : set) {
				specombo.getItems().add(sp);
			}
			
			if( geneset.uclusterlist != null ) geneset.clusterMap = Serifier.initClusterNew(geneset.uclusterlist, null, null);
			//table.tableChanged( new TableModelEvent( table.getModel() ) );
			//ftable.tableChanged( new TableModelEvent( ftable.getModel() ) );
		//table.setModel( nullmodel );
			//ftable.setModel( nullmodel );
		//table.setModel( groupModel );
			//ftable.setModel( ftablemodel );
			//ftable.setItems( geneset.f);
			
			for( String spec : geneset.specList ) {
				TableColumn<GeneGroup, Teginfo> speccol = new TableColumn<>(spec);
				//speccol.getStyleClass().add("tabstyle");
				speccol.setCellFactory( cell -> {
					final TableCell<GeneGroup,Teginfo> tc = new TableCell<>() {
						@Override
						protected void updateItem(Teginfo item, boolean empty) {
							super.updateItem(item, empty);

							if (item == null || item.toString().length() == 0 || empty) {
								setText(null);
								setStyle("");
								//getStyleClass().remove("tabcellstyle");
							} else {
								setText(item.toString());
								cellRender(this, item, 0);
								//getStyleClass().add("tabcellstyle");
				                /*if( (this.getTableRow() != null && getTableRow().isSelected()) || isSelected() ) {
				                	//setTextFill( javafx.scene.paint.Color.WHITE );
				                	setStyle("-fx-background-color: darkgreen");
				                } else {
				                	//setTextFill( javafx.scene.paint.Color.BLACK );
				                	setStyle("-fx-background-color: green");
				                }*/
							}
						}
					};
					return tc;
				});
				speccol.setCellValueFactory( cellValue -> {
					GeneGroup gg = cellValue.getValue();
					Teginfo tes = gg.getTes( spec );
					return new ReadOnlyObjectWrapper(tes);
					//return new SimpleStringProperty( tes != null ? tes.toString() : "" );
					//Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
					//return new ObservableValue<String>( ret.toString() );
					//return ret;
				});
				speccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sortedData.comparatorProperty().bind(results.comparatorProperty()));
				results.getColumns().add( speccol );

				TableColumn<Gene, Teg> gspeccol = new TableColumn(spec);
				gspeccol.setComparator((o1, o2) -> {
					if( o1 == null ) {
						if( o2 == null ) {
							return 0;
						} else return -1;
					} else if( o1 instanceof Tegeval ) {
						if( o2 == null || o2 instanceof Teginfo ) {
							return 1;
						}
					} else {
						if( o2 == null ) {
							return 1;
						} else if( o2 instanceof Tegeval ) {
							return -1;
						}
					}
					return o1.compareTo(o2);
				});
				gspeccol.setCellFactory( cell -> {
					final TableCell<Gene,Teg> tc = new TableCell<>() {
						@Override
						protected void updateItem(Teg item, boolean empty) {
							super.updateItem(item, empty);

							if (item == null || item.toString().length() == 0 || empty) {
								setText("");
								setStyle("");
							} else {
								boolean render = cellRenderGene(this, item);
								setText(item.toString());
								if (!render) setStyle("");
								//getStyleClass().add("tabcellstyle");
				                /*if( (this.getTableRow() != null && getTableRow().isSelected()) || isSelected() ) {
				                	//setTextFill( javafx.scene.paint.Color.WHITE );
				                	setStyle("-fx-background-color: white");
				                } else {
				                	//setTextFill( javafx.scene.paint.Color.BLACK );
				                	setStyle("-fx-background-color: black");
				                }*/
							}
						}
					};
					return tc;
				});
				gspeccol.setCellValueFactory( cellValue -> {
					Gene g = cellValue.getValue();

					//Teginfo tes = g.tegeval;//getTes( spec );
					if( spec.equals(g.getSpecies()) ) {
						return new ReadOnlyObjectWrapper(g.getTegeval());
					} else {
						GeneGroup gg = g.getGeneGroup();
						Set<String> specset = gg.getSpecies();
						if( specset.contains(spec) ) {
							Teginfo ti = gg.getGenes(spec);
							return new ReadOnlyObjectWrapper(ti);
						}
					}
					//return new SimpleStringProperty( tes != null ? tes.toString() : "" );
					//Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
					//return new ObservableValue<String>( ret.toString() );
					//return ret;
					return null;
				});

				/*gspeccol.setCellValueFactory( cellValue -> {
					return new SimpleStringProperty( cellValue.getValue().toString() );
					//Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
					//return new ObservableValue<String>( ret.toString() );
					//return ret;
				});*/
				gresults.getColumns().add( gspeccol );
			}
			
			ObservableList<Function> ofunc = FXCollections.observableList( geneset.funclist );
			ffilteredData = new FilteredList<>(ofunc, p -> true);
			fsortedData = new SortedList<>( ffilteredData );
			fsortedData.comparatorProperty().bind(ftable.comparatorProperty());
			ftable.setItems( fsortedData );
			
			ObservableList<GeneGroup> ogenegroup = FXCollections.observableList( geneset.allgenegroups );
			filteredData = new FilteredList<>(ogenegroup, p -> true);
			sortedData = new SortedList<>( filteredData );
			//sortedData2 = new SortedList<>( filteredData );
			ScrollBar scrollBarOne = (ScrollBar)table.lookup(".scroll-bar:vertical");
			ScrollBar scrollBarTwo = (ScrollBar)results.lookup(".scroll-bar:vertical");
			scrollBarOne.valueProperty().bindBidirectional(scrollBarTwo.valueProperty());
			results.selectionModelProperty().bindBidirectional(table.selectionModelProperty());

			results.setOnMouseClicked(event -> {
				var y = event.getY();
				if (y<27) sortedData.comparatorProperty().bind(results.comparatorProperty());
			});
			table.setOnMouseClicked(event -> {
				var y = event.getY();
				if (y<27) sortedData.comparatorProperty().bind(table.comparatorProperty());
			});

			/*results.onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
				sortedData.comparatorProperty().bind(results.comparatorProperty());
			});
			table.onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
				sortedData.comparatorProperty().bind(table.comparatorProperty());
			});

			results.onMousePressedProperty().addListener((observable, oldValue, newValue) -> {
				sortedData.comparatorProperty().bind(results.comparatorProperty());
			});
			table.onMousePressedProperty().addListener((observable, oldValue, newValue) -> {
				sortedData.comparatorProperty().bind(table.comparatorProperty());
			});*/

			table.setItems( sortedData );
			results.setItems( sortedData );

			//Bindings.
			//sortedData.comparatorProperty().bindBidirectional(sortedData2.comparatorProperty());
			sortedData.comparatorProperty().bind(table.comparatorProperty());
			//sortedData.comparatorProperty().bind(results.comparatorProperty());
			//sortedData.comparatorProperty().bind
			//sortedData2.comparatorProperty().bind(results.comparatorProperty());


			scrollBarOne.setVisible(false);
			scrollBarOne.setMaxWidth(0.0);

			ObservableList<Gene> ogene = FXCollections.observableList( geneset.genelist );
			geneFilteredList = new FilteredList<>(ogene, p -> true);
			geneSortedList = new SortedList<>( geneFilteredList );

			/*gresults.setOnMouseClicked(event -> {
				geneSortedList.comparatorProperty().bind(gresults.comparatorProperty());
			});
			gtable.setOnMouseClicked(event -> {
				geneSortedList.comparatorProperty().bind(gtable.comparatorProperty());
			});*/

			/*gresults.getFocusModel().focusedItemProperty().addListener((observable, oldValue, newValue) -> {
				geneSortedList.comparatorProperty().bind(gresults.comparatorProperty());
			});
			gtable.getFocusModel().focusedItemProperty().addListener((observable, oldValue, newValue) -> {
				geneSortedList.comparatorProperty().bind(gtable.comparatorProperty());
			});*/

			//geneSortedList.comparatorProperty().bind(gresults.comparatorProperty());

			/*ScrollBar gscrollBarOne = (ScrollBar)gtable.lookup(".scroll-bar:vertical");
			ScrollBar gscrollBarTwo = (ScrollBar)gresults.lookup(".scroll-bar:vertical");
			gscrollBarOne.valueProperty().bindBidirectional(gscrollBarTwo.valueProperty());
			gresults.selectionModelProperty().bindBidirectional(gtable.selectionModelProperty());*/

			gresults.setOnMouseClicked(event -> {
				var y = event.getY();
				if (y<27) geneSortedList.comparatorProperty().bind(gresults.comparatorProperty());
			});
			gtable.setOnMouseClicked(event -> {
				var y = event.getY();
				if (y<27) geneSortedList.comparatorProperty().bind(gtable.comparatorProperty());
			});

			gtable.setItems( geneSortedList );
			gresults.setItems( geneSortedList );

			geneSortedList.comparatorProperty().bind(gtable.comparatorProperty());

			scrollBarOne.setVisible(false);
			scrollBarOne.setMaxWidth(0.0);

			int me = 0;
			int mu = 0;
			for( Gene g : geneset.genelist ) {
				if( g.getGeneGroup() != null ) me++;
				else mu++;
			}
			System.err.println( me + "  " + mu );

			gresults.getSelectionModel().setCellSelectionEnabled(true);
			results.getSelectionModel().setCellSelectionEnabled(true);
			gresults.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			results.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		
		String userhome = System.getProperty("user.home");
		File f = new File( userhome );
		File idf = new File(f, "idspec.txt");
		BufferedWriter bw = new BufferedWriter( new FileWriter(idf) ); //Files.newBufferedWriter(idf.toPath(), OpenOption.);
		for( String id : geneset.refmap.keySet() ) {
			Annotation a = geneset.refmap.get(id);
			Gene g = a.getGene();
			if(g != null) bw.write( id + "\t" + g.getSpecies() + "\n" );
		}
		bw.close();
	}
	
	public Serifier getConcatenatedSequencesMaxLength() {
		Map<String,Sequence>	smap = new HashMap<>();
		Set<String>				specset = new HashSet<>();
		Map<GeneGroup,Integer>	genegroups = new HashMap<>();
		//int[] rr = table.getSelectedRows();
		if( !isGeneview() ) {
			for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
				//int cr = table.convertRowIndexToModel(r);
				int max = 0;
				//GeneGroup gg = geneset.allgenegroups.get(cr);
				specset.addAll( gg.getSpecies() );
				
				for( Annotation tv : gg.getTegevals() ) {
					int l = tv.getAlignedSequence().length();
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		} else {
			for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
				//int cr = table.convertRowIndexToModel(r);
				//Gene g = geneset.genelist.get(cr);
				int max = 0;
				GeneGroup gg = g.getGeneGroup();
				specset.addAll( gg.getSpecies() );
				for( Annotation tv : gg.getTegevals() ) {
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
				List<Annotation> ltv = ggroup.getTegevals(selspec);
				
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
				for( Annotation tv : ltv ) {
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
		Set<String>							specset = new HashSet<>();
		Map<GeneGroup,Integer>				genegroups = new HashMap<>();
		//int[] rr = table.getSelectedRows();
		if( !isGeneview() ) {
			for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
				//int cr = table.convertRowIndexToModel(r);
				int max = 0;
				//GeneGroup gg = geneset.allgenegroups.get(cr);
				specset.addAll( gg.getSpecies() );
				
				for( Annotation tv : gg.getTegevals() ) {
					Sequence seq = tv.getAlignedSequence();
					int l = seq != null ? seq.length() : 0;
					if( l > max ) max = l;
				}
				genegroups.put( gg, max );
			}
		} else {
			for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
				//int cr = table.convertRowIndexToModel(r);
				//Gene g = geneset.genelist.get(cr);
				int max = 0;
				GeneGroup gg = g.getGeneGroup();
				specset.addAll( gg.getSpecies() );
				for( Annotation tv : gg.getTegevals() ) {
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
		JOptionPane.showMessageDialog(null, objs);
		
		doBlastn( ta.getText(), tf.getText(), false, null, show );
	}
	
	private void proxi(FilteredList genefilterset, boolean onlygenes) {
		if( !isGeneview() ) {
			genefilterset.setPredicate( p -> {
				GeneGroup thegg = (GeneGroup)p;
				for( GeneGroup gg : table.getSelectionModel().getSelectedItems() ) {
					if( thegg.index == gg.index ) return true;
					for( Annotation a : gg.genes ) {
						Annotation next = a.getNext();
						if( next != null ) {
							GeneGroup ngg = next.getGene().getGeneGroup();
							if( ngg != null ) if( thegg.index == ngg.index ) return true;
						}
						Annotation prev = a.getPrevious();
						if( prev != null ) {
							GeneGroup pgg = prev.getGene().getGeneGroup();
							if( pgg != null ) if( thegg.index == pgg.index ) return true;
						}
					}
				}
				return false;
			});
		} else {
			genefilterset.setPredicate( p -> {
				Gene thegene = (Gene)p;
				for (Gene gene : gtable.getSelectionModel().getSelectedItems()) {
					GeneGroup gg = gene.getGeneGroup();

					if (onlygenes) {
						if( thegene.index == gene.index) return true;
						Annotation next = gene.getTegeval().getNext();
						if (next != null) {
							Gene ng = next.getGene();
							if (ng != null) if( thegene.index == ng.index ) return true;
						}
						Annotation prev = gene.getTegeval().getPrevious();
						if (prev != null) {
							Gene pg = prev.getGene();
							if (pg != null) if( thegene.index == pg.index) return true;
						}
					} else {
						genefilterset.add(gg.index);
						for (Annotation a : gg.genes) {
							Annotation next = a.getNext();
							if (next != null) {
								GeneGroup ngg = next.getGene().getGeneGroup();
								if (ngg != null) if( thegene.index == ngg.index) return true;
							}
							Annotation prev = a.getPrevious();
							if (prev != null) {
								GeneGroup pgg = prev.getGene().getGeneGroup();
								if (pgg != null) if( thegene.index == pgg.index) return true;
							}
						}
					}
				}
				return false;
			});
		}
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
		JOptionPane.showMessageDialog(null, objs);
		
		doBlast( ta.getText(), tf.getText(), true, null, x );
	}

	public Set<String> getSelspec( GeneSetHead comp, final List<String> specs) {
		return getSelspec(comp, specs, false);
	}
	
	public Set<String> getSelspec( GeneSetHead comp, final List<String> specs, boolean custom, final JCheckBox ... contigs ) {
		final JTable	table = new JTable();
		table.setDragEnabled( true );
		JScrollPane	scroll = new JScrollPane( table );
		table.setAutoCreateRowSorter( true );

		final List<Sequence> ctgs = new ArrayList<>( geneset.contigmap.values() );
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
				return 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return columnIndex == 0 ? "Id" : "Spec";
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
				if( columnIndex == 1 ) return Sequence.nameFix(specs.get(rowIndex),true);
				else return specs.get(rowIndex);
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
		
		if( !custom && contigs != null && contigs.length > 0 && !contigs[0].getText().equals("Plasmid") ) contigs[0].addChangeListener(e -> {
			if( contigs[0] != null && contigs[0].isSelected() ) table.setModel( contigmodel );
			else table.setModel( specmodel );
		});
		
		Object[] ctls = new Object[] { scroll, contigs };
		//Object[] ctls2 = new Object[] { scroll };
		
		//if( contigs != null ) 
		JOptionPane.showMessageDialog( null, ctls );
		//else JOptionPane.showMessageDialog( comp, ctls2 );
		Set<String>	selspec = new LinkedHashSet<>();
		for( int i = 0; i < table.getRowCount(); i++ ) {
			if( table.isRowSelected(i) ) {
				String spec = (String)table.getValueAt(i, 0);
				System.err.println("test " + spec);
				selspec.add( spec );
			}
		}
		return selspec;
	}
	
	public TableView<GeneGroup> getGeneGroupTable() {
		return table;
	}
	
	public TableView<Gene> getGeneTable() {
		return gtable;
	}
	
	public TableView<Function> getFunctionTable() {
		return ftable;
	}
	
	int searchi = 0;
	public int searchTable( TableView table, String text, int i, boolean back, int ... columns ) {
		int v;
		if( back ) {
			v = i-1;
			if( v == -1 ) v = table.getItems().size();
		} else v = (i+1)%table.getItems().size();
		if( isGeneview() ) {
			while( v != i ) {
				//int m = table.convertRowIndexToModel(v);
				//if( m != -1 ) {
					//Gene g = geneset.genelist.get(m);
					//String name = column == 7 || column[0] == 8 ? g.getGeneGroup().getCommonSymbol() + ", " + g.getGeneGroup().getCommonKOName( ko2name ) + ", " +  : g.getGeneGroup().getCommonName();
					
				ObservableList<TableColumn> cc = table.getColumns();
				String name = "";
				for( int k : columns ) {
					TableColumn tc = cc.get(k);
					String val = tc.getCellObservableValue(v).toString();
					name += val;//table.getValueAt(v, k);
				}
				
				if( name.toLowerCase().contains( text ) ) {
					//int r = table.convertRowIndexToView(v);
					table.scrollTo(v);
					//Rectangle rect = table.getCellRect(v, 0, true);
					//table.scrollRectToVisible( rect );
					break;
				}
				if( back ) {
					v--;
					if( v == -1 ) v = table.getItems().size()-1;
				} else v = (v+1)%table.getItems().size();
					
				//} else break;
			}
		} else {
			while( v != i ) {
				//int m = table.convertRowIndexToModel(v);
				//if( m != -1 ) {
					
				//GeneGroup gg = allgenegroups.get(m);
				//String name = gg.getCommonName();
				ObservableList<TableColumn> cc = table.getColumns();
				String name = "";
				for( int k : columns ) {
					TableColumn tc = cc.get(k);
					String val = tc.getCellObservableValue(v).toString();
					name += val;
					//name += table.getValueAt(v, k);
				}
				
				if( name.toLowerCase().contains( text ) ) {
					//int r = table.convertRowIndexToView(i);
					//Rectangle rect = table.getCellRect(v, 0, true);
					//table.scrollRectToVisible( rect );
					table.scrollTo(v);
					break;
				}
				if( back ) {
					v--;
					if( v == -1 ) v = table.getItems().size()-1;
				} else v = (v+1)%table.getItems().size();
				
				//} else break;
			}
		}
		return v;
	}
	
	public void doBlast(final String fasta, final String evaluestr, final boolean ids, final GeneSet.RunnableResult rr, boolean x ) {
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
		String[] mcmds = {OS.contains("mac") ? "/usr/local/bin/makeblastdb" : "makeblastdb", "-dbtype", "prot", "-title", "tmp", "-out", "tmp" };
		List<String>	lcmd = new ArrayList<>(Arrays.asList(mcmds));
		
		final ProcessBuilder mpb = new ProcessBuilder( lcmd );
		mpb.redirectErrorStream( true );
		try {
			final Process mp = mpb.start();
			
			new Thread(() -> {
				try {
					OutputStream pos = mp.getOutputStream();
					Writer ow = new OutputStreamWriter( pos );
					for( Gene g : geneset.genelist ) {
						//if( g.getTag() == null || g.getTag().length() == 0 ) {
							GeneGroup gg = g.getGeneGroup();

							if( gg != null ) {
								String name;
								if( ids ) name = g.id;
								else {
									String addstr = "";
									Cog cog = gg.getCog( geneset.cogmap );
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
								Sequence sb = g.getTegeval().getProteinSequence();
								if( sb != null ) {
									sb.setName(name);
									sb.writeSequence(ow);
								}
								/*for( int i = 0; i < sb.length(); i+=70 ) {
									pos.write( sb.substring(i, Math.min( sb.length(), i+70) ).getBytes() );
								}
								pos.write( '\n' );*/
							}
						//}
					}
					ow.close();
					pos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
			
			new Thread(() -> {
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
			}).run();
			
			//File blastFile = x ? blastx : blastp; //dbType.equals("prot") ? type.equals("prot") ? blastp : blastx : blastn;
			
			String[] 		cmds = { OS.indexOf("mac") >= 0 ? "/usr/local/bin/blastp" : "blastp", "-query", "-", "-db", "tmp", "-evalue", evaluestr, "-num_threads", Integer.toString(procs) };
			lcmd = new ArrayList<String>( Arrays.asList(cmds) );
			//String[] exts = extrapar.trim().split("[\t ]+");
			
			ProcessBuilder pb = new ProcessBuilder( lcmd );
			pb.redirectErrorStream( true );
			final Process p = pb.start();
		
			final Thread t = new Thread(() -> {
				try {
					OutputStream pos = p.getOutputStream();
					pos.write( fasta.getBytes() );
					pos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			t.start();

			final Thread t2 = new Thread(() -> {
				try {
					System.err.println("WHY NOT");
					InputStreamReader rdr = new InputStreamReader(p.getInputStream());
					//FileReader fr = new FileReader( new File("c:/dot.blastout") );
					String res = "";
					BufferedReader br = new BufferedReader(rdr);
					String line = br.readLine();
					while (line != null) {
						//System.err.println( line );
						if (line.startsWith(">")) {
							int i = line.indexOf(' ', 2);
							if (i == -1) i = line.length();
							String id = line.substring(1, i).trim();

							Gene g = geneset.genemap.get(id);
							if (g != null) {
								if (!isGeneview()) {
						/*i = geneset.allgenegroups.indexOf( g.getGeneGroup() );
						if( i != -1 && i < table.getRowCount() ) {
							int r = table.convertRowIndexToView( i );
							table.addRowSelectionInterval(r, r);
						}*/
									Platform.runLater(() -> table.getSelectionModel().select(g.getGeneGroup()));
								} else {
						/*i = geneset.genelist.indexOf( g );
						if( i != -1 && i < table.getRowCount() ) {
							int r = table.convertRowIndexToView( i );
							table.addRowSelectionInterval(r, r);
						}*/
									Platform.runLater(() -> gtable.getSelectionModel().select(g));
								}
							}

							StringBuilder stuff = new StringBuilder(line + "\n");
							line = br.readLine();
							while (line != null && !line.startsWith("Query=") && !line.startsWith("> ")) {
								stuff.append(line).append("\n");
								line = br.readLine();
							}
							if (rr != null) {
								rr.run(stuff.toString());
								//res += line+"\n";
							}
						} else line = br.readLine();
					}
					br.close();

					//System.err.println("wn done");
					p.destroy();

					if (rr != null) rr.run("close");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			t2.start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public void doBlastn(final String fasta, final String evaluestr, final boolean ids, final GeneSet.RunnableResult rr, boolean show ) {
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
			
			new Thread(() -> {
				try {
					OutputStream pos = mp.getOutputStream();
					for( String cname : geneset.contigmap.keySet() ) {
						Sequence c = geneset.contigmap.get( cname );
						if( ids ) pos.write( (">" + c.getId() + "\n").getBytes() );
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
			}).start();
			
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

											Platform.runLater(() -> {
                                                if( !isGeneview() ) {
                                                /*int ggindex = geneset.allgenegroups.indexOf( gg );
                                                int i = table.convertRowIndexToView( ggindex );
                                                if( i != -1 ) table.addRowSelectionInterval(i, i);*/

													table.getSelectionModel().select( gg );
												} else {
                                                /*int gindex = geneset.genelist.indexOf( g );
                                                int i = table.convertRowIndexToView( gindex );
                                                table.addRowSelectionInterval(i, i);*/

													gtable.getSelectionModel().select( g );
												}
											});
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
						
						/*int k = 0;
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
	
	public StringBuffer getSelectedASeqs( TableView table, List<Gene> genelist, JPanel applet, Collection<String> species ) throws IOException {
		StringWriter sb = new StringWriter();
		
		Set<String> selectedSpecies = getSelspec( GeneSetHead.this, new ArrayList<>( species ) );
		for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
			//if (gg.species != null) {
			sb.append(gg.name + ":\n");
				//for (String sp : gg.species.keySet()) {
				//for( String sp : selectedSpecies ) {
			if( selectedSpecies.contains(species) ) {
				Annotation tv = gg.getTegeval();
				//sb.append(">" + tv.name + " " + tv.teg + " " + tv.eval + "\n");
				Sequence protseq = tv.getProteinSequence();
				protseq.setName( tv.getName() + " " + tv.getSpecies() + " " + tv.eval );
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

	/*int[]		currentRowSelection;
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
						}
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
	}*/
	
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
	
	RadioMenuItem	gb;
	RadioMenuItem	ggb;

	boolean isTableSelectListenerEnabled = true;
	TableView<GeneGroup>		table;
	TableView<GeneGroup>        results;
	TableView<Gene>				gtable;
	TableView<Gene>             gresults;
	TableView<Function>			ftable;
	TableModel	defaultModel;
	TableModel	groupModel;
	
	public void updateFilter(TableView table, Label label) {
		if (label != null)
			label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
	}
	
	public void updateFilter(JTable table, RowFilter filter, Label label) {
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
			gc.draw(g2, null, GeneSetHead.this, 1024, 1024, null, geneset.specList, blosumap, table.getItems().size(), 0 );
		}
		g2.dispose();
		
		return bimg;
	}
	
	public void updateFilter(int val, String ustr, TableView table, RowFilter filter, Set<Integer> filterset, Label label, int ... ind ) {
		filterset.clear();
		/*TableModel model = table.getModel();
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
		}*/
		
		updateFilter(table, label);
	}
	
	public void updateFilter(int val, String ustr, JTable table, RowFilter filter, Set<Integer> filterset, Label label, int ... ind ) {
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
					for( Annotation a : gg.genes ) {
						Gene g = a.getGene();
						if( g != null && g.getRefid().toLowerCase().contains(ustr) ) {
							filterset.add(i);
							break;
						}
					}
					i++;
				}
			} else {
				for( Gene g : geneset.genelist ) {
					if( g.getRefid().toLowerCase().contains(ustr) ) {
						filterset.add(i);
					}
					i++;
				}
			}
		}
		
		updateFilter(table, filter, label);
	}
	
	public void showAlignedSequences( Component comp, Serifier	serifier ) {
		SwingUtilities.invokeLater(() -> {
            JFrame frame1 = new JFrame();
            frame1.setSize(800, 600);
            frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JavaFasta jf = new JavaFasta( null, serifier, geneset.cs );
            jf.initGui(frame1);
            jf.updateView();

            frame1.setVisible(true);
        });
	}
	
	public void showSomeSequences( Serifier serifier ) {
		SwingUtilities.invokeLater(() -> {
            JFrame frame1 = new JFrame();
            frame1.setSize(800, 600);
            frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JavaFasta jf = new JavaFasta( null, serifier, geneset.cs );
            jf.initGui(frame1);

            /*for( String contig : contset.keySet() ) {
                Sequence seq = contset.get(contig);
                serifier.addSequence(seq);
                if (seq.getAnnotations() != null)
                    Collections.sort(seq.getAnnotations());
            }*/
            jf.getSerifier().checkMaxMin();
            jf.updateView();

            frame1.setVisible(true);
        });
	}
	
	public void showSelectedSequences( Set<Annotation> tset, boolean dna, String names ) {
		SwingUtilities.invokeLater(() -> {
            JFrame frame1 = new JFrame();
            frame1.setSize(800, 600);
            frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Serifier	serifier = new Serifier();
            JavaFasta jf = new JavaFasta( null, serifier, geneset.cs );
            jf.initGui(frame1);

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

            frame1.setVisible(true);
        });
	}
	
	public void showSequences( Set<GeneGroup> ggset, boolean dna, Set<String> specs ) {
		showSequences(ggset, dna, specs, EnumSet.of(ToShow.STRAINS));
	}

	enum ToShow {
		STRAINS,
		GENES,
		IDS
	}

	public void showSequences( Set<GeneGroup> ggset, boolean dna, Set<String> specs, EnumSet<ToShow> toShow ) {
		SwingUtilities.invokeLater(() -> {
            JFrame frame1 = new JFrame();
            frame1.setSize(800, 600);
            frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Serifier	serifier = new Serifier();
            JavaFasta jf = new JavaFasta( null, serifier, geneset.cs );
            jf.initGui(frame1);

            for( GeneGroup ggroup : ggset ) {
            //for( Gene gg : ggroup.genes ) {
                //if (gg.species != null) {
                    //for (String sp : gg.species.keySet()) {
                    //	Teginfo stv = gg.species.get(sp);
                for( Annotation tv : ggroup.getTegevals() ) {
                    Sequence cont = tv.getContshort();
                    if( cont != null ) {
                        String selspec = cont.getSpec();//tv.getContig();
						if(toShow.containsAll(EnumSet.of(ToShow.STRAINS,ToShow.IDS))) {
							Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
							seq.setName( selspec + "-" + tv.getGeneGroup().getRefid() );
							serifier.mseq.put( seq.getName(), seq );
							//Sequence seq = new Sequence( tv.gene != null ? tv.gene.name : tv.name, seqstr, serifier.mseq );
							serifier.addSequence(seq);
						} else if(toShow.contains(ToShow.IDS)) {
							Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
							seq.setName( tv.getId() );
							serifier.mseq.put( seq.getName(), seq );
							//Sequence seq = new Sequence( tv.gene != null ? tv.gene.name : tv.name, seqstr, serifier.mseq );
							serifier.addSequence(seq);
						} else if(toShow.contains(ToShow.GENES)) {
                            Sequence seq = dna ? tv.createSequence() : tv.getProteinSequence();
                            seq.setName( tv.getGene() != null ? tv.getGene().name : tv.getName() );
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

            frame1.setVisible(true);
        });
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
	
	public boolean isGeneview() {
		return splitpane.getItems().contains( gsplit );
	}
	
	public int getSelectionSize() {
		return isGeneview() ? gtable.getSelectionModel().getSelectedIndices().size() : table.getSelectionModel().getSelectedIndices().size();
	}
	
	public void cogBlastDlg( Set<String> species, boolean pfam ) {
		Dialog<String> cogdlg = new Dialog();
		
		ToggleGroup group = new ToggleGroup();
		VBox vbox = new VBox();
		RadioButton docker = new RadioButton("docker");
		docker.setToggleGroup( group );
		RadioButton local = new RadioButton("local");
		local.setToggleGroup( group );
		docker.setSelected( true );
		TextField dbpath = new TextField("/cdd_delta");
		TextField host = new TextField("geneset");
		
		vbox.getChildren().add( docker );
		vbox.getChildren().add( local );
		vbox.getChildren().add( dbpath );
		vbox.getChildren().add( host );
		
		cogdlg.getDialogPane().setContent( vbox );
		
		/*String dbPath = "/data/Cog";
		JTextField tf = new JTextField( dbPath );
		JTextField host = new JTextField("localhost");
		JOptionPane.showMessageDialog( null, new Object[] {tf, host} );*/
		
		cogdlg.getDialogPane().getButtonTypes().add( ButtonType.OK );
		cogdlg.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
		cogdlg.setResultConverter(param -> {
			if( !param.getButtonData().isCancelButton() ) {
		        return dbpath.getText();
			}
			return "";
		});
		Optional<String> ostr = cogdlg.showAndWait();
		if( ostr.isPresent() ) {
        	String str = ostr.get();
        	if( str.length() > 0 ) SwingUtilities.invokeLater(() -> {
        		geneset.cogBlast( species, dbpath.getText(), host.getText(), false, docker.isSelected(), pfam );
        	});
        }
	}
	
	TextField tb1 = null;
	TextField tb2 = null;
	TextField epar = null;
	final JFXPanel fxpanel = new JFXPanel();
	Scene fxs = null;
	
	BufferedImage bimg;
	
	ComboBox<String> selcomb;
	ComboBox<String> searchcolcomb;
	ComboBox<String> syncolorcomb;
	
	ComboBox<String> 						specombo;
	ComboBox<String> 						combo;
	
	SplitPane		splitpane;
	SplitPane		ggsplit;
	SplitPane		gsplit;
	Stage			primaryStage;

	Component comp;
	public void init(final Stage primaryStage, final Container comp, final SplitPane splitpane, final SplitPane ggsplit, final SplitPane gsplit, final TableView<Gene> genetable, final TableView<Function> upper, final TableView<GeneGroup> lower, final TableView<GeneGroup> ggresults, final TableView<Gene> g_results, final MenuBar menubar, final ToolBar toolbar, final ToolBar btoolbar ) {
		geneset.user = System.getProperty("user.name");
		JavaFasta.user = geneset.user;
		this.splitpane = splitpane;
		this.primaryStage = primaryStage;
		this.ggsplit = ggsplit;
		this.gsplit = gsplit;

		table = lower;
		results = ggresults;
		gtable = genetable;
		gresults = g_results;

		table.setEditable(true);

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
			InputStream isk = GeneSet.class.getClassLoader().getResourceAsStream("org/simmi/javafasta/genesetkey");
			//Path gkey = Paths.get( url.toURI() );
			InputStream iskp = GeneSet.class.getClassLoader().getResourceAsStream("org/simmi/javafasta/genesetkey.pub");
			//Path gkeypub = Paths.get( url.toURI() );
			
			Path gkeyssh = Paths.get(userhome);
			//Path gkeyssh = userpath.resolve(".ssh");
			if( !Files.exists(gkeyssh) ) Files.createDirectory(gkeyssh);
			Path gkeylocal = gkeyssh.resolve("org/simmi/javafasta/genesetkey");
			Path gkeylocalpub = gkeyssh.resolve("org/simmi/javafasta/genesetkey.pub");
			if( !Files.exists(gkeylocal) && isk != null ) {
				Files.copy(isk, gkeylocal, StandardCopyOption.REPLACE_EXISTING);
			}
			if( !Files.exists(gkeylocalpub) && iskp != null ) {
				Files.copy(iskp, gkeylocalpub);
			}
			
			Set<PosixFilePermission> poset = new HashSet<>();
			poset.add( PosixFilePermission.OWNER_READ );
			poset.add( PosixFilePermission.OWNER_WRITE );
			if( Files.exists(gkeylocal) ) Files.setPosixFilePermissions(gkeylocal, poset);
			if( Files.exists(gkeylocalpub) ) Files.setPosixFilePermissions(gkeylocalpub, poset);
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
		selcomb = new ComboBox<>();
		searchcolcomb = new ComboBox<>();
		syncolorcomb = new ComboBox<>();
		
		searchcolcomb.getItems().add("Name");
		searchcolcomb.getItems().add("Symbol");
		searchcolcomb.getSelectionModel().select(0);
		
		setColors();

		JMenuBar	jmenubar = new JMenuBar();
		Menu		file = new Menu("File");
		
		MenuItem newitem = new MenuItem("New");
		newitem.setOnAction( actionEvent -> newFile() );
		file.getItems().add( newitem );
		
		MenuItem openitem = new MenuItem("Open");
		openitem.setOnAction( actionEvent -> {
			try {
				importStuff( null );
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		});
		file.getItems().add( openitem );	
		file.getItems().add( new SeparatorMenuItem() );

		MenuItem excelitem = new MenuItem("Open selection in Excel");
		excelitem.setOnAction(actionEvent -> ExcelUtils.excelExport( GeneSetHead.this, geneset.projectname, false ));
		file.getItems().add( excelitem );

		MenuItem exseqitem = new MenuItem("Open sequences in Excel");
		exseqitem.setOnAction(actionEvent -> ExcelUtils.excelExport( GeneSetHead.this, geneset.projectname, true ));
		file.getItems().add( exseqitem );

		MenuItem excentitem = new MenuItem("Open center in Excel");
		excentitem.setOnAction(actionEvent -> ExcelUtils.excelCenterExport( GeneSetHead.this, geneset.projectname, true ));
		file.getItems().add( excentitem );

		file.getItems().add( new SeparatorMenuItem() );
		
		MenuItem importitem = new MenuItem("Import genomes");
		importitem.setOnAction( actionEvent -> fetchGenomes() );
		file.getItems().add( importitem );
		
		MenuItem exportitem = new MenuItem("Export genomes");
		exportitem.setOnAction( actionEvent -> exportGenomes( geneset.speccontigMap, false ) );
		file.getItems().add( exportitem );

		MenuItem exportfastaitem = new MenuItem("Export genomes fasta");
		exportfastaitem.setOnAction( actionEvent -> exportGenomes( geneset.speccontigMap, true ) );
		file.getItems().add( exportfastaitem );
		
		file.getItems().add( new SeparatorMenuItem() );
		
		MenuItem exportproteinitem = new MenuItem("Export protein sequences");
		exportproteinitem.setOnAction(actionEvent -> {
			try {
				exportProteinSequences(geneset.genelist);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		file.getItems().add( exportproteinitem );	
		
		MenuItem exportgeneitem = new MenuItem("Export gene clusters");
		exportgeneitem.setOnAction(actionEvent -> {
			try {
				exportGeneClusters(geneset.allgenegroups);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		file.getItems().add( exportgeneitem );

		MenuItem exportrepgeneitem = new MenuItem("Export representative gene sequence");
		exportrepgeneitem.setOnAction(actionEvent -> {
			try {
				exportRepGeneSeq(geneset.allgenegroups);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		file.getItems().add( exportrepgeneitem );
		
		file.getItems().add( new SeparatorMenuItem() );
		
		MenuItem quititem = new MenuItem("Quit");
		quititem.setOnAction( actionEvent -> System.exit( 0 ) );
		file.getItems().add( quititem );
		
		Menu		edit = new Menu("Edit");
		MenuItem	clustergenes = new MenuItem("Cluster genes");
		clustergenes.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
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

                Set<String> species = getSelspec(null, geneset.getSpecies(), false,null);
				try {
					geneset.clusterGenes( species, false );
				} catch (IOException e) {
					e.printStackTrace();
				}
				//}
            });
		});
		MenuItem	alignclusters = new MenuItem("Align clusters");
		alignclusters.setOnAction(new AlignClusterAction(this));

		MenuItem	sharenumaction = new MenuItem("Update share numbers");
		sharenumaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            Set<String> specs = getSelspec(GeneSetHead.this, geneset.specList, false, null);
            geneset.updateShareNum(specs);
        }));
		
		MenuItem	importgeneclusteringaction = new MenuItem("Import gene clustering");
		importgeneclusteringaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            panel.setLayout( grid );

            JLabel label1 = new JLabel("Id:");
            JTextField tb11 = new JTextField("0.5");
            JLabel label2 = new JLabel("Len:");
            JTextField tb21 = new JTextField("0.5");

            Dimension d = new Dimension( 300, 30 );
            JTextField epar1 = new JTextField();
            epar1.setSize( d );
            epar1.setPreferredSize( d );

            JCheckBox	fromscratch = new JCheckBox("From scratch");
            fromscratch.setSelected(true);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = 1;
            c.gridheight = 1;

            c.gridx = 0;
            c.gridy = 0;
            panel.add( label1, c );
            c.gridx = 1;
            c.gridy = 0;
            panel.add(tb11, c );
            c.gridx = 0;
            c.gridy = 1;
            panel.add( label2, c );
            c.gridx = 1;
            c.gridy = 1;
            panel.add(tb21, c );
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            panel.add(epar1, c );
            c.gridx = 0;
            c.gridy = 3;
            c.gridwidth = 2;
            panel.add(fromscratch, c);

            JOptionPane.showMessageDialog(comp, new Object[] {panel}, "Clustering parameters", JOptionPane.PLAIN_MESSAGE );

            float id = Float.parseFloat( tb11.getText() );
            float len = Float.parseFloat( tb21.getText() );

            //JFileChooser fc = new JFileChooser();
            //if( fc.showOpenDialog( GeneSetHead.this ) == JFileChooser.APPROVE_OPTION ) {
            Serifier s = new Serifier();
            //s.mseq = aas;
            for( String gk : geneset.refmap.keySet() ) {
                Annotation a = geneset.refmap.get( gk );
                //if( a.getAlignedSequence() != null ) System.err.println( g.tegeval.getAlignedSequence().getName() );
                s.mseq.put( gk, a.getAlignedSequence() );
            }

            Map<String,String>	idspec = new HashMap<>();
            for( String idstr : geneset.refmap.keySet() ) {
                if( idstr.contains(" ") ) {
                    System.err.println( "coooonnnnnni " + idstr );
                }

                Annotation a = geneset.refmap.get( idstr );
                Gene gene = a.getGene();
                idspec.put(idstr, gene.getSpecies());
            }
            //Sequences seqs = new Sequences(user, name, type, path, nseq)
            try {
                Map<String,String> env = new HashMap<>();
                env.put("create", "true");
                String uristr = "jar:" + geneset.zippath.toUri();
                geneset.zipuri = URI.create( uristr );
                try(FileSystem zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env )) {
					for (Path root : zipfilesystem.getRootDirectories()) {
						List<Path> p = Files.list(root).filter(t -> {
							String fname = t.getFileName().toString();
							return fname.endsWith(".blastout");
						}).collect(Collectors.toList());

						List<Set<String>> cluster = (geneset.uclusterlist == null || fromscratch.isSelected()) ? new ArrayList<>() : new ArrayList<>(geneset.uclusterlist);
						s.makeBlastCluster(root, p, 1, id, len, idspec, cluster, geneset.refmap);

						System.err.println(cluster.get(0));
						if (geneset.uclusterlist != null) System.err.println(geneset.uclusterlist.get(0));
					}
				}
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		MenuItem importgenesymbolaction = new MenuItem("Import gene symbols");
		importgenesymbolaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            JFileChooser fc = new JFileChooser();
            if( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
                try {
                    Map<String,String> env = new HashMap<>();
                    env.put("create", "true");
                    //Path path = zipfile.toPath();
                    String uristr = "jar:" + geneset.zippath.toUri();
                    geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
                    geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );

                    Path nf = geneset.zipfilesystem.getPath("/smap_short.txt");
                    BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE);

                    File f = fc.getSelectedFile();
                    InputStream is = new FileInputStream( f );
                    if( f.getName().endsWith(".gz") ) is = new GZIPInputStream( is );
                    geneset.uni2symbol(new InputStreamReader(is), bw, geneset.unimap);

                    bw.close();
                    //long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
                    geneset.zipfilesystem.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }));
		
		MenuItem	importcazyaction = new MenuItem("Import Cazy");
		importcazyaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            JFileChooser fc = new JFileChooser();
            if( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
                try {
                    BufferedReader rd = Files.newBufferedReader(fc.getSelectedFile().toPath());
                    geneset.loadcazymap(geneset.cazymap, rd);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }));

		MenuItem	gene2refseqaction = new MenuItem("Gene 2 refseq");
		gene2refseqaction.setOnAction( actionEvent -> {
			try {
				TextField tf = new TextField();
				tf.setText("ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/gene2refseq.gz");
				Dialog<Path> dialog = new Dialog();
				dialog.getDialogPane().setContent( tf );
				dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
				dialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
				dialog.setResultConverter(param -> param.getButtonData().isCancelButton() ? null : Paths.get(URI.create(tf.getText())));
				Optional<Path> opath = dialog.showAndWait();

				if( opath.isPresent() ) {
					Map<String, String> env = new HashMap<>();
					env.put("create", "true");
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create(uristr /*.replace("file://", "file:")*/);
					geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);

					Path path = opath.get();
					InputStream is = path.toUri().toURL().openStream();
					if( path.getFileName().toString().endsWith(".gz") ) {
						is = new GZIPInputStream( is );
					}
					Path nf = geneset.zipfilesystem.getPath("/gene2refseq_short.txt");
					BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
					geneset.idMapping(new InputStreamReader(is), bw, 5, 1, geneset.refmap, null, geneset.gimap);
					bw.close();
				}
			} catch( Exception e ) {
				e.printStackTrace();
			} finally {
				try {
					geneset.zipfilesystem.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		MenuItem	functionmappingaction = new MenuItem("Function mapping");
		functionmappingaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            final JTextField 	tf = new JTextField();
            JButton				btn = new JButton("File");
            JComponent 			comp2 = new JComponent() {};
            comp2.setLayout( new BorderLayout() );
            comp2.add( tf );
            comp2.add(btn, BorderLayout.EAST);
            tf.setText("http://130.208.252.239/data/sp2go.txt.gz");

            final File[] file2 = new File[1];
            btn.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                if( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
                    file2[0] = fc.getSelectedFile();
                    try {
                        tf.setText( fc.getSelectedFile().toURI().toURL().toString() );
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            try {
                Map<String,String> env = new HashMap<>();
                env.put("create", "true");
                //Path path = zipfile.toPath();
                String uristr = "jar:" + geneset.zippath.toUri();
                geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
                geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );

                Path nf = geneset.zipfilesystem.getPath("/sp2go_short.txt");
                BufferedWriter bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

                JOptionPane.showMessageDialog(null, comp2);

                final JDialog	dialog = new JDialog();
                dialog.setTitle( "Function mapping" );
                dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
                dialog.setSize(400, 300);

                comp2 = new JComponent() {};
                comp2.setLayout( new BorderLayout() );

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
                final Thread t = new Thread(() -> {
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
				});

                ta.setEditable( false );
                final JScrollPane	sp = new JScrollPane( ta );

                dialog.add( comp2 );
                comp2.add( pbar, BorderLayout.NORTH );
                comp2.add( sp, BorderLayout.CENTER );
                pbar.setIndeterminate( true );

                t.start();
                /*okokdialog.addWindowListener( new WindowListener() {

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
                });*/
                dialog.setVisible( true );
                //long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		MenuItem	importidmappingaction = new MenuItem("Import idmapping");
		importidmappingaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            final JTextField 	tf = new JTextField();
            JButton				btn = new JButton("File");
            JComponent 			comp2 = new JComponent() {};
            comp2.setLayout( new BorderLayout() );
            comp2.add( tf );
            comp2.add(btn, BorderLayout.EAST);
            tf.setText("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping.dat.gz");

            final File[] file2 = new File[1];
            btn.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                if( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
                    file2[0] = fc.getSelectedFile();
                    try {
                        tf.setText( fc.getSelectedFile().toURI().toURL().toString() );
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            JOptionPane.showMessageDialog(null, comp2);

            //Thread t = new Thread() {
            //	public void run() {
            try {
                Map<String,String> env = new HashMap<>();
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

                comp2 = new JComponent() {};
                comp2.setLayout( new BorderLayout() );

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
                final Thread t = new Thread(() -> {
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
				});

                ta.setEditable( false );
                final JScrollPane	sp = new JScrollPane( ta );

                dialog.add( comp2 );
                comp2.add( pbar, BorderLayout.NORTH );
                comp2.add( sp, BorderLayout.CENTER );
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

                /*okokdialog.addWindowListener( new WindowListener() {

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
                            }*

                            pbar.setIndeterminate( false );
                            pbar.setEnabled( false );
                        }
                    }

                    @Override
                    public void windowActivated(WindowEvent e) {}
                });*/
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
        }));
		MenuItem	cogblastaction = new MenuItem("Cog blast");
		cogblastaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
			Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);

			if( species != null && species.size() > 0 ) Platform.runLater(() -> cogBlastDlg( species, false ));
        }));
		MenuItem	pfamblastaction = new MenuItem("Pfam blast");
		pfamblastaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
			Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);

			if( species != null && species.size() > 0 ) Platform.runLater(() -> cogBlastDlg( species, true ));
		}));
		MenuItem	unresolvedblastaction = new MenuItem("Unresolved blast");
		unresolvedblastaction.setOnAction(  actionEvent -> SwingUtilities.invokeLater(() -> {
			Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);

			Platform.runLater(() -> {
				try {
					StringWriter sb = new StringWriter();
					ObservableList<Gene> genes = gtable.getSelectionModel().getSelectedItems();
					if (genes.size() > 0) {
						if (isGeneview()) {
							for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
								//int i = table.convertRowIndexToModel(r);
								//Gene g = geneset.genelist.get(i);
								Sequence gs = g.getTegeval().getProteinSequence();
								gs.setName(g.id);
								gs.writeSequence(sb);
							}
						} else {
							for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
								//int i = table.convertRowIndexToModel(r);
								//GeneGroup gg = geneset.allgenegroups.get(i);
								Annotation a = null;
								for (Annotation anno : gg.genes) {
									a = anno;
									break;
								}
								Sequence gs = a.getProteinSequence();
								gs.setName(a.getId());
								gs.writeSequence(sb);
							}
						}
					} else {
						for (Gene g : geneset.genelist) {
							if (g.getTag() == null || g.getTag().equalsIgnoreCase("gene")) {
								if (species.contains(g.getSpecies())) {
									Sequence gs = g.getTegeval().getProteinSequence();
									gs.setName(g.id);
									gs.writeSequence(sb);

/*sb.append(">" + g.id + "\n");
for (int i = 0; i < gs.length(); i += 70) {
sb.append( gs.substring(i, Math.min( i + 70, gs.length() )) + "\n");
}*/
								}
							}
						}
					}

					Map<String, String> env = new HashMap<>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create(uristr /*.replace("file://", "file:")*/);
					geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
					Path resPath = geneset.zipfilesystem.getPath("/unresolved.blastout");

					Path dbPath = Paths.get("/data/nr");

					NativeRun nrun = new NativeRun();
					blastpRun(nrun, sb.getBuffer(), dbPath, resPath, "-evalue 0.00001", null, true, geneset.zipfilesystem, geneset.user, primaryStage);
				} catch( Exception e ) {
					e.printStackTrace();
				}
			});
        }));
		MenuItem	hhimportaction = new MenuItem("hhimport");
		hhimportaction.setOnAction(  actionEvent -> {
			var fileChooser = new FileChooser();
			var files = fileChooser.showOpenMultipleDialog(null);
			//files.stream().map(f -> f)
		});
		MenuItem	hhblitsaction = new MenuItem("hhblits pdb70");
		hhblitsaction.setOnAction(  actionEvent -> SwingUtilities.invokeLater(() -> {
			//Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);
			Platform.runLater(() -> HHBlitsTools.runHHBlits(GeneSetHead.this, GeneSet.HHBLITS_PATH, "/Volumes/Untitled/pdb70/pdb70", false));
		}));
		MenuItem	hhblitsuniaction = new MenuItem("hhblits uniref");
		hhblitsuniaction.setOnAction(  actionEvent -> SwingUtilities.invokeLater(() -> {
			//Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);
			Platform.runLater(() -> HHBlitsTools.runHHBlits(GeneSetHead.this, GeneSet.HHBLITS_PATH, "/Volumes/Untitled/UniRef30_2020_06", false));
		}));

        MenuItem	hhblitsphrogaction = new MenuItem("hhblits phrogref");
        hhblitsphrogaction.setOnAction(  actionEvent -> SwingUtilities.invokeLater(() -> {
            //Set<String> species = getSelspec(null, geneset.getSpecies(), false, null);
            Platform.runLater(() -> HHBlitsTools.runHHBlits(GeneSetHead.this, GeneSet.HHBLITS_PHROG_PATH, "/Volumes/Untitled/phrog", false));
        }));


		MenuItem	registerlocusidaction = new MenuItem("Register locus id");
		registerlocusidaction.setOnAction( actionEvent -> {
			try {
				TableView<Locus> tv = new TableView<>();
				tv.setEditable(true);

				TableColumn<Locus, String> specCol = new TableColumn("Species");
				specCol.setEditable(false);
				TableColumn<Locus, String> locusCol = new TableColumn("Locus");
				locusCol.setEditable(true);

				tv.getColumns().add(specCol);
				tv.getColumns().add(locusCol);

				specCol.setCellValueFactory(new PropertyValueFactory<>("species"));
				locusCol.setCellValueFactory(new PropertyValueFactory<>("locus"));

				ObservableList<Locus> o = FXCollections.observableArrayList();
				geneset.locusidmap.entrySet().forEach(s -> o.add(new Locus(s.getKey(), s.getValue())));
				geneset.getSpecies().stream().filter(s -> !geneset.locusidmap.containsKey(s)).forEach(s -> o.add(new Locus(s, "")));
				tv.setItems(o);

				Dialog dialog = new Dialog();
				dialog.getDialogPane().setContent(tv);
				dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
				dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
				dialog.showAndWait();

				Map<String, String> env = new HashMap<>();
				env.put("create", "true");
				String uristr = "jar:" + geneset.zippath.toUri();
				geneset.zipuri = URI.create(uristr /*.replace("file://", "file:")*/);
				geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
				Path resPath = geneset.zipfilesystem.getPath("/locusids.txt");
				String res = o.stream().filter(loc -> loc.locus != null && loc.locus.length() > 0).map(loc -> loc.species + "\t" + loc.locus).collect(Collectors.joining("\n"));
				Files.write(resPath, res.getBytes());
				geneset.zipfilesystem.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		MenuItem	importbiosystemsaction = new MenuItem("Import biosystems");
		importbiosystemsaction.setOnAction( actionEvent -> {
			Dialog<Map<String,Set<String>>> dialog = new Dialog();

			TextField gene = new TextField();
			TextField biosys = new TextField();

			gene.setText("ftp://ftp.ncbi.nlm.nih.gov/pub/biosystems/biosystems.20160519/biosystems_gene_all.gz");
			biosys.setText("ftp://ftp.ncbi.nlm.nih.gov/pub/biosystems/biosystems.20160519/bsid2info.gz");

			VBox	duo = new VBox();
			duo.getChildren().add( gene );
			duo.getChildren().add( biosys );

			dialog.getDialogPane().setContent( duo );
			dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
			dialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
			dialog.setResultConverter(param -> {
				if( !param.getButtonData().isCancelButton() ) {
					Map<String,Set<String>> result = null;
					Set<String> geneidset = geneset.genelist.stream().map( g -> g.genid ).collect(Collectors.toSet());
					try {
						InputStream p = new URI(gene.getText()).toURL().openStream();
						InputStream b = new URI(biosys.getText()).toURL().openStream();

						if( gene.getText().endsWith(".gz") ) p = new GZIPInputStream(p);
						if( biosys.getText().endsWith(".gz") ) b = new GZIPInputStream(b);

						Map<String,List<String[]>> group = new BufferedReader(new InputStreamReader(p)).lines().map(l -> l.split("\t")).filter(s -> geneidset.contains(s[1])).collect( Collectors.groupingBy(s->s[1]) );
						Set<String> bsids = group.entrySet().stream().flatMap( e -> e.getValue().stream() ).map( s -> s[0] ).collect(Collectors.toSet());
						Map<String,String> bsid2name = new BufferedReader(new InputStreamReader(b)).lines().map( s -> s.split("\t") ).filter( s -> bsids.contains(s[0]) ).collect( Collectors.toMap(s->s[0],s->s[2]+":"+s[3]) );
						result = group.entrySet().stream().collect( Collectors.toMap(Map.Entry::getKey, s->s.getValue().stream().map(sub-> bsid2name.get(sub[0]) ).collect(Collectors.toSet())) );
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
					//Path p = Paths.get(gene.getText());
					//Path b = Paths.get(biosys.getText());
					return result;
				}
				return null;
			});
			Optional<Map<String,Set<String>>> od = dialog.showAndWait();

			if( od.isPresent() ) {
				geneset.biosystemsmap = od.get();
				Map<String,String> env = new HashMap<>();
				env.put("create", "true");
				String uristr = "jar:" + geneset.zippath.toUri();
				geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
					Path resPath = geneset.zipfilesystem.getPath("/biosystems.txt");
					BufferedWriter bw = Files.newBufferedWriter( resPath, StandardOpenOption.TRUNCATE_EXISTING );
					geneset.biosystemsmap.entrySet().stream().forEach(e -> {
						try {
							bw.write(e.getKey() + "\t" + e.getValue().stream().collect(Collectors.joining(";")) + "\n");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
					bw.close();
				} catch( Exception e ) {
					e.printStackTrace();
				} finally {
					try {
						geneset.zipfilesystem.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		MenuItem	importkeggpathwayaction = new MenuItem("Import kegg pathways");
		importkeggpathwayaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<String> keggids = new HashSet<>();
				for (Gene g : geneset.genelist) {
					if (g.keggid != null) {
						int i = g.keggid.indexOf(':');
						if (i > 0) {
							keggids.add(g.keggid.substring(0, i));
						}
					}
				}
				System.err.println(keggids);

				JTextField tf = new JTextField("http://130.208.252.239/organisms/");
				JOptionPane.showMessageDialog(null, tf);

				Map<String, String> env = new HashMap<>();
				env.put("create", "true");

				Path rootp = null;
				try {
					geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
				} catch (Exception ee) {
					ee.printStackTrace();
				}

				for (Path root : geneset.zipfilesystem.getRootDirectories()) {
					rootp = root;
					break;
				}


				for (String kegg : keggids) {
					try {
						URL url = new URL(tf.getText() + kegg + ".tar.gz");
						InputStream is = url.openStream();
						GZIPInputStream gz = new GZIPInputStream(is);

						TarArchiveInputStream tar = new TarArchiveInputStream(gz);
						TarArchiveEntry tae = (TarArchiveEntry) tar.getNextEntry();
						while (tae != null) {
							geneset.traverseTar(tar, tae, rootp);

							tae = (TarArchiveEntry) tar.getNextEntry();
						}

						is.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				try {
					geneset.zipfilesystem.close();
				} catch (Exception ee) {
					ee.printStackTrace();
				};
			});
		});
		
		edit.getItems().add( clustergenes );
		edit.getItems().add( alignclusters );
		edit.getItems().add( new SeparatorMenuItem() );
		edit.getItems().add( sharenumaction );
		edit.getItems().add( importgeneclusteringaction );
		edit.getItems().add( importgenesymbolaction );
		edit.getItems().add( importcazyaction );
		edit.getItems().add( functionmappingaction );
		edit.getItems().add( importidmappingaction );
		edit.getItems().add( gene2refseqaction );
		edit.getItems().add( importbiosystemsaction );
		edit.getItems().add( importkeggpathwayaction );
		edit.getItems().add( new SeparatorMenuItem() );
		edit.getItems().add( cogblastaction );
		edit.getItems().add( pfamblastaction );
		edit.getItems().add( unresolvedblastaction );
		edit.getItems().add( hhblitsaction );
		edit.getItems().add( hhblitsuniaction );
		edit.getItems().add( new SeparatorMenuItem() );
		edit.getItems().add( registerlocusidaction );
		
		Menu		view = new Menu("View");
		gb = new RadioMenuItem("Genes");
		gb.setOnAction( actionEvent -> {
			splitpane.getItems().remove( ggsplit );
			splitpane.getItems().add( 0, gsplit );
			//table.setModel( defaultModel );

			var scrollBarOne = (ScrollBar)gtable.lookup(".scroll-bar:vertical");
			var scrollBarTwo = (ScrollBar)gresults.lookup(".scroll-bar:vertical");
			if (scrollBarOne!=null && scrollBarTwo!=null) {
				scrollBarOne.valueProperty().bindBidirectional(scrollBarTwo.valueProperty());
				scrollBarOne.setVisible(false);
				scrollBarOne.setMaxWidth(0.0);
			}
			gresults.selectionModelProperty().bindBidirectional(gtable.selectionModelProperty());
			/*ScrollBar gscrollBarOne = (ScrollBar)gtable.lookup(".scroll-bar:vertical");
			ScrollBar gscrollBarTwo = (ScrollBar)gresults.lookup(".scroll-bar:vertical");
			gscrollBarOne.valueProperty().bindBidirectional(gscrollBarTwo.valueProperty());
			gresults.selectionModelProperty().bindBidirectional(gtable.selectionModelProperty());*/
		});
		view.getItems().add( gb );
		ggb = new RadioMenuItem("Gene groups");
		ggb.setOnAction( actionEvent -> {
			splitpane.getItems().remove( gsplit );
			splitpane.getItems().add( 0, ggsplit );
			//table.setModel( groupModel );
		});
		
		ToggleGroup bg = new ToggleGroup();
		gb.setToggleGroup( bg );
		ggb.setToggleGroup( bg );
		//ButtonGroup	bg = new ButtonGroup();
		//bg.add( gb );
		//bg.add( ggb );
		
		ggb.setSelected( true );
		view.getItems().add( ggb );
		ActionCollection.addAll( view, geneset.clusterMap, GeneSetHead.this, geneset.speccontigMap, table, comp, geneset.cs );
		
		Menu		help = new Menu("Help");
		MenuItem 	about = new MenuItem("About");
		about.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog( comp, "CompGen 1.0" )));
		help.getItems().add( about );
		
		MenuItem test = new MenuItem("Test");
		test.setOnAction( actionEvent -> {
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
		});
		help.getItems().add( test );
		help.getItems().add( new SeparatorMenuItem() );
		MenuItem runserver = new MenuItem("Run server");
		runserver.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                JSpinner	spin = new JSpinner();
                JOptionPane.showMessageDialog(null, spin, "Port", JOptionPane.QUESTION_MESSAGE);
                try {
                    geneset.cs = WSServer.startServer(GeneSetHead.this, (Integer)spin.getValue());
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            });
		});
		help.getItems().add( runserver );
		help.getItems().add( new SeparatorMenuItem() );
		
		CheckMenuItem cbmi = new CheckMenuItem("Use geneset user");
		help.getItems().add( cbmi );
		
		cbmi.setOnAction( actionEvent -> {
				if( cbmi.isSelected() ) {
					geneset.user = "geneset";
				} else geneset.user = System.getProperty("user.name");
				
				JavaFasta.user = geneset.user;
				if( geneset.currentSerify != null ) geneset.currentSerify.user = geneset.user;
		});
		
		help.getItems().add( new SeparatorMenuItem() );
		MenuItem helptut = new MenuItem("Help & Tutorial");
		helptut.setOnAction( actionEvent -> {
				try {
					Desktop.getDesktop().browse( new URI("http://thermusgenes.appspot.com/pancore.html") );
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
		});
		help.getItems().add( helptut );
		
		Menu		sequencemenu = new Menu("Sequence");
		MenuItem	showgroupseq = new MenuItem("Show group sequences");
		showgroupseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
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
				if (geneset.currentSerify == null) {
					frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setSize(400, 300);

					Map<String, String> env = new HashMap<String, String>();
					//Path path = zipfile.toPath();
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create(uristr /*.replace("file://", "file:")*/);
					try {
						geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					SerifyApplet sa = new SerifyApplet(geneset.zipfilesystem, geneset.noseq);
					sa.init(frame, null, geneset.user);
					//frame.add( )
					geneset.currentSerify = sa;
				}/* else frame = (JFrame)currentSerify.cnt;*/

				String[] farr = new String[]{"o.profundus", "mt.silvanus", "mt.ruber", "m.hydrothermalis", "t.thermophilus_SG0_5JP17_16",
						"t.thermophilusJL18", "t.thermophilusHB8", "t.thermophilusHB27", "t.scotoductusSA01", "t.scotoductus4063",
						"t.scotoductus1572", "t.scotoductus2101", "t.scotoductus2127", "t.scotoductus346",
						"t.scotoductus252", "t.antranikiani", "t.kawarayensis", "t.brockianus", "t.igniterrae", "t.eggertsoni",
						"t.RLM", "t.oshimai_JL2", "t.oshimai", "t.filiformis", "t.arciformis", "t.islandicus", "t.aquaticus", "t.spCCB"};

				Map<Integer, String> ups = new HashMap<>();
				Set<Integer> stuck = new HashSet<>();
				Map<Integer, List<Annotation>> ups2 = new HashMap<>();
				//int[] rr = table.getSelectedRows();
				for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
					//int cr = table.convertRowIndexToModel(r);
					//Gene gg = geneset.genelist.get(cr);
					if (gg.getSpecies() != null) {
						if (gg.genid != null && gg.genid.length() > 0) {
							ups.put(gg.getGroupIndex(), gg.name);
							stuck.add(gg.getGroupIndex());
						}
						if (!stuck.contains(gg.getGroupIndex())) {
							if (!ups.containsKey(gg.getGroupIndex()) || !(gg.name.contains("unnamed") || gg.name.contains("hypot")))
								ups.put(gg.getGroupIndex(), gg.name);
						}

						List<Annotation> tlist;
						if (ups2.containsKey(gg.getGroupIndex())) tlist = ups2.get(gg.getGroupIndex());
						else {
							tlist = new ArrayList<>();
							ups2.put(gg.getGroupIndex(), tlist);
						}

						//Set<String>	 specs = new HashSet<String>();
						//textarea.append(gg.name + ":\n");
						//for (String sp : gg.species.keySet()) {
						int count = 0;
						for (String sp : farr) {
							//Teginfo stv = gg.species.equals(sp) ? gg.teginfo : null;

							if (gg.getSpecies().equals(sp)) tlist.add(gg.getTegeval());
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
					for (int gi : ups.keySet()) {
						String name = ups.get(gi);
						List<Annotation> tlist = ups2.get(gi);

						sb.append(name.replace('/', '-') + ":\n");
						if (tlist.size() < 28) {
							for (Annotation tv : tlist) {
								System.err.println(tv.getName());
							}
							System.err.println();
						}
						for (Annotation tv : tlist) {
							Sequence ps = tv.getProteinSequence();
							ps.setName(tv.getName().substring(0, tv.getName().indexOf('_')));
							ps.writeSequence(sb);
							/*sb.append(">" + tv.name.substring(0, tv.name.indexOf('_')) + "\n");
							for (int i = 0; i < ps.length(); i += 70) {
								sb.append( ps.substring(i, Math.min(i + 70, tv.getProteinLength() )) + "\n");
							}*/
						}
					}

					Reader rd = new StringReader(sb.toString());
					BufferedReader br = new BufferedReader(rd);
					geneset.currentSerify.addSequences("uh", br, Paths.get("/"), null);
				} catch (URISyntaxException | IOException e1) {
					e1.printStackTrace();
				}
				frame.setVisible(true);
			});
		});
		sequencemenu.getItems().add( showgroupseq );
		
		MenuItem showgroupdnaseq = new MenuItem("Show group DNA sequences");
		showgroupdnaseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				final JTextArea textarea = new JTextArea();
				JScrollPane scrollpane = new JScrollPane(textarea);

				try {
					Action action = new CopyAction("Copy", null, "Copy data", KeyEvent.VK_CONTROL + KeyEvent.VK_C);
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
								public Object getTransferData(DataFlavor arg0) {
									if (arg0.equals(df)) {
										return new ByteArrayInputStream(textarea.getText().getBytes());
									} else {
										return textarea.getText();
									}
								}

								@Override
								public DataFlavor[] getTransferDataFlavors() {
									return new DataFlavor[]{df, DataFlavor.stringFlavor};
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
					textarea.setTransferHandler(th);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				Map<Integer, String> ups = new HashMap<>();
				Set<Integer> stuck = new HashSet<>();
				Map<Integer, List<Annotation>> ups2 = new HashMap<>();
				//int[] rr = table.getSelectedRows();
				for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
					//int cr = table.convertRowIndexToModel(r);
					//Gene gg = geneset.genelist.get(cr);
					if (gg.getSpecies() != null) {
						if (gg.genid != null && gg.genid.length() > 0) {
							ups.put(gg.getGroupIndex(), gg.name);
							stuck.add(gg.getGroupIndex());
						}
						if (!stuck.contains(gg.getGroupIndex())) {
							if (!ups.containsKey(gg.getGroupIndex()) || !(gg.name.contains("unnamed") || gg.name.contains("hypot")))
								ups.put(gg.getGroupIndex(), gg.name);
						}

						List<Annotation> tlist;
						if (ups2.containsKey(gg.getGroupIndex())) tlist = ups2.get(gg.getGroupIndex());
						else {
							tlist = new ArrayList<>();
							ups2.put(gg.getGroupIndex(), tlist);
						}

						//textarea.append(gg.name + ":\n");
						tlist.add(gg.getTegeval());
								/*textarea.append(">" + tv.cont + " " + tv.teg + " " + tv.eval + "\n");
								if (tv.dna != null) {
									for (int i = 0; i < tv.dna.length(); i += 70) {
										textarea.append(tv.dna.substring(i, Math.min(i + 70, tv.dna.length())) + "\n");
									}
								}*/
					}
				}

				for (int gi : ups.keySet()) {
					String name = ups.get(gi);
					List<Annotation> tlist = ups2.get(gi);

					textarea.append(name.replace('/', '-') + ":\n");
					for (Annotation tv : tlist) {
						textarea.append(">" + tv.getName().substring(0, tv.getName().indexOf('_')) + "\n");
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
			});
		});
		sequencemenu.getItems().add( showgroupdnaseq );
		sequencemenu.getItems().add( new SeparatorMenuItem() );
		
		MenuItem showallseq = new MenuItem("Show all sequences");
		showallseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta(null, serifier, geneset.cs);
				jf.initGui(frame);

				Map<String, Sequence> contset = new HashMap<>();
				//int[] rr = table.getSelectedRows();
				for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
					//int cr = table.convertRowIndexToModel(r);
					//Gene gg = geneset.genelist.get(cr);
					Annotation tv = gg.getTegeval();
					String contig = tv.getContshort().getName();
					Sequence seq = tv.getProteinSequence();
					seq.setName(contig);
					serifier.mseq.put(seq.getName(), seq);
					//Sequence seq = new Sequence( contig, aa, serifier.mseq );
					serifier.addSequence(seq);
				}
				jf.updateView();

				frame.setVisible(true);
			});
		});
		sequencemenu.getItems().add( showallseq );
		
		MenuItem showseq = new MenuItem("Show sequences");
		showseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				//int[] rr = table.getSelectedRows();
				if (!isGeneview()) {
					for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add(gg);
					}
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//Gene gg = geneset.genelist.get(cr);
						genegroups.add(gg.getGeneGroup());
					}
				}
				SwingUtilities.invokeLater(() -> {
					Set<String> specs = null;
					if (table.getItems().size() > 1) specs = getSelspec(GeneSetHead.this, geneset.specList, false, null);
					showSequences(genegroups, false, specs);
				});
			});
		});
		sequencemenu.getItems().add( showseq );
		
		MenuItem showseqwgenenames = new MenuItem("Show sequences w/genenames");
		showseqwgenenames.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				//int[] rr = table.getSelectedRows();
				if (!isGeneview()) {
					for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//GeneGroup gg = geneset.allgenegroups.get(cr);
						genegroups.add(gg);
					}
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//Gene gg = geneset.genelist.get(cr);
						genegroups.add(gg.getGeneGroup());
					}
				}
				//Set<String>	specs = null;
				//if( rr.length > 1 ) specs = getSelspec(comp, specList, null);
				showSequences(genegroups, false, null, EnumSet.of(ToShow.GENES));
			});
		});
		sequencemenu.getItems().add( showseqwgenenames );

		MenuItem showseqwstraingenenames = new MenuItem("Show sequences w/strainandgenenames");
		showseqwstraingenenames.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				//int[] rr = table.getSelectedRows();
				if (!isGeneview()) {
					//int cr = table.convertRowIndexToModel(r);
					//GeneGroup gg = geneset.allgenegroups.get(cr);
					genegroups.addAll(table.getSelectionModel().getSelectedItems());
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//Gene gg = geneset.genelist.get(cr);
						genegroups.add(gg.getGeneGroup());
					}
				}
				//Set<String>	specs = null;
				//if( rr.length > 1 ) specs = getSelspec(comp, specList, null);
				showSequences(genegroups, false, null, EnumSet.of(ToShow.STRAINS,ToShow.IDS));
			});
		});
		sequencemenu.getItems().add( showseqwstraingenenames );

		MenuItem showseqwid = new MenuItem("Show sequences w/id");
		showseqwid.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				//int[] rr = table.getSelectedRows();
				if (!isGeneview()) {
					//int cr = table.convertRowIndexToModel(r);
					//GeneGroup gg = geneset.allgenegroups.get(cr);
					genegroups.addAll(table.getSelectionModel().getSelectedItems());
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						//int cr = table.convertRowIndexToModel(r);
						//Gene gg = geneset.genelist.get(cr);
						genegroups.add(gg.getGeneGroup());
					}
				}
				//Set<String>	specs = null;
				//if( rr.length > 1 ) specs = getSelspec(comp, specList, null);
				showSequences(genegroups, false, null, EnumSet.of(ToShow.IDS));
			});
		});
		sequencemenu.getItems().add( showseqwid );
		
		MenuItem showalignseq = new MenuItem("Show aligned sequences");
		showalignseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				//int[] rr = table.getSelectedRows();
				if (!isGeneview()) {
					for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
						genegroups.add(gg);
					}
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						genegroups.add(gg.getGeneGroup());
					}
				}

				Serifier serifier = new Serifier();
				for (GeneGroup ggroup : genegroups) {
					for (Annotation tv : ggroup.getTegevals()) {
						Contig ctg = tv.getContshort();
						if(ctg!=null) {
							String selspec = tv.getContshort().getSpec();//tv.getContig();
							String spec = geneset.nameFix(selspec);
						/*if( selspec.contains("hermus") ) spec = selspec;
						else {
							Matcher m = Pattern.compile("\\d").matcher(selspec); 
							int firstDigitLocation = m.find() ? m.start() : 0;
							if( firstDigitLocation == 0 ) spec = "Thermus_" + selspec;
							else spec = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
						}*/

							Sequence seq = tv.getAlignedSequence();
							//System.err.println( "seqlen " + seq.length() );
							if (seq != null) {
								seq.setName(spec);
								//Sequence seq = new Sequence( contig, seqstr, null );
								serifier.addSequence(seq);
							} else {
								Sequence sb = tv.getProteinSequence();
								sb.setName(spec);
								//Sequence sseq = new Sequence( spec, sb, serifier.mseq );
								serifier.addSequence(sb);
							}
						}
					}
				}
				showAlignedSequences(comp, serifier);
			});
		});
		sequencemenu.getItems().add( showalignseq );
		
		MenuItem splitseq = new MenuItem("Split/Show sequences");
		splitseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				try {
					StringBuffer sb = getSelectedASeqs(table, geneset.genelist, null, geneset.specList);
					if (geneset.currentSerify == null) {
						JFrame frame = new JFrame();
						frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						frame.setSize(800, 600);

						SerifyApplet sa = new SerifyApplet(geneset.zipfilesystem, geneset.noseq);
						sa.init(frame, null, geneset.user);
						geneset.currentSerify = sa;

						frame.setVisible(true);
					}
					Reader rd = new StringReader(sb.toString());
					BufferedReader br = new BufferedReader(rd);
					geneset.currentSerify.addSequences("uh", br, Paths.get("/"), null);
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
			});
		});
		sequencemenu.getItems().add( splitseq );
		
		MenuItem showdnaseq = new MenuItem("Show DNA sequences");
		showdnaseq.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<GeneGroup> genegroups = new HashSet<>();
				int rr = 0;
				if (!isGeneview()) {
					ObservableList<GeneGroup> lgg = table.getSelectionModel().getSelectedItems();
					genegroups.addAll(lgg);
					rr = lgg.size();
				} else {
					for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
						genegroups.add(gg.getGeneGroup());
						rr++;
					}
				}
				Set<String> specs = null;
				if (rr > 1) specs = getSelspec(GeneSetHead.this, geneset.specList, false, null);
				showSequences(genegroups, true, specs);
				
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
			});
		});
		sequencemenu.getItems().add( showdnaseq );
		
		MenuItem expalldna = new MenuItem("Export all DNA sequences");
		expalldna.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				try {
					Map<Integer, FileWriter> lfw = new HashMap<>();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();
						for (Gene gg : getGeneTable().getSelectionModel().getSelectedItems()) {
							FileWriter fw = null;
							if (lfw.containsKey(gg.getGroupIndex())) {
								fw = lfw.get(gg.getGroupIndex());
							} else {
								fw = new FileWriter(new File(f, "group_" + gg.getGroupIndex() + ".fasta"));
								lfw.put(gg.getGroupIndex(), fw);
							}

							Annotation tv = gg.getTegeval();
							fw.append(">" + tv.getName() + " " + tv.getSpecies() + " " + tv.eval + "\n");
							for (int i = 0; i < tv.getLength(); i += 70) {
								fw.append(tv.getSubstring(i, Math.min(i + 70, tv.getLength())) + "\n");
							}
						}
					}
					for (int gi : lfw.keySet()) {
						lfw.get(gi).close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});
		sequencemenu.getItems().add( expalldna );
		
		MenuItem exprelcont = new MenuItem("Export relevant contigs");
		exprelcont.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFileChooser jfc = new JFileChooser();

				try {
					Map<Integer, FileWriter> lfw = new HashMap<>();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();

						Set<Sequence> contset = new HashSet<>();
						for (Gene gg : getGeneTable().getSelectionModel().getSelectedItems()) {
							Annotation tv = gg.getTegeval();
							contset.add(tv.getContshort());
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
			});
		});
		sequencemenu.getItems().add( exprelcont );
		sequencemenu.getItems().add( new SeparatorMenuItem() );
		
		MenuItem viewselrange = new MenuItem("View selected range");
		viewselrange.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta(null, serifier, geneset.cs);
				jf.initGui(frame);

				Set<Sequence> contset = new HashSet<>();
				Set<Annotation> tvset = new HashSet<>();
				if (isGeneview()) {
					for (Gene gg : getGeneTable().getSelectionModel().getSelectedItems()) {
						Annotation tv = gg.getTegeval();
						tvset.add(tv);
						//serifier.addAnnotation( tv );
						contset.add(tv.getContshort());
					}
				} else {
					for (GeneGroup gg : getGeneGroupTable().getSelectionModel().getSelectedItems()) {
						for (Annotation tv : gg.getTegevals()) {
							tv.color = Color.red;
							tvset.add(tv);
							Sequence contig = tv.getContshort();
							contset.add(contig);
							//serifier.addAnnotation( tv );
						}
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

				for (Sequence contig : contset) {
					int start = Integer.MAX_VALUE;
					int stop = Integer.MIN_VALUE;
					for (Annotation tv : tvset) {
						if (contig == tv.getSeq()) {
							start = Math.min(start, tv.start);
							stop = Math.max(stop, tv.stop);
						}
					}

					int rstart = 0;
					int rstop = contig.length();
					if (contig.annset != null) for (Annotation tv : contig.annset) {
						if (contig == tv.getSeq()) {
							if (tv.stop < start && tv.stop > rstart) {
								rstart = tv.stop;
							}
							if (tv.start > stop && tv.start < rstop) {
								rstop = tv.start;
							}
						}
					}

					start = rstart;
					stop = rstop;

					Sequence newseq = new Sequence(contig.getName(), new StringBuilder(contig.getSubstring(start, stop, 1)), serifier.mseq);
					/*if( contig.isReverse() ) {
						newseq.reverse();
						newseq.complement();
					}*/
					//Executors.newVirtualThreadPerTaskExecutor();

					serifier.addSequence(newseq);
					for (Annotation tv : tvset) {
						Annotation newann = new Annotation(newseq, tv.start - start, tv.stop - start, tv.ori, tv.getName());
						if (contig == tv.getSeq()) {
							newseq.addAnnotation(newann);
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
			});
		});
		sequencemenu.getItems().add( viewselrange );
		
		MenuItem viewwhole = new MenuItem("View whole contigs for selection");
		viewwhole.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta(null, serifier, geneset.cs);
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

				Set<Sequence> contigs = new HashSet<>();
				if (isGeneview()) {
					for (Gene gg : getGeneTable().getSelectionModel().getSelectedItems()) {
						Annotation tv = gg.getTegeval();
						tv.color = Color.red;
						Sequence contig = tv.getContshort();
						//contig.offset = -tv.start;
						contigs.add(contig);
					}
					
					
					/*Annotation a = new Annotation(contig, contig.getName(), Color.red, serifier.mann);
					a.setStart(tv.start);
					a.setStop(tv.stop);
					a.setOri(tv.ori);
					a.setGroup(g.name);
					a.setType("gene");*/
					//serifier.addAnnotation( tv );
				} else {
					for (GeneGroup gg : getGeneGroupTable().getSelectionModel().getSelectedItems()) {
						for (Annotation tv : gg.getTegevals()) {
							tv.color = Color.red;
							Sequence contig = tv.getContshort();
							//contig.offset = -tv.start;
							contigs.add(contig);
							
							
							/*Annotation a = new Annotation(contig, contig.getName(), Color.red, serifier.mann);
							a.setStart(tv.start);
							a.setStop(tv.stop);
							a.setOri(tv.ori);
							a.setGroup(gg.getCommonName());
							a.setType("gene");*/
							//serifier.addAnnotation( tv );
						}
					}
				}
				//Gene gg = geneset.genelist.get(cr);
				//for (Gene g : geneset.genelist) {
				//if (g.species != null) {
				//for (String sp : g.species.keySet()) {

				for (Sequence contig : contigs) {
					for (Annotation ann : contig.getAnnotations()) {
						serifier.addAnnotation(ann);
					}

					serifier.addSequence(contig);
					serifier.mseq.put(contig.getName(), contig);
					//if(contig.getAnnotations() != null)
					//	Collections.sort(contig.getAnnotations());
				}
				jf.updateView();

				frame.setVisible(true);
			});
		});
		sequencemenu.getItems().add( viewwhole );
		sequencemenu.getItems().add( new SeparatorMenuItem() );
		
		MenuItem viewspecseq = new MenuItem("View species sequence");
		viewspecseq.setOnAction( (javafx.event.ActionEvent actionEvent) -> {
		    SwingUtilities.invokeLater(() -> {
                Set<String> selspec = getSelspec(GeneSetHead.this, geneset.specList);

                JFrame frame = new JFrame();
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                Serifier serifier = new Serifier();
                JavaFasta jf = new JavaFasta(null, serifier, geneset.cs);
                jf.initGui(frame);

                for (String spec : selspec) {
                    List<Sequence> contigs = geneset.speccontigMap.get(spec);

                    for (Sequence contig : contigs) {
                        serifier.addSequence(contig);
                        serifier.mseq.put(contig.getName(), contig);
                    }
                }

                frame.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(java.awt.event.WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {}

                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        if( jf.isEdited() ) {
                            Map<String,String> env = new HashMap<>();
                            env.put("create", "true");

                            try {
                                geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
                                for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
                                    for (String spec : selspec) {
                                        Path subf = root.resolve(spec + ".fna");
                                        if (Files.exists(subf)) {
                                            BufferedWriter bw = Files.newBufferedWriter(subf, StandardOpenOption.TRUNCATE_EXISTING);
                                            serifier.lseq.stream().forEach(s -> {
                                                try {
                                                    s.writeSequence(bw);
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }
                                            });
                                            bw.close();
                                        }

                                        Path subp = root.resolve(spec + ".prodigal.fsa");
                                        if (Files.exists(subp)) {
                                            BufferedWriter bw = Files.newBufferedWriter(subp, StandardOpenOption.TRUNCATE_EXISTING);
                                            serifier.lseq.stream().forEach(s -> s.getAnnotations().stream().forEach(ss -> {
                                                try {
                                                    Sequence seq = ss.getProteinSequence();
                                                    if( seq.getName() != null ) seq.writeSequence(bw);
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }) );
                                            bw.close();
                                        }
                                    }

                                    break;
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } finally {
                                try{ geneset.zipfilesystem.close(); } catch( IOException ie ) { ie.printStackTrace(); };
                            }
                        }
                    }

                    @Override
                    public void windowIconified(java.awt.event.WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(java.awt.event.WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(java.awt.event.WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(java.awt.event.WindowEvent e) {

                    }
                });
                jf.updateView();

                frame.setVisible(true);
            });
		});
		sequencemenu.getItems().add( viewspecseq );
		
		Menu		windowmenu = new Menu("Tools");
		MenuItem seqviewer = new MenuItem("Sequence viewer");	
		seqviewer.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Serifier serifier = new Serifier();
				JavaFasta jf = new JavaFasta(null, serifier, geneset.cs);
				jf.initGui(frame);
				jf.updateView();

				frame.setVisible(true);
			});
		});
		windowmenu.getItems().add( seqviewer );
		windowmenu.getItems().add( new SeparatorMenuItem() );
		MenuItem genesorter = new MenuItem("Gene sorter");
		genesorter.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				try {
					//if( gb.isSelected() ) new GeneSorter().mynd( GeneSetHead.this, genelist, table, null, contigmap );
					//else 
					new GeneSorter().groupMynd(GeneSetHead.this, geneset.allgenegroups, geneset.specList, geneset.genelist, table, geneset.contigmap, geneset.specset);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});
		windowmenu.getItems().add( genesorter );
		MenuItem specorderaction = new MenuItem("Order species list");
		specorderaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				org.simmi.treedraw.shared.TreeUtil tu = new org.simmi.treedraw.shared.TreeUtil();
				/*corrInd.clear();
				for( String spec : specList ) {
					corrInd.add( nameFix( spec ) );
				}*/

				Serifier serifier = getConcatenatedSequences(false, false);

				Map<String, Integer> blosumap = JavaFasta.getBlosumMap();
				double[] dmat = new double[serifier.lseq.size() * serifier.lseq.size()];
				Sequence.distanceMatrixNumeric(serifier.lseq, dmat, null, false, false, null, blosumap);

				List<String> ret = new ArrayList<>();
				for (Sequence seqname : serifier.lseq) {
					ret.add(seqname.getName()); //.replace(' ', '_') );
				}
				//List<String>	corrInd = currentjavafasta.getNames();

				//Sequence.distanceMatrixNumeric(serifier.lseq, dmat, idxs, bootstrap, cantor, ent, blosum);
				Node n = tu.neighborJoin(dmat, ret, null, false, false);

				Comparator<Node> comp2 = (o1, o2) -> {
					int c1 = o1.countLeaves();
					int c2 = o2.countLeaves();

					if (c1 > c2) return 1;
					else if (c1 == c2) return 0;

					return -1;
				};
				tu.arrange(n, comp2);
				//corrInd.clear();
				List<String> ordInd = n.traverse();

				for (String spec : ordInd) {
					System.err.println(spec);
				}

				for (String oldspec : geneset.specList) {
					if (!ordInd.contains(oldspec)) {
						ordInd.add(oldspec);
					}
				}
				geneset.specList = ordInd;

				//TableModel model = table.getModel();
				//table.setModel( nullmodel );
				//table.setModel( model );


				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				//table.getColumnModel().
				System.err.println(geneset.specList.size());
			});
		});
		MenuItem matrixaction = new MenuItem("Relation matrix");
		matrixaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                JComboBox<String>	descombo = new JComboBox<String>( geneset.deset.toArray( new String[geneset.deset.size()] ) );
                JCheckBox			anicheck = new JCheckBox("ANImatrix");
				JCheckBox			plasmidcheck = new JCheckBox("Skip plasmids");
                descombo.insertItemAt("", 0);
                descombo.setSelectedIndex( 0 );
                JOptionPane.showMessageDialog( null, new Object[] { descombo, anicheck, plasmidcheck } );
                String val = descombo.getSelectedItem().toString();

                Collection<GeneGroup> ss = new HashSet<>();
				/*int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					ss.add( geneset.allgenegroups.get( table.convertRowIndexToModel(r) ) );
				}*/
                ss.addAll( table.getSelectionModel().getSelectedItems() );
                if( ss.isEmpty() ) ss = geneset.allgenegroups;

                Set<String> species = getSelspec( GeneSetHead.this, geneset.specList );
                bimg = anicheck.isSelected() ? geneset.animatrix( species, geneset.clusterMap, val, ss, plasmidcheck.isSelected() ) : geneset.bmatrix( species, geneset.clusterMap, val );

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
                    final TransferComponent comp2 = new TransferComponent(bimg, transferable);

                    TransferHandler th = new TransferHandler() {
                        private static final long serialVersionUID = 1L;

                        public int getSourceActions(JComponent c) {
                            return TransferHandler.COPY_OR_MOVE;
                        }

                        public boolean canImport(TransferSupport support) {
                            return false;
                        }

                        protected Transferable createTransferable(JComponent c) {
                            return transferable;
                        }

                        public boolean importData(TransferSupport support) {
                            return true;
                        }
                    };
                    comp2.setTransferHandler(th);

                    comp2.setEnabled(true);
                    JScrollPane fsc = new JScrollPane(comp2);
                    comp2.setPreferredSize(new Dimension(bimg.getWidth(), bimg.getHeight()));

                    JPopupMenu	popup = new JPopupMenu();
                    popup.add( new AbstractAction("Save image") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //FileSaveService fss = null;
                            //FileContents fileContents = null;

                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                OutputStreamWriter	osw = new OutputStreamWriter( baos );
                                ImageIO.write(bimg, "png", baos);
                                baos.close();

                                /*try {
                                    fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
                                } catch( UnavailableServiceException e1 ) {
                                    fss = null;
                                }*/

                                /*if (fss != null) {
                                    ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
                                    fileContents = fss.saveFileDialog(null, null, bais, "export.png");
                                    bais.close();
                                    OutputStream os = fileContents.getOutputStream(true);
                                    os.write( baos.toByteArray() );
                                    os.close();
                                } else {*/
                                	Platform.runLater(() -> {
                                        FileChooser fc = new FileChooser();
                                        File sf = fc.showSaveDialog( null );
										try {
											FileOutputStream fos = new FileOutputStream(sf);
											fos.write( baos.toByteArray() );
											fos.close();
											Desktop.getDesktop().browse( sf.toURI() );
										} catch (IOException e1) {
											e1.printStackTrace();
										}
                                    });
                                //}
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    });
                    comp2.setComponentPopupMenu( popup );

                    f.add(fsc);
                    f.setVisible(true);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            });
		});
		MenuItem tniaction = new MenuItem("TNI/ANI");
		tniaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				ANITools.showAni(GeneSetHead.this);
			});
		});
		MenuItem aaiaction = new MenuItem("AAI");
		aaiaction.setOnAction( actionEvent -> {
			List<GeneGroup> agg = table.getSelectionModel().getSelectedItems();
			List<GeneGroup> lagg = agg.isEmpty() ? geneset.allgenegroups : agg;
			SwingUtilities.invokeLater(() -> {
				JCheckBox all = new JCheckBox("Skip gaps");
				Set<String> species = getSelspec(GeneSetHead.this, geneset.specList, true, all);
				List<String> speclist = new ArrayList<>(species);

				var aniResult = JavaFasta.corr(speclist, lagg, false, all.isSelected());
				JavaFasta.showAniMatrix(speclist, aniResult);
			});
			/*SwingUtilities.invokeLater(() -> {
				ANITools.aaiAction(GeneSetHead.this,agg);
				ANITools.showAni(GeneSetHead.this);
			});*/
		});
		MenuItem anitreeaction = new MenuItem("ANI tree");
		anitreeaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				Set<String> species = getSelspec(GeneSetHead.this, geneset.specList);
				List<String> speclist = new ArrayList<>(species);
				List<GeneGroup> agg = table.getSelectionModel().getSelectedItems();

				ANIResult aniResult = JavaFasta.corr(speclist, agg, true, false);
				org.simmi.treedraw.shared.TreeUtil tu = new org.simmi.treedraw.shared.TreeUtil();
				geneset.corrInd.clear();
				for (String spec : speclist) {
					geneset.corrInd.add(geneset.nameFix(spec));
				}
				Node n = tu.neighborJoin(aniResult.corrarr, geneset.corrInd, null, false, false);
				System.err.println(n);
			});
		});
		windowmenu.getItems().add( specorderaction );
		windowmenu.getItems().add( matrixaction );
		windowmenu.getItems().add( tniaction );
		windowmenu.getItems().add( aaiaction );
		windowmenu.getItems().add( anitreeaction );
		
		MenuItem neighbourhood = new MenuItem("Neighbourhood");
		neighbourhood.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				try {
					Set<GeneGroup> genset = new HashSet<>();
					if (!isGeneview()) {
						for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
							genset.add(gg);
						}
					} else {
						for (Gene gene : gtable.getSelectionModel().getSelectedItems()) {
							genset.add(gene.getGeneGroup());
						}
					}
					new Neighbour(genset).neighbourMynd(GeneSetHead.this, comp, geneset.genelist, geneset.contigmap);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});
		windowmenu.getItems().add( neighbourhood );
		
		MenuItem synteny = new MenuItem("Synteny");
		synteny.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                //Set<String> species = speciesFromCluster( clusterMap );
                new Synteni().syntenyMynd( GeneSetHead.this, comp, geneset.genelist );
            });
		});
		windowmenu.getItems().add( synteny );
		MenuItem compareplotaction = new MenuItem("Gene atlas");
		compareplotaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                try {
                    new GeneCompare().comparePlot( GeneSetHead.this, comp, geneset.genelist, geneset.clusterMap, 8192, 8192 );
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
				
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
		});
		windowmenu.getItems().add( compareplotaction );

		MenuItem loccompareplotaction = new MenuItem("Location Gene atlas");
		loccompareplotaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				try {
					new IGeneCompare().comparePlot( GeneSetHead.this, comp, geneset.genelist, geneset.clusterMap, 8192, 8192 );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});

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
		});
		windowmenu.getItems().add( loccompareplotaction );
		
		MenuItem syntenygradientaction = new MenuItem("Synteny gradient");
		syntenygradientaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                Set<String> presel = new HashSet<>();
                if( isGeneview() ) {
                    for( Gene g : gtable.getSelectionModel().getSelectedItems() ) {
                        presel.addAll( g.getGeneGroup().getSpecies() );
                    }
                } else {
                    for( GeneGroup gg : table.getSelectionModel().getSelectedItems() ) {
                        presel.addAll( gg.getSpecies() );
                    }
                }
                new SyntGrad().syntGrad( GeneSetHead.this, 4096, 4096, presel );
            });
		});
		windowmenu.getItems().add( syntenygradientaction );
		
		MenuItem genexyplotaction = new MenuItem("Gene XY plot");
		genexyplotaction.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> new XYPlot().xyPlot( GeneSetHead.this, comp, geneset.genelist, geneset.clusterMap )));
		windowmenu.getItems().add( genexyplotaction );
		
		MenuItem refalignaction = new MenuItem("Reference align");
		refalignaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
                final TableView<Gene> table12 = getGeneTable();
                final Collection<String> 		specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
                final List<String>				species = new ArrayList<>( specset );

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

                Map<String,String> env = new HashMap<>();
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
            });
				
				//flx.start( f.getParentFile().getAbsolutePath()+"/", f.getName(), false, fw, comp, bb);
		});
		windowmenu.getItems().add( refalignaction );
		
		windowmenu.getItems().add( new SeparatorMenuItem() );
		
		MenuItem runantismash = new MenuItem("Run antismash");
		runantismash.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            try {
                Serifier ser = new Serifier();
                Set<String> selspec = getSelspec(null, geneset.getSpecies(), false, null);

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

                List<Object> commands = new ArrayList<>();
                //commands.add(genexyplotaction)

                for( String spec : selspec ) {
                    Path pp = Paths.get( userhome );
                    Path p = pp.resolve(spec+".gbk");
                    //BufferedWriter fw = Files.newBufferedWriter( p );
                    List<Sequence> clist = geneset.speccontigMap.get( spec );

                    Map<String,List<Annotation>> mapan = new HashMap<>();
                    Serifier serifier = new Serifier();
                    for( Sequence c : clist ) {
                        serifier.addSequence(c);
                        serifier.mseq.put(c.getName(), c);

                        List<Annotation> lann = new ArrayList<>();
                        if( c.getAnnotations() != null ) for( Annotation ann : c.getAnnotations() ) {
                            Tegeval tv = (Tegeval)ann;

                            Gene g = tv.getGene();
                            GeneGroup gg = g.getGeneGroup();
                            String name = g.getName();
                            if( gg != null && name.contains(spec) ) {
                                name = gg.getName();
                            }
                            Annotation anno = new Annotation( c, tv.start, tv.stop, tv.ori, name );
                            anno.setId(tv.getGene().getId());
                            anno.type = "CDS";

                            String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
                            if( cazy != null ) anno.addDbRef( "CAZY:"+cazy );
                            lann.add( anno );
                        }
                        mapan.put( c.getName(), lann );
                    }
                    Sequences s = new Sequences(null,spec,"nucl",null,clist.size());
                    //serifier.addSequences(seqs);
                    serifier.writeGenebank( p, false, true, s, mapan, false);

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

                Runnable run = () -> {
				for( String spec : selspec ) {
				Path p = Paths.get(userhome, spec);

				Map<String,String> env = new HashMap<>();
				env.put("create", "true");

				String uristr = "jar:" + geneset.zippath.toUri();
				URI zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
				final List<Path>	lbi = new ArrayList<>();
				try {
				geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
				for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
				Path specdir = root;
				Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file1, BasicFileAttributes attrs) throws IOException {
				final Path destFile = Paths.get(specdir.toString(), file1.toString());
				//System.out.printf("Extracting file %s to %s\n", file, destFile);
				Files.copy(file1, destFile, StandardCopyOption.REPLACE_EXISTING);
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
				};

                NativeRun nr = new NativeRun( run );
                nr.runProcessBuilder("antismash", commands, new Object[3], false, run, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		windowmenu.getItems().add( runantismash );
		
		MenuItem runsignalp = new MenuItem("Run signalP");
		runsignalp.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            try {
                Serifier ser = new Serifier();
                Set<String> selspec = getSelspec(null, geneset.getSpecies(), false, null);

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
                            Stream<Gene> genestream = geneset.genelist.stream().filter( gene -> spec.equals(gene.getSpecies()) && (gene.getTegeval().type == null || gene.getTegeval().type.length() == 0) );
                            Path sigout = root.resolve(spec+".signalp");
                            Path[] pt = new Path[] {null,sigout,null};
                            if( hostname.equals("localhost") ) {
                                String[] cmds = {"/Users/sigmar/signalp-4.1/signalp", "-t", "gram-", "-"};
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

                Runnable run = () -> {
                    try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
                };

                NativeRun nr = new NativeRun( run );
                nr.runProcessBuilder("/Users/sigmar/signalp-4.1/signalp", commands, new Object[3], false, run, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		windowmenu.getItems().add( runsignalp );
		
		MenuItem runtransm = new MenuItem("Run TransM");
		runtransm.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            try {
                Serifier ser = new Serifier();
                Set<String> selspec = getSelspec(null, geneset.getSpecies(), false, null);

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

                List<Object> commands = new ArrayList<>();
                //commands.add(genexyplotaction)

                try {
                    Map<String,String> env = new HashMap<>();
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

                            Stream<Gene> genestream = geneset.genelist.stream().filter( gene -> spec.equals(gene.getSpecies()) && (gene.getTegeval().type == null || gene.getTegeval().type.length() == 0) );
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(baos) );
                            genestream.forEach( gene -> {
                                try {
                                    gene.writeGeneIdAAFasta(bw);
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
                                String[] cmds = {"/usr/local/bin/docker","exec","-i","dcaaea383def","/root/tmhmm-2.0c/bin/decodeanhmm.Linux_x86_64", "-f", "/root/tmhmm-2.0c/lib/TMHMM2.0.options", "-modelfile", "/root/tmhmm-2.0c/lib/TMHMM2.0.model"};
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

                Runnable run = () -> {
					try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
				};

                NativeRun nr = new NativeRun( run );
                nr.runProcessBuilder("transm", commands, new Object[3], false, run, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		windowmenu.getItems().add( runtransm );
		
		MenuItem runtrnascan = new MenuItem("tRNAscan");
		runtrnascan.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> {
            try {
                Serifier ser = new Serifier();
                Set<String> selspec = getSelspec(null, geneset.getSpecies(), false, null);

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

                List<Object> commands = new ArrayList<>();
                //commands.add(genexyplotaction)

                try {
                    Map<String,String> env = new HashMap<>();
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
                            if( hostname.equals("localhost1") ) {
								Path[] pt = new Path[] {specdir,sigout,null};
                                String[] cmds = {"/usr/local/bin/tRNAscan-SE","-B","-"};
                                commands.add( pt );
                                commands.add( Arrays.asList( cmds ) );
                            } else {
								Path[] pt = new Path[] {null,sigout,null};
                                Path p = Paths.get(spec+".trnascan");
                                Files.copy(specdir, p, StandardCopyOption.REPLACE_EXISTING);

								List<String> lcmd;
								if( hostname.equals("localhost") ) {
									//String[] cmds = {"/usr/local/bin/trnascan-1.4", spec + ".trnascan"};
									String[] cmds = {"/usr/local/bin/tRNAscan-SE","-B",spec+".trnascan"};
									lcmd = Arrays.asList( cmds );
								} else {
									String[] cmds = {"scp", spec + ".trnascan", hostname + ":~", ";", "ssh", hostname, "trnascan-1.4", spec + ".trnascan"};
									lcmd = Arrays.asList( cmds );
									//String[] cmds = {"ssh",hostname,"tRNAscan-SE","-B","-"};
								}

                                commands.add( pt );
                                commands.add( lcmd );
                            }
                        }

                        break;
                    }
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }

                Runnable run = () -> {
                    try { geneset.zipfilesystem.close(); } catch( Exception e ) { e.printStackTrace(); };
                };

                NativeRun nr = new NativeRun( run );
                nr.runProcessBuilder("tRNAscan", commands, new Object[3], false, run, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
		windowmenu.getItems().add( runtrnascan );
		
		Menu		select = new Menu("Select");
		MenuItem breakpointselAction = new MenuItem("Select breakpoints");
		breakpointselAction.setOnAction( actionEvent -> {
				String spec = syncolorcomb.getSelectionModel().getSelectedItem();
				
				int rr = 0;
				for( Gene g : geneset.genelist ) {
					if( !spec.equals( g.getSpecies() ) && g.getSpecies().contains("eggert") ) {
						Annotation tv2 = g.getTegeval();
						Annotation n2 = tv2.getNext();
						Annotation p2 = tv2.getPrevious();
						
						GeneGroup gg = g.getGeneGroup();
						
						if( gg.getName().contains("rhodane") ) {
							System.err.println();
						}
						
						Teginfo ti = gg.getGenes( spec );
						int msimcount = 0;
						if( ti != null ) {
							for( Annotation tv1 : ti.tset ) {
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
								gtable.getSelectionModel().select(g);
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
		});
		MenuItem saveselAction = new MenuItem("Save selection");
		saveselAction.setOnAction( actionEvent -> {
			/*int[] rr = table.getSelectedRows();
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
			}*/
		});
		MenuItem selhiAction = new MenuItem("Select hilighted");
		selhiAction.setOnAction( actionEvent -> {
			for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
				gg.setSelected(true);
			}
		});
		MenuItem hiselAction = new MenuItem("Hilight selected");
		hiselAction.setOnAction( actionEvent -> {
			for (GeneGroup gg : geneset.allgenegroups) {
				if(gg.isSelected()) table.getSelectionModel().select(gg);
			}
		});
		select.getItems().add( breakpointselAction );
		select.getItems().add( saveselAction );
		select.getItems().add( selhiAction );
		select.getItems().add( hiselAction );
		select.getItems().add( new SeparatorMenuItem() );
		
		MenuItem showall = new MenuItem("Show all");
		showall.setOnAction( actionEvent -> {
				genefilterset.clear();
				updateFilter(table, label);
		});
		select.getItems().add( showall );
		MenuItem croptosel = new MenuItem("Crop to selection");
		croptosel.setOnAction( actionEvent -> {
			if( isGeneview() ) {
				Set<Gene> selitems = new HashSet<>( gtable.getSelectionModel().getSelectedItems() );
				geneFilteredList.setPredicate(selitems::contains);
			} else {
				Set<GeneGroup> selitems = geneset.allgenegroups.stream().filter(GeneGroup::isSelected).collect(Collectors.toSet()); //new HashSet<>( table.getSelectionModel().getSelectedItems() );
				filteredData.setPredicate(selitems::contains);
			}
		});
		select.getItems().add( croptosel );
		MenuItem cropToHilighted = new MenuItem("Crop to hilighted");
		cropToHilighted.setOnAction( actionEvent -> {
			if( isGeneview() ) {
				Set<Gene> selitems = new HashSet<>( gtable.getSelectionModel().getSelectedItems() );
				geneFilteredList.setPredicate(selitems::contains);
			} else {
				Set<GeneGroup> selitems = new HashSet<>( table.getSelectionModel().getSelectedItems() );
				filteredData.setPredicate(selitems::contains);
			}
		});
		select.getItems().add( cropToHilighted );
		MenuItem croptoinvsel = new MenuItem("Crop to inverted selection");
		croptoinvsel.setOnAction( actionEvent -> {
			if( isGeneview() ) {
				Set<Gene> selitems = new HashSet<>( gtable.getSelectionModel().getSelectedItems() );
				geneFilteredList.setPredicate( p -> !selitems.contains(p) );
			} else {
				Set<GeneGroup> selitems = geneset.allgenegroups.stream().filter(GeneGroup::isSelected).collect(Collectors.toSet());;
				filteredData.setPredicate( p -> !selitems.contains(p) );
			}
				/*genefilterset.clear();
				for (int i = 0; i < table.getItems().size(); i++) {
					if( !table.getSelectionModel().isSelected(i) ) {
						genefilterset.add(i);
					}
				}
				updateFilter(table, label);*/
		});
		select.getItems().add( croptoinvsel );
		MenuItem croptoinvhil = new MenuItem("Crop to inverted hilighted");
		croptoinvhil.setOnAction( actionEvent -> {
			if( isGeneview() ) {
				Set<Gene> selitems = new HashSet<>( gtable.getSelectionModel().getSelectedItems() );
				geneFilteredList.setPredicate( p -> !selitems.contains(p) );
			} else {
				Set<GeneGroup> selitems = new HashSet<>( table.getSelectionModel().getSelectedItems() );
				filteredData.setPredicate( p -> !selitems.contains(p) );
			}
				/*genefilterset.clear();
				for (int i = 0; i < table.getItems().size(); i++) {
					if( !table.getSelectionModel().isSelected(i) ) {
						genefilterset.add(i);
					}
				}
				updateFilter(table, label);*/
		});
		select.getItems().add( croptoinvhil );
		MenuItem removesel = new MenuItem("Remove selection");
		removesel.setOnAction( actionEvent -> {
			geneset.allgenegroups.forEach(p -> p.setSelected(false));
				// genefilterset.clear();
			//int[] rr = table.getSelectedRows();
			/*if (genefilterset.isEmpty()) {
				Set<Integer> ii = new HashSet<>();
				for (int r : table.getSelectionModel().getSelectedIndices())
					ii.add(r);
				for (int i = 0; i < geneset.genelist.size(); i++) {
					if (!ii.contains(i))
						genefilterset.add(i);
				}
			} else {
				for (int r : table.getSelectionModel().getSelectedIndices()) {
					//int mr = table.convertRowIndexToModel(r);
					genefilterset.remove(r);
				}
			}*/
		});
		select.getItems().add( removesel );
		MenuItem removehilight = new MenuItem("Remove hilight");
		removehilight.setOnAction( actionEvent -> {
			table.getSelectionModel().clearSelection();
			updateFilter(table, label);
		});
		select.getItems().add( removehilight );
		MenuItem invsel = new MenuItem("Invert selection");
		invsel.setOnAction( actionEvent -> {
			geneset.allgenegroups.forEach(p -> p.setSelected(!p.isSelected()));
		});
		select.getItems().add( invsel );
		MenuItem invhil = new MenuItem("Invert hilight");
		invhil.setOnAction( actionEvent -> {
			ObservableList<GeneGroup> selitems = table.getSelectionModel().getSelectedItems();
			List<GeneGroup> newsel = new ArrayList<>(filteredData);
			newsel.removeAll( selitems );

			table.getSelectionModel().clearSelection();
			newsel.forEach(gg -> table.getSelectionModel().select(gg) );

			// genefilterset.clear();
			//int[] rr = table.getSelectedRows();
				/*Set<Integer> iset = new HashSet<>();
				for( int r : table.getSelectionModel().getSelectedIndices() ) {
					iset.add( r );
				}
				table.getSelectionModel().clearSelection();
				for (int r = 0; r < table.getItems().size(); r++) {
					if( !iset.contains(r) ) table.getSelectionModel().select(r);
					/*if (table.isRowSelected(r))
						table.removeRowSelectionInterval(r, r);
					else
						table.addRowSelectionInterval(r, r);
				}*/
		});
		select.getItems().add( invhil );
		//select.addSeparator();
		select.getItems().add( new SeparatorMenuItem() );
		MenuItem selsinglemult = new MenuItem("Select single copy genes found in multiple strains");
		selsinglemult.setOnAction( actionEvent -> {
				Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
				for( GeneGroup gg : geneset.allgenegroups ) {
					Set<String> checkspec = new HashSet<>( gg.species.keySet() );
					checkspec.retainAll( specset );
					if( gg.getCommonTag() == null && checkspec.size() > 1 && gg.getTegevals().size() == gg.species.size() ) {//gg.getTegevals(checkspec).size() == checkspec.size() ) {
						table.getSelectionModel().select( gg );
						//table.setro
					}
				}
		});
		select.getItems().add( selsinglemult );
		MenuItem selsinglemultstrain = new MenuItem("Select single copy genes in accessory genome of multiple strains");
		selsinglemultstrain.setOnAction( actionEvent -> {
				Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
				for( GeneGroup gg : geneset.allgenegroups ) {
					Set<String> checkspec = new HashSet<String>( gg.species.keySet() );
					checkspec.retainAll( specset );
					if( gg.getCommonTag() == null && checkspec.size() > 1 && checkspec.size() < specset.size() && gg.getTegevals().size() == gg.species.size() ) {//gg.getTegevals(checkspec).size() == checkspec.size() ) {
						table.getSelectionModel().select(gg);
						//table.setro
					}
				}
		});
		select.getItems().add( selsinglemultstrain );

        MenuItem selgeneclusters = new MenuItem("Select gene clusters");
        selgeneclusters.setOnAction( actionEvent -> {
            SwingUtilities.invokeLater(() -> {
                Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
                Platform.runLater(() -> {
                    int total = 0;
                    for( GeneGroup gg : geneset.allgenegroups ) {
                        Set<Annotation> geneset = new HashSet<>();
                        for( String spec : specset ) {
                            if( gg.species.containsKey(spec) ) {
                                Teginfo ti = gg.species.get(spec);
                                geneset.addAll(ti.tset);
                            }
                        }
                        if ( geneset.size() > 1 ) {
                            table.getSelectionModel().select(gg);
                            total += geneset.size();
                        }
                    }

                    Dialog<String> ds = new Dialog<>();
                    ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    ds.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                    Label tf = new Label();
                    tf.setText( ""+total );
                    ds.getDialogPane().setContent(tf);
                    ds.showAndWait();
                    //table.setro
                });
            });
        });
        select.getItems().add( selgeneclusters );
		MenuItem selsinglecopygenes = new MenuItem("Select single copy genes");
		selsinglecopygenes.setOnAction( actionEvent -> {
		    SwingUtilities.invokeLater(() -> {
                Set<String> specset = getSelspec(GeneSetHead.this, geneset.specList);
                Platform.runLater(() -> {
                    for (GeneGroup gg : geneset.allgenegroups) {
                        if (gg.getTegevals().size() == gg.species.size()) {
                            table.getSelectionModel().select(gg);
                            //table.setro
                        }
                    }
                });
            });
		});
		select.getItems().add( selsinglecopygenes );
		MenuItem selduplgenes = new MenuItem("Select duplicated genes");
		selduplgenes.setOnAction( actionEvent -> {
				for( GeneGroup gg : geneset.allgenegroups ) {
					int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 2 ) {
							List<Annotation> ta = new ArrayList<>( ti.tset );
							if( ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1)) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						table.getSelectionModel().select(gg);
					}
				}
		});
		select.getItems().add( selduplgenes );
		MenuItem seltriplgenes = new MenuItem("Select triplicated genes");
		seltriplgenes.setOnAction( actionEvent -> {
				for( GeneGroup gg : geneset.allgenegroups ) {
					int cnt = 0;
					for( String spec : gg.species.keySet() ) {
						Teginfo ti = gg.species.get( spec );
						if( ti.tset.size() == 3 ) {
							List<Annotation> ta = new ArrayList<>( ti.tset );
							if( (ta.get(0).getNext() == ta.get(1) || ta.get(0).getPrevious() == ta.get(1))
									&& (ta.get(1).getNext() == ta.get(2) || ta.get(1).getPrevious() == ta.get(2))) cnt++;
						}
					}
					if( (float)cnt / (float)gg.species.size() > 0.7 ) {
						table.getSelectionModel().select(gg);
					}
				}
		});
		select.getItems().add( seltriplgenes );
		
		MenuItem selplasmidgenes = new MenuItem("Select plasmid genes");
		selplasmidgenes.setOnAction( actionEvent -> {
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.isOnAnyPlasmid() ) {
						table.getSelectionModel().select(gg);
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
		});
		select.getItems().add( selplasmidgenes );
		
		MenuItem selectphagegenes = new MenuItem("Select phage genes");
		selectphagegenes.setOnAction( actionEvent -> {
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.isInAnyPhage() ) {
						table.getSelectionModel().select(gg);
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
		});
		select.getItems().add( selectphagegenes );
		select.getItems().add( new SeparatorMenuItem() );
		MenuItem	selectsharingaction = new MenuItem("Select sharing");
		selectsharingaction.setOnAction( actionEvent -> {
				RadioButton panbtn = new RadioButton("Pan");
				RadioButton corebtn = new RadioButton("Core");
				RadioButton blehbtn = new RadioButton("Only in");
				RadioButton selcorebtn = new RadioButton("Selection Core");
				RadioButton selpanbtn = new RadioButton("Selection Pan");
				ToggleGroup	tg = new ToggleGroup();
				panbtn.setToggleGroup( tg );
				corebtn.setToggleGroup( tg );
				blehbtn.setToggleGroup( tg );
				selcorebtn.setToggleGroup( tg );
				selpanbtn.setToggleGroup( tg );
				
				HBox sp = new HBox();
				sp.getChildren().add( panbtn );
				sp.getChildren().add( corebtn );
				sp.getChildren().add( blehbtn );
				sp.getChildren().add( selcorebtn );
				sp.getChildren().add( selpanbtn );
				Scene scene = new Scene(sp);
				
				//FlowLayout flowlayout = new FlowLayout();
				final JFXPanel c = new JFXPanel();
				c.setScene(scene);
				
				/*Group  root  =  new  Group();
		        Scene  scene  =  new  Scene(root, javafx.scene.paint.Color.ALICEBLUE);
		        root.getChildren().add(panbtn);
		        root.getChildren().add(corebtn);
		        root.getChildren().add(blehbtn);
				JFXPanel fxpanel = new JFXPanel();
				fxpanel.setScene( scene );*/
				//bg.add( panbtn );
				//bg.add( corebtn );
				//bg.add( blehbtn );
				corebtn.setSelected( true );
				//Object[] objs = new Object[] { panbtn, corebtn };
				//JOptionPane.showMessageDialog( geneset, objs, "Select id types", JOptionPane.PLAIN_MESSAGE );
				
				SwingUtilities.invokeLater(() -> {
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
					JTable jtable = new JTable( model );
					jtable.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
					JScrollPane	scroll = new JScrollPane( jtable );

					Object[] objs = new Object[] { scroll, c };
					JOptionPane.showMessageDialog(comp, objs);

					final Set<String>	specs = new HashSet<>();
					int[] rr = jtable.getSelectedRows();
					for( int r : rr ) {
						String spec = (String)jtable.getValueAt(r, 0);
						specs.add( spec );
					}

					Platform.runLater(() -> {
						isTableSelectListenerEnabled = false;
						for( GeneGroup gg : geneset.allgenegroups ) {
							if( blehbtn.isSelected() ) {
								Set<String> ss = new HashSet<>(gg.species.keySet());
								ss.removeAll(specs);
								if (ss.size() == 0) {
									//int i = GeneSetHead.this.sortedData.indexOf(gg);
									//System.err.println(i);
									//ii.add(i);
									//GeneSetHead.this.table.getSelectionModel().select(gg);
									gg.setSelected(true);
								}
							} else if( selpanbtn.isSelected() ) {
								Set<String> ss = new HashSet<>(specs);
								ss.removeAll(gg.species.keySet());
								if (ss.size() < specs.size()) {
									gg.setSelected(true);
								}
							} else if( selcorebtn.isSelected() ) {
								Set<String> ss = new HashSet<>(specs);
								ss.removeAll(gg.species.keySet());
								if( ss.size() == 0 ) {
									gg.setSelected(true);
								}
							} else {
								if( gg.species.keySet().containsAll( specs ) && (panbtn.isSelected() || specs.size() == gg.species.size()) ) {
									//GeneSetHead.this.table.getSelectionModel().select(gg);
									gg.setSelected(true);
								}
							}
						}
						//GeneSetHead.this.table.getSelectionModel().selectIndices(0, ii.stream().mapToInt(i -> i).toArray());
						isTableSelectListenerEnabled = true;
						table.refresh();
					});
				});
		});
		select.getItems().add( selectsharingaction );
		MenuItem	selectdirtyaction = new MenuItem("Select dirty");
		selectdirtyaction.setOnAction( actionEvent -> {
			if( !isGeneview() ) {
				for( GeneGroup gg : geneset.allgenegroups ) {
					if( gg.containsDirty() ) {
						table.getSelectionModel().select(gg);
					}
				}
			}
		});
		select.getItems().add( selectdirtyaction );
		MenuItem	selectdesignationaction = new MenuItem("Select designation");
		selectdesignationaction.setOnAction( actionEvent -> {
			SwingUtilities.invokeLater(() -> {
				JComboBox<String>	descombo = new JComboBox<String>( geneset.deset.toArray(new String[0]) );
				descombo.insertItemAt("", 0);
				descombo.setSelectedIndex( 0 );

				JOptionPane.showMessageDialog(null, descombo);
				String seldes = (String)descombo.getSelectedItem();
				if( !isGeneview() ) {
					int i = 0;
					for( GeneGroup gg : geneset.allgenegroups ) {
						if( gg.genes != null ) for( Annotation a : gg.genes ) {
							if( seldes.equals(a.designation) ) {
								table.getSelectionModel().select(gg);
							}
						}
						i++;
					}
				}
			});
		});
		select.getItems().add( selectdesignationaction );
		
		MenuItem blastselect = new MenuItem("Blast select");
		blastselect.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> blast(false)) );
		select.getItems().add( blastselect );
		
		MenuItem blastxselect = new MenuItem("Blastx select");
		blastxselect.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> blast(true)) );
		select.getItems().add( blastxselect );
		
		MenuItem blastnselect = new MenuItem("Blastn select");
		blastnselect.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> blastn(true)) );
		select.getItems().add( blastnselect );
		
		MenuItem blastsearch = new MenuItem("Blastn search");
		blastsearch.setOnAction( actionEvent -> SwingUtilities.invokeLater(() -> blastn(false)) );
		select.getItems().add( blastsearch );
		
		menubar.getMenus().add( file );
		menubar.getMenus().add( edit );
		menubar.getMenus().add( view );
		menubar.getMenus().add( sequencemenu );
		menubar.getMenus().add( windowmenu );
		menubar.getMenus().add( select );
		menubar.getMenus().add( help );
		
		if( comp != null ) {
			final Window window = SwingUtilities.windowForComponent(comp);
			initFSKeyListener(window);
			if ( comp instanceof JFrame || window instanceof JFrame) {
				JFrame frame = (JFrame)( window == null ? comp : window );
				if (!frame.isResizable())
					frame.setResizable(true);
	
				frame.addKeyListener(keylistener);
				frame.setJMenuBar(jmenubar);
			}
		}

		final Button jb = new Button("Atlas");
		jb.setOnAction( event -> {
			try {
				URL url = new URL("file:///home/sigmar/workspace/distann/bin/circle.html");
				//GeneSetHead.this.getAppletContext().showDocument(url, "_blank");
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		});
		
		try {
			newSoft(jb, comp, genetable, upper, lower, toolbar, btoolbar, null, selcomb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String colorToRGBString( Color c ) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		return "rgba("+r+","+g+","+b+",0.0)";
	}

	public String colorToString( Color c ) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		String red = r < 16 ? 0+Integer.toString(r, 16) : Integer.toString(r, 16);
		String green = g < 16 ? 0+Integer.toString(g, 16) : Integer.toString(g, 16);
		String blue = b < 16 ? 0+Integer.toString(b, 16) : Integer.toString(b, 16);

		return "#"+red+green+blue;
	}
	
	public void cellRender( TableCell<GeneGroup,Teginfo> cell, Teginfo ti, int row ) {
		cell.setStyle( "-fx-background-color: white" );
		String spec = syncolorcomb.getSelectionModel().getSelectedItem();
		if( spec != null && spec.length() > 0 ) {
			if( spec.equals("All") ) {
				cell.setStyle( "-fx-background-color: green" );
				for( Annotation tv : ti.tset ) {
					Gene gene = tv.getGene();
					if(gene!=null) {
						String tspec = gene.getSpecies();
						List<Sequence> scontigs = geneset.speccontigMap.get(tspec);
						GeneGroup gg = tv.getGene().getGeneGroup();
						double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
						if (ratio == -1) {
							ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
							cell.setStyle("-fx-background-color: " + colorToString(GeneCompare.gradientGrayscaleColor(ratio)));
							//label.setForeground( Color.white );
						} else {
							cell.setStyle("-fx-background-color: " + colorToString(GeneCompare.gradientColor(ratio)));
							//label.setForeground( Color.black );
						}
					}
					break;
					//GeneCompare.gradientColor();
				}
			} else {
				List<Sequence> contigs = geneset.speccontigMap.get(spec);
				cell.setStyle("-fx-background-color: green");

				//GeneGroup 	gg = ti.best.getGene().getGeneGroup();
				//Teginfo		gene2s = gg.getGenes(spec);
				//double ratio = -1.0;
				int msimcount = 0;
				Annotation tv2;

				GeneGroup gg = table.getItems().get(row);
				if (gg != null) {
					Teginfo gene2s = gg.getGenes(spec);
					if( gene2s != null ) {
						Annotation tv = gene2s.best;
						tv2 = gene2s.best;
						if (gene2s.tset != null) for (Annotation tv1 : gene2s.tset) {
							int simcount = 0;

							Annotation n = tv1.getNext();
							Annotation p = tv1.getPrevious();
							Annotation n2 = tv2.getNext();
							Annotation p2 = tv2.getPrevious();

							if (n != null && n.getGene() != null) {
								GeneGroup ngg = n.getGene().getGeneGroup();
								if (n2 != null && n2.getGene() != null) {
									if (ngg == n2.getGene().getGeneGroup()) simcount++;
								}

								if (p2 != null && p2.getGene() != null) {
									if (ngg == p2.getGene().getGeneGroup()) simcount++;
								}
							}

							if (p != null && p.getGene()!=null) {
								GeneGroup pgg = p.getGene().getGeneGroup();
								if (n2 != null && n2.getGene() != null) {
									if (pgg == n2.getGene().getGeneGroup()) simcount++;
								}

								if (p2 != null && p2.getGene() != null) {
									if (pgg == p2.getGene().getGeneGroup()) simcount++;
								}
							}

							//double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
							if (simcount >= msimcount) {
								tv = tv1;
								msimcount = simcount;
							}

							//double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
							//GeneCompare.gradientColor();
						}

						double ratio = GeneCompare.invertedGradientRatio(spec, contigs, tv);
						if (ratio == -1) {
							ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, gg);
							String color = colorToString(GeneCompare.gradientGrayscaleColor(ratio));
							cell.setStyle("-fx-background-color: " + color);
							//label.setForeground( Color.black );
						} else {
							String color = colorToString(GeneCompare.gradientColor(ratio));
							cell.setStyle("-fx-background-color: " + color);
							//label.setForeground( Color.black );
						}
					}
				}
			}
		} else {
			boolean plasmid = false;
			boolean phage = false;
			for( Annotation tv : ti.tset ) {
				phage = phage | tv.isPhage();
				Sequence seq = tv.getContshort();
				plasmid = plasmid | (seq != null && seq.isPlasmid());
			}
			
			if( phage && plasmid ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkmagenta" );
				else cell.setStyle( "-fx-background-color: magenta" );
			} else if( phage ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkblue" );
				else cell.setStyle( "-fx-background-color: blue" );
			} else if( plasmid ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkred" );
				else cell.setStyle( "-fx-background-color: red" );
			} else {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkgreen" );
				else cell.setStyle( "-fx-background-color: green" );
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

	public boolean cellRenderGene( TableCell<Gene,Teg> cell, Teg tev ) {
		Annotation tv;
		if( tev instanceof Tegeval ) {
			tv = (Tegeval)tev;
		} else if( tev instanceof Teginfo ) {
			tv = ((Teginfo)tev).best;
		} else {
			cell.setStyle("-fx-background-color: white");
			return false;
		}
		String spec = syncolorcomb.getSelectionModel().getSelectedItem();
		if( spec != null && spec.length() > 0 ) {
			if( spec.equals("All") ) {
				String tspec = tv.getGene().getSpecies();
				List<Sequence> scontigs = geneset.speccontigMap.get( tspec );
				GeneGroup gg = tv.getGene().getGeneGroup();
				double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
				if( ratio == -1 ) {
					ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
					cell.setStyle( "-fx-background-color: "+colorToString(GeneCompare.gradientGrayscaleColor( ratio ))+(cell.isSelected()?";-fx-text-fill: black;":";-fx-text-fill: white;") );
					//label.setForeground( Color.white );
				} else {
					cell.setStyle( "-fx-background-color: "+colorToString(GeneCompare.gradientColor( ratio ))+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
					//label.setForeground( Color.black );
				}
			} else {
				List<Sequence> contigs = geneset.speccontigMap.get( spec );
				Annotation tv2 = null;
				GeneGroup gg = tv.getGene().getGeneGroup();
				int msimcount = 0;
				if( gg != null ) {
					Teginfo		gene2s = gg.getGenes(spec);
					if( gene2s != null && gene2s.tset != null ) for( Annotation tv1 : gene2s.tset ) {
						int simcount = 0;

						Annotation n = tv1.getNext();
						Annotation p = tv1.getPrevious();
						Annotation n2 = tv.getNext();
						Annotation p2 = tv.getPrevious();

						if( n != null ) {
							GeneGroup ngg = n.getGene() == null ? null : n.getGene().getGeneGroup();
							if( n2 != null && n2.getGene() != null ) {
								if( ngg == n2.getGene().getGeneGroup() ) simcount++;
							}

							if( p2 != null && p2.getGene() != null ) {
								if( ngg == p2.getGene().getGeneGroup() ) simcount++;
							}
						}

						if( p != null ) {
							GeneGroup pgg = p.getGene() == null ? null : p.getGene().getGeneGroup();
							if( n2 != null && n2.getGene() != null ) {
								if( pgg == n2.getGene().getGeneGroup() ) simcount++;
							}

							if( p2 != null && p2.getGene() != null ) {
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
					String color = colorToString(GeneCompare.gradientGrayscaleColor( ratio ));
					cell.setStyle( "-fx-background-color: "+color+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
					//label.setForeground( Color.black );
				} else {
					String color = colorToString(GeneCompare.gradientColor( ratio ));
					cell.setStyle( "-fx-background-color: "+color+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
					//label.setForeground( Color.black );
				}
				/*double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
				label.setBackground( GeneCompare.gradientColor( ratio ) );*/
			}
		} else {
			Gene g = tv.getGene();
			GeneGroup gg = g.getGeneGroup();
			Teginfo ti = gg.species.get( g.getSpecies() );

			boolean phage = tv.isPhage();
			boolean plasmid = tv.getContshort() != null && tv.getContshort().isPlasmid();
			if( phage && plasmid ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkmagenta" );
				else cell.setStyle( "-fx-background-color: magenta" );
			} else if( phage ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkblue" );
				else cell.setStyle( "-fx-background-color: blue" );
			} else if( plasmid ) {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkred" );
				else cell.setStyle( "-fx-background-color: red" );
			} else {
				if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkgreen" );
				else cell.setStyle( "-fx-background-color: green" );
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
		return true;
	}
	
	public void stop() {
		try {
			if( geneset.zippath != null ) geneset.saveContigOrder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newFile() {
		FileChooser fc = new FileChooser();
		//fc.getExtensionFilters().add( new ExtensionFilter("Zip files", "*.zip") );
		File f = fc.showSaveDialog(primaryStage);
		if( f != null ) geneset.zippath = f.toPath();
	}
	
	public void fetchGenomes() {
		if( geneset.zippath == null ) {
			newFile();
		}
		
		VBox vbox = new VBox();
		
		Stage stage = new Stage();
		stage.setTitle("Fetch genomes");
		stage.setScene( new Scene(vbox) );
		stage.initOwner( primaryStage );
		
		//frame.setSize(400, 600);
		
		try {
			Map<String,Object> env = new HashMap<>();
			env.put("create", "true");
			env.put("useTempFile", Boolean.TRUE);
			//Path path = zipfile.toPath();
			String uristr = "jar:" + geneset.zippath.toUri();
			geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
			geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
			
			final SerifyApplet	sa = new SerifyApplet( geneset.zipfilesystem, geneset.noseq );
			sa.init( null, vbox, geneset.user );

			for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
				Files.list(root).filter(t -> {
                    String fname = t.getFileName().toString();
                    return /*fname.endsWith(".gbk") || */fname.endsWith(".fna") || fname.endsWith("fastg") || fname.endsWith(".fsa") || fname.endsWith(".fa") || fname.endsWith(".fasta") || fname.endsWith(".aa") || fname.endsWith(".nn") || fname.endsWith(".trna") || fname.endsWith(".rrna") || fname.endsWith(".ssu") || fname.endsWith(".lsu") || fname.endsWith(".tsu");
                }).forEach(t -> {
                    try {
                        sa.addSequences(t.getFileName().toString(), t, null);
                    } catch (URISyntaxException | IOException e) {
                        e.printStackTrace();
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
		
		/*frame.addWindowListener( new WindowListener() {
			
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
		
		frame.setVisible( true );*/
		
		stage.setOnCloseRequest(event -> {
            try {
                geneset.zipfilesystem.close();
                geneset.cleanUp();
                importStuff( geneset.zippath );
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
		stage.show();
	}

	private BufferedReader getResource(String res) throws IOException {
		InputStream is = GeneSet.class.getClassLoader().getResourceAsStream(res);
		if(is != null) {
			InputStreamReader isr = new InputStreamReader(is);
			return new BufferedReader(isr);
		}
		return Files.newBufferedReader(Paths.get("distann/src/main/java/"+res ));
	}

	public void saveGeneGroups(List<GeneGroup> gg, List<GeneGroup> c) {
		Map<String,String> env = new HashMap<>();
		env.put("create", "true");
		try(var zfsystem = FileSystems.newFileSystem( geneset.zipuri, env )) {
			//geneset.zipfilesystem = zfsystem;
			for( Path root : zfsystem.getRootDirectories() ) {
				try(var fstream = Files.walk(root)) {
					fstream.filter(f -> f.toString().startsWith("/aligned"))
							//.filter( f -> f.toString().endsWith(".aa") )
							.filter(f -> {
								String filename = f.getFileName().toString();
								return gg.stream().flatMap(ggg -> ggg.genes.stream()).anyMatch(g -> g.getId().equals(filename));
							}).forEach(p -> {
								try {
									Files.deleteIfExists(p);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							});
				}
						/*for( Gene g : gg.genes ) {
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
						}*/
				final Path p = root.resolve("/aligned");
				for (GeneGroup fgg : c) {
					Path np = p.resolve(fgg.genes.iterator().next().getId());
					try (Writer w = Files.newBufferedWriter(np)) {
						fgg.getFasta(w, false);
					}
				}

				var clustersPath = root.resolve("simpleclusters.txt");
				try(var fos = Files.newBufferedWriter(clustersPath, StandardOpenOption.TRUNCATE_EXISTING)) {
					var gset = geneset.allgenegroups.stream().map(ggg -> ggg.genes.stream().map(Annotation::getId).collect(Collectors.toSet()));
					geneset.writeClusters(fos, gset);
				}
				break;
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void saveSimpleClusters() {
		Map<String,String> env = new HashMap<>();
		env.put("create", "true");
		try(var zfsystem = FileSystems.newFileSystem( geneset.zipuri, env )) {
			//geneset.zipfilesystem = zfsystem;
			for( Path root : zfsystem.getRootDirectories() ) {
				var clustersPath = root.resolve("simpleclusters.txt");
				try(var fos = Files.newBufferedWriter(clustersPath, StandardOpenOption.TRUNCATE_EXISTING)) {
					var gset = geneset.allgenegroups.stream().map(ggg -> ggg.genes.stream().map(Annotation::getId).collect(Collectors.toSet()));
					geneset.writeClusters(fos, gset);
				}
				break;
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	Set<GeneGroup> updateSplit(GeneGroup gg, double d, double l) {
		Set<GeneGroup> ggmap = new HashSet<>();
		Map<String,Integer> blosumMap = JavaFasta.getBlosumMap( true );
		for( Annotation a : gg.genes ) {
			if( ggmap.stream().flatMap( f -> f.genes.stream() ).noneMatch( p -> a == p ) ) {
				Set<Annotation> ggset = new HashSet<>();
				Sequence seq1 = a.getAlignedSequence();
				for (Annotation ca : gg.genes) {
					Sequence seq2 = ca.getAlignedSequence();

					int[] tscore = GeneCompare.blosumValue(seq1, seq1, seq2, blosumMap);
					int sscore = GeneCompare.blosumValue(seq1, seq2, blosumMap);

					double dval = (double) (sscore - tscore[1]) / (double) (tscore[0] - tscore[1]);

					/*int[] basescore_count = GeneCompare.blosumValueCount(seq1, seq1, seq2, blosumMap);
					int[] score_count = GeneCompare.blosumValueCount(seq1, seq2, blosumMap);
					int tscore = basescore_count[0];
					int sscore = score_count[0];
					int scount = score_count[1];
					double dval = (double) (sscore) / (double) (tscore);
					double lval = (double)scount/(double)seq1.getUnalignedLength();
					if (dval > d && lval > l) {
						ggset.add(ca);
					}*/

					if (dval > d) {
						ggset.add(ca);
					}
				}
				System.err.println( ggset.size() );

				Set<GeneGroup> osubgg = ggmap.stream().filter( f -> {
					Set<Annotation> gs = new HashSet<>(ggset); gs.retainAll(f.genes); return gs.size() > 0;
				}).collect(Collectors.toSet());
				GeneGroup subgg;
				if( osubgg.size() > 0 ) {
					Iterator<GeneGroup> git = osubgg.iterator();
					subgg = git.next();
					while( git.hasNext() ) {
						GeneGroup remgg = git.next();
						subgg.addGenes( remgg.genes );
						ggmap.remove( remgg );
					}
				} else {
					subgg = new GeneGroup();
					subgg.setCogMap( gg.getCogMap() );
					subgg.setKonameMap( gg.getKonameMap() );
					subgg.setSpecSet( gg.getSpecSet() );
					ggmap.add( subgg );
				}
				subgg.addGenes( ggset );
			}
		}
		Set<GeneGroup> sgg = new HashSet<>(ggmap);
		return sgg;
	}
	
	private void showGeneTable(/*final Map<String, Gene> genemap, final List<Gene> genelist, 
			final List<Function> funclist, final List<Set<String>> iclusterlist, final List<Set<String>> uclusterlist,
			final Map<Set<String>, ShareNum> specset,*/ final Map<Set<String>, GeneSet.ClusterInfo> clustInfoMap, final Button jb, final TableView<Gene> genetable, final TableView<Function> upper, final TableView<GeneGroup> lower,
							   final ToolBar toolbar, final ToolBar btoolbar, final Container comp, final JPanel applet, final ComboBox<String> selcomblocal) throws IOException {
		//JSplitPane splitpane = new JSplitPane();
		//splitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		//splitpane.setDividerLocation(400);
		//JScrollPane scrollpane = new JScrollPane();
		
		/*table = new JTable() {
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
		};*/

		//table.setDragEnabled(true);
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
							Map<String, List<Annotation>> contigs = new HashMap<>();
							StringBuilder ret = new StringBuilder();
							for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
								Annotation tv = gg.getTegeval();
								if (!contigs.containsKey(tv.getContshort())) {
									List<Annotation> ltv = new ArrayList<>();
									ltv.add(tv);
									contigs.put(tv.getContshort().getName(), ltv);
								} else {
									List<Annotation> ltv = contigs.get(tv.getContshort());
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
								List<Annotation> tv = contigs.get(cont);
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
								List<Annotation> ltv = contigs.get(cont);
								ret.append(">" + cont + "\n"); // + " " + tv.teg
																// + " " +
																// tv.eval +
																// "\n" );
								for (Annotation tv : ltv) {
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
					int b = Arrays.binarySearch(support.getDataFlavors(), DataFlavor.javaFileListFlavor, (o1, o2) -> o1 == o2 ? 1 : 0);

					try {
						obj = support.getTransferable().getTransferData(DataFlavor.imageFlavor);
					} catch (UnsupportedFlavorException | IOException e) {
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
					} catch (UnsupportedFlavorException | IOException e) {
						e.printStackTrace();
					}

					return true;
				}
			};
			//table.setTransferHandler(th);
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}

		final Color darkgreen = new Color( 0, 128, 0 );
		final Color darkred = new Color( 128, 0, 0 );
		final Color darkblue = new Color( 0, 0, 128 );
		final Color darkmag = new Color( 128, 0, 128 );
		/*table.setDefaultRenderer(Teg.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if( value == null ) {
					label.setBackground(Color.white);
				} else {
					cellRender();
				}
				return label;
			}
		});*/

		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.setAutoCreateRowSorter(true);
		//scrollpane.setViewportView(table);

		Set<String> current = null;
		Set<String> currentko = null;

		//FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
		//Path jrtroot = fs.getPath(".");
		//Files.walk(jrtroot).filter( p -> p.toString().contains("distann")).forEach( System.err::println );

		BufferedReader br = getResource("kegg_pathways");
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				current = new HashSet<>();
				currentko = new HashSet<>();
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
		
		

		final TextField textfield = new TextField();
		//JComponent topcomp = new JComponent() {};
		//topcomp.setLayout(new BorderLayout());
		//topcomp.add(scrollpane);

		textfield.setPrefSize( 350, 25 );
		
		final RadioButton	search = new RadioButton("Search");
		final RadioButton	filter = new RadioButton("Filter");
		
		ToggleGroup bgsf = new ToggleGroup();
		search.setToggleGroup( bgsf );
		filter.setToggleGroup( bgsf );
		//ButtonGroup bgsf = new ButtonGroup();
		//bgsf.add( search );
		//bgsf.add( filter );
		
		filter.setSelected( true );
		
		//ToolBar topcombo = new ToolBar();
		// topcombo.
		// topcombo.setLayout( new FlowLayout() );
		
		specombo = new ComboBox<>();
		combo = new ComboBox<>();
				
		specombo.getItems().add("Select blast species");
		combo.getItems().add("Select pathway");
		btoolbar.getItems().add(combo);
		btoolbar.getItems().add(specombo);
		//topcomp.add(topcombo, BorderLayout.SOUTH);

		//JComponent ttopcom = new JComponent() {};
		//ttopcom.setLayout(new FlowLayout());

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
		
		toolbar.getItems().add(textfield);
		toolbar.getItems().add(search);
		toolbar.getItems().add(filter);
		toolbar.getItems().add(label);

		selcomblocal.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String key = newValue;
            /*if( ((GeneSetHead)comp).selectionMap.containsKey(key) ) {
                Set<Integer> val = ((GeneSetHead)comp).selectionMap.get(key);
                if( val != null ) {
                    table.getSelectionModel().clearSelection();
                    for( int i : val ) {
                        //int r = table.convertRowIndexToView(i);
                        table.getSelectionModel().select( i );
                    }
                } else {
                    System.err.println( "null "+key );
                }
            } else {
                System.err.println( "no "+key );
            }*/
        });
		toolbar.getItems().add(selcomblocal);
		
		
		/*syncolorcomb.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String spec = (String)syncolorcomb.getSelectedItem();
				//if( spec.length() > 0 )
			}
		});*/
		toolbar.getItems().add( searchcolcomb );
		toolbar.getItems().add( syncolorcomb );
		//topcomp.add(ttopcom, BorderLayout.NORTH);

		table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		/*table.getSelectionModel().selectedItemProperty().addListener( e -> {
			label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
		});*/
		
		gtable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		gtable.getSelectionModel().selectedItemProperty().addListener( e -> {
			label.setText(gtable.getItems().size() + "/" + gtable.getSelectionModel().getSelectedItems().size());
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
		
		final TextField ftextfield = new TextField();
		btoolbar.getItems().add( ftextfield );

		ComboBox<String> scombo = new ComboBox();
		scombo.getItems().add("5S/8S");
		scombo.getItems().add("16S/18S");
		scombo.getItems().add("23S/28S");
		scombo.getSelectionModel().selectedItemProperty().addListener( e -> {
			String name = e.toString().split("/")[0];
			InputStream iss = GeneSet.class.getResourceAsStream("/all" + name + ".fsa");
			InputStreamReader isr = new InputStreamReader(iss);
			BufferedReader brr = new BufferedReader(isr);

			JTextArea textarea = new JTextArea();
			JScrollPane scrollpane = new JScrollPane(textarea);

			try {
				String ln = brr.readLine();
				while (ln != null) {
					textarea.append(ln + "\n");

					ln = brr.readLine();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(scrollpane);
			frame.setSize(400, 300);
			frame.setVisible(true);
		});
		btoolbar.getItems().add(scombo);

		Button swsearch = new Button("SW Search");
		swsearch.setOnAction( e -> {
			JComponent c = new JComponent() {};
			final JProgressBar pb = new JProgressBar();
			final JTextArea textarea = new JTextArea();
			JButton searchbut = new JButton(new AbstractAction("Blast") {
				@Override
				public void actionPerformed(ActionEvent e) {
					final String fasta = textarea.getText();
					final SmithWater sw = new SmithWater();
					final InputStream is = GeneSet.class.getResourceAsStream("/allthermus.aa");
					new Thread(() -> {
						try {
							sw.fasta_align(new StringReader(fasta), new InputStreamReader(is), pb);
							List<SmithWater.ALN> alns = sw.getAlignments();
							SmithWater.ALN first = null;
							int count = 0;
							String result = "";
							Set<String> regnames = new HashSet<>();
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
								Annotation tv = g.getTegeval();
								if (regnames.contains(tv.getName())) {
									found = true;
									break;
								}
								if (found) {
									gtable.getSelectionModel().select(g);
									break;
								}
							}
						} catch (IOException e12) {
							e12.printStackTrace();
						}
					}).start();
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
		});
		btoolbar.getItems().add(swsearch);
		btoolbar.getItems().add(jb);

		TableColumn<GeneGroup, String> namedesccol = new TableColumn("Desc");
		namedesccol.setCellValueFactory( new PropertyValueFactory<>("name"));
		namedesccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sortedData.comparatorProperty().bind(table.comparatorProperty()));
		table.getColumns().add( namedesccol );
		TableColumn<GeneGroup, String> origincol = new TableColumn("Origin");
		origincol.setCellValueFactory( new PropertyValueFactory<>("origin"));
		table.getColumns().add( origincol );
		TableColumn<GeneGroup, String> geneidcol = new TableColumn("Genid");
		geneidcol.setCellValueFactory( new PropertyValueFactory<>("genid"));
		table.getColumns().add( geneidcol );
		TableColumn<GeneGroup, String> refidcol = new TableColumn("Refid");
		refidcol.setCellValueFactory( new PropertyValueFactory<>("refid"));
		table.getColumns().add( refidcol );
		TableColumn<GeneGroup, String> unidcol = new TableColumn("Unid");
		unidcol.setCellValueFactory( new PropertyValueFactory<>("unid"));
		table.getColumns().add( unidcol );
		TableColumn<GeneGroup, String> descol = new TableColumn("Designation");
		descol.setCellValueFactory( new PropertyValueFactory<>("designation"));
		table.getColumns().add( descol );
		TableColumn<GeneGroup, String> goidcol = new TableColumn("Goid");
		goidcol.setCellValueFactory( new PropertyValueFactory<>("goid"));
		table.getColumns().add( goidcol );
		TableColumn<GeneGroup, String> keggpathcol = new TableColumn("Kegg pathway");
		keggpathcol.setCellValueFactory( new PropertyValueFactory<>("keggPathway"));
		table.getColumns().add( keggpathcol );
		TableColumn<GeneGroup, String> kocol = new TableColumn("KO");
		kocol.setCellValueFactory( new PropertyValueFactory<>("ko"));
		table.getColumns().add( kocol );
		TableColumn<GeneGroup, String> cazy = new TableColumn("Cazy");
		cazy.setCellValueFactory( new PropertyValueFactory<>("cazy"));
		table.getColumns().add( cazy );

		TableColumn<GeneGroup, String> dbcan = new TableColumn("Dbcan");
		dbcan.setCellValueFactory( new PropertyValueFactory<>("dbcan"));
		table.getColumns().add( dbcan );

		TableColumn<GeneGroup, String> phaster = new TableColumn("Phrog");
		phaster.setCellValueFactory( new PropertyValueFactory<>("phrog"));
		table.getColumns().add( phaster );

		TableColumn<GeneGroup, String> hhpred = new TableColumn("HHPred");
		hhpred.setCellValueFactory( new PropertyValueFactory<>("hhblits"));
		table.getColumns().add( hhpred );

		TableColumn<GeneGroup, String> hhpreduni = new TableColumn("HHPredUni");
		hhpreduni.setCellValueFactory( new PropertyValueFactory<>("hhblitsuni"));
		table.getColumns().add( hhpreduni );

		TableColumn<GeneGroup, String> symbcol = new TableColumn("Symbol");
		symbcol.setCellValueFactory( new PropertyValueFactory<>("symbol"));
		table.getColumns().add( symbcol );
		TableColumn<GeneGroup, String> konamecol = new TableColumn("KO name");
		konamecol.setCellValueFactory( new PropertyValueFactory<>("koname"));
		table.getColumns().add( konamecol );
		TableColumn<GeneGroup, String> pbidcol = new TableColumn("Pfam");
		pbidcol.setCellValueFactory( new PropertyValueFactory<>("Pfam"));
		table.getColumns().add( pbidcol );
		TableColumn<GeneGroup, String> eccol = new TableColumn("Ec");
		eccol.setCellValueFactory( new PropertyValueFactory<>("ec"));
		table.getColumns().add( eccol );
		TableColumn<GeneGroup, String> cognamecol = new TableColumn("Cog name");
		cognamecol.setCellValueFactory( new PropertyValueFactory<>("cogname"));
		table.getColumns().add( cognamecol );
		TableColumn<GeneGroup, String> cogcol = new TableColumn("Cog");
		cogcol.setCellValueFactory( new PropertyValueFactory<>("cog"));
		table.getColumns().add( cogcol );
		TableColumn<GeneGroup, String> cogannocol = new TableColumn("Cog annotation");
		cogannocol.setCellValueFactory( new PropertyValueFactory<>("coganno"));
		table.getColumns().add( cogannocol );
		TableColumn<GeneGroup, String> cogsymbcol = new TableColumn("Cog symbol");
		cogsymbcol.setCellValueFactory( new PropertyValueFactory<>("cogsymbol"));
		table.getColumns().add( cogsymbcol );

		TableColumn<GeneGroup, String> oldsymbcol = new TableColumn("Original cogsymbol");
		oldsymbcol.setCellValueFactory( new PropertyValueFactory<>("oldsymbol"));
		table.getColumns().add( oldsymbcol );

		TableColumn<GeneGroup, String> cazyaa = new TableColumn("Cazy_AA");
		cazyaa.setCellValueFactory( new PropertyValueFactory<>("cazyAA"));
		table.getColumns().add( cazyaa );
		TableColumn<GeneGroup, String> cazyce = new TableColumn("Cazy_CE");
		cazyce.setCellValueFactory( new PropertyValueFactory<>("cazyCE"));
		table.getColumns().add( cazyce );
		TableColumn<GeneGroup, String> cazygh = new TableColumn("Cazy_GH");
		cazygh.setCellValueFactory( new PropertyValueFactory<>("cazyGH"));
		table.getColumns().add( cazygh );
		TableColumn<GeneGroup, String> cazygt = new TableColumn("Cazy_GT");
		cazygt.setCellValueFactory( new PropertyValueFactory<>("cazyGT"));
		table.getColumns().add( cazygt );
		TableColumn<GeneGroup, String> cazypl = new TableColumn("Cazy_PL");
		cazypl.setCellValueFactory( new PropertyValueFactory<>("cazyPL"));
		table.getColumns().add( cazypl );
		TableColumn<GeneGroup, String> prescol = new TableColumn("Present in");
		prescol.setCellValueFactory( new PropertyValueFactory<>("presentin"));
		table.getColumns().add( prescol );
		
		TableColumn<GeneGroup, Integer> groupindcol = new TableColumn("Group index");
		groupindcol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupIndex"));
		table.getColumns().add( groupindcol );
		TableColumn<GeneGroup, Integer> groupcovcol = new TableColumn("Group coverage");
		groupcovcol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupCoverage"));
		table.getColumns().add( groupcovcol );
		TableColumn<GeneGroup, Integer> groupsizecol = new TableColumn("Group size");
		groupsizecol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupGeneCount"));
		table.getColumns().add( groupsizecol );
		
		TableColumn<GeneGroup, String> locprefcol = new TableColumn("Loc pref");
		locprefcol.setCellValueFactory( new PropertyValueFactory<>("locpref"));
		table.getColumns().add( locprefcol );
		TableColumn<GeneGroup, String> avgcpcol = new TableColumn("Avg GC%");
		avgcpcol.setCellValueFactory( new PropertyValueFactory<>("avggcp"));
		table.getColumns().add( avgcpcol );
		TableColumn<GeneGroup, String> maxlencol = new TableColumn("Max length");
		maxlencol.setCellValueFactory( new PropertyValueFactory<>("maxLength"));
		table.getColumns().add( maxlencol );
		TableColumn<GeneGroup, String> numloccol = new TableColumn("#Loc");
		numloccol.setCellValueFactory( new PropertyValueFactory<>("numloc"));
		table.getColumns().add( numloccol );
		TableColumn<GeneGroup, String> numlocgroupcol = new TableColumn("#Loc group");
		numlocgroupcol.setCellValueFactory( new PropertyValueFactory<>("numlocgroup"));
		table.getColumns().add( numlocgroupcol );
		
		TableColumn<GeneGroup, ShareNum> sharenumcol = new TableColumn("Sharing number");
		sharenumcol.setCellValueFactory( new PropertyValueFactory<>("sharingNumber"));
		table.getColumns().add( sharenumcol );
		TableColumn<GeneGroup, String> maxcyccol = new TableColumn("Max cyc");
		maxcyccol.setCellValueFactory( new PropertyValueFactory<>("maxCyc"));
		table.getColumns().add( maxcyccol );

		TableColumn<GeneGroup, Boolean> selectedcol = new TableColumn<>("Selected");
		selectedcol.setCellValueFactory(param -> param.getValue().selectedProperty());
		selectedcol.setCellFactory(CheckBoxTableCell.forTableColumn(selectedcol));
		selectedcol.setEditable(true);
		table.getColumns().add( selectedcol );

		TableColumn<Gene, String> gnamedesccol = new TableColumn("Desc");
		gnamedesccol.setCellValueFactory( new PropertyValueFactory<>("derivedName"));
		gtable.getColumns().add( gnamedesccol );
		TableColumn<Gene, String> gorigincol = new TableColumn("Origin");
		gorigincol.setCellValueFactory( new PropertyValueFactory<>("origin"));
		gtable.getColumns().add( gorigincol );
		TableColumn<Gene, String> ggeneidcol = new TableColumn("Genid");
		ggeneidcol.setCellValueFactory( new PropertyValueFactory<>("genid"));
		gtable.getColumns().add( ggeneidcol );
		TableColumn<Gene, String> grefidcol = new TableColumn("Refid");
		grefidcol.setCellValueFactory( new PropertyValueFactory<>("refid"));
		gtable.getColumns().add( grefidcol );
		TableColumn<Gene, String> gunidcol = new TableColumn("Unid");
		gunidcol.setCellValueFactory( new PropertyValueFactory<>("unid"));
		gtable.getColumns().add( gunidcol );
		TableColumn<Gene, String> gdescol = new TableColumn("Designation");
		gdescol.setCellValueFactory( new PropertyValueFactory<>("designation"));
		gtable.getColumns().add( gdescol );
		TableColumn<Gene, String> gkeggidcol = new TableColumn("Keggid");
		gkeggidcol.setCellValueFactory( new PropertyValueFactory<>("keggid"));
		gtable.getColumns().add( gkeggidcol );
		TableColumn<Gene, String> gkeggpathcol = new TableColumn("Kegg pathway");
		gkeggpathcol.setCellValueFactory( new PropertyValueFactory<>("keggPathway"));
		gtable.getColumns().add( gkeggpathcol );
		TableColumn<Gene, String> gkocol = new TableColumn("KO");
		gkocol.setCellValueFactory( new PropertyValueFactory<>("ko"));
		gtable.getColumns().add( gkocol );
		TableColumn<Gene, String> gksymbcol = new TableColumn("Ksymbol");
		gksymbcol.setCellValueFactory( new PropertyValueFactory<>("ksymbol"));
		gtable.getColumns().add( gksymbcol );
		TableColumn<Gene, String> gsymbcol = new TableColumn("Symbol");
		gsymbcol.setCellValueFactory( new PropertyValueFactory<>("symbol"));
		gtable.getColumns().add( gsymbcol );
		TableColumn<Gene, String> gkonamecol = new TableColumn("KO name");
		gkonamecol.setCellValueFactory( new PropertyValueFactory<>("koname"));
		gtable.getColumns().add( gkonamecol );
		TableColumn<Gene, String> hhpredcol = new TableColumn("Hhpred");
		hhpredcol.setCellValueFactory( new PropertyValueFactory<>("hhpred"));
		gtable.getColumns().add( hhpredcol );
		TableColumn<Gene, String> hhpredunicol = new TableColumn("HhpredUni");
		hhpredunicol.setCellValueFactory( new PropertyValueFactory<>("hhpreduni"));
		gtable.getColumns().add( hhpredunicol );
		TableColumn<Gene, String> gpbidcol = new TableColumn("Pbid");
		gpbidcol.setCellValueFactory( new PropertyValueFactory<>("pbid"));
		gtable.getColumns().add( gpbidcol );
		TableColumn<Gene, String> geccol = new TableColumn("Ec");
		geccol.setCellValueFactory( new PropertyValueFactory<>("ec"));
		gtable.getColumns().add( geccol );
		TableColumn<Gene, String> gcognamecol = new TableColumn("Cog name");
		gcognamecol.setCellValueFactory( new PropertyValueFactory<>("cogname"));
		gtable.getColumns().add( gcognamecol );
		TableColumn<Gene, String> gcogcol = new TableColumn("Cog");
		gcogcol.setCellValueFactory( new PropertyValueFactory<>("cog"));
		gtable.getColumns().add( gcogcol );
		TableColumn<Gene, String> gcogannocol = new TableColumn("Cog annotation");
		gcogannocol.setCellValueFactory( new PropertyValueFactory<>("coganno"));
		gtable.getColumns().add( gcogannocol );
		TableColumn<Gene, String> gcogsymbcol = new TableColumn("Cog symbol");
		gcogsymbcol.setCellValueFactory( new PropertyValueFactory<>("cogsymbol"));
		gtable.getColumns().add( gcogsymbcol );
		TableColumn<Gene, String> gcazycol = new TableColumn("Cazy");
		gcazycol.setCellValueFactory( new PropertyValueFactory<>("cazy"));
		gtable.getColumns().add( gcazycol );

		TableColumn<Gene, String> gdbcancol = new TableColumn("Dbcan");
		gdbcancol.setCellValueFactory( new PropertyValueFactory<>("dbcan"));
		gtable.getColumns().add( gdbcancol );

		TableColumn<Gene, String> gprescol = new TableColumn("Present in");
		gprescol.setCellValueFactory( new PropertyValueFactory<>("presentin"));
		gtable.getColumns().add( gprescol );

		TableColumn<Gene, Integer> ggroupindcol = new TableColumn("Group index");
		ggroupindcol.setCellValueFactory( new PropertyValueFactory<>("groupIndex"));
		gtable.getColumns().add( ggroupindcol );
		TableColumn<Gene, Integer> ggroupcovcol = new TableColumn("Group coverage");
		ggroupcovcol.setCellValueFactory( new PropertyValueFactory<>("groupCoverage"));
		gtable.getColumns().add( ggroupcovcol );
		TableColumn<Gene, Integer> ggroupsizecol = new TableColumn("Group size");
		ggroupsizecol.setCellValueFactory( new PropertyValueFactory<>("groupGeneCount"));
		gtable.getColumns().add( ggroupsizecol );

		TableColumn<Gene, String> glocprefcol = new TableColumn("Loc pref");
		glocprefcol.setCellValueFactory( new PropertyValueFactory<>("locpref"));
		gtable.getColumns().add( glocprefcol );
		TableColumn<Gene, String> gavgcpcol = new TableColumn("Avg GC%");
		gavgcpcol.setCellValueFactory( new PropertyValueFactory<>("avggcp"));
		gtable.getColumns().add( gavgcpcol );
		TableColumn<Gene, String> gmaxlencol = new TableColumn("Max length");
		gmaxlencol.setCellValueFactory( new PropertyValueFactory<>("maxLength"));
		gtable.getColumns().add( gmaxlencol );
		TableColumn<Gene, String> gnumloccol = new TableColumn("#Loc");
		gnumloccol.setCellValueFactory( new PropertyValueFactory<>("numloc"));
		gtable.getColumns().add( gnumloccol );
		TableColumn<Gene, String> gnumlocgroupcol = new TableColumn("#Loc group");
		gnumlocgroupcol.setCellValueFactory( new PropertyValueFactory<>("numlocgroup"));
		gtable.getColumns().add( gnumlocgroupcol );

		TableColumn<Gene, ShareNum> gsharenumcol = new TableColumn("Sharing number");
		gsharenumcol.setCellValueFactory( new PropertyValueFactory<>("sharingNumber"));
		gtable.getColumns().add( gsharenumcol );
		TableColumn<Gene, String> gmaxcyccol = new TableColumn("Max cyc");
		gmaxcyccol.setCellValueFactory( new PropertyValueFactory<>("maxCyc"));
		gtable.getColumns().add( gmaxcyccol );

		//gmaxcyccol.
		
		/*if( upper != null ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					//upper.setContent( botcomp );
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
				}*
				
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
				}*
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
					}*
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
					}*
					
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
		table.setModel( groupModel );*/
		//table.setModel( defaultModel );

		/*
		 * Comparator<Tegeval> wrapMe = new Comparator<Tegeval>() { public int
		 * compare(Tegeval o1, Tegeval o2) { return o1.compareTo(o2); } };
		 * DefaultRowSorter<TableModel, Integer> rowsorter =
		 * (DefaultRowSorter<TableModel,Integer>)table.getRowSorter(); for( int
		 * i = 10; i < 23; i++ ) { rowsorter.setComparator(i,
		 * NullComparators.atEnd(wrapMe)); }
		 */

		/*table.getRowSorter().addRowSorterListener( new RowSorterListener() {
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
		});*/

		ftable = upper;
		ftable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		/*ftable = new JTable() {
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
		};*/

		ContextMenu fpopup = new ContextMenu();
		MenuItem amigo = new MenuItem("Amigo lookup");
		amigo.setOnAction( e -> {
			String go = ftable.getSelectionModel().getSelectedItem().getGo();
			try {
				// GeneSetHead.this.getAppletContext().
				Desktop.getDesktop().browse(new URI("http://amigo.geneontology.org/amigo/medial_search?q=" + go));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		fpopup.getItems().add( amigo );
		MenuItem keggl = new MenuItem("KEGG lookup");
		keggl.setOnAction( e -> {
			String kegg = ftable.getSelectionModel().getSelectedItem().getKegg();
			try {
				Desktop.getDesktop().browse(new URI("http://www.genome.jp/dbget-bin/www_bget?rn:" + kegg));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		fpopup.getItems().add( keggl );
		MenuItem ecl = new MenuItem("EC lookup");
		ecl.setOnAction( e -> {
			String ec = ftable.getSelectionModel().getSelectedItem().getEc();
			try {
				Desktop.getDesktop().browse(new URI("http://enzyme.expasy.org/EC/" + ec));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		fpopup.getItems().add( ecl );
		fpopup.getItems().add( new SeparatorMenuItem() );
		
		MenuItem excelreport = new MenuItem("Excel report");
		excelreport.setOnAction( e -> {
			/*Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("enzyme");
			int k = 0;
			for( Function f : ftable.getSelectionModel().getSelectedItems() ) {
				//String ec = (String)ftable.getValueAt(r, 1);
				//String go = (String)ftable.getValueAt(r, 0);
				
				//int i = ftable.getSelectionModel().convertRowIndexToModel(r);
				//Function f = geneset.funclist.get(i);
				for( GeneGroup gg : f.getGeneGroups() ) {
					for( String spec : gg.getSpecies() ) {
						Teginfo ti = gg.getGenes(spec);
						
						Row row = sheet.createRow(k++);
						org.apache.poi.ss.usermodel.Cell ecell = row.createCell(0);
						ecell.setCellValue( "EC:"+f.getEc() );
						org.apache.poi.ss.usermodel.Cell ncell = row.createCell(1);
						ncell.setCellValue( f.getName() );
						org.apache.poi.ss.usermodel.Cell spell = row.createCell(2);
						spell.setCellValue( spec );
						org.apache.poi.ss.usermodel.Cell seqcell = row.createCell(3);
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
					}*
				}
				sheet.createRow(k++);
			}
			
			try {
				Path tempfile = Files.createTempFile("enzyme",".xlsx");
				OutputStream os = Files.newOutputStream( tempfile );
				workbook.write( os );
				os.close();
				
				Desktop.getDesktop().open( tempfile.toFile() );
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}*/
		});
		fpopup.getItems().add( excelreport );
		ftable.setContextMenu( fpopup );

		ContextMenu popup = new ContextMenu();
		MenuItem splitaction = new MenuItem("Split");
		splitaction.setOnAction( e -> {
			Dialog<List<GeneGroup>> dialog = new Dialog<>();
			dialog.setResizable( true );

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 20, 10, 10));

			TextField len = new TextField();
			len.setPromptText("0.5");
			TextField id = new TextField();
			id.setPromptText("0.5");

			grid.add(new Label("%Length:"), 0, 0);
			grid.add(len, 1, 0);
			grid.add(new Label("%Identity:"), 0, 1);
			grid.add(id, 1, 1);

			final ListView<GeneGroup> list = new ListView<>();
			list.setPrefWidth(400);
			grid.add(list, 0, 2, 2, 1);

			final ObservableList<GeneGroup> gg = table.getSelectionModel().getSelectedItems();
			list.setItems( gg );

			Label groupsize = new Label(""/*+gg.genes.size()*/);
			grid.add(groupsize, 0, 3, 2, 1);

			id.textProperty().addListener((observable, oldValue, newValue) -> {
				if( !newValue.equals(oldValue) ) {
					boolean failed = false;
					double d = 0;
					double l = 0;
					try {
						d = Double.parseDouble(newValue);
						l = Double.parseDouble(newValue);
					} catch( Exception ex ) { failed = true; }

					if( !failed && l > 0 ) {
						List<GeneGroup> lgg = new ArrayList<>();
						for(GeneGroup ggg : gg) {
							Set<GeneGroup> sgg = updateSplit(ggg, d, l);
							lgg.addAll(sgg);
						}

						list.setItems(FXCollections.observableList(lgg));
						dialog.setResultConverter(param -> lgg);
					}
				}
			});

			/*len.textProperty().addListener((observable, oldValue, newValue) -> {
				if( !newValue.equals(oldValue) ) {
					boolean failed = false;
					double d = 0;
					double l = 0;
					try {
						d = Double.parseDouble(id.getText());
						l = Double.parseDouble(newValue);
					} catch( Exception ex ) { failed = true; }

					if( !failed && l > 0 ) {
						Set<GeneGroup> sgg = updateSplit(gg, d, l);
						List<GeneGroup> lgg = new ArrayList<>(sgg);
						list.setItems(FXCollections.observableList(lgg));
						dialog.setResultConverter(param -> sgg);
					}
				}
			});*/

			dialog.getDialogPane().setContent( grid );
			dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
			dialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
			Optional<List<GeneGroup>> ogg = dialog.showAndWait();

			ogg.ifPresent( c -> {
				geneset.allgenegroups.removeAll(gg);
				geneset.allgenegroups.addAll( c );

				saveGeneGroups(gg, c);
			});
		});
		popup.getItems().add( splitaction );
		MenuItem joinaction = new MenuItem("Join");
		joinaction.setOnAction(event -> {
			final ObservableList<GeneGroup> ogg = table.getSelectionModel().getSelectedItems();
			var firstGG = ogg.get(0);
			for (int i = 1; i < ogg.size(); i++) {
				var ngg = ogg.get(i);
				firstGG.addGenes(ngg.genes);
				geneset.allgenegroups.remove(ngg);
			}
			saveSimpleClusters();
		});
		popup.getItems().add( joinaction );

		MenuItem mergejoinaction = new MenuItem("Merge join");
		mergejoinaction.setOnAction(event -> {
			final ObservableList<GeneGroup> ogg = table.getSelectionModel().getSelectedItems();
			var firstGG = ogg.get(0);
			for (int i = 1; i < ogg.size(); i++) {
				var ngg = ogg.get(i);
				firstGG.mergeAnnotations(ngg.genes);
				geneset.allgenegroups.remove(ngg);
			}
			saveSimpleClusters();
		});
		popup.getItems().add( mergejoinaction );

		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem showkegg = new MenuItem("Show KEGG pathway");
		showkegg.setOnAction( e -> {
			GeneGroup gg = table.getSelectionModel().getSelectedItem();
			
			Map<String,String> env = new HashMap<>();
			env.put("create", "true");
			
			/*String uristr = "jar:" + geneset.zippath.toUri();
			URI zipuri = URI.create( uristr /*.replace("file://", "file:")* );
			final List<Path>	lbi = new ArrayList<>();*/
			boolean shown = false;
			try {
				geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );
				for( Path root : geneset.zipfilesystem.getRootDirectories() ) {
					for( Annotation a : gg.genes ) {
						Gene g = a.getGene();
						if( g.keggpathway != null ) {
							String sub = g.keggpathway.substring(0,3);
							Path subf = root.resolve(sub);
							if( Files.exists(subf) ) {
								String[] split = g.keggpathway.split(" ");
								for( String s : split ) {
									Path pimg = subf.resolve(s+".png");
									if( Files.exists(pimg) ) {
										showKeggPathway( sub, pimg );
										shown = true;
										break;
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

			if( !shown ) {
				for( Annotation a : gg.genes ) {
					Gene g = a.getGene();
					if (g != null && g.keggpathway != null) {
						String[] keggsplit = g.keggpathway.split(";");
						Arrays.stream(keggsplit).map( s -> s.split(":")[0] ).findFirst().ifPresent(c -> {
							try {
								Desktop.getDesktop().browse(URI.create("http://www.genome.jp/dbget-bin/www_bget?map" + c.substring(2)));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						});
					}
				}
			}
		});
		popup.getItems().add( showkegg );
		MenuItem plasmid = new MenuItem("Plasmid");
		plasmid.setOnAction( e -> {
			Gene g = gtable.getSelectionModel().getSelectedItem();
			Sequence contig = g.getTegeval().getContshort();
			String contigstr = contig.toString();
			contig.setPlasmid( !geneset.plasmids.contains( contigstr ) );
			if( contig.isPlasmid() ) geneset.plasmids.add( contigstr );
			else geneset.plasmids.remove( contigstr );
			
			try {
				Map<String,String> env = new HashMap<>();
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
		});
		popup.getItems().add( plasmid );
		MenuItem designate = new MenuItem("Designate");
		designate.setOnAction( e -> { SwingUtilities.invokeLater(() -> {
            JComboBox<String>	descombo = new JComboBox<>( geneset.deset.toArray(new String[0]) );
            descombo.setEditable( true );
            JOptionPane.showMessageDialog(null, descombo);
            String val = Objects.requireNonNull(descombo.getSelectedItem()).toString();
            geneset.deset.add( val );
			if(isGeneview()) {
				for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
					g.getTegeval().designation = val;
					if (g.id != null) {
						geneset.designations.put(g.id, val);
					} else {
						System.err.println(g.getRefid());
					}
					//ta.append( g.tegeval.id + "\n" );
				}
			} else {
				for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
					gg.genes.stream().filter(g -> g.getId() != null).forEach(g -> {
						geneset.designations.put(g.getId(), val);
						if(g.getGene()!=null && g.getGene().getTegeval()!=null) g.getGene().getTegeval().designation = val;
					});
				}
			}

            try {
                Map<String,String> env = new HashMap<>();
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
            });
		});
		popup.getItems().add( designate );
		MenuItem koname = new MenuItem("KO to name");
		koname.setOnAction( e -> {
			Set<String>	koids = new HashSet<>();
			for( Gene g : geneset.genelist ) {
				if( g.koid != null && g.koid.length() > 0 && !(geneset.ko2name != null && geneset.ko2name.containsKey( g.koid )) ) koids.add( g.koid );
			}
			
			try {
				Map<String,String>	ko2name = new HashMap<>();
				int cnt = 0;
				for( String koid : koids ) {
					URL url = new URL("http://www.kegg.jp/dbget-bin/www_bget?ko:"+koid);
					InputStream is0 = url.openStream();
					StringBuilder sb = new StringBuilder();
					BufferedReader br0 = new BufferedReader( new InputStreamReader(is0) );
					String line0 = br0.readLine();
					while( line0 != null ) {
						sb.append( line0 );
						line0 = br0.readLine();
					}
					br0.close();
					
					int i = sb.indexOf("<nobr>Name</nobr>");
					if( i != -1 ) {
						int k = sb.indexOf(":hidden\">");
						if( k != -1 ) {
							k = sb.indexOf(":hidden\">", k+9);
							if( k != -1 ) {
								String koname0 = sb.substring(k + 9, sb.indexOf("<br>", k));
								ko2name.put(koid, koname0);

								System.err.println(koid + "\t" + koname0);
							}
						}
					}
					
					System.err.println( ko2name.size() + " " + koids.size() );
					//if( cnt++ > 20 ) break;
				}
				
				FileWriter fw = new FileWriter("~ko2name.txt");
				for( String koid : ko2name.keySet() ) {
					fw.write( koid + "\t" + ko2name.get(koid) + "\n" );
				}
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		popup.getItems().add( koname );
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem genegainloss = new MenuItem("Gene gain/loss");
		genegainloss.setOnAction( e -> {
			Map<Node,List<GeneGroup>>	nodeGainMap = new HashMap<>();
			Map<Node,List<GeneGroup>>	nodeLossMap = new HashMap<>();
			
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
			
			org.simmi.treedraw.shared.TreeUtil tu = new org.simmi.treedraw.shared.TreeUtil();
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
			
			List<String>	rootgroup = new ArrayList<>();
			int[] rr = table.getSelectedRows();
			for( int r : rr ) {
				rootgroup.add( (String)table.getValueAt(r, 0) );
			}
			
			//String[] sobj = {"mt.ruber", "mt.silvanus", "o.profundus", "m.hydrothermalis"};
			Node newnode = tu.getParent( n, new HashSet<>( rootgroup ) );
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
		});
		popup.getItems().add( genegainloss );
		MenuItem concattree = new MenuItem("Concatenate tree");
		concattree.setOnAction( e -> {
			Serifier serifier = getConcatenatedSequences( false, true );
			
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
		});
		popup.getItems().add( concattree );
		MenuItem majocons = new MenuItem("Majority rule consensus");
		majocons.setOnAction( e -> {
			Serifier serifier = new Serifier();
			
			Set<GeneGroup>	genegroups = new HashSet<>();
			if( !isGeneview() ) {
				genegroups.addAll( table.getSelectionModel().getSelectedItems() );
			} else {
				for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
					genegroups.add( gg.getGeneGroup() );
				}
			}
			
			org.simmi.treedraw.shared.TreeUtil treeutil = new org.simmi.treedraw.shared.TreeUtil();
			Map<Set<String>,NodeSet> nmap = new HashMap<>();
			for( GeneGroup ggroup : genegroups ) {
				//List<Sequence>	seqlist = new ArrayList<Sequence>();
				
				for( Annotation tv : ggroup.getTegevals() ) {
					String spec = tv.getContshort().getSpec();
					Sequence seq = tv.getAlignedSequence();
					
					//Sequence seq = new Sequence( spec, null );
					//if( seqstr != null && seqstr.length() > 0 ) seq.append( seqstr );
					serifier.addSequence( seq );			
				}

				String tree = serifier.getFastTree( serifier.lseq, GeneSet.user, false );
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
			
			if( geneset.cs.getConnections().size() > 0 ) {
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
		});
		popup.getItems().add( majocons );
		MenuItem addsim = new MenuItem("Add similar");
		addsim.setOnAction( e -> {
			/*int r = table.getSelectedRow();
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
			}*/
		});
		popup.getItems().add( addsim );
		MenuItem selsim = new MenuItem("Select similar");
		selsim.setOnAction( e -> {
			/*int r = table.getSelectedRow();
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
			}*/
		});
		popup.getItems().add( selsim );
		MenuItem tabtxt = new MenuItem("Table text");
		tabtxt.setOnAction( e -> {
			/*JTextArea ta = new JTextArea();
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
			frame.setVisible(true);*/
		});
		popup.getItems().add( tabtxt );
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem ncbil = new MenuItem("NCBI lookup");
		ncbil.setOnAction( e -> {
			/*int r = table.getSelectedRow();
			if (r >= 0) {
				String ref = (String) table.getValueAt(r, 2);
				try {
					Desktop.getDesktop().browse(new URI("http://www.ncbi.nlm.nih.gov/gene?term=" + ref));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}*/
		});
		popup.getItems().add( ncbil );
		table.setContextMenu( popup );
		gtable.setContextMenu( popup );
		
		TableColumn<Function, String> gocol = new TableColumn("GO");
		gocol.setCellValueFactory( new PropertyValueFactory<>("go"));
		ftable.getColumns().add( gocol );
		TableColumn<Function, String> ecfcol = new TableColumn("EC");
		ecfcol.setCellValueFactory( new PropertyValueFactory<>("ec"));
		ftable.getColumns().add( ecfcol );
		TableColumn<Function, String> metacyccol = new TableColumn("MetaCyc");
		metacyccol.setCellValueFactory( new PropertyValueFactory<>("metacyc"));
		ftable.getColumns().add( metacyccol );
		TableColumn<Function, String> keggcol = new TableColumn("KEGG");
		keggcol.setCellValueFactory( new PropertyValueFactory<>("kegg"));
		ftable.getColumns().add( keggcol );
		TableColumn<Function, String> funcovcol = new TableColumn("Funciton coverage");
		funcovcol.setCellValueFactory( new PropertyValueFactory<>("speciesCount"));
		ftable.getColumns().add( funcovcol );
		TableColumn<Function, String> numprotcol = new TableColumn("Number of proteins");
		numprotcol.setCellValueFactory( new PropertyValueFactory<>("groupCount"));
		ftable.getColumns().add( numprotcol );
		
		TableColumn<Function, String> namecol = new TableColumn("Name");
		namecol.setCellValueFactory( new PropertyValueFactory<>("name"));
		ftable.getColumns().add( namecol );
		TableColumn<Function, String> namespacecol = new TableColumn("Namespace");
		namespacecol.setCellValueFactory( new PropertyValueFactory<>("namespace"));
		ftable.getColumns().add( namespacecol );
		TableColumn<Function, String> desccol = new TableColumn("Desc");
		desccol.setCellValueFactory( new PropertyValueFactory<>("desc"));
		ftable.getColumns().add( desccol );
		
		ftable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		
		//ftable.setAutoCreateRowSorter(true);
		/*ftablemodel = new TableModel() {
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
		fscrollpane.setViewportView(ftable);*/

		updateFilter(ftable, null);
		updateFilter(table, label);

		combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			filterset.clear();
            if (geneset.pathwaymap.containsKey(newValue)) {
                Set<String> enz = geneset.pathwaymap.get(newValue);
                for (Function f : geneset.funclist) {
                    if (f.getEc() != null && enz.contains(f.getEc())) {
                        filterset.add(f.index);
                    }
                }
            }
            updateFilter(ftable, null);
        });

		specombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String sel = newValue;
            genefilterset.clear();
            for (Gene g : geneset.genelist) {
                Annotation tv = g.getTegeval();
                if (sel.equals(tv.getSpecies())) {
                    //System.out.println(g.name + " " + sp + " " + sel + "  " + tv.eval);
                    genefilterset.add(g.index);
                }
            }
            updateFilter(table, label);
        });

		MenuItem findcon = new MenuItem("Find conserved terms");
		findcon.setOnAction( e -> {
			Set<Integer> res = new HashSet<>();
			for (Function f : geneset.funclist) {
				if (f.getGeneGroups() != null) {
					Set<String> check = new HashSet<>();
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
			filterset.addAll(res);
			updateFilter(ftable, null);
		});
		fpopup.getItems().add( findcon );
		fpopup.getItems().add( new SeparatorMenuItem() );
		
		MenuItem showgen = new MenuItem("Show genes");
		showgen.setOnAction( e -> {
			genefilterset.clear();
			Set<GeneGroup> sset = new HashSet<>();
			for (Function f : ftable.getSelectionModel().getSelectedItems()) {
				if( !isGeneview() ) {
					sset.addAll( f.getGeneGroups() );
					/*if( sset != null ) for (GeneGroup gg : sset) {
						//Gene g = genemap.get(s);
						genefilterset.add(gg.index);
					}*/
				} else {
					/*Set<Gene> sset = f.getGeneentries();
					for (Gene g : sset) {
						//Gene g = genemap.get(s);
						genefilterset.add(g.index);
					}*/
				}
			}
			
			//int[] rows = sset.stream().mapToInt( gg -> sortedData.indexOf(gg) ).toArray();
			//table.getSelectionModel().selectIndices(rows[0], rows);
			
			filteredData.setPredicate(sset::contains);
			if (label != null)
				label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
		});
		fpopup.getItems().add( showgen );

		table.getSelectionModel().selectedItemProperty().addListener( e -> {
			if(isTableSelectListenerEnabled) {
				label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
				// table.clearSelection();
				tableisselecting = true;
				if (!ftableisselecting && filterset.isEmpty()) {
					//ftable.removeRowSelectionInterval(0, ftable.getRowCount() - 1);
					if (!isGeneview()) {
						for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
							for (Function f : gg.getFunctions()) {
								try {
									ftable.getSelectionModel().select(f);
									//int rf = ftable.convertRowIndexToView(f.index);
									//if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					} else {
						for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
							if (g.funcentries != null) {
								for (Function f : g.funcentries) {
									//Function f = funcmap.get(go);
									try {
										ftable.getSelectionModel().select(f);
										//int rf = ftable.convertRowIndexToView(f.index);
										//if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
									} catch (Exception ex) {
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

		ftable.setOnKeyPressed( ke -> {
			if (ke.getCode() == KeyCode.ESCAPE) {
				ffilteredData.setPredicate(null);
			}
		});

		table.setOnKeyPressed( ke -> {
			if (ke.getCode() == KeyCode.ESCAPE) {
				GeneGroup selgg = table.getSelectionModel().getSelectedItem();

				List<GeneGroup> sel = new ArrayList<>( filteredData );
				filteredData.setPredicate(null);
				int[] rows = sel.stream().mapToInt( gg -> sortedData.indexOf(gg) ).toArray();
				if( rows.length > 0 ) table.getSelectionModel().selectIndices(rows[0], rows);
				if (label != null)
					label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
				
				table.scrollTo( selgg );
				//genefilterset.clear();
				//updateFilter(table, genefilter, label);
				//geneset.scrollToSelection( table );
			}
		});

		results.setOnKeyPressed( ke -> {
			if (ke.getCode() == KeyCode.ESCAPE) {
				GeneGroup selgg = table.getSelectionModel().getSelectedItem();
				List<GeneGroup> sel = new ArrayList<>( filteredData );
				filteredData.setPredicate(null);
				int[] rows = sel.stream().mapToInt( gg -> sortedData.indexOf(gg) ).toArray();
				if( rows.length > 0 ) table.getSelectionModel().selectIndices(rows[0], rows);
				label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
				table.scrollTo( selgg );
			}
		});

		gtable.setOnKeyPressed( ke -> {
			if (ke.getCode() == KeyCode.ESCAPE) {
				Gene selg = gtable.getSelectionModel().getSelectedItem();

				List<Gene> sel = new ArrayList<>( geneFilteredList );
				geneFilteredList.setPredicate(null);
				int[] rows = sel.stream().mapToInt( g -> geneSortedList.indexOf(g) ).toArray();
				if( rows.length > 0 ) gtable.getSelectionModel().selectIndices(rows[0], rows);
				label.setText(gtable.getItems().size() + "/" + gtable.getSelectionModel().getSelectedIndices().size());
				gtable.scrollTo( selg );
			}
		});

		table.setOnMousePressed( e -> {
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

				Set<Function> fset = new HashSet<>();
				filterset.clear();
				if( !isGeneview() ) {
					for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
						fset.addAll( gg.getFunctions() );
					}
				} else {
					for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
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
				ffilteredData.setPredicate(fset::contains);
			}
			tableisselecting = false;
		});

		results.setOnMousePressed( e -> {
			tableisselecting = true;
			if (!ftableisselecting && e.getClickCount() == 2) {
				Set<Function> fset = new HashSet<>();
				filterset.clear();
				if( !isGeneview() ) {
					for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
						fset.addAll( gg.getFunctions() );
					}
				} else {
					for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
						if (g.funcentries != null) {
							for( Function f : g.funcentries ) {
								filterset.add(f.index);
							}
						}
					}
				}
				ffilteredData.setPredicate(fset::contains);
			}
			tableisselecting = false;
		});

		ftable.setOnMousePressed( e -> {
			ftableisselecting = true;
			Set<GeneGroup> ggset = new HashSet<>();
			if (!tableisselecting && e.getClickCount() == 2) {
				genefilterset.clear();
				for (Function f : ftable.getSelectionModel().getSelectedItems()) {
					if (f.getGeneentries() != null) {
						if( !isGeneview() ) {
							ggset.addAll( f.getGeneGroups() );
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
				filteredData.setPredicate( p -> ggset.contains(p) );
			}
			ftableisselecting = false;
		});

		ftable.getSelectionModel().selectedItemProperty().addListener( e -> {
			ftableisselecting = true;
			if (!tableisselecting && genefilterset.isEmpty()) {
				table.getSelectionModel().clearSelection();
				//table.removeRowSelectionInterval(0, table.getRowCount() - 1);
				for (Function f : ftable.getSelectionModel().getSelectedItems()) {
					if( f.getGeneentries() != null ) {
						for( Gene g : f.getGeneentries() ) {
							table.getSelectionModel().select( g.getGeneGroup() );
							
							//Gene g = genemap.get(ref);
							/*int i = g.getGroupIndex();
							if( i >= 0 && i <= table.getItems().size() ) {
								int rf = table.convertRowIndexToView(i);
								table.addRowSelectionInterval(rf, rf);
							}*/
						}
					}
				}
			}
			ftableisselecting = false;
		});
		
		textfield.setOnKeyPressed( e -> {
			String text = textfield.getText().toLowerCase();
			if( e.getCode() == KeyCode.ENTER ) {
				if (isGeneview()) {
					searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable(gtable, text, searchi, e.isAltDown(), 8, 9, 10, 16) : searchTable(gtable, text, searchi, e.isAltDown(), 0);
				} else {
					searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable(table, text, searchi, e.isAltDown(), 8, 9, 10, 16) : searchTable(table, text, searchi, e.isAltDown(), 0);
				}
			}
		});

		textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            //String text = textfield.getText().toLowerCase();
            String lowerCaseFilter = newValue.toLowerCase();

			if( isGeneview() ) {
				Predicate<Gene> p = gene -> {
					GeneGroup genegroup = gene.getGeneGroup();
					if (newValue.isEmpty()) {
						return true;
					}

					if (searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol")) {
						if ((genegroup.getCogsymbol() != null && genegroup.getCogsymbol().toLowerCase().contains(lowerCaseFilter)) || (genegroup.getSymbol() != null && genegroup.getSymbol().toLowerCase().contains(lowerCaseFilter)) || (genegroup.getKoname() != null && genegroup.getKoname().toLowerCase().contains(lowerCaseFilter))) {
							return true; // Filter matches first name.
						}
					} else {
						if (genegroup.getName().toLowerCase().contains(lowerCaseFilter) || genegroup.genes.stream().anyMatch(gg -> gg.getName().toLowerCase().contains(lowerCaseFilter))) {
							return true; // Filter matches first name.
						}/* else if (genegroup.getLastName().toLowerCase().contains(lowerCaseFilter)) {
						return true; // Filter matches last name.
					}*/
					}
					return false; // Does not match.
				};
				if (filter.isSelected()) {
					geneFilteredList.setPredicate(p);
					label.setText(gtable.getItems().size() + "/" + gtable.getSelectionModel().getSelectedIndices().size());
					//if( searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ) updateFilter(0, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					//else updateFilter(0, text, table, genefilter, genefilterset, label, 0 );
				} else {
					Optional<Gene> og = geneFilteredList.stream().filter(p).findFirst();
					if (og.isPresent()) {
						Gene g = og.get();
						gtable.getSelectionModel().select(g);
						gtable.scrollTo(g);
					}
					//searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			} else {
				Predicate<GeneGroup> p = genegroup -> {
					if (newValue.isEmpty()) {
						return true;
					}

					if (searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol")) {
						if ((genegroup.getCogsymbol() != null && genegroup.getCogsymbol().toLowerCase().contains(lowerCaseFilter)) || (genegroup.getSymbol() != null && genegroup.getSymbol().toLowerCase().contains(lowerCaseFilter)) || (genegroup.getKoname() != null && genegroup.getKoname().toLowerCase().contains(lowerCaseFilter))) {
							return true; // Filter matches first name.
						}
					} else {
						if (genegroup.getName().toLowerCase().contains(lowerCaseFilter) || genegroup.genes.stream().anyMatch(gg -> gg.getName().toLowerCase().contains(lowerCaseFilter))) {
							return true; // Filter matches first name.
						}/* else if (genegroup.getLastName().toLowerCase().contains(lowerCaseFilter)) {
						return true; // Filter matches last name.
					}*/
					}
					return false; // Does not match.
				};
				if (filter.isSelected()) {
					filteredData.setPredicate(p);
					if (label != null)
						label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
					//if( searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ) updateFilter(0, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					//else updateFilter(0, text, table, genefilter, genefilterset, label, 0 );
				} else {
					Optional<GeneGroup> ogg = filteredData.stream().filter(p).findFirst();
					if (ogg.isPresent()) {
						GeneGroup gg = ogg.get();
						table.getSelectionModel().select(gg);
						table.scrollTo(gg);
					}
					//searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			}
        });
		/*textfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ) updateFilter(0, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					else updateFilter(0, text, table, genefilter, genefilterset, label, 0 );
				} else {
					searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void insertUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ) updateFilter(1, text, table, genefilter, genefilterset, label, 8, 9, 10, 16);
					else updateFilter(1, text, table, genefilter, genefilterset, label, 0);
				} else {
					searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
				}
			}

			public void removeUpdate(DocumentEvent e) {
				String text = textfield.getText().toLowerCase();
				if( filter.isSelected() ) {
					if( searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ) updateFilter(2, text, table, genefilter, genefilterset, label, 8, 9, 10, 16 );
					else updateFilter(2, text, table, genefilter, genefilterset, label, 0);
				} else {
					searchi = searchTable( table, text, 0, false, searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? 7 : 0 );
				}
			}
		});*/

		ftextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            //String text = textfield.getText().toLowerCase();
            if( filter.isSelected() ) {
                ffilteredData.setPredicate(function -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    // Compare first name and last name of every person with filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    boolean desc = function.getDesc() != null && function.getDesc().toLowerCase().contains(lowerCaseFilter);
                    boolean name = function.getName() != null && function.getName().toLowerCase().contains(lowerCaseFilter);
                    boolean go = function.getGo() != null && function.getGo().toLowerCase().contains(lowerCaseFilter);
                    boolean ec = function.getEc() != null && function.getEc().toLowerCase().contains(lowerCaseFilter);

                    if (desc || name || go || ec) {
                        return true; // Filter matches first name.
                    }/* else if (genegroup.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    }*/
                    return false; // Does not match.
                });
            } else {
                //searchi = searchcolcomb.getSelectionModel().getSelectedItem().equals("Symbol") ? searchTable( table, text, 0, false, 8, 9, 10, 16 ) : searchTable( table, text, 0, false, 0 );
            }
        });
		/*ftextfield.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateFilter(0, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2, ftextfield.getText(), ftable, rowfilter, filterset, null, 6);
			}
		});*/
		MenuItem kegggl = new MenuItem("KEGG gene lookup");
		kegggl.setOnAction( e -> {
				Gene g = gtable.getSelectionModel().getSelectedItem();
				try {
					Desktop.getDesktop().browse(new URI("http://www.genome.jp/dbget-bin/www_bget?" + g.keggid));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
		});
		popup.getItems().add( kegggl );
		MenuItem showgenes = new MenuItem("Show genes with same sharing");
		showgenes.setOnAction( e -> {
			genefilterset.clear();
			GeneGroup gg = table.getSelectionModel().getSelectedItem();
			for (GeneGroup g : geneset.allgenegroups) {
				if (gg.species != null && g.species != null) {
					Set<String> ggset = gg.species.keySet();
					Set<String> gset = g.species.keySet();

					if (gset.size() == ggset.size() && gset.containsAll(ggset)) {
						genefilterset.add(g.index);
					}
				}
			}
			updateFilter(table, label);
		});
		popup.getItems().add( showgenes );
		MenuItem showshared = new MenuItem("Show shared function");
		showshared.setOnAction( e -> {
				filterset.clear();
				Set<Function> startfunc = new HashSet<>();
				if( isGeneview() ) {
					for( Gene gg : gtable.getSelectionModel().getSelectedItems() ) {
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
					}
				} else {
					for( GeneGroup gg : table.getSelectionModel().getSelectedItems() ) {
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
				updateFilter(ftable, null);
		});
		popup.getItems().add( showshared );
		MenuItem showall = new MenuItem("Show all functions");
		showall.setOnAction( e -> {
				filterset.clear();
				Set<Function> startfunc = null;
				if( isGeneview() ) {
					for( Gene gg : gtable.getSelectionModel().getSelectedItems() ) {
						if (gg.funcentries != null) {
							for( Function f : gg.funcentries ) {
								filterset.add( f.index );
							}
						}
					}
				} else {
					for( GeneGroup gg : table.getSelectionModel().getSelectedItems() ) {
						Set<Function> fset = gg.getFunctions();
						for( Function f : fset ) {
							filterset.add( f.index );
						}
					}
				}
				updateFilter(ftable, null);
		});
		popup.getItems().add( showall );
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem showgenegroups = new MenuItem("Show gene groups in proximity");
		showgenegroups.setOnAction( e -> {
				proxi(filteredData, false);
				updateFilter(table, label);
		});
		popup.getItems().add( showgenegroups );
		MenuItem selgenegroups = new MenuItem("Select gene groups in proximity");
		selgenegroups.setOnAction( e -> {
				genefilterset.clear();
				proxi(filteredData, false);
				for( int i : genefilterset ) {
					table.getSelectionModel().select(i);
				}
				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				if (label != null) label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
				//updateFilter(table, genefilter, label);
		});
		popup.getItems().add( selgenegroups );
		MenuItem selgenes = new MenuItem("Select genes in proximity");
		selgenes.setOnAction( e -> {
				genefilterset.clear();
				proxi(filteredData, true);
				for( int i : genefilterset ) {
					table.getSelectionModel().select(i);
				}
				//table.tableChanged( new TableModelEvent( table.getModel() ) );
				if (label != null) label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
				//updateFilter(table, genefilter, label);
		});
		popup.getItems().add( selgenes );
		MenuItem addgene = new MenuItem("Add gene groups in proximity");
		addgene.setOnAction( e -> {
				proxi(filteredData, false);
				updateFilter(table, label);
		});
		popup.getItems().add( addgene );
		MenuItem remgene = new MenuItem("Remove gene groups in proximity");
		remgene.setOnAction( e -> {
				ObservableList<Integer> rr = table.getSelectionModel().getSelectedIndices();
				if (genefilterset.isEmpty()) {
					Set<Integer> ii = new HashSet<>();
					for (int r : rr)
						ii.add(r);
					for (int i = 0; i < geneset.genelist.size(); i++) {
						if (!ii.contains(i))
							genefilterset.add(i);
					}
				}
				proxi(filteredData, false);
				updateFilter(table, label);
		});
		popup.getItems().add( remgene );
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem showrel = new MenuItem("Show related genes");
		showrel.setOnAction( e -> {
				genefilterset.clear();
				relati(gtable, geneset.genelist, genefilterset, geneset.uclusterlist, false);
				updateFilter(gtable, label);
		});
		popup.getItems().add( showrel );
		MenuItem addrel = new MenuItem("Add related genes");
		addrel.setOnAction( e -> {
				relati(gtable, geneset.genelist, genefilterset, geneset.uclusterlist, false);
				updateFilter(gtable, label);
		});
		popup.getItems().add( addrel );
		MenuItem remrel = new MenuItem("Remove related genes");
		remrel.setOnAction( e -> {
			ObservableList<Integer> rr = gtable.getSelectionModel().getSelectedIndices();
			if (genefilterset.isEmpty()) {
				Set<Integer> ii = new HashSet<>();
				for (int r : rr)
					ii.add(r);
				for (int i = 0; i < geneset.genelist.size(); i++) {
					if (!ii.contains(i))
						genefilterset.add(i);
				}
			}
			relati(gtable, geneset.genelist, genefilterset, geneset.uclusterlist, true);
			updateFilter(table, label);
		});
		popup.getItems().add( remrel );
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem showcloserel = new MenuItem("Show closely related genes");
		showcloserel.setOnAction( e -> {
			genefilterset.clear();
			Set<String> ct = new HashSet<>();
			for (Gene gg : gtable.getSelectionModel().getSelectedItems()) {
				// genefilterset.add( gg.index );
				Annotation tv = gg.getTegeval();
				for (Set<String> uset : geneset.iclusterlist) {
					if (uset.contains(tv.getName())) {
						ct.addAll(uset);
						break;
					}
				}
			}

			for (Gene g : geneset.genelist) {
				Annotation tv = g.getTegeval();
				if (ct.contains(tv.getName())) {
					genefilterset.add(g.index);
					break;
				}
			}

			updateFilter(table, label);
		});
		popup.getItems().add( showcloserel );
		MenuItem showdist = new MenuItem("Show distance matrix");
		showdist.setOnAction( e -> {
			JTextArea textarea = new JTextArea();
			
			try {
				Action action = new CopyAction("Copy", null, "Copy data", KeyEvent.VK_CONTROL + KeyEvent.VK_C);
				textarea.getActionMap().put("copy", action);
				grabFocus = true;
			} catch (Exception ee) {
				ee.printStackTrace();
				System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
			}
			textarea.setDragEnabled(true);

			JScrollPane scrollpane = new JScrollPane(textarea);
			Gene gg = gtable.getSelectionModel().getSelectedItem();
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
				
				org.simmi.treedraw.shared.TreeUtil treeutil = new org.simmi.treedraw.shared.TreeUtil();
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
	}
	
	private static void relati(TableView<Gene> table, List<Gene> genelist, Set<Integer> genefilterset, List<Set<String>> uclusterlist, boolean remove) {
		Set<String> ct = new HashSet<>();
 		for (Gene gg : table.getSelectionModel().getSelectedItems()) {
			GeneGroup ggroup = gg.getGeneGroup();
			for( Annotation a : ggroup.genes ) {
				Gene g = a.getGene();
				ct.add( g.getRefid() );
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
			if( ct.contains(g.getRefid()) ) {
				if (remove)
					genefilterset.remove( genelist.indexOf( g ) );
				else
					genefilterset.add( genelist.indexOf( g ) );
				//break;
			}
		}
		System.err.println( genefilterset.size() + "  " + ct.size() );
	}
	
	private void newSoft(Button jb, Container comp, TableView<Gene> genetable, TableView<Function> upper, TableView<GeneGroup> lower, ToolBar toolbar, ToolBar btoolbar, JPanel applet, ComboBox selcomblocal) throws IOException {
		/*InputStream nis2 = GeneSet.class.getResourceAsStream("/exp_short.blastout");
		BufferedReader br2 = new BufferedReader( new InputStreamReader(nis2) );
		String line2 = br2.readLine();
		br2.close();*/
		// aas.clear();

		// return new JComponent() {};
		showGeneTable(null, jb, genetable, upper, lower, toolbar, btoolbar, comp, applet, selcomblocal);// clustInfoMap// );
	}
	
	public void init() {
		init(primaryStage, null, null, null, null, null, null, null, null, null, null, null, null);
	}
	
	public void exportGenomes( Map<String,List<Sequence>> speccontigMap, boolean fasta ) {
		SwingUtilities.invokeLater(() -> {
			JCheckBox joincontigs = new JCheckBox("Join contigs");
			JCheckBox translations = new JCheckBox("Include translations");
			JCheckBox editdefaults = new JCheckBox("Edit defaults");
			JComponent[] comps = new JComponent[]{joincontigs, translations, editdefaults};
			List scl = getSelspecContigs(Arrays.asList(comps), speccontigMap);

			var ob = table.getSelectionModel().getSelectedItems();
			var ggset = ob.stream().collect(Collectors.toUnmodifiableSet());

			if (scl.get(0) instanceof String) {
				List<String> lspec = (List<String>) scl;

				Platform.runLater(() -> {
                    DirectoryChooser dirchooser = new DirectoryChooser();
                    File f = dirchooser.showDialog( null );
                    if (f != null && f.exists()) {
                        for (String spec : lspec) {
                            Map<String, List<Annotation>> mapan = new HashMap<>();
                            List<Sequence> contigs = geneset.speccontigMap.get(spec);
                            Serifier serifier = new Serifier();
                            for (Sequence c : contigs) {
                                serifier.addSequence(c);
                                serifier.mseq.put(c.getName(), c);

                                var lann = new ArrayList<Annotation>();
                                if (c.getAnnotations() != null) for (Annotation ann : c.getAnnotations()) {
                                    Tegeval tv = (Tegeval) ann;
                                    GeneGroup gg = tv.getGene().getGeneGroup();

									if(ggset.size()==0 || ggset.contains(gg)) {
										Annotation anno = new Annotation(c, tv.start, tv.stop, tv.ori, gg != null ? gg.getName() : tv.getGene().getName());
										anno.type = "CDS";
										anno.setId(tv.getGene().id);

										String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
										if (cazy != null) anno.addDbRef("CAZY:" + cazy);
										Cog cog = gg != null ? gg.getCog(geneset.cogmap) : null;
										if (cog != null) anno.addDbRef("COG:" + cog.id);
										String ec = gg != null ? gg.getEc() : null;
										if (ec != null) anno.addDbRef("EC:" + ec);

										lann.add(anno);
									}
                                }
                                mapan.put(c.getName(), lann);
                            }
                            Sequences s = new Sequences(null, spec, "nucl", null, contigs.size());
                            try {
                                if (fasta) {
									serifier.writeFasta(Files.newBufferedWriter(f.toPath()), true);
								} else {
									serifier.writeGenebank(f.toPath().resolve(spec + ".gbk"), !joincontigs.isSelected(), translations.isSelected(), s, mapan, false);
								}
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
			} else {
				Map<String, List<Annotation>> mapan = new HashMap<>();
				List<Sequence> contigs = (List<Sequence>) scl;
				Serifier serifier = new Serifier();
				for (Sequence c : contigs) {
					serifier.addSequence(c);
					serifier.mseq.put(c.getName(), c);

					List<Annotation> lann = new ArrayList<>();
					if (c.getAnnotations() != null) {
						/*if(c.isReverse()) {
							var loann = c.getAnnotations();
							for (int k = loann.size()-1; k >= 0; k--) {
								var ann = loann.get(k);
								Tegeval tv = (Tegeval) ann;
								GeneGroup gg = tv.getGene().getGeneGroup();

								if (ggset.size() == 0 || ggset.contains(gg)) {
									Annotation anno = new Annotation(c, c.length()-tv.stop, c.length()-tv.start, tv.ori == -1 ? 0 : -1, gg != null ? gg.getName() : tv.getGene().getName());
									anno.type = "CDS";
									anno.setId(tv.getGene().id);

									String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
									if (cazy != null) anno.addDbRef("CAZY:" + cazy);
									lann.add(anno);
								}
							}
						} else {*/
							for (Annotation ann : c.getAnnotations()) {
								Tegeval tv = (Tegeval) ann;
								GeneGroup gg = tv.getGene().getGeneGroup();

								if (ggset.size() == 0 || ggset.contains(gg)) {
									Annotation anno = new Annotation(c, tv.start, tv.stop, tv.ori, gg != null ? gg.getName() : tv.getGene().getName());
									anno.type = "CDS";
									anno.setId(tv.getGene().id);

									String cazy = gg != null ? gg.getCommonCazy(geneset.cazymap) : null;
									if (cazy != null) anno.addDbRef("CAZY:" + cazy);
									lann.add(anno);
								}
							}
						//}
					}
					mapan.put(c.getName(), lann);
				}
				Sequences s = new Sequences(null, contigs.get(0).getSpec(), "nucl", null, contigs.size());

				Platform.runLater(() -> {
                    FileChooser filechooser = new FileChooser();
                    File f = filechooser.showSaveDialog(null);
                    if ( f != null ) {
                        try {
							if (fasta) {
								var writer = Files.newBufferedWriter(f.toPath());
								serifier.writeFasta(writer, true);
								writer.close();
							} else {
								serifier.writeGenebank(f.toPath(), !joincontigs.isSelected(), translations.isSelected(), s, mapan, editdefaults.isSelected());
							}
                            Desktop.getDesktop().open(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
				//serifier.addSequences(seqs);
			}
		});
	}
	
	public void exportProteinSequences( List<Gene> genelist ) throws IOException {
		FileChooser filechooser = new FileChooser();
		File f = filechooser.showSaveDialog(null);
		if (f != null /*&& f.exists()*/) {
			Path dbPath = f.toPath();
			BufferedWriter bw = Files.newBufferedWriter(dbPath);
			var selist = table.getSelectionModel().getSelectedItems();
			if (selist.size() > 0) {
				genelist = selist.stream().flatMap(gg -> gg.genes.stream()).map(Annotation::getGene).toList();
			}
			for (Gene g : genelist) {
				if (g.getTag() == null || g.getTag().equalsIgnoreCase("gene")) {
					g.getFasta(bw, true);
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
		}
	}

	public void exportRepGeneSeq( List<GeneGroup> allgenegroups ) throws IOException {
		FileChooser fc = new FileChooser();
		fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Fasta file","*.fasta"));
		File res = fc.showSaveDialog(null);
		if (res!=null) {
			var path = res.toPath();
			BufferedWriter writer = Files.newBufferedWriter(path);;
			try {
				for (var geneGroup : allgenegroups) {
					var sortTe = new ArrayList<>(geneGroup.getTegevals());
					sortTe.sort(Comparator.comparingInt(Annotation::getLength));
					var te = sortTe.get(sortTe.size() / 2);
					var gene = te.getGene();
					if (gene != null) {
						var pseq = te.getProteinSequence();
						if (pseq!=null) pseq.writeIdSequence(writer);
					}
				}
			} finally {
				writer.close();
			}
		}
	}
	
	public void exportGeneClusters( List<GeneGroup> allgenegroups ) throws IOException {
		DirectoryChooser dirchooser = new DirectoryChooser();
		File dir = dirchooser.showDialog( null );
		if( dir != null && dir.exists() ) {
			Path dbPath = dir.toPath();
			for( GeneGroup gg : allgenegroups ) {
				if( gg != null /*&& gg.getCommonTag() == null*/ ) {
					Path file = dbPath.resolve( gg.getCommonId()+".faa" );
					try(BufferedWriter bw = Files.newBufferedWriter( file )) {
						gg.getAlignedFasta(bw, true);
					}
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
		}
	}
	
	public void showKeggPathway( String sub, Path p ) throws IOException {
		SwingUtilities.invokeLater(() -> {
			try {
				final BufferedImage selimg = ImageIO.read(Files.newInputStream(p));
				if (selimg != null) {
					JFrame frame = new JFrame(sub);
					frame.setSize(800, 600);
					final JComponent c = new JComponent() {
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(selimg, 0, 0, this);
						}
					};
					Dimension dim = new Dimension(selimg.getWidth(), selimg.getHeight());
					c.setSize(dim);
					c.setPreferredSize(dim);
					JScrollPane sc2 = new JScrollPane(c);
					frame.add(sc2);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				}
			} catch( Exception e ) {
				e.printStackTrace();
			}
		});
	}
	
	public List getSelspecContigs( List<JComponent> complist, final Map<String,List<Sequence>> speccontigMap, final String ... selspec ) {
		List<Sequence>				contigs = null;
		final List<String>			specs = new ArrayList<>( speccontigMap.keySet() );
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
		
		stable.getSelectionModel().addListSelectionListener(e -> ctable.tableChanged( new TableModelEvent( ctablemodel ) ));
		JOptionPane.showMessageDialog(null, c);
		
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
