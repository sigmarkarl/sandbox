package org.simmi.javafasta.shared;

import java.util.*;
import java.util.stream.Collectors;

public class Cassette {
    public Set<Cassette>    front = Collections.emptySet();
    public Set<Cassette>	back = Collections.emptySet();
    public Cassette         parent;
    public Set<Cassette>    prevIslands = new HashSet<>();

    //public java.util.Set<GeneGroup> geneGroups = new java.util.HashSet<>();

    public Set<Cassette> getFront() {
        return front;
    }

    public Set<Cassette> getBack() {
        return back;
    }

    public Set<GeneGroup> getGeneGroups() {
        return prevIslands.stream().flatMap(g -> g.getGeneGroups().stream()).collect(Collectors.toSet());
    }

    public String getName() {
        return prevIslands.stream().map(Cassette::getName).filter(p -> !p.startsWith("hypot")).findFirst().orElse("hypothetical protein");
    }

    public int getSize() {
        return prevIslands.stream().mapToInt(Cassette::getSize).sum();
    }

    public int getIslandSize() {
        return parent != null ? parent.getIslandSize() : getSize();
    }

    public String getIslandId() {
        return parent != null ? parent.getIslandId() : getName();
    }

    public void add(Cassette gg) {
        gg.parent = Cassette.this;
        prevIslands.add(gg);
    }

    public void addAll(Collection<Cassette> cgg) {
        for (var gg : cgg) {
            gg.parent = Cassette.this;
        }
        prevIslands.addAll(cgg);
    }

    public Islinfo getInfo(String spec) {
        return prevIslands.stream().map(g -> g.getInfo(spec)).findFirst().get();
    }

    public boolean contains(GeneGroup gg) {
        return prevIslands.stream().anyMatch(p -> p.contains(gg));
    }
}
