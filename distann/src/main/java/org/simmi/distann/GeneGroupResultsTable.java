package org.simmi.distann;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.simmi.javafasta.shared.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneGroupResultsTable extends TableView<GeneGroup> {
    GeneSetHead     geneSetHead;
    GeneGroupTable  table;
    FunctionTable   ftable;
    GeneTable       gtable;

    public GeneGroupResultsTable(GeneSetHead geneSetHead, FunctionTable ftable, GeneTable gtable, GeneGroupTable table) {
        this.geneSetHead = geneSetHead;
        this.ftable = ftable;
        this.gtable = gtable;
        this.table = table;
    }

    public static String colorToString( Color c ) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        String red = r < 16 ? 0+Integer.toString(r, 16) : Integer.toString(r, 16);
        String green = g < 16 ? 0+Integer.toString(g, 16) : Integer.toString(g, 16);
        String blue = b < 16 ? 0+Integer.toString(b, 16) : Integer.toString(b, 16);

        return "#"+red+green+blue;
    }

    public void cellRender( TableCell<GeneGroup,Teginfo> cell, Teginfo ti, int row ) {
        cell.setStyle( "-fx-background-color: white" );
        String spec = geneSetHead.syncolorcomb.getSelectionModel().getSelectedItem();
        if( spec != null && spec.length() > 0 ) {
            if( spec.equals("All") ) {
                cell.setStyle( "-fx-background-color: green" );
                for( Annotation tv : ti.tset ) {
                    Gene gene = tv.getGene();
                    if(gene!=null) {
                        String tspec = gene.getSpecies();
                        List<Sequence> scontigs = geneSetHead.geneset.speccontigMap.get(tspec);
                        GeneGroup gg = tv.getGene().getGeneGroup();
                        double ratio = GeneCompare.invertedGradientRatio(tspec, scontigs, -1.0, gg, tv);
                        if (ratio == -1) {
                            ratio = GeneCompare.invertedGradientPlasmidRatio(tspec, scontigs, -1.0, gg);
                            cell.setStyle("-fx-background-color: " + colorToString(GeneCompare.gradientGrayscaleColor(ratio)));
                            //label.setForeground( Color.white );
                        } else {
                            cell.setStyle("-fx-background-color: " + colorToString(GeneCompare.gradientColor(ratio)));
                            //label.setForeground( Color.black );
                        }
                    }
                    break;
                    //GeneCompare.gradientColor();
                }
            } else {
                List<Sequence> contigs = geneSetHead.geneset.speccontigMap.get(spec);
                cell.setStyle("-fx-background-color: green");

                //GeneGroup 	gg = ti.best.getGene().getGeneGroup();
                //Teginfo		gene2s = gg.getGenes(spec);
                //double ratio = -1.0;
                int msimcount = 0;
                Annotation tv2;

                GeneGroup gg = geneSetHead.table.getItems().get(row);
                if (gg != null) {
                    Teginfo gene2s = gg.getGenes(spec);
                    if( gene2s != null ) {
                        Annotation tv = gene2s.best;
                        tv2 = gene2s.best;
                        if (gene2s.tset != null) for (Annotation tv1 : gene2s.tset) {
                            int simcount = 0;

                            Annotation n = tv1.getNext();
                            Annotation p = tv1.getPrevious();
                            Annotation n2 = tv2.getNext();
                            Annotation p2 = tv2.getPrevious();

                            if (n != null && n.getGene() != null) {
                                GeneGroup ngg = n.getGene().getGeneGroup();
                                if (n2 != null && n2.getGene() != null) {
                                    if (ngg == n2.getGene().getGeneGroup()) simcount++;
                                }

                                if (p2 != null && p2.getGene() != null) {
                                    if (ngg == p2.getGene().getGeneGroup()) simcount++;
                                }
                            }

                            if (p != null && p.getGene()!=null) {
                                GeneGroup pgg = p.getGene().getGeneGroup();
                                if (n2 != null && n2.getGene() != null) {
                                    if (pgg == n2.getGene().getGeneGroup()) simcount++;
                                }

                                if (p2 != null && p2.getGene() != null) {
                                    if (pgg == p2.getGene().getGeneGroup()) simcount++;
                                }
                            }

                            //double rat = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
                            if (simcount >= msimcount) {
                                tv = tv1;
                                msimcount = simcount;
                            }

                            //double ratio = GeneCompare.invertedGradientRatio(spec, contigs, -1.0, gg, tv);
                            //GeneCompare.gradientColor();
                        }

                        double ratio = GeneCompare.invertedGradientRatio(spec, contigs, tv);
                        if (ratio == -1) {
                            ratio = GeneCompare.invertedGradientPlasmidRatio(spec, contigs, -1.0, gg);
                            String color = colorToString(GeneCompare.gradientGrayscaleColor(ratio));
                            cell.setStyle("-fx-background-color: " + color);
                            //label.setForeground( Color.black );
                        } else {
                            String color = colorToString(GeneCompare.gradientColor(ratio));
                            cell.setStyle("-fx-background-color: " + color);
                            //label.setForeground( Color.black );
                        }
                    }
                }
            }
        } else {
            boolean plasmid = false;
            boolean phage = false;
            for( Annotation tv : ti.tset ) {
                phage = phage | tv.isPhage();
                Sequence seq = tv.getContshort();
                plasmid = plasmid | (seq != null && seq.isPlasmid());
            }

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
    }

    public void populate(List<String> specList) {
        for (String spec : specList) {
            TableColumn<GeneGroup, Teginfo> speccol = new TableColumn<>(spec);
            //speccol.getStyleClass().add("tabstyle");
            speccol.setCellFactory(cell -> {
                final TableCell<GeneGroup, Teginfo> tc = new TableCell<>() {
                    @Override
                    protected void updateItem(Teginfo item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || item.toString().length() == 0 || empty) {
                            setText(null);
                            setStyle("");
                            //getStyleClass().remove("tabcellstyle");
                        } else {
                            setText(item.toString());
                            cellRender(this, item, 0);
                            //getStyleClass().add("tabcellstyle");
				                /*if( (this.getTableRow() != null && getTableRow().isSelected()) || isSelected() ) {
				                	//setTextFill( javafx.scene.paint.Color.WHITE );
				                	setStyle("-fx-background-color: darkgreen");
				                } else {
				                	//setTextFill( javafx.scene.paint.Color.BLACK );
				                	setStyle("-fx-background-color: green");
				                }*/
                        }
                    }
                };
                return tc;
            });
            speccol.setCellValueFactory(cellValue -> {
                GeneGroup gg = cellValue.getValue();
                Teginfo tes = gg.getTes(spec);
                return new ReadOnlyObjectWrapper<>(tes);
                //return new SimpleStringProperty( tes != null ? tes.toString() : "" );
                //Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
                //return new ObservableValue<String>( ret.toString() );
                //return ret;
            });
            speccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> table.sortedData.comparatorProperty().bind(comparatorProperty()));
            getColumns().add(speccol);
        }

        ScrollBar scrollBarOne = (ScrollBar)table.lookup(".scroll-bar:vertical");
        ScrollBar scrollBarTwo = (ScrollBar)lookup(".scroll-bar:vertical");
        scrollBarOne.valueProperty().bindBidirectional(scrollBarTwo.valueProperty());
        selectionModelProperty().bindBidirectional(table.selectionModelProperty());

        scrollBarOne.setVisible(false);
        scrollBarOne.setMaxWidth(0.0);
    }

    public void init() {
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        var results = this;
        results.setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                GeneGroup selgg = table.getSelectionModel().getSelectedItem();
                List<GeneGroup> sel = new ArrayList<>( table.filteredData );
                table.filteredData.setPredicate(null);
                int[] rows = sel.stream().mapToInt( gg -> table.sortedData.indexOf(gg) ).toArray();
                if( rows.length > 0 ) table.getSelectionModel().selectIndices(rows[0], rows);
                table.label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());
                table.scrollTo( selgg );
            }
        });

        results.setOnMousePressed( e -> {
            table.tableisselecting = true;
            if (!ftable.ftableisselecting && e.getClickCount() == 2) {
                Set<Function> fset = new HashSet<>();
                table.filterset.clear();
                if( !geneSetHead.isGeneview() ) {
                    for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
                        fset.addAll( gg.getFunctions() );
                    }
                } else {
                    for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
                        if (g.funcentries != null) {
                            for( Function f : g.funcentries ) {
                                table.filterset.add(f.index);
                            }
                        }
                    }
                }
                ftable.ffilteredData.setPredicate(fset::contains);
            }
            table.tableisselecting = false;
        });
    }
}
