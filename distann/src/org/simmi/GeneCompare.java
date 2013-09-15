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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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
		
		String spec1 = (String)table1.getValueAt( table1.getSelectedRow(), 0 );
		final List<String>	spec2s = new ArrayList<String>();
		int[] rr = table2.getSelectedRows();
		for( int r : rr ) {
			String spec2 = (String)table2.getValueAt(r, 0);
			spec2s.add( spec2 );
		}
		
		final Map<String,Integer>	blosumap = getBlosumMap();
		
		contigs = geneset.speccontigMap.get( spec1 );
		int total = 0;
		for( Contig ctg : contigs ) {
			total += ctg.getGeneCount();
		}
		
		final BufferedImage bimg = new BufferedImage( 2048, 2048, BufferedImage.TYPE_INT_ARGB );
		final Graphics2D g2 = bimg.createGraphics();
		draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total );
		
		final JRadioButton	relcol = new JRadioButton("Rel color");
		final JRadioButton	gccol = new JRadioButton("GC color");
		final JRadioButton	syntcol = new JRadioButton("Synteni color");
		final JRadioButton	brcol = new JRadioButton("Breakpoint color");
		final JRadioButton	syntgrad = new JRadioButton("Synteni gradient");
		ButtonGroup		bg = new ButtonGroup();
		bg.add( relcol );
		bg.add( gccol );
		bg.add( syntcol );
		bg.add( brcol );
		bg.add( syntgrad );
		
		final JComboBox<String>	specombo = new JComboBox<String>();
		for( String spec : specset ) specombo.addItem( spec );
		
		specombo.setSelectedItem( spec1 );
		
		relcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total );
			}
		});
		gccol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total );
			}
		});
		syntcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, 1 );
			}
		});
		brcol.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, 2 );
			}
		});
		syntgrad.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String spec1 = (String)specombo.getSelectedItem();
				final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, null, total, -1 );
			}
		});
		
		specombo.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//spec1 = (String)e.getItem()
				String spec1 = (String)e.getItem();
				contigs = geneset.speccontigMap.get( spec1 );
				int total = 0;
				for( Contig ctg : contigs ) {
					total += ctg.getGeneCount();
				}
				draw( g2, spec1, geneset, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, relcol.isSelected() ? blosumap : null, total );
			}
		});
		
		JComponent cmp = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				//draw( g2 );
				g2.drawImage(bimg, 0, 0, this);
			}
		};
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean succ = true;
				try {
					ImageIO.write(bimg, "png", new File("/home/sigmar/cir.png") );
				} catch(Exception e1) {
					succ = false;
					e1.printStackTrace();
				}
				
				if( !succ ) {
					FileSaveService fss = null;
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
			        }
				}
			}
		});
		cmp.setComponentPopupMenu( popup );
		
		final int size = total;
		cmp.addMouseListener( new MouseListener() {
			Point p;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point np = e.getPoint();
				if( p != null ) {
					double t1 = Math.atan2( p.y-bimg.getHeight()/2, p.x-bimg.getWidth()/2 );
					double t2 = Math.atan2( np.y-bimg.getHeight()/2, np.x-bimg.getWidth()/2 );
					
					if( t1 < 0 ) t1 += Math.PI*2.0;
					if( t2 < 0 ) t2 += Math.PI*2.0;
					
					int loc1 = (int)(t1*size/(2*Math.PI));
					int loc2 = (int)(t2*size/(2*Math.PI));
					
					int minloc = Math.min( loc1, loc2 );
					int maxloc = Math.max( loc1, loc2 );
					
					int i = 0;
					int loc = 0;
					for( i = 0; i < contigs.size(); i++ ) {
						Contig c = contigs.get(i);
						if( loc + c.getGeneCount() > minloc ) {
							break;
						} else loc += c.getGeneCount();
					}
					System.err.println( minloc + "  " + maxloc );
					Contig c = contigs.get(i);
					for( int k = minloc; k < maxloc; k++ ) {
						if( k-loc >= c.getGeneCount() ) {
							loc += c.getGeneCount();
							i++;
							c = contigs.get( i%contigs.size() );
						}
						Tegeval tv = c.tlist.get(k-loc);
						int u = geneset.allgenegroups.indexOf( tv.getGene().getGeneGroup() );
						int r = geneset.table.convertRowIndexToView(u);
						geneset.table.addRowSelectionInterval( r, r );
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
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
		
		JToolBar	toolbar = new JToolBar();
		toolbar.add( specombo );
		toolbar.add( relcol );
		toolbar.add( gccol );
		toolbar.add( syntcol );
		toolbar.add( brcol );
		toolbar.add( syntgrad );
		
		JComponent panel = new JComponent() {};
		panel.setLayout( new BorderLayout() );
		panel.add( toolbar, BorderLayout.NORTH );
		panel.add( scrollpane );
		
		JFrame frame = new JFrame();
		frame.add( panel );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void draw( Graphics2D g2, String spec1, GeneSet geneset, int w, int h, Collection<Contig> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total ) {
		draw( g2, spec1, geneset, w, h, contigs, spec2s, blosumap, total, 0 );
	}
	
	public static Color blosumColor( StringBuilder seq, String spec2, GeneGroup gg, Map<String,Integer> blosumap, boolean rs ) {
		Color color = Color.green;
		if( seq != null ) {
			int tscore = 0;
            for( int i = 0; i < seq.length(); i++ ) {
            	char c = seq.charAt(i);
            	String comb = c+""+c;
            	if( blosumap.containsKey(comb) ) tscore += blosumap.get(comb);
            }
            
            int score = 0;
            Teginfo gene2s = gg.getGenes( spec2 );
            for( Tegeval tv2 : gene2s.tset ) {
                StringBuilder seq2 = tv2.getAlignedSequence();
                
                int sscore = 0;
                for( int i = 0; i < Math.min( seq.length(), seq2.length() ); i++ ) {
                	char c = seq.charAt( i );
                	char c2 = seq2.charAt( i );
                	
                	String comb = c+""+c2;
                	if( blosumap.containsKey(comb) ) sscore += blosumap.get(comb);
                }
                if( sscore > score ) score = sscore;
                
                if( seq == seq2 && sscore != tscore ) {
                	System.err.println();
                }
            }
            int cval = Math.min( 192, 512-score*512/tscore );
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
	
	public void draw( Graphics2D g2, String spec1, GeneSet geneset, int w, int h, Collection<Contig> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total, int synbr ) {
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
		int count = 0;
		for( Contig ctg : contigs ) {
			Tegeval prev = null;
			if( ctg.tlist != null ) {
				for( Tegeval tv : ctg.tlist ) {
					StringBuilder seq = tv.getAlignedSequence();
					GeneGroup gg = tv.getGene().getGeneGroup();
					
					int ii = geneset.allgenegroups.indexOf( gg );
					if( ii >= 0 && ii < geneset.table.getRowCount() ) {
						int r = geneset.table.convertRowIndexToView( ii );
						boolean rs = geneset.table.isRowSelected( r );
						
						/*if( rs ) {
							System.err.println();
						}*/
						
						double ratio = (double)count/(double)total;
						
						int scount = 0;
						for( String spec2 : spec2s ) {
							if( gg.species.containsKey(spec2) ) {
								if( synbr == -1 ) {
									final List<Contig> contigs2 = geneset.speccontigMap.get( spec2 );
									int total2 = 0;
									for( Contig ctg2 : contigs2 ) {
										total2 += ctg2.getGeneCount();
									}
									double ratio2 = -1.0;
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
										double rat2 = (double)count2/(double)total2;
										 
										if( ratio2 == -1.0 || Math.abs(ratio - rat2) < Math.abs(ratio - ratio2) ) ratio2 = rat2;
										//ratio2 = rat2;
										//break;
									}
									
									//float green = (float)(1.0-ratio2);
									if( ratio2 < 0.5 ) {
										Color c = new Color(0,(float)(ratio2*2.0),1.0f);
										g2.setColor( c );
									} else {
										Color c = new Color(0,1.0f,(float)((1.0-ratio2)*2.0));
										g2.setColor( c );
									}
									
									double theta = count*Math.PI*2.0/total;
									g2.translate( w/2, h/2 );
									g2.rotate( theta );
									g2.fillRect( 250+15*(scount), -1, 15, 3);
									g2.rotate( -theta );
				                    g2.translate( -w/2, -h/2 );
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
					                        
											double theta = count*Math.PI*2.0/total;
											g2.translate( w/2, h/2 );
											g2.rotate( theta );
											g2.fillRect( 250+15*(scount), -1, 15, 3);
											g2.rotate( -theta );
						                    g2.translate( -w/2, -h/2 );
				                        }
									}
								} else {
									Color color = Color.green;
									if( blosumap != null ) {
										color = blosumColor(seq, spec2, gg, blosumap, rs);
									} else {
										Teginfo gene2s = gg.getGenes( spec2 );
				                        for( Tegeval tv2 : gene2s.tset ) {
				                        	color = tv2.getGCColor();
				                        	break;
				                        }
									}
									if( color != null ) g2.setColor( color );
			                        
									double theta = count*Math.PI*2.0/total;
									g2.translate( w/2, h/2 );
									g2.rotate( theta );
									g2.fillRect( 250+15*(scount), -1, 15, 3);
									g2.rotate( -theta );
				                    g2.translate( -w/2, -h/2 );
								}
							}
							scount++;
						}
						count++;
						
						prev = tv;
					}
				}
			
				g2.setColor( Color.black );
				double theta = count*Math.PI*2.0/total;
				g2.translate( w/2, h/2 );
				g2.rotate( theta );
				int x = 250+15*spec2s.size();
				g2.drawLine( x, 0, x+15, 0);
				g2.rotate( -theta );
	            g2.translate( -w/2, -h/2 );
			}
		}
		g2.setColor( Color.black );
		g2.setFont( g2.getFont().deriveFont( Font.ITALIC ).deriveFont(32.0f) );
		String[] specsplit = spec1.split("_");
		int k = 0;
		for( String spec : specsplit ) {
			int strw = g2.getFontMetrics().stringWidth( spec );
			g2.drawString( spec, (w-strw)/2, h/2 - specsplit.length*32/2 + 32 + k*32 );
			k++;
		}
	}
}
