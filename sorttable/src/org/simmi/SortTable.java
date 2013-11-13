package org.simmi;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.simmi.RecipePanel.Recipe;
import org.simmi.RecipePanel.RecipeIngredient;
import org.simmi.shared.TreeUtil;

public class SortTable extends JApplet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	JScrollPane scrollPane;
	JScrollPane leftScrollPane;
	JScrollPane topScrollPane;
	JScrollPane topLeftScrollPane;
	LinkedSplitPane leftSplitPane;
	LinkedSplitPane rightSplitPane;
	JSplitPane splitPane;
	JCompatTable table;
	JCompatTable leftTable;
	JCompatTable topTable;
	JCompatTable topLeftTable;
	JTextField field;
	JTabbedPane tabbedPane;
	RecipePanel recipe;
	HabitsPanel	eat;
	FriendsPanel friendsPanel;
	JComboBox combo;

	ImagePanel imgPanel;
	JComponent graph;
	Image img;

	TableModel model;
	TableModel topModel;

	DetailPanel detail;

	MySorter tableSorter;
	MySorter leftTableSorter;
	MySorter currentSorter;

	static List<Object[]> 				stuff = null;
	// List<Object[]> header;
	static Map<String, Integer> 		ngroupMap;
	static List<String> 				ngroupList;
	static List<String> 				ngroupGroups;

	private static Map<String, Integer> foodInd = new HashMap<String, Integer>();
	private static Map<String, Integer> foodNameInd = new HashMap<String, Integer>();

	String lang;
	boolean hringur = false;

	MyFilter filter;

	/*
	 * public static class ObjectArray extends Object[] {
	 * 
	 * };
	 */

	// static String lof =
	// "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel";

	static {
		CompatUtilities.updateLof();
		try {
			//System.getSecurityManager().checkPropertiesAccess();
			System.setProperty("file.encoding", "UTF8");
		} catch( SecurityException e ) {
			
		}
	}

	public void selectTabIndex(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public void selectTabName(String name) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (tabbedPane.getTitleAt(i).equalsIgnoreCase(name)) {
				tabbedPane.setSelectedIndex(i);
				break;
			}
		}
	}

	public SortTable() {
		super();
	}

	public class Nut {
		public Nut(String name, String unit) {
			this.name = name;
			this.unit = unit;
		}

		String name;
		String unit;
	}
	
	public String[] intelliSplit( String line, String sep ) {
		int com = 0;
		List<String> splitlist = new ArrayList<String>();
		while( com < line.length() ) {
			if( line.charAt(com) == '"' ) {
				int newgus = line.indexOf('"', com+1);
				splitlist.add( line.substring(com+1, newgus) );
				
				int ind = line.indexOf(sep, newgus+1);
				if( ind == -1 ) ind = line.length();
				com = ind+1;
			} else {
				int newcom = line.indexOf(sep, com);
				if( newcom == -1 ) newcom = line.length();
				splitlist.add( line.substring(com, newcom) );
				com = newcom+1;
			}
		}
		String[] split = splitlist.toArray( new String[0] );
		return split;
	}
	
	public void inThread( Reader rd, final List<Object[]>	result, final Set<Integer> is, String sep, boolean skipfirst ) throws IOException {
		int start = -1;

		/*
		 * File file = new File(
		 * System.getProperty("user.home"), ".isgem" ); if(
		 * !file.exists() ) { file.mkdirs(); } file = new File(
		 * file, "result.zip" ); if( !file.exists() ||
		 * file.length() != 3200582 ) { if( !file.exists() )
		 * System.err.println( "hey file" );
		 * 
		 * InputStream inputStream =
		 * this.getClass().getResourceAsStream( "/result.txt" );
		 * FileOutputStream fos = new FileOutputStream( file );
		 * 
		 * byte[] bb = new byte[3200582]; int r =
		 * inputStream.read(bb); while( r > 0 ) { fos.write(bb,
		 * 0, r); r = inputStream.read(bb); } fos.close(); }
		 * 
		 * //if( f.exists() ) { //inputStream =
		 * this.getClass().getResourceAsStream( "result.zip" );
		 * ZipFile zipfile = new ZipFile( file ); ZipEntry
		 * zipentry = zipfile.getEntry("result.txt");
		 */
		// InputStream inputStream = zipfile.getInputStream(
		// zipentry );

		if (rd != null) {
			BufferedReader br = new BufferedReader( rd );
			//int	linen = 0;
			String line = br.readLine();
			if( skipfirst ) line = br.readLine();
			while (line != null) {
				String[] split = intelliSplit( line, sep );
				//String[] split = line.split( sep );

				if( split.length > 2 ) {
					int ival = -1;
					try {
						ival = Integer.parseInt(split[1]);
					} catch( Exception e ) {
						
					}
					if( ival == -1 ) {
						System.err.println( split[1] );
					}
					
					if( is.contains(ival) ) {
						if (foodInd.containsKey(split[0])) {
							start = foodInd.get(split[0]);
						
							Object[] objs = result.get(start + 2);
							if (split[2].length() > 0) {
								String replc = split[2].replace(',', '.');
								replc = replc.replace("<", "");
								float f = -1.0f;
								try {
									f = Float.parseFloat(replc);
								} catch (Exception e) {
								}
								Integer ngroupOffset = ngroupMap.get(split[1]);
								
								if( ngroupOffset != null ) {
									if (f != -1.0f)
										objs[2 + ngroupOffset] = f;
									else
										objs[2 + ngroupOffset] = null;
								} else {
									System.err.println();
								}
							}
						}
					}
				}
				line = br.readLine();
				//linen++;
			}
		}
		
		int poffset = ngroupMap.get("0001");
		int foffset = ngroupMap.get("0002");
		int koffset = ngroupMap.get("0010");
		int aoffset = ngroupMap.get("0014");
		int toffset = ngroupMap.get("0013");
		for( int i = 2; i < result.size(); i++ ) {
			Object[] objs = result.get(i);
			float jres = 0.0f;
			float calres = 0.0f;
			if( objs[2 + aoffset] != null ) {
				Float f = (Float)objs[2+aoffset];
				jres += 29.0f*f;
				calres += 7.0f*f;
			}
			if( objs[2 + poffset] != null ) {
				Float f = (Float)objs[2+poffset];
				jres += 17.0f*f;
				calres += 4.0f*f;
			}
			if( objs[2 + koffset] != null ) {
				Float f = (Float)objs[2+koffset];
				jres += 17.0f*f;
				calres += 4.0f*f;
			}
			if( objs[2 + foffset] != null ) {
				Float f = (Float)objs[2+foffset];
				jres += 37.0f*f;
				calres += 9.0f*f;
			}
			if( objs[2 + toffset] != null ) {
				Float f = (Float)objs[2+toffset];
				jres += 8.0*f;
				calres += 2.0f*f;
			}
			
			//System.err.println( objs[0] + "  " + jres + "  " + calres );
			objs[2] = jres;
			objs[3] = calres;
		}

		System.err.println("issgem loaded");
		/*
		 * while( tableSorter == null ) Thread.sleep(100);
		 * table.setModel( model ); table.setRowSorter(
		 * tableSorter );
		 */
		// table.tableChanged( new TableModelEvent( model ) );
	}
	
	String input = null;
	public void setInput( String input ) {
		this.input = input;
	}
	
	public Reader run(String query, boolean isUsingEncId) throws IOException, InterruptedException {
		boolean fail = false;
		String lowercaseQuery = query.toLowerCase();
		String encodedQuery = URLEncoder.encode(query, "UTF-8");
		encodedQuery = encodedQuery.replace("+", "%20");
		String urlstr = SERVICE_URL + "?sql=" + encodedQuery + "&encid=" + isUsingEncId;
		
		System.err.println( "uu" );
		try {
		   JSUtil.console( SortTable.this, "about to loaddata" + query );
		   JSUtil.call( SortTable.this, "loadData", new Object[] { urlstr } );
		   JSUtil.console( SortTable.this, "done loaddata");
		   
		   while( input == null ) Thread.sleep(1000);
		   
		   JSUtil.console( SortTable.this, "after wait");
		   
		   StringReader res = new StringReader( input );
		   input = null;
		   return res;
	   } catch(Exception e) {
		   fail = true;
	   }
	    
		System.err.println( "fail "+fail );
	    if( fail ) {	
		   /*GDataRequest request = null;
		   // If the query is a select, describe, or show query, run a GET request.
		   if (lowercaseQuery.startsWith("select") || lowercaseQuery.startsWith("describe") || lowercaseQuery.startsWith("show")) {
			   System.err.println("about to get "+urlstr);
			   
		     URL url = new URL(urlstr);
		     System.err.println("what");
		     try {
		    	 request = service.getRequestFactory().getRequest(RequestType.QUERY, url, ContentType.TEXT_PLAIN);
		     } catch( Exception e ) {
		    	 e.printStackTrace();
		     }
		   } else {
			   System.err.println("about to post");
			   
		     // Otherwise, run a POST request.
		     URL url = new URL(SERVICE_URL + "?encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.INSERT, url, new ContentType("application/x-www-form-urlencoded"));
		     OutputStreamWriter writer = new OutputStreamWriter(request.getRequestStream());
		     writer.append("sql=" + encodedQuery);
		     writer.flush();
		   }
		   
		   if( request != null ) {
			   	System.err.println("about to exec");
		   		request.execute();
	
		   		System.err.println("returning");
		   		return new InputStreamReader( request.getResponseStream() );
		   }*/
	   }
	    System.err.println("ermermerm");
	   return null;
	}
	
	/*private StringBuilder getResultsText(GDataRequest request) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(request.getResponseStream());
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb;
	}*/
	
	public Map<String, String> isGroup( Reader reader, String sep, boolean skipfirst ) throws IOException {
		Map<String, String> fgroupMap = new HashMap<String, String>();
		
		BufferedReader br = new BufferedReader( reader );
		String line = br.readLine();
		
		if( skipfirst ) line = br.readLine();
		
		while (line != null) {
			while( countOccur( line, '"' ) % 2 != 0 ) line += br.readLine();
			
			String[] split = intelliSplit(line, sep);
			//String[] split = line.split( sep );
			if (split.length > 1 && split[0].contains(".")) {
				fgroupMap.put(split[0], split[1]);
			}
			line = br.readLine();
		}
		
		return fgroupMap;
	}
	
	public Set<Integer> isComponent( Reader rd, String sep, boolean skipfirst ) throws IOException {
		System.err.println("eeeeerm ----------------------------------------------------------------------------------------");
		
		Integer[] ii = { 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20, 21, 23, 24, 28, 29, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 44, 137, 138 };
		final Set<Integer> is = new HashSet<Integer>(Arrays.asList(ii));
		List<String[]> idList = new ArrayList<String[]>();
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
	
		if( skipfirst ) line = br.readLine();
		
		int i = 0;
		while (line != null) {
			while( countOccur( line, '"' ) % 2 != 0 ) line += br.readLine();
			
			String[] split = intelliSplit(line, sep);
			//String[] split = line.split( sep );
			if (split.length > 3 && is.contains(Integer.parseInt(split[1]))) {
				String sName = null;
				if (split[3] != null && split[3].length() > 0) {
					sName = split[3];
				}
				String nName = split[2];
	
				String[] strs = new String[] { split[1], nName, split[7], sName, split[5] };
				idList.add(strs);
				// ngroupMap.put( split[2], i++ );
	
				/*
				 * ngroupList.add( nName ); // + " ("+split[1].substring(1,
				 * split[1].length()-1)+")" ); ngroupGroups.add( split[8] );
				 * //List<Object> lobj = nutList.get(i).get(i)
				 * nutList[0].add( sName ); String mName = split[6];
				 * nutList[1].add( mName );
				 */
			}
			line = br.readLine();
		}
	
		Collections.sort(idList, new Comparator<String[]>() {
			public int compare(String[] s1, String[] s2) {
				return s1[0].compareTo(s2[0]);
			}
		});
		
		ngroupMap.put( "0200", i++ );
		ngroupList.add( "Orka" );
		ngroupGroups.add( "1" );
		nutList[0].add( "Energy" );
		nutList[1].add( "kJ" );
		ngroupMap.put( "0201", i++ );
		ngroupList.add( "Orka" );
		ngroupGroups.add( "1" );
		nutList[0].add( "Energy" );
		nutList[1].add( "kcal" );
		for (String[] vals : idList) {
			ngroupMap.put(vals[0], i++);
			String cname = vals[1];
			String group = vals[2];
			if( group.equals("4") || group.equals("5") ) {
				cname = cname.split(",")[0];
			} else {
				if( group.equals("6") || group.equals("1") ) {
					if( cname.endsWith("n-3") ) {
						cname = "Fjölóm. fitus. ómega-3";
					} else if( cname.endsWith("n-6") ) {
						cname = "Fjölóm. fitus. ómega-6";
					} else {
						cname = cname.replace("cis-", "");
					}
				}
				
				cname = cname.replace(", alls", "");
				cname = cname.replace("Trefjaefni", "Trefjar");
			}
			ngroupList.add( cname );
			ngroupGroups.add( group );
			nutList[0].add(vals[3].trim());
			nutList[1].add(vals[4].trim());
		}
		
		return is;
	}
	
	public int countOccur( String str, char c ) {
		int count = 0;
		
		for( int i = 0; i < str.length(); i++ ) {
			if( str.charAt( i ) == c ) count++;
		}
		
		return count;
	}
	
	public void isFood( Reader rd, Map<String,String> fgroupMap, List<Object[]> result, String sep, boolean skipfirst ) throws IOException {
		int i = 0;
		int k = 0;
		
		BufferedReader br = new BufferedReader( rd );
		String line = br.readLine();
		
		if( skipfirst ) line = br.readLine();
		
		while (line != null) {
			while( countOccur( line, '"' ) % 2 != 0 ) {
				String addline = br.readLine();
				line += addline;
			}
			
			String[] split = intelliSplit(line, sep);
			if( split.length > 7 ) {
				foodInd.put(split[0], k);
				// foodNameInd.put(split[2], k);
				k++;
	
				String val = split[5];
				split[5] = fgroupMap.get(val);
				Object[] array = new Object[2 + ngroupList.size()];
				array[0] = split[1];
				array[1] = split[5];
				for (i = 2; i < array.length; i++) {
					array[i] = null;
				}
				result.add(array);
				line = br.readLine();
			} else {
				System.err.println( line );
				break;
			}
		}
	}

	List<Object>[] nutList = null;
	public List<Object[]> parseData( String loc ) throws IOException, InterruptedException {
		Map<String, String> fgroupMap = null;

		if( loc.startsWith("IS_fusion") ) {
			/*if( service == null ) {
				service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			}
			
			if( service != null ) {
				Reader rd = run("select * from "+grouptableid, true);
				fgroupMap = isGroup( rd, ",", true );
			}*/
		} else if (loc.startsWith("IS")) {
			InputStream inputStream = this.getClass().getResourceAsStream("/thsGroups.txt");
			fgroupMap = isGroup( new InputStreamReader( inputStream, "UTF-8" ), "\t", false );
		} else {
			fgroupMap = new HashMap<String, String>();
			InputStream inputStream = this.getClass().getResourceAsStream("FD_GROUP.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\\^");
				if (split.length == 2) {
					fgroupMap.put(split[0], split[1].substring(1, split[1].length() - 1));
				}
				line = br.readLine();
			}
		}

		nutList = new List[2];
		// List<Object>[] nutList = nutList;
		for (int i = 0; i < nutList.length; i++) {
			List<Object> list = new ArrayList<Object>();
			nutList[i] = list;
			list.add(null);
			list.add(null);
		}
		ngroupMap = new HashMap<String, Integer>();
		ngroupList = new ArrayList<String>();
		ngroupGroups = new ArrayList<String>();

		Set<Integer> is = null;
		if( loc.startsWith("IS_fusion") ) {
			/*if( service == null ) {
				service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			}
			
			if( service != null ) {
				Reader rd = run("select * from "+componenttableid, true);
				is = isComponent( rd, ",", true );
			}*/
		} else if (loc.startsWith("IS")) {
			InputStream inputStream = this.getClass().getResourceAsStream("/Component.txt");
			is = isComponent( new InputStreamReader(inputStream, "UTF-8"), "\t", false );
		} else {
			InputStream inputStream = this.getClass().getResourceAsStream("NUTR_DEF.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			int i = 0;
			while (line != null) {
				String[] split = line.split("\\^");
				if (split.length > 3) {
					String sName = null;
					if (split[2] != null && split[2].length() > 0) {
						sName = split[2].substring(1, split[2].length() - 1);
					}
					String nName = split[3].substring(1, split[3].length() - 1);
					ngroupMap.put(split[0], i++);
					ngroupList.add(nName);// + " ("+split[1].substring(1,
					// split[1].length()-1)+")" );
					ngroupGroups.add(split[5]);
					// List<Object> lobj = nutList.get(i).get(i)
					nutList[0].add( sName == null ? null : sName.trim() );
					String mName = split[1].substring(1, split[1].length() - 1);
					nutList[1].add( mName == null ? null : mName.trim() );
				}
				line = br.readLine();
			}
		}

		final List<Object[]> result = new ArrayList<Object[]>();
		for (List<Object> l : nutList) {
			result.add(l.toArray(new Object[0]));
		}
		
		if( loc.startsWith("IS_fusion") ) {
			/*if( service == null ) {
				service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			}
			
			if( service != null ) {
				Reader rd = run("select * from "+foodtableid+" where WebPublishReady = 'J'", true);
				isFood( rd, fgroupMap, result, ",", true );
			}*/
		} else if (loc.startsWith("IS")) {
			InputStream inputStream = this.getClass().getResourceAsStream("/Food.txt");
			isFood( new InputStreamReader( inputStream, "UTF-8" ), fgroupMap, result, "\t", false );
		} else {
			InputStream inputStream = this.getClass().getResourceAsStream("FOOD_DES.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\\^");
				String val = split[1];
				split[1] = fgroupMap.get(val);
				Object[] array = new Object[2 + ngroupList.size()];
				array[0] = split[2].substring(1, split[2].length() - 1);
				array[1] = split[1];
				for (int i = 2; i < array.length; i++) {
					array[i] = null;
				}
				result.add(array);
				line = br.readLine();
			}
		}

		String prev = "";
		if( loc.startsWith("IS_fusion") ) {
			/*if( service == null ) {
				service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			}
			
			if( service != null ) {
				String istr = is.toString();
				istr = '('+istr.substring(1, istr.length()-1)+')';
				Reader rd = run("select OriginalFoodCode, OriginalComponentCode, SelectedValue from "+componentvaluetableid+" where OriginalComponentCode in "+istr, true ); //+" and OriginalFoodCode in (select OriginalFoodCode from "+foodtableid+" where WebPublishReady = 'J')", true);
				inThread( rd, result, is, ",", true );
			}*/
		} else if( loc.startsWith("IS") ) {
			/*try {
				new Thread() {
					public void run() {
						try {
							inThread( result, is );
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.run();
			} catch( SecurityException e ) {*/
			InputStream inputStream = this.getClass().getResourceAsStream("/result.txt");
			inThread( new InputStreamReader( inputStream, "UTF-8" ), result, is, "\t", false );
		} else {
			int start = -1;
			InputStream inputStream = this.getClass().getResourceAsStream("NUT_DATA.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\\^");
				if (!split[0].equals(prev)) {
					prev = split[0];
					start++;
				}
				result.get(start + 2)[2 + ngroupMap.get(split[1])] = Float.parseFloat(split[2]);
				// split[1] = fgroupMap.get( val );
				// result.add( split );
				line = br.readLine();
			}
		}

		return result;
	}

	public void updateFilter(int val) {
		// currentSorter = (MySorter)leftTableSorter;
		combo.setSelectedIndex(0);
		String text = field.getText();
		if (text.length() > 0) {
			filter.fInd = 0;
			filter.setFilterText( text );
			// leftTableSorter.modelStructureChanged();
			leftTable.updateFilter();
			table.updateFilter();
			if (leftTable.getRowCount() == 0) {
				filter.fInd = 1;
				leftTable.updateFilter();
				table.updateFilter();
			}
		} else {
			filter.setFilterText( null );
			leftTable.updateFilter();
			table.updateFilter();
		}

		if (leftTable.getRowCount() > 0) {
			if (val == 1) {
				sel = false;
				leftTable.setRowSelectionIntervalSuper(0, 0);
			}

			int r = leftTable.getSelectedRow();

			if (r != -1) {
				Rectangle selrect = leftTable.getCellRect(r, 0, false);
				// System.err.println( selrect );
				// leftTable.repaint();
				table.scrollRectToVisible(selrect);
			}
		}

		// table.tableChanged( new TableModelEvent( table.getModel() ) );
	}

	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
		protected int visibleRow;

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, table.getHeight());
		}

		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			visibleRow = row;
			// ( value.toString() );
			return this;
		}
	};
	
	public KeyListener initFSKeyListener( final Window window ) {
		return new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if( e.getKeyCode() == KeyEvent.VK_F11 && gd.isFullScreenSupported() ) {
					/*Window w = gd.getFullScreenWindow();
					if( w != null ) {
						gd.setFullScreenWindow(null);
					} else {
						gd.setFullScreenWindow(window);
					}*/
					//Screen s;R
					//s.set
					
					if( window instanceof Frame ) {
						((Frame)window).setExtendedState(Frame.MAXIMIZED_BOTH);
						((Frame)window).setUndecorated( true );
						((Frame)window).setResizable( false );
						
					}
					
					if( gd.getFullScreenWindow() == null ) {
						gd.setFullScreenWindow(window);
					} else {
						gd.setFullScreenWindow( null );
					}
				}
			}
		};
	}

	//static boolean shiftdown = false;
	public KeyListener firstInit() {
		CompatUtilities.updateLof();
		
		//this.getAppletContext().

		Window window = SwingUtilities.windowForComponent(this);
		KeyListener keylistener = initFSKeyListener( window );
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			//frame.setUndecorated( true );
			if (!frame.isResizable())
				frame.setResizable(true);
		}
		
		try {
			this.getAppletContext().showDocument( new URL("http://www.matis.is/ISGEM/is/hvad-er-i-matnum/leidbeiningar-med-notkun-forritsins/"), "_blank");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		this.getContentPane().setBackground(bgcolor);
		this.setBackground(bgcolor);
		try {
			//System.getSecurityManager().checkPropertiesAccess();
			System.setProperty("file.encoding", "UTF8");
		} catch( SecurityException e ) {
			
		}

		loadStuff();
		
		this.addKeyListener( new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				reload();
			}
		});

		// String par = this.getParameter("tab");
		// System.err.println( "tabpar " + par );

		/*
		 * try { JSObject jso = JSObject.getWindow( this ); JSObject obj =
		 * (JSObject)jso.getMember("document"); Object par =
		 * (JSObject)obj.getMember("param");
		 * 
		 * if( par != null && par instanceof Integer ) { Integer ii =
		 * (Integer)par; selectTabIndex(ii-1);
		 * 
		 * System.err.println( "succ " + par ); } else { System.err.println( par
		 * ); } } catch( Exception e ) { e.printStackTrace(); }
		 */

		// List<Object[]> sublist = Collections.
		
		return keylistener;
	}
	
	public List<Object[]> reload() {
		final List<Object[]> result = new ArrayList<Object[]>();
		for( List l : nutList ) {
			result.add(l.toArray(new Object[0]));
		}
		
		Collections.sort(stuff, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				if( o1[0] == null && o2[0] == null ) {
					if( lang.startsWith("IS") ) return ((String) o1[2]).compareToIgnoreCase((String) o2[2]);
					else return ((String) o2[2]).compareToIgnoreCase((String) o1[2]);
				}
				else if( o1[0] == null ) return Integer.MIN_VALUE;
				else if( o2[0] == null ) return Integer.MAX_VALUE;
				
				return ((String) o1[0]).compareToIgnoreCase((String) o2[0]);
			}
		});

		foodNameInd.clear();
		int i = 0;
		for (Object[] oo : stuff) {
			if (oo[0] != null)
				foodNameInd.put((String) oo[0], i++);
		}
		
		return result;
	}

	//private static GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	private static final String grouptableid = "1VDYeREOR6SvxX7VxiGBHVvUZbw_q1GeSsFsvh3U";
	private static final String componenttableid = "1V220glQaJXgeWrimZ5gDiyRwhKMNp437ZeAnRNI";
	private static final String foodtableid = "1kVdzllCGHpktvP7jjwVGuQ6M5XF3qUMkBBq9Cn4";
	private static final String componentvaluetableid = "1iPhnOf7BlPQSeMz1zsanxA2mqQ2Kv0PYo-BQE9U";
	
	public void loadStuff() {
		lang = "IS";
		/*
		 * String loc = this.getParameter("loc"); if( loc != null ) { lang =
		 * loc; }
		 */

		//ToolTipManager.sharedInstance().setInitialDelay(100);
		//ToolTipManager.sharedInstance().setDismissDelay(10000);
		//ToolTipManager.sharedInstance().setReshowDelay(0);
		System.err.println("about to load stuff");
		if (stuff == null) {
			try {
				stuff = parseData(lang);

				Collections.sort(stuff, new Comparator<Object[]>() {
					public int compare(Object[] o1, Object[] o2) {
						if( o1[0] == null && o2[0] == null ) {
							if( lang.startsWith("IS") ) return ((String) o1[2]).compareToIgnoreCase((String) o2[2]);
							else return ((String) o2[2]).compareToIgnoreCase((String) o1[2]);
						}
						else if( o1[0] == null ) return Integer.MIN_VALUE;
						else if( o2[0] == null ) return Integer.MAX_VALUE;
						
						return ((String) o1[0]).compareToIgnoreCase((String) o2[0]);
					}
				});

				int i = 0;
				for (Object[] oo : stuff) {
					if (oo[0] != null)
						foodNameInd.put((String) oo[0], i++);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	String sessionKey = null;
	String currentUser = null;
	public String lastResult;
	public void init() {
		final KeyListener keylistener = firstInit();

		try {
			sessionKey = SortTable.this.getParameter("fb_sig_session_key");
			currentUser = SortTable.this.getParameter("fb_sig_user");

			/*
			 * if( sessionKey == null ) { //URL url = new URL(
			 * "http://test.matis.is/isgem/fb.php" );
			 * //getAppletContext().showDocument( url, "_self" );
			 * //getAppletContext().
			 * 
			 * JSObject myBrowser = (JSObject)JSObject.getWindow(this); JSObject
			 * myDocument = (JSObject) myBrowser.getMember("document"); String
			 * myCookie = (String)myDocument.getMember("cookie");
			 * 
			 * System.err.println("cookie "+myCookie);
			 * 
			 * if( myCookie != null && myCookie.length() > 0 ) { int start =
			 * myCookie.indexOf("_user="); if( start >= 0 ) { start += 6; int
			 * end = myCookie.indexOf(';',start); currentUser =
			 * myCookie.substring( start, end ); System.err.println( currentUser
			 * ); }
			 * 
			 * start = myCookie.indexOf("_session_key="); if( start >= 0 ) {
			 * start += 13; int end = myCookie.indexOf(';',start); sessionKey =
			 * myCookie.substring( start, end ); System.err.println( sessionKey
			 * ); } } //System.err.println( "cookie " + myCookie ); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {				
					SortTable.this.setLayout(new BorderLayout());
					SortTable.this.getContentPane().setBackground(bgcolor);
					SortTable.this.setBackground(bgcolor);
					SortTable.this.getRootPane().setBackground(bgcolor);
					initGui(sessionKey, currentUser);
					
					table.addKeyListener( keylistener );
					
					SortTable.this.requestFocus();
					
					try {
						URL url = SortTable.this.getDocumentBase();
						String urlstr = url.toString();
						int l = urlstr.length();
						String c = urlstr.substring(l - 1, l);
						int v = -1;
						
						char chr = c.charAt(0);
						if( chr >= '0' && chr <= '9' ) {
							try {
								v = Integer.parseInt(c);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (v >= 0 && v < 8) {
							SortTable.this.selectTabIndex(v - 1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					SortTable.this.add(splitPane);
					//SortTable.this.invalidate();
					//SortTable.this.repaint();
					//SortTable.this.revalidate();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		/*try {
			new Thread() {
				public void run() {
					System.err.println("starting");
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.err.println("now");
					updateFriends(sessionKey, currentUser);
				}
			}.start();
		} catch( SecurityException se ) {
			updateFriends(sessionKey, currentUser);
		}*/
	}

	Color bgcolor = new Color(255, 255, 255);

	boolean midact = true;
	boolean elsact = false;

	boolean sel = false;

	int size = 300;
	
	public String makeCopyString() {
		StringBuilder sb = new StringBuilder();
            
        int[] rr = table.getSelectedRows();
        int[] cc = table.getSelectedColumns();
        
        sb.append("\t");
        for( int c : cc ) {
        	sb.append( "\t" + topTable.getValueAt(0, c) );
        }
        sb.append("\n\t");
        for( int c : cc ) {
        	sb.append( "\t" + table.getColumnName(c) );
        }
        sb.append("\n");
        
        for( int ii : rr ) {
        	sb.append( leftTable.getValueAt(ii, 0) );
        	sb.append( "\t"+leftTable.getValueAt(ii, 1) );
            for( int jj : cc ) {
            	Object val = table.getValueAt(ii,jj);
                if( val != null && val instanceof Float ) sb.append( "\t"+Float.toString( (Float)val ) );
                else sb.append( "\t" );
            }
            sb.append( "\n" );
        }
        return sb.toString();
	}

	public void initGui(String sessionKey, String currentUser) throws IOException, ClassNotFoundException {
		scrollPane = new JScrollPane();
		leftScrollPane = new JScrollPane();
		topScrollPane = new JScrollPane();
		topLeftScrollPane = new JScrollPane();
		table = new JCompatTable() {
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = (MySorter) e.getSource();
				leftTable.repaint();
				super.sorterChanged(e);
			}

			/*
			 * public void moveColumn( int column, int targetColumn ) {
			 * super.moveColumn(column, targetColumn);
			 * topTable.moveColumn(column, targetColumn); }
			 */

			public void setRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.addColumnSelectionInterval(c1, c2);
			}
		};
		table.setColumnSelectionAllowed(true);
		
		//if( this.applet != null ) {
		try {
			WSUtil.clipboard( this );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		//}
	   
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] rr = table.getSelectedRows();
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								leftTable.setRowSelectionInterval(r, r);
							else
								leftTable.addRowSelectionInterval(r, r);
							sel = true;
						}
					}
				}
			}
		});

		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] cc = table.getSelectedColumns();
					if (cc != null && cc.length > 0) {
						for (int c : cc) {
							if (c == cc[0])
								topTable.setColumnSelectionInterval(c, c);
							else
								topTable.addColumnSelectionInterval(c, c);
							sel = true;
						}
					}
				}
			}
		});
		
		final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();//new DataFlavor("text/plain;charset=utf-8");
		//final DataFlavor textfl = DataFlavor.pl
		//String mime = df.getMimeType();
		final String charset = df.getParameter("charset");
		//int start = mime.indexOf("harset=")+7;
		//int stop = mime.indexOf(';', start);
		//if( stop == -1 ) stop = mime.length();
		//final String type = mime.substring(start, stop);
		
		final Transferable transferable = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
				String ret = makeCopyString();
				//return arg0.getReaderForText( this );
				return new ByteArrayInputStream( ret.getBytes( charset ) );
				//return ret;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { df };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor arg0) {
				if( arg0.equals(df) ) {
					return true;
				}
				return false;
			}
		};

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
				/*try {
					Object obj = support.getTransferable().getTransferData( df );
					InputStream is = (InputStream)obj;
					
					byte[] bb = new byte[2048];
					int r = is.read(bb);
					
					//importFromText( new String(bb,0,r) );
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				return false;
			}
		};
		table.setTransferHandler( th );
		table.setDragEnabled( true );

		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {}

			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> tcs = table.getColumnModel().getColumns();
				int i = 0;
				while (tcs.hasMoreElements()) {
					TableColumn tc = tcs.nextElement();
					topTable.getColumnModel().getColumn(i++).setPreferredWidth(tc.getPreferredWidth());
				}
			}

			public void columnMoved(TableColumnModelEvent e) {
				topTable.moveColumn(e.getFromIndex(), e.getToIndex());
			}

			public void columnRemoved(TableColumnModelEvent e) {}

			public void columnSelectionChanged(ListSelectionEvent e) {
			}
		});

		leftTable = new JCompatTable() {
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = (MySorter) e.getSource();
				table.repaint();
				super.sorterChanged(e);
			}

			public String getToolTipText() {
				return super.getToolTipText();
			}

			/*public String getToolTipText(MouseEvent me) {
				Point p = me.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				if (r >= 0 && r < super.getRowCount()) {
					Object ret = super.getValueAt(r, c);
					if (ret != null) {
						return ret.toString(); // super.getToolTipText( me );
					}
				}
				return "muus";
			}*/

			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}

			/*
			 * public void addRowSelectionInterval( int r1, int r2 ) {
			 * super.addRowSelectionInterval(r1, r2); if( table != null ) {
			 * table.addColumnSelectionInterval(0, table.getColumnCount()-1);
			 * table.setRowSelectionInterval(r1, r2); } }
			 * 
			 * public void setRowSelectionInterval( int r1, int r2 ) {
			 * super.setRowSelectionInterval(r1, r2); if( table != null ) {
			 * table.setColumnSelectionInterval(0, table.getColumnCount()-1);
			 * table.setRowSelectionInterval(r1, r2); } }
			 */

			public Point getToolTipLocation(MouseEvent e) {
				return super.getToolTipLocation(e); //e.getPoint();
			}
		};
		leftTable.setDragEnabled(true);
		//leftTable.setToolTipText(" ");
		//leftTable.sett
		// leftTable.en
		topTable = new JCompatTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}
		};
		topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		topTable.setRowSelectionAllowed(false);
		topTable.setColumnSelectionAllowed(true);
		topTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int[] cc = topTable.getSelectedColumns();
					if (cc != null && cc.length > 0) {
						for (int c : cc) {
							if (c == cc[0])
								table.setColumnSelectionInterval(c, c);
							else
								table.addColumnSelectionInterval(c, c);
							sel = false;
						}
					}
				}
			}
		});

		final CharBuffer cb = CharBuffer.allocate(1000000);
		// ba.order( ByteOrder.LITTLE_ENDIAN );
		leftTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				int row = leftTable.getSelectedRow();
				if (row >= 0 && row < leftTable.getRowCount()) {
					final String oStr = (String) leftTable.getValueAt(row, 0);
					if (oStr != null) {
						final String str = oStr.replaceAll("[ ,]+", "+");
						// int row = e.getFirstIndex();
						if (tabbedPane.getSelectedComponent() == graph) {
							graph.repaint();
						} else if (tabbedPane.getSelectedComponent() == detail) {
							if (!str.equals(lastResult)) {
								int rrow = leftTable.convertRowIndexToModel(row);
								int r = rrow - (stuff.size() - 2);

								lastResult = str;
								imgPanel.img = null;
								imgPanel.imgUrl = null;
								imgPanel.progressbar.setVisible(false);
								imgPanel.repaint();

								if (r >= 0 && r < recipe.recipes.size()) {
									if (imgPanel.imageNameCache.containsKey(oStr)) {
										String imgUrl = imgPanel.imageNameCache.get(oStr);
										if (imgUrl != null) {
											imgPanel.img = imgPanel.imageCache.get(imgUrl);
											imgPanel.repaint();
										}
									} /*
									 * else if( imgPanel.vals.contains(oStr) ) {
									 * imgPanel.imgUrl = imgPanel.vals.get( oStr
									 * ); imgPanel.progressbar.setVisible( true
									 * ); }
									 */else {
										Recipe rep = recipe.recipes.get(r);
										if (rep.desc != null) {
											int i = rep.desc.indexOf("http://");
											if (i >= 0) {
												int n = rep.desc.indexOf('"', i);
												if (n > i) {
													String s = rep.desc.substring(i, n);
													imgPanel.imageNameCache.put(oStr, s);
													imgPanel.threadRun(s, row);
													// System.err.println(
													// s );

													/*
													 * lastResult = str;
													 * imgPanel.img = null;
													 * imgPanel.repaint ();
													 * //imgPanel.runThread (
													 * str ); imgPanel.tryName(
													 * s );
													 */
												}
											}
										}
									}
								} else {
									try {
										imgPanel.tryName(oStr);
									} catch (UnsupportedEncodingException e1) {
										e1.printStackTrace();
									}
								}

							}
							detail.detailTable.tableChanged(new TableModelEvent(detail.detailModel));
						} else if (tabbedPane.getSelectedComponent() == rightSplitPane) {
							boolean ss = sel;
							sel = false;
							if (!ss) {
								int[] rr = leftTable.getSelectedRows();
								if (rr != null && rr.length > 0) {
									for (int r : rr) {
										if (r == rr[0])
											table.setRowSelectionInterval(r, r);
										else
											table.addRowSelectionInterval(r, r);
										sel = false;
									}
								}
							}
							// table.setrow
							// table.repaint();
						}
					}
				}
			}
		});
		// System.err.println( leftTable.getColumnModel() );
		topLeftTable = new JCompatTable() {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
				return c;
			}
		};
		topLeftTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				/*
				 * TableModel old = table.getModel(); TableModel oldTop =
				 * topTable.getModel();
				 * 
				 * table.setModel( nullModel ); topTable.setModel( nullModel );
				 * 
				 * table.setModel( old ); topTable.setModel( oldTop );
				 * 
				 * topTable.tableChanged( new TableModelEvent(
				 * topTable.getModel() ) ); table.tableChanged( new
				 * TableModelEvent( table.getModel() ) );
				 * 
				 * topTable.revalidate(); topTable.repaint();
				 * table.revalidate(); table.repaint();
				 */

				/*
				 * if( e.getClickCount() == 2 ) { int r =
				 * topLeftTable.getSelectedRow(); if( r >= 0 && r <
				 * topLeftTable.getRowCount() ) { for( int start = 0; start <
				 * topTable.getColumnCount()-1; start++ ) { String min = ""; int
				 * ind = start; for( int i = start; i <
				 * topTable.getColumnCount(); i++ ) { Object val =
				 * topTable.getValueAt(r, i); if( val != null ) { String s =
				 * val.toString(); if( s.compareTo(min) > 0 ) { min = s; ind =
				 * i; } } } if( ind > start ) { table.moveColumn(ind, start); }
				 * } } }
				 */
			}
		});

		// table.setAutoCreateRowSorter( true );
		// leftTable.setRowSorter( table.getRowSorter() );
		// leftTable.setAutoCreateRowSorter( true );

		JComponent topComp = new JComponent() {};
		topComp.setLayout(new BorderLayout());

		JComponent topLeftComp = new JComponent() {};
		topLeftComp.setLayout(new BorderLayout());

		topTable.setShowGrid(true);
		topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);

		// table.getTableHeader().setVisible( false );
		// table.setTableHeader( null );

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setRowHeaderView(leftTable);
		leftScrollPane.setViewport(scrollPane.getRowHeader());
		leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		leftScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		topComp.add(topTable);
		topComp.add(table.getTableHeader(), BorderLayout.SOUTH);

		final Image matisLogo;
		URL codeBase = null;
		try {
			codeBase = SortTable.this.getCodeBase();
		} catch (Exception e) {}
		
		System.err.println("codebase " + codeBase);

		if (codeBase == null) {
			Image img = null;

			InputStream is = SortTable.class.getResourceAsStream("/matis.png");
			img = ImageIO.read( is );
			/*try {
				URL url = new URL("http://test.matis.is/isgem/Matis_logo.jpg");
				img = ImageIO.read(url.openStream());
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/

			matisLogo = img;
		} else {
			matisLogo = SortTable.this.getImage(codeBase, "matis.png");
		}

		/*
		 * JComponent logoPaint = new JComponent() { public void paintComponent(
		 * Graphics g ) { Graphics2D g2 = (Graphics2D)g; g2.setRenderingHint(
		 * RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		 * g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_ON ); g2.setRenderingHint(
		 * RenderingHints.KEY_INTERPOLATION,
		 * RenderingHints.VALUE_INTERPOLATION_BICUBIC ); g2.drawImage(
		 * matisLogo, 0, 0, this.getWidth(), this.getHeight(), this ); } };
		 */

		// logoPaint.setPreferredSize( new Dimension( 32, 32 ) );
		// topLeftComp.add(topLeftTable, BorderLayout.EAST );
		// topLeftComp.add( logoPaint, BorderLayout.WEST );
		topLeftComp.add(leftTable.getTableHeader(), BorderLayout.SOUTH);
		// topScrollPane.setViewportView( topTable );
		// scrollPane.setColumnHeader( topScrollPane.getViewport() );
		// scrollPane.setColumnHeaderView( topTable );

		final JComboBox topLeftCombo = new JComboBox() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private boolean layingOut = false;

			public void doLayout() {
				try {
					layingOut = true;
					super.doLayout();
				} finally {
					layingOut = false;
				}
			}

			public Dimension getSize() {
				Dimension sz = super.getSize();
				if (!layingOut) {
					sz.width = Math.max(sz.width, size);
				}
				return sz;
			}
		};
		combo = topLeftCombo;

		Set<String> foodTypes = new TreeSet<String>();
		for (Object[] objs : stuff) {
			if (objs[1] != null)
				foodTypes.add((String) objs[1]);
		}

		final String allfood = lang.startsWith("IS") ? "Allar fæðutegundir" : "All food groups";
		final String recipes = lang.startsWith("IS") ? "Uppskriftir" : "Recipes";
		
		topLeftCombo.addItem(allfood);
		topLeftCombo.addItem(recipes);
		for (String ft : foodTypes) {
			topLeftCombo.addItem(ft.length() > 64 ? ft.substring(0, 64) + "..." : ft);
		}

		Dimension d = new Dimension(20, 20);
		// topLeftCombo.setMaximumSize( d );
		size = Math.max(20, topLeftCombo.getPreferredSize().width);
		topLeftCombo.setPreferredSize(d);
		topLeftCombo.setMinimumSize(d);
		topLeftComp.add(topLeftCombo);

		topLeftCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String item = (String) combo.getSelectedItem();
				item = item.substring(0, Math.min(100, item.length()));
				if (item.equals(allfood)) {
					filter.setFilterText( null );
					filter.fInd = 0;

					// leftTable.getColumnModel().getColumn(1).setMinWidth( 0 );
					leftTable.getColumnModel().getColumn(1).setMaxWidth(1000);
					leftTable.getColumnModel().getColumn(1).setPreferredWidth(leftTable.getWidth() / 2);
					leftTable.getColumnModel().getColumn(1).setWidth(leftTable.getWidth() / 2);
				} else {
					filter.setFilterText( item );
					filter.fInd = 1;

					leftTable.getColumnModel().getColumn(1).setMinWidth(0);
					leftTable.getColumnModel().getColumn(1).setMaxWidth(0);
					// leftTable.getColumnModel().getColumn(1).setPreferredWidth(
					// 0 );
					leftTable.getColumnModel().getColumn(1).setWidth(0);
				}
				leftTable.updateFilter();
				table.updateFilter();
			}
		});

		JViewport spec = new JViewport() {
			public void setView(Component view) {
				if (!(view instanceof JTableHeader))
					super.setView(view);
			}
		};
		spec.setView(topComp);

		JViewport leftSpec = new JViewport() {
			public void setView(Component view) {
				if (!(view instanceof JTableHeader))
					super.setView(view);
			}
		};
		leftSpec.setView(topLeftComp);

		scrollPane.setColumnHeader(spec);
		topTable.setTableHeader(null);
		topScrollPane.setViewport(scrollPane.getColumnHeader());
		topScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		topScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		leftScrollPane.setColumnHeader(leftSpec);
		topLeftScrollPane.setViewport(leftScrollPane.getColumnHeader());
		topLeftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		topLeftScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		field = new JTextField() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				// g2.setRenderingHint( , hintValue)

				if (this.getText() == null || this.getText().length() == 0) {
					Font f = g.getFont();
					if (!(f.isBold() && f.isItalic())) {
						f = f.deriveFont(Font.BOLD | Font.ITALIC);
						g.setFont(f);
					}
					g.setColor(Color.gray);
					
					String leit = lang.startsWith("IS") ? "leit í " + (stuff.size()-2) + " fæðutegundum" : "search " + (stuff.size()-2) + " food entries";
					int strw = g.getFontMetrics().stringWidth(leit);
					int x = (this.getWidth() - strw) / 2;
					g.drawString(leit, x, 20);
				}
			}
		};
		field.setPreferredSize(new Dimension(100, 30));
		JComponent leftComponent = new JComponent() {

		};
		leftComponent.setLayout(new BorderLayout());
		leftComponent.add(leftScrollPane);
		leftComponent.add(field, BorderLayout.SOUTH);

		imgPanel = new ImagePanel( this, leftTable, lang );
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

		FriendsPanel fp = new FriendsPanel(sessionKey, currentUser, lang);
		friendsPanel = fp;

		try {
			URL url = new URL("http://test.matis.is");
			URLConnection uc = url.openConnection();
			uc.setDefaultUseCaches(true);
			uc.setUseCaches(true);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		RdsPanel rdsPanel = new RdsPanel(fp, SortTable.this);

		rightSplitPane = new LinkedSplitPane(JSplitPane.VERTICAL_SPLIT, topScrollPane, scrollPane);
		leftSplitPane = new LinkedSplitPane(JSplitPane.VERTICAL_SPLIT, topLeftScrollPane, leftComponent);
		rightSplitPane.setLinkedSplitPane(leftSplitPane);
		leftSplitPane.setLinkedSplitPane(rightSplitPane);

		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setDividerLocation(50);

		recipe = new RecipePanel(fp, lang, table, leftTable, foodNameInd);
		try {
			eat = new HabitsPanel(lang, friendsPanel, stuff, recipe.recipes, foodNameInd, recipe.allskmt, recipe.skmt);
		} catch( NoClassDefFoundError e2 ) {
			e2.printStackTrace();
		}
		//final HabitsPanel eat = new HabitsPanel(lang, friendsPanel, stuff, recipe.recipes, foodNameInd, recipe.allskmt, recipe.skmt);
		CostPanel buy = new CostPanel();

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, tabbedPane);
		splitPane.setOneTouchExpandable(true);
		// splitPane.setDividerSize( 3 );

		field.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				updateFilter(0);
			}

			public void insertUpdate(DocumentEvent e) {
				updateFilter(1);
			}

			public void removeUpdate(DocumentEvent e) {
				updateFilter(2);
			}
		});
		// final JFrame f = new JFrame();
		// f.setAlwaysOnTop(true);
		// f.setUndecorated( true );
		// f.add( field );
		// Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().
		// System.err.println(c);
		/*
		 * leftTable.addKeyListener( new KeyListener() {
		 * 
		 * 
		 * public void keyPressed(KeyEvent e) { if( !f.isVisible() ) {
		 * f.setBounds(10, 10, 200, 25); f.setVisible( true ); } try { if(
		 * e.getKeyCode() != KeyEvent.VK_BACK_SPACE )
		 * field.getDocument().insertString(field.getCaretPosition(),
		 * Character.toString(e.getKeyChar()), null); } catch
		 * (BadLocationException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } //System.err.println( field.getText() );
		 * //leftTableSorter.setRowFilter(
		 * RowFilter.regexFilter(field.getText(), 1) );
		 * //System.err.println(e.getKeyChar()); }
		 * 
		 * 
		 * public void keyReleased(KeyEvent e) { // TODO Auto-generated method
		 * stub
		 * 
		 * }
		 * 
		 * 
		 * public void keyTyped(KeyEvent e) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * });
		 */

		// scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, field);
		// leftScrollPane.set

		/*
		 * table.setDefaultRenderer( Float.class, new TableCellRenderer() {
		 * public Component getTableCellRendererComponent(JTable table, Object
		 * value, boolean isSelected, boolean hasFocus, int row, int column) {
		 * 
		 * return null; } });
		 */

		TableModel topLeftModel = new TableModel() {

			public void addTableModelListener(TableModelListener l) {}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 1;
			}

			public String getColumnName(int columnIndex) {
				return null;
			}

			public int getRowCount() {
				return 2;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				if (lang.startsWith("IS")) {
					if (rowIndex == 0)
						return "Nafn efnis";
					return "Eining";
				} else {
					if (rowIndex == 0)
						return "Name";
					return "Unit";
				}
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {}

			public void setValueAt(Object value, int rowIndex, int columnIndex) {}
		};
		topLeftTable.setModel(topLeftModel);

		detail = new DetailPanel( this, rdsPanel, lang, imgPanel, table, topTable, leftTable, stuff, ngroupList, ngroupGroups, foodNameInd, recipe.recipes );
		topModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {
			}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				int cc = detail.countVisible();
				return cc;
				// return stuff.get(0).length-2;
			}

			public String getColumnName(int columnIndex) {
				return null;
			}

			public int getRowCount() {
				return 1;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				//Object[] obj = stuff.get(rowIndex);
				int realColumnIndex = detail.convertIndex(columnIndex);
				return ngroupList.get(realColumnIndex); //obj[realColumnIndex + 2];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
			}

		};
		topTable.setModel(topModel);

		final TableModel leftModel = new TableModel() {

			public void addTableModelListener(TableModelListener l) {
			}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 2;
			}

			public String getColumnName(int columnIndex) {
				if (lang.startsWith("IS")) {
					if (columnIndex == 0)
						return "Fæðutegund";
					else if (columnIndex == 1)
						return "Fæðuflokkur";
					return "Óþekkt";
				} else {
					if (columnIndex == 0)
						return "Food Name";
					else if (columnIndex == 1)
						return "Food Group";
					return "Unknown";
				}
			}

			public int getRowCount() {
				return stuff.size() - 2 + recipe.recipes.size() + 1;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[] obj = null;
				if (rowIndex < stuff.size() - 2) {
					obj = stuff.get(rowIndex + 2);
					if (columnIndex >= 0)
						return obj[columnIndex];
				} else if( rowIndex < stuff.size() - 2 + recipe.recipes.size() ) {
					int r = rowIndex - (stuff.size() - 2);
					if (r < recipe.recipes.size()) {
						Recipe rep = recipe.recipes.get(r);
						if (columnIndex == 1) {
							return "Uppskriftir - " + rep.group;
						} else {
							return rep.name + " - " + rep.author;
						}
					}
				} else {
					if( columnIndex == 0 ) return "Val";
					return "í máltíðartöflu";
				}

				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
			}
		};
		leftTable.setModel(leftModel);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sel = true;
			}
		});
		topTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sel = false;
			}
		});
		leftTable.addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved( MouseEvent me ) {
				Point p = me.getPoint();
				int r = leftTable.rowAtPoint(p);
				int c = leftTable.columnAtPoint(p);
				if (r >= 0 && r < leftTable.getRowCount()) {
					Object ret = leftTable.getValueAt(r, c);
					if (ret != null) {
						leftTable.setToolTipText( ret.toString() ); // super.getToolTipText( me );
					}
				}
			}
		});
		leftTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				leftTable.requestFocus();

				sel = false;

				if (e.getClickCount() == 2) {
					if (tabbedPane.getSelectedComponent() == recipe /*
																	 * &&
																	 * leftTable
																	 * .
																	 * columnAtPoint
																	 * (p) == 0
																	 */) {
						if (recipe.currentRecipe != null) {
							// recipe.currentRecipe.ingredients.add( new
							// RecipePanel.RecipeIngredient() );

							try {
								recipe.currentRecipe.destroy();
							} catch (IOException e2) {
								e2.printStackTrace();
							}

							int r = leftTable.getSelectedRow();
							int rr = leftTable.convertRowIndexToModel(r);
							Object val = leftTable.getValueAt(r, 0);
							if (val != null) {
								recipe.currentRecipe.addIngredient(val.toString(), 100, "g");
							}
							recipe.recipeDetailTable.revalidate();
							recipe.recipeDetailTable.repaint();

							try {
								recipe.currentRecipe.save();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} else {
						/*
						 * int r = leftTable.getSelectedRow(); int rr =
						 * leftTable.convertRowIndexToModel(r);
						 * 
						 * String str = (String)leftModel.getValueAt(rr, 0);
						 * cropped.add(str);
						 * 
						 * leftTableSorter.setRowFilter( filter );
						 * tableSorter.setRowFilter( filter );
						 * 
						 * //tableSorter.sort(); leftTableSorter.sort();
						 * //tableSorter.modelStructureChanged();
						 * //leftTableSorter.modelStructureChanged();
						 * //leftTable.tableChanged( new TableModelEvent(
						 * leftModel ) );
						 */
					}
					/*
					 * int r = leftTable.getSelectedRow(); if( r >= 0 && r <
					 * leftTable.getRowCount() ) { for( int start = 0; start <
					 * table.getColumnCount()-1; start++ ) { float min =
					 * Float.NEGATIVE_INFINITY; int ind = start; for( int i =
					 * start; i < table.getColumnCount(); i++ ) { Object val =
					 * table.getValueAt(r, i); if( val != null ) { float f =
					 * (Float)val; if( f > min ) { min = f; ind = i; } } } if(
					 * ind > start ) { table.moveColumn(ind, start); } } }
					 */
				}
			}
		});

		// TreeTableCellRenderer treeCellRenderer = new TreeTableCellRenderer();
		// leftTable.getColumnModel().getColumn(0).setCellRenderer(
		// treeCellRenderer );

		model = new TableModel() {
			// Object[] retObj = {};

			public void addTableModelListener(TableModelListener l) {
			}

			public Class<?> getColumnClass(int columnIndex) {
				return Float.class;
			}

			public int getColumnCount() {
				int cc = topModel.getColumnCount();
				return cc;
			}

			public String getColumnName(int columnIndex) {
				int realColumnIndex = detail.convertIndex(columnIndex);
				if (realColumnIndex != -1) {
					Object[] obj = stuff.get(1);
					//return "erm";
					Object o = obj[2+realColumnIndex];
					if( o instanceof String ) return (String)o;
					/*else {
						System.err.println("erm");
					}*/
					//return ngroupList.get(realColumnIndex);
				}
				return null;
			}

			public int getRowCount() {
				return stuff.size() - 2 + recipe.recipes.size() + 1;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				Float	retval = null;
				
				Object[] obj = null;
				if (rowIndex < stuff.size() - 2) {
					obj = stuff.get(rowIndex + 2);
					int realColumnIndex = detail.convertIndex(columnIndex);
					if (realColumnIndex != -1) {
						Object retobj = obj[realColumnIndex + 2];
						if( retobj instanceof Float ) retval = (Float)retobj;
					}
				} else if( rowIndex < stuff.size() - 2 + recipe.recipes.size() ) {
					float ret = 0.0f;
					int i = rowIndex - (stuff.size() - 2);
					Recipe rep = recipe.recipes.get(i);
					//float tot = 0.0f;
					//int realColumnIndex = detail.convertIndex(columnIndex);
					for (RecipeIngredient rip : rep.ingredients) {
						int k = -1;
						float div = 100.0f;
						if (foodNameInd.containsKey(rip.stuff)) {
							k = foodNameInd.get(rip.stuff);
							//int newRowIndex = table.convertRowIndexToView( k );
							//obj = stuff.get(k + 2);
							//if (realColumnIndex != -1) {
							//}
						} else {
							int 	ri = 0;
							Recipe 	rp = null;
							for( Recipe r : recipe.recipes ) {
								if( rep != r && rip.stuff.equals( r.name + " - " + r.author ) ) {
									rp = r;
									break;
								}
								ri++;
							}
							
							if( ri < recipe.recipes.size() ) {
								k = stuff.size()-2+ri;
								div = rp.getWeight();
							}
						}
						
						if( k != -1 ) {
							Object val = getValueAt( k, columnIndex ); //obj[realColumnIndex + 2];
							if (val != null && val instanceof Float) {
								/*float d = rip.measure;
								if (!rip.unit.equals("g")) {
									String ru = rip.unit;
									int f = ru.indexOf("(");
									int n = ru.indexOf(")");
									if (n > f && f != -1) {
										String subbi = ru.substring(f + 1, n);
										if (subbi.endsWith("g"))
											subbi = subbi.substring(0, subbi.length() - 1);
	
										float fl = 0.0f;
										try {
											fl = Float.parseFloat(subbi);
										} catch (Exception e) {
	
										}
										d *= fl;
									}
								}
								tot += d;
	
								float f = (((Float) val) * d) / 100.0f;*/
								
								float f = rip.getValue( (Float)val ) / div;
								ret += f;
							}
						}
					}

					if (ret != 0.0f) {
						// return (ret * 100.0f) / tot;
						retval = ret;
					}
				} else {
					float ret = 0.0f;
					String[] spl = eat.getSelection().split("\n");
					
					for( String val : spl ) {
						String[] split = ((String)val).split("\\|");
						Integer ii = foodNameInd.get( split[0] );
						
						int i;
						if( ii == null ) {
							i = stuff.size()-2;
							for( Recipe rep : recipe.recipes ) {
								if( split[0].equals( rep.name + " - " + rep.author ) ) break;
								i++;
							}
						} else {
							i = ii;
						}
						
						if( i < stuff.size()+recipe.recipes.size()-2 ) {
							Float fval = (Float)DetailPanel.getVal(i, columnIndex, stuff, foodNameInd, recipe.recipes, false);
							if( fval != null ) {
								ret += eat.updateEng( fval, split );
							}
						}
						
						/*int k = -1;
						float div = 100.0f;
						
						String[] split = ss.split("\\|");
						String name = split[0];
						//Integer ii = foodInd.get( name );
						
						if( foodNameInd.containsKey( name ) ) {
							k = foodNameInd.get( name );
						} else {
							/*int 	ri = 0;
							Recipe 	rp = null;
							for( Recipe r : recipe.recipes ) {
								if( r.name.equals( r.name + " - " + r.author ) ) {
									rp = r;
									break;
								}
								ri++;
							}
							
							if( ri < recipe.recipes.size() ) {
								k = stuff.size()-2+ri;
								div = rp.getWeight();
								
								float f = rip.getValue( (Float)val ) / div;
								ret += f;
							}*
						}
						
						if( k != -1 ) {
							Object val = getValueAt( k, columnIndex ); //obj[realColumnIndex + 2];
							if (val != null && val instanceof Float) {
								float f = 0.0f;//rip.getValue( (Float)val ) / div;
								ret += f;
							}
						}*/
					}
					
					if (ret >= 0) {
						retval = ret;
					}
				}
				return retval;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
			}
		};
		// table.setModel( model );

		tableSorter = new MySorter(model) {
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper(index);
			}

			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper(index);
				// leftTableSorter.
			}

			public int getViewRowCount() {
				return leftTableSorter.getViewRowCount();
			}
		};
		// table.setRowSorter( tableSorter );
		/*
		 * tableSorter.addRowSorterListener( new RowSorterListener() {
		 * 
		 * public void sorterChanged(RowSorterEvent e) {
		 * 
		 * } });
		 */

		currentSorter = (MySorter) tableSorter;
		leftTableSorter = new MySorter(leftModel) {
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper(index);
			}

			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper(index);
				// super.
				// currentSorter.
			}
		};
		leftTable.setRowSorter(leftTableSorter);

		filter = new MyFilter(leftModel);
		tableSorter.setRowFilter(filter);

		leftTable.sorter = leftTableSorter;
		table.sorter = tableSorter;
		leftTable.setFilter(filter);
		MyFilter subfilt = table.setFilter(filter);

		filter.addFilterListener(new Runnable() {
			public void run() {
				// leftTable.setSortable( true );
				// table.setSortable( false );
				table.setSortOrder(leftTable.getSortedColumn(), CompatUtilities.UNSORTED);

				table.repaint();
				leftTable.repaint();
			}
		});

		subfilt.addFilterListener(new Runnable() {
			public void run() {
				// leftTable.getSortedColumn();
				// leftTable.setSortable( false );
				// table.setSortable( true );
				leftTable.setSortOrder(leftTable.getSortedColumn(), CompatUtilities.UNSORTED);

				table.repaint();
				leftTable.repaint();
			}
		});

		graph = new GraphPanel(rdsPanel, recipe, eat, foodNameInd, stuff.size(), lang, new JCompatTable[] { table, leftTable, topTable }, topModel);

		ImageIcon fodicon = null;
		ImageIcon helicon = null;
		ImageIcon samicon = null;
		ImageIcon rdsicon = null;
		ImageIcon uppicon = null;
		ImageIcon vinicon = null;
		ImageIcon maticon = null;
		ImageIcon hlpicon = null;

		int size = 16;
		try {
			InputStream url = SortTable.this.getClass().getResourceAsStream("/food.png");
			if (url != null)
				fodicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/sams.png");
			if (url != null)
				samicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/helix.png");
			if (url != null)
				helicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/rds.png");
			if (url != null)
				rdsicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/mat.png");
			if (url != null)
				maticon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/uppsk.png");
			if (url != null)
				uppicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/vinir.png");
			if (url != null)
				vinicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			url = SortTable.this.getClass().getResourceAsStream("/help.png");
			if (url != null)
				hlpicon = new ImageIcon(ImageIO.read(url).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//URL helpurl = this.getClass().getResource("/helppage.html");
		JEditorPane helppane = new JEditorPane() {
			public void paint( Graphics g ) {
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				super.paintComponent( g );
			}
			
			public void paintComponent( Graphics g ) {
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				super.paintComponent( g );
			}
		};
		//((Graphics2D)(helppane.getGraphics())).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		helppane.setContentType("text/html");
		helppane.setEditable( false );
		
		try {
			SecurityManager secm = System.getSecurityManager();
			if( secm != null ) {
				secm.checkConnect("test.matis.is", 80);
				URL	url = new URL( "http://test.matis.is/isgem/help/" );
				helppane.setPage( url );
			} else {
				URL	url = new URL( "http://test.matis.is/isgem/help/" );
				helppane.setPage( url );
			}
		} catch( AccessControlException e ) {
			
		} catch( IOException e ) {
			
		}
		
		JScrollPane help = new JScrollPane( helppane );
		if (lang.startsWith("IS")) {
			tabbedPane.addTab("Hjálp", hlpicon, help);
			tabbedPane.addTab("Fæða", fodicon, rightSplitPane);
			tabbedPane.addTab("Næringarefni", helicon, detail);
			tabbedPane.addTab("Samsetning", samicon, graph);
			tabbedPane.addTab("Uppskriftir", uppicon, recipe);
			if (fp != null) tabbedPane.addTab("Ég og vinir", vinicon, fp);
			tabbedPane.addTab("Máltíðir", maticon, eat);
			
			//tabbedPane.addTab("Mataræði og Hreyfing", maticon, eat);
			//tabbedPane.addTab("Innkaup og kostnaður", buy);
			// tabbedPane.setEnabledAt( tabbedPane.getTabCount()-2, false );
			//tabbedPane.setEnabledAt(tabbedPane.getTabCount() - 1, false);
			
			/*tabbedPane.addTab("Hjálp", hlpicon, help);
			tabbedPane.addTab("Fæða", fodicon, rightSplitPane);
			// tabbedPane.addTab( "Myndir", imgPanel );
			tabbedPane.addTab("Næringarefni", helicon, detail);
			tabbedPane.addTab("Samsetning", samicon, graph);
			tabbedPane.addTab("Uppskriftir", uppicon, recipe);
			if (fp != null) tabbedPane.addTab("Ég og vinir", vinicon, fp);
			//tabbedPane.addTab("RDS", rdsicon, rdsPanel);
			tabbedPane.addTab("Máltíðir", maticon, eat);
			//tabbedPane.addTab("Mataræði og Hreyfing", maticon, eat);
			//tabbedPane.addTab("Innkaup og kostnaður", buy);
			// tabbedPane.setEnabledAt( tabbedPane.getTabCount()-2, false );
			//tabbedPane.setEnabledAt(tabbedPane.getTabCount() - 1, false);*/
		} else {
			tabbedPane.addTab("Food", fodicon, rightSplitPane);
			// tabbedPane.addTab( "Image", imgPanel );
			tabbedPane.addTab("Nutrition", helicon, detail);
			tabbedPane.addTab("Combination", samicon, graph);
			tabbedPane.addTab("Recipes", uppicon, recipe);
			if (fp != null)
				tabbedPane.addTab("Friends", vinicon, fp);
			//tabbedPane.addTab("Rds", rdsicon, rdsPanel);
			tabbedPane.addTab("Eating and training", maticon, eat);
			//tabbedPane.addTab("Cost of buying", buy);

			//tabbedPane.setEnabledAt(tabbedPane.getTabCount() - 1, false);
		}

		table.setModel(model);
		table.setRowSorter(tableSorter);

		// RowFilter<TableModel, Integer> rf = RowFilter.regexFilter("Milk",1);
		// leftTableSorter.setRowFilter( rf );
		// panel.setLayout( new BorderLayout() );
		// Dimension d = new Dimension(300,300);
		// panel.setPreferredSize( d );
		// panel.setSize( d );
		// panel.setMinimumSize( d );
		// imagePanel.setPreferredSize( d );
		// imagePanel.setSize( d );
		// panel.add( imagePanel, BorderLayout.WEST );

		/*
		 * ed = new JEditorPane(); ed.setContentType("text/html");
		 * ed.setEditable(false); ed.setText(
		 * "<html><body><center><table cellpadding=0><tr><td><img src=\"http://test.matis.is/isgem/Matis_logo.jpg\" hspace=\"5\" width=\"32\" height=\"32\">"
		 * +
		 * "</td><td align=\"center\"><a href=\"http://www.matis.is\">Matís ohf.</a> - Borgartún 21 | 105 Reykjavík - Sími 422 50 00 | Fax 422 50 01 - <a href=\"mailto:matis@matis.is\">matis@matis.is</a><br><a href=\"http://www.matis.is/ISGEM/is/skyringar/\">Hjálp</a> - "
		 * + ((sessionKey != null && sessionKey.length() > 1) ?
		 * "<a href=\"http://test.matis.is/isgem\">Allur glugginn</a>" :
		 * "<a href=\"http://apps.facebook.com/matisgem\">Facebook</a>") //
		 * +" - <a href=\"dark\">Dark</a> - <a href=\"light\">Light</a>" +
		 * "</td></tr></table></center></body></html>"); Dimension d = new
		 * Dimension(1000, 42); ed.setPreferredSize(d); ed.setSize(d);
		 * ed.addHyperlinkListener(new HyperlinkListener() {
		 * 
		 * public void hyperlinkUpdate(HyperlinkEvent e) { if (e.getEventType()
		 * == HyperlinkEvent.EventType.ACTIVATED) { if
		 * (e.getDescription().equals("dark")) { lof =
		 * "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel";
		 * updateLof(); } else if (e.getDescription().equals("light")) { lof =
		 * "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; updateLof(); }
		 * else { try { Desktop.getDesktop().browse(e.getURL().toURI()); } catch
		 * (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } catch (URISyntaxException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); } } } } });
		 */
		
		final JPopupMenu	leftpopup = new JPopupMenu();
		leftpopup.add( new AbstractAction( lang.startsWith("IS") ? "Senda fæðutegund í valda uppskrift" : "Put food in selected recipe" ) {
			public void actionPerformed(ActionEvent e) {
				String str = "";
				
				for( int rr : leftTable.getSelectedRows() ) {
					Object obj = leftTable.getValueAt( rr, 0 );
					
					str += (String)obj+"\n";
				}
				
				try {
					recipe.insertRepInfo(str);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		String[] sortset = lang.startsWith("IS") ? 	new String[] {"Alcohol","Dietary fiber","Carbohydrates, total","Protein, total","Water","Ash","Fat, total"} : 
												new String[] {"ALC","FIBTG","CHOCDF","PROCNT","WATER","ASH","FAT"};
		final Set<String>	sortStuff = new HashSet<String>( Arrays.asList( sortset ) );
		final JPopupMenu	mainpopup = new JPopupMenu();
		mainpopup.add( new AbstractAction( lang.startsWith("IS") ? "Raða fæðutegundum með svipuðu innihaldi" : "Sort food items with similar ingredients" ) {
			public void actionPerformed(ActionEvent e) {
				int r = leftTable.getSelectedRow();
				final int mi = leftTable.convertRowIndexToModel(r);
				
				MySorter ms = (MySorter)leftTable.getRowSorter();
				Comparator comp = ms.getComparator( 0 );
				ms.setComparator(0, new Comparator<String>() {
					Object[]	rr = stuff.get(0);
					Object[]	mm = stuff.get(mi+2);
					@Override
					public int compare(String o1, String o2) {
						Integer i1 = foodNameInd.get(o1);
						Integer i2 = foodNameInd.get(o2);
						
						if( i1 == null && i2 == null ) return Integer.MAX_VALUE;
						else if( i1 == null ) return Integer.MAX_VALUE;
						else if( i2 == null ) return Integer.MIN_VALUE;
						
						Object[] r1 = stuff.get(i1+2);
						Object[] r2 = stuff.get(i2+2);
						
						double val1 = 0;
						double val2 = 0;
						int r = 0;
						for( Object o : rr ) {
							if( sortStuff.contains(o) ) {
								Float f = (Float)mm[r];
								Float f1 = (Float)r1[r];
								Float f2 = (Float)r2[r];
								
								if( f != null ) {
									float abs1 = Math.abs( f-(f1 == null ? 0 : f1) );
									float abs2 = Math.abs( f-(f2 == null ? 0 : f2) );
									
									val1 += abs1*abs1;
									val2 += abs2*abs2;
								}
							}
							r++;
						}
						
						return Double.compare( Math.sqrt( val1 )*1000.0, Math.sqrt(val2)*1000.0 );
					}
				});
				ms.setSortKeys( Arrays.asList( new RowSorter.SortKey[] { new RowSorter.SortKey(0,SortOrder.ASCENDING) } ) );
				ms.sort();
				ms.setComparator(0, comp);
				//leftTable.sorter.sort();
			}
		});
		mainpopup.add( new AbstractAction("Sýna matartré") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Double>	distmatrix = new ArrayList<Double>();
				
				Object[]	rr = stuff.get(0);
				double[]	sums = new double[ stuff.size()-2 ];
				List<Node>	nodelist = new ArrayList<Node>();
				for( int i = 0; i < sums.length; i++ ) {
					nodelist.add( new Node() );
				}
				Arrays.fill(sums, 0.0);
				for( int i = 2; i < stuff.size(); i++ ) {
					for( int k = i+1; k < stuff.size(); k++ ) {
						Object[] r1 = stuff.get(i);
						Object[] r2 = stuff.get(k);
						
						double val = 0;
						int r = 0;
						
						for( Object o : rr ) {
							if( sortStuff.contains(o) ) {
								Float f1 = (Float)r1[r];
								Float f2 = (Float)r2[r];
								
								float fl1 = (f1 == null) ? 0 : f1;
								float fl2 = (f2 == null) ? 0 : f2;
								
								float abs = Math.abs( fl1-fl2 );
								val += abs*abs;
							}
						}
						double dist = Math.sqrt( val );
						sums[i-2] += dist;
						int v = k-2-stuff.size()+i+1;
						if( v >= 0 ) {
							sums[sums.length-(i-2)-1-v] += dist;
						}
						
						distmatrix.add( dist );
					}
				}
				double[]	qmatrix = new double[ distmatrix.size() ];
				for( int i = 0; i < qmatrix.length; i++ ) {
					int y = i/(sums.length-1);
					qmatrix[i] = (sums.length-2)*distmatrix.get(i) - sums[i] - sums[i+1];
					
				}
			}
		});
		
		tabbedPane.addChangeListener(new ChangeListener() {
			Component previous;
				
			public void stateChanged(ChangeEvent e) {
				Component	comp = tabbedPane.getSelectedComponent();
				//if( tabbedPane.getTitleAt( tabbedPane.getSelectedIndex() ).equals("Uppskriftir") ) {
				
				if ( comp.equals(rightSplitPane) ) {
					leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
					leftTable.setComponentPopupMenu( mainpopup );
					// leftSplitPane.setDividerLocation(60);
				} else {
					if( comp.equals(recipe) ) {
						leftTable.setComponentPopupMenu( leftpopup );
						try {
							recipe.loadRecipes();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else if( comp.equals(graph) && eat.equals(previous) ) {
						topLeftCombo.setSelectedIndex(0);
						int rc = leftTable.getRowCount();
						leftTable.setRowSelectionIntervalSuper(rc-1, rc-1);
						leftTable.scrollRectToVisible( leftTable.getCellRect(rc-1, 0, false) );
					} else {
						leftTable.setComponentPopupMenu( null );
					}
					leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					// leftSplitPane.setDividerLocation(0);
				}
				
				previous = comp;
			}
		});
		
		JPopupMenu		popup = new JPopupMenu();
		InputStream is = this.getClass().getResourceAsStream("/xlsx.png");
		if( is != null ) {
			//ImageProducer 	ims = (ImageProducer)xurl.getContent();
			BufferedImage bimg = ImageIO.read(is);
			Image 		img = bimg.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			popup.add( new AbstractAction( lang.startsWith("IS") ? "Opna val í Excel" : "Open in Excel", img != null ? new ImageIcon(img) : null ) {
				public void actionPerformed(ActionEvent ae) {
					try {
						PoiFactory.export(table, topTable, leftTable, SortTable.this);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			});
			popup.add( new AbstractAction( lang.startsWith("IS") ? "Opna tré" : "Open tree", img != null ? new ImageIcon(img) : null ) {
				public void actionPerformed(ActionEvent ae) {
					JCheckBox message = new JCheckBox("Sleppa ókláruðum matvælum");
					JOptionPane.showMessageDialog(SortTable.this, message);
					
					List<Integer> lint = new ArrayList<Integer>();
					int colc = table.getColumnCount();
					for( int i = 0; i < colc; i++ ) {
						int val = table.convertColumnIndexToModel(i);
						if( val >= 2 ) lint.add( val+2 );
					}
					
					TreeUtil tu = new TreeUtil();
					List<String> corrInd = new ArrayList<String>();					
					List<Integer> validIndices = new ArrayList<Integer>();
					for( int i = 2; i < stuff.size(); i++ ) {
						Object[] objs = stuff.get(i);
						boolean valid = true;
						for( int u = 0; u < lint.size(); u++ ) {
							Object obj = objs[ lint.get(u) ];
							if( message.isSelected() && obj == null ) {
								valid = false;
								break;
							}
						}
						if( valid ) {
							validIndices.add( i );
							corrInd.add( ((String)objs[0]).replace(",", "") );
						}
					}
					int len = validIndices.size();
					double[] corrarr = new double[ len*len ];
					Arrays.fill(corrarr, 0.0);
						
					for( int i = 0; i < validIndices.size(); i++ ) {
						Object[] obj = stuff.get( validIndices.get(i) );
						for( int k = i+1; k < validIndices.size(); k++ ) {
							Object[] obj2 = stuff.get( validIndices.get(k) );
							
							double tot = 0.0;
							for( int y = 0; y < lint.size(); y++ ) {
								int u = lint.get(y);
								Float f1 = (Float)obj[u];
								Float f2 = (Float)obj2[u];
								
								if( message.isSelected() ) {
									double val = f1-f2;
									tot += val*val;
								} else {
									double val;
									
									if( f1 != null ) {
										if( f2 != null ) val = f1-f2;
										else val = f1;
									} else {
										if( f2 != null ) val = f2;
										else val = 0.0;
									}
									
									tot += val*val;
								}
							}
							tot = Math.sqrt( tot );
							corrarr[ (i)*len+(k) ] = tot;
							corrarr[ (k)*len+(i) ] = tot;
						}
					}
					TreeUtil.Node node = tu.neighborJoin(corrarr, corrInd, null, true, false);
					String tree = node.toString();
					System.err.println("about to call showTree");
					System.err.println( tree );
					JSUtil.call( SortTable.this, "showTree",  new Object[] {tree} );
				}
			});
		}
		table.setComponentPopupMenu( popup );	
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		// SortTable.this.add(ed, BorderLayout.SOUTH);
		splitPane.setDividerLocation(1.0 / 3.0);
		splitPane.setDividerLocation(300);
		// this.add( panel, BorderLayout.SOUTH );
		// this.add( field, BorderLayout.SOUTH );

		// splitPane.setBackground( Color.white );
		// tabbedPane.setBackground( Color.white );

		// scrollPane.setColumnHeaderView( topTable );
		// topScrollPane.setViewport( scrollPane.getColumnHeader() );

		// SwingUtilities.updateComponentTreeUI( this );
	}
	
	public String getAppletInfo() {
		return "SortTable";
	}
	
	public JTable[] getThreeTables() {
		return new JTable[] { table, topTable, leftTable };
	}
	
	public String applet;
	public void openExcel() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//this.getCodeBase();
		
		if( applet == null ) {
			//String js = "document.getElementById('applet').innerHTML = '<applet codebase=\"http://localhost/\" code=\"org.simmi.PoiFactory\" id=\"minunsign\" name=\"minunsign\"><param name=\"jnlp_href\" value=\"export.jnlp\"></applet>'"; 
				//"var attributes = { codebase:'http://localhost/', archive:'minapplet.jar', code:'org.simmi.PoiFactory', width:'1', height:'1', id:'food', name:'minunsign' }; var parameters = { jnlp_href:'minapplet.jnlp' }; deployJava.runApplet(attributes, parameters, '1.6');";
				//System.err.println( js );
			
			/*String 	js = "var attributes = { codebase:\\'http://localhost/\\', archive:\\'minapplet.jar\\', code:\\'org.simmi.PoiFactory\\', width:\\'10\\', height:\\'10\\', id:\\'poi\\', name:\\'export\\' }; ";
					js += "var parameters = { jnlp_href:\\'export.jnlp\\' }; ";
					js += "deployJava.runApplet(attributes, parameters, \\'1.6\\')";
			
			System.err.println("mo5");
			/*Applet app = this.getAppletContext().getApplet("panel1");
			Enumeration<Applet> enappl = this.getAppletContext().getApplets();
			while( enappl.hasMoreElements() ) {
				Applet ap = enappl.nextElement();
				System.err.println( ap.getName() );
			}*
			
			JSObject win = JSObject.getWindow(this);
			//JSObject doc = (JSObject)win.getMember("document");
			
			//doc.eval("var appi = document.getElementById('applet'); var scrippi = document.createElement('script'); scrippi.appendChild( document.createTextNode('"+js+"') ); appi.appendChild( scrippi );");
			win.call( "emmi", new Object[] {"export"} );
			
			/*JSObject app = (JSObject)doc.call("getElementById", new Object[] {"applet"});
			//doc.
			
			JSObject script = (JSObject)doc.call("createElement", new Object[] {"script"} );
			script.setMember("src", "dummy.js");
			app.call("appendChild", new Object[] {script} );*/
			
			//JSObject win = JSObject.getWindow(this);
			//JSObject doc = (JSObject)win.getMember("document");
			
			//app.eval( js );
			//win.call("updateApplet", new Object[]{} );
			
			/*try {
			      getAppletContext().showDocument( new URL("http://localhost/simple.html"), "simmi" );
			      //getAppletContext().showDocument(url, target)
			} catch (MalformedURLException me) { }*
			//applet = getAppletContext().getApplet("minunsign");
			applet = "simmi";
			
			*/
		} else { //if( applet != null ) {			
			//String js = "document.getElementById('minunsign').notifyAll();";
			//JSObject win = JSObject.getWindow(this);
			//JSObject doc = (JSObject)win.getMember("document");
			//win.eval( js );
		
			Enumeration<Applet> appen = this.getAppletContext().getApplets();
			while( appen.hasMoreElements() ) {
				Applet ap = appen.nextElement();
				
				try {
					Method m = ap.getClass().getMethod("runpriv", JTable.class, JTable.class, JTable.class);
					m.invoke( ap, table, topTable, leftTable );
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
			
			//if( applet instanceof PoiFactory ) {
			//	System.err.println("me2");
				/*for( Method m : applet.getClass().getMethods() ) {
					System.err.println( m.getName() );
				}
				Method m = applet.getClass().getMethod("run", JTable.class, JTable.class, JTable.class);
				if( m != null ) {
					m.invoke( applet, table, topTable, leftTable );
				}*/
				
				//.run( table, topTable, leftTable );
			//}
		}/* else {
			JOptionPane.showMessageDialog(this, "Hef ekki leyfi til að skifa á diskinn");
		}*/
		
		//PoiFactory.run( table, topTable, leftTable );
	}
	
	public void openExcel( JCompatTable detailTable, JCompatTable	leftTable ) throws IOException {
		//Applet applet = getAppletContext().getApplet("minunsign");
		//if( applet != null ) {
		//	if( applet instanceof PoiFactory ) {
		//		((PoiFactory)applet).run2( detailTable, leftTable );
		//	}
		//}
		
		PoiFactory.run2( detailTable, leftTable );
	}

	public String updateFriends(final String sessionKey, final String currentUser) {
		System.err.println("try friends update");
		if (friendsPanel != null) {
			friendsPanel.updateFriends( sessionKey, currentUser );
		}
		return "";
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	/*
	 * public JEditorPane getEditor() { return ed; }
	 */

	public ImagePanel getImagePanel() {
		return imgPanel;
	}
	
	public void setImage( BufferedImage image ) {
		ImagePanel imgPanel = getImagePanel();
		imgPanel.setImage( image );
		imgPanel.repaint();
	}
	
	public void setByteArrayImage( byte[] bb ) throws IOException {
		System.err.println("bull "+bb.length);
		ByteArrayInputStream bais = new ByteArrayInputStream( bb );
		BufferedImage img = ImageIO.read( bais );
		setImage( img );
	}
	
	public void setStrImage( String strdata ) throws IOException {
		System.err.println( strdata );
		setByteArrayImage( strdata.getBytes() );
	}
	
	public void setJSImage( Object jsimg ) {
		System.err.println("ok "+jsimg.toString().substring(0, 50));
	}
	
	public void setImageURL( String urlstr ) throws UnsupportedEncodingException, IOException {
		ImagePanel imgPanel = getImagePanel();
		String newurl = this.getCodeBase().toString()+"imgproxy.php?url="+URLEncoder.encode(urlstr,"UTF8");
		System.err.println( "the new url " + newurl );
		URL url = new URL( newurl );
		imgPanel.setURL( url );
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/*
		 * URL url; try { url = new URL("http://www.google.com"); //InputStream
		 * stream = url.openStream(); Proxy proxy = new Proxy( Type.HTTP, new
		 * InetSocketAddress("proxy.decode.is",8080) ); URLConnection connection
		 * = url.openConnection( proxy ); InputStream stream =
		 * connection.getInputStream(); } catch (MalformedURLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

		final SortTable sortTable = new SortTable();

		CompatUtilities.updateLof();
		System.setProperty("file.encoding", "UTF8");

		/*
		 * ToolTipManager.sharedInstance().setInitialDelay(0); try {
		 * sortTable.stuff = sortTable.parseData(sortTable.lang); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		sortTable.loadStuff();
		sortTable.frame();
		
		/*SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				sortTable.frame(); 
			}
		});*/
	}

	public void frame() {
		JFrame frame = new JFrame("Matisgem");
		//frame.setUndecorated( true );
		frame.setSize(800, 600);
		try {
			frame.setBackground( bgcolor );
			frame.getContentPane().setBackground( bgcolor );
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.initGui(null, null);
			
			KeyListener keylistener = initFSKeyListener( frame );
			table.addKeyListener( keylistener );
			
			//frame.getContentPane().setLayout(new BorderLayout());
			frame.add( this.splitPane );
			//frame.getContentPane().add( new JButton("simmi"), BorderLayout.CENTER );
			//frame.pack();
			//frame.setContentPane( panel );
		} catch(Exception e) {
			e.printStackTrace();
		}
		// frame.add(sortTable.getEditor(), BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	public JTextField getSearchField() {
		return field;
	}

	public static String detectEncoding(InputStream in) throws IOException {
		String encoding = null;
		in.mark(400);
		int ignoreBytes = 0;
		boolean readEncoding = false;
		byte[] buffer = new byte[400];
		int read = in.read(buffer, 0, 4);
		switch (buffer[0]) {
		case (byte) 0x00:
			if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0xFE && buffer[3] == (byte) 0xFF) {
				ignoreBytes = 4;
				encoding = "UTF_32BE";
			} else if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x3C) {
				encoding = "UTF_32BE";
				readEncoding = true;
			} else if (buffer[1] == (byte) 0x3C && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x3F) {
				encoding = "UnicodeBigUnmarked";
				readEncoding = true;
			}
			break;
		case (byte) 0xFF:
			if (buffer[1] == (byte) 0xFE && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x00) {
				ignoreBytes = 4;
				encoding = "UTF_32LE";
			} else if (buffer[1] == (byte) 0xFE) {
				ignoreBytes = 2;
				encoding = "UnicodeLittleUnmarked";
			}
			break;

		case (byte) 0x3C:
			readEncoding = true;
			if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x00) {
				encoding = "UTF_32LE";
			} else if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x3F && buffer[3] == (byte) 0x00) {
				encoding = "UnicodeLittleUnmarked";
			} else if (buffer[1] == (byte) 0x3F && buffer[2] == (byte) 0x78 && buffer[3] == (byte) 0x6D) {
				encoding = "ASCII";
			}
			break;
		case (byte) 0xFE:
			if (buffer[1] == (byte) 0xFF) {
				encoding = "UnicodeBigUnmarked";
				ignoreBytes = 2;
			}
			break;
		case (byte) 0xEF:
			if (buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
				encoding = "UTF8";
				ignoreBytes = 3;
			}
			break;
		case (byte) 0x4C:
			if (buffer[1] == (byte) 0x6F && buffer[2] == (byte) 0xA7 && buffer[3] == (byte) 0x94) {
				encoding = "CP037";
			}
			break;
		}
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		if (readEncoding) {
			read = in.read(buffer, 4, buffer.length - 4);
			Charset cs = Charset.forName(encoding);
			String s = CompatUtilities.getCharsetString(buffer, 4, read, cs);
			int pos = s.indexOf("encoding");
			if (pos == -1) {
				encoding = System.getProperty("file.encoding");
			} else {
				char delim;
				int start = s.indexOf(delim = '\'', pos);
				if (start == -1)
					start = s.indexOf(delim = '"', pos);
				// if (start == -1)
				// notifyEncodingError(buffer);
				int end = s.indexOf(delim, start + 1);
				// if (end == -1)
				// notifyEncodingError(buffer);
				encoding = s.substring(start + 1, end);
			}
		}

		// in.reset();
		// while (ignoreBytes-- > 0)
		// in.read();
		return encoding;
	}

	public void sortByColumn(String str) {
		DefaultRowSorter sorter = ((DefaultRowSorter)table.getRowSorter());
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		int c = 0;
		System.err.println(str);
		while (c < model.getColumnCount() && !model.getColumnName(c++).contains(str))
			;
		if (c < model.getColumnCount()) {
			RowSorter.SortKey sortKey = new RowSorter.SortKey(c, SortOrder.DESCENDING);
			sortKeys.add(sortKey);
			sorter.setSortKeys(sortKeys);
		}
	}
	
	public boolean jnlpShowDocument( URL url ) {
		/*try {
           BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");  
           return bs.showDocument(url); 
       } catch(UnavailableServiceException ue) {  
           return false; 
       }*/
		
		return false;
	}
}
