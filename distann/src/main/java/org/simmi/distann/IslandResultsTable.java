package org.simmi.distann;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.simmi.javafasta.shared.Cassette;
import org.simmi.javafasta.shared.Islinfo;

import java.util.List;

public class IslandResultsTable extends TableView<Cassette> {
    GeneSetHead geneSetHead;

    public IslandResultsTable(GeneSetHead geneSetHead) {
        this.geneSetHead = geneSetHead;
    }

    public void init() {

    }

    public void popuplate(List<String> specList) {
        for (String spec : specList) {
            TableColumn<Cassette, Islinfo> speccol = new TableColumn<>(spec);
            //speccol.getStyleClass().add("tabstyle");
            speccol.setCellFactory(cell -> {
                final TableCell<Cassette, Islinfo> tc = new TableCell<>() {
                    @Override
                    protected void updateItem(Islinfo item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || item.toString().length() == 0 || empty) {
                            setText(null);
                            setStyle("");
                            //getStyleClass().remove("tabcellstyle");
                        } else {
                            setText(item.toString());

                            //cellRender(this, item, 0);

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
                var isl = cellValue.getValue();
                Islinfo tes = isl.getInfo(spec);
                return new ReadOnlyObjectWrapper<>(tes);
                //return new SimpleStringProperty( tes != null ? tes.toString() : "" );
                //Teginfo ret = geneset.getGroupTes( cellValue.getValue(), spec );
                //return new ObservableValue<String>( ret.toString() );
                //return ret;
            });
            speccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> geneSetHead.itable.islandData.comparatorProperty().bind(comparatorProperty()));
            getColumns().add(speccol);
        }
    }

    public void focus() {
        ScrollBar scrollBarOne = (ScrollBar)geneSetHead.itable.lookup(".scroll-bar:vertical");
        ScrollBar scrollBarTwo = (ScrollBar)lookup(".scroll-bar:vertical");
        scrollBarOne.valueProperty().bindBidirectional(scrollBarTwo.valueProperty());
        selectionModelProperty().bindBidirectional(geneSetHead.itable.selectionModelProperty());

        scrollBarOne.setVisible(false);
        scrollBarOne.setMaxWidth(0.0);
    }
}
