package domain.enemy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.lanterna.TextColor;
import domain.Coordinate;

import java.util.Random;

public class Ghost extends Enemy {
    @JsonIgnore
    private final int BASE_DAMAGE = 5;

    public Ghost(Coordinate coordinate, String type, char enemySymbol, int agility, int strength,
                 int hostility, int health, int currentRoom, boolean visibility, TextColor color) {
        super(coordinate, type, enemySymbol, agility, strength, hostility, health, currentRoom, visibility, color);
    }

    public int damageCalc() {
        Random rand = new Random();
        double randModifier = 0.8 * rand.nextDouble() * 0.4;
        double damage = (BASE_DAMAGE * 1.8 * randModifier) * (1 + (strength / 80.0));
        return Math.max(1, (int) damage);
    }
}