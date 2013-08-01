package org.simmi;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class GeneCompare {
	public void comparePlot(  final GeneSet geneset, final Container comp, final List<Gene> genelist, Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) {
		final JTable 				table = geneset.getGeneTable();
		final Collection<String> 	specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
		final List<String>			species = new ArrayList<String>( specset );
		
		TableModel model = new TableModel() {
			@Override
			public int getRowCount() {
				return species.size();
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
				return species.get( rowIndex );
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
		table2.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		JScrollPane	scroll1 = new JScrollPane( table1 );
		JScrollPane	scroll2 = new JScrollPane( table2 );
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		
		c.add( scroll1 );
		c.add( scroll2 );
		
		JOptionPane.showMessageDialog(comp, c);
		
		String spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			String spec2 = (String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
		JComponent cmp = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
                                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                                g2.clearRect( 0, 0, this.getWidth(), this.getHeight() );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				g.setColor( Color.black );
				int count = 0;
				for( Contig ctg : contigs ) {
					for( Tegeval tv : ctg.tlist ) {
						GeneGroup gg = tv.getGene().getGeneGroup();
						int scount = 0;
						for( String spec2 : spec2s ) {
							if( gg.species.containsKey(spec2) ) {
                                Gene gene2 = gg.getGene( spec2 );
                                Tegeval tv2;
                                for( Tegeval tval : gene2.teginfo.tset ) {
                                    tv2 = tval;
                                    break;
                                }
                                StringBuilder seq = tv.getAlignedSequence();
                                StringBuilder seq2 = tv2.getAlignedSequence();
								double theta = count*Math.PI*2.0/total;
								g2.translate( 1024, 1024 );
								g2.rotate( theta );
								g2.fillRect( 502+20*(scount++), -1, 20, 3);
								g2.rotate( -theta );
                                g2.translate( -1024, -1024 );
							}
						}
						count++;
					}
				}
			}
		};
		Dimension dim = new Dimension( 2048, 2048 );
		cmp.setPreferredSize( dim );
		cmp.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( cmp );
		JFrame frame = new JFrame();
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
}
