package com.armen.wai.util;

import java.util.Collections;
import java.util.Set;

/**
 * @author armen.mkrtchyan
 */
public class Node {

    private int weight;
    private final Set<Node> neighbours;
    private final String id;

    public Node(int weight, Set<Node> neighbours, String id) {
        this.weight = weight;
        this.neighbours = Collections.unmodifiableSet(neighbours);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Set<Node> getNeighbours() {
        return neighbours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Node node = (Node) o;

        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
