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
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
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
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;

public class SyntGrad {
	JCheckBox	contcheck = new JCheckBox("Show contig lines");
	JCheckBox	selcheck = new JCheckBox("Show lines for selection");
	JCheckBox	vischeck = new JCheckBox("Table visibility");
	JCheckBox	syntcol = new JCheckBox("Table order color");
	JCheckBox	homol = new JCheckBox("Show homologs");
	JButton		repaint = new JButton("Repaint");
	
	public void syntGrad( final GeneSetHead genesethead, final int w, final int h, Set<String> presel ) {
		GeneSet	geneset = genesethead.geneset;
		final JTable 				table = genesethead.getGeneTable();
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
		
		if( presel != null ) {
			int i = 0;
			for( String spec : species ) {
				if( presel.contains(spec) ) {
					int r = table2.convertRowIndexToView(i);
					table2.addRowSelectionInterval(r, r);
				}
				
				i++;
			}
		}
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent cmp = new JComponent() {};
		cmp.setLayout( flowlayout );
		
		cmp.add( scroll1 );
		cmp.add( scroll2 );
		
		JOptionPane.showMessageDialog(genesethead, cmp);
		
		int sr = table1.getSelectedRow();
		final String 		spec1 = sr != -1 ? species.get( table1.convertRowIndexToModel( sr )) : null; //(String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<Sequence>	contigs1 = spec1 != null ? geneset.speccontigMap.get( spec1 ) : null;
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			//String spec2 = (String)table2.getValueAt(r, 0);
			String spec2 = species.get( table2.convertRowIndexToModel(r) );
			spec2s.add( spec2 );
		}

		final BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		
		final Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
		
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
				drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
				c.repaint();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Sort contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( String spec : spec2s ) {
					if( !spec.equals(spec1) ) {
						List<Sequence> scontigs = geneset.speccontigMap.get( spec );
						Collections.sort( scontigs, new Comparator<Sequence>() {
							@Override
							public int compare(Sequence o1, Sequence o2) {
								List<Double> ratios = new ArrayList<Double>();
								if( o1.getAnnotations() != null ) {
									for( Annotation ann : o1.getAnnotations() ) {
										Tegeval tv = (Tegeval)ann;
										GeneGroup gg = tv.getGene().getGeneGroup();
										double val = tv.getGene() != null ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
										if( val != -1 ) ratios.add( val );
									}
								}
								Collections.sort( ratios );
								double r1 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;
								
								ratios = new ArrayList<Double>();
								if( o2.getAnnotations() != null ) {
									for( Annotation ann : o2.getAnnotations() ) {
										Tegeval tv = (Tegeval)ann;
										GeneGroup gg = tv.getGene().getGeneGroup();
										double val = tv.getGene() != null ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
										if( val != -1 ) ratios.add( val );
									}
								}
								Collections.sort( ratios );
								double r2 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;
								
								return Double.compare(r1, r2);
							}
						});
						for( Sequence c : scontigs ) {
							List<Double>	dvals = new ArrayList<Double>();
							Annotation tv = c.getFirst();
							while( tv != null ) {
								Annotation next = c.getNext( tv );
								if( next != null ) {
									Gene gene = null;
									if( tv instanceof Tegeval ) gene = ((Tegeval)tv).getGene();
									GeneGroup gg = gene != null ? gene.getGeneGroup() : null;
									double val1 = gene != null ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
									double val2 = (next instanceof Tegeval && ((Tegeval)next).getGene() != null) ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
									
									if( val1 != -1.0 && val2 != -1.0 ) {
										dvals.add( val2-val1 );
									}
								}
								tv = next;
							}
							if( dvals.size() > 0 ) {
								Collections.sort( dvals );
								double rev = dvals.get( dvals.size()/2 );
								if( rev < 0 ) {
									c.setReverse( !c.isReverse() );
								}
							}
						}
					}
				}
				drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ImageIO.write(bi, "png", new File("/Users/sigmar/synt.png"));
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
				Container comp = genesethead;
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
								Sequence c = contigs.get(i);
								if( loc + c.getAnnotationCount() > mloc ) {
									break;
								} else loc += c.getAnnotationCount();
							}
							Sequence c = contigs.get(i);
							
							if( mloc-loc < c.getAnnotationCount() ) {
								//loc += c.getAnnotationCount();
								//c = contigs.get( i%contigs.size() );
								Tegeval tv = c.annset.get(mloc-loc);
								Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
								
								if( ti != null && ti.best != null ) {
									Sequence ct1 = ti.best.getContshort();
									
									tv = c.annset.get(nloc-loc);
									ti = tv.getGene().getGeneGroup().getGenes(spec);
									Sequence ct2 = ti.best.getContshort();
									
									if( ct1 == ct2 ) ct1.setReverse( !ct1.isReverse() );
									else {
										List<Sequence> conts2 = geneset.speccontigMap.get(spec);
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
							List<Sequence> contigs = geneset.speccontigMap.get( spec );
							int total = 0;
							for( Sequence c : contigs ) {
								total += c.getAnnotationCount();
							}
							
							int loc1 = (int)(t1*total/(2*Math.PI));
							int loc2 = (int)(t2*total/(2*Math.PI));
							
							int minloc = Math.min( loc1, loc2 );
							int maxloc = Math.max( loc1, loc2 );
							
							int i = 0;
							int loc = 0;
							Sequence c = null;
							if( contigs != null ) {
								for( i = 0; i < contigs.size(); i++ ) {
									c = contigs.get(i);
									if( loc + c.getAnnotationCount() > minloc ) {
										break;
									} else loc += c.getAnnotationCount();
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
								genesethead.table.clearSelection();
								if( c == null ) {
									for( int k = minloc; k < maxloc; k++ ) {
										genesethead.table.addRowSelectionInterval(k, k);
									}
								} else for( int k = minloc; k < maxloc; k++ ) {
									if( k-loc >= c.getAnnotationCount() ) {
										loc += c.getAnnotationCount();
										i++;
										c = contigs.get( i%contigs.size() );
									}
									Tegeval tv = (Tegeval)(c.isReverse() ? c.getAnnotation( c.getAnnotations().size()-1-(k-loc) ) : c.getAnnotation(k-loc));
									if( e.isShiftDown() ) {
										Set<GeneGroup>	gset = new HashSet<GeneGroup>();
										gset.add( tv.getGene().getGeneGroup() );
										try {
											new Neighbour( gset ).neighbourMynd( genesethead, comp, geneset.genelist, geneset.contigmap );
										} catch (IOException e1) {
											e1.printStackTrace();
										}
										break;
									} else {
										int r;
										if( genesethead.table.getModel() == genesethead.groupModel ) {
											int u = geneset.allgenegroups.indexOf( tv.getGene().getGeneGroup() );
											r = genesethead.table.convertRowIndexToView(u);
											if( r >= 0 && r < genesethead.table.getRowCount() ) genesethead.table.addRowSelectionInterval( r, r );
										} else {
											Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
											int selr = -1;
											for( Tegeval te : ti.tset ) {
												int u = geneset.genelist.indexOf( te.getGene() );
												r = genesethead.table.convertRowIndexToView(u);
												if( selr == -1 ) selr = r;
												if( r >= 0 && r < genesethead.table.getRowCount() ) genesethead.table.addRowSelectionInterval( r, r );
											}
											Rectangle rect = genesethead.table.getCellRect(selr, 0, true);
											genesethead.table.scrollRectToVisible(rect);
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
		toolbar.add( selcheck );
		toolbar.add( vischeck );
		toolbar.add( syntcol );
		toolbar.add( homol );
		toolbar.add( repaint );
		
		repaint.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
				c.repaint();
			}
		});
		
		JFrame frame = new JFrame();
		frame.add( toolbar, BorderLayout.NORTH );
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void drawImage( GeneSetHead genesethead, Graphics2D g2, String spec1, List<Sequence> contigs1, List<String> spec2s, int w, int h ) {
		drawImage(genesethead, g2, spec1, contigs1, spec2s, w, h, 1.0);
	}
	
	public void doTv( GeneSetHead genesethead, Graphics2D g2, Annotation tv, int tvn, int total, int ptvn, int ptotal, String spec1, List<Sequence> contigs1, int w2, int h2, int rad, double radscale ) {
		GeneSet geneset = genesethead.geneset;
		Gene gene = tv.getGene();
		boolean phage = gene != null ? tv.isPhage() : false;
		boolean plasmid = tv.getContig().isPlasmid();
		
		GeneGroup gg = gene != null ? gene.getGeneGroup() : null;
		double r = (double)tvn/(double)total;
		double pr = (double)ptvn/(double)ptotal;
		double tr = (double)(tvn)/(double)(total+ptotal);
		double ptr = (double)(total+ptvn)/(double)(total+ptotal);
		
		//Gene sgene = gg.s
		
		boolean visible = false;
		if( spec1 != null ) {
			if( gg != null ) {
				Teginfo gene2s = gg.getGenes( spec1 );
				visible = true;
				
				boolean inplasmid = false;
				if( gene2s != null && gene2s.tset != null ) for( Tegeval tee : gene2s.tset ) {
					if( tee.getContshort().isPlasmid() ) {
						inplasmid = true;
						break;
					}
				}
				
				double ratio;
				if( inplasmid ) {
					ratio = GeneCompare.invertedGradientPlasmidRatio( spec1, contigs1, pr, gg );
				} else {
					ratio = GeneCompare.invertedGradientRatio( spec1, contigs1, r, gg, tv );
				}
				
				if( ratio >= 0.0 ) {
					Color color = inplasmid ? GeneCompare.gradientGrayscaleColor( ratio ) : GeneCompare.gradientColor( ratio );
					g2.setColor( color );
				} else {
					//System.err.println( "kukur " + ratio + " " + r );
					g2.setColor( Color.white );
				}
			} else {
				//System.err.println( "labbi ");
				g2.setColor( Color.white );
			}
		} else {
			Color color = Color.white;
			int count = 1;
			if( gg != null ) {
				Teginfo ti = gg.getGenes( gene.getSpecies() );
				count = ti.tset.size();
				
				int i = 0;
				if( genesethead.table.getModel() == genesethead.defaultModel ) {
					i = geneset.genelist.indexOf(gene);
				} else {
					i = geneset.allgenegroups.indexOf(gg);
				}
				
				if( i != -1 ) {
					int rv = genesethead.table.convertRowIndexToView(i);
					if( rv >= 0 && rv < genesethead.table.getRowCount() ) {
						visible = true; //geneset.table.isRowSelected( rv );
						
						if( syntcol.isSelected() ) {
							//double ratio = (double)gg.index/(double)geneset.allgenegroups.size(); 
							double ratio = (double)rv/(double)genesethead.table.getRowCount();
							color = GeneCompare.gradientColor(ratio);
						}
					}
				}
			}
			
			if( !syntcol.isSelected() ) {
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
			}
			g2.setColor( color );
		}
		
		if( visible ) {
			double rr = 2.0*Math.PI*( plasmid ? ptr : tr );
			
			g2.translate(w2, h2);
			g2.rotate( rr*radscale );
			g2.fillRect(rad, -1, 15, 3);
			//g2.drawLine(rad, 0, rad+15, 0);
			g2.rotate( -rr*radscale );
			g2.translate(-w2, -h2);
			
			if( selcheck.isSelected() ) {
				int i = 0;
				if( genesethead.table.getModel() == genesethead.defaultModel ) {
					i = geneset.genelist.indexOf(gene);
				} else {
					i = geneset.allgenegroups.indexOf(gg);
				}
				
				if( i != -1 ) {
					int rv = genesethead.table.convertRowIndexToView(i);
					if( rv >= 0 && rv < genesethead.table.getRowCount() ) {
						if( genesethead.table.isRowSelected( rv ) ) {
							g2.setColor( Color.black );
							g2.translate(w2, h2);
							g2.rotate( rr );
							g2.setColor( Color.magenta );
							g2.drawLine(rad+15, 0, rad+100, 0);
							g2.rotate( -rr );
							g2.translate(-w2, -h2);
						}
					}
				}
			}
			
			if( homol.isSelected() ) {
				if( gg != null ) {
					Teginfo ti = gg.getGenes(spec1);
					if( true ) {
					//if( tv.getContshort().isPlasmid() ) {
					//if( ti.tset.size() > 23 ) {
					//if( ti.tset.size() > 1 && ti.tset.size() < 4 ) {
						//System.err.println( "tiname " + rr + " " + ti.tset.size() + "  " + ti.best.name );
						
						g2.setColor( Color.black );
						for( Tegeval te : ti.tset ) {
							if( te != tv ) {
								g2.translate(w2, h2);
								
								tvn = 0;
								ptvn = 0;
								
								double rrr = 0.0; //= 2.0*Math.PI*( plasmid ? ptr : tr );
								if( te.getContshort().isPlasmid() ) {
									for( Sequence c : contigs1 ) {
										if( c.isPlasmid() ) {
											if( c.annset != null ) {
												int k = c.indexOf( te );
												if( k != -1 ) {
													ptvn += c.isReverse() ? c.getAnnotationCount() - k - 1 : k;
													break;
												}
											}
											ptvn += c.getAnnotationCount();
										}
									}
									//rrr = 2*Math.PI*(double)(tvn)/(double)(total+ptotal);
									rrr = 2.0*Math.PI*(double)(total+ptvn)/(double)(total+ptotal);
								} else {
									for( Sequence c : contigs1 ) {
										if( !c.isPlasmid() ) {
											if( c.annset != null ) {
												int k = c.indexOf( te );
												if( k != -1 ) {
													tvn += c.isReverse() ? c.getAnnotationCount() - k - 1 : k;
													break;
												}
											}
											tvn += c.getAnnotationCount();
										}
									}
									rrr = 2.0*Math.PI*(double)(tvn)/(double)(total+ptotal);
									//rrr = 2*Math.PI*(double)(total+ptvn)/(double)(total+ptotal);
								}
								
								//System.err.println( "rrr " + rrr );
								//rrr = Math.PI/2.0;
								double cs2 = Math.cos( rrr );
								double sn2 = Math.sin( rrr );
								double cs = Math.cos( rr );
								double sn = Math.sin( rr );
								int x1 = (int)(250*cs);
								int y1 = (int)(250*sn);
								int x2 = (int)(250*cs2);
								int y2 = (int)(250*sn2);
								g2.drawLine(x1, y1, x2, y2);
								
								g2.translate(-w2, -h2);
							}
						}
					}
				}
			}
		}
	}
	
	public void drawImage( GeneSetHead genesethead, Graphics2D g2, String spec1, List<Sequence> contigs1, List<String> spec2s, int w, int h, double radscale ) {
		GeneSet geneset = genesethead.geneset;
		
		int w2 = w/2;
		int h2 = h/2;
		
		int rad = 250;
		g2.setColor( Color.white );
		g2.fillRect( 0, 0, w, h );
		for( String spec : spec2s ) {
			List<Sequence> scontigs = geneset.speccontigMap.get( spec );
			
			int ptotal = 0;
			int total = 0;
			for( Sequence c : scontigs ) {
				if( c.isPlasmid() ) ptotal += c.getAnnotationCount();
				else total += c.getAnnotationCount();
			}
			
			int tvn = 0;
			int ptvn = 0;
			
			boolean succ = false;
			int rr = genesethead.table.getSelectedRow();
			if( rr != -1 ) {
				Tegeval te = null;
				int i = genesethead.table.convertRowIndexToModel(rr);
				if( genesethead.table.getModel() == genesethead.defaultModel ) {
					Gene gene = geneset.genelist.get(i);
					te = gene.tegeval;
				} else {
					GeneGroup gg = geneset.allgenegroups.get(i);
					Teginfo t = gg.getGenes(spec);
					if( t != null ) {
						te = t.best;
					}
				}
				
				if( te != null ) {
					succ = true;
					
					int ci = 0;
					int k = -1;
					//Tegeval te = t.best;
					/*if( te == null ) {
						for( Tegeval tv : t.tset ) {
							te = tv;
							if( te != null ) break;
						}
					}*/
					
					if( te != null ) {
						if( te.getContshort().isPlasmid() ) {
							for( Sequence c : scontigs ) {
								if( c.isPlasmid() ) {
									if( c.annset != null ) {
										k = c.indexOf( te );
										if( k != -1 ) {
											ptvn += c.isReverse() ? c.getAnnotationCount() - k - 1 : k;
											break;
										}
									}
									/*if( k != -1 ) {
										break;
									}*/
									ptvn += c.getAnnotationCount();
								}
								ci++;
							}
						} else {
							for( Sequence c : scontigs ) {
								if( !c.isPlasmid() ) {
									if( c.annset != null ) {
										k = c.indexOf( te );
										if( k != -1 ) {
											tvn += c.isReverse() ? c.getAnnotationCount() - k - 1 : k;
											break;
										}
									}
									/*if( k != -1 ) {
										break;
									}*/
									tvn += c.getAnnotationCount();
								}
								ci++;
							}
						}
						
						for( int cci = ci; cci <= ci+scontigs.size(); cci++ ) {
							int cii = cci%scontigs.size();
							Sequence c = scontigs.get(cii);
							if( cii == ci ) {
								Annotation tv = c.getAnnotation(k);
								if( cci == ci ) {
									while( tv != null ) {
										doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale );
										Annotation prev = tv;
										
										boolean plas = tv.getContig().isPlasmid();
										if( plas ) {
											ptvn = (ptvn+1)%ptotal;
										} else {
											tvn = (tvn+1)%total;
										}
										tv = c.getNext(tv);
										
										if( tv != null && tv.start == prev.start ) {
											System.err.println( tv.name + "   " + prev.name );
											System.err.println( tv == prev );
											
											break;
										}
									}
								} else {
									Annotation ftv = c.getFirst();
									while( ftv != tv ) {
										doTv( genesethead, g2, ftv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale );
										Annotation prev = ftv;
										
										boolean plas = tv.getContig().isPlasmid();
										if( plas ) {
											ptvn = (ptvn+1)%ptotal;
										} else {
											tvn = (tvn+1)%total;
										}
										ftv = c.getNext( ftv );
										
										if( ftv != null && ftv.start == prev.start ) {
											System.err.println( ftv.name + "   " + prev.name );
											System.err.println( ftv == prev );
											
											break;
										}
									}
								}
							} else {
								Annotation tv = c.getFirst();
								while( tv != null ) {
									doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale );
									
									Annotation prev = tv;
									
									boolean plas = tv.getContig().isPlasmid();
									if( plas ) {
										ptvn = (ptvn+1)%ptotal;
									} else {
										tvn = (tvn+1)%total;
									}
									tv = c.getNext( tv );
									
									if( tv != null && tv.start == prev.start ) {
										System.err.println( tv.name + "   " + prev.name );
										System.err.println( tv == prev );
										
										break;
									}
									
									/*if( tv == c.getFirst() ) {
										break;
									}*/
								}
							}
							
							if( contcheck.isSelected() ) {
								double r = 2.0*Math.PI*( (c.isPlasmid() ? (double)(total+ptvn) : (double)tvn)/(double)(ptotal+total) );
								g2.translate(w2, h2);
								g2.rotate( r );
								g2.setColor( Color.black );
								g2.drawLine(rad, 0, rad+100, 0);
								g2.rotate( -r );
								g2.translate(-w2, -h2);
							}
							
							/*Tegeval prev = tv;
							
							if( tv != null && tv.start == prev.start ) {
								System.err.println( tv.name + "   " + prev.name );
								System.err.println( tv == prev );
								
								break;
							}*/
						}
					}
				}
			}
			
			if( !succ ) {
				//System.err.println("packkki .................................................");
				for( Sequence c : scontigs ) {
					Annotation tv = c.getFirst();
					while( tv != null ) {
						doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale );
						
						Annotation prev = tv;
						
						boolean plas = tv.getContig().isPlasmid();
						if( plas ) {
							ptvn = (ptvn+1)%ptotal;
						} else {
							tvn = (tvn+1)%total;
						}
						
						tv = c.getNext( tv );
						
						if( tv != null && tv.start == prev.start ) {
							System.err.println( tv.name + "   " + prev.name );
							System.err.println( tv == prev );
							
							break;
						}
						
						/*if( tv == c.getFirst() ) {
							break;
						}*/
					}
					
					if( contcheck.isSelected() ) {
						double r = 2.0*Math.PI*( (c.isPlasmid() ? (double)(total+ptvn) : (double)tvn)/(double)(ptotal+total) );
						g2.translate(w2, h2);
						g2.rotate( r );
						g2.setColor( Color.black );
						g2.drawLine(rad, 0, rad+100, 0);
						g2.rotate( -r );
						g2.translate(-w2, -h2);
					}
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
	
	/*public static double invertedGradientRatio( String spec2, Collection<Sequence> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			total2 += ctg2.getAnnotationCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg != null ? gg.getGenes( spec2 ) : null;
		if( gene2s != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Sequence ctg2 : contigs2 ) {
				if( ctg2.getAnnotations() != null ) {
					int idx = ctg2.getAnnotations().indexOf( tv2 );
					if( idx == -1 ) {
						count2 += ctg2.getAnnotationCount();
					} else {
						count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx; 
						break;
					}
				}
			}
			double rat2 = (double)count2/(double)total2;
			 
			if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) ratio2 = rat2;
			//ratio2 = rat2;
			//break;
		}
		
		if( ratio2 == -1.0 ) {
			System.err.println( ratio2 + "  " + ratio );
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
		}*
		
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
	}*/
}
