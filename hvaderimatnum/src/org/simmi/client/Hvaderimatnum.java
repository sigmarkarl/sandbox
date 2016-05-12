package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Hvaderimatnum implements EntryPoint {
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

	public native int initApplet( final String server ) /*-{
		$wnd.runApplet = function() {
			var attributes = { codebase:'http://'+server+'/', archive:'pontun.jar', code:'org.simmi.Order', width:'100%', height:'100%', id:'order', name:'order' };
	    	var parameters = { jnlp_href:'order.jnlp' };
	    	$wnd.deployJava.runApplet(attributes, parameters, '1.6');
		}
	}-*/;
	
	public native int initFunctions() /*-{
		var s = this;
		
		$wnd.showTable = function( str ) {
			s.@org.simmi.client.Hvaderimatnum::showTable(Ljava/lang/String;)( str );
		};
		
		$wnd.loadData = function( str ) {
			s.@org.simmi.client.Hvaderimatnum::loadData(Ljava/lang/String;)( str );
		};
	}-*/;
	
	public void loadData( String query ) {
		console("about" + query);
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, query );
		try {
			rb.sendRequest( "", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String jsonString = response.getText();
					console("sko "+jsonString);
					setAppletInputStream( appletelement, jsonString );
					
					/*JSONValue jsval = JSONParser.parseLenient( jsonString );
					JSONObject jsobj = jsval.isObject();
					if( jsobj != null ) {
						JSONArray rows = (JSONArray)jsobj.get("rows");
						for( int i = 0; i < rows.size(); i++ ) {
							JSONArray row = (JSONArray)rows.get(i);
							JSONString id = (JSONString)row.get(0);
							JSONString groupid = (JSONString)row.get(1);
							JSONString name = (JSONString)row.get(2);
							String idstr = id.stringValue();
							String groupidstr = groupid.stringValue();
							String namestr = name.stringValue();
							lfoodinfo.add( new FoodInfo( namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ), 0.0 ) );
							//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
						}
						draw( canvas.getContext2d(), xstart, ystart );
					}
					//console( response.getText() );*/
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					console( "error " + exception.getMessage() );
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		/*greetingService.greetServer( query, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				setAppletInputStream( appletelement, result );
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});*/
	}
	
	public native void setAppletInputStream( Element ae, String result ) /*-{
		ae.setInput( result );
	}-*/;
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	public void showTable( String str ) {
		console("erm");
		
		DialogBox	db = new DialogBox();
		
		db.setAutoHideEnabled( true );
		db.getCaption().setText("Copy data");
		db.setSize(800+"px", 600+"px");
		
		/*data = DataTable.create();
		String[] split = str.split("\n");
		String[] subs = split[0].split("\t");
		
		for( String col : subs ) {
			data.addColumn(ColumnType.STRING, col);
		}
		
		for( int i = 1; i < subs.length; i++ ) {
			subs = split[i].split("\t");
			
			int r = data.getNumberOfRows();
			data.addRow();
			
			int k = 0;
			for( String s : subs ) {
				data.setValue( r, k++, s );
			}
		}
		view = DataView.create( data );
		table.draw( view, options );
		
		db.add( table );*/
		
		console("muu");
		db.center();
		console("done");
	}
	
	//DataTable	data;
	//DataView	view;
	//Table		table;
	//Options		options;
	Element		appletelement;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		String server = Location.getHost();
		//initApplet( server );
		
		Window.enableScrolling( false );
		Window.setMargin("0px");
		
		initFunctions();
		
		final RootPanel	rp = RootPanel.get();
		Style rootstyle = rp.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		rp.setWidth( Window.getClientWidth()+"px" );
		rp.setHeight( Window.getClientHeight()+"px" );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				rp.setWidth( event.getWidth()+"px" );
				rp.setHeight( event.getHeight()+"px" );
			}
		});
		
		/*Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	//data.addColumn( ColumnType.STRING, "Starfsmaður");
		    	//data.addColumn( ColumnType.STRING, "Kennitala");
		    	  
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
		    	  
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	table.addSelectHandler( new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						//selectEvent();
					}
		    	});
			}	
	    };
	    VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);*/
		
		//ScriptElement se = Document.get().createScriptElement();
		/*Style scriptstyle = se.getStyle();
		scriptstyle.setMargin(0.0, Unit.PX);
		scriptstyle.setPadding(0.0, Unit.PX);*/
		
		/*se.setText( //"alert('hoho');\n" +
			"var attributes = { codebase:'http://"+server+"/', archive:'pontun.jar', code:'org.simmi.Order', width:'100%', height:'100%', hspace:'0', vspace:'0', id:'order', name:'order' };\n"+
	    	"var parameters = { jnlp_href:'order.jnlp' };\n"+
	    	"deployJava.runApplet(attributes, parameters, '1.6');" );*/
		
		Element ae = Document.get().createElement("object");
		appletelement = ae;
		ae.setAttribute("width", "100%");
		ae.setAttribute("height", "100%");
		ae.setAttribute("type", "application/x-java-applet");
		/*ae.setAttribute("codebase", "http://"+server+"/");
		ae.setAttribute("archive", "food.jar,swingx-all-1.6.3.jar");
		ae.setAttribute("code", "org.simmi.SortTable");*/
		ae.setAttribute("id", "isgem");
		ae.setAttribute("name", "isgem");
		
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "isgem.jnlp");
		
		Element pe1 = Document.get().createElement("param");
		pe1.setAttribute("name", "image");
		pe1.setAttribute("value", "matis2.gif");
		
		Element pe2 = Document.get().createElement("param");
		pe2.setAttribute("name", "centerimage");
		pe2.setAttribute("value", "true");
		
		Element pe3 = Document.get().createElement("param");
		pe3.setAttribute("name", "boxborder");
		pe3.setAttribute("value", "false");
		
		Element pe4 = Document.get().createElement("param");
		pe4.setAttribute("name", "draggable");
		pe4.setAttribute("value", "true");
		
		/*<param name="image" value="matis2.gif"/> 
		<param name="centerimage" value="true"/> 
		<param name="boxborder" value="false"/>
		<param name="draggable" value="true"/>*/
		
		ae.appendChild( pe );
		ae.appendChild( pe1 );
		ae.appendChild( pe2 );
		ae.appendChild( pe3 );
		ae.appendChild( pe4 );
		//HTML applet = new HTML( "<applet width='100%' height='100%' codebase='http://"+server+"/' archive='pontun.jar' code='org.simmi.Order'><param name='jnlp_href' value='order.jnlp' /></applet>" );
		rp.getElement().appendChild( ae );
	}
}
