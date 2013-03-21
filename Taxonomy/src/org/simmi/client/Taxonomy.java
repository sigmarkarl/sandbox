package org.simmi.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	
	Map<String,String> rsnaedis1map;
	Map<String,String> rsnaedis2map;
	Map<String,String> rsnaedis3map;
	Map<String,String> rsnaedis4map;
	Map<String,String> rsnaedis5map;
	Map<String,String> rsnaedis6map;
	Map<String,String> rsnaedis7map;
	Map<String,String> rsnaedis8map;
	public void runStuff( String server, Tree tree, Map<String,String> rmapstr ) {
		final TreeItem	eyjosilva1 = tree.addItem( "eyjoACGACTACAG" );
		final TreeItem	eyjosilva2 = tree.addItem( "eyjoACGCTCGACA" );
		final TreeItem	eyjosilva3 = tree.addItem( "eyjoAGACGCACTC" );
		final TreeItem	eyjosilva4 = tree.addItem( "eyjoATACGACGTA" );
		final TreeItem	eyjosilva5 = tree.addItem( "eyjoATATCGCGAG" );
		final TreeItem	eyjosilva6 = tree.addItem( "eyjoATCAGACACG" );
		final TreeItem	eyjosilva7 = tree.addItem( "eyjoATCAGACACT" );
		final TreeItem	eyjosilva8 = tree.addItem( "eyjoCATAGTAGTG" );
		final TreeItem	eyjosilva9 = tree.addItem( "eyjoCGAGAGATAC" );
		final TreeItem	eyjosilva10 = tree.addItem( "eyjoCGGAGAGATA" );
		final TreeItem	eyjosilva11 = tree.addItem( "eyjoCGTAGACTAG" );
		final TreeItem	eyjosilva12 = tree.addItem( "eyjoCGTCTAGTAC" );
		final TreeItem	eyjosilva13 = tree.addItem( "eyjoCGTGTCTCTA" );
		final TreeItem	eyjosilva14 = tree.addItem( "eyjoCTCGCGTGTC" );
		final TreeItem	eyjosilva15 = tree.addItem( "eyjoGAGACGCACT" );
		final TreeItem	eyjosilva16 = tree.addItem( "eyjoGTCTACGTAG" );
		final TreeItem	eyjosilva17 = tree.addItem( "eyjoTACGAGTATG" );
		final TreeItem	eyjosilva18 = tree.addItem( "eyjoTACTCTCGTG" );
		final TreeItem	eyjosilva19 = tree.addItem( "eyjoTAGAGACGAG" );
		final TreeItem	eyjosilva20 = tree.addItem( "eyjoTCACGTACTA" );
		final TreeItem	eyjosilva21 = tree.addItem( "eyjoTCTACGTAGC" );
		final TreeItem	eyjosilva22 = tree.addItem( "eyjoTCTCTATGCG" );
		final TreeItem	eyjosilva23 = tree.addItem( "eyjoTGATACGTCT" );
		final TreeItem	eyjosilva24 = tree.addItem( "eyjoTGTACTACTC" );
		
		int i = 0;
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rmapstr.keySet() ) {
				if( text.contains(key) ) {
					String val = rmapstr.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedissilva1 = tree.addItem( "snaedis1ACGACGTCGA" );
		final TreeItem	snaedissilva2 = tree.addItem( "snaedis1ACGAGTGCGT" );
		final TreeItem	snaedissilva3 = tree.addItem( "snaedis1ACGCTCGACA" );
		final TreeItem	snaedissilva4 = tree.addItem( "snaedis1ACGNGTCGCT" );
		final TreeItem	snaedissilva5 = tree.addItem( "snaedis1AGACGCACTC" );
		final TreeItem	snaedissilva6 = tree.addItem( "snaedis1AGCACTGTAG" );
		final TreeItem	snaedissilva7 = tree.addItem( "snaedis1AGGACGCACT" );
		final TreeItem	snaedissilva8 = tree.addItem( "snaedis1AGGCACTGTA" );
		final TreeItem	snaedissilva9 = tree.addItem( "snaedis1ATATCGCGAG" );
		final TreeItem	snaedissilva10 = tree.addItem( "snaedis1ATATCGCGGA" );
		final TreeItem	snaedissilva11 = tree.addItem( "snaedis1ATATCGGCGA" );
		final TreeItem	snaedissilva12 = tree.addItem( "snaedis1ATCAGACACG" );
		final TreeItem	snaedissilva13 = tree.addItem( "snaedis1CGGTGTCTCT" );
		final TreeItem	snaedissilva14 = tree.addItem( "snaedis1CGTCGTCGTC" );
		final TreeItem	snaedissilva15 = tree.addItem( "snaedis1CGTCTCTCAG" );
		final TreeItem	snaedissilva16 = tree.addItem( "snaedis1CGTGTCTCTA" );
		final TreeItem	snaedissilva17 = tree.addItem( "snaedis1CTCGCGTGTC" );
		final TreeItem	snaedissilva18 = tree.addItem( "snaedis1GACGCTCGAC" );
		final TreeItem	snaedissilva19 = tree.addItem( "snaedis1GAGACGCACT" );
		final TreeItem	snaedissilva20 = tree.addItem( "snaedis1GATATCGCGA" );
		final TreeItem	snaedissilva21 = tree.addItem( "snaedis1GCGTGTCTCT" );
		final TreeItem	snaedissilva22 = tree.addItem( "snaedis1GCTCGCGTGT" );
		final TreeItem	snaedissilva23 = tree.addItem( "snaedis1GTCTCTATGC" );
		final TreeItem	snaedissilva24 = tree.addItem( "snaedis1TCTCTATGCG" );
		final TreeItem	snaedissilva25 = tree.addItem( "snaedis1TGATACGTCT" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis1map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis1map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis2silva1 = tree.addItem( "snaedis2ACGAGTGCGT" );
		final TreeItem	snaedis2silva2 = tree.addItem( "snaedis2ACGCTCGACA" );
		final TreeItem	snaedis2silva3 = tree.addItem( "snaedis2AGACGCACTC" );
		final TreeItem	snaedis2silva4 = tree.addItem( "snaedis2AGCACTGTAG" );
		final TreeItem	snaedis2silva5 = tree.addItem( "snaedis2ATCAGACACG" );
		final TreeItem	snaedis2silva6 = tree.addItem( "snaedis2ATATCGCGAG" );
		final TreeItem	snaedis2silva7 = tree.addItem( "snaedis2CGTGTCTCTA" );
		final TreeItem	snaedis2silva8 = tree.addItem( "snaedis2CTCGCGTGTC" );
		final TreeItem	snaedis2silva9 = tree.addItem( "snaedis2TGATACGTCT" );
		final TreeItem	snaedis2silva10 = tree.addItem( "snaedis2TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis2map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis2map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis3silva1 = tree.addItem( "snaedis3ACGAGTGCGT" );
		final TreeItem	snaedis3silva2 = tree.addItem( "snaedis3ACGCTCGACA" );
		final TreeItem	snaedis3silva3 = tree.addItem( "snaedis3AGACGCACTC" );
		final TreeItem	snaedis3silva4 = tree.addItem( "snaedis3AGCACTGTAG" );
		final TreeItem	snaedis3silva5 = tree.addItem( "snaedis3ATCAGACACG" );
		final TreeItem	snaedis3silva6 = tree.addItem( "snaedis3ATATCGCGAG" );
		final TreeItem	snaedis3silva7 = tree.addItem( "snaedis3CGTGTCTCTA" );
		final TreeItem	snaedis3silva8 = tree.addItem( "snaedis3CTCGCGTGTC" );
		final TreeItem	snaedis3silva9 = tree.addItem( "snaedis3TGATACGTCT" );
		final TreeItem	snaedis3silva10 = tree.addItem( "snaedis3TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis3map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis3map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis4silva1 = tree.addItem( "snaedis4ACGAGTGCGT" );
		final TreeItem	snaedis4silva2 = tree.addItem( "snaedis4ACGCTCGACA" );
		final TreeItem	snaedis4silva3 = tree.addItem( "snaedis4AGACGCACTC" );
		final TreeItem	snaedis4silva4 = tree.addItem( "snaedis4AGCACTGTAG" );
		final TreeItem	snaedis4silva5 = tree.addItem( "snaedis4ATCAGACACG" );
		final TreeItem	snaedis4silva6 = tree.addItem( "snaedis4ATATCGCGAG" );
		final TreeItem	snaedis4silva7 = tree.addItem( "snaedis4CGTGTCTCTA" );
		final TreeItem	snaedis4silva8 = tree.addItem( "snaedis4CTCGCGTGTC" );
		final TreeItem	snaedis4silva9 = tree.addItem( "snaedis4TGATACGTCT" );
		final TreeItem	snaedis4silva10 = tree.addItem( "snaedis4TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis4map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis4map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis5silva1 = tree.addItem( "snaedis5ACGAGTGCGT" );
		final TreeItem	snaedis5silva2 = tree.addItem( "snaedis5ACGCTCGACA" );
		final TreeItem	snaedis5silva3 = tree.addItem( "snaedis5AGACGCACTC" );
		final TreeItem	snaedis5silva4 = tree.addItem( "snaedis5AGCACTGTAG" );
		final TreeItem	snaedis5silva5 = tree.addItem( "snaedis5ATCAGACACG" );
		final TreeItem	snaedis5silva6 = tree.addItem( "snaedis5ATATCGCGAG" );
		final TreeItem	snaedis5silva7 = tree.addItem( "snaedis5CGTGTCTCTA" );
		final TreeItem	snaedis5silva8 = tree.addItem( "snaedis5CTCGCGTGTC" );
		final TreeItem	snaedis5silva9 = tree.addItem( "snaedis5TGATACGTCT" );
		final TreeItem	snaedis5silva10 = tree.addItem( "snaedis5TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis5map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis5map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis6silva1 = tree.addItem( "snaedis6ACGAGTGCGT" );
		final TreeItem	snaedis6silva2 = tree.addItem( "snaedis6ACGCTCGACA" );
		final TreeItem	snaedis6silva3 = tree.addItem( "snaedis6AGACGCACTC" );
		final TreeItem	snaedis6silva4 = tree.addItem( "snaedis6AGCACTGTAG" );
		final TreeItem	snaedis6silva5 = tree.addItem( "snaedis6ATCAGACACG" );
		final TreeItem	snaedis6silva6 = tree.addItem( "snaedis6ATATCGCGAG" );
		final TreeItem	snaedis6silva7 = tree.addItem( "snaedis6CGTGTCTCTA" );
		final TreeItem	snaedis6silva8 = tree.addItem( "snaedis6CTCGCGTGTC" );
		final TreeItem	snaedis6silva9 = tree.addItem( "snaedis6TGATACGTCT" );
		final TreeItem	snaedis6silva10 = tree.addItem( "snaedis6TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis6map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis6map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	snaedis7silva1 = tree.addItem( "snaedis7ACGAGTGCGT" );
		final TreeItem	snaedis7silva2 = tree.addItem( "snaedis7ACGCTCGACA" );
		final TreeItem	snaedis7silva3 = tree.addItem( "snaedis7AGACGCACTC" );
		final TreeItem	snaedis7silva4 = tree.addItem( "snaedis7AGCACTGTAG" );
		final TreeItem	snaedis7silva5 = tree.addItem( "snaedis7ATCAGACACG" );
		final TreeItem	snaedis7silva6 = tree.addItem( "snaedis7ATATCGCGAG" );
		final TreeItem	snaedis7silva7 = tree.addItem( "snaedis7CGTGTCTCTA" );
		final TreeItem	snaedis7silva8 = tree.addItem( "snaedis7CTCGCGTGTC" );
		final TreeItem	snaedis7silva9 = tree.addItem( "snaedis7TGATACGTCT" );
		final TreeItem	snaedis7silva10 = tree.addItem( "snaedis7TCTCTATGCG" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis7map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis7map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
	
		final TreeItem	snaedis8silva1 = tree.addItem( "snaedis8ACGAGTGCGT" );
		final TreeItem	snaedis8silva2 = tree.addItem( "snaedis8ACGCTCGACA" );
		final TreeItem	snaedis8silva3 = tree.addItem( "snaedis8AGACGCACTC" );
		final TreeItem	snaedis8silva4 = tree.addItem( "snaedis8AGCACTGTAG" );
		final TreeItem	snaedis8silva5 = tree.addItem( "snaedis8ATCAGACACG" );
		final TreeItem	snaedis8silva6 = tree.addItem( "snaedis8ATATCGCGAG" );
		final TreeItem	snaedis8silva7 = tree.addItem( "snaedis8CGTGTCTCTA" );
		final TreeItem	snaedis8silva8 = tree.addItem( "snaedis8CTCGCGTGTC" );
		
		for( ; i < tree.getItemCount(); i++ ) {
			TreeItem ti = tree.getItem( i );
			String text = ti.getText();
			for( String key : rsnaedis8map.keySet() ) {
				if( text.contains(key) ) {
					String val = rsnaedis8map.get(key);
					ti.setText( text.replace(key, val) );
				}
			}
		}
		
		final TreeItem	gumolsilva1 = tree.addItem( "gumolACGACGTCGA" );
		final TreeItem	gumolsilva2 = tree.addItem( "gumolACGAGTGCGT" );
		final TreeItem	gumolsilva3 = tree.addItem( "gumolACGCTCGACA" );
		final TreeItem	gumolsilva4 = tree.addItem( "gumolACGNGTCGCT" );
		final TreeItem	gumolsilva5 = tree.addItem( "gumolAGACGCACTC" );
		final TreeItem	gumolsilva6 = tree.addItem( "gumolAGCACTGTAG" );
		final TreeItem	gumolsilva7 = tree.addItem( "gumolAGGACGCACT" );
		final TreeItem	gumolsilva8 = tree.addItem( "gumolAGGCACTGTA" );
		final TreeItem	gumolsilva9 = tree.addItem( "gumolATATCGCGAG" );
		final TreeItem	gumolsilva10 = tree.addItem( "gumolATATCGCGGA" );
		final TreeItem	gumolsilva11 = tree.addItem( "gumolATATCGGCGA" );
		final TreeItem	gumolsilva12 = tree.addItem( "gumolATCAGACACG" );
		final TreeItem	gumolsilva13 = tree.addItem( "gumolCGGTGTCTCT" );
		final TreeItem	gumolsilva14 = tree.addItem( "gumolCGTCGTCGTC" );
		final TreeItem	gumolsilva15 = tree.addItem( "gumolCGTCTCTCAG" );
		final TreeItem	gumolsilva16 = tree.addItem( "gumolCGTGTCTCTA" );
		final TreeItem	gumolsilva17 = tree.addItem( "gumolCTCGCGTGTC" );
		final TreeItem	gumolsilva18 = tree.addItem( "gumolGACGCTCGAC" );
		final TreeItem	gumolsilva19 = tree.addItem( "gumolGAGACGCACT" );
		final TreeItem	gumolsilva20 = tree.addItem( "gumolGATATCGCGA" );
		final TreeItem	gumolsilva21 = tree.addItem( "gumolGCGTGTCTCT" );
		final TreeItem	gumolsilva22 = tree.addItem( "gumolGCTCGCGTGT" );
		final TreeItem	gumolsilva23 = tree.addItem( "gumolGTCTCTATGC" );
		final TreeItem	gumolsilva24 = tree.addItem( "gumolTCTCTATGCG" );
		final TreeItem	gumolsilva25 = tree.addItem( "gumolTGATACGTCT" );
		
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
	
	boolean shift = false;
	boolean ctrl = false;
	boolean alt = false;
	boolean zkey = false;
	
	public void recursiveBlast( TreeItem ti, Map<Integer,Map<String,Integer>> mstr, int depth ) {
		if( ti.getChildCount() > 0 ) {
			Map<String,Integer> mi;
			if( mstr.containsKey(depth) ) {
				mi = mstr.get(depth);
			} else {
				mi = new HashMap<String,Integer>();
				mstr.put( depth, mi );
			}
			
			String t = ti.getText();
			int i = t.lastIndexOf('(');
			if( i != -1 ) {
				int n = t.indexOf(')', i+1);
				try {
					int val = Integer.parseInt( t.substring(i+1, n) );
					mi.put( t.substring(0,i).trim(), val );
				} catch( Exception e ) {}
			}
			
			for( i = 0; i < ti.getChildCount(); i++ ) {
				recursiveBlast( ti.getChild(i), mstr, depth+1 );
			}
		}
	}
	
	public static Map<String,List<Double>>	datamap = new HashMap<String,List<Double>>();
	public static Map<String,Integer>		indexmap = new HashMap<String,Integer>();
	public static int max = 0;
	
	Map<String,String>	snaedis1map;
	Map<String,String>	snaedis2map;
	Map<String,String>	snaedis3map;
	Map<String,String>	snaedis4map;
	Map<String,String>	snaedis5map;
	Map<String,String>	snaedis6map;
	Map<String,String>	snaedis7map;
	Map<String,String>	snaedis8map;
	
	Map<String,String>	snaedis1colormap;
	Map<String,String>	snaedis2colormap;
	Map<String,String>	snaedis3colormap;
	Map<String,String>	snaedis4colormap;
	Map<String,String>	snaedis5colormap;
	Map<String,String>	snaedis6colormap;
	Map<String,String>	snaedis7colormap;
	Map<String,String>	snaedis8colormap;
	
	Map<String,Double>	snaedis1heatmap;
	Map<String,Double>	snaedis2heatmap;
	Map<String,Double>	snaedis3heatmap;
	Map<String,Double>	snaedis4heatmap;
	Map<String,Double>	snaedis5heatmap;
	Map<String,Double>	snaedis6heatmap;
	Map<String,Double>	snaedis7heatmap;
	Map<String,Double>	snaedis8heatmap;
	
	Map<String,Double>	snaedis1phmap;
	Map<String,Double>	snaedis2phmap;
	Map<String,Double>	snaedis3phmap;
	Map<String,Double>	snaedis4phmap;
	Map<String,Double>	snaedis5phmap;
	Map<String,Double>	snaedis6phmap;
	Map<String,Double>	snaedis7phmap;
	Map<String,Double>	snaedis8phmap;
	
	public void pcaAnalysis() {
		console( "about to pca" );
		String allstr = "";
		String[] names = new String[ indexmap.size() ];
		for( String key : indexmap.keySet() ) {
			names[ indexmap.get(key) ] = key;
		}
		for( String name : names ) {
			allstr += "\t"+name;
		}
		allstr += "\n";
		
		for( String key : datamap.keySet() ) {
			console( "pca "+key );
			List<Double> tdlist = datamap.get(key);
			String cres = key;
			for( int i = 0; i < tdlist.size(); i++ ) {
				cres += "\t"+tdlist.get(i);
			}
			for( int i = tdlist.size(); i < max; i++ ) {
				tdlist.add( 0.0 );
				cres += "\t"+tdlist.get(i);
			}
			
			allstr += cres+"\n";
		}
		
		allstr += "\n";
		for( String key : datamap.keySet() ) {
			double dval = 0.0;
			String keyval = key.substring(8);
			if( snaedis1heatmap.containsKey( keyval ) ) {
				dval = snaedis1heatmap.get( keyval );
			} else if( snaedis2heatmap.containsKey( keyval ) ) {
				dval = snaedis2heatmap.get( keyval );
			} else if( snaedis3heatmap.containsKey( keyval ) ) {
				dval = snaedis3heatmap.get( keyval );
			} else if( snaedis4heatmap.containsKey( keyval ) ) {
				dval = snaedis4heatmap.get( keyval );
			} else if( snaedis5heatmap.containsKey( keyval ) ) {
				dval = snaedis5heatmap.get( keyval );
			} else if( snaedis6heatmap.containsKey( keyval ) ) {
				dval = snaedis6heatmap.get( keyval );
			} else if( snaedis7heatmap.containsKey( keyval ) ) {
				dval = snaedis7heatmap.get( keyval );
			} else if( snaedis8heatmap.containsKey( keyval ) ) {
				dval = snaedis8heatmap.get( keyval );
			}
			
			double tval = (dval-50.0)/40.0;
			allstr += tval+"\t0.0\t"+(1.0-tval)+"\n";
		}
		
		allstr += "\n";
		for( String key : datamap.keySet() ) {
			double dval = 0.0;
			String keyval = key.substring(8);
			if( snaedis1phmap.containsKey( keyval ) ) {
				dval = snaedis1phmap.get( keyval );
			} else if( snaedis2phmap.containsKey( keyval ) ) {
				dval = snaedis2phmap.get( keyval );
			} else if( snaedis3phmap.containsKey( keyval ) ) {
				dval = snaedis3phmap.get( keyval );
			} else if( snaedis4phmap.containsKey( keyval ) ) {
				dval = snaedis4phmap.get( keyval );
			} else if( snaedis5phmap.containsKey( keyval ) ) {
				dval = snaedis5phmap.get( keyval );
			} else if( snaedis6phmap.containsKey( keyval ) ) {
				dval = snaedis6phmap.get( keyval );
			} else if( snaedis7phmap.containsKey( keyval ) ) {
				dval = snaedis7phmap.get( keyval );
			} else if( snaedis8phmap.containsKey( keyval ) ) {
				dval = snaedis8phmap.get( keyval );
			}
			
			double tval = (dval-5.0)/4.0;
			allstr += tval+"\t"+(1.0-tval)+"\t0.0\n";
		}
		
		allstr += "\n";
		for( String key : datamap.keySet() ) {
			String dval = "";
			String keyval = key.substring(8);
			if( snaedis1colormap.containsKey( keyval ) ) {
				dval = snaedis1colormap.get( keyval );
			} else if( snaedis2colormap.containsKey( keyval ) ) {
				dval = snaedis2colormap.get( keyval );
			} else if( snaedis3colormap.containsKey( keyval ) ) {
				dval = snaedis3colormap.get( keyval );
			} else if( snaedis4colormap.containsKey( keyval ) ) {
				dval = snaedis4colormap.get( keyval );
			} else if( snaedis5colormap.containsKey( keyval ) ) {
				dval = snaedis5colormap.get( keyval );
			} else if( snaedis6colormap.containsKey( keyval ) ) {
				dval = snaedis6colormap.get( keyval );
			} else if( snaedis7colormap.containsKey( keyval ) ) {
				dval = snaedis7colormap.get( keyval );
			} else if( snaedis8colormap.containsKey( keyval ) ) {
				dval = snaedis8colormap.get( keyval );
			}
			
			allstr += dval;
		}

		DialogBox db = new DialogBox();
		Caption cap = db.getCaption();
		db.setAutoHideEnabled( true );
		cap.setText("PCA");
		TextArea ta = new TextArea();
		ta.setSize("512px", "384px");
		db.add( ta );
		ta.setText( allstr );
		db.center();
		
		/*String allstr = "";
		for( String key : datamap.keySet() ) {
			List<Double> tdlist = datamap.get(key);
			String cres = key;
			for( int i = 0; i < tdlist.size(); i++ ) {
				cres += "\t"+tdlist.get(i);
			}
			for( int i = tdlist.size(); i < max; i++ ) {
				tdlist.add( 0.0 );
				cres += "\t"+tdlist.get(i);
			}
			
			allstr += cres+"\n";
		}

		DialogBox db = new DialogBox();
		Caption cap = db.getCaption();
		db.setAutoHideEnabled( true );
		cap.setText("PCA");
		TextArea ta = new TextArea();
		ta.setSize("512px", "384px");
		db.add( ta );
		ta.setText( allstr );
		db.center();*/
	}
	
	public StringBuilder calcContent( TreeItem selectedtree, int level, String filter ) {
		Map<Integer,Map<String,Integer>>	mstr = new HashMap<Integer,Map<String,Integer>>();
		try {
			recursiveBlast( selectedtree, mstr, 0 );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		StringBuilder res = new StringBuilder();
		for( Integer d : mstr.keySet() ) {
			Map<String,Integer> mi = mstr.get(d);
			
			List<Object[]> lobj = new ArrayList<Object[]>();
			for( String key : mi.keySet() ) {
				Integer i = mi.get(key);
				Object[] obj = { i, key };
				lobj.add( obj );
			}
			Collections.sort( lobj, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					Integer i1 = (Integer)o1[0];
					Integer i2 = (Integer)o2[0];
					return i2.intValue() - i1.intValue();
				}
			});
			for( Object[] obj : lobj ) {
				res.append( obj[1] + "\t" + obj[0] + "\n" );
			}
			res.append("\n");
			
			if( d == level ) {
				double sum = 0.0;
				for( Object[] obj : lobj ) {
					String val = (String)obj[1];
					if( filter == null || val.startsWith(filter) ) {
						if( !indexmap.containsKey( val ) ) {
							indexmap.put( val, indexmap.size() );
							/*for( String key : datamap.keySet() ) {
								List<Double> dlist = datamap.get(key);
								for( int i = dlist.size(); i <= indexmap.size(); i++ ) {
									dlist.add( 0.0 );
								}
							}*/
							max = Math.max(max, indexmap.size());
						}
						sum += (Integer)obj[0];
					}
				}
				
				List<Double>	dlist = new ArrayList<Double>();
				String tstr = selectedtree.getText();
				int ti = tstr.indexOf(' ');
				if( ti == -1 ) ti = tstr.length();
				datamap.put( tstr.substring(0,ti), dlist );
				
				for( Object[] obj : lobj ) {
					String val = (String)obj[1];
					
					if( filter == null || val.startsWith(filter) ) {
						int k = indexmap.get( val );
						for( int i = dlist.size(); i <= k; i++ ) {
							dlist.add( 0.0 );
						}
						dlist.set( k, ((Integer)obj[0])/sum );
					}
				}
				
				/*for( String key : datamap.keySet() ) {
					List<Double> tdlist = datamap.get(key);
					String cres = key;
					for( int i = 0; i < tdlist.size(); i++ ) {
						cres += "\t"+tdlist.get(i);
					}
					for( int i = tdlist.size(); i < max; i++ ) {
						tdlist.add( 0.0 );
						cres += "\t"+tdlist.get(i);
					}
					console( cres );
				}
				console("\n");*/
			}
		}
		
		return res;
	}
	
	public void loadNode( TreeItem selectedtree, String server, Map<String,String> mapstr ) {
		String nodename = selectedtree.getText();
		boolean already = false;
		if( mapstr != null ) for( String key : mapstr.keySet() ) {
			if( nodename.equals("eyjo"+key) ) {
				String val = mapstr.get(key);
				runSpec( selectedtree, "http://"+server+"/mysilva_"+val+".txt" );
				already = true;
				break;
				}
		}
		
		String prefix = "snaedis";
		if( zkey ) {
			console( "zkey " + zkey );
			prefix = "short";
		}
		
		console( prefix );
		for( String key : snaedis1map.keySet() ) {
			if( nodename.equals("snaedis1"+key) ) {
				String val = snaedis1map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"1_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis2map.keySet() ) {
			if( nodename.equals("snaedis2"+key) ) {
				String val = snaedis2map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"2_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis3map.keySet() ) {
			if( nodename.equals("snaedis3"+key) ) {
				String val = snaedis3map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"3_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis4map.keySet() ) {
			if( nodename.equals("snaedis4"+key) ) {
				String val = snaedis4map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"4_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis5map.keySet() ) {
			if( nodename.equals("snaedis5"+key) ) {
				String val = snaedis5map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"5_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis6map.keySet() ) {
			if( nodename.equals("snaedis6"+key) ) {
				String val = snaedis6map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"6_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis7map.keySet() ) {
			if( nodename.equals("snaedis7"+key) ) {
				String val = snaedis7map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"7_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		for( String key : snaedis8map.keySet() ) {
			if( nodename.equals("snaedis8"+key) ) {
				String val = snaedis8map.get(key);
				runSpec( selectedtree, "http://"+server+"/"+prefix+"8_"+val+".txt" );
				already = true;
				break;
			}
		}
		
		if( !already ) {
			if( nodename.startsWith("gumol") ) {
				runSpec( selectedtree, "http://"+server+"/gumol_"+nodename.substring(5)+".txt" );
			} else if( nodename.startsWith("snaedis1") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"1_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis2") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"2_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis3") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"3_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis4") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"4_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis5") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"5_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis6") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"6_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis7") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"7_"+nodename.substring(7)+".txt" );
			} else if( nodename.startsWith("snaedis8") ) {
				runSpec( selectedtree, "http://"+server+"/"+prefix+"8_"+nodename.substring(7)+".txt" );
			} else if( nodename.equals("eyjosilva") ) runSpec( selectedtree, "http://"+server+"/mysilva1.txt" );
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
		}
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final String server = "130.208.252.7";//Location.getHost();
		//final String server = "127.0.0.1:8888";in
		
		final Map<String,String>	mapstr = new HashMap<String,String>();
		mapstr.put( "148-30", "ACGAGTGCGT" );
		mapstr.put( "115-50", "ACGCTCGACA" );
		mapstr.put( "121-0", "AGACGCACTC" );
		mapstr.put( "133-0", "ATCAGACACG" );
		mapstr.put( "133-50", "ATATCGCGAG" );
		mapstr.put( "137-0", "CGTGTCTCTA" );
		mapstr.put( "137-50", "CTCGCGTGTC" );
		mapstr.put( "148-0", "TCTCTATGCG" );
		mapstr.put( "148-50", "TGATACGTCT" );
		mapstr.put( "153-50", "CATAGTAGTG" );
		mapstr.put( "153-800", "CGAGAGATAC" );
		mapstr.put( "160-0", "ATACGACGTA" );
		mapstr.put( "160-50", "TCACGTACTA" );
		mapstr.put( "166-0", "CGTCTAGTAC" );
		mapstr.put( "166-1000", "TCTACGTAGC" );
		mapstr.put( "197-0", "TGTACTACTC" );
		mapstr.put( "197-10", "ACGACTACAG" );
		mapstr.put( "197-50", "CGTAGACTAG" );
		mapstr.put( "200-0", "TACGAGTATG" );
		mapstr.put( "200-50", "TACTCTCGTG" );
		mapstr.put( "200-1000", "TAGAGACGAG" );
		final Map<String,String>	rmapstr = new HashMap<String,String>();
		for( String key : mapstr.keySet() ) {
			rmapstr.put( mapstr.get(key), key );
		}
		
		snaedis1map = new HashMap<String,String>();
		snaedis1map.put( "770_geysir_north_jardvegur", "ACGAGTGCGT" );
		snaedis1map.put( "770_geysir_north_vatn", "ACGCTCGACA" );
		snaedis1map.put( "771_geysir_north_jardvegur", "AGACGCACTC" );
		snaedis1map.put( "771_geysir_north_vatn", "AGCACTGTAG" );
		snaedis1map.put( "772_geysir_north_jardvegur", "ATCAGACACG" );
		snaedis1map.put( "772_geysir_north_vatn", "ATATCGCGAG" );
		snaedis1map.put( "773_geysir_west_jardvegur", "CGTGTCTCTA" );
		snaedis1map.put( "773_geysir_west_vatn", "CTCGCGTGTC" );
		snaedis1map.put( "774_geysir_west_jardvegur", "TGATACGTCT" );
		snaedis1map.put( "774_geysir_west_vatn", "TCTCTATGCG" );
		
		snaedis1colormap = new HashMap<String,String>();
		snaedis1colormap.put( "770_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "770_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "771_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "771_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "772_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "772_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "773_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "773_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "774_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "774_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		
		snaedis1heatmap = new HashMap<String,Double>();
		snaedis1heatmap.put( "770_geysir_north_jardvegur", 83.0 );
		snaedis1heatmap.put( "770_geysir_north_vatn", 83.0 );
		snaedis1heatmap.put( "771_geysir_north_jardvegur", 72.0 );
		snaedis1heatmap.put( "771_geysir_north_vatn", 72.0 );
		snaedis1heatmap.put( "772_geysir_north_jardvegur", 68.5 );
		snaedis1heatmap.put( "772_geysir_north_vatn", 68.5 );
		snaedis1heatmap.put( "773_geysir_west_jardvegur", 79.4 );
		snaedis1heatmap.put( "773_geysir_west_vatn", 79.4 );
		snaedis1heatmap.put( "774_geysir_west_jardvegur", 88.0 );
		snaedis1heatmap.put( "774_geysir_west_vatn", 88.0 );
		
		snaedis1phmap = new HashMap<String,Double>();
		snaedis1phmap.put( "770_geysir_north_jardvegur", 6.75 );
		snaedis1phmap.put( "770_geysir_north_vatn", 6.75 );
		snaedis1phmap.put( "771_geysir_north_jardvegur", 6.0 );
		snaedis1phmap.put( "771_geysir_north_vatn", 6.0 );
		snaedis1phmap.put( "772_geysir_north_jardvegur", 5.0 );
		snaedis1phmap.put( "772_geysir_north_vatn", 5.0 );
		snaedis1phmap.put( "773_geysir_west_jardvegur", 9.0 );
		snaedis1phmap.put( "773_geysir_west_vatn", 9.0 );
		snaedis1phmap.put( "774_geysir_west_jardvegur", 8.0 );
		snaedis1phmap.put( "774_geysir_west_vatn", 8.0 );
		
		rsnaedis1map = new HashMap<String,String>();
		for( String key : snaedis1map.keySet() ) {
			rsnaedis1map.put( snaedis1map.get(key), key );
		}
		
		snaedis2map = new HashMap<String,String>();
		snaedis2map.put( "775_geysir_west_jardvegur", "ACGAGTGCGT" );
		snaedis2map.put( "775_geysir_west_vatn", "ACGCTCGACA" );
		snaedis2map.put( "776_geysir_west_jardvegur", "AGACGCACTC" );
		snaedis2map.put( "776_geysir_west_vatn", "AGCACTGTAG" );
		snaedis2map.put( "777_fludir_vatn", "ATCAGACACG" );
		snaedis2map.put( "777_fludir_lifmassi", "ATATCGCGAG" );
		snaedis2map.put( "778_fludir_jardvegur", "CGTGTCTCTA" );
		snaedis2map.put( "778_fludir_vatn", "CTCGCGTGTC" );
		snaedis2map.put( "779_fludir_jardvegur", "TGATACGTCT" );
		snaedis2map.put( "779_fludir_vatn", "TCTCTATGCG" );
		
		snaedis2colormap = new HashMap<String,String>();
		snaedis2colormap.put( "775_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "775_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "776_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "776_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "777_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "777_fludir_lifmassi", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "778_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "778_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "779_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "779_fludir_vatn", "1.0\t0.0\t1.0\n" );
		
		snaedis2heatmap = new HashMap<String,Double>();
		snaedis2heatmap.put( "775_geysir_west_jardvegur", 83.0 );
		snaedis2heatmap.put( "775_geysir_west_vatn", 83.0 );
		snaedis2heatmap.put( "776_geysir_west_jardvegur", 88.0 );
		snaedis2heatmap.put( "776_geysir_west_vatn", 88.0 );
		snaedis2heatmap.put( "777_fludir_vatn", 63.0 );
		snaedis2heatmap.put( "777_fludir_lifmassi", 63.0 );
		snaedis2heatmap.put( "778_fludir_jardvegur", 69.4 );
		snaedis2heatmap.put( "778_fludir_vatn", 69.4 );
		snaedis2heatmap.put( "779_fludir_jardvegur", 79.1 );
		snaedis2heatmap.put( "779_fludir_vatn", 79.1 );
		
		snaedis2phmap = new HashMap<String,Double>();
		snaedis2phmap.put( "775_geysir_west_jardvegur", 9.0 );
		snaedis2phmap.put( "775_geysir_west_vatn", 9.0 );
		snaedis2phmap.put( "776_geysir_west_jardvegur", 7.0 );
		snaedis2phmap.put( "776_geysir_west_vatn", 7.0 );
		snaedis2phmap.put( "777_fludir_vatn", 8.0 );
		snaedis2phmap.put( "777_fludir_lifmassi", 8.0 );
		snaedis2phmap.put( "778_fludir_jardvegur", 8.25 );
		snaedis2phmap.put( "778_fludir_vatn", 8.25 );
		snaedis2phmap.put( "779_fludir_jardvegur", 8.0 );
		snaedis2phmap.put( "779_fludir_vatn", 8.0 );
		
		snaedis3map = new HashMap<String,String>();
		snaedis3map.put( "780_fludir_jardvegur", "ACGAGTGCGT" );
		snaedis3map.put( "780_fludir_vatn", "ACGCTCGACA" );
		snaedis3map.put( "781_olkelduhals_vatn", "AGACGCACTC" );
		snaedis3map.put( "781_olkelduhals_lifmassi", "AGCACTGTAG" );
		snaedis3map.put( "782_olkelduhals_lifmassi", "ATCAGACACG" );
		snaedis3map.put( "783_olkelduhals_jardvlifm", "ATATCGCGAG" );
		snaedis3map.put( "783_olkelduhals_vatn", "CGTGTCTCTA" );
		snaedis3map.put( "808_hrafntinnusker_jardvegur", "CTCGCGTGTC" );
		snaedis3map.put( "808_hrafntinnusker_vatn", "TGATACGTCT" );
		snaedis3map.put( "808_hrafntinnusker_lifmassi", "TCTCTATGCG" );
		
		snaedis3colormap = new HashMap<String,String>();
		snaedis3colormap.put( "780_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "780_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "781_olkelduhals_vatn", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "781_olkelduhals_lifmassi", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "782_olkelduhals_lifmassi", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "783_olkelduhals_jardvlifm", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "783_olkelduhals_vatn", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		
		snaedis3heatmap = new HashMap<String,Double>();
		snaedis3heatmap.put( "780_fludir_jardvegur", 87.6 );
		snaedis3heatmap.put( "780_fludir_vatn", 87.6 );
		snaedis3heatmap.put( "781_olkelduhals_vatn", 70.0 );
		snaedis3heatmap.put( "781_olkelduhals_lifmassi", 70.0 );
		snaedis3heatmap.put( "782_olkelduhals_lifmassi", 60.0 );
		snaedis3heatmap.put( "783_olkelduhals_jardvlifm", 70.0 );
		snaedis3heatmap.put( "783_olkelduhals_vatn", 70.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_jardvegur", 72.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_vatn", 72.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_lifmassi", 72.0 );
		
		snaedis3phmap = new HashMap<String,Double>();
		snaedis3phmap.put( "780_fludir_jardvegur", 8.5 );
		snaedis3phmap.put( "780_fludir_vatn", 8.5 );
		snaedis3phmap.put( "781_olkelduhals_vatn", 6.5 );
		snaedis3phmap.put( "781_olkelduhals_lifmassi", 6.5 );
		snaedis3phmap.put( "782_olkelduhals_lifmassi", 6.5 );
		snaedis3phmap.put( "783_olkelduhals_jardvlifm", 6.0 );
		snaedis3phmap.put( "783_olkelduhals_vatn", 6.0 );
		snaedis3phmap.put( "808_hrafntinnusker_jardvegur", 7.0 );
		snaedis3phmap.put( "808_hrafntinnusker_vatn", 7.0 );
		snaedis3phmap.put( "808_hrafntinnusker_lifmassi", 7.0 );
		
		snaedis4map = new HashMap<String,String>();
		snaedis4map.put( "809_hrafntinnusker_jardvegur", "ACGAGTGCGT" );
		snaedis4map.put( "809_hrafntinnusker_vatn", "ACGCTCGACA" );
		snaedis4map.put( "809_hrafntinnusker_lifmassi", "AGACGCACTC" );
		snaedis4map.put( "810_hrafntinnusker_jardvegur", "AGCACTGTAG" );
		snaedis4map.put( "810_hrafntinnusker_vatn", "ATCAGACACG" );
		snaedis4map.put( "810_hrafntinnusker_lifmassi", "ATATCGCGAG" );
		snaedis4map.put( "811_hrafntinnusker_jardvegur", "CGTGTCTCTA" );
		snaedis4map.put( "811_hrafntinnusker_vatn", "CTCGCGTGTC" );
		snaedis4map.put( "811_hrafntinnusker_lifmassi", "TGATACGTCT" );
		snaedis4map.put( "812_hrafntinnusker_jardvegur", "TCTCTATGCG" );
		
		snaedis4colormap = new HashMap<String,String>();
		snaedis4colormap.put( "809_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "809_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "809_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "812_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		
		snaedis4heatmap = new HashMap<String,Double>();
		snaedis4heatmap.put( "809_hrafntinnusker_jardvegur", 63.5 );
		snaedis4heatmap.put( "809_hrafntinnusker_vatn", 63.5 );
		snaedis4heatmap.put( "809_hrafntinnusker_lifmassi", 63.5 );
		snaedis4heatmap.put( "810_hrafntinnusker_jardvegur", 68.0 );
		snaedis4heatmap.put( "810_hrafntinnusker_vatn", 68.0 );
		snaedis4heatmap.put( "810_hrafntinnusker_lifmassi", 68.0 );
		snaedis4heatmap.put( "811_hrafntinnusker_jardvegur", 71.1 );
		snaedis4heatmap.put( "811_hrafntinnusker_vatn", 71.1 );
		snaedis4heatmap.put( "811_hrafntinnusker_lifmassi", 71.1 );
		snaedis4heatmap.put( "812_hrafntinnusker_jardvegur", 68.3 );
		
		snaedis4phmap = new HashMap<String,Double>();
		snaedis4phmap.put( "809_hrafntinnusker_jardvegur", 6.0 );
		snaedis4phmap.put( "809_hrafntinnusker_vatn", 6.0 );
		snaedis4phmap.put( "809_hrafntinnusker_lifmassi", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_jardvegur", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_vatn", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_lifmassi", 6.0 );
		snaedis4phmap.put( "811_hrafntinnusker_jardvegur", 7.0 );
		snaedis4phmap.put( "811_hrafntinnusker_vatn", 7.0 );
		snaedis4phmap.put( "811_hrafntinnusker_lifmassi", 7.0 );
		snaedis4phmap.put( "812_hrafntinnusker_jardvegur", 6.0 );
	
		snaedis5map = new HashMap<String,String>();
		snaedis5map.put( "812_hrafntinnusker_vatn", "ACGAGTGCGT" );
		snaedis5map.put( "813_hrafntinnusker_jardvegur", "ACGCTCGACA" );
		snaedis5map.put( "813_hrafntinnusker_vatn", "AGACGCACTC" );
		snaedis5map.put( "814_hrafntinnusker_jardvegur", "AGCACTGTAG" );
		snaedis5map.put( "814_hrafntinnusker_vatn", "ATCAGACACG" );
		snaedis5map.put( "815_reykjadalir_jardvegur", "ATATCGCGAG" );
		snaedis5map.put( "815_reykjadalir_vatn", "CGTGTCTCTA" );
		snaedis5map.put( "815_reykjadalir_lifmassi", "CTCGCGTGTC" );
		snaedis5map.put( "816_vondugil_jardvegur", "TGATACGTCT" );
		snaedis5map.put( "816_vondugil_vatn", "TCTCTATGCG" );
		
		snaedis5colormap = new HashMap<String,String>();
		snaedis5colormap.put( "812_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "813_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "813_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "814_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "814_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "815_reykjadalir_jardvegur", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "815_reykjadalir_vatn", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "815_reykjadalir_lifmassi", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "816_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis5colormap.put( "816_vondugil_vatn", "1.0\t0.0\t0.0\n" );
	
		snaedis5heatmap = new HashMap<String,Double>();
		snaedis5heatmap.put( "812_hrafntinnusker_vatn", 68.3 );
		snaedis5heatmap.put( "813_hrafntinnusker_jardvegur", 71.5 );
		snaedis5heatmap.put( "813_hrafntinnusker_vatn", 71.5 );
		snaedis5heatmap.put( "814_hrafntinnusker_jardvegur", 71.7 );
		snaedis5heatmap.put( "814_hrafntinnusker_vatn", 71.7 );
		snaedis5heatmap.put( "815_reykjadalir_jardvegur", 54.0 );
		snaedis5heatmap.put( "815_reykjadalir_vatn", 54.0 );
		snaedis5heatmap.put( "815_reykjadalir_lifmassi", 54.0 );
		snaedis5heatmap.put( "816_vondugil_jardvegur", 78.0 );
		snaedis5heatmap.put( "816_vondugil_vatn", 78.0 );
	
		snaedis5phmap = new HashMap<String,Double>();
		snaedis5phmap.put( "812_hrafntinnusker_vatn", 6.0 );
		snaedis5phmap.put( "813_hrafntinnusker_jardvegur", 5.75 );
		snaedis5phmap.put( "813_hrafntinnusker_vatn", 5.75 );
		snaedis5phmap.put( "814_hrafntinnusker_jardvegur", 5.0 );
		snaedis5phmap.put( "814_hrafntinnusker_vatn", 5.0 );
		snaedis5phmap.put( "815_reykjadalir_jardvegur", 6.0 );
		snaedis5phmap.put( "815_reykjadalir_vatn", 6.0 );
		snaedis5phmap.put( "815_reykjadalir_lifmassi", 6.0 );
		snaedis5phmap.put( "816_vondugil_jardvegur", 9.0 );
		snaedis5phmap.put( "816_vondugil_vatn", 9.0 );
		
		snaedis6map = new HashMap<String,String>();
		snaedis6map.put( "817_vondugil_jardvegur", "ACGAGTGCGT" );
		snaedis6map.put( "817_vondugil_vatn", "ACGCTCGACA" );
		snaedis6map.put( "818_vondugil_jardvegur", "AGACGCACTC" );
		snaedis6map.put( "818_vondugil_vatn", "AGCACTGTAG" );
		snaedis6map.put( "819_vondugil_jardvegur", "ATCAGACACG" );
		snaedis6map.put( "819_vondugil_vatn", "ATATCGCGAG" );
		snaedis6map.put( "820_vondugil_jardvegur", "CGTGTCTCTA" );
		snaedis6map.put( "820_vondugil_vatn", "CTCGCGTGTC" );
		snaedis6map.put( "821_vondugil_jardvegur", "TGATACGTCT" );
		snaedis6map.put( "821_vondugil_vatn", "TCTCTATGCG" );
		
		snaedis6colormap = new HashMap<String,String>();
		snaedis6colormap.put( "817_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "817_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "818_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "818_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "819_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "819_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "820_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "820_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "821_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "821_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		
		snaedis6heatmap = new HashMap<String,Double>();
		snaedis6heatmap.put( "817_vondugil_jardvegur", 75.7 );
		snaedis6heatmap.put( "817_vondugil_vatn", 75.7 );
		snaedis6heatmap.put( "818_vondugil_jardvegur", 78.5 );
		snaedis6heatmap.put( "818_vondugil_vatn", 78.5 );
		snaedis6heatmap.put( "819_vondugil_jardvegur", 80.5 );
		snaedis6heatmap.put( "819_vondugil_vatn", 80.5 );
		snaedis6heatmap.put( "820_vondugil_jardvegur", 71.1 );
		snaedis6heatmap.put( "820_vondugil_vatn", 71.1 );
		snaedis6heatmap.put( "821_vondugil_jardvegur", 79.0 );
		snaedis6heatmap.put( "821_vondugil_vatn", 79.0 );
	
		snaedis6phmap = new HashMap<String,Double>();
		snaedis6phmap.put( "817_vondugil_jardvegur", 9.0 );
		snaedis6phmap.put( "817_vondugil_vatn", 9.0 );
		snaedis6phmap.put( "818_vondugil_jardvegur", 8.5 );
		snaedis6phmap.put( "818_vondugil_vatn", 8.5 );
		snaedis6phmap.put( "819_vondugil_jardvegur", 8.5 );
		snaedis6phmap.put( "819_vondugil_vatn", 8.5 );
		snaedis6phmap.put( "820_vondugil_jardvegur", 7.5 );
		snaedis6phmap.put( "820_vondugil_vatn", 7.5 );
		snaedis6phmap.put( "821_vondugil_jardvegur", 6.0 );
		snaedis6phmap.put( "821_vondugil_vatn", 6.0 );
		
		snaedis7map = new HashMap<String,String>();
		snaedis7map.put( "846_hurdarbak_jardvegur", "ACGAGTGCGT" );
		snaedis7map.put( "846_hurdarbak_vatn", "ACGCTCGACA" );
		snaedis7map.put( "846_hurdarbak_lifmassi", "AGACGCACTC" );
		snaedis7map.put( "847_hurdarbak_jardvegur", "AGCACTGTAG" );
		snaedis7map.put( "847_hurdarbak_vatn", "ATCAGACACG" );
		snaedis7map.put( "848_kleppjarnsreykir_jardvegur", "ATATCGCGAG" );
		snaedis7map.put( "848_kleppjarnsreykir_vatn", "CGTGTCTCTA" );
		snaedis7map.put( "848_kleppjarnsreykir_lifmassi", "CTCGCGTGTC" );
		snaedis7map.put( "849_kleppjarnsreykir_jardvegur", "TGATACGTCT" );
		snaedis7map.put( "849_kleppjarnsreykir_vatn", "TCTCTATGCG" );
		
		snaedis7colormap = new HashMap<String,String>();
		snaedis7colormap.put( "846_hurdarbak_jardvegur", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "846_hurdarbak_vatn", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "846_hurdarbak_lifmassi", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "847_hurdarbak_jardvegur", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "847_hurdarbak_vatn", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "849_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "849_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		
		snaedis7heatmap = new HashMap<String,Double>();
		snaedis7heatmap.put( "846_hurdarbak_jardvegur", 80.5 );
		snaedis7heatmap.put( "846_hurdarbak_vatn", 80.5 );
		snaedis7heatmap.put( "846_hurdarbak_lifmassi", 80.5 );
		snaedis7heatmap.put( "847_hurdarbak_jardvegur", 71.5 );
		snaedis7heatmap.put( "847_hurdarbak_vatn", 71.5 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_jardvegur", 70.7 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_vatn", 70.7 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_lifmassi", 70.7 );
		snaedis7heatmap.put( "849_kleppjarnsreykir_jardvegur", 76.8 );
		snaedis7heatmap.put( "849_kleppjarnsreykir_vatn", 76.8 );
		
		snaedis7phmap = new HashMap<String,Double>();
		snaedis7phmap.put( "846_hurdarbak_jardvegur", 8.0 );
		snaedis7phmap.put( "846_hurdarbak_vatn", 8.0 );
		snaedis7phmap.put( "846_hurdarbak_lifmassi", 8.0 );
		snaedis7phmap.put( "847_hurdarbak_jardvegur", 8.0 );
		snaedis7phmap.put( "847_hurdarbak_vatn", 8.0 );
		snaedis7phmap.put( "848_kleppjarnsreykir_jardvegur", 8.0 );
		snaedis7phmap.put( "848_kleppjarnsreykir_vatn", 8.8 );
		snaedis7phmap.put( "848_kleppjarnsreykir_lifmassi", 8.0 );
		snaedis7phmap.put( "849_kleppjarnsreykir_jardvegur", 7.5 );
		snaedis7phmap.put( "849_kleppjarnsreykir_vatn", 7.5 );
	
		snaedis8map = new HashMap<String,String>();
		snaedis8map.put( "849_kleppjarnsreykir_lifmassi", "ACGAGTGCGT" );
		snaedis8map.put( "850_kleppjarnsreykir_jardvegur", "ACGCTCGACA" );
		snaedis8map.put( "850_kleppjarnsreykir_vatn", "AGACGCACTC" );
		snaedis8map.put( "850_kleppjarnsreykir_lifmassi", "AGCACTGTAG" );
		snaedis8map.put( "851_deildartunguhver_jardvegur", "ATCAGACACG" );
		snaedis8map.put( "851_deildartunguhver_vatn", "ATATCGCGAG" );
		snaedis8map.put( "852_deildartunguhver_jardvegur", "CGTGTCTCTA" );
		snaedis8map.put( "852_deildartunguhver_vatn", "CTCGCGTGTC" );
		
		snaedis8colormap = new HashMap<String,String>();
		snaedis8colormap.put( "849_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "851_deildartunguhver_jardvegur", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "851_deildartunguhver_vatn", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "852_deildartunguhver_jardvegur", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "852_deildartunguhver_vatn", "0.5\t0.0\t0.5\n" );
		
		snaedis8heatmap = new HashMap<String,Double>();
		snaedis8heatmap.put( "849_kleppjarnsreykir_lifmassi", 76.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_jardvegur", 65.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_vatn", 65.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_lifmassi", 65.8 );
		snaedis8heatmap.put( "851_deildartunguhver_jardvegur", 83.3 );
		snaedis8heatmap.put( "851_deildartunguhver_vatn", 83.3 );
		snaedis8heatmap.put( "852_deildartunguhver_jardvegur", 86.1 );
		snaedis8heatmap.put( "852_deildartunguhver_vatn", 86.1 );
		
		snaedis8phmap = new HashMap<String,Double>();
		snaedis8phmap.put( "849_kleppjarnsreykir_lifmassi", 7.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_jardvegur", 8.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_vatn", 8.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_lifmassi", 8.5 );
		snaedis8phmap.put( "851_deildartunguhver_jardvegur", 8.5 );
		snaedis8phmap.put( "851_deildartunguhver_vatn", 8.5 );
		snaedis8phmap.put( "852_deildartunguhver_jardvegur", 8.5 );
		snaedis8phmap.put( "852_deildartunguhver_vatn", 8.5 );
		
		rsnaedis2map = new HashMap<String,String>();
		for( String key : snaedis2map.keySet() ) {
			rsnaedis2map.put( snaedis2map.get(key), key );
		}
		
		rsnaedis3map = new HashMap<String,String>();
		for( String key : snaedis3map.keySet() ) {
			rsnaedis3map.put( snaedis3map.get(key), key );
		}
		
		rsnaedis4map = new HashMap<String,String>();
		for( String key : snaedis4map.keySet() ) {
			rsnaedis4map.put( snaedis4map.get(key), key );
		}
		
		rsnaedis5map = new HashMap<String,String>();
		for( String key : snaedis5map.keySet() ) {
			rsnaedis5map.put( snaedis5map.get(key), key );
		}
		
		rsnaedis6map = new HashMap<String,String>();
		for( String key : snaedis6map.keySet() ) {
			rsnaedis6map.put( snaedis6map.get(key), key );
		}
		
		rsnaedis7map = new HashMap<String,String>();
		for( String key : snaedis7map.keySet() ) {
			rsnaedis7map.put( snaedis7map.get(key), key );
		}
		
		rsnaedis8map = new HashMap<String,String>();
		for( String key : snaedis8map.keySet() ) {
			rsnaedis8map.put( snaedis8map.get(key), key );
		}
		
		RootPanel		rp = RootPanel.get();
		Style rootstyle = rp.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		final Tree		tree = new Tree();

		tree.addKeyDownHandler( new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				shift = event.getNativeKeyCode() == KeyCodes.KEY_SHIFT;
				ctrl = event.getNativeKeyCode() == KeyCodes.KEY_CTRL;
				alt = event.getNativeKeyCode() == KeyCodes.KEY_ALT;
				
				int cc = event.getNativeEvent().getCharCode();
				zkey = cc == 'z' || cc == 'Z';
				console( "zkey now "+zkey );
				
				if( cc == 'f' || cc == 'F' ) {
					pcaAnalysis();
				}
			}
		});
		tree.addKeyUpHandler( new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				//if( event.getNativeKeyCode() == KeyCodes.KEY_SHIFT ) {
				shift = !(event.getNativeKeyCode() == KeyCodes.KEY_SHIFT);
				ctrl = !(event.getNativeKeyCode() == KeyCodes.KEY_CTRL);
				alt = !(event.getNativeKeyCode() == KeyCodes.KEY_ALT);
				
				int cc = event.getNativeEvent().getCharCode();
				zkey = !(cc == 'z');
				//}
			}
		});
		
		tree.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if( event.getNativeButton() == NativeEvent.BUTTON_MIDDLE ) {
					pcaAnalysis();
				}
			}
		});
		tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selectedtree = event.getSelectedItem();
				String nodename = selectedtree.getText();
				if( ( nodename.contains("snaedis") || nodename.contains("gumol") || nodename.contains("eyjo") || nodename.contains("root") || nodename.equals("arciformis") || nodename.equals("kawarayensis") ) && selectedtree.getChildCount() == 0 ) {
					loadNode( selectedtree, server, mapstr );
				} else if( shift ) {
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
					} else if( rootnodename.contains("snaedis1") ) {
						for( String snaedis : snaedis1map.keySet() ) {
							if( rootnodename.contains(snaedis) ) {
								searchnum = rootnodename.replace(snaedis, snaedis1map.get(snaedis) );
								break;
							}
						}
					} 
					
					if( searchnum.length() == 0 ) {
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
				} else if( ctrl ) {
					StringBuilder res = calcContent( selectedtree, 2, null );
					
					DialogBox db = new DialogBox();
					Caption cap = db.getCaption();
					db.setAutoHideEnabled( true );
					cap.setText("Fasta");
					TextArea ta = new TextArea();
					ta.setSize("512px", "384px");
					db.add( ta );
					ta.setText( res.toString() );
					db.center();
				}
			}
		});
		//tree.setSize("100%", "100%");
		
		//tree.getElement().getStyle().setBorderColor("#aa4444");
		//tree.getElement().getStyle().setBorderWidth( 2.0, Unit.PX );
		
		greetingService.getRemoteAddress( new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String result) {
				//if( result != null && result.contains("130.208.252.") ) 
				runStuff( server, tree, rmapstr );
			}
		});
		FocusPanel	focus = new FocusPanel( tree );
		//focus.setSize("100%", "100%");
		
		//focus.getElement().getStyle().setBorderColor( "#444444" );
		//focus.getElement().getStyle().setBorderWidth(2.0, Unit.PX);
		focus.getElement().getStyle().setBackgroundColor("#ddddff");
		
		focus.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {}
		});
		focus.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {}
		});
		focus.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {}
		});
		focus.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {}
		});
		focus.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {}
		});
		focus.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {}
		});
		focus.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				String str = event.getData("text/plain");
				//stuff( str, rootitem5 );
			}
		});
		
		HorizontalPanel	hp = new HorizontalPanel();
		CheckBox check = new CheckBox( "98% identity threshold" );
		check.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				zkey = event.getValue();
			}
		});
		Button pcabutton = new Button( "PCA" );
		pcabutton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pcaAnalysis();
			}
		});
		Button calcbutton = new Button( "Content Calc" );
		calcbutton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for( int i = 0; i < tree.getItemCount(); i++ ) {
					TreeItem ti = tree.getItem(i);
					//console( "hey "+i );
					if( ti.getChildCount() > 0 ) {
						console( "calculating "+ti.getText() );
						calcContent( ti, 7, "Thermus" );
					}
				}
			}
		});
		Button snaedisbutton = new Button( "Snaedis" );
		snaedisbutton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for( int i = 0; i < tree.getItemCount(); i++ ) {
					TreeItem ti = tree.getItem(i);
					//console( "hey "+i );
					if( ti.getText().startsWith("snaedis") ) {
						loadNode( ti, server, null );
					}
				}
			}
		});
		hp.add( check );
		hp.add( pcabutton );
		hp.add( calcbutton );
		hp.add( snaedisbutton );
		
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("100%", "100%");
		vp.add( hp );
		vp.add( focus );
		
		rp.add( vp );
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
