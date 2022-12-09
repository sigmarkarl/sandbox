package org.simmi.javafasta.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Island {
    public java.util.Set<GeneGroup> geneGroups = new java.util.HashSet<>();

    public Island() {

    }

    public boolean contains(GeneGroup gg) {
        return geneGroups.contains(gg);
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

    public static boolean frontBackContains(GeneGroup gg, GeneGroup ngg) {
        return (ngg.front != null && ngg.front.size() == 1 && ngg.front.contains(gg)) || ngg.back != null && ngg.back.size() == 1 && ngg.back.contains(gg);
    }

    public static boolean frontBackOtherContains(GeneGroup gg, GeneGroup ngg, GeneGroup othergg) {
        return (ngg.front != null && ngg.front.size() == 2 && ngg.front.contains(gg) && ngg.front.contains(othergg))
                || (ngg.back != null && ngg.back.size() == 2 && ngg.back.contains(gg) && ngg.back.contains(othergg));
    }

    public static boolean setSizeCheck(Set<GeneGroup> ggset) {
        return ggset != null && ggset.size() <= 2;
    }

    public static void connectGeneGroups(List<GeneGroup> allgenegroups) {
        for (var gg : allgenegroups) {
            mergeCheck(gg, gg.front);
            mergeCheck(gg, gg.back);
            if (gg.island==null) {
                var island = new Island(gg);
                islands.add(island);
                island.add(gg);
            }
        }
    }

    public static Island mergeIslands(GeneGroup gg, GeneGroup ngg) {
        if (gg.island != ngg.island) {
            islands.remove(gg.island);
            islands.remove(ngg.island);
            var island = new Island();
            islands.add(island);
            gg.island.geneGroups.forEach(island::add);
            ngg.island.geneGroups.forEach(island::add);
            return island;
        } else {
            return gg.island;
        }
    }

    public static void mergeCheck(GeneGroup gg, Set<GeneGroup> frontBack) {
        if (setSizeCheck(frontBack)) {
            if (frontBack.size() == 1) {
                for (var ngg : frontBack) {
                    if (frontBackContains(gg, ngg)) {
                        if (ngg.island == null) {
                            Island island;
                            if (gg.island != null) {
                                island = gg.island;
                            } else {
                                island = new Island(gg);
                                islands.add(island);
                            }
                            island.add(ngg);
                        } else if (gg.island == null) {
                            ngg.island.add(gg);
                        } else {
                            mergeIslands(gg, ngg);
                        }
                    }
                }
            } else if (frontBack.size() == 2) {
                var it = frontBack.iterator();
                var gg1 = it.next();
                var gg2 = it.next();
                var b1 = frontBackContains(gg, gg1) && frontBackOtherContains(gg, gg2, gg1);
                var b2 = frontBackContains(gg, gg2) && frontBackOtherContains(gg, gg1, gg2);
                if (b1 || b2) {
                    if (b1) gg1.triangle = true;
                    else if (b2) gg2.triangle = true;

                    if (gg1.island == null) {
                        Island island;
                        if (gg2.island == null) {
                            if (gg.island != null) {
                                island = gg.island;
                            } else {
                                island = new Island(gg);
                                islands.add(island);
                            }
                            island.add(gg1);
                            island.add(gg2);
                        } else {
                            if (gg.island != null) {
                                island = mergeIslands(gg, gg2);
                            } else {
                                island = gg2.island;
                            }
                            island.add(gg1);
                        }
                    } else if (gg2.island == null) {
                        Island island;
                        if (gg.island != null) {
                            island = mergeIslands(gg, gg1);
                        } else {
                            island = gg1.island;
                        }
                        island.add(gg2);
                    } else if (gg.island == null) {
                        mergeIslands(gg1, gg2).add(gg);
                    } else {
                        mergeIslands(gg1, gg2);
                        mergeIslands(gg, gg1);
                    }
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
                if (next!=null && next.getContig() == a.getContig() && next.getGeneGroup()!=null) {
                    ggset.add(next.getGeneGroup());
                }
                if (prev!=null && prev.getContig() == a.getContig() && prev.getGeneGroup()!=null) {
                    ggset.add(prev.getGeneGroup());
                }
                if (ggset.size() > 0) ggsetcount.add(ggset);
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

            gg.front = frontSet;
            gg.back = backSet;
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
