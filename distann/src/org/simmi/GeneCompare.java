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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import netscape.javascript.JSObject;

import org.apache.commons.codec.binary.Base64;
import org.simmi.shared.Sequence;

public class GeneCompare {
	List<Contig> contigs;
	
	public static Map<String,Integer> getBlosumMap() throws IOException {
		Map<String,Integer> blosumap = new HashMap<String,Integer>();
		InputStream is = GeneCompare.class.getResourceAsStream("/BLOSUM62");
		InputStreamReader 	ir = new InputStreamReader( is );
		BufferedReader		br = new BufferedReader( ir );
		String[] abet = null;
		//int i = 0;
		String line = br.readLine();
		while( line != null ) {
			if( line.charAt(0) != '#' ) {
				String[] split = line.trim().split("[ ]+");
				char chr = line.charAt(0);
				if( chr == ' ' ) {
					abet = split;
					abet[abet.length-1] = "-";
				} else {
					if( chr == '*' ) chr = '-';
					int k = 0;
					for( String a : abet ) {
						blosumap.put( chr+a, Integer.parseInt(split[++k]) );
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		
		return blosumap;
	}
	
	public void rearrangeContigs( String spec1, GeneSet geneset ) {
		if( spec1 != null && geneset.speccontigMap.containsKey( spec1 ) ) {
			final List<Contig> lcont = geneset.speccontigMap.get( spec1 );
			
			List<Contig> newcontigs = new ArrayList<Contig>();
			for( Contig c : lcont ) {
				if( contigs.contains( c ) ) newcontigs.add( c );
			}
			contigs = newcontigs;
		}
	}
	
	int total = 0;
	int ptotal = 0;
	public void selectContigs( Container comp, String spec1, GeneSet geneset ) {
		final List<Contig> lcont = geneset.speccontigMap.get( spec1 );
		
		System.err.println( lcont.size() );
		
		ptotal = 0;
		total = 0;
		JTable cseltable = new JTable();
		cseltable.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return lcont.size();
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "contig";
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
				return lcont.get( rowIndex );
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
			
		});
		JScrollPane	sp = new JScrollPane( cseltable );
		JOptionPane.showMessageDialog(comp, sp);
		contigs = new ArrayList<Contig>();
		for( int r : cseltable.getSelectedRows() ) {
			int i = cseltable.convertRowIndexToModel(r);
			Contig ctg = lcont.get( i );
			contigs.add( ctg );
			if( ctg.isPlasmid() ) ptotal += ctg.getGeneCount();
			else total += ctg.getGeneCount();
		}
		
		/*if( contigs.size() <= 3 ) {
			int max = 0;
			Contig chromosome = null;
			for( Contig ctg : contigs ) {
				if( ctg.getGeneCount() > max ) {
					max = ctg.getGeneCount();
					chromosome = ctg;
				}
			}
			
			ptotal = total - chromosome.getGeneCount();
			total = chromosome.getGeneCount();
		}*/
	}
	
	JRadioButtonMenuItem	relcol;
	JRadioButtonMenuItem	oricol;
	JRadioButtonMenuItem	gccol;
	JRadioButtonMenuItem	gcskewcol;
	JRadioButtonMenuItem	syntcol;
	JRadioButtonMenuItem	brcol;
	JRadioButtonMenuItem	gapcol;
	JRadioButtonMenuItem	syntgrad;
	JRadioButtonMenuItem	isyntgrad;
	
	JCheckBox				contiglanes;
	
	public void comparePlot(  final GeneSet geneset, final Container comp, final List<Gene> genelist, Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap ) throws IOException {
		final JTable 				table = geneset.getGeneTable();
		final Collection<String> 	specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
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
		table2.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		JScrollPane	scroll1 = new JScrollPane( table1 );
		JScrollPane	scroll2 = new JScrollPane( table2 );
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		
		c.add( scroll1 );
		c.add( scroll2 );
		
		JOptionPane.showMessageDialog(comp, c);
		
		int rsel = table1.getSelectedRow();
		String spec1 = rsel != -1 ? (String)table1.getValueAt( rsel, 0 ) : null;
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			String spec2 = (String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final Map<String,Integer>	blosumap = getBlosumMap();
		
		if( spec1 != null ) {
			selectContigs(comp, spec1, geneset);
		} else {
			total = geneset.table.getRowCount();
			ptotal = 0;
		}
		
		final BufferedImage bimg = new BufferedImage( 2048, 2048, BufferedImage.TYPE_INT_ARGB );
		final Graphics2D g2 = bimg.createGraphics();
		draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total, ptotal );
		
		relcol = new JRadioButtonMenuItem("Rel color");
		oricol = new JRadioButtonMenuItem("Ori color");
		gccol = new JRadioButtonMenuItem("GC color");
		gcskewcol = new JRadioButtonMenuItem("GC skew color");
		syntcol = new JRadioButtonMenuItem("Synteni color");
		brcol = new JRadioButtonMenuItem("Breakpoint color");
		gapcol = new JRadioButtonMenuItem("Gap color");
		syntgrad = new JRadioButtonMenuItem("Synteni gradient");
		isyntgrad = new JRadioButtonMenuItem("Inverted synteni gradient");
		ButtonGroup		bg = new ButtonGroup();
		bg.add( relcol );
		bg.add( oricol );
		bg.add( gccol );
		bg.add( gcskewcol );
		bg.add( syntcol );
		bg.add( brcol );
		bg.add( gapcol );
		bg.add( syntgrad );
		bg.add( isyntgrad );
		
		final JComboBox<String>	specombo = new JComboBox<String>();
		for( String spec : specset ) specombo.addItem( spec );
		
		specombo.setSelectedItem( spec1 );
		
		final JComponent cmp = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				//draw( g2 );
				g2.drawImage(bimg, 0, 0, this);
			}
		};
		
		relcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total, ptotal );
				cmp.repaint();
			}
		});
		oricol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -10 );
				cmp.repaint();
			}
		});
		gccol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		gcskewcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		syntcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 1 );
				cmp.repaint();
			}
		});
		brcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 2 );
				cmp.repaint();
			}
		});
		gapcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		syntgrad.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -1 );
				cmp.repaint();
			}
		});
		isyntgrad.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -2 );
				cmp.repaint();
			}
		});
		
		specombo.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//spec1 = (String)e.getItem()
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, relcol.isSelected() ? blosumap : null, total, ptotal );
				cmp.repaint();
			}
		});
		
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Repaint") {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaintCompare(g2, bimg, spec2s, specombo, geneset, blosumap, cmp);
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean succ = true;
				try {
					ImageIO.write(bimg, "png", new File("c:/cir.png") );
				} catch(Exception e1) {
					succ = false;
					e1.printStackTrace();
				}
				
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bimg, "png", baos);
					baos.close();
					String b64str = Base64.encodeBase64String( baos.toByteArray() );
					
					JSObject window;
					
					//JSObject window = JSObject.getWindow(geneset);
					//window.call( "string2Blob", new Object[] {b64str, "image/png"} );
				} catch(Exception e1) {
					succ = false;
					e1.printStackTrace();
				}
				
				if( !succ ) {
					/*FileSaveService fss = null;
			        FileContents fileContents = null;
			    	 
			        try {
			        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
				        //OutputStreamWriter	osw = new OutputStreamWriter( baos );
						ImageIO.write(bimg, "png", baos);
						baos.close();

				    	try {
				    		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
				    	} catch( UnavailableServiceException e1 ) {
				    		fss = null;
				    	}
				    	 
				        if (fss != null) {
				        	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
				            fileContents = fss.saveFileDialog(null, null, bais, "export.png");
				            bais.close();
				            OutputStream os = fileContents.getOutputStream(true);
				            os.write( baos.toByteArray() );
				            os.close();
				        }
			        } catch( Exception e1 ) {
			        	e1.printStackTrace();
			        }*/
				}
			}
		});
		cmp.setComponentPopupMenu( popup );
		
		final int size = total+ptotal;
		cmp.addMouseListener( new MouseListener() {
			Point p;
			boolean doubleclicked = false;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point np = e.getPoint();
				if( p != null ) {
					doubleclicked = doubleclicked || e.getClickCount() == 2;
					
					double ndx = np.x-bimg.getWidth()/2;
					double ndy = np.y-bimg.getHeight()/2;
					
					double dx = p.x-bimg.getWidth()/2;
					double dy = p.y-bimg.getHeight()/2;
					
					if( doubleclicked ) {						
						double t = Math.atan2( dy, dx );
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
								Tegeval tv = c.tlist.get(mloc-loc);
								Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
								
								if( ti != null && ti.best != null ) {
									Contig ct1 = ti.best.getContshort();
									
									tv = c.tlist.get(nloc-loc);
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
						
						repaintCompare( g2, bimg, spec2s, specombo, geneset, blosumap, cmp );
					} else {						
						double t1 = Math.atan2( dy, dx );
						double t2 = Math.atan2( ndy, ndx );
						
						double rad = Math.sqrt( dx*dx + dy*dy );
						
						if( t1 < 0 ) t1 += Math.PI*2.0;
						if( t2 < 0 ) t2 += Math.PI*2.0;
						
						int loc1 = (int)(t1*size/(2*Math.PI));
						int loc2 = (int)(t2*size/(2*Math.PI));
						
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
							Tegeval tv1 = c.tlist.get(minloc-loc);
							Tegeval tv2 = c.tlist.get(maxloc-loc);
							
							int from = Math.min( tv1.start, tv2.start );
							int to = Math.max( tv1.stop, tv2.stop );
							String seqstr = c.getSubstring( from, to, 1 );
						
							Sequence seq = new Sequence("phage_"+from+"_"+to, null);
							seq.append( seqstr );
							geneset.showSomeSequences( geneset, Arrays.asList( new Sequence[] {seq} ) );
						} else {
							if( c == null ) {
								geneset.table.clearSelection();
								for( int k = minloc; k < maxloc; k++ ) {
									geneset.table.addRowSelectionInterval(k, k);
								}
							} else for( int k = minloc; k < maxloc; k++ ) {
								if( k-loc >= c.getGeneCount() ) {
									loc += c.getGeneCount();
									i++;
									c = contigs.get( i%contigs.size() );
								}
								Tegeval tv = c.isReverse() ? c.tlist.get( c.tlist.size()-1-(k-loc) ) : c.tlist.get(k-loc);
								if( e.isShiftDown() ) {
									Set<GeneGroup>	gset = new HashSet<GeneGroup>();
									gset.add( tv.getGene().getGeneGroup() );
									try {
										new Neighbour().neighbourMynd( geneset, comp, genelist, gset, geneset.contigmap );
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
										String spec = spec2s.get( (int)((rad-250.0)/15.0) );
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
		/*cmp.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				
			}
		});*/
		
		Dimension dim = new Dimension( 2048, 2048 );
		cmp.setPreferredSize( dim );
		cmp.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( cmp );
		
		JToolBar	tb = new JToolBar();
		tb.add( specombo );
		
		JMenu		toolbar = new JMenu("Color");
		toolbar.add( relcol );
		toolbar.add( oricol );
		toolbar.add( gccol );
		toolbar.add( gcskewcol );
		toolbar.add( syntcol );
		toolbar.add( brcol );
		toolbar.add( gapcol );
		toolbar.add( syntgrad );
		toolbar.add( isyntgrad );
		
		JMenuBar	mbr = new JMenuBar();
		mbr.add( toolbar );
		tb.add( mbr );
		
		contiglanes = new JCheckBox("Show contig lanes");
		contiglanes.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				repaintCompare(g2, bimg, spec2s, specombo, geneset, blosumap, cmp);
			}
		});
		tb.add( contiglanes );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( tb, BorderLayout.NORTH );
		panel.add( scrollpane );
		
		JFrame frame = new JFrame();
		frame.add( panel );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void repaintCompare( Graphics2D g2, BufferedImage bimg, List<String> spec2s, JComboBox<String> specombo, GeneSet geneset, Map<String,Integer> blosumap, JComponent cmp ) {
		String spec1 = (String)specombo.getSelectedItem();
		rearrangeContigs(spec1, geneset);
		if( relcol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total, ptotal );
		} else if( gccol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
		} else if( gcskewcol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
		} else if( syntcol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 1 );
		} else if( brcol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 2 );
		} else if( gapcol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
		} else if( syntgrad.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -1 );
		} else if( isyntgrad.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -2 );
		} else if( oricol.isSelected() ) {
			draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -10 );
		}
		
		cmp.repaint();
	}
	
	public void draw( Graphics2D g2, String spec1, GeneSet geneset, int w, int h, Collection<Contig> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total, int ptotal ) {
		draw( g2, spec1, geneset, w, h, contigs, spec2s, blosumap, total, ptotal, 0 );
	}
	
	public static double blosumVal( Sequence seq, String spec2, GeneGroup gg, Map<String,Integer> blosumap ) {
		double ret = 0.0;
		if( seq != null ) {
			int tscore = 0;
            for( int i = 0; i < seq.length(); i++ ) {
            	char lc = seq.charAt(i);
            	char c = Character.toUpperCase( lc );
            	//if( )
            	String comb = c+""+c;
            	if( blosumap.containsKey(comb) ) tscore += blosumap.get(comb);
            }
            
            int score = 0;
            Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
                Sequence seq2 = tv2.getAlignedSequence();
                
                int sscore = 0;
                for( int i = 0; i < Math.min( seq.length(), seq2.length() ); i++ ) {
                	char lc = seq.charAt( i );
                	char c = Character.toUpperCase( lc );
                	char lc2 = seq2.charAt( i );
                	char c2 = Character.toUpperCase( lc2 );
                	
                	String comb = c+""+c2;
                	if( blosumap.containsKey(comb) ) sscore += blosumap.get(comb);
                }
                if( sscore > score ) score = sscore;
                
                if( seq == seq2 && sscore != tscore ) {
                	System.err.println();
                }
            }
            ret = (double)score/(double)tscore; //int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
		}
		return ret;
	}
	
	public static Color blosumColor( Sequence seq, String spec2, GeneGroup gg, Map<String,Integer> blosumap, boolean rs ) {
		Color color = Color.green;
		if( seq != null && seq.length() > 0 ) {
			int tscore = 0;
            for( int i = 0; i < seq.length(); i++ ) {
            	char lc = seq.charAt(i);
            	char c = Character.toUpperCase( lc );
            	//if( )
            	String comb = c+""+c;
            	if( blosumap.containsKey(comb) ) tscore += blosumap.get(comb);
            }
            
            /*if( gg.getCommonName().contains("tRNA-Ile") ) {
            	System.err.println(gg.getCommonName());
            }*/
            
            int score = 0;
            Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
                Sequence seq2 = tv2.getAlignedSequence();
                
                int sscore = 0;
                for( int i = 0; i < Math.min( seq.length(), seq2.length() ); i++ ) {
                	char lc = seq.charAt( i );
                	char c = Character.toUpperCase( lc );
                	char lc2 = seq2.charAt( i );
                	char c2 = Character.toUpperCase( lc2 );
                	
                	String comb = c+""+c2;
                	if( blosumap.containsKey(comb) ) sscore += blosumap.get(comb);
                }
                if( sscore > score ) score = sscore;
                
                if( seq == seq2 && sscore != tscore ) {
                	//System.err.println();
                }
            }
            int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
            color = rs ? new Color( 255, cval, cval ) : new Color( cval, cval, cval );
		} else {
			Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
            	if( tv2.getGene().tag != null )
            		color = tv2.getGene().tag.equals("rrna") ? Color.red : Color.blue;
            	else color = Color.green;
            	break;
            }
		}
		
		return color;
	}
	
	static int ctgoff = 0;
	public static Color gradientColor( String spec1, String spec2, List<Contig> contigs2, double ratio, double pratio, int offset2, GeneGroup gg, boolean contiglanes ) {
		//Contig chromosome = null;
		int total2 = 0;
		int ptotal2 = 0;
		
		int chromstart = -1;
		for( Contig ctg2 : contigs2 ) {
			if( ctg2.isPlasmid() ) ptotal2 += ctg2.getGeneCount();
			else {
				if( chromstart == -1 ) chromstart = ptotal2;
				total2 += ctg2.getGeneCount();
			}
		}
		
		/*int tot2 = 0;
		int ptot2 = 0;
		for( Contig ctg2 : contigs2 ) {
			tot2 += ctg2.getGeneCount();
		}
		
		int chromstart = 0;//total2;
		if( contigs2.size() <= 3 ) {
			int max = 0;
			int ccount = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.getGeneCount() > max ) {
					chromosome = ctg2;
					chromstart = ccount;
					max = ctg2.getGeneCount();
				}
				ccount += ctg2.getGeneCount();
			}
			
			ptot2 = tot2 - max;
			tot2 = max;
		}*/
		
		double ratio2 = -1.0;
		double pratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		
		Contig hit = null;
		for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			
			if( ptotal2 > 0 ) {
				for( Contig ctg2 : contigs2 ) {
					if( ctg2.tlist != null ) {
						int idx = ctg2.tlist.indexOf( tv2 );
						if( idx != -1 ) {
							hit = ctg2;
							break;
						}
					}
				}
				
				if( hit != null && hit.isPlasmid() ) {
					for( Contig c2 : contigs2 ) {
						if( c2.isPlasmid() && c2.tlist != null ) {
							int idx = c2.tlist.indexOf( tv2 );
							if( idx == -1 ) {
								count2 += c2.getGeneCount();
							} else {
								count2 += c2.isReverse() ? c2.getGeneCount() - idx - 1 : idx;
								break;
							}
						}
					}
					double prat2 = (double)count2/(double)ptotal2;
					if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
				} else {
					for( Contig c2 : contigs2 ) {
						if( c2.tlist != null ) {
							int idx = c2.tlist.indexOf( tv2 );
							if( idx == -1 ) {
								count2 += c2.getGeneCount();
							} else {
								count2 += c2.isReverse() ? c2.getGeneCount() - idx - 1 : idx;
								hit = c2;
								break;
							}
						}
					}
				}
				
				/*if( chromosome.tlist != null ) {
					int idx = chromosome.tlist.indexOf( tv2 );
					if( idx == -1 ) {
						for( Contig ctg2 : contigs2 ) {
							if( ctg2 != chromosome && ctg2.tlist != null ) {
								idx = ctg2.tlist.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getGeneCount();
								} else {
									count2 += ctg2.isReverse() ? ctg2.getGeneCount() - idx - 1 : idx; 
									//count2 += idx;
									break;
								}
							}
						}
						double prat2 = (double)count2/(double)ptotal2;
						if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
					} else count2 = chromosome.isReverse() ? chromosome.getGeneCount() - idx - 1 : idx;
				}*/
			} else {
				for( Contig ctg2 : contigs2 ) {
					if( ctg2.tlist != null ) {
						int idx = ctg2.tlist.indexOf( tv2 );
						if( idx == -1 ) {
							count2 += ctg2.getGeneCount();
						} else {
							count2 += ctg2.isReverse() ? ctg2.getGeneCount() - idx - 1 : idx;
							hit = ctg2;
							break;
						}
					}
				}
			}
			
			int val2 = count2 - offset2;
			if( val2 < 0 ) val2 = total2 + val2;
			
			double rat2 = (double)val2/(double)total2;
			
			if( rat2 > 1.0f || rat2 < 0.0f ) {
				System.err.println("");
			}
			
			/*if( spec1.equals(spec2) && ratio2 != -1 ) {
				System.err.println( "erm " + ratio );
			}*/
			
			if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) {
				ratio2 = rat2;
			}
			//ratio2 = rat2;
			//break;
		}
		
		if( contiglanes && !spec1.equals(spec2) ) ctgoff = contigs2.indexOf( hit );
		
		/*if( spec1.equals(spec2) && gene2s.tset.size() > 2 && pratio == 0 && ratio != ratio2 ) {
			System.err.println( "raterm " + ratio + "  " + ratio2 );
		}*/
		
		//float green = (float)(1.0-ratio2);
		/*if( ratio2 < 0.5 ) {
			Color c = new Color(0,(float)(ratio2*2.0),1.0f);
			g2.setColor( c );
		} else {
			Color c = new Color(0,1.0f,(float)((1.0-ratio2)*2.0));
			g2.setColor( c );
		}*/
		
		Color c = Color.RED;
		if( pratio2 != -1.0 && (pratio != 0.0 || !spec1.equals(spec2)) ) {
			c = gradientGrayscaleColor( pratio2 );
			/*float val = (float)(9.0f*pratio2/10.0f);
			if( val >= 0.0f && val <= 1.0f ) {
				if( val > 9.0f/20.0f ) {
					c = new Color(18.0f/10.0f-val*2.0f,18.0f/10.0f-2.0f*val,18.0f/10.0f-2.0f*val);
				} else {
					c = new Color(val*2.0f,val*2.0f,val*2.0f);
				}
			}*/
			//System.err.println( pratio + "  " + pratio2 );
		} else if( ratio2 >= 0 ) {
			/*if( ratio2 < 1.0/6.0 ) {
				c = new Color(0.0f,(float)(ratio2*6.0),1.0f);
			} else if( ratio2 < 2.0/6.0 ) {
				c = new Color(0.0f,1.0f,(float)((2.0/6.0-ratio2)*6.0));
			} else if( ratio2 < 3.0/6.0 ) {
				c = new Color((float)((ratio2-2.0/6.0)*6.0),1.0f,0.0f);
			} else if( ratio2 < 4.0/6.0 ) {
				c = new Color(1.0f,(float)((4.0/6.0-ratio2)*6.0),0.0f);
			} else if( ratio2 < 5.0/6.0 ) {
				c = new Color(1.0f,0.0f,(float)((ratio2-4.0/6.0)*6.0));
			} else if( ratio2 <= 1.0 ) {
				c = new Color((float)((1.0-ratio2)*6.0),0.0f,1.0f);
			} else {
				System.err.println();
			}*/
			c = gradientColor( ratio2 );
		}
		
		return c;
	}
	
	public static double invertedGradientPlasmidRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			if( ctg2.isPlasmid() ) total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		if( gene2s != null && gene2s.tset != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.isPlasmid() && ctg2.tlist != null ) {
					int idx = ctg2.tlist.indexOf( tv2 );
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
	
	public static double invertedGradientRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			if( !ctg2.isPlasmid() ) total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		if( gene2s != null && gene2s.tset != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.tlist != null ) {
					int idx = ctg2.tlist.indexOf( tv2 );
					if( idx == -1 ) {
						count2 += ctg2.getGeneCount();
					} else {
						if( ctg2.isPlasmid() ) return -1;
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
	
	public static double invertedGradientTotalRatio( String spec2, Collection<Contig> contigs2, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Contig ctg2 : contigs2 ) {
			total2 += ctg2.getGeneCount();
		}
		double ratio2 = -1.0;
		
		if( gg == null || spec2 == null ) {
			System.err.println("ermermermermerm");
		}
		Teginfo gene2s = gg.getGenes( spec2 );
		if( gene2s != null && gene2s.tset != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.tlist != null ) {
					int idx = ctg2.tlist.indexOf( tv2 );
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
	
	public static Color gradientGrayscaleColor( double ratio ) {		
		//float green = (float)(1.0-ratio2);
		/*if( ratio2 < 0.5 ) {
			Color c = new Color(0,(float)(ratio2*2.0),1.0f);
			g2.setColor( c );
		} else {
			Color c = new Color(0,1.0f,(float)((1.0-ratio2)*2.0));
			g2.setColor( c );
		}*/
		
		Color c = Color.white;
		if( ratio >= 0.0 ) {
			float val = (float)(9.0f*ratio/10.0f);
			if( val >= 0.0f && val <= 1.0f ) {
				if( val > 9.0f/20.0f ) {
					c = new Color(18.0f/10.0f-val*2.0f,18.0f/10.0f-2.0f*val,18.0f/10.0f-2.0f*val);
				} else {
					c = new Color(val*2.0f,val*2.0f,val*2.0f);
				}
			}
		}
		
		return c;
	}
	
	public static Color gradientColor( double ratio ) {		
		//float green = (float)(1.0-ratio2);
		/*if( ratio2 < 0.5 ) {
			Color c = new Color(0,(float)(ratio2*2.0),1.0f);
			g2.setColor( c );
		} else {
			Color c = new Color(0,1.0f,(float)((1.0-ratio2)*2.0));
			g2.setColor( c );
		}*/
		
		Color c = Color.white;
		if( ratio >= 0.0 ) {
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
			} else if( ratio <= 1.0 ) {
				c = new Color((float)((1.0-ratio)*6.0),0.0f,1.0f);
			}
		}
		
		return c;
	}
	
	public void draw( Graphics2D g2, String spec1, GeneSet geneset, int w, int h, Collection<Contig> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total, int ptotal, int synbr ) {
		boolean contiglanesb = contiglanes != null && contiglanes.isSelected();
		/*g.setColor( Color.black );
		int count = 0;
		for( Contig ctg : contigs ) {
			for( Tegeval tv : ctg.tlist ) {
				GeneGroup gg = tv.getGene().getGeneGroup();
				int scount = 0;
				for( String spec2 : spec2s ) {
					if( gg.species.containsKey(spec2) ) {
                        Gene gene2 = gg.getGene( spec2 );
                        Tegeval tv2;
                        for( Tegeval tval : gene2.teginfo.tset ) {
                            tv2 = tval;
                            break;
                        }
                        StringBuilder seq = tv.getAlignedSequence();
                        StringBuilder seq2 = tv2.getAlignedSequence();
						double theta = count*Math.PI*2.0/total;
						g2.translate( 1024, 1024 );
						g2.rotate( theta );
						g2.fillRect( 502+20*(scount++), -1, 20, 3);
						g2.rotate( -theta );
                        g2.translate( -1024, -1024 );
					}
				}
				count++;
		}*/
		
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        g2.setBackground( Color.white );
		g2.clearRect( 0, 0, w, h );
		g2.setColor( Color.black );
		if( spec1 == null ) {
			int rowcount = geneset.table.getRowCount();
			for( int r = 0; r < rowcount; r++ ) {
				int i = geneset.table.convertRowIndexToModel(r);
				GeneGroup gg = geneset.allgenegroups.get( i );
				subDraw(g2, null, null, geneset, spec1, r, null, null, rowcount, spec2s, synbr, w, h, blosumap, i, gg, null);
			}
		} else {
			Map<String,Integer>	offsetMap = new HashMap<String,Integer>();
			int r = geneset.table.getSelectedRow();
			if( r >= 0 && r < geneset.table.getRowCount() ) {
				int i = geneset.table.convertRowIndexToModel(r);
				
				if( geneset.table.getModel() == geneset.groupModel ) {
					GeneGroup gg = geneset.allgenegroups.get( i );
					
					for( String spec2 : spec2s ) {
						if( gg.species.containsKey(spec2) ) {
							final Collection<Contig> contigs2 = spec1.equals(spec2) ? contigs : geneset.speccontigMap.get( spec2 );
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
								System.err.println( spec2 + "   " + count2 );
								offsetMap.put(spec2, count2);
							}
						}
					}
				} else {
					GeneGroup gg = geneset.genelist.get(i).getGeneGroup();
					
					for( String spec2 : spec2s ) {
						if( gg.species.containsKey(spec2) ) {
							final Collection<Contig> contigs2 = spec1.equals(spec2) ? contigs : geneset.speccontigMap.get( spec2 );
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
				}
			}
			
			int count = 0;
			int pcount = 0;
			int current = 0;
			for( Contig ctg : contigs ) {
				Tegeval prev = null;
				if( ctg.tlist != null ) {
					current = count;
					if( ctg.isReverse() ) {
						for( int i = ctg.tlist.size()-1; i >= 0; i-- ) {
							Tegeval tv = ctg.tlist.get( i );
							Sequence seq = tv.getAlignedSequence();
							GeneGroup gg = tv.getGene().getGeneGroup();
							
							int ii = geneset.allgenegroups.indexOf( gg );
							if( ii >= 0 && ii < geneset.table.getRowCount() ) {
								subDraw( g2, tv, prev, geneset, spec1, count, offsetMap, ctg, r, spec2s, synbr, w, h, blosumap, ii, gg, seq );
								count++;
								prev = tv;
							}
						}
					} else {
						for( Tegeval tv : ctg.tlist ) {
							Sequence seq = tv.getAlignedSequence();
							GeneGroup gg = tv.getGene().getGeneGroup();
							
							int ii = geneset.allgenegroups.indexOf( gg );
							if( ii >= 0 && ii < geneset.table.getRowCount() ) {
								subDraw( g2, tv, prev, geneset, spec1, count, offsetMap, ctg, r, spec2s, synbr, w, h, blosumap, ii, gg, seq );
								count++;
								prev = tv;
							}
						}
					}
				
					double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					int x;
					if( !contiglanesb ) {
						g2.setColor( Color.black );
						x = 250+15*spec2s.size();
						g2.drawLine( x, 0, x+15, 0);
					} else {
						g2.setColor( Color.darkGray );
						x = 135;
						g2.drawLine( x, 0, x+1000, 0);
					}
					g2.rotate( -theta );
		            g2.translate( -w/2, -h/2 );
				}
			}
			g2.setColor( Color.black );
			
			Font oldfont = g2.getFont().deriveFont( Font.ITALIC ).deriveFont(21.0f);
			g2.setFont( oldfont );
			//String[] specsplit = ;
			/*if( spec1.contains("hermus") ) specsplit = spec1.split("_");
			else {
				Matcher m = Pattern.compile("\\d").matcher(spec1); 
				int firstDigitLocation = m.find() ? m.start() : 0;
				if( firstDigitLocation == 0 ) specsplit = new String[] {"Thermus", spec1};
				else specsplit = new String[] {"Thermus", spec1.substring(0,firstDigitLocation), spec1.substring(firstDigitLocation)};
			}*/
			
			//g2.setFont( g2.getFont().deriveFont(18) );
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
				g2.drawString( specstr, (w-strw)/2, h/2 - spec2s.size()*23/2 + 23 + k*23 );
				k++;
			}
		}
	}
	
	public void subDraw( Graphics2D g2, Tegeval tv, Tegeval prev, GeneSet geneset, String spec1, int count, Map<String,Integer> offsetMap, Contig ctg, int r, List<String> spec2s, int synbr, int w, int h, Map<String,Integer> blosumap, int ii, GeneGroup gg, Sequence seq ) {
		boolean rs = false;
		if( geneset.table.getModel() == geneset.groupModel ) {
			r = geneset.table.convertRowIndexToView( ii );
			rs = geneset.table.isRowSelected( r );
		} else {
			for( Gene g : gg.genes ) {
				r = geneset.table.convertRowIndexToView( g.index );
				rs = geneset.table.isRowSelected( r );
				if( rs ) break;
			}
		}
		
		/*if( rs ) {
			System.err.println();
		}*/
		
		boolean contiglanesb = contiglanes != null && contiglanes.isSelected();
		int offset = 0;
		if( spec1 != null && offsetMap.containsKey(spec1) ) offset = offsetMap.get(spec1);
		
		double ratio = 0.0;
		double pratio = 0.0;
		double tratio = 0.0;
		if( ptotal > 0 ) {
			if( !ctg.isPlasmid() ) {
				int val = count - offset;
				if( val < 0 ) {
					val = total + (count-offset);
				}
				
				ratio = (double)(val)/(double)total;
				
				if( ratio > 1.0 ) {
					System.err.println();
				}
			} else {
				if( count - total >= 0 ) {
					pratio = (double)(count-total)/(double)ptotal;
				} else {
					pratio = (double)(count)/(double)ptotal;
				}
			}
			
			/*if( ctg.getGeneCount() == total ) {
				int val = count - offset;
				if( val < 0 ) val = total + (count-offset);
				
				ratio = (double)(val-current)/(double)total;
			} else {
				if( count - total >= 0 ) {
					pratio = (double)(count-total)/(double)ptotal;
				} else {
					pratio = (double)(count)/(double)ptotal;
				}
			}*/
		} else {
			int val = count - offset;
			if( val < 0 ) val = total + (count-offset);
			
			ratio = (double)val/(double)total;
		}
		
		tratio = (double)(count - offset)/(double)(total + ptotal);
		
		/*if( ratio == 0.0 ) {
			System.err.println();
		}*/
		
		//System.err.println( ratio );
		
		int scount = 0;
		for( String spec2 : spec2s ) {
			final List<Contig> contigs2;
			boolean speceq = false;
			if( spec1 != null ) speceq = spec1.equals(spec2);
			contigs2 = speceq ? contigs : geneset.speccontigMap.get( spec2 );
			
			if( gg.species.containsKey(spec2) ) {
				int offset2 = 0;
				if( offsetMap != null && offsetMap.containsKey( spec2 ) ) offset2 = offsetMap.get(spec2);
				if( synbr == -10 ) {
					//final Collection<Contig> contigs2 = spec1.equals(spec2) ? contigs : geneset.speccontigMap.get( spec2 );
					
					Teginfo gene2s = gg.getGenes( spec2 );
                    for( Tegeval tv2 : gene2s.tset ) {
                    	g2.setColor( tv2.ori == -1 ? Color.red : Color.blue );
                    	break;
                    }
					//double ratio2 = invertedGradientRatio( spec2, contigs2, ratio, gg );
					//Color c = invertedGradientColor( ratio );
					
                    double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					g2.fillRect( 250+15*(scount), -1, 15, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
				} else if( synbr == -2 ) {
					if( spec1 == null ) {
						ratio = GeneCompare.invertedGradientRatio(spec2, contigs2, -1.0, gg);
						if( ratio == -1 ) {
							ratio = GeneCompare.invertedGradientPlasmidRatio(spec2, contigs2, -1.0, gg);
							g2.setColor( GeneCompare.gradientGrayscaleColor( ratio ) );
						} else {
							g2.setColor( GeneCompare.gradientColor( ratio ) );
						}
						
						double theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( 250+15*(scount), -1, 15, 3);
						g2.rotate( -theta );
	                    g2.translate( -w/2, -h/2 );
					} else {
						Color c;
						if( ptotal > 0 ) {
							//double pratio2 = invertedGradientPlasmidRatio( spec2, contigs2, pratio, gg );
							c = gradientGrayscaleColor( pratio );
						} else {
							//double ratio2 = invertedGradientRatio( spec2, contigs2, ratio, gg );
							c = gradientColor( ratio );
						}
						g2.setColor( c );
						
						double ratio2 = invertedGradientTotalRatio( spec2, contigs2, tratio, gg );
						if( ratio2 != -1.0 ) {
							double theta = ratio2*Math.PI*2.0;
							g2.translate( w/2, h/2 );
							g2.rotate( theta );
							g2.fillRect( 250+15*(scount), -1, 15, 3);
							g2.rotate( -theta );
		                    g2.translate( -w/2, -h/2 );
						}
					}
				} else if( synbr == -1 ) {
					//System.err.println( ratio );
					ctgoff = 0;
					Color c = gradientColor( spec1, spec2, contigs2, ratio, pratio, offset2, gg, contiglanesb );
					g2.setColor( c );
					
					double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					if( contiglanesb ) g2.fillRect( 150+15*(scount)+15*ctgoff, -1, 15, 2);
					else g2.fillRect( 250+15*(scount), -1, 15, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
                    
                    if( gg.species.size() == 1 ) {
                    	g2.setColor( Color.black );
                    	 
                    	theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( contiglanesb ? 100 : 200, 0, 7, 1 );
						g2.rotate( -theta );
	                    g2.translate( -w/2, -h/2 );
                    }
				} else if( synbr > 0 ) {
					if( prev != null ) {
						Teginfo gene2s = gg.getGenes( spec2 );
						Color c = null;
                        for( Tegeval tv2 : gene2s.tset ) {
                        	GeneGroup fwgg = tv2.getNext() != null ? tv2.getNext().getGene().getGeneGroup() : null;
                        	GeneGroup bkgg = tv2.getPrevious() != null ? tv2.getPrevious().getGene().getGeneGroup() : null;
                        	
                        	if( prev.getGene().getGeneGroup().equals( bkgg ) ) {
                        		c = Color.blue;
                        	} else if( prev.getGene().getGeneGroup().equals( fwgg ) ) {
                        		if( c == null ) c = Color.red;
                        	}
                        }
                        
                        if( (synbr < 2 && c != null) || (synbr == 2) ) {
                        	if( synbr == 2 ) {
                        		if( c == null ) g2.setColor( Color.red );
                        		else g2.setColor( Color.lightGray );
                        	}
                        	else g2.setColor( c );
	                        
							double theta = count*Math.PI*2.0/(total+ptotal);
							g2.translate( w/2, h/2 );
							g2.rotate( theta );
							g2.fillRect( 250+15*(scount), -1, 15, 3);
							g2.rotate( -theta );
		                    g2.translate( -w/2, -h/2 );
                        }
					}
				} else {
					Color color = Color.green;
					if( spec1 != null ) {
						if( blosumap != null ) {
							color = blosumColor(seq, spec2, gg, blosumap, rs);
						} else if( gcskewcol.isSelected() ) {
							Teginfo gene2s = gg.getGenes( spec2 );
	                        for( Tegeval tv2 : gene2s.tset ) {
	                        	color = tv2.getGCSkewColor();
	                        	break;
	                        }
						} else if( gapcol.isSelected() ) {
							Teginfo gene2s = gg.getGenes( spec2 );
	                        for( Tegeval tv2 : gene2s.tset ) {
	                        	color = tv2.getFrontFlankingGapColor();
	                        	break;
	                        }
						} else {
							Teginfo gene2s = gg.getGenes( spec2 );
	                        for( Tegeval tv2 : gene2s.tset ) {
	                        	color = tv2.getGCColor();
	                        	break;
	                        }
						}
					} else {
						boolean phage = false;//gg.isInAnyPhage();
						boolean plasmid = false;//gg.isOnAnyPlasmid();
						
						Teginfo ti = gg.getGenes( spec2 );
						for( Tegeval tv2 : ti.tset ) {
                        	phage |= tv2.getGene().isPhage();
                        	plasmid |= tv2.getContshort().isPlasmid();
                        }
						
						if( phage && plasmid ) {
							if( ti.tset.size() > 1 ) color = darkmag;
							else color = Color.magenta;
						} else if( phage ) {
							if( ti.tset.size() > 1 ) color = darkblue;
							else color = Color.blue;
						} else if( plasmid ) {
							if( ti.tset.size() > 1 ) color = darkred;
							else color = Color.red;
						} else {
							if( ti.tset.size() > 1 ) color = Color.gray;
							else color = Color.lightGray;
						}
					}
					if( color != null ) g2.setColor( color );
                    
					double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					g2.fillRect( 250+15*(scount), -1, 15, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
                    
                    if( gg.species.size() == 1 ) {
                    	g2.setColor( Color.black );
                    	 
                    	theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( 200, 0, 15, 1);
						g2.rotate( -theta );
	                    g2.translate( -w/2, -h/2 );
                    }
				}
			}
			if( !speceq && contiglanesb ) {
				scount += contigs2.size();
			} else scount++;
		}
	}
	
	final Color darkgreen = new Color( 0, 128, 0 );
	final Color darkred = new Color( 128, 0, 0 );
	final Color darkblue = new Color( 0, 0, 128 );
	final Color darkmag = new Color( 128, 0, 128 );
}
