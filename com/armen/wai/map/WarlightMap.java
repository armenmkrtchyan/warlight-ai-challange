package com.armen.wai.map;

import com.armen.wai.util.SuperGraph;

import java.util.Collection;

/**
 * @author armen.mkrtchyan
 */
public interface WarlightMap {

    void setup(String configKey, String config);

    void finalSetup();

    void update(String config);

    Collection<Region> getRegionsByIds(String config);

    SuperGraph getSuperGraph();

}
