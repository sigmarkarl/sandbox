package org.simmi.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Suggestadate implements EntryPoint {
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

	public native void fbInit( String login ) /*-{
		var ths = this;	    	
		$wnd.fbAsyncInit = function() {			
	    	$wnd.FB.init({appId: '390867450957358', status: true, cookie: true, xfbml: true, oauth : true});
	    	if( login == null ) {
		    	try {
		    		$wnd.console.log( "login null" );
		    		$wnd.console.log( $wnd.FB );
		    		$wnd.console.log( $wnd.FB.getLoginStatus );
					$wnd.FB.getLoginStatus( function(response) {
						$wnd.console.log( "inside login response" );
						try {
							if (response.status === 'connected') {
							    // the user is logged in and has authenticated your
							    // app, and response.authResponse supplies
							    // the user's ID, a valid access token, a signed
							    // request, and the time the access token 
							    // and signed request each expire
							    var uid = response.authResponse.userID;
							    var accessToken = response.authResponse.accessToken;
							    ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( uid );
							} else if (response.status === 'not_authorized') {
								ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( "" );
							    // the user is logged in to Facebook, 
							    // but has not authenticated your app
							} else {
							    // the user isn't logged in to Facebook.
							    ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( "" );
							}
							$wnd.FB.XFBML.parse();
						} catch( e ) {
							$wnd.console.log( "getLoginStatus error" );
							$wnd.console.log( e );
						}
					});
				} catch( e ) {
					$wnd.console.log( "gls error" );
					$wnd.console.log( e );
				}
			} else {
				$wnd.console.log( login );
				ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( login );
			}
	  	};
	}-*/;
	
	public native void console( String s ) /*-{
		$wnd.console.log( s );
	}-*/;
	
	public native void hehe() /*-{
		var ths = this;
		try {
			$wnd.FB.api('/me/friends', function(response) {
				for( i = 0; i < response.data.length; i++ ) {
					var frd = response.data[i];
					ths.@org.simmi.client.Suggestadate::putFriend(ILjava/lang/String;)( frd.id, frd.name );
				}
				ths.@org.simmi.client.Suggestadate::showFriendDialog()();
			
			});
		} catch( e ) {
			$wnd.console.log( e );
		}
	}-*/;
	
	Map<String,Integer>	fmap = new TreeMap<String,Integer>();
	public void putFriend( int id, String name) {
		fmap.put( name, id );
	}
	
	HorizontalPanel	hp;
	Anchor	a1;
	Anchor	a2;
	Anchor	currentAnchor;
	Button	match;
	public void showFriendDialog() {
		final int ia = hp.getWidgetIndex( currentAnchor );
		hp.remove( currentAnchor );
		//PopupPanel pp = new PopupPanel( true );
		
		final ListBox	lb = new ListBox();
		for( String uname : fmap.keySet() ) {
			lb.addItem( uname );
		}
		lb.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int i = lb.getSelectedIndex();
				String name = lb.getItemText( i );
				currentAnchor.setText( name );
				//currentAnchor.setName( Integer.toString(fmap.get(name)) );
				hp.remove(lb);
				hp.insert(currentAnchor, ia);
				
				if( match != null && !a1.getText().contains("Friend") && !a2.getText().contains("Friend") ) {
					match.setEnabled( true );
				}
			}
		});
		hp.insert(lb, ia);
		
		/*pp.add( lb );
		pp.center();*/
	}
	
	public void showLoginRequestDialog() {
		PopupPanel pp = new PopupPanel( true );
		
		HTML	req = new HTML("Please login to Facebook");
		pp.add( req );
		
		pp.center();
	}
	
	public native void fetchUserName() /*-{
		FB.api('/me', function(response) {
	    	ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( response.name );
	    });
	}-*/;
	
	public void setUserName( String val ) {
		uname = val;
	}
	
	String		uid = null;
	String		uname = null;
	SimplePanel	sp;
	Element		like;
	Element		login;
	public void setUserId( String val ) {
		uid = val;
		
		NodeList<Node> childs = sp.getElement().getChildNodes();
		for( int i = 0; i < childs.getLength(); i++ ) {
			sp.getElement().removeChild( childs.getItem(i) );
		}
		
		if( uid.length() > 0 ) sp.getElement().appendChild( like );
		else sp.getElement().appendChild( login );
		
		if( uid.length() > 0 ) {
			//getSuperPowers( uid, null );
			//sendListener( uid );
		}
	}
	
	public Element loginButton() {
		Element elem = Document.get().createElement("div");
		elem.setAttribute("class", "fb-login-button");
		//elem.setAttribute("scope", "user");
		
		return elem;
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
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
		
		like = Document.get().createElement("fb:like");
		like.setAttribute( "width", "200" );
		like.setAttribute( "font", "arial" );
		like.setAttribute( "layout", "button_count" );
		like.setAttribute( "colorscheme", "dark" );
		like.setAttribute( "send", "true" );
		like.setAttribute("href", "http://apps.facebook.com/suggestdate");
		like.setId("fblike");
		
		login = loginButton();
		sp = new SimplePanel();
		
		String fbuid = null;
		NodeList<Element> nl = Document.get().getElementsByTagName("meta");
		int i;
		for( i = 0; i < nl.getLength(); i++ ) {
			Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			if( prop.equals("fbuid") ) {
				//setUserId( e.getAttribute("content") );
				fbuid = e.getAttribute("content");
				break;
			}
		}
		fbInit( fbuid );
		
		String id = "facebook-jssdk";
		ScriptElement se = Document.get().createScriptElement();
		se.setId( id );
		se.setAttribute("async", "true");
		se.setSrc("//connect.facebook.net/en_US/all.js");
		Document.get().getElementById("fb-root").appendChild(se);
		
		VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSpacing( 5 );
		
		final Element ads = Document.get().getElementById("ads");
		if( ads != null ) {
			SimplePanel	adspanel = new SimplePanel();
			ads.removeFromParent();
			adspanel.getElement().appendChild( ads );
			vp.add( adspanel );
		}
		vp.add( new HTML("<h3>Suggest a date</h3>") );
		
		hp = new HorizontalPanel();
		hp.setSpacing( 5 );
		
		match = new Button("Match");
		match.setEnabled( false );
		a1 = new Anchor("Friend 1");
		a1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( uid != null ) {
					currentAnchor = a1;
					if( fmap.size() == 0 ) hehe();
					else showFriendDialog();
				} else showLoginRequestDialog();
			}
		});
		a2 = new Anchor("Friend 2");
		a2.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( uid != null ) {
					currentAnchor = a2;
					if( fmap.size() == 0 ) hehe();
					else showFriendDialog();
				} else showLoginRequestDialog();
			}
		});
		
		hp.add( new HTML("I suggest") );
		hp.add( a1 );
		hp.add( new HTML(" dates ") );
		hp.add( a2 );
		
		vp.add( sp );
		vp.add( hp );
		match.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String u1 = a1.getTitle();
				String u2 = a2.getTitle();
				String date = uname+"\t"+u1+"\t"+u2+"\t"+uid+"\t"+fmap.get(u1)+"\t"+fmap.get(u2);
				greetingService.greetServer(date, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(String result) {
						
					}
				});
			}
		});
		vp.add( match );
		
		vp.add( new HTML("<h3>Your dates</h3>") );
	
		final Grid	grid = new Grid();
		grid.resizeColumns(3);
		greetingService.greetServer("", new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String[]	split = result.split("\n");
				grid.resizeRows(split.length);
				
				int r = 0;
				for( String lin : split ) {
					String[] subsplit = lin.split("\t");
					
					final String key = subsplit[0];
					String name = subsplit[1];
					grid.setText(r, 0, name);
					
					final int row = r;
					Anchor	accept = new Anchor("Accept");
					accept.addClickHandler( new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							greetingService.greetServer("delete "+key, new AsyncCallback<String>() {
								@Override
								public void onSuccess(String result) {
									grid.removeRow(row);
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
					});
					grid.setWidget(r, 1, accept);
					Anchor	reject = new Anchor("Reject");
					accept.addClickHandler( new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							greetingService.greetServer("delete "+key, new AsyncCallback<String>() {
								@Override
								public void onSuccess(String result) {
									grid.removeRow(row);
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
					});
					grid.setWidget(r, 2, reject);
					
					r++;
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		vp.add( grid );
		vp.add( new HTML("*your date will only know you accepted if he/she self accepts") );
		
		HorizontalPanel bot = new HorizontalPanel();
		bot.setSpacing( 5 );
		Anchor l1 = new Anchor("http://suggestadate.appspot.com");
		l1.setHref( "http://suggestadate.appspot.com" );
		Anchor l2 = new Anchor("http://apps.facebook.com/suggestdate");
		l2.setHref( "http://apps.facebook.com/suggestdate" );
		Anchor l3 = new Anchor("huldaeggerts@gmail.com");
		l3.setHref( "mailto:huldaeggerts@gmail.com" );
		bot.add( l1 );
		bot.add( new HTML("|") );
		bot.add( l2 );
		bot.add( new HTML("|") );
		bot.add( l3 );
		vp.add( bot );
		
		VerticalPanel mainvp = new VerticalPanel();
		mainvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//mainvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		mainvp.setSize("100%", "100%");
		mainvp.add( vp );
		rp.add( mainvp );
	}
}
