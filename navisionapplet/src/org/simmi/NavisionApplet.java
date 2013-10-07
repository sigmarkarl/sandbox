package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXDatePicker;
import org.simmi.Report.Result;

public class NavisionApplet extends JApplet {
	JToolBar	toolbar;
	JSplitPane	splitpane;
	JTabbedPane	tabbedpane;
	JTabbedPane	personoptionspane;
	JScrollPane	personscrollpane;
	JTable		persontable;
	
	JLabel			afterLabel;
	JXDatePicker	after;
	JLabel			beforeLabel;
	JXDatePicker	before;
	
	JButton		connect;
	JLabel		serverlabel;
	JTextField	serverfield;
	JLabel		dblabel;
	JTextField	dbfield;
	
	public void connect( String connectionUrl ) throws SQLException {
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		rep.con = DriverManager.getConnection(connectionUrl);
	}
	
	public List<Job> loadJobs( Collection<String>	set ) throws SQLException {
		List<Job>	jlist = new ArrayList<Job>();
		
		String aftstr = (after == null || after.getDate() == null) ? null : (after.getDate().getYear()+1900) + "-" + (after.getDate().getMonth()+1) + "-" + after.getDate().getDate();
		String befstr = (before == null || before.getDate() == null) ? null : (before.getDate().getYear()+1900) + "-" + (before.getDate().getMonth()+1) + "-" + before.getDate().getDate();
		String sql = "select j.[Description], jle.[Quantity] from [MATIS].[dbo].[Matís ohf_$Job]j, [MATIS].[dbo].[Matís ohf_$Job Ledger Entry] jle where j.[No_] = jle.[Job No_] and jle.[No_] in ('";
		for( String p : set ) {
			sql += p;
		}
		sql += "')";
		if( aftstr != null ) sql += " and jle.[Posting Date] >= '"+aftstr+"'";
		if( befstr != null ) sql += " and jle.[Posting Date] <= '"+befstr+"'";
		
		PreparedStatement 	ps = rep.con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			Double d = rs.getDouble(2);
			jlist.add( new Job( rs.getString(1), d ) );
		}
		
		rs.close();
		ps.close();

		return jlist;
	}

	public List<Person> loadPersons() throws SQLException {
		List<Person>	plist = new ArrayList<Person>();
		
		String sql = "select [Name], [User ID] from [MATIS].[dbo].[User]";
		PreparedStatement 	ps = rep.con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			String kt = rs.getString(2);
			if( kt.length() == 10 ) plist.add( new Person( rs.getString(1), kt ) );
		}
		
		rs.close();
		ps.close();
		
		return plist;
	}
	
	class Job implements Comparable<Job> {
		String	name;
		double	hour;
		
		public Job( String name, double hour ) {
			this.name = name;
			this.hour = hour;
		}

		@Override
		public int compareTo(Job o) {
			return (int)(o.hour - hour);
		}
	};
	
	class Person {
		String 	name;
		String	kt;
		
		public Person( String name, String kt ) {
			this.name = name;
			this.kt = kt;
		}
	}
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
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
						
						if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}
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
				boolean editable = f.getName().startsWith("e_");// && ( (this.getValueAt(rowIndex, 4).equals(user)) || userCheck() );
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
	
	Color[] cc = new Color[] { new Color(100,100,100), new Color(200,100,100), new Color(100,200,100), new Color(100,100,200), new Color(200,200,100), new Color(200,100,200), new Color(100,200,200), new Color(200,200,200) };
	
	Map<String,PersonJob>	pjMap = new HashMap<String,PersonJob>();
	class PersonJob extends JComponent {
		Map<String,Double>	work = new HashMap<String,Double>();
		List<Job>			subwork = new ArrayList<Job>();
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream				ps = new PrintStream( baos );
		
		public PersonJob() {
			
		}
		
		public PersonJob( List<Job>	e ) {
			for( Job j : e ) {
				if( work.containsKey(j.name) ) {
					double d = work.get( j.name );
					work.put( j.name, d+j.hour );
				} else {
					work.put( j.name, j.hour );
				}
			}
			
			for( String name : work.keySet() ) {
				double d = work.get( name );
				subwork.add( new Job(name,d) );
			}
			
			Collections.sort( subwork );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D	g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			double total = 0.0;
			
			for( Job j : subwork ) {
				total += j.hour;
			}
			
			int w = this.getWidth();
			int h = this.getHeight();
			
			int c = 0;
			double k = 0.0;
			int next = (int)((k*360.0)/total);
			for( int i = 0; i < (subwork.size()+1)/2; i++ ) {
				Job j = subwork.get(i);
				double d = j.hour;
				g2.setColor( cc[c] );
				
				int val = (int)(((k+d)*360.0)/total);
				g2.fillArc(w/2-250, h/2-250, 500, 500, next, val-next );
				next = val;
				
				g2.setColor( Color.black );
				double u = k+d/2.0;
				if( u > 0.25*total && u < 0.75*total ) {
					int strw = g2.getFontMetrics().stringWidth(j.name);
					u += 0.5*total;
					g2.rotate( -Math.PI*2.0*u/total, w/2, h/2 );
					g2.drawString(j.name, w/2-255-strw, h/2 );
					g2.rotate( Math.PI*2.0*u/total, w/2, h/2 );
				} else {
					g2.rotate( -Math.PI*2.0*u/total, w/2, h/2 );
					g2.drawString(j.name, w/2+255, h/2 );
					g2.rotate( Math.PI*2.0*u/total, w/2, h/2 );
				}				
				k += d;
				c = (c+1)%cc.length;
				
				if( i != subwork.size()-i-1 ) {
					j = subwork.get(subwork.size()-i-1);
					d = j.hour;
					g2.setColor( cc[c] );
					
					val = (int)(((k+d)*360.0)/total);
					g2.fillArc(w/2-250, h/2-250, 500, 500, next, val-next );
					next = val;
					g2.setColor( Color.black );
					u = k+d/2.0;
					
					String name = j.name;
					double pc = j.hour/total;
					if( pc > 0.5 ) {
						ps.printf("%.2f", (float)(pc*100.0) );
						name = "(" + baos.toString() + ") "+name;
						ps.flush();
						baos.reset();
					}
					
					if( u > 0.25*total && u < 0.75*total ) {
						int strw = g2.getFontMetrics().stringWidth(j.name);
						u += 0.5*total;
						g2.rotate( -Math.PI*2.0*u/total, w/2, h/2 );
						g2.drawString(j.name, w/2-255-strw, h/2 );
						g2.rotate( Math.PI*2.0*u/total, w/2, h/2 );
					} else {
						g2.rotate( -Math.PI*2.0*u/total, w/2, h/2 );
						g2.drawString(j.name, w/2+255, h/2 );
						g2.rotate( Math.PI*2.0*u/total, w/2, h/2 );
					}				
					k += d;
					c = (c+1)%cc.length;
				}
			}
		}
	};
	
	Report rep;
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
		
		try {
			rep = new Report();
			rep.initGUI();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		final Map<String,String>	pmap = new HashMap<String,String>();
		connect = new JButton( new AbstractAction("Connect") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//String connectionUrl = "jdbc:sqlserver://"+serverfield.getText()+":1433;databaseName="+dbfield.getText()+";integratedSecurity=true;";
					String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=mirodc30;";
					connect( connectionUrl );
					
					List<Person>	plist = loadPersons();
					
					for( Person p : plist ) {
						pmap.put(p.kt, p.name);
					}
					
					TableModel		pmodel = createModel( plist );
					persontable.setModel( pmodel );
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar = new JToolBar();
		
		Dimension 	d = new Dimension(200,30);
		serverlabel = new JLabel("Servername:");
		serverfield = new JTextField();
		serverfield.setPreferredSize(d);
		dblabel = new JLabel("Dbname:");
		dbfield = new JTextField();
		dbfield.setPreferredSize(d);
		
		afterLabel = new JLabel("After:");
		after = new JXDatePicker();
		after.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pjMap.clear();
			}
		});
		beforeLabel = new JLabel("Before:");
		before = new JXDatePicker();
		before.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pjMap.clear();
			}
		});
		
		toolbar.add( serverlabel );
		toolbar.add( serverfield );
		toolbar.add( dblabel );
		toolbar.add( dbfield );
		toolbar.add( connect );
		
		toolbar.add( afterLabel );
		toolbar.add( after );
		toolbar.add( beforeLabel );
		toolbar.add( before );
		
		toolbar.add( rep.xlsComp );
		toolbar.add( rep.pdfComp );
		
		persontable = new JTable();
		persontable.setAutoCreateRowSorter( true );
		persontable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		persontable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = persontable.getSelectedRow();
				if( r != -1 ) {
					String str = (String)persontable.getValueAt( r, 1 );
					PersonJob pj = null;
					if( pjMap.containsKey( str ) ) {
						pj = pjMap.get( str );
					} else {
						try {
							List<Job> joblist = loadJobs( Arrays.asList( new String[] {str} ) );
							pj = new PersonJob( joblist );
							pjMap.put(str, pj);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					//personoptionspane.set
					if( pj != null ) {
						int ind = 0;
						String title = personoptionspane.getTitleAt( ind );
						while( ind < personoptionspane.getTabCount()-1 && !title.equals("Jobs") ) {
							title = personoptionspane.getTitleAt( ++ind );
						}
						
						if( title.equals("Jobs") ) {
							personoptionspane.removeTabAt( ind );
							personoptionspane.addTab( "Jobs", pj );
							personoptionspane.setSelectedIndex(ind);
							pj.repaint();
						}
					}
				}
			}
		});
		
		personscrollpane = new JScrollPane( persontable );
		PersonJob	personjob = new PersonJob();
		
		tabbedpane = new JTabbedPane( JTabbedPane.BOTTOM );
		tabbedpane.addTab("Person", personscrollpane);
		tabbedpane.addTab("Job", rep.scrollpane);
		personoptionspane = new JTabbedPane( JTabbedPane.BOTTOM );
		personoptionspane.addTab("Jobs", personjob);
		personoptionspane.addTab("Summary", rep.summaryscrollpane);
		personoptionspane.addTab("Detail", rep.detailscrollpane);
		//personoptionspane.addTab("Graph", graph);
		
		splitpane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitpane.setLeftComponent( tabbedpane );
		splitpane.setRightComponent( personoptionspane );
		
		tabbedpane.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if( rep.con != null ) {
					int selind = tabbedpane.getSelectedIndex();
					String title = tabbedpane.getTitleAt( selind );
					if( title.equals("Job") ) {
						if( rep.model == null ) {
							try {
								rep.initModels( pmap );
								Result res = rep.loadAll();
								
								tabbedpane.remove(1);
								tabbedpane.addTab("Job", rep.scrollpane);
								tabbedpane.setSelectedIndex(1);
								
								personoptionspane.setComponentAt(0, rep.summaryscrollpane);
								personoptionspane.setComponentAt(1, rep.detailscrollpane);
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						} else {
							personoptionspane.setSelectedIndex(0);
						}
					} else if( title.equals("Person") ) {
						personoptionspane.setSelectedIndex(2);
					}
				}
			}
		});
		
		this.add( toolbar, BorderLayout.NORTH );
		this.add( splitpane );
	}
}
