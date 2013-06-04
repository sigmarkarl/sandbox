package org.simmi.fastdroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

	String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
	//String base = "http://mail.google.com/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
	
	public class Ibud {
		String nafn;
		int verd;
		int fastm;
		int brunm;
		String teg;
		int ferm;
		int herb;
		Date dat;
		String url;
		
		@JavascriptInterface
		public String getNafn() {
			return nafn;
		}

		@JavascriptInterface
		public int getVerd() {
			return verd;
		}
		
		@JavascriptInterface
		public int getFasteignamat() {
			return fastm;
		}
		
		@JavascriptInterface
		public int getBrunabotamat() {
			return brunm;
		}
		
		@JavascriptInterface
		public int getFermetrar() {
			return ferm;
		}
		
		@JavascriptInterface
		public int getHerbergi() {
			return herb;
		}
		
		@JavascriptInterface
		public String getTegund() {
			return teg;
		}
		
		@JavascriptInterface
		public Date getDagsetning() {
			return dat;
		}
		
		public Ibud(String nafn) {
			this.nafn = nafn;
		}

		public Ibud(String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url) throws ParseException {
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
		
		@JavascriptInterface
		public String getUrlString() {
			return url;
		}
		
		public boolean equals( Object o ) {
			return o != null && url!= null && o instanceof Ibud && url.equals( ((Ibud)o).url );
		}

		public String toString() {
			return nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat + "\t" + url;
		}
	};
	
	List<Ibud> iblist = new ArrayList<Ibud>();
	
	final String h2 = "<h2 style=\"margin-bottom: 0.91em; font-size:1.5em;\">";
	final String[] buds = { "estate-verd", "estate-fasteignamat", "estate-brunabotamat", "estate-teg_eign", "estate-fermetrar", "estate-fjoldi_herb", "estate-sent_dags" };
	public Ibud subload( String str ) {
		int ind = str.indexOf(h2);
		int stop = str.indexOf("</h2>", ind);
		String ibud = str.substring(ind + h2.length(), stop).trim();
		Ibud ib = new Ibud(ibud);
		if( !iblist.contains(ib) ) {
			iblist.add(ib);
			int i = 0;
			for (String bud : buds) {
				ind = str.indexOf(bud);
				int start = str.indexOf("fst-rvalue\">", ind);
				stop = str.indexOf("</td>", start);
				String sval = str.substring(start + 12, stop).trim();

				ib.set(i++, sval);
			}
		}
		//count++;
		return ib;
	}
	
	int count;
	int total = -1;
	public void load( String str ) {
		count = 0;

		String[] vals = str.split("fast-nidurstada clearfix");
		total = vals.length;
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
				
				try {
					URL	url = new URL( suburlstr );
					java.io.InputStream is = url.openStream();
					ByteArrayOutputStream	baos = new java.io.ByteArrayOutputStream();
					byte[] bb = new byte[4096];
					int r = is.read( bb );
					while( r > 0 ) {
						baos.write( bb, 0, r );
						r = is.read( bb );
					}
					is.close();
					baos.close();
					
					Ibud ib = subload( baos.toString() );
					ib.url = suburlstr;
					
					//myWebView.loadUrl("javascript:erm('"+ib.nafn+"')");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/*RequestBuilder rb = new RequestBuilder( RequestBuilder.GET, suburlstr );
				rb.sendRequest("", new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						String str = response.getText();
						Ibud ib = subload( str );
						ib.url = suburlstr;
						count++;
						if( count == 25 && t != null ) t.schedule(1000);
						Browser.getWindow().getConsole().log( ib );
					}
					
					@Override
					public void onError(Request request, Throwable exception) {}
				});*/
			}
		}
	}
	
	private class searchTask extends AsyncTask<String,Integer,Long> {
		@Override
		protected Long doInBackground(String... arg0) {
			try {
				URL	url = new URL( arg0[0] );
				java.io.InputStream is = url.openStream();
				ByteArrayOutputStream	baos = new java.io.ByteArrayOutputStream();
				byte[] bb = new byte[4096];
				int r = is.read( bb );
				while( r > 0 ) {
					baos.write( bb, 0, r );
					r = is.read( bb );
				}
				is.close();
				baos.close();
				
				load( baos.toString() );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	};
	
	public class Fastpack {
		@JavascriptInterface
		public void search( String what ) {			
			int i = 0;
			new searchTask().execute( what.replace("offset", "offset=" + i) );
		}
		
		@JavascriptInterface
		public int getFetchNum() {
			return iblist.size();
		}
		
		@JavascriptInterface
		public List<Ibud> getIbuds() {
			return iblist;
		}
		
		@JavascriptInterface
		public Ibud getIbud( int i ) {
			return iblist.get(i);
		}
		
		@JavascriptInterface
		public int getTotal() {
			return total;
		}
		
		@JavascriptInterface
		public String getName( int i ) {
			return iblist.get(i).nafn;
		}
	};
	
	WebView	myWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		myWebView = (WebView)findViewById(R.id.webView1);
		//myWebView.setWebViewClient( new WebViewClient() );
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		Fastpack fastpack = new Fastpack();
		myWebView.addJavascriptInterface( fastpack, "fastpack" );
		Log.i("mu", "ma");
		myWebView.loadUrl("http://192.168.1.70:8888/Fasteignaverd.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
