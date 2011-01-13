package com.dropbox.client;

import java.io.File;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

/**
 * Unit test for simple App.
 */
public class TrustedDropboxClientTest extends TestCase {
    public static Map config = null;
    public static Authenticator auth = null;

    DropboxClient client = null;

    static {
        try {
            config = TrustedAuthenticator.loadConfig("/home/sigmar/dropbox-client-java/config/trusted_testing.json");
            TrustedAuthenticator trusted_auth = new TrustedAuthenticator(config);
            String username = (String)config.get("testing_user");
            assert username != null : "You failed to set the testing_user for trusted access.";
            String password = (String)config.get("testing_password");
            assert password != null : "You failed to set the testing_password for trusted access.";
            trusted_auth.retrieveTrustedAccessToken(username, password);     
            auth = trusted_auth;
            /*String access_key = auth.getTokenKey();
            String access_secret = auth.getTokenSecret();
            config.put("access_token_key", access_key );
            config.put("access_token_value", access_secret );*/
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Total failure trying to access the trusted authenticator." + e;
        }
    }


    public void setUp() throws Exception 
    {
        assert auth != null : "Auth didn't get configured.";
        this.client = new DropboxClient(TrustedDropboxClientTest.config, TrustedDropboxClientTest.auth);
        this.client.fileDelete("dropbox", "/trusted_tests", null);
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TrustedDropboxClientTest( String testName ) {
    	super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( TrustedDropboxClientTest.class );
    }

    public void test_request() throws Exception {
        Object req = client.request("GET", "http", client.api_host, client.port, "/account/info", 0, null, client.auth);
        assert req != null : "Should not get a null from request.";
    }
    
    public void test_fileCreateFolder() throws Exception
    {
        Map resp = client.fileCreateFolder("dropbox", "/trusted_tests", null);
        assertValidResponse(resp, true);

        resp = client.fileCreateFolder("dropbox", "/trusted_tests/tempthing", null);
        assertValidResponse(resp, true);
    }


    public void test_fileDelete() throws Exception
    {
        test_fileCreateFolder();
        Map resp = client.fileDelete("dropbox", "/trusted_tests/tempthing", null);
        assertValidResponse(resp, false);
    }


    public void test_fileCopy() throws Exception
    {
        test_fileCreateFolder();
        Map resp = client.fileCopy("dropbox", "/trusted_tests/tempthing", "/trusted_tests/copiedthing", null);
        assertValidResponse(resp, true);

        resp = client.fileDelete("dropbox", "/trusted_tests/copiedthing", null);
        assertValidResponse(resp, false);
    }

    public void test_fileCopyToRoot() throws Exception
    {
        test_fileCopy();

        Map resp = client.fileCopy("dropbox", "/trusted_tests/tempthing", "/tempthing", null);
        assertValidResponse(resp, true);

        resp = client.fileDelete("dropbox", "/tempthing", null);
        assertValidResponse(resp, false);
    }
    
    public void test_fileMove() throws Exception
    {
        test_fileCreateFolder();
        client.fileDelete("dropbox", "/trusted_tests/movedthing", null);  // ignore the response

        Map resp = client.fileMove("dropbox", "/trusted_tests/tempthing", "/trusted_tests/movedthing", null);
        assertValidResponse(resp, true);

        resp = client.fileDelete("dropbox", "/trusted_tests/movedthing", null);
        assertValidResponse(resp, false);
    }

    public void test_fileMoveToRoot() throws Exception
    {
        // Map resp = client.fileCopy("sandbox", "/fail/toobigtoo.jpg", "/toobigtofail.jpg", null);
        // assertInvalidResponse(resp, 404);

        // resp = client.fileDelete("dropbox", "/toobigtofail.jpg", null);
        // assertInvalidResponse(resp, 404);
        client.fileDelete("dropbox", "/movedthing", null); // ignore response

        Map resp = client.fileCreateFolder("dropbox", "/trusted_tests", null);
        assertValidResponse(resp, true);
        resp = client.fileCreateFolder("dropbox", "/trusted_tests/tempthing", null);
        assertValidResponse(resp, true);

        resp = client.fileMove("dropbox", "/trusted_tests/tempthing", "/movedthing", null);
        assertValidResponse(resp, true);

        resp = client.fileMove("dropbox", "/nodir/shouldfail", "/didntfail", null);
        assertInvalidResponse(resp, 404);

        resp = client.fileDelete("dropbox", "/movedthing", null);
        assertValidResponse(resp, false);
    }

    
    public void test_links() throws Exception {
        String url = client.links("dropbox", "/");
        assert url.equals("http://" + (String)config.get("server") + "/0/links/dropbox/") : "Not equal: " + url;
    }

    public void test_metadata() throws Exception {
        Map res = client.metadata("dropbox", "/", 100, null, false, false, null);
        assertValidResponse(res, true);
    }

    public void test_getFile() throws Exception {
        HttpResponse resp = client.getFile("dropbox", "/idont_exist.txt");
        assert resp != null : "Should get a valid response.";
        int status = resp.getStatusLine().getStatusCode();
        assert status == 404 : "Should get a 404: " + status;

        test_putFile();

        resp = client.getFile("dropbox", "/trusted_put/MIT-LICENSE.txt");
        assert resp != null : "Should get a valid response.";
        status = resp.getStatusLine().getStatusCode();
        assert status == 200 : "Should get valid status code:" + status;
    }

    public void test_putFile() throws Exception {
        Map trusted = client.fileCreateFolder("dropbox", "/trusted_put", null);
        assertValidResponse(trusted, true);

        File sample = new File("/home/sigmar/sl.sl");
        HttpResponse resp = client.putFile("dropbox", "/trusted_put", sample);

        assert resp != null : "Didn't get a valid response.";
        int status = resp.getStatusLine().getStatusCode();
        System.err.println( status );
        assert status == 200 : "Should get valid status code:" + status;
    }

    public void assertValidResponse(Map resp, boolean json) {
        assert resp != null : "Should always get a response.";
        assert resp.get("ERROR") == null : "Should not get an error: " +  resp.get("ERROR") + ":" + resp.get("BODY");

        if(json) {
            assert resp.get("RESULT") == null : "Should not get a raw result: " + resp.get("RESULT");
        }
    }

    public void assertInvalidResponse(Map resp, int code)
    {
        assert resp != null;
        assert resp.get("ERROR") != null : "SHOULD get an error:" + resp.get("BODY");

        StatusLine status = (StatusLine)resp.get("ERROR");
        assert status.getStatusCode() == code : "Should get a " + code + " got: " + status.getStatusCode();
    }
    
    public static void main( String[] args ) {
    	try {
			TrustedDropboxClientTest dbc = new TrustedDropboxClientTest("test_request");
			dbc.setUp();
			dbc.test_request();
			
			/*dbc = new TrustedDropboxClientTest("test_links");
			dbc.setUp();
			dbc.test_links();
			
			dbc = new TrustedDropboxClientTest("test_metadata");
			dbc.setUp();
			dbc.test_metadata();
			
			dbc = new TrustedDropboxClientTest("test_getFile");
			dbc.setUp();
			dbc.test_getFile();*/
			
			dbc = new TrustedDropboxClientTest("test_putFile");
			dbc.setUp();
			dbc.test_putFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}


	