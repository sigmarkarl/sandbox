package org.simmi.fastdroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
	
	public class Ibud implements Comparable<Ibud> {
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
		public double getFermetraverd() {
			return (double)this.getVerd()/(double)this.getFermetrar();
		}
		
		@JavascriptInterface
		public double getFermetraverdFasteignamats() {
			return (double)this.getFasteignamat()/(double)this.getFermetrar();
		}
		
		@JavascriptInterface
		public double getVerdPFasteignamat() {
			if( this.getFasteignamat() > 0 ) return (double)this.getVerd()/(double)this.getFasteignamat();
			return -1.0;
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

		@Override
		public int compareTo(Ibud another) {
			return fastpack.compare( this, another );
		}
	};
	
	static List<Ibud> iblist = new ArrayList<Ibud>();
	
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
			
			//updateAverage();
			
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
	
	//int count;
	//int total = -1;
	public boolean load( String str ) {
		int count = 0;
		String[] vals = str.split("fast-nidurstada clearfix");
		//total = vals.length;
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
				count++;
			}
		}
		return count == 25;
	}
	
	private class searchTask extends AsyncTask<String,Integer,Long> {
		@Override
		protected Long doInBackground(String... arg0) {
			int i = 0;
			while( true ) {
				boolean b = false;
				ByteArrayOutputStream	baos = new java.io.ByteArrayOutputStream();
				try {
					String urlstr = arg0[0].replace("offset", "offset=" + i);
					URL	url = new URL( urlstr );
					java.io.InputStream is = url.openStream();
					byte[] bb = new byte[4096];
					int r = is.read( bb );
					while( r > 0 ) {
						baos.write( bb, 0, r );
						r = is.read( bb );
					}
					is.close();
					baos.close();
					
					b = load( baos.toString("ISO-8859-1") );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if( !b ) break;
				i += 25;
			}
			
			return null;
		}
	};
	
	public class Fastpack {
		int comp = 0;
		
		@JavascriptInterface
		public void search( String what ) {			
			new searchTask().execute( what );
		}
		
		public int compare( Ibud ib1, Ibud ib2 ) {
			if( comp == 0 ) return ib1.nafn.compareTo( ib2.nafn );
			else if( comp == 1 ) return ib1.verd - ib2.verd;
			else if( comp == 2 ) return (int)(ib1.getFermetraverd() - ib2.getFermetraverd());
			else if( comp == 3 ) return ib1.getFermetrar() - ib2.getFermetrar();
			else if( comp == 4 ) return ib1.getHerbergi() - ib2.getHerbergi();
			return 0;
		}
		
		@JavascriptInterface
		public double getAvgFermverd() {
			double avg = 0.0;
			for( Ibud ib : iblist ) {
				avg += ib.getFermetraverd();
			}
			return avg/iblist.size();
		}
		
		@JavascriptInterface
		public double getFermverdStdDev( double avg ) {
			double tot = 0.0;
			for( Ibud ib : iblist ) {
				double val = avg - ib.getFermetraverd();
				tot += val*val;
			}
			return Math.sqrt( tot/iblist.size() );
		}
		
		@JavascriptInterface
		public double getAvgVerdpFmat() {
			double avg = 0.0;
			int i = 0;
			for( Ibud ib : iblist ) {
				if( ib.getFasteignamat() > 0 ) {
					double vpfm = ib.getVerdPFasteignamat();
					avg += vpfm;
					i++;
				}
			}
			return Math.round( 100.0*avg/i )/100.0;
		}
		
		@JavascriptInterface
		public double getVerdpFmatStdDev( double avg ) {
			double tot = 0.0;
			int i = 0;
			for( Ibud ib : iblist ) {
				if( ib.getFasteignamat() > 0 ) {
					double vpfm = avg - ib.getVerdPFasteignamat();
					tot += vpfm*vpfm;
					i++;
				}
			}
			return Math.round( 100.0*Math.sqrt( tot/i ) )/100.0;
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
		
		/*@JavascriptInterface
		public int getTotal() {
			return total;
		}*/
		
		@JavascriptInterface
		public String getName( int i ) {
			return iblist.get(i).nafn;
		}
	};
	
	public static void updateAverage() {
		double ravgfmv = fastpack.getAvgFermverd();
		double avgfmv = Math.round( 1.0*ravgfmv )/1.0;
		double stddfmv = Math.round( 1.0*fastpack.getFermverdStdDev( ravgfmv ) )/1.0;
		
		double avgvpfm = fastpack.getAvgVerdpFmat();
		double stddvpfm = fastpack.getVerdpFmatStdDev( avgvpfm );
		
		if( textsmt != null ) {
			//String dval = Integer.toString( (int)avgfmv );
			textsmt.setText( "M.fermv: "+(int)avgfmv+"±"+(int)stddfmv );
		}
		
		if( textsmt2 != null ) {
			//String dval = Double.toString( avgfmv );
			textsmt2.setText( "M.verð/fmat: "+avgvpfm+"±"+stddvpfm );
		}
		
		int i = 0;
		for( LinearLayout ll : labels ) {
			Ibud ib = iblist.get( i );
			//LinearLayout ll = labels.get( i );
			
			LinearLayout hl = (LinearLayout)ll.getChildAt(1);
			
			TextView tv3 = (TextView)hl.getChildAt(2);
			
			String verdstr =  Integer.toString( ib.getVerd() );
			String fsmstr = Integer.toString( ib.getFasteignamat() );
			double vpfm = Math.round( 100.0*ib.getVerdPFasteignamat() )/100.0;
			String str = "Verð: " + verdstr + " / Fasteignamat: " + fsmstr + " = ";
			String vstr = str + vpfm;
			
			SpannableString spstr = new SpannableString( vstr );
			if( vpfm > avgvpfm+stddvpfm ) spstr.setSpan( new ForegroundColorSpan( Color.RED ), str.length(), vstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			else if( vpfm < avgvpfm-stddvpfm ) spstr.setSpan( new ForegroundColorSpan( Color.BLUE ), str.length(), vstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			
			tv3.setText( spstr, TextView.BufferType.SPANNABLE );
			
			TextView tv4 = (TextView)hl.getChildAt(3);
			String fmstr = Integer.toString( (int)ib.getFermetraverd() );
			String ffmstr = Integer.toString( (int)ib.getFermetraverdFasteignamats() );
			str = "Fermetraverð: " + fmstr + " - Fermv.fasteignamats: " + ffmstr;
			
			spstr = new SpannableString( str );
			if( ib.getFermetraverd() > avgfmv+stddfmv ) spstr.setSpan( new ForegroundColorSpan( Color.RED ), 14, 14+fmstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			else if( ib.getFermetraverd() < avgfmv-stddfmv ) spstr.setSpan( new ForegroundColorSpan( Color.BLUE ), 14, 14+fmstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			//if( ib.getFermetraverdFasteignamats() > avgvpfm+stddvpfm ) spstr.setSpan( new ForegroundColorSpan( Color.RED ), 38+fmstr.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			//else if( ib.getFermetraverdFasteignamats() < avgvpfm-stddvpfm ) spstr.setSpan( new ForegroundColorSpan( Color.BLUE ), 38+fmstr.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			tv4.setText( spstr, TextView.BufferType.SPANNABLE );
			
			//LinearLayout ll = (LinearLayout)hlayout.getChildAt( i );
			i++;
			
		}
	}
	
	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";
	public static void openBrowser(final Context context, String url) {
	     if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
	            url = HTTP + url;
	     }

	     Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	     context.startActivity(Intent.createChooser(intent, "Choose browser"));
	}
	
	CollectionPagerAdapter mDemoCollectionPagerAdapter;
	WebView	myWebView;
	static ViewPager myPager;
	static Fastpack	fastpack;
	Handler	handler;
	static List<LinearLayout>	labels = new ArrayList<LinearLayout>();
	static final int fcol = Color.rgb(220, 220, 255);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		mDemoCollectionPagerAdapter = new CollectionPagerAdapter(getFragmentManager());
	    myPager = (ViewPager) findViewById(R.id.pager);
	    myPager.setAdapter(mDemoCollectionPagerAdapter);
	    
	    handler = new Handler(Looper.getMainLooper()) {
	        @Override
	        public void handleMessage(Message msg) {
	        	//if( labels.size() == fastpack.getFetchNum() ) ;//this.cancel();
				//else {
					for( int i = labels.size(); i < fastpack.getFetchNum(); i++ ) {
						final Ibud ib = fastpack.getIbud(i);
						final int ii = i;
						
						final LinearLayout llayout = (LinearLayout)otherView.findViewById(R.id.llayout1);
						LinearLayout hlayout = (LinearLayout)llayout.inflate( llayout.getContext(), R.layout.sub_object, (ViewGroup)llayout );
						
						if( hlayout.getChildCount() > 0 ) {
							final LinearLayout ll = (LinearLayout)hlayout.getChildAt( hlayout.getChildCount()-1 );
							ll.setClickable( true );
							
							ll.setBackgroundColor( Color.WHITE );
							ll.setOnClickListener( new OnClickListener() {
								@Override
								public void onClick(View v) {
									Drawable background = v.getBackground();
									v.setBackgroundColor( background == null || Color.WHITE == ((ColorDrawable)v.getBackground()).getColor() ? fcol : Color.WHITE );
								}
							});
							ImageView iv = (ImageView)ll.getChildAt(0);
							Drawable drawable = ib.getImage();
							if( drawable != null ) iv.setImageDrawable( drawable );
							
							LinearLayout hl = (LinearLayout)ll.getChildAt(1);
							TextView tv = (TextView)hl.getChildAt(0);
							tv.setText( Html.fromHtml( "<b>"+ib.getNafn()+"</b>" ) );
							
							TextView tv2 = (TextView)hl.getChildAt(1);
							tv2.setText( "Fermetrar: " + ib.getFermetrar() + " / Herbergi: " + ib.getHerbergi() );
							
							OnGestureListener gl = new OnGestureListener() {			
								@Override
								public boolean onSingleTapUp(MotionEvent e) {
									return false;
								}
								
								@Override
								public void onShowPress(MotionEvent e) {}
								
								@Override
								public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
									return false;
								}
								
								@Override
								public void onLongPress(MotionEvent e) {
									openBrowser(ll.getContext(), iblist.get(ii).url);
								}
								
								@Override
								public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
									return false;
								}
								
								@Override
								public boolean onDown(MotionEvent e) {
									return false;
								}
							};
							final GestureDetector gestureDetector = new GestureDetector( ll.getContext(), gl );
							ll.setOnTouchListener( new OnTouchListener() {
								@Override
								public boolean onTouch(View v, MotionEvent event) {
									return gestureDetector.onTouchEvent( event );
								}
							});
							
							labels.add( ll );
							updateAverage();
							
							//hlayout.find
							//final TextView textview = (TextView)hlayout.inflate( hlayout.getContext(), R.layout.textview_object, (ViewGroup)hlayout);
							//textview.setText( ib.getNafn() );
							
							//llayout.addView( hlayout );
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
	
	static TextView	textsmt = null;
	static TextView	textsmt2 = null;
	
	static View otherView = null;
	static final String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&gata=&lysing=";
	public static class ObjectFragment extends Fragment {
	    public static final String ARG_OBJECT = "object";

	    @Override
	    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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
						WebView wv = new WebView( container.getContext() );
						WebViewClient wvc = new WebViewClient() {
							@Override
							public void onPageFinished( WebView view, String url ) {
								super.onPageFinished(view, url);
								
								String loc = spinner1.getSelectedItem().toString();//loccomb.getItemText( loccomb.getSelectedIndex() );
								String[] split = loc.split(" ");
								String pnr = split[0];
								String val = base.replace("svaedi=", "svaedi=" + pnr + "_" + pnr);
								String teg = spinner2.getSelectedItem().toString();//typcomb.getItemText( typcomb.getSelectedIndex() ).toLowerCase();
								teg = teg.replace("æ", "ae");
								teg = teg.replace("ö", "o");
								teg = teg.replace("ý", "y");
								
								if( teg.equals("Fjolbyli") ) teg = "fjolbyli";
								else if( teg.equals("Einbyli") ) teg = "einbyli";
								else if( teg.equals("Haeðir") ) teg = "haedir";
								else teg = "par_radhus";
								val = val.replace("tegund=", "tegund=" + teg);
								//String diffstr = bigdifffield.getText();
								int ferm = Integer.parseInt( edittext1.getText().toString() ); //bigdifffield.getValue(); //Integer.parseInt(diffstr);
								int diff = Integer.parseInt( edittext2.getText().toString() ); //bigfield.getValue(); //Integer.parseInt(bigfield.getText());
								val = val.replace("fermetrar_fra=", "fermetrar_fra=" + (ferm - diff));
								val = val.replace("fermetrar_til=", "fermetrar_til=" + (ferm + diff));

								final String tstr = val;
								
								//texterm.setText( tstr );
								//otherView.
								fastpack.search( tstr );
								myPager.setCurrentItem( 1 );
							}
						};
						wv.setWebViewClient( wvc );
						WebSettings ws = wv.getSettings();
						ws.setJavaScriptEnabled( true );
						
						wv.loadUrl("http://www.mbl.is/fasteignir");
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
	        	
	        	textsmt = (TextView)rootView.findViewById(R.id.textsmt);
	        	textsmt2 = (TextView)rootView.findViewById(R.id.textsmt2);
	        	
	        	Button button = (Button)rootView.findViewById(R.id.deletebutton);
	        	button.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						List<Ibud>	delist = new ArrayList<Ibud>();
						List<LinearLayout>	delayout = new ArrayList<LinearLayout>();
						int i = 0;
						for( Ibud ib : iblist ) {
							if( i < labels.size() ) {
								LinearLayout ll = labels.get( i );
								//LinearLayout ll = (LinearLayout)hlayout.getChildAt( i );
								i++;
	
								Drawable background = ll.getBackground();
								if( background != null && ((ColorDrawable)background).getColor() == fcol ) {
									delist.add( ib );
									delayout.add( ll  );
								}
							} else break;
						}
						
						for( LinearLayout del : delayout ) {
							LinearLayout par = (LinearLayout)del.getParent();
							par.removeView( del );
						}
						iblist.removeAll( delist );
						labels.removeAll( delayout );
						
						updateAverage();
					}
	        	});
	        	Button delbutton = (Button)rootView.findViewById(R.id.deleteallbutton);
	        	delbutton.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						List<Ibud>	delist = new ArrayList<Ibud>();
						List<LinearLayout>	delayout = new ArrayList<LinearLayout>();
						int i = 0;
						for( Ibud ib : iblist ) {
							LinearLayout ll = labels.get( i );
							//LinearLayout ll = (LinearLayout)hlayout.getChildAt( i );
							i++;

							//Drawable background = ll.getBackground();
							//if( background != null && ((ColorDrawable)background).getColor() == fcol ) {
							delist.add( ib );
							delayout.add( ll  );
						}
						
						for( LinearLayout del : delayout ) {
							LinearLayout par = (LinearLayout)del.getParent();
							par.removeView( del );
						}
						iblist.removeAll( delist );
						labels.removeAll( delayout );
						
						updateAverage();
					}
	        	});
	        	
	        	final Spinner sortspinner = (Spinner)rootView.findViewById(R.id.sortspinner);
	        	ArrayAdapter<CharSequence> sortadapter = ArrayAdapter.createFromResource(container.getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
	        	sortadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	
	        	sortspinner.setOnItemSelectedListener( new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						fastpack.comp = arg2;
						Collections.sort( iblist );
						
						int i = 0;
						for( Ibud ib : iblist ) {
							LinearLayout ll = labels.get( i );
							//LinearLayout ll = (LinearLayout)hlayout.getChildAt( i );
							i++;
							
							ImageView iv = (ImageView)ll.getChildAt(0);
							Drawable drawable = ib.getImage();
							if( drawable != null ) iv.setImageDrawable( drawable );
							
							LinearLayout hl = (LinearLayout)ll.getChildAt(1);
							TextView tv = (TextView)hl.getChildAt(0);
							tv.setText( Html.fromHtml( "<b>"+ib.getNafn()+"</b>" ) );
							
							TextView tv2 = (TextView)hl.getChildAt(1);
							tv2.setText( "Fermetrar: " + ib.getFermetrar() + " / Herbergi: " + ib.getHerbergi() );
							
							//labels.add( ll );
							updateAverage();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
	        	});
	        	
	        	sortspinner.setAdapter(sortadapter);
	        }
	        //((TextView) rootView.findViewById(android.R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
	        return rootView;
	    }
	};
}
