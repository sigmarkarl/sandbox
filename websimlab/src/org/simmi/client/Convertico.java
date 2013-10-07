package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Convertico implements EntryPoint {

	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	@Override
	public void onModuleLoad() {
		final RootPanel rp = RootPanel.get();
		
		Window.enableScrolling( false );
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize(w+"px", h+"px");
			}
		});
		
		VerticalPanel vp = new VerticalPanel();
		vp.setSize( "100%", "100%");
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		FocusPanel fp = new FocusPanel();
		fp.setSize( "100%", "100%");
		fp.add( vp );
		fp.setFocus( true );
		
		HTML html = new HTML("Drop stuff to convert");
		vp.add( html );
		
		fp.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.getData("mp3");
				
				Audio a = Audio.createIfSupported();
				//a.getAudioElement().
			}
		});
		
		rp.add( fp );
	}

}
