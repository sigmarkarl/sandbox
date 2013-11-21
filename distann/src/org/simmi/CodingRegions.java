package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class CodingRegions {
	public void coderegPlot( GeneSet geneset, Container comp ) {
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
		table1.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		JScrollPane	scroll1 = new JScrollPane( table1 );
		
		JOptionPane.showMessageDialog(comp, scroll1);
		
		String spec = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final BufferedImage bimg = new BufferedImage( 2048, 2048, BufferedImage.TYPE_INT_ARGB );
		final Graphics2D g2 = bimg.createGraphics();
		
		final List<Contig> contigs = geneset.speccontigMap.get( spec );
		draw( g2, spec, geneset, bimg.getWidth(), bimg.getHeight(), contigs );
		JComponent cmp = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				//draw( g2 );
				g2.drawImage(bimg, 0, 0, this);
			}
		};
		
		Dimension dim = new Dimension( 2048, 2048 );
		cmp.setPreferredSize( dim );
		cmp.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( cmp );
		
		JToolBar	toolbar = new JToolBar();
		/*toolbar.add( specombo );
		toolbar.add( relcol );
		toolbar.add( gccol );
		toolbar.add( syntcol );
		toolbar.add( brcol );
		toolbar.add( syntgrad );*/
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( scrollpane );
		
		JFrame frame = new JFrame();
		frame.add( panel );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void draw( Graphics2D g2, String spec1, GeneSet geneset, int w, int h, Collection<Contig> contigs ) {		
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        g2.setBackground( Color.white );
		g2.clearRect( 0, 0, w, h );
		g2.setColor( Color.black );
		
		int total = 0;
		for( Contig ctg : contigs ) {
			total += ctg.length();
		}
		System.err.println( total );
		
		int count = 0;
		for( Contig ctg : contigs ) {
			if( ctg.tlist != null ) {
				for( Tegeval tv : ctg.tlist ) {						
					//double theta = count*Math.PI*2.0/total;
					double theta = (count+(tv.stop+tv.start)/2.0)*Math.PI*2.0/total;
					double theta1 = (count+tv.start)*Math.PI*2.0/total;
					double theta2 = (count+tv.stop)*Math.PI*2.0/total;
					
					double val = (tv.stop-tv.start)*1440*Math.PI/total;
					
					//System.err.println( thetam );
					//g2.fillArc(512, 512, 2048-1024, 2048-1024, theta1, theta2-theta1);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					//g2.fillRect( 640, -1, 50, 3);
					g2.fillRect( 720, (int)Math.min(-1.0, -val/2.0), 50, (int)Math.max(3.0, val/2.0));
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
				}
			
				g2.setColor( Color.black );
				double theta = count*Math.PI*2.0/total;
				g2.translate( w/2, h/2 );
				g2.rotate( theta );
				int x = 720+50;
				g2.drawLine( x, 0, x+15, 0);
				g2.rotate( -theta );
	            g2.translate( -w/2, -h/2 );
			}
			count += ctg.length();
		}
		
		g2.setColor( Color.black );
		g2.setFont( g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f) );
		String[] specsplit = spec1.split("_");
		int k = 0;
		for( String spec : specsplit ) {
			int strw = g2.getFontMetrics().stringWidth( spec );
			g2.drawString( spec, (w-strw)/2, h/2 - specsplit.length*32/2 + 32 + k*32 );
			k++;
		}
	}
}
