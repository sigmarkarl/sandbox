package org.simmi.client;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smasaga implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync 		greetingService = GWT.create(GreetingService.class);
	private final ListGreetingServiceAsync 	listGreetingService = GWT.create(ListGreetingService.class);
	private final EinkunnServiceAsync 		gradeService = GWT.create(EinkunnService.class);
	private final LoginServiceAsync			loginService = GWT.create(LoginService.class);
	
	private LoginInfo 						loginInfo = null;
	private VerticalPanel 					loginPanel = new VerticalPanel();
	private Label 							loginLabel = new Label("Vinsamlegast skráðu þig inn með Google Account.");
	private Anchor 							signInLink = new Anchor("Sign In");

	FlexTable	smtable;
	DeckPanel	p;
	
	Greeting		currentBook;
	
	private void fillTable( FlexTable f, List<Greeting> result ) throws UnsupportedEncodingException {
		int r = 1;
		//Dictionary parameters = Dictionary.getDictionary("parameters");
		//String val = parameters.get("val");
		//Window.alert(val);
		
		for( final Greeting greet : result ) {
			greet.r = r;
			/*Anchor	ancor = new Anchor(greet.title);
			ancor.set
			ancor.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					currentBook = greet;
					
					/*NodeList<Element> tags = Document.get().getElementsByTagName("meta");
				    for (int i = 0; i < tags.getLength(); i++) {
				        MetaElement metaTag = ((MetaElement) tags.getItem(i));
				        if (metaTag.getName().equals("description")) {
				            metaTag.setContent( greet.title );
				        }
				    }*

					
					saga.nafn.setText( greet.title );
					saga.dags.setText( greet.date );
					saga.hof.setText( greet.author );
					String fname = greet.filename == null ? "null" : greet.filename;
					saga.skra.setHTML( "<a href=\"http://dl.dropbox.com/u/10024658/"+URL.encode(fname)+"\">"+greet.title+"</a>" );
					saga.skrateg.setText( greet.filetype );
					saga.skrast.setText( Integer.toString(greet.filesize) );
					saga.yourgradeBook.setValue( greet.key );
					saga.yourgradeUser.setValue( greet.author );
					
					boolean adding = true;
					
					gradeService.einkunnServer( greet.key, new AsyncCallback<List<EinkunnSerializable>>() {
						
						@Override
						public void onSuccess(List<EinkunnSerializable> result) {
							int min = 10;
							int max = 0;
							
							int avg = 0;
							int cnt = 0;
							
							saga.table.removeAllRows();
							saga.table.setText(0, 0, "Einkunnargjafi");
							//saga.table.setText(0, 1, "Umsögn");
							saga.table.setText(0, 1, "Einkunn");
							
							int[]	mi = new int[result.size()];
							for( EinkunnSerializable grade : result ) {
								saga.table.setText(cnt+1, 0, grade.getGrader());
								//saga.table.setText(cnt+1, 1, grade.getComment());
								saga.table.setText(cnt+1, 2, Integer.toString(grade.getGrade()) );
								
								mi[cnt] = grade.getGrade();
								
								if( grade.getGrade() > max ) max = grade.getGrade();
								if( grade.getGrade() < min ) min = grade.getGrade();
								
								avg += grade.getGrade();
								cnt++;
							}
							Arrays.sort( mi );
							int val = (int)Math.round( avg / (double)cnt );
							int mid = mi.length > 0 ? mi.length % 2 == 0 ? (mi[mi.length/2-1]+mi[mi.length/2])/2 : mi[ mi.length/2 ] : 0;
							saga.maxeinkunn.setText( Integer.toString(max) );
							saga.mineinkunn.setText( Integer.toString(min) );
							saga.meeinkunn.setText( Integer.toString(val) );
							saga.mieinkunn.setText( Integer.toString(mid) );
							saga.neinkunn.setText( Integer.toString(cnt) );						
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.err.println("fail");
						}
					});
					
					String nickname = loginInfo.getNickname();
					if( nickname == null || greet.author.equals(nickname) ) {
						saga.yourgradeComment.setEnabled( false );
						saga.yourgrade.setEnabled( false );
						saga.yourgradeSubmit.setEnabled( false );
					}
					
					p.showWidget(3);
				}
			});*/
			//f.setWidget(r, 0, ancor);
			f.setHTML(r, 0, "<a href=\"Saga.jsp?id="+greet.id+"\">"+greet.title+"</a>");
			f.setText(r, 1, greet.author);
			f.setText(r, 2, greet.date);
			String fname = greet.filename == null ? "null" : greet.filename;
			f.setHTML(r, 3, "<a href=\"http://dl.dropbox.com/u/10024658/"+URL.encode(fname)+"\">"+greet.title+"</a>" );
			f.setText(r, 4, greet.filetype);
			f.setText(r, 5, Integer.toString(greet.filesize) );
			f.setText(r, 6, Integer.toString(greet.numgrade) );
			
			r++;
		}
	}
	
	Bok	saga;
	class Bok extends VerticalPanel {
		AbsolutePanel	absPanel;
		
		Label	nafnLabel;
		Label	nafn;
		
		Label	dagsLabel;
		Label	dags;
		
		Label	hofLabel;
		Anchor	hof;
		
		Label	skraLabel;
		Anchor	skra;
		
		Label	skrategLabel;
		Label	skrateg;
		
		Label	skrastLabel;
		Label	skrast;
		
		Label	neinkunn;
		Label	meeinkunn;
		Label	mieinkunn;
		Label	maxeinkunn;
		Label	mineinkunn;
		
		Label	neinkunnLabel;
		Label	meeinkunnLabel;
		Label	mieinkunnLabel;
		Label	maxeinkunnLabel;
		Label	mineinkunnLabel;
		
		Label		yourgradeLabel;
		TextBox		yourgrade;
		TextArea	yourgradeComment;
		Button		yourgradeSubmit;
		Hidden		yourgradeBook;
		Hidden		yourgradeUser;
		
		ScrollPanel	scroll;
		FlexTable	table;
		
		final	int	offset = 300;
		
		public Bok() {
			this.setStyleName("blue");
			this.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
			this.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
			
			absPanel = new AbsolutePanel();
			absPanel.setStyleName("bok");
			absPanel.setSize("600px", "400px");
			
			neinkunn = new Label("0");
			meeinkunn = new Label("0.0");
			mieinkunn = new Label("0.0");
			mineinkunn = new Label("0.0");
			maxeinkunn = new Label("0.0");
			
			neinkunnLabel = new Label("Fjöldi einkunna:");
			meeinkunnLabel = new Label("Medaleinkunn:");
			mieinkunnLabel = new Label("Midgildi einkunnar:");
			mineinkunnLabel = new Label("Lagmarkseinkunn:");
			maxeinkunnLabel = new Label("Hamarkseinkunn:");
			
			nafnLabel = new Label("Nafn:");
			dagsLabel = new Label("Dags:");
			hofLabel = new Label("Hofundur:");
			skraLabel = new Label("Skra:");
			skrategLabel = new Label("Tegund:");
			skrastLabel = new Label("Staerd:");
			
			nafn = new Label();
			dags = new Label();
			hof = new Anchor();
			skra = new Anchor();
			skrateg = new Label();
			skrast = new Label();
			
			table = new FlexTable();
			table.setWidth("285px");
			table.setStyleName("white");
			//table.set
			scroll = new ScrollPanel( table );
			scroll.setSize("285px", "200px");
			scroll.setStyleName("white");
			
			absPanel.add(nafnLabel,10,10);
			absPanel.add(nafn,70,10);
			
			absPanel.add(dagsLabel,10,30);
			absPanel.add(dags,70,30);
			
			absPanel.add(hofLabel,10,50);
			absPanel.add(hof,70,50);
			
			absPanel.add(skraLabel,offset,10);
			absPanel.add(skra,offset+60,10);
			
			absPanel.add(skrategLabel,offset,30);
			absPanel.add(skrateg,offset+60,30);
			
			absPanel.add(skrastLabel,offset,50);
			absPanel.add(skrast,offset+60,50);
			
			absPanel.add( neinkunn, 150, 150 );
			absPanel.add( meeinkunn, 150, 100 );
			absPanel.add( mieinkunn, 150, 120 );
			absPanel.add( maxeinkunn, offset+140, 100 );
			absPanel.add( mineinkunn, offset+140, 120 );
			
			absPanel.add( neinkunnLabel, 10, 150 );
			absPanel.add( meeinkunnLabel, 10, 100 );
			absPanel.add( mieinkunnLabel, 10, 120 );
			absPanel.add( maxeinkunnLabel, offset, 100 );
			absPanel.add( mineinkunnLabel, offset, 120 );
			
			absPanel.add( scroll, 300, 150 );
			
			/*absPanel.add(nafnLabel);
			absPanel.add(nafn);
			
			absPanel.add(dagsLabel);
			absPanel.add(dags);
			
			absPanel.add(hofLabel);
			absPanel.add(hof);
			
			absPanel.add(skraLabel);
			absPanel.add(skra);
			
			absPanel.add(skrategLabel);
			absPanel.add(skrateg);
			
			absPanel.add(skrastLabel);
			absPanel.add(skrast);
			
			absPanel.add( meeinkunn );
			absPanel.add( mieinkunn );
			absPanel.add( maxeinkunn );
			absPanel.add( mineinkunn );
			
			absPanel.add( meeinkunnLabel );
			absPanel.add( mieinkunnLabel );
			absPanel.add( maxeinkunnLabel );
			absPanel.add( mineinkunnLabel );*/
			
			yourgradeLabel = new Label("Min einkunn:");
			yourgrade = new TextBox();
			yourgrade.setName("grade");
			yourgrade.setMaxLength(2);
			yourgrade.setWidth("30px");
			
			yourgrade.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					if( !(yourgrade.getText().matches("[0-9]*") && Integer.parseInt(yourgrade.getText()) <= 10) ) {
						yourgradeSubmit.setEnabled(false);
						yourgradeSubmit.setText("milli 0-10");
					} else {
						yourgradeSubmit.setEnabled(true);
						yourgradeSubmit.setText("Meta");
					}
				}
			});
			
			yourgradeBook = new Hidden();
			yourgradeBook.setName("book");
			
			yourgradeUser = new Hidden();
			yourgradeUser.setName("user");
			
			yourgradeSubmit = new Button();
			yourgradeSubmit.setText("Meta");
			
			yourgradeComment = new TextArea();
			yourgradeComment.setSize("275px", "195px");
			yourgradeComment.setName("comment");
			
			//fp.add( yourgradeLabel );
			//fp.add( yourgrade );
			//fp.add( yourgradeSubmit );
			
			absPanel.add( yourgradeLabel, 10, 367 );
			absPanel.add( yourgrade, 140, 365 );
			absPanel.add( yourgradeComment, 10, 150 );
			absPanel.add( yourgradeSubmit, 190, 365 );
			absPanel.add( yourgradeBook );
			absPanel.add( yourgradeUser );
			
			final FormPanel	fp0 = new FormPanel();
			fp0.setAction("/grade");
			//fp0.setEncoding(FormPanel.ENCODING_MULTIPART);
		    fp0.setMethod(FormPanel.METHOD_POST);
			fp0.add( absPanel );
			this.add( fp0 );
			
			yourgradeSubmit.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fp0.submit();
					smtable.setText(currentBook.r, 6, Integer.toString(currentBook.numgrade) );
				}
			});
			
			this.add( fp0 );
			
			//fp.get
		}
	}
	
	 private void loadLogin( VerticalPanel vp ) {
		    // Assemble login panel.
		    signInLink.setHref(loginInfo.getLoginUrl());
		    loginPanel.add(loginLabel);
		    loginPanel.add(signInLink);
		    vp.add(loginPanel);
		    RootPanel.get().add( vp );
	 }
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		int h = Window.getClientHeight();
		vp.setSize("100%", h+"px");
		vp.setStyleName("mainstyle");
		//vp.setTitle("stockList");
		
		login( vp );
	}
	
	public void login( final VerticalPanel vp ) {
		 loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
		      public void onFailure(Throwable error) {
		      }
	
		      public void onSuccess(LoginInfo result) {
		        loginInfo = result;
		        /*if(loginInfo.isLoggedIn()) {
		        	loadStuff( vp );
		        } else {
		        	loadLogin( vp );
		        }*/
		        loadStuff( vp );
		      }
		    });
	}
	
	public void loadStuff( VerticalPanel vp ) {		
		VerticalPanel	fixpanel = new VerticalPanel();
		fixpanel.setSpacing( 0 );
		
		p = new DeckPanel();
		p.setStyleName("test");
		p.setSize("758px", "600px");
		
		fixpanel.add( p );
		vp.add( fixpanel );
		
		HorizontalPanel	blue = new HorizontalPanel();
		//blue.setSize("200px", "200px");
		blue.setStyleName("blue");
		blue.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		blue.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		HorizontalPanel	green = new HorizontalPanel();
		green.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		green.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		VerticalPanel hey = new VerticalPanel();
		hey.setSize("600px","600px");
		hey.setStyleName("green");
		green.add( hey );
		//green.setSize("200px", "200px");
		green.setStyleName("test2");
		
		FlexTable hoftable = new FlexTable();
		
		SimplePanel	red = new SimplePanel();
		red.setSize("200px", "200px");
		red.setStyleName("red");
		
		p.add( blue );
		p.add( green );
		p.add( red );
		
		saga = new Bok();
		p.add( saga );
		
		//VerticalPanel	tempvert = new VerticalPanel();
		HorizontalPanel	p2 = new HorizontalPanel();
		p2.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		p2.setSpacing(5);
		//p2.setStyleName("test2");
		//p2.setSize("900px", "15px");
		
		fixpanel.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		fixpanel.add( p2 );
		
		Anchor	a = new Anchor("senda smásögu");
		a.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				p.showWidget(0);
			}
		});
		p2.add( a );
		p2.add(new Label("|"));
		a = new Anchor("höfundar");
		a.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				p.showWidget(1);
			}
		});
		p2.add( a );
		p2.add(new Label("|"));
		a = new Anchor("smásögur");
		a.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*NodeList<Element> tags = Document.get().getElementsByTagName("meta");
			    for (int i = 0; i < tags.getLength(); i++) {
			        MetaElement metaTag = ((MetaElement) tags.getItem(i));
			        if (metaTag.getName().equals("description")) {
			            metaTag.setContent("new description");
			        }
			    }*/
				
				listGreetingService.greetServer("", new AsyncCallback<List<Greeting>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(List<Greeting> result) {
						try {
							fillTable( smtable, result );
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				});

				p.showWidget(2);
			}
		});
		p2.add( a );
		p2.add(new Label("|"));
		if( loginInfo != null && loginInfo.isLoggedIn() ) {
			a = new Anchor("Útskra "+loginInfo.getNickname());
			a.setHref( loginInfo.getLogoutUrl() );
		} else {
			a = new Anchor("innskrá");
			if( loginInfo != null ) a.setHref( loginInfo.getLoginUrl() );
			
		}
		p2.add( a );
		
		HorizontalPanel temphor = new HorizontalPanel();
		temphor.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		temphor.setSpacing(5);
		a = new Anchor("huldaeggerts@gmail.com");
		a.setHref("mailto:huldaeggerts@gmail.com");
		temphor.add( a );
		temphor.add(new Label("| Fleiri forrit:"));
		a = new Anchor("http://fasteignaverd.appspot.com");
		a.setHref("http://fasteignaverd.appspot.com");
		temphor.add( a );
		temphor.add(new Label("|"));
		a = new Anchor("http://nutritiondb.appspot.com");
		a.setHref("http://nutritiondb.appspot.com");
		temphor.add( a );
		temphor.add(new Label("|"));
		
		HorizontalPanel temphor2 = new HorizontalPanel();
		temphor2.setSpacing(5);
		a = new Anchor("http://webspectroscope.appspot.com");
		a.setHref("http://webspectroscope.appspot.com");
		temphor2.add( a );
		temphor2.add(new Label("|"));
		a = new Anchor("http://webconnectron.appspot.com");
		a.setHref("http://webconnectron.appspot.com");
		temphor2.add( a );
		temphor2.add(new Label("|"));
		a = new Anchor("http://websimlab.appspot.com");
		a.setHref("http://websimlab.appspot.com");
		temphor2.add( a );
		
		fixpanel.add( temphor );
		fixpanel.add( temphor2 );
		//tempvert.add(p2);
		//tempvert.add(temphor);
		
		p.showWidget( 0 );
		RootPanel.get().add( vp );
		
		/*DockPanel	dp = new DockPanel();
		//dp.setBorderWidth(1);
		//dp.set
		vp.add( dp );
		dp.setSize("900px", "600px");
		
		final Image	img0 = new Image("smabbi.png");
		final Image	img00 = new Image("smabbi2.png");
		final Image	img1 = new Image("http://www.bonappetit.com/images/tips_tools_ingredients/ingredients/ttar_orange_01_h_launch.jpg");
		final Image	img2 = new Image("http://drmyers.files.wordpress.com/2009/06/apple.jpg");
		final Image	img3 = new Image("http://www.pickyourown.org/peaches/peach.jpg");
		
		MenuBar	mb = new MenuBar( true );
		MenuBar	mainmenu = new MenuBar( true );
		//mainmenu.addItem("hoho", mb);
		mb.addItem("simmi2", new Command() {
			@Override
			public void execute() {
				img1.setUrl("http://www.pickyourown.org/peaches/peach.jpg");
			}
		}).setWidth("100px");
		mb.addItem("ok", new Command() {
			@Override
			public void execute() {
				img1.setUrl("http://drmyers.files.wordpress.com/2009/06/apple.jpg");
			}
		});
		
		//new DockPane
		mb.setWidth("100px");
		//DockLayoutConstant dlc = new Docklay
		
		//img1.setHeight("100px");
		//img3.setHeight("100px");
		/*HorizontalSplitPanel hsp = new HorizontalSplitPanel();
		hsp.setLeftWidget( img2 );
		hsp.setRightWidget(img3);
		//vsp.
		//vsp.setSplitPosition("600px");
		hsp.setSize("400px","400px");
		dp.add(hsp, DockPanel.CENTER);*
		
		VerticalSplitPanel vsp = new VerticalSplitPanel();
		vsp.setTopWidget( img2 );
		vsp.setBottomWidget(img3);
		//vsp.
		//vsp.setSplitPosition("600px");
		vsp.setSize("300px","300px");
		
		FlowPanel	fpn = new FlowPanel();
		//fp.add( img00 );
		
		HTML html = new HTML( "<a href=\"mailto:smasogur@gmail.com\">smasogur@gmail.com</a>" );
		html.setStyleName("simmi");
		html.setHeight("30px");
		
		HTML name = new HTML( "smásögur" );
		name.setStyleName("simmi2");
		name.setHeight("30px");
		//name.set
		
		//fp.add( html );
		//html.add( img00 );
		//HTML html2;
		
		//SimplePanel sp2 = new SimplePanel();
		//sp.add( html );
		
		//CanvasRenderingContext2D c = new CanvasRenderingContext2D();
		dp.add(name, DockPanel.NORTH);
		dp.add(html, DockPanel.SOUTH);*/
		
		Grid grid = new Grid( 4, 3 );
		grid.setHeight("100%");
		//grid.setWidth("700px");
		grid.setStyleName("ermi");
		//grid.set
		final FormPanel	fp = new FormPanel();
		
		
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		//final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		//dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				//sendButton.setEnabled(true);
				//sendButton.setFocus(true);
			}
		});
		
		/*greetingService.greetServer("empty", new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				dialogBox.setText("Remote Procedure Call - Failure");
				String s = caught.getMessage();
				for( StackTraceElement ste : caught.getStackTrace() ) {
					s += ste.toString();
				}
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML( s );
				dialogBox.center();
				closeButton.setFocus(true);
			}

			public void onSuccess(String result) {
				/*dialogBox.setText( "oh" );
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML( result );
				dialogBox.center();
				closeButton.setFocus(true);
				
				fp.setAction( result );
			}
		});
		//fp.setAction( BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/upload") );*/
		
		fp.setAction( "/upload" );
		//grid.setBorderWidth(1);
		fp.setStyleName("formi");
		
		VerticalPanel	holder = new VerticalPanel();
		holder.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		holder.setStyleName("test2");
		holder.add( grid );
		holder.setSpacing( 5 );
		
		CheckBox	barn = new CheckBox("Barnasaga (B)");
		barn.setName("B");
		grid.setWidget(0, 0, barn);
		CheckBox	glaep = new CheckBox("Glæpasaga (R)");
		glaep.setName("P");
		grid.setWidget(1, 0, glaep);
		CheckBox	weird = new CheckBox("Undarleg saga (W)");
		weird.setName("W");
		grid.setWidget(2, 0, weird);
		CheckBox	ast = new CheckBox("Ástarsaga (A)");
		ast.setName("L");
		grid.setWidget(3, 0, ast);
		CheckBox	ero = new CheckBox("Erótík (E)");
		ero.setName("E");
		grid.setWidget(0, 1, ero);
		CheckBox	draug = new CheckBox("Drauga/Hryllingssaga (D)");
		draug.setName("H");
		grid.setWidget(1, 1, draug);
		CheckBox	ung = new CheckBox("Unglingasaga (U)");
		ung.setName("J");
		grid.setWidget(2, 1, ung);
		CheckBox	sog = new CheckBox("Söguleg smásaga (L)");
		sog.setName("N");
		grid.setWidget(3, 1, sog);
		CheckBox	gam = new CheckBox("Gamansaga (G)");
		gam.setName("F");
		grid.setWidget(0, 2, gam);
		CheckBox	san = new CheckBox("Sannsöguleg (S)");
		san.setName("T");
		grid.setWidget(1, 2, san);
		CheckBox	vis = new CheckBox("Vísindaskáldsaga (V)");
		vis.setName("S");
		grid.setWidget(2, 2, vis);
		CheckBox bund = new CheckBox("Í bundnu máli (M)");
		bund.setName("M");
		grid.setWidget(3, 2, bund);
		
		blue.add( fp );
		
		//grid.setHTML(0, 0, html);
		/*dp.add(mb, DockPanel.WEST);
		grid.setStyleName("formi");
		fpn.add( fp );
		dp.add(fpn, DockPanel.CENTER);
		
		//canv
		dp.setCellHeight(name, "1px");
		/*dp.setCellHorizontalAlignment(fp, DockPanel.ALIGN_CENTER);*/
		//dp.setCellWidth(mb, "100px");
		
		//Window.alert(img0.getHeight()+"");
		//dp.setSize("100%", "100%");
		
		/*final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		//RootPanel.get("nameFieldContainer").add(nameField);
		//RootPanel.get("sendButtonContainer").add(sendButton);
		//RootPanel.get("errorLabelContainer").add(errorLabel);
		
		final FormPanel	form = new FormPanel();*/
		fp.setEncoding(FormPanel.ENCODING_MULTIPART);
	    fp.setMethod(FormPanel.METHOD_POST);

	    HorizontalPanel	hholder = new HorizontalPanel();
	    holder.add( hholder );
	    hholder.setSpacing( 5 );
	    
	    Label	lab = new Label("Nafn sogu:");
	    hholder.add( lab );
	    TextBox userid = new TextBox();
	    userid.setMaxLength(100);
	    userid.setWidth("500px");
	    userid.setName("content");
	    hholder.add(userid);
	    
	    HorizontalPanel	hholder2 = new HorizontalPanel();
	    holder.add( hholder2 );
	    hholder2.setSpacing( 5 );
	    
		FileUpload	upload = new FileUpload();
		upload.setName( "myFile" );
		hholder2.add( upload );
		//RootPanel.get("nameFieldContainer").add( panel );
		
		fp.add( holder );
		
		ScrollPanel scrollpanel = new ScrollPanel();
		//scrollpanel.
		scrollpanel.setSize("758px", "600px");
		scrollpanel.setStyleName("blue");
		smtable = new FlexTable();
		smtable.setWidth("758px");
		smtable.setStyleName("test2");
		smtable.setText(0, 0, "Nafn");
		smtable.setText(0, 1, "Hofundur");
		smtable.setText(0, 2, "Dagsetning");
		smtable.setText(0, 3, "Skra");
		smtable.setText(0, 4, "Tegund");
		smtable.setText(0, 5, "Staerd");
		smtable.setText(0, 6, "Fj. eink");
		scrollpanel.add( smtable );
		
		listGreetingService.greetServer("", new AsyncCallback<List<Greeting>>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<Greeting> result) {
				try {
					fillTable( smtable, result );
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		
		red.add( scrollpanel );
		
		fp.addSubmitCompleteHandler( new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				smtable.removeAllRows();
				smtable.setText(0, 0, "Nafn");
				smtable.setText(0, 1, "Hofundur");
				smtable.setText(0, 2, "Dagsetning");
				smtable.setText(0, 3, "Skra");
				smtable.setText(0, 4, "Tegund");
				smtable.setText(0, 5, "Staerd");
				smtable.setText(0, 6, "Fj. eink");
				
				listGreetingService.greetServer("", new AsyncCallback<List<Greeting>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(List<Greeting> result) {
						//System.err.println( result.size() );
						try {
							fillTable( smtable, result );
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				});
				
				p.showWidget(2);
				/*greetingService.greetServer("empty", new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						fp.setAction( result );
					}
				});*/
				
				fp.setAction( "/upload" );
			}
		});
		
		hholder2.add(new Button("Senda", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fp.submit();
			}
	    }));
		
		/*VerticalPanel panel1 = new VerticalPanel();
		VerticalPanel panel2 = new VerticalPanel();
		
		panel1.set
		
		RootPanel.get().add( mainmenu );
		
		TabPanel lp = new TabPanel();
		//lp.
		//lp.
		lp.add(panel, "ermi0");
		lp.add(panel1, "ermi1");
		lp.add(panel2, "ermi2");
		
		RootPanel.get("nameFieldContainer").add( lp );

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}
			
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);*/
	}
}
