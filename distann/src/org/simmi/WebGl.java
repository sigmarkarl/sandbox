package org.simmi;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;

import netscape.javascript.JSObject;

public class WebGl extends JApplet {
	public void paint( Graphics g ) {
		super.paint(g);
		Graphics2D g2;
		
		g.drawString("hoho", 10, 10);
	}
	
	public void retur() {
		System.err.println("inret");
		/*try {
			final GLProfile gl2Profile = GLProfile.get(GLProfile.GL2ES2);
			final GLContext context = GLDrawableFactory.getFactory(gl2Profile).createExternalGLContext();
			
			context.makeCurrent();
		    GL2ES2 gl = context.getGL().getGL2ES2();
		    gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
		    gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT );
		    context.release();
		} catch( Exception e ) {
			e.printStackTrace();
		}*/
	}
	
	public void init() {
		final JSObject jo = JSObject.getWindow(this);
		
		jo.call( "stuff", new Object[] {} );
		
		/*final TextRenderer renderer = new TextRenderer( this.getFont() );
		javax.swing.Timer t = new Timer( 1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jo.call("drawPicture", new Object[] {});
				
				//GL2ES2 g2e2 = new Gl
				//gl.glClearColor(0.0f, 1.0f, 0.5f, 1.0f);
				//gl.glClear( GL.GL_COLOR_BUFFER_BIT );
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D( "Kjallari", -0.5f, 1.1f, 0.0f, 0.001f );
				renderer.end3DRendering();
			}
		});
		t.start();*/
		
		//jo.call("drawPicture", new Object[] {});
		//jo.call("test", new Object[] {});

		/*GLCapabilities caps = new GLCapabilities(glp);
		caps.setSampleBuffers(true);
		caps.setNumSamples(8);*/

		/*GLCanvas glCanvas = new GLCanvas();
		glCanvas.addGLEventListener( new GLEventListener() {
			@Override
			public void reshape(GLAutoDrawable arg0, int x, int y, int w, int h) {
				GL gl = arg0.getGL();
				
				GL2 gl2 = gl.getGL2();
				gl2.glMatrixMode( GL2.GL_PROJECTION );
				gl2.glLoadIdentity();
				if (w > h) {
					double aspect = 0.01*(double)w/(double)h;
				    gl2.glFrustum(-aspect, aspect, -0.01, 0.01, 0.01, 5000.0);
				} else {
					double aspect = 0.01*(double)h/(double)w;
				    gl2.glFrustum (-0.01, 0.01, -aspect, aspect, 0.01, 5000.0);
				}
				gl2.glTranslatef(0.0f, 0.0f, -1.0f);
			}
			
			@Override
			public void init(GLAutoDrawable gld) {
				GL gl = gld.getGL();
				gl.glClearColor(0.0f, 1.0f, 0.5f, 1.0f);
			}
			
			@Override
			public void dispose(GLAutoDrawable arg0) {
				
			}
			
			@Override
			public void display(GLAutoDrawable gld) {
				GL gl = gld.getGL();
				gl.glClear( GL.GL_COLOR_BUFFER_BIT );
				
				//gl.glc
				//gl.gl
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D("Simmi best", 0.0f, 0.0f, 0.0f, 0.01f);
				renderer.end3DRendering();
			}
		});
		this.add(glCanvas);*/
	}
}
