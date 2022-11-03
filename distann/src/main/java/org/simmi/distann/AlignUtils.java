package org.simmi.distann;

import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Tegeval;

import java.util.*;

public class AlignUtils {

    public static boolean checkForward(Annotation a1, List<Annotation> lann, int window, int prevmax) {
        var gg = a1.getGeneGroup();
        var max = 0;
        for (var a2 : lann) {
            if (a2.getGene() != null && a1 != a2 /*&& gg!=a2.getGeneGroup()*/) {
                var k = 0;
                var ck = 0;
                var an = a2.getNext();
                while (an != null && k < window) {
                    k++;
                    if (gg == an.getGeneGroup()) {
                        ck = k;
                    }
                    an = an.getNext();
                }
                if (an == null) ck = -1;
                max = Math.max(max, ck);
            }
        }
        return max > 0 && max < prevmax;
    }

    public static List<Annotation> align(List<Annotation> lann, int window) {
        var ret = new ArrayList<Annotation>();
        Annotation currentAnn = null;
        for(var a1 : lann) {
            var gg = a1.getGeneGroup();
            if (a1 != currentAnn && gg != null) {
                var u = 0;
                var nn = a1.getNext();
                if (nn!=null) {
                    while (gg != nn.getGeneGroup() && u < window) {
                        u++;
                        nn = nn.getNext();
                    }
                    if (u == window) {
                        var contig = a1.getContig();
                        var max = 0;
                        for (var a2 : lann) {
                            if (a2.getGene() != null && a1 != a2 /*&& gg!=a2.getGeneGroup()*/) {
                                var k = 0;
                                var ck = 0;
                                var an = a2.getNext();
                                while (an != null && k < window) {
                                    k++;
                                    if (gg == an.getGeneGroup()) {
                                        ck = k;
                                    }
                                    an = an.getNext();
                                }
                                if (an == null) ck = -1;
                                if (ck > max) {
                                    max = ck;
                                    currentAnn = a2;
                                }
                            }
                        }
                        if (max > 0) {
                            boolean b = false;
                        /*var an = a1.getNext();
                        if (an != null && max > 1) {
                            b = checkForward(an, lann, window, max-1);
                            an = an.getNext();
                            if (!b && an != null && max > 2) {
                                b = checkForward(an, lann, window, max-2);
                                an = an.getNext();
                                if (!b && an != null && max > 3) {
                                    b = checkForward(an, lann, window, max-3);
                                    an = an.getNext();
                                    if (!b && an != null && max > 4) {
                                        b = checkForward(an, lann, window, max-4);
                                    }
                                }
                            }
                        }*/

                            if (!b) {
                                var done = false;
                                while (max > 0) {
                                    var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
                                    if (!done) {
                                        done = true;
                                        ret.add(te);
                                    }
                                    if (contig.isReverse()) contig.injectAfter(a1, te);
                                    else contig.injectBefore(a1, te);
                                    max--;
                                }
                            }
                        } else {
                            ret.add(a1);
                        }
                    } else {
                        ret.add(a1);
                    }
                }
            } else {
                ret.add(a1);
            }
        }
        return ret;
    }

    public static boolean notUntilWindow(Annotation a1, GeneGroup gg, int window) {
        var u = 0;
        var nn = a1.getNext();
        while (nn != null && !gg.equals(nn.getGeneGroup()) && u < window) {
            u++;
            nn = nn.getNext();
        }
        return nn == null || u==window;
    }

    public static Tegeval injectFromMax(Annotation a, int max) {
        Tegeval ret = null;
        var contig = a.getContig();
        var done = false;
        while (max > 0) {
            var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
            if (!done) {
                done = true;
                ret = te;
            }
            if (contig.isReverse()) contig.injectAfter(a, te);
            else contig.injectBefore(a, te);
            max--;
        }
        return ret;
    }

    public static boolean findDissimilar(Annotation a1, List<Annotation> ret) {
        var contig = a1.getContig();
        for (var a2 : ret) {
            if (!a2.isPseudo() && a1 != a2) {
                var a1gg = a1.getGeneGroup();
                var a2gg = a2.getGeneGroup();
                if (a2gg == null || !a2gg.equals(a1gg)) {
                    var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
                    ret.add(te);
                    if (contig.isReverse()) contig.injectAfter(a1, te);
                    else contig.injectBefore(a1, te);
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Annotation> nextAnnotation(List<Annotation> lann, Map<GeneGroup,Integer> maxmap, int window) {
        var ret = new ArrayList<Annotation>();
        for (var a1 : lann) {
            var max = maxmap.get(a1.getGeneGroup());
            var gg = a1.getGeneGroup();
            if (gg != null && notUntilWindow(a1, gg, window)) {
                if (max > 0) {
                    var ta = injectFromMax(a1, max);
                    if (ta!=null) ret.add(ta);
                } else if (!findDissimilar(a1, ret)) {
                    ret.add(a1);
                }
            } else {
                ret.add(a1);
            }
        }
        return ret;
    }

    public static List<Annotation> alignOffset(List<Annotation> lann, int window) {
        var maxmap = new HashMap<GeneGroup,Integer>();
        for(var a1 : lann) {
            var gg = a1.getGeneGroup();
            int max = 0;
            if (gg != null && notUntilWindow(a1, gg, window)) {
                var maxset = new TreeMap<Integer, SetRecord>();
                for (var a2 : lann) {
                    if (!a2.isPseudo() && a1 != a2 /*&& gg!=a2.getGeneGroup()*/) {
                        var tgroups = new HashSet<GeneGroup>();
                        var ggroups = new HashSet<GeneGroup>();
                        var k = 0;
                        var ck = 0;
                        var an = a2.getNext();
                        while (an != null && k < window) {
                            k++;
                            var agg = an.getGeneGroup();
                            if (gg.equals(agg)) {
                                ck = k;
                                tgroups.addAll(ggroups);
                                ggroups.clear();
                            } else if (agg != null) {
                                ggroups.add(agg);
                            }
                            an = an.getNext();
                        }
                        if (an == null) ck = -1;
                        if (ck > 0) maxset.put(ck, new SetRecord(a2, tgroups));
                    }
                }
                for (var ck : maxset.descendingKeySet()) {
                    var sr = maxset.get(ck);
                    var tgroups = new HashSet<>(sr.sgg());
                    /*var a2 = sr.a();
                    for (var a3 : lann) {
                        if (!a3.isPseudo() && a1 != a3 && a2 != a3 && gg != a3.getGeneGroup()) {
                            var anext = a3.getNext();
                            for (int m = 0; m < ck - 1; m++) {
                                if (anext == null) break;
                                tgroups.add(anext.getGeneGroup());
                                anext = anext.getNext();
                            }
                        }
                    }*/
                    var check = true;
                    for (var a3 : lann) {
                        if (!a3.isPseudo() && a1 == a3 /*&& a2 != a3*/ && gg == a3.getGeneGroup()) {
                            var anext = a3.getNext();
                            for (int m = 0; m < ck - 1; m++) {
                                if (anext == null) break;
                                if (tgroups.contains(anext.getGeneGroup())) {
                                    check = false;
                                    //if (!ret.contains(a3)) ret.add(a3);
                                    break;
                                }
                                anext = anext.getNext();
                            }
                        }
                    }
                    if (check) {
                        max = ck;
                        break;
                    }
                }
            }
            final int fmax = max;
            maxmap.compute(gg, (geneGroup, i) -> i!=null?Math.min(fmax,i):fmax);
        }
        return nextAnnotation(lann, maxmap, window);
    }
}
