package domain.item.scrol;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.model.Stats;

public class ScrollDeath extends AbstractScroll {

    public ScrollDeath(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.setCurrentHealth(1);
    }

    public String getType() {
        return "scroll death";
    }

}
