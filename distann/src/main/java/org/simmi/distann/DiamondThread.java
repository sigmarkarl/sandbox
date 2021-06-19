package org.simmi.distann;

import org.simmi.javafasta.shared.FastaSequence;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class DiamondThread implements Callable<Optional<List<Set<String>>> > {
    SparkBlast sparkBlast;
    Stream<FastaSequence> sparkSeqList;

    public DiamondThread(SparkBlast sparkBlast, Stream<FastaSequence> stuff) {
        this.sparkBlast = sparkBlast;
        this.sparkSeqList = stuff;
    }

    @Override
    public Optional<List<Set<String>>> call() throws Exception {
        Optional<List<Set<String>>> subpart = null;
        try {
            Stream<List<Set<String>>> repart = sparkBlast.stream(sparkSeqList);
						/*Serifier s = new Serifier();
						//s.mseq = aas;
						for (String gk : refmap.keySet()) {
							Annotation a = refmap.get(gk);
							s.mseq.put(gk, a.getAlignedSequence());
						}

						Map<String, String> idspec = new HashMap<>();
						for (String idstr : refmap.keySet()) {
							Annotation a = refmap.get(idstr);
							Gene gene = a.getGene();
							if (gene != null) idspec.put(idstr, gene.getSpecies());
						}*/
            ReduceClusters reduceCluster = new ReduceClusters();
            Optional<List<Set<String>>> ototali = repart.reduce(reduceCluster);
            return ototali;
            //return subpart;
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return subpart;
    }
}
