#include "simlab.h"
#include <cstdio>

template <typename T, typename K, typename U> class c_shift {
public:
	c_shift( T buffer, int length, K chunk, int clen, U shift, int ulen ) {}
};

extern int _gcd( int a, int b );/* {
	if( b == 0 ) return a;
	else return _gcd( b, a%b );
}*/

template <typename T, typename K, typename U> class c_shift<T*,K,U> {
public:
	c_shift( T* buffer, int length, K chk, int clen, U sft, int ulen ) {
		int u = 0;
		int chunk = 0;

		for( int r = 0; r < length; r+=chunk ) {
			//printf( "%d %d %d\n", chunk, shift, ec );

			chunk = chk[u];
			int shift = sft[u];
			u++;

			shift %= chunk;
			if( shift < 0 ) shift = chunk + shift;
			int ec = _gcd( shift, chunk );

			T* rdata = &buffer[r];
			for( int i = 0; i < ec; i++ ) {
				int k = i;
				T	tmp1;
				T	tmp2 = rdata[k];
				do {
					tmp1 = tmp2;
					k = (k+shift)%chunk;
					tmp2 = rdata[k];
					rdata[k] = tmp1;
				} while( k != i );
			}
		}
	}
};

template <typename T, typename K, typename U> class c_shift<c_simlab<T&>&,K,U> {
public:
	c_shift( c_simlab<T&> & buffer, int length, K chk, int clen, U sft, int ulen ) {
		int u = 0;
		int chunk = chk[u];
		int shift = sft[u];
		u++;

		shift %= chunk;
		if( shift < 0 ) shift = chunk + shift;
		int ec = _gcd( shift, chunk );

		for( int r = 0; r < length; r+=chunk ) {
			//T* rdata = &buffer[r];
			for( int i = 0; i < ec; i++ ) {
				int k = i;
				T	tmp1;
				T	tmp2 = buffer[r+k];
				do {
					tmp1 = tmp2;
					k = (k+shift)%chunk;
					tmp2 = buffer[r+k];
					buffer[r+k] = tmp1;
				} while( k != i );
			}

			chunk = chk[u];
			shift = sft[u];
			u++;

			shift %= chunk;
			if( shift < 0 ) shift = chunk + shift;
			ec = _gcd( shift, chunk );
		}
	}
};

template<template<typename T, typename K, typename T3> class c_func, typename T, typename K> void triplestep3( T buffer, int length, K kbuffer, int klength, simlab & value ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			c_func< T,K,c_simlab<unsigned int>& >( buffer, length, kbuffer, klength, sl, 1 );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			c_func< T,K,c_simlab<int>& >( buffer, length, kbuffer, klength, sl, 1 );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			c_func< T,K,c_simlab<float>& >( buffer, length, kbuffer, klength, sl, 1 );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			c_func< T,K,c_simlab<unsigned long long>& >( buffer, length, kbuffer, klength, sl, 1 );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			c_func< T,K,c_simlab<long long>& >( buffer, length, kbuffer, klength, sl, 1 );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			c_func< T,K,c_simlab<double>& >( buffer, length, kbuffer, klength, sl, 1 );
		}
	} else if( value.length == -1 ) {
		/*if( value.type == -66 ) step3< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length );
		else if( value.type == -65 ) step3< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length );
		else if( value.type == -64 ) step3< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length );
		else if( value.type == -34 ) step3< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length );
		else if( value.type == -33 ) step3< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), value.length );
		else if( value.type == -32 ) step3< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), value.length );
		else if( value.type == -16 ) step3< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length );*/
	} else {
		if( value.type == 66 ) c_func< T,K,double* >( buffer, length, kbuffer, klength, (double*)value.buffer, value.length );
		else if( value.type == 65 ) c_func< T,K,long long* >( buffer, length, kbuffer, klength, (long long*)value.buffer, value.length );
		else if( value.type == 64 ) c_func< T,K,unsigned long long* >( buffer, length, kbuffer, klength, (unsigned long long*)value.buffer, value.length );
		else if( value.type == 34 ) c_func< T,K,float* >( buffer, length, kbuffer, klength, (float*)value.buffer, value.length );
		else if( value.type == 33 ) c_func< T,K,int* >( buffer, length, kbuffer, klength, (int*)value.buffer, value.length );
		else if( value.type == 32 ) c_func< T,K,unsigned int* >( buffer, length, kbuffer, klength, (unsigned int*)value.buffer, value.length );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) c_func< T,K,short* >( buffer, length, kbuffer, klength, (short*)value.buffer, value.length );
		else if( value.type == 16 ) c_func< T,K,unsigned short* >( buffer, length, kbuffer, klength, (unsigned short*)value.buffer, value.length );
		else if( value.type == 9 ) c_func< T,K,char* >( buffer, length, kbuffer, klength, (char*)value.buffer, value.length );
		else if( value.type == 8 ) c_func< T,K,unsigned char* >( buffer, length, kbuffer, klength, (unsigned char*)value.buffer, value.length );
	}
}

template<template<typename T1, typename T2, typename T3> class c_func, typename T> void triplestep2( T buffer, int length, simlab & value, simlab & last ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			triplestep3< c_func, T, c_simlab<unsigned int>& >( buffer, length, sl, 1, last );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			triplestep3< c_func, T, c_simlab<int>& >( buffer, length, sl, 1, last );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			triplestep3< c_func, T, c_simlab<float>& >( buffer, length, sl, 1, last );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			triplestep3< c_func, T, c_simlab<unsigned long long>& >( buffer, length, sl, 1, last );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			triplestep3< c_func, T, c_simlab<long long>& >( buffer, length, sl, 1, last );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			triplestep3< c_func, T, c_simlab<double>& >( buffer, length, sl, 1, last );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) triplestep3< c_func, T, c_simlab<double>& >( buffer, length, *(c_simlab<double>*)value.buffer, value.length, last );
		else if( value.type == -65 ) triplestep3< c_func, T, c_simlab<long long>& >( buffer, length, *((c_simlab<long long>*)value.buffer), value.length, last );
		else if( value.type == -64 ) triplestep3< c_func, T, c_simlab<unsigned long long>& >( buffer, length, *((c_simlab<unsigned long long>*)value.buffer), value.length, last );
		else if( value.type == -34 ) triplestep3< c_func, T, c_simlab<float>& >( buffer, length, *((c_simlab<float>*)value.buffer), value.length, last );
		else if( value.type == -33 ) triplestep3< c_func, T, c_simlab<int>& >( buffer, length, *((c_simlab<int>*)value.buffer), value.length, last );
		else if( value.type == -32 ) triplestep3< c_func, T, c_simlab<unsigned int>& >( buffer, length, *((c_simlab<unsigned int>*)value.buffer), value.length, last );
		else if( value.type == -16 ) triplestep3< c_func, T, c_simlab<short>& >( buffer, length, *((c_simlab<short>*)value.buffer), value.length, last );
	} else {
		if( value.type == 66 ) triplestep3< c_func, T, double* >( buffer, length, (double*)value.buffer, value.length, last );
		else if( value.type == 65 ) triplestep3< c_func, T, long long* >( buffer, length, (long long*)value.buffer, value.length, last );
		else if( value.type == 64 ) triplestep3< c_func, T, unsigned long long* >( buffer, length, (unsigned long long*)value.buffer, value.length, last );
		else if( value.type == 34 ) triplestep3< c_func, T, float* >( buffer, length, (float*)value.buffer, value.length, last );
		else if( value.type == 33 ) triplestep3< c_func, T, int* >( buffer, length, (int*)value.buffer, value.length, last );
		else if( value.type == 32 ) triplestep3< c_func, T, unsigned int* >( buffer, length, (unsigned int*)value.buffer, value.length, last );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) triplestep3< c_func, T, short* >( buffer, length, (short*)value.buffer, value.length, last );
		else if( value.type == 16 ) triplestep3< c_func, T, unsigned short* >( buffer, length, (unsigned short*)value.buffer, value.length, last );
		else if( value.type == 9 ) triplestep3< c_func, T, char* >( buffer, length, (char*)value.buffer, value.length, last );
		else if( value.type == 8 ) triplestep3< c_func, T, unsigned char* >( buffer, length, (unsigned char*)value.buffer, value.length, last );
	}
}

template<template<typename T1, typename T2, typename T3> class c_func> void triplestep1( simlab & value, simlab & next, simlab & last ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			triplestep2< c_func, c_simlab<unsigned int>& >( sl, 1, next, last );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			triplestep2< c_func, c_simlab<int>& >( sl, 1, next, last );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			triplestep2< c_func, c_simlab<float>& >( sl, 1, next, last );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			triplestep2< c_func, c_simlab<unsigned long long>& >( sl, 1, next, last );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			triplestep2< c_func, c_simlab<long long>& >( sl, 1, next, last );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			triplestep2< c_func, c_simlab<double>& >( sl, 1, next, last );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) triplestep2< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length, next, last );
		else if( value.type == -65 ) triplestep2< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length, next, last );
		else if( value.type == -64 ) triplestep2< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length, next, last );
		else if( value.type == -34 ) triplestep2< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length, next, last );
		else if( value.type == -33 ) triplestep2< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), value.length, next, last );
		else if( value.type == -32 ) triplestep2< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), value.length, next, last );
		else if( value.type == -16 ) triplestep2< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length, next, last );
	} else {
		if( value.type == 66 ) triplestep2< c_func, double* >( (double*)value.buffer, value.length, next, last );
		else if( value.type == 65 ) triplestep2< c_func, long long* >( (long long*)value.buffer, value.length, next, last );
		else if( value.type == 64 ) triplestep2< c_func, unsigned long long* >( (unsigned long long*)value.buffer, value.length, next, last );
		else if( value.type == 34 ) triplestep2< c_func, float* >( (float*)value.buffer, value.length, next, last );
		else if( value.type == 33 ) triplestep2< c_func, int* >( (int*)value.buffer, value.length, next, last );
		else if( value.type == 32 ) triplestep2< c_func, unsigned int* >( (unsigned int*)value.buffer, value.length, next, last );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type, next, last );
		else if( value.type == 17 ) triplestep2< c_func, short* >( (short*)value.buffer, value.length, next, last );
		else if( value.type == 16 ) triplestep2< c_func, unsigned short* >( (unsigned short*)value.buffer, value.length, next, last );
		else if( value.type == 9 ) triplestep2< c_func, char* >( (char*)value.buffer, value.length, next, last );
		else if( value.type == 8 ) triplestep2< c_func, unsigned char* >( (unsigned char*)value.buffer, value.length, next, last );
	}
}

JNIEXPORT int shift( simlab ret, simlab chunk, simlab shift ) {
	triplestep1< c_shift >( ret, chunk, shift );

	return 3;
}
