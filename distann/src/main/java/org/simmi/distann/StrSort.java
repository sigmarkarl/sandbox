package org.simmi.distann;


class StrSort implements Comparable<StrSort> {
	double d;
	String s;

	StrSort(double d, String s) {
		this.d = d;
		this.s = s;
	}

	@Override
	public int compareTo(StrSort o) {
		double mis = o.d - d;

		return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
	}
};