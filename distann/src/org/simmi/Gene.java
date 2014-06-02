package org.simmi;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

public class Gene {
	public Gene(GeneGroup gg, String id, String name, String origin) {
		//super();
		this.name = name;
		this.species = origin;
		this.gg = gg;
		this.refid = id;
		this.id = id;
		// this.setAa( aa );
		
		//groupIdx = -10;
	}
	
	/*public Gene( GeneGroup gg, String id, String name, String origin, String tag ) {
		this( gg, id, name, origin );
		this.tegeval.type = tag;
	}*/
	
	public void getFasta( Writer w ) throws IOException {
		StringBuilder ps = tegeval.getProteinSequence();
		w.append(">" + this.tegeval.name + "\n"); //this.getId() + " " + this.getName() + (this.idstr != null ? " (" + this.idstr + ") [" : " [") + this.tegeval.name + "]" +" # " + this.tegeval.start + " # " + this.tegeval.stop + " # " + this.tegeval.ori + " #" + "\n");
		for (int i = 0; i < ps.length(); i += 70) {
			w.append( ps.substring(i, Math.min(i + 70, ps.length())) + "\n");
		}
	}
	
	public String getFasta() {
		StringWriter sb = new StringWriter();
		try {
			getFasta( sb );
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public Contig getContig() {
		return tegeval.getContshort();
	}
	
	public void setIdStr( String idstr ) {
		this.idstr = idstr;
	}
	
	public String toString() {
		return getName();
	}
	
	public int getMaxCyc() {
		return tegeval.numCys;
	}
	
	public int getMaxLength() {
		return tegeval.getProteinLength();
	}
	
	public String getTag() {
		return tegeval.type;
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
	
	public String getId() {
		return id;
	}
	
	public double getGCPerc() {
		return tegeval.getGCPerc();
	}
	
	public void setTegeval( Tegeval tegeval ) {
		/*Teginfo ti;
		if( species == null ) species = new HashMap<String,Teginfo>();
		if( species.containsKey( tegeval.teg ) ) {
			ti = species.get( tegeval.teg );
		} else {
			ti = new Teginfo();
			species.put( tegeval.teg, ti );
		}*/
		this.tegeval = tegeval;
		//if( teginfo == null ) teginfo = new Teginfo();
		//teginfo.add( tegeval );
	}
	
	public String getSpecies() {
		return species;
	}

	String name;
	String symbol;
	//String tag;
	//String origin;
	String id;
	String refid;
	String idstr;
	Cog cog;
	Set<String> allids;
	String genid;
	String uniid;
	String keggid;
	String pdbid;
	String koid;
	String koname;
	String ecid;
	String blastspec;
	Set<Function> funcentries;
	//Map<String, Teginfo> species;
	private String 	species;
	Tegeval tegeval;
	private String aac;
	int index;
	String	designation;
	
	public boolean isPhage() {
		return designation != null && designation.contains("phage");
	}

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
