package org.simmi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class DbSave {
	//private static final String MAC_NAME = "HmacSHA1";

	public static String getParamstr(Map<String, String> map, List<String> set) {
		String ret = "";

		for (String s : set) {
			if (ret.length() == 0)
				ret += s + "=\"" + map.get(s) + "\"";
			else
				ret += ", " + s + "=\"" + map.get(s) + "\"";
		}

		return ret;
	}
	
	public static void save( String fname, byte[] contents ) {
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
		
		/*String keyString = OAuth.percentEncode("9or8lsn165d44qv") + '&' + OAuth.percentEncode(tokensecret);
        byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

        String MAC_NAME = "HmacSHA1";
        //Base64 base64 = new Base64();
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
        //Base64.
        //byte[] nb = base64.encode(b);
        //String sign = new String(nb).trim();
        String sign = Base64.encode(b);
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
		httpConnection.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, ; q=.2" );
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
		
		httpConnection.disconnect();*/
    }
}
