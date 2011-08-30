package org.simmi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JApplet;

public class SerifyApplet extends JApplet {
	public void init() {
		
	}
	
	public void paint( Graphics g ) {
		super.paint( g );
		
		g.setColor( Color.red );
		g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
	}
	
	public String getUser() {
		return System.getProperty("user.name");
	}
}
