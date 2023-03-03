package org.simmi.javafasta.shared;

import java.io.Serializable;

public class BaseGeneGroup extends Cassette implements Serializable {
    long id;
    String name;

    static long globalId = 0;

    public BaseGeneGroup() {
        id = ++globalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newname) {
        name = newname;
    }

    public long getId() {
        return id;
    }

    public void setId(long newid) {
        id = newid;
    }
}
