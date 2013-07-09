package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Neighbour {
	public static void recenter( JTable rowheader, JComponent c ) {
		selectedGenesGroups = new HashSet<GeneGroup>();
		selectedGenesGroups.add( currentTe.getGene().getGeneGroup() );
		//hteg = loadContigs( selectedGenes, null );
		hteg.clear();
		hteg = new ArrayList<Tegeval>();
		for( GeneGroup selectedGeneGroup : selectedGenesGroups ) {
			for( Gene selectedGene : selectedGeneGroup.genes ) {
				for( String species : selectedGene.species.keySet() ) {
					Teginfo ti = selectedGene.species.get( species );
					for( Tegeval te : ti.tset ) {
						hteg.add( te );
					}
				}
			}
		}
		/*speclist.clear();
		for( Gene selectedGene : selectedGenes ) {
			for( String species : selectedGene.species.keySet() ) {
				if( !speclist.contains( species ) ) speclist.add( species );
			}
		}*/
		rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
		int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
		if (rh == 0) {
			rh = rowheader.getRowCount() * rowheader.getRowHeight();
		}
		c.setPreferredSize( new Dimension(6000, rh) );
		c.setSize(6000, rh);
		
		int i = hteg.indexOf( currentTe );
		if( i != -1 ) {
			int r = rowheader.convertRowIndexToView( i );
			rowheader.setRowSelectionInterval(r, r);
		}
		
		c.repaint();
	}
	
	public static Tegeval getSelectedTe( Point p, JTable rowheader, JRadioButton sequenceView, List<Tegeval> lte, int rowheight ) {
		if( sequenceView.isSelected() ) {			
			for( int y = 0; y < rowheader.getRowCount(); y++ ) {
				int r = rowheader.convertRowIndexToModel( y );
				/*	String species = speclist.get( r );
				for( Gene selectedGene : selectedGenes ) {
				//for( String species : selectedGene.species.keySet() ) {
					if( selectedGene.species.containsKey(species) ) {
						Teginfo ti = selectedGene.species.get( species );
						for( Tegeval te : ti.tset ) {*/
				//for( Tegeval te : lte ) {
				int xoff = 3000;
				
				Tegeval te = lte.get(r);
				Tegeval next = te;
				//int k = 0;
				while( next != null && xoff < 5500 ) {					
					double len = next.getProteinLength()*neighbourscale;											
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					
					if( rect.contains( p ) ) return next;
					
					xoff += len+10;
					next = next.getNext();
				}
				
				xoff = 3000;
				Tegeval prev = te.getPrevious();
				//int k = 0;
				while( prev != null && xoff > 5 ) {					
					double len = prev.getProteinLength()*neighbourscale;
					xoff -= len+10;
					
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					if( rect.contains( p ) ) return prev;
					
					prev = prev.getPrevious();
				}
				//break;
			}
		} else {
			List<Tegeval>	hteglocal = new ArrayList<Tegeval>( lte );
			int xoff = 3000;
			//int k = 0;
			while( xoff < 5500 ) {
				int max = 0;
				for( Tegeval tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Tegeval te = hteglocal.get(r);
					Tegeval next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				xoff += max+10;
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Tegeval te = hteglocal.get(i);
					if( te != null ) hteglocal.set(i, te.getNext() );
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
			
			hteglocal.clear();
			for( Tegeval te : lte ) {
				hteglocal.add( te.getPrevious() );
			}
			xoff = 3000;
			//int k = 0;
			while( xoff > 500 ) {
				int max = 0;
				for( Tegeval tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				xoff -= max+10;
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Tegeval te = hteglocal.get(r);
					Tegeval next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Tegeval te = hteglocal.get(i);
					if( te != null ) hteglocal.set(i, te.getPrevious() );
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
					
			/*		xoff = 3000;
					Tegeval prev = te.getPrevious();
					//int k = 0;
					while( prev != null && xoff > 5 ) {					
						double len = prev.getProteinLength()*neighbourscale;
						xoff -= len+10;
						
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						if( rect.contains( p ) ) return prev;
						
						prev = prev.getPrevious();
					}
				}
			}*/
		}
		
		return null;
	}
	
	static double neighbourscale = 1.0;
	static Tegeval currentTe = null;
	static Set<GeneGroup> selectedGenesGroups;
	static List<Tegeval>	hteg;
	//static int colorscheme = 0;
	//static List<String>	speclist;
	public static void neighbourMynd( final Container comp, final List<Gene> genes, final JTable sorting, final Set<GeneGroup> selGenes ) throws IOException {
		selectedGenesGroups = selGenes;
		
		final JRadioButton	sequenceView = new JRadioButton("Sequence");
		final JRadioButton	blocksView = new JRadioButton("Blocks");
		final JRadioButton	realView = new JRadioButton("Real");
		
		final JButton	zoomIn = new JButton("+");
		final JButton	zoomOut = new JButton("-");
		final JButton	recenter = new JButton("Recenter");
		final JButton	addrelated = new JButton("Add related");
		final JButton	highrel = new JButton("Highlight related");
		final JButton	showseqs = new JButton("Show sequences");
		final JButton	showdnaseqs = new JButton("Show DNA sequences");
		final JMenuBar	mbr = new JMenuBar();
		final JMenu		mnu = new JMenu("Colors");
		final JMenu		mvmnu = new JMenu("Move");
		final JButton	turn = new JButton("Forward");
		
		final JButton	backTen = new JButton("<<");
		final JButton	back = new JButton("<");
		final JButton	forw = new JButton(">");
		final JButton	forwTen = new JButton(">>");
		
		mbr.add( mnu );
		mbr.add( mvmnu );
		final JRadioButtonMenuItem funcol = new JRadioButtonMenuItem("Functions");
		final JRadioButtonMenuItem gccol = new JRadioButtonMenuItem("GC%");
		final JRadioButtonMenuItem abucol = new JRadioButtonMenuItem("Abundance");
		final JRadioButtonMenuItem precol = new JRadioButtonMenuItem("Proximity preservation");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( funcol );
		bg.add( gccol );
		bg.add( abucol );
		bg.add( precol );
		mnu.add( funcol );
		mnu.add( gccol );
		mnu.add( abucol );
		mnu.add( precol );
		
		final JFrame frame = new JFrame();
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			//hteg = loadContigs( genes, null );
			//hteg.clear();
			hteg = new ArrayList<Tegeval>();
			for( GeneGroup selectedGeneGroup : selectedGenesGroups ) {
				for( Gene selectedGene : selectedGeneGroup.genes ) {
					for( String species : selectedGene.species.keySet() ) {
						Teginfo ti = selectedGene.species.get( species );
						for( Tegeval te : ti.tset ) {
							hteg.add( te );
						}
					}
				}
			}
			
			/*speclist = new ArrayList<String>();
			for( Gene selectedGene : selectedGenes ) {
				for( String species : selectedGene.species.keySet() ) {
					if( !speclist.contains( species ) ) speclist.add( species );
				}
			}*/
			
			//final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			final JTable rowheader = new JTable();
			
			final int		nPoints = 6;
			final int[]		xPoints = new int[ nPoints ];
			final int[]		yPoints = new int[ nPoints ];
			
			final Map<Set<Function>,Color>	funcMap = new HashMap<Set<Function>,Color>();
			final Random rand = new Random();
			
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();
				Color altcol = Color.black;
				// Color dg = Color.green.darker();

				public String getToolTipText( MouseEvent me ) {
					Point p = me.getPoint();
					Tegeval te = getSelectedTe(p, rowheader, sequenceView, hteg, rowheader.getRowHeight());
					if( te != null ) return "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+ "<br>" + te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>";
					return null;
				}
				
				public void paintComponent( Graphics g ) {
					super.paintComponent(g);
					
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
					g.setFont( g.getFont().deriveFont( 8.0f ) );
					
					Rectangle clip = this.getVisibleRect(); //g.getClipBounds();
					if( sequenceView.isSelected() || realView.isSelected() ) {
						//int y = 0;
						for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
							int r = rowheader.convertRowIndexToModel( i );
						//	String species = speclist.get( r );
							//for( String species : selectedGene.species.keySet() ) {
							
							/*if( speclist == null ) {
								speclist = new ArrayList<String>();
								for( Gene selGene : selectedGenes ) {
									for( String species : selGene.species.keySet() ) {
										if( !speclist.contains( species ) ) speclist.add( species );
									}
								}
							}*
							
							for( Gene selectedGene : selectedGenes ) {
								if( selectedGene.species.containsKey(species) ) {
									g.setColor( Color.black );
									g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
									
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {*/
						//for( Tegeval te : hteg ) {
							Tegeval te = hteg.get(r);
							int xoff = 3000;
							
							if( clip.x+clip.width > xoff ) {
								Tegeval next = te;
								//int k = 0;
								while( next != null && xoff <= 5500 && clip.x+clip.width > xoff ) {
									double len = next.getProteinLength()*neighbourscale;
									if( next.getGene() != null ) {
										String genename = next.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										if( xoff+len > clip.x ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												
												Gene gene = next.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = next.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = next.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( next.getNext() != null ) {
													tegevals = next.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( next.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (next.ori == -1) ^ next.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( next.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									}
									
									Tegeval thenext = next.getNext();
									int bil = 10;
									if( thenext != null && realView.isSelected() ) {
										bil = next.getContshort().isReverse() ? Math.abs( thenext.stop-next.start ) : Math.abs( thenext.start-next.stop );
										bil = (int)(neighbourscale*bil/3);
									}
									xoff += len + bil;
									next = thenext;
									/*if( tev == null ) {
										Contig nextcontig = next.getContshort().next;
										nextcontig.
									} else next = tev;*/
								}
							}
							
							xoff = 3000;
							if( clip.x < xoff ) {
								Tegeval prev = te != null ? te.getPrevious() : null;
								
								//int k = 0;
								while( prev != null && xoff >= 500 && clip.x < xoff ) {
									double len = prev.getProteinLength()*neighbourscale;
									
									Tegeval theprev = prev.getPrevious();
									int bil = 10;
									if( theprev != null && realView.isSelected() ) {
										bil = prev.getContshort().isReverse() ? Math.abs( theprev.start-prev.stop ) : Math.abs( theprev.stop-prev.start );
										bil = (int)(neighbourscale*bil/3);
									}
									xoff -= len + bil;
									
									if( prev.getGene() != null ) {
										String genename = prev.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										if( clip.x+clip.width > xoff ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												Gene gene = prev.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = prev.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = prev.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( prev.getNext() != null ) {
													tegevals = prev.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( prev.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)prev.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (prev.ori == -1) ^ prev.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( prev.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											g.setColor( Color.black );
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									}
									prev = theprev;
									/*if( prev != null ) {
										len = prev.getProteinLength()*neighbourscale;
										xoff -= len+10;
									}*/
								}
							}
							//y++;
						}
					} else if( realView.isSelected() ) {
						
					} else {					
						/*for( Gene selectedGene : selectedGenes ) {
							for( String species : speclist ) {
								if( selectedGene.species.containsKey(species) ) {
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {
										hteg.add( te );
									}
								}
							}
						}*/
						
						List<Tegeval>	hteglocal = new ArrayList<Tegeval>( hteg );
						int xoff =  3000;
						while( xoff < 5500 ) {
							int max = 0;
							for( Tegeval tes : hteglocal ) {
								//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
								int val = 0;
								if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
								if( val > max ) max = val;
							}
							
							for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
								int r = rowheader.convertRowIndexToModel( i );
								Tegeval te = hteglocal.get(r);
								//int y = 0;
								//for( Tegeval te : hteglocal ) {
								//g.setColor( Color.black );
								//g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
								
								if( te != null ) {
									Tegeval next = te;
									if( te.getGene() != null ) {
										String genename = te.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										double len = te.getProteinLength()*neighbourscale;
										
										if( clip.x+clip.width > xoff ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
												Gene gene = next.getGene();
												GeneGroup gg = gene.getGeneGroup();
												Set<Function> funcset = gg != null ? gg.getFunctions() : null;
												if( funcset != null && funcset.size() > 0 ) {
													if( funcMap.containsKey( funcset ) ) {
														g.setColor( funcMap.get( funcset ) );
													} else {
														Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
														g.setColor( rc );
														funcMap.put( funcset, rc );
													}
												}
											} else if( abucol.isSelected() ) {
												GeneGroup gg = next.getGene().getGeneGroup();
												int numspec = gg.species.size();
												float abu = numspec/28.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = next.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
												
												if( next.getNext() != null ) {
													tegevals = next.getNext().getGene().getGeneGroup().getTegevals();
													total = tegevals.size();
													for( Tegeval tev : tegevals ) {
														Tegeval thenext = tev.getPrevious();
														GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
														int val = 0;
														if( shanmap.containsKey(c) ) val = shanmap.get(c);
														shanmap.put( c, val+1 );
													}
													for( GeneGroup c : shanmap.keySet() ) {
														int val = shanmap.get(c);
														double p = (double)val/(double)total;
														res -= p*Math.log(p)/Math.log(2.0);
													}
												}
												
												float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
												Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
												g.setColor( rc );
											} else {
												if( next.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}
											}
											
											boolean revis = (next.ori == -1) ^ next.getContshort().isReverse();
											int addon = revis ? -5 : 5;
											int offset = revis ? 5 : 0;
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											int y = i;
											xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
											xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
											xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
											xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
											xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
											g.fillPolygon(xPoints, yPoints, nPoints);
											g.setColor( next.isSelected() ? Color.black : Color.gray );
											g.drawPolygon(xPoints, yPoints, nPoints);
											g.setColor( Color.black );
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											g.setColor( Color.black );
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									/*g.setColor( Color.green );
									Set<Function> funcset = te.getGene().getGeneGroup().getFunctions();
									if( funcset != null && funcset.size() > 0 ) {
										if( funcMap.containsKey( funcset ) ) {
											g.setColor( funcMap.get( funcset ) );
										} else {
											Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
											g.setColor( rc );
											funcMap.put( funcset, rc );
										}
									}*/
									//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
									/*xPoints[0] = xoff; yPoints[0] = y * rowheader.getRowHeight()+2;
									xPoints[1] = xoff+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
									xPoints[2] = xoff+(int)len+5; yPoints[2] = y * rowheader.getRowHeight()+2+6;
									xPoints[3] = xoff+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
									xPoints[4] = xoff; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
									xPoints[5] = xoff+5; yPoints[5] = y * rowheader.getRowHeight()+2+6;
									g.fillPolygon(xPoints, yPoints, nPoints);
									g.setColor( te.isSelected() ? Color.black : Color.gray );
									g.drawPolygon(xPoints, yPoints, nPoints);
									g.setColor( Color.black );
									
									int strlen = g.getFontMetrics().stringWidth( genename );
									while( strlen > len ) {
										genename = genename.substring(0, genename.length()-1);
										strlen = g.getFontMetrics().stringWidth( genename );
									}
									g.setColor( Color.black );
									g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );*/
									}
								}
								//y++;
							}
							xoff += max+10;
							
							for( int i = 0; i < hteglocal.size(); i++ ) {
								Tegeval te = hteglocal.get(i);
								if( te != null ) hteglocal.set(i, te.getNext() );
								//if( te.getLength() > max ) max = te.getLength();
							}
						}
						
						hteglocal.clear();
						hteglocal.addAll( hteg );
						for( int i = 0; i < hteglocal.size(); i++ ) {
							Tegeval te = hteglocal.get(i);
							if( te != null ) hteglocal.set(i, te.getPrevious() );
							//if( te.getLength() > max ) max = te.getLength();
						}
						/************* 
						hteg.clear();
						hteg = new ArrayList<Tegeval>();
						for( Gene selectedGene : selectedGenes ) {
							for( String species : speclist ) {
								if( selectedGene.species.containsKey(species) ) {
									Teginfo ti = selectedGene.species.get( species );
									for( Tegeval te : ti.tset ) {
										hteg.add( te.getPrevious() );
									}
								}
							}
						}*****************************/
						
						xoff =  3000;
						while( xoff > 500 ) {
							int max = 0;
							for( Tegeval te : hteglocal ) {
								int val = 0;
								if( te != null ) val = (int)(te.getProteinLength()*neighbourscale);
								if( val > max ) max = val;
							}
							
							xoff -= max+10;
							for( int i = Math.max(0, clip.y/rowheader.getRowHeight()); i < Math.min( (clip.y+clip.height)/rowheader.getRowHeight()+1, rowheader.getRowCount() ); i++ ) {
								int r = rowheader.convertRowIndexToModel( i );
								Tegeval te = hteglocal.get(r);
								//g.setColor( Color.black );
								//g.drawLine( 0, y*rowheader.getRowHeight()+8, this.getWidth(), y*rowheader.getRowHeight()+8 );
								
								if( te != null ) {
									Tegeval prev = te;
									if( te.getGene() != null ) {
										String genename = te.getGene().getName();
										genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										double len = te.getProteinLength()*neighbourscale;
										/*g.setColor( Color.green );
										Set<Function> funcset = te.getGene().getGeneGroup().getFunctions();
										if( funcset != null && funcset.size() > 0 ) {
											if( funcMap.containsKey( funcset ) ) {
												g.setColor( funcMap.get( funcset ) );
											} else {
												Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
												g.setColor( rc );
												funcMap.put( funcset, rc );
											}
										}*/
										
										int y = i;
										//g.fillRect(xoff, y * rowheader.getRowHeight()+2, len, rowheader.getRowHeight() - 4);
										if( funcol.isSelected() ) {
											g.setColor( Color.green );
											Gene gene = prev.getGene();
											GeneGroup gg = gene.getGeneGroup();
											Set<Function> funcset = gg != null ? gg.getFunctions() : null;
											if( funcset != null && funcset.size() > 0 ) {
												if( funcMap.containsKey( funcset ) ) {
													g.setColor( funcMap.get( funcset ) );
												} else {
													Color rc = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
													g.setColor( rc );
													funcMap.put( funcset, rc );
												}
											}
										} else if( abucol.isSelected() ) {
											GeneGroup gg = prev.getGene().getGeneGroup();
											int numspec = gg.species.size();
											float abu = numspec/28.0f;
											Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
											g.setColor( rc );
										} else if( precol.isSelected() ) {
											Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
											shanmap.clear();
											double res = 0.0;
											
											List<Tegeval> tegevals = prev.getGene().getGeneGroup().getTegevals();
											int total = tegevals.size();
											for( Tegeval tev : tegevals ) {
												Tegeval thenext = tev.getNext();
												GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
												int val = 0;
												if( shanmap.containsKey(c) ) val = shanmap.get(c);
												shanmap.put( c, val+1 );
											}
											for( GeneGroup c : shanmap.keySet() ) {
												int val = shanmap.get(c);
												double p = (double)val/(double)total;
												res -= p*Math.log(p)/Math.log(2.0);
											}
											
											if( prev.getNext() != null ) {
												tegevals = prev.getNext().getGene().getGeneGroup().getTegevals();
												total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getPrevious();
													GeneGroup c = thenext == null ? null : thenext.getGene().getGeneGroup();
													int val = 0;
													if( shanmap.containsKey(c) ) val = shanmap.get(c);
													shanmap.put( c, val+1 );
												}
												for( GeneGroup c : shanmap.keySet() ) {
													int val = shanmap.get(c);
													double p = (double)val/(double)total;
													res -= p*Math.log(p)/Math.log(2.0);
												}
											}
											
											float gc = Math.min( 1.0f, Math.max( 0.0f, (float)res/20.0f ) );
											Color rc = new Color( 1.0f, 1.0f-gc, 1.0f-gc );
											g.setColor( rc );
										} else {
											if( prev.getGCPerc() <= 0 ) {
												Color rc = new Color( 1.0f, 1.0f, 1.0f );
												g.setColor( rc );
											} else {
												float gc = Math.max( 0.0f, Math.min(((float)prev.getGCPerc()-0.5f)*4.0f, 1.0f) );
												Color rc = new Color( 1.0f-gc, gc, 1.0f );
												g.setColor( rc );
											}
										}
										
										boolean revis = (prev.ori == -1) ^ prev.getContshort().isReverse();
										int addon = revis ? -5 : 5;
										int offset = revis ? 5 : 0;
										//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
										xPoints[0] = xoff+offset; yPoints[0] = y * rowheader.getRowHeight()+2;
										xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheader.getRowHeight()+2;
										xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheader.getRowHeight()+2+6;
										xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
										xPoints[4] = xoff+offset; yPoints[4] = y * rowheader.getRowHeight()+2+rowheader.getRowHeight()-4;
										xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheader.getRowHeight()+2+6;
										g.fillPolygon(xPoints, yPoints, nPoints);
										g.setColor( prev.isSelected() ? Color.black : Color.gray );
										g.drawPolygon(xPoints, yPoints, nPoints);
										g.setColor( Color.black );
										
										int strlen = g.getFontMetrics().stringWidth( genename );
										while( strlen > len ) {
											genename = genename.substring(0, genename.length()-1);
											strlen = g.getFontMetrics().stringWidth( genename );
										}
										g.setColor( Color.black );
										g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
									}
								}
							}
							
							for( int i = 0; i < hteglocal.size(); i++ ) {
								Tegeval te = hteglocal.get(i);
								if( te != null ) hteglocal.set( i, te.getPrevious() );
								//if( te.getLength() > max ) max = te.getLength();
							}
						}
					}
				
					/*Rectangle rc = g.getClipBounds();
					for (int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++) {
						int r = sorting.convertRowIndexToModel(i);
						Gene gene = genes.get(r);
						
						if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								String contig = (String) rowheader.getValueAt(y, 0);

								int und = contig.indexOf("_");
								String spec = contig.substring(0, und);
								if (gene.species.containsKey(spec)) {
									Teginfo stv = gene.species.get(spec);
									for (Tegeval tv : stv.tset) {
										if (tv.cont.startsWith(contig)) {
											g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
										}
									}
								}
							}
						}
					}*/

					/*
					 * Color color; int i = 0; for( Tegeval tv : ltv ) { if(
					 * tv.ori < 0 ) color = Color.red; else color = Color.green;
					 * 
					 * if( (++i)%2 == 0 ) { color = color.darker(); }
					 * g.setColor( color );
					 * 
					 * if( (tv.stop-tv.start)/1000 > 100 ) {
					 * System.out.println("hund"); } g.fillRect(tv.start/1000,
					 * 0, (tv.stop-tv.start)/10, 20); } System.out.println( i );
					 */
				}

				/*
				 * public Rectangle getBounds() { Rectangle r =
				 * super.getBounds(); r.width = hey; r.height =
				 * rowheader.getHeight(); return r; }
				 * 
				 * public void setBounds( int x, int y, int w, int h ) {
				 * super.setBounds(x, y, hey, rowheader.getHeight()); }
				 */
			};
			c.setToolTipText("bleh");
			
			final AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			funcol.setAction( a );
			gccol.setAction( a );
			abucol.setAction( a );
			precol.setAction( a );
			
			funcol.setText("Functions");
			gccol.setText("GC%");
			abucol.setText("Abundance");
			precol.setText("Proximity preservation");
			
			turn.setAction( new AbstractAction("Forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.err.println("hteg " + hteg.size());
					for( Tegeval te : hteg ) {
						boolean rev = te.ori == -1 ^ te.getContshort().isReverse();
						if( rev ) te.getContshort().setReverse( !te.getContshort().isReverse() );
					}
					c.repaint();
				}
			});
			mvmnu.add( new AbstractAction("Inject forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval previous = currentTe.prev;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							currentTe.setPrevious( te );
							te.setPrevious( previous );
						} else {
							Tegeval next = currentTe.next;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							te.setPrevious( currentTe );
							next.setPrevious( te );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Inject back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval next = currentTe.next;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							te.setPrevious( currentTe );
							next.setPrevious( te );
						} else {
							Tegeval previous = currentTe.prev;
							Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, null, null, currentTe.getContshort(), null, 0, 0, 1 );
							currentTe.setPrevious( te );
							te.setPrevious( previous );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Delete forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval prevprev = currentTe.prev.prev;
							currentTe.setPrevious( prevprev );
						} else {
							Tegeval nextnext = currentTe.next.next;
							nextnext.setPrevious( currentTe );
						}
						c.repaint();
					}
				}
			});
			mvmnu.add( new AbstractAction("Delete back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						if( currentTe.getContshort().isReverse() ) {
							Tegeval nextnext = currentTe.next.next;
							nextnext.setPrevious( currentTe );
						} else {
							Tegeval prevprev = currentTe.prev.prev;
							currentTe.setPrevious( prevprev );
						}
						c.repaint();
					}
				}
			});
			
			zoomIn.setAction( new AbstractAction("+") {
				@Override
				public void actionPerformed(ActionEvent e) {
					neighbourscale *= 1.25;
					c.repaint();
				}
			});
			zoomOut.setAction( new AbstractAction("-") {
				@Override
				public void actionPerformed(ActionEvent e) {
					neighbourscale *= 0.8;
					c.repaint();
				}
			});
			
			backTen.setAction( new AbstractAction("<<") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te;
					i = 0;
					while( currentTe.getPrevious() != null && i < 10 ) {
						currentTe = currentTe.getPrevious();
						i++;
					}
					recenter( rowheader, c );
				}
			});
			back.setAction( new AbstractAction("<") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te.getPrevious() == null ? te : te.getPrevious();
					recenter( rowheader, c );
				}
			});
			forw.setAction( new AbstractAction(">") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te.getNext() == null ? te : te.getNext();
					recenter( rowheader, c );
					
				}
			});
			forwTen.setAction( new AbstractAction(">>") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					currentTe = te;
					i = 0;
					while( currentTe.getNext() != null && i < 10 ) {
						currentTe = currentTe.getNext();
						i++;
					}
					recenter( rowheader, c );
				}
			});
			
			recenter.setAction( new AbstractAction("Recenter") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						recenter( rowheader, c );
					}
				}
			});
			addrelated.setAction( new AbstractAction("Add related") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						GeneGroup gg = currentTe.getGene().getGeneGroup();
						List<Tegeval> lte = gg.getTegevals();
						List<Tegeval> include = new ArrayList<Tegeval>();
						for( Tegeval te : lte ) {
							Contig ct = te.getContshort();
							for( Tegeval ste : hteg ) {
								if( ste.getContshort() == ct ) {
									ct = null;
									break;
								}
							}
							if( ct != null ) include.add( te );
						}
						hteg.addAll( include );
						
						rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
						int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
						if (rh == 0) {
							rh = rowheader.getRowCount() * rowheader.getRowHeight();
						}
						c.setPreferredSize( new Dimension(6000, rh) );
						c.setSize(6000, rh);
						c.repaint();
					}
				}
			});
			highrel.setAction( new AbstractAction("Highlight related") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( currentTe != null ) {
						List<Tegeval> lte = currentTe.getGene().getGeneGroup().getTegevals();
						for( Tegeval te : lte ) {
							te.setSelected( true );
						}
						c.repaint();
					}
				}
			});
			showseqs.setAction( new AbstractAction("Show sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					GeneSet.showSequences( comp, selectedGenesGroups );
				}
			});
			
			sequenceView.setAction( a );
			blocksView.setAction( a );
			realView.setAction( a );
			sequenceView.setText("Sequence");
			blocksView.setText("Blocks");
			realView.setText("Real");
			
			sequenceView.setSelected( true );
			
			bg = new ButtonGroup();
			bg.add( sequenceView );
			bg.add( blocksView );
			bg.add( realView );
			
			c.addMouseListener(new MouseAdapter() {
				Point p;

				public void mousePressed(MouseEvent me) {
					p = me.getPoint();
					
					Tegeval te = getSelectedTe( p, rowheader, sequenceView, hteg, rowheader.getRowHeight() );
					//System.err.println();
					if( te != null ) {
						if( me.getClickCount() == 2 ) {
							c.setToolTipText( "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+"<br>"+ te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>" );
							//c.sett
						} else {
							te.setSelected( !te.isSelected() );
							int i;
							if( sorting.getModel() == GeneSet.groupModel ) {
								i = GeneSet.allgenegroups.indexOf( te.getGene().getGeneGroup() );
							} else {
								i = genes.indexOf( te.getGene() );
							}
							int r = sorting.convertRowIndexToView(i);
							if( te.isSelected() ) {
								currentTe = te;
								if( r >= 0 && r < sorting.getRowCount() ) {
									sorting.addRowSelectionInterval(r, r);
									sorting.scrollRectToVisible( sorting.getCellRect(r, 0, false) );
								}
							} else {
								if( r >= 0 && r < sorting.getRowCount() ) {
									sorting.removeRowSelectionInterval(r, r);
									sorting.scrollRectToVisible( sorting.getCellRect(r, 0, false) );
								}
							}
							c.repaint();
						}
					}
				}

				public void mouseReleased(MouseEvent me) {
					Point np = me.getPoint();

					if (np.x > p.x) {
						Rectangle rect = sorting.getCellRect(p.x, 0, false);
						rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						sorting.scrollRectToVisible(rect);
						//sorting.setRowSelectionInterval(p.x, np.x);
					}
				}
			});
			
			JPopupMenu	popup = new JPopupMenu();
			popup.add( new AbstractAction("Reverse") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						Contig contig = te.getContshort();
						te.getContshort().setReverse( !contig.isReverse() );
						
						Contig nextc = contig.next;
						while( nextc != null ) {
							nextc.setReverse( !nextc.isReverse() );
							nextc = nextc.next;
						}
						
						Contig prevc = contig.prev;
						while( prevc != null ) {
							prevc.setReverse( !prevc.isReverse() );
							prevc = prevc.prev;
						}
						
						/*for( Gene selectedGene : selectedGenes ) {
							String spec = (String)rowheader.getValueAt(r, 0);
							if( selectedGene.species.containsKey( spec ) ) {
								Teginfo ti = selectedGene.species.get( spec );
								for( Tegeval te : ti.tset ) {
									te.getContshort().setReverse( !te.getContshort().isReverse() );
									break;
								}
							}
						}*/
					}
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Connect contig") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Tegeval te = hteg.get( i );
					Contig 	cont = te.getContshort();
					String	spec = cont.getSpec();
					
					final List<Contig>	specont = new ArrayList<Contig>();
					for( String name : GeneSet.contigmap.keySet() ) {
						Contig c = GeneSet.contigmap.get( name );
						if( c != cont && spec.equals( c.getSpec() ) ) specont.add( c );
					}
					
					JTable	table = new JTable();
					table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
					table.setAutoCreateRowSorter( true );
					TableModel model = new TableModel() {
						@Override
						public int getRowCount() {
							return specont.size();
						}

						@Override
						public int getColumnCount() {
							return 1;
						}

						@Override
						public String getColumnName(int columnIndex) {
							return "Contig";
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
							return specont.get(rowIndex).name;
						}

						@Override
						public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
							
						}

						@Override
						public void addTableModelListener(TableModelListener l) {}

						@Override
						public void removeTableModelListener( TableModelListener l ) {}
					};
					table.setModel( model );
					JScrollPane	scroll = new JScrollPane( table );
					JCheckBox	reverse = new JCheckBox( "reverse" );
					JCheckBox	forward = new JCheckBox( "forward" );
					Object[] message = { scroll, reverse, forward };
					JOptionPane.showMessageDialog( frame, message, "Select contig", JOptionPane.PLAIN_MESSAGE );
					
					r = table.getSelectedRow();
					i = -1;
					if( r != -1 ) i = table.convertRowIndexToModel( r );
					if( i != -1 ) {
						if( forward.isSelected() ) {
							Tegeval con = cont.end.next;
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.start.prev : cont.end.next;
								
								if( con != null && con.getContshort().equals( cont ) ) {
									break;
								}
							}
						} else {
							Tegeval con = cont.start.prev;
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.start.prev : cont.end.next;
								
								if( con != null && con.getContshort().equals( cont ) ) {
									break;
								}
							}
						}
						
						cont.setConnection( specont.get(i), reverse.isSelected(), forward.isSelected() );
					}
					
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Delete") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Tegeval>	ste = new HashSet<Tegeval>();
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						ste.add( hteg.get(i) );
					}
					hteg.removeAll( ste );
					
					rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
					int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
					if (rh == 0) {
						rh = rowheader.getRowCount() * rowheader.getRowHeight();
					}
					c.setPreferredSize( new Dimension(6000, rh) );
					c.setSize(6000, rh);
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Move left") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						hteg.set( i, te.next == null ? te : te.next );
					}
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Move right") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						Tegeval te = hteg.get( i );
						hteg.set( i, te.prev == null ? te : te.prev );
					}
					c.repaint();
				}
			});
			rowheader.setComponentPopupMenu( popup );

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.addKeyListener( new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
						Set<Tegeval>	ste = new HashSet<Tegeval>();
						int[] rr = rowheader.getSelectedRows();
						for( int r : rr ) {
							int i = rowheader.convertRowIndexToModel( r );
							ste.add( hteg.get(i) );
						}
						hteg.removeAll( ste );
						
						rowheader.tableChanged( new TableModelEvent( rowheader.getModel() ) );
						int rh = rowheader.getRowCount() * rowheader.getRowHeight();//rowheader.getHeight();
						if (rh == 0) {
							rh = rowheader.getRowCount() * rowheader.getRowHeight();
						}
						c.setPreferredSize( new Dimension(6000, rh) );
						c.setSize(6000, rh);
						c.repaint();
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return hteg.size();
				}

				@Override
				public int getColumnCount() {
					return 4;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if( columnIndex == 1 ) return "Contig";
					else if( columnIndex == 2 ) return "Length";
					else if( columnIndex == 3 ) return "Orientation";
					return "Species";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if( columnIndex == 2 ) return Integer.class;
					else if( columnIndex == 3 ) return Boolean.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					//String species = speclist.get( rowIndex );
					Tegeval te = hteg.get(rowIndex);
					if( columnIndex == 0 ) return te.getSpecies();
					else if( columnIndex == 1 ) return te.getContshort().getName();
					else if( columnIndex == 2 ) return te.getLength();
					else if( columnIndex == 3 ) return te.getContshort().isReverse();
					/*for( Gene selectedGene : selectedGenes ) {
						if( selectedGene.species.containsKey( species ) ) {
							Teginfo ti = selectedGene.species.get( species );
							for( Tegeval te : ti.tset ) {
								return te.getContshort().getName();
							}
						}
					}*/
					return null;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

				@Override
				public void addTableModelListener(TableModelListener l) {}

				@Override
				public void removeTableModelListener(TableModelListener l) {}
			});
			scrollpane.setRowHeaderView(rowheader);
			rowheaderscroll.setViewport(scrollpane.getRowHeader());
			rowheaderscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			rowheaderscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			// scrollpane.setCorner( JScrollPane.UPPER_LEFT_CORNER,
			// rowheader.getTableHeader() );

			rowheader.getRowSorter().addRowSorterListener(new RowSorterListener() {
				@Override
				public void sorterChanged(RowSorterEvent e) {
					c.repaint();
				}
			});

			splitpane.setLeftComponent(rowheaderscroll);
			splitpane.setRightComponent(scrollpane);

			JComponent fillup = new JComponent() {};
			fillup.setPreferredSize(new Dimension(6000, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(6000, rh));
			c.setSize(6000, rh);
		}
		
		/*JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( gsplitpane );*/
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( sequenceView );
		toolbar.add( blocksView );
		toolbar.add( realView );
		toolbar.add( zoomIn );
		toolbar.add( zoomOut );
		toolbar.add( backTen );
		toolbar.add( back );
		toolbar.add( forw );
		toolbar.add( forwTen );
		toolbar.add( recenter );
		toolbar.add( addrelated );
		toolbar.add( highrel );
		toolbar.add( showseqs );
		toolbar.add( showdnaseqs );
		toolbar.add( mbr );
		toolbar.add( turn );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( splitpane );

		frame.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					GeneSet.saveContigOrder();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add( panel );

		frame.setVisible(true);
	}
}
