package org.simmi.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.parser.ParseException;

import com.dropbox.client.Authenticator;
import com.dropbox.client.DropboxClient;
import com.dropbox.client.DropboxException;
import com.dropbox.client.TrustedAuthenticator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Upload extends HttpServlet {
    //private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	private static final String MAC_NAME = "HmacSHA1";
	
	public static String getParamstr( Map<String,String> map, List<String> set ) {
		String ret = "";
		
		for( String s : set ) {
			if( ret.length() == 0 ) ret += s + "=\"" + map.get(s) + "\"";
			else ret += ", " + s + "=\"" + map.get(s) + "\"";
		}
		
		return ret;
	}
	
	public static void hey( String fname, byte[] contents ) {
    	try {            
			String urlstr = "https://api.dropbox.com/0/token?email=sigmarkarl@gmail.com&password=skc.311";
			String oauth = "&oauth_consumer_key=jemmmn3c5ot8rdu";
			
			URL url = new URL(urlstr+oauth);
			InputStream is = url.openStream();
			
			byte[] bb = new byte[256];
			int r = is.read( bb );
			String s = "";
			while( r > 0 ) {
				s += new String( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			
			Map<String,String>	json = new HashMap<String,String>();
			String sub = s.substring( s.indexOf('{')+1, s.indexOf('}') );
			String[] split = sub.split(",");
			for( String sp : split ) {
				String[] subspl = sp.split(":");
				json.put( "oauth_"+subspl[0].trim().replace("\"", ""), subspl[1].trim().replace("\"", "") );
			}
			String tokenkey = json.get("oauth_token");
			String tokensecret = json.get("oauth_secret");
            String baseurl = "api-content.getdropbox.com";
			//String baseurl = "localhost:8899";
            String target = "http://"+baseurl+"/0/files/dropbox/Public";
            stuff( target, tokenkey, tokensecret, fname, contents );
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Total failure trying to access the trusted authenticator." + e;
        }
    }
    
    public static void stuff( String target, String tokenkey, String tokensecret, String fname, byte[] contents ) throws IOException {
        Map<String,String>	parameters = new TreeMap<String,String>();
		parameters.put("file", fname);
		parameters.put("oauth_consumer_key", "jemmmn3c5ot8rdu");
		parameters.put("oauth_token", tokenkey );
		parameters.put("oauth_timestamp", Long.toString(System.currentTimeMillis()/1000) );
		parameters.put("oauth_signature_method", "HMAC-SHA1");
		parameters.put("oauth_nonce", Long.toString(Math.abs(new Random().nextLong())) );
		parameters.put("oauth_version", "1.0" ); 
		
		String paramstr = null;
		for( String par : parameters.keySet() ) {
			if( paramstr == null ) paramstr = par + "=" + parameters.get(par);
			else paramstr += "&" + par + "=" + parameters.get(par);
		}
		
		String keyString = OAuth.percentEncode("9or8lsn165d44qv") + '&' + OAuth.percentEncode(tokensecret);
        byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

        String MAC_NAME = "HmacSHA1";
        Base64 base64 = new Base64();
        SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
        Mac mac = null;
		try {
			mac = Mac.getInstance(MAC_NAME);
			mac.init(key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		String str = OAuth.percentEncode(target) + "&" + OAuth.percentEncode(paramstr);
        //return "GET&" + str;
        String sbs = "POST&" + str; //generate( baseurl, paramstr );
        byte[] text = sbs.getBytes(OAuth.ENCODING);
        
        System.err.println(sbs);

        byte[] b = mac.doFinal(text);
        byte[] nb = base64.encode(b);
        String sign = new String(nb).trim();
        parameters.put("oauth_signature", sign );
		
		URL url = new URL(target);
		HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
		
		String dispstr = "Content-Disposition: form-data; name=\"file\"; filename=\""+fname+"\"\n\n";
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoInput( true );
		httpConnection.setDoOutput( true );
		String[] incl = {"file", "oauth_token", "oauth_consumer_key", "oauth_version", "oauth_signature_method", "oauth_timestamp", "oauth_nonce", "oauth_signature"};
		String stuff = getParamstr( parameters, Arrays.asList( incl ) );
		httpConnection.setRequestProperty("Authorization", "OAuth "+stuff);
		httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=46-B_DuxmY9qurDm33PMiHpZ2dP7Lr"); //req.getContentType());
		
		String cont = "--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr\n";
		cont += dispstr;
		//cont += filecontents;
		cont += "\n--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr--\n";
		int clen = cont.length()+contents.length;
		
		httpConnection.setRequestProperty("Content-Length", ""+clen);
		httpConnection.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2" );
		httpConnection.setRequestProperty("Connection", "keep-alive");
		httpConnection.setRequestProperty("Expect", "100-Continue");
		
		OutputStream os = httpConnection.getOutputStream();
		os.write( "--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr\n".getBytes() );
		os.write( dispstr.getBytes() );
		os.write( contents );
		os.write( "\n--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr--\n".getBytes() );
		
		InputStream is = httpConnection.getInputStream();
		byte[] bb = new byte[256];
		int r = is.read( bb );
		String s = "";
		while( r > 0 ) {
			s += new String( bb, 0, r );
			r = is.read( bb );
		}
		is.close();
		os.close();
		
		System.out.println( s );
		System.out.println( httpConnection.getResponseCode() );
		
		httpConnection.disconnect();
    }
	
	public static void erm( HttpServletRequest req ) {
		try {
			Base64 base64 = new Base64();
			String urlstr = "https://api.dropbox.com/0/token?email=sigmarkarl@gmail.com&password=skc.311";
			String oauth = "&oauth_consumer_key=jemmmn3c5ot8rdu";
			
			URL url = new URL(urlstr+oauth);
			InputStream is = url.openStream();
			
			byte[] bb = new byte[256];
			int r = is.read( bb );
			String s = "";
			while( r > 0 ) {
				s += new String( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			
			Map<String,String>	json = new HashMap<String,String>();
			String sub = s.substring( s.indexOf('{')+1, s.indexOf('}') );
			String[] split = sub.split(",");
			for( String sp : split ) {
				String[] subspl = sp.split(":");
				json.put( "oauth_"+subspl[0].trim().replace("\"", ""), subspl[1].trim().replace("\"", "") );
			}
			
			Map<String,String>	parameters = new TreeMap<String,String>();
			parameters.put("oauth_consumer_key", "jemmmn3c5ot8rdu");
			parameters.put("oauth_token", json.get("oauth_token"));
			parameters.put("oauth_timestamp", Long.toString(System.currentTimeMillis()/1000) );
			parameters.put("oauth_signature_method", "HMAC-SHA1");
			parameters.put("oauth_nonce", Integer.toString(Math.abs(new Random().nextInt())) );
			parameters.put("oauth_version", "1.0" ); 
			
			//String baseurl = "http://localhost:3333/0/files/dropbox";
			//String baseurl = "https://api-content.dropbox.com/0/files/dropbox/trusted_put";
			String baseurl = "http://localhost:8899/0/files/dropbox/trusted_put";
			
			String paramstr = null;
			for( String par : parameters.keySet() ) {
				if( paramstr == null ) paramstr = par + "=" + parameters.get(par);
				else paramstr += "&" + par + "=" + parameters.get(par);
			}
			//paramstr = "file=simmi.txt&"+paramstr;
			
			urlstr = baseurl;//+" HTTPþ1.1";
			
			/*urlstr += "?oauth_token="+json.get("oauth_token");
			urlstr += "&oauth_timestamp="+(System.currentTimeMillis()/1000);
			urlstr += "&oauth_signature_method=HMAC-SHA1";
			urlstr += "&oauth_nonce="+Integer.toString(Math.abs(new Random().nextInt()));
			urlstr += oauth;*/
			
			

		            String keyString = OAuth.percentEncode("9or8lsn165d44qv") + '&' + OAuth.percentEncode(json.get("oauth_secret") );
		            byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

		            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
		            Mac mac = null;
					try {
						mac = Mac.getInstance(MAC_NAME);
						mac.init(key);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					}

					String str = OAuth.percentEncode(baseurl) + "&" + OAuth.percentEncode(paramstr);
			        //return "GET&" + str;
		            String sbs = "POST&" + str; //generate( baseurl, paramstr );
		            byte[] text = sbs.getBytes(OAuth.ENCODING);

		            byte[] b = mac.doFinal(text);
		            byte[] nb = base64.encode(b);
		            String sign = new String(nb).trim();
		            parameters.put("oauth_signature", sign );
		            //urlstr += "&oauth_signature="+new String(nb).trim();
			
		    String filecontents = "simmibest";
			System.out.println( sbs );
			url = new URL(urlstr);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoInput( true );
			httpConnection.setDoOutput( true );
			String[] incl = {"oauth_token", "oauth_consumer_key", "oauth_version", "oauth_signature_method", "oauth_timestamp", "oauth_nonce", "oauth_signature"};
			//Set<String>	ist = new HashSet<String>( Arrays.asList(incl) );
			String stuff = getParamstr( parameters, Arrays.asList( incl ) );
			httpConnection.setRequestProperty("Authorization", "OAuth "+stuff);
			httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=46-B_DuxmY9qurDm33PMiHpZ2dP7Lr"); //req.getContentType());
			//httpConnection.setRequestProperty("Content-Disposition", "form-data; name=file; file=\"testfile.txt\"" );
			httpConnection.setRequestProperty("Content-Length", ""+filecontents.length());
			httpConnection.setRequestProperty("Expect", "100-Continue");
			String ss = null; //req.getParameter("myFile");
			String uenc = "file="+URLEncoder.encode( paramstr, "UTF-8" );//"ISO-8859-1" );
			httpConnection.getOutputStream().write( "--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr\n".getBytes() );
			httpConnection.getOutputStream().write( "Content-Disposition: form-data; name=\"file\"; filename=\"simmihoho.txt\"\n\n".getBytes() );
				
			httpConnection.getOutputStream().write( filecontents.getBytes() ); //uenc.getBytes() );
			httpConnection.getOutputStream().write( "\n--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr--\n".getBytes() );
			
			is = httpConnection.getInputStream();
			bb = new byte[256];
			r = is.read( bb );
			s = "";
			while( r > 0 ) {
				s += new String( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			httpConnection.getOutputStream().close();
						
			System.out.println( s );
			System.out.println( httpConnection.getResponseCode() );
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	//req.get
    	//req.getParameter("")

        //Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        //BlobKey blobKey = blobs.get("myFile");
    	
    	//res.setHeader("Access-Control-Allow-Origin", "*");
    	//res.setContentType("text/javascript");
    	//res.setCharacterEncoding("utf-8");

        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        Date date = new Date();
        
        /*String baby = req.getParameter("B");
        String horr = req.getParameter("H");
        String love = req.getParameter("L");
        String pulp = req.getParameter("P");
        String weird = req.getParameter("W");
        String ero = req.getParameter("E");
        String tru = req.getParameter("T");
        String hist = req.getParameter("S");
        String fun = req.getParameter("F");*/
        
        boolean b = false;
        boolean h = false;
        boolean l = false;
        boolean p = false;
        boolean w = false;
        boolean e = false;
        boolean t = false;
        boolean s = false;
        boolean f = false;

        
        /*String fname = req.getParameter("myFile");
        Enumeration en = req.getAttributeNames();
        while( en.hasMoreElements() ) {
        	System.err.println( "attr "+en.nextElement() );
        }
        
        en = req.getParameterNames();
        while( en.hasMoreElements() ) {
        	System.err.println( "par " + en.nextElement() );
        }*/
        
        byte[] contents = null;
        String 	fname = "";
        String	sname = "";
        
        InputStream is = req.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        
        byte[] bb = new byte[256];
        int r = is.read(bb);
        while( r > 0 ) {
        	baos.write(bb, 0, r);
        	r = is.read(bb);
        }
        byte[] allcontent = baos.toByteArray();
        String allstr = new String( allcontent );
        System.err.println( allstr );
        baos.flush();
        baos.reset();
        
        String name = null;
        String type = null;
        String val = null;
        
        //BufferedReader br = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( allcontent ) ) );
        int ll = 0;
        while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
        String stopcode = new String( allcontent, 0, ll ).trim();
        ll++;
        int oldll = ll;
        
        while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
    	String line = new String( allcontent, oldll, ll-oldll ).trim();
    	ll++;
    	oldll = ll;
        while( ll < allcontent.length ) {        	
        	if( line.startsWith("Content-Disposition") ) {
        		if( "content".equals(name) ) {
        			sname = val;
        		} else if( "on".equals(val) ) {
        			b = "B".equals(name);
        			h = "H".equals(name);
        			l = "L".equals(name);
        			p = "P".equals(name);
        			w = "W".equals(name);
        			e = "E".equals(name);
        			t = "T".equals(name);
        			s = "S".equals(name);
        			f = "F".equals(name);
        		}
        		
        		int nindex = line.indexOf("name");
        		int findex = line.indexOf("filename");
        		
        		if( nindex != -1 ) {
        			int startIndex = nindex+6;
        			int endIndex = line.indexOf('"', startIndex);
        			name = line.substring(startIndex, endIndex);
        		}
        		
        		if( findex != -1 ) {
        			int startIndex = findex+10;
        			int endIndex = line.indexOf('"', startIndex);
        			fname = line.substring(startIndex, endIndex);
        			int fn = fname.indexOf('/');
        			if( fn != -1 ) fname = fname.substring(fn+1);
        			fn = fname.indexOf('\\');
        			if( fn != -1 ) fname = fname.substring(fn+1);
        			fn = fname.indexOf("%5C");
        			if( fn != -1 ) fname = fname.substring(fn+3);
        		} else {
        			while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
                	line = new String( allcontent, oldll, ll-oldll ).trim();
                	ll++;
                	oldll = ll;
                	
                	while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
                	val = new String( allcontent, oldll, ll-oldll ).trim();
                	ll++;
                	oldll = ll;
        		}
        	} else if( line.startsWith("Content-Type") ) {
        		type = line.substring( 14 );
        		
        		while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
            	line = new String( allcontent, oldll, ll-oldll ).trim();
            	ll++;
            	oldll = ll;
        		
            	while( ll < allcontent.length ) {
            		if( allcontent[ll] == stopcode.charAt(0) ) {
            			int kk = ll+1;
            			while( kk-ll < stopcode.length() && allcontent[kk] == stopcode.charAt(kk-ll) ) kk++;
            			if( kk-ll >= stopcode.length() ) break;
            		}
            		
            		baos.write( allcontent[ll] );
            		ll++;
            	}
            	ll++;
            	oldll = ll;
        	}
        	
        	while( ll < allcontent.length && allcontent[ll] != '\n' ) ll++;
        	line = new String( allcontent, oldll, ll-oldll ).trim();
        	ll++;
        	oldll = ll;
        }
        
        /*boolean b = baby == null ? false : baby.equalsIgnoreCase("on");
        boolean h = horr == null ? false : horr.equalsIgnoreCase("on");
        boolean l = love == null ? false : love.equalsIgnoreCase("on");
        boolean p = pulp == null ? false : pulp.equalsIgnoreCase("on");
        boolean w = weird == null ? false : weird.equalsIgnoreCase("on");
        boolean e = ero == null ? false : ero.equalsIgnoreCase("on");
        boolean t = tru == null ? false : tru.equalsIgnoreCase("on");
        boolean s = hist == null ? false : hist.equalsIgnoreCase("on");
        boolean f = fun == null ? false : fun.equalsIgnoreCase("on");*/
        
        byte[] barr = baos.toByteArray();
        contents = barr;//Arrays.copyOfRange( barr, 0, Math.max(0, barr.length-(stopcode.length()+2) ) );
        
        hey( sname+"_"+fname, contents );
        //erm( req );

		/*try {
			ServletFileUpload upload = new ServletFileUpload();
	        FileItemIterator iter = upload.getItemIterator( req );
	        
			Map<String,String>	config = TrustedAuthenticator.loadConfig( Upload.class.getResourceAsStream("/trusted_testing.json") ); //"/home/sigmar/dropbox-client-java/config/trusted_testing.json");
			TrustedAuthenticator trusted_auth = new TrustedAuthenticator(config);
	        String username = (String)config.get("testing_user");
	        assert username != null : "You failed to set the testing_user for trusted access.";
	        String password = (String)config.get("testing_password");
	        assert password != null : "You failed to set the testing_password for trusted access.";
	        trusted_auth.retrieveTrustedAccessToken(username, password);     
	        Authenticator auth = trusted_auth;
	        
	        assert auth != null : "Auth didn't get configured.";
	        DropboxClient client = new DropboxClient(config, auth);
	        
	        //DropboxClient client = new DropboxClient(config, auth);
	        
	        while( iter.hasNext() ) {
	        	String filename = iter.next().getName();
	        	client.putFile("dropbox", "/smasogur", new File( filename ) );
	        }
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthCommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthMessageSignerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthExpectationFailedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DropboxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		try {
				Map<String, String> config = Authenticator.loadConfig( Upload.class.getResourceAsStream("/org/simmi/simmi.json") );
			 	Authenticator auth = new Authenticator(config);
		        String url = auth.retrieveRequestToken(null);
		        System.out.println("Url is: " + url);
		        Util.authorizeForm(url, (String)config.get("testing_user"), (String)config.get("testing_password"));
		        auth.retrieveAccessToken("");
		        
		        DropboxClient client = new DropboxClient(config, auth);
		        client.putFile("smasogur", "/", new File("/home/sigmar/sl.sl") );
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (OAuthCommunicationException e1) {
			e1.printStackTrace();
		} catch (OAuthException e1) {
			e1.printStackTrace();
		} catch (DropboxException e1) {
			e1.printStackTrace();
		}*/
        
        GreetingImpl greeting = new GreetingImpl(user, date, sname, sname+"_"+fname, type, contents.length, b, p, w, l, e, s, t, f, h);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(greeting);
        } finally {
            pm.close();
        }

        //if (blobKey == null) {
        //res.sendRedirect("/Smasaga.html?k=2");
        //} else {
        //    res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        //}
    }
    
    public static void main( String[] args ) {
    	//System.err.println( System.currentTimeMillis() );
    	//erm( null );
    	hey( "newfile.txt", new byte[] {'s', 'i'} );
    }
    
    public static void ermi( String[] args ) {
    	try {
    		Map config = null;
    	    Authenticator auth = null;

    		config = TrustedAuthenticator.loadConfig("/home/sigmar/dropbox-client-java/config/trusted_testing.json");
            TrustedAuthenticator trusted_auth = new TrustedAuthenticator(config);
            String username = (String)config.get("testing_user");
            assert username != null : "You failed to set the testing_user for trusted access.";
            String password = (String)config.get("testing_password");
            assert password != null : "You failed to set the testing_password for trusted access.";
            trusted_auth.retrieveTrustedAccessToken(username, password);     
            auth = trusted_auth;
            
            assert auth != null : "Auth didn't get configured.";
            DropboxClient client = new DropboxClient(config, auth);
            client.fileDelete("dropbox", "/trusted_tests", null);
	        
	        //DropboxClient client = new DropboxClient(config, auth);
	        client.putFile("dropbox", "/smasogur", new File("/home/sigmar/sl.sl") );
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (OAuthCommunicationException e1) {
			e1.printStackTrace();
		} catch (OAuthException e1) {
			e1.printStackTrace();
		} catch (DropboxException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
