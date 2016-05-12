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
	
	public String getStart() {
		return startDate;
	}
	
	public String getStop() {
		return endDate;
	}

	String user;
	String name;
	String type;
	String path;
	String machine;
	String	startDate;
	String	endDate;
	String result;
	String key;
	
	public Blast() {
		
	}
	
	public Blast( String user, String name, String type, String path, String machine, String start, String stop, String result ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.path = path;
		this.machine = machine;
		this.startDate = start;
		this.endDate = stop;
		this.result = result;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
};
