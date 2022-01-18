package org.simmi.distann;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javafx.application.Platform;
import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Teginfo;
import org.simmi.javafasta.shared.Gene;
import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Tegeval;

public class SyntGrad {
	JCheckBox	contcheck = new JCheckBox("Show contig lines");
	JCheckBox	selcheck = new JCheckBox("Show lines for selection");
	JCheckBox	vischeck = new JCheckBox("Table visibility");
	JCheckBox	syntcol = new JCheckBox("Table order color");
	JCheckBox	homol = new JCheckBox("Show homologs");
	JButton		repaint = new JButton("Repaint");

	JRadioButton chromandplasm = new JRadioButton("Chrom&Plasm");
	JRadioButton onlychrom = new JRadioButton("Chrom");
	JRadioButton onlyplasm = new JRadioButton("Plasm");
	JRadioButton plasmcolor = new JRadioButton("PlasmColor");

	boolean isbeingdragged = false;

	public void changeOrder(List<Sequence> scontigs, List<Sequence> contigs1, String spec1) {
		for( Sequence c : scontigs ) {
			List<Double>	dvals = new ArrayList<>();
			Annotation tv = c.getFirst();
			while( tv instanceof Tegeval ) {
				Annotation next = c.getNext( tv );
				if(next instanceof Tegeval) {
					Gene gene = tv.getGene();
					Gene ngene = next.getGene();
					if(gene!=null && ngene!=null) {
						GeneGroup gg = gene.getGeneGroup();
						GeneGroup ngg = ngene.getGeneGroup();

						double val1 = GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv);
						double val2 = GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, ngg, next);

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

	public void syntGrad( final GeneSetHead genesethead, final int w, final int h, Set<String> presel ) {
		GeneSet									geneset = genesethead.geneset;
		//final Collection<String> 				specset = geneset.getSelspec(geneset, geneset.getSpecies(), (JCheckBox[])null); 
		final Collection<String>				specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
		final List<String>						species = new ArrayList<>( specset );
		
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

		table2.setDragEnabled( true );
		TransferHandler th = genesethead.dragRows( table2, species );
		scroll2.setTransferHandler( th );
		table2.setTransferHandler( th );
		
		JOptionPane.showMessageDialog(null, cmp);
		
		int sr = table1.getSelectedRow();
		final String 		spec1 = sr != -1 ? species.get( table1.convertRowIndexToModel( sr )) : null; //(String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<Sequence>	contigs1 = spec1 != null ? geneset.speccontigMap.get( spec1 ) : null;
		final List<String>	spec2s = new ArrayList<>();
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
				
				g.drawImage(bi,0,0,bi.getWidth()/2,bi.getHeight()/2,this);
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
						Collections.sort( scontigs, (o1, o2) -> {
                            List<Double> ratios = new ArrayList<>();
                            if( o1.getAnnotations() != null ) {
                                for( Annotation ann : o1.getAnnotations() ) {
                                	if(ann != null && ann instanceof Tegeval) {
										Tegeval tv = (Tegeval) ann;
										GeneGroup gg = tv.getGene().getGeneGroup();
										double val = tv.getGene() != null ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
										if (val != -1) ratios.add(val);
									}
                                }
                            }
                            Collections.sort( ratios );
                            double r1 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;

                            ratios = new ArrayList<>();
                            if( o2.getAnnotations() != null ) {
                                for( Annotation ann : o2.getAnnotations() ) {
                                	if(ann != null && ann instanceof Tegeval) {
										Tegeval tv = (Tegeval) ann;
										GeneGroup gg = tv.getGene().getGeneGroup();
										double val = tv.getGene() != null ? GeneCompare.invertedGradientRatio(spec1, contigs1, -1.0, gg, tv) : -1;
										if (val != -1) ratios.add(val);
									}
                                }
                            }
                            Collections.sort( ratios );
                            double r2 = ratios.size() > 0 ? ratios.get( ratios.size()/2 ) : 0;

                            return Double.compare(r1, r2);
                        });
						changeOrder(scontigs, contigs1, spec1);
					}
				}
				drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
				c.repaint();

				try {
					geneset.saveContigOrder();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					var userhome = System.getProperty("user.home");
					ImageIO.write(bi, "png", Paths.get(userhome).resolve("synt.png").toFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		c.setComponentPopupMenu( popup );

		c.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				isbeingdragged = true;
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});
		c.addMouseListener( new MouseListener() {
			Point p;
			boolean doubleclicked = false;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				//Container comp = genesethead;
				Point np = e.getPoint();
				if( p != null ) {
					doubleclicked = doubleclicked || e.getClickCount() == 2;
					
					double ndx = 2*np.x-bi.getWidth()/2;
					double ndy = 2*np.y-bi.getHeight()/2;
					
					double dx = 2*p.x-bi.getWidth()/2;
					double dy = 2*p.y-bi.getHeight()/2;

					double rad = Math.sqrt( dx*dx + dy*dy );

					int ind = (int)((rad-500.0)/30.0);
					String spec = ind >= 0 && ind < spec2s.size() ? spec2s.get( ind ) : null;
					if( spec != null ) {
						List<Sequence> scontigs = geneset.speccontigMap.get(spec);
						int size = 0;
						for (Sequence seq : scontigs) {
							size += seq.getAnnotationCount();
						}

						if (doubleclicked || isbeingdragged) {
							double t = Math.atan2(dy, dx);
							double nt = Math.atan2(ndy, ndx);
							double nrad = Math.sqrt(ndx * ndx + ndy * ndy);

							if (t < 0) t += Math.PI * 2.0;
							int mloc = (int) (t * size / (2 * Math.PI));

							if (nt < 0) nt += Math.PI * 2.0;
							int nloc = (int) (nt * size / (2 * Math.PI));

							int i;
							int loc = 0;
							for (i = 0; i < scontigs.size(); i++) {
								Sequence c = scontigs.get(i);
								if (loc + c.getAnnotationCount() > mloc) {
									break;
								} else loc += c.getAnnotationCount();
							}
							Sequence ct1 = scontigs.get(i);
							System.err.println("from " + i + " " + ct1.getName() + " " + mloc + "  " + size + " " + scontigs.size());
							int k = i;

							loc = 0;
							for (i = 0; i < scontigs.size(); i++) {
								Sequence c = scontigs.get(i);
								if (loc + c.getAnnotationCount() > nloc) {
									break;
								} else loc += c.getAnnotationCount();
							}
							Sequence ct2 = scontigs.get(i);
							System.err.println("dest " + i + " " + ct1.getName() + " " + nloc);

							if (ct1 == ct2) ct1.setReverse(!ct1.isReverse());
							else {
								//int k2 = scontigs.indexOf(ct2);
								scontigs.remove(k);
								scontigs.add(i, ct1);
							}
							drawImage(genesethead, g2, spec1, contigs1, spec2s, w, h);
							c.repaint();
						} else {
							double t1 = Math.atan2(dy, dx);
							double t2 = Math.atan2(ndy, ndx);

							if (t1 < 0) t1 += Math.PI * 2.0;
							if (t2 < 0) t2 += Math.PI * 2.0;

							if (spec != null) {
								List<Sequence> contigs = geneset.speccontigMap.get(spec);
								int total = 0;
								for (Sequence c : contigs) {
									total += c.getAnnotationCount();
								}

								int loc1 = (int) (t1 * total / (2 * Math.PI));
								int loc2 = (int) (t2 * total / (2 * Math.PI));

								int minloc = Math.min(loc1, loc2);
								int maxloc = Math.max(loc1, loc2);

								int i = 0;
								int loc = 0;
								Sequence c = null;
								if (contigs != null) {
									for (i = 0; i < contigs.size(); i++) {
										c = contigs.get(i);
										if (loc + c.getAnnotationCount() > minloc) {
											break;
										} else loc += c.getAnnotationCount();
									}
									//c = contigs.get(i);
								}

								if (e.isAltDown()) {
								/*Tegeval tv1 = c.annset.get(minloc-loc);
								Tegeval tv2 = c.annset.get(maxloc-loc);
								
								int from = Math.min( tv1.start, tv2.start );
								int to = Math.max( tv1.stop, tv2.stop );
								String seqstr = c.getSubstring( from, to, 1 );
							
								Sequence seq = new Sequence("phage_"+from+"_"+to, null);
								seq.append( seqstr );
								geneset.showSomeSequences( geneset, Arrays.asList( new Sequence[] {seq} ) );*/
								} else {
									if (c == null) {
										Platform.runLater(() -> {
											genesethead.getGeneGroupTable().getSelectionModel().clearSelection();
											for (int k = minloc; k < maxloc; k++) {
												genesethead.getGeneGroupTable().getSelectionModel().select(k);
											}
										});
									} else for (int k = minloc; k < maxloc; k++) {
										if (k - loc >= c.getAnnotationCount()) {
											loc += c.getAnnotationCount();
											i++;
											c = contigs.get(i % contigs.size());
										}
										Tegeval tv = (Tegeval) (c.isReverse() ? c.getAnnotation(c.getAnnotations().size() - 1 - (k - loc)) : c.getAnnotation(k - loc));
										if (e.isShiftDown()) {
											Set<GeneGroup> gset = new HashSet<>();
											gset.add(tv.getGene().getGeneGroup());
											try {
												new Neighbour(gset).neighbourMynd(genesethead, null, geneset.genelist, geneset.contigmap);
											} catch (IOException e1) {
												e1.printStackTrace();
											}
											break;
										} else {
											Platform.runLater(() -> {
												genesethead.getGeneGroupTable().getSelectionModel().clearSelection();
                                                if (!genesethead.isGeneview()) {
                                                    genesethead.getGeneGroupTable().getSelectionModel().select(tv.getGene().getGeneGroup());
                                                } else {
                                                    Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
                                                    for (Annotation te : ti.tset) {
                                                        genesethead.getGeneTable().getSelectionModel().select(te.getGene());
                                                    }
                                                    genesethead.getGeneTable().scrollTo(ti.best.getGene());
                                                }
                                            });
										}
									}
								}
							}
						}
					}
				}
				isbeingdragged = false;
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

		ButtonGroup bg = new ButtonGroup();
		bg.add(chromandplasm);
		bg.add(onlychrom);
		bg.add(onlyplasm);
		bg.add(plasmcolor);

		chromandplasm.setSelected(true);
		
		JToolBar toolbar = new JToolBar();
		toolbar.add( contcheck );
		toolbar.add( selcheck );
		toolbar.add( vischeck );
		toolbar.add( syntcol );
		toolbar.add( homol );
		toolbar.add( chromandplasm );
		toolbar.add( onlychrom );
		toolbar.add( onlyplasm );
		toolbar.add( plasmcolor );
		toolbar.add( repaint );
		
		repaint.addActionListener(e -> {
            drawImage( genesethead, g2, spec1, contigs1, spec2s, w, h );
            c.repaint();
        });
		
		JFrame frame = new JFrame();
		frame.add( toolbar, BorderLayout.NORTH );
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					geneset.saveContigOrder();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public int getInclusion() {
		if( chromandplasm.isSelected() ) return 3;
		else if( onlychrom.isSelected() ) return 1;
		else if( onlyplasm.isSelected() ) return 2;
		else if( plasmcolor.isSelected() ) return 4;
		return 0;
	}
	
	public void drawImage( GeneSetHead genesethead, Graphics2D g2, String spec1, List<Sequence> contigs1, List<String> spec2s, int w, int h ) {
		drawImage(genesethead, g2, spec1, contigs1, spec2s, w, h, 1.0, getInclusion());
	}
	
	public void doTv( GeneSetHead genesethead, Graphics2D g2, Annotation tv, int tvn, int total, int ptvn, int ptotal, String spec1, List<Sequence> contigs1, int w2, int h2, int rad, double radscale, int inclusion ) {
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
				if( gene2s != null && gene2s.tset != null ) for( Annotation tee : gene2s.tset ) {
					if( tee.getContshort() != null && tee.getContshort().isPlasmid() ) {
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
					Color color = inplasmid ^ inclusion == 4 ? GeneCompare.gradientGrayscaleColor( ratio ) : GeneCompare.gradientColor( ratio );
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
				if( genesethead.isGeneview() ) {
					int rv = genesethead.getGeneTable().getItems().indexOf(gene);
					if( rv >= 0 && rv < genesethead.getGeneTable().getItems().size() ) {
						visible = true; //geneset.table.isRowSelected( rv );
						
						if( syntcol.isSelected() ) {
							//double ratio = (double)gg.index/(double)geneset.allgenegroups.size(); 
							double ratio = (double)rv/(double)genesethead.getGeneTable().getItems().size();
							color = GeneCompare.gradientColor(ratio);
						}
					}
				} else {
					int rv = genesethead.getGeneGroupTable().getItems().indexOf(gg);
					if( rv >= 0 && rv < genesethead.getGeneGroupTable().getItems().size() ) {
						visible = true; //geneset.table.isRowSelected( rv );
						
						if( syntcol.isSelected() ) {
							//double ratio = (double)gg.index/(double)geneset.allgenegroups.size(); 
							double ratio = (double)rv/(double)genesethead.getGeneGroupTable().getItems().size();
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
			double rr = 2.0*Math.PI*(inclusion == 3 ? (plasmid ? ptr : tr) : (inclusion == 1 ? r : pr));

			g2.translate(w2, h2);
			g2.rotate( rr*radscale );
			if( (inclusion == 3 && total + ptotal > 200) || ((inclusion == 2 || inclusion == 4) && ptotal > 200) || (inclusion == 1 && total > 200) ) {
				g2.fillRect(rad, -1, 30, 3);
			} else {
				g2.fillRect(rad, -3, 30, 5);
			}

			//g2.drawLine(rad, 0, rad+15, 0);
			g2.rotate( -rr*radscale );
			g2.translate(-w2, -h2);
			
			if( selcheck.isSelected() ) {
				int i = 0;
				if( genesethead.isGeneview() ) {
					int rv = genesethead.getGeneTable().getItems().indexOf(gene);
					if( rv >= 0 && rv < genesethead.getGeneTable().getItems().size() ) {
						if( genesethead.getGeneTable().getSelectionModel().isSelected( rv ) ) {
							g2.setColor( Color.black );
							g2.translate(w2, h2);
							g2.rotate( rr );
							//g2.setColor( Color.magenta );
							//g2.drawLine(rad+30, 0, rad+100, 0);
							g2.fillRect(rad+30, -1, 100, 3);
							g2.rotate( -rr );
							g2.translate(-w2, -h2);
						}
					}
				} else {
					int rv = genesethead.getGeneGroupTable().getItems().indexOf(gg);
					if( rv >= 0 && rv < genesethead.getGeneGroupTable().getItems().size() ) {
						if( genesethead.getGeneGroupTable().getSelectionModel().isSelected( rv ) ) {
							g2.setColor( Color.black );
							g2.translate(w2, h2);
							g2.rotate( rr );
							//g2.setColor( Color.magenta );
							//g2.drawLine(rad+30, 0, rad+200, 0);
							g2.fillRect(rad+30, -1, 100, 3);
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
						for( Annotation te : ti.tset ) {
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
								int x1 = (int)(rad*cs);
								int y1 = (int)(rad*sn);
								int x2 = (int)(rad*cs2);
								int y2 = (int)(rad*sn2);
								g2.drawLine(x1, y1, x2, y2);
								
								g2.translate(-w2, -h2);
							}
						}
					}
				}
			}
		}
	}
	
	public void drawImage( GeneSetHead genesethead, Graphics2D g2, String spec1, List<Sequence> contigs1, List<String> spec2s, int w, int h, double radscale, int inclusion ) {
		GeneSet geneset = genesethead.geneset;
		
		int w2 = w/2;
		int h2 = h/2;

		int count = 0;
		int rad = 500;
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
			Annotation te = null;
			
			if( genesethead.isGeneview() ) {
				Gene gene = genesethead.getGeneTable().getSelectionModel().getSelectedItem();
				te = gene.getTegeval();
			} else {
				GeneGroup gg = genesethead.getGeneGroupTable().getSelectionModel().getSelectedItem();
				if( gg != null ) {
					Teginfo t = gg.getGenes(spec);
					if( t != null ) {
						te = t.best;
					}
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
				
				if( te != null && te.getContshort() != null ) {
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

					tvn = 0;
					ptvn = 0;
					for( int cci = ci; cci <= ci+scontigs.size(); cci++ ) {
						int cii = cci%scontigs.size();
						Sequence c = scontigs.get(cii);
						if( cii == ci ) {
							Annotation tv = c.getAnnotation(k);
							if( cci == ci ) {
								while( tv != null ) {
									boolean plas = tv.getContig().isPlasmid();
									if( inclusion == 3 || (plas && inclusion > 1) || (!plas && (inclusion&1) == 1) ) doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale, inclusion );
									Annotation prev = tv;
									if( plas ) {
										ptvn = (ptvn+1)%ptotal;
									} else {
										tvn = (tvn+1)%total;
									}
									tv = c.getNext(tv);
									
									if( tv != null && tv.start == prev.start ) {
										System.err.println( tv.getName() + "   " + prev.getName() );
										System.err.println( tv == prev );
										
										break;
									}
								}
							} else {
								Annotation ftv = c.getFirst();
								if( contcheck.isSelected() ) {
									if( count == 0 ) {
										double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
										g2.translate(w2, h2);
										g2.rotate(r);
										g2.setColor(Color.black);
										g2.drawLine(rad-20, 0, rad, 0);
										g2.rotate(-r);
										g2.translate(-w2, -h2);
									} else {
										double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
										g2.translate(w2, h2);
										g2.rotate(r);
										g2.setColor(Color.black);
										g2.drawLine(rad+30, 0, rad + 50, 0);
										g2.rotate(-r);
										g2.translate(-w2, -h2);
									}
								}

								while( ftv != tv ) {
									boolean plas = tv.getContig().isPlasmid();
									if( inclusion == 3 || (plas && inclusion > 1) || (!plas && (inclusion&1) == 1) )  doTv( genesethead, g2, ftv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale, inclusion );
									Annotation prev = ftv;
									if( plas ) {
										ptvn = (ptvn+1)%ptotal;
									} else {
										tvn = (tvn+1)%total;
									}
									ftv = c.getNext( ftv );
									
									if( ftv != null && ftv.start == prev.start ) {
										System.err.println( ftv.getName() + "   " + prev.getName() );
										System.err.println( ftv == prev );
										
										break;
									}
								}
							}
						} else {
							Annotation tv = c.getFirst();
							if( contcheck.isSelected() ) {
								if( count == 0 ) {
									double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
									g2.translate(w2, h2);
									g2.rotate(r);
									g2.setColor(Color.black);
									g2.drawLine(rad-20, 0, rad, 0);
									g2.rotate(-r);
									g2.translate(-w2, -h2);
								} else {
									double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
									g2.translate(w2, h2);
									g2.rotate(r);
									g2.setColor(Color.black);
									g2.drawLine(rad+30, 0, rad + 50, 0);
									g2.rotate(-r);
									g2.translate(-w2, -h2);
								}
							}

							while( tv != null ) {
								boolean plas = tv.getContig().isPlasmid();
								if( inclusion == 3 || (plas && inclusion > 1) || (!plas && (inclusion&1) == 1) ) doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale, inclusion );
								
								Annotation prev = tv;
								if( plas ) {
									ptvn = (ptvn+1)%ptotal;
								} else {
									tvn = (tvn+1)%total;
								}
								tv = c.getNext( tv );
								
								if( tv != null && tv.start == prev.start ) {
									System.err.println( tv.getName() + "   " + prev.getName() );
									System.err.println( tv == prev );
									
									break;
								}
								
								/*if( tv == c.getFirst() ) {
									break;
								}*/
							}
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
			
			if( !succ ) {
				//System.err.println("packkki .................................................");
				for( Sequence c : scontigs ) {
					Annotation tv = c.getFirst();
					while( tv != null ) {
						boolean plas = tv.getContig().isPlasmid();
						if( inclusion == 3 || (plas && inclusion > 1) || (!plas && (inclusion&1) == 1) ) doTv( genesethead, g2, tv, tvn, total, ptvn, ptotal, spec1, contigs1, w2, h2, rad, radscale, inclusion );
						
						Annotation prev = tv;
						if( plas ) {
							ptvn = (ptvn+1)%ptotal;
						} else {
							tvn = (tvn+1)%total;
						}
						
						tv = c.getNext( tv );
						
						if( tv != null && tv.start == prev.start ) {
							System.err.println( tv.getName() + "   " + prev.getName() );
							System.err.println( tv == prev );
							
							break;
						}
						
						/*if( tv == c.getFirst() ) {
							break;
						}*/
					}
					
					if( contcheck.isSelected() ) {
						if (count == 0) {
							double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
							g2.translate(w2, h2);
							g2.rotate(r);
							g2.setColor(Color.black);
							g2.drawLine(rad-20, 0, rad, 0);
							g2.rotate(-r);
							g2.translate(-w2, -h2);
						} else {
							double r = 2.0 * Math.PI * ((c.isPlasmid() ? (double) (total + ptvn) : (double) tvn) / (double) (ptotal + total));
							g2.translate(w2, h2);
							g2.rotate(r);
							g2.setColor(Color.black);
							g2.drawLine(rad+30, 0, rad + 50, 0);
							g2.rotate(-r);
							g2.translate(-w2, -h2);
						}
					}
				}
			}
				
			rad += 30;
			count++;
		}
		
		g2.setColor( Color.black );
		Font oldfont = g2.getFont().deriveFont( Font.ITALIC ).deriveFont( spec2s.size() > 10 ? 22.0f : 42.0f );
		int val = spec2s.size() > 10 ? 24 : 46;
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
