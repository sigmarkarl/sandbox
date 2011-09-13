package org.simmi.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Navisionexplorer implements EntryPoint {
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

	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	public class Person {
		String  name;
		String	kt;
		
		public String getName() {
			return name;
		}
		
		public String getKt() {
			return kt;
		}
		
		public Person( String name, String kt ) {
			this.name = name;
			this.kt = kt;
		}
	};
	
	class Job implements Comparable<Job> {
		String	name;
		double	hour;
		
		public Job( String name, double hour ) {
			this.name = name;
			this.hour = hour;
		}

		@Override
		public int compareTo(Job o) {
			return (int)(o.hour - hour);
		}
	};
	
	public void updateTable( List<Person> plist ) {
		for( Person p : plist ) {
			int r = data.getNumberOfRows();
			data.addRow();
			
			data.setValue( r, 0, p.getName() );
			data.setValue( r, 1, p.getKt() );
		}
		view = DataView.create( data );
		table.draw( view, options );
	}
	
	public void loadPersons( final List<Person> plist ) {
		RequestBuilder rb = new RequestBuilder( RequestBuilder.POST, "http://130.208.252.31/cgi-bin/lubbi" );
		try {
			String sql = "select [Name], [User ID] from [MATIS].[dbo].[User]";
			rb.sendRequest(sql, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String str = response.getText();
					String[] split = str.split("\n");
					plist.clear();
					for( String spl : split ) {
						String[] subsplit = spl.split("\t");
						if( subsplit.length == 2 ) {
							String name = subsplit[0];
							String kt = subsplit[1];
							if( kt.length() == 10 ) plist.add( new Person( name, kt ) );
						}
					}
					if( table != null ) updateTable( plist );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					console( exception.getMessage() );
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	DateBox before;
	DateBox after;
	public void loadJobs( final Collection<String>	set ) {
		Date befdate = before == null ? null : before.getDatePicker().getValue();
		Date aftdate = after == null ? null : after.getDatePicker().getValue();
		
		String aftstr = aftdate == null ? null : (aftdate.getYear()+1900) + "-" + (aftdate.getMonth()+1) + "-" + aftdate.getDate();
		String befstr = befdate == null ? null : (befdate.getYear()+1900) + "-" + (befdate.getMonth()+1) + "-" + befdate.getDate();
		String sql = "select j.[Description], jle.[Quantity] from [MATIS].[dbo].[Matís ohf_$Job]j, [MATIS].[dbo].[Matís ohf_$Job Ledger Entry] jle where j.[No_] = jle.[Job No_] and jle.[No_] in ('";
		for( String p : set ) {
			sql += p;
		}
		sql += "')";
		if( aftstr != null ) sql += " and jle.[Posting Date] >= '"+aftstr+"'";
		if( befstr != null ) sql += " and jle.[Posting Date] <= '"+befstr+"'";
		
		RequestBuilder rb = new RequestBuilder( RequestBuilder.POST, "http://130.208.252.31/cgi-bin/lubbi" );
		try {
			rb.sendRequest(sql, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					final List<Job>	jlist = new ArrayList<Job>();
					String str = response.getText();
					String[] split = str.split("\n");
					jlist.clear();
					for( String spl : split ) {
						String[] subsplit = spl.split("\t");
						if( subsplit.length == 2 ) {
							String name = subsplit[0];
							String d = subsplit[1];
							jlist.add( new Job( name, Double.parseDouble(d) ) );
						}
					}
					
					String person = null;
					for( String p : set ) {
						person = p;
						break;
					}
					
					List<Job> subwork = initPersonJob( jlist );
					pjMap.put( person, subwork );
					
					repaint( subwork );
					
					//if( pjMap.containsKey(key))
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					console( exception.getMessage() );
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	//Map<String,PersonJob>	pjMap = new HashMap<String,PersonJob>();

	String[] cc = new String[] { "#666666", "#DD6666", "#66DD66", "#6666DD", "#DDDD66", "#DD66DD", "#66DDDD", "#DDDDDD" };
	
	Map<String,List<Job>>	pjMap = new HashMap<String,List<Job>>();	
	//ByteArrayOutputStream	baos = new ByteArrayOutputStream();
	//PrintStream				ps = new PrintStream( baos );
	
	public List<Job> initPersonJob( List<Job> e ) {
		Map<String,Double>	work = new HashMap<String,Double>();
		for( Job j : e ) {
			if( work.containsKey(j.name) ) {
				double d = work.get( j.name );
				work.put( j.name, d+j.hour );
			} else {
				work.put( j.name, j.hour );
			}
		}
		
		List<Job>	subwork = new ArrayList<Job>();
		for( String name : work.keySet() ) {
			double d = work.get( name );
			subwork.add( new Job(name,d) );
		}
		
		Collections.sort( subwork );
		return subwork;
	}
	
	Canvas canvas;
	public void repaint( List<Job> subwork ) {
		Context2d context = canvas.getContext2d();
		double total = 0.0;
		
		for( Job j : subwork ) {
			total += j.hour;
		}
		
		int w = canvas.getCoordinateSpaceWidth();
		int h = canvas.getCoordinateSpaceHeight();
		
		int c = 0;
		double k = 0.0;
		double w2 = w/2.0;
		double h2 = h/2.0;
		double next = (int)((k*2.0*Math.PI)/total);
		for( int i = 0; i < (subwork.size()+1)/2; i++ ) {
			Job j = subwork.get(i);
			double d = j.hour;
			context.setFillStyle( cc[c] );
			
			double val = ((k+d)*2.0*Math.PI)/total;
			context.beginPath();
			context.arc( w2, h2, 250.0, next, val-next );
			context.closePath();
			context.fill();
			//g2.fillArc(w/2-250, h/2-250, 500, 500, next, val-next );
			next = val;
			
			context.setFillStyle( "#000000" );
			double u = k+d/2.0;
			if( u > 0.25*total && u < 0.75*total ) {
				TextMetrics tm = context.measureText( j.name );
				double strw = tm.getWidth();
				u += 0.5*total;
				context.translate( w2, h2 );
				context.rotate( -Math.PI*2.0*u/total );
				context.fillText(j.name, -255-strw, 0 );
				context.rotate( Math.PI*2.0*u/total );
				context.translate( -w2, -h2 );
			} else {
				context.translate( w2, h2 );
				context.rotate( -Math.PI*2.0*u/total );
				context.fillText(j.name, 255, 0 );
				context.rotate( Math.PI*2.0*u/total );
				context.translate( -w2, -h2 );
			}				
			k += d;
			c = (c+1)%cc.length;
			
			if( i != subwork.size()-i-1 ) {
				j = subwork.get(subwork.size()-i-1);
				d = j.hour;
				context.setFillStyle( cc[c] );
				
				val = (int)(((k+d)*360.0)/total);
				//g2.fillArc(w/2-250, h/2-250, 500, 500, next, val-next );
				
				context.beginPath();
				context.arc( w2, h2, 250.0, next, val-next );
				context.closePath();
				context.fill();
				
				next = val;
				context.setFillStyle( "#000000" );
				u = k+d/2.0;
				
				String name = j.name;
				double pc = j.hour/total;
				if( pc > 0.5 ) {
					double dval = Math.round( pc*10000.0 )/100.0;
					name = "(" + dval + ") "+name;
					//ps.printf("%.2f", (float)(pc*100.0) );
					//name = "(" + baos.toString() + ") "+name;
					//ps.flush();
					//baos.reset();
				}
				
				if( u > 0.25*total && u < 0.75*total ) {
					TextMetrics tm = context.measureText( j.name );
					double strw = tm.getWidth();
					u += 0.5*total;
					context.translate( w2, h2 );
					context.rotate( -Math.PI*2.0*u/total );
					context.fillText(j.name, -255-strw, 0.0 );
					context.rotate( Math.PI*2.0*u/total );
					context.translate( -w2, -h2 );
				} else {
					context.translate( w2, h2 );
					context.rotate( -Math.PI*2.0*u/total );
					context.fillText(j.name, 255, 0.0 );
					context.rotate( Math.PI*2.0*u/total );
					context.translate( -w2, -h2 );
				}				
				k += d;
				c = (c+1)%cc.length;
			}
		}
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		
		final DockLayoutPanel	dlp = new DockLayoutPanel( Unit.PX );
		final SplitLayoutPanel 	slp = new SplitLayoutPanel();
		
		HorizontalPanel	toolbar = new HorizontalPanel();
		
		ScriptElement	se = Document.get().createScriptElement();
		
		dlp.addNorth( toolbar, 25 );
		dlp.add( slp );
		
		final List<Person>	plist = new ArrayList<Person>();
		loadPersons( plist );
		
		canvas = Canvas.createIfSupported();
		canvas.setSize("100%", "100%");
		/*canvas.addAttachHandler( new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				int w = canvas.getOffsetWidth();
				int h = canvas.getOffsetHeight();
				
				console( w + " " + h );
				
				w = canvas.getCanvasElement().getWidth();
				h = canvas.getCanvasElement().getHeight();
				
				console( w + " n " + h );
				
				canvas.setCoordinateSpaceWidth( w );
				canvas.setCoordinateSpaceHeight( h );
			}
		});*/
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "Starfsmaður");
		    	data.addColumn( ColumnType.STRING, "Kennitala");
		    	  
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
		    	  
		    	view = DataView.create( data );
		    	table = new Table( view, options );
		    	
		    	table.addSelectHandler( new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						Set<String>	pkt = new HashSet<String>();
						
						String name = "";
						JsArray<Selection> jsel = table.getSelections();
						for( int i = 0; i < jsel.length(); i++ ) {
							Selection sel = jsel.get( i );
							int r = sel.getRow();
							name += data.getValueString(r, 0);
							String kt = data.getValueString(r, 1);
							pkt.add(kt);
						}
						
						String person = null;
						for( String p : pkt ) {
							person = p;
							break;
						}
						
						Context2d context = canvas.getContext2d();
						int w = canvas.getCoordinateSpaceWidth();
						int h = canvas.getCoordinateSpaceHeight();
						context.clearRect(0, 0, w, h);
						context.fillText(name, 10, 30);
						
						if( pjMap.containsKey(person) ) {
							List<Job> subwork = pjMap.get(person);
							repaint( subwork );
						} else {
							loadJobs( pkt );
						}
					}
		    	});
		    	
		    	ResizeLayoutPanel rlp = new ResizeLayoutPanel();
		    	//rlp.setSize("100%", "100%");
		    	rlp.add( table );
		    	rlp.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						int w = event.getWidth();
						int h = event.getHeight();
						
						table.setSize(w+"px", h+"px");
						//if( w != table.getOffsetWidth() ) table.setWidth(w+"px");
						//if( h != table.getOffsetHeight() ) table.setHeight(h+"px");
						table.draw( view, options );
						//if( w != table.getOffsetWidth() ) table.setWidth(w+"px");
						//if( h != table.getOffsetHeight() ) table.setHeight(h+"px");
						table.setSize(w+"px", h+"px");
						
						w = canvas.getOffsetWidth();
						h = canvas.getOffsetHeight();
						canvas.setCoordinateSpaceWidth( w );
				    	canvas.setCoordinateSpaceHeight( h );
					}
		    	});
		    	
		    	  int w = Window.getClientWidth()-200;
		    	  int h = Window.getClientHeight();
		    	  canvas.setCoordinateSpaceWidth( w );
		    	  canvas.setCoordinateSpaceHeight( h );
		    	  
		    	  slp.addWest( rlp, 200.0 );
		    	  slp.add( canvas );
		      }
		    };
		    VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		    
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = canvas.getOffsetWidth();
				int h = canvas.getOffsetHeight();
				canvas.setCoordinateSpaceWidth( w );
				canvas.setCoordinateSpaceHeight( h );
			}
		});
		
		rp.add( slp );
	}
}
