package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

public class Corp extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
	//int						id;
	String					home;
	String					kt;
	String					type;
	private double			x,y,z;
	double					vx,vy,vz;
	double					px,py,pz;
	String					text;
	double					depz;
	double					coulomb;
	Color					color = Color.green;
	Color					subcolor = Color.red;
	
	List<BufferedImage>			images = new ArrayList<BufferedImage>();
	List<String>				imageNames = new ArrayList<String>();
	Map<Corp,LinkInfo>		connections = new HashMap<Corp,LinkInfo>();
	Map<Corp,LinkInfo>		backconnections = new HashMap<Corp,LinkInfo>();
	boolean						selected = false;
	boolean						hilighted = false;
	
	static byte[]			buffer = new byte[200000];
	double					size = 32;
	static Set<Corp>		selectedList = new HashSet<Corp>();
	static Corp				drag;
	static Color 			paleColor = new Color( 255,255,255,128 );
	//static JTextField	textfield = new JTextField();
	static Prop					prop;
	static List<Corp>			corpList = new ArrayList<Corp>();
	static Map<String,Corp>		corpMap = new HashMap<String,Corp>();
	static boolean				autosave = false;
	//static Map<String,Corp>		corpNameMap = new HashMap<String,Corp>();
	
	class LinkInfo {
		public LinkInfo( double strength, double offset ) {
			this.linkStrength = strength;
			this.linkOffset = offset;
		}
		
		public double getStrength() {
			return linkStrength;
		}
		
		public double getOffset() {
			return linkOffset;
		}
		
		Set<String>				linkTitles = new HashSet<String>();
		private double			linkStrength;
		private double			linkOffset;
	};
	
	String				selectedLink = null;
	
	static {
		/*textfield.setHorizontalAlignment( JTextField.CENTER );
		textfield.addKeyListener( new KeyListener() {
			
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
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					drag.name = textfield.getText();
					Container c = textfield.getParent();
					if( c != null ) {
						c.remove( textfield );
						c.repaint();
					}
				}
			}
		});*/
	}
	
	public void swapColors() {
		Color oldcolor = color;
		color = subcolor;
		subcolor = oldcolor;
	}
	
	public void setCoulomb( double coulomb ) {
		this.coulomb = coulomb;
	}
	
	public double getCoulomb() {
		return this.coulomb;
	}
	
	public double getSizeDouble() {
		return size;
	}
	
	public void setSize( double size ) {
		this.size = size;
	}
	
	boolean hassaved = false;
	public void saveRecursive() throws IOException {
		this.save();
		hassaved = true;
		for( Corp c : this.getLinks() ) {
			if( !c.hassaved ) c.saveRecursive();
		}
	}
	
	static String newline = "\r\n";
	
	static int createIndex = 1;
	static String getCreateName() {
		return "unknown"+createIndex++;
	}
	
	private void writeSave( Writer fw, int[] hashs ) throws IOException {
		fw.write( this.getName().trim() + newline );
		fw.write( (this.kt == null || this.kt.length() == 0) ? "noid" : this.kt + newline );
		fw.write( this.type + newline );
		fw.write( (int)this.x+","+(int)this.y+","+(int)this.z + newline );
		fw.write( newline );
		if( connections.size() == 0 ) {
			fw.write( "nil" + newline );
		} else {
			for( Corp c : connections.keySet() ) {
				fw.write( c.getName() + "\t" + connections.get(c) + newline );
			}
		}
		fw.write( newline );
		
		int i = 0;
		for( BufferedImage img : images ) {
			//String imgName = imageNames.get(i);
			//int hash = imgName == null ? img.hashCode() : imgName.hashCode();
			fw.write( hashs[i] + ".png" + newline );
			
			i++;
		}
		fw.write( newline );
		if( this.text != null ) {
			fw.write( this.text );
		}
		fw.write( newline );	
		fw.close();
	}
	
	public void save() throws IOException {
		if( autosave ) {
			boolean succ = false;
			try {
				PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
				BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
				 
				String name = "spoil_" + this.getName().trim();
				URL url = new URL( bs.getCodeBase().toString() + name );
				String[] names = ps.getNames( bs.getCodeBase() );
				Set<String>	nameset = new HashSet<String>( Arrays.asList(names) );
				if( !nameset.contains(name) ) {
					ps.create( url, 1000 );
				}
				FileContents fc = ps.get( url );
				OutputStream os = fc.getOutputStream( false );
				OutputStreamWriter ow = new OutputStreamWriter( os );
				
				int[] hashs = new int[images.size()];
				int i= 0;
				for( BufferedImage img : images ) {
					String imgName = imageNames.get(i);
					int hash = imgName == null ? img.hashCode() : imgName.hashCode();
					hashs[i] = hash;
		
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write( img, "png", baos );
					byte[] imgbb = baos.toByteArray();
					
					URL imgurl = new URL( bs.getCodeBase().toString() + hash + ".png" );
					ps.create( imgurl, imgbb.length );
					fc = ps.get( url );
					os = fc.getOutputStream( false );
					os.write( imgbb );
					
					i++;
				}
				writeSave( ow, hashs );
				
				succ = true;
			} catch( NoClassDefFoundError e ) {
				e.printStackTrace();
			} catch( Exception e ) {
				e.printStackTrace();
			}
			
			if( !succ ) {
				String homedir = System.getProperty("user.home");
				File dir = new File( homedir, "spoil" );
				if( !dir.exists() ) dir.mkdirs();
				File mdir = new File( dir, "images" );
				if( !mdir.exists() ) mdir.mkdirs();
				File save = new File( dir, this.getName().trim() );
				FileWriter fw = new FileWriter( save );
				
				int[] hashs = new int[images.size()];
				int i= 0;
				for( BufferedImage img : images ) {
					String imgName = imageNames.get(i);
					int hash = imgName == null ? img.hashCode() : imgName.hashCode();
					hashs[i] = hash;
		
					File f = new File( mdir, Integer.toString(hash)+".png" );
					ImageIO.write( img, "png", f );
					i++;
				}
				writeSave( fw, hashs );
			}
		}
		
		for( String name : corpMap.keySet() ) {
			Corp c = corpMap.get( name );
			if( c == this ) {
				corpMap.remove( name );
				corpMap.put( c.getName(), c );
				
				break;
			}
		}
	}
	
	private void save( String oldname ) throws IOException {
		save();
	}
	
	static char[]	cbuf = new char[8192];
	public void load() throws IOException {
		boolean succ = false;
		try {
			PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
			 
			URL url = new URL( bs.getCodeBase().toString() + "spoil_" + this.getName().trim() );
			//ps.create( url, 1000 );
			FileContents fc = ps.get( url );
			InputStream is = fc.getInputStream();
			InputStreamReader isr = new InputStreamReader( is );
			loadFile( isr, bs.getCodeBase().toString() );
			
			succ = true;
		} catch( NoClassDefFoundError e ) {
			e.printStackTrace();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( !dir.exists() ) dir.mkdirs();
			File save = new File( dir, this.getName().trim() );
			loadFile( new FileReader( save ), new File( save.getParentFile(), "images" ).toURI().toURL().toString() );
		}
	}
	
	private void deleteSave() {
		boolean succ = false;
		try {
			PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
			 
			String name = "spoil_" + this.getName().trim();
			String[] names = ps.getNames( bs.getCodeBase() );
			Set<String>	nameset = new HashSet<String>( Arrays.asList(names) );
			if( nameset.contains(name) ) {
				URL url = new URL( bs.getCodeBase().toString() + name );
				ps.delete( url );
			}
			
			succ = true;
			
			System.err.println( "deleting " + name );
		} catch( NoClassDefFoundError e ) {
			e.printStackTrace();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( !dir.exists() ) dir.mkdirs();
			File save = new File( dir, this.getName().trim() );
			save.delete();
		}
	}
	
	public void delete() {
		deleteSave();
		
		Container ct = this.getParent();
		ct.remove( this );
		Corp crp = corpMap.remove( this.getName().trim() );
		for( Corp corp : corpList ) {
			//corp = corpMap.get(name);
			if( corp.getLinks().contains( this ) ) {
				corp.connections.remove( this );
			}
			
			if( corp.getBackLinks().contains( this ) ) {
				corp.backconnections.remove( this );	
			}
		}
		corpList.remove( this );
		//ct.repaint();
	}
	
	public void loadFile( Reader fr, String baseurl ) throws IOException {
		//FileReader 	fr = new FileReader( save );
		//File		mdir = new File( save.getParentFile(), "images" );
		
		int r = fr.read(cbuf);
		if( r > 0 ) {
			String str = new String( cbuf, 0, r );
			
			String[] split = str.split( newline+newline );
			String[] nkt = split[0].split( newline );
			
			if( nkt.length >= 4 ) {
				super.setName( nkt[0] );
				this.kt = nkt[1];
				this.type = nkt[2];
				String[] loc = nkt[3].split(",");
				if( loc.length == 3 ) {
					this.x = Double.parseDouble(loc[0]);
					this.y = Double.parseDouble(loc[1]);
					this.z = Double.parseDouble(loc[2]);
				}
				
				this.setBounds((int)x, (int)y, (int)size, (int)size);
			}
			
			String[] con = split[1].split( newline );
			for( String val : con ) {
				String[] tsplit = val.split("\t");
				if( tsplit.length == 2 ) {
					//int id = Integer.parseInt(tsplit[0]);
					String id = tsplit[0];
					if( corpMap.containsKey( id ) ) {
						Corp 	corp = corpMap.get( id );
						String 		sval = tsplit[1].substring(1, tsplit[1].length()-1 );
						String[] 	splt = sval.split(",");
						for( String s : splt ) {
							this.addLink( corp, s.trim() );
						}
						//connections.put( corp, tsplit[1] );
					}
				}
			}
			
			if( split.length > 2 ) {
				String[] img = split[2].split( newline );
				
				boolean succ = false;
				try {
					PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
					BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
					 
					String[] names = ps.getNames( bs.getCodeBase() );
					Set<String>	snames = new HashSet<String>( Arrays.asList(names) );
					for( String val : img ) {
						if( val.length() > 0 && snames.contains(val) ) {
							URL url = new URL( baseurl + val );
							FileContents fc = ps.get( url );
							InputStream is = fc.getInputStream();
							
							images.add( ImageIO.read( is ) );
							imageNames.add( url.toString() );
						}
					}
					
					succ = true;
				} catch( NoClassDefFoundError e ) {
					e.printStackTrace();
				} catch( Exception e ) {
					e.printStackTrace();
				}
				
				if( !succ ) {
					for( String val : img ) {
						//File f = new File( mdir, val );
						if( val.length() > 0 ) {
							URL url = new URL( baseurl + val );
							
							BufferedImage bi = null;
							try {
								bi = ImageIO.read( url );
							} catch( Exception e ) {
								
							}
							
							if( bi != null ) {
								images.add( bi );
								imageNames.add( url.toString() );
							}
						}
					}
				}
			}
			
			this.text = "";
			for( int i = 3; i < split.length; i++ ) {
				this.text += split[i] + newline + newline;
			}
			if( this.text.length() > 0 ) this.setToolTipText( this.text );
		}
		fr.close();
	}
	
	public String relCall( String xml ) {
		URL 		url = null;
		try {
			URLConnection connection = url.openConnection();
			if( connection instanceof HttpURLConnection ) {
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				if( connection instanceof HttpsURLConnection ) {
					HttpsURLConnection httpsConnection = (HttpsURLConnection)connection;
					
					TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}
		
							public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
							public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
						}
					};
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					
					httpsConnection.setHostnameVerifier( new HostnameVerifier() {
						public boolean verify(String rserver, SSLSession sses) {
							if (!rserver.equals(sses.getPeerHost())){
								System.out.println( "certificate does not match host but continuing anyway" );
							}
							return true;
						}
					});
					String encoding = Base64Coder.encodeString("enter");
			        httpsConnection.setRequestProperty ("Authorization", "Basic " + encoding);
					httpsConnection.setSSLSocketFactory( sc.getSocketFactory() );
				}
				httpConnection.setRequestMethod("POST");
				httpConnection.setDoInput( true );
				httpConnection.setDoOutput( true );
				String uenc = "fbxml="+URLEncoder.encode( xml, "UTF-8" );//"ISO-8859-1" );
				httpConnection.getOutputStream().write( uenc.getBytes() );
				
				int total = 0;
				InputStream inputStream = httpConnection.getInputStream();
				int r = inputStream.read( buffer );
				while( r != -1 ) {
					total += r;
					r = inputStream.read( buffer, total, buffer.length-total );
				}
				
				if( total > 0 ) {
					String ret = new String( buffer, 0, total, "ISO-8859-1" );
					return ret;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void getRelations() throws UnsupportedEncodingException {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
		xml += "<facebook-relation>\n";
		
		int i = 0;
		String name = this.getName().trim();//new String( this.name.getBytes(), "ISO-8859-1" );
		xml +="<facebook-user id=\"0\" name=\""+name+"\" dob=\""+this.kt+"\" />\n";
		for( Component c : this.getParent().getComponents() ) {
			if( c instanceof Corp && c != this && ((Corp)c).type.equals("person") ) {
				Corp corp = (Corp)c;
				
				if( corp.kt.length() == 8 ) {
					name = corp.getName();//new String( corp.name.getBytes(), "ISO-8859-1" );
					xml += "<facebook-friend id=\""+(++i)+"\" name=\""+name+"\" dob=\""+corp.kt+"\" />\n";
				}
			}
		}
		xml += "</facebook-relation>\n";
		
		String result = relCall( xml );
		System.err.println( result );
		
		String check = "relation=\"";
		String[]	split = result.split("\n");
		for( String str : split ) {
			i = str.indexOf(check);
			if( i > 0 ) {
				int n = str.indexOf("\"",i+check.length());
				String rel = str.substring(i+check.length(),n);
				rel = rel.replace("&eth;", "ð");
				rel = rel.replace("&oacute;", "ó");
				rel = rel.replace("&uacute;", "ú");
				rel = rel.replace("&ouml;", "ö");
				char ch = str.charAt(i+check.length());
				if( ch < '0' || ch > '9' ) { //|| ch == '3' || ch == '2' ) {
					i = str.indexOf("name=\"");
					n = str.indexOf('"', i+6);
					name = str.substring(i+6,n);
					
					for( Component c : this.getParent().getComponents() ) {
						if( c instanceof Corp && c != this && ((Corp)c).type.equals("person") ) {
							Corp corp = (Corp)c;
							if( corp.getName().equals(name) ) {
								this.addLink( corp, rel );
							}
						}
					}
					this.getParent().repaint();
				}
			}
		}
		//result = new String( result.getBytes(), "UTF-8" );
		//System.err.println( result );
	}
	
	public Corp() {
		int i = 0;
		String str = "unkown";
		String val = str+i;
		while( corpMap.containsKey(val) ) {
			i++;
			val = str+i;
		}
		
		super.setName( val );
		this.kt = "00";
		this.type = "unkown";
		this.x = 20;
		this.y = 20;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		
		init();
	}
	
	public Corp( String name ) {		
		super.setName( name );
		this.kt = "00";
		this.type = "unkown";
		this.x = 20;
		this.y = 20;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		
		init();
	}
	
	public Corp( String name, String type, double x, double y ) {
		super();
		
		super.setName( name );
		this.kt = "00";
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		
		this.setBounds((int)x, (int)y, (int)size, (int)size);
		init();
	}
	
	public void properties() {
		Corp.prop.currentCorp = Corp.this;
		Corp.prop.name.setText( Corp.this.getName().trim() );
		Corp.prop.kt.setText( Corp.this.type );
		Corp.prop.color = Corp.this.color;
		//Corp.prop.home.setText( Corp.this.home );
		Corp.prop.text.setText( Corp.this.text );
		Corp.this.getParent().add( Corp.prop );
		Corp.this.getParent().setComponentZOrder( Corp.prop, 0 );
		Corp.prop.setBounds( Corp.this.getX()+Corp.this.getWidth()+10, Corp.this.getY(), 400, 300 );
	}
	
	public void subselect( Corp c ) {
		if( !c.selected ) {
			c.selected = true;
			selectedList.add( c );
			for( Corp nc : c.getLinks() ) {
				subselect( nc );
			}
		}
	}
	
	public void deselectAll() {
		for( Corp c : selectedList ) {
			c.selected = false;
		}
		selectedList.clear();
	}
	
	public void init() {
		corpList.add( this );
		corpMap.put( this.getName().trim(), this );
		
		//this.setToolTipText( name );	
		
		this.addMouseListener( this );
		this.addMouseMotionListener( this );
		this.addKeyListener( this );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Select subgraph") {
			@Override
			public void actionPerformed(ActionEvent e) {
				deselectAll();
				Corp.this.selected = false;
				subselect( Corp.this );
				Container ct = Corp.this.getParent();
				ct.requestFocus();
				ct.repaint();
			}
		});
		/*popup.add( new AbstractAction("Get relations") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getRelations();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		});*/
		popup.addSeparator();
		popup.add( new AbstractAction("Properties") {
			@Override
			public void actionPerformed(ActionEvent e) {
				properties();
			}
		});
		this.setComponentPopupMenu( popup );
		
		this.setFocusable( true );
		
		TransferHandler th = new TransferHandler() {
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY;
			}

			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
						String ret = "";
						return ret;
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.stringFlavor, DataFlavor.javaFileListFlavor, DataFlavor.imageFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.stringFlavor || arg0 == DataFlavor.javaFileListFlavor || arg0 == DataFlavor.imageFlavor ) {
							return true;
						}
						return false;
					}
				};
			}
			
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				if( flavor == DataFlavor.imageFlavor || flavor == DataFlavor.stringFlavor || flavor == DataFlavor.javaFileListFlavor ) {
					return true;
				}
				return false;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;
				
				System.err.println( support.getDataFlavors().length );
				int b = Arrays.binarySearch( support.getDataFlavors(), DataFlavor.javaFileListFlavor, new Comparator<DataFlavor>() {
					@Override
					public int compare(DataFlavor o1, DataFlavor o2) {
						return o1 == o2 ? 1 : 0;
					}
				});
				/*System.err.println(b);
				if( b != -1 ) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.imageFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
				try {
					obj = support.getTransferable().getTransferData(DataFlavor.imageFlavor);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					if( obj != null && obj instanceof File[] ) {
						//File[] ff = (File[])obj;
						//wbStuff( ff[0].getCanonicalPath() );
					} else if( obj instanceof Image ) {
						BufferedImage	image = (BufferedImage)support.getTransferable().getTransferData(DataFlavor.imageFlavor);
						System.err.println( "adding image " );
						images.add( image );
						imageNames.add( null );
						Corp.this.repaint();
					} else {
						obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						System.err.println(obj);
						URL url = null;
						try {
							url = new URL( (String)obj );
							Image image = ImageIO.read( url );
							
							if( image != null ) {
								images.add( (BufferedImage)image );
								imageNames.add( url.toString() );
								Corp.this.repaint();
							}
						} catch( Exception e ) {
							e.printStackTrace();
						}
						/*if (obj != null) {
							String stuff = obj.toString();
							if( stuff.contains("file://") ) {
								URL url = new URL( stuff );
								wbStuff( url.getFile() );
							}
							parseData(stuff);
							tupleList = calcData();
							
							reload();
						}*/
					}	
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return true;
			}
		};
		
		this.setTransferHandler( th );
		
		ActionMap map = this.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME),
				TransferHandler.getCutAction());
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
				TransferHandler.getPasteAction());

		InputMap imap = this.getInputMap();
		imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler.getCutAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));
	}
	
	public Set<Corp>	getLinks() {
		return connections.keySet();
	}
	
	public Set<Corp>	getBackLinks() {
		return backconnections.keySet();
	}
	
	public void addLink( Corp corp, String link, double strength, double offset ) {
		LinkInfo linkInfo = connections.get(corp);
		if( linkInfo == null ) {
			linkInfo = new LinkInfo( strength, offset );
			connections.put( corp, linkInfo );
			corp.backconnections.put( this, linkInfo );
		}
		linkInfo.linkTitles.add( link );
	}
	
	public void addLink( Corp corp, Set<String> linknames ) {
		LinkInfo linkInfo = connections.get(corp);
		if( linkInfo == null ) {
			linkInfo = new LinkInfo( 1.0, 0.0 );
			connections.put( corp, linkInfo );
			corp.backconnections.put( this, linkInfo );
		}
		linkInfo.linkTitles.addAll( linknames );
	}
	
	public void addLink( Corp corp, String link ) {
		addLink( corp, link, 1.0, 0.0 );
	}
	
	public void addLink( Corp corp ) {
		addLink( corp, "link" );
	}
	
	public boolean hasLink( Corp corp ) {
		return connections.keySet().contains( corp );
	}
	
	public void removeLink( Corp c ) {
		connections.remove( c );
		c.backconnections.remove( this );
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics2D	g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		if( images.size() > 0 ) {
			BufferedImage	img = images.get( 0 );
			if( img.getWidth()*this.getHeight() > this.getWidth()*img.getHeight() ) {
				int h = (this.getWidth()*img.getHeight())/img.getWidth();
				g.drawImage( img, 0, (this.getHeight()-h)/2, this.getWidth(), h, this );
			} else {
				int w = (this.getHeight()*img.getWidth())/img.getHeight();
				g.drawImage( img, (this.getWidth()-w)/2, 0, w, this.getHeight(), this );
			}
		} else {
			/*if( type.equals("person") ) {
				g.setColor( Color.red );
				g.fillOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
				g.setColor( Color.black );
				g.drawOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
			} else if( type.equals("corp") ) {
				g.setColor( Color.blue );
				g.fillOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
				g.setColor( Color.black );
				g.drawOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
			} else {*/
				g.setColor( color );
				g.fillOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
				g.setColor( Color.black );
				g.drawOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
			//}
		}
		
		if( selected ) {
			g2.setColor( paleColor );
			g2.fillRect( 0, 0, this.getWidth(), this.getHeight() );
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public Point getp() {
		return p;
	}
	
	private Point	p;
	Point	l;
	@Override
	public void mousePressed(MouseEvent e) {
		dragging = (e.getModifiers() == MouseEvent.BUTTON1_MASK || e.isShiftDown()) && e.getClickCount() != 2;
		this.requestFocus();
		
		if( (e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0 ) {
			//Arrays.this.getParent().getComponents()
			
			//this.getParent().add( textfield );
			//textfield.setBounds( this.getX()-size, this.getY()+size, size*3, 25 );
			//textfield.setText( this.name );
			
			prop.currentCorp = this;
			prop.name.setText( this.getName().trim() );
			prop.kt.setText( this.type );
			prop.color = this.color;
			//prop.home.setText( this.home );
			prop.text.setText( this.text );
			this.getParent().add( prop );
			this.getParent().setComponentZOrder( prop, 0 );
			prop.setBounds( this.getX()+this.getWidth()+10, this.getY(), 400, 65 );
		}
		
		p = e.getPoint();
		l = this.getLocation();
		this.selected = true;
		drag = this;
		this.getParent().setComponentZOrder(this, 0);
		this.getParent().repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		
		if( drag != null ) p.translate(drag.getX(), drag.getY());
		Container ct = this.getParent();
		Component c = ct.getComponentAt( p );
		if( c != null && c instanceof Corp && c != drag ) {
			Corp corp = (Corp)c;
			drag.addLink( corp );
		} else if( !dragging && Corp.drag != null && !(c instanceof Corp) ) {
			Corp corp = new Corp( getCreateName(), "unknown", p.x-size/2, p.y-size/2 );
			corp.depz = Corp.drag.depz;
			Connectron.backtrack(p.x-size/2, p.y-size/2, Corp.drag.depz, ct.getParent().getWidth(), ct.getParent().getHeight(), corp);
			this.getParent().add( corp );
			Corp.drag.addLink( corp );
			Corp.drag = null;
		}
		
		dragging = false;
		
		try {
			if( drag != null ) {
				drag.saveRecursive();
				this.hasSaved();
			} else {
				this.saveRecursive();
				this.hasSaved();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		drag = null;
		
		int maxw = this.getParent().getWidth();
		int maxh = this.getParent().getHeight();
		for( Component comp : this.getParent().getComponents() ) {
			if( comp instanceof Corp ) {
				if( comp.getX()+comp.getWidth()+10 > maxw ) maxw = comp.getX()+comp.getWidth()+10;
				if( comp.getY()+comp.getHeight()+10 > maxh ) maxh = comp.getY()+comp.getHeight()+10;
			}
		}
		if( maxw > this.getParent().getWidth() || maxh > this.getParent().getHeight() ) {
			Dimension d = new Dimension( maxw, maxh );
			this.getParent().setPreferredSize( d );
			this.getParent().setSize( d );			
		}
		
		this.p = null;
		this.getParent().repaint();
	}
	
	public void hasSaved() {
		boolean hb = hassaved;
		hassaved = false;
		if( hb ) {
			for( Corp c : this.getLinks() ) {
				c.hasMoved();
			}
		}
	}
	
	public void hasMoved() {
		boolean hb = hasmoved;
		hasmoved = false;
		if( hb ) {
			for( Corp c : this.getLinks() ) {
				c.hasMoved();
			}
		}
	}
	
	boolean hasmoved = false;
	public void moveRelative( int x, int y, boolean recursive ) {
		hasmoved = true;
		
		Connectron.birta = false;
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
		double oldx = this.getx();
		double oldy = this.gety();
		double oldz = this.getz();
		
		Container	ct = this.getParent();
		Connectron.backtrack( this.getX()-3+x, this.getY()-3+y, this.depz, ct.getParent().getWidth(), ct.getParent().getHeight(), this );
		/*this.setx( this.getx()+x );
		this.sety( this.gety()+y );
		this.setz( 0 );*/
		
		//this.x = loc.x+x; //this.getX();
		//this.y = loc.y+y; //this.getY();
		//System.err.println( this.x + "  " + this.y );
		
		if( recursive ) {
			for( Corp c : this.getLinks() ) {
				if( !c.hasmoved ) c.moveRelativeReal( (this.getx()-oldx), (this.gety()-oldy), (this.getz()-oldz), recursive );
			}
		}
		
		Connectron.birta = true;
	}
	
	public void moveRelativeVirt( int x, int y, boolean recursive ) {
		hasmoved = true;
		
		Connectron.birta = false;
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
		double oldx = this.getx();
		double oldy = this.gety();
		double oldz = this.getz();
		
		Container	ct = this.getParent();
		Connectron.backtrack( this.px+x-3, this.py+y-3, this.depz, ct.getParent().getWidth(), ct.getParent().getHeight(), this );
		/*this.setx( this.getx()+x );
		this.sety( this.gety()+y );
		this.setz( 0 );*/
		
		//this.x = loc.x+x; //this.getX();
		//this.y = loc.y+y; //this.getY();
		//System.err.println( this.x + "  " + this.y );
		
		if( recursive ) {
			for( Corp c : this.getLinks() ) {
				if( !c.hasmoved ) c.moveRelativeReal( (this.getx()-oldx), (this.gety()-oldy), (this.getz()-oldz), recursive );
			}
		}
		
		Connectron.birta = true;
	}
	
	public void moveRelativeReal( double x, double y, double z, boolean recursive ) {
		hasmoved = true;
		
		Connectron.birta = false;
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
		//Container	ct = this.getParent();
		//Spilling.backtrack( this.getX()-3+x, this.getY()-3+y, ct.getWidth(), ct.getHeight(), this );
		/*this.setx( this.getx()+x );
		this.sety( this.gety()+y );
		this.setz( 0 );*/
		
		//this.x = loc.x+x; //this.getX();
		//this.y = loc.y+y; //this.getY();
		//System.err.println( this.x + "  " + this.y );
		
		this.setx(this.getx()+x);
		this.sety(this.gety()+y);
		this.setz(this.getz()+z);
		
		if( recursive ) {
			for( Corp c : this.getLinks() ) {
				if( !c.hasmoved ) c.moveRelativeReal( x, y, z, recursive );
			}
		}
		
		Connectron.birta = true;
	}

	boolean dragging = false;
	@Override
	public void mouseDragged(MouseEvent e) {
		//this.getParent().remove( textfield );
		
		if( !e.isShiftDown() ) Connectron.fixed = true;
		
		//Corp corpusmemorus = null;
		prop.currentCorp = null;
		this.getParent().remove( prop );
		if( dragging ) {
			Point	np = e.getPoint();
			
			int dx = np.x - p.x;
			int dy = np.y - p.y;
			System.err.println( dx + "  " + dy );
			System.err.println( p );
			
			if( selectedList.size() == 0 || !selectedList.contains(this) ) {
				moveRelative( dx, dy, true );
				hasMoved();
			} else {				
				moveRelativeVirt( dx, dy, false );
				//System.err.println( "mimem " + this.getX() + "  " + this.getY() + "  " + dx + "  " + dy );
				for( Corp c : selectedList ) {
					//c.dragging = true;
					//Corp.drag = c;
					if( c != this ) {
						//corpusmemorus = c;
						c.moveRelativeVirt( dx, dy, false );
					}
				}
				for( Corp c : selectedList ) {
					c.hasMoved();
				}
			}
		} else {
			p = e.getPoint();
			System.err.println( "hey " + p );
		}
		this.getParent().repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	char last = ' ';
	@Override
	public void keyPressed(KeyEvent e) {
		Container ct = this.getParent();
		int keycode = e.getKeyCode();
		if( keycode == KeyEvent.VK_ENTER ) {
			try {
				this.save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			this.selected = false;
			//this.properties();
		} else if( keycode == KeyEvent.VK_DELETE ) {
			this.delete();
		}
		ct.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {
		Container ct = this.getParent();
		char keychar = e.getKeyChar();
		String name = this.getName();
		if( keychar == '\b' ) {
			if( name.length() > 0 ) this.setName( name.substring(0, name.length()-1) );
		} else if( e.getKeyCode() != KeyEvent.VK_ALT && e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_SHIFT ) {						
			if( name.startsWith("unknown") ) {
				this.setName( ""+keychar );
			} else this.setName( name + keychar );
		}
	}
	
	public void setName( String name ) {
		if( autosave ) deleteSave();
		super.setName(name);
		if( autosave )
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public double getx() {
		return x;
	}
	
	public double gety() {
		return y;
	}
	
	public double getz() {
		return z;
	}
	
	public void setx( double x ) {
		this.x = x;
	}
	
	public void sety( double y ) {
		this.y = y;
	}
	
	public void setz( double z ) {
		this.z = z;
	}
}
