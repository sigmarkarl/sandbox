package org.simmi;

import java.awt.BorderLayout;
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
import java.io.ByteArrayOutputStream;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

public class SerifyApplet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTable				table;
	List<Sequences>		sequences;
	String globaluser = null;
	Container			cnt = null;
	
	public SerifyApplet() {
		super();
	}
	
	public List<Sequences> initSequences( int rowcount ) {
		List<Sequences>	seqs = new ArrayList<Sequences>();
		
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "getSequences", new Object[] {rowcount == 0} );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		/*String[] split = seqsStr.split("\n");
		
		for( String ss : split ) {
			String[] s = ss.split("\t");
			seqs.add( new Sequences(s[0], s[1], s[2], s[3], Integer.parseInt(s[4]) ) );
		}*/
		
		return seqs;
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
		} catch( Exception e ) {
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
						ret = f.get( datalist.get(rowIndex) );
						
						if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
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
	
	public File checkProdigalInstall( File dir ) throws IOException {
		File check = new File( dir, "prodigal.v2_60.windows.exe" );
		if( !check.exists() ) {
			check = installProdigal( dir );
		}
		return check;
	}
	
	public void checkInstall( File dir ) throws IOException {
		File check1 = new File( "/opt/ncbi-blast-2.2.25+/bin/blastp" );
		File check2 = new File( "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\blastp.exe" );
		if( !check1.exists() && !check2.exists() ) {
			File f = installBlast( dir );
		}
	}
	
	public void runProcess( String title, Runnable run, JDialog dialog ) {
		dialog.setTitle( title );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.setSize(400, 300);
		
		JComponent comp = new JComponent() {
			
		};
		comp.setLayout( new BorderLayout() );
		
		final JTextArea		ta = new JTextArea();
		ta.setEditable( false );
		final JScrollPane	sp = new JScrollPane( ta );
		final JProgressBar	pbar = new JProgressBar();
		
		dialog.add( comp );
		comp.add( pbar, BorderLayout.NORTH );
		comp.add( sp, BorderLayout.CENTER );
		pbar.setIndeterminate( true );
		
		Thread thread = new Thread( run );
		thread.start();
	}
	
	public File installBlast( final File homedir ) throws IOException {
		final URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.25/ncbi-blast-2.2.25+-win32.exe");
		String fileurl = url.getFile();
		String[] split = fileurl.split("/");
		
		final File f = new File( homedir, split[split.length-1] );
		final ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		final byte[] bb = new byte[100000];
		if( !f.exists() ) {
			final JDialog	dialog = new JDialog();
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

					try {
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 && !interrupted ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						
						if( !interrupted ) {
							FileOutputStream fos = new FileOutputStream( f );
							fos.write( baos.toByteArray() );
							fos.close();
							baos.close();
							
							byte[] bb = new byte[100000];
							
							String path = f.getAbsolutePath();
							//String[] cmds = new String[] { "wine", path };
							ProcessBuilder pb = new ProcessBuilder( path );
							pb.directory( homedir );
							Process p = pb.start();
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							is = p.getInputStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							is = p.getErrorStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							System.out.println( "erm " + baos.toString() );
							baos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			runProcess( "Downloading blast...", run, dialog );
		}
		
		return f;
	}
	
	public File installProdigal( final File homedir ) throws IOException {
		final URL url = new URL("http://prodigal.googlecode.com/files/prodigal.v2_60.windows.exe");
		String fileurl = url.getFile();
		String[] split = fileurl.split("/");
		
		final File f = new File( homedir, split[split.length-1] );
		if( !f.exists() ) {
			final JDialog dialog;
			Window window = SwingUtilities.windowForComponent(cnt);
			if( window != null ) dialog = new JDialog( window );
			else dialog = new JDialog();
			
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
					
					try {
						byte[] bb = new byte[100000];
						ByteArrayOutputStream	baos = new ByteArrayOutputStream();
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						FileOutputStream fos = new FileOutputStream( f );
						fos.write( baos.toByteArray() );
						fos.close();
						baos.close();
						
						doProdigal(homedir, SerifyApplet.this.cnt, f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			};
			runProcess("Downloading Prodigal ...", run, dialog);
			
			/*JProgressBar pb = new JProgressBar();
			pb.setIndeterminate( true );
			
			dialog.setTitle("Downloading Prodigal ...");
			dialog.add( pb );
			dialog.setVisible( true );
			
			InputStream is = url.openStream();
			int r = is.read(bb);
			while( r > 0 ) {
				baos.write( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			//f.mkdirs();
			FileOutputStream fos = new FileOutputStream( f );
			fos.write( baos.toByteArray() );
			fos.close();
			baos.close();
			
			dialog.setVisible( false );*/
			
			return null;
		}
		
		return f;
	}
	
	public void init() {
		try {
			JSObject jso = JSObject.getWindow( this );
			final JSObject con = (JSObject)jso.getMember("console");
		} catch( Exception e ) {
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
	
	public String fixPath( String path ) {
		String[] split = path.split("\\\\");
		String res = "";
		for( String s : split ) {
			if( s == split[0] ) res += s;
			else if( s.contains(" ") ) {
				s = s.replace(" ", "");
				if( s.length() > 8 ) {
					res += "\\"+s.substring(0, 6)+"~1";
				} else {
					res += "\\"+s;
				}
			} else {
				res += "\\"+s;
			}
		}
		
		return res;
	}
	
	public void deleteSequence( String key ) {
		Sequences selseq = null;
		for( Sequences s : sequences ) {
			if( key.equals( s.getKey() ) ) selseq = s;
		}
		if( selseq != null ) {
			sequences.remove( selseq );		
			table.tableChanged( new TableModelEvent(table.getModel()) );
		}
	}
	
	public static void splitit( int spin, Sequences seqs, File dir, SerifyApplet applet ) {
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
			
			int i = 0;
			FileWriter 	fw = null;
			File		of = null;
			FileReader 	fr = new FileReader( inf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( i%spin == 0 ) {
						if( fw != null ) {
							fw.close();
							
							if( applet != null ) {
								name = of.getName();
								ind = name.lastIndexOf('.');
								name = name.substring(0,ind);
								applet.addSequences(name, seqs.getType(), of.toURI().toString(), spin);
							}
						}
						of = new File( dir, sff + "_" + (i/spin+1) + "." + sf2 );
						fw = new FileWriter( of );
					}
					i++;
				}
				fw.write( line+"\n" );
				
				line = br.readLine();
			}
			if( fw != null ) {
				fw.close();
				if( applet != null ) {
					name = of.getName();
					ind = name.lastIndexOf('.');
					name = name.substring(0,ind);
					applet.addSequences(name, seqs.getType(), of.toURI().toString(), i%spin);
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
	
	public void getParameters() {
		JSObject js = JSObject.getWindow( SerifyApplet.this );
		js.call( "getBlastParameters", new Object[] {} );
	}
	
	public void runBlastInApplet( final String extrapar, final String dbPath, final String dbType ) {
		AccessController.doPrivileged( new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				try {
					String userhome = System.getProperty("user.home");
					File dir = new File( userhome );
					
					System.out.println("run blast in applet");
					String blasttype = dbType.equals("nucl") ? "blastn" : "blastp";
					File blast = new File( "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\" + blasttype+".exe" );
					if( !blast.exists() ) blast = new File( "/opt/ncbi-blast-2.2.25+/bin/"+blasttype );
					if( blast.exists() ) {
						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
						if( fc.showSaveDialog( SerifyApplet.this.cnt ) == JFileChooser.APPROVE_OPTION ) {
							File selectedfile = fc.getSelectedFile();
							if( !selectedfile.isDirectory() ) selectedfile = selectedfile.getParentFile();
						
							String dbPathFixed = fixPath( dbPath );
							int[] rr = table.getSelectedRows();
							for( int r : rr ) {
								String path = (String)table.getValueAt( r, 3 );
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
						
								String queryPathFixed = fixPath( infile.getAbsolutePath() ).trim();
								final String outPathFixed = fixPath( new File( selectedfile, title+".blastout" ).getAbsolutePath() ).trim();
								
								List<String>	lcmd = new ArrayList<String>();
								String[] cmds = { blast.getAbsolutePath(), "-query", queryPathFixed, "-db", dbPathFixed };
								String[] exts = extrapar.trim().split("[\t ]+");
								
								String[] nxst = { "-out", outPathFixed };
								lcmd.addAll( Arrays.asList(cmds) );
								if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
								lcmd.addAll( Arrays.asList(nxst) );
								
								final String start = new Date( System.currentTimeMillis() ).toString();								
								final Object[] cont = new Object[3];
								Runnable run = new Runnable() {
									public void run() {										
										infile.delete();
										//System.err.println( "ok " + (cont[0] == null ? "null" : "something else" ) );
										if( cont[0] != null ) {
											JSObject js = JSObject.getWindow( SerifyApplet.this );
											String machineinfo = getMachine();
											String[] split = machineinfo.split("\t");
											js.call( "addResult", new Object[] {getUser(), title, outPathFixed, split[0], start, cont[2], cont[1]} );
										}
									}
								};
								/*for( String cmd : lcmd ) {
									System.err.println(cmd);
								}
								Thread.sleep(10000);*/
								runProcessBuilder( "Performing blast", lcmd, run, cont );
							}
						}
					} else System.err.println( "no blast installed" );
				} catch( Exception e ) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
	}
	
	public void doProdigal( File dir, Container c, File f  ) throws IOException {		
		int r = table.getSelectedRow();
		String path = (String)table.getValueAt( r, 3 );
		URL url = new URL( path );
		
		String file = url.getFile();
		String[] split = file.split("/");
		String fname = split[ split.length-1 ];
		split = fname.split("\\.");
		final String title = split[0];
		File infile = new File( dir, fname );
		
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
			
			final String outPathD = fixPath( new File( selectedfile, title+".prodigal.fna" ).getAbsolutePath() );
			final String outPathA = fixPath( new File( selectedfile, title+".prodigal.fsa" ).getAbsolutePath() );
			String[] cmds = new String[] { f.getAbsolutePath(), "-i", fixPath( infile.getAbsolutePath() ), "-a", outPathA, "-d", outPathD };
			
			final Object[] cont = new Object[3];
			Runnable run = new Runnable() {
				public void run() {
					if( cont[0] != null ) {
						System.err.println( cont[0] );
						addSequences(title, "nucl", outPathD, 50);
						addSequences(title, "prot", outPathA, 50);
					}
				}
			};
			runProcessBuilder( "Running prodigal", Arrays.asList( cmds ), run, cont );
			//JSObject js = JSObject.getWindow( SerifyApplet.this );
			//js = (JSObject)js.getMember("document");
			//js.call( "addDb", new Object[] {getUser(), title, "nucl", outPath, result} );
		}
		
		//infile.delete();
	}
	
	private static void joinSets( Set<String> all, List<Set<String>> total ) {		
		Set<String> cont = null;
		Set<Set<String>>	rem = new HashSet<Set<String>>();
		int i = 0;
		for( Set<String>	check : total ) {			
			for( String aval : all ) {
				if( check.contains(aval) ) {
					if( cont == null ) {
						cont = check;
						check.addAll( all );
						break;
					} else {
						cont.addAll( check );
						rem.add( check );
						break;
					}
				}
			}
			
			i++;
		}
		
		for( Set<String> erm : rem ) {
			int ind = -1;
			int count = 0;
			for( Set<String> ok : total ) {
				if( ok.size() == erm.size() && ok.containsAll(erm) ) {
					ind = count;
					break;
				}
				count++;
			}
			
			if( ind != -1 ) {
				total.remove( ind );
			}
		}
		
		rem.clear();
		if( cont == null ) total.add( all );
		
		Set<String>	erm = new HashSet<String>();
		for( Set<String> ss : total ) {
			for( String s : ss ) {
				if( erm.contains( s ) ) {
					break;
				}
			}
			erm.addAll( ss );
		}
	}
	
	private static Collection<Set<String>> joinBlastSets( InputStream is, String write, boolean union ) throws IOException {
		List<Set<String>>	total = new ArrayList<Set<String>>();
		FileWriter fw = write == null ? null : new FileWriter( write ); //new FileWriter("/home/sigmar/blastcluster.txt");
		BufferedReader	br = new BufferedReader( new InputStreamReader( is ) );
			
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith("Sequences prod") ) {
				line = br.readLine();
				Set<String>	all = new HashSet<String>();
				while( line != null && !line.startsWith(">") ) {
					String trim = line.trim();
					if( trim.startsWith("t.scoto") || trim.startsWith("t.antr") || trim.startsWith("t.aqua") || trim.startsWith("t.t") || trim.startsWith("t.egg") || trim.startsWith("t.island") || trim.startsWith("t.oshi") || trim.startsWith("t.brock") || trim.startsWith("t.fili") ) {
						String val = trim.substring( 0, trim.indexOf('#')-1 );
						int v = val.indexOf("contig");
						all.add( val );
					}
					line = br.readLine();
				}
				
				if( fw != null ) fw.write( all.toString()+"\n" );
				
				if( union ) joinSets( all, total );
				//else intersectSets( all, total );
				
				if( line == null ) break;
			}
			
			line = br.readLine();
		}
		if( fw != null ) fw.close();
		
		return total;
	}
	
	private static Map<Set<String>,Set<Map<String,Set<String>>>> initCluster( Collection<Set<String>>	total, Set<String> species ) {
		Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		for( Set<String>	t : total ) {
			Set<String>	teg = new HashSet<String>();
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf('_') );
				/*if( joinmap.containsKey( str ) ) {
					str = joinmap.get(str);
				}*/
				teg.add( str );
				
				species.add(str);
			}
			
			Set<Map<String,Set<String>>>	setmap;
			if( clusterMap.containsKey( teg ) ) {
				setmap = clusterMap.get( teg );
			} else {
				setmap = new HashSet<Map<String,Set<String>>>();
				clusterMap.put( teg, setmap );
			}
			
			Map<String,Set<String>>	submap = new HashMap<String,Set<String>>();
			setmap.add( submap );
			
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf('_') );
				/*if( joinmap.containsKey( str ) ) {
					str = joinmap.get(str);
				}*/
				
				Set<String>	set;
				if( submap.containsKey( str ) ) {
					set = submap.get(str);
				} else {
					set = new HashSet<String>();
					submap.put( str, set );
				}
				set.add( e );
			}
		}
		
		return clusterMap;
	}
	
	private static void writeSimplifiedCluster( OutputStream os, Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap ) throws IOException {
		OutputStreamWriter	fos = new OutputStreamWriter( os );
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	mapset = clusterMap.get( set );
			fos.write( set.toString()+"\n" );
			int i = 0;
			for( Map<String,Set<String>> erm : mapset ) {
				fos.write((i++)+"\n");
				
				for( String erm2 : erm.keySet() ) {
					Set<String>	erm3 = erm.get(erm2);
					fos.write("\t"+erm2+"\n");
					fos.write("\t\t"+erm3.toString()+"\n");
				}
			}
		}
		fos.close();
	}
	
	public void blastClusters( final InputStream is, final OutputStream os ) {
		final JDialog	dialog = new JDialog();
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
				
				try {
					Set<String>	species = new TreeSet<String>();
					Collection<Set<String>> total = joinBlastSets( is, null, true );
					if( !interrupted ) {
						Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
						//if( writeSimplifiedCluster != null ) 
						writeSimplifiedCluster( os, clusterMap );
						//writeBlastAnalysis( clusterMap, species );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		runProcess( "Blast clusters", run, dialog );
	}
	
	public void blastJoin( InputStream is, OutputStream os ) throws IOException {
		InputStreamReader 		fr = new InputStreamReader( is );
		BufferedReader 	br = new BufferedReader( fr );
		String line = br.readLine();
		
		PrintStream ps = new PrintStream( os );
		
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
				
				Map<List<String>,Integer>	joinMap = new HashMap<List<String>,Integer>();
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
										
										if( joinMap.containsKey( joinset ) ) {
											joinMap.put( joinset, joinMap.get(joinset)+1 );
										} else {
											joinMap.put( joinset, 1 );
										}
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
				Map<Integer,List<List<String>>>	reverseset = new TreeMap<Integer,List<List<String>>>( Collections.reverseOrder() );
				for( List<String> joinset : joinMap.keySet() ) {
					int cnt = joinMap.get(joinset);
					
					if( joinset.get(0).contains("all") || joinset.get(1).contains("all") ) cnt -= 1000; 
					
					if( reverseset.containsKey(cnt) ) {
						List<List<String>>	joinlist = reverseset.get(cnt);
						joinlist.add( joinset );
					} else {
						List<List<String>> joinlist = new ArrayList<List<String>>();
						joinlist.add( joinset );
						reverseset.put(cnt, joinlist);
					}
				}
				
				for( int cnt : reverseset.keySet() ) {
					List<List<String>>	joinlist = reverseset.get(cnt);
					for( List<String> joinset : joinlist ) {
						ps.println( joinset + ": " + cnt);
					}
				}
					
					for( int cnt : reverseset.keySet() ) {
						List<List<String>>	joinlist = reverseset.get(cnt);
						for( List<String> joinset : joinlist ) {
							
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
	
	public static int trimFasta( BufferedReader br, BufferedWriter bw, Set<String> filterset, boolean inverted ) throws IOException {
		int nseq = 0;
		
		String line = br.readLine();
		String seqname = null;
		while( line != null ) {
			if( line.startsWith(">") ) {
				nseq++;
				if( inverted ) {
					seqname = line;
					for( String f : filterset ) {
						if( line.contains(f) ) {
							seqname = null;
							break;
						}
					}
					if( seqname != null ) bw.write( seqname+"\n" );
				} else {
					seqname = null;
					for( String f : filterset ) {
						if( line.contains(f) ) {
							bw.write( line+"\n" );
							seqname = line;
							break;
						}
					}
				}
			} else if( seqname != null ) {
				bw.write( line+"\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		bw.flush();
		bw.close();
		
		return nseq;
	}
	
	public Map<String,String> mapNameHit( InputStream blasti ) throws IOException {
		Map<String,String>	mapHit = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader( new InputStreamReader( blasti ) );
		String line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith("Query=") ) {
				String name = trim.substring(6).trim();
				line = br.readLine();
				while( line != null && !line.startsWith(">") ) line = br.readLine();
				if( line != null ) {
					mapHit.put( name, line.substring(1).trim() );
				}
			}
			line = br.readLine();
		}
		br.close();
		
		return mapHit;
	}
	
	public int doMapHitStuff( Map<String,String> mapHit, InputStream is, OutputStream os ) throws IOException {
		int nseq = 0;
		PrintStream pr = new PrintStream( os );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		String line = br.readLine();
		boolean include = false;
		while( line != null ) {
			if( line.startsWith(">") ) {
				String name = line.substring(1).trim();
				if( mapHit.containsKey(name) ) {
					nseq++;
					pr.println( ">" + name + "_" + mapHit.get(name) );
					include = true;
				} else include = false;
			} else if( include ) {
				pr.println( line );
			}
			line = br.readLine();
		}
		br.close();
		pr.close();
		
		return nseq;
	}
	
	public void init( final Container c ) {
		this.cnt = c;
		globaluser = System.getProperty("user.name");
		
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
		
		sequences = initSequences( table.getRowCount() );
		initMachines();
		table.setAutoCreateRowSorter( true );
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
		table.addKeyListener( new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if( keycode == KeyEvent.VK_DELETE ) {
					Set<String>	keys = new HashSet<String>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int ind = table.convertRowIndexToModel( r );
						if( ind >= 0 ) {
							Sequences seqs = sequences.get(ind);
							keys.add( seqs.getKey() );
							
							//sequences.remove( ind );
							//table.tableChanged( new TableModelEvent(table.getModel()) );
						}
					}
					
					try {
						JSObject js = JSObject.getWindow( SerifyApplet.this );
						for( String key : keys ) {
							js.call( "deleteSequenceKey", new Object[] {key} );
						}
					} catch( Exception e1 ) {
						e1.printStackTrace();
					}
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
		popup.add( new AbstractAction("Create database") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					
					checkInstall( dir );
						
					File makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\makeblastdb.exe" );
					if( !makeblastdb.exists() ) makeblastdb = new File( "/opt/ncbi-blast-2.2.25+/bin/makeblastdb" );
					if( makeblastdb.exists() ) {
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
							
							final String outPath = fixPath( new File( selectedfile, title ).getAbsolutePath() );
							String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", fixPath( infile.getAbsolutePath() ), "-title", title, "-dbtype", dbtype, "-out", outPath };
							
							final Object[] cont = new Object[3];
							Runnable run = new Runnable() {
								public void run() {
									if( cont[0] != null ) {
										infile.delete();
									
										if( cont[0] != null ) {
											JSObject js = JSObject.getWindow( SerifyApplet.this );
											//js = (JSObject)js.getMember("document");
										
											String machineinfo = getMachine();
											String[] split = machineinfo.split("\t");
											js.call( "addDb", new Object[] {getUser(), title, dbtype, outPath, split[0], cont[1]} );
										}
									}
								}
							};
							runProcessBuilder( "Creating database", Arrays.asList( cmds ), run, cont );
						}
						
						//infile.delete();
					} else System.err.println( "no blast installed" );
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
						title = title.substring(0,i);
					}
					
					final String outPath = fixPath( selectedfile.getParentFile().getAbsolutePath()+System.getProperty("file.separator")+title );
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
				try {
					String userhome = System.getProperty("user.home");
					File dir = new File( userhome );
					
					checkInstall( dir );
						
					getParameters();					
					//infile.delete();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
		
		popup.add( new AbstractAction("tBlast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		popup.addSeparator();
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
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					File f = checkProdigalInstall( dir );
					if( f != null && f.exists() ) {
						doProdigal( dir, c, f );
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
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					
					int r = table.getSelectedRow();
					int rr = table.convertRowIndexToModel( r );
					if( rr >= 0 ) {
						final Sequences seqs = sequences.get( rr );
						
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
							addSequences(file.getName(), seqs.type, file.toURI().toURL().toString(), nseq);
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
		popup.add( new AbstractAction("Join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				//fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					//if( !f.isDirectory() ) f = f.getParentFile();
					
					try {
						FileWriter fw = new FileWriter( f );
						int[] rr = table.getSelectedRows();
						String seqtype = "nucl";
						String joinname = f.getName();
						int nseq = 0;
						for( int r : rr ) {
							int rear = table.convertRowIndexToModel( r );
							if( rear >= 0 ) {
								Sequences s = sequences.get( rear );
								
								seqtype = s.getType();
								//if( joinname == null ) joinname = s.getName();
								//else joinname += "_"+s.getName();
								
								File inf = new File( new URI(s.getPath()) );
								BufferedReader br = new BufferedReader( new FileReader(inf) );
								String line = br.readLine();
								while( line != null ) {
									if( line.startsWith(">") ) {
										fw.write( line.replace( ">", ">"+s.getName()+"_" )+"\n" );
										nseq++;
									}
									else fw.write( line+"\n" );
									line = br.readLine();
								}
								br.close();
							}
						}
						fw.close();
						
						SerifyApplet.this.addSequences( joinname, seqtype, f.toURI().toString(), nseq);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("Split") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					
					int r = table.getSelectedRow();
					int rr = table.convertRowIndexToModel( r );
					if( rr >= 0 ) {
						final Sequences seqs = sequences.get( rr );
						final JSpinner spinner = new JSpinner();
						spinner.setValue( seqs.getNSeq() );
						spinner.setPreferredSize( new Dimension(100,25) );

						final JDialog dl;
						Window window = SwingUtilities.windowForComponent(cnt);
						if( window != null ) dl = new JDialog( window );
						else dl = new JDialog();
						
						dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
						JComponent c = new JComponent() {
							
						};
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
								splitit( spin, seqs, dir, SerifyApplet.this );
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
			}
		});
		popup.add( new AbstractAction("Trim") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					
					int r = table.getSelectedRow();
					int rr = table.convertRowIndexToModel( r );
					if( rr >= 0 ) {
						final Sequences seqs = sequences.get( rr );
						final JTextField spinner = new JTextField();
						//spinner.setValue( seqs.getNSeq() );
						spinner.setPreferredSize( new Dimension(100,25) );

						final JDialog dl;
						Window window = SwingUtilities.windowForComponent(cnt);
						if( window != null ) dl = new JDialog( window );
						else dl = new JDialog();
						
						dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
						JComponent c = new JComponent() {
							
						};
						c.setLayout( new FlowLayout() );
						dl.setTitle("Filter sequences");
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
								try {
									String trim = spinner.getText();
									
									URI uri = new URI( seqs.getPath() );
									InputStream is = uri.toURL().openStream();
									
									if( seqs.getPath().endsWith(".gz") ) {
										is = new GZIPInputStream( is );
									}
									
									String name = uri.toURL().getFile();
									int ind = name.lastIndexOf('.');
									
									String sff = name;
									String sf2 = "";
									if( ind != -1 ) {
										sff = name.substring(0, ind);
										sf2 = name.substring(ind+1,name.length());
									}
									
									String trimname = sff+"_trimmed";
									File f = new File( trimname+"."+sf2 );
									FileWriter fw = new FileWriter(f);
									
									String[] farray = { trim };
									
									int nseq = trimFasta( new BufferedReader( new InputStreamReader( is ) ), new BufferedWriter( fw ), new HashSet<String>( Arrays.asList( farray ) ), false);
									
									SerifyApplet.this.addSequences(trimname, seqs.getType(), f.toURI().toString(), nseq);
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (URISyntaxException e1) {
									e1.printStackTrace();
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
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Blast join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSObject	jso = JSObject.getWindow(SerifyApplet.this);
				String s = (String)jso.call("getSelectedBlast", new Object[] {} );
				
				JFileChooser fc = new JFileChooser();
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						blastJoin( new FileInputStream(s), new FileOutputStream(f) );
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
				JSObject	jso = JSObject.getWindow(SerifyApplet.this);
				String s = (String)jso.call("getSelectedBlast", new Object[] {} );
				
				JFileChooser fc = new JFileChooser();
				if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						blastClusters( new FileInputStream(s), new FileOutputStream(f) );
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Blast rename") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int rr = table.convertRowIndexToModel( r );
				if( rr >= 0 ) {
					JFileChooser fc = new JFileChooser();
					if( fc.showSaveDialog( cnt ) == JFileChooser.APPROVE_OPTION ) {
						File f = fc.getSelectedFile();
						
						final Sequences seqs = sequences.get( rr );
						JSObject	jso = JSObject.getWindow(SerifyApplet.this);
						String s = (String)jso.call("getSelectedBlast", new Object[] {} );
					
						try {
							URI uri = new URI( seqs.getPath() );
							InputStream is = uri.toURL().openStream();
							
							if( seqs.getPath().endsWith(".gz") ) {
								is = new GZIPInputStream( is );
							}
							
							Map<String,String> nameHitMap = mapNameHit( new FileInputStream(s) );
							int nseq = doMapHitStuff( nameHitMap, is, new FileOutputStream(f) );
							
							SerifyApplet.this.addSequences(f.getName(), seqs.getType(), f.toURI().toString(), nseq );
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		table.setComponentPopupMenu( popup );
		
		JScrollPane	scrollpane = new JScrollPane( table );
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
							addSequences( f.getName(), f.toURI().toString() );
						}
					} else if( obj instanceof Image ) {
						
					} else if( obj instanceof String ) {
						System.err.println("String");
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						String filelistStr = (String)obj;
						String[] fileStr = filelistStr.split("\n");
						
						System.err.println( filelistStr );
						for( String fileName : fileStr ) {
							String val = fileName.trim();
							//File f = new File( new URI( fileName ) );
							String[] split = val.split("/");
							addSequences( split[ split.length-1 ], val );
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
							addSequences( split[ split.length-1 ], val.trim() );
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
							addSequences( split[ split.length-1 ], val );
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
		AccessController.doPrivileged( new PrivilegedAction() {
			@Override
			public Object run() {
				new Thread() {
					public void run() {
						boolean succ = true;
						try {
							URL url = new URL( path );
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
							Sequences seqs = new Sequences( user, name, type, path, nseq );
							seqs.setKey( key );
							sequences.add( seqs );
							table.tableChanged( new TableModelEvent( table.getModel() ) );
						}
					}
				}.start();
				
				return null;
			}
		});
	}
	
	private void addSequences( String user, String name, String type, String path, int nseq ) {
		boolean unsucc = false;
		try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "addSequences", new Object[] {user, name, type, path, nseq} );
		} catch( Exception e ) {
			unsucc = true;
		}
		
		if( unsucc ) {
			updateSequences(user, name, type, path, nseq, null);
		}
	}
	
	private void addSequences( String name, String path ) throws URISyntaxException, IOException {
		String type = "nucl";
		int nseq = 0;
		
		URL url = new URL(path);
		InputStream is = url.openStream();
		if( path.endsWith(".gz") ) is = new GZIPInputStream(is);
		InputStreamReader isr = new InputStreamReader( is );
		//FileReader	fr = new FileReader( f );
		BufferedReader br = new BufferedReader( isr );
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				nseq++;
				
				if( nseq % 1000 == 0 ) System.err.println( "seq counting: "+nseq );
			}
			else if( type.equals("nucl") && !line.matches("^[acgtykvrswmnxACGTDYKVRSWMNX]+$") ) {
				System.err.println( line );
				type = "prot";
			}
			line = br.readLine();
		}
		br.close();
		
		if( nseq > 0 ) {
			addSequences(name, type, path, nseq);
		} else System.err.println( "no sequences in file" );
	}
	
	private void addSequences( String name, String type, String path, int nseq ) {
		addSequences( getUser(), name, type, path, nseq );
	}
	
	public String getUser() {
		return globaluser;
	}
	
	public String runProcessBuilder( String title, final List<String> commands, final Runnable run, final Object[] cont ) throws IOException {
		//System.err.println( pb.toString() );
		//pb.directory( dir );
		
		final JDialog	dialog = new JDialog();
		dialog.setTitle( title );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.setSize(400, 300);
		
		JComponent comp = new JComponent() {
			
		};
		comp.setLayout( new BorderLayout() );
		
		final JTextArea		ta = new JTextArea();
		for( String cmd : commands ) {
			ta.append(cmd+" ");
		}
		ta.append("\n");
		
		ta.setEditable( false );
		final JScrollPane	sp = new JScrollPane( ta );
		final JProgressBar	pbar = new JProgressBar();
		
		dialog.add( comp );
		comp.add( pbar, BorderLayout.NORTH );
		comp.add( sp, BorderLayout.CENTER );
		pbar.setIndeterminate( true );
		
		System.err.println( "about to run" );
		for( String c : commands ) {
			System.err.print( c+" " );
		}
		System.err.println();
		
		Runnable runnable = new Runnable() {
			boolean interupted = false;
			
			@Override
			public void run() {
				try {
					ProcessBuilder pb = new ProcessBuilder( commands );
					final Process p = pb.start();
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
							if( p != null ) {
								interupted = true;
								p.destroy();
								//tt.interrupt();
							}
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );
					
					InputStream is = p.getInputStream();
					BufferedReader br = new BufferedReader( new InputStreamReader(is) );
					String line = br.readLine();
					while( line != null ) {
						String str = line + "\n";
						ta.append( str );
					
						line = br.readLine();
					}
					br.close();
					is.close();
					
					is = p.getErrorStream();
					br = new BufferedReader( new InputStreamReader(is) );
					
					line = br.readLine();
					while( line != null ) {
						String str = line + "\n";
						ta.append( str );
						
						line = br.readLine();
					}
					br.close();
					is.close();
					
					String result = ta.getText().trim();
					if( run != null ) {
					
						cont[0] = interupted ? null : ""; 
						cont[1] = result;
						cont[2] = new Date( System.currentTimeMillis() ).toString();
						run.run();
					
					}
					
					pbar.setIndeterminate( false );
					pbar.setEnabled( false );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		final Thread trd = new Thread( runnable );
		trd.start();
		
		//dialog.setVisible( false );
		return "";
	}
	
	public static void main(String[] args) {
		SerifyApplet sa = new SerifyApplet();
		
		try {
			File f = new File( "/home/horfrae/result.txt" );
			FileInputStream is = new FileInputStream("/home/horfrae/454AllContigs_flanking.blastout");
			FileOutputStream os = new FileOutputStream(f);
			sa.blastJoin( is, os );
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		}
		/*JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800, 600 );
		
		SerifyApplet	sa = new SerifyApplet();
		
		String[] cmds = new String[] { "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\makeblastdb.exe", "-in", "c:\\\\Documents and settings\\sigmar\\Desktop\\erm.fna", "-out", "c:\\\\Documents and settings\\sigmar\\sim", "-title", "sim" };
		try {
			String result = sa.runProcessBuilder( "Something", Arrays.asList( cmds ), null, null );
			System.err.println( result );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//sa.init( frame );
		
		frame.setVisible( true );*/
	}
}
