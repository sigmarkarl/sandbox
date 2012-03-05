package org.simmi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultRowSorter;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.TreeUtil;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class DataTable extends JApplet implements ClipboardOwner {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static void updateLof() {
		try {
			UIManager.setLookAndFeel(lof);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public void updateTable( String tabmap ) {
		try {
			JSONObject jsono = new JSONObject( tabmap );
			Iterator<String> keys = jsono.keys();
			while( keys.hasNext() ) {
				String key = keys.next();
				if( tablemap.containsKey(key) ) {
					Object[] strs = tablemap.get( key );
					JSONObject jo = jsono.getJSONObject(key);
					strs[11] = jo.getString("country");
					String vb = (String)jo.getString("valid");
					if( vb != null ) strs[13] = Boolean.parseBoolean( vb );
				}
			}
			table.tableChanged( new TableModelEvent(table.getModel()) );
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String makeCopyString() {
		StringBuilder sb = new StringBuilder();
            
        int[] rr = table.getSelectedRows();
        int[] cc = table.getSelectedColumns();
        
        System.err.println( rr );
        System.err.println( cc );
        
        for( int ii : rr ) {
            for( int jj = 0; jj < table.getColumnCount(); jj++ ) {
            	Object val = table.getValueAt(ii,jj);
                //if( val != null && val instanceof Float ) sb.append( "\t"+Float.toString( (Float)val ) );
                //else sb.append( "\t" );
            	
            	if( jj == 0 ) sb.append( val.toString() );
            	else sb.append( "\t"+val.toString() );
            }
            sb.append( "\n" );
        }
        return sb.toString();
	}
	
	public void copyData(Component source) {
        TableModel model = table.getModel();
 
        String s = makeCopyString();
        if (s==null || s.trim().length()==0) {
            JOptionPane.showMessageDialog(this, "There is no data selected!");
        } else {
        	if( clipboardService != null ) {
        		StringSelection selection = new StringSelection(s);
            	clipboardService.setContents( selection );
        	} else {
        		StringSelection stringSelection = new StringSelection( s );
        		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	    clipboard.setContents( stringSelection, this );
        	}
        }
        
        if (grabFocus) {
            source.requestFocus();
        }
    }
	 
    class CopyAction extends AbstractAction {
        public CopyAction(String text) {
            super(text);
            //putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
        }
 
        public void actionPerformed(ActionEvent e) {
            copyData((Component)e.getSource());
        }
    }

    JTable	table;
    Map<String,Object[]>		tablemap;
    private ClipboardService 	clipboardService;
    private boolean			grabFocus = false;
    JCheckBoxMenuItem 			cbmi = new JCheckBoxMenuItem("Collapse tree");
    final Map<String,String>	nameaccmap = new HashMap<String,String>();
    final List<Object[]>		rowList = new ArrayList<Object[]>();
    
    public void loadData( String data ) {
    	String[] lines = data.split("\n");
    	List<String> splitlist = new ArrayList<String>();
    	
    	try {
	    	for( int i = 1; i < lines.length; i++ ) {
	    		splitlist.clear();
	    		
	    		//System.err.println( "uff " + i );
	    		String line = lines[i];
	    		int first = 0;
	    		int last = line.indexOf('"');
	    		while( last != -1 ) {
	    			
	    			if( last > first ) {
	    				String sub = line.substring(first, last-1);
		    		//if( sub.length() > 0 ) {
		    			int uno = 0;
		    			int duo = sub.indexOf(',');
		    			while( duo != -1 ) {
		    				splitlist.add( sub.substring(uno, duo) );
		    				uno = duo+1;
		    				duo = sub.indexOf(',', uno);
		    			}
		    			splitlist.add( sub.substring(uno) );
		    		}
		    		first = last+1;
		    		last = line.indexOf('"', first);
		    		
		    		if( last != -1 ) {
		    			String sub = line.substring(first, last);
		    			splitlist.add( sub );
		    		
		    			first = last+2;
		    			last = line.indexOf('"', first);
		    		}
	    		}
	    		if( first != -1 && first < line.length() ) {
		    		String sub = line.substring(first);
		    		if( sub.length() > 0 ) {
		    			int uno = 0;
		    			int duo = sub.indexOf(',');
		    			while( duo != -1 ) {
		    				splitlist.add( sub.substring(uno, duo) );
		    				uno = duo+1;
		    				duo = sub.indexOf(',', uno);
		    			}
		    			splitlist.add( sub.substring(uno) );
		    		}
	    		} else System.err.println("first is not");
	    		String[] split = splitlist.toArray( new String[0] ); //lines[i].split(",");
	    		
	    		if( split.length > 8 ) {
		    		nameaccmap.put(split[0], split[1]);
					Object[] strs = new Object[ 14 ];
					
					int k = 0;
					for( k = 0; k < split.length; k++ ) {
						/*if( k < 3 ) {
							strs[k] = split[(k+2)%3];
						} else */
						if( k == 3 || k == 4 ) {
							try {
								strs[k] = Integer.parseInt( split[k] );
							} catch( Exception e ) {
								e.printStackTrace();
							}
						}
						else if( k == 13 ) strs[k] = (split[k] != null && (split[k].equalsIgnoreCase("true") || split[k].equalsIgnoreCase("false")) ? Boolean.parseBoolean( split[k] ) : true);
						else strs[k] = split[k];
					}
					
					if( k == 8 ) strs[k++] = "";
					if( k == 9 ) strs[k++] = "";
					if( k == 10 ) strs[k++] = "";
					if( k == 11 ) strs[k++] = "";
					if( k == 12 ) strs[k++] = "";
					strs[k] = true;
					
					//Arrays.copyOfRange(split, 1, split.length );
					rowList.add( strs );
					tablemap.put((String)strs[2], strs);
	    		} else {
	    			System.err.println("ermimeri " + split.length );
	    		}
	    	}
	    	table.tableChanged( new TableModelEvent( table.getModel() ) );
    	} catch( Exception e ) {
    		e.printStackTrace();
    	}
    }
    
    private GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	private final String email = "huldaeggerts@gmail.com";
	private final String password = "b.r3a1h1ms";
	private final String tableid = "1QbELXQViIAszNyg_2NHOO9XcnN_kvaG1TLedqDc";
	
	public String getThermusFusion() {
		//System.setProperty(GoogleGDataRequest.DISABLE_COOKIE_HANDLER_PROPERTY, "true");
		if( service == null ) {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			try {
				service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}
		}
		
		if( service != null ) {
			try {
				String ret = run("select * from "+tableid, true);
				return ret;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public String run(String query, boolean isUsingEncId) throws IOException, ServiceException {
		   String lowercaseQuery = query.toLowerCase();
		   String encodedQuery = URLEncoder.encode(query, "UTF-8");

		   GDataRequest request;
		   // If the query is a select, describe, or show query, run a GET request.
		   if (lowercaseQuery.startsWith("select") ||
		       lowercaseQuery.startsWith("describe") ||
		       lowercaseQuery.startsWith("show")) {
		     URL url = new URL(SERVICE_URL + "?sql=" + encodedQuery + "&encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.QUERY, url,
		         ContentType.TEXT_PLAIN);
		   } else {
		     // Otherwise, run a POST request.
		     URL url = new URL(SERVICE_URL + "?encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.INSERT, url,
		         new ContentType("application/x-www-form-urlencoded"));
		     OutputStreamWriter writer = new OutputStreamWriter(request.getRequestStream());
		     writer.append("sql=" + encodedQuery);
		     writer.flush();
		   }

		   request.execute();

		   return getResultsText(request);
	}
	
	private String getResultsText(GDataRequest request) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(request.getResponseStream());
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb.toString();
	}
	
	public static void updateFilter(JTable table, RowFilter filter) {
		DefaultRowSorter<TableModel, Integer> rowsorter = (DefaultRowSorter<TableModel, Integer>)table.getRowSorter();
		rowsorter.setRowFilter(filter);
	}
    
	public void init() {
		updateLof();
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		//table.setColumnSelectionAllowed( true );
		JScrollPane	scrollpane = new JScrollPane( table );
		tablemap = new HashMap<String,Object[]>();
		
		/*InputStream is = this.getClass().getResourceAsStream( "/therm3.txt" );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		try {
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				
				nameaccmap.put(split[0], split[1]);
				Object[] strs = new Object[ 14 ];
				int i;
				for( i = 0; i < split.length; i++ ) {
					if( i == 3 || i == 4 ) strs[i] = Integer.parseInt( split[i] );
					else strs[i] = split[i];
				}
				if( i == 8 ) strs[i++] = "";
				if( i == 9 ) strs[i++] = "";
				if( i == 10 ) strs[i++] = "";
				if( i == 11 ) strs[i++] = "";
				if( i == 12 ) strs[i++] = "";
				//if( i == 13 ) strs[i++] = "";
				strs[i] = true;
				//Arrays.copyOfRange(split, 1, split.length );
				rowList.add( strs );
				tablemap.put((String)strs[1t], strs);
				
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return rowList.size();
			}

			@Override
			public int getColumnCount() {
				return 14;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "name";
				else if( columnIndex == 1 ) return "acc";
				else if( columnIndex == 2 ) return "species";
				else if( columnIndex == 3 ) return "len";
				else if( columnIndex == 4 ) return "ident";
				else if( columnIndex == 5 ) return "doi";
				else if( columnIndex == 6 ) return "pubmed";
				else if( columnIndex == 7 ) return "journal";
				else if( columnIndex == 8 ) return "auth";
				else if( columnIndex == 9 ) return "sub_auth";
				else if( columnIndex == 10 ) return "sub_date";
				else if( columnIndex == 11 ) return "country";
				else if( columnIndex == 12 ) return "source";
				else if( columnIndex == 13 ) return "valid";
				//else if( columnIndex == 13 ) return "color";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 3 || columnIndex == 4 ) return Integer.class;
				else if( columnIndex == 13 ) return Boolean.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 11 || columnIndex == 13 ) return true;
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = "";
				
				Object[] val = rowList.get(rowIndex);
				if( columnIndex < val.length ) {
					//if( columnIndex == 2 || columnIndex ==3 ) return Integer.parseInt( val[columnIndex] );
					ret = val[columnIndex];
				}
				if( ret instanceof Integer ) System.err.println( columnIndex );
				return ret;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				//int r = table.convertRowIndexToModel( rowIndex );
				Object[] row = rowList.get(rowIndex);
				row[columnIndex] = aValue;
				
				JSObject jso = JSObject.getWindow( DataTable.this );
				jso.call( "saveMeta", new Object[] {row[1], row[11], row[13]} );
			}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		
		table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		table.getColumnModel().getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		//final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
		final DataFlavor df;
		DataFlavor dflocal = null;
		try {
			dflocal = new DataFlavor("text/plain;charset=utf-8");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} finally {
			df = dflocal;
		}
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
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus.ntree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/RAxML_10993.ntree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/testskrimsli.phb");
				InputStream is = DataTable.this.getClass().getResourceAsStream("/lmin900idmin95phyml.tree");	
				
				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while( line != null ) {
					sb.append( line );
					line = br.readLine();
				}
				br.close();
				
				Map<String,String>	colormap = new HashMap<String,String>();
				
				String[] ss = new String[] {"unknown", "kawarayensis", "scotoductus", "thermophilus", "eggertsoni", "islandicus", "igniterrae", "brockianus", "aquaticus", "oshimai", "filiformis", "antranikianii"};
				Set<String> collapset = new HashSet<String>( Arrays.asList( ss ) );
				
				String[] cc = new String[] {"USA", "Yellowstone", "Hawaii", "Tibet", "Taiwan", "Italy", "Bulgaria", "Hungary", "Iceland", "Portugal", "China", "Japan", "Australia", "New Zealand", "Chile", "Antarctica", "Puerto Rico", "Greece", "Switzerland", "Russia", "India", "Indonesia"};
				Set<String> countryset = new HashSet<String>( Arrays.asList( cc ) );
				
				Random rnd = new Random();
				for( String c : cc ) {
					//String cstr = "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )";
					String cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
					colormap.put( c, cstr );
				}
				for( String s : ss ) {
					//colormap.put( s, "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )" );
					String cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
					colormap.put( s, cstr );
					for( String c : cc ) {
						//colormap.put( s+"-"+c, "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )" );
						cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
						colormap.put( s+"-"+c, cstr );
					}
				}
				
				Map<String,Map<String,String>> mapmap = new HashMap<String,Map<String,String>>();
				Set<String>	include = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String name = (String)table.getValueAt(r, 0);
					String acc = (String)table.getValueAt(r, 1);
					include.add( name );
					include.add( acc );
					
					Map<String,String>	map = new HashMap<String,String>();
					String nm = (String)table.getValueAt(r, 2);
					int id = (Integer)table.getValueAt(r, 4);
					
					if( id >= 97 ) {
						if( nm.contains("t.eggertsoni") ) nm = "Thermus eggertsoni";
						else if( nm.contains("t.islandicus") ) nm = "Thermus islandicus";
						else if( nm.contains("t.kawarayensis") ) nm = "Thermus kawarayensis";
						else {
							int ix = nm.indexOf(' ');
							if( ix > 0 ) {
								nm = nm.substring(0, nm.indexOf(' ', ix+1) );
							}
						}
					} else {
						nm = "Thermus unknown";
					}
					
					map.put("name", nm);
					String country = (String)table.getValueAt(r, 11);
					//String acc = (String)table.getValueAt(r, 1);
					if( country != null && country.length() > 0 ) {
						map.put( "country", country );
					}
					map.put( "acc", acc );
					map.put("id", Integer.toString(id));
					//mapmap.put(acc, map);
					mapmap.put(name, map);
				}
				
				TreeUtil tu = new TreeUtil( sb.toString(), false, include, mapmap, cbmi.isSelected(), collapset, colormap, true );
				//return arg0.getReaderForText( this );
				String str = tu.currentNode.toString();
				return new ByteArrayInputStream( str.getBytes( charset ) );
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
		
		try {
	    	clipboardService = (ClipboardService)ServiceManager.lookup("javax.jnlp.ClipboardService");
            //table.getActionMap().put( "copy", action );
            grabFocus = true;
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
	    }
		/*table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				if( me.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					int i = table.convertRowIndexToModel(r);
					if( i != -1 ) {
						String[] str = rowList.get( i );
						String doi = str[4];
						try {
							URL url = new URL( "http://dx.doi.org/"+doi );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});*/
		
		final Set<Integer>	filterset = new HashSet<Integer>();
		final RowFilter 	filter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return filterset.isEmpty() || filterset.contains(entry.getIdentifier());
			}
		};
		updateFilter(table, filter);
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("SQL") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				final JTextArea	textarea = new JTextArea();
				
				JButton	exc = new JButton( new AbstractAction("Execute") {
					@Override
					public void actionPerformed(ActionEvent e) {
						String sql = textarea.getSelectedText();
						if( sql == null ) sql = textarea.getText();
						if( sql != null ) {
							sql = sql.replace("table", tableid);
							try {
								if( sql.startsWith("update") && sql.contains(" in ") ) {
									int start = sql.indexOf('(')+1;
									int stop = sql.indexOf(')', start);
									
									String innersql = sql.substring(start, stop);
									String result = run( innersql, true );
									String[] split = result.split("\n");
									
									int sw = sql.indexOf("where");
									String subsql = sql.substring(0, sw);
									for( int i = 1; i < split.length; i++ ) {
										String rowid = split[i];
										System.err.println("about to run "+rowid);
										run( subsql+"where rowid = '"+rowid+"'", true );
									}
								} else {
									run( sql, true );
								}
							} catch (IOException | ServiceException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				JButton	cls = new JButton( new AbstractAction("Close") {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				
				JComponent comp = new JComponent() {};
				comp.setLayout( new FlowLayout() );
				comp.add( exc );
				comp.add( cls );
				
				dialog.setLayout( new BorderLayout() );
				JScrollPane	scrollpane = new JScrollPane( textarea );
				dialog.add( scrollpane );
				dialog.add( comp, BorderLayout.SOUTH );
				
				dialog.setVisible( true );
			}
		});
		popup.add(new AbstractAction("Crop to selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int mr = table.convertRowIndexToModel(r);
					filterset.add(mr);
				}
				updateFilter(table, filter);
			}
		});
		Action action = new CopyAction( "Copy" );
		popup.add( action );
		popup.addSeparator();
		popup.add( new AbstractAction("Show fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				JTextArea textarea = new JTextArea();
				textarea.setDragEnabled(true);
				Set<String>	include = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					if( i != -1 ) {
						Object[] val = rowList.get(i);
						include.add( (String)val[0] );
					}
				}
				StringBuilder sb = new StringBuilder();
				InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus_all_gaps.fasta");
				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
				try {
					boolean inc = false;
					String line = br.readLine();
					while( line != null ) {
						if( line.startsWith(">") ) {
							int v = line.indexOf(' ');
							String name = line.substring(1, v).trim();
							String acc = nameaccmap.get(name);
							if( include.contains(name) ) {
								inc = true;
								sb.append( line+"\n" );
							} else inc = false;
						} else if( inc ) {
							if( line.length() > 100 ) {
								for( int i = 0; i < line.length(); i+= 70 ) {
									sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
								}
							} else sb.append( line+"\n" );
						}
						line = br.readLine();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				textarea.setText( sb.toString() );
				
				JScrollPane	scrollpane = new JScrollPane( textarea );
				dialog.add( scrollpane );
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				dialog.setVisible( true );
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Show article") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				if( i != -1 ) {
					Object[] str = rowList.get( i );
					String doi = (String)str[5];
					if( doi != null && doi.length() > 0 ) {
						try {
							URL url = new URL( "http://dx.doi.org/"+doi );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					} else {
						String pubmed = (String)str[6];
						try {
							URL url = new URL( "http://www.ncbi.nlm.nih.gov/pubmed/?term="+pubmed );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Article in new window") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				if( i != -1 ) {
					try {
						Object[] str = rowList.get( i );
						String doi = (String)str[5];
						if( doi != null && doi.length() > 0 ) {
						
							URL url = new URL( "http://dx.doi.org/"+doi );
							Desktop.getDesktop().browse( url.toURI() );
							//URL url = new URL( "http://dx.doi.org/"+doi );
							//DataTable.this.getAppletContext().showDocument( url );
						} else {
							String pubmed = (String)str[6];
							try {
								URL url = new URL( "http://www.ncbi.nlm.nih.gov/pubmed/?term="+pubmed );
								Desktop.getDesktop().browse( url.toURI() );
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							}
						}
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Select marked") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.removeRowSelectionInterval(0, table.getRowCount()-1);
				for( int r = 0; r < table.getRowCount(); r++ ) {
					boolean b = (Boolean)table.getValueAt(r, 11);
					if( b ) table.setRowSelectionInterval(r, r);
				}
			}
		});
		popup.addSeparator();
		popup.add( cbmi );
		
		table.setComponentPopupMenu( popup );
		
		String res = getThermusFusion();
		loadData( res );
		
		try {
			JSObject win = JSObject.getWindow(this);
			System.err.println( "about to run loadData" );
			win.call("loadData", new Object[] {});
			//System.err.println( "done loadData" );
			//win.call("loadMeta", new Object[] {});
		} catch( Exception e ) {
			
		}
		
		this.add( scrollpane );
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
