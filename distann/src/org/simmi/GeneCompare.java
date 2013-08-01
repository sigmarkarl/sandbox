package org.simmi;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class GeneCompare {
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
		
		final Map<String,Integer>	blosumap = new HashMap<String,Integer>();
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
		
		final List<Contig> contigs = geneset.speccontigMap.get( spec1 );
		int total = 0;
		for( Contig ctg : contigs ) {
			total += ctg.getGeneCount();
		}
		
		final BufferedImage bimg = new BufferedImage( 1500, 1500, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2 = bimg.createGraphics();
		draw( g2, bimg.getWidth(), bimg.getHeight(), contigs, spec2s, blosumap, total );
		
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
				try {
					ImageIO.write(bimg, "png", new File("/home/sigmar/cir.png") );
				} catch (IOException e1) {
					e1.printStackTrace();
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
				Contig c = contigs.get(i);
				for( i = minloc; i < maxloc; i++ ) {
					Tegeval tv = c.tlist.get(i-loc);
					int k = geneset.allgenegroups.indexOf( tv.getGene().getGeneGroup() );
					int r = geneset.table.convertRowIndexToView(k);
					geneset.table.addRowSelectionInterval( r, r );
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				p = e.getPoint();
			}
		});
		
		Dimension dim = new Dimension( 2048, 2048 );
		cmp.setPreferredSize( dim );
		cmp.setSize( dim );
		JScrollPane	scrollpane = new JScrollPane( cmp );
		JFrame frame = new JFrame();
		frame.add( scrollpane );
		frame.setSize( dim );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public void draw( Graphics2D g2, int w, int h, Collection<Contig> contigs, List<String> spec2s, Map<String,Integer> blosumap, int total ) {
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
        g2.setBackground( Color.white );
		g2.clearRect( 0, 0, w, h );
		g2.setColor( Color.black );
		int count = 0;
		for( Contig ctg : contigs ) {
			for( Tegeval tv : ctg.tlist ) {
				StringBuilder seq = tv.getAlignedSequence();
				GeneGroup gg = tv.getGene().getGeneGroup();
				int scount = 0;
				for( String spec2 : spec2s ) {
					if( gg.species.containsKey(spec2) ) {                                
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
                            for( int i = 0; i < seq.length(); i++ ) {
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
                        
                        int cval = Math.min( 128, 512-score*512/tscore );
                        if( cval != 0 ) {
                        	System.err.println();
                        }
                        Color color = new Color( cval, cval, cval );
                        g2.setColor( color );
                        
						double theta = count*Math.PI*2.0/total;
						g2.translate( w/2, h/2 );
						g2.rotate( theta );
						g2.fillRect( 200+15*(scount), -1, 15, 3);
						g2.rotate( -theta );
                        g2.translate( -w/2, -h/2 );
					}
					scount++;
				}
				count++;
			}
		}
	}
}
