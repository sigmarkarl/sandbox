package org.simmi.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.html.Console;

public class Webnutrition implements EntryPoint {

	Console console;
	
	int sortcolumn = 0;
	public class FoodInfo implements Comparable<FoodInfo> {
		Object[]	columns;
		
		public FoodInfo( String name, String group ) {
			columns = new Object[ lcolumnwidth.size() ];
			columns[0] = name;
			columns[1] = group;
		}
		
		public Object getSortObject() {
			return columns[sortcolumn];
		}
		
		public Object valAt( int i ) {
			if( i < columns.length ) return columns[i];
			return null;
		}
		
		public int getLength() {
			return columns.length;
		}

		@Override
		public int compareTo(FoodInfo o) {
			Object obj = columns[ sortcolumn ];
			if( obj instanceof String ) {
				return ((String)obj).compareTo( (String)o.getSortObject() );
			} else if( obj instanceof Double ) {
				return ((Double)obj).compareTo( (Double)o.getSortObject() );
			}
			return 0;
		}
	};
	
	public class Column {
		public Column( String name, int width, String id ) {
			this.name = name;
			this.width = width;
			this.id = id;
		}
		
		String 	name;
		int		width;
		String 	id;
	};
	
	Map<String,Column> nutrmap = new HashMap<String,Column>();
	Map<String,FoodInfo> foodmap = new HashMap<String,FoodInfo>();
	List<FoodInfo>	lfoodinfo = new ArrayList<FoodInfo>();
	List<Column>	lcolumnwidth = new ArrayList<Column>();
	
	public native String bunzip2( String bintext ) /*-{
		return $wnd.ArchUtils.bz2.decode( bintext );
	}-*/;
	
	public native String getBinaryResourceOld(String url) /*-{
	    // ...implemented with JavaScript                 
	    var req = new XMLHttpRequest();
	    req.open("GET", url, false);  // The last parameter determines whether the request is asynchronous -> this case is sync.
	    req.overrideMimeType('text/plain; charset=x-user-defined');
	    req.send(null);
	    if (req.status == 200) {                    
	        return req.responseText;
	    } else return null;
	}-*/;
	
	public native String getBinaryResource( String url ) /*-{
		var ths = this;
		
		var oReq = new XMLHttpRequest();
		oReq.open("GET", url, true);
		oReq.responseType = "arraybuffer";
		 
		oReq.onload = function (oEvent) {
		  var arrayBuffer = oReq.response; // Note: not oReq.responseText
		  if (arrayBuffer) {
		  	var view = new Uint8Array( arrayBuffer );
		  	//var blob = new Blob([view], {type: "application/x-bzip2"});
		  	var blob = new Blob([view], {type: "text/plan"});
		  	var reader = new FileReader();
		  	reader.onload = function( ev ) {
		  		$wnd.console.log( "about to unzip" );
		  		var unzip = ev.target.result; //$wnd.ArchUtils.bz2.decode( ev.target.result );
		  		$wnd.console.log( "done unzipping" );
		  		ths.@org.simmi.client.Webnutrition::fetchNutrFromText(Ljava/lang/String;)( unzip );
		  		//( $wnd.ArchUtils.bz2.decode( ev.target.result ) );
		  	};
		  	reader.readAsBinaryString( blob );
		    //var byteArray = new Uint8Array(arrayBuffer);
		    //for (var i = 0; i < byteArray.byteLength; i++) {
		    // do something with each byte in the array
		    //}
		  }
		};
	 
		oReq.send(null);
	}-*/;

	
	public void fetchNutr() {
		getBinaryResource( "http://127.0.0.1:8888/NUT_DATA_trim.txt" );
	}
	
	public void fetchNutrRequest() throws RequestException {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "http://127.0.0.1:8888/NUT_DATA.txt.bz2.base64" );
		//rb.getHeader(header)
		rb.sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String bintext = response.getText();
				//ArrayBufferNative ab = new ArrayBufferImpl(length);
				console.log( bintext.length() );
			}
			
			@Override
			public void onError(Request request, Throwable exception) {}
		});
	}
	
	public void fetchNutrResource() {
		//DataResource dr = MyResources.INSTANCE.nut_bzip2();
		//dr.getSafeUri().
	}
	
	public void fetchNutrFromText( String text ) {
		console.log( text.length() + " " + text.substring( 0,100 ) );
		
		int s = 0;
		int i = text.indexOf( '\n' );
		while( i != -1 ) {
			/*int t1 = text.indexOf('^', s);
			String foodShort = text.substring(s+1, t1-1);
			int t2 = text.indexOf('^', t1+1);
			String nutrShort = text.substring( t1+2, t2-1);
			int t3 = text.indexOf('^', t2+1);
			double val = Double.parseDouble( text.substring(t2+1,t3) );*/
			
			String foodShort = text.substring(s, s+5);
			String nutrShort = text.substring(s+5, s+8);
			double val = Double.parseDouble( text.substring(s+8,i) );
			
			if( foodmap.containsKey( foodShort ) ) {
				FoodInfo fi = foodmap.get( foodShort );
				
				int ind = lcolumnwidth.indexOf( nutrmap.get( nutrShort ) );
				fi.columns[ind] = val;
			} else {
				console.log( foodShort );
				for( String key : foodmap.keySet() ) {
					console.log("uff " + key);
					break;
				}
				if( i > 1000 ) break;
			}
			
			s = i+1;
			i = text.indexOf( '\n', s );
			//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
		}
		console.log( "done" );
	
		draw( canvas.getContext2d(), xstart, ystart );
	}
	
	public void fetchNutrFusion() throws RequestException {
		String url = "https://www.googleapis.com/fusiontables/v1/";
		
		String appkey = "key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
		//String sql = "SELECT FoodId,NutrId,Value FROM 1NXpzVjOWmM9AXPOb173Z7fZmGrpUlISH3P6DBdo where NutrId='~268~'";
		String sql = "SELECT FoodId,NutrId,Value FROM 1NXpzVjOWmM9AXPOb173Z7fZmGrpUlISH3P6DBdo";
		String sqlkey = "sql="+URL.encode( sql );
		String requestData = "query?"+sqlkey+"&"+appkey;
		url += requestData;
		console.log( "about to "+requestData );
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, url );
		rb.sendRequest( "", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String jsonString = response.getText();
				JSONValue jsval = JSONParser.parseLenient( jsonString );
				JSONObject jsobj = jsval.isObject();
				if( jsobj != null ) {
					JSONArray rows = (JSONArray)jsobj.get("rows");
					//boolean done = false;
					for( int i = 0; i < rows.size(); i++ ) {
						JSONArray row = (JSONArray)rows.get(i);
						JSONString foodid = (JSONString)row.get(0);
						JSONString nutrid = (JSONString)row.get(1);
						JSONString value = (JSONString)row.get(2);
						String foodidstr = foodid.stringValue();
						String nutridstr = nutrid.stringValue();
						double val = Double.parseDouble( value.stringValue() );
						
						String foodShort = foodidstr.substring(1, foodidstr.length()-1);
						String nutrShort = nutridstr.substring(1, nutridstr.length()-1);
						if( foodmap.containsKey( foodShort ) ) {
							FoodInfo fi = foodmap.get( foodShort );
							
							int ind = lcolumnwidth.indexOf( nutrmap.get( nutrShort ) );
							fi.columns[ind] = val;
						} else {
							for( String key : foodmap.keySet() ) {
								console.log("uff" + key);
								break;
							}
						}
						//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
					}
					
					draw( canvas.getContext2d(), xstart, ystart );
				}
				//console( response.getText() );
			}
			
			@Override
			public void onError(Request request, Throwable exception) { console.log("uhh"); }
		});
	}
	
	public void fetchNutrDef() throws RequestException {
		String url = "https://www.googleapis.com/fusiontables/v1/";
		
		String appkey = "key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
		String sql = "SELECT Id,Unit,Name FROM 129hFkqxrJnhaRTPDS1COEGs5d2q0dBasWg9gOJM";
		String sqlkey = "sql="+URL.encode( sql );
		String requestData = "query?"+sqlkey+"&"+appkey;
		url += requestData;
		console.log( "about to "+requestData );
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, url );
		rb.sendRequest( "", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String jsonString = response.getText();
				JSONValue jsval = JSONParser.parseLenient( jsonString );
				JSONObject jsobj = jsval.isObject();
				if( jsobj != null ) {
					JSONArray rows = (JSONArray)jsobj.get("rows");
					//boolean done = false;
					for( int i = 0; i < rows.size(); i++ ) {
						JSONArray  row = (JSONArray)rows.get(i);
						JSONString id = (JSONString)row.get(0);
						JSONString unit = (JSONString)row.get(1);
						JSONString name = (JSONString)row.get(2);
						String idstr = id.stringValue();
						String unitstr = unit.stringValue();
						String namestr = name.stringValue();
						//double val = Double.parseDouble( value.stringValue() );
						
						String idShort = idstr.substring(1, idstr.length()-1);
						String unitShort = unitstr.substring(1, unitstr.length()-1);
						String nameShort = namestr.substring(1, namestr.length()-1);
						
						Column col = new Column( nameShort, 100, idShort);
						lcolumnwidth.add( col );
						nutrmap.put( idShort, col );
					}
				}
				
				try {
					fetchFood();
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) { console.log("uhh"); }
		});
	}
	
	public void fetchFood() throws RequestException {
		String url = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20Id,GroupId,Name%20FROM%2011kJvZY3UCjtqcA0gN8WRZwtTVGXJ_MxAto7cdUU&key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
		//String url = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20Id,GroupId,Name%20FROM%201H84iyd1430-Nxzd1o84rFzZQN3dXi-BP7MsNuCE&key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
		                                                                                                   //1ysVkwxLAO7U4F-ULp58q4P5DqcD70V_MpiKuJ4U
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, url );
		rb.sendRequest( "", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String jsonString = response.getText();
				//console.log( jsonString );
				JSONValue jsval = JSONParser.parseLenient( jsonString );
				JSONObject jsobj = jsval.isObject();
				if( jsobj != null ) {
					JSONArray rows = (JSONArray)jsobj.get("rows");
					boolean done = false;
					for( int i = 0; i < rows.size(); i++ ) {
						JSONArray row = (JSONArray)rows.get(i);
						JSONString id = (JSONString)row.get(0);
						JSONString groupid = (JSONString)row.get(1);
						JSONString name = (JSONString)row.get(2);
						String idstr = id.stringValue();
						String groupidstr = groupid.stringValue();
						String namestr = name.stringValue();
						
						if( namestr.length() > 0 && groupidstr.length() > 0 ) {
							FoodInfo fi = new FoodInfo( namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ) );
							foodmap.put( idstr.substring(1, idstr.length()-1), fi );
							lfoodinfo.add( fi );
						} else if( !done ) {
							done = true;
							console.log( jsonString );
						}
						//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
					}
					/*lcolumnwidth.add( new Column("Energy (kJ)", 100) );
					lcolumnwidth.add( new Column("Protein (g)", 100) );
					lcolumnwidth.add( new Column("Carbohydrates (g)", 100) );
					lcolumnwidth.add( new Column("Starch (g)", 100) );
					lcolumnwidth.add( new Column("Fat (g)", 100) );
					lcolumnwidth.add( new Column("Alcohol (g)", 100) );*/
					
					//nutrmap.put( "268", lcolumnwidth.get(2) );
					
					//fetchNutr2();
					fetchNutr();
					
					draw( canvas.getContext2d(), xstart, ystart );
				}
				//console( response.getText() );
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				
			}
		});
	}
	
	public int getFoodNumber() {
		return filtInd.size() == 0 ? lfoodinfo.size() : filtInd.size();
	}
	
	public FoodInfo getFoodInfo( int i ) {
		int k = i;
		if( filtInd.size() > 0 ) k = filtInd.get(i);
		return lfoodinfo.get(k);
	}
	
	public void drawSection( Context2d context, int xstartLocal, int ystartLocal, int xloc, int yloc, int canvasWidth, int canvasHeight ) {
		Set<Integer>	selset = new HashSet<Integer>();
		
		//console.log( xstartLocal + "  " + ystartLocal );
		//int xs = Math.max( 0, ((xstartLocal+xloc)/unitwidth)*unitwidth );
		int ys = Math.max( 0, ((ystartLocal+yloc)/unitheight)*unitheight );
		//int xe = ((xstartLocal+xloc+canvasWidth)/unitwidth+1)*unitwidth;
		int ye = Math.min( ((ystartLocal+yloc+canvasHeight)/unitheight+1)*unitheight, getFoodNumber()*unitheight );
		
		//context.fillRect( xs-xstartLocal, ys-ystartLocal+unitheight, xe-xs, ye-ys );
		context.fillRect( 0, ys-ystartLocal+columnHeight, canvas.getCoordinateSpaceWidth()-scrollBarWidth, ye-ys );
		context.setFillStyle("#DDDDFF");
		for( int y = ys; y < ye; y+=unitheight ) {
			int i = y/unitheight;
			if( i % 2 == 0 ) {
				int yy = i*unitheight;
				context.fillRect( 0, yy+columnHeight-ystartLocal, canvas.getCoordinateSpaceWidth()-scrollBarWidth, unitheight );
			}
		}
		
		int w = 0;
		int k = 0;
		context.setFillStyle("#222222");
		for( Column c : lcolumnwidth ) {
			if( w+c.width > xstartLocal ) {
				//int x = xs; x < Math.min( getMax(), xe ); x+=unitwidth 
				
				//int k = x/unitwidth;
				//int xx = k*unitwidth;
				
				context.save();
				context.beginPath();
				context.rect(w-xstartLocal, 0, c.width, canvas.getCoordinateSpaceHeight());
				context.clip();
				for( int y = ys; y < ye; y+=unitheight ) {
					int i = y/unitheight;
					int yy = i*unitheight;
					FoodInfo fi = getFoodInfo(i);
					//int[]	ann = seq.getAnnotationIndex();
						
					if( selset.contains(i) ) {
						context.setFillStyle("#DDDDFF");
						context.fillRect(0, ystartLocal, canvasWidth, unitheight );
						context.setFillStyle("#222222");
					}
					
					Object o = fi.valAt(k);
					if( o != null ) context.fillText(o.toString(), w+5-xstartLocal, yy+columnHeight+unitheight-3.0-ystartLocal );
				}
				context.restore();
			}
			w += c.width;
			k++;
		}
	}
	
	public void draw( Context2d context, int xstartLocal, int ystartLocal ) {
		if( lfoodinfo != null ) {			
			context.setFillStyle("#FFFFFF");
			
			int cw = canvas.getCoordinateSpaceWidth();
			int ch = canvas.getCoordinateSpaceHeight();//-columnHeight-scrollBarHeight;
			
			int rw = cw-scrollBarWidth;
			int rh = ch-scrollBarHeight-columnHeight;
			
			context.clearRect(0.0, 0.0, cw, ch);
			//int tcw = tcanvas.getCoordinateSpaceWidth();
			//int tch = tcanvas.getCoordinateSpaceHeight();
			
			int ax = Math.abs(xstartLocal-prevx);
			int ay = Math.abs(ystartLocal-prevy);
			
			if( false && ay < ch/2 && ax < cw/2 ) {
				int h = ch-ay;
				int w = cw-ax;
				int xuno = Math.max(0,xstartLocal-prevx);
				int xduo = Math.max(0,prevx-xstartLocal);
				int yuno = Math.max(0,ystartLocal-prevy);
				int yduo = Math.max(0,prevy-ystartLocal);
				context.drawImage(context.getCanvas(), xuno, yuno+unitheight, w, h, xduo, yduo+unitheight, w, h);
				//tcontext.drawImage(tcontext.getCanvas(), 0, yuno+unitheight, tcw, h, 0, yduo+unitheight, tcw, h);
				if( xuno > xduo ) {
					if( yuno > yduo ) {
						drawSection( context, xstartLocal, ystartLocal, 0, h, w, ay );
						drawSection( context, xstartLocal, ystartLocal, w, 0, ax, h );
						drawSection( context, xstartLocal, ystartLocal, w, h, ax, ay ); //, "#0000ff" );
					} else {
						drawSection( context, xstartLocal, ystartLocal, 0, 0, w, ay );
						drawSection( context, xstartLocal, ystartLocal, w, ay, ax, h );
						drawSection( context, xstartLocal, ystartLocal, w, 0, ax, ay );
					}
				} else {
					if( yuno > yduo ) {
						drawSection( context, xstartLocal, ystartLocal, 0, 0, ax, h );
						drawSection( context, xstartLocal, ystartLocal, ax, h, w, ay );
						drawSection( context, xstartLocal, ystartLocal, 0, h, ax, ay );
					} else {
						drawSection( context, xstartLocal, ystartLocal, ax, 0, w, ay );
						drawSection( context, xstartLocal, ystartLocal, 0, ay, ax, h );
						drawSection( context, xstartLocal, ystartLocal, 0, 0, ax, ay );
					}
				}
			} else {
				drawSection( context, xstartLocal, ystartLocal, 0, 0, rw, rh );
			}
			
			context.setFillStyle("#EEEEEE");
			context.fillRect(0, 0, rw, columnHeight);
			context.setFillStyle("#111111");
			/*for( int x = xstartLocal; x < xstartLocal+cw; x+=unitwidth ) {
				int val = 3;
				if( (x/unitwidth)%unitwidth == 0 ) val = 7;
				else if( (x/10)%10 == 5 ) val = 5;
				context.fillRect( (x-xstartLocal), unitheight-val, 1, val );
			}
			for( int x = xstartLocal; x < xstartLocal+cw; x+=100 ) {
				context.fillText( ""+(x/unitwidth), (x-xstartLocal), unitheight-7 );
			}*/
			
			int w = 0;
			for( Column c : lcolumnwidth ) {
				context.fillRect( w-xstartLocal, 0, 1, columnHeight );
				context.fillText( c.name, (w+5-xstartLocal), columnHeight-7 );
				w += c.width;
			}
			
			context.setFillStyle("#EEEEEE");
			context.fillRect(rw, columnHeight, scrollBarWidth, rh);
			context.fillRect(0, rh+columnHeight, rw, scrollBarHeight);
			context.setFillStyle("#888888");
			context.fillRect(rw, 0, scrollBarWidth, columnHeight);
			context.fillRect(rw, rh+columnHeight, scrollBarWidth, scrollBarHeight);
			context.setFillStyle("#333333");
			int max = getMax();
			if( getFoodNumber() > 0 && max > 0 ) {
				context.fillRect( rw, columnHeight+(rh*ystartLocal)/(getFoodNumber()*unitheight-rh)-3.0, scrollBarWidth, 6.0 );
				context.fillRect( (rw*xstartLocal)/(max-rw)-3.0, rh+columnHeight, 6.0, scrollBarHeight );
			}
			
			prevx = xstartLocal;
			prevy = ystartLocal;
		}
	}
	
	Canvas canvas = null;
	final Map<String,String>	groupIdMap = new HashMap<String,String>();
	boolean	mousedown = false;
	double	mousex = 0.0;
	double	mousey = 0.0;
	boolean					scrollx = false;
	boolean					scrolly = false;
	
	int columnHeight = 20;
	int scrollBarWidth = 20;
	int scrollBarHeight = 20;
	
	int	unitheight = 20;
	//int	unitwidth = 200;
	int prevx = 0;
	int prevy = 0;
	int xstart = 0;
	int ystart = 0;
	
	public int getMax() {
		int w = 0;
		for( Column c : lcolumnwidth ) {
			w += c.width;
		}
		return w;
	}
	
	final ListBox	filterCombo = new ListBox();
	List<Integer>	filtInd = new ArrayList<Integer>();
	
	public int getVisibleHeight() {
		return canvas.getCoordinateSpaceHeight()-(columnHeight+scrollBarHeight);
	}
	
	@Override
	public void onModuleLoad() {
		elemental.html.Window wnd = Browser.getWindow();
		console = wnd.getConsole();
		console.log("starting");
		
		RootPanel	rp = RootPanel.get();
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		vp.setSize(w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				vp.setSize(w+"px", h+"px");
			}
		});
		
		canvas = Canvas.createIfSupported();
		canvas.setSize("1024px", "600px");
		canvas.setCoordinateSpaceWidth( 1024 );
		canvas.setCoordinateSpaceHeight( 600 );
		
		lcolumnwidth.add( new Column("Food", 300, "0") );
		lcolumnwidth.add( new Column("Group", 300, "0") );
		
		String groupurl = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20*%20FROM%201ysVkwxLAO7U4F-ULp58q4P5DqcD70V_MpiKuJ4U&key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";		
		try {
			RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, groupurl );
			rb.sendRequest( "", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String jsonString = response.getText();
					JSONValue jsval = JSONParser.parseLenient( jsonString );
					JSONObject jsobj = jsval.isObject();
					if( jsobj != null ) {
						JSONArray rows = (JSONArray)jsobj.get("rows");
						for( int i = 0; i < rows.size(); i++ ) {
							JSONArray row = (JSONArray)rows.get(i);
							JSONString id = (JSONString)row.get(0);
							JSONString name = (JSONString)row.get(1);
							String idstr = id.stringValue();
							String namestr = name.stringValue();
							groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
						}
						
						filterCombo.addItem("");
						for( String group : groupIdMap.keySet() ) {
							filterCombo.addItem( groupIdMap.get(group) );
						}
						
						try {
							fetchNutrDef();
						} catch (RequestException e) {
							e.printStackTrace();
						}
					}
					//console( response.getText() );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		canvas.addMouseWheelHandler( new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				if( event.isShiftKeyDown() ) {
					
				} else {
					ystart = Math.max( 0, Math.min( getFoodNumber()*unitheight - getVisibleHeight(), ystart + event.getDeltaY()*unitheight ) );
				}
				//console.log( xstart + "  " + ystart );
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousedown = true;
				mousex = event.getX();
				mousey = event.getY();
				
				int cw = canvas.getCoordinateSpaceWidth();
				int ch = canvas.getCoordinateSpaceHeight();
				
				if( mousey < columnHeight ) {
					int i = 0;
					int w = lcolumnwidth.get(i).width;
					while( w < mousex-xstart ) {
						i++;
						if( i == lcolumnwidth.size() ) {
							i = -1;
							break;
						}
						w += lcolumnwidth.get(i).width;
					}
					
					console.log("ok "+i);
					if( i != -1 ) {
						sortcolumn = i;	
						Collections.sort( lfoodinfo );
					}
				} else if( mousex > cw-scrollBarWidth || mousey > ch-scrollBarHeight ) {
					//int xstart = Webfasta.this.xstart;
					if( mousey > ch-scrollBarHeight ) {
						scrollx = true;
						
						int max = getMax();
						double xmin1 = max-(cw-scrollBarWidth);
						double xmin2 = (xmin1*mousex)/(cw-scrollBarWidth);
						xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
					}
					
					//int ystart = Webfasta.this.ystart;
					if( mousex > cw-scrollBarWidth ) {
						scrolly = true;
						
						double ymin1 = getFoodNumber()*unitheight-(ch-columnHeight-scrollBarHeight);
						double ymin2 = (ymin1*(mousey-columnHeight))/(ch-columnHeight-scrollBarHeight);
						ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
					}
				}
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		canvas.addMouseOutHandler( new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				mousedown = false;
				scrollx = false;
				scrolly = false;
			}
		});
		canvas.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mousedown = false;
				scrollx = false;
				scrolly = false;
			}
		});
		canvas.addMouseMoveHandler( new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if( mousedown ) {
					int x = event.getX();
					int y = event.getY();
					
					double cw = (double)canvas.getCoordinateSpaceWidth();
					double ch = (double)canvas.getCoordinateSpaceHeight();
					if( scrollx || scrolly ) {
						if( scrollx ) {
							int max = getMax();
							double xmin1 = max-(cw-scrollBarWidth);
							double xmin2 = (xmin1*x)/(cw-scrollBarWidth);
							xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
						}
						
						if( scrolly ) {
							double ymin1 = getFoodNumber()*unitheight-(ch-columnHeight-scrollBarHeight);
							double ymin2 = (ymin1*(y-unitheight))/(ch-columnHeight-scrollBarHeight);
							ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
						}
					} else {
						int max = getMax();
						double xmin1 = max-cw;
						double xmin2 = xstart + (mousex-x);
						int xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
						
						if( xmin2 > xmin1 ) {
							mousex = mousex+(int)(xmin1-xmin2);
						}
						
						double ymin1 = getFoodNumber()*unitheight-(ch-columnHeight-scrollBarHeight);
						double ymin2 = ystart + (mousey-y);
						int ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
						
						if( ymin2 > ymin1 ) {
							mousey = mousey+(int)(ymin1-ymin2);
						}
					}
					draw( canvas.getContext2d(), xstart, ystart );
				}
			}
		});
		vp.add( canvas );
		
		HorizontalPanel	filterPanel = new HorizontalPanel();
		
		filterCombo.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String val = filterCombo.getValue( filterCombo.getSelectedIndex() );
				filtInd.clear();
				
				if( val != null && val.length() > 0 ) {
					int i = 0;
					for( FoodInfo fi : lfoodinfo ) {
						String group = (String)fi.valAt(1);
						if( group != null && group.toLowerCase().contains( val.toLowerCase() ) ) filtInd.add(i);
						//else console.log( fi.valAt(0) );
						i++;
					}
				} else console.log( filterCombo.getSelectedIndex() );
				
				ystart = 0;
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		final TextBox	filter = new TextBox();
		filter.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String val = filter.getValue();
				filtInd.clear();
				
				if( val.length() > 0 ) {
					int i = 0;
					for( FoodInfo fi : lfoodinfo ) {
						String food = (String)fi.valAt(0);
						if( food.toLowerCase().contains( val.toLowerCase() ) ) filtInd.add(i);
						i++;
					}
				}
				
				ystart = 0;
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		filterPanel.add( filterCombo );
		filterPanel.add( filter );
		vp.add( filterPanel );
		
		rp.add( vp );
	}
}
