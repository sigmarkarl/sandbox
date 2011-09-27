package org.simmi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class Flanking extends JApplet {
	JScrollPane	scrollpane = new JScrollPane();
	JTable		table = new JTable();
	TableModel	model;
	ControlComp	buttons;
	NumComp		nums;
	
	class NumComp extends JComponent {
		JLabel	label;
		JLabel	len;
		
		public NumComp() {
			super();
			
			this.setLayout( new FlowLayout() );
			
			label = new JLabel( "Number of reads:" );
			len = new JLabel( "", JLabel.RIGHT );
			
			len.setPreferredSize( new Dimension( 50, 25 ) );
			this.add( label );
			this.add( len );
		}
	};
	
	class ControlComp extends JComponent {
		JTextField	minSeqField;
		JTextField 	minRepeatField;
		JTextField 	leftFlankingField;
		JTextField 	rightFlankingField;
		JTextField	minRepeatNumField;
		
		public int getRightFlankingLength() {
			int rightFlankingLength = 20;
			String rf = rightFlankingField.getText();
			rightFlankingLength = Integer.parseInt(rf);
			return rightFlankingLength;
		}
		
		public int getLeftFlankingLength() {
			int leftFlankingLength = 20;
			String lf = leftFlankingField.getText();
			leftFlankingLength = Integer.parseInt(lf);
			return leftFlankingLength;
		}
		
		public int getMinSeqLength() {
			int minSeqLength = 20;
			String msl = minSeqField.getText();
			minSeqLength = Integer.parseInt(msl);
			return minSeqLength;
		}
		
		public int getMinRepeatLength() {
			int minRepeatLength = 20;
			String mrl = minRepeatField.getText();
			minRepeatLength = Integer.parseInt(mrl);
			return minRepeatLength;
		}
		
		public int getMinRepeatNum() {
			int minRepeatNum = 4;
			String mrl = minRepeatNumField.getText();
			minRepeatNum = Integer.parseInt(mrl);
			return minRepeatNum;
		}
		
		public ControlComp() {
			super();
			
			this.setLayout( new FlowLayout() );
			
			Dimension d = new Dimension( 50,25 );
			
			JLabel minSeqLength = new JLabel( "Min Seq Length" );
			this.add( minSeqLength );
			minSeqField = new JTextField( "100" );
			this.add( minSeqField );
			minSeqField.setPreferredSize( d );
			minSeqField.setSize( d );
			JLabel	minRepeatLength = new JLabel( "Min Repeat Length" );
			this.add( minRepeatLength );
			minRepeatField = new JTextField( "50" );
			this.add( minRepeatField );
			minRepeatField.setPreferredSize( d );
			minRepeatField.setSize( d );
			JLabel	minRepeatNum = new JLabel( "Min Repeat Num" );
			this.add( minRepeatNum );
			minRepeatNumField = new JTextField( "4" );
			this.add( minRepeatNumField );
			minRepeatNumField.setPreferredSize( d );
			minRepeatNumField.setSize( d );
			
			JLabel	leftFlankingLength = new JLabel( "Left Flank Len" );
			this.add( leftFlankingLength );
			leftFlankingField = new JTextField( "20" );
			this.add( leftFlankingField );
			leftFlankingField.setPreferredSize( d );
			leftFlankingField.setSize( d );
			JLabel	rightFlankingLength = new JLabel( "Right Flank Len" );
			this.add( rightFlankingLength );
			rightFlankingField = new JTextField( "20" );
			this.add( rightFlankingField );
			rightFlankingField.setPreferredSize( d );
			rightFlankingField.setSize( d );
			
			final JTextField fileField = new JTextField( "" );
			d = new Dimension( 300,25 );
			fileField.setPreferredSize( d );
			JButton	reload = new JButton( new AbstractAction("Load") {
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = fileField.getText();
					if( text.length() == 0 ) {
						JFileChooser fc = new JFileChooser();
						if( fc.showOpenDialog( Flanking.this ) == JFileChooser.APPROVE_OPTION ) {
							File f = fc.getSelectedFile();
							try {
								text = f.toURI().toURL().toString();
								fileField.setText( text );
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							}
						}
					}
					
					if( text.length() > 0 )
						try {
							load( text );
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
			});
			this.add(reload);
			this.add( fileField );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
		}
		
	}
	
	class Repeat implements Comparable<Repeat> {
		int	start;
		int stop;
		int length;
		
		@Override
		public int compareTo(Repeat o) {
			return start-o.start;
		}
	};
	
	class Sequence {
		String 			name;
		Integer			length;
		StringBuilder	htmlsequence;
		List<Repeat>	_repeats;
		
		public void clear() {
			_repeats.clear();
		}
		
		public boolean hasRepeats() {
			return _repeats.size() > 0; 
		}
		
		public boolean hasRepeats( int length ) {
			for( Repeat r : _repeats ) {
				if( r.length == length ) return true;
			}
			return false;
		}
		
		public boolean hasRepeatsMoreThan( int length ) {
			for( Repeat r : _repeats ) {
				if( r.length > length ) return true;
			}
			return false;
		}
		
		public boolean hasFlankingEnds( int left, int right, int min ) {
			if( _repeats.size() > 0 ) {
				Repeat first = _repeats.get(0);
				Repeat next = first;
				int rlen = next.stop-next.start;
				
				int oold = first.start;
				int nnew = 0;
				
				for( int i = 1; i < _repeats.size(); i++ ) {
					next = _repeats.get(i);
					nnew = next.start - first.stop;
	
					if( rlen >= min && oold > left && nnew > right ) return true;
					oold = nnew;
					first = next;
					rlen = first.stop-first.start;
				}
				
				nnew = length - next.stop;
				if( rlen >= min && oold > left && nnew > right ) return true;
			}
			
			return false;
		}
		
		public boolean hasMinimumRepeatLength( int length ) {
			for( Repeat r : _repeats ) {
				if( r.stop-r.start > length ) return true;
			}
			return false;
		}
		
		/*public boolean hasMinimumRepeatNum( int num ) {
			for( Repeat r : _repeats ) {
				if( r.stop-r.start > length ) return true;
			}
			return false;
		}*/
		
		public Sequence( String name, int length, StringBuilder sequence, int repNum ) {
			int LEN = 4;
			
			this.name = name;
			this.length = length;
			//this.htmlsequence = sequence;
			
			this._repeats = new ArrayList<Repeat>();
			
			for( int k = 0; k < sequence.length()-LEN*4; k++ ) {
				for( int i = 2; i < 5; i++ ) {
					boolean yes = true;
					for( Repeat r : _repeats ) {
						if( r.length != i && k >= r.start && k <= r.stop ) {
							yes = false;
							break;
						}
					}
					
					if( yes ) {
						for( int l = k; l < k+i; l++ ) {
							for( int r = 1; r < repNum; r++ ) {
								if( sequence.charAt(l) != sequence.charAt(l+i*r) ) {
									yes = false;
									break;
								}
							}
							if( !yes ) break;
							/*if( sequence.charAt(l) != sequence.charAt(l+i) || sequence.charAt(l) != sequence.charAt(l+i*2) || sequence.charAt(l) != sequence.charAt(l+i*3) ) {
								yes = false;
								break;
							}*/
						}
						
						if( yes ) {
							Repeat r = new Repeat();
							r.start = k;
							r.length = i;
							
							int u = LEN;
							while( yes ) {
								for( int l = k; l < k+i; l++ ) {
									int val = l+u*i;
									if( val >= sequence.length() || sequence.charAt(l) != sequence.charAt(val) ) {
										yes = false;
										break;
									}
								}
								if( yes ) u++;
							}
							
							r.stop = k+i*u;
							
							k = r.stop;
							
							_repeats.add( r );
						}
					}
				}
			}
			
			Collections.sort( _repeats );
			
			//StringBuffer buf = new StringBuil( sequence );
			
			int offset = 0;
			for( Repeat r : _repeats ) {
				//int startval = r.start+offset;
				/*char c = buf.charAt( startval );
				if( c != 'A' && c != 'C' && c != 'G' && c != 'T' ) {
					System.err.println( startval + "   " + buf.toString() );
				}*/
				if( r.length == 2 ) {
					sequence.insert(r.start+offset, "<b><font color=#00aa00>");
				} else if( r.length == 3 ) {
					sequence.insert(r.start+offset, "<b><font color=#0000aa>");
				} else if( r.length == 4 ) {
					sequence.insert(r.start+offset, "<b><font color=#aa0000>");
				} else if( r.length == 5 ) {
					sequence.insert(r.start+offset, "<b><font color=#aa00aa>");
				}
				offset += 23;
				sequence.insert(r.stop+offset, "</font></b>");
				offset += 11;
			}
			htmlsequence = sequence.insert(0, "<html>").append("</html>"); //"<html>"+sequence.toString()+"</html>";
		}
	};
	
	int row = 0;
	List<Sequence> loadFna( String urlstr ) throws IOException {
		File file = new File( urlstr );
		Reader reader;
		if( !file.exists() ) {
			String[] split = urlstr.split("/");
			String name = split[ split.length-1 ];
			String home = System.getProperty("user.home");
			file = new File( home, name );
			//InputStream stream = null;
			//ByteBuffer bb = null;
			if( file.exists() ) {
				reader = new FileReader( file );
				//bb = ByteBuffer.allocate( (int)file.length() );		
			} else {
				URL url = new URL( urlstr );
				reader = new InputStreamReader( url.openStream() );
				//bb = ByteBuffer.allocate( 1000000 );
			}
		} else {
			reader = new FileReader( file );
		}
		
		int left = 20;
		int right = 20;
		int minSeqLen = 100;
		int minRepeatLen = 50;
		int minRepeatNum = 4;
		
		if( buttons != null ) {
			left = buttons.getLeftFlankingLength();
			right = buttons.getRightFlankingLength();
			minSeqLen = buttons.getMinSeqLength();
			minRepeatLen = buttons.getMinRepeatLength();
			minRepeatNum = buttons.getMinRepeatNum();
		}
		
		List<Sequence>	seqList = new ArrayList<Sequence>();
		
		BufferedReader br = new BufferedReader( reader );
		String line = br.readLine();
		StringBuilder seq = new StringBuilder();
		String head = null;
		int max = 0;
		row = 0;
		while( line != null ) {
			if( line.startsWith(">") ) {
				int seqlen = seq.length();
				System.err.println("trying " + seqlen + "  " + minSeqLen );
				if( seqlen > minSeqLen ) {
					String[] spl = head.split("[ ]+");
					Sequence seqobj = new Sequence( spl[0].substring(1), seqlen, seq, minRepeatNum );
					
					boolean flankingEnds = seqobj.hasFlankingEnds( left, right, minRepeatLen );
					System.err.println( flankingEnds );
					if( seqobj.hasRepeats() && flankingEnds && seqobj.hasMinimumRepeatLength(minRepeatLen) /*&& seqobj.hasMinimumRepeatLength(minRepeatNum)*/ ) {
						if( seqlen > max ) {
							max = seqlen;
							row = seqList.size();
						}
						seqList.add( seqobj );
					} else {
						seqobj.clear();
					}
				}
				seq = new StringBuilder();
				head = line;
			} else {
				seq.append( line );
			}
			line = br.readLine();
		}
		
		int seqlen = seq.length();
		if( seqlen > minSeqLen ) {
			String[] spl = head.split("[ ]+");
			Sequence seqobj = new Sequence( spl[0].substring(1), seqlen, seq, minRepeatNum );
			
			boolean flankingEnds = seqobj.hasFlankingEnds( left, right, minRepeatLen );
			System.err.println( flankingEnds );
			if( seqobj.hasRepeats() && flankingEnds && seqobj.hasMinimumRepeatLength(minRepeatLen) /*&& seqobj.hasMinimumRepeatLength(minRepeatNum)*/ ) {
				if( seqlen > max ) {
					max = seqlen;
					row = seqList.size();
				}
				seqList.add( seqobj );
			} else {
				seqobj.clear();
			}
		}
		
		/*if( !(stream instanceof FileInputStream) ) {
			FileWriter fw = new FileWriter( file );
			fw.write( s );
		}*/
		
		//String s = new String( bb.array() );
		
		return seqList;
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
				Field[] ff = cls != null ? cls.getDeclaredFields() : null;
				if( ff != null ) {
					return Math.max( 0, ff.length-2 );
				}
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "");
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
				return f.getName().startsWith("e_");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	void load( String urlstr ) throws IOException {		
		List<Sequence>	seqList = loadFna( urlstr );
		
		/*int left = 20;
		int right = 20;
		int minRepeatLen = 50;
		int minRepeatNum = 4;
		
		if( buttons != null ) {
			left = buttons.getLeftFlankingLength();
			right = buttons.getRightFlankingLength();
			minRepeatLen = buttons.getMinRepeatLength();
			minRepeatNum = buttons.getMinRepeatNum();
		}
		
		int max = 0;
		int row = 0;
		for( String s : split ) {
			int i = s.indexOf('\n');
			if( i > 0 ) {
				String head = s.substring(0, i);
				String foot = s.substring(i+1);
				String[] spl = head.split("[ ]+");
				String seq = foot.replace("\n", "");
				int seqlen = seq.length();
				Sequence seqobj = new Sequence( spl[0], seqlen, seq, minRepeatNum );
				if( seqobj.hasRepeats() && seqobj.hasFlankingEnds( left, right ) && seqobj.hasMinimumRepeatLength(minRepeatLen) /*&& seqobj.hasMinimumRepeatLength(minRepeatNum)* ) {
					if( seqlen > max ) {
						max = seqlen;
						row = seqList.size();
					}
					seqList.add( seqobj );
				} else {
					seqobj.clear();
				}
			}
		}*/
		System.err.println( seqList.size() );
		
		model = createModel( seqList );
		
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		scrollpane.setViewportView( table );
		
		TableColumnModel cm = table.getColumnModel();
		if( cm.getColumnCount() >= 3 ) {
			TableColumn tc = table.getColumnModel().getColumn(2);
			TableCellRenderer tcr = table.getDefaultRenderer(model.getColumnClass(2));
			Component c = tcr.getTableCellRendererComponent(table,model.getValueAt(row,2),false,false,row,2);
			tc.setPreferredWidth( c.getPreferredSize().width );
		}
		nums.len.setText( Integer.toString(seqList.size()) );
		
		this.repaint();
	}
	
	String[] split;
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
		
		//load( "http://test.matis.is/flanking/2.TCA.454Reads.fna" );
		
		buttons = new ControlComp();
		nums = new NumComp();
		
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		comp.add( scrollpane );
		this.add( nums, BorderLayout.NORTH );
		comp.add( buttons, BorderLayout.SOUTH );
		this.add( comp );
	}
}
