package com.armen.wai.analytics;

import com.armen.wai.map.Region;

import java.util.Collection;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public interface MapAnalysis {

    List<Region> suggestRegionOrder(Collection<Region> regions);

}
