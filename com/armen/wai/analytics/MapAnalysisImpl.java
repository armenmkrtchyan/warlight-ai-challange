package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.AdjacencyList;
import com.armen.wai.util.helper.Edge;
import com.armen.wai.util.helper.Node;
import com.armen.wai.util.helper.OwnerType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class MapAnalysisImpl implements MapAnalysis {

    private final SuperGraph superGraph;
    private final WarlightMap warlightMap;

    public MapAnalysisImpl(SuperGraph superGraph, WarlightMap warlightMap) {
        this.superGraph = superGraph;
        this.warlightMap = warlightMap;
    }

    public List<Region> suggestRegionOrder(Collection<Region> regions) {
        List<Region> suggestion = new ArrayList<>(regions);
        TreeMap<Integer, List<Region>> treeMap = new TreeMap<>(Comparator.reverseOrder());

        for (Region region : suggestion) {
            AdjacencyList adjacencyList = superGraph.getMinBranching(region.getId(),
                    region.getSuperRegionId());
            int weight = adjacencyList.getTotalWeight();
            int depth = findDepth(adjacencyList, new Node(region.getId()));
            int key = weight + depth;
            treeMap.computeIfAbsent(key, ArrayList::new);
            treeMap.get(key).add(region);
        }
        ArrayList<Region> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Region>> entry : treeMap.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    @Override
    public List<Region> suggestDeploymentRegions() {
        Set<Integer> ownNodes = getOwnNodes();
        List<Region> allRegions = new ArrayList<>(warlightMap.getAllRegions());
        allRegions.removeIf(region -> ownNodes.contains(region.getId()));
        return suggestRegionOrder(allRegions);
    }

    public Set<Integer> getOwnNodes() {
        return superGraph.getSourceNodeSet()
                .stream()
                .filter(node -> !node.getOwnerType().equals(
                        OwnerType.Self)).
                        map(Node::getId).
                        collect(Collectors.toSet());
    }

    private Integer findDepth(AdjacencyList adjacencyList, Node rootNode) {
        adjacencyList.getSourceNodeSet();
        if (!adjacencyList.getAdjacent(rootNode).isEmpty()) {
            return 1;
        } else {
            int max = 0;
            for (Edge edge : adjacencyList.getAdjacent(rootNode)) {
                max = Math.max(findDepth(adjacencyList, edge.getTo()), max);
            }
            return max + 1;
        }

    }


}
