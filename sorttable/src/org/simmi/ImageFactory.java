package org.simmi;

import java.applet.Applet;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JTable;

public class ImageFactory extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTable	table, topTable, leftTable;
	
	public String getAppletInfo() {
		return "PoiFactory";
	}
	
	public void init() {
		JLabel label = new JLabel("simmi");
		this.add( label );
		
		String par = this.getParameter("function");
		
		System.err.println("parameter function "+par);
		if( par.equals("export") ) {
			Enumeration<Applet> appen = this.getAppletContext().getApplets();
			while( appen.hasMoreElements() ) {
				Applet ap = appen.nextElement();
				
				System.err.println( "try " + ap );
				try {
					Method m = ap.getClass().getMethod("getThreeTables");
					JTable[] all = (JTable[])m.invoke( ap );
					PoiFactory.export( all[0], all[1], all[2], ImageFactory.this );
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		} else if( par.contains("http://") ) {
			Enumeration<Applet> appen = this.getAppletContext().getApplets();
			while( appen.hasMoreElements() ) {
				Applet ap = appen.nextElement();
				
				try {
					Method m = ap.getClass().getMethod("setImage", Image.class );
					Image img = ImageIO.read( new URL(par) );
					JTable[] all = (JTable[])m.invoke( ap, img );
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		} else {
			Enumeration<Applet> appen = this.getAppletContext().getApplets();
			while( appen.hasMoreElements() ) {
				Applet ap = appen.nextElement();
				
				try {
					Method m = ap.getClass().getMethod("setImage", Image.class );
					String val = urlFetch( par );
					String url = getImageURL( val );
					if( url != null ) {
						Image img = ImageIO.read( new URL(val) );
						m.invoke( ap, img );
					}
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		
		/*Applet applet = this.getAppletContext().getApplet("food");
		
		Enumeration<Applet> 	eappl = this.getAppletContext().getApplets();
		while( eappl.hasMoreElements() ) {
			System.err.println( "eappl "+eappl.nextElement().getAppletInfo() );
			
		}
		if( applet != null ) {
			System.err.println("found");
			try {
				Field f = applet.getClass().getField("applet");
				f.set(applet, this);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		
		new Thread() {
			public void run() {
				bulli();
			}
			
			public synchronized void bulli() {
				while( true ) {
					try {
						wait();
						dummy();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();*/
	}
	
	public void runpriv( final JTable table, final JTable topTable, final JTable leftTable ) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				try {
					PoiFactory.export( table, topTable, leftTable, ImageFactory.this );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	public synchronized void hey( JTable table, JTable topTable, JTable leftTable ) {
		this.table = table;
		this.topTable = topTable;
		this.leftTable = leftTable;
		notify();
	}
	
	final static ByteBuffer	ba = ByteBuffer.allocate(1000000);	
	public Image runimage( final URL url ) {
		return (Image)AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				try {
					return ImageIO.read(url);
				} catch (IOException e) {
					e.printStackTrace();
				} //return getImage( url );
				return null;
			}
		});
	}
	
	public String runfetch( final String str ) {
		return (String)AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				try {
					return urlFetch( str );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	final static String startTag = "imgurl";
	public static String getImageURL( String result ) {
		String urlstr = null;
		
		int index = result.indexOf( startTag );
		int val = result.indexOf("http:", index); //index+startTag.length();
		
		int stop = -1;
		if( val >= 0 ) {
			stop = result.indexOf( "\\x26", val );
			if( stop == -1 || stop-val > 200 ) {
				stop = result.indexOf( '&', val );
			}
			
			urlstr = result.substring(val, val+20);
			if( stop != -1 ) {
				urlstr = result.substring( val, stop );
			}
		} else {
			System.err.println( result );
		}
		
		//System.err.println( urlstr );
		
		/*while( index > 0 && (result.charAt(val) != 'h' || !(urlstr.endsWith("jpg") || urlstr.endsWith("png") || urlstr.endsWith("gif"))) ) {
			index = result.indexOf( startTag, val );
			val = index+startTag.length();
			stop = result.indexOf( '&', val );
			urlstr = result.substring( val, stop );
		}*/
		
		if( stop > 0 ) {
			urlstr = urlstr.replace("%20", " ").replace("%2520", " ");
			
			return urlstr;
		}
		
		return null;
	}
	
	public static String urlFetch( String str ) throws IOException {
		String vstr = str.replace(",", "");
		vstr = vstr.replace(' ', '+');
		vstr = URLEncoder.encode(vstr, "UTF-8");
		URL url = new URL("http://images.google.com/images?hl=en&biw=1920&bih=1043&gbv=2&tbs=isch:1&sa=1&q="+vstr ); //+"&btnG=Search+Images&gbv=2" ); //&btnG=Search+Images" );//hl=en&q=Orange");//+str);
		System.err.println( "searching for " + url.toString() );
		URLConnection connection = null;
		connection = url.openConnection();
		//Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress("proxy.decode.is",8080) );
		//connection = url.openConnection( proxy );
		//connection.setDoOutput( true );
		if( connection instanceof HttpURLConnection ) {
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			httpConnection.setRequestProperty("Host", "images.google.com" );
			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.6) Gecko/2009020518 Ubuntu/9.04 (jaunty) Firefox/3.0.6" );
			httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8" );
			httpConnection.setRequestProperty("Accept-Language", "en-us,en;q=0.5" );
			httpConnection.setRequestProperty("Accept-Encoding", "gzip,deflate" );
			httpConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" );
			httpConnection.setRequestProperty("Keep-Alive", "300" );
			httpConnection.setRequestProperty("Connection", "keep-alive" );
		}
		InputStream stream = connection.getInputStream();
		stream = new GZIPInputStream( stream );
		
		int total = 0;
		int read = stream.read(ba.array(), total, ba.limit()-total );
		total = 0;
		while( read > 0 ) {
			total += read;
			read = stream.read( ba.array(), total, ba.limit()-total );
		}
		stream.close();
		
		return new String( ba.array(), 0, total);
	}
}
