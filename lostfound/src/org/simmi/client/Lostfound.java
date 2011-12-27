package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Lostfound implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Style st = rp.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		
		HorizontalPanel	hp = new HorizontalPanel();
		hp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		//hp.setSize(800+"px", 600+"px");
		
		VerticalPanel	vpleft = new VerticalPanel();
		VerticalPanel	vpright = new VerticalPanel();
		
		hp.add( vpleft );
		hp.add( vpright );
		
		Label	l1 = new Label("Tapað");
		Label	l2 = new Label("Fundið");
		
		HorizontalPanel	lostwhat = new HorizontalPanel();
		Label	lostlab = new Label("Hvað týndist");
		ListBox	lostlist = new ListBox();
		lostlist.addItem("Húfa");
		lostlist.addItem("Vettlingar");
		lostlist.addItem("Fatnaður");
		lostlist.addItem("Húslyklar");
		lostlist.addItem("Bíllyklar");
		lostlist.addItem("Veski");
		lostlist.addItem("Kort");
		lostlist.addItem("GSM-sími");
		lostlist.addItem("Hjól");
		lostlist.addItem("Sleði/Snjóþota");
		lostlist.addItem("Annað");
		lostwhat.add( lostlab );
		lostwhat.add( lostlist );
		
		HorizontalPanel	foundwhat = new HorizontalPanel();
		Label	foundlab = new Label("Hvað fannst");
		ListBox	foundlist = new ListBox();
		foundlist.addItem("Húfa");
		foundlist.addItem("Vettlingar");
		foundlist.addItem("Fatnaður");
		foundlist.addItem("Húslyklar");
		foundlist.addItem("Bíllyklar");
		foundlist.addItem("Veski");
		foundlist.addItem("Kort");
		foundlist.addItem("GSM-sími");
		foundlist.addItem("Hjól");
		foundlist.addItem("Sleði/Snjóþota");
		foundlist.addItem("Annað");
		foundwhat.add( foundlab );
		foundwhat.add( foundlist );
		
		HorizontalPanel	first = new HorizontalPanel();
		Label	firstlab = new Label("Í fyrsta lagi");
		DatePicker	firstdate = new DatePicker();
		first.add( firstlab );
		first.add( firstdate );
		
		HorizontalPanel	last = new HorizontalPanel();
		Label	lastlab = new Label("Í síðasta lagi");
		DatePicker	lastdate = new DatePicker();
		last.add( lastlab );
		last.add( lastdate );
		
		vpleft.add( l1 );
		vpleft.add( lostwhat );
		vpleft.add( first );
		vpright.add( l2 );
		vpright.add( foundwhat );
		vpright.add( last );
		
		rp.add( hp );
	}
}
