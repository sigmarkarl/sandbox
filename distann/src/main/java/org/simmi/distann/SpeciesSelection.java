package org.simmi.distann;

import org.simmi.javafasta.shared.Sequence;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SpeciesSelection {
    GeneSet geneset;

    public SpeciesSelection(GeneSet geneset) {
        this.geneset = geneset;
    }

    int[]		currentRowSelection;
    public TransferHandler dragRows( final JTable table, final List<String> specs ) {
        TransferHandler th = null;
        try {
            final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
            final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
            final String charset = df.getParameter("charset");
            final Transferable transferable = new Transferable() {
                @Override
                public Object getTransferData(DataFlavor arg0) throws IOException {
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

    public Set<String> getSelspec( GeneSetHead comp, final List<String> specs, boolean custom, final JCheckBox ... contigs ) {
        final JTable	table = new JTable();
        table.setDragEnabled( true );
        JScrollPane	scroll = new JScrollPane( table );
        table.setAutoCreateRowSorter( true );

        final List<Sequence> ctgs = new ArrayList<>( geneset.contigmap.values() );
        final TableModel contigmodel = new TableModel() {
            @Override
            public int getRowCount() {
                return ctgs.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return "Contigs/Scaffolds";
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
                return ctgs.get( rowIndex ).toString();
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

            @Override
            public void addTableModelListener(TableModelListener l) {}

            @Override
            public void removeTableModelListener(TableModelListener l) {}
        };
        final TableModel specmodel = new TableModel() {
            @Override
            public int getRowCount() {
                return specs.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return columnIndex == 0 ? "Id" : "Spec";
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
                if( columnIndex == 1 ) return Sequence.nameFix(specs.get(rowIndex),true);
                else return specs.get(rowIndex);
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

            @Override
            public void addTableModelListener(TableModelListener l) {}

            @Override
            public void removeTableModelListener(TableModelListener l) {}
        };
        table.setModel( specmodel );

        TransferHandler th = dragRows( table, specs );
        table.setTransferHandler( th );

        if( !custom && contigs != null && contigs.length > 0 && !contigs[0].getText().equals("Plasmid") ) contigs[0].addChangeListener(e -> {
            if( contigs[0] != null && contigs[0].isSelected() ) table.setModel( contigmodel );
            else table.setModel( specmodel );
        });

        Object[] ctls = new Object[] { scroll, contigs };
        //Object[] ctls2 = new Object[] { scroll };

        //if( contigs != null )
        JOptionPane.showMessageDialog( null, ctls );
        //else JOptionPane.showMessageDialog( comp, ctls2 );
        Set<String>	selspec = new LinkedHashSet<>();
        for( int i = 0; i < table.getRowCount(); i++ ) {
            if( table.isRowSelected(i) ) {
                String spec = (String)table.getValueAt(i, 0);
                System.err.println("test " + spec);
                selspec.add( spec );
            }
        }
        return selspec;
    }
}
