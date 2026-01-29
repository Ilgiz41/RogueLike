package Datalayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import domain.GameSession;

public class GameStatistic {

    @JsonProperty
    private String name;
    @JsonProperty
    private int level;
    @JsonProperty
    private int coins;
    @JsonProperty
    private int kills;
    @JsonProperty
    private int steps;


    GameStatistic() {
    }

    public GameStatistic(GameSession gameSession) {
        this.name = gameSession.getHero().getName();
        this.level = gameSession.getLevel(gameSession.getCurrentIndexLevel()).getLevelNumber();
        this.coins = gameSession.getHero().getBackpack().getCoinCount();
        this.kills = gameSession.getHero().getKills();
        this.steps = gameSession.getHero().getSteps();
    }

    public int getCoins() {
        return coins;
    }

    @Override
    public String toString() {
        return String.format("Name: %s level: %d coins: %d kills: %d steps: %d", name, level, coins, kills, steps);
    }
}
