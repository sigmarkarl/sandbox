package org.simmi.client;

import java.util.ArrayList;
import java.util.List;
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
import com.google.gwt.user.client.ui.Widget;

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
			$wnd.FB.api('/me/friends', {fields: 'id,name,link'}, function(response) {
				for( i = 0; i < response.data.length; i++ ) {
					var frd = response.data[i];
					ths.@org.simmi.client.Suggestadate::putFriend(ILjava/lang/String;Ljava/lang/String;)( frd.id, frd.name, frd.link );
				}
				ths.@org.simmi.client.Suggestadate::showFriendDialog()();
			
			});
		} catch( e ) {
			$wnd.console.log( e );
		}
	}-*/;
	
	Map<String,Integer>	fmap = new TreeMap<String,Integer>();
	Map<Integer,String>	lmap = new TreeMap<Integer,String>();
	public void putFriend( int id, String name, String link ) {
		fmap.put( name, id );
		lmap.put( id, link );
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
		
		int sel = -1;
		final ListBox	lb = new ListBox();
		for( String uname : fmap.keySet() ) {
			if( uname.equals(currentAnchor.getText()) ) sel = lb.getItemCount();
			lb.addItem( uname );
		}
		if( sel != -1 ) lb.setSelectedIndex( sel );
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
		var ths = this;
		$wnd.FB.api('/me', function(response) {
	    	ths.@org.simmi.client.Suggestadate::setUserName(Ljava/lang/String;)( response.name );
	    });
	}-*/;
	
	public native void fetchUserEmail( final String key ) /*-{
		var ths = this;
		$wnd.FB.api('/me', function(response) {
	    	ths.@org.simmi.client.Suggestadate::setUserEmail(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( key, response.id, response.email );
	    });
	}-*/;
	
	public native void sendAcceptMessage( String fuid, String body, final int row, final String femail ) /*-{
		var ths = this;
		var requestCallback = function(response) {
	    	$wnd.console.log( response );
	    	ths.@org.simmi.client.Suggestadate::addEmail(ILjava/lang/String;)( row, femail );
	  	}
	  
		$wnd.FB.ui({method: 'apprequests',
	    	message: body,
	    	to: fuid
	  	}, requestCallback);
	}-*/;
	
	public native void sendMessage( String fuid1, String fuid2, String fname, String body ) /*-{
		var ths = this;
		$wnd.console.log( "about to request" );
		
		var requestCallback = function(response) {
        	$wnd.console.log( response );
      	}
      
		$wnd.FB.ui({method: 'apprequests',
	    	message: body,
	    	to: fuid1+','+fuid2
	  	}, requestCallback);
//		$wnd.FB.api('/me/feed', 'post', { to: friend, message: body }, function(response) {
//		  if (!response || response.error) {
//		    if( !response ) alert('Error occured');
//		    else $wnd.console.log( response.error );
//		  } else {
//		    alert('Post ID: ' + response.id);
//		  }
//		});
		
		//$wnd.FB.api('/'+fuid+'/feed', function(response) {
	    //	ths.@org.simmi.client.Suggestadate::setUserName(Ljava/lang/String;)( response.name );
	    //});
    }-*/;
	
	public void setUserName( String val ) {
		uname = val;
	}
	
	public void addEmail( int row, String femail ) {
		Anchor mail = new Anchor( femail );
		mail.setTarget("_parent");
		mail.setHref( "mailto:"+femail );
		grid.setWidget(row, 1, mail);
	}
	
	List<String>	keys = new ArrayList<String>();
	public void setUserEmail( final String key, final String uid, final String email ) {
		console("set email "+key+"\t"+uid+"\t"+email);
		greetingService.greetServer("accept "+key+"\t"+uid+"\t"+email, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				int row = keys.indexOf( key );
				String[] split = result.split("\t");
				String	fuid = split[0];
				
				if( split.length > 1 && split[1].length() > 0 ) {
					String	femail = split[1];
					
					String body = "Your date request has been accepted";
					sendAcceptMessage(fuid, body, row, femail);
					//grid.setWidget(row, 1, mail);
				} else {
					grid.setText(row, 1, "waiting");
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				console("set email fail");
				console(caught.getMessage());
			}
		});
	}
	
	String		uid = "";
	String		uname = "";
	SimplePanel	sp;
	Element		like;
	Element		login;
	Grid		grid;
	public void setUserId( String val ) {
		uid = val;
		
		NodeList<Node> childs = sp.getElement().getChildNodes();
		for( int i = 0; i < childs.getLength(); i++ ) {
			sp.getElement().removeChild( childs.getItem(i) );
		}
		
		if( uid.length() > 0 ) sp.getElement().appendChild( like );
		else sp.getElement().appendChild( login );
		
		if( uid.length() > 0 ) {
			fetchUserName();
			greetingService.greetServer(uid, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					if( result.length() > 0 ) {
						console("fetch all "+result);
						
						String[]	split = result.split("\n");
						grid.resizeRows(split.length);
						
						keys.clear();
						int r = 0;
						for( String lin : split ) {
							String[] subsplit = lin.split("\t");
							
							final String key = subsplit[0];
							keys.add( key );
							
							String name = subsplit[1];
							String link = subsplit[2];
							Anchor	dateanchor = new Anchor(name);
							dateanchor.setTarget("_blank");
							dateanchor.setHref( link );
							grid.setWidget(r, 0, dateanchor);
							
							String email1 = "";
							String email2 = "";
							if( subsplit.length > 3 ) email1 = subsplit[3];
							if( subsplit.length > 4 ) email2 = subsplit[4];
							
							final int row = r;
							
							Widget w;
							if( email1.length() > 0 && email2.length() > 0 ) {
								Anchor a = new Anchor( email1 );
								a.setTarget("_parent");
								a.setHref("mailto:"+email1);
								w = a;
							} else if( email2.length() > 0 ) {
								HTML html = new HTML("waiting");
								w = html;
							} else {
								Anchor	accept = new Anchor("Accept");
								accept.addClickHandler( new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										fetchUserEmail( key );
									}
								});
								w = accept;
							}
							grid.setWidget(r, 1, w);
							
							Anchor	reject = new Anchor("Remove");
							reject.addClickHandler( new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									greetingService.greetServer("delete "+key, new AsyncCallback<String>() {
										@Override
										public void onSuccess(String result) {
											console( "successfully removed row "+row+" using key "+key );
											grid.removeRow( keys.indexOf(key) );
											keys.remove( key );
										}
										
										@Override
										public void onFailure(Throwable caught) {
											console( "delete failure " + key );
											console( caught.getMessage() );
										}
									});
								}
							});
							grid.setWidget(r, 2, reject);
							
							r++;
						}
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
			//getSuperPowers( uid, null );
			//sendListener( uid );
		}
	}
	
	public Element loginButton() {
		Element elem = Document.get().createElement("div");
		elem.setAttribute("class", "fb-login-button");
		elem.setAttribute("scope", "email");
		
		return elem;
	}
	
	/**
	 * This is the entry point method.	hkt
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
				final String u1 = a1.getText();
				final String u2 = a2.getText();
				final Integer ui1 = fmap.get(u1);
				final Integer ui2 = fmap.get(u2);
				final String l1 = lmap.get(ui1);
				final String l2 = lmap.get(ui2);
				String date = uname+"\t"+u1+"\t"+u2+"\t"+uid+"\t"+ui1+"\t"+ui2+"\t"+l1+"\t"+l2;
				console( date );
				greetingService.greetServer(date, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						console("fail");
						console( caught.getMessage() );
					}

					@Override
					public void onSuccess(String result) {
						if( result.length() > 0 ) {
							Window.alert("This date has already been suggested");
						} else {
							console( "before send" );
							sendMessage( Integer.toString(ui1), Integer.toString(ui2), u1, "A date has been suggested for you" );
							//sendMessage( Integer.toString(ui2), u2, "Haeho" );
							console( "message sent" );
						}
					}
				});
			}
		});
		vp.add( match );
		
		vp.add( new HTML("<h3>Your dates</h3>") );
	
		grid = new Grid();
		grid.resizeColumns(3);
		vp.add( grid );
		vp.add( new HTML("*your date will only know you accepted if he/she self accepts") );
		vp.add( new HTML("if both accept their emails will show up") );
		
		HorizontalPanel bot = new HorizontalPanel();
		bot.setSpacing( 5 );
		Anchor l1 = new Anchor("https://suggestadate.appspot.com");
		l1.setTarget("_parent");
		l1.setHref( "https://suggestadate.appspot.com" );
		Anchor l2 = new Anchor("https://apps.facebook.com/suggestdate");
		l2.setTarget("_parent");
		l2.setHref( "https://apps.facebook.com/suggestdate" );
		Anchor l3 = new Anchor("huldaeggerts@gmail.com");
		l3.setTarget("_parent");
		l3.setHref( "mailto:huldaeggerts@gmail.com" );
		bot.add( l1 );
		bot.add( new HTML("|") );
		bot.add( l2 );
		bot.add( new HTML("|") );
		bot.add( l3 );
		vp.add( bot );
		
		HorizontalPanel	mapps = new HorizontalPanel();
		mapps.setSpacing( 5 );
		mapps.add( new HTML("More apps: ") );
		Anchor	webworm = new Anchor("Webworm");
		webworm.setTarget("_parent");
		webworm.setHref("https://webwormgame.appspot.com");
		mapps.add( webworm );
		Anchor	treedraw = new Anchor("Treedraw");
		treedraw.setTarget("_parent");
		treedraw.setHref("https://webconnectron.appspot.com/Treedraw.html");
		mapps.add( treedraw );
		vp.add( mapps );
		
		VerticalPanel mainvp = new VerticalPanel();
		mainvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//mainvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		mainvp.setSize("100%", "100%");
		mainvp.add( vp );
		rp.add( mainvp );
	}
}