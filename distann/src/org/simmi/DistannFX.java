package org.simmi;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author sigmar
 */
public class DistannFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        SwingNode swing = new SwingNode();

        GeneSet gs = new GeneSet();
        swing.setContent(null);
        StackPane root = new StackPane();
        root.getChildren().add(swing);

        Scene scene = new Scene(root, 800, 600);

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