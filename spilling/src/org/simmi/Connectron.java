package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.Corp.LinkInfo;
import org.simmi.shared.TreeUtil;

public class Connectron extends JApplet implements MouseListener, MouseMotionListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JScrollPane			scrollpane = null;
	JComponent			c = null;
	Rectangle 			selRect = null;
	static Color 		selColor = new Color( 0,128,255,32 );
	Corp				linkCorp = null;
	Corp				linkCorp2 = null;
	boolean 			toggle = false;
	static boolean		birta = true;
	boolean				drawLinks = true;
	boolean				drawLinkNames = true;
	boolean				drawNodeNames = true;
	//boolean				drawPersonNames = true;
	boolean				d3 = true;
	
	static double hhx, hhy, hhz;
	static double cx;
	static double cy;
	static double cz;
	boolean shift = false;
	static boolean	fixed = false;
	
	double dsize = 1.0;
	
	Point 	np;
	Point	p;
	
	public Thread springThread() {
		return new Thread() {
			public void run() {
				while( toggle ) {
					c.repaint();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	double u = 1000.0;
	public void spring() {
		final double damp = 0.97;
		//final double u = 1000.0;
		final double gorm = 0.0;
		final double k = 0.001;
		
		for( Corp corp : Corp.corpList ) {
			double fx = 0;
			double fy = 0;
			double fz = 0;
			for( Corp c : Corp.corpList ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				double r3 = r*r*r;
				
				if( r3 > 0.1 ) {
					fx += (u*dx)/r3;
					fy += (u*dy)/r3;
					fz += (u*dz)/r3;
				}
			}
			for( Corp c : corp.connections.keySet() ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				
				LinkInfo li = corp.connections.get(c);
				double st = li.getStrength();
								
				fx -= k*st*(dx-gorm);
				fy -= k*st*(dy-gorm);
				fz -= k*st*(dz-gorm);
			}
			
			corp.vx = (corp.vx+fx)*damp;
			corp.vy = (corp.vy+fy)*damp;
			corp.vz = (corp.vz+fz)*damp;
		
			corp.setx( corp.getx() + corp.vx );
			corp.sety( corp.gety() + corp.vy );
			corp.setz( corp.getz() + corp.vz );
			
			//corp.setBounds( (int)(corp.x-Corp.size/2), (int)(corp.y-Corp.size/2), Corp.size, Corp.size );
		}
		//c.repaint();
	}
	
	static Corp backtracker = null;
	static void backtrack( double x, double y, double z, int w, int h, Corp c ) {
		double cosx = Math.cos(hhx);
		double sinx = Math.sin(hhx);
		double cosy = Math.cos(hhy);
		double siny = Math.sin(hhy);
		double cosz = Math.cos(hhz);
		double sinz = Math.sin(hhz);
		
		backtracker = c;
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		double dx = x - w/2.0;
		double dy = y - h/2.0;
		double dz = z; //c.depz;//zoomval;
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );
	}
	
	void backtrack( Point p, double z, Corp c ) {		
		//double hx = hhx;
		//double hy = hhy;
		//double hz = hhz;
		
		/*if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}*/
		
		//System.err.println( p + "    " + np );
		
		//updateCenterOfMass();
		backtrack( p.x, p.y, z, scrollpane.getWidth(), scrollpane.getHeight(), c );
		
		/*double dx = p.x - this.getWidth()/2;
		double dy = p.y - this.getHeight()/2;
		double dz = zoomval;
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );*/
	}
	
	public void backtest( double x, double y ) {		
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		//System.err.println( "testing " + hx + "  " + hy + "  " + hz );
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}
		
		System.err.println( p + "    " + np );

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		updateCenterOfMass();
		
		System.err.println( "xyz " + x + "  " + y + "  " + cz );
		
		double dx = x - scrollpane.getWidth()/2;
		double dy = y - scrollpane.getHeight()/2;
		double dz = cz;
		
		System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		System.err.println( "xxyz " + xx + "  " + yy + "  " + zz + "  " + cosx + "   " + sinx );
		
		double ry = yy + cy;
		double rx = xx * cosy + zz * siny + cx;
		double rz = zz * cosy - xx * siny + cz;
		
		System.err.println( "erm " + cx + "  " + cy + "  " + cz );
		System.err.println( "erm " + hx + "  " + hy + "  " + hz );
		System.err.println( "erm " + cosx + "  " + cosy + "  " + cosz );
		
		System.err.println( "rxyz " + rx + "  " + ry + "  " + rz );
		
		xx = (rx - cx) * cosy - (rz - cz) * siny;
		yy = (ry - cy);
		zz = (rx - cx) * siny + (rz - cz) * cosy;
		
		System.err.println( "xxyz " + xx + "  " + yy + "  " + zz );

		nx = xx;
		ny = yy * cosx - zz * sinx;
		nz = yy * sinx + zz * cosx; // cz;
		
		System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );

		lx = nx * cosz + ny * sinz;
		ly = ny * cosz - nx * sinz;
		lz = nz + zoomval;
		
		System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );

		dz = lz;
		dx = (lx * mval) / (zval + dz);
		dy = (ly * mval) / (zval + dz);
		
		System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		x = dx+scrollpane.getWidth()/2;
		y = dy+scrollpane.getHeight()/2;
		double z = dz;
		
		System.err.println( "xyz " + x + "  " + y + "  " + z );
	}
	
	static void updateCenterOfMass() {
		double len = Corp.corpList.size();
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		for( Corp rel : Corp.corpList ) {
			cx += rel.getx();
			cy += rel.gety();
			cz += rel.getz();
		}
		if( len > 0 ) {
			cx /= len;
			cy /= len;
			cz /= len;
		}
	}
	
	static double zoomval = 500.0;
	static double dzoomval = 500.0;
	public void depth() {
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		//System.err.println("depcalc");
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		if( !fixed ) {
			updateCenterOfMass();
		}
		
		for( Corp rel : Corp.corpList ) {
			double xx = (rel.getx() - cx) * cosy - (rel.getz() - cz) * siny;
			double yy = (rel.gety() - cy);
			double zz = (rel.getx() - cx) * siny + (rel.getz() - cz) * cosy;

			double nx = xx;
			double ny = yy * cosx - zz * sinx;
			double nz = yy * sinx + zz * cosx; // cz;

			double lx = nx * cosz + ny * sinz;
			double ly = ny * cosz - nx * sinz;
			double lz = nz + zoomval;

			double dz = lz;
			double dx = (lx * mval) / (zval + dz);
			double dy = (ly * mval) / (zval + dz);

			rel.px = dx+scrollpane.getWidth()/2.0;
			rel.py = dy+scrollpane.getHeight()/2.0;
			rel.pz = dz;
			
			int size = (int) (d * rel.getSizeDouble() * dsize / (zval + dz));
			
			int x = (int)dx+scrollpane.getWidth()/2;
			int y = (int)dy+scrollpane.getHeight()/2;
			
			/*if( backtracker != null ) {
				System.err.println( "backtracker " + x + "  " + y + "  " + dz );
				
				backtracker = null;
			}*/
			
			rel.depz = dz;

			/*if( rel == Corp.corpList.get(Corp.corpList.size()-1) ) {
				System.err.println( "hoho " + cx + "  " + cy + "  " + cz );
				System.err.println( "hoho " + hx + "  " + hy + "  " + hz );
				System.err.println( "hoho " + cosx + "  " + cosy + "  " + cosz );
				System.err.println( "heyr " + rel.x + "   " + rel.y + "   " + rel.z );
				System.err.println( "xx " + xx + "   " + yy + "   " + zz );
				System.err.println( "nx " + nx + "   " + ny + "   " + nz );
				System.err.println( "lx " + lx + "   " + ly + "   " + lz );
				System.err.println( "dx " + dx + "   " + dy + "   " + dz );
				System.err.println( "x " + x + "   " + y );
			}*/
			//System.err.println( "set bounds " + x + "   " + y + "   " + rel.x + "   " + rel.y + "   " + rel.z );
			rel.setBounds( x, y, size, size );
		}
	}
	
	public void excelLoad( InputStream is ) throws IOException {
		final int fasti = 1000;
		final int zoffset = 0;
		XSSFWorkbook workbook = new XSSFWorkbook( is );
		XSSFSheet corpSheet = workbook.getSheet("Adilar");
		//XSSFSheet linkSheet = workbook.getSheet("Links");
		
		Corp.corpMap.clear();
		c.removeAll();
		
		Random rand = new Random();
		int i = 0;
		XSSFRow 	corpRow = corpSheet.getRow( ++i );
		while( i < 500 ) {
			XSSFCell cell = null;
			if( corpRow != null ) cell = corpRow.getCell(7);
			if( cell != null ) {
				String name = Integer.toString( cell.getStringCellValue().hashCode() );
				
				cell = corpRow.getCell(9);
				String kt = "";
				if( cell != null ) {
					int ctype = cell.getCellType();
					if( ctype == XSSFCell.CELL_TYPE_NUMERIC ) {
						kt = Integer.toString( (int)cell.getNumericCellValue() );
					} else if( ctype == XSSFCell.CELL_TYPE_STRING ) { 
						kt = cell.getStringCellValue();
					}
				}
				
				cell = corpRow.getCell(10);
				String desc = "";
				if( cell != null ) desc = Integer.toString( cell.getStringCellValue().hashCode() );
				
				cell = corpRow.getCell(11);
				String home = "";
				if( cell != null ) home = cell.getStringCellValue();
				
				cell = corpRow.getCell(16);
				String father = "";
				if( cell != null ) father = Integer.toString( cell.getStringCellValue().hashCode() );
				
				cell = corpRow.getCell(17);
				String mother = "";
				if( cell != null ) mother = Integer.toString( cell.getStringCellValue().hashCode() );
				
				cell = corpRow.getCell(18);
				String maki = "";
				if( cell != null ) maki = Integer.toString( cell.getStringCellValue().hashCode() );
				
				Corp corp = null;
				if( !Corp.corpMap.containsKey(name) ) {
					corp = new Corp( name );
					corp.type = "person";
					corp.text = desc;
					corp.home = home;
					corp.kt = kt;
					corp.setx( rand.nextInt(fasti) );
					corp.sety( rand.nextInt(fasti) );
					corp.setz( rand.nextInt(fasti)-zoffset );
					corp.color = Color.green;
					c.add( corp );
					//corp.setBounds( (int)(corp.x-Corp.size/2), (int)(corp.y-Corp.size/2), Corp.size, Corp.size );
				} else {
					corp = Corp.corpMap.get(name);
					corp.text += "\n\n"+desc;
				}
				
				if( father.length() > 0 ) {
					Corp fcorp = null;
					if( Corp.corpMap.containsKey(father) ) {
						fcorp = Corp.corpMap.get(father);
					} else {
						fcorp = new Corp( father );
						fcorp.type = "person";
						fcorp.setx( rand.nextInt(fasti) );
						fcorp.sety( rand.nextInt(fasti) );
						fcorp.setz( rand.nextInt(fasti)-zoffset );
						fcorp.color = Color.green;
						c.add( fcorp );
					}
					
					//corp.connections.put( fcorp, new HashSet<String>( Arrays.asList( new String[] {"barn"} ) ) );
					//fcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"faðir"} ) ) );
					
					corp.addLink( fcorp, "barn" );
					fcorp.addLink( corp, "faðir" );
				}
				
				if( mother.length() > 0 ) {
					Corp mcorp = null;
					if( Corp.corpMap.containsKey(mother) ) {
						mcorp = Corp.corpMap.get(mother);
					} else {
						mcorp = new Corp( mother );
						mcorp.type = "person";
						mcorp.setx( rand.nextInt(fasti) );
						mcorp.sety( rand.nextInt(fasti) );
						mcorp.setz( rand.nextInt(fasti)-zoffset );
						mcorp.color = Color.green;
						c.add( mcorp );
					}
					
					//corp.connections.put( mcorp, new HashSet<String>( Arrays.asList( new String[] {"barn"} ) ) );
					//mcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"móðir"} ) ) );
					
					corp.addLink( mcorp, "barn" );
					mcorp.addLink( corp, "móðir" );
				}
				
				if( maki.length() > 0 ) {
					Corp mcorp = null;
					if( Corp.corpMap.containsKey(maki) ) {
						mcorp = Corp.corpMap.get(maki);
					} else {
						mcorp = new Corp( maki );
						mcorp.type = "person";
						mcorp.setx( rand.nextInt(fasti) );
						mcorp.sety( rand.nextInt(fasti) );
						mcorp.setz( rand.nextInt(fasti)-zoffset );
						mcorp.color = Color.red;
						c.add( mcorp );
					}
					
					//corp.connections.put( mcorp, new HashSet<String>( Arrays.asList( new String[] {"maki"} ) ) );
					//mcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"maki"} ) ) );
					
					corp.addLink( mcorp, "maki" );
					mcorp.addLink( corp, "maki" );
				}
				
				int l = 0;
				while( l < 5 ) {
					cell = corpRow.getCell(l);
					if( cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
						String	id = Integer.toString( cell.getStringCellValue().hashCode() );
						
						if( id.length() > 0 ) {
							Corp link = null;
							if( !Corp.corpMap.containsKey(id) ) {
								link = new Corp( id );
								link.type = "corp";
								link.setx( rand.nextInt(fasti) );
								link.sety( rand.nextInt(fasti) );
								link.setz( rand.nextInt(fasti)-zoffset );
								link.color = Color.blue;
								c.add( link );
								//link.setBounds( (int)(link.x-Corp.size/2), (int)(link.y-Corp.size/2), Corp.size, Corp.size );
							} else {
								link = Corp.corpMap.get( id );
							}
							
							//corp.connections.put( link, new HashSet<String>( Arrays.asList( new String[] {"link"} ) ) );
							//link.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"link"} ) ) );
							
							corp.addLink( link, "link" );
							link.addLink( corp, "link" );
						}
					}
					l++;
				}
			}
			
			corpRow = corpSheet.getRow( ++i );
		}
		Connectron.this.repaint();
	}
	
	public void selectAll() {
		for( Corp c : Corp.corpList ) {
			c.selected = true;
			Corp.selectedList.add( c );
		}
		Connectron.this.c.repaint();
	}
	
	public void invertSelection() {
		Corp.selectedList.clear();
		for( Corp c : Corp.corpList ) {
			if( !c.selected ) {
				Corp.selectedList.add( c );
			}
			c.selected = !c.selected;
		}
		Connectron.this.c.repaint();
	}
	
	public void init() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		try {
			initGUI( this );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		this.add( scrollpane );
	}
	
	public void importFrom454ContigGraph( String text, double scaleval ) {
		String[] split = text.split("\n");
		importFrom454ContigGraph( Arrays.asList(split), scaleval );
	}
	
	public void importFrom454ContigGraph( List<String> split, double scaleval ) {
		//String[] split = text.split("\n");
		
		//Map<String,Corp> corpMap = new HashMap<String,Corp>();
		//Map<String,Integer>	lenMap = new HashMap<String,Integer>();
		//Map<String,Double>	covMap = new HashMap<String,Double>();
		
		List<String[]>	lines = new ArrayList<String[]>();
		for( int i = 0; i < split.size(); i++ ) {
			String line = split.get(i);
			String[] subsplit = line.split("\t");
			if( line.startsWith("C") ) lines.add( subsplit );
			else if( line.contains("contig") ) {
				String str = subsplit[0];
				try {
					Integer.parseInt( str );
					//lenMap.put( str, Integer.parseInt(subsplit[2]) );
					//covMap.put( str, Double.parseDouble(subsplit[3]) );
					
					int size = Integer.parseInt(subsplit[2]);
					double cov = Double.parseDouble(subsplit[3]);
					
					int val = (int)(Math.min(100.0, cov)*255.0/100.0);
					String cc = Integer.toString(val, 16);
					if( cc.length() == 1 ) cc = "0"+cc;
					
					Corp c1 = new Corp( str );
					c1.setSize( Math.log(size/10.0)*5.0 );
					c1.subcolor = new Color( val, val, val );
					//corpMap.put( str, c1 );
					Connectron.this.add( c1 );
				} catch( Exception e ) {}
			}
		}
		
		importContigGraph(lines, scaleval);
	}
	
	public void importContigGraph( List<String[]> lines, double scaleval ) {
		Random r = new Random();
		for( String[] spec : lines ) {
			if( spec.length > 1 ) {
				Corp c1 = null;
				if( Corp.corpMap.containsKey(spec[1]) ) {
					c1 = Corp.corpMap.get( spec[1] );
				} /*else {
					c1 = new Corp( spec[1] );
					int size = lenMap.get( spec[1] );
					 cov = lenMap.get( spec[1] );
					c1.setSize( Math.log(size/10.0)*5.0 );
					//corpMap.put( spec[1], c1 );
					Connectron.this.add( c1 );
				}*/
				/*if( c1 == null ) {
					for( String key : corpMap.keySet() ) 
					Browser.getWindow().getConsole().log( "dd " + corpMap.keySet() );
				}*/
				
				c1.setCoulomb(50.0);
				c1.setx( scaleval*r.nextDouble() );
				c1.sety( scaleval*r.nextDouble() );
				c1.setz( scaleval*r.nextDouble() );
				//c1.color = col1111ee;
				Connectron.this.add( c1 );
				
				Corp c2 = null;
				if( Corp.corpMap.containsKey(spec[3]) ) {
					c2 = Corp.corpMap.get( spec[3] );
				}/* else {
					c2 = new Corp( spec[3] );
					int size = lenMap.get( spec[3] );
					c2.setSize( Math.log(size/10.0)*5.0 );
					corpMap.put( spec[3], c2 );
					Connectron.this.add( c2 );
				}*/
				//if( c2 == null ) Browser.getWindow().getConsole().log( "bo2 "+spec[3] );
				
				c2.setCoulomb(50.0);
				c2.setx( scaleval*r.nextDouble() );
				c2.sety( scaleval*r.nextDouble() );
				c2.setz( scaleval*r.nextDouble() );
				//c2.color = col1111ee;
				
				boolean col1set = c1.color != null;
				boolean col2set = c2.color != null;
				
				Color col1111ee = new Color( 0x11, 0x11, 0xEE );
				Color colee11ee = new Color( 0xEE, 0x11, 0xEE );
				Color col11ee11 = new Color( 0x11, 0xEE, 0x11 );
				
				double tscale = 0.0001;
				Double ds = Double.parseDouble( spec[5] );
				if( spec[2].equals("3'") && spec[4].equals("3'") ) {	
					if( col1set && col2set ) {
						if( c1.color.equals(col1111ee) ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals(col1111ee) ) {
							c2.color = col11ee11;
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = col1111ee;
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals(col1111ee) ) {
							c1.color = col11ee11;
						} else {
							c1.color = col1111ee;
						}
					} else {
						c2.color = col11ee11;
						c1.color = col1111ee;
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("3'") && spec[4].equals("5'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals(col1111ee) ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals(col1111ee) ) {
							c2.color = col11ee11;
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = col1111ee;
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals(col1111ee) ) {
							c1.color = col11ee11;
						} else {
							c1.color = col1111ee;
						}
					} else {
						c2.color = col11ee11;
						c1.color = col1111ee;
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("5'") && spec[4].equals("3'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals(col1111ee) ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals(col1111ee) ) {
							c2.color = col11ee11;
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = col1111ee;
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals(col1111ee) ) {
							c1.color = col11ee11;
						} else {
							c1.color = col1111ee;
						}
					} else {
						c2.color = col11ee11;
						c1.color = col1111ee;
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				} else if( spec[2].equals("5'") && spec[4].equals("5'") ) {
					if( col1set && col2set ) {
						if( c1.color.equals(col1111ee) ) {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							}
						} else {
							if( c1.color.equals(c2.color) ) {
								c2.color = colee11ee;
								c2.addLink( c1, spec[5], tscale*ds, 0.0 );
							} else {
								c1.addLink( c2, spec[5], tscale*ds, 0.0 );
							}
						}
					} else if( col1set ) {
						if( c1.color.equals(col1111ee) ) {
							c2.color = col11ee11;
							c2.addLink( c1, spec[5], tscale*ds, 0.0 );
						} else {
							c2.color = col1111ee;
							c1.addLink( c2, spec[5], tscale*ds, 0.0 );
						}
					} else if( col2set ) {
						if( c2.color.equals(col1111ee) ) {
							c1.color = col11ee11;
						} else {
							c1.color = col1111ee;
						}
					} else {
						c2.color = col11ee11;
						c1.color = col1111ee;
						c2.addLink( c1, spec[5], tscale*ds, 0.0 );
					}
				}
				
				//c1.addLink( c2, spec[5], 0.001*ds, 0.0 );
				//c2.addLink( c1, spec[5], 0.001*ds, 0.0 );
			}
		}
	}
	
	private void importExcel( InputStream is ) {
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook( is );
			XSSFSheet corpSheet = workbook.getSheet("Nodes");
			XSSFSheet linkSheet = workbook.getSheet("Links");
			
			Corp.corpMap.clear();
			c.removeAll();
			
			int i = 0;
			int l = 0;
			XSSFRow 	corpRow = corpSheet.getRow( ++i );
			while( corpRow != null ) {
				XSSFCell	cell = corpRow.getCell(0);
				String 	name = cell.getStringCellValue();
				cell = corpRow.getCell(1);
				String 	type = cell.getStringCellValue();
				cell = corpRow.getCell(2);
				String 	text = cell != null ? cell.getStringCellValue() : "";
				cell = corpRow.getCell(3);
				double x = cell.getNumericCellValue();
				cell = corpRow.getCell(4);
				double y = cell.getNumericCellValue();
				cell = corpRow.getCell(5);
				double z = cell.getNumericCellValue();
				cell = corpRow.getCell(6);
				Color color = new Color( Integer.parseInt( cell.getStringCellValue() ) ); //Color.decode( cell.getStringCellValue() );
				
				Corp corp = new Corp( name );
				corp.setName( name );
				corp.type = type;
				//corp.kt = kt;
				corp.text = text;
				corp.setx( x );
				corp.sety( y );
				corp.setz( z );
				corp.color = color;
				c.add( corp );
				corp.setBounds( (int)(corp.getx()-corp.size/2), (int)(corp.gety()-corp.size/2), (int)corp.size, (int)corp.size );
				
				corpRow = corpSheet.getRow( ++i );
			}
			System.err.println( c.getComponentCount() );
			
			XSSFRow 	linkRow = linkSheet.getRow( ++l );
			while( linkRow != null ) {
				XSSFCell cell = linkRow.getCell(0);
				String 	id1 = cell.getStringCellValue();
				cell = linkRow.getCell(1);
				String		id2 = cell.getStringCellValue();
				cell = linkRow.getCell(2);
				String 	str = cell.getStringCellValue();
				
				Corp p1 = Corp.corpMap.get(id1);
				Corp p2 = Corp.corpMap.get(id2);
				
				String[] ss = str.split("\n");
				Set<String> 	value = new HashSet<String>( Arrays.asList(ss) );
				//p1.connections.put( p2, value );
				p1.addLink( p2, value );
				
				linkRow = linkSheet.getRow( ++l );
			}
			Connectron.this.repaint();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void exportExcel( OutputStream os ) throws IOException {
		XSSFWorkbook 	workbook = new XSSFWorkbook();
		
		XSSFSheet corpSheet = workbook.createSheet("Nodes");
		XSSFSheet linkSheet = workbook.createSheet("Links");
		
		int i = 0;
		int l = 0;
		XSSFRow 	corpRow = corpSheet.createRow( i );
		XSSFCell cell = corpRow.createCell( 0 );
		cell.setCellValue( "name" );
		cell = corpRow.createCell( 1 );
		cell.setCellValue( "type" );
		cell = corpRow.createCell( 2 );
		cell.setCellValue( "desc" );
		cell = corpRow.createCell( 3 );
		cell.setCellValue( "x" );
		cell = corpRow.createCell( 4 );
		cell.setCellValue( "y" );
		cell = corpRow.createCell( 5 );
		cell.setCellValue( "z" );
		cell = corpRow.createCell( 6 );
		cell.setCellValue( "image" );
		
		XSSFRow 		linkRow = linkSheet.createRow( l );
		cell = linkRow.createCell( 0 );
		cell.setCellValue( "id1" );
		cell = linkRow.createCell( 1 );
		cell.setCellValue( "id2" );
		cell = linkRow.createCell( 2 );
		cell.setCellValue( "desc" );
		
		for( String name : Corp.corpMap.keySet() ) {
			Corp 		corp = Corp.corpMap.get( name );
			corpRow = corpSheet.createRow( ++i );
			
			cell = corpRow.createCell( 0 );
			cell.setCellValue( corp.getName() );
			cell = corpRow.createCell( 1 );
			cell.setCellValue( corp.type );
			cell = corpRow.createCell( 2 );
			cell.setCellValue( corp.text );
			cell = corpRow.createCell( 3 );
			cell.setCellValue( corp.getx() );
			cell = corpRow.createCell( 4 );
			cell.setCellValue( corp.gety() );
			cell = corpRow.createCell( 5 );
			cell.setCellValue( corp.getz() );
			cell = corpRow.createCell( 6 );
			if( corp.imageNames.size() > 0 && corp.imageNames.get(0) != null ) {
				cell.setCellValue( corp.imageNames.get(0) );
			} else {
				cell.setCellValue( Integer.toString( corp.color.getRGB() ) );
			}
			
			for( Corp cp : corp.connections.keySet() ) {
				Set<String>		link = corp.connections.get(cp).linkTitles;
				linkRow = linkSheet.createRow( ++l );
				cell = linkRow.createCell( 0 );
				cell.setCellValue( corp.getName() );
				cell = linkRow.createCell( 1 );
				cell.setCellValue( cp.getName() );
				cell = linkRow.createCell( 2 );
				String val = "";
				for( String str : link ) {
					val += str+"\n";
				}
				cell.setCellValue( val );
			}
		}
		workbook.write( os );
	}
	
	public Color translateColor( String colorstr ) {
		Color color = Color.green;
		try {
			int r = Integer.parseInt( colorstr.substring(0, 2), 16 );
			int g = Integer.parseInt( colorstr.substring(2, 4), 16 );
			int b = Integer.parseInt( colorstr.substring(4, 6), 16 );
			color = new Color( r,g,b );
		} catch( Exception e ) {
			
		}
		return color;
	}
	
	public void importFromText( String text ) {
		u = 50000.0;
		
		String[] split = text.split("\n");
		String[] species = split[0].split("\t");
		
		List<Corp> corpList = new ArrayList<Corp>();
		
		Random r = new Random();
		for( String spec : species ) {
			Corp corp = new Corp( spec );
			corp.setx( 400.0*r.nextDouble() );
			corp.sety( 400.0*r.nextDouble() );
			corp.setz( 400.0*r.nextDouble() );
			c.add( corp );
			
			corpList.add( corp );
		}
		
		for( int i = 1; i < split.length; i++ ) {
			String[] subsplit = split[i].split("\t");
			int y = i-1;
			for( int x = 0; x < subsplit.length; x++ ) {
				if( x != y ) {
					double d = Double.parseDouble( subsplit[x] );
					
					Corp corpDst = corpList.get(x);
					Corp corpSrc = corpList.get(y);
					
					corpSrc.addLink( corpDst, subsplit[x], d, 0.0 );
				}
			}
		}
		
		c.repaint();
	}
	
	int loc;
	Random r = new Random();
	private Corp recursiveNodeGeneration( List<Corp> corpList, TreeUtil.Node node ) {
		int i = node.getName().indexOf("Thermus");
		Corp corp = new Corp( i > 0 ? node.getName().substring(i) : node.getName() );
		corp.setx( 400.0*r.nextDouble() );
		corp.sety( 400.0*r.nextDouble() );
		corp.setz( 400.0*r.nextDouble() );
		
		if( node.getName() == null || node.getName().trim().length() == 0 ) {
			corp.setSize( 8, 8 );
			corp.color = Color.black;
		} else {
			String color = node.getColor();
			if( color != null ) corp.color = translateColor( color );
			else {
				//TreeUtil.Node parent = node.getParent();
				for( Corp parent : corp.getBackLinks() ) {
					if( parent != null && parent.getName() != null && parent.getName().length() > 0 ) {
						corp.color = parent.color;
						break;
					}
				}
			}
		}
		//else corp.color = "#000000";
		//console( "col " + corp.color );
		this.add( corp );
		corpList.add( corp );
		
		for( TreeUtil.Node n : node.getNodes() ) {
			Corp c = recursiveNodeGeneration(corpList, n);
			double val = (1.0/( Math.abs( node.geth() )+0.0005 ))/50.0;
			String strval = Double.toString( node.geth() ); //Math.round(val*100.0)/100.0 );
			corp.addLink(c, strval, val, 0.0 );
			c.addLink(corp, strval, val, 0.0 );
		}
		
		return corp;
	}
	
	public void importFromTree( String text ) {
		u = 50.0;
		
		loc = 0;
		TreeUtil treeutil = new TreeUtil();
		treeutil.init( text, false, null, null, false, null, null, false );
		TreeUtil.Node resultnode = treeutil.getNode();
		//Node resultnode = parseTreeRecursive( text, false );
		
		List<Corp> corpList = new ArrayList<Corp>();
		recursiveNodeGeneration( corpList, resultnode );
		
		repaint();
	}
	
	public void importFromMatrix( String text ) {
		u = 5000.0;
		
		String[] split = text.split("\n");
		String[] persons = split[0].split("\t");
		
		List<Corp> corpList = new ArrayList<Corp>();
		
		Random r = new Random();
		for( String spec : persons ) {
			if( spec.length() > 1 ) {
				Corp corp = new Corp( spec );
				corp.setx( 400.0*r.nextDouble() );
				corp.sety( 400.0*r.nextDouble() );
				corp.setz( 400.0*r.nextDouble() );
				this.add( corp );
				
				corpList.add( corp );
			}
		}
		
		for( int i = 1; i < split.length; i++ ) {
			String[] subsplit = split[i].split("\t");
			//int y = i-1;
			String spec = subsplit[0];
			Corp corp = new Corp( spec );
			corp.setx( 400.0*r.nextDouble() );
			corp.sety( 400.0*r.nextDouble() );
			corp.setz( 400.0*r.nextDouble() );
			corp.color = translateColor( "1111ee" );
			this.add( corp );
			corpList.add( corp );
			
			for( int x = 1; x < subsplit.length; x++ ) {
				double d = Double.parseDouble( subsplit[x] );
				if( d > 0.0 ) {
					//Corp corpDst = corpList.get(x);
					//Corp corpSrc = corpList.get(y);
					
					Corp pcorp = corpList.get(x-1);
					corp.addLink( pcorp, subsplit[x], d/100.0, 0.0 );
					pcorp.addLink( corp, subsplit[x], d/100.0, 0.0 );
				}
			}
		}
		
		repaint();
	}
	
	public void initGUI( Container cnt ) throws ClassNotFoundException {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//SwingUtilities.updateComponentTreeUI( this );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.getContentPane().setBackground( Color.white );
		
		Corp.prop = new Prop();
		Corp.prop.setBounds(0, 0, 400, 75);
		
		scrollpane = new JScrollPane();
		c = new JComponent() {
			long last = 0;
			
			public boolean isVisible() {
				return super.isVisible() && birta;
			}
			
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				//System.err.println(cx + " " + cy + " " + cz);
				if( !shift ) {
					birta = false;
					if( toggle ) {
						updateCenterOfMass();
						spring();
					}
					depth();
					birta = true;
				}
				
				g.setColor( Color.white );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font	oldFont = g2.getFont();
				if( oldFont.getSize() != 7 ) {
					oldFont = oldFont.deriveFont(8.0f);
					g2.setFont( oldFont );
				}
				if( drawLinks ) {
					for( Component c : this.getComponents() ) {
						if( c instanceof Corp ) {
							Corp corp = (Corp)c;						
							Rectangle inrect = this.getVisibleRect();
							for( Corp cc : corp.getLinks() ) {
								int x1 = c.getX()+c.getWidth()/2;
								int y1 = c.getY()+c.getHeight()/2;
								int x2 = cc.getX()+cc.getWidth()/2;
								int y2 = cc.getY()+cc.getHeight()/2;
								
								if( cc.depz > 0.0 && (inrect.contains(x1, y1) || inrect.contains(x2, y2)) ) {
									g.setColor( Color.gray );
									g.drawLine( x1, y1, x2, y2 );
									
									/*double h1 = Math.atan2( y2 - y1, x2 - x1 );
									double h2 = h1 + Math.PI/8.0;
									int x3 = (int)( x2 - cc.getWidth()*Math.cos(h1)/2.0 );
									int y3 = (int)( y2 - cc.getHeight()*Math.sin(h1)/2.0 );
									
									//int offx = (x2 - x3)/5;
									//int offy = (y2 - y3)/5;
									//System.err.println( offx + "  " + offy );
									g.drawLine( x3, y3, (int)(x3 - 10.0*Math.cos( h2 )), (int)(y3 - 10.0*Math.sin( h2 )) );*/
									
									if( drawLinkNames && !toggle && p == null ) {
										Set<String> strset = corp.connections.get(cc).linkTitles;
										int x = (x1+x2)/2;
										int y = (y1+y2)/2;
										double t = Math.atan2( y2-y1, x2-x1 );
										g2.rotate(t, x, y);
										int k = 0;
										g.setColor( Color.black );
										for( String str : strset ) {
											if( !str.equals("link") ) {
												int strw = g.getFontMetrics().stringWidth( str );
												//if( corp.selectedLink != null ) System.err.println( corp.selectedLink );
												if( cc == linkCorp2 && str.equals( corp.selectedLink ) ) {
													g2.setFont( oldFont.deriveFont( Font.BOLD ) );
												}
												g.drawString( str, x-strw/2, y-5-k );
												if( g2.getFont() != oldFont ) g2.setFont( oldFont );
												k += 10;
											}
										}
										g2.rotate(-t, x, y);
									}
								}
							}
						}
					}
				}
				
				for( Component c : this.getComponents() ) {
					if( c instanceof Corp ) {
						Corp corp = (Corp)c;
						int strWidth = g.getFontMetrics().stringWidth( corp.getName() );
						g.setColor( Color.black );
						//if( (corp.type.equals("person") && drawNodeNames) || (corp.type.equals("corp") && drawCorpNames) ) {
						if( drawNodeNames ) {
							if( corp.getName().length() > 50 ) g.drawString( corp.getName().substring(0, 50), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
							else g.drawString( corp.getName(), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
						}
					}
				}
				
				Corp c = Corp.drag;
				if( c != null && c.getp() != null ) {
					//g2.drawLine( c.getX()+c.getWidth()/2, c.getY()+c.getHeight()/2, c.getX()+c.p.x, c.getY()+c.p.y );
				}
				
				if( selRect != null ) {
					g2.setColor( selColor );
					g2.fillRect( selRect.x, selRect.y, selRect.width, selRect.height );
				}
				
				if( toggle ) {
					long val = System.currentTimeMillis();
					long diff = val - last;
					val = last;
					
					if( diff < 100 ) {
						try {
							Thread.sleep(100-diff);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					this.repaint();
				}
			}
		};
		c.addMouseListener( this );
		c.addMouseMotionListener( this );
		c.addKeyListener( this );
		
		final DataFlavor df = new DataFlavor("text/plain;charset=utf-8");
		
		final Transferable transferable = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
				StringBuilder ret = new StringBuilder();				
				return new ByteArrayInputStream( ret.toString().getBytes() );
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
		
		TransferHandler th = new TransferHandler() {
			private static final long serialVersionUID = 1L;
			
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			protected Transferable createTransferable(JComponent c) {
				return transferable;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				try {
					Object obj = support.getTransferable().getTransferData( df );
					InputStream is = (InputStream)obj;
					
					ByteArrayOutputStream	baos = new ByteArrayOutputStream();
					byte[] bb = new byte[2048];
					int r = is.read(bb);
					while( r > 0 ) {
						baos.write(bb, 0, r);
						r = is.read(bb);
					}
					baos.close();
					String dropstuff = new String(baos.toString());
					if( dropstuff.startsWith("(") ) {
						importFromTree( dropstuff.replaceAll("[\r\n]+", "") );
					} else if( dropstuff.startsWith("\t") ) {
						importFromMatrix( dropstuff );
					} else importFromText( dropstuff );
					
					//importFromText( new String(bb,0,r) );
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		c.setTransferHandler( th );
		
		/*try {
			loadAll();
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/
		
		c.setPreferredSize( new Dimension(this.getWidth(), this.getHeight()) );
		c.addContainerListener( new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				Component c = e.getChild();
				if( c instanceof Prop ) {
					Prop p = (Prop)c;
					if( p.currentCorp != null ) {
						p.currentCorp.setName( p.name.getText() );
						p.currentCorp.type = p.kt.getText();
						p.currentCorp.text = p.text.getText();
						p.currentCorp.color = p.color;
						
						for( Corp corp : Corp.corpList ) {
							if( corp != p.currentCorp && corp.type.equals( p.currentCorp.type ) ) {
								corp.color = p.currentCorp.color;
							}
						}
						
						if( p.currentCorp.text.length() > 0 ) p.currentCorp.setToolTipText( p.currentCorp.text );
						
						try {
							p.currentCorp.save();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {}
		});
		
		final JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Show/Hide Links") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawLinks = !drawLinks;
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Show/Hide Links Names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawLinkNames = !drawLinkNames;
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Show/Hide Node Names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawNodeNames = !drawNodeNames;
				c.repaint();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Enable/Disable autosave") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Corp.autosave = !Corp.autosave;
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Add Node") {
			@Override
			public void actionPerformed(ActionEvent e) {
				double z = 0.0;
				for( Corp corp : Corp.corpList ) {
					z += corp.depz;
				}
				if( Corp.corpList.size() > 0 ) z /= Corp.corpList.size();
				//System.err.println( "len "+ Corp.corpList.size() );
				
				Corp 		corp = new Corp(Corp.getCreateName(),"unknown",m.x-(int)(32/2.0), m.y-(int)(32/2.0));
				
				//backtest( m.x, m.y );
				
				//System.err.println( m );
				backtrack( m, z, corp );
				try {
					corp.save();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Select all") {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});
		popup.add( new AbstractAction("Invert selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				invertSelection();
			}
		});
		popup.add( new AbstractAction("Flatten") {
			@Override
			public void actionPerformed(ActionEvent e) {
				double len = Corp.corpList.size();
				double lcx = 0.0;
				double lcy = 0.0;
				double lcz = 0.0;
				for( Corp rel : Corp.corpList ) {
					lcx += rel.getx();
					lcy += rel.gety();
					lcz += rel.getz();
				}
				lcx /= len;
				lcy /= len;
				lcz /= len;
			}
		});
		/*popup.add( new AbstractAction("Add Corp") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unknown","corp",m.x,m.y);
				try {
					corp.save();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});*/
		popup.addSeparator();
		
		popup.add( new AbstractAction("Load sample data") {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputStream is = this.getClass().getResourceAsStream("/Greining2.xlsx");
				try {
					excelLoad( is );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/*popup.add( new AbstractAction("Import from dirty Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						excelLoad( new FileInputStream( f ) );
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
		});*/
		popup.add( new AbstractAction("Import from Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean succ = false;
			    try {
			    	FileOpenService fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
			    	FileContents fc = fos.openFileDialog( null, new String[] {"xls", "xlsx"} );
			    	importExcel( fc.getInputStream() );
			    	succ = true;
			    } catch( NoClassDefFoundError er) { 
			        er.printStackTrace();
			    } catch( Exception ex ) { 
			        ex.printStackTrace();
			    }
			    
			    if( !succ ) {
					JFileChooser fc = new JFileChooser();
					if( fc.showOpenDialog( Connectron.this ) == JFileChooser.APPROVE_OPTION ) {
						File f = fc.getSelectedFile();
						try {
							importExcel( new FileInputStream(f) );
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
			    }
			}
		});
		popup.add( new AbstractAction("Open in Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean succ = false;
			    try {
			    	FileSaveService fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
			    	ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			    	exportExcel( baos );
			    	FileContents fc = fss.saveFileDialog( null, new String[] {"xls", "xlsx"}, new ByteArrayInputStream( baos.toByteArray() ), "export.xlsx" );
			    	importExcel( fc.getInputStream() );
			    	succ = true;
			    } catch( NoClassDefFoundError er) { 
			        er.printStackTrace();
			    } catch( Exception ex ) { 
			        ex.printStackTrace();
			    }
			    
			    if( !succ ) {
				
				//JFileChooser fc = new JFileChooser();
				//if( fc.showSaveDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
				//File f = fc.getSelectedFile();
					try {
						File nf = File.createTempFile("tmp", ".xlsx");
						exportExcel( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Spring Graph") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if( !toggle ) springThread().start();
				
				toggle = !toggle;
				c.repaint();
			}
		});
		c.setComponentPopupMenu(popup);
		
		scrollpane.setViewportView( c );
	}

	Point	m = new Point(0,0);
	@Override
	public void mouseDragged(MouseEvent e) {
		np = e.getPoint();
		if( shift ) {
			selRect = new Rectangle( Math.min(p.x, np.x), Math.min(p.y, np.y), Math.abs(p.x-np.x), Math.abs(p.y-np.y) );
			
			/*Rectangle rect = c.getVisibleRect();			
			rect.translate( p.x-np.x, p.y-np.y );
			c.scrollRectToVisible( rect );*/
		}
		c.repaint();
	}
	
	public void saveAll() throws IOException {
		for( Component c : this.getComponents() ) {
			if( c instanceof Corp ) {
				Corp corp = (Corp)c;
				corp.save();
			}
		}
	}
	
	public void reloadAll() throws IOException {
		for( Component c : this.getComponents() ) {
			if( c instanceof Corp ) {
				Corp corp = (Corp)c;
				corp.load();
			}
		}
	}
	
	public void loadAll() throws IOException {
		boolean succ = false;
		try {
			PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
			 
			String[] names = ps.getNames( bs.getCodeBase() );
			for( String name : names ) {
				if( name.startsWith("spoil_") ) {
					Corp corp = new Corp( name.substring(6) );
					c.add( corp );
				}
			}
			
			//URL url = new URL( bs.getCodeBase().toString() + "spoil_" + this.getName() );
			//ps.create( url, 1000 );
			//FileContents fc = ps.get( url );
			//InputStream is = fc.getInputStream();
			//InputStreamReader isr = new InputStreamReader( is );
			//loadFile( isr, bs.getCodeBase().toString() );
			
			succ = true;
		} catch( NoClassDefFoundError e ) {
			e.printStackTrace();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( !succ ) {
			String homedir = System.getProperty("user.home");
			File dir = new File( homedir, "spoil" );
			if( dir.exists() ) {
				File[] ff = dir.listFiles();
				for( File f : ff ) {
					if( !f.isDirectory() ) {
						Corp corp = new Corp( f.getName() );
						c.add( corp );
					}
					//addFile( f );
				}
			}
		}
			
		for( String name : Corp.corpMap.keySet() ) {
			Corp c = Corp.corpMap.get( name );
			c.load();
		}
	}
	
	public void addCorp( String name ) throws IOException {
		c.add( load( name ) );
	}
	
	public void addFile( File save ) throws IOException {
		c.add( loadFile( save ) );
	}
	
	public Corp load( String name ) throws IOException {
		Corp corp = new Corp( name );
		corp.load();
		return corp;
	}
	
	public Corp loadFile( File save ) throws IOException {
		String name = save.getName();
		Corp corp = new Corp( name );
		corp.loadFile( new FileReader(save), new File( save.getParentFile(), "images" ).toURI().toURL().toString() );
		return corp;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m = e.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		toggle = false;
		p = e.getPoint();
		np = p;
		shift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
		
		c.requestFocus();
		
		Corp.selectedList.clear();
		linkCorp = null;
		linkCorp2 = null;
		for( Component comp : c.getComponents() ) {
			if( comp instanceof Corp ) {
				Corp corp = ((Corp)comp);
				corp.selected = false;
				corp.selectedLink = null;
				corp.vx = 0;
				corp.vy = 0;
				corp.vz = 0;
				for( Corp corp2 : corp.connections.keySet() ) {						
					double x1 = corp.getX() + corp.getWidth()/2;
					double x2 = corp2.getX() + corp2.getWidth()/2;
					double y1 = corp.getY() + corp.getHeight()/2;
					double y2 = corp2.getY() + corp2.getHeight()/2;
					double xx = (x1 + x2) / 2.0;
					double yy = (y1 + y2) / 2.0;
					
					double h1 = Math.atan2( x1-xx, y1-yy );
					//if( h1 < 0 ) h1 += 2*Math.PI;
					double h2 = Math.atan2( p.x-xx, p.y-yy );
					//if( h2 < 0 ) h2 += 2*Math.PI;
					double h = h2 - h1;
					if( h > Math.PI ) h -= 2*Math.PI;
					if( h < -Math.PI ) h += 2*Math.PI;
					/*System.err.println( (x1) + "  " + (y1) );
					System.err.println( (x2) + "  " + (y2) );
					System.err.println( (x1-xx) + "  " + (y1-yy) );
					System.err.println( h1+ "  " + h2 + "  " + h );*/
					if( h < 0 ) {
						Set<String>	strset = corp.connections.get(corp2).linkTitles;
						if( strset != null && strset.size() > 0 && p.distance( xx, yy ) < 32 ) {
							linkCorp = corp;
							linkCorp2 = corp2;
							
							System.err.println("found link " + linkCorp.getName() + "  " + linkCorp2.getName() );
							corp.selectedLink = corp.connections.get(corp2).linkTitles.iterator().next();
						}
					}
				}
			}
		}
		//c.remove( Corp.textfield );
		if( Corp.prop != null ) {
			Corp.prop.currentCorp = null;
			c.remove( Corp.prop );
		}
		c.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
		if( selRect != null ) {
			for( Component cc : c.getComponents() ) {
				if( cc instanceof Corp ) {
					Corp corp = (Corp)cc;
					if( corp.getBounds().intersects(selRect) ) {
						corp.selected = true;
						Corp.selectedList.add(corp);
					}
					else corp.selected = false;
				}
			}
			selRect = null;
		}
		
		if( p != null && !shift ) {
			double d = (np.x - p.x) / 100.0 + hhy + Math.PI;
			double h = Math.floor( d / (2.0*Math.PI) );
			double nd = d - h * (2.0*Math.PI) - Math.PI;
			hhy = nd; //(np.x - p.x) / 100.0 + hhy;
			/*} else {
				double d = (np.x - p.x) / 100.0 + hhz + Math.PI;
				double h = Math.floor( d / (2.0*Math.PI) );
				double nd = d - h * (2.0*Math.PI) - Math.PI;
				hhz = nd;
			}*/
			
			d = (np.y - p.y) / 100.0 + hhx + Math.PI;
			h = Math.floor( d / (2.0*Math.PI) );
			nd = d - h * (2.0*Math.PI) - Math.PI;
			hhx = nd;
		}
		
		shift = false;
		p = null;
		c.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//System.err.println( KeyEvent.getModifiersExText(e.getModifiersEx()) );
		if( e.getKeyChar() == '+' ) {
			zoomval -= 100;
		} else if( e.getKeyChar() == '-' ) {
			zoomval += 100;
		} else if( e.getKeyChar() == '*' ) {
			dzoomval -= 100;
		} else if( e.getKeyChar() == '/' ) {
			dzoomval += 100;
		} else if( e.getKeyChar() == ',' ) {
			dsize *= 0.8;
		} else if( e.getKeyChar() == '.' ) {
			dsize *= 1.25;
		} else if( e.getKeyChar() == ' ' ) {
			updateCenterOfMass();
		} else if( linkCorp != null ) {
			if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( strset.size() == 0 ) linkCorp.connections.remove( linkCorp2 );
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
				try {
					linkCorp.save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.length() > 0 ) {
					linkCorp.selectedLink = linkCorp.selectedLink.substring(0, linkCorp.selectedLink.length()-1);
					strset.add( linkCorp.selectedLink );
				} /*else if( strset.size() == 0 ) {
					linkCorp.connections.remove( linkCorp2 );
				}*/
			} else if( e.getKeyCode() != KeyEvent.VK_ALT && e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_SHIFT ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 ).linkTitles;
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.equals("link") ) linkCorp.selectedLink = "";
				linkCorp.selectedLink += e.getKeyChar();
				strset.add( linkCorp.selectedLink );
			}
		} else {
			if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
				Set<Corp>	delset = new HashSet<Corp>();
				for( Corp c : Corp.corpList ) {
					//Corp c = Corp.corpMap.get(name);
					if( c.selected ) {
						delset.add( c );
					}
				}
				
				for( Corp c : delset ) {
					c.delete();
				}
			}
		}
		
		c.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		Connectron cntr = new Connectron();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setSize(800, 600);
		try {
			cntr.initGUI( f );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		f.add( cntr.scrollpane );
		f.setVisible(true);
	}
}
