package com.armen.wai.util;
import java.util.ArrayList;

/**
 * @author Arthur Rafayelyan
 * @since 2/24/2017.
 */
public class Settings {

    private Long timeBank;
    private Integer timePerMove;
    private Integer maxRounds;
    private Integer startingArmies;
    private Integer startingPickAmount;
    private ArrayList<Integer> startingRegions;
    private String yourBot;
    private String opponentBot;


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

    public void setup(String configKey, String config) {
        switch (configKey) {
            case "timebank":
                this.setTimeBank(Long.valueOf(config));
                break;
            case "time_per_move":
                this.setTimePerMove(Integer.valueOf(config));
                break;
            case "max_rounds":
                this.setMaxRounds(Integer.valueOf(config));
                break;
            case "your_bot":
                this.setYourBot(config);
                break;
            case "opponent_bot":
                this.setOpponentBot(config);
                break;
            case "starting_armies":
                this.setStartingArmies(Integer.valueOf(config));
                break;
            case "starting_regions":
                ArrayList<Integer> startingRegions = new ArrayList<>();
                String[] subConfig = config.split(" ");
                for (String aSubConfig : subConfig) {
                    startingRegions.add(Integer.valueOf(aSubConfig));
                }
                this.setStartingRegions(startingRegions);
                break;
            case "starting_pick_amount":
                this.setStartingPickAmount(Integer.valueOf(config));
        }
    }
}
