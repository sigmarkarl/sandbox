package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Afstemming extends JApplet {

	Connection connection;
	public void connect( String connectionUrl ) throws SQLException {
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		connection = DriverManager.getConnection(connectionUrl);
	}
	
	public class Varda implements Comparable<Varda> {
		public Varda( String name, double cost, double price ) {
			this( name, cost, price, false, "-1" );
		}
		
		public Varda( String name, double cost, double price, String type ) {
			this( name, cost, price, false, type );
		}
		
		public Varda( String name, double cost, double price, boolean lokid ) {
			this( name, cost, price, lokid, "-1" );
		}
		
		public Varda( String name, double cost, double price, boolean lokid, String type ) {
			this.name = name;
			this.price = price;
			this.cost = cost;
			this.lokid = lokid;
			this.type = type;
		}
		
		String	name;
		double	cost;
		double	price;
		boolean lokid;
		String type;
		
		public double getValue() {
			//if( type.contains("2") ) 
			if( type.equals("1") ) return -price;
			else if( type.equals("2") ) return price;
			return cost;
		}
		
		@Override
		public int compareTo(Varda o) {
			return Double.compare(getValue(), o.getValue());
		}
	};
	
	public class Verk {
		public Verk( String vnr ) {
			this.vnr = vnr;
			vordur = new ArrayList<Varda>();
			lokid = new ArrayList<Varda>();
			aaetlun = new ArrayList<Varda>();
			raun = new ArrayList<Varda>();
		}
		
		public void addVarda( String name, double cost, double price, boolean lok ) {
			if( lok ) lokid.add( new Varda(name,cost,price,lok) );
			else vordur.add( new Varda(name,cost,price,lok) );
		}
		
		public void addOLokid( String name, double cost, double price ) {
			vordur.add( new Varda(name,cost,price,false) );
		}
		
		public void addLokid( String name, double cost, double price ) {
			lokid.add( new Varda(name,cost,price,true) );
		}
		
		public void addAaetlun( String name, double cost, double price, String type ) {
			aaetlun.add( new Varda(name,cost,price,type) );
		}
		
		public void addRaun( String name, double cost, double price, String type ) {
			raun.add( new Varda(name,cost,price,type) );
		}
		
		String		vnr;
		String		name;
		String		svid;
		String		vstj;
		List<Varda> vordur;
		List<Varda> lokid;
		List<Varda>	aaetlun;
		List<Varda>	raun;
	};
	
	public void loadRaun() throws SQLException {
		String sql = "select le.\"Job No_\", le.No_, ge.\"Sales Account\", le.Type, ge.\"Gen_ Prod_ Posting Group\", sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge where "
				//+ "le.\"Job No_\" = " + str + " and "
				+ "ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" "
				+ "group by le.\"Job No_\", le.No_, ge.\"Sales Account\", ge.\"Gen_ Prod_ Posting Group\", le.Type";

		
		//if( endDate != null ) sql += " and be.Date <= '"+endDate+"'";
		//sql += " group by be.\"Job No_\", be.No_, be.Type, bl.Description";
		
		PreparedStatement 	ps = connection.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();
		
		while (rs.next()) {
			String jno = rs.getString(1);
			String no = rs.getString(2);
			String sac = rs.getString(3);
			String typ = rs.getString(4);
			String dsc = rs.getString(5);
			double cost = rs.getDouble(6);
			double price = rs.getDouble(7);
			
			String vnr = jno.substring( jno.length()-4 );
			int vnum = 0;
			try {
				vnum = Integer.parseInt( no );
			} catch( Exception e ) {}
			
			if( vnr.equals("1894") ) {
				System.err.println( "ranni " + typ + "  " + no + "  " + dsc + "  " + price + "  " + cost );
			}
			
			if( typ.equals("1") && verkmap.containsKey( vnr ) ) {
				Verk vrk = verkmap.get(vnr);
				vrk.addRaun( dsc, cost, price, typ );
			}
		}
		
		rs.close();
		ps.close();
	}
	
	public void loadAaetlun() throws SQLException {
		String	sql = "select be.\"Job No_\", be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where "
				//+ "be.\"Job No_\" = " o.toString() + " and "
				+ "be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\"";
		
		//if( endDate != null ) sql += " and be.Date <= '"+endDate+"'";
		sql += " group by be.\"Job No_\", be.No_, be.Type, bl.Description";
		
		PreparedStatement 	ps = connection.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();
		
		while (rs.next()) {
			String jno = rs.getString(1);
			String no = rs.getString(2);
			String typ = rs.getString(3);
			String dsc = rs.getString(4);
			double cost = rs.getDouble(5);
			double price = rs.getDouble(6);
			
			String vnr = jno.substring( jno.length()-4 );
			int vnum = 0;
			try {
				vnum = Integer.parseInt( no );
			} catch( Exception e ) {}
			
			if( (vnum < 1100 && vnum >= 1000) && verkmap.containsKey( vnr ) ) {
				Verk vrk = verkmap.get(vnr);
				vrk.addAaetlun( dsc, cost, price, typ );
			}
		}
		
		rs.close();
		ps.close();
	}
	
	Map<String,Verk>	verkmap;
	public List<Verk> loadVerks() throws SQLException {
		List<Verk>	vlist = new ArrayList<Verk>();
		
		String sql = "select [Verknúmer], [Heiti vörðu], [Verðmæti], [Lokið] from [vdb].[dbo].[Vörður]";
		PreparedStatement 	ps = connection.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		verkmap = new HashMap<String,Verk>();
		while (rs.next()) {
			String vnr = rs.getString(1);
			String htv = rs.getString(2);
			double vrd = rs.getDouble(3);
			boolean lok = rs.getBoolean(4);
			
			Verk vrk;
			if( verkmap.containsKey( vnr ) ) vrk = verkmap.get(vnr);
			else {
				vrk = new Verk( vnr );
				verkmap.put( vnr, vrk );
			}
			
			vrk.addVarda( htv, vrd, vrd, lok );
		}
		
		rs.close();
		ps.close();
		
		sql = "select [Verknúmer], [Heiti verks (isl)], [Svið], [Verkefnisstjóri] from [vdb].[dbo].[Verkefnalisti]";
		ps = connection.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			String vnr = rs.getString(1);
			String htv = rs.getString(2);
			String svd = rs.getString(3);
			String vst = rs.getString(4);
			
			if( verkmap.containsKey( vnr ) ) {
				Verk vrk = verkmap.get(vnr);
				vrk.name = htv;
				vrk.svid = svd;
				vrk.vstj = vst;
			}
		}
		
		rs.close();
		ps.close();
		
		for( String vnr : verkmap.keySet() ) {
			Verk verk = verkmap.get(vnr);
			Collections.sort( verk.vordur );
			vlist.add( verk );
		}
		
		return vlist;
	}
	
	Color[] cc = new Color[] { new Color(100,100,100), new Color(200,100,100), new Color(100,200,100), new Color(100,100,200), new Color(200,200,100), new Color(200,100,200), new Color(100,200,200), new Color(200,200,200) };
	public class VorduCanvas extends JComponent {
		List<Varda>	vlist;
		List<Varda>	llist;
		List<Varda>	alist;
		List<Varda>	rlist;
		String		title;
		String		vstj;
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			g.setColor( Color.white );
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if( vlist != null ) {
				int w2 = this.getWidth()/2;
				int h2 = this.getHeight()/2;
				
				double tvalvdb = 0.0;
				for( Varda v : vlist ) {
					tvalvdb += v.getValue();
				}
				
				double tvalvdbl = 0.0;
				for( Varda v : llist ) {
					tvalvdbl += v.getValue();
				}
				
				double tvalaaetlun = 0.0;
				for( Varda v : alist ) {
					tvalaaetlun += v.getValue();
				}
				
				double tvalraun = 0.0;
				for( Varda v : rlist ) {
					tvalraun += v.getValue();
				}
				
				double tval = Math.max( tvalvdbl, Math.max( tvalraun, Math.max( tvalvdb, tvalaaetlun ) ) );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				g.setFont( g.getFont().deriveFont(9.0f) );
				
				stuff( g2, vlist, w2, h2, tval, -300, "verkefnagrunnur" );
				stuff( g2, llist, w2, h2, tval, -100, "vgrunnur lokið" );
				stuff( g2, alist, w2, h2, tval, 300, "raun" );
				stuff( g2, rlist, w2, h2, tval, 100, "áætlun" );
				
				int strw = g.getFontMetrics().stringWidth( title );
				g.drawString( title, w2-strw/2, h2-380);
				
				strw = g.getFontMetrics().stringWidth( vstj );
				g.drawString( vstj, w2-strw/2, h2-360);
			}
		}
		
		public void updateVordur( List<Varda> vlist, List<Varda> llist, List<Varda> alist, List<Varda> rlist, String title, String vstj ) {
			this.vlist = vlist;
			this.llist = llist;
			this.alist = alist;
			this.rlist = rlist;
			this.title = title;
			this.vstj = vstj;
			this.repaint();
		}
	};
	
	public void stuff( Graphics2D g, List<Varda> vlist, int w2, int h2, double tval, int offset, String type ) {
		double total = 0.0;
		int c = 0;
		
		for( int i = 0; i < vlist.size()/2; i++ ) {
			Varda varda = vlist.get(i);
			double fval = varda.getValue();					
			double hf = (total*600.0)/tval;
			total += fval;
			double hn = (total*600.0)/tval;
			g.setColor( cc[c] );
			g.fillRect(w2+offset-25, h2+300-(int)hn, 50, (int)(hn-hf) );
			c = (c+1)%cc.length;
			
			g.setColor( Color.darkGray );
			g.drawString("("+(int)varda.getValue()+"kr.) "+varda.name, w2+offset+30, h2+300-(int)hf);
			
			varda = vlist.get( vlist.size()-1-i );
			double nval = vlist.get( vlist.size()-1-i ).getValue();
			hf = hn;
			total += nval;
			hn = (total*600.0)/tval;
			g.setColor( cc[c] );
			g.fillRect(w2+offset-25, h2+300-(int)hn, 50, (int)(hn-hf) );
			c = (c+1)%cc.length;
			
			g.setColor( Color.darkGray );
			String str = varda.name+" ("+(int)varda.getValue()+"kr.)";
			int strw = g.getFontMetrics().stringWidth(str);
			g.drawString(str, w2+offset-30-strw, h2+300-(int)hf);
		}
		if( vlist.size()%2 != 0 ) {
			Varda varda = vlist.get( vlist.size()/2 );
			double fval = varda.getValue();				
			double hf = (total*600.0)/tval;
			total += fval;
			double hn = (total*600.0)/tval;
			g.setColor( cc[c] );
			g.fillRect(w2+offset-25, h2+300-(int)hn, 50, (int)(hn-hf) );
			//c = (c+1)%cc.length;
			
			g.setColor( Color.darkGray );
			g.drawString("("+(int)varda.getValue()+"kr.) "+varda.name, w2+offset+30, h2+300-(int)hf);
		}
		g.setColor( Color.black );
		String tstr = "Total: "+(int)total+" kr.";
		int strw = g.getFontMetrics().stringWidth( tstr );
		g.drawString( tstr, w2+offset-strw/2, h2-310);
		
		strw = g.getFontMetrics().stringWidth( type );
		g.drawString( type, w2+offset-strw/2, h2-340);
	}
	
	JTable			table;
	VorduCanvas		vcanvas;
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
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=mirodc30;";
		try {
			connect( connectionUrl );
			final List<Verk> vlist = loadVerks();
			loadAaetlun();
			loadRaun();
			TableModel model = new TableModel() {
				
				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void removeTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if( columnIndex == 0 ) return vlist.get(rowIndex).vnr;
					else if( columnIndex == 1 ) return vlist.get(rowIndex).name;
					else if( columnIndex == 2 ) return vlist.get(rowIndex).svid;
					else return vlist.get(rowIndex).vstj;
				}
				
				@Override
				public int getRowCount() {
					return vlist.size();
				}
				
				@Override
				public String getColumnName(int columnIndex) {
					if( columnIndex == 0 ) return "Verknúmer";
					else if( columnIndex == 1 ) return "Verkheiti";
					else if( columnIndex == 2 ) return "Svið";
					else return "Verkstjóri";
				}
				
				@Override
				public int getColumnCount() {
					return 4;
				}
				
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return String.class;
				}
				
				@Override
				public void addTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}
			};
			table.setModel( model );
			
			table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					int r = table.getSelectedRow();
					int i = table.convertRowIndexToModel(r);
					
					Verk v = vlist.get(i);
					vcanvas.updateVordur( v.vordur, v.lokid, v.aaetlun, v.raun, v.name, v.vstj );
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		vcanvas = new VorduCanvas();
		JScrollPane scrollpane = new JScrollPane( table );
		
		JSplitPane	splitpane = new JSplitPane();
		splitpane.setLeftComponent( scrollpane );
		splitpane.setRightComponent( vcanvas );
		
		this.add( splitpane );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
