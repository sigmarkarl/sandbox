package org.simmi.distann;

import org.simmi.javafasta.shared.GeneGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

    public void exportSifEdgeNames(Path sif) throws IOException {
        try (var e = Files.newBufferedWriter(sif)) {
            for (GeneGroup gg : geneGroupList) {
                var idCount = new HashMap<String,String>();
                for (String spec : specs) {
                    if (gg.species.containsKey(spec)) {
                        var ti = gg.species.get(spec);
                        for (var a : ti.tset) {
                            var n = a.getNext();
                            if (n!=null && n.getContig() == a.getContig()) {
                                var ngg = n.getGeneGroup();
                                var id = ngg.getCommonId()+"_"+ngg.getName().replace(' ','_');
                                idCount.merge(id, spec, String::concat);
                            }
                            var p = a.getPrevious();
                            if (p!=null && p.getContig() == a.getContig()) {
                                var pgg = p.getGeneGroup();
                                var id = pgg.getCommonId()+"_"+pgg.getName().replace(' ', '_');
                                idCount.merge(id, spec, String::concat);
                            }
                        }
                    }
                }

                var countId = new HashMap<String,Set<String>>();
                idCount.forEach((key, value) -> countId.merge(value.replace(' ','_'), new HashSet<>(Collections.singletonList(key)), (p, c) -> {
                    p.addAll(c);
                    return p;
                }));

                for (var key : countId.keySet()) {
                    e.write(gg.getCommonId()+"_"+gg.getName().replace(' ','_'));
                    e.write(" ");
                    e.write(key);
                    e.write(" ");
                    e.write(String.join(" ", countId.get(key)));
                    e.write("\n");
                }
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
                                idCount.merge(id, 1, Integer::sum);
                            }
                            var p = a.getPrevious();
                            if (p!=null && p.getContig() == a.getContig()) {
                                var pgg = p.getGeneGroup();
                                var id = pgg.getCommonId()+"_"+pgg.getName().replace(' ', '_');
                                idCount.merge(id, 1, Integer::sum);
                            }
                        }
                    }
                }

                var countId = new HashMap<Integer,Set<String>>();
                idCount.forEach((key, value) -> countId.merge(value, new HashSet<>(Collections.singletonList(key)), (p, c) -> {
                    p.addAll(c);
                    return p;
                }));

                for (var key : countId.keySet()) {
                    e.write(gg.getCommonId()+"_"+gg.getName().replace(' ','_'));
                    e.write(" ");
                    e.write(Integer.toString(key));
                    e.write(" ");
                    e.write(String.join(" ", countId.get(key)));
                    e.write("\n");
                }
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
