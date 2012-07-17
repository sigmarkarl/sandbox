package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
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
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Naclsimlab implements EntryPoint {
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
	
	public native void postParent( String from ) /*-{
		//var s = this;
		$wnd.addEventListener('message',function(event) {
			$wnd.console.log( event.origin );
			$wnd.console.log('message received from '+from);
			if(event.origin == 'http://'+from+'.appspot.com') {
				$wnd.console.log('correct origin');
				if( event.data instanceof ArrayBuffer ) $wnd.postMessage( event.data );
				//s.@org.simmi.client.Websimlab::handleText(Ljava/lang/String;)( event.data );
			}
		});
		$wnd.console.log('posting ready');
		if( $wnd.simmiModule != null ) $wnd.opener.postMessage('ready','http://'+from+'.appspot.com');
		else $wnd.message = 'http://'+from+'.appspot.com';
	}-*/;
	
	public native void save( String type, JavaScriptObject buf ) /*-{
		var ia = new Int8Array( buf );
		var b = new Blob( [ia], { "type" : type } );
		var f = new FileReader();
		f.onerror = function(e) {
			$wnd.console.log(e.getMessage());
		};
		f.onload = function(e) {
			var url = e.target.result;
			$wnd.open( url );
		};
		f.readAsDataURL( b );
	}-*/;
	
	public native void savecurrent( String type ) /*-{
		var s = this;
		$wnd.currentFunc = function( buf ) {
			s.@org.simmi.client.Naclsimlab::save(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)( type, buf );
			//save( type, buf );
		}
		$wnd.postMessage("current");
	}-*/;
	
	public native void imSet( ImageData id, JavaScriptObject buf ) /*-{
		var clamp = new Uint8ClampedArray( buf );
		id.data.set( clamp );
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
		textarea.setSize("728px", "512px");
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
					} else if( last.startsWith("image") ) {
						String[] 			split = last.split( "[(, )]" );
						if( split.length > 2 ) {
							final int 			b = Integer.parseInt( split[1] );
							final int 			w = Integer.parseInt( split[2] );
							image( b, w );
						}
					} else if( last.startsWith("line") ) {
						final String[] split = last.split( "[(, )]" );
						final PopupPanel	pp = new PopupPanel( true );
						pp.setSize("800px", "600px");
						pp.setPopupPositionAndShow( new PositionCallback() {
							@Override
							public void setPosition(int offsetWidth, int offsetHeight) {
								pp.setPopupPosition( (int)(Window.getClientWidth()*0.1), (int)(Window.getClientHeight()*0.1) );
								
								String name = "";
								if( split.length > 0 ) name = split[1];
								String a1 = "";
								if( split.length > 1 ) a1 = split[2];
								String a2 = "";
								if( split.length > 2 ) a2 = split[3];
								line( name, pp.getElement(), (int)(Window.getClientWidth()*0.8), (int)(Window.getClientHeight()*0.8), a1, a2 );
							}
						});
					} else if( last.startsWith("loadimage") ) {
						String type = "text/plain";
						String[]	split = last.split( "[(, )]" );
						if( split.length > 1 ) {
							type = split[1];
						}
						loadimage( type );
					} else if( last.startsWith("loadaudio") ) {
						String type = "text/plain";
						String[]	split = last.split( "[(, )]" );
						if( split.length > 1 ) {
							type = split[1];
						}
						loadaudio( type );
					} else if( last.startsWith("saveimage") ) {
						String type = "image/png";
						String[]	split = last.split( "[(, )]" );
						if( split.length > 1 ) {
							int w = Integer.parseInt( split[1] );
							if( split.length > 2 ) type = split[2];
							saveimage( type, w );
						}
					} else if( last.startsWith("save") ) {
						String type = "text/plain";
						String[]	split = last.split( "[(, )]" );
						if( split.length > 1 ) {
							type = split[1];
						}
						savecurrent( type );
					} else if( last.startsWith("plot") ) {
						postMessage( "fetch" );
						//line();
					} else postMessage( last );
				}
			}
		});
		
		SimplePanel	ads = new SimplePanel();
		Element adsElem = Document.get().getElementById("ads");
		adsElem.removeFromParent();
		ads.getElement().appendChild( adsElem );
		
		HorizontalPanel links = new HorizontalPanel();
		links.setSpacing(10);
		Anchor jsimlab = new Anchor( "javasimlab ", "http://wensimlab.appspot.com/Javasimlab.html" );
		links.add( jsimlab );
		Anchor mail = new Anchor( "| huldaeggerts@gmail.com", "mailto:huldaeggerts@gmail.com" );
		links.add( mail );
		
		VerticalPanel vp = new VerticalPanel();
		VerticalPanel subvp = new VerticalPanel();
		
		vp.setSpacing(0);
		subvp.setSpacing(0);
		
		vp.setSize( "100%", "100%" );
		
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		vp.add( subvp );
		fp.add( vp );
		subvp.add( ads );
		subvp.add( textarea );
		subvp.add( links );
		subvp.add( file );
		rp.add( fp );
	}
	
	public native void init() /*-{
		var ths = this;
		$doc.appendText = function( str ) {
			ths.@org.simmi.client.Naclsimlab::appendText(Ljava/lang/String;)( str );
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
		this.@org.simmi.client.Naclsimlab::appendText(Ljava/lang/String;)( str );
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
	
	public native void loadimage( String type ) /*-{
		var hthis = this;
		$wnd.currentFunc = function( buf ) {
			var ia = new Int8Array( buf );
			var b = new Blob( [ia], { "type" : type } );
			var f = new FileReader();
			f.onerror = function(e) {
				$wnd.console.log(e.getMessage());
			};
			f.onload = function(e) {
				hthis.@org.simmi.client.Naclsimlab::handleImage(Ljava/lang/String;)(e.target.result);
			};
			f.readAsDataURL( b );
		}
		$wnd.postMessage("current");
	}-*/;
	
	public native void loadaudio( String type ) /*-{
		var hthis = this;
		$wnd.currentFunc = function( buf ) {
			var ia = new Int8Array( buf );
			var b = new Blob( [ia], { "type" : type } );
			var f = new FileReader();
			f.onerror = function(e) {
				$wnd.console.log(e.getMessage());
			};
			f.onload = function(e) {
				hthis.@org.simmi.client.Naclsimlab::handleAudio(Ljava/lang/String;)(e.target.result);
			};
			f.readAsDataURL( b );
		}
		$wnd.postMessage("current");
	}-*/;
	
	public native void saveimage( String type, int w ) /*-{
		var s = this;
		$wnd.currentFunc = function( buf ) {
			var h = buf.byteLength / (4*w);
			$wnd.console.log( type );
			$wnd.console.log( w + " " + h );
			s.@org.simmi.client.Naclsimlab::saveImage(Ljava/lang/String;IILcom/google/gwt/core/client/JavaScriptObject;)( type, w, h, buf );
		}
		$wnd.postMessage("current");
	}-*/;
	
	public void saveImage( String type, int w, int h, JavaScriptObject arraybuffer ) {
		Canvas c = Canvas.createIfSupported();
		c.setSize(w+"px", h+"px");
		c.setCoordinateSpaceWidth(w);
		c.setCoordinateSpaceHeight(h);
		Context2d ctx = c.getContext2d();
		ImageData id = ctx.getImageData(0, 0, w, h);
		imSet( id, arraybuffer );
		ctx.putImageData( id, 0, 0 );
		String url = c.toDataUrl( type );
		
		Window.open( url, "_blank", "" );
	}
	
	public void handleText( String text ) {
		
	}
	
	public native void postimage( ImageData id ) /*-{
		$wnd.console.log( id.data.buffer );
		$wnd.current = id.data.buffer;
		$wnd.postMessage( id.data.buffer );
	}-*/;
	
	public native void imageLoad( Element im, Canvas c, Image img ) /*-{
		var s = this;
		im.onload = function() {
			s.@org.simmi.client.Naclsimlab::imgLoadFunc(Lcom/google/gwt/canvas/client/Canvas;Lcom/google/gwt/user/client/ui/Image;)(c, img);
		}
	}-*/;
	
	public void imgLoadFunc( Canvas c, Image img ) {
		console("hehe");
		
		c.setCoordinateSpaceWidth( img.getWidth() );
		c.setCoordinateSpaceHeight( img.getHeight() );
		Context2d c2 = c.getContext2d();
		c2.drawImage( (ImageElement)img.getElement().cast(), 0, 0 );
		ImageData id = c2.getImageData(0, 0, img.getWidth(), img.getHeight());
		
		appendText(img.getWidth()+" "+img.getHeight()+"\n");
		
		console("postimage");
		postimage( id );
	}
	
	public void handleImage( String dataurl ) {
		console("handling image");
		final Image img = new Image();
		final Canvas c = Canvas.createIfSupported();
		img.addLoadHandler( new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				imgLoadFunc( c, img );
			}
		});
		imageLoad( img.getElement(), c, img );
		img.setUrl( dataurl );
	}
	
	public void handleAudio( String dataurl ) {
		console("handling audio");
		final Audio audio = Audio.createIfSupported();
		audio.setSrc( dataurl );
		audio.load();
		/*audio.getAudioElement().
		final Canvas c = Canvas.createIfSupported();
		img.addLoadHandler( new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				imgLoadFunc( c, img );
			}
		});
		imageLoad( img.getElement(), c, img );
		img.setUrl( dataurl );*/
	}
	
	public void drawLine() {
		//LineChart lc = new LineChart();
	}
	
	public void imageInt( final int w, final int h, final JavaScriptObject arraybuffer ) {
		console("what the fuck");
		final PopupPanel	pp = new PopupPanel( true );
		final ScrollPanel	sp = new ScrollPanel();
		final Canvas 		c = Canvas.createIfSupported();
		pp.setSize(w+"px", h+"px");
		pp.setPopupPositionAndShow( new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {				
				pp.setPopupPosition( (int)(Window.getClientWidth()-w)/2, (int)(Window.getClientHeight()-h)/2 );
				
				c.setSize(w+"px", h+"px");
				c.setCoordinateSpaceWidth(w);
				c.setCoordinateSpaceHeight(h);
				Context2d ctx = c.getContext2d();
				ImageData id = ctx.getImageData(0, 0, w, h);
				imSet( id, arraybuffer );
				ctx.putImageData( id, 0, 0 );
				if( w > Window.getClientWidth() || h > Window.getClientHeight() ) {
					int neww = (int)(Window.getClientWidth()*0.8);
					int newh = (int)(Window.getClientHeight()*0.8);
					
					sp.setSize( neww+"px", newh+"px" );
					
					sp.add( c );
					pp.add( sp );
				} else {					
					pp.add( c );
				}
			}
		});
	}
	
	public native void image( int b, int w ) /*-{
		var s = this;
		$wnd.console.log( b + "  " + w );
		$wnd.currentFunc = function( buf ) {
			var cl = buf.byteLength;
			var h = cl/(w*b);
			s.@org.simmi.client.Naclsimlab::imageInt(IILcom/google/gwt/core/client/JavaScriptObject;)(w, h, buf);
		}
		$wnd.postMessage("current");
	}-*/;	
	
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
		
		if( Window.Location.getParameterMap().keySet().contains("callback") ) {
			postParent( Window.Location.getParameter("callback") );
		}
	}
}
