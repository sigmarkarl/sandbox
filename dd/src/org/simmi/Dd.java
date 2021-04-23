package org.simmi;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class Dd extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Create a Box
        Box box = new Box(240, 240, 240);
        box.setTranslateX(360);
        box.setTranslateY(240);
        box.setTranslateZ(400);

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setSpecularColor(Color.ORANGE);
        redMaterial.setDiffuseColor(Color.RED);
        box.setMaterial(redMaterial);

        // Create a Sphere
        Sphere sphere = new Sphere(50);
        sphere.setTranslateX(300);
        sphere.setTranslateY(-5);
        sphere.setTranslateZ(400);

        // Create a Cylinder
        Cylinder cylinder = new Cylinder(40, 120);
        cylinder.setTranslateX(500);
        cylinder.setTranslateY(-25);
        cylinder.setTranslateZ(600);

        // Create a Light
        PointLight light = new PointLight();
        light.setTranslateX(720);
        light.setTranslateY(360);
        light.setTranslateZ(100);

        // Create a Camera to view the 3D Shapes
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(100);
        camera.setTranslateY(-50);
        camera.setTranslateZ(300);

        // Add the Shapes and the Light to the Group
        Group root = new Group(box, sphere, cylinder, light);

        // Create a Scene with depth buffer enabled
        Scene scene = new Scene(root, 400, 200, true);
        // Add the Camera to the Scene
        scene.setCamera(camera);

        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("An Example with Predefined 3D Shapes");
        // Display the Stage
        stage.show();
    }
}
