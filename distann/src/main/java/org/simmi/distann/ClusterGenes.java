package org.simmi.distann;

import org.apache.spark.api.java.function.MapFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class ClusterGenes implements MapFunction<String, String> {
    int id;
    int cmplen;

    public ClusterGenes() {}

    public String getClusters(String query) throws IOException {
        Set<java.lang.String> all = new HashSet<>();
        StringReader sr = new StringReader(query);
        BufferedReader br = new BufferedReader(sr);
        String line = br.readLine();
        while( line != null ) {
            if( line.startsWith("Query=") ) {
                StringBuilder trim = new StringBuilder(line.substring(7));
                line = br.readLine();
                while( line != null && !line.startsWith("Length") ) {
                    trim.append(" ").append(line);
                    line = br.readLine();
                }

                int i = trim.toString().indexOf(' ');
                if( i == -1 ) i = trim.length();
                java.lang.String astr = trim.substring(0, i);

                if( astr.contains("..") ) {
                    int k = trim.toString().indexOf('[');
                    int u = trim.toString().indexOf(']', k+1);
                    if( u != -1 ) {
                        astr = trim.substring(k+1,u)+"_"+astr;
                    }
                }

                /*Gene g = refmap.get( astr );
                if( g != null ) {
                    astr = g.getLongName();//astr + " ["+cont+"] # " + g.tegeval.start + " # " + g.tegeval.stop + " # " + g.tegeval.ori;
                }*/

                all.add( astr );
            } else if( line.startsWith("Sequences prod") ) {
                line = br.readLine();
                while( line != null && !line.startsWith("Query=") ) {
                    if( line.startsWith(">") ) {
                        StringBuilder trim = new StringBuilder(line.substring(1).trim());
                        line = br.readLine();
                        while( line != null && !line.startsWith("Length") ) {
                            trim.append(line);
                            line = br.readLine();
                        }

                        if(line != null && line.startsWith("Length")) {
                            int len = Integer.parseInt( line.substring(7) );

                            line = br.readLine();
                            while( line != null && !line.trim().startsWith("Identities")) line = br.readLine();

                            if(line != null) {
                                int idx0 = line.indexOf('/');
                                int idx1 = line.indexOf('(');
                                int idx2 = line.indexOf('%');

                                int percid = Integer.parseInt(line.substring(idx1 + 1, idx2));
                                int lenid = Integer.parseInt(line.substring(idx0 + 1, idx1 - 1));
                                //int v = val.indexOf("contig");

                                if (percid >= id * 100 && lenid >= len * cmplen) {
                                    int i = trim.toString().indexOf(' ');
                                    if (i == -1) i = trim.length();
                                    String astr = trim.substring(0, i);

                                    if (astr.contains("..")) {
                                        int k = trim.toString().indexOf('[');
                                        int u = trim.toString().indexOf(']', k + 1);
                                        if (u != -1) {
                                            astr = trim.substring(k + 1, u) + "_" + astr;
                                        }
                                    }

                                /*Gene g = refmap.get( astr );
                                if( g != null ) {
                                    astr = g.getLongName();
                                }*/
                                    all.add(astr); //.replace(".fna", "") );
                                }
                            }
                        }
                    }
                    line = br.readLine();
                }
            }
            line = br.readLine();
        }
        return all.toString();
    }

    @Override
    public String call(String input) throws Exception {
        return getClusters(input);
    }
}
