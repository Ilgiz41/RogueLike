package Datalayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.GameSession;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatisticManager {

    private static final String FILE_NAME = "Statistics.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static List<GameStatistic> stat = loadStat();


    public static void saveStat(GameSession gameSession) {
        GameStatistic gameStatistic = new GameStatistic(gameSession);
        stat.add(gameStatistic);
        saveLevelStat(stat);
    }


    public static void saveLevelStat(List<GameStatistic> stat) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), stat);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<GameStatistic> loadStat() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, GameStatistic.class));

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static List<GameStatistic> getStat() {
        return stat;
    }


    public static void sortStat(List<GameStatistic> stat) {
        stat.sort(Comparator.comparing(GameStatistic::getCoins).reversed());
    }
}
