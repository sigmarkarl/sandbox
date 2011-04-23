#include "simlab.h"

template <typename T, typename K> class c_range {
public:
	c_range( T buffer, int length, K idx, int klen ) {
		int l = 0;
		for( int i = 0; i < klen; i+=2 ) {
			for( int k = (int)idx[i]; k < (int)idx[i+1]; k++ ) {
				buffer[l++] = k;
			}
		}
	}
};

/*template <typename T, typename K> class c_range<T*,K> {
public:
	c_range( T* buffer, int length, K idx, int klen ) {
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

template <typename T, typename K> class c_range<c_simlab<T&>&,K> {
public:
	c_range( c_simlab<T&> & buffer, int length, K idx, int klen ) {
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
};*/


template<template<typename T, typename K> class c_func, typename T> void doublestep2( T buffer, int length, simlab & value ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			c_func< T,c_simlab<unsigned int>& >( buffer, length, sl, 1 );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			c_func< T,c_simlab<int>& >( buffer, length, sl, 1 );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			c_func< T,c_simlab<float>& >( buffer, length, sl, 1 );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			c_func< T,c_simlab<unsigned long long>& >( buffer, length, sl, 1 );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			c_func< T,c_simlab<long long>& >( buffer, length, sl, 1 );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			c_func< T,c_simlab<double>& >( buffer, length, sl, 1 );
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
	} else if( value.length == -1 ) {
		if( value.type == -66 ) doublestep2< c_func, c_simlab<double&>& >( *(c_simlab<double&>*)value.buffer, value.length, last );
		else if( value.type == -65 ) doublestep2< c_func, c_simlab<long long&>& >( *((c_simlab<long long&>*)value.buffer), value.length, last );
		else if( value.type == -64 ) doublestep2< c_func, c_simlab<unsigned long long&>& >( *((c_simlab<unsigned long long&>*)value.buffer), value.length, last );
		else if( value.type == -34 ) doublestep2< c_func, c_simlab<float&>& >( *((c_simlab<float&>*)value.buffer), value.length, last );
		else if( value.type == -33 ) doublestep2< c_func, c_simlab<int&>& >( *((c_simlab<int&>*)value.buffer), value.length, last );
		else if( value.type == -32 ) doublestep2< c_func, c_simlab<unsigned int&>& >( *((c_simlab<unsigned int&>*)value.buffer), value.length, last );
		else if( value.type == -16 ) doublestep2< c_func, c_simlab<short&>& >( *((c_simlab<short&>*)value.buffer), value.length, last );
	} else {
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

JNIEXPORT int range( simlab data, simlab r ) {
	doublestep1< c_range >( data, r );

	return 2;
}
