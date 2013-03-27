package org.simmi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.simmi.Mummy.Coff;
import org.simmi.Mummy.Offset;
import org.simmi.Mummy.Sequence;

public class Erfitt {
	public static List<Sequence> stuff( Mummy mummy ) throws IOException {
		//String[]	seq1s = new String[] {"l3_contig00100", "l3_contig00015", "l3_contig00019", "l3_contig00045", "l3_contig00052", "l3_contig00061", "l3_contig00069", "l3_contig00114"};
		//String[]	seq2s = new String[] {"l8_contig00002"};
		
		//Set<String>	seq1Filter = new HashSet<String>( Arrays.asList( seq1s ) );
		//Set<String>	seq2Filter = new HashSet<String>( Arrays.asList( seq2s ) );
		
		
		//final List<Sequence> lseq2 = load("/home/sigmar/fass/assembly1/454LargeContigs.fna", "/home/sigmar/fass/assembly2/454LargeContigs.fna", "/home/sigmar/fass/ab12", "/home/sigmar/fass/ab21", "l1_", "l2_");
		//final List<Sequence> lseq2 = load("/home/sigmar/fass/assembly1/454LargeContigs.fna", "/home/sigmar/fass/assembly4/454LargeContigs.fna", "/home/sigmar/fass/ab14", "/home/sigmar/fass/ab41", "l1_", "l4_");
		//final List<Sequence> lseq2 = load("/home/sigmar/fass/assembly1/454LargeContigs.fna", "/home/sigmar/fass/assembly6/454LargeContigs.fna", "/home/sigmar/fass/ab16", "/home/sigmar/fass/ab61", "l1_", "l6_", seq1Filter, seq2Filter);
		//final List<Sequence> lseq2 = mummy.load("/home/sigmar/fass/assembly3/454LargeContigs.fna", "/home/sigmar/fass/assembly8/454LargeContigs.fna", "/home/sigmar/fass/ab38", "/home/sigmar/fass/ab83", "l3_", "l8_", seq1Filter, seq2Filter);
		final List<Sequence> lseq2 = mummy.load("/home/sigmar/islandicus/islandicus.fna", "/home/sigmar/islandicus/mira.fasta", "/home/sigmar/islandicus/ab38", "/home/sigmar/islandicus/ab83", "newbler_", "mira_");

		int countno = 0;
		List<Coff> coffList = new ArrayList<Coff>();
		for (Sequence seq : lseq2) {
			if (seq.name.startsWith("newbler_")) {
				for (String key : seq.offsetMap.keySet()) {
					Coff cff = seq.offsetMap.get(key);
					/*
					 * if( cff.seq1.name.equals("l1_contig00094") &&
					 * cff.seq2.name.equals("l2_contig00004") ) {
					 * System.err.println( "start " + cff.getFirstAlignIndex() +
					 * "  " + cff.getLastAlignIndex() ); System.err.println(
					 * "next " + cff.getAlignStart() + "  " + cff.getAlignStop()
					 * ); for( Offset o : cff.loffset ) { System.err.println(
					 * o.c ); } }
					 */
					if (cff.getAlignLength() > 50) {

						coffList.add(cff);
					}
				}
			}
		}
		Collections.sort(coffList);

		System.err.println(coffList.size());
		for (Coff cff : coffList) {
			if (cff.rc)
				System.err.println();//cff.seq1.name + " Reverse on " + cff.seq2.name + " is " + cff.getAlignCount() + "  " + cff.getAlignLength());
			else
				System.err.println();//cff.seq1.name + " on " + cff.seq2.name + " is " + cff.getAlignCount() + "  " + cff.getAlignLength());
		}

		/*
		 * for( Coff cff : coffList ) { cff }
		 */

		SortedMap<String, Sequence> seqset = new TreeMap<String, Sequence>() {
			public Sequence put(String key, Sequence seq) {
				return super.put(key, seq);
			}
		};
		int gid = 0;
		int ind = 0;
		for (Coff cff : coffList) {
			int f = cff.getFirstAlignIndex();
			int l = cff.getLastAlignIndex();

			Offset fo = cff.loffset.get(f);
			Offset lo = cff.loffset.get(l);

			int alen = cff.getAlignLength();

			if( cff.seq1.name.equals("l1_contig00054") || cff.seq2.name.equals("l2_contig00050") || cff.seq2.name.equals("l2_contig00067") || cff.seq1.name.equals("l2_contig00133") ) {
				System.err.println(cff.seq1.getGid());
				System.err.println(cff.seq2.getGid());
			}
			
			if( cff.seq1.name.equals("l1_contig00023") || cff.seq2.name.equals("l1_contig00069") || cff.seq2.name.equals("l6_contig00017") || cff.seq1.name.equals("l6_contig00203") ) {
				System.err.println(cff.seq1.getGid());
				System.err.println(cff.seq2.getGid());
			}
			
			if( cff.seq1.name.equals("l1_contig00065") || cff.seq1.name.equals("l1_contig00095") || cff.seq2.name.equals("l6_contig00166") || cff.seq1.name.equals("l1_contig00037") || cff.seq2.name.equals("l6_contig00035")) {
				System.err.println(cff.seq1.getGid());
				System.err.println(cff.seq2.getGid());
			}
			
			/*if( cff.seq1.name.equals("l1_contig000") ) {
				System.err.println(cff.seq1.getGid());
				System.err.println(cff.seq2.getGid());
			}*/
			
			if (!seqset.keySet().contains(cff.seq1.name) && !seqset.keySet().contains(cff.seq2.name)) {
				String gidstr = Integer.toString(gid++);
				cff.seq1.setGid(gidstr);
				cff.seq2.setGid(gidstr);
				
				ind += 100;
				cff.seq1.setIndex( ind );
				cff.seq2.setIndex( ind+1 );

				cff.seq1.lsubseq = null;
				cff.seq2.lsubseq = null;

				if (alen > cff.seq1.match)
					cff.seq1.match = alen;
				if (alen > cff.seq2.match)
					cff.seq2.match = alen;

				seqset.put(cff.seq1.name, cff.seq1);
				seqset.put(cff.seq2.name, cff.seq2);

				cff.seq1.rc = false;
				if (cff.rc) {
					cff.seq2.rc = true;
					// if( fo.a > fo.b ) {

					int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;

					if( a < 0 ) {
						cff.seq1.setOffset( -a );
						cff.seq2.setOffset( 0 );
					} else {
						cff.seq1.setOffset( 0 );
						cff.seq2.setOffset( a );
					}
					
					for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
						byte b = cff.seq2.bb.get(i);
						if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
					}
						
					/*for( int i = cff.seq2.getLength()-fo.a; i < cff.seq2.getLength()-(lo.a+lo.c-3); i++ ) {
						byte b = cff.seq2.bb.get(i);
						if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
					}
					
					for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
						byte b = cff.seq1.bb.get(i);
						if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
					}*/
					
					for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
						byte b = cff.seq1.bb.get(i);
						if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
					}
					
					/*
					 * } else if( cff.seq1.bb.limit() - lo.b <
					 * cff.seq2.bb.limit() - lo.a ) { cff.seq1.offset = 0;
					 * cff.seq2.offset = lo.b - lo.a; } else { cff.seq1.offset =
					 * 0; cff.seq2.offset = fo.b - fo.a; }
					 */
				} else {
					cff.seq2.rc = false;
					// if( fo.a > fo.b ) {

					int a = fo.a - fo.b;

					if( a < 0 ) {
						cff.seq1.setOffset( 0 );
						cff.seq2.setOffset( -a );
					} else {
						cff.seq1.setOffset( a );
						cff.seq2.setOffset( 0 );
					}
					
					for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
						byte b = cff.seq2.bb.get(i);
						if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
					}
					
					for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
						byte b = cff.seq1.bb.get(i);
						if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
					}

					/*
					 * } else if( cff.seq1.bb.limit() - lo.b <
					 * cff.seq2.bb.limit() - lo.a ) { cff.seq1.offset = 0;
					 * cff.seq2.offset = lo.b - lo.a; } else { cff.seq1.offset =
					 * 0; cff.seq2.offset = fo.b - fo.a; }
					 */
				}
			} else if (!seqset.keySet().contains(cff.seq1.name)) {
				cff.seq1.setGid(cff.seq2.getGid());
				cff.seq1.lsubseq = null;
				seqset.put(cff.seq1.name, cff.seq1);
				
				cff.seq1.setIndex( cff.seq2.getIndex()+1 );

				if (alen > cff.seq1.match)
					cff.seq1.match = alen;
				if (alen > cff.seq2.match)
					cff.seq2.match = alen;

				if (cff.rc == true) {
					if (cff.seq2.rc == true) {
						cff.seq1.rc = false;

						//int a = fo.a - fo.b;
						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;

						cff.seq1.setOffset( cff.seq2.getOffset() - a );						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
					} else {
						cff.seq1.rc = true;

						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
						cff.seq1.setOffset( cff.seq2.getOffset() - a );
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
					}
				} else {
					if (cff.seq2.rc == true) {
						cff.seq1.rc = true;

						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength();
						//int a = fo.a - fo.b;
						cff.seq1.setOffset( cff.seq2.getOffset() - a );
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
					} else {
						cff.seq1.rc = false;
						int a = fo.a - fo.b;
						cff.seq1.setOffset( cff.seq2.getOffset() - a );
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
					}
				}
				
				if( cff.seq1.offset < 0 ) {
					for( String str : seqset.keySet() ) {
						Sequence seq = seqset.get(str);
						if( seq.getGid().equals(cff.seq1.getGid()) ) {
							seq.setOffset( seq.getOffset() - cff.seq1.getOffset() );
						}
					}
				}
			} else if (!seqset.keySet().contains(cff.seq2.name)) {
				cff.seq2.setGid(cff.seq1.getGid());
				cff.seq2.lsubseq = null;
				seqset.put(cff.seq2.name, cff.seq2);

				cff.seq2.setIndex( cff.seq1.getIndex()-1 );
				
				if (alen > cff.seq1.match)
					cff.seq1.match = alen;
				if (alen > cff.seq2.match)
					cff.seq2.match = alen;

				if (cff.rc == true) {
					if (cff.seq1.rc == true) {
						cff.seq2.rc = false;

						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
						cff.seq2.setOffset( cff.seq1.getOffset() + a );
						//cff.seq1.offset = 0;
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
					} else {
						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
						
						cff.seq2.rc = true;
						/*if (a > 0) {
							cff.seq2.offset = cff.seq1.offset - a;
						} else {*/
						cff.seq2.setOffset( cff.seq1.getOffset() + a );
						//}
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-(lo.a+lo.c-3); i < cff.seq2.getLength()-fo.a; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}*/
						
						/*for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
					}
				} else {
					if (cff.seq2.rc == true) {
						cff.seq1.rc = true;
						
						int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
						cff.seq2.setOffset( cff.seq1.getOffset() - a );
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-fo.a; i < cff.seq2.getLength()-(lo.a+lo.c-3); i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
					} else {
						cff.seq1.rc = false;
						int a = fo.a - fo.b;
						cff.seq2.setOffset( cff.seq1.getOffset() - a );
						
						for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq2.getLength()-fo.a; i < cff.seq2.getLength()-(lo.a+lo.c-3); i++ ) {
							byte b = cff.seq2.bb.get(i);
							if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
						}*/
						
						for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}
						
						/*for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
							byte b = cff.seq1.bb.get(i);
							if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
						}*/
					}
				}
				
				if( cff.seq2.offset < 0 ) {
					for( String str : seqset.keySet() ) {
						Sequence seq = seqset.get(str);
						if( seq.getGid().equals(cff.seq2.getGid()) ) {
							seq.setOffset( seq.getOffset() - cff.seq2.getOffset() );
						}
					}
				}
			} else {
				int newval = 0;
				if (!cff.seq1.getGid().equals(cff.seq2.getGid())) {
					//System.err.println(cff.seq1.gid + " " + cff.seq2.gid);
					int diff = 0;

					
					int onecount = 0;
					int twocount = 0;
					for (String seqname : seqset.keySet()) {
						Sequence seq = seqset.get(seqname);

						if (seq.getGid().equals(cff.seq1.getGid()))
							onecount++;
						else if (seq.getGid().equals(cff.seq2.getGid()))
							twocount++;
					}
					
					if (cff.rc == true) {
						if (cff.seq1.rc == true) {
							cff.seq2.rc = false;

							int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
							cff.seq2.setOffset( cff.seq1.getOffset() + a );
							//cff.seq1.offset = 0;
							
							for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
								byte b = cff.seq2.bb.get(i);
								if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
							}
							
							for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
								byte b = cff.seq1.bb.get(i);
								if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
							}
						} else {
							int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
							
							cff.seq2.rc = true;
							/*if (a > 0) {
								cff.seq2.offset = cff.seq1.offset - a;
							} else {*/
							cff.seq2.offset = cff.seq1.offset + a;
							//}
							
							for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
								byte b = cff.seq2.bb.get(i);
								if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
							}
							
							for( int i = cff.seq1.getLength()-(lo.b+lo.c-3); i < cff.seq1.getLength()-fo.b; i++ ) {
								byte b = cff.seq1.bb.get(i);
								if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
							}
						}
					} else {
						if (cff.seq2.rc == true) {
							cff.seq1.rc = true;
							
							int a = (cff.seq1.getLength() + (fo.a - fo.b)) - cff.seq2.getLength() + 1;
							cff.seq2.setOffset( cff.seq1.offset - a );
							
							for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
								byte b = cff.seq2.bb.get(i);
								if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
							}
							
							for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
								byte b = cff.seq1.bb.get(i);
								if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
							}
						} else {
							cff.seq1.rc = false;
							int a = fo.a - fo.b;
							cff.seq2.setOffset( cff.seq1.offset - a );
							
							for( int i = fo.a; i < lo.a+lo.c-3; i++ ) {
								byte b = cff.seq2.bb.get(i);
								if( b > 95 ) cff.seq2.bb.put( i, (byte)(b-32) );
							}
							
							for( int i = fo.b; i < lo.b+lo.c-3; i++ ) {
								byte b = cff.seq1.bb.get(i);
								if( b > 95 ) cff.seq1.bb.put( i, (byte)(b-32) );
							}
						}
					}

					Sequence repseq;
					Sequence newseq;
					if (onecount >= twocount) {
						repseq = cff.seq2;
						newseq = cff.seq1;
					} else {
						repseq = cff.seq1;
						newseq = cff.seq2;
					}
					diff = newval - repseq.offset;

					Set<String>	sset = new HashSet<String>();
					for (String seqname : seqset.keySet()) {
						Sequence seq = seqset.get(seqname);
						if (seq.getGid().equals(repseq.getGid())) {
							sset.add(seqname);
						}
					}
					
					if( cff.rc == true && (cff.seq1.rc == cff.seq2.rc) ) {
						int min = 2000000;
						int max = 0;
						for( String seqname : sset ) {
							Sequence seq = seqset.get(seqname);
							int mmin = seq.getOffset();
							int mmax = seq.getOffset() + seq.getLength();
							
							if( mmin < min ) min = mmin;
							if( mmax > max ) max = mmax;
						}
						
						for( String seqname : sset ) {
							Sequence seq = seqset.get(seqname);
							seq.rc = !seq.rc;
							seq.offset = max - (seq.getOffset()+seq.getLength());
						}
						
						for( String seqname : sset ) {
							Sequence seq = seqset.get(seqname);
							seq.setGid(newseq.getGid());
							seq.offset += diff;
						}
					} else {
						for( String seqname : sset ) {
							Sequence seq = seqset.get(seqname);
							seq.setGid(newseq.getGid());
							seq.offset += diff;
						}
					}
				} else {
					//System.err.println("same " + cff.seq1.name + "  " + cff.seq2.name);
					countno++;
				}
			}

			// cff.seq1.offset = 0;
			// cff.seq2.offset = cff.get
			/*
			 * for( Coff cff2 : coffList ) { if( cff == cff2 ) break;
			 * 
			 * if( cff2.seq1 == cff.seq1 ) }
			 */
		}
		System.err.println("count " + countno);

		
		SortedSet<Integer> si = new TreeSet<Integer>();
		for (String seqname : seqset.keySet()) {
			Sequence seq = seqset.get(seqname);
			si.add(Integer.parseInt(seq.gid));
		}

		lseq2.clear();
		gid = 0;
		Set<String>		removeSet = new HashSet<String>();
		for (int val : si) {
			removeSet.clear();
			for (String seqname : seqset.keySet()) {
				Sequence seq = seqset.get(seqname);
				if (Integer.toString(val).equals(seq.gid)) {
					seq.gid = Integer.toString(gid);
					removeSet.add( seq.name );
				}
			}
			
			for( String remstr : removeSet ) {
				Sequence s = seqset.remove( remstr );
				lseq2.add( s );
			}
			
			gid++;
		}
		
		return lseq2;
	}
}
