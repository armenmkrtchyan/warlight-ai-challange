package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.RegionEdge;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.util.helper.OwnerType;

import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;

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

    private final WarlightMap warlightMap;

    public MapAnalysisImpl(WarlightMap warlightMap) {
        this.warlightMap = warlightMap;
    }

    public List<Region> suggestRegionOrder(Collection<Region> regions) {
        List<Region> suggestion = new ArrayList<>(regions);
        TreeMap<Integer, List<Region>> treeMap = new TreeMap<>(Comparator.reverseOrder());

        for (Region region : suggestion) {
            KruskalMinimumSpanningTree<Region, RegionEdge> spanningTreeAlgorithm = new KruskalMinimumSpanningTree<>(
                    warlightMap.getSuperRegionGraph(region.getSuperRegionId()));
            SpanningTreeAlgorithm.SpanningTree<RegionEdge> spanningTree = spanningTreeAlgorithm.getSpanningTree();
            int weight = Double.valueOf(spanningTree.getWeight()).intValue();
            int key = weight / spanningTree.getEdges().size();
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
        List<Region> allRegions = new ArrayList<>(warlightMap.getAllRegions());
        allRegions.removeIf(region -> !region.getOwner().equals(OwnerType.Self));
        return suggestRegionOrder(allRegions);
    }
}
