package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdesktop.swingx.JXDatePicker;

public class Report extends JApplet {
	JButton 		pdfComp;
	JButton 		xlsComp;
	Image 			pdfImg;
	Image 			xlsImg;
	XSSFWorkbook 	workbook;
	Map<Long,String>	ledgerMap;
	
	JTable			table;
	TableModel		rowHeader;
	JScrollPane		summaryscrollpane;
	JTable			summarytable;
	TableModel		summarymodel;
	TableModel		model;
	
	MySorter		currentsorter;
	MySorter		summarysorter;
	MySorter		sorter;
	
	JScrollPane		scrollpane;
	JScrollPane		detailscrollpane;
	
	JXDatePicker	after;
	JXDatePicker	before;

	public Report() throws IOException {
		super();
		
		ledgerMap = new HashMap<Long,String>();
		ledgerMap.put((long)103, "1050");
		ledgerMap.put((long)106, "1050");
		ledgerMap.put((long)20001, "1040");
		ledgerMap.put((long)20002, "1040");

		/*try {
			xlsImg = ImageIO.read(this.getClass().getResource("/xlsx.png"));
			pdfImg = ImageIO.read(this.getClass().getResource("/pdf.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		ImageProducer 	ims = (ImageProducer)this.getClass().getResource("/xlsx.png").getContent();
		xlsImg = (ims != null ? this.createImage( ims ).getScaledInstance(16, 16, Image.SCALE_SMOOTH) : null);
		ims = (ImageProducer)this.getClass().getResource("/pdf.png").getContent();
		pdfImg = (ims != null ? this.createImage( ims ).getScaledInstance(16, 16, Image.SCALE_SMOOTH) : null);
	}

	public Report(String[] args) {
		super();

		/*
		 * try { loadXlsx("/home/sigmar/Desktop/Kennitölur 270509.xlsx"); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		load(args[0]);
	}
	
	public void setDatePickers( JXDatePicker before, JXDatePicker after ) {
		this.before = before;
		this.after = after;
	}

	class Job {
		String no;
		String desc;
		String boss;
		Date start;
		Date end;

		public Job(String no, String desc, String boss, Date start, Date end) {
			this.no = no;
			this.desc = desc;
			this.boss = boss;
			this.start = start;
			this.end = end;
		}
	};

	class Cost {
		long	no;
		String	nostr;
		String 	subno;
		String 	type;
		String 	name;
		double 	c;
		double 	p;

		public Cost(String nostr, String type, String name, double c, double p, String subno) {
			this.nostr = nostr;
			this.type = type;
			this.name = name;
			this.c = c;
			this.p = p;
			this.subno = subno;
			
			no = -1;
			try {
				no = Long.parseLong(nostr);
			} catch( Exception e ) {
				System.err.println( "nostr " + nostr );
			}
		}
	};

	Map<String, Cost> costMap = new HashMap<String, Cost>();
	List<Cost> costList = new ArrayList<Cost>();

	public void loadXlsx(String fname) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(fname);
		XSSFSheet sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
		XSSFRow row = sheet.getRow(12);

		if (row == null)
			row = sheet.createRow(12);
		XSSFCell cell = row.getCell(2);
		if (cell == null)
			cell = row.createCell(12);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue("siiiiimmmmmmmmmmmmmmmmmmmmmmmmmmmmmi");

		workbook.write(new FileOutputStream("/home/sigmar/Desktop/simmi2.xlsx"));
	}
	
	public class Result {
		Map<String,List<Cost>>	realCost = new HashMap<String,List<Cost>>();
		Map<String,List<Cost>>	estCost = new HashMap<String,List<Cost>>();
		
		public Result() {
			
		}
		
		public void addRealCost( String jobno, Cost cost ) {
			List<Cost>	costList;
			if( !realCost.containsKey( jobno ) ) {
				costList = new ArrayList<Cost>();
				realCost.put( jobno, costList );
			} else costList = realCost.get( jobno );
			
			costList.add( cost );
		}
		
		public void addEstCost( String jobno, Cost cost ) {
			List<Cost>	costList;
			if( !estCost.containsKey( jobno ) ) {
				costList = new ArrayList<Cost>();
				estCost.put( jobno, costList );
			} else costList = estCost.get( jobno );
			
			costList.add( cost );
		}
	}
	
	public Result	loadAll() throws ClassNotFoundException, SQLException {			
		final Result	res = new Result();
		
		String str = "('"+(String)model.getValueAt(0, 1);
		for( int r = 1; r < model.getRowCount(); r++ ) {
			String val = (String)model.getValueAt(r, 1);
			str += "', '"+val;
		}
		str += "')";
		
		String sql = "select be.\"Job No_\", be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(be.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" in "
				+ str
				+ " and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" group by be.\"Job No_\", be.No_, be.Type, bl.Description";

		System.err.println( "executing job " + str );
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		int i = 0;
		while (rs.next()) {
			String jobno = rs.getString(1);
			String nostr = rs.getString(2);
			Cost cost = new Cost(nostr, rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getDouble(6), null);
			res.addEstCost( jobno, cost );
			
			i++;
			if( i % 10 == 0 ) System.err.println( i );
		}
		rs.close();
		ps.close();
		
		sql = "select le.\"Job No_\", le.No_, ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge "
				+ "where le.\"Job No_\" in " + str
				+ "and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by le.\"Job No_\", le.No_, ge.\"Sales Account\", le.Type";

		System.err.println( "executing real " + str );
		
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		i = 0;
		while (rs.next()) {
			String jobno = rs.getString(1);
			String nostr = rs.getString(3);
			if( nostr.equals("") ) nostr = rs.getString(2);
			Cost cost = new Cost(nostr, rs.getString(4), "", rs.getDouble(5), rs.getDouble(6), null);
			res.addRealCost( jobno, cost );
			
			i++;
			if( i % 10 == 0 ) System.err.println( i );
		}
		rs.close();
		ps.close();
		
		return res;
	}
	
	Connection con = null;
	public Result	loadSingle( String str ) throws ClassNotFoundException, SQLException {			
		final Result	res = new Result();
		//List<Cost>			realList = res.realCost;
		//Lst<Cost>			estList = res.estCost;
		
		String sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(be.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = "
				+ str
				+ " and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" group by be.No_, be.Type, bl.Description";

		System.err.println( "executing job " + str );
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			String jobno = rs.getString(1);
			String nostr = rs.getString(2);
			Cost cost = new Cost(nostr, rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getDouble(6), null);
			res.addEstCost( jobno, cost );
		}
		rs.close();
		ps.close();
		
		sql = "select le.No_, ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge "
				+ "where le.\"Job No_\" = " + str
				+ "and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by le.No_, ge.\"Sales Account\", le.Type";

		System.err.println( "executing real " + str );
		
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			String jobno = rs.getString(1);
			String nostr = rs.getString(3);
			if( nostr.equals("") ) nostr = rs.getString(2);
			Cost cost = new Cost(nostr, rs.getString(4), "", rs.getDouble(5), rs.getDouble(6), null);
			res.addRealCost( jobno, cost );
		}
		rs.close();
		ps.close();
		
		return res;
	}
	
	Map<String,Result>	resMap = new HashMap<String,Result>();
	public TableModel calcModel() throws ClassNotFoundException, SQLException {		
		final List<Result>	reslist = new ArrayList<Result>();
		Set<String>		rest = new HashSet<String>();
		
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			Object val = table.getValueAt(r, 1);
			if( resMap.containsKey( val ) ) {
				reslist.add( resMap.get( val ) );
			} else rest.add( val.toString() );
		}
		
		for( String str : rest ) {
			Result res = new Result();
			resMap.put( str, res );
		}
		
		int k = 3;
		for( String str : rest ) {
			Result res = resMap.get(str);
			//List<Cost>	costList = res.realCost;
			
			String sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(be.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = "
					+ str
					+ " and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" group by be.No_, be.Type, bl.Description";

			System.err.println( "executing job " + str );
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			ResultSet 			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();
		}

		k = 2;
		for (String str : rest) {
			Result res = resMap.get(str);
			//List<Cost>	costList = res.realCost;
			
			String sql = "select le.No_, ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge "
					+ "where le.\"Job No_\" = " + str
					+ "and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by le.No_, ge.\"Sales Account\", le.Type";

			System.err.println( "executing real " + str );
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			ResultSet			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(2);
				if( nostr.equals("") ) nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(3), "", rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();
			
			k += 2;
		}
		
		model = new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return reslist.size();
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex % 2 == 0 ) return "Raun";
				else return "Áætlun";
			}

			@Override
			public int getRowCount() {
				return rowHeader.getRowCount();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String ret = "";
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				
			}
		}; 
		return model;
	}
	
	public String getStartDate() {
		String aftstr = (after == null || after.getDate() == null) ? null : (after.getDate().getYear()+1900) + "-" + (after.getDate().getMonth()+1) + "-" + after.getDate().getDate();
		return aftstr;
	}
	
	public String getEndDate() {
		String befstr = (before == null || before.getDate() == null) ? null : (before.getDate().getYear()+1900) + "-" + (before.getDate().getMonth()+1) + "-" + before.getDate().getDate();
		return befstr;
	}
	
	public void plan( List<Object> jobstr, Sheet sheet ) throws SQLException {
		int k = 3;
		
		String startDate = getStartDate();
		String endDate = getEndDate();
		
		Row arow = sheet.getRow(3);
		Cell acell = null;
		if( arow == null ) {
			arow = sheet.createRow( 3 );
			acell = arow.createCell(1);
		} else {
			acell = arow.getCell(1);
			if( acell == null ) acell = arow.createCell( 1 );				
		}
		acell.setCellValue( "Verkefni" );
		
		arow = sheet.getRow(4);
		acell = null;
		if( arow == null ) {
			arow = sheet.createRow( 4 );
			acell = arow.createCell(1);
		} else {
			acell = arow.getCell(1);
			if( acell == null ) acell = arow.createCell( 1 );				
		}
		acell.setCellValue( "Verkefnastjóri" );
		
		arow = sheet.getRow(5);
		acell = null;
		if( arow == null ) {
			arow = sheet.createRow( 5 );
			acell = arow.createCell(1);
		} else {
			acell = arow.getCell(1);
			if( acell == null ) acell = arow.createCell( 1 );				
		}
		acell.setCellValue( "Upphafs dags." );
		
		arow = sheet.getRow(6);
		acell = null;
		if( arow == null ) {
			arow = sheet.createRow( 6 );
			acell = arow.createCell(1);
		} else {
			acell = arow.getCell(1);
			if( acell == null ) acell = arow.createCell( 1 );				
		}
		acell.setCellValue( "Enda dags." );		
		
		for (Object o : jobstr) {
			String sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(be.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = "
					+ o.toString()
					+ " and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\"";
			
			if( startDate != null ) sql += " and be.Date >= '"+startDate+"'";
			if( endDate != null ) sql += " and be.Date <= '"+endDate+"'";
			sql += " group by be.No_, be.Type, bl.Description";

			System.err.println( "executing job " + o.toString() );
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			ResultSet			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();

			arow = sheet.getRow(8);
			acell = null;
			if( arow == null ) {
				arow = sheet.createRow( 8 );
				acell = arow.createCell(k);
			} else {
				acell = arow.getCell(k);
				if( acell == null ) acell = arow.createCell( k );				
			}
			acell.setCellValue("Áætlun");
			
			for( int i = 9; i < 40; i++ ) {
				Row row = sheet.getRow(i);
				if (row == null) row = sheet.createRow( i );
				
				boolean unsucc = true;

				Cell cell = row.getCell(0);
				if (cell == null) {
					cell = row.createCell(0);
					Cell cell2 = row.createCell(1);
					
					if( i-9 == 0 ) {
						cell2.setCellValue( "Kostnaður v/vinnu" );
					} else if( i-9 == 2 ) {
						cell.setCellValue( 2310 );
						cell2.setCellValue( "Rannsóknarstofuefni" );
					} else if( i-9 == 3 ) {
						cell.setCellValue( 2311 );
						cell2.setCellValue( "Aðföng rekstarvörur" );
					} else if( i-9 == 4 ) {
						cell.setCellValue( 2396 );
						cell2.setCellValue( "Mælingar á milli deilda" );
					} else if( i-9 == 5 ) {
						cell.setCellValue( 2498 );
						cell2.setCellValue( "Sérfræðiþjónusta samtals" );
					} else if( i-9 == 6 ) {
						cell.setCellValue( 2598 );
						cell2.setCellValue( "Ferða- og uppihaldsk samtals" );
					} else if( i-9 == 7 ) {
						cell.setCellValue( 2710 );
						cell2.setCellValue( "Styrkir til nemenda" );
					} else if( i-9 == 9 ) {
						cell.setCellValue( 2999 );
						cell2.setCellValue( "Almennur rekstrarkostnaður samtals" );
					} else if( i-9 == 10 ) {
						cell2.setCellValue( "Kostnaður samtals" );
					} else if( i-9 == 12 ) {
						cell.setCellValue( 1010 );
						cell2.setCellValue( "Fyrirtæki innlend m/vsk" );
					} else if( i-9 == 13 ) {
						cell.setCellValue( 1012 );
						cell2.setCellValue( "Fyrirtæki innlend án/vsk" );
					} else if( i-9 == 14 ) {
						cell.setCellValue( 1013 );
						cell2.setCellValue( "Opinberir aðilar m/vsk" );
					} else if( i-9 == 15 ) {
						cell.setCellValue( 1015 );
						cell2.setCellValue( "Opinberir aðilar án/vsk" );
					} else if( i-9 == 16 ) {
						cell.setCellValue( 1030 );
						cell2.setCellValue( "Erlendir sjóðir" );
					} else if( i-9 == 17 ) {
						cell.setCellValue( 1031 );
						cell2.setCellValue( "Erlend fyrirtæki" );
					} else if( i-9 == 18 ) {
						cell.setCellValue( 1040 );
						cell2.setCellValue( "Sjóðir innlent" );
					} else if( i-9 == 19 ) {
						cell.setCellValue( 1050 );
						cell2.setCellValue( "Sjávarútvegs- og landb" );
					} else if( i-9 == 20 ) {
						cell.setCellValue( 1060 );
						cell2.setCellValue( "Sala án/vsk" );
					} else if( i-9 == 21 ) {
						cell.setCellValue( 1096 );
						cell2.setCellValue( "Sala milli deilda" );
					} else if( i-9 == 23 ) {
						cell.setCellValue( 1993 );
						cell2.setCellValue( "Rekstrartekjur samtals" );
					} else if( i-9 == 24 ) {
						cell2.setCellValue( "Afkoma (v/útselds taxta)" );
					}
				}
				
				int d = (int) cell.getNumericCellValue();
				String dstr = Integer.toString(d);
				if (d > 0) {
					if (costMap.containsKey(dstr)) {
						Cost cost = costMap.get(dstr);
						cell = row.getCell(k);
						if( cell == null ) cell = row.createCell(k);
						if (d >= 1000 && d < 2000)
							cell.setCellValue(cost.p);
						else
							cell.setCellValue(cost.c);
					} else {
						double tot = 0.0;

						if (dstr.endsWith("99")) {
							for (Cost c : costList) {
								if (c.no >= d - 999 && c.no < d + 1)
									tot += c.c;
							}
						} else if (dstr.endsWith("98")) {
							for (Cost c : costList) {
								if (c.no >= d - 98 && c.no < d + 2)
									tot += c.c;
							}
						} else if (dstr.equals("1993")) {
							for (Cost c : costList) {
								if (c.no >= d - 993 && c.no < d + 7)
									tot += c.p;
							}
						}
						cell = row.getCell(k);
						if( cell == null ) cell = row.createCell(k);
						cell.setCellValue(tot);
					}

					unsucc = false;
				}

				if (unsucc) {
					cell = row.getCell(1);
					if( cell == null ) {
						cell = row.createCell(1);
						cell.setCellValue( (String)rowHeader.getValueAt(i-9, 0) );
					}
					
					if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
						dstr = cell.getStringCellValue();
						if (dstr.equals("Kostnaður samtals")) {
							double tot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;
								else tot += c.c;
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(tot);
						} else if (dstr.equals("Kostnaður v/vinnu")) {
							double tot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(tot);
						} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
							double tot = 0.0;
							double ctot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;
								//else if( c.type.contains("1") ) ctot += c.p;
								else tot += c.c;
								
								if (c.no >= 1000 && c.no < 2000 ) {
									ctot += c.p;
								}
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(ctot-tot);
						}
					}
				}
			}

			sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = "
					+ o.toString() + " and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\"";
			
			if( endDate != null ) sql += " and be.Date <= '"+endDate+"'";
			sql += " group by be.No_, be.Type, bl.Description";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			int count = 0;
			while (rs.next()) {
				count++;
				//System.err.print( (count) + " " );
				String nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();

			for( int i = 40; i < 100; i++) {
				Row row = sheet.getRow(i);
				if (row == null) row = sheet.createRow(i);
				
				boolean unsucc = true;

				Cell cell = row.getCell(0);
				if( cell == null ) {
					cell = row.createCell(0);
					Cell	cell2 = row.createCell(1);
					
					if( i-40 == 0 ) {
						cell2.setCellValue( "Kostnaður v/vinnu" );
					} else if( i-40 == 2 ) {
						cell.setCellValue( 2310 );
						cell2.setCellValue( "Rannsóknarstofuefni" );
					} else if( i-40 == 3 ) {
						cell.setCellValue( 2311 );
						cell2.setCellValue( "Aðföng rekstarvörur" );
					} else if( i-40 == 4 ) {
						cell.setCellValue( 2396 );
						cell2.setCellValue( "Mælingar á milli deilda" );
					} else if( i-40 == 5 ) {
						cell.setCellValue( 2498 );
						cell2.setCellValue( "Sérfræðiþjónusta samtals" );
					} else if( i-40 == 6 ) {
						cell.setCellValue( 2598 );
						cell2.setCellValue( "Ferða- og uppihaldsk samtals" );
					} else if( i-40 == 7 ) {
						cell.setCellValue( 2710 );
						cell2.setCellValue( "Styrkir til nemenda" );
					} else if( i-40 == 9 ) {
						cell.setCellValue( 2999 );
						cell2.setCellValue( "Almennur rekstrarkostnaður samtals" );
					} else if( i-40 == 10 ) {
						cell2.setCellValue( "Kostnaður samtals" );
					} else if( i-40 == 12 ) {
						cell.setCellValue( 1010 );
						cell2.setCellValue( "Fyrirtæki innlend m/vsk" );
					} else if( i-40 == 13 ) {
						cell.setCellValue( 1012 );
						cell2.setCellValue( "Fyrirtæki innlend án/vsk" );
					} else if( i-40 == 14 ) {
						cell.setCellValue( 1013 );
						cell2.setCellValue( "Opinberir aðilar m/vsk" );
					} else if( i-40 == 15 ) {
						cell.setCellValue( 1015 );
						cell2.setCellValue( "Opinberir aðilar án/vsk" );
					} else if( i-40 == 16 ) {
						cell.setCellValue( 1030 );
						cell2.setCellValue( "Erlendir sjóðir" );
					} else if( i-40 == 17 ) {
						cell.setCellValue( 1031 );
						cell2.setCellValue( "Erlend fyrirtæki" );
					} else if( i-40 == 18 ) {
						cell.setCellValue( 1040 );
						cell2.setCellValue( "Sjóðir innlent" );
					} else if( i-40 == 19 ) {
						cell.setCellValue( 1050 );
						cell2.setCellValue( "Sjávarútvegs- og landb" );
					} else if( i-40 == 20 ) {
						cell.setCellValue( 1060 );
						cell2.setCellValue( "Sala án/vsk" );
					} else if( i-40 == 21 ) {
						cell.setCellValue( 1096 );
						cell2.setCellValue( "Sala milli deilda" );
					} else if( i-40 == 23 ) {
						cell.setCellValue( 1993 );
						cell2.setCellValue( "Rekstrartekjur samtals" );
					} else if( i-40 == 24 ) {
						cell2.setCellValue( "Afkoma (v/útselds taxta)" );
					}
				}
				
				if (cell != null) {
					int d = (int) cell.getNumericCellValue();
					String dstr = Integer.toString(d);
					if (d > 0) {
						if (costMap.containsKey(dstr)) {
							Cost cost = costMap.get(dstr);
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							if (d >= 1000 && d < 2000)
								cell.setCellValue(cost.p);
							else
								cell.setCellValue(cost.c);
						} else {
							double tot = 0.0;

							if (dstr.endsWith("99")) {
								for (Cost c : costList) {
									if (c.no >= d - 999 && c.no < d + 1)
										tot += c.c;
								}
							} else if (dstr.endsWith("98")) {
								for (Cost c : costList) {
									if (c.no >= d - 98 && c.no < d + 2)
										tot += c.c;
								}
							} else if (dstr.equals("1993")) {
								for (Cost c : costList) {
									if (c.no >= d - 993 && c.no < d + 7)
										tot += c.p;
								}
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(tot);
						}

						unsucc = false;
					}
				}

				if (unsucc) {
					cell = row.getCell(1);
					if( cell == null ) {
						cell = row.createCell(1);
						cell.setCellValue( (String)rowHeader.getValueAt(i-40, 0) );
					}
					
					if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
						String dstr = cell.getStringCellValue();
						if (dstr.equals("Kostnaður samtals")) {
							double tot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;
								else tot += c.c;
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(tot);
						} else if (dstr
								.equals("Kostnaður v/vinnu")) {
							double tot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;									
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(tot);
						} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
							double tot = 0.0;
							double ctot = 0.0;
							for (String no : costMap.keySet()) {
								Cost c = costMap.get(no);
								if( c.type.contains("0") ) tot += c.p;
								//else if( c.type.contains("1") ) ctot += c.p;
								else tot += c.c;
								
								if (c.no >= 1000 && c.no < 2000 ) {
									ctot += c.p;
								}
							}
							cell = row.getCell(k);
							if( cell == null ) cell = row.createCell(k);
							cell.setCellValue(ctot-tot);
						}
					}
				}	
			}

			k += 2;
		}
	}
	
	public void collectSomething( Row row, Sheet sheet, String sql ) throws SQLException {
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		Cell[] lastGoodCell = null;
		Cell[] lastGoodCell2 = null;
		int i = 2;
		
		Cell cell = row.getCell(i);
		while( rs.next() ) {
			String val = rs.getString(1);
			
			if( cell == null ) {
				if( lastGoodCell != null ) {
					for( int k = 2; k < 100; k++ ) {
						Row		rr = sheet.getRow(k);
						if( rr != null ) {
							Cell 	cc = lastGoodCell[k];
							Cell 	cc2 = lastGoodCell2[k];
							Cell 	oc = rr.createCell(i);
							Cell 	oc2 = rr.createCell(i+1);
							
							Comment comment;
							CellStyle style;
							//String rawValue;
							RichTextString	rawValue;
							int type;
							if( cc != null ) {
								comment = cc2.getCellComment();
								if( comment != null ) oc2.setCellComment( comment );
								style = cc2.getCellStyle();
								if( style != null ) oc2.setCellStyle( style );
								type = cc2.getCellType();
								oc2.setCellType( type );
								rawValue = cc2.getRichStringCellValue();
								if( rawValue != null ) {
									oc2.setCellValue( rawValue );
									//oc2.setRawValue( rawValue );
								}
								
								comment = cc.getCellComment();
								if( comment != null ) oc.setCellComment( comment );
								style = cc.getCellStyle();
								if( style != null ) oc.setCellStyle( style );
								type = cc.getCellType();
								oc.setCellType( type );
								rawValue = cc.getRichStringCellValue(); //cc.getRawValue();
								if( rawValue != null ) {
									oc.setCellValue( rawValue );
									//oc.setRawValue( rawValue );
								}
							}
							
							if( k >= 2 && k <= 6 ) {
								sheet.addMergedRegion( new CellRangeAddress(k, k, i, i+1) );
							}
						}
					}
				}
				cell = row.getCell(i);
				if( cell == null ) cell = row.createCell(i);
			} else if( lastGoodCell == null ) {
				lastGoodCell = new XSSFCell[100];
				lastGoodCell2 = new XSSFCell[100];
				for( int k = 2; k < 100; k++ ) {
					Row	rr = sheet.getRow(k);
					if( rr != null ) {
						lastGoodCell[k] = rr.getCell(i);
						lastGoodCell2[k] = rr.getCell(i+1);
					}
				}
			}
			cell.setCellValue( val );
			
			i += 2;
			cell = row.getCell(i);
		}
		rs.close();
	}

	public void load(String filename) {
		try {
			Sheet 	sheet;
			Row		row;
			Cell	cell;
			String 	str = null;
			if( filename == null ) {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet("Report");
				row = sheet.createRow(0);
				cell = row.createCell(1);
				
				row = sheet.createRow(2);
			} else {
				workbook = new XSSFWorkbook(filename);
				sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
				row = sheet.getRow(0);
				cell = row.getCell(1);
				
				String cellv = cell.getStringCellValue();
				String[] splt = cellv.split(":");
				if( splt.length > 1 ) {
					str = splt[1].trim();
				}
				row = sheet.getRow(2);
			}
			
			List<Object> jobstr = new ArrayList<Object>();
			if( str == null ) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					Object jstr = table.getValueAt(r, 1);
					jobstr.add( jstr );
					
					if( str == null ) str = "("+jstr;
					else str += ","+jstr;
				}
				str += ")";
				//String sql = "SELECT No_ FROM \"Matís ohf_$Job\" where [No_] in "+str+" and \"Job Posting Group\" != 'INNSELD' and \"Completion Date\" < '1900-01-01'";
				//collectSomething( row, sheet, sql );
			} else {
				String sql = "SELECT No_ FROM \"Matís ohf_$Job\" where \"Global Dimension 1 Code\" = '"+str+"' and \"Job Posting Group\" != 'INNSELD' and \"Completion Date\" < '1900-01-01'";
				collectSomething( row, sheet, sql );
				
				int i = 2;
				cell = row.getCell(i);
				while (cell != null && ((cell.getCellType() == Cell.CELL_TYPE_NUMERIC && cell.getNumericCellValue() > 0) || (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().length() > 0))) {
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						jobstr.add((int) cell.getNumericCellValue());
					} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						String val = cell.getStringCellValue();
						if (!val.equals("xxx"))
							jobstr.add(val);
					}
					i += 2;
					cell = row.getCell(i);
				}
		
				str = "(";
				for (Object o : jobstr) {
					if (o instanceof Integer) {
						str += "'" + Integer.toString((Integer) o) + "'";
					} else if (o instanceof String) {
						String res = (String) o;
						str += "'" + res + "'";
					}
					if (!o.equals(jobstr.get(jobstr.size() - 1)))
						str += ", ";
				}
				str += ")";
			}
	
			String sql = "select * from dbo.job_excel where No_ in " + str;
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
	
			// List<Job> jobs = new ArrayList<Job>();
			//i = 0;
			while (rs.next()) {
				int i = 0;
				String job = rs.getString(1);
				while( i < jobstr.size() && !job.contains( jobstr.get(i).toString() ) ) i++;
				
				if( i < jobstr.size() ) {
					row = sheet.getRow(3);
					if( row == null ) row = sheet.createRow(3);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getString(2));
		
					row = sheet.getRow(4);
					if( row == null ) row = sheet.createRow(4);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getString(3));
		
					row = sheet.getRow(5);
					if( row == null ) row = sheet.createRow(5);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getDate(4).toString());
		
					row = sheet.getRow(6);
					if( row == null ) row = sheet.createRow(6);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getDate(5).toString());
					// jobs.add( new Job( rs.getString(1), rs.getString(2),
					// rs.getString(3), rs.getDate(4), rs.getDate(5) ) );
					//i += 2;
				} else {
					System.err.println( job + " not found" );
				}
			}
			rs.close();
			ps.close();
			
			/*String startDate; // = getStartDate(); //"2009-01-01";
			String endDate; // = getEndDate(); //"2009-04-01";
			
			row = sheet.getRow(7);
			if( row == null ) {
				row = sheet.createRow(7);
				cell = row.createCell(1);
			} else {
				cell = row.getCell(1);
				String dateVal = cell.getStringCellValue();
				String[] splVal = dateVal.split(":");
				if( splVal.length > 0 ) {
					String[] dDelta = splVal[1].split("-");
					String[] startSplit = dDelta[0].trim().split("\\.");
					String[] endSplit = dDelta[1].trim().split("\\.");
					
					if( startSplit[2].length() == 2 ) {
						startDate = "20" + startSplit[2] + "-" + startSplit[1] + "-" + startSplit[0];
					}
					
					if( endSplit[2].length() == 2 ) {
						endDate = "20" + endSplit[2] + "-" + endSplit[1] + "-" + endSplit[0];
					}
				}
			}*/
	
			/********** plan ***************/
			plan( jobstr, sheet );
	
			System.err.println( "fetching real data" );
			/************ real ******************/
			real( jobstr, sheet );
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// workbook.write( new FileOutputStream("/mnt/tmp/simmi.xlsx") );
	}
	
	public void real( List<Object> jobstr, Sheet sheet ) throws SQLException {
		int k = 2;
		String startDate = getStartDate();
		String endDate = getEndDate();
		for (Object o : jobstr) {
			// sql =
			// "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Ledger Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"+o.toString()+"' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.\"Posting Date\" >= '2009-01-01' and be.\"Posting Date\" < '2009-04-01' group by be.No_, be.Type, bl.Description";
			String sql = "select le.No_, ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge "
					+ "where le.\"Job No_\" = " + o.toString();
			
			if( startDate != null ) sql += " and le.\"Posting Date\" >= '"+startDate+"'";
			if( endDate != null ) sql += " and le.\"Posting Date\" <= '"+endDate+"'";
			sql += " and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by le.No_, ge.\"Sales Account\", le.Type";

			System.err.println( "executing real " + o.toString() );
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			ResultSet 			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(2);
				if( nostr.equals("") ) nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(3), "", rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();
			
			Row arow = sheet.getRow(8);
			Cell acell = null;
			if( arow == null ) {
				arow = sheet.createRow( 8 );
				acell = arow.createCell(k);
			} else {
				acell = arow.getCell(k);
				if( acell == null ) acell = arow.createCell( k );				
			}
			acell.setCellValue("Raun");

			for ( int i = 9; i < 40; i++ ) {
				Row row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					Cell cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} /*else if( ledgerMap.containsValue(dstr) ) {
								Set<Entry<Long,String>>	entr = ledgerMap.entrySet();
								double tot = 0.0;
								for (Cost c : costList) {
									boolean check = false;
									if( c.type.equals("1") ) {
										for( Entry<Long,String> e : entr ) {
											if( e.getValue().equals(dstr) && c.no == d ) {
												check = true;
												break;
											}
										}
										if( check ) {
											tot += c.p;
										}
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							}*/ else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if( c.type.contains("1") ) {
											tot += c.p;
										}
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}

					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.p;
									else tot += c.c;
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							} else if (dstr.equals("Kostnaður v/vinnu")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.p;
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
								double tot = 0.0;
								double ctot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.p;
									//else if( c.type.contains("1") ) ctot += c.p;
									else tot += c.c;
									
									if (c.no >= 1000 && c.no < 2000 ) {
										ctot += c.p;
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(-ctot-tot);
							}
						}
					}
				}
			}

			sql = "select le.No_, ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge where le.\"Job No_\" = "
					+ o.toString();
			if( endDate != null ) sql += " and le.\"Posting Date\" <= '"+endDate+"'";
			sql += " and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by le.No_, ge.\"Sales Account\", le.Type";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(2);
				if( nostr.equals("") ) nostr = rs.getString(1);
				Cost cost = new Cost(nostr, rs.getString(3), "", rs.getDouble(4), rs.getDouble(5), null);
				costMap.put(nostr, cost);
				costList.add(cost);
			}
			rs.close();
			ps.close();

			for ( int i = 40; i < 100; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					Cell cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} /*else if( ledgerMap.containsValue(dstr) ) {
								Set<Entry<Long,String>>	entr = ledgerMap.entrySet();
								double tot = 0.0;
								for (Cost c : costList) {
									boolean check = false;
									if( c.type.equals("1") ) {
										for( Entry<Long,String> e : entr ) {
											if( e.getValue().equals(dstr) && e.getKey().equals(c.no) ) {
												check = true;
												break;
											}
										}
										if( check ) {
											tot += c.p;
										}
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							}*/ else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if( c.type.contains("1") ) {
											tot += c.p;
										}
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}
					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									
									if( c.type.contains("0") ) tot += c.p;
									else tot += c.c;
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							} else if (dstr.equals("Kostnaður v/vinnu")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.p;
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(tot);
							} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
								double tot = 0.0;
								double ctot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.p;
									//else if( c.type.contains("1") ) ctot += c.p;
									else tot += c.c;
									
									if( c.no >= 1000 && c.no < 2000 ) {
										ctot += c.p;
									}
								}
								cell = row.getCell(k);
								if( cell == null ) cell = row.createCell(k);
								cell.setCellValue(-ctot-tot);
							}
						}
					}
				}
			}

			//sheet.autoSizeColumn(k);
			//sheet.autoSizeColumn(k+1);
			
			k += 2;
		}
	}
	
	public class Svid {
		String 	svid;
		String	no;
		String	name;
		String	person;
		Date	start;
		Date	stop;
		
		public Svid( String svid, String no, String name, String person, Date start, Date stop ) {
			this.svid = svid;
			this.no = no;
			this.name = name;
			this.person = person;
			this.start = start;
			this.stop = stop;
		}
	};
	
	public TableModel createModel( final List<?> datalist ) {
		final Class cls = datalist.get(0).getClass();
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getClass();
			}

			@Override
			public int getColumnCount() {
				int cc = cls.getDeclaredFields().length-1;
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName();
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String ret = "";
				try {
					Field f = cls.getDeclaredFields()[columnIndex];
					if( ret != null ) {
						Object obj = f.get( datalist.get(rowIndex) );
						if( obj != null ) ret = obj.toString();
						/*else {
							System.err.println("null obj "+rowIndex + "  " + columnIndex);
						}*/
					} else {
						System.err.println("null field"+rowIndex + "  " + columnIndex);
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
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
	}
	
	public class MySorter extends TableRowSorter<TableModel> {
		public MySorter( TableModel model ) {
			super( model );
		}
		
		public int convertRowIndexToModelSuper(int index) {
			return super.convertRowIndexToModel( index );
		}

		public int convertRowIndexToViewSuper(int index) {
			return super.convertRowIndexToView( index );
		}
	};
	
	public TableModel createSummaryModel( final Result res, final Object[] matrix ) {
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}

			@Override
			public int getColumnCount() {
				return rowHeader.getRowCount();
			}

			@Override
			public String getColumnName(int columnIndex) {
				return rowHeader.getValueAt(columnIndex, 0).toString();
			}

			@Override
			public int getRowCount() {
				return model.getRowCount();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int i = rowIndex*rowHeader.getRowCount();
				if( matrix[ i ] == null ) {
					String str = (String)table.getValueAt(rowIndex, 1);
					List<Cost> costlist = res.realCost.get( str );
					
					if( costlist != null ) {
						double 	flaun = 0.0f;
						double 	fcost = 0.0f;
						double 	ftekj = 0.0f;
						
						double	f1015 = 0.0f;
						double	f1040 = 0.0f;
						double	f1050 = 0.0f;
						
						for( Cost c : costlist ) {
							if( c.type.equals("0") ) flaun += c.p;
							else if( c.type.equals("1") ) {
								if( c.nostr.equals("1015") ) f1015 += c.p;
								else if( c.nostr.equals("1040") ) f1040 += c.p;
								else if( c.nostr.equals("1050") ) f1050 += c.p;
								ftekj += c.p;
							}
							else if( c.type.equals("2") ) fcost += c.c;
						}
						
						matrix[ i ] = Math.floor(flaun);
						matrix[ i+23 ] = -Math.floor(ftekj);
						matrix[ i+24 ] = Math.floor(-ftekj-(flaun+fcost));
						matrix[ i+9 ] = Math.floor(fcost);
						matrix[ i+10 ] = Math.floor(fcost+flaun);
						
						matrix[ i+15 ] = Math.floor(f1015);
						matrix[ i+18 ] = Math.floor(f1040);
						matrix[ i+19 ] = Math.floor(f1050);
					} else matrix[ i ] = null;
				}
				return matrix[ i + columnIndex ];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
	}

	Result res;
	
	@Override
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
		
		initGUI();
		JComponent 	parcomp = initParComp();
		initModels( null );
		
		JComponent graph = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
			}
		};
		
		JTabbedPane	tabbedpane = new JTabbedPane( JTabbedPane.BOTTOM );
		tabbedpane.addTab("Summary", summaryscrollpane);
		tabbedpane.addTab("Detail", detailscrollpane);
		tabbedpane.addTab("Graph", graph);
		
		JSplitPane	splitpane = new JSplitPane();
		scrollpane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		comp.add( scrollpane );
		comp.add( new JTextField(), BorderLayout.SOUTH );
		
		splitpane.setLeftComponent( comp );
		splitpane.setRightComponent( tabbedpane );
		
		this.add( splitpane );
		this.add( parcomp, BorderLayout.EAST );
	}
	
	public void exportExcel() throws FileNotFoundException, IOException {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile( new File("excel_export.xlsx") );
		
		if( fc.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			workbook.write( new FileOutputStream( f ) );
		} else {
			try {
				File nf = File.createTempFile("tmp", ".xlsx");
				workbook.write( new FileOutputStream( nf ) );
				Desktop.getDesktop().open( nf );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void initGUI() {
		xlsComp = new JButton( new ImageIcon(xlsImg) );
		xlsComp.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					load( null );
					exportExcel();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
		});
		
		/*{
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(xlsImg, 0, 0, this);
			}
		};*/

		TransferHandler th = new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			@Override
			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0)
							throws UnsupportedFlavorException, IOException {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return null;
						} else {
							return null;
						}
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.getTextPlainUnicodeFlavor() };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return true;
						}
						return false;
					}
				};
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;

				int b = Arrays.binarySearch(support.getDataFlavors(),
						DataFlavor.javaFileListFlavor,
						new Comparator<DataFlavor>() {
							@Override
							public int compare(DataFlavor o1, DataFlavor o2) {
								return o1 == o2 ? 1 : 0;
							}
						});
				System.err.println(b);
				if (b != -1) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				//System.err.println( "simsim " + obj.getClass() );

				try {
					System.err.println("try");
					if (obj != null && obj instanceof List) {
						System.err.println("next");
						List<File>	l = (List<File>)obj;
						File f = l.get(0);
						load( f.getCanonicalPath() );
						
						int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						/*File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						while( !nf.canWrite() && i < 10 ) {
							i++;
							nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						}*/
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
						
						//JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
						/*if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
							workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
						}*/
					} else {
						if( true ) {
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath() );
									int i = 0;
									File nf = File.createTempFile("tmp", ".xlsx");
									//File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									/*while( nf.exists() && !nf.canWrite() && i < 10 ) {
										i++;
										nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									}*/
									workbook.write( new FileOutputStream( nf ) );
									Desktop.getDesktop().open( nf );
								}
							}
						} else {
							//char[] cc = new char[256];
							//Reader r = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(support.getTransferable());
							//int read = r.read(cc);
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath() );
									JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}
								}
							}
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
		xlsComp.setTransferHandler(th);

		pdfComp = new JButton();
		pdfComp.setIcon( new ImageIcon(pdfImg) );
		/*{
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(pdfImg, 0, 0, this);
			}
		};*/
	}
	
	public JComponent initParComp() {
		JComponent parcomp = new JComponent() {
			public void setBounds(int x, int y, int w, int h) {
				super.setBounds(x, y, w, h);
				xlsComp.setBounds(this.getWidth() / 2 - xlsImg.getWidth( this ) - 10, (this.getHeight() - xlsImg.getHeight( this )) / 2, xlsImg.getWidth( this ), xlsImg.getHeight( this ));
				pdfComp.setBounds(this.getWidth() / 2 + 10,	(this.getHeight() - pdfImg.getHeight( this )) / 2, pdfImg.getWidth( this ), pdfImg.getHeight( this ));
			}
		};
		parcomp.setLayout( null );
		parcomp.add( xlsComp );
		parcomp.add( pdfComp );
		parcomp.setPreferredSize( new Dimension(100,100) );
		
		return parcomp;
	}
	
	public TableModel createSvidModel( Map<String,String> pmap ) {
		final List<Svid>	svidlist = new ArrayList<Svid>();
		try {			
			String sql = "SELECT \"Global Dimension 1 Code\", No_, Description, \"Person Responsible\", \"Starting Date\", \"Ending Date\" FROM \"Matís ohf_$Job\" where \"Job Posting Group\" != 'INNSELD' and \"Completion Date\" < '1900-01-01'";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String svid = rs.getString(1);
				String no = rs.getString(2);
				String name = rs.getString(3);
				String pn = rs.getString(4);
				String person = (pmap != null && pmap.containsKey( pn )) ? pmap.get(pn) : pn;
				Date	startdate = rs.getDate(5);
				Date	stopdate = rs.getDate(6);
				
				svidlist.add( new Svid( svid, no, name, person, startdate, stopdate ) );
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return createModel( svidlist );
	}
	
	public void initModels( Map<String,String> pmap ) {		
		model = createSvidModel( pmap );
		table = new JTable( model ) {
			public void sorterChanged( RowSorterEvent e ) {
				currentsorter = (MySorter)e.getSource();
				summarytable.repaint();
				super.sorterChanged( e );
			}
		};
		//table.setAutoCreateRowSorter( true );
		scrollpane = new JScrollPane();
		scrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		
		final JTable		detailTable = new JTable();
		detailscrollpane = new JScrollPane( detailTable );
		
		rowHeader = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "";
			}

			@Override
			public int getRowCount() {
				return 25;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String ret = "";
				
				switch( rowIndex ) {
				case 0:
					ret = "Kostnaður v/vinnu";
					break;
				case 2:
					ret = "2310 Rannsóknarstofuefni";
					break;
				case 3:
					ret = "2311 Aðföng rekstarvörur";
					break;
				case 4:
					ret = "2396	Mælingar á milli deilda";
					break;
				case 5:
					ret = "2498	Sérfræðiþjónusta samtals";
					break;
				case 6:
					ret = "2598	Ferða- og uppihaldsk samtals";
					break;
				case 7:
					ret = "2710	Styrkir til nemenda";
					break;
				case 9:
					ret = "2999	Almennur rekstrarkostnaður samtals";
					break;
				case 10:
					ret = "Kostnaður samtals";
					break;
				case 12:
					ret = "1010	Fyrirtæki innlend m/vsk";
					break;
				case 13:
					ret = "1012	Fyrirtæki innlend án/vsk";
					break;
				case 14:
					ret = "1013	Opinberir aðilar m/vsk";
					break;
				case 15:
					ret = "1015	opinberir aðilar án/vsk";
					break;
				case 16:
					ret = "1030	Erlendir sjóðir";
					break;
				case 17:
					ret = "1031	Erlend fyrirtæki";
					break;
				case 18:
					ret = "1040	Sjóðir innlent";
					break;
				case 19:
					ret = "1050	Sjávarútvegs- og landb";
					break;
				case 20:
					ret = "1060	Sala án/vsk";
					break;
				case 21:
					ret = "1096	Sala milli deilda";
					break;
				case 23:
					ret = "1993	Rekstrartekjur samtals";
					break;
				case 24:
					ret = "Afkoma (v/útselds taxta)";
					break;
				default:
					ret = "";
				}
				
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		JTable	rowHeaderTable = new JTable();
		rowHeaderTable.setModel( rowHeader );
		detailscrollpane.setRowHeaderView( rowHeaderTable );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Calculate") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TableModel model = calcModel();
					detailTable.setModel( model );
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Export To Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {}
		});
		scrollpane.setComponentPopupMenu(popup);
		table.setComponentPopupMenu( popup );
		
		/*JComponent graph = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
			}
		};*/
		
		final Object[]	matrix = new Object[ model.getRowCount() * rowHeader.getRowCount() ];
		
		try {
			res = loadAll();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		summaryscrollpane = new JScrollPane();
		summaryscrollpane.setRowHeaderView( table );
		scrollpane.setViewport( summaryscrollpane.getRowHeader() );
		summarytable = new JTable() {
			public void sorterChanged( RowSorterEvent e ) {
				currentsorter = (MySorter)e.getSource();
				table.repaint();
				super.sorterChanged( e );
			}
		};
		//summarytable.setAutoCreateRowSorter( true );
		summarymodel = createSummaryModel( res, matrix );
		summarytable.setModel( summarymodel );
		summarytable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		summaryscrollpane.setViewportView( summarytable );
		
		sorter = new MySorter( model ) {
			@Override
			public int convertRowIndexToModel(int index) {
				return currentsorter.convertRowIndexToModelSuper( index );
			}

			@Override
			public int convertRowIndexToView(int index) {
				return currentsorter.convertRowIndexToViewSuper( index );
				//super.
				//currentSorter.
			}
		};
		summarysorter = new MySorter( summarymodel ) {
			@Override
			public int convertRowIndexToModel(int index) {
				return currentsorter.convertRowIndexToModelSuper( index );
			}

			@Override
			public int convertRowIndexToView(int index) {
				return currentsorter.convertRowIndexToViewSuper( index );
				//leftTableSorter.
			}
			
			@Override
			public int getViewRowCount() {
				return sorter.getViewRowCount();
			}
		};
		currentsorter = (MySorter)summarysorter;
		summarytable.setRowSorter( summarysorter );
		table.setRowSorter( sorter );

		/*
		 * try { Desktop.getDesktop().browse( new URI("http://test.matis.is") );
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (URISyntaxException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public class TransparentBackground extends JComponent { 
		//implements ComponentListener, WindowFocusListener, Runnable {
		/*private JFrame _frame;
		private BufferedImage _background;
		private long _lastUpdate = 0;
		private boolean _refreshRequested = true;
		private Robot _robot;
		private Rectangle _screenRect;
		private ConvolveOp _blurOp;

		// constructor
		// -------------------------------------------------------------

		public TransparentBackground(JFrame frame) {
			_frame = frame;
			try {
				_robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
				return;
			}

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			_screenRect = new Rectangle(dim.width, dim.height);

			float[] my_kernel = { 0.10f, 0.10f, 0.10f, 0.10f, 0.20f, 0.10f,
					0.10f, 0.10f, 0.10f };
			_blurOp = new ConvolveOp(new Kernel(3, 3, my_kernel));

			updateBackground();
			_frame.addComponentListener(this);
			_frame.addWindowFocusListener(this);
			new Thread(this).start();
		}

		// protected
		// ---------------------------------------------------------------

		protected void updateBackground() {
			_background = _robot.createScreenCapture(_screenRect);
		}

		protected void refresh() {
			if (_frame.isVisible() && this.isVisible()) {
				repaint();
				_refreshRequested = true;
				_lastUpdate = System.currentTimeMillis();
			}
		}

		// JComponent
		// --------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Point pos = this.getLocationOnScreen();
			BufferedImage buf = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_RGB);
			buf.getGraphics().drawImage(_background, -pos.x, -pos.y, null);

			Image img = _blurOp.filter(buf, null);
			g2.drawImage(img, 0, 0, null);
			g2.setColor(new Color(255, 255, 255, 192));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		// ComponentListener
		// -------------------------------------------------------
		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
			repaint();
		}

		public void componentResized(ComponentEvent e) {
			repaint();

		}

		public void componentShown(ComponentEvent e) {
			repaint();
		}

		// WindowFocusListener
		// -----------------------------------------------------
		public void windowGainedFocus(WindowEvent e) {
			refresh();
		}

		public void windowLostFocus(WindowEvent e) {
			refresh();
		}

		// Runnable
		// ----------------------------------------------------------------
		public void run() {
			try {
				while (true) {
					Thread.sleep(100);
					long now = System.currentTimeMillis();
					if (_refreshRequested && ((now - _lastUpdate) > 1000)) {
						if (_frame.isVisible()) {
							Point location = _frame.getLocation();
							_frame.setLocation(-_frame.getWidth(), -_frame
									.getHeight());
							updateBackground();
							_frame.setLocation(location);
							refresh();
						}
						_lastUpdate = now;
						_refreshRequested = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, y, w, h);
			xlsComp.setBounds(this.getWidth() / 2 - xlsImg.getWidth( this ) - 10, (this.getHeight() - xlsImg.getHeight( this )) / 2, xlsImg.getWidth( this ), xlsImg.getHeight( this ));
			pdfComp.setBounds(this.getWidth() / 2 + 10, (this.getHeight() - pdfImg.getHeight( this )) / 2, pdfImg.getWidth( this ), pdfImg.getHeight( this ));
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("Report Generator");
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//f.setBackground(Color.white);
		f.getContentPane().setBackground(Color.white);

		try {
			Report r = new Report();
			r.init();
			Report.TransparentBackground c = r.new TransparentBackground();

			c.add( r.xlsComp );
			c.add( r.pdfComp );
			
			f.add(c);

			f.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
