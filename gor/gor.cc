#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <zlib.h>

int inflate(const void *src, int srcLen, void *dst, int dstLen) {
    z_stream strm  = {0};
    strm.total_in  = strm.avail_in  = srcLen;
    strm.total_out = strm.avail_out = dstLen;
    strm.next_in   = (Bytef *) src;
    strm.next_out  = (Bytef *) dst;

    strm.zalloc = Z_NULL;
    strm.zfree  = Z_NULL;
    strm.opaque = Z_NULL;

    int err = -1;
    int ret = -1;

    err = inflateInit2(&strm, (15 + 32));
		//15 window bits, and the +32 tells zlib to to detect if using gzip or zlib
    if (err == Z_OK) {
        err = inflate(&strm, Z_FINISH);
        if (err == Z_STREAM_END) {
            ret = strm.total_out;
        }
        else {
             inflateEnd(&strm);
             return err;
        }
    } else {
        inflateEnd(&strm);
        return err;
    }

    inflateEnd(&strm);
    return ret;
}

int to8BitInplace(char* ba, int off, int length) {
    int end = off+length-1;
    int bit = 0, readPos = off, writePos = off;
    while (readPos < end) {
        char b1 = (ba[readPos] - 33);
        char b2 = (ba[++readPos] - 33);
        ba[writePos++] = (((b1 & 0xff) >> bit)
                | (((b2 & 0xff) << (7 - bit))));
        if (++bit == 7) {
            bit = 0;
            ++readPos;
        }
    }
    return writePos-off;
}

char* to8Bit(char* ba, int balen) {
    int len = balen;
    char* out = new char[(len * 7) / 8];
    int bit = 0, readPos = 0, writePos = 0;
    while (readPos < len - 1) {
        char b1 = (ba[readPos] - 33);
        char b2 = (ba[++readPos] - 33);
        out[writePos++] = (((b1 & 0xff) >> bit)
                | (((b2 & 0xff) << (7 - bit))));
        if (++bit == 7) {
            bit = 0;
            ++readPos;
        }
    }
    return out;
}

struct searchresult {
	long	fileoffset;
	int 	siz;
	char* chr;
	int		pos;
	int		start;
	int		end;
};

#define min(a,b) (((a)<(b))?(a):(b))
#define max(a,b) (((a)>(b))?(a):(b))

inline int compare( char* chr, int pos, char* buffer, int & i, int & tpos, int & zeropos ) {
	zeropos = i--;

	int store = i;
	buffer[store] = 0;
	while( i > 0 && buffer[--i] != '\t' );
	tpos = atoi( buffer+i+1 );
	buffer[store] = '\t';

	store = i;
	buffer[store] = 0;
	while( i > 0 && buffer[--i] != '\n' );
	int scmp = strcmp( chr, buffer+i+1 );
	buffer[store] = '\t';

	return scmp != 0 ? scmp : pos - tpos;
}

searchresult search( FILE* f, char buffer[], int buflen, char* chr, int pos, long b, long s, long e ) {
	long l = e-s;
	long d = (e+s)/2;
	long d2 = max( b, (e+s-buflen)/2 );

	int m = fseek( f, d2, SEEK_SET );
	int r = fread( buffer, 1, buflen, f );

	int i = min(buflen,r);
	int zeropos = 0;
	int tpos = 0;
	while( i > 0 && buffer[--i] != 0 );
	int cmp = compare( chr, pos, buffer, i, tpos, zeropos );

	if( cmp >= 0 ) {
		if( cmp == 0 ) {
			searchresult sr;
			sr.chr = (char*)buffer+i+1;
			sr.siz = r;
			sr.pos = tpos;
			sr.end = -1;
			sr.start = zeropos+2;
			return sr;
		} else if( r < buflen ) {
			searchresult sr;
			sr.chr = NULL;
			return sr;
		} else {
			return search( f, buffer, buflen, chr, pos, b, d, e );
		}
	}

	int tcount = 0;
	i = 0;
	while( i < r && buffer[i] != 0 && tcount < 2 ) {
		if( buffer[i] == '\t' ) tcount++;
		else if( buffer[i] == 0 ) tcount = 0;
		i++;
	}
	if( zeropos != i ) cmp = compare( chr, pos, buffer, i, tpos, zeropos );
	if( cmp >= 0 || d2 == b ) {
		searchresult sr;
		sr.chr = (char*)buffer+i+1;
		sr.siz = r;
		sr.pos = tpos;
		i = zeropos+1;
		sr.start = i;
		while( i < r && buffer[i++] != '\n' );
		sr.end = i-1;

		return sr;
	} else {
		return search( f, buffer, buflen, chr, pos, b, s, d );
	}
}

int searchUnzipped( char* dst, int l, int s, int e, char* chr, int pos, int last, int cmp ) {
	int d = (s+e)/2;

	int i = d;
	while( i > 0 && dst[--i] != '\n' );
	if( dst[i] == '\n' ) i++;
	int zeropos = i;

	if( i == last ) {
		//if( cmp > 0 )
		while( cmp > 0 ) {
			while( i < l && dst[i++] != '\n' );
			if( i < l ) {
				while( dst[i++] != '\t' );
				char* spos = dst+i;
				while( dst[++i] != '\t' );
				dst[i] = 0;
				cmp = pos-atoi(spos);
				dst[i] = '\t';
			} else {
				break;
			}
		}/*	else if( cmp < 0 ) {
			int k = i - 2;
			while( k > 0 && dst[k] != '\n' ) k--;
			if( k == 0 ) return k;
		}*/
		return i;
	} else last = i;

	char* schr = dst+i;
	while( dst[++i] != '\t' );

	dst[i] = 0;
	int scmp = strcmp( chr, schr );
	dst[i] = '\t';

	char* spos = dst+i+1;
	while( dst[++i] != '\t' );

	dst[i] = 0;
	int tpos = atoi( spos );
	dst[i] = '\t';

	cmp = scmp != 0 ? scmp : pos - tpos;

	if( cmp > 0 ) {
		return searchUnzipped( dst, l, zeropos, e, chr, pos, last, cmp );
	} else if( cmp < 0 ) {
		return searchUnzipped( dst, l, s, zeropos-1, chr, pos, last, cmp );
	} else return last;
}

int main( int argc, char* argv[] ) {
	if( strcmp( argv[1], "-p" ) == 0 ) {
		char	buffer[100000];
		char	dst[100000];

		FILE* f = fopen( argv[3], "r" );
		int r = fread( buffer, 1, 1000, f );

		int i = 0;
		while( buffer[i++] != '\n' );

		fwrite( buffer, 1, i, stdout );

		int m = fseek( f, 0L, SEEK_END );
		long filesize = ftell( f );
		char* chr = argv[2];
		int k = 0;
		while( chr[k] != 0 && chr[k] != ':' ) k++;
		int s = 0;
		int e = 1000000000;
		if( chr[k] == ':' ) {
			chr[k] = 0;
			k++;
			char* pos = chr+k;
			while( chr[k] != 0 && chr[k] != '-' ) k++;
			if( chr[k] == '-' ) {
				chr[k] = 0;
				char* end = chr+k+1;
				e = atoi(end);
			}
			s = atoi(pos);
		}

		int l = sizeof(buffer);
		searchresult loc = search( f, buffer, l, chr, s, i, i, filesize );
		if( loc.chr != NULL ) {
			int beg = loc.start;
			int end = loc.end;
			int tpos = loc.pos;
			int siz = loc.siz;
			int i = end;

			int scmp = strcmp( chr, loc.chr );
			int cmp = scmp != 0 ? scmp : s - tpos;
			while( cmp >= 0 ) {
				const char* schr = buffer+i+1;
				while( i < siz && buffer[++i] != '\t' );
				buffer[i] = 0;
				const char* spos = buffer+i+1;
				while( i < siz && buffer[++i] != '\t' );
				buffer[i] = 0;
				tpos = atoi( spos );

				int scmp = strcmp( chr, schr );
				cmp = scmp != 0 ? scmp : s - tpos;
				i += 2;
				beg = i;
				while( i < siz && buffer[i++] != 0 );
				if( i == siz ) {
					end = siz-beg;
					memcpy( buffer, buffer+beg, end );
					r = fread( buffer+end, 1, beg, f );
					beg = 0;
					i = end;
					while( /*i < r &&*/ buffer[i++] != 0 );
				}

				while( buffer[--i] != '\n' );
				end = i;
			}

			int len = to8BitInplace( buffer, beg, end-beg );
			int inf = inflate( buffer+beg, len, dst, sizeof(dst) );

			int start = searchUnzipped( dst, inf, 0, inf, chr, s, 0, 0 );
			int endpos = searchUnzipped( dst, inf, start, inf, chr, e, 0, 0 );

			while( endpos == inf ) {
					bool startnotfound = start == inf;
					if( !startnotfound ) fwrite( dst+start, 1, endpos-start, stdout );

					while( i < siz && buffer[i++] != 0 );
					beg = i;
					while( i < siz && buffer[i++] != 0 );

					if( i == l && buffer[i-1] != '\n' ) {
						end = l-beg;
						memcpy( buffer, buffer+beg, end );
						r = fread( buffer+end, 1, beg, f ) + end;
						i = end;
						while( /*i < l &&*/ buffer[i++] != 0 );
						beg = 0;
					}

					while( buffer[--i] != '\n' );
					end = i;

					len = to8BitInplace( buffer, beg, end-beg );
					inf = inflate( buffer+beg, len, dst, sizeof(dst) );

					start = startnotfound ? searchUnzipped( dst, inf, 0, inf, chr, s, 0, 0 ) : 0;
					endpos = searchUnzipped( dst, inf, start, inf, chr, e, 0, 0 );
			}
			if( endpos > 0 && endpos > start ) fwrite( dst+start, 1, endpos-start, stdout );
		}
		fclose( f );
	} else {
		char	buffer[1000000];
		char	dst[100000];

		FILE* f = fopen( argv[1], "r" );
		int l = sizeof(buffer);
		int r = fread( buffer, 1, l, f );
		int i = 0;
		while( buffer[i++] != '\n' );
		fwrite( buffer, 1, i, stdout );
		while( i < r-1 || r == l ) {
			while( i < r && buffer[i++] != 0 );
			int start = i;
			while( i < r && buffer[i++] != 0 );

			if( i == l && buffer[i-1] != '\n' ) {
				int end = l-start;
				memcpy( buffer, buffer+start, end );
				r = fread( buffer+end, 1, start, f ) + end;
				i = end;
				while( /*i < l &&*/ buffer[i++] != 0 );
				start = 0;
			}

			while( buffer[--i] != '\n' );
			int end = i;

			int len = to8BitInplace( buffer, start, end-start );
			int inf = inflate( buffer+start, len, dst, sizeof(dst) );

			if( inf > 0 ) fwrite( dst, 1, inf, stdout );
		}
		fclose( f );
	}
	return 0;
}
