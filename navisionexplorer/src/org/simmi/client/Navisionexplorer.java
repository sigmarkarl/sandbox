package org.simmi.client;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
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
public class Navisionexplorer implements EntryPoint {
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

	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		
		final DockLayoutPanel	dlp = new DockLayoutPanel( Unit.PX );
		final SplitLayoutPanel 	slp = new SplitLayoutPanel();
		
		HorizontalPanel	toolbar = new HorizontalPanel();
		
		ScriptElement	se = Document.get().createScriptElement();
		
		dlp.addNorth( toolbar, 25 );
		dlp.add( slp );
		
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "http://130.208.252.31/cbi-bin/lubbi" );
		try {
			rb.sendRequest("", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					console( "succ" );
					console( response.getText() );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					console( exception.getMessage() );
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		final Canvas canvas = Canvas.createIfSupported();
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "Starfsmaður");
		    	data.addColumn( ColumnType.STRING, "Kennitala");
		    	  
		    	options = Options.create();
		    	//options.setWidth("100px");
		    	//options.setHeight("480px");
		    	options.setAllowHtml( true );
		    	  
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	  
		    	greetingService.getAllUsers( new AsyncCallback<List<Person>>() {
			    	@Override
					public void onSuccess(List<Person> result) {
						if( result != null ) {
				    		for( Person person : result ) {
								int r = data.getNumberOfRows();
								data.addRow();
								
								data.setValue( r, 0, person.getName() );
								data.setValue( r, 1, person.getKt() );
								view = DataView.create( data );
								table.draw( view, options );
							}
						}
					}
				
					@Override
					public void onFailure(Throwable caught) {
							
					}
			    });
		    //dropHandler( table.getElement() );
		    	  
		    /*vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		    vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );    	  
		    	  
		    VerticalPanel	subvp = new VerticalPanel();
		    subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		    subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		    	  
		    final HTML html = new HTML();
		    //html.setText( "Dragðu skrána með smásögunni þinni í töfluna. <br>Ef þú ert logguð/loggaður inná facebook er réttur höfundur skráður. <br>Þú getur valið höfundarnafn, nafnið á raunverulegum höfundi þarf ekki að vera valið" );
		    html.setHTML( "Drag-drop the file containing you short story into the table. <br>" +
		    "If you are logged into facebook, you are registered as the author. <br>" +
		    "You can choose you own authorname, it doesn't have to be your real name" );
		    html.setWidth("100%");
		    html.getElement().getStyle().setMargin(20.0, Unit.PX);
		    	  
		    HTML title = new HTML("<h2>Shortstories<h2/>");
		    subvp.add( title );
		    HTML subtitle = new HTML("<h4>Brought to you by The Basement At 5 o'Clock reading club<h4/>");
		    subvp.add( subtitle );
		    	  
		    SimplePanel log = new SimplePanel();
		    SimplePanel gug = new SimplePanel();
		    com.google.gwt.dom.client.Element plus = Document.get().createElement("g:plusone");
		  	plus.setAttribute("size", "small");
		    gug.getElement().appendChild( plus );
		  		  
		    HorizontalPanel	sharehp = new HorizontalPanel();
		    sharehp.add( log );
		    sharehp.add( gug );
		    subvp.add( sharehp );
		    	  
		    subvp.add( html );
		    subvp.add( focuspanel );
		    	  
		    Anchor	a = new Anchor( "huldaeggerts@gmail.com" );
		    	  a.setHref("mailto:huldaeggers@gmail.com");
		    	  Anchor	fast = new Anchor( "http://fasteignaverd.appspot.com" );
		    	  fast.setHref("http://fasteignaverd.appspot.com");
		    	  Anchor	conn = new Anchor( "http://webconnectron.appspot.com" );
		    	  conn.setHref("http://webconnectron.appspot.com");
		    	  
		    	  HorizontalPanel	hp = new HorizontalPanel();
		    	  hp.setSpacing(10);
		    	  hp.add( a );
		    	  hp.add( fast );
		    	  hp.add( conn );
		    	  subvp.add( hp );
		    	  
		    	  status = new Label();
		    	  subvp.add( status );
		    	  
		    	  vp.add( subvp );		    	  
		    	  module.add( vp );*/
		    	  
		    	  slp.addWest( table, 200.0 );
		    	  slp.add( canvas );
		      }
		    };
		    VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		//slp.add( erm );
		
		rp.add( slp );
	}
}
