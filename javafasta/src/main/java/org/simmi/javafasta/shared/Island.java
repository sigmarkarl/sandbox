package org.simmi.javafasta.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Island {
    public java.util.Set<GeneGroup> geneGroups = new java.util.HashSet<>();

    public Island() {

    }

    public static void connectGeneGroups2(GeneGroup gg, Set<GeneGroup> frontSet, Set<GeneGroup> backSet) {
        if (frontSet.size() == 1 && backSet.size() == 1) {
            var fgg = frontSet.iterator().next();
            var bgg = backSet.iterator().next();

            if (fgg.island == null && bgg.island == null) {
                var island = new Island(gg);
                islands.add(island);
                island.add(fgg);
                island.add(bgg);
            } else if (fgg.island == null) {
                bgg.island.add(fgg);
                bgg.island.add(gg);
            } else if (bgg.island == null) {
                fgg.island.add(bgg);
                fgg.island.add(gg);
            } else {
                var island = new Island(gg);
                islands.add(island);
						/*islands.remove(fgg.island);
						islands.remove(bgg.island);
						fgg.island.geneGroups.forEach(island::add);
						bgg.island.geneGroups.forEach(island::add);*/
            }
        } else if (frontSet.size() == 1) {
            var fgg = frontSet.iterator().next();
            if (fgg.island == null) {
                var island = new Island(gg);
                islands.add(island);
                island.add(fgg);
            } else {
                fgg.island.add(gg);
            }
        } else if (backSet.size() == 1) {
            var bgg = backSet.iterator().next();
            if (bgg.island == null) {
                var island = new Island(gg);
                islands.add(island);
                island.add(bgg);
            } else {
                bgg.island.add(gg);
            }
        } else {
            var island = new Island(gg);
            islands.add(island);
        }
    }

    public static List<Island>	islands = new ArrayList<>();

    public static void connectGeneGroups(List<GeneGroup> allgenegroups) {
        for (var gg : allgenegroups) {
            if (gg.island == null) {
                if (gg.front != null) {
                    if (gg.front.front == gg || gg.front.back == gg) {
                        if (gg.front.island == null) {
                            var island = new Island(gg);
                            islands.add(island);
                            island.add(gg.front);
                        } else gg.front.island.add(gg);
                    }
                }
                if (gg.back != null) {
                    if (gg.back.front == gg || gg.back.back == gg) {
                        if (gg.back.island == null) {
                            Island island;
                            if (gg.island != null) {
                                island = gg.island;
                            } else {
                                island = new Island(gg);
                                islands.add(island);
                            }
                            island.add(gg.back);
                        } else if (gg.island == null) {
                            gg.back.island.add(gg);
                        } else {
                            islands.remove(gg.island);
                            islands.remove(gg.back.island);
                            var island = new Island();
                            islands.add(island);
                            gg.island.geneGroups.forEach(island::add);
                            gg.back.island.geneGroups.forEach(island::add);
                        }
                    }
                }
                if (gg.island==null) {
                    var island = new Island(gg);
                    islands.add(island);
                    island.add(gg);
                }
            }
        }
    }

    public static void initIslands(List<GeneGroup> allgenegroups) {
        for (var gg : allgenegroups) {
            var ggsetcount = new HashSet<Set<GeneGroup>>();
            for (Annotation a : gg.genes) {
                var next = a.getNext();
                var prev = a.getPrevious();

                var ggset = new HashSet<GeneGroup>();
                if (next!=null && next.getGeneGroup()!=null) {
                    ggset.add(next.getGeneGroup());
                }
                if (prev!=null && prev.getGeneGroup()!=null) {
                    ggset.add(prev.getGeneGroup());
                }
                ggsetcount.add(ggset);
            }

            var frontSet = new HashSet<GeneGroup>();
            var backSet = new HashSet<GeneGroup>();

            for (Set<GeneGroup> set : ggsetcount.stream().filter(s -> s.size() == 2).toList()) {
                var it = set.iterator();
                var tgg = it.next();
                var ngg = it.next();

                if (frontSet.contains(tgg)) backSet.add(ngg);
                else if (backSet.contains(tgg)) frontSet.add(ngg);
                else if (frontSet.contains(ngg)) backSet.add(tgg);
                else if (backSet.contains(ngg)) frontSet.add(tgg);
                else {
                    frontSet.add(tgg);
                    backSet.add(ngg);
                }
            }

            for (Set<GeneGroup> set : ggsetcount.stream().filter(s -> s.size() == 1).toList()) {
                if (!frontSet.containsAll(set) && !backSet.containsAll(set)) {
                    frontSet.addAll(set);
                    backSet.addAll(set);
                }
            }

            if (frontSet.size() == 1) gg.front = frontSet.iterator().next();
            if (backSet.size() == 1) gg.back = backSet.iterator().next();
            //connectGeneGroups(gg, frontSet, backSet);
        }

        connectGeneGroups(allgenegroups);
    }

    public Islinfo getInfo(String spec) {
        var pres = geneGroups.stream().filter(gg -> gg.getSpecies().contains(spec)).findAny();
        return pres.isPresent() ? new Islinfo(spec) : new Islinfo("");
    }

    public Island(GeneGroup gg) {
        add(gg);
    }

    public void add(GeneGroup gg) {
        gg.island = this;
        geneGroups.add(gg);
    }

    public String getName() {
        return geneGroups.stream().map(GeneGroup::getName).filter(p -> !p.startsWith("hypot")).findFirst().orElse("hypothetical protein");
    }

    public int getSize() {
        return geneGroups.size();
    }

    public int size() {
        return geneGroups.size();
    }

    public String toString() {
        return "";
    }
}
