package org.simmi.distann;

import javafx.collections.ObservableList;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.simmi.javafasta.shared.FastaSequence;
import org.simmi.javafasta.shared.Gene;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class HHBlitsTools {
    public static void runHHBlits(GeneSetHead geneSet, String root, String db, boolean useSpark) {
        try {
            StringWriter sb = new StringWriter();

            if (useSpark) {
                Encoder<FastaSequence> seqenc = ExpressionEncoder.javaBean(FastaSequence.class);
                try (SparkSession spark = SparkSession.builder()
                        /*.master("spark://mimir.cs.hi.is:7077")
                        .config("spark.driver.memory","2g")
                        .config("spark.driver.cores",1)
                        .config("spark.executor.memory","16g")
                        .config("spark.executor.cores",32)
                        .config("spark.task.cpus",32)
                        .config("spark.executor.instances",5)
                        .config("spark.driver.host","mimir.cs.hi.is")
                        .config("spark.local.dir","/home/sks17/tmp")*/
                        //.config("spark.submit.deployMode","cluster")

                        //.config("spark.jars","/home/sks17/jars/distann.jar,/home/sks17/jars/javafasta.jar")
                        .master("local[1]")
                        /*.master("k8s://https://6A0DA5D06C34D9215711B1276624FFD9.gr7.us-east-1.eks.amazonaws.com")
                        .config("spark.submit.deployMode","cluster")
                        .config("spark.driver.memory","4g")
                        .config("spark.driver.cores",2)
                        .config("spark.executor.instances",16)
                        .config("spark.executor.memory","2g")*/
                        //.config("spark.executor.cores",12)
                        //.config("spark.jars","/Users/sigmar/sandbox/distann/build/install/distann/lib/*.jar")
                        /*.config("spark.executor.instances",10)
                        .config("spark.kubernetes.namespace","spark")
                        .config("spark.kubernetes.container.image","nextcode/glow:latest")
                        .config("spark.kubernetes.executor.container.image","nextcode/glow:latest")
                        .config("spark.kubernetes.container.image.pullSecrets", "dockerhub-nextcode-download-credentials")
                        .config("spark.kubernetes.container.image.pullPolicy", "Always")
                            .config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                            .config("spark.kubernetes.driver.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")
                        .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
                        .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")*/
                        .getOrCreate()) {
                    if (geneSet.isGeneview()) {
                        ObservableList<Gene> genes = geneSet.gtable.getSelectionModel().getSelectedItems();
                        if (genes.size() > 0) {

                            //String blastp = "/home/sks17/miniconda3/bin/blastp";
                            //String makeblastdb = "/home/sks17/miniconda3/bin/makeblastdb";
                            //String envMap = "";

							/*String blastp = "/home/sks17/ncbi-blast-2.10.1+/bin/blastp";
							String makeblastdb = "/home/sks17/ncbi-blast-2.10.1+/bin/makeblastdb";
							String envMap = "LD_LIBRARY_PATH=/home/sks17/glibc-2.14/lib/:/home/sks17/zlib-1.2.11/";*/

                            var allSeqList = geneSet.gtable.getSelectionModel().getSelectedItems().stream().map(g -> {
                                var tvs = g.getGeneGroup().getTegevals();
                                var sw = new StringWriter();
                                sw.append(g.id);
                                sw.append('\n');
                                try {
                                    if (tvs.size() > 1) {
                                        for (var tv : tvs) {
                                            tv.getAlignedSequence().writeIdSequence(sw);
                                        }
                                    } else {
                                        tvs.get(0).getProteinSequence().writeIdSequence(sw);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return sw.toString();
                            }).collect(Collectors.toList());
                            var allds = spark.createDataset(allSeqList, Encoders.STRING());
                            var res = allds.map(new SparkHHBlits(root, db), Encoders.STRING()).collectAsList();
                            res.forEach(s -> {
                                try {
                                    var n = s.indexOf('\n');
                                    if (n > 0) {
                                        var fname = s.substring(6, n).trim();
                                        Files.writeString(Path.of(fname), s);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } else {
                        var allSeqList = geneSet.table.getSelectionModel().getSelectedItems().stream().<String>mapMulti((gg, c) -> {
                            var tvs = gg.genes;
                            var first0 = tvs.stream().filter(p -> p.getId() != null /*&& p.getId().startsWith("QAY")*/).findFirst();
                            if (first0.isPresent()) {
                                var first = first0.get();
                                var sw = new StringWriter();
                                sw.append(first.getId());
                                sw.append('\n');
                                try {
                                    if (tvs.size() > 1) {
                                        for (var tv : tvs) {
                                            tv.getAlignedSequence().writeIdSequence(sw);
                                        }
                                    } else {
                                        first.getProteinSequence().writeIdSequence(sw);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                c.accept(sw.toString());
                            }
                        }).collect(Collectors.toList());
                        var allds = spark.createDataset(allSeqList, Encoders.STRING());
                        var res = allds.map(new SparkHHBlits(root, db), Encoders.STRING()).collectAsList();
                        var mapping = Path.of(root).resolve("mapping.txt");
                        Files.writeString(mapping, String.join("\n", res));
							/*res.forEach(s -> {
								try {
									var n = s.indexOf('\n');
									if (n > 0) {
										var fname = s.substring(6, n).trim();
										Files.writeString(Path.of(fname), s);
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							});*/
									/*for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
										Annotation a = null;
										for (Annotation anno : gg.genes) {
											a = anno;
											break;
										}
										Sequence gs = a.getProteinSequence();
										gs.setName(a.getId());
										gs.writeSequence(sb);
									}*/
                    }
                }/* else {
						for (Gene g : geneset.genelist) {
							if (g.getTag() == null || g.getTag().equalsIgnoreCase("gene")) {
								if (species.contains(g.getSpecies())) {
									Sequence gs = g.getTegeval().getProteinSequence();
									gs.setName(g.id);
									gs.writeSequence(sb);
								}
							}
						}
					}*/

					/*Map<String, String> env = new HashMap<>();
					env.put("create", "true");
					String uristr = "jar:" + geneset.zippath.toUri();
					geneset.zipuri = URI.create(uristr /*.replace("file://", "file:")*);
					geneset.zipfilesystem = FileSystems.newFileSystem(geneset.zipuri, env);
					Path resPath = geneset.zipfilesystem.getPath("/unresolved.blastout");

					Path dbPath = Paths.get("/data/nr");

					NativeRun nrun = new NativeRun();
					blastpRun(nrun, sb.getBuffer(), dbPath, resPath, "-evalue 0.00001", null, true, geneset.zipfilesystem, geneset.user, primaryStage);*/
            } else {
                var allSeqList = geneSet.table.getSelectionModel().getSelectedItems().stream().<String>mapMulti((gg, c) -> {
                    var tvs = gg.genes;
                    var first0 = tvs.stream().filter(p -> p.getId() != null /*&& p.getId().startsWith("QAY")*/).findFirst();
                    if (first0.isPresent()) {
                        var first = first0.get();
                        var sw = new StringWriter();
                        sw.append(first.getId());
                        sw.append('\n');
                        try {
                            if (tvs.size() > 1) {
                                for (var tv : tvs) {
                                    tv.getAlignedSequence().writeIdSequence(sw);
                                }
                            } else {
                                first.getProteinSequence().writeIdSequence(sw);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        c.accept(sw.toString());
                    }
                }).toList();
                //var allds = spark.createDataset(allSeqList, Encoders.STRING());
                var res = allSeqList.stream().map(new SparkHHBlits(root, db)).toList();
                var mapping = Path.of(root).resolve("mapping2.txt");
                Files.writeString(mapping, String.join("\n", res));
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
