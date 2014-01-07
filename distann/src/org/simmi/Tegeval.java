package org.simmi;

import java.awt.Color;

import org.simmi.shared.Sequence;

public class Tegeval extends Sequence implements Teg {
	public Tegeval(Gene gene, String tegund, double evalue, String contig, Contig shortcontig, String locontig, int sta, int sto, int orient) {
		this( contig, shortcontig, locontig, sta, sto, orient );
		
		teg = tegund;
		eval = evalue;
		this.gene = gene;
		//dna = dnaseq;
		//setSequence(sequence);
	}
	
	public Tegeval( String contig, Contig shortcontig, String locontig, int sta, int sto, int orient ) {
		init( contig, shortcontig, locontig, sta, sto, orient );
	}
	
	public Tegeval() {
		super();
	}
	
	public void init( String contig, Contig shortcontig, String locontig, int sta, int sto, int orient ) {
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
	
	public void setAlignedSequence( Sequence alseq ) {
		//this.sb = alseq;
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
		return contshort.getSubstring(start+u, start+e, ori);
	}
	
	public String getSequence() {
		return contshort.getSubstring(start, stop, ori);
	}
	
	public Sequence getAlignedSequence() {
		return this;
	}
	
	public StringBuilder getProteinSubsequence( int u, int e ) {
		return contshort.getProteinSequence( start+u, start+e, ori );
	}
	
	public StringBuilder getProteinSequence() {
		StringBuilder ret = contshort.getProteinSequence( start, stop, ori );
		return ret;
	}
	
	public int getLength() {
		return stop - start;
	}
	
	public int getSequenceLength() {
		return super.length();
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
			char n = /*this.ori == -1 ? contshort.revCompCharAt(i) :*/ contshort.charAt(i);
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
			char c = /*this.ori == -1 ? contshort.revCompCharAt(i) :*/ contshort.charAt(i);
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
		if( isDirty() ) return Color.red;
		double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
		return new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f );
		
		/*double gcp = Math.min( Math.max( 0.35, gc ), 0.55 );
		return new Color( (float)(0.55-gcp)/0.2f, (float)(gcp-0.35)/0.2f, 1.0f );*/
	}
	
	public Color getGCSkewColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		return new Color( (float)Math.min( 1.0, Math.max( 0.0, 0.5+5.0*gcskew ) ), 0.5f, (float)Math.min( 1.0, Math.max( 0.0, 0.5-5.0*gcskew ) ) );
	}
	
	public Color getBackFlankingGapColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		
		//int i = contshort.tlist.indexOf(this);
		//if( i == 0 || i == contshort.tlist.size()-1 ) return Color.blue;
		//else if( unresolvedGap() > 0 ) return Color.red;
		return backgap ? Color.red : Color.lightGray;
	}
	
	public Color getFrontFlankingGapColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		
		//int i = contshort.tlist.indexOf(this);
		//if( i == 0 || i == contshort.tlist.size()-1 ) return Color.blue;
		//else if( unresolvedGap() > 0 ) return Color.red;
		return frontgap ? Color.red : Color.lightGray;
	}
	
	public void unresolvedGap( int i ) {
		//int ret = 0;
		
		if( this.getGene().id.startsWith("t") ) {
			if( this.getGene().id.contains("thermophilus791_scaffold00005_42") ) {
				System.err.println();
			}
		}
		
		//int i = contshort.tlist.indexOf(this);
		if( i == 0 ) {
			Tegeval tv = contshort.tlist.get(i);
			for( int m = 0; m < tv.start; m++ ) {
				char c = contshort.charAt(m);
				if( c == 'n' || c == 'N' ) {
					//ret |= 1;
					if( this.ori == -1 ) frontgap = true;
					else backgap = true;
					
					break;
				}
			}
		} else {
			Tegeval tv = contshort.tlist.get(i);
			Tegeval tvp = contshort.tlist.get(i-1);
			for( int m = tvp.stop; m < tv.start; m++ ) {
				char c = contshort.charAt(m);
				if( c == 'n' || c == 'N' ) {
					//ret |= 1;
					if( this.ori == -1 ) frontgap = true;
					else backgap = true;
					
					break;
				}
			}
		}
		
		if( i == contshort.tlist.size()-1 ) {
			Tegeval tv = contshort.tlist.get(i);
			for( int m = tv.stop; m < contshort.length(); m++ ) {
				char c = contshort.charAt(m);
				if( c == 'n' || c == 'N' ) {
					//ret |= 2;
					if( this.ori == -1 ) backgap = true;
					else frontgap = true;
					
					break;
				}
			}
		} else {
			Tegeval tv = contshort.tlist.get(i);
			Tegeval tvn = contshort.tlist.get(i+1);
			for( int m = tv.stop; m < tvn.start; m++ ) {
				char c = contshort.charAt(m);
				if( c == 'n' || c == 'N' ) {
					//ret |= 2;
					if( this.ori == -1 ) backgap = true;
					else frontgap = true;
					
					break;
				}
			}
		}
		
		//return ret;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	double			gc;
	double			gcskew;
	String 			teg;
	double 			eval;
	String 			cont;
	Contig 			contshort;
	String 			contloc;
	//Sequence	 	seq;
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
	
	boolean			dirty = false;
	boolean			backgap = false;
	boolean			frontgap = false;
	//boolean			
	
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
		return ori == -1 ? "-" + this.contloc : this.contloc;
	}
	
	public static boolean locsort = true;

	@Override
	public int compareTo(Object o) {
		if( o instanceof Tegeval ) {
			Tegeval tv = (Tegeval)o;
			if( locsort ) {
				int ret = contshort.compareTo(tv.contshort);
				/*
				 * if( o.contshort != null || o.contshort.length() < 2 ) { ret =
				 * contshort.compareTo(o.contshort); } else {
				 * System.err.println(); }
				 */
				return ret == 0 ? start - tv.start : ret;
			} else {
				int comp = Double.compare(eval, tv.eval);
				return comp == 0 ? teg.compareTo(tv.teg) : comp;
			}
		} else if( o instanceof Teginfo ) {
			return 1;
		} else {
			return super.compareTo(o);
		}
	}
	
	/*public int compareTo(Tegeval tv) {
		if( locsort ) {
			int ret = contshort.compareTo(tv.contshort);
			/*
			 * if( o.contshort != null || o.contshort.length() < 2 ) { ret =
			 * contshort.compareTo(o.contshort); } else {
			 * System.err.println(); }
			 *
			return ret == 0 ? start - tv.start : ret;
		} else {
			int comp = Double.compare(eval, tv.eval);
			return comp == 0 ? teg.compareTo(tv.teg) : comp;
		}
	}
	
	public int compareTo(Teginfo ti) {
		return 1;
	}*/
}