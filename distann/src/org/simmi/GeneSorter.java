package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class GeneSorter {
	public static List<Tegeval> loadContigs( Collection<Gene> genes, String species, final List<Contig> contigs ) {
		final List<Tegeval> ltv = new ArrayList<Tegeval>();
		for (Gene g : genes) {
			if (g.species != null) {
				System.err.println( g.species.keySet() );
				// for( String sp : g.species.keySet() ) {
				for (String spec : g.species.keySet()) {
					Teginfo stv = g.species.get(spec);
					if (stv != null)
						for (Tegeval tv : stv.tset) {
							if (spec.equals(species)) {
								ltv.add(tv);
							}

							//int first = tv.cont.indexOf("_");
							//int sec = tv.cont.indexOf("_", first + 1);
							if( tv.cont != null ) {
								int sec = tv.cont.lastIndexOf('_');
	
								String cname = tv.cont.substring(0, sec);
								if( !GeneSet.contigmap.containsKey( cname ) ) {
									GeneSet.contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
				// }
			}
		}
		// locsort = true;
		Collections.sort(ltv);
		// locsort = false;

		contigs.clear();
		for (String c : GeneSet.contigmap.keySet()) {
			Contig contig = GeneSet.contigmap.get(c);
			contigs.add( contig );
		}
		
		return ltv;
	}

	public static void mynd(final List<Gene> genes, final JTable sorting, String species, final List<Contig> contigs) throws IOException {
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	shareColorScheme = new JRadioButton("Sharing");
		final JRadioButton	groupCoverageColorScheme = new JRadioButton("GroupCoverage");
		
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {			
			List<Tegeval> ltv = loadContigs( genes, species, contigs );
			
			final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			System.out.println(hey);
			final JTable rowheader = new JTable();
				
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();
				Color altcol = Color.black;
				// Color dg = Color.green.darker();

				public void paintComponent(Graphics g) {
					super.paintComponent(g);

					Gene	lastgene = null;
					Rectangle rc = g.getClipBounds();
					for (int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++) {
						int r = sorting.convertRowIndexToModel(i);
						Gene gene = genes.get(r);

						if( binaryColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								if (i % 2 == 0)
									g.setColor(rd);
								else
									g.setColor(dr);
							} else {
								if (i % 2 == 0)
									g.setColor(gr);
								else
									g.setColor(dg);
							}
						} else if( gcColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								double gcp = Math.min( Math.max( 0.5, gene.getAvgGCPerc() ), 0.8 );
								g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
							} else {
								double gcp = Math.min( Math.max( 0.5, gene.getAvgGCPerc() ), 0.8 );
								g.setColor( new Color( (float)(gcp-0.5)/0.3f, (float)(0.8-gcp)/0.3f, 0.0f ) );
							}
						} else if( locprevColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								double locprev = Math.min( 5.0, gene.proximityGroupPreservation );
								g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
							} else {
								double locprev = Math.min( 5.0, gene.proximityGroupPreservation );
								g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
							}
						} else if( cycColorScheme.isSelected() ) {
							int max = gene.getMaxCyc();
							
							if (sorting.isRowSelected(i)) {					
								//double locprev = max; //Math.min( , max );
								g.setColor( new Color( 1.0f-(float)max/27.0f, 1.0f, 1.0f ) );
							} else {
								g.setColor( new Color( (float)max/27.0f, 0.0f, 0.0f ) );
							}
						} else if( lenColorScheme.isSelected() ) {
							int max = gene.getMaxLength();
							
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								g.setColor( new Color( 1.0f-(float)(max-20)/2775.0f, 1.0f, 1.0f ) );
							} else {
								g.setColor( new Color( (float)(max-20)/2775.0f, 0.0f, 0.0f ) );
							}
						} else if( shareColorScheme.isSelected() ) {
							//if( lastgene == null || !gene.species.keySet().equals( lastgene.species.keySet() ) ) {
							if( lastgene == null || !gene.getGeneGroup().getSpecies().equals( lastgene.getGeneGroup().getSpecies() ) ) {
								if( altcol == Color.black ) altcol = Color.red;
								else altcol = Color.black;
							}
							
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								g.setColor( altcol );
							} else {
								g.setColor( altcol );
							}
						} else if( groupCoverageColorScheme.isSelected() ) {
							int gc = gene.getGroupCoverage();
							if (sorting.isRowSelected(i)) {								
								//double locprev = max; //Math.min( , max );
								if( gc == -1 ) g.setColor( Color.cyan );
								else g.setColor( new Color( 1.0f-(float)(gc)/28.0f, 1.0f, 1.0f ) );
							} else {
								if( gc == -1 ) g.setColor( Color.blue );
								else g.setColor( new Color( (float)(gene.getGroupCoverage())/28.0f, 0.0f, 0.0f ) );
							}
						}
						lastgene = gene;
						
						if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								String contig = (String) rowheader.getValueAt(y, 0);

								int und = contig.indexOf("_");
								String spec = contig.substring(0, und);
								if (gene.species.containsKey(spec)) {
									Teginfo stv = gene.species.get(spec);
									if( stv != null && stv.tset != null )
									for (Tegeval tv : stv.tset) {
										if( tv.cont != null && tv.cont.startsWith(contig)) {
											g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
										}
									}
								}
							}
						}
					}

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
			
			final AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			binaryColorScheme.setAction( a );
			gcColorScheme.setAction( a );
			locprevColorScheme.setAction( a );
			cycColorScheme.setAction( a );
			lenColorScheme.setAction( a );
			shareColorScheme.setAction( a );
			groupCoverageColorScheme.setAction( a );
			
			binaryColorScheme.setText("Binary");
			gcColorScheme.setText("GC");
			locprevColorScheme.setText("Loc");
			cycColorScheme.setText("#Cys");
			lenColorScheme.setText("Len");
			shareColorScheme.setText("Share");
			groupCoverageColorScheme.setText("GroupCoverage");
			
			binaryColorScheme.setSelected( true );
			
			ButtonGroup	bg = new ButtonGroup();
			bg.add( binaryColorScheme );
			bg.add( gcColorScheme );
			bg.add( locprevColorScheme );
			bg.add( cycColorScheme );
			bg.add( lenColorScheme );
			bg.add( shareColorScheme );
			bg.add( groupCoverageColorScheme );

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
						sorting.setRowSelectionInterval(p.x, np.x);
					}
				}
			});

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if (columnIndex == 1)
						return "species";
					else if (columnIndex == 2)
						return "com";
					return "contig";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 2)
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (columnIndex == 2) {
						Contig c = contigs.get(rowIndex);
						if (c.count > 0)
							return (int) ((c.loc) / c.count);
						return 0;
					} else if (columnIndex == 1) {
						Contig c = contigs.get(rowIndex);
						String cname = c.getName();
						int i = cname.indexOf('_');
						return cname.substring(0, i);
					}
					return contigs.get(rowIndex).getName();
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
			fillup.setPreferredSize(new Dimension(hey, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(hey, rh));
			c.setSize(hey, rh);
		}
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( binaryColorScheme );
		toolbar.add( gcColorScheme );
		toolbar.add( locprevColorScheme );
		toolbar.add( cycColorScheme );
		toolbar.add( lenColorScheme );
		toolbar.add( shareColorScheme );
		toolbar.add( groupCoverageColorScheme );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( splitpane );

		//if (!frame.isVisible()) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add( panel );

		frame.setVisible(true);
	}
	
	public static void loadContigs( List<GeneGroup> genegroups, List<Contig> contigs ) {
		for (GeneGroup gg : genegroups) {
			for( Gene g : gg.genes ) {
				// for( String sp : g.species.keySet() ) {
				for (String spec : g.species.keySet()) {
					Teginfo stv = g.species.get(spec);
					if (stv != null)
						for (Tegeval tv : stv.tset) {
							//int first = tv.cont.indexOf("_");
							//int sec = tv.cont.indexOf("_", first + 1);
							
							if( tv.cont != null ) {
								int sec = tv.cont.lastIndexOf('_');
	
								String cname = tv.cont.substring(0, sec);
								//System.err.println( cname );
								if( !GeneSet.contigmap.containsKey( cname ) ) {
									GeneSet.contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
			}
		}

		contigs.clear();
		for (String c : GeneSet.contigmap.keySet()) {
			contigs.add( GeneSet.contigmap.get(c) );
		}
	}
	
	public void groupMynd( final GeneSet geneset, final List<GeneGroup> geneGroups, final List<Gene> genelist, final JTable sorting, final List<Contig> contigs, final Map<Set<String>, ShareNum> specset) throws IOException {
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	shareColorScheme = new JRadioButton("Sharing");
		final JRadioButton	freqColorScheme = new JRadioButton("Freq");
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			loadContigs( geneGroups, contigs );
			
			JCheckBox	check = new JCheckBox("All positions");
			JOptionPane.showMessageDialog( null, check );
			final boolean allpos = check.isSelected();
			
			final int hey = geneGroups.size(); // ltv.get(ltv.size()-1).stop/1000;
			final JTable rowheader = new JTable();
			final JComponent c = new JComponent() {
				Color gr = Color.green;
				Color dg = Color.green.darker();
				Color rd = Color.red;
				Color dr = Color.red.darker();

				// Color dg = Color.green.darker();

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Rectangle rc = g.getClipBounds();
					Set<GeneGroup>	ggset = new HashSet<GeneGroup>();
					
					Color altcol = Color.darkGray;
					int prevnumshare = -1;
					if( shareColorScheme.isSelected() ) {						
						prevnumshare = -1;
						for( int i = 0; i < (int) rc.getMinX(); i++ ) {
							GeneGroup genegroup;
							Gene 		tgene = null;
							int r = sorting.convertRowIndexToModel(i);
							if( sorting.getModel() == geneset.groupModel ) {
								genegroup = GeneSet.allgenegroups.get( r );
							} else {
								tgene = genelist.get( r );
								genegroup = tgene.getGeneGroup();
							}
							
							if( ggset.add( genegroup ) ) {
								int numshare = specset.get( genegroup.getSpecies() ).numshare;
								if( numshare != prevnumshare ) {
									altcol = altcol == Color.lightGray ? Color.darkGray : Color.lightGray;
								}								
								prevnumshare = numshare;
							}
						}
					}
					
					for( int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getRowCount(), rc.getMaxX()); i++ ) {
						GeneGroup genegroup;
						Gene 		tgene = null;
						int r = sorting.convertRowIndexToModel(i);
						if( sorting.getModel() == geneset.groupModel ) {
							genegroup = GeneSet.allgenegroups.get( r );
						} else {
							tgene = genelist.get( r );
							genegroup = tgene.getGeneGroup();
						}
						//GeneGroup genegroup = geneGroups.get(r);
						if( ggset.add( genegroup ) ) {
							if( allpos ) ggset.clear();
							
							if( binaryColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									if (i % 2 == 0)
										g.setColor(rd);
									else
										g.setColor(dr);
								} else {
									if (i % 2 == 0)
										g.setColor(gr);
									else
										g.setColor(dg);
								}
							} else if( gcColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
								} else {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(gcp-0.5)/0.3f, (float)(0.8-gcp)/0.3f, 0.0f ) );
								}
							} else if( locprevColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
								}
							} else if( freqColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									//g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {

								}
								
								double freq = genegroup.species.size()/28;
								//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
								g.setColor( new Color( 1.0f-(float)freq, 1.0f, 1.0f ) );
							} else if( cycColorScheme.isSelected() ) {
								int max = genegroup.getMaxCyc();
								
								if (sorting.isRowSelected(i)) {								
									//double locprev = max; //Math.min( , max );
									g.setColor( new Color( 1.0f-(float)max/27.0f, 1.0f, 1.0f ) );
								} else {
									g.setColor( new Color( (float)max/27.0f, 0.0f, 0.0f ) );
								}
							} else if( lenColorScheme.isSelected() ) {
								int max = genegroup.getMaxLength();
								
								if (sorting.isRowSelected(i)) {								
									//double locprev = max; //Math.min( , max );
									g.setColor( new Color( 1.0f-(float)(max+1)/2800.0f, 1.0f, 1.0f ) );
								} else {
									g.setColor( new Color( (float)(max+1)/2800.0f, 0.0f, 0.0f ) );
								}
							} else if( shareColorScheme.isSelected() ) {
								if (sorting.isRowSelected(i)) {
									int numshare = specset.get( genegroup.getSpecies() ).numshare;
									if( numshare != prevnumshare ) {
										altcol = altcol == Color.lightGray ? Color.darkGray : Color.lightGray;
									}
									g.setColor( altcol );
									
									prevnumshare = numshare;
								} else {
									int numshare = specset.get( genegroup.getSpecies() ).numshare;
									if( numshare != prevnumshare ) {
										altcol = altcol == Color.lightGray ? Color.darkGray : Color.lightGray;
									}
									g.setColor( altcol );
									
									prevnumshare = numshare;
								}
							}
	
							//if (gene.species != null) {
							for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								if( y < rowheader.getRowCount() ) {
									String contig = (String)rowheader.getValueAt(y, 0);
	
									int und = contig.indexOf("_");
									String spec = contig.substring(0, und);
									/*if( genegroup.getCommonName().contains("tRNA-Phe") && spec.contains("SA01") ) {
										System.err.println();
									}*/
									if( genegroup.getSpecies().contains(spec) ) {
										List<Tegeval>	ltv = genegroup.getTegevals( spec );
										//Teginfo stv = gene.species.get(spec);
										for (Tegeval tv : ltv /*stv.tset*/ ) {
											if( tv.getContshort().getName().startsWith(contig) ) {
												g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
											}
										}
									}
								}
							}
						}
					}
					ggset.clear();

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
			
			AbstractAction	a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.repaint();
				}
			};
			binaryColorScheme.setAction( a );
			gcColorScheme.setAction( a );
			locprevColorScheme.setAction( a );
			cycColorScheme.setAction( a );
			lenColorScheme.setAction( a );
			shareColorScheme.setAction( a );
			freqColorScheme.setAction( a );
			
			binaryColorScheme.setText("Binary");
			gcColorScheme.setText("GC");
			locprevColorScheme.setText("Loc");
			cycColorScheme.setText("#Cys");
			lenColorScheme.setText("Len");
			shareColorScheme.setText("Share");
			freqColorScheme.setText("Freq");
			
			binaryColorScheme.setSelected( true );
			
			ButtonGroup	bg = new ButtonGroup();
			bg.add( binaryColorScheme );
			bg.add( gcColorScheme );
			bg.add( locprevColorScheme );
			bg.add( cycColorScheme );
			bg.add( lenColorScheme );
			bg.add( shareColorScheme );
			bg.add( freqColorScheme );

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
						sorting.setRowSelectionInterval(p.x, np.x);
					}
					
					c.repaint();
				}
			});

			JScrollPane scrollpane = new JScrollPane(c);
			scrollpane.getViewport().setBackground(Color.white);
			JScrollPane rowheaderscroll = new JScrollPane();
			rowheader.setAutoCreateRowSorter(true);
			rowheader.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if (columnIndex == 1)
						return "species";
					else if (columnIndex == 2)
						return "com";
					return "contig";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 2)
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (columnIndex == 2) {
						Contig c = contigs.get(rowIndex);
						if (c.count > 0)
							return (int) ((c.loc) / c.count);
						return 0;
					} else if (columnIndex == 1) {
						Contig c = contigs.get(rowIndex);
						String cname = c.getName();
						int i = cname.indexOf('_');
						return cname.substring(0, i);
					}
					return contigs.get(rowIndex).getName();
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
			fillup.setPreferredSize(new Dimension(hey, 20));
			scrollpane.setColumnHeaderView(fillup);

			// JComponent filldown = new JComponent() {};
			// filldown.setPreferredSize( new Dimension(100,25) );
			// rowheaderscroll.setCorner( JScrollPane., corner)

			int rh = rowheader.getHeight();
			if (rh == 0) {
				rh = rowheader.getRowCount() * rowheader.getRowHeight();
			}
			c.setPreferredSize(new Dimension(hey, rh));
			c.setSize(hey, rh);
		}
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( binaryColorScheme );
		toolbar.add( gcColorScheme );
		toolbar.add( locprevColorScheme );
		toolbar.add( cycColorScheme );
		toolbar.add( lenColorScheme );
		toolbar.add( shareColorScheme );
		toolbar.add( freqColorScheme );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( splitpane );

		//if (!frame.isVisible()) {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(800, 600);
			frame.add( panel );
		//}

		frame.setVisible(true);
	}
}