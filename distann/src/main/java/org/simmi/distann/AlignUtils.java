package org.simmi.distann;

import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.Tegeval;

import java.util.ArrayList;
import java.util.List;

public class AlignUtils {
    public static List<Annotation> align(List<Annotation> lann) {
        var ret = new ArrayList<Annotation>();
        for(var a1 : lann) {
            var gg = a1.getGeneGroup();
            if (gg != null) {
                var u = 0;
                var nn = a1.getNext();
                while (gg != nn.getGeneGroup() && u < 10) {
                    u++;
                    nn = nn.getNext();
                }
                if (u==10) {
                    var contig = a1.getContig();
                    var max = 0;
                    for (var a2 : lann) {
                        if (a2.getGene() != null && a1 != a2 /*&& gg!=a2.getGeneGroup()*/) {
                            var k = 0;
                            var ck = 0;
                            var an = a2.getNext();
                            while (an != null && k < 10) {
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
                        ret.add(a1);
                    }
                } else {
                    ret.add(a1);
                }
            } else {
                ret.add(a1);
            }
        }
        return ret;
    }
}
