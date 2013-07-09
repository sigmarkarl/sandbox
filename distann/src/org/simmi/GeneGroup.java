package org.simmi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneGroup {
	Set<Gene>	genes = new HashSet<Gene>();
	Set<String>	species = new HashSet<String>();
	int 		groupIndex = -10;
	int 		groupCount = -1;
	int			index;
	//int			groupGeneCount;
	
	public int getMaxCyc() {
		int max = -1;
		for( Gene g : genes ) {
			if( g.getMaxCyc() > max ) max = g.getMaxCyc();
		}
		return max;
	}
	
	public int getMaxLength() {
		int max = -1;
		for( Gene g : genes ) {
			if( g.getMaxLength() > max ) max = g.getMaxLength();
		}
		return max;
	}
	
	public List<Tegeval> getTegevals( Set<String> sortspecies ) {
		List<Tegeval>	ltv = new ArrayList<Tegeval>();
		
		for( String sp : sortspecies )
		/*for( Gene g : genes ) {
			Teginfo stv = g.species.get(sp);
			if( stv == null ) {
				//System.err.println( sp );
			} else {
				for (Tegeval tv : stv.tset) {
					ltv.add( tv );
				}
			}
		}*/
			ltv.addAll( getTegevals( sp ) );
		
		return ltv;
	}
	
	public List<Tegeval> getTegevals( String species ) {
		List<Tegeval>	ltv = new ArrayList<Tegeval>();
		
		for( Gene g : genes ) {
			Teginfo stv = g.species.get(species);
			if( stv == null ) {
				//System.err.println( sp );
			} else {
				for (Tegeval tv : stv.tset) {
					ltv.add( tv );
				}
			}
		}
		
		return ltv;
	}
	
	public List<Tegeval> getTegevals() {
		List<Tegeval>	ltv = new ArrayList<Tegeval>();
		
		for( Gene g : genes ) {
			for( String species : g.species.keySet() ) {
				Teginfo stv = g.species.get(species);
				if( stv == null ) {
					//System.err.println( sp );
				} else {
					for (Tegeval tv : stv.tset) {
						ltv.add( tv );
					}
				}
			}
		}
		
		return ltv;
	}
	
	public void setIndex( int i ) {
		this.index = i;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getAvgGCPerc() {
		double gc = 0.0;
		int count = 0;
		for( Gene g : genes ) {
			for( String spec : g.species.keySet() ) {
				Teginfo ti = g.species.get(spec);
				for( Tegeval te : ti.tset ) {
					gc += te.getGCPerc();
					count++;
				}
			}
		}
		return gc/count;
	}
	
	public double getStddevGCPerc( double avggc ) {
		double gc = 0.0;
		int count = 0;
		for( Gene g : genes ) {
			for( String spec : g.species.keySet() ) {
				Teginfo ti = g.species.get(spec);
				for( Tegeval te : ti.tset ) {
					double val = te.getGCPerc()-avggc;
					gc += val*val;
					count++;
				}
			}
		}
		return Math.sqrt(gc/count);
	}
	
	public Set<Function> getFunctions() {
		Set<Function>	funcset = new HashSet<Function>();
		for( Gene g : genes ) {
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( Function f : g.funcentries ) {
					//Function f = funcmap.get( go );
					funcset.add( f );
				}
			}
		}
		return funcset;
	}
	
	public String getCommonFunction( boolean breakb, Set<Function> allowedFunctions ) {
		String ret = "";
		for( Gene g : genes ) {
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( Function f : g.funcentries ) {
					//Function f = funcmap.get( go );
					
					if( allowedFunctions == null || allowedFunctions.contains(f) ) {
						String name = f.getName().replace('/', '-').replace(",", "");
							
						//System.err.println( g.getName() + "  " + go );
						if( ret.length() == 0 ) ret += name;
						else ret += ","+name;
					}
				}
				if( breakb ) break;
			}
		}
		return ret;
	}
	
	public String getCommonNamespace() {
		String ret = "";
		Set<String>	included = new HashSet<String>();
		for( Gene g : genes ) {
			if( g.funcentries != null ) for( Function f : g.funcentries ) {
				//Function f = funcmap.get( go );
				String namespace = f.getNamespace();
				//System.err.println( g.getName() + "  " + go );
				if( !included.contains(namespace) ) {
					if( ret.length() == 0 ) ret += namespace;
					else ret += ","+namespace;
					included.add(namespace);
				}
			}
		}
		return ret;
	}
	
	public String getCommonName() {
		String ret = null;
		for( Gene g : genes ) {
			if( ret == null || !(g.getName().contains("unnamed") || g.getName().contains("hypot")) ) ret = g.getName();
		}
		return ret;
	}
	
	public String getKeggid() {
		String ret = null;
		for( Gene g : genes ) {
			if( g.keggid != null ) ret = g.keggid;
		}
		return ret;
	}
	
	public Set<String> getSpecies() {
		return species;
	}
	
	public boolean isSingluar() {
		return this.getGroupCount() == this.getGroupCoverage();
	}
	
	public void addGene( Gene gene ) {
		if( gene.getGeneGroup() != this ) gene.setGeneGroup( this );
		else genes.add( gene );
	}
	
	public void addSpecies( Set<String> species ) {
		this.species.addAll( species );
	}
	
	public GeneGroup( int i ) {
		this.groupIndex = i;
	}
	
	public int getGroupCoverage() {
		return this.species.size();
	}
	
	public void setGroupCount( int count ) {
		this.groupCount = count;
	}
	
	public int getGroupCount() {
		if( groupCount == -1 ) {
			int val = 0;
			for (Gene g : genes) {
				if (g.species != null) {
					for (String str : g.species.keySet()) {
						val += g.species.get(str).tset.size();
					}
				}
			}
			this.groupCount = val;
		}
		return this.groupCount;
	}
	
	public int getGroupGeneCount() {
		return this.genes.size();//this.groupGeneCount;
	}
};
