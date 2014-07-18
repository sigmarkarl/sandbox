package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.shared.Annotation;

public class SyntGrad {
	JCheckBox	contcheck = new JCheckBox("Show contig lines");
	public void syntGrad( final GeneSet geneset ) {
		final JTable 				table = geneset.getGeneTable();
		//final Collection<String> 	specset = geneset.getSelspec(geneset, geneset.getSpecies(), (JCheckBox[])null); 
		final Collection<String>	specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
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
				return geneset.nameFix( species.get( rowIndex ) );
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
		
		int sr = table1.getSelectedRow();
		final String 		spec1 = sr != -1 ? species.get( table1.convertRowIndexToModel( sr )) : null; //(String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<Contig>	contigs1 = spec1 != null ? geneset.speccontigMap.get( spec1 ) : null;
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			//String spec2 = (String)table2.getValueAt(r, 0);
			String spec2 = species.get( table2.convertRowIndexToModel(r) );
			spec2s.add( spec2 );
		}
		
		final BufferedImage bi = new BufferedImage( 2048, 2048, BufferedImage.TYPE_INT_ARGB );
		
		final Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		drawImage( geneset, g2, spec1, contigs1, spec2s );
		
		final JComponent c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.drawImage(bi,0,0,this);
			}
		};
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Repaint") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawImage( geneset, g2, spec1, contigs1, spec2s );
				c.repaint();
			}
		});
		popup.addSeparator();
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
								if( o1.annset != null ) {
									for( Annotation ann : o1.annset ) {
										Tegeval tv = (Tegeval)ann;
										double val = tv.getGene() != null ? invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup()) : -1;
										if( val != -1 ) ratios.add( val );
									}
								}
								Collections.sort( ratios );
								double r1 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;
								
								ratios = new ArrayList<Double>();
								if( o2.annset != null ) {
									for( Annotation ann : o2.annset ) {
										Tegeval tv = (Tegeval)ann;
										double val = tv.getGene() != null ? invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup()) : -1;
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
									double val1 = tv.getGene() != null ? invertedGradientRatio(spec1, contigs1, -1.0, tv.getGene().getGeneGroup()) : -1;
									double val2 = next.getGene() != null ? invertedGradientRatio(spec1, contigs1, -1.0, next.getGene().getGeneGroup()) : -1;
									
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
					ImageIO.write(bi, "png", new File("c:/out.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		c.setComponentPopupMenu( popup );
		
		c.addMouseListener( new MouseListener() {
			Point p;
			boolean doubleclicked = false;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Container comp = geneset;
				Point np = e.getPoint();
				if( p != null ) {
					doubleclicked = doubleclicked || e.getClickCount() == 2;
					
					double ndx = np.x-bi.getWidth()/2;
					double ndy = np.y-bi.getHeight()/2;
					
					double dx = p.x-bi.getWidth()/2;
					double dy = p.y-bi.getHeight()/2;
					
					if( doubleclicked ) {						
						/*double t = Math.atan2( dy, dx );
						double rad = Math.sqrt( dx*dx + dy*dy );
						
						double nt = Math.atan2( ndy, ndx );
						double nrad = Math.sqrt( ndx*ndx + ndy*ndy );
						
						if( t < 0 ) t += Math.PI*2.0;
						int mloc = (int)(t*size/(2*Math.PI));
						
						if( nt < 0 ) nt += Math.PI*2.0;
						int nloc = (int)(nt*size/(2*Math.PI));
						
						int ind = (int)((rad-250.0)/15.0);
						if( ind >= 0 && ind < spec2s.size() ) {
							String spec = spec2s.get( ind );
							
							int i = 0;
							int loc = 0;
							for( i = 0; i < contigs.size(); i++ ) {
								Contig c = contigs.get(i);
								if( loc + c.getGeneCount() > mloc ) {
									break;
								} else loc += c.getGeneCount();
							}
							Contig c = contigs.get(i);
							
							if( mloc-loc < c.getGeneCount() ) {
								//loc += c.getGeneCount();
								//c = contigs.get( i%contigs.size() );
								Tegeval tv = c.annset.get(mloc-loc);
								Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
								
								if( ti != null && ti.best != null ) {
									Contig ct1 = ti.best.getContshort();
									
									tv = c.annset.get(nloc-loc);
									ti = tv.getGene().getGeneGroup().getGenes(spec);
									Contig ct2 = ti.best.getContshort();
									
									if( ct1 == ct2 ) ct1.setReverse( !ct1.isReverse() );
									else {
										List<Contig> conts2 = geneset.speccontigMap.get(spec);
										int k2 = conts2.indexOf( ct2 );
										conts2.remove( ct1 );
										conts2.add(k2, ct1);
									}
								}
							}
						}
						
						//repaintCompare( g2, bi, spec2s, specombo, geneset, blosumap, cmp );*/
					} else {						
						double t1 = Math.atan2( dy, dx );
						double t2 = Math.atan2( ndy, ndx );
						
						double rad = Math.sqrt( dx*dx + dy*dy );
						
						int ind = (int)((rad-250.0)/15.0);
						String spec = ind >= 0 && ind < spec2s.size() ? spec2s.get( ind ) : null;
						
						if( t1 < 0 ) t1 += Math.PI*2.0;
						if( t2 < 0 ) t2 += Math.PI*2.0;
						
						if( spec != null ) {
							List<Contig> contigs = geneset.speccontigMap.get( spec );
							int total = 0;
							for( Contig c : contigs ) {
								total += c.getGeneCount();
							}
							
							int loc1 = (int)(t1*total/(2*Math.PI));
							int loc2 = (int)(t2*total/(2*Math.PI));
							
							int minloc = Math.min( loc1, loc2 );
							int maxloc = Math.max( loc1, loc2 );
							
							int i = 0;
							int loc = 0;
							Contig c = null;
							if( contigs != null ) {
								for( i = 0; i < contigs.size(); i++ ) {
									c = contigs.get(i);
									if( loc + c.getGeneCount() > minloc ) {
										break;
									} else loc += c.getGeneCount();
								}
								//c = contigs.get(i);
							}
							
							if( e.isAltDown() ) {
								/*Tegeval tv1 = c.annset.get(minloc-loc);
								Tegeval tv2 = c.annset.get(maxloc-loc);
								
								int from = Math.min( tv1.start, tv2.start );
								int to = Math.max( tv1.stop, tv2.stop );
								String seqstr = c.getSubstring( from, to, 1 );
							
								Sequence seq = new Sequence("phage_"+from+"_"+to, null);
								seq.append( seqstr );
								geneset.showSomeSequences( geneset, Arrays.asList( new Sequence[] {seq} ) );*/
							} else {
								geneset.table.clearSelection();
								if( c == null ) {
									for( int k = minloc; k < maxloc; k++ ) {
										geneset.table.addRowSelectionInterval(k, k);
									}
								} else for( int k = minloc; k < maxloc; k++ ) {
									if( k-loc >= c.getGeneCount() ) {
										loc += c.getGeneCount();
										i++;
										c = contigs.get( i%contigs.size() );
									}
									Tegeval tv = (Tegeval)(c.isReverse() ? c.annset.get( c.annset.size()-1-(k-loc) ) : c.annset.get(k-loc));
									if( e.isShiftDown() ) {
										Set<GeneGroup>	gset = new HashSet<GeneGroup>();
										gset.add( tv.getGene().getGeneGroup() );
										try {
											new Neighbour( gset ).neighbourMynd( geneset, comp, geneset.genelist, geneset.contigmap );
										} catch (IOException e1) {
											e1.printStackTrace();
										}
										break;
									} else {
										int r;
										if( geneset.table.getModel() == geneset.groupModel ) {
											int u = geneset.allgenegroups.indexOf( tv.getGene().getGeneGroup() );
											r = geneset.table.convertRowIndexToView(u);
											geneset.table.addRowSelectionInterval( r, r );
										} else {
											Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
											int selr = -1;
											for( Tegeval te : ti.tset ) {
												int u = geneset.genelist.indexOf( te.getGene() );
												r = geneset.table.convertRowIndexToView(u);
												if( selr == -1 ) selr = r;
												geneset.table.addRowSelectionInterval( r, r );
											}
											Rectangle rect = geneset.table.getCellRect(selr, 0, true);
											geneset.table.scrollRectToVisible(rect);
										}
									}
								}
							}
						}
					}
				}
				doubleclicked = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
				doubleclicked = e.getClickCount() == 2; 
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				p = e.getPoint();
			}
		});
		
		Dimension dim = new Dimension( 2048, 2048 );
		c.setPreferredSize( dim );
		c.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( c );
		
		/*JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( tb, BorderLayout.NORTH );
		panel.add( scrollpane );*/
		
		JToolBar toolbar = new JToolBar();
		toolbar.add( contcheck );
		
		JFrame frame = new JFrame();
		frame.add( toolbar, BorderLayout.NORTH );
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void drawImage( GeneSet geneset, Graphics2D g2, String spec1, List<Contig> contigs1, List<String> spec2s ) {
		int w = 2048;
		int h = 2048;
		
		int w2 = w/2;
		int h2 = h/2;
		
		int rad = 250;
		g2.setColor( Color.white );
		g2.fillRect( 0, 0, w, h );
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
					Gene gene = tv.getGene();
					GeneGroup gg = gene != null ? gene.getGeneGroup() : null;
					double r = 2.0*Math.PI*(double)tvn/(double)total;
					
					boolean visible = false;
					if( spec1 != null ) {
						if( gg != null ) {
							visible = true;
							double ratio = invertedGradientRatio( spec1, contigs1, r, gg );
							if( ratio >= 0.0 ) {
								g2.setColor( invertedGradientColor( ratio ) );						
							}
						} else g2.setColor( Color.white );
					} else {
						boolean phage = gene.isPhage();
						boolean plasmid = tv.getContshort().isPlasmid();
						
						int count = 1;
						if( gg != null ) {
							Teginfo ti = gg.getGenes( gene.getSpecies() );
							count = ti.tset.size();
							
							int i = geneset.allgenegroups.indexOf(gg);
							if( i != -1 ) {
								int rv = geneset.table.convertRowIndexToView(i);
								if( rv != -1 ) {
									visible = geneset.table.isRowSelected( rv );
								}
							}
						}
						
						Color color = Color.white;
						if( phage && plasmid ) {
							if( count > 1 ) color = darkmag;
							else color = Color.magenta;
						} else if( phage ) {
							if( count > 1 ) color = darkblue;
							else color = Color.blue;
						} else if( plasmid ) {
							if( count > 1 ) color = darkred;
							else color = Color.red;
						} else {
							if( count > 1 ) color = Color.gray;
							else color = Color.lightGray;
						}
						g2.setColor( color );
					}
					
					if( visible ) {
						g2.translate(w2, h2);
						g2.rotate( r );
						g2.fillRect(rad, -1, 15, 3);
						//g2.drawLine(rad, 0, rad+15, 0);
						g2.rotate( -r );
						g2.translate(-w2, -h2);
					}
					
					Tegeval prev = tv;
					
					tvn++;
					tv = c.getNext( tv );
					
					if( tv != null && tv.start == prev.start ) {
						System.err.println( tv.name + "   " + prev.name );
						System.err.println();
					}
					
					/*if( tv == c.getFirst() ) {
						break;
					}*/
				}
				
				if( contcheck.isSelected() ) {
					double r = 2.0*Math.PI*(double)tvn/(double)total;
					g2.translate(w2, h2);
					g2.rotate( r );
					g2.setColor( Color.black );
					g2.drawLine(rad, 0, rad+100, 0);
					g2.rotate( -r );
					g2.translate(-w2, -h2);
				}
			}
			
			rad += 15;
		}
		
		g2.setColor( Color.black );
		Font oldfont = g2.getFont().deriveFont( Font.ITALIC ).deriveFont( spec2s.size() > 10 ? 11.0f : 21.0f );
		int val = spec2s.size() > 10 ? 12 : 23;
		g2.setFont( oldfont );
		int k = 0;
		for( String spec : spec2s ) {
			if( spec.equals(spec1) ) {
				g2.setFont( oldfont.deriveFont(Font.BOLD | Font.ITALIC) );
			} else {
				g2.setFont( oldfont );
			}
			
			String specstr = geneset.nameFix( spec );
			/*if( spec.contains("hermus") ) {
				int u = spec.indexOf("_uid");
				if( u == -1 ) u = spec.length();
				specstr = spec.substring(0, u);
			} else {
				Matcher m = Pattern.compile("\\d").matcher(spec); 
				int firstDigitLocation = m.find() ? m.start() : 0;
				if( firstDigitLocation == 0 ) specstr = "Thermus_"+spec;
				else specstr = "Thermus_" + spec.substring(0,firstDigitLocation) + "_" + spec.substring(firstDigitLocation);
			}*/
			
			if( specstr.length() > 30 ) specstr = specstr.substring(0, specstr.lastIndexOf('_'));
			
			int strw = g2.getFontMetrics().stringWidth( specstr );
			g2.drawString( specstr, (w-strw)/2, h/2 - spec2s.size()*val/2 + val + k*val );
			k++;
		}
		
		/*if( spec1 != null ) {
			g2.setColor( Color.black );
			g2.setFont( g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f) );
			String[] specsplit = spec1.split("_");
			int k = 0;
			for( String spec : specsplit ) {
				int strw = g2.getFontMetrics().stringWidth( spec );
				g2.drawString( spec, 1024-strw/2, 1024 - specsplit.length*32/2 + 32 + k*32 );
				k++;
			}
		}*/
	}
	final Color darkgreen = new Color( 0, 128, 0 );
	final Color darkred = new Color( 128, 0, 0 );
	final Color darkblue = new Color( 0, 0, 128 );
	final Color darkmag = new Color( 128, 0, 128 );
	
	public static double invertedGradientRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg != null ? gg.getGenes( spec2 ) : null;
		if( gene2s != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.annset != null ) {
					int idx = ctg2.annset.indexOf( tv2 );
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
