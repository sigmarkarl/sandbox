package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.typedarrays.client.Int16ArrayNative;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.dom.RequestAnimationFrameCallback;
import elemental.html.ArrayBuffer;
import elemental.html.Blob;
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
		WebGLShader fragmentShader = getShader(gl, "fshader");
		WebGLShader vertexShader = getShader(gl, "vshader");
		   
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
	
	private native JavaScriptObject init( WebGLRenderingContext gl ) /*-{
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
            [ 0, 0, 0, 1 ], 10000);

        // Set some uniform variables for the shaders
        gl.uniform3f(gl.getUniformLocation(program, "lightDir"), 0, 0, 1);
        gl.uniform1i(gl.getUniformLocation(program, "sampler2d"), 0);

        // Create a box. On return 'gl' contains a 'box' property with
        // the BufferObjects containing the arrays for vertices,
        // normals, texture coords, and indices.
        g.box = $wnd.makeBox(gl);
        g.indfuck = [-1.0, -1.0, 1.0, 1.0, -1.0, 1.0, 1.0, 1.0, 1.0];

        // Load an image to use. Returns a WebGLTexture object
        //spiritTexture = $wnd.loadImageTexture(gl, "resources/spirit.jpg");

        // Create some matrices to use later and save their locations in the shaders
        g.mvMatrix = new $wnd.J3DIMatrix4();
        g.u_normalMatrixLoc = gl.getUniformLocation(program, "u_normalMatrix");
        g.normalMatrix = new $wnd.J3DIMatrix4();
        g.u_modelViewProjMatrixLoc = gl.getUniformLocation(program, "u_modelViewProjMatrix");
        g.mvpMatrix = new $wnd.J3DIMatrix4();
        
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
        g.perspectiveMatrix.perspective(30, 1.0, 1, 10000);
        g.perspectiveMatrix.lookat(0, 0, 5, 0, 0, 0, 0, 1, 0);
	}-*/;
	
	private native WebGLBuffer vbuf( JavaScriptObject g ) /*-{
		return g.box.vertexObject;
	}-*/;
	
	private native WebGLBuffer nbuf( JavaScriptObject g ) /*-{
		return g.box.normalObject;
	}-*/;
	
	private native WebGLBuffer tbuf( JavaScriptObject g ) /*-{
		return g.box.texCoordObject;
	}-*/;
	
	private native WebGLBuffer ibuf( JavaScriptObject g ) /*-{
		return g.box.indexObject;
	}-*/;
	
	private native void drawPicture( WebGLRenderingContext gl, CanvasElement canvas, WebGLTexture spiritTexture, WebGLTexture starfield, JavaScriptObject g ) /*-{
	    //Make sure the canvas is sized correctly.
	    this.@org.simmi.client.Starwars::reshape(Lelemental/html/WebGLRenderingContext;Lelemental/html/CanvasElement;Lcom/google/gwt/core/client/JavaScriptObject;)(gl, canvas, g);
	    //(Lelemental/html/WebGLRenderingContext;Lelemental/html/CanvasElement;LJavaScriptObject;)(gl, canvas, g);

        // Clear the canvas
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Make a model/view matrix.
        g.mvMatrix.makeIdentity();
        //g.mvMatrix.rotate(20, 1,0,0);
        //g.mvMatrix.rotate(0.0, 0,1,0);

        // Construct the normal matrix from the model-view matrix and pass it in
        g.normalMatrix.load(g.mvMatrix);
        g.normalMatrix.invert();
        g.normalMatrix.transpose();
        g.normalMatrix.setUniform(gl, g.u_normalMatrixLoc, false);

        // Construct the model-view * projection matrix and pass it in, 
        g.mvpMatrix.load(g.perspectiveMatrix);
        g.mvpMatrix.multiply(g.mvMatrix);
        g.mvpMatrix.setUniform(gl, g.u_modelViewProjMatrixLoc, false);
	}-*/;
	
	WebGLBuffer svb1, svb, vb, nb, tb, ib;
	public void drawStuff( WebGLRenderingContext gl, JavaScriptObject g, int jobj, WebGLTexture spiritTexture ) {
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, spiritTexture);
        gl.drawElements(WebGLRenderingContext.TRIANGLES, 6, WebGLRenderingContext.UNSIGNED_SHORT, 0);
        
        //gl.enable( WebGLRenderingContext.BLEND );
		//gl.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
		
        //gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, spiritTexture);
        //gl.drawElements(WebGLRenderingContext.TRIANGLES, 3, gl.UNSIGNED_BYTE, 0);
	}
	
	public int measureText( String text, Canvas canvas ) {
		Context2d ctx = canvas.getContext2d();
		//ctx.clearRect(0, 0, 1024, 1024 );
		ctx.setFont("48pt Calibri");
		ctx.setFillStyle("#FFFF00");
		
		String[] splitn = text.split("\n");
		int y = 0;
		for( String spltext : splitn ) {
			y++;
			
			String[] split = spltext.split(" ");
			int i = 0;
			String current = split[i];
			while( i < split.length-1 ) {
				current += " "+split[++i];
				double width = ctx.measureText( current ).getWidth();
				while( i < split.length-1 ) {
					String metext = current+" "+split[i+1];
					width = ctx.measureText( metext ).getWidth();
					//Browser.getWindow().getConsole().log( "w " + width + "  " + metext );
					
					if( width < 1024 ) {
						current += " "+split[++i];
					} else break;
				}
				
				if( i < split.length-1 ) {
					y++;
					current = split[++i];
				} else current = "";
			}
			//if( current.length() > 0 ) y++;
		}
		
		//Browser.getWindow().getConsole().log( "y " + y );
		return y*96;
	}
	
	public void changeText( WebGLRenderingContext gl, String text, Canvas canvas, /*int sizy,*/ List<WebGLTexture> textures, List<WebGLBuffer> buffers ) {
		int sizy = 1024;
		canvas.setSize(1024+"px", sizy+"px");
		canvas.setCoordinateSpaceWidth(1024);
		canvas.setCoordinateSpaceHeight( sizy );
		Context2d ctx = canvas.getContext2d();
		ctx.clearRect(0, 0, 1024, sizy );
		ctx.setFont("52pt Calibri");
		ctx.setFillStyle("#FFFF00");
		
		int tind = 0;
		String[] splitn = text.split("\n");
		int y = 0;
		for( String spltext : splitn ) {
			y++;
			
			if( y % 11 == 0 ) {
				Browser.getWindow().getConsole().log("hoho "+tind);
				//int ind = y/5-1;
				y = 1;
				
				if( tind >= textures.size() ) {
					textures.add( gl.createTexture() );
					buffers.add( gl.createBuffer() );
				}
				WebGLTexture texture = textures.get( tind );
				
				gl.bindTexture( WebGLRenderingContext.TEXTURE_2D, texture);
				gl.texImage2D( WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, (CanvasElement)canvas.getElement());
				//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
				//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
				gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
				
				ctx.clearRect( 0, 0, 1024, sizy );
				
				tind++;
			}
			
			String[] split = spltext.split(" ");
			int i = 0;
			String current = split[i];
			while( i < split.length-1 ) {
				current += " "+split[++i];
				double width = ctx.measureText( current ).getWidth();
				while( i < split.length-1 ) {
					width = ctx.measureText( current+" "+split[i+1] ).getWidth();
					if( width < 1004 ) {
						current += " "+split[++i];
					} else break;
				}
				
				/*if( i >= split.length-1 ) {
					ctx.fillText(current, 10, 96*y );
				} else {
				}*/
				
				if( i < split.length-1 ) {
					String[] newspl = current.split("[ ]+");
					double left = 1004.0-ctx.measureText( current.replace(" ", "") ).getWidth();
					double total = 0.0;
					for( String newstr : newspl ) {
						ctx.fillText(newstr, 10+total, 101*y );
						total += ctx.measureText( newstr ).getWidth()+left/(newspl.length-1);
					}
					y++;
					
					if( y % 11 == 0 ) {
						Browser.getWindow().getConsole().log("hoho "+tind);
						//int ind = y/5-1;
						y = 1;
						
						if( tind >= textures.size() ) {
							textures.add( gl.createTexture() );
							buffers.add( gl.createBuffer() );
						}
						WebGLTexture texture = textures.get( tind );
						
						gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
						gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, (CanvasElement)canvas.getElement());
						//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
						//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
						gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
						
						ctx.clearRect( 0, 0, 1024, sizy );
						
						tind++;
					}
					
					current = split[++i];
				} else {
					ctx.fillText(current, 10, 101*y );
					current = "";
				}
			}
			if( current.length() > 0 ) {
				ctx.fillText(current, 10, 101*y );
				
				/*double left = 1004.0-ctx.measureText( current.replace(" ", "") ).getWidth();
				String[] newspl = current.split("[ ]+");
				double total = 0.0;
				for( String newstr : newspl ) {
					ctx.fillText(newstr, 10+total, 96*y );
					total += ctx.measureText( newstr ).getWidth()+left/(newspl.length-1);
				}
				y++;*/
			}
			
			/*if( y % 5 == 0 ) {
				Browser.getWindow().getConsole().log("hoho "+tind);
				//int ind = y/5-1;
				y = 0;
				
				if( tind >= textures.size() ) textures.add( gl.createTexture() );
				WebGLTexture texture = textures.get( tind );
				
				gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
				gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, (CanvasElement)canvas.getElement());
				//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
				//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
				gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
				
				ctx.clearRect(0, 0, 1024, sizy );
				
				tind++;
			}*/
		}
		
		if( y % 11 != 0 ) {
			Browser.getWindow().getConsole().log("bleh "+tind);
			//int ind = y/5;
			//y = 0;
			
			if( tind >= textures.size() ) {
				textures.add( gl.createTexture() );
				buffers.add( gl.createBuffer() );
			}
			WebGLTexture texture = textures.get( tind );
			
			gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
			gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, (CanvasElement)canvas.getElement());
			//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
			//gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
			gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
			
			ctx.clearRect( 0, 0, 1024, sizy );
		}
	}
	
	public void setBuffers( WebGLRenderingContext gl, WebGLBuffer vertexObject, WebGLBuffer normalObject, WebGLBuffer texCoordObject, WebGLBuffer indexObject, boolean offset ) {
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vertexObject);
		if( offset ) gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)svfa1.buffer(), WebGLRenderingContext.STATIC_DRAW);
        gl.vertexAttribPointer(2, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
        
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, normalObject);
        gl.vertexAttribPointer(0, 3, WebGLRenderingContext.FLOAT, false, 0, 0);

        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, texCoordObject);
        gl.vertexAttribPointer(1, 2, WebGLRenderingContext.FLOAT, false, 0, 0);

        gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, indexObject);
	}
	
	public native elemental.html.ImageElement imgEl( Element e ) /*-{
		return e;
	}-*/;
	
	/*public native String btoa( String str ) -{
		var ret = '';
		try {
			ret = $wnd.btoa( str );
		} catch( e ) {
			
		}
		return ret;
	}-;
	
	public native String atob( String str ) -{
		var ret = '';
		try {
			ret = $wnd.atob( str );
		} catch( e ) {
			
		}
		return ret;
	}-*/;
	
	RequestAnimationFrameCallback rafc = null;
	final Float32ArrayNative svfa1 = Float32ArrayNative.create(12);
	
	double pi4 = Math.PI/12.0;
	float sinv = (float)Math.sin( pi4 );
	float cosv = (float)Math.cos( pi4 );
	
	float offval = -50.0f;
	float newy = 1024.0f;
	public void offset( float offval, float newy ) {
		float mul = newy/1024.0f;
		
		svfa1.set(0, 10.0f);
		svfa1.set(1, mul*30.0f*sinv+offval*sinv-7.0f);
		svfa1.set(2, -50.0f-mul*30.0f*cosv-offval*cosv);
		svfa1.set(3, -10.0f);
		svfa1.set(4, mul*30.0f*sinv+offval*sinv-7.0f);
		svfa1.set(5, -50.0f-mul*30.0f*cosv-offval*cosv);
		svfa1.set(6, -10.0f);
		svfa1.set(7, -mul*30.0f*sinv+offval*sinv-7.0f);
		svfa1.set(8, -50.0f+mul*30.0f*cosv-offval*cosv);
		svfa1.set(9, 10.0f);
		svfa1.set(10, -mul*30.0f*sinv+offval*sinv-7.0f);
		svfa1.set(11, -50.0f+mul*30.0f*cosv-offval*cosv);
	}
	
	public native void requestAnimationFrame( WebGLRenderingContext gl, List<WebGLTexture> textures, List<WebGLBuffer> buffers, WebGLTexture starfield, JavaScriptObject g ) /*-{
		$wnd.pause = 0;
		var s = this;
		var requestAnimationFrame = $wnd.requestAnimationFrame || $wnd.mozRequestAnimationFrame || $wnd.webkitRequestAnimationFrame || $wnd.msRequestAnimationFrame;
		function step(timestamp) {
			s.@org.simmi.client.Starwars::animate(Lelemental/html/WebGLRenderingContext;Ljava/util/List;Ljava/util/List;Lelemental/html/WebGLTexture;Lcom/google/gwt/core/client/JavaScriptObject;)( gl, textures, buffers, starfield, g );
			if( !$wnd.pause ) requestAnimationFrame( step );
		}
		requestAnimationFrame( step );
	}-*/;
	
	public native JavaScriptObject wham() /*-{
		//$wnd.pause = 1;
		var encoder = new $wnd.Whammy.Video(24);
		return encoder;
	}-*/;
	
	public native void addFrame( JavaScriptObject encoder, com.google.gwt.dom.client.CanvasElement canvas ) /*-{
		encoder.add( canvas );
	}-*/;
	
	public native Blob compile( JavaScriptObject encoder ) /*-{
		return encoder.compile();
	}-*/;
	
	public native String createObjectURL( Blob blob ) /*-{
		return $wnd.URL.createObjectURL( blob );
	}-*/;
	
	String uid;
	public void setUserId( String val ) {
		uid = val;
	}
	
	public native void fbInit( String login ) /*-{
		var ths = this;	    	
		$wnd.fbAsyncInit = function() {			
	    	$wnd.FB.init({appId: '508404285869563', status: true, cookie: true, xfbml: true, oauth : true});
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					try {
						if (response.status === 'connected') {
						    var uid = response.authResponse.userID;
						    var accessToken = response.authResponse.accessToken;
						    ths.@org.simmi.client.Starwars::setUserId(Ljava/lang/String;)( uid );
						} else if (response.status === 'not_authorized') {
							ths.@org.simmi.client.Starwars::setUserId(Ljava/lang/String;)( "" );
						} else {
						    ths.@org.simmi.client.Starwars::setUserId(Ljava/lang/String;)( "" );
						}
						$wnd.FB.XFBML.parse();
					} catch( e ) {}
				});
			} catch( e ) {}
	  	};
	}-*/;
	
	public native void sendMessage( String body ) /*-{
		$wnd.console.log( body );
		$wnd.console.log( body.length );
		
		var requestCallback = function(response) {
	    	if( $wnd.console ) $wnd.console.log( response );
	  	}
	  
		$wnd.FB.ui({method: 'apprequests',
	    	message: body
	  	}, requestCallback);
	}-*/;
	
	final String url = "http://starwarsflyingtext.appspot.com";
	JavaScriptObject encoder;
	public void onModuleLoad() {
		final RootPanel	root = RootPanel.get("starwars");
		Style style = root.getElement().getStyle();
		style.setPadding(0.0, Unit.PX);
		style.setMargin(0.0, Unit.PX);
		style.setBorderWidth(0.0, Unit.PX);
		
		final RootPanel	download = RootPanel.get("download");
		
		//VerticalPanel	vp = new VerticalPanel();
		//vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		//vp.setSize("100%", "100%");
		
		//int w = Window.getClientWidth();
		//int h = Window.getClientHeight();
		//root.setSize(w+"px", h+"px");
		
		/*Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				root.setSize(w+"px", h+"px");
			}
		});*/
		
		final elemental.html.Window wnd = Browser.getWindow();
		final Console console = wnd.getConsole();
		final TextArea ta = new TextArea();
		
		String fbuid = null;
		NodeList<Element> nl = Document.get().getElementsByTagName("meta");
		for( int i = 0; i < nl.getLength(); i++ ) {
			Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			if( prop.equals("fbuid") ) {
				//setUserId( e.getAttribute("content") );
				fbuid = e.getAttribute("content");
				break;
			}
		}
		if( fbuid == null ) fbInit( fbuid );
		else setUserId( fbuid );
		
		//Document.get().getElementById("fb-root")
		RootPanel	facebook = RootPanel.get("fb-root");
		Button	fbbutton = new Button("Send facebook message");
		fbbutton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String href;
				String text = ta.getText();
				try {
					String btoa = wnd.btoa( URL.encode(text) );
					int i = btoa.indexOf('=');
					if( i != -1 ) btoa = btoa.substring(0, i);
					href = url+"?bflyer="+btoa;
				} catch( Exception e ) {
					href = url+"?bflyer=";
				}
				sendMessage( href );
			}
		});
		
		String id = "facebook-jssdk";
		ScriptElement se = Document.get().createScriptElement();
		se.setId( id );
		se.setAttribute("async", "true");
		se.setSrc("//connect.facebook.net/en_US/all.js");
		facebook.getElement().appendChild(se);
		
		final Canvas starcanvas2d = Canvas.createIfSupported();
		starcanvas2d.setSize(512+"px", 256+"px");
		starcanvas2d.setCoordinateSpaceWidth(512);
		starcanvas2d.setCoordinateSpaceHeight(256);
		final CanvasElement starcanvas2dElement = (CanvasElement)starcanvas2d.getElement();
		final Context2d starctx = starcanvas2d.getContext2d();
		
		String text = "Custom StarWars flying text\n"+
				"Be sure to have WebGL enabled in your Chrome browser. Go to url chrome://flags and enable WebGL if not already enabled.\n"+
				"Edit text in text area and press Run to change flying text.";
		
		boolean change = false;
		String query = Window.Location.getQueryString();
		if( query != null ) {
			change = true;
			if( query.contains("bflyer") ) {
				int i = query.indexOf("bflyer");
				String qs = query.substring(i+7);
				try {
					String atob = wnd.atob( qs );
					text = URL.decode( atob );
				} catch( Exception e ) {
					text = "";
				}
			} else if( query.contains("flyer") ) {
				int i = query.indexOf("flyer");
				String qs = query.substring(i+6);
				text = URL.decode( qs );
				//console.log("text");
			}
		}
		//String text = "Fighting spam is now a community effort. Every post has a Flag button at the lower left.\nIf you hit that to reveal the popup, one of the options is Spam. It's quick and painless. Bonus: if enough people tag something as spam, the post will disappear even without moderator intervention. So don't be bashful. Flag that spam. Don't reply to them, don't copy their posts. Just flag them.";
		final Canvas canvas2d = Canvas.createIfSupported();
		canvas2d.setSize(1024+"px", 1024+"px");
		canvas2d.setCoordinateSpaceWidth(1024);
		canvas2d.setCoordinateSpaceHeight(1024);
		final CanvasElement canvas2dElement = (CanvasElement)canvas2d.getElement();
		
		//int y = measureText( text, canvas2d );
		//double val = Math.log((double)y) / Math.log(2.0);
		//int newy = (int)Math.pow(2.0, Math.ceil( val ) );
		//this.newy = newy;
		//offval = -newy/36.0f-20.0f;
		//console.log("newy "+ newy);
		
		//CanvasElement	canvas = Browser.getDocument().createCanvasElement();
		final Canvas canvas3d = Canvas.createIfSupported();
		canvas3d.getElement().setId("example");
		canvas3d.setSize(960+"px", 540+"px");
		final CanvasElement canvas3dElement = (CanvasElement)canvas3d.getElement();
		canvaselement = canvas3d.getCanvasElement();
		//canvas.setWidth(800);
		//canvas.setHeight(600);
		WebGLRenderingContext webgl = null;
		webgl = (WebGLRenderingContext)canvas3dElement.getContext("experimental-webgl");
		if( webgl == null ) webgl = (WebGLRenderingContext)canvas3dElement.getContext("webgl");
		final WebGLRenderingContext gl = webgl;
		
		/*gl.viewport(0, 0, 800, 600);
		gl.clearColor(0.3f, 0.2f, 0.2f, 1.0f);
		gl.enable( WebGLRenderingContext.DEPTH_TEST );
		gl.depthFunc( WebGLRenderingContext.LEQUAL );
		gl.clear( WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT );
		gl.enable( WebGLRenderingContext.CULL_FACE );*/
		
		//WebGLProgram shaderProgram = initShaders( gl );
		//int vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition");
		//gl.enableVertexAttribArray(vertexPositionAttribute);
		
		//initBuffersNative( gl );
		//WebGLBuffer squareVerticesBuffer = initBuffers( gl );		
		
		final List<WebGLBuffer>		buffers = new ArrayList<WebGLBuffer>();
		final List<WebGLTexture>	textures = new ArrayList<WebGLTexture>();
		WebGLTexture texture = gl.createTexture();
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, canvas2dElement);
		gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
		gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
		gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
		textures.add( texture );
		
		changeText( gl, text, canvas2d, /*newy,*/ textures, buffers );
	
//		//drawPicture( gl, canvas3dElement, texture );
//		
//		//gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);
//		
//		final WebGLBuffer cubeVerticesTextureCoordBuffer = gl.createBuffer();
				
		offset( offval, newy );
		
		final Float32ArrayNative svfa = Float32ArrayNative.create(12);
		svfa.set(0, 1600.0f);
		svfa.set(1, 1600.0f);
		svfa.set(2, -6000.0f);
		svfa.set(3, -1600.0f);
		svfa.set(4, 1600.0f);
		svfa.set(5, -6000.0f);
		svfa.set(6, -1600.0f);
		svfa.set(7, -1600.0f);
		svfa.set(8, -6000.0f);
		svfa.set(9, 1600.0f);
		svfa.set(10, -1600.0f);
		svfa.set(11, -6000.0f);
		
		final Float32ArrayNative vfa = Float32ArrayNative.create(12);
		vfa.set(0, 1.0f);
		vfa.set(1, 1.0f);
		vfa.set(2, 1.0f);
		vfa.set(3, -1.0f);
		vfa.set(4, 1.0f);
		vfa.set(5, 1.0f);
		vfa.set(6, -1.0f);
		vfa.set(7, -1.0f);
		vfa.set(8, 1.0f);
		vfa.set(9, 1.0f);
		vfa.set(10, -1.0f);
		vfa.set(11, 1.0f);
		
		final Float32ArrayNative nfa = Float32ArrayNative.create(12);
		nfa.set(0, 0.0f);
		nfa.set(1, 0.0f);
		nfa.set(2, 1.0f);
		nfa.set(3, 0.0f);
		nfa.set(4, 0.0f);
		nfa.set(5, 1.0f);
		nfa.set(6, 0.0f);
		nfa.set(7, 0.0f);
		nfa.set(8, 1.0f);
		nfa.set(9, 0.0f);
		nfa.set(10, 0.0f);
		nfa.set(11, 1.0f);
		
		final Float32ArrayNative tfa = Float32ArrayNative.create(8);
		tfa.set(0, 1.0f);
		tfa.set(1, 1.0f);
		tfa.set(2, 0.0f);
		tfa.set(3, 1.0f);
		tfa.set(4, 0.0f);
		tfa.set(5, 0.0f);
		tfa.set(6, 1.0f);
		tfa.set(7, 0.0f);
	
		final Int16ArrayNative ifa = Int16ArrayNative.create(6);
		ifa.set(0, 0);
		ifa.set(1, 1);
		ifa.set(2, 2);
		ifa.set(3, 0);
		ifa.set(4, 2);
		ifa.set(5, 3);
		
		svb1 = gl.createBuffer();
		buffers.add( svb1 );
		svb = gl.createBuffer();
		vb = gl.createBuffer();
		nb = gl.createBuffer();
		tb = gl.createBuffer();
		ib = gl.createBuffer();
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, svb1);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)svfa1.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, svb);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)svfa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vb);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)vfa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, nb);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)nfa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, tb);
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)tfa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, null);
		
		gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, ib);
		gl.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, (ArrayBuffer)ifa.buffer(), WebGLRenderingContext.STATIC_DRAW);
		gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, null);
		
		/*gl.activeTexture(WebGLRenderingContext.TEXTURE0);
		gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		//webgl.uniform1i(webgl.getUniformLocation(shaderProgram, "uSampler"), 0);*
		
		//drawScene( gl, squareVerticesBuffer, shaderProgram, vertexPositionAttribute );
		drawSceneNative( gl, vertexPositionAttribute, shaderProgram );*/
		
		final JavaScriptObject g = init( gl );
		
		// Enable all of the vertex attribute arrays.
        gl.enableVertexAttribArray(0);
        gl.enableVertexAttribArray(1);
        gl.enableVertexAttribArray(2);

        //Set up all the vertex attributes for vertices, normals and texCoords
        
        //final String url = "http://192.168.1.65:8888/Starwars.html";
        final Anchor a = new Anchor("Link");
        final Anchor b = new Anchor("BLink");
        
		if( change ) {
			a.setHref( url+"?flyer="+URL.encode(text) );
			try {
				String btoa = wnd.btoa( URL.encode(text) );
				int i = btoa.indexOf('=');
				if( i != -1 ) btoa = btoa.substring(0, i);
				b.setHref( url+"?bflyer="+btoa );
			} catch( Exception e ) {
				b.setHref( url+"?bflyer=" );
			}
			ta.setText( text );
		}
		ta.setSize(945+"px", 120+"px");
		final Button	button = new Button("Run");
		button.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*int y = measureText( ta.getText(), canvas2d );
				double val = Math.log((double)y) / Math.log(2.0);
				int newy = (int)Math.pow(2.0, Math.ceil( val ) );
				Starwars.this.newy = newy;
				offval = -newy/36.0f-20.0f;
				console.log("newy "+ newy);*/
				offval = -50.0f;
				
				String text = ta.getText();
				changeText( gl, text, canvas2d, /*newy,*/ textures, buffers );
				//gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
				gl.clear( WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT );
				
				//drawPicture( gl, canvas3dElement, texture, starfield, g );
				a.setHref( url+"?flyer="+URL.encode(text) );
				try {
					String btoa = wnd.btoa( URL.encode(text) );
					int i = btoa.indexOf('=');
					if( i != -1 ) btoa = btoa.substring(0, i);
					b.setHref( url+"?bflyer="+btoa );
				} catch( Exception e) {
					b.setHref( url+"?bflyer=" );
				}
				//gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVerticesTextureCoordBuffer);
				//gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)fa.buffer(), WebGLRenderingContext.STATIC_DRAW);
				//gl.drawArrays(WebGLRenderingContext.TRIANGLE_STRIP, 0, 4);
			}
		});
		//vp.setSpacing( 5 );
		
		VerticalPanel subvp = new VerticalPanel();
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		//HTML	starwars = new HTML( "<h1>Custom StarWars flying text</h1>" );
		//subvp.add( starwars );
		subvp.getElement().getStyle().setMargin(10.0, Unit.PX);
		subvp.add( canvas3d );
		subvp.add( ta );
		subvp.add( button );
		
		HorizontalPanel	linkhp = new HorizontalPanel();
		linkhp.setSpacing(5);
		linkhp.add( a );
		linkhp.add( b );
		subvp.add( linkhp );
		
		final WebGLTexture starfield = gl.createTexture();
		
		final Image image = new Image();
		Browser.getWindow().getConsole().log("doing");
		image.addLoadHandler( new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				Browser.getWindow().getConsole().log("mememe");
				
				starctx.drawImage( ImageElement.as(image.getElement()), 0.0, 0.0, 512.0, 256.0 );
				gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, starfield);
				gl.texImage2D( WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, starcanvas2dElement );
				gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
				gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);
				gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
		        
				drawPicture( gl, canvas3dElement, textures.get(0), starfield, g );
				
				setBuffers( gl, svb, nb, tb, ib, false );
		        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, starfield);
		        gl.drawElements(WebGLRenderingContext.TRIANGLES, 6, WebGLRenderingContext.UNSIGNED_SHORT, 0);
		        drawStuff( gl, g, 0, textures.get(0) );
				//drawStuff(Lelemental/html/WebGLRenderingContext;Lcom/google/gwt/core/client/JavaScriptObject;ILelemental/html/WebGLTexture;Lelemental/html/WebGLTexture;)(gl, g, g.box.numIndices, spiritTexture, starfield);
				
				/*rafc = new RequestAnimationFrameCallback() {
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						animate( gl, textures, buffers, starfield, g );
						
						if( rafc != null ) requestAnimationFrame( rafc ); //wnd.webkitRequestAnimationFrame( rafc );
						return true;
					}
				};
				requestAnimationFrame( rafc ); //wnd.webkitRequestAnimationFrame( rafc );*/
		        requestAnimationFrame( gl, textures, buffers, starfield, g );
			}
		});
		image.setVisible( false );
		image.setUrl("starfield.png");
		//Image.prefetch("starfield.png");
		RootPanel tex = RootPanel.get("texture");
		tex.add( image );
		
		danch = new Anchor("Download as movie (be patient)");
		danch.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				danch.setEnabled( false );
				offval = -50.0f;
				encoder = wham();
			}
		});
		
		HorizontalPanel	du = new HorizontalPanel();
		du.setSpacing( 5 );
		du.add( danch );
		//du.add( fbbutton );
		download.add( du );
		
		//vp.add( subvp );
		root.add( subvp );
		
		//drawPicture( gl, canvas3dElement, texture, g );
		/*reshape( gl, canvas3dElement, g );
		gl.clear( WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT );
		
		gl.enableVertexAttribArray(0);
		//gl.bindBuffer(gl.ARRAY_BUFFER, g.box.vertexObject);
		gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVerticesTextureCoordBuffer);
		gl.vertexAttribPointer(0, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
		
		gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, (ArrayBuffer)fa.buffer(), WebGLRenderingContext.STATIC_DRAW);*/
		//gl.drawArrays(WebGLRenderingContext.TRIANGLE_STRIP, 0, 4);
		
		/*final Timer timer = new Timer() {

			@Override
			public void run() 
		{
				drawPicture( gl, canvas3dElement, texture, g );
				//timer.schedule(200);
			}
		};
		timer.schedule(200);*/
	}
	
	Anchor	danch;
	com.google.gwt.dom.client.CanvasElement canvaselement;
	public void animate( WebGLRenderingContext gl, List<WebGLTexture> textures, List<WebGLBuffer> buffers, WebGLTexture starfield, JavaScriptObject g ) {
		setBuffers( gl, svb, nb, tb, ib, false );
		//gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, spiritTexture);
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, starfield);
        gl.drawElements(WebGLRenderingContext.TRIANGLES, 6, WebGLRenderingContext.UNSIGNED_SHORT, 0);
        
        gl.enable( WebGLRenderingContext.BLEND );
		//gl.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
        //gl.blendFunc(WebGLRenderingContext.ONE, WebGLRenderingContext.ONE);
        //gl.blendFunc(WebGLRenderingContext.ONE, WebGLRenderingContext.ONE_MINUS_CONSTANT_ALPHA);
        gl.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.DST_COLOR);
		
        float i = 0.0f;
        //WebGLTexture texture = textures.size() > 1 ? textures.get(1) : textures.get(2);
        int k = 0;
		for( WebGLTexture texture : textures ) {
			//WebGLTexture texture = textures.get(0);
			//console.log("erm "+ i);
			
			offset( offval-i, newy );
			i += 60.0f;
			
			setBuffers( gl, buffers.get(k), nb, tb, ib, true );
			k++;
			
			drawStuff( gl, g, 0, texture );
		}
		
		if( encoder != null ) {
			//com.google.gwt.dom.client.CanvasElement canvaselement = canvas3d.getCanvasElement();
			//for( int i = 0; i < 240; i++ ) {
			//Browser.getWindow().getConsole().log("erm "+i);
			addFrame( encoder, canvaselement );
				
			if( offval > 100.0f ) {
				Blob blob = compile( encoder );
				String url = createObjectURL( blob );
				Window.open( url, "_blank", "" );
				encoder = null;
				
				danch.setText( "Download as movie (Be patient)" );
				danch.setEnabled( true );
			} else {
				int pc = (int)(100*(offval+50.0)/150.0);
				danch.setText( "Download as movie ("+ pc +"%)" );
			}
			offval += 0.1f;
		} else offval += 0.07f;
	}
}
