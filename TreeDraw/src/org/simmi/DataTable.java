package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
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

import javax.jnlp.ClipboardService;
import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultRowSorter;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import netscape.javascript.JSObject;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.Sequence;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.unsigned.JavaFasta;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class DataTable extends JApplet implements ClipboardOwner {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	Map<String,Sequence>	seqcache = new HashMap<String,Sequence>();
	
	public void updateTable( String tabmap ) {
		try {
			JSONObject jsono = new JSONObject( tabmap );
			Iterator<String> keys = jsono.keys();
			while( keys.hasNext() ) {
				String key = keys.next();
				if( tablemap.containsKey(key) ) {
					Object[] strs = tablemap.get( key );
					JSONObject jo = jsono.getJSONObject(key);
					strs[6] = jo.getString("country");
					String vb = (String)jo.getString("valid");
					if( vb != null ) strs[20] = Boolean.parseBoolean( vb );
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
        
        for( int ii : rr ) {
            for( int jj = 0; jj < table.getColumnCount(); jj++ ) {
            	Object val = table.getValueAt(ii,jj);
                //if( val != null && val instanceof Float ) sb.append( "\t"+Float.toString( (Float)val ) );
                //else sb.append( "\t" );
            	if( jj == 0 ) sb.append( val.toString() );
            	else {
            		if( val == null ) sb.append( "\t" );
            		else sb.append( "\t"+val.toString() );
            	}
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
	
	public void replaceTreeText( String tree ) {
		int seqi = 0;
		for( Sequence seq : currentjavafasta.lseq ) {
			String nm = "";
			String sind = Integer.toString( seqi++ );
			int m = 0;
			while( m < 10-sind.length() ) {
				nm += "0";
				m++;
			}
			nm += sind;
			tree = tree.replace( nm, seq.getName() );
		}
		JSObject win = JSObject.getWindow( DataTable.this );
		win.call( "showTree", new Object[] { tree } );
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

    JTable						table;
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
					Object[] strs = new Object[ 22 ];
					
					int k = 0;
					for( k = 0; k < split.length; k++ ) {
						/*if( k < 3 ) {
							strs[k] = split[(k+2)%3];
						} else */
						if( k == 4 || k == 5 ) {
							String istr = split[k];
							if( istr != null && istr.length() > 0 ) {
								try {
									strs[k] = Integer.parseInt( istr );
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								strs[k] = null;
							}
						} else if( k == 19 || k == 20 ) {
							String dstr = split[k];
							if( dstr != null && dstr.length() > 0 ) {
								try {
									strs[k] = Double.parseDouble( dstr );
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								strs[k] = null;
							}
						} else if( k == 21 ) {
							strs[k] = (split[k] != null && (split[k].equalsIgnoreCase("true") || split[k].equalsIgnoreCase("false")) ? Boolean.parseBoolean( split[k] ) : true);
						} else {
							strs[k] = split[k];
						}
					}
					
					if( k == 8 ) strs[k++] = "";
					if( k == 9 ) strs[k++] = "";
					if( k == 10 ) strs[k++] = "";
					if( k == 11 ) strs[k++] = "";
					if( k == 12 ) strs[k++] = "";
					if( k == 13 ) strs[k++] = "";
					if( k == 14 ) strs[k++] = "";
					if( k == 15 ) strs[k++] = "";
					if( k == 16 ) strs[k++] = "";
					if( k == 17 ) strs[k++] = "";
					if( k == 18 ) strs[k++] = "";
					if( k == 19 ) strs[k++] = null;
					if( k == 20 ) strs[k++] = null;
					strs[k] = true;
					
					//Arrays.copyOfRange(split, 1, split.length );
					rowList.add( strs );
					tablemap.put((String)strs[1], strs);
	    		} else {
	    			System.err.println("ermimeri " + split.length );
	    		}
	    	}
	    	table.tableChanged( new TableModelEvent( table.getModel() ) );
    	} catch( Exception e ) {
    		e.printStackTrace();
    	}
    }
    
    private static GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	private static final String oldtableid = "1QbELXQViIAszNyg_2NHOO9XcnN_kvaG1TLedqDc";
	private static final String tableid = "1dmyUhlXVEoWHrT-rfAaAHl3vl3lCUvQy3nkuNUw";
	
	public String getThermusFusion() {
		//System.setProperty(GoogleGDataRequest.DISABLE_COOKIE_HANDLER_PROPERTY, "true");
		if( service == null ) {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			/*try {
				service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}*/
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
	
	public static String run(String query, boolean isUsingEncId) throws IOException, ServiceException {
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
	
	private static String getResultsText(GDataRequest request) throws IOException {
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
	
	public void conservedSpecies( JavaFasta jf, boolean variant ) {
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		JCheckBox accession = new JCheckBox("Accession");
		Object[] params = new Object[] {country, source, accession};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				include.add( (String)val[1] );
			}
		}
		
		List<Sequence> contset = new ArrayList<Sequence>();
		Sequence	seq = null;
		int nseq = 0;
		
		Map<String,Collection<Sequence>>	specMap = new HashMap<String,Collection<Sequence>>();
		InputStream is = DataTable.this.getClass().getResourceAsStream("/thermaceae_16S_aligned.fasta");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			String inc = null;
			String line = br.readLine();
			while( line != null ) {
				/*if( line.startsWith(">") ) {
					int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);
					if( include.contains(name) ) {
						Object[] obj = tablemap.get(acc);
						
						inc = true;
						String fname = ">";
						if( accession.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						if( country.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[11];
							else fname += "_"+obj[11];
						} 
						if( source.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						
						if( fname.length() > 1 ) {
							sb.append(">"+fname+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( line.length() > 100 ) {
						for( int i = 0; i < line.length(); i+= 70 ) {
							sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
						}
					} else sb.append( line+"\n" );
				}*/
				
				if( line.startsWith(">") ) {
					if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}
					
					inc = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							inc = str;
							break;
						}
					}
					
					if( inc != null ) {
						Object[] obj = tablemap.get(inc);
						
						String fname = "";
						String spec = (String)obj[3];
						int iv = spec.indexOf('_');
						if( iv == -1 ) {
							iv = spec.indexOf("16S");
						}
						if( iv != -1 ) spec = spec.substring(0, iv).trim();
						if( fname.length() == 0 ) fname += spec;
						else fname += "_"+spec;
						
						if( country.isSelected() ) {
							String cntr = (String)obj[6];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[7];
							else fname += "_"+obj[7];
						}
						if( accession.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						
						String cont;
						if( fname.length() > 1 ) {
							cont = fname;
						} else cont = line.substring(1);
					//if( rr.length == 1 ) cont = line.replace( ">", "" );
					//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = new Sequence( inc, cont, jf.mseq );
						
						Collection<Sequence> specset;
						if( specMap.containsKey( spec ) ) {
							specset = specMap.get( spec );
						} else {
							specset = new HashSet<Sequence>();
							specMap.put( spec, specset );
						}
						specset.add( seq );
					//dna.append( line.replace( ">", ">"+seqs.getName()+"_" )+"\n" );
						nseq++;
					}
				} else if( inc != null ) {
					seq.append( line.replace(" ", "") );
				}
				line = br.readLine();
			}
			br.close();
			if( inc != null && seq != null ) {
				//Sequence seq = jf.new Sequence(cont, dna);
				contset.add(seq);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Sequence contig : contset) {
			jf.addSequence(contig);
			if (contig.getAnnotations() != null)
				Collections.sort(contig.getAnnotations());
		}
		
		if( variant ) {
			jf.clearConservedSites( specMap );
		} else {
			for( String spec : specMap.keySet() ) {
				Collection<Sequence> specset = specMap.get(spec);
				jf.clearSites( specset, false );
			}
			jf.clearSitesWithGaps( contset );
		}
		
		jf.updateView();
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned ) {
		loadAligned( jf, aligned, null );
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned, Object[] extra ) {
		List<NameSel> namesel = nameSelection( extra );
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String cacheval = (String)val[1];
				if( seqcache.containsKey( cacheval ) ) {
					Sequence seq = seqcache.get( cacheval );
					
					Object[] obj = tablemap.get( seq.id );
					if( obj != null ) {
						String fname = getFastaName( namesel, obj );
						String cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
						cont = cont.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "");
						seq.setName( cont );
					}
					
					jf.addSequence( seq );
				} else include.add( cacheval );
			}
		}
		if( include.size() > 0 ) loadAligned( jf, aligned, include, namesel );
		else if( runnable != null ) {
			runnable.run();
			runnable = null;
		}
	}
	
	class NameSel {
		String 		name;
		Boolean		selected;
		
		public NameSel( String name ) {
			this.name = name;
		}
		
		public boolean isSelected() {
			return selected != null && selected;
		}
	}
	
	public int selectionOfEach() {
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		JSpinner spin = new JSpinner( new SpinnerNumberModel(5, 1, 10, 1) );
		JTable	table = nameSelectionComponent();
		
		comp.add( table );
		comp.add( spin, BorderLayout.NORTH );
		
		JOptionPane.showMessageDialog( DataTable.this, comp, "Select names and number", JOptionPane.PLAIN_MESSAGE );
		
		return (Integer)spin.getValue();
	}
	
	List<NameSel>		names = new ArrayList<NameSel>();
	int[] currentRowSelection;
	public List<NameSel> nameSelection( Object[] extra ) {
		JTable table = nameSelectionComponent();
		if( extra == null ) extra = new Object[] {table};
		else {
			Object[] oldextra = extra;
			extra = new Object[ extra.length+1 ];
			extra[0] = table;
			for( int i = 0; i < oldextra.length; i++ ) {
				extra[i+1] = oldextra[ i ];
			}
		}
		JOptionPane.showMessageDialog( DataTable.this, extra, "Select names", JOptionPane.PLAIN_MESSAGE );
		return names;
	}
	
	public JTable nameSelectionComponent() {
		final JTable table = new JTable();
		table.setDragEnabled( true );
		String[] nlist = {"Species", "Pubmed", "Country", "Source", "Accession"};
		names.clear();
		for( String name : nlist ) {
			names.add( new NameSel( name ) );
		}
		
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<NameSel>	selseq = new ArrayList<NameSel>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( names.get(i) );
						}
						return selseq;
					}
					return null;
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
			
			TransferHandler th = new TransferHandler() {
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
						if( support.isDataFlavorSupported( ndf ) ) {						
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<NameSel>	seqs = (ArrayList<NameSel>)obj;
							
							ArrayList<NameSel> newlist = new ArrayList<NameSel>( names.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( names.get(i) );
							}
							names.clear();
							names = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							names.removeAll( seqs );
							for( NameSel s : seqs ) {
								names.add(k++, s);
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
			table.setTransferHandler( th );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		table.setModel( new TableModel() {
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				NameSel ns = names.get( rowIndex );
				ns.selected = (Boolean)aValue;
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) return true;
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				NameSel ns = names.get(rowIndex);
				if( columnIndex == 0 ) return ns.selected;
				return ns.name;
			}
			
			@Override
			public int getRowCount() {
				return names.size();
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return Boolean.class;
				return String.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {}
		});
		return table;
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned, Set<String> iset, List<NameSel> namesel ) {
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source =s new JCheckBox("Source");
		JCheckBox accession = new JCheckBox("Accession");
		Object[] params = new Object[] {species, country, source, accession};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		boolean fail = false;
		try {
			JSObject win = JSObject.getWindow( DataTable.this );
			StringBuilder include = new StringBuilder();
			for( String is : iset ) {
				if( include.length() == 0 ) include.append( is );
				else include.append( ","+is );
			}
			this.ns = namesel;
			String includes = include.toString();
			win.call( "fetchSeq", new Object[] { includes } );
		} catch( Exception e ) {
			fail = true;
		}
		
		if( fail ) {
			try {
				JSONObject jsono = new JSONObject();
				for( String is : iset ) {
					jsono.put(is, (Object)"");
				}
				loadSequences( jsono.toString(), aligned, namesel );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	List<NameSel> ns;
	public void loadSequences( String jsonstr ) throws JSONException {
		loadSequences( jsonstr, true, ns );
		ns = null;
	}
	
	public void console( String message ) {
		try {
			JSObject win = JSObject.getWindow( DataTable.this );
			JSObject con = (JSObject)win.getMember("console");
			con.call("log", new Object[] {message} );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void loadSequences( String jsonstr, boolean aligned, List<NameSel> namesel ) throws JSONException {
		//List<NameSel> namesel = nameSelection( extra );
		
		List<Sequence> contset = new ArrayList<Sequence>();
		Sequence	seq = null;
		//int nseq = 0;
		
		JSONObject jsono = new JSONObject( jsonstr );
		Set<String> include = new HashSet<String>();
		Iterator it = jsono.keys();
		while( it.hasNext() ) {
			String n = it.next().toString();
			Object o = jsono.get( n );
			if( o == null || o.toString().length() <= 1 || o.toString().equalsIgnoreCase("null") ) {
				include.add( n );
			} else {
				Object[] obj = tablemap.get(n);
				String fname = getFastaName( namesel, obj );
				
				String cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
				cont = cont.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "");
				
				contset.add( new Sequence( n, cont, new StringBuilder(o.toString()), currentjavafasta.mseq ) );
			}
		}
		
		InputStream is = DataTable.this.getClass().getResourceAsStream("/thermaceae_16S_aligned.fasta");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			String inc = null;
			String line = br.readLine();
			while( line != null ) {
				/*if( line.startsWith(">") ) {
					int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);
					if( include.contains(name) ) {
						Object[] obj = tablemap.get(acc);
						
						inc = true;
						String fname = ">";
						if( accession.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						if( country.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[11];
							else fname += "_"+obj[11];
						} 
						if( source.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						
						if( fname.length() > 1 ) {
							sb.append(">"+fname+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( line.length() > 100 ) {
						for( int i = 0; i < line.length(); i+= 70 ) {
							sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
						}
					} else sb.append( line+"\n" );
				}*/
				
				if( line.startsWith(">") ) {
					if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}
					
					inc = null;
					
					//Iterator it = jsono.keys();
					//while( it.hasNext() ) {
					for( String str : include ) {
						//String str = it.next().toString();
						if( line.contains( str ) ) {
							inc = str;
							break;
						}
					}
					
					if( inc != null ) {
						Object[] obj = tablemap.get(inc);
						String fname = getFastaName( namesel, obj );
						/*String fname = "";
						for( NameSel ns : namesel ) {
							if( ns.isSelected() ) {
								if( ns.name.equals("Species") ) {
									String spec = (String)obj[2];
									spec = spec.replace("Thermus ", "T.");
									
									int iv = spec.indexOf('_');
									int iv2 = spec.indexOf(' ');
									if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
									else iv = Math.min(iv, iv2);
									if( iv == -1 ) {
										iv = spec.indexOf("16S");
									}
									if( iv != -1 ) spec = spec.substring(0, iv).trim();
									if( fname.length() == 0 ) fname += spec;
									else fname += "_"+spec;
								} else if( ns.name.equals("country") ) {
									String cntr = (String)obj[11];
									int idx = cntr.indexOf('(');
									if( idx > 0 ) {
										int idx2 = cntr.indexOf(')', idx+1);
										if( idx2 == -1 ) idx2 = cntr.length()-1;
										cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
									}
									if( fname.length() == 0 ) fname += cntr;
									else fname += "_"+cntr;
								} else if( ns.name.equals("Source") ) {
									if( fname.length() == 0 ) fname += obj[12];
									else fname += "_"+obj[12];
								} else if( ns.name.equals("Accession") ) {
									String acc = (String)obj[1];
									acc = acc.replace("_", "");
									if( fname.length() == 0 ) fname += acc;
									else fname += "_"+acc;
								} else if( ns.name.equals("Pubmed") ) {
									String pubmed = (String)obj[6];
									if( fname.length() == 0 ) fname += pubmed;
									else fname += "_"+pubmed;
								}
							}
						}*/
						
						String cont;
						if( fname.length() > 1 ) {
							cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
						} else cont = line.substring(1);
						cont = cont.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "");
						//if( rr.length == 1 ) cont = line.replace( ">", "" );
						//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = new Sequence( inc, cont, currentjavafasta.mseq );
						//dna.append( line.replace( ">", ">"+seqs.getName()+"_" )+"\n" );
						//nseq++;
					}
				} else if( inc != null ) {
					String lrp = line.replace(" ", "");
					if( !aligned ) lrp = lrp.replace("-", "");
					seq.append( lrp );
				}
				line = br.readLine();
			}
			br.close();
			if( inc != null && seq != null ) {
				//Sequence seq = jf.new Sequence(cont, dna);
				contset.add(seq);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Sequence contig : contset) {
			contig.checkLengths();
			currentjavafasta.addSequence(contig);
			if (contig.getAnnotations() != null)
				Collections.sort(contig.getAnnotations());
			
			seqcache.put( contig.getId(), contig );
		}
		
		if( runnable != null ) {
			runnable.run();
			runnable = null;
		}
		//currentjavafasta.updateView();
	}
	
	Runnable runnable = null;
	public void viewAligned( JavaFasta jf, boolean aligned ) {
		loadAligned( jf, aligned );
		Sequence cons = jf.getConsensus();
		jf.addAnnotation( cons.new Annotation(null,"V1 - 16S rRNA",Color.blue,140,226, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V2 - 16S rRNA",Color.blue,276,438, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V3 - 16S rRNA",Color.blue,646,742, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V4 - 16S rRNA",Color.blue,865,1024, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V5 - 16S rRNA",Color.blue,1217,1309, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V6 - 16S rRNA",Color.blue,1469,1595, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V7 - 16S rRNA",Color.blue,1708,1804, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V8 - 16S rRNA",Color.blue,1894,1956, jf.mann ) );
		jf.addAnnotation( cons.new Annotation(null,"V9 - 16S rRNA",Color.blue,2149,2209, jf.mann ) );
	}
	
	public void addSave( JFrame frame, final JavaFasta jf ) {
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
				List<Sequence> lseq = jf.getEditedSequences();
				if( lseq.size() > 0 && JOptionPane.showConfirmDialog(DataTable.this, "Save", "Do you want to save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
					JSObject jso = JSObject.getWindow( DataTable.this );
					Map<String,String>	map = new HashMap<String,String>();
					for( Sequence s : lseq ) {
						map.put(s.getId(), s.getStringBuilder().toString());
					}
					JSONObject jsono = new JSONObject( map );
					String savestr = jsono.toString();
					jso.call("saveSeq2", new Object[] {savestr} );
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	public String getFastaName( List<NameSel> namesel, Object[] obj ) {
		String fname = "";
		for( NameSel ns : namesel ) {
			if( ns.isSelected() ) {
				if( ns.name.equals("Species") ) {
					String spec = (String)obj[3];
					int		id = (Integer)obj[5];
					if( id >= 97 ) {
						spec = spec.replace("Thermus ", "T.");
						
						if( spec.contains("eggert") || spec.contains("yunnan") || spec.contains("rehai") || spec.contains("malas") || spec.contains("chile") ) {
							spec = '"'+spec+'"';
						}
					} else spec = '"'+"T.unkown"+'"';
					
					int iv = spec.indexOf('_');
					int iv2 = spec.indexOf(' ');
					if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
					else iv = Math.min(iv, iv2);
					if( iv == -1 ) {
						iv = spec.indexOf("16S");
					}
					if( iv != -1 ) spec = spec.substring(0, iv).trim();
					if( fname.length() == 0 ) fname += spec;
					else fname += "_"+spec;
				} else if( ns.name.equals("Country") ) {
					String cntr = (String)obj[6];
					int idx = cntr.indexOf('(');
					if( idx > 0 ) {
						int idx2 = cntr.indexOf(')', idx+1);
						if( idx2 == -1 ) idx2 = cntr.length()-1;
						cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
					}
					if( fname.length() == 0 ) fname += cntr;
					else fname += "_"+cntr;
				} else if( ns.name.equals("Source") ) {
					if( fname.length() == 0 ) fname += obj[7];
					else fname += "_"+obj[7];
				} else if( ns.name.equals("Accession") ) {
					String acc = (String)obj[1];
					acc = acc.replace("_", "");
					if( fname.length() == 0 ) fname += acc;
					else fname += "_"+acc;
				} else if( ns.name.equals("Pubmed") ) {
					String pubmed = (String)obj[8];
					if( fname.length() == 0 ) fname += pubmed;
					else fname += "_"+pubmed;
				}
			}
		}
		return fname;
	}
	
	public String extractPhy( String filename ) {
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox accession = new JCheckBox("Acc");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		Object[] params = new Object[] {species, accession, country, source};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		List<NameSel>	namesel = nameSelection( null );
		
		int start = 0;
		int stop = -1;
		if( currentjavafasta != null && currentjavafasta.getSelectedRect() != null ) {
			Rectangle selrect = currentjavafasta.getSelectedRect();
			if( selrect.width > 0 ) {
				start = selrect.x;
				stop = selrect.x+selrect.width;
			}
		}
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String acc = (String)val[1];
				//System.err.println( acc );
				include.add( acc );
			}
		}
		
		System.err.println( "about to" );
		StringBuilder sb = new StringBuilder();
		InputStream is = DataTable.this.getClass().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			int istart = 0;
			boolean inc = false;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					istart = 0;
					/*int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);*/
					
					/*if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}*/
					
					String incstr = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							incstr = str;
							break;
						}
					}
					
					if( incstr != null ) {
						Object[] obj = tablemap.get(incstr);
						
						inc = true;
						/*String fname = "";
						if( species.isSelected() ) {
							Integer ident = (Integer)obj[4];
							String spec = "T.unkown";
							if( ident >= 97 ) {
								spec = (String)obj[2];
								spec = spec.replace("Thermus ", "T.");
								
								int iv = spec.indexOf('_');
								int iv2 = spec.indexOf(' ');
								if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
								else iv = Math.min(iv, iv2);
								if( iv == -1 ) {
									iv = spec.indexOf("16S");
								}
								if( iv != -1 ) spec = spec.substring(0, iv).trim();
							}
							
							if( fname.length() == 0 ) fname += spec;
							else fname += "_"+spec;
						} 
						if( country.isSelected() ) {
							String cntr = (String)obj[11];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						if( accession.isSelected() ) {
							String acc = (String)obj[1];
							acc = acc.replace("_", "");
							if( fname.length() == 0 ) fname += acc;
							else fname += "_"+acc;
						}*/
						String fname = getFastaName( namesel, obj );
						
						if( fname.length() > 1 ) {
							String startf = (Integer)obj[3] >= 900 ? ">" : ">*";
							sb.append(startf+fname.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "")+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( stop > 0 ) {
						for( int i = start; i < Math.min( stop, istart+line.length() ); i++ ) {
							sb.append( line.charAt(i) );
							if( (i-start)%70 == 69 ) sb.append( '\n' );
						}
						istart += line.length();
					} else {
						if( line.length() > 100 ) {
							for( int i = 0; i < line.length(); i+= 70 ) {
								sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
							}
						} else sb.append( line+"\n" );
					}
				}
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.err.println( "after" );
		return sb.toString();
	}
	
	public StringBuilder extractFasta( String filename ) {
		
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox accession = new JCheckBox("Acc");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		Object[] params = new Object[] {species, accession, country, source};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		List<NameSel>	namesel = nameSelection( null );
		
		int start = 0;
		int stop = -1;
		if( currentjavafasta != null && currentjavafasta.getSelectedRect() != null ) {
			Rectangle selrect = currentjavafasta.getSelectedRect();
			if( selrect != null && selrect.width > 0 ) {
				start = selrect.x;
				stop = selrect.x+selrect.width;
			}
		}
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String acc = (String)val[1];
				//System.err.println( acc );
				include.add( acc );
			}
		}
		
		System.err.println( "about to" );
		StringBuilder sb = new StringBuilder();
		InputStream is = DataTable.this.getClass().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			int istart = 0;
			boolean inc = false;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					istart = 0;
					/*int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);*/
					
					/*if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}*/
					
					String incstr = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							incstr = str;
							break;
						}
					}
					
					if( incstr != null ) {
						Object[] obj = tablemap.get(incstr);
						
						inc = true;
						/*String fname = "";
						if( species.isSelected() ) {
							Integer ident = (Integer)obj[4];
							String spec = "T.unkown";
							if( ident >= 97 ) {
								spec = (String)obj[2];
								spec = spec.replace("Thermus ", "T.");
								
								int iv = spec.indexOf('_');
								int iv2 = spec.indexOf(' ');
								if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
								else iv = Math.min(iv, iv2);
								if( iv == -1 ) {
									iv = spec.indexOf("16S");
								}
								if( iv != -1 ) spec = spec.substring(0, iv).trim();
							}
							
							if( fname.length() == 0 ) fname += spec;
							else fname += "_"+spec;
						} 
						if( country.isSelected() ) {
							String cntr = (String)obj[11];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						if( accession.isSelected() ) {
							String acc = (String)obj[1];
							acc = acc.replace("_", "");
							if( fname.length() == 0 ) fname += acc;
							else fname += "_"+acc;
						}*/
						String fname = getFastaName( namesel, obj );
						
						if( fname.length() > 1 ) {
							String startf = (Integer)obj[4] >= 900 ? ">" : ">*";
							sb.append(startf+fname.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "")+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( stop > 0 ) {
						for( int i = start; i < Math.min( stop, istart+line.length() ); i++ ) {
							sb.append( line.charAt(i) );
							if( (i-start)%70 == 69 ) sb.append( '\n' );
						}
						istart += line.length();
					} else {
						if( line.length() > 100 ) {
							for( int i = 0; i < line.length(); i+= 70 ) {
								sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
							}
						} else sb.append( line+"\n" );
					}
				}
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//String fst = sb.toString();
		return sb;
	}
	
	public void runSql( String sql ) {
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
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	JMenu	selectionMenu = new JMenu("Saved selections");
	//Map<String,String>	selectionMap = new HashMap<String,String>();
	public void appendSelection( final String key, final String value ) {
		selectionMenu.add( new AbstractAction( key ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] split = value.split(",");
				Set<String>	selset = new HashSet<String>( Arrays.asList(split) );
				
				for( int i = 0; i < table.getRowCount(); i++ ) {
					if( selset.contains( table.getValueAt(i, 1) ) ) {
						table.addRowSelectionInterval(i, i);
					}
				}
			}
		});
	}
    
	JavaFasta	currentjavafasta;
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
				return 22;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "name";
				else if( columnIndex == 1 ) return "acc";
				else if( columnIndex == 2 ) return "fullname";
				else if( columnIndex == 3 ) return "species";
				else if( columnIndex == 4 ) return "len";
				else if( columnIndex == 5 ) return "ident";
				else if( columnIndex == 6 ) return "country";
				else if( columnIndex == 7 ) return "source";
				else if( columnIndex == 8 ) return "doi";
				else if( columnIndex == 9 ) return "pubmed";
				else if( columnIndex == 10 ) return "author";
				else if( columnIndex == 11 ) return "journal";
				else if( columnIndex == 12 ) return "sub_auth";
				else if( columnIndex == 13 ) return "sub_date";
				else if( columnIndex == 14 ) return "lat_lon";
				else if( columnIndex == 15 ) return "date";
				else if( columnIndex == 16 ) return "title";
				else if( columnIndex == 17 ) return "arb";
				else if( columnIndex == 18 ) return "color";
				else if( columnIndex == 19 ) return "temp";
				else if( columnIndex == 20 ) return "pH";
				else if( columnIndex == 21 ) return "valid";
				//else if( columnIndex == 13 ) return "color";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 4 || columnIndex == 5 ) return Integer.class;
				else if( columnIndex == 19 || columnIndex == 20 ) return Double.class;
				else if( columnIndex == 21 ) return Boolean.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 6 || columnIndex == 19 ) return true;
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
				//if( ret instanceof Integer ) System.err.println( columnIndex );
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
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/lmin900idmin95phyml.tree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_hq.phb");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_hq.phy_phyml_tree.txt");
				InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_selected.phb");
				
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
					name = name.substring(0, Math.min( name.length(), 10 ));
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
						else if( nm.contains("t.brock") ) nm = "Thermus brockianus";
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
		popup.add( new AbstractAction("Export biogeography report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] specs = {"antranikianii","aquaticus","arciformis","brockianus","eggertsoni","filiformis","igniterrae","islandicus","kawarayensis","oshimai","scotoductus","thermophilus","yunnanensis","rehai","composti","unknownchile"};
				
				Map<String,Map<String,Long>>	specLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSpec = new TreeMap<String,Map<String,Long>>();
				for( Object[] row : rowList ) {
					String country = (String)row[6];
					if( country != null && country.length() > 0 ) {
						String species = (String)row[3];
						String thespec = null;
						for( String spec : specs ) {
							if( species.contains(spec) ) {
								thespec = "T."+spec;
								break;
							}
						}
						
						if( thespec != null ) {
							int len = (Integer)row[4];
							int id = (Integer)row[5];
							long idlen = (((long)len)<<16)+id;
							
							Map<String,Long> cmap;
							if( specLoc.containsKey( thespec ) ) {
								cmap = specLoc.get( thespec );
							} else {
								cmap = new TreeMap<String,Long>();
								specLoc.put( thespec, cmap );
							}
							
							if( cmap.containsKey(country) ) {
								long oldidlencount = cmap.get(country);
								
								int oldidlen = (int)(oldidlencount&0xFFFFFFFF);
								int oldcount = (int)(oldidlencount>>32);
								
								int oldid = (int)(oldidlen&0xFFFF);
								int oldlen = (int)(oldidlen>>16);
								
								if( id > oldid || (id == oldid && len > oldlen) ) {
									cmap.put( country, idlen+((long)(oldcount+1)<<32) );
								} else {
									cmap.put( country, oldidlen+((long)(oldcount+1)<<32) );
								}
							} else {
								cmap.put( country, idlen+(1L<<32) );
							}
							
							Map<String,Long> smap;
							if( locSpec.containsKey( country ) ) {
								smap = locSpec.get( country );
							} else {
								smap = new TreeMap<String,Long>();
								locSpec.put( country, smap );
							}
							if( smap.containsKey(thespec) ) {
								long oldidlencount = smap.get( thespec );
								
								int oldidlen = (int)(oldidlencount&0xFFFFFFFF);
								int oldcount = (int)(oldidlencount>>32);
								
								int oldid = (int)(oldidlen&0xFFFF);
								int oldlen = (int)(oldidlen>>16);
								
								/*long oldidlen = smap.get(country);
								int oldid = (int)(oldidlen&0xFFFF);
								int oldlen = (int)(oldidlen>>32);*/
								
								if( id > oldid || (id == oldid && len > oldlen) ) {
									smap.put( thespec, idlen+((long)(oldcount+1)<<32) );
								} else {
									smap.put( thespec, oldidlen+((long)(oldcount+1)<<32) );
								}
							} else {
								smap.put( thespec, idlen+(1L<<32)  );
							}
						}
					}
				}
				
				Workbook wb = new XSSFWorkbook();
				Sheet lSheet = wb.createSheet("Locations");
				Sheet sSheet = wb.createSheet("Species");
				Sheet bSheet = wb.createSheet("Boolean");
				
				Row r = lSheet.createRow(0);
				r.createCell(0).setCellValue("Loction");
				r.createCell(1).setCellValue("Species");
				r.createCell(2).setCellValue("Identity");
				r.createCell(3).setCellValue("Length");
				
				int val = 1;
				for( String cnt : locSpec.keySet() ) {
					Map<String,Long> smap = locSpec.get(cnt);
					int subval = 0;
					for( String spec : smap.keySet() ) {
						long idlencount = smap.get(spec);
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						r = lSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( cnt );
						if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
							r.createCell(1).setCellValue( "*"+spec+" ("+count+")" );
						} else {
							r.createCell(1).setCellValue( spec+" ("+count+")" );
						}
						r.createCell(2).setCellValue( id );
						r.createCell(3).setCellValue( len );
						
						subval++;
					}
					val += subval;
				}
				
				r = sSheet.createRow(0);
				r.createCell(0).setCellValue("Species");
				r.createCell(1).setCellValue("Location");
				r.createCell(2).setCellValue("Identity");
				r.createCell(3).setCellValue("Length");
				
				val = 1;
				for( String spec : specLoc.keySet() ) {
					Map<String,Long> cmap = specLoc.get(spec);
					int subval = 0;
					for( String cnt : cmap.keySet() ) {
						long idlencount = cmap.get( cnt);
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						/*long idlen = cmap.get(cnt);
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>32);*/
						
						r = sSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( spec );
						if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
							r.createCell(1).setCellValue( "*"+cnt+" ("+count+")" );
						} else {
							r.createCell(1).setCellValue( cnt+" ("+count+")" );
						}
						r.createCell(2).setCellValue( id );
						r.createCell(3).setCellValue( len );
						
						subval++;
					}
					val += subval;
				}
				
				Map<String,Integer>	specIndex = new HashMap<String,Integer>();
				r = bSheet.createRow(0);
				val = 0;
				for( String spec : specLoc.keySet() ) {
					specIndex.put( spec, val );
					r.createCell(++val).setCellValue( spec );
				}
				
				val = 1;
				for( String cnt : locSpec.keySet() ) {
					Map<String,Long> smap = locSpec.get(cnt);
					
					r = bSheet.createRow(val);
					r.createCell(0).setCellValue(cnt);
					for( String spec : smap.keySet() ) {
						long idlencount = smap.get( spec );
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						/*long idlen = smap.get(spec);
						int id = (int)(idlen&0xFF);
						int len = (int)(idlen>>32);*/
						
						int idx = -1;
						if( specIndex.containsKey(spec) ) idx = specIndex.get(spec);
						if( idx != -1 ) {
							if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
								r.createCell(idx+1).setCellValue( "*" + id + "/" + len+" ("+count+")" );
							} else {
								r.createCell(idx+1).setCellValue( id + "/" + len+" ("+count+")" );
							}
						}
					}
					val++;
				}
				
				FileSaveService fss = null;
		        FileContents fileContents = null;
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        OutputStreamWriter	osw = new OutputStreamWriter( baos );
		    	
		    	try {
		    		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
		    	} catch( UnavailableServiceException e1 ) {
		    		fss = null;
		    	}
		    	 
		    	try {
			        if (fss != null) {
			        	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
			            fileContents = fss.saveFileDialog(null, null, bais, "export.xlsx");
			            bais.close();
			            OutputStream os = fileContents.getOutputStream(true);
			            //os.write( baos.toByteArray() );
			            wb.write( os );
			            os.close();
			        } else {
			        	JFileChooser jfc = new JFileChooser();
			        	if( jfc.showSaveDialog( DataTable.this ) == JFileChooser.APPROVE_OPTION ) {
			        		File f = jfc.getSelectedFile();
			        		FileOutputStream fos = new FileOutputStream( f );
			        		wb.write( fos );
			        		//fos.write( baos.toByteArray() );
			        		fos.close();
			        		 
			        		Desktop.getDesktop().browse( f.toURI() );
			        	}
			        }
		    	} catch( IOException ioe ) {
		    		ioe.printStackTrace();
		    	}
			}
		});
		popup.addSeparator();
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
						runSql( sql );
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
		popup.add( new AbstractAction("Save selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selname = JOptionPane.showInputDialog("Name of selection?");
				StringBuilder sb = new StringBuilder();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					Object o = table.getValueAt(r, 1);
					if( r == rr[0] ) sb.append( (String)o );
					else sb.append( ","+(String)o );
				}
				
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("saveSel", new Object[] {selname, sb.toString()} );
			}
		});
		popup.add( selectionMenu );
		Action action = new CopyAction( "Copy" );
		popup.add( action );
		popup.addSeparator();
		
		JMenu	njmenu = new JMenu( "NJTree" );
		popup.add( njmenu );
		njmenu.add( new AbstractAction("NJTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
				final JCheckBox	boots = new JCheckBox("Bootstrap");
				final JCheckBox	entropy = new JCheckBox("Entropy weighting");
				final JCheckBox	exgaps = new JCheckBox("Exclude gaps");
				Object[] extraObjs = new Object[] {jukes, boots, exgaps, entropy};
				//JOptionPane.showMessageDialog( DataTable.this, extraObjs );
				
				runnable = new Runnable() {
					public void run() {
						boolean cantor = jukes.isSelected();
						boolean bootstrap = boots.isSelected();
						boolean entr = entropy.isSelected();
						boolean exg = exgaps.isSelected();
						
						double[] ent = null;
						if( entr ) {
							ent = Sequence.entropy( currentjavafasta.lseq );
						}
						
						List<Integer>	idxs = null;
						if( exg ) {
							int start = Integer.MIN_VALUE;
							int end = Integer.MAX_VALUE;
							
							for( Sequence seq : currentjavafasta.lseq ) {
								if( seq.getRealStart() > start ) start = seq.getRealStart();
								if( seq.getRealStop() < end ) end = seq.getRealStop();
							}
							
							idxs = new ArrayList<Integer>();
							for( int x = start; x < end; x++ ) {
								boolean skip = false;
								for( Sequence seq : currentjavafasta.lseq ) {
									char c = seq.charAt( x );
									if( c != '-' && c != '.' && c == ' ' ) {
										skip = true;
										break;
									}
								}
								
								if( !skip ) {
									idxs.add( x );
								}
							}
						}
						
						String tree = "";
						List<String>	corrInd = currentjavafasta.getNames();
						double[] corr = new double[ currentjavafasta.lseq.size()*currentjavafasta.lseq.size() ];
						Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, null, false, cantor, ent );
						TreeUtil	tu = new TreeUtil();
						Node n = tu.neighborJoin(corr, corrInd, null);

						if( bootstrap ) {
							Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
								@Override
								public int compare(Node o1, Node o2) {
									String c1 = o1.toStringWoLengths();
									String c2 = o2.toStringWoLengths();
									
									return c1.compareTo( c2 );
								}
							};
							tu.arrange( n, comp );
							tree = n.toStringWoLengths();
							
							for( int i = 0; i < 1000; i++ ) {
								Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, idxs, true, cantor, ent );
								Node nn = tu.neighborJoin(corr, corrInd, null);
								tu.arrange( nn, comp );
								tu.compareTrees( tree, n, nn );
								
								//String btree = nn.toStringWoLengths();
								//System.err.println( btree );
							}
							tu.appendCompare( n );
						}
						tree = n.toString();
						
						boolean scc = true;
						if( tree.length() > 0 ) {
							try {
								JSObject win = JSObject.getWindow( DataTable.this );
								Object[] objs = { tree };
								win.call("showTree", objs);
							} catch( Exception e1 ) {
								scc = false;
								console( e1.getMessage() );
								console( tree );
							}
							
							if( !scc ) {
								JTextArea	text = new JTextArea();
								text.setText( tree );
								JFrame frame = new JFrame();
								frame.setSize(800, 600);
								frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							
								JScrollPane	scrollpane = new JScrollPane( text );
								frame.add( scrollpane );
								frame.setVisible(true);
							}
						}
					}
				};
				
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*/
				loadAligned(jf, true, extraObjs);
			}
		});
		/*njmenu.add( new AbstractAction("NJTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				
				runnable = new Runnable() {
					@Override
					public void run() {
						JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
						JCheckBox	boots = new JCheckBox("Bootstrap");
						//JCheckBox	boots = new JCheckBox("Bootstrap");
						JOptionPane.showMessageDialog( DataTable.this, new Object[] {jukes, boots} );
						boolean cantor = jukes.isSelected();
						boolean bootstrap = boots.isSelected();
						
						int start = Integer.MIN_VALUE;
						int end = Integer.MAX_VALUE;
						
						for( Sequence seq : currentjavafasta.lseq ) {
							if( seq.getRealStart() > start ) start = seq.getRealStart();
							if( seq.getRealStop() < end ) end = seq.getRealStop();
						}
						
						List<Integer>	idxs = new ArrayList<Integer>();
						for( int x = start; x < end; x++ ) {
							int i;
							boolean skip = false;
							for( Sequence seq : currentjavafasta.lseq ) {
								char c = seq.charAt( x );
								if( c != '-' && c != '.' && c == ' ' ) {
									skip = true;
									break;
								}
							}
							
							if( !skip ) {
								idxs.add( x );
							}
						}
						
						double[] corr = new double[ currentjavafasta.lseq.size()*currentjavafasta.lseq.size() ];
						Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, idxs, false, cantor, ent );
						List<String>	corrInd = currentjavafasta.getNames();
						
						TreeUtil	tu = new TreeUtil();
						Node n = tu.neighborJoin(corr, corrInd);
						
						if( bootstrap ) {
							Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
								@Override
								public int compare(Node o1, Node o2) {
									String c1 = o1.toStringWoLengths();
									String c2 = o2.toStringWoLengths();
									
									return c1.compareTo( c2 );
								}
							};
							tu.arrange( n, comp );
							String tree = n.toStringWoLengths();
							
							for( int i = 0; i < 1000; i++ ) {
								Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, idxs, true, cantor, ent );
								Node nn = tu.neighborJoin(corr, corrInd);
								tu.arrange( nn, comp );
								tu.compareTrees( tree, n, nn );
								
								//String btree = nn.toStringWoLengths();
								//System.err.println( btree );
							}
							tu.appendCompare( n );
						}
						
						Object[] objs = { n.toString() };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("showTree", objs);	
					}
				};
				
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*
				loadAligned(jf, true);
			}
		});*/
		njmenu.add( new AbstractAction("NJTree current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
				JCheckBox	boots = new JCheckBox("Bootstrap");
				JCheckBox	entropy = new JCheckBox("Entropy weighting");
				
				JOptionPane.showMessageDialog( DataTable.this, new Object[] {jukes, boots} );
				boolean cantor = jukes.isSelected();
				boolean bootstrap = boots.isSelected();
				boolean entr = entropy.isSelected();
				
				double[] ent = null;
				if( entr ) ent = Sequence.entropy( currentjavafasta.lseq );
				
				double[] corr = new double[ currentjavafasta.lseq.size()*currentjavafasta.lseq.size() ];
				Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, null, false, cantor, ent );
				List<String>	corrInd = currentjavafasta.getNames();
				
				TreeUtil	tu = new TreeUtil();
				Node n = tu.neighborJoin(corr, corrInd, null);
				
				if( bootstrap ) {
					Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
						@Override
						public int compare(Node o1, Node o2) {
							String c1 = o1.toStringWoLengths();
							String c2 = o2.toStringWoLengths();
							
							return c1.compareTo( c2 );
						}
					};
					tu.arrange( n, comp );
					String tree = n.toStringWoLengths();
					
					for( int i = 0; i < 1000; i++ ) {
						Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, null, true, cantor, ent );
						Node nn = tu.neighborJoin(corr, corrInd, null);
						tu.arrange( nn, comp );
						tu.compareTrees( tree, n, nn );
						
						//String btree = nn.toStringWoLengths();
						//System.err.println( btree );
					}
					tu.appendCompare( n );
				}
				
				Object[] objs = { n.toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("showTree", objs);
			}
		});
		
		JMenu fasttreemenu = new JMenu("FastTree");
		popup.add( fasttreemenu );
		fasttreemenu.add( new AbstractAction("FastTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*Runnable run = new Runnable() {
					Object[] objs = { "f"+tree };
					JSObject win = JSObject.getWindow( DataTable.this );
					win.call("fasttree", objs);
				}*/
				
				StringBuilder tree = extractFasta("/thermaceae_16S_aligned.fasta");
				String t1 = "f"+tree.substring(0, tree.length()/2);
				String t2 = tree.substring(tree.length()/2, tree.length());
				
				int tlen = tree.length()+1;
				JSObject win = JSObject.getWindow( DataTable.this );
				Object[] objs1 = { t1, tlen };
				win.call( "postModuleMessage", objs1 );
				Object[] objs2 = { t2, tlen };
				win.call( "postModuleMessage", objs2 );
				
				/*Object smod = win.getMember("simmiModule");
				System.err.println("about to call nacl");
				if( smod != null && smod instanceof JSObject ) {
					JSObject obj = (JSObject)smod;
					System.err.println("about to postmessage to nacl");
					obj.call("postMessage", objs);
				} else {
					System.err.println("fasttree fail");
				}*/
			}
		});
		fasttreemenu.add( new AbstractAction("FastTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				runnable = new Runnable() {
					public void run() {
						StringBuilder tree = currentjavafasta.getFastaWoGaps();
						Object[] objs = { "f"+tree.toString() };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("fasttree", objs);
					}
				};
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
				//extractFastaWoGaps("/thermales.fasta", run);
			}
		});
		fasttreemenu.add( new AbstractAction("FastTree current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fasta = currentjavafasta.getFasta( currentjavafasta.getSequences() );
				Object[] objs = { "f"+fasta.toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("fasttree", objs);
			}
		});
		JMenu	dnaparsmenu = new JMenu("Dnapars");
		popup.add( dnaparsmenu );
		dnaparsmenu.add( new AbstractAction("Dnapars") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						String phy = currentjavafasta.getPhylip( true );
						Object[] objs = { "p"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);	
					}
				};
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnaparsmenu.add( new AbstractAction("Dnapars w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.removeGaps( currentjavafasta.getSequences() );
						String phy = currentjavafasta.getPhylip( true );
						
						Object[] objs = { "p"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnaparsmenu.add( new AbstractAction("Dnapars current view") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				String phy = currentjavafasta.getPhylip( true );
				Object[] objs = { "p"+phy };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("dnapars", objs);
			}
		});
		JMenu	dnamlmenu = new JMenu("Dnaml");
		popup.add( dnamlmenu );
		dnamlmenu.add( new AbstractAction("Dnaml") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					
					@Override
					public void run() {
						String phy = currentjavafasta.getPhylip( true );
						Object[] objs = { "c"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				JavaFasta jf = new JavaFasta( DataTable.this );
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnamlmenu.add( new AbstractAction("Dnaml w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.removeGaps( currentjavafasta.getSequences() );
						String phy = currentjavafasta.getPhylip( true );
						
						Object[] objs = { "c"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				JavaFasta jf = new JavaFasta( DataTable.this );
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnamlmenu.add( new AbstractAction("Dnaml current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String phy = currentjavafasta.getPhylip( true );
				Object[] objs = { "c"+phy };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("dnapars", objs);
			}
		});
		dnamlmenu.add( new AbstractAction("Show conserved species sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JavaFasta jf = new JavaFasta( DataTable.this );
				JFrame frame = new JFrame();
				addSave( frame, jf );
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				conservedSpecies( jf, false );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("Show variant species sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JavaFasta jf = new JavaFasta( DataTable.this );
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				conservedSpecies( jf, true );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("View aligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JavaFasta jf = new JavaFasta( DataTable.this );
				JFrame frame = new JFrame();
				addSave( frame, jf );
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.updateView();
					}
				};
				viewAligned( jf, true );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("View unaligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JavaFasta jf = new JavaFasta( DataTable.this );
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.updateView();
					}
				};
				viewAligned( jf, false );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("Append aligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( currentjavafasta == null ) {
					currentjavafasta = new JavaFasta( DataTable.this );
					JFrame frame = new JFrame();
					frame.setSize(800, 600);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					currentjavafasta.initGui(frame);
					frame.setVisible(true);
				}
				viewAligned( currentjavafasta, true );
			}
		});
		/*popup.add( new AbstractAction("Show fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				JTextArea textarea = new JTextArea();
				textarea.setDragEnabled(true);
				String fasta = extractFasta("/thermus_all_gaps.fasta");
				textarea.setText( fasta );
				
				JScrollPane	scrollpane = new JScrollPane( textarea );
				dialog.add( scrollpane );
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				dialog.setVisible( true );
			}
		});*/
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
		popup.add( new AbstractAction("Selection of each") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int num = selectionOfEach();
				int[] rr = table.getSelectedRows();
				Map<String,Integer>	nameNum = new HashMap<String,Integer>();
				for( int r : rr ) {
					int rv = table.convertRowIndexToModel( r );
					Object[] obj = rowList.get( rv );
					
					String name = getFastaName(names, obj);
					if( nameNum.containsKey( name ) ) {
						int nnum = nameNum.get( name );
						if( num > nnum ) nameNum.put( name, nnum+1 );
						else table.removeRowSelectionInterval(r, r);
					} else {
						nameNum.put( name, 1 );
					}
				}
			}
		});
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
		
		table.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
					filterset.clear();
					updateFilter(table, filter);
				}
			}
		});
		table.setComponentPopupMenu( popup );
		
		//String res = getThermusFusion();
		//loadData( res );
		
		boolean succ = true;
		try {
			JSObject win = JSObject.getWindow(this);
			System.err.println( "about to run loadData" );
			win.call("loadData", new Object[] {});
			win.call("reqSavedSel", new Object[] {});
			//System.err.println( "done loadData" );
			//win.call("loadMeta", new Object[] {});
		} catch( Exception e ) {
			succ = false;
		}
		
		if( !succ ) {
			String res = getThermusFusion();
			loadData( res );
		}
		
		this.add( scrollpane );
	}
	
	public void showTree( String tree ) {
		JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
		dialog.setSize(800, 600);
		JTextArea textarea = new JTextArea();
		textarea.setDragEnabled(true);
		textarea.setText( tree );
		
		JScrollPane	scrollpane = new JScrollPane( textarea );
		dialog.add( scrollpane );
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		dialog.setVisible( true );
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	
	public static class StrId {
		public StrId(String teg, int len) {
			name = teg;
			this.len = len;
		}

		String name;
		int id;
		int len;
	};
	
	public static void main(String[] args) {
		/*File f = new File("/home/sigmar/sim.newick");
		try {
			char[] cbuf = new char[(int)f.length()];
			FileReader fr = new FileReader(f);
			int r = fr.read(cbuf);
			String str = new String( cbuf );
			String tree = str.replaceAll("[\r\n]+", "");
			TreeUtil	treeutil = new TreeUtil( tree, false, null, null, false, null, null, false );
			Node n = treeutil.getNode();
			String thetree = n.toString();
			FileWriter fw = new FileWriter("/home/sigmar/fw.newick");
			fw.write( thetree );
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			service.setUserCredentials("signinhelpdesk@gmail.com", "vid-311.hald", ClientLoginAccountType.GOOGLE);
			
			//String ret = run("select acc from "+oldtabhleid+" where country like '%hile%' and species like '%filiform%'", true);
			String ret = run("select acc, country from "+oldtableid+" where len(country) > 1", true);
			String[] split = ret.split("\n");
			Map<String,String>	oldids = new HashMap<String,String>();
			for( int i = 1; i < split.length; i++ ) {
				String s = split[i];
				int val = s.indexOf(',');
				if( val != -1 ) {
					oldids.put( s.substring(0, val), s.substring(val+1, s.length()) );	
				} else {
					oldids.put( s, null );
				}
			}
			/*System.err.println( oldids.size() );
			System.err.println( oldids.keySet() );*/
			
			ret = run("select acc, rowid from "+tableid+" where len(country) < 2", true);
			split = ret.split("\n");
			HashMap<String,String>	newids = new HashMap<String,String>();
			for( String s : split ) {
				int val = s.indexOf(',');
				newids.put( s.substring(0, val), s.substring(val+1, s.length()) );
			}
			
			newids.keySet().retainAll( oldids.keySet() );
			
			for( String id : newids.keySet() ) {
				String rowid = newids.get(id);
				String country = oldids.get(id).replace("\"", "");
				//System.err.println( id + "\t" + oldids.get(id) );
				run( "update "+tableid+" set country = '"+country+"' where rowid = '"+rowid+"'", true );
			}
			
			/*for( int i = 1; i < split.length; i++ ) {
				String row = split[i];
				//String[] subsplit = row.split(",");
				//int ident = Integer.parseInt( subsplit[1] );
				run( "update "+tableid+" set country = 'USA:Yellowstone' where rowid = '"+row+"'", true );
			}
			/*String ret = run("select rowid from "+tableid+" where name = 'Unl042jm'", true);
			System.err.println( ret );
			String[] lines = ret.split("\n");
			run("update "+tableid+" set species = 'Thermus antranikianii strain HN3-7 16S ribosomal RNA, partialsequence' where rowid = '"+lines[1]+"'", true);*/
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public static void main_old(String[] args) {
		Map<String, StrId> tegmap = new HashMap<String, StrId>();
		//Map<String,String>	rowidmap = new HashMap<String,String>();
		
		try {
			FileReader fr = new FileReader("/home/sigmar/thermus16S_all.blastout");
			//FileReader fr = new FileReader("/home/sigmar/newthermus16S.blastout");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String current = null;
			StrId currteg = null;
			int currlen = 0;
			boolean done = false;
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
					if( currteg != null ) {
						int sv = trim.indexOf('(');
						int svl = trim.indexOf('%', sv + 1);
	
						String trimsub = trim.substring(sv + 1, svl);
						currteg.id = Integer.parseInt( trimsub );
					}
					done = true;
				}

				line = br.readLine();
			}
			fr.close();
			
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			//service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
		
			//String ret = run("select name, rowid from "+tableid+" where name like 't.spCCB%'", true);
			String ret = run("select name, rowid from "+tableid, true);
			String[] lines = ret.split("\n");
			for( int i = 1; i < lines.length; i++ ) {
				line = lines[i];
				int comma = line.indexOf(',');
				String name = line.substring(0, comma);
				String rowid = line.substring( comma+1 );
				if( tegmap.containsKey(name) ) {
					StrId species = tegmap.get(name);
					//System.err.println( i + "  " + name + "  " + species.id + " of " + lines.length );
					run("update "+tableid+" set species = '"+species.name+"', ident = '"+species.id+"', len = '"+species.len+"' where rowid = '"+rowid+"'", true);
				} else {
					System.err.println( "fail "+name );
				}
				//rowidmap.put( name, rowid );
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		//System.err.println( tegmap.size() );
	}
}
