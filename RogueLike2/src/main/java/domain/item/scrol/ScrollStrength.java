package domain.item.scrol;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.model.Stats;

public class ScrollStrength extends AbstractScroll {

    public ScrollStrength(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.addStrength(2);
    }

    public String getType() {
        return "scroll strength";
    }

}
