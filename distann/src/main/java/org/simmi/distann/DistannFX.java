package org.simmi.distann;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Window;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Function;
import org.simmi.javafasta.shared.Gene;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author sigmar
 */
public class DistannFX extends Application {
    Window owner;

    public Window getOwner() {
        return owner;
    }

    @Override
    public void start(Stage primaryStage) {
    	final VBox 		vbox = new VBox();
    	final MenuBar	menubar = new MenuBar();
    	final ToolBar	toolbar = new ToolBar();
    	final ToolBar	btoolbar = new ToolBar();
    	final TableView<Function> 	upper = new TableView<>();
        final TableView<GeneGroup>	lower = new TableView<>();
        final TableView<GeneGroup>  ggresults = new TableView<>();
        final TableView<Gene>		gene = new TableView<>();
        final TableView<Gene>       gresults = new TableView<>();
        final SplitPane ggsplit = new SplitPane(lower, ggresults);
        ggsplit.setOrientation(Orientation.HORIZONTAL );
        final SplitPane gsplit = new SplitPane(gene, gresults);
        gsplit.setOrientation(Orientation.HORIZONTAL );
    	final SplitPane	splitpane = new SplitPane(ggsplit, upper);
    	splitpane.setOrientation( Orientation.VERTICAL );

    	primaryStage.setOnCloseRequest( c -> Platform.exit() );
    	
    	vbox.getChildren().add( menubar );
    	vbox.getChildren().add( toolbar );

        GeneSet gs = new GeneSet();
        GeneSetHead gsh = new GeneSetHead( this, gs );
        //final JPanel panel = new JPanel();
        //panel.setLayout( new BorderLayout() );

        /*ggresults.getSelectionModel().setCellSelectionEnabled(true);
        gresults.getSelectionModel().setCellSelectionEnabled(true);
        ggresults.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gresults.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);*/
        gsh.init( primaryStage, null, splitpane, ggsplit, gsplit, gene, upper, lower, ggresults, gresults, menubar, toolbar, btoolbar );
        
        BorderPane root = new BorderPane();
        root.setTop( vbox );
        root.setBottom( btoolbar );
        root.setCenter( splitpane );

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("view/mystyle.css");

        owner = primaryStage.getOwner();
        primaryStage.setTitle("Genav");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if( args.length == 0 ) {
            launch(args);
		} else {
            System.err.println("heyho");
            GeneSet.main( args );
        }
    }

}
