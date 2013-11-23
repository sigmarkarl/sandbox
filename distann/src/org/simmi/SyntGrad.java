package org.simmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SyntGrad {
	public void syntGrad( final GeneSet geneset ) {
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
		
		final String 		spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<Contig>	contigs1 = geneset.speccontigMap.get( spec1 );
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			String spec2 = (String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final BufferedImage bi = new BufferedImage( 2048, 2048, BufferedImage.TYPE_INT_ARGB );
		
		final Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		drawImage( geneset, g2, spec1, contigs1, spec2s );
		
		final JComponent c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.drawImage(bi,0,0,this);
			}
		};
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Sort contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( String spec : spec2s ) {
					if( !spec.equals(spec1) ) {
						List<Contig> scontigs = geneset.speccontigMap.get( spec );
						Collections.sort( scontigs, new Comparator<Contig>() {
							@Override
							public int compare(Contig o1, Contig o2) {
								List<Double> ratios = new ArrayList<Double>();
								if( o1.tlist != null ) {
									for( Tegeval tv : o1.tlist ) {
										double val = invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup());
										if( val != -1 ) ratios.add( val );
									}
								}
								Collections.sort( ratios );
								double r1 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;
								
								ratios = new ArrayList<Double>();
								if( o2.tlist != null ) {
									for( Tegeval tv : o2.tlist ) {
										double val = invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup());
										if( val != -1 ) ratios.add( val );
									}
								}
								Collections.sort( ratios );
								double r2 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;
								
								return Double.compare(r1, r2);
							}
						});
						for( Contig c : scontigs ) {
							List<Double>	dvals = new ArrayList<Double>();
							Tegeval tv = c.getFirst();
							while( tv != null ) {
								Tegeval next = c.getNext( tv );
								if( next != null ) {
									double val1 = invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup());
									double val2 = invertedGradientRatio(spec1, contigs1, -1.0, next.getGene().getGeneGroup());
									
									if( val1 != -1.0 && val2 != -1.0 ) dvals.add( val2-val1 );
								}
								tv = next;
							}
							if( dvals.size() > 0 ) {
								Collections.sort( dvals );
								if( dvals.get( dvals.size()/2 ) < 0 ) {
									c.setReverse( !c.isReverse() );
								}
							}
						}
					}
				}
				drawImage( geneset, g2, spec1, contigs1, spec2s );
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ImageIO.write(bi, "png", new File("/tmp/out.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		c.setComponentPopupMenu( popup );
		
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
	
	public void drawImage( GeneSet geneset, Graphics2D g2, String spec1, List<Contig> contigs1, List<String> spec2s ) {
		int rad = 250;
		g2.setColor( Color.white );
		g2.fillRect( 0, 0, 2048, 2048 );
		for( String spec : spec2s ) {
			List<Contig> scontigs = geneset.speccontigMap.get( spec );
			int total = 0;
			for( Contig c : scontigs ) {
				total += c.getGeneCount();
			}
			
			int tvn = 0;
			for( Contig c : scontigs ) {
				Tegeval tv = c.getFirst();
				while( tv != null ) {
					GeneGroup gg = tv.getGene().getGeneGroup();
					if( gg != null ) {
						double r = 2.0*Math.PI*(double)tvn/(double)total;
						double ratio = invertedGradientRatio( spec1, contigs1, r, gg );
						if( ratio >= 0.0 ) {
							g2.translate(1024, 1024);
							g2.rotate( r );
							g2.setColor( invertedGradientColor( ratio ) );						
							g2.drawLine(rad, 0, rad+24, 0);
							g2.rotate( -r );
							g2.translate(-1024, -1024);
						}
					}
					tvn++;
					tv = c.getNext( tv );
				}
				double r = 2.0*Math.PI*(double)tvn/(double)total;
				g2.translate(1024, 1024);
				g2.rotate( r );
				g2.setColor( Color.black );
				g2.drawLine(rad, 0, rad+100, 0);
				g2.rotate( -r );
				g2.translate(-1024, -1024);
			}
			
			rad += 24;
		}
		
		g2.setColor( Color.black );
		g2.setFont( g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f) );
		String[] specsplit = spec1.split("_");
		int k = 0;
		for( String spec : specsplit ) {
			int strw = g2.getFontMetrics().stringWidth( spec );
			g2.drawString( spec, 1024-strw/2, 1024 - specsplit.length*32/2 + 32 + k*32 );
			k++;
		}
	}
	
	public static double invertedGradientRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		if( gene2s != null ) for( Tegeval tv2 : gene2s.tset ) {
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
