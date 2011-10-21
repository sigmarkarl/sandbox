package org.simmi.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Blast implements IsSerializable {
	public String getUser() {
		return user;
	}

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
	
	public String getMachine() {
		return machine;
	}

	public String getResult() {
		return result;
	}
	
	public Date getStart() {
		return startDate;
	}
	
	public Date getStop() {
		return endDate;
	}

	String user;
	String name;
	String type;
	String path;
	String machine;
	Date	startDate;
	Date	endDate;
	String result;
	String key;
	
	public Blast() {
		
	}
	
	public Blast( String user, String name, String type, String path, String result ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.path = path;
		this.result = result;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
};
