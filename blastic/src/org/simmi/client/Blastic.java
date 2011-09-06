package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.Sequences;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blastic implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.f
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	String user = null;
	
	private native String fetchUser() /*-{
		var user = null;
		try {
			var appli = $doc.getElementById('serify');
			$wnd.console.log( appli );
			user = appli.getUser();
			$wnd.console.log( user );
		} catch( e ) {
			$wnd.console.log( e );
		}
		return user;
	}-*/;
	
	private String getUser() {
		if( user == null ) user = fetchUser();
		return user;
	}
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;		
	
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
	
	public native void addSequenceInApplet( JavaScriptObject appletelement, String user, String name, String type, String path, int num, String key ) /*-{
		appletelement.updateSequences( user, name, type, path, num, key );
	}-*/;
	
	public void addSequence( final String user, final String name, final String type, final String path, final int num ) {
		final Sequences seqs = new Sequences( user, name, type, path, num );
		greetingService.saveSequences( seqs, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				Element applet = Document.get().getElementById("serify");
				addSequenceInApplet( applet, user, name, type, path, num, result );
				
				/*int r = data.getNumberOfRows();
				data.addRow();
				
				data.setValue(r, 0, seqs.getUser());
				data.setValue(r, 1, seqs.getName());
				data.setValue(r, 2, seqs.getType());
				data.setValue(r, 3, seqs.getPath());
				data.setValue(r, 4, seqs.getNum());
				
				view = DataView.create( data );
				table.draw( view, options );*/
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
	
	private native int browse( Element applet, String url ) /*-{
		e.browse( url );
	}-*/;
	
	public native int initFunctions() /*-{
		var s = this;
		
		$wnd.addDb = function( user, name, type, path, result ) {
			s.@org.simmi.client.Blastic::addDbInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( user, name, type, path, result );
		};
		
		$wnd.addResult = function( user, name, path, result ) {
			return s.@org.simmi.client.Blastic::addBlastInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( user, name, path, result );
		};
		
		$wnd.addSequences = function( user, name, type, path, nseq ) {
			s.@org.simmi.client.Blastic::addSequence(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)( user, name, path, type, nseq );
		};
		
		$wnd.getSequences = function( tableEmpty ) {
			return s.@org.simmi.client.Blastic::getSequences(Z)( tableEmpty );
		};
		
		$wnd.getSelectedDb = function() {
			return s.@org.simmi.client.Blastic::getSelectedDb()();
		};
		
		$wnd.deleteSequenceKey = function( key ) {
			s.@org.simmi.client.Blastic::deleteSequenceKey(Ljava/lang/String;)( key );
		};
		
		return 0;
	}-*/;
	
	public native void deleteSequenceInApplet( String key ) /*-{
		var applet = $doc.getElementById('serify');
		applet.deleteSequence( key );
	}-*/;
	
	public void deleteSequenceKey( final String key ) {
		greetingService.deleteKey( key, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				deleteSequenceInApplet( key );
			}
		});
	}
	
	public String getSelectedDb() {
		JsArray<Selection> jas = table.getSelections();
		
		console( "hoho " + jas.length() );
		if( jas.length() > 0 ) {
			int row = jas.get(0).getRow();
			String val = (String)data.getValueString(row, 3);
			console("erm " + val + " " + row );
			return val;
		}
		
		return null;
	}
	
	public void addBlastInfo( final String user, final String name, final String path, final String result ) {
		final Blast b = new Blast( user, name, "unk", path, result );
		greetingService.saveBlast( b, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String res) {
				b.setKey( res );
				blastList.add( b );
				addBlastToTable(user, name, path, result);
			}
		});
	}
	
	private void updateDatabaseTable() {
		for( Database db : databaseList ) {
			String user = db.getUser();
			String name = db.getName();
			String type = db.getType();
			String path = db.getPath();
			String result = db.getResult();
			addDbToTable(user, name, type, path, result);
		}
	}
	
	private void updateBlastTable() {
		for( Blast b : blastList ) {
			String user = b.getUser();
			String name = b.getName();
			//String type = b.getType();
			String path = b.getPath();
			String result = b.getResult();
			addBlastToTable(user, name, path, result);
		}
	}
	
	private void addBlastToTable( String user, String name, String path, String result ) {
		int r = blastdata.getNumberOfRows();
		
		blastdata.addRow();
		blastdata.setValue( r, 0, user );
		blastdata.setValue( r, 1, name );
		blastdata.setValue( r, 2, path );
		blastdata.setValue( r, 3, result );
		blastview = DataView.create( blastdata );
		blasttable.draw( blastview, blastoptions );
	}
	
	private void addDbToTable( String user, String name, String type, String path, String result ) {
		int r = data.getNumberOfRows();
		
		data.addRow();
		data.setValue( r, 0, user );
		data.setValue( r, 1, name );
		data.setValue( r, 2, type );
		data.setValue( r, 3, path );
		data.setValue( r, 4, result );
		view = DataView.create( data );
		table.draw( view, options );
	}
	
	private void addDbInfo( final String user, final String name, final String type, final String path, final String result ) {
		console("saving db");
		final Database db = new Database( user, name, type, path, result );
		greetingService.saveDb( db, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String res) {
				console("saving db succesful");
				db.setKey( res );
				databaseList.add( db );
				addDbToTable( user, name, type, path, result );
			}
		});
	}
	
	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	
	DataTable	blastdata;
	DataView	blastview;
	Table		blasttable;
	Options		blastoptions;
	
	List<Blast>		blastList = new ArrayList<Blast>();
	List<Database> 	databaseList = new ArrayList<Database>();
	//List<Sequences>	sequencesList = new ArrayList<Sequences>();
	
	boolean		hasFilled = false;
	
	public void getSequences( final boolean tableEmpty ) {
		greetingService.getSequences( new AsyncCallback<Sequences[]>() {
	    	@Override
			public void onSuccess(Sequences[] result) {
		    	Element e = Document.get().getElementById("serify");
				if( e != null || result != null ) {
					if( !hasFilled || (hasFilled && tableEmpty) ) {
						hasFilled = true;
			    		for( Sequences seqs : result ) {
			    			addSequenceInApplet( e, seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), (int)seqs.getNum(), seqs.getKey() );
						}
					}
				}
			}
		
			@Override
			public void onFailure(Throwable caught) {
				console( "get sequences failure" );
			}
	    });
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize((w-20)+"px", (h-20)+"px");
		
		final SplitLayoutPanel	slp = new SplitLayoutPanel();
		slp.setSize((w-20)+"px", (h-20)+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize((w-20)+"px", (h-20)+"px");
				slp.setSize((w-20)+"px", (h-20)+"px");
			}
		});
		
		initFunctions();
		
		//slp.getElement().getStyle().setBorderWidth(3.0, Unit.PX);
		
		//String source = "var attributes = { codebase:'http://130.208.252.31:8888/', archive:'serify.jar', code:'org.simmi.SerifyApplet', width:'80', height:'80', id:'serify', name:'serify' }\n";
        //source += "var parameters = { jnlp_href:'serify.jnlp' }\n";
        //source += "deployJava.runApplet(attributes, parameters, '1.6')\n";
		//ScriptElement se = Document.get().createScriptElement();
		//se.removeFromParent();
		
		//final ResizeLayoutPanel	applet = new ResizeLayoutPanel();
		final SimplePanel	applet = new SimplePanel();
		//ResizeLayoutPanel rp;
		final FocusPanel	tableviewfocus = new FocusPanel();
		final FocusPanel	blasttablefocus = new FocusPanel();
		//tableview.setWidth("100%");
		//tableview.setHeight("100%");
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "User");
		    	data.addColumn( ColumnType.STRING, "Name");
		    	data.addColumn( ColumnType.STRING, "Type");
		    	data.addColumn( ColumnType.STRING, "Path");
		    	data.addColumn( ColumnType.STRING, "Result");
		    	  
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
		    	  
		    	/*data.addRow();
				data.setValue( 0, 0, "erm" );
				data.setValue( 0, 1, "erm" );
				data.setValue( 0, 2, "erm" );
				data.setValue( 0, 3, "erm" );*/
				
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	blastdata = DataTable.create();
		    	blastdata.addColumn( ColumnType.STRING, "User");
		    	blastdata.addColumn( ColumnType.STRING, "Name");
		    	blastdata.addColumn( ColumnType.STRING, "Path");
		    	blastdata.addColumn( ColumnType.STRING, "Result");
		    	  
		    	blastoptions = Options.create();
		    	blastoptions.setWidth("100%");
		    	blastoptions.setHeight("100%");
		    	blastoptions.setAllowHtml( true );
		    	  
		    	blastview = DataView.create( blastdata );
		    	blasttable = new Table( blastview, blastoptions );		    	
		    	
		    	//dropHandler( table.getElement(), user );
		    	  
		    	//getSequences( true );
		    	greetingService.getDatabases( new AsyncCallback<Database[]>() {
			    	@Override
					public void onSuccess(Database[] dbs) {			    		
			    		databaseList.clear();
			    		for( Database b : dbs ) {			    			
			    			databaseList.add( b );
			    		}
			    		updateDatabaseTable();
			    		
						/*if( result != null ) {
				    		for( Sequences seqs : result ) {
				    			Element e = Document.get().getElementById("serify");
				    			addSeqs( e, seqs.getUser(), seqs.getName(), seqs.getType(), seqs.getPath(), seqs.getNum() );
							}
						}*/
					}
				
					@Override
					public void onFailure(Throwable caught) {
						console("fail get database");
					}
			    });
		    	
		    	greetingService.getBlastResults( new AsyncCallback<Blast[]>() {
			    	@Override
					public void onSuccess(Blast[] bbs) {
			    		blastList.clear();
			    		for( Blast b : bbs ) {			    			
			    			blastList.add( b );
			    		}
			    		updateBlastTable();
					}
				
					@Override
					public void onFailure(Throwable caught) {
							
					}
			    });
		    	
		    	table.setWidth("100%");
		    	table.setHeight("100%");
		    	tableviewfocus.add( table );
		    	blasttablefocus.add( blasttable );
		    	
		    	slp.addSouth( blasttablefocus, 300.0 );
		    	slp.addWest( applet, 500.0 );
		    	slp.add( tableviewfocus );
		    	  
		    	//tableview.add( table );
		    	//table.setWidth("640px");
		    	//table.setHeight("480px");
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		
		/*blasttablefocus.addKeyPressHandler( new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				event.get
			}
		});*/
		
		tableviewfocus.addKeyDownHandler( new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keycode = event.getNativeKeyCode();
				if( keycode == KeyCodes.KEY_DELETE ) {
					if( table != null ) {
						JsArray<Selection> sel = table.getSelections();
						if( sel.length() > 0 ) {
							Selection selection = sel.get(0);
							final int row = selection.getRow();
							String userinfo = data.getValueString(row, 0);
							if( userinfo.equals( getUser() ) ) {
								String key = databaseList.get( row ).getKey();
								greetingService.deleteKey( key, new AsyncCallback<String>() {
									@Override
									public void onFailure(Throwable caught) {
										
									}
	
									@Override
									public void onSuccess(String result) {
										databaseList.remove( row );
										data.removeRow(row);
										view = DataView.create( data );
										table.draw( view, options );
									}
								});
							}
						}
					}
				} else if( keycode == KeyCodes.KEY_ENTER ) {
					if( table != null ) {
						JsArray<Selection> sel = table.getSelections();
						if( sel.length() > 0 ) {
							Selection selection = sel.get(0);
							int row = selection.getRow();
							String path = data.getValueString(row, 3);
							
							browse( Document.get().getElementById("serify"), path );
						}
					}
				}
			}
		});
		
		blasttablefocus.addKeyDownHandler( new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keycode = event.getNativeKeyCode();
				if( keycode == KeyCodes.KEY_DELETE ) {
					if( blasttable != null ) {
						JsArray<Selection> sel = blasttable.getSelections();
						if( sel.length() > 0 ) {
							Selection selection = sel.get(0);
							final int row = selection.getRow();
							String userinfo = blastdata.getValueString(row, 0);
							String user = getUser();
							if( userinfo.equals( user ) ) {
								String key = blastList.get( row ).getKey();
								greetingService.deleteKey( key, new AsyncCallback<String>() {
									@Override
									public void onFailure(Throwable caught) {
										
									}
	
									@Override
									public void onSuccess(String result) {
										blastList.remove( row );
										blastdata.removeRow(row);
										blastview = DataView.create( blastdata );
										blasttable.draw( blastview, blastoptions );
									}
								});
							}
						}
					}
				} else if( keycode == KeyCodes.KEY_ENTER ) {
					if( blasttable != null ) {
						JsArray<Selection> sel = blasttable.getSelections();
						if( sel.length() > 0 ) {
							Selection selection = sel.get(0);
							int row = selection.getRow();
							String path = blastdata.getValueString(row, 2);
							
							browse( Document.get().getElementById("serify"), path );
						}
					}
				}
			}
		});
				
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "serify.jnlp");
		
		final Element ae = Document.get().createElement("applet");
		ae.appendChild( pe );
		
		ae.setAttribute("id", "serify");
		ae.setAttribute("name", "serify");
		ae.setAttribute("codebase", "http://127.0.0.1:8888/");
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
