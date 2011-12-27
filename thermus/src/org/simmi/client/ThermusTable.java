package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class ThermusTable implements EntryPoint {

	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	
	int w;
	int h;
	
	@Override
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		Window.enableScrolling( false );
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		
		/*final RootPanel	rp = RootPanel.get();
		Window.enableScrolling( false );
		w = Window.getClientWidth();
		h = Window.getClientHeight();
		rp.setSize(w+"", h+"");
		
		Style s = rp.getElement().getStyle();
		s.setBorderWidth(0.0, Unit.PX);
		s.setMargin(0.0, Unit.PX);
		s.setPadding(0.0, Unit.PX);
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				w = event.getWidth();
				h = event.getHeight();
				rp.setSize(w+"", h+"");
				if( table != null ) table.setSize(w+"", h+"");
			}
		});
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "acc");
		    	data.addColumn( ColumnType.STRING, "species");
		    	data.addColumn( ColumnType.STRING, "doi");
		    	data.addColumn( ColumnType.STRING, "pubmed");
		    	data.addColumn( ColumnType.STRING, "journal");
		    	data.addColumn( ColumnType.STRING, "auth");
		    	data.addColumn( ColumnType.STRING, "sub_auth");
		    	data.addColumn( ColumnType.STRING, "sub_date");
		    	data.addColumn( ColumnType.STRING, "source");
		    	
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
				
		    	RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "http://130.208.252.7/therm.txt" );
		    	try {
					rb.sendRequest("", new RequestCallback() {
						@Override
						public void onResponseReceived(Request request, Response response) {
							String text = response.getText();
							String[] nsplit = text.split("\n");
							for( String n : nsplit ) {
								int r = data.getNumberOfRows();
								
								String[] tsplit = n.split("\t");
								data.addRow();
								data.setValue( r, 0, tsplit[0] );
								data.setValue( r, 1, tsplit[1] );
								data.setValue( r, 2, tsplit[2] );
								data.setValue( r, 3, tsplit[3] );
								data.setValue( r, 4, tsplit[4] );
								data.setValue( r, 5, tsplit[5] );
								data.setValue( r, 6, tsplit[6] );
								data.setValue( r, 7, tsplit[7] );
								data.setValue( r, 8, tsplit[8] );
							}
							
						    view = DataView.create( data );
						    table = new Table( view, options );
						    
						    table.setSize(w+"", h+"");
						    	
						    rp.add( table );
						}
						
						@Override
						public void onError(Request request, Throwable exception) {}
					});
				} catch (RequestException e) {
					e.printStackTrace();
				}
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);*/
	}

}
