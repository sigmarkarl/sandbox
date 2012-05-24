package org.simmi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.TreeUtil;
import org.simmi.unsigned.JavaFasta;
import org.simmi.unsigned.JavaFasta.Sequence;

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
					if( vb != null ) strs[15] = Boolean.parseBoolean( vb );
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
					Object[] strs = new Object[ 16 ];
					
					int k = 0;
					for( k = 0; k < split.length; k++ ) {
						/*if( k < 3 ) {
							strs[k] = split[(k+2)%3];
						} else */
						if( k == 3 || k == 4 ) {
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
						} else if( k == 13 || k == 14 ) {
							String dstr = split[k];
							if( dstr != null && dstr.length() > 0 ) {
								System.err.println("hu");
								try {
									strs[k] = Double.parseDouble( dstr );
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								strs[k] = null;
							}
						} else if( k == 15 ) strs[k] = (split[k] != null && (split[k].equalsIgnoreCase("true") || split[k].equalsIgnoreCase("false")) ? Boolean.parseBoolean( split[k] ) : true);
						else strs[k] = split[k];
					}
					
					if( k == 8 ) strs[k++] = "";
					if( k == 9 ) strs[k++] = "";
					if( k == 10 ) strs[k++] = "";
					if( k == 11 ) strs[k++] = "";
					if( k == 12 ) strs[k++] = "";
					if( k == 13 ) strs[k++] = null;
					if( k == 14 ) strs[k++] = null;
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
	private static final String email = "huldaeggerts@gmail.com";
	private static final String password = "b.r3a1h1ms";
	private static final String tableid = "1QbELXQViIAszNyg_2NHOO9XcnN_kvaG1TLedqDc";
	
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
		InputStream is = DataTable.this.getClass().getResourceAsStream("/thermales.fasta");
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
						String spec = (String)obj[2];
						int iv = spec.indexOf('_');
						if( iv == -1 ) {
							iv = spec.indexOf("16S");
						}
						if( iv != -1 ) spec = spec.substring(0, iv).trim();
						if( fname.length() == 0 ) fname += spec;
						else fname += "_"+spec;
						
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
							if( fname.length() == 0 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						
						String cont;
						if( fname.length() > 1 ) {
							cont = fname;
						} else cont = line.substring(1);
					//if( rr.length == 1 ) cont = line.replace( ">", "" );
					//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = jf.new Sequence( cont );
						
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
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				include.add( (String)val[1] );
			}
		}
		loadAligned( jf, aligned, include );
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned, Set<String> include ) {
		JCheckBox species = new JCheckBox("Species");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		JCheckBox accession = new JCheckBox("Accession");
		Object[] params = new Object[] {species, country, source, accession};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);
		
		List<Sequence> contset = new ArrayList<Sequence>();
		Sequence	seq = null;
		//int nseq = 0;
		
		InputStream is = DataTable.this.getClass().getResourceAsStream("/thermales.fasta");
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
						if( species.isSelected() ) {
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
						}
						
						String cont;
						if( fname.length() > 1 ) {
							cont = (Integer)obj[3] >= 900 ? fname : "*"+fname;
						} else cont = line.substring(1);
						//if( rr.length == 1 ) cont = line.replace( ">", "" );
						//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = jf.new Sequence( cont.replace(": ", "-").replace(' ', '_').replace(':', '-').replace(",", "").replace(";", "") );
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
			jf.addSequence(contig);
			if (contig.getAnnotations() != null)
				Collections.sort(contig.getAnnotations());
		}
	}
	
	public void viewAligned( JavaFasta jf, boolean aligned ) {
		loadAligned( jf, aligned );
		jf.updateView();
	}
	
	public StringBuilder extractFastaWoGaps( String filename ) {
		JavaFasta jf = new JavaFasta( DataTable.this );
		jf.initDataStructures();
		loadAligned(jf, true);
		return jf.getFastaWoGaps();
	}
	
	public String extractFasta( String filename ) {
		JCheckBox species = new JCheckBox("Species");
		JCheckBox accession = new JCheckBox("Acc");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		Object[] params = new Object[] {species, accession, country, source};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);
		
		int start = 0;
		int stop = -1;
		if( currentjavafasta != null ) {
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
						String fname = "";
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
						} 
						
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
			} catch (IOException | ServiceException e1) {
				e1.printStackTrace();
			}
		}
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
				return 16;
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
				else if( columnIndex == 13 ) return "temp";
				else if( columnIndex == 14 ) return "pH";
				else if( columnIndex == 15 ) return "valid";
				//else if( columnIndex == 13 ) return "color";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 3 || columnIndex == 4 ) return Integer.class;
				else if( columnIndex == 13 || columnIndex == 14 ) return Double.class;
				else if( columnIndex == 15 ) return Boolean.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 11 || columnIndex == 15 ) return true;
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
				String[] specs = {"antranikianii","aquaticus","arciformis","brockianus","eggertsoni","filiformis","igniterrae","islandicus","kawarayensis","oshimai","scotoductus","thermophilus"};
				
				Map<String,Map<String,Long>>	specLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSpec = new TreeMap<String,Map<String,Long>>();
				for( Object[] row : rowList ) {
					String country = (String)row[11];
					if( country != null && country.length() > 0 ) {
						String species = (String)row[2];
						String thespec = null;
						for( String spec : specs ) {
							if( species.contains(spec) ) {
								thespec = "T."+spec;
								break;
							}
						}
						
						if( thespec != null ) {
							int len = (Integer)row[3];
							int id = (Integer)row[4];
							long idlen = (((long)len)<<32)+id;
							
							Map<String,Long> cmap;
							if( specLoc.containsKey( thespec ) ) {
								cmap = specLoc.get( thespec );
							} else {
								cmap = new TreeMap<String,Long>();
								specLoc.put( thespec, cmap );
							}
							
							if( cmap.containsKey(country) ) {
								long oldidlen = cmap.get(country);
								int oldid = (int)(oldidlen&0xFFFF);
								int oldlen = (int)(oldidlen>>32);
								
								if( id > oldid || (id == oldid && len > oldlen) ) cmap.put( country, idlen );
							} else cmap.put( country, idlen );
							
							Map<String,Long> smap;
							if( locSpec.containsKey( country ) ) {
								smap = locSpec.get( country );
							} else {
								smap = new TreeMap<String,Long>();
								locSpec.put( country, smap );
							}
							if( smap.containsKey(country) ) {
								long oldidlen = smap.get(country);
								int oldid = (int)(oldidlen&0xFFFF);
								int oldlen = (int)(oldidlen>>32);
								
								if( id > oldid || (id == oldid && len > oldlen) ) smap.put( thespec, idlen );
							} else smap.put( thespec, idlen );
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
						long idlen = smap.get(spec);
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>32);
						
						r = lSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( cnt );
						r.createCell(1).setCellValue( spec );
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
						long idlen = cmap.get(cnt);
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>32);
						r = sSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( spec );
						r.createCell(1).setCellValue( cnt );
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
						long idlen = smap.get(spec);
						int id = (int)(idlen&0xFF);
						int len = (int)(idlen>>32);
						
						int idx = -1;
						if( specIndex.containsKey(spec) ) idx = specIndex.get(spec);
						if( idx != -1 ) r.createCell(idx+1).setCellValue( id );
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
		Action action = new CopyAction( "Copy" );
		popup.add( action );
		popup.addSeparator();
		popup.add( new AbstractAction("NJTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				JavaFasta jf = new JavaFasta( DataTable.this );
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*/
				System.err.println( "erm" );
				loadAligned(jf, true);
				double[] corr = jf.distanceMatrixNumeric( false );
				List<String>	corrInd = jf.getNames();
				
				System.err.println( "befne" );
				
				TreeUtil	tu = new TreeUtil();
				tu.neighborJoin(corr, jf.getNumberOfSequences(), corrInd);
				String tree = tu.getNode().toString();
				System.err.println( tree );
				Object[] objs = { tree };
				JSObject win = JSObject.getWindow( DataTable.this );
				System.err.println( "bleh" );
				win.call("showTree", objs);
			}
		});
		popup.add( new AbstractAction("NJTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				JavaFasta jf = new JavaFasta( DataTable.this );
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*/
				loadAligned(jf, true);
				double[] corr = jf.distanceMatrixNumeric( true );
				List<String>	corrInd = jf.getNames();
				
				TreeUtil	tu = new TreeUtil();
				tu.neighborJoin(corr, jf.getNumberOfSequences(), corrInd);
				Object[] objs = { tu.getNode().toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("showTree", objs);
			}
		});
		popup.add( new AbstractAction("FastTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tree = extractFasta("/thermales.fasta");
				Object[] objs = { tree };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("fasttree", objs);
			}
		});
		popup.add( new AbstractAction("FastTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder tree = extractFastaWoGaps("/thermales.fasta");
				Object[] objs = { tree.toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("fasttree", objs);
			}
		});
		popup.add( new AbstractAction("Show conserved species sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JavaFasta jf = new JavaFasta( DataTable.this );
				JFrame frame = new JFrame();
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
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
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
		
		try {
			JSObject win = JSObject.getWindow(this);
			System.err.println( "about to run loadData" );
			win.call("loadData", new Object[] {});
			//System.err.println( "done loadData" );
			//win.call("loadMeta", new Object[] {});
		} catch( Exception e ) {}
		
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
		try {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			
			String ret = run("select rowid, ident from "+tableid+" where country like '%hile%' and species like '%filiform%'", true);
			String[] split = ret.split("\n");
			for( int i = 1; i < split.length; i++ ) {
				String row = split[i];
				String[] subsplit = row.split(",");
				int ident = Integer.parseInt( subsplit[1] );
				run( "update "+tableid+" set species = 'Thermus chileunknown', ident = "+(ident+4)+" where rowid = '"+subsplit[0]+"'", true );
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
			service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
		
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
					System.err.println( i + "  " + name + "  " + species.id + " of " + lines.length );
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
		
		System.err.println( tegmap.size() );
	}
}
