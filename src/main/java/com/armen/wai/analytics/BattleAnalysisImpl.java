package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.RegionEdge;
import com.armen.wai.map.SuperRegion;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.move.Deployment;
import com.armen.wai.move.DeploymentImpl;
import com.armen.wai.move.Move;
import com.armen.wai.move.MoveImpl;
import com.armen.wai.util.Settings;
import com.armen.wai.util.helper.OwnerType;

import org.jgrapht.DirectedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class BattleAnalysisImpl implements BattleAnalysis {

    private final DirectedGraph<Region, RegionEdge> mainGraph;
    private final WarlightMap warlightMap;
    private final Settings settings;

    public BattleAnalysisImpl(DirectedGraph<Region, RegionEdge> mainGraph,
                              WarlightMap warlightMap, Settings settings) {
        this.mainGraph = mainGraph;
        this.warlightMap = warlightMap;
        this.settings = settings;
    }

    @Override
    public List<Deployment> suggestDeployment(List<Region> orderedRegions) {
        final List<Deployment> deployments = new ArrayList<>();
        HashSet<Integer> registeredNodes = new HashSet<>();
        for (Region selfRegion : enemyNeighbourRegions(orderedRegions)) {
            deployments.add(new DeploymentImpl(selfRegion.getId(),
                    maxNeededDeploymentForRegion(selfRegion)));
            registeredNodes.add(selfRegion.getId());
        }
        final Integer[] availableArmies = {availableArmies()};
        orderedRegions.stream()
                .filter(region -> !registeredNodes.contains(region.getId()))
                .forEachOrdered(region -> {
                    if (availableArmies[0] > 0) {
                        Integer needed = maxNeededDeploymentForRegion(region);
                        deployments.add(new DeploymentImpl(region.getId(),
                                Math.min(availableArmies[0], needed)));
                        availableArmies[0] -= needed;
                    }
                });
        return deployments;
    }

    private Collection<Region> enemyNeighbourRegions(Collection<Region> selfRegions) {
        return selfRegions.stream()
                .filter(region -> mainGraph.edgesOf(region)
                        .stream()
                        .anyMatch(edge -> edge.getTarget().getOwner().equals(OwnerType.Enemy)))
                .collect(
                        Collectors.toList());
    }

    private Integer maxNeededDeploymentForRegion(Region region) {
        List<RegionEdge> regionEdges = mainGraph.edgesOf(region)
                .stream()
                .filter(regionEdge -> !regionEdge.getTarget().getOwner().equals(OwnerType.Self))
                .collect(Collectors.toList());

        int neededDeployment = 0;
        for (RegionEdge edge : regionEdges) {
            neededDeployment += mainGraph.getEdgeWeight(edge);
        }
        return neededDeployment;
    }

    public List<Move> suggestMoves() {
        List<Move> moves = new ArrayList<>();
        Collection<Region> ownRegions = warlightMap.getRegionByOwner(OwnerType.Self);

        for (Region region : ownRegions) {
            Set<RegionEdge> regionEdges = mainGraph.edgesOf(region);
            for (RegionEdge edge : regionEdges) {
                if (edge.getTarget().getOwner().equals(OwnerType.Neutral)
                        && canAttack(edge.getSource(), edge.getTarget())) {
                    Integer startRegionId = region.getId();
                    Integer endRegionId = edge.getTarget().getId();
                    moves.add(new MoveImpl(startRegionId,
                            endRegionId,
                            Double.valueOf(mainGraph.getEdgeWeight(edge) + 1 ).intValue()));
                }
            }
        }

        return moves;
    }

    private Integer availableArmies() {
        Integer availableArmies = settings.getStartingArmies();
        for (SuperRegion superRegion : warlightMap.getFullyOwnedSuperRegions()) {
            availableArmies += superRegion.getArmiesReward();
        }
        return availableArmies;
    }

    private boolean canAttack(Region from, Region to) {
        return Math.round(3 * (from.getDeployedArmies() - 1) / 5) >= to.getDeployedArmies();
    }
}
