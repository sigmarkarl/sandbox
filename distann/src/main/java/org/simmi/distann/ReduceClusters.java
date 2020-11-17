package org.simmi.distann;

import org.apache.spark.api.java.function.ReduceFunction;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class ReduceClusters implements ReduceFunction<String>, BinaryOperator<List<Set<String>>> {
    public ReduceClusters() {}

    @Override
    public String call(String v1, String v2) {
        List<Set<String>> lset1 = Arrays.stream(v1.split(";")).filter(s -> s.length()>0).map(s -> new HashSet<>(Arrays.asList(s.substring(1, s.length() - 1).split(",\\s*")))).collect(Collectors.toList());
        List<Set<String>> lset2 = Arrays.stream(v2.split(";")).filter(s -> s.length()>0).map(s -> new HashSet<>(Arrays.asList(s.substring(1, s.length() - 1).split(",\\s*")))).collect(Collectors.toList());

        List<Set<String>> ret = apply(lset1, lset2);
        //Set<String> one = new HashSet<>(Arrays.asList(v1.substring(1, v1.length() - 1).split(",")));
        //Set<String> two = new HashSet<>(Arrays.asList(v1.substring(1, v1.length() - 1).split(",")));

        return ret.stream().map(Object::toString).collect(Collectors.joining(";"));
    }

    @Override
    public List<Set<String>> apply(List<Set<String>> lset1, List<Set<String>> lset2) {
        List<Set<String>> lset2tmp = new ArrayList<>(lset2);
        lset1.forEach(set -> {
            List<Set<String>> rem = new ArrayList<>();
            Set<String> add = new HashSet<>(set);
            for(Set<String> ss : lset2tmp) {
                for(String v : ss) {
                    if(set.contains(v)) {
                        add.addAll(ss);
                        rem.add(ss);
                        break;
                    }
                }
            }
            if(rem.size()>0) {
                lset2tmp.removeAll(rem);
            }
            lset2tmp.add(add);
        });
        return lset2tmp;
    }
}
