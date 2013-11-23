package org.simmi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simmi.shared.Sequence;

public class Contig extends Sequence {
	public Contig(String name ) {
		super( name, null );
		loc = 0.0;
	}
	
	public void add( Tegeval tv ) {
		if( tlist == null ) tlist = new ArrayList<Tegeval>();
		tlist.add( tv );
	}
	
	public void deleteAfter( Tegeval cur ) {
		int i = tlist.indexOf( cur );
		if( i != -1 && i < tlist.size() && tlist.get(i+1).getGene() == null ) {
			tlist.remove( i+1 );
		}
	}
	
	public void deleteBefore( Tegeval cur ) {
		int i = tlist.indexOf( cur );
		if( i > 0 && tlist.get(i-1).getGene() == null )
			tlist.remove( i-1 );
	}
	
	public void injectAfter( Tegeval cur, Tegeval tv ) {
		int i = tlist.indexOf( cur );
		if( i != -1 ) {
			tlist.add(i+1, tv);
		}
	}
	
	public void injectBefore( Tegeval cur, Tegeval tv ) {
		int i = tlist.indexOf( cur );
		if( i != -1 ) {
			tlist.add( i, tv );
		}
	}
	
	public List<Tegeval> getTegevalsList() {
		return tlist;
	}
	
	public void sortLocs() {
		if( tlist != null ) {
			Collections.sort( tlist );
			int i = 0;
			//Tegeval prev = null;
			for( Tegeval tv : tlist ) {
				tv.setNum( i++ );
				//if( prev != null ) tv.setPrevious( prev );
				//prev = tv;
			}
		}
	}
	
	/*public char revCompCharAt( int i ) {
		return seq.revCompCharAt( i );
	}*/
	
	public char charAt( int i ) {
		return reverse ? super.revCompCharAt( i ) : super.charAt( i );
	}
	
	public int getGeneCount() {
		if( tlist != null ) return tlist.size();
		return 0;
	}
	
	public Contig( String name, StringBuilder sb ) {
		this( name );
		setSequenceString( sb );
	}
	
	public Tegeval getNext( Tegeval from ) {
		int i = tlist.indexOf( from );
		if( i != -1 ) {
			if( isReverse() ) {
				if( i > 0 ) return tlist.get( i-1 );
			} else {
				 if( i < tlist.size()-1 ) return tlist.get( i+1 );
			}
		}
		return null;
	}
	
	public Tegeval getPrev( Tegeval from ) {
		int i = tlist.indexOf( from );
		if( i != -1 ) {
			if( isReverse() ) {
				if( i < tlist.size()-1  ) return tlist.get( i+1 );
			} else {
				if( i > 0 ) return tlist.get( i-1 );
			}
		}
		return null;
	}
	
	public String getSpec() {
		String spec = "";
		int i = getName().indexOf("uid");
		if( i == -1 ) {
			i = getName().indexOf("contig");
			if( i == -1 ) {
				i = getName().indexOf("scaffold");
			}
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
	
	public StringBuilder getSequenceString() {
		return getStringBuilder();
	}
	
	public Tegeval getFirst() {
		if( tlist != null ) return isReverse() ? tlist.get(tlist.size()-1) : tlist.get(0);
		return null;
	}
	
	public Tegeval getLast() {
		if( tlist != null ) return isReverse() ? tlist.get(0) : tlist.get(tlist.size()-1);
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
	//Sequence		seq;
	boolean			reverse = false;
	Contig			next;
	Contig			prev;
	List<Tegeval>	tlist;
	List<Contig>	partof;
	
	public Tegeval getEndTegeval() {
		if( tlist != null ) return tlist.get( tlist.size()-1 );
		return null;
	}
	
	public Tegeval getStartTegeval() {
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
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getEnd();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getEnd();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		}
	}
	
	public void setBackwardConnection( Contig contig, boolean rev ) {
		this.prev = contig;
		if( rev ) {
			contig.next = this;
			
			/*this.getStart().prev = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getStart();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*this.getStart().prev = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getStart();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
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
	public int compareTo(Sequence o) {
		if( partof != null ) {
			return partof.indexOf( this ) - partof.indexOf( o );
		}
		return getName().compareTo( o.getName() );
	}
}