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

import java.util.*;
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
        final Integer[] availableArmies = {availableArmies()};
        for (Region selfRegion : enemyNeighbourRegions(orderedRegions)) {
            if (availableArmies[0] <= 0) {
                break;
            } else {
                Integer needed = minNeededDeploymentForRegionDefence(selfRegion);
                if (needed > 0) {
                    deployments.add(new DeploymentImpl(selfRegion.getId(),
                            Math.min(availableArmies[0], needed)));
                    availableArmies[0] -= needed;
                    registeredNodes.add(selfRegion.getId());
                }
            }
        }
        if (availableArmies[0] > 0) {
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
        }
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

    private Integer minNeededDeploymentForRegionDefence(Region region) {
        List<RegionEdge> regionEdges = mainGraph.edgesOf(region)
                .stream()
                .filter(regionEdge -> regionEdge.getTarget().getOwner().equals(OwnerType.Enemy))
                .collect(Collectors.toList());

        int enemyCount = regionEdges
                .stream()
                .map(RegionEdge::getTarget)
                .mapToInt(Region::getDeployedArmies)
                .max()
                .getAsInt();

        Integer armiesForDefence = minArmiesCountForDefence(enemyCount + Math.round(2 * enemyAvailableArmies() / 5));
        Integer deployedArmies = region.getDeployedArmies();

        return armiesForDefence <= deployedArmies
                ? 0
                : armiesForDefence - deployedArmies;
    }

    private Integer minArmiesCountForDefence(int enemyCount) {
        return 10 * enemyCount / 7;
    }

    private Integer maxNeededDeploymentForRegion(Region region) {
        List<RegionEdge> regionEdges = mainGraph.edgesOf(region)
                .stream()
                .filter(regionEdge -> !regionEdge.getTarget().getOwner().equals(OwnerType.Self))
                .collect(Collectors.toList());

        List<Integer> neededDeployments = new ArrayList<>();

        for (RegionEdge edge : regionEdges) {
            if (edge.getTarget().getOwner().equals(OwnerType.Neutral) &&
                    canAttackToNeutral(region, edge.getTarget())) {
                neededDeployments.add(0);
            } else if (edge.getTarget().getOwner().equals(OwnerType.Enemy) &&
                    canAttackToEnemy(region, edge.getTarget())) {
                neededDeployments.add(0);
            } else {
                neededDeployments.add(Double.valueOf(mainGraph.getEdgeWeight(edge) + 1).intValue());
            }
        }
        return Collections.min(neededDeployments);
    }

    private Integer availableArmies() {
        return settings.getStartingArmies();
    }

    private Integer enemyAvailableArmies() {
        Integer availableArmies = settings.getStartingArmies();

        for (SuperRegion superRegion : warlightMap.getEnemyFullyOwnedSuperRegions()) {
            availableArmies += superRegion.getArmiesReward();
        }

        return availableArmies;
    }

    public List<Move> suggestMoves() {
        List<Move> moves = new ArrayList<>();
        Collection<Region> ownRegions = warlightMap.getRegionByOwner(OwnerType.Self);

        ownRegions
                .stream()
                .filter(region -> region.getDeployedArmies() > 2)
                .forEach(region -> {
                    Set<RegionEdge> regionEdges = mainGraph.edgesOf(region);
                    for (RegionEdge edge : regionEdges) {
                        if (canAttackToNeutral(region, edge.getTarget())) {
                            moves.add(createMove(region, edge));
                        } else if (canAttackToEnemy(region, edge.getTarget())) {
                            moves.add(createMove(region, edge));
                        }
                    }
                });
        return moves;
    }

    private boolean canAttackToNeutral(Region from, Region to) {
        return to.getOwner().equals(OwnerType.Neutral) &&
                Math.round(3 * (from.getDeployedArmies() - 1) / 5) >=
                        to.getDeployedArmies();
    }

    private boolean canAttackToEnemy(Region from, Region to) {
        return to.getOwner().equals(OwnerType.Enemy) &&
                Math.round(3 * (from.getDeployedArmies() - 1) / 5) >=
                        to.getDeployedArmies() + Math.round(2 * enemyAvailableArmies() / 5);
    }

    private Move createMove(Region region, RegionEdge edge) {
        Integer startRegionId = region.getId();
        Integer endRegionId = edge.getTarget().getId();
        Integer armiesCount = Double.valueOf(mainGraph.getEdgeWeight(edge) + 1).intValue();

        region.setDeployedArmies(region.getDeployedArmies() - armiesCount);
        edge.getTarget().setOwner(OwnerType.Self);

        return new MoveImpl(startRegionId, endRegionId, armiesCount);
    }
}
