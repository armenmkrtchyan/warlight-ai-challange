package com.armen.wai.util;

import com.armen.wai.util.algorithm.EdmondsChuLiu;
import com.armen.wai.util.helper.AdjacencyList;
import com.armen.wai.util.helper.Node;
import com.armen.wai.util.helper.OwnerType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class SuperGraph extends AdjacencyList {

    private final Map<Integer, AdjacencyList> subGraphs = new HashMap<>();

    public void group(Integer groupingKey, Set<Integer> nodeIds) {
        subGraphs.put(groupingKey,
                new AdjacencyList(getAllEdges().stream()
                        .filter(edge -> nodeIds.contains(edge.getFrom()
                                .getId()) || nodeIds.contains(edge.getTo().getId()))
                        .collect(Collectors.toList())));
    }

    public void groupAll(Map<Integer, Set<Integer>> nodeGroups) {
        for (Map.Entry<Integer, Set<Integer>> entry : nodeGroups.entrySet()) {
            group(entry.getKey(), entry.getValue());
        }
    }


    public Map<Integer, AdjacencyList> getSubGraphs() {
        return subGraphs;
    }


    public AdjacencyList getSubGraph(Integer groupKey) {
        return subGraphs.get(groupKey);
    }

    public AdjacencyList getMinBranching(Integer root, Integer subGraphGroup) {
        AdjacencyList adjacencyList = getSubGraph(subGraphGroup).clone();
        adjacencyList.removeTargets(getSelfNodes(adjacencyList));
        return new EdmondsChuLiu().getMinBranching(new Node(root),
                adjacencyList);
    }

    public List<Node> getSelfNodes(AdjacencyList adjacencyList) {
        return adjacencyList.getSourceNodeSet()
                .stream()
                .filter(node -> node.getOwnerType().equals(OwnerType.Self))
                .collect(Collectors.toList());
    }


    public void updateOwnership(Set<Integer> id, OwnerType ownerType) {
        getSourceNodeSet().stream()
                .filter(node -> id.contains(node.getId()))
                .forEach(node -> node.setOwnerType(ownerType));
    }

    public void updateOwnership(Integer id, OwnerType ownerType) {
        getSourceNodeSet().stream()
                .filter(node -> id.equals(node.getId()))
                .forEach(node -> node.setOwnerType(ownerType));
    }

}
