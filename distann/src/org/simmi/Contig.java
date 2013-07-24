package org.simmi;

import org.simmi.shared.Sequence;

class Contig implements Comparable<Contig> {
	public Contig(String name, int s) {
		seq = new Sequence( name, null );
		loc = 0.0;
		size = s;
	}
	
	public char charAt( int i ) {
		return seq.charAt( i );
	}
	
	public int getGeneCount() {
		if( end != null ) return end.getNum()+1;
		return 0;
	}
	
	public Contig( String name, StringBuilder sb ) {
		this( name, sb.length() );
		seq.setSequenceString( sb );
	}
	
	public String toString() {
		return seq.getName();
	}
	
	public int getLength() {
		return size;
	}
	
	public String getSpec() {
		String spec = "";
		int i = getName().indexOf("uid");
		if( i == -1 ) {
			i = getName().indexOf("contig");
			spec = getName().substring(0, i-1);
		} else {
			i = getName().indexOf("_", i+1);
			spec = getName().substring(0, i);
		}
		return spec;
	}
	
	/*@Override
	public boolean equals( Object other ) {
		return other instanceof Contig && name.equals( ((Contig)other).toString() );
	}*/
	
	public String getName() {
		return seq.getName();
	}
	
	public StringBuilder getSequenceString() {
		return seq.getStringBuilder();
	}
	
	public Sequence getSequence() {
		return seq;
	}
	
	public Tegeval getFirst() {
		return reverse ? end : start;
	}
	
	public Tegeval getIndex( int i ) {
		Tegeval first = getFirst();
	
		int k = 0;
		while( first != null && k < i ) {
			first = first.getNext();
			k++;
		}
		
		return first;
	}

	double 			loc;
	int 			size;
	Sequence		seq;
	boolean			reverse = false;
	Contig			next;
	Contig			prev;
	Tegeval			start;
	Tegeval			end;
	
	public void setConnection( Contig contig, boolean rev, boolean forw ) {
		if( forw ) setForwardConnection( contig, rev );
		else setBackwardConnection( contig, rev );
	}
	
	public void setForwardConnection( Contig contig, boolean rev ) {
		this.next = contig;
		if( rev ) {
			contig.next = this;
			
			if( this.end != null ) this.end.next = contig.end;
			if( contig.end != null ) contig.end.next = this.end;
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}
		} else {
			contig.prev = this;
			
			if( this.end != null ) this.end.next = contig.start;
			if( contig.start != null ) contig.start.prev = this.end;
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}
		}
	}
	
	public void setBackwardConnection( Contig contig, boolean rev ) {
		this.prev = contig;
		if( rev ) {
			contig.next = this;
			
			this.start.prev = contig.end;
			if( contig.end != null ) contig.end.next = this.start;
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}
		} else {
			contig.prev = this;
			
			this.start.prev = contig.start;
			if( contig.start != null ) contig.start.prev = this.start;
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}
		}
	}
	
	public Contig getNextContig() {
		return next;
	}
	
	public Contig getPrevContig() {
		return prev;
	}
	
	public boolean isReverse() {
		return reverse;
	}
	
	public void setReverse( boolean rev ) {
		this.reverse = rev;
	}
	
	@Override
	public int compareTo(Contig o) {
		return getName().compareTo( o.getName() );
	}
}