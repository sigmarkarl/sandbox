package org.simmi.distann;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.simmi.javafasta.shared.FastaSequence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SparkMakedb implements ForeachPartitionFunction<FastaSequence> {
    String dbPath;
    List<String> makeblastdbcmd;
    String envMap;

    public SparkMakedb(String[] makeblastdb, String envMap, String dbPath) {
        this.dbPath = dbPath;
        this.envMap = envMap;
        this.makeblastdbcmd = Arrays.asList(makeblastdb);
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

        int procs = Runtime.getRuntime().availableProcessors();
        List<String> cmds = new ArrayList<>(makeblastdbcmd);    
        //cmds.addAll(Arrays.asList("-dbtype", "prot", "-title", dbPath.getFileName().toString(), "-out", dbPath.toString()));
        cmds.addAll(Arrays.asList("--db", dbPath.getFileName().toString(), "--out", dbPath.toString()+"_out"));

        ProcessBuilder pb = new ProcessBuilder(cmds);
        if(envMap!=null) Arrays.stream(envMap.split(",")).map(env -> env.split("=")).filter(s -> s.length==2).forEach(s -> pb.environment().put(s[0],s[1]));
        Process pc = pb.start();
        Future<Long> fout = es.submit(() -> {
            try(InputStream is = pc.getInputStream()) {
                return is.transferTo(System.out);
            }
        });
        Future<Long> ferr = es.submit(() -> {
            //Path berr = root.resolve("make"+rnd+".err");
            try(InputStream is = pc.getErrorStream()) {
                return is.transferTo(System.err);
            }
        });
        try(OutputStream os = pc.getOutputStream()) {
            //pc.waitFor();
            Files.copy(dbPath, os);
        } finally {
            fout.get();
            ferr.get();
            pc.waitFor();
            es.shutdown();
        }
    }
}
