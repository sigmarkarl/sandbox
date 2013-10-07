package org.simmi.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.identity.ObjectIdentity;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Grade extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        //BlobKey blobKey = blobs.get("myFile");
    	//String query = req.getQueryString();
        
        UserService 	userService = UserServiceFactory.getUserService();
        User 			gradeuser = userService.getCurrentUser();
        //userService.

        String grade = req.getParameter("grade");
        //String grade = (String)req.getAttribute("grade");
        String book = req.getParameter("book");
        String user = req.getParameter("user");
        Date date = new Date();
        
        PersistenceManager pm = PMF.get().getPersistenceManager();
        //KeyFactory.
        //pm.getObjectById(GreetingImpl.class, "109");
        
	    String query = "select from " + GreetingImpl.class.getName();
	    List<GreetingImpl> greets = (List<GreetingImpl>) pm.newQuery(query).execute();
	    
        //Key key = KeyFactory.stringToKey( book );
	    
	    GreetingImpl greet = null;
	    for( GreetingImpl gi : greets ) {
	    	if( KeyFactory.keyToString(gi.getKey()).equals(book) ) {
	    		greet = gi;
	    		break;
	    	}
	    }
        
	    if( greet != null ) {
	    	EinkunnPersistent eink = null;
	    	Collection<String> gradecoll = greet.getGrades();
	    	if( gradecoll != null && gradecoll.size() > 0 ) {
	    		List<ObjectIdentity>	gc = new ArrayList<ObjectIdentity>();
	    		for( String val : gradecoll ) {
	    			gc.add( new ObjectIdentity(EinkunnPersistent.class, KeyFactory.stringToKey(val)) );
	    		}
	    		Collection<EinkunnPersistent> eps = (Collection<EinkunnPersistent>)pm.getObjectsById( gc );
	    		//EinkunnPersistent eink = pm.getObjectById(EinkunnPersistent.class, 259);
		    
			    for( EinkunnPersistent ep : eps ) {
			    	if( ep.getGrader().equals(gradeuser) ) {
			    		eink = ep;
			    		break;
			    	}
			    }
	    	}
		    
	    	boolean insert = false;
		    pm.currentTransaction().begin();
		    if( eink == null ) {
		    	insert = true;
		    	eink = new EinkunnPersistent( gradeuser, user, KeyFactory.keyToString(greet.getKey()), Integer.parseInt(grade), date );
		    	try {
		            pm.makePersistent( eink );
		        } catch( Exception e ) {
		            e.printStackTrace();
		        }
		    } else {
		    	eink.setGrade( Integer.parseInt(grade) );
		    }
		    pm.currentTransaction().commit();
		    
		    if( insert ) {
		    	ObjectIdentity erm = (ObjectIdentity)pm.getObjectId( eink );
		    	greet.addGrade( KeyFactory.keyToString((Key)erm.getKey()) );
		    }
		    
		    pm.close();
	    }

        //if (blobKey == null) {
        //res.sendRedirect("/Smasaga.html?k=2");
        //} else {
        //    res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        //}
    }
}
