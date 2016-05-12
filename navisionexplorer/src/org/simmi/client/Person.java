package org.simmi.client;

public class Person {
	String 	name;
	String	kt;
	
	public Person( String name, String kt ) {
		this.name = name;
		this.kt = kt;
	}
	
	public String getKt() {
		return kt;
	}
	
	public String getName() {
		return name;
	}
}