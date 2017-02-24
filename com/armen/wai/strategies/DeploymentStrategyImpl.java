package com.armen.wai.strategies;

import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;

import java.util.Collection;

/**
 * Created by Anushavan on 2/25/17.
 */
public class DeploymentStrategyImpl implements DeploymentStrategy {

    public DeploymentStrategyImpl() { }

    @Override
    public Collection<Deployment> getDeployments(MapAnalysis mapAnalysis) {
        return null;
    }

    @Override
    public Collection<Region> pickInitialRegions(MapAnalysis mapAnalysis) {
        return null;
    }

}
