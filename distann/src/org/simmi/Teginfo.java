package org.simmi;

import java.util.HashSet;
import java.util.Set;

public class Teginfo implements Comparable<Teginfo> {
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
		for (Tegeval tv : tset) {
			if (tv != best)
				ret += " " + tv.toString();
		}
		return ret;
	}

	@Override
	public int compareTo(Teginfo o) {
		return best.compareTo(o.best);
	}
}