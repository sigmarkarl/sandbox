package org.simmi.distann;

import org.apache.spark.api.java.function.MapFunction;
import scala.Tuple2;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkMafft implements MapFunction<Tuple2<String,String>, Tuple2<String,String>> {
    String root;

    public SparkMafft(String rootpath) {
        this.root = rootpath;
    }

    @Override
    public Tuple2<String,String> call(Tuple2<String, String> value) throws IOException, ExecutionException, InterruptedException {
        String fasta = value._2;
        ExecutorService es = Executors.newFixedThreadPool(2);

        //Path resPath = rootpath.resolve(id+".aa");
        // zipfilesystem.getPath(group + ".blastout");
        //int procs = Runtime.getRuntime().availableProcessors();
        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        ProcessBuilder pb = new ProcessBuilder("mafft", "-");
        Process pc = pb.start();
        Future<Long> fout = es.submit(() -> {
            try(InputStream is = pc.getInputStream()) {
                return is.transferTo(sb);
            }
        });
        Future<Long> ferr = es.submit(() -> {
            try(InputStream is = pc.getErrorStream()) {
                return is.transferTo(System.err);
            }
        });
        try(Writer w = new OutputStreamWriter(pc.getOutputStream())) {
            w.write(fasta);
        }
        fout.get();
        ferr.get();
        pc.waitFor();
        es.shutdown();
        sb.close();

        //System.err.println("procs " + procs);
        //SerifyApplet.blastRun(nrun, queryPath, Paths.get(dbpath), resPath, "prot", "-num_threads " + procs + " -evalue 0.00001", null, true, user, true);

        return new Tuple2<>(value._1, sb.toString());
        //return Files.lines(resPath).iterator();
        //return Iterators.emptyIterator();
    }
}
