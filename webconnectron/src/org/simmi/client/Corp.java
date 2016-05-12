package org.simmi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;

public class Corp {
	//int						id;
	Connectron				connectron;
	String					home;
	String					kt;
	String					type;
	private double			x,y,z;
	double					vx,vy,vz;
	double					px,py,pz;
	String					text;
	double					depz;
	double					coulomb;
	String					color = "#00FF00";
	String					subcolor = "#FF0000";
	
	List<Image>				images = new ArrayList<Image>();
	List<String>			imageNames = new ArrayList<String>();
	Map<Corp,LinkInfo>		connections = new HashMap<Corp,LinkInfo>();
	Map<Corp,LinkInfo>		backconnections = new HashMap<Corp,LinkInfo>();
	boolean						selected = false;
	boolean						hilighted = false;
	
	static byte[]		buffer = new byte[200000];
	double				size = 32;
	static Set<Corp>	selectedList = new HashSet<Corp>();
	//static Corp			drag;
	static String 		paleColor = "#ffffff77";
	//static JTextField	textfield = new JTextField();
	static Prop					prop;
	static List<Corp>			corpList = new ArrayList<Corp>();
	static Map<String,Corp>		corpMap = new HashMap<String,Corp>();
	static boolean		autosave = false;
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
		String oldcolor = color;
		color = subcolor;
		subcolor = oldcolor;
	}
	
	public void setCoulomb( double coulomb ) {
		this.coulomb = coulomb;
	}
	
	public double getCoulomb() {
		return this.coulomb;
	}
	
	public double getSize() {
		return size;
	}
	
	public void setSize( double size ) {
		this.size = size;
	}
	
	Rectangle	bounds = new Rectangle();
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setBounds( double x, double y, double w, double h ) {
		bounds = new Rectangle( x,y,w,h );
	}
	
	public void setSize( int w, int h ) {
		bounds.width = w;
		bounds.height = h;
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
	
	/*private void writeSave( Writer fw, int[] hashs ) throws IOException {
		fw.write( this.getName().trim() + newline );
		fw.write( this.kt + newline );
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
		for( Image img : images ) {
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
	}*/
	
	public void save() {
		if( autosave ) {
			boolean succ = false;
			/*try {
				PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
				BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
				 
				String name = "spoil_" + this.getName().trim();
				URL url = new URL( bs.getCodeBase().toString() + name );
				String[] names = ps.getNames( bs.getCodeBase() );
				Set<String>	nameset = new HashSet<String>( Arrays.asList(names) );
				if( !nameset.contains(name) ) {
					System.err.println("about to create " + name + " with url " + url.toString() );
					ps.create( url, 1000 );
				}
				FileContents fc = ps.get( url );
				OutputStream os = fc.getOutputStream( false );
				OutputStreamWriter ow = new OutputStreamWriter( os );
				
				int[] hashs = new int[images.size()];
				int i= 0;
				for( Image img : images ) {
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
				
				System.err.println( "saving " + name );
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
			}*/
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
	public void load() {
		boolean succ = false;
		/*try {
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
		}*/
		
		/*if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( !dir.exists() ) dir.mkdirs();
			File save = new File( dir, this.getName().trim() );
			//loadFile( new FileReader( save ), new File( save.getParentFile(), "images" ).toURI().toURL().toString() );
		}*/
	}
	
	public double distance( Corp other ) {
		double xval = x-other.x;
		double yval = y-other.y;
		double zval = z-other.z;
		return Math.sqrt( xval*xval + yval*yval + zval*zval );
	}
	
	private void deleteSave() {
		boolean succ = false;
		/*try {
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
		}*/
		
		/*if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( !dir.exists() ) dir.mkdirs();
			File save = new File( dir, this.getName().trim() );
			save.delete();
		}*/
	}
	
	public void delete() {
		deleteSave();
		
		Connectron ct = this.getParent();
		
		ct.components.remove( this );
		this.setParent( null );
		
		//ct.remove( this );
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
	
	/*public void loadFile( Reader fr, String baseurl ) {
		//FileReader 	fr = new FileReader( save );
		//File		mdir = new File( save.getParentFile(), "images" );
		
		int r = fr.read(cbuf);
		if( r > 0 ) {
			String str = new String( cbuf, 0, r );
			
			String[] split = str.split( newline+newline );
			String[] nkt = split[0].split( newline );
			
			if( nkt.length == 4 ) {
				this.name = nkt[0];
				this.kt = nkt[1];
				this.type = nkt[2];
				String[] loc = nkt[3].split(",");
				if( loc.length == 3 ) {
					this.x = Double.parseDouble(loc[0]);
					this.y = Double.parseDouble(loc[1]);
					this.z = Double.parseDouble(loc[2]);
				}
				
				this.setBounds((int)x, (int)y, size, size);
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
				/*try {
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
				}*
				
				if( !succ ) {
					for( String val : img ) {
						//File f = new File( mdir, val );
						if( val.length() > 0 ) {
							String urlstr = baseurl+val;
							//URL url = new URL( urlstr );
							
							images.add( new Image( urlstr ) ); //ImageIO.read( url ) );
							imageNames.add( urlstr );
						}
					}
				}
			}
			
			this.text = "";
			for( int i = 3; i < split.length; i++ ) {
				this.text += split[i] + newline + newline;
			}
			//if( this.text.length() > 0 ) this.setToolTipText( this.text );
		}
	}*/
	
	public Corp() {
		int i = 0;
		String str = "unkown";
		String val = str+i;
		while( corpMap.containsKey(val) ) {
			i++;
			val = str+i;
		}
		
		this.name = val;
		this.kt = "00";
		this.type = "unkown";
		this.x = 20;
		this.y = 20;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		this.coulomb = 1000.0;
		
		init();
	}
	
	public Corp( String name ) {
		this.name = name;
		this.kt = "00";
		this.type = "unkown";
		this.x = 20;
		this.y = 20;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		this.coulomb = 1000.0;
		
		init();
	}
	
	public Corp( String name, String type, double x, double y ) {
		super();
		
		this.name = name;
		this.kt = "00";
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		this.coulomb = 1000.0;
		
		this.setBounds((int)x, (int)y, size, size);
		init();
	}
	
	public void properties() {
		Corp.prop.currentCorp = Corp.this;
		Corp.prop.name.setText( Corp.this.getName().trim() );
		Corp.prop.kt.setText( Corp.this.type );
		//Corp.prop.color = Corp.this.color;
		//Corp.prop.home.setText( Corp.this.home );
		Corp.prop.text.setText( Corp.this.text );
		//Corp.this.getParent().add( Corp.prop );
		//Corp.this.getParent().setComponentZOrder( Corp.prop, 0 );
		//Corp.prop.setBounds( Corp.this.getX()+Corp.this.getWidth()+10, Corp.this.getY(), 400, 300 );
	}
	
	public void setSelected( boolean selected ) {
		this.selected = selected;
		selectedList.add( this );
	}
	
	public boolean isSelected() {
		return this.selected;
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
		
		MenuBar	popup = new MenuBar();
		popup.addItem( "Select subgraph", new Command() {
			@Override
			public void execute() {
				deselectAll();
				Corp.this.selected = false;
				subselect( Corp.this );
				Connectron ct = Corp.this.getParent();
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
		popup.addItem( "Properties", new Command() {
			@Override
			public void execute() {
				properties();
			}
		});
		
		/*TransferHandler th = new TransferHandler() {
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
		imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));*/
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
			linkInfo = new LinkInfo( 0.001, 0.0 );
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
	
	//int xs;
	public double getX() {
		return bounds.x;
	}
	
	//int ys;
	public double getY() {
		return bounds.y;
	}
	
	public double getWidth() {
		return bounds.width;
	}
	
	public double getHeight() {
		return bounds.height;
	}
	
	public void paintComponent( Context2d g ) {	
		if( images.size() > 0 ) {
			Image	img = images.get( 0 );
			if( img.getWidth()*this.getHeight() > this.getWidth()*img.getHeight() ) {
				double h = (this.getWidth()*img.getHeight())/img.getWidth();
				g.drawImage( ImageElement.as( img.getElement() ), 0, (this.getHeight()-h)/2, this.getWidth(), h );
			} else {
				double w = (this.getHeight()*img.getWidth())/img.getHeight();
				g.drawImage( ImageElement.as( img.getElement() ), (this.getWidth()-w)/2, 0, w, this.getHeight() );
			}
		} else {
			if( this.getWidth() > 0 && this.getHeight() > 0 ) {
				g.beginPath();
				g.setFillStyle( color );
				g.setStrokeStyle( "#000000" );
				g.arc( this.getWidth()/2, this.getHeight()/2, this.getWidth()/2, 0, 2.0*Math.PI );
				g.closePath();
				g.fill();
				g.stroke();
			}
		}
		
		if( selected ) {
			if( this.getWidth() > 0 && this.getHeight() > 0 ) {
				g.setFillStyle( paleColor );
				g.setStrokeStyle( "#000000" );
				g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
				g.strokeRect( 0, 0, this.getWidth(), this.getHeight() );
			}
		}
	}

	int		pxs;
	int		pys;
	double	lxs;
	double	lys;
	public void mousePressed( MouseEvent me, int x, int y, boolean isShiftKeyDown, boolean doubleClick) {
		dragging = (me.getNativeButton() == NativeEvent.BUTTON_LEFT || isShiftKeyDown) && !doubleClick;		
		if( me.getNativeButton() == NativeEvent.BUTTON_RIGHT ) {
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
			//this.getParent().setComponentZOrder( prop, 0 );
			prop.setBounds( (int)(this.getX()+this.getWidth()+10), (int)this.getY(), 400, 65 );
		}
		
		pxs = x;
		pys = y;
		lxs = this.getX();
		lys = this.getY();
		this.selected = true;
		//drag = this;
		//this.getParent().setComponentZOrder(this, 0);
		this.getParent().repaint();
	}

	public void mouseReleased( MouseEvent e, int xx, int yy, boolean isShiftKeyDown, boolean doubleClick, Corp drag ) {		
		/*if( drag != null ) {
			pxs += drag.getX();
			pys += drag.getY();
		}*/
		Connectron ct = this.getParent();
		Corp c = ct.getComponentAt( xx, yy );
		if( c != null && c != drag && drag != null ) {
			drag.addLink( c );
		} else if( !dragging && drag != null /*&& !(c instanceof Corp)*/ ) {
			Corp corp = new Corp( getCreateName(), "unknown", xx-size/2, yy-size/2 );
			corp.depz = drag.depz;
			ct.backtrack(xx-size/2, yy-size/2, drag.depz, ct.getParentWidth(), ct.getParentHeight(), corp);
			this.getParent().add( corp );
			drag.addLink( corp );
			drag = null;
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
		for( Corp comp : this.getParent().getComponents() ) {
			if( comp.getX()+comp.getWidth()+10 > maxw ) maxw = (int)(comp.getX()+comp.getWidth()+10);
			if( comp.getY()+comp.getHeight()+10 > maxh ) maxh = (int)(comp.getY()+comp.getHeight()+10);
		}
		if( maxw > this.getParent().getWidth() || maxh > this.getParent().getHeight() ) {
			this.getParent().setPixelSize( maxw, maxh );
		}
		
		this.pxs = -1;
		this.pys = -1;
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
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
		double oldx = this.getx();
		double oldy = this.gety();
		double oldz = this.getz();
		
		Connectron	ct = this.getParent();
		ct.birta = false;
		ct.backtrack( this.getX()+x, this.getY()+y, this.depz, ct.getParentWidth(), ct.getParentHeight(), this );
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
		
		ct.birta = true;
	}
	
	public void moveRelativeVirt( int x, int y, boolean recursive ) {
		hasmoved = true;
		
		Connectron	ct = this.getParent();
		ct.birta = false;
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
		double oldx = this.getx();
		double oldy = this.gety();
		double oldz = this.getz();
		
		ct.backtrack( this.px+x, this.py+y, this.depz, ct.getParentWidth(), ct.getParentHeight(), this );
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
		
		ct.birta = true;
	}
	
	public void moveRelativeReal( double x, double y, double z, boolean recursive ) {
		hasmoved = true;
		
		Connectron	ct = this.getParent();
		ct.birta = false;
		
		//Point	loc = this.getLocation();
		//this.setLocation(loc.x+x, loc.y+y);
		
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
		
		ct.birta = true;
	}

	boolean dragging = false;
	public void mouseDragged(MouseMoveEvent e, int npx, int npy, boolean isshift ) {		
		Connectron	ct = this.getParent();
		if( !isshift ) ct.fixed = true;
		
		prop.currentCorp = null;
		this.getParent().remove( prop );
		if( dragging ) {
			int dx = npx - pxs;
			int dy = npy - pys;
			
			//console( dx + "  " + dy );
			
			if( selectedList.size() == 0 || !selectedList.contains(this) ) {
				moveRelative( dx, dy, true );
				hasMoved();
			} else {
				moveRelativeVirt( dx, dy, false );
				for( Corp c : selectedList ) {
					if( c != this ) {
						c.moveRelativeVirt( dx, dy, false );
					}
				}
				for( Corp c : selectedList ) {
					c.hasMoved();
				}
			}
		}
			//console( npx + "  " + npy );
		pxs = npx;
		pys = npy;
		
		this.getParent().repaint();
	}
	
	public native void console( String val ) /*-{
		$wnd.console.log( val );
	}-*/;
	
	public Connectron getParent() {
		return connectron;
	}
	
	public void setParent( Connectron parent ) {
		connectron = parent;
		
		/*if( connectron != null ) {
			Canvas canvas = connectron.getCanvas();
			canvas.addMouseDownHandler( this );
			canvas.addMouseUpHandler( this );
			canvas.addMouseMoveHandler( this );
			canvas.addKeyDownHandler( this );
			canvas.addKeyUpHandler( this );
		}*/
	}

	char last = ' ';
	public void keyPressed(KeyDownEvent e) {
		Connectron ct = this.getParent();
		int keycode = e.getNativeKeyCode();
		if( keycode == KeyCodes.KEY_ENTER ) {
			this.save();
			this.selected = false;
			//this.properties();
		} else if( keycode == KeyCodes.KEY_DELETE ) {
			this.delete();
		}
		ct.repaint();
	}

	public void keyTyped(KeyPressEvent e) {
		char keychar = e.getCharCode();
		int keycode = e.getNativeEvent().getKeyCode();
		String name = this.getName();
		if( keychar == '\b' ) {
			if( name.length() > 0 ) this.setName( name.substring(0, name.length()-1) );
		} else if( keycode != KeyCodes.KEY_ALT && keycode != KeyCodes.KEY_CTRL && keycode != KeyCodes.KEY_SHIFT ) {						
			if( name.startsWith("unknown") ) {
				this.setName( ""+keychar );
			} else this.setName( name + keychar );
		}
	}
	
	String name;
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		if( autosave ) deleteSave();
		this.name = name;
		if( autosave )
			save();
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

	/*@Override
	public void onKeyUp(KeyUpEvent event) {
		
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		keyPressed( event );
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if( mousedown ) {
			mouseDragged( event );
		}
	}

	boolean	mousedown = false;
	@Override
	public void onMouseDown(MouseDownEvent event) {
		mousedown = true;
		mousePressed( event );
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mousedown = false;
		mouseReleased( event );
	}*/
}
