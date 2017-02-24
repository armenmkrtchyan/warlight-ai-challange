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

import com.armen.wai.map.Region;
import com.armen.wai.map.WarlightMap;
import com.armen.wai.map.WarlightMapImpl;
import com.armen.wai.move.AttackTransferMove;
import com.armen.wai.move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotParser {

    final Scanner scan;

    private final static Pattern configPattern = Pattern.compile("([\\w_]+)\\s([\\w_]+)\\s+(.+)");

    final Bot bot;

    BotState currentState;
    private final WarlightMap warlightMap = new WarlightMapImpl();

    public BotParser(Bot bot) {
        this.scan = new Scanner(System.in);
        this.bot = bot;
        this.currentState = new BotState();
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
                if (parts.group(1)
                        .equals("pick_starting_region")) //pick which regions you want to start with
                {
    //				currentState.setPickableStartingRegions(parts);
                    Region startingRegion = bot.getStartingRegion(currentState,
                            Long.valueOf(parts.group(2)));

                    System.out.println(startingRegion.getId());
                } else if (parts.groupCount() == 3 && parts.group(1).equals("go")) {
                    //we need to do a move
                    String output = "";
                    if (parts.group(2).equals("place_armies")) {
                        //place armies
                        ArrayList<PlaceArmiesMove> placeArmiesMoves = null;
                        for (PlaceArmiesMove move : placeArmiesMoves)
                            output = output.concat(move.getString() + ",");
                    } else if (parts.group(2).equals("attack/transfer")) {
                        //attack/transfer
                        ArrayList<AttackTransferMove> attackTransferMoves = null;
                        for (AttackTransferMove move : attackTransferMoves)
                            output = output.concat(move.getString() + ",");
                    }
                    if (output.length() > 0)
                        System.out.println(output);
                    else
                        System.out.println("No moves");
                } else if (parts.group(1).equals("settings")) {
                    //update settings
    //				currentState.updateSettings(parts.group(2), parts.group(3));
                } else if (parts.group(1).equals("setup_map")) {
                    warlightMap.setup(parts.group(2), parts.group(3));
                } else if (parts.group(1).equals("update_map")) {
                    //all visible regions are given
    //				currentState.updateMap(parts.group(2));
                } else if (parts.group(1).equals("opponent_moves")) {
                    //all visible opponent moves are given
    //				currentState.readOpponentMoves(parts);
                } else {
                    System.err.printf("Unable to parse line \"%s\"\n", line);
                }
            }
        }
        warlightMap.finalSetup();
    }

}
