package org.simmi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Database implements IsSerializable {
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

	String user;
	String name;
	String type;
	String path;
	String machine;
	String result;
	String key;
	
	public Database() {
		
	}
	
	public Database( String user, String name, String type, String path, String machine, String result ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.result = result;
		this.path = path;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
};
