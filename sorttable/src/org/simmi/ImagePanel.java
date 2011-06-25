package org.simmi;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

public class ImagePanel extends JComponent implements DragGestureListener, DragSourceListener, Transferable, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage	img;
	String	imgstr = "http://members.lycos.nl/gitte1965/photoalbum/images/uni57.jpg";
	Set<String>	imageNames;
	Map<String,BufferedImage>	imageCache;
	Map<String,String>	imageNameCache;
	JTable				leftTable;
	int					orientation;
	JProgressBar		progressbar;
	String				imgUrl = null;
	String				lang;
	SortTable			applet;
	boolean				restricted = true;
	
	JEditorPane				imged;
	
	DragSource 				dragSource;
	Transferable			transferable;
	//Image			currentImage;
	/*class SimImageView extends ImageView {
		public SimImageView(Element elem) {
			super(elem);
		}
		
	}*/
	
	class SimViewFactory extends HTMLEditorKit.HTMLFactory {
		public View create( Element e ) {
			View ret = super.create(e);
			
			if( ret instanceof ImageView ) {
				//img = ((ImageView)ret).getImage();
			}
			
			return ret;
		}
	};
	
	class EdKit extends HTMLEditorKit {
		SimViewFactory svf = new SimViewFactory();
		
		public ViewFactory getViewFactory() {
			return svf;
		}
	};
	
	public ImagePanel( SortTable applet, final JTable leftTable, String lang ) {
		super();
		this.applet = applet;
		this.addMouseListener(this);
		/*this.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int r = leftTable.getSelectedRow();
				//int rr = leftTable.convertRowIndexToModel(r);
				if( r >= 0 && r < leftTable.getRowCount() ) {
					Object obj = leftTable.getValueAt(r, 0);
					if( obj != null ) {
						String s = obj.toString();
						//System.err.println("hey " + s );
						
						if( !imageNameCache.containsKey(s) ) runThread( s );
					}
				}
			}
		});*/
		this.leftTable = leftTable;
		
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		
		imged = new JEditorPane();
		imged.setEditable(false);
		//imged.setEditorKitForContentType("text/html", new EdKit() );
			/*try {
				imged.setPage("http://localhost/canvas.html");
				//HTMLEditorKit htmled = (HTMLEditorKit)imged.getEditorKitForContentType("text/html");
				EditorKit edkit = imged.getEditorKit();
				if( edkit instanceof HTMLEditorKit ) {
					HTMLEditorKit htmled = (HTMLEditorKit)edkit;
					htmled.set
					//StyleSheet sheet = htmled.getStyleSheet();
					//sheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
					//sheet.addRule("imed {width:100%}");
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}*/
			//imged.setContentType("text/html");
			//imged.setText("<html><body><img src=\""+imgstr+"\"</img></body></html>");
		
		this.lang = lang;
		imageNames = new HashSet<String>();
		imageCache = new HashMap<String,BufferedImage>();
		imageNameCache = new HashMap<String,String>();
		 
		InputStream inputStream = this.getClass().getResourceAsStream("/myndir.txt");
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
			String line;
			line = br.readLine();
			while( line != null ) {
				imageNames.add( line );
				line = br.readLine();
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//this.setDropTarget(dt)
		//final DataFlavor df = DataFlavor.imageFlavor;//new DataFlavor("text/plain;charset=utf-8");
		try { 
			//final DataFlavor df = new DataFlavor ("application/x-java-url; class=java.net.URL"); 
			//final DataFlavor df =	new DataFlavor ("application/x-chrome-named-url");
			final DataFlavor df = new DataFlavor("application/x-moz-nativeimage");
			//final DataFlavor df = DataFlavor.imageFlavor;
			transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "jpeg", baos);
					return new ByteArrayInputStream( baos.toByteArray() );
					//return new ByteArrayInputStream( imgstr.getBytes() );
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
		} catch (ClassNotFoundException cnfe) { 
			cnfe.printStackTrace( );
		}
		
		TransferHandler th = new TransferHandler() {
			private static final long serialVersionUID = 1L;
			
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			public boolean canImport(TransferHandler.TransferSupport support) {
				DataFlavor[] daf = support.getDataFlavors();
				return false;
			}

			protected Transferable createTransferable(JComponent c) {
				return transferable;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				return true;
			}
		};		
		this.setTransferHandler( th );
		
		progressbar = new JProgressBar();
		progressbar.setVisible( false );
		progressbar.setIndeterminate( true );
		progressbar.setString(lang.equals("IS") ? "Sæki mynd" : "Fetch image");
		progressbar.setValue( 0 );
		progressbar.setStringPainted( true );
		this.add( progressbar );
		this.add( imged );
	}
	
	public void setURL( String url ) throws IOException {
		try {
			imged.setPage( url );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		this.repaint();
	}
	
	public void setImage( BufferedImage image ) {
		this.img = image;
	}
	
	public void setImage( String image ) {
		this.img = null;
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		super.setBounds(x, y, w, h);
		
		Component[] cc = this.getComponents();
		Component c = null;
		for( Component tc : cc ) {
			if( tc instanceof JButton ) {
				c = tc;
				break;
			}
		}
		
		if( c != null ) {
			if( orientation == 0 ) {
				c.setLocation(9, 9);
			} else if( orientation == 1 ) {
				c.setLocation(this.getWidth()-41, 9);
			} else if( orientation == 2 ) {
				c.setLocation(this.getWidth()-41, this.getHeight()-41);
			} else {
				c.setLocation(9, this.getHeight()-41);
			}
		}
		progressbar.setBounds(this.getWidth()/2-75, this.getHeight()/2-10, 150, 20);
		imged.setBounds(0,0,10,10);
	}
	
	public void drawString( Graphics g, String str, int h ) {
		String[] split = str.split("\n");
		for( String s : split ) {
			int strw = g.getFontMetrics().stringWidth( s );
			g.drawString( s, (this.getWidth()-strw)/2, this.getHeight()/2+g.getFontMetrics().getHeight()*(h-split.length/2) );
			h++;
		}
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		
		Graphics2D	g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		if( img != null ) {
			int iw = img.getWidth(this);
			int ih = img.getHeight(this);
			int w = this.getWidth();
			int h = this.getHeight();
			
			if( w*ih > iw*h ) {
				int rw = (iw*h)/ih;
				g.drawImage(img, (w-rw)/2, 0, rw, this.getHeight(), this );
			} else {
				int rh = (ih*w)/iw;
				g.drawImage(img, 0, (h-rh)/2, this.getWidth(), rh, this );
			}
		} else if( !progressbar.isVisible() ) {
			boolean b = false;
			int r = leftTable.getSelectedRow();
			if( r >= 0 && r < leftTable.getRowCount() ) {
				Object obj = leftTable.getValueAt(r, 0);
				if( obj != null ) {
					String s = obj.toString();
					b = imageNameCache.containsKey( s );
				}
			}
			
			g.setColor( Color.lightGray );
			if( b ) {
				String str = lang.equals("IS") ? "Engin mynd" : "No image";
				drawString( g, str, 0 );
			} else {
				String str = lang.equals("IS") ? "Engin mynd\nSmelltu hér til að sækja mynd á google" : "No image\nClick here to fetch from google";
				drawString( g, str, 0 );
			}
		} else if( imgUrl != null ) {
			drawString( g, imgUrl, -1 );
		}
	}
	
	public BufferedImage getImage( URL url ) throws IOException {
		boolean	safeUrl = false;
		try {
			SecurityManager secm = System.getSecurityManager();
			if( secm != null ) {
				secm.checkConnect(url.getHost(), url.getPort());
				safeUrl = true;
			} else safeUrl = true;
		} catch( AccessControlException e ) {}
		
		if( !restricted || safeUrl ) {
			return ImageIO.read( url ); //applet.getImage( url );
		} else {
			if( applet.applet == null ) {
				JSUtil.call( applet, "emmi", new Object[] {url.toString()} );
				applet.applet = "simmi";
			} else {
				Enumeration<Applet> appen = applet.getAppletContext().getApplets();
				while( appen.hasMoreElements() ) {
					Applet ap = appen.nextElement();
					try {
						Method m = ap.getClass().getMethod( "runimage", URL.class );
						return (BufferedImage)m.invoke( ap, url );
					} catch( Exception e ) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	public void getImage( String val, int index ) throws IOException {
		//path = "http://test.matis.is/isgem/myndir/"+val;//URLEncoder.encode(iName,"UTF-8");		
		BufferedImage	image = null;
		if( imageCache.containsKey(val) ) {
			image = imageCache.get(val);
		} else {
			URL url = new URL( val.replace(" ", "%20") );
			image = getImage( url );
			//image = ImageIO.read(url);
			imageCache.put(val, image);
		}
		//imageNameCache.put(oname, val);
		
		int sr = leftTable.getSelectedRow();
		if( index == sr ) {
			progressbar.setVisible( false );
			img = image;
			ImagePanel.this.repaint();
		} else {
			if( sr >= 0 && sr < leftTable.getRowCount() ) {
				Object obj = leftTable.getValueAt(sr, 0);
				if( obj != null ) {
					String s = (String)obj;
					if( val.equals(imageNameCache.get(s)) ) {
						progressbar.setVisible( false );
						img = image;
						ImagePanel.this.repaint();
					}
				}
			}
		}
	}
	
	Set<String>	vals = new HashSet<String>();
	Thread t = null;
	public void threadRun( final String val, final int index ) {
		if( !vals.contains( val ) ) {
			vals.add( val );
			
			imgUrl = val;
			
			progressbar.setVisible( true );
			ImagePanel.this.repaint();
			
			t = new Thread() {
				public void run() {					
					try {
						getImage( val, index );
					} catch (IOException e) {
						e.printStackTrace();
					}
					vals.remove( val );
					if( vals.size() == 0 ) progressbar.setVisible( false );
					
					if( !imageCache.containsKey(val) ) imageCache.put(val, null);
				}
			};
			t.start();
		} else {
			imgUrl = val;
			progressbar.setVisible( true );
		}
	}

	public void tryName( String oName ) throws UnsupportedEncodingException {
		if( imageNameCache.containsKey(oName) ) {
			String imgUrl = imageNameCache.get(oName);
			if( imageCache.containsKey(imgUrl) ) {
				BufferedImage image = imageCache.get(imgUrl);

				progressbar.setVisible( false );
				this.img = image;
			} else {
				this.imgUrl = imgUrl;
				progressbar.setVisible( true );
				img = null;
			}
		} else if( lang.equals("IS") ) {			
			/*boolean b = true;
			try {
				URL url = new URL( oName );
			} catch( Exception e ) {
				b = false;
			}
			
			if( b ) {
				threadRun(oName,oName,leftTable.getSelectedRow());
			} else {*/
				//path = "http://test.matis.is/isgem/myndir/"+oName;
				
				String lName = oName.toLowerCase();
				String name = lName.replace('á', 'a');
				name = name.replace('ó', 'o');
				name = name.replace('ú', 'u');
				name = name.replace('ý', 'y');
				name = name.replace('í', 'i');
				name = name.replace('é', 'e');
				name = name.replace('ð', 'd');
				name = name.replace('þ', 't');
				name = name.replace("æ", "ae");
				
				List<String> oSpl = Arrays.asList( lName.split("[, ]+") );
				List<String> nSpl = Arrays.asList( name.split("[, ]+") );
				
				Set<String>	ign = new HashSet<String>();
				ign.add("hrar");
				ign.add("sodin");
				ign.add("sodinn");
				ign.add("jpg");
				ign.add("sosa");
				ign.add("-");
				
				int max = 0;
				String val = null;
				for( String iName : imageNames ) {
					String[] spl = iName.toLowerCase().split("[\\._ 0123456789]+");
					
					int count = 0;
					for( String iStr : spl ) {
						if( !ign.contains(iStr) && (oSpl.contains(iStr) || nSpl.contains(iStr)) ) count++;
					}
					
					if( count > max ) {
						max = count;
						val = iName;
					}
				}
				
				if( val != null ) {
					String path = "http://test.matis.is/isgem/myndir/"+URLEncoder.encode( val, "UTF-8" );;
					if( imageCache.containsKey(path) ) {
						img = imageCache.get(path);
						imageNameCache.put(oName, path);
						progressbar.setVisible( false );
					} else {
						imageNameCache.put(oName, path);
						threadRun(path,leftTable.getSelectedRow());
					}
				}
			//}
		}
	}
	
	private void runImageSearchThread( final String str, final int imindex ) {
		Thread t = new Thread() {
			public void run() {
				String urlstr = null;
				try {
					//url = new URL("http://localhost:5001/images?hl=en&q="+URLEncoder.encode(str, "UTF-8") );
					//url = new URL("http://search.live.com/images/results.aspx?q="+str);
					//String result = PoiFactory.urlFetch( str );
					
					String result = null;
					
					if( restricted && applet != null ) {
						try {
							JSUtil.call( applet, "imsearch2", new Object[] {str} );
						} catch( Exception e ) {
							result = ImageFactory.urlFetch( str );
						}
						
						/*if( applet.applet == null ) {
							JSObject win = JSObject.getWindow(applet);
							win.call( "emmi", new Object[] {str} );
							applet.applet = "simmi";
						} else {
							Enumeration<Applet> appen = applet.getAppletContext().getApplets();
							while( appen.hasMoreElements() ) {
								Applet ap = appen.nextElement();
								
								try {
									Method m = ap.getClass().getMethod( "runfetch", String.class );
									result = (String)m.invoke( ap, str );
									break;
								} catch( Exception e ) {
									e.printStackTrace();
								}
							}
						}*/
					} else {
						result = ImageFactory.urlFetch( str );
					}
					
					if( result != null ) {
						urlstr = ImageFactory.getImageURL( result );
							
						vals.add( urlstr );
						imgUrl = urlstr;
						ImagePanel.this.repaint();
						
						imageNameCache.put( str, urlstr );
						getImage( urlstr, imindex );
						/*url = new URL( urlstr );
						connection = url.openConnection();
						stream = connection.getInputStream();
						Image image = ImageIO.read(stream);
						imageCache.put(urlstr,image);
						imageNameCache.put( str, urlstr );
						
						if( imindex == leftTable.getSelectedRow() ) {
							progressbar.setVisible( false );
							img = image;
							ImagePanel.this.repaint();
						}*/
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				if( urlstr != null ) {
					vals.remove( urlstr );
					if( !imageCache.containsKey( urlstr ) ) {
						imageCache.put( urlstr, null );
					}
				}
				if( vals.size() == 0 ) {
					progressbar.setVisible( false );
				}
				ImagePanel.this.repaint();
			}
		};
		t.start();
	}
	
	public void runThread( final String str ) {
		if( imageNameCache.containsKey(str) ) {
			//String urlstr = imageNameCache.get(str);
			img = imageCache.get( str );
			ImagePanel.this.repaint();
		} else {
			final int imindex = leftTable.getSelectedRow();
			
			runImageSearchThread( str, imindex );
			/*Enumeration<Applet> appen = applet.getAppletContext().getApplets();
			while( appen.hasMoreElements() ) {
				Applet ap = appen.nextElement();
				
				System.err.println( "try " + ap );
				try {
					Method m = ap.getClass().getMethod( "runImageSeachThread", JComponent.class, Set.class, Map.class, Map.class, String.class, int.class );
					//m.invoke( ap, table, topTable, leftTable );
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}*/
			//PoiFactory.runImageSearchThread( ImagePanel.this, str );
			
			imgUrl = null;
			progressbar.setVisible( true );
			this.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if( img != null ) {
			this.requestFocus();
			this.getTransferHandler().exportAsDrag(this, e, TransferHandler.COPY_OR_MOVE);
		} else {
			int r = leftTable.getSelectedRow();
			//int rr = leftTable.convertRowIndexToModel(r);
			if( r >= 0 && r < leftTable.getRowCount() ) {
				Object obj = leftTable.getValueAt(r, 0);
				if( obj != null ) {
					String s = obj.toString();
					//System.err.println("hey " + s );
					
					if( !imageNameCache.containsKey(s) ) runThread( s );
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		dragSource.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
	}
}
