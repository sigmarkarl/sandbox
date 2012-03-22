package org.simmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneIO {

	public static byte comp( byte c ) {
        if( c == 'A' ) return 'T';
        else if( c == 'C' ) return 'G';
        else if( c == 'G' ) return 'C';
        else if( c == 'T' ) return 'A';
        else if( c == 'a' ) return 't';
        else if( c == 'c' ) return 'g';
        else if( c == 'g' ) return 'c';
        else if( c == 't' ) return 'a';

        return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File	dna = new File( args[0] );
		byte[]	buffer = new byte[(int)dna.length()];		
		//byte[]	rbuffer = new byte[(int)dna.length()];
		try {
			FileInputStream	fis = new FileInputStream( dna );
			fis.read( buffer );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean rc = false;
		for( String arg : args ) {
			if( arg.equals("--rc") ) {
				rc = true;
				break;
			}
		}
		
		boolean c = false;
		for( String arg : args ) {
			if( arg.equals("--c") ) {
				c = true;
				break;
			}
		}
		
		int f = 0;
		for( String arg : args ) {
			if( arg.equals("--f1") ) {
				f = 1;
				break;
			} else if( arg.equals("--f2") ) {
				f = 2;
				break;
			}
		}
		
		boolean o = false;
		for( String arg : args ) {
			if( arg.equals("--o") ) {
				o = true;
				break;
			}
		}
		
		if( rc ) {
			for( int i = 0; i < buffer.length/2; i++ ) {
				byte t = buffer[i];
				buffer[i] = comp( buffer[buffer.length-i-1] );
				buffer[buffer.length-i-1] = comp( t );
			}
		} else if( c ) {
			for( int i = 0; i < buffer.length; i++ ) {
				buffer[i] = comp( buffer[i] );
			}
		}
		
		if( o ) {
			try {
				FileOutputStream fos = new FileOutputStream("simmi.out.txt");
				fos.write( buffer );
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for( int i = f; i < buffer.length-3; i+=3 ) {
			String cs = GBK2AminoFasta.amimap.get( new String(buffer,i,3).toUpperCase() );
			System.out.print( cs );
		}
	}
}
