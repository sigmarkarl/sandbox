package org.simmi.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
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
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public native void fbInit() /*-{
		var ths = this;	    	
		$wnd.fbAsyncInit = function() {			
	    	$wnd.FB.init( {appId: '390867450957358', channelUrl: '//suggestadate.appspot.com/channel.jsp', status: true, cookie: true, xfbml: true} );
			$wnd.FB.Event.subscribe('auth.authResponseChange', function(response) {
				if (response.status === 'connected') {
				    var uid = response.authResponse.userID;
				    var accessToken = response.authResponse.accessToken;
				    ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( uid );
				} else if (response.status === 'not_authorized') {
					//ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( "" );
				    $wnd.FB.login();
				} else {
				    //ths.@org.simmi.client.Suggestadate::setUserId(Ljava/lang/String;)( "" );
				    $wnd.FB.login();
				}
			});
	  	};
	}-*/;
	
	public native void fbLogout() /*-{
		$wnd.FB.logout();
	}-*/;
	
	public native void console( String s ) /*-{
		if( $wnd.console ) $wnd.console.log( s );
	}-*/;
	
	public native void hehe( Anchor anc ) /*-{
		var ths = this;
		try {
			$wnd.FB.api('/me/friends', {fields: 'id,name,link'}, function(response) {
				for( i = 0; i < response.data.length; i++ ) {
					var frd = response.data[i];
					ths.@org.simmi.client.Suggestadate::putFriend(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( frd.id, frd.name, frd.link );
				}
				ths.@org.simmi.client.Suggestadate::showFriendDialog(Lcom/google/gwt/user/client/ui/Anchor;)( anc );
			
			});
		} catch( e ) {
			if( $wnd.console ) $wnd.console.log( e );
		}
	}-*/;
	
	Map<String,Long>	fmap = new TreeMap<String,Long>();
	Map<Long,String>	lmap = new TreeMap<Long,String>();
	public void putFriend( String id, String name, String link ) {
		long lid = Long.parseLong( id );
		fmap.put( name, lid );
		lmap.put( lid, link );
	}
	
	HorizontalPanel	hp;
	Anchor	a1;
	Anchor	a2;
	//Anchor	currentAnchor;
	Button	match;
	public void showFriendDialog( final Anchor anc ) {
		final int ia = hp.getWidgetIndex( anc );
		hp.remove( anc );
		//PopupPanel pp = new PopupPanel( true );
		
		int sel = -1;
		final ListBox	lb = new ListBox();
		for( String uname : fmap.keySet() ) {
			if( uname.equals(anc.getText()) ) sel = lb.getItemCount();
			lb.addItem( uname );
		}
		if( sel != -1 ) lb.setSelectedIndex( sel );
		lb.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int i = lb.getSelectedIndex();
				String name = lb.getItemText( i );
				anc.setText( name );
				//currentAnchor.setName( Integer.toString(fmap.get(name)) );
				hp.remove(lb);
				hp.insert(anc, ia);
				
				if( match != null && !a1.getText().contains("Friend") && !a2.getText().contains("Friend") ) {
					match.setEnabled( true );
				}
			}
		});
		hp.insert(lb, ia);
		
		/*pp.add( lb );
		pp.center();
		
		PopupPanel pp = new PopupPanel( true );
		
		HTML	req = new HTML("Please login to Facebook");
		pp.add( req );
		
		pp.center();
		*/
	}
	
	
	public native void showLoginRequestDialog() /*-{
		$wnd.FB.login();
	}-*/;
	
	public native void fetchUserName() /*-{
		var ths = this;
		$wnd.FB.api('/me', function(response) {
	    	ths.@org.simmi.client.Suggestadate::setUserName(Ljava/lang/String;)( response.name );
	    	ths.@org.simmi.client.Suggestadate::putFriend(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( response.id, response.name, response.link );
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
	    	if( $wnd.console ) $wnd.console.log( response );
	    	ths.@org.simmi.client.Suggestadate::addEmail(ILjava/lang/String;)( row, femail );
	  	}
	  
		$wnd.FB.ui({method: 'apprequests',
	    	message: body,
	    	to: fuid
	  	}, requestCallback);
	}-*/;
	
	public native void sendMessage( String fuid1, String fuid2, String fname, String body ) /*-{
		var ths = this;
		if( $wnd.console ) $wnd.console.log( "about to request" );
		
		var requestCallback = function(response) {
        	if( $wnd.console ) $wnd.console.log( response );
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
		
		/*NodeList<Node> childs = sp.getElement().getChildNodes();
		for( int i = 0; i < childs.getLength(); i++ ) {
			sp.getElement().removeChild( childs.getItem(i) );
		}
		
		if( uid.length() > 0 ) sp.getElement().appendChild( like );
		else sp.getElement().appendChild( login );*/
		
		if( uid.length() > 0 ) {
			fetchUserName();
			greetingService.greetServer(uid, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					if( result.length() > 0 ) {						
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
							
							boolean both = email1.length() > 0 && email2.length() > 0;
							Widget w;
							if( both ) {
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
							
							int ui = link.lastIndexOf('/');
							String ulink = link.substring(ui+1);
							Image img = new Image("http://graph.facebook.com/"+ulink+"/picture");
							grid.setWidget(r, 3, img);
							
							if( both ) {
								Anchor a = new Anchor("Rate");
								a.addClickHandler( new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										DialogBox 		d = new DialogBox();
										d.setAutoHideEnabled( true );
										VerticalPanel	vp = new VerticalPanel();
										vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
										vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
										d.add( vp );
										
										//HorizontalPanel	hp = new HorizontalPanel();
										//vp.add( hp );
										
										LabelElement l3 = DOM.createLabel().cast();
										l3.setHtmlFor("skulls3");
										
										RadioButton	skulls3 = new RadioButton("rate");
										skulls3.getElement().setId("skulls3");
										
										Image		s3img = new Image("/3skulls.png");
										
										l3.appendChild( skulls3.getElement() );
										l3.appendChild( s3img.getElement() );
										
										SimplePanel	s3 = new SimplePanel();
										s3.getElement().appendChild( l3 );
										
										
										LabelElement l2 = DOM.createLabel().cast();
										l2.setHtmlFor("skulls2");
										
										RadioButton	skulls2 = new RadioButton("rate");
										skulls2.getElement().setId("skulls2");
										
										Image		s2img = new Image("/2skulls.png");
										
										l2.appendChild( skulls2.getElement() );
										l2.appendChild( s2img.getElement() );
										
										SimplePanel	s2 = new SimplePanel();
										s2.getElement().appendChild( l2 );
										
										
										LabelElement l1 = DOM.createLabel().cast();
										l3.setHtmlFor("skulls1");
										
										RadioButton	skulls1 = new RadioButton("rate");
										skulls3.getElement().setId("skulls1");
										
										Image		s1img = new Image("/1skulls.png");
										
										l1.appendChild( skulls1.getElement() );
										l1.appendChild( s1img.getElement() );
										
										SimplePanel	s1 = new SimplePanel();
										s1.getElement().appendChild( l1 );
										
										
										LabelElement l0 = DOM.createLabel().cast();
										l0.setHtmlFor("neutral");
										
										RadioButton	neutral = new RadioButton("rate");
										neutral.getElement().setId("neutral");
										
										//Image		hs0img = new Image("/neutral.png");
										
										l0.appendChild( neutral.getElement() );
										//l0.appendChild( hs0img.getElement() );
										
										SimplePanel	hs0 = new SimplePanel();
										hs0.getElement().appendChild( l0 );
										
										
										LabelElement lp1 = DOM.createLabel().cast();
										lp1.setHtmlFor("heart1");
										
										RadioButton	heart1 = new RadioButton("rate");
										heart1.getElement().setId("heart1");
										
										Image		h1img = new Image("/heart1.png");
										
										lp1.appendChild( h1img.getElement() );
										lp1.appendChild( heart1.getElement() );
										
										SimplePanel	h1 = new SimplePanel();
										h1.getElement().appendChild( lp1 );
										
										
										LabelElement lp2 = DOM.createLabel().cast();
										lp2.setHtmlFor("heart2");
										
										RadioButton	heart2 = new RadioButton("rate");
										heart2.getElement().setId("heart2");
										
										Image		h2img = new Image("/heart2.png");
										
										lp2.appendChild( h2img.getElement() );
										lp2.appendChild( heart2.getElement() );
										
										SimplePanel	h2 = new SimplePanel();
										h2.getElement().appendChild( lp2 );
										
										
										LabelElement lp3 = DOM.createLabel().cast();
										lp3.setHtmlFor("heart3");
										
										RadioButton	heart3 = new RadioButton("rate");
										heart3.getElement().setId("heart3");
										
										Image		h3img = new Image("/heart3.png");
										
										lp3.appendChild( h3img.getElement() );
										lp3.appendChild( heart3.getElement() );
										
										SimplePanel	h3 = new SimplePanel();
										h3.getElement().appendChild( lp3 );
										
										TextArea ta = new TextArea();
										ta.setSize("400px", "300px");
										vp.add( ta );
										
										vp.add( s3 );
										vp.add( s2 );
										vp.add( s1 );
										vp.add( hs0 );
										vp.add( h1 );
										vp.add( h2 );
										vp.add( h3 );
										
										d.center();
									}
								});
								w = a;
							} else {
								w = new SimplePanel();
							}
							grid.setWidget(r, 4, w);
							
							r++;
						}
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
			//getSuperPowers( uid, null );
			//sendListener( uid );
		} else {
			keys.clear();
			grid.clear();
		}
	}
	
	public Element loginButton() {
		Element elem = Document.get().createElement("div");
		elem.setAttribute("style", "text-align: center");
		elem.setAttribute("class", "fb-login-button");
		elem.setAttribute("show-faces", "true");
		elem.setAttribute("width", "200");
		elem.setAttribute("max-rows", "1");
		
		return elem;
	}
	
	/**
	 * This is the entry point method.	hkt
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		//Window.enableScrolling( false );
		int w = Window.getClientWidth();
		//int h = Window.getClientHeight();
		
		rp.setWidth(w+"px"); //, h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				//int h = event.getHeight();
				
				rp.setWidth(w+"px");
			}
		});
		
		like = Document.get().createElement("div");
		like.setAttribute( "style", "text-align: center" );
		like.setAttribute( "data-href", "http://apps.facebook.com/suggestdate/" );
		like.setAttribute( "data-width", "200" );
		like.setAttribute( "data-show-faces", "true" );
		like.setAttribute( "data-send", "true" );
		like.setId( "fblike" );
		like.setClassName( "fb-like" );
		
		login = loginButton();
		sp = new SimplePanel();
		sp.getElement().getStyle().setTextAlign( com.google.gwt.dom.client.Style.TextAlign.CENTER );
		sp.getElement().appendChild( login );
		
		SimplePanel sp2 = new SimplePanel();
		sp.getElement().getStyle().setTextAlign( com.google.gwt.dom.client.Style.TextAlign.CENTER );
		sp2.getElement().appendChild( like );
		
		/*String fbuid = null;
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
		}*/
		fbInit();
		
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
		
		/*final Element ads = Document.get().getElementById("ads");
		if( ads != null ) {
			SimplePanel	adspanel = new SimplePanel();
			ads.removeFromParent();
			adspanel.getElement().appendChild( ads );
			vp.add( adspanel );
		}*/
		vp.add( new HTML("<h2>Suggest a date</h2>") );
		vp.add( new HTML("<h3>Try out the <a href=\"https://play.google.com/store/apps/details?id=com.suggestdroid\" target=\"_blank\">Android app</a></h3>") );
		
		hp = new HorizontalPanel();
		hp.setSpacing( 5 );
		
		match = new Button("Match");
		match.setEnabled( false );
		a1 = new Anchor("Friend 1");
		a1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( uid != null && uid.length() > 0 ) {
					//currentAnchor = a1;
					if( fmap.size() <= 1 ) hehe( a1 );
					else showFriendDialog( a1 );
				} else showLoginRequestDialog();
			}
		});
		a2 = new Anchor("Friend 2");
		a2.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( uid != null && uid.length() > 0 ) {
					//currentAnchor = a2;
					if( fmap.size() <= 1 ) hehe( a2 );
					else showFriendDialog( a2 );
				} else showLoginRequestDialog();
			}
		});
		
		hp.add( new HTML("I suggest") );
		hp.add( a1 );
		hp.add( new HTML(" dates ") );
		hp.add( a2 );
		
		vp.add( sp );
		vp.add( sp2 );
		vp.add( hp );
		match.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String u1 = a1.getText();
				final String u2 = a2.getText();
				final long ui1 = fmap.get(u1);
				final long ui2 = fmap.get(u2);
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
						if( result.length() == 0 || Window.confirm("This date has already been suggested. Do you want to suggest it again?") ) {
							console( "before send" );
							sendMessage( Long.toString(ui1), Long.toString(ui2), u1, "A date has been suggested for you" );
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
		grid.setCellSpacing(3);
		grid.resizeColumns(5);
		vp.add( grid );
		vp.add( new HTML("*your date will only know you accepted if he/she self accepts") );
		vp.add( new HTML("if both accept their emails will show up") );
		
		HorizontalPanel bot = new HorizontalPanel();
		bot.setSpacing( 5 );
		Anchor l1 = new Anchor("http://suggestadate.appspot.com");
		l1.setTarget("_blank");
		l1.setHref( "http://suggestadate.appspot.com" );
		Anchor l2 = new Anchor("https://apps.facebook.com/suggestdate");
		l2.setTarget("_blank");
		l2.setHref( "https://apps.facebook.com/suggestdate" );
		Anchor l3 = new Anchor("huldaeggerts@gmail.com");
		l3.setTarget("_blank");
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
		webworm.setTarget("_blank");
		webworm.setHref("http://webwormgame.appspot.com");
		mapps.add( webworm );
		Anchor	treedraw = new Anchor("Treedraw");
		treedraw.setTarget("_blank");
		treedraw.setHref("http://webconnectron.appspot.com/Treedraw.html");
		mapps.add( treedraw );
		
		HTML html = new HTML("|");
		mapps.add( html );
		
		Anchor	logout = new Anchor("logout");
		logout.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fbLogout();
				setUserId( "" );
			}
		});
		mapps.add( logout );
		
		vp.add( mapps );
		
		VerticalPanel mainvp = new VerticalPanel();
		mainvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//mainvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		mainvp.setSize("100%", "100%");
		mainvp.add( vp );
		rp.add( mainvp );
	}
}
