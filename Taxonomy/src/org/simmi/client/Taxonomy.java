package org.simmi.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Taxonomy implements EntryPoint {
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

	Map<String,TreeItem>	treemap = new HashMap<String,TreeItem>();
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel		rp = RootPanel.get();
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		final Tree		tree = new Tree();
		//tree.setSize("100%", "100%");
		
		//tree.getElement().getStyle().setBorderColor("#aa4444");
		//tree.getElement().getStyle().setBorderWidth( 2.0, Unit.PX );
		
		final TreeItem	rootitem3 = tree.addItem( "root3" );
		final TreeItem	rootitem4 = tree.addItem( "root4" );
		final TreeItem	rootitem5 = tree.addItem( "root5" );
		final TreeItem	rootitem6 = tree.addItem( "root6" );
		final TreeItem	rootitem13 = tree.addItem( "root13" );
		final TreeItem	rootitem14 = tree.addItem( "root14" );
		
		RequestBuilder rb3 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/3v1.txt" );
		RequestBuilder rb4 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/4v1.txt" );
		RequestBuilder rb5 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/5v4.txt" );
		RequestBuilder rb6 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/6v1.txt" );
		RequestBuilder rb13 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/13v1.txt" );
		RequestBuilder rb14 = new RequestBuilder( RequestBuilder.GET, "http://www.matis.is/taxonomy/14v1.txt" );
		try {
			rb3.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem3 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
			rb4.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem4 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
			rb5.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem5 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
			rb6.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem6 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
			rb13.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem13 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
			rb14.sendRequest("", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					String resp = response.getText();
					stuff( resp, rootitem14 );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		FocusPanel	focus = new FocusPanel( tree );
		//focus.setSize("100%", "100%");
		
		//focus.getElement().getStyle().setBorderColor( "#444444" );
		//focus.getElement().getStyle().setBorderWidth(2.0, Unit.PX);
		focus.getElement().getStyle().setBackgroundColor("#ddddff");
		
		focus.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {
				
			}
		});
		focus.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				
			}
		});
		focus.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				
			}
		});
		focus.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				
			}
		});
		focus.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {
				
			}
		});
		focus.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				
			}
		});
		focus.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				String str = event.getData("text/plain");
				
				stuff( str, rootitem5 );
			}
		});
		
		rp.add( focus );
		focus.setFocus( true );
	}
	
	public void stuff( String str, TreeItem rootitem ) {
		String[] split = str.split("\n");
		TreeItem	current = rootitem;
		for( String s : split ) {
			boolean gogg = s.startsWith(">");
			if( !gogg && !s.contains("subtot") ) {
				String[] subs = s.split("\\:");
				if( subs.length > 1 ) {
					current = rootitem;
					for( int i = subs.length-2; i >= 0; i-- ) {
						String name = subs[i];
						
						int k;
						for( k = 0; k < current.getChildCount(); k++ ) {
							TreeItem ti = current.getChild(k);
							if( ti.getText().equals( name ) ) {
								current = ti;
								break;
							}
						}
						if( k == current.getChildCount() ) {
							current = current.addItem( name );
							treemap.put( name, current );
						}
						
						/*if( treemap.containsKey( name ) ) {
							current = treemap.get( name );
						} else {
							//if( name.contains(">") ) console( s );
							current = current.addItem( name );
							treemap.put( name, current );
						}*/
					}
				}
			} else if( gogg && current != rootitem ) {
				current.addItem( s );
				current = rootitem;
			}
		}
		
		recursiveCount( rootitem );
	}
	
	public int recursiveCount( TreeItem item ) {
		int total = 0;
		
		if( item.getChildCount() == 0 ) {
			String[] ss = item.getText().split("[\t ]+");
			total = Integer.parseInt( ss[ss.length-1] );
		} else {
			for( int i = 0; i < item.getChildCount(); i++ ) {
				TreeItem ti = item.getChild(i);
				total += recursiveCount( ti );
			}
			item.setText( item.getText()+" ("+total+")" );
		}
		
		return total;
	}
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
}