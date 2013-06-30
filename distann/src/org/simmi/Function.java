package org.simmi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Function {
	public Function() {}
	public Function( String go ) { this.go = go; }

	String go;
	String ec;
	String metacyc;
	String kegg;
	String name;
	String namespace;
	String desc;
	Set<String> isa;
	Set<String> subset;
	private Set<Gene> geneentries;
	private Set<GeneGroup>	groupentries;
	int index;
	
	public int getGeneCount() {
		return geneentries != null ? geneentries.size() : 0;
	}
	
	public int getGroupSize() {
		return groupentries != null ? groupentries.size() : 0;
	}
	
	public Set<Gene> getGeneentries() {
		return geneentries;
	}
	
	public Set<GeneGroup> getGeneGroups() {
		return groupentries;
	}
	
	public void addGeneentries( Collection<Gene> ge ) {
		if( geneentries == null ) {
			geneentries = new HashSet<Gene>();
			groupentries = new HashSet<GeneGroup>();
		}
		geneentries.addAll( ge );
		for( Gene g : ge ) {
			groupentries.add( g.getGeneGroup() );
		}
	}
	
	public void addGroupentry( GeneGroup gg ) {
		if( groupentries == null ) groupentries = new HashSet<GeneGroup>();
		groupentries.add( gg );
	}
	
	public void addGroupentries( Collection<GeneGroup> ge ) {
		if( groupentries == null ) groupentries = new HashSet<GeneGroup>();
		groupentries.addAll( ge );
	}
	
	public int getSpeciesCount() {
		if( groupentries != null ) {
			if( groupentries.size() <= 1 ) {
				for( GeneGroup gg : groupentries ) {
					return gg.getSpecies().size();
				}
			} else {
				Set<String>	specset = new HashSet<String>();
				for( GeneGroup gg : groupentries ) {
					specset.addAll( gg.getSpecies() );
				}
				return specset.size();
			}
		}
		return 0;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public String toString() {
		return name;
	}
};