package com.armen.wai.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class Graph {

    private final Map<String, Node> nodes;

    public Graph(Collection<Node> nodes) {
        this.nodes = Collections.unmodifiableMap(nodes.stream()
                .collect(Collectors.toMap(Node::getId, Function.identity())));
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public Collection<Node> getNodes() {
        return nodes;
    }
}
