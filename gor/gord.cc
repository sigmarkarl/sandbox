#include <stdio.h>
#include <string.h>
#include <vector>
#include <queue>

struct genomepos {
	const char*	chr;
	int		pos;

	int cmp( const genomepos& rhs ) const {
		int scmp = strcmp( rhs.chr, chr );
		return scmp != 0 ? scmp : rhs.pos - pos;
	}
	bool operator< (const genomepos& rhs) const { return cmp(rhs) < 0; }
};

int main( int argc, char** argv ) {
	char	buffer[1000000];

	/*char* filename = argv[1];
	FILE*	f = fopen( filename, "r" );
	fseek( f, 0, SEEK_END );
	long l = ftell( f );
	fseek( f, 0, SEEK_SET );

	int r = fread( buffer, 1, sizeof(buffer), f );
	std::vector<char*> v;
	int i = 0;
	while( i < r ) {
		v.push_back( buffer+i );
		while( i < r && buffer[++i] != '\t' );
		buffer[i] = 0;
		v.push_back( buffer+i+1 );
		while( i < r && buffer[++i] != '\n' );
		buffer[i++] = 0;
	}*/

	genomepos g1;
	genomepos g2;
	genomepos g3;
	genomepos g4;

	g1.chr = "chr1";
	g1.pos = 200;
	g2.chr = "chr1";
	g2.pos = 2;
	g3.chr = "chr2";
	g3.pos = 2;
	g4.chr = "chr1";
	g4.pos = 50;

	std::priority_queue<genomepos>	pq;
	pq.push( g1 );
	pq.push( g2 );
	pq.push( g3 );
	pq.push( g4 );

	while( !pq.empty() ) {
		genomepos gp = pq.top();
		printf( "%s %d\n", gp.chr, gp.pos );
		pq.pop();
	}

	return 0;
}
