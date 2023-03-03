package org.simmi.javafasta.shared;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Island extends Cassette {
    public Island() {

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
        return checkSet(gg, ngg.getFront()) || checkSet(gg, ngg.getBack());
    }

    public static boolean checkSet(Cassette gg, Set<Cassette> set) {
        return set != null && set.size() == 1 && set.contains(gg);
    }

    public static boolean frontBackOtherContains(GeneGroup gg, GeneGroup ngg, GeneGroup othergg) {
        return (ngg.getTopFront() != null && ngg.getTopFront().size() == 2 && ngg.getTopFront().contains(gg) && ngg.getTopFront().contains(othergg))
                || (ngg.getTopBack() != null && ngg.getTopBack().size() == 2 && ngg.getTopBack().contains(gg) && ngg.getTopBack().contains(othergg));
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
        var it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.getTopFront().stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.getTopFront(), cassette.getBack(), done);
                    if (island != null) {
                        island.setTag("frontsimple "+i+" "+it);
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

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
            it++;
        } while(testislands.size() < islands.size());

        it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.getTopBack().stream().noneMatch(done::contains)) {
                    var island = mergeCheck(cassette, cassette.getTopBack(), cassette.getFront(), done);
                    if (island != null) {
                        island.setTag("backsimple "+i+" "+it);
                        newislands.add(island);
                    }
                }
            }

            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            //islands.stream().filter(s -> !prev.contains(s)).forEach(newislands::add);
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands.addAll(newislands);
            System.err.println("back");
            log(testislands, newislands);

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
            it++;
        } while(testislands.size() < islands.size());

        it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);

                if (!done.contains(cassette) && cassette.getTopFront().stream().noneMatch(done::contains) && cassette.getTopFront().stream().flatMap(c -> Stream.concat(c.getTopFront().stream(), c.getTopBack().stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.getTopFront(), cassette.getBack(), done, testislands, newislands);
                    if (island != null) {
                        island.setTag("frontcomplex "+i+" "+it);
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

            //var ggsinnewislands = newislands.stream().flatMap(i -> i.getGeneGroups().stream()).collect(Collectors.toSet());
            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            var prev2  = newislands.stream().flatMap(s -> s.getGeneGroups().stream()).collect(Collectors.toSet());

            if (prev.size() != prev2.size()) {
                System.err.println();
            }
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
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
            it++;

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
        } while(testislands.size() < islands.size());

        it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.getTopBack().stream().noneMatch(done::contains) && cassette.getTopFront().stream().noneMatch(done::contains) && cassette.getTopBack().stream().flatMap(c -> Stream.concat(c.getTopFront().stream(), c.getTopBack().stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.getTopBack(), cassette.getFront(), done, testislands, newislands);
                    if (island != null) {
                        island.setTag("backcomplex "+i+" "+it);
                        newislands.add(island);
                    }
                }
            }
            //var ggsinnewislands = newislands.stream().flatMap(i -> i.getGeneGroups().stream()).collect(Collectors.toSet());

            /*var la = islands.stream().filter(s -> s.getGeneGroups().stream().anyMatch(ggsinnewislands::contains)).findFirst();
            if (la.isPresent()) {
                var laa = la.get();
                var ia = newislands.stream().filter(i -> i.getGeneGroups().stream().anyMatch(g -> laa.getGeneGroups().contains(g))).findFirst();
                if (ia.isPresent()) {
                    System.err.println();
                }
            }*/

            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands.addAll(newislands);
            System.err.println("logging motif back");
            log(testislands, newislands);
            it++;

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
        } while(testislands.size() < islands.size());

        it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);

                if (!done.contains(cassette) && cassette.getTopFront().stream().noneMatch(done::contains) && cassette.getTopFront().stream().flatMap(c -> Stream.concat(c.getTopFront().stream(), c.getTopBack().stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.getTopFront(), cassette.getBack(), done, testislands, newislands);
                    if (island != null) {
                        island.setTag("frontcomplex2 "+i+" "+it);
                        newislands.add(island);
                    }
                }
            }

            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            var prev2  = newislands.stream().flatMap(s -> s.getGeneGroups().stream()).collect(Collectors.toSet());

            if (prev.size() != prev2.size()) {
                System.err.println();
            }
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands.addAll(newislands);

            System.err.println("logging motif front 2");
            log(testislands, newislands);
            it++;

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
        } while(testislands.size() < islands.size());

        it = 0;
        do {
            islands = new ArrayList<>(testislands);
            var newislands = new HashSet<Cassette>();
            var done = new HashSet<Cassette>();
            for (int i = 0; i < islands.size(); i++) {
                var cassette = islands.get(i);
                if (!done.contains(cassette) && cassette.getTopBack().stream().noneMatch(done::contains) && cassette.getTopFront().stream().noneMatch(done::contains) && cassette.getTopBack().stream().flatMap(c -> Stream.concat(c.getTopFront().stream(), c.getTopBack().stream())).noneMatch(done::contains)) {
                    var island = mergeMotifCheck(cassette, cassette.getTopBack(), cassette.getFront(), done, testislands, newislands);
                    if (island != null) {
                        island.setTag("backcomplex2 "+i+" "+it);
                        newislands.add(island);
                    }
                }
            }

            var prev = newislands.stream().flatMap(s -> s.prevIslands.stream()).collect(Collectors.toSet());
            testislands = islands.stream().filter(s -> !prev.contains(s)).collect(Collectors.toSet());
            testislands.addAll(newislands);
            System.err.println("logging motif back 2");
            log(testislands, newislands);
            it++;

            final var ti = testislands;
            if (testislands.stream().flatMap(g -> Stream.concat(g.getTopBack().stream(), g.getTopFront().stream())).anyMatch(p -> !ti.contains(p))) {
                System.err.println();
            }
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
        } while(testislands.size() < islands.size());*/

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

    public static void testMotif1(int testnum, boolean swap, boolean insert, boolean fault) {
        var f1 = new Cassette("f1");
        var a1 = new Cassette("a1");
        var a2 = new Cassette("a2");
        var frontSet = new HashSet<Cassette>();
        frontSet.add(a1);
        frontSet.add(a2);

        var backSet = new HashSet<>(frontSet);
        if (insert) backSet.add(f1);

        var cas = new Cassette("cas");
        if (insert) frontSet.add(cas);

        var nfb1 = new HashSet<Cassette>();
        nfb1.add(cas);
        if (fault) {
            var faultcas = new Cassette("fault");
            nfb1.add(faultcas);

            var faultset = new HashSet<Cassette>();
            faultset.add(a1);
            faultcas.setBack(faultset);
        }
        if (swap) nfb1.add(a2);
        var nfb2 = new HashSet<>(nfb1);

        var pfb1 = new HashSet<Cassette>();
        pfb1.add(f1);
        var pfb2 = new HashSet<>(pfb1);
        if (swap) pfb2.add(a1);

        a1.setFront(nfb1);
        a2.setFront(nfb2);
        a1.setBack(pfb1);
        a2.setBack(pfb2);

        var l1 = new Cassette("l1");
        var l2 = new Cassette("l2");
        var restSet = new HashSet<Cassette>();
        restSet.add(l1);
        restSet.add(l2);

        var rfb1 = new HashSet<Cassette>();
        rfb1.add(cas);
        var rfb2 = new HashSet<>(rfb1);
        l1.setBack(rfb1);
        l2.setBack(rfb2);

        cas.setBack(backSet);
        cas.setFront(restSet);

        f1.setFront(frontSet);

        var done = new HashSet<Cassette>();
        if (testnum == 0) {
            var isl = mergeMotifCheck(f1, frontSet, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        } else if (testnum == 1) {
            var isl = mergeMotifCheck(a1, nfb1, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        } else if(testnum == 2) {
            var isl = mergeMotifCheck(a1, pfb1, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        } else if(testnum == 3) {
            var isl = mergeMotifCheck(a2, nfb2, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        } else if(testnum == 4) {
            var isl = mergeMotifCheck(a2, pfb2, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        } else if(testnum == 5) {
            var isl = mergeMotifCheck(cas, backSet, Collections.emptyNavigableSet(), done, null, null);
            System.err.println(testnum + " " + isl);
        }
    }

    public static void main(String[] args) {
        testMotif1(0, false, false, false);
        testMotif1(1, false, false, false);
        testMotif1(2, false, false, false);
        testMotif1(3, false, false, false);
        testMotif1(4, false, false, false);
        testMotif1(5, false, false, false);

        testMotif1(0, false, false, true);
        testMotif1(1, false, false, true);
        testMotif1(2, false, false, true);
        testMotif1(3, false, false, true);
        testMotif1(4, false, false, true);
        testMotif1(5, false, false, true);
    }

    public static Island mergeMotifCheck(Cassette gg, Set<Cassette> frontBack, Set<Cassette> other, Set<Cassette> done, Set<Cassette> testislands, Set<Cassette> newislands) {
        var ggsets2 = frontBack.stream().map(ngg -> ngg.getTopFront().contains(gg) ? ngg.getTopFront() : ngg.getTopBack()).toList();
        var ggmap2 = new HashMap<Set<Cassette>,Integer>();
        var remset2 = new HashSet<>(frontBack);
        for (var ggset : ggsets2) {
            remset2.removeAll(ggset);
        }
        for (var ggset : ggsets2) {
            var nggset = new HashSet<>(ggset);
            if (nggset.removeAll(remset2)) {
                System.err.println();
            }
            if (nggset.size() > 0) ggmap2.merge(nggset, 1, Integer::sum);
        }

        if (ggmap2.size() == 1 && ggmap2.keySet().iterator().next().size() == 1) {

            var ggsets = frontBack.stream().map(ngg -> ngg.getTopFront().contains(gg) ? ngg.getTopBack() : ngg.getTopFront()).toList();
            var ggmap = new HashMap<Set<Cassette>, Integer>();
            var interconnected = false;
            var remset = new HashSet<>(frontBack);
            for (var ggset : ggsets) {
                remset.removeAll(ggset);
            }
            for (var ggset : ggsets) {
                var nggset = new HashSet<>(ggset);
                if (nggset.removeAll(remset)) {
                    interconnected = true;
                }
                if (nggset.size() > 0) ggmap.merge(nggset, 1, Integer::sum);
            }

            if (ggmap.size() == 1) {
                for (var ggset : ggmap.keySet()) {
                    if (ggset.size() == 1) {
                        if (interconnected) {
                            System.err.println();
                        }
                        var tgg = ggset.iterator().next();

                        var frontCheck = new HashSet<>(tgg.getTopFront());
                        frontCheck.removeAll(frontBack);
                        var reverseFrontCheck = new HashSet<>(frontBack);
                        reverseFrontCheck.removeAll(tgg.getTopFront());
                        var backCheck = new HashSet<>(tgg.getTopBack());
                        backCheck.removeAll(frontBack);
                        var reverseBackCheck = new HashSet<>(frontBack);
                        reverseBackCheck.removeAll(tgg.getTopBack());
                        if ((frontCheck.size() == 0 && reverseFrontCheck.size() == 0) || (backCheck.size() == 0 && reverseBackCheck.size() == 0)) {
                            if (interconnected) {
                                System.err.println();
                            }
                            var bother = frontCheck.size() == 0 ? tgg.getBack() : tgg.getFront();
                            var islandStream = Stream.concat(Stream.of(gg), frontBack.stream());
                            islandStream = Stream.concat(ggset.stream(), islandStream);
                            var islandSet = islandStream.collect(Collectors.toSet());

                            if (islandSet.stream().flatMap(i -> i.getGeneGroups().stream()).anyMatch(g -> g.getName().contains("ATP-binding subunit"))) {
                                System.err.println();
                            }

                            var island = new Island();

                            island.setFront(other);
                            island.setBack(bother);

                            island.addAll(islandSet);

                            done.addAll(islandSet);
                            done.add(island);
                            done.addAll(other);
                            done.addAll(bother);
                            return island;
                        }
                    }
                }
            } else if (ggmap.size() == 2) {
                var ggsetlist = ggmap.entrySet().stream().toList();
                var ggset1 = ggsetlist.get(0);
                var ggset2 = ggsetlist.get(1);
                var b1 = ggset1.getKey().size() == 1 && frontBack.containsAll(ggset1.getKey()) && ggset2.getValue() == 1;
                var b2 = ggset2.getKey().size() == 1 && frontBack.containsAll(ggset2.getKey()) && ggset1.getValue() == 1;
                if (b1 || b2) {
                    var g1key = ggset1.getKey().iterator();
                    var g2key = ggset2.getKey().iterator();
                    var tgg = b2 ? (g2key.hasNext() ? g2key.next() : null) : (g1key.hasNext() ? g1key.next() : null);
                    if (tgg != null) {
                        if (interconnected) {
                            System.err.println();
                        }
                        var frontCheck = new HashSet<>(tgg.getTopFront());
                        frontCheck.removeAll(frontBack);
                        var reverseFrontCheck = new HashSet<>(frontBack);
                        reverseFrontCheck.removeAll(tgg.getTopFront());
                        var backCheck = new HashSet<>(tgg.getTopBack());
                        backCheck.removeAll(frontBack);
                        var reverseBackCheck = new HashSet<>(frontBack);
                        reverseBackCheck.removeAll(tgg.getTopBack());
                        var a0 = frontCheck.size() == 1 && reverseFrontCheck.size() == 1;
                        var a1 = backCheck.size() == 1 && reverseBackCheck.size() == 1;
                        if (a0 || a1) {
                            if (interconnected) {
                                System.err.println();
                            }
                            var bother = frontCheck.size() == 0 ? tgg.getBack() : tgg.getFront();
                            var islandSet = Stream.concat(Stream.of(gg), frontBack.stream())
                                    .filter(Objects::nonNull).collect(Collectors.toSet());

                            if (islandSet.stream().flatMap(i -> i.getGeneGroups().stream()).anyMatch(g -> g.getName().contains("ATP-binding subunit"))) {
                                System.err.println();
                            }
                            //Island island;
                   /* if (islandSet.size() == 0) {
                        island = new Island(gg);
                        islands.add(island);
                    } else if (islandSet.size() == 1) {
                        island = islandSet.stream().findFirst().get();
                        island.add(gg);
                    } else {*/
                            var island = new Island();

                            island.setFront(other);
                            island.setBack(bother);

                            island.addAll(islandSet);
                            //islands.add(island);
                            //islands.removeAll(islandSet);
                            //islandSet.stream().flatMap(isl -> isl.geneGroups.stream()).forEach(island::add);
                            //}
                            done.addAll(islandSet);
                            done.add(island);
                            done.addAll(other);
                            done.addAll(bother);

                            return island;
                        }
                    }
                    //island.addAll(frontBack.stream().flatMap(g -> g.geneGroups.stream()));
                }
            }
        }
        return null;
    }

    public static Island mergeCheck(Cassette gg, Set<Cassette> frontBack, Set<Cassette> other, Set<Cassette> done) {
        if (setSizeCheck(frontBack)) {
            if (frontBack.size() == 1) {
                for (var ngg : frontBack) {
                    if (gg != ngg) {
                        if (checkSet(gg, ngg.getTopFront())) {
                            var bother = ngg.getBack();
                            var island = new Island();

                            island.add(gg);
                            island.add(ngg);

                            island.setFront(other);
                            island.setBack(bother);

                            /*replaceIsland(island.getFront(), gg, island);
                            replaceIsland(island.getBack(), ngg, island);

                            /*gg.setFront(null);
                            gg.setBack(null);
                            ngg.setBack(null);
                            ngg.setFront(null);*/

                            done.add(gg);
                            done.add(ngg);
                            done.add(island);

                            if (other.stream().anyMatch(o -> o == island)) {
                                System.err.println();
                            }

                            if (bother.stream().anyMatch(o -> o == island)) {
                                System.err.println();
                            }

                            return island;
                        } else if (checkSet(gg, ngg.getTopBack())) {
                            var bother = ngg.getFront();
                            var island = new Island();

                            island.add(gg);
                            island.add(ngg);

                            island.setFront(other);
                            island.setBack(bother);

                            done.add(gg);
                            done.add(ngg);
                            done.add(island);

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
            gg.setFront(frontSet);
            gg.setBack(backSet);
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
