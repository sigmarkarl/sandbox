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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JComboBox;
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
import javax.swing.TransferHandler;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.simmi.shared.Annotation;
import org.simmi.shared.Cog;
import org.simmi.shared.Function;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;
import org.simmi.unsigned.JavaFasta;

import javafx.scene.control.TableView;

public class Neighbour {
	public Neighbour( Set<GeneGroup> sgg ) {
		selectedGenesGroups = sgg;
		
		hteg = new ArrayList<Annotation>();
		for( GeneGroup selectedGeneGroup : selectedGenesGroups ) {
			for( Gene selectedGene : selectedGeneGroup.genes ) {
				hteg.add( selectedGene.tegeval );
			}
		}
	}
	
	public void recenter( JTable rowheader, JComponent c ) {
		selectedGenesGroups = new HashSet<GeneGroup>();
		selectedGenesGroups.add( currentTe.getGene().getGeneGroup() );
		//hteg = loadContigs( selectedGenes, null );
		hteg.clear();
		hteg = new ArrayList<Annotation>();
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
	
	public Annotation getSelectedTe( Point p, JTable rowheader, JRadioButton sequenceView, JRadioButton realView, List<Annotation> lte, int rowheight ) {
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
				
				Annotation te = lte.get(r);
				Annotation next = te;
				//int k = 0;
				while( next != null && xoff < 5500 ) {					
					double len = next.getProteinLength()*neighbourscale;											
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					
					if( rect.contains( p ) ) return next;
					
					Annotation thenext = next.getNext();
					int bil = 10;
					if( thenext != null && realView.isSelected() ) {
						bil = next.getContig().isReverse() ? Math.abs( thenext.stop-next.start ) : Math.abs( thenext.start-next.stop );
						bil = (int)(neighbourscale*bil/3);
					}
					xoff += len+bil;
					
					if( thenext == null ) {
						Sequence ncont = next.getContig();
						if( ncont.isChromosome() ) {
							thenext = ncont.getFirst();
						} else {
						int k = ncont.partof.indexOf( next.getContig() );
							k = (k+1)%ncont.partof.size();
							Sequence c = ncont.partof.get(k);
							while( c.annset == null || c.annset.size() == 0 ) {
								k = (k+1)%ncont.partof.size();
								c = ncont.partof.get(k);
							}
							thenext = c.getFirst();
						}
						//if( c.isReverse() ) thenext = c.annset.get( c.annset.size()-1 );
						//else thenext = c.annset.get(0);
					}
					
					next = thenext;
				}
				
				Annotation prev = te.getPrevious();
				int bil = 10;
				if( prev != null && realView.isSelected() ) {
					bil = prev.getContig().isReverse() ? Math.abs( prev.start-te.stop ) : Math.abs( prev.stop-te.start );
					bil = (int)(neighbourscale*bil/3);
					
					//if( prev.getContig().getSpec().contains("2127") ) System.err.println( "bl " + bil );
				}
				
				if( prev == null ) {
					Sequence prevcontig = te.getContig();
					if( prevcontig.isChromosome() ) {
						prev = prevcontig.getLast();
					} else {
						List<Sequence> partof = prevcontig.partof;;
						int k = partof.indexOf( prevcontig );
						k--;
						if( k < 0 ) k = partof.size()-1;
						Sequence c = partof.get(k);
						while( c.annset == null || c.annset.size() == 0 ) {
							k--;
							if( k < 0 ) k = partof.size()-1;
							c = partof.get(k);
						}
						prev = c.getLast();
					}
				}
				
				xoff = 3000;
				//int k = 0;
				while( prev != null && xoff > 5 ) {					
					double len = prev.getProteinLength()*neighbourscale;
					
					Annotation theprev = prev.getPrevious();
					xoff -= len+bil;
					bil = 10;
					if( theprev != null && realView.isSelected() ) {
						bil = prev.getContig().isReverse() ? Math.abs( theprev.start-prev.stop ) : Math.abs( theprev.stop-prev.start );
						bil = (int)(neighbourscale*bil/3);
					}
					
					Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
					if( rect.contains( p ) ) return prev;
					
					if( theprev == null ) {
						Sequence prevcontig = prev.getContig();
						
						/*System.err.println( prevcontig.getSpec() );
						if( prevcontig.getSpec().contains("eggertsoni") ) {
							System.err.println();
						}*/
						if( prevcontig.isChromosome() ) {
							theprev = prevcontig.getLast();
						} else {
							List<Sequence> partof = prevcontig.partof;;
							int k = partof.indexOf( prevcontig );
							k--;
							if( k < 0 ) k = partof.size()-1;
							Sequence c = partof.get(k);
							while( c.annset == null || c.annset.size() == 0 ) {
								k--;
								if( k < 0 ) k = partof.size()-1;
								c = partof.get(k);
							}
							theprev = c.getLast();
						}
					}
					
					prev = theprev;
				}
				//break;
			}
		} else {
			List<Annotation>	hteglocal = new ArrayList<Annotation>( lte );
			int xoff = 3000;
			//int k = 0;
			while( xoff < 5500 ) {
				int max = 0;
				for( Annotation tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Annotation te = hteglocal.get(r);
					Annotation next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				xoff += max+10;
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Annotation te = hteglocal.get(i);
					if( te != null ) {
						Annotation thenext = te.getNext();
						if( thenext == null ) {
							Sequence cont = te.getContig();
							int k = cont.partof.indexOf( cont );
							k = (k+1)%cont.partof.size();
							Sequence c = cont.partof.get(k);
							while( c.annset == null || c.annset.size() == 0 ) {
								k = (k+1)%cont.partof.size();
								c = cont.partof.get(k);
							}
							thenext = c.getFirst();
							//if( c.isReverse() ) thenext = c.annset.get( c.annset.size()-1 );
							//else thenext = c.annset.get(0);
						}
						//System.err.println( "nexterm " + thenext.name + "  " + te.name + "  " + te.start + "  " + thenext.start );
						hteglocal.set( i, thenext );
					}
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
			
			hteglocal.clear();
			for( Annotation te : lte ) {
				hteglocal.add( te.getPrevious() );
			}
			xoff = 3000;	
			//int k = 0;
			while( xoff > 500 ) {
				int max = 0;
				for( Annotation tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				xoff -= max+10;
				
				for( int y = 0; y < rowheader.getRowCount(); y++ ) {
					int r = rowheader.convertRowIndexToModel( y );
					Annotation te = hteglocal.get(r);
					Annotation next = te;
					
					if( next != null ) {
						double len = next.getProteinLength()*neighbourscale;										
						Rectangle rect = new Rectangle(xoff, y * rowheight+2, (int)len, rowheight - 4);
						
						if( rect.contains( p ) ) return next;
					}
				}
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Annotation te = hteglocal.get(i);
					if( te != null ) {
						Annotation theprev = te.getPrevious();
						if( theprev == null ) {
							Sequence prevcont = te.getContig();
							List<Sequence> partof = prevcont.partof;
							int k = partof.indexOf( prevcont );
							k--;
							if( k < 0 ) k = partof.size()-1;
							Sequence c = partof.get(k);
							while( c.getAnnotations() == null || c.getAnnotations().size() == 0 ) {
								k--;
								if( k < 0 ) k = partof.size()-1;
								c = partof.get(k);
							}
							theprev = c.getLast();
						}
						
						//System.err.println( theprev.name );
						hteglocal.set( i, theprev );
					}
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
			if( currentTe.getContig().isReverse() ) {
				/*Tegeval previous = currentTe.getPrevious();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.setPrevious( te );
				te.setPrevious( previous );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.getContig().injectBefore( currentTe, te );
			} else {
				/*Tegeval next = currentTe.getNext();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				te.setPrevious( currentTe );
				next.setPrevious( te );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.getContig().injectAfter( currentTe, te );
			}
			c.repaint();
		}
	}
	
	public void injectBack( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContig().isReverse() ) {
				/*Tegeval next = currentTe.getNext();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				te.setPrevious( currentTe );
				next.setPrevious( te );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.getContig().injectAfter( currentTe, te );
			} else {
				/*Tegeval previous = currentTe.getPrevious();
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.setPrevious( te );
				te.setPrevious( previous );*/
				Tegeval te = new Tegeval( null, currentTe.getSpecies(), 0.0, null, currentTe.getContig(), null, 0, 0, 1 );
				currentTe.getContig().injectBefore( currentTe, te );
			}
			c.repaint();
		}
	}
	
	public void deleteForward( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContig().isReverse() ) {
				/*Tegeval prevprev = currentTe.prev.prev;
				currentTe.setPrevious( prevprev );*/
				currentTe.getContig().deleteBefore( currentTe );
			} else {
				/*Tegeval nextnext = currentTe.next.next;
				nextnext.setPrevious( currentTe );*/
				currentTe.getContig().deleteAfter( currentTe );
			}
			c.repaint();
		}
	}
	
	public void deleteBack( JComponent c ) {
		if( currentTe != null ) {
			if( currentTe.getContig().isReverse() ) {
				/*Tegeval nextnext = currentTe.getNext().getNext();
				nextnext.setPrevious( currentTe );*/
				currentTe.getContig().deleteAfter( currentTe );
			} else {
				/*Tegeval prevprev = currentTe.getPrevious().getPrevious();
				currentTe.setPrevious( prevprev );*/
				currentTe.getContig().deleteBefore( currentTe );
			}
			c.repaint();
		}
	}
	
	double rowheight;
	double fsize;
	int total = 0;
	int ptotal = 0;
	public void initContigs( String spec1, GeneSet geneset ) {
		if( spec1 != null && geneset.speccontigMap.containsKey( spec1 ) ) {
			final List<Sequence> lcont = geneset.speccontigMap.get( spec1 );
			
			ptotal = 0;
			total = 0;
			//List<Sequence> contigs = new ArrayList<Sequence>();
			for( Sequence ctg : lcont ) {
				total += ctg.getAnnotationCount();
			}
			
			if( lcont.size() <= 3 ) {
				int max = 0;
				Sequence chromosome = null;
				for( Sequence ctg : lcont ) {
					if( ctg.getAnnotationCount() > max ) {
						max = ctg.getAnnotationCount();
						chromosome = ctg;
					}
				}
				
				ptotal = total - chromosome.getAnnotationCount();
				total = chromosome.getAnnotationCount();
			}
		}
	}
	
	public BufferedImage getImage( GeneSet geneset, int rowheight, int imwidth ) {
		int size = 0;
		for( GeneGroup gg : selectedGenesGroups ) {
			size += gg.genes.size();
		}
		BufferedImage bimg = new BufferedImage(imwidth, size*rowheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bimg.createGraphics();
		
		Rectangle clip = new Rectangle(0,0,imwidth,size*rowheight);
		makeStuff(g2, clip, geneset, rowheight, size, "Default names", true );
		
		g2.dispose();
		return bimg;
	}
	
	public void setZoomLevel( double level ) {
		neighbourscale = level;
	}
	
	public void makeStuff( Graphics g, Rectangle clip, GeneSet geneset, int rowheight, int rowcount, String showNames, boolean seqView ) {
		Map<String,Integer>	offsetMap = new HashMap<String,Integer>();
		for( GeneGroup gg : selectedGenesGroups ) {
			for( String spec2 : gg.getSpecies() ) {
				final Collection<Sequence> contigs2 = geneset.speccontigMap.get( spec2 );
				if( contigs2 != null ) {
					Teginfo gene2s = gg.getGenes( spec2 );
					for( Tegeval tv2 : gene2s.tset ) {
						int count2 = 0;
						for( Sequence ctg2 : contigs2 ) {
							if( ctg2.annset != null ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getAnnotationCount();
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
		
		if( seqView || realView.isSelected() ) {
			//int y = 0;
			for( int i = Math.max(0, clip.y/rowheight); i < Math.min( (clip.y+clip.height)/rowheight+1, rowcount ); i++ ) {
				int r = rowheader.getRowCount() == 0 ? i : rowheader.convertRowIndexToModel( i );
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
						g.drawLine( 0, y*rowheight+8, this.getWidth(), y*rowheight+8 );
						
						Teginfo ti = selectedGene.species.get( species );
						for( Tegeval te : ti.tset ) {*/
			//for( Tegeval te : hteg ) {
				Annotation te = hteg.get(r);
				int xoff = 3000;
				
				if( clip.x+clip.width > xoff ) {
					Annotation next = te;
					//int k = 0;
					while( next != null && xoff <= 5500 && clip.x+clip.width > xoff ) {
						double len = next.getProteinLength()*neighbourscale;
						Gene gene = next.getGene();
						//if( gene != null ) {
						/*String genename = gene.getName();
						if( commonname.isSelected() && (genename == null || genename.contains("_")) ) {
							GeneGroup gg = gene.getGeneGroup();
							if( gg != null ) genename = gg.getName();
						}
						if( genename != null ) genename = genename.contains("hypothetical") ? "hth-p" : genename;*/
						String genename = gene != null ? geneset.getGeneName(showNames, gene) : next.type;
						
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
								int numspec = (gg != null && gg.species != null) ? Math.min( gg.species.size(), 37 ) : 0;
								float abu = numspec/37.0f;
								Color rc = new Color( 0.0f+abu, 1.0f, 0.0f+abu );
								g.setColor( rc );
							} else if( relcol.isSelected() ) {
								if( spec1 != null ) {
									
									//StringBuilder seq = next.seq;
									Color rc = Color.green;
									GeneGroup gg = next.getGene().getGeneGroup();
									List<Tegeval> ltv = gg.getTegevals( spec1 );
									if( ltv != null && ltv.size() > 0 ) {
										rc = GeneCompare.blosumColor( ltv.get(0).getAlignedSequence(), next.getSpecies(), gg, blosumap, false );
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
									List<Tegeval> ltv = null;
									if( gg != null ) {
										ltv = gg.getTegevals( spec1 );
									} else {
										System.err.println();
									}
									if( ltv != null && ltv.size() > 0 ) {
										//String spec2 = next.getSpecies();
										final Collection<Sequence> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
										
										/*double ratio = 0.0;
										double pratio = 0.0;
										if( ptotal > 0 ) {
											if( ctg.getAnnotationCount() == total ) {
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
										double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg, next);
										//rc = GeneCompare.invertedGradientColor( ratio );
										if( ratio == -1 ) {
											ratio = GeneCompare.invertedGradientPlasmidRatio(spec1, contigs, -1.0, gg);
											rc = GeneCompare.gradientGrayscaleColor( ratio );
										} else rc = GeneCompare.gradientColor( ratio );
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
									Annotation thenext = tev.getNext();
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
										Annotation thenext = tev.getPrevious();
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
							
							Sequence ncont = next.getContig();
							boolean revis = (next.ori == -1) ^ (ncont != null && ncont.isReverse());
							int addon = revis ? -5 : 5;
							int offset = revis ? 5 : 0;
							
							int y = i;
							
							if( gene == null || next.ori == 0 ) { //next.type != null && next.type.equals("mummer") ) {
								g.fillRect(xoff+offset, y*rowheight+2, (int)len, rowheight-4);
								g.setColor( next.isSelected() ? Color.black : Color.gray );
								g.drawRect(xoff+offset, y*rowheight+2, (int)len, rowheight-4);
							} else {
								xPoints[0] = xoff+offset; yPoints[0] = y * rowheight+2;
								xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheight+2;
								xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheight+2+(rowheight-4)/2;
								xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheight+2+rowheight-4;
								xPoints[4] = xoff+offset; yPoints[4] = y * rowheight+2+rowheight-4;
								xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheight+2+(rowheight-4)/2;
								g.fillPolygon(xPoints, yPoints, nPoints);
								g.setColor( next.isSelected() ? Color.black : Color.gray );
								g.drawPolygon(xPoints, yPoints, nPoints);
							}
					
							/*int gap = next.unresolvedGap();
							g.setColor( Color.red );
							if( (gap & 1) > 0 ) {
								//g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
								if( !next.getContig().isReverse() ) g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
								else g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
							}
							if( (gap & 2) > 0 ) {
								//g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
								if( !next.getContig().isReverse() ) g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
								else g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
							}*/
							
							Color fc = next.getFrontFlankingGapColor();
							if( fc == Color.red ) {
								g.setColor( fc );
								int val = (int)(rowheight-4)/2;
								if( revis ) {
									g.fillRect(xPoints[0]+1, yPoints[2]-2, val, val);
								} else {
									g.fillRect(xPoints[2]+1, yPoints[2]-2, val, val);
								}
							}
							
							Color bc = next.getBackFlankingGapColor();
							if( bc == Color.red ) {
								g.setColor( bc );
								int val = (int)(rowheight-4)/2;
								if( revis ) {
									g.fillRect(xPoints[2]+1, yPoints[2]-2, val, val);
								} else {
									g.fillRect(xPoints[0]+1, yPoints[2]-2, val, val);
								}
							}
							
							g.setColor( Color.black );
							//g.fillRect(xoff, y * rowheight+2, (int)len, rowheight - 4);
							
							int strlen = g.getFontMetrics().stringWidth( genename );
							while( strlen > len ) {
								genename = genename.substring(0, genename.length()-1);
								strlen = g.getFontMetrics().stringWidth( genename );
							}
							
							if( showNames.length() > 0 /*names.getSelectedIndex() != 0*/ ) {
								if( relcol.isSelected() ) g.setColor( Color.white );
								else g.setColor( Color.black );
								g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheight-(int)(rowheight*0.3) );
							}
						}
						//}
						
						Annotation thenext = next.getNext();
						int bil = 10;
						if( thenext != null && realView.isSelected() ) {
							/*if( next.getGene().getGeneGroup().getName().contains("Elongation") && next.getSpecies().contains("antranik") ) {
								System.err.println();
							}*/
							
							bil = next.getContig().isReverse() ? Math.abs( thenext.stop-next.start ) : Math.abs( thenext.start-next.stop );
							bil = (int)(neighbourscale*bil/3);
						}
						
						if( thenext == null ) {
							Sequence ncont = next.getContig();
							if( ncont != null ) {
								if( ncont.isChromosome() ) {
									thenext = ncont.getFirst();
								} else {
									int k = ncont.partof.indexOf( ncont );
									k = (k+1)%ncont.partof.size();
									Sequence c = ncont.partof.get(k);
									while( c.annset == null || c.annset.size() == 0 ) {
										k = (k+1)%ncont.partof.size();
										c = ncont.partof.get(k);
									}
									thenext = c.getFirst();
									//if( c.isReverse() ) thenext = c.annset.get( c.annset.size()-1 );
									//else thenext = c.annset.get(0);
								}
								
								g.setColor( Color.black );
								g.fillRect(xPoints[2]+5, yPoints[2]-7, 3, rowheight-4);
							}
						}
						
						/*if( thenext != null && thenext.getNext() == next ) {
							thenext = null;
						}*/
						
						xoff += len + bil;
						next = thenext;
						/*if( tev == null ) {
							Sequence nextcontig = next.getContig().next;
							nextcontig.
						} else next = tev;*/
					}
				}
				
				xoff = 3000;
				if( clip.x < xoff ) {
					Annotation prev = te != null ? te.getPrevious() : null;

					int bil = 10;
					if( prev != null && realView.isSelected() ) {
						bil = prev.getContig().isReverse() ? Math.abs( prev.start-te.stop ) : Math.abs( prev.stop-te.start );
						//bil = Math.abs( theprev.stop-prev.start );
						bil = (int)(neighbourscale*bil/3);
						
						//if( prev.getContig().getSpec().contains("2127") ) System.err.println( bil );
						//xoff -= bil;
					}
					
					if( prev == null ) {
						Sequence prevcont = te.getContig();
						if( prevcont != null ) {
							if( prevcont.isChromosome() ) {
								prev = prevcont.getLast();
							} else {
								List<Sequence> partof = prevcont.partof;
								int k = partof.indexOf( prevcont );
								k--;
								if( k < 0 ) k = partof.size()-1;
								Sequence c = partof.get(k);
								while( c.annset == null || c.annset.size() == 0 ) {
									k--;
									if( k < 0 ) k = partof.size()-1;
									c = partof.get(k);
								}
								prev = c.getLast();
							}
							
							int xp = xoff;
							int yp = i * rowheight+2+(rowheight-4)/2;
							
							g.setColor( Color.black );
							g.fillRect(xp-5, yp-7, 3, rowheight-4);
						}
					}
					
					//int k = 0;
					while( prev != null && xoff >= 500 && clip.x < xoff ) {
						double len = prev.getProteinLength()*neighbourscale;
						xoff -= len+bil;
						Annotation theprev = prev.getPrevious();
						bil = 10;
						if( theprev != null && realView.isSelected() ) {
							/*if( prev.getGene().getGeneGroup().getName().contains("Elongation") && prev.getSpecies().contains("antranik") ) {
								System.err.println();
							}*/
							
							bil = prev.getContig().isReverse() ? Math.abs( theprev.start-prev.stop ) : Math.abs( theprev.stop-prev.start );
							//bil = Math.abs( theprev.stop-prev.start );
							bil = (int)(neighbourscale*bil/3);
						}
						
						/*if( theprev != null && theprev.getPrevious() == prev ) {
							theprev = null;
						}*/
						
						Gene gene = prev.getGene();
						//if( gene != null ) {
						/*String genename = prev.getGene().getName();
						if( commonname.isSelected() && genename.contains("_") ) {
							GeneGroup gg = prev.getGene().getGeneGroup();
							if( gg != null ) genename = gg.getName();
						}
						genename = (gene != null && genename.contains("hypothetical")) ? "hth-p" : genename;*/
						String genename = gene != null ? geneset.getGeneName(showNames, prev.getGene()) : prev.type;
						
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
									rc = GeneCompare.blosumColor( ltv.get(0).getAlignedSequence(), prev.getSpecies(), gg, blosumap, false );
								} else {
									rc = Color.white;
								}
								if( rc != null ) g.setColor( rc );												
							} else if( sgradcol.isSelected() ) {
								if( spec1 != null ) {													
									//StringBuilder seq = next.seq;
									Color rc = Color.black;
									GeneGroup gg = prev.getGene().getGeneGroup();
									if( gg != null ) {
										List<Tegeval> ltv = gg.getTegevals( spec1 );
										if( ltv != null && ltv.size() > 0 ) {
											final Collection<Sequence> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
											
											double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg, prev);
											//rc = GeneCompare.invertedGradientColor( ratio );
											if( ratio == -1 ) {
												ratio = GeneCompare.invertedGradientPlasmidRatio(spec1, contigs, -1.0, gg);
												rc = GeneCompare.gradientGrayscaleColor( ratio );
											} else rc = GeneCompare.gradientColor( ratio );
										} else {
											rc = Color.white;
										}
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
									Annotation thenext = tev.getNext();
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
									for( Annotation tev : tegevals ) {
										Annotation thenext = tev.getPrevious();
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
							
							boolean revis = (prev.ori == -1) ^ prev.getContig().isReverse();
							int addon = revis ? -5 : 5;
							int offset = revis ? 5 : 0;
							//g.fillRect(xoff, y * rowheight+2, (int)len, rowheight - 4);
							int y = i;
							
							if( gene == null || prev.ori == 0 ) {
								g.fillRect(xoff+offset, y*rowheight+2, (int)len, rowheight-4);
								g.setColor( prev.isSelected() ? Color.black : Color.gray );
								g.drawRect(xoff+offset, y*rowheight+2, (int)len, rowheight-4);
							} else {
								xPoints[0] = xoff+offset; yPoints[0] = y * rowheight+2;
								xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheight+2;
								xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheight+2+(rowheight-4)/2;
								xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheight+2+rowheight-4;
								xPoints[4] = xoff+offset; yPoints[4] = y * rowheight+2+rowheight-4;
								xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheight+2+(rowheight-4)/2;
								g.fillPolygon(xPoints, yPoints, nPoints);
								g.setColor( prev.isSelected() ? Color.black : Color.gray );
								g.drawPolygon(xPoints, yPoints, nPoints);
							}
							
							/*int gap = prev.unresolvedGap();
							g.setColor( Color.red );
							if( (gap & 1) > 0 ) {
								//g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
								if( !prev.getContig().isReverse() ) g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
								else g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
							}
							if( (gap & 2) > 0 ) {
								//g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
								if( !prev.getContig().isReverse() ) g.fillRect(xPoints[2]+1, yPoints[2]-2, 5, 5);
								else g.fillRect(xPoints[0]-4, yPoints[2]-2, 5, 5);
							}*/
							
							Color fc = prev.getFrontFlankingGapColor();
							if( fc != Color.lightGray ) {
								g.setColor( fc );
								int val = (int)(rowheight-4)/2;
								if( revis ) {
									g.fillRect(xPoints[0]+1, yPoints[2]-2, val, val);
								} else {
									g.fillRect(xPoints[2]+1, yPoints[2]-2, val, val);
								}
							}
							
							Color bc = prev.getBackFlankingGapColor();
							if( bc != Color.lightGray ) {
								g.setColor( bc );
								int val = (int)(rowheight-4)/2;
								if( revis ) {
									g.fillRect(xPoints[2]+1, yPoints[2]-2, val, val);
								} else {
									g.fillRect(xPoints[0]+1, yPoints[2]-2, val, val);
								}
							}
							
							g.setColor( Color.black );
							
							int strlen = g.getFontMetrics().stringWidth( genename );
							while( strlen > len ) {
								genename = genename.substring(0, genename.length()-1);
								strlen = g.getFontMetrics().stringWidth( genename );
							}
							
							if( showNames.length() > 0 /*names.getSelectedIndex() != 0*/ ) {
								if( relcol.isSelected() ) g.setColor( Color.white );
								else g.setColor( Color.black );
								g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheight-(int)(rowheight*0.3) );
							}
						}
						//}
						
						if( theprev == null ) {
							Sequence prevcont = prev.getContig();
							if( prevcont.isChromosome() ) {
								theprev = prevcont.getLast();
							} else {
								List<Sequence> partof = prevcont.partof;
								int k = partof.indexOf( prevcont );
								k--;
								if( k < 0 ) k = partof.size()-1;
								Sequence c = partof.get(k);
								while( c.annset == null || c.annset.size() == 0 ) {
									k--;
									if( k < 0 ) k = partof.size()-1;
									c = partof.get(k);
								}
								theprev = c.getLast();
							}
							
							g.setColor( Color.black );
							g.fillRect(xPoints[0]-5, yPoints[2]-7, 3, rowheight-4);
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
			
			List<Annotation>	hteglocal = new ArrayList<Annotation>( hteg );
			int xoff =  3000;
			while( xoff < 5500 ) {
				int max = 0;
				for( Annotation tes : hteglocal ) {
					//if( te != null && te.getProteinLength() > max ) max = (int)(te.getProteinLength()*neighbourscale);
					int val = 0;
					if( tes != null ) val = (int)(tes.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				
				for( int i = Math.max(0, clip.y/rowheight); i < Math.min( (clip.y+clip.height)/rowheight+1, rowcount ); i++ ) {
					int r = rowheader.getRowCount() == 0 ? i : rowheader.convertRowIndexToModel( i );
					Annotation te = hteglocal.get(r);
					
					//int y = 0;
					//for( Tegeval te : hteglocal ) {
					//g.setColor( Color.black );
					//g.drawLine( 0, y*rowheight+8, this.getWidth(), y*rowheight+8 );
					
					if( te != null ) {
						Annotation next = te;
						if( te.getGene() != null ) {
							String genename = geneset.getGeneName(showNames, next.getGene());
							/*if( names.getSelectedItem().equals("Default names") ) {
								genename = next.getGene().getName();
								//if( commonname.isSelected() && genename.contains("_") ) genename = next.getGene().getGeneGroup().getName();
								genename = genename.contains("hypothetical") ? "hth-p" : genename;
							} else if( names.getSelectedItem().equals("Group names") ) {
								genename = next.getGene().getName();
								if( genename.contains("_") ) genename = next.getGene().getGeneGroup().getName();
								genename = genename.contains("hypothetical") ? "hth-p" : genename;
							} else if( names.getSelectedItem().equals("Cog") ) {
								genename = next.getGene().getGeneGroup().getCommonCog(geneset.cogmap).id;
							} else if( names.getSelectedItem().equals("Cazy") ) {
								genename = next.getGene().getGeneGroup().getCommonCazy(geneset.cazymap);
							}*/
							
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
										rc = GeneCompare.blosumColor( ltv.get(0).getAlignedSequence(), next.getSpecies(), gg, blosumap, false );
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
											final Collection<Sequence> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
											
											double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg, next);
											//rc = GeneCompare.invertedGradientColor( ratio );
											if( ratio == -1 ) {
												ratio = GeneCompare.invertedGradientPlasmidRatio(spec1, contigs, -1.0, gg);
												rc = GeneCompare.gradientGrayscaleColor( ratio );
											} else rc = GeneCompare.gradientColor( ratio );
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
									for( Annotation tev : tegevals ) {
										Annotation thenext = tev.getNext();
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
										for( Annotation tev : tegevals ) {
											Annotation thenext = tev.getPrevious();
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
								
								boolean revis = (next.ori == -1) ^ next.getContig().isReverse();
								int addon = revis ? -5 : 5;
								int offset = revis ? 5 : 0;
								//g.fillRect(xoff, y * rowheight+2, (int)len, rowheight - 4);
								int y = i;
								xPoints[0] = xoff+offset; yPoints[0] = y * rowheight+2;
								xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheight+2;
								xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheight+2+(rowheight-4)/2;
								xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheight+2+rowheight-4;
								xPoints[4] = xoff+offset; yPoints[4] = y * rowheight+2+rowheight-4;
								xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheight+2+(rowheight-4)/2;
								g.fillPolygon(xPoints, yPoints, nPoints);
								g.setColor( next.isSelected() ? Color.black : Color.gray );
								g.drawPolygon(xPoints, yPoints, nPoints);
								g.setColor( Color.black );
								
								int strlen = g.getFontMetrics().stringWidth( genename );
								while( strlen > len ) {
									genename = genename.substring(0, genename.length()-1);
									strlen = g.getFontMetrics().stringWidth( genename );
								}
								
								if( showNames.length() > 0 /*names.getSelectedIndex() != 0*/ ) {
									if( relcol.isSelected() ) g.setColor( Color.white );
									else g.setColor( Color.black );
									g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheight-(int)(rowheight*0.3) );
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
						//g.fillRect(xoff, y * rowheight+2, (int)len, rowheight - 4);
						/*xPoints[0] = xoff; yPoints[0] = y * rowheight+2;
						xPoints[1] = xoff+(int)len; yPoints[1] = y * rowheight+2;
						xPoints[2] = xoff+(int)len+5; yPoints[2] = y * rowheight+2+6;
						xPoints[3] = xoff+(int)len; yPoints[3] = y * rowheight+2+rowheight-4;
						xPoints[4] = xoff; yPoints[4] = y * rowheight+2+rowheight-4;
						xPoints[5] = xoff+5; yPoints[5] = y * rowheight+2+6;
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
						g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheight-5 );*/
						}
					}
					//y++;
				}
				xoff += max+10;
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Annotation te = hteglocal.get(i);
					if( te != null ) {
						Annotation thenext = te.getNext();
						if( thenext == null ) {
							Sequence cont = te.getContig();
							int k = cont.partof.indexOf( cont );
							k = (k+1)%cont.partof.size();
							Sequence c = cont.partof.get(k);
							while( c.annset == null || c.annset.size() == 0 ) {
								k = (k+1)%cont.partof.size();
								c = cont.partof.get(k);
							}
							thenext = c.getFirst();
							//if( c.isReverse() ) thenext = c.annset.get( c.annset.size()-1 );
							//else thenext = c.annset.get(0);
							
							int r = rowheader.getRowCount() == 0 ? i : rowheader.convertRowIndexToView( i );
							g.setColor( Color.black );
							g.fillRect(xoff-10, r*rowheight+2, 3, rowheight-4);
						}
						/*if( thenext != null && thenext.getNext() == te ) {
							thenext = null;
						}*/
						/*System.err.println( "next " + thenext.name + "  " + te.name + "  " + te.start + "  " + thenext.start );
						if( te.start == 0 ) {
							System.err.println();
						}*/
						hteglocal.set(i, thenext);
					}
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
			
			hteglocal.clear();
			hteglocal.addAll( hteg );
			for( int i = 0; i < hteglocal.size(); i++ ) {
				Annotation te = hteglocal.get(i);
				if( te != null ) {
					hteglocal.set(i, te.getPrevious() );
				}
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
				for( Annotation te : hteglocal ) {
					int val = 0;
					if( te != null ) val = (int)(te.getProteinLength()*neighbourscale);
					if( val > max ) max = val;
				}
				
				xoff -= max+10;
				for( int i = Math.max(0, clip.y/rowheight); i < Math.min( (clip.y+clip.height)/rowheight+1, rowcount ); i++ ) {
					int r = rowheader.getRowCount() == 0 ? i : rowheader.convertRowIndexToModel( i );
					Annotation te = hteglocal.get(r);
					//g.setColor( Color.black );
					//g.drawLine( 0, y*rowheight+8, this.getWidth(), y*rowheight+8 );
					
					if( te != null ) {
						Annotation prev = te;
						if( te.getGene() != null ) {
							String genename = geneset.getGeneName( showNames, prev.getGene() );
							/*String genename = prev.getGene().getName();
							if( commonname.isSelected() && genename.contains("_") ) genename = prev.getGene().getGeneGroup().getName();
							genename = genename.contains("hypothetical") ? "hth-p" : genename;*/
							
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
							//g.fillRect(xoff, y * rowheight+2, len, rowheight - 4);
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
										final Collection<Sequence> contigs = /*spec1.equals(spec2) ? contigs :*/geneset.speccontigMap.get( spec1 );
										double ratio = GeneCompare.invertedGradientRatio(spec1, contigs, -1.0, gg, prev);
										if( ratio == -1 ) {
											ratio = GeneCompare.invertedGradientPlasmidRatio(spec1, contigs, -1.0, gg);
											rc = GeneCompare.gradientGrayscaleColor( ratio );
										} else rc = GeneCompare.gradientColor( ratio );
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
									Annotation thenext = tev.getNext();
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
										Annotation thenext = tev.getPrevious();
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
							
							boolean revis = (prev.ori == -1) ^ prev.getContig().isReverse();
							int addon = revis ? -5 : 5;
							int offset = revis ? 5 : 0;
							//g.fillRect(xoff, y * rowheight+2, (int)len, rowheight - 4);
							xPoints[0] = xoff+offset; yPoints[0] = y * rowheight+2;
							xPoints[1] = xoff+offset+(int)len; yPoints[1] = y * rowheight+2;
							xPoints[2] = xoff+offset+(int)len+addon; yPoints[2] = y * rowheight+2+(rowheight-4)/2;
							xPoints[3] = xoff+offset+(int)len; yPoints[3] = y * rowheight+2+rowheight-4;
							xPoints[4] = xoff+offset; yPoints[4] = y * rowheight+2+rowheight-4;
							xPoints[5] = xoff+offset+addon; yPoints[5] = y * rowheight+2+(rowheight-4)/2;
							g.fillPolygon(xPoints, yPoints, nPoints);
							g.setColor( prev.isSelected() ? Color.black : Color.gray );
							g.drawPolygon(xPoints, yPoints, nPoints);
							g.setColor( Color.black );
							
							int strlen = g.getFontMetrics().stringWidth( genename );
							while( strlen > len ) {
								genename = genename.substring(0, genename.length()-1);
								strlen = g.getFontMetrics().stringWidth( genename );
							}
							
							if( showNames.length() > 0 /*names.getSelectedIndex() != 0*/ ) {
								if( relcol.isSelected() ) g.setColor( Color.white );
								g.setColor( Color.black );
								g.drawString( genename, 5+xoff+(int)(len-strlen)/2, (y+1)*rowheight-(int)(rowheight*0.3) );
							}
						}
					}
				}
				
				for( int i = 0; i < hteglocal.size(); i++ ) {
					Annotation te = hteglocal.get(i);
					if( te != null ) {
						Annotation theprev = te.getPrevious();
						
						if( theprev == null ) {
							Sequence prevcont = te.getContig();
							List<Sequence> partof = prevcont.partof;
							int k = partof.indexOf( prevcont );
							k--;
							if( k < 0 ) k = partof.size()-1;
							Sequence c = partof.get(k);
							while( c.annset == null || c.annset.size() == 0 ) {
								k--;
								if( k < 0 ) k = partof.size()-1;
								c = partof.get(k);
							}
							theprev = c.getLast();
							
							int r = rowheader.getRowCount() == 0 ? i : rowheader.convertRowIndexToView( i );
							g.setColor( Color.black );
							g.fillRect(xoff-10, r*rowheight+2, 3, rowheight-4);
						}
						
						/*if( theprev != null && theprev.getPrevious() == te ) {
							theprev = null;
						}*/
						//System.err.println( theprev.name + "  " + te.name + "  " + te.start + "  " + theprev.start );
						hteglocal.set( i, theprev );
					}
					//if( te.getLength() > max ) max = te.getLength();
				}
			}
		}
	}
	
	public void forward() {
		for( Annotation te : hteg ) {
			boolean rev = te.ori == -1 ^ te.getContig().isReverse();
			if( rev ) {
				List<Sequence>	partof = te.getContig().partof;
				for( Sequence ctg : partof ) {
					ctg.setReverse( !ctg.isReverse() );
				}
				Collections.reverse( partof );
			}
		}
	}
	
	final int		nPoints = 6;
	final int[]		xPoints = new int[ nPoints ];
	final int[]		yPoints = new int[ nPoints ];
	
	final JRadioButtonMenuItem funcol = new JRadioButtonMenuItem("Functions");
	final JRadioButtonMenuItem gccol = new JRadioButtonMenuItem("GC%");
	final JRadioButtonMenuItem gcskewcol = new JRadioButtonMenuItem("GC skew");
	final JRadioButtonMenuItem abucol = new JRadioButtonMenuItem("Abundance");
	final JRadioButtonMenuItem relcol = new JRadioButtonMenuItem("Relation");
	final JRadioButtonMenuItem sgradcol = new JRadioButtonMenuItem("Synteny gradient");
	final JRadioButtonMenuItem precol = new JRadioButtonMenuItem("Proximity preservation");
	
	final Map<Set<Function>,Color>	funcMap = new HashMap<Set<Function>,Color>();
	final Random rand = new Random();
	final JTable rowheader = new JTable();
	final Map<String,Integer> blosumap = JavaFasta.getBlosumMap();
	
	final JRadioButton	sequenceView = new JRadioButton("Sequence");
	final JRadioButton	blocksView = new JRadioButton("Blocks");
	final JRadioButton	realView = new JRadioButton("Real");
	
	public final JComboBox<String>			names = new JComboBox<String>();
	JComponent c;
	double neighbourscale = 1.0;
	static Annotation currentTe = null;
	Set<GeneGroup> selectedGenesGroups;
	static List<Annotation>	hteg;
	//static int colorscheme = 0;
	//static List<String>	speclist;
	public void neighbourMynd( final GeneSetHead genesethead, final Container comp, final List<Gene> genes, final Map<String,Sequence> contigmap ) throws IOException {
		GeneSet geneset = genesethead.geneset;
		
		final TableView<GeneGroup> sorting = genesethead.getGeneGroupTable();
		
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
		
		//final JCheckBox		commonname = new JCheckBox("Group names");
		//final JCheckBox		noname = new JCheckBox("No names");
		
		final JButton	smallerRows = new JButton("^");
		final JButton	largerRows = new JButton("v");
		
		mbr.add( seqsmenu );
		mbr.add( mnu );
		mbr.add( mvmnu );
		mbr.add( selmnu );
		
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
		
		final JFrame frame = new JFrame();
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			//hteg = loadContigs( genes, null );
			//hteg.clear();
			
			/*speclist = new ArrayList<String>();
			for( Gene selectedGene : selectedGenes ) {
				for( String species : selectedGene.species.keySet() ) {
					if( !speclist.contains( species ) ) speclist.add( species );
				}
			}*/
			
			//final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			rowheight = rowheader.getRowHeight();
			
			c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();
				Color altcol = Color.black;
				// Color dg = Color.green.darker();

				public String getToolTipText( MouseEvent me ) {
					Point p = me.getPoint();
					Annotation te = getSelectedTe(p, rowheader, sequenceView, realView, hteg, rowheader.getRowHeight());
					if( te != null ) {
						Gene g = te.getGene();
						GeneGroup gg = g != null ? g.getGeneGroup() : null;
						if( gg != null ) return "<html>"+
											//(g.getName().equals( g.refid ) ? gg.getName() : g.getName())+ "<br>" + te.getGene().refid + "<br>" + gg.getFunctions() + "<br>" + te.start + ".." + te.stop
											gg.getName() + "<br>" + te.getGene().refid + "<br>" + gg.getFunctions() + "<br>" + te.start + ".." + te.stop
											+ "</html>";
					}
					return null;
				}
				
				public void paintComponent( Graphics g ) {
					super.paintComponent(g);
					
					g.setFont( g.getFont().deriveFont((float)fsize) );
					
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
					//g.setFont( g.getFont().deriveFont( 8.0f ) );
					
					Rectangle clip = this.getVisibleRect(); //g.getClipBounds();
					makeStuff( g, clip, geneset, rowheader.getRowHeight(), rowheader.getRowCount(), names.getSelectedItem().toString(), sequenceView.isSelected() );
					
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
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
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
			fsize = 8.0;//c.getFont().getSize();
			
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
			
			names.addItemListener( new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					c.repaint();
				}
			});
			
			//commonname.setAction( a );
			//noname.setAction( a );
			
			funcol.setText("Functions");
			gccol.setText("GC%");
			gcskewcol.setText("GC skew");
			abucol.setText("Abundance");
			precol.setText("Proximity preservation");
			
			//commonname.setText("Group names");
			//noname.setText( "No names" );
			
			names.addItem("No names");
			names.addItem("Default names");
			names.addItem("Group names");
			names.addItem("Refids");
			names.addItem("Ids");
			names.addItem("Cog");
			names.addItem("Cazy");
			
			smallerRows.addActionListener( new AbstractAction("^") {
				@Override
				public void actionPerformed(ActionEvent e) {
					rowheight *= 0.8;
					rowheader.setRowHeight( (int)rowheight );
					fsize *= 0.8;
					c.setFont( c.getFont().deriveFont((int)fsize) );
					c.repaint();
				}
			});
			
			largerRows.addActionListener( new AbstractAction("v") {
				@Override
				public void actionPerformed(ActionEvent e) {
					rowheight *= 1.25;
					rowheader.setRowHeight( (int)rowheight );
					fsize *=1.25;
					c.setFont( c.getFont().deriveFont((int)fsize) );
					c.repaint();
				}
			});
			
			turn.setAction( new AbstractAction("Forward") {
				@Override
				public void actionPerformed(ActionEvent e) {
					//System.err.println("hteg " + hteg.size());
					forward();
					c.repaint();
				}
			});
			
			mvmnu.add( new AbstractAction("Connect contig")  {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Sequence> contigs = genesethead.getSelspecContigs(null, geneset.speccontigMap, currentTe.getSpecies());
					if( contigs.size() > 0 ) {
						Sequence sctg = contigs.get(0);
						
						Sequence ctg = currentTe.getContig();
						int i = ctg.partof.indexOf( ctg );
						int k = ctg.annset.indexOf( currentTe );
						
						if( k == 0 || ctg.isReverse() ) {
							ctg.partof.remove( sctg );
							ctg.partof.add(i, sctg);
						} else if( k == ctg.annset.size()-1 || !ctg.isReverse() ) {
							ctg.partof.remove( sctg );
							ctg.partof.add(i+1, sctg);
						}
					}
				}
			});
			mvmnu.add( new AbstractAction("Reverse contig")  {
				@Override
				public void actionPerformed(ActionEvent e) {
					Sequence ctg = currentTe.getContig();
					ctg.setReverse( !ctg.isReverse() );
				}
			});
			mvmnu.addSeparator();
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
					Map<String,Sequence>  ctind = new HashMap<String,Sequence>();
					
					for( GeneGroup gg : selectedGenesGroups ) {
						for( String sp : gg.species.keySet() ) {
							Teginfo ti = gg.species.get( sp );
							
							Sequence ct = ti.best.getContig();
							ctind.put( sp, ct );
							spind.put( sp, ct.annset.indexOf( ti.best ) );
						}
					}
					
					for( int i = 1; i < 20; i++ ) {
						for( String spec : ctind.keySet() ) {
							Sequence ct = ctind.get( spec );
							//ct.annset.
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
							for( Annotation ann : tv.getContig().annset ) {
								ann.selected = false;
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
							int k = tv.getContig().annset.indexOf( tv );
							for( int i = k+1; i < tv.getContig().annset.size(); i++ ) {
								Tegeval tv2 = (Tegeval)tv.getContig().annset.get(i);
								currentTe = tv2;
								if( tv2.isDirty() || tv2.backgap || tv2.frontgap ) {
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
							int k = tv.getContig().annset.indexOf( tv );
							for( int i = k-1; i >= 0; i-- ) {
								Tegeval tv2 = (Tegeval)tv.getContig().annset.get(i);
								currentTe = tv2;
								if( tv2.isDirty() || tv2.backgap || tv2.frontgap ) {
									break;
								}
								/*if( tv2.unresolvedGap() > 0 ) {
									break;
								}*/
							}
							recenter( rowheader, c );
						}
					}
					c.repaint();
				}
			});
			selmnu.addSeparator();
			selmnu.add( new AbstractAction("Next scaffold") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int r = rowheader.getSelectedRow();
					String spec = (String)rowheader.getValueAt( r, 0 );
					for( GeneGroup gg : selectedGenesGroups ) {
						Teginfo ti = gg.species.get( spec );
						for( Tegeval tv : ti.tset ) {
							//currentTe = tv.getContig().getLast();
							Sequence cont = tv.getContig();
							int k = cont.partof.indexOf(tv.getContig());
							/*if( cont.isReverse() ) {
								k = (k-1);
								if( k < 0 ) k = tv.getContig().partof.size()-1;
							} else {*/
								k = (k+1)%cont.partof.size();
							//}
							currentTe = cont.partof.get(k).getFirst();
							
							/*for( int i = k+1; i < tv.getContig().annset.size(); i++ ) {
								Tegeval tv2 = tv.getContig().annset.get(i);
								currentTe = tv2;
								if( tv2.isDirty() || tv2.backgap || tv2.frontgap ) {
									break;
								}
							}*/
							recenter( rowheader, c );
							break;
						}
					}
					c.repaint();
				}
			});
			selmnu.add( new AbstractAction("Previous scaffold") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int r = rowheader.getSelectedRow();
					String spec = (String)rowheader.getValueAt( r, 0 );
					for( GeneGroup gg : selectedGenesGroups ) {
						Teginfo ti = gg.species.get( spec );
						for( Tegeval tv : ti.tset ) {
							Sequence cont = tv.getContig();
							int k = cont.partof.indexOf( cont );
							//if( cont.isReverse() ) {
								k = (k-1);
								if( k < 0 ) k = cont.partof.size()-1;
							/*} else {
								k = (k+1)%cont.partof.size();
							}*/
							currentTe = cont.partof.get(k).getLast();
							recenter( rowheader, c );
							break;
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
					Annotation te = hteg.get( i );
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
					Annotation te = hteg.get( i );
					currentTe = te.getPrevious() == null ? te : te.getPrevious();
					recenter( rowheader, c );
				}
			});
			forw.setAction( new AbstractAction(">") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Annotation te = hteg.get( i );
					currentTe = te.getNext() == null ? te : te.getNext();
					recenter( rowheader, c );
					
				}
			});
			forwTen.setAction( new AbstractAction(">>") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int r = rowheader.getSelectedRow();
					int i = rowheader.convertRowIndexToModel( r );
					Annotation te = hteg.get( i );
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
							Sequence ct = te.getContig();
							for( Annotation ste : hteg ) {
								if( ste.getContig() == ct ) {
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
			final JMenuItem	showareaseqs = new JMenuItem("Show area");
			showseqs.setAction( new AbstractAction("Sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					genesethead.showSequences( comp, selectedGenesGroups, false, null );
				}
			});
			showdnaseqs.setAction( new AbstractAction("DNA sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					genesethead.showSequences( comp, selectedGenesGroups, true, null );
				}
			});
			showselectedseqs.setAction( new AbstractAction("Selected sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Annotation>	tset = new HashSet<Annotation>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							for( Annotation ann : tv.getContig().annset ) {
								if( ann.isSelected() ) tset.add( ann );
							}
						}
					}
					genesethead.showSelectedSequences( comp, tset, false, names.getSelectedItem().toString() );
				}
			});
			showselecteddnaseqs.setAction( new AbstractAction("Selected DNA sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<Annotation>	tset = new HashSet<Annotation>();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							for( Annotation ann : tv.getContig().annset ) {
								if( ann.isSelected() ) tset.add( ann );
							}
						}
					}
					genesethead.showSelectedSequences( comp, tset, true, names.getSelectedItem().toString() );
				}
			});
			showflankingseqs.setAction( new AbstractAction("Show flanking sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Serifier serifier = new Serifier();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							Tegeval prev = null;
							//Tegeval prevprev = null;
							for( Annotation ann : tv.getContig().annset ) {
								Tegeval tv2 = (Tegeval)ann;
								/*if( tv2.getSpecies().contains("antra") && tv2.getGene().getGeneGroup().getName().contains("Elongation") ) {
									System.err.println();
								}*/
								
								if( prev != null && prev.isSelected() && prev.ori == 1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContig().length() ) {
										Sequence seq = new Sequence( prev.getGene().getGeneGroup().getName(), null );
										seq.append( prev.getContig().sb.substring(start, stop) );
										serifier.addSequence( seq );
									}
								} else if( tv2 != null && tv2.isSelected() && tv2.ori == -1 ) {
									int start = prev != null ? prev.stop : 0;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContig().length() ) {
										Sequence seq = new Sequence( tv2.getGene().getGeneGroup().getName(), null );
										seq.append( tv2.getContig().sb.substring(start, stop) );
										serifier.addSequence( seq );
									}
								}
								
								//prevprev = prev;
								prev = tv2;
							}
						}
					}
					genesethead.showSomeSequences( comp, serifier );
				}
			});
			showbackflankingseqs.setAction( new AbstractAction("Show back flanking sequences") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Serifier serifier = new Serifier();
					for( GeneGroup gg : selectedGenesGroups ) {
						List<Tegeval> ltv = gg.getTegevals();
						for( Tegeval tv : ltv ) {
							Tegeval prev = null;
							//Tegeval prevprev = null;
							for( Annotation ann : tv.getContig().annset ) {
								Tegeval tv2 = (Tegeval)ann;
								/*if( tv2.getSpecies().contains("antra") && tv2.getGene().getGeneGroup().getName().contains("Elongation") ) {
									System.err.println();
								}*/
								
								if( prev != null && prev.isSelected() && prev.ori == -1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContig().length() ) {
										Sequence seq = new Sequence( prev.getGene().getGeneGroup().getName(), null );
										seq.append( prev.getContig().sb.substring(start, stop) );
										serifier.addSequence( seq );
									}
								} else if( tv2 != null && tv2.isSelected() && tv2.ori == 1 ) {
									int start = prev.stop;
									int stop = tv2.start;
									
									if( stop > start && start >= 0 && stop < tv2.getContig().length() ) {
										Sequence seq = new Sequence( tv2.getGene().getGeneGroup().getName(), null );
										seq.append( tv2.getContig().sb.substring(start, stop) );
										serifier.addSequence( seq );
									}
								}
								
								//prevprev = prev;
								prev = tv2;
							}
						}
					}
					genesethead.showSomeSequences( comp, serifier );
				}
			});
			showareaseqs.setAction( new AbstractAction("Show area") {
				@Override
				public void actionPerformed(ActionEvent e) {
					Serifier serifier = new Serifier();
					List<Sequence> lseq = new ArrayList<Sequence>();
					int[] rr = rowheader.getSelectedRows();
					
					int upph = -8000;
					int endh = 16000;
					
					Color cg = new Color(100,150,100);
					Color cdg = new Color(50,100,50);
					Color cr = new Color(150,100,100);
					Color cdr = new Color(100,50,50);
					Color cb = new Color(100,100,150);
					Color cdb = new Color(50,50,100);
					
					Set<Color> darkColors = new HashSet<Color>( Arrays.asList(new Color[] {Color.gray,cdg,cdr,cdb}) );
					Set<Color> colors = new HashSet<Color>( Arrays.asList(new Color[] {Color.lightGray,cg,cr,cb}) );
					
					if( rr == null || rr.length == 0 ) {
						for( GeneGroup gg : selectedGenesGroups ) {
							List<Tegeval> ltv = gg.getTegevals();
							for( Tegeval tv : ltv ) {
								String seqname = Sequence.nameFix(tv.getSpecies(),true);
								boolean aq = false;
								System.err.println( seqname );
							
								if( seqname.contains("calidit") ) {
									aq = true;
								}
								
								int uh = tv.ori != -1 ? upph : -endh;
								int eh = tv.ori != -1 ? endh : -upph;
								//int start = Math.max( 0, tv.start-3000 );
								//int stop = Math.min( tv.getContig().sb.length(), tv.stop+3000 );
								Sequence seq = new Sequence( seqname+(tv.ori == 1 ? "->" : "<-"), null );
								String str = tv.ori == -1 ? tv.getPaddedSubstring(-endh-tv.start+tv.stop-1, -upph-tv.start+tv.stop-1) : tv.getPaddedSubstring(upph, endh);
								seq.append( str ); //tv.getLength()+3000) );
								
								int offset = 0; //tv.ori == -1 ? tv.getSubstringOffset(-endh-tv.start+tv.stop-1, -upph-tv.start+tv.stop-1) : tv.getSubstringOffset(upph, endh);;
								/*if( str.length() < endh-upph ) {
									offset = endh-upph-str.length();
									offset = -offset;
								}*/
								
								//if( tv.ori == -1 ) {
									Gene tgene = tv.getGene();
									String symb = null;
									String name = null;
									if( tv.id == null || !tv.id.equals(tv.name) ) {
										if( tgene != null ) {
											symb = tgene.getGeneGroup().getSymbol();
											if( symb == null || (!symb.contains(",") && symb.length() > 4) ) {
												symb = null;
												Cog cog = tgene.getGeneGroup().getCog(geneset.cogmap);
												if( cog != null ) {
													symb = cog.genesymbol;
												}
											}
										}
										name = symb != null ? symb : tv.gene == null ? tv.name : tv.gene.getGeneGroup().getName();
									}
									if( name != null && name.contains("hypo") ) name = "hyp";
									//name += tv.ori == 1 ? "->" : "<-";
									
									Annotation newann = new Annotation(seq, -upph+offset, -upph+offset+tv.stop-tv.start, 1, name);
									newann.group = tv.gene != null && tv.gene.getGeneGroup() != null ? Integer.toString(tv.gene.getGeneGroup().index) : "";
									newann.designation = tv.designation;
									Color color = tv.type != null && tv.type.contains("mummer") ? cdb : tv.isPhage() ? cdg : tv.seq.isPlasmid() ? cdr : Color.gray;
									newann.color = color;
									seq.addAnnotation( newann );
									serifier.addAnnotation(newann);
									
									// = tv.type != null && tv.type.contains("mummer") ? cb : tv.isPhage() ? cg : tv.seq.isPlasmid() ? cr : Color.lightGray;
									Annotation ntev = tv.getNext();
									int bil = ntev != null ? (tv.ori == -1 ? ntev.stop-tv.start : ntev.start-tv.start) : -1;
									//int bil2 = tv.ori != -1 ? ntev.stop-tv.start : ntev.start-tv.start;
									while( ntev != null && bil < eh && bil > uh ) {
										if( aq ) System.err.println( bil + " nxt " + (ntev.getGene() != null ? ntev.getGene().getGeneGroup().getName() : "") );
										
										//name = ntev.gene == null ? ntev.name : ntev.gene.getGeneGroup().getName();
										//name += ntev.ori == 1 ? "->" : "<-";
										name = null;
										symb = null;
										tgene = ntev.getGene();
										if( ntev.id == null || !ntev.id.equals(ntev.name) ) {
											if( tgene != null ) {
												symb = tgene.getGeneGroup().getSymbol();
												if( symb == null || (!symb.contains(",") && symb.length() > 4) ) {
													symb = null;
													Cog cog = tgene.getGeneGroup().getCog(geneset.cogmap);
													if( cog != null ) {
														symb = cog.genesymbol;
													}
												}
											}
											name = symb != null ? symb : ntev.gene == null ? ntev.name : ntev.gene.getGeneGroup().getName();
										}
										if( name != null && name.contains("hypo") ) name = "hyp";
										
										if( tv.ori == -1 ) {
											//int bil = ntev.stop-tv.start;
											int len = tv.stop-tv.start;
											
											int start = -upph+offset+len-bil;
											int nlen = ntev.stop-ntev.start;
											newann = new Annotation(seq, start, start+nlen, 1, name);
										} else {
											//int bil = ntev.start-tv.start;
											newann = new Annotation(seq, -upph+offset+bil, -upph+offset+bil+ntev.stop-ntev.start, 1, name);
										}
										
										if( colors.contains(color) ) color = ntev.type != null && ntev.type.contains("mummer") ? cdb : ntev.isPhage() ? cdg : ntev.seq.isPlasmid() ? cdr : Color.gray;
										else color = ntev.type != null && ntev.type.contains("mummer") ? cb : ntev.isPhage() ? cg : ntev.seq.isPlasmid() ? cr : Color.lightGray;
										
										newann.group = ntev.gene != null && ntev.gene.getGeneGroup() != null ? Integer.toString(ntev.gene.getGeneGroup().index) : "";
										newann.color = color;
										seq.addAnnotation( newann );
										serifier.addAnnotation(newann);
										
										ntev = ntev.getNext();
										
										if( ntev != null ) {
											bil = tv.ori == -1 ? ntev.stop-tv.start : ntev.start-tv.start;
											//bil2 = tv.ori != -1 ? ntev.stop-tv.start : ntev.start-tv.start;
											
											if( aq ) {
												System.err.println( "nxt2 " + (ntev.getGene() != null ? ntev.getGene().getGeneGroup().getName() : "") );
												System.err.println( bil + "  " + "  " + uh + "  " + eh );
											}
										}
									}
									
									color = tv.type != null && tv.type.contains("mummer") ? cdb : tv.isPhage() ? cdg : tv.seq.isPlasmid() ? cdr : Color.darkGray;;
									ntev = tv.getPrevious();
									bil = ntev != null ? (tv.ori == -1 ? ntev.start-tv.stop : ntev.start-tv.start) : -1;
									int bbil = ntev != null ? (tv.ori == -1 ? ntev.stop-tv.start : ntev.start-tv.start) : -1;
									//bil2 = tv.ori != -1 ? ntev.stop-tv.start : ntev.start-tv.start;
									while( ntev != null && bil < eh && bil > uh ) {
										if( aq ) System.err.println( bil + "prv " + (ntev.getGene() != null ? ntev.getGene().getGeneGroup().getName() : "") );
										
										//name = ntev.gene == null ? ntev.name : ntev.gene.getGeneGroup().getName();
										//name += ntev.ori == 1 ? "->" : "<-";
										
										name = null;
										symb = null;
										tgene = ntev.getGene();
										if( ntev.id == null || !ntev.id.equals(ntev.name) ) {
											if( tgene != null ) {
												symb = tgene.getGeneGroup().getSymbol();
												if( symb == null || (!symb.contains(",") && symb.length() > 4) ) {
													symb = null;
													Cog cog = tgene.getGeneGroup().getCog(geneset.cogmap);
													if( cog != null ) {
														symb = cog.genesymbol;
													}
												}
											}
											name = symb != null ? symb : ntev.gene == null ? ntev.name : ntev.gene.getGeneGroup().getName();
										}
										if( name != null && name.contains("hypo") ) name = "hyp";
										
										if( tv.ori == -1 ) {
											//int bil = ntev.stop-tv.start;
											int len = tv.stop-tv.start;
											int start = -upph+offset+len-bbil;
											int nlen = ntev.stop-ntev.start;
											newann = new Annotation(seq, start, start+nlen, 1, name);
										} else {
											//int bil = ntev.start-tv.start;
											newann = new Annotation(seq, -upph+offset+bbil, -upph+offset+bbil+ntev.stop-ntev.start, 1, name);
										}
										
										if( colors.contains(color) ) color = ntev.type != null && ntev.type.contains("mummer") ? cdb : ntev.isPhage() ? cdg : ntev.seq.isPlasmid() ? cdr : Color.gray;
										else color = ntev.type != null && ntev.type.contains("mummer") ? cb : ntev.isPhage() ? cg : ntev.seq.isPlasmid() ? cr : Color.lightGray;
										
										newann.group = ntev.gene != null && ntev.gene.getGeneGroup() != null ? Integer.toString(ntev.gene.getGeneGroup().index) : "";
										newann.color = color;
										seq.addAnnotation( newann );
										serifier.addAnnotation(newann);
										
										ntev = ntev.getPrevious();
										
										if( ntev != null ) {
											bil = tv.ori == -1 ? ntev.start-tv.stop : ntev.start-tv.start;
											bbil = tv.ori == -1 ? ntev.stop-tv.start : ntev.start-tv.start;
											if( aq ) System.err.println( bil + "prv " + (ntev.getGene() != null ? ntev.getGene().getGeneGroup().getName() : "") + ntev.start + "  " + ntev.stop + "  " + tv.start );
											
											//bil2 = tv.ori != -1 ? ntev.stop-tv.start : ntev.start-tv.start;
										}
									}
								/*} else {
									Annotation newann = new Annotation(seq, 3000, 3000+tv.stop-tv.start, 1, tv.name);
									seq.addAnnotation( newann );
									serifier.addAnnotation(newann);
									
									Tegeval ntev = tv.getNext();
									while( ntev != null && ntev.start-tv.start < 5000 && ntev.stop-tv.start > -3000 ) {
										int bil = ntev.start-tv.start;
										newann = new Annotation(seq, 3000+bil, 3000+bil+ntev.stop-ntev.start, 1, ntev.name);
										seq.addAnnotation( newann );
										serifier.addAnnotation(newann);
										
										ntev = ntev.getNext();
									}
									
									ntev = tv.getPrevious();
									while( ntev != null && ntev.start-tv.start < 5000 && ntev.stop-tv.start > -3000 ) {
										int bil = ntev.start-tv.start;
										newann = new Annotation(seq, 3000+bil, 3000+bil+ntev.stop-ntev.start, 1, ntev.name);
										seq.addAnnotation( newann );
										serifier.addAnnotation(newann);
										
										ntev = ntev.getPrevious();
									}
								}*/
								serifier.addSequence( seq );
							}
						}
					} else {
						for( int r : rr ) {
							int i = rowheader.convertRowIndexToModel(r);
							Annotation tv = hteg.get(i);
							Sequence seq = new Sequence( ((Tegeval)tv).getSpecies(), null );
							seq.append( tv.getSubstring(-3000, tv.getLength()+3000) );
							serifier.addSequence( seq );
						}
					}
					
					for( Sequence seq : serifier.lseq ) {
						if( seq.getAnnotations() != null ) {
							Collections.sort( seq.getAnnotations() );
						}
					}
					genesethead.showSomeSequences( comp, serifier );
				}
			});
			
			seqsmenu.add( showseqs );
			seqsmenu.add( showdnaseqs );
			seqsmenu.addSeparator();
			seqsmenu.add( showselectedseqs );
			seqsmenu.add( showselecteddnaseqs );
			seqsmenu.add( showflankingseqs );
			seqsmenu.add( showbackflankingseqs );
			seqsmenu.addSeparator();
			seqsmenu.add( showareaseqs );
			
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
					
					Annotation te = getSelectedTe( p, rowheader, sequenceView, realView, hteg, rowheader.getRowHeight() );
					//System.err.println();
					if( te != null ) {
						if( me.getClickCount() == 2 ) {
							c.setToolTipText( "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+"<br>"+ te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>" );
							//c.sett
						} else {
							te.setSelected( !te.isSelected() );
							if( !genesethead.isGeneview() ) {
								genesethead.getGeneGroupTable().getSelectionModel().select( te.getGene().getGeneGroup() );
							} else {
								genesethead.getGeneTable().getSelectionModel().select( te.getGene() );
							}
							c.repaint();
						}
					}
				}

				public void mouseReleased(MouseEvent me) {
					Point np = me.getPoint();

					if (p != null && np.x > p.x) {
						//Rectangle rect = sorting.getCellRect(p.x, 0, false);
						//rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						
						sorting.scrollTo(0); /*object*/
						
						//sorting.scrollRectToVisible(rect);
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
						Annotation te = hteg.get( i );
						Sequence contig = te.getContig();
						contig.setReverse( !contig.isReverse() );
						
						Sequence nextc = contig.next;
						while( nextc != null ) {
							nextc.setReverse( !nextc.isReverse() );
							nextc = nextc.next;
						}
						
						Sequence prevc = contig.prev;
						while( prevc != null ) {
							prevc.setReverse( !prevc.isReverse() );
							prevc = prevc.prev;
						}
						
						/*for( Gene selectedGene : selectedGenes ) {
							String spec = (String)rowheader.getValueAt(r, 0);
							if( selectedGene.species.containsKey( spec ) ) {
								Teginfo ti = selectedGene.species.get( spec );
								for( Tegeval te : ti.tset ) {
									te.getContig().setReverse( !te.getContig().isReverse() );
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
					Annotation te = hteg.get( i );
					Sequence 	cont = te.getContig();
					String		spec = cont.getSpec();
					
					final List<Sequence>	specont = new ArrayList<Sequence>();
					for( String name : contigmap.keySet() ) {
						Sequence c = contigmap.get( name );
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
							return "Sequence";
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
							Annotation con = cont.getEndAnnotation().getNext();
							while( con != null ) {
								cont = (Sequence)con.getContig();
								con = cont.isReverse() ? cont.getStartAnnotation().getPrevious() : cont.getEndAnnotation().getNext();
								
								if( con != null && con.getContig().equals( cont ) ) {
									break;
								}
							}
						} else {
							Annotation con = cont.getStartAnnotation().getPrevious();
							while( con != null ) {
								cont = (Sequence)con.getContig();
								con = cont.isReverse() ? cont.getStartAnnotation().getPrevious() : cont.getEndAnnotation().getNext();
								
								if( con != null && con.getContig().equals( cont ) ) {
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
					Set<Annotation>	ste = new HashSet<Annotation>();
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
						Annotation te = hteg.get( i );
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
						Annotation te = hteg.get( i );
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
			rowheader.setDragEnabled( true );
			rowheader.addKeyListener( new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyPressed(KeyEvent e) {
					if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
						Set<Annotation>	ste = new HashSet<Annotation>();
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
					if( columnIndex == 1 ) return "Sequence";
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
					Annotation te = hteg.get(rowIndex);
					if( columnIndex == 0 ) {
						return geneset.nameFix( te.getSpecies() );
					}
					else if( columnIndex == 1 ) return te.getContig().getName();
					else if( columnIndex == 2 ) return te.getLength();
					else if( columnIndex == 3 ) return te.getContig().isReverse();
					/*for( Gene selectedGene : selectedGenes ) {
						if( selectedGene.species.containsKey( species ) ) {
							Teginfo ti = selectedGene.species.get( species );
							for( Tegeval te : ti.tset ) {
								return te.getContig().getName();
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
			
			DataFlavor tdf = null;
			try {
				tdf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			final DataFlavor ndf = tdf;
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Sequence>	selseq = new ArrayList<Sequence>( rr.length );
						for( int r : rr ) {
							int i = rowheader.convertRowIndexToModel(r);
							selseq.add( hteg.get(i).getAlignedSequence() );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						for( int r = 0; r < rowheader.getRowCount(); r++ ) {
							Object o = rowheader.getValueAt(r, 0);
							if( o != null ) {
								ret += o.toString();
							} else {
								ret += "";
							}
							for( int c = 1; c < rowheader.getColumnCount(); c++ ) {
								o = rowheader.getValueAt(r, c);
								if( o != null ) {
									ret += "\t"+o.toString();
								} else {
									ret += "\t";
								}
							}
							ret += "\n";
						}
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.getBytes( charset ) );
					}
					//return ret;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df, ndf };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) || arg0.equals(ndf) ) {
						return true;
					}
					return false;
				}
			};
			
			TransferHandler th = new TransferHandler() {
				private static final long serialVersionUID = 1L;
				
				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}

				public boolean canImport(TransferHandler.TransferSupport support) {					
					return true;
				}

				protected Transferable createTransferable(JComponent c) {
					currentRowSelection = rowheader.getSelectedRows();
					return transferable;
				}

				public boolean importData(TransferHandler.TransferSupport support) {
					try {
						System.err.println( rowheader.getSelectedRows().length );
						
						if( support.isDataFlavorSupported( ndf ) ) {						
							Object obj = support.getTransferable().getTransferData( ndf );
							List<Annotation>	seqs = (ArrayList<Annotation>)obj;
							
							List<Annotation> newlist = new ArrayList<Annotation>( hteg.size() );
							for( int r = 0; r < rowheader.getRowCount(); r++ ) {
								int i = rowheader.convertRowIndexToModel(r);
								newlist.add( hteg.get(i) );
							}
							hteg.clear();
							hteg = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = rowheader.rowAtPoint( p );
							
							hteg.removeAll( seqs );
							for( Annotation tv : seqs ) {
								hteg.add(k++, tv);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)rowheader.getRowSorter();
							trs.setSortKeys( null );
							
							rowheader.tableChanged( new TableModelEvent( rowheader.getModel()) );
							c.repaint();
							
							return true;
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
			rowheaderscroll.setTransferHandler( th );
			rowheader.setTransferHandler( th );

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
		toolbar.add( names );
		//toolbar.add( noname );
		toolbar.add( smallerRows );
		toolbar.add( largerRows );
		
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
	int[]	currentRowSelection;
}
