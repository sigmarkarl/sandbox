package org.simmi.serifier;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javafx.scene.control.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.simmi.javafasta.DataTable;
import org.simmi.javafasta.shared.*;
import org.simmi.javafasta.unsigned.JavaFasta;
import org.simmi.treedraw.shared.TreeUtil;
import org.simmi.treedraw.shared.TreeUtil.Node;
import org.simmi.treedraw.shared.TreeUtil.NodeSet;
import org.simmi.javafasta.unsigned.FlxReader;
import org.simmi.javafasta.unsigned.NativeRun;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class SerifyApplet {

	TableView<Sequences> table;
	Serifier serifier;
	//String 						globaluser = null;
	NativeRun nrun = new NativeRun();
	boolean 						noseq = false;
	
	Map<Path,Sequences> mseq = new HashMap<>();
	
	public String user;

	Thread watcherThread = null;
	Map<Path, Path> watchMap = new HashMap<>();
	WatchService watcher = null;
	Path filesInEdit = null;
	
	public SerifyApplet( FileSystem fs, boolean noseq ) {
		this();
		this.fs = fs;
		this.root = fs.getPath("/");
		this.noseq = noseq;
	}
	
	public SerifyApplet() {
		super();
		serifier = new Serifier();
		nrun = new NativeRun();
		fs = FileSystems.getDefault();
		root = fs.getPath( System.getProperty("user.home") );
	}
	
	public ObservableList<Sequences> initSequences() {		
		/*try {
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
		
		/*try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "initMachines", new Object[] {hostname, procs} );
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}*/
	}
	
	/*public TableModel createModel( final List<?> datalist ) {
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
							
							/*if( ret != null && !ret.getClass().isInstance(f.getType()) ) {
								System.err.println( ret.getClass() + "  " + f.getType() );
								ret = null;
							}*
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
	}*/
	
	public void init() {
		/*try {
			JSObject jso = JSObject.getWindow( this );
			final JSObject con = (JSObject)jso.getMember("console");
		} catch( NoSuchMethodError | Exception e ) {
			e.printStackTrace();
		}
		
		//fs = FileSystems.getDefault();
		//root = fs.getPath( System.getProperty("user.home") );
		//fs.
		
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
		
		init( null, null, System.getProperty("user.name") );
	}
	
	public void browse( final String url ) {
		try {
			Desktop.getDesktop().browse( URI.create(url) );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		ObservableList<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
		for( Sequences seqs : lseqs ) {
			rselset.add( seqs );
			if( seqs.getKey() != null ) keys.add( seqs.getKey() );
			
			try {
				Files.delete( seqs.getPath() );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		boolean unsucc = false;
		/*try {
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
		}*/
		
		removeAllSequences( rselset );
		//table.tableChanged( new TableModelEvent(table.getModel()) );
	}
	
	public void deleteSequence( String key ) {
		Sequences selseq = null;
		for( Sequences s : serifier.getSequencesList() ) {
			if( key.equals( s.getKey() ) ) selseq = s;
		}
		if( selseq != null ) {
			removeSequences( selseq );		
			//okoktable.tableChanged( new TableModelEvent(table.getModel()) );
		}
	}
	
	public static void tagsplit( Map<String,String> tagmap, Sequences seqs, File dir, SerifyApplet applet ) {
		try {
			//File inf = new File( new URI(seqs.getPath() ) );
			String name = seqs.getPath().getName(0).toString(); //inf.getName();
			int ind = name.lastIndexOf('.');
			
			String sff = name;
			String sf2 = "";
			if( ind != -1 ) {
				sff = name.substring(0, ind);
				sf2 = name.substring(ind+1,name.length());
			}
			
			Map<String,Path>		urlmap = new HashMap<String,Path>();
			Map<String,FileWriter> fwmap = new HashMap<String,FileWriter>();
			StringBuilder			include = new StringBuilder();
			int i = 0;
			String			current = null;
			//FileWriter 		fw = null;
			//File			of = null;
			//FileReader 		fr = new FileReader( inf );
			BufferedReader 	br = Files.newBufferedReader( seqs.getPath() );
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
									urlmap.put( key, of.toPath() );
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
						urlmap.put( key, of.toPath() );
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

	public static void blastpRun( NativeRun nrun, StringBuffer query, Path dbPath, Path resPath, String extrapar, JTable table, boolean homedir, final FileSystem fs, final String user, final Stage primaryStage ) throws IOException {
		String userhome = System.getProperty("user.home");
		Path selectedpath = null;
		if( homedir ) selectedpath = new File( userhome ).toPath();
		else {
			DirectoryChooser fc = new DirectoryChooser();
			File dir = fc.showDialog( null );
			if( dir != null && dir.exists() ) {
				Path path = dir.toPath();
				if (!Files.isDirectory(path)) selectedpath = path.getParent();
				else selectedpath = path;
			}
		}

	   final Dialog<Pair<String,String>> dialog = new Dialog();
	   dialog.setTitle("Select host and blast db path");
	   dialog.setHeaderText("Host and blast db selection");

	   dialog.initModality(Modality.APPLICATION_MODAL);
	   dialog.initOwner( primaryStage );

	   dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

	   GridPane grid = new GridPane();
	   grid.setHgap(10);
	   grid.setVgap(10);
	   grid.setPadding(new Insets(20, 150, 10, 10));

	   TextField hostfield = new TextField("localhost");
	   TextField pathfield = new TextField( dbPath.toString() );

	   grid.add(new Label("Host:"), 0, 0);
	   grid.add(hostfield, 1, 0);
	   grid.add(new Label("Dbpath:"), 0, 1);
	   grid.add(pathfield, 1, 1);

	   dialog.getDialogPane().setContent(grid);

	   dialog.setResultConverter(dialogButton -> {
		   if (dialogButton == ButtonType.OK) {
			   return new Pair<>(hostfield.getText(), pathfield.getText());
		   }
		   return null;
	   });

	   Optional<Pair<String, String>> result = dialog.showAndWait();

	   if( result.isPresent() ) {
		   String username = System.getProperty("user.name");
		   String hostname = result.get().getKey();
		   String pathname = result.get().getValue();

		   String cygpathstr = NativeRun.cygPath( userhome+"/genesetkey" );

		   List<Object>	lscmd = new ArrayList<>();
		   if( table != null ) {
			   int[] rr = table.getSelectedRows();
			   for( int r : rr ) {
				   Path	path = (Path)table.getValueAt( r, 3 );
				   //String blastFile = "rpsblast+";
				   Path res = selectedpath.resolve(path.getFileName().toString()+".blastout");
				   int procs = Runtime.getRuntime().availableProcessors();

				   List<String>	lcmd = new ArrayList<>();
				   String[] bcmds = { "blastp", "-db", pathname, "-num_threads", Integer.toString(procs) };
				   String[] exts = extrapar.trim().split("[\t ]+");

				   lcmd.addAll( Arrays.asList(bcmds) );
				   if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );

				   lscmd.add( new Path[] {path, res, selectedpath} );
				   lscmd.add( lcmd );
			   }
		   } else {
			   int procs = Runtime.getRuntime().availableProcessors();

			   List<String>	lcmd = new ArrayList<>();
			   String[] bcmds;
			   //String[] cmds;
			   if( hostfield.getText().equals("localhost") ) bcmds = new String[] { "blastp"/*blastpath.resolve("blastp").toString()*/, "-db", pathname, "-num_threads", Integer.toString(procs), "-num_alignments", "1", "-num_descriptions", "1" };
			   else {
				   if( user.equals("geneset") ) bcmds = new String[] { "ssh", "-i", cygpathstr, "geneset@"+hostname, "blastp"/*blastpath.resolve("blastp").toString()*/, "-db", pathname, "-num_threads", "32", "-num_alignments", "1", "-num_descriptions", "1" };
				   else bcmds = new String[] { "ssh", hostname, "blastp"/*blastpath.resolve("blastp").toString()*/, "-db", pathname, "-num_threads", "32", "-num_alignments", "1", "-num_descriptions", "1" };
			   }
			   String[] exts = extrapar.trim().split("[\t ]+");

			   lcmd.addAll( Arrays.asList(bcmds) );
			   if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );

			   lscmd.add( new Object[] {query.toString().getBytes(), resPath, selectedpath} );
			   lscmd.add( lcmd );
		   }

		   final Object[] cont = new Object[3];
		   Runnable run = () -> {
				if( cont[0] != null ) {

				}
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		   };
		   nrun.setRun( run );

		   try {
			   nrun.runProcessBuilder( "Performing blast", lscmd, cont, false, run, false );
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
	   }
	}
	
	public static void rpsBlastRun( NativeRun nrun, StringBuffer query, String dbPath, Path resPath, String extrapar, JTable table, boolean homedir, final FileSystem fs, final String user, final String hostname, boolean headless ) throws IOException {
		String userhome = System.getProperty("user.home");
		Path selectedpath = null;
		if( homedir ) selectedpath = new File( userhome ).toPath();
		else {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
				selectedpath = fc.getSelectedFile().toPath();
				if( !Files.isDirectory(selectedpath) ) selectedpath = selectedpath.getParent();
			}
		}
		
		String username = System.getProperty("user.name");
		String cygpathstr = NativeRun.cygPath( userhome+"/genesetkey" );
		
		List<Object>	lscmd = new ArrayList<>();
		if( table != null ) {
			int[] rr = table.getSelectedRows();
			for( int r : rr ) {
				Path	path = (Path)table.getValueAt( r, 3 );
				//String blastFile = "rpsblast+";
				Path res = selectedpath.resolve(path.getFileName().toString()+".blastout");
				int procs = Runtime.getRuntime().availableProcessors();
				
				List<String>	lcmd = new ArrayList<>();
				String[] bcmds = { "rpsblast", "-db", dbPath, "-num_threads", Integer.toString(procs) };
				String[] exts = extrapar.trim().split("[\t ]+");
				
				lcmd.addAll( Arrays.asList(bcmds) );
				if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
				
				lscmd.add( new Path[] {path, res, selectedpath} );
				lscmd.add( lcmd );
			}
		} else {
			int procs = Runtime.getRuntime().availableProcessors();
			
			String OS = System.getProperty("os.name").toLowerCase();
			List<String>	lcmd = new ArrayList<String>();
			String[] bcmds;
			//String[] cmds;
			if( hostname.equals("localhost") ) bcmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/ncbi/blast/bin/rpsblast" : "rpsblast"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "1", "-num_descriptions", "1", "-evalue", "0.01" };
			else {
				if( user.equals("geneset") ) bcmds = new String[] { "ssh", "-i", cygpathstr, "geneset@"+hostname, "rpsblast+"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "5", "-num_descriptions", "5", "-evalue", "0.01" };
				bcmds = new String[] { "ssh", hostname, "rpsblast+"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "5", "-num_descriptions", "5", "-evalue", "0.01" };
			}
			String[] exts = extrapar.trim().split("[\t ]+");
			
			lcmd.addAll( Arrays.asList(bcmds) );
			if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
			
			lscmd.add( new Object[] {query.toString().getBytes(), resPath, selectedpath} );
			lscmd.add( lcmd );
		}
		
		final Object[] cont = new Object[3];
		Runnable run = () -> {
            if( cont[0] != null ) {

            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
		nrun.setRun( run );
		nrun.runProcessBuilder( "Performing rpsblast", lscmd, cont, false, run, headless );
	}
	
	public static void deltaBlastRun( NativeRun nrun, StringBuffer query, String dbPath, Path resPath, String extrapar, JTable table, boolean homedir, final FileSystem fs, final String user, final String hostname, boolean headless, boolean docker ) throws IOException {
		String userhome = System.getProperty("user.home");
		Path selectedpath = null;
		if( homedir ) selectedpath = new File( userhome ).toPath();
		else {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
				selectedpath = fc.getSelectedFile().toPath();
				if( !Files.isDirectory(selectedpath) ) selectedpath = selectedpath.getParent();
			}
		}
		
		String username = System.getProperty("user.name");
		String cygpathstr = NativeRun.cygPath( userhome+"/genesetkey" );
		
		List<Object>	lscmd = new ArrayList<>();
		if( table != null ) {
			int[] rr = table.getSelectedRows();
			for( int r : rr ) {
				Path	path = (Path)table.getValueAt( r, 3 );
				//String blastFile = "rpsblast+";
				Path res = selectedpath.resolve(path.getFileName().toString()+".blastout");
				int procs = Runtime.getRuntime().availableProcessors();
				
				List<String>	lcmd = new ArrayList<>();
				String[] bcmds = { "rpsblast", "-db", dbPath, "-num_threads", Integer.toString(procs) };
				String[] exts = extrapar.trim().split("[\t ]+");
				
				lcmd.addAll( Arrays.asList(bcmds) );
				if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
				
				lscmd.add( new Path[] {path, res, selectedpath} );
				lscmd.add( lcmd );
			}
		} else {
			int procs = Runtime.getRuntime().availableProcessors();
			
			String OS = System.getProperty("os.name").toLowerCase();
			List<String>	lcmd = new ArrayList<String>();
			String[] bcmds;
			
			if( docker ) {
				bcmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/bin/docker" : "docker", "run", "-i", "geneset", "/ncbi-blast-2.5.0+/bin/deltablast", "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "1", "-num_descriptions", "1", "-evalue", "0.01" };
			} else {
				//String[] cmds;
				if( hostname.equals("localhost") ) bcmds = new String[] { OS.indexOf("mac") >= 0 ? "/ncbi-blast-2.5.0+/bin/blast" : "rpsblast"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "1", "-num_descriptions", "1", "-evalue", "0.01" };
				else {
					if( user.equals("geneset") ) bcmds = new String[] { "ssh", "-i", cygpathstr, "geneset@"+hostname, "rpsblast+"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "5", "-num_descriptions", "5", "-evalue", "0.01" };
					bcmds = new String[] { "ssh", hostname, "rpsblast+"/*blastpath.resolve("blastp").toString()*/, "-db", dbPath, "-num_threads", Integer.toString(procs), "-num_alignments", "5", "-num_descriptions", "5", "-evalue", "0.01" };
				}
			}
			String[] exts = extrapar.trim().split("[\t ]+");
			
			lcmd.addAll( Arrays.asList(bcmds) );
			if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
			
			lscmd.add( new Object[] {query.toString().getBytes(), resPath, selectedpath} );
			lscmd.add( lcmd );
		}
		
		final Object[] cont = new Object[3];
		Runnable run = () -> {
            if( cont[0] != null ) {

            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
		nrun.setRun( run );
		nrun.runProcessBuilder( "Performing rpsblast", lscmd, cont, false, run, headless );
	}
	
	public static void blastRun( NativeRun nrun, Path queryPath, Path dbPath, Path resPath, String dbType, String extrapar, TableView table, boolean homedir, final String user, final boolean headless ) throws IOException {
		String userhome = System.getProperty("user.home");
		Path selectedpath = null;
		if( homedir || headless ) selectedpath = new File( userhome ).toPath();
		/*System.out.println("run blast in applet");
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
		if( blastx.exists() ) {*/
		//String dbPathFixed = nrun.fixPath( dbPath );	
		else {
			DirectoryChooser fc = new DirectoryChooser();
			File f = fc.showDialog( null );
			if( f != null ) {
				selectedpath = f.toPath();
				if( !Files.isDirectory(selectedpath) ) selectedpath = selectedpath.getParent();
			}
		}
		
		String command = "";
		if( !headless ) {
			command = "/usr/local/bin/";
			JTextField host = new JTextField(command);
			JOptionPane.showMessageDialog(null, host);
			command = host.getText();
		}
		List<String> commandsplit = Arrays.asList(command.split("[ ]+"));
		List<String> cmds = new ArrayList<>( commandsplit );
		
		String username = System.getProperty("user.name");
		String cygpathstr = NativeRun.cygPath( userhome+"/genesetkey" );
		List<Object>	lscmd = new ArrayList<>();

			if( table != null ) {
				List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
				for( Sequences seqs : lseqs ) {
					Path	path = seqs.getPath();
					String 	type = seqs.getType();
					
					//String blasttype = dbType.equals("nucl") ? type.equals("prot") ? "blastx" : "blastn" : "blastp";
					String blastFile = dbType.equals("prot") ? type.equals("prot") ? "blastp" : "blastx" : "blastn";
					
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
			
					//Path p = Paths.get( URI.create(path) );
					
					//String queryPathFixed = nrun.fixPath( path.toAbsolutePath().toString() ).trim();
					
					//InputStream input = Files.newInputStream(path, StandardOpenOption.READ);
					
					//Path p = selectedfile.toPath();
					Path res = selectedpath.resolve(path.getFileName().toString()+".blastout");
					//OutputStream output = Files.newOutputStream(res, StandardOpenOption.CREATE);
					//final String outPathFixed = nrun.fixPath( new File( selectedfile, path.getFileName().toString()+".blastout" ).getAbsolutePath() ).trim();
					
					List<String>	lcmd = new ArrayList<>( commandsplit );
					int cmdsize = lcmd.size();
					lcmd.set(cmdsize-1, cmds.get(cmdsize-1)+blastFile);
					List<String> rest = Arrays.asList( new String[] {"-db", dbPath.getFileName().toString()} );
					lcmd.addAll( rest );
					String[] exts = extrapar.trim().split("[\t ]+");

					lcmd.addAll( Arrays.asList(exts) );
					//lcmd.addAll( Arrays.asList(nxst) );
					
					lscmd.add( new Path[] {path, res, selectedpath} );
					lscmd.add( lcmd );
				}
			} else {
				//Path res = Files.createTempFile("all", "blastout"); 
				Path res = resPath != null ? resPath : selectedpath.resolve( "tmp.blastout.gz" ); //path.getFileName().toString()+".blastout");
				
				//OutputStream output = Files.newOutputStream(res, StandardOpenOption.CREATE);
				//final String outPathFixed = nrun.fixPath( new File( selectedfile, path.getFileName().toString()+".blastout" ).getAbsolutePath() ).trim();
				
				List<String>	lcmd = new ArrayList<>();
				List<String> rest = Arrays.asList("-db", dbPath.getFileName().toString());
				List<String> 	bcmds = new ArrayList<>( commandsplit );
				int cmdsize = bcmds.size();
				bcmds.set(cmdsize-1, bcmds.get(cmdsize-1)+"blastp");
				bcmds.addAll( rest );

				/*if( hostname.equals("localhost") ) bcmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/bin/blastp" : "blastp"/*blastpath.resolve("blastp").toString()*, "-db", dbPath.getFileName().toString(), "-num_threads", Integer.toString(procs) };
				else {
					if( user.equals("geneset") ) bcmds  = new String[] { "ssh", "-i", cygpathstr, "geneset@"+hostname, "blastp"/*blastpath.resolve("blastp").toString()*, "-db", dbPath.getFileName().toString(), "-num_threads", "32" };
					else bcmds  = new String[] { "ssh", hostname, "blastp"/*blastpath.resolve("blastp").toString()*, "-db", dbPath.getFileName().toString(), "-num_threads", "32"}; //, "|", "gzip", "-c" };
				}*/
				String[] exts = extrapar.trim().split("[\t ]+");
				
				//String[] nxst = { "-out", outPathFixed };
				lcmd.addAll( bcmds );
				if( exts.length > 1 ) lcmd.addAll( Arrays.asList(exts) );
				//lcmd.addAll( Arrays.asList(nxst) );
				
				lscmd.add( new Path[] {queryPath, res, selectedpath} );
				lscmd.add( lcmd );
			}
			
			final String start = new Date( System.currentTimeMillis() ).toString();								
			final Object[] cont = new Object[3];
			/*Runnable run = new Runnable() {
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
			};*/
			/*for( String cmd : lcmd ) {
				System.err.println(cmd);
			}
			Thread.sleep(10000);*/
			//nrun.setRun( run );

		boolean spark = false;
		if(spark) {
			//SparkSession spark = SparkSession.builder().getOrCreate();
			//spark.createDataset(seqs);
			//Dataset<Annotation> ds = ;
		} else {
			nrun.runProcessBuilder("Performing blast", lscmd, cont, false, nrun.run, headless);
		}
		//}
		//} else System.err.println( "no blast installed" );
	}
	
	public void runBlastInApplet( final String extrapar, final Path dbPath, final String dbType ) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                blastRun( nrun, dbPath, dbPath, null, dbType, extrapar, table, false, user, false );
            } catch( Exception e ) {
                e.printStackTrace();
            }

            return null;
        });
	}
	
	public void blastClusters( final BufferedReader is, final BufferedWriter os ) {
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
					try {
						List<Set<String>> total = new ArrayList<>();
						serifier.makeBlastCluster( is, os, 0, 0.5f, 0.5f, null, total, null );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
		
		Map<String,Map<String,Set<String>>>	specmap = new HashMap<>();
		
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
							contigmap = new HashMap<>();
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
		Path path = null;
		String type = null;
		String trimname = null;
		
		int nseq = 0;
		BufferedWriter bw = null;
		List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
		for( Sequences seqs : lseqs ) {
			InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ );
			
			if( seqs.getPath().endsWith(".gz") ) {
				is = new GZIPInputStream( is );
			}
			
			if( bw == null ) {
				String urlstr = path.toString();
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
				path = f.toPath();
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
		List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
		for ( Sequences seqs : lseqs ) {
			if( lseqs.size() == 1 ) jf.setCurrentPath( seqs.getPath() );
			
			//int nseq = 0;
			serifier.appendSequenceInJavaFasta( seqs, contset, lseqs.size() == 1 );
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
		Platform.runLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            final JavaFasta jf = new JavaFasta( null, serifier, null );
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
                    if( jf.isEdited() && JOptionPane.showConfirmDialog( null, "Do you wan't to save?" ) == JOptionPane.YES_OPTION ) {
                        Path cp = jf.getCurrentPath();
                        if( cp == null ) {
                            FileChooser jfc = new FileChooser();
                            File f = jfc.showOpenDialog(null);
                            if( f != null ) {
                                try {
                                    FileWriter fw = new FileWriter( f );
                                    serifier.writeFasta( serifier.lseq, fw, jf.getSelectedRect() );
                                    fw.close();

                                    SerifyApplet.this.addSequences( f.getName(), f.toPath(), null );
                                } catch (IOException | URISyntaxException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            //Sequence.write
                            try {
                                BufferedWriter bw = Files.newBufferedWriter(cp, StandardOpenOption.TRUNCATE_EXISTING);
                                serifier.writeFasta( serifier.lseq, bw, null );
                                bw.close();

                                Sequences seqs = mseq.get(cp);
                                if( seqs != null ) {
                                    seqs.nseq = serifier.lseq.size();
                                    //okoktable.tableChanged( new TableModelEvent(table.getModel()) );
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void windowActivated(WindowEvent e) {}
            });
            frame.setVisible(true);
        });
	}
	
	FileSystem 	fs;
	Path		root;
	/*
	ACDK	Clostridium_sp-7-2-43FAA
	AWST	Clostridium_sp-KLE-1755
	AVKD	Clostridium_difficile-DA00256
	ACIO	Clostridium_hathewayi-DSM-13479
	
	-evalue 0.00001 -num_alignments 1 -num_descriptions 1
	*/
	
	public void doProdigal( Path dir, Container c, final Path fs, List<Path> urls ) {
		//JFileChooser fc = new JFileChooser();
		//fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		//if( fc.showSaveDialog( c ) == JFileChooser.APPROVE_OPTION ) {
		//	Path selectedfile = fc.getSelectedFile().toPath();
		//	if( !Files.isDirectory(selectedfile) ) selectedfile = selectedfile.getParent();
		SwingUtilities.invokeLater(() -> {
            JTextField host = new JTextField("prodigal");
            JOptionPane.showMessageDialog(null, host);

            for( Path path : urls ) {
                //URL url = path.toUri().toURL();

				/*String file = path.getFileName().toString();
				String[] split = file.split("/");
				String fname = split[ split.length-1 ];*/

                String fname = path.getFileName().toString();
                String[] split = fname.split("\\.");
                final String title = split[0];

				/*Path infile = dir.resolve( fname ); //new File( dir, fname );
				if( Files.exists(infile) ) {
					infile = dir.resolve( "tmp_"+fname );
				}*/

                //FileOutputStream fos = new FileOutputStream( infile );



                //Files.copy( path, infile, StandardCopyOption.REPLACE_EXISTING );



        /*InputStream is = url.openStream();
        byte[] bb = new byte[100000];
        int r = is.read(bb);
        while( r > 0 ) {
            fos.write(bb, 0, r);
            r = is.read(bb);
        }
        is.close();
        fos.close();*/

                String userhome = System.getProperty("user.home");
                String username = System.getProperty("user.name");
                String hostname = host.getText();
                Path selectedfile = new File( userhome ).toPath();
                String tmpout = title+".prodigal.fsa";
                final Path pathD = selectedfile.resolve( title+".prodigal.fna" );
                final Path pathA = selectedfile.resolve( title+".prodigal.fsa" );
                final String outPathD = NativeRun.fixPath( pathD.toAbsolutePath().toString() );
                final String outPathA = NativeRun.fixPath( pathA.toAbsolutePath().toString() );
                final String cygPathA = NativeRun.cygPath( pathA.toAbsolutePath().toString() );
                //NativeRun.fixPath( infile.toAbsolutePath().toString() )

                String OS = System.getProperty("os.name").toLowerCase();

                String[] cmdsplit = host.getText().split("[ ]+");
				Stream.Builder<String> builder = Stream.builder();
				for( String cmd : cmdsplit ) builder.add(cmd);
				builder.add("-a");
				builder.add(outPathA);
                String[] cmds = builder.build().toArray( size -> new String[size] );
                /*if( host.getText().equals("localhost") ) cmds = new String[] { (OS.indexOf("mac") >= 0) ? "/usr/local/bin/prodigal" : "prodigal", "-a", outPathA }; //"-d", outPathD };
                else {
                    if( user.equals("geneset") ) cmds = new String[] { "ssh", "-i", NativeRun.cygPath(userhome+"/genesetkey"), "geneset@"+hostname, "prodigal", "-a", tmpout };
                    else cmds = new String[] { "ssh", hostname, "prodigal", "-a", tmpout };
                }*/

                List<Object>	lscmd = new ArrayList<>();
                //String[] cmds = new String[] { "makeblastdb", "-dbtype", dbType, "-title", dbPath.getFileName().toString(), "-out", dbPath.getFileName().toString() };
                lscmd.add( new Path[] { path, null, dir } );
                lscmd.add( Arrays.asList( cmds ) );

                Path resp = fs.resolve( tmpout );

                final Object[] cont = new Object[3];
                Runnable run = () -> {
                    if( cont[0] != null ) {
                        System.err.println( cont[0] );

                        try {
                            /*if( !host.getText().equals("localhost") ) {
                                ProcessBuilder pb = new ProcessBuilder("scp", "-q", username+"@"+hostname+":~/"+tmpout, cygPathA);
                                Process pc = pb.start();
                                InputStream is = pc.getInputStream();
                                while( is.read() != -1 );
                                InputStream es = pc.getErrorStream();
                                while( es.read() != -1 );
                                pc.waitFor();
                                System.err.println("done " + outPathA + "  " + cygPathA);
                            }*/
                            Files.copy(pathA, resp);
                            addSequences( title+".aa", resp, null );
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }

                /*try {
                    addSequences(title+".nn", new File( outPathD ).toURI().toURL().toString() );
                    addSequences(title+".aa", new File( outPathA ).toURI().toURL().toString() );
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                    }
                };
                nrun.setRun( run );
                try {
                    nrun.runProcessBuilder( "Running prodigal", lscmd, cont, false, run, false );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //JSObject js = JSObject.getWindow( SerifyApplet.this );
                //js = (JSObject)js.getMember("document");
                //js.call( "addDb", new Object[] {getUser(), title, "nucl", outPath, result} );
            }
        });
		//infile.delete();
	}
	
	public void doRnammer( Path dir, Container c, final Path fs, List<Path> urls ) {
		SwingUtilities.invokeLater(() -> {
            JTextField host = new JTextField("localhost");
            JOptionPane.showMessageDialog(null, host);
            String userhome = System.getProperty("user.home");
            String username = System.getProperty("user.name");
            String hostname = host.getText();

            String cygcheck = NativeRun.cygPath(userhome);
            String cygpathstr = cygcheck + "/genesetkey";

            String[] sus = new String[]{"SSU", "LSU", "TSU"};

            for (String su : sus) {
                for (Path path : urls) {
                    String file = path.getFileName().toString();
                    String[] split = file.split("/");
                    String fname = split[split.length - 1];
                    split = fname.split("\\.");
                    final String title = split[0];
            /*Path infile = dir.resolve( fname ); //new File( dir, fname );
            if( Files.exists(infile) ) {
                infile = dir.resolve( "tmp_"+fname );
            }

            Files.copy( path, infile, StandardCopyOption.REPLACE_EXISTING );*/
                    String tmpout = title + "." + su.toLowerCase();
                    final Path pathD = fs.resolve(tmpout);
                    //final Path pathA = selectedfile.resolve( title+".prodigal.fsa" );

                    //final String outPathD = NativeRun.fixPath( pathD.toAbsolutePath().toString() );
                    //final String cygPathD = NativeRun.cygPath( pathD.toAbsolutePath().toString() );

                    //final String outPathA = NativeRun.fixPath( pathA.toAbsolutePath().toString() );
                    //NativeRun.fixPath( infile.toAbsolutePath().toString() )

                    String OS = System.getProperty("os.name").toLowerCase();

                    String[] cmds;
                    if (host.getText().equals("localhost"))
                        cmds = new String[]{OS.indexOf("mac") >= 0 ? "/usr/local/bin/rnammer" : "rnammer", "-s", "BAC", "-m", su, "-f", tmpout}; //"-d", outPathD };
                    else {
                        if (user.equals("geneset"))
                            cmds = new String[]{"ssh", "-i", cygpathstr, "genset@" + hostname, "rnammer", "-s", "BAC", "-m", su, "-f", tmpout};
                        else cmds = new String[]{"ssh", hostname, "rnammer", "-s", "BAC", "-m", su, "-f", tmpout};
                    }

                    List<Object> lscmd = new ArrayList<Object>();
                    //String[] cmds = new String[] { "makeblastdb", "-dbtype", dbType, "-title", dbPath.getFileName().toString(), "-out", dbPath.getFileName().toString() };
                    lscmd.add(new Path[]{path, null, dir});
                    lscmd.add(Arrays.asList(cmds));

                    final Object[] cont = new Object[3];
                    Runnable run = () -> {
					if (cont[0] != null) {
					System.err.println(cont[0]);

					try {
					if (host.getText().equals("localhost")) {
					Path uh = Paths.get(userhome);
					Files.copy(uh.resolve(tmpout), pathD, StandardCopyOption.REPLACE_EXISTING);
					} else {
					Path uh = Paths.get(cygcheck);
					System.err.println("about to scp " + tmpout);
					ProcessBuilder pb = new ProcessBuilder("scp", "-q", username + "@" + hostname + ":~/" + tmpout, tmpout);
					pb.directory(uh.toFile());
					Process pc = pb.start();
					InputStream is = pc.getInputStream();
					ByteArrayOutputStream bab = new ByteArrayOutputStream();
					int r = is.read();
					while (r != -1) {
					bab.write(r);
					r = is.read();
					}
					bab.close();
					System.err.println(bab.toString());

					bab = new ByteArrayOutputStream();
					InputStream es = pc.getErrorStream();
					r = es.read();
					while (r != -1) {
					bab.write(r);
					r = es.read();
					}
					bab.close();
					System.err.println(bab.toString());

					pc.waitFor();

					//System.err.println("done " + cygPathD);
					Path from = uh.resolve(tmpout);
					System.err.println("exists " + Files.exists(from));
					Files.copy(from, pathD, StandardCopyOption.REPLACE_EXISTING);
					//System.err.println("done " + outPathD);
					}
					addSequences(title + "." + su.toLowerCase(), pathD, null);
					} catch (IOException | URISyntaxException | InterruptedException e) {
					e.printStackTrace();
					}
					}
					};
                    nrun.setRun(run);
                    try {
                        nrun.runProcessBuilder("Running rnammer", lscmd, cont, false, run, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
	}

	public void doBarrnap( Path dir, Container c, final Path fs, List<Path> urls ) {
		SwingUtilities.invokeLater(() -> {
			JTextField host = new JTextField("localhost");
			JOptionPane.showMessageDialog(null, host);
			String userhome = System.getProperty("user.home");
			String username = System.getProperty("user.name");
			String hostname = host.getText();

			String cygcheck = NativeRun.cygPath(userhome);
			String cygpathstr = cygcheck+"/genesetkey";

			for( Path path : urls ) {
				String file = path.getFileName().toString();
				String[] split = file.split("/");
				String fname = split[ split.length-1 ];
				split = fname.split("\\.");
				final String title = split[0];
/*Path infile = dir.resolve( fname ); //new File( dir, fname );
if( Files.exists(infile) ) {
	infile = dir.resolve( "tmp_"+fname );
}

Files.copy( path, infile, StandardCopyOption.REPLACE_EXISTING );*/
				String tmpout = title;
				final Path pathD = fs.resolve( tmpout );
				//final Path pathA = selectedfile.resolve( title+".prodigal.fsa" );

				//final String outPathD = NativeRun.fixPath( pathD.toAbsolutePath().toString() );
				//final String cygPathD = NativeRun.cygPath( pathD.toAbsolutePath().toString() );

				//final String outPathA = NativeRun.fixPath( pathA.toAbsolutePath().toString() );
				//NativeRun.fixPath( infile.toAbsolutePath().toString() )

				String OS = System.getProperty("os.name").toLowerCase();

				String[] cmds;
				if( host.getText().equals("localhost") ) cmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/bin/barrnap" : "barrnap", "--kingdom", "bac", "-f", tmpout }; //"-d", outPathD };
				else {
					if( user.equals("geneset") ) cmds = new String[] { "ssh", "-i", cygpathstr, "genset@"+hostname, "barrnap", "--kingdom", "bac", "-f", tmpout };
					else cmds = new String[] { "ssh", hostname, "barrnap", "--kingdom", "bac", "-f", tmpout };
				}

				List<Object>	lscmd = new ArrayList();
				//String[] cmds = new String[] { "makeblastdb", "-dbtype", dbType, "-title", dbPath.getFileName().toString(), "-out", dbPath.getFileName().toString() };
				lscmd.add( new Path[] { path, null, dir } );
				lscmd.add( Arrays.asList( cmds ) );

				final Object[] cont = new Object[3];
				Runnable run = new Runnable() {
					public void run() {
						if( cont[0] != null ) {
							System.err.println( cont[0] );

							try {
								if( host.getText().equals("localhost") ) {
									Path uh = Paths.get(userhome);
									Files.copy(uh.resolve( tmpout ), pathD, StandardCopyOption.REPLACE_EXISTING);
								} else {
									Path uh = Paths.get(cygcheck);
									System.err.println( "about to scp " + tmpout );
									ProcessBuilder pb = new ProcessBuilder("scp", "-q", username+"@"+hostname+":~/"+tmpout, tmpout);
									pb.directory( uh.toFile() );
									Process pc = pb.start();
									InputStream is = pc.getInputStream();
									ByteArrayOutputStream bab = new ByteArrayOutputStream();
									int r = is.read();
									while( r != -1 ) {
										bab.write( r );
										r = is.read();
									}
									bab.close();
									System.err.println( bab.toString() );

									bab = new ByteArrayOutputStream();
									InputStream es = pc.getErrorStream();
									r = es.read();
									while( r != -1 ) {
										bab.write( r );
										r = es.read();
									}
									bab.close();
									System.err.println( bab.toString() );

									pc.waitFor();

									//System.err.println("done " + cygPathD);
									Path from = uh.resolve( tmpout );
									System.err.println("exists " + Files.exists(from));
									Files.copy(from, pathD, StandardCopyOption.REPLACE_EXISTING);
									//System.err.println("done " + outPathD);
								}
								addSequences( title, pathD, null );
							} catch (IOException | URISyntaxException | InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				nrun.setRun( run );
				try {
					nrun.runProcessBuilder( "Running barrnap", lscmd, cont, false, run, false );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void startWatcherThread() {
		watcherThread = new Thread(() -> {
			while (true) {
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					watcherThread = null;
					return;
				}

				try {
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == ENTRY_MODIFY) {
							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							Path savedpath = ev.context();
							Path pathToSave = filesInEdit.resolve(savedpath);

							Optional<Path> remotepath = watchMap.entrySet().stream().filter(e -> e.getValue().equals(pathToSave)).map(Map.Entry::getKey).findFirst();
							if (remotepath.isPresent()) {
								Files.copy(pathToSave, remotepath.get(), StandardCopyOption.REPLACE_EXISTING);
							}
						}
					}
				} catch (IOException e) {
					watcherThread = null;
					key.reset();
					throw new RuntimeException("Invalid path", e);
				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		});
		watcherThread.setDaemon(true);
		watcherThread.start();
	}
	
	public void init( final Container c, final VBox vbox, String tuser ) {
		nrun.cnt = c;
		user = tuser;
		
		/*try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			user = (String)js.call("getUser", new Object[] {});
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
		}*/
		
		//final String[] 	columns = new String[] {"user", "name", "path", "type", "#seq"};
		//final Class[]	types = new Class[] {String.class, String.class, String.class, String.class, Integer.class};
		
		//Color bgcolor = Color.white;
		//SerifyApplet.this.getContentPane().setBackground(bgcolor);
		//SerifyApplet.this.setBackground(bgcolor);
		//SerifyApplet.this.getRootPane().setBackground(bgcolor);
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		TableColumn<Sequences, String> usercol = new TableColumn<>("User");
		usercol.setCellValueFactory( new PropertyValueFactory<>("user"));
		table.getColumns().add( usercol );
		TableColumn<Sequences, String> namedesccol = new TableColumn<>("Name");
		namedesccol.setCellValueFactory( new PropertyValueFactory<>("name"));
		table.getColumns().add( namedesccol );
		TableColumn<Sequences, String> typecol = new TableColumn<>("Type");
		typecol.setCellValueFactory( new PropertyValueFactory<>("type"));
		table.getColumns().add( typecol );
		TableColumn<Sequences, Path> pathcol = new TableColumn<>("Path");
		pathcol.setCellValueFactory( new PropertyValueFactory<>("path"));
		table.getColumns().add( pathcol );
		TableColumn<Sequences, Integer> nseqcol = new TableColumn<>("NSeq");
		nseqcol.setCellValueFactory( new PropertyValueFactory<>("nSeq"));
		table.getColumns().add( nseqcol );
		TableColumn<Sequences, Integer> metadatacol = new TableColumn<>("Metadata");
		metadatacol.setCellValueFactory( new PropertyValueFactory<>("metadata"));
		table.getColumns().add( metadatacol );
		
		ObservableList<Sequences> sequences = initSequences();
		initMachines();
		serifier.setSequencesList( sequences );
		table.setItems( sequences );



		ContextMenu ctxm = new ContextMenu();

		MenuItem emd = new MenuItem("Edit metadata");
		emd.setOnAction( e -> {
			Path ec = table.getSelectionModel().getSelectedItem().getPath();
			Path path = ec.resolveSibling(ec.getFileName().toString()+".yaml");

			var defaultMeta = """
					topology: circular
					comment: 'Matis comment'
					consortium: 'consortium'
					sra:
					    - accession: 'MAT2789'
					tp_assembly: true
					organism:
					    genus_species: 'Thermus thermophilus'
					    strain: 'replaceme'
					contact_info:
					    last_name: 'Stefansson'
					    first_name: 'Sigmar'
					    email: 'sigmarkarl@gmail.com'
					    organization: 'Matis'
					    department: 'Department of Microbiology'
					    phone: '354-857-5049'
					    street: 'Vínlandsleið 8'
					    city: 'Reykjavík'
					    postal_code: '110'
					    country: 'Iceland'
					   \s
					authors:
					    -     author:
					            first_name: 'Sigmar'
					            last_name: 'Stefánsson'
					            middle_initial: 'K'
					    -     author:
					            first_name: 'Guðmundur'
					            last_name: 'Hreggviðsson'
					bioproject: 'PRJNA9999999'
					biosample: 'SAMN99999999'     \s
					# -- Locus tag prefix - optional. Limited to 9 letters. Unless the locus tag prefix was officially assigned by NCBI, ENA, or DDBJ, it will be replaced upon submission of the annotation to NCBI and is therefore temporary and not to be used in publications. If not provided, pgaptmp will be used.
					#locus_tag_prefix: 'tmp'
					#publications:
					#    - publication:
					#        pmid: 16397293
					#        title: 'Discrete CHARMm of Klebsiella foobarensis. Journal of Improbable Results, vol. 34, issue 13, pages: 10001-100005, 2018'
					#        status: published  # this is enum: controlled vocabulary
					#        authors:
					#            - author:
					#                first_name: 'Sigmar'
					#                last_name: ''
					#                middle_initial: 'T'
					#            - author:
					#                  first_name: 'Linda'
					#                  last_name: 'Hamilton'
				     
					""";

			try {
				if(!Files.exists(path)) {
					Files.writeString(path, defaultMeta);
				}

				Optional<Path> openpath;
				if (watchMap.containsKey(path)) {
					openpath = Optional.of(watchMap.get(path));
				} else {
					if (watcher == null) {
						filesInEdit = Files.createTempDirectory("tmp");
						watcher = FileSystems.getDefault().newWatchService();
						WatchKey k = filesInEdit.register(watcher, ENTRY_MODIFY);
					}

					String filename = path.getFileName().toString();
					Path tmpfile = Files.createTempFile(filesInEdit, "tmp", filename);

					watchMap.put(path, tmpfile);
					Files.copy(path, tmpfile, StandardCopyOption.REPLACE_EXISTING);
					Desktop.getDesktop().open(tmpfile.toFile());
					openpath = Optional.of(tmpfile);

					if (watcherThread == null) startWatcherThread();
				}
				Desktop.getDesktop().open(openpath.get().toFile());
			} catch (IOException ie) {
				throw new RuntimeException("Invalid path: " + path, ie);
			}
			/*try {
				Path m = Files.createTempFile("stuff",".yaml");
				if(!Files.exists(sib)) {
					Files.writeString(m, defaultMeta);
				} else {
					Files.copy(sib, m);
				}
				Desktop.getDesktop().open(m.toFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}*/
		});
		ctxm.getItems().add(emd);


		table.setContextMenu(ctxm);


		//TableModel model = createModel( sequences, Sequences.class );
		//table.setModel( model );
		
		/*Field[] odecl = Sequences.class.getDeclaredFields();
		Set<TableColumn>			remcol = new HashSet<TableColumn>();
		Enumeration<TableColumn>	taben = table.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = odecl[tc.getModelIndex()].getName();
			if( name.startsWith("_") ) {
				remcol.add( tc );
			}
		}
		for( TableColumn tc : remcol ) {
			table.removeColumn( tc );
		}*/
		
		table.setOnMouseClicked(me -> {
            if( me.getClickCount() == 2 ) {
                load();
            }
        });
			/*public void mousePressed( MouseEvent me ) {
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
					}*
					
					//browse( path );
				}
			}

			public void changed(ObservableValue<? extends Sequences> observable, Sequences oldValue, S newValue) {
				// TODO Auto-generated method stub
				
			}*/
		//});
		
		table.setOnKeyPressed(e -> {
            KeyCode keycode = e.getCode();
            if( keycode == KeyCode.DELETE ) {
                deleteSeqs();
            } else if( keycode == KeyCode.ENTER ) {
                String path = table.getSelectionModel().getSelectedItem().getPath().toString();
                /*try {
                    SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }*/

                browse( path );
            }
        });
		
		MenuBar mb = new MenuBar();
		Menu popup = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu programs = new Menu("Programs");
		mb.getMenus().add( popup );
		mb.getMenus().add(edit);
		mb.getMenus().add(programs);
		/*if( c instanceof JFrame ) {
			JFrame fr = (JFrame)c;
			JMenuBar mb = new JMenuBar();
			//c
			//popup.get
			//menu.add(popup);
			mb.add( popup );
			fr.setJMenuBar(mb);
		} else if( c instanceof JApplet ) {
			JApplet ap = (JApplet)c;
			JMenuBar mb = new JMenuBar();
			mb.add( popup );
			ap.setJMenuBar( mb );
		}*/
		
		MenuItem jgifetch = new MenuItem("JGI Fetch");
		popup.getItems().add( jgifetch );
		jgifetch.setOnAction(event -> {
            try {
                CookieManager cm = new CookieManager( null, CookiePolicy.ACCEPT_ALL );
                CookieHandler.setDefault( cm );

                URL loginurl = new URL("https://signon.jgi.doe.gov/signon/create");

                HttpsURLConnection hu = (HttpsURLConnection)loginurl.openConnection();
                hu.setRequestMethod("POST");
                hu.setRequestProperty("login", "sigmarkarl@gmail.com");
                hu.setRequestProperty("password", "drsmorc.311");
                hu.setDoOutput( true );
                hu.setDoInput( true );

                //hu.connect();

                OutputStream os = hu.getOutputStream();
                os.write( "login=sigmarkarl@gmail.com&password=drsmorc.311".getBytes() );
                os.close();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = hu.getInputStream();
                int r = is.read();
                while( r != -1 ) {
                    baos.write( r );
                    r = is.read();
                }
                is.close();

                baos.close();
                System.err.println( baos.toString() );

                Map<String, List<String>> headerFields = hu.getHeaderFields();
                String cookiesHeader = hu.getHeaderField("Set-Cookie");

                /*if(cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                      HttpCookie.parse(cookie).get(0);
                      cm.getCookieStore().add(null,cc);
                    }
                }*/

                List<HttpCookie> lhc = cm.getCookieStore().getCookies();
                for( HttpCookie hc : lhc ) {
                    System.err.println( hc );
                }

                URL fetchurl = new URL("http://genome.jgi.doe.gov/ext-api/downloads/get-directory?organism=ThescoKI2");
                HttpURLConnection hu2 = (HttpURLConnection)fetchurl.openConnection();

                /*String cstr = "";
                for( String hc : cookiesHeader ) { //HttpCookie hc : cm.getCookieStore().getCookies() ) {
                    if( cstr == null ) cstr = hc;
                    else cstr += ","+hc;
                }

                hu2.setRequestProperty("Cookie", cstr);*/
                is = hu2.getInputStream();
                baos = new ByteArrayOutputStream();
                r = is.read();
                while( r != -1 ) {
                    baos.write( r );
                    r = is.read();
                }
                is.close();
                baos.close();

                String xml = baos.toString();
                int k = xml.lastIndexOf("url=\"");
                int u = xml.indexOf("\"", k+5);

                String subs = xml.substring(k+5, u);

                URL downloadurl = new URL("http://genome.jgi.doe.gov" + subs);
                URLConnection uc = downloadurl.openConnection();
                //Path p = Paths.get( downloadurl.toURI() );

                Path target = Paths.get("/Users/sigmar/stuff.tar.gz");
                Files.copy(uc.getInputStream(), target);
                //System.err.println( baos.toString() );
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
		MenuItem ncbifetch = new MenuItem("NCBI Fetch");
		popup.getItems().add( ncbifetch );
		ncbifetch.setOnAction(e -> {
            final Path cd;
            if( fs == null ) {
                DirectoryChooser	filechooser = new DirectoryChooser();
                File selectedFile = filechooser.showDialog(null);
                if( selectedFile != null ) {
                    cd = selectedFile.toPath();
                } else cd = null;
            } else {
                cd = fs.getPath("/");
            }

            String userhome = System.getProperty("user.home");
            final Path uhome = Paths.get(userhome);

                /*final JCheckBox	whole = new JCheckBox("whole");
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
                final Path uripath = cd.toPath();*/
                //final String replace = subsplit.length > 1 && subsplit[1].length() > 0 ? subsplit[1] : null;

			SwingUtilities.invokeLater(() -> {
                final JCheckBox	whole = new JCheckBox("whole");
                whole.setSelected( true );
                final JCheckBox	plasmid = new JCheckBox("plasmid");
                plasmid.setSelected( true );
                final JCheckBox	phage = new JCheckBox("phage");
                phage.setSelected( true );

                final JTextArea ta = new JTextArea();
                JScrollPane	sp = new JScrollPane( ta );
                Dimension dim = new Dimension(400,300);
                sp.setPreferredSize( dim );
                sp.setSize( dim );
                JOptionPane.showMessageDialog(c, new Object[] {whole, plasmid, phage, "Filter term",sp});
                final Map<String,String> searchmap = new HashMap<>();
                String searchstr = ta.getText();
                String[] split = searchstr.split("\n");
                for( String strsearch : split ) {
                    String[] subsplit = strsearch.split("\t");
                    if( subsplit.length > 1 ) searchmap.put( subsplit[0], subsplit[1] );
                    else searchmap.put( strsearch, null );
                }

                //final String basesave =  cd.toAbsolutePath().toString();
                //final Path uripath = cd;
                //final String replace = subsplit.length > 1 && subsplit[1].length() > 0 ? subsplit[1] : null;

                final JDialog		dialog = new JDialog();
                final JProgressBar	pbar = new JProgressBar();

                Runnable run = new Runnable() {
                    boolean interrupted = false;

                    public void run() {
                        dialog.addWindowListener( new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e12) {}

                            @Override
                            public void windowIconified(WindowEvent e12) {}

                            @Override
                            public void windowDeiconified(WindowEvent e12) {}

                            @Override
                            public void windowDeactivated(WindowEvent e12) {}

                            @Override
                            public void windowClosing(WindowEvent e12) {}

                            @Override
                            public void windowClosed(WindowEvent e12) {
                                //interrupted = true;
                            }

                            @Override
                            public void windowActivated(WindowEvent e12) {}
                        });
                        dialog.setVisible( true );

                        //String ftpsite = "ftp.rhnet.is";
                        //FTPClientConfig ftpcc = new FTPClientConfig();
                        try {
							FtpFetch.ftpFetch(SerifyApplet.this, searchmap, cd, whole.isSelected(), phage.isSelected(), plasmid.isSelected(), interrupted);
							//HttpFetch.httpFetch(SerifyApplet.this, searchmap, cd, whole.isSelected(), phage.isSelected(), plasmid.isSelected(), interrupted);
                        } catch (IOException e12) {
                            e12.printStackTrace();
                        }

						pbar.setIndeterminate( false );
                        pbar.setEnabled( false );
                    }
                };
                nrun.runProcess("NCBI Fetch", run, dialog, pbar);
            });
        });
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem importfasta = new MenuItem("Import fasta");
		popup.getItems().add( importfasta );
		importfasta.setOnAction(e -> {
            FileChooser	filechooser = new FileChooser();
            List<File> lfile = filechooser.showOpenMultipleDialog(null);
            //filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if( lfile != null  ) {
                //File cd = filechooser.getCurrentDirectory();
                //String path = JOptionPane.showInputDialog("Select path", cd.toURI().toString());

                try {
                    String comp = null;
                    FlxReader flx = new FlxReader();
                    for( File f : lfile ) {
                        if( f.isDirectory() ) {
                            List<Sequence> bb = null;
                            if( comp == null ) {
                                Sequences seqs = table.getSelectionModel().getSelectedItem();
                                comp = seqs.getName();
                                Path filepath = seqs.getPath();
                                bb = Sequence.readFasta(filepath, null);
                            }

                            Path dest = root.resolve(f.getName()+".fna");
                            //Files.copy(f.t, dest, StandardCopyOption.REPLACE_EXISTING);
                            //addSequences( f.getName(), dest, null );
                            //File nf = new File(f, f.getName()+".fna");
                            Writer fw = Files.newBufferedWriter(dest, StandardOpenOption.CREATE);
                            flx.start( f.getParentFile().getAbsolutePath()+"/", f.getName(), false, fw, comp, bb);
                            fw.close();

                            Platform.runLater(() -> {
                                JavaFasta	jf = new JavaFasta(flx.serifier);
                                JFrame		frame = new JFrame();
                                frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
                                frame.setSize(800, 600);
                                jf.initGui(frame);
                                jf.updateView();
                                frame.setVisible( true );

                                System.err.println("about to add seqs " + f.getName());
                            });

                            addSequences( f.getName(), dest, null );
                        } else {
                        	String filename = f.getName();
                        	Path dest;
                        	if( filename.toLowerCase().endsWith(".gz") ) {
                        		dest = root.resolve(filename.substring(0,filename.length()-3));
                        		Files.copy(new GZIPInputStream(Files.newInputStream(f.toPath())), dest, StandardCopyOption.REPLACE_EXISTING);
							} else {
								dest = root.resolve(f.getName());
								Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
							}
							addSequences( f.getName(), dest, null );
                        }
                    }
                } catch (URISyntaxException | IOException | InterruptedException e1) {
                    e1.printStackTrace();
                }
			}
        });
		MenuItem concatenate = new MenuItem("Concatenate");
		popup.getItems().add( concatenate );
		concatenate.setOnAction(e -> {
            FileChooser	filechooser = new FileChooser();
            File f = filechooser.showOpenDialog(null);
            if( f != null ) {
                try {
                    List<BufferedReader>	lrd = new ArrayList<>();
                    List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
                    for( Sequences seqs : lseqs ) {
                        Path path = seqs.getPath();
                        //URL url = new URL( path );
                        lrd.add( Files.newBufferedReader( path, Charset.defaultCharset() ) );
                    }
                    final Map<String,StringBuilder>	seqmap = serifier.concat( lrd );

                    JFrame popup1 = new JFrame();
                    popup1.setSize(800, 600);
                    popup1.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

                    final List<String> keyList = new ArrayList<>( seqmap.keySet() );
                    final boolean[]	barr = new boolean[ keyList.size() ];
					Arrays.fill(barr, true);

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

                    popup1.add( scrollpane );
                    popup1.setVisible( true );

                    popup1.addWindowListener(new WindowListener() {

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

                                addSequences( f.getName(), f.toPath(), null );
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
        });
		MenuItem viewsequences = new MenuItem("View sequences");
		popup.getItems().add( viewsequences );
		viewsequences.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				load();
			}
		});
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem delete = new MenuItem("Delete");
		popup.getItems().add( delete );
		delete.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				deleteSeqs();
			}
		});
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem createdatabase = new MenuItem("Create database");
		createdatabase.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					String userhome = System.getProperty("user.home");	
					File dir = new File( userhome );
					
					nrun.checkInstall( dir );
						
					Sequences seqs = table.getSelectionModel().getSelectedItem();
					final String dbtype = seqs.getType();
					final String path = seqs.getPath().toString();
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
					int r = is.read(bb);
					while( r > 0 ) {
						fos.write(bb, 0, r);
						r = is.read(bb);
					}
					is.close();
					fos.close();
					
					DirectoryChooser fc = new DirectoryChooser();
					File selectedfile = fc.showDialog( null );
					if( selectedfile != null ) {
						if( !selectedfile.isDirectory() ) selectedfile = selectedfile.getParentFile();
						
						final String outPath = nrun.fixPath( new File( selectedfile, title ).getAbsolutePath() );
						final Object[] cont = new Object[3];
						Runnable run = new Runnable() {
							public void run() {
								if( cont[0] != null ) {
									infile.delete();
								
									/*if( cont[0] != null ) {
										try {
											JSObject js = JSObject.getWindow( SerifyApplet.this );
											//js = (JSObject)js.getMember("document");
										
											String machineinfo = getMachine();
											String[] split = machineinfo.split("\t");
											js.call( "addDb", new Object[] {getUser(), title, dbtype, outPath, split[0], cont[1]} );
										} catch( NoSuchMethodError | Exception e ) {
											
										}
									}*/
								}
							}
						};
						
						File makeblastdb = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\makeblastdb.exe" );
						if( !makeblastdb.exists() ) makeblastdb = new File( "/opt/ncbi-blast-2.2.28+/bin/makeblastdb" );
						if( makeblastdb.exists() ) {
							String[] cmds = new String[] { makeblastdb.getAbsolutePath(), "-in", nrun.fixPath( infile.getAbsolutePath() ), "-title", title, "-dbtype", dbtype, "-out", outPath };
							nrun.setRun( run );
							nrun.runProcessBuilder( "Creating database", Arrays.asList( cmds ), cont, false, run, false );
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
		MenuItem linkdatabase = new MenuItem("Link database");
		popup.getItems().add( linkdatabase );
		linkdatabase.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String userhome = System.getProperty("user.home");	
					
				FileChooser fc = new FileChooser();
				File selectedfile = fc.showOpenDialog(null);
				if( selectedfile != null ) {
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
					
					//okokJSObject js = JSObject.getWindow( SerifyApplet.this );
					//js = (JSObject)js.getMember("document");
					
					//String machineinfo = getMachine();
					//String[] split = machineinfo.split("\t");
					//js.call( "addDb", new Object[] {getUser(), title, dbtype, outPath, split[0], ""} );
				}
			}
		});
		MenuItem blast = new MenuItem("Blast");
		popup.getItems().add( blast );
		blast.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				boolean success = false;
				/*try {
					String userhome = System.getProperty("user.home");
					File dir = new File( userhome );
					
					//nrun.checkInstall( dir );
						
					//JSObject js = JSObject.getWindow( SerifyApplet.this );
					//getParameters( js );
					success = true;
					//infile.delete();
				} catch (NoSuchMethodError e0) {
					e0.printStackTrace();
				}*/
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
					
					Path filepath = seqs.getPath();
					runBlastInApplet( tf.getText(), filepath, seqs.getType() ); //db.getText().contains(".p") ? "prot" : "nucl" );
				}
			}
		});
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem genbankfromblast = new MenuItem("Genbank from blast");
		programs.getItems().add( genbankfromblast );
		genbankfromblast.setOnAction(arg0 -> {
            String addon = "nnnttaattaattaannn";
            List<Integer>	startlist = new ArrayList<>();
            List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
            FileChooser fc = new FileChooser();
            File dir = null;
            if( lseqs.size() > 1 ) {
                DirectoryChooser dc = new DirectoryChooser();
                dir = dc.showDialog(null);
                if( dir != null ) {

                }
            }
            for( Sequences s : table.getSelectionModel().getSelectedItems() ) {
                File blastFile = fc.showOpenDialog(null);
                if( blastFile != null ) {
                    File f = null;
                    if( dir != null ) {
                        f = new File( dir, s.getName()+".gb" );
                    } else {
                        f = fc.showSaveDialog(null);
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
        });
		MenuItem genbankfromnr = new MenuItem("Genbank from nr");
		programs.getItems().add( genbankfromnr );
		genbankfromnr.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				List<Integer>	startlist = new ArrayList<>();
				List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
				DirectoryChooser	dc = new DirectoryChooser();
				File dir = null;
				if( lseqs.size() > 1 ) {
					dir = dc.showDialog(null);
				}
				FileChooser fc = new FileChooser();
				for( Sequences s : lseqs ) {
					File blastFile = fc.showOpenDialog(null);
					if( blastFile != null ) {
						File f = null;
						if( dir != null ) {
							f = new File( dir, s.getName()+".gb" );
						} else {
							f = fc.showSaveDialog(null);
						}
						
						try {
							serifier.genbankFromNR( s, blastFile.toPath(), f.toPath(), false );
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		MenuItem downloadfiles = new MenuItem("Download files");
		programs.getItems().add( downloadfiles );
		downloadfiles.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DirectoryChooser dc = new DirectoryChooser();
				File f = dc.showDialog(null);
				if( f != null ) {
					if( !f.isDirectory() ) f = f.getParentFile();
					final File dir = f;
					for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
						String path = seqs.getPath().toString();
						try {
							URL url = new URL( path );
							String[] str = path.split("\\/");
							f = new File( dir, str[str.length-1] );
							FileOutputStream	fos = new FileOutputStream( f );
							InputStream is = url.openStream();
							byte[] bb = new byte[2048];
							int r = is.read(bb);
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
		MenuItem showfile = new MenuItem("Show file");
		programs.getItems().add( showfile );
		showfile.setOnAction(e -> {
            Sequences seqs = table.getSelectionModel().getSelectedItem();
            String path = seqs.getPath().toString();

            /*try {
                SerifyApplet.this.getAppletContext().showDocument( new URL(path) );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }*/

            browse( path );
        });
		MenuItem prodigal = new MenuItem("Prodigal");
		programs.getItems().add( prodigal );
		prodigal.setOnAction(e -> {
			List<Path> urls = new ArrayList<>();
			for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
				//int r = table.getSelectedRow();
				Path path = seqs.getPath();
				urls.add( path );
			}

			String userhome = System.getProperty("user.home");
			Path dir = Paths.get( userhome );
			//File f = nrun.checkProdigalInstall( dir, urls );
			//if( f != null && f.exists() ) {
			Path root = null;
			for( Path p: fs.getRootDirectories() ) {
				root = p;
				break;
			}
			doProdigal( dir, c, root, urls );
			//} else System.err.println( "no prodigal installed" );
        });
		MenuItem rnammer = new MenuItem("Rnammer");
		programs.getItems().add( rnammer );
		rnammer.setOnAction(e -> {
			List<Path> urls = new ArrayList<>();
			for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
				//int r = table.getSelectedRow();
				Path path = (Path)seqs.getPath();
				urls.add( path );
			}

			String userhome = System.getProperty("user.home");
			Path dir = Paths.get( userhome );
			//File f = nrun.checkProdigalInstall( dir, urls );
			//if( f != null && f.exists() ) {
			Path root = null;
			for( Path p: fs.getRootDirectories() ) {
				root = p;
				break;
			}
			doRnammer( dir, c, root, urls );
			//} else System.err.println( "no prodigal installed" );
        });
		MenuItem barrnap = new MenuItem("Barrnap");
		programs.getItems().add( barrnap );
		barrnap.setOnAction(e -> {
			List<Path> urls = new ArrayList<>();
			for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
				//int r = table.getSelectedRow();
				Path path = seqs.getPath();
				urls.add( path );
			}

			String userhome = System.getProperty("user.home");
			Path dir = Paths.get( userhome );
			//File f = nrun.checkProdigalInstall( dir, urls );
			//if( f != null && f.exists() ) {
			Path root = null;
			for( Path p: fs.getRootDirectories() ) {
				root = p;
				break;
			}
			doBarrnap( dir, c, root, urls );
			//} else System.err.println( "no prodigal installed" );
        });
		MenuItem flanking = new MenuItem("Flanking");
		programs.getItems().add( flanking );
		flanking.setOnAction(e -> {
            DirectoryChooser fc = new DirectoryChooser();
            File f = fc.showDialog(null);
            if( f != null ) {
                if( !f.isDirectory() ) f = f.getParentFile();
                final File dir = f;
                final Sequences seqs = table.getSelectionModel().getSelectedItem();

                try {
                    InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ );

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
                    addSequences(file.getName(), seqs.getType(), file.toPath(), nseq);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		MenuItem clustal = new MenuItem("Clustal");
		programs.getItems().add( clustal );
		clustal.setOnAction(e -> {
            DirectoryChooser fc = new DirectoryChooser();
            File f = fc.showDialog(null);
            if( f != null ) {
                if( !f.exists() ) f.mkdirs();
                else if( !f.isDirectory() ) f = f.getParentFile();

                for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                    final String seqtype = seqs.getType();
                    try {
                        String file = seqs.getPath().getFileName().toString();
                        String[] split = file.split("/");
                        String fname = split[ split.length-1 ];
                        split = fname.split("\\.");
                        final String title = split[0];

                        final File infile = new File( f, "tmp_"+fname );

                        FileOutputStream fos = new FileOutputStream( infile );
                        InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ );

                        byte[] bb = new byte[100000];
                        int r = is.read(bb);
                        while( r > 0 ) {
                            fos.write(bb, 0, r);
                            r = is.read(bb);
                        }
                        is.close();
                        fos.close();

                        String inputPathFixed = NativeRun.fixPath( infile.getAbsolutePath() ).trim();
                        final String newname = seqs.getName()+"_aligned";
                        String newpath = f.getAbsolutePath()+"/"+newname+".fasta";
                        final Path newurl = new File( newpath ).toPath();
                        final Object[] cont = new Object[3];
                        Runnable run = () -> {
                            infile.delete();
                            addSequences(newname, seqtype, newurl, seqs.getNSeq());
                        };

                        List<String> cmdarr = null;
                        if( seqtype.equals("nucl") ) {
                            String[] cmds = {"clustalw", "-infile="+inputPathFixed, "-align", "-outfile="+newpath, "-output=FASTA"};
                            cmdarr = Arrays.asList( cmds );
                        } else {
                            String[] cmds = {"clustalo", "-i "+inputPathFixed, "-o "+newpath};
                            cmdarr = Arrays.asList( cmds );
                        }
                        nrun.runProcessBuilder("Clustal alignment", cmdarr, cont, false, run, false);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
		MenuItem concattree = new MenuItem("Concatenated tree");
		popup.getItems().add( concattree );
		concattree.setOnAction(e -> {
            JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
            JCheckBox	exgaps = new JCheckBox("Exclude gaps");
            JCheckBox	boots = new JCheckBox("Bootstrap");
            JOptionPane.showMessageDialog( null, new Object[] {jukes, exgaps, boots} );
            boolean cantor = jukes.isSelected();
            boolean bootstrap = boots.isSelected();
            boolean excludeGaps = exgaps.isSelected();

            List<Sequence> lseq = new ArrayList<>();

            Map<String,StringBuilder>	seqmap = new HashMap<>();

            try {
                for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                    String path = seqs.getPath().toString();
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
                        char c1 = seq.getCharAt( x );
                        if( c1 != '-' && c1 != '.' && c1 == ' ' ) {
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
            Sequence.distanceMatrixNumeric(lseq, dd, idxs, bootstrap, cantor, null, null);

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
            //okokJSObject win = JSObject.getWindow( SerifyApplet.this );
            //win.call("showTree", new Object[] {tree.toString()});
        });
		/*popup.add( new AbstractAction("Majority rule consensus tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});*/
		MenuItem distmatcorr = new MenuItem("Gene evolution phylogeny (distance matrix correlation)");
		popup.getItems().add( distmatcorr );
		distmatcorr.setOnAction(e -> {
            List<double[]>	ldmat = new ArrayList<>();
            List<String>	names = new ArrayList<>();

            try {
                List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
                for( Sequences seqs : lseqs ) {
                    String path = seqs.getPath().toString();
                    String name = seqs.getName();
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

                    Sequence.distanceMatrixNumeric(lseq, dmat, null, false, false, null, null);
                }

                if( ldmat.size() == lseqs.size() ) {
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
                    //okokJSObject win = JSObject.getWindow( SerifyApplet.this );
                    //win.call("showTree", new Object[] {treestr});
                }
            } catch( Exception e1 ) {
                e1.printStackTrace();
            }
        });
		MenuItem nnidistance = new MenuItem("Gene evolution phylogeny (nni distance)");
		popup.getItems().add( nnidistance );
		nnidistance.setOnAction(e -> {
            Map<String,String> namePath = new HashMap<String,String>();
            for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                String path = seqs.getPath().toString();
                String name = seqs.getName();
                int l = name.lastIndexOf('.');
                if( l != -1 ) name = name.substring(0, l);
                name = name.replace("(", "").replace(")", "").replace(",", "");

                namePath.put( name, path );
            }
            String tree = genePhylogenyNNI( namePath, false );
            //okokJSObject win = JSObject.getWindow( SerifyApplet.this );
            //win.call("showTree", new Object[] { tree });
        });
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem appendfilename = new MenuItem("Append filename");
		edit.getItems().add(appendfilename);
		appendfilename.setOnAction(e -> {
            for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                String filename = seqs.getPath().getFileName().toString();
                filename = "fn_"+filename;
                Path outp = seqs.getPath().getParent().resolve( filename );
                //File outf = outp.toFile();
                try {
                    serifier.appendFilename(seqs, outp);
                    Sequences nseqs = new Sequences( seqs.user, filename, seqs.type, outp, seqs.nseq );
                    addSequences(nseqs);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		MenuItem append = new MenuItem("Append");
		edit.getItems().add( append );
		append.setOnAction(e -> {
            Path dest = null;
            if( fs == null ) {
                JFileChooser fc = new JFileChooser();
                //fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
                    File f = fc.getSelectedFile();
                    dest = f.toPath();
                    //if( !f.isDirectory() ) f = f.getParentFile();
                }
            } else dest = fs.getPath("/joined.aa");

            List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
            List<Sequences> retlseqs = serifier.join( dest, lseqs, true, null, false );
            for( Sequences seqs : retlseqs ) {
                addSequences( seqs );
            }
        });
		MenuItem join = new MenuItem("Join");
		edit.getItems().add( join );
		join.setOnAction(e -> {
            Path dest = null;
            if( fs == null ) {
                JFileChooser fc = new JFileChooser();
                //fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
                    File f = fc.getSelectedFile();
                    dest = f.toPath();
                    //if( !f.isDirectory() ) f = f.getParentFile();
                }
            } else dest = fs.getPath("/joined.aa");

            List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
            List<Sequences> retlseqs = serifier.join( dest, lseqs, true, null, true );
            for( Sequences seqs : retlseqs ) {
                addSequences( seqs );
            }
        });
		MenuItem minlenfilt = new MenuItem("Min length filter");
		edit.getItems().add( minlenfilt );
		minlenfilt.setOnAction(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
                File f = fc.getSelectedFile();
                if( !f.isDirectory() ) f = f.getParentFile();
                final File dir = f;
                final Spinner spinner = new Spinner();
                //spinner.setValue( 500 ); //seqs.getNSeq() );
                //spinner.setPreferredSize( new Dimension(100,25) );
                final JDialog dl;
                java.awt.Window window = SwingUtilities.windowForComponent(nrun.cnt);
                if( window != null ) dl = new JDialog( window );
                else dl = new JDialog();
                dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
                JFXPanel c12 = new JFXPanel();
                c12.setLayout( new FlowLayout() );
                dl.setTitle("Number of sequences in each file");
                Button button = new Button("Ok");
                button.setOnAction(e13 -> {
                    dl.setVisible( false );
                    dl.dispose();
                });
                HBox hbox = new HBox();
                Scene scene = new Scene( hbox );
                hbox.getChildren().add( spinner );
                hbox.getChildren().add( button );
                c12.setScene( scene );
                dl.add(c12);
                dl.setSize(200, 60);

                dl.addWindowListener( new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}

                    @Override
                    public void windowClosing(WindowEvent e) {}

                    @Override
                    public void windowClosed(WindowEvent e) {
                        int spin = (Integer)spinner.getValue();
                        List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
                        for( Sequences seqs : lseqs ) {
                            Sequences nseqs = serifier.filtit( spin, seqs, dir );
                            serifier.addSequences( nseqs );
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
        });
		MenuItem tagsplit = new MenuItem("Tag split");
		edit.getItems().add( tagsplit );
		tagsplit.setOnAction(e -> {
            DirectoryChooser dfc = new DirectoryChooser();
            File f = dfc.showDialog(null);
            if( f != null ) {
                if( !f.isDirectory() ) f = f.getParentFile();
                final File dir = f;

                FileChooser fc = new FileChooser();
                File fo = fc.showOpenDialog(null);
                if( fo != null ) {
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

                    for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                        tagsplit( tagmap, seqs, dir, SerifyApplet.this );
                    }
                }
            }
        });
		MenuItem split = new MenuItem("Split");
		edit.getItems().add( split );
		split.setOnAction(e -> {
            DirectoryChooser fc = new DirectoryChooser();
            File f = fc.showDialog( null );
            if( f != null ) {
                if( !f.isDirectory() ) f = f.getParentFile();
                final File dir = f;
                final Spinner spinner = new Spinner();
                spinner.setValueFactory( new SpinnerValueFactory<Integer>() {

                    @Override
                    public void decrement(int steps) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void increment(int steps) {
                        // TODO Auto-generated method stub

                    }
                });
                //spinner.setValue( 1 ); //seqs.getNSeq() );
                //spinner.setPreferredSize( new Dimension(100,25) );
                final JDialog dl;
                java.awt.Window window = SwingUtilities.windowForComponent(nrun.cnt);
                if( window != null ) dl = new JDialog( window );
                else dl = new JDialog();
                dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
                JFXPanel c13 = new JFXPanel() {};
                c13.setLayout( new FlowLayout() );
                dl.setTitle("Number of sequences in each file");
                Button button = new Button("Ok");
                button.setOnAction( new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        dl.setVisible( false );
                        dl.dispose();
                    }
                });
                HBox hbox = new HBox();
                Scene scene = new Scene( hbox );
                c13.setScene( scene );
                hbox.getChildren().add(spinner);
                hbox.getChildren().add(button);
                dl.add(c13);
                dl.setSize(200, 60);

                dl.addWindowListener( new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}

                    @Override
                    public void windowClosing(WindowEvent e) {}

                    @Override
                    public void windowClosed(WindowEvent e) {
                        int spin = (Integer)spinner.getValue();
                        for( final Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                            List<Sequences> lseqs = serifier.splitit( spin, seqs, dir );
                            for( Sequences nseqs : lseqs ) {
                                addSequences( nseqs );
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
        });
		MenuItem cut = new MenuItem("Cut");
		edit.getItems().add( cut );
		cut.setOnAction(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
                File f1 = fc.getSelectedFile();
                if( !f1.isDirectory() ) f1 = f1.getParentFile();
                final File dir = f1;

                final Sequences seqs = table.getSelectionModel().getSelectedItem();
                String val = JOptionPane.showInputDialog("Select character", "_");
                try {
                    //URI uri = new URI( seqs.getPath() );
                    //seqs.getPath().toUri();
                    InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ );

                    if( seqs.getPath().endsWith(".gz") ) {
                        is = new GZIPInputStream( is );
                    }

                    //URL url = uri.toURL();
                    String urlstr = seqs.getPath().toUri().toString();
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
                    SerifyApplet.this.addSequences( trimname, seqs.getType(), f.toPath(), seqs.getNSeq() );
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		MenuItem trim = new MenuItem("Trim");
		edit.getItems().add( trim );
		trim.setOnAction(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            if( fc.showSaveDialog( nrun.cnt ) == JFileChooser.APPROVE_OPTION ) {
                File f1 = fc.getSelectedFile();
                if( !f1.isDirectory() ) f1 = f1.getParentFile();
                final File dir = f1;

                final TextField spinner = new TextField();
                //spinner.setValue( seqs.getNSeq() );
                //spinner.setPreferredSize( new Dimension(600,25) );

                final JDialog dl;
                java.awt.Window window = SwingUtilities.windowForComponent(nrun.cnt);
                if( window != null ) dl = new JDialog( window );
                else dl = new JDialog();

                dl.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
                JFXPanel c14 = new JFXPanel();
                c14.setLayout( new FlowLayout() );
                dl.setTitle("Filter sequences");
                Button browse = new Button("Browse");
                browse.setOnAction( new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        FileChooser	filechooser = new FileChooser();
                        File f = filechooser.showOpenDialog(null);
                        if( f != null ) {
                            spinner.setText( f.toURI().toString() );
                        }
                    }
                });
                Button button = new Button("Ok");
                button.setOnAction( new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        dl.setVisible( false );
                        dl.dispose();
                    }
                });
                HBox hbox = new HBox();
                Scene scene = new Scene( hbox );
                c14.setScene( scene );
                hbox.getChildren().add( spinner );
                hbox.getChildren().add( browse );
                hbox.getChildren().add( button );
                dl.add(c14);
                dl.setSize(800, 60);

                dl.addWindowListener( new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}

                    @Override
                    public void windowClosing(WindowEvent e) {}

                    @Override
                    public void windowClosed(WindowEvent e) {
                        String trim1 = spinner.getText();
                        trim( dir, trim1);
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
        });
		MenuItem transpose = new MenuItem("Transpose");
		edit.getItems().add( transpose );
		transpose.setOnAction(e -> {
            try {
                DirectoryChooser fc = new DirectoryChooser();
                File f1 = fc.showDialog( null );
                if( f1 != null ) {
                    if( !f1.isDirectory() ) f1 = f1.getParentFile();
                    final File dir = f1;

                    Map<String,Map<String,StringBuilder>>	seqmap = new HashMap<String,Map<String,StringBuilder>>();

                    List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
                    for( Sequences seqs : lseqs ) {
                        String path = seqs.getPath().toString();
                        String name = seqs.getName();
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
        });
		MenuItem removegaps = new MenuItem("Remove gaps");
		edit.getItems().add( removegaps );
		removegaps.setOnAction(e -> {
            DirectoryChooser fc = new DirectoryChooser();
            File f1 = fc.showDialog(null);
            if( f1 != null ) {
                if( !f1.isDirectory() ) f1 = f1.getParentFile();
                final File dir = f1;

                Map<String,Map<String,StringBuilder>>	seqmap = new HashMap<String,Map<String,StringBuilder>>();

                JavaFasta jf = new JavaFasta( null, serifier, null );
                jf.initDataStructures();

                for( Sequences seqs : table.getSelectionModel().getSelectedItems() ) {
                    String name = seqs.getName();
                    String path = seqs.getPath().toString();

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
        });
		MenuItem blastjoin = new MenuItem("Blast join");
		popup.getItems().add( blastjoin );
		blastjoin.setOnAction(e -> {
            //JSObject	jso = JSObject.getWindow(SerifyApplet.this);
            String s = null;//(String)jso.call("getSelectedBlast", new Object[] {} );

            FileChooser fc = new FileChooser();
            File f = fc.showSaveDialog(null);
            if( f != null ) {
                try {
                    blastJoin( new FileInputStream(s), new PrintStream(new FileOutputStream(f)) );
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		MenuItem makeclusters = new MenuItem("Make clusters");
		popup.getItems().add( makeclusters );
		makeclusters.setOnAction(e -> {
            InputStream is = null;
            /*try {
                JSObject	jso = JSObject.getWindow(SerifyApplet.this);
                String s = (String)jso.call("getSelectedBlast", new Object[] {} );
                is = new FileInputStream(s);
            } catch( Exception ex ) {

            }*/

            FileChooser fc = new FileChooser();
            if( is == null ) {
                File f = fc.showOpenDialog(null);
                if( f != null ) {
                    try {
                        is = new FileInputStream( f );
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            File f = fc.showSaveDialog(null);
            if( f != null ) {
                try {
                    List<Set<String>> cluster = new ArrayList<Set<String>>();
                    serifier.makeBlastCluster( new BufferedReader( new InputStreamReader(is) ), null, 1, 0.5f, 0.5f, null, cluster, null );

                    Set<String> headset = new HashSet<String>();
                    for( Set<String> cl : cluster ) {
                        for( String c15 : cl ) {
                            headset.add(c15);
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
        });
		popup.getItems().add( new SeparatorMenuItem() );
		MenuItem fasttree = new MenuItem("FastTree prepare");
		popup.getItems().add( fasttree );
		fasttree.setOnAction(e -> {
            List<Sequences> lseqs = table.getSelectionModel().getSelectedItems();
            List<Sequences> retlseqs = serifier.fastTreePrepare( lseqs );
            for( Sequences seqs : retlseqs ) {
                try {
                    SerifyApplet.this.addSequences(seqs.getName(), seqs.getPath(), null);
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		MenuItem bren = new MenuItem("Blast rename");
		popup.getItems().add( bren );
		bren.setOnAction(e -> {
            Sequences seqs = table.getSelectionModel().getSelectedItem();
            if( seqs != null ) {
                FileChooser fc = new FileChooser();
                File f = fc.showOpenDialog(null);
                if( f != null ) {
                    String s = null;
                    /*try {
                        JSObject	jso = JSObject.getWindow(SerifyApplet.this);
                        s = (String)jso.call("getSelectedBlast", new Object[] {} );
                    } catch( Exception exp ) {
                        exp.printStackTrace();
                    }*/

                    if( s == null ) {
                        FileChooser filechooser = new FileChooser();
                        File sf = filechooser.showOpenDialog(null);
                        if( sf != null ) {
                            s = sf.getAbsolutePath();
                        }
                    }

                    if( s != null ) {
                        Sequences ret = serifier.blastRename( seqs, s, f, false );
                        serifier.addSequences( ret );
                    }
                }
            }
        });
		
		//table.setComponentPopupMenu( popup );
		//JScrollPane	scrollpane = new JScrollPane( table );
		//scrollpane.setComponentPopupMenu( popup );
		//scrollpane.setBackground( bgcolor );
		//scrollpane.getViewport().setBackground( bgcolor );
		
		JScrollPane	scrollpane = new JScrollPane();
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
						FlxReader flx = new FlxReader();
						
						String comp = null;
						List<Sequence> bb = null;
						//byte[] bb = null;
						Sequences seqs = table.getSelectionModel().getSelectedItem();
						if( seqs != null ) {
							comp = seqs.getName();
							Path filepath = seqs.getPath();
							bb = Sequence.readFasta(filepath, null);
						}
						for( File f : lf ) {
							if( f.isDirectory() ) {
								Path dest = root.resolve(f.getName()+".fna");
								//Files.copy(f.t, dest, StandardCopyOption.REPLACE_EXISTING);
								//addSequences( f.getName(), dest, null );
								//File nf = new File(f, f.getName()+".fna");
								Writer fw = Files.newBufferedWriter(dest, StandardOpenOption.CREATE);
								flx.start( f.getParentFile().getAbsolutePath()+"/", f.getName(), false, fw, comp, bb);
								fw.close();
								
								JavaFasta	jf = new JavaFasta(flx.serifier);
								JFrame		frame = new JFrame();
								frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
								frame.setSize(800, 600);
								jf.initGui(frame);
								jf.updateView();
								frame.setVisible( true );
								
								System.err.println("about to add seqs " + f.getName());
								
								addSequences( f.getName(), dest, null );
							} else {
								
								System.err.println("about to add seqs2 " + f.getName());
								addSequences( f.getName(), f.toPath(), null );
							}
						}
					} else if( obj instanceof Image ) {
						
					} else if( obj instanceof String ) {
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						String filelistStr = (String)obj;
						if( filelistStr.contains("\n>") ) {
							Map<String,BufferedReader>	isrmap = new HashMap<>();
							BufferedReader br = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(filelistStr.getBytes()) ));
							isrmap.put("", br);
							addSequences(null, isrmap, null, null);
						} else {
							String[] fileStr = filelistStr.split("\n");
							for( String fileName : fileStr ) {
								Path val = Paths.get( fileName );
								//File f = new File( new URI( fileName ) );
								//String[] split = val.split("/");
								addSequences( val.getFileName().toString()/*split[ split.length-1 ]*/, val, null );
							}
						}
					} else if( obj instanceof Reader ) {
						//obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						
						CharArrayWriter	caw = new CharArrayWriter();
						Reader rd = (Reader)obj;
						char[] cbuf = new char[1024];
						int r = rd.read(cbuf);
						while( r > 0 ) {
							caw.write( cbuf, 0, r );
							r = rd.read(cbuf);
						}
						
						String filelistStr = caw.toString();
						String[] fileStr = filelistStr.split("\n");
						
						for( String fileName : fileStr ) {
							Path val = Paths.get( fileName.trim() );
							//String[] split = val.split("/");
							addSequences( val.getFileName().toString(), val, null );
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
							Path val = Paths.get( fileName.trim() );
							//String[] split = val.split("/");
							addSequences( val.getFileName().toString(), val, null );
						}
					}
				} catch (IOException | URISyntaxException | InterruptedException e) {
					e.printStackTrace();
				}
				return true;
			}
		});

		if(vbox!=null) {
			ObservableList<javafx.scene.Node> children = vbox.getChildren();
			if (children != null) {
				children.add(mb);
				children.add(table);
			}

			VBox.setVgrow(table, Priority.ALWAYS);
		}
	}
	
	public void updateSequences( final String user, final String name, final String type, final Path path, final int nseq, final String key ) {
		Sequences seqs = new Sequences( user, name, type, path, nseq );
		
		mseq.put( path, seqs );
		
		seqs.setKey( key );
		serifier.addSequences( seqs );
		//serifier.sequences.add( seqs );
		updateSequences(seqs);
	}
	
	public void updateSequences( final Sequences seqs ) {
		AccessController.doPrivileged((PrivilegedAction<String>) () -> {
            new Thread(() -> {
				try {
					InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ );
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*if( succ ) {
					table.tableChanged( new TableModelEvent( table.getModel() ) );
				}*/
			}).start();

            return null;
        });
	}
	
	public void addSequences( Sequences seqs ) {
		boolean unsucc = false;
		/*try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "addSequences", new Object[] {seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNSeq()} );
		} catch( NoSuchMethodError | Exception e ) {
			unsucc = true;
		}*/
		
		if( unsucc ) {
			updateSequences( seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNSeq(), null ); 
			//updateSequences( seqs );
		}
	}
	
	private void addSequences( String user, String name, String type, Path path, int nseq ) {
		boolean unsucc = true;
		/*try {
			JSObject js = JSObject.getWindow( SerifyApplet.this );
			js.call( "addSequences", new Object[] {user, name, type, path, nseq} );
		} catch( NoSuchMethodError | Exception e ) {
			unsucc = true;
		}*/
		
		if( unsucc ) {
			updateSequences(user, name, type, path, nseq, null);
		}
	}
	
	public void addSequences( String name, BufferedReader rd, Path path, String replace ) throws URISyntaxException, IOException {
		Map<String,BufferedReader> rds = new HashMap<>();
		rds.put( name, rd );
		addSequences(name, rds, path, replace);
	}

	public void addSequences( String name, Map<String,BufferedReader> rds, Path path, String replace ) throws URISyntaxException, IOException {
		String type = "nucl";
		int nseq = 0;
		
		Map<String,Stream<String>>	gbks = new HashMap<>();
		for( String tag : rds.keySet() ) {
			BufferedReader br = rds.get( tag );
			//String path = paths.get( tag );
			String line = br.readLine();
		
			if( line != null ) {
				if( line.endsWith(":") ) {
					DirectoryChooser	dirchooser = new DirectoryChooser();
					//filechooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
					File dir = dirchooser.showDialog(null);
					if( dir != null ) {
						if( !dir.exists() ) dir.mkdirs();
						
						//Set<String>	curset = new HashSet<String>();
						int idx = line.indexOf(']');
						String[] split = null;
						if( idx >= 0 ) {
							split = line.substring(1, idx).split(",");
						}
						String curname = line.substring( idx+1, Math.min( 128+idx+1, line.length()-1 ) ).replace(' ', '_');;
						//curset.add( curname );
						
						List<FileWriter>	lfw = new ArrayList<>();
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
								
								addSequences( curname, f.toPath(), replace );
								
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
						
						addSequences( curname, f.toPath(), replace );
					}
				} else if( line.startsWith(">") ) {
					if( path == null ) {
						DirectoryChooser	dirchooser = new DirectoryChooser();
						File f = dirchooser.showDialog( null );
						if( f != null ) {
							path = f.toPath();
						}
					}
					
					if( path != null ) {
						while( line != null ) {
							if( line.startsWith(">") ) {
								nseq++;
								
								if( nseq % 1000 == 0 ) System.err.println( "seq counting: "+nseq );
							} else if( type.equals("nucl") && line.length() > 0 && !line.matches("^[acgtbdykhvrswmunxACGTBDYKHVRSWMUNX]+$") ) {
								type = "prot";
							}
							line = br.readLine();
						}
						
						if( nseq > 0 ) {
							addSequences(name, type, path, nseq);
						} else System.err.println( "no sequences in file" );
					}
				} else {
					/*StringBuilder filetext = new StringBuilder();
					while( line != null ) {
						filetext.append( line+"\n" );
						line = br.readLine();
					}*/
					Stream<String> strstr = Stream.<String>builder().add(line).build();
					Stream<String> gbff = Stream.concat(strstr, br.lines());
					gbks.put(tag, gbff);
					
					//boolean amino = true;
					//String[] annoarray = {"tRNA", "rRNA"};//{"CDS", "tRNA", "rRNA", "mRNA"};
					//String[] annoarray = {"CDS"};
				}
				//br.close();
			}
		}
		
		if( gbks.size() > 0 ) {
			Map<String,Path>	map = new HashMap<>();
			Path firsturi = path.getParent().resolve( path.getFileName()+".fna" ); //Paths.get( new URI(path+".fna") );
			BufferedWriter out = Files.newBufferedWriter( firsturi, StandardOpenOption.CREATE );
			
			Path uri = path.getParent().resolve( path.getFileName()+".aa" );
			//FileWriter out = new FileWriter( new File( uri ) );
			map.put( "CDS", uri );
			
			uri = path.getParent().resolve( path.getFileName()+".trna" );
			//out = new FileWriter( new File( uri ) );
			map.put( "tRNA", uri );
			
			uri = path.getParent().resolve( path.getFileName()+".rrna" );
			//out = new FileWriter( new File( uri ) );
			map.put( "rRNA", uri );
			
			uri = path.getParent().resolve( path.getFileName()+".mrna" );
			//out = new FileWriter( new File( uri ) );
			map.put( "mRNA", uri );
			
			/*uri = new URI(path+".namemap");
			map.put( "nameMap", uri );*/
			
			GBK2AminoFasta.handleText( gbks, map, out, path, replace, noseq );
			
			//+firsturi.toString().replace(name, "")

			/*addSequences( name+".fna", firsturi, null );
			for( String tg : map.keySet() ) {
				uri = map.get( tg );
				addSequences( name+"."+tg, uri, null );
			}*/
		}
	}
	
	public void addSequencesPath( String name, Map<String,Path> urimap, Path p, String replace ) throws URISyntaxException, IOException {
		try {
			Map<String,BufferedReader>	isrmap = new HashMap<>();
			for( String tag : urimap.keySet() ) {
				Path uripath = urimap.get( tag );
				BufferedReader br;
				if( uripath.endsWith(".gz") ) {
					InputStream is = Files.newInputStream( uripath, StandardOpenOption.READ );
					is = new GZIPInputStream(is);
					br = new BufferedReader( new InputStreamReader(is) );
				} else br = Files.newBufferedReader(uripath);

				isrmap.put( tag, br );
				p = uripath;
			}
			Path path = p;
			
			/*String uri = null;
			for( String ur : urimap.keySet() ) {
				uri = urimap.get( ur );
				break;
			}*/
			Platform.runLater(() -> {
				try {
					addSequences(name, isrmap, path, replace);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void addSequences( String name, Path path, String replace ) throws URISyntaxException, IOException {
		//URI uri = path.toUri();

		//boolean succ = true;
		BufferedReader br = null;
		try {
			if( Files.exists(path) ) {
				if( path.toString().endsWith(".gz") ) {
					InputStream is = Files.newInputStream( path, StandardOpenOption.READ );
					is = new GZIPInputStream(is);
					InputStreamReader isr = new InputStreamReader( is );
					br = new BufferedReader(isr);
				} else {
					br = Files.newBufferedReader(path);
				}
			}
		} catch( Exception e ) {
			//succ = false;
			e.printStackTrace();
		}
		
		if( br != null ) {
			Map<String,BufferedReader>	isrmap = new HashMap<>();
			isrmap.put( name.substring(0, name.lastIndexOf('.')), br);
			addSequences( name, isrmap, path, replace );
			//FileReader	fr = new FileReader( f );
		}
	}
	
	public void addSequences( String name, String type, Path path, int nseq ) {
		addSequences( getUser(), name, type, path, nseq );
	}
	
	public String getUser() {
		return user;
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
			Map<String,Node>	alltrees = new TreeMap<>();
			TreeUtil treeutil = new TreeUtil();
			Map<Set<String>,NodeSet> nmap = new HashMap<>();
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
			}s
			
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
				
				List<Double> lad = new ArrayList<>();
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
			
			List<Object[]>	sortlist = new ArrayList<>();
			for( String name : alltrees.keySet() ) {
				double dist = 0.0;
				Node tree = alltrees.get( name );
				dist = treeutil.nDistance(root, tree);
				//System.err.println( name + "  " + dist );
				sortlist.add( new Object[] {name, dist} );
			}
			Collections.sort( sortlist, Comparator.comparingDouble(o2 -> (Double) o2[1]));
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
		
		List<Node>		lnode = new ArrayList<>();
		List<String>	names = new ArrayList<>();
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
						Sequence.distanceMatrixNumeric(lseq, dmat, null, false, false, null, null);
						
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
				
				Path nf = new File( "/u0/all.blastout" ).toPath();//new File( dir, ""+f.getName()+".blastout" );
				System.err.println( "about to parse " + nf.getFileName() );
				List<Set<String>> cluster = new ArrayList<>();
				serifier.makeBlastCluster( Files.newBufferedReader(nf), null, 1, 0.5f, 0.5f, null, cluster, null );
				
				Map<String,String> headset = new HashMap<>();
				for( Set<String> cl : cluster ) {
					Map<String,Integer> tegcount = new HashMap<>();
					Map<String,Integer> tegpercmap = new HashMap<>();
					Map<String,String> 	tegmap = new HashMap<>();
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
			
			System.err.println( key + "\t" +mean+"������"+stdev+"\t"+meanph+"������"+stdevph );
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
		
		sa.init( frame, null, System.getProperty("user.name") );
		frame.setVisible( true );
	}
}
