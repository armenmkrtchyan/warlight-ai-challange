package com.armen.wai.strategies;

import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;

import java.util.Collection;

/**
 * @author armen.mkrtchyan
 */
public interface DeploymentStrategy {

    Collection<Deployment> getDeployments(MapAnalysis mapAnalysis);

    Collection<Region> pickInitialRegions(MapAnalysis mapAnalysis);

}
