package org.simmi.client;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nytthvaderimatnum implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public class Food {
		public Food( String name, String group ) {
			this.name = name;
			this.type = group;
		}
		
		String	name;
		String	type;
	};
	
	public void requestFood( final DataGrid<Food> foodtable, final Map<String,String> groupmap, final TextColumn<Food>	namecol, final TextColumn<Food>	typecol ) {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "Food.txt" );
		try {
			rb.sendRequest("", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resptext = response.getText();
					int bit = 0;
					
					ListDataProvider<Food> dataProvider = new ListDataProvider<Food>();
					dataProvider.addDataDisplay(foodtable);
				    List<Food> flist = dataProvider.getList();
					while( flist.size() == 0 || bit != 0 ) {
						int it = resptext.indexOf('\t', bit+7);
						if( it > bit+7 ) {
							int nit = resptext.indexOf( '\t', it+1 );
							int net = resptext.indexOf( '\t', nit+1 );
							int net1 = resptext.indexOf( '\t', net+1 );
							int net2 = resptext.indexOf( '\t', net1+1 );
							String group = resptext.substring(net1+1, net2);
							if( groupmap.containsKey( group ) ) group = groupmap.get(group);	
							
							String sub = resptext.substring(bit+7, it);
							flist.add( new Food( sub, group ) );
						}
						bit = resptext.indexOf('\n',it)+1;
					}
					foodtable.setPageSize( flist.size() );
					//foodtable.setRowCount( flist.size(), true );
					//foodtable.setRowData( flist );
					
					ListHandler<Food> columnSortHandler = new ListHandler<Food>( flist );
					columnSortHandler.setComparator(namecol, new Comparator<Food>() {
						public int compare(Food o1, Food o2) {
							if (o1 == o2) {
							  return 0;
							}
							
							if (o1 != null) {
							  return (o2 != null) ? o1.name.compareTo(o2.name) : 1;
							}
							return -1;
						}
				    });
					columnSortHandler.setComparator(typecol, new Comparator<Food>() {
						public int compare(Food o1, Food o2) {
							if (o1 == o2) {
							  return 0;
							}
							
							if (o1 != null) {
							  return (o2 != null) ? o1.type.compareTo(o2.type) : 1;
							}
							return -1;
						}
				    });
					foodtable.addColumnSortHandler(columnSortHandler);
					//foodtable.getColumnSortList().push( namecol );
					//foodtable.getColumnSortList().push( typecol );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		/*Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize(w+"px", h+"px");
			}
		});
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");*/
		
		final DataGrid<Food>	foodtable = new DataGrid<Food>();
		//foodtable.sets
		//foodtable.setSize("100%", "100%");
		final TextColumn<Food>	namecol = new TextColumn<Food>() {
			@Override
			public String getValue(Food object) {
				return object.name;
			}
		};
		namecol.setSortable( true );
		final TextColumn<Food>	typecol = new TextColumn<Food>() {
			@Override
			public String getValue(Food object) {
				return object.type;
			}
		};
		typecol.setSortable( true );
		
		foodtable.addColumn( namecol, "Fæða" );
		foodtable.addColumn( typecol, "Fæðutegund" );
		
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "thsGroups.txt" );
		try {
			rb.sendRequest("", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resptext = response.getText();
					int bit = 0;
					Map<String,String>	groupmap = new HashMap<String,String>();
					while( groupmap.size() == 0 || bit != 0 ) {
						int it = resptext.indexOf('\t', bit);
						if( it > bit ) {
							String id = resptext.substring(bit, it).trim();
							bit = resptext.indexOf('\n',it);
							String name = resptext.substring(it, bit-1).trim();
							bit++;
							groupmap.put( id, name );
						} else bit = it+1;
					}
					
					requestFood( foodtable, groupmap, namecol, typecol );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		foodtable.setSize("800px","800px");
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize(w+"px", h+"px");
				foodtable.setSize("800px","600px");
			}
		});
		
		rp.add( foodtable );
	}
}
