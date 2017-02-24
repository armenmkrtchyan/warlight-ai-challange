package com.armen.wai.util;
import java.util.ArrayList;

/**
 * @author Arthur Rafayelyan
 * @since 2/24/2017.
 */
public class Settings {

    Long timeBank;
    Integer timePerMove;
    Integer maxRounds;
    Integer startingArmies;
    Integer startingPickAmount;
    ArrayList<Integer> startingRegions;
    String yourBot;
    String opponentBot;


    /*Getters and Setters*/

    public Long getTimeBank() {
        return timeBank;
    }

    public void setTimeBank(Long timeBank) {
        this.timeBank = timeBank;
    }

    public Integer getTimePerMove() {
        return timePerMove;
    }

    public void setTimePerMove(Integer timePerMove) {
        this.timePerMove = timePerMove;
    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getStartingArmies() {
        return startingArmies;
    }

    public void setStartingArmies(Integer startingArmies) {
        this.startingArmies = startingArmies;
    }

    public Integer getStartingPickAmount() {
        return startingPickAmount;
    }

    public void setStartingPickAmount(Integer startingPickAmount) {
        this.startingPickAmount = startingPickAmount;
    }

    public ArrayList<Integer> getStartingRegions() {
        return startingRegions;
    }

    public void setStartingRegions(ArrayList<Integer> startingRegions) {
        this.startingRegions = startingRegions;
    }

    public String getYourBot() {
        return yourBot;
    }

    public void setYourBot(String yourBot) {
        this.yourBot = yourBot;
    }

    public String getOpponentBot() {
        return opponentBot;
    }

    public void setOpponentBot(String opponentBot) {
        this.opponentBot = opponentBot;
    }
}
