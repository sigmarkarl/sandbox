package org.simmi.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable
public class EinkunnPersistent implements IsSerializable {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key 	key;
	
	@Persistent
	private User	grader;
	
	@Persistent
	private String	author;
	
	@Persistent
	private	String	book;
	
	@Persistent
	private int		grade;
	
	@Persistent
	private String	comment;
	
	@Persistent
	private Date	date;
	
	public EinkunnPersistent( User grader, String author, String book, int grade, Date date ) {
		this.grader = grader;
		this.author = author;
		this.book = book;
		this.grade = grade;
		this.date = date;
	}
	
	public User getGrader() {
		return grader;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getBook() {
		return book;
	}
	
	public void setGrade( int grade ) {
		this.grade = grade;
	}
	
	public void setComment( String comment ) {
		this.comment = comment;
	}
};
