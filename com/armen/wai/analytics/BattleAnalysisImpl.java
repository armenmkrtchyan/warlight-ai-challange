package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.move.Deployment;
import com.armen.wai.move.DeploymentImpl;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.algorithm.BFS;
import com.armen.wai.util.helper.Edge;
import com.armen.wai.util.helper.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Integer> regionIds = new ArrayList<>();
        for (Node selfNode : selfNodes) {
            if (bfs.closestEnemy(selfNode, superGraph).getValue() == 0) {
                deployments.add(new DeploymentImpl(selfNode, maxNeededDeploymentForRegion()));
            }
        }
        List<Region> regions = regionIds.stream().map(warlightMap.getAllRegions())
        regionIds.addAll(orderedRegions.stream().map(Region::getId).collect(Collectors.toList()));
        for (Integer regionId : regionIds) {
            deployments.add(new DeploymentImpl(regionId, maxNeededDeploymentForRegion(regionId)));
        }
        return;
    }

    private Integer maxNeededDeploymentForRegion(Region regionId) {
        List<Edge> edges = superGraph.getSubGraph(regionId.getSuperRegionId())
                .getAdjacent(new Node(regionId.getId()));

        int neededDeployment = 0;
        for (Edge edge : edges) {
            neededDeployment += edge.getWeight();
        }
        return neededDeployment;
    }
}
