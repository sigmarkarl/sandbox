package org.simmi;

import java.util.Set;

public class Function {
	public Function() {}
	public Function( String go ) { this.go = go; }

	String go;
	String ec;
	String metacyc;
	String kegg;
	String name;
	String namespace;
	String desc;
	Set<String> isa;
	Set<String> subset;
	Set<Gene> geneentries;
	int index;
	
	public String getName() {
		return name;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public String toString() {
		return name;
	}
};