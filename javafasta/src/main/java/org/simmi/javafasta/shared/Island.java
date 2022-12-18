package org.simmi.javafasta.shared;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Island extends Cassette {
    public String tag;

    public Island() {

    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /*public static void connectGeneGroups2(GeneGroup gg, Set<GeneGroup> frontSet, Set<GeneGroup> backSet) {
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
    }*/

    public static List<Cassette>	islands = new ArrayList<>();

    public static boolean frontBackContains(Cassette gg, Cassette ngg) {
        return checkSet(gg, ngg.front) || checkSet(gg, ngg.back);
    }

    public static boolean checkSet(Cassette gg, Set<Cassette> set) {
        return set != null && set.size() == 1 && set.contains(gg);
    }

    public static boolean frontBackOtherContains(GeneGroup gg, GeneGroup ngg, GeneGroup othergg) {
        return (ngg.front != null && ngg.front.size() == 2 && ngg.front.contains(gg) && ngg.front.contains(othergg))
                || (ngg.back != null && ngg.back.size() == 2 && ngg.back.contains(gg) && ngg.back.contains(othergg));
    }

    public static boolean setSizeCheckIsland(Set<Island> islset) {
        return islset != null && islset.size() <= 3;
    }

    public static boolean setSizeCheck(Set<Cassette> ggset) {
        return ggset != null && ggset.size() <= 3;
    }

    /*var island = mergeCheck(cassette, cassette.front, cassette.back, done);

                    if (island != null) {
                        newislands.add(island);
                        done.add(island);
                    } else {
                        /*if (cassette instanceof GeneGroup gg) {
                            if (gg.island != gg && gg.island != null) {
                                System.err.println();
                            }
                        }
                        var t = cassette.front.stream().filter(gg -> gg instanceof GeneGroup).map(gg -> (GeneGroup) gg).filter(gg -> gg.island != gg && gg.island != null).findAny();
                        if (t.isPresent()) {
                            System.err.println();
                        }*
                        newislands.addAll(cassette.front);
                        newislands.add(cassette);
                    }
                } else {
                    cassette.front.stream().filter(p -> !done.contains(p)).forEach(newislands::add);
                }*/

    public static boolean log(Set<Cassette> islands, Set<Cassette> newislands) {
        int nsum = newislands.stream().mapToInt(Cassette::getSize).sum();
        System.err.println("newislands sum of sizes: " + nsum);

        int nsums = newislands.stream().flatMap(cassette -> cassette.getGeneGroups().stream()).collect(Collectors.toSet()).size();
        System.err.println("newislands total genegroups: " + nsums);

        int sum = islands.stream().mapToInt(Cassette::getSize).sum();
        System.err.println(sum);

        int sums = islands.stream().flatMap(cassette -> cassette.getGeneGroups().stream()).collect(Collectors.toSet()).size();
        System.err.println(sums);

        //var newislands = connect(islands);
        long sum0 = islands.stream().filter(g -> g instanceof GeneGroup).count();
        System.err.println(sum0);
        long sum1 = islands.stream().filter(g -> g instanceof Island).count();
        System.err.println(sum1);
        long suma = islands.stream().filter(g -> g instanceof Island).mapToLong(Cassette::getSize).sum();
        System.err.println(suma);

        var set2 = islands.stream().filter(g -> g instanceof GeneGroup).flatMap(g -> g.getGeneGroups().stream()).collect(Collectors.toSet());
        System.err.println(set2.size());
        var set3 = islands.stream().filter(g -> g instanceof Island).flatMap(g -> g.getGeneGroups().stream()).collect(Collectors.toSet());
        System.err.println(set3.size());
        int sum4 = islands.stream().mapToInt(Cassette::getSize).sum();
        System.err.println(sum4);
        System.err.println();

        return nsum > nsums;
    }

    public static void connectCassettes(List<? extends Cassette> allgenegroups) {
        Set<Cassette> testislands = new HashSet<>(allgenegroups);
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.front.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.front, cassette.back, done);
                    if (island != null) {
                        island.setTag("frontsimple "+i);
                        newislands.add(island);
                    }
                }
            }

            //var ss = newislands.stream().map()
            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            //islands.stream().filter(s -> !prev.contains(s)).forEach(newislands::add);
            //testislands = newislands;
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands.addAll(newislands);
            System.err.println("front");
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.back.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.back, cassette.front, done);
                    if (island != null) {
                        island.setTag("backsimple "+i);
                        newislands.add(island);
                    }
                }
            }

            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            //islands.stream().filter(s -> !prev.contains(s)).forEach(newislands::add);
            var prevIslands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands = new HashSet<>(prevIslands);
            testislands.addAll(newislands);
            System.err.println("back");
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);

                if (!done.contains(cassette) && cassette.front.stream().noneMatch(done::contains) && cassette.front.stream().flatMap(c -> Stream.concat(c.front.stream(), c.back.stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.front, cassette.back, done, done, testislands);
                    if (island != null) {
                        island.setTag("frontcomplex "+i);
                        newislands.add(island);
                    }
                }
                /*if (ggsinnewislands.size() != ggsindoneislands.size()) {
                    cassette = islands.get(i);
                    if (!prevdone.contains(cassette) && cassette.front.stream().noneMatch(prevdone::contains) && cassette.front.stream().flatMap(c -> Stream.concat(c.front.stream(), c.back.stream())).noneMatch(prevdone::contains)) {
                        var island = mergeMotifCheck(cassette, cassette.front, cassette.back, prevdone, prevnewdone, testislands);
                        if (island != null) {
                            System.err.println();
                        }
                    }
                }*/
            }

            var ggsinnewislands = newislands.stream().flatMap(i -> i.getGeneGroups().stream()).collect(Collectors.toSet());
            testislands = islands.stream().filter(s -> s.getGeneGroups().stream().noneMatch(ggsinnewislands::contains)).collect(Collectors.toSet());
            testislands.addAll(newislands);

            /*var la = islands.stream().filter(s -> !done.contains()).filter(s -> s.geneGroups.stream().anyMatch(ggsinnewislands::contains)).findFirst();
            if (la.isPresent()) {
                var laa = la.get();
                var ia = newislands.stream().filter(i -> i.geneGroups.stream().anyMatch(g -> laa.geneGroups.contains(g))).findFirst();
                if (ia.isPresent()) {
                    System.err.println();
                }
            }*/

            //testislands = islands.stream().filter(s -> s.getGeneGroups().stream().noneMatch(ggsinnewislands::contains)).collect(Collectors.toSet());
            //testislands.addAll(newislands);
            System.err.println("logging motif front");
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.back.stream().noneMatch(done::contains) && cassette.back.stream().flatMap(c -> Stream.concat(c.front.stream(), c.back.stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.back, cassette.front, done, done, testislands);
                    if (island != null) {
                        island.setTag("backcomplex "+i);
                        newislands.add(island);
                    }
                }
            }
            var ggsinnewislands = newislands.stream().flatMap(i -> i.getGeneGroups().stream()).collect(Collectors.toSet());

            var la = islands.stream().filter(s -> s.getGeneGroups().stream().anyMatch(ggsinnewislands::contains)).findFirst();
            if (la.isPresent()) {
                var laa = la.get();
                var ia = newislands.stream().filter(i -> i.getGeneGroups().stream().anyMatch(g -> laa.getGeneGroups().contains(g))).findFirst();
                if (ia.isPresent()) {
                    System.err.println();
                }
            }

            testislands = islands.stream().filter(s -> s.getGeneGroups().stream().noneMatch(ggsinnewislands::contains)).collect(Collectors.toSet());
            testislands.addAll(newislands);
            System.err.println("logging motif back");
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.front.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.front, cassette.back, done);
                    if (island != null) {
                        newislands.add(island);
                    }
                }
            }
            //var ss = newislands.stream().map()
            islands.stream().filter(s -> !done.contains(s)).forEach(newislands::add);
            testislands = newislands;
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.back.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.back, cassette.front, done);
                    if (island != null) {
                        newislands.add(island);
                    }
                }
            }
            islands.stream().filter(s -> !done.contains(s)).forEach(newislands::add);
            testislands = newislands;
            log(testislands, newislands);
        } while(testislands.size() < islands.size());

        /*do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.front.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.front, cassette.back, done);
                    if (island != null) {
                        newislands.add(island);
                    }
                }
            }
            //var ss = newislands.stream().map()
            islands.stream().filter(s -> !done.contains(s)).forEach(newislands::add);
            testislands = newislands;
            log(testislands);
        } while(testislands.size() < islands.size());

        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.back.stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.back, cassette.front, done);
                    if (island != null) {
                        newislands.add(island);
                    }
                }
            }
            islands.stream().filter(s -> !done.contains(s)).forEach(newislands::add);
            testislands = newislands;
            log(testislands);
        } while(testislands.size() < islands.size());
        //islands = new ArrayList<>(newislands);
        /*while (newislands.size() < islands.size()) {
            islands = newislands;
            newislands = connect(islands);
        }*/
    }

    public static List<Cassette> connect(List<? extends Cassette> islands) {
        var newislands = new HashSet<Cassette>();
        /*for (var gg : islands) {
            if (!mergeCheck(gg, gg.front, gg.back, newislands)) {
                if (!mergeCheck(gg, gg.back, gg.front, newislands)) {
                    newislands.add(gg);
                }
            }
        }*/
        return new ArrayList<>(newislands);
    }

    /*public static void replaceIslandFrontBack(Set<Cassette> other, Set<Cassette> frontBack, Cassette gg, Cassette island) {
        other.stream().map(f).filter(cc -> cc.remove(gg)).forEach(c -> c.add(island));
    }*/

    public static void replaceIsland(Set<Cassette> others, Cassette gg, Cassette island) {
        for (var cas : others) {
            var front = cas.getFront();
            if (front != others) {
                if (front.remove(gg)) {
                    front.add(island);
                }
            }
        }
        for (var cas : others) {
            var back = cas.getBack();
            if (back != others) {
                if (back.remove(gg)) {
                    back.add(island);
                }
            }
        }
        /*other.stream().map(Cassette::getFront).filter(cc -> cc.remove(gg)).forEach(c -> c.add(island));
        other.stream().map(Cassette::getBack).filter(cc -> cc.remove(gg)).forEach(c -> c.add(island));*/
    }

    public static Island mergeMotifCheck(Cassette gg, Set<Cassette> frontBack, Set<Cassette> other, Set<Cassette> done, Set<Cassette> newdone, Set<Cassette> testislands) {
        var ggsets = frontBack.stream().map(ngg -> ngg.front.contains(gg) ? ngg.back : ngg.front).toList();
        var ggmap = new HashMap<Set<Cassette>,Integer>();
        for (var ggset : ggsets) {
            ggmap.merge(ggset, 1, Integer::sum);
        }

        if (ggmap.size() == 1) {
            for (var ggset : ggmap.keySet()) {
                if (ggset.size() == 1) {
                    var tgg = ggset.iterator().next();
                    var frontCheck = new HashSet<>(tgg.front);
                    frontCheck.removeAll(frontBack);
                    var backCheck = new HashSet<>(tgg.back);
                    backCheck.removeAll(frontBack);
                    if (frontCheck.size() == 0 || backCheck.size() == 0) {
                        var islandStream = Stream.concat(Stream.of(gg), frontBack.stream());
                        islandStream = Stream.concat(ggset.stream(), islandStream);
                        var islandSet = islandStream.collect(Collectors.toSet());

                    /*Cassette island;
                    if (islandSet.size() == 0) {
                        island = new Island(gg);
                        islands.add(island);
                    } else if (islandSet.size() == 1) {
                        island = islandSet.stream().findFirst().get();
                        island.add(gg);
                    } else {*/
                        var island = new Island();

                        /*island.add(gg);
                        island.add(tgg);
                        frontBack.forEach(island::add);*/

                        var bother = frontCheck.size() == 0 ? tgg.back : tgg.front;
                        replaceIsland(other, gg, island);
                        replaceIsland(bother, tgg, island);

                        other.removeAll(frontBack);
                        bother.removeAll(frontBack);

                        other.remove(gg);
                        bother.remove(tgg);

                        bother.removeAll(other);

                        if (!island.front.equals(other)) island.front = other;
                        if (!island.back.equals(bother)) island.back = bother;
                        //islands.add(island);
                        //islands.removeAll(islandSet);
                        if (islandSet.stream().anyMatch(i -> !testislands.contains(i))) {
                            System.err.println();
                        }
                        done.addAll(islandSet);
                        newdone.add(island);
                        island.addAll(islandSet);
                        return island;
                    }
                    //}
                    //island.add(frontBack.stream().flatMap(s -> s.geneGroups.stream()).toList());
                    //island.add(ggset.stream().flatMap(s -> s.geneGroups.stream()).toList());
                }
            }
        } /*else if (ggmap.size() == 2) {
            var ggsetlist = ggmap.entrySet().stream().toList();
            var ggset1 = ggsetlist.get(0);
            var ggset2 = ggsetlist.get(1);
            var b1 = ggset1.getKey().size() == 1 && frontBack.containsAll(ggset1.getKey()) && ggset2.getValue() == 1;
            var b2 = ggset2.getKey().size() == 1 && frontBack.containsAll(ggset2.getKey()) && ggset1.getValue() == 1;
            if (b1 || b2) {
                var islandSet = Stream.concat(Stream.of(gg), frontBack.stream().map(ngg -> ngg))
                        .filter(Objects::nonNull).collect(Collectors.toSet());
                Island island;
                if (islandSet.size() == 0) {
                    island = new Island(gg);
                    islands.add(island);
                } else if (islandSet.size() == 1) {
                    island = islandSet.stream().findFirst().get();
                    island.add(gg);
                } else {
                    island = new Island(gg);
                    islands.add(island);
                    islands.removeAll(islandSet);
                    islandSet.stream().flatMap(isl -> isl.geneGroups.stream()).forEach(island::add);
                }
                island.addAll(frontBack.stream().flatMap(g -> g.geneGroups.stream()));
            }
        }*/
        return null;
    }

    public static Island mergeCheck(Cassette gg, Set<Cassette> frontBack, Set<Cassette> other, Set<Cassette> done) {
        if (setSizeCheck(frontBack)) {
            if (frontBack.size() == 1) {
                for (var ngg : frontBack) {
                    if (gg != ngg) {
                        if (checkSet(gg, ngg.front)) {
                            var bother = ngg.back;
                            var island = new Island();

                            island.add(gg);
                            island.add(ngg);

                            replaceIsland(other, gg, island);
                            replaceIsland(bother, ngg, island);

                            done.add(gg);
                            done.add(ngg);
                            done.add(island);

                            if (bother.containsAll(other)) {
                                if (!island.front.equals(other)) island.front = other;
                            } else {
                                if (!island.front.equals(other)) island.front = other;
                                if (!island.back.equals(bother)) island.back = bother;
                            }
                            return island;
                        } else if (checkSet(gg, ngg.back)) {
                            var bother = ngg.front;
                            var island = new Island();

                            island.add(gg);
                            island.add(ngg);

                            replaceIsland(other, gg, island);
                            replaceIsland(bother, ngg, island);

                            done.add(gg);
                            done.add(ngg);
                            done.add(island);

                            if (bother.containsAll(other)) {
                                if (!island.front.equals(other)) island.front = other;
                            } else {
                                if (!island.front.equals(other)) island.front = other;
                                if (!island.back.equals(bother)) island.back = bother;
                            }
                            return island;
                        }
                    }/* else if (gg.island != null && ngg.island != null && gg.island != ngg.island) {
                        if (gg.island.front == null) {
                            if (gg.front.contains(ngg)) gg.island.front = gg.front.stream().map(g -> g.island).collect(Collectors.toSet());
                            else if (gg.back.contains(ngg)) gg.island.front = gg.back.stream().map(g -> g.island).collect(Collectors.toSet());
                        } else if (gg.island.back == null) {
                            if (gg.front.contains(ngg)) gg.island.back = gg.front.stream().map(g -> g.island).collect(Collectors.toSet());
                            else if (gg.back.contains(ngg)) gg.island.back = gg.back.stream().map(g -> g.island).collect(Collectors.toSet());
                        }
                    }*/
                }
            } /*else if (frontBack.size() > 1) {
                var ggsets = frontBack.stream().map(ngg -> ngg.front.contains(gg) ? ngg.back : ngg.front).toList();
                var ggmap = new HashMap<Set<GeneGroup>,Integer>();
                for (var ggset : ggsets) {
                    ggmap.merge(ggset, 1, Integer::sum);
                }

                if (ggmap.size() == 1) {
                    for (var ggset : ggmap.keySet()) {
                        if (ggset.size() == 1) {
                            var islandStream = Stream.concat(Stream.of(gg), frontBack.stream());
                            var tgg = ggset.iterator().next();
                            var frontCheck = new HashSet<>(tgg.front);
                            frontCheck.removeAll(frontBack);
                            var backCheck = new HashSet<>(tgg.back);
                            backCheck.removeAll(frontBack);
                            if (frontCheck.size() == 0 || backCheck.size() == 0) islandStream = Stream.concat(ggset.stream().map(ngg -> ngg.island), islandStream);
                            var islandSet = islandStream.filter(Objects::nonNull).collect(Collectors.toSet());
                            Cassette island;
                            if (islandSet.size() == 0) {
                                island = new Island(gg);
                                islands.add(island);
                            } else if (islandSet.size() == 1) {
                                island = islandSet.stream().findFirst().get();
                                island.add(gg);
                            } else {
                                island = new Island(gg);
                                islands.add(island);
                                islands.removeAll(islandSet);
                                islandSet.stream().flatMap(isl -> isl.geneGroups.stream()).forEach(island::add);
                            }
                            island.addAll(frontBack.stream().flatMap(s -> s.geneGroups.stream()));
                            island.add(ggset.iterator().next());
                        }
                    }
                } else if (ggmap.size() == 2) {
                    var ggsetlist = ggmap.entrySet().stream().toList();
                    var ggset1 = ggsetlist.get(0);
                    var ggset2 = ggsetlist.get(1);
                    var b1 = ggset1.getKey().size() == 1 && frontBack.containsAll(ggset1.getKey()) && ggset2.getValue() == 1;
                    var b2 = ggset2.getKey().size() == 1 && frontBack.containsAll(ggset2.getKey()) && ggset1.getValue() == 1;
                    if (b1 || b2) {
                        var islandSet = Stream.concat(Stream.of(gg), frontBack.stream().map(ngg -> ngg))
                                .filter(Objects::nonNull).collect(Collectors.toSet());
                        Island island;
                        if (islandSet.size() == 0) {
                            island = new Island(gg);
                            islands.add(island);
                        } else if (islandSet.size() == 1) {
                            island = islandSet.stream().findFirst().get();
                            island.add(gg);
                        } else {
                            island = new Island(gg);
                            islands.add(island);
                            islands.removeAll(islandSet);
                            islandSet.stream().flatMap(isl -> isl.geneGroups.stream()).forEach(island::add);
                        }
                        island.addAll(frontBack.stream().flatMap(g -> g.geneGroups.stream()));
                    }
                }*/

                /*var gg1 = it.next();
                var gg2 = it.next();
                var b1 = frontBackContains(gg, gg1) && frontBackOtherContains(gg, gg2, gg1);
                var b2 = frontBackContains(gg, gg2) && frontBackOtherContains(gg, gg1, gg2);
                if (b1 || b2) {
                    if (b1) gg1.triangle = true;
                    else gg2.triangle = true;

                    if (gg1.island == null) {
                        if (gg2.island == null) {
                            var island = getIsland(gg);
                            island.add(gg1);
                            island.add(gg2);
                        } else {
                            var island = getIslandOrMerge(gg, gg2);
                            island.add(gg1);
                        }
                    } else if (gg2.island == null) {
                        var island = getIslandOrMerge(gg, gg1);
                        island.add(gg2);
                    } else if (gg.island == null) {
                        mergeIslands(gg1, gg2).add(gg);
                    } else {
                        mergeIslands(gg1, gg2);
                        mergeIslands(gg, gg1);
                    }
                }*/
            //}
        }
        return null;
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

            var frontSet = new HashSet<Cassette>();
            var backSet = new HashSet<Cassette>();

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

            frontSet.remove(gg);
            backSet.remove(gg);

            if (backSet.removeAll(frontSet)) {
                System.err.println();
            }

            //gg.geneGroups.add(gg);
            gg.front = frontSet;
            gg.back = backSet;
            //connectGeneGroups(gg, frontSet, backSet);
        }
    }

    public Island(Cassette gg) {
        add(gg);
    }

    public int size() {
        return getSize();
    }

    public String toString() {
        return "";
    }
}
