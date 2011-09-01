package org.simmi.client;

import org.simmi.shared.Sequences;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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
	
	public native int dropHandler( JavaScriptObject table, String uid, String type ) /*-{
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
						//s.@org.simmi.client.Blastic::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, file.path, uid, type, res );
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
	
	public void fileLoad( String fileName, String filePath, String uid, String type, int num ) {
		final Sequences seqs = new Sequences( uid, fileName, type, filePath, num );
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
	
	public native int runApplet() /*-{
		var attributes = { codebase:'http://10.66.100.87:8888/', archive:'serify.jar', code:'org.simmi.SerifyApplet', width:'80', height:'80', id:'serify', name:'serify' };
        var parameters = { jnlp_href:'serify.jnlp' };
        $wnd.deployJava.runApplet(attributes, parameters, '1.6');
	}-*/;
	
	public native int resizeApplet( Element applet, String w, String h ) /*-{
		$wnd.console.log( w + ' ' + h );
		
		applet.setSize( w, h );
	}-*/;
	
	public native int initAddDb() /*-{
		var s = this;
		$wnd.addDb = function( user, name, path, result ) {
			s.@org.simmi.client.Blastic::addDbInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( user, name, path, result );
		};
	}-*/;
	
	public native int addSeqs( Element e, String user, String name, String type, String path, int num ) /*-{
		e.addSequences( "" );
	}-*/;
	
	public void addDbInfo( String user, String name, String path, String result ) {
		int r = data.getNumberOfRows();
		
		data.addRow();
		data.setValue( r, 0, user );
		data.setValue( r, 1, name );
		data.setValue( r, 2, path );
		data.setValue( r, 3, result );
		view = DataView.create( data );
		table.draw( view, options );
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
		
		initAddDb();
		
		final SplitLayoutPanel	slp = new SplitLayoutPanel();
		slp.setWidth("1000px");
		slp.setHeight("1000px");
		slp.getElement().getStyle().setBorderWidth(3.0, Unit.PX);
		
		//String source = "var attributes = { codebase:'http://130.208.252.31:8888/', archive:'serify.jar', code:'org.simmi.SerifyApplet', width:'80', height:'80', id:'serify', name:'serify' }\n";
        //source += "var parameters = { jnlp_href:'serify.jnlp' }\n";
        //source += "deployJava.runApplet(attributes, parameters, '1.6')\n";
		//ScriptElement se = Document.get().createScriptElement();
		//se.removeFromParent();
		
		//final ResizeLayoutPanel	applet = new ResizeLayoutPanel();
		final SimplePanel	applet = new SimplePanel();
		final SimplePanel	tableview = new SimplePanel();
		tableview.setWidth("100%");
		tableview.setHeight("100%");
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "User");
		    	data.addColumn( ColumnType.STRING, "Name");
		    	data.addColumn( ColumnType.STRING, "Path");
		    	data.addColumn( ColumnType.STRING, "Result");
		    	  
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
		    	  
		    	data.addRow();
				data.setValue( 0, 0, "erm" );
				data.setValue( 0, 1, "erm" );
				data.setValue( 0, 2, "erm" );
				data.setValue( 0, 3, "erm" );
				
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	//dropHandler( table.getElement(), user );
		    	  
		    	greetingService.getSequences( new AsyncCallback<Sequences[]>() {
			    	@Override
					public void onSuccess(Sequences[] result) {
						if( result != null ) {
				    		for( Sequences seqs : result ) {
				    			Element e = Document.get().getElementById("serify");
				    			addSeqs( e, seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNum() );
							}
						}
					}
				
					@Override
					public void onFailure(Throwable caught) {
							
					}
			    });
		    	
		    	table.setWidth("100%");
		    	table.setHeight("100%");
		    	tableview.add( table );
		    	slp.addWest( applet, 500.0 );
		    	slp.add( tableview );
		    	  
		    	//tableview.add( table );
		    	//table.setWidth("640px");
		    	//table.setHeight("480px");
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
				
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "serify.jnlp");
		
		final Element ae = Document.get().createElement("applet");
		ae.appendChild( pe );
		
		ae.setAttribute("id", "serify");
		ae.setAttribute("name", "serify");
		ae.setAttribute("codebase", "http://10.66.100.87:8888/");
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("jnlp_href", "serify.jnlp");
		ae.setAttribute("archive", "serify.jar");
		ae.setAttribute("code", "org.simmi.SerifyApplet");
		
		applet.getElement().appendChild( ae );
		
		/*applet.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resizeApplet( ae, event.getWidth()+"px", event.getHeight()+"px" );
			}
		});*/
    	
		rp.add( slp );
	}
}
