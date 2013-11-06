package org.simmi;

import java.awt.Color;

public class Tegeval implements Comparable<Tegeval> {
	public Tegeval(Gene gene, String tegund, double evalue, String contig, Contig shortcontig, String locontig, int sta, int sto, int orient) {
		this( contig, shortcontig, locontig, sta, sto, orient );
		
		teg = tegund;
		eval = evalue;
		this.gene = gene;
		//dna = dnaseq;
		//setSequence(sequence);
	}
	
	public Tegeval( String contig, Contig shortcontig, String locontig, int sta, int sto, int orient ) {
		cont = contig;
		contshort = shortcontig;
		contloc = locontig;
		start = sta;
		stop = sto;
		ori = orient;
		num = -1;
		
		/*if( shortcontig == null ) {
			System.err.println();
		}*/

		gc = (double)gcCount()/(double)(stop-start);
		gcskew = gcSkew();
		//else gc = -1.0;
		
		numCys = 0;
	}
	
	public String getCommonName() {
		return gene.getGeneGroup().getCommonName();
	}
	
	public String getCommonFunction() {
		return gene.getGeneGroup().getCommonFunction(true, null);
	}
	
	public void setAlignedSequence( StringBuilder alseq ) {
		seq = alseq;
	}
	
	public void setTegund( String teg ) {
		this.teg = teg;
	}
	
	public void setEval( double eval ) {
		this.eval = eval;
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
	
	public String getSubstring( int u, int e ) {
		return contshort.seq.getSubstring(start+u, start+e, ori);
	}
	
	public String getSequence() {
		return contshort.seq.getSubstring(start, stop, ori);
	}
	
	public StringBuilder getAlignedSequence() {
		return seq;
	}
	
	public StringBuilder getProteinSubsequence( int u, int e ) {
		return contshort.seq.getProteinSequence( start+u, start+e, ori );
	}
	
	public StringBuilder getProteinSequence() {
		StringBuilder ret = contshort.seq.getProteinSequence( start, stop, ori );
		return ret;
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
		if( contshort != null ) return contshort.getNext( this );
		return null;
	}
	
	public Tegeval getPrevious() {
		if( contshort != null ) return contshort.getPrev( this );
		return null;
	}
	
	/*public Tegeval setNext( Tegeval next ) {
		Tegeval old = this.next;
		this.next = next;
		return old;
	}
	
	public Tegeval setPrevious( Tegeval prev ) {
		Tegeval old = this.prev;
		this.prev = prev;
		prev.setNext( this );
		return old;
	}*/
	
	private double gcSkew() {
		int g = 0;
		int c = 0;
		//for( int i = 0; i < dna.length(); i++ ) {
		if( contshort != null ) for( int i = start; i < stop; i++ ) {
			char n = this.ori == -1 ? contshort.revCompCharAt(i) : contshort.charAt(i);
			if( n == 'g' || n == 'G' ) g++;
			else if( n == 'c' || n == 'C' ) c++;
		}
		double gc = g+c;
		return gc == 0 ? gc : (g-c)/gc;
	}
	
	private double gcCount() {
		int gc = 0;
		//for( int i = 0; i < dna.length(); i++ ) {
		if( contshort != null ) for( int i = start; i < stop; i++ ) {
			char c = this.ori == -1 ? contshort.revCompCharAt(i) : contshort.charAt(i);
			if( c == 'g' || c == 'G' || c == 'c' || c == 'C' ) gc++;
		}
		return gc;
	}
	
	public double getGCPerc() {
		return gc;
	}
	
	public double getGCSkew() {
		return gcskew;
	}
	
	public Color getGCColor() {
		double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
		return new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f );
		
		/*double gcp = Math.min( Math.max( 0.35, gc ), 0.55 );
		return new Color( (float)(0.55-gcp)/0.2f, (float)(gcp-0.35)/0.2f, 1.0f );*/
	}
	
	public Color getGCSkewColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		return new Color( (float)Math.min( 1.0, Math.max( 0.0, 0.5+5.0*gcskew ) ), 0.5f, (float)Math.min( 1.0, Math.max( 0.0, 0.5-5.0*gcskew ) ) );
	}

	double			gc;
	double			gcskew;
	String 			teg;
	double 			eval;
	String 			cont;
	Contig 			contshort;
	String 			contloc;
	StringBuilder 	seq;
	//StringBuilder 	dna;
	int 			start;
	int 			stop;
	int 			ori;
	int 			numCys;
	private int		num;
	Gene			gene;
	//Tegeval			next;
	//Tegeval			prev;
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

	/*public void setSequence(StringBuilder seq) {
		if (seq != null) {
			for (int i = 0; i < seq.length(); i++) {
				char c = (char) seq.charAt(i);
				if (c == 'C' || c == 'c')
					numCys++;
			}
			this.seq = seq;
		}
	}*/

	public String toString() {
		return contloc;
	}
	
	public static boolean locsort = true;

	@Override
	public int compareTo(Tegeval o) {
		if( locsort ) {
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