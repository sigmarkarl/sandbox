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
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.simmi.shared.Annotation;
import org.simmi.shared.Contig;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;
import org.simmi.shared.Tegeval;
import org.simmi.shared.Teginfo;
import org.simmi.unsigned.JavaFasta;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import netscape.javascript.JSObject;

public class GeneCompare {
	List<Sequence> contigs;
	
	public void rearrangeContigs( String spec1, GeneSet geneset ) {
		if( spec1 != null && geneset.speccontigMap.containsKey( spec1 ) ) {
			final List<Sequence> lcont = geneset.speccontigMap.get( spec1 );
			
			List<Sequence> newcontigs = new ArrayList<Sequence>( lcont );
			/*List<Sequence> newcontigs = new ArrayList<Sequence>();
			for( Sequence c : lcont ) {
				if( contigs.contains( c ) ) newcontigs.add( c );
			}
			contigs = newcontigs;*/
			
			contigs = newcontigs;
		}
	}
	
	int total = 0;
	int ptotal = 0;
	public void selectContigs( Container comp, String spec1, GeneSet geneset ) {
		final List<Sequence> lcont = geneset.speccontigMap.get( spec1 );
		
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
		contigs = new ArrayList<Sequence>();
		for( int r : cseltable.getSelectedRows() ) {
			int i = cseltable.convertRowIndexToModel(r);
			Sequence ctg = lcont.get( i );
			contigs.add( ctg );
			if( ctg.isPlasmid() ) ptotal += ctg.getAnnotationCount();
			else total += ctg.getAnnotationCount();
		}
		
		/*if( contigs.size() <= 3 ) {
			int max = 0;
			Contig chromosome = null;
			for( Contig ctg : contigs ) {
				if( ctg.getAnnotationCount() > max ) {
					max = ctg.getAnnotationCount();
					chromosome = ctg;
				}
			}
			
			ptotal = total - chromosome.getAnnotationCount();
			total = chromosome.getAnnotationCount();
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
	List<String>			species;
	
	int[]		currentRowSelection;
	public TransferHandler dragRows( final JTable table, final List<String> specs ) {
		TransferHandler th = null;
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<String>	selseq = new ArrayList<String>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( specs.get(i) );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						for( int r = 0; r < table.getRowCount(); r++ ) {
							Object o = table.getValueAt(r, 0);
							if( o != null ) {
								ret += o.toString();
							} else {
								ret += "";
							}
							for( int c = 1; c < table.getColumnCount(); c++ ) {
								o = table.getValueAt(r, c);
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
			
			th = new TransferHandler() {
				private static final long serialVersionUID = 1L;
				
				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}

				public boolean canImport(TransferHandler.TransferSupport support) {					
					return true;
				}

				protected Transferable createTransferable(JComponent c) {
					currentRowSelection = table.getSelectedRows();
					
					return transferable;
				}

				public boolean importData(TransferHandler.TransferSupport support) {
					try {
						System.err.println( table.getSelectedRows().length );
						
						DataFlavor[] dfs = support.getDataFlavors();
						if( support.isDataFlavorSupported( ndf ) ) {					
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<String>	seqs = (ArrayList<String>)obj;
							
							/*ArrayList<String> newlist = new ArrayList<String>( serifier.lgse.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( specs.get(i) );
							}
							serifier.lgseq.clear();
							serifier.lgseq = newlist;*/
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							specs.removeAll( seqs );
							for( String s : seqs ) {
								specs.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							
							return true;
						}/* else if( support.isDataFlavorSupported( df ) ) {							
							Object obj = support.getTransferable().getTransferData( df );
							InputStream is = (InputStream)obj;
							
							System.err.println( charset );
							importReader( new BufferedReader(new InputStreamReader(is, charset)) );
							
							updateView();
							
							return true;
						}  else if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {							
							Object obj = support.getTransferable().getTransferData( DataFlavor.stringFlavor );
							String str = (String)obj;
							importReader( new BufferedReader( new StringReader(str) ) );
							
							updateView();
							
							return true;
						}*/
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return th;
	}
	
	public void comparePlot(  final GeneSetHead genesethead, final Container comp, final List<Gene> genelist, Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap, int w, int h ) throws IOException {
		final GeneSet geneset = genesethead.geneset;
		
		final TableView<Gene> 				table = genesethead.getGeneTable();
		final Collection<String> 	specset = geneset.getSpecies(); //speciesFromCluster( clusterMap );
		species = new ArrayList<String>( specset );
		
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
		
		FlowLayout flowlayout = new FlowLayout();
		JComponent c = new JComponent() {};
		c.setLayout( flowlayout );
		
		c.add( scroll1 );
		c.add( scroll2 );
		
		table2.setDragEnabled( true );
		
		TransferHandler th = dragRows( table2, species );
		scroll2.setTransferHandler( th );
		table2.setTransferHandler( th );
		
		JOptionPane.showMessageDialog(comp, c);
		
		int rsel = table1.getSelectedRow();
		//String spec1 = rsel != -1 ? (String)table1.getValueAt( rsel, 0 ) : null;
		int i = table1.convertRowIndexToModel(rsel);
		String spec1 = rsel != -1 ? species.get( i ) : null;
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			i = table2.convertRowIndexToModel(r);
			String spec2 = species.get(i); //(String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final Map<String,Integer>	blosumap = JavaFasta.getBlosumMap();
		
		if( spec1 != null ) {
			selectContigs(comp, spec1, geneset);
		} else {
			total = genesethead.getGeneGroupTable().getItems().size();
			ptotal = 0;
		}
		
		final BufferedImage bimg = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		final Graphics2D g2 = bimg.createGraphics();
		draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total, ptotal );
		
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
		specombo.addItem("");
		for( String spec : specset ) specombo.addItem( spec );
		//specombo.addItem("All");
		
		if( spec1 != null ) specombo.setSelectedItem( spec1 );
		//specombo.setSelectedItem( "" );
		
		final JComponent cmp = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				//draw( g2 );
				g2.drawImage(bimg, 0, 0, bimg.getWidth()/2, bimg.getHeight()/2, 0, 0, bimg.getWidth(), bimg.getHeight(), this);
			}
		};
		
		relcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total, ptotal );
				cmp.repaint();
			}
		});
		oricol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -10 );
				cmp.repaint();
			}
		});
		gccol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		gcskewcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		syntcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 1 );
				cmp.repaint();
			}
		});
		brcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, 2 );
				cmp.repaint();
			}
		});
		gapcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal );
				cmp.repaint();
			}
		});
		syntgrad.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -1 );
				cmp.repaint();
			}
		});
		isyntgrad.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, ptotal, -2 );
				cmp.repaint();
			}
		});
		
		specombo.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//spec1 = (String)e.getItem()
				String spec1 = (String)specombo.getSelectedItem();
				//int total = selectContigs( comp, spec1, geneset );
				draw( g2, spec1, genesethead, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, relcol.isSelected() ? blosumap : null, total, ptotal );
				cmp.repaint();
			}
		});
		
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Repaint") {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaintCompare(g2, bimg, spec2s, specombo, genesethead, blosumap, cmp);
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean succ = true;
				try {
					ImageIO.write(bimg, "png", new File("/Users/sigmar/cir.png") );
				} catch(Exception e1) {
					succ = false;
					e1.printStackTrace();
				}
				
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bimg, "png", baos);
					baos.close();
					String b64str = Base64.getEncoder().encodeToString( baos.toByteArray() );
					
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
				if( e.getButton() == MouseEvent.BUTTON1 ) {
					Point np = e.getPoint();
					if( p != null ) {
						doubleclicked = doubleclicked || e.getClickCount() == 2;
						
						double ndx = np.x-bimg.getWidth()/4;
						double ndy = np.y-bimg.getHeight()/4;
						
						double dx = p.x-bimg.getWidth()/4;
						double dy = p.y-bimg.getHeight()/4;
						
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
									Sequence c = contigs.get(i);
									if( loc + c.getAnnotationCount() > mloc ) {
										break;
									} else loc += c.getAnnotationCount();
								}
								Sequence c = contigs.get(i);
								
								if( mloc-loc < c.getAnnotationCount() ) {
									//loc += c.getAnnotationCount();
									//c = contigs.get( i%contigs.size() );
									Tegeval tv = (Tegeval)c.annset.get(mloc-loc);
									Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
									
									if( ti != null && ti.best != null ) {
										Contig ct1 = ti.best.getContshort();
										
										tv = (Tegeval)c.annset.get(nloc-loc);
										ti = tv.getGene().getGeneGroup().getGenes(spec);
										Contig ct2 = ti.best.getContshort();
										
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
							
							repaintCompare( g2, bimg, spec2s, specombo, genesethead, blosumap, cmp );
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
								Tegeval tv1 = (Tegeval)c.annset.get(minloc-loc);
								Tegeval tv2 = (Tegeval)c.annset.get(maxloc-loc);
								
								int from = Math.min( tv1.start, tv2.start );
								int to = Math.max( tv1.stop, tv2.stop );
								String seqstr = c.getSubstring( from, to, 1 );
							
								Serifier serifier = new Serifier();
								Sequence seq = new Sequence("phage_"+from+"_"+to, null);
								seq.append( seqstr );
								serifier.addSequence( seq );
								genesethead.showSomeSequences( genesethead, serifier );
							} else {
								if( c == null ) {
									Platform.runLater(new Runnable() {
										public void run() {
											genesethead.getGeneGroupTable().getSelectionModel().clearSelection();
											for( int k = minloc; k < maxloc; k++ ) {
												genesethead.getGeneGroupTable().getSelectionModel().select(k);
											}
										}
									});
								} else for( int k = minloc; k < maxloc; k++ ) {
									if( k-loc >= c.getAnnotationCount() ) {
										loc += c.getAnnotationCount();
										i++;
										c = contigs.get( i%contigs.size() );
									}
									Tegeval tv = (Tegeval)(c.isReverse() ? c.annset.get( c.annset.size()-1-(k-loc) ) : c.annset.get(k-loc));
									if( e.isShiftDown() ) {
										Set<GeneGroup>	gset = new HashSet<GeneGroup>();
										gset.add( tv.getGene().getGeneGroup() );
										try {
											new Neighbour( gset ).neighbourMynd( genesethead, comp, genelist, geneset.contigmap );
										} catch (IOException e1) {
											e1.printStackTrace();
										}
										break;
									} else {
										if( !genesethead.isGeneview() ) {
											genesethead.getGeneGroupTable().getSelectionModel().select( tv.getGene().getGeneGroup() );
										} else {
											int ind = (int)((rad-250.0)/15.0);
											String spec = spec2s.get( ind );
											Teginfo ti = tv.getGene().getGeneGroup().getGenes(spec);
											if( ti != null && ti.tset != null ) for( Tegeval te : ti.tset ) {
												/*int u = geneset.genelist.indexOf( te.getGene() );
												r = genesethead.table.convertRowIndexToView(u);
												if( selr == -1 ) selr = r;
												genesethead.table.addRowSelectionInterval( r, r );*/
												genesethead.getGeneTable().getSelectionModel().select( te.getGene() );
											}
											//Rectangle rect = genesethead.table.getCellRect(selr, 0, true);
											genesethead.getGeneTable().getSelectionModel().select( ti.best.getGene() );
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
		/*cmp.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				
			}
		});*/
		
		Dimension dim = new Dimension( w/2, h/2 );
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
				//repaintCompare(g2, bimg, spec2s, specombo, geneset, blosumap, cmp);
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
	
	public void repaintCompare( Graphics2D g2, BufferedImage bimg, List<String> spec2s, JComboBox<String> specombo, GeneSetHead geneset, Map<String,Integer> blosumap, JComponent cmp ) {
		String spec1 = (String)specombo.getSelectedItem();
		rearrangeContigs(spec1, geneset.geneset);
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
	
	public void draw( Graphics2D g2, String spec1, GeneSetHead geneset, int w, int h, Collection<Sequence> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total, int ptotal ) {
		draw( g2, spec1, geneset, w, h, contigs, spec2s, blosumap, total, ptotal, 0 );
	}
	
	public static double blosumVal( Sequence seq1, Sequence seq2, Map<String,Integer> blosumap ) {
		double ret = 0.0;
		
		int startcheck = 0;
		int start = -1;
		int stopcheck = 0;
		int stop = -1;
		for( int i = 0; i < seq1.length(); i++ ) {
			if( seq1.getCharAt(i) != '-' ) {
				startcheck |= 1;
			}
			if( seq2.getCharAt(i) != '-' ) {
				startcheck |= 2;
			}
			
			if( start == -1 && startcheck == 3 ) {
				start = i;
				break;
			}
		}
		
		for( int i = seq1.length()-1; i >= 0; i-- ) {
			if( seq1.getCharAt(i) != '-' ) {
				stopcheck |= 1;
			}
			if( seq2.getCharAt(i) != '-' ) {
				stopcheck |= 2;
			}
			
			if( stop == -1 && stopcheck == 3 ) {
				stop = i+1;
				break;
			}
		}
		
		int tscore = blosumValue( seq1, seq1, blosumap, start, stop );
        int score = blosumValue( seq1, seq2, blosumap, start, stop );
        
        if( score > tscore ) {
        	System.err.println("ff");
        	
        	System.err.println( seq1.sb.substring(start, stop) );
        	System.err.println( seq2.sb.substring(start, stop) );
        	/*tscore = 0;
        	for( int i = start; i < stop; i++ ) {
            	char lc = seq1.charAt(i);
            	char c = Character.toUpperCase( lc );
            	//if( )
            	String comb = c+""+c;
            	if( blosumap.containsKey(comb) ) {
            		double val = blosumap.get(comb);
            		System.err.println( comb + " " + val );
            		tscore += val;
            	}
            }
            
            score = 0;
            for( int i = start; i < stop; i++ ) {
            	char lc = seq1.charAt( i );
            	char c = Character.toUpperCase( lc );
            	char lc2 = seq2.charAt( i );
            	char c2 = Character.toUpperCase( lc2 );
            	
            	String comb = c+""+c2;
            	if( blosumap.containsKey(comb) ) {
            		double val = blosumap.get(comb);
            		System.err.println( comb + " " + val );
            		score += val;
            	}
            }*/
            System.err.println();
        }
        
        ret = (double)score/(double)tscore; //int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
		return ret;
	}

	public static int blosumValue( Sequence seq1, Sequence seq2, Map<String,Integer> blosumap ) {
		return blosumValue( seq1, seq2, blosumap, 0, Math.min( seq1.length(), seq2.length() ) );
	}

	public static int blosumValue( Sequence seq1, Sequence seq2, Sequence seq3, Map<String,Integer> blosumap ) {
		return blosumValue( seq1, seq2, seq3, blosumap, 0, Math.min( seq1.length(), Math.min( seq2.length(), seq3.length() ) ) );
	}

	public static int blosumValue( Sequence seq1, Sequence seq2, Sequence seq3, Map<String,Integer> blosumap, int start , int stop ) {
		int sscore = 0;
		for( int i = start; i < stop; i++ ) {
			char lc = seq1.getCharAt( i );
			char c = Character.toUpperCase( lc );
			char lc2 = seq2.getCharAt( i );
			char c2 = Character.toUpperCase( lc2 );
			char lc3 = seq3.getCharAt( i );
			char c3 = Character.toUpperCase( lc3 );

			String comb = c+""+c2;
			String comb2 = c+""+c3;
			if( blosumap.containsKey(comb) && blosumap.containsKey(comb2) ) sscore += blosumap.get(comb);
		}
		return sscore;
	}

	public static int blosumValue( Sequence seq1, Sequence seq2, Map<String,Integer> blosumap, int start , int stop ) {
		int sscore = 0;
		for( int i = start; i < stop; i++ ) {
			char lc = seq1.getCharAt( i );
			char c = Character.toUpperCase( lc );
			char lc2 = seq2.getCharAt( i );
			char c2 = Character.toUpperCase( lc2 );

			String comb = c+""+c2;
			if( blosumap.containsKey(comb) ) sscore += blosumap.get(comb);
		}
		return sscore;
	}
	
	public static double blosumVal( Sequence seq, String spec2, GeneGroup gg, Map<String,Integer> blosumap ) {
		double ret = 0.0;
		if( seq != null ) {
			int tscore = blosumValue( seq, seq, blosumap );
            
            int score = 0;
            Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
                Sequence seq2 = tv2.getAlignedSequence();
                
                int sscore = blosumValue( seq, seq2, blosumap );
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
		Color color = Color.red;
		if( seq != null && seq.length() > 0 ) {
			int tscore = blosumValue( seq, seq, blosumap );
            
            /*if( gg.getCommonName().contains("tRNA-Ile") ) {
            	System.err.println(gg.getCommonName());
            }*/
            
            int score = 0;
            Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
                Sequence seq2 = tv2.getAlignedSequence();
                
                if( seq == null || seq2 == null ) {
                	System.err.println();	
                } else {
	                int sscore = blosumValue( seq, seq2, blosumap );
	                if( sscore > score ) score = sscore;
	                
	                if( seq == seq2 && sscore != tscore ) {
	                	//System.err.println();
	                }
                }
            }
            int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
            color = rs ? new Color( 255, cval, cval ) : new Color( cval, cval, cval );
		} else {
			Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
            	if( tv2.getGene().getTag() != null && !tv2.getGene().getTag().equalsIgnoreCase("gene") )
            		color = tv2.getGene().getTag().equals("rrna") ? Color.red : Color.blue;
            	else color = Color.black;
            	break;
            }
		}
		
		return color;
	}
	
	static int ctgoff = 0;
	public static Color gradientColor( String spec1, String spec2, List<Sequence> contigs2, double ratio, double pratio, int offset2, GeneGroup gg, boolean contiglanes, Annotation tv ) {
		//Contig chromosome = null;
		int total2 = 0;
		int ptotal2 = 0;
		
		int chromstart = -1;
		for( Sequence ctg2 : contigs2 ) {
			if( ctg2.isPlasmid() ) ptotal2 += ctg2.getAnnotationCount();
			else {
				if( chromstart == -1 ) chromstart = ptotal2;
				total2 += ctg2.getAnnotationCount();
			}
		}
		
		/*int tot2 = 0;
		int ptot2 = 0;
		for( Contig ctg2 : contigs2 ) {
			tot2 += ctg2.getAnnotationCount();
		}
		
		int chromstart = 0;//total2;
		if( contigs2.size() <= 3 ) {
			int max = 0;
			int ccount = 0;
			for( Contig ctg2 : contigs2 ) {
				if( ctg2.getAnnotationCount() > max ) {
					chromosome = ctg2;
					chromstart = ccount;
					max = ctg2.getAnnotationCount();
				}
				ccount += ctg2.getAnnotationCount();
			}
			
			ptot2 = tot2 - max;
			tot2 = max;
		}*/
		
		double ratio2 = -1.0;
		double pratio2 = -1.0;
		Teginfo gene2s = gg.getGenes( spec2 );
		
		Sequence hit = null;
		if( spec1.equals(spec2) ) {
			for( Tegeval tv2 : gene2s.tset ) {
				int count2 = 0;
				
				if( ptotal2 > 0 ) {
					for( Sequence ctg2 : contigs2 ) {
						if( ctg2.annset != null ) {
							int idx = ctg2.annset.indexOf( tv2 );
							if( idx != -1 ) {
								hit = ctg2;
								break;
							}
						}
					}
					
					if( hit != null && hit.isPlasmid() ) {
						for( Sequence c2 : contigs2 ) {
							if( c2.isPlasmid() && c2.annset != null ) {
								int idx = c2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += c2.getAnnotationCount();
								} else {
									count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
									break;
								}
							}
						}
						double prat2 = (double)count2/(double)ptotal2;
						if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
					} else {
						for( Sequence c2 : contigs2 ) {
							if( c2.annset != null ) {
								int idx = c2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += c2.getAnnotationCount();
								} else {
									count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
									hit = c2;
									break;
								}
							}
						}
					}
					
					/*if( chromosome.annset != null ) {
						int idx = chromosome.annset.indexOf( tv2 );
						if( idx == -1 ) {
							for( Contig ctg2 : contigs2 ) {
								if( ctg2 != chromosome && ctg2.annset != null ) {
									idx = ctg2.annset.indexOf( tv2 );
									if( idx == -1 ) {
										count2 += ctg2.getAnnotationCount();
									} else {
										count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx; 
										//count2 += idx;
										break;
									}
								}
							}
							double prat2 = (double)count2/(double)ptotal2;
							if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
						} else count2 = chromosome.isReverse() ? chromosome.getAnnotationCount() - idx - 1 : idx;
					}*/
				} else {
					for( Sequence ctg2 : contigs2 ) {
						if( ctg2.annset != null ) {
							int idx = ctg2.annset.indexOf( tv2 );
							if( idx == -1 ) {
								count2 += ctg2.getAnnotationCount();
							} else {
								count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
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
		} else {
			if( gene2s.tset.size() == 1 ) {
				for( Tegeval tv2 : gene2s.tset ) {
					int count2 = 0;
					
					if( ptotal2 > 0 ) {
						for( Sequence ctg2 : contigs2 ) {
							if( ctg2.annset != null ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx != -1 ) {
									hit = ctg2;
									break;
								}
							}
						}
						
						if( hit != null && hit.isPlasmid() ) {
							for( Sequence c2 : contigs2 ) {
								if( c2.isPlasmid() && c2.annset != null ) {
									int idx = c2.annset.indexOf( tv2 );
									if( idx == -1 ) {
										count2 += c2.getAnnotationCount();
									} else {
										count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
										break;
									}
								}
							}
							double prat2 = (double)count2/(double)ptotal2;
							if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
						} else {
							for( Sequence c2 : contigs2 ) {
								if( c2.annset != null ) {
									int idx = c2.annset.indexOf( tv2 );
									if( idx == -1 ) {
										count2 += c2.getAnnotationCount();
									} else {
										count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
										hit = c2;
										break;
									}
								}
							}
						}
						
						/*if( chromosome.annset != null ) {
							int idx = chromosome.annset.indexOf( tv2 );
							if( idx == -1 ) {
								for( Contig ctg2 : contigs2 ) {
									if( ctg2 != chromosome && ctg2.annset != null ) {
										idx = ctg2.annset.indexOf( tv2 );
										if( idx == -1 ) {
											count2 += ctg2.getAnnotationCount();
										} else {
											count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx; 
											//count2 += idx;
											break;
										}
									}
								}
								double prat2 = (double)count2/(double)ptotal2;
								if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
							} else count2 = chromosome.isReverse() ? chromosome.getAnnotationCount() - idx - 1 : idx;
						}*/
					} else {
						for( Sequence ctg2 : contigs2 ) {
							if( ctg2.annset != null ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getAnnotationCount();
								} else {
									count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
									hit = ctg2;
									break;
								}
							}
						}
					}
					
					int val2 = count2 - offset2;
					if( val2 < 0 ) val2 = total2 + val2;
					
					double rat2 = (double)val2/(double)total2;
					ratio2 = rat2;
					
					/*if( rat2 > 1.0f || rat2 < 0.0f ) {
						System.err.println("");
					}
					
					if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) {
						ratio2 = rat2;
					}*/
				}
			} else {
				int msimcount = 0;
				for( Tegeval tv2 : gene2s.tset ) {
					int count2 = 0;
					int simcount = 0;
					if( ptotal2 > 0 ) {
						for( Sequence ctg2 : contigs2 ) {
							if( ctg2.annset != null ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx != -1 ) {
									hit = ctg2;
									break;
								}
							}
						}
						
						if( hit != null && hit.isPlasmid() ) {
							for( Sequence c2 : contigs2 ) {
								if( c2.isPlasmid() && c2.annset != null ) {
									int idx = c2.annset.indexOf( tv2 );
									if( idx == -1 ) {
										count2 += c2.getAnnotationCount();
									} else {
										count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
										break;
									}
								}
							}
							double prat2 = (double)count2/(double)ptotal2;
							//if( prat2 == -1.0 || Math.abs(pratio - prat2) < Math.abs(pratio - pratio2) ) pratio2 = prat2;
							
							Annotation n = tv != null ? tv.getNext() : null;
							Annotation p = tv != null ? tv.getPrevious() : null;
							Annotation n2 = tv2.getNext();
							Annotation p2 = tv2.getPrevious();
							
							if( n != null ) {
								GeneGroup ngg = n.getGene().getGeneGroup();
								if( n2 != null ) {
									if( ngg == n2.getGene().getGeneGroup() ) simcount++;
								}
								
								if( p2 != null ) {
									if( ngg == p2.getGene().getGeneGroup() ) simcount++;
								}
							}
							
							if( p != null ) {
								GeneGroup pgg = p.getGene().getGeneGroup();
								if( n2 != null ) {
									if( pgg == n2.getGene().getGeneGroup() ) simcount++;
								}
								
								if( p2 != null ) {
									if( pgg == p2.getGene().getGeneGroup() ) simcount++;
								}
							}
							
							if( ratio2 == -1 || simcount > msimcount ) {
								pratio2 = prat2;
								msimcount = simcount;
							}
						} else {
							for( Sequence c2 : contigs2 ) {
								if( c2.annset != null ) {
									int idx = c2.annset.indexOf( tv2 );
									if( idx == -1 ) {
										count2 += c2.getAnnotationCount();
									} else {
										count2 += c2.isReverse() ? c2.getAnnotationCount() - idx - 1 : idx;
										hit = c2;
										break;
									}
								}
							}
						}
					} else {
						for( Sequence ctg2 : contigs2 ) {
							if( ctg2.annset != null ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getAnnotationCount();
								} else {
									count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
									hit = ctg2;
									break;
								}
							}
						}
					}
					
					int val2 = count2 - offset2;
					if( val2 < 0 ) val2 = total2 + val2;
					
					double rat2 = (double)val2/(double)total2;
					
					Annotation n = tv != null ? tv.getNext() : null;
					Annotation p = tv != null ? tv.getPrevious() : null;
					Annotation n2 = tv2.getNext();
					Annotation p2 = tv2.getPrevious();
					
					if( n != null ) {
						GeneGroup ngg = n.getGene().getGeneGroup();
						if( n2 != null ) {
							if( ngg == n2.getGene().getGeneGroup() ) simcount++;
						}
						
						if( p2 != null ) {
							if( ngg == p2.getGene().getGeneGroup() ) simcount++;
						}
					}
					
					if( p != null ) {
						GeneGroup pgg = p.getGene().getGeneGroup();
						if( n2 != null ) {
							if( pgg == n2.getGene().getGeneGroup() ) simcount++;
						}
						
						if( p2 != null ) {
							if( pgg == p2.getGene().getGeneGroup() ) simcount++;
						}
					}
					
					if( ratio2 == -1 || simcount > msimcount ) {
						ratio2 = rat2;
						msimcount = simcount;
					}
				}
			}
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
		
		Color c = Color.white;
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
	
	public static double invertedGradientPlasmidRatio( String spec2, Collection<Sequence> contigs2, double ratio, GeneGroup gg ) {
		Teginfo gene2s = gg.getGenes(spec2);
		
		int total2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			if( ctg2.isPlasmid() ) total2 += ctg2.getAnnotationCount();
		}
		double ratio2 = -1.0;
		if( gene2s != null && gene2s.tset != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Sequence ctg2 : contigs2 ) {
				if( ctg2.isPlasmid() && ctg2.annset != null ) {
					int idx = ctg2.annset.indexOf( tv2 );
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
		return ratio2;
	}
	
	public static double invertedGradientRatio( String spec2, Collection<Sequence> contigs2, Tegeval tv2 ) {
		int total2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			if( !ctg2.isPlasmid() ) total2 += ctg2.getAnnotationCount();
		}
		int count2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			if( ctg2.annset != null && !ctg2.isPlasmid() ) {
				int idx = ctg2.annset.indexOf( tv2 );
				if( idx == -1 ) {
					count2 += ctg2.getAnnotationCount();
				} else {
					//if( ctg2.isPlasmid() ) return -1;
					count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
					break;
				}
			}
		}
		double ratio2 = count2 == total2 ? -1.0 : (double)count2/(double)total2;
		return ratio2;
	}
	
	public static double invertedGradientRatio( String spec2, Collection<Sequence> contigs2, double ratio, GeneGroup gg, Annotation tv ) {
		Teginfo gene2s = gg.getGenes(spec2);
		
		int msimcount = 0;
		int total2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			if( !ctg2.isPlasmid() ) total2 += ctg2.getAnnotationCount();
		}
		double ratio2 = -1.0;
		if( gene2s != null && gene2s.tset != null ) {
			//if( spec2.equals( tv.getSpecies() ) ) {
				for( Tegeval tv2 : gene2s.tset ) {
					int count2 = 0;
					int simcount = 0;
					for( Sequence ctg2 : contigs2 ) {
						if( ctg2.annset != null && !ctg2.isPlasmid() ) {
							int idx = ctg2.annset.indexOf( tv2 );
							if( idx == -1 ) {
								count2 += ctg2.getAnnotationCount();
							} else {
								//if( ctg2.isPlasmid() ) return -1;
								count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
								break;
							}
						}
					}
					double rat2 = (double)count2/(double)total2;
					if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) ratio2 = rat2;
					
					if( ratio != -1.0 ) {
						if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) ratio2 = rat2;
					} else {
						Annotation n = tv.getNext();
						Annotation p = tv.getPrevious();
						Annotation n2 = tv2.getNext();
						Annotation p2 = tv2.getPrevious();
						
						if( n != null ) {
							GeneGroup ngg = n.getGene().getGeneGroup();
							if( n2 != null && n2.getGene() != null ) {
								if( ngg == n2.getGene().getGeneGroup() ) simcount++;
							}
							
							if( p2 != null && p2.getGene() != null ) {
								if( ngg == p2.getGene().getGeneGroup() ) simcount++;
							}
						}
						
						if( p != null ) {
							GeneGroup pgg = p.getGene().getGeneGroup();
							if( n2 != null && n2.getGene() != null ) {
								if( pgg == n2.getGene().getGeneGroup() ) simcount++;
							}
							
							if( p2 != null && p2.getGene() != null ) {
								if( pgg == p2.getGene().getGeneGroup() ) simcount++;
							}
						}
						
						if( ratio2 == -1 || simcount > msimcount ) {
							ratio2 = rat2;
							msimcount = simcount;
						}
					}
				}
			/*} else {
				if( gene2s.tset.size() == 1 ) {
					for( Tegeval tv2 : gene2s.tset ) {
						int simcount = 0;
						int count2 = 0;
						for( Contig ctg2 : contigs2 ) {
							if( ctg2.annset != null && !ctg2.isPlasmid() ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getAnnotationCount();
								} else {
									//if( ctg2.isPlasmid() ) return -1;
									count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
									break;
								}
							}
						}
						double rat2 = (double)count2/(double)total2;
						ratio2 = rat2;
					}
				} else {
					String spc = tv.getSpecies();
					for( Tegeval tv2 : gene2s.tset ) {
						int simcount = 0;
						int count2 = 0;
						for( Contig ctg2 : contigs2 ) {
							if( ctg2.annset != null && !ctg2.isPlasmid() ) {
								int idx = ctg2.annset.indexOf( tv2 );
								if( idx == -1 ) {
									count2 += ctg2.getAnnotationCount();
								} else {
									//if( ctg2.isPlasmid() ) return -1;
									count2 += ctg2.isReverse() ? ctg2.getAnnotationCount() - idx - 1 : idx;
									break;
								}
							}
						}
						double rat2 = (double)count2/(double)total2;
						/*if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) {
							ratio2 = rat2;
						}
						
						/*Tegeval n = tv.getNext();
						Tegeval p = tv.getPrevious();
						Tegeval n2 = tv2.getNext();
						Tegeval p2 = tv2.getPrevious();
						
						if( n != null ) {
							GeneGroup ngg = n.getGene().getGeneGroup();
							if( n2 != null ) {
								if( ngg == n2.getGene().getGeneGroup() ) simcount++;
							}
							
							if( p2 != null ) {
								if( ngg == p2.getGene().getGeneGroup() ) simcount++;
							}
						}
						
						if( p != null ) {
							GeneGroup pgg = p.getGene().getGeneGroup();
							if( n2 != null ) {
								if( pgg == n2.getGene().getGeneGroup() ) simcount++;
							}
							
							if( p2 != null ) {
								if( pgg == p2.getGene().getGeneGroup() ) simcount++;
							}
						}
						
						if( ratio2 == -1 || simcount > msimcount ) {
							ratio2 = rat2;
							msimcount = simcount;
						}*
					}
				}
			}*/
			//ratio2 = rat2;
			//break;
		}
		
		//System.err.println( "bbbbbb " + ratio2 );
		/*if( ratio2 == -1.0 ) {
			System.err.println();
		}*/
		
		return ratio2;
	}
	
	public static double invertedGradientTotalRatio( String spec2, Collection<Sequence> contigs2, Teginfo gene2s, double ratio, GeneGroup gg ) {
		int total2 = 0;
		for( Sequence ctg2 : contigs2 ) {
			total2 += ctg2.getAnnotationCount();
		}
		double ratio2 = -1.0;
		if( gene2s != null && gene2s.tset != null ) for( Tegeval tv2 : gene2s.tset ) {
			int count2 = 0;
			for( Sequence ctg2 : contigs2 ) {
				if( ctg2.annset != null ) {
					int idx = ctg2.annset.indexOf( tv2 );
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
				//if( val > 9.0f/20.0f ) {
				//	c = new Color(18.0f/10.0f-val*2.0f,18.0f/10.0f-2.0f*val,18.0f/10.0f-2.0f*val);
				//} else {
				c = new Color(val*1.0f,val*1.0f,val*1.0f);
				//}
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
		} /*else if( ratio > 1.0 ) {
			c = Color.red;
		}*/
		
		return c;
	}
	
	public void draw( Graphics2D g2, String spec1, GeneSetHead genesethead, int w, int h, Collection<Sequence> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total, int ptotal, int synbr ) {
		GeneSet geneset = genesethead.geneset;
		
		boolean contiglanesb = contiglanes != null && contiglanes.isSelected();
		
		g2.setBackground( Color.white );
		g2.clearRect(0, 0, w, h);
		/*g.setColor( Color.black );
		int count = 0;
		for( Contig ctg : contigs ) {
			for( Tegeval tv : ctg.annset ) {
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
		
		if( spec1 == null || spec1.length() == 0 ) {
			ObservableList<GeneGroup> lgg = genesethead.getGeneGroupTable().getItems();
			int rowcount = lgg.size();
			for( int r = 0; r < rowcount; r++ ) {
				//int i = genesethead.table.convertRowIndexToModel(r);
				GeneGroup gg = lgg.get(r);
				subDraw(g2, null, null, genesethead, spec1, r, null, null, spec2s, synbr, w, h, blosumap, gg, null, total, ptotal);
			}
			
			Font oldfont = g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f);
			g2.setFont( oldfont );
			int k = 0;
			for( String spec : spec2s ) {
				g2.translate( w/2, h/2 );
				
				g2.setColor( Color.lightGray );
				int r = 500 + 30 + k*30;
				g2.drawOval( -r, -r, 2*r, 2*r );
				
				g2.translate( -w/2, -h/2 );
				
				k++;
			}
			k = 0;
			for( String spec : spec2s ) {
				if( spec.equals(spec1) ) {
					g2.setFont( oldfont.deriveFont(Font.BOLD | Font.ITALIC) );
				} else {
					g2.setFont( oldfont );
				}
				
				String specstr = geneset.nameFix( spec );
				if( specstr.length() > 30 ) specstr = specstr.substring(0, specstr.lastIndexOf('_'));
				
				int strw = g2.getFontMetrics().stringWidth( specstr );
				g2.translate( w/2, h/2 );
				
				/*g2.setColor( Color.lightGray );
				int r = 250 + 15 + k*15;
				g2.drawOval( -r, -r, 2*r, 2*r );*/
				
				g2.rotate( Math.PI/2.0 );
				//-spec2s.size()*14/2
				g2.setColor( Color.white );
				g2.fillRect(-strw, -500-30-k*30 - 3, strw+1, 40);
				g2.setColor( Color.black );
				g2.drawString( specstr/*.replace("T.", "")*/, -strw+5, -500 - k*30 - 2 );
				g2.rotate( -Math.PI/2.0 );
				g2.translate( -w/2, -h/2 );
				
				k++;
			}
		} else {
			Map<String,Integer>	offsetMap = new HashMap<String,Integer>();
				
			if( !genesethead.isGeneview() ) {
				GeneGroup gg = genesethead.getGeneGroupTable().getSelectionModel().getSelectedItem();
				if( gg != null ) {
					for( String spec2 : spec2s ) {
						if( gg.species != null && gg.species.containsKey(spec2) ) {
							final Collection<Sequence> contigs2 = spec1.equals(spec2) ? contigs : geneset.speccontigMap.get( spec2 );
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
								//System.err.println( spec2 + "   " + count2 );
								offsetMap.put(spec2, count2);
							}
						}
					}
				}
			} else {
				GeneGroup gg = genesethead.getGeneTable().getSelectionModel().getSelectedItem().getGeneGroup();
				
				for( String spec2 : spec2s ) {
					if( gg.species.containsKey(spec2) ) {
						final Collection<Sequence> contigs2 = spec1.equals(spec2) ? contigs : geneset.speccontigMap.get( spec2 );
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
			}
			
			int count = 0;
			int pcount = 0;
			int current = 0;
			if( contigs != null ) for( Sequence ctg : contigs ) {
				Annotation prev = null;
				if( ctg.annset != null ) {
					current = count;
					if( ctg.isReverse() ) {
						for( int i = ctg.annset.size()-1; i >= 0; i-- ) {
							Annotation tv = ctg.annset.get( i );
							Sequence seq = tv.getAlignedSequence();
							GeneGroup gg = tv.getGene().getGeneGroup();
							
							subDraw( g2, tv, prev, genesethead, spec1, count, offsetMap, ctg, spec2s, synbr, w, h, blosumap, gg, seq, total, ptotal );
							count++;
							prev = tv;
							/*} else {
								System.err.println();
							}*/
						}
					} else {
						List<Annotation> lann = ctg.getAnnotations();
						for( Annotation ann : lann ) {
							Annotation tv = ann;
							Sequence seq = tv.getAlignedSequence();
							GeneGroup gg = tv.getGene().getGeneGroup();
							
							/*if( gg.species.size() > 1 ) {
								System.err.println( "commonname large " + gg.getCommonName() );
							} else {
								//System.err.println( "commonname small " + gg.getCommonName() );
							}*/
							
							subDraw( g2, tv, prev, genesethead, spec1, count, offsetMap, ctg, spec2s, synbr, w, h, blosumap, gg, seq, total, ptotal );
							count++;
							prev = tv;
						}
					}
				
					/*double theta = count*Math.PI*2.0/(total+ptotal);
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
		            g2.translate( -w/2, -h/2 );*/
				}
			}
			g2.setColor( Color.black );
			
			Font oldfont = g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f);
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
				g2.drawString( specstr/*.replace("T.", "")*/, (w-strw)/2, h/2 - spec2s.size()*36/2 + 36 + k*36 );
				k++;
			}
		}
	}
	
	public void subDraw( Graphics2D g2, Annotation tv, Annotation prev, GeneSetHead genesethead, String spec1, int count, Map<String,Integer> offsetMap, Sequence ctg, List<String> spec2s, int synbr, int w, int h, Map<String,Integer> blosumap, GeneGroup gg, Sequence seq, int total, int ptotal ) {
		GeneSet geneset = genesethead.geneset;
		boolean rs = false;
		if( !genesethead.isGeneview() ) {
			/*geneseg.allgenegroups[]
			r = genesethead.table.convertRowIndexToView( ii );
			rs = genesethead.table.isRowSelected( r );*/
			
			rs = genesethead.getGeneGroupTable().getSelectionModel().getSelectedItems().contains(gg);
		} else {
			for( Gene g : gg.genes ) {
				//r = genesethead.table.convertRowIndexToView( g.index );
				//rs = genesethead.table.isRowSelected( r );
				rs = genesethead.getGeneTable().getSelectionModel().getSelectedItems().contains(g);
				if( rs ) break;
			}
		}
		
		boolean contiglanesb = contiglanes != null && contiglanes.isSelected();
		int offset = 0;
		//if( spec1 != null && offsetMap.containsKey(spec1) ) offset = offsetMap.get(spec1);
		
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
			
			/*if( ctg.getAnnotationCount() == total ) {
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
		
		//final ExecutorService es = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
		
		int scount = 0;
		for( String spec2 : spec2s ) {
			final List<Sequence> contigs2;
			boolean speceq = false;
			if( spec1 != null ) speceq = spec1.equals(spec2);
			contigs2 = speceq ? contigs : geneset.speccontigMap.get( spec2 );
			
			if( gg.species.containsKey(spec2) ) {
				int offset2 = 0;
				//if( offsetMap != null && offsetMap.containsKey( spec2 ) ) offset2 = offsetMap.get(spec2);
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
					g2.fillRect( 500+30*(scount), -1, 30, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
				} else if( synbr == -2 ) {
					if( spec1 == null ) {
						ratio = GeneCompare.invertedGradientRatio(spec2, contigs2, -1.0, gg, tv);
						if( ratio == -1 ) {
							ratio = GeneCompare.invertedGradientPlasmidRatio(spec2, contigs2, -1.0, gg);
							g2.setColor( GeneCompare.gradientGrayscaleColor( ratio ) );
						} else {
							g2.setColor( GeneCompare.gradientColor( ratio ) );
						}
						
						double theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( 500+30*(scount), -1, 30, 3);
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
						
						double ratio2 = invertedGradientTotalRatio( spec2, contigs2, gg.getGenes(spec2), tratio, gg );
						if( ratio2 != -1.0 ) {
							double theta = ratio2*Math.PI*2.0;
							g2.translate( w/2, h/2 );
							g2.rotate( theta );
							g2.fillRect( 500+30*(scount), -1, 30, 3);
							g2.rotate( -theta );
		                    g2.translate( -w/2, -h/2 );
						}
					}
				} else if( synbr == -1 ) {
					//System.err.println( ratio );
					ctgoff = 0;
					Color c = gradientColor( spec1, spec2, contigs2, ratio, pratio, offset2, gg, contiglanesb, tv );
					g2.setColor( c );
					
					double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					if( contiglanesb ) g2.fillRect( 300+30*(scount)+30*ctgoff, -1, 30, 2);
					else g2.fillRect( 500+30*(scount), -1, 30, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
                    
                    if( gg.species.size() == 1 ) {
                    	g2.setColor( Color.black );
                    	 
                    	theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( contiglanesb ? 200 : 400, 0, 15, 1 );
						g2.rotate( -theta );
	                    g2.translate( -w/2, -h/2 );
                    }
            
                	if( genesethead.isGeneview() ? genesethead.getGeneTable().getSelectionModel().getSelectedItems().contains(tv.getGene()) : genesethead.getGeneGroupTable().getSelectionModel().getSelectedItems().contains(gg) ) {
                		g2.setColor( Color.black );
                   	 
                    	theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.drawLine( 500, 0, 490, -10 );
						g2.drawLine( 500, 0, 490, 10 );
						g2.drawLine( 490, -10, 490, 10 );
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
							g2.fillRect( 500+30*(scount), -1, 30, 3);
							g2.rotate( -theta );
		                    g2.translate( -w/2, -h/2 );
                        }
					}
				} else {
					Color color = Color.red;
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
						String spec = genesethead.syncolorcomb.getSelectionModel().getSelectedItem();
						if( spec != null && spec.length() > 0 ) {
							if( spec.equals("All") ) {
								Teginfo value = gg.getGenes( spec2 );
								//if( value instanceof Teginfo ) {
									Teginfo ti = (Teginfo)value;
									for( Tegeval tvv : ti.tset ) {
										String tspec = tvv.getGene().getSpecies();
										List<Sequence> scontigs = geneset.speccontigMap.get( tspec );
										
										GeneGroup ggg = tvv.getGene().getGeneGroup();
										//Teginfo gene2s = ggg.getGenes(tspec);
										ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, ggg, tvv);
										if( ratio == -1 ) {
											ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, ggg);
											color = GeneCompare.gradientGrayscaleColor( ratio );
											//label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
											//label.setForeground( Color.white );
										} else {
											color = GeneCompare.gradientColor( ratio );
											//label.setBackground( GeneCompare.gradientColor( ratio ) );
											//label.setForeground( Color.black );
										}
										break;
										//GeneCompare.gradientColor();
									}
								/*} else if( value instanceof Tegeval ) {
									Tegeval tv = (Tegeval)value;
									String tspec = tv.getGene().getSpecies();
									List<Contig> scontigs = geneset.speccontigMap.get( tspec );
									
									ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
									if( ratio == -1 ) {
										ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, tv.getGene().getGeneGroup());
										label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
										label.setForeground( Color.white );
									} else {
										label.setBackground( GeneCompare.gradientColor( ratio ) );
										label.setForeground( Color.black );
									}
								}*/
							} /*else {
								List<Contig> contigs = geneset.speccontigMap.get( spec );
								if( value instanceof Teginfo ) {
									Teginfo ti = (Teginfo)value;
									label.setBackground( Color.green );
									for( Tegeval tv : ti.tset ) {
										double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
										if( ratio == -1 ) {
											ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
											label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
											label.setForeground( Color.white );
										} else {
											label.setBackground( GeneCompare.gradientColor( ratio ) );
											label.setForeground( Color.black );
										}
										break;
										//GeneCompare.gradientColor();
									}
								} else if( value instanceof Tegeval ) {
									Tegeval tv = (Tegeval)value;
									
									ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
									if( ratio == -1 ) {
										ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
										label.setBackground( GeneCompare.gradientGrayscaleColor( ratio ) );
										label.setForeground( Color.white );
									} else {
										label.setBackground( GeneCompare.gradientColor( ratio ) );
										label.setForeground( Color.black );
									}
									
									/*double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
									label.setBackground( GeneCompare.gradientColor( ratio ) );*
								}
							}*/
						} else {
							boolean phage = false;//gg.isInAnyPhage();
							boolean plasmid = false;//gg.isOnAnyPlasmid();
							
							Teginfo ti = gg.getGenes( spec2 );
							for( Tegeval tv2 : ti.tset ) {
	                        	phage |= tv2.isPhage();
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
					}
					if( color != null ) g2.setColor( color );
                    
					double theta = count*Math.PI*2.0/(total+ptotal);
					g2.translate( w/2, h/2 );
					g2.rotate( theta );
					g2.fillRect( 500+30*(scount), -1, 30, 3);
					g2.rotate( -theta );
                    g2.translate( -w/2, -h/2 );
                    
                    if( gg.species.size() == 1 ) {
                    	g2.setColor( Color.black );
                    	 
                    	theta = count*Math.PI*2.0/(total+ptotal);
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( 400, 0, 30, 1);
						g2.rotate( -theta );
	                    g2.translate( -w/2, -h/2 );
                    }
                    
                    int i;
                    if( !genesethead.isGeneview() ) {
                    	if( genesethead.getGeneGroupTable().getSelectionModel().getSelectedItems().contains( gg ) ) {
                    		g2.setColor( Color.black );
                       	 
                        	theta = count*Math.PI*2.0/(total+ptotal);
    						g2.translate( w/2, h/2 );
    						g2.rotate( theta );
    						g2.drawLine( 500, 0, 490, -10 );
    						g2.drawLine( 500, 0, 490, 10 );
    						g2.drawLine( 490, -10, 490, 10 );
    						g2.rotate( -theta );
    	                    g2.translate( -w/2, -h/2 );
                    	}
                    } else {
                    	if( genesethead.getGeneTable().getSelectionModel().getSelectedItems().contains( gg ) ) {
                    		g2.setColor( Color.black );
                       	 
                        	theta = count*Math.PI*2.0/(total+ptotal);
    						g2.translate( w/2, h/2 );
    						g2.rotate( theta );
    						g2.drawLine( 500, 0, 490, -10 );
    						g2.drawLine( 500, 0, 490, 10 );
    						g2.drawLine( 490, -10, 490, 10 );
    						g2.rotate( -theta );
    	                    g2.translate( -w/2, -h/2 );
                    	}
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
