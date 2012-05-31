package org.simmi.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
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
	
	//private final String server = "http://www.matis.is/taxonomy/";
	//private final String server = "http://130.208.252.34/";
	
	public void recursiveNames( TreeItem item, StringBuilder sb ) {
		if( item.getChildCount() == 0 ) {
			String[] sp = item.getText().split(" ");
			if( sb.length() == 0 ) sb.append( sp[0] );
			else sb.append( ","+sp[0] );
		} else {
			for( int i = 0; i < item.getChildCount(); i++ ) {
				TreeItem ti = item.getChild(i);
				recursiveNames( ti, sb );
			}
		}
	}
	
	public void runSubStuff( RequestBuilder rb, final TreeItem rootitem ) throws RequestException {
		rb.sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String resp = response.getText();
				stuff( resp, rootitem );
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				
			}
		});
	}
	
	public void runStuff( String server, Tree tree ) {
		final TreeItem	eyjosilva = tree.addItem( "eyjosilva" );
		final TreeItem	eyjoroot = tree.addItem( "eyjo" );
		final TreeItem	newroot6 = tree.addItem( "newroot6" );
		final TreeItem	rootitem1 = tree.addItem( "root1" );
		final TreeItem	rootitem2 = tree.addItem( "root2" );
		final TreeItem	rootitem3 = tree.addItem( "root3" );
		final TreeItem	rootitem4 = tree.addItem( "root4" );
		final TreeItem	rootitem5 = tree.addItem( "root5" );
		final TreeItem	rootitem6 = tree.addItem( "root6" );
		final TreeItem	rootitem7 = tree.addItem( "root7" );
		final TreeItem	rootitem8 = tree.addItem( "root8" );
		final TreeItem	rootitem9 = tree.addItem( "root9" );
		final TreeItem	rootitem10 = tree.addItem( "root10" );
		final TreeItem	rootitem11 = tree.addItem( "root11" );
		final TreeItem	rootitem12 = tree.addItem( "root12" );
		final TreeItem	rootitem13 = tree.addItem( "root13" );
		final TreeItem	rootitem14 = tree.addItem( "root14" );
		final TreeItem	rootitem15 = tree.addItem( "root15" );
		final TreeItem	rootitem16 = tree.addItem( "root16" );
		final TreeItem	arciformis = tree.addItem( "arciformis" );
		final TreeItem	kawarayensis = tree.addItem( "kawarayensis" );
		
		/*RequestBuilder rb3 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/3v1.txt" );
		RequestBuilder rb4 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/4v1.txt" );
		RequestBuilder rb5 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/5v4.txt" );
		RequestBuilder rb6 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/6v1.txt" );
		RequestBuilder rb13 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/13v1.txt" );
		RequestBuilder rb14 = new RequestBuilder( RequestBuilder.GET, "http://"+server+"/14v1.txt" );
		try {
			runSubStuff(rb3, rootitem3);
			runSubStuff(rb4, rootitem4);
			runSubStuff(rb5, rootitem5);
			runSubStuff(rb6, rootitem6);
			runSubStuff(rb13, rootitem13);
			runSubStuff(rb14, rootitem14);
		} catch (RequestException e) {
			e.printStackTrace();
		}*/
	}
	
	public void runSpec( TreeItem rootitem, String serverurl ) {
		try {
			RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, serverurl );
			runSubStuff(rb, rootitem);
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final String server = "130.208.252.7";//Location.getHost();
		
		RootPanel		rp = RootPanel.get();
		Style rootstyle = rp.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		final Tree		tree = new Tree();
		tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selectedtree = event.getSelectedItem();
				
				String nodename = selectedtree.getText();
				if( ( nodename.contains("eyjo") || nodename.contains("root") || nodename.equals("arciformis") || nodename.equals("kawarayensis") ) && selectedtree.getChildCount() == 0 ) {
					if( nodename.equals("eyjosilva") ) runSpec( selectedtree, "http://"+server+"/mysilva1.txt" );
					else if( nodename.equals("eyjo") ) runSpec( selectedtree, "http://"+server+"/my1.txt" );
					else if( nodename.equals("newroot6") ) runSpec( selectedtree, "http://"+server+"/6v2.txt" );
					else if( nodename.equals("root1") ) runSpec( selectedtree, "http://"+server+"/1v1.txt" );
					else if( nodename.equals("root2") ) runSpec( selectedtree, "http://"+server+"/2v1.txt" );
					else if( nodename.equals("root3") ) runSpec( selectedtree, "http://"+server+"/3v1.txt" );
					else if( nodename.equals("root4") ) runSpec( selectedtree, "http://"+server+"/4v1.txt" );
					else if( nodename.equals("root5") ) runSpec( selectedtree, "http://"+server+"/5v1.txt" );
					else if( nodename.equals("root6") ) runSpec( selectedtree, "http://"+server+"/6v1.txt" );
					else if( nodename.equals("root7") ) runSpec( selectedtree, "http://"+server+"/7v1.txt" );
					else if( nodename.equals("root8") ) runSpec( selectedtree, "http://"+server+"/8v1.txt" );
					else if( nodename.equals("root9") ) runSpec( selectedtree, "http://"+server+"/9v1.txt" );
					else if( nodename.equals("root10") ) runSpec( selectedtree, "http://"+server+"/10v1.txt" );
					else if( nodename.equals("root11") ) runSpec( selectedtree, "http://"+server+"/11v1.txt" );
					else if( nodename.equals("root12") ) runSpec( selectedtree, "http://"+server+"/12v1.txt" );
					else if( nodename.equals("root13") ) runSpec( selectedtree, "http://"+server+"/13v1.txt" );
					else if( nodename.equals("root14") ) runSpec( selectedtree, "http://"+server+"/14v1.txt" );
					else if( nodename.equals("root15") ) runSpec( selectedtree, "http://"+server+"/15v1.txt" );
					else if( nodename.equals("root16") ) runSpec( selectedtree, "http://"+server+"/16v1.txt" );
					else if( nodename.equals("arciformis") ) runSpec( selectedtree, "http://"+server+"/arciformis_v1.txt" );
					else if( nodename.equals("kawarayensis") ) runSpec( selectedtree, "http://"+server+"/kawarayensis_v1.txt" );
				} else {
					StringBuilder sb = new StringBuilder();
					//sb.append( selectedtree.getText() );
					recursiveNames( selectedtree, sb );
					
					String searchnum = "";
					TreeItem parent = selectedtree.getParentItem();
					while( parent != null ) {
						selectedtree = parent;
						parent = selectedtree.getParentItem();
					}
					String rootname = selectedtree.getText();
					String[] rsplit = rootname.split(" ");
					String rootnodename = rsplit[0];
					if( rootnodename.contains("root") ) {
						try {
							searchnum = rootnodename.substring(4);
						} catch( Exception e ) {
							
						}
					} else {
						searchnum = rootnodename;
					}
					
					String qstr =  sb.toString();					
					RequestBuilder rb = new RequestBuilder( RequestBuilder.POST, "http://"+server+"/cgi-bin/getseq.cgi" );
					try {
						rb.sendRequest( searchnum+"_"+qstr, new RequestCallback() {
							@Override
							public void onResponseReceived(Request request, Response response) {
								String result = response.getText();
								
								DialogBox db = new DialogBox();
								Caption cap = db.getCaption();
								db.setAutoHideEnabled( true );
								cap.setText("Fasta");
								TextArea ta = new TextArea();
								ta.setSize("512px", "384px");
								db.add( ta );
								
								ta.setText( result );
								
								db.center();
							}

							@Override
							public void onError(Request request, Throwable exception) {
								console( "erm error" );
							}
						});
					} catch (RequestException e) {
						e.printStackTrace();
					}
					
					/*runSpec( selectedtree, "http://"+server+"/12v1.txt" );
					greetingService.greetServer( qstr, searchnum, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							DialogBox db = new DialogBox();
							//db.setSize("400px", "300px");
							Caption cap = db.getCaption();
							db.setAutoHideEnabled( true );
							cap.setText("Fasta");
							TextArea ta = new TextArea();
							ta.setSize("512px", "384px");
							db.add( ta );
							
							ta.setText( result );
							
							db.center();
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});*/
				}
			}
		});
		//tree.setSize("100%", "100%");
		
		//tree.getElement().getStyle().setBorderColor("#aa4444");
		//tree.getElement().getStyle().setBorderWidth( 2.0, Unit.PX );
		
		greetingService.getRemoteAddress( new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				//if( result != null && result.contains("130.208.252.") ) 
				runStuff( server, tree );
			}
		});
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
				
				//stuff( str, rootitem5 );
			}
		});
		
		rp.add( focus );
		focus.setFocus( true );
	}
	
	public void stuff( String str, TreeItem rootitem ) {
		String[] split = str.split("\n");
		TreeItem	current = rootitem;
		boolean first = true;
		for( String s : split ) {
			boolean gogg = s.startsWith(">");
			boolean svig = s.startsWith("(");
			boolean nohi = s.startsWith("*");
			if( s.length() > 0 && !svig && !gogg && !nohi && !first ) {
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
				current = current.addItem( s );
			} else if( svig && current != rootitem ) {
				String[] spl = s.substring(1,s.length()-1).split(",");
				//console("lenni " + spl.length);
				for( String splstr : spl ) {
					current.addItem( splstr );
				}
			} else if( nohi ) {
				current = current.addItem("*** No hits ***");
			} else {
				current = rootitem;
			}
			first = false;
		}
		
		recursiveCount( rootitem );
	}
	
	public int recursiveCount( TreeItem item ) {
		int total = 0;
		
		if( item.getText().startsWith(">") ) {
			String[] ss = item.getText().split("[\t ]+");
			total = Integer.parseInt( ss[ss.length-1] );
		} else if( item.getText().startsWith("*") ) {
			total = item.getChildCount();
			item.setText( item.getText()+" ("+total+")" );
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
