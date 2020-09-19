package org.simmi.distann;

import com.google.common.collect.Iterators;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.simmi.javafasta.shared.FastaSequence;

import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkBlast implements MapPartitionsFunction<FastaSequence, String> {
    String root;

    public SparkBlast(String rootpath) {
        this.root = rootpath;
    }

    @Override
    public Iterator<String> call(Iterator<FastaSequence> input) throws IOException, ExecutionException, InterruptedException {
        if (input.hasNext()) {
            Path rootpath = Paths.get(root);

            FastaSequence next = input.next();
            String group = next.getGroup();
            ExecutorService es = Executors.newFixedThreadPool(2);

            Path resPath = rootpath.resolve(group+".blastout");//zipfilesystem.getPath(group + ".blastout");
            Path dbpath = rootpath.resolve("db.fsa");

            int procs = Runtime.getRuntime().availableProcessors();
            ProcessBuilder pb = new ProcessBuilder("blastp", "-db", dbpath.toString(), "-out", resPath.toString(), "-num_threads", Integer.toString(procs), "-evalue", "0.00001");
            Process pc = pb.start();
            Future<Long> fout = es.submit(() -> {
                try(InputStream is = pc.getInputStream()) {
                    return is.transferTo(System.out);
                }
            });
            Future<Long> ferr = es.submit(() -> {
                try(InputStream is = pc.getErrorStream()) {
                    return is.transferTo(System.err);
                }
            });
            try(OutputStream os = pc.getOutputStream()) {
                Writer w = new OutputStreamWriter(os);
                next.writeSequence(w);
                while (input.hasNext()) {
                    FastaSequence fs = input.next();
                    fs.writeSequence(w);
                }
            }
            fout.get();
            ferr.get();
            pc.waitFor();
            es.shutdown();

            //System.err.println("procs " + procs);
            //SerifyApplet.blastRun(nrun, queryPath, Paths.get(dbpath), resPath, "prot", "-num_threads " + procs + " -evalue 0.00001", null, true, user, true);

            return Iterators.singletonIterator(resPath.toString());
            //return Files.lines(resPath).iterator();
        }
        return Iterators.emptyIterator();
    }
}
