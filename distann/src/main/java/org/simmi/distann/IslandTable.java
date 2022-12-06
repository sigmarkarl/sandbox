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
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Island;

import java.util.ArrayList;
import java.util.List;

public class IslandTable extends TableView<Island> {
    FilteredList<Island>                    islandFilteredList;
    SortedList<GeneGroup>                   sortedData2;
    SortedList<Island>						islandData;
    GeneSetHead                             geneSetHead;

    public IslandTable(GeneSetHead geneSetHead) {
        this.geneSetHead = geneSetHead;
    }

    public void init() {
        TableColumn<Island, String> islandcol = new TableColumn<>("Island");
        islandcol.setCellValueFactory( new PropertyValueFactory<>("name"));
        getColumns().add( islandcol );
        TableColumn<Island, String> islandsizecol = new TableColumn<>("Size");
        islandsizecol.setCellValueFactory( new PropertyValueFactory<>("size"));
        getColumns().add( islandsizecol );

        setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                Island seli = getSelectionModel().getSelectedItem();

                List<Island> sel = new ArrayList<>( islandFilteredList );
                islandFilteredList.setPredicate(null);
                int[] rows = sel.stream().mapToInt( g -> islandData.indexOf(g) ).toArray();
                if( rows.length > 0 ) getSelectionModel().selectIndices(rows[0], rows);
                geneSetHead.table.label.setText(getItems().size() + "/" + getSelectionModel().getSelectedIndices().size());
                scrollTo( seli );
            }
        });

        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        getSelectionModel().selectedItemProperty().addListener( e -> {
            geneSetHead.table.label.setText(getItems().size() + "/" + getSelectionModel().getSelectedItems().size());
        });
    }

    public void popuplate(List<Island> islands) {
        ObservableList<Island> oislands = FXCollections.observableList( islands );
        islandFilteredList = new FilteredList<>(oislands, p -> true);
        islandData = new SortedList<>( islandFilteredList );
        islandData.comparatorProperty().bind(comparatorProperty());
        setItems(islandData);
    }
}
