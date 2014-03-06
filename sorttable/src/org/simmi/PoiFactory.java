package org.simmi;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
//import javax.jnlp.UnavailableServiceException;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import netscape.javascript.JSObject;

/*import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.DetailPanel.PercStr;*/

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class PoiFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTable	table, topTable, leftTable;
	
	public String getAppletInfo() {
		return "PoiFactory";
	}
	
	public void init() {
		/*JLabel label = new JLabel("simmi");
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
					export( all[0], all[1], all[2] );
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
	
	public synchronized void hey( JTable table, JTable topTable, JTable leftTable ) {
		this.table = table;
		this.topTable = topTable;
		this.leftTable = leftTable;
		notify();
	}
	
	public void dummy() throws IOException {
		/*File tmp = File.createTempFile("tmp_", ".xlsx");
		Workbook	wb = new XSSFWorkbook();
		Sheet		sh = wb.createSheet("ISGEM");
		wb.write( new FileOutputStream( tmp ) );
		System.err.println( tmp.getName() );
		CompatUtilities.browse( tmp.toURI() );*/
	}
	
	public static void export( JTable table, JTable topTable, JTable leftTable, Applet applet ) throws FileNotFoundException, IOException {
		//File tmp = File.createTempFile("tmp_", ".xlsx");
		/*Workbook	wb = new XSSFWorkbook();
		Sheet		sh = wb.createSheet("ISGEM");
		Row			rw1 = sh.createRow(0);
		Row			rw2 = sh.createRow(1);
		int 		i = 2;
		for( int c : table.getSelectedColumns() ) {
			Cell cell1 = rw1.createCell( i );
			Cell cell2 = rw2.createCell( i );
			String name = (String)topTable.getValueAt(0, c);
			String unit = (String)table.getColumnName(c);
			cell1.setCellValue( name );
			cell2.setCellValue( unit );
			i++;
		}
		
		int ir = 2;
		for( int r : table.getSelectedRows() ) {
			Row row = sh.createRow( ir );
			row.createCell(0).setCellValue( (String)leftTable.getValueAt(r, 0) );
			row.createCell(1).setCellValue( (String)leftTable.getValueAt(r, 1) );
			
			i = 2;
			for( int c : table.getSelectedColumns() ) {
				Float f = (Float)table.getValueAt(r, c);
				if( f != null ) {
					double d = Math.round(f*100.0)/100.0;
					row.createCell(i).setCellValue( d );
				}
				i++;
			}
			
			ir++;
		}
		
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
    	wb.write( baos );
	    byte[] bb = baos.toByteArray();
	    String str = Base64.encode(bb);
	    String datauri = "data:application/vnd.ms-excel;name=export.xlsx;base64,"+str;
	    
	    boolean suc = true;
	    JSObject js = JSObject.getWindow(applet);
	    try {
		    js.eval( "location.href = '"+datauri+"'" );
	    } catch( Exception e ) {
	    	suc = false;
	    	e.printStackTrace();
	    }
		
	    if( !suc ) {
			FileSaveService fss; 
		    try {  
		        fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService"); 
		    } catch (Exception e) { 
		        fss = null; 
		    }
		    
		    if( fss != null ) {
		        try {
		            FileContents newfc = fss.saveFileDialog(null, new String[] {"xlsx"}, new ByteArrayInputStream( bb ), "export.xlsx");
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    } else {
		    	JFileChooser filechooser = new JFileChooser();
		    	if( filechooser.showSaveDialog( applet ) == JFileChooser.APPROVE_OPTION ) {
		    		File f = filechooser.getSelectedFile();
		    		FileOutputStream fw = new FileOutputStream( f );
		    		fw.write( bb );
		    		fw.close();
		    	}
		    }
			
			/*ExtendedService es;
		    try {  
		        es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
		    } catch (UnavailableServiceException e) {
		        es = null; 
		    }
	
		    //File tmp = new File("c:\\temp", "export.xlsx");
		    //FileContents fc = es.openFile( tmp );
		    
		    if( fc.canWrite() ) {
		    	wb.write( fc.getOutputStream(true) );
		    }
		    
		    try {
				Desktop.getDesktop().browse( new URI("file://c:/temp/export.xlsx") );
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}*
	    }      */
        
        //js.call("open", new Object[] {datauri});
	        
	    //}
	    
		//wb.write( new FileOutputStream( tmp ) );
		//System.err.println( tmp.getName() );
		
	    //if( applet.getAppletContext().)*/
	}

	public static void run2( JTable detailTable, JTable leftTable ) throws FileNotFoundException, IOException {
		//File tmp = File.createTempFile("tmp_", ".xlsx");
		/*Workbook	wb = new XSSFWorkbook();
		Sheet		sh = wb.createSheet("ISGEM");
		
		int rsel = leftTable.getSelectedRow();
		if( rsel != -1 ) {
			Row			rw1 = sh.createRow(0);
			Row			rw2 = sh.createRow(1);
			
			String s1 = (String)leftTable.getValueAt(rsel, 0);
			String s2 = (String)leftTable.getValueAt(rsel, 1);
			
			rw1.createCell(0).setCellValue( s1 );
			rw2.createCell(0).setCellValue( s2 );
		}
		
		Row			rw = sh.createRow(2);
		int 		i = 0;
		for( int c = 0; c < detailTable.getColumnCount()-1; c++ ) {
			Cell cell = rw.createCell( i );
			String name = (String)detailTable.getColumnName(c);
			cell.setCellValue( name );
			i++;
		}
		
		int ir = 3;
		for( int r : detailTable.getSelectedRows() ) {
			Row row = sh.createRow( ir );
			
			i = 0;
			for( int c = 0; c < detailTable.getColumnCount()-1; c++ ) {
				Object o = detailTable.getValueAt(r, c);
				if( o != null ) {
					if( o instanceof Float ) {
						double d = Math.round((Float)o*100.0)/100.0;
						row.createCell(i).setCellValue( d );
					} else if( o instanceof Double ) {
						double d = Math.round((Double)o*100.0)/100.0;
						row.createCell(i).setCellValue( d );
					} else if( o instanceof Integer ) {
						double d = Math.round((Integer)o*100.0)/100.0;
						row.createCell(i).setCellValue( d );
					} else if( o instanceof String ) {
						row.createCell(i).setCellValue( (String)o );
					} else if( o instanceof PercStr ) {
						PercStr ps = (PercStr)o;
						row.createCell(i).setCellValue( ps.toString() );
					}
				}
				i++;
			}
			
			ir++;
		}
		
		FileSaveService fss; 
	    try {  
	        fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService"); 
	    } catch (Exception e) { 
	        fss = null; 
	    } 

	    System.err.println( "erm "+fss );
	    if (fss != null) {
	        try {
	        	PipedOutputStream po = new PipedOutputStream();
	    	    wb.write( po );
	            FileContents newfc = fss.saveFileDialog(null, null, new PipedInputStream( po ), "export.xlsx"); 
	        } catch (Exception e) { 
	            e.printStackTrace();
	        } 
	    } 
		//wb.write( new FileOutputStream( tmp ) );
		//System.err.println( tmp.getName() );
		//CompatUtilities.browse( tmp.toURI() );*/
	}
}
