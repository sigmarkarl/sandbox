package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jnlp.BasicService;
import javax.jnlp.ExtendedService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;

public class FileUtil {
	public static FileContents doPersist() throws Exception {
		 PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
		 BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
		 
		 URL codebase = bs.getCodeBase();
		 String[] muffins = ps.getNames( codebase );
		 
		 return null;
		 
		 /*if( muffins.length == 0 ) {
			 ps.create( new URL( codebase.toString()+"list"), 100L);
		 } else return ps.get( muffins[0] );*/
	}
	
	public static void doThing( RecipePanel rp ) throws Exception {
		PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
		BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
		 
		URL codebase = bs.getCodeBase();
		String[] muffins = ps.getNames( codebase );
    	
		for( String muff : muffins ) {
			FileContents fc = ps.get( new URL( codebase.toString() + muff ) );
			rp.insertRecipe( new InputStreamReader(fc.getInputStream()) );
		}
	}
	
	public static void doThingOld( RecipePanel rp ) throws Exception {		
		ExtendedService es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
    	File f = new File( "~/.isgem/recipes/list" );
    	FileContents fc = es.openFile( f );
    	
    	if( fc.canRead() ) {
	    	InputStream is = fc.getInputStream();
	    	BufferedReader br = new BufferedReader( new InputStreamReader(is) );
	    	
	    	String str = br.readLine();
	    	while( str != null ) {
	    		f = new File( "~/.isgem/recipes/"+str );
	    		fc = es.openFile( f );
	    		rp.insertRecipe( new InputStreamReader(fc.getInputStream()) );
	    		str = br.readLine();
	    	}
    	}
	}
	
	public static void doTheThing( String str, String fname ) throws Exception {
		PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
		BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
		 
		URL url = new URL( bs.getCodeBase().toString()+"recipes_"+fname );
		//String[] muffins = ps.getNames( codebase );
		//URL = new URL( codebase.toString()+fname );
		ps.create( url, 1000 );
		FileContents fc = ps.get( url );
		OutputStream os = fc.getOutputStream( false );
		os.write( str.getBytes() );
		os.close();
		
		/*ExtendedService es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
		File f = new File( "~/.isgem/recipes/"+fname );
		FileContents fc = es.openFile( f );
		
		if( fc.canWrite() ) {
			OutputStream os = fc.getOutputStream( true );
			os.write( str.getBytes() );
			os.close();
		} else {
			System.err.println("can't write");
		}*/
	}
	
	public static void doTheNextThing( String fname ) throws Exception {
		PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
		BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
		 
		String name = "recipes_"+fname;
		Set<String> all = new HashSet<String>( Arrays.asList(ps.getNames( bs.getCodeBase()) ) );
		if( all.contains( name ) ) {
			ps.delete( new URL( bs.getCodeBase().toString()+name ) );
		}
		
		/*ExtendedService es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");	    
	    File f = new File("~/.isgem/recipes/"+fname);
		FileContents fc = es.openFile( f );
		if( fc.canWrite() ) {
			OutputStream os = fc.getOutputStream(true);
			os.write( new byte[0] );
			os.close();
	    }*/
	}
}
