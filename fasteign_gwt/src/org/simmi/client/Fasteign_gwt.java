package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Fasteign_gwt implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");
		
		//vp.add( new HTML("<h1>Hvað á fasteignin að kosta?</h1>") );
		//vp.add( new HTML("Forritið safnar upplýsingum um fasteignir á svipuðum stað og af svipaðri stærð og fasteignin þín.<br>Notaðu svo miðgildið eða meðaltalið af verði/fasteignamat (eða annað sem hentar betur, t.d. fermetraverð)<br>Tvíklikkaðu á íbúðir til að skoða nánar í vafra. Hægt er að raða íbúðunum með því að smella á dálkanöfnin<br>Einnig er hægt að henda út íbúðum úr meðaltalinu með því að hægriklikka og velja henda") );
		//vp.add( new HTML("<applet codebase=\"http://fasteignaverd.appspot.com\" archive=\"fasteign.jar,poi/poi-3.7-beta3-20100924.jar,poi/poi-ooxml-3.7-beta3-20100924.jar,poi/poi-ooxml-schemas-3.7-beta3-20100924.jar,poi/xmlbeans-2.3.0.jar,poi/dom4j-1.6.1.jar,poi/geronimo-stax-api_1.0_spec-1.0.jar\" code=\"org.simmi.Fasteign\" width=\"1200\" height=\"600\" jnlp_href=\"fasteign.jnlp\"><param name=\"jnlp_href\" value=\"fasteign.jnlp\"/></applet>") );
		//vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://webspectroscope.appspot.com\">http://webspectroscope.appspot.com</a> | <a href=\"http://webconnectron.appspot.com\">http://webconnectron.appspot.com</a> | <a href=\"http://nutritiondb.appspot.com\">http://nutritiondb.appspot.com</a> | <a href=\"http://websimlab.appspot.com\">http://websimlab.appspot.com</a> | <a href=\"http://webnavision.appspot.com\">http://webnavision.appspot.com</a>") );
		/*vp.add(	new HTML("<script>var attributes = { codebase:'http://localhost:8888/', archive:'fasteign.jar', code:'org.simmi.Fasteign', width:'800', height:'600', id:'fasteign' };"+
							"var parameters = { jnlp_href:'fasteign.jnlp', centerimage:'true', boxborder:'false', draggable:'true' };"+
							"deployJava.runApplet(attributes, parameters, '1.6');</script>") );*/
		
		RootPanel.get("fastdiv").add( vp );
	}
}
