package org.simmi;

import java.math.BigInteger;

public class Exponential {
    public static void main( String[] dict, String clause ) {
    	clause = clause.replaceAll("[ \\.]", "");
    	
    	int N = dict.length;
	    // read in input data
	    BigInteger[] a = new BigInteger[N];
	    for (int i = 0; i < N; i++)
	        a[i] = BigInteger.ONE;//StdIn.readLong();
	
	    // find subset closest to 0
	    long best = Long.MAX_VALUE;
	    for (int n = 1; n < (1 << N); n++)  {
	        long sum = 0;
	        for (int i = 0; i < N; i++) 
	            if (((n >> i) & 1) == 1) sum = BigInteger.ONE.longValue();//sum + a[i];
	        if (Math.abs(sum) < Math.abs(best)) best = sum;
	    }
	    System.out.println(best);
    }
}