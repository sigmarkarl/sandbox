/*
 * arith.cc
 *
 *  Created on: Dec 29, 2008
 *      Author: root
 */

#include "simlab.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

extern simlab 	data;
extern simlab	nulldata;
extern c_const<int>		iconst;
extern c_const<float>	fconst;

//extern void (*d_reorder)( double* buffer, int length );
extern void (*d_diff)( double* buffer, int length, int clen );
extern void (*d_integ)( double* buffer, int length, int clen );

int g_i = 0;
unsigned int g_ui = 0;
long		g_l = 0;
unsigned long	g_ul = 0;
long long	g_ll = 0;
unsigned long long	g_ull = 0;
float	g_f = 0;
double	g_d = 0;

template<> int c_simlab<int>::operator[]( int i ) {
	return g_i;
};

template<> int & c_simlab<int&>::operator[]( int i ) {
	return g_i;
};

template<> unsigned int c_simlab<unsigned int>::operator[]( int i ) {
	return g_ui;
};

template<> unsigned int& c_simlab<unsigned int&>::operator[]( int i ) {
	return g_ui;
};

template<> long long c_simlab<long long>::operator[]( int i ) {
	return g_ll;
};

template<> long long & c_simlab<long long&>::operator[]( int i ) {
	return g_ll;
};

template<> long c_simlab<long>::operator[]( int i ) {
	return g_l;
};

template<> long& c_simlab<long&>::operator[]( int i ) {
	return g_l;
};

template<> unsigned long c_simlab<unsigned long>::operator[]( int i ) {
	return g_ul;
};

template<> unsigned long& c_simlab<unsigned long&>::operator[]( int i ) {
	return g_ul;
};

template<> unsigned long long c_simlab<unsigned long long>::operator[]( int i ) {
	return g_ull;
};

template<> unsigned long long& c_simlab<unsigned long long&>::operator[]( int i ) {
	return g_ull;
};

template<> float c_simlab<float>::operator[]( int i ) {
	return g_f;
};

template<> float& c_simlab<float&>::operator[]( int i ) {
	return g_f;
};

template<> double c_simlab<double>::operator[]( int i ) {
	return g_d;
};

template<> double& c_simlab<double&>::operator[]( int i ) {
	return g_d;
};

template <typename T, typename K, typename U> void t_arrange( T buffer, long length, K value, long vallen ) {
	int i = 0;
	int l = 0;

	if( length == vallen ) {
		while( i < vallen && l < vallen ) {
			int k = value[l];
			while( k > l ) {
				k = value[k];
			}
			if( k == l ) {
				U	to = buffer[(int)l];
				int k = value[l];
				U	ti = buffer[(int)k];
				int n = l;

				while( k != l ) {
					buffer[(int)n] = ti;
					//ti = to;
					n = k;
					k = value[k];
					ti = buffer[(int)k];
					i++;
				}
				buffer[(int)n] = to;
				i++;
			}
			l++;
		}
	} else {
		while( i < vallen && l < vallen ) {
			int k = value[l];
			while( k > l ) {
				k = value[k];
			}
			if( k == l ) {
				U	to = buffer[(int)l];
				k = value[l];
				U	ti = buffer[(int)k];
				int n = l;

				while( k != l ) {
					buffer[(int)n] = ti;
					n = k;
					k = value[k];
					ti = buffer[(int)k];
					i++;
				}
				buffer[(int)k] = to;

				for( int u = vallen; u < length; u+=vallen ) {
					U	to = buffer[(int)l+u];
					k = value[l];
					U	ti = buffer[(int)k+u];

					while( k != l ) {
						buffer[(int)k+u] = to;
						to = ti;
						k = value[k];
						ti = buffer[(int)k+u];
					}
					buffer[(int)k+u] = to;
				}

				i++;
			}
			l++;
		}

		/*while( i < m-2 && l < m ) {
			double k = fmod( (l*r), m );
			double t = fmod( (l*c), m );
			if( k > l && t > l ) {
				k = fmod( (k*r), m );
				while( k > l ) {
					k = fmod( (k*r), m );
				}
				if( k == l ) {
					T buf = buffer;
					while( dim*len < length ) {
						K	to = buf[(int)l];
						k = fmod( (l*r), m );
						K	ti = buf[(int)k];

						while( k != l ) {
							buf[(int)k] = to;
							to = ti;
							k = fmod( (k*r), m );
							ti = buf[(int)k];
							i++;
						}
						buf[(int)k] = to;
						//heyheyho
						//buf += len;
						dim++;
					}
					i++;
				}
			}
			l++;
		}*/
	}
}

template <typename T, typename K, typename U> void t_order( T buffer, long length, K value, long vallen ) {
	int i = 0;
	int l = 0;

	if( length == vallen ) {
		while( i < vallen && l < vallen ) {
			int k = value[l];
			while( k > l ) {
				k = value[k];
			}
			if( k == l ) {
				U	to = buffer[(int)l];
				k = value[l];
				U	ti = buffer[(int)k];

				while( k != l ) {
					buffer[(int)k] = to;
					to = ti;
					k = value[k];
					ti = buffer[(int)k];
					i++;
				}
				buffer[(int)k] = to;
				i++;
			}
			l++;
		}
	} else {
		while( i < vallen && l < vallen ) {
			int k = value[l];
			while( k > l ) {
				k = value[k];
			}
			if( k == l ) {
				U	to = buffer[(int)l];
				k = value[l];
				U	ti = buffer[(int)k];

				while( k != l ) {
					buffer[(int)k] = to;
					to = ti;
					k = value[k];
					ti = buffer[(int)k];
					i++;
				}
				buffer[(int)k] = to;

				for( int u = vallen; u < length; u+=vallen ) {
					U	to = buffer[(int)l+u];
					k = value[l];
					U	ti = buffer[(int)k+u];

					while( k != l ) {
						buffer[(int)k+u] = to;
						to = ti;
						k = value[k];
						ti = buffer[(int)k+u];
					}
					buffer[(int)k+u] = to;
				}

				i++;
			}
			l++;
		}

		/*while( i < m-2 && l < m ) {
			double k = fmod( (l*r), m );
			double t = fmod( (l*c), m );
			if( k > l && t > l ) {
				k = fmod( (k*r), m );
				while( k > l ) {
					k = fmod( (k*r), m );
				}
				if( k == l ) {
					T buf = buffer;
					while( dim*len < length ) {
						K	to = buf[(int)l];
						k = fmod( (l*r), m );
						K	ti = buf[(int)k];

						while( k != l ) {
							buf[(int)k] = to;
							to = ti;
							k = fmod( (k*r), m );
							ti = buf[(int)k];
							i++;
						}
						buf[(int)k] = to;
						//heyheyho
						//buf += len;
						dim++;
					}
					i++;
				}
			}
			l++;
		}*/
	}
}

template <typename T, typename K> void t_specopy( T buffer, int length, K value, int vallen ) {
	//unsigned long long val;
	printf("hoho2\n");
	int len = vallen > length ? vallen : length;
	for( int i = 0; i < len; i++ ) {
		memcpy( (void*)&buffer[i], (void*)((long long)value+(long long)(vallen/8)*i), vallen );
	}
}

template <typename T, typename K> void t_copy( T buffer, int length, K value, int vallen ) {
	int len = vallen > length ? vallen : length;
	for( int i = 0; i < len; i++ ) {
		buffer[i] = value[i];
	}
}

template <typename T, typename K> void t_add( T buffer, int length, K value, int vallen ) {
	println("lllalalal\n");
	if( vallen < length ) {
		for( int i = 0; i < length; i++ ) {
			buffer[i] += value[i%vallen];
		}
	} else {
		int len = vallen > length ? vallen : length;
		for( int i = 0; i < len; i++ ) {
			buffer[i] += value[i];
		}
	}
}

template <typename T, typename K> void t_mul( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen > klen ? tlen : klen); i++ ) {
		t[i] *= k[i];
	}
}

template <typename T, typename K> void t_sub( T buffer, int length, K value, int vallen ) {
	int len = vallen > length ? vallen : length;
	for( int i = 0; i < len; i++ ) {
		buffer[i] -= value[i];
	}
}

template <typename T, typename K> void t_div( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen > klen ? tlen : klen); i++ ) {
		t[i] /= k[i];
	}
}

template <typename T, typename K> void t_mod( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen > klen ? tlen : klen); i++ ) {
		t[i] = fmod( (double)t[i], (double)k[i] );
		//t[i] %= (int)k[i];
	}
}

template <typename T, typename K> class c_specopy {
public:
	c_specopy( T tbuf, int tlen, K kbuf, int klen ) {
		t_specopy<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_copy {
public:
	c_copy( T tbuf, int tlen, K kbuf, int klen ) {
		t_copy<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K, typename U> class c_arrange {
public:
	c_arrange( T tbuf, int tlen, K kbuf, int klen ) {
		t_arrange<T,K,U>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K, typename U> class c_order {
public:
	c_order( T tbuf, int tlen, K kbuf, int klen ) {
		t_order<T,K,U>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_set {
public:
	c_set( T tbuf, int tlen, K kbuf, int klen ) {
		//template <typename T, typename K> void t_set( T buffer, long length, K value, long vallen ) {
		long len = klen > tlen ? klen : tlen;

		for( long i = 0; i < len; i++ ) {
			tbuf[i] = kbuf[i];
		}
		//}
		//t_set<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_mul {
public:
	c_mul( T tbuf, int tlen, K kbuf, int klen ) {
		t_mul<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_add {
public:
	c_add( T tbuf, int tlen, K kbuf, int klen ) {
		t_add<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_sub {
public:
	c_sub( T tbuf, int tlen, K kbuf, int klen ) {
		t_sub<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_div {
public:
	c_div( T tbuf, int tlen, K kbuf, int klen ) {
		t_div<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_mod {
public:
	c_mod( T t, int tlen, K k, int klen ) {
		for( int i = 0; i < (tlen > klen ? tlen : klen); i++ ) {
			t[i] = fmod( (double)t[i], (double)k[i] );
				//t[i] %= (int)k[i];
		}
		//t_mod<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_imod {
public:
	c_imod( T t, int tlen, K k, int klen ) {
		for( int i = 0; i < (tlen > klen ? tlen : klen); i++ ) {
			t[i] = fmod( (double)k[i], (double)t[i] );
		}
	}
};

template<template<typename T, typename V, typename U> class c_func, typename K> void suborder( K kbuf, long long klen ) {
	//printf("subarith\n");
	if( data.length == -1 ) {

	} else if( data.length == 0 ) {
		/*if( data.type == 66 ) c_func<double*,K>( (double*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 32 ) {
			c_func<unsigned int*,K>( (unsigned int*)&data.buffer, 1, kbuf, klen );
		}
		else if( data.type == 16 ) c_func<short*,K>( (short*)&data.buffer, 1, kbuf, klen );
	} else if( data.type < 0 ) {
		if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -32 ) c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );*/
	} else {
		if( data.type == 66 ) c_func<double*,K,double>( (double*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K,long long>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K,unsigned long long>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K,float>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K,int>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 32 ) c_func<unsigned int*,K,unsigned int>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 17 ) c_func<short*,K,short>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<unsigned short*,K,unsigned short>( (unsigned short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 9 ) c_func<char*,K,char>( (char*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 8 ) c_func<unsigned char*,K,unsigned char>( (unsigned char*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K, typename U> class c_func> void order( simlab & value ) {
	//printf("arith\n");
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			suborder< c_func, c_simlab<unsigned int>& >( sl, data.length );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			suborder< c_func, c_simlab<int>& >( sl, data.length );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			suborder< c_func, c_simlab<float>& >( sl, data.length );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			suborder< c_func, c_simlab<unsigned long long>& >( sl, data.length );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			suborder< c_func, c_simlab<long long>& >( sl, data.length );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			suborder< c_func, c_simlab<double>& >( sl, data.length );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) suborder< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, data.length );
		else if( value.type == -65 ) suborder< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), data.length );
		else if( value.type == -64 ) suborder< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), data.length );
		else if( value.type == -34 ) suborder< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), data.length );
		else if( value.type == -33 ) suborder< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), data.length );
		else if( value.type == -32 ) suborder< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), data.length );
		else if( value.type == -16 ) suborder< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), data.length );
	} else {
		if( value.type == 66 ) suborder< c_func, double* >( (double*)value.buffer, value.length );
		else if( value.type == 65 ) suborder< c_func, long long* >( (long long*)value.buffer, value.length );
		else if( value.type == 64 ) suborder< c_func, unsigned long long* >( (unsigned long long*)value.buffer, value.length );
		else if( value.type == 34 ) suborder< c_func, float* >( (float*)value.buffer, value.length );
		else if( value.type == 33 ) suborder< c_func, int* >( (int*)value.buffer, value.length );
		else if( value.type == 32 ) suborder< c_func, unsigned int* >( (unsigned int*)value.buffer, value.length );
		else if( value.type == 17 ) suborder< c_func, short* >( (short*)value.buffer, value.length );
		else if( value.type == 16 ) suborder< c_func, unsigned short* >( (unsigned short*)value.buffer, value.length );
		else if( value.type == 9 ) suborder< c_func, char* >( (char*)value.buffer, value.length );
		else if( value.type == 8 ) suborder< c_func, unsigned char* >( (unsigned char*)value.buffer, value.length );
	}
}

template<template<typename T, typename V> class c_func, typename K> void specarith( K kbuf, long klen ) {
	//printf("subarith\n");
	if( data.length == -1 ) {

	} else if( data.length == 0 ) {

	} else if( data.type < 0 ) {

	} else {
		if( data.type == 66 ) c_func<double*,K>( (double*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 32 ) c_func<unsigned int*,K>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 17 ) c_func<short*,K>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<unsigned short*,K>( (unsigned short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 9 ) c_func<char*,K>( (char*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 8 ) c_func<unsigned char*,K>( (unsigned char*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename V> class c_func, typename K> void subarith( K kbuf, long klen ) {
	printf("subarith\n");
	if( data.length == -1 ) {
		if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -32 ) {
			printf("yes2! %lld\n", klen);
			c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		}
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );
	} else if( data.length == 0 ) {
		if( data.type == 66 ) c_func<double*,K>( (double*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 65 ) {
			c_func<long long*,K>( (long long*)&data.buffer, 1, kbuf, klen );
		}
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 32 ) {
			c_func<unsigned int*,K>( (unsigned int*)&data.buffer, 1, kbuf, klen );
		}
		else if( data.type == 16 ) c_func<short*,K>( (short*)&data.buffer, 1, kbuf, klen );
	} else if( data.type < 0 ) {
		if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) {
			printf("lala\n");
			c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		}
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -32 ) {
			printf("yes!\n");
			c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		}
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );
	} else {
		if( data.type == 66 ) c_func<double*,K>( (double*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 32 ) c_func<unsigned int*,K>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 17 ) c_func<short*,K>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<unsigned short*,K>( (unsigned short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 9 ) c_func<char*,K>( (char*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 8 ) c_func<unsigned char*,K>( (unsigned char*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K> class c_func> void specarith( simlab & value ) {
	if( value.length == 0 ) {
	} else if( value.length == -1 ) {
	} else {
		if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
	}
}

template<template<typename T, typename K> class c_func> void arith( simlab & value ) {
	printf("arith %lld %lld %lld\n",value.length,value.buffer,value.type);
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			printf("heyhey\n");
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			subarith< c_func, c_simlab<unsigned int>& >( sl, 1 );
		} else if( value.type == 33 ) {
			printf("heyhey2\n");
			c_const<int>	sl( *(int*)&value.buffer );
			subarith< c_func, c_simlab<int>& >( sl, 1 );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			subarith< c_func, c_simlab<float>& >( sl, 1 );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			subarith< c_func, c_simlab<unsigned long long>& >( sl, 1 );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			subarith< c_func, c_simlab<long long>& >( sl, 1 );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			subarith< c_func, c_simlab<double>& >( sl, 1 );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) subarith< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length );
		else if( value.type == -65 ) subarith< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length );
		else if( value.type == -64 ) subarith< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length );
		else if( value.type == -34 ) subarith< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length );
		else if( value.type == -33 ) subarith< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), value.length );
		else if( value.type == -32 ) subarith< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), value.length );
		else if( value.type == -16 ) subarith< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), value.length );
	} else {
		if( value.type == 66 ) subarith< c_func, double* >( (double*)value.buffer, value.length );
		else if( value.type == 65 ) subarith< c_func, long long* >( (long long*)value.buffer, value.length );
		else if( value.type == 64 ) subarith< c_func, unsigned long long* >( (unsigned long long*)value.buffer, value.length );
		else if( value.type == 34 ) subarith< c_func, float* >( (float*)value.buffer, value.length );
		else if( value.type == 33 ) subarith< c_func, int* >( (int*)value.buffer, value.length );
		else if( value.type == 32 ) subarith< c_func, unsigned int* >( (unsigned int*)value.buffer, value.length );
		else if( value.type == 24 ) specarith< c_func, int* >( (int*)value.buffer, value.type );
		else if( value.type == 17 ) subarith< c_func, short* >( (short*)value.buffer, value.length );
		else if( value.type == 16 ) subarith< c_func, unsigned short* >( (unsigned short*)value.buffer, value.length );
		else if( value.type == 9 ) subarith< c_func, char* >( (char*)value.buffer, value.length );
		else if( value.type == 8 ) subarith< c_func, unsigned char* >( (unsigned char*)value.buffer, value.length );
	}
}

template<template<typename T, typename V> class c_func, typename K> void subiarith( K kbuf, int klen ) {
	if( data.length == -1 ) {

	} else if( data.type < 0 ) {
		if( data.type == -32 ) c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
	} else {
		if( data.type == 32 ) c_func<unsigned int*,K>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<short*,K>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 66 ) c_func<double*,K>( (double*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K> class c_func> void iarith( simlab & value ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			subiarith< c_func, c_simlab<int>& >( sl, data.length );
		}
	} else if( value.length == -1 ) {
		if( value.type == -32 ) subiarith< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), data.length );
		else if( value.type == -16 ) subiarith< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), data.length );
	} else {
		if( value.type == 32 ) subiarith< c_func, int* >( (int*)value.buffer, data.length );
		else if( value.type == 16 ) subiarith< c_func, short* >( (short*)value.buffer, data.length );
	}
}

JNIEXPORT int irrange( simlab ret, simlab ord, simlab elen ) {
	double* d = (double*)elen.buffer;
	double* o = (double*)ord.buffer;
	long long dst = ret.buffer;
	long long src = data.buffer;

	int l = elen.length;
	int tbl = bytelength( elen.type, l );

	double* t = (double*)realloc( NULL, tbl );
	memcpy( t, d, tbl );
	d_diff( t, l, l );
	t_arrange<double*,double*,double>( t, (long)l, o, (long)ord.length );
	d_integ( t, l, l );

	int elsize = data.type/8;
	for( int i = 0; i < l; i++ ) {
		int ml = i > 0 ? t[i]-t[i-1] : t[i];

		int k = o[i];

		int dadd = (i > 0 ? (int)t[i-1] : 0);
		int sadd = (k > 0 ? (int)d[k-1] : 0);
		void* dest = (void*)(dst+dadd*elsize);
		void* source = (void*)(src+sadd*elsize);
		memcpy( dest, source, ml*elsize );

		printf( "%d %d %d\n", ml, dadd, sadd );
	}
	free(t);

	return 3;
}

JNIEXPORT int select2( simlab ret, simlab ind, simlab len, simlab dnd ) {
	long long s = data.buffer;
	long long d = ret.buffer;

	int elsize = ret.type/8;
	double* d_ind = (double*)ind.buffer;
	double* d_dnd = (double*)dnd.buffer;
	double* d_len = (double*)len.buffer;
	for( int i = 0; i < ind.length; i++ ) {
		memcpy( (void*)(long long)(d+d_dnd[i]*elsize), (void*)(long long)(s+d_ind[i]*elsize), (int)(d_len[i]*elsize) );
	}
}

JNIEXPORT int ordir( simlab ret, simlab ord, simlab elen ) {
	//int bl = bytelength( data.type, data.length );
	double* d = (double*)elen.buffer;
	double* o = (double*)ord.buffer;
	long long dst = ret.buffer;
	long long src = data.buffer;

	/*if( ord.length == 0 ) {
		int c = cl.length;
		double sum = d[c-1];
		/*for( int i = 0; i < l; i++ ) {
			sum += d[i];
		}*
		int r = bl/sum;
		int l = c*r;
		int tot = 0;
		for( int i = 0; i < l; i++ ) {
			int cc = i%c;
			int rr = i/c;
			int ml = d[cc]-tot+sum*rr;
			void* dest = (void*)(dst+(cc == 0 ? 0 : (int)d[cc-1]*r)+ml*rr);
			void* source = (void*)(src+tot);
			memcpy( dest, source, ml );
			tot += ml;
		}
	} else {*/

	int l = elen.length;
	int tbl = bytelength( elen.type, l );

	double* t = (double*)realloc( NULL, tbl );
	memcpy( t, d, tbl );
	d_diff( t, l, l );
	t_order<double*,double*,double>( t, (long)l, o, (long)ord.length );
	d_integ( t, l, l );

	int elsize = data.type/8;
	for( int i = 0; i < l; i++ ) {
		int ml = i > 0 ? d[i]-d[i-1] : d[i];

		int k = o[i];
		void* dest = (void*)(dst+(k > 0 ? (int)t[k-1]*elsize : 0));
		void* source = (void*)(src+(i > 0 ? (int)d[i-1]*elsize : 0));
		memcpy( dest, source, ml*elsize );
	}
	free(t);

	return 3;
}

JNIEXPORT int simmi( simlab value ) {
	printf("erm %d %d %d %d\n", (int)value.buffer, (int)data.buffer, (int)data.type, (int)data.length );

	return 1;
}

/*JNIEXPORT int hist( simlab ret, simlab chunk, simlab min, simlab max ) {
	histo< c_hist >( ret );
}*/

JNIEXPORT int copy( simlab value, simlab indices, simlab sizes, simlab dind ) {
	if( memcmp( &indices, &nulldata, sizeof(simlab) ) == 0 ) {
		if( value.type == 24 ) {
			specarith< c_specopy >( value );
		}
		else arith< c_copy >( value );
	} else {
		/*double sum = 0;
		if( sizes.type == 66 ) {
			double val;
			t_sum( sizes.buffer, sizes.length, sizes.length, sizes.length, &val );
			sum = val;
		}*/


	}

	return 1;
}

JNIEXPORT int rearrange( simlab value ) {
	order< c_arrange >( value );

	return 1;
}

JNIEXPORT int reorder( simlab value ) {
	order< c_order >( value );

	return 1;
}

JNIEXPORT int set( simlab value ) {
	arith< c_set >( value );

	return 1;
}

JNIEXPORT int add( simlab value ) {
	arith< c_add >( value );
	return 1;
}

JNIEXPORT int simlab_mul( simlab value ) {
	arith< c_mul >( value );
	return 1;
}

JNIEXPORT int sub( simlab value ) {
	arith< c_sub >( value );
	return 1;
}

JNIEXPORT int simlab_div( simlab value ) {
	arith< c_div >( value );
	return 1;
}

JNIEXPORT int mod( simlab value ) {
	arith< c_mod >( value );
	return 1;
}

JNIEXPORT int imod( simlab value ) {
	arith< c_imod >( value );
	return 1;
}

template<template<typename T, typename K, typename U> class c_func> void gettertemp( simlab & ret, simlab & what, simlab & where ) {
	if( ret.length == 0 ) {
		/*if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			subarith< c_func, c_simlab<unsigned int>& >( sl, 1 );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			subarith< c_func, c_simlab<int>& >( sl, 1 );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			subarith< c_func, c_simlab<float>& >( sl, 1 );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			subarith< c_func, c_simlab<unsigned long long>& >( sl, 1 );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			subarith< c_func, c_simlab<long long>& >( sl, 1 );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			subarith< c_func, c_simlab<double>& >( sl, 1 );
		}*/
	} else if( ret.length == -1 ) {
		/*if( value.type == -66 ) subarith< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, value.length );
		else if( value.type == -65 ) subarith< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), value.length );
		else if( value.type == -64 ) subarith< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), value.length );
		else if( value.type == -34 ) subarith< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), value.length );
		else if( value.type == -33 ) subarith< c_func, c_simlab<int>& >( *((c_simlab<int>*)ret.buffer), ret.length );
		else if( ret.type == -32 ) subarith< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)ret.buffer), ret.length );
		else if( ret.type == -16 ) subarith< c_func, c_simlab<short>& >( *((c_simlab<short>*)ret.buffer), ret.length );*/
	} else {
		if( ret.type == 66 ) subarith< c_func, double* >( (double*)ret.buffer, ret.length );
		else if( ret.type == 65 ) subarith< c_func, long long* >( (long long*)ret.buffer, ret.length );
		else if( ret.type == 64 ) subarith< c_func, unsigned long long* >( (unsigned long long*)ret.buffer, ret.length );
		else if( ret.type == 34 ) subarith< c_func, float* >( (float*)ret.buffer, ret.length );
		else if( ret.type == 33 ) subarith< c_func, int* >( (int*)ret.buffer, ret.length );
		else if( ret.type == 32 ) subarith< c_func, unsigned int* >( (unsigned int*)ret.buffer, ret.length );
		else if( ret.type == 24 ) specarith< c_func, int* >( (int*)ret.buffer, ret.type );
		else if( ret.type == 17 ) subarith< c_func, short* >( (short*)ret.buffer, ret.length );
		else if( ret.type == 16 ) subarith< c_func, unsigned short* >( (unsigned short*)ret.buffer, ret.length );
		else if( ret.type == 9 ) subarith< c_func, char* >( (char*)ret.buffer, ret.length );
		else if( ret.type == 8 ) subarith< c_func, unsigned char* >( (unsigned char*)ret.buffer, ret.length );
	}
}

/*templatpublic:
	getter( simlab & ret ) {
		//getter<t_func,C-1>( ret );
	}e*/


template <typename T, typename K, typename V, typename U> void t_get( T t, int tlen, K k, int klen, V v, int vlen, U u, int ulen ) {
	for( int i = 0; i < klen; i++ ) {
		t[ (int)v[i] ] = k[ (int)u[i] ];
	}
}

/*template <typename T, typename K, typename V, typename U> class c_get {
public:
	c_get( T tbuf, int tlen, K kbuf, int klen, V vbuf, int vlen, U ubuf, int ulen ) {
		t_get<T,K>( tbuf, tlen, kbuf, klen, vbuf, vlen, ubuf, ulen );
	}
};*/

typedef void (*f_ptr)( simlab & ret );

template<typename T> void mu( T r ) {}

//template<typename T> f_str;

/*template< typename T, typename K, typename V, typename U, void (*s)( T, int, K, int, V, int, U, int ) > void getter( s func, ... ) {
	//func();
};

template< void (*t_func)() > class getter<0> {
	t_func();
}

JNIEXPORT int get( simlab ret, simlab from, simlab what, simlab where ) {
	//getter( t_get, ret, from, what, where );

	return 4;
}*/
