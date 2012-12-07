package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.CanvasElement;
import elemental.html.Console;
import elemental.html.WebGLBuffer;
import elemental.html.WebGLProgram;
import elemental.html.WebGLRenderingContext;
import elemental.html.WebGLShader;
import elemental.html.WebGLTexture;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Starwars implements EntryPoint {
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

	private WebGLShader getShader( WebGLRenderingContext gl, String id ) {
		  //var shaderScript, theSource, currentChild, shader;
		   
		ScriptElement shaderScript = ScriptElement.as( Document.get().getElementById(id) );
		   
		if( shaderScript == null ) {
			return null;
		}
		   
		String theSource = "";
		Node currentChild = shaderScript.getFirstChild();
		while( currentChild != null ) {
			if( currentChild.getNodeType() == Node.TEXT_NODE ) {
				theSource += currentChild.getNodeValue(); //.textContent;
			}
		     
		    currentChild = currentChild.getNextSibling();
		}
		
		WebGLShader shader;
		String shadertype = shaderScript.getType();
		if( "x-shader/x-fragment".equals( shadertype ) ) {
		    shader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER);
		} else if( "x-shader/x-vertex".equals( shadertype ) ) {
		    shader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER);
		} else {
			return null;
		}
		    
		gl.shaderSource(shader, theSource);
		gl.compileShader(shader);
		       
		if( gl.getShaderParameter(shader, WebGLRenderingContext.COMPILE_STATUS) != null ) {  
		    Window.alert("An error occurred compiling the shaders: " + gl.getShaderInfoLog(shader));  
		    return null;  
		}
		       
		return shader;
	}

	private void initShaders( WebGLRenderingContext gl ) {
		WebGLShader fragmentShader = getShader(gl, "shader-fs");
		WebGLShader vertexShader = getShader(gl, "shader-vs");
		   
		WebGLProgram shaderProgram = gl.createProgram();
		gl.attachShader(shaderProgram, vertexShader);
		gl.attachShader(shaderProgram, fragmentShader);
		gl.linkProgram(shaderProgram);
		   
		if( gl.getProgramParameter(shaderProgram, WebGLRenderingContext.LINK_STATUS) != null ) {
			Window.alert("Unable to initialize the shader program.");
		}
		   
		gl.useProgram(shaderProgram);
		   
		int vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition");
		gl.enableVertexAttribArray(vertexPositionAttribute);
	}
	
	double horizAspect = 480.0/640.0;
	 
	private void initBuffers( WebGLRenderingContext gl ) {
		WebGLBuffer squareVerticesBuffer = gl.createBuffer();
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, squareVerticesBuffer);
	   
		Float32ArrayNative fa = Float32ArrayNative.create(12);
		fa.set(0, 1.0f);
		fa.set(1, 1.0f);
		fa.set(2, 0.0f);
		fa.set(3, -1.0f);
		fa.set(4, 1.0f);
		fa.set(5, 0.0f);
		fa.set(6, 1.0f);
		fa.set(7, -1.0f);
		fa.set(8, 0.0f);
		fa.set(9, -1.0f);
		fa.set(10, -1.0f);
		fa.set(11, 0.0f);
	  
	  /*var vertices = [
	    1.0,  1.0,  0.0,
	    -1.0, 1.0,  0.0,
	    1.0,  -1.0, 0.0,
	    -1.0, -1.0, 0.0
	  ];*/
	   
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)fa.buffer(), WebGLRenderingContext.STATIC_DRAW);
	}
	
	public void drawScene( WebGLRenderingContext gl ) {
		gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT);   
		perspectiveMatrix = makePerspective(45, 640.0/480.0, 0.1, 100.0);
		
		loadIdentity();
		mvTranslate([-0.0, 0.0, -6.0]);
		   
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, squareVerticesBuffer);
		gl.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
		setMatrixUniforms();
		gl.drawArrays(WebGLRenderingContext.TRIANGLE_STRIP, 0, 4);
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	root = RootPanel.get();
		Style style = root.getElement().getStyle();
		style.setPadding(0.0, Unit.PX);
		style.setMargin(0.0, Unit.PX);
		style.setBorderWidth(0.0, Unit.PX);
		
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		root.setSize(w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				root.setSize(w+"px", h+"px");
			}
		});
		
		Console console = Browser.getWindow().getConsole();
		
		String text = "Fighting spam is now a community effort. Every post has a Flag button at the lower left. If you hit that to reveal the popup, one of the options is Spam. It's quick and painless. Bonus: if enough people tag something as spam, the post will disappear even without moderator intervention. So don't be bashful. Flag that spam. Don't reply to them, don't copy their posts. Just flag them.";
		Canvas canvas2d = Canvas.createIfSupported();
		canvas2d.setSize(800+"px", 600+"px");
		canvas2d.setCoordinateSpaceWidth(800);
		canvas2d.setCoordinateSpaceHeight(600);
		CanvasElement canvas2dElement = (CanvasElement)canvas2d.getElement();
		Context2d ctx = canvas2d.getContext2d();
		ctx.setFont("32pt Calibri");
		ctx.setFillStyle("#0000FF");
		
		String[] split = text.split(" ");
		int i = 0;
		int y = 1;
		String current = split[i];
		while( i < split.length ) {
			current += " "+split[++i];
			double width = ctx.measureText( current ).getWidth();
			while( i < split.length-1 ) {
				width = ctx.measureText( current+" "+split[i+1] ).getWidth();
				if( width < 800 ) {
					current += " "+split[++i];
				} else break;
			}
			
			ctx.fillText(current, 10, 42*(y++) );
			current = split[++i];
		}
		//CanvasElement	canvas = Browser.getDocument().createCanvasElement();
		Canvas canvas3d = Canvas.createIfSupported();
		canvas3d.setSize(800+"px", 600+"px");
		CanvasElement canvas3dElement = (CanvasElement)canvas3d.getElement();
		//canvas.setWidth(800);
		//canvas.setHeight(600);
		WebGLRenderingContext gl = (WebGLRenderingContext)canvas3dElement.getContext("experimental-webgl");
		initBuffers( gl );
		initShaders( gl );
		//console.log(""+ctx.getClass());
		gl.viewport(0, 0, 800, 600);
		gl.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
		//webgl.enable(WebGLRenderingContext.DEPTH_TEST);
		//webgl.depthFunc(WebGLRenderingContext.LEQUAL);
		gl.clear( WebGLRenderingContext.COLOR_BUFFER_BIT );
		
		WebGLTexture texture = gl.createTexture();
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, canvas2dElement);
		//webgl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
		//webgl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
		//webgl.generateMipmap(gl.TEXTURE_2D);
		//webgl.bindTexture(gl.TEXTURE_2D, null);
		
		WebGLBuffer cubeVerticesTextureCoordBuffer = gl.createBuffer();
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVerticesTextureCoordBuffer);
		
		Float32ArrayNative fa = Float32ArrayNative.create(8);
		fa.set(0, 0.0f);
		fa.set(1, 0.0f);
		fa.set(2, 1.0f);
		fa.set(3, 0.0f);
		fa.set(4, 1.0f);
		fa.set(5, 1.0f);
		fa.set(6, 0.0f);
		fa.set(7, 1.0f);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)fa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		
		gl.activeTexture(WebGLRenderingContext.TEXTURE0);
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		//webgl.uniform1i(webgl.getUniformLocation(shaderProgram, "uSampler"), 0);
		
		vp.add( canvas2d );
		root.add( vp );
	}
}
