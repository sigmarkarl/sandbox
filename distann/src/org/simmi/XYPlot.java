package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class XYPlot {
	static final List<Contig>	spec1Conts = new ArrayList<Contig>();
	static final List<Contig>	spec2Conts = new ArrayList<Contig>();
	static int fsum1;
	static int fsum2;
	
	public static void initSpecConts( String spec1, String spec2 ) {
		spec1Conts.clear();
		spec2Conts.clear();
		
		for( String ctname : GeneSet.contigmap.keySet() ) {
			if( ctname.contains( spec1 ) ) {
				spec1Conts.add( GeneSet.contigmap.get( ctname ) );
			}
			
			if( ctname.contains( spec2 ) ) {
				spec2Conts.add( GeneSet.contigmap.get( ctname ) );
			}
		}
		
		System.err.println( spec1Conts.size() + "  " + spec2Conts.size() );
		
		int sum1 = 0;
		for( Contig ct : spec1Conts ) {
			sum1 += ct.getGeneCount();
		}
		fsum1 = sum1;
		
		int sum2 = 0;
		for( Contig ct : spec2Conts ) {
			sum2 += ct.getGeneCount();
		}
		fsum2 = sum2;
		
		System.err.println( fsum1 + "  " + fsum2 );
	}
	
	static Point	mouseSel;
	public static void xyPlot( final Container comp, final List<Gene> genelist, final JTable table, Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) {
		final Set<String> 	specset = GeneSet.speciesFromCluster( clusterMap );
		final List<String>	species = new ArrayList<String>( specset );
		final List<String>	specList = new ArrayList<String>( species );
		
		TableModel model = new TableModel() {
			@Override
			public int getRowCount() {
				return specList.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return specList.get( rowIndex );
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		JTable table1 = new JTable( model );
		JTable table2 = new JTable( model );
		
		table1.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		table2.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		JScrollPane	scroll1 = new JScrollPane( table1 );
		JScrollPane	scroll2 = new JScrollPane( table2 );
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		
		c.add( scroll1 );
		c.add( scroll2 );
		
		JOptionPane.showMessageDialog(comp, c);
		
		final String spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final String spec2 = (String)table2.getValueAt( table2.getSelectedRow(), 0 );
		
		initSpecConts(spec1, spec2);
		
		final JRadioButton	oricolor = new JRadioButton("Orientation");
		final JRadioButton	gccolor = new JRadioButton("GC%");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( oricolor );
		bg.add( gccolor );
		
		final JComponent  drawc = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.setColor( Color.white );
				g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				//g.setColor( Color.blue );
				
				int count = 0;
				for( Contig ct : spec1Conts ) {
					Tegeval val = ct.getFirst();
					while( val != null ) {
						GeneGroup gg = val.getGene().getGeneGroup();
						
						int a = GeneSet.allgenegroups.indexOf( gg );
						int l = table.convertRowIndexToView( a );
							
						if( l != -1 ) {
							boolean rs = table.isRowSelected( l );
							List<Tegeval> tv2list = gg.getTegevals( spec2 );
							for( Tegeval tv2 : tv2list ) {
								tv2.setSelected( rs );
								int count2 = 0;
								int k = spec2Conts.indexOf( tv2.contshort );
								if( k != -1 ) {
									for( int i = 0; i < k; i++ ) {
										Contig ct2 = spec2Conts.get( i );
										count2 += ct2.getGeneCount();
									}
									Contig ct2 = spec2Conts.get( k );
									count2 += (ct2.isReverse() ? ct2.getGeneCount() - tv2.getNum() - 1 : tv2.getNum());
									
									if( gccolor.isSelected() ) {
										double gc = (val.getGCPerc()+tv2.getGCPerc())/2.0;
										double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
										g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
									} else {
										if( val.isSelected() || tv2.isSelected() ) g.setColor( Color.red );
										else g.setColor( Color.blue );
									}
									g.fillOval( (int)((count-1)*this.getWidth()/fsum1), (int)((count2-1)*this.getHeight()/fsum2), 3, 3);
								} else {
									//System.err.println();
								}
							}
						}
						
						val = val.getNext();
						count++;
						
						//System.err.println( count );
					}
				}
			}
		};
		
		drawc.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseSel = e.getPoint();
				if( e.getClickCount() == 2 ) {
					for( Contig ct : spec1Conts ) {
						Tegeval tv = ct.getFirst();
						while( tv != null ) {
							tv.setSelected( false );
							tv = tv.getNext();
						}
					}
					
					for( Contig ct : spec2Conts ) {
						Tegeval tv = ct.getFirst();
						while( tv != null ) {
							tv.setSelected( false );
							tv = tv.getNext();
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Point relmouse = e.getPoint();
				
				int xmin = Math.min( mouseSel.x, relmouse.x )*fsum1/drawc.getWidth();
				int xmax = Math.max( mouseSel.x, relmouse.x )*fsum1/drawc.getWidth();
				for( int x = xmin; x < xmax; x++ ) {
					int count = 0;
					Contig tct = null;
					for( Contig ct : spec1Conts ) {
						if( ct.getGeneCount()+count < x ) count += ct.getGeneCount();
						else {
							tct = ct;
							break;
						}
					}
					Tegeval te = tct.getIndex( x-count );
					te.setSelected( true );
					
					int i;
					if( table.getModel() == GeneSet.groupModel ) {
						i = GeneSet.allgenegroups.indexOf( te.getGene().getGeneGroup() );
					} else {
						i = genelist.indexOf( te.getGene() );
					}
					int r = table.convertRowIndexToView(i);
					if( te.isSelected() ) {
						GeneSet.currentTe = te;
						if( r >= 0 && r < table.getRowCount() ) {
							table.addRowSelectionInterval(r, r);
							table.scrollRectToVisible( table.getCellRect(r, 0, false) );
						}
					} else {
						if( r >= 0 && r < table.getRowCount() ) {
							table.removeRowSelectionInterval(r, r);
							table.scrollRectToVisible( table.getCellRect(r, 0, false) );
						}
					}
				}
				
				int ymin = Math.min( mouseSel.y, relmouse.y )*fsum2/drawc.getHeight();
				int ymax = Math.max( mouseSel.y, relmouse.y )*fsum2/drawc.getHeight();
				for( int y = ymin; y < ymax; y++ ) {
					int count = 0;
					Contig tct = null;
					for( Contig ct : spec2Conts ) {
						if( ct.getGeneCount()+count < y ) count += ct.getGeneCount();
						else {
							tct = ct;
							break;
						}
					}
					Tegeval te = tct.getIndex( y-count );
					te.setSelected( true );
					
					int i;
					if( table.getModel() == GeneSet.groupModel ) {
						i = GeneSet.allgenegroups.indexOf( te.getGene().getGeneGroup() );
					} else {
						i = genelist.indexOf( te.getGene() );
					}
					int r = table.convertRowIndexToView(i);
					if( te.isSelected() ) {
						GeneSet.currentTe = te;
						if( r >= 0 && r < table.getRowCount() ) {
							table.addRowSelectionInterval(r, r);
							table.scrollRectToVisible( table.getCellRect(r, 0, false) );
						}
					} else {
						if( r >= 0 && r < table.getRowCount() ) {
							table.removeRowSelectionInterval(r, r);
							table.scrollRectToVisible( table.getCellRect(r, 0, false) );
						}
					}
				}
				
				drawc.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
						
		Dimension dim = new Dimension( fsum1, fsum2 );
		drawc.setPreferredSize( dim );
		drawc.setSize( dim );
		JScrollPane	drawscroll = new JScrollPane( drawc );

		JToolBar	toolbox = new JToolBar();
		toolbox.add( oricolor );
		toolbox.add( gccolor );
		toolbox.add( new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(drawc.getWidth()*1.25), (int)(drawc.getHeight()*1.25) );
				drawc.setPreferredSize( dim );
				drawc.setSize( dim );
			}
		});
		toolbox.add( new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(drawc.getWidth()*0.8), (int)(drawc.getHeight()*0.8) );
				drawc.setPreferredSize( dim );
				drawc.setSize( dim );
			}
		});
		
		final JComboBox<String>	comb1 = new JComboBox<String>();
		final JComboBox<String>	comb2 = new JComboBox<String>();
		
		ComboBoxModel<String>	cbmodel1 = new ComboBoxModel<String>() {
			String sel;
			
			@Override
			public int getSize() {
				return species.size();
			}

			@Override
			public String getElementAt(int index) {
				return species.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				
			}

			@Override
			public void setSelectedItem(Object anItem) {
				sel = (String)anItem;
			}

			@Override
			public Object getSelectedItem() {
				return sel;
			}
		};
		comb1.setModel( cbmodel1 );
		
		ComboBoxModel<String>	cbmodel2 = new ComboBoxModel<String>() {
			String sel;
			
			@Override
			public int getSize() {
				return species.size();
			}

			@Override
			public String getElementAt(int index) {
				return species.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {}

			@Override
			public void removeListDataListener(ListDataListener l) {}

			@Override
			public void setSelectedItem(Object anItem) {
				sel = (String)anItem;
			}

			@Override
			public Object getSelectedItem() {
				return sel;
			}
		};
		comb2.setModel( cbmodel2 );
		
		comb1.setSelectedItem( spec1 );
		comb2.setSelectedItem( spec2 );
		
		comb1.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				initSpecConts( (String)comb1.getSelectedItem(), (String)comb2.getSelectedItem() );
				drawc.repaint();
			}
		});
		comb2.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				initSpecConts( (String)comb1.getSelectedItem(), (String)comb2.getSelectedItem() );
				drawc.repaint();
			}
		});
		
		toolbox.add( comb1 );
		toolbox.add( comb2 );
		
		oricolor.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				drawc.repaint();
			}
		});
		gccolor.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				drawc.repaint();
			}
		});
		
		JFrame frame = new JFrame();
		frame.setLayout( new BorderLayout() );
		frame.add( toolbox, BorderLayout.NORTH );
		frame.add( drawscroll );
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
}
