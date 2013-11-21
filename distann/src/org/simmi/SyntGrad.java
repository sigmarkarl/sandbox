package org.simmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SyntGrad {
	public void syntGrad( GeneSet geneset ) {
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
		JComponent cmp = new JComponent() {};
		cmp.setLayout( flowlayout );
		
		cmp.add( scroll1 );
		cmp.add( scroll2 );
		
		JOptionPane.showMessageDialog(geneset, cmp);
		
		String 				spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		List<Contig>		contigs1 = geneset.speccontigMap.get( spec1 );
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			String spec2 = (String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final BufferedImage bi = new BufferedImage( 1024, 1024, BufferedImage.TYPE_INT_ARGB );
		
		int rad = 150;
		Graphics2D g2 = bi.createGraphics();
		for( String spec : spec2s ) {
			List<Contig> scontigs = geneset.speccontigMap.get( spec );
			int total = 0;
			for( Contig c : scontigs ) {
				total += c.tlist.size();
			}
			
			int tvn = 0;
			for( Contig c : scontigs ) {
				for( Tegeval tv : c.tlist ) {
					double r = 2.0*Math.PI*(double)tvn/(double)total;
					g2.translate(1024, 1024);
					g2.rotate( r );
					GeneGroup gg = tv.getGene().getGeneGroup();
					if( gg == null ) g2.setColor( Color.black );
					else {
						double ratio = invertedGradientRatio( spec1, contigs1, r, gg );
						g2.setColor( invertedGradientColor( ratio ) );
					}
					g2.drawLine(rad, 0, rad+15, 0);
					g2.rotate( -r );
					g2.translate(-1024, -1024);
					tvn++;
				}
				double r = 2.0*Math.PI*(double)tvn/(double)total;
				g2.translate(1024, 1024);
				g2.rotate( r );
				g2.setColor( Color.black );
				g2.drawLine(rad, 0, rad+100, 0);
				g2.rotate( -r );
				g2.translate(-1024, -1024);
			}
			
			rad += 15;
		}
		
		JComponent c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.drawImage(bi,0,0,this);
			}
		};
		
		Dimension dim = new Dimension( 2048, 2048 );
		c.setPreferredSize( dim );
		c.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( c );
		
		/*JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( tb, BorderLayout.NORTH );
		panel.add( scrollpane );*/
		
		JFrame frame = new JFrame();
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public static double invertedGradientRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.tlist != null ) {
					int idx = ctg2.tlist.indexOf( tv2 );
					if( idx == -1 ) {
						count2 += ctg2.getGeneCount();
					} else {
						count2 += ctg2.isReverse() ? ctg2.getGeneCount() - idx - 1 : idx; 
						break;
					}
				}
			}
			double rat2 = (double)count2/(double)total2;
			 
			if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) ratio2 = rat2;
			//ratio2 = rat2;
			//break;
		}
		return ratio2;
	}
	
	public static Color invertedGradientColor( double ratio ) {		
		//float green = (float)(1.0-ratio2);
		/*if( ratio2 < 0.5 ) {
			Color c = new Color(0,(float)(ratio2*2.0),1.0f);
			g2.setColor( c );
		} else {
			Color c = new Color(0,1.0f,(float)((1.0-ratio2)*2.0));
			g2.setColor( c );
		}*/
		
		Color c = Color.black;
		if( ratio < 1.0/6.0 ) {
			c = new Color(0.0f,(float)(ratio*6.0),1.0f);
		} else if( ratio < 2.0/6.0 ) {
			c = new Color(0.0f,1.0f,(float)((2.0/6.0-ratio)*6.0));
		} else if( ratio < 3.0/6.0 ) {
			c = new Color((float)((ratio-2.0/6.0)*6.0),1.0f,0.0f);
		} else if( ratio < 4.0/6.0 ) {
			c = new Color(1.0f,(float)((4.0/6.0-ratio)*6.0),0.0f);
		} else if( ratio < 5.0/6.0 ) {
			c = new Color(1.0f,0.0f,(float)((ratio-4.0/6.0)*6.0));
		} else {
			c = new Color((float)((1.0-ratio)*6.0),0.0f,1.0f);
		}
		
		return c;
	}
}
