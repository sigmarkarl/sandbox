package org.simmi.distann;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.simmi.javafasta.shared.FastaSequence;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkMakedb implements ForeachPartitionFunction<FastaSequence> {
    String dbPath;

    public SparkMakedb(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void call(Iterator<FastaSequence> it) throws Exception {
        Path root = Paths.get(dbPath);
        Path dbPath = root.resolve("db.fsa");
        BufferedWriter bw = Files.newBufferedWriter(dbPath);
        while(it.hasNext()) {
            FastaSequence fs = it.next();
            fs.writeSequence(bw);
        }
        bw.close();

        ExecutorService es = Executors.newFixedThreadPool(2);

        List<String> cmds = Arrays.asList("makeblastdb","-dbtype", "prot", "-title", dbPath.getFileName().toString(), "-out", dbPath.toString());
        ProcessBuilder pb = new ProcessBuilder(cmds);
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
            Files.copy(dbPath, os);
        }
        fout.get();
        ferr.get();
        pc.waitFor();
        es.shutdown();
    }
}
