package org.simmi;

import org.simmi.shared.Function;
import org.simmi.shared.Gene;
import org.simmi.shared.GeneGroup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author sigmar
 */
public class DistannFX extends Application {

    @Override
    public void start(Stage primaryStage) {
    	final VBox 		vbox = new VBox();
    	final MenuBar	menubar = new MenuBar();
    	final ToolBar	toolbar = new ToolBar();
    	final ToolBar	btoolbar = new ToolBar();
    	final TableView<Function> 	upper = new TableView<Function>();
        final TableView<GeneGroup>	lower = new TableView<GeneGroup>();
        final TableView<Gene>		gene = new TableView<Gene>();
    	final SplitPane	splitpane = new SplitPane(lower, upper);
    	splitpane.setOrientation( Orientation.VERTICAL );
    	
    	vbox.getChildren().add( menubar );
    	vbox.getChildren().add( toolbar );

        GeneSet gs = new GeneSet();
        GeneSetHead gsh = new GeneSetHead( gs );
        //final JPanel panel = new JPanel();
        //panel.setLayout( new BorderLayout() );
        gsh.init( null, splitpane, gene, upper, lower, menubar, toolbar, btoolbar );
        
        BorderPane root = new BorderPane();
        root.setTop( vbox );
        root.setBottom( btoolbar );
        root.setCenter( splitpane );

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("view/mystyle.css");

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
        launch(args);
    }

}
