package org.simmi.distann;

import java.io.Serializable;

public class JavaEdge implements Serializable {
    long srcId;
    long dstId;

    public long getSrcId() {
        return srcId;
    }

    public void setSrcId(long srcId) {
        this.srcId = srcId;
    }

    public long getDstId() {
        return dstId;
    }

    public void setDstId(long dstId) {
        this.dstId = dstId;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    String attr;

    public JavaEdge() {

    }

    public JavaEdge(long srcId, long dstId, int attr) {
        this.srcId = srcId;
        this.dstId = dstId;
        this.attr = Integer.toString(attr);
    }
}
