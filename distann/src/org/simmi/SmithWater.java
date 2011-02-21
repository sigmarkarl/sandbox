package org.simmi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SmithWater {
	float   	p_gapf;
	float   	p_gapn;
	int     	p_minall;
	String		p_blmat;
	int			p_verbose;
	int			p_show;
	
	final double SMALL = 1e-10;
	
	public class FSALIST {
		public FSALIST() {
			next = null;
			seq = null;
			len = 0;
			score = -99.9f;
			i = null;
			name = "UNDEF";
		}
		
        FSALIST		next;
        char[]   	seq;
        String   	name;
        int			len;
		float		score;
		int[]		i;
	};
	
	public class ALN implements Comparable<ALN> {
		public ALN() {
			//next = null;

			alen = -99;
			type = "undef";

			qname = "undef";
			qlen = -99;
			qof = -99;
			qal = null;
			qsscore = -99.9f;

			dname = "undef";
			dlen = -99;
			dof = -99;
			dal = null;
			dsscore = -99.9f;

			mlen = nid = ngap = -99;
			score = sscore = rscore = zscore = -99.9f;
		}
		
		public String toString() {
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream( baos );
			
			aln_write_single( ps );
			
			return baos.toString();
		}
		
		public void aln_write_single()	{
			aln_write_single( System.out );
		}
		
		public void aln_write_single( PrintStream ps )	{
			String	shortname;

			ps.printf( "ALN %s %d %s %d type %s alen %d %d %d %d score %g %g %g %f\n",
				qname, qlen,
				dname.substring(0, dname.indexOf('#')), dlen,
				type, alen, mlen, nid, ngap,
				score, sscore, rscore, zscore );

			if(qal != null ) {
				shortname = qname.substring( 0, Math.min(10, qname.length()) );
				ps.printf( "QAL %s %5d %s\n", shortname, qof, new String(qal) );
			}

			if(dal != null) {
				shortname = dname.substring( 0, Math.min(10, dname.length()) );
				ps.printf( "DAL %s %5d %s\n", shortname, dof, new String(dal) );
			}
		}
		
		//ALN 	next;

		int     alen;
		String  type;

		String  qname;
		int     qlen;		/* query structure length */
		int     qof;
		char[]  qal;		/* alen */
		float   qsscore;	/* q selfscore */

		String  dname;
		int     dlen;		/* database structure length */
		int     dof;
		char[]  dal;		/* alen */
		float   dsscore;	/* d selfscore */

		int     mlen, nid, ngap;
		float   score, sscore, rscore, zscore;
		
		@Override
		public int compareTo(ALN o) {
			return Float.compare(o.score, score);
		}
	};
	
	public SmithWater() {
		p_gapf = 11.0f;
		p_gapn = 1.0f;
		p_minall = 3;
		p_blmat = "BLOSUM50";
		p_verbose = 0;
		p_show = 0;
	}

	public float sw_alignment( 
		float[][] m,		/* Scoring matrix, d matrix from lecture */
		int l1,			/* Length of query sequence */
		int l2,			/* Length of database sequence */
		float fg,		/* Penalty for first gap */
		float ng,		/* Penalty for each of subsequent gaps */
		float[][] S,		/* match scores, D matrix from lecture */
		int[] firsti,		/* Offset in query sequence */
		int[] firstj,		/* Offset in database sequence */
		char[] qal,		/* Query alignment */
		char[] dal, 		/* DB algnemnt */
		int[] alen,		/* alignemnt length */
		char[] qseq,		/* Query sequence */
		char[] dseq		/* Database sequence */
		)
	{
		int acur = 0;
		
		float[][] P;		/* P matrix */	
		float[][] Q;		/* Q matrix */
		int     i, j;
		float   temp1, temp2, temp;
		float   sij, pij, qij;
		float[]  Si, Sp; /* pointers to rows in S matrix */
		float[]  Pi; /* pointers to row in P matrix */
		float[]  Qi, Qp; /*  pointers to row in Q matrix */
		float[]  Mi; /*  pointers to row in m matrix */
		int[][]	eij;
		int	e;
		int[]	Ei;
		float	score;
		int	keep_going;
		int	best;
		int	k;

		score = 0;
		firsti[0] = -1;
		firstj[0] = -1;

		eij = new int[l1+1][l2+1]; //imatrix(0, l1, 0, l2);
		P = new float[l1+1][l2+1]; //fmatrix(0, l1, 0, l2);
		Q = new float[l1+1][l2+1]; //fmatrix(0, l1, 0, l2);
		S[l1][l2] = 0.0f;
		P[l1][l2] = 0.0f;
		Q[l1][l2] = 0.0f;

		for (j = l2 - 1; j >= 0; j--) {
			sij = S[l1][j + 1];
			S[l1][j] = sij;
			P[l1][j] = sij;  /* Here one can penalize ends and set P[l1][j] = sij - fg */
			Q[l1][j] = sij;  /* Here one can penalize ends and set Q[l1][j] = sij - fg */
		}

		for (j = l1 - 1; j >= 0; j--) {
			sij = S[j+1][l2];
			S[j][l2] = sij;
			P[j][l2] = sij;  /* Here one can penalize ends and set P[j][l2] = sij - fg */
			Q[j][l2] = sij;	 /* Here one can penalize ends and set Q[j][l2] = sij - fg */
		}

		/* Loop over Query sequence */
		for (i = l1 - 1; i >= 0; i--) {

			/* indirect array access to speed up code */
			/* Mi, Si, Pi etc points to row i in the corresponding matrix */

			Mi = m[i];
			Si = S[i];
			Pi = P[i];
			Qi = Q[i];

			Sp = S[i + 1]; /* pointer to the i+1 row in S */
			Qp = Q[i + 1];

			Ei = eij[i];

			/* eij is the backtrack direction matrix
				eij = 0 stop back tracking
				eij = 1 match
				eij = 2 gap-opening database
				eij = 3 gap-extension database
				eij = 4 gap-opening query
				eij = 5 gap-extension query
			*/

			for (j = l2 - 1; j >= 0; j--) {

	/* Fill in the missing code XXXXX */

				/* Try match state */

				sij = Sp[j + 1] + Mi[j]; /* sij = S[i+1][j+1] + m[i][j] */

				/* Try Gap in Database sequence (insertion in query sequence) */

				temp1 = Qp[j] - ng; /* Gap extension temp1 = Q[i+1][j] - ng */
	            temp2 = Sp[j] - fg; /* Gap opening   temp2 = S[i+1][j] - fg */

				if ( temp1 > temp2 ) { /* extension best */
					qij = temp1;
					e = 3;
				} else { /* gap opening best */
					qij = temp2; 
					e = 2;
				}

				/* Select if match or gap scores best */

				if ( qij > sij ) 
					sij = qij; /* Gap is best */
				else 
					e = 1; /* Match best */

				/* Try Gap in query sequence (insertion in database sequence) */

				temp1 = Pi[j+1] - ng; /* Gap extension */
				temp2 = Si[j+1] - fg; /* Gap opening */

				if ( temp1 > temp2 ) { /* extension best */

					pij = temp1;

					if ( temp1 > sij ) { /* extension best */
						sij = temp1;
						e = 5;
					}

				} else { /* gap opening best */

					pij = temp2;

					if ( temp2 > sij ) { /* gap opening best */
						sij = temp2;
						e = 4;
	                }

				}

				if (sij > score) {
					score = sij;
					firsti[0] = i;
					firstj[0] = j;
				}

				if ( sij <= 0 ) {
					sij = 0.0f;
					e = 0;
				}

				Si[j] = sij; /* S[i][j] = sij */
				Qi[j] = qij; /* Q[i][j] = qij */
				Pi[j] = pij; /* P[i][j] = pij */
				Ei[j] = e;   /* eij[i][j] = e */
			}
		}

	/* This is some code you might use to debug the program */

		if ( p_show != 0 ) {

	                System.out.printf( "# S-matrix\n" );

	                for ( j=0; j<l2; j++ )
	                	System.out.printf( "%4c ", dseq[j] );
	                System.out.printf( "\n" );

	                for ( i=0; i<=l1; i++ ) {
	                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
	                        for ( j=0; j<=l2; j++ )
	                        	System.out.printf( " %5.2f", S[i][j] );
	                        System.out.printf( "\n" );
	                }

	                System.out.printf( "# Q-matrix\n" );

	                for ( j=0; j<l2; j++ )
	                	System.out.printf( "%4c ", dseq[j] );
	                System.out.printf( "\n" );

	                for ( i=0; i<=l1; i++ ) {
	                	System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
	                        for ( j=0; j<=l2; j++ )
	                        	System.out.printf( " %5.2f", Q[i][j] );
	                        System.out.printf( "\n" );
	                }
	               
	                System.out.printf( "# P-matrix\n" );
	                
	                for ( j=0; j<l2; j++ )
	                	System.out.printf( "%4c ", dseq[j] );
	                System.out.printf( "\n" );
	                
	                for ( i=0; i<=l1; i++ ) {
	                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
	                        for ( j=0; j<=l2; j++ )
	                        	System.out.printf( " %5.2f", P[i][j] );
	                        System.out.printf( "\n" );
	                }
	                
	                System.out.printf( "# Eij-matrix\n" );
	                
	                for ( j=0; j<l2; j++ )
	                	System.out.printf( "%4c ", dseq[j] );
	                System.out.printf( "\n" );
	                
	                for ( i=0; i<=l1; i++ ) {
	                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
	                        for ( j=0; j<=l2; j++ )
	                        	System.out.printf( " %2i", eij[i][j] );
	                        System.out.printf( "\n" );
	                }
		}

		//fmatrix_free(P, 0, l1, 0, l2);
		//fmatrix_free(Q, 0, l1, 0, l2);

		/* Do back tracking */

		if (firsti[0] < 0 || firstj[0] < 0 ) 
			System.out.printf( "No alignment found. Exit\n" );

		alen[acur] = 0;

		i = firsti[0];
		j = firstj[0];
		qal[alen[acur]] = qseq[i];
		dal[alen[acur]] = dseq[j];
		i++;
		j++;

		alen[acur]++;

		keep_going = 1;

		while( (i < l1 ) && (j < l2 ) && keep_going != 0 ) {

			if ( eij[i][j] == 0 ) {
				keep_going = 0;
			} else if ( eij[i][j] == 1 ) { /* Match */
				qal[alen[acur]] = qseq[i];
				dal[alen[acur]] = dseq[j];
				i++;
				j++;
				alen[acur]++;
			} else if ( eij[i][j] == 4 ) { /* gap opening in Query */
				qal[alen[acur]] = '-';
				dal[alen[acur]] = dseq[j];
				j++;
				alen[acur]++;
			} else if (  eij[i][j] == 5 ) { /* gap extension in Query */
				best = j+2;
				Si = S[i];
				temp = Si[best] - fg - ( best-j-1) * ng - Si[j];
				while ( temp*temp > SMALL ) {
					best++;
					temp = Si[best] - fg - ( best-j-1) * ng - Si[j];
				}

				for ( k=j; k<best; k++ ) {
					qal[alen[acur]] = '-';
					dal[alen[acur]] = dseq[j];
					j++;
					alen[acur]++;
				}
			} else if ( eij[i][j] == 2 ) { /* gap opening in Database */
				qal[alen[acur]] = qseq[i];
				dal[alen[acur]] = '-';
				i++;
				alen[acur]++;
			} else if (  eij[i][j] == 3 ) { /* gap extension in Database */

				/* Write code for gap extension in database inspired by code for gap extension 
				in query 25 linies up */

				best = i+2;
				//Si = S[i];
				temp = S[best][j] - fg - (best-i-1) * ng - S[i][j];

				while ( temp*temp > SMALL ) {
					best++;
					//if( best < S.length && j < S[best].length )
					temp = S[best][j] - fg - (best-i-1) * ng - S[i][j];
				}
				                              
				for ( k=i; k<best; k++ ) {
					qal[alen[acur]] = qseq[i];
					dal[alen[acur]] = '-';
					i++;
					alen[acur]++;
				}
			}
		}

	/* NOW YOU ARE DONE */

		qal[alen[acur]] = 0;
		dal[alen[acur]] = 0;

		//imatrix_free( eij, 0, l1, 0, l2);

		return( score );
	}

	public ALN align(float[][] m, FSALIST q, FSALIST d, float gapf, float gapn) {
		float[][] sco;
		float   score;
		int[]   firsti = new int[1];
		int[]	firstj = new int[1];
		int[]   alen = new int[1];
		ALN     neb = new ALN();
		int     i;
		char[]	qpal = new char[15000];
		char[]	dpal = new char[15000];

		/* This is the D matrix from the lecture */
		sco = new float[q.len+1][d.len+1]; //fmatrix(0, q.len, 0, d.len);

		score = sw_alignment(m, q.len, d.len, gapf, gapn, sco, firsti, firstj, qpal, dpal, alen, q.seq, d.seq );

		//fmatrix_free(sco, 0, q.len, 0, d.len);

		if(alen[0] < p_minall) return null;

		neb.alen = alen[0];

		neb.score = score;

		neb.mlen = 0;
		neb.ngap = 0;
		neb.nid = 0;

		for (i = 0; i < neb.alen; i++) {
			if (qpal[i] != '-' && dpal[i] != '-') {
				neb.mlen++;
			} else {
				neb.ngap++;
			}
			if (qpal[i] == dpal[i])
				neb.nid++;
		}

		neb.qof = firsti[0];
		int val = 0;
		while( dpal[val] != 0 ) val++;
		neb.qal = new char[val]; //cvector(0, strlen(qpal));
		System.arraycopy(qpal, 0, neb.qal, 0, val); //neb.qal = qpal;

		neb.dof = firstj[0];
		val = 0;
		while( dpal[val] != 0 ) val++;
		neb.dal = new char[val]; //cvector(0, strlen(dpal));
		System.arraycopy(dpal, 0, neb.dal, 0, val); //neb.dal = dpal;

		neb.qname = q.name;
		neb.qlen = q.len;

		neb.dname = d.name;
		neb.dlen = d.len;

		neb.type = "SW_ALN";

		neb.rscore = -neb.score;

		return neb;
	}

	public float[][] score_mat( FSALIST q, FSALIST d, float[][] blmat ) {
	        float[][]   scomat;
	        int     i,j;
	        int     ix, jx;

	        scomat = new float[q.len][d.len];

	        for ( i=0; i<q.len; i++ ) {
	                ix = q.i[i];

	                if ( ix < 0 ) {
	                        System.out.printf( "Error. Unconventional amino acid i query sequence %c %s\n", q.seq[i], q.name );
	                }

			if ( ix > 19 )
				continue;
	                 
	                for ( j=0; j<d.len; j++ ) {

	                        jx = d.i[j];

	                        if ( jx < 0 ) {
	                                System.out.printf( "Error. Unconventional amino acid i query sequence %c %s\n", d.seq[j], d.name );
	                        }

				if ( jx > 19 )
					continue;
	               
	                        scomat[i][j] = blmat[ix][jx];
	                }
	        }
	        return( scomat );
	}
	
	/*String sepchars = " ,\t";
	final int WORDSIZE = 56;
	char[]	word = new char[WORDSIZE];
	public char[][]	split( String line, int[] n ) {
		char[][]	wvec;
		int	len;
		int	e,b;

		len = line.length();

		e = 0;
		n[0] = 0;
		while ( e<len ) {
			while ( e < len && strpos( sepchars, line[e] ) >= 0 )
				e++;

			b = e;
			while ( e < len && strpos( sepchars, line[e] ) < 0 )
				e++;

			if ( b < e ) {
				n[0]++;
			}
		}

		wvec = new char[n[0]-1][WORDSIZE]; //cmatrix( 0, n[0]-1, 0, WORDSIZE );

		e = 0;
		n[0] = 0;
		while ( e<len ) {

			while ( e < len && strpos( sepchars, line[e] ) >= 0 )
				e++;

			b = e;
			while ( e < len && strpos( sepchars, line[e] ) < 0 )
				e++;

			if ( b < e ) {
				strncpy( wvec[n[0]], line+b, e-b );
				wvec[(*n)][e-b] = 0;

				(*n)++;
			}
		}

		return( wvec );
	}*/
	
	final int MAXFSALEN = 100000;
	final String MFORMAT2 = "%*s %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f";
	public float[][] read_blosummat( Reader rd, char[] alphabet ) throws IOException	{
	        //LINELIST        *linelist, *ln;
	        float[][]		m;
	        int             nc, l, i;
	        String[]		wvec;

	        BufferedReader	br = new BufferedReader( rd );
	        //linelist = linelist_read( filename );

	        if( br == null ) {
	                System.out.printf( "Error. Cannot read from file %s\n", rd.toString() );
	        }
	         
	        //(*alphabet) = cvector( 0, 20 );
	        m = new float[20][20]; //fmatrix( 0, 19, 0 ,19 );
	        l = 0;

	        String ln = br.readLine();
	        while( ln != null ) {
	                if( ln.length() <= 1 ) {
	                	ln = br.readLine();
	                	continue;
	                }
	               
	                if( ln.startsWith("#") ) {
	                	ln = br.readLine();
	                	continue; //strncmp( ln->line, "#", 1 ) == 0 ) continue;
	                }
	                
	                if( ln.startsWith("   A") ) { //if ( strncmp( ln->line, "   A", 4 ) == 0 ) {
                        wvec = ln.split("[ ,\t]+"); //split( ln->line, &nc );
               
                        for ( i=0;i<20;i++ ) {
                        	alphabet[i] = wvec[i+1].charAt(0);
                        }
               
                        ln = br.readLine();
                        continue;
	                }
	               
	                if( l < 20 ) {
		                int k = 0;
		                Scanner	scan = new Scanner(ln);
		                scan.next();
		                while( scan.hasNext() && k < 20 ) {
		                	float f = scan.nextFloat();
		                	m[l][k++] = f;
		                }
	                }
	                
	                /*if ( l < 20 && sscanf( ln, MFORMAT2,
	                        &m[l][0], &m[l][1], &m[l][2], &m[l][3], &m[l][4],
	                        &m[l][5], &m[l][6], &m[l][7], &m[l][8], &m[l][9],
	                        &m[l][10], &m[l][11], &m[l][12], &m[l][13], &m[l][14],
	                        &m[l][15], &m[l][16], &m[l][17], &m[l][18], &m[l][19] ) != 20 ) {
	               
	                        System.out.printf( "Wrong line format %s", ln );
	                        System.exit( 1 );
	                }*/
	               
	                l++;
	                ln = br.readLine();
	        }      
	               
	        //linelist_free( linelist );
	               
	        System.out.printf( "# Read_realblosum done. Alphabet %s\n", new String(alphabet) );
	               
	        return m;
	}
	
	final int	fsa_verbose = 1;
	char[]	seq = new char[MAXFSALEN];
	public FSALIST fsalist_read_single( BufferedReader br, FSALIST last ) throws IOException {
		FSALIST neb;
		//LINE	line, junk;
		int	len, o, i, read;
		char 	ch;
		//char[]	longline = new char[20000];

		neb = null;
		len = 0;
		o=0;
		seq[0] = 0;
		read = 0;

		//BufferedReader br = new BufferedReader( new FileReader( fp ) );
		String line = br.readLine();
		while ( line != null ) {
			if ( line.startsWith("#") ) { //fp && ( ch = fgetc( fp ) ) && ungetc( ch, fp ) && ch == '#' ) {
				//fgets( line, sizeof(line), fp );
				System.out.printf( "# %s", line );
				continue;
			}

			if ( line.startsWith(">") ) { //fp && ( ch = fgetc( fp ) ) && ungetc( ch, fp ) && ch == '>' ) {
				/*if( last == null ) { /* Read name of entry *

					//fgets( longline, sizeof(longline), fp );

					neb = new FSALIST();
					neb.name = line;
					/*sscanf( longline, ">%[^\n]", junk );
					strncpy( neb.name, junk, sizeof( neb.name ) );
					if ( strlen( junk ) >= sizeof( neb.name ) )
						neb.name[sizeof( neb.name )-1] = 0;*
				} else { /* Start next entry. Entry read *
					neb.next = new FSALIST();
					neb.next.name = line;
					read = 1;
				}*/
				
				neb = new FSALIST();
				neb.name = line;
				break;
			} else { //if ( fp && ( fgets( line, sizeof(line), fp ) != null ) ) {
				/*junk[0] = 0;
				sscanf( line, "%[^\n\t ]", junk );

				if ( strlen( junk ) < 1 ) continue;
				if ( !isascii( junk[0] ) ) continue;*/

				len = line.length();
				if( line.endsWith("*") ) len--;

				//junk[len] = 0;

				if ( o+len > MAXFSALEN ) {
					System.out.printf( "Error. %s Length > %i\n", neb.name, MAXFSALEN );
				}

				for ( i=0; i<len; i++ )
					seq[i+o] = line.charAt(i);//junk[i];

				o += len;
				seq[o] = 0;

			}
			
			line = br.readLine();
		}

		if ( last != null ) {
			last.seq = new char[o+1]; //cvector( 0, o );
			System.arraycopy(seq, 0, last.seq, 0, last.seq.length); //strcpy( neb.seq, seq );
			last.seq[o] = 0;
			last.len = o;
		}

		return neb;
	}
	
	public FSALIST fsalist_check_names( FSALIST fsalist ) {
        FSALIST fsa;
        int     l;

        for ( fsa=fsalist; fsa != null; fsa=fsa.next ) {
                l = fsa.name.length() - 1;

                while ( l >= 0 && fsa.name.charAt(l) == ' ' ) l--;
                l++;

                fsa.name = fsa.name.substring(0, l);
                fsa.name.replace(' ', '_');
        }

        return( fsalist );
	}

	
	public FSALIST fsalist_read( Reader rd ) throws IOException {
		File	fp;
		FSALIST	list = null;
		FSALIST	last = null;
		FSALIST neb = null;
		int	fc, ff;
		int	n;

		/*fp = new File( filename );
	    if( !fp.exists() ) {
	    	System.out.printf( "Error. Cannot read FSALIST from file %s. Exit\n", rd.toString() );
	        System.exit( 1 );
	    }*/

		list = null;
		n=0;

		BufferedReader br = new BufferedReader( rd );
		while( (neb = fsalist_read_single( br, neb )) != null )  {
			if( list == null )
				list = neb;
			else
				last.next = neb;

			last = neb;

			n++;
		}
		br.close();

		if ( fsa_verbose != 0 )
			System.out.printf( "# Number of FASTA entries read %d from file %s\n", n, rd.toString() );

		//stream_close( fp, fc, filename );

		return( list );
	}
	
	String PROFILE_ORDER = "ARNDCQEGHILKMFPSTWYVX";
	public void fsalist_iassign_profile_order( FSALIST fsalist ) {
        FSALIST    fsa;
        int     	n;

        for( fsa = fsalist; fsa != null; fsa = fsa.next ) {
                n = fsa.len;

                fsa.i = new int[n];

                for( int i = 0; i < n; i++ ) {
                	fsa.i[i] = PROFILE_ORDER.indexOf(fsa.seq[i]); //strpos(PROFILE_ORDER, fsa.seq[i]);
                }
        }
	}
	
	public void fasta_align( String[] args ) throws IOException {
		fasta_align( new FileReader(args[0]), new FileReader(args[1]) );
	}

	public List<ALN> fasta_align( Reader qr, Reader dr ) throws IOException {
		FSALIST		q_fsa, db_fsa, d;
		float   	gapf, gapn;
		ALN			neb;
		float[][]   blmat, scomat;
		char[]      alphabet = new char[20];

		//pparse(&argc, &argv, fasta_align_param, 2, "fsa1 db");

		/* Read Blosum substutution scoring matrix from file */

		InputStream is = this.getClass().getResourceAsStream("/"+p_blmat);
	    blmat = read_blosummat( new InputStreamReader(is), alphabet );

		if ( blmat == null ) {
			System.out.printf( "Error. Cannot read BLOSUM matrix from file %s. Exit\n", p_blmat );
		}

		/* Read query FASTA file */

		if ( ( q_fsa = fsalist_read( qr ) ) == null ) {
			System.out.printf("Cannot read fasta file %s\n", qr.toString() );
		}

		q_fsa = fsalist_check_names( q_fsa );

		 /* Assign Blosum alphabet order to variable q_fsa->i */

		fsalist_iassign_profile_order( q_fsa );

		if ( ( db_fsa = fsalist_read( dr ) ) == null ) {
			System.out.printf("Cannot read fasta file %s\n", dr.toString() );
	    }

		db_fsa = fsalist_check_names( db_fsa );

		/* Assign Blosum alphabet order to variable ->i in all faste entries in db_fsa */

		fsalist_iassign_profile_order( db_fsa );

		gapf = p_gapf;
		gapn = p_gapn;

		System.out.printf("# Gap penalties. fgap: %f. ngap: %f\n", gapf, gapn);

		List<ALN>	alnlist = new ArrayList<ALN>();
		for ( d = db_fsa; d != null; d=d.next ) {
			scomat = score_mat( q_fsa, d, blmat );

			neb = align( scomat, q_fsa, d, gapf, gapn );

			if ( neb != null ) alnlist.add( neb );
				//aln_write_single( neb );

					/* Free dymanically allocated memory */

	                //fmatrix_free( scomat, 0, q_fsa->len-1, 0, d->len-1 );
	                //aln_free( neb );

		}
		
		Collections.sort( alnlist );
		//alnlist.get(0).aln_write_single();

		//System.exit(0);
		
		return alnlist;
	}
	
	public static void main(String[] args) {
		try {
			SmithWater sw = new SmithWater();
			sw.fasta_align( args );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
