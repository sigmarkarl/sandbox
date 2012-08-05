<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.gwt.user.server.Base64Utils" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>

<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

	<meta property="fb:app_id" content="390867450957358" />
	<meta property="og:title" content="SuggestADate" />
	<meta property="og:type" content="Entertainment" />
	<meta id="metaurl" property="og:url" content="https://apps.facebook.com/suggestdate/" />
	<!--meta property="og:image" content="" /-->
	<meta property="og:site_name" content="Suggest a date" />
	
	<%
	String str = request.getParameter("signed_request");
    if( str != null ) {
    	int k = str.indexOf(".");
		byte[] bb = null;  
    	try {
    		//bb = Base64Utils.fromBase64( str.substring(k+1) );
    		bb = Base64.decodeBase64( str.substring(k+1) );
    	} catch( Exception e ) {
    		e.printStackTrace();
    	}
    	if( bb != null ) {
    		int i;
    		for( i = 0; i < bb.length; i++ ) {
    			if( bb[i] == 0 ) break;
    		}
    		if( i == bb.length ) i = 0;
	    	String val = new String( bb, i+1, bb.length-i-1 );
	    	i = val.indexOf("user_id");
	    	if( i != -1 ) {
	    		int n = i+10;
	    		int m = val.indexOf("\"", n);
	    		if( m > n ) {%>
	    			<meta property="fbuid" content="<%=val.substring(n,m)%>" /><%
	    		}
	    	}
	    }
	}
    %>
    
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="Suggestadate.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Suggest a date</title>
    
    <%if( str == null ) {%>
    <script type="text/javascript">
		var _gaq = _gaq || [];
		_gaq.push(['_setAccount', 'UA-31639031-1']);
		_gaq.push(['_trackPageview']);

		(function() {
		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	})();
	</script>
	<%}%>
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="suggestadate/suggestadate.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>
	<!-- if( str == null ) { -->
    <div id="ads" style="text-align: center">
    	<script type="text/javascript"><!--
		google_ad_client = "ca-pub-7204381538404733";
		/* Suggestadate */
		google_ad_slot = "6264606283";
		google_ad_width = 728;
		google_ad_height = 90;
		//-->
		</script>
		<script type="text/javascript" src="//pagead2.googlesyndication.com/pagead/show_ads.js">
		</script>
	</div>
	<!-- } -->
    
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
    
    <div id="fb-root"></div>
  </body>
</html>
