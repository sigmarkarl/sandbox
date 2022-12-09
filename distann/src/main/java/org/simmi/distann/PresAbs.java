package org.simmi.distann;

import org.apache.spark.ml.feature.PCA;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Sequence;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

import static org.apache.spark.sql.types.DataTypes.StringType;

public class PresAbs {
    SparkSession sparkSession;
    GeneSet geneSet;

    public PresAbs(GeneSet geneSet) {
        this.geneSet = geneSet;
        this.sparkSession = geneSet.sparkSession;
    }

    public void phylo() {
        SwingUtilities.invokeLater(() -> {
            var specSet = new SpeciesSelection(geneSet).getSelspec(null, geneSet.specList, false);
            int specNum = specSet.size();
            var sublist = new ArrayList<GeneGroup>();
            for (var gg : geneSet.allgenegroups) {
                var ggset = new HashSet<>(gg.getSpecies());
                ggset.retainAll(specSet);
                var ggspecNum = ggset.size();
                if (ggspecNum > 1 && ggspecNum < specNum) {
                    sublist.add(gg);
                }
            }
            var seqs = new ArrayList<Sequence>();
            for (var spec : specSet) {
                var presabs = new Sequence();
                presabs.setName(spec);
                seqs.add(presabs);
                for (int i = 0; i < sublist.size(); i++) {
                    presabs.append(sublist.get(i).getSpecies().contains(spec) ? 'A' : 'C');
                }
            }
            try (var fileout = Files.newBufferedWriter(Path.of("/Users/sigmar/al.fasta"))) {
                for (var seq : seqs) {
                    seq.writeSequence(fileout);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void pca() {
        SwingUtilities.invokeLater(() -> {
            var specSet = new SpeciesSelection(geneSet).getSelspec(null, geneSet.specList, false);
            int specNum = specSet.size();
            var sublist = new ArrayList<GeneGroup>();
            for (var gg : geneSet.allgenegroups) {
                var ggset = new HashSet<>(gg.getSpecies());
                ggset.retainAll(specSet);
                var ggspecNum = ggset.size();
                if (ggspecNum > 1 && ggspecNum < specNum) {
                    sublist.add(gg);
                }
            }
            var rowlist = new ArrayList<Row>();
            for (var spec : specSet) {
                var presabs = new double[sublist.size()];
                for (int i = 0; i < presabs.length; i++) {
                    presabs[i] = sublist.get(i).getSpecies().contains(spec) ? 1.0 : 0.0;
                }
                var row = RowFactory.create(spec, Vectors.dense(presabs));
                rowlist.add(row);
            }
            //var schema = StructType.fromDDL("label string,features array<double>");

            var fields = new StructField[2];
            fields[0] = new StructField("label", StringType, true, Metadata.empty());
            fields[1] = new StructField("features", new VectorUDT(), true, Metadata.empty());
            var schema = new StructType(fields);
            var dataset = sparkSession.createDataset(rowlist, RowEncoder.apply(schema));
            //dataset.withColumn("features", Vectors.);

            var pca = new PCA("pca");
            pca.setK(3);
            pca.setInputCol("features");
            pca.setOutputCol("dims");
            var model = pca.fit(dataset);

            var evar = model.explainedVariance();
            System.err.println(evar.toString());

            var res = model.transform(dataset);
            sparkSession.udf().register("vectorToArray", (UDF1<Vector, double[]>) Vector::toArray, DataTypes.createArrayType(DataTypes.DoubleType));
            sparkSession.udf().register("vectorToString", (UDF1<Vector, String>) Object::toString, DataTypes.StringType);
            res = res.withColumn("dims", functions.callUDF("vectorToString", functions.col("dims")));
            res.select("label","dims").coalesce(1).write().format("csv").option("header",true).option("delimiter","\t").mode(SaveMode.Overwrite).save("/Users/sigmar/data.tsv");
        });
    }
}
