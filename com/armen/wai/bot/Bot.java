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

import java.util.ArrayList;

import com.armen.wai.map.Region;
import com.armen.wai.move.AttackTransferMove;
import com.armen.wai.move.PlaceArmiesMove;

public interface Bot {
	
	public Region getStartingRegion(BotState state, Long timeOut);
	
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut);
	
	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut);

}
