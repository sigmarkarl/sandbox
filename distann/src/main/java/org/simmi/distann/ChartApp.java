package org.simmi.distann;

import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class ChartApp extends Application {
	String[] names;
	double[] xdata;
	double[] ydata;
	
	public static void main(String[] args) {
		launch( args );
	}
	
	public ChartApp() {
		
	}
	
	public ChartApp( String[] names, double[] xdata, double[] ydata ) {
		this.names = names;
		this.xdata = xdata;
		this.ydata = ydata;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
	    final NumberAxis xAxis = new NumberAxis(-0.5, 0.5, 0.025);
        final NumberAxis yAxis = new NumberAxis(-0.5, 0.5, 0.025);    
        final ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Dim 1");
        yAxis.setLabel("Dim 2");
        sc.setTitle("Genes");
       
        Random rnd = new Random();
        
        XYChart.Series<Number,Number> series1 = new XYChart.Series<Number,Number>();
        series1.setName("PCA");
        for( int i = 0; i < 5; i++ ) {
        	XYChart.Data<Number,Number> d = new XYChart.Data<Number,Number>( rnd.nextDouble()-0.5, rnd.nextDouble()-0.5 );
        	//Tooltip.install( d.getNode(), new Tooltip( "bleh" ) );
        	series1.getData().add( d );
        }
 
        sc.getData().addAll(series1);
        Scene scene = new Scene( sc );
        
        for (XYChart.Series<Number, Number> s : sc.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip("bleh") );
            }
        }
        
        stage.setScene( scene );
        stage.show();
	}
}
