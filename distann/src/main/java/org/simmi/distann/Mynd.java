package org.simmi.distann;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sigmar on 11/06/16.
 */
public class Mynd extends Application {
    @Override
    public void start(Stage primaryStage) {
        Image image = new Image("file:///Users/sigmar/image1.png");
        //ImageView   imgview = new ImageView(image);

        Group root = new Group();
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
        root.getChildren().add( canvas );
        Scene scene = new Scene(root, canvas.getWidth(), canvas.getHeight());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);

        double centerX = image.getWidth()/2.0-5;
        double centerY = centerX+3;
        double largeX = centerX-15;
        double largeY = centerY-15;
        double smallX = largeX - 45;
        double smallY = largeY -45;
        double fontX = largeX - 25;
        double fontY = largeY -25;

        gc.translate( centerX, centerY );
        //gc.scale(1, 1);
        gc.beginPath();
        //gc.moveTo(0, 0);
        //gc.lineTo(0, image.getHeight()/2.0);
        gc.arc(0,0,largeX+10, largeY+10, 90, 360);
        gc.lineTo( smallX*Math.cos(90*Math.PI/180.0), -smallY*Math.sin(90*Math.PI/180.0) );
        gc.arc(0,0,smallX, smallY, 90, -360);
        gc.lineTo( (largeX+10)*Math.cos(90*Math.PI/180.0), -(largeY+10)*Math.sin(90*Math.PI/180.0) );
        //gc.closePath();
        gc.fill();

        Font font = Font.font(gc.getFont().getFamily(), FontPosture.ITALIC,gc.getFont().getSize()+10.0);
        gc.setFont( font );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, 93, 61, "T.scotoductus", "2101", false );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, 60, 36, "T.scotoductus", "2127", false );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, 35, 0, "T.scotoductus", "252", false );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -1, -30, "T.eggertsoniae", true );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -31, -60, "T.brockianus", true );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -61, -90, "T.igniterrae", true );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -91, -123, "T.islandicus", true );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -124, -154, "T.oshimai", true );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -155, -216, "T.scotoductus", "346", false );
        fromTo( gc, largeX, largeY, smallX, smallY, fontX, fontY, -217, -266, "T.antranikianii", "and T.scotoductus 1572", false );

        WritableImage wim = new WritableImage( (int)canvas.getWidth(), (int)canvas.getHeight() );
        canvas.snapshot(null, wim);
        BufferedImage bim = SwingFXUtils.fromFXImage(wim, null);
        try {
            ImageIO.write( bim, "png", new File("/Users/sigmar/goli.png") );
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Mynd");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void fromTo( GraphicsContext gc, double largeX, double largeY, double smallX, double smallY, double fontX, double fontY, double start, double stop, String tscoto ) {
        fromTo(gc,largeX,largeY,smallX,smallY,fontX,fontY,start,stop,tscoto,null,false);
    }

    public static void fromTo( GraphicsContext gc, double largeX, double largeY, double smallX, double smallY, double fontX, double fontY, double start, double stop, String tscoto, boolean reverse ) {
        fromTo(gc,largeX,largeY,smallX,smallY,fontX,fontY,start,stop,tscoto,null,reverse);
    }

    public static void fromTo( GraphicsContext gc, double largeX, double largeY, double smallX, double smallY, double fontX, double fontY, double start, double stop, String tscoto, String subsp, boolean reverse ) {
        gc.beginPath();
        gc.arc(0,0,largeX, largeY, start, stop-start);
        gc.lineTo( smallX*Math.cos(stop*Math.PI/180.0), -smallY*Math.sin(stop*Math.PI/180.0) );
        gc.arc(0,0,smallX, smallY, stop, start-stop);
        gc.lineTo( largeX*Math.cos(start*Math.PI/180.0), -largeY*Math.sin(start*Math.PI/180.0) );
        gc.stroke();

        double middle = (start+stop)/2.0;

        gc.setFill(Color.BLACK);
        Text text = new Text( tscoto );
        double sw = text.getLayoutBounds().getWidth()/4.0;//+tscoto.length()/2.0;
        System.err.println(sw);
        for( int i = 0; i < tscoto.length(); i++ ) {
            text = new Text( tscoto.substring(0,i) );
            double fw = text.getLayoutBounds().getWidth()/4.0;//+i*tscoto.length()/20.0;
            System.err.println(fw);
            double horn = reverse ? middle+fw-sw/2.0 : middle-fw+sw/2.0;
            double x = (fontX + (reverse ? 10 : 0)) * Math.cos(horn * Math.PI / 180.0);
            double y = -(fontY + (reverse ? 10 : 0)) * Math.sin(horn * Math.PI / 180.0);
            gc.translate(x, y);
            gc.rotate( reverse ? -horn-90 : -horn+90 );
            gc.fillText(tscoto.substring(i,i+1),0,0);
            gc.rotate( reverse ? horn+90 : horn-90);
            gc.translate(-x, -y);
        }

        if( subsp != null ) {
            Font font = Font.font(gc.getFont().getFamily(), FontPosture.ITALIC, gc.getFont().getSize()-10.0);
            gc.setFont( font );
            text = new Text( subsp );
            sw = text.getLayoutBounds().getWidth()/6.0;//+tscoto.length()/2.0;
            for( int i = 0; i < subsp.length(); i++ ) {
                text = new Text( subsp.substring(0,i) );
                double fw = text.getLayoutBounds().getWidth()/6.0;//+i*tscoto.length()/20.0;
                double horn = reverse ? middle+fw-sw/2.0 : middle-fw+sw/2.0;
                double x = (fontX-15) * Math.cos(horn * Math.PI / 180.0);
                double y = -(fontY-15) * Math.sin(horn * Math.PI / 180.0);
                gc.translate(x, y);
                gc.rotate( reverse ? -horn-90 : -horn+90);
                gc.fillText(subsp.substring(i,i+1),0,0);
                gc.rotate( reverse ? horn+90 : horn-90);
                gc.translate(-x, -y);
            }
            font = Font.font(gc.getFont().getFamily(), FontPosture.ITALIC,gc.getFont().getSize()+10.0);
            gc.setFont( font );
        }
    }
}
