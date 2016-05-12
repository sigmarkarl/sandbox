package org.simmi;

import java.awt.Component;

import javax.swing.JSplitPane;

public class SimSplitPane extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SimSplitPane() {
		super();
		
		init();
	}
	
	public SimSplitPane( int ori ) {
		super( ori );
		
		init();
	}

	public void init() {
		this.setOneTouchExpandable( true );
	}
	
	public void swapComponents() {
		Component top = this.getTopComponent();
		Component bottom = this.getBottomComponent();
		
		this.setTopComponent( null );
		this.setBottomComponent( null );
		
		this.setTopComponent( bottom );
		this.setBottomComponent( top );
	}
}
