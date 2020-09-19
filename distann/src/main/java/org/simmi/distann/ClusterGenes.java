package org.simmi.distann;

import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClusterGenes implements FlatMapFunction<String, String> {
    int id;
    int cmplen;

    public ClusterGenes() {}

    public List<String> getClusters(BufferedReader br) throws IOException {
        Set<java.lang.String> all = null;
        java.lang.String line = br.readLine();
        List<String> ret = new ArrayList<>();
        while( line != null ) {
            if( line.startsWith("Query=") ) {
                StringBuilder trim = new StringBuilder(line.substring(7));
                line = br.readLine();
                while( line != null && !line.startsWith("Length") ) {
                    trim.append(" ").append(line);
                    line = br.readLine();
                }

                if( all != null && all.size() > 0 ) {
                    ret.add(all.toString());
                    //joinSets( all, total );
                }
                all = new HashSet<>();

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

                        if( line != null ) {
                            int len = Integer.parseInt( line.substring(7) );

                            line = br.readLine();
                            if( line != null ) line = line.trim();
                            else continue;

                            while( !line.startsWith("Identities") ) {
                                line = br.readLine().trim();
                            }

                            int idx0 = line.indexOf('/');
                            int idx1 = line.indexOf('(');
                            int idx2 = line.indexOf('%');

                            int percid = Integer.parseInt( line.substring(idx1+1, idx2) );
                            int lenid = Integer.parseInt( line.substring(idx0+1, idx1-1) );
                            //int v = val.indexOf("contig");

                            if( percid >= id*100 && lenid >= len*cmplen ) {
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
                                    astr = g.getLongName();
                                }*/
                                all.add( astr ); //.replace(".fna", "") );
                            }
                        }
                    }
                    line = br.readLine();
                }
                if( line == null ) break;
                else continue;
            }
            line = br.readLine();
        }
        return ret;
    }

    @Override
    public Iterator<String> call(String s) throws Exception {
        Path p = Paths.get(s);
        BufferedReader br = Files.newBufferedReader(p);
        List<String> list = getClusters(br);
        return list.iterator();
    }
}
