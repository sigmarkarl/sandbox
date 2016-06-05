package org.simmi;

import java.util.List;

public class HitList implements Comparable<HitList> {
	List<String>	hitlist;
	String			group;
	
	public HitList( String group, List<String> hitlist ) {
		this.group = group;
		this.hitlist = hitlist;
	}
	
	@Override
	public int compareTo(HitList o) {
		return o.hitlist.size() - this.hitlist.size();
	}
};