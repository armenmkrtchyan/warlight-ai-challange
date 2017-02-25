package com.armen.wai.strategies;

import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.analytics.MapAnalysisImpl;
import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;

import java.util.Collection;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public class DeploymentStrategyImpl implements DeploymentStrategy {

    private final MapAnalysis mapAnalysis;

    public DeploymentStrategyImpl(MapAnalysis mapAnalysis) {
        this.mapAnalysis = mapAnalysis;
    }


    @Override
    public Collection<Deployment> getDeployments() {
        List<Region> orderedRegions
                = mapAnalysis.suggestDeploymentRegions();
        List<Deployment> deployments =
        return null;
    }

    @Override
    public List<Region> pickInitialRegions(Collection<Region> regions) {
        return mapAnalysis.suggestRegionOrder(regions);
    }
}
