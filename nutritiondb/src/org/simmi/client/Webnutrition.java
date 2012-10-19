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
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
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
		
		public FoodInfo( String name, String group, double energy ) {
			columns = new Object[] { name, group, energy };
		}
		
		public Object getSortObject() {
			return columns[sortcolumn];
		}
		
		public Object valAt( int i ) {
			return columns[i];
		}
		
		public int getLength() {
			return columns.length;
		}

		@Override
		public int compareTo(FoodInfo o) {
			Object obj = columns[ sortcolumn ];
			if( obj instanceof String ) {
				((String)obj).compareTo( (String)o.getSortObject() );
			} else if( obj instanceof Double ) {
				((Double)obj).compareTo( (Double)o.getSortObject() );
			}
			return 0;
		}
	};
	List<FoodInfo>	lfoodinfo = new ArrayList<FoodInfo>();
	List<Integer>	lcolumnwidth = new ArrayList<Integer>();
	
	public void fetchFood() throws RequestException {
		String url = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20Id,GroupId,Name%20FROM%2011kJvZY3UCjtqcA0gN8WRZwtTVGXJ_MxAto7cdUU&key=AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
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
							lfoodinfo.add( new FoodInfo( namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ), 0.0 ) );
							lcolumnwidth.add( 100 );
						} else if( !done ) {
							done = true;
							console.log( jsonString );
						}
						//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
					}
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
		
		int xs = Math.max( 0, ((xstartLocal+xloc)/unitwidth)*unitwidth );
		int ys = Math.max( 0, ((ystartLocal+yloc)/unitheight)*unitheight );
		int xe = ((xstartLocal+xloc+canvasWidth)/unitwidth+1)*unitwidth;
		int ye = Math.min( ((ystartLocal+yloc+canvasHeight)/unitheight+1)*unitheight, getFoodNumber()*unitheight );
		
		context.fillRect( xs-xstartLocal, ys-ystartLocal+unitheight, xe-xs, ye-ys );
		
		context.setFillStyle("#222222");
		for( int x = xs; x < Math.min( lfoodinfo.get(0).getLength()*unitwidth, xe ); x+=unitwidth ) {
			int k = x/unitwidth;
			int xx = k*unitwidth;
			
			context.save();
			context.beginPath();
			context.rect(x, 0, unitwidth, canvas.getCoordinateSpaceHeight());
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
				
				Object c = fi.valAt(k);
				context.fillText(c.toString(), (xx-xstartLocal), yy+2.0*unitheight-3.0-ystartLocal );
			}
			context.restore();
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
			
			context.setFillStyle("#EEEEEE");
			context.fillRect(rw, columnHeight, scrollBarWidth, rh);
			context.fillRect(0, rh+columnHeight, rw, scrollBarHeight);
			context.setFillStyle("#888888");
			context.fillRect(rw, 0, scrollBarWidth, columnHeight);
			context.fillRect(rw, rh+columnHeight, scrollBarWidth, scrollBarHeight);
			context.setFillStyle("#333333");
			if( getFoodNumber() > 0 && max > 0 ) {
				context.fillRect( rw, columnHeight+(rh*ystartLocal)/(getFoodNumber()*unitheight-rh)-3.0, scrollBarWidth, 6.0 );
				context.fillRect( (rw*xstartLocal)/(max*unitwidth-rw)-3.0, rh+columnHeight, 6.0, scrollBarHeight );
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
	int	unitwidth = 200;
	int prevx = 0;
	int prevy = 0;
	int xstart = 0;
	int ystart = 0;
	
	int max = 3;
	
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
		
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		canvas = Canvas.createIfSupported();
		canvas.setSize("1024px", "768px");
		canvas.setCoordinateSpaceWidth( 1024 );
		canvas.setCoordinateSpaceHeight( 768 );
		
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
							fetchFood();
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
					int w = lcolumnwidth.get(i);
					while( w < mousex ) {
						i++;
						w += lcolumnwidth.get(i);
					}
					sortcolumn = i;
					
					Collections.sort( lfoodinfo );
				} else if( mousex > cw-scrollBarWidth || mousey > ch-scrollBarHeight ) {
					//int xstart = Webfasta.this.xstart;
					if( mousey > ch-scrollBarHeight ) {
						scrollx = true;
						
						double xmin1 = max*unitwidth-cw;
						double xmin2 = (xmin1*mousex)/(cw-scrollBarWidth);
						xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
					}
					
					//int ystart = Webfasta.this.ystart;
					if( mousex > cw-scrollBarWidth ) {
						scrolly = true;
						
						double ymin1 = getFoodNumber()*unitheight-ch;
						double ymin2 = (ymin1*(mousey-unitheight))/(ch-scrollBarHeight-unitheight);
						ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
					}
					
					draw( canvas.getContext2d(), xstart, ystart );
				}
			}
		});
		canvas.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mousedown = false;
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
							double xmin1 = max*unitwidth-cw;
							double xmin2 = (xmin1*x)/(cw-scrollBarWidth);
							xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
						}
						
						if( scrolly ) {
							double ymin1 = getFoodNumber()*unitheight-canvas.getCoordinateSpaceHeight();
							double ymin2 = (ymin1*(y-unitheight))/(ch-scrollBarHeight-unitheight);
							ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
						}
						
						draw( canvas.getContext2d(), xstart, ystart );
					} else {
						double xmin1 = max*unitwidth-cw;
						double xmin2 = xstart + (mousex-x);
						int xstart = (int)Math.max( 0.0, Math.min( xmin1, xmin2 ) );
						
						if( xmin2 > xmin1 ) {
							mousex = mousex+(int)(xmin1-xmin2);
						}
						
						double ymin1 = getFoodNumber()*unitheight-ch;
						double ymin2 = ystart + (mousey-y);
						int ystart = (int)Math.max( 0.0, Math.min( ymin1, ymin2 ) );
						
						if( ymin2 > ymin1 ) {
							mousey = mousey+(int)(ymin1-ymin2);
						}
						
						draw( canvas.getContext2d(), xstart, ystart );
					}
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
