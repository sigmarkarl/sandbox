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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
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
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.html.Console;

public class Webnutrition implements EntryPoint {
	Console console;
	static Set<Integer>	selset = new HashSet<Integer>();
	static int sortcolumn = 0;
	
	public interface FoodInfo extends Comparable<FoodInfo> {
		public void setSelected( boolean sel, int ind );		
		public boolean isSelected();
		public float getSortFloat();
		public float floatValAt( int i );
		public int getLength();
		@Override
		public int compareTo(FoodInfo o);
		public float getColumn( int i );
		public void setColumn( int i, float c );
		public String getId();
		public String getName();
		public String getGroup();
	}
	
	public static final class AndroidFoodInfo extends JavaScriptObject implements FoodInfo {
		protected AndroidFoodInfo() {}
		
		public native void setSelected( boolean sel, int ind ) /*-{}-*/;
		public native boolean isSelected() /*-{ return this.isSelected(); }-*/;
		public native float getSortFloat() /*-{ return this.getSortObject(); }-*/;
		public native float floatValAt( int i ) /*-{ return this.doubleValAt( i ); }-*/;
		public native int getLength() /*-{ return this.getLength(); }-*/;
		@Override
		public native int compareTo(FoodInfo o) /*-{ return this.compareTo( o ); }-*/;
		public native float getColumn( int i ) /*-{ return this.getColumn( i ); }-*/;
		public native void setColumn( int i, float c ) /*-{ this.setColumn( i, c ); }-*/;
		public native String getId() /*-{ return this.getId(); }-*/;
		public native String getName() /*-{ return this.getName(); }-*/;
		public native String getGroup() /*-{ return this.getGroup(); }-*/;
	}
	
	public static class WebFoodInfo implements FoodInfo {
		String				name;
		String				group;
		Float32ArrayNative	columns;
		boolean				selected = false;
		String				id;
		
		public WebFoodInfo( String id, String name, String group ) {
			this.columns = Float32ArrayNative.create( lcolumnwidth.size()-2 ); //new Object[ lcolumnwidth.size() ];
			for( int i = 0; i < this.columns.length(); i++ ) {
				this.columns.set(i, -1.0f);
			}
			this.name = name;
			this.group = group;
			
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getGroup() {
			return group;
		}
		
		public float getColumn( int i ) {
			return columns.get(i);
		}
		
		public void setColumn( int i, float o ) {
			columns.set(i, o);
		}
		
		public void setSelected( boolean sel, int ind ) {
			selected = sel;
			if( sel ) selset.add( ind );
			else selset.remove( ind );
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		public float getSortFloat() {
			//if( sortcolumn == 0 ) return getName();
			//else if( sortcolumn == 1 ) return getGroup();
			return columns.get(sortcolumn-2);
		}
		
		public float floatValAt( int i ) {
			float ret = -1.0f;
			if( i < columns.length() ) ret = columns.get(i);
			return ret;
		}
		
		public int getLength() {
			return columns.length();
		}

		@Override
		public int compareTo(FoodInfo o) {
			if( sortcolumn == 0 ) {
				return getName().compareTo(o.getName());
			} else if( sortcolumn == 1 ) {
				return getGroup().compareTo(o.getGroup());
			}
			return Float.compare( this.floatValAt(sortcolumn-2), o.floatValAt(sortcolumn-2) );
			//Object obj = columns[ sortcolumn ];
			//Object sobj = o == null ? null : o.getSortObject();
			
			/*if( obj != null && sobj != null ) {
				if( obj instanceof String ) {
					return ((String)obj).compareTo( (String)sobj );
				} else if( obj instanceof Double ) {
					return ((Double)sobj).compareTo( (Double)obj );
				}
			} else if( obj == null && sobj != null ) {
				return 1;
			} else if( obj != null && sobj == null ) {
				return -1;
			}*/
					
			//return 0;
		}
	};
	
	public interface Column {
		public int getWidth();
		public String getId();
		public String getName();
		public String getUnit();
	}
	
	public static final class AndroidColumn extends JavaScriptObject implements Column {
		protected AndroidColumn() {};
		
		public native int getWidth() /*-{
			return this.getWidth();
		}-*/;
		
		public native String getId() /*-{
			return this.getId();
		}-*/;
		
		public native String getName() /*-{
			return this.getName();
		}-*/;
		
		public native String getUnit() /*-{
			return this.getUnit();
		}-*/;
	}
	
	public class WebColumn implements Column {
		public WebColumn( String name, String unit, int width, String id ) {
			this.name = name;
			this.unit = unit;
			this.width = width;
			this.id = id;
		}
		
		String 	name;
		String	unit;
		int		width;
		String 	id;
		
		public int getWidth() {
			return width;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getUnit() {
			return unit;
		}
	};
	
	Map<String,Column> nutrmap = new HashMap<String,Column>();
	Map<String,FoodInfo> foodmap = new HashMap<String,FoodInfo>();
	List<FoodInfo>	lfoodinfo = new ArrayList<FoodInfo>();
	static List<Column>	lcolumnwidth = new ArrayList<Column>();
	
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
	
	public native NutData getNutData() /*-{
		return $wnd.nutdata;
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
		  	
		  	$wnd.console.log( "about to unzip" );
	  		$wnd.zip = new $wnd.Zlib.Unzip( view );
	  		var filenames = $wnd.zip.getFilenames();
		    for( var fkey in filenames ) {
		    	var filename = filenames[fkey];
				var array = $wnd.zip.decompress( filename );
				var text = new Uint8Array( array );
				var blob = new Blob([text], {type: "text/plain"});
			  	var reader = new FileReader();
			  	reader.onload = function( ev ) {
					ths.@org.simmi.client.Webnutrition::fetchNutrFromText(Ljava/lang/String;Z)( ev.target.result, false );
		  		};
		  		reader.readAsText( blob );
				break;
		    }
	  		//$wnd.console.log( "done unzipping" );
		  	
		  	//var blob = new Blob([view], {type: "application/x-bzip2"});
		  	//reader.readAsBinaryString( blob );
		    //var byteArray = new Uint8Array(arrayBuffer);
		    //for (var i = 0; i < byteArray.byteLength; i++) {
		    // do something with each byte in the array
		    //}
		  }
		};
		oReq.send(null);
	}-*/;

	
	public void fetchNutr() {
		getBinaryResource( "NUT_DATA_trim.zip" );
		//getBinaryResource( "http://192.168.1.166:8888/NUT_DATA_trim.txt" );
	}
	
	public void fetchNutrRequest() throws RequestException {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "http://nutritiondb.appspot.com/NUT_DATA.txt.bz2.base64" );
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

	public void fetchNutrFromText( String text, boolean wherefrom ) {
		console.log( text.length() + " " + text.substring( 0,100 ) );
		
		/*if( wherefrom ) {
			RootPanel emptypanel = RootPanel.get("emptyspace");
			emptypanel.add( new HTML("whatafuck") );
		}*/
		
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
			float val = Float.parseFloat( text.substring(s+8,i) );
			
			if( foodmap.containsKey( foodShort ) ) {
				FoodInfo fi = foodmap.get( foodShort );
				
				int ind = lcolumnwidth.indexOf( nutrmap.get( nutrShort ) );
				fi.setColumn( ind-2, val );
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
						float val = Float.parseFloat( value.stringValue() );
						
						String foodShort = foodidstr.substring(1, foodidstr.length()-1);
						String nutrShort = nutridstr.substring(1, nutridstr.length()-1);
						if( foodmap.containsKey( foodShort ) ) {
							FoodInfo fi = foodmap.get( foodShort );
							
							int ind = lcolumnwidth.indexOf( nutrmap.get( nutrShort ) );
							fi.setColumn( ind-2, val );
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
						
						Column col = new WebColumn( nameShort, unitShort, 75, idShort);
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
							FoodInfo fi = new WebFoodInfo( idstr, namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ) );
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
			public void onError(Request request, Throwable exception) {}
		});
	}
	
	public int getFoodNumber() {
		return filtInd.size() == 0 ? lfoodinfo.size() : filtInd.size();
	}
	
	public static int getRealIndex( int i ) {
		int k = i;
		if( filtInd.size() > 0 ) {
			if( i < filtInd.size() ) k = filtInd.get(i);
			else k = -1;
		}
		return k;
	}
	
	public FoodInfo getFoodInfo( int i ) {
		int k = getRealIndex( i );
		return k != -1 ? lfoodinfo.get(k) : null;
	}
	
	public void drawSection( Context2d context, int xstartLocal, int ystartLocal, int xloc, int yloc, int canvasWidth, int canvasHeight ) {		
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
		
		context.setFillStyle("#222222");
		if( nutdata != null ) subdrawandroid( context, ys, ye, xstartLocal, ystartLocal, canvasWidth, canvasHeight );
		else subdraw( context, ys, ye, xstartLocal, ystartLocal, canvasWidth, canvasHeight );
	}
	
	public static final class NutData extends JavaScriptObject {
		protected NutData() {};
		
		public native int getColumnCount() /*-{
			return this.getColumnCount();
		}-*/;
		
		public native int getGroupCount() /*-{
			return this.getGroupCount();
		}-*/;
		
		public native int getFoodInfoCount() /*-{
			return this.getFoodInfoCount();
		}-*/;
		
		public native String getGroup( int i ) /*-{
		 	return this.getGroup( i );
		}-*/;
		
		public native Column getColumn( int i ) /*-{
			 return this.getColumn( i );
		}-*/;
		
		public FoodInfo getTheFoodInfo( int i ) {
			int k = getRealIndex( i );
			return k != -1 ? getFoodInfo(k) : null;
		}
		
		private native FoodInfo getFoodInfo( int i ) /*-{
			return this.getFoodInfo( i );
		}-*/;
	}
	NutData nutdata = null;
	
	HTML	html = new HTML("bleheheheheh");
	HTML	html2 = new HTML("blehehehehehu");
	HTML	html3 = new HTML("blehehehehehu");
	public void subdrawandroid( Context2d context, int ys, int ye, int xstartLocal, int ystartLocal, int canvasWidth, int canvasHeight ) {
		int u = contentrp.getWidgetIndex( html3 );
		if( u == -1 ) contentrp.add( html3 );
		
		double rhs = getRowHeaderSize();
		int w = 0;
		for( int k = 0; k < nutdata.getColumnCount(); k++ ) {
			int cwidth = nutdata.getColumn( k ).getWidth();
			double sub = k > 1 ? xstartLocal : 0;
			double next =  k>1 ? rhs : 0;
			if( k == 0 || k == 1 ) {
				context.save();
				context.beginPath();
				context.rect(w, 0, cwidth, canvas.getCoordinateSpaceHeight());
				context.clip();
				for( int y = ys; y < ye; y+=unitheight ) {
					int i = y/unitheight;
					int yy = i*unitheight;
					FoodInfo fi = nutdata.getTheFoodInfo(i);
						
					if( fi != null ) {
						if( fi.isSelected() ) {
							context.setFillStyle("rgba( 100, 100, 155, 0.5 )");
							context.fillRect(w, yy-ystartLocal+columnHeight, cwidth, unitheight );
							context.setFillStyle("#222222");
						}	
						/*if( selset.contains(i) ) {
							context.setFillStyle("#DDDDFF");
							context.fillRect(0, ystartLocal, canvasWidth, unitheight );
							context.setFillStyle("#222222");
						}*/
						
						String o = k == 0 ? fi.getName() : fi.getGroup();
						if( o != null ) context.fillText(o.toString(), w+5, yy+columnHeight+unitheight-3.0-ystartLocal );
					}
				}
				context.restore();
			} else if( w+cwidth-next > sub && w-next < sub+canvasWidth-rhs ) { //else if( w+c.width > xstartLocal && w < xstartLocal+(canvasWidth-rhs) ) {
				//int x = xs; x < Math.min( getMax(), xe ); x+=unitwidth 
				
				//int k = x/unitwidth;
				//int xx = k*unitwidth;
				//Math.max( k>1 ? rhs : 0,w-sub)
				context.save();
				context.beginPath();
				context.rect( Math.max( rhs, w-xstartLocal ), 0, cwidth, canvas.getCoordinateSpaceHeight());
				context.clip();
				for( int y = ys; y < ye; y+=unitheight ) {
					int i = y/unitheight;
					int yy = i*unitheight;
					FoodInfo fi = nutdata.getTheFoodInfo(i);
					//int[]	ann = seq.getAnnotationIndex();
						
					if( fi != null ) {
						if( fi.isSelected() ) {
							context.setFillStyle("rgba( 100, 100, 155, 0.5 )");
							context.fillRect(w-xstartLocal, yy-ystartLocal+columnHeight, cwidth, unitheight );
							context.setFillStyle("#222222");
						}
						/*if( selset.contains(i) ) {
							context.setFillStyle("#DDDDFF");
							context.fillRect(0, ystartLocal, canvasWidth, unitheight );
							context.setFillStyle("#222222");
						}*/
						
						try {
							float o = fi.floatValAt(k);
							if( o != -1.0 ) context.fillText( Float.toString(o), w+5-xstartLocal, yy+columnHeight+unitheight-3.0-ystartLocal );
						} catch( Exception e ) {
							context.fillText( e.getMessage(), w+5-xstartLocal, yy+columnHeight+unitheight-3.0-ystartLocal );
						}
					}
				}
				context.restore();
			}
			w += cwidth;
			k++;
		}
	}
	
	public void subdraw( Context2d context, int ys, int ye, int xstartLocal, int ystartLocal, int canvasWidth, int canvasHeight ) {
		double rhs = getRowHeaderSize();
		int w = 0;
		int k = 0;
		for( Column c : lcolumnwidth ) {
			double sub = k > 1 ? xstartLocal : 0;
			double next =  k>1 ? rhs : 0;
			if( k == 0 || k == 1 ) {
				context.save();
				context.beginPath();
				context.rect(w, 0, c.getWidth(), canvas.getCoordinateSpaceHeight());
				context.clip();
				for( int y = ys; y < ye; y+=unitheight ) {
					int i = y/unitheight;
					int yy = i*unitheight;
					FoodInfo fi = getFoodInfo(i);
						
					if( fi != null ) {
						if( fi.isSelected() ) {
							context.setFillStyle("rgba( 100, 100, 155, 0.5 )");
							context.fillRect(w, yy-ystartLocal+columnHeight, c.getWidth(), unitheight );
							context.setFillStyle("#222222");
						}	
						/*if( selset.contains(i) ) {
							context.setFillStyle("#DDDDFF");
							context.fillRect(0, ystartLocal, canvasWidth, unitheight );
							context.setFillStyle("#222222");
						}*/
						
						String o = k == 0 ? fi.getName() : fi.getGroup();
						if( o != null ) context.fillText(o.toString(), w+5, yy+columnHeight+unitheight-3.0-ystartLocal );
					}
				}
				context.restore();
			} else if( w+c.getWidth()-next > sub && w-next < sub+canvasWidth-rhs ) { //else if( w+c.width > xstartLocal && w < xstartLocal+(canvasWidth-rhs) ) {
				//int x = xs; x < Math.min( getMax(), xe ); x+=unitwidth 
				
				//int k = x/unitwidth;
				//int xx = k*unitwidth;
				//Math.max( k>1 ? rhs : 0,w-sub)
				context.save();
				context.beginPath();
				context.rect( Math.max( rhs, w-xstartLocal ), 0, c.getWidth(), canvas.getCoordinateSpaceHeight() );
				context.clip();
				for( int y = ys; y < ye; y+=unitheight ) {
					int i = y/unitheight;
					int yy = i*unitheight;
					FoodInfo fi = getFoodInfo(i);
					//int[]	ann = seq.getAnnotationIndex();
						
					if( fi != null ) {
						if( fi.isSelected() ) {
							context.setFillStyle("rgba( 100, 100, 155, 0.5 )");
							context.fillRect(w-xstartLocal, yy-ystartLocal+columnHeight, c.getWidth(), unitheight );
							context.setFillStyle("#222222");
						}
						/*if( selset.contains(i) ) {
							context.setFillStyle("#DDDDFF");
							context.fillRect(0, ystartLocal, canvasWidth, unitheight );
							context.setFillStyle("#222222");
						}*/
						
						float o = fi.floatValAt(k-2);
						if( o != -1.0f ) context.fillText( Float.toString( Math.round(o*100.0f)/100.0f ), w+5-xstartLocal, yy+columnHeight+unitheight-3.0-ystartLocal );
					}
				}
				context.restore();
			}
			w += c.getWidth();
			k++;
		}
	}
	
	public int getRowHeaderSize() {
		return lcolumnwidth.get(0).getWidth()+lcolumnwidth.get(1).getWidth()	;
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
			
			int rhs = getRowHeaderSize();
			int k = 0;
			int w = 0;
			for( Column c : lcolumnwidth ) {
				double sub = k > 1 ? xstartLocal : 0;
				double next =  k>1 ? rhs : 0;
				if( w+c.getWidth()-next > sub && w-next < sub+rw-rhs ) {					
					double xstart = Math.max( next,w-sub);
					double clipw = c.getWidth()-(xstart-(w-sub));
					
					String unit = c.getUnit() == null ? null : "("+c.getUnit()+")";
					double strw = unit == null ? 0.0 : context.measureText( unit ).getWidth();
					
					context.save();
					context.beginPath();
					context.rect( xstart, 0, Math.max(0,clipw-strw-5), columnHeight);
					//context.rect(rhs, 0, rw-rhs, ch);
					context.closePath();
					context.clip();
					context.fillRect( w-sub, 0, 1, columnHeight );
					context.fillText( c.getName(), (w+5-sub), columnHeight-7 );
					
					context.restore();
					context.save();
					context.beginPath();
					context.rect( xstart, 0, Math.max(0, clipw), columnHeight);
					context.closePath();
					context.clip();
					
					if( unit != null ) context.fillText( unit, (w+c.getWidth()-xstartLocal-strw-2.0), columnHeight-7 );
					context.restore();
				}
				
				w += c.getWidth();
				k++;
			}
			
			context.setFillStyle("#EEEEEE");
			context.fillRect(rw, columnHeight, scrollBarWidth, rh);
			context.fillRect(rhs, rh+columnHeight, rw-rhs, scrollBarHeight);
			context.setFillStyle("#888888");
			context.fillRect(rw, 0, scrollBarWidth, columnHeight);
			context.fillRect(0, rh+columnHeight, rhs, scrollBarHeight);
			context.fillRect(rw, rh+columnHeight, scrollBarWidth, scrollBarHeight);
			context.setFillStyle("#333333");
			int max = getMax();
			if( getFoodNumber() > 0 && max > 0 ) {
				context.fillRect( rw, columnHeight+(rh*ystartLocal)/(getFoodNumber()*unitheight-rh)-3.0, scrollBarWidth, 6.0 );
				context.fillRect( rhs + ((rw-rhs)*xstartLocal)/(max-(rw))-3.0, rh+columnHeight, 6.0, scrollBarHeight );
			}
			
			prevx = xstartLocal;
			prevy = ystartLocal;
		}
	}
	
	Canvas canvas = null;
	final Map<String,String>	groupIdMap = new HashMap<String,String>();
	boolean	mousedown = false;
	boolean touched = false;
	boolean	touchmoved = false;
	boolean touchcontent = false;
	double	mousex = 0.0;
	double	mousey = 0.0;
	double	touchx = 0.0;
	double	touchy = 0.0;
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
			w += c.getWidth();
		}
		return w;
	}
	
	final Label		filterLabel = new Label("Filter:");
	final TextBox	filterText = new TextBox();
	final ListBox	filterCombo = new ListBox();
	static List<Integer>	filtInd = new ArrayList<Integer>();
	
	public int getVisibleHeight() {
		return canvas.getCoordinateSpaceHeight()-(columnHeight+scrollBarHeight);
	}
	
	public void sort() {
		if( sortcolumn >= 2 ) Collections.sort( lfoodinfo, Collections.reverseOrder() );
		else Collections.sort( lfoodinfo );
		applyFilter();
	}
	
	public void applyFilter() {
		String groupfilter = filterCombo.getValue( filterCombo.getSelectedIndex() );
		String foodfilter = filterText.getValue();
		filtInd.clear();
		selset.clear();
		
		String groupval = groupfilter.toLowerCase();
		String foodval = foodfilter.toLowerCase();
		
		//if( foodfilter != null ) {
		int i = 0;
		for( FoodInfo fi : lfoodinfo ) {
			String group = fi.getGroup();
			group = group.toLowerCase();
			String food = fi.getName();
			food = food.toLowerCase();
			
			boolean b1 = groupval.length() > 0 && foodval.length() > 0 && group.contains( groupval ) && food.contains( foodval );
			boolean b2 = groupval.length() == 0 && foodval.length() > 0 && (group.contains( foodval ) || food.contains( foodval ));
			boolean b3 = groupval.length() > 0 && foodval.length() == 0 && group.contains( groupval );
			
			if( fi.isSelected() ) selset.add( i );
			if( b1 || b2 || b3 ) filtInd.add(i);
			
			i++;
		}
		
		
//		String groupfilter = filterCombo.getValue( filterCombo.getSelectedIndex() );
//		String foodfilter = filterText.getValue();
//		filtInd.clear();
//		
//		if( groupfilter != null ) {
//			String groupval = groupfilter.toLowerCase();
//			String foodval = foodfilter.toLowerCase();
//			
//			int i = 0;
//			for( FoodInfo fi : lfoodinfo ) {
//				String group = (String)fi.valAt(1);
//				group = group.toLowerCase();
//				String food = (String)fi.valAt(0);
//				food = food.toLowerCase();
//				
//				boolean b1 = groupval.length() > 0 && foodval.length() > 0 && (group.contains( groupval ) && food.contains( foodval ));
//				boolean b2 = groupval.length() == 0 && foodval.length() > 0 && (group.contains( foodval ) || food.contains( foodval ));
//				boolean b3 = groupval.length() > 0 && foodval.length() == 0 && group.contains( groupval );
//				
//				if( food.contains("sweets") ) {
//					console.log( b1 + " " + b2 + " " + b3 );
//					console.log( group + " " + food );
//				}
//				if( b1 || b2 || b3 ) filtInd.add(i);
//				
//				//if( group != null && group.toLowerCase().contains( val.toLowerCase() ) ) filtInd.add(i);
//				//else console.log( fi.valAt(0) );
//				i++;
//			}
//		} else console.log( filterCombo.getSelectedIndex() );
	}
	
	public void touchMouseMove( double x, double y ) {
		double cw = (double)canvas.getCoordinateSpaceWidth();
		double ch = (double)canvas.getCoordinateSpaceHeight();
		if( scrollx || scrolly ) {
			if( scrollx ) {
				int max = getMax();
				int rhs = getRowHeaderSize();
				double xmin1 = max-(cw-scrollBarWidth);
				double xmin2 = (xmin1*(x-rhs))/(cw-rhs-scrollBarWidth);
				xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
			}
			
			if( scrolly ) {
				double ymin1 = getFoodNumber()*unitheight-(ch-columnHeight-scrollBarHeight);
				double ymin2 = (ymin1*(y-unitheight))/(ch-columnHeight-scrollBarHeight);
				ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
			}
			draw( canvas.getContext2d(), xstart, ystart );
		} else {
			draw( canvas.getContext2d(), Math.max( 0, xstart-(int)(x-mousex) ), Math.max( 0, ystart-(int)(y-mousey) ) );
			/*int max = getMax();
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
			}*/
		}
	}
	
	public boolean touchMouseDown( double mousex, double mousey ) {
		int cw = canvas.getCoordinateSpaceWidth();
		int ch = canvas.getCoordinateSpaceHeight();
		
		if( mousey < columnHeight && mousex <= cw-scrollBarWidth ) {
			int i = -1;
			int w = lcolumnwidth.get(0).getWidth();
			int w2 = w + lcolumnwidth.get(1).getWidth();
			
			if( mousex < w ) {
				i = 0;
			} else if( mousex < w2 ) {
				i = 1;
			} else {
				i = 2;
				w = lcolumnwidth.get(i).getWidth();
				while( w < mousex+xstart-w2 ) {
					i++;
					if( i == lcolumnwidth.size() ) {
						i = -1;
						break;
					}
					w += lcolumnwidth.get(i).getWidth();
				}
			}
			
			if( i != -1 ) {
				sortcolumn = i;	
				sort();
			}
		} else if( mousex > cw-scrollBarWidth || mousey > ch-scrollBarHeight ) {
			//int xstart = Webfasta.this.xstart;
			if( mousey > ch-scrollBarHeight ) {
				scrollx = true;
				
				int rhs = getRowHeaderSize();
				int max = getMax();
				double xmin1 = max-(cw-scrollBarWidth);
				double xmin2 = (xmin1*(mousex-rhs))/(cw-rhs-scrollBarWidth);
				xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
			}
			
			//int ystart = Webfasta.this.ystart;
			if( mousex > cw-scrollBarWidth ) {
				scrolly = true;
				
				double ymin1 = getFoodNumber()*unitheight-(ch-columnHeight-scrollBarHeight);
				double ymin2 = (ymin1*(mousey-columnHeight))/(ch-columnHeight-scrollBarHeight);
				ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
			}
		} else return false;
		
		return true;
	}
	
	RootPanel	contentrp;
	@Override
	public void onModuleLoad() {
		elemental.html.Window wnd = Browser.getWindow();
		console = wnd.getConsole();
		console.log("starting");
		
		nutdata = getNutData();
		
		RootPanel	rp = RootPanel.get();
		contentrp = RootPanel.get("content");
		RootPanel	menurp = RootPanel.get("menu");
		
		final RootPanel	ad1 = RootPanel.get("ad1");
		final RootPanel	ad2 = RootPanel.get("ad2");
		
		final Style ad1style = ad1.getElement().getStyle();
		final Style ad2style = ad2.getElement().getStyle();
		
		Style style = rp.getElement().getStyle();
		style.setBorderWidth(0.0, Unit.PX);
		style.setMargin(0.0, Unit.PX);
		style.setPadding(0.0, Unit.PX);
		
		style = contentrp.getElement().getStyle();
		style.setBorderWidth(0.0, Unit.PX);
		style.setMargin(0.0, Unit.PX);
		style.setPadding(0.0, Unit.PX);
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_LEFT );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		//int w = 1024;//Window.getClientWidth();
		//int h = 640;//Window.getClientHeight();
		//vp.setSize(w+"px", h+"px");
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		vp.setSize("100%", "100%");
		
		boolean wmh = w > h;
		int nw = Math.max( w, 735 ) - (wmh ? 195 : 10);
		if( wmh ) {
			ad1style.setDisplay( Display.NONE );
			ad2style.setDisplay( Display.INLINE );
			
			ad1.setHeight( "0%" );
			ad2.setWidth( "100%" );
		} else {
			ad1style.setDisplay( Display.INLINE );
			ad2style.setDisplay( Display.NONE );
			
			ad1.setHeight( "100%" );
			ad2.setWidth( "0%" );
		}
		
		canvas = Canvas.createIfSupported();
		canvas.setSize( nw+"px", "600px");
		canvas.setCoordinateSpaceWidth( nw );
		canvas.setCoordinateSpaceHeight( 600 );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				boolean wmh = w > h;
				int nw = Math.max( w, 735 ) - (wmh ? 175 : 10);
				canvas.setSize( nw+"px", "600px" );
				canvas.setCoordinateSpaceWidth( nw );
				canvas.setCoordinateSpaceHeight( 600 );
				draw( canvas.getContext2d(), xstart, ystart );
				//vp.setSize((w-10)+"px", 640+"px");
				
				if( wmh ) {
					ad1style.setDisplay( Display.NONE );
					ad2style.setDisplay( Display.INLINE );
					
					ad1.setHeight( "0%" );
					ad2.setWidth( "100%" );
				} else {
					ad1style.setDisplay( Display.INLINE );
					ad2style.setDisplay( Display.NONE );
					
					ad1.setHeight( "100%" );
					ad2.setWidth( "0%" );
				}
			}
		});
		
		if( nutdata == null ) {
			lcolumnwidth.add( new WebColumn("Food", null, 300, "0") );
			lcolumnwidth.add( new WebColumn("Group", null, 180, "0") );
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
		} else {
			int u = contentrp.getWidgetIndex( html );
			if( u == -1 ) contentrp.add( html );
			
			contentrp.add( new HTML("fuckfuk") );
			
			filterCombo.addItem("");
			for( int i = 0; i < nutdata.getGroupCount(); i++ ) {
				String group = nutdata.getGroup(i);
				filterCombo.addItem( groupIdMap.get(group) );
			}
			
			contentrp.add( new HTML("okfuckb") );
			contentrp.add( new HTML("okfuck"+nutdata.getFoodInfoCount()) );
			
			for( int i = 0; i < nutdata.getFoodInfoCount(); i++ ) {
				FoodInfo foodInfo = nutdata.getFoodInfo(i);
				lfoodinfo.add( foodInfo );
			}
			
			contentrp.add( new HTML("simmi") );
			contentrp.add( new HTML( "bleh "+nutdata.getColumnCount() ) );
			//u = contentrp.getWidgetIndex( html2 );
			//if( u == -1 ) contentrp.add( html2 );
			
			for( int i = 0; i < nutdata.getColumnCount(); i++ ) {
				Column column = nutdata.getColumn(i);
				lcolumnwidth.add( column );
			}
			
			//u = contentrp.getWidgetIndex( html2 );
			//if( u == -1 ) contentrp.add( html2 );
			contentrp.add( new Label("done") );
		}
		
		canvas.addTouchCancelHandler( new TouchCancelHandler() {

			@Override
			public void onTouchCancel(TouchCancelEvent event) {
				Browser.getWindow().getConsole().log("touchcancel");
			}
			
		});
		canvas.addTouchEndHandler( new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				Browser.getWindow().getConsole().log("touchend");
				
				//Touch touch = event.getTouches().get(0);
				//int x = touch.getRelativeX( canvas.getElement() );
				//int y = touch.getRelativeY( canvas.getElement() );
				
				Browser.getWindow().getConsole().log("touchend2");
				if( touchcontent ) {
					if( !touchmoved ) {
						//touchMouseDown( x, y );
						double wherey = ystart+mousey-columnHeight;
						int indy = (int)(wherey/unitheight);
						FoodInfo foodinf = getFoodInfo( indy );
						if( foodinf != null ) {
							for( int i : selset ) {
								FoodInfo fi = lfoodinfo.get(i); //getFoodInfo(i);
								if( fi != null ) fi.setSelected( false, i );
							}
							foodinf.setSelected( !foodinf.isSelected(), getRealIndex(indy) );
						}
					} else {
						xstart = Math.max( 0, xstart-(int)(touchx-mousex) );
						ystart = Math.max( 0, ystart-(int)(touchy-mousey) );
					}
					draw( canvas.getContext2d(), xstart, ystart );
				}
				
				//Window.alert("touchend");
				
				mousedown = false;
				scrollx = false;
				scrolly = false;
			}
		});
		canvas.addTouchStartHandler( new TouchStartHandler() {

			@Override
			public void onTouchStart(TouchStartEvent event) {
				Browser.getWindow().getConsole().log("touchstart");
				
				touched = true;
				touchmoved = false;
				
				Touch touch = event.getTouches().get(0);
				int x = touch.getRelativeX( canvas.getElement() );
				int y = touch.getRelativeY( canvas.getElement() );
				
				mousex = x;
				mousey = y;
				touchx = x;
				touchy = y;
				
				touchcontent = !touchMouseDown(x, y);
				if( !touchcontent ) draw( canvas.getContext2d(), xstart, ystart );
				//if( mousey > ch-scrollBarHeight ) scrollx = true;
				//if( mousex > cw-scrollBarWidth ) scrolly = true;
				
				event.preventDefault();
				event.stopPropagation();
			}
		});
		canvas.addTouchMoveHandler( new TouchMoveHandler() {

			@Override
			public void onTouchMove(TouchMoveEvent event) {
				touchmoved = true;
				event.preventDefault();
				event.stopPropagation();
				
				Touch touch = event.getTouches().get(0);
				touchx = touch.getRelativeX( canvas.getElement() );
				touchy = touch.getRelativeY( canvas.getElement() );
				touchMouseMove( touchx, touchy );
				//draw( canvas.getContext2d(), xstart, ystart );
			}
			
		});
		
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
				if( !touched ) {
					mousedown = true;
					mousex = event.getX();
					mousey = event.getY();
					boolean scrollsort = touchMouseDown( mousex, mousey );
					if( !scrollsort ) {
						double wherey = ystart+mousey-columnHeight;
						int indy = (int)(wherey/unitheight);
						FoodInfo foodinf = getFoodInfo( indy );
						if( foodinf != null ) {
							for( int i : selset ) {
								FoodInfo fi = lfoodinfo.get(i); //getFoodInfo(i);
								if( fi != null ) fi.setSelected( false, i );
							}
							foodinf.setSelected( !foodinf.isSelected(), getRealIndex(indy) );
						}
					}
					
					draw( canvas.getContext2d(), xstart, ystart );
				} else touched = false;
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
					
					touchMouseMove( x, y );
					draw( canvas.getContext2d(), xstart, ystart );
				}
			}
		});		
		HorizontalPanel	filterPanel = new HorizontalPanel();
		filterPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		filterPanel.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		filterPanel.setSpacing( 5 );
		filterCombo.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				applyFilter();
				
				ystart = 0;
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		filterText.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				applyFilter();
				
				ystart = 0;
				draw( canvas.getContext2d(), xstart, ystart );
			}
		});
		filterPanel.add( filterLabel );
		filterPanel.add( filterCombo );
		filterPanel.add( filterText );
		filterPanel.add( new Label("(press enter)") );
		
		final VerticalPanel	subvp = new VerticalPanel();
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		subvp.setSize("100%", "40px");
		//subvp.add( canvas );
		subvp.add( filterPanel );
		//vp.add( subvp );
		vp.add( canvas );
		
		RootPanel emptypanel = RootPanel.get("emptyspace");
		emptypanel.setHeight("100px");
		
		contentrp.add( vp );
		menurp.add( subvp );
	}
}
