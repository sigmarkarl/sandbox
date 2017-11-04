package org.simmi.distann;

import java.util.stream.IntStream;

public final class Aas implements Comparable<Aas> {
	String name;
	final StringBuilder aas;
	int start;
	int stop;
	int dir;

	public Aas(String name, StringBuilder aas, int start, int stop, int dir) {
		this.name = name;
		this.aas = aas;
		this.start = start;
		this.stop = stop;
		this.dir = dir;
		
		IntStream.builder().add(10);
	}

	@Override
	public final int compareTo(Aas o) {
		return name.compareTo(o.name);
	}

	public final String toString() {
		return name;
	}

	// public byte[] get( String name ) {
};