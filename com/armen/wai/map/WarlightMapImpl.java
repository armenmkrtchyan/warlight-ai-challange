package com.armen.wai.map;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author armen.mkrtchyan
 */
public class WarlightMapImpl implements WarlightMap {

    public WarlightMapImpl() { }

    @Override
    public void setup(String configKey, String config) {
        switch (configKey) {
            case "super_regions": {
                List<SuperRegion> superRegions = createSuperRegions(config);
                break;
            }
            case "regions": {
                List<Region> regions = createRegions(config);
                break;
            }
            case "neighbors": {
                neighborsParse(config);
                break;
            }
        }
    }

    @Override
    public void update(String configKey, String config) {

    }

    private List<SuperRegion> createSuperRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<SuperRegion> superRegions = new ArrayList<>(configs.size()/2);

        for (int i = 0; i < configs.size() - 1; i += 2) {
            int id = Integer.parseInt(configs.get(i));
            int armiesReward = Integer.parseInt(configs.get(i + 1));

            superRegions.add(new SuperRegion(id, armiesReward));
        }

        return superRegions;
    }

    private List<Region> createRegions(String config) {
        List<String> configs = Arrays.asList(config.split(" "));
        List<Region> regions = new ArrayList<>(configs.size()/2);

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
            neighbors = neighborsConfig.stream().map(Integer::parseInt).collect(Collectors.toList());

            parsedNeighborsMap.put(regionId, neighbors);
        }

        return parsedNeighborsMap;
    }

}
