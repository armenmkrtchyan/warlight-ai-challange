package com.armen.wai.map;

import com.armen.wai.util.Settings;
import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.Node;

import java.util.ArrayList;
import java.util.Arrays;
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

    public WarlightMapImpl() {
        this.superGraph = new SuperGraph();
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
                wastelands = configs.stream().map(Integer::parseInt).collect(Collectors.toSet());
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
    public void update(String configKey, String config) {

    }

    private List<SuperRegion> createSuperRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<SuperRegion> superRegions = new ArrayList<>(configs.size() / 2);

        for (int i = 0; i < configs.size() - 1; i += 2) {
            int id = Integer.parseInt(configs.get(i));
            int armiesReward = Integer.parseInt(configs.get(i + 1));

            superRegions.add(new SuperRegion(id, armiesReward));
        }

        return superRegions;
    }

    private List<Region> createRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<Region> regions = new ArrayList<>(configs.size() / 2);

        for (int i = 0; i < configs.size() - 1; i += 2) {
            int id = Integer.parseInt(configs.get(i));
            int superRegionId = Integer.parseInt(configs.get(i + 1));

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

    @Override
    public void settings(String[] configLine) {
        Settings settings = new Settings();
        switch (configLine[0]) {
            case "timebank":
                settings.setTimeBank(Long.valueOf(configLine[1]));
                break;
            case "time_per_move":
                settings.setTimePerMove(Integer.valueOf(configLine[1]));
                break;
            case "max_rounds":
                settings.setMaxRounds(Integer.valueOf(configLine[1]));
                break;
            case "your_bot":
                settings.setYourBot(configLine[1]);
                break;
            case "opponent_bot":
                settings.setOpponentBot(configLine[1]);
                break;
            case "starting_regions":
                ArrayList<Integer> startingRegions = new ArrayList<>();
                for(int i = 1; i < configLine.length; i++) {
                    startingRegions.add(Integer.valueOf(configLine[i]));
                }
                settings.setStartingRegions(startingRegions);
                break;
            case "starting_pick_amount":
                settings.setStartingPickAmount(Integer.valueOf(configLine[1]));
        }
    }

}
