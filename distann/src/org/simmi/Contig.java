package org.simmi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simmi.shared.Sequence;

class Contig implements Comparable<Contig> {
	public Contig(String name, int s) {
		seq = new Sequence( name, null );
		loc = 0.0;
		size = s;
	}
	
	public void add( Tegeval tv ) {
		if( tlist == null ) tlist = new ArrayList<Tegeval>();
		tlist.add( tv );
	}
	
	public void sortLocs() {
		if( tlist != null ) {
			Collections.sort( tlist );
			int i = 0;
			Tegeval prev = null;
			for( Tegeval tv : tlist ) {
				tv.setNum( i++ );
				if( prev != null ) tv.setPrevious( prev );
				prev = tv;
			}
		}
	}
	
	public char charAt( int i ) {
		return seq.charAt( i );
	}
	
	public int getGeneCount() {
		if( tlist != null ) return tlist.size();
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
		if( tlist != null ) return reverse ? tlist.get(tlist.size()-1) : tlist.get(0);
		return null;
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
	List<Tegeval>	tlist;
	
	public Tegeval getEnd() {
		if( tlist != null ) return tlist.get( tlist.size()-1 );
		return null;
	}
	
	public Tegeval getStart() {
		if( tlist != null ) return tlist.get( 0 );
		return null;
	}
	
	public void setConnection( Contig contig, boolean rev, boolean forw ) {
		if( forw ) setForwardConnection( contig, rev );
		else setBackwardConnection( contig, rev );
	}
	
	public void setForwardConnection( Contig contig, boolean rev ) {
		this.next = contig;
		if( rev ) {
			contig.next = this;
			
			if( this.getEnd() != null ) this.getEnd().next = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getEnd();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}
		} else {
			contig.prev = this;
			
			if( this.getEnd() != null ) this.getEnd().next = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getEnd();
			
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
			
			this.getStart().prev = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getStart();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}
		} else {
			contig.prev = this;
			
			this.getStart().prev = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getStart();
			
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