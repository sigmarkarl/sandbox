package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Naclgwt implements EntryPoint {
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

	
	public native void dropHandler( JavaScriptObject dataTransfer ) /*-{			
		$wnd.console.log("dropp");
		var files = dataTransfer.files;		
		var count = files.length;
		
		if(count > 0) {
			var file = files[0];
			var reader = new FileReader();
			reader.onload = function(e) {
				var res = e.target.result;
				$wnd.postMessage( res );
			};
			reader.readAsArrayBuffer( file );
		}
	}-*/;
	
	public void subinit() {
		final RootPanel	rp = RootPanel.get();
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		
		rp.setSize(w+"px", h+"px");
		Window.addResizeHandler( new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		FormPanel fp = new FormPanel();
		fp.setSize("100%", "100%");
		final FileUpload	file = new FileUpload();
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleFiles( file.getElement(), 0 );
			}
		});
		Style st = file.getElement().getStyle();
		st.setVisibility( Visibility.HIDDEN );
		textarea.setSize("800px", "600px");
		textarea.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				dropHandler( event.getDataTransfer() );
			}
		});
		  textarea.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {}
		  });
		  textarea.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {}	    		  
		  });
		  textarea.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {}
		  });
		  textarea.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {}
		  });
		  textarea.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {}
		  });
		  textarea.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {}
		  });
		textarea.addKeyPressHandler( new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char cc = event.getCharCode();
				if( cc == '\r' ) {
					String val = textarea.getText();
					int i = val.lastIndexOf('\n');
					String last = "";
					if( i != -1 ) {
						last = val.substring(i+1, val.length());
					} else {
						last = val.substring(0, val.length());
					}
					//console( last );
					if( last.startsWith("fileread") ) {
						click( file.getElement() );
					} else if( last.startsWith("line") ) {
						String[] split = last.split( "[(, )]" );
						final PopupPanel	pp = new PopupPanel( true );
						pp.setSize("800px", "600px");
						pp.setPopupPositionAndShow( new PositionCallback() {
							@Override
							public void setPosition(int offsetWidth, int offsetHeight) {
								pp.setPopupPosition( (int)(Window.getClientWidth()*0.1), (int)(Window.getClientHeight()*0.1) );
							}
						});
						String name = "";
						if( split.length > 0 ) name = split[1];
						String a1 = "";
						if( split.length > 1 ) a1 = split[2];
						String a2 = "";
						if( split.length > 2 ) a2 = split[3];
						line( name, pp.getElement(), (int)(Window.getClientWidth()*0.8), (int)(Window.getClientHeight()*0.8), a1, a2 );
					} else if( last.startsWith("loadimage") ) {
						loadimage();
					} else if( last.startsWith("plot") ) {
						postMessage( "fetch" );
						//line();
					} else postMessage( last );
				}
			}
		});
		VerticalPanel vp = new VerticalPanel();
		fp.add( vp );
		vp.add( textarea );
		vp.add( file );
		rp.add( fp );
	}
	
	public native void init() /*-{
		if ($wnd.simmiModule == null) {
        	$wnd.updateStatus('LOADING...');
    	} else {
        	$wnd.updateStatus();
    	}
		
		var ths = this;
		$doc.appendText = function( str ) {
			ths.@org.simmi.client.Naclgwt::appendText(Ljava/lang/String;)( str );
		};
	}-*/;
	
	public void appendText( String str ) {
		textarea.setText( textarea.getText()+str );
	}
	
	public native void resize( int size, int type ) /*-{
		if( type == 8 ) $wnd.current = new Int8Array( size );
		else if( type == 9 ) $wnd.current = new Uint8Array( size );
		else if( type == 16 ) $wnd.current = new Int16Array( size );
		else if( type == 17 ) $wnd.current = new Uint16Array( size );
		else if( type == 32 ) $wnd.current = new Int32Array( size );
		else if( type == 33 ) $wnd.current = new Uint32Array( size );
		else if( type == 34 ) $wnd.current = new Float32Array( size );
		else if( type == 66 ) $wnd.current = new Float64Array( size );
		//$wnd.curstruct = size + (type<<32);
		//$wnd.console.log( "siztyp " + $wnd.curstruct + "  " + size + "  " + type + " " + (type<<2) );
		
		$wnd.postMessage( $wnd.current.buffer );
		$wnd.postMessage( "type "+type );
	}-*/;
	
	public native void print() /*-{
		var str = "\n";
		for( i = 0; i < $wnd.current.length; i++ ) {
			str += $wnd.current[i] + "\t";
		}
		this.@org.simmi.client.Naclgwt::appendText(Ljava/lang/String;)( str );
	}-*/;

	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	public native void postMessage( String message ) /*-{
		$wnd.postMessage( message );
	}-*/;
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	public native void loadimage() /*-{
		var hthis = this;
		
		$wnd.console.log("erm");
		var b = new Blob( $wnd.current );
		$wnd.console.log("erm2");
		var f = new FileReader();
		$wnd.console.log("erm3");
		f.onerror = function(e) {
		  	$wnd.console.log("error");
			$wnd.console.log(e.getMessage());
		};
		f.onload = function(e) {
			hthis.@org.simmi.client.Naclgwt::handleImage(Ljava/lang/String;)(e.target.result);
		};
		f.readAsDataURL( b );
	}-*/;
	
	public void handleText( String text ) {
		
	}
	
	public native void postimage( ImageData id ) /*-{
		$wnd.postMessage( id.data );
	}-*/;
	
	public void handleImage( String dataurl ) {
		Image img = new Image( dataurl );
		Canvas c = Canvas.createIfSupported();
		c.setCoordinateSpaceWidth( img.getWidth() );
		c.setCoordinateSpaceHeight( img.getHeight() );
		Context2d c2 = c.getContext2d();
		c2.drawImage( (ImageElement)img.getElement().cast(), 0, 0 );
		ImageData id = c2.getImageData(0, 0, img.getWidth(), img.getHeight());
		
		postimage( id );
	}
	
	public void drawLine() {
		//LineChart lc = new LineChart();
	}
	
	public native void line( String name, Element popup, int width, int height, String ax, String ay ) /*-{
		$wnd.currentFunc = function( arraybuf ) {
			var arr = [ [ax, ay] ];
			var fa = new Float64Array( arraybuf );
			for( i = 0; i < fa.length; i++ ) {
				arr[i+1] = [i, fa[i]];
			}
	        var data = $wnd.google.visualization.arrayToDataTable(arr);
	        var options = {
	          title: name
	        };
	        options['width'] = width;
	        options['height'] = height;
			
	        var chart = new $wnd.google.visualization.LineChart( popup );
	        chart.draw(data, options);
		}
		$wnd.postMessage( "current" );
	}-*/;
	
	public native String handleFiles( Element ie, int append ) /*-{	
		var hthis = this;
		file = ie.files[0];
		var reader = new FileReader();
		reader.onerror = function(e) {
		  	$wnd.console.log("error", e);
			$wnd.console.log(e.getMessage());
		};
		reader.onload = function(e) {
			$wnd.current = e.target.result;
			$wnd.postMessage(e.target.result);
		};
		reader.readAsArrayBuffer( file );
	}-*/;
	
	/*if( file.fileName.endsWith('.png') ) {
		reader.onload = function(e) {
			hthis.@org.simmi.client.Naclgwt::handleBinary(Ljava/lang/String;)(e.target.result);
		};
		reader.readAsDataURL( file );
	} else {
		reader.onload = function(e) {
			hthis.@org.simmi.client.Naclgwt::handleText(Ljava/lang/String;)(e.target.result);
		};
		reader.readAsText( file, "utf8" );
	}*/
	final TextArea	textarea = new TextArea();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		init();
		subinit();
	}
}
