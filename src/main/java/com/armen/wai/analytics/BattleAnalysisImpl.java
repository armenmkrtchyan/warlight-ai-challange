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
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

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
//        for (Region selfRegion : enemyNeighbourRegions(orderedRegions)) {
//            if (availableArmies[0] <= 0) {
//                break;
//            } else {
//                Integer needed = minNeededDeploymentForRegionDefence(selfRegion);
//                if (needed > 0) {
//                    deployments.add(new DeploymentImpl(selfRegion.getId(),
//                            Math.min(availableArmies[0], needed)));
//                    availableArmies[0] -= needed;
//                    registeredNodes.add(selfRegion.getId());
//                }
//            }
//        }
        if (availableArmies[0] > 0) {
            orderedRegions.stream()
                    .filter(region -> !registeredNodes.contains(region.getId()))
                    .forEachOrdered(region -> {
                        if (availableArmies[0] > 0) {
                            Integer needed = maxNeededDeploymentForRegion(region);
                            if (needed > 0) {
                                deployments.add(new DeploymentImpl(region.getId(),
                                        Math.min(availableArmies[0], needed)));
                                availableArmies[0] -= needed;
                            }
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
                .orElse(0);

        Integer armiesForDefence = minArmiesCountForDefence(enemyCount + (int) Math.round((double) (2 * enemyAvailableArmies()) / 5));
        Integer deployedArmies = region.getDeployedArmies();

        return armiesForDefence <= deployedArmies
                ? 0
                : armiesForDefence - deployedArmies;
    }

    private int minArmiesCountForDefence(Integer enemyCount) {
        return (int) Math.round((double) (6 * enemyCount) / 10) + 1;
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
        return neededDeployments.size() > 0 ? Collections.min(neededDeployments) : 0;
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
        List<Region> ownRegions = new ArrayList<>(warlightMap.getRegionByOwner(OwnerType.Self));

        Collections.sort(ownRegions, (o1, o2) -> -1 * o1.getDeployedArmies().compareTo(o2.getDeployedArmies()));
        ownRegions
                .stream()
                .filter(region -> region.getDeployedArmies() > 3)
                .forEach(region -> moves.addAll(getMovesFrom(region)));

        return moves;
    }

    private List<Move> getMovesFrom(Region region) {
        List<Move> moves = new ArrayList<>();
        Set<RegionEdge> regionEdges = mainGraph.edgesOf(region);
        Integer transferRegionId = areAllEdgesSelf(regionEdges)
                ? getTransferRegionId(region)
                : -1;

        if (transferRegionId > -1) {
            moves.add(new MoveImpl(region.getId(), transferRegionId, region.getDeployedArmies() - 1));
        } else {
            moves.addAll(getAttacks(region, regionEdges));
        }

        return moves;
    }

    private List<Move> getAttacks(Region region, Set<RegionEdge> regionEdges) {
        List<Move> moves = new ArrayList<>();

        List<RegionEdge> notSelfEdges = regionEdges
                .stream()
                .filter(edge -> !edge.getTarget().getOwner().equals(OwnerType.Self))
                .collect(Collectors.toList());

        List<RegionEdge> sameSuperRegionEdges = notSelfEdges
                .stream()
                .filter(edge -> edge.getTarget().getSuperRegionId().equals(region.getSuperRegionId()))
                .collect(Collectors.toList());

        List<RegionEdge> differentSuperRegionEdges = notSelfEdges
                .stream()
                .filter(edge -> !edge.getTarget().getSuperRegionId().equals(region.getSuperRegionId()))
                .collect(Collectors.toList());

        moves.addAll(getAttacksTo(sameSuperRegionEdges));
        moves.addAll(getAttacksTo(differentSuperRegionEdges));

        return moves;
    }

    private List<Move> getAttacksTo(List<RegionEdge> notSelfEdges) {
        List<Move> moves = new ArrayList<>();

        notSelfEdges
                .stream()
                .forEach(edge -> {
                    Region from = edge.getSource();
                    Region to = edge.getTarget();

                    if (canAttackToNeutral(from, to)) {
                        moves.add(createMove(from, edge));
                    } else if (canAttackToEnemy(from, to)) {
                        moves.add(createMove(from, edge));
                    }
                });

        return moves;
    }

    private boolean areAllEdgesSelf(Set<RegionEdge> regionEdges) {
        return regionEdges
                .stream()
                .allMatch(regionEdge -> regionEdge.getTarget().getOwner().equals(OwnerType.Self));
    }

    private Integer getTransferRegionId(Region from) {
        Collection<Region> ownedRegions = warlightMap.getRegionByOwner(OwnerType.Self);
        GraphPath<Region, RegionEdge> minGraphPath = null;
        List<GraphPath<Region, RegionEdge>> graphPaths = ownedRegions
                .stream()
                .filter(region -> !from.getId().equals(region.getId()))
                .filter(region -> mainGraph.edgesOf(region)
                        .stream()
                        .anyMatch(edge -> !edge.getTarget().getOwner().equals(OwnerType.Self)))
                .map(to -> DijkstraShortestPath.findPathBetween(mainGraph, from, to))
                .collect(Collectors.toList());

        if (graphPaths.size() > 0) {
            minGraphPath = Collections
                    .min(graphPaths, (o1, o2) -> ((Integer) o1.getLength()).compareTo(o2.getLength()));
        }

        return minGraphPath != null
                ? minGraphPath.getVertexList().get(1).getId()
                : -1;
    }

    private boolean canAttackToNeutral(Region from, Region to) {
        return to.getOwner().equals(OwnerType.Neutral) &&
                Math.round((double) (3 * (from.getDeployedArmies() - 1)) / 5) >=
                        to.getDeployedArmies();
    }

    private boolean canAttackToEnemy(Region from, Region to) {
        return to.getOwner().equals(OwnerType.Enemy) &&
                Math.round((double) (3 * (from.getDeployedArmies() - 1)) / 5) >=
                        to.getDeployedArmies() + Math.round((double) (2 * enemyAvailableArmies()) / 5);
    }

    private Move createMove(Region region, RegionEdge edge) {
        Integer startRegionId = region.getId();
        Integer endRegionId = edge.getTarget().getId();
        Integer armiesCount = Double.valueOf(mainGraph.getEdgeWeight(edge) + 1).intValue();
        Integer remainingArmies = region.getDeployedArmies() - armiesCount;

        edge.getTarget().setOwner(OwnerType.Self);
        if (remainingArmies < 4 || areAllEdgesSelf(mainGraph.edgesOf(region))) {
            armiesCount = region.getDeployedArmies() - 1;
        }
        region.setDeployedArmies(region.getDeployedArmies() - armiesCount);

        return new MoveImpl(startRegionId, endRegionId, armiesCount);
    }
}
