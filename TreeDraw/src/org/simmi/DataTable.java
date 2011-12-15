package org.simmi;

import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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
		
		final List<String[]>	rowList = new ArrayList<String[]>();
		InputStream is = this.getClass().getResourceAsStream( "/therm.txt" );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		String line;
		try {
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				rowList.add( split );
				line = br.readLine();
			}
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
				return 10;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "acc";
				else if( columnIndex == 1 ) return "species";
				else if( columnIndex == 2 ) return "doi";
				else if( columnIndex == 3 ) return "pubmed";
				else if( columnIndex == 4 ) return "journal";
				else if( columnIndex == 5 ) return "auth";
				else if( columnIndex == 6 ) return "sub_auth";
				else if( columnIndex == 7 ) return "sub_date";
				else if( columnIndex == 8 ) return "country";
				else if( columnIndex == 9 ) return "source";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String[] val = rowList.get(rowIndex);
				if( columnIndex < val.length ) return val[columnIndex];
				
				return "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
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
		
		table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		table.getColumnModel().getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
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
						String doi = str[2];
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
		
		this.add( scrollpane );
	}
}
