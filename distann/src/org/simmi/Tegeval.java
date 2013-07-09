package org.simmi;

public class Tegeval implements Comparable<Tegeval> {
	public Tegeval(Gene gene, String tegund, double evalue, StringBuilder sequence, StringBuilder dnaseq, String contig, Contig shortcontig, String locontig, int sta, int sto, int orient) {
		teg = tegund;
		eval = evalue;
		dna = dnaseq;
		cont = contig;
		contshort = shortcontig;
		contloc = locontig;
		start = sta;
		stop = sto;
		ori = orient;
		num = -1;
		this.gene = gene;

		if( dna != null ) gc = (double)gcCount()/(double)dna.length();
		else gc = -1.0;
		
		numCys = 0;
		setSequence(sequence);
	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int i) {
		num = i;
	}

	public String getSpecies() {
		return teg;
	}
	
	public int getLength() {
		return stop - start;
	}
	
	public int getProteinLength() {
		return getLength()/3;
	}
	
	public Contig getContshort() {
		return contshort;
	}
	
	public String getContloc() {
		return contloc;
	}
	
	public String getContig() {
		return cont;
	}
	
	public Tegeval getNext() {
		if( contshort != null ) return contshort.isReverse() ? prev : next;
		return null;
	}
	
	public Tegeval getPrevious() {
		if( contshort != null )return contshort.isReverse() ? next : prev;
		return null;
	}
	
	public Tegeval setNext( Tegeval next ) {
		Tegeval old = this.next;
		this.next = next;
		return old;
	}
	
	public Tegeval setPrevious( Tegeval prev ) {
		Tegeval old = this.prev;
		this.prev = prev;
		prev.setNext( this );
		return old;
	}
	
	private double gcCount() {
		int gc = 0;
		for( int i = 0; i < dna.length(); i++ ) {
			char c = dna.charAt(i);
			if( c == 'g' || c == 'G' || c == 'c' || c == 'C' ) gc++;
		}
		return gc;
	}
	
	public double getGCPerc() {
		return gc;
	}

	double			gc;
	String 			teg;
	double 			eval;
	String 			cont;
	Contig 			contshort;
	String 			contloc;
	StringBuilder 	seq;
	StringBuilder 	dna;
	int 			start;
	int 			stop;
	int 			ori;
	int 			numCys;
	private int		num;
	Gene			gene;
	Tegeval			next;
	Tegeval			prev;
	boolean			selected = false;
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean sel ) {
		this.selected = sel;
	}
	
	public void setGene( Gene gene ) {
		this.gene = gene;
	}
	
	public Gene getGene() {
		return this.gene;
	}

	public void setSequence(StringBuilder seq) {
		if (seq != null) {
			for (int i = 0; i < seq.length(); i++) {
				char c = (char) seq.charAt(i);
				if (c == 'C' || c == 'c')
					numCys++;
			}
			this.seq = seq;
		}
	}

	public String toString() {
		return eval + " " + contloc;
	}
	
	public	static boolean locsort = true;

	@Override
	public int compareTo(Tegeval o) {
		if (locsort) {
			int ret = contshort.compareTo(o.contshort);
			/*
			 * if( o.contshort != null || o.contshort.length() < 2 ) { ret =
			 * contshort.compareTo(o.contshort); } else {
			 * System.err.println(); }
			 */
			return ret == 0 ? start - o.start : ret;
		} else {
			int comp = Double.compare(eval, o.eval);
			return comp == 0 ? teg.compareTo(o.teg) : comp;
		}
	}
}