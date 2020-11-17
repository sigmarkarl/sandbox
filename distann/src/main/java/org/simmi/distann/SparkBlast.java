package org.simmi.distann;

import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.simmi.javafasta.shared.FastaSequence;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SparkBlast implements MapPartitionsFunction<FastaSequence, String> {
    String root;
    String tmp;
    String blastp;
    String envMap;

    public SparkBlast(String blastp,String env,String rootpath,String tmppath) {
        this.root = rootpath;
        this.blastp = blastp;
        this.tmp = tmppath;
        this.envMap = env;
    }

    @Override
    public Iterator<String> call(Iterator<FastaSequence> input) throws IOException, ExecutionException, InterruptedException {
        if (input.hasNext()) {
            Path rootpath = Paths.get(root);
            Path tmpPath = Paths.get(tmp);

            Random r = new Random();
            int rnd = r.nextInt();

            FastaSequence next = input.next();
            String group = next.getGroup();
            ExecutorService es = Executors.newFixedThreadPool(3);

            Path resPath = tmpPath.resolve(group+".blastout");//zipfilesystem.getPath(group + ".blastout");
            Path dbpath = rootpath.resolve("db.fsa");

            int procs = Runtime.getRuntime().availableProcessors();
            ProcessBuilder pb = new ProcessBuilder(blastp, "-db", dbpath.toString(), "-num_threads", Integer.toString(procs), "-evalue", "0.00001"); //"-out", resPath.toString(),
            if(envMap!=null) Arrays.stream(envMap.split(",")).map(env -> env.split("=")).filter(s -> s.length==2).forEach(s -> pb.environment().put(s[0],s[1]));
            Process pc = pb.start();
            /*Future<Long> fout = es.submit(() -> {
                try(InputStream is = pc.getInputStream()) {
                    return is.transferTo(System.out);
                }
            });*/
            String hostname = InetAddress.getLocalHost().getHostName();
            Future<Long> ferr = es.submit(() -> {
                Path berr = rootpath.resolve("blast"+rnd+".err");
                try(InputStream is = pc.getErrorStream(); OutputStream fos = Files.newOutputStream(berr)) {
                    fos.write(hostname.getBytes());
                    fos.write('\n');
                    return is.transferTo(fos);
                }
            });
            Future<Long> fout = es.submit(() -> {
                try (OutputStream os = pc.getOutputStream(); Writer w = new OutputStreamWriter(os)) {
                    next.writeSequence(w);
                    while (input.hasNext()) {
                        FastaSequence fs = input.next();
                        fs.writeSequence(w);
                    }
                    return 0L;
                }
            });
            SynchronousQueue<List<String>> sq = new SynchronousQueue<>();
            Future<Long> fin = es.submit(() -> {
                try (InputStreamReader isr = new InputStreamReader(pc.getInputStream()); BufferedReader br = new BufferedReader(isr)) {
                    Iterator<String> it = br.lines().iterator();
                    Iterator<String> qit = new Iterator<>() {
                        StringBuilder next;
                        String last;
                        boolean closed = true;

                        {
                            while (it.hasNext()) {
                                last = it.next();
                                if (last.startsWith("Query=")) {
                                    closed = false;
                                    break;
                                }
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            if (!closed) {
                                next = new StringBuilder();
                                next.append(last);
                                closed = true;
                                while (it.hasNext()) {
                                    last = it.next();
                                    if (last.startsWith("Query=")) {
                                        closed = false;
                                        break;
                                    } else {
                                        next.append('\n');
                                        next.append(last);
                                    }
                                }
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public String next() {
                            return next.toString();
                        }
                    };

                    List<String> qlist = new ArrayList<>();
                    int count = 0;
                    while(qit.hasNext()) {
                        String query = qit.next();
                        qlist.add(query);
                        if(128 == ++count) {
                            sq.put(qlist);
                            qlist = new ArrayList<>();
                            count = 0;
                        }
                    }
                    sq.put(qlist);
                    if(qlist.size()>0) sq.put(Collections.emptyList());

                    return 0L;
                }
            });
            ClusterGenes clusterGenes = new ClusterGenes();
            ReduceClusters reduceClusters = new ReduceClusters();
            return new Iterator<>() {
                List<Set<String>> queries;

                @Override
                public boolean hasNext() {
                    try {
                        List<String> res = sq.take();
                        if(res.size()==0) {
                            fin.get();
                            fout.get();
                            ferr.get();
                            pc.waitFor();
                            es.shutdown();
                            return false;
                        }
                        Optional<List<Set<String>>> ores = res.stream().parallel().map(clusterGenes).map(Collections::singletonList).reduce(reduceClusters);
                        queries = ores.orElse(Collections.emptyList());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public String next() {
                    return queries.stream().map(Object::toString).collect(Collectors.joining(";"));
                }
            };

            //System.err.println("procs " + procs);
            //SerifyApplet.blastRun(nrun, queryPath, Paths.get(dbpath), resPath, "prot", "-num_threads " + procs + " -evalue 0.00001", null, true, user, true);

            //return Iterators.singletonIterator(resPath.toString());
            //return Files.lines(resPath).iterator();*/
        }
        return Collections.emptyIterator();
    }
}
