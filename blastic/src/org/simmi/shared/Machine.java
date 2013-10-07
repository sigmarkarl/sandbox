package org.simmi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Machine implements IsSerializable {
	public String getName() {
		return name;
	}
	
	public int getProcs() {
		return nproc;
	}
	
	public int getInuse() {
		return inuse;
	}
	
	public void setProcs( int procs ) {
		this.nproc = procs;
	}

	String name;
	String key;
	int nproc;
	int inuse;
	boolean	on;
	
	public Machine() {
		
	}
	
	public Machine( String name, int proc, boolean on ) {
		this.name = name;
		this.nproc = proc;
		this.on = on;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
	
	public boolean getOn() {
		return on;
	}
	
	public void setOn( boolean on ) {
		this.on = on;
	}
};
