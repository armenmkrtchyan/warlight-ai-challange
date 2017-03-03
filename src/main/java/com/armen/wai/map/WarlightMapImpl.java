package com.armen.wai.map;

import com.armen.wai.move.Deployment;
import com.armen.wai.util.Settings;
import com.armen.wai.util.helper.OwnerType;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedSubgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class WarlightMapImpl implements WarlightMap {

    private final DefaultDirectedWeightedGraph<Region, RegionEdge> mainGraph;
    private final Map<Integer, DirectedWeightedSubgraph<Region, RegionEdge>> subGraphs;
    private Map<Integer, List<Integer>> neighbours;
    private List<Integer> opponentStartingRegions;
    private List<Region> regions;
    private Map<Integer, Region> regionMap = new HashMap<>();
    private Map<Integer, SuperRegion> superRegions;
    private final Settings settings;

    public WarlightMapImpl(Settings settings) {
        this.mainGraph = new DefaultDirectedWeightedGraph<>(RegionEdge::new);
        this.subGraphs = new HashMap<>();
        this.settings = settings;
    }

    private void configureNeighbours() {
        for (Map.Entry<Integer, List<Integer>> neighbourEntry : neighbours.entrySet()) {
            for (Integer neighbourId : neighbourEntry.getValue()) {
                Integer currentiId = neighbourEntry.getKey();
                Region sourceRegion = getRegionById(currentiId);
                Region targetRegion = getRegionById(neighbourId);
                Graphs.addEdge(mainGraph, sourceRegion,
                        targetRegion,
                        targetRegion.getDeployedArmies());
                Graphs.addEdge(mainGraph, targetRegion,
                        sourceRegion,
                        sourceRegion.getDeployedArmies());
            }
        }
    }

    private void configureSubRegions() {
        for (Region region : regions) {
            subGraphs.computeIfAbsent(region.getSuperRegionId(),
                    i -> new DirectedWeightedSubgraph<>(mainGraph));
            subGraphs.get(region.getSuperRegionId()).addVertex(region);
        }
    }

    @Override
    public void finalSetup() {
        configureNeighbours();
        configureSubRegions();
    }

    @Override
    public void setup(String configKey, String config) {
        switch (configKey) {
            case "super_regions": {
                superRegions = createSuperRegions(config);
                break;
            }
            case "regions": {
                parseRegions(config);

                break;
            }
            case "neighbors": {
                this.neighbours = neighborsParse(config);
                break;
            }
            case "wastelands": {
                List<String> configs = Arrays.asList(config.split(" "));
                Set<Region> wastelands = configs.stream()
                        .map(Integer::parseInt)
                        .map(this::getRegionById)
                        .collect(Collectors.toSet());
                wastelands.forEach(region -> region.setOwner(OwnerType.Neutral));

                break;
            }
            case "opponent_starting_regions": {
                List<String> configs = Arrays.asList(config.split(" "));
                opponentStartingRegions = configs.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                break;
            }
            default:
                throw new IllegalStateException("Unrecognized configuration");
        }
    }

    @Override
    public List<Region> getAllRegions() {
        return regions;
    }


    @Override
    public Integer superRegionId(Integer regionId) {
        return getRegionById(regionId).getSuperRegionId();
    }

    @Override
    public Region getRegionById(Integer id) {
        return regionMap.get(id);
    }

    @Override
    public DirectedGraph<Region, RegionEdge> getSuperRegionGraph(Integer id) {
        return subGraphs.get(id);
    }

    @Override
    public void update(String config) {
        String[] configs = config.split(" ");
        for (int i = 0; i < configs.length - 2; i += 3) {
            Integer id = Integer.parseInt(configs[i]);
            String botName = configs[i + 1];
            Integer armiesCount = Integer.parseInt(configs[i + 2]);
            updateDeployedArmiesCount(id, armiesCount);
            if (botName.equals(settings.getYourBot())) {
                updateOwnership(id,OwnerType.Self);
            } else if (botName.equals(settings.getOpponentBot())) {
                updateOwnership(id, OwnerType.Enemy);
            } else {
                updateOwnership(id, OwnerType.Neutral);
            }

        }
    }

    @Override
    public void update(Collection<Deployment> deployments) {
        for (Deployment deployment : deployments) {
            Integer regionId = deployment.getRegion();
            Region region = getRegionById(regionId);
            updateDeployedArmiesCount(regionId, region.getDeployedArmies() + deployment.getArmies());
        }
    }

    @Override
    public Collection<SuperRegion> getFullyOwnedSuperRegions() {
        Collection<Integer> superRegionIds = new HashSet<>(superRegions.keySet());
        regions.stream()
                .filter(region -> !region.getOwner().equals(OwnerType.Self))
                .forEach(region -> superRegionIds.remove(region.getSuperRegionId()));

        return superRegionIds.stream()
                .map(superRegions::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<SuperRegion> getEnemyFullyOwnedSuperRegions() {
        Collection<Integer> superRegionIds = new HashSet<>(superRegions.keySet());
        regions.stream()
                .filter(region -> !region.getOwner().equals(OwnerType.Enemy))
                .forEach(region -> superRegionIds.remove(region.getSuperRegionId()));

        return superRegionIds.stream()
                .map(superRegions::get)
                .collect(Collectors.toList());
    }

    private void updateOwnership(Integer regionId, OwnerType ownerType) {
        Region regionById = getRegionById(regionId);
        regionById.setOwner(ownerType);
    }

    private void updateDeployedArmiesCount(Integer regionId, Integer armiesCount) {
        Region region = getRegionById(regionId);
        region.setDeployedArmies(armiesCount);
        mainGraph.incomingEdgesOf(region)
                .forEach(edge -> mainGraph.setEdgeWeight(edge, armiesCount));
    }

    private Map<Integer, SuperRegion> createSuperRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        Map<Integer, SuperRegion> superRegions = new HashMap<>();

        for (int i = 0; i < configs.size() - 1; i += 2) {
            Integer id = Integer.parseInt(configs.get(i));
            Integer armiesReward = Integer.parseInt(configs.get(i + 1));

            superRegions.put(id, new SuperRegion(id, armiesReward));
        }

        return superRegions;
    }

    private List<Region> parseRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        regions = new ArrayList<>(configs.size() / 2);
        for (int i = 0; i < configs.size() - 1; i += 2) {
            Integer id = Integer.parseInt(configs.get(i));
            Integer superRegionId = Integer.parseInt(configs.get(i + 1));
            Region region = new Region(id, superRegionId);
            mainGraph.addVertex(region);
            regions.add(region);
            regionMap.put(id, region);
        }

        return regions;
    }

    private Map<Integer, List<Integer>> neighborsParse(String config) {
        Map<Integer, List<Integer>> parsedNeighborsMap = new HashMap<>();
        List<Integer> neighbors;
        Integer regionId;
        List<String> neighborsConfig;
        List<String> regionConfig = Arrays.asList(config.split(" "));

        for (int i = 0; i < regionConfig.size() - 1; i += 2) {
            regionId = Integer.parseInt(regionConfig.get(i));
            neighborsConfig = Arrays.asList(regionConfig.get(i + 1).split(","));
            neighbors = neighborsConfig.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            parsedNeighborsMap.put(regionId, neighbors);
        }

        return parsedNeighborsMap;
    }

    public Collection<Region> getRegionsByIds(String config) {
        return Arrays.stream(config.split(" "))
                .map(regionId -> getRegionById(Integer.parseInt(regionId)))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<Region> getRegionByOwner(OwnerType ownerType) {
        return regions.stream()
                .filter(region -> region.getOwner().equals(ownerType))
                .collect(Collectors.toList());
    }

    public DirectedGraph<Region, RegionEdge> getMainGraph() {
        return mainGraph;
    }

    public boolean isRegionFromOwnedSuperRegions(Region region) {
        return getFullyOwnedSuperRegions()
                .stream()
                .anyMatch(superRegion -> superRegion.getId().equals(region.getSuperRegionId()));
    }
}
