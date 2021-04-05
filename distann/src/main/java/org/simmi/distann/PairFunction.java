package org.simmi.distann;

import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.simmi.javafasta.shared.FastaSequence;
import scala.Tuple2;

import java.util.Collections;
import java.util.Iterator;

public class PairFunction implements PairFlatMapFunction<Iterator<FastaSequence>, String, Integer> {
    @Override
    public Iterator<Tuple2<String, Integer>> call(Iterator<FastaSequence> fastaSequenceIterator) throws Exception {
        int count = 0;
        String group = "empty";
        while(fastaSequenceIterator.hasNext()) {
            group = fastaSequenceIterator.next().getGroup();
            count++;
        }
        return Collections.singletonList(new Tuple2<>(group,count)).iterator();
    }
}
