package org.simmi.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.events.SortHandler;
import com.google.gwt.visualization.client.visualizations.Table;
import com.googlecode.gwtgl.array.Float32Array;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwtgl implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	private WebGLRenderingContext glContext;
    private WebGLProgram shaderProgram;
    private int vertexPositionAttribute;
    private WebGLBuffer vertexBuffer;
    
    Context2d 			context;
    Context2d 			ocontext;
    //static final int 	canvasHeight = 2000;
    //static final int 	canvasWidth = 2000;
    
    private Table.Options createTableOptions() {
	    options = Table.Options.create();
	    
	    //options.setWidth("200px");
	    //options.setHeight("200px");
	    //options.set
	    //options.set
	    //options.set3D(true);
	    //options.setTitle("My Daily Activities");
	    //options.seta
	    return options;
	}
	
	private AbstractDataTable createTable() {
	    data = DataTable.create();
	    data.addColumn(ColumnType.STRING, "Name");
	    data.addColumn(ColumnType.NUMBER, "Length");
	    data.addRows(2);
	    //data.
	    data.setValue(0, 0, "wrk");
	    data.setValue(0, 1, 14);
	   
	    //data.set
	    //data.setFormattedValue(1, 0, "<a href=\"http://www.google.com\">Sleep2</a>");
	    //data.setValue(1, 1, 10);
	    return data;
	}
	
	/*public void stuff() {
		canvas = Canvas.createIfSupported();
		
		if (canvas == null) {
            RootPanel.get().add(new Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
            return;
		}
		
		canvas.setStyleName("mainCanvas");
		canvas.setWidth(canvasWidth + "px");
		canvas.setCoordinateSpaceWidth(500);
		
		canvas.setHeight(canvasHeight + "px");
		canvas.setCoordinateSpaceHeight(500);
		
		context = canvas.getContext2d();
		
		/*final Timer timer = new Timer() {
		    @Override
		    public void run() {
		        drawSomethingNew();
		    }
		};
		timer.scheduleRepeating(1500);*
		
		
		//context.fillText("hohohoho", 10, 10);
	}*/
	
	public native void scrollEv( JavaScriptObject te ) /*-{
		//$wnd.alert('hook');
		//for(var key in te){
      	//	if( typeof te[key] == 'function' ) console.log(key);
		//}
		
		//var oldfun = te.onscroll;
		var hthis = this;
		te.onscroll = function() {
			//oldfun();
			hthis.@org.simmi.client.Gwtgl::draw()();
		};
		
	}-*/;
	
	public native String handleFiles( Element ie, int append ) /*-{		
		$wnd.console.log('dcw');
		var hthis = this;
		file = ie.files[0];
		var reader = new FileReader();
		reader.onload = function(e) {
			hthis.@org.simmi.client.Gwtgl::fileLoaded(Ljava/lang/String;I)(e.target.result, append);
		};
		reader.onerror = function(e) {
      		$wnd.console.log("error", e);
      		$wnd.console.log(e.getMessage());
    	};
    	$wnd.console.log('befreadastext');
		reader.readAsText( file, "utf8" );
		$wnd.console.log('afterreadastext');
	}-*/;
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	String[] 	val;
	int			max = 0;
	public void fileLoaded( String content, int append ) {
		console("befread");
		String[] split = content.split(">");
		
		max = 0;
		
		if( append == 0 ) {
			data = DataTable.create();
	    	data.addColumn(ColumnType.STRING, "Name");
	    	data.addColumn(ColumnType.NUMBER, "Length");
		}
		int start = data.getNumberOfRows();
		data.addRows( split.length-1 );
		val = new String[ split.length-1 ];
		console("befloop");
		for( int r = 0; r < split.length-1; r++ ) {
			String s = split[r+1];
			int i = s.indexOf('\n');
			String seq = s.substring(i, s.length()).replace("\n", "");
			val[r] = seq;
			if( seq.length() > max ) max = seq.length();
			
			data.setValue( r+start, 0, s.substring(0, i) );
			data.setValue( r+start, 1, seq.length() );
			
			console("inloop "+i);
		}
		
		//table.setWidth("200px");
		//table.setHeight("200px");
		table.draw(data, createTableOptions());
		draw();
		
		Element e = table.getElement();        
        e = e.getFirstChildElement();
        e = e.getFirstChildElement();
        scrollEv( e );
	}
	
	public Node nodeRecursive( Node n ) {
		NodeList<Node> nlf = n.getChildNodes();
		for( int i = 0; i < nlf.getLength(); i++ ) {
			Node nn = nlf.getItem(i);
			if( nn.getNodeName().toLowerCase().contains("table") ) return n;
		}
		return null;
	}
	
	/*public void odraw() {
		if( val != null ) {
			ocontext.setFillStyle("#CCFFCC");
			//ocontext.fillRect(20, 20, 100, 100);
			
			//ocontext.setStrokeStyle("#CCFFCC");
			//ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
			//ocontext.setLineWidth(2.0);
			for( int i = 0; i < val.length; i++ ) {		
				int y = sortind != null ? sortind.get(i) : i;
				String seq = val[y];
				//ocontext.moveTo(0, i);
				int x = seq.length()*ocontext.getCanvas().getClientWidth()/max;
				//ocontext.lineTo( x, i );
				ocontext.fillRect(0, i, x, 1);
			}
		}
	}*/
	
	int xstart = 0;
	public void draw() {
		if( val != null ) {
			CanvasElement canvas = context.getCanvas();
			CanvasElement ocanvas = ocontext.getCanvas();
			
			context.setFillStyle("#222222");
			
			//table.gete
			NodeList<Element> ne = table.getElement().getElementsByTagName("table");
			TableElement te = TableElement.as( ne.getItem(0) );
			NodeList<TableRowElement> nl = te.getRows();
			
			int top = table.getElement().getFirstChildElement().getFirstChildElement().getScrollTop();
			//console( "ok " + table.getElement().getFirstChildElement().getFirstChildElement().getOffsetTop() );
			int th = 0;
			context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			Set<Integer>	selset = new HashSet<Integer>();
			JsArray<Selection> jsel = table.getSelections();
			for( int i = 0; i < jsel.length(); i++ ) {
				Selection sel = jsel.get(i);
				selset.add( sel.getRow() );
			}
			//for( int y = 0; y < Math.min( val.length, canvas.getHeight()/10 ); y++ ) {
			for( int y = 0; y < val.length; y++ ) {
				int r = sortind != null ? sortind.get(y) : y;
				String str = val[r];
				TableRowElement	tre = nl.getItem(y+1);
				
				if( th+tre.getOffsetHeight() > top ) {		
					//console( tre.getRowIndex() + " " + tre.getId() + " " + tre.getClassName() + " " + tre.getNodeValue() );
					if( selset.contains(r) ) {
						context.setFillStyle("#DDDDFF");
						context.fillRect(0, th-top, canvas.getWidth(), tre.getClientHeight() );
						context.setFillStyle("#222222");
					}
					for( int x = xstart; x < Math.min( str.length(), xstart+canvas.getWidth()/10+1 ); x++ ) {
						context.fillText(""+str.charAt(x), (x-xstart)*10, th+tre.getOffsetHeight()-top );
					}
				}
				th += tre.getOffsetHeight();
			}
			
			ocontext.clearRect(0, 0, ocanvas.getWidth(), ocanvas.getHeight());
			ocontext.setFillStyle("#CCFFCC");
			//ocontext.fillRect(20, 20, 100, 100);
			
			//ocontext.setStrokeStyle("#CCFFCC");
			//ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
			//ocontext.setLineWidth(2.0);
			for( int i = 0; i < val.length; i++ ) {		
				int y = sortind != null ? sortind.get(i) : i;
				String seq = val[y];
				//ocontext.moveTo(0, i);
				int x = seq.length()*ocanvas.getWidth()/max;
				//ocontext.lineTo( x, i );
				ocontext.fillRect(0, i, x, 1);
			}
			
			ocontext.setFillStyle("#333333");
			int w = ( canvas.getWidth()*ocanvas.getWidth() ) / (max*10);
			ocontext.fillRect(xstart*ocanvas.getWidth()/(max), 0, w, 20);
		}
	}
	
	public native JsArrayInteger getSortInfo( JavaScriptObject t ) /*-{		
		var si = t.getSortInfo();
		return si.sortedIndexes;
	}-*/;
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	public void stuff( final int append ) {
		final DialogBox	db = new DialogBox();
		db.setText("Open file ...");
		
		//HorizontalPanel	vp = new HorizontalPanel();
		final FormPanel	form = new FormPanel();
		form.setAction("/");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    
		//form.add( vp );*/
		final FileUpload	file = new FileUpload();
		file.getElement().setId("fasta");

		form.add( file );
		db.add( form );
		/*file.fireEvent( GwtEvent)
		
		//SubmitButton	submit = new SubmitButton();	
		//submit.setText("Open");*/
		
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleFiles( file.getElement(), append );
				db.hide();
			}
		});
		
		db.setAutoHideEnabled( true );
		db.center();
		//file.fireEvent( new ClickEvent(){} );
		click( file.getElement() );
	}

	Table 				table;
	DataTable 			data;
	Table.Options 		options;
	JsArrayInteger		sortind = null;
	boolean				mouseDown = false;
	public void onModuleLoad() {		
		HorizontalPanel	hpanel = new HorizontalPanel();
		hpanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		hpanel.setWidth("100%");
		
		int height = Math.max( 1000, Window.getClientHeight() );
		//int height = RootPanel.get().getOffsetHeight();
		//hpanel.setHeight(Math.max(1000, height)+"px");
		
		final SplitLayoutPanel slp = new SplitLayoutPanel();
		slp.setWidth("100%");
		slp.setHeight(height+"px");
		
		MenuBar	popup = new MenuBar(true);
		popup.addItem("Open", new Command() {
			@Override
			public void execute() {
				stuff( 0 );
				
				//db.hide();
				
				/*submit.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						form.submit();
						db.hide();
					}
				});*/
				/*form.addSubmitHandler( new SubmitHandler() {
					@Override
					public void onSubmit(SubmitEvent event) {
						//File f;
						try {
							tryOut( file.getElement() );
							//table = new Table( data, createTableOptions() );
						    //table.setWidth("100%");
						    //table.setHeight("100%");
						    
						    //slp.add
							
							//String str = new String( bb );
							//System.err.println( str );
							//GWT.log( filename );
						} catch( Exception e ) {
							GWT.log( "ex", e );
							//e.printStackTrace();
						}
					}
				});*/
				/*form.addSubmitCompleteHandler( new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						Window.alert("erm");
					}
				});*/
				
				//vp.add( file );
				//vp.add( submit );
				//db.add( form );
				//db.setAutoHideEnabled( true );
				//db.center();
			}
		});
		popup.addItem( "Append", new Command() {
			@Override
			public void execute() {
				stuff( 1 );
			}
		});
		MenuBar	hpopup = new MenuBar(true);
		hpopup.addItem("About", new Command() {
			@Override
			public void execute() {
				DialogBox	db = new DialogBox();
				db.setText("WebFasta");
				db.setAutoHideEnabled( true );
				//SimplePanel	simplepanel = new SimplePanel();
				//Text simplepanel = new Text();
				//Panel
				HTMLPanel	html = new HTMLPanel("WebFasta 1.0<br>Sigmasoft LTD.");
				db.add( html );
				//db.setHTML("WebFasta 1.0<br>Sigmasoft LTD.");
				db.center();
			}
		});
		
		MenuBar	menubar = new MenuBar();
		menubar.addItem("File", popup);
		menubar.addItem("Help", hpopup);
		VerticalPanel	vpanel = new VerticalPanel();
		vpanel.setWidth("100%");
		vpanel.add( menubar );
		vpanel.add( slp );
		
		final Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth("100%");
		canvas.setHeight("100%");
		context = canvas.getContext2d();
		
		final ResizeLayoutPanel	sp = new ResizeLayoutPanel();
		sp.setWidth("100%");
		sp.setHeight("100%");
		sp.add( canvas );
		
		final ResizeLayoutPanel	tableresize = new ResizeLayoutPanel();
		tableresize.setWidth("100%");
		tableresize.setHeight("100%");
		
		ResizeHandler rh = new ResizeHandler() {		
			@Override
			public void onResize(ResizeEvent event) {
				//canvas.setWidth(100+i+"px");
				//canvas.setHeight(100+"px");
				
				//canvas.setCoordinateSpaceWidth( canvas.getOffsetWidth() );
				//canvas.setCoordinateSpaceHeight( canvas.getOffsetHeight() );
				
				canvas.setCoordinateSpaceWidth( canvas.getElement().getClientWidth() );
				canvas.setCoordinateSpaceHeight( canvas.getElement().getClientHeight() );
				//context.setFillStyle("#00ff00");
				//context.fillRect(10, 10, 50, 50);
				draw();
				
				/*Canvas newcanvas = Canvas.createIfSupported();
				Context2d context = newcanvas.getContext2d();
				//context.clearRect( 0, 0, 1000, 1000 );
				context.setFillStyle("#00ff00");
				context.fillRect(10, 10, 100, 100);
				if( canvas != null ) {
					canvas.removeFromParent();
				}
				sp.add( newcanvas );
				newcanvas.setWidth("100%");
				newcanvas.setHeight("100%");*/
			
				//canvas = newcanvas;
			}
		};
		sp.addHandler( rh, ResizeEvent.getType() );
		//canvas.addHandler( rh, ResizeEvent.getType() );
		
		tableresize.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				String w = tableresize.getOffsetWidth()+"px";
				String h = tableresize.getOffsetHeight()+"px";
				
				options.setWidth( w );
				options.setHeight( h );
				//options.setSortColumn(table.)
				//table.draw( data, options );
				table.setWidth( w );
				table.setHeight( h );
				table.draw( data, options );
				
				Element e = table.getElement();        
		        e = e.getFirstChildElement();
		        e = e.getFirstChildElement();
		        scrollEv( e );
			}
		});
		
		final Canvas overview = Canvas.createIfSupported();
		overview.setWidth("100%");
		overview.setHeight("100%");
		
		overview.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( max > 0 ) {
					int w = ( overview.getOffsetWidth()*canvas.getOffsetWidth() ) / (max*10);
					int val = ( (event.getX()-w/2)*max ) / overview.getOffsetWidth();
					xstart = Math.max( 0, Math.min( val, max ) );
					draw();
				}
			}
		});
		
		overview.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
			}
		});
		
		overview.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mouseDown = false;
			}
		});
		
		overview.addMouseMoveHandler( new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if( mouseDown ) { //event.getNativeButton() == NativeEvent.BUTTON_LEFT ) {
					int w = ( overview.getOffsetWidth()*canvas.getOffsetWidth() ) / (max*10);
					int val = ( (event.getX()-w/2)*max ) / overview.getOffsetWidth();
					xstart = Math.max( 0, Math.min( val, max ) );
					draw();
				}
			}
		});
		
		//overview.addMouseDownHandler( new )
		
		final ResizeLayoutPanel	roverview = new ResizeLayoutPanel();
		roverview.add( overview );
		
		ocontext = overview.getContext2d();
		ocontext.setFillStyle("#0000ff");
		//ocontext.fillRect(10, 10, 100, 100);
		
		hpanel.add( vpanel );
		RootPanel.get().add( hpanel );
		
		/*//HorizontalSplitPanel hsp = new HorizontalSplitPanel();
		slp.setWidth("1200px");
		slp.setHeight("400px");
		//final HorizontalPanel	horp = new HorizontalPanel();
		stuff();
		final ScrollPanel	sp = new ScrollPanel( canvas );
		//sp.setWidth("10000px");
		//sp.setHeight("200px");
		
		//final ScrollPanel	spheader = new ScrollPanel();
		//spheader.setWidth("200px");
		//spheader.setHeight("200px");*/
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
		        //Panel panel = RootPanel.get();		        
		        //AbstractDataTable abs = new AbstractDataTable() {
		        	
		        //};
		        table = new Table( createTable(), createTableOptions() );
		        //table.setWidth("100%");
		        //table.setHeight("100%");
		        //Table table = new Table();
		        table.setTitle("erm");
		        
		        /*Event.ons
		        table.addHandler( new ScrollHandler() {
					@Override
					public void onScroll(ScrollEvent event) {
						console("scrolling");
					}
		        }, ScrollEvent.getType() );*/
		        
		        table.addSelectHandler( new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						draw();
					}
		        });
		        
		        table.addSortHandler( new SortHandler() {
					@Override
					public void onSort(SortEvent event) {
						sortind = getSortInfo( table.getJso() );
						draw();
						
						Element e = table.getElement();        
				        e = e.getFirstChildElement();
				        e = e.getFirstChildElement();
				        scrollEv( e );
					}
		        });
		        
		        /*sp.addHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						//fl.publish( new LogRecord( Level.INFO, "res") );
						int val = 1200-event.getWidth();
						Window.alert(""+val);
						table.setWidth(val+"px");
					}
				}, ResizeEvent.getType() );*/
		        
		        /*slp.addHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						//fl.publish( new LogRecord( Level.INFO, "res") );
						int val = 1200-event.getWidth();
						Window.alert("me "+val);
						table.setWidth(val+"px");
					}
				}, ResizeEvent.getType() );*/
		        
		        tableresize.add( table );
		        
		        slp.addSouth( roverview, 100 );
		        slp.addWest( tableresize, 200 );
		        slp.add( sp );
		        
		        roverview.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						overview.setCoordinateSpaceWidth( overview.getOffsetWidth() );
						overview.setCoordinateSpaceHeight( overview.getOffsetHeight() );
						draw();
					}
		        } );
		        
		        //console( table.getElement().getClassName() + " 1 " + table.getElement().getParentElement().getClassName() + " 2 " + table.getElement().getParentElement().getParentElement().getClassName() );
		        //NodeList<Element> ne = table.getElement().getElementsByTagName("table");
		        //table.getElement().getElementsByTagName(name)
				//TableElement te = TableElement.as( ne.getItem(0) );
		        //scrollEv( (JavaScriptObject)ne.getItem(0).getParentElement().getParentElement() );
		        //slp.add( sp );
		        //horp.add(table);
				//horp.add(sp);
		      }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		/*RootPanel.get().add( slp );
		
		/*final WebGLCanvas webGLCanvas = new WebGLCanvas("500px", "500px");
        glContext = webGLCanvas.getGlContext();
        glContext.viewport(0, 0, 500, 500);
        RootPanel.get("gwtGL").add(webGLCanvas);
        start();*/
	}
	
	/*public void drawSomethingNew() {
        // Get random coordinates and sizing
        int rndX = Random.nextInt(canvasWidth);
        int rndY = Random.nextInt(canvasHeight);
        int rndWidth = Random.nextInt(canvasWidth);
        int rndHeight = Random.nextInt(canvasHeight);

        // Get a random color and alpha transparency
        int rndRedColor = Random.nextInt(255);
        int rndGreenColor = Random.nextInt(255);
        int rndBlueColor = Random.nextInt(255);
        double rndAlpha = Random.nextDouble();

        CssColor randomColor = CssColor.make("rgba(" + rndRedColor + ", " + rndGreenColor + "," + rndBlueColor + ", " + rndAlpha + ")");

        context.setFillStyle(randomColor);
        context.fillRect( rndX, rndY, rndWidth, rndHeight);
        context.fill();
    }*/
	
	private void drawScene() {
        glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT);
        //float[] perspectiveMatrix = createPerspectiveMatrix(45, 1, 0.1f, 1000);
        WebGLUniformLocation uniformLocation = glContext.getUniformLocation(shaderProgram, "perspectiveMatrix");
        //glContext.uniformMatrix4fv(uniformLocation, false, perspectiveMatrix);
        glContext.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
        glContext.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 3);
	}
	
	private void start() {
        //initShaders();
        glContext.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glContext.clearDepth(1.0f);
        glContext.enable(WebGLRenderingContext.DEPTH_TEST);
        glContext.depthFunc(WebGLRenderingContext.LEQUAL);
        initBuffers();

        drawScene();
	}
	
	private void initBuffers() {
        vertexBuffer = glContext.createBuffer();
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vertexBuffer);
        float[] vertices = new float[]{
                         0.0f,  1.0f,  -5.0f, // first vertex
                        -1.0f, -1.0f,  -5.0f, // second vertex
                         1.0f, -1.0f,  -5.0f  // third vertex
        };
        glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER, Float32Array.create(vertices), WebGLRenderingContext.STATIC_DRAW);
	}
}
