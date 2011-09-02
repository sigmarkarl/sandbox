package org.simmi.shared;

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

	public String getResult() {
		return result;
	}

	String user;
	String name;
	String type;
	String path;
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
