package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.RegionEdge;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.util.helper.OwnerType;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.util.Pair;

import java.util.*;

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
        TreeMap<Pair<Integer, Integer>, List<Region>> treeMap = new TreeMap<>((o1, o2) -> {
            int compareTo = o1.getFirst().compareTo(o2.getFirst());
            if (compareTo == 0) {
                return -1 * o1.getSecond().compareTo(o2.getSecond());
            } else {
                return -1 * compareTo;
            }
        });

        for (Region region : suggestion) {
            KruskalMinimumSpanningTree<Region, RegionEdge> spanningTreeAlgorithm = new KruskalMinimumSpanningTree<>(
                    warlightMap.getSuperRegionGraph(region.getSuperRegionId()));
            SpanningTreeAlgorithm.SpanningTree<RegionEdge> spanningTree = spanningTreeAlgorithm.getSpanningTree();
            int weight = Double.valueOf(spanningTree.getWeight()).intValue();
            Pair<Integer, Integer> key = Pair.of(weight, spanningTree.getEdges().size());
            treeMap.computeIfAbsent(key, aDouble -> new ArrayList<>());
            treeMap.get(key).add(region);
        }
        ArrayList<Region> result = new ArrayList<>();
        for (Map.Entry<Pair<Integer, Integer>, List<Region>> entry : treeMap.entrySet()) {
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
