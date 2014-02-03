package org.simmi;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.simmi.shared.GBK2AminoFasta;
import org.simmi.shared.Sequence;
import org.simmi.shared.Sequences;
import org.simmi.shared.Serifier;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.shared.TreeUtil.NodeSet;
import org.simmi.signed.NativeRun;
import org.simmi.unsigned.JavaFasta;

public class SerifyApplet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTable				table;
	Serifier			serifier;
	String 				globaluser = null;
	NativeRun			nrun = new NativeRun();
	
	public SerifyApplet() {
		super();
		serifier = new Serifier();
		nrun = new NativeRun();
	}
	
	public List<Sequences> initSequences( int rowcount ) {		
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "getSequences", new Object[] {rowcount == 0} );
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}
		/*String[] split = seqsStr.split("\n");
		
		for( String ss : split ) {
			String[] s = ss.split("\t");
			seqs.add( new Sequences(s[0], s[1], s[2], s[3], Integer.parseInt(s[4]) ) );
		}*/
		
		return serifier.getSequencesList();
	}
	
	public String getMachine() {
		String hostinfo = "localhost\t1";
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			int val = Runtime.getRuntime().availableProcessors();
			
			hostinfo = hostname + "\t" + val;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostinfo;
	}
	
	private void initMachines() {
		String hostname = "localhost";
		int procs = 1;
		
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			procs = Runtime.getRuntime().availableProcessors();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "initMachines", new Object[] {hostname, procs} );
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}
	}
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	public TableModel createModel( final List<?> datalist, final Class cls ) {
		//System.err.println( cls );
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getType();
			}

			@Override
			public int getColumnCount() {
				int cc = cls.getDeclaredFields().length-1;
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "").replace("_", " ");
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;
				try {
					if( columnIndex >= 0 ) {
						Field f = cls.getDeclaredFields()[columnIndex];
						if( rowIndex >= datalist.size() ) {
							System.err.println( "out of b" );
						} else {
							System.err.println( datalist.size() );
							if( datalist.size() > 0 ) System.err.println( datalist.get(0) );
							
							Object obj = datalist.get(rowIndex);
							//System.err.println( obj );
							ret = f.get( obj );
							
							if( ret != null && ret.getClass() != f.getType() ) {
								//System.err.println( ret.getClass() + "  " + f.getType() );
								ret = null;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch( Exception e ) {
					e.printStackTrace();
				}
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				String userstr = this.getColumnCount() > 6 ? (String)this.getValueAt(rowIndex, 6) : null;
				String fname = f.getName();
				boolean editable = fname.startsWith("e_"); // && ( user.equals(userstr) || userCheck() );
				return editable;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				Object o = datalist.get( rowIndex );
				Field f = cls.getDeclaredFields()[columnIndex];
				try {
					f.set( o, aValue );
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public void console( String log ) {
		System.err.println( log );
	}
	
	public void init() {
		try {
			JSObject jso = JSObject.getWindow( this );
			final JSObject con = (JSObject)jso.getMember("console");
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}
		
		/*OutputStream o = new OutputStream() {
			Object[]	objs = {""};
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			
			@Override
			public void write(int b) throws IOException {
				if( b == '\n' ) {
					baos.write(b);
					objs[0] = baos.toString();
					baos.flush();
					baos.reset();
					con.call("log", objs);
				} else baos.write(b);
			}
		};
		PrintStream po = new PrintStream( o );
		System.setOut( po );
		System.setErr( po );*/
		
		init( this );
	}
	
	public void browse( final String url ) {
		//System.err.println("");
		
		AccessController.doPrivileged( new PrivilegedAction() {
			@Override
			public Object run() {
				try {
					URI uri = new URI( url );
					Desktop.getDesktop().browse( uri );
					Desktop.getDesktop().open( new File( uri ) );
					SerifyApplet.this.getAppletContext().showDocument( uri.toURL() );
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return null;
			}
		});
	}
	
	public Sequences getSequences( int i ) {
		return serifier.getSequencesList().get(i);
	}
	
	public void removeSequences( Sequences s ) {
		serifier.getSequencesList().remove( s );
	}
	
	public void removeAllSequences( Set<Sequences> ss ) {
		serifier.getSequencesList().removeAll( ss );
	}
	
	public void clearSequences() {
		serifier.getSequencesList().clear();
	}
	
	public void deleteSeqs() {
		Set<String>	keys = new TreeSet<String>();
		Set<Sequences>	rselset = new TreeSet<Sequences>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int ind = table.convertRowIndexToModel( r );
			if( ind >= 0 ) {
				Sequences seqs = getSequences(ind);
				rselset.add( seqs );
				if( seqs.getKey() != null ) keys.add( seqs.getKey() );
				
				//sequences.remove( ind );
				//table.tableChanged( new TableModelEvent(table.getModel()) );
			}
		}
		
		boolean unsucc = false;
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			for( String key : keys ) {
				js.call( "deleteSequenceKey", new Object[] {key} );
			}
		} catch( NoSuchMethodError | Exception e1 ) {
			unsucc = true;
		}
		
		if( unsucc ) {
			for( int r : rr ) {
				int ind = table.convertRowIndexToModel( r );
				rselset.add( getSequences(ind) );
			}
		}
		
		removeAllSequences( rselset );
		table.tableChanged( new TableModelEvent(table.getModel()) );
	}
	
	public void deleteSequence( String key ) {
		Sequences selseq = null;
		for( Sequences s : serifier.getSequencesList() ) {
			if( key.equals( s.getKey() ) ) selseq = s;
		}
		if( selseq != null ) {
			removeSequences( selseq );		
			table.tableChanged( new TableModelEvent(table.getModel()) );
		}
	}
	
	public static void tagsplit( Map<String,String> tagmap, Sequences seqs, File dir, SerifyApplet applet ) {
		try {
			File inf = new File( new URI(seqs.getPath() ) );
			String name = inf.getName();
			int ind = name.lastIndexOf('.');
			
			String sff = name;
			String sf2 = "";
			if( ind != -1 ) {
				sff = name.substring(0, ind);
				sf2 = name.substring(ind+1,name.length());
			}
			
			Map<String,String>		urlmap = new HashMap<String,String>();
			Map<String,FileWriter> fwmap = new HashMap<String,FileWriter>();
			StringBuilder			include = new StringBuilder();
			int i = 0;
			String			current = null;
			//FileWriter 		fw = null;
			//File			of = null;
			FileReader 		fr = new FileReader( inf );
			BufferedReader 	br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( current != null ) {
						String all = include.toString();
						for( String key : tagmap.keySet() ) {
							if( all.startsWith(key) ) {
								String tagname = tagmap.get(key);
								FileWriter fw;
								if( fwmap.containsKey(key) ) {
									fw = fwmap.get(key);
								} else {
									File of = new File( dir, tagname+".fna" );
									fw = new FileWriter( of );
									fwmap.put( key, fw );
									urlmap.put( key, of.toURI().toString() );
								}
								fw.write( current + "\n" + all );
								break;
							}
						}
					}
					current = line;
					include.delete(0, include.length());
				} else {
					include.append( line+"\n" );
				}
				
				line = br.readLine();
			}
			br.close();
			String all = include.toString();
			for( String key : tagmap.keySet() ) {
				if( all.startsWith(key) ) {
					String tagname = tagmap.get(key);
					FileWriter fw;
					if( fwmap.containsKey(key) ) {
						fw = fwmap.get(key);
					} else {
						File of = new File( dir, tagname+".fna" );
						fw = new FileWriter( of );
						fwmap.put( key, fw );
						urlmap.put( key, of.toURI().toString() );
					}
					fw.write( current + "\n" + all );
					break;
				}
			}
			
			for( String key : fwmap.keySet() ) {
				FileWriter fw = fwmap.get( key );
				fw.close();
				if( applet != null ) {
					name = tagmap.get(key);
					/*ind = name.lastIndexOf('.');
					name = name.substring(0,ind);*/
					applet.addSequences( name, urlmap.get( key ), null );
				}
			}					
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void getParameters( JSObject js ) {
		js.call( "getBlastParameters", new Object[] {} );
	}
	
	public static void blastRun( NativeRun nrun, String dbPath, String dbType, String extrapar, JTable table ) throws IOException {
		String userhome = System.getProperty("user.home");
		File dir = new File( userhome );
		
		System.out.println("run blast in applet");
		File blastn;
		File blastp;
		File blastx = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\blastx.exe" );
		if( !blastx.exists() ) {
			blastx = new File( "/opt/ncbi-blast-2.2.29+/bin/blastx" );
			blastn = new File( "/opt/ncbi-blast-2.2.29+/bin/blastn" );
			blastp = new File( "/opt/ncbi-blast-2.2.29+/bin/blastp" );
		} else {
			blastn = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\blastn.exe" );
			blastp = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\blastp.exe" );
		}
		if( blastx.exists() ) {
			String dbPathFixed = nrun.fixPath( dbPath );			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				if( !selectedfile.isDirectory() ) selectedfile = selectedfile.getParentFile();
			
				List<List<String>>	lscmd = new ArrayList<List<String>>();
				File makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\makeblastdb.exe" );
				if( !makeblastdb.exists() ) makeblastdb = new File( "/opt/ncbi-blast-2.2.29+/bin/makeblastdb" );
				if( makeblastdb.exists() ) {
					String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", dbPathFixed, "-dbtype", dbType };
					//nrun.runProcessBuilder( "Creating database", Arrays.asList( cmds ), null, null );
					lscmd.add( Arrays.asList( cmds ) );
				}
				
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String path = (String)table.getValueAt( r, 3 );
					String type = (String)table.getValueAt( r, 2 );
					
					//String blasttype = dbType.equals("nucl") ? type.equals("prot") ? "blastx" : "blastn" : "blastp";
					File blastFile = dbType.equals("prot") ? type.equals("prot") ? blastp : blastx : blastn;
					
					/*URL url = new URL( path );
					String file = url.getFile();
					String[] split = file.split("/");
					String fname = split[ split.length-1 ];
					split = fname.split("\\.");
					final String title = split[0];
					
					final File infile = new File( dir, "tmp_"+fname );
					
					FileOutputStream fos = new FileOutputStream( infile );
					InputStream is = url.openStream();
					
					byte[] bb = new byte[100000];
					r = is.read(bb);
					while( r > 0 ) {
						fos.write(bb, 0, r);
						r = is.read(bb);
					}
					is.close();
					fos.close();*/
			
					Path p = Paths.get( URI.create(path) );
					
					String queryPathFixed = nrun.fixPath( p.toAbsolutePath().toString() ).trim();
					final String outPathFixed = nrun.fixPath( new File( selectedfile, p.getFileName().toString()+".blastout" ).getAbsolutePath() ).trim();
					
					int procs = Runtime.getRuntime().availableProcessors();
					
					List<String>	lcmd = new ArrayList<String>();
					String[] cmds = { blastFile.getAbsolutePath(), "-query", queryPathFixed, "-db", dbPathFixed, "-num_threads", Integer.toString(procs) };
					String[] exts = extrapar.trim().split("[\t ]+");
					
					String[] nxst = { "-out", outPathFixed };
					lcmd.addAll( Arrays.asList(cmds) );
					if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
					lcmd.addAll( Arrays.asList(nxst) );
					
					lscmd.add( lcmd );
				}
				
				final String start = new Date( System.currentTimeMillis() ).toString();								
				final Object[] cont = new Object[3];
				Runnable run = new Runnable() {
					public void run() {										
						//infile.delete();
						//System.err.println( "ok " + (cont[0] == null ? "null" : "something else" ) );
						if( cont[0] != null ) {
							// ok JSObject js = JSObject.getWindow( SerifyApplet.this );
							//  String machineinfo = getMachine();
							//  String[] split = machineinfo.split("\t");
							//  js.call( "addResult", new Object[] {getUser(), title, outPathFixed, split[0], start, cont[2], cont[1]} );
						}
					}
				};
				/*for( String cmd : lcmd ) {
					System.err.println(cmd);
				}
				Thread.sleep(10000);*/
				nrun.runProcessBuilder( "Performing blast", lscmd, run, cont );
			}
		} else System.err.println( "no blast installed" );
	}
	
	public void runBlastInApplet( final String extrapar, final String dbPath, final String dbType ) {
		AccessController.doPrivileged( new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				try {
					blastRun( nrun, dbPath, dbType, extrapar, table );
				} catch( Exception e ) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
	}
	
	public void blastClusters( final InputStream is, final OutputStream os ) {
		final JDialog		dialog = new JDialog();
		final JProgressBar	pbar = new JProgressBar();
		Runnable run = new Runnable() {
			boolean interrupted = false;
			
			public void run() {
				dialog.addWindowListener( new WindowListener() {
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
						interrupted = true;
					}
					
					@Override
					public void windowActivated(WindowEvent e) {}
				});
				dialog.setVisible( true );
				
				if( !interrupted ) {
					serifier.makeBlastCluster( is, os, 0 );
				}
			}
		};
		nrun.runProcess( "Blast clusters", run, dialog, pbar );
	}
	
	public static class AvgProvider {
		List<String>	joinset;
		double			avg;
		
		public int count() {
			return joinset.size();
		}
		
		public AvgProvider( List<String> joinset, double avg ) {
			 this.joinset = joinset;
			 this.avg = avg;
		}
	};
	
	public final static class CountAverage implements Comparable<CountAverage> {
		int count;
		double avg;
		
		public CountAverage( int count, double avg ) {
			this.count = count;
			this.avg = avg;
		}

		@Override
		public int compareTo(CountAverage o) {
			int res = Integer.compare(count, o.count);
			if( res == 0 ) res = Double.compare(avg, o.avg);
			return res;
		}
	}
	
	public static void blastJoin( InputStream is, PrintStream ps ) throws IOException {
		InputStreamReader 		fr = new InputStreamReader( is );
		BufferedReader 	br = new BufferedReader( fr );
		String line = br.readLine();
		
		//PrintStream ps = new PrintStream( os );
		
		Map<String,Map<String,Set<String>>>	specmap = new HashMap<String,Map<String,Set<String>>>();
		
		String stuff = null;
		String subject = null;
		String length = null;
		String start = null;
		String stop = null;
		String score = null;
		String strand = null;
		
		String thespec = null;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				if( subject != null ) {
					int val = subject.indexOf('_');
					boolean valid = true;
					String spec = stuff.substring(0, stuff.indexOf('_'));
					if( val != -1 ) {
						String inspec = subject.substring(0, subject.indexOf('_'));
						valid = !spec.equals(inspec);
					}
					if( valid ) {
						Map<String,Set<String>>	contigmap;
						if( specmap.containsKey(spec) ) {
							contigmap = specmap.get(spec);
						} else {
							contigmap = new HashMap<String,Set<String>>();
							specmap.put(spec, contigmap );
						}
						
						Set<String>	hitmap;
						if( contigmap.containsKey( subject ) ) {
							hitmap = contigmap.get( subject );
						} else {
							hitmap = new HashSet<String>();
							contigmap.put( subject, hitmap );
						}
						hitmap.add( stuff + " " + start + " " + stop + " " + score + " " + strand );
					}
					
					subject = null;
				}
				stuff = line.substring(7).trim();
				if( thespec == null ) thespec = stuff.split("_")[0];
			} else if( line.startsWith("Length=") ) {
				length = line;
			} else if( line.startsWith(">") ) {
				if( subject != null ) {
					int val = subject.indexOf('_');
					boolean valid = true;
					String spec = stuff.substring(0, stuff.indexOf('_'));
					if( val != -1 ) {
						String inspec = subject.substring(0, subject.indexOf('_'));
						valid = !spec.equals(inspec);
					}
					if( valid ) {
						Map<String,Set<String>>	contigmap;
						if( specmap.containsKey(spec) ) {
							contigmap = specmap.get(spec);
						} else {
							contigmap = new HashMap<String,Set<String>>();
							specmap.put(spec, contigmap );
						}
						
						Set<String>	hitmap;
						if( contigmap.containsKey( subject ) ) {
							hitmap = contigmap.get( subject );
						} else {
							hitmap = new HashSet<String>();
							contigmap.put( subject, hitmap );
						}
						hitmap.add( stuff + " " + start + " " + stop + " " + score + " " + strand );
					}
				}
				length = null;
				start = null;
				stop = null;
				subject = line.substring(1).trim();
			} else if( line.startsWith("Sbjct") ) {
				String[] split = line.split("[\t ]+");
				if( start == null ) start = split[1];
				stop = split[split.length-1];
			} else if( length == null && subject != null ) {
				subject += line;
			} else if( line.startsWith(" Score") ) {				
				if( start != null ) {
					int val = subject.indexOf('_');
					boolean valid = true;
					String spec = stuff.substring(0, stuff.indexOf('_'));
					if( val != -1 ) {
						String inspec = subject.substring(0, subject.indexOf('_'));
						valid = !spec.equals(inspec);
					}
					if( valid ) {
						Map<String,Set<String>>	contigmap;
						if( specmap.containsKey(spec) ) {
							contigmap = specmap.get(spec);
						} else {
							contigmap = new HashMap<String,Set<String>>();
							specmap.put(spec, contigmap );
						}
						
						Set<String>	hitmap;
						if( contigmap.containsKey( subject ) ) {
							hitmap = contigmap.get( subject );
						} else {
							hitmap = new HashSet<String>();
							contigmap.put( subject, hitmap );
						}
						hitmap.add( stuff + " " + start + " " + stop + " " + score + " " + strand );
					}
				}
				score = line;
				start = null;
				stop = null;
			} else if( line.startsWith(" Strand") ) {
				strand = line;
			}
			
			line = br.readLine();
		}
		fr.close();
		
		for( String spec : specmap.keySet() ) {
			if( spec.contains( thespec ) ) {
				ps.println( spec );
				
				List<List<String>>	sortorder = new ArrayList<List<String>>();
				
				Map<List<String>,List<Double>>	joinMap = new HashMap<List<String>,List<Double>>();
				Map<String,Set<String>>	contigmap = specmap.get(spec);
				for( String contig : contigmap.keySet() ) {
					Set<String>	hitmap = contigmap.get(contig);
					List<String>	hitlist = new ArrayList<String>( hitmap );
					hitmap.clear();
					for( int i = 0; i < hitlist.size(); i++ ) {
						for( int x = i+1; x < hitlist.size(); x++ ) {
							String str1 = hitlist.get(i);
							String str2 = hitlist.get(x);
							
							boolean left1 = str1.contains("left");
							boolean left2 = str2.contains("left");
							boolean minus1 = str1.contains("Minus");
							boolean minus2 = str2.contains("Minus");
							boolean all1 = str1.contains("all");
							boolean all2 = str2.contains("all");
							
							if( (all1 || all2) || (left1 != left2 && minus1 == minus2) || (left1 == left2 && minus1 != minus2) ) {
								String[] split1 = str1.split("[\t ]+");
								String[] split2 = str2.split("[\t ]+");
								
								int start1 = Integer.parseInt( split1[1] );
								int stop1 = Integer.parseInt( split1[2] );
								int start2 = Integer.parseInt( split2[1] );
								int stop2 = Integer.parseInt( split2[2] );
								
								if( start1 > stop1 ) {
									int tmp = start1;
									start1 = stop1;
									stop1 = tmp;
								}
								
								if( start2 > stop2 ) {
									int tmp = start2;
									start2 = stop2;
									stop2 = tmp;
								}
								
								if( (stop2-start2 > 50) && (stop1-start1 > 50) && ((start2 > start1-150 && start2 < stop1+150) || (stop2 > start1-150 && stop2 < stop1+150)) ) {
									hitmap.add( str1 );
									hitmap.add( str2 );
									
									int jfn = str1.indexOf("Score =");
									int nxt = str1.indexOf("bits", jfn+8);
									double scr1 = 0;
									try {
										scr1 = Double.parseDouble( str1.substring(jfn+8, nxt).trim() );
									} catch( Exception e ) {
										System.err.println( str1 );
									}
									
									jfn = str2.indexOf("Score =");
									nxt = str2.indexOf("bits", jfn+8);
									double scr2 = 0;
									try {
										scr2 = Double.parseDouble( str2.substring(jfn+8, nxt).trim() );
									} catch( Exception e ) {
										System.err.println( str2 );
									}
									
									int ind1 = str1.indexOf("_left");
									if( ind1 == -1 ) ind1 = str1.indexOf("_right");
									if( ind1 == -1 ) ind1 = str1.indexOf("_all");
									String str1simple = str1.substring(0,ind1);
									String str1compl = str1.substring(0, str1.indexOf(' ', ind1));
									
									int ind2 = str2.indexOf("_left");
									if( ind2 == -1 ) ind2 = str2.indexOf("_right");
									if( ind2 == -1 ) ind2 = str2.indexOf("_all");
									String str2simple = str2.substring(0,ind2);
									String str2compl = str2.substring(0, str2.indexOf(' ', ind2));
									
									/*if( minus1 != minus2 ) {
										if( str1compl.compareTo( str2compl ) > 0 ) {
											str1compl += " Minus";
										} else str2compl += " Minus";
									}*/
									
									if( !str2simple.equals(str1simple) ) {
										List<String> joinset = new ArrayList<String>();
										joinset.add( str1compl );
										joinset.add( str2compl );
										Collections.sort( joinset );
										if( minus1 != minus2 ) joinset.set(1, joinset.get(1)+" Minus");
										
										List<Double>	cntList;
										if( joinMap.containsKey( joinset ) ) {
											cntList = joinMap.get(joinset);
										} else {
											cntList = new ArrayList<Double>();
											joinMap.put( joinset, cntList );
										}
										cntList.add( (scr1+scr2)/2.0 );
									}
								}
							}
						}
					}
					if( hitmap.size() > 1 ) {
						ps.println( "\t"+contig );
						for( String hit : hitmap ) {
							ps.println( "\t\t"+hit );
						}
					}
				}

				ps.println("Printing join count");
				Map<CountAverage,List<AvgProvider>>	reverseset = new TreeMap<CountAverage,List<AvgProvider>>( Collections.reverseOrder() );
				for( List<String> joinset : joinMap.keySet() ) {
					List<Double> cntList = joinMap.get(joinset);
					int cnt = cntList.size();
					double avg = 0;
					for( double val : cntList ) {
						avg += val;
					}
					avg /= cnt;
					CountAverage cntavg = new CountAverage(cnt,avg);
					
					if( joinset.get(0).contains("all") || joinset.get(1).contains("all") ) cnt -= 1000; 
					
					if( reverseset.containsKey( cntavg ) ) {
						List<AvgProvider>	joinlist = reverseset.get(cntavg);
						joinlist.add( new AvgProvider( joinset, avg ) );
					} else {
						List<AvgProvider> joinlist = new ArrayList<AvgProvider>();
						joinlist.add( new AvgProvider( joinset, avg ) );
						reverseset.put( new CountAverage(cnt, avg), joinlist);
					}
				}
				
				for( CountAverage cnt : reverseset.keySet() ) {
					List<AvgProvider>	joinlist = reverseset.get(cnt);
					for( AvgProvider avgp : joinlist ) {
						ps.println( avgp.joinset + " " + avgp.avg + ": " + cnt.count);
					}
				}
					
					for( CountAverage cnt : reverseset.keySet() ) {
						List<AvgProvider>	joinlist = reverseset.get(cnt);
						for( AvgProvider avgp : joinlist ) {
							List<String>	joinset = avgp.joinset;
							String str1 = joinset.get(0);
							String str2 = joinset.get(1);
							
							/*for( String joinstr : joinset ) {
								if( str1 == null ) str1 = joinstr;
								else {
									str2 = joinstr;
									break;
								}
							}*/
							
							boolean minus1 = str1.contains("Minus");
							str1 = str1.replace(" Minus", "");
							str2 = str2.replace(" Minus", "");
							//boolean minus1 = str1.contains("Minus");
							//String str1com = str1.substring(0,str1.lastIndexOf('_'));
							//String str2simple = str1.substring(0,str2.lastIndexOf('_'));
							String str1simple = str1.substring(0,str1.lastIndexOf('_'));
							String str2simple = str2.substring(0,str2.lastIndexOf('_'));
							
									List<String>	seqlist1 = null;
									List<String>	seqlist2 = null;								
									//boolean both = false;
									for( List<String> sl : sortorder ) {
										for( String seq : sl ) {
											/*if( seq.contains(str1simple) && seq.contains(str2simple) ) {
												seqlist1 = sl;
												seqlist2 = sl;
											} else*/
											
											if( seq.contains(str1simple) ) {
												if( seqlist1 == null ) seqlist1 = sl;
											} else if( seq.contains(str2simple) ) {
												if( seqlist2 == null ) seqlist2 = sl;
											}
										}
										if( seqlist1 != null && seqlist2 != null ) break;
									}
									
									/*for( List<String> sl1 : sortorder ) {
										for( List<String> sl2 : sortorder ) {
											if( sl1 != sl2 ) {
												for( String str : sl1 ) {
													if( sl2.contains(str) ) {
														System.err.println( str );
														System.err.println();
														for( String s1 : sl1 ) {
															System.err.println( s1 );
														}
														System.err.println();
														for( String s2 : sl2 ) {
															System.err.println( s2 );
														}
														System.err.println();
													}
												}
											}
										}
									}
									
									int count = 0;
									if( seqlist1 != null ) {
										for( String s : seqlist1 ) {
											if( s.contains("00006") ) count++;
											else if( s.contains("00034") ) count++;
											
											if( count == 2 ) {
												System.err.println();
											}
										}
									}
									
									if( seqlist2 != null ) {
										for( String s : seqlist2 ) {
											if( s.contains("00006") ) count++;
											else if( s.contains("00034") ) count++;
											
											if( count == 2 ) {
												System.err.println();
											}
										}
									}*/
									
									boolean left1 = str1.contains("left");
									boolean left2 = str2.contains("left");
									
									if( seqlist1 == null && seqlist2 == null ) {
										List<String> seqlist = new ArrayList<String>();
										sortorder.add( seqlist );
										
										if( left1 ) {
											if( left2 ) {
												if( minus1 ) {
													seqlist.add( str1+" reverse" );
													seqlist.add( str2 );
												} else {
													seqlist.add( str2+" reverse" );
													seqlist.add( str1 );
												}
											} else {
												if( minus1 ) {
													seqlist.add( str1+" reverse" );
													seqlist.add( str2+" reverse" );
												} else {
													seqlist.add( str2 );
													seqlist.add( str1 );
												}
											}
										} else {
											if( left2 ) {
												if( minus1 ) {
													seqlist.add( str2+" reverse" );
													seqlist.add( str1+" reverse" );
												} else {
													seqlist.add( str1 );
													seqlist.add( str2 );
												}
											} else {
												if( minus1 ) {
													seqlist.add( str2 );
													seqlist.add( str1+" reverse" );
												} else {
													seqlist.add( str1 );
													seqlist.add( str2+" reverse" );
												}
											}
										}
									} else if( (seqlist1 == null && seqlist2 != null) || (seqlist1 != null && seqlist2 == null) ) {
										List<String>	seqlist;
										String selseq = null;
										String noseq = null;
										
										int ind = -1;
										if( seqlist1 == null ) {
											seqlist = seqlist2;
											selseq = str2;
											noseq = str1;
											
											String seqf = seqlist.get(0);
											String seql = seqlist.get( seqlist.size()-1 );
											boolean bf = true; //(seqf.contains("left") && !seqf.contains("reverse")) || (!seqf.contains("left") && seqf.contains("reverse"));
											boolean bl = true; //(seql.contains("left") && seql.contains("reverse")) || (!seql.contains("left") && !seql.contains("reverse"));
											
 											if( seqf.contains(str2simple) && bf ) ind = 0;
											else if( seql.contains(str2simple) && bl ) ind = seqlist.size()-1;
										} else {
											seqlist = seqlist1;
											selseq = str1;
											noseq = str2;
											
											String seqf = seqlist.get(0);
											String seql = seqlist.get( seqlist.size()-1 );
											boolean bf = true; //(seqf.contains("left") && !seqf.contains("reverse")) || (!seqf.contains("left") && seqf.contains("reverse"));
											boolean bl = true; //(seql.contains("left") && seql.contains("reverse")) || (!seql.contains("left") && !seql.contains("reverse"));
											
											if( seqf.contains(str1simple) && bf ) ind = 0;
											else if( seql.contains(str1simple) && bl ) ind = seqlist.size()-1;
										}
										
										if( ind != -1 ) {
											String tstr = seqlist.get(ind);
											boolean leftbef = tstr.contains("left");
											boolean leftaft = selseq.contains("left");
											boolean allaft = false;//selseq.contains("all");
											boolean revbef = tstr.contains("reverse");
											//boolean revaft = selseq.contains("reverse");
											
											boolean leftno = noseq.contains("left");
											//boolean revno = selseq.contains("reverse");
											
											if( leftbef && revbef) {
												if( leftaft ) {
													if( ind == seqlist.size()-1 || allaft ) {
														if( leftno ) seqlist.add( seqlist.size(), noseq );
														else seqlist.add( seqlist.size(), noseq+" reverse" );
													}
												} else {
													if( ind == 0 || allaft ) {
														seqlist.add( 0, selseq+" reverse" );
														if( leftno ) seqlist.add( 0, noseq+" reverse" );
														else seqlist.add( 0, noseq );
													}
												}
											} else if( !leftbef && !revbef ) {
												if( leftaft ) {
													if( ind == 0 || allaft ) {
														seqlist.add( 0, selseq );
														if( leftno ) seqlist.add( 0, noseq+" reverse" );
														else seqlist.add( 0, noseq );
													}
												} else {
													if( ind == seqlist.size()-1 || allaft ) {
														if( leftno ) seqlist.add( seqlist.size(), noseq );
														else seqlist.add( seqlist.size(), noseq+" reverse" );
													}
												}
											} else if( !leftbef && revbef ) {
												if( leftaft ) {
													if( ind == seqlist.size()-1 || allaft ) {
														seqlist.add( seqlist.size(), selseq+" reverse" );
														if( leftno ) seqlist.add( seqlist.size(), noseq );
														else seqlist.add( seqlist.size(), noseq+" reverse" );
													}
												} else {
													if( ind == 0 || allaft ) {
														if( leftno ) seqlist.add( 0, noseq+" reverse" );
														else seqlist.add( 0, noseq );
													}
												}
												
												//if( leftno ) seqlist.add( 0, noseq+" reverse" );
												//else seqlist.add( 0, noseq );
											} else if( leftbef && !revbef ) {
												if( leftaft ) {
													if( ind == 0 || allaft ) {
														if( leftno ) seqlist.add( 0, noseq+" reverse" );
														else seqlist.add( 0, noseq );
													}
												} else {
													if( ind == seqlist.size()-1 || allaft ) {
														seqlist.add( seqlist.size(), selseq );
														if( leftno ) seqlist.add( seqlist.size(), noseq );
														else seqlist.add( seqlist.size(), noseq+" reverse" );
													}
												}
												
												//if( leftno ) seqlist.add( 0, noseq+" reverse" );
												//else seqlist.add( 0, noseq );
											}
											
											/*if( selseq.contains(str1simple) ) {
												if( selseq.contains("reverse ") ) {
													if( left1 ) {
														if( left2 ) {
															seqlist.add( ind+1, str2 );
														} else {
															seqlist.add( ind+1, str2+" reverse" );
														}
													} else {
														if( left2 ) {
															seqlist.add( ind, str2+" reverse" );
														} else {
															seqlist.add( ind, str2 );
														}
													}
												} else {
													if( left1 ) {
														if( left2 ) {
															seqlist.add( ind, str2+" reverse" );
														} else {
															seqlist.add( ind, str2 );
														}
													} else {
														if( left2 ) {
															seqlist.add( ind+1, str2 );
														} else {
															seqlist.add( ind+1, str2+" reverse" );
														}
													}
												}
											} else {
												if( selseq.contains("reverse ") ) {
													if( left1 ) {
														if( left2 ) {
															seqlist.add( ind+1, str1 );
														} else {
															seqlist.add( ind, str1+" reverse" );
														}
													} else {
														if( left2 ) {
															seqlist.add( ind+1, str1+" reverse" );
														} else {
															seqlist.add( ind, str1 );
														}
													}
												} else {
													if( left1 ) {
														if( left2 ) {
															seqlist.add( ind, str1+" reverse" );
														} else {
															seqlist.add( ind+1, str1 );
														}
													} else {
														if( left2 ) {
															seqlist.add( ind, str1 );
														} else {
															seqlist.add( ind+1, str1+" reverse" );
														}
													}
												}
											}*/
										}
									} else if( seqlist1 != seqlist2 ) {
										String selseq1 = null;
										String selseq2 = null;
										
										int ind1 = -1;
										if( seqlist1.get(0).contains(str1simple) ) {
											ind1 = 0;
											selseq1 = seqlist1.get(0);
										} else if( seqlist1.get( seqlist1.size()-1 ).contains(str1simple) ) {
											ind1 = seqlist1.size()-1;
											selseq1 = seqlist1.get( seqlist1.size()-1 );
										}
										
										int ind2 = -1;
										if( seqlist2.get(0).contains(str2simple) ) {
											ind2 = 0;
											selseq2 = seqlist2.get(0);
										} else if( seqlist2.get( seqlist2.size()-1 ).contains(str2simple) ) {
											ind2 = seqlist2.size()-1;
											selseq2 = seqlist2.get( seqlist2.size()-1 );
										}
										
										boolean success = false;
										
										if( selseq1 == null || selseq2 == null ) {
											System.err.println("bleh");
										} else {											
											System.err.println( "joining: " + seqlist1 );
											System.err.println( "and: " + seqlist2 );
										
											boolean lef1 = selseq1.contains("left");
											boolean lef2 = selseq2.contains("left");
											boolean rev1 = selseq1.contains("reverse");
											boolean rev2 = selseq2.contains("reverse");
											
											boolean bb = false;
											if( bb ) {
												System.err.println("subleh");
											} else {
												if( lef1 && !left1 ) {
													if( rev1 && ind1 == 0 ) seqlist1.add( 0, str1+" reverse" );
													else if( ind1 == seqlist1.size()-1 ) seqlist1.add( seqlist1.size(), str1 );
												} else if( !lef1 && left1 ) {
													if( rev1 && ind1 == seqlist1.size()-1 ) seqlist1.add( seqlist1.size(), str1+" reverse" );
													else if( ind1 == 0 ) seqlist1.add( 0, str1 );
												}
												
												if( lef2 && !left2 ) {
													if( rev2 && ind2 == 0 ) seqlist2.add( 0, str2+" reverse" );
													else if( ind2 == seqlist2.size()-1 ) seqlist2.add( seqlist2.size(), str2 );
												} else if( !lef2 && left2 ) {
													if( rev2 && ind2 == seqlist2.size()-1 ) seqlist2.add( seqlist2.size(), str2+" reverse" );
													else if( ind2 == 0 ) seqlist2.add( 0, str2 );
												}
												
												boolean left1beg = seqlist1.get(0).contains("left");
												boolean rev1beg = seqlist1.get(0).contains("reverse");
												boolean left1end = seqlist1.get(seqlist1.size()-1).contains("left");
												boolean rev1end = seqlist1.get(seqlist1.size()-1).contains("reverse");
												
												boolean left2beg = seqlist2.get(0).contains("left");
												boolean rev2beg = seqlist2.get(0).contains("reverse");
												boolean left2end = seqlist2.get(seqlist2.size()-1).contains("left");
												boolean rev2end = seqlist2.get(seqlist2.size()-1).contains("reverse");
												
												if( seqlist1.get(0).contains(str1simple) ) {
													if( seqlist2.get(0).contains(str2simple) ) {
														if( ((left1beg && !rev1beg) || (!left1beg && rev1beg)) && (((left2beg && !rev2beg) || (!left2beg && rev2beg))) ) {
															Collections.reverse( seqlist2 );
															for( int u = 0; u < seqlist2.size(); u++ ) {
																String val = seqlist2.get(u);
																if( val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "") );
																else {
																	int end = val.length()-1;
																	while( val.charAt(end) == 'I' ) end--;
																	seqlist2.set(u, val.substring(0, end+1)+" reverse"+val.substring(end+1, val.length()) );
																}
															}
															success = true;
															seqlist1.addAll(0, seqlist2);
														}
													} else {
														if( ((left1beg && !rev1beg) || (!left1beg && rev1beg)) && (((left2end && rev2end) || (!left2end && !rev2end))) ) {
															success = true;
															seqlist1.addAll(0, seqlist2);
														}
													}
												} else { //if( seqlist1.indexOf(str1) == seqlist1.size()-1 ) {
													if( seqlist2.get(0).contains(str2simple) ) {
														if( ((left1end && rev1end) || (!left1end && !rev1end)) && (((left2beg && !rev2beg) || (!left2beg && rev2beg))) ) {
															success = true;
															seqlist1.addAll(seqlist1.size(), seqlist2);
														}
													} else {
														if( ((left1end && rev1end) || (!left1end && !rev1end)) && (((left2end && rev2end) || (!left2end && !rev2end))) ) {
															Collections.reverse( seqlist2 );
															for( int u = 0; u < seqlist2.size(); u++ ) {
																String val = seqlist2.get(u);
																if( val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "") );
																else {
																	int end = val.length()-1;
																	while( val.charAt(end) == 'I' ) end--;
																	seqlist2.set(u, val.substring(0, end+1)+" reverse"+val.substring(end+1, val.length()) );
																}
															}
															success = true;
															seqlist1.addAll(seqlist1.size(), seqlist2);
														}
													}
												}
												
												if( success ) {
													if( !sortorder.remove( seqlist2 ) ) {
														System.err.println("no remove");
													}
												}
												System.err.println("result is: "+seqlist1);
											}
										}
									} else {
										System.err.println( "same shit " + seqlist1 + " " + str1 + " " + str2 );
										/*for( int k = 0; k < seqlist1.size(); k++ ) {
											if( seqlist1.get(k).contains(str1) ) seqlist1.set(k, seqlist1.get(k)+"I");
											else if( seqlist1.get(k).contains(str2) ) seqlist1.set(k, seqlist1.get(k)+"I");
										}*/
										int i = 0;
										i = 2;
									}
								}
							//}
						//}
					//}
				}
				
				ps.println("join");
				for( List<String> so : sortorder ) {
					for( int i = 0; i < so.size(); i++ ) {
						String s = so.get(i);
						String ss = s.substring(0, s.indexOf("_contig")+12);
						if( i == so.size()-1 ) {
							ps.println( ss + (s.contains("everse") ? "_reverse" : "") );
						} else {
							String n = so.get(i+1);
							String nn = n.substring(0, n.indexOf("_contig")+12);
							if( ss.equals(nn) ) {
								ps.println( ss + (s.contains("everse") ? "_reverse" : "") );
								i++;
							} else {
								ps.println( ss + (s.contains("everse") ? "_reverse" : "") );
							}
						}
					}
					/*for( String s : so ) {
						System.out.println(s);
					}*/
					ps.println();
				}
			}
		}
	}
	
	public int flankingFasta( InputStream is, OutputStream os ) throws IOException {        
        OutputStreamWriter 		fw = new OutputStreamWriter( os );
        BufferedWriter			bw = new BufferedWriter( fw );
        
        int fasti = 60;
        int nseq = 0;
		InputStreamReader fr = new InputStreamReader( is );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		String seqname = null;
		StringBuilder	seq = new StringBuilder();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( seqname != null ) {
					if( seq.length() < 200 ) {
						fw.write( seqname+"_all"+"\n" );
						for( int i = 0; i < seq.length(); i+=fasti ) {
							fw.write( seq.substring( i, Math.min( seq.length(), i+fasti ) )+"\n" );
						}
						nseq++;
					} else {
						fw.write( seqname+"_left"+"\n" );
						for( int i = 0; i < 150; i+=fasti ) {
							fw.write( seq.substring( i, Math.min( 150, i+fasti ) )+"\n" );
						}
						nseq++;
						fw.write( seqname+"_right"+"\n" );
						for( int i = seq.length()-151; i < seq.length(); i+=fasti ) {
							fw.write( seq.substring( i, Math.min( seq.length(), i+fasti ) )+"\n" );
						}
						nseq++;
					}
				}
				int endind = line.indexOf(' ');
				if( endind == -1 ) endind = line.indexOf('\t');
				if( endind == -1 ) endind = line.length();
				seqname = line.substring(0, endind);
				seq = new StringBuilder();
			} else {
				seq.append( line );
			}
			
			line = br.readLine();
		}
		fr.close();
		
		if( seqname != null ) {
			if( seq.length() < 200 ) {
				fw.write( seqname+"_all"+"\n" );
				for( int i = 0; i < seq.length(); i+=fasti ) {
					fw.write( seq.substring( i, Math.min( seq.length(), i+fasti ) )+"\n" );
				}
				nseq++;
			} else {
				fw.write( seqname+"_left"+"\n" );
				for( int i = 0; i < 150; i+=fasti ) {
					fw.write( seq.substring( i, Math.min( 150, i+fasti ) )+"\n" );
				}
				nseq++;
				fw.write( seqname+"_right"+"\n" );
				for( int i = seq.length()-151; i < seq.length(); i+=fasti ) {
					fw.write( seq.substring( i, Math.min( seq.length(), i+fasti ) )+"\n" );
				}
				nseq++;
			}
		}
		
		bw.flush();
		fw.close();
		
		return nseq;
	}
		
	public static void cutFasta( BufferedReader br, BufferedWriter bw, char ch ) throws IOException {
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				bw.write( line.substring( 0, line.indexOf(ch) )+"\n" );
			} else {
				bw.write( line+"\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		bw.flush();
		bw.close();
	}
	
	public static double correlateDistance(double[] dmat1, double[] dmat2) {
		double ret = 0.0;
		
		for( int i = 0; i < dmat1.length; i++ ) {
			double dif = dmat1[i]-dmat2[i];
			ret += dif*dif;
		}
		
		return Math.sqrt( ret );
	}
	
	public void totalTrim( File dir, Object fset ) throws URISyntaxException, MalformedURLException, IOException {
		String path = null;
		String type = null;
		String trimname = null;
		
		int nseq = 0;
		BufferedWriter bw = null;
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int rrr = table.convertRowIndexToModel( r );
			final Sequences seqs = getSequences( rrr );
			URI uri = new URI( seqs.getPath() );
			InputStream is = uri.toURL().openStream();
			
			if( seqs.getPath().endsWith(".gz") ) {
				is = new GZIPInputStream( is );
			}
			
			if( bw == null ) {
				URL url = uri.toURL();
				String urlstr = url.toString();
				String[] erm = urlstr.split("\\/");
				String name = erm[ erm.length-1 ];
				int ind = name.lastIndexOf('.');
				
				String sff = name;
				String sf2 = "";
				if( ind != -1 ) {
					sff = name.substring(0, ind);
					sf2 = name.substring(ind+1,name.length());
				}
				
				trimname = sff+"_trimmed";
				File f = new File( dir, trimname+"."+sf2 );
				path = f.toURI().toString();
				type = seqs.getType();
				FileWriter fw = new FileWriter(f);
				
				bw = new BufferedWriter( fw );
			}
			
			nseq += serifier.trimFasta( new BufferedReader( new InputStreamReader( is ) ), bw, fset, false, false );
		}
		if( bw != null ) {
			bw.flush();
			bw.close();
		}
		
		SerifyApplet.this.addSequences(trimname, type, path, nseq);
	}
	
	public void loadSequencesInJavaFasta( JavaFasta jf ) {
		Map<String, Sequence> contset = new HashMap<String, Sequence>();
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int cr = table.convertRowIndexToModel(r);
			Sequences seqs = getSequences(cr);
			
			//int nseq = 0;
			serifier.appendSequenceInJavaFasta( seqs, contset, rr.length == 1 );
						/*Annotation a = jf.new Annotation(seq, contig, Color.red);
						a.setStart(tv.start);
						a.setStop(tv.stop);
						a.setOri(tv.ori);
						a.setGroup(gg.name);
						a.setType("gene");
						jf.addAnnotation(a);
						// seq.addAnnotation( new Annotation( seq, ) );*/
		}

		for (String contig : contset.keySet()) {
			Sequence seq = contset.get(contig);
			//serifier.addSequence(seq);
			if (seq.getAnnotations() != null)
				Collections.sort(seq.getAnnotations());
		}
	}
		
	public void trim( File dir, String trim ) {
		try {
			Map<String,String> fset = serifier.makeFset( trim );
			totalTrim( dir, fset );
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}
	
	public void load() {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JavaFasta jf = new JavaFasta( SerifyApplet.this, serifier, null );
		jf.initGui(frame);
		serifier.clearAll();
		loadSequencesInJavaFasta( jf );
		jf.updateView();

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
				if( jf.isEdited() && JOptionPane.showConfirmDialog( SerifyApplet.this, "Do you wan't to save?" ) == JOptionPane.YES_OPTION ) {
					JFileChooser jfc = new JFileChooser();
					if( jfc.showSaveDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						try {
							File f = jfc.getSelectedFile();
							FileWriter fw = new FileWriter( f );
							serifier.writeFasta( serifier.lseq, fw, jf.getSelectedRect() );
							fw.close();
							
							SerifyApplet.this.addSequences( f.getName(), f.toURI().toURL().toString(), null );
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		frame.setVisible(true);
	}
	
	public void init( final Container c ) {
		nrun.cnt = c;
		globaluser = System.getProperty("user.name");
		
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			globaluser = (String)js.call("getUser", new Object[] {});
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}
		
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
		
		Window window = SwingUtilities.windowForComponent( c );
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		//final String[] 	columns = new String[] {"user", "name", "path", "type", "#seq"};
		//final Class[]	types = new Class[] {String.class, String.class, String.class, String.class, Integer.class};
		
		Color bgcolor = Color.white;
		SerifyApplet.this.getContentPane().setBackground(bgcolor);
		SerifyApplet.this.setBackground(bgcolor);
		SerifyApplet.this.getRootPane().setBackground(bgcolor);
		
		table = new JTable();
		//table.setBackground( bgcolor );
		
		List<Sequences> sequences = initSequences( table.getRowCount() );
		initMachines();
		table.setAutoCreateRowSorter( true );
		serifier.setSequencesList( sequences );
		TableModel model = createModel( sequences, Sequences.class );
		table.setModel( model );
		
		Field[] odecl = Sequences.class.getDeclaredFields();
		Set<TableColumn>			remcol = new HashSet<TableColumn>();
		Enumeration<TableColumn>	taben = table.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = odecl[tc.getModelIndex()].getName();
			System.err.println( name );
			if( name.startsWith("_") ) {
				remcol.add( tc );
			}
		}
		for( TableColumn tc : remcol ) {
			table.removeColumn( tc );
		}
		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				if( me.getClickCount() == 2 ) {
					load();
					
					/*int r = table.getSelectedRow();
					String path = (String)table.getValueAt( r, 3 );
					
					JFrame frame = new JFrame();
					frame.setSize(800, 600);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					
					JavaFasta jf = new JavaFasta( c instanceof JApplet ? (JApplet)c : null, serifier, null );
					jf.initGui(frame);
					jf.updateView();

					frame.setVisible(true);
					
					/*try {
						SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}*/
					
					//browse( path );
				}
			}
		});
		table.addKeyListener( new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if( keycode == KeyEvent.VK_DELETE ) {
					deleteSeqs();
				} else if( keycode == KeyEvent.VK_ENTER ) {
					int r = table.getSelectedRow();
					String path = (String)table.getValueAt( r, 3 );
					
					/*try {
						SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}*/
					
					browse( path );
				}
			}
		});
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("NCBI Fetch") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	filechooser = new JFileChooser();
				filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
					File cd = filechooser.getSelectedFile();
					
					final JCheckBox	whole = new JCheckBox("whole");
					whole.setSelected( true );
					final JCheckBox	draft = new JCheckBox("draft");
					draft.setSelected( true );
					final JCheckBox	phage = new JCheckBox("phage");
					phage.setSelected( true );
					final JTextArea ta = new JTextArea();
					JScrollPane	sp = new JScrollPane( ta );
					Dimension dim = new Dimension(400,300);
					sp.setPreferredSize( dim );
					sp.setSize( dim );
					JOptionPane.showMessageDialog(c, new Object[] {whole, draft, phage,"Filter term",sp});
					final Map<String,String> searchmap = new HashMap<String,String>();
					String searchstr = ta.getText();
					String[] split = searchstr.split("\n");
					for( String strsearch : split ) {
						String[] subsplit = strsearch.split("\t");
						if( subsplit.length > 1 ) searchmap.put( subsplit[0], subsplit[1] );
						else searchmap.put( strsearch, null );
					}
					
					final String basesave =  cd.getAbsolutePath();
					final String uripath = cd.toURI().toString();
					//final String replace = subsplit.length > 1 && subsplit[1].length() > 0 ? subsplit[1] : null;
					
					final JDialog		dialog = new JDialog();
					final JProgressBar	pbar = new JProgressBar();
					
					Runnable run = new Runnable() {
						boolean interrupted = false;
						
						public void run() {
							dialog.addWindowListener( new WindowListener() {
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
									//interrupted = true;
								}
								
								@Override
								public void windowActivated(WindowEvent e) {}
							});
							dialog.setVisible( true );
							
							//String ftpsite = "ftp.rhnet.is";
							String ftpsite = "ftp.ncbi.nlm.nih.gov";
							//FTPClientConfig ftpcc = new FTPClientConfig();
							FTPClient ftp = new FTPClient();
							try {
								ftp.connect( ftpsite );
								//ftp.enterLocalPassiveMode();
								ftp.login("anonymous", "");
								
								for( String search : searchmap.keySet() ) {
									String replace = searchmap.get(search);
									replace = replace == null || replace.length() == 0 ? null : replace;
									if( search.length() == 4 ) {
										String subdir = "/genbank/wgs/";
										ftp.cwd( subdir );
										String fwname = search+".gbk";
										
										String filename = "wgs."+search+".1.gbff.gz";
										/*File gfile = new File(basesave, fwname+".gz");
										FileOutputStream fos = new FileOutputStream( gfile );
										ftp.retrieveFile("wgs."+search+".1.fsa_nt.gz",fos);
										fos.close();*/
										
										File thefile = new File( basesave, fwname );
										if( !thefile.exists() ) {
											FileWriter fw = new FileWriter( thefile );
											URL url = new URL( "ftp://"+ftpsite+subdir+filename );
											InputStream is = new GZIPInputStream( url.openStream() );//ftp.retrieveFileStream( newfname );
											BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
											String line = br.readLine();
											while( line != null ) {
												fw.write( line + "\n" );
												line = br.readLine();
											}
											br.close();
											is.close();
											//ftp.completePendingCommand();
											fw.close();
										}
										
										try {
											Map<String,String>	urimap = new HashMap<String,String>();
											urimap.put( fwname.substring(0, fwname.length()-4), thefile.toURI().toString() );
											addSequencesPath( fwname, urimap, uripath, replace );
										} catch (URISyntaxException e) {
											e.printStackTrace();
										}
										//FTPFile[] files = ftp.listFiles();
									} else {									
										if( whole.isSelected() ) {
											String subdir = "/genomes/Bacteria/";
											ftp.cwd( subdir );
											FTPFile[] files = ftp.listFiles();
											for( FTPFile ftpfile : files ) {
												if( interrupted ) break;
												if( ftpfile.isDirectory() ) {
													String fname = ftpfile.getName();
													if( fname.startsWith( search ) ) {
														if( !ftp.isConnected() ) {
															ftp.connect("ftp.ncbi.nih.gov");
															ftp.login("anonymous", "");
														}
														ftp.cwd( subdir+fname );
														FTPFile[] newfiles = ftp.listFiles();
														//int cnt = 1;
														
														File thefile = new File( basesave, fname+".gbk" );
														if( !thefile.exists() ) {
															FileWriter fw = new FileWriter( thefile );
															
															for( FTPFile newftpfile : newfiles ) {
																if( interrupted ) break;
																
																String newfname = newftpfile.getName();
																if( newfname.endsWith(".gbk") ) {
																	//if( newftpfile != newfiles[0] ) fw.write("//\n");
																	//long size = newftpfile.getSize();
																	//String basename = fname;
																	//if( size > 3000000 ) basename = fname;//+".gbk";
																	//else basename = fname+"_p"+(cnt++);//+".gbk";
																	
																	//String fwname = basename+"_"+newfname;
																	//if( size > 1500000 ) fwname = fname+".fna";
																	//else fwname = fname+"_p"+(cnt++)+".fna";
																	
																	URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
																	InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
																	BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
																	String line = br.readLine();
																	while( line != null ) {
																		fw.write( line + "\n" );
																		line = br.readLine();
																	}
																	is.close();
																	//ftp.completePendingCommand();
																	//System.err.println("done " + fname);
																}
															}
															fw.close();
														}
														
														try {
															Map<String,String>	urimap = new HashMap<String,String>();
															urimap.put( fname, thefile.toURI().toString() );
															addSequencesPath( fname, urimap, thefile.toURI().toString(), replace );
														} catch (URISyntaxException e) {
															e.printStackTrace();
														}
													}
												}
											}
										}
										
										if( draft.isSelected() ) {
											String subdir = "/genomes/Bacteria_DRAFT/";
											FileSystemManager fsManager = VFS.getManager();
											byte[] bb = new byte[30000000];
											ftp.cwd( subdir );
											FTPFile[] files2 = ftp.listFiles();
											for( FTPFile ftpfile : files2 ) {
												if( interrupted ) break;
												if( ftpfile.isDirectory() ) {
													String fname = ftpfile.getName();
													if( fname.startsWith( search ) ) {
														ftp.cwd( subdir+fname );
														FTPFile[] newfiles = ftp.listFiles();
														
														File thefile = new File( basesave, fname+".gbk" );
														if( !thefile.exists() ) {
															FileOutputStream fos = new FileOutputStream( thefile );
															
															for( FTPFile newftpfile : newfiles ) {
																if( interrupted ) break;
																
																//String newfname = newftpfile.getName().getBaseName();
																String newfname = newftpfile.getName();
																if( newfname.endsWith("scaffold.gbk.tgz") ) {
																	//if( newftpfile != newfiles[0] ) fos.write( "//\n".getBytes() );
																	//long size = newftpfile.getSize();
																	//String fwname = "";
																	//if( size > 1500000 ) fwname = fname+".fna";
																	//else fwname = fname+"_p"+(cnt++)+".fna";
																	
																	//InputStream is = newftpfile.getContent().getInputStream();
																	URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
																	InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
																	System.err.println( "trying "+newfname + (is == null ? "critical" : "success" ) );
																	//InputStream gis = is;
																	GZIPInputStream gis = new GZIPInputStream( is );
																	
																	File file = new File( basesave, fname.substring(0,fname.length()-4)+".tar" );
																	if( !file.exists() && gis != null ) {
																		int r = gis.read(bb);
																		FileOutputStream tfos = new FileOutputStream( file );
																		while( r > 0 ) {
																			System.err.println( "reading " + r );
																			tfos.write( bb, 0, r );
																			
																			r = gis.read(bb);
																		}
																		gis.close();
																		tfos.close();
																	}
																	
																	//FileSystemManager fsManager = VFS.getManager();
																	FileObject jarFile = fsManager.resolveFile( "tar://"+file.getAbsolutePath() );
				
																	// List the children of the Jar file
																	FileObject[] children = jarFile.getChildren();
																	//System.out.println( "Children of " + jarFile.getName().getURI() );
																	//int contig = 1;
																	for ( int i = 0; i < children.length; i++ ) {
																		FileObject child = children[i];
																		
																		//if( i > 0 ) fos.write( "//\n".getBytes() );
																		//String childname = child.getName().getBaseName();
																		//int k = childname.indexOf(".gbk");
																		//if( k == -1 ) k = childname.length();
																		//String lfname = fname+"_contig"+(contig++)+"_"+childname;
																		FileContent fc = child.getContent();
																		InputStream sis = fc.getInputStream();
																		int r = sis.read( bb );
																		//int total = r;
																		
																		while( r != -1 ) {
																			fos.write( bb, 0, r );
																			r = sis.read( bb );
																			//total += r;
																		}
																		
																		/*try {
																			addSequences( lfname, thefile.toURI().toString() );
																		} catch (URISyntaxException e) {
																			e.printStackTrace();
																		}*/
																	}
																}
															}
															fos.close();
														}
														
														try {
															Map<String,String>	urimap = new HashMap<String,String>();
															urimap.put( fname, thefile.toURI().toString() );
															addSequencesPath( fname, urimap, thefile.toURI().toString(), replace );
														} catch (URISyntaxException e) {
															e.printStackTrace();
														}
													}
												}
											}
										}
										
										if( phage.isSelected() ) {
											String subdir = "/genomes/Viruses/";
											ftp.cwd( subdir );
											FTPFile[] files3 = ftp.listFiles();
											for( FTPFile ftpfile : files3 ) {
												if( interrupted ) break;
												if( ftpfile.isDirectory() ) {
													String fname = ftpfile.getName();
													if( fname.startsWith( search ) ) {//fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
														if( !ftp.isConnected() ) {
															ftp.connect("ftp.ncbi.nih.gov");
															ftp.login("anonymous", "anonymous");
														}
														ftp.cwd( subdir+fname );
														FTPFile[] newfiles = ftp.listFiles();
														int cnt = 1;
														
														File thefile = new File( basesave, fname+".gbk" );
														if( !thefile.exists() ) {
															FileWriter fw = new FileWriter( thefile );
															
															for( FTPFile newftpfile : newfiles ) {
																if( interrupted ) break;
																
																String newfname = newftpfile.getName();
																if( newfname.endsWith(".gbk") ) {
																	URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
																	InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
																	BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
																	String line = br.readLine();
																	while( line != null ) {
																		fw.write( line + "\n" );
																		line = br.readLine();
																	}
																	is.close();
																}
															}
															fw.close();
														}
														
														/*for( FTPFile newftpfile : newfiles ) {
															if( interrupted ) break;
															String newfname = newftpfile.getName();
															System.err.println("trying " + newfname + " in " + fname);
															if( newfname.endsWith(".gbk") ) {
																System.err.println("in " + fname);
																long size = newftpfile.getSize();
																String fwname = fname+"_"+newfname;
																
																File thefile = new File( basesave, fwname );
																if( !thefile.exists() ) {
																	FileWriter fw = new FileWriter( thefile );
																	URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
																	InputStream is = url.openStream(); //ftp.retrieveFileStream( newfname );
																	BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
																	String line = br.readLine();
																	while( line != null ) {
																		fw.write( line + "\n" );
																		line = br.readLine();
																	}
																	is.close();
																	fw.close();
																	System.err.println("done " + fname);
																}
																urimap.put( fwname.substring(0, fwname.length()-4), thefile.toURI().toString() );
																
																/*try {
																	addSequences( fwname, thefile.toURI().toString() );
																} catch (URISyntaxException e) {
																	e.printStackTrace();
																}*
															}
														}*/
														
														try {
															Map<String,String>	urimap = new HashMap<String,String>();
															urimap.put( fname, thefile.toURI().toString() );
															addSequencesPath( fname, urimap, thefile.toURI().toString(), replace );
														} catch (URISyntaxException e) {
															e.printStackTrace();
														}
														/*try {
															addSequencesPath( fname, urimap, uripath );
														} catch (URISyntaxException e) {
															e.printStackTrace();
														}*/
													}
												}
											}
										}
									}
								}
							} catch (SocketException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							pbar.setIndeterminate( false );
							pbar.setEnabled( false );
						}
					};					
					nrun.runProcess("NCBI Fetch", run, dialog, pbar);
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Import fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	filechooser = new JFileChooser();
				filechooser.setMultiSelectionEnabled( true );
				if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
					File cd = filechooser.getCurrentDirectory();
					String path = JOptionPane.showInputDialog("Select path", cd.toURI().toString());
					
					if( path != null ) {
						try {
							for( File f : filechooser.getSelectedFiles() ) {
								addSequences( f.getName(), path+f.getName(), null );
							}
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Concatenate") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	filechooser = new JFileChooser();
				if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
					final File f = filechooser.getSelectedFile();
					try {
						List<Reader>	lrd = new ArrayList<Reader>();
						int[] rr = table.getSelectedRows();
						for( int r : rr ) {
							String path = (String)table.getValueAt( r, 3 );
							URL url = new URL( path );
							lrd.add( new InputStreamReader( url.openStream() ) );
						}
						final Map<String,StringBuilder>	seqmap = serifier.concat( lrd );
						
						JFrame popup = new JFrame();
						popup.setSize(800, 600);
						popup.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
						
						final List<String> keyList = new ArrayList<String>( seqmap.keySet() );
						final boolean[]	barr = new boolean[ keyList.size() ];
						for( int i = 0; i < barr.length; i++ ) {
							barr[i] = true;
						}
						
						JTable table = new JTable();
						table.setModel( new TableModel() {
							
							@Override
							public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
								barr[rowIndex] = (Boolean)aValue;
							}
							
							@Override
							public void removeTableModelListener(TableModelListener l) {}
							
							@Override
							public boolean isCellEditable(int rowIndex, int columnIndex) {
								return columnIndex == 0;
							}
							
							@Override
							public Object getValueAt(int rowIndex, int columnIndex) {
								if( columnIndex == 0 ) return barr[ rowIndex ]; 
								return keyList.get( rowIndex );
							}
							
							@Override
							public int getRowCount() {
								return keyList.size();
							}
							
							@Override
							public String getColumnName(int columnIndex) {
								if( columnIndex == 0 ) return "Use";
								return "Name";
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
						JScrollPane	scrollpane = new JScrollPane( table );
						
						popup.add( scrollpane );
						popup.setVisible( true );
						
						popup.addWindowListener( new WindowListener() {
							
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
									int k = 0;
									FileWriter fw = new FileWriter( f );
									for( boolean b : barr ) {
										if( b ) {
											String key = keyList.get(k);
											fw.write( ">"+key+"\n" );
											StringBuilder sb = seqmap.get( key );
											for (int i = 0; i < sb.length(); i += 70) {
												fw.append(sb.substring(i, Math.min(i + 70, sb.length())) + "\n");
											}
										}
										k++;
									}
									fw.close();
									
									addSequences( f.getName(), f.toURI().toString(), null );
								} catch (IOException | URISyntaxException e1) {
									e1.printStackTrace();
								}
							}
							
							@Override
							public void windowActivated(WindowEvent e) {}
						});
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("View sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Delete") {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSeqs();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Create database") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					
					nrun.checkInstall( dir );
						
					int r = table.getSelectedRow();
					final String dbtype = (String)table.getValueAt( r, 2 );
					final String path = (String)table.getValueAt( r, 3 );
					URL url = new URL( path );
					
					String file = url.getFile();
					String[] split = file.split("/");
					String fname = split[ split.length-1 ];
					split = fname.split("\\.");
					final String title = split[0]; 
					final File infile = new File( dir, "tmp_"+fname );
					
					FileOutputStream fos = new FileOutputStream( infile );
					InputStream is = url.openStream();
					
					byte[] bb = new byte[100000];
					r = is.read(bb);
					while( r > 0 ) {
						fos.write(bb, 0, r);
						r = is.read(bb);
					}
					is.close();
					fos.close();
					
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog( c ) == JFileChooser.APPROVE_OPTION ) {
						File selectedfile = fc.getSelectedFile();
						if( !selectedfile.isDirectory() ) selectedfile = selectedfile.getParentFile();
						
						final String outPath = nrun.fixPath( new File( selectedfile, title ).getAbsolutePath() );
						final Object[] cont = new Object[3];
						Runnable run = new Runnable() {
							public void run() {
								if( cont[0] != null ) {
									infile.delete();
								
									if( cont[0] != null ) {
										try {
											JSObject js = JSObject.getWindow( SerifyApplet.this );
											//js = (JSObject)js.getMember("document");
										
											String machineinfo = getMachine();
											String[] split = machineinfo.split("\t");
											js.call( "addDb", new Object[] {getUser(), title, dbtype, outPath, split[0], cont[1]} );
										} catch( NoSuchMethodError | Exception e ) {
											
										}
									}
								}
							}
						};
						
						File makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\makeblastdb.exe" );
						if( !makeblastdb.exists() ) makeblastdb = new File( "/opt/ncbi-blast-2.2.28+/bin/makeblastdb" );
						if( makeblastdb.exists() ) {
							String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", nrun.fixPath( infile.getAbsolutePath() ), "-title", title, "-dbtype", dbtype, "-out", outPath };
							nrun.runProcessBuilder( "Creating database", Arrays.asList( cmds ), run, cont );
						}
					}
					
					//infile.delete();
					//} else System.err.println( "no blast installed" );
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Link database") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userhome = System.getProperty("user.home");	
					
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( c ) == JFileChooser.APPROVE_OPTION ) {
					File selectedfile = fc.getSelectedFile();
					String title = selectedfile.getName();
					String dbtype = "prot";
					int i = title.lastIndexOf('.');
					if( i != -1 ) {
						if( title.charAt(i+1) == 'n' ) dbtype = "nucl";
						
						int li = title.lastIndexOf('.', i-1);
						if( li != -1 ) {
							if( i-li == 3 ) {
								title = title.substring(0,li);
							} else {
								title = title.substring(0,i);
							}
						} else {
							title = title.substring(0,i);
						}
					}
					
					final String outPath = nrun.fixPath( selectedfile.getParentFile().getAbsolutePath()+System.getProperty("file.separator")+title );
					//String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", fixPath( infile.getAbsolutePath() ), "-out", outPath, "-title", title };
					
					JSObject js = JSObject.getWindow( SerifyApplet.this );
					//js = (JSObject)js.getMember("document");
					
					String machineinfo = getMachine();
					String[] split = machineinfo.split("\t");
					js.call( "addDb", new Object[] {getUser(), title, dbtype, outPath, split[0], ""} );
				}
			}
		});
		popup.add( new AbstractAction("Blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean success = false;
				try {
					String userhome = System.getProperty("user.home");
					File dir = new File( userhome );
					
					nrun.checkInstall( dir );
						
					JSObject js = JSObject.getWindow( SerifyApplet.this );
					getParameters( js );
					success = true;
					//infile.delete();
				} catch (NoSuchMethodError e0) {
					e0.printStackTrace();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if( !success ) {
					TableModel model = new TableModel() {
						@Override
						public int getRowCount() {
							return serifier.getSequencesList().size();
						}

						@Override
						public int getColumnCount() {
							return 1;
						}

						@Override
						public String getColumnName(int columnIndex) {
							return "Sequences";
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
							return serifier.getSequencesList().get(rowIndex).getName();
						}

						@Override
						public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

						@Override
						public void addTableModelListener(TableModelListener l) {}

						@Override
						public void removeTableModelListener(TableModelListener l) {}
						
					};
					JTable	table = new JTable( model );
					JScrollPane pane = new JScrollPane( table );
					Dimension dim = new Dimension(400,300);
					pane.setPreferredSize( dim );
					pane.setSize( dim );
					JTextField	tf = new JTextField();
										
					JOptionPane.showMessageDialog(c, new Object[] {"against: ", pane, "parameters: ", tf} );
					
					int i = table.getSelectedRow();
					Sequences seqs = serifier.getSequencesList().get(i);
					
					String filepath = Paths.get( URI.create(seqs.getPath()) ).toAbsolutePath().toString();
					runBlastInApplet( tf.getText(), filepath, seqs.getType() ); //db.getText().contains(".p") ? "prot" : "nucl" );
				}
			}
		});
		popup.add( new AbstractAction("tBlast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Genbank from blast") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String addon = "nnnttaattaattaannn";
				List<Integer>	startlist = new ArrayList<Integer>();
				int[] rr = table.getSelectedRows();
				JFileChooser	fc = new JFileChooser();
				File dir = null;
				if( rr.length > 1 ) {
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						dir = fc.getSelectedFile();
						fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
					}
				}
				for( int r : rr ) {
					int i = table.convertRowIndexToModel( r );
					Sequences s = getSequences(i);
					if( fc.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						File blastFile = fc.getSelectedFile();
						File f = null;
						if( dir != null ) {
							f = new File( dir, s.getName()+".gb" );
						} else if( fc.showSaveDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
							f = fc.getSelectedFile();
						}
						try {
							serifier.genbankFromBlast(s, blastFile, f);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Genbank from nr") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Integer>	startlist = new ArrayList<Integer>();
				int[] rr = table.getSelectedRows();
				JFileChooser	fc = new JFileChooser();
				File dir = null;
				if( rr.length > 1 ) {
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						dir = fc.getSelectedFile();
						fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
					}
				}
				for( int r : rr ) {
					int i = table.convertRowIndexToModel( r );
					Sequences s = getSequences(i);
					
					if( fc.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						File blastFile = fc.getSelectedFile();
						File f = null;
						if( dir != null ) {
							f = new File( dir, s.getName()+".gb" );
						} else if( fc.showSaveDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
							f = fc.getSelectedFile();
						}
						
						try {
							serifier.genbankFromNR( s, blastFile, f, false );
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Download files") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
				
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						String path = (String)table.getValueAt( r, 3 );
						try {
							URL url = new URL( path );
							String[] str = path.split("\\/");
							f = new File( dir, str[str.length-1] );
							FileOutputStream	fos = new FileOutputStream( f );
							InputStream is = url.openStream();
							byte[] bb = new byte[2048];
							r = is.read(bb);
							while( r > 0 ) {
								fos.write(bb,0,r);
								r = is.read(bb);
							}
							is.close();
							fos.close();
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				/*try {
					SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}*/
			}
		});
		popup.add( new AbstractAction("Show file") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				String path = (String)table.getValueAt( r, 3 );
				
				/*try {
					SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}*/
				
				browse( path );
			}
		});
		popup.add( new AbstractAction("Prodigal") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> urls = new ArrayList<String>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						//int r = table.getSelectedRow();
						String path = (String)table.getValueAt( r, 3 );
						urls.add( path );
					}
					
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					File f = nrun.checkProdigalInstall( dir, urls );
					if( f != null && f.exists() ) {
						nrun.doProdigal( dir, c, f, urls );
					} else System.err.println( "no blast installed" );
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Flanking") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					
					int r = table.getSelectedRow();
					int rr = table.convertRowIndexToModel( r );
					if( rr >= 0 ) {
						final Sequences seqs = getSequences( rr );
						
						try {
							URI uri = new URI( seqs.getPath() );
							InputStream is = uri.toURL().openStream();
							
							String fname = seqs.getName();
							String endn = ".fasta";
							int li = fname.lastIndexOf('.');
							if( li != -1 ) {
								endn = fname.substring(li);
								fname = fname.substring(0,li);
							}
							
							File file = new File( dir, fname+"_flanking"+endn );
							OutputStream os = new FileOutputStream( file );
							
							int nseq = flankingFasta(is, os);
							addSequences(file.getName(), seqs.getType(), file.toURI().toURL().toString(), nseq);
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Clustal") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.exists() ) f.mkdirs();
					else if( !f.isDirectory() ) f = f.getParentFile();
					
					int[] rr = table.getSelectedRows();
					//String seqtype = "nucl";
					//String joinname = f.getName();
					//int nseq = 0;
					for( int r : rr ) {
						int rear = table.convertRowIndexToModel( r );
						if( rear >= 0 ) {
							final Sequences s = getSequences( rear );
							final String seqtype = s.getType();
							String path = s.getPath();
							
							try {
								URL url = new URL( path );
								
								String file = url.getFile();
								String[] split = file.split("/");
								String fname = split[ split.length-1 ];
								split = fname.split("\\.");
								final String title = split[0];
								
								final File infile = new File( f, "tmp_"+fname );
								
								FileOutputStream fos = new FileOutputStream( infile );
								InputStream is = url.openStream();
								
								byte[] bb = new byte[100000];
								r = is.read(bb);
								while( r > 0 ) {
									fos.write(bb, 0, r);
									r = is.read(bb);
								}
								is.close();
								fos.close();
						
								String inputPathFixed = nrun.fixPath( infile.getAbsolutePath() ).trim();
								final String newname = s.getName()+"_aligned";
								String newpath = f.getAbsolutePath()+"/"+newname+".fasta";
								final String newurl = new File( newpath ).toURI().toString();
								final Object[] cont = new Object[3];
								Runnable run = new Runnable() {
									public void run() {										
										infile.delete();
										addSequences(newname, seqtype, newurl, s.getNSeq());
									}
								};
								
								List<String> cmdarr = null;
								if( seqtype.equals("nucl") ) {
									String[] cmds = {"clustalw", "-infile="+inputPathFixed, "-align", "-outfile="+newpath, "-output=FASTA"};
									cmdarr = Arrays.asList( cmds );
								} else {
									String[] cmds = {"clustalo", "-i "+inputPathFixed, "-o "+newpath};
									cmdarr = Arrays.asList( cmds );
								}
								nrun.runProcessBuilder("Clustal alignment", cmdarr, run, cont);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Concatenated tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
				JCheckBox	exgaps = new JCheckBox("Exclude gaps");
				JCheckBox	boots = new JCheckBox("Bootstrap");
				JOptionPane.showMessageDialog( SerifyApplet.this, new Object[] {jukes, exgaps, boots} );
				boolean cantor = jukes.isSelected();
				boolean bootstrap = boots.isSelected();
				boolean excludeGaps = exgaps.isSelected();
				
				List<Sequence> lseq = new ArrayList<Sequence>();
				
				Map<String,StringBuilder>	seqmap = new HashMap<String,StringBuilder>();
				
				try {
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						String path = (String)table.getValueAt( r, 3 );
						URL url = new URL( path );
						StringBuilder	sb = null;
						InputStream is = url.openStream();
						BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
						String line = br.readLine();
						while( line != null ) {
							if( line.startsWith(">") ) {
								String subline = line.substring(1);
								if( seqmap.containsKey( subline ) ) {
									sb = seqmap.get( subline );
								} else {
									sb = new StringBuilder();
									seqmap.put( subline, sb );
								}
							} else {
								if( sb != null ) sb.append( line );
							}
							
							line = br.readLine();
						}
						br.close();
					}
				} catch( Exception e1 ) {
					e1.printStackTrace();
				}
				
				for( String key : seqmap.keySet() ) {
					StringBuilder sb = seqmap.get( key );
					Sequence s = new Sequence( key, sb, null );
					s.checkLengths();
					lseq.add( s );
				}
				
				List<Integer>	idxs = null;
				if( excludeGaps ) {
					int start = Integer.MIN_VALUE;
					int end = Integer.MAX_VALUE;
					
					for( Sequence seq : lseq ) {
						if( seq.getRealStart() > start ) start = seq.getRealStart();
						if( seq.getRealStop() < end ) end = seq.getRealStop();
					}
					
					idxs = new ArrayList<Integer>();
					for( int x = start; x < end; x++ ) {
						int i;
						boolean skip = false;
						for( Sequence seq : lseq ) {
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
				
				double[] dd = new double[ lseq.size()*lseq.size() ];
				Sequence.distanceMatrixNumeric(lseq, dd, idxs, bootstrap, cantor, null);
				
				StringBuilder tree = new StringBuilder();
				tree.append( "\t"+lseq.size()+"\n" );
				int i = 0;
				for( Sequence s : lseq ) {
					tree.append( s.getName() );
					int k;
					for( k = i; k < i+lseq.size(); k++ ) {
						tree.append( "\t"+dd[k] );
					}
					i = k;
					
					tree.append("\n");
				}
				System.err.println( tree.toString() );
				JSObject win = JSObject.getWindow( SerifyApplet.this );
				win.call("showTree", new Object[] {tree.toString()});
			}
		});
		popup.add( new AbstractAction("Majority rule consensus tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		popup.add( new AbstractAction("Gene evolution phylogeny (distance matrix correlation)") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<double[]>	ldmat = new ArrayList<double[]>();
				List<String>	names = new ArrayList<String>();
				
				try {
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						String path = (String)table.getValueAt( r, 3 );
						
						String name = (String)table.getValueAt( r, 1 );
						int l = name.lastIndexOf('.');
						if( l != -1 ) name = name.substring(0, l);
						name = name.replace("(", "").replace(")", "").replace(",", "");
						names.add( name );
						
						URL url = new URL( path );
						InputStream is = url.openStream();
						BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
						String line = br.readLine();
						Sequence seq = null;
						List<Sequence> 	lseq = new ArrayList<Sequence>();
						while( line != null ) {
							if( line.startsWith(">") ) {
								String subline = line.substring(1);
								seq = new Sequence( subline, null );
								lseq.add( seq );
							} else {
								if( seq != null ) seq.append( line );
							}
							
							line = br.readLine();
						}
						br.close();
						
						for( Sequence s : lseq ) {
							s.checkLengths();
						}
					
						if( ldmat.size() > 0 && ldmat.get(0).length != lseq.size()*lseq.size() ) {
							System.err.println( ldmat.size() + "  " + lseq.size() );
							System.err.println( lseq.size()*lseq.size() + "  " + ldmat.get(0).length );
							break;
						}
						double[] dmat = new double[ lseq.size()*lseq.size() ];
						ldmat.add( dmat );
						
						Sequence.distanceMatrixNumeric(lseq, dmat, null, false, false, null);
					}
					
					if( ldmat.size() == rr.length ) {
						double[]	submat = new double[ ldmat.size()*ldmat.size() ];
						for( int i = 0; i < ldmat.size(); i++ ) {
							submat[i*ldmat.size()+i] = 0.0;
						}
						
						double mincorr = Double.MAX_VALUE;
						int mini = 0;
						int mink = 0;
						for( int i = 0; i < ldmat.size()-1; i++ ) {
							for( int k = i+1; k < ldmat.size(); k++ ) {
								double[] dmat1 = ldmat.get(i);
								double[] dmat2 = ldmat.get(k);
								double corr = correlateDistance( dmat1, dmat2 );
								if( corr < mincorr ) {
									mincorr = corr;
									mini = i;
									mink = k;
								}
								submat[i*ldmat.size()+k] = corr;
								submat[k*ldmat.size()+i] = corr;
							}
						}
						System.err.println( mini + "  " + mink + "  " + mincorr );
						
						StringBuilder tree = new StringBuilder();
						tree.append( "\t"+ldmat.size()+"\n" );
						int i = 0;
						for( String name : names ) {
							tree.append( name );
							int k;
							for( k = i; k < i+ldmat.size(); k++ ) {
								tree.append( "\t"+submat[k] );
							}
							i = k;
							
							tree.append("\n");
						}
						String treestr = tree.toString();
						/*FileWriter fw = new FileWriter( "/home/sigmar/mat.txt" );
						fw.write( treestr );
						fw.close();*/
						//System.err.println( tree.toString() );
						JSObject win = JSObject.getWindow( SerifyApplet.this );
						win.call("showTree", new Object[] {treestr});
					}
				} catch( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Gene evolution phylogeny (nni distance)") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,String> namePath = new HashMap<String,String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String path = (String)table.getValueAt( r, 3 );
					
					String name = (String)table.getValueAt( r, 1 );
					int l = name.lastIndexOf('.');
					if( l != -1 ) name = name.substring(0, l);
					name = name.replace("(", "").replace(")", "").replace(",", "");
					
					namePath.put( name, path );
				}
				String tree = genePhylogenyNNI( namePath, false );
				JSObject win = JSObject.getWindow( SerifyApplet.this );
				win.call("showTree", new Object[] { tree });
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Append") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				//fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					//if( !f.isDirectory() ) f = f.getParentFile();
					
					List<Sequences> lseqs = new ArrayList<Sequences>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int rear = table.convertRowIndexToModel( r );
						if( rear >= 0 ) {
							Sequences s = getSequences( rear );
							lseqs.add( s );
						}
					}
					List<Sequences> retlseqs = serifier.join( f, lseqs, true, null, false );
					for( Sequences seqs : retlseqs ) {
						addSequences( seqs );
					}
				}
			}
		});
		popup.add( new AbstractAction("Join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				//fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					//if( !f.isDirectory() ) f = f.getParentFile();
					
					List<Sequences> lseqs = new ArrayList<Sequences>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int rear = table.convertRowIndexToModel( r );
						if( rear >= 0 ) {
							Sequences s = getSequences( rear );
							lseqs.add( s );
						}
					}
					List<Sequences> retlseqs = serifier.join( f, lseqs, true, null, true );
					for( Sequences seqs : retlseqs ) {
						addSequences( seqs );
					}
				}
			}
		});
		popup.add( new AbstractAction("Min length filter") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					final JSpinner spinner = new JSpinner();
					spinner.setValue( 500 ); //seqs.getNSeq() );
					spinner.setPreferredSize( new Dimension(100,25) );
					final JDialog dl;
					Window window = SwingUtilities.windowForComponent(nrun.cnt);
					if( window != null ) dl = new JDialog( window );
					else dl = new JDialog();
					dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					JComponent c = new JComponent() {};
					c.setLayout( new FlowLayout() );
					dl.setTitle("Number of sequences in each file");
					JButton button = new JButton( new AbstractAction("Ok") {
						@Override
						public void actionPerformed(ActionEvent e) {
							dl.setVisible( false );
							dl.dispose();
						}
					});
					c.add( spinner );
					c.add( button );
					dl.add( c );
					dl.setSize(200, 60);
					
					dl.addWindowListener( new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}

						@Override
						public void windowClosing(WindowEvent e) {}

						@Override
						public void windowClosed(WindowEvent e) {
							int spin = (Integer)spinner.getValue();
							int[] ra = table.getSelectedRows();
							for( int r : ra ) {
								int rr = table.convertRowIndexToModel( r );
								if( rr >= 0 ) {
									final Sequences seqs = getSequences( rr );
									Sequences nseqs = serifier.filtit( spin, seqs, dir );
									serifier.addSequences( nseqs );
								}
							}
						}

						@Override
						public void windowIconified(WindowEvent e) {}

						@Override
						public void windowDeiconified(WindowEvent e) {}

						@Override
						public void windowActivated(WindowEvent e) {}

						@Override
						public void windowDeactivated(WindowEvent e) {}
					});
					dl.setVisible( true );
				}
			}
		});
		popup.add( new AbstractAction("Tag split") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					
					fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
					if( fc.showOpenDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
						File fo = fc.getSelectedFile();
						Path pt = Paths.get( fo.toURI() );
						Map<String,String>	tagmap = new HashMap<String,String>();
						try {
							List<String> lines = Files.readAllLines( pt, Charset.defaultCharset() );
							for( String line : lines ) {
								String[] split = line.split("\t");
								tagmap.put( split[0], split[1] );
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						int[] ra = table.getSelectedRows();
						for( int r : ra ) {
							int rr = table.convertRowIndexToModel( r );
							if( rr >= 0 ) {
								final Sequences seqs = getSequences( rr );
								tagsplit( tagmap, seqs, dir, SerifyApplet.this );
							}
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Split") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					final JSpinner spinner = new JSpinner();
					spinner.setValue( 1 ); //seqs.getNSeq() );
					spinner.setPreferredSize( new Dimension(100,25) );
					final JDialog dl;
					Window window = SwingUtilities.windowForComponent(nrun.cnt);
					if( window != null ) dl = new JDialog( window );
					else dl = new JDialog();
					dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					JComponent c = new JComponent() {};
					c.setLayout( new FlowLayout() );
					dl.setTitle("Number of sequences in each file");
					JButton button = new JButton( new AbstractAction("Ok") {
						@Override
						public void actionPerformed(ActionEvent e) {
							dl.setVisible( false );
							dl.dispose();
						}
					});
					c.add( spinner );
					c.add( button );
					dl.add( c );
					dl.setSize(200, 60);
					
					dl.addWindowListener( new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}

						@Override
						public void windowClosing(WindowEvent e) {}

						@Override
						public void windowClosed(WindowEvent e) {
							int spin = (Integer)spinner.getValue();
							int[] ra = table.getSelectedRows();
							for( int r : ra ) {
								int rr = table.convertRowIndexToModel( r );
								if( rr >= 0 ) {
									final Sequences seqs = getSequences( rr );
									List<Sequences> lseqs = serifier.splitit( spin, seqs, dir );
									for( Sequences nseqs : lseqs ) {
										addSequences( nseqs );
									}
								}
							}
						}

						@Override
						public void windowIconified(WindowEvent e) {}

						@Override
						public void windowDeiconified(WindowEvent e) {}

						@Override
						public void windowActivated(WindowEvent e) {}

						@Override
						public void windowDeactivated(WindowEvent e) {}
					});
					dl.setVisible( true );
				}
			}
		});
		popup.add( new AbstractAction("Cut") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f1 = fc.getSelectedFile();
					if( !f1.isDirectory() ) f1 = f1.getParentFile();
					final File dir = f1;
					
					int r = table.getSelectedRow();
					int rr = table.convertRowIndexToModel( r );
					if( rr >= 0 ) {
						final Sequences seqs = getSequences( rr );
						
						String val = JOptionPane.showInputDialog("Select character", "_");
						try {
							URI uri = new URI( seqs.getPath() );
							InputStream is = uri.toURL().openStream();
							
							if( seqs.getPath().endsWith(".gz") ) {
								is = new GZIPInputStream( is );
							}
							
							URL url = uri.toURL();
							String urlstr = url.toString();
							String[] erm = urlstr.split("\\/");
							String name = erm[ erm.length-1 ];
							int ind = name.lastIndexOf('.');
							
							String sff = name;
							String sf2 = "";
							if( ind != -1 ) {
								sff = name.substring(0, ind);
								sf2 = name.substring(ind+1,name.length());
							}
							
							String trimname = sff+"_cut";
							File f = new File( dir, trimname+"."+sf2 );
							FileWriter fw = new FileWriter(f);
							
							cutFasta( new BufferedReader( new InputStreamReader( is ) ), new BufferedWriter( fw ), val.charAt(0) );
							SerifyApplet.this.addSequences( trimname, seqs.getType(), f.toURI().toString(), seqs.getNSeq() );
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Trim") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f1 = fc.getSelectedFile();
					if( !f1.isDirectory() ) f1 = f1.getParentFile();
					final File dir = f1;
					
					final JTextField spinner = new JTextField();
					//spinner.setValue( seqs.getNSeq() );
					spinner.setPreferredSize( new Dimension(600,25) );

					final JDialog dl;
					Window window = SwingUtilities.windowForComponent(nrun.cnt);
					if( window != null ) dl = new JDialog( window );
					else dl = new JDialog();
					
					dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					JComponent c = new JComponent() {};
					c.setLayout( new FlowLayout() );
					dl.setTitle("Filter sequences");
					JButton browse = new JButton( new AbstractAction("Browse") {
						@Override
						public void actionPerformed(ActionEvent e) {
							JFileChooser	filechooser = new JFileChooser();
							if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
								spinner.setText( filechooser.getSelectedFile().toURI().toString() );
							}
						}
					});
					JButton button = new JButton( new AbstractAction("Ok") {
						@Override
						public void actionPerformed(ActionEvent e) {
							dl.setVisible( false );
							dl.dispose();
						}
					});
					c.add( spinner );
					c.add( browse );
					c.add( button );
					dl.add( c );
					dl.setSize(800, 60);
					
					dl.addWindowListener( new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}

						@Override
						public void windowClosing(WindowEvent e) {}

						@Override
						public void windowClosed(WindowEvent e) {
							String trim = spinner.getText();
							trim( dir, trim );
						}

						@Override
						public void windowIconified(WindowEvent e) {}

						@Override
						public void windowDeiconified(WindowEvent e) {}

						@Override
						public void windowActivated(WindowEvent e) {}

						@Override
						public void windowDeactivated(WindowEvent e) {}
					});
					dl.setVisible( true );
				}
			}
		});
		popup.add( new AbstractAction("Transpose") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
						File f1 = fc.getSelectedFile();
						if( !f1.isDirectory() ) f1 = f1.getParentFile();
						final File dir = f1;
						
						Map<String,Map<String,StringBuilder>>	seqmap = new HashMap<String,Map<String,StringBuilder>>();
						
						int[] rr = table.getSelectedRows();
						for( int r : rr ) {
							String path = (String)table.getValueAt( r, 3 );
							
							String name = (String)table.getValueAt( r, 1 );
							int l = name.lastIndexOf('.');
							if( l != -1 ) name = name.substring(0, l);
							
							URL url = new URL( path );
							Map<String,StringBuilder>	msb = null;
							StringBuilder				sb = null;
							InputStream is = url.openStream();
							BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
							String line = br.readLine();
							while( line != null ) {
								if( line.startsWith(">") ) {
									sb = new StringBuilder();
									String subline = line.substring(1);
									if( seqmap.containsKey( subline ) ) {
										msb = seqmap.get( subline );
									} else {
										msb = new HashMap<String,StringBuilder>();
										seqmap.put( subline, msb );
									}
									msb.put( name, sb );
								} else {
									if( sb != null ) sb.append( line );
								}
								
								line = br.readLine();
							}
							br.close();
						}
						
						for( String name : seqmap.keySet() ) {
							Map<String,StringBuilder>	subset = seqmap.get( name );
							FileWriter fw = new FileWriter( new File(dir, name+".fasta") );
							for( String subname : subset.keySet() ) {
								fw.write( ">"+subname+"\n" );
								StringBuilder sb = subset.get(subname);
								for( int i = 0; i < sb.length(); i+=70 ) {
									fw.write( sb.substring(i, Math.min(sb.length(), i+70) ) + "\n" );
								}
							}
							fw.close();
						}
					}
				} catch( Exception ee ) {
					ee.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Remove gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f1 = fc.getSelectedFile();
					if( !f1.isDirectory() ) f1 = f1.getParentFile();
					final File dir = f1;
					
					Map<String,Map<String,StringBuilder>>	seqmap = new HashMap<String,Map<String,StringBuilder>>();
					
					JavaFasta jf = new JavaFasta( SerifyApplet.this, serifier, null );
					jf.initDataStructures();
					
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						String name = (String)table.getValueAt( r, 1 );
						String path = (String)table.getValueAt( r, 3 );
						
						serifier.lseq.clear();
						try {
							URL url = new URL( path );
							jf.importFile( name, url.openStream() );
							serifier.removeAllGaps( serifier.lseq );
							int i = path.lastIndexOf('.');
							if( i == -1 ) i = path.length();
							URI uri = new URI(path.substring(0, i)+"_unaligned.fasta");
							File f = new File( uri );
							serifier.writeFasta( serifier.lseq, new FileWriter( f ), jf.getSelectedRect() );
						} catch(IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Blast join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSObject	jso = JSObject.getWindow(SerifyApplet.this);
				String s = (String)jso.call("getSelectedBlast", new Object[] {} );
				
				JFileChooser fc = new JFileChooser();
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						blastJoin( new FileInputStream(s), new PrintStream(new FileOutputStream(f)) );
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("Make clusters") {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputStream is = null;
				try {
					JSObject	jso = JSObject.getWindow(SerifyApplet.this);
					String s = (String)jso.call("getSelectedBlast", new Object[] {} );
					is = new FileInputStream(s);
				} catch( Exception ex ) {
					
				}
				
				JFileChooser fc = new JFileChooser();
				if( is == null ) {
					if( fc.showOpenDialog(nrun.cnt) == JFileChooser.APPROVE_OPTION ) {
						try {
							is = new FileInputStream( fc.getSelectedFile() );
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						List<Set<String>> cluster = serifier.makeBlastCluster( is, null, 1 );
						
						Set<String> headset = new HashSet<String>();
						for( Set<String> cl : cluster ) {
							for( String c : cl ) {
								headset.add( c );
								break;
							}
						}
						
						totalTrim( f.getParentFile(), headset );
						//trimFasta( br, new FileWriter( f ), headset, false );
					} catch ( URISyntaxException | IOException e1) {
						e1.printStackTrace();
					}
					
					/*try {
						blastClusters( new FileInputStream(s), new FileOutputStream(f) );
					} catch(FileNotFoundException e1) {
						e1.printStackTrace();
					} catch(IOException e1) {
						e1.printStackTrace();
					}*/
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("FastTree prepare") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				List<Sequences> lseqs = new ArrayList<Sequences>();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int cr = table.convertRowIndexToModel(r);
					Sequences seqs = getSequences(cr);
					
					lseqs.add( seqs );
				}
				
				List<Sequences> retlseqs = serifier.fastTreePrepare( lseqs );
				for( Sequences seqs : retlseqs ) {
					try {
						SerifyApplet.this.addSequences(seqs.getName(), seqs.getPath(), null);
					} catch (URISyntaxException | IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("Blast rename") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int rr = table.convertRowIndexToModel( r );
				if( rr >= 0 ) {
					JFileChooser fc = new JFileChooser();
					if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
						File f = fc.getSelectedFile();
						
						final Sequences seqs = getSequences( rr );
						String s = null;
						try {
							JSObject	jso = JSObject.getWindow(SerifyApplet.this);
							s = (String)jso.call("getSelectedBlast", new Object[] {} );
						} catch( Exception exp ) {
							exp.printStackTrace();
						}
						
						if( s == null ) {
							JFileChooser filechooser = new JFileChooser();
							if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
								s = filechooser.getSelectedFile().getAbsolutePath();
							}
						}
					
						if( s != null ) {
							Sequences ret = serifier.blastRename( seqs, s, f, false );
							serifier.addSequences( ret );
						}
					}
				}
			}
		});
		
		table.setComponentPopupMenu( popup );
		JScrollPane	scrollpane = new JScrollPane( table );
		scrollpane.setComponentPopupMenu( popup );
		scrollpane.setBackground( bgcolor );
		scrollpane.getViewport().setBackground( bgcolor );
		scrollpane.setTransferHandler( new TransferHandler() {
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
						return null;
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.javaFileListFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						/*if (arg0.equals( DataFlavor.javaFileListFlavor ) ) {
							return true;
						}*/
						return true;
					}
				};
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;
				
				DataFlavor[] dflv = support.getDataFlavors();
				for( DataFlavor df : dflv ) {
					System.err.println( df );
				}
				
				int b = Arrays.binarySearch( support.getDataFlavors(), DataFlavor.javaFileListFlavor, new Comparator<DataFlavor>() {
					@Override
					public int compare(DataFlavor o1, DataFlavor o2) {
						return o1 == o2 ? 1 : 0;
					}
				});
				
				try {
					obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if( obj == null ) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if( obj == null ) {
					try {
						obj = support.getTransferable().getTransferData( DataFlavor.getTextPlainUnicodeFlavor() );
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					if( obj != null && obj instanceof List ) {
						List<File> lf = (List<File>)obj;
						for( File f : lf ) {
							addSequences( f.getName(), f.toURI().toString(), null );
						}
					} else if( obj instanceof Image ) {
						
					} else if( obj instanceof String ) {
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						String filelistStr = (String)obj;
						
						if( filelistStr.contains("\n>") ) {
							Map<String,Reader>	isrmap = new HashMap<String,Reader>();
							InputStreamReader isr = new InputStreamReader( new ByteArrayInputStream(filelistStr.getBytes()) );
							isrmap.put("", isr);
							addSequences(null, isrmap, null, null);
						} else {
							String[] fileStr = filelistStr.split("\n");
							
							System.err.println( filelistStr );
							for( String fileName : fileStr ) {
								String val = fileName.trim();
								//File f = new File( new URI( fileName ) );
								String[] split = val.split("/");
								addSequences( split[ split.length-1 ], val, null );
							}
						}
					} else if( obj instanceof Reader ) {
						System.err.println("Reader");
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						
						CharArrayWriter	caw = new CharArrayWriter();
						Reader rd = (Reader)obj;
						char[] cbuf = new char[1024];
						int r = rd.read(cbuf);
						while( r > 0 ) {
							caw.write( cbuf, 0, r );
							r = rd.read(cbuf);
						}
						
						String filelistStr = (String)caw.toString();
						String[] fileStr = filelistStr.split("\n");
						
						for( String fileName : fileStr ) {
							String val = fileName.trim();
							String[] split = val.split("/");
							addSequences( split[ split.length-1 ], val.trim(), null );
						}
					} else if( obj instanceof InputStream ) {
						System.err.println("InputStream");
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						
						CharArrayWriter	caw = new CharArrayWriter();
						Reader rd = new InputStreamReader( (InputStream)obj );
						char[] cbuf = new char[1024];
						int r = rd.read(cbuf);
						while( r > 0 ) {
							caw.write( cbuf, 0, r );
							r = rd.read(cbuf);
						}
						
						String filelistStr = (String)caw.toString();
						String[] fileStr = filelistStr.split("\n");
						
						for( String fileName : fileStr ) {
							String val = fileName.trim();
							String[] split = val.split("/");
							addSequences( split[ split.length-1 ], val, null );
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				return true;
			}
		});
		
		c.add( scrollpane );
	}
	
	public void paint( Graphics g ) {
		super.paint( g );
	}
	
	public void update( Graphics g ) {
		super.update( g );
	}
	
	public void updateSequences( final String user, final String name, final String type, final String path, final int nseq, final String key ) {
		Sequences seqs = new Sequences( user, name, type, path, nseq );
		seqs.setKey( key );
		serifier.addSequences( seqs );
		//serifier.sequences.add( seqs );
		updateSequences(seqs);
	}
	
	public void updateSequences( final Sequences seqs ) {
		AccessController.doPrivileged( new PrivilegedAction<String>() {
			@Override
			public String run() {
				new Thread() {
					public void run() {
						boolean succ = true;
						try {
							URL url = new URL( seqs.getPath() );
							InputStream is = url.openStream();
							if( is == null ) succ = false;
							else is.close();
						} catch (MalformedURLException e) {
							e.printStackTrace();
							succ = false;
						} catch (IOException e) {
							e.printStackTrace();
							succ = false;
						} catch( Exception e ) {
							e.printStackTrace();
							succ = false;
						}
						
						if( succ ) {
							table.tableChanged( new TableModelEvent( table.getModel() ) );
						}
					}
				}.start();
				
				return null;
			}
		});
	}
	
	public void addSequences( Sequences seqs ) {
		boolean unsucc = false;
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "addSequences", new Object[] {seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNSeq()} );
		} catch( NoSuchMethodError | Exception e ) {
			unsucc = true;
		}
		
		if( unsucc ) {
			updateSequences( seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNSeq(), null ); 
			//updateSequences( seqs );
		}
	}
	
	private void addSequences( String user, String name, String type, String path, int nseq ) {
		boolean unsucc = false;
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "addSequences", new Object[] {user, name, type, path, nseq} );
		} catch( NoSuchMethodError | Exception e ) {
			unsucc = true;
		}
		
		if( unsucc ) {
			updateSequences(user, name, type, path, nseq, null);
		}
	}
	
	public void addSequences( String name, Reader rd, String path, String replace ) throws URISyntaxException, IOException {
		Map<String,Reader> rds = new HashMap<String,Reader>();
		rds.put( name, rd );
		addSequences(name, rds, path, replace);
	}
	
	public void addSequences( String name, Map<String,Reader> rds, String path, String replace ) throws URISyntaxException, IOException {
		String type = "nucl";
		int nseq = 0;
		
		Map<String,StringBuilder>	gbks = new HashMap<String,StringBuilder>();
		for( String tag : rds.keySet() ) {
			Reader rd = rds.get( tag );
			//String path = paths.get( tag );
			BufferedReader br = new BufferedReader( rd );
			String line = br.readLine();
		
			if( line != null ) {
				if( line.endsWith(":") ) {
					JFileChooser	filechooser = new JFileChooser();
					filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
						File dir = filechooser.getSelectedFile();
						if( !dir.exists() ) dir.mkdirs();
						
						//Set<String>	curset = new HashSet<String>();
						int idx = line.indexOf(']');
						String[] split = null;
						if( idx >= 0 ) {
							split = line.substring(1, idx).split(",");
						}
						String curname = line.substring( idx+1, Math.min( 128+idx+1, line.length()-1 ) ).replace(' ', '_');;
						//curset.add( curname );
						
						List<FileWriter>	lfw = new ArrayList<FileWriter>();
						File subdir = new File( dir, "all" );
						subdir.mkdir();
						File f = new File( subdir, curname+".fasta" );
						FileWriter	nfw = new FileWriter( f );
						lfw.add( nfw );
						for( String subdirstr : split ) {
							subdir = new File( dir, subdirstr );
							subdir.mkdir();
							f = new File( subdir, curname+".fasta" );
							FileWriter fw = new FileWriter( f );
							lfw.add( fw );
						}
						
						line = br.readLine();
						while( line != null ) {
							if( line.endsWith(":") ) {
								for( FileWriter tfw : lfw ) tfw.close();
								lfw.clear();
								
								addSequences( curname, f.toURI().toString(), replace );
								
								//int val = 1;
								
								idx = line.indexOf(']');
								if( idx >= 0 ) {
									split = line.substring(1, idx).split(",");
								}
								curname = line.substring( idx+1, Math.min( 128+idx+1, line.length()-1 ) ).replace(' ', '_');;
								//curset.add( curname );
								//curname = line.substring( 0,  Math.min( 128, line.length()-1 ) ).replace(' ', '_');
								String newcurname = curname;
								//while( curset.contains(newcurname) ) newcurname = curname+"_"+(val++);
								curname = newcurname;
								
								//f = new File( dir, curname+".fasta" );
								//fw = new FileWriter( f );
								subdir = new File( dir, "all" );
								subdir.mkdir();
								f = new File( subdir, curname+".fasta" );
								nfw = new FileWriter( f );
								lfw.add( nfw );
								for( String subdirstr : split ) {
									subdir = new File( dir, subdirstr );
									subdir.mkdir();
									f = new File( subdir, curname+".fasta" );
									FileWriter fw = new FileWriter( f );
									lfw.add( fw );
								}
							} else {
								for( FileWriter tfw : lfw ) tfw.write( line+"\n" );
							}
							
							line = br.readLine();
						}
						for( FileWriter tfw : lfw ) tfw.close();
						lfw.clear();
						
						addSequences( curname, f.toURI().toString(), replace );
					}
				} else if( line.startsWith(">") ) {
					if( path == null ) {
						JFileChooser	filechooser = new JFileChooser();
						filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
						if( filechooser.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
							File f = filechooser.getSelectedFile();
							path = f.toURI().toString();
						}
					}
					
					if( path != null ) {
						while( line != null ) {
							if( line.startsWith(">") ) {
								nseq++;
								
								if( nseq % 1000 == 0 ) System.err.println( "seq counting: "+nseq );
							} else if( type.equals("nucl") && !line.matches("^[acgtykvrswmunxACGTDYKVRSWMUNX]+$") ) {
								type = "prot";
							}
							line = br.readLine();
						}
						
						if( nseq > 0 ) {
							addSequences(name, type, path, nseq);
						} else System.err.println( "no sequences in file" );
					}
				} else {
					StringBuilder filetext = new StringBuilder();
					while( line != null ) {
						filetext.append( line+"\n" );
						line = br.readLine();
					}
					gbks.put(tag, filetext);
					
					//boolean amino = true;
					//String[] annoarray = {"tRNA", "rRNA"};//{"CDS", "tRNA", "rRNA", "mRNA"};
					//String[] annoarray = {"CDS"};
				}
				br.close();
			}
		}
		
		if( gbks.size() > 0 ) {
			Map<String,URI>	map = new HashMap<String,URI>();
			URI firsturi = new URI(path+".fna");
			FileWriter out = new FileWriter( new File( firsturi ) );
			
			URI uri = new URI(path+".aa");
			//FileWriter out = new FileWriter( new File( uri ) );
			map.put( "CDS", uri );
			
			uri = new URI(path+".trna");
			//out = new FileWriter( new File( uri ) );
			map.put( "tRNA", uri );
			
			uri = new URI(path+".rrna");
			//out = new FileWriter( new File( uri ) );
			map.put( "rRNA", uri );
			
			uri = new URI(path+".mrna");
			//out = new FileWriter( new File( uri ) );
			map.put( "mRNA", uri );
			
			/*uri = new URI(path+".namemap");
			map.put( "nameMap", uri );*/
			
			GBK2AminoFasta.handleText( name, gbks, map, out, path, replace );
			
			//+firsturi.toString().replace(name, "")
			addSequences( name+".fna", firsturi.toString(), null );
			for( String tg : map.keySet() ) {
				uri = map.get( tg );
				addSequences( name+"."+tg, uri.toString(), null );
			}
		}
	}
	
	public void addSequencesPath( String name, Map<String,String> urimap, String path, String replace ) throws URISyntaxException, IOException {
		try {
			Map<String,Reader>	isrmap = new HashMap<String,Reader>();
			for( String tag : urimap.keySet() ) {
				String uripath = urimap.get( tag );
				URL url = new URL( uripath );
				InputStream is = url.openStream();
				
				if( is != null ) {
					if( uripath.endsWith(".gz") ) is = new GZIPInputStream(is);
					InputStreamReader isr = new InputStreamReader( is );
					isrmap.put( tag, isr );
					//FileReader	fr = new FileReader( f );
				}
				
				path = uripath;
			}
			
			/*String uri = null;
			for( String ur : urimap.keySet() ) {
				uri = urimap.get( ur );
				break;
			}*/
			addSequences( name, isrmap, path, replace );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void addSequences( String name, String path, String replace ) throws URISyntaxException, IOException {
		URL url = new URL(path);

		boolean succ = true;
		InputStream is = null;
		try {
			is = url.openStream();
		} catch( Exception e ) {
			succ = false;
			e.printStackTrace();
		}
		
		try {
			if( !succ ) {
				is = new FileInputStream( url.getFile() );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( is != null ) {
			if( path.endsWith(".gz") ) is = new GZIPInputStream(is);
			InputStreamReader isr = new InputStreamReader( is );
			
			Map<String,Reader>	isrmap = new HashMap<String,Reader>();
			isrmap.put( name.substring(0, name.length()-4), isr);
			
			addSequences( name, isrmap, path, replace );
			//FileReader	fr = new FileReader( f );
		}
	}
	
	public void addSequences( String name, String type, String path, int nseq ) {
		addSequences( getUser(), name, type, path, nseq );
	}
	
	public String getUser() {
		return globaluser;
	}
	
	public static boolean inPath( Node leaf, Node n ) {
		if( leaf == n ) return true;
		else {
			Node p = leaf.getParent();
			if( p != null ) {
				return inPath( p, n );
			}
		}
		return false;
	}
	
	public void majorityRuleConsensus( double[] distmat, List<String> corrInd, boolean copybootstrap ) {
		try {
			Map<String,Node>	alltrees = new TreeMap<String,Node>();
			TreeUtil treeutil = new TreeUtil();
			Map<Set<String>,NodeSet> nmap = new HashMap<Set<String>,NodeSet>();
			//File dir = new File( "/home/sigmar/40genes/thermus/aligned/trees/" );
			
			
			//File dir = new File( "/home/sigmar/thermusgenes/aligned/trees/" );
			//File dir = new File( "/root/thermusgenes_transposed/trees/" );
			File dir = new File( "/u0/serify/aligned/concrand/trees/" );
			
			File guidetreefile = new File( "/u0/guidetree.tre" );
			FileReader fr = new FileReader( guidetreefile );
			BufferedReader br = new BufferedReader( fr );
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while( line != null ) {
				sb.append( line );
				line = br.readLine();
			}
			br.close();
			Node guidetree = treeutil.parseTreeRecursive( sb.toString(), false );
			//n.nodeCalcMap( guideMap );
			
			
			//File dir = new File( "c:/cygwin/home/simmi/thermusgenes_short/aligned/trees/" );
			File[] ff = dir.listFiles();
			for( File f : ff ) {
				fr = new FileReader( f );
				br = new BufferedReader( fr );
				sb = new StringBuilder();
				line = br.readLine();
				while( line != null ) {
					sb.append( line );
					line = br.readLine();
				}
				br.close();

				Node n = treeutil.parseTreeRecursive( sb.toString(), false );
				String treename = f.getName();
				int i = treename.indexOf(".fasta");
				if( i == -1 ) i = treename.length();
				alltrees.put( treename.substring(0, i), n );
				treeutil.setLoc( 0 );
				//System.err.println( "about to process "+f.getName() );
				n.nodeCalcMap( nmap );
			}
			
			Node root = DataTable.majoRuleConsensus(treeutil, nmap, guidetree, copybootstrap);
			
			/*List<NodeSet>	nslist = new ArrayList<NodeSet>();
			System.err.println( nmap.size() );
			for( Set<String> nodeset : nmap.keySet() ) {
				NodeSet count = nmap.get( nodeset );
				nslist.add( count );
			}
			
			Collections.sort( nslist );
			int c = 0;
			for( NodeSet nodeset : nslist ) {
				System.err.println( nodeset.getCount() + "  " + nodeset.getNodes() + "  " + nodeset.getAverageHeight() + "  " + nodeset.getAverageBootstrap() );
				c++;
				if( c > 20 ) break;
			}
			
			Map<Set<String>, Node>	nodemap = new HashMap<Set<String>, Node>();
			Map<String, Node>		leafmap = new HashMap<String, Node>();
			NodeSet	allnodes = nslist.get(0);
			int total = allnodes.getCount();
			Node root = treeutil.new Node();
			for( String nname : allnodes.getNodes() ) {
				Node n = treeutil.new Node( nname );
				root.addNode(n, 1.0);
				//n.seth( 1.0 );
				leafmap.put( nname, n );
			}
			
			for( int i = 1; i < 100; i++ ) {
				NodeSet	allsubnodes = nslist.get(i);
				Node subroot = treeutil.new Node();
				if( !copybootstrap ) {
					subroot.setName( Math.round( (double)(allsubnodes.getCount()*1000) / (double)total ) / 10.0 + "%" );
				} else {
					subroot.setName( Double.toString( Math.round( (allsubnodes.getAverageBootstrap()*100.0) )/100.0 ) );
				}
				
				Node vn = treeutil.getValidNode( allsubnodes.getNodes(), root );
				if( treeutil.isValidSet( allsubnodes.getNodes(), vn ) ) {
					while( allsubnodes.getNodes().size() > 0 ) {
						for( String nname : allsubnodes.getNodes() ) {
							Node leaf = leafmap.get( nname );
							Node newparent = leaf.getParent();
							Node current = leaf;
							while( newparent.countLeaves() <= allsubnodes.getNodes().size() ) {
								current = newparent;
								newparent = current.getParent();
							}
							
							if( allsubnodes.getNodes().containsAll( current.getLeaveNames() ) ) {
								Node parent = current.getParent();
								parent.removeNode( current );
								
								double h = allsubnodes.getAverageHeight();
								//double b = allsubnodes.getAverageBootstrap();
								double lh = allsubnodes.getAverageLeaveHeight(nname);
								
								/*subroot.addNode( current, h );
								if( lh != -1.0 ) parent.addNode( subroot, lh );
								else parent.addNode( subroot, 1.0 );*
								
								parent.addNode( subroot, h );
								
								if( current.isLeaf() && lh != -1.0 ) {
									System.err.println( "printing "+current.getName() + "  " + lh );
									subroot.addNode( current, lh );
								} else subroot.addNode( current, current.geth() );
							
								removeNames( allsubnodes.getNodes(), current );
							} else allsubnodes.getNodes().clear();
							
							break;
						}
					}
				}
			}*/
			
			if( distmat != null ) {
				if( root.getNodes().size() == 2 ) root = treeutil.removeRoot( root );
				List<Node> nodes = treeutil.getLeaves( root );
				int c = 0;
				for( String s : corrInd ) {
					int i = c;
					while( !s.equals( nodes.get(i).getName() ) ) i++;
					
					Node tnode = nodes.get(c);
					nodes.set( c, nodes.get(i) );
					nodes.set( i, tnode );
					
					c++;
				}
				
				List<Double> lad = new ArrayList<Double>();
				for( int y = 0; y < corrInd.size()-1; y++ ) {
					for( int x = y+1; x < corrInd.size(); x++ ) {
						lad.add( distmat[y*corrInd.size()+x] );
					}
				}
				
				double[][] W = new double[lad.size()][lad.size()];
				for( int y = 0; y < lad.size(); y++ ) {
					for( int x = 0; x < lad.size(); x++ ) {
						W[y][x] = 0.0;
					}
				}
				double[] d = new double[ lad.size() ];
				int count = 0;
				for( double dval : lad ) {
					d[count] = dval;
					W[count][count] = 1.0; //(dval*dval);
					count++;
				}
				
				List<Node> subnodes = treeutil.getSubNodes( root );
				int nodecount = subnodes.size();
				double[][] X = new double[ lad.size() ][ nodecount ];
				for( int k = 0; k < nodecount; k++ ) {
					Node n = subnodes.get(k);
					int i = 0;
					for( int y = 0; y < corrInd.size()-1; y++ ) {
						Node l1 = nodes.get(y);
						boolean hit = inPath( l1, n );
						for( int x = y+1; x < corrInd.size(); x++ ) {
							if( hit ) X[i][k] = 1.0;
							else {
								Node l2 = nodes.get(x);
								X[i][k] = inPath(l2,n) ? 1.0 : 0.0;
							}
							i++;
						}
					}
					//for( int i = 0; i < lad.size(); i++ ) {}
				}
				
				/**** okokok
				RealMatrix dmat = new Array2DRowRealMatrix( d );
				RealMatrix Xmat = new Array2DRowRealMatrix( X );
				RealMatrix Wmat = new Array2DRowRealMatrix( W );
				System.err.println( Xmat.getColumnDimension() + "  " + Xmat.getRowDimension() );
				System.err.println( Xmat.toString() );
				RealMatrix Xtranspose = Xmat.transpose();
				RealMatrix XW = Xtranspose.multiply( Wmat );
				RealMatrix XWX = XW.multiply(Xmat);
				RealMatrix invXX = new LUDecomposition( XWX ).getSolver().getInverse();
				RealMatrix v = invXX.multiply( XW.multiply(dmat) );
				******/
				
				/*int i = 0;
				for( Node n : subnodes ) {
					n.seth( v.getEntry(i++, 0) );
				}*/
			}
			
			List<Object[]>	sortlist = new ArrayList<Object[]>();
			for( String name : alltrees.keySet() ) {
				double dist = 0.0;
				Node tree = alltrees.get( name );
				dist = treeutil.nDistance(root, tree);
				//System.err.println( name + "  " + dist );
				sortlist.add( new Object[] {name, dist} );
			}
			Collections.sort( sortlist, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					return Double.compare( (Double)o1[1], (Double)o2[1] );
				}
			});
			for( Object[] o : sortlist ) {
				System.err.println( o[0] + "  " + o[1] );
			}
			System.err.println( root.toString() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String genePhylogenyNNI( Map<String,String> namePath, boolean treefiles ) {
		String			treestr = null;
		
		List<Node>		lnode = new ArrayList<Node>();
		List<String>	names = new ArrayList<String>();
		TreeUtil 		treeutil = new TreeUtil();
		String			rootid = null;
		try {
			for( String name : namePath.keySet() ) {
				String path = namePath.get( name );
				names.add( name );
				Node n = null;
				if( !treefiles ) {
					URL url = new URL( path );
					
					boolean mu = true;
					String fstr = url.getFile();
					int fi = fstr.lastIndexOf('/');
					if( fi != -1 ) {
						File f = new File( fstr.substring(0, fi), "tree" );
						if( f.exists() && f.isDirectory() ) {
							File treef = new File( f, fstr.substring(fi+1) );
							if( treef.exists() ) {
								mu = false;
								url = treef.toURI().toURL();
							}
						}
					}
					if( mu ) {
						InputStream is = url.openStream();
						BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
						String line = br.readLine();
						Sequence seq = null;
						List<String> 	corrInd = new ArrayList<String>();
						List<Sequence> 	lseq = new ArrayList<Sequence>();
						while( line != null ) {
							if( line.startsWith(">") ) {
								String subline = line.substring(1);
								corrInd.add( subline );
								seq = new Sequence( subline, null );
								lseq.add( seq );
							} else {
								if( seq != null ) seq.append( line );
							}
							
							line = br.readLine();
						}
						br.close();
						
						for( Sequence s : lseq ) {
							s.checkLengths();
						}
					
						/*if( lnode.size() > 0 && lnode.get(0).length != lseq.size()*lseq.size() ) {
							System.err.println( lnode.size() + "  " + lseq.size() );
							System.err.println( lseq.size()*lseq.size() + "  " + lnode.get(0).length );
							break;
						}*/
						double[] dmat = new double[ lseq.size()*lseq.size() ];
						Sequence.distanceMatrixNumeric(lseq, dmat, null, false, false, null);
						
						n = treeutil.neighborJoin(dmat, corrInd, null, false, true);
					} else {
						StringBuilder tree = new StringBuilder();
						InputStream is = url.openStream();
						BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
						String str = br.readLine();
						while( str != null ) {
							tree.append( str );
							str = br.readLine();
						}
						//treeutil = new TreeUtil( tree.toString(), false, null, null, false, null, null, false );
						treeutil.setLoc( 0 );
						n = treeutil.parseTreeRecursive( tree.toString(), false );
					}
				} else {
					StringBuilder tree = new StringBuilder();
					BufferedReader	br = new BufferedReader( new FileReader( path ) );
					String str = br.readLine();
					while( str != null ) {
						tree.append( str );
						str = br.readLine();
					}
					br.close();
					//treeutil = new TreeUtil( tree.toString(), false, null, null, false, null, null, false );
					treeutil.setLoc( 0 );
					n = treeutil.parseTreeRecursive( tree.toString(), false );
				}
				
				if( rootid == null ) rootid = n.firstLeaf().getId();
				Node root = n.findNode( rootid );
				if( root == null ) {
					System.err.println("ok");
				}
				Node rootparent = root.getParent();
				treeutil.setNode( n );
				treeutil.reroot( rootparent );
				Node newroot = treeutil.getNode();
				//System.err.println( rootparent.getNodes().size() );
				lnode.add( newroot );
			}
			
			if( lnode.size() == namePath.size() ) treestr = treeDistTree( treeutil, lnode, names );
		} catch( Exception e1 ) {
			e1.printStackTrace();
		}
		
		return treestr;
	}
	
	public static String treeDistTree( TreeUtil treeutil, List<Node> lnode, List<String> names ) {
		double[]	submat = new double[ lnode.size()*lnode.size() ];
		for( int i = 0; i < lnode.size(); i++ ) {
			submat[i*lnode.size()+i] = 0.0;
		}
		
		Node nod = lnode.get(0);
		double corri = treeutil.nDistance( nod, nod );
		
		for( int i = 0; i < lnode.size()-1; i++ ) {
			for( int k = i+1; k < lnode.size(); k++ ) {
				Node node1 = lnode.get(i);
				Node node2 = lnode.get(k);
				double corr = Math.exp( (treeutil.nDistance( node1, node2 ) - 2.0)*0.2 ) - 1.0;
				
				
				/*if( (names.get(i).equals("transcription_elongation_factor_NusA") && names.get(k).equals("ABC_transporter_permease")) || (names.get(k).equals("transcription_elongation_factor_NusA") && names.get(i).equals("ABC_transporter_permease")) ) {
					System.err.println( "corr" + corr );
				}*/
				if( corr == 0.0 ) System.err.println( names.get(i) + "  " + names.get(k) );
				
				submat[i*lnode.size()+k] = corr;
				submat[k*lnode.size()+i] = corr;
			}
		}
		
		/*StringBuilder tree = new StringBuilder();
		tree.append( "\t"+lnode.size()+"\n" );
		int i = 0;
		for( String name : names ) {
			tree.append( name );
			int k;
			for( k = i; k < i+lnode.size(); k++ ) {
				tree.append( "\t"+submat[k] );
			}
			i = k;
			
			tree.append("\n");
		}
		FileWriter distm = new FileWriter("/home/sigmar/distm.txt");
		distm.write( tree.toString() );
		distm.close();*/
		
		Node node = treeutil.neighborJoin(submat, names, null, false, true);
		return node.toString();
	}
	
	private static double[] parseDistance( int len, String[] lines, List<String> names ) {
		double[] dvals = new double[ len*len ];
		int m = 0;
		int u = 0;
		for( int i = 1; i < lines.length; i++ ) {
			String line = lines[i];
			String[] ddstrs = line.split("[ \t]+");
			if( !line.startsWith(" ") ) {
				m++;
				u = 0;
				
				//int si = ddstrs[0].indexOf('_');
				//String name = si == -1 ? ddstrs[0] : ddstrs[0].substring( 0, si );
				//console( "name: " + name );
				
				String name = ddstrs[0];
				names.add( name );
			}
			if( ddstrs.length > 2 ) {
				for( int k = 1; k < ddstrs.length; k++ ) {
					int idx = (m-1)*len+(u++);
					if( idx < dvals.length ) dvals[idx] = Double.parseDouble(ddstrs[k]);
					else System.err.println( m + " more " + u );
				}
			}
		}
		
		return dvals;
	}
	
	public void pyroSeq( Map<String,List<Double>>	specmap, Map<String,List<Double>>	specphmap ) {
		File dir = new File("/home/sigmar/pyro/locs");
		/*File[] ff = dir.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if( name.endsWith(".fna") ) return true;
				return false;
			}
		});*/
		
		File[] ff = { new File("/u0/all.fa") };
		
		try {
			//Map<String,String> nameHitMap = mapNameHit( new FileInputStream( "/home/sigmar/snaedis/snaedis.blastout" ), 95, true );
			Map<String,String> nameHitMap = serifier.mapNameHit( new FileInputStream( "/u0/all.blastout" ), -1, true );
			/*System.err.println( nameHitMap.size() );
			
			for( String key : nameHitMap.keySet() ) {
				System.err.println( key + "  " + nameHitMap.get(key) );
				break;
			}
			
			System.err.println( nameHitMap.get("HWBYD8R03DO0EO") );*/
			
			int countmissing = 0;
			for( File f : ff ) {
				//String loc = f.getName();
				//loc = loc.substring(0, loc.length()-4);
				
				//double heat = snaedisheatmap.get( loc );
				//double ph = snaedisphmap.get( loc );
				
				File nf = new File( "/u0/all.blastout" );//new File( dir, ""+f.getName()+".blastout" );
				System.err.println( "about to parse " + nf.getName() );
				List<Set<String>> cluster = serifier.makeBlastCluster( new FileInputStream( nf ), null, 1 );
				
				Map<String,String> headset = new HashMap<String,String>();
				for( Set<String> cl : cluster ) {
					Map<String,Integer> tegcount = new HashMap<String,Integer>();
					Map<String,Integer> tegpercmap = new HashMap<String,Integer>();
					Map<String,String> 	tegmap = new HashMap<String,String>();
					for( String c : cl ) {
						String tegperc = nameHitMap.get(c);						
						if( tegperc == null ) {
							System.err.println( c );
							countmissing++;
						} else if( tegperc.contains("Meiothermus") || tegperc.contains("Marinithermus") || tegperc.contains("Oceanithermus") || tegperc.contains( "Thermus" ) ) {
							String teg;
							int perc;
							if( tegperc.endsWith("%") ) {
								int i = tegperc.lastIndexOf('_');
								teg = tegperc.substring(0,i);
								perc = Integer.parseInt( tegperc.substring(i+1, tegperc.length()-1) );
							} else {
								teg = tegperc;
								perc = -1;
							}
							
							for( String spec : specmap.keySet() ) {
								if( teg.contains( spec ) ) {
									List<Double> dvals = specmap.get(spec);
									//dvals.add( heat );
									List<Double> phvals = specphmap.get(spec);
									//phvals.add( ph );
									break;
								}
							}
							
							if( !tegcount.containsKey( teg ) ) {
								tegmap.put( teg, c );
								tegpercmap.put( teg, perc );
								tegcount.put( teg, 1 );
							} else {
								if( tegpercmap.get( teg ) < perc ) {
									tegpercmap.put(teg, perc);
									tegmap.put( teg, c );
								}
								tegcount.put( teg, tegcount.get(teg)+1 );
							}
						}
					}
					for( String teg : tegmap.keySet() ) {
						String c = tegmap.get( teg );
						Integer perc = tegpercmap.get(teg);
						int count = tegcount.get(teg);
						if( count > 1 ) {
							System.err.println( count );
						}
						if( perc != null && perc != -1 ) headset.put( c, teg+"_"+perc+"%_"+count );
						else headset.put( c, teg+"_"+count );
					}
				}
				
				FileWriter fw = new FileWriter("/u0/ampliconnoise/"+f.getName().substring(0, f.getName().length()-4)+"_thermus.fna");
				serifier.trimFasta( new BufferedReader( new FileReader(f) ), fw, headset, false, false );
				fw.close();
			}
			System.err.println( countmissing );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ftpExtract( String basesave, String search ) {
		//String basesave = "/home/sigmar/ftpncbi/";
		
		String replace = null;
		String ftpsite = "ftp.ncbi.nih.gov";
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect( ftpsite );
			ftp.login("anonymous", "anonymous");
			
			String subdir = "/genomes/Bacteria/";
			ftp.cwd( subdir );
			FTPFile[] files = ftp.listFiles();
			for( FTPFile ftpfile : files ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith( search ) ) {
						if( !ftp.isConnected() ) {
							ftp.connect("ftp.ncbi.nih.gov");
							ftp.login("anonymous", "anonymous");
						}
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						int cnt = 1;
						for( FTPFile newftpfile : newfiles ) {
							String newfname = newftpfile.getName();
							if( newfname.endsWith(".gbk") ) {
								long size = newftpfile.getSize();
								String fwname;
								if( size > 3000000 ) fwname = fname+"_"+newfname;//+".gbk";
								else fwname = fname+"_p"+(cnt++)+"_"+newfname;//+".gbk";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								FileWriter fw = new FileWriter( basesave+fwname );
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
								BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
								String line = br.readLine();
								while( line != null ) {
									fw.write( line + "\n" );
									line = br.readLine();
								}
								is.close();
								//ftp.completePendingCommand();
								fw.close();
								System.err.println("done " + fname);
								
								try {
									addSequences( fwname, basesave+fwname, replace );
								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			
			subdir = "/genomes/Bacteria_DRAFT/";
			FileSystemManager fsManager = VFS.getManager();
			//FileObject ftpconn = fsManager.resolveFile( "ftp://anonymous:anonymous@"+ftpsite+ftppath );
			//FileObject[] files2 = ftpconn.getChildren();
			
			//for( FileObject ftpfile : files2 ) {
			//	String fname = ftpfile.getName().getBaseName();
			byte[] bb = new byte[30000000];
			ftp.cwd( subdir );
			FTPFile[] files2 = ftp.listFiles();
			for( FTPFile ftpfile : files2 ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith( search ) ) {
						//System.err.println( "erm "+fname );
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						//FileObject[] newfiles = ftpfile.getChildren();
						//amint cnt = 1;
						//for( FileObject newftpfile : newfiles ) {
						for( FTPFile newftpfile : newfiles ) {
							//String newfname = newftpfile.getName().getBaseName();
							String newfname = newftpfile.getName();
							if( newfname.endsWith("scaffold.gbk.tgz") ) {
								//long size = newftpfile.getSize();
								//String fwname = "";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								//InputStream is = newftpfile.getContent().getInputStream();
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
								System.err.println( "trying "+newfname + (is == null ? "critical" : "success" ) );
								//InputStream gis = is;
								GZIPInputStream gis = new GZIPInputStream( is );
								
								String filename = basesave+fname.substring(0,fname.length()-4)+".tar";
								if( gis != null ) {
									int r = gis.read(bb);
									FileOutputStream fos = new FileOutputStream( filename );
									while( r > 0 ) {
										System.err.println( "reading " + r );
										fos.write( bb, 0, r );
										
										r = gis.read(bb);
									}
									gis.close();
									fos.close();
								}
								
								//FileSystemManager fsManager = VFS.getManager();
								FileObject jarFile = fsManager.resolveFile( "tar://"+filename );

								// List the children of the Jar file
								FileObject[] children = jarFile.getChildren();
								//System.out.println( "Children of " + jarFile.getName().getURI() );
								int contig = 1;
								for ( int i = 0; i < children.length; i++ ) {
									FileObject child = children[i];
									FileOutputStream fos = new FileOutputStream( basesave+fname.substring(0,fname.length())+"_contig"+(contig++)+"_"+child.getName().getBaseName() );
									FileContent fc = child.getContent();
									InputStream sis = fc.getInputStream();
									int r = sis.read( bb );
									int total = r;
									
									while( r != -1 ) {
										fos.write( bb, 0, r );
										r = sis.read( bb );
										total += r;
									}
									fos.close();
									
									try {
										addSequences( fname, basesave+fname, replace );
									} catch (URISyntaxException e) {
										e.printStackTrace();
									}
								}
								
								//FileInputStream	fis = new FileInputStream( filename );
								
								/*TarInputStream tais = new TarInputStream( fis );
								TarEntry te = tais.getNextEntry();
								int contig = 1;
								// (int)te.getSize() ];
								
								while( te != null ) {
									//size = te.getSize();
									//if( size > 0 ) {
										byte[] bb = new byte[ 2048 ];
										
										FileOutputStream fos = new FileOutputStream( "/home/sigmar/ftpncbi/"+fname.substring(0,fname.length())+"_contig"+(contig++)+".gbk" );
										int r = tais.read( bb );
										int total = r;
										//fis.write( bb, 0, r );
										//System.err.println( te.getName() + "  " + total + "  " + size );
										
										while( r != -1 ) {
											fos.write( bb, 0, r );
											//System.err.println( te.getName() + "  " + total + "  " + size );
											r = tais.read( bb );
											total += r;
										}
										//IOUtils.copy(tis, fis, (int)ae.getSize());
										fos.close();
									//}
									te = tais.getNextEntry();
								}
								tais.close();
								fis.close();*/
								//ftp.completePendingCommand();
							}
						}
					}
				}
			}
			
			subdir = "/genomes/Viruses/";
			ftp.cwd( subdir );
			FTPFile[] files3 = ftp.listFiles();
			for( FTPFile ftpfile : files3 ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith( search ) ) {//fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
						if( !ftp.isConnected() ) {
							ftp.connect("ftp.ncbi.nih.gov");
							ftp.login("anonymous", "anonymous");
						}
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						int cnt = 1;
						for( FTPFile newftpfile : newfiles ) {
							String newfname = newftpfile.getName();
							System.err.println("trying " + newfname + " in " + fname);
							if( newfname.endsWith(".gbk") ) {
								System.err.println("in " + fname);
								long size = newftpfile.getSize();
								String fwname = fname+"_"+newfname;
								//if( size > 3000000 ) fwname = fname+".gbk";
								//else fwname = fname+"_p"+(cnt++)+".gbk";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								FileWriter fw = new FileWriter( basesave+fwname );
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream(); //ftp.retrieveFileStream( newfname );
								BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
								String line = br.readLine();
								while( line != null ) {
									fw.write( line + "\n" );
									line = br.readLine();
								}
								is.close();
								//ftp.completePendingCommand();
								fw.close();
								System.err.println("done " + fname);
								
								try {
									addSequences( fwname, basesave+fwname, replace );
								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SerifyApplet sa = new SerifyApplet();
		//sa.parseDistance(len, lines, names)( args );
		
		/*try {
			Map<String,String> nameHitMapOld = mapNameHit( new FileInputStream( "/home/sigmar/snaedis/snaedis.blastout" ), 95, false );
			Map<String,String> nameHitMapNew = mapNameHit( new FileInputStream( "/home/sigmar/snaedis/snaedis.blastout" ), 95, true );
			
			FileWriter	fw = new FileWriter("/home/sigmar/pyro_sim.fasta");
			FileReader fr = new FileReader("/home/sigmar/pyro_alignment_new.fasta");
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					int i = line.indexOf("HWB");
					if( i != -1 ) {
						int e = line.indexOf('[', i);
						String id = line.substring(i, e);
						if( nameHitMapOld.containsKey( id ) ) {
							String oldstr = nameHitMapOld.get(id).replace(' ', '_');
							String newstr = nameHitMapNew.get(id).replace(' ', '_');
							
							line = line.replace(oldstr, newstr);
						}
					}
				}
				fw.write( line + "\n" );
				line = br.readLine();
			}
			br.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*try {
			FileInputStream fis = new FileInputStream( "/scratch/sks17/"+args[0]+".blastout" );
			FileOutputStream fos = new FileOutputStream( "/scratch/sks17/"+args[0]+"_unioncluster2.txt" );
			sa.makeBlastCluster(fis, fos, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
	
		//sa.initMaps();
		
		/*try {
			Path p = Paths.get( "/home/sigmar/thermusmeta.tre" );
			byte[] bb = Files.readAllBytes( p );
			StringBuilder str = new StringBuilder( new String( bb ) );
			str = sa.replaceTreeColors( str );
			p = Paths.get( "/home/sigmar/thermusmeta_new.tre" );
			Files.write( p, str.toString().getBytes() );
		} catch (IOException e1) {
			e1.printStackTrace();
		}*
		
		Map<String,List<Double>>	specmap = new HashMap<String,List<Double>>();
		specmap.put( "antranikianii", new ArrayList<Double>() );
		specmap.put( "arciformis", new ArrayList<Double>() );
		specmap.put( "brockianus", new ArrayList<Double>() );
		specmap.put( "igniterrae", new ArrayList<Double>() );
		specmap.put( "islandicus", new ArrayList<Double>() );
		specmap.put( "scotoductus", new ArrayList<Double>() );
		
		Map<String,List<Double>>	specphmap = new HashMap<String,List<Double>>();
		specphmap.put( "antranikianii", new ArrayList<Double>() );
		specphmap.put( "arciformis", new ArrayList<Double>() );
		specphmap.put( "brockianus", new ArrayList<Double>() );
		specphmap.put( "igniterrae", new ArrayList<Double>() );
		specphmap.put( "islandicus", new ArrayList<Double>() );
		specphmap.put( "scotoductus", new ArrayList<Double>() );
		
		//corr
		//sa.corr();
		
		/*try {
			File f = new File( "/home/sigmar/union_16.blastout" );
			List<Set<String>> cluster = sa.makeBlastCluster( new FileInputStream( f ), null, false );
			FileOutputStream os = new FileOutputStream( "/home/sigmar/union_16.txt" );
			sa.writeClusters(os, cluster);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		//sa.pyroSeq( specmap, specphmap );
		/*for( String key : specmap.keySet() ) {
			List<Double> vals = specmap.get( key );
			double mean = 0.0;
			for( double val : vals ) mean += val;
			mean /= vals.size();
			
			double var = 0.0;
			for( double val : vals ) var += (val-mean)*(val-mean);
			var /= vals.size();
			double stdev = Math.sqrt( var );
			
			vals = specphmap.get( key );
			double meanph = 0.0;
			for( double val : vals ) meanph += val;
			meanph /= vals.size();
			
			double varph = 0.0;
			for( double val : vals ) varph += (val-meanph)*(val-meanph);
			varph /= vals.size();
			double stdevph = Math.sqrt( varph );
			
			System.err.println( key + "\t" +mean+"±"+stdev+"\t"+meanph+"±"+stdevph );
		}*/
		
		//mapFiles();
		
		/*try {
			FileReader fr = new FileReader("/home/sigmar/conc_40genes.dst");
			List<String>	llines = new ArrayList<String>();
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				llines.add( line );
				line = br.readLine();
			}
			br.close();
			String[] lines = llines.toArray( new String[ llines.size() ] );
			int len = Integer.parseInt( lines[0].trim() );
			List<String> names = new ArrayList<String>();
			double[] dvals = parseDistance( len, lines, names );
			
			TreeUtil tu = new TreeUtil();
			Node n = tu.neighborJoin(dvals, names, null, false, true);
			System.err.println( dvals.length + " " + names.size() );
			System.err.println( n );
			
			sa.majorityRuleConsensus(null, null, true);
			//majorityRuleConsensus(dvals, names);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		/*try {
			Map<String,String> mstr = mapNameHit( new FileInputStream("/u0/viggo_nt_10.blastout") );
			for( String key : mstr.keySet() ) {
				String val = mstr.get(key);
				if( val.contains("No hits") ) {
					mstr.put( key, "Unknown" );
				} else {
					int i = val.indexOf(' ');
					int k = val.indexOf("strain");
					if( k < 0 ) k = val.length();
					
					String newval = val.substring(i+1, k).trim().replace("Uncultured ", "");//.replace(' ', '_');
					i = newval.indexOf(' ');
					if( i > 0 ) newval = newval.substring(0,i);
					newval = newval.substring(0, 1).toUpperCase()+newval.substring(1);
					
					mstr.put( key, newval );
				}
			}
			doMapHitStuff( mstr, new FileInputStream("/u0/viggo_aligned.fasta"), new FileOutputStream("/u0/viggo_aligned_renamed_nt.fasta"), ";" );
		} catch( IOException e ) {
			e.printStackTrace();
		}*/
		
		/*Map<String,String> nmap = new HashMap<String,String>();
		File dir = new File( "/home/sigmar/thermusgenes_short/aligned/trees/" );
		File[] ff = dir.listFiles();
		for( File f : ff ) {
			String fname = f.getName();
			int i = fname.indexOf('.');
			if( i == -1 ) i = fname.length();
			String nodename = fname.substring(0, i).replace("(", "");
			nodename = nodename.replace(")", "");
			nodename = nodename.replace(",", "");
			nodename = nodename.replace("'", "");
			nmap.put( nodename, f.getAbsolutePath() );
		}
		
		String tree = genePhylogenyNNI( nmap, true );
		System.err.println( tree );*/
		//majorityRuleConsensus();
		
		/*List<Set<String>>	total = new ArrayList<Set<String>>();
		try {
			joinBlastSets( new FileInputStream("/u0/retry.blastout"), "/u0/union_16S2.txt", true, total );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*try {
			List<double[]>	ldmat = new ArrayList<double[]>();
			File f = new File( "/root/ermermerm/dist/" );
			File[] ff = f.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if( name.endsWith(".fasta") ) return true;
					return false;
				}
			});
			List<String> names = new ArrayList<String>();
			for( File tf : ff ) {
				names.add( tf.getName().replace("(", "").replace(")", "").replace("'", "") );
				BufferedReader br = new BufferedReader( new FileReader(tf) );
				String line = br.readLine();
				int c = Integer.parseInt(line.trim());
				double[] d = new double[ c*c ];
				ldmat.add( d );
				line = br.readLine();
				int k = 0;
				while( line != null ) {
					String[] split = line.split("[\t ]+");
					for( int i = 1; i < split.length; i++ ) {
						d[k] = Double.parseDouble( split[i] );
						k++;
					}
					line = br.readLine();
				}
				br.close();
			}
			
			double[]	submat = new double[ ldmat.size()*ldmat.size() ];
			for( int i = 0; i < ldmat.size(); i++ ) {
				submat[i*ldmat.size()+i] = 0.0;
			}
			
			double mincorr = Double.MAX_VALUE;
			int mini = 0;
			int mink = 0;
			for( int i = 0; i < ldmat.size()-1; i++ ) {
				for( int k = i+1; k < ldmat.size(); k++ ) {
					double[] dmat1 = ldmat.get(i);
					double[] dmat2 = ldmat.get(k);
					double corr = correlateDistance( dmat1, dmat2 );
					if( corr < mincorr ) {
						mincorr = corr;
						mini = i;
						mink = k;
					}
					submat[i*ldmat.size()+k] = corr;
					submat[k*ldmat.size()+i] = corr;
				}
			}
			System.err.println( mini + "  " + mink + "  " + mincorr );
			
			StringBuilder tree = new StringBuilder();
			tree.append( "\t"+ldmat.size()+"\n" );
			int i = 0;
			for( String name : names ) {
				tree.append( name );
				int k;
				for( k = i; k < i+ldmat.size(); k++ ) {
					tree.append( "\t"+submat[k] );
				}
				i = k;
				
				tree.append("\n");
			}
			String treestr = tree.toString();
			FileWriter fw = new FileWriter( "/home/sigmar/mat.txt" );
			fw.write( treestr );
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		/*try {
			List<String> names = new ArrayList<String>();
			FileReader f = new FileReader( "/root/ermermerm/aligned/tree/erm.txt" );
			BufferedReader br = new BufferedReader( f );
			String line = br.readLine();
			while( line != null ) {
				names.add( line.replace("(", "").replace(")", "").replace("'", "") );
				line = br.readLine();
			}
			br.close();
			
			f = new FileReader( "/root/ermermerm/aligned/tree/outfile" );
			br = new BufferedReader( f );
			line = br.readLine();
			double[] dmat = new double[403*403];
			int c = 0;
			int start = 0;
			while( line != null ) {
				int k = line.indexOf('|');
				if( k != -1 ) {
					String[] split = line.substring(k+1).trim().split("[ ]+");
					int i = start;
					for( String num : split ) {
						int n = Integer.parseInt(num);
						int ind = c*403+i;
						if( ind > dmat.length ) {
							System.err.println();
						}
						dmat[ind] = Math.exp( (double)n/5.0 )-1.0;
						i++;
					}
					c++;
					if( c == 403 ) {
						c = 0;
						start += 10;
					}
				}
				line = br.readLine();
			}
			br.close();
			f.close();
			
			FileWriter fw = new FileWriter("/home/sigmar/fwout.txt");
			/*fw.write("\t"+403);
			c = 0;
			for( double d : dmat ) {
				if( c % 403 == 0 ) fw.write( "\n"+names.get(c/403)+"\t"+d );
				else fw.write( "\t"+d );
				
				c++;
			}
			fw.write("\n");*
			TreeUtil tu = new TreeUtil();
			Node n = tu.neighborJoin(dmat, names, null);
			fw.write( n.toString() );
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			File f = new File( "/home/sigmar/thermus_union_cluster.txt" );
			FileInputStream is = new FileInputStream("/home/sigmar/sandbox/distann/src/thermus_join.blastout");
			FileOutputStream os = new FileOutputStream(f);
			SerifyApplet.makeBlastCluster( is, os );
			//SerifyApplet.blastJoin( is, new PrintStream(os) );
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		/*String[] lcmd = "/opt/ncbi-blast-2.2.25+/bin/blastp -query /home/horfrae/arciformis_3.aa -db /opt/db/nr  -num_alignments 1 -num_descriptions 1 -out /home/horfrae/arciformis_3.blastout".split("[ ]");
		Runnable run = new Runnable() {
			public void run() {
				
			}
		};
		Object[] cont = new Object[3];
		
		SerifyApplet a = new SerifyApplet();
		try {
			a.runProcessBuilder( "Performing blast", Arrays.asList(lcmd), run, cont );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*List<String>	largs = Arrays.asList( args );
		int i = largs.indexOf("--split");
		if( i != -1 ) {
			String filename;
			if( i == 0 ) filename = largs.get( largs.size()-1 );
			else filename = largs.get( 0 );
			
			File f = new File( filename );
			
			int chunks = Integer.parseInt( largs.get(i+1) );
			
			String type = "nucl";
			int nseq = 0;
			
			try {
				FileReader fr = new FileReader( f );
				BufferedReader br = new BufferedReader( fr );
				String line = br.readLine();
				while( line != null ) {
					if( line.startsWith(">") ) nseq++;
					else if( type.equals("nucl") && !line.matches("^[acgtnACGTN]+$") ) {
						type = "prot";
					}
					line = br.readLine();
				}
				fr.close();
				
				if( nseq > 0 ) {
					Sequences seqs = new Sequences( null, f.getName(), type, f.toURI().toString(), nseq );
					splitit( chunks, seqs, f.getParentFile(), null );
				} else System.err.println( "no sequences in file" );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800, 600 );
		
		/*String[] cmds = new String[] { "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\makeblastdb.exe", "-in", "c:\\\\Documents and settings\\sigmar\\Desktop\\erm.fna", "-out", "c:\\\\Documents and settings\\sigmar\\sim", "-title", "sim" };
		try {
			String result = sa.runProcessBuilder( "Something", Arrays.asList( cmds ), null, null );
			System.err.println( result );
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		sa.init( frame );
		frame.setVisible( true );
	}
}
