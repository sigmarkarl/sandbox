package org.simmi.distann;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.javafasta.shared.*;

import javafx.scene.control.TableView;

public class GeneSorter {
	public List<Contig> loadContigs( Collection<Gene> genes, final Map<String,Contig> contigmap ) {
		/*final List<Tegeval> ltv = new ArrayList<Tegeval>();
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
								if( !contigmap.containsKey( cname ) ) {
									contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
				// }
			}
		}
		// locsort = true;
		Collections.sort(ltv);
		// locsort = false;*/

		List<Contig> contigs = new ArrayList<Contig>();
		contigs.clear();
		for (String c : contigmap.keySet()) {
			Contig contig = contigmap.get(c);
			contigs.add( contig );
		}
		return contigs;
		
		//return ltv;
	}

	final Color gr = Color.lightGray;
	final Color dg = Color.gray;
	final Color rd = Color.red;
	final Color dr = new Color(196,0,0);
	final Color bl = Color.blue;
	final Color db = new Color(0,0,196);
	final Color mg = Color.magenta;
	final Color dm = new Color(196,0,196);
	public void mynd(GeneSet geneset, final List<Gene> genes, final List<String> speclist, final JTable sorting, String species, final Map<String,Contig> contigmap) throws IOException {
		final JCheckBox	collapsed = new JCheckBox("Collapsed");
		
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	shareColorScheme = new JRadioButton("Sharing");
		final JRadioButton	groupCoverageColorScheme = new JRadioButton("GroupCoverage");
		
		final JTable rowheader = new JTable();
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {			
			//List<Tegeval> ltv = loadContigs( genes, species, contigs, contigmap );
			final List<Contig> contigs = loadContigs( genes, contigmap );
			
			final int hey = genes.size(); // ltv.get(ltv.size()-1).stop/1000;
			System.out.println(hey);
				
			final JComponent c = new JComponent() {
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
								boolean phage = gene.getTegeval().isPhage();
								boolean plasmid = gene.getTegeval().getContshort().isPlasmid();
								
								GeneGroup gg = gene.getGeneGroup();
								Teginfo ti = gg.species.get( gene.getSpecies() );
								if( phage && plasmid ) {
									if( ti.tset.size() > 2 ) g.setColor(dm);
									else g.setColor(mg);
								} else if( phage ) {
									if( ti.tset.size() > 2 ) g.setColor(bl);
									else g.setColor(db);
								} else if( plasmid ) {
									if( ti.tset.size() > 2 ) g.setColor(dr);
									else g.setColor(rd);
								} else {
									if( ti.tset.size() > 2 ) g.setColor(dg);
									else g.setColor(gr);
								}
								/*if (i % 2 == 0)
									g.setColor(gr);
								else
									g.setColor(dg);*/
							}
						} else if( gcColorScheme.isSelected() ) {
							if (sorting.isRowSelected(i)) {
								double gcp = Math.min( Math.max( 0.5, gene.getGCPerc() ), 0.8 );
								g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
							} else {
								double gcp = Math.min( Math.max( 0.5, gene.getGCPerc() ), 0.8 );
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
						
						if (gene.getSpecies() != null) {
							if( collapsed.isSelected() ) {
								for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
									String spec = speclist.get(y);
									if( gene.getSpecies().equals(spec) ) {
										Annotation tv = gene.getTegeval();
										//if( tv.cont != null && tv.cont.startsWith(contig)) {
										g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
										//}
									}
								}
							} else for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								String contig = (String) rowheader.getValueAt(y, 0);

								int und = contig.indexOf("_");
								String spec = contig.substring(0, und);
								if( gene.getSpecies().equals(spec) ) {
									Annotation tv = gene.getTegeval();
									if( tv.getName() != null && tv.getName().startsWith(contig)) {
										g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
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
					if( collapsed.isSelected() ) return speclist.size();
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return collapsed.isSelected() ? 2 : 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if( collapsed.isSelected() ) {
						if( columnIndex == 1 ) {
							return "com";
						}
					} else {
						if (columnIndex == 0)
							return "contig";
						else if (columnIndex == 2)
							return "com";
					}
					return "species";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					boolean c = collapsed.isSelected();
					if (columnIndex == 2 && !c || (c && columnIndex == 1) )
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					boolean c = collapsed.isSelected();
					if( c ) {
						if (columnIndex == 1) {
							return 0;
						}
						return speclist.get(rowIndex);
					} else {
						if (columnIndex == 2) {
							Contig ctg = contigs.get(rowIndex);
							//if (c.count > 0)
							//	return (int) ((c.loc) / c.count);
							return 0;
						} else if (columnIndex == 1) {
							Contig ctg = contigs.get(rowIndex);
							String cname = ctg.getName();
							int i = cname.indexOf('_');
							return cname.substring(0, i);
						}
						return contigs.get(rowIndex).getName();
					}
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
		
		collapsed.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TableModel model = rowheader.getModel();
				rowheader.setModel( null );
				rowheader.setModel( model );
			}
		});
		toolbar.add( collapsed );
		
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
	
	public List<Sequence> loadContigs( List<GeneGroup> genegroups, Map<String,Sequence> contigmap ) {
		/*for (GeneGroup gg : genegroups) {
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
								if( !contigmap.containsKey( cname ) ) {
									contigmap.put(cname, tv.getContshort());
								}
							}
						}
				}
			}
		}*/

		List<Sequence>	contigs = new ArrayList<Sequence>();
		//contigs.clear();
		for (String c : contigmap.keySet()) {
			contigs.add( contigmap.get(c) );
		}
		return contigs;
	}
	
	public void groupMynd( final GeneSetHead genesethead, final List<GeneGroup> geneGroups, final List<String> speclist, final List<Gene> genelist, final TableView sorting, final Map<String,Sequence> contigmap, final Map<Set<String>, ShareNum> specset) throws IOException {
		GeneSet geneset = genesethead.geneset;
		
		final JCheckBox	collapsed = new JCheckBox("Collapsed");
		
		final JRadioButton	binaryColorScheme = new JRadioButton("Binary");
		final JRadioButton	gcColorScheme = new JRadioButton("GC");
		final JRadioButton	locprevColorScheme = new JRadioButton("Loc");
		final JRadioButton	cycColorScheme = new JRadioButton("#Cyc");
		final JRadioButton	lenColorScheme = new JRadioButton("Len");
		final JRadioButton	shareColorScheme = new JRadioButton("Sharing");
		final JRadioButton	freqColorScheme = new JRadioButton("Freq");
		
		final JTable rowheader = new JTable();
		JSplitPane splitpane = new JSplitPane();
		if (true) { //gsplitpane == null) {
			final List<Sequence> contigs = loadContigs( geneGroups, contigmap );
			
			JCheckBox	check = new JCheckBox("All positions");
			JOptionPane.showMessageDialog( null, check );
			final boolean allpos = check.isSelected();
			
			final int hey = !genesethead.isGeneview() ? geneGroups.size() : genelist.size(); // ltv.get(ltv.size()-1).stop/1000;
			final JComponent c = new JComponent() {
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
							int r = 0;//sorting.convertRowIndexToModel(i);
							if( !genesethead.isGeneview() ) {
								genegroup = geneset.allgenegroups.get( r );
							} else {
								tgene = genelist.get( r );
								genegroup = tgene.getGeneGroup();
							}
							
							if( ggset.add( genegroup ) ) {
								int numshare = specset.get( genegroup.getSpecies() ).numshare;
								if( numshare != prevnumshare ) {
									altcol = altcol == Color.lightGray ? Color.gray : Color.lightGray;
								}								
								prevnumshare = numshare;
							}
						}
					}
					
					for( int i = (int) rc.getMinX(); i < (int) Math.min(sorting.getItems().size(), rc.getMaxX()); i++ ) {
						GeneGroup genegroup;
						Gene 		tgene = null;
						int r = 0;//sorting.convertRowIndexToModel(i);
						if( !genesethead.isGeneview() ) {
							genegroup = geneset.allgenegroups.get( r );
						} else {
							tgene = genelist.get( r );
							genegroup = tgene.getGeneGroup();
						}
						//GeneGroup genegroup = geneGroups.get(r);
						if( ggset.add( genegroup ) ) {
							if( allpos ) ggset.clear();
							
							if( binaryColorScheme.isSelected() ) {
								if (sorting.getSelectionModel().isSelected(i)) {
									if (i % 2 == 0)
										g.setColor(rd);
									else
										g.setColor(dr);
								} else {
									boolean phage;
									boolean plasmid;
									if( tgene != null ) {
										phage = tgene.getTegeval().isPhage();
										plasmid = tgene.getTegeval().getContshort().isPlasmid();
									} else {
										phage = genegroup.isInAnyPhage();
										plasmid = genegroup.isOnAnyPlasmid();
									}
									
									if( tgene != null ) {
										GeneGroup gg = tgene.getGeneGroup();
										Teginfo ti = gg.species.get( tgene.getSpecies() );
										if( phage && plasmid ) {
											if( ti.tset.size() > 2 ) g.setColor(dm);
											else g.setColor(mg);
										} else if( phage ) {
											if( ti.tset.size() > 2 ) g.setColor(bl);
											else g.setColor(db);
										} else if( plasmid ) {
											if( ti.tset.size() > 2 ) g.setColor(dr);
											else g.setColor(rd);
										} else {
											if( ti.tset.size() > 2 ) g.setColor(dg);
											else g.setColor(gr);
										}
									} else {
										if( phage && plasmid ) {
											g.setColor(mg);
										} else if( phage ) {
											g.setColor(bl);
										} else if( plasmid ) {
											g.setColor(rd);
										} else {
											g.setColor(gr);
										}
									}
									/*if (i % 2 == 0)
										g.setColor(gr);
									else
										g.setColor(dg);*/
								}
							} else if( gcColorScheme.isSelected() ) {
								if (sorting.getSelectionModel().isSelected(i)) {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f ) );
								} else {
									double gcp = Math.min( Math.max( 0.5, genegroup.getAvgGCPerc() ), 0.8 );
									g.setColor( new Color( (float)(gcp-0.5)/0.3f, (float)(0.8-gcp)/0.3f, 0.0f ) );
								}
							} else if( locprevColorScheme.isSelected() ) {
								if (sorting.getSelectionModel().isSelected(i)) {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {
									double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									g.setColor( new Color( (float)locprev/5.0f, 0.0f, 0.0f ) );
								}
							} else if( freqColorScheme.isSelected() ) {
								if (sorting.getSelectionModel().isSelected(i)) {
									//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
									//g.setColor( new Color( 1.0f-(float)locprev/5.0f, 1.0f, 1.0f ) );
								} else {

								}
								
								double freq = genegroup.species.size()/28;
								//double locprev = Math.min( 5.0, tgene.proximityGroupPreservation );
								g.setColor( new Color( 1.0f-(float)freq, 1.0f, 1.0f ) );
							} else if( cycColorScheme.isSelected() ) {
								int max = genegroup.getMaxCyc();
								
								if (sorting.getSelectionModel().isSelected(i)) {								
									//double locprev = max; //Math.min( , max );
									g.setColor( new Color( 1.0f-(float)max/27.0f, 1.0f, 1.0f ) );
								} else {
									g.setColor( new Color( (float)max/27.0f, 0.0f, 0.0f ) );
								}
							} else if( lenColorScheme.isSelected() ) {
								int max = genegroup.getMaxLength();
								
								if (sorting.getSelectionModel().isSelected(i)) {								
									//double locprev = max; //Math.min( , max );
									g.setColor( new Color( 1.0f-(float)(max+1)/2800.0f, 1.0f, 1.0f ) );
								} else {
									g.setColor( new Color( (float)(max+1)/2800.0f, 0.0f, 0.0f ) );
								}
							} else if( shareColorScheme.isSelected() ) {
								if (sorting.getSelectionModel().isSelected(i)) {
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
							if( collapsed.isSelected() ) {
								for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
									if( y < speclist.size() ) {
										String spec = speclist.get(y);
										if( genegroup.getSpecies().contains(spec) ) {
											//Tegeval tv = gene.tegeval;
											//if( tv.cont != null && tv.cont.startsWith(contig)) {
											g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
											//}
										}
									}
								}
							} else for (int y = (int) (rc.getMinY() / rowheader.getRowHeight()); y < rc.getMaxY() / rowheader.getRowHeight(); y++) {
								if( y < rowheader.getRowCount() ) {
									String contig = (String)rowheader.getValueAt(y, 0);
	
									int und = contig.indexOf("_contig");
									if( und == -1 ) {
										und = contig.indexOf("uid");
										und = contig.indexOf("_", und+1);
									}
									String spec = contig.substring(0, und);
									/*if( genegroup.getCommonName().contains("tRNA-Phe") && spec.contains("SA01") ) {
										System.err.println();
									}*/
									if( genegroup.getSpecies().contains(spec) ) {
										List<Annotation>	ltv = genegroup.getTegevals( spec );
										//Teginfo stv = gene.species.get(spec);
										for (Annotation tv : ltv /*stv.tset*/ ) {
											if( binaryColorScheme.isSelected() ) {
												if (sorting.getSelectionModel().isSelected(i)) {
													if (i % 2 == 0)
														g.setColor(rd);
													else
														g.setColor(dr);
												} else {
													boolean phage;
													boolean plasmid;
													phage = tv.isPhage();
													plasmid = tv.getContshort().isPlasmid();
													
													if( tgene != null ) {
														GeneGroup gg = tgene.getGeneGroup();
														Teginfo ti = gg.species.get( tgene.getSpecies() );
														if( phage && plasmid ) {
															if( ti.tset.size() > 2 ) g.setColor(dm);
															else g.setColor(mg);
														} else if( phage ) {
															if( ti.tset.size() > 2 ) g.setColor(bl);
															else g.setColor(db);
														} else if( plasmid ) {
															if( ti.tset.size() > 2 ) g.setColor(dr);
															else g.setColor(rd);
														} else {
															if( ti.tset.size() > 2 ) g.setColor(dg);
															else g.setColor(gr);
														}
													} else {
														if( phage && plasmid ) {
															g.setColor(mg);
														} else if( phage ) {
															g.setColor(bl);
														} else if( plasmid ) {
															g.setColor(rd);
														} else {
															g.setColor(gr);
														}
													}
													/*if (i % 2 == 0)
														g.setColor(gr);
													else
														g.setColor(dg);*/
												}
											}
											
											if( tv.getContshort().getName().startsWith(contig) ) {
												g.fillRect(i, y * rowheader.getRowHeight(), 1, rowheader.getRowHeight());
												break;
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

					/*if (np.x > p.x) {
						Rectangle rect = sorting.getCellRect(p.x, 0, false);
						rect = rect.union(sorting.getCellRect(np.x, sorting.getColumnCount() - 1, false));
						sorting.scrollRectToVisible(rect);
						sorting.setRowSelectionInterval(p.x, np.x);
					}*/
					
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
					if( collapsed.isSelected() ) return speclist.size();
					return contigs.size();
				}

				@Override
				public int getColumnCount() {
					return collapsed.isSelected() ? 2 : 3;
				}

				@Override
				public String getColumnName(int columnIndex) {
					if( collapsed.isSelected() ) {
						if( columnIndex == 1 ) {
							return "com";
						}
					} else {
						if (columnIndex == 0)
							return "contig";
						else if (columnIndex == 2)
							return "com";
					}
					return "species";
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					boolean c = collapsed.isSelected();
					if (columnIndex == 2 && !c || (c && columnIndex == 1) )
						return Integer.class;
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return false;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					boolean c = collapsed.isSelected();
					if( c ) {
						if (columnIndex == 1) {
							return 0;
						}
						return speclist.get(rowIndex);
					} else {
						Sequence ctg = contigs.get(rowIndex);
						if (columnIndex == 2) {
							//if (c.count > 0)
							//	return (int) ((c.loc) / c.count);
							return ctg.length();
						} else if (columnIndex == 1) {
							//String cname = c.getName();
							//int i = cname.indexOf('_');
							return ctg.getSpec();
						}
						return ctg.getName();
					}
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

			rowheader.getRowSorter().addRowSorterListener(e -> c.repaint());

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
		
		collapsed.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TableModel model = rowheader.getModel();
				/*rowheader.setModel( null );
				rowheader.setModel( model );
				
				TableColumnModel columnModel = rowheader.getColumnModel();
				rowheader.setColumnModel( null );
				rowheader.setColumnModel(columnModel);*/
				
				rowheader.tableChanged( new TableModelEvent( model ) );
			}
		});
		toolbar.add( collapsed );
		
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