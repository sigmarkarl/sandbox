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
import org.simmi.javafasta.shared.Gene;
import org.simmi.javafasta.shared.ShareNum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneTable extends TableView<Gene> {
    final Set<Integer> genefilterset = new HashSet<>();

    FilteredList<Gene> geneFilteredList;
    SortedList<Gene> geneSortedList;

    GeneSetHead geneSetHead;

    public GeneTable(GeneSetHead geneSetHead) {
        this.geneSetHead = geneSetHead;
    }

    public void popuplate(List<Gene> genelist) {
        ObservableList<Gene> ogene = FXCollections.observableList( genelist );
        geneFilteredList = new FilteredList<>(ogene, p -> true);
        geneSortedList = new SortedList<>( geneFilteredList );
        geneSortedList.comparatorProperty().bind(comparatorProperty());
        setItems(geneSortedList);
    }

    public void init() {
        var gtable = this;

        TableColumn<Gene, String> gnamedesccol = new TableColumn<>("Desc");
        gnamedesccol.setCellValueFactory( new PropertyValueFactory<>("derivedName"));
        gtable.getColumns().add( gnamedesccol );
        TableColumn<Gene, String> gorigincol = new TableColumn<>("Origin");
        gorigincol.setCellValueFactory( new PropertyValueFactory<>("origin"));
        gtable.getColumns().add( gorigincol );
        TableColumn<Gene, String> ggeneidcol = new TableColumn<>("Genid");
        ggeneidcol.setCellValueFactory( new PropertyValueFactory<>("genid"));
        gtable.getColumns().add( ggeneidcol );
        TableColumn<Gene, String> grefidcol = new TableColumn<>("Refid");
        grefidcol.setCellValueFactory( new PropertyValueFactory<>("refid"));
        gtable.getColumns().add( grefidcol );
        TableColumn<Gene, String> gunidcol = new TableColumn<>("Unid");
        gunidcol.setCellValueFactory( new PropertyValueFactory<>("unid"));
        gtable.getColumns().add( gunidcol );
        TableColumn<Gene, String> gdescol = new TableColumn<>("Designation");
        gdescol.setCellValueFactory( new PropertyValueFactory<>("designation"));
        gtable.getColumns().add( gdescol );
        TableColumn<Gene, String> gkeggidcol = new TableColumn<>("Keggid");
        gkeggidcol.setCellValueFactory( new PropertyValueFactory<>("keggid"));
        gtable.getColumns().add( gkeggidcol );
        TableColumn<Gene, String> gkeggpathcol = new TableColumn<>("Kegg pathway");
        gkeggpathcol.setCellValueFactory( new PropertyValueFactory<>("keggPathway"));
        gtable.getColumns().add( gkeggpathcol );
        TableColumn<Gene, String> gkocol = new TableColumn<>("KO");
        gkocol.setCellValueFactory( new PropertyValueFactory<>("ko"));
        gtable.getColumns().add( gkocol );
        TableColumn<Gene, String> gksymbcol = new TableColumn<>("Ksymbol");
        gksymbcol.setCellValueFactory( new PropertyValueFactory<>("ksymbol"));
        gtable.getColumns().add( gksymbcol );
        TableColumn<Gene, String> gsymbcol = new TableColumn<>("Symbol");
        gsymbcol.setCellValueFactory( new PropertyValueFactory<>("symbol"));
        gtable.getColumns().add( gsymbcol );
        TableColumn<Gene, String> gkonamecol = new TableColumn<>("KO name");
        gkonamecol.setCellValueFactory( new PropertyValueFactory<>("koname"));
        gtable.getColumns().add( gkonamecol );
        TableColumn<Gene, String> hhpredcol = new TableColumn<>("Hhpred");
        hhpredcol.setCellValueFactory( new PropertyValueFactory<>("hhpred"));
        gtable.getColumns().add( hhpredcol );
        TableColumn<Gene, String> hhpredunicol = new TableColumn<>("HhpredUni");
        hhpredunicol.setCellValueFactory( new PropertyValueFactory<>("hhpreduni"));
        gtable.getColumns().add( hhpredunicol );
        TableColumn<Gene, String> gpbidcol = new TableColumn<>("Pbid");
        gpbidcol.setCellValueFactory( new PropertyValueFactory<>("pbid"));
        gtable.getColumns().add( gpbidcol );
        TableColumn<Gene, String> geccol = new TableColumn<>("Ec");
        geccol.setCellValueFactory( new PropertyValueFactory<>("ec"));
        gtable.getColumns().add( geccol );
        TableColumn<Gene, String> gcognamecol = new TableColumn<>("Cog name");
        gcognamecol.setCellValueFactory( new PropertyValueFactory<>("cogname"));
        gtable.getColumns().add( gcognamecol );
        TableColumn<Gene, String> gcogcol = new TableColumn<>("Cog");
        gcogcol.setCellValueFactory( new PropertyValueFactory<>("cog"));
        gtable.getColumns().add( gcogcol );
        TableColumn<Gene, String> gcogannocol = new TableColumn<>("Cog annotation");
        gcogannocol.setCellValueFactory( new PropertyValueFactory<>("coganno"));
        gtable.getColumns().add( gcogannocol );
        TableColumn<Gene, String> gcogsymbcol = new TableColumn<>("Cog symbol");
        gcogsymbcol.setCellValueFactory( new PropertyValueFactory<>("cogsymbol"));
        gtable.getColumns().add( gcogsymbcol );
        TableColumn<Gene, String> gcazycol = new TableColumn<>("Cazy");
        gcazycol.setCellValueFactory( new PropertyValueFactory<>("cazy"));
        gtable.getColumns().add( gcazycol );

        TableColumn<Gene, String> gdbcancol = new TableColumn<>("Dbcan");
        gdbcancol.setCellValueFactory( new PropertyValueFactory<>("dbcan"));
        gtable.getColumns().add( gdbcancol );

        TableColumn<Gene, String> gprescol = new TableColumn<>("Present in");
        gprescol.setCellValueFactory( new PropertyValueFactory<>("presentin"));
        gtable.getColumns().add( gprescol );

        TableColumn<Gene, Integer> ggroupindcol = new TableColumn<>("Group index");
        ggroupindcol.setCellValueFactory( new PropertyValueFactory<>("groupIndex"));
        gtable.getColumns().add( ggroupindcol );
        TableColumn<Gene, Integer> ggroupcovcol = new TableColumn<>("Group coverage");
        ggroupcovcol.setCellValueFactory( new PropertyValueFactory<>("groupCoverage"));
        gtable.getColumns().add( ggroupcovcol );
        TableColumn<Gene, Integer> ggroupsizecol = new TableColumn<>("Group size");
        ggroupsizecol.setCellValueFactory( new PropertyValueFactory<>("groupGeneCount"));
        gtable.getColumns().add( ggroupsizecol );

        TableColumn<Gene, String> glocprefcol = new TableColumn<>("Loc pref");
        glocprefcol.setCellValueFactory( new PropertyValueFactory<>("locpref"));
        gtable.getColumns().add( glocprefcol );
        TableColumn<Gene, String> gavgcpcol = new TableColumn<>("Avg GC%");
        gavgcpcol.setCellValueFactory( new PropertyValueFactory<>("avggcp"));
        gtable.getColumns().add( gavgcpcol );
        TableColumn<Gene, String> gmaxlencol = new TableColumn<>("Max length");
        gmaxlencol.setCellValueFactory( new PropertyValueFactory<>("maxLength"));
        gtable.getColumns().add( gmaxlencol );
        TableColumn<Gene, String> gnumloccol = new TableColumn<>("#Loc");
        gnumloccol.setCellValueFactory( new PropertyValueFactory<>("numloc"));
        gtable.getColumns().add( gnumloccol );
        TableColumn<Gene, String> gnumlocgroupcol = new TableColumn<>("#Loc group");
        gnumlocgroupcol.setCellValueFactory( new PropertyValueFactory<>("numlocgroup"));
        gtable.getColumns().add( gnumlocgroupcol );

        TableColumn<Gene, ShareNum> gsharenumcol = new TableColumn<>("Sharing number");
        gsharenumcol.setCellValueFactory( new PropertyValueFactory<>("sharingNumber"));
        gtable.getColumns().add( gsharenumcol );
        TableColumn<Gene, String> gmaxcyccol = new TableColumn<>("Max cyc");
        gmaxcyccol.setCellValueFactory( new PropertyValueFactory<>("maxCyc"));
        gtable.getColumns().add( gmaxcyccol );

        gtable.setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                Gene selg = getSelectionModel().getSelectedItem();

                List<Gene> sel = new ArrayList<>( geneFilteredList );
                geneFilteredList.setPredicate(null);
                int[] rows = sel.stream().mapToInt( g -> geneSortedList.indexOf(g) ).toArray();
                if( rows.length > 0 ) getSelectionModel().selectIndices(rows[0], rows);
                geneSetHead.table.label.setText(getItems().size() + "/" + getSelectionModel().getSelectedIndices().size());
                scrollTo( selg );
            }
        });

        gtable.setOnMouseClicked(event -> {
            var y = event.getY();
            if (y<27) geneSortedList.comparatorProperty().bind(comparatorProperty());
        });

        gtable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        gtable.getSelectionModel().selectedItemProperty().addListener( e -> {
            geneSetHead.table.label.setText(getItems().size() + "/" + getSelectionModel().getSelectedItems().size());
        });
    }
}
