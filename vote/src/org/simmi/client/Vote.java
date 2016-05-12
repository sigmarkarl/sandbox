package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Vote implements EntryPoint {
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
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	String 					selected = null;
	List<CheckBox>			allcheck = null;
	String					key = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		final Grid		grid = new Grid( 7, 16 );
		grid.setSize("800px", "600px");
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		vp.setSize(w+"px", h+"px");
		//vp.setSize( "100%", "100%" );
		
		//HTML title = new HTML("Kosning í Starfsmannaráð Matís 2011<br>(veljið þrjá, kosið er jafnóðum og hnappur er valinn. Hægt er að breyta valinu seinna.)");
		HTML title = new HTML("Kosning öryggistrúnaðarmanna Matís 2013<br>(veljið tvo, kosið er jafnóðum og hnappur er valinn. Hægt er að breyta valinu seinna.)");
		vp.add( title );
		vp.add( grid );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				vp.setSize(w+"px", h+"px");
			}
		});
		
		final String uid = Location.getParameter("uid");
		
		/*String uid = null;
		NodeList<Element> nl = Document.get().getElementsByTagName("meta");
		int i;
		for( i = 0; i < nl.getLength(); i++ ) {
			Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			if( prop.equals("uid") ) {
				uid = e.getAttribute("content");
				break;
			}
		}*/
		
		final List<CheckBox>	checkList = new ArrayList<CheckBox>();
		greetingService.greetServer( uid, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				selected = result;
				
				if( selected != null ) {
					String[] split = selected.split("\t");
					List<String> lst = Arrays.asList(split);
					key = lst.get(0);
					if( allcheck != null ) {
						Set<String>	set = new HashSet<String>( lst.subList(1, lst.size()) );
						for( CheckBox cb : allcheck ) {
							if( set.contains( cb.getText() ) ) {
								cb.setValue( true );
								if( !checkList.contains( cb ) ) checkList.add( cb );
							}
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
		RequestBuilder rb = new RequestBuilder( RequestBuilder.POST, "/newnew.txt" );
		try {
			rb.sendRequest("", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String list = response.getText();
					String[] lines = list.split("\n");
					
					Set<String>	set = null;
					allcheck = new ArrayList<CheckBox>();
					int i = 0;
					for( int k = 0; k < lines.length; k++ ) {
						String name = lines[k];
						String[] split = name.split("\t");
						
						VerticalPanel ver = new VerticalPanel();
						ver.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
						ver.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
						
						final CheckBox	cap = new CheckBox( split[0] );
						allcheck.add( cap );
						if( set == null && selected != null ) {
							set = new HashSet<String>( Arrays.asList( selected.split("\t") ) );						
						}
						
						if( set != null ) {
							if( set.contains( cap.getText() ) ) {
								cap.setValue( true );
								if( !checkList.contains( cap ) ) checkList.add( cap );
							}
						}
						
						Style s = cap.getElement().getStyle();
						s.setFontSize(9.0, Unit.PX);
						
						final int fjoldi = 2; //3
						cap.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								if( event.getValue() ) {
									if( checkList.size() == fjoldi ) {
										CheckBox desel = checkList.remove(0);
										desel.setValue( false );
									}
									checkList.add( cap );
								} else {
									checkList.remove( cap );
								}
								
								String vote = null;
								if( checkList.size() > 0 ) {
									vote = checkList.get(0).getText();
									for( int k = 1; k < checkList.size(); k++ ) {
										vote += "\t"+checkList.get(k).getText();
									}
								}
								greetingService.saveVote( key, uid, vote, new AsyncCallback<String>() {
									@Override
									public void onFailure(Throwable caught) {
										
									}

									@Override
									public void onSuccess(String result) {
										key = result;
									}
								});
							}
						});
												
						if( split.length > 2 ) {
							String imgsrc = split[2];
							int f = imgsrc.indexOf('"');
							int n = imgsrc.indexOf('"', f+1);
							String imgurl = imgsrc.substring(f+1, n);
							Image 		img = new Image( imgurl );
							img.setPixelSize(70, 90);
							ver.add( img );
						} else {
							SimplePanel dum = new SimplePanel();
							dum.setSize( "70px", "90px");
							ver.add( dum );
						}
						ver.add( cap );
						grid.setWidget( i/16, i%16, ver );
						
						i++;
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		rp.add( vp );
	}
}
