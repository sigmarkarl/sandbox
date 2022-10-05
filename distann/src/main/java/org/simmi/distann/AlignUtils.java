package org.simmi.distann;

import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.Tegeval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static List<Annotation> alignOffset(List<Annotation> lann, int window) {
        var ret = new ArrayList<Annotation>();
        Set<Annotation> currentAnn = new HashSet<>();
        for(var a1 : lann) {
            var contig = a1.getContig();
            var gg = a1.getGeneGroup();
            if (!currentAnn.contains(a1) && gg != null) {
                if (gg.getName().startsWith("UvrD")) {
                    System.err.println();
                }

                var u = 0;
                var nn = a1.getNext();
                while (!gg.equals(nn.getGeneGroup()) && u < window) {
                    u++;
                    nn = nn.getNext();
                }

                if (u==window) {
                    var max = 0;
                    for (var a2 : lann) {
                        if (/*a2.getGene() != null &&*/ a1 != a2 /*&& gg!=a2.getGeneGroup()*/) {
                            var k = 0;
                            var ck = 0;
                            var an = a2.getNext();
                            while (an != null && k < window) {
                                k++;
                                if (gg.equals(an.getGeneGroup())) {
                                    ck = k;
                                }
                                an = an.getNext();
                            }
                            if (an == null) ck = -1;
                            if (ck > max) {
                                max = ck;
                                currentAnn.add(a2);
                            }
                        }
                    }
                    if (max > 0) {
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
                    } else {
                        boolean found = false;
                        for (var a2 : ret) {
                            if (a2.getGene() != null && a1 != a2) {
                                var a1gg = a1.getGeneGroup();
                                var a2gg = a2.getGeneGroup();
                                if (a2gg == null || !a2gg.equals(a1gg)) {
                                    var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
                                    ret.add(te);
                                    if (contig.isReverse()) contig.injectAfter(a1, te);
                                    else contig.injectBefore(a1, te);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) ret.add(a1);
                    }
                } else {
                    /*boolean found = false;
                    for (var a2 : lann) {
                        if (a2.getGene() != null && a1 != a2) {
                            if (a2.getGeneGroup() != a1.getGeneGroup()) {
                                var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
                                ret.add(te);
                                if (contig.isReverse()) contig.injectAfter(a1, te);
                                else contig.injectBefore(a1, te);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) */
                    ret.add(a1);
                }
            } else {
                /*boolean found = false;
                        for (var a2 : lann) {
                            if (a2.getGene() != null && a1 != a2) {
                                if (a2.getGeneGroup() != a1.getGeneGroup()) {
                                    var te = new Tegeval(null, 0.0, null, contig, 0, 0, 1, false);
                                    ret.add(te);
                                    if (contig.isReverse()) contig.injectAfter(a1, te);
                                    else contig.injectBefore(a1, te);
                                    found = true;
                                    break;
                                }
                            }
                        }
                if (!found) */
                ret.add(a1);
            }
        }
        return ret;
    }
}
