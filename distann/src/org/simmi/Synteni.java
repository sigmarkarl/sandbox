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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.shared.Contig;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Tegeval;

public class Synteni {
	final int FASTI = 3632;
	
	public void syntenyMynd( final GeneSet geneset, final Container comp, final List<Gene> genes ) {
		Set<String>	tspecies = new HashSet<String>();
		final JTable sorting = geneset.getGeneTable();
		int[] rr = sorting.getSelectedRows();
		if( sorting.getModel() == geneset.groupModel ) {
			for( int r : rr ) {
				int i = sorting.convertRowIndexToModel( r );
				GeneGroup gg = geneset.allgenegroups.get( i );
				tspecies.addAll( gg.getSpecies() );
			}
		} else {
			for( int r : rr ) {
				int i = sorting.convertRowIndexToModel( r );
				GeneGroup gg = genes.get(i).getGeneGroup();
				tspecies.addAll( gg.getSpecies() );
			}
		}
		
		final List<String>	selspec = new ArrayList( geneset.getSelspec( geneset, new ArrayList<String>(tspecies), (JCheckBox[])null) );
		final JTable rowheader = new JTable();
		TableModel model = new TableModel() {
			@Override
			public int getRowCount() {
				return selspec.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Species";
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
				return geneset.nameFix( selspec.get( rowIndex ) );
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		};
		rowheader.setModel( model );
		rowheader.setRowHeight( 25 );
		
		final JComponent c = new JComponent() {
			Color gr = Color.green;
			Color dg = Color.green.darker();
			Color rd = Color.red;
			Color dr = Color.red.darker();
			Color altcol = Color.black;
			// Color dg = Color.green.darker();

			public String getToolTipText( MouseEvent me ) {
				Point p = me.getPoint();
				//Tegeval te = getSelectedTe(p, rowheader, sequenceView, hteg, rowheader.getRowHeight());
				//if( te != null ) return "<html>"+te.getGene().getName()+ "<br>" + te.getGene().refid+ "<br>" + te.getGene().getGeneGroup().getFunctions() + "<br>" + te.start + ".." + te.stop + "</html>";
				return null;
			}
			
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setFont( g.getFont().deriveFont( 8.0f ) );
				
				Rectangle clip = this.getVisibleRect(); //g.getClipBounds();
				
				g.setColor( Color.black );
				for( int k = 0; k < rowheader.getRowCount(); k++ ) {
					int l = rowheader.convertRowIndexToModel( k );
					String spec = selspec.get( l );
					
					int h = k*rowheader.getRowHeight()+rowheader.getRowHeight()/2;
					g.fillRect( 0, h, this.getWidth(), 1 );
					
					int loc = 0;
					List<Contig>	cannset = geneset.speccontigMap.get( spec );
					for( Contig c : cannset ) {
						loc += c.getGeneCount();
						
						int nloc = loc*this.getWidth()/FASTI;
						g.fillRect( nloc, h-2, 1, 5 );
					}
				}
				
				String selsyn = (String)geneset.syncolorcomb.getSelectedItem();
				if( selsyn != null && selsyn.length() > 0 ) {
					List<Contig> scannset = geneset.speccontigMap.get(selsyn);
					for( int k = 0; k < rowheader.getRowCount(); k++ ) {
						int l = rowheader.convertRowIndexToModel( k );
						String spec = selspec.get( l );
						
						int h = k*rowheader.getRowHeight()+rowheader.getRowHeight()/2;
						
						int loc = 0;
						List<Contig>	cannset = geneset.speccontigMap.get( spec );
						for( Contig c : cannset ) {
							if( c.getAnnotations() != null ) {
								if( c.isReverse() ) {
									for( int i = c.getAnnotations().size()-1; i >= 0; i-- ) {
										Tegeval tv = (Tegeval)c.getAnnotation(i);
										GeneGroup gg = tv.getGene().getGeneGroup();
										
										if( gg != null ) {
											int nloc = geneset.getGlobalIndex(tv)*this.getWidth()/FASTI; //(loc+(c.annset.size()-i-1))*this.getWidth()/FASTI;
											
											double ratio2 = GeneCompare.invertedGradientTotalRatio( selsyn, scannset, -1.0, tv.getGene().getGeneGroup() );
											if( ratio2 != -1 ) {
												g.setColor( GeneCompare.gradientColor( ratio2 ) );
												g.fillRect(nloc, h-6, 1, 4);
											}
										}
									}
								} else {
									for( int i = 0; i < c.getAnnotations().size(); i++ ) {
										Tegeval tv = (Tegeval)c.getAnnotation(i);
										GeneGroup gg = tv.getGene().getGeneGroup();
										
										if( gg != null ) {
											int nloc = geneset.getGlobalIndex(tv)*this.getWidth()/FASTI; //int nloc = (loc+i)*this.getWidth()/FASTI;
											
											double ratio2 = GeneCompare.invertedGradientTotalRatio( selsyn, scannset, -1.0, gg );
											
											/*String symb = gg.getCommonSymbol();
											if( symb != null && symb.contains("polA1") ) {
												System.err.println( spec + " " + ratio2 );
												
												g.setColor( GeneCompare.gradientColor( ratio2 ) );
												g.fillRect(nloc, h-6, 10, 10);
											}*/
											
											if( ratio2 != -1 ) {
												g.setColor( GeneCompare.gradientColor( ratio2 ) );
												g.fillRect(nloc, h-6, 1, 4);
											}
										}
									}
								}
								loc += c.getGeneCount();
								
								//int nloc = loc*this.getWidth()/3000;
								//g.fillRect( nloc, h-2, 1, 5 );
							}
						}
					}
				}
				
				//g.setColor( Color.blue );
				int[] rr = sorting.getSelectedRows();
				if( sorting.getModel() == geneset.groupModel ) {
					for( int r : rr ) {
						int i = sorting.convertRowIndexToModel( r );
						GeneGroup gg = geneset.allgenegroups.get( i );
						for( int k = 0; k < rowheader.getRowCount(); k++ ) {
							int l = rowheader.convertRowIndexToModel( k );
							String spec1 = selspec.get( l );
							List<Tegeval> tvlist = gg.getTegevals( spec1 );
							
							if( k < rowheader.getRowCount()-1 ) {
								int rh2 = rowheader.getRowHeight()/2;
								for( Tegeval tv : tvlist ) {
									int m = rowheader.convertRowIndexToModel( k+1 );
									String spec2 = selspec.get( m );
									List<Tegeval> tvlist2 = gg.getTegevals( spec2 );
									
									int gind = geneset.getGlobalIndex( tv )*this.getWidth()/FASTI;
									for( Tegeval tv2 : tvlist2 ) {
										int gind2 = geneset.getGlobalIndex( tv2 )*this.getWidth()/FASTI;
										if( tv.ori != tv2.ori ^ tv.getContshort().isReverse() != tv2.getContshort().isReverse() ) g.setColor( Color.red );
										else g.setColor( Color.blue );
										g.drawLine(gind, k*rowheader.getRowHeight()+rh2, gind2, (k+1)*rowheader.getRowHeight()+rh2 );
									}
								}
							}
						}
					}
				} else {
					for( int r : rr ) {
						int i = sorting.convertRowIndexToModel( r );
						Gene gene = geneset.genelist.get( i );
						for( int k = 0; k < rowheader.getRowCount(); k++ ) {
							int l = rowheader.convertRowIndexToModel( k );
							String spec1 = selspec.get( l );
							List<Tegeval> tvlist = gene.getGeneGroup().getTegevals( spec1 );
							
							if( k < rowheader.getRowCount()-1 ) {
								int rh2 = rowheader.getRowHeight()/2;
								for( Tegeval tv : tvlist ) {
									int m = rowheader.convertRowIndexToModel( k+1 );
									String spec2 = selspec.get( m );
									List<Tegeval> tvlist2 = gene.getGeneGroup().getTegevals( spec2 );
									
									int gind = geneset.getGlobalIndex( tv )*this.getWidth()/FASTI;
									if( tvlist2.isEmpty() ) {
										g.setColor( Color.blue );
										g.drawLine(gind, k*rowheader.getRowHeight()+rh2, gind, k*rowheader.getRowHeight()+rh2+5 );
									} else for( Tegeval tv2 : tvlist2 ) {
										int gind2 = geneset.getGlobalIndex( tv2 )*this.getWidth()/FASTI;
										if( tv.ori != tv2.ori ^ tv.getContshort().isReverse() != tv2.getContshort().isReverse() ) g.setColor( Color.red );
										else g.setColor( Color.blue );
										g.drawLine(gind, k*rowheader.getRowHeight()+rh2, gind2, (k+1)*rowheader.getRowHeight()+rh2 );
									}
								}
							}
						}
					}
				}
			}
		};
		c.setToolTipText("bleh");
		
		final AbstractAction	a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.repaint();
			}
		};
		
		c.addMouseListener(new MouseAdapter() {
			Point p;

			public void mousePressed(MouseEvent me) {
				p = me.getPoint();
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
		rowheader.setComponentPopupMenu( popup );

		JScrollPane scrollpane = new JScrollPane(c);
		scrollpane.getViewport().setBackground(Color.white);
		JScrollPane rowheaderscroll = new JScrollPane();
		rowheader.setAutoCreateRowSorter(true);
		rowheader.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					Set<Tegeval>	ste = new HashSet<Tegeval>();
					int[] rr = rowheader.getSelectedRows();
					for( int r : rr ) {
						int i = rowheader.convertRowIndexToModel( r );
						ste.add( Neighbour.hteg.get(i) );
					}
					Neighbour.hteg.removeAll( ste );
					
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
		scrollpane.setRowHeaderView(rowheader);
		
		rowheaderscroll.setViewport(scrollpane.getRowHeader());
		rowheaderscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		rowheaderscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		rowheader.getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				c.repaint();
			}
		});

		JSplitPane splitpane = new JSplitPane();
		splitpane.setLeftComponent(rowheaderscroll);
		splitpane.setRightComponent(scrollpane);
		geneset.splitpaneList.add( splitpane );

		JComponent fillup = new JComponent() {};
		fillup.setPreferredSize(new Dimension(6000, 20));
		scrollpane.setColumnHeaderView(fillup);

		int rh = rowheader.getHeight();
		if (rh == 0) {
			rh = rowheader.getRowCount() * rowheader.getRowHeight();
		}
		
		Dimension dim = new Dimension(3000, rh);
		c.setPreferredSize( dim );
		c.setSize( dim );
	
		final JToolBar	toolbar = new JToolBar();
		toolbar.add( new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(c.getWidth()*1.25), c.getHeight() );
				c.setPreferredSize( dim );
				c.setSize( dim );
			}
		});
		toolbar.add( new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension	dim = new Dimension( (int)(c.getWidth()*0.8), c.getHeight() );
				c.setPreferredSize( dim );
				c.setSize( dim );
			}
		});
		
		JComponent panel = new JComponent() { private static final long serialVersionUID = 1L; };
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( splitpane );
	
		JFrame frame = new JFrame();
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
