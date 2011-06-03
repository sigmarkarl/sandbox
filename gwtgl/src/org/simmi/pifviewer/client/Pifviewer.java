package org.simmi.pifviewer.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Pifviewer implements EntryPoint {
	public native void dropTarget( JavaScriptObject canvas, JavaScriptObject id ) /*-{
		var s = this;
		//jso.ondrop = function() {
		//	$wnd.alert('alert');
		//	s.@org.simmi.client.Pifviewer::slubb()();
		//};
		
		function f1( evt ) {
			evt.stopPropagation();
			evt.preventDefault();
			
			//$wnd.alert("hey");
		};
		
		function f( evt ) {
			evt.stopPropagation();
			evt.preventDefault();
 
			var files = evt.dataTransfer.files;
			var count = files.length;
 
			if(count > 0) {
				var file = files[0];
				var reader = new FileReader();
				reader.onload = function(e) {
					//var iv;
					//try {
					//	var res = e.target.result;
					//	iv = new Int16Array( res );
					//} catch( e ) {
					//	$wnd.alert(e);
					//}
					var res = e.target.result;
					//s.@org.simmi.client.Pifviewer::loadImage(Lcom/google/gwt/dom/client/CanvasElement;Lcom/google/gwt/core/client/JavaScriptObject;)( canvas, iv );
					s.@org.simmi.pifviewer.client.Pifviewer::loadImage(Lcom/google/gwt/dom/client/CanvasElement;Ljava/lang/String;Lcom/google/gwt/canvas/dom/client/ImageData;)( canvas, res, id );
				};
				//reader.readAsArrayBuffer( file );
				reader.readAsBinaryString( file );
			}
		};
		canvas.addEventListener("dragenter", f1, false );
		canvas.addEventListener("dragexit", f1, false );
		canvas.addEventListener("dragover", f1, false );
		canvas.addEventListener("drop", f, false );
	}-*/;
	
	public native int bb( JavaScriptObject jo, int ind ) /*-{
		return jo.charCodeAt(ind);
	}-*/;
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	int w = 4096;//Window.getClientWidth();
	int h = 4096;//Window.getClientHeight();
	public void loadImage( CanvasElement ce, String binaryString, ImageData id ) {
		Context2d context = ce.getContext2d();
		
		//ByteBuffer buf = ByteBuffer.wrap( bb, 12, bb.length-12 );
		//buf.order( ByteOrder.LITTLE_ENDIAN );
		//ShortBuffer sb = buf.asShortBuffer();
		int max = 0;
		
		int[] hist = new int[256];
		for( int i = 0; i < hist.length; i++ ) {
			hist[i] = 0;
		}
		
		for( int i = 12; i < binaryString.length(); i+=2 ) {
			int one = binaryString.charAt(i*2);
			int two = binaryString.charAt(i*2+1);
			int s = (one + (two*256))&16383;
			
			hist[(s*256)/16384]++;
		}
		
		for( int i = 1; i < hist.length; i++ ) {
			hist[i] += hist[i-1];
		}
		
		int tot = w*h;
		int start = hist[0];
		
		//console( tot + " " + hist[hist.length-1] );
		for( int y = 0; y < h; y++ ) {
			for( int x = 0; x < w; x++ ) {
				int ind = y*w+x;
				
				int rind = 12+2*ind;
				//int one = bb(binaryString, rind);
				//int two = bb(binaryString, rind+1);
				int one = binaryString.charAt(rind);
				int two = binaryString.charAt(rind+1);
				int s = (one + (two*256))&16383;
				
				int ii = (256*s)/16834;
				int val = (256*(hist[ii]-start))/(tot-start+1);
				//console( s + " " + val );
				//int val = Math.min( 255, (s*255)/max );
				
				id.setBlueAt(val, x, y);
				id.setRedAt(val, x, y);
				id.setGreenAt(val, x, y);
				id.setAlphaAt(255, x, y);
			}
		}
		context.putImageData(id, 0, 0);
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel	rootpanel = RootPanel.get();
		final Canvas canvas = Canvas.createIfSupported();
		
		int cw = Window.getClientWidth();
		int ch = Window.getClientHeight();
		
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( h );
		canvas.setSize( cw+"px", ch+"px" );
		
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if( canvas.getOffsetHeight() == h ) {
					canvas.setSize( Window.getClientWidth()+"px", Window.getClientHeight()+"px" );
				} else {
					canvas.setSize( w+"px", h+"px" );
				}
			}
		});
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if( canvas.getOffsetHeight() != h ) {
					canvas.setSize( Window.getClientWidth()+"px", Window.getClientHeight()+"px" );
				}
			}
		});
		
		Context2d context2d = canvas.getContext2d();
		context2d.setFillStyle("#ccccff");
		context2d.fillRect( 0, 0, context2d.getCanvas().getWidth(), context2d.getCanvas().getHeight() );
		String message = "Dragdrop pif file here";
		context2d.setFillStyle("#000000");
		context2d.setFont("64pt Arial");
		TextMetrics tm = context2d.measureText( message );
		double strw = tm.getWidth();
		context2d.fillText( message, (w-strw)/2, (h-5)/2 );
		ImageData id = context2d.createImageData(w, h);
		
		dropTarget( context2d.getCanvas(), id );
		
		rootpanel.add( canvas );
	}
}