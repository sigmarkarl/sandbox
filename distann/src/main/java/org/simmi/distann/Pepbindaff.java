package org.simmi.distann;


public class Pepbindaff implements Comparable<Pepbindaff> {
	String pep;
	double aff;

	public Pepbindaff(String pep, String aff) {
		this.pep = pep;
		this.aff = Double.parseDouble(aff);
	}

	public Pepbindaff(String pep, double aff) {
		this.pep = pep;
		this.aff = aff;
	}

	@Override
	public int compareTo(Pepbindaff o) {
		return aff > o.aff ? 1 : (aff < o.aff ? -1 : 0);
	}
}