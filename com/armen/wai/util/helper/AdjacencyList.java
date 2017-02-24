/* Copyright (C) 2009-2011  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.armen.wai.util.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Asad
 */
public class AdjacencyList implements Cloneable {

    private Map<Node, List<Edge>> adjacencies = new HashMap<Node, List<Edge>>();

    public AdjacencyList() {
    }

    public AdjacencyList(List<Edge> adjacencies) {
        this.adjacencies = adjacencies.stream().collect(Collectors.groupingBy(Edge::getFrom));
    }

    /**
     *
     * @param adjacencies
     */
    public void setAdjacencyList(Map<Node, List<Edge>> adjacencies) {
        this.adjacencies.putAll(adjacencies);
    }

    /**
     *
     * @param source
     * @param target
     * @param weight
     */
    public void addEdge(Node source, Node target, int weight) {
        List<Edge> list;
        if (!adjacencies.containsKey(source)) {
            list = new ArrayList<>();
            adjacencies.put(source, list);
        } else {
            list = adjacencies.get(source);
        }
        list.add(new Edge(source, target, weight));
    }

    public void addEdge(Integer sourceId, Integer targetId, int weight) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);
        addEdge(source, target, weight);
    }


    public void removeTargets(Collection<Node> targets) {
        this.adjacencies.values()
                .forEach(edges -> edges.removeIf(edge -> targets.contains(edge.getTo())));
    }

    public void setEdgeWeight(Integer sourceId, Integer targetId, int weight) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);
        adjacencies.computeIfPresent(source,
                (node, edges) -> {
                    edges.stream().filter(edge -> edge.getTo().equals(target))
                            .findFirst().orElseThrow(RuntimeException::new).setWeight(weight);
                    return edges;
                });

    }

    public void setAdjacencyWeight(Integer targetId, int weight) {
        Node target = new Node(targetId);
        getAllEdges().stream()
                .filter(edge -> edge.getTo().getId() == targetId)
                .forEach(edge -> edge.setWeight(weight));

    }

    /**
     *
     * @param source
     * @return
     */
    public List<Edge> getAdjacent(Node source) {
        return adjacencies.get(source);
    }

    /**
     *
     * @param e
     */
    public void reverseEdge(Edge e) {
        adjacencies.get(e.getFrom()).remove(e);
        addEdge(e.getTo(), e.getFrom(), e.getWeight());
    }

    /**
     *
     */
    public void reverseGraph() {
        setAdjacencies(getReversedList().adjacencies);
    }

    /**
     *
     * @return
     */
    public AdjacencyList getReversedList() {
        AdjacencyList newlist = new AdjacencyList();
        for (List<Edge> edges : adjacencies.values()) {
            for (Edge e : edges) {
                newlist.addEdge(e.getTo(), e.getFrom(), e.getWeight());
            }
        }
        return newlist;
    }

    public Integer getTotalWeight() {
        final int[] sum = {0};
        getAllEdges().forEach(edge -> sum[0] += edge.getWeight());
        return sum[0];
    }

    /**
     *
     * @return
     */
    public Set<Node> getSourceNodeSet() {
        return adjacencies.keySet();
    }

    /**
     *
     * @return
     */
    public Collection<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<Edge>();
        for (List<Edge> e : adjacencies.values()) {
            edges.addAll(e);
        }
        return edges;
    }

    /**
     * @param adjacencies the adjacencies to set
     */
    private void setAdjacencies(Map<Node, List<Edge>> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public void clear() {
        if (this.adjacencies != null) {
            adjacencies.clear();
        }
    }

    @Override
    public AdjacencyList clone() {
        try {
            AdjacencyList adjacencyList = (AdjacencyList) super.clone();
            adjacencyList.adjacencies = new HashMap<>();
            for (Map.Entry<Node, List<Edge>> entry : adjacencies.entrySet()) {
                adjacencyList.adjacencies.put(entry.getKey().clone(),
                        entry.getValue().stream().map(Edge::clone).collect(
                                Collectors.toList()));
            }
            return adjacencyList;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
