package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.AdjacencyList;
import com.armen.wai.util.helper.Edge;
import com.armen.wai.util.helper.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author armen.mkrtchyan
 */
public class MapAnalysisImpl implements MapAnalysis {

    private final SuperGraph superGraph;

    public MapAnalysisImpl(SuperGraph superGraph) {
        this.superGraph = superGraph;
    }

    public List<Region> suggestRegionOrder(Collection<Region> regions) {
        List<Region> suggestion = new ArrayList<>(regions);
        TreeMap<Integer, List<Region>> treeMap = new TreeMap<>(Comparator.reverseOrder());

        for (Region region : suggestion) {
            AdjacencyList adjacencyList = superGraph.getMinBranching(region.getId(), region.getSuperRegionId());
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
