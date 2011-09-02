package org.simmi;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

public class SerifyApplet extends JApplet {
	JTable				table;
	List<Sequences>		sequences;
	
	public SerifyApplet() {
		super();
	}
	
	class Sequences {
		public Sequences( String user, String name, String type, String path, int nseq ) {
			this.user = user;
			this.name = name;
			this.type = type;
			this.path = path;
			this.nseq = nseq;
		}
		
		String user;
		String name;
		String type;
		String path;
		Integer nseq;
	};
	
	public List<Sequences> initSequences() {
		List<Sequences>	seqs = new ArrayList<Sequences>();
		
		/*JSObject js = JSObject.getWindow( SerifyApplet.this );
		String seqsStr = (String)js.call( "getSequences", new Object[] {} );
		String[] split = seqsStr.split("\n");
		
		for( String ss : split ) {
			String[] s = ss.split("\t");
			seqs.add( new Sequences(s[0], s[1], s[2], s[3], Integer.parseInt(s[4]) ) );
		}*/
		
		return seqs;
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
	
	public void checkInstall( File dir ) throws IOException {
		File check1 = new File( dir, "bin/blastp.exe" );
		File check2 = new File( "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\blastp.exe" );
		if( !check1.exists() && !check2.exists() ) {
			File f = installBlast( dir );
			byte[] bb = new byte[100000];
			
			String path = f.getAbsolutePath();
			//String[] cmds = new String[] { "wine", path };
			ProcessBuilder pb = new ProcessBuilder( path );
			pb.directory( dir );
			Process p = pb.start();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = p.getInputStream();
			int r = is.read(bb);
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
	}
	
	public File installBlast( File homedir ) throws IOException {
		URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.25/ncbi-blast-2.2.25+-win32.exe");
		String fileurl = url.getFile();
		String[] split = fileurl.split("/");
		
		File f = new File( homedir, split[split.length-1] );
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		byte[] bb = new byte[100000];
		if( !f.exists() ) {
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
		}
		
		return f;
	}
	
	public void init() {
		init( this );
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
	
	public void init( final Container c ) {
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
		
		sequences = initSequences();
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		TableModel model = createModel( sequences, Sequences.class );
		table.setModel( model );
		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				if( me.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					String path = (String)table.getValueAt( r, 2 );
					
					try {
						SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					/*try {
						Desktop.getDesktop().browse( new URI( path ) );
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}*/
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
					if( makeblastdb.exists() ) {
						int r = table.getSelectedRow();
						String path = (String)table.getValueAt( r, 3 );
						URL url = new URL( path );
						
						String file = url.getFile();
						String[] split = file.split("/");
						String fname = split[ split.length-1 ];
						split = fname.split("\\.");
						String title = split[0]; 
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
							
							String outPath = fixPath( new File( selectedfile, title ).getAbsolutePath() );
							String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", fixPath( infile.getAbsolutePath() ), "-out", outPath, "-title", title };
							String result = runProcessBuilder( Arrays.asList( cmds ) );
							
							JSObject js = JSObject.getWindow( SerifyApplet.this );
							//js = (JSObject)js.getMember("document");
							js.call( "addDb", new Object[] {getUser(), "erm", outPath, result} );
							
							System.out.println( "erm " + result );
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
		
		popup.add( new AbstractAction("Blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					
					checkInstall( dir );
						
					File blast = new File( "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\blastp.exe" );
					if( blast.exists() ) {
						int r = table.getSelectedRow();
						String path = (String)table.getValueAt( r, 2 );
						URL url = new URL( path );
						
						String file = url.getFile();
						String[] split = file.split("/");
						String fname = split[ split.length-1 ];
						split = fname.split("\\.");
						String title = split[0]; 
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
							
							JSObject js = JSObject.getWindow( SerifyApplet.this );
							String dbPath = (String)js.call( "getSelectedDb", new Object[] {} );
							
							String[] cmds = new String[] { blast.getAbsolutePath(), "-query", fixPath( infile.getAbsolutePath() ), "-db", fixPath( dbPath ), "-out", fixPath( new File( selectedfile, title ).getAbsolutePath() ) };
							String result = runProcessBuilder( Arrays.asList( cmds ) );
							
							js.call( "addResult", new Object[] {getUser(), "erm", "erm", result} );
							
							System.out.println( "erm " + result );
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
		
		popup.add( new AbstractAction("tBlast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		table.setComponentPopupMenu( popup );
		
		JScrollPane	scrollpane = new JScrollPane( table );
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
				
				try {
					if( obj != null && obj instanceof List ) {
						List<File> lf = (List<File>)obj;
						for( File f : lf ) {
							addSequences( f.getName(), f.toURI().toString(), "dna", 50 );
						}
					} else if( obj instanceof Image ) {
						
					} else {
						obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						String filelistStr = (String)obj;
						String[] fileStr = filelistStr.split("\r\n");
						
						for( String fileName : fileStr ) {
							File f = new File( new URI( fileName ) );
							addSequences( f.getName(), "dna", f.toURI().toString(), 50 );
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
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
	
	public void updateSequences( String user, String name, String type, String path, int nseq ) {
		sequences.add( new Sequences( user, name, type, path, nseq ) );
		table.tableChanged( new TableModelEvent( table.getModel() ) );
	}
	
	private void addSequences( String user, String name, String type, String path, int nseq ) {
		System.err.println("adding sequences in applet");
		
		JSObject js = JSObject.getWindow( SerifyApplet.this );
		js.call( "addSequences", new Object[] {user, name, type, path, nseq} );
	}
	
	private void addSequences( String name, String type, String path, int nseq ) {
		addSequences( getUser(), name, type, path, nseq );
	}
	
	public String getUser() {
		return System.getProperty("user.name");
	}
	
	public String runProcessBuilder( List<String> commands ) throws IOException {
		ProcessBuilder pb = new ProcessBuilder( commands );
		//System.err.println( pb.toString() );
		//pb.directory( dir );
		
		byte[] bb = new byte[100000];
		for( String s : pb.command() ) {
			System.err.println( s );
		}
		
		Process p = pb.start();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = p.getInputStream();
		int r = is.read(bb);
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
		
		String result = baos.toString();
		return result;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800, 600 );
		
		SerifyApplet	sa = new SerifyApplet();
		
		String[] cmds = new String[] { "c:\\\\Program files\\NCBI\\blast-2.2.25+\\bin\\makeblastdb.exe", "-in", "c:\\\\Documents and settings\\sigmar\\Desktop\\erm.fna", "-out", "c:\\\\Documents and settings\\sigmar\\sim", "-title", "sim" };
		try {
			String result = sa.runProcessBuilder( Arrays.asList( cmds ) );
			System.err.println( result );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//sa.init( frame );
		
		frame.setVisible( true );
	}
}
