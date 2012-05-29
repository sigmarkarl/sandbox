package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Javaconnectron implements EntryPoint {

	@Override
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		Style s = rp.getElement().getStyle();
		s.setMargin(0.0, Unit.PX);
		s.setPadding(0.0, Unit.PX);
		s.setBorderWidth(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Window.enableScrolling( false );
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
	}

}
