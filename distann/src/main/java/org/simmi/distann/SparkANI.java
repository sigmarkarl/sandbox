package org.simmi.distann;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;

import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkANI implements MapFunction<Row, String> {
    public SparkANI() {}

    @Override
    public String call(Row value) throws Exception {
        String makeblastdb = "makeblastdb";
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.contains("mac")) makeblastdb = "/usr/local/bin/makeblastdb";

        String f1 = value.getString(0);
        String f2 = value.getString(1);

        int i1 = f1.indexOf('\n');
        int i2 = f2.indexOf('\n');

        String spec1 = f1.substring(0,i1);
        String spec2 = f2.substring(0,i2);

        String fasta1 = f1.substring(i1+1);
        String fasta2 = f2.substring(i2+1);

        String spec = spec1+"_"+spec2;

        ExecutorService es = Executors.newFixedThreadPool(2);
        ProcessBuilder pb = new ProcessBuilder(makeblastdb,"-dbtype","nucl","-title",spec,"-out",spec);
        File dir = Paths.get(System.getProperty("user.home")).resolve("rho").toFile();

        pb.directory( dir );
        Process p = pb.start();
        Future<Long> ferr1 = es.submit(() -> {
            try(InputStream is = p.getErrorStream()) {
                return is.transferTo(System.err);
            }
        });
        OutputStream out = p.getOutputStream();
        out.write(fasta1.getBytes());
        out.close();
        ferr1.get();
        p.waitFor();

        String blastn = "blastn";
        if(OS.contains("mac")) blastn = "/usr/local/bin/blastn";
        pb = new ProcessBuilder(blastn,"-db",spec,
                "-num_threads",Integer.toString(Runtime.getRuntime().availableProcessors()),
                "-num_alignments","1","-num_descriptions","1"); //,"-max_hsps","1");

        pb.directory( dir );

        Process p2 = pb.start();
        Future<Long> ferr = es.submit(() -> {
            try(InputStream is = p2.getErrorStream()) {
                return is.transferTo(System.err);
            }
        });
        Future<Long> fout = es.submit(() -> {
            try(OutputStream out2 = p2.getOutputStream()) {
               out2.write(fasta2.getBytes());
            }
            return 0L;
        });

        //int tnum = 0;
        //int tdenum = 0;
        double avg = 0.0;
        int count = 0;

        BufferedReader br = new BufferedReader( new InputStreamReader(p2.getInputStream()) );
        String line = br.readLine();
        while( line != null ) {
            if( line.startsWith(" Identities") ) {
                int i = line.indexOf('(');
                String sub = line.substring(14,i-1);
                String[] split = sub.split("/");
                int num = Integer.parseInt(split[0]);
                int denum = Integer.parseInt(split[1]);

                avg += (double)num/(double)denum;

                //tnum += num;
                //tdenum += denum;
                count++;
            }
            line = br.readLine();
        }
        br.close();

        if( count > 0 ) avg /= count;

        fout.get();
        ferr.get();
        p.waitFor();
        es.shutdown();
        //double val = (double)tnum/(double)tdenum;
        //matrix[y*species.size()+x] = avg;//val;
        //System.err.println( spec + " on " + dbspec + " " + val );

        return spec1 + "/" + spec2 + "/" + avg;
    }
}
