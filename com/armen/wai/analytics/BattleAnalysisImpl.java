package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.move.Deployment;
import com.armen.wai.move.DeploymentImpl;
import com.armen.wai.move.Move;
import com.armen.wai.move.MoveImpl;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.algorithm.BFS;
import com.armen.wai.util.helper.Edge;
import com.armen.wai.util.helper.Node;
import com.armen.wai.util.helper.OwnerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public class BattleAnalysisImpl implements BattleAnalysis {

    private final SuperGraph superGraph;
    private final WarlightMap warlightMap;

    public BattleAnalysisImpl(SuperGraph superGraph, WarlightMap warlightMap) {
        this.superGraph = superGraph;
        this.warlightMap = warlightMap;
    }

    @Override
    public List<Deployment> suggestDeployment(List<Region> orderedRegions) {
        List<Deployment> deployments = new ArrayList<>();
        List<Node> selfNodes = superGraph.getSelfNodes(superGraph);
        BFS bfs = new BFS();
        HashSet<Integer> registeredNodes = new HashSet<>();
        for (Node selfNode : selfNodes) {
            if (bfs.closestEnemy(selfNode, superGraph).getValue() == 0) {
                deployments.add(new DeploymentImpl(selfNode.getId(),
                        maxNeededDeploymentForRegion(warlightMap.superRegionId(selfNode.getId()))));
                registeredNodes.add(selfNode.getId());
            }
        }
        orderedRegions.stream()
                .filter(region -> !registeredNodes.contains(region.getId()))
                .forEachOrdered(region -> deployments.add(new DeploymentImpl(region.getId(),
                        maxNeededDeploymentForRegion(region.getSuperRegionId()))));
        return deployments;
    }

    private Integer maxNeededDeploymentForRegion(Integer superRegionId) {
        List<Edge> edges = superGraph.getSubGraph(superRegionId)
                .getAdjacent(new Node(superRegionId));

        int neededDeployment = 0;
        for (Edge edge : edges) {
            neededDeployment += edge.getWeight();
        }
        return neededDeployment;
    }

    public List<Move> suggestMoves() {
        List<Move> moves = new ArrayList<>();
        List<Node> selfNodes = superGraph.getSelfNodes(superGraph);

        for (Node selfNode : selfNodes) {
            List<Edge> adjacencies = superGraph.getAdjacent(selfNode);
            for (Edge adjacency : adjacencies) {
                if (adjacency.getTo().getOwnerType().equals(OwnerType.Neutral)) {
                    Integer startRegionId = selfNode.getId();
                    Integer endRegionId = adjacency.getTo().getId();
                    moves.add(new MoveImpl(startRegionId, endRegionId, adjacency.getWeight()));
                }
            }
        }

        return moves;
    }
}
