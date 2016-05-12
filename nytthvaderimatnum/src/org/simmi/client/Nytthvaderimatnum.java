package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
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
	
	public void requestFood( final DataGrid<Food> foodtable, final Map<String,String> groupmap, final TextColumn<Food>	namecol, final TextColumn<Food>	typecol ) throws RequestException {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "Food.txt" );
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
			public void onError(Request request, Throwable exception) {}
		});
	}
	
	public void requestComponents( final DataGrid<Food> foodtable, final Map<String,String> groupmap, final TextColumn<Food>	namecol, final TextColumn<Food>	typecol ) throws RequestException {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "Components.txt" );
		rb.sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				Integer[] ii = { 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20, 21, 23, 24, 28, 29, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 44, 137, 138 };
				final Set<Integer> is = new HashSet<Integer>(Arrays.asList(ii));
		
				List<String[]> idList = new ArrayList<String[]>();
				String text = response.getText();
				
				int i = 0;
				int k = text.indexOf('\n', i);
				
				while ( k != -1 ) {
					String line = text.substring(i, k);
					i = k+1;
					
					String[] split = line.split("[\t]");
					if (split.length > 3 && is.contains(Integer.parseInt(split[2]))) {
						String sName = null;
						if (split[4] != null && split[4].length() > 0) {
							sName = split[4];
						}
						String nName = split[3];
		
						String[] strs = new String[] { split[2], nName, split[8], sName, split[6] };
						idList.add(strs);
						
						final TextColumn<Food>	compcol = new TextColumn<Food>() {
							@Override
							public String getValue(Food object) {
								return "0";
							}
						};
						compcol.setSortable( true );
						foodtable.addColumn( compcol, nName );
						
						// ngroupMap.put( split[2], i++ );
		
						/*
						 * ngroupList.add( nName ); // + " ("+split[1].substring(1,
						 * split[1].length()-1)+")" ); ngroupGroups.add( split[8] );
						 * //List<Object> lobj = nutList.get(i).get(i)
						 * nutList[0].add( sName ); String mName = split[6];
						 * nutList[1].add( mName );
						 */
					}
					k = text.indexOf('\n', i);
				}
		
				Collections.sort(idList, new Comparator<String[]>() {
					public int compare(String[] s1, String[] s2) {
						return s1[0].compareTo(s2[0]);
					}
				});
				
				try {
					requestFood( foodtable, groupmap, namecol, typecol );
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {}
		});
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
					
					try {
						requestComponents( foodtable, groupmap, namecol, typecol );
					} catch (RequestException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		foodtable.setSize(w+"px", h+"px");
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				foodtable.setSize(w+"px", h+"px");
			}
		});
		
		rp.add( foodtable );
	}
}
