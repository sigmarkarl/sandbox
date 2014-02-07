package org.simmi;

import java.util.HashSet;
import java.util.Set;

public class Teginfo implements Teg {
	String tegund;
	Set<Tegeval> tset;
	Tegeval best;

	public void add(Tegeval tv) {
		if (tset == null)
			tset = new HashSet<Tegeval>();
		tset.add(tv);
		if (best == null || tv.eval < best.eval)
			best = tv;
	}

	public String toString() {
		String ret = best.toString();
		String design = best.getGene().designation;
		if( design != null ) ret += " " + design;
		for (Tegeval tv : tset) {
			if (tv != best) {
				ret += " " + tv.toString();
			}
		}
		return ret;
	}

	@Override
	public int compareTo(Object o) {
		if( o instanceof Teginfo ) return best.compareTo(((Teginfo)o).best);
		return -1;
	}
}