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
import java.util.Collection;
import java.util.Collections;
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
import javax.swing.JMenuItem;
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

import org.simmi.shared.Sequence;

public class Neighbour {
	public void recenter( JTable rowheader, JComponent c ) {
		selectedGenesGroups = new HashSet<GeneGroup>();
		selectedGenesGroups.add( currentTe.getGene().getGeneGroup() );
		//hteg = loadContigs( selectedGenes, null );
		hteg.clear();
		hteg = new ArrayList<Tegeval>();
		for( GeneGroup selectedGeneGroup : selectedGenesGroups ) {
			for( Gene selectedGene : selectedGeneGroup.genes ) {
				hteg.add( selectedGene.tegeval );
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
	
	public Tegeval getSelectedTe( Point p, JTable rowheader, JRadioButton sequenceView, JRadioButton realView, List<Tegeval> lte, int rowheight ) {
		if( sequenceView.isSelected() || realView.isSelected() ) {			
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
					
					Tegeval thenext = next.getNext();
					int bil = 10;
					if( thenext != null && realView.isSelected() ) {
						bil = next.getContshort().isReverse() ? Math.abs( thenext.stop-next.start ) : Math.abs( thenext.start-next.stop );
						bil = (int)(neighbourscale*bil/3);
					}
					xoff += len+bil;
					
					if( thenext == null ) {
						int k = next.getContshort().partof.indexOf( next.getContshort() );
						k = (k+1)%next.getContshort().partof.size();
						Contig c = next.getContshort().partof.get(k);
						while( c.tlist == null || c.tlist.size() == 0 ) {
							k = (k+1)%next.getContshort().partof.size();
							c = next.getContshort().partof.get(k);
						}
						thenext = c.getFirst();
						//if( c.isReverse() ) thenext = c.tlist.get( c.tlist.size()-1 );
						//else thenext = c.tlist.get(0);
					}
					
					next = thenext;
				}
				
				Tegeval prev = te.getPrevious();
				int bil = 10;
				if( prev != null && realView.isSelected() ) {
					bil = prev.getContshort().isReverse() ? Math.abs( prev.start-te.stop ) : Math.abs( prev.stop-te.start );
					bil = (int)(neighbourscale*bil/3);
					
					//if( prev.getContshort().getSpec().contains("2127") ) System.err.println( "bl " + bil );
				}
				
				if( prev == null ) {
					Contig prevcontig = te.getContshort();
					List<Contig> partof = prevcontig.partof;;
					int k = partof.indexOf( prevcontig );
					k--;
					if( k < 0 ) k = partof.size()-1;
					Contig c = partof.get(k);
					while( c.tlist == null || c.tlist.size() == 0 ) {
						k--;
						if( k < 0 ) k = partof.size()-1;
						c = partof.get(k);
					}
					prev = c.getLast();
				}
				
				xoff = 3000;
				//int k = 0;
				while( prev != null && xoff > 5 ) {					
					double len = prev.getProteinLength()*neighbourscale;
					
					Tegeval theprev = prev.getPrevious();
					xoff -= len+bil;
					bil = 10;
					if( theprev != null && realView.isSelected() ) {
						bil = prev.getContshort().isReverse() ? Math.abs( theprev.start-prev.stop ) : Math.abs( theprev.stop-prev.start );
						bil = (int)(neighbourscale*bil/3);
					}
					
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					if( rect.contains( p ) ) return prev;
					
					if( theprev == null ) {
						Contig prevcontig = prev.getContshort();
						
						/*System.err.println( prevcontig.getSpec() );
						if( prevcontig.getSpec().contains("eggertsoni") ) {
							System.err.println();
						}*/
						List<Contig> partof = prevcontig.partof;;
						int k = partof.indexOf( prevcontig );
						k--;
						if( k < 0 ) k = partof.size()-1;
						Contig c = partof.get(k);
						while( c.tlist == null || c.tlist.size() == 0 ) {
							k--;
							if( k < 0 ) k = partof.size()-1;
							c = partof.get(k);
						}
						theprev = c.getLast();
					}
					
					prev = theprev;
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
	
	public void injectForward( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContshort().isReverse() ) {
				/*Tegeval previous = currentTe.getPrevious();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.setPrevious( te );
				te.setPrevious( previous );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.getContshort().injectBefore( currentTe, te );
			} else {
				/*Tegeval next = currentTe.getNext();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				te.setPrevious( currentTe );
				next.setPrevious( te );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.getContshort().injectAfter( currentTe, te );
			}
			c.repaint();
		}
	}
	
	public void injectBack( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContshort().isReverse() ) {
				/*Tegeval next = currentTe.getNext();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				te.setPrevious( currentTe );
				next.setPrevious( te );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.getContshort().injectAfter( currentTe, te );
			} else {
				/*Tegeval previous = currentTe.getPrevious();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.setPrevious( te );
				te.setPrevious( previous );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContshort(), null, 0, 0, 1 );
				currentTe.getContshort().injectBefore( currentTe, te );
			}
			c.repaint();
		}
	}
	
	public void deleteForward( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContshort().isReverse() ) {
				/*Tegeval prevprev = currentTe.prev.prev;
				currentTe.setPrevious( prevprev );*/
				currentTe.getContshort().deleteBefore( currentTe );
			} else {
				/*Tegeval nextnext = currentTe.next.next;
				nextnext.setPrevious( currentTe );*/
				currentTe.getContshort().deleteAfter( currentTe );
			}
			c.repaint();
		}
	}
	
	public void deleteBack( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContshort().isReverse() ) {
				/*Tegeval nextnext = currentTe.getNext().getNext();
				nextnext.setPrevious( currentTe );*/
				currentTe.getContshort().deleteAfter( currentTe );
			} else {
				/*Tegeval prevprev = currentTe.getPrevious().getPrevious();
				currentTe.setPrevious( prevprev );*/
				currentTe.getContshort().deleteBefore( currentTe );
			}
			c.repaint();
		}
	}
	
	int total = 0;
	int ptotal = 0;
	public void initContigs( String spec1, GeneSet geneset ) {
		if( spec1 != null && geneset.speccontigMap.containsKey( spec1 ) ) {
			final List<Contig> lcont = geneset.speccontigMap.get( spec1 );
			
			ptotal = 0;
			total = 0;
			//List<Contig> contigs = new ArrayList<Contig>();
			for( Contig ctg : lcont ) {
				total += ctg.getGeneCount();
			}
			
			if( lcont.size() <= 3 ) {
				int max = 0;
				Contig chromosome = null;
				for( Contig ctg : lcont ) {
					if( ctg.getGeneCount() > max ) {
						max = ctg.getGeneCount();
						chromosome = ctg;
					}
				}
				
				ptotal = total - chromosome.getGeneCount();
				total = chromosome.getGeneCount();
			}
		}
	}
	
	double neighbourscale = 1.0;
	static Tegeval currentTe = null;
	Set<GeneGroup> selectedGenesGroups;
	static List<Tegeval>	hteg;
	//static int colorscheme = 0;
	//static List<String>	speclist;
	public void neighbourMynd( final GeneSet geneset, final Container comp, final List<Gene> genes, final Set<GeneGroup> selGenes, final Map<String,Contig> contigmap ) throws IOException {
		final JTable sorting = geneset.getGeneTable();
		
		selectedGenesGroups = selGenes;
		
		final JRadioButton	sequenceView = new JRadioButton("Sequence");
		final JRadioButton	blocksView = new JRadioButton("Blocks");
		final JRadioButton	realView = new JRadioButton("Real");
		
		final JButton	zoomIn = new JButton("+");
		final JButton	zoomOut = new JButton("-");
		final JButton	recenter = new JButton("Recenter");
		final JButton	addrelated = new JButton("Add related");
		final JButton	highrel = new JButton("Highlight related");
		
		final JMenuBar	mbr = new JMenuBar();
		final JMenu		seqsmenu = new JMenu("Show");
		final JMenu		mnu = new JMenu("Colors");
		final JMenu		mvmnu = new JMenu("Move");
		final JMenu		selmnu = new JMenu("Select");
		final JButton	turn = new JButton("Forward");
		
		final JButton	backTen = new JButton("<<");
		final JButton	back = new JButton("<");
		final JButton	forw = new JButton(">");
		final JButton	forwTen = new JButton(">>");
		
		final JCheckBox		commonname = new JCheckBox("Group names");
		final JCheckBox		noname = new JCheckBox("No names");
		
		mbr.add( seqsmenu );
		mbr.add( mnu );
		mbr.add( mvmnu );
		mbr.add( selmnu );
		final JRadioButtonMenuItem funcol = new JRadioButtonMenuItem("Functions");
		final JRadioButtonMenuItem gccol = new JRadioButtonMenuItem("GC%");
		final JRadioButtonMenuItem gcskewcol = new JRadioButtonMenuItem("GC skew");
		final JRadioButtonMenuItem abucol = new JRadioButtonMenuItem("Abundance");
		final JRadioButtonMenuItem relcol = new JRadioButtonMenuItem("Relation");
		final JRadioButtonMenuItem sgradcol = new JRadioButtonMenuItem("Synteny gradient");
		final JRadioButtonMenuItem precol = new JRadioButtonMenuItem("Proximity preservation");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( funcol );
		bg.add( gccol );
		bg.add( gcskewcol );
		bg.add( abucol );
		bg.add( relcol );
		bg.add( sgradcol );
		bg.add( precol );
		mnu.add( funcol );
		mnu.add( gccol );
		mnu.add( gcskewcol );
		mnu.add( abucol );
		mnu.add( relcol );
		mnu.add( sgradcol );
		mnu.add( precol );
		
		final Map<String,Integer> blosumap = GeneCompare.getBlosumMap();
		
		final JFrame frame = new JFrame();
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			//hteg = loadContigs( genes, null );
			//hteg.clear();
			hteg = new ArrayList<Tegeval>();
			for( GeneGroup selectedGeneGroup : selectedGenesGroups ) {
				for( Gene selectedGene : selectedGeneGroup.genes ) {
					hteg.add( selectedGene.tegeval );
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
					Tegeval te = getSelectedTe(p, rowheader, sequenceView, realView, hteg, rowheader.getRowHeight());
					if( te != null ) {
						Gene g = te.getGene();
						GeneGroup gg = g.getGeneGroup();
						if( gg != null ) return "<html>"+(g.getName().equals( g.refid ) ? gg.getCommonName() : g.getName())+ "<br>" + te.getGene().refid + "<br>" + gg.getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>";
					}
					return null;
				}
				
				public void paintComponent( Graphics g ) {
					super.paintComponent(g);
					
					Map<String,Integer>	offsetMap = new HashMap<String,Integer>();
					for( GeneGroup gg : selectedGenesGroups ) {
						for( String spec2 : gg.getSpecies() ) {
							final Collection<Contig> contigs2 = geneset.speccontigMap.get( spec2 );
							if( contigs2 != null ) {
								Teginfo gene2s = gg.getGenes( spec2 );
								for( Tegeval tv2 : gene2s.tset ) {
									int count2 = 0;
									for( Contig ctg2 : contigs2 ) {
										if( ctg2.tlist != null ) {
											int idx = ctg2.tlist.indexOf( tv2 );
											if( idx == -1 ) {
												count2 += ctg2.getGeneCount();
											} else {
												count2 += idx;
												break;
											}
										}
									}
									offsetMap.put(spec2, count2);
								}
							}
						}
						break;
					}
					
					String spec1 = null;
					int 		rs = rowheader.getSelectedRow();
					if( rs >= 0 && rs < rowheader.getRowCount() ) spec1 = (String)rowheader.getValueAt(rs, 0);
					initContigs( spec1, geneset );
					
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
									Gene gene = next.getGene();
									if( gene != null ) {
										String genename = gene.getName();
										if( commonname.isSelected() && (genename == null || genename.contains("_")) ) {
											GeneGroup gg = gene.getGeneGroup();
											if( gg != null ) genename = gg.getCommonName();
										}
										if( genename != null ) genename = genename.contains("hypothetical") ? "hth-p" : genename;
										
										if( xoff+len > clip.x ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
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
												int numspec = Math.min( gg.species.size(), 39 );
												float abu = numspec/39.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( relcol.isSelected() ) {
												if( spec1 != null ) {
													
													//StringBuilder seq = next.seq;
													Color rc = Color.green;
													GeneGroup gg = next.getGene().getGeneGroup();
													List<Tegeval> ltv = gg.getTegevals( spec1 );
													if( ltv != null && ltv.size() > 0 ) {
														rc = GeneCompare.blosumColor( ltv.get(0), next.getSpecies(), gg, blosumap, false );
													} else {
														rc = Color.white;
													}
													if( rc != null ) g.setColor( rc );
												}
											} else if( sgradcol.isSelected() ) {
												if( spec1 != null ) {													
													//StringBuilder seq = next.seq;
													Color rc = Color.black;
													GeneGroup gg = next.getGene().getGeneGroup();
													List<Tegeval> ltv = gg.getTegevals( spec1 );
													if( ltv != null && ltv.size() > 0 ) {
														//String spec2 = next.getSpecies();
														final Collection<Contig> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
														
														/*double ratio = 0.0;
														double pratio = 0.0;
														if( ptotal > 0 ) {
															if( ctg.getGeneCount() == total ) {
																int val = count - offset;
																if( val < 0 ) val = total + (count-offset);
																
																ratio = (double)(val-current)/(double)total;
															} else {
																if( count - total >= 0 ) {
																	pratio = (double)(count-total)/(double)ptotal;
																} else {
																	pratio = (double)(count)/(double)ptotal;
																}
															}
														} else {
															int val = count - offset;
															if( val < 0 ) val = total + (count-offset);
															
															ratio = (double)val/(double)total;
														}*/
														
														//int offset2 = 0;
														//if( offsetMap.containsKey( spec2 ) ) offset2 = offsetMap.get(spec2);
														//rc = GeneCompare.gradientColor( spec1, spec2, contigs2, 0.0, 0.0, offset2, gg );
														double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg);
														rc = GeneCompare.invertedGradientColor( ratio );
													} else {
														rc = Color.white;
													}
													if( rc != null ) g.setColor( rc );
												}
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
											} else if( gcskewcol.isSelected() ) {
												g.setColor( next.getGCSkewColor() );
											} else {
												g.setColor( next.getGCColor() );
												/*if( next.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}*/
											}
											
											Contig ncont = next.getContshort();
											boolean revis = (next.ori == -1) ^ (ncont != null && ncont.isReverse());
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
									
											int gap = next.unresolvedGap();
											g.setColor( Color.red );
											if( (gap & 1) > 0 ) {
												//g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
												if( !next.getContshort().isReverse() ) g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
												else g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
											}
											if( (gap & 2) > 0 ) {
												//g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
												if( !next.getContshort().isReverse() ) g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
												else g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
											}
											/*if( fc != Color.lightGray ) {
												g.setColor( fc );
												g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
											}*/
											
											g.setColor( Color.black );
											//g.fillRect(xoff, y * rowheader.getRowHeight()+2, (int)len, rowheader.getRowHeight() - 4);
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											
											if( !noname.isSelected() ) {
												if( relcol.isSelected() ) g.setColor( Color.white );
												else g.setColor( Color.black );
												g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
											}
										}
									}
									
									Tegeval thenext = next.getNext();
									int bil = 10;
									if( thenext != null && realView.isSelected() ) {
										/*if( next.getGene().getGeneGroup().getCommonName().contains("Elongation") && next.getSpecies().contains("antranik") ) {
											System.err.println();
										}*/
										
										bil = next.getContshort().isReverse() ? Math.abs( thenext.stop-next.start ) : Math.abs( thenext.start-next.stop );
										bil = (int)(neighbourscale*bil/3);
									}
									
									if( thenext == null ) {
										Contig ncont = next.getContshort();
										if( ncont != null ) {
											int k = ncont.partof.indexOf( ncont );
											k = (k+1)%ncont.partof.size();
											Contig c = ncont.partof.get(k);
											while( c.tlist == null || c.tlist.size() == 0 ) {
												k = (k+1)%ncont.partof.size();
												c = ncont.partof.get(k);
											}
											thenext = c.getFirst();
											//if( c.isReverse() ) thenext = c.tlist.get( c.tlist.size()-1 );
											//else thenext = c.tlist.get(0);
											
											g.setColor( Color.black );
											g.fillRect(xPoints[2], yPoints[2]-7, 3, 15);
										}
									}
									
									/*if( thenext != null && thenext.getNext() == next ) {
										thenext = null;
									}*/
									
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

								if( prev == null ) {
									Contig prevcont = te.getContshort();
									if( prevcont != null ) {
										List<Contig> partof = prevcont.partof;
										int k = partof.indexOf( prevcont );
										k--;
										if( k < 0 ) k = partof.size()-1;
										Contig c = partof.get(k);
										while( c.tlist == null || c.tlist.size() == 0 ) {
											k--;
											if( k < 0 ) k = partof.size()-1;
											c = partof.get(k);
										}
										prev = c.getLast();
										
										int xp = xoff;
										int yp = i * rowheader.getRowHeight()+2+6;
										
										g.setColor( Color.black );
										g.fillRect(xp+8, yp-7, 3, 15);
									}
								}
								
								int bil = 10;
								if( prev != null && realView.isSelected() ) {
									bil = prev.getContshort().isReverse() ? Math.abs( prev.start-te.stop ) : Math.abs( prev.stop-te.start );
									//bil = Math.abs( theprev.stop-prev.start );
									bil = (int)(neighbourscale*bil/3);
									
									//if( prev.getContshort().getSpec().contains("2127") ) System.err.println( bil );
									//xoff -= bil;
								}
								
								//int k = 0;
								while( prev != null && xoff >= 500 && clip.x < xoff ) {
									double len = prev.getProteinLength()*neighbourscale;
									xoff -= len+bil;
									Tegeval theprev = prev.getPrevious();
									bil = 10;
									if( theprev != null && realView.isSelected() ) {
										/*if( prev.getGene().getGeneGroup().getCommonName().contains("Elongation") && prev.getSpecies().contains("antranik") ) {
											System.err.println();
										}*/
										
										bil = prev.getContshort().isReverse() ? Math.abs( theprev.start-prev.stop ) : Math.abs( theprev.stop-prev.start );
										//bil = Math.abs( theprev.stop-prev.start );
										bil = (int)(neighbourscale*bil/3);
									}
									
									/*if( theprev != null && theprev.getPrevious() == prev ) {
										theprev = null;
									}*/
									
									Gene gene = prev.getGene();
									if( gene != null ) {
										String genename = prev.getGene().getName();
										if( commonname.isSelected() && genename.contains("_") ) {
											GeneGroup gg = prev.getGene().getGeneGroup();
											if( gg != null ) genename = gg.getCommonName();
										}
										genename = (gene != null && genename.contains("hypothetical")) ? "hth-p" : genename;
										
										if( clip.x+clip.width > xoff ) {
											if( funcol.isSelected() ) {
												g.setColor( Color.green );
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
												int numspec = Math.min( 39, gg.species.size() );
												float abu = numspec/39.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( relcol.isSelected() ) {												
												//StringBuilder seq = next.seq;
												Color rc = Color.green;
												GeneGroup gg = prev.getGene().getGeneGroup();
												List<Tegeval> ltv = gg.getTegevals( spec1 );
												if( ltv != null && ltv.size() > 0 ) {
													rc = GeneCompare.blosumColor( ltv.get(0), prev.getSpecies(), gg, blosumap, false );
												} else {
													rc = Color.white;
												}
												if( rc != null ) g.setColor( rc );												
											} else if( sgradcol.isSelected() ) {
												if( spec1 != null ) {													
													//StringBuilder seq = next.seq;
													Color rc = Color.black;
													GeneGroup gg = prev.getGene().getGeneGroup();
													List<Tegeval> ltv = gg.getTegevals( spec1 );
													if( ltv != null && ltv.size() > 0 ) {
														final Collection<Contig> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
														double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg);
														rc = GeneCompare.invertedGradientColor( ratio );
													} else {
														rc = Color.white;
													}
													if( rc != null ) g.setColor( rc );
												}
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
											} else if( gcskewcol.isSelected() ) {
												g.setColor( prev.getGCSkewColor() );
											} else {
												g.setColor( prev.getGCColor() );
												/*if( prev.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)prev.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}*/
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
											
											int gap = prev.unresolvedGap();
											g.setColor( Color.red );
											if( (gap & 1) > 0 ) {
												//g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
												if( !prev.getContshort().isReverse() ) g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
												else g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
											}
											if( (gap & 2) > 0 ) {
												//g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
												if( !prev.getContshort().isReverse() ) g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
												else g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
											}
											
											g.setColor( Color.black );
											
											int strlen = g.getFontMetrics().stringWidth( genename );
											while( strlen > len ) {
												genename = genename.substring(0, genename.length()-1);
												strlen = g.getFontMetrics().stringWidth( genename );
											}
											
											if( !noname.isSelected() ) {
												if( relcol.isSelected() ) g.setColor( Color.white );
												else g.setColor( Color.black );
												g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
											}
										}
									}
									
									if( theprev == null ) {
										Contig prevcont = prev.getContshort();
										List<Contig> partof = prevcont.partof;
										int k = partof.indexOf( prevcont );
										k--;
										if( k < 0 ) k = partof.size()-1;
										Contig c = partof.get(k);
										while( c.tlist == null || c.tlist.size() == 0 ) {
											k--;
											if( k < 0 ) k = partof.size()-1;
											c = partof.get(k);
										}
										theprev = c.getLast();
										
										g.setColor( Color.black );
										g.fillRect(xPoints[2]+8, yPoints[2]-7, 3, 15);
									}
									
									prev = theprev;
									//xoff -= len + bil;
									
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
										String genename = next.getGene().getName();
										if( commonname.isSelected() && genename.contains("_") ) genename = next.getGene().getGeneGroup().getCommonName();
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
												int numspec = Math.min( 39, gg.species.size() );
												float abu = numspec/39.0f;
												Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
												g.setColor( rc );
											} else if( relcol.isSelected() ) {
												//StringBuilder seq = next.seq;
												Color rc = Color.green;
												GeneGroup gg = next.getGene().getGeneGroup();
												List<Tegeval> ltv = gg.getTegevals( spec1 );
												if( ltv != null && ltv.size() > 0 ) {
													rc = GeneCompare.blosumColor( ltv.get(0), next.getSpecies(), gg, blosumap, false );
												} else {
													rc = Color.white;
												}
												if( rc != null ) g.setColor( rc );
											} else if( sgradcol.isSelected() ) {
												if( spec1 != null ) {													
													//StringBuilder seq = next.seq;
													Color rc = Color.black;
													GeneGroup gg = next.getGene().getGeneGroup();
													List<Tegeval> ltv = gg.getTegevals( spec1 );
													if( ltv != null && ltv.size() > 0 ) {
														final Collection<Contig> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
														double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg);
														rc = GeneCompare.invertedGradientColor( ratio );
													} else {
														rc = Color.white;
													}
													if( rc != null ) g.setColor( rc );
												}
											} else if( precol.isSelected() ) {
												Map<GeneGroup,Integer>	shanmap = new HashMap<GeneGroup,Integer>(); 
												shanmap.clear();
												double res = 0.0;
												
												List<Tegeval> tegevals = next.getGene().getGeneGroup().getTegevals();
												int total = tegevals.size();
												for( Tegeval tev : tegevals ) {
													Tegeval thenext = tev.getNext();
													GeneGroup c = thenext == null ? null : (thenext.getGene() != null ? thenext.getGene().getGeneGroup() : null);
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
											} else if( gcskewcol.isSelected() ) {
												g.setColor( next.getGCSkewColor() );
											} else {
												g.setColor( next.getGCColor() );
												/*if( next.getGCPerc() <= 0 ) {
													Color rc = new Color( 1.0f, 1.0f, 1.0f );
													g.setColor( rc );
												} else {
													float gc = Math.max( 0.0f, Math.min(((float)next.getGCPerc()-0.5f)*4.0f, 1.0f) );
													Color rc = new Color( 1.0f-gc, gc, 1.0f );
													g.setColor( rc );
												}*/
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
											
											if( !noname.isSelected() ) {
												if( relcol.isSelected() ) g.setColor( Color.white );
												else g.setColor( Color.black );
												g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
											}
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
								if( te != null ) {
									Tegeval thenext = te.getNext();
									if( thenext != null && thenext.getNext() == te ) {
										thenext = null;
									}
									hteglocal.set(i, thenext);
								}
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
										String genename = prev.getGene().getName();
										if( commonname.isSelected() && genename.contains("_") ) genename = prev.getGene().getGeneGroup().getCommonName();
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
											int numspec = Math.min( 39, gg.species.size() );
											float abu = numspec/39.0f;
											Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
											g.setColor( rc );
										} else if( sgradcol.isSelected() ) {
											if( spec1 != null ) {													
												//StringBuilder seq = next.seq;
												Color rc = Color.black;
												GeneGroup gg = prev.getGene().getGeneGroup();
												List<Tegeval> ltv = gg.getTegevals( spec1 );
												if( ltv != null && ltv.size() > 0 ) {
													final Collection<Contig> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
													double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg);
													rc = GeneCompare.invertedGradientColor( ratio );
												} else {
													rc = Color.white;
												}
												if( rc != null ) g.setColor( rc );
											}
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
										} else if( gcskewcol.isSelected() ) {
											g.setColor( prev.getGCSkewColor() );
										} else {
											g.setColor( prev.getGCColor() );
											/*if( prev.getGCPerc() <= 0 ) {
												Color rc = new Color( 1.0f, 1.0f, 1.0f );
												g.setColor( rc );
											} else {
												float gc = Math.max( 0.0f, Math.min(((float)prev.getGCPerc()-0.5f)*4.0f, 1.0f) );
												Color rc = new Color( 1.0f-gc, gc, 1.0f );
												g.setColor( rc );
											}*/
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
										
										if( !noname.isSelected() ) {
											if( relcol.isSelected() ) g.setColor( Color.white );
											g.setColor( Color.black );
											g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheader.getRowHeight()-5 );
										}
									}
								}
							}
							
							for( int i = 0; i < hteglocal.size(); i++ ) {
								Tegeval te = hteglocal.get(i);
								if( te != null ) {
									Tegeval theprev = te.getPrevious();
									if( theprev != null && theprev.getPrevious() == te ) {
										theprev = null;
									}
									hteglocal.set( i, theprev );
								}
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
			
			c.addKeyListener( new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
						injectBack( c );
					} else if( e.getKeyCode() == KeyEvent.VK_TAB ) {
						injectForward( c );
					}  else if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
						deleteBack( c );
					}  else if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
						deleteForward( c );
					}
				}
			});
			
			final AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			funcol.setAction( a );
			gccol.setAction( a );
			gcskewcol.setAction( a );
			abucol.setAction( a );
			precol.setAction( a );
			
			commonname.setAction( a );
			noname.setAction( a );
			
			funcol.setText("Functions");
			gccol.setText("GC%");
			gcskewcol.setText("GC skew");
			abucol.setText("Abundance");
			precol.setText("Proximity preservation");
			
			commonname.setText("Group names");
			noname.setText( "No names" );
			
			turn.setAction( new AbstractAction("Forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					//System.err.println("hteg " + hteg.size());
					for( Tegeval te : hteg ) {
						boolean rev = te.ori == -1 ^ te.getContshort().isReverse();
						if( rev ) {
							List<Contig>	partof = te.getContshort().partof;
							for( Contig ctg : partof ) {
								ctg.setReverse( ctg.isReverse() );
							}
							Collections.reverse( partof );
						}
					}
					c.repaint();
				}
			});
			mvmnu.add( new AbstractAction("Inject forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					injectForward( c );
				}
			});
			mvmnu.add( new AbstractAction("Inject back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					injectBack( c );
				}
			});
			mvmnu.add( new AbstractAction("Delete forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteForward( c );
				}
			});
			mvmnu.add( new AbstractAction("Delete back") {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteBack( c );
				}
			});
			mvmnu.addSeparator();
			mvmnu.add( new AbstractAction("Align forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Map<String,Integer>	spind = new HashMap<String,Integer>();
					Map<String,Contig>  ctind = new HashMap<String,Contig>();
					
					for( GeneGroup gg : selectedGenesGroups ) {
						for( String sp : gg.species.keySet() ) {
							Teginfo ti = gg.species.get( sp );
							
							Contig ct = ti.best.contshort;
							ctind.put( sp, ct );
							spind.put( sp, ct.tlist.indexOf( ti.best ) );
						}
					}
					
					for( int i = 1; i < 20; i++ ) {
						for( String spec : ctind.keySet() ) {
							Contig ct = ctind.get( spec );
							//ct.tlist.
						}
					}
				}
			});
			
			selmnu.add( new AbstractAction("Clear selection") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							for( Tegeval tv2 : tv.getContshort().tlist ) {
								tv2.selected = false;
							}
						}
					}
					c.repaint();
				}
			});
			selmnu.addSeparator();
			selmnu.add( new AbstractAction("Next gap") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int r = rowheader.getSelectedRow();
					String spec = (String)rowheader.getValueAt( r, 0 );
					for( GeneGroup gg : selectedGenesGroups ) {
						Teginfo ti = gg.species.get( spec );
						for( Tegeval tv : ti.tset ) {
							int k = tv.getContshort().tlist.indexOf( tv );
							for( int i = k+1; i < tv.getContshort().tlist.size(); i++ ) {
								Tegeval tv2 = tv.getContshort().tlist.get(i);
								currentTe = tv2;
								if( tv2.unresolvedGap() > 0 ) {
									break;
								}
							}
							recenter( rowheader, c );
						}
					}
					c.repaint();
				}
			});
			selmnu.add( new AbstractAction("Previous gap") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int r = rowheader.getSelectedRow();
					String spec = (String)rowheader.getValueAt( r, 0 );
					for( GeneGroup gg : selectedGenesGroups ) {
						Teginfo ti = gg.species.get( spec );
						for( Tegeval tv : ti.tset ) {
							int k = tv.getContshort().tlist.indexOf( tv );
							for( int i = k-1; i >= 0; i-- ) {
								Tegeval tv2 = tv.getContshort().tlist.get(i);
								currentTe = tv2;
								if( tv2.unresolvedGap() > 0 ) {
									break;
								}
							}
							recenter( rowheader, c );
						}
					}
					c.repaint();
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
			
			final JMenuItem	showseqs = new JMenuItem("Sequences");
			final JMenuItem	showdnaseqs = new JMenuItem("DNA sequences");
			final JMenuItem	showselectedseqs = new JMenuItem("Selected sequences");
			final JMenuItem	showselecteddnaseqs = new JMenuItem("Selected DNA sequences");
			final JMenuItem	showflankingseqs = new JMenuItem("Show flanking sequences");
			final JMenuItem	showbackflankingseqs = new JMenuItem("Show back flanking sequences");
			showseqs.setAction( new AbstractAction("Sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					geneset.showSequences( comp, selectedGenesGroups, false );
				}
			});
			showdnaseqs.setAction( new AbstractAction("DNA sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					geneset.showSequences( comp, selectedGenesGroups, true );
				}
			});
			showselectedseqs.setAction( new AbstractAction("Selected sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Tegeval>	tset = new HashSet<Tegeval>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							for( Tegeval tv2 : tv.getContshort().tlist ) {
								if( tv2.isSelected() ) tset.add( tv2 );
							}
						}
					}
					geneset.showSelectedSequences( comp, tset, false );
				}
			});
			showselecteddnaseqs.setAction( new AbstractAction("Selected DNA sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Tegeval>	tset = new HashSet<Tegeval>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							for( Tegeval tv2 : tv.getContshort().tlist ) {
								if( tv2.isSelected() ) tset.add( tv2 );
							}
						}
					}
					geneset.showSelectedSequences( comp, tset, true );
				}
			});
			showflankingseqs.setAction( new AbstractAction("Show flanking sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Sequence> lseq = new ArrayList<Sequence>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							Tegeval prev = null;
							//Tegeval prevprev = null;
							for( Tegeval tv2 : tv.getContshort().tlist ) {
								/*if( tv2.getSpecies().contains("antra") && tv2.getGene().getGeneGroup().getCommonName().contains("Elongation") ) {
									System.err.println();
								}*/
								
								if( prev != null && prev.isSelected() && prev.ori == 1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContshort().length() ) {
										Sequence seq = new Sequence( prev.getGene().getGeneGroup().getCommonName(), null );
										seq.append( prev.getContshort().sb.substring(start, stop) );
										lseq.add( seq );
									}
								} else if( tv2 != null && tv2.isSelected() && tv2.ori == -1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContshort().length() ) {
										Sequence seq = new Sequence( tv2.getGene().getGeneGroup().getCommonName(), null );
										seq.append( tv2.getContshort().sb.substring(start, stop) );
										lseq.add( seq );
									}
								}
								
								//prevprev = prev;
								prev = tv2;
							}
						}
					}
					geneset.showSomeSequences( comp, lseq );
				}
			});
			showbackflankingseqs.setAction( new AbstractAction("Show back flanking sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Sequence> lseq = new ArrayList<Sequence>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							Tegeval prev = null;
							//Tegeval prevprev = null;
							for( Tegeval tv2 : tv.getContshort().tlist ) {
								/*if( tv2.getSpecies().contains("antra") && tv2.getGene().getGeneGroup().getCommonName().contains("Elongation") ) {
									System.err.println();
								}*/
								
								if( prev != null && prev.isSelected() && prev.ori == -1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContshort().length() ) {
										Sequence seq = new Sequence( prev.getGene().getGeneGroup().getCommonName(), null );
										seq.append( prev.getContshort().sb.substring(start, stop) );
										lseq.add( seq );
									}
								} else if( tv2 != null && tv2.isSelected() && tv2.ori == 1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContshort().length() ) {
										Sequence seq = new Sequence( tv2.getGene().getGeneGroup().getCommonName(), null );
										seq.append( tv2.getContshort().sb.substring(start, stop) );
										lseq.add( seq );
									}
								}
								
								//prevprev = prev;
								prev = tv2;
							}
						}
					}
					geneset.showSomeSequences( comp, lseq );
				}
			});
			seqsmenu.add( showseqs );
			seqsmenu.add( showdnaseqs );
			seqsmenu.addSeparator();
			seqsmenu.add( showselectedseqs );
			seqsmenu.add( showselecteddnaseqs );
			seqsmenu.add( showflankingseqs );
			seqsmenu.add( showbackflankingseqs );
			
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
					c.requestFocus();
					
					Tegeval te = getSelectedTe( p, rowheader, sequenceView, realView, hteg, rowheader.getRowHeight() );
					//System.err.println();
					if( te != null ) {
						if( me.getClickCount() == 2 ) {
							c.setToolTipText( "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+"<br>"+ te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>" );
							//c.sett
						} else {
							te.setSelected( !te.isSelected() );
							int i;
							if( sorting.getModel() == geneset.groupModel ) {
								i = geneset.allgenegroups.indexOf( te.getGene().getGeneGroup() );
							} else {
								i = genes.indexOf( te.getGene() );
							}
							if( i != -1 ) {
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
					for( String name : contigmap.keySet() ) {
						Contig c = contigmap.get( name );
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
							return specont.get(rowIndex).getName();
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
							Tegeval con = cont.getEndTegeval().getNext();
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.getStartTegeval().getPrevious() : cont.getEndTegeval().getNext();
								
								if( con != null && con.getContshort().equals( cont ) ) {
									break;
								}
							}
						} else {
							Tegeval con = cont.getStartTegeval().getPrevious();
							while( con != null ) {
								cont = con.getContshort();
								con = cont.isReverse() ? cont.getStartTegeval().getPrevious() : cont.getEndTegeval().getNext();
								
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
						hteg.set( i, te.getNext() == null ? te : te.getNext() );
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
						hteg.set( i, te.getPrevious() == null ? te : te.getPrevious() );
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
				public void keyReleased(KeyEvent e) {}
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
		toolbar.add( mbr );
		toolbar.add( turn );
		toolbar.add( commonname );
		toolbar.add( noname );
		
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
					geneset.saveContigOrder();
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
