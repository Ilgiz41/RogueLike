package domain.item.eat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.model.Stats;

public class Meat extends AbstractEat {

    public Meat(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.addHealth(10);
        stats.addMaxHealth(1);
    }

    public String getType() {
        return "meat";
    }

}
