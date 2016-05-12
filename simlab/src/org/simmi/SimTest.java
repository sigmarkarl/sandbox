package org.simmi;

import com.sun.jna.Native;

public class SimTest {

	static {
		Native.register("csimlab");
	}
	
	public static native void sim();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println( "do nothing" );
	}
}
