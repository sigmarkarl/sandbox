package org.simmi;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JSplitPane;

public class LinkedSplitPane extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LinkedSplitPane pane;
	boolean paint = true;

	public LinkedSplitPane(int or, Component lt, Component rt) {
		super(or, lt, rt);
		
		this.setBackground( Color.white );
		// this.setDividerSize( 5 );
	}

	public void setDividerLocationSuper(int d) {
		/*
		 * if( this == leftSplitPane ) { System.err.println( "lefty" ); }
		 * else if( this == rightSplitPane ) { System.err.println( "righty"
		 * ); }
		 */

		paint = false;
		super.setDividerLocation(d);
		// this.invalidate();
		// this.repaint();
	}

	public void setDividerLocation(int d) {
		if (paint)
			pane.setDividerLocationSuper(d);
		else
			paint = true;
		super.setDividerLocation(d);
	}

	public void setDividerLocationSuper(double d) {
		paint = false;
		super.setDividerLocation(d);
		// paint = true;
		// this.invalidate();
		// this.repaint();
	}

	public void setDividerLocation(double d) {
		if (paint)
			pane.setDividerLocationSuper(d);
		else
			paint = true;
		super.setDividerLocation(d);
	}

	public void setLinkedSplitPane(LinkedSplitPane pane) {
		this.pane = pane;
	}

	public boolean isVisible() {
		return super.isVisible();
	}

	public boolean isShowing() {
		return super.isShowing();
	}
}