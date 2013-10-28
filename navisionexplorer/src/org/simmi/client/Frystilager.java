package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

import elemental.client.Browser;

public class Frystilager implements EntryPoint {
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private final String serverUrl = "http://130.208.252.7/cgi-bin/lubbi";
	
	public native boolean ie( String url ) /*-{
		var s = this;
		console.log('buck');
		if (typeof XDomainRequest != "undefined") {
			console.log('succ');
			var xhr = new XDomainRequest();
    		xhr.open('POST', url);
			xhr.onload = function() {
				var responseText = xhr.responseText;
				s.@org.simmi.client.Frystilager::updateTable(Ljava/lang/String;)( responseText );
			};
			xhr.onerror = function() {
				console.log('There was an error!');
			};
			xhr.send();
			
			return true;
		}
		return false;
	}-*/;
	
	public void updateTable( String str ) {
		String[] split = str.split("\n");
		
		//DateFormat df = new SimpleDateFormat( "yyyy-MM-DD hh:mm:ss.z" );
		
		int r = 0;
		for( String spl : split ) {
			String[] subsplit = spl.split("\t");
			if( subsplit.length > 2 ) {
				String ab = subsplit[0].trim();
				String simi = subsplit[1];
				String verkn = subsplit[2];
				String lota = subsplit[3];
				String klefi = subsplit[4];
				String golf = subsplit[5];
				String rekki = subsplit[6];
				String hilla = subsplit[7];
				String dags = subsplit[8];
				String kassaf = subsplit[9];
				String kjot = subsplit[10];
				String fiskur = subsplit[11];
				String annad = subsplit[12];
				
				data.addRow();
				data.setValue(r, 0, ab);
				data.setValue(r, 1, simi);
				data.setValue(r, 2, verkn);
				data.setValue(r, 3, lota);
				data.setValue(r, 4, klefi);
				data.setValue(r, 5, golf);
				
				int rekkn = -1;
				try {
					rekkn = Integer.parseInt(rekki);
				} catch( Exception e ) {
					
				}
				data.setValue(r, 6, rekkn );
				data.setValue(r, 7, hilla);
			
				//DateTimeFormat.getFormat( "yyyy-MM-DD hh:mm:ss.z" ).parse( dags )
				data.setValue(r, 8, dags );
				
				int kassn = -1;
				try {
					kassn = Integer.parseInt(kassaf);
				} catch( Exception e ) {
					
				}
				data.setValue(r, 9, kassn );
				data.setValue(r, 10, kjot);
				data.setValue(r, 11, fiskur);
				data.setValue(r, 12, annad);
				r++;
			}
		}
	}
	
	DataTable data;
	
	@Override
	public void onModuleLoad() {
		final RootPanel rp = RootPanel.get();
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize(w+"px", h+"px");
			}
		});
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		final VerticalPanel	subvp = new VerticalPanel();
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		
		vp.setSize( "100%", "100%" );
		subvp.setSize( "100%", "100%" );
		
		subvp.add( new Label("Frystilager Matís") );
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {		    	  
				data = DataTable.create();
		    	  
				data.addColumn( ColumnType.STRING,"Ábyrgðaraðili" );
				data.addColumn( ColumnType.STRING,"Sími" );
				data.addColumn( ColumnType.STRING,"Verknúmer" );
				data.addColumn( ColumnType.STRING,"Lota" );
				data.addColumn( ColumnType.STRING,"Klefi" );
				data.addColumn( ColumnType.STRING,"Gólf" );
				data.addColumn( ColumnType.NUMBER,"Rekki" );
				data.addColumn( ColumnType.STRING,"Hilla" );
				data.addColumn( ColumnType.STRING,"Dagsetning" );
				data.addColumn( ColumnType.NUMBER,"Kassafjöldi" );
				data.addColumn( ColumnType.STRING,"Kjöt" );
				data.addColumn( ColumnType.STRING,"Fiskur" );
				data.addColumn( ColumnType.STRING,"Annað" );
		    	  
		    	final Options options = Options.create();
	    		options.setWidth("1024px");
		    	options.setHeight("600px");
		    	//options.setAllowHtml( true );
		    	  
		    	DataView view = DataView.create( data );
		    	  
		    	final Table table = new Table( view, options );
		    	table.setSize("1024px", "600px");
		    	Style tstyle = table.getElement().getStyle();
		    	tstyle.setMargin(0.0, Unit.PX);
		    	tstyle.setBorderWidth(0.0, Unit.PX);
		    	tstyle.setPadding(0.0, Unit.PX);
		    	  
		    	subvp.add( table );
		    	  
		    	if( !ie( serverUrl ) ) {
			    	RequestBuilder rb = new RequestBuilder( RequestBuilder.POST, serverUrl );
			  		try {
			  			String sql = "select f.[Ábyrgðaraðili], f.[Sími], f.[Verknúmer], f.[Lota], f.[Klefi], f.[Gólf], f.[Rekki], f.[Hilla], f.[Dagsetning], f.[Kassafjöldi], f.[Kjöt], f.[Fiskur], f.[Annað] from [vdb].[dbo].[Frystilager] f";
			  			rb.sendRequest(sql, new RequestCallback() {
			  				@Override
			  				public void onResponseReceived(Request request, Response response) {
			  					updateTable( response.getText() );
			  					
			  					DataView view = DataView.create( data );
								table.draw( view, options );
			  				}
			  				
			  				@Override
			  				public void onError(Request request, Throwable exception) {
			  					Browser.getWindow().getConsole().log( exception.getMessage() );
			  				}
			  			});
			  		} catch (RequestException e) {
			  			e.printStackTrace();
			  		}
		    	}
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		
		vp.add( subvp );
		rp.add( vp );
	}
}
