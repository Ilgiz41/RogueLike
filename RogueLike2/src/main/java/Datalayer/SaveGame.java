package Datalayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.GameSession;
import domain.Hero;
import domain.Level;

import java.io.File;
import java.io.IOException;

public class SaveGame {

    @JsonProperty("currentLevelNum")
    private int currentIndexLevel;
    @JsonProperty("hero")
    private Hero hero;
    @JsonProperty("level")
    private Level level;

    public SaveGame() {
    }

    public SaveGame(GameSession gameSession) {
        this.currentIndexLevel = gameSession.getCurrentIndexLevel();
        this.hero = gameSession.getHero();
        this.level = gameSession.getLevel(currentIndexLevel);
    }

    public void saveGame() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("save.json"), this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadGame(GameSession gameSession) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SaveGame saveGame = objectMapper.readValue(new File("save.json"), SaveGame.class);
        saveGame.restore(gameSession);
    }

    private void restore(GameSession gameSession) {
        gameSession.setCurrentIndexLevel(currentIndexLevel);
        gameSession.setHero(hero);
        gameSession.setLevel(level);
    }

    public static int deleteFile() {
        File file = new File("save.json");
        if (file.delete()) {
            return 1;
        } else {
            return 0;
        }
    }
}