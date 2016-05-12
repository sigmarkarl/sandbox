package org.simmi;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Frystilager extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String	user = "";
	
	JScrollPane	scrollpane;
	JTable		table = new JTable();
	
	public class Lager {
		Long	ID;
		String	Ábyrgðaraðili;
		String	Sími;
		String	Verknúmer;
		String	Lota;
		String	Klefi;
		String	Gólf;
		String	Rekki;
		String	Hilla;
		Date	Dagsetning;
		Long	Kassafjöldi;
		Boolean	Kjöt;
		Boolean	Fiskur;
		Boolean	Annað;
		
		public Lager( 	Long	ID,
						String	Ábyrgðaraðili,
						String	Sími,
						String	Verknúmer,
						String	Lota,
						String	Klefi,
						String	Gólf,
						String	Rekki,
						String	Hilla,
						Date	Dagsetning,
						Long	Kassafjöldi,
						Boolean	Kjöt,
						Boolean	Fiskur,
						Boolean	Annað ) {
			this.ID = ID;
			this.Ábyrgðaraðili = Ábyrgðaraðili;
			this.Sími = Sími;
			this.Verknúmer = Verknúmer;
			this.Lota = Lota;
			this.Klefi = Klefi;
			this.Gólf = Gólf;
			this.Rekki = Rekki;
			this.Hilla = Hilla;
			this.Dagsetning = Dagsetning;
			this.Kassafjöldi = Kassafjöldi;
			this.Kjöt = Kjöt;
			this.Fiskur = Fiskur;
			this.Annað = Annað;
		}
	};
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	boolean userCheck() {
		return user.equals("ragnar") || user.equals("annas") || user.equals("johanna") || user.equals("adalheidur") || user.equals("unnurh") || user.equals("gulla") || user.equals("julia") || user.equals("andrea") || user.equals("hjordis") || user.equals("root") || user.equals("sigmar");
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
						ret = f.get( datalist.get(rowIndex) );
						
						/*if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}*/
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
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				//this.getColumnCount() > 5 ? this.getValueAt(rowIndex, 5).equals(user) :
				String userstr = this.getColumnCount() > 6 ? (String)this.getValueAt(rowIndex, 6) : null;
				//if( userstr == null ) userstr = "";
				//System.out.println(userstr + " " + user);
				String fname = f.getName();
				boolean editable = fname.startsWith("e_") && ( user.equals(userstr) || userCheck() );
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
	
	Connection con;
	public void connect() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=mirodc30;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		con = DriverManager.getConnection(connectionUrl);
	}
	
	public String getUser() {
		return user;
	}
	
	public List<Lager> loadLager() throws IOException, SQLException {
		List<Lager>	lagerlist = new ArrayList<Lager>();
		
		String query = "";
		Field[] ff = Lager.class.getDeclaredFields();
		for( int i = 0; i < ff.length-1; i++ ) {
			Field f = ff[ i ];
			if( i == 0 ) query += "["+f.getName()+"]";
			else query += ",["+f.getName()+"]";
		}
		//System.err.println( query );
		String sql = "select "+query+" from [vdb].[dbo].[Frystilager]"; // where [user] = '"+user+"'";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while(rs.next()) {
			String kjot = rs.getString("Kjöt");
			String fiskur = rs.getString("Fiskur");
			String annad = rs.getString("Annað");
			lagerlist.add( new Lager( 	rs.getLong("ID"), 
										rs.getString("Ábyrgðaraðili"), 
										rs.getString("Sími"), 
										rs.getString("Verknúmer"), 
										rs.getString("Lota"), 
										rs.getString("Klefi"), 
										rs.getString("Gólf"), 
										rs.getString("Rekki"), 
										rs.getString("Hilla"), 
										rs.getDate("Dagsetning"), 
										rs.getLong("Kassafjöldi"), 
										kjot != null && kjot.length() > 0, 
										fiskur != null && fiskur.length() > 0, 
										annad != null && annad.length() > 0 ) );
		}
		
		rs.close();
		ps.close();
		
		return lagerlist;
	}
	
	static {
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
	}
	
	public PreparedStatement getPrepStat( String sql ) throws SQLException {
		if( con == null || con.isClosed() ) {
			connect();
		}
		
		return con.prepareStatement( sql );
	}
	
	public long getMax() {
		long max = 0;
		for( Lager l : lagerlist ) {
			if( l.ID > max ) max = l.ID;
		}
		return max;
	}
	
	public void insert( String ábyr, String sími, String verk, String lota, String klefi, String gólf, String rekki, String hilla, Integer kassa, boolean kjot, boolean fiskur, boolean annad ) throws SQLException {
		//long id = getMax()+1;
		String ord = "'"+ábyr+"','"+sími+"','"+verk+"','"+lota+"','"+klefi+"','"+gólf+"','"+rekki+"','"+hilla+"',GetDate(),"+kassa+(kjot ? ",'X'" : ",''")+(fiskur ? ",'X'" : ",''")+(annad ? ",'X'" : ",''");
		String sql = "insert into [vdb].[dbo].[Frystilager] values ("+ord+")";
		
		PreparedStatement 	ps = getPrepStat(sql);
		boolean				b = ps.execute();
		
		/*if( !b ) {
			String framl = ordmap.containsKey(cat) ? ordmap.get(cat).e_Framleiðandi : "";
			lagerList.add( new Lager( false, birgi, cat, ordno, name, framl, user, whom, quant, unit, new Date( System.currentTimeMillis() ), null, null, "", verknr, location, price, null ) );
		}*/
		
		ps.close();
		
		/*if( verknr != null && location != null ) {
			sql = "update [order].[dbo].[Vara] set lastjob = '"+verknr+"', lastloc = '"+location+"', lastunit = '"+unit+"' where name = '"+name+"' and cat = '"+cat+"'";
			ps = getPrepStat(sql);
			b = ps.execute();
			ps.close();
		}*/
	}
	
	public void init() {
		initGUI( this );
	}
	
	List<Lager>			lagerlist = new ArrayList<Lager>();
	Map<Long,Lager>		lagermap = new HashMap<Long,Lager>();
	public void initGUI( Container cont ) {
		//boolean valid = false;
		try {
			connect();
			lagerlist = loadLager();
			//valid = con.isValid(2);
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for( Lager l : lagerlist ) {
			lagermap.put( l.ID, l );
		}
		TableModel	model = createModel( lagerlist );
		
		final JRadioButton	kjot = new JRadioButton("Kjöt");
		final JRadioButton	fiskur = new JRadioButton("Fiskur");
		final JRadioButton	annad = new JRadioButton("Annað");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( kjot );
		bg.add( fiskur );
		bg.add( annad );
		
		final JLabel		ábyrLabel = new JLabel("Ábyrgðaraðili");
		final JTextField	ábyr = new JTextField( 30 );
		final JLabel		símiLabel = new JLabel("Sími");
		final JTextField	sími = new JTextField();
		final JLabel		verkLabel = new JLabel("Verknúmer");
		final JTextField	verk = new JTextField();
		final JLabel		lotaLabel = new JLabel("Lota");
		final JTextField	lota = new JTextField();
		final JLabel		klefiLabel = new JLabel("Klefi");
		final JTextField	klefi = new JTextField();
		final JLabel		gólfLabel = new JLabel("Gólf");
		final JTextField	gólf = new JTextField();
		final JLabel		rekkiLabel = new JLabel("Rekki");
		final JTextField	rekki = new JTextField();
		final JLabel		hillaLabel = new JLabel("Hilla");
		final JTextField	hilla = new JTextField();
		final JLabel		kassaLabel = new JLabel("Kassafjöldi");
		final JSpinner		kassa = new JSpinner();
		final JComponent comp = new JComponent() {};
		GridLayout layout = new GridLayout(0,2);
		comp.setLayout( layout );
		comp.add( ábyrLabel );
		comp.add( ábyr );
		comp.add( símiLabel );
		comp.add( sími );
		comp.add( verkLabel );
		comp.add( verk );
		comp.add( lotaLabel );
		comp.add( lota );
		comp.add( klefiLabel );
		comp.add( klefi );
		comp.add( gólfLabel );
		comp.add( gólf );
		comp.add( rekkiLabel );
		comp.add( rekki );
		comp.add( hillaLabel );
		comp.add( hilla );
		comp.add( kassaLabel );
		comp.add( kassa );
		comp.add( kjot );
		comp.add( fiskur );
		comp.add( annad );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Nýskrá") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( JOptionPane.showConfirmDialog( Frystilager.this, comp, "Skráðu", JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.OK_OPTION ) {
					try {
						insert( ábyr.getText(), sími.getText(), verk.getText(), lota.getText(), klefi.getText(), gólf.getText(), rekki.getText(), hilla.getText(), (Integer)kassa.getValue(), kjot.isSelected(), fiskur.isSelected(), annad.isSelected() );
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
		table.setComponentPopupMenu( popup );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		scrollpane = new JScrollPane( table );
		
		cont.add( scrollpane );
	}
	
	public static void main(String[] args) {
		String userhome = System.getProperty("user.home");
		System.setProperty("java.library.path", userhome);
		
		JFrame	frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().setBackground( Color.white );
		
		Frystilager flager = new Frystilager();
		
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bb = new byte[2048];
			InputStream is = System.getProperty("sun.arch.data.model").contains("64") ? flager.getClass().getResourceAsStream("/windows_x64/sqljdbc_auth.dll") : flager.getClass().getResourceAsStream("/windows_x86/sqljdbc_auth.dll");
			int r = is.read(bb);
			while( r > 0 ) {
				baos.write(bb, 0, r);
				r = is.read( bb );
			}
			is.close();
			Path path = Paths.get( userhome );
			Path subpath = path.resolve("sqljdbc_auth.dll");
			Files.write( subpath, baos.toByteArray(), StandardOpenOption.CREATE );
			String strpath = subpath.toAbsolutePath().toString();
			System.err.println( "writing " + strpath );
			System.load( strpath );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		flager.initGUI( frame );
		
		frame.setVisible( true );
	}
}
