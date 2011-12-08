package org.simmi.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable
public class GreetingImpl implements IsSerializable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private User author;

    @Persistent
    private String title;
    
    @Persistent
    private String description;
    
    @Persistent
    private String content;
    	
    @Persistent
    private Date	date;
    
    @Persistent
    private Collection<String>	grades;
    
    @Persistent
    private String	filename;
    
    @Persistent
    private String	filetype;
    
    @Persistent
    private Integer	filesize;
    
    @Persistent
    private Boolean	draug;
    
    @Persistent
    private Boolean	gaman;
    
    @Persistent
    private Boolean	ungl;
    
    @Persistent
    private Boolean	ast;
    
    @Persistent
    private Boolean	erotik;
    
    @Persistent
    private Boolean	sanns;
    
    @Persistent
    private Boolean	vis;
    
    @Persistent
    private Boolean	bund;
    
    @Persistent
    private Boolean	soguleg;
    
    @Persistent
    private Boolean	und;
    
    @Persistent
    private Boolean	reif;
    
    @Persistent
    private Boolean	barn;
    
    public GreetingImpl(User author, Date date, String title, String filename, String filetype, int filesize, boolean baby, boolean pulp, boolean weird, boolean love, boolean ero, boolean hist, boolean tru, boolean fun, boolean horr ) {
        this.author = author;
        this.title = title;
        this.filename = filename;
        this.filetype = filetype;
        this.filesize = filesize;
        this.date = date;
        
        this.barn = baby;
        this.reif = pulp;
        this.und = weird;
        this.ast = love;
        this.erotik = ero;
        this.soguleg = hist;
        this.sanns = tru;
        this.gaman = fun;
        this.draug = horr;
        
        grades = new ArrayList<String>();
        
        //BlobstoreServiceFactory.getBlobstoreService().
    }
    
    public Collection<String> getGrades() {
    	return grades;
    }
    
    public void setGrades( Collection<String> grades ) {
    	this.grades = grades;
    }

    public void addGrade( String grade ) {
    	grades.add( grade );
    }
    
    public Key getKey() {
        return key;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
    
    public String getFilename() {
    	return filename;
    }
    
    public String getFiletype() {
    	return filetype;
    }
    
    public int getFilesize() {
    	return filesize == null ? -1 : filesize;
    }
    
    public Date getDate() {
    	return date;
    }
    
    public boolean getBarn() {
        return barn;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    
    public String getTitle() {
    	return title;
    }
    
    public void setTitle( String title ) {
    	this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public void setDescription( String description ) {
    	this.description = description;
    }
}