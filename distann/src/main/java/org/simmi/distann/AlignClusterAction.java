package org.simmi.distann;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.simmi.javafasta.shared.GeneGroup;
import scala.Tuple2;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class AlignClusterAction implements EventHandler<ActionEvent> {
    GeneSetHead genesethead;

    public AlignClusterAction(GeneSetHead genesethead) {
        this.genesethead = genesethead;
    }

    @Override
    public void handle(ActionEvent event) {
        TableView<GeneGroup> table = genesethead.table;
        Path zippath = genesethead.geneset.zippath;
        Collection<GeneGroup> allgenegroups = genesethead.geneset.allgenegroups;

        Collection<GeneGroup> ggset;
        ObservableList<GeneGroup> ogg = table.getSelectionModel().getSelectedItems();
        ggset = new HashSet<>();
        if( ogg.size() == 0 ) {
            for( GeneGroup gg : allgenegroups ) {
                //GeneGroup gg = allgenegroups.get(table.convertRowIndexToModel(r));
                //gg.getCommonTag()
                if( gg != null && gg.size() > 1 && (gg.getCommonTag() == null || gg.getCommonTag().equals("gene")) ) ggset.add( gg );
            }
        } else {
            for( GeneGroup gg : ogg ) {
                //GeneGroup gg = geneset.allgenegroups.get(table.convertRowIndexToModel(r));
                //gg.getCommonTag()
                if( gg != null && gg.getCommonTag() == null && gg.size() > 1 ) ggset.add( gg );
            }
        }

        SparkSession spark = SparkSession.builder().master("local[*]")
                /*.master("k8s://https://6A0DA5D06C34D9215711B1276624FFD9.gr7.us-east-1.eks.amazonaws.com")
            .config("spark.executor.memory","4g")
            .config("spark.executor.cores",2)
            .config("spark.executor.instances",10)
            .config("spark.kubernetes.namespace","spark")
            .config("spark.kubernetes.container.image","nextcode/glow:latest")
            .config("spark.kubernetes.executor.container.image","nextcode/glow:latest")
            .config("spark.kubernetes.container.image.pullSecrets", "dockerhub-nextcode-download-credentials")
            .config("spark.kubernetes.container.image.pullPolicy", "Always")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.options.claimName", "pvc-sparkgor-nfs")
            .config("spark.kubernetes.executor.volumes.persistentVolumeClaim.mntcsa.mount.path", "/mnt/csa")*/
                .getOrCreate();

        String userhome = System.getProperty("user.home");
        Path userhomePath = Paths.get(userhome);
        String root = userhomePath.resolve("tmp").toString(); //"/Users/sigmar/tmp";//"/mnt/csa/tmp";
        List<Tuple2<String,String>> fastaList = ggset.stream().filter(gg -> gg.getGroupCount() >= 2).map(gg -> {
            try {
                return new Tuple2<>(gg.getCommonId(), gg.getFasta(true));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        Encoder<Tuple2<String,String>> tupEncoder = Encoders.tuple(Encoders.STRING(), Encoders.STRING());
        Dataset<Tuple2<String,String>> dss = spark.createDataset(fastaList, tupEncoder);
        Dataset<Tuple2<String,String>> repart = dss.map(new SparkMafft(root), tupEncoder);
        Map<String,String> resmap = repart.javaRDD().mapToPair((PairFunction<Tuple2<String, String>, String, String>) stringStringTuple2 -> stringStringTuple2).collectAsMap();

        Map<String,String> env = new HashMap<>();
        env.put("create", "true");
        String uristr = "jar:" + zippath.toUri();
        URI zipuri = URI.create( uristr );
        //s.makeBlastCluster(zipfilesystem.getPath("/"), p, 1);

        try(FileSystem zipfilesystem = FileSystems.newFileSystem( zipuri, env )) {
            Path aldir = zipfilesystem.getPath("aligned");
            final Path aligneddir = Files.exists( aldir ) ? aldir : Files.createDirectory( aldir );

            for(String pstr : resmap.keySet()) {
                String fasta = resmap.get(pstr);
                Path resPath = aligneddir.resolve(pstr);
                //Iterable<String> it = rds::toLocalIterator;
                Files.writeString(resPath, fasta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //int i = 0;
            /*List commandsList = new ArrayList();
            for( GeneGroup gg : ggset ) {
                String fasta = gg.getFasta( true );
                String[] cmds = new String[] { OS.indexOf("mac") >= 0 ? "/usr/local/bin/mafft" : "/usr/bin/mafft", "-"};
                Object[] paths = new Object[] {fasta.getBytes(), aligneddir.resolve(gg.getCommonId()+".aa"), null};
                commandsList.add( paths );
                commandsList.add( Arrays.asList(cmds) );

                //if( i++ > 5000 ) break;
            }
            SwingUtilities.invokeLater(() -> {
                try {
                    nrun.runProcessBuilder("Running mafft", commandsList, cont, true, run, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });*/

    }
}
