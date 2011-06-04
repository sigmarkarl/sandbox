package org.simmi.mapviewer.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mapviewer implements EntryPoint {
	
	private final LocationFetchAsync locFetchService = GWT.create(LocationFetch.class);
	
	String locs = null;
	
	public void onModuleLoad() {
		Maps.loadMapsApi("", "2", false, new Runnable() {
			public void run() {
				buildUi();
		    }
		});
	}
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	List<Marker>	markersArray = new ArrayList<Marker>();
	public void clearMarkers( MapWidget map ) {
		for( Marker marker : markersArray ) {
			map.removeOverlay( marker );
		}
		markersArray.clear();
	}
	
	public void pos( final MapWidget map, String str, double lat, double lng, final String contentString, String imurl ) {
		  LatLng pos = LatLng.newInstance( lat, lng );
		  Icon icon = Icon.newInstance(imurl);
		  MarkerOptions markerOptions = MarkerOptions.newInstance( icon );
		  markerOptions.setTitle( str );
		  final Marker marker = new Marker( pos, markerOptions );
		  //marker.setImage( "http://hudson-assembler.googlecode.com/files/griffon-icon-16x16.png" );//imurl );
		  markersArray.add( marker );
		  map.addOverlay( marker );
		  
		  marker.addMarkerClickHandler( new MarkerClickHandler() {
			@Override
			public void onClick(MarkerClickEvent event) {
				map.getInfoWindow().open(marker.getLatLng(), new InfoWindowContent(contentString));
			}
		  });
		  
		  /*var infowindow = new google.maps.InfoWindow({
			    content: contentString
		  });
		  
		  google.maps.event.addListener(marker, 'click', function() {
			  infowindow.open(map,marker);
		  });*/
	  }
	
	public void load( MapWidget map, Map<String,LatLng> m, int mintemp, int maxtemp, int phmin, int phmax, String selitem ) {
		//InputStream is = this.getClass().getResourceAsStream("/locs.txt");
		//BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		//Random r = new Random();
		//JSObject jo = JSObject.getWindow(this);
		//jo.call("clearMarkers", new Object[] {});
		clearMarkers( map );
		
		Canvas canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceWidth(16);
		canvas.setCoordinateSpaceHeight(16);
		//BufferedImage bi = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		//Graphics2D g = (Graphics2D)bi.getGraphics();
		Context2d context2d = canvas.getContext2d();
		
		/*g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE );*/
		//g.setRenderingHint( RenderingHints.KEY_ );
		
		String[] lines = locs.split("\n");
		for( String line : lines ) {
			String[] split = line.split("[\t]+");
			
			double ph = 7.0;
			if( split[3].contains("-") ) {
				String[] subsplit = split[3].trim().split("-");
				ph = ( Double.parseDouble( subsplit[0] ) + Double.parseDouble( subsplit[1] ) ) / 2.0;
			} else ph = Double.parseDouble(split[3].trim());
			
			int temp = 0;
			if( split[2].contains("-") ) {
				String[] subsplit = split[2].split("-");
				temp = ( Integer.parseInt( subsplit[0].trim() ) + Integer.parseInt( subsplit[1].trim() ) ) / 2;
			} else temp = Integer.parseInt(split[2].trim());
			
			boolean selname = selitem.equals("All");
			String[] specsplit = split[5].split("T\\. ");
			
			for( int i = 1; i < specsplit.length && !selname; i++ ) {
				String ss = specsplit[i];
				int k = ss.indexOf(' ');
				
				String ch;
				if( k > 0 ) ch = "T."+ss.substring(0, k);
				else ch = "T."+ss;
				
				if( selitem.contains(ch) ) {
					selname = true;
				}
			}
			
			if( temp >= mintemp && temp <= maxtemp && ph >= phmin && ph <= phmax && selname ) {
				String content = "<div id=\"content\">"+
			    "<div id=\"siteNotice\">"+
			    "</div>"+
			    "<div id=\"bodyContent\">"+
			    "<b>"+ split[0] + "\t" + split[1] + "</b>" +
			    "<br>Temperature: "+split[2]+
			    "<br>Ph: "+split[3]+
			    "<br>Sampletype: "+split[4]+
			    "<br>Strains: "+split[5]+
			    "</div>";
				
				//context2d.setFillStyle("#000000");
				context2d.clearRect(0, 0, canvas.getCoordinateSpaceWidth()-1, canvas.getCoordinateSpaceHeight()-1);
				
				String color = "#"+Integer.toString(200+(int)(15*(ph-7.0)), 16)+Integer.toString(200-(int)(15*(ph-7.0)), 16)+"00";
				//color = "#0000ff";
				context2d.setFillStyle( color );
				//g.setColor( new Color( 200+(int)(15*(ph-7.0)), 200-(int)(15*(ph-7.0)), 0 ) );
				context2d.beginPath();
				context2d.moveTo(0.0, 0.0);
				context2d.arc( 8, 16, 16, Math.PI/2.0, 2.0*Math.PI/3.0 );
				context2d.closePath();
				context2d.fill();
				//fillArc(-8, 0, 32, 32, 60, 30);
				
				//g.setColor( new Color( 255, (int)((100-temp)*5.0), 0 ) );
				//g.fillArc(-8, 0, 32, 32, 90, 30);
				color = "#"+Integer.toString(255, 16)+Integer.toString( (int)((100-temp)*5.0), 16)+"00";
				//color = "#00ff00";
				context2d.setFillStyle( color );
				
				context2d.beginPath();
				context2d.moveTo(0.0, 0.0);
				//context2d.arc( 8, 8, 8, 0, 2*Math.PI );//Math.PI/2.0, 2.0*Math.PI/3.0);
				context2d.arc(8, 16, 16, Math.PI/3.0, Math.PI/2.0);
				context2d.closePath();
				context2d.fill();
				
				//g.setColor( Color.black );
				//g.drawArc(-8, 0, 32, 32, 60, 60);
				color = "#000000";
				context2d.setFillStyle( color );
				context2d.beginPath();
				context2d.moveTo(0.0, 0.0);
				context2d.arc(8, 16, 16, Math.PI/3.0, 2.0*Math.PI/3.0 );
				context2d.closePath();
				context2d.stroke();
				
				String dataurl = canvas.toDataUrl();
				console( dataurl );
				
				LatLng ll = null;
				if( m.containsKey(split[0]) ) {
					ll = m.get(split[0]);
					//jo.call("pos", new Object[] {split[split.length-1]+" Ph:"+split[3]+" Temp:"+split[2],ll.lat+r.nextDouble()/100.0,ll.lng+r.nextDouble()/100.0+"",content, "data:image/png;base64,"+dataurl});
					pos( map, split[split.length-1]+" Ph:"+split[3]+" Temp:"+split[2],ll.getLatitude()+Random.nextDouble()/100.0,ll.getLongitude()+Random.nextDouble()/100.0,content, dataurl );
				} else {
					Window.alert( split[0] );
				}
			}
		}
	}
	
	public void doStuff( final MapWidget map ) {
		final Map<String,LatLng>	m = new HashMap<String,LatLng>();
		m.put("Hveragerdi", LatLng.newInstance(63.99,-21.10));
		m.put("Hveravellir", LatLng.newInstance(64.45,-19.33));
		m.put("Oxarfjordur", LatLng.newInstance(66.11,-16.75));
		m.put("Hrafntinnusker", LatLng.newInstance(63.55,-19.09));
		m.put("Geysir area", LatLng.newInstance(64.19,-20.18));
		m.put("Snaefellsnes", LatLng.newInstance(64.65,-22.15));
		m.put("Hagongur", LatLng.newInstance(64.32,-18.12));
		m.put("Reykjanes", LatLng.newInstance(63.88,-22.42));
		m.put("USA", LatLng.newInstance(43.88,-22.42));
		m.put("Japan", LatLng.newInstance(43.88,-2.42));
		m.put("South-africa", LatLng.newInstance(23.88,-32.42));
		
		final ListBox	combobox = new ListBox();
		HorizontalPanel toolbar = new HorizontalPanel();
		toolbar.setSpacing(5);
		toolbar.setHeight("25px");
		toolbar.setWidth("100%");
		toolbar.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		toolbar.add( new Label("Temp filter:") );

		final IntegerBox minspin = new IntegerBox();
		minspin.setValue( 45 );
		toolbar.add( minspin );
		final IntegerBox maxspin = new IntegerBox();
		maxspin.setValue( 100 );
		toolbar.add( maxspin );
		
		toolbar.add( new Label("Ph filter:") );
		final IntegerBox phminspin = new IntegerBox();
		phminspin.setValue( 2 );
		toolbar.add( phminspin );
		final IntegerBox phmaxspin = new IntegerBox();
		phmaxspin.setValue( 12 );
		toolbar.add( phmaxspin );
		
		ValueChangeHandler<Integer> cl = new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				load( map, m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getValue( combobox.getSelectedIndex() ) );
			}
		};
		
		minspin.addValueChangeHandler( cl );
		maxspin.addValueChangeHandler( cl );
		phminspin.addValueChangeHandler( cl );
		phmaxspin.addValueChangeHandler( cl );
		
		combobox.addItem( "All" );
		combobox.addItem( "T.thermophilus" );
		combobox.addItem( "T.scotoductus" );
		combobox.addItem( "T.brockianus" );
		combobox.addItem( "T.filiformis" );
		combobox.addItem( "T.eggertsoni" );
		combobox.addItem( "T.islandicus" );
		combobox.addItem( "T.oshimai" );
		combobox.addItem( "T.antranikianii" );
		combobox.addItem( "T.igniterrae" );
		
		combobox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(com.google.gwt.event.dom.client.ChangeEvent event) {
				load( map, m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getValue( combobox.getSelectedIndex() ) );
			}
		});
		
		toolbar.add( new Label("Species filter:") );
		toolbar.add( combobox );
		
		//int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		
		map.setHeight( (h-25)+"px" );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int h = event.getHeight();
				map.setHeight( (h-25)+"px" );
			}
		});
		
		VerticalPanel vp = new VerticalPanel();
		//vp.setSize(w+"px", h+"px");
		vp.setWidth("100%");
		vp.add( toolbar );
		vp.add( map );
		RootPanel.get().add( vp );
		
		try {
			locFetchService.fetchLocations( new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}

				@Override
				public void onSuccess(String result) {
					locs = result;
					load( map, m, (Integer)minspin.getValue(), (Integer)maxspin.getValue(), (Integer)phminspin.getValue(), (Integer)phmaxspin.getValue(), (String)combobox.getValue( combobox.getSelectedIndex() ) );
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildUi() {
	    LatLng cawkerCity = LatLng.newInstance(39.509, -98.434);
	    final MapWidget map = new MapWidget(cawkerCity, 2);
	    map.addControl(new LargeMapControl());
	    
		doStuff( map );

	    /*map.addOverlay(new Marker(cawkerCity));
	    map.getInfoWindow().open(map.getCenter(), new InfoWindowContent("World's Largest Ball of Sisal Twine"));
	    
	    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	    dock.addSouth(map, 500);

	    RootLayoutPanel.get().add(dock);*/
	}
}
