package org.simmi.distann;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.simmi.javafasta.shared.Function;
import org.simmi.javafasta.shared.Gene;
import org.simmi.javafasta.shared.GeneGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionTable extends TableView<Function> {
    boolean ftableisselecting = false;
    GeneGroupTable  table;
    GeneTable       gtable;

    FilteredList<Function> ffilteredData;
    SortedList<Function> fsortedData;
    GeneSetHead    geneSetHead;

    public void FunctionTable(GeneSetHead geneSetHead, GeneGroupTable table, GeneTable gtable) {
        this.geneSetHead = geneSetHead;
        this.table = table;
        this.gtable = gtable;
    }

    public void popuplate(List<Function> funclist) {
        ObservableList<Function> ofunc = FXCollections.observableList( funclist );
        ffilteredData = new FilteredList<>(ofunc, p -> true);
        fsortedData = new SortedList<>( ffilteredData );
        fsortedData.comparatorProperty().bind(this.comparatorProperty());
        setItems( fsortedData );
    }

    public void init() {
        var ftable = this;

        TableColumn<Function, String> gocol = new TableColumn<>("GO");
        gocol.setCellValueFactory( new PropertyValueFactory<>("go"));
        ftable.getColumns().add( gocol );
        TableColumn<Function, String> ecfcol = new TableColumn<>("EC");
        ecfcol.setCellValueFactory( new PropertyValueFactory<>("ec"));
        ftable.getColumns().add( ecfcol );
        TableColumn<Function, String> metacyccol = new TableColumn<>("MetaCyc");
        metacyccol.setCellValueFactory( new PropertyValueFactory<>("metacyc"));
        ftable.getColumns().add( metacyccol );
        TableColumn<Function, String> keggcol = new TableColumn<>("KEGG");
        keggcol.setCellValueFactory( new PropertyValueFactory<>("kegg"));
        ftable.getColumns().add( keggcol );
        TableColumn<Function, String> funcovcol = new TableColumn<>("Funciton coverage");
        funcovcol.setCellValueFactory( new PropertyValueFactory<>("speciesCount"));
        ftable.getColumns().add( funcovcol );
        TableColumn<Function, String> numprotcol = new TableColumn<>("Number of proteins");
        numprotcol.setCellValueFactory( new PropertyValueFactory<>("groupCount"));
        ftable.getColumns().add( numprotcol );

        TableColumn<Function, String> namecol = new TableColumn<>("Name");
        namecol.setCellValueFactory( new PropertyValueFactory<>("name"));
        ftable.getColumns().add( namecol );
        TableColumn<Function, String> namespacecol = new TableColumn<>("Namespace");
        namespacecol.setCellValueFactory( new PropertyValueFactory<>("namespace"));
        ftable.getColumns().add( namespacecol );
        TableColumn<Function, String> desccol = new TableColumn<>("Desc");
        desccol.setCellValueFactory( new PropertyValueFactory<>("desc"));
        ftable.getColumns().add( desccol );

        ftable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ftable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

        ftable.setOnMousePressed( e -> {
            ftableisselecting = true;
            Set<GeneGroup> ggset = new HashSet<>();
            if (!table.tableisselecting && e.getClickCount() == 2) {
                gtable.genefilterset.clear();
                for (Function f : ftable.getSelectionModel().getSelectedItems()) {
                    if (f.getGeneentries() != null) {
                        if( !geneSetHead.isGeneview() ) {
                            ggset.addAll( f.getGeneGroups() );
                        } else {
                            for( Gene g : f.getGeneentries() ) {
                                //Gene g = genemap.get(ref);
                                // int rf = table.convertRowIndexToView( g.index
                                // );
                                // table.addRowSelectionInterval(rf, rf);
                                gtable.genefilterset.add(g.index);
                            }
                        }
                    }
                }
                table.filteredData.setPredicate(ggset::contains);
            }
            ftableisselecting = false;
        });

        ftable.getSelectionModel().selectedItemProperty().addListener( e -> {
            ftableisselecting = true;
            if (!table.tableisselecting && gtable.genefilterset.isEmpty()) {
                table.getSelectionModel().clearSelection();
                //table.removeRowSelectionInterval(0, table.getRowCount() - 1);
                for (Function f : ftable.getSelectionModel().getSelectedItems()) {
                    if( f.getGeneentries() != null ) {
                        for( Gene g : f.getGeneentries() ) {
                            table.getSelectionModel().select( g.getGeneGroup() );

                            //Gene g = genemap.get(ref);
							/*int i = g.getGroupIndex();
							if( i >= 0 && i <= table.getItems().size() ) {
								int rf = table.convertRowIndexToView(i);
								table.addRowSelectionInterval(rf, rf);
							}*/
                        }
                    }
                }
            }
            ftableisselecting = false;
        });

        ftable.setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                ftable.ffilteredData.setPredicate(null);
            }
        });
    }
}
