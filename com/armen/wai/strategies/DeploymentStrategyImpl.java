package com.armen.wai.strategies;

import com.armen.wai.analytics.BattleAnalysis;
import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;

import java.util.Collection;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public class DeploymentStrategyImpl implements DeploymentStrategy {

    private final MapAnalysis mapAnalysis;
    private final BattleAnalysis battleAnalysis;

    public DeploymentStrategyImpl(MapAnalysis mapAnalysis, BattleAnalysis battleAnalysis) {
        this.mapAnalysis = mapAnalysis;
        this.battleAnalysis = battleAnalysis;
    }


    @Override
    public Collection<Deployment> getDeployments() {
        List<Region> orderedRegions
                = mapAnalysis.suggestDeploymentRegions();
        return battleAnalysis.suggestDeployment(orderedRegions);
    }

    @Override
    public List<Region> pickInitialRegions(Collection<Region> regions) {
        return mapAnalysis.suggestRegionOrder(regions);
    }
}
