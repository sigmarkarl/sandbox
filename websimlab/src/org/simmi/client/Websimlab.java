package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Websimlab implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public static native void deploySimlab() /*-{
		var attributes = { codebase:'http://dl.dropbox.com/u/10024658/simlab/', archive:'simlab.jar:jna.jar', code:'org.simmi.SimCard', width:'1024', height:'600', id:'simlabs', name:'simlab' };
      	var parameters = { jnlp_href:'simlab.jnlp' };
      	$wnd.deployJava.runApplet(attributes, parameters, '1.6')
	}-*/;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");
		
		vp.add( new HTML("<h1>Web Simlab</h1>") );
		vp.add( new HTML("<h3>You need <a href=\"http://www.java.com/\">Java</a> to run this. Run as <a href=\"simlab.jnlp\">program</a>.</h3>") );
		vp.add( new HTML("A small exercise in programming languages. Uses similar syntax as assembly but works on vector.<br>No for/while or ifs, not even compare and jump like in assembly.<br>Just vectors of certain length and modulus for repeats. Transpose function is very useful for rearranging data in memory.<br>Small demo application will be posted irregularly, the first being <a href=\"fagen.sl\">fagen.sl</a> a simple script showing period doubling (chaos theory).<br>You can either paste into the script window (and do enter) or save and write read \"[filepath]\" and press enter then write parse in the next line.<br>") ); 
		vp.add( new HTML("Here is an example of how to do an histogram equalization on an image <a href=\"histo.sl\">histo.sl</a>. Each color channel is calculated independently so the colors can become skewed.") );
		
		//deploySimlab();
		//Originaly written in C++, it depends on native libraries. As there is no cross-compiler readily available for mac (e.g. you have to own a mac to compile for mac), macs are not supported.") );
		vp.add( new HTML("<applet codebase=\"http://dl.dropbox.com/u/10024658/simlab/\" archive=\"simlab.jar,jna.jar\" code=\"org.simmi.SimCard\" width=\"1024\" height=\"600\" jnlp_href=\"simlab.jnlp\"><param name=\"jnlp_href\" value=\"simlab.jnlp\"/></applet>") );
		vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://webspectroscope.appspot.com\">http://webspectroscope.appspot.com</a> | <a href=\"http://webconnectron.appspot.com\">http://webconnectron.appspot.com</a> | <a href=\"http://nutritiondb.appspot.com\">http://nutritiondb.appspot.com</a> | <a href=\"http://fasteignaverd.appspot.com\">http://fasteignaverd.appspot.com</a>") );
		
		RootPanel.get("simlab").add( vp );
	}
}
