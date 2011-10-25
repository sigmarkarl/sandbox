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
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			
			String sff = name.substring(0, ind);
			String sf2 = name.substring(ind+1,name.length());
			
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
		System.err.println("ff");
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
								
								System.err.println( "nane" );
								for( String ext : exts ) {
									System.err.println( ext );
								}
								System.err.println( "bubu" );
								
								String[] nxst = { "-out", outPathFixed };
								lcmd.addAll( Arrays.asList(cmds) );
								if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
								lcmd.addAll( Arrays.asList(nxst) );
								
								final String start = new Date( System.currentTimeMillis() ).toString();								
								final Object[] cont = new Object[3];
								Runnable run = new Runnable() {
									public void run() {										
										infile.delete();
										System.err.println( "ok " + (cont[0] == null ? "null" : "something else" ) );
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
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
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
					int i = title.indexOf('.');
					if( i != -1 ) title = title.substring(0,i);
					
					final String outPath = fixPath( selectedfile.getParentFile().getAbsolutePath()+System.getProperty("file.separator")+title );
					//String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", fixPath( infile.getAbsolutePath() ), "-out", outPath, "-title", title };
					
					JSObject js = JSObject.getWindow( SerifyApplet.this );
					//js = (JSObject)js.getMember("document");
					
					String machineinfo = getMachine();
					String[] split = machineinfo.split("\t");
					js.call( "addDb", new Object[] {getUser(), title, "prot", outPath, split[0], ""} );
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
						for( int r : rr ) {
							int rear = table.convertRowIndexToModel( r );
							if( rear >= 0 ) {
								Sequences s = sequences.get( rear );
								File inf = new File( new URI(s.getPath()) );
								BufferedReader br = new BufferedReader( new FileReader(inf) );
								String line = br.readLine();
								while( line != null ) {
									if( line.startsWith(">") ) fw.write( line.replace( ">", ">"+s.getName()+"_" )+"\n" );
									else fw.write( line+"\n" );
									line = br.readLine();
								}
								br.close();
							}
						}
						fw.close();
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
	
	public void updateSequences( String user, String name, String type, String path, int nseq, String key ) {
		Sequences seqs = new Sequences( user, name, type, path, nseq );
		seqs.setKey( key );
		sequences.add( seqs );
		table.tableChanged( new TableModelEvent( table.getModel() ) );
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
		InputStreamReader isr = new InputStreamReader( url.openStream() );
		//FileReader	fr = new FileReader( f );
		BufferedReader br = new BufferedReader( isr );
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) nseq++;
			else if( type.equals("nucl") && !line.matches("^[acgtnxACGTNX]+$") ) {
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
		
		String[] lcmd = "/opt/ncbi-blast-2.2.25+/bin/blastp -query /home/horfrae/arciformis_3.aa -db /opt/db/nr  -num_alignments 1 -num_descriptions 1 -out /home/horfrae/arciformis_3.blastout".split("[ ]");
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
