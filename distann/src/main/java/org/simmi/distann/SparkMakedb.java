package org.simmi.distann;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.simmi.javafasta.shared.FastaSequence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkMakedb implements ForeachPartitionFunction<FastaSequence> {
    String dbPath;
    String makeblastdbpath;
    String envMap;

    public SparkMakedb(String makeblastdb, String envMap, String dbPath) {
        this.dbPath = dbPath;
        this.envMap = envMap;
        this.makeblastdbpath = makeblastdb;
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

        Random r = new Random();
        int rnd = r.nextInt();

        ExecutorService es = Executors.newFixedThreadPool(2);

        List<String> cmds = Arrays.asList(makeblastdbpath,"-dbtype", "prot", "-title", dbPath.getFileName().toString(), "-out", dbPath.toString());
        ProcessBuilder pb = new ProcessBuilder(cmds);
        if(envMap!=null) Arrays.stream(envMap.split(",")).map(env -> env.split("=")).filter(s -> s.length==2).forEach(s -> pb.environment().put(s[0],s[1]));
        Process pc = pb.start();
        Future<Long> fout = es.submit(() -> {
            try(InputStream is = pc.getInputStream()) {
                return is.transferTo(System.out);
            }
        });
        Future<Long> ferr = es.submit(() -> {
            Path berr = root.resolve("make"+rnd+".err");
            try(InputStream is = pc.getErrorStream(); OutputStream fos = Files.newOutputStream(berr)) {
                return is.transferTo(fos);
            }
        });
        try(OutputStream os = pc.getOutputStream()) {
            Files.copy(dbPath, os);
        } finally {
            fout.get();
            ferr.get();
            pc.waitFor();
            es.shutdown();
        }
    }
}
