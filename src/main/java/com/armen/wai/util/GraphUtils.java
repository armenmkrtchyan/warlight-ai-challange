package com.armen.wai.util;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;

/**
 * @author armen.mkrtchyan
 */
public class GraphUtils {
    public static <V, E> E addEdge(Graph<V, E> g, V sourceVertex, V targetVertex, double weight)
    {
        EdgeFactory<V, E> ef = g.getEdgeFactory();
        E e = ef.createEdge(sourceVertex, targetVertex);

        // we first create the edge and set the weight to make sure that
        // listeners will see the correct weight upon addEdge.

        assert (g instanceof WeightedGraph<?, ?>) : g.getClass();
        ((WeightedGraph<V, E>) g).setEdgeWeight(e, weight);

        return g.addEdge(sourceVertex, targetVertex, e) ? e : null;
    }
}
