package org.simmi.distann;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.lib.ConnectedComponents;
import org.apache.spark.graphx.lib.LabelPropagation;
import org.apache.spark.graphx.lib.PageRank;
import org.apache.spark.sql.Encoders;
import org.apache.spark.storage.StorageLevel;
import org.simmi.javafasta.shared.BaseGeneGroup;
import org.simmi.javafasta.shared.GeneGroup;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import java.nio.file.*;
import java.util.*;

public class LabelPropagationAction implements EventHandler<ActionEvent> {
    GeneSetHead genesethead;

    public LabelPropagationAction(GeneSetHead genesethead) {
        this.genesethead = genesethead;
    }

    private static final ClassTag<String> tagString =
            ClassTag$.MODULE$.apply(String.class);
    private static final ClassTag<BaseGeneGroup> tagGeneGroup =
            ClassTag$.MODULE$.apply(BaseGeneGroup.class);


    @Override
    public void handle(ActionEvent event) {
        TableView<GeneGroup> table = genesethead.table;
        Path zippath = genesethead.geneset.zippath;
        List<GeneGroup> allgenegroups = genesethead.geneset.allgenegroups;
        var idMap = new HashMap<Long, GeneGroup>();
        for (GeneGroup gg : allgenegroups) {
            idMap.put(gg.getId(), gg);
        }

        var vertices = genesethead.geneset.sparkSession.createDataset(allgenegroups.stream().map(t -> Tuple2.apply(t.getId(),(BaseGeneGroup)t)).toList(), Encoders.tuple(Encoders.LONG(), Encoders.bean(BaseGeneGroup.class)));

        var edgelist = allgenegroups.stream().flatMap(gg -> {
            var idCount = new HashMap<Long,Integer>();
            for (String spec : gg.getSpecies()) {
                var ti = gg.species.get(spec);
                for (var a : ti.tset) {
                    var n = a.getNext();
                    if (n!=null) {
                        var ngg = n.getGeneGroup();
                        var id = ngg.getId();
                        if (idCount.containsKey(id)) {
                            idCount.put(id, idCount.get(id)+1);
                        } else {
                            idCount.put(id, 1);
                        }
                    }
                    var p = a.getPrevious();
                    if (p!=null) {
                        var pgg = p.getGeneGroup();
                        var id = pgg.getId();
                        if (idCount.containsKey(id)) {
                            idCount.put(id, idCount.get(id)+1);
                        } else {
                            idCount.put(id, 1);
                        }
                    }
                }
            }
            return idCount.entrySet().stream().map(e -> new JavaEdge(gg.getId(), e.getKey(), e.getValue()));
        }).toList();
        var edges = genesethead.geneset.sparkSession.createDataset(edgelist, Encoders.bean(JavaEdge.class));

        var emptyGeneGroup = new BaseGeneGroup();
        var vertexRdd = vertices.javaRDD().map(v -> Tuple2.apply((Object)v._1,v._2));
        var edgeRdd = edges.javaRDD().map(je -> Edge.apply(je.srcId, je.dstId, je.attr));
        var graph = Graph.apply(vertexRdd.rdd(), edgeRdd.rdd(),emptyGeneGroup, StorageLevel.MEMORY_ONLY(),
                StorageLevel.MEMORY_ONLY(), tagGeneGroup, tagString);

        //graph

        var labgraph = LabelPropagation.run(graph, 100, tagString);
        var labvert = labgraph.vertices().toJavaRDD().collect();
        for (var tup : labvert) {
            var gg = idMap.get(tup._1);
            gg.setLabel(tup._2.toString());
        }

        var pagegraph = PageRank.run(graph, 100, 0.15,  tagGeneGroup, tagString);
        var pagevert = pagegraph.vertices().toJavaRDD().collect();
        for (var tup : pagevert) {
            var gg = idMap.get(tup._1);
            gg.setPageRank((double)tup._2);
        }

        var congraph = ConnectedComponents.run(graph, tagGeneGroup, tagString);
        var connvert = congraph.vertices().toJavaRDD().collect();
        for (var tup : connvert) {
            var gg = idMap.get(tup._1);
            gg.setConnected(tup._2.toString());
        }
    }
}
