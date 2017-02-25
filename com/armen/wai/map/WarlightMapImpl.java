package com.armen.wai.map;

import com.armen.wai.util.Settings;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.Node;
import com.armen.wai.util.helper.OwnerType;

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

    private final SuperGraph superGraph;
    private Map<Integer, List<Integer>> neighbours;
    private Set<Integer> wastelands;
    private List<Integer> opponentStartingRegions;
    private List<Region> regions;
    private List<SuperRegion> superRegions;
    private final Settings settings;

    public WarlightMapImpl(Settings settings) {
        this.superGraph = new SuperGraph();
        this.settings = settings;
    }


    private void configureNeighbours() {
        for (Map.Entry<Integer, List<Integer>> neighbourEntry : neighbours.entrySet()) {
            for (Integer neighbourIds : neighbourEntry.getValue()) {
                Integer currentiId = neighbourEntry.getKey();
                superGraph.addEdge(new Node(currentiId),
                        new Node(neighbourIds),
                        getWeight(neighbourIds, wastelands));
                superGraph.addEdge(new Node(neighbourIds),
                        new Node(currentiId),
                        getWeight(currentiId, wastelands));
            }
        }

    }

    private void configureSubRegions() {
        Map<Integer, Set<Integer>> group = new HashMap<>();
        for (Region region : regions) {
            group.computeIfAbsent(region.getSuperRegionId(), HashSet::new);
            group.get(region.getSuperRegionId()).add(region.getId());
        }
        superGraph.groupAll(group);
    }


    private int getWeight(Integer id, Set<Integer> wastelands) {
        return wastelands.contains(id) ? 6 : 2;
    }

    @Override
    public void finalSetup() {
        configureNeighbours();
        configureSubRegions();
        this.superGraph.updateOwnership(this.wastelands, OwnerType.Neutral);
    }

    @Override
    public void setup(String configKey, String config) {
        switch (configKey) {
            case "super_regions": {
                superRegions = createSuperRegions(config);
                break;
            }
            case "regions": {
                regions = createRegions(config);
                break;
            }
            case "neighbors": {
                this.neighbours = neighborsParse(config);
                break;
            }
            case "wastelands": {
                List<String> configs = Arrays.asList(config.split(" "));
                wastelands = configs.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

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
        return regions.stream();
    }

    @Override
    public void update(String config) {
        String[] configs = config.split(" ");
        for (int i = 0; i < configs.length - 2; i += 3) {
            Integer id = Integer.parseInt(configs[i]);
            String botName = configs[i + 1];
            Integer armiesCount = Integer.parseInt(configs[i + 2]);
            superGraph.setAdjacencyWeight(id, armiesCount);
            superGraph.updateOwnership(id,
                    botName.equals(settings.getYourBot()) ? OwnerType.Self : OwnerType.Enemy);
        }
    }

    private List<SuperRegion> createSuperRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<SuperRegion> superRegions = new ArrayList<>(configs.size() / 2);

        for (int i = 0; i < configs.size() - 1; i += 2) {
            Integer id = Integer.parseInt(configs.get(i));
            Integer armiesReward = Integer.parseInt(configs.get(i + 1));

            superRegions.add(new SuperRegion(id, armiesReward));
        }

        return superRegions;
    }

    private List<Region> createRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<Region> regions = new ArrayList<>(configs.size() / 2);

        for (int i = 0; i < configs.size() - 1; i += 2) {
            Integer id = Integer.parseInt(configs.get(i));
            Integer superRegionId = Integer.parseInt(configs.get(i + 1));

            regions.add(new Region(id, superRegionId));
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
        return Arrays.asList(config.split(" "))
                .stream()
                .map(regionId -> getRegionById(Integer.parseInt(regionId)))
                .collect(Collectors.toList());
    }

    private Region getRegionById(Integer regionid) {
        return this.regions
                .stream()
                .filter(region -> region.getId().equals(regionid))
                .collect(Collectors.toList())
                .get(0);
    }

    @Override
    public SuperGraph getSuperGraph() {
        return superGraph;
    }
}
