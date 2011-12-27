package org.simmi;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.shared.TreeUtil;

public class DataTable extends JApplet {
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
	
	public String makeCopyString() {
		StringBuilder sb = new StringBuilder();
            
        int[] rr = table.getSelectedRows();
        int[] cc = table.getSelectedColumns();
        
        System.err.println( rr );
        System.err.println( cc );
        
        for( int ii : rr ) {
            for( int jj : cc ) {
            	Object val = table.getValueAt(ii,jj);
                if( val != null && val instanceof Float ) sb.append( "\t"+Float.toString( (Float)val ) );
                else sb.append( "\t" );
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
            StringSelection selection = new StringSelection(s);
            clipboardService.setContents( selection );
        }
        
        if (grabFocus) {
            source.requestFocus();
        }
    }
	 
    class CopyAction extends AbstractAction {
        public CopyAction(String text, ImageIcon icon,
            String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
 
        public void actionPerformed(ActionEvent e) {
            copyData((Component)e.getSource());
        }
    }

    JTable	table;
    private ClipboardService 	clipboardService;
    private boolean				grabFocus = false;
	
	public void init() {
		updateLof();
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		table.setColumnSelectionAllowed( true );
		JScrollPane	scrollpane = new JScrollPane( table );
		
		final Map<String,String>	nameaccmap = new HashMap<String,String>();
		
		final List<String[]>	rowList = new ArrayList<String[]>();
		InputStream is = this.getClass().getResourceAsStream( "/therm2.txt" );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		try {
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				nameaccmap.put(split[0], split[1]);
				rowList.add( Arrays.copyOfRange(split, 1, split.length ) );
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return rowList.size();
			}

			@Override
			public int getColumnCount() {
				return 12;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "acc";
				else if( columnIndex == 1 ) return "species";
				else if( columnIndex == 2 ) return "len";
				else if( columnIndex == 3 ) return "ident";
				else if( columnIndex == 4 ) return "doi";
				else if( columnIndex == 5 ) return "pubmed";
				else if( columnIndex == 6 ) return "journal";
				else if( columnIndex == 7 ) return "auth";
				else if( columnIndex == 8 ) return "sub_auth";
				else if( columnIndex == 9 ) return "sub_date";
				else if( columnIndex == 10 ) return "country";
				else if( columnIndex == 11 ) return "source";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 2 || columnIndex ==3 ) return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String[] val = rowList.get(rowIndex);
				if( columnIndex < val.length ) {
					if( columnIndex == 2 || columnIndex ==3 ) return Integer.parseInt( val[columnIndex] );
					return val[columnIndex];
				}
				
				return "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

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
				InputStream is = DataTable.this.getClass().getResourceAsStream("/temp.ntree");
				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while( line != null ) {
					sb.append( line );
					line = br.readLine();
				}
				br.close();
				
				Map<String,Map<String,String>> mapmap = new HashMap<String,Map<String,String>>();
				Set<String>	include = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String name = (String)table.getValueAt(r, 0);
					include.add( name );
					
					String country = (String)table.getValueAt(r, 10);
					if( country != null && country.length() > 0 ) {
						Map<String,String>	map = new HashMap<String,String>();
						map.put( "country", country );
						mapmap.put(name, map);
					}
				}
				
				TreeUtil tu = new TreeUtil( sb.toString(), false, include, mapmap );
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
	    	Action action = new CopyAction( "Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL+KeyEvent.VK_C) );
            table.getActionMap().put( "copy", action );
            grabFocus = true;
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
	    }
		table.addMouseListener( new MouseAdapter() {
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
		});
		
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Show fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				JTextArea textarea = new JTextArea();
				Set<String>	include = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					if( i != -1 ) {
						String[] val = rowList.get(i);
						include.add( val[0] );
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
							if( include.contains(acc) ) {
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
		table.setComponentPopupMenu( popup );
		
		this.add( scrollpane );
	}
}