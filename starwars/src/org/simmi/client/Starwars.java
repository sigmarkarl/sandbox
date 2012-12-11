package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.CanvasElement;
import elemental.html.Console;
import elemental.html.Float32Array;
import elemental.html.WebGLBuffer;
import elemental.html.WebGLProgram;
import elemental.html.WebGLRenderingContext;
import elemental.html.WebGLShader;
import elemental.html.WebGLTexture;
import elemental.html.WebGLUniformLocation;

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
		
		Console console = Browser.getWindow().getConsole();
		
		WebGLShader shader;
		String shadertype = shaderScript.getType();
		if( "x-shader/x-fragment".equals( shadertype ) ) {
		    shader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER);
		    console.log( "mu" + shader.toString() );
		} else if( "x-shader/x-vertex".equals( shadertype ) ) {
		    shader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER);
		    console.log( "ma" + shader.toString() );
		} else {
			return null;
		}
		    
		gl.shaderSource(shader, theSource);
		gl.compileShader(shader);
		    
		Object obj = gl.getShaderParameter(shader, WebGLRenderingContext.COMPILE_STATUS);
		console.log( obj );
		if( obj == null ) {
		    Window.alert("An error occurred compiling the shaders: " + gl.getShaderInfoLog(shader));  
		    return null;  
		}
		       
		return shader;
	}

	private WebGLProgram initShaders( WebGLRenderingContext gl ) {
		WebGLShader fragmentShader = getShader(gl, "shader-fs");
		WebGLShader vertexShader = getShader(gl, "shader-vs");
		   
		Console console = Browser.getWindow().getConsole();
		console.log( fragmentShader );
		console.log( vertexShader );
		
		WebGLProgram shaderProgram = gl.createProgram();
		gl.attachShader(shaderProgram, vertexShader);
		gl.attachShader(shaderProgram, fragmentShader);
		gl.linkProgram(shaderProgram);
		   
		Object obj = gl.getProgramParameter(shaderProgram, WebGLRenderingContext.LINK_STATUS);
		if( obj == null ) {
			Window.alert("Unable to initialize the shader program.");
		}
		   
		gl.useProgram(shaderProgram);		
		return shaderProgram;
	}
	
	double horizAspect = 480.0/640.0;
	 
	public native void initBuffersNative( WebGLRenderingContext gl ) /*-{
		squareVerticesBuffer = gl.createBuffer();
		gl.bindBuffer(gl.ARRAY_BUFFER, squareVerticesBuffer);
		   
		var vertices = [
		  1.0,  1.0,  0.0,
		  -1.0, 1.0,  0.0,
		  1.0,  -1.0, 0.0,
		  -1.0, -1.0, 0.0
		];
		   
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);
	}-*/;

	private WebGLBuffer initBuffers( WebGLRenderingContext gl ) {
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
		
		return squareVerticesBuffer;
	}
	
	private Float32ArrayNative loadIdentity() {
		Float32ArrayNative fa = Float32ArrayNative.create(4*4);
		for( int i = 0; i < fa.length(); i++ ) {
			int x = i%4;
			int y = i/4;
			
			if( x == y ) fa.set(i, 1.0f);
			else fa.set(i, 0.0f);
		}
		return fa;
	}
	
	private Float32ArrayNative makePerspective( double fov, float aspect, float znear, float zfar ) {
		float xymax = znear * (float)Math.tan( fov * Math.PI/360.0 );
		float ymin = -xymax;
		float xmin = -xymax;

		float width = xymax - xmin;
		float height = xymax - ymin;

		float depth = zfar - znear;
		float q = -(zfar + znear) / depth;
		float qn = -2 * (zfar * znear) / depth;

		float w = 2 * znear / width;
		w = w / aspect;
		float h = 2 * znear / height;
		  
		Float32ArrayNative fa = Float32ArrayNative.create(4*4);
		fa.set(0, w);
		fa.set(1, 0.0f);
		fa.set(2, 0.0f);
		fa.set(3, 0.0f);
	
		fa.set(4, 0.0f);
		fa.set(5, h);
		fa.set(6, 0.0f);
		fa.set(7, 0.0f);
		
		fa.set(8, 0.0f);
		fa.set(9, 0.0f);
		fa.set(10, q);
		fa.set(11, -1.0f);
		
		fa.set(12, 0.0f);
		fa.set(13, 0.0f);
		fa.set(14, qn);
		fa.set(15, 0.0f);
		
		return fa;
	}
	
	private native void setMatrixUniformsNative( WebGLRenderingContext gl, Float32ArrayNative perspectiveMatrix, Float32ArrayNative mvMatrix, WebGLProgram shaderProgram ) /*-{
		this.@org.simmi.client.Starwars::setMatrixUniforms(Lelemental/html/WebGLRenderingContext;Lelemental/html/Float32Array;Lelemental/html/Float32Array;Lelemental/html/WebGLProgram;)( gl, perspectiveMatrix, mvMatrix, shaderProgram );
	}-*/;
	
	private native void drawSceneNative( WebGLRenderingContext gl, int vertexPositionAttribute, WebGLProgram shaderProgram ) /*-{
		gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
		 
		perspectiveMatrix = this.@org.simmi.client.Starwars::makePerspective(DFFF)(45, 640.0/480.0, 0.1, 100.0);
		   
		mvMatrix = this.@org.simmi.client.Starwars::loadIdentity()();
		mvMatrix[11] = -6.0;
		//mvTranslate([-0.0, 0.0, -6.0]);
		
		gl.bindBuffer(gl.ARRAY_BUFFER, squareVerticesBuffer);
		gl.vertexAttribPointer(vertexPositionAttribute, 3, gl.FLOAT, false, 0, 0);
		this.@org.simmi.client.Starwars::setMatrixUniforms(Lelemental/html/WebGLRenderingContext;Lelemental/html/Float32Array;Lelemental/html/Float32Array;Lelemental/html/WebGLProgram;)( gl, perspectiveMatrix, mvMatrix, shaderProgram );
		gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
	}-*/;
	
	public void drawScene( WebGLRenderingContext gl, WebGLBuffer squareVerticesBuffer, WebGLProgram shaderProgram, int vertexPositionAttribute ) {		
		gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT);   
		Float32ArrayNative perspectiveMatrix = makePerspective( 45.0, 800.0f/600.0f, 0.1f, 100.0f );//makePerspective(45, 640.0/480.0, 0.1, 100.0);
		
		Float32ArrayNative mvMatrix = loadIdentity();
		mvMatrix.set(11, -6.0f);
		//mvMatrix.set(15, -0.0f);
		//mvTranslate([-0.0, 0.0, -6.0]);
		   
		
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, squareVerticesBuffer);
		gl.vertexAttribPointer( vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0 );
		setMatrixUniformsNative( gl, perspectiveMatrix, mvMatrix, shaderProgram );
		gl.drawArrays( WebGLRenderingContext.TRIANGLE_STRIP, 0, 4 );
	}
	
	private void setMatrixUniforms( WebGLRenderingContext gl, Float32Array perspectiveMatrix, Float32Array mvMatrix, WebGLProgram shaderProgram ) {
		Console console = Browser.getWindow().getConsole();
		
		console.log( "mvmat" );
		console.log( mvMatrix.numberAt(11) );
		
		console.log( "pmat" + perspectiveMatrix.numberAt(0) + " " + perspectiveMatrix.numberAt(1) );
		
		WebGLUniformLocation pUniform = gl.getUniformLocation(shaderProgram, "uPMatrix");
		gl.uniformMatrix4fv( pUniform, false, perspectiveMatrix );
		 
		WebGLUniformLocation mvUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix");
		gl.uniformMatrix4fv( mvUniform, false, mvMatrix );
	}
	
	private native JavaScriptObject init( WebGLRenderingContext gl, WebGLTexture spiritTexture ) /*-{
		var g = {};
		
	    var program = $wnd.simpleSetup(
            gl,
            // The ids of the vertex and fragment shaders
            "vshader", "fshader",
            // The vertex attribute names used by the shaders.
            // The order they appear here corresponds to their index
            // used later.
            [ "vNormal", "vColor", "vPosition"],
            // The clear color and depth values
            [ 0, 0, 0.5, 1 ], 10000);

        // Set some uniform variables for the shaders
        gl.uniform3f(gl.getUniformLocation(program, "lightDir"), 0, 0, 1);
        gl.uniform1i(gl.getUniformLocation(program, "sampler2d"), 0);

        // Create a box. On return 'gl' contains a 'box' property with
        // the BufferObjects containing the arrays for vertices,
        // normals, texture coords, and indices.
        g.box = $wnd.makeBox(gl);

        // Load an image to use. Returns a WebGLTexture object
        //spiritTexture = $wnd.loadImageTexture(gl, "resources/spirit.jpg");

        // Create some matrices to use later and save their locations in the shaders
        g.mvMatrix = new $wnd.J3DIMatrix4();
        g.u_normalMatrixLoc = gl.getUniformLocation(program, "u_normalMatrix");
        g.normalMatrix = new $wnd.J3DIMatrix4();
        g.u_modelViewProjMatrixLoc =
        gl.getUniformLocation(program, "u_modelViewProjMatrix");
        g.mvpMatrix = new $wnd.J3DIMatrix4();

        // Enable all of the vertex attribute arrays.
        gl.enableVertexAttribArray(0);
        gl.enableVertexAttribArray(1);
        gl.enableVertexAttribArray(2);

        // Set up all the vertex attributes for vertices, normals and texCoords
        gl.bindBuffer(gl.ARRAY_BUFFER, g.box.vertexObject);
        gl.vertexAttribPointer(2, 3, gl.FLOAT, false, 0, 0);

        gl.bindBuffer(gl.ARRAY_BUFFER, g.box.normalObject);
        gl.vertexAttribPointer(0, 3, gl.FLOAT, false, 0, 0);

        gl.bindBuffer(gl.ARRAY_BUFFER, g.box.texCoordObject);
        gl.vertexAttribPointer(1, 2, gl.FLOAT, false, 0, 0);

        // Bind the index array
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, g.box.indexObject);
        
        return g;
	}-*/;
	
	private native void reshape( WebGLRenderingContext gl, CanvasElement canvas, JavaScriptObject g ) /*-{
	   // change the size of the canvas's backing store to match the size it is displayed.
        //var canvas = document.getElementById('example');
        if (canvas.clientWidth == canvas.width && canvas.clientHeight == canvas.height)
            return;

        canvas.width = canvas.clientWidth;
        canvas.height = canvas.clientHeight;

        // Set the viewport and projection matrix for the scene
        gl.viewport(0, 0, canvas.clientWidth, canvas.clientHeight);
        g.perspectiveMatrix = new $wnd.J3DIMatrix4();
        g.perspectiveMatrix.perspective(30, canvas.clientWidth / canvas.clientHeight, 1, 10000);
        g.perspectiveMatrix.lookat(0, 0, 7, 0, 0, 0, 0, 1, 0);
	}-*/;
	
	private native void drawPicture( WebGLRenderingContext gl, CanvasElement canvas, WebGLTexture spiritTexture, JavaScriptObject g ) /*-{
	    //Make sure the canvas is sized correctly.
	    this.@org.simmi.client.Starwars::reshape(Lelemental/html/WebGLRenderingContext;Lelemental/html/CanvasElement;Lcom/google/gwt/core/client/JavaScriptObject;)(gl, canvas, g);
	    //(Lelemental/html/WebGLRenderingContext;Lelemental/html/CanvasElement;LJavaScriptObject;)(gl, canvas, g);

        // Clear the canvas
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Make a model/view matrix.
        g.mvMatrix.makeIdentity();
        g.mvMatrix.rotate(20, 1,0,0);
        g.mvMatrix.rotate(0.0, 0,1,0);

        // Construct the normal matrix from the model-view matrix and pass it in
        g.normalMatrix.load(g.mvMatrix);
        g.normalMatrix.invert();
        g.normalMatrix.transpose();
        g.normalMatrix.setUniform(gl, g.u_normalMatrixLoc, false);

        // Construct the model-view * projection matrix and pass it in
        g.mvpMatrix.load(g.perspectiveMatrix);
        g.mvpMatrix.multiply(g.mvMatrix);
        g.mvpMatrix.setUniform(gl, g.u_modelViewProjMatrixLoc, false);

        // Bind the texture to use
        gl.bindTexture(gl.TEXTURE_2D, spiritTexture);

        // Draw the cube
        gl.drawElements(gl.TRIANGLES, g.box.numIndices, gl.UNSIGNED_BYTE, 0);
	}-*/;
	
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
		canvas2d.setSize(512+"px", 512+"px");
		canvas2d.setCoordinateSpaceWidth(512);
		canvas2d.setCoordinateSpaceHeight(512);
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
		canvas3d.getElement().setId("example");
		canvas3d.setSize(800+"px", 600+"px");
		final CanvasElement canvas3dElement = (CanvasElement)canvas3d.getElement();
		//canvas.setWidth(800);
		//canvas.setHeight(600);
		final WebGLRenderingContext gl = (WebGLRenderingContext)canvas3dElement.getContext("experimental-webgl");
		
		/*gl.viewport(0, 0, 800, 600);
		gl.clearColor(0.3f, 0.2f, 0.2f, 1.0f);
		gl.enable( WebGLRenderingContext.DEPTH_TEST );
		gl.depthFunc( WebGLRenderingContext.LEQUAL );
		gl.clear( WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT );
		gl.enable( WebGLRenderingContext.CULL_FACE );
		
		WebGLProgram shaderProgram = initShaders( gl );
		int vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition");
		gl.enableVertexAttribArray(vertexPositionAttribute);
		
		initBuffersNative( gl );
		//WebGLBuffer squareVerticesBuffer = initBuffers( gl );
		
		console.log( "texture" );*/
		
		final WebGLTexture texture = gl.createTexture();
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, canvas2dElement);
		gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
		gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
		gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
		
		//drawPicture( gl, canvas3dElement, texture );
		
		//gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);
		
		/*WebGLBuffer cubeVerticesTextureCoordBuffer = gl.createBuffer();
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
		//webgl.uniform1i(webgl.getUniformLocation(shaderProgram, "uSampler"), 0);*
		
		//drawScene( gl, squareVerticesBuffer, shaderProgram, vertexPositionAttribute );
		drawSceneNative( gl, vertexPositionAttribute, shaderProgram );*/
		vp.add( canvas3d );
		root.add( vp );
		
		final JavaScriptObject g = init( gl, texture );
		final Timer timer = new Timer() {

			@Override
			public void run() {
				drawPicture( gl, canvas3dElement, texture, g );
				//timer.schedule(200);
			}
		};
		timer.schedule(200);
	}
}
