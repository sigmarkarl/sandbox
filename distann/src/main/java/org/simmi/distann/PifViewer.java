package org.simmi.distann;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class PifViewer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		
		File f = new File( "/home/sigmar/00638isl.pif" );
		byte[] bb = new byte[ (int)f.length() ];
		try {
			FileInputStream	fis = new FileInputStream( f );
			fis.read( bb );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final BufferedImage	img = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);
		ByteBuffer buf = ByteBuffer.wrap(bb, 12, bb.length-12);
		buf.order( ByteOrder.BIG_ENDIAN );
		ShortBuffer sb = buf.asShortBuffer();
		short max = 1024;
		/*for( int i = 0; i < sb.limit(); i++ ) {
			short sval = sb.get(i);
			if( sval > max ) max = sval;
		}*/
		
		for( int y = 0; y < 4096; y++ ) {
			for( int x = 0; x < 4096; x++ ) {
				int val = Math.min( 255, (sb.get(y*4096+x)*255)/max );
				int rgb = (val<<0)+(val<<8)+(val<<16);
				img.setRGB( x, y, rgb );
			}
		}
		
		JComponent c = new JComponent() {
			public void paintComponent( Graphics g ) {
				g.drawImage( img, 0, 0, this );
			}
		};
		Dimension d = new Dimension(4096,4096);
		c.setPreferredSize( d );
		c.setSize( d );
		JScrollPane	scrollpane = new JScrollPane(c);
		frame.add( scrollpane );
		frame.setVisible( true );
	}
}
