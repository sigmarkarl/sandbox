package org.simmi;

import java.applet.Applet;

import netscape.javascript.JSObject;

public class JSUtil {
	public static void call( Applet applet, String f, Object[] objs ) {
		JSObject win = JSObject.getWindow(applet);
		win.call( f, objs );
	}
}
