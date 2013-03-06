#include "simlab.h"
#include <cstdarg>
#include <cstring>
#include <cstdio>

template <typename T, typename K> class c_flip {
public:
	c_flip( T buffer, int length, K idx, int klen ) {}
};

template <typename T, typename K> class c_flip<T*,K> {
public:
	c_flip( T* buffer, int length, K chunk, int klen ) {
		int s = 0;
		int u = 0;
		for( int i = 0; (klen == 0 || i < klen) && s < length; i++ ) {
			u += chunk[i];
			for( int n = s; n < s+(int)chunk[i]/2; n++ ) {
				int l = u-n+s-1;

				T tmp = buffer[ n ];
				buffer[ n ] = buffer[ l ];
				buffer[ l ] = tmp;
			}
			s = u;
		}
	}
};

template <typename T, typename K> class c_flip<c_simlab<T&>&,K> {
public:
	c_flip( c_simlab<T&> & buffer, int length, K chunk, int klen ) {
		int s = 0;
		int u = 0;
		for( int i = 0; (klen == 0 || i < klen) && s < length; i++ ) {
			u += chunk[i];
			for( int n = s; n < s+(int)chunk[i]/2; n++ ) {
				int l = u-n+s-1;

				T tmp = buffer[ n ];
				buffer[ n ] = buffer[ l ];
				buffer[ l ] = tmp;
			}
			s = u;
		}
	}
};

template<template<typename T, typename K> class c_func, typename T> void doublestep2( T buffer, int length, simlab & value ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			c_func< T,c_simlab<unsigned int>& >( buffer, length, sl, 0 );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			c_func< T,c_simlab<int>& >( buffer, length, sl, 0 );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			c_func< T,c_simlab<float>& >( buffer, length, sl, 0 );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			c_func< T,c_simlab<unsigned long long>& >( buffer, length, sl, 0 );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			c_func< T,c_simlab<long long>& >( buffer, length, sl, 0 );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			c_func< T,c_simlab<double>& >( buffer, length, sl, 0 );
		}
	} else if( value.type < 0 ) {
		/*if( value.type == -66 ) step3< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length );
		else if( value.type == -65 ) step3< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length );
		else if( value.type == -64 ) step3< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length );
		else if( value.type == -34 ) step3< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length );
		else if( value.type == -33 ) step3< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), value.length );
		else if( value.type == -32 ) step3< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), value.length );
		else if( value.type == -16 ) step3< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length );*/
	} else {
		//if( value.type == 130 ) c_func< T,__float128* >( buffer, length, (__float128*)value.buffer, value.length );
		//else if( value.type == 129 ) c_func< T,__int128* >( buffer, length, (__int128*)value.buffer, value.length );
		//else if( value.type == 128 ) c_func< T,unsigned __int128* >( buffer, length, (unsigned __int128*)value.buffer, value.length );
		if( value.type == 66 ) c_func< T,double* >( buffer, length, (double*)value.buffer, value.length );
		else if( value.type == 65 ) c_func< T,long long* >( buffer, length, (long long*)value.buffer, value.length );
		else if( value.type == 64 ) c_func< T,unsigned long long* >( buffer, length, (unsigned long long*)value.buffer, value.length );
		else if( value.type == 34 ) c_func< T,float* >( buffer, length, (float*)value.buffer, value.length );
		else if( value.type == 33 ) c_func< T,int* >( buffer, length, (int*)value.buffer, value.length );
		else if( value.type == 32 ) c_func< T,unsigned int* >( buffer, length, (unsigned int*)value.buffer, value.length );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) c_func< T,short* >( buffer, length, (short*)value.buffer, value.length );
		else if( value.type == 16 ) c_func< T,unsigned short* >( buffer, length, (unsigned short*)value.buffer, value.length );
		else if( value.type == 9 ) c_func< T,char* >( buffer, length, (char*)value.buffer, value.length );
		else if( value.type == 8 ) c_func< T,unsigned char* >( buffer, length, (unsigned char*)value.buffer, value.length );
	}
}

template<template<typename T, typename K> class c_func> void doublestep1( simlab & value, simlab & last ) {
	if( value.length == 0 ) {
		/*if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			doublestep2< c_func, c_simlab<unsigned int>& >( sl, 1, last );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			doublestep2< c_func, c_simlab<int>& >( sl, 1, last );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			doublestep2< c_func, c_simlab<float>& >( sl, 1, last );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			doublestep2< c_func, c_simlab<unsigned long long>& >( sl, 1, last );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			doublestep2< c_func, c_simlab<long long>& >( sl, 1, last );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			doublestep2< c_func, c_simlab<double>& >( sl, 1, last );
		}*/
	} else if( value.type < 0 ) {
		if( value.type == -66 ) doublestep2< c_func, c_simlab<double&>& >( *(c_simlab<double&>*)value.buffer, value.length, last );
		else if( value.type == -65 ) doublestep2< c_func, c_simlab<long long&>& >( *((c_simlab<long long&>*)value.buffer), value.length, last );
		else if( value.type == -64 ) doublestep2< c_func, c_simlab<unsigned long long&>& >( *((c_simlab<unsigned long long&>*)value.buffer), value.length, last );
		else if( value.type == -34 ) doublestep2< c_func, c_simlab<float&>& >( *((c_simlab<float&>*)value.buffer), value.length, last );
		else if( value.type == -33 ) doublestep2< c_func, c_simlab<int&>& >( *((c_simlab<int&>*)value.buffer), value.length, last );
		else if( value.type == -32 ) doublestep2< c_func, c_simlab<unsigned int&>& >( *((c_simlab<unsigned int&>*)value.buffer), value.length, last );
		else if( value.type == -16 ) doublestep2< c_func, c_simlab<short&>& >( *((c_simlab<short&>*)value.buffer), value.length, last );
		else if( value.type == -9 ) doublestep2< c_func, c_simlab<char&>& >( *((c_simlab<char&>*)value.buffer), value.length, last );
		else if( value.type == -8 ) doublestep2< c_func, c_simlab<unsigned char&>& >( *((c_simlab<unsigned char&>*)value.buffer), value.length, last );
	} else {
		//if( value.type == 130 ) doublestep2< c_func, __float128* >( (__float128*)value.buffer, value.length, last );
		//else if( value.type == 129 ) doublestep2< c_func, __int128_t* >( (__int128_t*)value.buffer, value.length, last );
		//else if( value.type == 128 ) doublestep2< c_func, unsigned __int128_t* >( (unsigned __int128_t*)value.buffer, value.length, last );
		if( value.type == 66 ) doublestep2< c_func, double* >( (double*)value.buffer, value.length, last );
		else if( value.type == 65 ) doublestep2< c_func, long long* >( (long long*)value.buffer, value.length, last );
		else if( value.type == 64 ) doublestep2< c_func, unsigned long long* >( (unsigned long long*)value.buffer, value.length, last );
		else if( value.type == 34 ) doublestep2< c_func, float* >( (float*)value.buffer, value.length, last );
		else if( value.type == 33 ) doublestep2< c_func, int* >( (int*)value.buffer, value.length, last );
		else if( value.type == 32 ) doublestep2< c_func, unsigned int* >( (unsigned int*)value.buffer, value.length, last );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) doublestep2< c_func, short* >( (short*)value.buffer, value.length, last );
		else if( value.type == 16 ) doublestep2< c_func, unsigned short* >( (unsigned short*)value.buffer, value.length, last );
		else if( value.type == 9 ) doublestep2< c_func, char* >( (char*)value.buffer, value.length, last );
		else if( value.type == 8 ) doublestep2< c_func, unsigned char* >( (unsigned char*)value.buffer, value.length, last );
	}
}

JNIEXPORT int flip( simlab data, simlab r ) {
	doublestep1< c_flip >( data, r );

	return 2;
}

/*extern simlab nulldata;

template <typename T, typename K, typename U> class c_flip {
public:
	c_flip( T buffer, int length, K idx, int klen, U chunk, int ulen ) {}
};

template <typename T, typename K, typename U> class c_flip<T*,K,U> {
public:
	c_flip( T* buffer, int length, K idx, int klen, U chunk, int ulen ) {
		int s = 0;
		int u = 0;
		for( int i = 0; i < ulen; i++ ) {
			u += chunk[i];
			for( int n = s; n < s+(int)chunk[i]/2; n++ ) {
				int one = (int)idx[n];
				int two = (int)idx[u-n+s-1];

				T tmp = buffer[ one ];
				buffer[ one ] = buffer[ two ];
				buffer[ two ] = tmp;
			}
			s = u;
		}
	}
};

template <typename T, typename K, typename U> class c_flip< c_simlab<T&>&,K,U > {
public:
	c_flip( c_simlab<T&> & buffer, int length, K idx, int klen, U chunk, int ulen ) {
		int s = 0;
		int u = 0;
		for( int i = 0; i < ulen; i++ ) {
			u += chunk[i];
			for( int n = s; n < s+(int)chunk[i]/2; n++ ) {
				int one = (int)idx[n];
				int two = (int)idx[u-n+s-1];

				T tmp = buffer[ one ];
				buffer[ one ] = buffer[ two ];
				buffer[ two ] = tmp;
			}
			s = u;
		}
	}
};

template<typename T, typename K> void step2( void (*f)( ... ), T buffer, int length, K buffer2, int length2, simlab next, ... ) {
	va_list	ap;
	va_start( ap, next );
	simlab s = va_arg( ap, simlab );
	int val = memcmp( &s, &nulldata, sizeof(simlab) );
	if( val == 0 ) {
		if( next.type == 66 ) f( buffer, length, buffer2, length2, (double*)next.buffer, next.length );
	} else {
		while( val != 0 ) {
			printf( "len %d\n", (int)s.length );

			s = va_arg( ap, simlab );
			val = memcmp( &s, &nulldata, sizeof(simlab) );
		}
	}
}

template<template<typename T, typename K, typename T3> class c_func, typename T, typename K> void step3( T buffer, int length, K kbuffer, int klength, simlab & value ) {
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
		else if( value.type == -16 ) step3< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length );*
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

template<template<typename T1, typename T2, typename T3> class c_func, typename T> void step2( T buffer, int length, simlab & value, simlab & last ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			step3< c_func, T, c_simlab<unsigned int>& >( buffer, length, sl, 1, last );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			step3< c_func, T, c_simlab<int>& >( buffer, length, sl, 1, last );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			step3< c_func, T, c_simlab<float>& >( buffer, length, sl, 1, last );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			step3< c_func, T, c_simlab<unsigned long long>& >( buffer, length, sl, 1, last );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			step3< c_func, T, c_simlab<long long>& >( buffer, length, sl, 1, last );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			step3< c_func, T, c_simlab<double>& >( buffer, length, sl, 1, last );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) step3< c_func, T, c_simlab<double>& >( buffer, length, *(c_simlab<double>*)value.buffer, value.length, last );
		else if( value.type == -65 ) step3< c_func, T, c_simlab<long long>& >( buffer, length, *((c_simlab<long long>*)value.buffer), value.length, last );
		else if( value.type == -64 ) step3< c_func, T, c_simlab<unsigned long long>& >( buffer, length, *((c_simlab<unsigned long long>*)value.buffer), value.length, last );
		else if( value.type == -34 ) step3< c_func, T, c_simlab<float>& >( buffer, length, *((c_simlab<float>*)value.buffer), value.length, last );
		else if( value.type == -33 ) step3< c_func, T, c_simlab<int>& >( buffer, length, *((c_simlab<int>*)value.buffer), value.length, last );
		else if( value.type == -32 ) step3< c_func, T, c_simlab<unsigned int>& >( buffer, length, *((c_simlab<unsigned int>*)value.buffer), value.length, last );
		else if( value.type == -16 ) step3< c_func, T, c_simlab<short>& >( buffer, length, *((c_simlab<short>*)value.buffer), value.length, last );
	} else {
		if( value.type == 66 ) step3< c_func, T, double* >( buffer, length, (double*)value.buffer, value.length, last );
		else if( value.type == 65 ) step3< c_func, T, long long* >( buffer, length, (long long*)value.buffer, value.length, last );
		else if( value.type == 64 ) step3< c_func, T, unsigned long long* >( buffer, length, (unsigned long long*)value.buffer, value.length, last );
		else if( value.type == 34 ) step3< c_func, T, float* >( buffer, length, (float*)value.buffer, value.length, last );
		else if( value.type == 33 ) step3< c_func, T, int* >( buffer, length, (int*)value.buffer, value.length, last );
		else if( value.type == 32 ) step3< c_func, T, unsigned int* >( buffer, length, (unsigned int*)value.buffer, value.length, last );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) step3< c_func, T, short* >( buffer, length, (short*)value.buffer, value.length, last );
		else if( value.type == 16 ) step3< c_func, T, unsigned short* >( buffer, length, (unsigned short*)value.buffer, value.length, last );
		else if( value.type == 9 ) step3< c_func, T, char* >( buffer, length, (char*)value.buffer, value.length, last );
		else if( value.type == 8 ) step3< c_func, T, unsigned char* >( buffer, length, (unsigned char*)value.buffer, value.length, last );
	}
}

template<template<typename T1, typename T2, typename T3> class c_func> void step1( simlab & value, simlab & next, simlab & last ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			step2< c_func, c_simlab<unsigned int>& >( sl, 1, next, last );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			step2< c_func, c_simlab<int>& >( sl, 1, next, last );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			step2< c_func, c_simlab<float>& >( sl, 1, next, last );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			step2< c_func, c_simlab<unsigned long long>& >( sl, 1, next, last );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			step2< c_func, c_simlab<long long>& >( sl, 1, next, last );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			step2< c_func, c_simlab<double>& >( sl, 1, next, last );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) step2< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length, next, last );
		else if( value.type == -65 ) step2< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length, next, last );
		else if( value.type == -64 ) step2< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length, next, last );
		else if( value.type == -34 ) step2< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length, next, last );
		else if( value.type == -33 ) step2< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), value.length, next, last );
		else if( value.type == -32 ) step2< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), value.length, next, last );
		else if( value.type == -16 ) step2< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length, next, last );
	} else {
		if( value.type == 66 ) step2< c_func, double* >( (double*)value.buffer, value.length, next, last );
		else if( value.type == 65 ) step2< c_func, long long* >( (long long*)value.buffer, value.length, next, last );
		else if( value.type == 64 ) step2< c_func, unsigned long long* >( (unsigned long long*)value.buffer, value.length, next, last );
		else if( value.type == 34 ) step2< c_func, float* >( (float*)value.buffer, value.length, next, last );
		else if( value.type == 33 ) step2< c_func, int* >( (int*)value.buffer, value.length, next, last );
		else if( value.type == 32 ) step2< c_func, unsigned int* >( (unsigned int*)value.buffer, value.length, next, last );
		//else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type, next, last );
		else if( value.type == 17 ) step2< c_func, short* >( (short*)value.buffer, value.length, next, last );
		else if( value.type == 16 ) step2< c_func, unsigned short* >( (unsigned short*)value.buffer, value.length, next, last );
		else if( value.type == 9 ) step2< c_func, char* >( (char*)value.buffer, value.length, next, last );
		else if( value.type == 8 ) step2< c_func, unsigned char* >( (unsigned char*)value.buffer, value.length, next, last );
	}
}

JNIEXPORT int flip( simlab data, simlab index, simlab chunk ) {
	step1< c_flip >( data, index, chunk );

	return 3;
}*/
