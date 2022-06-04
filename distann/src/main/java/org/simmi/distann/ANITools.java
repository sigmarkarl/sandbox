package org.simmi.distann;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.simmi.ann.ANIResult;
import org.simmi.javafasta.shared.*;
import org.simmi.javafasta.unsigned.JavaFasta;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ANITools {
    public static void aaiAction(GeneSetHead genesethead, List<GeneGroup> lgg) {
        GeneSet geneset = genesethead.geneset;
        Set<String> species = genesethead.getSelspec(genesethead, geneset.specList);
        //List<String> speclist = new ArrayList<>(species);

        Collection<GeneGroup> allgg = lgg.isEmpty() ? geneset.allgenegroups : lgg;
        List<FastaSequence> allAligned = allgg.stream()
                .flatMap(gg -> species.stream().map(s -> gg.species.get(s)).filter(Objects::nonNull))
                .flatMap(t -> t.tset.stream())
                .filter(Objects::nonNull).map(Annotation::getAlignedSequence)
                .filter(Objects::nonNull).collect(Collectors.toList());


        SparkSession spark = SparkSession.builder()
                .master("local[4]")
                /*.master("spark://10.42.0.223:7077")
                //.master("k8s://http://127.0.0.1:8001")
                //.config("spark.submit.deployMode","cluster")
                .config("spark.driver.memory","490m")
                .config("spark.driver.cores",2)
                //.config("spark.executor.instances",10)
                .config("spark.executor.memory","490m")
            .config("spark.executor.cores",2)
            .config("spark.executor.instances",16)*/
                /*.config("spark.kubernetes.namespace","spark")
                .config("spark.kubernetes.container.image","nextcode/spark:3.0.1-redis")
                .config("spark.kubernetes.executor.container.image","nextcode/spark:3.0.1-redis")
                .config("spark.kubernetes.container.image.pullSecrets", "dockerhub-nextcode-download-credentials")
                .config("spark.kubernetes.container.image.pullPolicy", "Always")*/
                /*.config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                .config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")*/
                .getOrCreate();

        Dataset<FastaSequence> dsf = spark.createDataset(allAligned, ExpressionEncoder.javaBean(FastaSequence.class));
        //dsf.join(dsf, )

        //double[] corr = ANITools.corr(speclist, allgg, false);
        //SwingUtilities.invokeLater(() -> ANITools.showAniMatrix(geneset, speclist, corr));
    }

    public static void showAai(GeneSetHead genesethead) {
        GeneSet geneset = genesethead.geneset;
        Set<String> species = genesethead.getSelspec(genesethead, geneset.specList );

        SparkSession spark = SparkSession.builder()
                .master("local[4]")
                /*.master("spark://10.42.0.223:7077")
                //.master("k8s://http://127.0.0.1:8001")
                //.config("spark.submit.deployMode","cluster")
                .config("spark.driver.memory","490m")
                .config("spark.driver.cores",2)
                //.config("spark.executor.instances",10)
                .config("spark.executor.memory","490m")
            .config("spark.executor.cores",2)
            .config("spark.executor.instances",16)*/
                /*.config("spark.kubernetes.namespace","spark")
                .config("spark.kubernetes.container.image","nextcode/spark:3.0.1-redis")
                .config("spark.kubernetes.executor.container.image","nextcode/spark:3.0.1-redis")
                .config("spark.kubernetes.container.image.pullSecrets", "dockerhub-nextcode-download-credentials")
                .config("spark.kubernetes.container.image.pullPolicy", "Always")*/
                /*.config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                .config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")*/
                .getOrCreate();

        List<String> specList = species.stream().map(spec -> {
            int ind = spec.indexOf(' ');
            return ind == -1 ? spec : spec.substring(0, ind);
        }).toList();
        List<String> fastaSequences = species.stream().map(spec -> geneset.speccontigMap.get(spec).stream().map(s -> {
            String ret = "";
            try {
                int ind = spec.indexOf(' ');
                String nspec = ind == -1 ? spec : spec.substring(0,ind);
                ret = nspec+"\n"+s.asFasta();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }).collect(Collectors.joining("\n"))).collect(Collectors.toList());
        List<String> splitFastaSequences = species.stream().map(spec -> geneset.speccontigMap.get(spec).stream().map(s -> {
            String ret = "";
            try {
                int ind = spec.indexOf(' ');
                String nspec = ind == -1 ? spec : spec.substring(0,ind);
                ret = nspec+"\n"+s.asSplitFasta(Optional.of(200));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }).collect(Collectors.joining("\n"))).collect(Collectors.toList());
        Dataset<String> speciesDataset = spark.createDataset(fastaSequences, Encoders.STRING());
        Dataset<String> splitSpeciesDataset = spark.createDataset(splitFastaSequences, Encoders.STRING());
        Dataset<org.apache.spark.sql.Row> rowDataset = speciesDataset.crossJoin(splitSpeciesDataset);
        List<String> res = rowDataset.map(new SparkANI(), Encoders.STRING()).collectAsList();

        spark.close();

        ANIResult aniResult = new ANIResult(specList.size());
        res.stream().map(s -> s.split("/")).forEach(s -> {
            String spec1 = s[0];
            String spec2 = s[1];
            int y = specList.indexOf(spec1);
            int x = specList.indexOf(spec2);
            double avg = Double.parseDouble(s[2]);
            int cnt = Integer.parseInt(s[3]);
            aniResult.corrarr[y*specList.size()+x] = avg;
            aniResult.countarr[y*specList.size()+x] = cnt;
        });

        geneset.corrInd.clear();
        for( String spec : specList ) {
            geneset.corrInd.add( geneset.nameFix( spec ) );
        }

        final BufferedImage bi = JavaFasta.showRelation( geneset.corrInd, aniResult, false );
        JFrame f = new JFrame("TNI matrix");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(500, 500);

        JComponent comp2 = new JComponent() {
            public void paintComponent( Graphics g ) {
                super.paintComponent(g);
                g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), this);
            }
        };
        Dimension dim = new Dimension(bi.getWidth(),bi.getHeight());
        comp2.setPreferredSize(dim);
        comp2.setSize( dim );
        JScrollPane scroll = new JScrollPane(comp2);
        f.add(scroll);

        f.setVisible( true );
    }

    public static void showAni(GeneSetHead genesethead) {
        GeneSet geneset = genesethead.geneset;
        Set<String> species = genesethead.getSelspec(genesethead, geneset.specList );

        List<String> specList = species.stream().map(spec -> {
            int ind = spec.indexOf(' ');
            return ind == -1 ? spec : spec.substring(0, ind);
        }).collect(Collectors.toList());

        List<String> fastaSequences = species.stream().map(spec -> geneset.speccontigMap.get(spec).stream().map(s -> {
            String ret = "";
            try {
                int ind = spec.indexOf(' ');
                String nspec = ind == -1 ? spec : spec.substring(0, ind);
                ret = nspec + "\n" + s.asFasta();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }).collect(Collectors.joining("\n"))).collect(Collectors.toList());
        List<String> splitFastaSequences = species.stream().map(spec -> geneset.speccontigMap.get(spec).stream().map(s -> {
            String ret = "";
            try {
                int ind = spec.indexOf(' ');
                String nspec = ind == -1 ? spec : spec.substring(0, ind);
                ret = nspec + "\n" + s.asSplitFasta(Optional.of(200));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }).collect(Collectors.joining("\n"))).collect(Collectors.toList());

        var sparkAni = new SparkANI();
        List<String> res;

        boolean local = true;
        if (local) {
            res = new ArrayList<>();
            fastaSequences.parallelStream().forEach(seq1 -> {
                for(String seq2: splitFastaSequences) {
                    try {
                        res.add(sparkAni.ani(seq1, seq2));
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            SparkSession spark = SparkSession.builder()
                    .master("local[4]")
                    /*.master("spark://10.42.0.223:7077")
                    //.master("k8s://http://127.0.0.1:8001")
                    //.config("spark.submit.deployMode","cluster")
                    .config("spark.driver.memory","490m")
                    .config("spark.driver.cores",2)
                    //.config("spark.executor.instances",10)
                    .config("spark.executor.memory","490m")
                .config("spark.executor.cores",2)
                .config("spark.executor.instances",16)*/
                    /*.config("spark.kubernetes.namespace","spark")
                    .config("spark.kubernetes.container.image","nextcode/spark:3.0.1-redis")
                    .config("spark.kubernetes.executor.container.image","nextcode/spark:3.0.1-redis")
                    .config("spark.kubernetes.container.image.pullSecrets", "dockerhub-nextcode-download-credentials")
                    .config("spark.kubernetes.container.image.pullPolicy", "Always")*/
                    /*.config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                    .config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")
                .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")*/
                    .getOrCreate();

            Dataset<String> speciesDataset = spark.createDataset(fastaSequences, Encoders.STRING());
            Dataset<String> splitSpeciesDataset = spark.createDataset(splitFastaSequences, Encoders.STRING());
            Dataset<org.apache.spark.sql.Row> rowDataset = speciesDataset.crossJoin(splitSpeciesDataset);
            res = rowDataset.map(sparkAni, Encoders.STRING()).collectAsList();

            spark.close();
        }

        ANIResult aniResult = new ANIResult(specList.size());
        res.stream().map(s -> s.split("/")).forEach(s -> {
            String spec1 = s[0];
            String spec2 = s[1];
            int y = specList.indexOf(spec1);
            int x = specList.indexOf(spec2);
            double avg = Double.parseDouble(s[2]);
            int cnt = Integer.parseInt(s[3]);
            aniResult.corrarr[y*specList.size()+x] = avg;
            aniResult.countarr[y*specList.size()+x] = cnt;
        });

                /*for( String dbspec : species ) {
                    int x = 0;
                    for( String spec : species ) {
                        //if( !spec.equals(dbspec) ) {
                        final List<Sequence> lseq = geneset.speccontigMap.get(spec);
                        String blastn = "blastn";
                        if(OS.contains("mac")) blastn = "/usr/local/bin/blastn";
                        ProcessBuilder pb = new ProcessBuilder(blastn,"-db",dbspec,
                                "-num_threads",Integer.toString(Runtime.getRuntime().availableProcessors()),
                                "-num_alignments","1","-num_descriptions","1"); //,"-max_hsps","1");
                        File dir = new File( System.getProperty("user.home") );
                        pb.directory( dir );
                        try {
                            Process p = pb.start();
                            final BufferedWriter fw = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );
                            Thread t = new Thread(() -> {
								try {
									for( Sequence seq : lseq ) {
										seq.writeSplitSequence(fw);
										//seq.writeSequence(fw);
									}
									fw.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							});
                            t.start();
                            //Path path = Paths.get("/Users/sigmar/"+spec+"_"+dbspec+".blastout");
                            //Files.copy(p.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                            int tnum = 0;
                            int tdenum = 0;
                            double avg = 0.0;
                            int count = 0;

                            BufferedReader br = new BufferedReader( new InputStreamReader(p.getInputStream()) );
                            String line = br.readLine();
                            while( line != null ) {
                                if( line.startsWith(" Identities") ) {
                                    int i = line.indexOf('(');
                                    String sub = line.substring(14,i-1);
                                    String[] split = sub.split("/");
                                    int num = Integer.parseInt(split[0]);
                                    int denum = Integer.parseInt(split[1]);

                                    avg += (double)num/(double)denum;

                                    tnum += num;
                                    tdenum += denum;
                                    count++;
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if( count > 0 ) avg /= count;
                            double val = (double)tnum/(double)tdenum;
                            matrix[y*species.size()+x] = avg;//val;
                            System.err.println( spec + " on " + dbspec + " " + val );
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        //}
                        x++;
                    }
                    y++;
                }*/

        JavaFasta.showAniMatrix(specList, aniResult);
    }
}
