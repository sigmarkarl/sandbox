package org.simmi;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import org.lwjgl.opengl.GL11;

public class SurfaceDraw {
	int			matSize;
	FloatBuffer	vertexBuffer = null;
	FloatBuffer	dataBuffer = null;
	ByteBuffer	colorBuffer = null;
	FloatBuffer modelBuffer;
	FloatBuffer lightBuffer;
	int 		size = 6;
    int 		x, y;
	
	public SurfaceDraw( int matSize ) {
		this.matSize = matSize;
		
		initBuffers();
		initLight();
		loadData();
	}
	
	public void mouseDown( GL11 gl, int ex, int ey, int ebutton ) {
		x = ex;
		y = ey;
	}
	 
	public void mouseRightDrag( GL11 gl, int ex, int ey ) {
		float fy = ey-y;
		float fx = x-ex;
		GL11.glMatrixMode( GL11.GL_MODELVIEW );
		GL11.glLoadIdentity();
		float a = (float)Math.sqrt(fy*fy+fx*fx);
		if( a > 0.1f ) GL11.glRotatef( (float)(0.2*a), fy/a, fx/a, 0.0f );
		GL11.glMultMatrix( modelBuffer );
	}
	
	public void initBuffers() {
		int matSqure = matSize*matSize;
		
		vertexBuffer = FloatBuffer.allocate( size*(matSqure)*10 );
		colorBuffer = ByteBuffer.allocate( matSqure );
		Arrays.fill( colorBuffer.array(), (byte)0 );
		
		modelBuffer = FloatBuffer.allocate( 16 );
		lightBuffer = FloatBuffer.allocate( 19 );
		
		Random random = new Random();
		dataBuffer = FloatBuffer.allocate( matSqure );
		for( int i = 0; i < dataBuffer.limit(); i++ ) {
			dataBuffer.put(i, random.nextFloat());
		}
	}
	
	public void initMatrix() {
		GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, modelBuffer );
	}
	
	public void initLights( GL11 gl ) {
		int light = 1;
		//float[]	exp_cut = {0.0f, 500.0f};
		//FloatBuffer	fb_exp_cut = FloatBuffer.wrap(exp_cut);
		FloatBuffer f = lightBuffer;
		f.rewind();
		GL11.glEnable( GL11.GL_LIGHTING );
		GL11.glEnable( GL11.GL_LIGHT0 );
		GL11.glShadeModel( GL11.GL_SMOOTH );
		GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		int lightNumber = light+GL11.GL_LIGHT0;
		GL11.glEnable( lightNumber );
		GL11.glLight(lightNumber, GL11.GL_POSITION, f );
		GL11.glLight(lightNumber, GL11.GL_AMBIENT, f );
		GL11.glLight(lightNumber, GL11.GL_DIFFUSE, f );
		GL11.glLight(lightNumber, GL11.GL_SPECULAR, f );
		GL11.glLight(lightNumber, GL11.GL_SPOT_DIRECTION, f );
		//GL11.glLight(lightNumber, GL11.GL_SPOT_EXPONENT, fb_exp_cut );
		//GL11.glLight(lightNumber, GL11.GL_SPOT_CUTOFF, fb_exp_cut );
	}
	
	public void initLight( ) {
		FloatBuffer fLightBuffer = lightBuffer;
		fLightBuffer.put(0, 0.0f);
		fLightBuffer.put(1, 0.0f);
		fLightBuffer.put(2, 100.0f);
		fLightBuffer.put(3, 1.0f);
		
		fLightBuffer.put(4, 0.3f);
		fLightBuffer.put(5, 0.3f);
		fLightBuffer.put(6, 0.3f);
		fLightBuffer.put(7, 1.0f);
		
		fLightBuffer.put(8, 0.8f);
		fLightBuffer.put(9, 0.8f);
		fLightBuffer.put(10, 0.8f);
		fLightBuffer.put(11, 1.0f);
		
		fLightBuffer.put(12, 0.5f);
		fLightBuffer.put(13, 0.5f);
		fLightBuffer.put(14, 0.5f);
		fLightBuffer.put(15, 1.0f);
		
		fLightBuffer.put(16, 0.0f); //-normMatrix.get(2));
		fLightBuffer.put(17, 0.0f); //-normMatrix.get(6));
		fLightBuffer.put(18, 1.0f); //-normMatrix.get(10));
	}
	
	public void loadData() {
		makeSurface();
	}
	
	public void makeSurface() {
		makeSurface( 0,0,matSize-1,matSize-1 );
	}
	
	public void makeSurface( int x1, int y1, int x2, int y2 ) {
    	makeSurface( x1, y1, x2, y2, false, null );
    }
	
	public void makeSurface( int x1, int y1, int x2, int y2, boolean selecting, Set<Integer> selectFilter ) {
		int xx1 = Math.max( 0, x1 );
		int xx2 = Math.min( matSize-1, x2 );
		int yy1 = Math.max( 0, y1 );
		int yy2 = Math.min( matSize-1, y2 );
		makeSurface( vertexBuffer, selecting, xx1, yy1, xx2, yy2, 0, selectFilter);		
	}
	 
	public void makeSurface( FloatBuffer floatBuffer, boolean selecting, int x1, int y1, int x2, int y2, int offset, Set<Integer> selectFilter ) {
		for( int r = y1; r < y2; r++ ) {
			for( int c = x1; c < x2; c++ ) {
				int i = r*matSize+c + offset;
				int k = 10*size*i;

				float fasti = 0.2f;
				
				float c1 = fasti*(c-matSize/2.0f);
				float c2 = fasti*(c+1-matSize/2.0f);
				float r1 = fasti*(r-matSize/2.0f);
				float r2 = fasti*(r+1-matSize/2.0f);
				
				boolean select = false;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				float h1 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl1 = colorBuffer.get(i);
				
				i = (r+1)*matSize+c + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				float h2 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl2 = colorBuffer.get(i);
				i = (r+1)*matSize+c+1 + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;;
				float h3 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl3 = colorBuffer.get(i);
				
				float Qx = c1-c1;
				float Qy = r2-r1;
				float Qz = h2-h1;
				float Px = c2-c1;
				float Py = r2-r1;
				float Pz = h3-h1;

				float nx = Py*Qz - Pz*Qy;
				float ny = Pz*Qx - Px*Qz;
				float nz = Px*Qy - Py*Qx;
				
				float div = (float)Math.sqrt( nx*nx+ny*ny+nz*nz );
				if( selecting ) {
					if( selectFilter == null ) div /= 2.0;
					else if( select ) {
						div /= 2.0;
					}
				}
				
				nx /= div;
				ny /= div;
				nz /= div;
				
				float clr = cl1 == 1 ? 1.0f : 0.5f;
				float clg = cl1 == 2 ? 1.0f : 0.5f;
				float clb = cl1 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h1);
				
				clr = cl2 == 1 ? 1.0f : 0.5f;
				clg = cl2 == 2 ? 1.0f : 0.5f;
				clb = cl2 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h2);
				
				clr = cl3 == 1 ? 1.0f : 0.5f;
				clg = cl3 == 2 ? 1.0f : 0.5f;
				clb = cl3 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h3);
				
				select = false;
				
				i = (r+1)*matSize+c+1 + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h1 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl1 = colorBuffer.get(i);
				i = r*matSize+c+1 + offset;
				
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h2 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl2 = colorBuffer.get(i);
				i = (r)*matSize+c + offset;
				
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h3 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl3 = colorBuffer.get(i);
				
				Qx = c2-c2;
				Qy = r1-r2;
				Qz = h2-h1;
				Px = c1-c2;
				Py = r1-r2;
				Pz = h3-h1;

				nx= Py*Qz - Pz*Qy;
				ny = Pz*Qx - Px*Qz;
				nz = Px*Qy - Py*Qx;
				
				div = (float)Math.sqrt( nx*nx+ny*ny+nz*nz );
				if( selecting ) {
					if( selectFilter == null ) div /= 2.0;
					else if( select ) {
						div /= 2.0;
					}
				}
				nx /= div;
				ny /= div;
				nz /= div;
				
				clr = cl1 == 1 ? 1.0f : 0.5f;
				clg = cl1 == 2 ? 1.0f : 0.5f;
				clb = cl1 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h1);
				
				clr = cl2 == 1 ? 1.0f : 0.5f;
				clg = cl2 == 2 ? 1.0f : 0.5f;
				clb = cl2 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h2);
				
				clr = cl3 == 1 ? 1.0f : 0.5f;
				clg = cl3 == 2 ? 1.0f : 0.5f;
				clb = cl3 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h3);
			}
		}
	}

	public void draw() {
		int count = vertexBuffer.limit();
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
		
		GL11.glEnable( GL11.GL_COLOR_MATERIAL );
		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glEnable( GL11.GL_LIGHTING );
		
		GL11.glEnable( GL11.GL_CULL_FACE );
		GL11.glCullFace( GL11.GL_BACK );
		GL11.glInterleavedArrays( GL11.GL_C4F_N3F_V3F, 0, vertexBuffer );
		GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, count/(10) );
		GL11.glCullFace( GL11.GL_FRONT );
		GL11.glInterleavedArrays( GL11.GL_C4F_N3F_V3F, 0, vertexBuffer );
		GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, count/(10) );
		
		GL11.glDisable( GL11.GL_COLOR_MATERIAL );
		GL11.glDisable( GL11.GL_DEPTH_TEST );
		GL11.glDisable( GL11.GL_LIGHTING );
	}
}
