package org.simmi;

import java.applet.Applet;

import netscape.javascript.JSObject;

public class JSUtil {
	public static void call( Applet applet, String f, Object[] objs ) {
		//JSObject win = JSObject.getWindow(applet);
		//win.call( f, objs );
	}
	
	public static void console( Applet applet, String message ) {
		/*try {
			JSObject win = JSObject.getWindow( applet );
			JSObject con = (JSObject)win.getMember("console");
                        con.call("log", new Object[] {message} );
		} catch( Exception e ) {
			e.printStackTrace();
		}*/
	}
}
