package org.simmi;


class Erm implements Comparable<Erm> {
	double d;
	char c;

	Erm(double d, char c) {
		this.d = d;
		this.c = c;
	}

	@Override
	public int compareTo(Erm o) {
		double mis = d - o.d;

		return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
	}
};