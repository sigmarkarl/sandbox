package org.simmi.distann;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.simmi.javafasta.shared.*;

import java.awt.*;
import java.util.List;
import java.util.Set;

import static org.simmi.distann.GeneGroupResultsTable.colorToString;

public class GeneResultsTable extends TableView<Gene> {
    GeneTable gtable;
    GeneSetHead geneSetHead;

    public GeneResultsTable(GeneSetHead geneSetHead, GeneTable gtable) {
        this.geneSetHead = geneSetHead;
        this.gtable = gtable;
    }

    public String colorToRGBString( Color c ) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        return "rgba("+r+","+g+","+b+",0.0)";
    }

    public boolean cellRenderGene( TableCell<Gene,Teg> cell, Teg tev ) {
        Annotation tv;
        if( tev instanceof Tegeval ) {
            tv = (Tegeval)tev;
        } else if( tev instanceof Teginfo ) {
            tv = ((Teginfo)tev).best;
        } else {
            cell.setStyle("-fx-background-color: white");
            return false;
        }
        String spec = geneSetHead.syncolorcomb.getSelectionModel().getSelectedItem();
        if( spec != null && spec.length() > 0 ) {
            if( spec.equals("All") ) {
                String tspec = tv.getGene().getSpecies();
                List<Sequence> scontigs = geneSetHead.geneset.speccontigMap.get( tspec );
                GeneGroup gg = tv.getGene().getGeneGroup();
                double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
                if( ratio == -1 ) {
                    ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
                    cell.setStyle( "-fx-background-color: "+colorToString(GeneCompare.gradientGrayscaleColor( ratio ))+(cell.isSelected()?";-fx-text-fill: black;":";-fx-text-fill: white;") );
                    //label.setForeground( Color.white );
                } else {
                    cell.setStyle( "-fx-background-color: "+colorToString(GeneCompare.gradientColor( ratio ))+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
                    //label.setForeground( Color.black );
                }
            } else {
                List<Sequence> contigs = geneSetHead.geneset.speccontigMap.get( spec );
                Annotation tv2 = null;
                GeneGroup gg = tv.getGene().getGeneGroup();
                int msimcount = 0;
                if( gg != null ) {
                    Teginfo		gene2s = gg.getGenes(spec);
                    if( gene2s != null && gene2s.tset != null ) for( Annotation tv1 : gene2s.tset ) {
                        int simcount = 0;

                        Annotation n = tv1.getNext();
                        Annotation p = tv1.getPrevious();
                        Annotation n2 = tv.getNext();
                        Annotation p2 = tv.getPrevious();

                        if( n != null ) {
                            GeneGroup ngg = n.getGene() == null ? null : n.getGene().getGeneGroup();
                            if( n2 != null && n2.getGene() != null ) {
                                if( ngg == n2.getGene().getGeneGroup() ) simcount++;
                            }

                            if( p2 != null && p2.getGene() != null ) {
                                if( ngg == p2.getGene().getGeneGroup() ) simcount++;
                            }
                        }

                        if( p != null ) {
                            GeneGroup pgg = p.getGene() == null ? null : p.getGene().getGeneGroup();
                            if( n2 != null && n2.getGene() != null ) {
                                if( pgg == n2.getGene().getGeneGroup() ) simcount++;
                            }

                            if( p2 != null && p2.getGene() != null ) {
                                if( pgg == p2.getGene().getGeneGroup() ) simcount++;
                            }
                        }

                        //double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
                        if( simcount >= msimcount ) {
                            tv2 = tv1;
                            msimcount = simcount;
                        }

                        //double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
                        //GeneCompare.gradientColor();
                    }
                }
                //double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
                double ratio = GeneCompare.invertedGradientRatio(spec, contigs, tv2);
                if( ratio == -1 ) {
                    ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, gg);
                    String color = colorToString(GeneCompare.gradientGrayscaleColor( ratio ));
                    cell.setStyle( "-fx-background-color: "+color+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
                    //label.setForeground( Color.black );
                } else {
                    String color = colorToString(GeneCompare.gradientColor( ratio ));
                    cell.setStyle( "-fx-background-color: "+color+(cell.isSelected()?";-fx-text-fill: white;":";-fx-text-fill: black;") );
                    //label.setForeground( Color.black );
                }
				/*double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, tv.getGene().getGeneGroup());
				label.setBackground( GeneCompare.gradientColor( ratio ) );*/
            }
        } else {
            Gene g = tv.getGene();
            GeneGroup gg = g.getGeneGroup();
            Teginfo ti = gg.species.get( g.getSpecies() );

            boolean phage = tv.isPhage();
            boolean plasmid = tv.getContshort() != null && tv.getContshort().isPlasmid();
            if( phage && plasmid ) {
                if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkmagenta" );
                else cell.setStyle( "-fx-background-color: magenta" );
            } else if( phage ) {
                if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkblue" );
                else cell.setStyle( "-fx-background-color: blue" );
            } else if( plasmid ) {
                if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkred" );
                else cell.setStyle( "-fx-background-color: red" );
            } else {
                if( ti.tset.size() > 1 ) cell.setStyle( "-fx-background-color: darkgreen" );
                else cell.setStyle( "-fx-background-color: green" );
            }
        }
        // label.setText( value.toString() );
		/*if (colorCodes[0] == null)
			GeneSet.setColors();
		if (tv.best.eval == 0) {
			label.setBackground(colorCodes[0]);
		} else if (tv.best.eval < 1e-100)
			label.setBackground(colorCodes[0]);
		else if (tv.best.eval < 1e-50)
			label.setBackground(colorCodes[1]);
		else if (tv.best.eval < 1e-24)
			label.setBackground(colorCodes[2]);
		else if (tv.best.eval < 1e-10)
			label.setBackground(colorCodes[3]);
		else if (tv.best.eval < 1e-5)
			label.setBackground(colorCodes[4]);
		else if (tv.best.eval < 1e-2)
			label.setBackground(colorCodes[5]);
		else if (tv.best.eval < 1e-1)
			label.setBackground(colorCodes[6]);
		else if (tv.best.eval < 1e0)
			label.setBackground(colorCodes[7]);
		else if (tv.best.eval < 1e10)
			label.setBackground(colorCodes[8]);*/
        return true;
    }

    public void populate(List<String> specList) {
        for( String spec : specList ) {
            TableColumn<Gene, Teg> gspeccol = new TableColumn<>(spec);
            gspeccol.setComparator((o1, o2) -> {
                if( o1 == null ) {
                    if( o2 == null ) {
                        return 0;
                    } else return -1;
                } else if( o1 instanceof Tegeval) {
                    if( o2 == null || o2 instanceof Teginfo ) {
                        return 1;
                    }
                } else {
                    if( o2 == null ) {
                        return 1;
                    } else if( o2 instanceof Tegeval ) {
                        return -1;
                    }
                }
                return o1.compareTo(o2);
            });
            gspeccol.setCellFactory( cell -> {
                final TableCell<Gene,Teg> tc = new TableCell<>() {
                    @Override
                    protected void updateItem(Teg item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || item.toString().length() == 0 || empty) {
                            setText("");
                            setStyle("");
                        } else {
                            boolean render = cellRenderGene(this, item);
                            setText(item.toString());
                            if (!render) setStyle("");
                            //getStyleClass().add("tabcellstyle");
				                /*if( (this.getTableRow() != null && getTableRow().isSelected()) || isSelected() ) {
				                	//setTextFill( javafx.scene.paint.Color.WHITE );
				                	setStyle("-fx-background-color: white");
				                } else {
				                	//setTextFill( javafx.scene.paint.Color.BLACK );
				                	setStyle("-fx-background-color: black");
				                }*/
                        }
                    }
                };
                return tc;
            });
            gspeccol.setCellValueFactory( cellValue -> {
                Gene g = cellValue.getValue();

                //Teginfo tes = g.tegeval;//getTes( spec );
                if( spec.equals(g.getSpecies()) ) {
                    return new ReadOnlyObjectWrapper(g.getTegeval());
                } else {
                    GeneGroup gg = g.getGeneGroup();
                    Set<String> specset = gg.getSpecies();
                    if( specset.contains(spec) ) {
                        var ti = gg.getGenes(spec);
                        return new ReadOnlyObjectWrapper<>(ti);
                    }
                }
                //return new SimpleStringProperty( tes != null ? tes.toString() : "" );
                //Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
                //return new ObservableValue<String>( ret.toString() );
                //return ret;
                return null;
            });

				/*gspeccol.setCellValueFactory( cellValue -> {
					return new SimpleStringProperty( cellValue.getValue().toString() );
					//Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
					//return new ObservableValue<String>( ret.toString() );
					//return ret;
				});*/
            getColumns().add( gspeccol );
        }
    }

    public void init() {
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /*results.setOnMouseClicked(event -> {
            var y = event.getY();
            if (y<27) table.sortedData.comparatorProperty().bind(results.comparatorProperty());
        });*/
        setOnMouseClicked(event -> {
            var y = event.getY();
            if (y<27) gtable.geneSortedList.comparatorProperty().bind(comparatorProperty());
        });
    }
}
