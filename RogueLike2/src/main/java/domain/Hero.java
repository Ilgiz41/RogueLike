package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.lanterna.TextColor;
import domain.enemy.Enemy;
import domain.item.Item;
import domain.item.coin.Coin;
import domain.item.weapon.Sword;
import domain.model.Equipment;
import domain.model.Stats;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Hero {


    private String name;
    private char heroSymbol;

    //characteristics
    @Getter
    private final Stats stats;

    @JsonIgnore
    private int BASE_DAMAGE = 10;
    //Location

    private Coordinate coordinate;

    private int currentRoom;

    private int steps = -1;

    private Backpack backpack;

    private int kills;

    List<Item> hands;

    @Getter
    private final Equipment equipment;

    @JsonCreator
    Hero(@JsonProperty("coordinate") Coordinate coordinate,
         @JsonProperty("currentRoom") int currentRoom) {
        this.kills = 0;
        this.heroSymbol = 'H';
        this.name = name;
        this.stats = new Stats(100);
        this.coordinate = coordinate;
        this.currentRoom = currentRoom;
        this.backpack = new Backpack();
        this.hands = new ArrayList<>(1);
        this.equipment = new Equipment();
    }

    public void movePlayer(int x, int y, GameSession gameSession) {
        int startX = coordinate.getX();
        int startY = coordinate.getY();

        coordinate.setNewPlusOld(x, y);
        currentRoom = gameSession.getCurrentRoomForEntity(coordinate);
        checkEnemy(gameSession, startX, startY);

    }

    /// Расчет вероятности попадания
    private boolean hitProbCal(Enemy enemy) {
        Random random = new Random();
        double baseChance = 0.5, agilityFactor = 0.01, minHitChance = 0.1, maxHitChance = 0.9;
        double hitChance = baseChance + (enemy.getAgility() - stats.getAgility() * agilityFactor);
        hitChance = Math.min(maxHitChance, Math.max(minHitChance, hitChance));
        return random.nextDouble() < hitChance;
    }

    private void attackEnemy(Enemy enemy, GameSession gameSession) {

        if (hitProbCal(enemy)) {
            int damage = calcDamage();
            enemy.takeDamage(damage);
            gameSession.getGameEvent().addDamageEvent(enemy, String.format("Нанесено %d урона ", damage));
        } else {
            gameSession.getGameEvent().addDamageEvent(this, "MISS!!! ");
        }
    }

    private int calcDamage() {
        Random rand = new Random();
        double randModifier = 0.8 * rand.nextDouble() * 0.4;
        double damage = (BASE_DAMAGE * 2 * randModifier) * (1 + (stats.getStrength() / 80.0));
        if (!hands.isEmpty()) {
            if (hands.getFirst() instanceof Sword) {
                damage *= 2;
            } else {
                damage *= 2.5;
            }
        }
        return Math.max(1, (int) damage);
    }

    private void checkEnemy(GameSession gameSession, int startX, int startY) {
        Iterator<Enemy> iterator = gameSession.getLevels().get(gameSession.getCurrentIndexLevel()).getEnemies().iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (coordinate.getX() == enemy.getPoint().getCoordinate().getX() && coordinate.getY() == enemy.getPoint().getCoordinate().getY()) {
                attackEnemy(enemy, gameSession);
                steps--;
                if (enemy.getHealth() < 1) {
                    iterator.remove();
                    randomCoinAfterDeathEnemy(enemy.getPoint().getCoordinate().getX(), enemy.getPoint().getCoordinate().getY(), gameSession);
                    setKillPlus();
                }
                coordinate.setNew(startX, startY);
            }
        }
    }

    private void randomCoinAfterDeathEnemy(int x, int y, GameSession gameSession) {
        Random rand = new Random();
        Coordinate coordinate = new Coordinate(x, y);
        int chance = rand.nextInt(2);

        if (chance == 1) {
            gameSession.getLevels().get(gameSession.getCurrentIndexLevel()).getItems().add(new Coin(coordinate, Coin.generateAmountCoin(), true, TextColor.ANSI.YELLOW));
        }
    }

    public void takeDamage(double damage) {
        stats.takeDamage(damage);
    }

    public void takeMaxHealthDamage(int damage) {
        this.stats.setMaxHealth(stats.getMaxHealth() - damage);
    }

    //GET
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public char getHeroSymbol() {
        return heroSymbol;
    }

    public String getName() {
        return name;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public int getSteps() {
        return steps;
    }

    @JsonIgnore
    public String getHandsType() {
        if (equipment.getAbstractWeapon() != null) {
            return equipment.getAbstractWeapon().getType();
        }
        return "Пусто";
    }

    public List<Item> getHands() {
        return hands;
    }

    public int getKills() {
        return kills;
    }

    //SET
    public void setHeroSymbol(char heroSymbol) {
        this.heroSymbol = heroSymbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @JsonIgnore
    public void setStepsPlus() {
        steps++;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setKillPlus() {
        kills++;
    }

    public void setNewPos(int posX, int posY) {
        coordinate.setNew(posX, posY);
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public void setHands(List<Item> hands) {
        this.hands = hands;
    }
}
