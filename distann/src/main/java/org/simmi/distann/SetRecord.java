package org.simmi.distann;

import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.GeneGroup;

import java.util.Set;

public record SetRecord(Annotation a, Set<GeneGroup> sgg) {
}
