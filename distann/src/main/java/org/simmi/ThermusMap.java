package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.ByteArrayOutputStream;
import netscape.javascript.JSObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class ThermusMap extends JApplet {
	public void paint( Graphics g ) {
		super.paint(g);
		
		g.drawString("", 10, 10);
	}
	
	class LatLng {
		public LatLng( double lat, double lng ) {
			this.lat = lat;
			this.lng = lng;
		}
		
		double	lat;
		double	lng;
	}
	
	public void load( Map<String,LatLng> m, int mintemp, int maxtemp, int phmin, int phmax, String selitem ) {
		InputStream is = this.getClass().getResourceAsStream("/locs.txt");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		Random r = new Random();
		JSObject jo = JSObject.getWindow(this);
		jo.call("clearMarkers", new Object[] {});
		
		try {
			BufferedImage bi = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)bi.getGraphics();
			g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			g.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE );
			//g.setRenderingHint( RenderingHints.KEY_ );
			
			int[] xx = new int[] { 0, 0, 0 };
			int[] yy = new int[] { 0, 0, 0 };
			
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("[\t]+");
				
				double ph = 7.0;
				if( split[3].contains("-") ) {
					String[] subsplit = split[3].trim().split("-");
					ph = ( Double.parseDouble( subsplit[0] ) + Double.parseDouble( subsplit[1] ) ) / 2.0;
				} else ph = Double.parseDouble(split[3].trim());
				
				int temp = 0;
				if( split[2].contains("-") ) {
					String[] subsplit = split[2].split("-");
					temp = ( Integer.parseInt( subsplit[0].trim() ) + Integer.parseInt( subsplit[1].trim() ) ) / 2;
				} else temp = Integer.parseInt(split[2].trim());
				
				boolean selname = selitem.equals("All");
				String[] specsplit = split[5].split("T\\. ");
				
				for( int i = 1; i < specsplit.length && !selname; i++ ) {
					String ss = specsplit[i];
					int k = ss.indexOf(' ');
					
					String ch;
					if( k > 0 ) ch = "T."+ss.substring(0, k);
					else ch = "T."+ss;
					
					//System.err.println("hoho" + ss);
					
					if( selitem.contains(ch) ) {
						selname = true;
					}
				}
				
				//System.err.println( selname + "  " + selitem );
				
				if( temp >= mintemp && temp <= maxtemp && ph >= phmin && ph <= phmax && selname ) {
					String content = "<div id=\"content\">"+
				    "<div id=\"siteNotice\">"+
				    "</div>"+
				    "<div id=\"bodyContent\">"+
				    "<b>"+ split[0] + "\t" + split[1] + "</b>" +
				    "<br>Temperature: "+split[2]+
				    "<br>Ph: "+split[3]+
				    "<br>Sampletype: "+split[4]+
				    "<br>Strains: "+split[5]+
				    "</div>";
					
					g.setBackground( new Color(0,0,0,0) );
					g.clearRect(0, 0, bi.getWidth()-1, bi.getHeight()-1);
					//g.setColor( new Color(0,0,0,0) );
					//g.fillRect(0, 0, bi.getWidth()-1, bi.getHeight()-1);
					
					g.setColor( new Color( 200+(int)(15*(ph-7.0)), 200-(int)(15*(ph-7.0)), 0 ) );
					/*xx[0] = 0;
					xx[1] = 7;
					xx[2] = 7;
					yy[0] = 0;
					yy[1] = 0;
					yy[2] = 15;
					g.fillPolygon(xx, yy, xx.length);
					g.drawPolygon(xx, yy, xx.length);*/
					g.fillArc(-8, 0, 32, 32, 60, 30);
					
					g.setColor( new Color( 255, (int)((100-temp)*5.0), 0 ) );
					/*xx[0] = 8;
					xx[1] = 15;
					xx[2] = 8;
					yy[0] = 0;
					yy[1] = 0;
					yy[2] = 15;
					g.fillPolygon(xx, yy, xx.length);
					g.drawPolygon(xx, yy, xx.length);*/
					g.fillArc(-8, 0, 32, 32, 90, 30);
					
					g.setColor( Color.black );
					g.drawArc(-8, 0, 32, 32, 60, 60);
					//g.drawLine(0, 0, 15, 0);
					//g.drawLine( 15, 0, 8, 15);
					//g.drawLine(0, 0, 7, 15);
					//g.drawPolygon(xx, yy, xx.length);
					
					//g.fillOval(0, 0, bi.getWidth()-1, bi.getHeight()-1);
					
					//g.drawRect(0, 0, 15, 15);
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bi, "png", baos);
					
					String dataurl = Base64.encode( baos.getBytes() );
					
					LatLng ll = null;
					if( m.containsKey(split[0]) ) {
						ll = m.get(split[0]);
						jo.call("pos", new Object[] {split[split.length-1]+" Ph:"+split[3]+" Temp:"+split[2],ll.lat+r.nextDouble()/100.0,ll.lng+r.nextDouble()/100.0+"",content, "data:image/png;base64,"+dataurl});
					} else {
						jo.call("alert", new Object[] {split[0]});
					}
				}
				
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
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
		
		final Map<String,LatLng>	m = new HashMap<String,LatLng>();
		m.put("Hveragerdi", new LatLng(63.99,-21.10));
		m.put("Hveravellir", new LatLng(64.45,-19.33));
		m.put("Oxarfjordur", new LatLng(66.11,-16.75));
		m.put("Hrafntinnusker", new LatLng(63.55,-19.09));
		m.put("Geysir area", new LatLng(64.19,-20.18));
		m.put("Snaefellsnes", new LatLng(64.65,-22.15));
		m.put("Hagongur", new LatLng(64.32,-18.12));
		m.put("Reykjanes", new LatLng(63.88,-22.42));
		
		final JComboBox	combobox = new JComboBox();
		JToolBar toolbar = new JToolBar();
		toolbar.add( new JLabel("Temp filter:") );
		
		final JSpinner minspin = new JSpinner( new SpinnerNumberModel(45,0,100,1) );
		toolbar.add( minspin );
		final JSpinner maxspin = new JSpinner( new SpinnerNumberModel(100,0,100,1) );
		toolbar.add( maxspin );
		
		toolbar.add( new JLabel("Ph filter:") );
		final JSpinner phminspin = new JSpinner( new SpinnerNumberModel(2,0,14,1) );
		toolbar.add( phminspin );
		final JSpinner phmaxspin = new JSpinner( new SpinnerNumberModel(12,0,14,1) );
		toolbar.add( phmaxspin );
		
		ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				load( m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getSelectedItem() );
			}
		};
		minspin.addChangeListener( cl );
		maxspin.addChangeListener( cl );
		phminspin.addChangeListener( cl );
		phmaxspin.addChangeListener( cl );
		
		combobox.addItem( "All" );
		combobox.addItem( "T.thermophilus" );
		combobox.addItem( "T.scotoductus" );
		combobox.addItem( "T.brockianus" );
		combobox.addItem( "T.filiformis" );
		combobox.addItem( "T.eggertsoni" );
		combobox.addItem( "T.islandicus" );
		combobox.addItem( "T.oshimai" );
		combobox.addItem( "T.antranikianii" );
		combobox.addItem( "T.igniterrae" );
		
		combobox.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				load( m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getSelectedItem() );
			}
		});
		
		toolbar.add( new JLabel("Species filter:") );
		toolbar.add( combobox );
		
		this.add( toolbar );
		
		load( m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getSelectedItem() );
		
		//jo.call("pos", new Object[] {"thermi","50.0","50.0"});
		//jo.call("pos", new Object[] {"thermi2","50.0","54.0"});
		//JSObject map = jo.getMember("map");
		//map.call(arg0, arg1)
	}
	
	public static void main(String[] args) {
		try {
			FileWriter fw = new FileWriter("/home/sigmar/bphage.fasta");
			BufferedReader br = new BufferedReader( new FileReader("/media/5edfba74-d384-4284-9f44-8367804a64a1/nr") );
			String line = br.readLine();
			while( line != null ) {
				if( line.contains("acteriophage") ) {
					fw.write( line+"\n" );
					
					line = br.readLine();
					while( line != null && !line.startsWith(">") ) {
						fw.write( line+"\n" );
						line = br.readLine();
					}
				} else line = br.readLine();
			}
			br.close();
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
