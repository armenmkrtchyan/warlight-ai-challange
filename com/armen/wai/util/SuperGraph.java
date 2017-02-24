package com.armen.wai.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author armen.mkrtchyan
 */
public class SuperGraph extends Graph {

    private final Collection<Graph> subGraphs;

    public SuperGraph(Collection<Node> nodes, Collection<Graph> subGraphs) {
        super(nodes);
        this.subGraphs = Collections.unmodifiableCollection(subGraphs);
    }

    public Collection<Graph> getSubGraphs() {
        return subGraphs;
    }
}
