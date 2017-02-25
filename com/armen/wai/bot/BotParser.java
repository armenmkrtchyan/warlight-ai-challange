/**
 * Warlight AI Game Bot
 *
 * Last update: January 29, 2015
 *
 * @author Jim van Eeden
 * @version 1.1
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package com.armen.wai.bot;

import com.armen.wai.analytics.BattleAnalysis;
import com.armen.wai.analytics.BattleAnalysisImpl;
import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.analytics.MapAnalysisImpl;
import com.armen.wai.map.Region;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.map.WarlightMapImpl;
import com.armen.wai.move.Deployment;
import com.armen.wai.move.Move;
import com.armen.wai.strategies.DeploymentStrategy;
import com.armen.wai.strategies.DeploymentStrategyImpl;
import com.armen.wai.strategies.MoveStrategy;
import com.armen.wai.strategies.MoveStrategyImpl;
import com.armen.wai.strategies.DeploymentStrategy;
import com.armen.wai.strategies.DeploymentStrategyImpl;
import com.armen.wai.util.Settings;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotParser {

    final Scanner scan;

    private final static Pattern configPattern = Pattern.compile("([\\w_]+)\\s([\\w_]+)\\s+(.+)");

    private final Settings settings = new Settings();
    private final WarlightMap warlightMap = new WarlightMapImpl(settings);
    private final MoveStrategy moveStrategy = new MoveStrategyImpl();
    private MapAnalysis mapAnalysis = new MapAnalysisImpl(warlightMap.getSuperGraph(), warlightMap);
    private final DeploymentStrategy deploymentStrategy = new DeploymentStrategyImpl(mapAnalysis);
    private BattleAnalysis battleAnalysis;
    private List<Region> suggestedRegions;

    public BotParser() {
        this.scan = new Scanner(System.in);
    }

    public void run() {
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.length() == 0) {
                continue;
            } else if (line.equals("break")) {
                break;
            }
            Matcher parts = configPattern.matcher(line);
            if (parts.matches()) {
                if (parts.group(1) .equals("pick_starting_region")) {
                    if (suggestedRegions == null) {
                        Collection<Region> regions = warlightMap.getRegionsByIds(parts.group(3));
                        suggestedRegions = deploymentStrategy.pickInitialRegions(regions);
                    }
                    System.out.println(suggestedRegions.get(0));
                    suggestedRegions.remove(0);
                } else if (parts.groupCount() == 3 && parts.group(1).equals("go")) {
                    String output = "";
                    if (parts.group(2).equals("place_armies")) {
                        if (mapAnalysis == null) {
                            throw new IllegalStateException("Something goes wrong in starting picks");
                        }
                        sysOutDeploys(deploymentStrategy.getDeployments());
                    } else if (parts.group(2).equals("attack/transfer")) {
                        if (mapAnalysis == null) {
                            throw new IllegalStateException("Something goes wrong in starting picks");
                        }
                        sysOutTransfers(moveStrategy.getMoves(battleAnalysis, mapAnalysis));
                    }
                    if (output.length() > 0)
                        System.out.println(output);
                    else
                        System.out.println("No moves");
                } else if (parts.group(1).equals("settings")) {
                    settings.setup(parts.group(2), parts.group(3));
                } else {
                    if (parts.group(1).equals("setup_map")) {
                        warlightMap.setup(parts.group(2), parts.group(3));
                    } else if (parts.group(1).equals("update_map")) {
                        warlightMap.update(parts.group(2));
                    } else if (parts.group(1).equals("opponent_moves")) {
                        //all visible opponent moves are given
                        //				currentState.readOpponentMoves(parts);
                    } else {
                        System.err.printf("Unable to parse line \"%s\"\n", line);
                    }
                }
            }
        }
        warlightMap.finalSetup();
    }

    private void sysOutDeploys(Collection<Deployment> deployments) {
        String output = "";
        String botName = settings.getYourBot();

        for (Deployment deployment : deployments) {
            output += botName + " place_armies "
                    + deployment.getRegion().getId() + " "
                    + deployment.getArmies() + ", ";
        }

        System.out.println(output.trim());
    }

    private void sysOutTransfers(List<Move> moves) {
        String output = "";
        String botName = settings.getYourBot();

        for (Move move : moves) {
            output += botName + " attack/transfer "
                    + move.getStartRegion().getId() + " "
                    + move.getEndRegion().getId() + " "
                    + move.getArmies() + ", ";
        }

        System.out.println(output.trim());
    }

}
