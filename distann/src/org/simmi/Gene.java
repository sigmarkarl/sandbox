package org.simmi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Gene {
	public Gene(GeneGroup gg, String id, String name, String origin) {
		this.name = name;
		this.origin = origin;
		this.gg = gg;
		this.refid = id;
		// this.setAa( aa );
		
		//groupIdx = -10;
	}
	
	public int getMaxCyc() {
		int max = -1;
		if( species != null ) {
			for (String str : species.keySet()) {
				Teginfo set = species.get(str);
				for (Tegeval tv : set.tset) {
					max = Math.max(max, tv.numCys);
				}
			}
		}
		return max;
	}
	
	public int getMaxLength() {
		int max = -1;
		if( this.species != null ) {
			for (String str : this.species.keySet()) {
				Teginfo set = this.species.get(str);
				for (Tegeval tv : set.tset) {
					max = Math.max(max, tv.getProteinLength());
				}
			}
		}
		return max;
	}

	public void setAa(String aa) {
		if (aa != null) {
			this.aac = aa;
		}
	}

	public String getAa() {
		return aac;
	}
	
	public void setGeneGroup( GeneGroup gg ) {
		this.gg = gg;
		
		gg.addGene( this );
		//gg.addSpecies( this.species );	
	}
	
	public GeneGroup getGeneGroup() {
		return gg;
	}
	
	public int getGroupIndex() {
		if( gg != null ) return gg.groupIndex;
		return -10;
	}
	
	public int getGroupCoverage() {
		if( gg != null ) return gg.getGroupCoverage();
		return -1;
	}
	
	public int getGroupCount() {
		if( gg != null ) return gg.getGroupCount();
		return -1;
	}
	
	public int getGroupGenCount() {
		if( gg != null ) return gg.getGroupGeneCount();
		return -1;
	}
	
	public String getName() {
		return name;
	}
	
	public double getAvgGCPerc() {
		double gc = 0.0;
		int count = 0;
		if( species != null ) {
			for( String spec : species.keySet() ) {
				Teginfo ti = species.get(spec);
				for( Tegeval te : ti.tset ) {
					gc += te.getGCPerc();
					count++;
				}
			}
		} else count = 1;
		return gc/count;
	}
	
	public double getStddevGCPerc( double avggc ) {
		double gc = 0.0;
		int count = 0;
		for( String spec : species.keySet() ) {
			Teginfo ti = species.get(spec);
			for( Tegeval te : ti.tset ) {
				double val = te.getGCPerc()-avggc;
				gc += val*val;
				count++;
			}
		}
		return Math.sqrt(gc/count);
	}
	
	public void addTegeval( Tegeval tegeval ) {
		Teginfo ti;
		if( species == null ) species = new HashMap<String,Teginfo>();
		if( species.containsKey( tegeval.teg ) ) {
			ti = species.get( tegeval.teg );
		} else {
			ti = new Teginfo();
			species.put( tegeval.teg, ti );
		}
		ti.add( tegeval );
	}

	String name;
	String origin;
	String refid;
	Set<String> allids;
	String genid;
	String uniid;
	String keggid;
	String pdbid;
	String blastspec;
	Set<Function> funcentries;
	Map<String, Teginfo> species;
	private String aac;
	int index;

	GeneGroup	gg;
	// Set<String> group;
	//int groupGenCount;
	//int groupCoverage;
	//int groupIdx;
	//int groupCount;
	double corr16s;
	double[] corrarr;

	double proximityGroupPreservation;
};
