package org.simmi.fastdroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	//String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&gata=&lysing=";
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
		String imgurl;
		Drawable img;
		
		@JavascriptInterface
		public String getNafn() {
			return nafn;
		}
		
		@JavascriptInterface
		public String getImgUrl() {
			return imgurl;
		}
		
		public Drawable getImage() {
			return img;
		}
		
		public void setDrawable( Drawable draw ) {
			img = draw;
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

		public Ibud( String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url, String imgurl ) throws ParseException {
			this.nafn = nafn;
			this.verd = verd;
			this.fastm = fastm;
			this.brunm = brunm;
			this.teg = teg;
			this.ferm = ferm;
			this.herb = herb;
			this.dat = null;//DateFormat.getDateInstance().parse(dat);
			this.imgurl = imgurl;
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
		int ind = str.indexOf("src=\"/tncache");
		int stop = str.indexOf(".jpg", ind);
		
		while( stop > ind+100 ) {
			Log.i("bleh", ind + "  " + stop );
			
			ind = str.indexOf("src=\"/tncache", ind+1);
			stop = str.indexOf(".jpg", ind);
		}
		
		String imgurl = null;
		if( ind != -1 && stop != -1 ) imgurl = "http://www.mbl.is"+str.substring(ind+5, stop+4); 
		
		ind = str.indexOf(h2);
		stop = str.indexOf("</h2>", ind);
		String ibud = str.substring(ind + h2.length(), stop).trim();
		Ibud ib = new Ibud(ibud);
		ib.imgurl = imgurl;
		
		if( imgurl != null ) {
			try {
				InputStream is = (InputStream)new URL(imgurl).getContent();
				ib.setDrawable( Drawable.createFromStream(is, ib.getNafn()) );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
					
					Ibud ib = subload( baos.toString("ISO-8859-1") );
					ib.url = suburlstr;
					
					handler.sendEmptyMessage(0);
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
				
				load( baos.toString("ISO-8859-1") );
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
	
	CollectionPagerAdapter mDemoCollectionPagerAdapter;
	WebView	myWebView;
	ViewPager myPager;
	static Fastpack	fastpack;
	Handler	handler;
	List<LinearLayout>	labels = new ArrayList<LinearLayout>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		mDemoCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
	    myPager = (ViewPager) findViewById(R.id.pager);
	    myPager.setAdapter(mDemoCollectionPagerAdapter);
	    
	    handler = new Handler(Looper.getMainLooper()) {
	        @Override
	        public void handleMessage(Message msg) {
	        	//if( labels.size() == fastpack.getFetchNum() ) ;//this.cancel();
				//else {
	        		final int c = Color.rgb(220, 220, 255);
					for( int i = labels.size(); i < fastpack.getFetchNum(); i++ ) {
						Ibud ib = fastpack.getIbud(i);
						
						final LinearLayout llayout = (LinearLayout)otherView.findViewById(R.id.llayout1);
						LinearLayout hlayout = (LinearLayout)llayout.inflate( llayout.getContext(), R.layout.sub_object, (ViewGroup)llayout );
						
						if( hlayout.getChildCount() > 0 ) {
							LinearLayout ll = (LinearLayout)hlayout.getChildAt( hlayout.getChildCount()-1 );
							ll.setClickable( true );
							
							ll.setOnClickListener( new OnClickListener() {
								@Override
								public void onClick(View v) {
									Drawable background = v.getBackground();
									v.setBackgroundColor( background == null || Color.WHITE == ((ColorDrawable)v.getBackground()).getColor() ? c : Color.WHITE );
								}
							});
							ImageView iv = (ImageView)ll.getChildAt(0);
							Drawable drawable = ib.getImage();
							if( drawable != null ) iv.setImageDrawable( drawable );
							
							LinearLayout hl = (LinearLayout)ll.getChildAt(1);
							TextView tv = (TextView)hl.getChildAt(0);
							tv.setText( ib.getNafn() );
							
							//hlayout.find
							//final TextView textview = (TextView)hlayout.inflate( hlayout.getContext(), R.layout.textview_object, (ViewGroup)hlayout);
							//textview.setText( ib.getNafn() );
							
							//llayout.addView( hlayout );
							
							labels.add( hlayout );
						}
					}
				//}
	        }
	    };
	        
		//myWebView = (WebView)findViewById(R.id.webView1);
		//myWebView.setWebViewClient( new WebViewClient() );
		
		//WebSettings webSettings = myWebView.getSettings();
		//webSettings.setJavaScriptEnabled(true);
		
		fastpack = new Fastpack();
		//myWebView.addJavascriptInterface( fastpack, "fastpack" );
		//myWebView.loadUrl("http://192.168.1.70:8888/Fasteignaverd.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
	    public CollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	        Fragment fragment = new ObjectFragment();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt(ObjectFragment.ARG_OBJECT, i + 1);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return 2;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return "OBJECT " + (position + 1);
	    }
	};
	
	static View otherView = null;
	static final String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
	public static class ObjectFragment extends Fragment {
	    public static final String ARG_OBJECT = "object";

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        // The last two arguments ensure LayoutParams are inflated
	        // properly.
	    	Bundle args = getArguments();
	    	int val = args.getInt(ARG_OBJECT);
	        View rootView;
	        if( val == 1 ) {
	        	rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
	        	if( otherView == null ) otherView = inflater.inflate(R.layout.list_object, container, false);
	        	final LinearLayout llayout = (LinearLayout)otherView.findViewById(R.id.llayout1);
	        	
	        	final Spinner spinner1 = (Spinner)rootView.findViewById(R.id.spinner1);
	        	final Spinner spinner2 = (Spinner)rootView.findViewById(R.id.spinner2);
	        	
	        	final EditText edittext1 = (EditText)rootView.findViewById(R.id.editText1);
	        	final EditText edittext2 = (EditText)rootView.findViewById(R.id.editText2);
	        	
	        	Button button = (Button)rootView.findViewById(R.id.button);
	        	button.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String loc = spinner1.getSelectedItem().toString();//loccomb.getItemText( loccomb.getSelectedIndex() );
						String[] split = loc.split(" ");
						String pnr = split[0];
						String val = base.replace("svaedi=", "svaedi=" + pnr + "_" + pnr);
						String teg = spinner2.getSelectedItem().toString();//typcomb.getItemText( typcomb.getSelectedIndex() ).toLowerCase();
						teg = teg.replace("æ", "ae");
						teg = teg.replace("ö", "o");
						teg = teg.replace("ý", "y");
						val = val.replace("tegund=", "tegund=" + teg);
						//String diffstr = bigdifffield.getText();
						int diff = Integer.parseInt( edittext1.getText().toString() ); //bigdifffield.getValue(); //Integer.parseInt(diffstr);
						int ferm = Integer.parseInt( edittext2.getText().toString() ); //bigfield.getValue(); //Integer.parseInt(bigfield.getText());
						val = val.replace("fermetrar_fra=", "fermetrar_fra=" + (ferm - diff));
						val = val.replace("fermetrar_til=", "fermetrar_til=" + (ferm + diff));

						final String tstr = val;
						
						//texterm.setText( tstr );
						//otherView.
						fastpack.search( tstr );
					}
				});
	        	// Create an ArrayAdapter using the string array and a default spinner layout
	        	ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(container.getContext(), R.array.loc_array, android.R.layout.simple_spinner_item);
	        	adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	
	        	ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(container.getContext(), R.array.typ_array, android.R.layout.simple_spinner_item);
	        	adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	// Apply the adapter to the spinner
	        	spinner1.setAdapter(adapter1);
	        	spinner2.setAdapter(adapter2);
	        } else {
	        	if( otherView == null ) otherView = inflater.inflate(R.layout.list_object, container, false);
	        	rootView = otherView;
	        	
	        	final Spinner sortspinner = (Spinner)rootView.findViewById(R.id.sortspinner);
	        	ArrayAdapter<CharSequence> sortadapter = ArrayAdapter.createFromResource(container.getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
	        	sortadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	
	        	sortspinner.setAdapter(sortadapter);
	        }
	        //((TextView) rootView.findViewById(android.R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
	        return rootView;
	    }
	};
}
