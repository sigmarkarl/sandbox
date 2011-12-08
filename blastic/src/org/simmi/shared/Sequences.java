package org.simmi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Sequences implements IsSerializable {
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

	public long getNum() {
		return num;
	}

	String user;
	String name;
	String type;
	String path;
	long   num;
	String key;
	
	public Sequences() {
		
	}
	
	public Sequences( String user, String name, String type, String path, long num ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.path = path;
		this.num = num;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
};
