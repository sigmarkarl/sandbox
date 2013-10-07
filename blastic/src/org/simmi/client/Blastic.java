package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.Machine;
import org.simmi.shared.Sequences;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
		if( user == null ) user = guid;//fetchUser();
		return user;
	}
	
	public native void console( String log ) /*-{
		applet = $doc.getElementById( 'serify' );
		//applet.console( log );
		//$wnd.console.log( log );
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
	
	public native void runBlastInApplet( JavaScriptObject appletelement, String extrapar, String dbPath, String dbType ) /*-{
		try {
			appletelement.runBlastInApplet( extrapar, dbPath, dbType );
		} catch( e ) {
			console.log( e );
		}
	}-*/;
	
	public native void addSequenceInApplet( JavaScriptObject appletelement, String user, String name, String type, String path, int num, String key ) /*-{
		appletelement.updateSequences( user, name, type, path, num, key );
	}-*/;
	
	public native String getMachineName( JavaScriptObject appletelement ) /*-{
		if( appletelement.getMachine ) {
			return appletelement.getMachine();
		}
		return null;
	}-*/;
	
	public void addSequence( final String user, final String name, final String type, final String path, final int num ) {
		final Sequences seqs = new Sequences( user, name, type, path, num );
		greetingService.saveSequences( seqs, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				console("bluh");
				
				try {
					console("one");
					Element applet = Document.get().getElementById("serify");
					console("two");
					addSequenceInApplet( applet, user, name, type, path, num, result );
					console("trhe");
				} catch( Exception e ) {
					console( e.getMessage() );
				}
				
				console("bleh");
				
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
		
		$wnd.getUser = function( user, name, type, path, machine, result ) {
			return s.@org.simmi.client.Blastic::getUser()();
		};
		
		$wnd.addDb = function( user, name, type, path, machine, result ) {
			s.@org.simmi.client.Blastic::addDbInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( user, name, type, path, machine, result );
		};
		
		$wnd.addResult = function( user, name, path, machine, start, stop, result ) {
			return s.@org.simmi.client.Blastic::addBlastInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( user, name, path, machine, start, stop, result );
		};
		
		$wnd.addSequences = function( user, name, type, path, nseq ) {
			s.@org.simmi.client.Blastic::addSequence(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)( user, name, type, path, nseq );
		};
		
		$wnd.getSequences = function( tableEmpty ) {
			return s.@org.simmi.client.Blastic::getSequences(Z)( tableEmpty );
		};
		
		$wnd.getSelectedDb = function() {
			return s.@org.simmi.client.Blastic::getSelectedDb()();
		};
		
		$wnd.getSelectedBlast = function() {
			return s.@org.simmi.client.Blastic::getSelectedBlast()();
		};
		
		$wnd.deleteSequenceKey = function( key ) {
			s.@org.simmi.client.Blastic::deleteSequenceKey(Ljava/lang/String;)( key );
		};
		
		$wnd.initMachines = function( hostname, procs ) {
			s.@org.simmi.client.Blastic::initMachines(Ljava/lang/String;I)( hostname, procs );
		};
		
		$wnd.getBlastParameters = function() {
			s.@org.simmi.client.Blastic::getBlastParameters()();
		};
		
		return 0;
	}-*/;
	
	public void initMachines( String hostname, int procs ) {		
		try {
			greetingService.getMachineInfo( hostname, procs, new AsyncCallback<Machine[]>() {
				@Override
				public void onSuccess(Machine[] mcs) {
					machineList.clear();
					for( Machine m : mcs ) {    			
						machineList.add( m );
					}
					updateMachineTable();
				}
			
				@Override
				public void onFailure(Throwable caught) {
					StackTraceElement[] stes = caught.getStackTrace();
					for( StackTraceElement ste : stes ) {
						console( ste.toString() );
					}
				}
			});
		} catch (Exception e) {
			console( "something amiss "+e.getMessage() );
		}
	}
	
	public void runBlast( String extrapar ) {
		String[][] dbInfos = getSelectedDbs();
		
		if( dbInfos.length > 0 ) {
			String dbPath = "";
			String dbType = null;
			
			//if( dbInfos.length > 1 ) dbPath += "'";
			for( String[] stra : dbInfos ) {
				if( dbType == null ) dbType = stra[1];
				else if( !dbType.equals(stra[1]) ) {
					dbType = "";
					break;
				}
				if( !stra[0].equals(dbInfos[0][0]) ) dbPath += " ";
				dbPath += stra[0];
			}
			//if( dbInfos.length > 1 ) dbPath += "'";
	
			if( !dbType.equals("") ) {
				Element e = Document.get().getElementById("serify");
				runBlastInApplet(e, extrapar, dbPath, dbType );
			}
		}
	}
	
	public void getBlastParameters() {
		DialogBox d = new DialogBox( true );
		Caption c = d.getCaption();
		c.setText("Blast parameters");
		
		//VerticalPanel	vp = new VerticalPanel();
		//d.add( vp );
		
		Grid	grid = new Grid(3, 2);
		
		final CheckBox	eval = new CheckBox("evalue");
		final CheckBox	numal = new CheckBox("num_alignments");
		final CheckBox	numde = new CheckBox("num_descriptions");
		
		final DoubleBox			evalinput = new DoubleBox();
		final IntegerBox		numalinput = new IntegerBox();
		final IntegerBox		numdeinput = new IntegerBox();
		
		grid.setWidget(0, 0, eval);
		grid.setWidget(0, 1, evalinput);
		grid.setWidget(1, 0, numal);
		grid.setWidget(1, 1, numalinput);
		grid.setWidget(2, 0, numde);
		grid.setWidget(2, 1, numdeinput);
		
		d.add( grid );
		
		d.addCloseHandler( new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String extrapar = "";
				if( eval.getValue() ) {
					extrapar += " -evalue "+evalinput.getValue();
				}
				if( numal.getValue() ) {
					extrapar += " -num_alignments "+numalinput.getValue();
				}
				if( numde.getValue() ) {
					extrapar += " -num_descriptions "+numdeinput.getValue();
				}
				runBlast( extrapar );
			}
		});
		
		d.center();
	}
	
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
	
	public String getSelectedBlast() {
		JsArray<Selection> jas = blasttable.getSelections();
		
		if( jas.length() > 0 ) {
			int row = jas.get(0).getRow();
			String path = (String)blastdata.getValueString(row, 2);
			//String typ = (String)blastdata.getValueString(row, 2);
			return path; //new String[] {val, typ};
		}
		
		return null;
	}
	
	public String[][] getSelectedDbs() {
		JsArray<Selection> jas = table.getSelections();
		
		List<String[]>	slist = new ArrayList<String[]>();
		for( int i = 0; i < jas.length(); i++ ) {
			int row = jas.get(i).getRow();
			String val = (String)data.getValueString(row, 3);
			String typ = (String)data.getValueString(row, 2);
			slist.add( new String[] {val, typ} );
		}
		
		return slist.toArray( new String[0][] );
	}
	
	public String[] getSelectedDb() {
		JsArray<Selection> jas = table.getSelections();
		
		if( jas.length() > 0 ) {
			int row = jas.get(0).getRow();
			String val = (String)data.getValueString(row, 3);
			String typ = (String)data.getValueString(row, 2);
			return new String[] {val, typ};
		}
		
		return null;
	}
	
	public void addBlastInfo( final String user, final String name, final String path, final String machine, final String start, final String stop, final String result ) {
		System.err.println("add blastinfo");
		
		final Blast b = new Blast( user, name, "unk", path, machine, start, stop, result );
		greetingService.saveBlast( b, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String res) {
				b.setKey( res );
				blastList.add( b );
				addBlastToTable(user, name, path, machine, start, stop, result);
			}
		});
	}
	
	public void addMachineInfo( final String name, final int nproc, final int inuse, final boolean on ) {
		final Machine m = new Machine( name, nproc, on );
		greetingService.saveMachine( m, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String res) {
				m.setKey( res );
				machineList.add( m );
				addMachineToTable(name, nproc, inuse, on);
			}
		});
	}
	
	private void updateDatabaseTable() {
		for( Database db : databaseList ) {
			String user = db.getUser();
			String name = db.getName();
			String type = db.getType();
			String path = db.getPath();
			String machine = db.getMachine();
			String result = db.getResult();
			addDbToTable(user, name, type, path, machine, result);
		}
	}
	
	private void updateBlastTable() {
		for( Blast b : blastList ) {
			String user = b.getUser();
			String name = b.getName();
			//String type = b.getType();
			String path = b.getPath();
			String machine = b.getMachine();
			String	startDate = b.getStart();
			String	stopDate = b.getStop();
			String result = b.getResult();
			addBlastToTable(user, name, path, machine, startDate, stopDate, result);
		}
	}
	
	private void updateMachineTable() {
		for( Machine m : machineList ) {
			String name = m.getName();
			int nproc = m.getProcs();
			int inuse = m.getInuse();
			boolean on = m.getOn();
			addMachineToTable( name, nproc, inuse, on );
		}
	}

	private void addMachineToTable( String name, int nproc, int inuse, boolean on ) {
		if( machinedata != null ) {
			int r = machinedata.getNumberOfRows();
			
			machinedata.addRow();
			machinedata.setValue( r, 0, name );
			machinedata.setValue( r, 1, nproc );
			machinedata.setValue( r, 2, inuse );
			machinedata.setValue( r, 3, on );
			machineview = DataView.create( machinedata );
			machinetable.draw( machineview, machineoptions );
		}
	}
	
	private void addBlastToTable( String user, String name, String path, String machine, String startDate, String stopDate, String result ) {
		int r = blastdata.getNumberOfRows();
		
		blastdata.addRow();
		blastdata.setValue( r, 0, user );
		blastdata.setValue( r, 1, name );
		blastdata.setValue( r, 2, path );
		blastdata.setValue( r, 3, machine );
		blastdata.setValue( r, 4, startDate );
		blastdata.setValue( r, 5, stopDate );
		blastdata.setValue( r, 6, result );
		blastview = DataView.create( blastdata );
		blasttable.draw( blastview, blastoptions );
	}
	
	private void addDbToTable( String user, String name, String type, String path, String machine, String result ) {
		int r = data.getNumberOfRows();
		
		data.addRow();
		data.setValue( r, 0, user );
		data.setValue( r, 1, name );
		data.setValue( r, 2, type );
		data.setValue( r, 3, path );
		data.setValue( r, 4, machine );
		data.setValue( r, 5, result );
		view = DataView.create( data );
		table.draw( view, options );
	}
	
	private void addDbInfo( final String user, final String name, final String type, final String path, final String machine, final String result ) {
		console("saving db");
		final Database db = new Database( user, name, type, path, machine, result );
		greetingService.saveDb( db, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String res) {
				console("saving db succesful");
				db.setKey( res );
				databaseList.add( db );
				addDbToTable( user, name, type, path, machine, result );
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
	
	DataTable	machinedata;
	DataView	machineview;
	Table		machinetable;
	Options		machineoptions;
	
	List<Blast>		blastList = new ArrayList<Blast>();
	List<Database> 	databaseList = new ArrayList<Database>();
	List<Machine> 	machineList = new ArrayList<Machine>();
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
	
	String guid;
	String login;
	String logout;
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		NodeList<com.google.gwt.dom.client.Element> nl = Document.get().getElementsByTagName("meta");
		int i;
		for( i = 0; i < nl.getLength(); i++ ) {
			com.google.gwt.dom.client.Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			
			if( prop.equals("guid") ) {
				guid = e.getAttribute("content");
			} else if( prop.equals("login") ) {
				login = e.getAttribute("content");
			} else if( prop.equals("logout") ) {
				logout = e.getAttribute("content");
			}
		}
		
		final VerticalPanel	vp = new VerticalPanel();
		
		Window.enableScrolling( false );
		Style st = rp.getElement().getStyle();
		st.setBorderWidth( 0.0, Unit.PX );
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize((w)+"px", (h)+"px");
		vp.setSize((w)+"px", (h)+"px");
		
		final SplitLayoutPanel	slp = new SplitLayoutPanel();
		slp.setSize((w)+"px", (h-25)+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize((w)+"px", (h)+"px");
				vp.setSize((w)+"px", (h)+"px");
				slp.setSize((w)+"px", (h-25)+"px");
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
		
		final ResizeLayoutPanel tableviewresize = new ResizeLayoutPanel();
		final ResizeLayoutPanel blasttableresize = new ResizeLayoutPanel();
		final ResizeLayoutPanel machinetableresize = new ResizeLayoutPanel();
		final FocusPanel	tableviewfocus = new FocusPanel();
		final FocusPanel	blasttablefocus = new FocusPanel();
		final FocusPanel	machinefocus = new FocusPanel();
		//tableview.setWidth("100%");
		//tableview.setHeight("100%");
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "User");
		    	data.addColumn( ColumnType.STRING, "Name");
		    	data.addColumn( ColumnType.STRING, "Type");
		    	data.addColumn( ColumnType.STRING, "Path");
		    	data.addColumn( ColumnType.STRING, "Machine");
		    	data.addColumn( ColumnType.STRING, "Result");
		    	  
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
				
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	blastdata = DataTable.create();
		    	blastdata.addColumn( ColumnType.STRING, "User");
		    	blastdata.addColumn( ColumnType.STRING, "Name");
		    	blastdata.addColumn( ColumnType.STRING, "Path");
		    	blastdata.addColumn( ColumnType.STRING, "Machine");
		    	blastdata.addColumn( ColumnType.STRING, "Start date");
		    	blastdata.addColumn( ColumnType.STRING, "Stop date");
		    	blastdata.addColumn( ColumnType.STRING, "Result");
		    	  
		    	blastoptions = Options.create();
		    	blastoptions.setWidth("100%");
		    	blastoptions.setHeight("100%");
		    	blastoptions.setAllowHtml( true );
		    	  
		    	blastview = DataView.create( blastdata );
		    	blasttable = new Table( blastview, blastoptions );
		    	
		    	machinedata = DataTable.create();
		    	machinedata.addColumn( ColumnType.STRING, "Name");
		    	machinedata.addColumn( ColumnType.NUMBER, "Procs");
		    	machinedata.addColumn( ColumnType.NUMBER, "In use");
		    	machinedata.addColumn( ColumnType.BOOLEAN, "On/Off");
		    	  
		    	machineoptions = Options.create();
		    	machineoptions.setWidth("100%");
		    	machineoptions.setHeight("100%");
		    	machineoptions.setAllowHtml( true );
		    	  
		    	machineview = DataView.create( machinedata );
		    	machinetable = new Table( machineview, machineoptions );
		    	
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
		    	
		    	/*if( machinedata.getNumberOfRows() < machineList.size() ) {
		    		Element ae = Document.get().getElementById("serify");
		    		String machinename = getMachineName( ae );
		    		if( machinename != null ) {
		    			String[] split = machinename.split("\t");
		    	    	greetingService.getMachineInfo( split[0], Integer.parseInt(split[1]), new AsyncCallback<Machine[]>() {
		    		    	@Override
		    				public void onSuccess(Machine[] mcs) {
		    		    		machineList.clear();
		    		    		for( Machine m : mcs ) {			    			
		    		    			machineList.add( m );
		    		    		}
		    		    		updateMachineTable();
		    				}
		    			
		    				@Override
		    				public void onFailure(Throwable caught) {
		    						
		    				}
		    		    });
		    		}
		    	}*/
		    	
		    	table.setWidth("100%");
		    	table.setHeight("100%");
		    			    	
		    	tableviewresize.add( table );
		    	tableviewfocus.add( tableviewresize );
		    	
		    	tableviewfocus.addDropHandler( new DropHandler() {
					@Override
					public void onDrop(DropEvent event) {
						
					}
		    	});
		    	
		    	tableviewresize.setSize("100%", "100%");
		    	
		    	tableviewresize.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						int w = event.getWidth();
						int h = event.getHeight();
						
						//console( w + " " + h );
						
						options.setWidth(w+"px");
						options.setHeight(h+"px");
						//view = DataView.create( data );
						table.setSize(w+"px", h+"px");
						table.draw( view, options );
						table.setSize(w+"px", h+"px");
					}
		    	});
		    	
		    	blasttableresize.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						int w = event.getWidth();
						int h = event.getHeight();
						
						blastoptions.setWidth(w+"px");
						blastoptions.setHeight(h+"px");
						blasttable.setSize(w+"px", h+"px");
						blasttable.draw( blastview, blastoptions );
						blasttable.setSize(w+"px", h+"px");
					}
		    	});
		    	
		    	machinetableresize.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						int w = event.getWidth();
						int h = event.getHeight();
						
						machineoptions.setWidth(w+"px");
						machineoptions.setHeight(h+"px");
						machinetable.setSize(w+"px", h+"px");
						machinetable.draw( machineview, machineoptions );
						machinetable.setSize(w+"px", h+"px");
					}
		    	});
		    	
		    	blasttable.setWidth("100%");
		    	blasttable.setHeight("100%");
		    	machinetable.setWidth("100%");
		    	machinetable.setHeight("100%");
		    	
		    	blasttableresize.add( blasttable );
		    	blasttablefocus.add( blasttableresize );
		    	machinetableresize.add( machinetable );
		    	machinefocus.add( machinetableresize );
		    	
		    	machinetableresize.setSize("100%", "100%");
		    	blasttableresize.setSize("100%", "100%");
		    	
		    	slp.addEast( machinefocus, 200.0 );
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
		ae.setAttribute("codebase", "http://funblastic.appspot.com/");
		//ae.setAttribute("codebase", "http://127.0.0.1/");
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("jnlp_href", "serify.jnlp");
		ae.setAttribute("archive", "serify.jar");
		ae.setAttribute("code", "org.simmi.SerifyApplet");
		
		applet.getElement().appendChild( ae );
		
		/*String machinename = getMachineName( ae );
		String 	mcname = null;
		int		nprocs = 0;
		if( machinename != null ) {
			String[] split = machinename.split("\t");
			mcname = split[0];
			nprocs = Integer.parseInt(split[1]);
		}
    	greetingService.getMachineInfo( mcname, nprocs, new AsyncCallback<Machine[]>() {
	    	@Override
			public void onSuccess(Machine[] mcs) {
	    		machineList.clear();
	    		for( Machine m : mcs ) {			    			
	    			machineList.add( m );
	    		}
	    		updateMachineTable();
			}
		
			@Override
			public void onFailure(Throwable caught) {
					
			}
	    });
    	
		/*applet.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resizeApplet( ae, event.getWidth()+"px", event.getHeight()+"px" );
			}
		});*/
		
		HorizontalPanel hp = new HorizontalPanel();
		if( logout != null && logout.length() > 0 ) {
			hp.add( new Anchor( "Sign out", logout ) );
		} else if( login != null && login.length() > 0 ) {
			hp.add( new Anchor( "Sign in with Google", login ) );
		}
		
		vp.add( slp );
		vp.add( hp );
		rp.add( vp );
	}
}
