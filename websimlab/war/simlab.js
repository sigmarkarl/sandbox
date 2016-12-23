var current;
var textarea;
var ind;
var fr;
var acontext;

var init = function() {
	textarea = document.getElementById('command');
	fr = document.getElementById('fileread');

	window.AudioContext = window.AudioContext||window.webkitAudioContext;
	acontext = new AudioContext();

	fr.onchange = function( e ) {
		var file = fr.files[0];
		var frd = new FileReader();
		frd.onload = function( es ) {
			current = new Int8Array( es.target.result );
		}
		frd.readAsArrayBuffer( file );
	}

	echo( 'Welcome to Simlab\n' );

	ind = textarea.value.length;
	textarea.onkeypress = function( e ) {
		if( e.keyCode == 13 ) {
			var command = textarea.value.substring( ind );
			//window.console.log( command );
			eval( command );
			ind = textarea.value.length;
		}
	}
}

function str(buf) {
	return String.fromCharCode.apply(null, new Uint8Array(buf.buffer));
}

var store = function( name ) {
	window[name] = current;
}

var fetch = function( name ) {
	current = window[name];
}

var read = function() {
	fr.click();
}

function line( title, xdata ) {
	var data = new google.visualization.DataTable();
        data.addColumn('number', '');
        data.addColumn('number', '');
	dv = [];
	if( typeof xdata == 'undefined' ) {
		for( i = 0; i < current.length; i++ ) {
			dv[i] = [i, current[i]];
		}
	} else {
		for( i = 0; i < current.length; i++ ) {
			dv[i] = [xdata[i], current[i]];
		}
	}
	data.addRows( dv );

        var options = {'title':title,
                       'width':800,
                       'height':600};

        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, options);
}

var welcome = function() {
	echo( 'Welcome to Simlab 1.0\n' );
}

var echo = function( str ) {
	process.stdout.write( str );
	//console.log( str );
	//textarea.value = textarea.value + str;
}

var flip = function( val ) {
	if( typeof val == 'undefined' ) {
		for( i = 0; i < current.length/2; i++ ) {
			var tmp = current[i];
			current[i] = current[current.length-i-1];
			current[current.length-i-1] = tmp;
		}
	} else {
		for( k = 0; k < current.length; k+=val ) {
			for( i = k; i < (k+val)/2; i++ ) {
				var tmp = current[i];
				current[i] = current[k+val-(i-k)-1];
				current[k+val-(i-k)-1] = tmp;
			}
		}
	}
}

var diff = function( clen ) {
	if( clen == null ) clen = current.length;
	for( k = 0; k < current.length; k+=clen ) {
		for( i = k+clen-1; i > k; i-- ) {
			current[i] -= current[i-1];
		}
	}
}

var integ = function( clen ) {
	if( clen == null ) clen = current.length;
	for( k = 0; k < current.length; k+=clen ) {
		for( i = k+1; i < k+clen; i++ ) {
			current[i] += current[i-1];
		}
	}
}

var add = function( val ) {
	if( typeof val == 'number' )
	for( i = 0; i < current.length; i++ ) {
		current[i] += val;
	}
}

var sub = function( val ) {
	for( i = 0; i < current.length; i++ ) {
		curent[i] -= val[i%val.length];
	}
}

var mul = function( val ) {
	for( i = 0; i < current.length; i++ ) {
		current[i] *= val[i%val.length];
	}
}

var div = function( val ) {
	for( i = 0; i < current.length; i++ ) {
		current[i] /= val;
	}
}

var floor = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.floor( current[i] );
	}
}

var ceil = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.ceil( current[i] );
	}
}

var round = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.round( cur[i] );
	}
}

var sin = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.sin( current[i] );
	}
}

var cos = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.cos( current[i] );
	}
}

var idx = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = i;
	}
}

var rnd = function() {
	for( i = 0; i < current.length; i++ ) {
		current[i] = Math.random();
	}
}

var gcd = function( a, b ) {
	if( b == 0 ) return a;
	else return gcd( b, a%b );
}

var matmul = function( mul, val ) {
	if( val == null ) val = gcd( current.length, mul.length );
	//int length = (data.length/val)*(mul.length/val);
	//printf( "%d\n", val );
	var retc = (mul.length/val);
	var retr = (current.length/val);
	var size = retc*retr;
	var ret;

	if( typeof current == 'Int8Array' ) ret = new Int8Array( size );
	else if( typeof current == 'Int16Array' ) ret = new Int16Array( size );
	else if( typeof current == 'Int32Array' ) ret = new Int32Array( size );
	else if( typeof current == 'Float32Array' ) ret = new Float32Array( size );
	else if( typeof current == 'Float64Array' ) ret = new Float64Array( size );
	else ret = new Float64Array( size );

	//T* ret = new T[retlen];
	//memset( ret, 0, sizeof( T )*retlen );
	for( r = 0; r < retr; r++ ) {
		var rretc = r*retc;
		for( c = 0; c < retc; c++ ) {
			var reti = rretc+c;
			var rval = r*val;
			for( i = 0; i < val; i++ ) {
				ret[reti] += current[rval+i]*mul[i*retc+c];
			}
		}
	}
	current = ret;
}

var sum = function( chunk ) {
	if( chunk == null ) chunk = current.length;

	var size = current.length/chunk;
	if( typeof current == 'Int8Array' ) ret = new Int8Array( size );
	else if( typeof current == 'Int16Array' ) ret = new Int16Array( size );
	else if( typeof current == 'Int32Array' ) ret = new Int32Array( size );
	else if( typeof current == 'Float32Array' ) ret = new Float32Array( size );
	else if( typeof current == 'Float64Array' ) ret = new Float64Array( size );
	else ret = new Float64Array( size );

	for( i = 0; i < current.length; i+=chunk ) {
		var ri = i/chunk;
		for( k = i; k < i+chunk; k++ ) {
			ret[ri] += current[k];
		}
	}
	current = ret;
}

var avg = function( chunk ) {
	if( chunk == null ) chunk = current.length;
	sum( chunk );
	for( k = 0; k < current.length; k++ ) {
		current[k] /= chunk;
	}
}

const Float64 = 66
const Int64 = 65
const Uint64 = 64
const Float32 = 34
const Int32 = 33
const Uint32 = 32
const Int16 = 17
const Uint16 = 16
const Int8 = 9
const Uint8 = 8

var type = function( t ) {
	if( t == 66 ) current = new Float64Array( 0 );
	else if( t == 34 ) current = new Float32Array( 0 );
	else if( t == 33 ) current = new Int32Array( 0 );
	else if( t == 32 ) current = new Uint32Array( 0 );
	else if( t == 17 ) current = new Int16Array( 0 );
	else if( t == 16 ) current = new Uint16Array( 0 );
	else if( t == 9 ) current = new Int8Array( 0 );
	else if( t == 8 ) current = new Uint8Array( 0 );
}

var resize = function( size ) {
	if( typeof size != 'number' ) size = size[0];

	if( typeof current == 'Int8Array' ) current = new Int8Array( size );
	else if( typeof current == 'Int16Array' ) current = new Int16Array( size );
	else if( typeof current == 'Int32Array' ) current = new Int32Array( size );
	else if( typeof current == 'Float32Array' ) current = new Float32Array( size );
	else if( typeof current == 'Float64Array' ) current = new Float64Array( size );
	else current = new Float64Array( size );
}

var dump = function( val ) {
	if( typeof val == 'number' ) {
		var nval = new Int32Array(1);
		nval[0] = val;
		val = nval;
	} else if( typeof val == 'undefined' ) {
		val = new Int32Array(1);
		val[0] = 10;
	}

	var ret = "";
	var ncount = 0;
	var next = val;
	for( i = 0; i < current.length; i++ ) {
		if( i == next ) {
			ret += current[i];
			next += val;
		} else {
			ret = current[i];
		}
	}
}

function fmod(x, y) {
	var tmp, tmp2, p = 0,
	pY = 0,
	l = 0.0,
	l2 = 0.0;

	tmp = x.toExponential().match(/^.\.?(.*)e(.+)$/);
	p = parseInt(tmp[2], 10) - (tmp[1] + '').length;
	tmp = y.toExponential().match(/^.\.?(.*)e(.+)$/);
	pY = parseInt(tmp[2], 10) - (tmp[1] + '').length;

	if (pY > p) {
		p = pY;
	}

	tmp2 = (x % y);

	if (p < -100 || p > 20) {
		// toFixed will give an out of bound error so we fix it like this:
		l = Math.round(Math.log(tmp2) / Math.log(10));
		l2 = Math.pow(10, l);

		return (tmp2 / l2).toFixed(l - p) * l2;
	} else {
		return parseFloat(tmp2.toFixed(-p));
	}
}

var log = function( str ) {
	window.console.log( str );
}

var _gcd = function( a, b ) {
	if( b == 0 ) return a;
	return _gcd( b, a%b );
}

var shift = function( chk, sft ) {
	var u = 0;
	var chunk = chk; //[u];
	var shift = sft; //[u];
	u++;

	shift %= chunk;
	if( shift < 0 ) shift = chunk + shift;
	var ec = _gcd( shift, chunk );

	for( var r = 0; r < current.length; r+=chunk ) {
		for( var i = 0; i < ec; i++ ) {
			var k = i;
			var	tmp1;
			var	tmp2 = current[r+k];
			do {
				//window.console.log( k );
				tmp1 = tmp2;
				k = (k+shift)%chunk;
				tmp2 = current[r+k];
				current[r+k] = tmp1;
			} while( k != i );
		}

		chunk = chk; //[u];
		shift = sft; //[u];
		u++;

		shift %= chunk;
		if( shift < 0 ) shift = chunk + shift;
		ec = _gcd( shift, chunk );
	}
}

var trans = function( c, r ) {
	var len = c*r;

	var m = len-1;
	var i = 0;
	var l = 1;

	while( i < m-2 && l < m ) {
		var k = (l*r) % m;
		var t = (l*c) % m;
		if( k > l && t > l ) {
			k = (k*r) % m;
			while( k > l ) {
				k = (k*r) % m;
			}
			if( k == l ) {
				var	to = current[l];
				k = (l*r) % m;
				var ti = current[k];

				while( k != l ) {
					current[k] = to;
					to = ti;
					k = (k*r) % m;
					ti = current[k];
					i++;
				}
				current[k] = to;
				i++;
			}
		}
		l++;
	}
}

var play = function() {
	acontext.decodeAudioData( current.buffer, function(abuffer) {
		var source = acontext.createBufferSource();
		source.buffer = abuffer;
		source.connect( acontext.destination );
		source.start(0);
	});
}

var print = function( val ) {
	if( typeof val == 'number' ) {
		var nval = new Int32Array(1);
		nval[0] = val;
		val = nval;
	} else if( typeof val == 'undefined' ) {
		val = new Int32Array(1);
		val[0] = 10;
	}

	var ncount = 0;
	var next = 0;
	for( i = 0; i < current.length; i++ ) {
		echo( current[i]+'\t' );
	}
	echo( '\n' );
}

var readline = require('readline');

var parse = function( rl ) {
	rl.question("", function( command ) {
		if( command.indexOf('quit') == 0 ) rl.close();
		else {
			eval( command );
			parse( rl );
		}
	});
}

var main = function() {
	var rl = readline.createInterface({
		  input: process.stdin,
		  output: process.stdout
	});

	welcome();
	parse( rl );
}

if( require.main === module ) {
    main();
}
