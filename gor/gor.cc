#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <zlib.h>
#include <map>
#include <vector>

using namespace std;

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
		int newlen = (len * 7) / 8;
    char* out = new char[newlen];
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
	if( cmp >= 0 || d2 == b ){
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
		int hit = i;
		while( cmp > 0 ) {
			while( i < l && dst[i++] != '\n' );
			hit = i;
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
		return hit;
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
	} else {
		return last;
	}
}

unsigned int readUnsignedIntBigEndian(char* byteBuf, int pos) {
    return (((byteBuf[pos] & 0xff) << 24) | ((byteBuf[pos + 1] & 0xff) << 16) | ((byteBuf[pos + 2] & 0xff) << 8) | ((byteBuf[pos + 3] & 0xff) << 0));
}

int readIntBigEndian(char* byteBuf, int pos) {
    return (((byteBuf[pos] & 0xff) << 24) | ((byteBuf[pos + 1] & 0xff) << 16) | ((byteBuf[pos + 2] & 0xff) << 8) | ((byteBuf[pos + 3] & 0xff) << 0));
}

long long readLongBigEndian(char* buf, int offset) {
    return ((long long) readIntBigEndian(buf, offset) << 32) + (readIntBigEndian(buf, offset+4) & 0xFFFFFFFFL);
}

unsigned short readUnsignedShortBigEndian(char* byteBuf, int pos) {
    return (((byteBuf[pos] & 0xff) << 8) | ((byteBuf[pos + 1] & 0xff) << 0));
}

int cntIntDigits(int val) {
    if (val <= 99999) {
        if (val <= 99) {
            return (val <= 9 ? 1 : 2);
        }
        if (val <= 9999) {
            return (val <= 999 ? 3 : 4);
        }
        return 5;
    }
    if (val <= 9999999) {
        return (val <= 999999 ? 6 : 7);
    }
    if (val <= 999999999) {
        return (val <= 99999999 ? 8 : 9);
    }
    return 10;
}

int cntLongDigits(long long val) {
    if (val <= 2000000000/*Integer.MAX_VALUE*/) {
        return cntIntDigits((int)val);
    }

    if (val <= 99999999999999L) {
        if (val <= 99999999999L) {
            return (val <= 9999999999L ? 10 : 11);
        }
        if (val <= 9999999999999L) {
            return (val <= 999999999999L ? 12 : 13);
        }
        return 14;
    }

    if (val <= 9999999999999999L) {
        return (val <= 999999999999999L ? 15 : 16);
    }

    if (val <= 999999999999999999L) {
        return (val <= 99999999999999999L ? 17 : 18);
    }
    return 19;
}

int writeLong(char* dest, int offset, long val) {
    int idx = offset;
    if (val < 0) {
        dest[idx++] = '-';
        if (val == -4000000000L/*Long.MIN_VALUE*/) {
            dest[idx++] = '9';
            dest[idx++] = '2';
            dest[idx++] = '2';
            dest[idx++] = '3';
            dest[idx++] = '3';
            dest[idx++] = '7';
            dest[idx++] = '2';
            dest[idx++] = '0';
            dest[idx++] = '3';
            dest[idx++] = '6';
            dest[idx++] = '8';
            dest[idx++] = '5';
            dest[idx++] = '4';
            dest[idx++] = '7';
            dest[idx++] = '7';
            dest[idx++] = '5';
            dest[idx++] = '8';
            dest[idx++] = '0';
            dest[idx++] = '8';
            return 20;
        }
        val = -val;
    }
    int len = cntLongDigits(val);
    for (int end = idx+len; end > idx;) {
        int mod = (int)(val % 10);
        val /= 10;
        dest[--end] = '0'+mod;
    }
    return (idx-offset) + len;
}

class RowDecoder {
public:
	char* src;
	int		rowcnt;
	RowDecoder() {
		src = NULL;
		rowcnt = 0;
	}
	RowDecoder( char* nsrc, int rc ) {
		src = nsrc;
		rowcnt = rc;
	}
	virtual int decodeNext(char* bytes, int offset) {
		return 0;
	};
	virtual int getReadLen() {
		return 0;
	};
};

class Case3RowDecoder : public RowDecoder {
public:
		int srcpos;
		long long minvalue3;
		Case3RowDecoder( char* src, int rowcnt, long long mv3, int start3 ) : RowDecoder( src, rowcnt ) {
			srcpos = start3;
			minvalue3 = mv3;
		}
		int decodeNext(char* bytes, int offset) override {
				long long value = minvalue3 + readUnsignedIntBigEndian(src, srcpos);
				srcpos += 4;
				int ret = writeLong(bytes, offset, value);
				//printf("ret3 %d val %lld %lld %d\n", ret, value, minvalue3, srcpos);
				return ret;
		}
		int getReadLen() override {
				return 8 + (4*rowcnt); // Long base value + byte per row
		}
};

class Case10RowDecoder : public RowDecoder {
public:
		int srcpos;
		long long lastvalue;
		Case10RowDecoder( char* src, int rowcnt, long long fv10, int start10 ) : RowDecoder( src, rowcnt ) {
			srcpos = start10;
			lastvalue = fv10;
		}
		int decodeNext(char* bytes, int offset) override {
				lastvalue += readUnsignedShortBigEndian(src, srcpos);
				srcpos+=2;
				return writeLong(bytes, offset, lastvalue);
		}
		int getReadLen() override {
				return 8 + (2*rowcnt); // Long base value + short per row
		}
};

class Case11RowDecoder : public RowDecoder {
public:
		int srcpos;
		long long lastvalue;
		Case11RowDecoder( char* src, int rowcnt, long long fv11, int start11 ) : RowDecoder( src, rowcnt ) {
			srcpos = start11;
			lastvalue = fv11;
		}
		int decodeNext(char* bytes, int offset) override {
				lastvalue += readUnsignedIntBigEndian(src, srcpos);
				srcpos += 4;
				int ret = writeLong(bytes, offset, lastvalue);
				//printf("ret11 %d\n", ret);
				return ret;
		}
		int getReadLen() override {
				return 8 + (4*rowcnt); // Long base value + byte per row
		}
};

class Case21RowDecoder : public RowDecoder {
public:
		int srcpos;
		Case21RowDecoder( char* src, int readlen, int start21 ) : RowDecoder( src, readlen ) {
				srcpos = start21;
		}
		int decodeNext(char* bytes, int offset) override {
				int begin = srcpos;
				while (src[srcpos] != 0) { srcpos++; /* find zero termination */}
				int length = (srcpos-begin);
				memcpy(bytes+offset, src+begin, length);
				srcpos++;
				return length;
		}
		int getReadLen() override {
				return rowcnt + 4; // Number of bytes for varchars plus the byte counter
		}
};

class Case23RowDecoder : public RowDecoder {
public:
		int begin;
		Case23RowDecoder( char* src, int length, int b ) : RowDecoder( src, length ) {
				begin = b;
		}
		int decodeNext(char* bytes, int offset) override {
			  memcpy(bytes+offset, src+begin, rowcnt);
				return rowcnt;
		}
		int getReadLen() override {
				return rowcnt+1; // the constant length plus the terminating zero
		}
};

RowDecoder* getDecoder(char* src, int pos, int type, int rowcnt, map<int, char*> * mapColExternalTable) {
		switch (type) {
        case 3: // Int offset, i.e. minimum value is written as long and the rest of the values as value-min (which is guaranteed to fit in 32 bits)
        {
				    unsigned int minvalue3 = readLongBigEndian(src, pos);//*(long long*)(src+pos);
            int start3 = pos + 8;
            Case3RowDecoder *ret = new Case3RowDecoder( src, rowcnt, minvalue3, start3 );
						return ret;
				}

        /*case 4: // Short offset, i.e. minimum value is written as long and the rest of the values as value-min (which is guaranteed to fit in 16 bits)
            final long minvalue4 = GByteArray.readLongBigEndian(src, pos);
            final int start4 = pos + 8;
            return new RowDecoder() {
                int srcpos = start4;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final long value = minvalue4 + GByteArray.readUnsignedShortBigEndian(src, srcpos);
                    srcpos += 2;
                    return ByteTextBuilder.writeLong(bytes, offset, value);
                }
                @Override final int getReadLen() {
                    return 8 + (2*rowcnt); // Long base value + byte per row
                }
            };

        case 5: // Byte offset, i.e. minimum value is written as long and the rest of the values as value-min (which is guaranteed to fit in 8 bits)
            final long minvalue5 = GByteArray.readLongBigEndian(src, pos);
            final int start5 = pos + 8;
            return new RowDecoder() {
                int srcpos = start5;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final long value = minvalue5 + GByteArray.readUnsignedByte(src, srcpos++);
                    return ByteTextBuilder.writeLong(bytes, offset, value);
                }
                @Override final int getReadLen() {
                    return 8 + rowcnt; // Long base value + byte per row
                }
            };

        case 7: // Incremental values, i.e. a basevalue and the step size to generate the value of each row
            final long basevalue = GByteArray.readLongBigEndian(src, pos);
            final int incr = GByteArray.readIntBigEndian(src, pos+8);
            return new RowDecoder() {
                private long next = basevalue;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    next += incr;
                    return ByteTextBuilder.writeLong(bytes, offset, next);
                }
                @Override final int getReadLen() {
                    return 8 + 4; // Long base value + int increment
                }
            };

        case 9: // Byte diff, i.e. first value is written as long and the rest of the values as value-last_value (which is guaranteed to fit in 8 bits)
            final long firstvalue9 = GByteArray.readLongBigEndian(src, pos);
            final int start9 = pos + 8;
            return new RowDecoder() {
                int srcpos = start9;
                long lastvalue = firstvalue9;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    lastvalue += GByteArray.readUnsignedByte(src, srcpos++);
                    return ByteTextBuilder.writeLong(bytes, offset, lastvalue);
                }
                @Override final int getReadLen() {
                    return 8 + rowcnt; // Long base value + byte per row
                }
            };*/

        case 10: // Short diff, i.e. first value is written as long and the rest of the values as value-last_value (which is guaranteed to fit in 16 bits)
				{
						long long firstvalue10 = readLongBigEndian(src, pos);
          	int start10 = pos + 8;
            return new Case10RowDecoder(src,rowcnt,firstvalue10,start10);
				}
        case 11: // Int diff, i.e. first value is written as long and the rest of the values as value-last_value (which is guaranteed to fit in 32 bits)
        {
				    long long firstvalue11 = readLongBigEndian(src, pos);//*(long long*)(src+pos);
            int start11 = pos + 8;
						Case11RowDecoder* ret = new Case11RowDecoder( src, rowcnt, firstvalue11, start11 );
						return ret;
				}
            /*return RowDecoder {
                int srcpos = start11;
                long long lastvalue = firstvalue11;
                int decodeNext(char* bytes, int offset) {
                    lastvalue += *(unsigned int*)(src+srcpos);
                    srcpos+=4;
                    return writeLong(bytes, offset, lastvalue);
                }
                int getReadLen() {
                    return 8 + (4*rowcnt); // Long base value + short per row
                }
            };*/

        /*case 12: // Byte diff, i.e. first value is written as long and the rest of the values as value-last_value (which is guaranteed to fit in 8 bits)
            final long firstvalue12 = GByteArray.readLongBigEndian(src, pos);
            final int start12 = pos + 8;
            return new RowDecoder() {
                int srcpos = start12;
                long lastvalue = firstvalue12;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    lastvalue += src[srcpos++];
                    return ByteTextBuilder.writeLong(bytes, offset, lastvalue);
                }
                @Override final int getReadLen() {
                    return 8 + rowcnt; // Long base value + byte per row
                }
            };

        case 13: // Short diff, i.e. first value is written as long and the rest of the values as value-last_value (which is guaranteed to fit in 16 bits)
            final long firstvalue13 = GByteArray.readLongBigEndian(src, pos);
            final int start13 = pos + 8;
            return new RowDecoder() {
                int srcpos = start13;
                long lastvalue = firstvalue13;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    lastvalue += GByteArray.readShortBigEndian(src, srcpos);
                    srcpos+=2;
                    return ByteTextBuilder.writeLong(bytes, offset, lastvalue);
                }
                @Override final int getReadLen() {
                    return 8 + (2*rowcnt); // Long base value + short per row
                }
            };

        case 19: // Empty column
            return new RowDecoder() {
                @Override final int decodeNext(byte[] bytes, int offset) {
                    return 0;
                }
                @Override final int getReadLen() {
                    return 0; // No data written for empty columns
                }
            };*/

        case 21: // Varchar sequence, i.e. sequence of zero terminated string, one per row
				{
				    int readlen = readIntBigEndian(src, pos);//*(int*)(src+pos);
            int start21 = pos+4;
            Case21RowDecoder *ret = new Case21RowDecoder( src, readlen, start21 );
						return ret;
				}

        /*case 22: // Text lookup, i.e. a sequence of zero terminated strings forming a lookup table, followed by byte ref per row
            final int start22 = pos;
            final int cnt = GByteArray.readUnsignedByte(src, pos++);
            final int[] begins = new int[cnt];
            final int[] lengths = new int[cnt];
            for (int i = 0; i < cnt; i++) {
                begins[i] = pos;
                while (src[pos] != 0) { pos++; }
                lengths[i] = (pos-begins[i]);
                pos++; // Increment past zero termination
            }
            final int refbegin = pos;

            return new RowDecoder() {
                private int next = refbegin;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final int ref = GByteArray.readUnsignedByte(src, next++);
                    System.arraycopy(src, begins[ref], bytes, offset, lengths[ref]);
                    return lengths[ref];
                }
                @Override final int getReadLen() {
                    return (refbegin-start22) + rowcnt; // table length + byte per entry
                }
            };*/

        case 23: // Sametext, i.e. constant text for each row
				{
            int begin = pos;
            while (src[pos] != 0) { pos++; }
            int length = pos - begin;
            Case23RowDecoder *ret = new Case23RowDecoder( src, length, begin );
						return ret;
				}

        /*case 24: // External table, i.e. byte ref per row, refering to an externally stored table
            final int refbegin24 = pos;
            return new RowDecoder() {
                private int next = refbegin24;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final int ref = GByteArray.readUnsignedByte(src, next++);
                    final byte[] text = mapColExternalTable.get(ref);
                    if (text == null) return 0;
                    System.arraycopy(text, 0, bytes, offset, text.length);
                    return text.length;
                }
                @Override final int getReadLen() {
                    return rowcnt; // byte per entry
                }
            };

        case 25: // External table, i.e. byte ref per row, diff from last value refering to an externally stored table
            final int refbegin25 = pos;
            return new RowDecoder() {
                private int lastval = 0;
                private int next = refbegin25;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final int diff = src[next++];
                    lastval += diff;
                    final byte[] text = mapColExternalTable.get(lastval);
                    if (text == null) return 0;
                    System.arraycopy(text, 0, bytes, offset, text.length);
                    return text.length;
                }
                @Override final int getReadLen() {
                    return rowcnt; // byte per entry
                }
            };

        case 28: // Text lookup with diff keys, i.e. byte ref per row, diff from last value refering to an externally stored table
            final int start28 = pos;
            final int cnt28 = GByteArray.readUnsignedByte(src, pos++);
            final int[] begins28 = new int[cnt28];
            final int[] lengths28 = new int[cnt28];
            for (int i = 0; i < cnt28; i++) {
                begins28[i] = pos;
                while (src[pos] != 0) { pos++; }
                lengths28[i] = (pos-begins28[i]);
                pos++; // Increment past zero termination
            }

            final int refbegin28 = pos;
            return new RowDecoder() {
                private int lastval = 0;
                private int next = refbegin28;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final int diff = src[next++];
                    lastval += diff;
                    System.arraycopy(src, begins28[lastval], bytes, offset, lengths28[lastval]);
                    return lengths28[lastval];
                }
                @Override final int getReadLen() {
                    return (refbegin28-start28) + rowcnt; // table length + byte per entry
                }
            };

        case 29: // External table, i.e. short ref per row, diff from last value refering to an externally stored table
            final int refbegin29 = pos;
            return new RowDecoder() {
                private int index = 0; // First value is the actual value, so by adding zero we get that value
                private int next = refbegin29;
                @Override final int decodeNext(byte[] bytes, int offset) {
                    final int diff = GByteArray.readShortBigEndian(src, next);
                    next += 2;
                    index += diff; // Index will always be the sum of last index and the difference between them
                    final byte[] text = mapColExternalTable.get(index);
                    if (text != null) {
                        System.arraycopy(text, 0, bytes, offset, text.length);
                        return text.length;
                    }
                    return 0;
                }
                @Override final int getReadLen() {
                    return rowcnt*2; // short per entry
                }
            };*/


        default:
				{
						printf("sko %d\n", type);
            exit(-1);
				}
						//throw new RuntimeException("Unexpected data type " + type + " when decoding packed block");*/
    }
}

int decode(char* src, int off, char* dest, int destOffset, map<int, map<int, char*>> * mapExternalTables) {
    // Read the rowcnt
    unsigned short rowcnt = readUnsignedShortBigEndian(src, off);//*((unsigned short*)(src+off));
    // Read the types
    int pos = off+2;
		vector<int> types;
    while (src[pos] != 0) {
        types.push_back(src[pos++]);
    }
    pos++; // Increment past the zero termination

		RowDecoder *decoders[types.size()];
    for (int i = 0; i < types.size(); i++) {
        int type = types[i];
        decoders[i] = getDecoder(src, pos, type, rowcnt, NULL);//mapExternalTables[i]);
				int readLen = decoders[i]->getReadLen();
        pos += readLen;
    }
    // Write the decoded block into the destination buffer
    int dp = destOffset;
    int colcnt = types.size();
    if (colcnt != 0) {
        for (int i = 0; i < rowcnt; i++) {
					  dp += decoders[0]->decodeNext(dest, dp);
						for (int j = 1; j < colcnt; j++) {
                dest[dp++] = '\t';
                dp += decoders[j]->decodeNext(dest, dp);
						}
            dest[dp++] = '\n';
        }
    }
		return dp;
}

int main( int argc, char* argv[] ) {
	if( strcmp( argv[1], "-p" ) == 0 ) {
		char	buffer[100000];
		char	dst[100000];
		char	zbuf[100000];

		char* chr;
		int s = 0;
		int e = 1000000000;
		int filestart;
		if( argv[2][0] == 'c' && argv[2][1] == 'h' && argv[2][2] == 'r' ) {
			filestart = 3;
			chr = argv[2];
			int k = 0;
			while( chr[k] != 0 && chr[k] != ':' ) k++;
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
		} else {
			filestart = 2;
			size_t sizeloc = 0;
			getline( &chr, &sizeloc, stdin );
			char* sloc = chr;
			while( *sloc != '\t' ) sloc++;
			*sloc = 0;
			sloc++;
			char* eloc = sloc;
			while( *eloc != '\t' ) eloc++;
			*eloc = 0;
			s = atoi(sloc);
			eloc++;
		}

		FILE* f = fopen( argv[filestart], "r" );
		int r = fread( buffer, 1, 1000, f );

		int z = -1;
		int i = 0;
		while( buffer[i++] != '\n' ) {
			if( buffer[i] == 0 ) {
				z = i;
			}
		}

		if( z != -1 ) {
			buffer[z] = '\n';
			fwrite( buffer, 1, z+1, stdout );
			int len = i-z-2;
			int reslen = to8BitInplace( buffer, z+1, len );
			int inf = inflate( buffer+z+1, reslen, dst, sizeof(dst) );
			//fwrite( dst, 1, inf, stdout );
		} else fwrite( buffer, 1, i, stdout );

		int m = fseek( f, 0L, SEEK_END );
		long filesize = ftell( f );

		int l = sizeof(buffer);
		searchresult loc = search( f, buffer, l, chr, s, i, i, filesize );
		if( loc.chr != NULL ) {
			int beg = loc.start;
			int end = loc.end;
			int tpos = loc.pos;
			int siz = loc.siz;
			int i = end;

			int stopval = z != -1 ? 1 : 0;

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
				while( i < siz && buffer[i++] != stopval );
				if( i == siz ) {
					end = siz-beg;
					memcpy( buffer, buffer+beg, end );
					r = fread( buffer+end, 1, beg, f );
					beg = 0;
					i = end;
					while( /*i < r &&*/ buffer[i++] != stopval );
				}

				while( buffer[--i] != '\n' );
				end = i;
			}

			int len = to8BitInplace( buffer, beg, end-beg );
			int inf = inflate( buffer+beg, len, dst, sizeof(dst) );

			int start, endpos;
			if( z != -1 ) {
				inf = decode( dst, 0, zbuf, 0, NULL );
				start = searchUnzipped( zbuf, inf, 0, inf, chr, s, 0, 0 );
				endpos = searchUnzipped( zbuf, inf, start, inf, chr, e, 0, 0 );
			} else {
				start = searchUnzipped( dst, inf, 0, inf, chr, s, 0, 0 );
				endpos = searchUnzipped( dst, inf, start, inf, chr, e, 0, 0 );
			}

			while( endpos == inf ) {
				bool startnotfound = start == inf;
				if( !startnotfound ) {
					if( z != -1 ) {
						fwrite( zbuf+start, 1, endpos-start, stdout );
					} else {
						fwrite( dst+start, 1, endpos-start, stdout );
					}
				}

				while( i < siz && buffer[i++] != stopval );
				beg = i;
				while( i < siz && buffer[i++] != stopval );

				if( i == l && buffer[i-1] != '\n' ) {
					end = l-beg;
					memcpy( buffer, buffer+beg, end );
					r = fread( buffer+end, 1, beg, f ) + end;
					i = end;
					while( /*i < l &&*/ buffer[i++] != stopval );
					beg = 0;
				}

				while( buffer[--i] != '\n' );
				end = i;

				len = to8BitInplace( buffer, beg, end-beg );
				inf = inflate( buffer+beg, len, dst, sizeof(dst) );
				if( inf > 0 ) {
					if( z != -1 ) {
						inf = decode( dst, 0, zbuf, 0, NULL );
						start = startnotfound ? searchUnzipped( zbuf, inf, 0, inf, chr, s, 0, 0 ) : 0;
						endpos = searchUnzipped( zbuf, inf, start, inf, chr, e, 0, 0 );
					} else {
						start = startnotfound ? searchUnzipped( dst, inf, 0, inf, chr, s, 0, 0 ) : 0;
						endpos = searchUnzipped( dst, inf, start, inf, chr, e, 0, 0 );
					}
				}
			}
			if( endpos > 0 && endpos > start ) {
				if( z != -1 ) {
					fwrite( zbuf+start, 1, endpos-start, stdout );
				} else {
					fwrite( dst+start, 1, endpos-start, stdout );
				}
			}
		}
		fclose( f );
	} else {
		char	buffer[1000000];
		char	zbuf[100000];
		char	dst[100000];

		FILE* f = fopen( argv[1], "r" );
		int l = sizeof(buffer);
		int r = fread( buffer, 1, l, f );
		int i = 0;
		int z = -1;
		while( buffer[i++] != '\n' ) {
			if( buffer[i] == 0 ) {
				z = i;
			}
		}
		if( z != -1 ) {
			buffer[z] = '\n';
			fwrite( buffer, 1, z+1, stdout );
			int len = i-z-2;
			int reslen = to8BitInplace( buffer, z+1, len );
			//fprintf( stdout, "%d %d %d\n", buffer[z+1], buffer[z+2], buffer[z+3] );
			int inf = inflate( buffer+z+1, reslen, dst, sizeof(dst) );
			//fprintf( stdout, "%d %d %d\n", len, reslen, inf );
			fwrite( dst, 1, inf, stdout );
			//exit(0);
		} else fwrite( buffer, 1, i, stdout );

		int stopval = z != -1 ? 1 : 0;
		//printf("bleh %d %d %d\n", i, r, l );
		while( i < r-1 || r == l ) {
			while( i < r && buffer[i++] != stopval );
			int start = i;
			while( i < r && buffer[i++] != stopval );

			//printf("%d\n", start);

			if( i == l && buffer[i-1] != '\n' ) {
				int end = l-start;
				memcpy( buffer, buffer+start, end );
				r = fread( buffer+end, 1, start, f ) + end;
				i = end;
				while( /*i < l &&*/ buffer[i++] != stopval );
				start = 0;
			}

			while( buffer[--i] != '\n' );
			int end = i;

			int prelen = end-start;
			int len = to8BitInplace( buffer, start, end-start );
			int inf = inflate( buffer+start, len, dst, sizeof(dst) );
			//printf("%d %d %d %d %d %d\n", dst[0], dst[1], dst[2], dst[3], inf, len);
			//printf("bleh %d %d %d %d %d\n", prelen, len, inf, start, end);
			if( inf > 0 ) {
				if( z != -1 ) {
					int siz = decode( dst, 0, zbuf, 0, NULL );
					fwrite( zbuf, 1, siz, stdout );
				} else {
					fwrite( dst, 1, inf, stdout );
				}
			}
		}
		fclose( f );
	}
	return 0;
}
