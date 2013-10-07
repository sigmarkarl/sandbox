package org.simmi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Chunk implements IsSerializable {
	String country;
	boolean	valid;
	
	public Chunk( String country, boolean valid ) {
		this.country = country;
		this.valid = valid;
	}
	
	public String getCountry() {
		return country;
	}
	
	public boolean isValid() {
		return valid;
	}
}
