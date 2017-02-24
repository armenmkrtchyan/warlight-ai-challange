package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;
import com.armen.wai.util.SuperGraph;

import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public class BattleAnalysisImpl implements BattleAnalysis {

    private final SuperGraph superGraph;

    public BattleAnalysisImpl(SuperGraph superGraph) {
        this.superGraph = superGraph;
    }

    @Override
    public List<Deployment> suggestDeployment(List<Region> orderedRegions) {

        return;
    }
}
