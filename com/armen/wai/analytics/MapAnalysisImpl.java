package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.AdjacencyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        for (Region region : suggestion) {
            AdjacencyList adjacencyList = superGraph.getMinBranching(region.getId(), region.getSuperRegionId());
            adjacencyList.getTotalWeight();
        }
        return null;
    }

}
