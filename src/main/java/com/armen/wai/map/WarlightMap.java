package com.armen.wai.map;

import com.armen.wai.move.Deployment;
import com.armen.wai.util.helper.OwnerType;

import org.jgrapht.DirectedGraph;

import java.util.Collection;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public interface WarlightMap {

    void setup(String configKey, String config);

    void finalSetup();

    void update(String config);

    void update(Collection<Deployment> deployments);

    Collection<Region> getRegionByOwner(OwnerType ownerType);

    Collection<Region> getRegionsByIds(String config);

    DirectedGraph<Region, RegionEdge> getMainGraph();

    List<Region> getAllRegions();

    Region getRegionById(Integer id);

    DirectedGraph<Region, RegionEdge> getSuperRegionGraph(Integer id);

    Collection<SuperRegion> getFullyOwnedSuperRegions();

    Collection<SuperRegion> getEnemyFullyOwnedSuperRegions();

    Integer superRegionId(Integer regionId);

    boolean isRegionFromOwnedSuperRegions(Region region);

}
