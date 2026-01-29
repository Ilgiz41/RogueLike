package domain.item.eat;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.model.Stats;


public class Chapman extends AbstractEat {

    public Chapman(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.addHealth(15);
        stats.addMaxHealth(2);
    }


    public String getType() {
        return "chapman";
    }
}



