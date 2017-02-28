package com.armen.wai.map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author armen.mkrtchyan
 */
public class RegionEdge extends DefaultWeightedEdge {

    private final Region source;
    private final Region target;

    public RegionEdge(Region source, Region target) {

        this.target = target;
        this.source = source;
    }

    public Region getSource() {
        return source;
    }

    public Region getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "RegionEdge{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
