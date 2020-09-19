package org.simmi.distann;

import org.apache.spark.api.java.function.ReduceFunction;

import java.util.*;
import java.util.stream.Collectors;

public class ReduceClusters implements ReduceFunction<String[]> {
    public ReduceClusters() {}

    @Override
    public String[] call(String[] v1, String[] v2) {
        List<String> ret = new ArrayList<>();
        List<Set<String>> lset1 = Arrays.stream(v1).map(s -> new HashSet<>(Arrays.asList(s.substring(1, s.length() - 1).split(",\\s*")))).collect(Collectors.toList());
        List<Set<String>> lset2 = Arrays.stream(v2).map(s -> new HashSet<>(Arrays.asList(s.substring(1, s.length() - 1).split(",\\s*")))).collect(Collectors.toList());

        lset1.forEach(set -> {
            List<Set<String>> rem = new ArrayList<>();
            for(Set<String> ss : lset2) {
                for(String v : ss) {
                    if(set.contains(v)) {
                        set.addAll(ss);
                        rem.add(ss);
                        break;
                    }
                }
            }
            if(rem.size()>0) {
                lset2.removeAll(rem);
                lset2.add(set);
            } else {
                ret.add(set.toString());
            }
        });
        ret.addAll(lset2.stream().map(Object::toString).collect(Collectors.toList()));
        //Set<String> one = new HashSet<>(Arrays.asList(v1.substring(1, v1.length() - 1).split(",")));
        //Set<String> two = new HashSet<>(Arrays.asList(v1.substring(1, v1.length() - 1).split(",")));
        return ret.toArray(String[]::new);
    }
}
