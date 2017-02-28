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
import com.armen.wai.util.Settings;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotParser {

    final Scanner scan;

    private final static Pattern configPattern = Pattern.compile("([\\w_]+)\\s(([\\w_/]+)\\s+(.+))");

    private final Settings settings = new Settings();
    private final WarlightMap warlightMap = new WarlightMapImpl(settings);
    private BattleAnalysis battleAnalysis = new BattleAnalysisImpl(warlightMap.getMainGraph(), warlightMap,
            settings);
    private MapAnalysis mapAnalysis = new MapAnalysisImpl(warlightMap);
    private final MoveStrategy moveStrategy = new MoveStrategyImpl(battleAnalysis, mapAnalysis);
    private final DeploymentStrategy deploymentStrategy = new DeploymentStrategyImpl(mapAnalysis,
            battleAnalysis);

    public BotParser() {
        this.scan = new Scanner(System.in);
    }

    public void run() {
        boolean mapIsSetUp = false;
        List<Region> suggestedRegions = null;
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
                    if (!mapIsSetUp) {
                        warlightMap.finalSetup();
                        mapIsSetUp = true;
                    }
                    Collection<Region> regions = warlightMap.getRegionsByIds(parts.group(4));
                    if (suggestedRegions == null) {
                        suggestedRegions = deploymentStrategy.pickInitialRegions(regions);
                    }
                    suggestedRegions.retainAll(regions);
                    System.out.println(suggestedRegions.get(0).getId());
                    suggestedRegions.remove(0);
                } else if (parts.groupCount() == 4 && parts.group(1).equals("go")) {
                    String output = "";
                    if (parts.group(3).equals("place_armies")) {
                        if (mapAnalysis == null) {
                            throw new IllegalStateException("Something goes wrong in starting picks");
                        }
                        Collection<Deployment> deployments = deploymentStrategy.getDeployments();
                        warlightMap.update(deployments);
                        output = sysOutDeploys(deployments);
                    } else if (parts.group(3).equals("attack/transfer")) {
                        if (mapAnalysis == null) {
                            throw new IllegalStateException("Something goes wrong in starting picks");
                        }
                        output = sysOutTransfers(moveStrategy.getMoves());
                    }
                    if (output.length() > 0) {
                        System.out.println(output);
                    } else {
                        System.out.println("No moves");
                    }
                } else if (parts.group(1).equals("settings")) {
                    settings.setup(parts.group(3), parts.group(4));
                } else {
                    if (parts.group(1).equals("setup_map")) {
                        warlightMap.setup(parts.group(3), parts.group(4));
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
    }

    private String sysOutDeploys(Collection<Deployment> deployments) {
        if (deployments.isEmpty()) {
            return "";
        }
        String output = "";
        String botName = settings.getYourBot();

        for (Deployment deployment : deployments) {
            output += botName + " place_armies "
                    + deployment.getRegion() + " "
                    + deployment.getArmies() + ", ";
        }

        return output.trim().substring(0, output.length() - 2);
    }

    private String sysOutTransfers(List<Move> moves) {
        if (moves.isEmpty()) {
            return "";
        }
        String output = "";
        String botName = settings.getYourBot();

        for (Move move : moves) {
            output += botName + " attack/transfer "
                    + move.getStartRegionId() + " "
                    + move.getEndRegionId() + " "
                    + move.getArmies() + ", ";
        }

        return output.trim().substring(0, output.length() - 2);
    }

}
