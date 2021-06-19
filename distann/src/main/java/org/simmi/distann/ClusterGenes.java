package org.simmi.distann;

import org.apache.spark.api.java.function.MapFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ClusterGenes implements MapFunction<String, String>, Function<String,Set<String>> {
    double id;
    double cmplen;

    public ClusterGenes() {
        id = 0.5;
        cmplen = 0.5;
    }

    public Stream<Set<String>> getClusterStream(BufferedReader br) throws IOException {
        var qit = getClusters(br);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(qit, 0), true);
    }

    public Set<String> getSingleCluster(String query) throws IOException {
        StringReader sr = new StringReader(query);
        BufferedReader br = new BufferedReader(sr);
        return getClusters(br).next();
    }

    public Iterator<Set<String>> getClusters(BufferedReader br) throws IOException {
        return new Iterator<>() {
            Set<String> all;

            private void queryLine(String line) throws IOException {
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
                all.add( astr );
            }

            @Override
            public boolean hasNext() {
                try {
                    if(all==null) {
                        String line = br.readLine();
                        while (line != null) {
                            if (line.startsWith("Query=")) {
                                all = new HashSet<>();
                                queryLine(line);
                                return true;
                            }
                            line = br.readLine();
                        }
                    } else return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public Set<String> next() {
                Set<String> ret = all;
                all = null;
                try {
                    String line = br.readLine();
                    while( line != null ) {
                        if (line.startsWith("Query=")) {
                            all = new HashSet<>();
                            queryLine(line);
                            break;
                        } else if (line.startsWith(">")) {
                            StringBuilder trim = new StringBuilder(line.substring(1).trim());
                            line = br.readLine();
                            while (line != null && !line.startsWith("Length")) {
                                trim.append(line);
                                line = br.readLine();
                            }

                            if (line != null && line.startsWith("Length")) {
                                int len = Integer.parseInt(line.substring(7));

                                line = br.readLine();
                                while (line != null && !line.trim().startsWith("Identities")) line = br.readLine();

                                if (line != null) {
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
                                        ret.add(astr); //.replace(".fna", "") );
                                    }
                                }
                            }
                        }
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ret;
            }
        };
        /*Set<String> all = new HashSet<>();
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
                }*

                all.add( astr );
            } else /*if( line.startsWith("Sequences prod") ) {
                line = br.readLine();
                while( line != null && !line.startsWith("Query=") ) {*
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
                                }*
                                    all.add(astr); //.replace(".fna", "") );
                                }
                            }
                        }
                    }
                    //line = br.readLine();
                //}
            //}
            //line = br.readLine();
        //}
        //return all;*/
    }

    @Override
    public String call(String input) throws Exception {
        return getSingleCluster(input).toString();
    }

    @Override
    public Set<String> apply(String s) {
        try {
            return getSingleCluster(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
