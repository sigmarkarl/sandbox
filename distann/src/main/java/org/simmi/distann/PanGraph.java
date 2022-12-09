package org.simmi.distann;

import org.simmi.javafasta.shared.GeneGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PanGraph {
    Set<String> specs;
    List<GeneGroup> geneGroupList = new ArrayList<>();

    public PanGraph(Set<String> specList, List<GeneGroup> ggList) {
        this.specs = specList;
        for (GeneGroup gg : ggList) {
            var ospec = gg.getSpecies().stream().filter(specList::contains).findAny();
            if (ospec.isPresent()) {
                geneGroupList.add(gg);
            }
        }
    }

    public void exportSif(Path sif) throws IOException {
        try (var e = Files.newBufferedWriter(sif)) {
            for (GeneGroup gg : geneGroupList) {
                var idCount = new HashMap<String,Integer>();
                for (String spec : specs) {
                    if (gg.species.containsKey(spec)) {
                        var ti = gg.species.get(spec);
                        for (var a : ti.tset) {
                            var n = a.getNext();
                            if (n!=null && n.getContig() == a.getContig()) {
                                var ngg = n.getGeneGroup();
                                var id = ngg.getCommonId()+"_"+ngg.getName().replace(' ','_');
                                idCount.merge(id, 1, (k,v) -> v+1);
                            }
                            var p = a.getPrevious();
                            if (p!=null && p.getContig() == a.getContig()) {
                                var pgg = p.getGeneGroup();
                                var id = pgg.getCommonId()+"_"+pgg.getName().replace(' ', '_');
                                idCount.merge(id, 1, (k,v) -> v+1);
                            }
                        }
                    }
                }
                e.write(gg.getCommonId()+"_"+gg.getName().replace(' ','_'));
                e.write(" ");
                e.write("1");
                for (String key : idCount.keySet()) {
                    e.write(" ");
                    e.write(key);
                }
                e.write("\n");
            }
        }
    }

    public void export(Path vertices, Path edges) throws IOException {
        try (var f = Files.newBufferedWriter(vertices); var e = Files.newBufferedWriter(edges)) {
            for (GeneGroup gg : geneGroupList) {
                f.write(gg.getCommonId());
                f.write(",");
                f.write(gg.getName());
                f.write(",");
                var retainSet = new HashSet<>(specs);
                retainSet.retainAll(gg.getSpecies());
                f.write(Integer.toString(retainSet.size()));
                f.write(",");
                if (gg.getSpecies().containsAll(specs)) {
                    f.write("#aa0000");
                } else {
                    f.write("#00aa00");
                }
                f.write("\n");
            }

            for (GeneGroup gg : geneGroupList) {
                var idCount = new HashMap<String,Integer>();
                for (String spec : specs) {
                    if (gg.species.containsKey(spec)) {
                        var ti = gg.species.get(spec);
                        for (var a : ti.tset) {
                            var n = a.getNext();
                            if (n!=null) {
                                var ngg = n.getGeneGroup();
                                var id = ngg.getCommonId();
                                if (idCount.containsKey(id)) {
                                    idCount.put(id, idCount.get(id)+1);
                                } else {
                                    idCount.put(id, 1);
                                }
                            }
                            var p = a.getPrevious();
                            if (p!=null) {
                                var pgg = p.getGeneGroup();
                                var id = pgg.getCommonId();
                                if (idCount.containsKey(id)) {
                                    idCount.put(id, idCount.get(id)+1);
                                } else {
                                    idCount.put(id, 1);
                                }
                            }
                        }
                    }
                }
                for (String key : idCount.keySet()) {
                    int count = idCount.get(key);
                    e.write(gg.getCommonId());
                    e.write(",");
                    e.write(key);
                    e.write(",");
                    e.write(Integer.toString(count));
                    e.write("\n");
                }
            }
        }
    }
}
