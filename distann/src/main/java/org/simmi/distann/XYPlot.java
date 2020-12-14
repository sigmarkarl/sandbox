package org.simmi.distann;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Gene;

import javafx.scene.control.TableView;

public class XYPlot {
	List<Sequence>	spec1Conts = new ArrayList<>();
	List<Sequence>	spec2Conts = new ArrayList<>();
	int fsum1;
	int fsum2;
	String spec1;
	String spec2;
	
	public void initSpecConts( Map<String,List<Sequence>> speccontigMap, String spec1, String spec2 ) {
		//spec1Conts.clear();
		//spec2Conts.clear();
		
		/*for( String ctname : GeneSet.contigmap.keySet() ) {
			Sequence ct = GeneSet.contigmap.get( ctname );
			if( ct.getSpec().equals( spec1 ) ) {
				spec1Conts.add( ct );
			}
			
			if( ct.getSpec().equals( spec2 ) ) {
				spec2Conts.add( ct );
			}
		}
		
		System.err.println( spec1Conts.size() + "  " + spec2Conts.size() );*/
		spec1Conts = speccontigMap.get( spec1 );
		spec2Conts = speccontigMap.get( spec2 );
		
		int sum1 = 0;
		for( Sequence ct : spec1Conts ) {
			//System.err.println( ct.getName() + "  " + ct.getAnnotationCount() + "  " + ct.getSequence().length() );
			
			sum1 += ct.getAnnotationCount();
		}
		fsum1 = sum1;
		
		int sum2 = 0;
		for( Sequence ct : spec2Conts ) {
			sum2 += ct.getAnnotationCount();
		}
		fsum2 = sum2;
		
		System.err.println( fsum1 + "  " + fsum2 );
	}
	
	Sequence	contigx;
	Sequence	contigy;
	Point	mouseSel;
	public void xyPlot( final GeneSetHead genesethead, final Container comp, final List<Gene> genelist, Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) {
		final TableView<GeneGroup> 	table = genesethead.getGeneGroupTable();
		final Collection<String> 	specset = genesethead.geneset.getSpecies(); //speciesFromCluster( clusterMap );
		final List<String>			species = new ArrayList<>( specset );
		//final List<String>	specList = new ArrayList<String>( species );
		
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
		table2.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		JScrollPane	scroll1 = new JScrollPane( table1 );
		JScrollPane	scroll2 = new JScrollPane( table2 );
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		
		c.add( scroll1 );
		c.add( scroll2 );
		
		JOptionPane.showMessageDialog(comp, c);
		
		spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		spec2 = (String)table2.getValueAt( table2.getSelectedRow(), 0 );
		
		initSpecConts( genesethead.geneset.speccontigMap, spec1, spec2 );
		
		final JRadioButton	oricolor = new JRadioButton("Orientation");
		final JRadioButton	gccolor = new JRadioButton("GC%");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( oricolor );
		bg.add( gccolor );
		
		final JCheckBox	showgrid = new JCheckBox();
		final JButton	swap = new JButton();
		
		final JComponent  drawc = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.setColor( Color.white );
				g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				
				int count = 0;
				for( Sequence ct : spec1Conts ) {
					if( ct.getAnnotations() != null ) {
						if( ct.isReverse() ) {
							for( int u = ct.getAnnotations().size()-1; u >= 0; u-- ) {
								Annotation val = ct.getAnnotation( u );
								Gene gene = val.getGene();
								if(gene!=null) {
									GeneGroup gg = gene.getGeneGroup();

									boolean rs = table.getSelectionModel().getSelectedItems().indexOf(gg) != -1;
									List<Annotation> tv2list = gg.getTegevals(spec2);
									for (Annotation tv2 : tv2list) {
										tv2.setSelected(rs);
										int count2 = 0;
										int k = spec2Conts.indexOf(tv2.getContshort());
										if (k != -1) {
											for (int i = 0; i < k; i++) {
												Sequence ct2 = spec2Conts.get(i);
												count2 += ct2.getAnnotationCount();
											}
											Sequence ct2 = spec2Conts.get(k);
											count2 += (ct2.isReverse() ? ct2.getAnnotationCount() - tv2.getNum() - 1 : tv2.getNum());

											if (gccolor.isSelected()) {
											/*double gc = (val.getGCPerc()+tv2.getGCPerc())/2.0;
											double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
											g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );*/
												g.setColor(tv2.getGCColor());
											} else {
												boolean sel = false;
												if (!genesethead.isGeneview()) {
													sel = table.getSelectionModel().getSelectedItems().indexOf(val.getGene().getGeneGroup()) != -1;
												} else {
													sel = genesethead.getGeneTable().getSelectionModel().getSelectedItems().indexOf(val.getGene()) != -1;
												}
												if (val.isSelected() || tv2.isSelected() || sel) g.setColor(Color.red);
												else g.setColor(Color.blue);
											}
											if (count == count2) {
												//ermcount++;
											}
											g.fillOval((int) ((count - 1) * this.getWidth() / fsum1), (int) ((count2 - 1) * this.getHeight() / fsum2), 3, 3);
										}
									}
								
								/*Tegeval next = val.getNext();
								if( next != null ) {
									Sequence nextcontig = next.getContshort();
									if( nextcontig == null || !nextcontig.equals(val.getContshort()) ) {
										next = null;
									}
								}
								val = next;*/
									count++;
								}
							}
						} else {
							for( Annotation ann : ct.getAnnotations() ) {
								Gene gene = ann.getGene();
								if( gene != null ) {
									GeneGroup gg = gene.getGeneGroup();
									if( gg != null ) {
										boolean rs = table.getSelectionModel().getSelectedItems().indexOf(gg) != -1;
										List<Annotation> tv2list = gg.getTegevals(spec2);
										for (Annotation tv2 : tv2list) {
											tv2.setSelected(rs);
											int count2 = 0;
											int k = spec2Conts.indexOf(tv2.getContshort());
											if (k != -1) {
												for (int i = 0; i < k; i++) {
													Sequence ct2 = spec2Conts.get(i);
													count2 += ct2.getAnnotationCount();
												}
												Sequence ct2 = spec2Conts.get(k);
												count2 += (ct2.isReverse() ? ct2.getAnnotationCount() - tv2.getNum() - 1 : tv2.getNum());

												if (gccolor.isSelected()) {
													/*double gc = (val.getGCPerc()+tv2.getGCPerc())/2.0;
													double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
													g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );*/
													g.setColor(tv2.getGCColor());
												} else {
													boolean sel = false;
													if (!genesethead.isGeneview()) {
														sel = table.getSelectionModel().getSelectedItems().indexOf(ann.getGene().getGeneGroup()) != -1;
													} else {
														sel = genesethead.getGeneTable().getSelectionModel().getSelectedItems().indexOf(ann.getGene()) != -1;
													}
													if (ann.isSelected() || tv2.isSelected() || sel)
														g.setColor(Color.red);
													else g.setColor(Color.blue);
												}
												if (count == count2) {
													//ermcount++;
												}
												g.fillOval((int) ((count - 1) * this.getWidth() / fsum1), (int) ((count2 - 1) * this.getHeight() / fsum2), 3, 3);
											}
										}
									}

									/*Tegeval next = val.getNext();
									if( next != null ) {
										Sequence nextcontig = next.getContshort();
										if( nextcontig == null || !nextcontig.equals(val.getContshort()) ) {
											next = null;
										}
									}
									val = next;*/
								}
								count++;
							}
						}
						//System.err.println( count );
					}
					//System.err.println( ct.getName() + "   " + count + "  " + ermcount );
					
					if( showgrid.isSelected() ) {
						g.setColor( Color.gray );
						int x = (int)(count*this.getWidth()/fsum1);
						System.err.println( x + "  " + this.getWidth() + " " + fsum1 + "  " + count );
						g.drawLine( x, 0, x, this.getHeight() );
					}
				}
				
				if( showgrid.isSelected() ) {
					g.setColor( Color.gray );
					count = 0;
					for( Sequence ct : spec2Conts ) {
						count += ct.getAnnotationCount();
						int y = (int)(count*this.getHeight()/fsum2);
						g.drawLine( 0, y, this.getWidth(), y );
					}
				}
			}
		};
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Reverse horizontal") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( mouseSel != null ) {
					int x = mouseSel.x;
					
					Sequence contig = null;
					int count = 0;
					for( Sequence ct : spec1Conts ) {
						count += ct.getAnnotationCount();
						
						if( x < count*drawc.getWidth()/fsum1 ) {
							contig = ct;
							break;
						}
					}
					if( contig != null ) {
						contig.setReverse( !contig.isReverse() );
						drawc.repaint();
					}
				}
			}
		});
		popup.add( new AbstractAction("Reverse vertical") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( mouseSel != null ) {
					int y = mouseSel.y;
					
					Sequence contig = null;
					int count = 0;
					for( Sequence ct : spec2Conts ) {
						count += ct.getAnnotationCount();
						
						if( y < count*drawc.getHeight()/fsum2 ) {
							contig = ct;
							break;
						}
					}
					if( contig != null ) {
						contig.setReverse( !contig.isReverse() );
						drawc.repaint();
					}
				}
			}
		});
		drawc.setComponentPopupMenu( popup );
		
		final JComboBox<String>	comb1 = new JComboBox<String>();
		final JComboBox<String>	comb2 = new JComboBox<String>();
		
		showgrid.setAction( new AbstractAction("Show grid") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawc.repaint();
			}
		});
		swap.setAction( new AbstractAction("Transpose") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Sequence>	tmpct = spec1Conts;
				spec1Conts = spec2Conts;
				spec2Conts = tmpct;
				
				String tmpit = (String)comb1.getSelectedItem();
				comb1.setSelectedItem( comb2.getSelectedItem() );
				comb2.setSelectedItem( tmpit );
			}
		});
		
		drawc.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseSel = e.getPoint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseSel = e.getPoint();
				if( e.getClickCount() == 2 ) {
					//System.err.println( spec1Conts.size() + "  " + spec2Conts.size() );
					for( Sequence ct : spec1Conts ) {
						Annotation tv = ct.getFirst();
						while( tv != null && tv.getContig().equals(ct) ) {
							tv.setSelected( false );
							tv = tv.getNext();
						}
					}
					
					for( Sequence ct : spec2Conts ) {
						Annotation tv = ct.getFirst();
						while( tv != null && tv.getContig().equals(ct) ) {
							tv.setSelected( false );
							tv = tv.getNext();
						}
					}
					table.getSelectionModel().clearSelection();
					//table.removeRowSelectionInterval(0, table.getRowCount());
					drawc.repaint();
				} else if( showgrid.isSelected() ) {
					int x = mouseSel.x;
					int count = 0;
					for( Sequence ct : spec1Conts ) {
						count += ct.getAnnotationCount();
						
						if( x < count*drawc.getWidth()/fsum1 ) {
							contigx = ct;
							break;
						}
					}
					
					int y = mouseSel.y;
					count = 0;
					for( Sequence ct : spec2Conts ) {
						count += ct.getAnnotationCount();
						
						if( y < count*drawc.getHeight()/fsum2 ) {
							contigy = ct;
							break;
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Point relmouse = e.getPoint();
				if( mouseSel != null && e.getButton() != MouseEvent.BUTTON3 && !showgrid.isSelected() ) {
					int xmin = Math.min( mouseSel.x, relmouse.x )*fsum1/drawc.getWidth();
					int xmax = Math.max( mouseSel.x, relmouse.x )*fsum1/drawc.getWidth();
					for( int x = xmin; x < xmax; x++ ) {
						int count = 0;
						Sequence tct = null;
						for( Sequence ct : spec1Conts ) {
							if( ct.getAnnotationCount()+count < x ) count += ct.getAnnotationCount();
							else {
								tct = ct;
								break;
							}
						}
						Annotation te = tct.getIndex( x-count );
						
						if( te != null ) {
							te.setSelected( true );
							
							if( !genesethead.isGeneview() ) {
								table.getSelectionModel().select( te.getGene().getGeneGroup() );
								table.scrollTo( te.getGene().getGeneGroup() );
							} else {
								genesethead.getGeneTable().getSelectionModel().select( te.getGene() );
								genesethead.getGeneTable().scrollTo( te.getGene() );
							}
							
							/*int r = table.convertRowIndexToView(i);
							if( te.isSelected() ) {
								Neighbour.currentTe = te;
								if( r >= 0 && r < table.getRowCount() ) {
									table.addRowSelectionInterval(r, r);
									table.scrollRectToVisible( table.getCellRect(r, 0, false) );
								}
							} else {
								if( r >= 0 && r < table.getRowCount() ) {
									table.removeRowSelectionInterval(r, r);
									table.scrollRectToVisible( table.getCellRect(r, 0, false) );
								}
							}*/
						}
					}
					
					int ymin = Math.min( mouseSel.y, relmouse.y )*fsum2/drawc.getHeight();
					int ymax = Math.max( mouseSel.y, relmouse.y )*fsum2/drawc.getHeight();
					for( int y = ymin; y < ymax; y++ ) {
						int count = 0;
						Sequence tct = null;
						for( Sequence ct : spec2Conts ) {
							if( ct.getAnnotationCount()+count < y ) count += ct.getAnnotationCount();
							else {
								tct = ct;
								break;
							}
						}
						Annotation te = tct.getIndex( y-count );
						
						if( te != null ) {
							te.setSelected( true );
							
							if( !genesethead.isGeneview() ) {
								table.getSelectionModel().select( te.getGene().getGeneGroup() );
								table.scrollTo( te.getGene().getGeneGroup() );
							} else {
								genesethead.getGeneTable().getSelectionModel().select( te.getGene() );
								genesethead.getGeneTable().scrollTo( te.getGene() );
							}
							
							/*int r = table.convertRowIndexToView(i);
							if( te.isSelected() ) {
								Neighbour.currentTe = te;
								if( r >= 0 && r < table.getRowCount() ) {
									table.addRowSelectionInterval(r, r);
									table.scrollRectToVisible( table.getCellRect(r, 0, false) );
								}
							} else {
								if( r >= 0 && r < table.getRowCount() ) {
									table.removeRowSelectionInterval(r, r);
									table.scrollRectToVisible( table.getCellRect(r, 0, false) );
								}
							}*/
						}
					}
					
					drawc.repaint();
				} else if( showgrid.isSelected() ) {
					int x = relmouse.x;
					int count = 0;
					Sequence ctx = null;
					for( Sequence ct : spec1Conts ) {
						count += ct.getAnnotationCount();
						
						if( x < count*drawc.getWidth()/fsum1 ) {
							ctx = ct;
							break;
						}
					}
					
					int y = relmouse.y;
					count = 0;
					Sequence cty = null;
					for( Sequence ct : spec2Conts ) {
						count += ct.getAnnotationCount();
						
						if( y < count*drawc.getHeight()/fsum2 ) {
							cty = ct;
							break;
						}
					}
					
					if( contigy != null && cty != null && contigy != cty ) {
						int i1 = spec2Conts.indexOf( contigy );
						int i2 = spec2Conts.indexOf( cty );
						
						spec2Conts.remove( contigy );
						spec2Conts.add(i2, contigy );
						//if( i1 > i2 ) spec2Conts.add(i2, contigy );
						//else spec2Conts.add(i2-1, contigy );
					}
					
					if( contigx != null && ctx != null && contigx != ctx ) {
						int i1 = spec1Conts.indexOf( contigx );
						int i2 = spec1Conts.indexOf( ctx );
						
						spec1Conts.remove( contigx );
						spec1Conts.add(i2, contigx );
						//if( i1 > i2 ) spec1Conts.add(i2, contigx );
						//else spec1Conts.add(i2-1, contigx );
					}
					
					drawc.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		drawc.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				int cnt = 0;
				Sequence selcontx = null;
				for( Sequence c : spec1Conts ) {
					selcontx = c;
					cnt += c.getAnnotationCount();
					if( p.x < cnt*drawc.getWidth()/fsum1 ) {
						break;
					}
				}
				cnt = 0;
				Sequence selconty = null;
				for( Sequence c : spec2Conts ) {
					selconty = c;
					cnt += c.getAnnotationCount();
					if( p.y < cnt*drawc.getHeight()/fsum2 ) {
						break;
					}
				}
				String ttstr = "<html>"+selcontx + "<br>" + selconty+"</html>";
				if( !ttstr.equals( drawc.getToolTipText() ) ) drawc.setToolTipText( ttstr );
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
						
		Dimension dim = new Dimension( fsum1, fsum2 );
		drawc.setPreferredSize( dim );
		drawc.setSize( dim );
		JScrollPane	drawscroll = new JScrollPane( drawc );

		JToolBar	toolbox = new JToolBar();
		toolbox.add( oricolor );
		toolbox.add( gccolor );
		toolbox.add( new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(drawc.getWidth()*1.25), (int)(drawc.getHeight()*1.25) );
				drawc.setPreferredSize( dim );
				drawc.setSize( dim );
			}
		});
		toolbox.add( new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(drawc.getWidth()*0.8), (int)(drawc.getHeight()*0.8) );
				drawc.setPreferredSize( dim );
				drawc.setSize( dim );
			}
		});
		
		ComboBoxModel<String>	cbmodel1 = new ComboBoxModel<>() {
			@Override
			public int getSize() {
				return species.size();
			}

			@Override
			public String getElementAt(int index) {
				return species.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
			}

			@Override
			public void setSelectedItem(Object anItem) {
				spec1 = (String) anItem;
			}

			@Override
			public Object getSelectedItem() {
				return spec1;
			}
		};
		comb1.setModel( cbmodel1 );
		
		ComboBoxModel<String>	cbmodel2 = new ComboBoxModel<>() {
			@Override
			public int getSize() {
				return species.size();
			}

			@Override
			public String getElementAt(int index) {
				return species.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
			}

			@Override
			public void setSelectedItem(Object anItem) {
				spec2 = (String) anItem;
			}

			@Override
			public Object getSelectedItem() {
				return spec2;
			}
		};
		comb2.setModel( cbmodel2 );
		
		comb1.setSelectedItem( spec1 );
		comb2.setSelectedItem( spec2 );
		
		comb1.addItemListener(e -> {
			initSpecConts( genesethead.geneset.speccontigMap, (String)comb1.getSelectedItem(), (String)comb2.getSelectedItem() );
			drawc.repaint();
		});
		comb2.addItemListener(e -> {
			initSpecConts( genesethead.geneset.speccontigMap, (String)comb1.getSelectedItem(), (String)comb2.getSelectedItem() );
			drawc.repaint();
		});
		
		toolbox.add( comb1 );
		toolbox.add( comb2 );
		
		toolbox.add( showgrid );
		toolbox.add( swap );
		
		oricolor.addChangeListener(e -> drawc.repaint());
		gccolor.addChangeListener(e -> drawc.repaint());
		
		JFrame frame = new JFrame();
		frame.setLayout( new BorderLayout() );
		frame.add( toolbox, BorderLayout.NORTH );
		frame.add( drawscroll );
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				try {
					genesethead.geneset.saveContigOrder();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
	}
}
