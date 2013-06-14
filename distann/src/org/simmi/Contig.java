package org.simmi;

class Contig implements Comparable<Contig> {
	public Contig(String name) {
		this.name = name;
		loc = 0.0;
		count = 0;
	}
	
	public int getGeneCount() {
		if( end != null ) return end.getNum();
		return 0;
	}
	
	public Contig( String name, StringBuilder sb ) {
		this( name );
		seq = sb;
	}
	
	public String toString() {
		return name;
	}
	
	public String getSpec() {
		int i = name.indexOf('_');
		return name.substring(0, i);
	}
	
	/*@Override
	public boolean equals( Object other ) {
		return other instanceof Contig && name.equals( ((Contig)other).toString() );
	}*/
	
	public String getName() {
		return name;
	}
	
	public StringBuilder getSequence() {
		return seq;
	}

	String 			name;
	double 			loc;
	int 			count;
	StringBuilder	seq;
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
			
			this.end.next = contig.end;
			contig.end.next = this.end;
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}
		} else {
			contig.prev = this;
			
			this.end.next = contig.start;
			contig.start.prev = this.end;
			
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
			contig.end.next = this.start;
			
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
			contig.start.prev = this.start;
			
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
		return name.compareTo( o.getName() );
	}
}