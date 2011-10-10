package org.simmi;

class Sequences {
	public Sequences( String user, String name, String type, String path, int nseq ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.path = path;
		this.nseq = nseq;
	}
	
	public void setKey( String key ) {
		_key = key;
	}
	
	public String getKey() {
		return _key;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getNSeq() {
		return nseq;
	}
	
	String user;
	String name;
	String type;
	String path;
	Integer nseq;
	String _key;
};
