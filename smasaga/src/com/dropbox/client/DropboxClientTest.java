/*
 * Copyright (c) 2009 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
*/

package com.dropbox.client;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

/**
 * Unit test for simple App.
 */
public class DropboxClientTest extends TestCase {
    public static Map config = null;
    public static Authenticator auth = null;
    DropboxClient client = null;

    static {
        try {
            config = Authenticator.loadConfig("/home/sigmar/dropbox-client-java/config/trusted_testing.json");
            auth = new Authenticator(config);
            String url = auth.retrieveRequestToken(null);
	    System.out.println("Url is: " + url);
            Util.authorizeForm(url, (String)config.get("testing_user"), (String)config.get("testing_password"));
            auth.retrieveAccessToken("");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Total failure initializing the authenticator.";

        }
    }


    public void setUp() throws Exception 
    {
        assert auth != null : "Auth didn't get configured.";
        this.client = new DropboxClient(this.config, DropboxClientTest.auth);
        this.client.fileDelete("sandbox", "/tests", null);
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DropboxClientTest( String testName )
    {
        super( testName );
    }
    
    public static void main( String[] args ) {
    	new DropboxClientTest("urlencode");
    }

    /**
     * @return the suite of tests being tested
     */
    /*public static Test suite()
    {
        return new TestSuite( DropboxClientTest.class );
    }*/

    public void test_urlencode() throws Exception {
        String encoded = DropboxClient.urlencode(new Object[] {
                "one", "two",
                "three", "four"});
        assert encoded.equals("one=two&three=four") : "Encoded wrong: " + encoded;
    }

    public void test_request() throws Exception {
        Object req = client.request("GET", "http", client.api_host, client.port, "/account/info", 0, null, client.auth);
        assert req != null : "Should not get a null from request.";
    }


    public void test_buildFullURL() throws Exception
    {
        String url = client.buildFullURL("http", (String)config.get("server"), ((Long)config.get("port")).intValue(), "/0/account/info");
        assert url.equals("http://" + (String)config.get("server") + "/0/account/info");
    }

    public void test_buildURL() throws Exception
    {
        String url = client.buildURL("/account/info", 0, new Object[] {});
        assert url.equals("/0/account/info");

        url = client.buildURL("/account/info", 0, null);
        assert url.equals("/0/account/info");

        url = client.buildURL("/account/info", 0, new Object[] {"one", "two"});
        assert url.equals("/0/account/info?one=two");
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

    public void test_accountInfo() throws Exception 
    {
        Map resp = client.accountInfo(false, "");
        assertValidResponse(resp, true);

        resp = client.accountInfo(true, "testing");
        assert resp.get("RESULT") != null;
    }

    public void test_fileCreateFolder() throws Exception
    {
        Map resp = client.fileCreateFolder("sandbox", "/tests", null);
        assertValidResponse(resp, true);

        resp = client.fileCreateFolder("sandbox", "/tests/tempthing", null);
        assertValidResponse(resp, true);

        resp = client.fileCreateFolder("dropbox", "/tests/noway", null);
        assertInvalidResponse(resp, 401);
    }


    public void test_fileDelete() throws Exception
    {
        test_fileCreateFolder();
        Map resp = client.fileDelete("sandbox", "/tests/tempthing", null);
        assertValidResponse(resp, false);

        resp = client.fileDelete("dropbox", "/tests/tempthing", null);
        assertInvalidResponse(resp, 401);
    }


    public void test_fileCopy() throws Exception
    {
        test_fileCreateFolder();
        Map resp = client.fileCopy("sandbox", "/tests/tempthing", "/tests/copiedthing", null);
        assertValidResponse(resp, true);

        resp = client.fileDelete("sandbox", "/tests/copiedthing", null);
        assertValidResponse(resp, false);
    }

    public void test_fileMove() throws Exception
    {
        test_fileCreateFolder();
        // BUG: These should pass but the current dropbox service doesn't return an error.
        // Map resp = client.fileDelete("dropbox", "/toobigtofail.jpg", null);
        // assertInvalidResponse(resp, 404);

        // resp = client.fileCopy("sandbox", "/fail/toobigtoo.jpg", "/toobigtofail.jpg", null);

        client.fileDelete("sandbox", "/tests/movedthing", null);  // ignore the response

        Map resp = client.fileMove("sandbox", "/tests/tempthing", "/tests/movedthing", null);
        assertValidResponse(resp, true);

        resp = client.fileMove("sandbox", "/tests/shouldfail", "/tests/didntfail", null);
        assertInvalidResponse(resp, 404);

        resp = client.fileDelete("sandbox", "/tests/movedthing", null);
        assertValidResponse(resp, false);
    }

    public void test_links() throws Exception {
        String url = client.links("sandbox", "/");
        assert url.equals("http://" + (String)config.get("server") + "/0/links/sandbox/") : "Not equal: " + url;
    }

    public void test_metadata() throws Exception {
        Map res = client.metadata("sandbox", "/", 100, null, false, false, null);
        assertValidResponse(res, true);
    }

    public void test_getFile() throws Exception {
        test_fileCreateFolder();

        HttpResponse resp = client.getFile("sandbox", "/idont_exist.txt");
        assert resp != null : "Should get a valid response.";
        int status = resp.getStatusLine().getStatusCode();
        assert status == 404 : "Should get a 404: " + status;

        test_putFile();
        resp = client.getFile("sandbox", "/tests/DropboxClientTest.java");
        assert resp != null : "Should get a valid response.";
        status = resp.getStatusLine().getStatusCode();
        assert status == 200 : "Should get valid status code:" + status;
        resp.getEntity().consumeContent();

        Map r = client.fileCopy("sandbox", "/tests/DropboxClientTest.java", "/tests/copied.java", null);
        assertValidResponse(r, true);

        r = client.fileMove("sandbox", "/tests/copied.java", "/tests/moved.java", null);
        assertValidResponse(r, true);

        r = client.fileDelete("sandbox", "/tests/DropboxClientTest.java", null);
        assertValidResponse(r, false);
    }

    public void test_putFile() throws Exception {
        test_fileCreateFolder();
        File sample = new File("src/test/java/com/dropbox/client/DropboxClientTest.java");
        HttpResponse resp = client.putFile("sandbox", "/tests/", sample);
        assert resp != null : "Didn't get a valid response.";
        int status = resp.getStatusLine().getStatusCode();
        assert status == 200 : "Should get valid status code:" + status;
    }

}


