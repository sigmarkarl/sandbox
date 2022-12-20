package org.simmi.javafasta.shared;

import java.util.*;
import java.util.stream.Collectors;

public class Cassette {
    public String tag;
    public final static Map<String,Cassette> allIslands = new HashMap<>();
    private Set<Cassette>    front = Collections.emptySet();
    private Set<Cassette>	back = Collections.emptySet();
    private Cassette         parent;
    public Set<Cassette>    prevIslands = new HashSet<>();

    public Cassette() {}

    public Cassette(String tag) {
        this.tag = tag;
    }

    public Cassette getParent() {
        return parent;
    }

    public void setTag(String tag) {
        this.tag = tag;
        allIslands.put(tag, this);
    }

    @Override
    public String toString() {
        return tag;
    }

    public void setParent(Cassette parent) {
        /*if (front != null && back != null) {
            if (front.contains(parent) || back.contains(parent) || front.stream().anyMatch(p -> p.parent != null && p.parent != parent) || back.stream().anyMatch(p -> p.parent != null && p.parent != parent)) {
                System.err.println();
            }
        }*/
        this.parent = parent;
    }

    //public java.util.Set<GeneGroup> geneGroups = new java.util.HashSet<>();

    public Set<Cassette> getTopFront() {
        var ret = front.stream().map(Cassette::getTopParent).collect(Collectors.toSet());
        ret.remove(this);
        return ret;
    }

    public Set<Cassette> getTopBack() {
        var ret = back.stream().map(Cassette::getTopParent).collect(Collectors.toSet());
        ret.remove(this);
        return ret;
    }

    public Set<Cassette> getFront() {
        return front;
    }

    public Set<Cassette> getBack() {
        return back;
    }

    public void setFront(Set<Cassette> front) {
        /*if (front != null) {
            if (front.contains(parent) || front.stream().anyMatch(p -> p.parent != null)) {
                System.err.println();
            }
        }*/
        this.front = front;
    }

    public void setBack(Set<Cassette> back) {
        /*if (back != null) {
            if (back.contains(parent) || back.stream().anyMatch(p -> p.parent != null)) {
                System.err.println();
            }
        }*/
        this.back = back;
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

    public Cassette getTopParent() {
        return parent != null ? parent.getTopParent() : this;
    }

    public void add(Cassette gg) {
        gg.setParent(Cassette.this);
        prevIslands.add(gg);
    }

    public void addAll(Collection<Cassette> cgg) {
        for (var gg : cgg) {
            gg.setParent(Cassette.this);
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
