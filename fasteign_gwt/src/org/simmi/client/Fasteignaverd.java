package org.simmi.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;

public class Fasteignaverd implements EntryPoint {

	static Map<String, Integer> mmap = new HashMap<String, Integer>();

	static {
		mmap.put("Janúar", 1);
		mmap.put("Febrúar", 2);
		mmap.put("Mars", 3);
		mmap.put("Apríl", 4);
		mmap.put("Maí", 5);
		mmap.put("Júní", 6);
		mmap.put("Júlí", 7);
		mmap.put("Ágúst", 8);
		mmap.put("September", 9);
		mmap.put("Október", 10);
		mmap.put("Nóvember", 11);
		mmap.put("Desember", 12);

		mmap.put("janúar", 1);
		mmap.put("febrúar", 2);
		mmap.put("mars", 3);
		mmap.put("apríl", 4);
		mmap.put("maí", 5);
		mmap.put("júní", 6);
		mmap.put("júlí", 7);
		mmap.put("ágúst", 8);
		mmap.put("september", 9);
		mmap.put("október", 10);
		mmap.put("nóvember", 11);
		mmap.put("desember", 12);
	}
	
	public interface Ibud {
		public String getNafn();
		public int getVerd();
		public int getFasteignamat();
		public int getBrunabotamat();
		public int getFermetrar();
		public int getHerbergi();
		public String getTegund();
		public Date getDagsetning();
		public String getUrlString();
		public String getImgUrl();
	};
	
	public final static class NatIbud extends JavaScriptObject implements Ibud {
		protected NatIbud() {};
		
		@Override
		public native String getNafn() /*-{
			return this.getNafn();
		}-*/;

		@Override
		public native int getVerd() /*-{
			return this.getVerd();
		}-*/;
		
		@Override
		public native String getImgUrl() /*-{
			return this.getImgUrl();
		}-*/;

		@Override
		public native int getFasteignamat() /*-{
			return this.getFasteignamat();
		}-*/;

		@Override
		public native int getBrunabotamat() /*-{
			return this.getBrunabotamat();
		}-*/;

		@Override
		public native int getFermetrar() /*-{
			return this.getFermetrar();
		}-*/;

		@Override
		public native int getHerbergi() /*-{
			return this.getHerbergi();
		}-*/;

		@Override
		public native String getTegund() /*-{
			return this.getTegund();
		}-*/;

		@Override
		public native Date getDagsetning() /*-{
			return this.getDagsetning();
		}-*/;

		@Override
		public native String getUrlString() /*-{
			return this.getUrlString();
		}-*/;
	};
	
	public class WebIbud implements Ibud {
		String nafn;
		int verd;
		int fastm;
		int brunm;
		String teg;
		int ferm;
		int herb;
		Date dat;
		String url;
		String imgurl;
		
		public String getImgUrl() {
			return imgurl;
		}
		
		public String getNafn() {
			return nafn;
		}

		public int getVerd() {
			return verd;
		}
		
		public int getFasteignamat() {
			return fastm;
		}
		
		public int getBrunabotamat() {
			return brunm;
		}
		
		public int getFermetrar() {
			return ferm;
		}
		
		public int getHerbergi() {
			return herb;
		}
		
		public String getTegund() {
			return teg;
		}
		
		public Date getDagsetning() {
			return dat;
		}

		public WebIbud(String nafn) {
			this.nafn = nafn;
		}

		public WebIbud(String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url) throws ParseException {
			this.nafn = nafn;
			this.verd = verd;
			this.fastm = fastm;
			this.brunm = brunm;
			this.teg = teg;
			this.ferm = ferm;
			this.herb = herb;
			this.dat = null;//DateFormat.getDateInstance().parse(dat);
		}

		public void set(int i, Object obj) {
			try {
				if (obj instanceof String) {
					String val = obj.toString();
					val = val.replaceAll("\\.", "");
					if (i == 0)
						verd = Integer.parseInt(val);
					else if (i == 1)
						fastm = Integer.parseInt(val);
					else if (i == 2)
						brunm = Integer.parseInt(val);
					else if (i == 3)
						teg = val;
					else if (i == 4)
						ferm = Integer.parseInt(val);
					else if (i == 5)
						herb = Integer.parseInt(val);
					else if (i == 6) {
						/*String[] split = val.split(" ");
						if (split.length >= 3 && mmap.containsKey(split[1])) {
							int year = Integer.parseInt(split[2]);
							int month = mmap.get(split[1]);
							int day = Integer.parseInt(split[0]);
							Calendar cal = Calendar.getInstance();
							cal.set(year, month - 1, day);
							dat = cal.getTime();
						}*/

					}
				} // else dat = (Date)obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getUrlString() {
			return url;
		}
		
		public boolean equals( Object o ) {
			return o instanceof Ibud && url.equals( ((Ibud)o).getUrlString() );
		}

		public String toString() {
			return nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat + "\t" + url;
		}
	};
	
	public final static class Fastpack extends JavaScriptObject {
		protected Fastpack() {};
		
		public native void search( String val ) /*-{
			this.search( val );
		}-*/;
		
		public native int getFetchNum() /*-{
			return this.getFetchNum();
		}-*/;
		
		public native int getTotal() /*-{
			return this.getFetchNum();
		}-*/;
		
		public native List<Ibud> getIbuds() /*-{
			return this.getIbuds();
		}-*/;
		
		public native NatIbud getIbud( int i ) /*-{
			return this.getIbud( i );
		}-*/;
		
		public native String getName( int i ) /*-{
			return this.getName( i );
		}-*/;
	};
	
	Fastpack fastpack;
	public native Fastpack getFastpack() /*-{
		return $wnd.fastpack;
	}-*/;
	
	List<Ibud> iblist = new ArrayList<Ibud>();
	String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
	//String base = "http://mail.google.com/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
	
	final String h2 = "<h2 style=\"margin-bottom: 0.91em; font-size:1.5em;\">";
	final String[] buds = { "estate-verd", "estate-fasteignamat", "estate-brunabotamat", "estate-teg_eign", "estate-fermetrar", "estate-fjoldi_herb", "estate-sent_dags" };
	public Ibud subload( String str ) {
		int ind = str.indexOf(h2);
		int stop = str.indexOf("</h2>", ind);
		String ibud = str.substring(ind + h2.length(), stop).trim();
		Ibud ib = new WebIbud(ibud);
		if( !iblist.contains(ib) ) {
			iblist.add(ib);
			int i = 0;
			for (String bud : buds) {
				ind = str.indexOf(bud);
				int start = str.indexOf("fst-rvalue\">", ind);
				stop = str.indexOf("</td>", start);
				String sval = str.substring(start + 12, stop).trim();

				((WebIbud)ib).set(i++, sval);
			}
		}
		//count++;
		return ib;
	}
	
	int count;
	public void load( String str ) throws RequestException {
		count = 0;

		String[] vals = str.split("fast-nidurstada clearfix");
		System.err.println(vals.length);
		for (String val : vals) {
			int ind = val.indexOf("<a href=\"");
			int stop = val.indexOf("\"", ind + 10);

			String sub = val.substring(ind + 9, stop);
			if (sub.contains("/mm/fasteignir")) {
				final String suburlstr = "http://www.mbl.is" + sub;
				/*url = new URL(suburlstr);
				stream = url.openStream();

				str = "";
				r = stream.read(bb);
				while (r > 0) {
					str += new String(bb, 0, r, "ISO-8859-1");
					r = stream.read(bb);
				}
				stream.close();*/
				
				RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, suburlstr );
				rb.sendRequest("", new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						String str = response.getText();
						Ibud ib = subload( str );
						((WebIbud)ib).url = suburlstr;
						count++;
						if( count == 25 && t != null ) t.schedule(1000);
						Browser.getWindow().getConsole().log( ib );
					}
					
					@Override
					public void onError(Request request, Throwable exception) {}
				});
			}
		}
	}
	
	public void stuff(String urlstr) throws RequestException {
		/*URL url = new URL(urlstr);
		InputStream stream = url.openStream();

		String str = "";
		byte[] bb = new byte[1024];
		int r = stream.read(bb);
		while (r > 0) {
			str += new String(bb, 0, r);
			r = stream.read(bb);
		}

		stream.close();*/
		
		RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, urlstr );
		rb.sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String str = response.getText();
				try {
					load( str );
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {}
		});

		//return count == 25;
	}
	
	Timer t = null;
	public native void init() /*-{
		var s = this;
		$wnd.erm = function( name ) {
			s.@org.simmi.client.Fasteignaverd::erm(Ljava/lang/String;)( name );
		};
	}-*/;
	
	public void erm( String name ) {
		vp.add( new Label(name) );
	}
	
	VerticalPanel	vp;
	
	@Override
	public void onModuleLoad() {	
		RootPanel	rp = RootPanel.get();
		//final DataGrid<Ibud>	ibudGrid = new DataGrid<Ibud>();
		fastpack = getFastpack();
		
		Window.enableScrolling( false );
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		
		HorizontalPanel	hp = new HorizontalPanel();
		//hp.setwi
		hp.setSpacing(0);
		
		final VerticalPanel	table = new VerticalPanel();
		final ScrollPanel	scroll = new ScrollPanel( table );
		
		scroll.setSize(w+"px", h+"px");
		table.setSize(w+"px", "100%");
		
		table.add( new Label("fuck") );
		
		vp = new VerticalPanel();
		vp.setSize(w+"px", h+"px");
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		final FocusPanel	fp = new FocusPanel();
		fp.setSize(w+"px", h+"px");
		final FocusPanel	fp2 = new FocusPanel();
		fp2.setSize(w+"px", h+"px");
		
		fp.addTouchStartHandler( new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				
			}
		});
		fp.addTouchMoveHandler( new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				fp2.getElement().scrollIntoView();
			}
		});
		fp.addTouchEndHandler( new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				
			}
		});
		fp.addTouchCancelHandler( new TouchCancelHandler() {
			@Override
			public void onTouchCancel(TouchCancelEvent event) {
				
			}
		});
		fp2.addTouchStartHandler( new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				
			}
		});
		fp2.addTouchMoveHandler( new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				fp.getElement().scrollIntoView();
			}
		});
		fp2.addTouchEndHandler( new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				
			}
		});
		fp2.addTouchCancelHandler( new TouchCancelHandler() {
			@Override
			public void onTouchCancel(TouchCancelEvent event) {
				
			}
		});
		
		VerticalPanel	subvp = new VerticalPanel();
		subvp.setWidth("100%");
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		fp.add( vp );
		vp.add( subvp );
		subvp.setSpacing(5);
		
		//ibudGrid.setSize( (w-20)+"px", "600px" );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				vp.setSize(w+"px", h+"px");
				fp.setSize(w+"px", h+"px");
				fp2.setSize(w+"px", h+"px");
				table.setWidth(w+"px");
				scroll.setSize(w+"px", h+"px");
				
				//int h = event.getHeight();
				//ibudGrid.setSize( (w-20)+"px", "600px" );
			}
		});
		
		HorizontalPanel	topcomp = new HorizontalPanel();
		topcomp.setSize("100%", "42px");
		topcomp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		topcomp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		Label	title = new Label("Fasteignaverð 1.1");
		//Style style = title.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		
		title.setSize("100%", "42px");
		subvp.add( title );
		//Label loc = new Label("Veldu svæði:");
		//subvp.add(loc);
		
		final ListBox loccomb = new ListBox();
		//style = loccomb.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		loccomb.setSize("100%", "42px");
		loccomb.addItem("Veldu svæði");
		loccomb.addItem("101 Miðbær");
		loccomb.addItem("103 Kringlan/Hvassaleiti");
		loccomb.addItem("104 Vogar");
		loccomb.addItem("105 Austurbær");
		loccomb.addItem("107 Vesturbær");
		loccomb.addItem("108 Austurbær");
		loccomb.addItem("109 Bakkar/Seljahverfi");
		loccomb.addItem("110 Árbær/Selás");
		loccomb.addItem("111 Berg/Hólar/Fell");
		loccomb.addItem("112 Grafarvogur");
		loccomb.addItem("113 Grafarholt");
		loccomb.addItem("116 Kjalarnes");
		loccomb.addItem("170 Seltjarnarnes");
		loccomb.addItem("190 Vogar");
		loccomb.addItem("110 Árbær/Selás");
		loccomb.addItem("200 Kópavogur");
		loccomb.addItem("201 Kópavogur");
		loccomb.addItem("202 Kópavogur");
		loccomb.addItem("203 Kópavogur");
		loccomb.addItem("210 Garðabær");
		loccomb.addItem("211 Garðabær (Arnarnes)");
		loccomb.addItem("220 Hafnarfjörður");
		loccomb.addItem("221 Hafnarfjörður");
		loccomb.addItem("225 Álftanes");
		subvp.add(loccomb);

		//Label typ = new Label("Veldu tegund:");
		//subvp.add(typ);
		final ListBox typcomb = new ListBox();
		//style = typcomb.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		typcomb.setSize("100%", "42px");
		typcomb.addItem("Veldu tegund");
		typcomb.addItem("Fjölbýli");
		typcomb.addItem("Einbýli");
		typcomb.addItem("Hæðir");
		typcomb.addItem("Parhús/Raðhús");
		subvp.add(typcomb);

		Label big = new Label("Veldu stærð:");
		//style = big.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		//big.setSize("15%", "42px");
		topcomp.add(big);
		final IntegerBox bigfield = new IntegerBox();
		bigfield.setStyleName("test");
		//style = bigfield.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		
		bigfield.setSize("100%", "42px");
		bigfield.setValue(100);
		topcomp.add(bigfield);
		
		Label bigdiff = new Label("+/-");
		//style = bigdiff.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		//bigdiff.setSize("5%", "42px");
		topcomp.add(bigdiff);
		final IntegerBox bigdifffield = new IntegerBox();
		bigdifffield.setStyleName("test");
		//style = bigdifffield.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		
		bigdifffield.setSize("100%", "42px");
		bigdifffield.setValue(30);
		topcomp.add(bigdifffield);
		subvp.add( topcomp );
		
		topcomp.setCellWidth(bigfield, "30%");
		topcomp.setCellWidth(bigdifffield, "30%");
		
		Button	leita = new Button("Leita");
		//style = leita.getElement().getStyle();
		//style.setFontSize(24.0, Unit.PX);
		
		leita.setSize("100%", "42px");
		leita.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				scroll.getElement().scrollIntoView();
				
				String loc = loccomb.getItemText( loccomb.getSelectedIndex() );
				String[] split = loc.split(" ");
				String pnr = split[0];
				String val = base.replace("svaedi=", "svaedi=" + pnr + "_" + pnr);
				String teg = typcomb.getItemText( typcomb.getSelectedIndex() ).toLowerCase();
				teg = teg.replace("æ", "ae");
				teg = teg.replace("ö", "o");
				teg = teg.replace("ý", "y");
				val = val.replace("tegund=", "tegund=" + teg);
				//String diffstr = bigdifffield.getText();
				int diff = bigdifffield.getValue(); //Integer.parseInt(diffstr);
				int ferm = bigfield.getValue(); //Integer.parseInt(bigfield.getText());
				val = val.replace("fermetrar_fra=", "fermetrar_fra=" + (ferm - diff));
				val = val.replace("fermetrar_til=", "fermetrar_til=" + (ferm + diff));

				final String tstr = val;
				fastpack.search( tstr );
				
				final List<Label>	labels = new ArrayList<Label>();
				final Timer t = new Timer() {
					@Override
					public void run() {
						if( labels.size() == fastpack.getFetchNum() ) this.cancel();
						else {
							for( int i = labels.size(); i < fastpack.getTotal(); i++ ) {
								NatIbud nib = fastpack.getIbud(i);
								Label label = new Label( nib.getNafn() );
								Image img = new Image( nib.getImgUrl(), 0, 0, 50, 50 );
								
								HorizontalPanel	hp = new HorizontalPanel();
								hp.add( img );
								hp.add( label );
								table.add( hp );
								labels.add( label );
							}
						}
					}
				};
				t.scheduleRepeating( 1000 );
				
				//tstr.replace("offset", "offset=" + i);
				
				/*int i = 0;
				//tstr.replace("offset", "offset=" + i);
				//pgbar.setIndeterminate(true);
				
				t = new Timer() {
					int i = 0;
					@Override
					public void run() {
						try {
							stuff( tstr.replace("offset", "offset=" + i) );
							i += 25;
						} catch (RequestException e) {
							e.printStackTrace();
						}
					}
				};
				t.schedule(0);
				/*Thread t = new Thread() {
					public void run() {
						calc(tstr);
						//createModels(table, ptable);
						//pgbar.setIndeterminate(false);
					
						/*try {
							JSObject jso = JSObject.getWindow( Fasteign.this );
							jso.call( "clearMarkers", new Object[] {} );
							Set<String>	adset = new HashSet<String>();
							for( Ibud ib : iblist ) {
								adset.add( ib.nafn );
							}
							
							for( String address : adset ) {
								jso.call( "codeAddress", new Object[] { address } );
							}
						} catch( Exception e ) {
							
						}*
					}
				};
				t.start();*/
			}
		});
		subvp.add( leita );

		/*JComponent botcomp = new JComponent() {};
		botcomp.setLayout(new FlowLayout());

		final JProgressBar pgbar = new JProgressBar();
		botcomp.add(pgbar);
		JComponent c = new JComponent() {};
		c.setPreferredSize( new Dimension(100,30) );
		botcomp.add( c );*/
		
		fp2.add( scroll );
		
		//vp.add( ibudGrid );
		hp.add( fp );
		hp.add( fp2 );
		rp.add( hp );
	}
}
