package org.simmi.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Greeting implements IsSerializable {
	public Greeting() {
		
	}
	
	public Greeting( String key, String id, String title, String description, String date, String author, String userid, String filename, String filetype, int filesize, int numgrade ) {
		this.key = key;
		this.id = id;
		this.title = title;
		this.description = description;
		this.date = date;
		this.author = author;
		this.userid = userid;
		this.filename = filename;
		this.filetype = filetype;
		this.filesize = filesize;
		this.numgrade = numgrade;
	}
	
	String 	key;
	String 	title;
	String	description;
	String 	date;
	String 	author;
	String 	userid;
	String 	filename;
	String 	filetype;
	String 	id;
	int 	filesize;
	int		numgrade;
	
	int		r;
	
	public String getKey() {
		return key;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getUser() {
		return userid;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getFiletype() {
		return filetype;
	}
	
	public int getFilesize() {
		return filesize;
	}
}
