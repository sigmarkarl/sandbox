package org.simmi.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import elemental.client.Browser;

public class ThermusTable implements EntryPoint {	
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	elemental.html.Window myPopup;
	public elemental.html.Window windowOpen( String url, String name ) {
		elemental.html.Window wnd = Browser.getWindow();
		myPopup = wnd.open( url, name );
		return myPopup;
	}
	
	public native int initFunctions( Element applet ) /*-{
		var s = this;
		
		$wnd.showTree = function( newtree ) {
			$wnd.treetext = newtree;
			$wnd.console.log( "sisim" );
			$wnd.console.log( newtree );
			$wnd.myPopup = s.@org.simmi.client.ThermusTable::windowOpen(Ljava/lang/String;Ljava/lang/String;)( $wnd.domain + '/Treedraw.html?callback=thermusgenes','_blank' ); 
			//window.open(domain + '/Treedraw.html?callback=thermusgenes','_blank');
		}
	
		$wnd.saveMeta = function( acc, country, valid ) {
			s.@org.simmi.client.ThermusTable::saveMeta(Ljava/lang/String;Ljava/lang/String;Z)( acc, country, valid );
		};
		
		$wnd.loadMeta = function() {
			s.@org.simmi.client.ThermusTable::loadMeta()();
		};
		
		$wnd.loadData = function() {
			s.@org.simmi.client.ThermusTable::loadData()();
		};
		
		$wnd.saveSel = function( key, val ) {
			s.@org.simmi.client.ThermusTable::saveSel(Ljava/lang/String;Ljava/lang/String;)( key, val );
		};
		
		$wnd.propSel = function( sel ) {
			s.@org.simmi.client.ThermusTable::propSel(Ljava/lang/String;)( sel );
		};
		
		$wnd.saveSeq2 = function( jsonstr ) {
			//$wnd.console.log('blehblehble');
			s.@org.simmi.client.ThermusTable::saveSeq(Ljava/lang/String;)( jsonstr );
		};
		
		$wnd.fetchSeq = function( include ) {
			//$wnd.console.log('blehblehble');
			s.@org.simmi.client.ThermusTable::fetchSeq(Ljava/lang/String;)( include );
		};
		
		$wnd.reqSavedSel = function() {
			s.@org.simmi.client.ThermusTable::requestSavedSelections()();
		};
		
		$wnd,replacetreetext = false;
		$wnd.fasttree = function( fasta ) {
			$wnd.replacetreetext = false;
			$wnd.postModuleMessage( fasta, fasta.length );
		};
		
		$wnd.dnapars = function( phy ) {
			$wnd.replacetreetext = true;
			$wnd.postModuleMessage( phy, phy.length );
		};
		
	 	$wnd.isString = function(s) {
    		return typeof(s) === 'string' || s instanceof String;
		}
		
		$doc.appendText = function( str ) {
			//$wnd.console.log( str );
			//var datatable = $doc.getElementById('datatable');
			//datatable.showTree( str );
			if( $wnd.replacetreetext ) {
				applet.replaceTreeText( str );
			} else {
				if( $wnd.isString(str) && str.indexOf('(') == 0 ) $wnd.showTree( str );
				else $wnd.console.log( str );
			}
		};

		return 0;
	}-*/;
	
	public void loadData() {
		console("loading data");
		greetingService.getThermusFusion( new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				console( "caught " + caught.getMessage() );
			}

			@Override
			public void onSuccess(String result) {
				if( result != null ) {
					//Element e = Document.get().getElementById("datatable");
					loadApplet( appletelement, result );
				}
			}
		});
	}
	
	public native void loadAppletSequences( JavaScriptObject applet, String data ) /*-{
		//var applet = $doc.getElementById('datatable');
		applet.loadSequences( data );
	}-*/;
	
	public native void loadApplet( JavaScriptObject applet, String data ) /*-{
		//var applet = $doc.getElementById('datatable');
  		applet.loadData( data );
	}-*/;
	
	public native String encodeURIPassword( String password ) /*-{
	  	return encodeURIComponent(password);
	}-*/;
	
	public native String getGAauthenticationToken( String email, String password ) /*-{
		  //password = encodeURIComponent(password);
		  //var response == UrlFetchApp.fetch("https://www.google.com/accounts/ClientLogin", {
		  //    method: "post",
		  //    payload: "accountType=GOOGLE&Email=" + email + "&Passwd=" + password + "&service=fusiontables&Source=testing"
		  //});
		  //var responseStr = response.getContentText();
		  //responseStr = responseStr.slice(responseStr.search("Auth=") + 5, responseStr.length);
		  ///responseStr = responseStr.replace(/\n/g, "");
		  //return responseStr;
		  return "ok";
	}-*/;

	public native String getdata( String authToken ) /*-{
		  query = encodeURIComponent("SHOW TABLES");
		  var URL = "http://www.google.com/fusiontables/api/query?sql=" + query;
		  var response = UrlFetchApp.fetch(URL, {
		     method: "get",
		     headers: {
		          "Authorization": "GoogleLogin auth=" + authToken,
		     }
		  });
		  return response.getContentText();
	}-*/;
	
	public native void appendSelection( Element ae, String key, String value ) /*-{
		ae.appendSelection( key, value );
	}-*/;
	
	public void saveSeq( String jsonstr ) {
		JSONValue jsonv = JSONParser.parseStrict( jsonstr );
		Map<String,String> jsonmap = new HashMap<String,String>();
		JSONObject jsono = jsonv.isObject();
		for( String json : jsono.keySet() ) {
			jsonmap.put( json, jsono.get(json).toString().replace("\"", "") );
		}
		greetingService.saveSeq( jsonmap, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				console("saveseq successfull");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				console( caught.getMessage() );
			}
		});
	}
	
	public void fetchSeq( String include ) {
		greetingService.fetchSeq( include, new AsyncCallback<Map<String,String>>() {
			@Override
			public void onSuccess(Map<String,String> result) {
				JSONObject	jsono = new JSONObject();
				for( String name : result.keySet() ) {
					String res = result.get(name);
					String erm = res == null ? "" : res;
					jsono.put( name, new JSONString(erm) );
				}
				String jsonstr = jsono.toString();
				//console( jsonstr );
				loadAppletSequences( appletelement, jsonstr );
			}
			
			@Override
			public void onFailure(Throwable caught) {
				console( caught.getMessage() );
			}
		});
	}
	
	public void propSel( String sel ) {
		if( myPopup != null ) myPopup.postMessage( "propogate{"+sel+"}", "http://webconnectron.appspot.com" );
	}
	
	public void saveSel( String key, String val ) {
		greetingService.saveThermusSel( key, val, new AsyncCallback<Map<String,String>>() {
			@Override
			public void onFailure(Throwable caught) {
				console( "fail: "+caught.getMessage() );
			}

			@Override
			public void onSuccess(Map<String,String> result) {
				//String applet = Document.get().getElementById("");
				for( String key : result.keySet() ) {
					appendSelection( appletelement, key, result.get(key) );
				}
			}
		});
	}
	
	public void requestSavedSelections() {
		console("trying");
		greetingService.saveThermusSel( null, null, new AsyncCallback<Map<String,String>>() {
			@Override
			public void onFailure(Throwable caught) {
				console( "fail: "+caught.getMessage() );
			}

			@Override
			public void onSuccess(Map<String,String> result) {
				console("success");
				console( ""+result.size() );
				//String applet = Document.get().getElementById("");
				for( String key : result.keySet() ) {
					appendSelection( appletelement, key, result.get(key) );
				}
			}
		});
	}
	
	public void saveMeta( String acc, String country, boolean valid ) {
		greetingService.greetServer( acc, country, valid, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				console( "fail: "+caught.getMessage() );
			}

			@Override
			public void onSuccess(String result) {
				console( "succ " + result );
			}
		});
	}
	
	public native void updateTableInApplet( JavaScriptObject appletelement, String tmap ) /*-{
		$wnd.console.log( 'about to update table '+tmap );
		appletelement.updateTable( tmap );
	}-*/;
	
	public void loadMeta() {
		greetingService.getThermus( new AsyncCallback<Map<String,String>>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Map<String, String> result) {
				//Element e = Document.get().getElementById("datatable");
				//console( "before update "+e );
				
				//com.google.appengine.repackaged.org.json.JSONObject jsono = new com.google.appengine.repackaged.org.json.JSONObject(result);
				JSONObject jsono = new JSONObject();
				for( String key : result.keySet() ) {
					String c = result.get(key);
					String[] split = c.split(";");
					if( split.length > 1 ) {
						JSONObject jsonc = new JSONObject();
						jsonc.put("country", new JSONString(split[0]) );
						jsonc.put("valid", new JSONString(split[1]) );
						jsono.put( key, jsonc );
					}
				}
				updateTableInApplet( appletelement, jsono.toString() );
			}
		});
	}
	
	Element appletelement;
	@Override
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		
		Window.enableScrolling( false );
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		//console( "authtoken: "+authtoken );
		//String data = getdata( authtoken );
		//console( "data: "+data );
		
		
		Element pe = Document.get().createElement("param");
		pe.setAttribute("name", "jnlp_href");
		pe.setAttribute("value", "treedraw.jnlp");
		
		final Element ae = Document.get().createElement("applet");
		appletelement = ae;
		ae.appendChild( pe );
			
		ae.setAttribute("id", "datatable");
		ae.setAttribute("name", "datatable");
		ae.setAttribute("codebase", "http://thermusgenes.appspot.com/");
		//ae.setAttribute("codebase", "http://127.0.0.1/");
		//ae.setAttribute("width", "100%");
		//ae.setAttribute("height", "100%");
		ae.setAttribute("jnlp_href", "treedraw.jnlp");
		ae.setAttribute("archive", "treedraw.jar");
		ae.setAttribute("code", "org.simmi.DataTable");
		st = ae.getStyle();
		st.setWidth(100, Unit.PCT);
		st.setHeight(100, Unit.PCT);
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		final SimplePanel	applet = new SimplePanel();
		//applet.setSize("100%", "100%");
		applet.getElement().appendChild( ae );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
				applet.setSize(w+"px", (h-40)+"px");
			}
		});
		rp.setSize(w+"px", h+"px");
		applet.setSize(w+"px", (h-40)+"px");
		
		rp.add( applet );
		
		initFunctions( ae );
		
		/*final RootPanel	rp = RootPanel.get();
		Window.enableScrolling( false );
		w = Window.getClientWidth();
		h = Window.getClientHeight();
		rp.setSize(w+"", h+"");
		
		Style s = rp.getElement().getStyle();
		s.setBorderWidth(0.0, Unit.PX);
		s.setMargin(0.0, Unit.PX);
		s.setPadding(0.0, Unit.PX);
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				w = event.getWidth();
				h = event.getHeight();
				rp.setSize(w+"", h+"");
				if( table != null ) table.setSize(w+"", h+"");
			}
		});
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				data = DataTable.create();
		    	data.addColumn( ColumnType.STRING, "acc");
		    	data.addColumn( ColumnType.STRING, "species");
		    	data.addColumn( ColumnType.STRING, "doi");
		    	data.addColumn( ColumnType.STRING, "pubmed");
		    	data.addColumn( ColumnType.STRING, "journal");
		    	data.addColumn( ColumnType.STRING, "auth");
		    	data.addColumn( ColumnType.STRING, "sub_auth");
		    	data.addColumn( ColumnType.STRING, "sub_date");
		    	data.addColumn( ColumnType.STRING, "source");
		    	
		    	options = Options.create();
		    	options.setWidth("100%");
		    	options.setHeight("100%");
		    	options.setAllowHtml( true );
				
		    	RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, "http://130.208.252.7/therm.txt" );
		    	try {
					rb.sendRequest("", new RequestCallback() {
						@Override
						public void onResponseReceived(Request request, Response response) {
							String text = response.getText();
							String[] nsplit = text.split("\n");
							for( String n : nsplit ) {
								int r = data.getNumberOfRows();
								
								String[] tsplit = n.split("\t");
								data.addRow();
								data.setValue( r, 0, tsplit[0] );
								data.setValue( r, 1, tsplit[1] );
								data.setValue( r, 2, tsplit[2] );
								data.setValue( r, 3, tsplit[3] );
								data.setValue( r, 4, tsplit[4] );
								data.setValue( r, 5, tsplit[5] );
								data.setValue( r, 6, tsplit[6] );
								data.setValue( r, 7, tsplit[7] );
								data.setValue( r, 8, tsplit[8] );
							}
							
						    view = DataView.create( data );
						    table = new Table( view, options );
						    
						    table.setSize(w+"", h+"");
						    	
						    rp.add( table );
						}
						
						@Override
						public void onError(Request request, Throwable exception) {}
					});
				} catch (RequestException e) {
					e.printStackTrace();
				}
		    }
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);*/
	}
}
