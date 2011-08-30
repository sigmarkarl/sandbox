package org.simmi.client;

import org.simmi.shared.Sequences;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blastic implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	String user = "";
	
	public native int dropHandler( JavaScriptObject table, String uid ) /*-{
		var s = this;
		
		function f1( evt ) {
			evt.stopPropagation();
			evt.preventDefault();
		};
		
		function f2( evt ) {
			
		};
		
		function ie( evt ) {
			f( evt );
		}
		
		function everythingelse( evt ) {
			f1( evt );
			f( evt );
		}
		
		function f( evt ) {	
			try {
				var files = evt.dataTransfer.files;		
				var count = files.length;

				if(count > 0) {
					var file = files[0];
					var reader = new FileReader();
					reader.onload = function(e) {
						var res = e.target.result;
						//s.@org.simmi.client.Blastic::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, file.path, uid, res );
					};
					reader.readAsBinaryString( file );
				}
			} catch( e ) {
				$wnd.alert( e );
			}
		};
		
		if( table.addEventListener ) {
			table.addEventListener( "dragenter", f1, false );
			table.addEventListener( "dragexit", f1, false );
			table.addEventListener( "dragover", f1, false );
			table.addEventListener( "drop", everythingelse, false );
		} else {
			table.attachEvent ("ondragenter", f2);
	        table.attachEvent ("ondragover", f2);
	        table.attachEvent ("ondragleave", f2);
	        table.attachEvent ("ondrop", ie);
		}
		
		return 0;
	}-*/;
	
	public void fileLoad( String fileName, String filePath, String uid, int num ) {
		final Sequences seqs = new Sequences( uid, fileName, filePath, num );
		greetingService.saveSequences( seqs, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				seqs.setKey( result );
				
				int r = data.getNumberOfRows();
				data.addRow();
				
				data.setValue(r, 0, seqs.getUser());
				data.setValue(r, 1, seqs.getName());
				data.setValue(r, 2, seqs.getPath());
				data.setValue(r, 3, seqs.getNum());
				
				view = DataView.create( data );
				table.draw( view, options );
			}
		});
	}
	
	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		final SplitLayoutPanel	slp = new SplitLayoutPanel();
		slp.setWidth("1000px");
		slp.setHeight("1000px");
		slp.getElement().getStyle().setBorderWidth(3.0, Unit.PX);
		
		String source = "var attributes = { codebase:'http://130.208.252.31:8888/', archive:'serify.jar', code:'org.simmi.SerifyApplet', width:'80', height:'80', id:'serify', name:'serify' }\n";
        source += "var parameters = { jnlp_href:'serify.jnlp' }\n";
        source += "deployJava.runApplet(attributes, parameters, '1.6')\n";
		ScriptElement se = Document.get().createScriptElement( source );
		//se.removeFromParent();
		
		final SimplePanel	applet = new SimplePanel();
		applet.getElement().appendChild( se );
		
		final SimplePanel	tableview = new SimplePanel();
		tableview.setWidth("640px");
		tableview.setHeight("480px");
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "User");
		    	data.addColumn( ColumnType.STRING, "Name");
		    	data.addColumn( ColumnType.STRING, "Path");
		    	data.addColumn( ColumnType.NUMBER, "#Seq");
		    	  
		    	options = Options.create();
		    	options.setWidth("640px");
		    	options.setHeight("480px");
		    	options.setAllowHtml( true );
		    	  
		    	data.addRow();
				data.setValue( 0, 0, "erm" );
				data.setValue( 0, 1, "erm" );
				data.setValue( 0, 2, "erm" );
				data.setValue( 0, 3, 1 );
				
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	//dropHandler( table.getElement(), user );
		    	  
		    	greetingService.getSequences( new AsyncCallback<Sequences[]>() {
			    	@Override
					public void onSuccess(Sequences[] result) {
						if( result != null ) {
				    		for( Sequences seqs : result ) {
								int r = data.getNumberOfRows();
								
								data.addRow();
								data.setValue( r, 0, seqs.getUser() );
								data.setValue( r, 1, seqs.getName() );
								data.setValue( r, 2, seqs.getPath() );
								data.setValue( r, 3, seqs.getNum() );
								view = DataView.create( data );
								table.draw( view, options );
							}
						}
					}
				
					@Override
					public void onFailure(Throwable caught) {
							
					}
			    });
		    	  
		    	tableview.add( table );
		    	table.setWidth("640px");
		    	table.setHeight("480px");
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
				
    	slp.add( tableview );
    	slp.addEast( applet, 100.0 );
    	
		rp.add( slp );
	}
}
