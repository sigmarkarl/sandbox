package org.simmi.distann;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.simmi.javafasta.shared.Function;
import org.simmi.javafasta.shared.Gene;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.ShareNum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneGroupTable extends TableView<GeneGroup> {
    SortedList<GeneGroup> sortedData;
    FilteredList<GeneGroup> filteredData;
    Label label;
    boolean tableisselecting = false;
    FunctionTable   ftable;
    GeneTable       gtable;
    final Set<Integer> filterset = new HashSet<>();
    GeneSetHead geneSetHead;

    public GeneGroupTable(GeneSetHead geneSetHead, FunctionTable ftable, GeneTable gtable, Label label) {
        this.geneSetHead = geneSetHead;
        this.label = label;
        this.ftable = ftable;
        this.gtable = gtable;
    }

    public void popuplate(List<GeneGroup> allgenegroups) {
        ObservableList<GeneGroup> ogenegroup = FXCollections.observableList( allgenegroups );
        filteredData = new FilteredList<>(ogenegroup, p -> true);
        sortedData = new SortedList<>( filteredData );
        setItems( sortedData );
        sortedData.comparatorProperty().bind(comparatorProperty());
        setItems(sortedData);
    }

    public void init() {
        var table = this;

        TableColumn<GeneGroup, String> namedesccol = new TableColumn<>("Desc");
        namedesccol.setCellValueFactory( new PropertyValueFactory<>("name"));
        namedesccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sortedData.comparatorProperty().bind(table.comparatorProperty()));
        table.getColumns().add( namedesccol );
        TableColumn<GeneGroup, String> ggislandidcol = new TableColumn<>("IslandID");
        ggislandidcol.setCellValueFactory( new PropertyValueFactory<>("IslandId"));
        table.getColumns().add( ggislandidcol );
        TableColumn<GeneGroup, String> ggislandsizecol = new TableColumn<>("IslandSize");
        ggislandsizecol.setCellValueFactory( new PropertyValueFactory<>("IslandSize"));
        table.getColumns().add( ggislandsizecol );
        TableColumn<GeneGroup, String> trianglecol = new TableColumn<>("SingleInsert");
        trianglecol.setCellValueFactory( new PropertyValueFactory<>("Triangle"));
        table.getColumns().add( trianglecol );
        TableColumn<GeneGroup, String> labelcol = new TableColumn<>("Label");
        labelcol.setCellValueFactory( new PropertyValueFactory<>("Label"));
        table.getColumns().add( labelcol );
        TableColumn<GeneGroup, String> pagerankcol = new TableColumn<>("PageRank");
        pagerankcol.setCellValueFactory( new PropertyValueFactory<>("PageRank"));
        table.getColumns().add( pagerankcol );
        TableColumn<GeneGroup, String> connectedcol = new TableColumn<>("Connected");
        connectedcol.setCellValueFactory( new PropertyValueFactory<>("Connected"));
        table.getColumns().add( connectedcol );
        TableColumn<GeneGroup, String> origincol = new TableColumn<>("Origin");
        origincol.setCellValueFactory( new PropertyValueFactory<>("origin"));
        table.getColumns().add( origincol );
        TableColumn<GeneGroup, String> geneidcol = new TableColumn<>("Genid");
        geneidcol.setCellValueFactory( new PropertyValueFactory<>("genid"));
        table.getColumns().add( geneidcol );
        TableColumn<GeneGroup, String> refidcol = new TableColumn<>("Refid");
        refidcol.setCellValueFactory( new PropertyValueFactory<>("refid"));
        table.getColumns().add( refidcol );
        TableColumn<GeneGroup, String> unidcol = new TableColumn<>("Unid");
        unidcol.setCellValueFactory( new PropertyValueFactory<>("unid"));
        table.getColumns().add( unidcol );
        TableColumn<GeneGroup, String> descol = new TableColumn<>("Designation");
        descol.setCellValueFactory( new PropertyValueFactory<>("designation"));
        table.getColumns().add( descol );
        TableColumn<GeneGroup, String> goidcol = new TableColumn<>("Goid");
        goidcol.setCellValueFactory( new PropertyValueFactory<>("goid"));
        table.getColumns().add( goidcol );
        TableColumn<GeneGroup, String> keggpathcol = new TableColumn<>("Kegg pathway");
        keggpathcol.setCellValueFactory( new PropertyValueFactory<>("keggPathway"));
        table.getColumns().add( keggpathcol );
        TableColumn<GeneGroup, String> kocol = new TableColumn<>("KO");
        kocol.setCellValueFactory( new PropertyValueFactory<>("ko"));
        table.getColumns().add( kocol );
        TableColumn<GeneGroup, String> cazy = new TableColumn<>("Cazy");
        cazy.setCellValueFactory( new PropertyValueFactory<>("cazy"));
        table.getColumns().add( cazy );

        TableColumn<GeneGroup, String> dbcan = new TableColumn<>("Dbcan");
        dbcan.setCellValueFactory( new PropertyValueFactory<>("dbcan"));
        table.getColumns().add( dbcan );

        TableColumn<GeneGroup, String> phaster = new TableColumn<>("Phrog");
        phaster.setCellValueFactory( new PropertyValueFactory<>("phrog"));
        table.getColumns().add( phaster );

        TableColumn<GeneGroup, String> hhpred = new TableColumn<>("HHPred");
        hhpred.setCellValueFactory( new PropertyValueFactory<>("hhblits"));
        table.getColumns().add( hhpred );

        TableColumn<GeneGroup, String> hhpreduni = new TableColumn<>("HHPredUni");
        hhpreduni.setCellValueFactory( new PropertyValueFactory<>("hhblitsuni"));
        table.getColumns().add( hhpreduni );

        TableColumn<GeneGroup, String> symbcol = new TableColumn<>("Symbol");
        symbcol.setCellValueFactory( new PropertyValueFactory<>("symbol"));
        table.getColumns().add( symbcol );
        TableColumn<GeneGroup, String> konamecol = new TableColumn<>("KO name");
        konamecol.setCellValueFactory( new PropertyValueFactory<>("koname"));
        table.getColumns().add( konamecol );
        TableColumn<GeneGroup, String> pbidcol = new TableColumn<>("Pfam");
        pbidcol.setCellValueFactory( new PropertyValueFactory<>("Pfam"));
        table.getColumns().add( pbidcol );
        TableColumn<GeneGroup, String> eccol = new TableColumn<>("Ec");
        eccol.setCellValueFactory( new PropertyValueFactory<>("ec"));
        table.getColumns().add( eccol );
        TableColumn<GeneGroup, String> cognamecol = new TableColumn<>("Cog name");
        cognamecol.setCellValueFactory( new PropertyValueFactory<>("cogname"));
        table.getColumns().add( cognamecol );
        TableColumn<GeneGroup, String> cogcol = new TableColumn<>("Cog");
        cogcol.setCellValueFactory( new PropertyValueFactory<>("cog"));
        table.getColumns().add( cogcol );
        TableColumn<GeneGroup, String> cogannocol = new TableColumn<>("Cog annotation");
        cogannocol.setCellValueFactory( new PropertyValueFactory<>("coganno"));
        table.getColumns().add( cogannocol );
        TableColumn<GeneGroup, String> cogsymbcol = new TableColumn<>("Cog symbol");
        cogsymbcol.setCellValueFactory( new PropertyValueFactory<>("cogsymbol"));
        table.getColumns().add( cogsymbcol );

        TableColumn<GeneGroup, String> oldsymbcol = new TableColumn<>("Original cogsymbol");
        oldsymbcol.setCellValueFactory( new PropertyValueFactory<>("oldsymbol"));
        table.getColumns().add( oldsymbcol );

        TableColumn<GeneGroup, String> cazyaa = new TableColumn<>("Cazy_AA");
        cazyaa.setCellValueFactory( new PropertyValueFactory<>("cazyAA"));
        table.getColumns().add( cazyaa );
        TableColumn<GeneGroup, String> cazyce = new TableColumn<>("Cazy_CE");
        cazyce.setCellValueFactory( new PropertyValueFactory<>("cazyCE"));
        table.getColumns().add( cazyce );
        TableColumn<GeneGroup, String> cazygh = new TableColumn<>("Cazy_GH");
        cazygh.setCellValueFactory( new PropertyValueFactory<>("cazyGH"));
        table.getColumns().add( cazygh );
        TableColumn<GeneGroup, String> cazygt = new TableColumn<>("Cazy_GT");
        cazygt.setCellValueFactory( new PropertyValueFactory<>("cazyGT"));
        table.getColumns().add( cazygt );
        TableColumn<GeneGroup, String> cazypl = new TableColumn<>("Cazy_PL");
        cazypl.setCellValueFactory( new PropertyValueFactory<>("cazyPL"));
        table.getColumns().add( cazypl );
        TableColumn<GeneGroup, String> prescol = new TableColumn<>("Present in");
        prescol.setCellValueFactory( new PropertyValueFactory<>("presentin"));
        table.getColumns().add( prescol );

        TableColumn<GeneGroup, Integer> groupindcol = new TableColumn<>("Group index");
        groupindcol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupIndex"));
        table.getColumns().add( groupindcol );
        TableColumn<GeneGroup, Integer> groupcovcol = new TableColumn<>("Group coverage");
        groupcovcol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupCoverage"));
        table.getColumns().add( groupcovcol );
        TableColumn<GeneGroup, Integer> groupsizecol = new TableColumn<>("Group size");
        groupsizecol.setCellValueFactory( new PropertyValueFactory<GeneGroup,Integer>("groupGeneCount"));
        table.getColumns().add( groupsizecol );

        TableColumn<GeneGroup, String> locprefcol = new TableColumn<>("Loc pref");
        locprefcol.setCellValueFactory( new PropertyValueFactory<>("locpref"));
        table.getColumns().add( locprefcol );
        TableColumn<GeneGroup, String> avgcpcol = new TableColumn<>("Avg GC%");
        avgcpcol.setCellValueFactory( new PropertyValueFactory<>("avggcp"));
        table.getColumns().add( avgcpcol );
        TableColumn<GeneGroup, String> maxlencol = new TableColumn<>("Max length");
        maxlencol.setCellValueFactory( new PropertyValueFactory<>("maxLength"));
        table.getColumns().add( maxlencol );
        TableColumn<GeneGroup, String> numloccol = new TableColumn<>("#Loc");
        numloccol.setCellValueFactory( new PropertyValueFactory<>("numloc"));
        table.getColumns().add( numloccol );
        TableColumn<GeneGroup, String> numlocgroupcol = new TableColumn<>("#Loc group");
        numlocgroupcol.setCellValueFactory( new PropertyValueFactory<>("numlocgroup"));
        table.getColumns().add( numlocgroupcol );

        TableColumn<GeneGroup, ShareNum> sharenumcol = new TableColumn<>("Sharing number");
        sharenumcol.setCellValueFactory( new PropertyValueFactory<>("sharingNumber"));
        table.getColumns().add( sharenumcol );
        TableColumn<GeneGroup, String> maxcyccol = new TableColumn<>("Max cyc");
        maxcyccol.setCellValueFactory( new PropertyValueFactory<>("maxCyc"));
        table.getColumns().add( maxcyccol );

        TableColumn<GeneGroup, Boolean> selectedcol = new TableColumn<>("Selected");
        selectedcol.setCellValueFactory(param -> param.getValue().selectedProperty());
        selectedcol.setCellFactory(CheckBoxTableCell.forTableColumn(selectedcol));
        selectedcol.setEditable(true);
        table.getColumns().add( selectedcol );

        table.getSelectionModel().selectedItemProperty().addListener( e -> {
            if(geneSetHead.isTableSelectListenerEnabled) {
                label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
                // table.clearSelection();
                tableisselecting = true;
                if (!ftable.ftableisselecting && filterset.isEmpty()) {
                    //ftable.removeRowSelectionInterval(0, ftable.getRowCount() - 1);
                    if (!geneSetHead.isGeneview()) {
                        for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
                            for (Function f : gg.getFunctions()) {
                                try {
                                    ftable.getSelectionModel().select(f);
                                    //int rf = ftable.convertRowIndexToView(f.index);
                                    //if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    } else {
                        for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
                            if (g.funcentries != null) {
                                for (Function f : g.funcentries) {
                                    //Function f = funcmap.get(go);
                                    try {
                                        ftable.getSelectionModel().select(f);
                                        //int rf = ftable.convertRowIndexToView(f.index);
                                        //if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                tableisselecting = false;
            }
        });

        table.setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                GeneGroup selgg = table.getSelectionModel().getSelectedItem();

                List<GeneGroup> sel = new ArrayList<>( filteredData );
                filteredData.setPredicate(null);
                int[] rows = sel.stream().mapToInt( gg -> sortedData.indexOf(gg) ).toArray();
                if( rows.length > 0 ) table.getSelectionModel().selectIndices(rows[0], rows);
                if (label != null)
                    label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());

                table.scrollTo( selgg );
                //genefilterset.clear();
                //updateFilter(table, genefilter, label);
                //geneset.scrollToSelection( table );
            }
        });

        table.setOnMousePressed( e -> {
            tableisselecting = true;
            if (!ftable.ftableisselecting && e.getClickCount() == 2) {
                /*
                 * int[] rr = ftable.getSelectedRows(); int minr =
                 * ftable.getRowCount(); int maxr = 0; for( int r : rr ) {
                 * if( r < minr ) minr = r; if( r > maxr ) maxr = r; }
                 * ftable.removeRowSelectionInterval(minr, maxr);
                 */
                // ftable.removeRowSelectionInterval(0, filterset.isEmpty()
                // ? ftable.getRowCount()-1 : filterset.size()-1 );

                Set<Function> fset = new HashSet<>();
                filterset.clear();
                if( !geneSetHead.isGeneview() ) {
                    for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
                        fset.addAll( gg.getFunctions() );
                    }
                } else {
                    for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
                        if (g.funcentries != null) {
                            for( Function f : g.funcentries ) {
                                //Function f = funcmap.get(go);
                                // ftable.getRowSorter().convertRowIndexToView(index)
                                // int rf = ftable.convertRowIndexToView(
                                // f.index );
                                filterset.add(f.index);
                                // ftable.addRowSelectionInterval(rf, rf);
                            }
                        }
                    }
                }
                ftable.ffilteredData.setPredicate(fset::contains);
            }
            tableisselecting = false;
        });

        table.setOnMouseClicked(event -> {
            var y = event.getY();
            if (y<27) sortedData.comparatorProperty().bind(comparatorProperty());
        });

        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		/*table.getSelectionModel().selectedItemProperty().addListener( e -> {
			label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
		});*/
    }
}
