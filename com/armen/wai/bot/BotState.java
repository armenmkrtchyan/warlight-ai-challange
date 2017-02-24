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
import com.armen.wai.move.Move;

import java.util.ArrayList;

public class BotState {
	
	private String myName = "";
	private String opponentName = "";
	

	private ArrayList<Region> pickableStartingRegions; //list of regions the player can choose the start from
	private ArrayList<Region> wastelands; //wastelands, i.e. neutral regions with a larger amount of armies on them. Given before the picking of starting regions
	
	private ArrayList<Move> opponentMoves; //list of all the opponent's moves, reset at the end of each round

	private int startingArmies; //number of armies the player can place on map
	private int maxRounds;
	private int roundNumber;

	private long totalTimebank; //total time that can be in the timebank
	private long timePerMove; //the amount of time that is added to the timebank per requested move
	
	public BotState()
	{
		opponentMoves = new ArrayList<Move>();
		roundNumber = 0;
	}
	
	public void updateSettings(String key, String[] parts)
	{
		String value;

		if(key.equals("starting_regions") && parts.length > 3) {
			setPickableStartingRegions(parts);
			return;
		} 
		value = parts[2];

		if(key.equals("your_bot")) //bot's own name
			myName = parts[2];
		else if(key.equals("opponent_bot")) //opponent's name
			opponentName = value;
		else if(key.equals("max_rounds"))
			maxRounds = Integer.parseInt(value);
		else if(key.equals("timebank"))
			totalTimebank = Long.parseLong(value);
		else if(key.equals("time_per_move"))
			timePerMove = Long.parseLong(value);
		else if(key.equals("starting_armies")) 
		{
			startingArmies = Integer.parseInt(value);
			roundNumber++; //next round
		}
	}
	
	//initial map is given to the bot with all the information except for player and armies info

	
	//regions from wich a player is able to pick his preferred starting region
	public void setPickableStartingRegions(String[] input)
	{
//		pickableStartingRegions = new ArrayList<Region>();
//		for(int i=2; i<input.length; i++)
//		{
//			int regionId;
//			try {
//				regionId = Integer.parseInt(input[i]);
//				Region pickableRegion = fullMapOld.getRegion(regionId);
//				pickableStartingRegions.add(pickableRegion);
//			}
//			catch(Exception e) {
//				System.err.println("Unable to parse pickable regions " + e.getMessage());
//			}
//		}
	}
	
	//visible regions are given to the bot with player and armies info

	//Parses a list of the opponent's moves every round. 
	//Clears it at the start, so only the moves of this round are stored.
	public void readOpponentMoves(String[] moveInput)
	{
	}
	
	public String getMyPlayerName(){
		return myName;
	}
	
	public String getOpponentPlayerName(){
		return opponentName;
	}
	
	public int getStartingArmies(){
		return startingArmies;
	}
	
	public int getRoundNumber(){
		return roundNumber;
	}
	

	public ArrayList<Move> getOpponentMoves(){
		return opponentMoves;
	}
	
	public ArrayList<Region> getPickableStartingRegions(){
		return pickableStartingRegions;
	}

	public ArrayList<Region> getWasteLands(){
		return wastelands;
	}

}
