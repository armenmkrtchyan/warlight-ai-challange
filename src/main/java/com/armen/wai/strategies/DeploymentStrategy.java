package com.armen.wai.strategies;

import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;

import java.util.Collection;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public interface DeploymentStrategy {

    Collection<Deployment> getDeployments();

    List<Region> pickInitialRegions(Collection<Region> regions);

}
