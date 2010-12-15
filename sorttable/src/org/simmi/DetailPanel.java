package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.simmi.RecipePanel.Recipe;
import org.simmi.RecipePanel.RecipeIngredient;

public class DetailPanel extends SimSplitPane {
	JCompatTable	detailTable;
	TableModel		detailModel;
	List<Boolean>	showColumn;
	TableModel		nullModel;
	Map<String,String>	groupMap;
	BufferedImage	bi;
	
	public int countVisible() {
		int count = 0;
		for( boolean b : showColumn ) {
			if( b ) count++;
		}
		return count;
	}
	
	public int convertIndex( int c ) {
		int count = 0;
		int i = 0;
		for( int k = 0; k < showColumn.size(); k++ ) {
			int ind = detailTable.convertRowIndexToModel(k);
			if( showColumn.get(ind) ) count++;
			if( count == c+1 ) return detailTable.convertRowIndexToModel(i);
			
			i++;
		}
		
		return -1;
	}
	
	public void updateModels( final JTable table, final JTable topTable ) {
		TableModel oldTop = topTable.getModel();
		TableModel old = table.getModel();
		
		topTable.setModel( nullModel );
		table.setModel( nullModel );
		
		topTable.setModel( oldTop );
		table.setModel( old );
	}
	
	ByteArrayOutputStream	baos = new ByteArrayOutputStream();
	PrintStream				ps = new PrintStream( baos );
	public final class PercStr implements Comparable<PercStr> {
		final double	val;
		
		public PercStr( Float val ) {
			this.val = val;
		}
		
		public PercStr( Double val ) {
			this.val = val;
		}
		
		public String toString() {
			baos.reset();
			ps.printf("%.2f %%", val);
			return baos.toString();
		}

		public int compareTo(PercStr o) {
			return Double.compare(val,o.val);
		}
	}
	
	public static Float getVal( int rowInd, int colInd, final List<Object[]> stuff, final Map<String,Integer> foodInd, final List<Recipe> recipes ) {
		return getVal(rowInd, colInd, stuff, foodInd, recipes, true);
	}
	
	public static Float getVal( int rowInd, int colInd, final List<Object[]> stuff, final Map<String,Integer> foodInd, final List<Recipe> recipes, boolean perHundredg ) {
		Object[] obj = null;
		if( rowInd < stuff.size()-2 ) {
			obj = stuff.get(rowInd+2);
			Object val = obj[ colInd+2 ];
			
			//Object val = model.getValueAt( lsel, rowIndex );
			if( val instanceof Float ) {
				return (Float)val;
			}
		} else {
			double ret = 0.0f;
			int i = rowInd - (stuff.size()-2);
			if( i < recipes.size() ) {
				Recipe rep = recipes.get(i);
				double tot = 0.0f;
				for (RecipeIngredient rip : rep.ingredients) {
					int k = -1;
					double div = 100.0f;
					if (foodInd.containsKey(rip.stuff)) {
						k = foodInd.get(rip.stuff);
					} else {
						int 	ri = 0;
						Recipe 	rp = null;
						for( Recipe r : recipes ) {
							String rname = r.name + " - " + r.author;
							if( rep != r && rip.stuff.equals( rname ) ) {
								rp = r;
								break;
							}
							ri++;
						}
						if( ri < recipes.size() ) {
							k = stuff.size()-2+ri;
							//if( perHundredg ) 
								div = rp.getWeight();
						}
					}
					
					if( k != -1 ) {
						//obj = stuff.get(k + 2);
						Object val = getVal( k, colInd, stuff, foodInd, recipes ); //obj[rowIndex + 2];
						if (val != null && val instanceof Float) {										
							double f;
							//if( perHundredg ) 
							f = rip.getValue( (Float)val ) / div;
							//else f = (Float)val;
							ret += f;
						}
					}
				}
				
				if( ret != 0.0 ) {
					if( perHundredg ) {
						return (float)ret;
					} else {
						return (float)( (ret * 100.0) / rep.getWeight() );
					}
				}
			} else {
				System.err.println( "outofbounds" );
			}
		}
		return null;
	}
	
	public DetailPanel( final SortTable sortTable, final RdsPanel rdsp, final String lang, final ImagePanel imgPanel, final JCompatTable table, final JCompatTable topTable, final JCompatTable leftTable, final List<Object[]> stuff, final List<String> ngroupList, final List<String> ngroupGroups, final Map<String,Integer> foodInd, final List<Recipe> recipes ) throws IOException {
		super( JSplitPane.VERTICAL_SPLIT );
		this.setDividerLocation( 300 );
		
		final JScrollPane	detailScroll = new JScrollPane();
		detailScroll.getViewport().setBackground( Color.white );
		
		URL url = this.getClass().getResource("/re.png");
		//bi = ImageIO.read( url );
		ImageIcon icon = new ImageIcon( url );
		final JButton	button = new JButton( icon );
		button.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imgPanel.orientation = (imgPanel.orientation+1)%4;
				
				if( imgPanel.orientation%2 == 1 ) {
					swapComponents();
				}
				
				int orientation = DetailPanel.this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT;
				DetailPanel.this.setOrientation( orientation );
				//DetailPanel.
			}
		});
		button.setBounds( 9,9,32,32 );
		imgPanel.add( button );
		
		groupMap = new HashMap<String,String>();
		if( lang.equals("IS") ) {
			groupMap.put("1", "Annað");
			groupMap.put("2", "Fituleysanleg vítamín");
			groupMap.put("3", "Vatnsleysanleg vítamín");
			groupMap.put("4", "Steinefni");
			groupMap.put("5", "Snefilsteinefni");
			groupMap.put("6", "Fitusýrur");
			groupMap.put("7", "Nítröt");
		} else {
			groupMap.put("1", "Sugar");
			groupMap.put("2", "Sugar");
			groupMap.put("5", "Ash");
			groupMap.put("6", "Ash");
			groupMap.put("7", "Fat soluble vitamin");
			groupMap.put("8", "Water soluble vitamin");
			groupMap.put("9", "Fatty acids");
			groupMap.put("10", "Fatty acids");
			groupMap.put("11", "Fatty acids");
			groupMap.put("12", "Fatty acids");
			groupMap.put("13", "Fatty acids");
			groupMap.put("14", "Fatty acids");
			groupMap.put("15", "Fatty acids");
			groupMap.put("16", "Amino acids");
			groupMap.put("17", "Amino acids");
			groupMap.put("18", "Amino acids");			
		}
		
		showColumn = new ArrayList<Boolean>();
		for( int i = 0; i < stuff.get(0).length-2; i++ ) {
			if( !ngroupGroups.get(i).equals("6") ) showColumn.add( Boolean.TRUE );
			else showColumn.add( Boolean.FALSE );
		}
		
		nullModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}

			public Class<?> getColumnClass(int columnIndex) {
				return null;
			}

			public int getColumnCount() {
				return 0;
			}

			public String getColumnName(int columnIndex) {
				return null;
			}

			public int getRowCount() {
				return 0;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {}
			public void setValueAt(Object value, int rowIndex, int columnIndex) {}
		};
		
		detailTable = new JCompatTable() {
			public void sorterChanged( RowSorterEvent e ) {
				super.sorterChanged(e);
			}
		};
		
		TableCellEditor tce = null;
		try{ 
			tce = detailTable.getDefaultEditor( Boolean.class );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( tce != null ) {
			tce.addCellEditorListener( new CellEditorListener() {
				public void editingCanceled(ChangeEvent e) {}
	
				public void editingStopped(ChangeEvent e) {
					updateModels( table, topTable );
				}
			});
		}
		
		JPopupMenu popup = new JPopupMenu();
		Action action = new AbstractAction(lang.equals("IS") ? "Sýna alla" : "Show all") {
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					int k = detailTable.convertRowIndexToModel(i);
					showColumn.set(k, Boolean.TRUE );
				}
				detailTable.revalidate();
				detailTable.repaint();
				
				updateModels( table, topTable );
			}
		};
		//if( lang.equals("EN") ) action.
		popup.add( action );
		action = new AbstractAction(lang.equals("IS") ? "Fela alla" : "Hide all") {
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					int k = detailTable.convertRowIndexToModel(i);
					showColumn.set(k, Boolean.FALSE );
				}
				detailTable.revalidate();
				detailTable.repaint();
				
				updateModels( table, topTable );
			}
		};
		popup.add( action );
		action = new AbstractAction(lang.equals("IS") ? "Viðsnúa vali" : "Swap selection") {
			public void actionPerformed(ActionEvent e) {
				int[] rows = detailTable.getSelectedRows();
				detailTable.selectAll();
				for( int r : rows ) {
					detailTable.removeRowSelectionInterval(r, r);
				}
			}
		};
		popup.add( action );
		detailTable.setComponentPopupMenu( popup );
		detailTable.setAutoCreateRowSorter( true );
		detailModel = new TableModel() {			
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
	
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 3 ) return Float.class;
				if( columnIndex == 4 ) return Float.class;
				if( columnIndex == 5 ) return PercStr.class;
				else if( columnIndex == 6 ) return Boolean.class;
				return String.class;
			}
	
			public int getColumnCount() {
				return 7;
			}
	
			public String getColumnName(int columnIndex) {
				if( lang.equals("IS") ) {
					if( columnIndex == 0 ) return "Næringarefni";
					else if( columnIndex == 1 ) return "Næringarefnaflokkur";
					else if( columnIndex == 2 ) return "Eining";
					else if( columnIndex == 3 ) return "Innihald";
					else if( columnIndex == 4 ) return "Æskilegt magn";
					else if( columnIndex == 5 ) return "Hlutfall";
					return "Sýna dálk";
				} else {
					if( columnIndex == 0 ) return "Name";
					else if( columnIndex == 1 ) return "Group";
					else if( columnIndex == 2 ) return "Unit";
					else if( columnIndex == 3 ) return "Measure";
					else if( columnIndex == 4 ) return "Suggested daily req";
					else if( columnIndex == 5 ) return "Sugg daily req %";
					return "Show column";
				}
			}
	
			public int getRowCount() {
				return showColumn.size();
			}
	
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) return ngroupList.get( rowIndex );
				else if( columnIndex == 1 ) {
					if( lang.equals("IS") ) {
						String ind = ngroupGroups.get( rowIndex );
						if( ind.equals("1") ) {
							String colVal = ngroupList.get( rowIndex );
							if( colVal.startsWith("Orka") || colVal.equals("Fita") || colVal.equals("Prótein") || colVal.equals("Kolvetni") || colVal.equals("Alkóhól") ) {
								return "Orkuefni";
							}
						}
						
						if( groupMap.containsKey(ind) ) {
							String ret = groupMap.get( ind );
							if( ret.equals("Annað") ) {
								String efni = ngroupList.get( rowIndex );
								if( efni.contains("fitus") || efni.contains("Fjöl") || efni.contains("trans") ) {
									return "Fitusýrur";
								} else if( efni.contains("sykur") || efni.contains("Sykrur") || efni.contains("Trefjar") ) {
									return "Orkuefni";
								} else if( efni.contains("Vatn") ) {
									return "Vatn";
								} else if( efni.contains("Kólesteról") ) {
									return "Fituefni";
								} else if( efni.contains("Steinefni") ) {
									return "Steinefni";
								}
							}
							return ret;
						} else return "Óþekkt";
					} else {
						String ind = ngroupGroups.get( rowIndex );
						int val = Integer.parseInt(ind);
						if( val < 1000 ) {
							String colVal = ngroupList.get( rowIndex );
							if( colVal.startsWith("Energy") || colVal.equals("Fat") || colVal.equals("Protein") || colVal.equals("Carbohydrates") || colVal.equals("Alcohol") ) {
								return "Energy";
							}
						}
						
						String floor = Integer.toString(val/1000);
						if( groupMap.containsKey(floor) ) {
							String ret = groupMap.get( floor );
							if( ret.equals("Other") ) {
								String efni = ngroupList.get( rowIndex );
								if( efni.contains("Fatty") || efni.contains("Fjöl") || efni.contains("trans") ) {
									return "Fatty acids";
								} else if( efni.contains("sugar") || efni.contains("Sykrur") || efni.contains("Fiber") ) {
									return "Energy";
								} else if( efni.contains("Water") ) {
									return "Water";
								} else if( efni.contains("Cholestrol") ) {
									return "Fat";
								} else if( efni.contains("Ash") ) {
									return "Ash";
								}
							}
							return ret;
						} else return "Unknown";
					}
				} else if( columnIndex == 2 ) {
					Object[]	obj = stuff.get(1);
					return obj[ rowIndex+2 ];
					//return topModel.getValueAt( 1, rowIndex );
				} else if( columnIndex == 3 ) {
					int rsel = leftTable.getSelectedRow();
					if( rsel >= 0 && rsel < leftTable.getRowCount() ) {
						int lsel = leftTable.convertRowIndexToModel( rsel );
						Object obj = getVal( lsel, rowIndex, stuff, foodInd, recipes );
						if( obj != null && obj instanceof Float ) {
							float ff = (Float)obj;
							
							ff = (float)( Math.floor( (double)ff*10 )/10.0 );
							
							return ff;
						}
					}
				} else if( columnIndex == 4 ) {
					String efni = ngroupList.get( rowIndex );
					if( efni.equals("Fita") ) {
						String 	val = rdsp.getRds( "Orka - kcal" );
						int		kcal = 0;
						try {
							kcal = Integer.parseInt( val );
						} catch( Exception e ) {
							
						}
						double	kj = 4.184*kcal;
						double	d = kj*0.3;
						double	g = d / 37.0;
						return (float)(Math.floor(10.0*g)/10.0);
					} else if( efni.equals("Prótein") ) {
						String 	val = rdsp.getRds( "Orka - kcal" );
						int		kcal = 0;
						try {
							kcal = Integer.parseInt( val );
						} catch( Exception e ) {
							
						}
						double	kj = 4.184*kcal;
						double	d = kj*0.15;
						double	g = d / 17.0;
						return (float)(Math.floor(10.0*g)/10.0);
					} else if( efni.equals("Kolvetni") ) {
						String 	val = rdsp.getRds( "Orka - kcal" );
						int		kcal = 0;
						try {
							kcal = Integer.parseInt( val );
						} catch( Exception e ) {
							
						}
						double	kj = 4.184*kcal;
						double	d = kj*0.55;
						double	g = d / 17.0;
						return (float)(Math.floor(10.0*g)/10.0);
					}/*else if( efni.equals("Alkóhól, alls") ) {
						String 	val = rdsp.getRds( "Orka-Venjul/kCal" );
						int		kcal = Integer.parseInt( val );
						double	kj = 4.184*kcal;
						double	d = kj*0.3;
						double	g = d / 37.0;
					}*/
					String[] split = efni.split("[,-]+");
					//String[] fsplit = split[0].split("-");
					Object[]	obj = stuff.get(1);
					String ostr = (String)obj[ rowIndex+2 ];
					String rdsStr = split[0];//+" - "+ostr;
					float val = rdsp.getRdsf( rdsStr, ostr.trim() );
					if( val != -1 ) {
						return val;
						/*try {
							float f = Float.parseFloat(val);
							
							/*if( !split[1].equals( ostr ) ) {
								if( split[1].equals("g") ) {
									if( ostr.equals("mg") ) {
										f*=1000.0f;
									}
								} else if( split[1].equals("mg") ) {
									if( ostr.equals("g") ) {
										f/=1000.0f;
									}
								}
							}							
							return f;
						} catch( Exception e ) {
							
						}*/
					}
					return null;
				} else if( columnIndex == 5 ) {
					if( rowIndex == 0 || rowIndex == 1 ) {
						Float val = (Float)this.getValueAt(0, 3);
						Float rds = (Float)this.getValueAt(0, 4);
						
						double pc1 = -1.0;
						if( val != null && rds != null ) {
							pc1 = (100.0*val)/rds;
						}
						
						Float val2 = (Float)this.getValueAt(1, 3);
						Float rds2 = (Float)this.getValueAt(1, 4);
						
						double pc2 = -1.0;
						if( val2 != null && rds2 != null ) {
							pc2 = (100.0*val2)/rds2;
						}
						
						if( pc1 != -1.0 && pc2 != -1.0 ) {
							PercStr perc = new PercStr( (pc1 + pc2)/2.0 );
							return perc;
						} else if( pc1 != -1.0 ) {
							PercStr perc = new PercStr( pc1 );
							return perc;
						} else if( pc2 != -1.0 ) {
							PercStr perc = new PercStr( pc2 );
							return perc;
						} 
					} else {
						Float val = (Float)this.getValueAt(rowIndex, 3);
						Float rds = (Float)this.getValueAt(rowIndex, 4);
						
						if( val != null && rds != null ) {
							PercStr perc = new PercStr( (100.0f*val)/rds );
							return perc;
						}
					}
				} else {
					return showColumn.get( rowIndex );
				}
				return null;
			}
	
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 5 ) return true;
				return false;
			}
	
			public void removeTableModelListener(TableModelListener l) {}
	
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if( columnIndex == 5 ) showColumn.set( rowIndex, (Boolean)value );
			}
			
		};
		detailTable.setModel( detailModel );
		detailTable.getRowSorter().addRowSorterListener( new RowSorterListener() {
			public void sorterChanged(RowSorterEvent e) {
				updateModels( table, topTable );
			}
		});
		detailScroll.setViewportView( detailTable );
		TableColumn col = lang.equals("IS") ? detailTable.getColumn("Sýna dálk") : detailTable.getColumn("Show column");
		if( col != null ) {
			col.setMaxWidth( 100 );
		}
		
		popup.addSeparator();
		URL				xurl = this.getClass().getResource("/xlsx.png");
		if( xurl != null ) {
			ImageProducer 	ims = (ImageProducer)xurl.getContent();
			Image 		img = (ims != null ? this.createImage( ims ).getScaledInstance(16, 16, Image.SCALE_SMOOTH) : null);
			popup.add( new AbstractAction(lang.equals("IS") ? "Opna val í Excel" : "Open selection in Excel", img != null ? new ImageIcon(img) : null ) {
				public void actionPerformed(ActionEvent e) {
					try {
						sortTable.openExcel( detailTable, leftTable );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		table.setComponentPopupMenu( popup );
		
		for( int rowIndex = 0; rowIndex < detailModel.getRowCount(); rowIndex++ ) {
			String efni = ngroupList.get( rowIndex );
			String[] split = efni.split(",");
			Object[]	obj = stuff.get(1);
			Object ostr = obj[ rowIndex+2 ];
			
			String[] fsplt = split[0].split("-");
			String rdsStr = fsplt[0]+"/"+ostr;
			rdsStr = rdsStr.replace(" ", "");
			String val = rdsp.getRds( rdsStr );
			if( val != null ) {
				String ind = ngroupGroups.get( rowIndex );
				if( groupMap.containsKey(ind) ) {
					String grp = groupMap.get( ind );
					rdsp.detailMapping.put(rdsStr, grp);
				} 
			}
		}
		
		JComponent c = new JComponent() {
			
		};
		c.setLayout( new BorderLayout() );
		
		JComponent buttonPanel = new JComponent() {
			
		};
		buttonPanel.setLayout( new BorderLayout() );
		//buttonPanel.add( rotate );
		//buttonPanel.add( flip );
		
		c.add(buttonPanel);
		c.add(imgPanel);
		
		this.setTopComponent( imgPanel );
		this.setBottomComponent( detailScroll );
	}
}
