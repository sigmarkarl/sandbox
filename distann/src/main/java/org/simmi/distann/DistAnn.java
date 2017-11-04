package org.simmi.distann;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DistAnn extends JApplet {
	JToolBar	toolbar;
	JSplitPane	splitpane;
	JScrollPane	projectpane;
	JTable		projecttable;
	JPopupMenu	projectmenu;
	TableModel	projectmodel;
	List<Project>	projectlist;
	File		projectroot;
	JScrollPane	fastapane;
	JTable		fastatable;
	TableModel	fastamodel;
	JPopupMenu	fastamenu;	
	
	class Fasta {
		String	name;
		String	path;
		
		public Fasta( String name, String path ) {
			this.name = name;
			this.path = path;
		}
	}
	
	class Project {
		String	e_name;
		String	admin;
		Date	date;
		List<Fasta>	fastalist = new ArrayList<Fasta>();
		
		public Project() {
			
		}
		
		public Project( String name, String admin, Date date ) throws IOException {
			this.e_name = name;
			this.admin = admin;
			this.date = date;
			
			writeToFile();
		}
		
		public void addFasta( Fasta fasta ) {
			fastalist.add( fasta );
		}
		
		public File getFile() {
			return new File( projectroot, e_name );
		}
		
		private void writeToFile() throws IOException {
			File f = new File( projectroot, e_name );
			f.setLastModified( date.getTime() );
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
	
	public TableModel nullModel() {
		return new TableModel() {
			@Override
			public int getRowCount() {
				return 0;
			}

			@Override
			public int getColumnCount() {
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
			
		};
	}
	
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static void updateLof() {
		try {
			UIManager.setLookAndFeel(lof);
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
	
	public void init() {
		updateLof();

		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}

		Dimension d = new Dimension( 100,30 );
		final JProgressBar	p = new JProgressBar();
		p.setIndeterminate( true );
		p.setPreferredSize( d );
		p.setSize( d );
		final JLabel	l = new JLabel("Erm");
		l.setPreferredSize( d );
		l.setSize( d );
		JComponent 		c = new JComponent() { 
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				int pw = p.getWidth();
				int ph = p.getHeight();
				l.setBounds((w-pw)/2, (h-ph)/2-20, pw, ph);
				p.setBounds((w-pw)/2, (h-ph)/2+20, pw, ph);
			}
		};
		c.setLayout( null );
		
		c.add( p );
		c.add( l );
		
		final String person = System.getProperty("user.name");
		final String home = System.getProperty("user.home");
		final String osname = System.getProperty("os.name");
		
		File f = new File(home, ".distann");
		if( !f.exists() ) {
			f.mkdirs();
		}
		projectroot = new File( f, "projects" );
		if( !projectroot.exists() ) {
			projectroot.mkdir();
		}
		
		if( f.exists() ) {
			File prodigal = new File( f, "Prodigal" );
			if( !prodigal.exists() ) {
				prodigal.mkdir();
				String path = osname.contains("indows") ? "http://prodigal.googlecode.com/files/prodigal.v2_50.windows.exe" : "http://prodigal.googlecode.com/files/prodigal.v2_50.linux";
				try {
					URL url = new URL( path );
					InputStream is = url.openStream();
					FileOutputStream	fos = new FileOutputStream( new File( prodigal, "prodigal" ) );
					
					byte[] b = new byte[1024];
					int r = is.read(b);
					
					while( r > 0 ) {
						fos.write(b, 0, r);
						r = is.read(b);
					}
					fos.close();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			File blast = new File( f, "Blast" );
			if( !blast.exists() ) {
				/*String path = osname.contains("indows") ? "http://code.google.com/p/prodigal/downloads/detail?name=prodigal.v2_50.windows.exe" : "http://code.google.com/p/prodigal/downloads/detail?name=prodigal.v2_50.linux";
				try {
					URL url = new URL( path );
					InputStream is = url.openStream();
					FileOutputStream	fos = new FileOutputStream( new File( prodigal, path.substring(path.indexOf("name=")+5) ) );
					
					byte[] b = new byte[1024];
					int r = is.read(b);
					
					while( r > 0 ) {
						fos.write(b, 0, r);
						r = is.read(b);
					}
					fos.close();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}
		
		projectlist = new ArrayList<Project>();
		//this.add( c );
		toolbar = new JToolBar();
		splitpane = new JSplitPane();
		projectmenu = new JPopupMenu();
		projectmenu.add( new AbstractAction("New project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date d = new Date( System.currentTimeMillis() );
				try {
					projectlist.add( new Project( Long.toString( (long)(d.getTime()/1000.0) ), person, d ) );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//TableModel model = projecttable.getModel();
				if( projectmodel == null ) {
					projectmodel = createModel( projectlist );
					projecttable.setModel( projectmodel );
				} else {
					//projecttable.tableChanged( new TableModelEvent( projecttable.getModel() ) );
				}
			}
		});
		projectmenu.add( new AbstractAction("Remove action") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = projecttable.getSelectedRows();
				Set<Project>	project = new HashSet<Project>();
				for( int r : rr ) {
					project.add( projectlist.get( projecttable.convertRowIndexToModel(r) ) );
				}
				projectlist.removeAll( project );
				projecttable.tableChanged( new TableModelEvent(projecttable.getModel()) );
			}
			
		});
		projecttable = new JTable();
		projecttable.setModel( nullModel() );
		projecttable.setComponentPopupMenu( projectmenu );
		projectpane = new JScrollPane( projecttable );
		projectpane.setComponentPopupMenu( projectmenu );
		
		/*fastatable = new JTable();
		fastapane = new JScrollPane( fastatable );
		fastamenu = new JPopupMenu();
		fastamenu.add( new AbstractAction("Add fasta file") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	filechooser = new JFileChooser();
				filechooser.showOpenDialog( DistAnn.this );
			}
		});
		fastapane.setComponentPopupMenu( fastamenu );
		fastatable.setComponentPopupMenu( fastamenu );
		fastatable.setModel( nullModel() );*/
		
		splitpane.setLeftComponent( projectpane );
		//splitpane.setRightComponent( fastapane );
		
		projecttable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				/*int r = projecttable.getSelectedRow();
				r = projecttable.convertRowIndexToModel( r );
				Project p = projectlist.get( r );
				fastatable.setModel( createModel( p.fastalist ) );
				fastatable.tableChanged( new TableModelEvent( fastatable.getModel() ) );*/
			}
		});
		
		projecttable.getDefaultEditor(String.class).addCellEditorListener( new CellEditorListener() {
			@Override
			public void editingStopped(ChangeEvent e) {
				int r = projecttable.getSelectedRow();
				//File
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {}
		});
		
		this.add( toolbar, BorderLayout.NORTH );
		this.add( splitpane );
	}
}
